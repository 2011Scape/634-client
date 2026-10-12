package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.GpuEnvironmentSampler
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!kv")
class NativeCubeTextureGenerator : GpuEnvironmentSampler {

	@OriginalMember(owner = "client!kv", name = "t", descriptor = "Lclient!nj;")
	private var nativeCubeTexture: CubeTextureSampler? = null

	@OriginalMember(owner = "client!kv", name = "p", descriptor = "I")
	private val material3: Int

	@OriginalMember(owner = "client!kv", name = "i", descriptor = "I")
	private val material5: Int

	@OriginalMember(owner = "client!kv", name = "q", descriptor = "I")
	private val material1: Int

	@OriginalMember(owner = "client!kv", name = "l", descriptor = "I")
	private val material4: Int

	@OriginalMember(owner = "client!kv", name = "r", descriptor = "I")
	private val material2: Int

	@OriginalMember(owner = "client!kv", name = "o", descriptor = "I")
	private val material6: Int

	@OriginalMember(owner = "client!kv", name = "s", descriptor = "Lclient!wga;")
	private val nativeToolkit: AbstractNativeToolkit?

	@OriginalMember(owner = "client!kv", name = "<init>", descriptor = "(Lclient!wga;IIIIII)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(1) material1: Int, @OriginalArg(2) material2: Int, @OriginalArg(3) material3: Int, @OriginalArg(4) material4: Int, @OriginalArg(5) material5: Int, @OriginalArg(6) material6: Int) : super() {
		this.material3 = material3
		this.material5 = material5
		this.material1 = material1
		this.material4 = material4
		this.material2 = material2
		this.material6 = material6
		this.nativeToolkit = nativeToolkit
	}

	@OriginalMember(owner = "client!kv", name = "a", descriptor = "(Z)Lclient!nj;")
	override fun getNativeCubeTexture(): CubeTextureSampler? {
		if (this.nativeCubeTexture == null) {
			NativeCubeTextureGenerator.faceMaterialIds[3] = this.material4
			NativeCubeTextureGenerator.faceMaterialIds[1] = this.material2
			NativeCubeTextureGenerator.faceMaterialIds[4] = this.material5
			@Pc(32) val textureSource: MaterialProvider? = this.nativeToolkit!!.materialProvider
			NativeCubeTextureGenerator.faceMaterialIds[5] = this.material6
			NativeCubeTextureGenerator.faceMaterialIds[2] = this.material3
			NativeCubeTextureGenerator.faceMaterialIds[0] = this.material1
			@Pc(49) var mipmapping: Boolean = false
			@Pc(51) var largestSize: Int = 0
			@Pc(53) var i: Int = 0
			while (i < 6) {
				if (!textureSource!!.textureAvailable(NativeCubeTextureGenerator.faceMaterialIds[i])) {
					return null
				}
				@Pc(71) val textureMetrics: MaterialType? = textureSource.getMetrics(NativeCubeTextureGenerator.faceMaterialIds[i])
				@Pc(78) val size: Int = if (textureMetrics!!.isSmall) 64 else 128
				if (size > largestSize) {
					largestSize = size
				}
				if (textureMetrics.mipmapping > 0) {
					mipmapping = true
				}
				i++
			}
			@Pc(103) var j: Int = 0
			while (j < 6) {
				NativeCubeTextureGenerator.facePixels[j] = textureSource!!.getPixels(false, NativeCubeTextureGenerator.faceMaterialIds[j], 1.0f, largestSize, largestSize)
				j++
			}
			this.nativeCubeTexture = this.nativeToolkit.generateNativeCubeTexture(NativeCubeTextureGenerator.facePixels, mipmapping, largestSize)
		}
		return this.nativeCubeTexture
	}

	companion object {
		@OriginalMember(owner = "client!us", name = "b", descriptor = "[I")
		@JvmField
		val faceMaterialIds: IntArray = IntArray(6)
		@OriginalMember(owner = "client!pp", name = "x", descriptor = "[[I")
		@JvmField
		val facePixels: Array<IntArray?> = arrayOfNulls<IntArray>(6)
	}
}
