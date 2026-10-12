package com.jagex.game.runetek5.graphics.texture.water

import com.jagex.game.runetek5.ByteArrayWrapper
import com.jagex.game.runetek5.PixelFormat
import com.jagex.game.runetek5.Texture3DSampler
import com.jagex.game.runetek5.graphics.GpuTexture
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!ur")
class NativeWaterTexture {

	@OriginalMember(owner = "client!ur", name = "c", descriptor = "Lclient!ir;")
	@JvmField
	var waterSampler3d: Texture3DSampler? = null

	@OriginalMember(owner = "client!ur", name = "f", descriptor = "Lclient!ir;")
	@JvmField
	var normalSampler: Texture3DSampler? = null

	@OriginalMember(owner = "client!ur", name = "e", descriptor = "[Lclient!wfa;")
	private var textureSamplers: Array<GpuTexture?>? = null

	@OriginalMember(owner = "client!ur", name = "j", descriptor = "[Lclient!wfa;")
	@JvmField
	var waterTextureSamplers: Array<GpuTexture?>? = null

	@OriginalMember(owner = "client!ur", name = "h", descriptor = "Lclient!wga;")
	private val activeToolkit: AbstractNativeToolkit

	@OriginalMember(owner = "client!ur", name = "i", descriptor = "Z")
	@JvmField
	var hasDepth: Boolean = false

	@OriginalMember(owner = "client!ur", name = "<init>", descriptor = "(Lclient!wga;)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?) {
		this.activeToolkit = nativeToolkit!!
		this.hasDepth = this.activeToolkit.SUPPORTS_TEXTURE3D
		if (this.hasDepth && !this.activeToolkit.supports3DTextureFormat(PixelFormat.aClass375_39, GLFormat.RGBA)) {
			this.hasDepth = false
		}
		if (this.hasDepth || this.activeToolkit.supportsTextureFormat(GLFormat.RGBA, PixelFormat.aClass375_39)) {
			NativeWaterTexture.generateTextureData()
			if (this.hasDepth) {
				@Pc(134) val waveData: ByteArray? = ByteArrayWrapper.unwrap(false, NativeWaterTexture.waveTextureDataBuffer)
				this.waterSampler3d = this.activeToolkit.create3DTexture(PixelFormat.aClass375_39, waveData)
				@Pc(150) val billowData: ByteArray? = ByteArrayWrapper.unwrap(false, NativeWaterTexture.billowTextureDataBuffer)
				this.activeToolkit.create3DTexture(PixelFormat.aClass375_39, billowData)
			} else {
				this.waterTextureSamplers = arrayOfNulls<GpuTexture>(16)
				@Pc(58) var i: Int = 0
				while (i < 16) {
					@Pc(72) val waveFrame: ByteArray? = ByteArrayWrapper.unwrapBlock(NativeWaterTexture.waveTextureDataBuffer, i * 128 * 128 * 2)
					this.waterTextureSamplers!![i] = this.activeToolkit.createTextureSampler(waveFrame, 128, PixelFormat.aClass375_39, true, 128)
					i++
				}
				this.textureSamplers = arrayOfNulls<GpuTexture>(16)
				@Pc(95) var j: Int = 0
				while (j < 16) {
					@Pc(109) val billowFrame: ByteArray? = ByteArrayWrapper.unwrapBlock(NativeWaterTexture.billowTextureDataBuffer, 128 * 128 * j * 2)
					this.textureSamplers!![j] = this.activeToolkit.createTextureSampler(billowFrame, 128, PixelFormat.aClass375_39, true, 128)
					j++
				}
			}
		}
	}

	@OriginalMember(owner = "client!ur", name = "a", descriptor = "(I)Z")
	fun readyToSample(): Boolean {
		if (this.hasDepth) {
			return this.waterSampler3d != null
		} else {
			return this.waterTextureSamplers != null
		}
	}

