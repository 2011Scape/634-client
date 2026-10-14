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
        if (materialId < CUSTOM_MATERIALS) return game?.getPixels(readReversed, materialId, brightness, width, height)
        return pixels(materialId, width, height)
    }

    override fun getPixelsArgb(height: Int, brightness: Float, materialId: Int, width: Int): IntArray? {
        if (materialId < CUSTOM_MATERIALS) return game?.getPixelsArgb(height, brightness, materialId, width)
        return pixels(materialId, width, height)
    }

    override fun floatArgbOutput(width: Int, brightness: Float, height: Int, materialId: Int): FloatArray? {
        if (materialId < CUSTOM_MATERIALS) return game?.floatArgbOutput(width, brightness, height, materialId)
        return null
    }

    override fun textureCount(): Int = game?.textureCount() ?: 0

    /** The albedo scaled to [width] x [height], as opaque ARGB. */
    private fun pixels(id: Int, width: Int, height: Int): IntArray? {
        val source: java.awt.image.BufferedImage = image(id) ?: return null
        val scaled = java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB)
        val graphics = scaled.createGraphics()
        graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.drawImage(source, 0, 0, width, height, null)
        graphics.dispose()
        val out = IntArray(width * height)
        scaled.getRGB(0, 0, width, height, out, 0, width)
        for (i in out.indices) out[i] = out[i] or -0x1000000
        return out
    }
}
