package com.jagex.game.runetek5.graphics.texture.water

import com.jagex.game.runetek5.cache.node.impl.SpriteRenderNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic
import lang.Class
import lang.getDeclaredConstructor

@OriginalClass("client!ef")
abstract class KeyedReferenceFactory {

	@OriginalMember(owner = "client!ef", name = "a", descriptor = "(ILclient!rd;)Lclient!rd;")
	abstract fun createSoftReference(@OriginalArg(1) node: SpriteRenderNode?): SpriteRenderNode?

	companion object {
		@OriginalMember(owner = "client!hha", name = "f", descriptor = "(I)Lclient!ef;")
		@JvmStatic
		fun create(): KeyedReferenceFactory? {
			try {
				return lang.forName("com.jagex.game.runetek5.graphics.texture.water.KeyedSoftReferenceFactory").getDeclaredConstructor().newInstance() as KeyedReferenceFactory?
			} catch (@Pc(17) ex: Throwable) {
				return null
			}
		}
	}
}
