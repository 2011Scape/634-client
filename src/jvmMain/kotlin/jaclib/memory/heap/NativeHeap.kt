package jaclib.memory.heap

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!jaclib/memory/heap/NativeHeap")
actual class NativeHeap @OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "<init>", descriptor = "(I)V") actual constructor(@OriginalArg(0) arg0: Int) {

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "peer", descriptor = "J")
	private var peer: Long = 0L

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "b", descriptor = "I")
	private val b: Int

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "a", descriptor = "Z")
	private var a: Boolean = false

	init {
		this.b = arg0
		this.allocateHeap(this.b)
		this.a = true
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "allocateHeap", descriptor = "(I)V")
	private external fun allocateHeap(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "a", descriptor = "()Z")
	@Synchronized
	actual fun a(): Boolean {
		return this.a
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "a", descriptor = "(IZ)Lclient!jaclib/memory/heap/NativeHeapBuffer;")
	actual fun a(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Boolean): NativeHeapBuffer {
		if (!this.a) {
			throw IllegalStateException()
		}
		return NativeHeapBuffer(this, this.allocateBuffer(arg0, arg1), arg0)
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "finalize", descriptor = "()V")
	@Synchronized
	@Throws(Throwable::class)
	actual fun finalize() {
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
		this.b()
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "b", descriptor = "()V")
	@Synchronized
	actual fun b() {
		if (this.a) {
			this.deallocateHeap()
		}
		this.a = false
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "allocateBuffer", descriptor = "(IZ)I")
	@Synchronized private external fun allocateBuffer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Boolean): Int

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "get", descriptor = "(I[BIII)V")
	@Synchronized private external fun get(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: ByteArray?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int)

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "put", descriptor = "(I[BIII)V")
	@Synchronized actual external fun put(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: ByteArray?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int)

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "deallocateHeap", descriptor = "()V")
	private external fun deallocateHeap()

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "getBufferAddress", descriptor = "(I)J")
	@Synchronized actual external fun getBufferAddress(@OriginalArg(0) arg0: Int): Long

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "deallocateBuffer", descriptor = "(I)V")
	@Synchronized actual external fun deallocateBuffer(@OriginalArg(0) arg0: Int)
}
