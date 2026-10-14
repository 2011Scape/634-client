/*
 * Not part of the original client: 634HD (toolkit 6). The game's material provider, plus materials of
 * 634HD's own: a materials.json entry with a texture id from CUSTOM_MATERIALS up and an albedo file in
 * its folder (634hd/materials/<texture>/). They draw like a plain repeating, mipmapped texture
 * modulated by the face colour, so they are meant as near-grey detail the game's colours tint; model
 * and tile overrides put them on faces and tiles the game leaves untextured. Every other id goes to
 * the game's provider unchanged.
 */
package com.jagex.game.runetek5.graphics.hd.content

import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.config.material.MaterialType

class HdMaterialProvider(private val game: MaterialProvider?) : MaterialProvider {

    companion object {
        /** The first id of 634HD's own materials: above any the 2010 cache has. */
        const val CUSTOM_MATERIALS: Int = 10000
    }

    /** Where the custom materials are defined (materials.json); set by the toolkit once it has it. */
    var content: HdContent? = null

    private val metrics: HashMap<Int, MaterialType> = HashMap()
    private val images: HashMap<Int, java.awt.image.BufferedImage?> = HashMap()
    private var contentVersion: Int = -1

    private fun image(id: Int): java.awt.image.BufferedImage? {
        val content: HdContent = this.content ?: return null
        if (content.version != contentVersion) {
            // the definitions changed (hot reload): read the files again
            contentVersion = content.version
            images.clear()
        }
        if (images.containsKey(id)) return images[id]
        val name: String? = content.materials[id]?.albedo
        val bytes: ByteArray? = if (name != null) content.materialFile(id, name) else null
        val image: java.awt.image.BufferedImage? = try {
            if (bytes != null) javax.imageio.ImageIO.read(java.io.ByteArrayInputStream(bytes)) else null
        } catch (ex: Exception) {
            System.err.println("634HD: cannot read material " + id + " " + name + ": " + ex)
            null
        }
        images[id] = image
        return image
    }

    override fun getMetrics(materialId: Int): MaterialType? {
        if (materialId < CUSTOM_MATERIALS) return game?.getMetrics(materialId)
        if (image(materialId) == null) return null
        return metrics.getOrPut(materialId) {
            val type = MaterialType()
            type.isSmall = false
            type.repeatS = true
            type.repeatT = true
            type.mipmapping = 1
            type
        }
    }

    override fun textureAvailable(materialId: Int): Boolean {
        if (materialId < CUSTOM_MATERIALS) return game?.textureAvailable(materialId) ?: false
        return image(materialId) != null
    }

    override fun getPixels(readReversed: Boolean, materialId: Int, brightness: Float, width: Int, height: Int): IntArray? {
        if (materialId < CUSTOM_MATERIALS) {
            if (!fresh(materialId)) return game?.getPixels(readReversed, materialId, brightness, width, height)
            val size: Int = native(materialId)
            return replaced(materialId, width, height, game?.getPixels(readReversed, materialId, brightness, size, size), size, readReversed)
        }
        return pixels(materialId, width, height)
    }

    override fun getPixelsArgb(height: Int, brightness: Float, materialId: Int, width: Int): IntArray? {
        if (materialId < CUSTOM_MATERIALS) {
            if (!fresh(materialId)) return game?.getPixelsArgb(height, brightness, materialId, width)
            val size: Int = native(materialId)
            return replaced(materialId, width, height, game?.getPixelsArgb(size, brightness, materialId, size), size, true)
        }
        return pixels(materialId, width, height)
    }

    /** Whether [materialId] draws as a fresh texture now (a game texture with an albedo, the switch on). */
    fun fresh(materialId: Int): Boolean = materialId < CUSTOM_MATERIALS && com.jagex.game.runetek5.client.features.HdSettings.textureReplacements &&
        content?.materials?.get(materialId)?.albedo != null

