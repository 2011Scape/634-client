/*
 * Not part of the original client: 634HD (toolkit 6), copied from OpenGlCubeTextureGenerator (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField

class HdCubeTextureGenerator : HdEnvironmentSampler {

	private val glToolkit: HdToolkit?

	private val cubeTexture: HdCubeTexture

	constructor(glToolkit: HdToolkit?, size: Int) : super() {
		this.glToolkit = glToolkit
		this.cubeTexture = HdCubeTexture(glToolkit, 6408, size)
	}

	fun blendCubeTextures(glCubeTexture: HdCubeTexture?, alpha: Float, glCubeTexture2: HdCubeTexture?): Boolean {
		var framebuffersReady: Boolean = true
		val glFramebuffer: HdFramebuffer? = this.glToolkit!!.integratedGraphicsImageProducer
		this.glToolkit.K(HdCubeTextureGenerator.savedClip)
		this.glToolkit.la()
		this.glToolkit.setOrthoMatrixMode()
		HdGl.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		HdGl.glLoadIdentity()
		HdGl.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0)
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		HdGl.glLoadIdentity()
		HdGl.glPushAttrib(OpenGLConstants.GL_VIEWPORT_BIT)
		HdGl.glViewport(0, 0, this.cubeTexture.size, this.cubeTexture.size)
		this.glToolkit.toggleFog(false)
		this.glToolkit.setLightingEnabled(false)
		this.glToolkit.toggleZbuffer(false)
		this.glToolkit.setDepthMask(false)
		this.glToolkit.renderTexture(-2)
		this.glToolkit.activateTexture(1)
		this.glToolkit.updatePointSizeRange(alpha, 0.0f, 0.0f, 0.0f)
		this.glToolkit.loadTextureCombineMode(34165, 34165)
		this.glToolkit.setActiveTexture(glCubeTexture2)
		this.glToolkit.activateTexture(0)
		this.glToolkit.setColourOp(1)
		this.glToolkit.setActiveTexture(glCubeTexture)
		this.glToolkit.pushTarget(glFramebuffer)
		for (i in 0 until 6) {
			val cubeFace: Int = i + 34069
			glFramebuffer!!.attachCubeFace(cubeFace, this.cubeTexture)
			glFramebuffer.drawBuffer(0)
			if (!glFramebuffer.isFramebufferComplete()) {
				framebuffersReady = false
				break
			}
			HdGl.glBegin(OpenGLConstants.GL_QUADS)
			if (cubeFace == 34069) {
				HdGl.glTexCoord3i(65535, 65534, 65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65535, 65534, 65534)
				HdGl.glVertex2f(0.0f, 0.0f)
				HdGl.glTexCoord3i(65535, 65534, -65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65535, 65534, -65534)
				HdGl.glVertex2f(1.0f, 0.0f)
				HdGl.glTexCoord3i(65535, -65534, -65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65535, -65534, -65534)
				HdGl.glVertex2f(1.0f, 1.0f)
				HdGl.glTexCoord3i(65535, -65534, 65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65535, -65534, 65534)
				HdGl.glVertex2f(0.0f, 1.0f)
			} else if (cubeFace == 34070) {
				HdGl.glTexCoord3i(-65535, 65534, -65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65535, 65534, -65534)
				HdGl.glVertex2f(0.0f, 0.0f)
				HdGl.glTexCoord3i(-65535, 65534, 65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65535, 65534, 65534)
				HdGl.glVertex2f(1.0f, 0.0f)
				HdGl.glTexCoord3i(-65535, -65534, 65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65535, -65534, 65534)
				HdGl.glVertex2f(1.0f, 1.0f)
				HdGl.glTexCoord3i(-65535, -65534, -65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65535, -65534, -65534)
				HdGl.glVertex2f(0.0f, 1.0f)
			} else if (cubeFace == 34071) {
				HdGl.glTexCoord3i(-65534, 65535, -65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, 65535, -65534)
				HdGl.glVertex2f(0.0f, 0.0f)
				HdGl.glTexCoord3i(65534, 65535, -65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, 65535, -65534)
				HdGl.glVertex2f(1.0f, 0.0f)
				HdGl.glTexCoord3i(65534, 65535, 65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, 65535, 65534)
				HdGl.glVertex2f(1.0f, 1.0f)
				HdGl.glTexCoord3i(-65534, 65535, 65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, 65535, 65534)
				HdGl.glVertex2f(0.0f, 1.0f)
			} else if (cubeFace == 34072) {
				HdGl.glTexCoord3i(-65534, -65535, 65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, -65535, 65534)
				HdGl.glVertex2f(0.0f, 0.0f)
				HdGl.glTexCoord3i(65534, -65535, 65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, -65535, 65534)
				HdGl.glVertex2f(1.0f, 0.0f)
				HdGl.glTexCoord3i(65534, -65535, -65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, -65535, -65534)
				HdGl.glVertex2f(1.0f, 1.0f)
				HdGl.glTexCoord3i(-65534, -65535, -65534)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, -65535, -65534)
				HdGl.glVertex2f(0.0f, 1.0f)
			} else if (cubeFace == 34073) {
				HdGl.glTexCoord3i(-65534, 65534, 65535)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, 65534, 65535)
				HdGl.glVertex2f(0.0f, 0.0f)
				HdGl.glTexCoord3i(65534, 65534, 65535)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, 65534, 65535)
				HdGl.glVertex2f(1.0f, 0.0f)
				HdGl.glTexCoord3i(65534, -65534, 65535)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, -65534, 65535)
				HdGl.glVertex2f(1.0f, 1.0f)
				HdGl.glTexCoord3i(-65534, -65534, 65535)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, -65534, 65535)
				HdGl.glVertex2f(0.0f, 1.0f)
			} else if (cubeFace == 34074) {
				HdGl.glTexCoord3i(65534, 65534, -65535)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, 65534, -65535)
				HdGl.glVertex2f(0.0f, 0.0f)
				HdGl.glTexCoord3i(-65534, 65534, -65535)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, 65534, -65535)
				HdGl.glVertex2f(1.0f, 0.0f)
				HdGl.glTexCoord3i(-65534, -65534, -65535)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, -65534, -65534, -65535)
				HdGl.glVertex2f(1.0f, 1.0f)
				HdGl.glTexCoord3i(65534, -65534, -65535)
				HdGl.glMultiTexCoord3i(OpenGLConstants.GL_TEXTURE1, 65534, -65534, -65535)
				HdGl.glVertex2f(0.0f, 1.0f)
			}
			HdGl.glEnd()
		}
		glFramebuffer!!.clearAttachment(0)
		this.glToolkit.popTarget(glFramebuffer)
		this.glToolkit.activateTexture(1)
		this.glToolkit.setActiveTexture(null)
		this.glToolkit.loadTextureCombineMode(8448, 8448)
		this.glToolkit.activateTexture(0)
		this.glToolkit.setActiveTexture(null)
		HdGl.glPopAttrib()
		this.glToolkit.KA(HdCubeTextureGenerator.savedClip[0], HdCubeTextureGenerator.savedClip[1], HdCubeTextureGenerator.savedClip[2], HdCubeTextureGenerator.savedClip[3])
		if (framebuffersReady && !this.glToolkit.isAmd) {
			this.cubeTexture.generateMipmaps()
		}
		return framebuffersReady
	}

	override fun getCubeTexture(): HdCubeTexture? {
		return this.cubeTexture
	}

	fun getSize(): Int {
		return this.cubeTexture.size
	}

	companion object {
		@JvmField
		val savedClip: IntArray = IntArray(4)
	}
}
