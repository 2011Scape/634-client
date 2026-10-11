package com.jagex.game.runetek5.client.menu;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!db")
public final class MiniMenuEntry extends CacheableNode {

	@OriginalMember(owner = "client!db", name = "y", descriptor = "Ljava/lang/String;")
	public String targetName;

	@OriginalMember(owner = "client!db", name = "M", descriptor = "I")
	public final int param1;

	@OriginalMember(owner = "client!db", name = "N", descriptor = "I")
	public int clickType;

	@OriginalMember(owner = "client!db", name = "D", descriptor = "I")
	public final int itemId;

	@OriginalMember(owner = "client!db", name = "J", descriptor = "J")
	public final long entityUid;

	@OriginalMember(owner = "client!db", name = "Q", descriptor = "Z")
	public final boolean showOtherLevelIcon;

	@OriginalMember(owner = "client!db", name = "B", descriptor = "Z")
	public final boolean isSelectable;

	@OriginalMember(owner = "client!db", name = "F", descriptor = "Ljava/lang/String;")
	public final String op;

	@OriginalMember(owner = "client!db", name = "R", descriptor = "Z")
	public final boolean isUncategorized;

	@OriginalMember(owner = "client!db", name = "w", descriptor = "Ljava/lang/String;")
	public final String opBase;

	@OriginalMember(owner = "client!db", name = "P", descriptor = "I")
	public final int cursor;

	@OriginalMember(owner = "client!db", name = "E", descriptor = "J")
	public final long bitpacked;

	@OriginalMember(owner = "client!db", name = "G", descriptor = "I")
	public final int param0;

	@OriginalMember(owner = "client!db", name = "<init>", descriptor = "(Ljava/lang/String;Ljava/lang/String;IIIJIIZZJZ)V")
	public MiniMenuEntry(@OriginalArg(0) String string, @OriginalArg(1) String string2, @OriginalArg(2) int cursor, @OriginalArg(3) int clickType, @OriginalArg(4) int itemId, @OriginalArg(5) long entityUid, @OriginalArg(6) int param0, @OriginalArg(7) int param1, @OriginalArg(8) boolean showOtherLevelIcon, @OriginalArg(9) boolean isSelectable, @OriginalArg(10) long bitpacked, @OriginalArg(11) boolean isUncategorized) {
		this.param1 = param1;
		this.clickType = clickType;
		this.itemId = itemId;
		this.entityUid = entityUid;
		this.showOtherLevelIcon = showOtherLevelIcon;
		this.isSelectable = isSelectable;
		this.op = string2;
		this.isUncategorized = isUncategorized;
		this.opBase = string;
		this.cursor = cursor;
		this.bitpacked = bitpacked;
		this.param0 = param0;
	}
}
