"""Not part of the original client: 634HD. Imports 117HD's per-area fog into environments.json.

Reads 117HD's environments.json and areas.json (its checkout given with --rlhd) and writes, after the
hand-written entries of our environments.json (those without a "source"), one generated entry per
117HD environment, in 117HD's order (the first area holding the player wins in both): its area as
bounds in world tiles (117HD's aabbs, regions, regionBoxes and nested areas resolved; levels only
when every box names the same planes), its fog colour and depth (117HD's fogDepth; an environment
without a colour outside the overworld gets 117HD's default black, one in the overworld keeps the
game's zone colour, our stand-in for 117HD's configured sky colour), and its ground fog (start and end
times 4: 117HD's units are 128 a tile, ours 512). Skipped: 117HD's keyed entries (DEFAULT, OVERWORLD,
the seasons), those under a varbit or varp condition, the underwater ones (our seabed fogs itself).
Generated entries carry "source": "117HD <AREA>" and are replaced when this runs again; hand-written
entries are kept as they are. Nothing but fog is imported: lighting, water and sky stay ours.

The data is 117HD's (https://github.com/117HD/RLHD, BSD 2-Clause licence).

usage: python3 -I tools/import_117hd_environments.py --rlhd ~/Documents/Projects/RLHD
         [--out src/jvmMain/resources/634hd/data/environments.json] [--check] [--report]
  --check   exits 1 when the generated entries in the file differ from what would be written
  --report  prints, for each hand-written entry, the 117HD environment covering its first area
"""
import argparse
import json
import os
import re
import sys

OURS = "src/jvmMain/resources/634hd/data/environments.json"
ALL_PLANES = frozenset((0, 1, 2, 3))


def load(path):
    with open(path, encoding="utf-8") as f:
        return json.load(f)


def region_box(region_id):
    x1 = (region_id >> 8) << 6
    y1 = (region_id & 0xFF) << 6
    return x1, y1, x1 + 63, y1 + 63


def planes(lo, hi):
    s = frozenset(range(lo, hi + 1))
    return None if s >= ALL_PLANES else s


def resolve(name, areas, seen=None):
    """The boxes (x1, y1, x2, y2, planes or None) of 117HD area [name], its nested areas included."""
    seen = seen or set()
    if name in seen:
        return []
    seen = seen | {name}
    area = areas.get(name)
    if area is None:
        print("warning: unknown area", name, file=sys.stderr)
        return []
    out = []
    for a in area.get("aabbs") or []:
        n = len(a)
        if n == 2:
            out.append((a[0], a[1], a[0], a[1], None))
        elif n == 3:
            out.append((a[0], a[1], a[0], a[1], frozenset((a[2],))))
        elif n == 4:
            out.append((a[0], a[1], a[2], a[3], None))
        elif n == 5:
            out.append((a[0], a[1], a[2], a[3], frozenset((a[4],))))
        elif n == 6:
            out.append((a[0], a[1], a[3], a[4], planes(a[2], a[5])))
        else:
            print("warning: odd aabb", name, a, file=sys.stderr)
    for r in area.get("regions") or []:
        out.append(region_box(r) + (None,))
    for b in area.get("regionBoxes") or []:
        x1, y1, _, _ = region_box(b[0])
        x2, y2, _, _ = region_box(b[1])
        box = (min(x1, x2), min(y1, y2), max(x1, x2) + 63, max(y1, y2) + 63)
        if len(b) == 2:
            out.append(box + (None,))
        elif len(b) == 3:
            out.append(box + (frozenset((b[2],)),))
        else:
            out.append(box + (planes(b[2], b[3]),))
    for sub in area.get("areas") or []:
        out.extend(resolve(sub, areas, seen))
    return [(min(x1, x2), min(y1, y2), max(x1, x2), max(y1, y2), p) for x1, y1, x2, y2, p in out]


def intersects(a, b):
    return a[0] <= b[2] and b[0] <= a[2] and a[1] <= b[3] and b[1] <= a[3]


def contains(boxes, x, y):
    return any(b[0] <= x <= b[2] and b[1] <= y <= b[3] for b in boxes)


def hex_colour(c):
    if isinstance(c, str):
        return c.lower()
    return "#%02x%02x%02x" % tuple(max(0, min(255, round(v * 255))) for v in c[:3])


def title(area):
    words = area.lower().split("_")
    return " ".join(words).capitalize()


