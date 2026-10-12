package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.sound.jagtheora.MusicPatchInfo
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket
import com.jagex.game.runetek5.config.midi.SoundBankCache

@OriginalClass("client!jf")
class MusicPatch : Node {

	@OriginalMember(owner = "client!jf", name = "s", descriptor = "[B")
	@JvmField
	var panOffset: ByteArray? = null

	@OriginalMember(owner = "client!jf", name = "t", descriptor = "[I")
	private var sampleOffsets: IntArray? = null

	@OriginalMember(owner = "client!jf", name = "v", descriptor = "[S")
	@JvmField
	var pitchOffset: ShortArray? = null

	@OriginalMember(owner = "client!jf", name = "w", descriptor = "[Lclient!ho;")
	@JvmField
	var musicPatchNodes2: Array<MusicPatchInfo?>? = null

	@OriginalMember(owner = "client!jf", name = "x", descriptor = "I")
	@JvmField
	var baseVelocity: Int = 0

	@OriginalMember(owner = "client!jf", name = "y", descriptor = "[B")
	@JvmField
	var loopMode: ByteArray? = null

	@OriginalMember(owner = "client!jf", name = "z", descriptor = "[B")
	@JvmField
	var volumeOffset: ByteArray? = null

	@OriginalMember(owner = "client!jf", name = "A", descriptor = "[Lclient!ob;")
	@JvmField
	var audioFiles: Array<VariableRateSoundPacket?>? = null

	@OriginalMember(owner = "client!jf", name = "<init>", descriptor = "()V")
	private constructor() : super()

