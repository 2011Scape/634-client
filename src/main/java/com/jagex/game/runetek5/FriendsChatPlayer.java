package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!hi")
public final class FriendsChatPlayer {

	@OriginalMember(owner = "client!hi", name = "c", descriptor = "Ljava/lang/String;")
	public String username;

	@OriginalMember(owner = "client!hi", name = "e", descriptor = "I")
	public int worldId;

	@OriginalMember(owner = "client!hi", name = "g", descriptor = "B")
	public byte rank;

	@OriginalMember(owner = "client!hi", name = "i", descriptor = "Ljava/lang/String;")
	public String textName;

	@OriginalMember(owner = "client!hi", name = "j", descriptor = "Ljava/lang/String;")
	public String worldName;

	@OriginalMember(owner = "client!hi", name = "k", descriptor = "Ljava/lang/String;")
	public String displayName;
}
