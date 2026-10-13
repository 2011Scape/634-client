/*
 * Not part of the original client: 634HD (toolkit 6), copied from RectangularTextureGL (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import kotlin.jvm.JvmField

class HdRectangularTexture : HdPostProcessEffect {

	private var textureDisplay: HdProgram? = null

	private var mipmappedScenetexShader: HdProgram? = null

	private var bloomTextureB: HdFramebuffer? = null

	private var paddedWidth: Int = 0

	private var blurTextureB: Hd2DTexture? = null

	private var sceneHeight: Int = 0

	private var blurTextureA: Hd2DTexture? = null

	private var bloomScenetexShader: HdProgram? = null

	private var mipmaps: Array<Hd2DTexture?>? = null

	private var paddedHeight: Int = 0

	private var sceneWidth: Int = 0

	private var scenetexShader: HdProgram? = null

	private var bloomTextureA: HdFramebuffer? = null

	constructor(glToolkit: HdToolkit?) : super(glToolkit)

	override fun endPass(pass: Int) {
		HdGl.glUseProgramObjectARB(0L)
		super.activeToolkit!!.activateTexture(1)
		super.activeToolkit!!.setActiveTexture(null)
		super.activeToolkit!!.activateTexture(0)
	}

	override fun draw(depthTexture: Hd2DTexture?, colorTexture: Hd2DTexture?, pass: Int) {
		HdGl.glPushAttrib(OpenGLConstants.GL_VIEWPORT_BIT)
		HdGl.glMatrixMode(OpenGLConstants.GL_PROJECTION)
		HdGl.glPushMatrix()
		HdGl.glLoadIdentity()
		HdGl.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0)
		var program: Long
		if (this.mipmaps == null) {
			super.activeToolkit!!.setActiveTexture(colorTexture)
			super.activeToolkit!!.pushTarget(this.bloomTextureB)
			this.bloomTextureB!!.drawBuffer(0)
			HdGl.glViewport(0, 0, 256, 256)
			program = this.scenetexShader!!.programHandle
			HdGl.glUseProgramObjectARB(program)
			HdGl.glUniform1iARB(HdGl.glGetUniformLocationARB(program, "sceneTex"), 0)
			HdGl.glUniform3fARB(HdGl.glGetUniformLocationARB(program, "params"), HdRectangularTexture.whitePoint, 0.0f, 0.0f)
			HdGl.glBegin(OpenGLConstants.GL_QUADS)
			HdGl.glTexCoord2f(0.0f, 0.0f)
			HdGl.glVertex2i(0, 0)
			HdGl.glTexCoord2f(this.sceneWidth.toFloat(), 0.0f)
			HdGl.glVertex2i(1, 0)
			HdGl.glTexCoord2f(this.sceneWidth.toFloat(), this.sceneHeight.toFloat())
			HdGl.glVertex2i(1, 1)
			HdGl.glTexCoord2f(0.0f, this.sceneHeight.toFloat())
			HdGl.glVertex2i(0, 1)
			HdGl.glEnd()
		} else {
			super.activeToolkit!!.pushTarget(this.bloomTextureA)
			var width: Int = Utils.floorPowerOfTwo(this.sceneWidth)
			var height: Int = Utils.floorPowerOfTwo(this.sceneHeight)
			var level: Int = 0
			while (width > 256 || height > 256) {
				HdGl.glViewport(0, 0, width, height)
				this.bloomTextureA!!.attachTexture(this.mipmaps!![level], 0)
				if (level == 0) {
					super.activeToolkit!!.setActiveTexture(colorTexture)
					HdGl.glBegin(OpenGLConstants.GL_QUADS)
					HdGl.glTexCoord2f(0.0f, 0.0f)
					HdGl.glVertex2i(0, 0)
					HdGl.glTexCoord2f(this.sceneWidth.toFloat(), 0.0f)
					HdGl.glVertex2i(1, 0)
					HdGl.glTexCoord2f(this.sceneWidth.toFloat(), this.sceneHeight.toFloat())
					HdGl.glVertex2i(1, 1)
					HdGl.glTexCoord2f(0.0f, this.sceneHeight.toFloat())
					HdGl.glVertex2i(0, 1)
					HdGl.glEnd()
				} else {
					super.activeToolkit!!.setActiveTexture(this.mipmaps!![level - 1])
					HdGl.glBegin(OpenGLConstants.GL_QUADS)
					HdGl.glTexCoord2f(0.0f, 0.0f)
					HdGl.glVertex2i(0, 0)
					HdGl.glTexCoord2f(1.0f, 0.0f)
					HdGl.glVertex2i(1, 0)
					HdGl.glTexCoord2f(1.0f, 1.0f)
					HdGl.glVertex2i(1, 1)
					HdGl.glTexCoord2f(0.0f, 1.0f)
					HdGl.glVertex2i(0, 1)
					HdGl.glEnd()
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
			HdGl.glViewport(0, 0, 256, 256)
			val mipmapProgram: Long = this.mipmappedScenetexShader!!.programHandle
			HdGl.glUseProgramObjectARB(mipmapProgram)
			HdGl.glUniform1iARB(HdGl.glGetUniformLocationARB(mipmapProgram, "sceneTex"), 0)
			HdGl.glUniform3fARB(HdGl.glGetUniformLocationARB(mipmapProgram, "params"), HdRectangularTexture.whitePoint, 0.0f, 0.0f)
			HdGl.glBegin(OpenGLConstants.GL_QUADS)
			HdGl.glTexCoord2f(0.0f, 0.0f)
			HdGl.glVertex2i(0, 0)
			HdGl.glTexCoord2f(1.0f, 0.0f)
			HdGl.glVertex2i(1, 0)
			HdGl.glTexCoord2f(1.0f, 1.0f)
			HdGl.glVertex2i(1, 1)
			HdGl.glTexCoord2f(0.0f, 1.0f)
			HdGl.glVertex2i(0, 1)
			HdGl.glEnd()
		}
		this.bloomTextureB!!.drawBuffer(1)
		super.activeToolkit!!.setActiveTexture(this.blurTextureA)
		program = this.textureDisplay!!.programHandle
		HdGl.glUseProgramObjectARB(program)
		HdGl.glUniform1iARB(HdGl.glGetUniformLocationARB(program, "baseTex"), 0)
		HdGl.glUniform3fARB(HdGl.glGetUniformLocationARB(program, "step"), 0.00390625f, 0.0f, 0.0f)
		HdGl.glBegin(OpenGLConstants.GL_QUADS)
		HdGl.glTexCoord2f(0.0f, 0.0f)
		HdGl.glVertex2i(0, 0)
		HdGl.glTexCoord2f(1.0f, 0.0f)
		HdGl.glVertex2i(1, 0)
		HdGl.glTexCoord2f(1.0f, 1.0f)
		HdGl.glVertex2i(1, 1)
		HdGl.glTexCoord2f(0.0f, 1.0f)
		HdGl.glVertex2i(0, 1)
		HdGl.glEnd()
		this.bloomTextureB!!.drawBuffer(0)
		super.activeToolkit!!.setActiveTexture(this.blurTextureB)
		HdGl.glUniform3fARB(HdGl.glGetUniformLocationARB(program, "step"), 0.0f, 0.00390625f, 0.0f)
		HdGl.glBegin(OpenGLConstants.GL_QUADS)
		HdGl.glTexCoord2f(0.0f, 0.0f)
		HdGl.glVertex2i(0, 0)
		HdGl.glTexCoord2f(1.0f, 0.0f)
		HdGl.glVertex2i(1, 0)
		HdGl.glTexCoord2f(1.0f, 1.0f)
		HdGl.glVertex2i(1, 1)
		HdGl.glTexCoord2f(0.0f, 1.0f)
		HdGl.glVertex2i(0, 1)
		HdGl.glEnd()
		HdGl.glPopAttrib()
		HdGl.glPopMatrix()
		HdGl.glMatrixMode(OpenGLConstants.GL_MODELVIEW)
		super.activeToolkit!!.popTarget(this.bloomTextureB)
		val bloomProgram: Long = this.bloomScenetexShader!!.programHandle
		HdGl.glUseProgramObjectARB(bloomProgram)
		HdGl.glUniform1iARB(HdGl.glGetUniformLocationARB(bloomProgram, "sceneTex"), 0)
		HdGl.glUniform1iARB(HdGl.glGetUniformLocationARB(bloomProgram, "bloomTex"), 1)
		HdGl.glUniform3fARB(HdGl.glGetUniformLocationARB(bloomProgram, "params"), HdRectangularTexture.brightpass, HdRectangularTexture.bloom, 0.0f)
		super.activeToolkit!!.activateTexture(1)
		super.activeToolkit!!.setActiveTexture(this.blurTextureA)
		super.activeToolkit!!.activateTexture(0)
		super.activeToolkit!!.setActiveTexture(colorTexture)
	}

	override fun resize(height: Int, width: Int) {
		this.sceneHeight = height
		this.sceneWidth = width
		val pow2Width: Int = Utils.floorPowerOfTwo(this.sceneWidth)
		val pow2Height: Int = Utils.floorPowerOfTwo(this.sceneHeight)
		if (pow2Width == this.paddedWidth && this.paddedHeight == pow2Height) {
			return
		}
		var local42: Int
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
			var mipHeight: Int = pow2Height
			var level: Int = 0
			label59@ while (true) {
				if (mipWidth <= 256 && mipHeight <= 256) {
					if (this.bloomTextureA == null) {
						this.bloomTextureA = HdFramebuffer(super.activeToolkit)
					}
					mipWidth = pow2Width
					this.mipmaps = arrayOfNulls<Hd2DTexture>(level)
					mipHeight = pow2Height
					level = 0
					while (true) {
						if (mipWidth <= 256 && mipHeight <= 256) {
							break@label59
						}
						this.mipmaps!![level++] = Hd2DTexture(super.activeToolkit, 3553, 34842, mipWidth, mipHeight)
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

	override fun isLoaded(): Boolean {
		return this.bloomTextureB != null
	}

	fun isSupported(): Boolean {
		return super.activeToolkit!!.extFramebufferObject && super.activeToolkit!!.arbFragmentShader && super.activeToolkit!!.arbTextureFloat
	}

	override fun getFormat(): Int {
		return 1
	}

	override fun createShaders(): Boolean {
		if (!super.activeToolkit!!.extFramebufferObject || !super.activeToolkit!!.arbFragmentShader || !super.activeToolkit!!.arbTextureFloat) {
			return false
		}
		this.bloomTextureB = HdFramebuffer(super.activeToolkit)
		this.blurTextureA = Hd2DTexture(super.activeToolkit, 3553, 34842, 256, 256)
		this.blurTextureA!!.setRepeat(false, false)
		this.blurTextureB = Hd2DTexture(super.activeToolkit, 3553, 34842, 256, 256)
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
		this.scenetexShader = HdProgram.link(super.activeToolkit, arrayOf<HdShader?>(HdShader.compile(35632, super.activeToolkit, "#extension GL_ARB_texture_rectangle : enable\nuniform vec3 params;\nuniform sampler2DRect sceneTex;\nconst vec3 lumCoef = vec3(0.2126, 0.7152, 0.0722);\nvoid main() {\n    vec4 col = texture2DRect(sceneTex, gl_TexCoord[0].xy);\n    gl_FragColor = col*step(params.x, dot(lumCoef, col.rgb));\n}\n")))
		this.mipmappedScenetexShader = HdProgram.link(super.activeToolkit, arrayOf<HdShader?>(HdShader.compile(35632, super.activeToolkit, "uniform vec3 params;\nuniform sampler2D sceneTex;\nconst vec3 lumCoef = vec3(0.2126, 0.7152, 0.0722);\nvoid main() {\n    vec4 col = texture2D(sceneTex, gl_TexCoord[0].xy);\n    gl_FragColor = col*step(params.x, dot(lumCoef, col.rgb));\n}\n")))
		this.bloomScenetexShader = HdProgram.link(super.activeToolkit, arrayOf<HdShader?>(HdShader.compile(35632, super.activeToolkit, "#extension GL_ARB_texture_rectangle : enable\nuniform vec3 params;\nuniform vec3 dimScale;\nuniform sampler2D bloomTex;\nuniform sampler2DRect sceneTex;\nconst vec3 lumCoef = vec3(0.2126, 0.7152, 0.0722);\nvoid main() {\n\t vec4 bloomCol = texture2D(bloomTex, gl_TexCoord[1].xy);\n\t vec4 sceneCol = texture2DRect(sceneTex, gl_TexCoord[0].xy);\n\t float preLum = 0.99*dot(lumCoef, sceneCol.rgb)+0.01;\n    float postLum = preLum*(1.0+(preLum/params.y))/(preLum+1.0);\n\t gl_FragColor = sceneCol*(postLum/preLum)+bloomCol*params.x;\n}\n")))
		this.textureDisplay = HdProgram.link(super.activeToolkit, arrayOf<HdShader?>(HdShader.compile(35632, super.activeToolkit, "uniform vec3 step;\nuniform sampler2D baseTex;\nvoid main() {\n\tvec4 fragCol = texture2D(baseTex, gl_TexCoord[0].xy)*0.091396265;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-1.0*step.xy))*0.088584304;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 1.0*step.xy))*0.088584304;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-2.0*step.xy))*0.08065692;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 2.0*step.xy))*0.08065692;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-3.0*step.xy))*0.068989515;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 3.0*step.xy))*0.068989515;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-4.0*step.xy))*0.055434637;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 4.0*step.xy))*0.055434637;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-5.0*step.xy))*0.04184426;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 5.0*step.xy))*0.04184426;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-6.0*step.xy))*0.029672023;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 6.0*step.xy))*0.029672023;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-7.0*step.xy))*0.019765828;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 7.0*step.xy))*0.019765828;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+(-8.0*step.xy))*0.012369139;\n\tfragCol += texture2D(baseTex, gl_TexCoord[0].xy+( 8.0*step.xy))*0.012369139;\n\tgl_FragColor = fragCol;\n}\n")))
		return this.mipmappedScenetexShader != null && this.scenetexShader != null && this.bloomScenetexShader != null && this.textureDisplay != null
	}

	companion object {
		@JvmField
		var whitePoint: Float = 1.0f

		@JvmField
		var brightpass: Float = 0.25f

		@JvmField
		var bloom: Float = 1.0f
	}
}
