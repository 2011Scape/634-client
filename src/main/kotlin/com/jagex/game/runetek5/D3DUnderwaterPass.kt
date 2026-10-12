package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.GpuTexture
import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.toolkit.D3DToolkit
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode
import jagdx.IDirect3DDevice
import jagdx.IDirect3DVertexShader
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!fl")
class D3DUnderwaterPass : RenderPass {

	@OriginalMember(owner = "client!fl", name = "p", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private var boundShader: IDirect3DVertexShader? = null

	@OriginalMember(owner = "client!fl", name = "s", descriptor = "Z")
	private var lit: Boolean = false

	@OriginalMember(owner = "client!fl", name = "i", descriptor = "Z")
	private var whiteTextureBound: Boolean = false

	@OriginalMember(owner = "client!fl", name = "k", descriptor = "Lclient!bfa;")
	private val d3dToolkit: D3DToolkit?

	@OriginalMember(owner = "client!fl", name = "l", descriptor = "Z")
	private val supported: Boolean

	@OriginalMember(owner = "client!fl", name = "o", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private var modelUnlitShader: IDirect3DVertexShader? = null

	@OriginalMember(owner = "client!fl", name = "m", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private var groundLitShader: IDirect3DVertexShader? = null

	@OriginalMember(owner = "client!fl", name = "q", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private var unlitModelShader: IDirect3DVertexShader? = null

	@OriginalMember(owner = "client!fl", name = "r", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private var modelLitShader: IDirect3DVertexShader? = null

	@OriginalMember(owner = "client!fl", name = "n", descriptor = "Lclient!wfa;")
	private var depthRamp: GpuTexture? = null

	@OriginalMember(owner = "client!fl", name = "<init>", descriptor = "(Lclient!bfa;Lclient!in;)V")
	constructor(@OriginalArg(0) d3DToolkit: D3DToolkit?, @OriginalArg(1) resourceProvider: Js5ResourceProvider?) : super(d3DToolkit) {
		this.d3dToolkit = d3DToolkit
		if (resourceProvider == null || (this.d3dToolkit!!.d3DCAPS!!.VertexShaderVersion and 0xFFFF) < 257) {
			this.supported = false
		} else {
			this.modelUnlitShader = this.d3dToolkit.iDirect3DDevice!!.b(resourceProvider.getFile("dx", "uw_ground_unlit"))
			this.groundLitShader = this.d3dToolkit.iDirect3DDevice!!.b(resourceProvider.getFile("dx", "uw_ground_lit"))
			this.unlitModelShader = this.d3dToolkit.iDirect3DDevice!!.b(resourceProvider.getFile("dx", "uw_model_unlit"))
			this.modelLitShader = this.d3dToolkit.iDirect3DDevice!!.b(resourceProvider.getFile("dx", "uw_model_lit"))
			if ((this.modelUnlitShader != null) and (this.groundLitShader != null) and (this.unlitModelShader != null) and (this.modelLitShader != null)) {
				this.depthRamp = this.nativeToolkit!!.createTexture(1, 2, false, intArrayOf(0, -1))
				this.depthRamp!!.setWarp(false, false)
				this.supported = true
			} else {
				this.supported = false
			}
		}
	}

	@OriginalMember(owner = "client!fl", name = "c", descriptor = "(I)V")
	override fun onUnderwaterSettingsChanged() {
		@Pc(9) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
		@Pc(14) val waterHeight: Int = this.nativeToolkit!!.getWaterHeight()
		@Pc(19) val view: NativeMatrix = this.nativeToolkit!!.getFlippedViewMatrix()
		@Pc(33) val shader: IDirect3DVertexShader?
		if (this.lit) {
			shader = if (waterHeight.inv() == Int.MIN_VALUE) this.groundLitShader else this.modelLitShader
		} else {
			shader = if (waterHeight.inv() == Int.MIN_VALUE) this.modelUnlitShader else this.unlitModelShader
		}
		if (this.boundShader !== shader) {
			this.boundShader = shader
			this.d3dToolkit.setVertexShader(shader)
			this.uploadLighting()
			this.onProjectionChanged()
			this.onTextureMatrixChanged()
			this.onModelMatrixChanged()
			this.onCameraChanged()
			this.onFogChanged()
		}
		view.transformPlane(0.0f, D3DUnderwaterPassConstants.VECTOR_BUFFER, -1.0f, 0.0f, waterHeight.toFloat())
		device!!.a(12, D3DUnderwaterPassConstants.VECTOR_BUFFER)
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(III)V")
	override fun setEffectParams(@OriginalArg(1) effectParam1: Int, @OriginalArg(2) effectParam2: Int) {
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(B)V")
	override fun disable() {
		this.nativeToolkit!!.setTextureSlot(1)
		this.nativeToolkit!!.setTextureSampler(null)
		this.nativeToolkit!!.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE)
		this.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
		this.nativeToolkit!!.setTextureRgbStageState(2, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
		this.nativeToolkit!!.setTextureSlot(0)
		if (this.whiteTextureBound) {
			this.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
			this.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
			this.whiteTextureBound = false
		}
		if (this.boundShader != null) {
			this.d3dToolkit!!.setVertexShader(null)
			this.boundShader = null
		}
	}

	@OriginalMember(owner = "client!fl", name = "e", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return this.supported
	}

	@OriginalMember(owner = "client!fl", name = "b", descriptor = "(B)V")
	override fun onModelMatrixChanged() {
		if (this.boundShader != null) {
			@Pc(15) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
			@Pc(22) val worldViewProjection: NativeMatrix = this.d3dToolkit.calibrateViewPosition()
			device!!.a(0, worldViewProjection.copyRowMajorInto(D3DUnderwaterPassConstants.MATRIX_BUFFER))
		}
	}

	@OriginalMember(owner = "client!fl", name = "b", descriptor = "(I)V")
	override fun onProjectionChanged() {
		if (this.boundShader != null) {
			@Pc(11) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
			device!!.a(4, this.nativeToolkit!!.getTransposedProjectionMatrix(D3DUnderwaterPassConstants.MATRIX_BUFFER))
		}
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(ILclient!du;I)V")
	override fun setBaseTexture(@OriginalArg(0) colourType: Int, @OriginalArg(1) baseTexture: TextureSampler?) {
		if (baseTexture != null) {
			if (this.whiteTextureBound) {
				this.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
				this.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
				this.whiteTextureBound = false
			}
			this.nativeToolkit!!.setTextureSampler(baseTexture)
			this.nativeToolkit!!.setColourType(colourType)
		} else if (!this.whiteTextureBound) {
			this.nativeToolkit!!.setTextureSampler(this.nativeToolkit!!.defaultTexture)
			this.nativeToolkit!!.setColourType(1)
			this.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS)
			this.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.PREVIOUS)
			this.whiteTextureBound = true
		}
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(I)V")
	override fun onFogChanged() {
		if (this.boundShader == null) {
			return
		}
		@Pc(14) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
		@Pc(18) val far: Int = this.nativeToolkit!!.XA()
		@Pc(22) val near: Int = this.nativeToolkit!!.i()
		@Pc(34) val fogFar: Float = -((far - near).toFloat() * 0.125f) + far.toFloat()
		@Pc(45) val fogNear: Float = far.toFloat() - (far - near).toFloat() * 0.25f
		device!!.a(10, fogNear, 1.0f / (fogFar - fogNear), fogFar, 1.0f / (far.toFloat() - fogFar))
		device.a(11, 1.0f / this.nativeToolkit!!.getWaterScale().toFloat(), this.nativeToolkit!!.getWaterIntensity().toFloat() / 255.0f, this.nativeToolkit!!.fogEnd, 1.0f / (this.nativeToolkit!!.fogEnd - this.nativeToolkit!!.fogStart))
		this.nativeToolkit!!.setColourValue(this.nativeToolkit!!.getWaterColour())
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(IZ)V")
	override fun enable(@OriginalArg(1) lit: Boolean) {
		this.lit = lit
		this.nativeToolkit!!.setTextureSlot(1)
		this.nativeToolkit!!.setTextureSampler(this.depthRamp)
		this.nativeToolkit!!.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_5)
		this.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit!!.setTextureColourSource(true, 2, TextureCombineSource.TEXTURE, false)
		this.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.PREVIOUS)
		this.nativeToolkit!!.setTextureSlot(0)
		this.onUnderwaterSettingsChanged()
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
	}

	@OriginalMember(owner = "client!fl", name = "g", descriptor = "(I)V")
	private fun uploadLighting() {
		if (this.boundShader == null || !this.lit) {
			return
		}
		@Pc(15) val viewRotation: NativeMatrix = this.nativeToolkit!!.getInverseSunMatrix44()
		@Pc(19) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
		device!!.a(13, this.nativeToolkit!!.intensity * this.nativeToolkit!!.baseLightColorRed, this.nativeToolkit!!.baseLightColorGreen * this.nativeToolkit!!.intensity, this.nativeToolkit!!.baseLightColorBlue * this.nativeToolkit!!.intensity, 1.0f)
		device.a(14, this.nativeToolkit!!.sunColorIntensity * this.nativeToolkit!!.baseLightColorRed, this.nativeToolkit!!.baseLightColorGreen * this.nativeToolkit!!.sunColorIntensity, this.nativeToolkit!!.sunColorIntensity * this.nativeToolkit!!.baseLightColorBlue, 1.0f)
		device.a(16, this.nativeToolkit!!.baseLightColorRed * this.nativeToolkit!!.antiSunColorIntensity, this.nativeToolkit!!.baseLightColorGreen * this.nativeToolkit!!.antiSunColorIntensity, this.nativeToolkit!!.baseLightColorBlue * this.nativeToolkit!!.antiSunColorIntensity, 1.0f)
		viewRotation.transformDirection(this.nativeToolkit!!.sunPosition[0], this.nativeToolkit!!.sunPosition[1], this.nativeToolkit!!.sunPosition[2], D3DUnderwaterPassConstants.VECTOR_BUFFER)
		device.SetVertexShaderConstantF(15, D3DUnderwaterPassConstants.VECTOR_BUFFER, 1)
		viewRotation.transformDirection(this.nativeToolkit!!.lightPosition[0], this.nativeToolkit!!.lightPosition[1], this.nativeToolkit!!.lightPosition[2], D3DUnderwaterPassConstants.VECTOR_BUFFER)
		device.SetVertexShaderConstantF(17, D3DUnderwaterPassConstants.VECTOR_BUFFER, 1)
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(Z)V")
	override fun onCameraChanged() {
		if (this.boundShader != null) {
			@Pc(11) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
			@Pc(16) val worldViewProjection: NativeMatrix = this.d3dToolkit.calibrateViewPosition()
			device!!.a(0, worldViewProjection.copyRowMajorInto(D3DUnderwaterPassConstants.MATRIX_BUFFER))
		}
	}

	@OriginalMember(owner = "client!fl", name = "f", descriptor = "(I)V")
	override fun onTextureMatrixChanged() {
		if (this.boundShader != null) {
			@Pc(14) val device: IDirect3DDevice? = this.d3dToolkit!!.iDirect3DDevice
			@Pc(19) val textureMatrix: NativeMatrix? = this.nativeToolkit!!.getTextureMatrix()
			device!!.SetVertexShaderConstantF(8, textureMatrix!!.copyXYInto(D3DUnderwaterPassConstants.MATRIX_BUFFER), 2)
		}
	}
}
