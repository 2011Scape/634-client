package com.jagex.game.runetek5.config.midi

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ra")
class Envelope {

	@OriginalMember(owner = "client!ra", name = "b", descriptor = "I")
	@JvmField
	var form: Int = 0

	@OriginalMember(owner = "client!ra", name = "e", descriptor = "I")
	@JvmField
	var end: Int = 0

	@OriginalMember(owner = "client!ra", name = "f", descriptor = "I")
	@JvmField
	var start: Int = 0

	@OriginalMember(owner = "client!ra", name = "g", descriptor = "I")
	private var step: Int = 0

	@OriginalMember(owner = "client!ra", name = "h", descriptor = "I")
	private var ticks: Int = 0

	@OriginalMember(owner = "client!ra", name = "i", descriptor = "I")
	private var critical: Int = 0

	@OriginalMember(owner = "client!ra", name = "j", descriptor = "I")
	private var phaseIndex: Int = 0

	@OriginalMember(owner = "client!ra", name = "k", descriptor = "I")
	private var amplitude: Int = 0

	@OriginalMember(owner = "client!ra", name = "c", descriptor = "I")
	private var numPhases: Int = 2

	@OriginalMember(owner = "client!ra", name = "d", descriptor = "[I")
	private var phaseDuration: IntArray = IntArray(2)

	@OriginalMember(owner = "client!ra", name = "a", descriptor = "[I")
	private var phasePeak: IntArray = IntArray(2)

	@OriginalMember(owner = "client!ra", name = "<init>", descriptor = "()V")
	constructor() {
		this.phaseDuration[0] = 0
		this.phaseDuration[1] = 65535
		this.phasePeak[0] = 0
		this.phasePeak[1] = 65535
	}

	@OriginalMember(owner = "client!ra", name = "a", descriptor = "(I)I")
	fun step(@OriginalArg(0) period: Int): Int {
		if (this.ticks >= this.critical) {
			this.amplitude = this.phasePeak[this.phaseIndex++] shl 15
			if (this.phaseIndex >= this.numPhases) {
				this.phaseIndex = this.numPhases - 1
			}
			this.critical = (this.phaseDuration[this.phaseIndex].toDouble() / 65536.0 * period.toDouble()).toInt()
			if (this.critical > this.ticks) {
				this.step = ((this.phasePeak[this.phaseIndex] shl 15) - this.amplitude) / (this.critical - this.ticks)
			}
		}
		this.amplitude += this.step
		this.ticks++
		return (this.amplitude - this.step) shr 15
	}

	@OriginalMember(owner = "client!ra", name = "a", descriptor = "(Lclient!cea;)V")
	fun decode(@OriginalArg(0) packet: Packet?) {
		this.form = packet!!.readUByte()
		this.start = packet.readInt()
		this.end = packet.readInt()
		this.decodeShape(packet)
	}

	@OriginalMember(owner = "client!ra", name = "a", descriptor = "()V")
	fun reset() {
		this.critical = 0
		this.phaseIndex = 0
		this.step = 0
		this.amplitude = 0
		this.ticks = 0
	}

	@OriginalMember(owner = "client!ra", name = "b", descriptor = "(Lclient!cea;)V")
	fun decodeShape(@OriginalArg(0) packet: Packet?) {
		this.numPhases = packet!!.readUByte()
		this.phaseDuration = IntArray(this.numPhases)
		this.phasePeak = IntArray(this.numPhases)
		@Pc(16) var i: Int = 0
		while (i < this.numPhases) {
			this.phaseDuration[i] = packet.readUShort()
			this.phasePeak[i] = packet.readUShort()
			i++
		}
	}
}
