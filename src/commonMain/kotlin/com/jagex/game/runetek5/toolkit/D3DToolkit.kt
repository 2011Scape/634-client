package com.jagex.game.runetek5.toolkit

import com.jagex.game.runetek5.DepthBuffer
import com.jagex.game.runetek5.graphics.FlipException
import com.jagex.game.runetek5.graphics.GpuIndexBuffer
import com.jagex.game.runetek5.graphics.GpuTexture
import com.jagex.game.runetek5.graphics.OffscreenSurface
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.graphics.Renderer
import com.jagex.game.runetek5.graphics.sprite.Sprite
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.toolkit.companion.NativeStream
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent
import com.jagex.game.runetek5.graphics.dx.D3DCubeMapTexture
import com.jagex.game.runetek5.graphics.dx.D3DTexture2D
import com.jagex.game.runetek5.graphics.dx.D3DTexture3D
import com.jagex.game.runetek5.graphics.dx.DxIndexBuffer
import com.jagex.game.runetek5.graphics.dx.DxVertexBuffer
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import jaclib.peer.hb
import jagdx.D3DADAPTER_IDENTIFIER
import jagdx.D3DCAPS
import jagdx.D3DDISPLAYMODE
import jagdx.D3DLIGHT
import jagdx.D3DPRESENT_PARAMETERS
import jagdx.GeometryBuffer
import jagdx.IDirect3D
import jagdx.IDirect3DDevice
import jagdx.IDirect3DEventQuery
import jagdx.IDirect3DPixelShader
import jagdx.IDirect3DSurface
import jagdx.IDirect3DVertexShader
import jagdx.PixelBuffer
import jagdx.fda
import jagdx.ue
import awt.Canvas
import awt.Dimension
import awt.Rectangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.PrimitiveType
import com.jagex.game.runetek5.D3DSwapChain
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.DataType
import com.jagex.game.runetek5.FilterMode
import com.jagex.game.runetek5.TexCoordGenMode
import com.jagex.game.runetek5.PixelFormat
import com.jagex.game.runetek5.D3DEnvironmentMappedWaterPass
import com.jagex.game.runetek5.D3DTexture
import com.jagex.game.runetek5.D3DTransparentWaterPass
import com.jagex.game.runetek5.D3DUnderwaterPass
import com.jagex.game.runetek5.EnvironmentSampler
import com.jagex.game.runetek5.Texture3DSampler
import com.jagex.game.runetek5.CubeTextureSampler
import com.jagex.game.runetek5.RenderPass
import com.jagex.game.runetek5.D3DEnumTranslator
import com.jagex.game.runetek5.dxVertexLayout
import com.jagex.game.runetek5.graphics.texture.TextureState
import com.jagex.game.runetek5.graphics.texture.sampler.SampleMode
import com.jagex.game.runetek5.toolkit.companion.BlendFuncMode
import com.jagex.game.runetek5.TextureTransformMode
import kotlin.jvm.JvmField
import kotlin.jvm.Synchronized
import lang.Thread
import kotlin.jvm.JvmStatic
import awt.height
import awt.width

@OriginalClass("client!bfa")
class D3DToolkit : AbstractNativeToolkit {

