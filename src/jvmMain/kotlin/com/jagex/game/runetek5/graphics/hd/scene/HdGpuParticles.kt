/*
 * Not part of the original client: 634HD (toolkit 6). GPU particles (client.hd.particles): embers
 * rising from fires, torches and braziers and sparks over lava, at the loc lights HdLights finds
 * (lights.json; flickering lights are fires, pulsing ones lava). The CPU uploads one record per
 * emitter; particles.vert works out every particle's age, place, size and colour from the time and a
 * seed, so there is no per-particle work on the CPU and the whole lot is one instanced draw, added
 * over the 3D view after its translucent entities. The game's own particle systems are untouched.
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.client.features.HdSettings
import com.jagex.game.runetek5.client.features.RenderStats
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdShaders
import com.jagex.game.runetek5.graphics.hd.gl.WebGLProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.lwjgl.opengl.GL33C

class HdGpuParticles {

    companion object {
        const val PER_EMITTER: Int = 24
    }

    private var debugFrame: Int = 0
    private var program: WebGLProgram? = null
    private var vao: Int = 0
    private var buffer: Int = 0
    private val upload: ByteBuffer = ByteBuffer.allocateDirect(HdLights.MAX_EMITTERS * 5 * 4).order(ByteOrder.nativeOrder())
    private val matrix: java.nio.FloatBuffer = org.lwjgl.BufferUtils.createFloatBuffer(16)

    /** Particles drawn last frame, for the debug panel. */
    var lastParticles: Int = 0
        private set

    /** Draws the particles of [lights]' emitters with world-to-eye [view] and the current projection. */
    fun draw(lights: HdLights, view: FloatArray) {
        lastParticles = 0
        val emitters: Int = lights.emitterCount
        if (!HdSettings.particles || emitters == 0) return
        HdGl.flushImmediate()
        if (program == null) {
            program = HdShaders.program("particles")
            vao = GL33C.glGenVertexArrays()
            buffer = GL33C.glGenBuffers()
            GL33C.glBindVertexArray(vao)
            GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, buffer)
            GL33C.glBufferData(GL33C.GL_ARRAY_BUFFER, upload.capacity().toLong(), GL33C.GL_STREAM_DRAW)
            GL33C.glEnableVertexAttribArray(0)
            GL33C.glVertexAttribPointer(0, 4, GL33C.GL_FLOAT, false, 20, 0L)
            GL33C.glVertexAttribDivisor(0, PER_EMITTER)
            GL33C.glEnableVertexAttribArray(1)
            GL33C.glVertexAttribPointer(1, 1, GL33C.GL_FLOAT, false, 20, 16L)
            GL33C.glVertexAttribDivisor(1, PER_EMITTER)
        }
        val p: WebGLProgram = program!!
        upload.clear()
        upload.asFloatBuffer().put(lights.emitters, 0, emitters * 5)
        upload.limit(emitters * 5 * 4)
        GL33C.glBindVertexArray(vao)
        GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, buffer)
        GL33C.glBufferSubData(GL33C.GL_ARRAY_BUFFER, 0L, upload)
        val blend: Boolean = GL33C.glIsEnabled(GL33C.GL_BLEND)
        val src: Int = GL33C.glGetInteger(GL33C.GL_BLEND_SRC_RGB)
        val dst: Int = GL33C.glGetInteger(GL33C.GL_BLEND_DST_RGB)
        val depthMask: Boolean = GL33C.glGetBoolean(GL33C.GL_DEPTH_WRITEMASK)
        val depthTest: Boolean = GL33C.glIsEnabled(GL33C.GL_DEPTH_TEST)
        val cull: Boolean = GL33C.glIsEnabled(GL33C.GL_CULL_FACE)
        GL33C.glEnable(GL33C.GL_BLEND)
        GL33C.glBlendFunc(GL33C.GL_ONE, GL33C.GL_ONE)
        GL33C.glDepthMask(false)
        GL33C.glEnable(GL33C.GL_DEPTH_TEST)
        GL33C.glDisable(GL33C.GL_CULL_FACE)
        GL33C.glUseProgram(p.id)
        GL33C.glUniform1i(GL33C.glGetUniformLocation(p.id, "uHdScene"), 1)
        GL33C.glUniform1i(GL33C.glGetUniformLocation(p.id, "uPerEmitter"), PER_EMITTER)
        matrix.clear()
        matrix.put(view).flip()
        GL33C.glUniformMatrix4fv(GL33C.glGetUniformLocation(p.id, "uView"), false, matrix)
        matrix.clear()
        matrix.put(HdGl.state.matrixStack.projection()).flip()
        GL33C.glUniformMatrix4fv(GL33C.glGetUniformLocation(p.id, "uProjection"), false, matrix)
        GL33C.glDrawArraysInstanced(GL33C.GL_TRIANGLE_STRIP, 0, 4, emitters * PER_EMITTER)
        RenderStats.drawCalls++
        lastParticles = emitters * PER_EMITTER
        if (System.getProperty("client.test.hdDebug") != null && (debugFrame++ % 200) == 0) {
            println("HDDEBUG particles emitters=" + emitters)
        }
        if (!blend) GL33C.glDisable(GL33C.GL_BLEND)
        GL33C.glBlendFunc(src, dst)
        GL33C.glDepthMask(depthMask)
        if (!depthTest) GL33C.glDisable(GL33C.GL_DEPTH_TEST)
        if (cull) GL33C.glEnable(GL33C.GL_CULL_FACE)
        val state = HdGl.state
        GL33C.glBindVertexArray(state.vertexArray)
        GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, state.boundArrayBuffer?.id ?: 0)
        state.invalidateProgramCache()
    }

    fun release() {
        if (program != null) GL33C.glDeleteProgram(program!!.id)
        if (vao != 0) GL33C.glDeleteVertexArrays(vao)
        if (buffer != 0) GL33C.glDeleteBuffers(buffer)
        program = null
        vao = 0
        buffer = 0
    }
}
