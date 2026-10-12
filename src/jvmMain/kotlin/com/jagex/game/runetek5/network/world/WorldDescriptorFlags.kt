package com.jagex.game.runetek5.network.world

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!mr")
abstract class WorldDescriptorFlags {

	@OriginalMember(owner = "client!mr", name = "f", descriptor = "I")
	@JvmField
	var population: Int = 0

	@OriginalMember(owner = "client!mr", name = "j", descriptor = "I")
	@JvmField
	var flags: Int = 0

	@OriginalMember(owner = "client!mr", name = "m", descriptor = "I")
	@JvmField
	var country: Int = 0

	@OriginalMember(owner = "client!mr", name = "a", descriptor = "(Z)Z")
	fun isMembers(): Boolean {
		return (this.flags and 0x1) != 0
	}

	@OriginalMember(owner = "client!mr", name = "b", descriptor = "(I)Z")
	fun isPvp(): Boolean {
		return (this.flags and 0x4) != 0
	}

	@OriginalMember(owner = "client!mr", name = "d", descriptor = "(I)Z")
	fun isLootshare(): Boolean {
		return (this.flags and 0x8) != 0
	}

	@OriginalMember(owner = "client!mr", name = "a", descriptor = "(B)Z")
	fun isQuickChat(): Boolean {
		return (this.flags and 0x2) != 0
	}
}
