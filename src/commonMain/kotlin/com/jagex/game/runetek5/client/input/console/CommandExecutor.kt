package com.jagex.game.runetek5.client.input.console

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object CommandExecutor {
	@OriginalMember(owner = "client!bb", name = "Bb", descriptor = "I")
	@JvmField
	var teleportCommandPositionY: Int = 0

	@OriginalMember(owner = "client!sfa", name = "f", descriptor = "I")
	@JvmField
	var teleportCommandPositionZ: Int = 0

	@OriginalMember(owner = "client!uf", name = "N", descriptor = "I")
	@JvmField
	var teleportCommandPositionX: Int = 0
}
