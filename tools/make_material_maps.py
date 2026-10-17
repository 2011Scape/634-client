#!/usr/bin/env python3
"""634HD material maps (src/jvmMain/resources/634hd/materials/<texture>/), derived from cache textures.

The cache texture stays the albedo (634hd-plan.md, Phase 5); this makes the extra maps from it, at
256x256 (twice the albedo, so the normal map carries finer detail than the colour):

  normal.png    tangent-space normal (RGB) from a height field, height in alpha (for parallax)
  orm.png       R ambient occlusion (cavities of the height field), G roughness, B emissive mask
  emissive.png  emitted colour (lava only)

The height field is the albedo's luminance (634 textures are mostly grey detail tinted by vertex
colour, so their brightness is their relief), blurred and stretched, with a per-class recipe below.
Every map tiles: filters wrap around the edges. A recipe.txt next to each set records how it was
made. The committed sets with a prompt.txt beside them took extra fine relief from a generated height
map (the prompt is in that file; the generation tooling is not in the repository), which this script
no longer adds: running it remakes such a set from the albedo alone. Input: build/textures/<id>.png,
written by the client:

  ./gradlew runClient -Pclient.test=smoke -Pclient.test.toolkit=6 '-Pclient.test.findText=textures:'

Needs Pillow and NumPy. Run again after changing a recipe; then point materials.json at the maps.
"""
import json, os, sys
import numpy as np
from PIL import Image

ROOT = os.path.join(os.path.dirname(__file__), '..')
SOURCE = os.path.join(ROOT, 'build', 'textures')
OUT = os.path.join(ROOT, 'src', 'jvmMain', 'resources', '634hd', 'materials')
SIZE = 256

# class: (normal strength, height blur, roughness base, roughness from brightness, specular, parallax)
CLASSES = {
    'ground': (2.2, 1.0, 0.85, 0.10, 0.25, 0.03),
    'gravel': (3.0, 0.7, 0.80, 0.15, 0.35, 0.04),
    'grass': (1.6, 0.8, 0.90, 0.05, 0.15, 0.0),
    'sand': (1.4, 1.2, 0.85, 0.05, 0.20, 0.02),
    'stone': (3.2, 1.0, 0.70, 0.20, 0.45, 0.04),
    'brick': (3.6, 0.8, 0.75, 0.15, 0.40, 0.05),
    'wood': (2.4, 0.9, 0.65, 0.20, 0.40, 0.0),
    'water': (1.0, 2.0, 0.08, 0.0, 1.20, 0.0),
    'lava': (2.0, 1.5, 0.55, 0.0, 0.60, 0.0),
    'fabric': (1.2, 1.0, 0.92, 0.04, 0.10, 0.0),
}

# the test set: texture id -> (name, class)
TEXTURES = {
    91: ('gravel path', 'gravel'), 82: ('gravel', 'gravel'), 154: ('sand', 'sand'),
    265: ('stony ground', 'ground'), 181: ('rocky ground', 'ground'), 510: ('cracked earth', 'ground'),
    66: ('loose rock', 'gravel'), 96: ('pebbles', 'gravel'), 519: ('grass', 'grass'),
    276: ('meadow grass', 'grass'), 70: ('packed dirt', 'ground'),
    83: ('rock face', 'stone'), 72: ('rough rock', 'stone'), 105: ('boulder rock', 'stone'),
    505: ('flagstones', 'brick'), 322: ('stone wall', 'brick'), 202: ('brick pattern', 'brick'),
    63: ('wooden planks', 'wood'), 132: ('bark', 'wood'), 160: ('wood boards', 'wood'),
    228: ('wood grain', 'wood'), 638: ('water', 'water'), 110: ('lava', 'lava'),
    # Lumbridge and Varrock: the most used of their textures without maps (HDDEBUG textures used)
    90: ('pebbled ground', 'ground'), 309: ('rocky ground', 'ground'), 127: ('streaked stone', 'stone'),
    108: ('mossy patches', 'grass'), 185: ('straw grass', 'grass'), 213: ('fine sand', 'sand'),
    125: ('speckled gravel', 'gravel'), 75: ('wood streaks', 'wood'), 387: ('wood grain fine', 'wood'),
    163: ('wall boards', 'wood'), 159: ('weathered boards', 'wood'),
    # for the fresh textures (albedo.png beside a set): more of the two areas' most used ones
    254: ('mottled plaster', 'sand'), 140: ('mottled plaster, shaded', 'sand'), 294: ('rough plaster', 'sand'),
    295: ('rough plaster, shaded', 'sand'), 109: ('pebbled ground, shaded', 'ground'),
}


