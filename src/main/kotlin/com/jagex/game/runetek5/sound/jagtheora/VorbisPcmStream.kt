package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.sound.jagtheora.PcmStream
import com.jagex.game.runetek5.cache.node.lru.MonoStereoData
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.sound.player.PcmPlayerManager
import com.jagex.game.runetek5.cache.node.CacheableNodeQueue

@OriginalClass("client!iha")
class VorbisPcmStream : PcmStream {

	@OriginalMember(owner = "client!iha", name = "Q", descriptor = "I")
	private var sampleOffset: Int = 0

	@OriginalMember(owner = "client!iha", name = "R", descriptor = "Z")
	private var isStopping: Boolean = false

	@OriginalMember(owner = "client!iha", name = "T", descriptor = "Z")
	private var isMuted: Boolean = false

	@OriginalMember(owner = "client!iha", name = "t", descriptor = "Lclient!uh;")
	private val nodeCollection: Deque = Deque()

	@OriginalMember(owner = "client!iha", name = "S", descriptor = "I")
	private var rightVolume: Int = 256

	@OriginalMember(owner = "client!iha", name = "U", descriptor = "I")
	private var leftVolume: Int = 256

	@OriginalMember(owner = "client!iha", name = "V", descriptor = "I")
	private var capacityUsed: Int = 0

	@OriginalMember(owner = "client!iha", name = "M", descriptor = "I")
	private val channelCount: Int

	@OriginalMember(owner = "client!iha", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) channelCount: Int) : super() {
		this.channelCount = channelCount
	}

	@OriginalMember(owner = "client!iha", name = "c", descriptor = "(II)V")
	fun setVolume(@OriginalArg(1) volume: Int) {
		this.rightVolume = volume
		this.leftVolume = volume
	}

	@OriginalMember(owner = "client!iha", name = "b", descriptor = "(I)Lclient!lm;")
	@Synchronized
	private fun getFirst(): MonoStereoData? {
		return this.nodeCollection.first() as MonoStereoData?
	}

	@OriginalMember(owner = "client!iha", name = "b", descriptor = "()Lclient!jba;")
	override fun getNextStream(): PcmStream? {
		return null
	}

	@OriginalMember(owner = "client!iha", name = "d", descriptor = "(I)I")
	@Synchronized
	fun size(): Int {
		return this.capacityUsed
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "(B)V")
	@Synchronized
	private fun removeFirst() {
		@Pc(15) val block: MonoStereoData? = this.getFirst()
		if (block != null) {
			block.unlink()
			this.capacityUsed--
			this.sampleOffset = 0
			VorbisPcmStream.aClass379_3.put(block.getCacheKey(), block)
		}
	}

	@OriginalMember(owner = "client!iha", name = "b", descriptor = "([III)V")
	@Synchronized
	override fun fill(@OriginalArg(0) mix: IntArray?, @OriginalArg(1) offset: Int, @OriginalArg(2) length: Int) {
		var offset: Int = offset
		if (this.isMuted) {
			return
		}
		if (this.getFirst() != null) {
			@Pc(28) var end: Int = offset + length
			if (PcmPlayerManager.stereo) {
				end = end shl 0x1
			}
			@Pc(38) var right: Byte = 0
			if (this.channelCount == 2) {
				right = 1
			}
			while (offset < end) {
				@Pc(52) val block: MonoStereoData? = this.getFirst()
				if (block == null) {
					return
				}
				@Pc(58) val samples: Array<ShortArray?>? = block.samples
				while (offset < end && this.sampleOffset < samples!![0]!!.size) {
					if (PcmPlayerManager.stereo) {
						mix!![offset++] = this.rightVolume * samples[0]!![this.sampleOffset].toInt()
						mix[offset++] = samples[right.toInt()]!![this.sampleOffset].toInt() * this.leftVolume
					} else {
						@Pc(92) val slot: Int = offset++
						mix!![slot] += samples[right.toInt()]!![this.sampleOffset].toInt() * this.leftVolume + this.rightVolume * samples[0]!![this.sampleOffset].toInt()
					}
					this.sampleOffset++
				}
				if (samples!![0]!!.size <= this.sampleOffset) {
					this.removeFirst()
				}
			}
		} else if (this.isStopping) {
			this.unlink()
			VorbisPcmStream.aClass379_3.clear()
		}
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "(IZ)V")
	@Synchronized
	fun setPaused(@OriginalArg(1) isMuted: Boolean) {
		this.isMuted = isMuted
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "(Lclient!lm;I)V")
	@Synchronized
	fun enqueueSampleData(@OriginalArg(0) node: MonoStereoData?) {
		while (this.capacityUsed >= 100) {
			this.nodeCollection.removeFirst()
			this.capacityUsed--
		}
		this.nodeCollection.addLast(node)
		this.capacityUsed++
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "()Lclient!jba;")
	override fun getFirstStream(): PcmStream? {
		return null
	}

	@OriginalMember(owner = "client!iha", name = "f", descriptor = "(I)V")
	@Synchronized
	fun finish() {
		this.isStopping = true
	}

	@OriginalMember(owner = "client!iha", name = "g", descriptor = "(I)D")
	@Synchronized
	fun getPlaybackTime(): Double {
		if (this.capacityUsed < 1) {
			return -1.0
		} else {
			@Pc(21) val sampleData: MonoStereoData? = this.nodeCollection.first() as MonoStereoData?
			return if (sampleData == null) -1.0 else (-(sampleData.samples!![0]!!.size.toFloat() / PcmPlayerManager.sampleRate.toFloat())).toDouble() + sampleData.timestamp
		}
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "(IDI)Lclient!lm;")
	fun allocateSampleData(@OriginalArg(0) sampleCount: Int, @OriginalArg(1) timestamp: Double): MonoStereoData {
		@Pc(11) val cacheKey: Long = (sampleCount or (this.channelCount shl 0)).toLong()
		@Pc(17) var sampleData: MonoStereoData? = VorbisPcmStream.aClass379_3.get(cacheKey) as MonoStereoData?
		if (sampleData == null) {
			sampleData = MonoStereoData(Array<ShortArray?>(this.channelCount) { ShortArray(sampleCount) }, timestamp)
		} else {
			sampleData.timestamp = timestamp
			VorbisPcmStream.aClass379_3.remove(cacheKey)
		}
		return sampleData
	}

	@OriginalMember(owner = "client!iha", name = "a", descriptor = "(I)V")
	@Synchronized
	override fun skip(@OriginalArg(0) length: Int) {
		var length: Int = length
		if (this.isMuted) {
			return
		}
		while (true) {
			@Pc(11) val block: MonoStereoData? = this.getFirst()
			if (block == null) {
				if (this.isStopping) {
					this.unlink()
					VorbisPcmStream.aClass379_3.clear()
				}
				return
			}
			if (length < block.samples!![0]!!.size - this.sampleOffset) {
				this.sampleOffset += length
				return
			}
			length -= block.samples!![0]!!.size - this.sampleOffset
			this.removeFirst()
		}
	}

	@OriginalMember(owner = "client!iha", name = "d", descriptor = "()I")
	override fun getActiveVoiceCount(): Int {
		return 1
	}

	companion object {
		@OriginalMember(owner = "client!kq", name = "O", descriptor = "Lclient!wu;")
		@JvmField
		val aClass379_3: CacheableNodeQueue = CacheableNodeQueue(64)
	}
}
