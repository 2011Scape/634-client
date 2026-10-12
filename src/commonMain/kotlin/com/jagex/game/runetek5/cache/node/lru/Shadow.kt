package com.jagex.game.runetek5.cache.node.lru

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.ground.Ground
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

@OriginalClass("client!r")
abstract class Shadow @OriginalMember(owner = "client!r", name = "<init>", descriptor = "()V") constructor() : CacheableNode() {

	companion object {
		@OriginalMember(owner = "client!np", name = "a", descriptor = "(Lclient!r;III[Z)Z")
		@JvmStatic
		fun renderShadows(@OriginalArg(0) shadow: Shadow?, @OriginalArg(1) virtualLevel: Int, @OriginalArg(2) x: Int, @OriginalArg(3) z: Int, @OriginalArg(4) castShadowAtPlane: BooleanArray?): Boolean {
			@Pc(1) var drawn: Boolean = false
			if (Scene.aClass65Array1 !== Scene.aClass65Array3) {
				@Pc(12) val baseHeight: Int = Scene.aClass65Array2!![virtualLevel]!!.averageHeight(x, z)
				@Pc(14) var i: Int = 0
				while (i <= virtualLevel) {
					@Pc(19) val ground: Ground? = Scene.aClass65Array2!![i]
					if (ground != null) {
						@Pc(29) val heightOffset: Int = baseHeight - ground.averageHeight(x, z)
						if (castShadowAtPlane != null) {
							castShadowAtPlane[i] = ground.method8002(shadow, x, heightOffset, z)
							if (!castShadowAtPlane[i]) {
								i++
								continue
							}
						}
						ground.CA(shadow, x, heightOffset, z, 0, false)
						drawn = true
					}
					i++
				}
			}
			return drawn
		}
	}
}
