package com.jagex.game.runetek5

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.graphics.GpuTexture
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.toolkit.GlxToolkit
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode

@OriginalClass("client!or")
class GlUnderwaterPass @OriginalMember(owner = "client!or", name = "<init>", descriptor = "(Lclient!bga;Lclient!in;)V") constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) resourceProvider: Js5ResourceProvider?) : RenderPass(glxToolkit) {

	@OriginalMember(owner = "client!or", name = "y", descriptor = "Z")
	private var programBound: Boolean = false

	@OriginalMember(owner = "client!or", name = "E", descriptor = "Z")
	private var lit: Boolean = false

	@OriginalMember(owner = "client!or", name = "r", descriptor = "Z")
	private var whiteTextureBound: Boolean = false

	@OriginalMember(owner = "client!or", name = "G", descriptor = "[F")
	private val waterPlane: FloatArray = FloatArray(4)

	@OriginalMember(owner = "client!or", name = "n", descriptor = "Lclient!qr;")
	private var groundUnlitProgram: GlxArbProgram? = null

	@OriginalMember(owner = "client!or", name = "k", descriptor = "Lclient!qr;")
	private var groundLitProgram: GlxArbProgram? = null

	@OriginalMember(owner = "client!or", name = "w", descriptor = "Lclient!qr;")
	private var modelUnlitProgram: GlxArbProgram? = null

	@OriginalMember(owner = "client!or", name = "z", descriptor = "Lclient!qr;")
	private var modelLitProgram: GlxArbProgram? = null

	@OriginalMember(owner = "client!or", name = "j", descriptor = "Z")
	private val supported: Boolean

	@OriginalMember(owner = "client!or", name = "i", descriptor = "Lclient!wfa;")
	private var depthRamp: GpuTexture? = null

	init {
		if (resourceProvider != null && glxToolkit!!.GLARBVertexProgram) {
			this.groundUnlitProgram = GlxArbProgram.createVertexProgram(resourceProvider.getFile("gl", "uw_ground_unlit"), glxToolkit)
			this.groundLitProgram = GlxArbProgram.createVertexProgram(resourceProvider.getFile("gl", "uw_ground_lit"), glxToolkit)
			this.modelUnlitProgram = GlxArbProgram.createVertexProgram(resourceProvider.getFile("gl", "uw_model_unlit"), glxToolkit)
			this.modelLitProgram = GlxArbProgram.createVertexProgram(resourceProvider.getFile("gl", "uw_model_lit"), glxToolkit)
			if ((this.groundUnlitProgram != null) and (this.groundLitProgram != null) and (this.modelUnlitProgram != null) and (this.modelLitProgram != null)) {
				this.depthRamp = super.nativeToolkit!!.createTexture(1, 2, false, intArrayOf(0, -1))
				this.depthRamp!!.setWarp(false, false)
				this.supported = true
			} else {
				this.supported = false
			}
		} else {
			this.supported = false
		}
	}

	@OriginalMember(owner = "client!or", name = "c", descriptor = "(I)V")
	override fun onUnderwaterSettingsChanged() {
		@Pc(13) val waterHeight: Int = super.nativeToolkit!!.getWaterHeight()
		@Pc(18) val view: NativeMatrix? = super.nativeToolkit!!.getFlippedViewMatrix()
		if (this.lit) {
			OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, if (waterHeight == Int.MAX_VALUE) this.groundLitProgram!!.programId else this.modelLitProgram!!.programId)
		} else {
			OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, if (waterHeight.inv() == Int.MIN_VALUE) this.groundUnlitProgram!!.programId else this.modelUnlitProgram!!.programId)
		}
		OpenGL.glEnable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB)
		this.programBound = true
		view!!.transformPlane(0.0f, this.waterPlane, -1.0f, 0.0f, waterHeight.toFloat())
		OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 1, this.waterPlane[0], this.waterPlane[1], this.waterPlane[2], this.waterPlane[3])
		this.onFogChanged()
	}

	@OriginalMember(owner = "client!or", name = "e", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return this.supported
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(B)V")
	override fun disable() {
		super.nativeToolkit!!.setTextureSlot(1)
		super.nativeToolkit!!.setTextureSampler(null)
		super.nativeToolkit!!.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE)
		super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
		super.nativeToolkit!!.setTextureRgbStageState(2, TextureCombineSource.CONSTANT_COLOR)
		super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
		super.nativeToolkit!!.setTextureSlot(0)
		if (this.whiteTextureBound) {
			super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
			super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
			this.whiteTextureBound = false
		}
		if (this.programBound) {
			OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0)
			OpenGL.glDisable(OpenGLConstants.GL_FRAGMENT_PROGRAM_ARB)
			OpenGL.glDisable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB)
			this.programBound = false
		}
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(III)V")
	override fun setEffectParams(@OriginalArg(1) effectParam1: Int, @OriginalArg(2) effectParam2: Int) {
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(ILclient!du;I)V")
	override fun setBaseTexture(@OriginalArg(0) colourType: Int, @OriginalArg(1) baseTexture: TextureSampler?) {
		if (baseTexture != null) {
			if (this.whiteTextureBound) {
				super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
				super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
				this.whiteTextureBound = false
			}
			super.nativeToolkit!!.setTextureSampler(baseTexture)
			super.nativeToolkit!!.setColourType(colourType)
		} else if (!this.whiteTextureBound) {
			super.nativeToolkit!!.setTextureSampler(super.nativeToolkit!!.defaultTexture)
			super.nativeToolkit!!.setColourType(1)
			super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS)
			super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.PREVIOUS)
			this.whiteTextureBound = true
		}
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(I)V")
	override fun onFogChanged() {
		if (!this.programBound) {
			return
		}
		@Pc(12) val far: Int = super.nativeToolkit!!.XA()
		@Pc(16) val near: Int = super.nativeToolkit!!.i()
		@Pc(27) val fogFar: Float = far.toFloat() - (far - near).toFloat() * 0.125f
		@Pc(39) val fogNear: Float = -((far - near).toFloat() * 0.25f) + far.toFloat()
		OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0, fogNear, fogFar, 1.0f / super.nativeToolkit!!.getWaterScale().toFloat(), super.nativeToolkit!!.getWaterIntensity().toFloat() / 255.0f)
		super.nativeToolkit!!.setTextureSlot(1)
		super.nativeToolkit!!.setColourValue(super.nativeToolkit!!.getWaterColour())
		super.nativeToolkit!!.setTextureSlot(0)
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(IZ)V")
	override fun enable(@OriginalArg(1) lit: Boolean) {
		this.lit = lit
		super.nativeToolkit!!.setTextureSlot(1)
		super.nativeToolkit!!.setTextureSampler(this.depthRamp)
		super.nativeToolkit!!.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_5)
		super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR)
		super.nativeToolkit!!.setTextureColourSource(true, 2, TextureCombineSource.TEXTURE, false)
		super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.PREVIOUS)
		super.nativeToolkit!!.setTextureSlot(0)
		this.onUnderwaterSettingsChanged()
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
	}
}
