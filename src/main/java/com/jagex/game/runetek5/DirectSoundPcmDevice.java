package com.jagex.game.runetek5;

import com.jagex.game.runetek5.PcmDevice;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.ms.com.ComFailException;
import com.ms.com._Guid;
import com.ms.directX.DSBufferDesc;
import com.ms.directX.DSCursors;
import com.ms.directX.DirectSound;
import com.ms.directX.DirectSoundBuffer;
import com.ms.directX.WaveFormatEx;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ac")
public final class DirectSoundPcmDevice implements PcmDevice {

	@OriginalMember(owner = "client!ac", name = "c", descriptor = "[I")
	private int[] silence;

	@OriginalMember(owner = "client!ac", name = "f", descriptor = "I")
	private int channels;

	@OriginalMember(owner = "client!ac", name = "g", descriptor = "I")
	private int sampleRate;

	@OriginalMember(owner = "client!ac", name = "j", descriptor = "I")
	private int frameBytes;

	@OriginalMember(owner = "client!ac", name = "d", descriptor = "[Lcom/ms/directX/DSCursors;")
	private final DSCursors[] cursors = new DSCursors[2];

	@OriginalMember(owner = "client!ac", name = "a", descriptor = "[Lcom/ms/directX/DSBufferDesc;")
	private final DSBufferDesc[] bufferDescs = new DSBufferDesc[2];

	@OriginalMember(owner = "client!ac", name = "e", descriptor = "[Z")
	private final boolean[] playing = new boolean[2];

	@OriginalMember(owner = "client!ac", name = "b", descriptor = "[Lcom/ms/directX/DirectSoundBuffer;")
	private final DirectSoundBuffer[] buffers = new DirectSoundBuffer[2];

	@OriginalMember(owner = "client!ac", name = "h", descriptor = "[[B")
	private final byte[][] bytes = new byte[2][];

	@OriginalMember(owner = "client!ac", name = "i", descriptor = "[I")
	private final int[] writePositions = new int[2];

	@OriginalMember(owner = "client!ac", name = "m", descriptor = "[I")
	private final int[] bufferSizes = new int[2];

	@OriginalMember(owner = "client!ac", name = "k", descriptor = "Lcom/ms/directX/DirectSound;")
	private final DirectSound directSound = new DirectSound();

	@OriginalMember(owner = "client!ac", name = "l", descriptor = "Lcom/ms/directX/WaveFormatEx;")
	private final WaveFormatEx waveFormat = new WaveFormatEx();

	@OriginalMember(owner = "client!ac", name = "<init>", descriptor = "()V")
	public DirectSoundPcmDevice() throws Exception {
		for (@Pc(41) int i = 0; i < 2; i++) {
			this.bufferDescs[i] = new DSBufferDesc();
		}
		for (@Pc(57) int j = 0; j < 2; j++) {
			this.cursors[j] = new DSCursors();
		}
	}

	@OriginalMember(owner = "client!ac", name = "b", descriptor = "(BI)V")
	@Override
	public void discardBuffer(@OriginalArg(1) int index) {
		this.buffers[index].stop();
		this.playing[index] = false;
		this.buffers[index].setCurrentPosition(0);
		this.writePositions[index] = 0;
	}

	@OriginalMember(owner = "client!ac", name = "a", descriptor = "(IB)V")
	@Override
	public void close(@OriginalArg(0) int index) {
		if (this.buffers[index] == null) {
			return;
		}
		try {
			this.buffers[index].stop();
		} catch (@Pc(11) ComFailException ignored) {
		}
		this.buffers[index] = null;
	}

	@OriginalMember(owner = "client!ac", name = "a", descriptor = "(I[I)V")
	@Override
	public void write(@OriginalArg(0) int index, @OriginalArg(1) int[] samples) {
		@Pc(2) int count = samples.length;
		if (count != this.channels * 256) {
			throw new IllegalArgumentException();
		}
		@Pc(20) int offset = this.writePositions[index] * this.frameBytes;
		for (@Pc(22) int sampleIndex = 0; sampleIndex < count; sampleIndex++) {
			@Pc(27) int sample = samples[sampleIndex];
			if ((sample + 8388608 & 0xFF000000) != 0) {
				sample = sample >> 31 ^ 0x7FFFFF;
			}
			this.bytes[index][offset + sampleIndex * 2] = (byte) (sample >> 8);
			this.bytes[index][offset + sampleIndex * 2 + 1] = (byte) (sample >> 16);
		}
		this.buffers[index].writeBuffer(offset, count * 2, this.bytes[index], 0);
		this.writePositions[index] = this.writePositions[index] + count / this.channels & 0xFFFF;
		if (!this.playing[index]) {
			this.buffers[index].play(1);
			this.playing[index] = true;
		}
	}

	@OriginalMember(owner = "client!ac", name = "a", descriptor = "(BI)I")
	@Override
	public int position(@OriginalArg(1) int index) {
		if (!this.playing[index]) {
			return 0;
		}
		this.buffers[index].getCurrentPosition(this.cursors[index]);
		@Pc(29) int playPosition = this.cursors[index].write / this.frameBytes;
		@Pc(39) int queued = this.writePositions[index] - playPosition & 0xFFFF;
		if (this.bufferSizes[index] < queued) {
			for (@Pc(59) int gap = playPosition - this.writePositions[index] & 0xFFFF; gap > 0; gap -= 256) {
				this.write(index, this.silence);
			}
			queued = this.writePositions[index] - playPosition & 0xFFFF;
		}
		return queued;
	}

	@OriginalMember(owner = "client!ac", name = "a", descriptor = "(IZLjava/awt/Component;I)V")
	@Override
	public void init(@OriginalArg(0) int sampleRate, @OriginalArg(1) boolean stereo, @OriginalArg(2) java.awt.Component component) throws Exception {
		if (this.sampleRate != 0) {
			return;
		}
		if (sampleRate < 8000 || sampleRate > 48000) {
			throw new IllegalArgumentException();
		}
		this.channels = stereo ? 2 : 1;
		this.frameBytes = stereo ? 4 : 2;
		this.silence = new int[this.channels * 256];
		this.directSound.initialize(null);
		this.directSound.setCooperativeLevel(component, 2);
		for (@Pc(53) int index = 0; index < 2; index++) {
			this.bufferDescs[index].flags = 16384;
		}
		this.waveFormat.avgBytesPerSec = this.frameBytes * sampleRate;
		this.waveFormat.formatTag = 1;
		this.waveFormat.bitsPerSample = 16;
		this.waveFormat.blockAlign = this.frameBytes;
		this.waveFormat.channels = this.channels;
		this.sampleRate = sampleRate;
		this.waveFormat.samplesPerSec = sampleRate;
	}

	@OriginalMember(owner = "client!ac", name = "a", descriptor = "(IIB)V")
	@Override
	public void open(@OriginalArg(0) int bufferSize, @OriginalArg(1) int index) throws Exception {
		if (this.sampleRate == 0 || this.buffers[index] != null) {
			throw new IllegalStateException();
		}
		@Pc(16) int byteCount = this.frameBytes * 65536;
		if (this.bytes[index] == null || byteCount != this.bytes[index].length) {
			this.bytes[index] = new byte[byteCount];
			this.bufferDescs[index].bufferBytes = byteCount;
		}
		this.buffers[index] = this.directSound.createSoundBuffer(this.bufferDescs[index], this.waveFormat);
		this.playing[index] = false;
		this.writePositions[index] = 0;
		this.bufferSizes[index] = bufferSize;
	}
}
