package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!wq")
public final class PixelFormat {

	@OriginalMember(owner = "client!wq", name = "d", descriptor = "I")
	public final int componentCount;

	@OriginalMember(owner = "client!wq", name = "<init>", descriptor = "(I)V")
	public PixelFormat(@OriginalArg(0) int componentCount) {
		this.componentCount = componentCount;
	}

	@OriginalMember(owner = "client!wq", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!qk", name = "p", descriptor = "Lclient!wq;")
	public static final PixelFormat aClass375_39 = new PixelFormat(2);

	@OriginalMember(owner = "client!sl", name = "cb", descriptor = "Lclient!wq;")
	public static final PixelFormat aClass375_44 = new PixelFormat(1);

	@OriginalMember(owner = "client!cm", name = "h", descriptor = "Lclient!wq;")
	public static final PixelFormat aClass375_49 = new PixelFormat(1);
	@OriginalMember(owner = "client!bu", name = "p", descriptor = "Lclient!wq;")
	public static final PixelFormat aClass375_38 = new PixelFormat(3);
}
