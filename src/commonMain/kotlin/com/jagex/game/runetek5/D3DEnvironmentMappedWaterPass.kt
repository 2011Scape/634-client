package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.graphics.texture.water.NativeWaterTexture
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.toolkit.D3DToolkit
import jagdx.IDirect3DDevice
import jagdx.IDirect3DPixelShader
import jagdx.IDirect3DVertexShader
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!gg")
class D3DEnvironmentMappedWaterPass : RenderPass {

	@OriginalMember(owner = "client!gg", name = "k", descriptor = "Z")
	private var active: Boolean = false

	@OriginalMember(owner = "client!gg", name = "o", descriptor = "Lclient!ur;")
	private val water: NativeWaterTexture?

	@OriginalMember(owner = "client!gg", name = "m", descriptor = "Lclient!bfa;")
	private val d3dToolkit: D3DToolkit?

	@OriginalMember(owner = "client!gg", name = "l", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private val vertexShader: IDirect3DVertexShader?

	@OriginalMember(owner = "client!gg", name = "n", descriptor = "Z")
	private val supported: Boolean

	@OriginalMember(owner = "client!gg", name = "j", descriptor = "Lclient!jagdx/IDirect3DPixelShader;")
	private val pixelShader: IDirect3DPixelShader?

	@OriginalMember(owner = "client!gg", name = "<init>", descriptor = "(Lclient!bfa;Lclient!in;Lclient!ur;)V")
	constructor(@OriginalArg(0) d3DToolkit: D3DToolkit?, @OriginalArg(1) resourceProvider: Js5ResourceProvider?, @OriginalArg(2) water: NativeWaterTexture?) : super(d3DToolkit) {
		this.water = water
		this.d3dToolkit = d3DToolkit
		if (resourceProvider != null && this.nativeToolkit!!.cubeMapSupported && this.nativeToolkit!!.SUPPORTS_TEXTURE3D && (this.d3dToolkit!!.d3DCAPS!!.VertexShaderVersion and 0xFFFF) >= 257) {
			this.vertexShader = this.d3dToolkit.iDirect3DDevice!!.b(resourceProvider.getFile("dx", "environment_mapped_water_v"))
			this.pixelShader = this.d3dToolkit.iDirect3DDevice!!.a(resourceProvider.getFile("dx", "environment_mapped_water_f"))
			this.supported = this.vertexShader != null && this.pixelShader != null && this.water!!.loadNormalMap()
		} else {
			this.vertexShader = null
			this.supported = false
			this.pixelShader = null
		}
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(III)V")
	override fun setEffectParams(@OriginalArg(1) effectParam1: Int, @OriginalArg(2) effectParam2: Int) {
		if (!this.active) {
			return
		}
		@Pc(15) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
		@Pc(21) val waveSpeed: Int = 0x1 shl (effectParam1 and 0x3)
		@Pc(32) val waveScale: Float = (0x1 shl ((effectParam1 shr 3) and 0x7)).toFloat() / 32.0f
		@Pc(36) val breakDepth: Int = effectParam2 and 0xFFFF
		@Pc(45) val breakOffset: Float = ((effectParam2 shr 16) and 0x3).toFloat() / 8.0f
		device!!.a(14, (this.nativeToolkit!!.animatingCycle * waveSpeed % 40000).toFloat() / 40000.0f, 0.0f, 0.0f, 0.0f)
		device.a(15, waveScale, 0.0f, 0.0f, 0.0f)
		device.b(4, breakDepth.toFloat(), 0.0f, 0.0f, 0.0f)
		device.b(5, breakOffset, 0.0f, 0.0f, 0.0f)
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
	}

	@OriginalMember(owner = "client!gg", name = "f", descriptor = "(I)V")
	override fun onTextureMatrixChanged() {
		if (this.active) {
			@Pc(6) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
			@Pc(13) val textureMatrix: NativeMatrix? = this.nativeToolkit!!.getTextureMatrix()
			device!!.SetVertexShaderConstantF(12, textureMatrix!!.copyXYInto(D3DEnvironmentMappedWaterMatrix.MATRIX_BUFFER), 2)
		}
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(ILclient!du;I)V")
	override fun setBaseTexture(@OriginalArg(0) combineMode: Int, @OriginalArg(1) baseTexture: TextureSampler?) {
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(I)V")
	override fun onFogChanged() {
		if (!this.active) {
			return
		}
		@Pc(6) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
		if (this.nativeToolkit!!.fogDistance <= 0) {
			device!!.a(16, 0.0f, 0.0f, 0.0f, 0.0f)
		} else {
			@Pc(23) val fogEnd: Float = this.nativeToolkit!!.fogEnd
			@Pc(27) val fogStart: Float = this.nativeToolkit!!.fogStart
			device!!.a(16, fogEnd, 1.0f / (fogEnd - fogStart), 0.0f, 0.0f)
		}
		device.b(0, ((this.nativeToolkit!!.fogColorRgb shr 16) and 0xFF).toFloat() / 255.0f, ((this.nativeToolkit!!.fogColorRgb shr 8) and 0xFF).toFloat() / 255.0f, (this.nativeToolkit!!.fogColorRgb and 0xFF).toFloat() / 255.0f, 0.0f)
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(IZ)V")
	override fun enable(@OriginalArg(1) lit: Boolean) {
		@Pc(8) val envMap: CubeTextureSampler? = this.nativeToolkit!!.getEnvironmentMapSampler()
		if (!this.supported || envMap == null) {
			return
		}
		@Pc(18) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
		this.d3dToolkit.setVertexShader(this.vertexShader)
		this.d3dToolkit.setPixelShader(this.pixelShader)
		this.nativeToolkit!!.setTextureSlot(1)
		this.nativeToolkit!!.setTextureSampler(envMap)
		this.nativeToolkit!!.setTextureSlot(0)
		this.nativeToolkit!!.setTextureSampler(this.water!!.normalSampler)
		this.active = true
		this.onCameraChanged()
		this.onTextureMatrixChanged()
		this.onProjectionChanged()
		this.onFogChanged()
		device!!.b(1, -this.nativeToolkit!!.sunPosition[0], -this.nativeToolkit!!.sunPosition[1], -this.nativeToolkit!!.sunPosition[2], 0.0f)
		device.b(2, this.nativeToolkit!!.baseLightColorRed, this.nativeToolkit!!.baseLightColorGreen, this.nativeToolkit!!.baseLightColorBlue, 1.0f)
		device.b(3, kotlin.math.abs(this.nativeToolkit!!.sunPosition[1]) * 928.0f + 96.0f, 0.0f, 0.0f, 0.0f)
	}

	@OriginalMember(owner = "client!gg", name = "e", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return this.supported
	}

	@OriginalMember(owner = "client!gg", name = "b", descriptor = "(B)V")
	override fun onModelMatrixChanged() {
		if (this.active) {
			@Pc(7) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
			@Pc(12) val worldViewProjection: NativeMatrix = this.d3dToolkit.calibrateViewPosition()
			device!!.a(0, worldViewProjection.copyRowMajorInto(D3DEnvironmentMappedWaterMatrix.MATRIX_BUFFER))
		}
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(B)V")
	override fun disable() {
		if (!this.active) {
			return
		}
		this.d3dToolkit!!.setVertexShader(null)
		this.d3dToolkit.setPixelShader(null)
		this.nativeToolkit!!.setTextureSlot(1)
		this.nativeToolkit!!.setTextureSampler(null)
		this.nativeToolkit!!.setTextureSlot(0)
		this.nativeToolkit!!.setTextureSampler(null)
		this.active = false
	}

	@OriginalMember(owner = "client!gg", name = "b", descriptor = "(I)V")
	override fun onProjectionChanged() {
		if (this.active) {
			@Pc(10) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
			device!!.a(8, this.nativeToolkit!!.getTransposedProjectionMatrix(D3DEnvironmentMappedWaterMatrix.MATRIX_BUFFER))
		}
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(Z)V")
	override fun onCameraChanged() {
		if (!this.active) {
			return
		}
		@Pc(10) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
		@Pc(15) val view: NativeMatrix = this.d3dToolkit.getFlippedViewMatrix()
		@Pc(20) val worldViewProjection: NativeMatrix = this.d3dToolkit.calibrateViewPosition()
		device!!.a(0, worldViewProjection.copyRowMajorInto(D3DEnvironmentMappedWaterMatrix.MATRIX_BUFFER))
		device.a(4, view.copyRotationRowMajorInto(D3DEnvironmentMappedWaterMatrix.MATRIX_BUFFER))
	}
}
