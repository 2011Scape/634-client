#!/usr/bin/env python3
"""634HD's own detail textures (materials.json entries 10000 and up, HdMaterialProvider).

Each is a tileable 256x256 near-grey map, made procedurally here (no third-party art, so no licence
to track). They are multiplied by the face or tile colour the game gives, so they add surface detail
- bark, stone, brick, roof tiles, planks, grass, dirt - while the game's colours stay: the mean is
about 0.92, the darkest parts about 0.6.

    python3 tools/make_detail_textures.py            # writes src/jvmMain/resources/634hd/materials/100xx/albedo.png
"""
import os
import sys

import numpy as np
from PIL import Image

SIZE = 256
ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "jvmMain", "resources", "634hd", "materials")

# id, name: the ids materials.json and the overrides use
TEXTURES = [
    (10001, "bark"),
    (10002, "stone"),
    (10003, "brick"),
    (10004, "roof_tiles"),
    (10005, "planks"),
    (10006, "grass"),
    (10007, "dirt"),
    (10008, "cobbles"),
]


def noise(rng, size, cells):
    """Tileable value noise: random values on a cells x cells grid, smoothly interpolated."""
    grid = rng.random((cells, cells))
    coords = np.arange(size) * cells / size
    i0 = np.floor(coords).astype(int)
    f = coords - i0
    f = f * f * (3 - 2 * f)
    i1 = (i0 + 1) % cells
    a = grid[np.ix_(i0, i0)]
    b = grid[np.ix_(i0, i1)]
    c = grid[np.ix_(i1, i0)]
    d = grid[np.ix_(i1, i1)]
    fy = f[:, None]
    fx = f[None, :]
    return (a * (1 - fx) + b * fx) * (1 - fy) + (c * (1 - fx) + d * fx) * fy


def fractal(rng, size, base, octaves):
    total = np.zeros((size, size))
    weight = 0.0
    amplitude = 1.0
    cells = base
    for _ in range(octaves):
        total += noise(rng, size, cells) * amplitude
        weight += amplitude
        amplitude *= 0.5
        cells *= 2
    return total / weight


def cells_pattern(rng, size, count):
    """Tileable Worley-style cells: distance to the nearest of [count] random points (wrapping)."""
    points = rng.random((count, 2)) * size
    ys, xs = np.mgrid[0:size, 0:size]
    best = np.full((size, size), 1e9)
    second = np.full((size, size), 1e9)
    for py, px in points:
        dy = np.abs(ys - py)
        dx = np.abs(xs - px)
        dy = np.minimum(dy, size - dy)
        dx = np.minimum(dx, size - dx)
        dist = np.sqrt(dx * dx + dy * dy)
        second = np.where(dist < best, best, np.minimum(second, dist))
        best = np.minimum(best, dist)
    return best, second


def bricks(rng, size, rows, columns, mortar):
    ys, xs = np.mgrid[0:size, 0:size]
    row = (ys * rows // size)
    offset = (row % 2) * (size // columns // 2)
    x = (xs + offset) % size
    in_row = (ys * rows % size) / size
    in_col = (x * columns % size) / size
    edge = np.minimum(np.minimum(in_row, 1 - in_row), np.minimum(in_col, 1 - in_col))
    mortar_mask = np.clip((mortar - edge) / mortar, 0, 1)
    brick_id = row * columns + (x * columns // size)
    shade = rng.random(rows * columns + columns)[brick_id] * 0.12
    return mortar_mask, shade


def make(name, rng):
    n = fractal(rng, SIZE, 4, 5)
    if name == "bark":
        # long vertical grain: noise fine across x, coarse along y, plus a few deep furrows
        across = noise(rng, SIZE, 48)
        along = noise(rng, SIZE, 4)
        grain = 0.7 * np.tile(across[0:1, :], (SIZE, 1)) + 0.3 * along.T * across
        furrows = np.tile((noise(rng, SIZE, 12)[0:1, :] < 0.25).astype(float), (SIZE, 1))
        v = 0.72 + 0.26 * grain - 0.18 * furrows * (0.5 + 0.5 * along) + 0.06 * (n - 0.5)
    elif name == "stone":
        best, second = cells_pattern(rng, SIZE, 24)
        crack = np.clip((second - best) / 6.0, 0, 1)
        v = 0.7 + 0.22 * crack + 0.1 * n
    elif name == "brick":
        mortar, shade = bricks(rng, SIZE, 8, 4, 0.07)
        v = 0.97 - shade + 0.08 * (n - 0.5) - 0.38 * mortar
    elif name == "roof_tiles":
        ys, xs = np.mgrid[0:SIZE, 0:SIZE]
        rows = 8
        row = ys * rows // SIZE
        x = (xs + (row % 2) * (SIZE // 16)) % SIZE
        in_row = (ys * rows % SIZE) / SIZE
        col = (x * 8 % SIZE) / SIZE
        # each tile darker toward its top, where the row above overlaps it; gaps between tiles
        v = 0.7 + 0.28 * in_row - 0.25 * (np.minimum(col, 1 - col) < 0.05) + 0.06 * (n - 0.5)
    elif name == "planks":
        ys, xs = np.mgrid[0:SIZE, 0:SIZE]
        board = (xs * 6 // SIZE)
        grain = np.repeat(noise(rng, SIZE, 64)[::16, :], 16, axis=0).T
        edge = (xs * 6 % SIZE) / SIZE
        v = 0.82 + 0.12 * grain + rng.random(6)[board] * 0.1 - 0.25 * (np.minimum(edge, 1 - edge) < 0.03)
    elif name == "grass":
        blades = fractal(rng, SIZE, 32, 3)
        v = 0.8 + 0.18 * blades + 0.08 * (n - 0.5)
    elif name == "dirt":
        pebbles, _ = cells_pattern(rng, SIZE, 60)
        v = 0.78 + 0.16 * n + 0.08 * np.clip(pebbles / 12.0, 0, 1)
    elif name == "cobbles":
        best, second = cells_pattern(rng, SIZE, 40)
        gap = np.clip((second - best) / 4.0, 0, 1)
        dome = np.clip(1 - best / 22.0, 0, 1)
        v = 0.62 + 0.25 * gap + 0.1 * dome + 0.06 * n
    else:
        raise ValueError(name)
    v = np.clip(v, 0.0, 1.0)
    # mean about 0.92, so the game's colours keep their brightness
    v = np.clip(v * (0.92 / v.mean()), 0.0, 1.0)
    return v


def main():
    for texture, name in TEXTURES:
        rng = np.random.default_rng(texture)
        v = make(name, rng)
        grey = (v * 255).round().astype(np.uint8)
        rgb = np.stack([grey, grey, grey], axis=-1)
        folder = os.path.join(ROOT, str(texture))
        os.makedirs(folder, exist_ok=True)
        Image.fromarray(rgb, "RGB").save(os.path.join(folder, "albedo.png"), optimize=True)
        print("%d %-10s mean %.2f min %.2f" % (texture, name, v.mean(), v.min()))
    return 0


if __name__ == "__main__":
    sys.exit(main())
