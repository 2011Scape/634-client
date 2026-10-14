#!/usr/bin/env python3
"""634HD colour-grading strips (src/jvmMain/resources/634hd/luts/*.png), made from formulas.

Each is a 16x16x16 colour cube laid out as 16 slices of 16x16 side by side (256x16), the layout
post_composite.frag reads: red along x within a slice, green along y, blue picking the slice. A
starting point for environments.json's "lut"; edit the grades below and run again (needs Pillow)."""
import colorsys, os, sys
from PIL import Image

SIZE = 16
OUT = os.path.join(os.path.dirname(__file__), '..', 'src', 'jvmMain', 'resources', '634hd', 'luts')

def clamp(v):
    return max(0.0, min(1.0, v))

def grade(r, g, b, saturation=1.0, warmth=0.0, contrast=1.0, tint=(0.0, 0.0, 0.0), lift=0.0):
    h, l, s = colorsys.rgb_to_hls(r, g, b)
    r, g, b = colorsys.hls_to_rgb(h, l, clamp(s * saturation))
    r, b = r + warmth, b - warmth
    r, g, b = [(c - 0.5) * contrast + 0.5 + lift for c in (r, g, b)]
    return [clamp(c + t) for c, t in zip((r, g, b), tint)]

GRADES = {
    'neutral_warm.png': dict(saturation=1.08, warmth=0.015, contrast=1.04),
    'swamp.png': dict(saturation=0.8, tint=(-0.01, 0.02, -0.01), contrast=0.97, lift=0.01),
    'volcanic.png': dict(saturation=1.15, warmth=0.05, contrast=1.08),
    'dungeon.png': dict(saturation=0.7, warmth=-0.01, contrast=1.1, lift=-0.02),
    'desert.png': dict(saturation=1.05, warmth=0.03, contrast=1.03, lift=0.01),
}

def main():
    os.makedirs(OUT, exist_ok=True)
    for name, params in GRADES.items():
        image = Image.new('RGB', (SIZE * SIZE, SIZE))
        for b in range(SIZE):
            for g in range(SIZE):
                for r in range(SIZE):
                    c = grade(r / (SIZE - 1), g / (SIZE - 1), b / (SIZE - 1), **params)
                    image.putpixel((b * SIZE + r, g), tuple(int(round(v * 255)) for v in c))
        image.save(os.path.join(OUT, name))
        print('wrote', name)

if __name__ == '__main__':
    sys.exit(main())
