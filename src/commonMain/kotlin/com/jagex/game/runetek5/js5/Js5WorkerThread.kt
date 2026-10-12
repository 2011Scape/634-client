package com.jagex.game.runetek5.js5

import com.jagex.game.runetek5.core.constants.CompressionType
import com.jagex.game.runetek5.cache.node.Queue
import com.jagex.game.runetek5.core.io.BufferedSocket
import com.jagex.game.runetek5.buffer.Packet
import io.IOException
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.js5.request.remote.Js5NetQueueRequest
import com.jagex.game.runetek5.utils.Utils
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmField

@OriginalClass("client!tha")
class Js5WorkerThread {

	@OriginalMember(owner = "client!tha", name = "w", descriptor = "J")
	private var lastAttempt: Long = 0L

	@OriginalMember(owner = "client!tha", name = "y", descriptor = "Lclient!re;")
	private var socket: BufferedSocket? = null

	@OriginalMember(owner = "client!tha", name = "z", descriptor = "I")
	private var latency: Int = 0

	@OriginalMember(owner = "client!tha", name = "E", descriptor = "Lclient!mba;")
	private var current: Js5NetQueueRequest? = null

	@OriginalMember(owner = "client!tha", name = "m", descriptor = "Lclient!mn;")
	private val pendingUrgentRequests: Queue = Queue()

	@OriginalMember(owner = "client!tha", name = "t", descriptor = "Lclient!mn;")
	private val inFlightUrgentRequests: Queue = Queue()

	@OriginalMember(owner = "client!tha", name = "u", descriptor = "Lclient!mn;")
	private val prefetchRequests: Queue = Queue()

	@OriginalMember(owner = "client!tha", name = "v", descriptor = "Lclient!mn;")
	private val pendingPrefetchRequests: Queue = Queue()

	@OriginalMember(owner = "client!tha", name = "x", descriptor = "Lclient!cea;")
	private val write: Packet = Packet(4)

	@OriginalMember(owner = "client!tha", name = "B", descriptor = "B")
	private var xorCode: Byte = 0

	@OriginalMember(owner = "client!tha", name = "C", descriptor = "I")
	@Volatile
	@JvmField
	var errors: Int = 0

	@OriginalMember(owner = "client!tha", name = "D", descriptor = "I")
	@Volatile
	@JvmField
	var response: Int = 0

