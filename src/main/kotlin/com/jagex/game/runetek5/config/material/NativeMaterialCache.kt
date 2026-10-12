package com.jagex.game.runetek5.config.material

import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.graphics.GpuTexture
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!fw")
class NativeMaterialCache {

	@OriginalMember(owner = "client!fw", name = "f", descriptor = "Lclient!jr;")
	private val cache: LRUCache = LRUCache(256)

	@OriginalMember(owner = "client!fw", name = "a", descriptor = "Lclient!wga;")
	private val toolkit: AbstractNativeToolkit?

	@OriginalMember(owner = "client!fw", name = "c", descriptor = "Lclient!d;")
	private val materialProvider: MaterialProvider?

	@OriginalMember(owner = "client!fw", name = "<init>", descriptor = "(Lclient!wga;Lclient!d;)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(1) textureSource: MaterialProvider?) {
		this.toolkit = nativeToolkit
		this.materialProvider = textureSource
	}

	@OriginalMember(owner = "client!fw", name = "a", descriptor = "(I)V")
	fun reset() {
		this.cache.reset()
	}

	@OriginalMember(owner = "client!fw", name = "b", descriptor = "(I)V")
	fun cacheClean() {
		this.cache.clean(5)
	}

	@OriginalMember(owner = "client!fw", name = "a", descriptor = "(BI)Lclient!wfa;")
	fun getTexture(@OriginalArg(1) materialId: Int): GpuTexture? {
		@Pc(12) val `object`: Any? = this.cache.get(materialId.toLong())
		if (`object` != null) {
			return `object` as GpuTexture
		} else if (this.materialProvider!!.textureAvailable(materialId)) {
			@Pc(36) val textureMetrics: MaterialType? = this.materialProvider.getMetrics(materialId)
			@Pc(45) val size: Int = if (textureMetrics!!.isSmall) 64 else this.toolkit!!.size
			@Pc(80) val gpuTexture: GpuTexture?
			if (textureMetrics.hdr && this.toolkit!!.bloom()) {
				@Pc(62) val pixelsHdr: FloatArray? = this.materialProvider.floatArgbOutput(size, 0.7f, size, materialId)
				gpuTexture = this.toolkit.createHdrTexture(pixelsHdr, size, size, GLFormat.HDR, textureMetrics.mipmapping.toInt() != 0)
			} else {
				@Pc(101) val pixels: IntArray?
				if (textureMetrics.effectCombiner != 2 && MaterialType.isOpaqueEffect(textureMetrics.effectType.toInt())) {
					pixels = this.materialProvider.getPixels(true, materialId, 0.7f, size, size)
				} else {
					pixels = this.materialProvider.getPixelsArgb(size, 0.7f, materialId, size)
				}
				gpuTexture = this.toolkit!!.createTexture(size, size, textureMetrics.mipmapping.toInt() != 0, pixels)
			}
			gpuTexture!!.setWarp(textureMetrics.repeatS, textureMetrics.repeatT)
			this.cache.put(gpuTexture, materialId.toLong())
			return gpuTexture
		} else {
			return null
		}
	}
}
