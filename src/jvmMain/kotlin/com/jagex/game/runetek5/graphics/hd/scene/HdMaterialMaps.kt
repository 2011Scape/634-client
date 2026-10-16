/*
 * Not part of the original client: 634HD (toolkit 6). Phase 5 material maps (client.hd.materials):
 * the extra maps of the textures listed in materials.json (HdContent), keyed by 634 texture id. The
 * cache texture stays the albedo. Each map type is one GL_TEXTURE_2D_ARRAY with a layer per texture,
 * all at SIZE with mipmaps: normal (tangent space x, y; BC5) on unit 19, packed occlusion/roughness/
 * emissive mask with the height in alpha on 20 (BC7), emitted colour on 21 (BC7). Each layer goes up
 * as the precompressed blocks of tools/compress_material_maps.py, the only form of the maps the jar
 * carries (jvmProcessResources leaves the PNGs out, but albedo.png). Without BC7 the two BC7 arrays are
 * RGBA8, and if the driver refuses to compress (or with client.hd.uncompressedMaps) all three are: the
 * blocks are then decoded here. A set still at its PNGs (made but not compressed yet, in the data
 * folder) is read from them and compressed by the driver. A texture without a map of a type gets a
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
import com.jagex.game.runetek5.graphics.hd.content.HdMaterialProvider
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
        // 448 sets: the two vec4 arrays of the HdMaterialMaps block stay under 16 KB, GL's least limit
        const val MAX_MAPS: Int = 448
        const val SIZE: Int = 256
        private const val TABLE_BYTES: Int = MAX_MAPS * 16 * 2
        private const val COMPRESSED_RGBA_BPTC: Int = 0x8E8C
        private val BC7_WEIGHTS: IntArray = intArrayOf(0, 4, 9, 13, 17, 21, 26, 30, 34, 38, 43, 47, 51, 55, 60, 64)
    }

    private val arrays: IntArray = IntArray(3)
    private var buffer: Int = 0
    private var loadedVersion: Int = -1

    /** Whether the loaded maps are the fresh textures' (HdSettings.textureReplacements). */
    private var loadedFresh: Boolean = false

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
        val fresh: Boolean = com.jagex.game.runetek5.client.features.HdSettings.textureReplacements
        if (loadedVersion != content.version || loadedFresh != fresh) {
            loadedVersion = content.version
            loadedFresh = fresh
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

    /** Layers uploaded from precompressed files at the last load (the rest compressed by the driver). */
    var precompressedLayers: Int = 0
        private set

    /** Every mip level of one layer from [data], blocks as tools/compress_material_maps.py writes them. */
    private fun uploadBlocks(data: ByteArray, layer: Int, format: Int): Boolean {
        var size: Int = SIZE
        var at: Int = 0
        val levels: Int = 32 - Integer.numberOfLeadingZeros(SIZE)
        val buffer: ByteBuffer = ByteBuffer.allocateDirect(data.size).order(ByteOrder.nativeOrder())
        buffer.put(data).flip()
        for (level in 0 until levels) {
            val blocksAcross: Int = maxOf(1, (size + 3) / 4)
            val bytes: Int = blocksAcross * blocksAcross * 16
            if (at + bytes > data.size) return false
            buffer.limit(at + bytes).position(at)
            GL33C.glCompressedTexSubImage3D(GL33C.GL_TEXTURE_2D_ARRAY, level, 0, 0, layer, size, size, 1, format, buffer)
            at += bytes
            size = maxOf(1, size shr 1)
        }
        return true
    }

    private fun readBytes(content: HdContent, texture: Int, name: String): ByteArray? = content.materialFile(texture, name)

    /** client.test.hdDebug: how far the GPU's decoding of [texture]'s layer ([slot]) is from the PNGs it was made from. */
    private fun checkDecode(content: HdContent, texture: Int, slot: Int, files: Array<String?>) {
        val png: ByteBuffer = ByteBuffer.allocateDirect(SIZE * SIZE * 4).order(ByteOrder.nativeOrder())
        val decoded: ByteBuffer = ByteBuffer.allocateDirect(SIZE * SIZE * 4 * count).order(ByteOrder.nativeOrder())
        for (type in 0 until 2) {
            val name: String = files[type] ?: continue
            png.clear()
            if (!read(content, texture, name, png, true)) continue
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, arrays[type])
            decoded.clear()
            GL33C.glGetTexImage(GL33C.GL_TEXTURE_2D_ARRAY, 0, GL33C.GL_RGBA, GL33C.GL_UNSIGNED_BYTE, decoded)
            var error: Long = 0L
            val channels: Int = if (type == 0) 2 else 3
            val base: Int = slot * SIZE * SIZE * 4
            for (i in 0 until SIZE * SIZE) {
                for (c in 0 until channels) {
                    error += Math.abs((png.get(i * 4 + c).toInt() and 0xFF) - (decoded.get(base + i * 4 + c).toInt() and 0xFF))
                }
            }
            println("HDDEBUG material " + texture + " " + name + " decoded vs png: mean error " + String.format(java.util.Locale.ROOT, "%.2f", error.toDouble() / (SIZE * SIZE * channels)) + " of 255")
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
        // the emitted-colour maps (lava) have their own, small array: a layer per material that has one
        var emissiveCount: Int = 0
        for (m in materials) if (m.emissive != null) emissiveCount++
        val layersOf: IntArray = intArrayOf(count, count, maxOf(1, emissiveCount))
        var nextEmissive: Int = 0
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
                GL33C.glTexImage3D(GL33C.GL_TEXTURE_2D_ARRAY, level, formats[type], size, size, layersOf[type], 0, GL33C.GL_RGBA, GL33C.GL_UNSIGNED_BYTE, null as ByteBuffer?)
                size = maxOf(1, size shr 1)
            }
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_MAX_LEVEL, levels - 1)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_LINEAR_MIPMAP_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_WRAP_S, GL33C.GL_REPEAT)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_WRAP_T, GL33C.GL_REPEAT)
        }
        bytes = 0L
        precompressedLayers = 0
        val heights: ByteArray = ByteArray(SIZE * SIZE)
        for ((slot, m) in materials.withIndex()) {
            slots[m.texture] = slot
            val files: Array<String?> = arrayOf(m.normal, m.orm, m.emissive)
            if (loadedFresh && m.albedo != null && m.texture < HdMaterialProvider.CUSTOM_MATERIALS) {
                // a fresh texture's maps were made from its new art (tools/make_material_maps.py, fresh)
                for (type in 0 until 3) {
                    if (files[type] != null && content.materialFile(m.texture, "fresh/" + files[type]) != null) files[type] = "fresh/" + files[type]
                }
            }
            val has: BooleanArray = BooleanArray(3)
            val emissiveLayer: Int = if (files[2] != null) nextEmissive++ else -1
            for (type in 0 until 3) {
                // no emitted colour: nothing to upload (the shader does not read the emissive array)
                if (type == 2 && emissiveLayer < 0) continue
                val layer: Int = if (type == 2) emissiveLayer else slot
                GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, arrays[type])
                pixels.clear()
                // precompressed blocks (tools/compress_material_maps.py) go up as they are
                val blocksFile: String? = if (files[type] == null) null else files[type]!!.substringBeforeLast('.') + (if (type == 0) ".bc5" else ".bc7")
                val precompressed: ByteArray? = if (blocksFile != null && formats[type] != GL33C.GL_RGBA8) readBytes(content, m.texture, blocksFile) else null
                if (precompressed != null && uploadBlocks(precompressed, layer, formats[type])) {
                    has[type] = true
                    if (type == 0 && files[1] != null && readBytes(content, m.texture, files[1]!!.substringBeforeLast('.') + ".bc7") == null) {
                        // the height travels in the packed map's alpha; only its PNG path needs it from here
                        read(content, m.texture, files[type]!!, pixels, true)
                        for (i in 0 until SIZE * SIZE) heights[i] = pixels.get(i * 4 + 3)
                    }
                    precompressedLayers++
                    continue
                }
                // an RGBA8 array: the blocks decoded here (the jar has no other form of the map), else the
                // PNG of a set not compressed yet
                val blocks: ByteArray? = if (blocksFile != null && formats[type] == GL33C.GL_RGBA8) readBytes(content, m.texture, blocksFile) else null
                has[type] = blocks != null && (if (type == 0) decodeBc5(blocks, pixels) else decodeBc7(blocks, pixels))
                // a missing PNG is only worth reporting when the set has no blocks in any form either:
                // the jar carries no PNG, and a set with fresh/ maps only has no root maps by design
                val quiet: Boolean = precompressed != null || blocks != null || (blocksFile != null && content.materialFile(m.texture, "fresh/" + blocksFile) != null)
                val fromPng: Boolean = !has[type] && files[type] != null && read(content, m.texture, files[type]!!, pixels, quiet)
                if (fromPng) has[type] = true
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
                    // the height (the normal map's alpha) travels in the packed map's alpha: BC5 has two
                    // channels (decoded blocks carry none: a set at PNGs keeps its height, else it is flat)
                    for (i in 0 until SIZE * SIZE) heights[i] = if (fromPng) pixels.get(i * 4 + 3) else -1
                } else if (type == 1 && fromPng) {
                    for (i in 0 until SIZE * SIZE) pixels.put(i * 4 + 3, heights[i])
                }
                upload(pixels, layer)
            }
            table.putFloat(slot * 16, slot.toFloat()).putFloat(slot * 16 + 4, m.specular).putFloat(slot * 16 + 8, m.emissiveStrength).putFloat(slot * 16 + 12, if (m.height != null) m.parallax else 0.0f)
            val b: Int = MAX_MAPS * 16 + slot * 16
            table.putFloat(b, if (has[0]) 1.0f else 0.0f).putFloat(b + 4, if (has[1]) 1.0f else 0.0f).putFloat(b + 8, if (has[2]) 1.0f else 0.0f).putFloat(b + 12, maxOf(0, emissiveLayer).toFloat())
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
            bytes += SIZE.toLong() * SIZE * layersOf[type] * perPixel * 4L / 3L
        }
        compressed = !uncompressed
        if (System.getProperty("client.test.hdDebug") != null) {
            // the first set whose PNGs are at hand to compare against (one with fresh/ maps only has none)
            for (m in materials) {
                if (m.normal == null || content.materialFile(m.texture, m.normal!!) == null) continue
                checkDecode(content, m.texture, slots[m.texture], arrayOf(m.normal, m.orm, m.emissive))
                break
            }
        }
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + HdGl.state.activeTextureUnit)
        buffer = GL33C.glGenBuffers()
        GL33C.glBindBuffer(GL33C.GL_UNIFORM_BUFFER, buffer)
        table.clear()
        GL33C.glBufferData(GL33C.GL_UNIFORM_BUFFER, table, GL33C.GL_STATIC_DRAW)
        GL33C.glBindBuffer(GL33C.GL_UNIFORM_BUFFER, 0)
        println("634HD: " + count + " material map sets, " + (bytes / 1048576L) + " MB" + (if (compressed) " (BC5/BC7, " + precompressedLayers + " layers precompressed)" else " (uncompressed)"))
    }

    /**
     * One map's pixels at SIZE x SIZE (scaled if it is not), RGBA, into [out]; false if it is missing or
     * unreadable (a missing one is reported unless [missingOk]: the jar carries no PNG).
     */
    private fun read(content: HdContent, texture: Int, name: String, out: ByteBuffer, missingOk: Boolean = false): Boolean {
        try {
            val folder: File? = content.folder
            val stream = if (folder != null && File(folder, "../materials/$texture/$name").isFile) {
                File(folder, "../materials/$texture/$name").inputStream()
            } else {
                HdMaterialMaps::class.java.getResourceAsStream("/634hd/materials/$texture/$name")
            }
            if (stream == null) {
                if (!missingOk) System.err.println("634HD: materials.json: no materials/$texture/$name")
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

    /**
     * Level 0 of BC5 blocks (as tools/compress_material_maps.py writes them) to RGBA in [out]: the two
     * channels, then 255 (the shader rebuilds z; the height is not in this map). False if [data] is short.
     */
    private fun decodeBc5(data: ByteArray, out: ByteBuffer): Boolean {
        val across: Int = SIZE / 4
        if (data.size < across * across * 16) return false
        out.clear()
        val palette: IntArray = IntArray(8)
        for (by in 0 until across) {
            for (bx in 0 until across) {
                val at: Int = (by * across + bx) * 16
                for (c in 0 until 2) {
                    // one BC4 block per channel: two end values, then sixteen 3-bit indices
                    val base: Int = at + c * 8
                    val r0: Int = data[base].toInt() and 0xFF
                    val r1: Int = data[base + 1].toInt() and 0xFF
                    palette[0] = r0
                    palette[1] = r1
                    if (r0 > r1) {
                        for (k in 2 until 8) palette[k] = ((8 - k) * r0 + (k - 1) * r1 + 3) / 7
                    } else {
                        for (k in 2 until 6) palette[k] = ((6 - k) * r0 + (k - 1) * r1 + 2) / 5
                        palette[6] = 0
                        palette[7] = 255
                    }
                    var bits: Long = 0L
                    for (k in 0 until 6) bits = bits or ((data[base + 2 + k].toLong() and 0xFF) shl (8 * k))
                    for (i in 0 until 16) {
                        val p: Int = ((by * 4 + (i shr 2)) * SIZE + bx * 4 + (i and 3)) * 4
                        out.put(p + c, palette[(bits ushr (3 * i)).toInt() and 7].toByte())
                    }
                }
                for (i in 0 until 16) {
                    val p: Int = ((by * 4 + (i shr 2)) * SIZE + bx * 4 + (i and 3)) * 4
                    out.put(p + 2, -1)
                    out.put(p + 3, -1)
                }
            }
        }
        return true
    }

    /**
     * Level 0 of BC7 blocks to RGBA in [out]: mode 6 only (one subset, 7-bit RGBA end points with a
     * p-bit each, 4-bit indices), the one mode tools/compress_material_maps.py writes. False on any
     * other mode or if [data] is short.
     */
    private fun decodeBc7(data: ByteArray, out: ByteBuffer): Boolean {
        val across: Int = SIZE / 4
        if (data.size < across * across * 16) return false
        out.clear()
        val e0: IntArray = IntArray(4)
        val e1: IntArray = IntArray(4)
        for (by in 0 until across) {
            for (bx in 0 until across) {
                val at: Int = (by * across + bx) * 16
                var lo: Long = 0L
                var hi: Long = 0L
                for (k in 0 until 8) {
                    lo = lo or ((data[at + k].toLong() and 0xFF) shl (8 * k))
                    hi = hi or ((data[at + 8 + k].toLong() and 0xFF) shl (8 * k))
                }
                if ((lo and 0x7FL) != 0x40L) return false
                // bits 7..62: R0 R1 G0 G1 B0 B1 A0 A1, 7 bits each; bits 63 and 64: the p-bits
                val p0: Int = (lo ushr 63).toInt() and 1
                val p1: Int = hi.toInt() and 1
                for (c in 0 until 4) {
                    e0[c] = (((lo ushr (7 + c * 14)).toInt() and 0x7F) shl 1) or p0
                    e1[c] = (((lo ushr (14 + c * 14)).toInt() and 0x7F) shl 1) or p1
                }
                // bits 65..127: the indices, the first 3 bits wide (its top bit implied 0), the rest 4
                var pos: Int = 1
                for (i in 0 until 16) {
                    val width: Int = if (i == 0) 3 else 4
                    val w: Int = BC7_WEIGHTS[(hi ushr pos).toInt() and ((1 shl width) - 1)]
                    pos += width
                    val p: Int = ((by * 4 + (i shr 2)) * SIZE + bx * 4 + (i and 3)) * 4
                    for (c in 0 until 4) out.put(p + c, (((64 - w) * e0[c] + w * e1[c] + 32) shr 6).toByte())
                }
            }
        }
        return true
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
