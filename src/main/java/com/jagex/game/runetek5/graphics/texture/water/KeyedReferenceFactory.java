package com.jagex.game.runetek5.graphics.texture.water;

import com.jagex.game.runetek5.cache.node.impl.SpriteRenderNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ef")
public abstract class KeyedReferenceFactory {

	@OriginalMember(owner = "client!ef", name = "a", descriptor = "(ILclient!rd;)Lclient!rd;")
	public abstract SpriteRenderNode createSoftReference(@OriginalArg(1) SpriteRenderNode node);
	@OriginalMember(owner = "client!hha", name = "f", descriptor = "(I)Lclient!ef;")
	public static KeyedReferenceFactory create() {
		try {
			return (KeyedReferenceFactory) Class.forName("com.jagex.game.runetek5.graphics.texture.water.KeyedSoftReferenceFactory").getDeclaredConstructor().newInstance();
		} catch (@Pc(17) Throwable ex) {
			return null;
		}
	}
}
