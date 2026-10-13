package jaclib.memory.heap

import jaclib.memory.NativeMemory
import org.lwjgl.system.MemoryUtil
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

/*
 * Not part of the original client: libjaclib's heap is replaced by JVM code. Each buffer is a block
 * of native memory from LWJGL's allocator, so getBufferAddress is a real pointer, as the GL driver
 * (client-side vertex arrays, glBufferDataARB) and libhw3d read and write through it. `peer` is
 * only a marker that the heap is allocated.
 */
@OriginalClass("client!jaclib/memory/heap/NativeHeap")
actual class NativeHeap @OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "<init>", descriptor = "(I)V") actual constructor(@OriginalArg(0) arg0: Int) {

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "peer", descriptor = "J")
	private var peer: Long = 0L

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "b", descriptor = "I")
	private val b: Int

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "a", descriptor = "Z")
	private var a: Boolean = false

	// Not part of the original client: the blocks by handle (see the class comment).
	private val addresses: HashMap<Int, Long> = HashMap<Int, Long>()
	private val sizes: HashMap<Int, Int> = HashMap<Int, Int>()
	private var nextHandle: Int = 1

	init {
		this.b = arg0
		this.allocateHeap(this.b)
		this.a = true
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "allocateHeap", descriptor = "(I)V")
	private fun allocateHeap(@OriginalArg(0) arg0: Int) {
		this.peer = 1L
	}

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
	@Synchronized private fun allocateBuffer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Boolean): Int {
		val address: Long = MemoryUtil.nmemCallocChecked(1L, maxOf(arg0, 1).toLong())
		val handle: Int = this.nextHandle++
		this.addresses.put(handle, address)
		this.sizes.put(handle, arg0)
		return handle
	}

	// Not part of the original client: the block of a handle, with the range checked.
	private fun address(handle: Int, offset: Int, length: Int): Long {
		val address: Long = this.addresses.get(handle) ?: throw IllegalStateException("no buffer " + handle)
		val size: Int = this.sizes.get(handle)!!
		if (offset < 0 || length < 0 || offset + length > size) {
			throw IndexOutOfBoundsException("buffer " + handle + " of " + size + " bytes: " + offset + "+" + length)
		}
		return address + offset
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "get", descriptor = "(I[BIII)V")
	@Synchronized private fun get(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: ByteArray?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int) {
		NativeMemory.get(this.address(arg0, arg3, arg4), arg1!!, arg2, arg4)
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "put", descriptor = "(I[BIII)V")
	@Synchronized actual fun put(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: ByteArray?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int) {
		NativeMemory.put(arg1!!, arg2, this.address(arg0, arg3, arg4), arg4)
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "deallocateHeap", descriptor = "()V")
	@Synchronized private fun deallocateHeap() {
		for (address in this.addresses.values) {
			MemoryUtil.nmemFree(address)
		}
		this.addresses.clear()
		this.sizes.clear()
		this.peer = 0L
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "getBufferAddress", descriptor = "(I)J")
	@Synchronized actual fun getBufferAddress(@OriginalArg(0) arg0: Int): Long {
		return this.addresses.get(arg0) ?: 0L
	}

	@OriginalMember(owner = "client!jaclib/memory/heap/NativeHeap", name = "deallocateBuffer", descriptor = "(I)V")
	@Synchronized actual fun deallocateBuffer(@OriginalArg(0) arg0: Int) {
		val address: Long? = this.addresses.remove(arg0)
		this.sizes.remove(arg0)
		if (address != null) {
			MemoryUtil.nmemFree(address)
		}
	}
}