def blur(a, radius):
    """Separable box blur, three passes (about Gaussian), wrapping at the edges."""
    if radius <= 0:
        return a
    r = max(1, int(round(radius)))
    for _ in range(3):
        acc = np.zeros_like(a)
        for d in range(-r, r + 1):
            acc += np.roll(a, d, axis=1)
        a = acc / (2 * r + 1)
        acc = np.zeros_like(a)
        for d in range(-r, r + 1):
            acc += np.roll(a, d, axis=0)
        a = acc / (2 * r + 1)
    return a


# how much of a fresh albedo's fine grain goes into its relief (make, fresh=True)
FRESH_RELIEF = 1.5


def wrap_resize(image, mode):
    """Resizes a tiling image to SIZE x SIZE without edge seams: resampled as a 3x3 tiling, middle kept."""
    w, h = image.size
    tiled = Image.new(image.mode, (w * 3, h * 3))
    for y in range(3):
        for x in range(3):
            tiled.paste(image, (x * w, y * h))
    big = tiled.resize((SIZE * 3, SIZE * 3), mode)
    return big.crop((SIZE, SIZE, SIZE * 2, SIZE * 2))


def make(texture, name, cls, fresh=False):
    """The maps for [texture]; [fresh]: with its fresh albedo (materials/<id>/albedo.png), into
    materials/<id>/fresh/, which 634HD loads instead while client.hd.textureReplacements is on."""
    strength, soften, rough_base, rough_var, specular, parallax = CLASSES[cls]
    out = os.path.join(OUT, str(texture), 'fresh') if fresh else os.path.join(OUT, str(texture))
    # the relief is the cache texture's in both sets, so the shapes read the same; a fresh set adds the
    # fresh albedo's grain (finer than a game texel) as a little extra relief, and takes its colour
    albedo = Image.open(os.path.join(SOURCE, '%d.png' % texture)).convert('RGB')
    big = wrap_resize(albedo, Image.BICUBIC)
    rgb = np.asarray(big, dtype=np.float64) / 255.0
    lum = rgb @ np.array([0.299, 0.587, 0.114])
    grain = None
    if fresh == 'realistic':
        # the realistic mode: the relief is the new art's broad shapes only (a wide blur) at a quarter
        # of the strength - its fine detail is already in the colour, and lit as relief too it doubled
        strength *= 0.25
        soften = max(soften, 5.0)
        rgb = np.asarray(wrap_resize(Image.open(os.path.join(OUT, str(texture), 'albedo.png')).convert('RGB'), Image.BICUBIC), dtype=np.float64) / 255.0
        lum = rgb @ np.array([0.299, 0.587, 0.114])
    elif fresh:
        rgb = np.asarray(wrap_resize(Image.open(os.path.join(OUT, str(texture), 'albedo.png')).convert('RGB'), Image.BICUBIC), dtype=np.float64) / 255.0
        fresh_lum = rgb @ np.array([0.299, 0.587, 0.114])
        grain = fresh_lum - blur(fresh_lum, 2)
    height = blur(lum, soften)
    low, high = np.percentile(height, 2), np.percentile(height, 98)
    height = np.clip((height - low) / max(high - low, 1e-6), 0.0, 1.0)
    if grain is not None:
        height = np.clip(height + grain * FRESH_RELIEF, 0.0, 1.0)
    # normals from the height field's slopes (Sobel, wrapping); y up in the texture is -v
    dx = (np.roll(height, -1, 1) - np.roll(height, 1, 1)) * 2 + (np.roll(np.roll(height, -1, 1), 1, 0) - np.roll(np.roll(height, 1, 1), 1, 0)) + (np.roll(np.roll(height, -1, 1), -1, 0) - np.roll(np.roll(height, 1, 1), -1, 0))
    dy = (np.roll(height, -1, 0) - np.roll(height, 1, 0)) * 2 + (np.roll(np.roll(height, -1, 0), 1, 1) - np.roll(np.roll(height, 1, 0), 1, 1)) + (np.roll(np.roll(height, -1, 0), -1, 1) - np.roll(np.roll(height, 1, 0), -1, 1))
    nx, ny, nz = -dx * strength, dy * strength, np.ones_like(height)
    length = np.sqrt(nx * nx + ny * ny + nz * nz)
    normal = np.stack([nx / length, ny / length, nz / length], axis=-1) * 0.5 + 0.5
    normal_rgba = np.concatenate([normal, height[..., None]], axis=-1)
    Image.fromarray((normal_rgba * 255 + 0.5).astype(np.uint8), 'RGBA').save(os.path.join(out, 'normal.png'))
    # occlusion: how far a point sits below its surroundings
    cavity = np.clip((blur(height, 6) - height) * 2.5, 0.0, 1.0)
    ao = 1.0 - cavity * (0.35 if fresh == 'realistic' else 0.8)
    rough = np.clip(rough_base - (lum - lum.mean()) * rough_var * 2.0, 0.04, 1.0)
    if cls == 'lava':
        r, g, b = rgb[..., 0], rgb[..., 1], rgb[..., 2]
        mask = np.clip((r - b) * 1.6 + (g - b) * 0.6 - 0.25, 0.0, 1.0)
    else:
        mask = np.zeros_like(height)
    orm = np.stack([ao, rough, mask], axis=-1)
    Image.fromarray((orm * 255 + 0.5).astype(np.uint8), 'RGB').save(os.path.join(out, 'orm.png'))
    emissive = rgb * mask[..., None] * 1.2
    # only lava glows: elsewhere there is no emitted-colour map (HdMaterialMaps uploads none)
    if cls == 'lava':
        Image.fromarray((np.clip(emissive, 0, 1) * 255 + 0.5).astype(np.uint8), 'RGB').save(os.path.join(out, 'emissive.png'))
    elif os.path.exists(os.path.join(out, 'emissive.png')):
        os.remove(os.path.join(out, 'emissive.png'))
    with open(os.path.join(out, 'recipe.txt'), 'w') as f:
        f.write('Derived by tools/make_material_maps.py from %s %d (%s), class %s:\n' % ('the fresh albedo of texture' if fresh else 'cache texture', texture, name, cls))
        f.write('normal strength %.2f, height blur %.2f, roughness %.2f +- %.2f by brightness, specular %.2f, parallax %.3f.\n' % (strength, soften, rough_base, rough_var, specular, parallax))
        f.write('Height is the albedo luminance at %dx%d, blurred and stretched to 2-98%%; normals by Sobel; AO from cavities.\n' % (SIZE, SIZE))
    entry = {'texture': texture, 'name': name, 'normal': 'normal.png', 'orm': 'orm.png', 'height': 'normal.png', 'specular': specular}
    if cls == 'lava':
        entry['emissive'] = 'emissive.png'
        entry['emissiveStrength'] = 2.5
    if parallax > 0:
        entry['parallax'] = parallax
    return entry


