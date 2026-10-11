package com.jagex.game.runetek5.network.world;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.Country;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

public final class WorldMeta {
	@OriginalMember(owner = "client!sc", name = "f", descriptor = "[Lclient!mf;")
	public static WorldDescriptor[] WORLD_LIST_DESCRIPTORS;

	private WorldMeta() {
		/* empty */
	}
	@OriginalMember(owner = "client!wd", name = "a", descriptor = "(BLclient!cea;)V")
	public static void decodeWorldList(@OriginalArg(1) Packet packet) {
		@Pc(9) int countryCount = packet.readUSmart();
		Country.aClass359Array1 = new Country[countryCount];
		for (@Pc(14) int i = 0; i < countryCount; i++) {
			Country.aClass359Array1[i] = new Country();
			Country.aClass359Array1[i].flag = packet.readUSmart();
			Country.aClass359Array1[i].name = packet.readJagString();
		}
		WorldDescriptor.WORLD_LIST_START = packet.readUSmart();
		WorldMeta.worldListEnd = packet.readUSmart();
		OrthoMode.worldListSize = packet.readUSmart();
		WorldMeta.WORLD_LIST_DESCRIPTORS = new WorldDescriptor[WorldMeta.worldListEnd + 1 - WorldDescriptor.WORLD_LIST_START];
		for (@Pc(66) int j = 0; j < OrthoMode.worldListSize; j++) {
			@Pc(72) int id = packet.readUSmart();
			@Pc(80) WorldDescriptor gameWorld = WorldMeta.WORLD_LIST_DESCRIPTORS[id] = new WorldDescriptor();
			gameWorld.country = packet.readUByte();
			gameWorld.flags = packet.readInt();
			gameWorld.worldNumber = WorldDescriptor.WORLD_LIST_START + id;
			gameWorld.address = packet.readJagString();
			gameWorld.activity = packet.readJagString();
		}
		WorldDescriptor.port = packet.readInt();
		WorldMeta.worldsLoaded = true;
	}
	@OriginalMember(owner = "client!qt", name = "k", descriptor = "Z")
	public static boolean worldsLoaded = false;
	@OriginalMember(owner = "client!kfa", name = "b", descriptor = "(ZI)Lclient!mf;")
	public static WorldDescriptor getWorldDescriptor(@OriginalArg(1) int worldId) {
		return WorldMeta.worldsLoaded && WorldDescriptor.WORLD_LIST_START <= worldId && worldId <= WorldMeta.worldListEnd ? WorldMeta.WORLD_LIST_DESCRIPTORS[worldId - WorldDescriptor.WORLD_LIST_START] : null;
	}

	@OriginalMember(owner = "client!nk", name = "a", descriptor = "(BLclient!cea;)V")
	public static void decodeWorldPopulations(@OriginalArg(1) Packet packet) {
		for (@Pc(7) int i = 0; i < OrthoMode.worldListSize; i++) {
			@Pc(13) int worldId = packet.readUSmart();
			@Pc(17) int population = packet.readUShort();
			if (population == 65535) {
				population = -1;
			}
			if (WorldMeta.WORLD_LIST_DESCRIPTORS[worldId] != null) {
				WorldMeta.WORLD_LIST_DESCRIPTORS[worldId].population = population;
			}
		}
	}

	@OriginalMember(owner = "client!fv", name = "v", descriptor = "I")
	public static int worldListEnd;
}