	@OriginalMember(owner = "client!jf", name = "<init>", descriptor = "([B)V")
	constructor(@OriginalArg(0) fileData: ByteArray?) : super() {
		this.pitchOffset = ShortArray(128)
		this.panOffset = ByteArray(128)
		this.loopMode = ByteArray(128)
		this.volumeOffset = ByteArray(128)
		this.audioFiles = arrayOfNulls<VariableRateSoundPacket>(128)
		this.musicPatchNodes2 = arrayOfNulls<MusicPatchInfo>(128)
		this.sampleOffsets = IntArray(128)
		@Pc(36) val packet: Packet = Packet(fileData)
		@Pc(38) var loopModeRunCount: Int
		loopModeRunCount = 0
		while (packet.buffer!![loopModeRunCount + packet.currentPosition].toInt() != 0) {
			loopModeRunCount++
		}
		@Pc(52) val loopModeRuns: ByteArray = ByteArray(loopModeRunCount)
		for (@Pc(54) i in 0 until loopModeRunCount) {
			loopModeRuns[i] = packet.readByte()
		}
		packet.currentPosition++
		loopModeRunCount++
		@Pc(76) var currentPosition: Int = packet.currentPosition
		packet.currentPosition += loopModeRunCount
		@Pc(84) var panRunCount: Int
		panRunCount = 0
		while (packet.buffer!![panRunCount + packet.currentPosition].toInt() != 0) {
			panRunCount++
		}
		@Pc(101) val panRuns: ByteArray = ByteArray(panRunCount)
		for (@Pc(103) j in 0 until panRunCount) {
			panRuns[j] = packet.readByte()
		}
		panRunCount++
		packet.currentPosition++
		@Pc(129) var panValuePosition: Int = packet.currentPosition
		packet.currentPosition += panRunCount
		@Pc(137) var patchInfoRunCount: Int
		patchInfoRunCount = 0
		while (packet.buffer!![packet.currentPosition + patchInfoRunCount].toInt() != 0) {
			patchInfoRunCount++
		}
		@Pc(151) val patchInfoRuns: ByteArray = ByteArray(patchInfoRunCount)
		for (@Pc(153) k in 0 until patchInfoRunCount) {
			patchInfoRuns[k] = packet.readByte()
		}
		packet.currentPosition++
		patchInfoRunCount++
		@Pc(175) val runPatchInfoIndices: ByteArray = ByteArray(patchInfoRunCount)
		@Pc(184) var patchInfoCount: Int
		@Pc(188) var index: Int
		@Pc(194) val local194: Int
		if (patchInfoRunCount > 1) {
			runPatchInfoIndices[1] = 1
			patchInfoCount = 2
			@Pc(186) var patchInfoIndex: Int = 1
			index = 2
			while (index < patchInfoRunCount) {
				var encodedPatchInfoIndex: Int = packet.readUByte()
				if (encodedPatchInfoIndex == 0) {
					patchInfoIndex = patchInfoCount++
				} else {
					if (encodedPatchInfoIndex <= patchInfoIndex) {
						encodedPatchInfoIndex--
					}
					patchInfoIndex = encodedPatchInfoIndex
				}
				runPatchInfoIndices[index] = patchInfoIndex.toByte()
				index++
			}
		} else {
			patchInfoCount = patchInfoRunCount
		}
		@Pc(226) val musicPatchEnvelopes: Array<MusicPatchInfo?> = arrayOfNulls<MusicPatchInfo>(patchInfoCount)
		index = 0
		while (index < musicPatchEnvelopes.size) {
			@Pc(238) val musicPatchEnvelope: MusicPatchInfo = MusicPatchInfo()
			musicPatchEnvelopes[index] = musicPatchEnvelope
			@Pc(242) var segmentCount: Int = packet.readUByte()
			if (segmentCount > 0) {
				musicPatchEnvelope.volumeEnvelopeData = ByteArray(segmentCount * 2)
			}
			segmentCount = packet.readUByte()
			if (segmentCount > 0) {
				musicPatchEnvelope.releaseEnvelopeData = ByteArray(segmentCount * 2 + 2)
				musicPatchEnvelope.releaseEnvelopeData!![1] = 64
			}
			index++
		}
		var curvePointCount: Int = packet.readUByte()
		@Pc(297) val volumeCurve: ByteArray? = if (curvePointCount > 0) ByteArray(curvePointCount * 2) else null
		curvePointCount = packet.readUByte()
		@Pc(313) val panCurve: ByteArray? = if (curvePointCount > 0) ByteArray(curvePointCount * 2) else null
		@Pc(315) var keyRunCount: Int
		keyRunCount = 0
		while (packet.buffer!![packet.currentPosition + keyRunCount].toInt() != 0) {
			keyRunCount++
		}
		@Pc(330) val keyRuns: ByteArray = ByteArray(keyRunCount)
		for (@Pc(332) m in 0 until keyRunCount) {
			keyRuns[m] = packet.readByte()
		}
		packet.currentPosition++
		keyRunCount++
		@Pc(357) var accumulator: Int = 0
		for (@Pc(359) key in 0 until 128) {
			accumulator += packet.readUByte()
			this.pitchOffset!![key] = accumulator.toShort()
		}
		accumulator = 0
		for (@Pc(381) key in 0 until 128) {
			accumulator += packet.readUByte()
			this.pitchOffset!![key] = (this.pitchOffset!![key].toInt() + (accumulator shl 8)).toShort()
		}
		@Pc(408) var runRemaining: Int = 0
		@Pc(410) var runIndex: Int = 0
		@Pc(412) var sampleOffset: Int = 0
		for (@Pc(414) key in 0 until 128) {
			if (runRemaining == 0) {
				if (keyRuns.size > runIndex) {
					runRemaining = keyRuns[runIndex++].toInt()
				} else {
					runRemaining = -1
				}
				sampleOffset = packet.readVarInt()
			}
			this.pitchOffset!![key] = (this.pitchOffset!![key].toInt() + (((sampleOffset - 1) and 0x2) shl 14)).toShort()
			runRemaining--
			this.sampleOffsets!![key] = sampleOffset
		}
		runIndex = 0
		runRemaining = 0
		@Pc(472) var loopModeValue: Int = 0
		for (@Pc(474) key in 0 until 128) {
			if (this.sampleOffsets!![key] != 0) {
				if (runRemaining == 0) {
					loopModeValue = packet.buffer!![currentPosition++].toInt() - 1
					if (runIndex < loopModeRuns.size) {
						runRemaining = loopModeRuns[runIndex++].toInt()
					} else {
						runRemaining = -1
					}
				}
				runRemaining--
				this.loopMode!![key] = loopModeValue.toByte()
			}
		}
		runIndex = 0
		runRemaining = 0
		@Pc(524) var panValue: Int = 0
		for (@Pc(526) key in 0 until 128) {
			if (this.sampleOffsets!![key] != 0) {
				if (runRemaining == 0) {
					if (runIndex >= panRuns.size) {
						runRemaining = -1
					} else {
						runRemaining = panRuns[runIndex++].toInt()
					}
					panValue = (packet.buffer!![panValuePosition++].toInt() + 16) shl 2
				}
				runRemaining--
				this.panOffset!![key] = panValue.toByte()
			}
		}
		runRemaining = 0
		runIndex = 0
		@Pc(580) var musicPatchEnvelope2: MusicPatchInfo? = null
		for (@Pc(582) key in 0 until 128) {
			if (this.sampleOffsets!![key] != 0) {
				if (runRemaining == 0) {
					musicPatchEnvelope2 = musicPatchEnvelopes[runPatchInfoIndices[runIndex].toInt()]
					if (runIndex < patchInfoRuns.size) {
						runRemaining = patchInfoRuns[runIndex++].toInt()
					} else {
						runRemaining = -1
					}
				}
				runRemaining--
				this.musicPatchNodes2!![key] = musicPatchEnvelope2
			}
		}
		runRemaining = 0
		runIndex = 0
		@Pc(630) var volumeValue: Int = 0
		for (@Pc(632) key in 0 until 128) {
			if (runRemaining == 0) {
				if (keyRuns.size > runIndex) {
					runRemaining = keyRuns[runIndex++].toInt()
				} else {
					runRemaining = -1
				}
				if (this.sampleOffsets!![key] > 0) {
					volumeValue = packet.readUByte() + 1
				}
			}
			this.volumeOffset!![key] = volumeValue.toByte()
			runRemaining--
		}
		this.baseVelocity = packet.readUByte() + 1
		@Pc(699) val local699: Int
		for (@Pc(688) envelopeIndex in 0 until patchInfoCount) {
			@Pc(694) val musicPatchEnvelope3: MusicPatchInfo? = musicPatchEnvelopes[envelopeIndex]
			if (musicPatchEnvelope3!!.volumeEnvelopeData != null) {
				var dataIndex: Int = 1
				while (dataIndex < musicPatchEnvelope3.volumeEnvelopeData!!.size) {
					musicPatchEnvelope3.volumeEnvelopeData!![dataIndex] = packet.readByte()
					dataIndex += 2
				}
			}
			if (musicPatchEnvelope3.releaseEnvelopeData != null) {
				var dataIndex: Int = 3
				while (dataIndex < musicPatchEnvelope3.releaseEnvelopeData!!.size - 2) {
					musicPatchEnvelope3.releaseEnvelopeData!![dataIndex] = packet.readByte()
					dataIndex += 2
				}
			}
		}
		@Pc(744) val local744: Int
		if (volumeCurve != null) {
			var curveIndex: Int = 1
			while (curveIndex < volumeCurve.size) {
				volumeCurve[curveIndex] = packet.readByte()
				curveIndex += 2
			}
		}
		if (panCurve != null) {
			var curveIndex: Int = 1
			while (curveIndex < panCurve.size) {
				panCurve[curveIndex] = packet.readByte()
				curveIndex += 2
			}
		}
		@Pc(793) val local793: Int
		for (envelopeIndex in 0 until patchInfoCount) {
			@Pc(786) val musicPatchEnvelope4: MusicPatchInfo? = musicPatchEnvelopes[envelopeIndex]
			if (musicPatchEnvelope4!!.releaseEnvelopeData != null) {
				accumulator = 0
				var dataIndex: Int = 2
				while (dataIndex < musicPatchEnvelope4.releaseEnvelopeData!!.size) {
					accumulator = accumulator + packet.readUByte() + 1
					musicPatchEnvelope4.releaseEnvelopeData!![dataIndex] = accumulator.toByte()
					dataIndex += 2
				}
			}
		}
		@Pc(835) val local835: Int
		for (envelopeIndex in 0 until patchInfoCount) {
			@Pc(828) val musicPatchEnvelope5: MusicPatchInfo? = musicPatchEnvelopes[envelopeIndex]
			if (musicPatchEnvelope5!!.volumeEnvelopeData != null) {
				accumulator = 0
				var dataIndex: Int = 2
				while (dataIndex < musicPatchEnvelope5.volumeEnvelopeData!!.size) {
					accumulator = accumulator + packet.readUByte() + 1
					musicPatchEnvelope5.volumeEnvelopeData!![dataIndex] = accumulator.toByte()
					dataIndex += 2
				}
			}
		}
		@Pc(910) val local910: Int
		@Pc(933) val local933: Int
		@Pc(939) var endKey: Byte
		@Pc(959) var interpAccumulator: Int
		@Pc(961) var interpKey: Int
		@Pc(971) var interpValue: Int
		@Pc(904) var startKey: Byte
		@Pc(1016) var tailKey: Int
		if (volumeCurve != null) {
			accumulator = packet.readUByte()
			volumeCurve[0] = accumulator.toByte()
			var curveIndex: Int = 2
			while (curveIndex < volumeCurve.size) {
				accumulator = accumulator + packet.readUByte() + 1
				volumeCurve[curveIndex] = accumulator.toByte()
				curveIndex += 2
			}
			startKey = volumeCurve[0]
			@Pc(908) var startVolume: Byte = volumeCurve[1]
			var key: Int = 0
			while (key < startKey) {
				this.volumeOffset!![key] = ((startVolume * this.volumeOffset!![key] + 32) shr 6).toByte()
				key++
			}
			curveIndex = 2
			while (curveIndex < volumeCurve.size) {
				endKey = volumeCurve[curveIndex]
				@Pc(945) val endVolume: Byte = volumeCurve[curveIndex + 1]
				interpAccumulator = (endKey - startKey) / 2 + (endKey - startKey) * startVolume
				interpKey = startKey.toInt()
				while (interpKey < endKey) {
					interpValue = MusicPatch.floorDivide(endKey - startKey, interpAccumulator)
					this.volumeOffset!![interpKey] = ((this.volumeOffset!![interpKey] * interpValue + 32) shr 6).toByte()
					interpAccumulator += endVolume - startVolume
					interpKey++
				}
				startKey = endKey
				curveIndex += 2
				startVolume = endVolume
			}
			tailKey = startKey.toInt()
			while (tailKey < 128) {
				this.volumeOffset!![tailKey] = ((startVolume * this.volumeOffset!![tailKey] + 32) shr 6).toByte()
				tailKey++
			}
		}
		@Pc(1084) val local1084: Int
		if (panCurve != null) {
			accumulator = packet.readUByte()
			panCurve[0] = accumulator.toByte()
			var curveIndex: Int = 2
			while (curveIndex < panCurve.size) {
				accumulator = packet.readUByte() + accumulator + 1
				panCurve[curveIndex] = accumulator.toByte()
				curveIndex += 2
			}
			startKey = panCurve[0]
			var startPan: Int = panCurve[1].toInt() shl 1
			var key: Int = 0
			while (key < startKey) {
				var keyPan: Int = (this.panOffset!![key].toInt() and 0xFF) + startPan
				if (keyPan < 0) {
					keyPan = 0
				}
				if (keyPan > 128) {
					keyPan = 128
				}
				this.panOffset!![key] = keyPan.toByte()
				key++
			}
			@Pc(1141) val local1141: Int
			curveIndex = 2
			while (curveIndex < panCurve.size) {
				endKey = panCurve[curveIndex]
				val endPan: Int = panCurve[curveIndex + 1].toInt() shl 1
				interpAccumulator = (endKey - startKey) * startPan + (endKey - startKey) / 2
				interpKey = startKey.toInt()
				while (interpKey < endKey) {
					interpValue = MusicPatch.floorDivide(endKey - startKey, interpAccumulator)
					@Pc(1176) var adjustedPan: Int = (this.panOffset!![interpKey].toInt() and 0xFF) + interpValue
					if (adjustedPan < 0) {
						adjustedPan = 0
					}
					if (adjustedPan > 128) {
						adjustedPan = 128
					}
					this.panOffset!![interpKey] = adjustedPan.toByte()
					interpAccumulator += endPan - startPan
					interpKey++
				}
				startPan = endPan
				startKey = endKey
				curveIndex += 2
			}
			tailKey = startKey.toInt()
			while (tailKey < 128) {
				var clampedPan: Int = startPan + (this.panOffset!![tailKey].toInt() and 0xFF)
				if (clampedPan < 0) {
					clampedPan = 0
				}
				if (clampedPan > 128) {
					clampedPan = 128
				}
				this.panOffset!![tailKey] = clampedPan.toByte()
				tailKey++
			}
		}
		for (envelopeIndex in 0 until patchInfoCount) {
			musicPatchEnvelopes[envelopeIndex]!!.volumeEnvelopeDecay = packet.readUByte()
		}
		for (envelopeIndex in 0 until patchInfoCount) {
			@Pc(1288) val musicPatchEnvelope6: MusicPatchInfo? = musicPatchEnvelopes[envelopeIndex]
			if (musicPatchEnvelope6!!.volumeEnvelopeData != null) {
				musicPatchEnvelope6.volumeEnvelopeKeyScaling = packet.readUByte()
			}
			if (musicPatchEnvelope6.releaseEnvelopeData != null) {
				musicPatchEnvelope6.releaseEnvelopeKeyScaling = packet.readUByte()
			}
			if (musicPatchEnvelope6.volumeEnvelopeDecay > 0) {
				musicPatchEnvelope6.decayKeyScaling = packet.readUByte()
			}
		}
		for (envelopeIndex in 0 until patchInfoCount) {
			musicPatchEnvelopes[envelopeIndex]!!.volumeEnvelopeRelease = packet.readUByte()
		}
		for (envelopeIndex in 0 until patchInfoCount) {
			@Pc(1346) val musicPatchEnvelope7: MusicPatchInfo? = musicPatchEnvelopes[envelopeIndex]
			if (musicPatchEnvelope7!!.volumeEnvelopeRelease > 0) {
				musicPatchEnvelope7.vibratoLfoPitch = packet.readUByte()
			}
		}
		for (envelopeIndex in 0 until patchInfoCount) {
			@Pc(1366) val musicPatchEnvelope8: MusicPatchInfo? = musicPatchEnvelopes[envelopeIndex]
			if (musicPatchEnvelope8!!.vibratoLfoPitch > 0) {
				musicPatchEnvelope8.vibratoDelay = packet.readUByte()
			}
		}
	}

