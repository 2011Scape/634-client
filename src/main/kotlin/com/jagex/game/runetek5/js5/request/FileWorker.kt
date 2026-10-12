package com.jagex.game.runetek5.js5.request

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.js5.Js5MasterIndex

@OriginalClass("client!ci")
abstract class FileWorker {

	@OriginalMember(owner = "client!ci", name = "<init>", descriptor = "()V")
	constructor() {
	}

	@OriginalMember(owner = "client!ci", name = "a", descriptor = "(II)I")
	abstract fun completePercentage(@OriginalArg(0) groupId: Int): Int

	@OriginalMember(owner = "client!ci", name = "a", descriptor = "(BI)V")
	abstract fun requestGroup(@OriginalArg(1) groupId: Int)

	@OriginalMember(owner = "client!ci", name = "a", descriptor = "(IB)[B")
	abstract fun fetchgroup(@OriginalArg(0) groupId: Int): ByteArray?

	@OriginalMember(owner = "client!ci", name = "a", descriptor = "(B)Lclient!vw;")
	abstract fun index(): Js5MasterIndex?
}
