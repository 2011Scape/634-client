package com.jagex.game.runetek5.config.midi;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ra")
public final class Envelope {

	@OriginalMember(owner = "client!ra", name = "b", descriptor = "I")
	public int form;

	@OriginalMember(owner = "client!ra", name = "e", descriptor = "I")
	public int end;

	@OriginalMember(owner = "client!ra", name = "f", descriptor = "I")
	public int start;

	@OriginalMember(owner = "client!ra", name = "g", descriptor = "I")
	private int step;

	@OriginalMember(owner = "client!ra", name = "h", descriptor = "I")
	private int ticks;

	@OriginalMember(owner = "client!ra", name = "i", descriptor = "I")
	private int critical;

	@OriginalMember(owner = "client!ra", name = "j", descriptor = "I")
	private int phaseIndex;

	@OriginalMember(owner = "client!ra", name = "k", descriptor = "I")
	private int amplitude;

	@OriginalMember(owner = "client!ra", name = "c", descriptor = "I")
	private int numPhases = 2;

	@OriginalMember(owner = "client!ra", name = "d", descriptor = "[I")
	private int[] phaseDuration = new int[2];

	@OriginalMember(owner = "client!ra", name = "a", descriptor = "[I")
	private int[] phasePeak = new int[2];

	@OriginalMember(owner = "client!ra", name = "<init>", descriptor = "()V")
	public Envelope() {
		this.phaseDuration[0] = 0;
		this.phaseDuration[1] = 65535;
		this.phasePeak[0] = 0;
		this.phasePeak[1] = 65535;
	}

	@OriginalMember(owner = "client!ra", name = "a", descriptor = "(I)I")
	public int step(@OriginalArg(0) int period) {
		if (this.ticks >= this.critical) {
			this.amplitude = this.phasePeak[this.phaseIndex++] << 15;
			if (this.phaseIndex >= this.numPhases) {
				this.phaseIndex = this.numPhases - 1;
			}
			this.critical = (int) ((double) this.phaseDuration[this.phaseIndex] / 65536.0D * (double) period);
			if (this.critical > this.ticks) {
				this.step = ((this.phasePeak[this.phaseIndex] << 15) - this.amplitude) / (this.critical - this.ticks);
			}
		}
		this.amplitude += this.step;
		this.ticks++;
		return this.amplitude - this.step >> 15;
	}

	@OriginalMember(owner = "client!ra", name = "a", descriptor = "(Lclient!cea;)V")
	public void decode(@OriginalArg(0) Packet packet) {
		this.form = packet.readUByte();
		this.start = packet.readInt();
		this.end = packet.readInt();
		this.decodeShape(packet);
	}

	@OriginalMember(owner = "client!ra", name = "a", descriptor = "()V")
	public void reset() {
		this.critical = 0;
		this.phaseIndex = 0;
		this.step = 0;
		this.amplitude = 0;
		this.ticks = 0;
	}

	@OriginalMember(owner = "client!ra", name = "b", descriptor = "(Lclient!cea;)V")
	public void decodeShape(@OriginalArg(0) Packet packet) {
		this.numPhases = packet.readUByte();
		this.phaseDuration = new int[this.numPhases];
		this.phasePeak = new int[this.numPhases];
		for (@Pc(16) int i = 0; i < this.numPhases; i++) {
			this.phaseDuration[i] = packet.readUShort();
			this.phasePeak[i] = packet.readUShort();
		}
	}
}
