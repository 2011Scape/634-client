package com.jagex.game.runetek5.config.rsinterface;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;

@OriginalClass("client!wb")
public final class IFEvents extends Node {

	@OriginalMember(owner = "client!wb", name = "q", descriptor = "I")
	public final int interfaceId;

	@OriginalMember(owner = "client!wb", name = "v", descriptor = "I")
	public final int settingsHash;

	@OriginalMember(owner = "client!wb", name = "<init>", descriptor = "(II)V")
	public IFEvents(@OriginalArg(0) int events, @OriginalArg(1) int targetParam) {
		this.interfaceId = targetParam;
		this.settingsHash = events;
	}

	@OriginalMember(owner = "client!wb", name = "a", descriptor = "(IZ)Z")
	public boolean isOpEnabled(@OriginalArg(0) int op) {
		return (this.settingsHash >> op + 1 & 0x1) != 0;
	}

	@OriginalMember(owner = "client!wb", name = "a", descriptor = "(I)Z")
	public boolean dragEnabled() {
		return (this.settingsHash >> 21 & 0x1) != 0;
	}

	@OriginalMember(owner = "client!wb", name = "b", descriptor = "(I)Z")
	public boolean isTargetableByUse() {
		return (this.settingsHash >> 22 & 0x1) != 0;
	}

	@OriginalMember(owner = "client!wb", name = "a", descriptor = "(B)I")
	public int getDragDepth() {
		return this.settingsHash >> 18 & 0x7;
	}

	@OriginalMember(owner = "client!wb", name = "d", descriptor = "(I)Z")
	public boolean continueOptionEnabled() {
		return (this.settingsHash & 0x1) != 0;
	}

	@OriginalMember(owner = "client!wb", name = "e", descriptor = "(I)I")
	public int getUseOptionFlags() {
		return IFEvents.getUseOptionFlags(this.settingsHash);
	}
	@OriginalMember(owner = "client!hn", name = "a", descriptor = "(IZ)I")
	public static int getUseOptionFlags(@OriginalArg(0) int settingsHash) {
		return settingsHash >> 11 & 0x7F;
	}
	@OriginalMember(owner = "client!sga", name = "a", descriptor = "Lclient!eq;")
	public static final IterableHashTable ICOMPONENT_SETTINGS_SLOTS = new IterableHashTable(512);
}