	@OriginalMember(owner = "client!tha", name = "A", descriptor = "Lclient!cea;")
	private val read: Packet = Packet(8)

	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(IZ)V")
	fun writeLoggedIn(@OriginalArg(1) loggedIn: Boolean) {
		if (this.socket == null) {
			return
		}
		try {
			this.write.currentPosition = 0
			this.write.writeByte(if (loggedIn) 2 else 3)
			this.write.writeMedium(0)
			this.socket!!.write(this.write.buffer, 4)
		} catch (@Pc(40) ex: IOException) {
			try {
				this.socket!!.close()
			} catch (@Pc(46) ex2: Exception) {
			}
			this.errors++
			this.socket = null
			this.response = -2
		}
	}

	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(B)Z")
	fun loop(): Boolean {
		@Pc(14) var local14: Int
		if (this.socket != null) {
			@Pc(7) val currentTime: Long = Utils.currentTimeMillis()
			var elapsed: Int = (currentTime - this.lastAttempt).toInt()
			if (elapsed > 200) {
				elapsed = 200
			}
			this.lastAttempt = currentTime
			this.latency += elapsed
			if (this.latency > 30000) {
				try {
					this.socket!!.close()
				} catch (@Pc(40) ex: Exception) {
				}
				this.socket = null
			}
		}
		if (this.socket == null) {
			return this.getUrgentRequestCount() == 0 && this.getPrefetchRequestCount() == 0
		}
		try {
			this.socket!!.sanityCheck()
			@Pc(78) var js5WorkerRequestMessage: Js5NetQueueRequest? = this.pendingUrgentRequests.first() as Js5NetQueueRequest?
			while (js5WorkerRequestMessage != null) {
				this.write.currentPosition = 0
				this.write.writeByte(Js5RequestCode.URGENT)
				this.write.writeMedium(js5WorkerRequestMessage.indexReference.toInt())
				this.socket!!.write(this.write.buffer, 4)
				this.inFlightUrgentRequests.insert(js5WorkerRequestMessage)
				js5WorkerRequestMessage = this.pendingUrgentRequests.next() as Js5NetQueueRequest?
			}
			@Pc(124) var js5WorkerRequestMessage2: Js5NetQueueRequest? = this.prefetchRequests.first() as Js5NetQueueRequest?
			while (js5WorkerRequestMessage2 != null) {
				this.write.currentPosition = 0
				this.write.writeByte(Js5RequestCode.PREFETCH)
				this.write.writeMedium(js5WorkerRequestMessage2.indexReference.toInt())
				this.socket!!.write(this.write.buffer, 4)
				this.pendingPrefetchRequests.insert(js5WorkerRequestMessage2)
				js5WorkerRequestMessage2 = this.prefetchRequests.next() as Js5NetQueueRequest?
			}
			var readIteration: Int = 0
			while (readIteration < 100) {
				@Pc(176) val available: Int = this.socket!!.available()
				if (available < 0) {
					throw IOException()
				}
				if (available == 0) {
					break
				}
				this.latency = 0
				@Pc(192) var expectedHeaderSize: Byte = 0
				if (this.current == null) {
					expectedHeaderSize = 8
				} else if (this.current!!.blockBytesRead == 0) {
					expectedHeaderSize = 1
				}
				@Pc(224) var local224: Int
				@Pc(231) var local231: Int
				@Pc(274) var local274: Int
				if (expectedHeaderSize <= 0) {
					val streamLength: Int = this.current!!.stream!!.buffer!!.size - this.current!!.padding
					var chunkSize: Int = 512 - this.current!!.blockBytesRead
					if (streamLength - this.current!!.stream!!.currentPosition < chunkSize) {
						chunkSize = streamLength - this.current!!.stream!!.currentPosition
					}
					if (available < chunkSize) {
						chunkSize = available
					}
					this.socket!!.readFully(this.current!!.stream!!.buffer, this.current!!.stream!!.currentPosition, chunkSize)
					if (this.xorCode.toInt() != 0) {
						var byteIndex: Int = 0
						while (byteIndex < chunkSize) {
							this.current!!.stream!!.buffer!![byteIndex + this.current!!.stream!!.currentPosition] = (this.current!!.stream!!.buffer!![byteIndex + this.current!!.stream!!.currentPosition].toInt() xor this.xorCode.toInt()).toByte()
							byteIndex++
						}
					}
					this.current!!.blockBytesRead += chunkSize
					this.current!!.stream!!.currentPosition += chunkSize
					if (this.current!!.stream!!.currentPosition == streamLength) {
						this.current!!.unlinkSecondary()
						this.current!!.waiting = false
						this.current = null
					} else if (this.current!!.blockBytesRead == 512) {
						this.current!!.blockBytesRead = 0
					}
				} else {
					var headerBytes: Int = expectedHeaderSize - this.read.currentPosition
					if (available < headerBytes) {
						headerBytes = available
					}
					this.socket!!.readFully(this.read.buffer, this.read.currentPosition, headerBytes)
					if (this.xorCode.toInt() != 0) {
						var byteIndex: Int = 0
						while (byteIndex < headerBytes) {
							this.read.buffer!![byteIndex + this.read.currentPosition] = (this.read.buffer!![byteIndex + this.read.currentPosition].toInt() xor this.xorCode.toInt()).toByte()
							byteIndex++
						}
					}
					this.read.currentPosition += headerBytes
					if (this.read.currentPosition >= expectedHeaderSize) {
						if (this.current == null) {
							this.read.currentPosition = 0
							val archiveId: Int = this.read.readUByte()
							val groupId: Int = this.read.readUShort()
							@Pc(452) val settings: Int = this.read.readUByte()
							@Pc(457) val payloadSize: Int = this.read.readInt()
							@Pc(461) val compression: Int = settings and 0x7F
							@Pc(472) val prefetch: Boolean = (settings and 0x80) != 0
							@Pc(479) val indexReference: Long = ((archiveId shl 16) + groupId).toLong()
							@Pc(489) var js5WorkerRequestMessage3: Js5NetQueueRequest?
							if (prefetch) {
								js5WorkerRequestMessage3 = this.pendingPrefetchRequests.first() as Js5NetQueueRequest?
								while (js5WorkerRequestMessage3 != null && indexReference != js5WorkerRequestMessage3.indexReference) {
									js5WorkerRequestMessage3 = this.pendingPrefetchRequests.next() as Js5NetQueueRequest?
								}
							} else {
								js5WorkerRequestMessage3 = this.inFlightUrgentRequests.first() as Js5NetQueueRequest?
								while (js5WorkerRequestMessage3 != null && js5WorkerRequestMessage3.indexReference != indexReference) {
									js5WorkerRequestMessage3 = this.inFlightUrgentRequests.next() as Js5NetQueueRequest?
								}
							}
							if (js5WorkerRequestMessage3 == null) {
								throw IOException()
							}
							@Pc(549) val headerSize: Int = if (compression == CompressionType.NONE) 5 else 9
							this.current = js5WorkerRequestMessage3
							this.current!!.stream = Packet(this.current!!.padding + headerSize + payloadSize)
							this.current!!.stream!!.writeByte(compression)
							this.current!!.stream!!.writeInt(payloadSize)
							this.read.currentPosition = 0
							this.current!!.blockBytesRead = 8
						} else if (this.current!!.blockBytesRead != 0) {
							throw IOException()
						} else if (this.read.buffer!![0].toInt() == -1) {
							this.current!!.blockBytesRead = 1
							this.read.currentPosition = 0
						} else {
							this.current = null
						}
					}
				}
				readIteration++
			}
			return true
		} catch (@Pc(629) ex2: IOException) {
			try {
				this.socket!!.close()
			} catch (@Pc(635) ex3: Exception) {
			}
			this.response = -2
			this.errors++
			this.socket = null
			return this.getUrgentRequestCount() == 0 && this.getPrefetchRequestCount() == 0
		}
	}

	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(I)I")
	private fun getPrefetchRequestCount(): Int {
		return this.prefetchRequests.size() + this.pendingPrefetchRequests.size()
	}

