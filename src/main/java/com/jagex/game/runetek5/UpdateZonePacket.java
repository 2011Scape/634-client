package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!kn")
public final class UpdateZonePacket {

	@OriginalMember(owner = "client!kn", name = "<init>", descriptor = "(II)V")
	public UpdateZonePacket(@OriginalArg(0) int id, @OriginalArg(1) int size) {
	}

	@OriginalMember(owner = "client!kn", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!fi", name = "P", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket SPOT_ANIM = new UpdateZonePacket(12, 7);

	@OriginalMember(owner = "client!ifa", name = "b", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket GROUND_ITEM_COUNT = new UpdateZonePacket(7, 7);
	@OriginalMember(owner = "client!uk", name = "c", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket OBJ_ANIM = new UpdateZonePacket(2, 4);

	@OriginalMember(owner = "client!de", name = "i", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket GROUND_ITEM_REVEAL = new UpdateZonePacket(4, 7);
	@OriginalMember(owner = "client!li", name = "a", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket SOUND_AREA = new UpdateZonePacket(10, 8);

	@OriginalMember(owner = "client!bea", name = "A", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket MIDI_SONG_LOCATION = new UpdateZonePacket(11, 8);

	@OriginalMember(owner = "client!gca", name = "b", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket DESTROY_OBJECT = new UpdateZonePacket(3, 2);
	@OriginalMember(owner = "client!pd", name = "f", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket MAP_PROJANIM = new UpdateZonePacket(5, 16);

	@OriginalMember(owner = "client!ri", name = "q", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket OBJECT_PREFETCH = new UpdateZonePacket(8, 3);

	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(B)[Lclient!kn;")
	public static UpdateZonePacket[] updateZonePacketValues() {
		return new UpdateZonePacket[] { UpdateZonePacket.REMOVE_GROUND_ITEM, UpdateZonePacket.CREATE_OBJECT, UpdateZonePacket.OBJ_ANIM, UpdateZonePacket.DESTROY_OBJECT, UpdateZonePacket.GROUND_ITEM_REVEAL, UpdateZonePacket.MAP_PROJANIM, UpdateZonePacket.CREATE_GROUND_ITEM, UpdateZonePacket.GROUND_ITEM_COUNT, UpdateZonePacket.OBJECT_PREFETCH, UpdateZonePacket.MAP_PROJANIM_HALFSQ, UpdateZonePacket.SOUND_AREA, UpdateZonePacket.MIDI_SONG_LOCATION, UpdateZonePacket.SPOT_ANIM, UpdateZonePacket.TILE_MESSAGE };
	}

	@OriginalMember(owner = "client!sv", name = "R", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket CREATE_GROUND_ITEM = new UpdateZonePacket(6, 5);

	@OriginalMember(owner = "client!vda", name = "f", descriptor = "I")
	public static int updateZoneX;

	@OriginalMember(owner = "client!an", name = "w", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket MAP_PROJANIM_HALFSQ = new UpdateZonePacket(9, 19);

	@OriginalMember(owner = "client!pf", name = "Q", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket REMOVE_GROUND_ITEM = new UpdateZonePacket(0, 3);
	@OriginalMember(owner = "client!qg", name = "b", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket TILE_MESSAGE = new UpdateZonePacket(13, -1);

	@OriginalMember(owner = "client!mj", name = "u", descriptor = "Lclient!kn;")
	public static final UpdateZonePacket CREATE_OBJECT = new UpdateZonePacket(1, 4);
	@OriginalMember(owner = "client!kk", name = "o", descriptor = "I")
	public static int updateZoneY;
}
