package com.jagex.game.runetek5.graphics.hint;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

public final class HintTrail {
	@OriginalMember(owner = "client!bn", name = "d", descriptor = "I")
	public static int trailPreferenceFlags;

	private HintTrail() {
		/* empty */
	}
	@OriginalMember(owner = "client!kea", name = "a", descriptor = "(II)V")
	public static void setTrailPreferenceFlags(@OriginalArg(0) int trailPreferenceFlags) {
		HintTrail.trailPreferenceFlags = trailPreferenceFlags;
		@Pc(7) LRUCache referenceCache = PlayerAppearance.aClass169_18;
		synchronized (PlayerAppearance.aClass169_18) {
			PlayerAppearance.aClass169_18.reset();
		}
		referenceCache = PlayerAppearance.aClass169_4;
		synchronized (PlayerAppearance.aClass169_4) {
			PlayerAppearance.aClass169_4.reset();
		}
	}
}
