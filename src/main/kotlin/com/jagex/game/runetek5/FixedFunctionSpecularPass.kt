package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!bk")
class FixedFunctionSpecularPass : RenderPass {

	@OriginalMember(owner = "client!bk", name = "r", descriptor = "Z")
	private var active: Boolean = false

	@OriginalMember(owner = "client!bk", name = "l", descriptor = "Z")
	private var singleStage: Boolean = false

	@OriginalMember(owner = "client!bk", name = "p", descriptor = "[Lclient!nj;")
	private var specularMaps: Array<CubeTextureSampler?>? = null

	@OriginalMember(owner = "client!bk", name = "<init>", descriptor = "(Lclient!wga;)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?) : super(nativeToolkit) {
		if (nativeToolkit!!.cubeMapSupported) {
			this.singleStage = nativeToolkit.maxConcurrentTextures < 3
			@Pc(28) val maxIntensity: Int = if (this.singleStage) 48 else 127
			@Pc(32) val broadSpecularFaces: Array<IntArray?> = Array<IntArray?>(6) { IntArray(4096) }
			@Pc(36) val sharpSpecularFaces: Array<IntArray?> = Array<IntArray?>(6) { IntArray(4096) }
			@Pc(40) val mediumSpecularFaces: Array<IntArray?> = Array<IntArray?>(6) { IntArray(4096) }
			@Pc(42) var texelIndex: Int = 0
			for (@Pc(44) i in 0 until 64) {
				for (@Pc(48) j in 0 until 64) {
					@Pc(59) val u: Float = i.toFloat() * 2.0f / 64.0f - 1.0f
					@Pc(68) val v: Float = j.toFloat() * 2.0f / 64.0f - 1.0f
					@Pc(83) val normalZ: Float = (1.0 / Math.sqrt((u * u + v * v + 1.0f).toDouble())).toFloat()
					@Pc(87) val normalX: Float = u * normalZ
					@Pc(91) val normalY: Float = v * normalZ
					for (@Pc(93) k in 0 until 6) {
						@Pc(109) val facing: Float
						if (k == 0) {
							facing = -normalY
						} else if (k == 1) {
							facing = normalY
						} else if (k == 2) {
							facing = normalX
						} else if (k == 3) {
							facing = -normalX
						} else if (k == 4) {
							facing = normalZ
						} else {
							facing = -normalZ
						}
						@Pc(150) val sharpIntensity: Int
						@Pc(149) val mediumIntensity: Int
						@Pc(147) val broadIntensity: Int
						if (facing > 0.0f) {
							sharpIntensity = (maxIntensity.toDouble() * Math.pow(facing.toDouble(), 96.0)).toInt()
							mediumIntensity = (maxIntensity.toDouble() * Math.pow(facing.toDouble(), 36.0)).toInt()
							broadIntensity = (Math.pow(facing.toDouble(), 12.0) * maxIntensity.toDouble()).toInt()
						} else {
							broadIntensity = 0
							mediumIntensity = 0
							sharpIntensity = 0
						}
						sharpSpecularFaces[k]!![texelIndex] = sharpIntensity shl 24
						mediumSpecularFaces[k]!![texelIndex] = mediumIntensity shl 24
						broadSpecularFaces[k]!![texelIndex] = broadIntensity shl 24
					}
					texelIndex++
				}
			}
			this.specularMaps = arrayOfNulls<CubeTextureSampler>(3)
			this.specularMaps!![0] = super.nativeToolkit!!.generateNativeCubeTexture(sharpSpecularFaces, false, 64)
			this.specularMaps!![1] = super.nativeToolkit!!.generateNativeCubeTexture(mediumSpecularFaces, false, 64)
			this.specularMaps!![2] = super.nativeToolkit!!.generateNativeCubeTexture(broadSpecularFaces, false, 64)
		}
	}

	@OriginalMember(owner = "client!bk", name = "e", descriptor = "(I)Z")
	override fun isSupported(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(IZ)V")
	override fun enable(@OriginalArg(1) lit: Boolean) {
		if (this.specularMaps == null || !lit) {
			super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.PRIMARY_COLOR)
			return
		}
		super.nativeToolkit!!.setTextureSlot(1)
		super.nativeToolkit!!.setTexCoordGeneration(TexCoordGenMode.aClass31_2)
		@Pc(29) val textureMatrix: NativeMatrix? = super.nativeToolkit!!.getCurrentTextureMatrix()
		textureMatrix!!.makeRotationX(1024)
		super.nativeToolkit!!.setTextureState(TextureTransformMode.aClass332_2)
		if (this.singleStage) {
			super.nativeToolkit!!.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_2)
			super.nativeToolkit!!.setTextureColourSource(true, 0, TextureCombineSource.TEXTURE, false)
			super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.PRIMARY_COLOR)
		} else {
			super.nativeToolkit!!.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineFunction.aClass315_4)
			super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS)
			super.nativeToolkit!!.setTextureSlot(2)
			super.nativeToolkit!!.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_2)
			super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS)
			super.nativeToolkit!!.setTextureColourSource(true, 1, TextureCombineSource.PREVIOUS, false)
			super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.PRIMARY_COLOR)
			super.nativeToolkit!!.setTextureSampler(super.nativeToolkit!!.defaultTexture)
		}
		super.nativeToolkit!!.setTextureSlot(0)
		this.active = true
	}

	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(ILclient!du;I)V")
	override fun setBaseTexture(@OriginalArg(0) colourType: Int, @OriginalArg(1) baseTexture: TextureSampler?) {
		super.nativeToolkit!!.setTextureSampler(baseTexture)
		super.nativeToolkit!!.setColourType(colourType)
	}

	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(B)V")
	override fun disable() {
		if (this.active) {
			super.nativeToolkit!!.setTextureSlot(1)
			super.nativeToolkit!!.setTextureSampler(null)
			super.nativeToolkit!!.setTexCoordGeneration(TexCoordGenMode.aClass31_3)
			super.nativeToolkit!!.resetCurrentTextureMatrix()
			if (this.singleStage) {
				super.nativeToolkit!!.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE)
				super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
				super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
			} else {
				super.nativeToolkit!!.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE)
				super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
				super.nativeToolkit!!.setTextureSlot(2)
				super.nativeToolkit!!.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE)
				super.nativeToolkit!!.setTextureRgbStageState(0, TextureCombineSource.TEXTURE)
				super.nativeToolkit!!.setTextureRgbStageState(1, TextureCombineSource.PREVIOUS)
				super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
				super.nativeToolkit!!.setTextureSampler(null)
			}
			super.nativeToolkit!!.setTextureSlot(0)
			this.active = false
		} else {
			super.nativeToolkit!!.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE)
		}
		super.nativeToolkit!!.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE)
	}

	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(ZB)V")
	override fun applyTextureCombine(@OriginalArg(0) lit: Boolean) {
		super.nativeToolkit!!.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineMode.MODULATE)
	}

	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(III)V")
	override fun setEffectParams(@OriginalArg(1) effectParam1: Int, @OriginalArg(2) effectParam2: Int) {
		if (this.active) {
			super.nativeToolkit!!.setTextureSlot(1)
			super.nativeToolkit!!.setTextureSampler(this.specularMaps!![effectParam1 - 1])
			super.nativeToolkit!!.setTextureSlot(0)
		}
	}
}
