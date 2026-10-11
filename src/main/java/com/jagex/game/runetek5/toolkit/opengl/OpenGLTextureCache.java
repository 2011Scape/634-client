package com.jagex.game.runetek5.toolkit.opengl;

import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!eo")
public final class OpenGLTextureCache {

	@OriginalMember(owner = "client!eo", name = "j", descriptor = "Lclient!jr;")
	private final LRUCache textureCache = new LRUCache(256);

	@OriginalMember(owner = "client!eo", name = "d", descriptor = "Lclient!qo;")
	private final GlToolkit toolkit;

	@OriginalMember(owner = "client!eo", name = "h", descriptor = "Lclient!d;")
	private final MaterialProvider materialProvider;

	@OriginalMember(owner = "client!eo", name = "<init>", descriptor = "(Lclient!qo;Lclient!d;)V")
	public OpenGLTextureCache(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) MaterialProvider textureSource) {
		this.toolkit = glToolkit;
		this.materialProvider = textureSource;
	}

	@OriginalMember(owner = "client!eo", name = "a", descriptor = "(B)V")
	public void reset() {
		this.textureCache.reset();
	}

	@OriginalMember(owner = "client!eo", name = "a", descriptor = "(II)Lclient!tp;")
	public OpenGl2DTexture getTexture(@OriginalArg(0) int textureId) {
		@Pc(12) Object object = this.textureCache.get((long) textureId);
		if (object != null) {
			return (OpenGl2DTexture) object;
		} else if (this.materialProvider.textureAvailable(textureId)) {
			@Pc(31) MaterialType textureMetrics = this.materialProvider.getMetrics(textureId);
			@Pc(40) int textureSize = textureMetrics.isSmall ? 64 : this.toolkit.size;
			@Pc(78) OpenGl2DTexture texture;
			if (textureMetrics.hdr && this.toolkit.bloom()) {
				@Pc(57) float[] hdrPixels = this.materialProvider.floatArgbOutput(textureSize, 0.7F, textureSize, textureId);
				texture = new OpenGl2DTexture(this.toolkit, 3553, 34842, textureSize, textureSize, textureMetrics.mipmapping != 0, hdrPixels, 6408);
			} else {
				@Pc(101) int[] pixels;
				if (textureMetrics.effectCombiner != 2 && MaterialType.isOpaqueEffect(textureMetrics.effectType)) {
					pixels = this.materialProvider.getPixels(true, textureId, 0.7F, textureSize, textureSize);
				} else {
					pixels = this.materialProvider.getPixelsArgb(textureSize, 0.7F, textureId, textureSize);
				}
				texture = new OpenGl2DTexture(this.toolkit, 3553, 6408, textureSize, textureSize, textureMetrics.mipmapping != 0, pixels, 0, 0, false);
			}
			texture.setRepeat(textureMetrics.repeatT, textureMetrics.repeatS);
			this.textureCache.put(texture, (long) textureId);
			return texture;
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!eo", name = "c", descriptor = "(I)V")
	public void clean() {
		this.textureCache.clean(5);
	}
}
