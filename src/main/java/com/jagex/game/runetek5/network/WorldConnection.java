package com.jagex.game.runetek5.network;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.network.world.WorldDescriptor;
import com.jagex.game.runetek5.WorldConnectionInfo;
import com.jagex.game.runetek5.WorldListSorter;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.client.PingWorker;
import com.jagex.game.runetek5.network.world.WorldMeta;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

public final class WorldConnection {
	@OriginalMember(owner = "client!np", name = "f", descriptor = "Z")
	public static boolean requestingWorldList = false;

	@OriginalMember(owner = "client!ta", name = "l", descriptor = "J")
	public static long worldListReceivedTime;

	private WorldConnection() {
		/* empty */
	}
	@OriginalMember(owner = "client!kb", name = "i", descriptor = "[Lclient!mf;")
	public static WorldDescriptor[] WORLD_DESCRIPTORS_BYID = new WorldDescriptor[0];
	@OriginalMember(owner = "client!ak", name = "a", descriptor = "(Z)Lclient!mf;")
	public static WorldDescriptor getNextWorldDescriptor() {
		return WorldConnection.nextWorldDescriptorIndex < WorldConnection.WORLD_DESCRIPTORS_BYID.length ? WorldConnection.WORLD_DESCRIPTORS_BYID[WorldConnection.nextWorldDescriptorIndex++] : null;
	}

	@OriginalMember(owner = "client!rq", name = "w", descriptor = "Lclient!ss;")
	public static Connection aClass116_2;
	@OriginalMember(owner = "client!kaa", name = "q", descriptor = "Lclient!pc;")
	public static WorldConnectionInfo aClass255_3;
	@OriginalMember(owner = "client!dda", name = "M", descriptor = "Lclient!pc;")
	public static WorldConnectionInfo aClass255_1;
	@OriginalMember(owner = "client!bca", name = "h", descriptor = "(I)V")
	public static void pingWorlds() {
		if (!MiniMenu.debugOps) {
			return;
		}
		while (true) {
			while (WorldConnection.WORLD_DESCRIPTORS_BYID.length > WorldListSorter.pingWorldIndex) {
				@Pc(25) WorldDescriptor gameWorld = WorldConnection.WORLD_DESCRIPTORS_BYID[WorldListSorter.pingWorldIndex];
				if (gameWorld != null && gameWorld.ping == -1) {
					if (WorldListSorter.aClass2_Sub36_2 == null) {
						WorldListSorter.aClass2_Sub36_2 = PingWorker.aClass262_1.requestPing(gameWorld.activity);
					}
					@Pc(50) int ping = WorldListSorter.aClass2_Sub36_2.ping;
					if (ping == -1) {
						return;
					}
					gameWorld.ping = ping;
					WorldListSorter.pingWorldIndex++;
					WorldListSorter.aClass2_Sub36_2 = null;
				} else {
					WorldListSorter.pingWorldIndex++;
				}
			}
			return;
		}
	}
	@OriginalMember(owner = "client!lp", name = "i", descriptor = "Lclient!pc;")
	public static WorldConnectionInfo aClass255_4;

	@OriginalMember(owner = "client!qea", name = "a", descriptor = "(Z[BB)V")
	public static void decodeWorldList(@OriginalArg(0) boolean refresh, @OriginalArg(1) byte[] buffer) {
		if (WorldConnection.aClass2_Sub11_5 == null) {
			WorldConnection.aClass2_Sub11_5 = new Packet(20000);
		}
		WorldConnection.aClass2_Sub11_5.writeBytes(buffer.length, 0, buffer);
		if (!refresh) {
			return;
		}
		WorldConnection.decodeWorldListResponse(WorldConnection.aClass2_Sub11_5.buffer);
		WorldConnection.WORLD_DESCRIPTORS_BYID = new WorldDescriptor[OrthoMode.worldListSize];
		@Pc(31) int count = 0;
		for (@Pc(33) int i = WorldDescriptor.WORLD_LIST_START; i <= WorldMeta.worldListEnd; i++) {
			@Pc(39) WorldDescriptor gameWorld = WorldMeta.getWorldDescriptor(i);
			if (gameWorld != null) {
				WorldConnection.WORLD_DESCRIPTORS_BYID[count++] = gameWorld;
			}
		}
		WorldConnection.requestingWorldList = false;
		WorldConnection.worldListReceivedTime = Utils.currentTimeMillis();
		WorldConnection.aClass2_Sub11_5 = null;
	}
	@OriginalMember(owner = "client!qda", name = "h", descriptor = "Lclient!cea;")
	public static Packet aClass2_Sub11_5;

	@OriginalMember(owner = "client!rk", name = "r", descriptor = "I")
	public static int nextWorldDescriptorIndex = 999999;

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "(B[B)Z")
	public static boolean decodeWorldListResponse(@OriginalArg(1) byte[] src) {
		@Pc(8) Packet packet = new Packet(src);
		@Pc(19) int version = packet.readUByte();
		if (version != 2) {
			return false;
		}
		@Pc(35) boolean hasWorldList = packet.readUByte() == 1;
		if (hasWorldList) {
			WorldMeta.decodeWorldList(packet);
		}
		WorldMeta.decodeWorldPopulations(packet);
		return true;
	}
}
