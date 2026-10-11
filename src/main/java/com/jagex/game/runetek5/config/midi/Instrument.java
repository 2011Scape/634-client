package com.jagex.game.runetek5.config.midi;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.core.util.Arrays;
import java.util.Random;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!laa")
public final class Instrument {

	@OriginalMember(owner = "client!laa", name = "j", descriptor = "[I")
	private static final int[] NOISE = new int[32768];

	@OriginalMember(owner = "client!laa", name = "f", descriptor = "[I")
	private static final int[] SINE;

	@OriginalMember(owner = "client!laa", name = "b", descriptor = "Lclient!ra;")
	private Envelope volumeEnvelope;

	@OriginalMember(owner = "client!laa", name = "g", descriptor = "Lclient!ra;")
	private Envelope volumeMultiplier;

	@OriginalMember(owner = "client!laa", name = "h", descriptor = "Lclient!ra;")
	private Envelope release;

	@OriginalMember(owner = "client!laa", name = "i", descriptor = "Lclient!ra;")
	private Envelope pitchModifier;

	@OriginalMember(owner = "client!laa", name = "k", descriptor = "Lclient!ra;")
	private Envelope filterEnvelope;

	@OriginalMember(owner = "client!laa", name = "l", descriptor = "Lclient!ra;")
	private Envelope pitch;

	@OriginalMember(owner = "client!laa", name = "m", descriptor = "Lclient!eha;")
	private Filter filter;

	@OriginalMember(owner = "client!laa", name = "n", descriptor = "Lclient!ra;")
	private Envelope attack;

	@OriginalMember(owner = "client!laa", name = "q", descriptor = "Lclient!ra;")
	private Envelope volumeAmplitude;

	@OriginalMember(owner = "client!laa", name = "s", descriptor = "Lclient!ra;")
	private Envelope pitchModifierAmplitude;

	@OriginalMember(owner = "client!laa", name = "a", descriptor = "I")
	public int loopBegin = 0;

	@OriginalMember(owner = "client!laa", name = "d", descriptor = "[I")
	private final int[] oscillatorVolume = new int[5];

	@OriginalMember(owner = "client!laa", name = "e", descriptor = "[I")
	private final int[] oscillatorPitch = new int[5];

	@OriginalMember(owner = "client!laa", name = "p", descriptor = "I")
	private int delayDecay = 100;

	@OriginalMember(owner = "client!laa", name = "o", descriptor = "I")
	public int duration = 500;

	@OriginalMember(owner = "client!laa", name = "v", descriptor = "[I")
	private final int[] oscillatorDelays = new int[5];

	@OriginalMember(owner = "client!laa", name = "u", descriptor = "I")
	private int delayTime = 0;

	static {
		@Pc(7) Random random = new Random(0L);
		for (@Pc(9) int i = 0; i < 32768; i++) {
			NOISE[i] = (random.nextInt() & 0x2) - 1;
		}
		SINE = new int[32768];
		for (@Pc(28) int i = 0; i < 32768; i++) {
			SINE[i] = (int) (Math.sin((double) i / 5215.1903D) * 16384.0D);
		}
	}

	@OriginalMember(owner = "client!laa", name = "a", descriptor = "(III)I")
	private int evaluateWave(@OriginalArg(0) int tphase, @OriginalArg(1) int amplitude, @OriginalArg(2) int table) {
		if (table == 1) {
			return (tphase & 0x7FFF) < 16384 ? amplitude : -amplitude;
		} else if (table == 2) {
			return SINE[tphase & 0x7FFF] * amplitude >> 14;
		} else if (table == 3) {
			return ((tphase & 0x7FFF) * amplitude >> 14) - amplitude;
		} else if (table == 4) {
			return NOISE[tphase / 2607 & 0x7FFF] * amplitude;
		} else {
			return 0;
		}
	}

