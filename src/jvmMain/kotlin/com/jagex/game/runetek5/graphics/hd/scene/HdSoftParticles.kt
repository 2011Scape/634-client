/*
 * Not part of the original client: 634HD (toolkit 6). Soft particles (client.hd.softParticles): before
 * the particle batch of the 3D view is drawn (HdParticleRenderer), the depth drawn so far is copied
 * into a texture; ff.frag fades each particle fragment out as it nears the geometry behind it.
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.client.features.HdSettings
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import java.nio.ByteBuffer
import org.lwjgl.opengl.GL33C

class HdSoftParticles {

    companion object {
        const val UNIT: Int = 18
    }

    private var texture: Int = 0
    private var framebuffer: Int = 0
    private var width: Int = 0
    private var height: Int = 0
    private var failed: Boolean = false

    /** Copies the bound framebuffer's depth ([width] x [height]) and turns soft particles on; false if it cannot. */
    fun begin(width: Int, height: Int): Boolean {
        if (!HdSettings.softParticles || failed || width <= 0 || height <= 0) return false
        HdGl.flushImmediate()
        val read: Int = GL33C.glGetInteger(GL33C.GL_DRAW_FRAMEBUFFER_BINDING)
        if (read == 0) return false
        if (texture == 0 || this.width != width || this.height != height) {
            release()
            this.width = width
            this.height = height
            texture = GL33C.glGenTextures()
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT)
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, texture)
            GL33C.glTexImage2D(GL33C.GL_TEXTURE_2D, 0, GL33C.GL_DEPTH_COMPONENT24, width, height, 0, GL33C.GL_DEPTH_COMPONENT, GL33C.GL_UNSIGNED_INT, null as ByteBuffer?)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MIN_FILTER, GL33C.GL_NEAREST)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_MAG_FILTER, GL33C.GL_NEAREST)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_S, GL33C.GL_CLAMP_TO_EDGE)
            GL33C.glTexParameteri(GL33C.GL_TEXTURE_2D, GL33C.GL_TEXTURE_WRAP_T, GL33C.GL_CLAMP_TO_EDGE)
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + HdGl.state.activeTextureUnit)
            framebuffer = GL33C.glGenFramebuffers()
            GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, framebuffer)
            GL33C.glFramebufferTexture2D(GL33C.GL_DRAW_FRAMEBUFFER, GL33C.GL_DEPTH_ATTACHMENT, GL33C.GL_TEXTURE_2D, texture, 0)
            GL33C.glDrawBuffer(GL33C.GL_NONE)
            GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, read)
        }
        val previousRead: Int = GL33C.glGetInteger(GL33C.GL_READ_FRAMEBUFFER_BINDING)
        val scissor: Boolean = GL33C.glIsEnabled(GL33C.GL_SCISSOR_TEST)
        GL33C.glDisable(GL33C.GL_SCISSOR_TEST)
        while (GL33C.glGetError() != GL33C.GL_NO_ERROR) { }
        GL33C.glBindFramebuffer(GL33C.GL_READ_FRAMEBUFFER, read)
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, framebuffer)
        GL33C.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL33C.GL_DEPTH_BUFFER_BIT, GL33C.GL_NEAREST)
        val error: Int = GL33C.glGetError()
        GL33C.glBindFramebuffer(GL33C.GL_DRAW_FRAMEBUFFER, read)
        GL33C.glBindFramebuffer(GL33C.GL_READ_FRAMEBUFFER, previousRead)
        if (scissor) GL33C.glEnable(GL33C.GL_SCISSOR_TEST)
        if (error != GL33C.GL_NO_ERROR) {
            // the view's depth buffer cannot be copied into this format on this driver: hard particles
            failed = true
            System.err.println("634HD: soft particles are off: the depth copy failed (GL error " + error + ")")
            return false
        }
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + UNIT)
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, texture)
        GL33C.glBindSampler(UNIT, 0)
        GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + HdGl.state.activeTextureUnit)
        val state = HdGl.state
        val projection: FloatArray = state.matrixStack.projection()
        state.hdDepthParams[0] = projection[10]
        state.hdDepthParams[1] = projection[14]
        state.hdDepthParams[2] = 1.0f / width
        state.hdDepthParams[3] = 1.0f / height
        state.hdSoft = 1
        return true
    }

    fun end() {
        HdGl.flushImmediate()
        HdGl.state.hdSoft = 0
    }

    fun release() {
        if (texture != 0) GL33C.glDeleteTextures(texture)
        if (framebuffer != 0) GL33C.glDeleteFramebuffers(framebuffer)
        texture = 0
        framebuffer = 0
    }
}
