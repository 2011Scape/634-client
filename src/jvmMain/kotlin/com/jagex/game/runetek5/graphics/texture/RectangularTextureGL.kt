package com.jagex.game.runetek5.graphics.texture

import com.jagex.game.runetek5.GlShader
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.gl.GlFramebuffer
import com.jagex.game.runetek5.graphics.gl.GlProgram
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture
import com.jagex.game.runetek5.utils.Utils
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!rda")
class RectangularTextureGL : PostProcessEffect {

	@OriginalMember(owner = "client!rda", name = "A", descriptor = "Lclient!dw;")
	private var textureDisplay: GlProgram? = null

	@OriginalMember(owner = "client!rda", name = "C", descriptor = "Lclient!dw;")
	private var mipmappedScenetexShader: GlProgram? = null

	@OriginalMember(owner = "client!rda", name = "D", descriptor = "Lclient!rg;")
	private var bloomTextureB: GlFramebuffer? = null

	@OriginalMember(owner = "client!rda", name = "E", descriptor = "I")
	private var paddedWidth: Int = 0

	@OriginalMember(owner = "client!rda", name = "I", descriptor = "Lclient!tp;")
	private var blurTextureB: OpenGl2DTexture? = null

	@OriginalMember(owner = "client!rda", name = "L", descriptor = "I")
	private var sceneHeight: Int = 0

	@OriginalMember(owner = "client!rda", name = "N", descriptor = "Lclient!tp;")
	private var blurTextureA: OpenGl2DTexture? = null

	@OriginalMember(owner = "client!rda", name = "O", descriptor = "Lclient!dw;")
	private var bloomScenetexShader: GlProgram? = null

	@OriginalMember(owner = "client!rda", name = "Q", descriptor = "[Lclient!tp;")
	private var mipmaps: Array<OpenGl2DTexture?>? = null

	@OriginalMember(owner = "client!rda", name = "R", descriptor = "I")
	private var paddedHeight: Int = 0

	@OriginalMember(owner = "client!rda", name = "U", descriptor = "I")
	private var sceneWidth: Int = 0

	@OriginalMember(owner = "client!rda", name = "V", descriptor = "Lclient!dw;")
	private var scenetexShader: GlProgram? = null

	@OriginalMember(owner = "client!rda", name = "X", descriptor = "Lclient!rg;")
	private var bloomTextureA: GlFramebuffer? = null