def main():
    entries = []
    for texture, (name, cls) in sorted(TEXTURES.items()):
        if not os.path.exists(os.path.join(SOURCE, '%d.png' % texture)):
            print('missing build/textures/%d.png: dump the cache textures first' % texture)
            return 1
        os.makedirs(os.path.join(OUT, str(texture)), exist_ok=True)
        entries.append(make(texture, name, cls))
        print('made', texture, name)
    # entries this tool does not make (634HD's own materials, 10000 and up) are kept
    path = os.path.join(ROOT, 'src', 'jvmMain', 'resources', '634hd', 'data', 'materials.json')
    made = set(TEXTURES)
    with open(path) as f:
        old = json.load(f)['materials']
    kept = [m for m in old if m['texture'] not in made]
    # a fresh texture's albedo (albedo.png beside its set) and the hand-written flow fields (the
    # fire cape, lava: 117HD's flowing, unlit materials) stay on its entry
    carried = ('albedo', 'flowMap', 'flowStrength', 'flowDuration', 'uvScroll', 'unlit', 'light')
    for m in old:
        if m['texture'] in made:
            for e in entries:
                if e['texture'] == m['texture']:
                    for key in carried:
                        if key in m:
                            e[key] = m[key]
    entries += kept
    with open(path, 'w') as f:
        json.dump({'materials': entries}, f, indent=2)
        f.write('\n')
    return 0


if __name__ == '__main__':
    sys.exit(main())
