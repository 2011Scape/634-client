package com.jagex.game.runetek5.load;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.GameShell;

@OriginalClass("client!ec")
public final class LoadingScreenAlignmentY {

	@OriginalMember(owner = "client!ec", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!ec", name = "a", descriptor = "(III)I")
	public int getY(@OriginalArg(0) int height, @OriginalArg(1) int elementHeight) {
		@Pc(15) int containerHeight = GameShell.screenCanvasHeight <= height ? height : GameShell.screenCanvasHeight;
		if (LoadingScreenAlignmentY.ALIGN_TOP == this) {
			return 0;
		} else if (LoadingScreenAlignmentY.ALIGN_BOTTOM == this) {
			return containerHeight - elementHeight;
		} else if (LoadingScreenAlignmentY.ALIGN_MIDDLE == this) {
			return (containerHeight - elementHeight) / 2;
		} else {
			return 0;
		}
	}
	@OriginalMember(owner = "client!ht", name = "m", descriptor = "Lclient!ec;")
	public static final LoadingScreenAlignmentY ALIGN_MIDDLE = new LoadingScreenAlignmentY();

	@OriginalMember(owner = "client!jw", name = "a", descriptor = "(I)[Lclient!ec;")
	public static LoadingScreenAlignmentY[] loadingScreenAlignmentYValues() {
		return new LoadingScreenAlignmentY[] { LoadingScreenAlignmentY.ALIGN_TOP, LoadingScreenAlignmentY.ALIGN_MIDDLE, LoadingScreenAlignmentY.ALIGN_BOTTOM };
	}
	@OriginalMember(owner = "client!vaa", name = "K", descriptor = "Lclient!ec;")
	public static final LoadingScreenAlignmentY ALIGN_BOTTOM = new LoadingScreenAlignmentY();
	@OriginalMember(owner = "client!la", name = "m", descriptor = "Lclient!ec;")
	public static final LoadingScreenAlignmentY ALIGN_TOP = new LoadingScreenAlignmentY();
}
