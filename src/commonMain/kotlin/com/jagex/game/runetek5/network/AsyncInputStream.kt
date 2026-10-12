package com.jagex.game.runetek5.network

import com.jagex.game.runetek5.DroppedInputStream
import com.jagex.game.runetek5.core.util.Arrays
import io.EOFException
import io.IOException
import io.InputStream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import lang.InterruptedException
import lang.Runnable
import lang.Thread
import lockNotifyAll
import lockWait
import withLock
import canBlock
import runLoop

@OriginalClass("client!gha")
class AsyncInputStream : Runnable {

	@OriginalMember(owner = "client!gha", name = "o", descriptor = "Ljava/io/IOException;")
	private var readError: IOException? = null

	@OriginalMember(owner = "client!gha", name = "k", descriptor = "I")
	private var readPosition: Int = 0

	@OriginalMember(owner = "client!gha", name = "m", descriptor = "I")
	private var bufferPointer: Int = 0

	@OriginalMember(owner = "client!gha", name = "a", descriptor = "I")
	private val capacity: Int

	@OriginalMember(owner = "client!gha", name = "c", descriptor = "Ljava/io/InputStream;")
	private var input: InputStream?

	@OriginalMember(owner = "client!gha", name = "i", descriptor = "[B")
	private val buffer: ByteArray

	@OriginalMember(owner = "client!gha", name = "l", descriptor = "Ljava/lang/Thread;")
	private val executingThread: Thread

	@OriginalMember(owner = "client!gha", name = "<init>", descriptor = "(Ljava/io/InputStream;I)V")
	constructor(@OriginalArg(0) inputStream: InputStream?, @OriginalArg(1) size: Int) {
		this.capacity = size + 1
		this.input = inputStream
		this.buffer = ByteArray(this.capacity)
		this.executingThread = Thread(this)
		this.executingThread.setDaemon(true)
		this.executingThread.start()
	}

	@OriginalMember(owner = "client!gha", name = "a", descriptor = "(I)V")
	fun shutdown() {
		withLock(this) {
			if (this.readError == null) {
				this.readError = IOException("")
			}
			lockNotifyAll(this)
		}
		try {
			this.executingThread.join()
		} catch (@Pc(34) ex: InterruptedException) {
		}
	}

	@OriginalMember(owner = "client!gha", name = "run", descriptor = "()V")
	override fun run() {
		// Not part of the original client: the loop goes through runLoop (PlatformLoop), the original
		// blocking loop on the JVM and an event-loop chain where there are no threads; runStep is one
		// turn of the original while, which ends its turn instead of waiting where it cannot block.
		runLoop(
			prelude = { true },
			step = { this.runStep() },
			onError = { throw it },
			onFinally = { },
		)
	}

	/* Not part of the original client: one turn of run's loop, false when the loop ends. */
	private fun runStep(): Boolean {
		@Pc(33) var length: Int = 0
		withLock(this) {
			while (true) {
				if (this.readError != null) {
					return false
				}
				if (this.readPosition == 0) {
					length = this.capacity - this.bufferPointer - 1
				} else if (this.readPosition < this.bufferPointer) {
					length = this.capacity - this.bufferPointer
				} else if (!canBlock && this.readPosition == this.bufferPointer) {
					// Not part of the original client: an empty buffer whose positions are not at 0 counts
					// as full in the original's arithmetic (length -1). A blocking reader never sees that
					// state, as it is already inside read() when the game catches up with it; a reader
					// that cannot block re-evaluates every turn and would wait for space for ever (PlatformLoop).
					length = this.capacity - this.bufferPointer
				} else {
					length = this.readPosition - this.bufferPointer - 1
				}
				if (length > 0) {
					break
				}
				if (!canBlock) {
					return true
				}
				try {
					lockWait(this)
				} catch (@Pc(63) ex: InterruptedException) {
				}
			}
		}
		@Pc(80) var bytesRead: Int = 0
		try {
			bytesRead = this.input!!.read(this.buffer, this.bufferPointer, length)
			if (bytesRead == -1) {
				throw EOFException()
			}
		} catch (@Pc(88) ex2: IOException) {
			@Pc(88) val local88x: IOException = ex2
			withLock(this) {
				this.readError = local88x
				return false
			}
		}
		withLock(this) {
			this.bufferPointer = (bytesRead + this.bufferPointer) % this.capacity
		}
		return true
	}

	@OriginalMember(owner = "client!gha", name = "a", descriptor = "(III[B)I")
	@Throws(IOException::class)
	fun read(@OriginalArg(0) length: Int, @OriginalArg(1) offset: Int, @OriginalArg(3) destination: ByteArray?): Int {
		var length: Int = length
		if (length < 0 || offset < 0 || offset + length > destination!!.size) {
			throw IOException()
		}
		withLock(this) {
			@Pc(49) val availableBytes: Int
			if (this.bufferPointer >= this.readPosition) {
				availableBytes = this.bufferPointer - this.readPosition
			} else {
				availableBytes = this.capacity + this.bufferPointer - this.readPosition
			}
			if (length > availableBytes) {
				length = availableBytes
			}
			if (length == 0 && this.readError != null) {
				throw IOException(this.readError!!.toString())
			}
			if (this.capacity < length + this.readPosition) {
				@Pc(97) val tailLength: Int = this.capacity - this.readPosition
				Arrays.copy(this.buffer, this.readPosition, destination, offset, tailLength)
				Arrays.copy(this.buffer, 0, destination, offset + tailLength, length - tailLength)
			} else {
				Arrays.copy(this.buffer, this.readPosition, destination, offset, length)
			}
			this.readPosition = (this.readPosition + length) % this.capacity
			lockNotifyAll(this)
			return length
		}
	}

	@OriginalMember(owner = "client!gha", name = "c", descriptor = "(I)V")
	fun drop() {
		this.input = DroppedInputStream()
	}

	@OriginalMember(owner = "client!gha", name = "a", descriptor = "(IZ)Z")
	@Throws(IOException::class)
	fun available(@OriginalArg(0) length: Int): Boolean {
		if (length <= 0 || length >= this.capacity) {
			throw IOException()
		}
		withLock(this) {
			@Pc(40) val availableBytes: Int
			if (this.bufferPointer < this.readPosition) {
				availableBytes = this.capacity + this.bufferPointer - this.readPosition
			} else {
				availableBytes = this.bufferPointer - this.readPosition
			}
			if (availableBytes >= length) {
				return true
			} else if (this.readError == null) {
				return false
			} else {
				throw IOException(this.readError!!.toString())
			}
		}
	}
}
