package com.jagex.game.runetek5.config.midi

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.core.util.Arrays
import java.util.Random
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!laa")
class Instrument {

	@OriginalMember(owner = "client!laa", name = "b", descriptor = "Lclient!ra;")
	private var volumeEnvelope: Envelope? = null

	@OriginalMember(owner = "client!laa", name = "g", descriptor = "Lclient!ra;")
	private var volumeMultiplier: Envelope? = null

	@OriginalMember(owner = "client!laa", name = "h", descriptor = "Lclient!ra;")
	private var release: Envelope? = null

	@OriginalMember(owner = "client!laa", name = "i", descriptor = "Lclient!ra;")
	private var pitchModifier: Envelope? = null

	@OriginalMember(owner = "client!laa", name = "k", descriptor = "Lclient!ra;")
	private var filterEnvelope: Envelope? = null

	@OriginalMember(owner = "client!laa", name = "l", descriptor = "Lclient!ra;")
	private var pitch: Envelope? = null

	@OriginalMember(owner = "client!laa", name = "m", descriptor = "Lclient!eha;")
	private var filter: Filter? = null

	@OriginalMember(owner = "client!laa", name = "n", descriptor = "Lclient!ra;")
	private var attack: Envelope? = null

	@OriginalMember(owner = "client!laa", name = "q", descriptor = "Lclient!ra;")
	private var volumeAmplitude: Envelope? = null

	@OriginalMember(owner = "client!laa", name = "s", descriptor = "Lclient!ra;")
	private var pitchModifierAmplitude: Envelope? = null

	@OriginalMember(owner = "client!laa", name = "a", descriptor = "I")
	@JvmField
	var loopBegin: Int = 0

	@OriginalMember(owner = "client!laa", name = "d", descriptor = "[I")
	private val oscillatorVolume: IntArray = IntArray(5)

	@OriginalMember(owner = "client!laa", name = "e", descriptor = "[I")
	private val oscillatorPitch: IntArray = IntArray(5)

	@OriginalMember(owner = "client!laa", name = "p", descriptor = "I")
	private var delayDecay: Int = 100

	@OriginalMember(owner = "client!laa", name = "o", descriptor = "I")
	@JvmField
	var duration: Int = 500

	@OriginalMember(owner = "client!laa", name = "v", descriptor = "[I")
	private val oscillatorDelays: IntArray = IntArray(5)

	@OriginalMember(owner = "client!laa", name = "u", descriptor = "I")
	private var delayTime: Int = 0

	@OriginalMember(owner = "client!laa", name = "a", descriptor = "(III)I")
	private fun evaluateWave(@OriginalArg(0) tphase: Int, @OriginalArg(1) amplitude: Int, @OriginalArg(2) table: Int): Int {
		if (table == 1) {
			return if ((tphase and 0x7FFF) < 16384) amplitude else -amplitude
		} else if (table == 2) {
			return (SINE[tphase and 0x7FFF] * amplitude) shr 14
		} else if (table == 3) {
			return (((tphase and 0x7FFF) * amplitude) shr 14) - amplitude
		} else if (table == 4) {
			return NOISE[(tphase / 2607) and 0x7FFF] * amplitude
		} else {
			return 0
		}
	}

	@OriginalMember(owner = "client!laa", name = "a", descriptor = "(Lclient!cea;)V")
	fun decode(@OriginalArg(0) packet: Packet?) {
		this.pitch = Envelope()
		this.pitch!!.decode(packet)
		this.volumeEnvelope = Envelope()
		this.volumeEnvelope!!.decode(packet)
		@Pc(21) var used: Int = packet!!.readUByte()
		if (used != 0) {
			packet.currentPosition--
			this.pitchModifier = Envelope()
			this.pitchModifier!!.decode(packet)
			this.pitchModifierAmplitude = Envelope()
			this.pitchModifierAmplitude!!.decode(packet)
		}
		used = packet.readUByte()
		if (used != 0) {
			packet.currentPosition--
			this.volumeMultiplier = Envelope()
			this.volumeMultiplier!!.decode(packet)
			this.volumeAmplitude = Envelope()
			this.volumeAmplitude!!.decode(packet)
		}
		used = packet.readUByte()
		if (used != 0) {
			packet.currentPosition--
			this.release = Envelope()
			this.release!!.decode(packet)
			this.attack = Envelope()
			this.attack!!.decode(packet)
		}
		for (@Pc(109) i in 0 until 10) {
			@Pc(114) val volume: Int = packet.readUSmart()
			if (volume == 0) {
				break
			}
			this.oscillatorVolume[i] = volume
			this.oscillatorPitch[i] = packet.readSmart()
			this.oscillatorDelays[i] = packet.readUSmart()
		}
		this.delayTime = packet.readUSmart()
		this.delayDecay = packet.readUSmart()
		this.duration = packet.readUShort()
		this.loopBegin = packet.readUShort()
		this.filter = Filter()
		this.filterEnvelope = Envelope()
		this.filter!!.decode(packet, this.filterEnvelope)
	}