	@OriginalMember(owner = "client!tha", name = "b", descriptor = "(I)V")
	private fun writeInitiating() {
		if (this.socket == null) {
			return
		}
		try {
			this.write.currentPosition = 0
			this.write.writeByte(6)
			this.write.writeMedium(3)
			this.socket!!.write(this.write.buffer, 4)
		} catch (@Pc(36) ex: IOException) {
			try {
				this.socket!!.close()
			} catch (@Pc(42) ignored: Exception) {
			}
			this.response = -2
			this.socket = null
			this.errors++
		}
	}

	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(Z)V")
	fun breakConnection() {
		if (this.socket != null) {
			this.socket!!.breakConnection()
		}
	}

	@OriginalMember(owner = "client!tha", name = "c", descriptor = "(I)Z")
	fun isPrefetchRequestQueueFull(): Boolean {
		return this.getPrefetchRequestCount() >= 20
	}

	@OriginalMember(owner = "client!tha", name = "d", descriptor = "(I)Z")
	fun isUrgentRequestQueueFull(): Boolean {
		return this.getUrgentRequestCount() >= 20
	}

	@OriginalMember(owner = "client!tha", name = "e", descriptor = "(I)V")
	fun quit() {
		if (this.socket != null) {
			this.socket!!.close()
		}
	}

