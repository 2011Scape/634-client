/*
 * Not part of the original client: 634HD (toolkit 6), copied from OpenGLTextureCache (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.client.features.HdSettings
import com.jagex.game.runetek5.graphics.hd.scene.HdMaterials

class HdTextureCache {

	private val textureCache: LRUCache = LRUCache(256)

	private val toolkit: HdToolkit?

	private val materialProvider: MaterialProvider?

	constructor(glToolkit: HdToolkit?, textureSource: MaterialProvider?) {
		this.toolkit = glToolkit
		this.materialProvider = textureSource
	}

	fun reset() {
		this.textureCache.reset()
	}

	fun getTexture(textureId: Int): Hd2DTexture? {
		val `object`: Any? = this.textureCache.get(textureId.toLong())
		if (`object` != null) {
			return `object` as Hd2DTexture
		} else if (this.materialProvider!!.textureAvailable(textureId)) {
			val textureMetrics: MaterialType? = this.materialProvider.getMetrics(textureId)
			// 634HD: a fresh texture (client.hd.textureReplacements) is made at its own, finer size
			val fresh: Boolean = (this.materialProvider as? com.jagex.game.runetek5.graphics.hd.content.HdMaterialProvider)?.fresh(textureId) == true
			val small: Boolean = textureMetrics!!.isSmall
			val textureSize: Int = if (fresh) com.jagex.game.runetek5.graphics.hd.scene.HdMaterials.FINE_SIZE else if (small) 64 else this.toolkit!!.size
			// 634HD: every world texture mipmapped with client.hd.mipmaps
			val mipmapped: Boolean = textureMetrics.mipmapping.toInt() != 0 || HdSettings.mipmaps
			val texture: Hd2DTexture
			if (textureMetrics.hdr && this.toolkit!!.bloom()) {
				val hdrPixels: FloatArray? = this.materialProvider.floatArgbOutput(textureSize, 0.7f, textureSize, textureId)
				texture = Hd2DTexture(this.toolkit, 3553, 34842, textureSize, textureSize, mipmapped, hdrPixels, 6408)
			} else {
				val pixels: IntArray?
				if (textureMetrics.effectCombiner != 2 && MaterialType.isOpaqueEffect(textureMetrics.effectType.toInt())) {
					pixels = this.materialProvider.getPixels(true, textureId, 0.7f, textureSize, textureSize)
				} else {
					pixels = this.materialProvider.getPixelsArgb(textureSize, 0.7f, textureId, textureSize)
				}
				texture = Hd2DTexture(this.toolkit, 3553, 6408, textureSize, textureSize, mipmapped, pixels, 0, 0, false)
			}
			texture.setRepeat(textureMetrics.repeatT, textureMetrics.repeatS)
			// 634HD: anisotropic filtering (client.hd.anisotropy), on the texture as it is bound
			val anisotropy: Float = HdMaterials.anisotropy(HdSettings.anisotropy)
			if (anisotropy > 0.0f && mipmapped) {
				this.toolkit!!.setActiveTexture(texture)
				com.jagex.game.runetek5.graphics.hd.gl.HdGl.glTexParameterf(3553, HdMaterials.TEXTURE_MAX_ANISOTROPY, anisotropy)
			}
			this.textureCache.put(texture, textureId.toLong())
			return texture
		} else {
			return null
		}
	}

	fun clean() {
		this.textureCache.clean(5)
	}
}
