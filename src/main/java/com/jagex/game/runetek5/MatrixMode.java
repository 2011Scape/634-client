package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!qv")
public final class MatrixMode {

	@OriginalMember(owner = "client!qv", name = "a", descriptor = "(I)Z")
	public boolean isSceneMode() {
		return MatrixMode.aClass286_6 == this | MatrixMode.aClass286_1 == this;
	}

	@OriginalMember(owner = "client!qv", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!sb", name = "c", descriptor = "Lclient!qv;")
	public static final MatrixMode aClass286_7 = new MatrixMode();

	@OriginalMember(owner = "client!at", name = "ld", descriptor = "Lclient!qv;")
	public static final MatrixMode aClass286_1 = new MatrixMode();
	@OriginalMember(owner = "client!qaa", name = "c", descriptor = "Lclient!qv;")
	public static final MatrixMode aClass286_6 = new MatrixMode();

	@OriginalMember(owner = "client!oga", name = "f", descriptor = "Lclient!qv;")
	public static final MatrixMode aClass286_5 = new MatrixMode();
}
