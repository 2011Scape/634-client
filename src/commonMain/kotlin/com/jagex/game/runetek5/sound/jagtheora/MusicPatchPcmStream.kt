package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.sound.jagtheora.PcmStream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.sound.AreaSoundSampleMixer
import com.jagex.game.runetek5.sound.player.PcmPlayerManager
import kotlin.jvm.JvmField

@OriginalClass("client!bea")
class MusicPatchPcmStream : PcmStream {

	@OriginalMember(owner = "client!bea", name = "v", descriptor = "Lclient!uh;")
	@JvmField
	val queue: Deque = Deque()

	@OriginalMember(owner = "client!bea", name = "C", descriptor = "Lclient!sq;")
	@JvmField
	val fadeOutScheduler: PcmStreamMixer = PcmStreamMixer()

	@OriginalMember(owner = "client!bea", name = "z", descriptor = "Lclient!ma;")
	private val midiPcmStream: MidiPcmStream?

	@OriginalMember(owner = "client!bea", name = "<init>", descriptor = "(Lclient!ma;)V")
	constructor(@OriginalArg(0) mixBuss: MidiPcmStream?) : super() {
		this.midiPcmStream = mixBuss
	}

	@OriginalMember(owner = "client!bea", name = "b", descriptor = "()Lclient!jba;")
	override fun getNextStream(): PcmStream? {
		@Pc(9) var musicPatchNode: MusicPatchNode?
		do {
			musicPatchNode = this.queue.next() as MusicPatchNode?
			if (musicPatchNode == null) {
				return null
			}
		} while (musicPatchNode!!.stream == null)
		return musicPatchNode.stream
	}

	@OriginalMember(owner = "client!bea", name = "b", descriptor = "([III)V")
	override fun fill(@OriginalArg(0) mix: IntArray?, @OriginalArg(1) offset: Int, @OriginalArg(2) length: Int) {
		this.fadeOutScheduler.fill(mix, offset, length)
		@Pc(17) var voice: MusicPatchNode? = this.queue.first() as MusicPatchNode?
		while (voice != null) {
			if (!this.midiPcmStream!!.isPatchNodeDead(voice)) {
				@Pc(27) var cursor: Int = offset
				@Pc(29) var remaining: Int = length
				do {
					if (voice.tickSampleLength >= remaining) {
						this.fillPatchNode(cursor + remaining, voice, cursor, remaining, mix)
						voice.tickSampleLength -= remaining
						break
					}
					this.fillPatchNode(cursor + remaining, voice, cursor, voice.tickSampleLength, mix)
					cursor += voice.tickSampleLength
					remaining -= voice.tickSampleLength
				} while (!this.midiPcmStream.updatePatchNode(mix, remaining, voice, cursor))
			}
			voice = this.queue.next() as MusicPatchNode?
		}
	}

	@OriginalMember(owner = "client!bea", name = "a", descriptor = "(IBLclient!vv;)V")
	private fun skipPatchNode(@OriginalArg(0) amount: Int, @OriginalArg(2) musicPatchNode: MusicPatchNode?) {
		var amount: Int = amount
		if ((this.midiPcmStream!!.switches[musicPatchNode!!.currentTrack] and 0x4) != 0 && musicPatchNode.releasePhase < 0) {
			@Pc(23) val step: Int = this.midiPcmStream.effectSpeedScaled[musicPatchNode.currentTrack] / PcmPlayerManager.sampleRate
			@Pc(33) val loopCount: Int = (step + 1048575 - musicPatchNode.retriggerPhase) / step
			musicPatchNode.retriggerPhase = (step * amount + musicPatchNode.retriggerPhase) and 0xFFFFF
			if (amount >= loopCount) {
				if (this.midiPcmStream.startOffsets[musicPatchNode.currentTrack] == 0) {
					musicPatchNode.stream = AreaSoundSampleMixer.create(musicPatchNode.audioFile, musicPatchNode.stream!!.getAbsolutePlaybackRate(), musicPatchNode.stream!!.getTargetVolume(), musicPatchNode.stream!!.getPanPosition())
				} else {
					musicPatchNode.stream = AreaSoundSampleMixer.create(musicPatchNode.audioFile, musicPatchNode.stream!!.getAbsolutePlaybackRate(), 0, musicPatchNode.stream!!.getPanPosition())
					this.midiPcmStream.applyInitialSampleOffset(musicPatchNode.patch!!.pitchOffset!![musicPatchNode.currentNotePitch] < 0, musicPatchNode)
				}
				if (musicPatchNode.patch!!.pitchOffset!![musicPatchNode.currentNotePitch] < 0) {
					musicPatchNode.stream!!.setNumLoops(-1)
				}
				amount = musicPatchNode.retriggerPhase / step
			}
		}
		musicPatchNode.stream!!.skip(amount)
	}

