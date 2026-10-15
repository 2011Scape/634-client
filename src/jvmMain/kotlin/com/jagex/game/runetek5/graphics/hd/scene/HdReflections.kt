/*
 * Not part of the original client: 634HD (toolkit 6). The water's planar reflection
 * (HdSettings.reflections): once a frame, before the ground and the water are drawn, the retained
 * static scene and the sky are drawn again, mirrored about the water plane, into a half-size texture
 * (unit UNIT). The water draws (ff.frag) sample it at their own screen position, bent a little by
 * the game's wave texture, and mix it in by the angle of view (strongest at grazing angles). The
 * mirror is applied in world space (uWorldToClip = projection * view * mirror), so the lights, which
 * the emulator keeps in eye space, stay as they are; the sky takes the mirrored projection. Dynamic
 * entities (players, NPCs) and the ground itself are not reflected.
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.client.features.HdSettings
import com.jagex.game.runetek5.graphics.hd.HdGround
import com.jagex.game.runetek5.graphics.hd.HdToolkit
import com.jagex.game.runetek5.graphics.hd.gl.GlState
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdShaders
import com.jagex.game.runetek5.graphics.hd.gl.Mat4
import com.jagex.game.runetek5.graphics.hd.gl.WebGLProgram
import com.jagex.game.runetek5.graphics.hd.gl.CoreGl
import com.jagex.game.runetek5.scene.Scene
import org.lwjgl.opengl.GL33C
import java.nio.ByteBuffer

class HdReflections {

    companion object {
        /** The reflection texture's unit, for the water draws. */
        const val UNIT: Int = 26

        /** The unit drawRegion binds the objects' matrices on while the reflection is drawn. */
        private const val MODELS_UNIT: Int = 23

        /** The unit a reflected emulated draw's texture is bound on. */
        private const val DYNAMIC_TEXTURE_UNIT: Int = 27
    }

    private var framebuffer: Int = 0
    private var texture: Int = 0
    private var depth: Int = 0
    private var width: Int = 0
    private var height: Int = 0
    private var program: WebGLProgram? = null
    private var dynamicProgram: WebGLProgram? = null

    private var debugFrame: Int = 0

    private val SHOT: String? = System.getProperty("client.test.hdReflectionShot")
    private val PARTS: String = System.getProperty("client.test.hdReflectionParts") ?: "static,dynamic,sky"
    private var shotFrame: Int = 0
    private val colourMask: ByteBuffer = ByteBuffer.allocateDirect(4)

    private fun ensureProgram(): WebGLProgram {
        var program: WebGLProgram? = this.program
        if (program == null) {
            program = HdShaders.program("reflect")
            GL33C.glUseProgram(program.id)
            GL33C.glUniform1i(GL33C.glGetUniformLocation(program.id, "uMaterials"), HdMaterials.UNIT_MATERIALS)
            GL33C.glUniform1i(GL33C.glGetUniformLocation(program.id, "uSmallLinear"), HdMaterials.UNIT_SMALL_LINEAR)
            GL33C.glUniform1i(GL33C.glGetUniformLocation(program.id, "uLargeLinear"), HdMaterials.UNIT_LARGE_LINEAR)
            GL33C.glUniform1i(GL33C.glGetUniformLocation(program.id, "uFineLinear"), HdMaterials.UNIT_FINE_LINEAR)
            this.program = program
        }
        return program
    }

    private fun ensureDynamicProgram(): WebGLProgram {
        var program: WebGLProgram? = this.dynamicProgram
        if (program == null) {
            program = HdShaders.program("reflect_dynamic")
            this.dynamicProgram = program
        }
        return program
    }

    private fun ensureTarget(w: Int, h: Int) {
        if (framebuffer != 0 && width == w && height == h) return
        if (framebuffer == 0) {
            framebuffer = GL33C.glGenFramebuffers()
            texture = GL33C.glGenTextures()
            depth = GL33C.glGenRenderbuffers()
        }
        width = w
        height = h
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, texture)
        GL33C.glTexImage2D(GL33C.GL_TEXTURE_2D, 0, GL33C.GL_RGBA8, w, h, 0, GL33C.GL_RGBA, GL33C.GL_UNSIGNED_BYTE, null as ByteBuffer?)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_LINEAR)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_LINEAR)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_S, GL33C.GL_CLAMP_TO_EDGE)
        GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_T, GL33C.GL_CLAMP_TO_EDGE)
        GL33C.glBindRenderbuffer(GL33C.GL_RENDERBUFFER, depth)
        GL33C.glRenderbufferStorage(GL33C.GL_RENDERBUFFER, GL33C.GL_DEPTH_COMPONENT24, w, h)
        GL33C.glBindRenderbuffer(GL33C.GL_RENDERBUFFER, 0)
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, framebuffer)
        GL33C.glFramebufferTexture2D(GL33C.GL_DRAW_FRAMEBUFFER, GL33C.GL_COLOR_ATTACHMENT0, GL33C.GL_TEXTURE_2D, texture, 0)
        GL33C.glFramebufferRenderbuffer(GL33C.GL_DRAW_FRAMEBUFFER, GL33C.GL_DEPTH_ATTACHMENT, GL33C.GL_RENDERBUFFER, depth)
    }

    /** The water plane of the local player's ground: the mean height of its water tiles, NaN for none. */
    private fun waterPlane(): Float {
        val grounds = Scene.aClass65Array2 ?: return Float.NaN
        val ground: HdGround = (if (grounds.isNotEmpty()) grounds[0] else null) as? HdGround ?: return Float.NaN
        return ground.hdWaterPlaneY
    }

    /**
     * Draws the reflection for this frame, in opaqueEntitiesDone (the main pass, after the shadow
     * map): the state's hdReflection carries the viewport it is for (z 0 when there is none).
     */
    fun render(toolkit: HdToolkit, scene: HdSceneRenderer, post: HdPost, shadows: HdShadows) {
        val state: GlState = HdGl.state
        state.hdReflection[2] = 0.0f
        if (!HdSettings.reflections || HdSettings.water == 0) return
        val plane: Float = waterPlane()
        if (plane.isNaN()) return
        if (System.getProperty("client.test.hdDebug") != null && (debugFrame++ % 60) == 0) {
            val player = com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate.localPlayer
            println("HDDEBUG reflection plane y " + plane + (if (player != null) " player " + player.x + "," + player.y + "," + player.z else ""))
        }
        HdGl.flushImmediate()
        val drawFramebuffer: Int = GL33C.glGetInteger(GL33C.GL_DRAW_FRAMEBUFFER_BINDING)
        val readFramebuffer: Int = GL33C.glGetInteger(GL33C.GL_READ_FRAMEBUFFER_BINDING)
        val viewport = IntArray(4)
        GL33C.glGetIntegerv(GL33C.GL_VIEWPORT, viewport)
        if (viewport[2] < 2 || viewport[3] < 2) return
        val scissor: Boolean = GL33C.glIsEnabled(GL33C.GL_SCISSOR_TEST)
        val blend: Boolean = GL33C.glIsEnabled(GL33C.GL_BLEND)
        val cull: Boolean = GL33C.glIsEnabled(GL33C.GL_CULL_FACE)
        val depthTest: Boolean = GL33C.glIsEnabled(GL33C.GL_DEPTH_TEST)
        val depthMask: Boolean = GL33C.glGetBoolean(GL33C.GL_DEPTH_WRITEMASK)
        val depthFunc: Int = GL33C.glGetInteger(GL33C.GL_DEPTH_FUNC)
        GL33C.glGetBooleanv(GL33C.GL_COLOR_WRITEMASK, colourMask)
        val elementBinding: Int = GL33C.glGetInteger(GL33C.GL_ELEMENT_ARRAY_BUFFER_BINDING)
        val previousUnit: Int = state.activeTextureUnit
        // the matrices: the view, the mirror about the plane (world y grows downwards), the projection
        val view: FloatArray = state.matrixStack.modelview().copyOf()
        val projection: FloatArray = state.matrixStack.projection().copyOf()
        val mirror: FloatArray = Mat4.identity()
        mirror[5] = -1.0f
        mirror[13] = 2.0f * plane
        val mirroredView: FloatArray = Mat4.multiply(view, mirror)
        val worldToClip: FloatArray = Mat4.multiply(projection, mirroredView)
        val inverseView: FloatArray = Mat4.invert(view) ?: return
        val skyProjection: FloatArray = Mat4.multiply(projection, Mat4.multiply(mirroredView, inverseView))

        ensureTarget(maxOf(1, viewport[2] / 2), maxOf(1, viewport[3] / 2))
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, framebuffer)
        GL33C.glViewport(0, 0, width, height)
        GL33C.glDisable(GL33C.GL_SCISSOR_TEST)
        GL33C.glDisable(GL33C.GL_BLEND)
        GL33C.glDisable(GL33C.GL_CULL_FACE)
        GL33C.glEnable(GL33C.GL_DEPTH_TEST)
        GL33C.glDepthFunc(GL33C.GL_LESS)
        GL33C.glDepthMask(true)
        GL33C.glColorMask(true, true, true, true)
        GL33C.glEnable(GL33C.GL_CLIP_DISTANCE0)
        // the texture must not be bound for sampling while it is drawn into
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, 0)
        GL33C.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GL33C.glClear(GL33C.GL_COLOR_BUFFER_BIT or GL33C.GL_DEPTH_BUFFER_BIT)

        val program: WebGLProgram = ensureProgram()
        GL33C.glUseProgram(program.id)
        GL33C.glUniformMatrix4fv(GL33C.glGetUniformLocation(program.id, "uWorldToClip"), false, worldToClip)
        GL33C.glUniformMatrix4fv(GL33C.glGetUniformLocation(program.id, "uView"), false, view)
        GL33C.glUniformMatrix4fv(GL33C.glGetUniformLocation(program.id, "uMirroredView"), false, mirroredView)
        GL33C.glUniform1f(GL33C.glGetUniformLocation(program.id, "uPlane"), plane)
        CoreGl.uniform4fv(GL33C.glGetUniformLocation(program.id, "uGlobalAmbient"), state.globalAmbient)
        for (i in 0 until 2) {
            val position: FloatArray = state.lightPosition[i]
            GL33C.glUniform4f(GL33C.glGetUniformLocation(program.id, "uDirLight[" + i + "]"), position[0], position[1], position[2], if (state.lightEnabled[i]) 1.0f else 0.0f)
            CoreGl.uniform4fv(GL33C.glGetUniformLocation(program.id, "uDirDiffuse[" + i + "]"), state.lightDiffuse[i])
        }
        CoreGl.uniform4fv(GL33C.glGetUniformLocation(program.id, "uFogColor"), state.fogColor)
        GL33C.glUniform1f(GL33C.glGetUniformLocation(program.id, "uFogStart"), if (state.fogEnabled) state.fogStart else 1.0e9f)
        GL33C.glUniform1f(GL33C.glGetUniformLocation(program.id, "uFogEnd"), if (state.fogEnabled) state.fogEnd else 2.0e9f)
        scene.bindMaterials()
        // client.test.hdReflectionParts=static,dynamic,sky: which parts are drawn (debugging)
        if (PARTS.contains("static")) scene.drawAllForBake(program, MODELS_UNIT)
        // the players, NPCs and animated locs drawn so far this frame (and last frame's translucent ones)
        val dynamic: WebGLProgram = ensureDynamicProgram()
        GL33C.glUseProgram(dynamic.id)
        GL33C.glUniformMatrix4fv(GL33C.glGetUniformLocation(dynamic.id, "uMirroredView"), false, mirroredView)
        GL33C.glUniform1f(GL33C.glGetUniformLocation(dynamic.id, "uPlane"), plane)
        CoreGl.uniform4fv(GL33C.glGetUniformLocation(dynamic.id, "uGlobalAmbient"), state.globalAmbient)
        for (i in 0 until 2) {
            val position: FloatArray = state.lightPosition[i]
            GL33C.glUniform4f(GL33C.glGetUniformLocation(dynamic.id, "uDirLight[" + i + "]"), position[0], position[1], position[2], if (state.lightEnabled[i]) 1.0f else 0.0f)
            CoreGl.uniform4fv(GL33C.glGetUniformLocation(dynamic.id, "uDirDiffuse[" + i + "]"), state.lightDiffuse[i])
        }
        CoreGl.uniform4fv(GL33C.glGetUniformLocation(dynamic.id, "uFogColor"), state.fogColor)
        GL33C.glUniform1f(GL33C.glGetUniformLocation(dynamic.id, "uFogStart"), if (state.fogEnabled) state.fogStart else 1.0e9f)
        GL33C.glUniform1f(GL33C.glGetUniformLocation(dynamic.id, "uFogEnd"), if (state.fogEnabled) state.fogEnd else 2.0e9f)
        // (hdReflectionParts=nomirror: the dynamic draws unmirrored, to check their placement against the frame)
        if (PARTS.contains("dynamic")) shadows.drawReflected(dynamic, if (PARTS.contains("nomirror")) Mat4.multiply(projection, view) else worldToClip, view, DYNAMIC_TEXTURE_UNIT, if (PARTS.contains("now")) 1 else if (PARTS.contains("late")) 2 else 0)
        GL33C.glDisable(GL33C.GL_CLIP_DISTANCE0)
        if (HdSettings.sky && PARTS.contains("sky")) {
            post.drawSky(skyProjection, true)
        }

        // client.test.hdReflectionShot=<png>: the reflection texture saved every 120 frames (debugging)
        if (SHOT != null && (++shotFrame % 120) == 0) {
            val pixels: ByteBuffer = org.lwjgl.BufferUtils.createByteBuffer(width * height * 4)
            GL33C.glBindFramebuffer(GL33C.GL_READ_FRAMEBUFFER, framebuffer)
            GL33C.glReadPixels(0, 0, width, height, GL33C.GL_RGBA, GL33C.GL_UNSIGNED_BYTE, pixels)
            GL33C.glBindFramebuffer(GL33C.GL_READ_FRAMEBUFFER, readFramebuffer)
            val image = java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val at: Int = ((height - 1 - y) * width + x) * 4
                    val r: Int = pixels.get(at).toInt() and 0xFF
                    val g: Int = pixels.get(at + 1).toInt() and 0xFF
                    val b: Int = pixels.get(at + 2).toInt() and 0xFF
                    val alpha: Int = pixels.get(at + 3).toInt() and 0xFF
                    image.setRGB(x, y, (alpha shl 24) or (r shl 16) or (g shl 8) or b)
                }
            }
            javax.imageio.ImageIO.write(image, "png", java.io.File(SHOT))
            println("HDDEBUG reflection saved " + SHOT + " (" + width + "x" + height + ")")
        }
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, drawFramebuffer)
        GL33C.glViewport(viewport[0], viewport[1], viewport[2], viewport[3])
        if (scissor) GL33C.glEnable(GL33C.GL_SCISSOR_TEST)
        if (blend) GL33C.glEnable(GL33C.GL_BLEND)
        if (cull) GL33C.glEnable(GL33C.GL_CULL_FACE)
        if (!depthTest) GL33C.glDisable(GL33C.GL_DEPTH_TEST)
        GL33C.glDepthFunc(depthFunc)
        GL33C.glDepthMask(depthMask)
        GL33C.glColorMask(colourMask.get(0).toInt() != 0, colourMask.get(1).toInt() != 0, colourMask.get(2).toInt() != 0, colourMask.get(3).toInt() != 0)
        GL33C.glBindVertexArray(state.vertexArray)
        GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, state.boundArrayBuffer?.id ?: 0)
        GL33C.glBindBuffer(GL33C.GL_ELEMENT_ARRAY_BUFFER, elementBinding)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, texture)
        GL33C.glBindSampler(UNIT, 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + previousUnit)
        state.invalidateProgramCache()
        // the viewport the water draws see it through
        state.hdReflection[0] = viewport[0].toFloat()
        state.hdReflection[1] = viewport[1].toFloat()
        state.hdReflection[2] = viewport[2].toFloat()
        state.hdReflection[3] = viewport[3].toFloat()
    }
}
