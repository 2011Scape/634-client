"""Not part of the original client: 634HD. An independent one-bounce reference on the dumped tiles, correlated with the grid's fill.

Direct light: each lamp onto every tile within its reach that a 2D ray (axis-stepping DDA across the
same line-of-sight wall bits) reaches, as an inverse square with a 2-tile knee. One bounce: every
directly lit tile re-emits half its light to the tiles within 6 that it can see, inverse square.

usage: ./gradlew runClient -Pclient.test=smoke -Pclient.test.toolkit=6 -Pclient.hd.lightGrid=on \
         -Pclient.test.hdDebug=true -Pclient.test.hdGridDump=/tmp/grid.json -Pclient.test.scenes=... 
       python3 -I tools/light_grid_reference.py /tmp/grid.json
"""
import json
import math
import sys

d = json.load(open(sys.argv[1]))
size, seeds, walls, fill = d["size"], d["seeds"], d["walls"], d["fill"]
N, E, S_, W, SOLID = 1, 2, 4, 8, 16


def wall(x, z):
    if x < 0 or z < 0 or x >= size or z >= size:
        return 31
    return walls[z][x]


def step_open(ax, az, bx, bz):
    a, b = wall(ax, az), wall(bx, bz)
    if a == 31 or b == 31 or b & SOLID:
        return False
    if bx > ax:
        return not (a & E or b & W)
    if bx < ax:
        return not (a & W or b & E)
    if bz > az:
        return not (a & N or b & S_)
    return not (a & S_ or b & N)


def visible(x0, z0, x1, z1):
    """DDA from tile centre to tile centre, one axis step at a time, each crossing checked."""
    if (x0, z0) == (x1, z1):
        return True
    fx, fz = x0 + 0.5, z0 + 0.5
    tx, tz = x1 + 0.5, z1 + 0.5
    dx, dz = tx - fx, tz - fz
    cx, cz = x0, z0
    sx = 1 if dx > 0 else -1
    sz = 1 if dz > 0 else -1
    tmx = ((cx + (1 if sx > 0 else 0)) - fx) / dx if dx != 0 else math.inf
    tmz = ((cz + (1 if sz > 0 else 0)) - fz) / dz if dz != 0 else math.inf
    tdx = abs(1 / dx) if dx != 0 else math.inf
    tdz = abs(1 / dz) if dz != 0 else math.inf
    for _ in range(4 * size):
        if (cx, cz) == (x1, z1):
            return True
        if tmx < tmz:
            nx, nz = cx + sx, cz
            tmx += tdx
        else:
            nx, nz = cx, cz + sz
            tmz += tdz
        if not step_open(cx, cz, nx, nz):
            return False
        cx, cz = nx, nz
    return False


direct = [[0.0] * size for _ in range(size)]
for (x, z, r, g, b, reach) in seeds:
    e = r + g + b
    rr = int(math.ceil(reach))
    for tz in range(max(0, z - rr), min(size, z + rr + 1)):
        for tx in range(max(0, x - rr), min(size, x + rr + 1)):
            dist = math.hypot(tx - x, tz - z)
            if dist > reach or wall(tx, tz) & SOLID or wall(tx, tz) == 31:
                continue
            if visible(x, z, tx, tz):
                direct[tz][tx] += e / (1.0 + (dist / 2.0) ** 2)

bounce = [[0.0] * size for _ in range(size)]
lit = [(x, z, direct[z][x]) for z in range(size) for x in range(size) if direct[z][x] > 0.02]
for (x, z, e) in lit:
    for tz in range(max(0, z - 6), min(size, z + 7)):
        for tx in range(max(0, x - 6), min(size, x + 7)):
            dist = math.hypot(tx - x, tz - z)
            if dist > 6 or dist == 0 or wall(tx, tz) & SOLID or wall(tx, tz) == 31:
                continue
            if visible(x, z, tx, tz):
                bounce[tz][tx] += 0.5 * e / (1.0 + dist * dist) / 12.0

ref = [[direct[z][x] + bounce[z][x] for x in range(size)] for z in range(size)]


def pearson(a, b):
    n = len(a)
    ma, mb = sum(a) / n, sum(b) / n
    cov = sum((p - ma) * (q - mb) for p, q in zip(a, b))
    va = math.sqrt(sum((p - ma) ** 2 for p in a))
    vb = math.sqrt(sum((q - mb) ** 2 for q in b))
    return cov / (va * vb) if va and vb else float("nan")


# tiles within 16 of any lamp (the region either field could reach), the open ones
near = set()
for (x, z, *_rest) in seeds:
    for tz in range(max(0, z - 16), min(size, z + 17)):
        for tx in range(max(0, x - 16), min(size, x + 17)):
            if not (wall(tx, tz) & SOLID) and wall(tx, tz) != 31:
                near.add((tx, tz))
near = sorted(near)
a = [fill[z][x] for (x, z) in near]
b = [ref[z][x] for (x, z) in near]
print("lamps on level 0: %d; open tiles within 16 of a lamp: %d" % (len(seeds), len(near)))
print("Pearson correlation, grid fill vs one-bounce reference: %.3f" % pearson(a, b))
print("rank-free agreement on where light is: tiles lit by the reference (> 1%% of its max) that the fill leaves dark: %d of %d; tiles the fill lights that the reference leaves dark: %d of %d" % (
    sum(1 for p, q in zip(a, b) if q > 0.01 * max(b) and p <= 0.01), sum(1 for q in b if q > 0.01 * max(b)),
    sum(1 for p, q in zip(a, b) if p > 0.01 and q <= 0.01 * max(b)), sum(1 for p in a if p > 0.01)))
print("share of the reference's light that is bounce (indirect): %.0f%%" % (100 * sum(bounce[z][x] for (x, z) in near) / max(1e-9, sum(ref[z][x] for (x, z) in near))))
print("correlation of the fill with the DIRECT part alone: %.3f, with the BOUNCE part alone: %.3f" % (pearson(a, [direct[z][x] for (x, z) in near]), pearson(a, [bounce[z][x] for (x, z) in near])))
