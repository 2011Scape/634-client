/*
 * Not part of the original client: 634HD (toolkit 6). Phase 5 material maps (client.hd.materials):
 * the extra maps of the textures listed in materials.json (HdContent), keyed by 634 texture id. The
 * cache texture stays the albedo. Each map type is one GL_TEXTURE_2D_ARRAY with a layer per texture,
 * all at SIZE with mipmaps: normal (tangent space x, y; BC5) on unit 19, packed occlusion/roughness/
 * emissive mask with the height in alpha on 20 (BC7), emitted colour on 21 (BC7). The driver compresses
 * them as they are uploaded; without BC7 those two stay RGBA8, and if compression fails all do. A texture without a map of a type gets a
 * neutral layer for it (flat normal, no occlusion, mid roughness, no emission).
 *
 * The material table is a uniform block (HdMaterialMaps in hd_material.glsl, binding 1): per slot its
 * layer, specular and emissive strength, parallax depth, and which maps it has. The toolkit passes a
 * draw's slot (GlState.hdMapSlot for emulated draws, the retained scene's material table otherwise).
 *
 * The maps are read from the jar (634hd/materials/<texture>/), or next to the data folder when
 * HdContent reads one (hot reload: they are loaded again when materials.json changes).
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.graphics.hd.content.HdContent
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.lwjgl.opengl.GL33C

class HdMaterialMaps {

    companion object {
        const val BINDING: Int = 1
        const val UNIT_NORMAL: Int = 19
        const val UNIT_ORM: Int = 20
        const val UNIT_EMISSIVE: Int = 21
        const val MAX_MAPS: Int = 256
        const val SIZE: Int = 256
        private const val TABLE_BYTES: Int = MAX_MAPS * 16 * 2
        private const val COMPRESSED_RGBA_BPTC: Int = 0x8E8C
    }

    private val arrays: IntArray = IntArray(3)
    private var buffer: Int = 0
    private var loadedVersion: Int = -1

    /** Texture id to slot, -1 for none. */
    private var slots: IntArray = IntArray(0)

    /** Bumped whenever the maps are loaded again, for tables that hold slots (HdMaterials). */
    var version: Int = 0
        private set

    /** Layers loaded, and their bytes on the GPU (for the debug panel and the 512 MB budget). */
    var count: Int = 0
        private set
    var bytes: Long = 0L
        private set

    fun slotOf(texture: Int): Int = if (texture >= 0 && texture < slots.size) slots[texture] else -1

    /** Loads the maps when materials.json changed, and binds the arrays and table for the frame. */
    fun prepare(content: HdContent) {
        if (loadedVersion != content.version) {
            loadedVersion = content.version
            load(content)
        }
        if (buffer == 0) return
        GL33C.glBindBufferBase(GL33C.GL_UNIFORM_BUFFER, BINDING, buffer)
        for (i in 0 until 3) {
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT_NORMAL + i)
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, arrays[i])
            GL33C.glBindSampler(UNIT_NORMAL + i, 0)
        }
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + HdGl.state.activeTextureUnit)
    }

    /** Whether the arrays are compressed; [uncompressed] is set when the driver refused. */
    var compressed: Boolean = false
        private set
    private var uncompressed: Boolean = System.getProperty("client.hd.uncompressedMaps") != null

    /** One layer at every mip level from RGBA8 [level0] (box filter), the driver compressing it. */
    private fun upload(level0: ByteBuffer, layer: Int) {
        var data: ByteBuffer = level0
        var size: Int = SIZE
        var level: Int = 0
        while (true) {
            data.position(0)
            GL33C.glTexSubImage3D(GL33C.GL_TEXTURE_2D_ARRAY, level, 0, 0, layer, size, size, 1, GL33C.GL_RGBA, GL33C.GL_UNSIGNED_BYTE, data)
            if (size == 1) break
            val half: Int = size shr 1
            val next: ByteBuffer = ByteBuffer.allocateDirect(half * half * 4).order(ByteOrder.nativeOrder())
            for (y in 0 until half) {
                for (x in 0 until half) {
                    for (c in 0 until 4) {
                        val sum: Int = (data.get(((y * 2) * size + x * 2) * 4 + c).toInt() and 0xFF) + (data.get(((y * 2) * size + x * 2 + 1) * 4 + c).toInt() and 0xFF) +
                            (data.get(((y * 2 + 1) * size + x * 2) * 4 + c).toInt() and 0xFF) + (data.get(((y * 2 + 1) * size + x * 2 + 1) * 4 + c).toInt() and 0xFF)
                        next.put((y * half + x) * 4 + c, ((sum + 2) shr 2).toByte())
                    }
                }
            }
            data = next
            size = half
            level++
        }
    }

    private fun load(content: HdContent) {
        release()
        val materials = content.materials.values.sortedBy { it.texture }.take(MAX_MAPS)
        var top: Int = 0
        for (m in materials) top = maxOf(top, m.texture + 1)
        slots = IntArray(top) { -1 }
        count = materials.size
        version++
        if (count == 0) return
        val layerBytes: Int = SIZE * SIZE * 4
        val pixels: ByteBuffer = ByteBuffer.allocateDirect(layerBytes).order(ByteOrder.nativeOrder())
        val table: ByteBuffer = ByteBuffer.allocateDirect(TABLE_BYTES).order(ByteOrder.nativeOrder())
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT_NORMAL)
        // flat normal, no occlusion with mid roughness and no emission, black
        val defaults: IntArray = intArrayOf(0x8080FF, 0xFFB300, 0x000000)
        val alphaDefaults: IntArray = intArrayOf(0xFF, 0xFF, 0xFF)
        // compressed on upload where the driver can: normals as BC5 (x, y; z is rebuilt), the packed
        // and emissive maps as BC7 (GL 4.2 or ARB_texture_compression_bptc); else uncompressed RGBA8
        val caps = org.lwjgl.opengl.GL.getCapabilities()
        val bptc: Boolean = !uncompressed && (caps.OpenGL42 || caps.GL_ARB_texture_compression_bptc)
        val formats: IntArray = if (uncompressed) intArrayOf(GL33C.GL_RGBA8, GL33C.GL_RGBA8, GL33C.GL_RGBA8)
            else intArrayOf(GL33C.GL_COMPRESSED_RG_RGTC2, if (bptc) COMPRESSED_RGBA_BPTC else GL33C.GL_RGBA8, if (bptc) COMPRESSED_RGBA_BPTC else GL33C.GL_RGBA8)
        val levels: Int = 32 - Integer.numberOfLeadingZeros(SIZE)
        while (GL33C.glGetError() != GL33C.GL_NO_ERROR) { }
        for (type in 0 until 3) {
            arrays[type] = GL33C.glGenTextures()
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, arrays[type])
            var size: Int = SIZE
            for (level in 0 until levels) {
                GL33C.glTexImage3D(GL33C.GL_TEXTURE_2D_ARRAY, level, formats[type], size, size, count, 0, GL33C.GL_RGBA, GL33C.GL_UNSIGNED_BYTE, null as ByteBuffer?)
                size = maxOf(1, size shr 1)
            }
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_MAX_LEVEL, levels - 1)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_LINEAR_MIPMAP_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_WRAP_S, GL33C.GL_REPEAT)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_WRAP_T, GL33C.GL_REPEAT)
        }
        bytes = 0L
        val heights: ByteArray = ByteArray(SIZE * SIZE)
        for ((slot, m) in materials.withIndex()) {
            slots[m.texture] = slot
            val files: Array<String?> = arrayOf(m.normal, m.orm, m.emissive)
            val has: BooleanArray = BooleanArray(3)
            for (type in 0 until 3) {
                GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, arrays[type])
                pixels.clear()
                has[type] = files[type] != null && read(content, m.texture, files[type]!!, pixels)
                if (!has[type]) {
                    // the neutral map of this type
                    pixels.clear()
                    val rgb: Int = defaults[type]
                    for (i in 0 until SIZE * SIZE) {
                        pixels.put((rgb shr 16).toByte()).put((rgb shr 8).toByte()).put(rgb.toByte()).put(alphaDefaults[type].toByte())
                    }
                    pixels.flip()
                }
                if (type == 0) {
                    // the height (the normal map's alpha) travels in the packed map's alpha: BC5 has two channels
                    for (i in 0 until SIZE * SIZE) heights[i] = pixels.get(i * 4 + 3)
                } else if (type == 1) {
                    for (i in 0 until SIZE * SIZE) pixels.put(i * 4 + 3, heights[i])
                }
                upload(pixels, slot)
            }
            table.putFloat(slot * 16, slot.toFloat()).putFloat(slot * 16 + 4, m.specular).putFloat(slot * 16 + 8, m.emissiveStrength).putFloat(slot * 16 + 12, if (m.height != null) m.parallax else 0.0f)
            val b: Int = MAX_MAPS * 16 + slot * 16
            table.putFloat(b, if (has[0]) 1.0f else 0.0f).putFloat(b + 4, if (has[1]) 1.0f else 0.0f).putFloat(b + 8, if (has[2]) 1.0f else 0.0f).putFloat(b + 12, 0.0f)
        }
        if (!uncompressed && GL33C.glGetError() != GL33C.GL_NO_ERROR) {
            // the driver would not compress: load again uncompressed
            System.err.println("634HD: the material maps cannot be compressed on this driver; loading them uncompressed")
            uncompressed = true
            load(content)
            return
        }
        for (type in 0 until 3) {
            val perPixel: Long = if (formats[type] == GL33C.GL_RGBA8) 4L else 1L
            bytes += SIZE.toLong() * SIZE * count * perPixel * 4L / 3L
        }
        compressed = !uncompressed
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + HdGl.state.activeTextureUnit)
        buffer = GL33C.glGenBuffers()
        GL33C.glBindBuffer(GL33C.GL_UNIFORM_BUFFER, buffer)
        table.clear()
        GL33C.glBufferData(GL33C.GL_UNIFORM_BUFFER, table, GL33C.GL_STATIC_DRAW)
        GL33C.glBindBuffer(GL33C.GL_UNIFORM_BUFFER, 0)
        println("634HD: " + count + " material map sets, " + (bytes / 1048576L) + " MB" + (if (compressed) " (BC5/BC7)" else " (uncompressed)"))
    }

    /** One map's pixels at SIZE x SIZE (scaled if it is not), RGBA, into [out]; false if it is missing or unreadable. */
    private fun read(content: HdContent, texture: Int, name: String, out: ByteBuffer): Boolean {
        try {
            val folder: File? = content.folder
            val stream = if (folder != null && File(folder, "../materials/$texture/$name").isFile) {
                File(folder, "../materials/$texture/$name").inputStream()
            } else {
                HdMaterialMaps::class.java.getResourceAsStream("/634hd/materials/$texture/$name")
            }
            if (stream == null) {
                System.err.println("634HD: materials.json: no materials/$texture/$name")
                return false
            }
            var image: java.awt.image.BufferedImage = stream.use { javax.imageio.ImageIO.read(it) } ?: return false
            if (image.width != SIZE || image.height != SIZE) {
                val scaled = java.awt.image.BufferedImage(SIZE, SIZE, java.awt.image.BufferedImage.TYPE_INT_ARGB)
                val g = scaled.createGraphics()
                g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC)
                g.drawImage(image, 0, 0, SIZE, SIZE, null)
                g.dispose()
                image = scaled
            }
            val alpha: Boolean = image.colorModel.hasAlpha()
            for (y in 0 until SIZE) {
                for (x in 0 until SIZE) {
                    val argb: Int = image.getRGB(x, y)
                    out.put((argb shr 16).toByte()).put((argb shr 8).toByte()).put(argb.toByte()).put(if (alpha) (argb ushr 24).toByte() else -1)
                }
            }
            out.flip()
            return true
        } catch (ex: java.io.IOException) {
            System.err.println("634HD: materials/$texture/$name: $ex")
            return false
        }
    }

    fun release() {
        for (i in 0 until 3) {
            if (arrays[i] != 0) GL33C.glDeleteTextures(arrays[i])
            arrays[i] = 0
        }
        if (buffer != 0) GL33C.glDeleteBuffers(buffer)
        buffer = 0
        count = 0
        bytes = 0L
    }
}
