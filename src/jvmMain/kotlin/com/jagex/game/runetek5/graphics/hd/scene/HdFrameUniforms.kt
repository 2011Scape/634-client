/*
 * Not part of the original client: 634HD (toolkit 6). The per-frame uniform block every 634HD scene
 * shader reads (hd_lib.glsl, "HdFrame", std140, binding 0): which features are on (HdSettings) and
 * what they need this frame (sun, fog, sky, shadow matrix, point lights).
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.client.features.HdSettings
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.lwjgl.opengl.GL33C

class HdFrameUniforms {

    companion object {
        const val BINDING: Int = 0
        const val MAX_LIGHTS: Int = 64

        const val HD_LINEAR: Int = 1
        const val HD_PIXEL_LIGHTING: Int = 2
        const val HD_SHADOWS: Int = 4
        const val HD_LIGHTS: Int = 8
        const val HD_FOG: Int = 16
        const val HD_FADE: Int = 32
        const val HD_SKY: Int = 64
        const val HD_AO: Int = 128
        const val HD_TERRAIN: Int = 256
        const val HD_WATER: Int = 512
        const val HD_FOLIAGE: Int = 1024
        const val HD_SOFT_PARTICLES: Int = 2048
        const val HD_MIPMAPS: Int = 4096
        const val HD_MATERIALS: Int = 8192

        // std140 offsets, in bytes
        private const val FLAGS: Int = 0
        private const val TIME: Int = 16
        private const val SUN_DIRECTION: Int = 32
        private const val SUN_COLOUR: Int = 48
        private const val AMBIENT: Int = 64
        private const val FOG_COLOUR: Int = 80
        private const val FOG_PARAMS: Int = 96
        private const val SKY_TOP: Int = 112
        private const val SKY_HORIZON: Int = 128
        private const val SHADOW_MATRIX: Int = 144
        private const val VIEW_TO_WORLD: Int = 208
        private const val VIEWPORT: Int = 272
        private const val LIGHT_INFO: Int = 288
        private const val LIGHT_POSITION: Int = 304
        private const val LIGHT_COLOUR: Int = LIGHT_POSITION + MAX_LIGHTS * 16
        private const val AO_MATRIX: Int = LIGHT_COLOUR + MAX_LIGHTS * 16
        private const val AO_PARAMS: Int = AO_MATRIX + 64
        private const val WATER: Int = AO_PARAMS + 16
        private const val DEBUG: Int = WATER + 16
        const val SIZE: Int = DEBUG + 16
    }

    private var buffer: Int = 0

    private val data: ByteBuffer = ByteBuffer.allocateDirect(SIZE).order(ByteOrder.nativeOrder())

    /** The feature bits for this frame, from HdSettings. */
    var flags: Int = 0
        private set

    private var frame: Int = 0

    fun flagsFromSettings(): Int {
        var bits: Int = 0
        if (HdSettings.tonemap != 0) bits = bits or HD_LINEAR
        if (HdSettings.pixelLighting) bits = bits or HD_PIXEL_LIGHTING
        if (HdSettings.shadows != 0) bits = bits or HD_SHADOWS
        if (HdSettings.lights) bits = bits or HD_LIGHTS
        if (HdSettings.fog) bits = bits or HD_FOG
        if (HdSettings.fade) bits = bits or HD_FADE
        if (HdSettings.sky) bits = bits or HD_SKY
        if (HdSettings.ambientOcclusion) bits = bits or HD_AO
        if (HdSettings.terrainBlend) bits = bits or HD_TERRAIN
        if (HdSettings.water != 0) bits = bits or HD_WATER
        if (HdSettings.foliage) bits = bits or HD_FOLIAGE
        if (HdSettings.softParticles) bits = bits or HD_SOFT_PARTICLES
        if (HdSettings.mipmaps) bits = bits or HD_MIPMAPS
        if (HdSettings.materials) bits = bits or HD_MATERIALS
        return bits
    }

    fun putInt4(at: Int, x: Int, y: Int, z: Int, w: Int) {
        data.putInt(at, x).putInt(at + 4, y).putInt(at + 8, z).putInt(at + 12, w)
    }

    fun putVec4(at: Int, x: Float, y: Float, z: Float, w: Float) {
        data.putFloat(at, x).putFloat(at + 4, y).putFloat(at + 8, z).putFloat(at + 12, w)
    }

    fun putMat4(at: Int, m: FloatArray) {
        for (i in 0 until 16) data.putFloat(at + i * 4, m[i])
    }

    fun setTime(seconds: Float, wind: Float, fadeStart: Float, fadeLength: Float) = putVec4(TIME, seconds, wind, fadeStart, fadeLength)
    fun setSun(x: Float, y: Float, z: Float, present: Boolean) = putVec4(SUN_DIRECTION, x, y, z, if (present) 1.0f else 0.0f)
    fun setSunColour(r: Float, g: Float, b: Float) = putVec4(SUN_COLOUR, r, g, b, 1.0f)
    fun setAmbient(r: Float, g: Float, b: Float) = putVec4(AMBIENT, r, g, b, 1.0f)
    fun setFog(r: Float, g: Float, b: Float, start: Float, end: Float, heightDensity: Float, heightTop: Float) {
        putVec4(FOG_COLOUR, r, g, b, 1.0f)
        putVec4(FOG_PARAMS, start, end, heightDensity, heightTop)
    }
    fun setSky(topR: Float, topG: Float, topB: Float, horizonR: Float, horizonG: Float, horizonB: Float) {
        putVec4(SKY_TOP, topR, topG, topB, 1.0f)
        putVec4(SKY_HORIZON, horizonR, horizonG, horizonB, 1.0f)
    }
    fun setShadowMatrix(m: FloatArray) = putMat4(SHADOW_MATRIX, m)
    fun setViewToWorld(m: FloatArray) = putMat4(VIEW_TO_WORLD, m)
    fun setViewport(x: Float, y: Float, width: Float, height: Float) = putVec4(VIEWPORT, x, y, width, height)

    /** Point lights this frame: eye-space position and radius, colour times intensity. */
    fun setLight(index: Int, x: Float, y: Float, z: Float, radius: Float, r: Float, g: Float, b: Float) {
        putVec4(LIGHT_POSITION + index * 16, x, y, z, radius)
        putVec4(LIGHT_COLOUR + index * 16, r, g, b, 0.0f)
    }

    fun setLightCount(count: Int) = putInt4(LIGHT_INFO, count, 0, 0, 0)

    /** The debug panel's overlay (HdDebugPanel.OVERLAYS), 0 for none. */
    fun setDebug(overlay: Int) = putInt4(DEBUG, overlay, 0, 0, 0)

    /** Water's tint (HD_WATER): colour, w how strongly it replaces the game's water colour. */
    fun setWater(r: Float, g: Float, b: Float, strength: Float) = putVec4(WATER, r, g, b, strength)

    /** The occlusion height map (HD_AO): world to (u, v, depth), and range, strength, uv per unit. */
    fun setAmbientOcclusion(matrix: FloatArray, params: FloatArray) {
        putMat4(AO_MATRIX, matrix)
        putVec4(AO_PARAMS, params[0], params[1], params[2], params[3])
    }

    /** Writes the flags and uploads the block; every program reading it sees this frame's values. */
    fun upload(shadowSize: Int) {
        flags = flagsFromSettings()
        putInt4(FLAGS, flags, HdSettings.tonemap, shadowSize, frame++)
        if (buffer == 0) {
            buffer = GL33C.glGenBuffers()
            GL33C.glBindBuffer(GL33C.GL_UNIFORM_BUFFER, buffer)
            GL33C.glBufferData(GL33C.GL_UNIFORM_BUFFER, SIZE.toLong(), GL33C.GL_DYNAMIC_DRAW)
        } else {
            GL33C.glBindBuffer(GL33C.GL_UNIFORM_BUFFER, buffer)
        }
        data.clear()
        GL33C.glBufferSubData(GL33C.GL_UNIFORM_BUFFER, 0L, data)
        GL33C.glBindBuffer(GL33C.GL_UNIFORM_BUFFER, 0)
        GL33C.glBindBufferBase(GL33C.GL_UNIFORM_BUFFER, BINDING, buffer)
    }

    fun release() {
        if (buffer != 0) {
            GL33C.glDeleteBuffers(buffer)
            buffer = 0
        }
    }
}
