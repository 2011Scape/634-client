package com.jagex.game.runetek5

import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.gl.GlCubeTexture
import com.jagex.game.runetek5.graphics.gl.GlEnvironmentSampler
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!dq")
class OpenGLArbTextureCubeMapGenerator : GlEnvironmentSampler {

	@OriginalMember(owner = "client!dq", name = "j", descriptor = "Lclient!q;")
	private var cubeTexture: GlCubeTexture? = null

	@OriginalMember(owner = "client!dq", name = "m", descriptor = "I")
	private val material3: Int

	@OriginalMember(owner = "client!dq", name = "l", descriptor = "I")
	private val material4: Int

	@OriginalMember(owner = "client!dq", name = "s", descriptor = "I")
	private val material6: Int

	@OriginalMember(owner = "client!dq", name = "k", descriptor = "I")
	private val material1: Int

	@OriginalMember(owner = "client!dq", name = "q", descriptor = "I")
	private val material2: Int

	@OriginalMember(owner = "client!dq", name = "h", descriptor = "I")
	private val material5: Int

	@OriginalMember(owner = "client!dq", name = "o", descriptor = "Lclient!qo;")
	private val glToolkit: GlToolkit?

	@OriginalMember(owner = "client!dq", name = "<init>", descriptor = "(Lclient!qo;IIIIII)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) material1: Int, @OriginalArg(2) material2: Int, @OriginalArg(3) material3: Int, @OriginalArg(4) material4: Int, @OriginalArg(5) material5: Int, @OriginalArg(6) material6: Int) : super() {
		this.material3 = material3
		this.material4 = material4
		this.material6 = material6
		this.material1 = material1
		this.material2 = material2
		this.material5 = material5
		this.glToolkit = glToolkit
	}

	@OriginalMember(owner = "client!dq", name = "a", descriptor = "(B)Lclient!q;")
	override fun getCubeTexture(): GlCubeTexture? {
		if (this.cubeTexture == null) {
			@Pc(19) val textureSource: MaterialProvider? = this.glToolkit!!.materialProvider
			OpenGLArbTextureCubeMapGenerator.faceMaterialIds[3] = this.material4
			OpenGLArbTextureCubeMapGenerator.faceMaterialIds[4] = this.material5
			OpenGLArbTextureCubeMapGenerator.faceMaterialIds[2] = this.material3
			OpenGLArbTextureCubeMapGenerator.faceMaterialIds[5] = this.material6
			OpenGLArbTextureCubeMapGenerator.faceMaterialIds[1] = this.material2
			OpenGLArbTextureCubeMapGenerator.faceMaterialIds[0] = this.material1
			@Pc(51) var mipmapping: Boolean = false
			@Pc(53) var largestSize: Int = 0
			@Pc(55) var i: Int = 0
			while (i < 6) {
				if (!textureSource!!.textureAvailable(OpenGLArbTextureCubeMapGenerator.faceMaterialIds[i])) {
					return null
				}
				@Pc(75) val textureMetrics: MaterialType? = textureSource.getMetrics(OpenGLArbTextureCubeMapGenerator.faceMaterialIds[i])
				@Pc(82) val size: Int = if (textureMetrics!!.isSmall) 64 else 128
				if (largestSize < size) {
					largestSize = size
				}
				if (textureMetrics.mipmapping > 0) {
					mipmapping = true
				}
				i++
			}
			@Pc(101) var j: Int = 0
			while (j < 6) {
				OpenGLArbTextureCubeMapGenerator.faceTexturePixels[j] = textureSource!!.getPixels(false, OpenGLArbTextureCubeMapGenerator.faceMaterialIds[j], 1.0f, largestSize, largestSize)
				j++
			}
			this.cubeTexture = GlCubeTexture(this.glToolkit, 6407, largestSize, mipmapping, OpenGLArbTextureCubeMapGenerator.faceTexturePixels)
		}
		return this.cubeTexture
	}

	companion object {
		@OriginalMember(owner = "client!ec", name = "h", descriptor = "[[I")
		@JvmField
		val faceTexturePixels: Array<IntArray?> = arrayOfNulls<IntArray>(6)
		@OriginalMember(owner = "client!wu", name = "e", descriptor = "[I")
		@JvmField
		val faceMaterialIds: IntArray = IntArray(6)
	}
}
