package com.jagex.game.runetek5

import com.jagex.game.runetek5.cache.node.lru.AbstractLRUNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic
import lang.Class
import lang.getDeclaredConstructor

@OriginalClass("client!ca")
abstract class ReferenceFactory {

	@OriginalMember(owner = "client!ca", name = "a", descriptor = "(ILclient!qia;)Lclient!qia;")
	abstract fun createSoftReference(@OriginalArg(1) node: AbstractLRUNode?): AbstractLRUNode?

	companion object {
		@OriginalMember(owner = "client!vha", name = "a", descriptor = "(B)Lclient!ca;")
		@JvmStatic
		fun createSoftReferenceFactory(): ReferenceFactory? {
			try {
				return lang.forName("com.jagex.game.runetek5.core.datastruct.ref.SoftReferenceFactory").getDeclaredConstructor().newInstance() as ReferenceFactory?
			} catch (@Pc(14) ex: Throwable) {
				return null
			}
		}
	}
}
