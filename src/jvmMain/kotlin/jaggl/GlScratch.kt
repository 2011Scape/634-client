package jaggl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.lwjgl.system.MemoryUtil

/**
 * Not part of the original client: native memory for the jaggl calls that take a Java array and an
 * offset. libjaggl pinned the array; LWJGL takes an address, so the array's elements from the offset
 * on are copied into one of two growable direct buffers per thread (the toolkit 1 context moves
 * between threads), and copied back for the calls that write into the array. The buffers are the
 * JDK's direct buffers, so a thread's are freed with it.
 */
internal class GlScratch private constructor() {

    private val slots = arrayOfNulls<ByteBuffer>(2)

    private fun buffer(slot: Int, bytes: Int): ByteBuffer {
        var buffer = slots[slot]
        if (buffer == null || buffer.capacity() < bytes) {
            var capacity = if (buffer == null) MIN_CAPACITY else buffer.capacity()
            while (capacity < bytes) capacity *= 2
            buffer = ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder())
            slots[slot] = buffer
        }
        buffer!!.clear()
        return buffer
    }

    companion object {

        /* At least this many bytes, so a GL query writing more than the array holds stays in bounds. */
        private const val MIN_CAPACITY = 64 * 1024

        private val local: ThreadLocal<GlScratch> = ThreadLocal.withInitial { GlScratch() }

        /** The elements of [array] from [offset] on, or at most [count] of them. */
        private fun count(size: Int, offset: Int, count: Int): Int = minOf(size - offset, count).coerceAtLeast(0)

        /**
         * [reserve]: how many bytes GL reads from the address. Where the array holds fewer, the rest
         * is zeroes, rather than whatever lies past the copy.
         */
        fun bytes(array: ByteArray?, offset: Int, count: Int = Int.MAX_VALUE, slot: Int = 0, reserve: Int = 0): Long {
            if (array == null) return 0L
            val n = count(array.size, offset, count)
            val buffer = local.get().buffer(slot, maxOf(n, reserve))
            buffer.put(array, offset, n)
            val address = MemoryUtil.memAddress0(buffer)
            if (reserve > n) MemoryUtil.memSet(address + n, 0, (reserve - n).toLong())
            return address
        }

        fun ints(array: IntArray?, offset: Int, count: Int = Int.MAX_VALUE, slot: Int = 0): Long {
            if (array == null) return 0L
            val n = count(array.size, offset, count)
            val buffer = local.get().buffer(slot, n * 4)
            buffer.asIntBuffer().put(array, offset, n)
            return MemoryUtil.memAddress0(buffer)
        }

        fun floats(array: FloatArray?, offset: Int, count: Int = Int.MAX_VALUE, slot: Int = 0): Long {
            if (array == null) return 0L
            val n = count(array.size, offset, count)
            val buffer = local.get().buffer(slot, n * 4)
            buffer.asFloatBuffer().put(array, offset, n)
            return MemoryUtil.memAddress0(buffer)
        }

        /** Space for a call that writes [bytes] bytes or fewer, not filled from an array. */
        fun space(bytes: Int, slot: Int = 0): Long = MemoryUtil.memAddress0(local.get().buffer(slot, bytes))

        fun readBytes(array: ByteArray?, offset: Int, count: Int = Int.MAX_VALUE, slot: Int = 0) {
            if (array == null) return
            val n = count(array.size, offset, count)
            val buffer = local.get().slots[slot]!!
            buffer.clear()
            buffer.get(array, offset, n)
        }

        fun readInts(array: IntArray?, offset: Int, count: Int = Int.MAX_VALUE, slot: Int = 0) {
            if (array == null) return
            val n = count(array.size, offset, count)
            val buffer = local.get().slots[slot]!!
            buffer.clear()
            buffer.asIntBuffer().get(array, offset, n)
        }

        fun readFloats(array: FloatArray?, offset: Int, count: Int = Int.MAX_VALUE, slot: Int = 0) {
            if (array == null) return
            val n = count(array.size, offset, count)
            val buffer = local.get().slots[slot]!!
            buffer.clear()
            buffer.asFloatBuffer().get(array, offset, n)
        }
    }
}
