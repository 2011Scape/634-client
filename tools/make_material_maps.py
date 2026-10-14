#!/usr/bin/env python3
"""634HD material maps (src/jvmMain/resources/634hd/materials/<texture>/), derived from cache textures.

The cache texture stays the albedo (634hd-plan.md, Phase 5); this makes the extra maps from it, at
256x256 (twice the albedo, so the normal map carries finer detail than the colour):

  normal.png    tangent-space normal (RGB) from a height field, height in alpha (for parallax)
  orm.png       R ambient occlusion (cavities of the height field), G roughness, B emissive mask
  emissive.png  emitted colour (lava, fire), black elsewhere

The height field is the albedo's luminance (634 textures are mostly grey detail tinted by vertex
colour, so their brightness is their relief), blurred and stretched, with a per-class recipe below.
Every map tiles: filters wrap around the edges. A recipe.txt next to each set records how it was
made. Input: build/textures/<id>.png, written by the client:

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
    # for the fresh textures (tools/make_fresh_textures.py): more of the two areas' most used ones
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


GENERATED = os.path.expanduser('~/Pictures/generations')

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


def generated_height(texture):
    """The newest /generate height map for [texture] (634hd_height-<id>_<time>.jpg|png), as a tiling
    256x256 array in 0..1, with its prompt; (None, None) if there is none."""
    if not os.path.isdir(GENERATED):
        return None, None
    names = sorted(f for f in os.listdir(GENERATED) if f.startswith('634hd_height-%d_' % texture) and f.endswith(('.png', '.jpg', '.jpeg')))
    if not names:
        return None, None
    path = os.path.join(GENERATED, names[-1])
    image = np.asarray(Image.open(path).convert('L').resize((SIZE, SIZE), Image.BICUBIC), dtype=np.float64) / 255.0
    image = make_tiling(image)
    sidecar = os.path.splitext(path)[0] + '.json'
    prompt = json.load(open(sidecar)).get('prompt') if os.path.exists(sidecar) else None
    if isinstance(prompt, dict):
        prompt = prompt.get('prompt')
    return image, (os.path.basename(path), prompt, sidecar if os.path.exists(sidecar) else None)


def make_tiling(a):
    """Removes edge seams: a cross-fade with the image shifted by half, weighted towards the shifted
    copy near the edges (where it is continuous if the image's middle is) and the original in the
    middle. A generated image may have a seam of its own down the middle, which the shift would move
    to the edge: whichever of the two tiles better is kept."""
    shifted = np.roll(np.roll(a, SIZE // 2, 0), SIZE // 2, 1)
    t = np.abs(np.arange(SIZE) - (SIZE - 1) / 2.0) / (SIZE / 2.0)
    w = np.clip((np.maximum(t[:, None], t[None, :]) - 0.6) / 0.4, 0, 1)
    blended = a * (1 - w) + shifted * w
    return blended if seam(blended) < seam(a) else a


def fix_edges(a):
    """Cancels the jump across both wraps with a smooth linear ramp (rows, then columns): the edges
    then meet exactly, and nothing finer than the ramp changes."""
    ramp = np.arange(SIZE) / (SIZE - 1.0) - 0.5
    for _ in range(6):
        # each pass disturbs the other direction's edges a little: a few passes settle both
        jump = a[:, 0] - a[:, -1]
        a = a + jump[:, None] * ramp[None, :]
        jump = a[0, :] - a[-1, :]
        a = a + jump[None, :] * ramp[:, None]
    return a


def seam(a):
    """How visible the tiling seam is: the mean jump across the wrap, over the mean jump inside."""
    across = (np.abs(a[:, 0] - a[:, -1]).mean() + np.abs(a[0, :] - a[-1, :]).mean()) / 2
    inside = (np.abs(np.diff(a, axis=1)).mean() + np.abs(np.diff(a, axis=0)).mean()) / 2
    return across / max(inside, 1e-6)


def make(texture, name, cls, fresh=False):
    """The maps for [texture]; [fresh]: with its fresh albedo (tools/make_fresh_textures.py), into
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
    if fresh:
        rgb = np.asarray(wrap_resize(Image.open(os.path.join(OUT, str(texture), 'albedo.png')).convert('RGB'), Image.BICUBIC), dtype=np.float64) / 255.0
        fresh_lum = rgb @ np.array([0.299, 0.587, 0.114])
        grain = fresh_lum - blur(fresh_lum, 2)
    height = blur(lum, soften)
    low, high = np.percentile(height, 2), np.percentile(height, 98)
    height = np.clip((height - low) / max(high - low, 1e-6), 0.0, 1.0)
    # a /generate height map adds the fine relief: its high frequencies only, over the albedo's own
    # shapes, so the stones and planks stay where the cache texture draws them
    generated, source = generated_height(texture)
    if generated is not None:
        detail = generated - blur(generated, 8)
        detail /= max(np.abs(detail).max(), 1e-6)
        height = np.clip(height * 0.75 + 0.25 * (0.5 + detail * 0.5) + (detail * 0.15), 0.0, 1.0)
        low, high = np.percentile(height, 2), np.percentile(height, 98)
        height = np.clip((height - low) / max(high - low, 1e-6), 0.0, 1.0)
        # where the cache texture tiles cleanly, the result must too (plan rule 4); where the cache
        # texture has its own seam (board edges, a frame), the height keeps it, matching the colour
        if seam(height) > max(1.5, seam(lum) * 1.2):
            height = make_tiling(height)
        # and the edges made to meet exactly (a low-frequency ramp, invisible inside the tile)
        height = np.clip(fix_edges(height), 0.0, 1.0)
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
    ao = 1.0 - cavity * 0.8
    rough = np.clip(rough_base - (lum - lum.mean()) * rough_var * 2.0, 0.04, 1.0)
    if cls == 'lava':
        r, g, b = rgb[..., 0], rgb[..., 1], rgb[..., 2]
        mask = np.clip((r - b) * 1.6 + (g - b) * 0.6 - 0.25, 0.0, 1.0)
    else:
        mask = np.zeros_like(height)
    orm = np.stack([ao, rough, mask], axis=-1)
    Image.fromarray((orm * 255 + 0.5).astype(np.uint8), 'RGB').save(os.path.join(out, 'orm.png'))
    emissive = rgb * mask[..., None] * 1.2
    Image.fromarray((np.clip(emissive, 0, 1) * 255 + 0.5).astype(np.uint8), 'RGB').save(os.path.join(out, 'emissive.png'))
    if generated is not None:
        name_, prompt, sidecar = source
        with open(os.path.join(out, 'prompt.txt'), 'w') as f:
            f.write('Height detail generated with /generate (nano-banana-2-lite, Kie.ai), reference: the cache texture %d.\n' % texture)
            f.write('Output: ~/Pictures/generations/%s (sidecar beside it).\n\nPrompt:\n%s\n' % (name_, prompt or '(see sidecar)'))
            f.write('\nUsed as: high-pass (minus a 8-texel blur) over the albedo-derived height, made tileable by a half-offset cross-fade.\n')
            f.write('Tiling seam (jump across the wrap / jump inside): %.2f\n' % seam(height))
        if sidecar:
            import shutil
            shutil.copy(sidecar, os.path.join(out, 'generate.json'))
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
    # a fresh texture's albedo (tools/make_fresh_textures.py) stays on its entry
    for m in old:
        if m['texture'] in made and 'albedo' in m:
            for e in entries:
                if e['texture'] == m['texture']:
                    e['albedo'] = m['albedo']
    entries += kept
    with open(path, 'w') as f:
        json.dump({'materials': entries}, f, indent=2)
        f.write('\n')
    return 0


if __name__ == '__main__':
    sys.exit(main())
