package com.jagex.game.runetek5.graphics.texture.water;

import com.jagex.game.runetek5.graphics.texture.water.KeyedReferenceFactory;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.impl.SpriteRenderNode;
import com.jagex.game.runetek5.cache.node.impl.KeyedSoftReferenceNode;

@OriginalClass("client!sw")
public final class KeyedSoftReferenceFactory extends KeyedReferenceFactory {

	@OriginalMember(owner = "client!sw", name = "a", descriptor = "(ILclient!rd;)Lclient!rd;")
	@Override
	public SpriteRenderNode createSoftReference(@OriginalArg(1) SpriteRenderNode keyedReferenceNode) {
		return new KeyedSoftReferenceNode(keyedReferenceNode.cacheKey, keyedReferenceNode.get(), keyedReferenceNode.size);
	}
}
