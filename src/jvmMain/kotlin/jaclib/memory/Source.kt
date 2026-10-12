package jaclib.memory

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jaclib/memory/Source")
actual interface Source {

	@OriginalMember(owner = "client!jaclib/memory/Source", name = "getSize", descriptor = "()I")
	actual fun getSize(): Int

	@OriginalMember(owner = "client!jaclib/memory/Source", name = "getAddress", descriptor = "()J")
	actual fun getAddress(): Long
}
