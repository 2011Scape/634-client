package com.jagex.game.runetek5.network.world;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!mr")
public abstract class WorldDescriptorFlags {

	@OriginalMember(owner = "client!mr", name = "f", descriptor = "I")
	public int population;

	@OriginalMember(owner = "client!mr", name = "j", descriptor = "I")
	public int flags;

	@OriginalMember(owner = "client!mr", name = "m", descriptor = "I")
	public int country;

	@OriginalMember(owner = "client!mr", name = "a", descriptor = "(Z)Z")
	public final boolean isMembers() {
		return (this.flags & 0x1) != 0;
	}

	@OriginalMember(owner = "client!mr", name = "b", descriptor = "(I)Z")
	public final boolean isPvp() {
		return (this.flags & 0x4) != 0;
	}

	@OriginalMember(owner = "client!mr", name = "d", descriptor = "(I)Z")
	public final boolean isLootshare() {
		return (this.flags & 0x8) != 0;
	}

	@OriginalMember(owner = "client!mr", name = "a", descriptor = "(B)Z")
	public final boolean isQuickChat() {
		return (this.flags & 0x2) != 0;
	}
}
