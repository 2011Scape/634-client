package com.jagex.game.runetek5.config.midi;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket;

@OriginalClass("client!ck")
public final class SoundEffect {

	@OriginalMember(owner = "client!ck", name = "c", descriptor = "[Lclient!laa;")
	private final Instrument[] instruments = new Instrument[10];

	@OriginalMember(owner = "client!ck", name = "a", descriptor = "I")
	private int loopStart;

	@OriginalMember(owner = "client!ck", name = "b", descriptor = "I")
	private int loopEnd;

	@OriginalMember(owner = "client!ck", name = "<init>", descriptor = "(Lclient!cea;)V")
	public SoundEffect(@OriginalArg(0) Packet packet) {
		for (@Pc(7) int i = 0; i < 10; i++) {
			@Pc(12) int used = packet.readUByte();
			if (used != 0) {
				packet.currentPosition--;
				this.instruments[i] = new Instrument();
				this.instruments[i].decode(packet);
			}
		}
		this.loopStart = packet.readUShort();
		this.loopEnd = packet.readUShort();
	}

	@OriginalMember(owner = "client!ck", name = "<init>", descriptor = "()V")
	private SoundEffect() {
	}

	@OriginalMember(owner = "client!ck", name = "a", descriptor = "()Lclient!ob;")
	public VariableRateSoundPacket getMixedAudio() {
		@Pc(2) byte[] mixed = this.mix();
		return new VariableRateSoundPacket(22050, mixed, this.loopStart * 22050 / 1000, this.loopEnd * 22050 / 1000);
	}

	@OriginalMember(owner = "client!ck", name = "b", descriptor = "()I")
	public int getDelay() {
		@Pc(1) int offset = 9999999;
		for (@Pc(3) int i = 0; i < 10; i++) {
			if (this.instruments[i] != null && this.instruments[i].loopBegin / 20 < offset) {
				offset = this.instruments[i].loopBegin / 20;
			}
		}
		if (this.loopStart < this.loopEnd && this.loopStart / 20 < offset) {
			offset = this.loopStart / 20;
		}
		if (offset == 9999999 || offset == 0) {
			return 0;
		}
		for (@Pc(55) int j = 0; j < 10; j++) {
			if (this.instruments[j] != null) {
				this.instruments[j].loopBegin -= offset * 20;
			}
		}
		if (this.loopStart < this.loopEnd) {
			this.loopStart -= offset * 20;
			this.loopEnd -= offset * 20;
		}
		return offset;
	}

	@OriginalMember(owner = "client!ck", name = "c", descriptor = "()[B")
	private byte[] mix() {
		@Pc(1) int maxDuration = 0;
		for (@Pc(3) int i = 0; i < 10; i++) {
			if (this.instruments[i] != null && this.instruments[i].duration + this.instruments[i].loopBegin > maxDuration) {
				maxDuration = this.instruments[i].duration + this.instruments[i].loopBegin;
			}
		}
		if (maxDuration == 0) {
			return new byte[0];
		}
		@Pc(49) int sampleCount = maxDuration * 22050 / 1000;
		@Pc(52) byte[] mixed = new byte[sampleCount];
		for (@Pc(54) int k = 0; k < 10; k++) {
			if (this.instruments[k] != null) {
				@Pc(70) int instrumentSamples = this.instruments[k].duration * 22050 / 1000;
				@Pc(80) int instrumentOffset = this.instruments[k].loopBegin * 22050 / 1000;
				@Pc(92) int[] samples = this.instruments[k].synthesize(instrumentSamples, this.instruments[k].duration);
				for (@Pc(94) int j = 0; j < instrumentSamples; j++) {
					@Pc(107) int out = mixed[j + instrumentOffset] + (samples[j] >> 8);
					if ((out + 128 & 0xFFFFFF00) != 0) {
						out = out >> 31 ^ 0x7F;
					}
					mixed[j + instrumentOffset] = (byte) out;
				}
			}
		}
		return mixed;
	}
}
