package com.jagex.game.runetek5.config.midi

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket

@OriginalClass("client!ck")
class SoundEffect {

	@OriginalMember(owner = "client!ck", name = "c", descriptor = "[Lclient!laa;")
	private val instruments: Array<Instrument?> = arrayOfNulls<Instrument>(10)

	@OriginalMember(owner = "client!ck", name = "a", descriptor = "I")
	private var loopStart: Int = 0

	@OriginalMember(owner = "client!ck", name = "b", descriptor = "I")
	private var loopEnd: Int = 0

	@OriginalMember(owner = "client!ck", name = "<init>", descriptor = "(Lclient!cea;)V")
	constructor(@OriginalArg(0) packet: Packet?) {
		for (@Pc(7) i in 0 until 10) {
			@Pc(12) val used: Int = packet!!.readUByte()
			if (used != 0) {
				packet.currentPosition--
				this.instruments[i] = Instrument()
				this.instruments[i]!!.decode(packet)
			}
		}
		this.loopStart = packet!!.readUShort()
		this.loopEnd = packet.readUShort()
	}

	@OriginalMember(owner = "client!ck", name = "<init>", descriptor = "()V")
	private constructor() {
	}

	@OriginalMember(owner = "client!ck", name = "a", descriptor = "()Lclient!ob;")
	fun getMixedAudio(): VariableRateSoundPacket {
		@Pc(2) val mixed: ByteArray = this.mix()
		return VariableRateSoundPacket(22050, mixed, this.loopStart * 22050 / 1000, this.loopEnd * 22050 / 1000)
	}

	@OriginalMember(owner = "client!ck", name = "b", descriptor = "()I")
	fun getDelay(): Int {
		@Pc(1) var offset: Int = 9999999
		for (@Pc(3) i in 0 until 10) {
			if (this.instruments[i] != null && this.instruments[i]!!.loopBegin / 20 < offset) {
				offset = this.instruments[i]!!.loopBegin / 20
			}
		}
		if (this.loopStart < this.loopEnd && this.loopStart / 20 < offset) {
			offset = this.loopStart / 20
		}
		if (offset == 9999999 || offset == 0) {
			return 0
		}
		for (@Pc(55) j in 0 until 10) {
			if (this.instruments[j] != null) {
				this.instruments[j]!!.loopBegin -= offset * 20
			}
		}
		if (this.loopStart < this.loopEnd) {
			this.loopStart -= offset * 20
			this.loopEnd -= offset * 20
		}
		return offset
	}

	@OriginalMember(owner = "client!ck", name = "c", descriptor = "()[B")
	private fun mix(): ByteArray {
		@Pc(1) var maxDuration: Int = 0
		for (@Pc(3) i in 0 until 10) {
			if (this.instruments[i] != null && this.instruments[i]!!.duration + this.instruments[i]!!.loopBegin > maxDuration) {
				maxDuration = this.instruments[i]!!.duration + this.instruments[i]!!.loopBegin
			}
		}
		if (maxDuration == 0) {
			return ByteArray(0)
		}
		@Pc(49) val sampleCount: Int = maxDuration * 22050 / 1000
		@Pc(52) val mixed: ByteArray = ByteArray(sampleCount)
		for (@Pc(54) k in 0 until 10) {
			if (this.instruments[k] != null) {
				@Pc(70) val instrumentSamples: Int = this.instruments[k]!!.duration * 22050 / 1000
				@Pc(80) val instrumentOffset: Int = this.instruments[k]!!.loopBegin * 22050 / 1000
				@Pc(92) val samples: IntArray = this.instruments[k]!!.synthesize(instrumentSamples, this.instruments[k]!!.duration)
				for (@Pc(94) j in 0 until instrumentSamples) {
					@Pc(107) var out: Int = mixed[j + instrumentOffset].toInt() + (samples[j] shr 8)
					if (((out + 128) and 0xFFFFFF00.toInt()) != 0) {
						out = (out shr 31) xor 0x7F
					}
					mixed[j + instrumentOffset] = out.toByte()
				}
			}
		}
		return mixed
	}
}
