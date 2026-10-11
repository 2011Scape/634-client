package com.jagex.game.runetek5.client.input.keyboard;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!efa")
public abstract class KeyRecorder {

	@OriginalMember(owner = "client!efa", name = "<init>", descriptor = "()V")
	public KeyRecorder() {
	}

	@OriginalMember(owner = "client!efa", name = "a", descriptor = "(I)V")
	public abstract void prepareAwaitingKeys();

	@OriginalMember(owner = "client!efa", name = "a", descriptor = "(II)Z")
	public abstract boolean isPressed(@OriginalArg(0) int keyCode);

	@OriginalMember(owner = "client!efa", name = "b", descriptor = "(I)Lclient!ll;")
	public abstract KeyRecord removeFirstRecorded();

	@OriginalMember(owner = "client!efa", name = "c", descriptor = "(I)V")
	public abstract void remove();
	@OriginalMember(owner = "client!qca", name = "e", descriptor = "Lclient!efa;")
	public static KeyRecorder keyRecorder;
	@OriginalMember(owner = "client!wu", name = "f", descriptor = "[Lclient!ll;")
	public static final KeyRecord[] KEYS_PRESSED = new KeyRecord[75];

	@OriginalMember(owner = "client!gda", name = "y", descriptor = "J")
	public static long lastKeyTime = -1L;
	@OriginalMember(owner = "client!u", name = "c", descriptor = "I")
	public static int keyCachePointerPoss = 0;
}
