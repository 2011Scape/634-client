/*
 * Not part of the original client: the WebGL 2 emulator and jaclib memory behind the Java OpenGL
 * toolkit (1) in a browser, from void-client's jsMain (milestone 5).
 */
package jaclib.memory.heap

private object NativeHeapMemory {
    private var nextHandle = 1
    val buffers = HashMap<Int, ByteArray>()

    fun allocate(size: Int): Int {
        val handle = nextHandle++
        buffers[handle] = ByteArray(size)
        return handle
    }

    fun get(handle: Int, dst: ByteArray?, dstOffset: Int, srcOffset: Int, length: Int) {
        val data = buffers[handle] ?: return
        if (dst == null) return
        data.copyInto(dst, dstOffset, srcOffset, srcOffset + length)
    }

    fun put(handle: Int, src: ByteArray?, srcOffset: Int, dstOffset: Int, length: Int) {
        val data = buffers[handle] ?: return
        if (src == null) return
        src.copyInto(data, dstOffset, srcOffset, srcOffset + length)
    }

    fun deallocate(handle: Int) {
        buffers.remove(handle)
    }
}

fun nativeHeapBytes(address: Long): ByteArray? = NativeHeapMemory.buffers[address.toInt()]

actual class NativeHeap actual constructor(arg0: Int) {
    val b: Int = arg0

    private var allocated = true

    actual fun b() {
        allocated = false
    }

    val peer: Long
        get() = 0L

    actual fun a(): Boolean = allocated

    actual fun a(arg0: Int, arg1: Boolean): NativeHeapBuffer {
        check(allocated)
        return NativeHeapBuffer(this, this.allocateBuffer(arg0, arg1), arg0)
    }

    var a: Boolean
        get() = allocated
        set(value) { allocated = value }

    fun allocateHeap(arg0: Int) {
    }

    actual fun finalize() {
        this.b()
    }

    fun allocateBuffer(arg0: Int, arg1: Boolean): Int = NativeHeapMemory.allocate(arg0)

    fun get(arg0: Int, arg1: ByteArray?, arg2: Int, arg3: Int, arg4: Int) {
        NativeHeapMemory.get(arg0, arg1, arg2, arg3, arg4)
    }

    actual fun put(arg0: Int, arg1: ByteArray?, arg2: Int, arg3: Int, arg4: Int) {
        NativeHeapMemory.put(arg0, arg1, arg2, arg3, arg4)
    }

    fun deallocateHeap() {
    }

    actual fun getBufferAddress(arg0: Int): Long = arg0.toLong()

    actual fun deallocateBuffer(arg0: Int) {
        NativeHeapMemory.deallocate(arg0)
    }
}
