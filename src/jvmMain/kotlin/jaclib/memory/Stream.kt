package jaclib.memory

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jaclib/memory/Stream")
actual class Stream @OriginalMember(owner = "client!jaclib/memory/Stream", name = "<init>", descriptor = "(I)V") private actual constructor(@OriginalArg(0) arg0: Int) {

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "a", descriptor = "I")
	private var a: Int = 0

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "b", descriptor = "Lclient!jaclib/memory/Buffer;")
	private var b: Buffer? = null

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "c", descriptor = "I")
	private var c: Int = 0

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "d", descriptor = "I")
	private var d: Int = 0

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "e", descriptor = "[B")
	private val e: ByteArray = ByteArray(arg0)

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "<init>", descriptor = "()V")
	actual constructor() : this(4096)

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "<init>", descriptor = "(Lclient!jaclib/memory/Buffer;)V")
	actual constructor(@OriginalArg(0) buffer: Buffer?) : this(buffer, 0, buffer!!.getSize())

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "<init>", descriptor = "(Lclient!jaclib/memory/Buffer;II)V")
	actual constructor(@OriginalArg(0) buffer: Buffer?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) : this(if (buffer!!.getSize() >= 4096) 4096 else buffer.getSize()) {
		this.a(buffer, arg1, arg2)
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "a", descriptor = "(I)V")
	actual fun a(@OriginalArg(0) arg0: Int) {
		if (this.c + 1 >= this.e.size) {
			this.a()
		}
		this.e[this.c++] = arg0.toByte()
		this.e[this.c++] = (arg0 shr 8).toByte()
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "a", descriptor = "(F)V")
	actual fun a(@OriginalArg(0) arg0: Float) {
		if (this.e.size <= this.c + 3) {
			this.a()
		}
		@Pc(12) val local12: Int = floatToRawIntBits(arg0)
		this.e[this.c++] = (local12 shr 24).toByte()
		this.e[this.c++] = (local12 shr 16).toByte()
		this.e[this.c++] = (local12 shr 8).toByte()
		this.e[this.c++] = local12.toByte()
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "a", descriptor = "(IIII)V")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		if (this.c + 3 >= this.e.size) {
			this.a()
		}
		this.e[this.c++] = arg0.toByte()
		this.e[this.c++] = arg1.toByte()
		this.e[this.c++] = arg2.toByte()
		this.e[this.c++] = arg3.toByte()
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "a", descriptor = "(Lclient!jaclib/memory/Buffer;)V")
	actual fun a(@OriginalArg(0) buffer: Buffer?) {
		this.a(buffer, 0, buffer!!.getSize())
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "b", descriptor = "(I)V")
	actual fun b(@OriginalArg(0) arg0: Int) {
		if (this.c + 3 >= this.e.size) {
			this.a()
		}
		this.e[this.c++] = arg0.toByte()
		this.e[this.c++] = (arg0 shr 8).toByte()
		this.e[this.c++] = (arg0 shr 16).toByte()
		this.e[this.c++] = (arg0 shr 24).toByte()
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "a", descriptor = "()V")
	actual fun a() {
		if (this.c <= 0) {
			return
		}
		if (this.a < this.c + this.d) {
			throw RuntimeException()
		}
		this.b!!.a(this.e, 0, this.d, this.c)
		this.d += this.c
		this.c = 0
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "a", descriptor = "(Lclient!jaclib/memory/Buffer;II)V")
	private fun a(@OriginalArg(0) buffer: Buffer?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
		this.a()
		this.a = arg1 + arg2
		this.d = arg1
		this.b = buffer
		if (this.a > buffer!!.getSize()) {
			throw RuntimeException()
		}
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "b", descriptor = "(IIII)V")
	actual fun b(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		if (this.e.size <= this.c + 3) {
			this.a()
		}
		this.e[this.c++] = arg2.toByte()
		this.e[this.c++] = arg1.toByte()
		this.e[this.c++] = arg0.toByte()
		this.e[this.c++] = arg3.toByte()
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "c", descriptor = "(I)V")
	actual fun c(@OriginalArg(0) arg0: Int) {
		if (this.e.size <= this.c + 3) {
			this.a()
		}
		this.e[this.c++] = (arg0 shr 16).toByte()
		this.e[this.c++] = (arg0 shr 8).toByte()
		this.e[this.c++] = arg0.toByte()
		this.e[this.c++] = (arg0 shr 24).toByte()
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "b", descriptor = "()I")
	actual fun b(): Int {
		return this.d + this.c
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "d", descriptor = "(I)V")
	actual fun d(@OriginalArg(0) arg0: Int) {
		if (this.e.size <= this.c + 1) {
			this.a()
		}
		this.e[this.c++] = (arg0 shr 8).toByte()
		this.e[this.c++] = arg0.toByte()
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "e", descriptor = "(I)V")
	actual fun e(@OriginalArg(0) arg0: Int) {
		this.a()
		this.d = arg0
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "b", descriptor = "(F)V")
	actual fun b(@OriginalArg(0) arg0: Float) {
		if (this.e.size <= this.c + 3) {
			this.a()
		}
		@Pc(12) val local12: Int = floatToRawIntBits(arg0)
		this.e[this.c++] = local12.toByte()
		this.e[this.c++] = (local12 shr 8).toByte()
		this.e[this.c++] = (local12 shr 16).toByte()
		this.e[this.c++] = (local12 shr 24).toByte()
	}

	@OriginalMember(owner = "client!jaclib/memory/Stream", name = "f", descriptor = "(I)V")
	actual fun f(@OriginalArg(0) arg0: Int) {
		if (this.c >= this.e.size) {
			this.a()
		}
		this.e[this.c++] = arg0.toByte()
	}

	actual companion object {

		@OriginalMember(owner = "client!jaclib/memory/Stream", name = "floatToRawIntBits", descriptor = "(F)I")
		// Not part of the original client: libjaclib's natives here are the JDK's equivalents.
		@JvmStatic actual fun floatToRawIntBits(@OriginalArg(0) arg0: Float): Int {
			return java.lang.Float.floatToRawIntBits(arg0)
		}

		@OriginalMember(owner = "client!jaclib/memory/Stream", name = "getLSB", descriptor = "(I)B")
		@JvmStatic private fun getLSB(@OriginalArg(0) arg0: Int): Byte {
			// The int's first byte in memory: its least significant byte on a little-endian machine.
			return if (java.nio.ByteOrder.nativeOrder() == java.nio.ByteOrder.BIG_ENDIAN) (arg0 ushr 24).toByte() else arg0.toByte()
		}

		@OriginalMember(owner = "client!jaclib/memory/Stream", name = "c", descriptor = "()Z")
		@JvmStatic
		actual fun c(): Boolean {
			return getLSB(-65536).toInt() == -1
		}
	}
}
