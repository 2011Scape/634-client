/*
 * Not part of the original client: 634HD (toolkit 6), copied from TextureContainerGL (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.graphics.texture.water.GLBillowTextureData
import com.jagex.game.runetek5.graphics.texture.water.NativeWaveTextureData
import com.jagex.game.runetek5.graphics.texture.water.NoiseGenerator
import com.jagex.game.runetek5.graphics.texture.water.PerlinNoiseGenerator

import com.jagex.game.runetek5.ByteArrayWrapper
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

class HdTextureContainer {

	@JvmField
	var aClass85_Sub2_2: HdVolumeTexture? = null

	@JvmField
	var waveTextures2d: Array<Hd2DTexture?>? = null

	@JvmField
	var texture3d: HdVolumeTexture? = null

	@JvmField
	var waveTexture3d: HdVolumeTexture? = null

	@JvmField
	var billowTextures2d: Array<Hd2DTexture?>? = null

	@JvmField
	val texture3dSupported: Boolean

	constructor(glToolkit: HdToolkit?) {
		this.texture3dSupported = glToolkit!!.texture3dSupported
		HdTextureContainer.initializeTextureData(glToolkit)
		if (this.texture3dSupported) {
			var data: ByteArray? = ByteArrayWrapper.unwrap(false, HdTextureContainer.waveTextureBuffer)
			this.waveTexture3d = HdVolumeTexture(glToolkit, 6410, 128, 128, 16, data, 6410)
			val billowData: ByteArray? = ByteArrayWrapper.unwrap(false, HdTextureContainer.billowTextureBuffer)
			this.texture3d = HdVolumeTexture(glToolkit, 6410, 128, 128, 16, billowData, 6410)
			val shader: HdTexture3DShader = glToolkit.texture3dShader
			if (shader.createShader()) {
				data = ByteArrayWrapper.unwrap(false, HdTextureContainer.noiseTextureBuffer)
				this.aClass85_Sub2_2 = HdVolumeTexture(glToolkit, 6408, 128, 128, 16)
				val glVolumeTexture: HdVolumeTexture = HdVolumeTexture(glToolkit, 6409, 128, 128, 16, data, 6409)
				if (shader.activate(this.aClass85_Sub2_2, glVolumeTexture, 2.0f)) {
					this.aClass85_Sub2_2!!.generateMipmaps()
				} else {
					this.aClass85_Sub2_2!!.delete()
					this.aClass85_Sub2_2 = null
				}
				glVolumeTexture.delete()
				return
			}
		} else {
			this.waveTextures2d = arrayOfNulls<Hd2DTexture>(16)
			var i: Int = 0
			while (i < 16) {
				val waveFrame: ByteArray? = ByteArrayWrapper.unwrapBlock(HdTextureContainer.waveTextureBuffer, 2 * 128 * i * 128)
				this.waveTextures2d!![i] = Hd2DTexture(glToolkit, 3553, 6410, 128, 128, true, waveFrame, 6410, false)
				i++
			}
			this.billowTextures2d = arrayOfNulls<Hd2DTexture>(16)
			var j: Int = 0
			while (j < 16) {
				val billowFrame: ByteArray? = ByteArrayWrapper.unwrapBlock(HdTextureContainer.billowTextureBuffer, j * 2 * 16384)
				this.billowTextures2d!![j] = Hd2DTexture(glToolkit, 3553, 6410, 128, 128, true, billowFrame, 6410, false)
				j++
			}
		}
	}

	companion object {
		@JvmField
		var noiseTextureBuffer: Any? = null

		@JvmField
		var billowTextureBuffer: Any? = null

		@JvmStatic
		fun initializeTextureData(glToolkit: HdToolkit?) {
			var data: ByteArray?
			if (HdTextureContainer.waveTextureBuffer == null) {
				val nativeRippleNoiseTexture: NativeWaveTextureData = NativeWaveTextureData()
				data = nativeRippleNoiseTexture.getData()
				HdTextureContainer.waveTextureBuffer = ByteArrayWrapper.wrap(data)
			}
			if (HdTextureContainer.billowTextureBuffer == null) {
				val billowTextureData: GLBillowTextureData = GLBillowTextureData()
				data = billowTextureData.getData()
				HdTextureContainer.billowTextureBuffer = ByteArrayWrapper.wrap(data)
			}
			val shader: HdTexture3DShader = glToolkit!!.texture3dShader
			if (shader.createShader() && HdTextureContainer.noiseTextureBuffer == null) {
				data = NoiseGenerator.generateHeightData(0.6f, 4.0f, 4.0f, 0.5f, 16.0f, PerlinNoiseGenerator(419684))
				HdTextureContainer.noiseTextureBuffer = ByteArrayWrapper.wrap(data)
			}
		}

		@JvmField
		var waveTextureBuffer: Any? = null
	}
}
