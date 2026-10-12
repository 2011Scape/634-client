package com.jagex.game.runetek5.graphics.shader

import com.jagex.game.runetek5.GlShader
import com.jagex.game.runetek5.cache.node.lru.OpenGlRenderBuffer
import com.jagex.game.runetek5.GlVolumeTexture
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.gl.GlFramebuffer
import com.jagex.game.runetek5.graphics.gl.GlProgram
import com.jagex.game.runetek5.graphics.gl.GlToolkit

@OriginalClass("client!qi")
class Texture3DShaderGL {

	@OriginalMember(owner = "client!qi", name = "c", descriptor = "Lclient!dw;")
	private var shader: GlProgram? = null

	@OriginalMember(owner = "client!qi", name = "a", descriptor = "Lclient!qo;")
	private val openglToolkit: GlToolkit?

	@OriginalMember(owner = "client!qi", name = "<init>", descriptor = "(Lclient!qo;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?) {
		this.openglToolkit = glToolkit
	}

	@OriginalMember(owner = "client!qi", name = "a", descriptor = "(Lclient!gj;Lclient!gj;IF)Z")
	fun activate(@OriginalArg(0) glVolumeTexture: GlVolumeTexture?, @OriginalArg(1) glVolumeTexture2: GlVolumeTexture?, @OriginalArg(3) relief: Float): Boolean {
		if (!this.createShader()) {
			return false
		}
		@Pc(15) val glFramebuffer: GlFramebuffer? = this.openglToolkit!!.integratedGraphicsImageProducer
		@Pc(34) val renderBuffer: OpenGlRenderBuffer = OpenGlRenderBuffer(this.openglToolkit, 6408, glVolumeTexture!!.width, glVolumeTexture.height)
		this.openglToolkit.pushTarget(glFramebuffer)
		@Pc(41) var success: Boolean = false
		glFramebuffer!!.attachRenderbuffer(0, renderBuffer)
		if (glFramebuffer.isFramebufferComplete()) {
			OpenGL.glPushMatrix()
			OpenGL.glLoadIdentity()
			OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION)
			OpenGL.glPushMatrix()
			OpenGL.glLoadIdentity()
			OpenGL.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0)
			OpenGL.glPushAttrib(OpenGLConstants.GL_VIEWPORT_BIT)
			OpenGL.glViewport(0, 0, glVolumeTexture.width, glVolumeTexture.height)
			OpenGL.glUseProgramObjectARB(this.shader!!.programHandle)
			OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(this.shader!!.programHandle, "heightMap"), 0)
			OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(this.shader!!.programHandle, "rcpRelief"), 1.0f / relief)
			OpenGL.glUniform2fARB(OpenGL.glGetUniformLocationARB(this.shader!!.programHandle, "sampleSize"), 1.0f / glVolumeTexture2!!.width.toFloat(), 1.0f / glVolumeTexture2.height.toFloat())
			@Pc(110) var i: Int = 0
			while (i < glVolumeTexture.depth) {
				@Pc(119) val depthCoord: Float = i.toFloat() / glVolumeTexture.depth.toFloat()
				this.openglToolkit.setActiveTexture(glVolumeTexture2)
				OpenGL.glBegin(OpenGLConstants.GL_QUADS)
				OpenGL.glTexCoord3f(0.0f, 0.0f, depthCoord)
				OpenGL.glVertex2f(0.0f, 0.0f)
				OpenGL.glTexCoord3f(1.0f, 0.0f, depthCoord)
				OpenGL.glVertex2f(1.0f, 0.0f)
				OpenGL.glTexCoord3f(1.0f, 1.0f, depthCoord)
				OpenGL.glVertex2f(1.0f, 1.0f)
				OpenGL.glTexCoord3f(0.0f, 1.0f, depthCoord)
				OpenGL.glVertex2f(0.0f, 1.0f)
				OpenGL.glEnd()
				glVolumeTexture.copyFromFramebuffer(glVolumeTexture.width, i, glVolumeTexture.height)
				i++
			}
			OpenGL.glUseProgramObjectARB(0L)
			OpenGL.glPopAttrib()
			OpenGL.glPopMatrix()
			OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
			OpenGL.glPopMatrix()
			success = true
		}
		glFramebuffer.clearAttachment(0)
		this.openglToolkit.popTarget(glFramebuffer)
		return success
	}

	@OriginalMember(owner = "client!qi", name = "a", descriptor = "(I)Z")
	fun createShader(): Boolean {
		if (this.openglToolkit!!.extFramebufferObject && this.openglToolkit.arbFragmentShader && this.shader == null) {
			@Pc(17) val fragmentShader: GlShader? = GlShader.compile(35632, this.openglToolkit, "uniform float rcpRelief;\nuniform vec2 sampleSize;\nuniform sampler3D heightMap;\nvoid main() {\nfloat dx = texture3D(heightMap, vec3(-sampleSize.x, 0.0, 0.0)+gl_TexCoord[0].xyz).r - texture3D(heightMap, vec3(sampleSize.x, 0.0, 0.0)+gl_TexCoord[0].xyz).r;\nfloat dy = texture3D(heightMap, vec3(0.0, -sampleSize.y, 0.0)+gl_TexCoord[0].xyz).r - texture3D(heightMap, vec3(0.0, sampleSize.y, 0.0)+gl_TexCoord[0].xyz).r;\ngl_FragColor = vec4(0.5+normalize(vec3(dx, dy, rcpRelief))*0.5, texture3D(heightMap, gl_TexCoord[0].xyz).r);\n}\n")
			if (fragmentShader != null) {
				this.shader = GlProgram.link(this.openglToolkit, arrayOf<GlShader?>(fragmentShader))
			}
		}
		return this.shader != null
	}
}
