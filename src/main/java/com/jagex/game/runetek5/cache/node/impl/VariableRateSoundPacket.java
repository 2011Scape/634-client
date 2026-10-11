package com.jagex.game.runetek5.cache.node.impl;

import com.jagex.game.runetek5.cache.node.impl.AudioNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.sound.Resampler;

@OriginalClass("client!ob")
public final class VariableRateSoundPacket extends AudioNode {

	@OriginalMember(owner = "client!ob", name = "s", descriptor = "Z")
	public boolean pingPong;

	@OriginalMember(owner = "client!ob", name = "t", descriptor = "I")
	public int sampleRate;

	@OriginalMember(owner = "client!ob", name = "p", descriptor = "[B")
	public byte[] data;

	@OriginalMember(owner = "client!ob", name = "r", descriptor = "I")
	public int nominalBitRate;

	@OriginalMember(owner = "client!ob", name = "q", descriptor = "I")
	public int minBitRate;

	@OriginalMember(owner = "client!ob", name = "<init>", descriptor = "(I[BII)V")
	public VariableRateSoundPacket(@OriginalArg(0) int sampleRate, @OriginalArg(1) byte[] data, @OriginalArg(2) int nominalBitRate, @OriginalArg(3) int minBitRate) {
		this.sampleRate = sampleRate;
		this.data = data;
		this.nominalBitRate = nominalBitRate;
		this.minBitRate = minBitRate;
	}

	@OriginalMember(owner = "client!ob", name = "<init>", descriptor = "(I[BIIZ)V")
	public VariableRateSoundPacket(@OriginalArg(0) int sampleRate, @OriginalArg(1) byte[] data, @OriginalArg(2) int nominalBitRate, @OriginalArg(3) int minBitRate, @OriginalArg(4) boolean pingPong) {
		this.sampleRate = sampleRate;
		this.data = data;
		this.nominalBitRate = nominalBitRate;
		this.minBitRate = minBitRate;
		this.pingPong = pingPong;
	}

	@OriginalMember(owner = "client!ob", name = "a", descriptor = "(Lclient!pe;)Lclient!ob;")
	public VariableRateSoundPacket resample(@OriginalArg(0) Resampler sampleRateConverter) {
		this.data = sampleRateConverter.resample(this.data);
		this.sampleRate = sampleRateConverter.convertSampleRate(this.sampleRate);
		if (this.nominalBitRate == this.minBitRate) {
			this.nominalBitRate = this.minBitRate = sampleRateConverter.convertBitRate(this.nominalBitRate);
		} else {
			this.nominalBitRate = sampleRateConverter.convertBitRate(this.nominalBitRate);
			this.minBitRate = sampleRateConverter.convertBitRate(this.minBitRate);
			if (this.nominalBitRate == this.minBitRate) {
				this.nominalBitRate--;
			}
		}
		return this;
	}
}
