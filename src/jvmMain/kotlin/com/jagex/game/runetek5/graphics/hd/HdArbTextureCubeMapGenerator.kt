/*
 * Not part of the original client: 634HD (toolkit 6), copied from OpenGLArbTextureCubeMapGenerator (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider
import kotlin.jvm.JvmField

class HdArbTextureCubeMapGenerator : HdEnvironmentSampler {

	private var cubeTexture: HdCubeTexture? = null

	private val material3: Int

	private val material4: Int

	private val material6: Int

	private val material1: Int

	private val material2: Int

	private val material5: Int

	private val glToolkit: HdToolkit?

	constructor(glToolkit: HdToolkit?, material1: Int, material2: Int, material3: Int, material4: Int, material5: Int, material6: Int) : super() {
		this.material3 = material3
		this.material4 = material4
		this.material6 = material6
		this.material1 = material1
		this.material2 = material2
		this.material5 = material5
		this.glToolkit = glToolkit
	}

	override fun getCubeTexture(): HdCubeTexture? {
		if (this.cubeTexture == null) {
			val textureSource: MaterialProvider? = this.glToolkit!!.materialProvider
			HdArbTextureCubeMapGenerator.faceMaterialIds[3] = this.material4
			HdArbTextureCubeMapGenerator.faceMaterialIds[4] = this.material5
			HdArbTextureCubeMapGenerator.faceMaterialIds[2] = this.material3
			HdArbTextureCubeMapGenerator.faceMaterialIds[5] = this.material6
			HdArbTextureCubeMapGenerator.faceMaterialIds[1] = this.material2
			HdArbTextureCubeMapGenerator.faceMaterialIds[0] = this.material1
			var mipmapping: Boolean = false
			var largestSize: Int = 0
			var i: Int = 0
			while (i < 6) {
				if (!textureSource!!.textureAvailable(HdArbTextureCubeMapGenerator.faceMaterialIds[i])) {
					return null
				}
				val textureMetrics: MaterialType? = textureSource.getMetrics(HdArbTextureCubeMapGenerator.faceMaterialIds[i])
				val size: Int = if (textureMetrics!!.isSmall) 64 else 128
				if (largestSize < size) {
					largestSize = size
				}
				if (textureMetrics.mipmapping > 0) {
					mipmapping = true
				}
				i++
			}
			var j: Int = 0
			while (j < 6) {
				HdArbTextureCubeMapGenerator.faceTexturePixels[j] = textureSource!!.getPixels(false, HdArbTextureCubeMapGenerator.faceMaterialIds[j], 1.0f, largestSize, largestSize)
				j++
			}
			this.cubeTexture = HdCubeTexture(this.glToolkit, 6407, largestSize, mipmapping, HdArbTextureCubeMapGenerator.faceTexturePixels)
		}
		return this.cubeTexture
	}

	companion object {
		@JvmField
		val faceTexturePixels: Array<IntArray?> = arrayOfNulls<IntArray>(6)
		@JvmField
		val faceMaterialIds: IntArray = IntArray(6)
	}
}
