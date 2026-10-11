package com.jagex.game.runetek5.config.defaults;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.entity.PathingEntity;
import com.jagex.game.runetek5.graphics.gl.HitsplatSlotXOffsets;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

public final class EntityDefaults {
	@OriginalMember(owner = "client!vi", name = "c", descriptor = "I")
	public static int loginInterfaceId;

	@OriginalMember(owner = "client!pi", name = "y", descriptor = "I")
	public static int profilingMesh = -1;

	@OriginalMember(owner = "client!jea", name = "e", descriptor = "I")
	public static int lobbyWindow;

	private EntityDefaults() {
		/* empty */
	}
	@OriginalMember(owner = "client!kg", name = "a", descriptor = "([BB)V")
	public static void decode(@OriginalArg(0) byte[] src) {
		@Pc(10) Packet packet = new Packet(src);
		@Pc(20) boolean offsetsDecoded = false;
		while (true) {
			while (true) {
				@Pc(24) int local24 = packet.readUByte();
				if (local24 == 0) {
					if (offsetsDecoded) {
						return;
					}
					if (HitsplatSlotXOffsets.hitsplatOffsetsX == null) {
						HitsplatSlotXOffsets.hitsplatOffsetsX = new int[4];
						EntityDefaults.hitsplatOffsetsY = new int[4];
						PathingEntity.maximumHits = 4;
					}
					for (local24 = 0; local24 < HitsplatSlotXOffsets.hitsplatOffsetsX.length; local24++) {
						HitsplatSlotXOffsets.hitsplatOffsetsX[local24] = 0;
						EntityDefaults.hitsplatOffsetsY[local24] = local24 * 20;
					}
					return;
				}
				if (local24 == 1) {
					if (HitsplatSlotXOffsets.hitsplatOffsetsX == null) {
						HitsplatSlotXOffsets.hitsplatOffsetsX = new int[4];
						PathingEntity.maximumHits = 4;
						EntityDefaults.hitsplatOffsetsY = new int[4];
					}
					for (@Pc(44) int i = 0; i < HitsplatSlotXOffsets.hitsplatOffsetsX.length; i++) {
						HitsplatSlotXOffsets.hitsplatOffsetsX[i] = packet.readShort();
						EntityDefaults.hitsplatOffsetsY[i] = packet.readShort();
					}
					offsetsDecoded = true;
				} else if (local24 == 2) {
					EntityDefaults.profilingMesh = packet.readUShort();
				} else if (local24 == 3) {
					PathingEntity.maximumHits = packet.readUByte();
					HitsplatSlotXOffsets.hitsplatOffsetsX = new int[PathingEntity.maximumHits];
					EntityDefaults.hitsplatOffsetsY = new int[PathingEntity.maximumHits];
				}
			}
		}
	}
	@OriginalMember(owner = "client!mia", name = "f", descriptor = "[I")
	public static int[] hitsplatOffsetsY = null;
}
