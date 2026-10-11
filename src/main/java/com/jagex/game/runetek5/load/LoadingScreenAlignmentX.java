package com.jagex.game.runetek5.load;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.GameShell;

@OriginalClass("client!sd")
public final class LoadingScreenAlignmentX {

	@OriginalMember(owner = "client!sd", name = "a", descriptor = "(IIB)I")
	public int getX(@OriginalArg(0) int width, @OriginalArg(1) int elementWidth) {
		@Pc(14) int containerWidth = width < GameShell.screenCanvasWidth ? GameShell.screenCanvasWidth : width;
		if (LoadingScreenAlignmentX.ALIGN_LEFT == this) {
			return 0;
		} else if (LoadingScreenAlignmentX.ALIGN_RIGHT == this) {
			return containerWidth - elementWidth;
		} else if (LoadingScreenAlignmentX.ALIGN_CENTER == this) {
			return (containerWidth - elementWidth) / 2;
		} else {
			return 0;
		}
	}

	@OriginalMember(owner = "client!sd", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!ce", name = "z", descriptor = "Lclient!sd;")
	public static final LoadingScreenAlignmentX ALIGN_LEFT = new LoadingScreenAlignmentX();
	@OriginalMember(owner = "client!mha", name = "k", descriptor = "Lclient!sd;")
	public static final LoadingScreenAlignmentX ALIGN_CENTER = new LoadingScreenAlignmentX();

	@OriginalMember(owner = "client!ad", name = "a", descriptor = "Lclient!sd;")
	public static final LoadingScreenAlignmentX ALIGN_RIGHT = new LoadingScreenAlignmentX();
	@OriginalMember(owner = "client!ot", name = "a", descriptor = "(I)[Lclient!sd;")
	public static LoadingScreenAlignmentX[] loadingScreenAlignmentXValues() {
		return new LoadingScreenAlignmentX[] { LoadingScreenAlignmentX.ALIGN_LEFT, LoadingScreenAlignmentX.ALIGN_CENTER, LoadingScreenAlignmentX.ALIGN_RIGHT };
	}
}
