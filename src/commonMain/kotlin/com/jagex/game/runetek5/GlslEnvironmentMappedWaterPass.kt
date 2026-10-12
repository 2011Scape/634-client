package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.texture.water.NativeWaterTexture
import com.jagex.game.runetek5.toolkit.GlxToolkit

@OriginalClass("client!ko")
class GlslEnvironmentMappedWaterPass @OriginalMember(owner = "client!ko", name = "<init>", descriptor = "(Lclient!bga;Lclient!in;Lclient!ur;)V") constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) resourceProvider: Js5ResourceProvider?, @OriginalArg(2) water: NativeWaterTexture?) : RenderPass(glxToolkit) {

	@OriginalMember(owner = "client!ko", name = "p", descriptor = "Z")
	private var active: Boolean = false

	@OriginalMember(owner = "client!ko", name = "r", descriptor = "Lclient!ur;")
	private val water: NativeWaterTexture?

	@OriginalMember(owner = "client!ko", name = "l", descriptor = "Lclient!lha;")
	private var program: GlxProgram? = null

	@OriginalMember(owner = "client!ko", name = "s", descriptor = "Z")
	private val supported: Boolean

	init {
		this.water = water
		if (resourceProvider != null && glxToolkit!!.GLARBVertexShader && glxToolkit.GLARBFragmentShader) {
			@Pc(23) val vertexShader: GlxShader? = GlxShader.compile(glxToolkit, resourceProvider.getFile("gl", "environment_mapped_water_v"), 35633)
			@Pc(33) val fragmentShader: GlxShader? = GlxShader.compile(glxToolkit, resourceProvider.getFile("gl", "environment_mapped_water_f"), 35632)
			this.program = GlxProgram.create(glxToolkit, arrayOf<GlxShader?>(vertexShader, fragmentShader))
			this.supported = this.program != null && this.water!!.loadNormalMap()
		} else {
			this.supported = false
		}
	}

	@OriginalMember(owner = "client!ko", name = "a", descriptor = "(III)V")
	override fun setEffectParams(@OriginalArg(1) effectParam1: Int, @OriginalArg(2) effectParam2: Int) {
		if (!this.active) {
			return
		}
		@Pc(12) val waveSpeed: Int = 0x1 shl (effectParam1 and 0x3)
		@Pc(23) val waveScale: Float = (0x1 shl ((effectParam1 shr 3) and 0x7)).toFloat() / 32.0f
		@Pc(27) val breakDepth: Int = effectParam2 and 0xFFFF
		@Pc(36) val breakOffset: Float = ((effectParam2 shr 16) and 0x3).toFloat() / 8.0f
		@Pc(40) val handle: Long = this.program!!.handle
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "time"), (super.nativeToolkit!!.animatingCycle * waveSpeed % 40000).toFloat() / 40000.0f)
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "scale"), waveScale)
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "breakWaterDepth"), breakDepth.toFloat())
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "breakWaterOffset"), breakOffset)
	}

	@OriginalMember(owner = "client!ko", name = "a", descriptor = "(B)V")
	override fun disable() {
		if (!this.active) {
			return
		}
		super.nativeToolkit!!.setTextureSlot(1)
		super.nativeToolkit!!.setTextureSampler(null)
		super.nativeToolkit!!.setTextureSlot(0)
		super.nativeToolkit!!.setTextureSampler(null)
		OpenGL.glUseProgramObjectARB(0L)
		this.active = false
	}

	@OriginalMember(owner = "client!ko", name = "e", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return this.supported
	}

	@OriginalMember(owner = "client!ko", name = "a", descriptor = "(ILclient!du;I)V")
	override fun setBaseTexture(@OriginalArg(0) colourType: Int, @OriginalArg(1) baseTexture: TextureSampler?) {
		if (!this.active) {
			super.nativeToolkit!!.setTextureSampler(baseTexture)
			super.nativeToolkit!!.setColourType(colourType)
		}
	}

	@OriginalMember(owner = "client!ko", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
	}

	@OriginalMember(owner = "client!ko", name = "a", descriptor = "(IZ)V")
	override fun enable(@OriginalArg(1) lit: Boolean) {
		@Pc(17) val envMap: CubeTextureSampler? = super.nativeToolkit!!.getEnvironmentMapSampler()
		if (!this.supported || envMap == null) {
			return
		}
		super.nativeToolkit!!.setTextureSlot(1)
		super.nativeToolkit!!.setTextureSampler(envMap)
		super.nativeToolkit!!.setTextureSlot(0)
		super.nativeToolkit!!.setTextureSampler(this.water!!.normalSampler)
		@Pc(50) val handle: Long = this.program!!.handle
		OpenGL.glUseProgramObjectARB(handle)
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(handle, "normalSampler"), 0)
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(handle, "envMapSampler"), 1)
		OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(handle, "sunDir"), -super.nativeToolkit!!.sunPosition[0], -super.nativeToolkit!!.sunPosition[1], -super.nativeToolkit!!.sunPosition[2])
		OpenGL.glUniform4fARB(OpenGL.glGetUniformLocationARB(handle, "sunColour"), super.nativeToolkit!!.baseLightColorRed, super.nativeToolkit!!.baseLightColorGreen, super.nativeToolkit!!.baseLightColorBlue, 1.0f)
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "sunExponent"), kotlin.math.abs(super.nativeToolkit!!.sunPosition[1]) * 928.0f + 96.0f)
		this.active = true
	}
}