	@OriginalMember(owner = "client!ur", name = "a", descriptor = "(B)Z")
	fun loadNormalMap(): Boolean {
		if (this.normalSampler == null) {
			@Pc(27) var heightData: ByteArray?
			if (NativeWaterTexture.hdrDatabuffer == null) {
				heightData = NoiseGenerator.generateHeightData(0.6f, 4.0f, 4.0f, 0.5f, 16.0f, PerlinNoiseGenerator(419684))
				NativeWaterTexture.hdrDatabuffer = ByteArrayWrapper.wrap(heightData)
			}
			heightData = ByteArrayWrapper.unwrap(false, NativeWaterTexture.hdrDatabuffer)
			@Pc(43) val normals: ByteArray = ByteArray(heightData!!.size * 4)
			@Pc(45) var outIndex: Int = 0
			@Pc(47) var i: Int = 0
			while (i < 16) {
				@Pc(55) var sourceIndex: Int = i * 128 * 128
				@Pc(57) val depthOffset: Int = sourceIndex
				@Pc(59) var j: Int = 0
				while (j < 128) {
					@Pc(67) val currentRow: Int = j * 128 + depthOffset
					@Pc(77) val prevRow: Int = ((j - 1) and 0x7F) * 128 + depthOffset
					@Pc(87) val nextRow: Int = depthOffset + ((j + 1) and 0x7F) * 128
					@Pc(89) var k: Int = 0
					while (k < 128) {
						@Pc(108) val verticalGradient: Float = ((heightData[k + prevRow].toInt() and 0xFF) - (heightData[k + nextRow].toInt() and 0xFF)).toFloat()
						@Pc(134) val horizontalGradient: Float = ((heightData[((k - 1) and 0x7F) + currentRow].toInt() and 0xFF) - (heightData[currentRow + ((k + 1) and 0x7F)].toInt() and 0xFF)).toFloat()
						@Pc(149) val normalScale: Float = (128.0 / kotlin.math.sqrt((verticalGradient * verticalGradient + horizontalGradient * horizontalGradient + 16384.0f).toDouble())).toFloat()
						normals[outIndex++] = (horizontalGradient * normalScale + 127.0f).toInt().toByte()
						normals[outIndex++] = (normalScale * 128.0f + 127.0f).toInt().toByte()
						normals[outIndex++] = (normalScale * verticalGradient + 127.0f).toInt().toByte()
						normals[outIndex++] = heightData[sourceIndex++]
						k++
					}
					j++
				}
				i++
			}
			this.normalSampler = this.activeToolkit.create3DTexture(GLFormat.HDR, normals)
		}
		return this.normalSampler != null
	}

	companion object {
		@OriginalMember(owner = "client!sh", name = "d", descriptor = "Ljava/lang/Object;")
		@JvmField
		var billowTextureDataBuffer: Any? = null

		@OriginalMember(owner = "client!aha", name = "e", descriptor = "Ljava/lang/Object;")
		@JvmField
		var waveTextureDataBuffer: Any? = null

		@OriginalMember(owner = "client!uh", name = "i", descriptor = "Ljava/lang/Object;")
		@JvmField
		var hdrDatabuffer: Any? = null

		@OriginalMember(owner = "client!sc", name = "b", descriptor = "(B)V")
		@JvmStatic
		fun generateTextureData() {
			@Pc(22) var data: ByteArray?
			if (NativeWaterTexture.waveTextureDataBuffer == null) {
				@Pc(15) val waveNoiseData: NativeWaveNoiseData = NativeWaveNoiseData()
				data = waveNoiseData.getData()
				NativeWaterTexture.waveTextureDataBuffer = ByteArrayWrapper.wrap(data)
			}
			if (NativeWaterTexture.billowTextureDataBuffer == null) {
				@Pc(37) val billowData: NativeBillowTextureData = NativeBillowTextureData()
				data = billowData.getData()
				NativeWaterTexture.billowTextureDataBuffer = ByteArrayWrapper.wrap(data)
			}
		}
	}
}
