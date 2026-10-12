package com.jagex.game.runetek5.toolkit.opengl

import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!eo")
class OpenGLTextureCache {

	@OriginalMember(owner = "client!eo", name = "j", descriptor = "Lclient!jr;")
	private val textureCache: LRUCache = LRUCache(256)

	@OriginalMember(owner = "client!eo", name = "d", descriptor = "Lclient!qo;")
	private val toolkit: GlToolkit?

	@OriginalMember(owner = "client!eo", name = "h", descriptor = "Lclient!d;")
	private val materialProvider: MaterialProvider?

	@OriginalMember(owner = "client!eo", name = "<init>", descriptor = "(Lclient!qo;Lclient!d;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) textureSource: MaterialProvider?) {
		this.toolkit = glToolkit
		this.materialProvider = textureSource
	}

	@OriginalMember(owner = "client!eo", name = "a", descriptor = "(B)V")
	fun reset() {
		this.textureCache.reset()
	}

	@OriginalMember(owner = "client!eo", name = "a", descriptor = "(II)Lclient!tp;")
	fun getTexture(@OriginalArg(0) textureId: Int): OpenGl2DTexture? {
		@Pc(12) val `object`: Any? = this.textureCache.get(textureId.toLong())
		if (`object` != null) {
			return `object` as OpenGl2DTexture
		} else if (this.materialProvider!!.textureAvailable(textureId)) {
			@Pc(31) val textureMetrics: MaterialType? = this.materialProvider.getMetrics(textureId)
			@Pc(40) val textureSize: Int = if (textureMetrics!!.isSmall) 64 else this.toolkit!!.size
			@Pc(78) val texture: OpenGl2DTexture
			if (textureMetrics.hdr && this.toolkit!!.bloom()) {
				@Pc(57) val hdrPixels: FloatArray? = this.materialProvider.floatArgbOutput(textureSize, 0.7f, textureSize, textureId)
				texture = OpenGl2DTexture(this.toolkit, 3553, 34842, textureSize, textureSize, textureMetrics.mipmapping.toInt() != 0, hdrPixels, 6408)
			} else {
				@Pc(101) val pixels: IntArray?
				if (textureMetrics.effectCombiner != 2 && MaterialType.isOpaqueEffect(textureMetrics.effectType.toInt())) {
					pixels = this.materialProvider.getPixels(true, textureId, 0.7f, textureSize, textureSize)
				} else {
					pixels = this.materialProvider.getPixelsArgb(textureSize, 0.7f, textureId, textureSize)
				}
				texture = OpenGl2DTexture(this.toolkit, 3553, 6408, textureSize, textureSize, textureMetrics.mipmapping.toInt() != 0, pixels, 0, 0, false)
			}
			texture.setRepeat(textureMetrics.repeatT, textureMetrics.repeatS)
			this.textureCache.put(texture, textureId.toLong())
			return texture
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!eo", name = "c", descriptor = "(I)V")
	fun clean() {
		this.textureCache.clean(5)
	}
}
