package com.jagex.game.runetek5.cache.node.lru;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.scene.ground.Ground;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!r")
public abstract class Shadow extends CacheableNode {

	@OriginalMember(owner = "client!r", name = "<init>", descriptor = "()V")
	public Shadow() {
	}
	@OriginalMember(owner = "client!np", name = "a", descriptor = "(Lclient!r;III[Z)Z")
	public static boolean renderShadows(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int virtualLevel, @OriginalArg(2) int x, @OriginalArg(3) int z, @OriginalArg(4) boolean[] castShadowAtPlane) {
		@Pc(1) boolean drawn = false;
		if (Scene.aClass65Array1 != Scene.aClass65Array3) {
			@Pc(12) int baseHeight = Scene.aClass65Array2[virtualLevel].averageHeight(x, z);
			for (@Pc(14) int i = 0; i <= virtualLevel; i++) {
				@Pc(19) Ground ground = Scene.aClass65Array2[i];
				if (ground != null) {
					@Pc(29) int heightOffset = baseHeight - ground.averageHeight(x, z);
					if (castShadowAtPlane != null) {
						castShadowAtPlane[i] = ground.method8002(shadow, x, heightOffset, z);
						if (!castShadowAtPlane[i]) {
							continue;
						}
					}
					ground.CA(shadow, x, heightOffset, z, 0, false);
					drawn = true;
				}
			}
		}
		return drawn;
	}
}