    /**
     * A fresh texture's pixels at [width] x [height]: the albedo's colours with the alpha of the game's
     * own pixels ([original], [size] square, scaled up: its cut-outs, transparency and masks stay as
     * they were); [flipped] mirrors it across, as the game's are
     * (readReversed, and always in the ARGB path: measured against the dumped textures).
     */
    private fun replaced(materialId: Int, width: Int, height: Int, original: IntArray?, size: Int, flipped: Boolean): IntArray? {
        if (original == null) return null
        val fresh: IntArray = pixels(materialId, width, height, true) ?: return original
        val relative: Boolean = image(materialId)?.colorModel?.hasAlpha() == true
        val mask: FloatArray = if (relative) relativeMask(original, size, fresh) else mask(original, size, width)
        val out = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val from: Int = if (flipped) y * width + (width - 1 - x) else y * width + x
                val a: Int = (mask[if (relative) from else y * width + x] + 0.5f).toInt().coerceIn(0, 255)
                out[y * width + x] = (a shl 24) or (fresh[from] and 0xFFFFFF)
            }
        }
        return out
    }

    /**
     * A mask that follows the new art: an albedo with alpha (tools/make_fresh_textures.py writes one
     * for effect 1's shine mask) holds a pattern about 128, 40 a standard deviation; it is scaled to
     * the game's own mask ([pixels], [size] square): the same mean and spread, the new art's shapes.
     */
    private fun relativeMask(pixels: IntArray, size: Int, fresh: IntArray): FloatArray {
        var sum = 0.0
        var squares = 0.0
        for (i in 0 until size * size) {
            val a: Int = pixels[i] ushr 24
            sum += a
            squares += a.toDouble() * a
        }
        val mean: Double = sum / (size * size)
        val spread: Double = Math.sqrt(Math.max(0.0, squares / (size * size) - mean * mean))
        val out = FloatArray(fresh.size)
        for (i in fresh.indices) {
            out[i] = (mean + ((fresh[i] ushr 24) - 128) * spread / 40.0).toFloat()
        }
        return out
    }

    /**
     * The alpha of [pixels] ([size] square) scaled up to [width] square: bilinear, then corrected a few
     * times (back-projection) until its mipmap at [size] is the game's alpha again. At the usual zoom
     * the GPU samples that level, so cut-outs and the specular mask of effect 1 (the wet cobbles of
     * Varrock) read as the game's; a plain bilinear scale would blur them there.
     */
    private fun mask(pixels: IntArray, size: Int, width: Int): FloatArray {
        val target = FloatArray(size * size)
        for (i in target.indices) target[i] = (pixels[i] ushr 24).toFloat()
        val out = FloatArray(width * width)
        val error = FloatArray(size * size)
        val block: Int = width / size
        System.arraycopy(target, 0, error, 0, error.size)
        for (pass in 0 until 6) {
            for (y in 0 until width) {
                for (x in 0 until width) {
                    out[y * width + x] += sample(error, size, (x + 0.5f) / block, (y + 0.5f) / block)
                }
            }
            if (block <= 1) break
            for (ty in 0 until size) {
                for (tx in 0 until size) {
                    var sum = 0.0f
                    for (y in ty * block until (ty + 1) * block) {
                        for (x in tx * block until (tx + 1) * block) {
                            sum += out[y * width + x].coerceIn(0.0f, 255.0f)
                        }
                    }
                    error[ty * size + tx] = target[ty * size + tx] - sum / (block * block)
                }
            }
        }
        return out
    }

    /** The game's size for [materialId]'s texture (64 for a small one, else 128). */
    private fun native(materialId: Int): Int = if (game?.getMetrics(materialId)?.isSmall == true) 64 else 128

    /** [values] ([size] square) at texel position [x], [y], bilinear and wrapping. */
    private fun sample(values: FloatArray, size: Int, x: Float, y: Float): Float {
        val fx: Float = x - 0.5f
        val fy: Float = y - 0.5f
        val bx: Int = Math.floor(fx.toDouble()).toInt()
        val by: Int = Math.floor(fy.toDouble()).toInt()
        val x0: Int = Math.floorMod(bx, size)
        val y0: Int = Math.floorMod(by, size)
        val x1: Int = (x0 + 1) % size
        val y1: Int = (y0 + 1) % size
        val tx: Float = fx - bx
        val ty: Float = fy - by
        val top: Float = values[y0 * size + x0] + (values[y0 * size + x1] - values[y0 * size + x0]) * tx
        val bottom: Float = values[y1 * size + x0] + (values[y1 * size + x1] - values[y1 * size + x0]) * tx
        return top + (bottom - top) * ty
    }

    override fun floatArgbOutput(width: Int, brightness: Float, height: Int, materialId: Int): FloatArray? {
        if (materialId < CUSTOM_MATERIALS) return game?.floatArgbOutput(width, brightness, height, materialId)
        return null
    }

    override fun textureCount(): Int = game?.textureCount() ?: 0

    /** The albedo scaled to [width] x [height], as opaque ARGB. */
    private fun pixels(id: Int, width: Int, height: Int): IntArray? = pixels(id, width, height, false)

    /** The albedo scaled to [width] x [height] as ARGB, with its own alpha when [keepAlpha], else opaque. */
    private fun pixels(id: Int, width: Int, height: Int, keepAlpha: Boolean): IntArray? {
        val source: java.awt.image.BufferedImage = image(id) ?: return null
        val scaled = java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB)
        val graphics = scaled.createGraphics()
        graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.composite = java.awt.AlphaComposite.Src
        graphics.drawImage(source, 0, 0, width, height, null)
        graphics.dispose()
        val out = IntArray(width * height)
        scaled.getRGB(0, 0, width, height, out, 0, width)
        if (!keepAlpha || !source.colorModel.hasAlpha()) {
            for (i in out.indices) out[i] = out[i] or -0x1000000
        }
        return out
    }
}
