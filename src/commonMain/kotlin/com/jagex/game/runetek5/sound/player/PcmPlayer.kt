package com.jagex.game.runetek5.sound.player

import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.sound.jagtheora.PcmStream
import com.jagex.game.runetek5.cache.node.impl.AudioNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import lang.Synchronized

@OriginalClass("client!cba")
open class PcmPlayer {

	@OriginalMember(owner = "client!cba", name = "j", descriptor = "[I")
	@JvmField
	var bitpackedSoundData: IntArray? = null

	@OriginalMember(owner = "client!cba", name = "k", descriptor = "Lclient!jba;")
	private var currentPcmStream: PcmStream? = null

	@OriginalMember(owner = "client!cba", name = "t", descriptor = "I")
	@JvmField
	var bufferCapacity: Int = 0

	@OriginalMember(owner = "client!cba", name = "w", descriptor = "I")
	private var currentOffset: Int = 0

	@OriginalMember(owner = "client!cba", name = "A", descriptor = "I")
	@JvmField
	var sampleRate2: Int = 0

	@OriginalMember(owner = "client!cba", name = "h", descriptor = "I")
	private val maxActiveVoices: Int = 32

	@OriginalMember(owner = "client!cba", name = "q", descriptor = "Z")
	private var shutdown: Boolean = false

	@OriginalMember(owner = "client!cba", name = "i", descriptor = "J")
	private var timeLastCycle: Long = Utils.currentTimeMillis()

	@OriginalMember(owner = "client!cba", name = "u", descriptor = "J")
	private var nextBufferSwapTime: Long = 0L

	@OriginalMember(owner = "client!cba", name = "y", descriptor = "J")
	private var nextUpdate: Long = 0L

	@OriginalMember(owner = "client!cba", name = "v", descriptor = "I")
	private var streamUpdateCountdown: Int = 0

	@OriginalMember(owner = "client!cba", name = "x", descriptor = "I")
	private var lastDataConsumed: Int = 0

	@OriginalMember(owner = "client!cba", name = "B", descriptor = "[Lclient!jba;")
	private val inactiveStreams: Array<PcmStream?> = arrayOfNulls<PcmStream>(8)

	@OriginalMember(owner = "client!cba", name = "D", descriptor = "Z")
	private var enabled: Boolean = true

	@OriginalMember(owner = "client!cba", name = "C", descriptor = "I")
	private var dataLeftLastCycle: Int = 0

	@OriginalMember(owner = "client!cba", name = "z", descriptor = "[Lclient!jba;")
	private val activeStreams: Array<PcmStream?> = arrayOfNulls<PcmStream>(8)

