package jagex3.jagmisc

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jagex3/jagmisc/jagmisc")
actual class jagmisc @OriginalMember(owner = "client!jagex3/jagmisc/jagmisc", name = "<init>", descriptor = "()V") private actual constructor() {

	actual companion object {

		@OriginalMember(owner = "client!jagex3/jagmisc/jagmisc", name = "getTotalPhysicalMemory", descriptor = "()J")
		@JvmStatic private external fun getTotalPhysicalMemory(): Long

		@OriginalMember(owner = "client!jagex3/jagmisc/jagmisc", name = "getAvailablePhysicalMemory", descriptor = "()J")
		@JvmStatic actual external fun getAvailablePhysicalMemory(): Long

		@OriginalMember(owner = "client!jagex3/jagmisc/jagmisc", name = "ping0", descriptor = "(BBBBJ)I")
		@JvmStatic private external fun ping0(@OriginalArg(0) arg0: Byte, @OriginalArg(1) arg1: Byte, @OriginalArg(2) arg2: Byte, @OriginalArg(3) arg3: Byte, @OriginalArg(4) arg4: Long): Int

		@OriginalMember(owner = "client!jagex3/jagmisc/jagmisc", name = "init", descriptor = "()Z")
		@JvmStatic actual external fun init(): Boolean

		@OriginalMember(owner = "client!jagex3/jagmisc/jagmisc", name = "nanoTime", descriptor = "()J")
		@JvmStatic actual external fun nanoTime(): Long

		@OriginalMember(owner = "client!jagex3/jagmisc/jagmisc", name = "quit", descriptor = "()V")
		@JvmStatic
		actual fun quit() {
			Quit0()
		}

		@OriginalMember(owner = "client!jagex3/jagmisc/jagmisc", name = "ping", descriptor = "(BBBBJ)I")
		@JvmStatic
		@Throws(Throwable::class)
		actual fun ping(@OriginalArg(0) arg0: Byte, @OriginalArg(1) arg1: Byte, @OriginalArg(2) arg2: Byte, @OriginalArg(3) arg3: Byte, @OriginalArg(4) arg4: Long): Int {
			@Pc(6) val local6: Int = ping0(arg0, arg1, arg2, arg3, arg4)
			if (local6 < 0) {
				throw Exception(java.lang.String.valueOf(local6))
			}
			return local6
		}

		@OriginalMember(owner = "client!jagex3/jagmisc/jagmisc", name = "Quit0", descriptor = "()V")
		@JvmStatic private external fun Quit0()
	}
}
