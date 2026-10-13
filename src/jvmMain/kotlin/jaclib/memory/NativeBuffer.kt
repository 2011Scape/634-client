package jaclib.memory

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

/*
 * Not part of the original client: libjaclib's copies to and from the buffer's address are JVM code
 * (NativeMemory).
 */
@OriginalClass("client!jaclib/memory/NativeBuffer")
actual open class NativeBuffer actual constructor() : Buffer, Source {

	@OriginalMember(owner = "client!jaclib/memory/NativeBuffer", name = "a", descriptor = "J")
	private var a: Long = 0L

	@OriginalMember(owner = "client!jaclib/memory/NativeBuffer", name = "b", descriptor = "I")
	private var b: Int = -1

	@OriginalMember(owner = "client!jaclib/memory/NativeBuffer", name = "getAddress", descriptor = "()J")
	actual final override fun getAddress(): Long {
		return this.a
	}

	@OriginalMember(owner = "client!jaclib/memory/NativeBuffer", name = "a", descriptor = "([BIII)V")
	actual override fun a(@OriginalArg(0) arg0: ByteArray?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		if ((arg2 + arg3 > this.b) or (arg2 < 0) or (arg0!!.size < arg3 + arg1) or (arg1 < 0) or (arg0 == null) or (this.a == 0L)) {
			throw RuntimeException()
		}
		this.put(this.a, arg0, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!jaclib/memory/NativeBuffer", name = "getSize", descriptor = "()I")
	actual final override fun getSize(): Int {
		return this.b
	}

	@OriginalMember(owner = "client!jaclib/memory/NativeBuffer", name = "a", descriptor = "(JI)V")
	actual fun a(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int) {
		this.b = arg1
		this.a = arg0
	}

	@OriginalMember(owner = "client!jaclib/memory/NativeBuffer", name = "get", descriptor = "(J[BIII)V")
	private fun get(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: ByteArray?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int) {
		NativeMemory.get(arg0 + arg3, arg1!!, arg2, arg4)
	}

	@OriginalMember(owner = "client!jaclib/memory/NativeBuffer", name = "put", descriptor = "(J[BIII)V")
	private fun put(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: ByteArray?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int) {
		NativeMemory.put(arg1!!, arg2, arg0 + arg3, arg4)
	}
}
