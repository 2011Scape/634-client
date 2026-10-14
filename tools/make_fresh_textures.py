#!/usr/bin/env python3
"""634HD's fresh textures: finer art for game textures, drawn while client.hd.textureReplacements is
on (HdMaterialProvider swaps the colour in at 256x256, keeping the game's alpha, effects and scrolling).

The art comes from /generate (nano-banana-2-lite, Kie.ai): each game texture went in as the reference
image with the prompt in tools/fresh_prompt.txt, and the newest result,
~/Pictures/generations/634hd_fresh-<id>_<time>.*, is used. It never goes in as it is. 634's textures
are mostly grey detail that the face colour tints, and the faces were coloured for them, so a fresh
texture keeps each one's look whole and only adds what the game's 64x64 or 128x128 cannot hold:

  - the game texture, scaled up to 256x256, is the base: its shapes, gradients, brightness and hue
  - the generated image, less a 2% margin, at 256x256, gives the detail finer than the game texture's own texels (its
    log luminance over a blur of itself about one game texel wide), made to tile with a half-offset
    cross-fade along
    each axis
  - that detail is scaled to GAIN times the game texture's own finest detail (one texel wide, at its
    own resolution), so the grain continues the game texture's contrast instead of changing it
  - laid over the base, and then corrected (back-projection) until its mipmap at the game's own size
    is the game texture: at the usual zoom, where the GPU samples that level, it reads exactly as the
    game's did; up close the finer levels show the new grain

Output per texture, in
src/jvmMain/resources/634hd/materials/<id>/:

  albedo.png             the fresh texture, 256x256 (HdMaterials.FINE_SIZE)
  fresh/normal.png ...   material maps from it (tools/make_material_maps.make, fresh=True), which
                         634HD loads instead of the texture's own while the switch is on
  fresh/recipe.txt       how it was made; fresh/generate.json the generation's sidecar

and materials.json gets "albedo": "albedo.png" on the texture's entry (the texture must have one:
add it to make_material_maps.py's TEXTURES first). Then run tools/compress_material_maps.py.
Input: build/textures/<id>.png, written by the client:

  ./gradlew runClient -Pclient.test=smoke -Pclient.test.toolkit=6 '-Pclient.test.findText=textures:'

To make a texture's art again: generate with fresh_prompt.txt ({name} filled in) and the game texture
at 512x512 as the reference, named 634hd_fresh-<id>, then run this. Needs Pillow and NumPy.
"""
import json
import os
import shutil
import sys

import numpy as np
from PIL import Image

sys.path.insert(0, os.path.dirname(__file__))
import make_material_maps as maps  # noqa: E402

ROOT = os.path.join(os.path.dirname(__file__), '..')
SOURCE = os.path.join(ROOT, 'build', 'textures')
OUT = os.path.join(ROOT, 'src', 'jvmMain', 'resources', '634hd', 'materials')
GENERATED = os.path.expanduser('~/Pictures/generations')
SIZE = 256
GAIN = 0.8
# the realistic mode: contrast at least this times the game texture's (fine detail has to survive distance)
CONTRAST_FLOOR = 1.6
# the realistic mode: art with a tiling seam above this (make_material_maps.seam) is cross-faded
SEAM_LIMIT = 1.5
# painterly art: contrast between these times the game texture's
PAINTERLY_CONTRAST = (1.0, 1.3)
EPS = 0.03

# texture id -> (name, material map class, generation it takes its detail from). A texture that is
# another one with a gradient laid over it (90 is 91 darkened towards the bottom) shares its art.
# effect 1 textures: their alpha is a shine mask, which must follow the new art in the realistic mode
SHINE_MASKS = {90, 91, 109}

# ground textures, which are mostly seen from afar (make_realistic lifts their mid-scale contrast)
GROUND = {66, 70, 82, 90, 91, 96, 109, 125, 154, 181, 213, 309}
GROUND_PUNCH = 2.0

# texture id -> (name, material map class, the texture whose art it takes): tools/fresh_catalogue.json,
# the most drawn surface textures across the world (a tour of 27 places, HDDEBUG textures used),
# less effects, cut-outs, scrolling, blank ones and gradients, stripes and other non-materials. A
# texture that is another one with a gradient over it (pattern correlation over 0.9) takes its art.
with open(os.path.join(os.path.dirname(__file__), 'fresh_catalogue.json')) as _f:
    TEXTURES = {int(k): (v['name'], v['class'], v['art']) for k, v in json.load(_f).items()}