	@OriginalMember(owner = "client!laa", name = "a", descriptor = "(II)[I")
	fun synthesize(@OriginalArg(0) mixDuration: Int, @OriginalArg(1) instrDuration: Int): IntArray {
		Arrays.clear(Instrument.OUTPUT, 0, mixDuration)
		if (instrDuration < 10) {
			return Instrument.OUTPUT
		}
		@Pc(16) val samplesPerMs: Double = mixDuration.toDouble() / (instrDuration.toDouble() + 0.0)
		this.pitch!!.reset()
		this.volumeEnvelope!!.reset()
		@Pc(24) var pitchModStep: Int = 0
		@Pc(26) var pitchModBaseStep: Int = 0
		@Pc(28) var tphase: Int = 0
		if (this.pitchModifier != null) {
			this.pitchModifier!!.reset()
			this.pitchModifierAmplitude!!.reset()
			pitchModStep = ((this.pitchModifier!!.end - this.pitchModifier!!.start).toDouble() * 32.768 / samplesPerMs).toInt()
			pitchModBaseStep = (this.pitchModifier!!.start.toDouble() * 32.768 / samplesPerMs).toInt()
		}
		@Pc(63) var volumeModStep: Int = 0
		@Pc(65) var volumeModBaseStep: Int = 0
		@Pc(67) var volumeModPhase: Int = 0
		if (this.volumeMultiplier != null) {
			this.volumeMultiplier!!.reset()
			this.volumeAmplitude!!.reset()
			volumeModStep = ((this.volumeMultiplier!!.end - this.volumeMultiplier!!.start).toDouble() * 32.768 / samplesPerMs).toInt()
			volumeModBaseStep = (this.volumeMultiplier!!.start.toDouble() * 32.768 / samplesPerMs).toInt()
		}
		for (@Pc(102) i in 0 until 5) {
			if (this.oscillatorVolume[i] != 0) {
				Instrument.PHASES[i] = 0
				Instrument.DELAYS[i] = (this.oscillatorDelays[i].toDouble() * samplesPerMs).toInt()
				Instrument.VOLUME_STEP[i] = (this.oscillatorVolume[i] shl 14) / 100
				Instrument.PITCH_STEP[i] = ((this.pitch!!.end - this.pitch!!.start).toDouble() * 32.768 * java.lang.Math.pow(1.0057929410678534, this.oscillatorPitch[i].toDouble()) / samplesPerMs).toInt()
				Instrument.PITCH_BASE_STEP[i] = (this.pitch!!.start.toDouble() * 32.768 / samplesPerMs).toInt()
			}
		}
		@Pc(182) val local182: Int
		@Pc(187) val local187: Int
		@Pc(195) val local195: Int
		@Pc(200) val local200: Int
		for (@Pc(176) j in 0 until mixDuration) {
			var frequency: Int = this.pitch!!.step(mixDuration)
			var amplitude: Int = this.volumeEnvelope!!.step(mixDuration)
			if (this.pitchModifier != null) {
				val pitchModValue: Int = this.pitchModifier!!.step(mixDuration)
				val pitchModAmplitude: Int = this.pitchModifierAmplitude!!.step(mixDuration)
				frequency += this.evaluateWave(tphase, pitchModAmplitude, this.pitchModifier!!.form) shr 1
				tphase += ((pitchModValue * pitchModStep) shr 16) + pitchModBaseStep
			}
			if (this.volumeMultiplier != null) {
				val volumeModValue: Int = this.volumeMultiplier!!.step(mixDuration)
				val volumeModAmplitude: Int = this.volumeAmplitude!!.step(mixDuration)
				amplitude = (amplitude * ((this.evaluateWave(volumeModPhase, volumeModAmplitude, this.volumeMultiplier!!.form) shr 1) + 32768)) shr 15
				volumeModPhase += ((volumeModValue * volumeModStep) shr 16) + volumeModBaseStep
			}
			for (oscillator in 0 until 5) {
				if (this.oscillatorVolume[oscillator] != 0) {
					val delayedIndex: Int = j + Instrument.DELAYS[oscillator]
					if (delayedIndex < mixDuration) {
						Instrument.OUTPUT[delayedIndex] += this.evaluateWave(Instrument.PHASES[oscillator], (amplitude * Instrument.VOLUME_STEP[oscillator]) shr 15, this.pitch!!.form)
						Instrument.PHASES[oscillator] += ((frequency * Instrument.PITCH_STEP[oscillator]) shr 16) + Instrument.PITCH_BASE_STEP[oscillator]
					}
				}
			}
		}
		@Pc(347) val local347: Int
		@Pc(352) val local352: Int
		if (this.release != null) {
			this.release!!.reset()
			this.attack!!.reset()
			var gateTimer: Int = 0
			@Pc(339) var muted: Boolean = true
			for (sampleIndex in 0 until mixDuration) {
				val releaseValue: Int = this.release!!.step(mixDuration)
				val attackValue: Int = this.attack!!.step(mixDuration)
				val gateThreshold: Int
				if (muted) {
					gateThreshold = this.release!!.start + (((this.release!!.end - this.release!!.start) * releaseValue) shr 8)
				} else {
					gateThreshold = this.release!!.start + (((this.release!!.end - this.release!!.start) * attackValue) shr 8)
				}
				gateTimer += 256
				if (gateTimer >= gateThreshold) {
					gateTimer = 0
					muted = !muted
				}
				if (muted) {
					Instrument.OUTPUT[sampleIndex] = 0
				}
			}
		}
		if (this.delayTime > 0 && this.delayDecay > 0) {
			val delaySamples: Int = (this.delayTime.toDouble() * samplesPerMs).toInt()
			for (delayIndex in delaySamples until mixDuration) {
				Instrument.OUTPUT[delayIndex] += Instrument.OUTPUT[delayIndex - delaySamples] * this.delayDecay / 100
			}
		}
		if (this.filter!!.numPairs[0] > 0 || this.filter!!.numPairs[1] > 0) {
			this.filterEnvelope!!.reset()
			var filterValue: Int = this.filterEnvelope!!.step(mixDuration + 1)
			var forwardOrder: Int = this.filter!!.compute(0, filterValue.toFloat() / 65536.0f)
			var backwardOrder: Int = this.filter!!.compute(1, filterValue.toFloat() / 65536.0f)
			if (mixDuration >= forwardOrder + backwardOrder) {
				var outputIndex: Int = 0
				var blockEnd: Int = backwardOrder
				if (backwardOrder > mixDuration - forwardOrder) {
					blockEnd = mixDuration - forwardOrder
				}
				@Pc(519) var ffIndex: Int
				@Pc(549) var fbIndex: Int
				while (outputIndex < blockEnd) {
					var filteredSample: Int = ((Instrument.OUTPUT[outputIndex + forwardOrder].toLong() * Filter.invUnityInt.toLong()) shr 16).toInt()
					ffIndex = 0
					while (ffIndex < forwardOrder) {
						filteredSample += ((Instrument.OUTPUT[outputIndex + forwardOrder - ffIndex - 1].toLong() * Filter.COEFFICIENT_INT[0][ffIndex].toLong()) shr 16).toInt()
						ffIndex++
					}
					fbIndex = 0
					while (fbIndex < outputIndex) {
						filteredSample -= ((Instrument.OUTPUT[outputIndex - fbIndex - 1].toLong() * Filter.COEFFICIENT_INT[1][fbIndex].toLong()) shr 16).toInt()
						fbIndex++
					}
					Instrument.OUTPUT[outputIndex] = filteredSample
					filterValue = this.filterEnvelope!!.step(mixDuration + 1)
					outputIndex++
				}
				blockEnd = 128
				while (true) {
					if (blockEnd > mixDuration - forwardOrder) {
						blockEnd = mixDuration - forwardOrder
					}
					while (outputIndex < blockEnd) {
						var filteredSample: Int = ((Instrument.OUTPUT[outputIndex + forwardOrder].toLong() * Filter.invUnityInt.toLong()) shr 16).toInt()
						ffIndex = 0
						while (ffIndex < forwardOrder) {
							filteredSample += ((Instrument.OUTPUT[outputIndex + forwardOrder - ffIndex - 1].toLong() * Filter.COEFFICIENT_INT[0][ffIndex].toLong()) shr 16).toInt()
							ffIndex++
						}
						fbIndex = 0
						while (fbIndex < backwardOrder) {
							filteredSample -= ((Instrument.OUTPUT[outputIndex - fbIndex - 1].toLong() * Filter.COEFFICIENT_INT[1][fbIndex].toLong()) shr 16).toInt()
							fbIndex++
						}
						Instrument.OUTPUT[outputIndex] = filteredSample
						filterValue = this.filterEnvelope!!.step(mixDuration + 1)
						outputIndex++
					}
					if (outputIndex >= mixDuration - forwardOrder) {
						while (outputIndex < mixDuration) {
							var filteredSample: Int = 0
							ffIndex = outputIndex + forwardOrder - mixDuration
							while (ffIndex < forwardOrder) {
								filteredSample += ((Instrument.OUTPUT[outputIndex + forwardOrder - ffIndex - 1].toLong() * Filter.COEFFICIENT_INT[0][ffIndex].toLong()) shr 16).toInt()
								ffIndex++
							}
							fbIndex = 0
							while (fbIndex < backwardOrder) {
								filteredSample -= ((Instrument.OUTPUT[outputIndex - fbIndex - 1].toLong() * Filter.COEFFICIENT_INT[1][fbIndex].toLong()) shr 16).toInt()
								fbIndex++
							}
							Instrument.OUTPUT[outputIndex] = filteredSample
							this.filterEnvelope!!.step(mixDuration + 1)
							outputIndex++
						}
						break
					}
					forwardOrder = this.filter!!.compute(0, filterValue.toFloat() / 65536.0f)
					backwardOrder = this.filter!!.compute(1, filterValue.toFloat() / 65536.0f)
					blockEnd += 128
				}
			}
		}
		for (sampleIndex in 0 until mixDuration) {
			if (Instrument.OUTPUT[sampleIndex] < -32768) {
				Instrument.OUTPUT[sampleIndex] = -32768
			}
			if (Instrument.OUTPUT[sampleIndex] > 32767) {
				Instrument.OUTPUT[sampleIndex] = 32767
			}
		}
		return Instrument.OUTPUT
	}

