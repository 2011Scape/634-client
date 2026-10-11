package com.jagex.game.runetek5.js5.request;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.js5.Js5MasterIndex;

@OriginalClass("client!ci")
public abstract class FileWorker {

	@OriginalMember(owner = "client!ci", name = "<init>", descriptor = "()V")
	public FileWorker() {
	}

	@OriginalMember(owner = "client!ci", name = "a", descriptor = "(II)I")
	public abstract int completePercentage(@OriginalArg(0) int groupId);

	@OriginalMember(owner = "client!ci", name = "a", descriptor = "(BI)V")
	public abstract void requestGroup(@OriginalArg(1) int groupId);

	@OriginalMember(owner = "client!ci", name = "a", descriptor = "(IB)[B")
	public abstract byte[] fetchgroup(@OriginalArg(0) int groupId);

	@OriginalMember(owner = "client!ci", name = "a", descriptor = "(B)Lclient!vw;")
	public abstract Js5MasterIndex index();
}
