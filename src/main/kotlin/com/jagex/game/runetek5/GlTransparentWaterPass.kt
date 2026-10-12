package com.jagex.game.runetek5

import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.texture.water.NativeWaterTexture
import com.jagex.game.runetek5.toolkit.GlxToolkit

@OriginalClass("client!ov")
class GlTransparentWaterPass @OriginalMember(owner = "client!ov", name = "<init>", descriptor = "(Lclient!bga;Lclient!in;Lclient!ur;)V") constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) resourceProvider: Js5ResourceProvider?, @OriginalArg(2) water: NativeWaterTexture?) : RenderPass(glxToolkit) {

	@OriginalMember(owner = "client!ov", name = "s", descriptor = "Lclient!bga;")
	private val glxToolkit: GlxToolkit?

	@OriginalMember(owner = "client!ov", name = "q", descriptor = "Lclient!ur;")
	private val water: NativeWaterTexture?

	@OriginalMember(owner = "client!ov", name = "p", descriptor = "Lclient!qr;")
	private val vertexProgram: GlxArbProgram?

	init {
		this.glxToolkit = glxToolkit
		this.water = water
		if (resourceProvider != null && this.water!!.readyToSample() && this.glxToolkit!!.GLARBVertexProgram) {
			this.vertexProgram = GlxArbProgram.createVertexProgram(resourceProvider.getFile("gl", "transparent_water"), this.glxToolkit)
		} else {
			this.vertexProgram = null
		}
	}

	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(ILclient!du;I)V")
	override fun setBaseTexture(@OriginalArg(0) combineMode: Int, @OriginalArg(1) baseTexture: TextureSampler?) {
	}

	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(IZ)V")
	override fun enable(@OriginalArg(1) lit: Boolean) {
		OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, this.vertexProgram!!.programId)
		OpenGL.glEnable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB)
		super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS)
	}

	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
		super.nativeToolkit!!.setTextureCombineModes(TextureCombineFunction.aClass315_2, TextureCombineFunction.aClass315_4)
	}

	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(B)V")
	override fun disable() {
		super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
		OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0)
		OpenGL.glDisable(OpenGLConstants.GL_FRAGMENT_PROGRAM_ARB)
		OpenGL.glDisable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB)
	}

	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(III)V")
	override fun setEffectParams(@OriginalArg(1) effectParam1: Int, @OriginalArg(2) effectParam2: Int) {
		if (this.water!!.hasDepth) {
			@Pc(47) val phase: Float = (super.nativeToolkit!!.animatingCycle % 4000).toFloat() / 4000.0f
			super.nativeToolkit!!.setTextureSampler(this.water.waterSampler3d)
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0, phase, 0.0f, 0.0f, 1.0f)
		} else {
			@Pc(21) val frame: Int = super.nativeToolkit!!.animatingCycle % 4000 * 16 / 4000
			super.nativeToolkit!!.setTextureSampler(this.water.waterTextureSamplers!![frame])
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0, 0.0f, 0.0f, 0.0f, 1.0f)
		}
	}

	@OriginalMember(owner = "client!ov", name = "e", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return this.vertexProgram != null
	}
}
