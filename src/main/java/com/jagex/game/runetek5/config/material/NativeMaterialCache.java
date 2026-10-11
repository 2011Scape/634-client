package com.jagex.game.runetek5.config.material;

import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.graphics.GpuTexture;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!fw")
public final class NativeMaterialCache {

	@OriginalMember(owner = "client!fw", name = "f", descriptor = "Lclient!jr;")
	private final LRUCache cache = new LRUCache(256);

	@OriginalMember(owner = "client!fw", name = "a", descriptor = "Lclient!wga;")
	private final AbstractNativeToolkit toolkit;

	@OriginalMember(owner = "client!fw", name = "c", descriptor = "Lclient!d;")
	private final MaterialProvider materialProvider;

	@OriginalMember(owner = "client!fw", name = "<init>", descriptor = "(Lclient!wga;Lclient!d;)V")
	public NativeMaterialCache(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) MaterialProvider textureSource) {
		this.toolkit = nativeToolkit;
		this.materialProvider = textureSource;
	}

	@OriginalMember(owner = "client!fw", name = "a", descriptor = "(I)V")
	public void reset() {
		this.cache.reset();
	}

	@OriginalMember(owner = "client!fw", name = "b", descriptor = "(I)V")
	public void cacheClean() {
		this.cache.clean(5);
	}

	@OriginalMember(owner = "client!fw", name = "a", descriptor = "(BI)Lclient!wfa;")
	public GpuTexture getTexture(@OriginalArg(1) int materialId) {
		@Pc(12) Object object = this.cache.get((long) materialId);
		if (object != null) {
			return (GpuTexture) object;
		} else if (this.materialProvider.textureAvailable(materialId)) {
			@Pc(36) MaterialType textureMetrics = this.materialProvider.getMetrics(materialId);
			@Pc(45) int size = textureMetrics.isSmall ? 64 : this.toolkit.size;
			@Pc(80) GpuTexture gpuTexture;
			if (textureMetrics.hdr && this.toolkit.bloom()) {
				@Pc(62) float[] pixelsHdr = this.materialProvider.floatArgbOutput(size, 0.7F, size, materialId);
				gpuTexture = this.toolkit.createHdrTexture(pixelsHdr, size, size, GLFormat.HDR, textureMetrics.mipmapping != 0);
			} else {
				@Pc(101) int[] pixels;
				if (textureMetrics.effectCombiner != 2 && MaterialType.isOpaqueEffect(textureMetrics.effectType)) {
					pixels = this.materialProvider.getPixels(true, materialId, 0.7F, size, size);
				} else {
					pixels = this.materialProvider.getPixelsArgb(size, 0.7F, materialId, size);
				}
				gpuTexture = this.toolkit.createTexture(size, size, textureMetrics.mipmapping != 0, pixels);
			}
			gpuTexture.setWarp(textureMetrics.repeatS, textureMetrics.repeatT);
			this.cache.put(gpuTexture, (long) materialId);
			return gpuTexture;
		} else {
			return null;
		}
	}
}
