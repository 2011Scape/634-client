package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.DataType
import com.jagex.game.runetek5.config.Identifiable
import jaclib.memory.Buffer

@OriginalClass("client!uq")
interface GpuIndexBuffer : Identifiable {

	@OriginalMember(owner = "client!uq", name = "a", descriptor = "(I)Z")
	fun unlock(): Boolean

	@OriginalMember(owner = "client!uq", name = "b", descriptor = "(I)V")
	fun release()

	@OriginalMember(owner = "client!uq", name = "a", descriptor = "(II)V")
	fun allocate(@OriginalArg(1) count: Int)

	@OriginalMember(owner = "client!uq", name = "a", descriptor = "(ZZ)Lclient!jaclib/memory/Buffer;")
	fun lock(): Buffer?

	@OriginalMember(owner = "client!uq", name = "a", descriptor = "(B)Lclient!baa;")
	fun getDataType(): DataType?
}
