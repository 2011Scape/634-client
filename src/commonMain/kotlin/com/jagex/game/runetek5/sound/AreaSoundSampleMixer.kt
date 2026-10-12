package com.jagex.game.runetek5.sound

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket
import com.jagex.game.runetek5.sound.jagtheora.AreaSoundStream
import kotlin.jvm.JvmStatic

object AreaSoundSampleMixer {

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(I[B[IIIIIIIIIILclient!via;)I")
	@JvmStatic
	fun mixForwardStereoRamped(@OriginalArg(1) samples: ByteArray?, @OriginalArg(2) mix: IntArray?, @OriginalArg(3) position: Int, @OriginalArg(4) offset: Int, @OriginalArg(5) leftGain: Int, @OriginalArg(6) rightGain: Int, @OriginalArg(7) leftGainStep: Int, @OriginalArg(8) rightGainStep: Int, @OriginalArg(10) end: Int, @OriginalArg(11) limit: Int, @OriginalArg(12) stream: AreaSoundStream?): Int {
		var position: Int = position
		var offset: Int = offset
		var leftGain: Int = leftGain
		var rightGain: Int = rightGain
		position = position shr 0x8
		@Pc(7) val limitIndex: Int = limit shr 8
		leftGain = leftGain shl 0x2
		rightGain = rightGain shl 0x2
		@Pc(19) val scaledLeftStep: Int = leftGainStep shl 2
		@Pc(23) val scaledRightStep: Int = rightGainStep shl 2
		@Pc(30) var stop: Int
		stop = offset + limitIndex - position
		if (stop > end) {
			stop = end
		}
		stream!!.gain += stream!!.gainStep * (stop - offset)
		offset = offset shl 0x1
		stop = stop shl 0x1
		stop -= 6
		@Pc(60) var sample0: Byte
		@Pc(63) var rightSlot0: Int
		while (offset < stop) {
			@Pc(58) val next0: Int = position + 1
			sample0 = samples!![position]
			rightSlot0 = offset + 1
			mix!![offset] += sample0 * leftGain
			@Pc(74) val leftGain1: Int = leftGain + scaledLeftStep
			@Pc(77) val leftSlot1: Int = rightSlot0 + 1
			mix!![rightSlot0] += sample0 * rightGain
			@Pc(88) val rightGain1: Int = rightGain + scaledRightStep
			@Pc(91) val next1: Int = next0 + 1
			@Pc(93) val sample1: Byte = samples!![next0]
			@Pc(96) val rightSlot1: Int = leftSlot1 + 1
			mix!![leftSlot1] += sample1 * leftGain1
			@Pc(107) val leftGain2: Int = leftGain1 + scaledLeftStep
			@Pc(110) val leftSlot2: Int = rightSlot1 + 1
			mix!![rightSlot1] += sample1 * rightGain1
			@Pc(121) val rightGain2: Int = rightGain1 + scaledRightStep
			@Pc(124) val next2: Int = next1 + 1
			@Pc(126) val sample2: Byte = samples!![next1]
			@Pc(129) val rightSlot2: Int = leftSlot2 + 1
			mix!![leftSlot2] += sample2 * leftGain2
			@Pc(140) val leftGain3: Int = leftGain2 + scaledLeftStep
			@Pc(143) val leftSlot3: Int = rightSlot2 + 1
			mix!![rightSlot2] += sample2 * rightGain2
			@Pc(154) val rightGain3: Int = rightGain2 + scaledRightStep
			position = next2 + 1
			@Pc(159) val sample3: Byte = samples!![next2]
			@Pc(162) val rightSlot3: Int = leftSlot3 + 1
			mix!![leftSlot3] += sample3 * leftGain3
			leftGain = leftGain3 + scaledLeftStep
			offset = rightSlot3 + 1
			mix!![rightSlot3] += sample3 * rightGain3
			rightGain = rightGain3 + scaledRightStep
		}
		stop += 6
		while (offset < stop) {
			sample0 = samples!![position++]
			rightSlot0 = offset + 1
			mix!![offset] += sample0 * leftGain
			leftGain += scaledLeftStep
			offset = rightSlot0 + 1
			mix!![rightSlot0] += sample0 * rightGain
			rightGain += scaledRightStep
		}
		stream!!.leftGain = leftGain shr 2
		stream!!.rightGain = rightGain shr 2
		stream!!.position = position shl 8
		return offset shr 1
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(II[B[IIIIIIIILclient!via;II)I")
	@JvmStatic
	fun mixBackwardStereoResampled(@OriginalArg(2) samples: ByteArray?, @OriginalArg(3) mix: IntArray?, @OriginalArg(4) position: Int, @OriginalArg(5) offset: Int, @OriginalArg(6) leftGain: Int, @OriginalArg(7) rightGain: Int, @OriginalArg(9) end: Int, @OriginalArg(10) limit: Int, @OriginalArg(11) stream: AreaSoundStream?, @OriginalArg(12) rate: Int, @OriginalArg(13) edgeSample: Int): Int {
		var position: Int = position
		var offset: Int = offset
		@Pc(14) var stop: Int
		if (rate == 0) {
			stop = end
		} else {
			stop = offset + (limit + rate + 256 - position) / rate
			if (stop > end) {
				stop = end
			}
		}
		offset = offset shl 0x1
		stop = stop shl 0x1
		@Pc(54) var nextSlot: Int
		@Pc(51) var interpolated: Int
		@Pc(53) var slot: Int
		while (offset < stop) {
			@Pc(31) val base: Int = position shr 8
			@Pc(37) val sample: Byte = samples!![base - 1]
			interpolated = (sample.toInt() shl 8) + (samples!![base] - sample) * (position and 0xFF)
			slot = offset
			nextSlot = offset + 1
			mix!![slot] += interpolated * leftGain shr 6
			@Pc(65) val rightSlot: Int = nextSlot
			offset = nextSlot + 1
			mix!![rightSlot] += interpolated * rightGain shr 6
			position += rate
		}
		if (rate == 0) {
			stop = end
		} else {
			stop = (offset shr 1) + (limit + rate - position) / rate
			if (stop > end) {
				stop = end
			}
		}
		stop = stop shl 0x1
		while (offset < stop) {
			interpolated = (edgeSample shl 8) + (samples!![position shr 8] - edgeSample) * (position and 0xFF)
			slot = offset
			nextSlot = offset + 1
			mix!![slot] += interpolated * leftGain shr 6
			slot = nextSlot
			offset = nextSlot + 1
			mix!![slot] += interpolated * rightGain shr 6
			position += rate
		}
		stream!!.position = position
		return offset shr 1
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(I[B[IIIIIIIILclient!via;)I")
	@JvmStatic
	fun mixBackwardStereo(@OriginalArg(1) samples: ByteArray?, @OriginalArg(2) mix: IntArray?, @OriginalArg(3) position: Int, @OriginalArg(4) offset: Int, @OriginalArg(5) leftGain: Int, @OriginalArg(6) rightGain: Int, @OriginalArg(8) end: Int, @OriginalArg(9) limit: Int, @OriginalArg(10) stream: AreaSoundStream?): Int {
		var position: Int = position
		var offset: Int = offset
		position = position shr 0x8
		@Pc(7) val limitIndex: Int = limit shr 8
		@Pc(11) val scaledLeft: Int = leftGain shl 2
		@Pc(15) val scaledRight: Int = rightGain shl 2
		@Pc(24) var stop: Int
		stop = offset + position + 1 - limitIndex
		if (stop > end) {
			stop = end
		}
		offset = offset shl 0x1
		stop = stop shl 0x1
		stop -= 6
		@Pc(43) var sample0: Byte
		@Pc(46) var leftSlot0: Int
		while (offset < stop) {
			@Pc(41) val prev0: Int = position - 1
			sample0 = samples!![position]
			leftSlot0 = offset + 1
			mix!![offset] += sample0 * scaledLeft
			@Pc(56) val rightSlot0: Int = leftSlot0 + 1
			mix!![leftSlot0] += sample0 * scaledRight
			@Pc(66) val prev1: Int = prev0 - 1
			@Pc(68) val sample1: Byte = samples!![prev0]
			@Pc(71) val leftSlot1: Int = rightSlot0 + 1
			mix!![rightSlot0] += sample1 * scaledLeft
			@Pc(81) val rightSlot1: Int = leftSlot1 + 1
			mix!![leftSlot1] += sample1 * scaledRight
			@Pc(91) val prev2: Int = prev1 - 1
			@Pc(93) val sample2: Byte = samples!![prev1]
			@Pc(96) val leftSlot2: Int = rightSlot1 + 1
			mix!![rightSlot1] += sample2 * scaledLeft
			@Pc(106) val rightSlot2: Int = leftSlot2 + 1
			mix!![leftSlot2] += sample2 * scaledRight
			position = prev2 - 1
			@Pc(118) val sample3: Byte = samples!![prev2]
			@Pc(121) val leftSlot3: Int = rightSlot2 + 1
			mix!![rightSlot2] += sample3 * scaledLeft
			offset = leftSlot3 + 1
			mix!![leftSlot3] += sample3 * scaledRight
		}
		stop += 6
		while (offset < stop) {
			sample0 = samples!![position--]
			leftSlot0 = offset + 1
			mix!![offset] += sample0 * scaledLeft
			offset = leftSlot0 + 1
			mix!![leftSlot0] += sample0 * scaledRight
		}
		stream!!.position = position shl 8
		return offset shr 1
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(II[B[IIIIIIIIIILclient!via;II)I")
	@JvmStatic
	fun mixForwardStereoResampledRamped(@OriginalArg(2) samples: ByteArray?, @OriginalArg(3) mix: IntArray?, @OriginalArg(4) position: Int, @OriginalArg(5) offset: Int, @OriginalArg(6) leftGain: Int, @OriginalArg(7) rightGain: Int, @OriginalArg(8) leftGainStep: Int, @OriginalArg(9) rightGainStep: Int, @OriginalArg(11) end: Int, @OriginalArg(12) limit: Int, @OriginalArg(13) stream: AreaSoundStream?, @OriginalArg(14) rate: Int, @OriginalArg(15) edgeSample: Int): Int {
		var position: Int = position
		var offset: Int = offset
		var leftGain: Int = leftGain
		var rightGain: Int = rightGain
		stream!!.gain -= stream!!.gainStep * offset
		@Pc(23) var stop: Int
		if (rate == 0) {
			stop = end
		} else {
			stop = offset + (limit + rate - position - 257) / rate
			if (stop > end) {
				stop = end
			}
		}
		offset = offset shl 0x1
		stop = stop shl 0x1
		@Pc(44) var sample: Byte
		@Pc(63) var next: Int
		@Pc(60) var interpolated: Int
		@Pc(62) var slot: Int
		while (offset < stop) {
			@Pc(40) val base: Int = position shr 8
			sample = samples!![base]
			interpolated = (sample.toInt() shl 8) + (samples!![base + 1] - sample) * (position and 0xFF)
			slot = offset
			next = offset + 1
			mix!![slot] += interpolated * leftGain shr 6
			leftGain += leftGainStep
			@Pc(78) val rightSlot: Int = next
			offset = next + 1
			mix!![rightSlot] += interpolated * rightGain shr 6
			rightGain += rightGainStep
			position += rate
		}
		if (rate == 0) {
			stop = end
		} else {
			stop = (offset shr 1) + (limit + rate - position - 1) / rate
			if (stop > end) {
				stop = end
			}
		}
		stop = stop shl 0x1
		while (offset < stop) {
			sample = samples!![position shr 8]
			interpolated = (sample.toInt() shl 8) + (edgeSample - sample) * (position and 0xFF)
			slot = offset
			next = offset + 1
			mix!![slot] += interpolated * leftGain shr 6
			leftGain += leftGainStep
			slot = next
			offset = next + 1
			mix!![slot] += interpolated * rightGain shr 6
			rightGain += rightGainStep
			position += rate
		}
		next = offset shr 1
		stream!!.gain += stream!!.gainStep * next
		stream!!.leftGain = leftGain
		stream!!.rightGain = rightGain
		stream!!.position = position
		return next
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(I[B[IIIIIIIIIILclient!via;)I")
	@JvmStatic
	fun mixBackwardStereoRamped(@OriginalArg(1) samples: ByteArray?, @OriginalArg(2) mix: IntArray?, @OriginalArg(3) position: Int, @OriginalArg(4) offset: Int, @OriginalArg(5) leftGain: Int, @OriginalArg(6) rightGain: Int, @OriginalArg(7) leftGainStep: Int, @OriginalArg(8) rightGainStep: Int, @OriginalArg(10) end: Int, @OriginalArg(11) limit: Int, @OriginalArg(12) stream: AreaSoundStream?): Int {
		var position: Int = position
		var offset: Int = offset
		var leftGain: Int = leftGain
		var rightGain: Int = rightGain
		position = position shr 0x8
		@Pc(7) val limitIndex: Int = limit shr 8
		leftGain = leftGain shl 0x2
		rightGain = rightGain shl 0x2
		@Pc(19) val scaledLeftStep: Int = leftGainStep shl 2
		@Pc(23) val scaledRightStep: Int = rightGainStep shl 2
		@Pc(32) var stop: Int
		stop = offset + position + 1 - limitIndex
		if (stop > end) {
			stop = end
		}
		stream!!.gain += stream!!.gainStep * (stop - offset)
		offset = offset shl 0x1
		stop = stop shl 0x1
		stop -= 6
		@Pc(62) var sample0: Byte
		@Pc(65) var rightSlot0: Int
		while (offset < stop) {
			@Pc(60) val prev0: Int = position - 1
			sample0 = samples!![position]
			rightSlot0 = offset + 1
			mix!![offset] += sample0 * leftGain
			@Pc(76) val leftGain1: Int = leftGain + scaledLeftStep
			@Pc(79) val leftSlot1: Int = rightSlot0 + 1
			mix!![rightSlot0] += sample0 * rightGain
			@Pc(90) val rightGain1: Int = rightGain + scaledRightStep
			@Pc(93) val prev1: Int = prev0 - 1
			@Pc(95) val sample1: Byte = samples!![prev0]
			@Pc(98) val rightSlot1: Int = leftSlot1 + 1
			mix!![leftSlot1] += sample1 * leftGain1
			@Pc(109) val leftGain2: Int = leftGain1 + scaledLeftStep
			@Pc(112) val leftSlot2: Int = rightSlot1 + 1
			mix!![rightSlot1] += sample1 * rightGain1
			@Pc(123) val rightGain2: Int = rightGain1 + scaledRightStep
			@Pc(126) val prev2: Int = prev1 - 1
			@Pc(128) val sample2: Byte = samples!![prev1]
			@Pc(131) val rightSlot2: Int = leftSlot2 + 1
			mix!![leftSlot2] += sample2 * leftGain2
			@Pc(142) val leftGain3: Int = leftGain2 + scaledLeftStep
			@Pc(145) val leftSlot3: Int = rightSlot2 + 1
			mix!![rightSlot2] += sample2 * rightGain2
			@Pc(156) val rightGain3: Int = rightGain2 + scaledRightStep
			position = prev2 - 1
			@Pc(161) val sample3: Byte = samples!![prev2]
			@Pc(164) val rightSlot3: Int = leftSlot3 + 1
			mix!![leftSlot3] += sample3 * leftGain3
			leftGain = leftGain3 + scaledLeftStep
			offset = rightSlot3 + 1
			mix!![rightSlot3] += sample3 * rightGain3
			rightGain = rightGain3 + scaledRightStep
		}
		stop += 6
		while (offset < stop) {
			sample0 = samples!![position--]
			rightSlot0 = offset + 1
			mix!![offset] += sample0 * leftGain
			leftGain += scaledLeftStep
			offset = rightSlot0 + 1
			mix!![rightSlot0] += sample0 * rightGain
			rightGain += scaledRightStep
		}
		stream!!.leftGain = leftGain shr 2
		stream!!.rightGain = rightGain shr 2
		stream!!.position = position shl 8
		return offset shr 1
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(II[B[IIIIIIIILclient!via;II)I")
	@JvmStatic
	fun mixForwardMonoResampledRamped(@OriginalArg(2) samples: ByteArray?, @OriginalArg(3) mix: IntArray?, @OriginalArg(4) position: Int, @OriginalArg(5) offset: Int, @OriginalArg(6) gain: Int, @OriginalArg(7) gainStep: Int, @OriginalArg(9) end: Int, @OriginalArg(10) limit: Int, @OriginalArg(11) stream: AreaSoundStream?, @OriginalArg(12) rate: Int, @OriginalArg(13) edgeSample: Int): Int {
		var position: Int = position
		var offset: Int = offset
		var gain: Int = gain
		stream!!.leftGain -= stream!!.leftGainStep * offset
		stream!!.rightGain -= stream!!.rightGainStep * offset
		@Pc(32) var stop: Int
		if (rate == 0) {
			stop = end
		} else {
			stop = offset + (limit + rate - position - 257) / rate
			if (stop > end) {
				stop = end
			}
		}
		@Pc(45) var sample: Byte
		@Pc(47) var slot: Int
		while (offset < stop) {
			@Pc(41) val base: Int = position shr 8
			sample = samples!![base]
			slot = offset++
			mix!![slot] += ((sample.toInt() shl 8) + (samples!![base + 1] - sample) * (position and 0xFF)) * gain shr 6
			gain += gainStep
			position += rate
		}
		if (rate == 0) {
			stop = end
		} else {
			stop = offset + (limit + rate - position - 1) / rate
			if (stop > end) {
				stop = end
			}
		}
		while (offset < stop) {
			sample = samples!![position shr 8]
			slot = offset++
			mix!![slot] += ((sample.toInt() shl 8) + (edgeSample - sample) * (position and 0xFF)) * gain shr 6
			gain += gainStep
			position += rate
		}
		stream!!.leftGain += stream!!.leftGainStep * offset
		stream!!.rightGain += stream!!.rightGainStep * offset
		stream!!.gain = gain
		stream!!.position = position
		return offset
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "([B[IIIIIIILclient!via;)I")
	@JvmStatic
	fun mixForwardMono(@OriginalArg(0) samples: ByteArray?, @OriginalArg(1) mix: IntArray?, @OriginalArg(2) position: Int, @OriginalArg(3) offset: Int, @OriginalArg(4) gain: Int, @OriginalArg(6) end: Int, @OriginalArg(7) limit: Int, @OriginalArg(8) stream: AreaSoundStream?): Int {
		var position: Int = position
		var offset: Int = offset
		position = position shr 0x8
		@Pc(7) val limitIndex: Int = limit shr 8
		@Pc(11) val scaledGain: Int = gain shl 2
		@Pc(18) var stop: Int
		stop = offset + limitIndex - position
		if (stop > end) {
			stop = end
		}
		stop -= 3
		@Pc(26) var slot0: Int
		while (offset < stop) {
			slot0 = offset++
			@Pc(29) val mixed0: Int = mix!![slot0]
			@Pc(32) val next0: Int = position + 1
			mix!![slot0] = mixed0 + samples!![position] * scaledGain
			@Pc(39) val slot1: Int = offset++
			@Pc(42) val mixed1: Int = mix!![slot1]
			@Pc(45) val next1: Int = next0 + 1
			mix!![slot1] = mixed1 + samples!![next0] * scaledGain
			@Pc(52) val slot2: Int = offset++
			@Pc(55) val mixed2: Int = mix!![slot2]
			@Pc(58) val next2: Int = next1 + 1
			mix!![slot2] = mixed2 + samples!![next1] * scaledGain
			@Pc(65) val slot3: Int = offset++
			@Pc(68) val mixed3: Int = mix!![slot3]
			position = next2 + 1
			mix!![slot3] = mixed3 + samples!![next2] * scaledGain
		}
		stop += 3
		while (offset < stop) {
			slot0 = offset++
			mix!![slot0] += samples!![position++] * scaledGain
		}
		stream!!.position = position shl 8
		return offset
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(II[B[IIIIIIILclient!via;II)I")
	@JvmStatic
	fun mixBackwardMonoResampled(@OriginalArg(2) samples: ByteArray?, @OriginalArg(3) mix: IntArray?, @OriginalArg(4) position: Int, @OriginalArg(5) offset: Int, @OriginalArg(6) gain: Int, @OriginalArg(8) end: Int, @OriginalArg(9) limit: Int, @OriginalArg(10) stream: AreaSoundStream?, @OriginalArg(11) rate: Int, @OriginalArg(12) edgeSample: Int): Int {
		var position: Int = position
		var offset: Int = offset
		@Pc(14) var stop: Int
		if (rate == 0) {
			stop = end
		} else {
			stop = offset + (limit + rate + 256 - position) / rate
			if (stop > end) {
				stop = end
			}
		}
		@Pc(31) var slot: Int
		while (offset < stop) {
			@Pc(23) val base: Int = position shr 8
			@Pc(29) val sample: Byte = samples!![base - 1]
			slot = offset++
			mix!![slot] += ((sample.toInt() shl 8) + (samples!![base] - sample) * (position and 0xFF)) * gain shr 6
			position += rate
		}
		if (rate == 0) {
			stop = end
		} else {
			stop = offset + (limit + rate - position) / rate
			if (stop > end) {
				stop = end
			}
		}
		while (offset < stop) {
			slot = offset++
			mix!![slot] += ((edgeSample shl 8) + (samples!![position shr 8] - edgeSample) * (position and 0xFF)) * gain shr 6
			position += rate
		}
		stream!!.position = position
		return offset
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "([B[IIIIIIIILclient!via;)I")
	@JvmStatic
	fun mixBackwardMonoRamped(@OriginalArg(0) samples: ByteArray?, @OriginalArg(1) mix: IntArray?, @OriginalArg(2) position: Int, @OriginalArg(3) offset: Int, @OriginalArg(4) gain: Int, @OriginalArg(5) gainStep: Int, @OriginalArg(7) end: Int, @OriginalArg(8) limit: Int, @OriginalArg(9) stream: AreaSoundStream?): Int {
		var position: Int = position
		var offset: Int = offset
		var gain: Int = gain
		position = position shr 0x8
		@Pc(7) val limitIndex: Int = limit shr 8
		gain = gain shl 0x2
		@Pc(15) val scaledStep: Int = gainStep shl 2
		@Pc(24) var stop: Int
		stop = offset + position + 1 - limitIndex
		if (stop > end) {
			stop = end
		}
		stream!!.leftGain += stream!!.leftGainStep * (stop - offset)
		stream!!.rightGain += stream!!.rightGainStep * (stop - offset)
		stop -= 3
		@Pc(54) var slot0: Int
		while (offset < stop) {
			slot0 = offset++
			@Pc(57) val mixed0: Int = mix!![slot0]
			@Pc(60) val prev0: Int = position - 1
			mix!![slot0] = mixed0 + samples!![position] * gain
			@Pc(69) val gain1: Int = gain + scaledStep
			@Pc(71) val slot1: Int = offset++
			@Pc(74) val mixed1: Int = mix!![slot1]
			@Pc(77) val prev1: Int = prev0 - 1
			mix!![slot1] = mixed1 + samples!![prev0] * gain1
			@Pc(86) val gain2: Int = gain1 + scaledStep
			@Pc(88) val slot2: Int = offset++
			@Pc(91) val mixed2: Int = mix!![slot2]
			@Pc(94) val prev2: Int = prev1 - 1
			mix!![slot2] = mixed2 + samples!![prev1] * gain2
			@Pc(103) val gain3: Int = gain2 + scaledStep
			@Pc(105) val slot3: Int = offset++
			@Pc(108) val mixed3: Int = mix!![slot3]
			position = prev2 - 1
			mix!![slot3] = mixed3 + samples!![prev2] * gain3
			gain = gain3 + scaledStep
		}
		stop += 3
		while (offset < stop) {
			slot0 = offset++
			mix!![slot0] += samples!![position--] * gain
			gain += scaledStep
		}
		stream!!.gain = gain shr 2
		stream!!.position = position shl 8
		return offset
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(I[B[IIIIIIIILclient!via;)I")
	@JvmStatic
	fun mixForwardStereo(@OriginalArg(1) samples: ByteArray?, @OriginalArg(2) mix: IntArray?, @OriginalArg(3) position: Int, @OriginalArg(4) offset: Int, @OriginalArg(5) leftGain: Int, @OriginalArg(6) rightGain: Int, @OriginalArg(8) end: Int, @OriginalArg(9) limit: Int, @OriginalArg(10) stream: AreaSoundStream?): Int {
		var position: Int = position
		var offset: Int = offset
		position = position shr 0x8
		@Pc(7) val limitIndex: Int = limit shr 8
		@Pc(11) val scaledLeft: Int = leftGain shl 2
		@Pc(15) val scaledRight: Int = rightGain shl 2
		@Pc(22) var stop: Int
		stop = offset + limitIndex - position
		if (stop > end) {
			stop = end
		}
		offset = offset shl 0x1
		stop = stop shl 0x1
		stop -= 6
		@Pc(41) var sample0: Byte
		@Pc(44) var leftSlot0: Int
		while (offset < stop) {
			@Pc(39) val next0: Int = position + 1
			sample0 = samples!![position]
			leftSlot0 = offset + 1
			mix!![offset] += sample0 * scaledLeft
			@Pc(54) val rightSlot0: Int = leftSlot0 + 1
			mix!![leftSlot0] += sample0 * scaledRight
			@Pc(64) val next1: Int = next0 + 1
			@Pc(66) val sample1: Byte = samples!![next0]
			@Pc(69) val leftSlot1: Int = rightSlot0 + 1
			mix!![rightSlot0] += sample1 * scaledLeft
			@Pc(79) val rightSlot1: Int = leftSlot1 + 1
			mix!![leftSlot1] += sample1 * scaledRight
			@Pc(89) val next2: Int = next1 + 1
			@Pc(91) val sample2: Byte = samples!![next1]
			@Pc(94) val leftSlot2: Int = rightSlot1 + 1
			mix!![rightSlot1] += sample2 * scaledLeft
			@Pc(104) val rightSlot2: Int = leftSlot2 + 1
			mix!![leftSlot2] += sample2 * scaledRight
			position = next2 + 1
			@Pc(116) val sample3: Byte = samples!![next2]
			@Pc(119) val leftSlot3: Int = rightSlot2 + 1
			mix!![rightSlot2] += sample3 * scaledLeft
			offset = leftSlot3 + 1
			mix!![leftSlot3] += sample3 * scaledRight
		}
		stop += 6
		while (offset < stop) {
			sample0 = samples!![position++]
			leftSlot0 = offset + 1
			mix!![offset] += sample0 * scaledLeft
			offset = leftSlot0 + 1
			mix!![leftSlot0] += sample0 * scaledRight
		}
		stream!!.position = position shl 8
		return offset shr 1
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(Lclient!ob;III)Lclient!via;")
	@JvmStatic
	fun create(@OriginalArg(0) packet: VariableRateSoundPacket?, @OriginalArg(1) rate: Int, @OriginalArg(2) volume: Int, @OriginalArg(3) range: Int): AreaSoundStream? {
		return if (packet!!.data == null || packet.data!!.size == 0) null else AreaSoundStream(packet, rate, volume, range)
	}

	@OriginalMember(owner = "client!via", name = "c", descriptor = "(II[B[IIIIIIIILclient!via;II)I")
	@JvmStatic
	fun mixBackwardMonoResampledRamped(@OriginalArg(2) samples: ByteArray?, @OriginalArg(3) mix: IntArray?, @OriginalArg(4) position: Int, @OriginalArg(5) offset: Int, @OriginalArg(6) gain: Int, @OriginalArg(7) gainStep: Int, @OriginalArg(9) end: Int, @OriginalArg(10) limit: Int, @OriginalArg(11) stream: AreaSoundStream?, @OriginalArg(12) rate: Int, @OriginalArg(13) edgeSample: Int): Int {
		var position: Int = position
		var offset: Int = offset
		var gain: Int = gain
		stream!!.leftGain -= stream!!.leftGainStep * offset
		stream!!.rightGain -= stream!!.rightGainStep * offset
		@Pc(32) var stop: Int
		if (rate == 0) {
			stop = end
		} else {
			stop = offset + (limit + rate + 256 - position) / rate
			if (stop > end) {
				stop = end
			}
		}
		@Pc(49) var slot: Int
		while (offset < stop) {
			@Pc(41) val base: Int = position shr 8
			@Pc(47) val sample: Byte = samples!![base - 1]
			slot = offset++
			mix!![slot] += ((sample.toInt() shl 8) + (samples!![base] - sample) * (position and 0xFF)) * gain shr 6
			gain += gainStep
			position += rate
		}
		if (rate == 0) {
			stop = end
		} else {
			stop = offset + (limit + rate - position) / rate
			if (stop > end) {
				stop = end
			}
		}
		while (offset < stop) {
			slot = offset++
			mix!![slot] += ((edgeSample shl 8) + (samples!![position shr 8] - edgeSample) * (position and 0xFF)) * gain shr 6
			gain += gainStep
			position += rate
		}
		stream!!.leftGain += stream!!.leftGainStep * offset
		stream!!.rightGain += stream!!.rightGainStep * offset
		stream!!.gain = gain
		stream!!.position = position
		return offset
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "([B[IIIIIIIILclient!via;)I")
	@JvmStatic
	fun mixForwardMonoRamped(@OriginalArg(0) samples: ByteArray?, @OriginalArg(1) mix: IntArray?, @OriginalArg(2) position: Int, @OriginalArg(3) offset: Int, @OriginalArg(4) gain: Int, @OriginalArg(5) gainStep: Int, @OriginalArg(7) end: Int, @OriginalArg(8) limit: Int, @OriginalArg(9) stream: AreaSoundStream?): Int {
		var position: Int = position
		var offset: Int = offset
		var gain: Int = gain
		position = position shr 0x8
		@Pc(7) val limitIndex: Int = limit shr 8
		gain = gain shl 0x2
		@Pc(15) val scaledStep: Int = gainStep shl 2
		@Pc(22) var stop: Int
		stop = offset + limitIndex - position
		if (stop > end) {
			stop = end
		}
		stream!!.leftGain += stream!!.leftGainStep * (stop - offset)
		stream!!.rightGain += stream!!.rightGainStep * (stop - offset)
		stop -= 3
		@Pc(52) var slot0: Int
		while (offset < stop) {
			slot0 = offset++
			@Pc(55) val mixed0: Int = mix!![slot0]
			@Pc(58) val next0: Int = position + 1
			mix!![slot0] = mixed0 + samples!![position] * gain
			@Pc(67) val gain1: Int = gain + scaledStep
			@Pc(69) val slot1: Int = offset++
			@Pc(72) val mixed1: Int = mix!![slot1]
			@Pc(75) val next1: Int = next0 + 1
			mix!![slot1] = mixed1 + samples!![next0] * gain1
			@Pc(84) val gain2: Int = gain1 + scaledStep
			@Pc(86) val slot2: Int = offset++
			@Pc(89) val mixed2: Int = mix!![slot2]
			@Pc(92) val next2: Int = next1 + 1
			mix!![slot2] = mixed2 + samples!![next1] * gain2
			@Pc(101) val gain3: Int = gain2 + scaledStep
			@Pc(103) val slot3: Int = offset++
			@Pc(106) val mixed3: Int = mix!![slot3]
			position = next2 + 1
			mix!![slot3] = mixed3 + samples!![next2] * gain3
			gain = gain3 + scaledStep
		}
		stop += 3
		while (offset < stop) {
			slot0 = offset++
			mix!![slot0] += samples!![position++] * gain
			gain += scaledStep
		}
		stream!!.gain = gain shr 2
		stream!!.position = position shl 8
		return offset
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(II[B[IIIIIIIIIILclient!via;II)I")
	@JvmStatic
	fun mixBackwardStereoResampledRamped(@OriginalArg(2) samples: ByteArray?, @OriginalArg(3) mix: IntArray?, @OriginalArg(4) position: Int, @OriginalArg(5) offset: Int, @OriginalArg(6) leftGain: Int, @OriginalArg(7) rightGain: Int, @OriginalArg(8) leftGainStep: Int, @OriginalArg(9) rightGainStep: Int, @OriginalArg(11) end: Int, @OriginalArg(12) limit: Int, @OriginalArg(13) stream: AreaSoundStream?, @OriginalArg(14) rate: Int, @OriginalArg(15) edgeSample: Int): Int {
		var position: Int = position
		var offset: Int = offset
		var leftGain: Int = leftGain
		var rightGain: Int = rightGain
		stream!!.gain -= stream!!.gainStep * offset
		@Pc(23) var stop: Int
		if (rate == 0) {
			stop = end
		} else {
			stop = offset + (limit + rate + 256 - position) / rate
			if (stop > end) {
				stop = end
			}
		}
		offset = offset shl 0x1
		stop = stop shl 0x1
		@Pc(63) var next: Int
		@Pc(60) var interpolated: Int
		@Pc(62) var slot: Int
		while (offset < stop) {
			@Pc(40) val base: Int = position shr 8
			@Pc(46) val sample: Byte = samples!![base - 1]
			interpolated = (sample.toInt() shl 8) + (samples!![base] - sample) * (position and 0xFF)
			slot = offset
			next = offset + 1
			mix!![slot] += interpolated * leftGain shr 6
			leftGain += leftGainStep
			@Pc(78) val rightSlot: Int = next
			offset = next + 1
			mix!![rightSlot] += interpolated * rightGain shr 6
			rightGain += rightGainStep
			position += rate
		}
		if (rate == 0) {
			stop = end
		} else {
			stop = (offset shr 1) + (limit + rate - position) / rate
			if (stop > end) {
				stop = end
			}
		}
		stop = stop shl 0x1
		while (offset < stop) {
			interpolated = (edgeSample shl 8) + (samples!![position shr 8] - edgeSample) * (position and 0xFF)
			slot = offset
			next = offset + 1
			mix!![slot] += interpolated * leftGain shr 6
			leftGain += leftGainStep
			slot = next
			offset = next + 1
			mix!![slot] += interpolated * rightGain shr 6
			rightGain += rightGainStep
			position += rate
		}
		next = offset shr 1
		stream!!.gain += stream!!.gainStep * next
		stream!!.leftGain = leftGain
		stream!!.rightGain = rightGain
		stream!!.position = position
		return next
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "([B[IIIIIIILclient!via;)I")
	@JvmStatic
	fun mixBackwardMono(@OriginalArg(0) samples: ByteArray?, @OriginalArg(1) mix: IntArray?, @OriginalArg(2) position: Int, @OriginalArg(3) offset: Int, @OriginalArg(4) gain: Int, @OriginalArg(6) end: Int, @OriginalArg(7) limit: Int, @OriginalArg(8) stream: AreaSoundStream?): Int {
		var position: Int = position
		var offset: Int = offset
		position = position shr 0x8
		@Pc(7) val limitIndex: Int = limit shr 8
		@Pc(11) val scaledGain: Int = gain shl 2
		@Pc(20) var stop: Int
		stop = offset + position + 1 - limitIndex
		if (stop > end) {
			stop = end
		}
		stop -= 3
		@Pc(28) var slot0: Int
		while (offset < stop) {
			slot0 = offset++
			@Pc(31) val mixed0: Int = mix!![slot0]
			@Pc(34) val prev0: Int = position - 1
			mix!![slot0] = mixed0 + samples!![position] * scaledGain
			@Pc(41) val slot1: Int = offset++
			@Pc(44) val mixed1: Int = mix!![slot1]
			@Pc(47) val prev1: Int = prev0 - 1
			mix!![slot1] = mixed1 + samples!![prev0] * scaledGain
			@Pc(54) val slot2: Int = offset++
			@Pc(57) val mixed2: Int = mix!![slot2]
			@Pc(60) val prev2: Int = prev1 - 1
			mix!![slot2] = mixed2 + samples!![prev1] * scaledGain
			@Pc(67) val slot3: Int = offset++
			@Pc(70) val mixed3: Int = mix!![slot3]
			position = prev2 - 1
			mix!![slot3] = mixed3 + samples!![prev2] * scaledGain
		}
		stop += 3
		while (offset < stop) {
			slot0 = offset++
			mix!![slot0] += samples!![position--] * scaledGain
		}
		stream!!.position = position shl 8
		return offset
	}

	@OriginalMember(owner = "client!via", name = "d", descriptor = "(II[B[IIIIIIIILclient!via;II)I")
	@JvmStatic
	fun mixForwardStereoResampled(@OriginalArg(2) samples: ByteArray?, @OriginalArg(3) mix: IntArray?, @OriginalArg(4) position: Int, @OriginalArg(5) offset: Int, @OriginalArg(6) leftGain: Int, @OriginalArg(7) rightGain: Int, @OriginalArg(9) end: Int, @OriginalArg(10) limit: Int, @OriginalArg(11) stream: AreaSoundStream?, @OriginalArg(12) rate: Int, @OriginalArg(13) edgeSample: Int): Int {
		var position: Int = position
		var offset: Int = offset
		@Pc(14) var stop: Int
		if (rate == 0) {
			stop = end
		} else {
			stop = offset + (limit + rate - position - 257) / rate
			if (stop > end) {
				stop = end
			}
		}
		offset = offset shl 0x1
		stop = stop shl 0x1
		@Pc(35) var sample: Byte
		@Pc(54) var nextSlot: Int
		@Pc(51) var interpolated: Int
		@Pc(53) var slot: Int
		while (offset < stop) {
			@Pc(31) val base: Int = position shr 8
			sample = samples!![base]
			interpolated = (sample.toInt() shl 8) + (samples!![base + 1] - sample) * (position and 0xFF)
			slot = offset
			nextSlot = offset + 1
			mix!![slot] += interpolated * leftGain shr 6
			@Pc(65) val rightSlot: Int = nextSlot
			offset = nextSlot + 1
			mix!![rightSlot] += interpolated * rightGain shr 6
			position += rate
		}
		if (rate == 0) {
			stop = end
		} else {
			stop = (offset shr 1) + (limit + rate - position - 1) / rate
			if (stop > end) {
				stop = end
			}
		}
		stop = stop shl 0x1
		while (offset < stop) {
			sample = samples!![position shr 8]
			interpolated = (sample.toInt() shl 8) + (edgeSample - sample) * (position and 0xFF)
			slot = offset
			nextSlot = offset + 1
			mix!![slot] += interpolated * leftGain shr 6
			slot = nextSlot
			offset = nextSlot + 1
			mix!![slot] += interpolated * rightGain shr 6
			position += rate
		}
		stream!!.position = position
		return offset shr 1
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(II[B[IIIIIIILclient!via;II)I")
	@JvmStatic
	fun mixForwardMonoResampled(@OriginalArg(2) samples: ByteArray?, @OriginalArg(3) mix: IntArray?, @OriginalArg(4) position: Int, @OriginalArg(5) offset: Int, @OriginalArg(6) gain: Int, @OriginalArg(8) end: Int, @OriginalArg(9) limit: Int, @OriginalArg(10) stream: AreaSoundStream?, @OriginalArg(11) rate: Int, @OriginalArg(12) edgeSample: Int): Int {
		var position: Int = position
		var offset: Int = offset
		@Pc(14) var stop: Int
		if (rate == 0) {
			stop = end
		} else {
			stop = offset + (limit + rate - position - 257) / rate
			if (stop > end) {
				stop = end
			}
		}
		@Pc(27) var sample: Byte
		@Pc(29) var slot: Int
		while (offset < stop) {
			@Pc(23) val base: Int = position shr 8
			sample = samples!![base]
			slot = offset++
			mix!![slot] += ((sample.toInt() shl 8) + (samples!![base + 1] - sample) * (position and 0xFF)) * gain shr 6
			position += rate
		}
		if (rate == 0) {
			stop = end
		} else {
			stop = offset + (limit + rate - position - 1) / rate
			if (stop > end) {
				stop = end
			}
		}
		while (offset < stop) {
			sample = samples!![position shr 8]
			slot = offset++
			mix!![slot] += ((sample.toInt() shl 8) + (edgeSample - sample) * (position and 0xFF)) * gain shr 6
			position += rate
		}
		stream!!.position = position
		return offset
	}
}
