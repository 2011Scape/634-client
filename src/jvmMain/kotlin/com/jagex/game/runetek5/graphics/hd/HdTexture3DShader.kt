/*
 * Not part of the original client: 634HD (toolkit 6), copied from Texture3DShaderGL (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext

class HdTexture3DShader {

	private var shader: HdProgram? = null

	private val openglToolkit: HdToolkit?

	constructor(glToolkit: HdToolkit?) {
		this.openglToolkit = glToolkit
	}

	fun activate(glVolumeTexture: HdVolumeTexture?, glVolumeTexture2: HdVolumeTexture?, relief: Float): Boolean {
		if (!this.createShader()) {
			return false
		}
		val glFramebuffer: HdFramebuffer? = this.openglToolkit!!.integratedGraphicsImageProducer
		val renderBuffer: HdRenderBuffer = HdRenderBuffer(this.openglToolkit, 6408, glVolumeTexture!!.width, glVolumeTexture.height)
		this.openglToolkit.pushTarget(glFramebuffer)
		var success: Boolean = false
		glFramebuffer!!.attachRenderbuffer(0, renderBuffer)
		if (glFramebuffer.isFramebufferComplete()) {
			HdGl.glPushMatrix()
			HdGl.glLoadIdentity()
			HdGl.glMatrixMode(OpenGLConstants.GL_PROJECTION)
			HdGl.glPushMatrix()
			HdGl.glLoadIdentity()
			HdGl.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0)
			HdGl.glPushAttrib(OpenGLConstants.GL_VIEWPORT_BIT)
			HdGl.glViewport(0, 0, glVolumeTexture.width, glVolumeTexture.height)
			HdGl.glUseProgramObjectARB(this.shader!!.programHandle)
			HdGl.glUniform1iARB(HdGl.glGetUniformLocationARB(this.shader!!.programHandle, "heightMap"), 0)
			HdGl.glUniform1fARB(HdGl.glGetUniformLocationARB(this.shader!!.programHandle, "rcpRelief"), 1.0f / relief)
			HdGl.glUniform2fARB(HdGl.glGetUniformLocationARB(this.shader!!.programHandle, "sampleSize"), 1.0f / glVolumeTexture2!!.width.toFloat(), 1.0f / glVolumeTexture2.height.toFloat())
			var i: Int = 0
			while (i < glVolumeTexture.depth) {
				val depthCoord: Float = i.toFloat() / glVolumeTexture.depth.toFloat()
				this.openglToolkit.setActiveTexture(glVolumeTexture2)
				HdGl.glBegin(OpenGLConstants.GL_QUADS)
				HdGl.glTexCoord3f(0.0f, 0.0f, depthCoord)
				HdGl.glVertex2f(0.0f, 0.0f)
				HdGl.glTexCoord3f(1.0f, 0.0f, depthCoord)
				HdGl.glVertex2f(1.0f, 0.0f)
				HdGl.glTexCoord3f(1.0f, 1.0f, depthCoord)
				HdGl.glVertex2f(1.0f, 1.0f)
				HdGl.glTexCoord3f(0.0f, 1.0f, depthCoord)
				HdGl.glVertex2f(0.0f, 1.0f)
				HdGl.glEnd()
				glVolumeTexture.copyFromFramebuffer(glVolumeTexture.width, i, glVolumeTexture.height)
				i++
			}
			HdGl.glUseProgramObjectARB(0L)
			HdGl.glPopAttrib()
			HdGl.glPopMatrix()
			HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
			HdGl.glPopMatrix()
			success = true
		}
		glFramebuffer.clearAttachment(0)
		this.openglToolkit.popTarget(glFramebuffer)
		return success
	}

	fun createShader(): Boolean {
		if (this.openglToolkit!!.extFramebufferObject && this.openglToolkit.arbFragmentShader && this.shader == null) {
			val fragmentShader: HdShader? = HdShader.compile(35632, this.openglToolkit, "uniform float rcpRelief;\nuniform vec2 sampleSize;\nuniform sampler3D heightMap;\nvoid main() {\nfloat dx = texture3D(heightMap, vec3(-sampleSize.x, 0.0, 0.0)+gl_TexCoord[0].xyz).r - texture3D(heightMap, vec3(sampleSize.x, 0.0, 0.0)+gl_TexCoord[0].xyz).r;\nfloat dy = texture3D(heightMap, vec3(0.0, -sampleSize.y, 0.0)+gl_TexCoord[0].xyz).r - texture3D(heightMap, vec3(0.0, sampleSize.y, 0.0)+gl_TexCoord[0].xyz).r;\ngl_FragColor = vec4(0.5+normalize(vec3(dx, dy, rcpRelief))*0.5, texture3D(heightMap, gl_TexCoord[0].xyz).r);\n}\n")
			if (fragmentShader != null) {
				this.shader = HdProgram.link(this.openglToolkit, arrayOf<HdShader?>(fragmentShader))
			}
		}
		return this.shader != null
	}
}