	@OriginalMember(owner = "client!jf", name = "a", descriptor = "(Lclient!hs;[I[BI)Z")
	fun ready(@OriginalArg(0) soundCache: SoundBankCache?, @OriginalArg(1) sampleRates: IntArray?, @OriginalArg(2) notes: ByteArray?): Boolean {
		@Pc(7) var isReady: Boolean = true
		@Pc(9) var previousSampleOffset: Int = 0
		@Pc(11) var variableRateSoundPacket: VariableRateSoundPacket? = null
		for (@Pc(13) i in 0 until 128) {
			if (notes == null || notes[i].toInt() != 0) {
				@Pc(28) var sampleOffset: Int = this.sampleOffsets!![i]
				if (sampleOffset != 0) {
					if (sampleOffset != previousSampleOffset) {
						previousSampleOffset = sampleOffset--
						if ((sampleOffset and 0x1) == 0) {
							variableRateSoundPacket = soundCache!!.listEffect(sampleOffset shr 2, sampleRates)
						} else {
							variableRateSoundPacket = soundCache!!.listMusic(sampleRates, sampleOffset shr 2)
						}
						if (variableRateSoundPacket == null) {
							isReady = false
						}
					}
					if (variableRateSoundPacket != null) {
						this.audioFiles!![i] = variableRateSoundPacket
						this.sampleOffsets!![i] = 0
					}
				}
			}
		}
		return isReady
	}

	@OriginalMember(owner = "client!jf", name = "b", descriptor = "(B)V")
	fun clearSoundIds() {
		this.sampleOffsets = null
	}

	companion object {
		@OriginalMember(owner = "client!uaa", name = "a", descriptor = "(III)I")
		@JvmStatic
		fun floorDivide(@OriginalArg(0) divisor: Int, @OriginalArg(2) dividend: Int): Int {
			@Pc(7) val signBit: Int = dividend ushr 31
			return (dividend + signBit) / divisor - signBit
		}
	}
}
