package jaclib.memory

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jaclib/memory/Buffer")
actual interface Buffer {

	@OriginalMember(owner = "client!jaclib/memory/Buffer", name = "getSize", descriptor = "()I")
	actual fun getSize(): Int

	@OriginalMember(owner = "client!jaclib/memory/Buffer", name = "getAddress", descriptor = "()J")
	actual fun getAddress(): Long

	@OriginalMember(owner = "client!jaclib/memory/Buffer", name = "a", descriptor = "([BIII)V")
	actual fun a(@OriginalArg(0) arg0: ByteArray?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)
}