def generated(texture):
    names = sorted(f for f in os.listdir(GENERATED) if f.startswith('634hd_fresh-%d_' % texture) and f.endswith(('.png', '.jpg', '.jpeg')))
    return os.path.join(GENERATED, names[-1]) if names else None


def lum(rgb):
    return rgb @ np.array([0.299, 0.587, 0.114])


def wrap_resize(image, size):
    """Resizes a tiling image to size x size without edge seams (as a 3x3 tiling, middle kept)."""
    w, h = image.size
    tiled = Image.new(image.mode, (w * 3, h * 3))
    for y in range(3):
        for x in range(3):
            tiled.paste(image, (x * w, y * h))
    return tiled.resize((size * 3, size * 3), Image.BICUBIC).crop((size, size, size * 2, size * 2))


def tiling_detail(d):
    """Makes a detail field (mean about 0) tile: per axis, a cross-fade with itself shifted by half
    along that axis, the shifted copy (continuous across the wrap) weighted towards the edges and
    unused at the middle, where its own seam lies. Two unrelated fields cross-faded lose contrast in
    between, so each mix is divided by its own spread, sqrt(w^2 + (1 - w)^2)."""
    t = np.abs(np.arange(SIZE) - (SIZE - 1) / 2.0) / (SIZE / 2.0)
    w = np.clip((t - 0.5) / 0.5, 0, 1)
    w = w * w * (3 - 2 * w)
    spread = np.sqrt(w * w + (1 - w) * (1 - w))
    shifted = np.roll(d, SIZE // 2, 1)
    d = (d * (1 - w[None, :]) + shifted * w[None, :]) / spread[None, :]
    shifted = np.roll(d, SIZE // 2, 0)
    return (d * (1 - w[:, None]) + shifted * w[:, None]) / spread[:, None]

def box_down(a, n):
    """[a] (SIZE x SIZE x 3) averaged in n x n blocks: what its mipmaps hold at the game's size."""
    k = SIZE // n
    return a.reshape(n, k, n, k, 3).mean(axis=(1, 3))


def back_project(fresh, game):
    """Corrects [fresh] until its mip level at the game's size is the game texture ([game], n x n x 3):
    what is left of the difference is scaled up smoothly and added, a few times over. At the usual zoom
    the GPU samples that level, so the texture reads exactly as the game's; the new grain stays in the
    finer levels, which show up close."""
    n = game.shape[0]
    for _ in range(8):
        error = game - box_down(fresh, n)
        up = np.stack([np.asarray(wrap_resize(Image.fromarray(error[..., c].astype(np.float32), 'F'), SIZE), dtype=np.float64) for c in range(3)], axis=-1)
        fresh = np.clip(fresh + up, 0.0, 1.0)
    return fresh


def generated_hd(texture):
    """The newest art for [texture]: painterly-realistic (634hd_pt-<id>, tools/pt_prompt.txt) when there
    is one, else photoreal (634hd_hd-<id>; tools/hd_prompt.txt, or hd_prompt_bold.txt for textures
    whose fine detail vanished on screen), or None."""
    for prefix in ('634hd_pt-%d_', '634hd_hd-%d_'):
        names = sorted(f for f in os.listdir(GENERATED) if f.startswith(prefix % texture) and f.endswith(('.png', '.jpg', '.jpeg')))
        if names:
            return os.path.join(GENERATED, names[-1])
    return None


def make_realistic(texture, name, cls, art, path):
    """The realistic mode: the generated art itself is the texture (a 634hd_hd-<id> generation, made
    with tools/hd_prompt.txt and a blurred layout guide from the game texture), fitted to the game:
    grey, with the hue of the game texture's mean colour so the face tints still colour it; its own
    broad light and dark flattened and the game texture's laid over it (90's darker bottom); the
    game texture's mean brightness; its own contrast, kept between CONTRAST_FLOOR and twice the game's."""
    game = Image.open(os.path.join(SOURCE, '%d.png' % texture)).convert('RGB')
    rgb = np.asarray(game, dtype=np.float64) / 255.0
    own = lum(rgb)
    image = Image.open(path).convert('L')
    art_lum = np.asarray(image.resize((SIZE, SIZE), Image.LANCZOS), dtype=np.float64) / 255.0
    # art that tiles by itself is used whole (a cross-fade would ghost its stones); art with a seam is
    # cross-faded along each axis (loose materials: dirt, sand, plaster, straw, where it does not show)
    if maps.seam(art_lum) > SEAM_LIMIT:
        art_lum = tiling_detail(art_lum - art_lum.mean()) + art_lum.mean()
    broad = SIZE / 8
    flat = art_lum / np.maximum(maps.blur(art_lum, broad), 1e-3) * art_lum.mean()
    up = lum(np.asarray(wrap_resize(game, SIZE), dtype=np.float64) / 255.0)
    shading = maps.blur(up, broad)
    shading = shading / max(shading.mean(), 1e-6)
    painterly = os.path.basename(path).startswith('634hd_pt-')
    if texture in GROUND and not painterly:
        # ground seen from afar: pebble clusters a few texels wide are what survives the distance, so
        # that band is lifted (an unsharp mask at that scale) and the contrast goes to the cap
        flat = flat + GROUND_PUNCH * (flat - maps.blur(flat, SIZE / 48))
    detail = flat - flat.mean()
    # painterly art keeps close to the game texture's contrast: its shapes read by themselves
    floor = PAINTERLY_CONTRAST[0] if painterly else 2.0 if texture in GROUND else CONTRAST_FLOOR
    cap = PAINTERLY_CONTRAST[1] if painterly else 2.0
    contrast = float(np.clip(detail.std(), floor * own.std(), cap * own.std()))
    detail *= contrast / max(detail.std(), 1e-6)
    grey = np.clip((own.mean() + detail) * shading, 0.0, 1.0)
    hue = rgb.reshape(-1, 3).mean(axis=0) / max(own.mean(), 1e-6)
    fresh = np.clip(grey[..., None] * hue[None, None, :], 0.0, 1.0)
    pixels = (fresh * 255 + 0.5).astype(np.uint8)
    folder = os.path.join(OUT, str(texture))
    os.makedirs(os.path.join(folder, 'fresh'), exist_ok=True)
    if texture in SHINE_MASKS:
        # effect 1: the game's alpha is a shine mask; the new art's pattern goes in alpha about 128, 40 a
        # standard deviation, and HdMaterialProvider scales it to the game mask's mean and spread
        relative = np.clip(128 + (grey - grey.mean()) / max(grey.std(), 1e-6) * 40, 0, 255).astype(np.uint8)
        Image.fromarray(np.dstack([pixels, relative]), 'RGBA').save(os.path.join(folder, 'albedo.png'), optimize=True)
    else:
        Image.fromarray(pixels, 'RGB').save(os.path.join(folder, 'albedo.png'), optimize=True)
    sidecar = os.path.splitext(path)[0] + '.json'
    if os.path.exists(sidecar):
        shutil.copy(sidecar, os.path.join(folder, 'fresh', 'generate.json'))
    with open(os.path.join(folder, 'fresh', 'recipe.txt'), 'w') as f:
        f.write('%s texture %d (%s), by tools/make_fresh_textures.py from ~/Pictures/generations/%s\n' % ('Painterly' if painterly else 'Realistic', texture, name, os.path.basename(path)))
        f.write('(nano-banana-2 at 2K, tools/pt_prompt.txt or hd_prompt.txt, a blurred layout guide of the game texture as the reference;\n')
        f.write('the prompt is in generate.json). Grey with the game texture\'s hue (%.2f %.2f %.2f), its mean brightness %.2f and broad\n' % (hue[0], hue[1], hue[2], own.mean()))
        f.write('shading; contrast %.3f (game %.3f).%s Tiling seam %.2f.\n' % (contrast, own.std(), ' Shine mask in alpha.' if texture in SHINE_MASKS else '', maps.seam(grey)))
    maps.make(texture, name, cls, fresh='realistic')
    return contrast / max(own.std(), 1e-6)


def make(texture, name, cls, art):
    realistic = generated_hd(art)
    if realistic is not None:
        return make_realistic(texture, name, cls, art, realistic)
    path = generated(art)
    if path is None:
        print('no generation for %d (634hd_fresh-%d_*): skipped' % (texture, art))
        return None
    game = Image.open(os.path.join(SOURCE, '%d.png' % texture)).convert('RGB')
    texel = SIZE / float(game.size[0])
    base = np.asarray(wrap_resize(game, SIZE), dtype=np.float64) / 255.0
    image = Image.open(path).convert('L')
    # generations often have a line of odd pixels along an edge, which the half-offset cross-fade
    # would move to the middle: a margin is cut off first
    margin = image.size[0] // 50
    image = image.crop((margin, margin, image.size[0] - margin, image.size[1] - margin))
    image = np.asarray(image.resize((SIZE, SIZE), Image.LANCZOS), dtype=np.float64) / 255.0
    detail = tiling_detail(np.log(image + EPS) - np.log(maps.blur(image, texel / 2) + EPS))
    own = lum(np.asarray(game, dtype=np.float64) / 255.0)
    own_detail = np.log(own + EPS) - np.log(maps.blur(own, 1) + EPS)
    gain = GAIN * own_detail.std() / max(detail.std(), 1e-6)
    fresh = np.clip(base * np.exp(detail * gain)[..., None], 0.0, 1.0)
    fresh = back_project(fresh, np.asarray(game, dtype=np.float64) / 255.0)
    folder = os.path.join(OUT, str(texture))
    os.makedirs(os.path.join(folder, 'fresh'), exist_ok=True)
    Image.fromarray((fresh * 255 + 0.5).astype(np.uint8), 'RGB').save(os.path.join(folder, 'albedo.png'), optimize=True)
    sidecar = os.path.splitext(path)[0] + '.json'
    if os.path.exists(sidecar):
        shutil.copy(sidecar, os.path.join(folder, 'fresh', 'generate.json'))
    with open(os.path.join(folder, 'fresh', 'recipe.txt'), 'w') as f:
        f.write('Fresh texture %d (%s), by tools/make_fresh_textures.py from ~/Pictures/generations/%s\n' % (texture, name, os.path.basename(path)))
        f.write('(nano-banana-2-lite, Kie.ai, the game texture as the reference; the prompt is in generate.json).\n')
        f.write('The game texture (%dx%d) scaled to %d, with the generation\'s detail finer than one game texel\n' % (game.size[0], game.size[1], SIZE))
        f.write('over it at x%.2f (%.2f times the game texture\'s finest contrast); mean colour kept. Tiling seam %.2f.\n' % (gain, GAIN, maps.seam(lum(fresh))))
    maps.make(texture, name, cls, fresh=True)
    return gain


def main():
    path = os.path.join(ROOT, 'src', 'jvmMain', 'resources', '634hd', 'data', 'materials.json')
    with open(path) as f:
        entries = json.load(f)['materials']
    by_texture = {m['texture']: m for m in entries}
    for texture, (name, cls, art) in sorted(TEXTURES.items()):
        if not os.path.exists(os.path.join(SOURCE, '%d.png' % texture)):
            print('missing build/textures/%d.png: dump the cache textures first' % texture)
            return 1
        if texture not in by_texture:
            # a new entry: its maps are only the fresh ones (fresh/), made below
            specular, parallax = maps.CLASSES[cls][4], maps.CLASSES[cls][5]
            entry = {'texture': texture, 'name': name, 'normal': 'normal.png', 'orm': 'orm.png', 'height': 'normal.png', 'specular': specular}
            if parallax > 0:
                entry['parallax'] = parallax
            entries.append(entry)
            by_texture[texture] = entry
        gain = make(texture, name, cls, art)
        if gain is None:
            continue
        by_texture[texture]['albedo'] = 'albedo.png'
        print('made %d %s (%s x%.2f)' % (texture, name, ('painterly' if os.path.basename(generated_hd(art)).startswith('634hd_pt-') else 'realistic') + ', contrast' if generated_hd(art) else 'fresh, detail', gain))
    with open(path, 'w') as f:
        json.dump({'materials': entries}, f, indent=2)
        f.write('\n')
    return 0


if __name__ == '__main__':
    sys.exit(main())
