package com.jagex.game.runetek5.toolkit.opengl.buffer

import jaclib.memory.Buffer
import jaclib.memory.Source
import jaclib.memory.heap.NativeHeapBuffer
import jaggl.MapBuffer
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.Identifiable
import com.jagex.game.runetek5.toolkit.GlxToolkit
import kotlin.jvm.JvmField

@OriginalClass("client!vi")
abstract class OpenGlArbBuffer : Identifiable {

	@OriginalMember(owner = "client!vi", name = "e", descriptor = "I")
	private var initialCapacity: Int = 0

	@OriginalMember(owner = "client!vi", name = "m", descriptor = "I")
	private var capacity: Int = 0

	@OriginalMember(owner = "client!vi", name = "n", descriptor = "Lclient!jaclib/memory/heap/NativeHeapBuffer;")
	private var nativeheapBuffer: NativeHeapBuffer? = null

	@OriginalMember(owner = "client!vi", name = "b", descriptor = "I")
	private var mapState: Int = 0

	@OriginalMember(owner = "client!vi", name = "l", descriptor = "I")
	private var activeHandle: Int = -1

	@OriginalMember(owner = "client!vi", name = "y", descriptor = "Lclient!bga;")
	@JvmField
	val nativeToolkit: GlxToolkit?

	@OriginalMember(owner = "client!vi", name = "u", descriptor = "I")
	private val bufferTarget: Int

	@OriginalMember(owner = "client!vi", name = "p", descriptor = "Z")
	private val drawStream: Boolean

	@OriginalMember(owner = "client!vi", name = "<init>", descriptor = "(Lclient!bga;IZ)V")
	constructor(@OriginalArg(0) glxToolkit: GlxToolkit?, @OriginalArg(1) bufferTarget: Int, @OriginalArg(2) drawStream: Boolean) {
		this.nativeToolkit = glxToolkit
		this.bufferTarget = bufferTarget
		this.drawStream = drawStream
	}

	@OriginalMember(owner = "client!vi", name = "a", descriptor = "(ILclient!jaggl/MapBuffer;Z)Lclient!jaclib/memory/Buffer;")
	fun map(@OriginalArg(1) mapBuffer: MapBuffer?): Buffer? {
		if (this.mapState == 0) {
			this.initialize()
			if (this.activeHandle <= 0) {
				this.mapState = 2
				return this.nativeheapBuffer
			}
			OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle)
			OpenGL.glBufferDataARBub(this.bufferTarget, this.initialCapacity, null, 0, if (this.drawStream) 35040 else 35044)
			if (this.capacity <= this.nativeToolkit!!.stagingBuffer!!.b) {
				this.mapState = 1
				return this.nativeToolkit.stagingBuffer
			}
			if (!mapBuffer!!.a() && mapBuffer.a(this.bufferTarget, this.capacity, 35001)) {
				this.mapState = 2
				return mapBuffer
			}
		}
		return null
	}

	@OriginalMember(owner = "client!vi", name = "a", descriptor = "(ILclient!jaclib/memory/Source;I)Z")
	fun createFromSource(@OriginalArg(1) source: Source?, @OriginalArg(2) size: Int): Boolean {
		if (size > this.initialCapacity) {
			this.initialize()
			if (this.activeHandle <= 0) {
				throw RuntimeException("ARGH!")
			}
			OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle)
			OpenGL.glBufferDataARBa(this.bufferTarget, size, source!!.getAddress(), if (this.drawStream) 35040 else 35044)
			this.nativeToolkit!!.textureBuffersSizeOccupied += size - this.capacity
			this.initialCapacity = size
		} else if (this.activeHandle > 0) {
			OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle)
			OpenGL.glBufferSubDataARBa(this.bufferTarget, 0, this.capacity, source!!.getAddress())
			this.nativeToolkit!!.textureBuffersSizeOccupied += size - this.capacity
		} else {
			throw RuntimeException("ARGH!")
		}
		this.capacity = size
		return true
	}

	@OriginalMember(owner = "client!vi", name = "b", descriptor = "(I)V")
	open fun deallocate() {
		if (this.activeHandle > 0) {
			this.nativeToolkit!!.requestTextureBufferDeletion(this.capacity, this.activeHandle)
			this.activeHandle = -1
		}
	}

	@OriginalMember(owner = "client!vi", name = "c", descriptor = "(I)J")
	fun getBufferAddress(): Long {
		return if (this.activeHandle == 0) this.nativeheapBuffer!!.getAddress() else 0L
	}

	@OriginalMember(owner = "client!vi", name = "a", descriptor = "(Lclient!jaggl/MapBuffer;I)Z")
	fun unmap(@OriginalArg(0) mapBuffer: MapBuffer?): Boolean {
		@Pc(5) var success: Boolean = true
		if (this.mapState != 0) {
			if (this.activeHandle > 0) {
				OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle)
				if (this.mapState == 1) {
					OpenGL.glBufferSubDataARBa(this.bufferTarget, 0, this.initialCapacity, this.nativeToolkit!!.stagingBuffer!!.getAddress())
				} else {
					success = mapBuffer!!.b()
				}
			}
			this.mapState = 0
		}
		return success
	}

	@OriginalMember(owner = "client!vi", name = "a", descriptor = "(II)V")
	open fun create(@OriginalArg(1) size: Int) {
		if (this.initialCapacity < size) {
			this.initialize()
			if (this.activeHandle <= 0) {
				this.nativeheapBuffer = this.nativeToolkit!!.generateNativeHeapBuffer(false, size)
			} else {
				OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle)
				OpenGL.glBufferDataARBub(this.bufferTarget, size, null, 0, if (this.drawStream) 35040 else 35044)
				this.nativeToolkit!!.textureBuffersSizeOccupied += size - this.initialCapacity
			}
			this.initialCapacity = size
		}
		this.capacity = size
	}

	@OriginalMember(owner = "client!vi", name = "e", descriptor = "(I)V")
	fun bind() {
		if (this.nativeToolkit!!.GLARBVertexBufferObject) {
			OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle)
		}
	}

	@OriginalMember(owner = "client!vi", name = "a", descriptor = "(Z)I")
	override fun getId(): Int {
		return this.capacity
	}

	@OriginalMember(owner = "client!vi", name = "finalize", descriptor = "()V")
	@Throws(Throwable::class)
	fun finalize() {
		this.deallocate()
		// super.finalize() is Object.finalize(), which does nothing and is not callable from Kotlin
	}

	@OriginalMember(owner = "client!vi", name = "b", descriptor = "(B)V")
	private fun initialize() {
		if (this.activeHandle >= 0) {
			return
		}
		if (this.nativeToolkit!!.GLARBVertexBufferObject) {
			OpenGL.glGenBuffersARB(1, OpenGlArbBuffer.HANDLE_BUFFER, 0)
			this.activeHandle = OpenGlArbBuffer.HANDLE_BUFFER[0]
			OpenGL.glBindBufferARB(this.bufferTarget, this.activeHandle)
		} else {
			this.activeHandle = 0
		}
	}

	companion object {
		@OriginalMember(owner = "client!ufa", name = "e", descriptor = "[I")
		@JvmField
		val HANDLE_BUFFER: IntArray = IntArray(1)
	}
}
