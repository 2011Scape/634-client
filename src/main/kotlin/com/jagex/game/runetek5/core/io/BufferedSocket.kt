package com.jagex.game.runetek5.core.io

import com.jagex.game.runetek5.ExplicitRuntimeException
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.sign.SignLink
import com.jagex.game.runetek5.sign.SignedResource
import com.jagex.game.runetek5.sign.SignedResourceStatus
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!re")
class BufferedSocket : Runnable {

	@OriginalMember(owner = "client!re", name = "m", descriptor = "Lclient!of;")
	private var threadReference: SignedResource? = null

	@OriginalMember(owner = "client!re", name = "r", descriptor = "[B")
	private var writeBuffer: ByteArray? = null

	@OriginalMember(owner = "client!re", name = "c", descriptor = "I")
	private var bufPos: Int = 0

	@OriginalMember(owner = "client!re", name = "i", descriptor = "Z")
	private var closed: Boolean = false

	@OriginalMember(owner = "client!re", name = "k", descriptor = "I")
	private var writePos: Int = 0

	@OriginalMember(owner = "client!re", name = "n", descriptor = "Z")
	private var error: Boolean = false

	@OriginalMember(owner = "client!re", name = "w", descriptor = "Ljava/net/Socket;")
	private val socket: Socket?

	@OriginalMember(owner = "client!re", name = "d", descriptor = "Lclient!wea;")
	private val signlink: SignLink?

	@OriginalMember(owner = "client!re", name = "g", descriptor = "Ljava/io/InputStream;")
	private var inputStream: InputStream? = null

	@OriginalMember(owner = "client!re", name = "l", descriptor = "Ljava/io/OutputStream;")
	private var outputStream: OutputStream? = null

	@OriginalMember(owner = "client!re", name = "x", descriptor = "I")
	private val bufSize: Int

	@OriginalMember(owner = "client!re", name = "<init>", descriptor = "(Ljava/net/Socket;Lclient!wea;I)V")
	@Throws(IOException::class)
	constructor(@OriginalArg(0) socket2: Socket?, @OriginalArg(1) signLink: SignLink?, @OriginalArg(2) bufSize: Int) {
		this.socket = socket2
		this.signlink = signLink
		this.socket!!.soTimeout = 30000
		this.socket.tcpNoDelay = true
		this.inputStream = this.socket.getInputStream()
		this.outputStream = this.socket.getOutputStream()
		this.bufSize = bufSize
	}

	@OriginalMember(owner = "client!re", name = "a", descriptor = "(B)I")
	@Throws(IOException::class)
	fun available(): Int {
		return if (this.closed) 0 else this.inputStream!!.available()
	}

	@OriginalMember(owner = "client!re", name = "a", descriptor = "(I)V")
	fun breakConnection() {
		if (!this.closed) {
			this.inputStream = DummyInputStream()
			this.outputStream = DummyOutputStream()
		}
	}

	@OriginalMember(owner = "client!re", name = "a", descriptor = "([BIII)V")
	@Throws(IOException::class)
	fun write(@OriginalArg(0) buffer: ByteArray?, @OriginalArg(1) length: Int) {
		if (this.closed) {
			return
		}
		if (this.error) {
			this.error = false
			throw IOException()
		}
		if (this.writeBuffer == null) {
			this.writeBuffer = ByteArray(this.bufSize)
		}
		synchronized(this) {
			@Pc(39) var i: Int = 0
			while (i < length) {
				this.writeBuffer!![this.bufPos] = buffer!![i]
				this.bufPos = (this.bufPos + 1) % this.bufSize
				if ((this.writePos + this.bufSize - 100) % this.bufSize == this.bufPos) {
					throw IOException()
				}
				i++
			}
			if (this.threadReference == null) {
				this.threadReference = this.signlink!!.startThread(this, 3)
			}
			(this as java.lang.Object).notifyAll()
		}
	}

	@OriginalMember(owner = "client!re", name = "a", descriptor = "(Z)V")
	@Throws(IOException::class)
	fun sanityCheck() {
		if (!this.closed && this.error) {
			this.error = false
			throw IOException()
		}
	}

	@OriginalMember(owner = "client!re", name = "c", descriptor = "(I)I")
	@Throws(IOException::class)
	fun read(): Int {
		return if (this.closed) 0 else this.inputStream!!.read()
	}

	@OriginalMember(owner = "client!re", name = "run", descriptor = "()V")
	override fun run() {
		try {
			while (true) {
				// label80 in the Java: `closing` replaces the labelled break out of the synchronized block.
				@Pc(35) var len: Int = 0
				@Pc(24) var off: Int = 0
				var closing: Boolean = false
				synchronized(this) {
					if (this.bufPos == this.writePos) {
						if (this.closed) {
							closing = true
						} else {
							try {
								(this as java.lang.Object).wait()
							} catch (@Pc(20) ex: InterruptedException) {
							}
						}
					}
					if (!closing) {
						off = this.writePos
						if (this.writePos > this.bufPos) {
							len = this.bufSize - this.writePos
						} else {
							len = this.bufPos - this.writePos
						}
					}
				}
				if (!closing) {
					if (len <= 0) {
						continue
					}
					try {
						this.outputStream!!.write(this.writeBuffer, off, len)
					} catch (@Pc(61) ex2: IOException) {
						this.error = true
					}
					this.writePos = (len + this.writePos) % this.bufSize
					try {
						if (this.bufPos == this.writePos) {
							this.outputStream!!.flush()
						}
					} catch (@Pc(83) ex3: IOException) {
						this.error = true
					}
					continue
				}
				try {
					if (this.inputStream != null) {
						this.inputStream!!.close()
					}
					if (this.outputStream != null) {
						this.outputStream!!.close()
					}
					if (this.socket != null) {
						this.socket.close()
					}
				} catch (@Pc(107) ex4: IOException) {
				}
				this.writeBuffer = null
				break
			}
		} catch (@Pc(112) ex5: Exception) {
			ExplicitRuntimeException.reportError(null, ex5)
		}
	}

	@OriginalMember(owner = "client!re", name = "a", descriptor = "([BIBI)V")
	@Throws(IOException::class)
	fun readFully(@OriginalArg(0) buffer: ByteArray?, @OriginalArg(1) offset: Int, @OriginalArg(3) length: Int) {
		var offset: Int = offset
		var length: Int = length
		if (this.closed) {
			return
		}
		while (length > 0) {
			@Pc(14) val bytesRead: Int = this.inputStream!!.read(buffer, offset, length)
			if (bytesRead <= 0) {
				throw EOFException()
			}
			length -= bytesRead
			offset += bytesRead
		}
	}

	@OriginalMember(owner = "client!re", name = "finalize", descriptor = "()V")
	fun finalize() {
		this.close()
	}

	@OriginalMember(owner = "client!re", name = "c", descriptor = "(B)V")
	fun close() {
		if (this.closed) {
			return
		}
		synchronized(this) {
			this.closed = true
			(this as java.lang.Object).notifyAll()
		}
		if (this.threadReference != null) {
			while (this.threadReference!!.status == SignedResourceStatus.IDLE) {
				TimeUtils.sleep(1L)
			}
			if (this.threadReference!!.status == SignedResourceStatus.SUCCESS) {
				try {
					(this.threadReference!!.result as Thread?)!!.join()
				} catch (@Pc(54) ignored: InterruptedException) {
				}
			}
		}
		this.threadReference = null
	}
}
