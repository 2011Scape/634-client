package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.sound.jagtheora.PcmStream
import com.jagex.game.runetek5.sound.jagtheora.MusicPatchInfo
import com.jagex.game.runetek5.cache.node.MidiProgramNode
import com.jagex.game.runetek5.sound.jagtheora.MidiFileReader
import com.jagex.game.runetek5.sound.jagtheora.MusicTrack
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket
import com.jagex.game.runetek5.config.midi.SoundBankCache
import com.jagex.game.runetek5.sound.AreaSoundSampleMixer
import com.jagex.game.runetek5.sound.player.PcmPlayerManager
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.Synchronized
import kotlin.math.pow

@OriginalClass("client!ma")
class MidiPcmStream : PcmStream {

	@OriginalMember(owner = "client!ma", name = "Hb", descriptor = "I")
	private var prioritizedTrackLength: Int = 0

	@OriginalMember(owner = "client!ma", name = "Ib", descriptor = "J")
	private var nextEventTickTime: Long = 0L

	@OriginalMember(owner = "client!ma", name = "Kb", descriptor = "J")
	private var currentTickTime: Long = 0L

	@OriginalMember(owner = "client!ma", name = "Lb", descriptor = "I")
	private var prioritizedTrack: Int = 0

	@OriginalMember(owner = "client!ma", name = "Mb", descriptor = "Z")
	private var isLooping: Boolean = false

	@OriginalMember(owner = "client!ma", name = "Nb", descriptor = "I")
	private var queuedTrackTick: Int = 0

	@OriginalMember(owner = "client!ma", name = "Ob", descriptor = "Lclient!dl;")
	private var queuedTrack: MusicTrack? = null

	@OriginalMember(owner = "client!ma", name = "Pb", descriptor = "Z")
	private var queuedAllSoundOff: Boolean = false

