package com.jagex.game.runetek5.network.world;

import com.jagex.game.runetek5.Country;
import com.jagex.game.runetek5.network.world.WorldDescriptorFlags;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.network.WorldConnection;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!mf")
public final class WorldDescriptor extends WorldDescriptorFlags {

	@OriginalMember(owner = "client!mf", name = "q", descriptor = "I")
	public int worldNumber;

	@OriginalMember(owner = "client!mf", name = "r", descriptor = "Ljava/lang/String;")
	public String address;

	@OriginalMember(owner = "client!mf", name = "y", descriptor = "Ljava/lang/String;")
	public String activity;

	@OriginalMember(owner = "client!mf", name = "s", descriptor = "I")
	public int ping = -1;

	@OriginalMember(owner = "client!mf", name = "b", descriptor = "(Z)Lclient!vh;")
	public Country getCountry() {
		return Country.aClass359Array1[super.country];
	}
	@OriginalMember(owner = "client!ce", name = "E", descriptor = "I")
	public static int port;
	@OriginalMember(owner = "client!fha", name = "B", descriptor = "I")
	public static int WORLD_LIST_START;
	@OriginalMember(owner = "client!un", name = "o", descriptor = "(I)Lclient!mf;")
	public static WorldDescriptor getFirstWorldDescriptor() {
		WorldConnection.nextWorldDescriptorIndex = 0;
		return WorldConnection.getNextWorldDescriptor();
	}
}
