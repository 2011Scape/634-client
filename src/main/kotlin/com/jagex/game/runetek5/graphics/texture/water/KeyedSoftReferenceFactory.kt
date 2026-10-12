package com.jagex.game.runetek5.graphics.texture.water

import com.jagex.game.runetek5.cache.node.impl.KeyedSoftReferenceNode
import com.jagex.game.runetek5.cache.node.impl.SpriteRenderNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!sw")
class KeyedSoftReferenceFactory : KeyedReferenceFactory() {

	@OriginalMember(owner = "client!sw", name = "a", descriptor = "(ILclient!rd;)Lclient!rd;")
	override fun createSoftReference(@OriginalArg(1) keyedReferenceNode: SpriteRenderNode?): SpriteRenderNode? {
		return KeyedSoftReferenceNode(keyedReferenceNode!!.cacheKey, keyedReferenceNode.get(), keyedReferenceNode.size)
	}
}
