package com.jagex.game.runetek5.config.staticelements;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!tu")
public final class StaticElements {

	@OriginalMember(owner = "client!tu", name = "e", descriptor = "I")
	public int elementCount;

	@OriginalMember(owner = "client!tu", name = "c", descriptor = "[I")
	public final int[] mapCategoryIds;

	@OriginalMember(owner = "client!tu", name = "b", descriptor = "[I")
	public final int[] bitpackedPlacement;

	@OriginalMember(owner = "client!tu", name = "<init>", descriptor = "(I)V")
	public StaticElements(@OriginalArg(0) int count) {
		this.elementCount = count;
		this.mapCategoryIds = new int[this.elementCount];
		this.bitpackedPlacement = new int[this.elementCount];
	}
	@OriginalMember(owner = "client!wt", name = "a", descriptor = "(Lclient!in;Ljava/lang/String;ZB)Lclient!tu;")
	public static StaticElements load(@OriginalArg(0) Js5ResourceProvider archive, @OriginalArg(1) String string, @OriginalArg(2) boolean includeMembers) {
		@Pc(10) int groupId = archive.getGroupId(string);
		if (groupId == -1) {
			return new StaticElements(0);
		}
		@Pc(31) int[] fileIds = archive.getFileIds(groupId);
		@Pc(37) StaticElements mapElementList = new StaticElements(fileIds.length);
		@Pc(39) int elementIndex = 0;
		@Pc(41) int fileIndex = 0;
		while (true) {
			while (elementIndex < mapElementList.elementCount) {
				@Pc(55) Packet packet = new Packet(archive.getFile(groupId, fileIds[fileIndex++]));
				@Pc(59) int placement = packet.readInt();
				@Pc(65) int categoryId = packet.readUShort();
				@Pc(69) int membersOnly = packet.readUByte();
				if (!includeMembers && membersOnly == 1) {
					mapElementList.elementCount--;
				} else {
					mapElementList.bitpackedPlacement[elementIndex] = placement;
					mapElementList.mapCategoryIds[elementIndex] = categoryId;
					elementIndex++;
				}
			}
			return mapElementList;
		}
	}
	@OriginalMember(owner = "client!uc", name = "O", descriptor = "Lclient!tu;")
	public static StaticElements aClass333_3;
}