	@OriginalMember(owner = "client!rda", name = "<init>", descriptor = "(Lclient!qo;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?) : super(glToolkit)

	@OriginalMember(owner = "client!rda", name = "a", descriptor = "(BI)V")
	override fun endPass(@OriginalArg(1) pass: Int) {
		OpenGL.glUseProgramObjectARB(0L)
		super.activeToolkit!!.activateTexture(1)
		super.activeToolkit!!.setActiveTexture(null)
		super.activeToolkit!!.activateTexture(0)
	}

	@OriginalMember(owner = "client!rda", name = "a", descriptor = "(Lclient!tp;Lclient!tp;IB)V")
	override fun draw(@OriginalArg(0) depthTexture: OpenGl2DTexture?, @OriginalArg(1) colorTexture: OpenGl2DTexture?, @OriginalArg(2) pass: Int) {
		OpenGL.glPushAttrib(OpenGLConstants.GL_VIEWPORT_BIT)
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		OpenGL.glPushMatrix()
		OpenGL.glLoadIdentity()
		OpenGL.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0)
		@Pc(269) var program: Long
		if (this.mipmaps == null) {
			super.activeToolkit!!.setActiveTexture(colorTexture)
			super.activeToolkit!!.pushTarget(this.bloomTextureB)
			this.bloomTextureB!!.drawBuffer(0)
			OpenGL.glViewport(0, 0, 256, 256)
			program = this.scenetexShader!!.programHandle
			OpenGL.glUseProgramObjectARB(program)
			OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(program, "sceneTex"), 0)
			OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(program, "params"), RectangularTextureGL.whitePoint, 0.0f, 0.0f)
			OpenGL.glBegin(OpenGLConstants.GL_QUADS)
			OpenGL.glTexCoord2f(0.0f, 0.0f)
			OpenGL.glVertex2i(0, 0)
			OpenGL.glTexCoord2f(this.sceneWidth.toFloat(), 0.0f)
			OpenGL.glVertex2i(1, 0)
			OpenGL.glTexCoord2f(this.sceneWidth.toFloat(), this.sceneHeight.toFloat())
			OpenGL.glVertex2i(1, 1)
			OpenGL.glTexCoord2f(0.0f, this.sceneHeight.toFloat())
			OpenGL.glVertex2i(0, 1)
			OpenGL.glEnd()
		} else {
			super.activeToolkit!!.pushTarget(this.bloomTextureA)
			@Pc(32) var width: Int = Utils.floorPowerOfTwo(this.sceneWidth)
			@Pc(37) var height: Int = Utils.floorPowerOfTwo(this.sceneHeight)
			@Pc(39) var level: Int = 0
			while (width > 256 || height > 256) {
				OpenGL.glViewport(0, 0, width, height)
				this.bloomTextureA!!.attachTexture(this.mipmaps!![level], 0)
				if (level == 0) {
					super.activeToolkit!!.setActiveTexture(colorTexture)
					OpenGL.glBegin(OpenGLConstants.GL_QUADS)
					OpenGL.glTexCoord2f(0.0f, 0.0f)
					OpenGL.glVertex2i(0, 0)
					OpenGL.glTexCoord2f(this.sceneWidth.toFloat(), 0.0f)
					OpenGL.glVertex2i(1, 0)
					OpenGL.glTexCoord2f(this.sceneWidth.toFloat(), this.sceneHeight.toFloat())
					OpenGL.glVertex2i(1, 1)
					OpenGL.glTexCoord2f(0.0f, this.sceneHeight.toFloat())
					OpenGL.glVertex2i(0, 1)
					OpenGL.glEnd()
				} else {
					super.activeToolkit!!.setActiveTexture(this.mipmaps!![level - 1])
					OpenGL.glBegin(OpenGLConstants.GL_QUADS)
					OpenGL.glTexCoord2f(0.0f, 0.0f)
					OpenGL.glVertex2i(0, 0)
					OpenGL.glTexCoord2f(1.0f, 0.0f)
					OpenGL.glVertex2i(1, 0)
					OpenGL.glTexCoord2f(1.0f, 1.0f)
					OpenGL.glVertex2i(1, 1)
					OpenGL.glTexCoord2f(0.0f, 1.0f)
					OpenGL.glVertex2i(0, 1)
					OpenGL.glEnd()
				}
				if (height > 256) {
					height = height shr 0x1
				}
				if (width > 256) {
					width = width shr 0x1
				}
				level++
			}
			super.activeToolkit!!.popTarget(this.bloomTextureA)
			super.activeToolkit!!.setActiveTexture(this.mipmaps!![level - 1])
			super.activeToolkit!!.pushTarget(this.bloomTextureB)
			this.bloomTextureB!!.drawBuffer(0)
			OpenGL.glViewport(0, 0, 256, 256)
			@Pc(201) val mipmapProgram: Long = this.mipmappedScenetexShader!!.programHandle
			OpenGL.glUseProgramObjectARB(mipmapProgram)
			OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(mipmapProgram, "sceneTex"), 0)
			OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(mipmapProgram, "params"), RectangularTextureGL.whitePoint, 0.0f, 0.0f)
			OpenGL.glBegin(OpenGLConstants.GL_QUADS)
			OpenGL.glTexCoord2f(0.0f, 0.0f)
			OpenGL.glVertex2i(0, 0)
			OpenGL.glTexCoord2f(1.0f, 0.0f)
			OpenGL.glVertex2i(1, 0)
			OpenGL.glTexCoord2f(1.0f, 1.0f)
			OpenGL.glVertex2i(1, 1)
			OpenGL.glTexCoord2f(0.0f, 1.0f)
			OpenGL.glVertex2i(0, 1)
			OpenGL.glEnd()
		}
		this.bloomTextureB!!.drawBuffer(1)
		super.activeToolkit!!.setActiveTexture(this.blurTextureA)
		program = this.textureDisplay!!.programHandle
		OpenGL.glUseProgramObjectARB(program)
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(program, "baseTex"), 0)
		OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(program, "step"), 0.00390625f, 0.0f, 0.0f)
		OpenGL.glBegin(OpenGLConstants.GL_QUADS)
		OpenGL.glTexCoord2f(0.0f, 0.0f)
		OpenGL.glVertex2i(0, 0)
		OpenGL.glTexCoord2f(1.0f, 0.0f)
		OpenGL.glVertex2i(1, 0)
		OpenGL.glTexCoord2f(1.0f, 1.0f)
		OpenGL.glVertex2i(1, 1)
		OpenGL.glTexCoord2f(0.0f, 1.0f)
		OpenGL.glVertex2i(0, 1)
		OpenGL.glEnd()
		this.bloomTextureB!!.drawBuffer(0)
		super.activeToolkit!!.setActiveTexture(this.blurTextureB)
		OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(program, "step"), 0.0f, 0.00390625f, 0.0f)
		OpenGL.glBegin(OpenGLConstants.GL_QUADS)
		OpenGL.glTexCoord2f(0.0f, 0.0f)
		OpenGL.glVertex2i(0, 0)
		OpenGL.glTexCoord2f(1.0f, 0.0f)
		OpenGL.glVertex2i(1, 0)
		OpenGL.glTexCoord2f(1.0f, 1.0f)
		OpenGL.glVertex2i(1, 1)
		OpenGL.glTexCoord2f(0.0f, 1.0f)
		OpenGL.glVertex2i(0, 1)
		OpenGL.glEnd()
		OpenGL.glPopAttrib()
		OpenGL.glPopMatrix()
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		super.activeToolkit!!.popTarget(this.bloomTextureB)
		@Pc(439) val bloomProgram: Long = this.bloomScenetexShader!!.programHandle
		OpenGL.glUseProgramObjectARB(bloomProgram)
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(bloomProgram, "sceneTex"), 0)
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(bloomProgram, "bloomTex"), 1)
		OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(bloomProgram, "params"), RectangularTextureGL.brightpass, RectangularTextureGL.bloom, 0.0f)
		super.activeToolkit!!.activateTexture(1)
		super.activeToolkit!!.setActiveTexture(this.blurTextureA)
		super.activeToolkit!!.activateTexture(0)
		super.activeToolkit!!.setActiveTexture(colorTexture)
	}

	@OriginalMember(owner = "client!rda", name = "a", descriptor = "(IBI)V")
	override fun resize(@OriginalArg(0) height: Int, @OriginalArg(2) width: Int) {
		this.sceneHeight = height
		this.sceneWidth = width
		@Pc(20) val pow2Width: Int = Utils.floorPowerOfTwo(this.sceneWidth)
		@Pc(25) val pow2Height: Int = Utils.floorPowerOfTwo(this.sceneHeight)
		if (pow2Width == this.paddedWidth && this.paddedHeight == pow2Height) {
			return
		}
		@Pc(42) var local42: Int
		if (this.mipmaps != null) {
			var mipmapIndex: Int = 0
			while (mipmapIndex < this.mipmaps!!.size) {
				this.mipmaps!![mipmapIndex]!!.delete()
				mipmapIndex++
			}
			this.mipmaps = null
		}
		if (pow2Width <= 256 && pow2Height <= 256) {
			this.bloomTextureA = null
		} else {
			var mipWidth: Int = pow2Width
			@Pc(75) var mipHeight: Int = pow2Height
			@Pc(77) var level: Int = 0
			label59@ while (true) {
				if (mipWidth <= 256 && mipHeight <= 256) {
					if (this.bloomTextureA == null) {
						this.bloomTextureA = GlFramebuffer(super.activeToolkit)
					}
					mipWidth = pow2Width
					this.mipmaps = arrayOfNulls<OpenGl2DTexture>(level)
					mipHeight = pow2Height
					level = 0
					while (true) {
						if (mipWidth <= 256 && mipHeight <= 256) {
							break@label59
						}
						this.mipmaps!![level++] = OpenGl2DTexture(super.activeToolkit, 3553, 34842, mipWidth, mipHeight)
						if (mipHeight > 256) {
							mipHeight = mipHeight shr 0x1
						}
						if (mipWidth > 256) {
							mipWidth = mipWidth shr 0x1
						}
					}
				}
				if (mipHeight > 256) {
					mipHeight = mipHeight shr 0x1
				}
				if (mipWidth > 256) {
					mipWidth = mipWidth shr 0x1
				}
				level++
			}
		}
		this.paddedWidth = pow2Width
		this.paddedHeight = pow2Height
	}

	@OriginalMember(owner = "client!rda", name = "b", descriptor = "(B)V")
	override fun destroy() {
		this.textureDisplay = null
		this.blurTextureA = null
		this.bloomTextureA = null
		this.bloomTextureB = null
		this.scenetexShader = null
		this.mipmaps = null
		this.mipmappedScenetexShader = null
		this.bloomScenetexShader = null
		this.blurTextureB = null
	}

	@OriginalMember(owner = "client!rda", name = "a", descriptor = "(Z)Z")
	override fun isLoaded(): Boolean {
		return this.bloomTextureB != null
	}

	@OriginalMember(owner = "client!rda", name = "e", descriptor = "(B)Z")
	fun isSupported(): Boolean {
		return super.activeToolkit!!.extFramebufferObject && super.activeToolkit!!.arbFragmentShader && super.activeToolkit!!.arbTextureFloat
	}

	@OriginalMember(owner = "client!rda", name = "b", descriptor = "(Z)I")
	override fun getFormat(): Int {
		return 1
	}

	@OriginalMember(owner = "client!rda", name = "b", descriptor = "(I)Z")
	override fun createShaders(): Boolean {
		if (!super.activeToolkit!!.extFramebufferObject || !super.activeToolkit!!.arbFragmentShader || !super.activeToolkit!!.arbTextureFloat) {
			return false
		}
		this.bloomTextureB = GlFramebuffer(super.activeToolkit)
		this.blurTextureA = OpenGl2DTexture(super.activeToolkit, 3553, 34842, 256, 256)
		this.blurTextureA!!.setRepeat(false, false)
		this.blurTextureB = OpenGl2DTexture(super.activeToolkit, 3553, 34842, 256, 256)
		this.blurTextureB!!.setRepeat(false, false)
		super.activeToolkit!!.pushTarget(this.bloomTextureB)
		this.bloomTextureB!!.attachTexture(this.blurTextureA, 0)
		this.bloomTextureB!!.attachTexture(this.blurTextureB, 1)
		this.bloomTextureB!!.drawBuffer(0)
		if (!this.bloomTextureB!!.isFramebufferComplete()) {
			super.activeToolkit!!.popTarget(this.bloomTextureB)
			return false
		}
		super.activeToolkit!!.popTarget(this.bloomTextureB)
		this.scenetexShader = GlProgram.link(super.activeToolkit, arrayOf<GlShader?>(GlShader.compile(35632, super.activeToolkit, "#extension GL_ARB_texture_rectangle : enable\nuniform vec3 params;\nuniform sampler2DRect sceneTex;\nconst vec3 lumCoef = vec3(0.2126, 0.7152, 0.0722);\nvoid main() {\n    vec4 col = texture2DRect(sceneTex, gl_TexCoord[0].xy);\n    gl_FragColor = col*step(params.x, dot(lumCoef, col.rgb));\n}\n")))
		this.mipmappedScenetexShader = GlProgram.link(super.activeToolkit, arrayOf<GlShader?>(GlShader.compile(35632, super.activeToolkit, "uniform vec3 params;\nuniform sampler2D sceneTex;\nconst vec3 lumCoef = vec3(0.2126, 0.7152, 0.0722);\nvoid main() {\n    vec4 col = texture2D(sceneTex, gl_TexCoord[0].xy);\n    gl_FragColor = col*step(params.x, dot(lumCoef, col.rgb));\n}\n")))
		this.bloomScenetexShader = GlProgram.link(super.activeToolkit, arrayOf<GlShader?>(GlShader.compile(35632, super.activeToolkit, "#extension GL_ARB_texture_rectangle : enable\nuniform vec3 params;\nuniform vec3 dimScale;\nuniform sampler2D bloomTex;\nuniform sampler2DRect sceneTex;\nconst vec3 lumCoef = vec3(0.2126, 0.7152, 0.0722);\nvoid main() {\n\t vec4 bloomCol = texture2D(bloomTex, gl_TexCoord[1].xy);\n\t vec4 sceneCol = texture2DRect(sceneTex, gl_TexCoord[0].xy);\n\t float preLum = 0.99*dot(lumCoef, sceneCol.rgb)+0.01;\n    float postLum = preLum*(1.0+(preLum/params.y))/(preLum+1.0);\n\t gl_FragColor = sceneCol*(postLum/preLum)+bloomCol*params.x;\n}\n")))
		this.textureDisplay = GlProgram.link(super.activeToolkit, arrayOf<GlShader?>(GlShader.compile(35632, super.activeToolkit, "uniform vec3 step;\nuniform sampler2D baseTex;\nvoid main() {\n\tvec4 fragCol = texture2D(baseTex, gl_TexCoord[0].xy)*0.091396265;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-1.0*step.xy))*0.088584304;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 1.0*step.xy))*0.088584304;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-2.0*step.xy))*0.08065692;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 2.0*step.xy))*0.08065692;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-3.0*step.xy))*0.068989515;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 3.0*step.xy))*0.068989515;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-4.0*step.xy))*0.055434637;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 4.0*step.xy))*0.055434637;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-5.0*step.xy))*0.04184426;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 5.0*step.xy))*0.04184426;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-6.0*step.xy))*0.029672023;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 6.0*step.xy))*0.029672023;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-7.0*step.xy))*0.019765828;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 7.0*step.xy))*0.019765828;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-8.0*step.xy))*0.012369139;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 8.0*step.xy))*0.012369139;\n\tgl_FragColor = fragCol;\n}\n")))
		return this.mipmappedScenetexShader != null && this.scenetexShader != null && this.bloomScenetexShader != null && this.textureDisplay != null
	}

	companion object {
		@OriginalMember(owner = "client!ada", name = "R", descriptor = "F")
		@JvmField
		var whitePoint: Float = 1.0f

		@OriginalMember(owner = "client!oi", name = "L", descriptor = "F")
		@JvmField
		var brightpass: Float = 0.25f

		@OriginalMember(owner = "client!fca", name = "u", descriptor = "F")
		@JvmField
		var bloom: Float = 1.0f
	}
}
