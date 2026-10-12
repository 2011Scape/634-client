package jaclib.memory

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jaclib/memory/Source")
interface Source {

	@OriginalMember(owner = "client!jaclib/memory/Source", name = "getSize", descriptor = "()I")
	fun getSize(): Int

	@OriginalMember(owner = "client!jaclib/memory/Source", name = "getAddress", descriptor = "()J")
	fun getAddress(): Long
}