	@OriginalMember(owner = "client!laa", name = "a", descriptor = "(Lclient!cea;)V")
	public void decode(@OriginalArg(0) Packet packet) {
		this.pitch = new Envelope();
		this.pitch.decode(packet);
		this.volumeEnvelope = new Envelope();
		this.volumeEnvelope.decode(packet);
		@Pc(21) int used = packet.readUByte();
		if (used != 0) {
			packet.currentPosition--;
			this.pitchModifier = new Envelope();
			this.pitchModifier.decode(packet);
			this.pitchModifierAmplitude = new Envelope();
			this.pitchModifierAmplitude.decode(packet);
		}
		used = packet.readUByte();
		if (used != 0) {
			packet.currentPosition--;
			this.volumeMultiplier = new Envelope();
			this.volumeMultiplier.decode(packet);
			this.volumeAmplitude = new Envelope();
			this.volumeAmplitude.decode(packet);
		}
		used = packet.readUByte();
		if (used != 0) {
			packet.currentPosition--;
			this.release = new Envelope();
			this.release.decode(packet);
			this.attack = new Envelope();
			this.attack.decode(packet);
		}
		for (@Pc(109) int i = 0; i < 10; i++) {
			@Pc(114) int volume = packet.readUSmart();
			if (volume == 0) {
				break;
			}
			this.oscillatorVolume[i] = volume;
			this.oscillatorPitch[i] = packet.readSmart();
			this.oscillatorDelays[i] = packet.readUSmart();
		}
		this.delayTime = packet.readUSmart();
		this.delayDecay = packet.readUSmart();
		this.duration = packet.readUShort();
		this.loopBegin = packet.readUShort();
		this.filter = new Filter();
		this.filterEnvelope = new Envelope();
		this.filter.decode(packet, this.filterEnvelope);
	}

