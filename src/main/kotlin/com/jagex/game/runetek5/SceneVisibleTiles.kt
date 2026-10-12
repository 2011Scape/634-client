package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalMember

object SceneVisibleTiles {

	@OriginalMember(owner = "client!ge", name = "fb", descriptor = "I")
	@JvmField
	var visibleTiles: Int = 0
}
