package com.jagex.game.runetek5.cache.node.impl

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.sound.Resampler
import kotlin.jvm.JvmField

@OriginalClass("client!ob")
class VariableRateSoundPacket : AudioNode {

	@OriginalMember(owner = "client!ob", name = "s", descriptor = "Z")
	@JvmField
	var pingPong: Boolean = false

	@OriginalMember(owner = "client!ob", name = "t", descriptor = "I")
	@JvmField
	var sampleRate: Int = 0

	@OriginalMember(owner = "client!ob", name = "p", descriptor = "[B")
	@JvmField
	var data: ByteArray? = null

	@OriginalMember(owner = "client!ob", name = "r", descriptor = "I")
	@JvmField
	var nominalBitRate: Int = 0

	@OriginalMember(owner = "client!ob", name = "q", descriptor = "I")
	@JvmField
	var minBitRate: Int = 0

	@OriginalMember(owner = "client!ob", name = "<init>", descriptor = "(I[BII)V")
	constructor(@OriginalArg(0) sampleRate: Int, @OriginalArg(1) data: ByteArray?, @OriginalArg(2) nominalBitRate: Int, @OriginalArg(3) minBitRate: Int) {
		this.sampleRate = sampleRate
		this.data = data
		this.nominalBitRate = nominalBitRate
		this.minBitRate = minBitRate
	}

	@OriginalMember(owner = "client!ob", name = "<init>", descriptor = "(I[BIIZ)V")
	constructor(@OriginalArg(0) sampleRate: Int, @OriginalArg(1) data: ByteArray?, @OriginalArg(2) nominalBitRate: Int, @OriginalArg(3) minBitRate: Int, @OriginalArg(4) pingPong: Boolean) {
		this.sampleRate = sampleRate
		this.data = data
		this.nominalBitRate = nominalBitRate
		this.minBitRate = minBitRate
		this.pingPong = pingPong
	}

	@OriginalMember(owner = "client!ob", name = "a", descriptor = "(Lclient!pe;)Lclient!ob;")
	fun resample(@OriginalArg(0) sampleRateConverter: Resampler?): VariableRateSoundPacket {
		this.data = sampleRateConverter!!.resample(this.data)
		this.sampleRate = sampleRateConverter.convertSampleRate(this.sampleRate)
		if (this.nominalBitRate == this.minBitRate) {
			this.minBitRate = sampleRateConverter.convertBitRate(this.nominalBitRate)
			this.nominalBitRate = this.minBitRate
		} else {
			this.nominalBitRate = sampleRateConverter.convertBitRate(this.nominalBitRate)
			this.minBitRate = sampleRateConverter.convertBitRate(this.minBitRate)
			if (this.nominalBitRate == this.minBitRate) {
				this.nominalBitRate--
			}
		}
		return this
	}
}