	@OriginalMember(owner = "client!ma", name = "z", descriptor = "[I")
	private val expression: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "S", descriptor = "[[Lclient!vv;")
	private val loopGroupNotes: Array<Array<MusicPatchNode?>> = Array<Array<MusicPatchNode?>>(16) { arrayOfNulls<MusicPatchNode>(128) }

	@OriginalMember(owner = "client!ma", name = "H", descriptor = "[I")
	private val parameters: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "R", descriptor = "[I")
	@JvmField
	val startOffsets: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "J", descriptor = "[I")
	@JvmField
	val effectSpeedScaled: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "x", descriptor = "[I")
	private val pan: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "C", descriptor = "[I")
	private val modulation: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "cb", descriptor = "[I")
	private val channelVolume: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "F", descriptor = "[I")
	private val bendRanges: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "y", descriptor = "[I")
	@JvmField
	val switches: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "fb", descriptor = "[[Lclient!vv;")
	private val activeNotes: Array<Array<MusicPatchNode?>> = Array<Array<MusicPatchNode?>>(16) { arrayOfNulls<MusicPatchNode>(128) }

	@OriginalMember(owner = "client!ma", name = "ob", descriptor = "[I")
	private val pitchBend: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "qb", descriptor = "[I")
	private val portamento: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "Y", descriptor = "I")
	private val microsecondsPerSecond: Int = 1000000

	@OriginalMember(owner = "client!ma", name = "tb", descriptor = "[I")
	private val retriggerRates: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "wb", descriptor = "[I")
	private val defaultPrograms: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "T", descriptor = "[I")
	private val programChange: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "kb", descriptor = "I")
	private var musicVolume: Int = 256

	@OriginalMember(owner = "client!ma", name = "Eb", descriptor = "[I")
	private val volume: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "yb", descriptor = "[I")
	private val banks: IntArray = IntArray(16)

	@OriginalMember(owner = "client!ma", name = "vb", descriptor = "Lclient!rf;")
	private val fileReader: MidiFileReader = MidiFileReader()

	@OriginalMember(owner = "client!ma", name = "Jb", descriptor = "Lclient!bea;")
	private val patchStream: MusicPatchPcmStream = MusicPatchPcmStream(this)

	@OriginalMember(owner = "client!ma", name = "u", descriptor = "Lclient!eq;")
	private val musicPatches: IterableHashTable

	@OriginalMember(owner = "client!ma", name = "<init>", descriptor = "()V")
	constructor() : super() {
		this.musicPatches = IterableHashTable(128)
		this.setChannelVolume(256, -1)
		this.reset(true)
	}

	@OriginalMember(owner = "client!ma", name = "<init>", descriptor = "(Lclient!ma;)V")
	constructor(@OriginalArg(0) mixBuss: MidiPcmStream?) : super() {
		this.musicPatches = mixBuss!!.musicPatches
		this.setChannelVolume(256, -1)
		this.reset(true)
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "()Lclient!jba;")
	@Synchronized
	override fun getNextStream(): PcmStream? {
		return null
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(ZB)V")
	@Synchronized
	private fun stop(@OriginalArg(0) allSoundOff: Boolean) {
		this.fileReader.clear()
		this.queuedTrack = null
		this.reset(allSoundOff)
	}

	@OriginalMember(owner = "client!ma", name = "c", descriptor = "(III)V")
	@Synchronized
	fun initialize() {
		this.initDrumChannel()
	}

	@OriginalMember(owner = "client!ma", name = "c", descriptor = "(II)V")
	private fun handleEvent(@OriginalArg(1) event: Int) {
		@Pc(9) var status: Int = event and 0xF0
		@Pc(16) val channel: Int
		@Pc(22) val data1: Int
		@Pc(28) val data2: Int
		if (status == 128) {
			channel = event and 0xF
			data1 = (event shr 8) and 0x7F
			data2 = (event shr 16) and 0x7F
			this.noteOff(data2, data1, channel)
		} else if (status == 144) {
			channel = event and 0xF
			data1 = (event shr 8) and 0x7F
			data2 = (event shr 16) and 0x7F
			if (data2 <= 0) {
				this.noteOff(64, data1, channel)
			} else {
				this.noteOn(data2, data1, channel)
			}
		} else if (status == 160) {
			channel = event and 0xF
			data1 = (event shr 8) and 0x7F
			data2 = (event shr 16) and 0x7F
			this.keyPressure(data2, channel, data1)
		} else if (status == 176) {
			channel = event and 0xF
			data1 = (event shr 8) and 0x7F
			data2 = (event shr 16) and 0x7F
			if (data1 == 0) {
				this.banks[channel] = (data2 shl 14) + (this.banks[channel] and 0xFFE03FFF.toInt())
			}
			if (data1 == 32) {
				this.banks[channel] = (data2 shl 7) + (this.banks[channel] and 0xFFFFC07F.toInt())
			}
			if (data1 == 1) {
				this.modulation[channel] = (this.modulation[channel] and 0xFFFFC07F.toInt()) + (data2 shl 7)
			}
			if (data1 == 33) {
				this.modulation[channel] = (this.modulation[channel] and 0xFFFFFF80.toInt()) + data2
			}
			if (data1 == 5) {
				this.portamento[channel] = (this.portamento[channel] and 0xFFFFC07F.toInt()) + (data2 shl 7)
			}
			if (data1 == 37) {
				this.portamento[channel] = data2 + (this.portamento[channel] and 0xFFFFFF80.toInt())
			}
			if (data1 == 7) {
				this.volume[channel] = (data2 shl 7) + (this.volume[channel] and 0xFFFFC07F.toInt())
			}
			if (data1 == 39) {
				this.volume[channel] = data2 + (this.volume[channel] and 0xFFFFFF80.toInt())
			}
			if (data1 == 10) {
				this.pan[channel] = (data2 shl 7) + (this.pan[channel] and 0xFFFFC07F.toInt())
			}
			if (data1 == 42) {
				this.pan[channel] = data2 + (this.pan[channel] and 0xFFFFFF80.toInt())
			}
			if (data1 == 11) {
				this.expression[channel] = (data2 shl 7) + (this.expression[channel] and 0xFFFFC07F.toInt())
			}
			if (data1 == 43) {
				this.expression[channel] = (this.expression[channel] and 0xFFFFFF80.toInt()) + data2
			}
			if (data1 == 64) {
				if (data2 < 64) {
					this.switches[channel] = this.switches[channel] and 0xFFFFFFFE.toInt()
				} else {
					this.switches[channel] = this.switches[channel] or 0x1
				}
			}
			if (data1 == 65) {
				if (data2 >= 64) {
					this.switches[channel] = this.switches[channel] or 0x2
				} else {
					this.releaseUnheldVoices(channel)
					this.switches[channel] = this.switches[channel] and 0xFFFFFFFD.toInt()
				}
			}
			if (data1 == 99) {
				this.parameters[channel] = (data2 shl 7) + (this.parameters[channel] and 0x7F)
			}
			if (data1 == 98) {
				this.parameters[channel] = (this.parameters[channel] and 0x3F80) + data2
			}
			if (data1 == 101) {
				this.parameters[channel] = (this.parameters[channel] and 0x7F) + (data2 shl 7) + 16384
			}
			if (data1 == 100) {
				this.parameters[channel] = data2 + (this.parameters[channel] and 0x3F80) + 16384
			}
			if (data1 == 120) {
				this.stopVoices(channel)
			}
			if (data1 == 121) {
				this.resetChannel(channel)
			}
			if (data1 == 123) {
				this.releaseVoices(channel)
			}
			@Pc(504) var parameter: Int
			if (data1 == 6) {
				parameter = this.parameters[channel]
				if (parameter == 16384) {
					this.bendRanges[channel] = (data2 shl 7) + (this.bendRanges[channel] and 0xFFFFC07F.toInt())
				}
			}
			if (data1 == 38) {
				parameter = this.parameters[channel]
				if (parameter == 16384) {
					this.bendRanges[channel] = data2 + (this.bendRanges[channel] and 0xFFFFFF80.toInt())
				}
			}
			if (data1 == 16) {
				this.startOffsets[channel] = (this.startOffsets[channel] and 0xFFFFC07F.toInt()) + (data2 shl 7)
			}
			if (data1 == 48) {
				this.startOffsets[channel] = (this.startOffsets[channel] and 0xFFFFFF80.toInt()) + data2
			}
			if (data1 == 81) {
				if (data2 >= 64) {
					this.switches[channel] = this.switches[channel] or 0x4
				} else {
					this.resetEffectSwitch(channel)
					this.switches[channel] = this.switches[channel] and 0xFFFFFFFB.toInt()
				}
			}
			if (data1 == 17) {
				this.setRetriggerRate(channel, (data2 shl 7) + (this.retriggerRates[channel] and 0xFFFFC07F.toInt()))
			}
			if (data1 == 49) {
				this.setRetriggerRate(channel, data2 + (this.retriggerRates[channel] and 0xFFFFFF80.toInt()))
			}
		} else if (status == 192) {
			channel = event and 0xF
			data1 = (event shr 8) and 0x7F
			this.setProgram(this.banks[channel] + data1, channel)
		} else if (status == 208) {
			channel = event and 0xF
			data1 = (event shr 8) and 0x7F
			this.channelPressure(channel, data1)
		} else if (status == 224) {
			channel = event and 0xF
			data1 = ((event shr 8) and 0x7F) + ((event and 0x7F0064) shr 9)
			this.setPitchBend(channel, data1)
		} else {
			status = event and 0xFF
			if (status == 255) {
				this.reset(true)
			}
		}
	}

	@OriginalMember(owner = "client!ma", name = "d", descriptor = "(III)V")
	@Synchronized
	fun setChannelVolume(@OriginalArg(0) volume: Int, @OriginalArg(1) channel: Int) {
		if (channel < 0) {
			for (@Pc(5) i in 0 until 16) {
				this.channelVolume[i] = volume
			}
		} else {
			this.channelVolume[channel] = volume
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(B)I")
	fun getVolume(): Int {
		return this.musicVolume
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(ILclient!vv;)I")
	private fun calculatePitch(@OriginalArg(1) musicPatchNode: MusicPatchNode?): Int {
		@Pc(20) var pitch: Int = musicPatchNode!!.notePitchAdjustment + ((musicPatchNode.portamentoPitchOffset * musicPatchNode.portamentoFactor) shr 12)
		pitch += ((this.pitchBend[musicPatchNode.currentTrack] - 8192) * this.bendRanges[musicPatchNode.currentTrack]) shr 12
		@Pc(41) val musicPatchEnvelope: MusicPatchInfo? = musicPatchNode.musicPatchInfo
		@Pc(61) val local61: Int
		if (musicPatchEnvelope!!.volumeEnvelopeRelease > 0 && (musicPatchEnvelope.vibratoLfoPitch > 0 || this.modulation[musicPatchNode.currentTrack] > 0)) {
			var vibratoDepth: Int = musicPatchEnvelope.vibratoLfoPitch shl 2
			@Pc(66) val vibratoDelay: Int = musicPatchEnvelope.vibratoDelay shl 1
			if (musicPatchNode.tickCounter < vibratoDelay) {
				vibratoDepth = vibratoDepth * musicPatchNode.tickCounter / vibratoDelay
			}
			vibratoDepth += this.modulation[musicPatchNode.currentTrack] shr 7
			@Pc(100) val vibratoWave: Double = kotlin.math.sin((musicPatchNode.vibratoPhase and 0x1FF).toDouble() * 0.01227184630308513)
			pitch += (vibratoWave * vibratoDepth.toDouble()).toInt()
		}
		val playbackRate: Int = ((musicPatchNode.audioFile!!.sampleRate * 256).toDouble() * 2.0.pow(pitch.toDouble() * 3.255208333333333E-4) / PcmPlayerManager.sampleRate.toDouble() + 0.5).toInt()
		return if (playbackRate < 1) 1 else playbackRate
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(ZZLclient!dl;Z)V")
	@Synchronized
	private fun loadAndPlayTrack(@OriginalArg(1) allSoundOff: Boolean, @OriginalArg(2) midiSong: MusicTrack?, @OriginalArg(3) isLooping: Boolean) {
		this.stop(allSoundOff)
		this.fileReader.decode(midiSong!!.midi)
		this.currentTickTime = 0L
		this.isLooping = isLooping
		@Pc(24) val trackCount: Int = this.fileReader.getTrackCount()
		for (@Pc(26) track in 0 until trackCount) {
			this.fileReader.getGotoTrack(track)
			this.fileReader.getTrackLength(track)
			this.fileReader.markTracePosition(track)
		}
		this.prioritizedTrack = this.fileReader.getPrioritizedTrack()
		this.prioritizedTrackLength = this.fileReader.trackLengths!![this.prioritizedTrack]
		this.nextEventTickTime = this.fileReader.getTickTimestamp(this.prioritizedTrackLength)
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "(ILclient!vv;)Z")
	fun isPatchNodeDead(@OriginalArg(1) musicPatchNode: MusicPatchNode?): Boolean {
		if (musicPatchNode!!.stream != null) {
			return false
		}
		if (musicPatchNode.releasePhase >= 0) {
			musicPatchNode.unlink()
			if (musicPatchNode.loopVariable > 0 && musicPatchNode === this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable]) {
				this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable] = null
			}
		}
		return true
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(Lclient!vv;B)I")
	private fun calculatePan(@OriginalArg(0) musicPatchNode: MusicPatchNode?): Int {
		@Pc(15) val channelPan: Int = this.pan[musicPatchNode!!.currentTrack]
		return if (channelPan >= 8192) 16384 - (((128 - musicPatchNode.currentPanValue) * (16384 - channelPan) + 32) shr 6) else (musicPatchNode.currentPanValue * channelPan - -32) shr 6
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(IIBI)V")
	private fun noteOff(@OriginalArg(0) velocity: Int, @OriginalArg(1) note: Int, @OriginalArg(3) channel: Int) {
		@Pc(12) val voice: MusicPatchNode? = this.activeNotes[channel][note]
		if (voice == null) {
			return
		}
		this.activeNotes[channel][note] = null
		if ((this.switches[channel] and 0x2) == 0) {
			voice.releasePhase = 0
			return
		}
		@Pc(47) var other: MusicPatchNode? = this.patchStream.queue.first() as MusicPatchNode?
		while (other != null) {
			if (other.currentTrack == voice.currentTrack && other.releasePhase < 0 && voice !== other) {
				voice.releasePhase = 0
				return
			}
			other = this.patchStream.queue.next() as MusicPatchNode?
		}
	}

	@OriginalMember(owner = "client!ma", name = "d", descriptor = "(II)V")
	private fun resetEffectSwitch(@OriginalArg(0) channel: Int) {
		if ((this.switches[channel] and 0x4) == 0) {
			return
		}
		@Pc(18) var musicPatchNode: MusicPatchNode? = this.patchStream.queue.first() as MusicPatchNode?
		while (musicPatchNode != null) {
			if (channel == musicPatchNode.currentTrack) {
				musicPatchNode.retriggerPhase = 0
			}
			musicPatchNode = this.patchStream.queue.next() as MusicPatchNode?
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(ZZ)V")
	private fun reset(@OriginalArg(1) allSoundOff: Boolean) {
		if (allSoundOff) {
			this.stopVoices(-1)
		} else {
			this.releaseVoices(-1)
		}
		this.resetChannel(-1)
		for (@Pc(29) channel in 0 until 16) {
			this.programChange[channel] = this.defaultPrograms[channel]
		}
		for (@Pc(47) i in 0 until 16) {
			this.banks[i] = this.defaultPrograms[i] and 0xFFFFFF80.toInt()
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "([IILclient!vv;IB)Z")
	fun updatePatchNode(@OriginalArg(0) mix: IntArray?, @OriginalArg(1) length: Int, @OriginalArg(2) musicPatchNode: MusicPatchNode?, @OriginalArg(3) offset: Int): Boolean {
		musicPatchNode!!.tickSampleLength = PcmPlayerManager.sampleRate / 100
		if (musicPatchNode.releasePhase >= 0 && (musicPatchNode.stream == null || musicPatchNode.stream!!.isFinished())) {
			musicPatchNode.clear()
			musicPatchNode.unlink()
			if (musicPatchNode.loopVariable > 0 && musicPatchNode === this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable]) {
				this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable] = null
			}
			return true
		}
		@Pc(53) var portamentoFactor: Int = musicPatchNode.portamentoFactor
		if (portamentoFactor > 0) {
			portamentoFactor -= (2.0.pow(this.portamento[musicPatchNode.currentTrack].toDouble() * 4.921259842519685E-4) * 16.0 + 0.5).toInt()
			if (portamentoFactor < 0) {
				portamentoFactor = 0
			}
			musicPatchNode.portamentoFactor = portamentoFactor
		}
		musicPatchNode.stream!!.setPlaybackRate(this.calculatePitch(musicPatchNode))
		@Pc(101) val musicPatchEnvelope: MusicPatchInfo? = musicPatchNode.musicPatchInfo
		@Pc(103) var isDead: Boolean = false
		musicPatchNode.vibratoPhase += musicPatchEnvelope!!.volumeEnvelopeRelease
		musicPatchNode.tickCounter++
		@Pc(135) val pitchScalar: Double = (((musicPatchNode.currentNotePitch - 60) shl 8) + ((musicPatchNode.portamentoPitchOffset * musicPatchNode.portamentoFactor) shr 12)).toDouble() * 5.086263020833333E-6
		if (musicPatchEnvelope.volumeEnvelopeDecay > 0) {
			if (musicPatchEnvelope.decayKeyScaling <= 0) {
				musicPatchNode.volumeEnvelopeTime += 128
			} else {
				musicPatchNode.volumeEnvelopeTime += (2.0.pow(musicPatchEnvelope.decayKeyScaling.toDouble() * pitchScalar) * 128.0 + 0.5).toInt()
			}
			if (musicPatchEnvelope.volumeEnvelopeDecay * musicPatchNode.volumeEnvelopeTime >= 819200) {
				isDead = true
			}
		}
		if (musicPatchEnvelope.volumeEnvelopeData != null) {
			if (musicPatchEnvelope.volumeEnvelopeKeyScaling > 0) {
				musicPatchNode.volumeEnvelopePosition += (2.0.pow(pitchScalar * musicPatchEnvelope.volumeEnvelopeKeyScaling.toDouble()) * 128.0 + 0.5).toInt()
			} else {
				musicPatchNode.volumeEnvelopePosition += 128
			}
			while (musicPatchEnvelope.volumeEnvelopeData!!.size - 2 > musicPatchNode.volumeEnvelopeIndex && musicPatchNode.volumeEnvelopePosition > ((musicPatchEnvelope.volumeEnvelopeData!![musicPatchNode.volumeEnvelopeIndex + 2].toInt() and 0xFF) shl 8)) {
				musicPatchNode.volumeEnvelopeIndex += 2
			}
			if (musicPatchNode.volumeEnvelopeIndex == musicPatchEnvelope.volumeEnvelopeData!!.size - 2 && musicPatchEnvelope.volumeEnvelopeData!![musicPatchNode.volumeEnvelopeIndex + 1].toInt() == 0) {
				isDead = true
			}
		}
		if (musicPatchNode.releasePhase >= 0 && musicPatchEnvelope.releaseEnvelopeData != null && (this.switches[musicPatchNode.currentTrack] and 0x1) == 0 && (musicPatchNode.loopVariable < 0 || this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable] !== musicPatchNode)) {
			if (musicPatchEnvelope.releaseEnvelopeKeyScaling > 0) {
				musicPatchNode.releasePhase += (2.0.pow(musicPatchEnvelope.releaseEnvelopeKeyScaling.toDouble() * pitchScalar) * 128.0 + 0.5).toInt()
			} else {
				musicPatchNode.releasePhase += 128
			}
			while (musicPatchEnvelope.releaseEnvelopeData!!.size - 2 > musicPatchNode.releaseEnvelopeIndex && musicPatchNode.releasePhase > ((musicPatchEnvelope.releaseEnvelopeData!![musicPatchNode.releaseEnvelopeIndex + 2].toInt() and 0xFF) shl 8)) {
				musicPatchNode.releaseEnvelopeIndex += 2
			}
			if (musicPatchEnvelope.releaseEnvelopeData!!.size - 2 == musicPatchNode.releaseEnvelopeIndex) {
				isDead = true
			}
		}
		if (!isDead) {
			musicPatchNode.stream!!.fadeToVolumeAndPan(musicPatchNode.tickSampleLength, this.calculateVolume(musicPatchNode), this.calculatePan(musicPatchNode))
			return false
		}
		musicPatchNode.stream!!.fadeOutAndStop(musicPatchNode.tickSampleLength)
		if (mix == null) {
			musicPatchNode.stream!!.skip(length)
		} else {
			musicPatchNode.stream!!.fill(mix, offset, length)
		}
		if (musicPatchNode.stream!!.isFading()) {
			this.patchStream.fadeOutScheduler.addSubStream(musicPatchNode.stream)
		}
		musicPatchNode.clear()
		if (musicPatchNode.releasePhase >= 0) {
			musicPatchNode.unlink()
			if (musicPatchNode.loopVariable > 0 && musicPatchNode === this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable]) {
				this.loopGroupNotes[musicPatchNode.currentTrack][musicPatchNode.loopVariable] = null
			}
		}
		return true
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(IIII)V")
	private fun noteOn(@OriginalArg(1) key: Int, @OriginalArg(2) velocity: Int, @OriginalArg(3) currentTrack: Int) {
		this.noteOff(64, velocity, currentTrack)
		if ((this.switches[currentTrack] and 0x2) != 0) {
			@Pc(30) var held: MusicPatchNode? = this.patchStream.queue.last() as MusicPatchNode?
			while (held != null) {
				if (currentTrack == held.currentTrack && held.releasePhase < 0) {
					this.activeNotes[currentTrack][held.currentNotePitch] = null
					this.activeNotes[currentTrack][velocity] = held
					@Pc(72) val pitch: Int = ((held.portamentoFactor * held.portamentoPitchOffset) shr 12) + held.notePitchAdjustment
					held.notePitchAdjustment += (velocity - held.currentNotePitch) shl 8
					held.portamentoPitchOffset = pitch - held.notePitchAdjustment
					held.currentNotePitch = velocity
					held.portamentoFactor = 4096
					return
				}
				held = this.patchStream.queue.previous() as MusicPatchNode?
			}
		}
		@Pc(118) val patch: MusicPatch? = this.musicPatches.get(this.programChange[currentTrack].toLong()) as MusicPatch?
		if (patch == null) {
			return
		}
		@Pc(126) val sound: VariableRateSoundPacket? = patch.audioFiles!![velocity]
		if (sound == null) {
			return
		}
		@Pc(133) val voice: MusicPatchNode = MusicPatchNode()
		voice.currentTrack = currentTrack
		voice.patch = patch
		voice.audioFile = sound
		voice.musicPatchInfo = patch.musicPatchNodes2!![velocity]
		voice.loopVariable = patch.loopMode!![velocity].toInt()
		voice.currentNotePitch = velocity
		voice.maxVolumeLevel = (patch.volumeOffset!![velocity] * patch.baseVelocity * key * key + 1024) shr 11
		voice.currentPanValue = patch.panOffset!![velocity].toInt() and 0xFF
		voice.notePitchAdjustment = (velocity shl 8) - (patch.pitchOffset!![velocity].toInt() and 0x7FFF)
		voice.releasePhase = -1
		voice.volumeEnvelopePosition = 0
		voice.volumeEnvelopeIndex = 0
		voice.volumeEnvelopeTime = 0
		voice.releaseEnvelopeIndex = 0
		if (this.startOffsets[currentTrack] == 0) {
			voice.stream = AreaSoundSampleMixer.create(sound, this.calculatePitch(voice), this.calculateVolume(voice), this.calculatePan(voice))
		} else {
			voice.stream = AreaSoundSampleMixer.create(sound, this.calculatePitch(voice), 0, this.calculatePan(voice))
			this.applyInitialSampleOffset(patch.pitchOffset!![velocity] < 0, voice)
		}
		if (patch.pitchOffset!![velocity] < 0) {
			voice.stream!!.setNumLoops(-1)
		}
		if (voice.loopVariable >= 0) {
			@Pc(288) val previous: MusicPatchNode? = this.loopGroupNotes[currentTrack][voice.loopVariable]
			if (previous != null && previous.releasePhase < 0) {
				this.activeNotes[currentTrack][previous.currentNotePitch] = null
				previous.releasePhase = 0
			}
			this.loopGroupNotes[currentTrack][voice.loopVariable] = voice
		}
		this.patchStream.queue.addLast(voice)
		this.activeNotes[currentTrack][velocity] = voice
	}

	@OriginalMember(owner = "client!ma", name = "e", descriptor = "(III)V")
	private fun initDrumChannel() {
		this.defaultPrograms[9] = 128
		this.banks[9] = 128
		this.setProgram(128, 9)
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(IB)V")
	private fun releaseVoices(@OriginalArg(0) channel: Int) {
		@Pc(16) var voice: MusicPatchNode? = this.patchStream.queue.first() as MusicPatchNode?
		while (voice != null) {
			if ((channel < 0 || voice.currentTrack == channel) && voice.releasePhase < 0) {
				this.activeNotes[voice.currentTrack][voice.currentNotePitch] = null
				voice.releasePhase = 0
			}
			voice = this.patchStream.queue.next() as MusicPatchNode?
		}
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "(B)V")
	private fun processNextMidiEvents() {
		@Pc(8) var track: Int = this.prioritizedTrack
		@Pc(11) var tick: Int = this.prioritizedTrackLength
		@Pc(14) var time: Long = this.nextEventTickTime
		if (this.queuedTrack != null && this.queuedTrackTick == tick) {
			this.loadAndPlayTrack(this.queuedAllSoundOff, this.queuedTrack, this.isLooping)
			this.processNextMidiEvents()
			return
		}
		while (this.prioritizedTrackLength == tick) {
			while (this.fileReader.trackLengths!![track] == tick) {
				this.fileReader.getGotoTrack(track)
				@Pc(43) val event: Int = this.fileReader.readMessage(track)
				if (event == 1) {
					this.fileReader.setTrackDone()
					this.fileReader.markTracePosition(track)
					if (this.fileReader.isDone()) {
						if (this.queuedTrack != null) {
							this.setMusicTrack(this.queuedTrack, this.isLooping)
							this.processNextMidiEvents()
							return
						}
						if (!this.isLooping || tick == 0) {
							this.reset(true)
							this.fileReader.clear()
							return
						}
						this.fileReader.resetTracksFromTimestamp(time)
					}
					break
				}
				if ((event and 0x80) != 0) {
					this.handleEvent(event)
				}
				this.fileReader.getTrackLength(track)
				this.fileReader.markTracePosition(track)
			}
			track = this.fileReader.getPrioritizedTrack()
			tick = this.fileReader.trackLengths!![track]
			time = this.fileReader.getTickTimestamp(tick)
		}
		this.nextEventTickTime = time
		this.prioritizedTrackLength = tick
		this.prioritizedTrack = track
		if (this.queuedTrack != null && tick > this.queuedTrackTick) {
			this.prioritizedTrack = -1
			this.prioritizedTrackLength = this.queuedTrackTick
			this.nextEventTickTime = this.fileReader.getTickTimestamp(this.prioritizedTrackLength)
		}
	}

	@OriginalMember(owner = "client!ma", name = "f", descriptor = "(III)V")
	private fun setRetriggerRate(@OriginalArg(0) rate: Int, @OriginalArg(2) rawRate: Int) {
		this.retriggerRates[rate] = rawRate
		this.effectSpeedScaled[rate] = (2.0.pow(rawRate.toDouble() * 5.4931640625E-4) * 2097152.0 + 0.5).toInt()
	}

	@OriginalMember(owner = "client!ma", name = "e", descriptor = "(II)V")
	private fun releaseUnheldVoices(@OriginalArg(1) channel: Int) {
		if ((this.switches[channel] and 0x2) == 0) {
			return
		}
		@Pc(28) var voice: MusicPatchNode? = this.patchStream.queue.first() as MusicPatchNode?
		while (voice != null) {
			if (channel == voice.currentTrack && this.activeNotes[channel][voice.currentNotePitch] == null && voice.releasePhase < 0) {
				voice.releasePhase = 0
			}
			voice = this.patchStream.queue.next() as MusicPatchNode?
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(I)V")
	@Synchronized
	override fun skip(@OriginalArg(0) length: Int) {
		var length: Int = length
		if (this.fileReader.dataLoaded()) {
			@Pc(18) val timePerSample: Int = this.fileReader.division * this.microsecondsPerSecond / PcmPlayerManager.sampleRate
			do {
				@Pc(27) val time: Long = this.currentTickTime + timePerSample.toLong() * length.toLong()
				if (this.nextEventTickTime - time >= 0L) {
					this.currentTickTime = time
					break
				}
				@Pc(56) val count: Int = ((this.nextEventTickTime + timePerSample.toLong() - this.currentTickTime - 1L) / timePerSample.toLong()).toInt()
				this.currentTickTime += count.toLong() * timePerSample.toLong()
				this.patchStream.skip(count)
				length -= count
				this.processNextMidiEvents()
			} while (this.fileReader.dataLoaded())
		}
		this.patchStream.skip(length)
	}

	@OriginalMember(owner = "client!ma", name = "g", descriptor = "(III)V")
	private fun channelPressure(@OriginalArg(0) channel: Int, @OriginalArg(1) pressure: Int) {
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "(I)Z")
	@Synchronized
	fun isPlaying(): Boolean {
		return this.fileReader.dataLoaded()
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "([III)V")
	@Synchronized
	override fun fill(@OriginalArg(0) mix: IntArray?, @OriginalArg(1) offset: Int, @OriginalArg(2) length: Int) {
		var offset: Int = offset
		var length: Int = length
		if (this.fileReader.dataLoaded()) {
			@Pc(18) val timePerSample: Int = this.microsecondsPerSecond * this.fileReader.division / PcmPlayerManager.sampleRate
			do {
				@Pc(27) val time: Long = this.currentTickTime + length.toLong() * timePerSample.toLong()
				if (this.nextEventTickTime - time >= 0L) {
					this.currentTickTime = time
					break
				}
				@Pc(56) val count: Int = ((timePerSample.toLong() + this.nextEventTickTime - this.currentTickTime - 1L) / timePerSample.toLong()).toInt()
				this.currentTickTime += timePerSample.toLong() * count.toLong()
				this.patchStream.fill(mix, offset, count)
				offset += count
				length -= count
				this.processNextMidiEvents()
			} while (this.fileReader.dataLoaded())
		}
		this.patchStream.fill(mix, offset, length)
	}

	@OriginalMember(owner = "client!ma", name = "f", descriptor = "(II)V")
	@Synchronized
	fun setVolume(@OriginalArg(1) musicVolume: Int) {
		this.musicVolume = musicVolume
	}

	@OriginalMember(owner = "client!ma", name = "b", descriptor = "(IIII)V")
	private fun keyPressure(@OriginalArg(0) pressure: Int, @OriginalArg(2) key: Int, @OriginalArg(3) note: Int) {
	}

	@OriginalMember(owner = "client!ma", name = "e", descriptor = "(B)V")
	@Synchronized
	fun resetMusicPatches() {
		@Pc(17) var musicPatch: MusicPatch? = this.musicPatches.first() as MusicPatch?
		while (musicPatch != null) {
			musicPatch.clearSoundIds()
			musicPatch = this.musicPatches.next() as MusicPatch?
		}
	}

	@OriginalMember(owner = "client!ma", name = "g", descriptor = "(II)V")
	private fun resetChannel(@OriginalArg(1) channel: Int) {
		if (channel < 0) {
			for (@Pc(9) c in 0 until 16) {
				this.resetChannel(c)
			}
			return
		}
		this.volume[channel] = 12800
		this.pan[channel] = 8192
		this.expression[channel] = 16383
		this.pitchBend[channel] = 8192
		this.modulation[channel] = 0
		this.portamento[channel] = 8192
		this.releaseUnheldVoices(channel)
		this.resetEffectSwitch(channel)
		this.switches[channel] = 0
		this.parameters[channel] = 32767
		this.bendRanges[channel] = 256
		this.startOffsets[channel] = 0
		this.setRetriggerRate(channel, 8192)
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(Lclient!dl;ILclient!hs;Lclient!in;I)Z")
	@Synchronized
	fun ready(@OriginalArg(0) midiSong: MusicTrack?, @OriginalArg(2) soundCache: SoundBankCache?, @OriginalArg(3) resource: Js5ResourceProvider?): Boolean {
		midiSong!!.buildPatchTable()
		@Pc(9) var isReady: Boolean = true
		@Pc(20) val sampleRates: IntArray = intArrayOf(22050)
		@Pc(26) var midiProgramNode: MidiProgramNode? = midiSong.table!!.first() as MidiProgramNode?
		while (midiProgramNode != null) {
			@Pc(32) val patchId: Int = midiProgramNode.key.toInt()
			@Pc(40) var musicPatch: MusicPatch? = this.musicPatches.get(patchId.toLong()) as MusicPatch?
			if (musicPatch == null) {
				musicPatch = MidiPcmStream.loadMusicPatch(resource, patchId)
				if (musicPatch == null) {
					isReady = false
					midiProgramNode = midiSong.table!!.next() as MidiProgramNode?
					continue
				}
				this.musicPatches.put(patchId.toLong(), musicPatch)
			}
			if (!musicPatch.ready(soundCache, sampleRates, midiProgramNode.notes)) {
				isReady = false
			}
			midiProgramNode = midiSong.table!!.next() as MidiProgramNode?
		}
		if (isReady) {
			midiSong.clear()
		}
		return isReady
	}

	@OriginalMember(owner = "client!ma", name = "d", descriptor = "(I)V")
	@Synchronized
	fun unlinkMusicPatches() {
		@Pc(15) var musicPatch: MusicPatch? = this.musicPatches.first() as MusicPatch?
		while (musicPatch != null) {
			musicPatch.unlink()
			musicPatch = this.musicPatches.next() as MusicPatch?
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(Lclient!vv;I)I")
	private fun calculateVolume(@OriginalArg(0) musicPatchNode: MusicPatchNode?): Int {
		if (this.channelVolume[musicPatchNode!!.currentTrack] == 0) {
			return 0
		}
		@Pc(21) val musicPatchEnvelope: MusicPatchInfo? = musicPatchNode.musicPatchInfo
		@Pc(37) var gain: Int = (this.volume[musicPatchNode.currentTrack] * this.expression[musicPatchNode.currentTrack] + 4096) shr 13
		@Pc(45) val gainSquared: Int = (gain * gain + 16384) shr 15
		@Pc(54) val noteGain: Int = (gainSquared * musicPatchNode.maxVolumeLevel + 16384) shr 15
		@Pc(63) val scaledGain: Int = (noteGain * this.musicVolume + 128) shr 8
		gain = (this.channelVolume[musicPatchNode.currentTrack] * scaledGain + 128) shr 8
		if (musicPatchEnvelope!!.volumeEnvelopeDecay > 0) {
			gain = (gain.toDouble() * 0.5.pow(musicPatchNode.volumeEnvelopeTime.toDouble() * 1.953125E-5 * musicPatchEnvelope.volumeEnvelopeDecay.toDouble()) + 0.5).toInt()
		}
		@Pc(105) var envelopePosition: Int
		@Pc(113) var envelopeValue: Int
		@Pc(131) var envelopeKeyStart: Int
		@Pc(143) var envelopeKeyEnd: Int
		if (musicPatchEnvelope.volumeEnvelopeData != null) {
			envelopePosition = musicPatchNode.volumeEnvelopePosition
			envelopeValue = musicPatchEnvelope.volumeEnvelopeData!![musicPatchNode.volumeEnvelopeIndex + 1].toInt()
			if (musicPatchNode.volumeEnvelopeIndex < musicPatchEnvelope.volumeEnvelopeData!!.size - 2) {
				envelopeKeyStart = (musicPatchEnvelope.volumeEnvelopeData!![musicPatchNode.volumeEnvelopeIndex].toInt() and 0xFF) shl 8
				envelopeKeyEnd = (musicPatchEnvelope.volumeEnvelopeData!![musicPatchNode.volumeEnvelopeIndex + 2].toInt() and 0xFF) shl 8
				envelopeValue += (musicPatchEnvelope.volumeEnvelopeData!![musicPatchNode.volumeEnvelopeIndex + 3].toInt() - envelopeValue) * (envelopePosition + -envelopeKeyStart) / (envelopeKeyEnd - envelopeKeyStart)
			}
			gain = (gain * envelopeValue + 32) shr 6
		}
		if (musicPatchNode.releasePhase > 0 && musicPatchEnvelope.releaseEnvelopeData != null) {
			envelopePosition = musicPatchNode.releasePhase
			envelopeValue = musicPatchEnvelope.releaseEnvelopeData!![musicPatchNode.releaseEnvelopeIndex + 1].toInt()
			if (musicPatchEnvelope.releaseEnvelopeData!!.size - 2 > musicPatchNode.releaseEnvelopeIndex) {
				envelopeKeyStart = (musicPatchEnvelope.releaseEnvelopeData!![musicPatchNode.releaseEnvelopeIndex].toInt() and 0xFF) shl 8
				envelopeKeyEnd = (musicPatchEnvelope.releaseEnvelopeData!![musicPatchNode.releaseEnvelopeIndex + 2].toInt() and 0xFF) shl 8
				envelopeValue += (musicPatchEnvelope.releaseEnvelopeData!![musicPatchNode.releaseEnvelopeIndex + 3].toInt() - envelopeValue) * (envelopePosition + -envelopeKeyStart) / (envelopeKeyEnd - envelopeKeyStart)
			}
			gain = (envelopeValue * gain + 32) shr 6
		}
		return gain
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(Lclient!dl;ZZ)V")
	@Synchronized
	fun setMusicTrack(@OriginalArg(0) midiSong: MusicTrack?, @OriginalArg(1) isLooping: Boolean) {
		this.loadAndPlayTrack(true, midiSong, isLooping)
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(Lclient!dl;JZZB)V")
	@Synchronized
	fun setMusicTrackAtPosition(@OriginalArg(0) midiSong: MusicTrack?, @OriginalArg(1) tickPosition: Long, @OriginalArg(2) isLooping: Boolean) {
		this.loadAndPlayTrack(true, midiSong, isLooping)
		this.fastForward(this.fileReader.division.toLong() * tickPosition)
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "()Lclient!jba;")
	@Synchronized
	override fun getFirstStream(): PcmStream? {
		return this.patchStream
	}

	@OriginalMember(owner = "client!ma", name = "h", descriptor = "(II)V")
	private fun stopVoices(@OriginalArg(1) channel: Int) {
		@Pc(14) var voice: MusicPatchNode? = this.patchStream.queue.first() as MusicPatchNode?
		while (voice != null) {
			if (channel < 0 || channel == voice.currentTrack) {
				if (voice.stream != null) {
					voice.stream!!.fadeOutAndStop(PcmPlayerManager.sampleRate / 100)
					if (voice.stream!!.isFading()) {
						this.patchStream.fadeOutScheduler.addSubStream(voice.stream)
					}
					voice.clear()
				}
				if (voice.releasePhase < 0) {
					this.activeNotes[voice.currentTrack][voice.currentNotePitch] = null
				}
				voice.unlink()
			}
			voice = this.patchStream.queue.next() as MusicPatchNode?
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(BII)V")
	private fun setProgram(@OriginalArg(1) program: Int, @OriginalArg(2) channel: Int) {
		if (program != this.programChange[channel]) {
			this.programChange[channel] = program
			for (@Pc(22) group in 0 until 128) {
				this.loopGroupNotes[channel][group] = null
			}
		}
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(IZLclient!vv;)V")
	fun applyInitialSampleOffset(@OriginalArg(1) reverseLoop: Boolean, @OriginalArg(2) musicPatchNode: MusicPatchNode?) {
		@Pc(8) var length: Int = musicPatchNode!!.audioFile!!.data!!.size
		@Pc(36) var sampleOffset: Int
		if (reverseLoop && musicPatchNode.audioFile!!.pingPong) {
			@Pc(23) val bounceLength: Int = length + length - musicPatchNode.audioFile!!.nominalBitRate
			sampleOffset = ((this.startOffsets[musicPatchNode.currentTrack].toLong() * bounceLength.toLong()) shr 6).toInt()
			length = length shl 0x8
			if (length <= sampleOffset) {
				sampleOffset = length + length - sampleOffset - 1
				musicPatchNode.stream!!.playBackward()
			}
		} else {
			sampleOffset = ((length.toLong() * this.startOffsets[musicPatchNode.currentTrack].toLong()) shr 6).toInt()
		}
		musicPatchNode.stream!!.setPlaybackPosition(sampleOffset)
	}

	@OriginalMember(owner = "client!ma", name = "h", descriptor = "(III)V")
	private fun setPitchBend(@OriginalArg(0) channel: Int, @OriginalArg(2) value: Int) {
		this.pitchBend[channel] = value
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(JI)V")
	private fun fastForward(@OriginalArg(0) limit: Long) {
		while (this.nextEventTickTime <= limit) {
			@Pc(12) var track: Int = this.prioritizedTrack
			@Pc(15) var tick: Int = this.prioritizedTrackLength
			@Pc(18) var time: Long = this.nextEventTickTime
			while (this.prioritizedTrackLength == tick) {
				while (this.fileReader.trackLengths!![track] == tick) {
					this.fileReader.getGotoTrack(track)
					@Pc(29) val event: Int = this.fileReader.readMessage(track)
					if (event == 1) {
						this.fileReader.setTrackDone()
						this.fileReader.markTracePosition(track)
						if (this.fileReader.isDone()) {
							if (!this.isLooping || tick == 0) {
								this.reset(true)
								this.fileReader.clear()
								return
							}
							this.fileReader.resetTracksFromTimestamp(time)
						}
						break
					}
					if ((event and 0x80) != 0 && (event and 0xF0) != 144) {
						this.handleEvent(event)
					}
					this.fileReader.getTrackLength(track)
					this.fileReader.markTracePosition(track)
				}
				this.currentTickTime = time
				track = this.fileReader.getPrioritizedTrack()
				tick = this.fileReader.trackLengths!![track]
				time = this.fileReader.getTickTimestamp(tick)
			}
			this.prioritizedTrack = track
			this.nextEventTickTime = time
			this.prioritizedTrackLength = tick
		}
	}

	@OriginalMember(owner = "client!ma", name = "f", descriptor = "(I)V")
	@Synchronized
	fun turnOffSound() {
		this.stop(true)
	}

	@OriginalMember(owner = "client!ma", name = "d", descriptor = "()I")
	@Synchronized
	override fun getActiveVoiceCount(): Int {
		return 0
	}

	companion object {
		@OriginalMember(owner = "client!qi", name = "a", descriptor = "(ILclient!in;I)Lclient!jf;")
		@JvmStatic
		fun loadMusicPatch(@OriginalArg(1) patchesResource: Js5ResourceProvider?, @OriginalArg(2) id: Int): MusicPatch? {
			@Pc(12) val data: ByteArray? = patchesResource!!.getFile(id)
			return if (data == null) null else MusicPatch(data)
		}

		@OriginalMember(owner = "client!pm", name = "a", descriptor = "(Lclient!ma;I)Lclient!ma;")
		@JvmStatic
		fun getStream(@OriginalArg(0) mixBuss: MidiPcmStream?): MidiPcmStream {
			@Pc(14) val mixBuss2: MidiPcmStream = if (mixBuss == null) MidiPcmStream() else MidiPcmStream(mixBuss)
			mixBuss2.initialize()
			return mixBuss2
		}
	}
}
