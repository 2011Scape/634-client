package com.jagex.game.runetek5.graphics.texture.water

import com.jagex.game.runetek5.ByteArrayWrapper
import com.jagex.game.runetek5.GlVolumeTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture
import com.jagex.game.runetek5.graphics.shader.Texture3DShaderGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!lc")
class TextureContainerGL {

	@OriginalMember(owner = "client!lc", name = "d", descriptor = "Lclient!gj;")
	@JvmField
	var aClass85_Sub2_2: GlVolumeTexture? = null

	@OriginalMember(owner = "client!lc", name = "e", descriptor = "[Lclient!tp;")
	@JvmField
	var waveTextures2d: Array<OpenGl2DTexture?>? = null

	@OriginalMember(owner = "client!lc", name = "g", descriptor = "Lclient!gj;")
	@JvmField
	var texture3d: GlVolumeTexture? = null

	@OriginalMember(owner = "client!lc", name = "a", descriptor = "Lclient!gj;")
	@JvmField
	var waveTexture3d: GlVolumeTexture? = null

	@OriginalMember(owner = "client!lc", name = "i", descriptor = "[Lclient!tp;")
	@JvmField
	var billowTextures2d: Array<OpenGl2DTexture?>? = null

	@OriginalMember(owner = "client!lc", name = "c", descriptor = "Z")
	@JvmField
	val texture3dSupported: Boolean

	@OriginalMember(owner = "client!lc", name = "<init>", descriptor = "(Lclient!qo;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?) {
		this.texture3dSupported = glToolkit!!.texture3dSupported
		TextureContainerGL.initializeTextureData(glToolkit)
		if (this.texture3dSupported) {
			@Pc(33) var data: ByteArray? = ByteArrayWrapper.unwrap(false, TextureContainerGL.waveTextureBuffer)
			this.waveTexture3d = GlVolumeTexture(glToolkit, 6410, 128, 128, 16, data, 6410)
			@Pc(50) val billowData: ByteArray? = ByteArrayWrapper.unwrap(false, TextureContainerGL.billowTextureBuffer)
			this.texture3d = GlVolumeTexture(glToolkit, 6410, 128, 128, 16, billowData, 6410)
			@Pc(65) val shader: Texture3DShaderGL = glToolkit.texture3dShader
			if (shader.createShader()) {
				data = ByteArrayWrapper.unwrap(false, TextureContainerGL.noiseTextureBuffer)
				this.aClass85_Sub2_2 = GlVolumeTexture(glToolkit, 6408, 128, 128, 16)
				@Pc(95) val glVolumeTexture: GlVolumeTexture = GlVolumeTexture(glToolkit, 6409, 128, 128, 16, data, 6409)
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
			this.waveTextures2d = arrayOfNulls<OpenGl2DTexture>(16)
			@Pc(127) var i: Int = 0
			while (i < 16) {
				@Pc(141) val waveFrame: ByteArray? = ByteArrayWrapper.unwrapBlock(TextureContainerGL.waveTextureBuffer, 2 * 128 * i * 128)
				this.waveTextures2d!![i] = OpenGl2DTexture(glToolkit, 3553, 6410, 128, 128, true, waveFrame, 6410, false)
				i++
			}
			this.billowTextures2d = arrayOfNulls<OpenGl2DTexture>(16)
			@Pc(167) var j: Int = 0
			while (j < 16) {
				@Pc(179) val billowFrame: ByteArray? = ByteArrayWrapper.unwrapBlock(TextureContainerGL.billowTextureBuffer, j * 2 * 16384)
				this.billowTextures2d!![j] = OpenGl2DTexture(glToolkit, 3553, 6410, 128, 128, true, billowFrame, 6410, false)
				j++
			}
		}
	}

	companion object {
		@OriginalMember(owner = "client!ou", name = "x", descriptor = "Ljava/lang/Object;")
		@JvmField
		var noiseTextureBuffer: Any? = null

		@OriginalMember(owner = "client!cn", name = "h", descriptor = "Ljava/lang/Object;")
		@JvmField
		var billowTextureBuffer: Any? = null

		@OriginalMember(owner = "client!sa", name = "a", descriptor = "(ILclient!qo;)V")
		@JvmStatic
		fun initializeTextureData(@OriginalArg(1) glToolkit: GlToolkit?) {
			@Pc(16) var data: ByteArray?
			if (TextureContainerGL.waveTextureBuffer == null) {
				@Pc(9) val nativeRippleNoiseTexture: NativeWaveTextureData = NativeWaveTextureData()
				data = nativeRippleNoiseTexture.getData()
				TextureContainerGL.waveTextureBuffer = ByteArrayWrapper.wrap(data)
			}
			if (TextureContainerGL.billowTextureBuffer == null) {
				@Pc(27) val billowTextureData: GLBillowTextureData = GLBillowTextureData()
				data = billowTextureData.getData()
				TextureContainerGL.billowTextureBuffer = ByteArrayWrapper.wrap(data)
			}
			@Pc(49) val shader: Texture3DShaderGL = glToolkit!!.texture3dShader
			if (shader.createShader() && TextureContainerGL.noiseTextureBuffer == null) {
				data = NoiseGenerator.generateHeightData(0.6f, 4.0f, 4.0f, 0.5f, 16.0f, PerlinNoiseGenerator(419684))
				TextureContainerGL.noiseTextureBuffer = ByteArrayWrapper.wrap(data)
			}
		}

		@OriginalMember(owner = "client!dw", name = "e", descriptor = "Ljava/lang/Object;")
		@JvmField
		var waveTextureBuffer: Any? = null
	}
}
