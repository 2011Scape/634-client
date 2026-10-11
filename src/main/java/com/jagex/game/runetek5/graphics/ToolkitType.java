package com.jagex.game.runetek5.graphics;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;

public final class ToolkitType {

	public static final int JAVA = 0;

	public static final int GL = 1;

	public static final int SSE = 2;

	public static final int D3D = 3;

	public static final int GLX = 5;

	private ToolkitType() {
		/* empty */
	}
	@OriginalMember(owner = "client!eh", name = "b", descriptor = "(II)Z")
	public static boolean isHardwareToolkit(@OriginalArg(1) int toolkitId) {
		return toolkitId == 1 || toolkitId == 3 || toolkitId == 5;
	}

	@OriginalMember(owner = "client!ok", name = "a", descriptor = "(BI)Z")
	public static boolean isSoftwareToolkit(@OriginalArg(1) int toolkitId) {
		return toolkitId == 0 || toolkitId == 2;
	}
}
