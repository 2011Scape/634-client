/*
 * Not part of the original client: 634HD (toolkit 6), copied from OpenGLTextureCache (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider

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
			val textureSize: Int = if (textureMetrics!!.isSmall) 64 else this.toolkit!!.size
			val texture: Hd2DTexture
			if (textureMetrics.hdr && this.toolkit!!.bloom()) {
				val hdrPixels: FloatArray? = this.materialProvider.floatArgbOutput(textureSize, 0.7f, textureSize, textureId)
				texture = Hd2DTexture(this.toolkit, 3553, 34842, textureSize, textureSize, textureMetrics.mipmapping.toInt() != 0, hdrPixels, 6408)
			} else {
				val pixels: IntArray?
				if (textureMetrics.effectCombiner != 2 && MaterialType.isOpaqueEffect(textureMetrics.effectType.toInt())) {
					pixels = this.materialProvider.getPixels(true, textureId, 0.7f, textureSize, textureSize)
				} else {
					pixels = this.materialProvider.getPixelsArgb(textureSize, 0.7f, textureId, textureSize)
				}
				texture = Hd2DTexture(this.toolkit, 3553, 6408, textureSize, textureSize, textureMetrics.mipmapping.toInt() != 0, pixels, 0, 0, false)
			}
			texture.setRepeat(textureMetrics.repeatT, textureMetrics.repeatS)
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
