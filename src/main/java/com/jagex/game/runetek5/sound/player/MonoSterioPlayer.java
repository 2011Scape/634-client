package com.jagex.game.runetek5.sound.player;

import com.jagex.game.runetek5.config.rsinterface.Component;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!bha")
public final class MonoSterioPlayer extends PcmPlayer {

	@OriginalMember(owner = "client!bha", name = "F", descriptor = "I")
	private int bufferLength;

	@OriginalMember(owner = "client!bha", name = "G", descriptor = "[B")
	private byte[] buffer;

	@OriginalMember(owner = "client!bha", name = "H", descriptor = "Ljavax/sound/sampled/SourceDataLine;")
	private SourceDataLine sourceDataLine;

	@OriginalMember(owner = "client!bha", name = "J", descriptor = "Ljavax/sound/sampled/AudioFormat;")
	private AudioFormat audioFormat;

	@OriginalMember(owner = "client!bha", name = "I", descriptor = "Z")
	private boolean hasSoundmax = false;

	@OriginalMember(owner = "client!bha", name = "c", descriptor = "()V")
	@Override
	public void close() {
		if (this.sourceDataLine != null) {
			this.sourceDataLine.close();
			this.sourceDataLine = null;
		}
	}

	@OriginalMember(owner = "client!bha", name = "a", descriptor = "(I)V")
	@Override
	public void play(@OriginalArg(0) int bufferLength) throws LineUnavailableException {
		try {
			@Pc(20) Info info = new Info(SourceDataLine.class, this.audioFormat, bufferLength << (PcmPlayerManager.stereo ? 2 : 1));
			this.sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
			this.sourceDataLine.open();
			this.sourceDataLine.start();
			this.bufferLength = bufferLength;
		} catch (@Pc(36) LineUnavailableException ex) {
			if (MonoSterioPlayer.bitCount(bufferLength) == 1) {
				this.sourceDataLine = null;
				throw ex;
			} else {
				this.play(Utils.nextPowerOfTwo(bufferLength));
			}
		}
	}

	@OriginalMember(owner = "client!bha", name = "a", descriptor = "(Ljava/awt/Component;)V")
	@Override
	public void init(@OriginalArg(0) java.awt.Component component) {
		@Pc(1) javax.sound.sampled.Mixer.Info[] infos = AudioSystem.getMixerInfo();
		if (infos != null) {
			for (@Pc(8) int i = 0; i < infos.length; i++) {
				@Pc(16) javax.sound.sampled.Mixer.Info info = infos[i];
				if (info != null) {
					@Pc(21) String string = info.getName();
					if (string != null && string.toLowerCase().indexOf("soundmax") >= 0) {
						this.hasSoundmax = true;
					}
				}
			}
		}
		this.audioFormat = new AudioFormat((float) PcmPlayerManager.sampleRate, 16, PcmPlayerManager.stereo ? 2 : 1, true, false);
		this.buffer = new byte[0x100 << (PcmPlayerManager.stereo ? 2 : 1)];
	}

	@OriginalMember(owner = "client!bha", name = "a", descriptor = "()I")
	@Override
	public int dataLeft() {
		return this.bufferLength - (this.sourceDataLine.available() >> (PcmPlayerManager.stereo ? 2 : 1));
	}

	@OriginalMember(owner = "client!bha", name = "b", descriptor = "()V")
	@Override
	public void open() throws LineUnavailableException {
		this.sourceDataLine.flush();
		if (!this.hasSoundmax) {
			return;
		}
		this.sourceDataLine.close();
		this.sourceDataLine = null;
		@Pc(34) Info info = new Info(SourceDataLine.class, this.audioFormat, this.bufferLength << (PcmPlayerManager.stereo ? 2 : 1));
		this.sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
		this.sourceDataLine.open();
		this.sourceDataLine.start();
	}

	@OriginalMember(owner = "client!bha", name = "d", descriptor = "()V")
	@Override
	public void writeToBuffer() {
		@Pc(1) short sampleCount = 256;
		if (PcmPlayerManager.stereo) {
			sampleCount = 512;
		}
		for (@Pc(9) int i = 0; i < sampleCount; i++) {
			@Pc(17) int sample = this.bitpackedSoundData[i];
			if ((sample + 8388608 & 0xFF000000) != 0) {
				sample = sample >> 31 ^ 0x7FFFFF;
			}
			this.buffer[i * 2] = (byte) (sample >> 8);
			this.buffer[i * 2 + 1] = (byte) (sample >> 16);
		}
		this.sourceDataLine.write(this.buffer, 0, sampleCount << 1);
	}
	@OriginalMember(owner = "client!er", name = "a", descriptor = "(IB)I")
	public static int bitCount(@OriginalArg(0) int value) {
		@Pc(14) int pairSums = (value & 0x55555555) + ((value & 0xAAAAAAAB) >>> 1);
		@Pc(28) int nibbleSums = (pairSums >>> 2 & 0x33333333) + (pairSums & 0x33333333);
		@Pc(36) int byteSums = (nibbleSums >>> 4) + nibbleSums & 0xF0F0F0F;
		@Pc(42) int shortSums = byteSums + (byteSums >>> 8);
		@Pc(48) int total = shortSums + (shortSums >>> 16);
		return total & 0xFF;
	}
}