	@OriginalMember(owner = "client!tha", name = "f", descriptor = "(I)I")
	fun getUrgentRequestCount(): Int {
		return this.pendingUrgentRequests.size() + this.inFlightUrgentRequests.size()
	}

	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(ZZLclient!re;)V")
	fun init(@OriginalArg(1) loggedIn: Boolean, @OriginalArg(2) bufferedSocket: BufferedSocket?) {
		if (this.socket != null) {
			try {
				this.socket!!.close()
			} catch (@Pc(10) ex: Exception) {
			}
			this.socket = null
		}
		this.socket = bufferedSocket
		this.writeInitiating()
		this.writeLoggedIn(loggedIn)
		this.read.currentPosition = 0
		this.current = null
		while (true) {
			@Pc(46) var js5WorkerRequestMessage: Js5NetQueueRequest? = this.inFlightUrgentRequests.removeFirst() as Js5NetQueueRequest?
			if (js5WorkerRequestMessage == null) {
				while (true) {
					js5WorkerRequestMessage = this.pendingPrefetchRequests.removeFirst() as Js5NetQueueRequest?
					if (js5WorkerRequestMessage == null) {
						if (this.xorCode.toInt() != 0) {
							try {
								this.write.currentPosition = 0
								this.write.writeByte(Js5RequestCode.ENCRYPTION)
								this.write.writeByte(this.xorCode.toInt())
								this.write.writeShort(0)
								this.socket!!.write(this.write.buffer, 4)
							} catch (@Pc(108) ex2: IOException) {
								try {
									this.socket!!.close()
								} catch (@Pc(114) ex3: Exception) {
								}
								this.response = -2
								this.errors++
								this.socket = null
							}
						}
						this.latency = 0
						this.lastAttempt = Utils.currentTimeMillis()
						return
					}
					this.prefetchRequests.insert(js5WorkerRequestMessage)
				}
			}
			this.pendingUrgentRequests.insert(js5WorkerRequestMessage)
		}
	}

	@OriginalMember(owner = "client!tha", name = "g", descriptor = "(I)V")
	fun rekey() {
		try {
			this.socket!!.close()
		} catch (@Pc(5) ex: Exception) {
		}
		this.socket = null
		this.errors++
		this.xorCode = (util.random() * 255.0 + 1.0).toInt().toByte()
		this.response = -1
	}

	@OriginalMember(owner = "client!tha", name = "h", descriptor = "(I)V")
	fun writeTerminate() {
		if (this.socket == null) {
			return
		}
		try {
			this.write.currentPosition = 0
			this.write.writeByte(7)
			this.write.writeMedium(0)
			this.socket!!.write(this.write.buffer, 4)
		} catch (@Pc(34) ex: IOException) {
			try {
				this.socket!!.close()
			} catch (@Pc(40) ex2: Exception) {
			}
			this.response = -2
			this.errors++
			this.socket = null
		}
	}

	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(BBZII)Lclient!mba;")
	fun request(@OriginalArg(1) padding: Byte, @OriginalArg(2) highPriority: Boolean, @OriginalArg(3) indexId: Int, @OriginalArg(4) archiveId: Int): Js5NetQueueRequest {
		@Pc(19) val indexReference: Long = ((archiveId shl 16) + indexId).toLong()
		@Pc(23) val js5WorkerRequestMessage: Js5NetQueueRequest = Js5NetQueueRequest()
		js5WorkerRequestMessage.highPriority = highPriority
		js5WorkerRequestMessage.indexReference = indexReference
		js5WorkerRequestMessage.padding = padding
		if (highPriority) {
			if (this.getUrgentRequestCount() >= 20) {
				throw RuntimeException()
			}
			this.pendingUrgentRequests.insert(js5WorkerRequestMessage)
		} else if (this.getPrefetchRequestCount() < 20) {
			this.prefetchRequests.insert(js5WorkerRequestMessage)
		} else {
			throw RuntimeException()
		}
		return js5WorkerRequestMessage
	}
}