def convert(envs, areas):
    """Our entries for 117HD's [envs], and the 117HD list kept for the report (name, boxes, entry)."""
    overworld = resolve("OVERWORLD", areas)
    generated = []
    resolved = []
    names = set()
    for env in envs:
        area = env.get("area")
        if not isinstance(area, str):
            continue
        boxes = resolve(area, areas)
        resolved.append((area, boxes, env))
        if not boxes or "key" in env or "varbitCondition" in env or "varpCondition" in env or env.get("isUnderwater"):
            continue
        name = env.get("description") or title(area)
        base, n = name, 2
        while name in names:
            name = "%s (%d)" % (base, n)
            n += 1
        names.add(name)
        entry = {"name": name, "source": "117HD " + area}
        plane_sets = [b[4] for b in boxes]
        our_area = {"bounds": [[b[0], b[1], b[2], b[3]] for b in boxes]}
        if all(p is not None for p in plane_sets) and len(set(plane_sets)) == 1:
            our_area["levels"] = sorted(plane_sets[0])
        entry["area"] = our_area
        fog = {}
        colour = env.get("fogColor")
        if colour is not None:
            fog["colour"] = hex_colour(colour)
        elif not any(intersects(b, o) for b in boxes for o in overworld):
            fog["colour"] = "#000000"
        fog["depth"] = env.get("fogDepth", 25)
        entry["fog"] = fog
        opacity = env.get("groundFogOpacity", 0)
        if opacity > 0:
            entry["groundFog"] = {
                "start": 4 * env.get("groundFogStart", -200),
                "end": 4 * env.get("groundFogEnd", -500),
                "opacity": opacity,
            }
        generated.append(entry)
    return generated, resolved


def centre(entry):
    area = entry.get("area") or {}
    if area.get("regions"):
        x1, y1, _, _ = region_box(area["regions"][0])
        return x1 + 32, y1 + 32
    if area.get("bounds"):
        b = area["bounds"][0]
        return (b[0] + b[2]) // 2, (b[1] + b[3]) // 2
    return None


def report(hand, resolved):
    for entry in hand:
        c = centre(entry)
        if c is None:
            print("%-20s default" % entry["name"])
            continue
        hit = None
        for area, boxes, env in resolved:
            if "varbitCondition" in env or "varpCondition" in env:
                continue
            if contains(boxes, *c):
                hit = (area, env)
                break
        if hit is None:
            print("%-20s %d,%d: no 117HD environment (its DEFAULT: black, depth 25)" % (entry["name"], c[0], c[1]))
        else:
            area, env = hit
            fog = "fog %s depth %s" % (env.get("fogColor", "(sky)"), env.get("fogDepth", 25))
            if env.get("groundFogOpacity", 0) > 0:
                fog += " ground %s..%s op %s" % (env.get("groundFogStart", -200), env.get("groundFogEnd", -500), env["groundFogOpacity"])
            print("%-20s %d,%d: %s%s %s" % (entry["name"], c[0], c[1], area, " (key %s)" % env["key"] if "key" in env else "", fog))


def dump(doc):
    text = json.dumps(doc, indent=2, ensure_ascii=False)
    # arrays of numbers on one line: [3276, 3195, 3337, 3265]
    text = re.sub(r"\[\s+((?:-?\d+(?:\.\d+)?,\s+)*-?\d+(?:\.\d+)?)\s+\]", lambda m: "[" + re.sub(r",\s+", ", ", m.group(1)) + "]", text)
    return text + "\n"


def main():
    ap = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    ap.add_argument("--rlhd", required=True, help="the 117HD checkout")
    ap.add_argument("--out", default=OURS)
    ap.add_argument("--check", action="store_true")
    ap.add_argument("--report", action="store_true")
    args = ap.parse_args()
    res = os.path.join(args.rlhd, "src/main/resources/rs117/hd/scene")
    areas = {a["name"]: a for a in load(os.path.join(res, "areas.json"))}
    envs = load(os.path.join(res, "environments.json"))
    generated, resolved = convert(envs, areas)
    ours = load(args.out)
    hand = [e for e in ours["environments"] if "source" not in e]
    existing = [e for e in ours["environments"] if "source" in e]
    if args.report or not args.check:
        report(hand, resolved)
    print("%d hand-written entries, %d generated (%d bounds)" % (len(hand), len(generated), sum(len(e["area"]["bounds"]) for e in generated)))
    if args.check:
        if existing == generated:
            print("up to date")
            return 0
        for a, b in zip(existing, generated):
            if a != b:
                print("differs at", a.get("name"), "/", b.get("name"))
                break
        else:
            print("differs in length: %d in the file, %d generated" % (len(existing), len(generated)))
        return 1
    ours["environments"] = hand + generated
    with open(args.out, "w", encoding="utf-8") as f:
        f.write(dump(ours))
    print("wrote", args.out)
    return 0


if __name__ == "__main__":
    sys.exit(main())