	companion object {

		@OriginalMember(owner = "client!laa", name = "j", descriptor = "[I")
		private val NOISE: IntArray = IntArray(32768)

		@OriginalMember(owner = "client!laa", name = "f", descriptor = "[I")
		private val SINE: IntArray

		init {
			@Pc(7) val random: Random = Random(0L)
			for (@Pc(9) i in 0 until 32768) {
				NOISE[i] = (random.nextInt() and 0x2) - 1
			}
			SINE = IntArray(32768)
			for (@Pc(28) i in 0 until 32768) {
				SINE[i] = (java.lang.Math.sin(i.toDouble() / 5215.1903) * 16384.0).toInt()
			}
		}

		@OriginalMember(owner = "client!laa", name = "c", descriptor = "[I")
		@JvmField
		val OUTPUT: IntArray = IntArray(220500)

		@OriginalMember(owner = "client!laa", name = "r", descriptor = "[I")
		@JvmField
		val DELAYS: IntArray = IntArray(5)

		@OriginalMember(owner = "client!laa", name = "t", descriptor = "[I")
		@JvmField
		val PITCH_BASE_STEP: IntArray = IntArray(5)

		@OriginalMember(owner = "client!laa", name = "w", descriptor = "[I")
		@JvmField
		val PITCH_STEP: IntArray = IntArray(5)

		@OriginalMember(owner = "client!laa", name = "x", descriptor = "[I")
		@JvmField
		val VOLUME_STEP: IntArray = IntArray(5)

		@OriginalMember(owner = "client!laa", name = "y", descriptor = "[I")
		@JvmField
		val PHASES: IntArray = IntArray(5)
	}
}
