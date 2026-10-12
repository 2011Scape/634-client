package com.jagex.game.runetek5.network

import com.jagex.game.runetek5.core.io.connection.DummyOutputStream
import com.jagex.game.runetek5.core.util.Arrays
import java.io.IOException
import java.io.OutputStream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!rha")
class AsyncOutputStream : Runnable {

	@OriginalMember(owner = "client!rha", name = "a", descriptor = "Ljava/io/IOException;")
	private var error: IOException? = null

	@OriginalMember(owner = "client!rha", name = "b", descriptor = "I")
	private var bufferPointer: Int = 0

	@OriginalMember(owner = "client!rha", name = "j", descriptor = "I")
	private var writePointer: Int = 0

	@OriginalMember(owner = "client!rha", name = "i", descriptor = "Ljava/io/OutputStream;")
	private var outputstream: OutputStream?

	@OriginalMember(owner = "client!rha", name = "l", descriptor = "I")
	private val bufferSize: Int

	@OriginalMember(owner = "client!rha", name = "c", descriptor = "[B")
	private val buffer: ByteArray

	@OriginalMember(owner = "client!rha", name = "e", descriptor = "Ljava/lang/Thread;")
	private val thread: Thread

	@OriginalMember(owner = "client!rha", name = "<init>", descriptor = "(Ljava/io/OutputStream;I)V")
	constructor(@OriginalArg(0) outputStream: OutputStream?, @OriginalArg(1) capacity: Int) {
		this.outputstream = outputStream
		this.bufferSize = capacity + 1
		this.buffer = ByteArray(this.bufferSize)
		this.thread = Thread(this)
		this.thread.setDaemon(true)
		this.thread.start()
	}

	@OriginalMember(owner = "client!rha", name = "run", descriptor = "()V")
	override fun run() {
		while (true) {
			@Pc(35) var length: Int = 0
			synchronized(this) {
				while (true) {
					if (this.error != null) {
						return
					}
					if (this.writePointer < this.bufferPointer) {
						length = this.bufferSize + this.writePointer - this.bufferPointer
					} else {
						length = this.writePointer - this.bufferPointer
					}
					if (length > 0) {
						break
					}
					try {
						(this as java.lang.Object).wait()
					} catch (@Pc(52) ex: InterruptedException) {
					}
				}
			}
			try {
				if (this.bufferSize >= this.bufferPointer + length) {
					this.outputstream!!.write(this.buffer, this.bufferPointer, length)
				} else {
					@Pc(85) val tailLength: Int = this.bufferSize - this.bufferPointer
					this.outputstream!!.write(this.buffer, this.bufferPointer, tailLength)
					this.outputstream!!.write(this.buffer, 0, length - tailLength)
				}
			} catch (@Pc(105) ex2: IOException) {
				@Pc(105) val local105x: IOException = ex2
				synchronized(this) {
					this.error = local105x
					return
				}
			}
			synchronized(this) {
				this.bufferPointer = (this.bufferPointer + length) % this.bufferSize
			}
		}
	}

	@OriginalMember(owner = "client!rha", name = "b", descriptor = "(B)V")
	fun stop() {
		synchronized(this) {
			if (this.error == null) {
				this.error = IOException("")
			}
			(this as java.lang.Object).notifyAll()
		}
		try {
			this.thread.join()
		} catch (@Pc(35) ignored: InterruptedException) {
		}
	}

	@OriginalMember(owner = "client!rha", name = "a", descriptor = "(I)V")
	fun reset() {
		this.outputstream = DummyOutputStream()
	}

	@OriginalMember(owner = "client!rha", name = "a", descriptor = "(BII[B)V")
	@Throws(IOException::class)
	fun write(@OriginalArg(1) length: Int, @OriginalArg(3) buffer: ByteArray?) {
		if (length < 0 || buffer!!.size < length) {
			throw IOException()
		}
		synchronized(this) {
			if (this.error != null) {
				throw IOException(this.error!!.toString())
			}
			@Pc(53) val freeSpace: Int
			if (this.bufferPointer <= this.writePointer) {
				freeSpace = this.bufferPointer + this.bufferSize - this.writePointer - 1
			} else {
				freeSpace = this.bufferPointer - this.writePointer - 1
			}
			if (freeSpace < length) {
				throw IOException("")
			}
			if (length + this.writePointer > this.bufferSize) {
				@Pc(91) val tailLength: Int = this.bufferSize - this.writePointer
				Arrays.copy(buffer, 0, this.buffer, this.writePointer, tailLength)
				Arrays.copy(buffer, tailLength, this.buffer, 0, length - tailLength)
			} else {
				Arrays.copy(buffer, 0, this.buffer, this.writePointer, length)
			}
			this.writePointer = (length + this.writePointer) % this.bufferSize
			(this as java.lang.Object).notifyAll()
		}
	}
}