	@OriginalMember(owner = "client!cba", name = "E", descriptor = "I")
	private var dataConsumed: Int = 0

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "()I")
	@Throws(Exception::class)
	open fun dataLeft(): Int {
		return this.bufferCapacity
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(I)V")
	@Throws(Exception::class)
	open fun play(@OriginalArg(0) bufferSize: Int) {
	}

	@OriginalMember(owner = "client!cba", name = "b", descriptor = "()V")
	@Throws(Exception::class)
	open fun open() {
	}

	@OriginalMember(owner = "client!cba", name = "b", descriptor = "(I)V")
	@Synchronized
	fun cycle() {
		if (this.shutdown) {
			return
		}
		@Pc(12) var currentTime: Long = Utils.currentTimeMillis()
		try {
			if (this.timeLastCycle + 6000L < currentTime) {
				this.timeLastCycle = currentTime - 6000L
			}
			while (this.timeLastCycle + 5000L < currentTime) {
				this.skipAudioSamples()
				this.timeLastCycle += (256000 / PcmPlayerManager.sampleRate).toLong()
				currentTime = Utils.currentTimeMillis()
			}
		} catch (@Pc(61) ex: Exception) {
			this.timeLastCycle = currentTime
		}
		if (this.bitpackedSoundData == null) {
			return
		}
		try {
			if (this.nextBufferSwapTime != 0L) {
				if (currentTime < this.nextBufferSwapTime) {
					return
				}
				this.play(this.bufferCapacity)
				this.enabled = true
				this.nextBufferSwapTime = 0L
			}
			@Pc(98) var dataLeft: Int = this.dataLeft()
			if (this.dataLeftLastCycle - dataLeft > this.dataConsumed) {
				this.dataConsumed = this.dataLeftLastCycle - dataLeft
			}
			@Pc(120) var targetOffset: Int = this.sampleRate2 + this.currentOffset
			if (targetOffset + 256 > 16384) {
				targetOffset = 16128
			}
			if (targetOffset + 256 > this.bufferCapacity) {
				this.bufferCapacity += 1024
				if (this.bufferCapacity > 16384) {
					this.bufferCapacity = 16384
				}
				this.close()
				this.play(this.bufferCapacity)
				dataLeft = 0
				this.enabled = true
				if (targetOffset + 256 > this.bufferCapacity) {
					targetOffset = this.bufferCapacity - 256
					this.currentOffset = targetOffset - this.sampleRate2
				}
			}
			while (targetOffset > dataLeft) {
				this.fill(this.bitpackedSoundData)
				this.writeToBuffer()
				dataLeft += 256
			}
			if (currentTime > this.nextUpdate) {
				if (this.enabled) {
					this.enabled = false
				} else if (this.dataConsumed == 0 && this.lastDataConsumed == 0) {
					this.close()
					this.nextBufferSwapTime = currentTime + 2000L
					return
				} else {
					this.currentOffset = kotlin.math.min(this.lastDataConsumed, this.dataConsumed)
					this.lastDataConsumed = this.dataConsumed
				}
				this.nextUpdate = currentTime + 2000L
				this.dataConsumed = 0
			}
			this.dataLeftLastCycle = dataLeft
		} catch (@Pc(248) ex2: Exception) {
			this.close()
			this.nextBufferSwapTime = currentTime + 2000L
		}
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(ILclient!jba;I)V")
	private fun enqueueStreamByPriority(@OriginalArg(1) audioBuss: PcmStream?, @OriginalArg(2) streamPriority: Int) {
		@Pc(7) val bucketIndex: Int = streamPriority shr 5
		@Pc(19) val audioBuss2: PcmStream? = this.inactiveStreams[bucketIndex]
		if (audioBuss2 == null) {
			this.activeStreams[bucketIndex] = audioBuss
		} else {
			audioBuss2.nextStream = audioBuss
		}
		this.inactiveStreams[bucketIndex] = audioBuss
		audioBuss!!.streamPriority = streamPriority
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "([II)V")
	private fun fill(@OriginalArg(0) samples: IntArray?) {
		@Pc(1) var count: Short = 256
		if (PcmPlayerManager.stereo) {
			count = 512
		}
		Arrays.clear(samples, 0, count.toInt())
		this.streamUpdateCountdown -= 256
		if (this.currentPcmStream != null && this.streamUpdateCountdown <= 0) {
			this.streamUpdateCountdown += PcmPlayerManager.sampleRate shr 4
			PcmStream.resetMixState(this.currentPcmStream)
			this.enqueueStreamByPriority(this.currentPcmStream, this.currentPcmStream!!.getPriority())
			@Pc(45) var totalCost: Int = 0
			@Pc(47) var pending: Int = 255
			@Pc(49) var pass: Int = 7
			@Pc(56) var bucket: Int
			label103@ while (pending != 0) {
				@Pc(61) var round: Int
				if (pass < 0) {
					bucket = pass and 0x3
					round = -(pass shr 2)
				} else {
					bucket = pass
					round = 0
				}
				@Pc(72) var bits: Int = (pending ushr bucket) and 0x11111111
				while (bits != 0) {
					if ((bits and 0x1) != 0) {
						pending = pending and (0x1 shl bucket).inv()
						@Pc(88) var prev: PcmStream? = null
						@Pc(93) var buss: PcmStream? = this.activeStreams[bucket]
						label97@ while (true) {
							while (true) {
								if (buss == null) {
									break@label97
								}
								@Pc(97) val packet: AudioNode? = buss.variableRateSoundPacket
								if (packet == null || packet.activeVoiceCount <= round) {
									buss.fill = true
									@Pc(121) val cost: Int = buss.getActiveVoiceCount()
									totalCost += cost
									if (packet != null) {
										packet.activeVoiceCount += cost
									}
									if (totalCost >= this.maxActiveVoices) {
										break@label103
									}
									@Pc(140) var subBuss: PcmStream? = buss.getFirstStream()
									if (subBuss != null) {
										@Pc(145) val parentPriority: Int = buss.streamPriority
										while (subBuss != null) {
											this.enqueueStreamByPriority(subBuss, (parentPriority * subBuss.getPriority()) shr 8)
											subBuss = buss.getNextStream()
										}
									}
									@Pc(164) val next: PcmStream? = buss.nextStream
									buss.nextStream = null
									if (prev == null) {
										this.activeStreams[bucket] = next
									} else {
										prev.nextStream = next
									}
									if (next == null) {
										this.inactiveStreams[bucket] = prev
									}
									buss = next
								} else {
									pending = pending or (0x1 shl bucket)
									prev = buss
									buss = buss.nextStream
								}
							}
						}
					}
					bucket += 4
					round++
					bits = bits ushr 0x4
				}
				pass--
			}
			bucket = 0
			while (bucket < 8) {
				@Pc(207) var head: PcmStream? = this.activeStreams[bucket]
				this.inactiveStreams[bucket] = null
				this.activeStreams[bucket] = null
				while (head != null) {
					@Pc(221) val audioBuss: PcmStream? = head.nextStream
					head.nextStream = null
					head = audioBuss
				}
				bucket++
			}
		}
		if (this.streamUpdateCountdown < 0) {
			this.streamUpdateCountdown = 0
		}
		if (this.currentPcmStream != null) {
			this.currentPcmStream!!.fill(samples, 0, 256)
		}
		this.timeLastCycle = Utils.currentTimeMillis()
	}

	@OriginalMember(owner = "client!cba", name = "c", descriptor = "(I)V")
	@Synchronized
	fun start() {
		this.enabled = true
		try {
			this.open()
		} catch (@Pc(16) ex: Exception) {
			this.close()
			this.nextBufferSwapTime = Utils.currentTimeMillis() + 2000L
		}
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(ZLclient!jba;)V")
	@Synchronized
	fun setCurrentPcmStream(@OriginalArg(1) audioBuss: PcmStream?) {
		this.currentPcmStream = audioBuss
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(IB)V")
	private fun skipAudioSamples() {
		this.streamUpdateCountdown -= 256
		if (this.streamUpdateCountdown < 0) {
			this.streamUpdateCountdown = 0
		}
		if (this.currentPcmStream != null) {
			this.currentPcmStream!!.skip(256)
		}
	}

	@OriginalMember(owner = "client!cba", name = "c", descriptor = "()V")
	open fun close() {
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(Z)V")
	@Synchronized
	fun shutdown() {
		if (PcmPlayerManager.runnable != null) {
			@Pc(9) var readyToExit: Boolean = true
			@Pc(11) var i: Int = 0
			while (i < 2) {
				if (PcmPlayerManager.runnable!!.players[i] === this) {
					PcmPlayerManager.runnable!!.players[i] = null
				}
				if (PcmPlayerManager.runnable!!.players[i] != null) {
					readyToExit = false
				}
				i++
			}
			if (readyToExit) {
				PcmPlayerManager.runnable!!.stopping = true
				while (PcmPlayerManager.runnable!!.running) {
					TimeUtils.sleep(50L)
				}
				PcmPlayerManager.runnable = null
			}
		}
		this.close()
		this.bitpackedSoundData = null
		this.shutdown = true
	}

	@OriginalMember(owner = "client!cba", name = "d", descriptor = "()V")
	@Throws(Exception::class)
	open fun writeToBuffer() {
	}

	@OriginalMember(owner = "client!cba", name = "a", descriptor = "(Ljava/awt/Component;)V")
	@Throws(Exception::class)
	open fun init(@OriginalArg(0) component: awt.Component?) {
	}
}
