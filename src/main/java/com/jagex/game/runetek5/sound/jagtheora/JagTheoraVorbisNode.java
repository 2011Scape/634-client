package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.cache.node.lru.MonoStereoData;
import com.jagex.game.runetek5.sound.jagtheora.VorbisPcmStream;
import com.jagex.game.runetek5.sound.Resampler;
import jagtheora.ogg.OggPacket;
import jagtheora.ogg.OggStreamState;
import jagtheora.vorbis.DSPState;
import jagtheora.vorbis.VorbisBlock;
import jagtheora.vorbis.VorbisComment;
import jagtheora.vorbis.VorbisInfo;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.sound.player.PcmPlayerManager;

@OriginalClass("client!jm")
public final class JagTheoraVorbisNode extends OggStreamDecoder {

	@OriginalMember(owner = "client!jm", name = "A", descriptor = "Lclient!jagtheora/vorbis/DSPState;")
	private DSPState dspState;

	@OriginalMember(owner = "client!jm", name = "B", descriptor = "Lclient!pe;")
	private Resampler resampler;

	@OriginalMember(owner = "client!jm", name = "C", descriptor = "Lclient!iha;")
	private VorbisPcmStream pcmStream;

	@OriginalMember(owner = "client!jm", name = "H", descriptor = "Lclient!jagtheora/vorbis/VorbisBlock;")
	private VorbisBlock vorbisBlock;

	@OriginalMember(owner = "client!jm", name = "I", descriptor = "I")
	private int totalSamplesRead;

	@OriginalMember(owner = "client!jm", name = "M", descriptor = "D")
	private double granuleTime;

	@OriginalMember(owner = "client!jm", name = "N", descriptor = "Lclient!jagtheora/vorbis/VorbisInfo;")
	private final VorbisInfo vorbisInfo = new VorbisInfo();

	@OriginalMember(owner = "client!jm", name = "J", descriptor = "Lclient!jagtheora/vorbis/VorbisComment;")
	private final VorbisComment vorbisComment = new VorbisComment();

	@OriginalMember(owner = "client!jm", name = "<init>", descriptor = "(Lclient!jagtheora/ogg/OggStreamState;)V")
	public JagTheoraVorbisNode(@OriginalArg(0) OggStreamState oggStreamState) {
		super(oggStreamState);
	}

	@OriginalMember(owner = "client!jm", name = "b", descriptor = "(I)I")
	public int getBufferedSampleCount() {
		return this.pcmStream == null ? 0 : this.pcmStream.size();
	}

	@OriginalMember(owner = "client!jm", name = "d", descriptor = "(I)Lclient!iha;")
	public VorbisPcmStream getPcmStream() {
		return this.pcmStream;
	}

	@OriginalMember(owner = "client!jm", name = "e", descriptor = "(B)D")
	public double getTime() {
		@Pc(6) double time = this.granuleTime;
		if (this.pcmStream != null) {
			time = this.pcmStream.getPlaybackTime();
			if (time < 0.0D) {
				time = this.granuleTime;
			}
		}
		return (double) -(256.0F / (float) PcmPlayerManager.sampleRate) + time;
	}

	@OriginalMember(owner = "client!jm", name = "a", descriptor = "(B)V")
	@Override
	public void stop() {
		if (this.vorbisBlock != null) {
			this.vorbisBlock.a();
		}
		if (this.dspState != null) {
			this.dspState.a();
		}
		this.vorbisComment.a();
		this.vorbisInfo.a();
		if (this.pcmStream != null) {
			this.pcmStream.finish();
		}
	}

	@OriginalMember(owner = "client!jm", name = "a", descriptor = "(BLclient!jagtheora/ogg/OggPacket;)V")
	@Override
	public void decode(@OriginalArg(1) OggPacket oggPacket) {
		if (super.packetCount < 3) {
			@Pc(18) int result = this.vorbisInfo.headerIn(this.vorbisComment, oggPacket);
			if (result < 0) {
				throw new IllegalStateException(String.valueOf(result));
			}
			if (super.packetCount == 2) {
				if (this.vorbisInfo.channels > 2 || this.vorbisInfo.channels < 1) {
					throw new RuntimeException(String.valueOf(this.vorbisInfo.channels));
				}
				this.dspState = new DSPState(this.vorbisInfo);
				this.vorbisBlock = new VorbisBlock(this.dspState);
				this.resampler = new Resampler(this.vorbisInfo.rate, PcmPlayerManager.sampleRate);
				this.pcmStream = new VorbisPcmStream(this.vorbisInfo.channels);
			}
			return;
		}
		if (this.vorbisBlock.synthesis(oggPacket) == 0) {
			this.dspState.blockIn(this.vorbisBlock);
		}
		@Pc(100) float[][] pcmData = this.dspState.pcmOut(this.vorbisInfo.channels);
		this.granuleTime = this.dspState.granuleTime();
		if (this.granuleTime == -1.0D) {
			this.granuleTime = (float) this.totalSamplesRead / (float) this.vorbisInfo.rate;
		}
		this.dspState.read(pcmData[0].length);
		this.totalSamplesRead += pcmData[0].length;
		@Pc(148) MonoStereoData sampleData = this.pcmStream.allocateSampleData(pcmData[0].length, this.granuleTime);
		JagTheoraVorbisNode.toShortPcm(sampleData.samples, pcmData);
		for (@Pc(156) int i = 0; i < this.vorbisInfo.channels; i++) {
			sampleData.samples[i] = this.resampler.resample(sampleData.samples[i]);
		}
		this.pcmStream.enqueueSampleData(sampleData);
	}
	@OriginalMember(owner = "client!efa", name = "a", descriptor = "(I[[S[[F)[[S")
	public static short[][] toShortPcm(@OriginalArg(1) short[][] output, @OriginalArg(2) float[][] input) {
		for (@Pc(8) int i = 0; i < input.length; i++) {
			for (@Pc(12) int j = 0; j < output[i].length; j++) {
				output[i][j] = (short) (input[i][j] * 16383.0F);
			}
		}
		return output;
	}
}