	@OriginalMember(owner = "client!bea", name = "d", descriptor = "()I")
	override fun getActiveVoiceCount(): Int {
		return 0
	}

	@OriginalMember(owner = "client!bea", name = "a", descriptor = "(I)V")
	override fun skip(@OriginalArg(0) length: Int) {
		this.fadeOutScheduler.skip(length)
		@Pc(15) var voice: MusicPatchNode? = this.queue.first() as MusicPatchNode?
		while (voice != null) {
			if (!this.midiPcmStream!!.isPatchNodeDead(voice)) {
				@Pc(25) var remaining: Int = length
				do {
					if (remaining <= voice.tickSampleLength) {
						this.skipPatchNode(remaining, voice)
						voice.tickSampleLength -= remaining
						break
					}
					this.skipPatchNode(voice.tickSampleLength, voice)
					remaining -= voice.tickSampleLength
				} while (!this.midiPcmStream.updatePatchNode(null, remaining, voice, 0))
			}
			voice = this.queue.next() as MusicPatchNode?
		}
	}

	@OriginalMember(owner = "client!bea", name = "a", descriptor = "(ILclient!vv;IBI[I)V")
	private fun fillPatchNode(@OriginalArg(0) endOffset: Int, @OriginalArg(1) musicPatchNode: MusicPatchNode?, @OriginalArg(2) offset: Int, @OriginalArg(4) length: Int, @OriginalArg(5) mix: IntArray?) {
		var offset: Int = offset
		var length: Int = length
		if ((this.midiPcmStream!!.switches[musicPatchNode!!.currentTrack] and 0x4) != 0 && musicPatchNode.releasePhase < 0) {
			@Pc(35) val step: Int = this.midiPcmStream.effectSpeedScaled[musicPatchNode.currentTrack] / PcmPlayerManager.sampleRate
			while (true) {
				@Pc(45) val loopCount: Int = (step + 1048575 - musicPatchNode.retriggerPhase) / step
				if (length < loopCount) {
					musicPatchNode.retriggerPhase += length * step
					break
				}
				musicPatchNode.stream!!.fill(mix, offset, loopCount)
				offset += loopCount
				musicPatchNode.retriggerPhase += step * loopCount - 1048576
				length -= loopCount
				@Pc(80) var fadeLength: Int = PcmPlayerManager.sampleRate / 100
				@Pc(84) val maxFadeLength: Int = 262144 / step
				if (maxFadeLength < fadeLength) {
					fadeLength = maxFadeLength
				}
				@Pc(92) val soundStream: AreaSoundStream? = musicPatchNode.stream
				if (this.midiPcmStream.startOffsets[musicPatchNode.currentTrack] == 0) {
					musicPatchNode.stream = AreaSoundSampleMixer.create(musicPatchNode.audioFile, soundStream!!.getAbsolutePlaybackRate(), soundStream.getTargetVolume(), soundStream.getPanPosition())
				} else {
					musicPatchNode.stream = AreaSoundSampleMixer.create(musicPatchNode.audioFile, soundStream!!.getAbsolutePlaybackRate(), 0, soundStream.getPanPosition())
					this.midiPcmStream.applyInitialSampleOffset(musicPatchNode.patch!!.pitchOffset!![musicPatchNode.currentNotePitch] < 0, musicPatchNode)
					musicPatchNode.stream!!.fadeToVolume(fadeLength, soundStream.getTargetVolume())
				}
				if (musicPatchNode.patch!!.pitchOffset!![musicPatchNode.currentNotePitch] < 0) {
					musicPatchNode.stream!!.setNumLoops(-1)
				}
				soundStream.fadeOutAndStop(fadeLength)
				soundStream.fill(mix, offset, endOffset - offset)
				if (soundStream.isFading()) {
					this.fadeOutScheduler.addSubStream(soundStream)
				}
			}
		}
		musicPatchNode.stream!!.fill(mix, offset, length)
	}

	@OriginalMember(owner = "client!bea", name = "a", descriptor = "()Lclient!jba;")
	override fun getFirstStream(): PcmStream? {
		@Pc(9) val musicPatchNode: MusicPatchNode? = this.queue.first() as MusicPatchNode?
		if (musicPatchNode == null) {
			return null
		} else if (musicPatchNode.stream == null) {
			return this.getNextStream()
		} else {
			return musicPatchNode.stream
		}
	}
}
