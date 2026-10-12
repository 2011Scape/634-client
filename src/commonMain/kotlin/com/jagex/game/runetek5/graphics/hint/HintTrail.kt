package com.jagex.game.runetek5.graphics.hint

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import withLock

object HintTrail {
	@OriginalMember(owner = "client!bn", name = "d", descriptor = "I")
	@JvmField
	var trailPreferenceFlags: Int = 0

	@OriginalMember(owner = "client!kea", name = "a", descriptor = "(II)V")
	@JvmStatic
	fun setTrailPreferenceFlags(@OriginalArg(0) trailPreferenceFlags: Int) {
		HintTrail.trailPreferenceFlags = trailPreferenceFlags
		@Pc(7) var referenceCache: LRUCache? = PlayerAppearance.aClass169_18
		withLock(PlayerAppearance.aClass169_18) {
			PlayerAppearance.aClass169_18.reset()
		}
		referenceCache = PlayerAppearance.aClass169_4
		withLock(PlayerAppearance.aClass169_4) {
			PlayerAppearance.aClass169_4.reset()
		}
	}
}
