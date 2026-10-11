package com.jagex.game.runetek5.sound;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket;
import com.jagex.game.runetek5.sound.jagtheora.AreaSoundStream;

public final class AreaSoundSampleMixer {

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(I[B[IIIIIIIIIILclient!via;)I")
	public static int mixForwardStereoRamped(@OriginalArg(1) byte[] samples, @OriginalArg(2) int[] mix, @OriginalArg(3) int position, @OriginalArg(4) int offset, @OriginalArg(5) int leftGain, @OriginalArg(6) int rightGain, @OriginalArg(7) int leftGainStep, @OriginalArg(8) int rightGainStep, @OriginalArg(10) int end, @OriginalArg(11) int limit, @OriginalArg(12) AreaSoundStream stream) {
		position >>= 0x8;
		@Pc(7) int limitIndex = limit >> 8;
		leftGain <<= 0x2;
		rightGain <<= 0x2;
		@Pc(19) int scaledLeftStep = leftGainStep << 2;
		@Pc(23) int scaledRightStep = rightGainStep << 2;
		@Pc(30) int stop;
		if ((stop = offset + limitIndex - position) > end) {
			stop = end;
		}
		stream.gain += stream.gainStep * (stop - offset);
		offset <<= 0x1;
		stop <<= 0x1;
		stop -= 6;
		@Pc(60) byte sample0;
		@Pc(63) int rightSlot0;
		while (offset < stop) {
			@Pc(58) int next0 = position + 1;
			sample0 = samples[position];
			rightSlot0 = offset + 1;
			mix[offset] += sample0 * leftGain;
			@Pc(74) int leftGain1 = leftGain + scaledLeftStep;
			@Pc(77) int leftSlot1 = rightSlot0 + 1;
			mix[rightSlot0] += sample0 * rightGain;
			@Pc(88) int rightGain1 = rightGain + scaledRightStep;
			@Pc(91) int next1 = next0 + 1;
			@Pc(93) byte sample1 = samples[next0];
			@Pc(96) int rightSlot1 = leftSlot1 + 1;
			mix[leftSlot1] += sample1 * leftGain1;
			@Pc(107) int leftGain2 = leftGain1 + scaledLeftStep;
			@Pc(110) int leftSlot2 = rightSlot1 + 1;
			mix[rightSlot1] += sample1 * rightGain1;
			@Pc(121) int rightGain2 = rightGain1 + scaledRightStep;
			@Pc(124) int next2 = next1 + 1;
			@Pc(126) byte sample2 = samples[next1];
			@Pc(129) int rightSlot2 = leftSlot2 + 1;
			mix[leftSlot2] += sample2 * leftGain2;
			@Pc(140) int leftGain3 = leftGain2 + scaledLeftStep;
			@Pc(143) int leftSlot3 = rightSlot2 + 1;
			mix[rightSlot2] += sample2 * rightGain2;
			@Pc(154) int rightGain3 = rightGain2 + scaledRightStep;
			position = next2 + 1;
			@Pc(159) byte sample3 = samples[next2];
			@Pc(162) int rightSlot3 = leftSlot3 + 1;
			mix[leftSlot3] += sample3 * leftGain3;
			leftGain = leftGain3 + scaledLeftStep;
			offset = rightSlot3 + 1;
			mix[rightSlot3] += sample3 * rightGain3;
			rightGain = rightGain3 + scaledRightStep;
		}
		stop += 6;
		while (offset < stop) {
			sample0 = samples[position++];
			rightSlot0 = offset + 1;
			mix[offset] += sample0 * leftGain;
			leftGain += scaledLeftStep;
			offset = rightSlot0 + 1;
			mix[rightSlot0] += sample0 * rightGain;
			rightGain += scaledRightStep;
		}
		stream.leftGain = leftGain >> 2;
		stream.rightGain = rightGain >> 2;
		stream.position = position << 8;
		return offset >> 1;
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(II[B[IIIIIIIILclient!via;II)I")
	public static int mixBackwardStereoResampled(@OriginalArg(2) byte[] samples, @OriginalArg(3) int[] mix, @OriginalArg(4) int position, @OriginalArg(5) int offset, @OriginalArg(6) int leftGain, @OriginalArg(7) int rightGain, @OriginalArg(9) int end, @OriginalArg(10) int limit, @OriginalArg(11) AreaSoundStream stream, @OriginalArg(12) int rate, @OriginalArg(13) int edgeSample) {
		@Pc(14) int stop;
		if (rate == 0 || (stop = offset + (limit + rate + 256 - position) / rate) > end) {
			stop = end;
		}
		offset <<= 0x1;
		stop <<= 0x1;
		@Pc(54) int nextSlot;
		@Pc(51) int interpolated;
		@Pc(53) int slot;
		while (offset < stop) {
			@Pc(31) int base = position >> 8;
			@Pc(37) byte sample = samples[base - 1];
			interpolated = (sample << 8) + (samples[base] - sample) * (position & 0xFF);
			slot = offset;
			nextSlot = offset + 1;
			mix[slot] += interpolated * leftGain >> 6;
			@Pc(65) int rightSlot = nextSlot;
			offset = nextSlot + 1;
			mix[rightSlot] += interpolated * rightGain >> 6;
			position += rate;
		}
		if (rate == 0 || (stop = (offset >> 1) + (limit + rate - position) / rate) > end) {
			stop = end;
		}
		stop <<= 0x1;
		while (offset < stop) {
			interpolated = (edgeSample << 8) + (samples[position >> 8] - edgeSample) * (position & 0xFF);
			slot = offset;
			nextSlot = offset + 1;
			mix[slot] += interpolated * leftGain >> 6;
			slot = nextSlot;
			offset = nextSlot + 1;
			mix[slot] += interpolated * rightGain >> 6;
			position += rate;
		}
		stream.position = position;
		return offset >> 1;
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(I[B[IIIIIIIILclient!via;)I")
	public static int mixBackwardStereo(@OriginalArg(1) byte[] samples, @OriginalArg(2) int[] mix, @OriginalArg(3) int position, @OriginalArg(4) int offset, @OriginalArg(5) int leftGain, @OriginalArg(6) int rightGain, @OriginalArg(8) int end, @OriginalArg(9) int limit, @OriginalArg(10) AreaSoundStream stream) {
		position >>= 0x8;
		@Pc(7) int limitIndex = limit >> 8;
		@Pc(11) int scaledLeft = leftGain << 2;
		@Pc(15) int scaledRight = rightGain << 2;
		@Pc(24) int stop;
		if ((stop = offset + position + 1 - limitIndex) > end) {
			stop = end;
		}
		offset <<= 0x1;
		stop <<= 0x1;
		stop -= 6;
		@Pc(43) byte sample0;
		@Pc(46) int leftSlot0;
		while (offset < stop) {
			@Pc(41) int prev0 = position - 1;
			sample0 = samples[position];
			leftSlot0 = offset + 1;
			mix[offset] += sample0 * scaledLeft;
			@Pc(56) int rightSlot0 = leftSlot0 + 1;
			mix[leftSlot0] += sample0 * scaledRight;
			@Pc(66) int prev1 = prev0 - 1;
			@Pc(68) byte sample1 = samples[prev0];
			@Pc(71) int leftSlot1 = rightSlot0 + 1;
			mix[rightSlot0] += sample1 * scaledLeft;
			@Pc(81) int rightSlot1 = leftSlot1 + 1;
			mix[leftSlot1] += sample1 * scaledRight;
			@Pc(91) int prev2 = prev1 - 1;
			@Pc(93) byte sample2 = samples[prev1];
			@Pc(96) int leftSlot2 = rightSlot1 + 1;
			mix[rightSlot1] += sample2 * scaledLeft;
			@Pc(106) int rightSlot2 = leftSlot2 + 1;
			mix[leftSlot2] += sample2 * scaledRight;
			position = prev2 - 1;
			@Pc(118) byte sample3 = samples[prev2];
			@Pc(121) int leftSlot3 = rightSlot2 + 1;
			mix[rightSlot2] += sample3 * scaledLeft;
			offset = leftSlot3 + 1;
			mix[leftSlot3] += sample3 * scaledRight;
		}
		stop += 6;
		while (offset < stop) {
			sample0 = samples[position--];
			leftSlot0 = offset + 1;
			mix[offset] += sample0 * scaledLeft;
			offset = leftSlot0 + 1;
			mix[leftSlot0] += sample0 * scaledRight;
		}
		stream.position = position << 8;
		return offset >> 1;
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(II[B[IIIIIIIIIILclient!via;II)I")
	public static int mixForwardStereoResampledRamped(@OriginalArg(2) byte[] samples, @OriginalArg(3) int[] mix, @OriginalArg(4) int position, @OriginalArg(5) int offset, @OriginalArg(6) int leftGain, @OriginalArg(7) int rightGain, @OriginalArg(8) int leftGainStep, @OriginalArg(9) int rightGainStep, @OriginalArg(11) int end, @OriginalArg(12) int limit, @OriginalArg(13) AreaSoundStream stream, @OriginalArg(14) int rate, @OriginalArg(15) int edgeSample) {
		stream.gain -= stream.gainStep * offset;
		@Pc(23) int stop;
		if (rate == 0 || (stop = offset + (limit + rate - position - 257) / rate) > end) {
			stop = end;
		}
		offset <<= 0x1;
		stop <<= 0x1;
		@Pc(44) byte sample;
		@Pc(63) int next;
		@Pc(60) int interpolated;
		@Pc(62) int slot;
		while (offset < stop) {
			@Pc(40) int base = position >> 8;
			sample = samples[base];
			interpolated = (sample << 8) + (samples[base + 1] - sample) * (position & 0xFF);
			slot = offset;
			next = offset + 1;
			mix[slot] += interpolated * leftGain >> 6;
			leftGain += leftGainStep;
			@Pc(78) int rightSlot = next;
			offset = next + 1;
			mix[rightSlot] += interpolated * rightGain >> 6;
			rightGain += rightGainStep;
			position += rate;
		}
		if (rate == 0 || (stop = (offset >> 1) + (limit + rate - position - 1) / rate) > end) {
			stop = end;
		}
		stop <<= 0x1;
		while (offset < stop) {
			sample = samples[position >> 8];
			interpolated = (sample << 8) + (edgeSample - sample) * (position & 0xFF);
			slot = offset;
			next = offset + 1;
			mix[slot] += interpolated * leftGain >> 6;
			leftGain += leftGainStep;
			slot = next;
			offset = next + 1;
			mix[slot] += interpolated * rightGain >> 6;
			rightGain += rightGainStep;
			position += rate;
		}
		next = offset >> 1;
		stream.gain += stream.gainStep * next;
		stream.leftGain = leftGain;
		stream.rightGain = rightGain;
		stream.position = position;
		return next;
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(I[B[IIIIIIIIIILclient!via;)I")
	public static int mixBackwardStereoRamped(@OriginalArg(1) byte[] samples, @OriginalArg(2) int[] mix, @OriginalArg(3) int position, @OriginalArg(4) int offset, @OriginalArg(5) int leftGain, @OriginalArg(6) int rightGain, @OriginalArg(7) int leftGainStep, @OriginalArg(8) int rightGainStep, @OriginalArg(10) int end, @OriginalArg(11) int limit, @OriginalArg(12) AreaSoundStream stream) {
		position >>= 0x8;
		@Pc(7) int limitIndex = limit >> 8;
		leftGain <<= 0x2;
		rightGain <<= 0x2;
		@Pc(19) int scaledLeftStep = leftGainStep << 2;
		@Pc(23) int scaledRightStep = rightGainStep << 2;
		@Pc(32) int stop;
		if ((stop = offset + position + 1 - limitIndex) > end) {
			stop = end;
		}
		stream.gain += stream.gainStep * (stop - offset);
		offset <<= 0x1;
		stop <<= 0x1;
		stop -= 6;
		@Pc(62) byte sample0;
		@Pc(65) int rightSlot0;
		while (offset < stop) {
			@Pc(60) int prev0 = position - 1;
			sample0 = samples[position];
			rightSlot0 = offset + 1;
			mix[offset] += sample0 * leftGain;
			@Pc(76) int leftGain1 = leftGain + scaledLeftStep;
			@Pc(79) int leftSlot1 = rightSlot0 + 1;
			mix[rightSlot0] += sample0 * rightGain;
			@Pc(90) int rightGain1 = rightGain + scaledRightStep;
			@Pc(93) int prev1 = prev0 - 1;
			@Pc(95) byte sample1 = samples[prev0];
			@Pc(98) int rightSlot1 = leftSlot1 + 1;
			mix[leftSlot1] += sample1 * leftGain1;
			@Pc(109) int leftGain2 = leftGain1 + scaledLeftStep;
			@Pc(112) int leftSlot2 = rightSlot1 + 1;
			mix[rightSlot1] += sample1 * rightGain1;
			@Pc(123) int rightGain2 = rightGain1 + scaledRightStep;
			@Pc(126) int prev2 = prev1 - 1;
			@Pc(128) byte sample2 = samples[prev1];
			@Pc(131) int rightSlot2 = leftSlot2 + 1;
			mix[leftSlot2] += sample2 * leftGain2;
			@Pc(142) int leftGain3 = leftGain2 + scaledLeftStep;
			@Pc(145) int leftSlot3 = rightSlot2 + 1;
			mix[rightSlot2] += sample2 * rightGain2;
			@Pc(156) int rightGain3 = rightGain2 + scaledRightStep;
			position = prev2 - 1;
			@Pc(161) byte sample3 = samples[prev2];
			@Pc(164) int rightSlot3 = leftSlot3 + 1;
			mix[leftSlot3] += sample3 * leftGain3;
			leftGain = leftGain3 + scaledLeftStep;
			offset = rightSlot3 + 1;
			mix[rightSlot3] += sample3 * rightGain3;
			rightGain = rightGain3 + scaledRightStep;
		}
		stop += 6;
		while (offset < stop) {
			sample0 = samples[position--];
			rightSlot0 = offset + 1;
			mix[offset] += sample0 * leftGain;
			leftGain += scaledLeftStep;
			offset = rightSlot0 + 1;
			mix[rightSlot0] += sample0 * rightGain;
			rightGain += scaledRightStep;
		}
		stream.leftGain = leftGain >> 2;
		stream.rightGain = rightGain >> 2;
		stream.position = position << 8;
		return offset >> 1;
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(II[B[IIIIIIIILclient!via;II)I")
	public static int mixForwardMonoResampledRamped(@OriginalArg(2) byte[] samples, @OriginalArg(3) int[] mix, @OriginalArg(4) int position, @OriginalArg(5) int offset, @OriginalArg(6) int gain, @OriginalArg(7) int gainStep, @OriginalArg(9) int end, @OriginalArg(10) int limit, @OriginalArg(11) AreaSoundStream stream, @OriginalArg(12) int rate, @OriginalArg(13) int edgeSample) {
		stream.leftGain -= stream.leftGainStep * offset;
		stream.rightGain -= stream.rightGainStep * offset;
		@Pc(32) int stop;
		if (rate == 0 || (stop = offset + (limit + rate - position - 257) / rate) > end) {
			stop = end;
		}
		@Pc(45) byte sample;
		@Pc(47) int slot;
		while (offset < stop) {
			@Pc(41) int base = position >> 8;
			sample = samples[base];
			slot = offset++;
			mix[slot] += ((sample << 8) + (samples[base + 1] - sample) * (position & 0xFF)) * gain >> 6;
			gain += gainStep;
			position += rate;
		}
		if (rate == 0 || (stop = offset + (limit + rate - position - 1) / rate) > end) {
			stop = end;
		}
		while (offset < stop) {
			sample = samples[position >> 8];
			slot = offset++;
			mix[slot] += ((sample << 8) + (edgeSample - sample) * (position & 0xFF)) * gain >> 6;
			gain += gainStep;
			position += rate;
		}
		stream.leftGain += stream.leftGainStep * offset;
		stream.rightGain += stream.rightGainStep * offset;
		stream.gain = gain;
		stream.position = position;
		return offset;
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "([B[IIIIIIILclient!via;)I")
	public static int mixForwardMono(@OriginalArg(0) byte[] samples, @OriginalArg(1) int[] mix, @OriginalArg(2) int position, @OriginalArg(3) int offset, @OriginalArg(4) int gain, @OriginalArg(6) int end, @OriginalArg(7) int limit, @OriginalArg(8) AreaSoundStream stream) {
		position >>= 0x8;
		@Pc(7) int limitIndex = limit >> 8;
		@Pc(11) int scaledGain = gain << 2;
		@Pc(18) int stop;
		if ((stop = offset + limitIndex - position) > end) {
			stop = end;
		}
		stop -= 3;
		@Pc(26) int slot0;
		while (offset < stop) {
			slot0 = offset++;
			@Pc(29) int mixed0 = mix[slot0];
			@Pc(32) int next0 = position + 1;
			mix[slot0] = mixed0 + samples[position] * scaledGain;
			@Pc(39) int slot1 = offset++;
			@Pc(42) int mixed1 = mix[slot1];
			@Pc(45) int next1 = next0 + 1;
			mix[slot1] = mixed1 + samples[next0] * scaledGain;
			@Pc(52) int slot2 = offset++;
			@Pc(55) int mixed2 = mix[slot2];
			@Pc(58) int next2 = next1 + 1;
			mix[slot2] = mixed2 + samples[next1] * scaledGain;
			@Pc(65) int slot3 = offset++;
			@Pc(68) int mixed3 = mix[slot3];
			position = next2 + 1;
			mix[slot3] = mixed3 + samples[next2] * scaledGain;
		}
		stop += 3;
		while (offset < stop) {
			slot0 = offset++;
			mix[slot0] += samples[position++] * scaledGain;
		}
		stream.position = position << 8;
		return offset;
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(II[B[IIIIIIILclient!via;II)I")
	public static int mixBackwardMonoResampled(@OriginalArg(2) byte[] samples, @OriginalArg(3) int[] mix, @OriginalArg(4) int position, @OriginalArg(5) int offset, @OriginalArg(6) int gain, @OriginalArg(8) int end, @OriginalArg(9) int limit, @OriginalArg(10) AreaSoundStream stream, @OriginalArg(11) int rate, @OriginalArg(12) int edgeSample) {
		@Pc(14) int stop;
		if (rate == 0 || (stop = offset + (limit + rate + 256 - position) / rate) > end) {
			stop = end;
		}
		@Pc(31) int slot;
		while (offset < stop) {
			@Pc(23) int base = position >> 8;
			@Pc(29) byte sample = samples[base - 1];
			slot = offset++;
			mix[slot] += ((sample << 8) + (samples[base] - sample) * (position & 0xFF)) * gain >> 6;
			position += rate;
		}
		if (rate == 0 || (stop = offset + (limit + rate - position) / rate) > end) {
			stop = end;
		}
		while (offset < stop) {
			slot = offset++;
			mix[slot] += ((edgeSample << 8) + (samples[position >> 8] - edgeSample) * (position & 0xFF)) * gain >> 6;
			position += rate;
		}
		stream.position = position;
		return offset;
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "([B[IIIIIIIILclient!via;)I")
	public static int mixBackwardMonoRamped(@OriginalArg(0) byte[] samples, @OriginalArg(1) int[] mix, @OriginalArg(2) int position, @OriginalArg(3) int offset, @OriginalArg(4) int gain, @OriginalArg(5) int gainStep, @OriginalArg(7) int end, @OriginalArg(8) int limit, @OriginalArg(9) AreaSoundStream stream) {
		position >>= 0x8;
		@Pc(7) int limitIndex = limit >> 8;
		gain <<= 0x2;
		@Pc(15) int scaledStep = gainStep << 2;
		@Pc(24) int stop;
		if ((stop = offset + position + 1 - limitIndex) > end) {
			stop = end;
		}
		stream.leftGain += stream.leftGainStep * (stop - offset);
		stream.rightGain += stream.rightGainStep * (stop - offset);
		stop -= 3;
		@Pc(54) int slot0;
		while (offset < stop) {
			slot0 = offset++;
			@Pc(57) int mixed0 = mix[slot0];
			@Pc(60) int prev0 = position - 1;
			mix[slot0] = mixed0 + samples[position] * gain;
			@Pc(69) int gain1 = gain + scaledStep;
			@Pc(71) int slot1 = offset++;
			@Pc(74) int mixed1 = mix[slot1];
			@Pc(77) int prev1 = prev0 - 1;
			mix[slot1] = mixed1 + samples[prev0] * gain1;
			@Pc(86) int gain2 = gain1 + scaledStep;
			@Pc(88) int slot2 = offset++;
			@Pc(91) int mixed2 = mix[slot2];
			@Pc(94) int prev2 = prev1 - 1;
			mix[slot2] = mixed2 + samples[prev1] * gain2;
			@Pc(103) int gain3 = gain2 + scaledStep;
			@Pc(105) int slot3 = offset++;
			@Pc(108) int mixed3 = mix[slot3];
			position = prev2 - 1;
			mix[slot3] = mixed3 + samples[prev2] * gain3;
			gain = gain3 + scaledStep;
		}
		stop += 3;
		while (offset < stop) {
			slot0 = offset++;
			mix[slot0] += samples[position--] * gain;
			gain += scaledStep;
		}
		stream.gain = gain >> 2;
		stream.position = position << 8;
		return offset;
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(I[B[IIIIIIIILclient!via;)I")
	public static int mixForwardStereo(@OriginalArg(1) byte[] samples, @OriginalArg(2) int[] mix, @OriginalArg(3) int position, @OriginalArg(4) int offset, @OriginalArg(5) int leftGain, @OriginalArg(6) int rightGain, @OriginalArg(8) int end, @OriginalArg(9) int limit, @OriginalArg(10) AreaSoundStream stream) {
		position >>= 0x8;
		@Pc(7) int limitIndex = limit >> 8;
		@Pc(11) int scaledLeft = leftGain << 2;
		@Pc(15) int scaledRight = rightGain << 2;
		@Pc(22) int stop;
		if ((stop = offset + limitIndex - position) > end) {
			stop = end;
		}
		offset <<= 0x1;
		stop <<= 0x1;
		stop -= 6;
		@Pc(41) byte sample0;
		@Pc(44) int leftSlot0;
		while (offset < stop) {
			@Pc(39) int next0 = position + 1;
			sample0 = samples[position];
			leftSlot0 = offset + 1;
			mix[offset] += sample0 * scaledLeft;
			@Pc(54) int rightSlot0 = leftSlot0 + 1;
			mix[leftSlot0] += sample0 * scaledRight;
			@Pc(64) int next1 = next0 + 1;
			@Pc(66) byte sample1 = samples[next0];
			@Pc(69) int leftSlot1 = rightSlot0 + 1;
			mix[rightSlot0] += sample1 * scaledLeft;
			@Pc(79) int rightSlot1 = leftSlot1 + 1;
			mix[leftSlot1] += sample1 * scaledRight;
			@Pc(89) int next2 = next1 + 1;
			@Pc(91) byte sample2 = samples[next1];
			@Pc(94) int leftSlot2 = rightSlot1 + 1;
			mix[rightSlot1] += sample2 * scaledLeft;
			@Pc(104) int rightSlot2 = leftSlot2 + 1;
			mix[leftSlot2] += sample2 * scaledRight;
			position = next2 + 1;
			@Pc(116) byte sample3 = samples[next2];
			@Pc(119) int leftSlot3 = rightSlot2 + 1;
			mix[rightSlot2] += sample3 * scaledLeft;
			offset = leftSlot3 + 1;
			mix[leftSlot3] += sample3 * scaledRight;
		}
		stop += 6;
		while (offset < stop) {
			sample0 = samples[position++];
			leftSlot0 = offset + 1;
			mix[offset] += sample0 * scaledLeft;
			offset = leftSlot0 + 1;
			mix[leftSlot0] += sample0 * scaledRight;
		}
		stream.position = position << 8;
		return offset >> 1;
	}

	@OriginalMember(owner = "client!via", name = "a", descriptor = "(Lclient!ob;III)Lclient!via;")
	public static AreaSoundStream create(@OriginalArg(0) VariableRateSoundPacket packet, @OriginalArg(1) int rate, @OriginalArg(2) int volume, @OriginalArg(3) int range) {
		return packet.data == null || packet.data.length == 0 ? null : new AreaSoundStream(packet, rate, volume, range);
	}

	@OriginalMember(owner = "client!via", name = "c", descriptor = "(II[B[IIIIIIIILclient!via;II)I")
	public static int mixBackwardMonoResampledRamped(@OriginalArg(2) byte[] samples, @OriginalArg(3) int[] mix, @OriginalArg(4) int position, @OriginalArg(5) int offset, @OriginalArg(6) int gain, @OriginalArg(7) int gainStep, @OriginalArg(9) int end, @OriginalArg(10) int limit, @OriginalArg(11) AreaSoundStream stream, @OriginalArg(12) int rate, @OriginalArg(13) int edgeSample) {
		stream.leftGain -= stream.leftGainStep * offset;
		stream.rightGain -= stream.rightGainStep * offset;
		@Pc(32) int stop;
		if (rate == 0 || (stop = offset + (limit + rate + 256 - position) / rate) > end) {
			stop = end;
		}
		@Pc(49) int slot;
		while (offset < stop) {
			@Pc(41) int base = position >> 8;
			@Pc(47) byte sample = samples[base - 1];
			slot = offset++;
			mix[slot] += ((sample << 8) + (samples[base] - sample) * (position & 0xFF)) * gain >> 6;
			gain += gainStep;
			position += rate;
		}
		if (rate == 0 || (stop = offset + (limit + rate - position) / rate) > end) {
			stop = end;
		}
		while (offset < stop) {
			slot = offset++;
			mix[slot] += ((edgeSample << 8) + (samples[position >> 8] - edgeSample) * (position & 0xFF)) * gain >> 6;
			gain += gainStep;
			position += rate;
		}
		stream.leftGain += stream.leftGainStep * offset;
		stream.rightGain += stream.rightGainStep * offset;
		stream.gain = gain;
		stream.position = position;
		return offset;
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "([B[IIIIIIIILclient!via;)I")
	public static int mixForwardMonoRamped(@OriginalArg(0) byte[] samples, @OriginalArg(1) int[] mix, @OriginalArg(2) int position, @OriginalArg(3) int offset, @OriginalArg(4) int gain, @OriginalArg(5) int gainStep, @OriginalArg(7) int end, @OriginalArg(8) int limit, @OriginalArg(9) AreaSoundStream stream) {
		position >>= 0x8;
		@Pc(7) int limitIndex = limit >> 8;
		gain <<= 0x2;
		@Pc(15) int scaledStep = gainStep << 2;
		@Pc(22) int stop;
		if ((stop = offset + limitIndex - position) > end) {
			stop = end;
		}
		stream.leftGain += stream.leftGainStep * (stop - offset);
		stream.rightGain += stream.rightGainStep * (stop - offset);
		stop -= 3;
		@Pc(52) int slot0;
		while (offset < stop) {
			slot0 = offset++;
			@Pc(55) int mixed0 = mix[slot0];
			@Pc(58) int next0 = position + 1;
			mix[slot0] = mixed0 + samples[position] * gain;
			@Pc(67) int gain1 = gain + scaledStep;
			@Pc(69) int slot1 = offset++;
			@Pc(72) int mixed1 = mix[slot1];
			@Pc(75) int next1 = next0 + 1;
			mix[slot1] = mixed1 + samples[next0] * gain1;
			@Pc(84) int gain2 = gain1 + scaledStep;
			@Pc(86) int slot2 = offset++;
			@Pc(89) int mixed2 = mix[slot2];
			@Pc(92) int next2 = next1 + 1;
			mix[slot2] = mixed2 + samples[next1] * gain2;
			@Pc(101) int gain3 = gain2 + scaledStep;
			@Pc(103) int slot3 = offset++;
			@Pc(106) int mixed3 = mix[slot3];
			position = next2 + 1;
			mix[slot3] = mixed3 + samples[next2] * gain3;
			gain = gain3 + scaledStep;
		}
		stop += 3;
		while (offset < stop) {
			slot0 = offset++;
			mix[slot0] += samples[position++] * gain;
			gain += scaledStep;
		}
		stream.gain = gain >> 2;
		stream.position = position << 8;
		return offset;
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(II[B[IIIIIIIIIILclient!via;II)I")
	public static int mixBackwardStereoResampledRamped(@OriginalArg(2) byte[] samples, @OriginalArg(3) int[] mix, @OriginalArg(4) int position, @OriginalArg(5) int offset, @OriginalArg(6) int leftGain, @OriginalArg(7) int rightGain, @OriginalArg(8) int leftGainStep, @OriginalArg(9) int rightGainStep, @OriginalArg(11) int end, @OriginalArg(12) int limit, @OriginalArg(13) AreaSoundStream stream, @OriginalArg(14) int rate, @OriginalArg(15) int edgeSample) {
		stream.gain -= stream.gainStep * offset;
		@Pc(23) int stop;
		if (rate == 0 || (stop = offset + (limit + rate + 256 - position) / rate) > end) {
			stop = end;
		}
		offset <<= 0x1;
		stop <<= 0x1;
		@Pc(63) int next;
		@Pc(60) int interpolated;
		@Pc(62) int slot;
		while (offset < stop) {
			@Pc(40) int base = position >> 8;
			@Pc(46) byte sample = samples[base - 1];
			interpolated = (sample << 8) + (samples[base] - sample) * (position & 0xFF);
			slot = offset;
			next = offset + 1;
			mix[slot] += interpolated * leftGain >> 6;
			leftGain += leftGainStep;
			@Pc(78) int rightSlot = next;
			offset = next + 1;
			mix[rightSlot] += interpolated * rightGain >> 6;
			rightGain += rightGainStep;
			position += rate;
		}
		if (rate == 0 || (stop = (offset >> 1) + (limit + rate - position) / rate) > end) {
			stop = end;
		}
		stop <<= 0x1;
		while (offset < stop) {
			interpolated = (edgeSample << 8) + (samples[position >> 8] - edgeSample) * (position & 0xFF);
			slot = offset;
			next = offset + 1;
			mix[slot] += interpolated * leftGain >> 6;
			leftGain += leftGainStep;
			slot = next;
			offset = next + 1;
			mix[slot] += interpolated * rightGain >> 6;
			rightGain += rightGainStep;
			position += rate;
		}
		next = offset >> 1;
		stream.gain += stream.gainStep * next;
		stream.leftGain = leftGain;
		stream.rightGain = rightGain;
		stream.position = position;
		return next;
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "([B[IIIIIIILclient!via;)I")
	public static int mixBackwardMono(@OriginalArg(0) byte[] samples, @OriginalArg(1) int[] mix, @OriginalArg(2) int position, @OriginalArg(3) int offset, @OriginalArg(4) int gain, @OriginalArg(6) int end, @OriginalArg(7) int limit, @OriginalArg(8) AreaSoundStream stream) {
		position >>= 0x8;
		@Pc(7) int limitIndex = limit >> 8;
		@Pc(11) int scaledGain = gain << 2;
		@Pc(20) int stop;
		if ((stop = offset + position + 1 - limitIndex) > end) {
			stop = end;
		}
		stop -= 3;
		@Pc(28) int slot0;
		while (offset < stop) {
			slot0 = offset++;
			@Pc(31) int mixed0 = mix[slot0];
			@Pc(34) int prev0 = position - 1;
			mix[slot0] = mixed0 + samples[position] * scaledGain;
			@Pc(41) int slot1 = offset++;
			@Pc(44) int mixed1 = mix[slot1];
			@Pc(47) int prev1 = prev0 - 1;
			mix[slot1] = mixed1 + samples[prev0] * scaledGain;
			@Pc(54) int slot2 = offset++;
			@Pc(57) int mixed2 = mix[slot2];
			@Pc(60) int prev2 = prev1 - 1;
			mix[slot2] = mixed2 + samples[prev1] * scaledGain;
			@Pc(67) int slot3 = offset++;
			@Pc(70) int mixed3 = mix[slot3];
			position = prev2 - 1;
			mix[slot3] = mixed3 + samples[prev2] * scaledGain;
		}
		stop += 3;
		while (offset < stop) {
			slot0 = offset++;
			mix[slot0] += samples[position--] * scaledGain;
		}
		stream.position = position << 8;
		return offset;
	}

	@OriginalMember(owner = "client!via", name = "d", descriptor = "(II[B[IIIIIIIILclient!via;II)I")
	public static int mixForwardStereoResampled(@OriginalArg(2) byte[] samples, @OriginalArg(3) int[] mix, @OriginalArg(4) int position, @OriginalArg(5) int offset, @OriginalArg(6) int leftGain, @OriginalArg(7) int rightGain, @OriginalArg(9) int end, @OriginalArg(10) int limit, @OriginalArg(11) AreaSoundStream stream, @OriginalArg(12) int rate, @OriginalArg(13) int edgeSample) {
		@Pc(14) int stop;
		if (rate == 0 || (stop = offset + (limit + rate - position - 257) / rate) > end) {
			stop = end;
		}
		offset <<= 0x1;
		stop <<= 0x1;
		@Pc(35) byte sample;
		@Pc(54) int nextSlot;
		@Pc(51) int interpolated;
		@Pc(53) int slot;
		while (offset < stop) {
			@Pc(31) int base = position >> 8;
			sample = samples[base];
			interpolated = (sample << 8) + (samples[base + 1] - sample) * (position & 0xFF);
			slot = offset;
			nextSlot = offset + 1;
			mix[slot] += interpolated * leftGain >> 6;
			@Pc(65) int rightSlot = nextSlot;
			offset = nextSlot + 1;
			mix[rightSlot] += interpolated * rightGain >> 6;
			position += rate;
		}
		if (rate == 0 || (stop = (offset >> 1) + (limit + rate - position - 1) / rate) > end) {
			stop = end;
		}
		stop <<= 0x1;
		while (offset < stop) {
			sample = samples[position >> 8];
			interpolated = (sample << 8) + (edgeSample - sample) * (position & 0xFF);
			slot = offset;
			nextSlot = offset + 1;
			mix[slot] += interpolated * leftGain >> 6;
			slot = nextSlot;
			offset = nextSlot + 1;
			mix[slot] += interpolated * rightGain >> 6;
			position += rate;
		}
		stream.position = position;
		return offset >> 1;
	}

	@OriginalMember(owner = "client!via", name = "b", descriptor = "(II[B[IIIIIIILclient!via;II)I")
	public static int mixForwardMonoResampled(@OriginalArg(2) byte[] samples, @OriginalArg(3) int[] mix, @OriginalArg(4) int position, @OriginalArg(5) int offset, @OriginalArg(6) int gain, @OriginalArg(8) int end, @OriginalArg(9) int limit, @OriginalArg(10) AreaSoundStream stream, @OriginalArg(11) int rate, @OriginalArg(12) int edgeSample) {
		@Pc(14) int stop;
		if (rate == 0 || (stop = offset + (limit + rate - position - 257) / rate) > end) {
			stop = end;
		}
		@Pc(27) byte sample;
		@Pc(29) int slot;
		while (offset < stop) {
			@Pc(23) int base = position >> 8;
			sample = samples[base];
			slot = offset++;
			mix[slot] += ((sample << 8) + (samples[base + 1] - sample) * (position & 0xFF)) * gain >> 6;
			position += rate;
		}
		if (rate == 0 || (stop = offset + (limit + rate - position - 1) / rate) > end) {
			stop = end;
		}
		while (offset < stop) {
			sample = samples[position >> 8];
			slot = offset++;
			mix[slot] += ((sample << 8) + (edgeSample - sample) * (position & 0xFF)) * gain >> 6;
			position += rate;
		}
		stream.position = position;
		return offset;
	}
}
