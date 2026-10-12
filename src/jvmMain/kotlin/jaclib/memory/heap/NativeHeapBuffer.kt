package jaclib.memory.heap

import jaclib.memory.Buffer
import jaclib.memory.Source
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jaclib/memory/heap/NativeHeapBuffer")
actual class NativeHeapBuffer @OriginalMember(owner = "client!jaclib/memory/heap/NativeHeapBuffer", name = "<init>", descriptor = "(Lclient!jaclib/memory/heap/NativeHeap;II)V") actual constructor(@OriginalArg(0) nativeHeap: NativeHeap?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) : Buffer, Source {

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeapBuffer", name = "c", descriptor = "Z")
	private var c: Boolean = true

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeapBuffer", name = "b", descriptor = "I")
	@JvmField
	actual val b: Int

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeapBuffer", name = "a", descriptor = "Lclient!jaclib/memory/heap/NativeHeap;")
	private val a: NativeHeap?

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeapBuffer", name = "d", descriptor = "I")
	private val d: Int

	init {
		this.b = arg2
		this.a = nativeHeap
		this.d = arg1
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeapBuffer", name = "getAddress", descriptor = "()J")
	actual override fun getAddress(): Long {
		return this.a!!.getBufferAddress(this.d)
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeapBuffer", name = "a", descriptor = "()Z")
	@Synchronized
	private fun a(): Boolean {
		return this.a!!.a() && this.c
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeapBuffer", name = "b", descriptor = "()V")
	@Synchronized
	private fun b() {
		if (this.a()) {
			this.a!!.deallocateBuffer(this.d)
		}
		this.c = false
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeapBuffer", name = "a", descriptor = "([BIII)V")
	@Synchronized
	actual override fun a(@OriginalArg(0) arg0: ByteArray?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		if ((arg1 < 0) or (arg0 == null) or !this.a() or (arg3 + arg1 > arg0!!.size) or (arg2 < 0) or (this.b < arg2 + arg3)) {
			throw RuntimeException()
		}
		this.a!!.put(this.d, arg0, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeapBuffer", name = "finalize", descriptor = "()V")
	@Synchronized
	@Throws(Throwable::class)
	actual fun finalize() {
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
		this.b()
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeapBuffer", name = "getSize", descriptor = "()I")
	actual override fun getSize(): Int {
		return this.b
	}
}