	@OriginalMember(owner = "client!laa", name = "a", descriptor = "(II)[I")
	public int[] synthesize(@OriginalArg(0) int mixDuration, @OriginalArg(1) int instrDuration) {
		Arrays.clear(Instrument.OUTPUT, 0, mixDuration);
		if (instrDuration < 10) {
			return Instrument.OUTPUT;
		}
		@Pc(16) double samplesPerMs = (double) mixDuration / ((double) instrDuration + 0.0D);
		this.pitch.reset();
		this.volumeEnvelope.reset();
		@Pc(24) int pitchModStep = 0;
		@Pc(26) int pitchModBaseStep = 0;
		@Pc(28) int tphase = 0;
		if (this.pitchModifier != null) {
			this.pitchModifier.reset();
			this.pitchModifierAmplitude.reset();
			pitchModStep = (int) ((double) (this.pitchModifier.end - this.pitchModifier.start) * 32.768D / samplesPerMs);
			pitchModBaseStep = (int) ((double) this.pitchModifier.start * 32.768D / samplesPerMs);
		}
		@Pc(63) int volumeModStep = 0;
		@Pc(65) int volumeModBaseStep = 0;
		@Pc(67) int volumeModPhase = 0;
		if (this.volumeMultiplier != null) {
			this.volumeMultiplier.reset();
			this.volumeAmplitude.reset();
			volumeModStep = (int) ((double) (this.volumeMultiplier.end - this.volumeMultiplier.start) * 32.768D / samplesPerMs);
			volumeModBaseStep = (int) ((double) this.volumeMultiplier.start * 32.768D / samplesPerMs);
		}
		for (@Pc(102) int i = 0; i < 5; i++) {
			if (this.oscillatorVolume[i] != 0) {
				Instrument.PHASES[i] = 0;
				Instrument.DELAYS[i] = (int) ((double) this.oscillatorDelays[i] * samplesPerMs);
				Instrument.VOLUME_STEP[i] = (this.oscillatorVolume[i] << 14) / 100;
				Instrument.PITCH_STEP[i] = (int) ((double) (this.pitch.end - this.pitch.start) * 32.768D * Math.pow(1.0057929410678534D, (double) this.oscillatorPitch[i]) / samplesPerMs);
				Instrument.PITCH_BASE_STEP[i] = (int) ((double) this.pitch.start * 32.768D / samplesPerMs);
			}
		}
		@Pc(182) int local182;
		@Pc(187) int local187;
		@Pc(195) int local195;
		@Pc(200) int local200;
		for (@Pc(176) int j = 0; j < mixDuration; j++) {
			int frequency = this.pitch.step(mixDuration);
			int amplitude = this.volumeEnvelope.step(mixDuration);
			if (this.pitchModifier != null) {
				int pitchModValue = this.pitchModifier.step(mixDuration);
				int pitchModAmplitude = this.pitchModifierAmplitude.step(mixDuration);
				frequency += this.evaluateWave(tphase, pitchModAmplitude, this.pitchModifier.form) >> 1;
				tphase += (pitchModValue * pitchModStep >> 16) + pitchModBaseStep;
			}
			if (this.volumeMultiplier != null) {
				int volumeModValue = this.volumeMultiplier.step(mixDuration);
				int volumeModAmplitude = this.volumeAmplitude.step(mixDuration);
				amplitude = amplitude * ((this.evaluateWave(volumeModPhase, volumeModAmplitude, this.volumeMultiplier.form) >> 1) + 32768) >> 15;
				volumeModPhase += (volumeModValue * volumeModStep >> 16) + volumeModBaseStep;
			}
			for (int oscillator = 0; oscillator < 5; oscillator++) {
				if (this.oscillatorVolume[oscillator] != 0) {
					int delayedIndex = j + Instrument.DELAYS[oscillator];
					if (delayedIndex < mixDuration) {
						Instrument.OUTPUT[delayedIndex] += this.evaluateWave(Instrument.PHASES[oscillator], amplitude * Instrument.VOLUME_STEP[oscillator] >> 15, this.pitch.form);
						Instrument.PHASES[oscillator] += (frequency * Instrument.PITCH_STEP[oscillator] >> 16) + Instrument.PITCH_BASE_STEP[oscillator];
					}
				}
			}
		}
		@Pc(347) int local347;
		@Pc(352) int local352;
		if (this.release != null) {
			this.release.reset();
			this.attack.reset();
			int gateTimer = 0;
			@Pc(339) boolean muted = true;
			for (int sampleIndex = 0; sampleIndex < mixDuration; sampleIndex++) {
				int releaseValue = this.release.step(mixDuration);
				int attackValue = this.attack.step(mixDuration);
				int gateThreshold;
				if (muted) {
					gateThreshold = this.release.start + ((this.release.end - this.release.start) * releaseValue >> 8);
				} else {
					gateThreshold = this.release.start + ((this.release.end - this.release.start) * attackValue >> 8);
				}
				gateTimer += 256;
				if (gateTimer >= gateThreshold) {
					gateTimer = 0;
					muted = !muted;
				}
				if (muted) {
					Instrument.OUTPUT[sampleIndex] = 0;
				}
			}
		}
		if (this.delayTime > 0 && this.delayDecay > 0) {
			int delaySamples = (int) ((double) this.delayTime * samplesPerMs);
			for (int delayIndex = delaySamples; delayIndex < mixDuration; delayIndex++) {
				Instrument.OUTPUT[delayIndex] += Instrument.OUTPUT[delayIndex - delaySamples] * this.delayDecay / 100;
			}
		}
		if (this.filter.numPairs[0] > 0 || this.filter.numPairs[1] > 0) {
			this.filterEnvelope.reset();
			int filterValue = this.filterEnvelope.step(mixDuration + 1);
			int forwardOrder = this.filter.compute(0, (float) filterValue / 65536.0F);
			int backwardOrder = this.filter.compute(1, (float) filterValue / 65536.0F);
			if (mixDuration >= forwardOrder + backwardOrder) {
				int outputIndex = 0;
				int blockEnd = backwardOrder;
				if (backwardOrder > mixDuration - forwardOrder) {
					blockEnd = mixDuration - forwardOrder;
				}
				@Pc(519) int ffIndex;
				@Pc(549) int fbIndex;
				while (outputIndex < blockEnd) {
					int filteredSample = (int) ((long) Instrument.OUTPUT[outputIndex + forwardOrder] * (long) Filter.invUnityInt >> 16);
					for (ffIndex = 0; ffIndex < forwardOrder; ffIndex++) {
						filteredSample += (int) ((long) Instrument.OUTPUT[outputIndex + forwardOrder - ffIndex - 1] * (long) Filter.COEFFICIENT_INT[0][ffIndex] >> 16);
					}
					for (fbIndex = 0; fbIndex < outputIndex; fbIndex++) {
						filteredSample -= (int) ((long) Instrument.OUTPUT[outputIndex - fbIndex - 1] * (long) Filter.COEFFICIENT_INT[1][fbIndex] >> 16);
					}
					Instrument.OUTPUT[outputIndex] = filteredSample;
					filterValue = this.filterEnvelope.step(mixDuration + 1);
					outputIndex++;
				}
				blockEnd = 128;
				while (true) {
					if (blockEnd > mixDuration - forwardOrder) {
						blockEnd = mixDuration - forwardOrder;
					}
					while (outputIndex < blockEnd) {
						int filteredSample = (int) ((long) Instrument.OUTPUT[outputIndex + forwardOrder] * (long) Filter.invUnityInt >> 16);
						for (ffIndex = 0; ffIndex < forwardOrder; ffIndex++) {
							filteredSample += (int) ((long) Instrument.OUTPUT[outputIndex + forwardOrder - ffIndex - 1] * (long) Filter.COEFFICIENT_INT[0][ffIndex] >> 16);
						}
						for (fbIndex = 0; fbIndex < backwardOrder; fbIndex++) {
							filteredSample -= (int) ((long) Instrument.OUTPUT[outputIndex - fbIndex - 1] * (long) Filter.COEFFICIENT_INT[1][fbIndex] >> 16);
						}
						Instrument.OUTPUT[outputIndex] = filteredSample;
						filterValue = this.filterEnvelope.step(mixDuration + 1);
						outputIndex++;
					}
					if (outputIndex >= mixDuration - forwardOrder) {
						while (outputIndex < mixDuration) {
							int filteredSample = 0;
							for (ffIndex = outputIndex + forwardOrder - mixDuration; ffIndex < forwardOrder; ffIndex++) {
								filteredSample += (int) ((long) Instrument.OUTPUT[outputIndex + forwardOrder - ffIndex - 1] * (long) Filter.COEFFICIENT_INT[0][ffIndex] >> 16);
							}
							for (fbIndex = 0; fbIndex < backwardOrder; fbIndex++) {
								filteredSample -= (int) ((long) Instrument.OUTPUT[outputIndex - fbIndex - 1] * (long) Filter.COEFFICIENT_INT[1][fbIndex] >> 16);
							}
							Instrument.OUTPUT[outputIndex] = filteredSample;
							this.filterEnvelope.step(mixDuration + 1);
							outputIndex++;
						}
						break;
					}
					forwardOrder = this.filter.compute(0, (float) filterValue / 65536.0F);
					backwardOrder = this.filter.compute(1, (float) filterValue / 65536.0F);
					blockEnd += 128;
				}
			}
		}
		for (int sampleIndex = 0; sampleIndex < mixDuration; sampleIndex++) {
			if (Instrument.OUTPUT[sampleIndex] < -32768) {
				Instrument.OUTPUT[sampleIndex] = -32768;
			}
			if (Instrument.OUTPUT[sampleIndex] > 32767) {
				Instrument.OUTPUT[sampleIndex] = 32767;
			}
		}
		return Instrument.OUTPUT;
	}

	@OriginalMember(owner = "client!laa", name = "c", descriptor = "[I")
	public static final int[] OUTPUT = new int[220500];

	@OriginalMember(owner = "client!laa", name = "r", descriptor = "[I")
	public static final int[] DELAYS = new int[5];

	@OriginalMember(owner = "client!laa", name = "t", descriptor = "[I")
	public static final int[] PITCH_BASE_STEP = new int[5];

	@OriginalMember(owner = "client!laa", name = "w", descriptor = "[I")
	public static final int[] PITCH_STEP = new int[5];

	@OriginalMember(owner = "client!laa", name = "x", descriptor = "[I")
	public static final int[] VOLUME_STEP = new int[5];

	@OriginalMember(owner = "client!laa", name = "y", descriptor = "[I")
	public static final int[] PHASES = new int[5];
}
