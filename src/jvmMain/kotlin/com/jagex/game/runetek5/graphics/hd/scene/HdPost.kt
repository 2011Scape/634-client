/*
 * Not part of the original client: 634HD (toolkit 6). The passes between the 3D view's framebuffer
 * (HdSceneTarget) and the frame, when a post-processing feature is on (HdSettings.postProcessing):
 *
 * - bloom: the bright parts at half size, a chain of downsamples and tent-filtered upsamples
 *   (post_bright, post_down, post_up), added back in the composite;
 * - composite: tonemapping from linear colour (ACES or AgX) to sRGB, a colour-grading look-up table,
 *   ordered dithering (post_composite);
 * - FXAA on the result (post_fxaa).
 *
 * Every pass draws a full-screen triangle with its own program, on texture units 8 and up; the GL
 * state the toolkit and the emulator rely on (framebuffers, viewport, blending, depth, scissor, the
 * emulator's program and vertex array) is put back afterwards.
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.client.features.HdSettings
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdShaders
import com.jagex.game.runetek5.graphics.hd.gl.Mat4
import com.jagex.game.runetek5.graphics.hd.gl.WebGLProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.lwjgl.opengl.GL33C

class HdPost {

    companion object {
        private const val UNIT_A: Int = 8
        private const val UNIT_B: Int = 9
        private const val UNIT_C: Int = 10
        private const val UNIT_D: Int = 11
        private const val BLOOM_LEVELS: Int = 5
        private const val LUT_SIZE: Int = 16

        private const val EXPOSURE_ACES: Float = 0.95f
        private const val EXPOSURE_AGX: Float = 1.05f

        /** The exposure the composite applies before the tonemap's curve (HdSettings.tonemap). */
        fun exposure(): Float = if (HdSettings.tonemap == 2) EXPOSURE_AGX else EXPOSURE_ACES
        private const val BLOOM_THRESHOLD: Float = 0.75f
        private const val BLOOM_STRENGTH: Float = 0.35f
        private val PROBE: Boolean = System.getProperty("client.test.hdSceneMax") != null
    }

    private class Target(val width: Int, val height: Int, val format: Int) {
        val texture: Int = GL33C.glGenTextures()
        val framebuffer: Int = GL33C.glGenFramebuffers()

        init {
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, texture)
            GL33C.glTexImage2D(GL33C.GL_TEXTURE_2D, 0, format, width, height, 0, GL33C.GL_RGBA, if (format == GL33C.GL_RGBA16F) GL33C.GL_HALF_FLOAT else GL33C.GL_UNSIGNED_BYTE, null as ByteBuffer?)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_LINEAR)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_S, GL33C.GL_CLAMP_TO_EDGE)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_T, GL33C.GL_CLAMP_TO_EDGE)
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, 0)
            GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, framebuffer)
            GL33C.glFramebufferTexture2D(GL33C.GL_DRAW_FRAMEBUFFER, GL33C.GL_COLOR_ATTACHMENT0, GL33C.GL_TEXTURE_2D, texture, 0)
        }

        fun release() {
            GL33C.glDeleteFramebuffers(framebuffer)
            GL33C.glDeleteTextures(texture)
        }
    }

    private var vao: Int = 0
    private var bright: WebGLProgram? = null
    private var down: WebGLProgram? = null
    private var up: WebGLProgram? = null
    private var composite: WebGLProgram? = null
    private var fxaa: WebGLProgram? = null
    private var sky: WebGLProgram? = null
    private var lut: Int = 0

    private val bloom: Array<Target?> = arrayOfNulls(BLOOM_LEVELS)
    private var resolved: Target? = null

    /** Colour-grading table to use (a size^3 cube, LUT_SIZE slices side by side); 0 for the neutral one. */
    var gradingTexture: Int = 0
        private set

    // the table faded from when an area's grading changes, and how far the fade is (0 to 1)
    private var gradingPrevious: Int = 0
    private var gradingMix: Float = 1.0f
    private var gradingLast: Long = 0L

    /** The grading table to draw with ([table], 0 for none): a change fades over about a second. */
    fun useGrading(table: Int) {
        val now: Long = System.nanoTime()
        if (table != gradingTexture) {
            gradingPrevious = gradingTexture
            gradingTexture = table
            gradingMix = 0.0f
        } else if (gradingMix < 1.0f) {
            gradingMix = minOf(1.0f, gradingMix + ((now - gradingLast).coerceIn(0L, 100000000L)).toFloat() / 1.0e9f)
        }
        gradingLast = now
    }

    @JvmField
    var gradingSize: Int = LUT_SIZE

    private fun program(fragment: String): WebGLProgram {
        val vertex = HdShaders.compile(GL33C.GL_VERTEX_SHADER, HdShaders.source("post.vert"), "post.vert")
        val shader = HdShaders.compile(GL33C.GL_FRAGMENT_SHADER, HdShaders.source(fragment), fragment)
        val linked = HdShaders.link(vertex, shader, fragment)
        GL33C.glDeleteShader(vertex.id)
        GL33C.glDeleteShader(shader.id)
        return linked
    }

    private fun ensure() {
        if (vao != 0) return
        vao = GL33C.glGenVertexArrays()
        bright = program("post_bright.frag")
        down = program("post_down.frag")
        up = program("post_up.frag")
        composite = program("post_composite.frag")
        fxaa = program("post_fxaa.frag")
        sky = program("sky.frag")
        // the neutral table: each colour maps to itself
        val size = LUT_SIZE
        val pixels = ByteBuffer.allocateDirect(size * size * size * 4).order(ByteOrder.nativeOrder())
        for (g in 0 until size) {
            for (b in 0 until size) {
                for (r in 0 until size) {
                    pixels.put((r * 255 / (size - 1)).toByte()).put((g * 255 / (size - 1)).toByte()).put((b * 255 / (size - 1)).toByte()).put(-1)
                }
            }
        }
        pixels.flip()
        lut = GL33C.glGenTextures()
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, lut)
        GL33C.glTexImage2D(GL33C.GL_TEXTURE_2D, 0, GL33C.GL_RGBA8, size * size, size, 0, GL33C.GL_RGBA, GL33C.GL_UNSIGNED_BYTE, pixels)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_LINEAR)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_LINEAR)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_S, GL33C.GL_CLAMP_TO_EDGE)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_T, GL33C.GL_CLAMP_TO_EDGE)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, 0)
    }

    private fun sized(target: Target?, width: Int, height: Int, format: Int): Target {
        if (target != null && target.width == width && target.height == height && target.format == format) return target
        target?.release()
        return Target(width, height, format)
    }

    private fun use(program: WebGLProgram, name: String): Int = GL33C.glGetUniformLocation(program.id, name)

    private fun bindTexture(unit: Int, texture: Int) {
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + unit)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, texture)
        // the retained scene leaves its samplers on these units (HdMaterials.bind)
        GL33C.glBindSampler(unit, 0)
    }

    /**
     * Draws the 3D view [scene] (a GL texture, [width] x [height]) into the framebuffer that is bound,
     * at ([x], [y], [viewWidth], [viewHeight]) in GL's window coordinates.
     */
    fun compose(scene: Int, width: Int, height: Int, x: Int, y: Int, viewWidth: Int, viewHeight: Int, frame: Int) {
        HdGl.flushImmediate()
        ensure()
        if (PROBE && frame % 30 == 0) probe(scene, width, height, frame)
        // what the toolkit and the emulator expect to find afterwards
        val drawFramebuffer = GL33C.glGetInteger(GL33C.GL_DRAW_FRAMEBUFFER_BINDING)
        val readFramebuffer = GL33C.glGetInteger(GL33C.GL_READ_FRAMEBUFFER_BINDING)
        val viewport = IntArray(4)
        GL33C.glGetIntegerv(GL33C.GL_VIEWPORT, viewport)
        val blend = GL33C.glIsEnabled(GL33C.GL_BLEND)
        val blendSrc = GL33C.glGetInteger(GL33C.GL_BLEND_SRC_RGB)
        val blendDst = GL33C.glGetInteger(GL33C.GL_BLEND_DST_RGB)
        val depth = GL33C.glIsEnabled(GL33C.GL_DEPTH_TEST)
        val scissor = GL33C.glIsEnabled(GL33C.GL_SCISSOR_TEST)
        val cull = GL33C.glIsEnabled(GL33C.GL_CULL_FACE)
        GL33C.glDisable(GL33C.GL_BLEND)
        GL33C.glDisable(GL33C.GL_DEPTH_TEST)
        GL33C.glDisable(GL33C.GL_SCISSOR_TEST)
        GL33C.glDisable(GL33C.GL_CULL_FACE)
        GL33C.glColorMask(true, true, true, true)
        GL33C.glBindVertexArray(vao)

        val linear = HdSettings.tonemap != 0
        var bloomTexture = 0
        if (HdSettings.bloom) {
            var w = maxOf(1, width / 2)
            var h = maxOf(1, height / 2)
            for (i in 0 until BLOOM_LEVELS) {
                bloom[i] = sized(bloom[i], w, h, GL33C.GL_RGBA16F)
                w = maxOf(1, w / 2)
                h = maxOf(1, h / 2)
            }
            // bright pass into level 0
            val brightProgram = bright!!
            GL33C.glUseProgram(brightProgram.id)
            GL33C.glUniform1i(use(brightProgram, "uSource"), UNIT_A)
            GL33C.glUniform2f(use(brightProgram, "uTexel"), 1.0f / width, 1.0f / height)
            GL33C.glUniform1f(use(brightProgram, "uThreshold"), BLOOM_THRESHOLD)
            GL33C.glUniform1i(use(brightProgram, "uLinear"), if (linear) 1 else 0)
            bindTexture(UNIT_A, scene)
            draw(bloom[0]!!)
            val downProgram = down!!
            GL33C.glUseProgram(downProgram.id)
            GL33C.glUniform1i(use(downProgram, "uSource"), UNIT_A)
            for (i in 1 until BLOOM_LEVELS) {
                val source = bloom[i - 1]!!
                GL33C.glUniform2f(use(downProgram, "uTexel"), 1.0f / source.width, 1.0f / source.height)
                bindTexture(UNIT_A, source.texture)
                draw(bloom[i]!!)
            }
            val upProgram = up!!
            GL33C.glUseProgram(upProgram.id)
            GL33C.glUniform1i(use(upProgram, "uSource"), UNIT_A)
            GL33C.glUniform1f(use(upProgram, "uRadius"), 1.0f)
            GL33C.glEnable(GL33C.GL_BLEND)
            GL33C.glBlendFunc(GL33C.GL_ONE, GL33C.GL_ONE)
            for (i in BLOOM_LEVELS - 1 downTo 1) {
                val source = bloom[i]!!
                GL33C.glUniform2f(use(upProgram, "uTexel"), 1.0f / source.width, 1.0f / source.height)
                bindTexture(UNIT_A, source.texture)
                draw(bloom[i - 1]!!)
            }
            GL33C.glDisable(GL33C.GL_BLEND)
            bloomTexture = bloom[0]!!.texture
        }

        // composite, into an intermediate target when FXAA follows, else into the frame
        val compositeProgram = composite!!
        GL33C.glUseProgram(compositeProgram.id)
        GL33C.glUniform1i(use(compositeProgram, "uScene"), UNIT_A)
        GL33C.glUniform1i(use(compositeProgram, "uBloom"), UNIT_B)
        GL33C.glUniform1i(use(compositeProgram, "uLut"), UNIT_C)
        GL33C.glUniform1i(use(compositeProgram, "uLutPrevious"), UNIT_D)
        GL33C.glUniform1f(use(compositeProgram, "uLutMix"), gradingMix)
        GL33C.glUniform1i(use(compositeProgram, "uTonemap"), HdSettings.tonemap)
        GL33C.glUniform1i(use(compositeProgram, "uBloomOn"), if (bloomTexture != 0) 1 else 0)
        GL33C.glUniform1f(use(compositeProgram, "uBloomStrength"), BLOOM_STRENGTH)
        GL33C.glUniform1i(use(compositeProgram, "uLutOn"), if (HdSettings.colourGrading) 1 else 0)
        GL33C.glUniform1f(use(compositeProgram, "uLutSize"), (if (gradingTexture != 0) gradingSize else LUT_SIZE).toFloat())
        GL33C.glUniform1i(use(compositeProgram, "uDither"), if (HdSettings.dither) 1 else 0)
        GL33C.glUniform1f(use(compositeProgram, "uExposure"), exposure())
        GL33C.glUniform1i(use(compositeProgram, "uFrame"), frame)
        bindTexture(UNIT_A, scene)
        bindTexture(UNIT_B, bloomTexture)
        bindTexture(UNIT_C, if (gradingTexture != 0) gradingTexture else lut)
        bindTexture(UNIT_D, if (gradingPrevious != 0) gradingPrevious else lut)
        if (HdSettings.fxaa) {
            val intermediate = sized(resolved, width, height, GL33C.GL_RGBA8)
            resolved = intermediate
            draw(intermediate)
            val fxaaProgram = fxaa!!
            GL33C.glUseProgram(fxaaProgram.id)
            GL33C.glUniform1i(use(fxaaProgram, "uSource"), UNIT_A)
            GL33C.glUniform2f(use(fxaaProgram, "uTexel"), 1.0f / width, 1.0f / height)
            bindTexture(UNIT_A, intermediate.texture)
        }
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, drawFramebuffer)
        // the window may be drawn at a whole-number interface scale (HdGl.windowScale)
        val n = if (drawFramebuffer == 0) HdGl.windowScale else 1
        GL33C.glViewport(x * n, y * n, viewWidth * n, viewHeight * n)
        GL33C.glDrawArrays(GL33C.GL_TRIANGLES, 0, 3)

        // put everything back
        bindTexture(UNIT_A, 0)
        bindTexture(UNIT_B, 0)
        bindTexture(UNIT_C, 0)
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, drawFramebuffer)
        GL33C.glBindFramebuffer(GL33C.GL_READ_FRAMEBUFFER, readFramebuffer)
        GL33C.glViewport(viewport[0], viewport[1], viewport[2], viewport[3])
        if (blend) GL33C.glEnable(GL33C.GL_BLEND) else GL33C.glDisable(GL33C.GL_BLEND)
        GL33C.glBlendFunc(blendSrc, blendDst)
        if (depth) GL33C.glEnable(GL33C.GL_DEPTH_TEST)
        if (scissor) GL33C.glEnable(GL33C.GL_SCISSOR_TEST)
        if (cull) GL33C.glEnable(GL33C.GL_CULL_FACE)
        val state = HdGl.state
        GL33C.glBindVertexArray(state.vertexArray)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + state.activeTextureUnit)
        state.invalidateProgramCache()
    }

    /**
     * The sky (HD_SKY) into the 3D view being drawn, where the depth buffer is still clear: called
     * once the opaque entities are down, so only the background is covered. [projection] is the
     * view's projection matrix.
     */
    fun drawSky(projection: FloatArray, srgb: Boolean = false) {
        HdGl.flushImmediate()
        ensure()
        val inverse = Mat4.invert(projection) ?: return
        val blend = GL33C.glIsEnabled(GL33C.GL_BLEND)
        val depth = GL33C.glIsEnabled(GL33C.GL_DEPTH_TEST)
        val depthFunc = GL33C.glGetInteger(GL33C.GL_DEPTH_FUNC)
        val depthMask = GL33C.glGetBoolean(GL33C.GL_DEPTH_WRITEMASK)
        val cull = GL33C.glIsEnabled(GL33C.GL_CULL_FACE)
        GL33C.glDisable(GL33C.GL_BLEND)
        GL33C.glDisable(GL33C.GL_CULL_FACE)
        GL33C.glEnable(GL33C.GL_DEPTH_TEST)
        GL33C.glDepthFunc(GL33C.GL_LEQUAL)
        GL33C.glDepthMask(false)
        GL33C.glBindVertexArray(vao)
        val program = sky!!
        GL33C.glUseProgram(program.id)
        GL33C.glUniformMatrix4fv(use(program, "uInverseProjection"), false, inverse)
        GL33C.glUniform1i(use(program, "uHdScene"), 1)
        GL33C.glUniform1i(use(program, "uSrgbOut"), if (srgb) 1 else 0)
        GL33C.glDrawArrays(GL33C.GL_TRIANGLES, 0, 3)
        if (blend) GL33C.glEnable(GL33C.GL_BLEND)
        if (!depth) GL33C.glDisable(GL33C.GL_DEPTH_TEST)
        GL33C.glDepthFunc(depthFunc)
        GL33C.glDepthMask(depthMask)
        if (cull) GL33C.glEnable(GL33C.GL_CULL_FACE)
        GL33C.glBindVertexArray(HdGl.state.vertexArray)
        HdGl.state.invalidateProgramCache()
    }

    /** Colour-grading tables by file name (634hd/luts), loaded when first used; 0 when missing. */
    private val gradingTables = HashMap<String, Int>()

    fun gradingTable(name: String): Int {
        gradingTables[name]?.let { return it }
        var texture = 0
        try {
            val stream = HdPost::class.java.getResourceAsStream("/634hd/luts/" + name)
            if (stream != null) {
                val image = stream.use { javax.imageio.ImageIO.read(it) }
                if (image != null && image.width == image.height * image.height) {
                    val pixels = ByteBuffer.allocateDirect(image.width * image.height * 4).order(ByteOrder.nativeOrder())
                    for (y in 0 until image.height) {
                        for (x in 0 until image.width) {
                            val rgb = image.getRGB(x, y)
                            pixels.put((rgb shr 16).toByte()).put((rgb shr 8).toByte()).put(rgb.toByte()).put(-1)
                        }
                    }
                    pixels.flip()
                    texture = GL33C.glGenTextures()
                    GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, texture)
                    GL33C.glTexImage2D(GL33C.GL_TEXTURE_2D, 0, GL33C.GL_RGBA8, image.width, image.height, 0, GL33C.GL_RGBA, GL33C.GL_UNSIGNED_BYTE, pixels)
                    GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_LINEAR)
                    GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_LINEAR)
                    GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_S, GL33C.GL_CLAMP_TO_EDGE)
                    GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_T, GL33C.GL_CLAMP_TO_EDGE)
                    GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, 0)
                } else {
                    System.err.println("634HD: luts/$name is not a 16x16x16 strip (256x16)")
                }
            } else {
                System.err.println("634HD: no luts/$name")
            }
        } catch (ex: java.io.IOException) {
            System.err.println("634HD: luts/$name: $ex")
        }
        gradingTables[name] = texture
        return texture
    }

    /**
     * client.test.hdSceneMax: every 30 frames, the brightest value of the half-float view and where
     * it is (window coordinates, y down), and how many pixels are over 4, printed. A real scene peaks
     * around 2 to 5; 65504 is the half-float ceiling, and anything near it blooms into a blob.
     */
    private fun probe(scene: Int, width: Int, height: Int, frame: Int) {
        val pixels = ByteBuffer.allocateDirect(width * height * 16).order(ByteOrder.nativeOrder()).asFloatBuffer()
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT_A)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, scene)
        GL33C.glGetTexImage(GL33C.GL_TEXTURE_2D, 0, GL33C.GL_RGBA, GL33C.GL_FLOAT, pixels)
        var best = -1.0f
        var bestX = 0
        var bestY = 0
        var over4 = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val i = (y * width + x) * 4
                val m = maxOf(pixels.get(i), maxOf(pixels.get(i + 1), pixels.get(i + 2)))
                if (m > 4.0f) over4++
                if (m > best) {
                    best = m
                    bestX = x
                    bestY = y
                }
            }
        }
        println("HDPROBE frame " + frame + " max " + best + " at " + bestX + "," + (height - 1 - bestY) + " over4 " + over4)
    }

    private fun draw(target: Target) {
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, target.framebuffer)
        GL33C.glViewport(0, 0, target.width, target.height)
        GL33C.glDrawArrays(GL33C.GL_TRIANGLES, 0, 3)
    }

    fun release() {
        for (i in 0 until BLOOM_LEVELS) {
            bloom[i]?.release()
            bloom[i] = null
        }
        resolved?.release()
        resolved = null
        if (vao != 0) {
            GL33C.glDeleteVertexArrays(vao)
            GL33C.glDeleteTextures(lut)
            for (p in arrayOf(bright, down, up, composite, fxaa, sky)) if (p != null) GL33C.glDeleteProgram(p.id)
            for (t in gradingTables.values) if (t != 0) GL33C.glDeleteTextures(t)
            gradingTables.clear()
            gradingTexture = 0
            vao = 0
        }
    }
}
