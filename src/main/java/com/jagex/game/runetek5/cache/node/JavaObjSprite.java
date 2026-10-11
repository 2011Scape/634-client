package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!gda")
public final class JavaObjSprite extends Node {

	@OriginalMember(owner = "client!gda", name = "u", descriptor = "I")
	public final int objNumMode;

	@OriginalMember(owner = "client!gda", name = "s", descriptor = "I")
	public final int invCount;

	@OriginalMember(owner = "client!gda", name = "v", descriptor = "I")
	public final int graphicShadow;

	@OriginalMember(owner = "client!gda", name = "x", descriptor = "Z")
	public final boolean objWearCol;

	@OriginalMember(owner = "client!gda", name = "p", descriptor = "I")
	public final int objId;

	@OriginalMember(owner = "client!gda", name = "w", descriptor = "I")
	public final int outline;

	@OriginalMember(owner = "client!gda", name = "<init>", descriptor = "(IIIIIZ)V")
	public JavaObjSprite(@OriginalArg(0) int outline, @OriginalArg(1) int graphicShadow, @OriginalArg(2) int invCount, @OriginalArg(3) int objNumMode, @OriginalArg(4) int objId, @OriginalArg(5) boolean objWearCol) {
		this.objNumMode = objNumMode;
		this.invCount = invCount;
		this.graphicShadow = graphicShadow;
		this.objWearCol = objWearCol;
		this.objId = objId;
		this.outline = outline;
	}
}
