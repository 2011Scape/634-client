package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.graphics.texture.water.NativeWaterTexture
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.toolkit.D3DToolkit
import jagdx.IDirect3DDevice
import jagdx.IDirect3DVertexShader
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!gaa")
class D3DTransparentWaterPass : RenderPass {

	@OriginalMember(owner = "client!gaa", name = "j", descriptor = "Lclient!ur;")
	private val water: NativeWaterTexture?

	@OriginalMember(owner = "client!gaa", name = "k", descriptor = "Lclient!bfa;")
	private val d3DToolkit: D3DToolkit?

	@OriginalMember(owner = "client!gaa", name = "l", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private val vertexShader: IDirect3DVertexShader?

	@OriginalMember(owner = "client!gaa", name = "<init>", descriptor = "(Lclient!bfa;Lclient!in;Lclient!ur;)V")
	constructor(@OriginalArg(0) d3DToolkit: D3DToolkit?, @OriginalArg(1) resourceProvider: Js5ResourceProvider?, @OriginalArg(2) water: NativeWaterTexture?) : super(d3DToolkit) {
		this.water = water
		this.d3DToolkit = d3DToolkit
		if (resourceProvider != null && this.water!!.readyToSample() && (this.d3DToolkit!!.d3DCAPS!!.VertexShaderVersion and 0xFFFF) >= 257) {
			this.vertexShader = this.d3DToolkit.iDirect3DDevice!!.b(resourceProvider.getFile("dx", "transparent_water"))
		} else {
			this.vertexShader = null
		}
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(ILclient!du;I)V")
	override fun setBaseTexture(@OriginalArg(0) combineMode: Int, @OriginalArg(1) baseTexture: TextureSampler?) {
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
		this.nativeToolkit!!.setTextureCombineModes(TextureCombineFunction.aClass315_2, TextureCombineFunction.aClass315_5)
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(III)V")
	override fun setEffectParams(@OriginalArg(1) effectParam1: Int, @OriginalArg(2) effectParam2: Int) {
		@Pc(3) val device: IDirect3DDevice? = this.d3DToolkit!!.iDirect3DDevice
		if (this.water!!.hasDepth) {
			@Pc(16) val phase: Float = (this.nativeToolkit!!.animatingCycle % 4000).toFloat() / 4000.0f
			this.nativeToolkit!!.setTextureSampler(this.water.waterSampler3d)
			device!!.a(11, phase, 0.0f, 0.0f, 0.0f)
		} else {
			@Pc(42) val frame: Int = this.nativeToolkit!!.animatingCycle % 4000 * 16 / 4000
			this.nativeToolkit!!.setTextureSampler(this.water.waterTextureSamplers!![frame])
			device!!.a(11, 0.0f, 0.0f, 0.0f, 0.0f)
		}
	}

	@OriginalMember(owner = "client!gaa", name = "b", descriptor = "(B)V")
	override fun onModelMatrixChanged() {
		if (this.vertexShader != null) {
			@Pc(6) val device: IDirect3DDevice? = this.d3DToolkit!!.iDirect3DDevice
			@Pc(11) val worldViewProjection: NativeMatrix = this.d3DToolkit.calibrateViewPosition()
			device!!.a(0, worldViewProjection.copyRowMajorInto(D3DTransparentWaterMatrix.MATRIX_BUFFER))
		}
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(B)V")
	override fun disable() {
		this.d3DToolkit!!.setVertexShader(null)
		this.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
		this.nativeToolkit!!.setTextureRgbStageState(1, TextureCombineSource.PRIMARY_COLOR)
		this.nativeToolkit!!.setTextureRgbStageState(2, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit!!.setFogSupported(true)
	}

	@OriginalMember(owner = "client!gaa", name = "b", descriptor = "(I)V")
	override fun onProjectionChanged() {
		if (this.vertexShader != null) {
			@Pc(6) val device: IDirect3DDevice? = this.d3DToolkit!!.iDirect3DDevice
			device!!.a(4, this.nativeToolkit!!.getTransposedProjectionMatrix(D3DTransparentWaterMatrix.MATRIX_BUFFER))
		}
	}

	@OriginalMember(owner = "client!gaa", name = "f", descriptor = "(I)V")
	override fun onTextureMatrixChanged() {
		if (this.vertexShader == null) {
			return
		}
		@Pc(7) val device: IDirect3DDevice? = this.d3DToolkit!!.iDirect3DDevice
		@Pc(12) val textureMatrix: NativeMatrix? = this.nativeToolkit!!.getTextureMatrix()
		textureMatrix!!.copyXYInto(D3DTransparentWaterMatrix.MATRIX_BUFFER)
		D3DTransparentWaterMatrix.MATRIX_BUFFER[0] *= 0.25f
		D3DTransparentWaterMatrix.MATRIX_BUFFER[2] *= 0.25f
		D3DTransparentWaterMatrix.MATRIX_BUFFER[3] *= 0.25f
		D3DTransparentWaterMatrix.MATRIX_BUFFER[6] *= 0.25f
		D3DTransparentWaterMatrix.MATRIX_BUFFER[5] *= 0.25f
		D3DTransparentWaterMatrix.MATRIX_BUFFER[1] *= 0.25f
		D3DTransparentWaterMatrix.MATRIX_BUFFER[7] *= 0.25f
		D3DTransparentWaterMatrix.MATRIX_BUFFER[4] *= 0.25f
		device!!.SetVertexShaderConstantF(8, D3DTransparentWaterMatrix.MATRIX_BUFFER, 2)
	}

	@OriginalMember(owner = "client!gaa", name = "e", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return this.vertexShader != null
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(Z)V")
	override fun onCameraChanged() {
		if (this.vertexShader != null) {
			@Pc(7) val device: IDirect3DDevice? = this.d3DToolkit!!.iDirect3DDevice
			@Pc(12) val worldViewProjection: NativeMatrix = this.d3DToolkit.calibrateViewPosition()
			device!!.a(0, worldViewProjection.copyRowMajorInto(D3DTransparentWaterMatrix.MATRIX_BUFFER))
		}
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(I)V")
	override fun onFogChanged() {
		if (this.vertexShader == null) {
			return
		}
		@Pc(13) val device: IDirect3DDevice? = this.d3DToolkit!!.iDirect3DDevice
		if (this.nativeToolkit!!.fogDistance <= 0) {
			device!!.a(10, 0.0f, 0.0f, 0.0f, 0.0f)
		} else {
			@Pc(30) val fogEnd: Float = this.nativeToolkit!!.fogEnd
			@Pc(34) val fogStart: Float = this.nativeToolkit!!.fogStart
			@Pc(38) val underwaterStart: Float = fogStart - 512.0f
			device!!.a(10, underwaterStart, 1.0f / (fogStart - underwaterStart), fogStart, 1.0f / (fogEnd - fogStart))
		}
		this.nativeToolkit!!.setColourValue(this.nativeToolkit!!.fogColorRgb)
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(IZ)V")
	override fun enable(@OriginalArg(1) lit: Boolean) {
		this.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
		this.nativeToolkit!!.setTextureRgbStageState(1, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit!!.setTextureColourSource(false, 2, TextureCombineSource.PRIMARY_COLOR, true)
		this.nativeToolkit!!.setFogSupported(false)
		this.d3DToolkit!!.setVertexShader(this.vertexShader)
		this.onModelMatrixChanged()
		this.onTextureMatrixChanged()
		this.onProjectionChanged()
		this.onFogChanged()
	}
}
