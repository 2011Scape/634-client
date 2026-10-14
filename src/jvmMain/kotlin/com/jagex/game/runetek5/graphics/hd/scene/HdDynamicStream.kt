/*
 * Not part of the original client: 634HD (toolkit 6). Streamed dynamic geometry.
 *
 * The OpenGL toolkit re-uploads an animated model (players, NPCs, animated locs, projectiles, spot
 * animations: the copy targets of Model.copy) every frame into a buffer of its own with
 * glBufferSubData, and many entities share one copy target, so one buffer is rewritten between
 * draws. Here every such upload takes the next range of one large stream buffer instead; when the
 * buffer is full it is orphaned (the driver hands out fresh storage while earlier draws still read
 * the old) and filling starts again from the front. A range's bytes are kept, so a model drawn in a
 * later frame without uploading again (its range may have been overwritten) is written again first.
 *
 * A range is an ArrayBufferGL (or ElementsBufferGL) whose address is its offset in the stream buffer,
 * so the toolkit's pointer and draw calls (getAddress() + offset) need no change.
 */
package com.jagex.game.runetek5.graphics.hd.scene

import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.buffer.ElementsBufferGL
import com.jagex.game.runetek5.graphics.hd.HdToolkit
import com.jagex.game.runetek5.graphics.hd.gl.HdGl

class HdDynamicStream(private val toolkit: HdToolkit, private val target: Int) {

    companion object {
        /** The GL names of the stream buffers: append-only, so their write counts mean nothing (HdShadows). */
        @JvmStatic
        val handles: HashSet<Int> = HashSet()

        /** Bumped whenever any stream buffer is orphaned: ranges written before are gone (HdShadows). */
        @JvmStatic
        var orphans: Int = 0
            private set

        private const val CAPACITY: Int = 16 * 1024 * 1024
        private const val ALIGN: Int = 64
        const val ARRAY: Int = 34962
        const val ELEMENTS: Int = 34963
    }

    private var handle: Int = 0

    private var offset: Int = 0

    // set at the start of a frame once the buffer is half full: the next write starts fresh storage,
    // so the buffer is never orphaned in the middle of a frame (shadow casters recorded earlier in
    // the frame keep their data until the shadow map is drawn)
    private var orphanNext: Boolean = false

    /** The ranges written into the current storage: offset to size. */
    private val written: java.util.TreeMap<Int, Int> = java.util.TreeMap()

    /** At the start of a frame (HdToolkit.beginScene). */
    fun startFrame() {
        if (offset > CAPACITY / 2) {
            orphanNext = true
        }
    }

    /** Bumped each time the buffer is orphaned: a range written in an earlier generation is stale. */
    private var generation: Int = 0

    /** A model's piece of the stream, valid while its generation is the stream's. */
    open inner class Range {
        @JvmField var address: Long = 0L
        @JvmField var size: Int = 0
        @JvmField var written: Int = -1
        @JvmField var bytes: ByteArray = ByteArray(0)

        fun id(): Int = handle

        /** Writes the range again if the stream moved on since it was written. */
        fun ensureCurrent() {
            if (written != generation && size > 0) {
                place(this)
            }
        }
    }

    inner class VertexRange : Range(), ArrayBufferGL {
        @JvmField var stride: Int = 0

        override fun getId(): Int = id()

        override fun putArray(stride: Int, size: Int, data: ByteArray?) {
            this.stride = stride
            write(this, data!!, size)
        }

        override fun getAddress(): Long = address

        override fun getStride(): Int = stride
    }

    inner class ElementRange : Range(), ElementsBufferGL {
        override fun getId(): Int = id()

        override fun getIndexType(): Int = 5123

        override fun putElements(data: ByteArray?, size: Int) {
            write(this, data!!, size)
        }

        override fun getAddress(): Long = address
    }

    fun write(range: Range, data: ByteArray, size: Int) {
        if (range.bytes.size < size) {
            range.bytes = ByteArray(maxOf(size, range.bytes.size * 3 / 2))
        }
        System.arraycopy(data, 0, range.bytes, 0, size)
        range.size = size
        place(range)
    }

    private fun place(range: Range) {
        if (handle == 0) {
            val ids: IntArray = IntArray(1)
            HdGl.glGenBuffersARB(1, ids, 0)
            handle = ids[0]
            handles.add(com.jagex.game.runetek5.graphics.hd.gl.CoreGl.bufferName(handle))
            bind(range)
            HdGl.glBufferDataARBub(target, CAPACITY, null, 0, 35040)
        }
        val size: Int = range.size
        if (offset + size > CAPACITY || (orphanNext && offset > 0)) {
            // shadow casters drawn from the old storage keep a copy of their ranges (HdShadows)
            HdGl.shadows?.beforeStreamOrphan(com.jagex.game.runetek5.graphics.hd.gl.CoreGl.bufferName(handle), written)
            written.clear()
            // orphan: draws still in flight keep the old storage
            bind(range)
            HdGl.glBufferDataARBub(target, CAPACITY, null, 0, 35040)
            offset = 0
            generation++
            orphans++
            orphanNext = false
        }
        bind(range)
        HdGl.glBufferSubDataARBub(target, offset, size, range.bytes, 0)
        written[offset] = size
        range.address = offset.toLong()
        range.written = generation
        offset = (offset + size + ALIGN - 1) / ALIGN * ALIGN
    }

    /** Binds the stream through the toolkit, so its binding cache stays true. */
    private fun bind(range: Range) {
        if (target == ARRAY) {
            toolkit.setArrayBuffer(range as ArrayBufferGL)
        } else {
            toolkit.setElementsBuffer(range as ElementsBufferGL)
        }
    }

    fun release() {
        if (handle != 0) {
            val ids: IntArray = intArrayOf(handle)
            HdGl.glDeleteBuffersARB(1, ids, 0)
            handle = 0
        }
    }
}
