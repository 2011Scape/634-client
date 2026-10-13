package jaclib.memory

import org.lwjgl.system.MemoryUtil

/**
 * Not part of the original client: copies between a Java byte array and native memory, which
 * libjaclib did for NativeBuffer and NativeHeap.
 */
internal object NativeMemory {

    fun put(src: ByteArray, srcOffset: Int, address: Long, length: Int) {
        if (srcOffset < 0 || length < 0 || srcOffset + length > src.size) throw IndexOutOfBoundsException()
        if (address == 0L) throw NullPointerException("address 0")
        if (length > 0) MemoryUtil.memByteBuffer(address, length).put(src, srcOffset, length)
    }

    fun get(address: Long, dst: ByteArray, dstOffset: Int, length: Int) {
        if (dstOffset < 0 || length < 0 || dstOffset + length > dst.size) throw IndexOutOfBoundsException()
        if (address == 0L) throw NullPointerException("address 0")
        if (length > 0) MemoryUtil.memByteBuffer(address, length).get(dst, dstOffset, length)
    }
}
