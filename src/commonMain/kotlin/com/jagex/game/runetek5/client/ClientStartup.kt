package com.jagex.game.runetek5.client

import com.jagex.game.runetek5.LoadingRequirement
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object ClientStartup {
	@OriginalMember(owner = "client!kl", name = "y", descriptor = "I")
	@JvmField
	var loadingStatusDependencies: Int = 0

	@OriginalMember(owner = "client!nia", name = "g", descriptor = "[Lclient!up;")
	@JvmField
	var fullyCachedDependencies: Array<LoadingRequirement?>? = null

	@OriginalMember(owner = "client!st", name = "M", descriptor = "I")
	@JvmField
	var loadingPercentDependencies: Int = 0
}