	@OriginalMember(owner = "client!bfa", name = "Xg", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private var iDirect3DVertexShader: IDirect3DVertexShader? = null

	@OriginalMember(owner = "client!bfa", name = "Og", descriptor = "I")
	private var failedFlipCount: Int = 0

	@OriginalMember(owner = "client!bfa", name = "eh", descriptor = "Z")
	private var lightsConfigured: Boolean = false

	@OriginalMember(owner = "client!bfa", name = "Rg", descriptor = "Lclient!jaclib/peer/hb;")
	@JvmField
	val peerTracker: hb?

	@OriginalMember(owner = "client!bfa", name = "dh", descriptor = "Lclient!jagdx/D3DPRESENT_PARAMETERS;")
	private val d3DPRESENT_PARAMETERS: D3DPRESENT_PARAMETERS?

	@OriginalMember(owner = "client!bfa", name = "kh", descriptor = "I")
	private val deviceType: Int

	@OriginalMember(owner = "client!bfa", name = "Wg", descriptor = "Lclient!jagdx/IDirect3D;")
	private val iDirect3D: IDirect3D?

	@OriginalMember(owner = "client!bfa", name = "ch", descriptor = "I")
	private val adapter: Int

	@OriginalMember(owner = "client!bfa", name = "Qg", descriptor = "Lclient!je;")
	private var currentSwapChain: D3DSwapChain? = null

	@OriginalMember(owner = "client!bfa", name = "nh", descriptor = "Lclient!jagdx/IDirect3DDevice;")
	@JvmField
	val iDirect3DDevice: IDirect3DDevice?

	@OriginalMember(owner = "client!bfa", name = "Ug", descriptor = "Lclient!jagdx/D3DCAPS;")
	@JvmField
	val d3DCAPS: D3DCAPS?

	@OriginalMember(owner = "client!bfa", name = "Sg", descriptor = "Lclient!jagdx/D3DLIGHT;")
	private val sunLight: D3DLIGHT

	@OriginalMember(owner = "client!bfa", name = "Zg", descriptor = "Lclient!jagdx/D3DLIGHT;")
	private val antiSunLight: D3DLIGHT

	@OriginalMember(owner = "client!bfa", name = "lh", descriptor = "Lclient!jagdx/D3DLIGHT;")
	private val scenePointLight: D3DLIGHT

	@OriginalMember(owner = "client!bfa", name = "gh", descriptor = "Lclient!jagdx/PixelBuffer;")
	@JvmField
	val pixelBuffer: PixelBuffer

	@OriginalMember(owner = "client!bfa", name = "oh", descriptor = "Lclient!jagdx/GeometryBuffer;")
	@JvmField
	val geometryBuffer: GeometryBuffer

	@OriginalMember(owner = "client!bfa", name = "bh", descriptor = "Z")
	@JvmField
	val mipmapSupported: Boolean

	@OriginalMember(owner = "client!bfa", name = "Vg", descriptor = "Z")
	@JvmField
	val nonPowerOfTwoSupported: Boolean

	@OriginalMember(owner = "client!bfa", name = "fh", descriptor = "Z")
	@JvmField
	val mipmappedCubeMapSupported: Boolean

	@OriginalMember(owner = "client!bfa", name = "jh", descriptor = "[Z")
	private val textureRepeatV: BooleanArray

	@OriginalMember(owner = "client!bfa", name = "hh", descriptor = "[Z")
	private val textureMipmapped: BooleanArray

	@OriginalMember(owner = "client!bfa", name = "Ng", descriptor = "[Z")
	private val textureBound: BooleanArray

	@OriginalMember(owner = "client!bfa", name = "Yg", descriptor = "[Lclient!ria;")
	private val textureFilters: Array<FilterMode?>

	@OriginalMember(owner = "client!bfa", name = "ih", descriptor = "[I")
	private val textureTransformFlags: IntArray

	@OriginalMember(owner = "client!bfa", name = "Pg", descriptor = "[Z")
	private val textureRepeatU: BooleanArray

	@OriginalMember(owner = "client!bfa", name = "<init>", descriptor = "(IILjava/awt/Canvas;Lclient!jaclib/peer/hb;Lclient!jagdx/IDirect3D;Lclient!jagdx/IDirect3DDevice;Lclient!je;Lclient!jagdx/D3DPRESENT_PARAMETERS;Lclient!jagdx/D3DCAPS;Lclient!d;Lclient!in;I)V")
	constructor(@OriginalArg(0) adapter: Int, @OriginalArg(1) deviceType: Int, @OriginalArg(2) canvas: Canvas?, @OriginalArg(3) peerTracker: hb?, @OriginalArg(4) iDirect3D: IDirect3D?, @OriginalArg(5) iDirect3DDevice: IDirect3DDevice?, @OriginalArg(6) swapChain: D3DSwapChain?, @OriginalArg(7) d3DPRESENT_PARAMETERS: D3DPRESENT_PARAMETERS?, @OriginalArg(8) d3DCAPS: D3DCAPS?, @OriginalArg(9) textureSource: MaterialProvider?, @OriginalArg(10) shaderResource: Js5ResourceProvider?, @OriginalArg(11) multiSampleCount: Int) : super(canvas, swapChain, textureSource, shaderResource, multiSampleCount, 0) {
		try {
			this.peerTracker = peerTracker
			this.d3DPRESENT_PARAMETERS = d3DPRESENT_PARAMETERS
			this.deviceType = deviceType
			this.iDirect3D = iDirect3D
			this.adapter = adapter
			this.currentSwapChain = swapChain
			this.iDirect3DDevice = iDirect3DDevice
			this.d3DCAPS = d3DCAPS
			this.sunLight = D3DLIGHT(this.peerTracker)
			this.antiSunLight = D3DLIGHT(this.peerTracker)
			this.scenePointLight = D3DLIGHT(this.peerTracker)
			this.pixelBuffer = PixelBuffer(this.peerTracker)
			this.geometryBuffer = GeometryBuffer(this.peerTracker)
			GeometryBuffer(this.peerTracker)
			this.cubeMapSupported = (this.d3DCAPS!!.TextureCaps and 0x800) != 0
			this.mipmapSupported = (this.d3DCAPS.TextureCaps and 0x4000) != 0
			this.nonPowerOfTwoSupported = (this.d3DCAPS.TextureCaps and 0x2) == 0
			this.mipmappedCubeMapSupported = (this.d3DCAPS.TextureCaps and 0x10000) != 0
			this.maxActiveLights = if (this.d3DCAPS.MaxActiveLights <= 0) 8 else this.d3DCAPS.MaxActiveLights
			this.SUPPORTS_TEXTURE3D = (this.d3DCAPS.TextureCaps and 0x2000) != 0
			this.maxConcurrentTextures = this.d3DCAPS.MaxSimultaneousTextures
			this.GLARBMultisample = this.multiSampleCount > 0 || this.iDirect3D!!.CheckDeviceMultiSampleType(this.adapter, this.deviceType, this.d3DPRESENT_PARAMETERS!!.BackBufferFormat, true, 2) == 0
			this.textureRepeatV = BooleanArray(this.maxConcurrentTextures)
			this.textureMipmapped = BooleanArray(this.maxConcurrentTextures)
			this.textureBound = BooleanArray(this.maxConcurrentTextures)
			this.textureFilters = arrayOfNulls<FilterMode>(this.maxConcurrentTextures)
			this.textureTransformFlags = IntArray(this.maxConcurrentTextures)
			this.textureRepeatU = BooleanArray(this.maxConcurrentTextures)
			this.iDirect3DDevice!!.BeginScene()
		} catch (@Pc(218) ex: Throwable) {
			ex.printStackTrace()
			this.free()
			throw RuntimeException("")
		}
	}

	@OriginalMember(owner = "client!bfa", name = "H", descriptor = "(I)V")
	override fun setFogColorAndIntensity() {
		this.sunLight.SetAmbient(this.baseLightColorRed * this.intensity, this.intensity * this.baseLightColorGreen, this.baseLightColorBlue * this.intensity, 0.toFloat())
		this.lightsConfigured = false
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!kf;IBZ)V")
	override fun setTextureAlphaStage(@OriginalArg(0) state: TextureCombineSource?, @OriginalArg(1) sourceIndex: Int) {
		@Pc(19) val argState: Byte
		if (sourceIndex == 1) {
			argState = 6
		} else if (sourceIndex == 2) {
			argState = 27
		} else {
			argState = 5
		}
		this.iDirect3DDevice!!.SetTextureStageState(this.currentTextureSlot, argState.toInt(), D3DEnumTranslator.toD3DTextureArg(state) or 0x0)
	}

	@OriginalMember(owner = "client!bfa", name = "na", descriptor = "(IIII)[I")
	override fun na(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int): IntArray? {
		@Pc(1) var pixels: IntArray? = null
		@Pc(6) val iDirect3DSurface: IDirect3DSurface? = this.iDirect3DDevice!!.c(0)
		@Pc(16) val iDirect3DSurface2: IDirect3DSurface? = this.iDirect3DDevice.a(width, height, 21, 0, 0, true)
		if (ue.a(this.iDirect3DDevice.StretchRect(iDirect3DSurface, x, y, width, height, iDirect3DSurface2, 0, 0, width, height, 0), false) && ue.a(iDirect3DSurface2!!.LockRect(0, 0, width, height, 16, this.pixelBuffer), false)) {
			pixels = IntArray(height * width)
			@Pc(56) val rowPitch: Int = this.pixelBuffer.getRowPitch()
			if (width * 4 == rowPitch) {
				this.pixelBuffer.b(pixels, 0, 0, width * height)
			} else {
				for (@Pc(67) i in 0 until height) {
					this.pixelBuffer.b(pixels, i * width, i * rowPitch, width)
				}
			}
			iDirect3DSurface2.UnlockRect()
		}
		iDirect3DSurface!!.a(9275)
		iDirect3DSurface2!!.a(9275)
		return pixels
	}

	@OriginalMember(owner = "client!bfa", name = "j", descriptor = "(Z)V")
	override fun activateCurrentTexture() {
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILclient!np;)V")
	override fun setVertexDeclaration(@OriginalArg(1) vertexDeclaration: VertexDeclaration?) {
		@Pc(2) val layout: dxVertexLayout? = vertexDeclaration as dxVertexLayout?
		this.iDirect3DDevice!!.SetVertexDeclaration(layout!!.vertexDeclaration)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILclient!kt;)V")
	fun bindTexture2D(@OriginalArg(1) texture: D3DTexture2D?) {
		this.bindTexture(texture)
		if (this.textureRepeatU[this.currentTextureSlot] != texture!!.repeatU) {
			this.iDirect3DDevice!!.SetSamplerState(this.currentTextureSlot, 1, if (texture.repeatU) 1 else 3)
			this.textureRepeatU[this.currentTextureSlot] = texture.repeatU
		}
		if (this.textureRepeatV[this.currentTextureSlot] != texture.repeatV) {
			this.iDirect3DDevice!!.SetSamplerState(this.currentTextureSlot, 2, if (texture.repeatV) 1 else 3)
			this.textureRepeatV[this.currentTextureSlot] = texture.repeatV
		}
	}

	@OriginalMember(owner = "client!bfa", name = "j", descriptor = "(B)V")
	override fun setDepthRenderState() {
		this.iDirect3DDevice!!.a(14, this.depthBufferingEnabled && this.depthMaskEnabled)
	}

	@OriginalMember(owner = "client!bfa", name = "v", descriptor = "()V")
	override fun compositeBloomEffects() {
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IILclient!wq;ZI[B)Lclient!ir;")
	override fun create3DTexture(@OriginalArg(2) format: PixelFormat?, @OriginalArg(5) data: ByteArray?): Texture3DSampler {
		return D3DTexture3D(this, format, 128, 128, 16, data)
	}

	@OriginalMember(owner = "client!bfa", name = "g", descriptor = "(B)V")
	override fun toggleFog() {
		this.iDirect3DDevice!!.a(28, this.fogSupported && this.fogEnabled && this.fogDistance >= 0)
	}

	@OriginalMember(owner = "client!bfa", name = "t", descriptor = "(B)V")
	override fun uploadModelMatrix() {
		if (this.isModelMatrixIdentity) {
			D3DEnumTranslator.MATRIX_BUFFER[1] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[2] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[15] = 1.0f
			D3DEnumTranslator.MATRIX_BUFFER[3] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[9] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[6] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[0] = 1.0f
			D3DEnumTranslator.MATRIX_BUFFER[7] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[11] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[13] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[10] = 1.0f
			D3DEnumTranslator.MATRIX_BUFFER[8] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[12] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[5] = 1.0f
			D3DEnumTranslator.MATRIX_BUFFER[14] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[4] = 0.0f
		} else {
			this.modelMatrix43!!.copyColumnMajorInto(D3DEnumTranslator.MATRIX_BUFFER)
		}
		this.iDirect3DDevice!!.SetTransform(256, D3DEnumTranslator.MATRIX_BUFFER)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(BZ)V")
	override fun toggleMultisampling(@OriginalArg(1) enabled: Boolean) {
		this.iDirect3DDevice!!.a(161, enabled)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ZZILclient!kf;Z)V")
	override fun setTextureColourSource(@OriginalArg(0) useAlpha: Boolean, @OriginalArg(2) sourceIndex: Int, @OriginalArg(3) state: TextureCombineSource?, @OriginalArg(4) invert: Boolean) {
		@Pc(12) val argState: Byte
		if (sourceIndex == 1) {
			argState = 3
		} else if (sourceIndex == 2) {
			argState = 26
		} else {
			argState = 2
		}
		@Pc(20) var argModifiers = 0
		if (useAlpha) {
			argModifiers = 32
		}
		if (invert) {
			argModifiers = argModifiers or 0x10
		}
		this.iDirect3DDevice!!.SetTextureStageState(this.currentTextureSlot, argState.toInt(), argModifiers or D3DEnumTranslator.toD3DTextureArg(state))
	}

	@OriginalMember(owner = "client!bfa", name = "x", descriptor = "(I)V")
	override fun setLightPosition() {
		this.sunLight.SetDirection(-this.sunPosition[0], -this.sunPosition[1], -this.sunPosition[2])
		this.antiSunLight.SetDirection(-this.lightPosition[0], -this.lightPosition[1], -this.lightPosition[2])
		this.lightsConfigured = false
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(II)Lclient!vm;")
	override fun createDepthBuffer(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int): DepthBuffer? {
		return null
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(BI)V")
	override fun setTexCoordIndex(@OriginalArg(1) texCoordIndex: Int) {
		this.iDirect3DDevice!!.SetTextureStageState(this.currentTextureSlot, 11, texCoordIndex)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Ljava/awt/Canvas;ILjava/lang/Object;)V")
	override fun destroySurface(@OriginalArg(0) canvas: Canvas?, @OriginalArg(2) `object`: Any?) {
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(BLclient!cg;)V")
	fun bindTexture(@OriginalArg(1) texture: D3DTexture?) {
		this.iDirect3DDevice!!.SetTexture(this.currentTextureSlot, texture!!.getTexture())
		if (this.textureFilters[this.currentTextureSlot] !== texture.filter) {
			@Pc(61) val filterMode: Int = D3DEnumTranslator.toD3DFilter(texture.filter)
			this.iDirect3DDevice.SetSamplerState(this.currentTextureSlot, 6, filterMode)
			this.iDirect3DDevice.SetSamplerState(this.currentTextureSlot, 5, filterMode)
			this.textureFilters[this.currentTextureSlot] = texture.filter
			if (this.textureMipmapped[this.currentTextureSlot] != texture.mipmapped) {
				this.iDirect3DDevice.SetSamplerState(this.currentTextureSlot, 7, if (texture.mipmapped) D3DEnumTranslator.toD3DFilter(texture.filter) else 0)
				this.textureMipmapped[this.currentTextureSlot] = texture.mipmapped
			}
		} else if (this.textureMipmapped[this.currentTextureSlot] != texture.mipmapped) {
			this.iDirect3DDevice.SetSamplerState(this.currentTextureSlot, 7, if (texture.mipmapped) D3DEnumTranslator.toD3DFilter(texture.filter) else 0)
			this.textureMipmapped[this.currentTextureSlot] = texture.mipmapped
		}
		if (!this.textureBound[this.currentTextureSlot]) {
			this.textureBound[this.currentTextureSlot] = true
			this.setTextureAlphaStage()
			this.setTextureColorStage()
		}
	}

	@OriginalMember(owner = "client!bfa", name = "d", descriptor = "(II)Lclient!he;")
	override fun method7268(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int): Sprite? {
		return null
	}

	@OriginalMember(owner = "client!bfa", name = "c", descriptor = "(Z)V")
	override fun uploadProjectionMatrix() {
		this.iDirect3DDevice!!.SetTransform(3, this.activeProjectionSource)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!jagdx/IDirect3DVertexShader;B)V")
	fun setVertexShader(@OriginalArg(0) vertexShader: IDirect3DVertexShader?) {
		this.iDirect3DVertexShader = vertexShader
		this.iDirect3DDevice!!.SetVertexShader(vertexShader)
		this.setTextureMatrix()
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!hj;ILclient!uq;IIII)V")
	override fun drawIndexedPrimitive(@OriginalArg(0) drawMode: PrimitiveType?, @OriginalArg(1) primitiveCount: Int, @OriginalArg(2) indexBuffer: GpuIndexBuffer?, @OriginalArg(3) minVertexIndex: Int, @OriginalArg(4) startIndex: Int, @OriginalArg(5) vertexCount: Int) {
		this.iDirect3DDevice!!.SetIndices((indexBuffer as DxIndexBuffer).indexBuffer)
		this.iDirect3DDevice.DrawIndexedPrimitive(D3DEnumTranslator.toD3DPrimitiveType(drawMode), 0, minVertexIndex, vertexCount, startIndex, primitiveCount)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!he;Lclient!vm;)Lclient!is;")
	override fun createOffscreenSurface(@OriginalArg(0) surface: Sprite?, @OriginalArg(1) buffer: DepthBuffer?): OffscreenSurface? {
		return null
	}

	@OriginalMember(owner = "client!bfa", name = "p", descriptor = "()V")
	override fun restoreSurface() {
	}

	@OriginalMember(owner = "client!bfa", name = "w", descriptor = "(I)V")
	override fun setScissorRect() {
		this.iDirect3DDevice!!.SetScissorRect(this.viewportOffsetX + this.cLeft, this.viewportOffsetY - -this.cTop, this.cRight, this.cBottom)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	@Throws(FlipException::class)
	override fun flipDirtyRect(@OriginalArg(0) rectangles: Array<Rectangle?>?, @OriginalArg(1) count: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int) {
		this.flip(x, y)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ZLclient!wq;Lclient!baa;)Z")
	override fun supports3DTextureFormat(@OriginalArg(1) format: PixelFormat?, @OriginalArg(2) type: DataType?): Boolean {
		@Pc(3) val d3DDISPLAYMODE = D3DDISPLAYMODE()
		return ue.a(this.iDirect3D!!.a(this.adapter, d3DDISPLAYMODE), false) && ue.a(this.iDirect3D.CheckDeviceFormat(this.adapter, this.deviceType, d3DDISPLAYMODE.Format, 0, 4, D3DEnumTranslator.toD3DFormat(type, format)), false)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Ljava/lang/Object;BLjava/awt/Canvas;)V")
	override fun setSurface(@OriginalArg(0) `object`: Any?, @OriginalArg(2) canvas: Canvas?) {
		this.currentSwapChain = `object` as D3DSwapChain?
	}

	@OriginalMember(owner = "client!bfa", name = "h", descriptor = "(Z)V")
	override fun setSunParameters() {
		@Pc(7) val sunIntensity: Float = if (this.sunEnabled) this.sunColorIntensity else 0.0f
		@Pc(16) val antiSunIntensity: Float = if (this.sunEnabled) -this.antiSunColorIntensity else 0.0f
		this.sunLight.SetDiffuse(sunIntensity * this.baseLightColorRed, this.baseLightColorGreen * sunIntensity, sunIntensity * this.baseLightColorBlue, 0.0f)
		this.antiSunLight.SetDiffuse(this.baseLightColorRed * antiSunIntensity, this.baseLightColorGreen * antiSunIntensity, this.baseLightColorBlue * antiSunIntensity, 0.0f)
		this.lightsConfigured = false
	}

	@OriginalMember(owner = "client!bfa", name = "m", descriptor = "()Z")
	override fun bloom(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "()V")
	override fun finish() {
		@Pc(3) val iDirect3DEventQuery: IDirect3DEventQuery = this.iDirect3DDevice!!.b()!!
		if (ue.a(iDirect3DEventQuery.Issue(), false)) {
			while (true) {
				@Pc(12) val status: Int = iDirect3DEventQuery.IsSignaled()
				if (status != 1) {
					break
				}
				lang.yield()
			}
		}
		iDirect3DEventQuery.a(9275)
	}

	@OriginalMember(owner = "client!bfa", name = "F", descriptor = "(II)V")
	override fun F(@OriginalArg(0) offsetX: Int, @OriginalArg(1) offsetY: Int) {
	}

	@OriginalMember(owner = "client!bfa", name = "P", descriptor = "(I)V")
	override fun setTextureEnvironmentColor() {
		this.iDirect3DDevice!!.SetRenderState(60, this.envColor)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!wh;Lclient!wh;FLclient!wh;)Lclient!wh;")
	override fun drawQuad(@OriginalArg(0) environmentSampler: EnvironmentSampler?, @OriginalArg(1) environmentSampler2: EnvironmentSampler?, @OriginalArg(2) alpha: Float, @OriginalArg(3) environmentSampler3: EnvironmentSampler?): EnvironmentSampler? {
		return if (alpha < 0.5f) environmentSampler else environmentSampler2
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(Z)V")
	override fun method7250(@OriginalArg(0) enabled: Boolean) {
	}

	@OriginalMember(owner = "client!bfa", name = "e", descriptor = "(B)V")
	override fun updateSceneLights() {
		@Pc(9) var lightIndex: Int
		lightIndex = 0
		while (lightIndex < this.lastSceneLightCount) {
			@Pc(22) val pointLight: PointLight = this.lightSources!![lightIndex]!!
			@Pc(26) val deviceLightIndex: Int = lightIndex + 2
			@Pc(30) val range: Int = pointLight.getColour()
			@Pc(36) val intensityScale: Float = pointLight.getIntensity() / 255.0f
			this.scenePointLight.SetPosition(pointLight.getX().toFloat(), pointLight.getY().toFloat(), pointLight.getZ().toFloat())
			this.scenePointLight.SetDiffuse(intensityScale * ((range shr 16) and 0xFF).toFloat(), ((range shr 8) and 0xFF).toFloat() * intensityScale, intensityScale * (range and 0xFF).toFloat(), 0.0f)
			this.scenePointLight.SetAttenuation(0.0f, 0.0f, 1.0f / (pointLight.getRadius() * pointLight.getRadius()).toFloat())
			this.scenePointLight.SetRange(pointLight.getRadius().toFloat())
			this.iDirect3DDevice!!.SetLight(deviceLightIndex, this.scenePointLight)
			this.iDirect3DDevice.LightEnable(deviceLightIndex, true)
			lightIndex++
		}
		while (this.currentSceneLightCount > lightIndex) {
			this.iDirect3DDevice!!.LightEnable(lightIndex + 2, false)
			lightIndex++
		}
		super.updateSceneLights()
	}

	@OriginalMember(owner = "client!bfa", name = "d", descriptor = "(B)V")
	override fun unbindCurrentTexture() {
		if (this.textureBound[this.currentTextureSlot]) {
			this.textureBound[this.currentTextureSlot] = false
			this.iDirect3DDevice!!.SetTexture(this.currentTextureSlot, null)
			this.setTextureAlphaStage()
			this.setTextureColorStage()
		}
	}

	@OriginalMember(owner = "client!bfa", name = "z", descriptor = "(I)V")
	override fun uploadViewMatrix() {
		if (this.matrixMode!!.isSceneMode()) {
			this.flippedViewMatrix!!.copyColumnMajorInto(D3DEnumTranslator.MATRIX_BUFFER)
		} else {
			D3DEnumTranslator.MATRIX_BUFFER[1] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[7] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[12] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[0] = 1.0f
			D3DEnumTranslator.MATRIX_BUFFER[5] = 1.0f
			D3DEnumTranslator.MATRIX_BUFFER[13] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[2] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[11] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[9] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[10] = 1.0f
			D3DEnumTranslator.MATRIX_BUFFER[14] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[8] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[4] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[15] = 1.0f
			D3DEnumTranslator.MATRIX_BUFFER[6] = 0.0f
			D3DEnumTranslator.MATRIX_BUFFER[3] = 0.0f
		}
		this.iDirect3DDevice!!.SetTransform(2, D3DEnumTranslator.MATRIX_BUFFER)
	}

	@OriginalMember(owner = "client!bfa", name = "h", descriptor = "()V")
	override fun stop() {
		this.peerTracker!!.a(13.toByte())
		super.stop()
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IZ)Lclient!uq;")
	override fun createIndexBuffer(@OriginalArg(1) dynamic: Boolean): GpuIndexBuffer {
		return DxIndexBuffer(this, DataType.aClass26_5, dynamic)
	}

	@OriginalMember(owner = "client!bfa", name = "GA", descriptor = "(I)V")
	override fun GA(@OriginalArg(0) colour: Int) {
		this.iDirect3DDevice!!.Clear(1, colour, 0.0f, 0)
	}

	@OriginalMember(owner = "client!bfa", name = "I", descriptor = "()I")
	override fun I(): Int {
		return 0
	}

	@OriginalMember(owner = "client!bfa", name = "o", descriptor = "(I)V")
	override fun configureFog() {
		this.fogEnd = (this.camFar - this.fogOffset).toFloat()
		this.fogStart = (-this.fogDistance).toFloat() + this.fogEnd
		if (this.fogStart < this.camNear.toFloat()) {
			this.fogStart = this.camNear.toFloat()
		}
		this.iDirect3DDevice!!.a(36, this.fogStart)
		this.iDirect3DDevice.a(37, this.fogEnd)
		this.iDirect3DDevice.SetRenderState(34, this.fogColorRgb)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!wq;II[FZIII)Lclient!wfa;")
	override fun createNativeTexture(@OriginalArg(0) format: PixelFormat?, @OriginalArg(1) width: Int, @OriginalArg(3) pixels: FloatArray?, @OriginalArg(4) mipmapped: Boolean, @OriginalArg(7) height: Int): GpuTexture? {
		return null
	}

	@OriginalMember(owner = "client!bfa", name = "r", descriptor = "(B)V")
	override fun enableLights() {
		if (this.lightsConfigured) {
			return
		}
		this.iDirect3DDevice!!.LightEnable(0, false)
		this.iDirect3DDevice.LightEnable(1, false)
		this.iDirect3DDevice.SetLight(0, this.sunLight)
		this.iDirect3DDevice.SetLight(1, this.antiSunLight)
		this.iDirect3DDevice.LightEnable(0, true)
		this.iDirect3DDevice.LightEnable(1, true)
		this.lightsConfigured = true
	}

	@OriginalMember(owner = "client!bfa", name = "o", descriptor = "()Z")
	override fun hasBloom(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!bfa", name = "D", descriptor = "(B)V")
	override fun setViewport() {
		this.iDirect3DDevice!!.SetViewport(this.viewportOffsetX, this.viewportOffsetY, this.surfaceWidth, this.surfaceHeight, 0.0f, 1.0f)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILclient!nc;)V")
	fun bindTexture3D(@OriginalArg(1) texture: D3DTexture3D?) {
		this.bindTexture(texture)
		if (!this.textureRepeatU[this.currentTextureSlot]) {
			this.iDirect3DDevice!!.SetSamplerState(this.currentTextureSlot, 1, 1)
			this.textureRepeatU[this.currentTextureSlot] = true
		}
		if (!this.textureRepeatV[this.currentTextureSlot]) {
			this.iDirect3DDevice!!.SetSamplerState(this.currentTextureSlot, 2, 1)
			this.textureRepeatV[this.currentTextureSlot] = true
		}
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IILclient!hj;Z)V")
	override fun drawPrimitive(@OriginalArg(0) primitiveCount: Int, @OriginalArg(1) first: Int, @OriginalArg(2) drawMode: PrimitiveType?) {
		this.iDirect3DDevice!!.DrawPrimitive(D3DEnumTranslator.toD3DPrimitiveType(drawMode), first, primitiveCount)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IIII)V")
	override fun method7261(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int) {
	}

	@OriginalMember(owner = "client!bfa", name = "d", descriptor = "(Z)V")
	override fun toggleSun() {
		this.setSunParameters()
		this.enableLights()
	}

	@OriginalMember(owner = "client!bfa", name = "q", descriptor = "()V")
	override fun renderLastFrame() {
	}

	@OriginalMember(owner = "client!bfa", name = "Q", descriptor = "(I)V")
	override fun toggleBlending() {
		this.iDirect3DDevice!!.a(27, this.enableBlending)
	}

	@OriginalMember(owner = "client!bfa", name = "p", descriptor = "(I)V")
	override fun toggleDepthTest() {
		this.iDirect3DDevice!!.a(7, this.enableDepthTest)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IIII[BLclient!wq;IZ)Lclient!wfa;")
	override fun createNativeTextureSampler(@OriginalArg(0) height: Int, @OriginalArg(2) width: Int, @OriginalArg(4) pixels: ByteArray?, @OriginalArg(5) format: PixelFormat?, @OriginalArg(7) mipmapped: Boolean): GpuTexture {
		return D3DTexture2D(this, format, width, height, mipmapped, pixels, 0, 0)
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(I)V")
	@Synchronized
	override fun tick(@OriginalArg(0) time: Int) {
		this.peerTracker!!.b((-82).toByte())
		super.tick(time)
	}

	@OriginalMember(owner = "client!bfa", name = "i", descriptor = "(Z)F")
	override fun getPixelOffset(): Float {
		return -0.5f
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILjava/awt/Canvas;)Ljava/lang/Object;")
	override fun createSurface(@OriginalArg(1) canvas: Canvas?): Any? {
		return null
	}

	@OriginalMember(owner = "client!bfa", name = "q", descriptor = "(Z)Z")
	private fun resetDevice(): Boolean {
		@Pc(3) val cooperativeLevel: Int = this.iDirect3DDevice!!.TestCooperativeLevel()
		if (cooperativeLevel == 0 || cooperativeLevel == -2005530519) {
			@Pc(23) val primarySwapChain: D3DSwapChain? = this.primarySurface as D3DSwapChain?
			this.deallocate()
			primarySwapChain!!.release()
			this.d3DPRESENT_PARAMETERS!!.BackBufferHeight = 0
			this.d3DPRESENT_PARAMETERS.BackBufferWidth = 0
			if (ClientFeatures.scaled()) {
				// Not part of the original client: with client.scale the back buffer is the game's
				// size, and Present stretches it over the larger window.
				this.d3DPRESENT_PARAMETERS.BackBufferWidth = this.surfaceWidth
				this.d3DPRESENT_PARAMETERS.BackBufferHeight = this.surfaceHeight
			}
			if (D3DEnumTranslator.selectPresentFormats(this.d3DPRESENT_PARAMETERS, this.adapter, this.iDirect3D, this.deviceType, this.multiSampleCount)) {
				@Pc(56) val result: Int = this.iDirect3DDevice.Reset(this.d3DPRESENT_PARAMETERS)
				if (ue.a(result, false)) {
					primarySwapChain.setSwapChain(this.iDirect3DDevice.b(0), this.iDirect3DDevice.c())
					this.initializeRenderers()
					this.setupGraphics()
					return true
				}
			}
		}
		return false
	}

	@OriginalMember(owner = "client!bfa", name = "F", descriptor = "(I)V")
	override fun setTextureColorStage() {
		@Pc(15) val colourOp: Int = if (this.textureBound[this.currentTextureSlot]) D3DEnumTranslator.toD3DTextureOp(this.textureRgbCombineModes!![this.currentTextureSlot]) else 1
		this.iDirect3DDevice!!.SetTextureStageState(this.currentTextureSlot, 1, colourOp)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(I[IZBIII)Lclient!wfa;")
	override fun createTextureSampler(@OriginalArg(0) width: Int, @OriginalArg(1) pixels: IntArray?, @OriginalArg(2) mipmapped: Boolean, @OriginalArg(4) offset: Int, @OriginalArg(5) rowLength: Int, @OriginalArg(6) height: Int): GpuTexture {
		return D3DTexture2D(this, width, height, mipmapped, pixels, 0, rowLength)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!is;)V")
	override fun swapSurface(@OriginalArg(0) surface: OffscreenSurface?) {
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "([[IZII)Lclient!nj;")
	override fun generateNativeCubeTexture(@OriginalArg(0) pixels: Array<IntArray?>?, @OriginalArg(1) mipmapped: Boolean, @OriginalArg(3) size: Int): CubeTextureSampler {
		return D3DCubeMapTexture(this, size, mipmapped, pixels)
	}

	@OriginalMember(owner = "client!bfa", name = "E", descriptor = "(B)V")
	override fun toggleAlpha() {
		this.iDirect3DDevice!!.a(15, this.enableAlphaTest)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(I[Lclient!jl;)Lclient!np;")
	override fun createVertexDeclaration(@OriginalArg(1) vertexDeclarationElements: Array<VertexDeclarationComponent?>?): VertexDeclaration {
		return dxVertexLayout(this, vertexDeclarationElements)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILclient!jagdx/IDirect3DPixelShader;)V")
	fun setPixelShader(@OriginalArg(1) pixelShader: IDirect3DPixelShader?) {
		this.iDirect3DDevice!!.SetPixelShader(pixelShader)
	}

	@OriginalMember(owner = "client!bfa", name = "A", descriptor = "()V")
	override fun stopBloom() {
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(ZI)Lclient!tc;")
	override fun createStream(@OriginalArg(0) dynamic: Boolean): NativeStream {
		return DxVertexBuffer(this, dynamic)
	}

	@OriginalMember(owner = "client!bfa", name = "G", descriptor = "(I)V")
	override fun toggleScissorTest() {
		this.iDirect3DDevice!!.a(174, this.boundsCalculated)
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(IIIID)V")
	override fun b(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) depthOffset: Double) {
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IB)Lclient!fj;")
	override fun createRenderPass(@OriginalArg(0) effect: Int): RenderPass? {
		if (effect == 3) {
			return D3DUnderwaterPass(this, this.shaderResource)
		} else if (effect == 4) {
			return D3DTransparentWaterPass(this, this.shaderResource, this.waterTexture)
		} else if (effect == 8) {
			return D3DEnvironmentMappedWaterPass(this, this.shaderResource, this.waterTexture)
		} else {
			return super.createRenderPass(effect)
		}
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(FFF)V")
	override fun setBloomParams(@OriginalArg(0) whitePoint: Float, @OriginalArg(1) bloom: Float, @OriginalArg(2) brightpass: Float) {
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!bf;I)V")
	override fun setTexCoordGeneration(@OriginalArg(0) mode: TexCoordGenMode?) {
		@Pc(1) var texCoordFlags = 0
		if (TexCoordGenMode.aClass31_2 === mode) {
			texCoordFlags = 65536
		} else if (TexCoordGenMode.aClass31_1 === mode) {
			texCoordFlags = 131072
		} else if (mode === TexCoordGenMode.aClass31_4) {
			texCoordFlags = 196608
		}
		this.iDirect3DDevice!!.SetTextureStageState(this.currentTextureSlot, 11, this.currentTextureSlot or texCoordFlags)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(Lclient!baa;Lclient!wq;B)Z")
	override fun supportsTextureFormat(@OriginalArg(0) type: DataType?, @OriginalArg(1) format: PixelFormat?): Boolean {
		@Pc(3) val d3DDISPLAYMODE = D3DDISPLAYMODE()
		return ue.a(this.iDirect3D!!.a(this.adapter, d3DDISPLAYMODE), false) && ue.a(this.iDirect3D.CheckDeviceFormat(this.adapter, this.deviceType, d3DDISPLAYMODE.Format, 0, 3, D3DEnumTranslator.toD3DFormat(type, format)), false)
	}

	@OriginalMember(owner = "client!bfa", name = "q", descriptor = "(B)V")
	override fun setupGraphics() {
		for (@Pc(1) i in 0 until this.maxConcurrentTextures) {
			this.iDirect3DDevice!!.SetSamplerState(i, 7, 0)
			this.iDirect3DDevice.SetSamplerState(i, 6, 2)
			this.iDirect3DDevice.SetSamplerState(i, 5, 2)
			this.iDirect3DDevice.SetSamplerState(i, 1, 1)
			this.iDirect3DDevice.SetSamplerState(i, 2, 1)
			this.textureFilters[i] = SampleMode.LINEAR
			this.textureRepeatV[i] = true
			this.textureRepeatU[i] = true
			this.textureMipmapped[i] = false
			this.textureTransformFlags[i] = 0
		}
		this.iDirect3DDevice!!.SetTextureStageState(0, 6, 1)
		this.iDirect3DDevice.SetRenderState(9, 2)
		this.iDirect3DDevice.SetRenderState(23, 4)
		this.iDirect3DDevice.SetRenderState(25, 5)
		this.iDirect3DDevice.SetRenderState(24, 0)
		this.iDirect3DDevice.SetRenderState(22, 2)
		this.iDirect3DDevice.SetRenderState(147, 1)
		this.iDirect3DDevice.SetRenderState(145, 1)
		this.iDirect3DDevice.a(38, 0.95f)
		this.iDirect3DDevice.SetRenderState(140, 3)
		this.sunLight.SetType(3)
		this.antiSunLight.SetType(3)
		this.scenePointLight.SetType(1)
		this.lightsConfigured = false
		super.setupGraphics()
	}

	@OriginalMember(owner = "client!bfa", name = "c", descriptor = "()Lclient!fia;")
	override fun renderer(): Renderer {
		@Pc(6) val identifier: D3DADAPTER_IDENTIFIER = this.iDirect3D!!.a(this.adapter, 0)!!
		return Renderer(identifier.VendorID, "Direct3D", 9, identifier.Description, identifier.DriverVersion)
	}

	@OriginalMember(owner = "client!bfa", name = "K", descriptor = "(I)V")
	override fun setTextureAlphaStage() {
		@Pc(19) val alphaOp: Int = if (this.textureBound[this.currentTextureSlot]) D3DEnumTranslator.toD3DTextureOp(this.textureAlphaCombineModes!![this.currentTextureSlot]) else 1
		this.iDirect3DDevice!!.SetTextureStageState(this.currentTextureSlot, 4, alphaOp)
	}

	@OriginalMember(owner = "client!bfa", name = "M", descriptor = "(I)V")
	override fun setTextureMatrix() {
		if (this.iDirect3DVertexShader != null || this.textureStates!![this.currentTextureSlot] === TextureState.INITIALIZE) {
			this.iDirect3DDevice!!.SetTextureStageState(this.currentTextureSlot, 24, 0)
			this.textureTransformFlags[this.currentTextureSlot] = 0
			return
		}
		if (TextureTransformMode.aClass332_6 === this.textureStates!![this.currentTextureSlot]) {
			this.iDirect3DDevice!!.SetTransform(this.currentTextureSlot + 16, this.textureMatrices!![this.currentTextureSlot]!!.copy2DColumnMajorInto(D3DEnumTranslator.MATRIX_BUFFER))
		} else {
			this.iDirect3DDevice!!.SetTransform(this.currentTextureSlot + 16, this.textureMatrices!![this.currentTextureSlot]!!.copyColumnMajorInto(D3DEnumTranslator.MATRIX_BUFFER))
		}
		@Pc(62) val transformFlags: Int = D3DEnumTranslator.toD3DTextureTransformFlags(this.textureStates!![this.currentTextureSlot])
		if (transformFlags != this.textureTransformFlags[this.currentTextureSlot]) {
			this.iDirect3DDevice.SetTextureStageState(this.currentTextureSlot, 24, transformFlags)
			this.textureTransformFlags[this.currentTextureSlot] = transformFlags
		}
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(IBILclient!baa;Lclient!wq;)Lclient!wfa;")
	override fun createNativeTexture2D(@OriginalArg(0) height: Int, @OriginalArg(2) width: Int, @OriginalArg(3) type: DataType?, @OriginalArg(4) format: PixelFormat?): GpuTexture {
		return D3DTexture2D(this, format, type, width, height)
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILjava/awt/Canvas;Ljava/lang/Object;)V")
	override fun resizeSurface(@OriginalArg(1) canvas: Canvas?, @OriginalArg(2) `object`: Any?) {
		if (canvas !== this.lb) {
			return
		}
		@Pc(7) val dimension: Dimension = canvas!!.getSize()
		if (dimension.width > 0 && dimension.height > 0) {
			this.iDirect3DDevice!!.EndScene()
			this.resetDevice()
			this.iDirect3DDevice.BeginScene()
		}
	}

	@OriginalMember(owner = "client!bfa", name = "u", descriptor = "(I)V")
	override fun toggleLights() {
		this.iDirect3DDevice!!.a(137, this.lightingEnabled && !this.forceLightsDisabled)
	}

	@OriginalMember(owner = "client!bfa", name = "S", descriptor = "(I)V")
	override fun blendDrawBuffer() {
		if (BlendFuncMode.ALPHA_BLENDING === this.blendFuncMode) {
			this.iDirect3DDevice!!.SetRenderState(19, 5)
			this.iDirect3DDevice.SetRenderState(20, 6)
		} else if (this.blendFuncMode === BlendFuncMode.OPAQUE_BLENDING) {
			this.iDirect3DDevice!!.SetRenderState(19, 2)
			this.iDirect3DDevice.SetRenderState(20, 2)
		} else if (BlendFuncMode.COLOR_BLENDING === this.blendFuncMode) {
			this.iDirect3DDevice!!.SetRenderState(19, 9)
			this.iDirect3DDevice.SetRenderState(20, 2)
		}
	}

	@OriginalMember(owner = "client!bfa", name = "b", descriptor = "(II)V")
	@Throws(FlipException::class)
	override fun flip(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int) {
		this.iDirect3DDevice!!.EndScene()
		if (this.currentSwapChain!!.isValid()) {
			this.failedFlipCount = 0
			if (ue.a(97.toByte(), this.currentSwapChain!!.present())) {
				this.resetDevice()
			}
		} else if (++this.failedFlipCount <= 50) {
			this.resetDevice()
		} else {
			throw FlipException()
		}
		this.iDirect3DDevice.BeginScene()
	}

	@OriginalMember(owner = "client!bfa", name = "a", descriptor = "(ILclient!tc;I)V")
	override fun setStreamSource(@OriginalArg(1) vertexBuffer: NativeStream?, @OriginalArg(2) streamIndex: Int) {
		@Pc(10) val dxVertexBuffer: DxVertexBuffer? = vertexBuffer as DxVertexBuffer?
		this.iDirect3DDevice!!.SetStreamSource(streamIndex, dxVertexBuffer!!.vertexBuffer, 0, dxVertexBuffer.getStride())
	}

	@OriginalMember(owner = "client!bfa", name = "ya", descriptor = "()V")
	override fun ya() {
		this.setDepthMaskEnabled(true)
		this.iDirect3DDevice!!.Clear(2, 0, 1.0f, 0)
	}

	companion object {
		@OriginalMember(owner = "client!bfa", name = "createToolkit", descriptor = "(Ljava/awt/Canvas;Lclient!d;Lclient!in;Ljava/lang/Integer;)Lclient!ha;")
		@JvmStatic
		fun createToolkit(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) textureSource: MaterialProvider?, @OriginalArg(2) shaderResource: Js5ResourceProvider?, @OriginalArg(3) integer: Int?): Toolkit {
			@Pc(1) var d3DToolkit: D3DToolkit? = null
			try {
				@Pc(3) val adapter: Byte = 0
				@Pc(5) val deviceType: Byte = 1
				@Pc(9) val peerTracker = hb()
				@Pc(13) val iDirect3D: IDirect3D = IDirect3D.a((-2147483616L).toInt(), peerTracker)!!
				@Pc(18) val d3DCAPS: D3DCAPS = iDirect3D.b(adapter.toInt(), deviceType.toInt())!!
				if ((d3DCAPS.RasterCaps and 0x1000000) == 0) {
					throw RuntimeException("")
				} else if (d3DCAPS.MaxSimultaneousTextures < 2) {
					throw RuntimeException("")
				} else if ((d3DCAPS.TextureOpCaps and 0x2) == 0) {
					throw RuntimeException("")
				} else if ((d3DCAPS.TextureOpCaps and 0x8) == 0) {
					throw RuntimeException("")
				} else if ((d3DCAPS.TextureOpCaps and 0x40) == 0) {
					throw RuntimeException("")
				} else if ((d3DCAPS.TextureOpCaps and 0x200) == 0) {
					throw RuntimeException("")
				} else if ((d3DCAPS.TextureOpCaps and 0x2000000) == 0) {
					throw RuntimeException("")
				} else if ((d3DCAPS.DestBlendCaps and 0x10 and d3DCAPS.SrcBlendCaps) == 0) {
					throw RuntimeException("")
				} else if ((d3DCAPS.DestBlendCaps and d3DCAPS.SrcBlendCaps and 0x20) == 0) {
					throw RuntimeException("")
				} else if ((d3DCAPS.SrcBlendCaps and d3DCAPS.DestBlendCaps and 0x2) == 0) {
					throw RuntimeException("")
				} else if (d3DCAPS.MaxActiveLights > 0 && d3DCAPS.MaxActiveLights < 2) {
					throw RuntimeException("")
				} else if (d3DCAPS.MaxStreams >= 5) {
					@Pc(181) val d3DPRESENT_PARAMETERS = D3DPRESENT_PARAMETERS(canvas)
					if (!D3DEnumTranslator.selectPresentFormats(d3DPRESENT_PARAMETERS, adapter.toInt(), iDirect3D, deviceType.toInt(), integer!!)) {
						throw RuntimeException("")
					}
					d3DPRESENT_PARAMETERS.PresentationInterval = Int.MIN_VALUE
					d3DPRESENT_PARAMETERS.EnableAutoDepthStencil = true
					d3DPRESENT_PARAMETERS.Windowed = true
					if (ClientFeatures.scaled()) {
						// Not part of the original client: with client.scale the back buffer is the game's
						// size, and Present stretches it over the larger window.
						val gameSize: Dimension = canvas!!.getSize()
						d3DPRESENT_PARAMETERS.BackBufferWidth = gameSize.width
						d3DPRESENT_PARAMETERS.BackBufferHeight = gameSize.height
					}
					@Pc(206) var behaviourFlags = 2
					if ((d3DCAPS.DevCaps and 0x100000) != 0) {
						behaviourFlags = behaviourFlags or 0x10
					}
					@Pc(230) var iDirect3DDevice: IDirect3DDevice?
					try {
						iDirect3DDevice = iDirect3D.a(adapter.toInt(), deviceType.toInt(), canvas, behaviourFlags or 0x40, d3DPRESENT_PARAMETERS)
					} catch (@Pc(232) ex: fda) {
						iDirect3DDevice = iDirect3D.a(adapter.toInt(), deviceType.toInt(), canvas, behaviourFlags or 0x20, d3DPRESENT_PARAMETERS)
					}
					@Pc(251) val swapChain = D3DSwapChain(iDirect3DDevice!!.b(0), iDirect3DDevice.c())
					d3DToolkit = D3DToolkit(adapter.toInt(), deviceType.toInt(), canvas, peerTracker, iDirect3D, iDirect3DDevice, swapChain, d3DPRESENT_PARAMETERS, d3DCAPS, textureSource, shaderResource, integer)
					d3DToolkit.initializeTables()
					return d3DToolkit
				} else {
					throw RuntimeException("")
				}
			} catch (@Pc(274) ex: RuntimeException) {
				if (d3DToolkit != null) {
					d3DToolkit.stop()
				}
				throw ex
			}
		}
	}
}
