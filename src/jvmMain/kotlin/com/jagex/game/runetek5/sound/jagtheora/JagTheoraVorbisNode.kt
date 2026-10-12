package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.cache.node.lru.MonoStereoData
import com.jagex.game.runetek5.sound.jagtheora.VorbisPcmStream
import com.jagex.game.runetek5.sound.Resampler
import jagtheora.ogg.OggPacket
import jagtheora.ogg.OggStreamState
import jagtheora.vorbis.DSPState
import jagtheora.vorbis.VorbisBlock
import jagtheora.vorbis.VorbisComment
import jagtheora.vorbis.VorbisInfo
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.sound.player.PcmPlayerManager

@OriginalClass("client!jm")
class JagTheoraVorbisNode : OggStreamDecoder {

	@OriginalMember(owner = "client!jm", name = "A", descriptor = "Lclient!jagtheora/vorbis/DSPState;")
	private var dspState: DSPState? = null

	@OriginalMember(owner = "client!jm", name = "B", descriptor = "Lclient!pe;")
	private var resampler: Resampler? = null

	@OriginalMember(owner = "client!jm", name = "C", descriptor = "Lclient!iha;")
	private var pcmStream: VorbisPcmStream? = null

	@OriginalMember(owner = "client!jm", name = "H", descriptor = "Lclient!jagtheora/vorbis/VorbisBlock;")
	private var vorbisBlock: VorbisBlock? = null

	@OriginalMember(owner = "client!jm", name = "I", descriptor = "I")
	private var totalSamplesRead: Int = 0

	@OriginalMember(owner = "client!jm", name = "M", descriptor = "D")
	private var granuleTime: Double = 0.0

	@OriginalMember(owner = "client!jm", name = "N", descriptor = "Lclient!jagtheora/vorbis/VorbisInfo;")
	private val vorbisInfo: VorbisInfo = VorbisInfo()

	@OriginalMember(owner = "client!jm", name = "J", descriptor = "Lclient!jagtheora/vorbis/VorbisComment;")
	private val vorbisComment: VorbisComment = VorbisComment()

	@OriginalMember(owner = "client!jm", name = "<init>", descriptor = "(Lclient!jagtheora/ogg/OggStreamState;)V")
	constructor(@OriginalArg(0) oggStreamState: OggStreamState?) : super(oggStreamState)

	@OriginalMember(owner = "client!jm", name = "b", descriptor = "(I)I")
	fun getBufferedSampleCount(): Int {
		return if (this.pcmStream == null) 0 else this.pcmStream!!.size()
	}

	@OriginalMember(owner = "client!jm", name = "d", descriptor = "(I)Lclient!iha;")
	fun getPcmStream(): VorbisPcmStream? {
		return this.pcmStream
	}

	@OriginalMember(owner = "client!jm", name = "e", descriptor = "(B)D")
	fun getTime(): Double {
		@Pc(6) var time: Double = this.granuleTime
		if (this.pcmStream != null) {
			time = this.pcmStream!!.getPlaybackTime()
			if (time < 0.0) {
				time = this.granuleTime
			}
		}
		return (-(256.0f / PcmPlayerManager.sampleRate.toFloat())).toDouble() + time
	}

	@OriginalMember(owner = "client!jm", name = "a", descriptor = "(B)V")
	override fun stop() {
		if (this.vorbisBlock != null) {
			this.vorbisBlock!!.a()
		}
		if (this.dspState != null) {
			this.dspState!!.a()
		}
		this.vorbisComment.a()
		this.vorbisInfo.a()
		if (this.pcmStream != null) {
			this.pcmStream!!.finish()
		}
	}

	@OriginalMember(owner = "client!jm", name = "a", descriptor = "(BLclient!jagtheora/ogg/OggPacket;)V")
	override fun decode(@OriginalArg(1) oggPacket: OggPacket?) {
		if (this.packetCount < 3) {
			@Pc(18) val result: Int = this.vorbisInfo.headerIn(this.vorbisComment, oggPacket)
			if (result < 0) {
				throw IllegalStateException(java.lang.String.valueOf(result))
			}
			if (this.packetCount == 2) {
				if (this.vorbisInfo.channels > 2 || this.vorbisInfo.channels < 1) {
					throw RuntimeException(java.lang.String.valueOf(this.vorbisInfo.channels))
				}
				this.dspState = DSPState(this.vorbisInfo)
				this.vorbisBlock = VorbisBlock(this.dspState)
				this.resampler = Resampler(this.vorbisInfo.rate, PcmPlayerManager.sampleRate)
				this.pcmStream = VorbisPcmStream(this.vorbisInfo.channels)
			}
			return
		}
		if (this.vorbisBlock!!.synthesis(oggPacket) == 0) {
			this.dspState!!.blockIn(this.vorbisBlock)
		}
		@Pc(100) val pcmData: Array<FloatArray?>? = this.dspState!!.pcmOut(this.vorbisInfo.channels)
		this.granuleTime = this.dspState!!.granuleTime()
		if (this.granuleTime == -1.0) {
			this.granuleTime = (this.totalSamplesRead.toFloat() / this.vorbisInfo.rate.toFloat()).toDouble()
		}
		this.dspState!!.read(pcmData!![0]!!.size)
		this.totalSamplesRead += pcmData[0]!!.size
		@Pc(148) val sampleData: MonoStereoData = this.pcmStream!!.allocateSampleData(pcmData[0]!!.size, this.granuleTime)
		JagTheoraVorbisNode.toShortPcm(sampleData.samples, pcmData)
		@Pc(156) var i: Int = 0
		while (i < this.vorbisInfo.channels) {
			sampleData.samples!![i] = this.resampler!!.resample(sampleData.samples[i])
			i++
		}
		this.pcmStream!!.enqueueSampleData(sampleData)
	}

	companion object {
		@OriginalMember(owner = "client!efa", name = "a", descriptor = "(I[[S[[F)[[S")
		@JvmStatic
		fun toShortPcm(@OriginalArg(1) output: Array<ShortArray?>?, @OriginalArg(2) input: Array<FloatArray?>?): Array<ShortArray?>? {
			for (@Pc(8) i in 0 until input!!.size) {
				for (@Pc(12) j in 0 until output!![i]!!.size) {
					output[i]!![j] = (input[i]!![j] * 16383.0f).toInt().toShort()
				}
			}
			return output
		}
	}
}
