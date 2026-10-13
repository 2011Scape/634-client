#!/usr/bin/env python3
"""Precompresses 634HD's material maps (634hd/materials/<texture>/) for the GPU, at build time.

  normal.png   -> normal.bc5    x, y of the normal (BC5 / RGTC2; the shader rebuilds z)
  orm.png      -> orm.bc7       occlusion, roughness, emissive mask, and normal.png's alpha (the
                                height) in alpha (BC7 mode 6)
  emissive.png -> emissive.bc7  emitted colour (BC7 mode 6)

Each file is every mip level from 256x256 down to 1x1 (box filtered), level after level, as raw
blocks with no header: HdMaterialMaps knows the size. Run after tools/make_material_maps.py or after
editing a map; HdMaterialMaps uploads these as they are (no compression when the game loads) and
falls back to the PNGs where they are missing. Needs Pillow and NumPy.
"""
import os, sys
import numpy as np
from PIL import Image

ROOT = os.path.join(os.path.dirname(__file__), '..')
MATERIALS = os.path.join(ROOT, 'src', 'jvmMain', 'resources', '634hd', 'materials')
SIZE = 256
BC7_WEIGHTS = np.array([0, 4, 9, 13, 17, 21, 26, 30, 34, 38, 43, 47, 51, 55, 60, 64], dtype=np.float64)


def mips(rgba):
    """Every level of a square RGBA float image down to 1x1, box filtered."""
    levels = [rgba]
    while levels[-1].shape[0] > 1:
        a = levels[-1]
        levels.append((a[0::2, 0::2] + a[1::2, 0::2] + a[0::2, 1::2] + a[1::2, 1::2]) / 4.0)
    return levels


def blocks(image):
    """(n, 16, c) 4x4 blocks in row order, padding images under 4x4 by repeating edge texels."""
    h, w, c = image.shape
    if h < 4 or w < 4:
        image = np.pad(image, ((0, max(0, 4 - h)), (0, max(0, 4 - w)), (0, 0)), mode='edge')
        h, w = image.shape[:2]
    b = image.reshape(h // 4, 4, w // 4, 4, c).transpose(0, 2, 1, 3, 4).reshape(-1, 16, c)
    return b


def bc4(values):
    """BC4 blocks (8 bytes each) for (n, 16) values in 0..255."""
    lo = values.min(axis=1)
    hi = values.max(axis=1)
    r0 = np.round(hi).astype(np.int64)
    r1 = np.round(lo).astype(np.int64)
    flat = r0 == r1
    r0 = np.where(flat & (r0 < 255), r0 + 1, r0)
    r1 = np.where(flat & (r0 == 255) & (r1 == 255), 254, r1)
    # eight-value mode (r0 > r1): palette i = 0 -> r0, 1 -> r1, 2..7 -> interpolations
    palette = np.stack([r0, r1] + [((8 - k) * r0 + (k - 1) * r1) / 7.0 for k in range(2, 8)], axis=1)
    index = np.abs(values[:, :, None] - palette[:, None, :]).argmin(axis=2).astype(np.uint64)
    bits = np.zeros(values.shape[0], dtype=np.uint64)
    for i in range(16):
        bits |= index[:, i] << np.uint64(3 * i)
    out = np.zeros((values.shape[0], 8), dtype=np.uint8)
    out[:, 0] = r0
    out[:, 1] = r1
    for k in range(6):
        out[:, 2 + k] = (bits >> np.uint64(8 * k)) & np.uint64(255)
    return out


def bc5(image):
    b = blocks(image[:, :, :2])
    return np.concatenate([bc4(b[:, :, 0]), bc4(b[:, :, 1])], axis=1).tobytes()


def bc7_mode6(image):
    """BC7 mode 6 blocks (16 bytes each): one subset, RGBA endpoints 7 bits plus a p-bit, 4-bit indices."""
    b = blocks(image)
    n = b.shape[0]
    lo = b.min(axis=1)
    hi = b.max(axis=1)

    def quantise(e):
        # 8-bit value -> (7-bit, p-bit) giving the nearest representable value, the p-bit shared by the channels
        best = None
        for p in (0, 1):
            q = np.clip(np.round((e - p) / 2.0), 0, 127).astype(np.int64)
            v = q * 2 + p
            err = np.abs(v - e).sum(axis=1)
            if best is None:
                best = (q, np.full(n, p), err, v)
            else:
                take = err < best[2]
                best = (np.where(take[:, None], q, best[0]), np.where(take, p, best[1]), np.where(take, err, best[2]), np.where(take[:, None], v, best[3]))
        return best[0], best[1], best[3]

    q0, p0, e0 = quantise(lo)
    q1, p1, e1 = quantise(hi)
    weights = BC7_WEIGHTS / 64.0
    palette = e0[:, None, :] * (1 - weights[None, :, None]) + e1[:, None, :] * weights[None, :, None]
    index = ((b[:, :, None, :] - palette[:, None, :, :]) ** 2).sum(axis=3).argmin(axis=2)
    # the first index's top bit is implied 0: swap the endpoints where it is set
    swap = index[:, 0] >= 8
    q0s = np.where(swap[:, None], q1, q0)
    q1s = np.where(swap[:, None], q0, q1)
    p0s = np.where(swap, p1, p0)
    p1s = np.where(swap, p0, p1)
    index = np.where(swap[:, None], 15 - index, index)
    out = np.zeros((n, 16), dtype=np.uint8)
    for row in range(n):
        bits = 1 << 6                                   # mode 6
        pos = 7
        for c in range(4):
            bits |= int(q0s[row, c]) << pos
            pos += 7
            bits |= int(q1s[row, c]) << pos
            pos += 7
        bits |= int(p0s[row]) << pos
        pos += 1
        bits |= int(p1s[row]) << pos
        pos += 1
        for i in range(16):
            width = 3 if i == 0 else 4
            bits |= int(index[row, i]) << pos
            pos += width
        out[row] = np.frombuffer(bits.to_bytes(16, 'little'), dtype=np.uint8)
    return out.tobytes()


def load(path):
    return np.asarray(Image.open(path).convert('RGBA').resize((SIZE, SIZE), Image.BICUBIC), dtype=np.float64)


def main():
    done = 0
    for name in sorted(os.listdir(MATERIALS), key=lambda s: int(s) if s.isdigit() else -1):
        folder = os.path.join(MATERIALS, name)
        if not os.path.isdir(folder):
            continue
        normal = load(os.path.join(folder, 'normal.png')) if os.path.exists(os.path.join(folder, 'normal.png')) else None
        if normal is not None:
            with open(os.path.join(folder, 'normal.bc5'), 'wb') as f:
                for level in mips(normal):
                    f.write(bc5(level))
        if os.path.exists(os.path.join(folder, 'orm.png')):
            orm = load(os.path.join(folder, 'orm.png'))
            orm[:, :, 3] = normal[:, :, 3] if normal is not None else 255.0
            with open(os.path.join(folder, 'orm.bc7'), 'wb') as f:
                for level in mips(orm):
                    f.write(bc7_mode6(level))
        if os.path.exists(os.path.join(folder, 'emissive.png')):
            with open(os.path.join(folder, 'emissive.bc7'), 'wb') as f:
                for level in mips(load(os.path.join(folder, 'emissive.png'))):
                    f.write(bc7_mode6(level))
        done += 1
        print('compressed', name)
    print(done, 'material folders')
    return 0


if __name__ == '__main__':
    sys.exit(main())
