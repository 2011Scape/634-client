/*
 * Not part of the original client: 634HD (toolkit 6). The materials the retained scene can draw in
 * one shader, as texture-array layers and a table the shaders read (scene.vert, scene.frag).
 *
 * A layer is a copy of the toolkit's own texture for the material (HdTextureCache), every mip level
 * read back and written into the array, so the retained scene samples the very texels the emulated
 * path does. Two arrays: 64x64 for small materials, the toolkit's texture size for the rest. Each has
 * a linear and a nearest sampler; the material's filter, wrap and mipmapping are kept in its flags.
 *
 * A material is "batchable" when the plain combiner draws it (effect types 0 and 6, not HDR, not
 * blended). Water, environment maps and the rest stay on the emulated per-model path.
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.features.HdSettings
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.graphics.hd.Hd2DTexture
import com.jagex.game.runetek5.graphics.hd.HdToolkit
import com.jagex.game.runetek5.graphics.hd.gl.CoreGl
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.lwjgl.opengl.GL33C

class HdMaterials(private val toolkit: HdToolkit) {

    companion object {
        /** GL_TEXTURE_MAX_ANISOTROPY (GL 4.6, and the EXT/ARB extensions before it). */
        const val TEXTURE_MAX_ANISOTROPY: Int = 0x84FE

        /** The anisotropy to set for HdSettings.anisotropy [wanted]: 0 for none or no support, else at most the driver's limit. */
        @JvmStatic
        fun anisotropy(wanted: Int): Float {
            if (wanted <= 0) return 0.0f
            val caps = org.lwjgl.opengl.GL.getCapabilities()
            if (!caps.OpenGL46 && !caps.GL_EXT_texture_filter_anisotropic && !caps.GL_ARB_texture_filter_anisotropic) return 0.0f
            return minOf(wanted.toFloat(), GL33C.glGetFloat(0x84FF))
        }

        const val FLAG_TEXTURED: Int = 1
        const val FLAG_MIPMAP: Int = 2
        const val FLAG_LINEAR: Int = 4
        const val FLAG_REPEAT_S: Int = 8
        const val FLAG_REPEAT_T: Int = 16
        const val FLAG_UNLIT: Int = 32

        const val MAX_SLOTS: Int = 4096

        /** Texture units the scene renderer binds, clear of the emulator's 0 to 7. */
        const val UNIT_SMALL_LINEAR: Int = 8
        const val UNIT_SMALL_NEAREST: Int = 9
        const val UNIT_LARGE_LINEAR: Int = 10
        const val UNIT_LARGE_NEAREST: Int = 11
        const val UNIT_MATERIALS: Int = 12

        private const val ARRAY_BUDGET_BYTES: Long = 96L * 1024L * 1024L
    }

    /** Material id + 1 to slot, 0 when not looked at yet, -1 when not batchable (or not loaded yet). */
    private var slotOfMaterial: IntArray = IntArray(0)

    private var slotCount: Int = 1

    /** Per slot: material id, for the per-frame UV scroll. */
    private val slotMaterial: IntArray = IntArray(MAX_SLOTS)

    private val table: FloatArray = FloatArray(MAX_SLOTS * 8)

    private var tableDirty: Boolean = true

    /** Slots whose texture scrolls, which the table rewrites every frame. */
    private val animated: IntArray = IntArray(MAX_SLOTS)

    private var animatedCount: Int = 0

    private val small: TextureArray = TextureArray(64)

    private val large: TextureArray = TextureArray(toolkit.size)

    private var tableBuffer: Int = 0

    private var tableTexture: Int = 0

    private var linearSampler: Int = 0

    private var nearestSampler: Int = 0

    private var scratch: ByteBuffer = ByteBuffer.allocateDirect(4 * 1024 * 1024).order(ByteOrder.nativeOrder())

    init {
        // slot 0: no texture, the vertex colour alone (material -1)
        slotMaterial[0] = -1
    }

    private class TextureArray(val size: Int) {
        var texture: Int = 0
        var layers: Int = 0
        var capacity: Int = 0
        var levels: Int = 1
    }

    /**
     * The slot that draws [materialId] in the shared shader, or -1 if the material needs the emulated
     * path (or its texture is not loaded yet, in which case it is looked at again next time).
     */
    fun slotFor(materialId: Int): Int {
        if (materialId < 0) {
            return 0
        }
        if (materialId + 1 >= slotOfMaterial.size) {
            slotOfMaterial = slotOfMaterial.copyOf(maxOf(materialId + 64, slotOfMaterial.size * 2))
        }
        val known: Int = slotOfMaterial[materialId + 1]
        if (known != 0) {
            return known
        }
        val slot: Int = createSlot(materialId)
        if (slot != -2) {
            slotOfMaterial[materialId + 1] = slot
        }
        return if (slot == -2) -1 else slot
    }

    /** -2: try again later (texture not loaded); -1: never batchable. */
    private fun createSlot(materialId: Int): Int {
        if (System.getProperty("client.test.hdNoTextures") != null) return -1
        val metrics: MaterialType = toolkit.materialProvider!!.getMetrics(materialId) ?: return -1
        val effect: Int = metrics.effectType.toInt()
        if (effect != 0 && effect != 6) {
            return -1
        }
        if (metrics.hdr && toolkit.bloom()) {
            return -1
        }
        if (metrics.effectCombiner.toInt() == 2) {
            return -1
        }
        if (metrics.effectCombiner.toInt() == 1 && ClientFeatures.AA_PIPELINE) {
            // alpha-to-coverage cut-outs are the emulated path's (setSceneAlphaToCoverage)
            return -1
        }
        if (slotCount >= MAX_SLOTS) {
            return -1
        }
        val texture: Hd2DTexture = toolkit.textureCache.getTexture(materialId) ?: return -2
        val array: TextureArray = if (metrics.isSmall) small else large
        if (texture.width != array.size || texture.length != array.size) {
            return -1
        }
        val layer: Int = copyLayer(texture, array)
        if (layer < 0) {
            return -1
        }
        val name: Int = CoreGl.textureName(texture.getId())
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + 15)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, name)
        val minFilter: Int = GL33C.glGetTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MIN_FILTER)
        val wrapS: Int = GL33C.glGetTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_S)
        val wrapT: Int = GL33C.glGetTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_T)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + com.jagex.game.runetek5.graphics.hd.gl.HdGl.state.activeTextureUnit)
        var flags: Int = FLAG_TEXTURED
        if (minFilter == GL33C.GL_LINEAR_MIPMAP_LINEAR || minFilter == GL33C.GL_NEAREST_MIPMAP_NEAREST || minFilter == GL33C.GL_LINEAR_MIPMAP_NEAREST || minFilter == GL33C.GL_NEAREST_MIPMAP_LINEAR) {
            flags = flags or FLAG_MIPMAP
        }
        if (minFilter == GL33C.GL_LINEAR || minFilter == GL33C.GL_LINEAR_MIPMAP_LINEAR || minFilter == GL33C.GL_LINEAR_MIPMAP_NEAREST) {
            flags = flags or FLAG_LINEAR
        }
        if (wrapS == GL33C.GL_REPEAT) {
            flags = flags or FLAG_REPEAT_S
        }
        if (wrapT == GL33C.GL_REPEAT) {
            flags = flags or FLAG_REPEAT_T
        }
        if (effect == 6) {
            flags = flags or FLAG_UNLIT
        }
        val slot: Int = slotCount++
        slotMaterial[slot] = materialId
        val at: Int = slot * 8
        table[at] = layer.toFloat()
        table[at + 1] = if (array === large) 1.0f else 0.0f
        table[at + 2] = metrics.combineMode.toFloat()
        table[at + 3] = flags.toFloat()
        table[at + 6] = array.size.toFloat()
        // the texture's 634HD material maps: their slot plus one, 0 for none (HdMaterialMaps)
        table[at + 7] = (toolkit.materialMaps.slotOf(materialId) + 1).toFloat()
        if (metrics.speedU.toInt() != 0 || metrics.speedV.toInt() != 0) {
            animated[animatedCount++] = slot
        }
        tableDirty = true
        if (System.getProperty("client.test.hdDebug") != null) {
            println("HDDEBUG material " + materialId + " slot " + slot + " layer " + layer + " large " + (array === large) + " size " + array.size + " op " + metrics.combineMode + " flags " + flags + " effect " + effect + " combiner " + metrics.effectCombiner)
        }
        return slot
    }

    /** Copies every level of [texture] into the next layer of [array]; -1 when the array is full. */
    /** The next mip level of an RGBA8 [source] [size] texels square: each texel the mean of four. */
    private fun halve(source: ByteBuffer, size: Int): ByteBuffer {
        val half: Int = maxOf(1, size shr 1)
        val out: ByteBuffer = ByteBuffer.allocateDirect(half * half * 4).order(ByteOrder.nativeOrder())
        for (y in 0 until half) {
            for (x in 0 until half) {
                for (c in 0 until 4) {
                    val x0: Int = minOf(size - 1, x * 2)
                    val x1: Int = minOf(size - 1, x * 2 + 1)
                    val y0: Int = minOf(size - 1, y * 2)
                    val y1: Int = minOf(size - 1, y * 2 + 1)
                    val sum: Int = (source.get((y0 * size + x0) * 4 + c).toInt() and 0xFF) + (source.get((y0 * size + x1) * 4 + c).toInt() and 0xFF) +
                        (source.get((y1 * size + x0) * 4 + c).toInt() and 0xFF) + (source.get((y1 * size + x1) * 4 + c).toInt() and 0xFF)
                    out.put((y * half + x) * 4 + c, ((sum + 2) shr 2).toByte())
                }
            }
        }
        return out
    }

    /** The anisotropy the samplers have (HdSettings.anisotropy, within what the driver allows). */
    private var samplerAnisotropy: Int = -1

    /** Puts HdSettings.anisotropy on the array samplers, when it changed. */
    private fun applyAnisotropy() {
        val wanted: Int = HdSettings.anisotropy
        if (wanted == samplerAnisotropy || linearSampler == 0) return
        samplerAnisotropy = wanted
        val level: Float = HdMaterials.anisotropy(wanted)
        if (level > 0.0f) {
            GL33C.glSamplerParameterf(linearSampler, HdMaterials.TEXTURE_MAX_ANISOTROPY, level)
            GL33C.glSamplerParameterf(nearestSampler, HdMaterials.TEXTURE_MAX_ANISOTROPY, level)
        }
    }

    private fun copyLayer(texture: Hd2DTexture, array: TextureArray): Int {
        if (array.texture == 0) {
            val levels: Int = 32 - Integer.numberOfLeadingZeros(array.size)
            val layerBytes: Long = array.size.toLong() * array.size * 4L * 4L / 3L
            array.capacity = minOf(1024L, ARRAY_BUDGET_BYTES / layerBytes).toInt()
            array.levels = levels
            array.texture = GL33C.glGenTextures()
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + 15)
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, array.texture)
            var size: Int = array.size
            for (level in 0 until levels) {
                GL33C.glTexImage3D(GL33C.GL_TEXTURE_2D_ARRAY, level, GL33C.GL_RGBA8, size, size, array.capacity, 0, GL33C.GL_RGBA, GL33C.GL_UNSIGNED_BYTE, null as ByteBuffer?)
                size = maxOf(1, size shr 1)
            }
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D_ARRAY, GL33C.GL_TEXTURE_MAX_LEVEL, levels - 1)
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, 0)
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + com.jagex.game.runetek5.graphics.hd.gl.HdGl.state.activeTextureUnit)
        }
        if (array.layers >= array.capacity) {
            return -1
        }
        val layer: Int = array.layers++
        val name: Int = CoreGl.textureName(texture.getId())
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + 15)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, name)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, array.texture)
        val packAlignment: Int = GL33C.glGetInteger(GL33C.GL_PACK_ALIGNMENT)
        val unpackAlignment: Int = GL33C.glGetInteger(GL33C.GL_UNPACK_ALIGNMENT)
        val unpackRowLength: Int = GL33C.glGetInteger(GL33C.GL_UNPACK_ROW_LENGTH)
        GL33C.glPixelStorei(GL33C.GL_PACK_ALIGNMENT, 4)
        GL33C.glPixelStorei(GL33C.GL_UNPACK_ALIGNMENT, 4)
        GL33C.glPixelStorei(GL33C.GL_UNPACK_ROW_LENGTH, 0)
        var size: Int = array.size
        var own: Boolean = true
        for (level in 0 until array.levels) {
            val bytes: Int = size * size * 4
            if (own && GL33C.glGetTexLevelParameteri(GL33C.GL_TEXTURE_2D, level, GL33C.GL_TEXTURE_WIDTH) != size) {
                // a texture without mipmaps has level 0 only; the shader samples level 0 for it unless
                // 634HD's mipmaps are on, for which the rest of the chain is made here (box filter)
                own = false
            }
            if (own) {
                if (scratch.capacity() < bytes) {
                    scratch = ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder())
                }
                scratch.clear()
                GL33C.glGetTexImage(GL33C.GL_TEXTURE_2D, level, GL33C.GL_RGBA, GL33C.GL_UNSIGNED_BYTE, scratch)
            } else {
                scratch = halve(scratch, size * 2)
            }
            GL33C.glTexSubImage3D(GL33C.GL_TEXTURE_2D_ARRAY, level, 0, 0, layer, size, size, 1, GL33C.GL_RGBA, GL33C.GL_UNSIGNED_BYTE, scratch)
            size = maxOf(1, size shr 1)
        }
        GL33C.glPixelStorei(GL33C.GL_PACK_ALIGNMENT, packAlignment)
        GL33C.glPixelStorei(GL33C.GL_UNPACK_ALIGNMENT, unpackAlignment)
        GL33C.glPixelStorei(GL33C.GL_UNPACK_ROW_LENGTH, unpackRowLength)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, 0)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + com.jagex.game.runetek5.graphics.hd.gl.HdGl.state.activeTextureUnit)
        return layer
    }

    /** Before a draw: the scroll offsets of animated materials (as renderTexture sets the texture matrix), and the table. */
    /** The material maps' version the table's slots were taken from. */
    private var mapsVersion: Int = -1

    fun update(tickMillis: Long) {
        if (mapsVersion != toolkit.materialMaps.version) {
            mapsVersion = toolkit.materialMaps.version
            for (slot in 0 until slotCount) {
                table[slot * 8 + 7] = (toolkit.materialMaps.slotOf(slotMaterial[slot]) + 1).toFloat()
            }
            tableDirty = true
        }
        if (tableBuffer == 0) {
            tableBuffer = GL33C.glGenBuffers()
            GL33C.glBindBuffer(GL33C.GL_TEXTURE_BUFFER, tableBuffer)
            GL33C.glBufferData(GL33C.GL_TEXTURE_BUFFER, (MAX_SLOTS * 8 * 4).toLong(), GL33C.GL_DYNAMIC_DRAW)
            tableTexture = GL33C.glGenTextures()
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT_MATERIALS)
            GL33C.glBindTexture(GL33C.GL_TEXTURE_BUFFER, tableTexture)
            GL33C.glTexBuffer(GL33C.GL_TEXTURE_BUFFER, GL33C.GL_RGBA32F, tableBuffer)
            linearSampler = GL33C.glGenSamplers()
            GL33C.glSamplerParameteri(linearSampler, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_LINEAR_MIPMAP_LINEAR)
            GL33C.glSamplerParameteri(linearSampler, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_LINEAR)
            nearestSampler = GL33C.glGenSamplers()
            GL33C.glSamplerParameteri(nearestSampler, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_NEAREST_MIPMAP_NEAREST)
            GL33C.glSamplerParameteri(nearestSampler, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_NEAREST)
        }
        for (i in 0 until animatedCount) {
            val slot: Int = animated[i]
            val metrics: MaterialType = toolkit.materialProvider!!.getMetrics(slotMaterial[slot]) ?: continue
            // HdToolkit.renderTexture: the period is 64 or 128 texels' worth, whatever the texture size
            val textureSize: Int = if (metrics.isSmall) 64 else 128
            val period: Int = textureSize * 50
            val v: Float = (tickMillis % period * metrics.speedV).toFloat() / period.toFloat()
            val u: Float = (metrics.speedU * (tickMillis % period)).toFloat() / period.toFloat()
            table[slot * 8 + 4] = u
            table[slot * 8 + 5] = v
            tableDirty = true
        }
        if (tableDirty) {
            GL33C.glBindBuffer(GL33C.GL_TEXTURE_BUFFER, tableBuffer)
            GL33C.glBufferSubData(GL33C.GL_TEXTURE_BUFFER, 0L, table.copyOf(slotCount * 8))
            GL33C.glBindBuffer(GL33C.GL_TEXTURE_BUFFER, 0)
            tableDirty = false
        }
    }

    /** Binds the arrays, samplers and table to their units (UNIT_*). */
    fun bind() {
        applyAnisotropy()
        bindArray(UNIT_SMALL_LINEAR, small.texture, linearSampler)
        bindArray(UNIT_SMALL_NEAREST, small.texture, nearestSampler)
        bindArray(UNIT_LARGE_LINEAR, large.texture, linearSampler)
        bindArray(UNIT_LARGE_NEAREST, large.texture, nearestSampler)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT_MATERIALS)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_BUFFER, tableTexture)
    }

    private fun bindArray(unit: Int, texture: Int, sampler: Int) {
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + unit)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D_ARRAY, texture)
        GL33C.glBindSampler(unit, sampler)
    }

    fun release() {
        if (small.texture != 0) GL33C.glDeleteTextures(small.texture)
        if (large.texture != 0) GL33C.glDeleteTextures(large.texture)
        if (tableTexture != 0) GL33C.glDeleteTextures(tableTexture)
        if (tableBuffer != 0) GL33C.glDeleteBuffers(tableBuffer)
        if (linearSampler != 0) GL33C.glDeleteSamplers(linearSampler)
        if (nearestSampler != 0) GL33C.glDeleteSamplers(nearestSampler)
        small.texture = 0
        large.texture = 0
        tableTexture = 0
        tableBuffer = 0
    }
}
