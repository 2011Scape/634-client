package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.cache.node.MidiProgramNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.sound.midi.Midi
import com.jagex.game.runetek5.sound.midi.MidiController
import com.jagex.game.runetek5.sound.midi.MidiStatus
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!dl")
class MusicTrack : Node {

	@OriginalMember(owner = "client!dl", name = "p", descriptor = "Lclient!eq;")
	@JvmField
	var table: IterableHashTable? = null

	@OriginalMember(owner = "client!dl", name = "o", descriptor = "[B")
	@JvmField
	val midi: ByteArray?

	@OriginalMember(owner = "client!dl", name = "<init>", descriptor = "(Lclient!cea;)V")
	constructor(@OriginalArg(0) packet: Packet?) : super() {
		packet!!.currentPosition = packet.buffer!!.size - 3
		@Pc(12) val trackCount: Int = packet.readUByte()
		@Pc(16) val division: Int = packet.readUShort()
		@Pc(22) var midiOutputSize: Int = trackCount * 10 + 14
		packet.currentPosition = 0
		@Pc(27) var tempoEventCount: Int = 0
		@Pc(29) var controllerChangeCount: Int = 0
		@Pc(31) var noteOnCount: Int = 0
		@Pc(33) var noteOffCount: Int = 0
		@Pc(35) var pitchBendCount: Int = 0
		@Pc(37) var channelPressureCount: Int = 0
		@Pc(39) var keyPressureCount: Int = 0
		@Pc(41) var programChangeCount: Int = 0
		@Pc(46) val local46: Int
		@Pc(50) val local50: Int
		for (@Pc(43) i in 0 until trackCount) {
			var lastStatus: Int = -1
			while (true) {
				val opcode: Int = packet.readUByte()
				if (opcode != lastStatus) {
					midiOutputSize++
				}
				lastStatus = opcode and Midi.CHANNEL_MASK
				if (opcode == 7) {
					break
				}
				if (opcode == 23) {
					tempoEventCount++
				} else if (lastStatus == 0) {
					noteOnCount++
				} else if (lastStatus == 1) {
					noteOffCount++
				} else if (lastStatus == 2) {
					controllerChangeCount++
				} else if (lastStatus == 3) {
					pitchBendCount++
				} else if (lastStatus == 4) {
					channelPressureCount++
				} else if (lastStatus == 5) {
					keyPressureCount++
				} else if (lastStatus == 6) {
					programChangeCount++
				} else {
					throw RuntimeException()
				}
			}
		}
		midiOutputSize += tempoEventCount * 5
		midiOutputSize += (noteOnCount + noteOffCount + controllerChangeCount + pitchBendCount + keyPressureCount) * 2
		midiOutputSize += channelPressureCount + programChangeCount
		val deltaTimeStart: Int = packet.currentPosition
		val eventCount: Int = trackCount + tempoEventCount + controllerChangeCount + noteOnCount + noteOffCount + pitchBendCount + channelPressureCount + keyPressureCount + programChangeCount
		for (@Pc(157) j in 0 until eventCount) {
			packet.readVarInt()
		}
		midiOutputSize += packet.currentPosition - deltaTimeStart
		@Pc(176) var currentPosition: Int = packet.currentPosition
		@Pc(178) var modWheelCoarseCount: Int = 0
		@Pc(180) var modWheelFineCount: Int = 0
		@Pc(182) var volumeCoarseCount: Int = 0
		@Pc(184) var volumeFineCount: Int = 0
		@Pc(186) var panCoarseCount: Int = 0
		@Pc(188) var panFineCount: Int = 0
		@Pc(190) var nrpnFineCount: Int = 0
		@Pc(192) var nrpnCoarseCount: Int = 0
		@Pc(194) var rpnFineCount: Int = 0
		@Pc(196) var rpnCoarseCount: Int = 0
		@Pc(198) var switchControllerCount: Int = 0
		@Pc(200) var otherControllerCount: Int = 0
		@Pc(202) var controllerNumber: Int = 0
		for (@Pc(204) k in 0 until controllerChangeCount) {
			controllerNumber = (controllerNumber + packet.readUByte()) and 0x7F
			if (controllerNumber == MidiController.BANK_SELECT_COARSE || controllerNumber == MidiController.BANK_SELECT_FINE) {
				programChangeCount++
			} else if (controllerNumber == MidiController.MODULATION_WHEEL_COARSE) {
				modWheelCoarseCount++
			} else if (controllerNumber == MidiController.MODULATION_WHEEL_FINE) {
				modWheelFineCount++
			} else if (controllerNumber == MidiController.MAIN_VOLUME_COARSE) {
				volumeCoarseCount++
			} else if (controllerNumber == MidiController.MAIN_VOLUME_FINE) {
				volumeFineCount++
			} else if (controllerNumber == MidiController.PAN_COARSE) {
				panCoarseCount++
			} else if (controllerNumber == MidiController.PAN_FINE) {
				panFineCount++
			} else if (controllerNumber == MidiController.NON_REGISTERED_PARAM_FINE) {
				nrpnFineCount++
			} else if (controllerNumber == MidiController.NON_REGISTERED_PARAM_COARSE) {
				nrpnCoarseCount++
			} else if (controllerNumber == MidiController.REGISTERED_PARAM_FINE) {
				rpnFineCount++
			} else if (controllerNumber == MidiController.REGISTERED_PARAM_COARSE) {
				rpnCoarseCount++
			} else if (controllerNumber == MidiController.HOLD || controllerNumber == MidiController.PORTAMENTO_PEDAL || controllerNumber == MidiController.ALL_SOUND_OFF || controllerNumber == MidiController.ALL_CONTROLLERS_OFF || controllerNumber == MidiController.ALL_NOTES_OFF) {
				switchControllerCount++
			} else {
				otherControllerCount++
			}
		}
		@Pc(294) var opcodePosition: Int = 0
		@Pc(297) var switchControllerOffset: Int = packet.currentPosition
		packet.currentPosition += switchControllerCount
		@Pc(306) var keyPressureOffset: Int = packet.currentPosition
		packet.currentPosition += keyPressureCount
		@Pc(315) var channelPressureOffset: Int = packet.currentPosition
		packet.currentPosition += channelPressureCount
		@Pc(324) var pitchBendHighOffset: Int = packet.currentPosition
		packet.currentPosition += pitchBendCount
		@Pc(333) var modWheelCoarseOffset: Int = packet.currentPosition
		packet.currentPosition += modWheelCoarseCount
		@Pc(342) var volumeCoarseOffset: Int = packet.currentPosition
		packet.currentPosition += volumeCoarseCount
		@Pc(351) var panCoarseOffset: Int = packet.currentPosition
		packet.currentPosition += panCoarseCount
		@Pc(360) var noteKeyOffset: Int = packet.currentPosition
		packet.currentPosition += noteOnCount + noteOffCount + keyPressureCount
		@Pc(373) var noteOnVelocityOffset: Int = packet.currentPosition
		packet.currentPosition += noteOnCount
		@Pc(382) var otherControllerOffset: Int = packet.currentPosition
		packet.currentPosition += otherControllerCount
		@Pc(391) var noteOffVelocityOffset: Int = packet.currentPosition
		packet.currentPosition += noteOffCount
		@Pc(400) var modWheelFineOffset: Int = packet.currentPosition
		packet.currentPosition += modWheelFineCount
		@Pc(409) var volumeFineOffset: Int = packet.currentPosition
		packet.currentPosition += volumeFineCount
		@Pc(418) var panFineOffset: Int = packet.currentPosition
		packet.currentPosition += panFineCount
		@Pc(427) var programChangeOffset: Int = packet.currentPosition
		packet.currentPosition += programChangeCount
		@Pc(436) var pitchBendLowOffset: Int = packet.currentPosition
		packet.currentPosition += pitchBendCount
		@Pc(445) var nrpnFineOffset: Int = packet.currentPosition
		packet.currentPosition += nrpnFineCount
		@Pc(454) var nrpnCoarseOffset: Int = packet.currentPosition
		packet.currentPosition += nrpnCoarseCount
		@Pc(463) var rpnFineOffset: Int = packet.currentPosition
		packet.currentPosition += rpnFineCount
		@Pc(472) var rpnCoarseOffset: Int = packet.currentPosition
		packet.currentPosition += rpnCoarseCount
		@Pc(481) var tempoOffset: Int = packet.currentPosition
		packet.currentPosition += tempoEventCount * 3
		this.midi = ByteArray(midiOutputSize)
		@Pc(499) val packet2: Packet = Packet(this.midi)
		packet2.writeInt(Midi.FILE_HEADER)
		packet2.writeInt(6)
		packet2.writeShort(if (trackCount > 1) 1 else 0)
		packet2.writeShort(trackCount)
		packet2.writeShort(division)
		packet.currentPosition = deltaTimeStart
		@Pc(529) var channel: Int = 0
		@Pc(531) var noteKey: Int = 0
		@Pc(533) var noteOnVelocity: Int = 0
		@Pc(535) var noteOffVelocity: Int = 0
		@Pc(537) var pitchBend: Int = 0
		@Pc(539) var channelPressure: Int = 0
		@Pc(541) var keyPressure: Int = 0
		@Pc(544) val controllerValues: IntArray = IntArray(128)
		controllerNumber = 0
		label220@ for (@Pc(548) m in 0 until trackCount) {
			packet2.writeInt(Midi.TRACK_HEADER)
			packet2.currentPosition += 4
			@Pc(562) val trackStartPosition: Int = packet2.currentPosition
			@Pc(564) var eventType: Int = -1
			while (true) {
				while (true) {
					@Pc(568) val deltaTime: Int = packet.readVarInt()
					packet2.writeFlags(deltaTime)
					@Pc(580) val encodedEvent: Int = packet.buffer!![opcodePosition++].toInt() and 0xFF
					@Pc(587) val eventTypeChanged: Boolean = encodedEvent != eventType
					eventType = encodedEvent and Midi.CHANNEL_MASK
					if (encodedEvent == 7) {
						if (eventTypeChanged) {
							packet2.writeByte(255)
						}
						packet2.writeByte(47)
						packet2.writeByte(0)
						packet2.writeIntLength(packet2.currentPosition - trackStartPosition)
						continue@label220
					}
					if (encodedEvent == 23) {
						if (eventTypeChanged) {
							packet2.writeByte(255)
						}
						packet2.writeByte(81)
						packet2.writeByte(3)
						packet2.writeByte(packet.buffer!![tempoOffset++].toInt())
						packet2.writeByte(packet.buffer!![tempoOffset++].toInt())
						packet2.writeByte(packet.buffer!![tempoOffset++].toInt())
					} else {
						channel = channel xor (encodedEvent shr 4)
						if (eventType == 0) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.NOTE_ON)
							}
							noteKey += packet.buffer!![noteKeyOffset++].toInt()
							noteOnVelocity += packet.buffer!![noteOnVelocityOffset++].toInt()
							packet2.writeByte(noteKey and 0x7F)
							packet2.writeByte(noteOnVelocity and 0x7F)
						} else if (eventType == 1) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.NOTE_OFF)
							}
							noteKey += packet.buffer!![noteKeyOffset++].toInt()
							noteOffVelocity += packet.buffer!![noteOffVelocityOffset++].toInt()
							packet2.writeByte(noteKey and 0x7F)
							packet2.writeByte(noteOffVelocity and 0x7F)
						} else if (eventType == 2) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.CONTROLLER)
							}
							controllerNumber = (controllerNumber + packet.buffer!![currentPosition++].toInt()) and 0x7F
							packet2.writeByte(controllerNumber)
							@Pc(772) val controllerDelta: Byte
							if (controllerNumber == MidiController.BANK_SELECT_COARSE || controllerNumber == MidiController.BANK_SELECT_FINE) {
								controllerDelta = packet.buffer!![programChangeOffset++]
							} else if (controllerNumber == MidiController.MODULATION_WHEEL_COARSE) {
								controllerDelta = packet.buffer!![modWheelCoarseOffset++]
							} else if (controllerNumber == MidiController.MODULATION_WHEEL_FINE) {
								controllerDelta = packet.buffer!![modWheelFineOffset++]
							} else if (controllerNumber == MidiController.MAIN_VOLUME_COARSE) {
								controllerDelta = packet.buffer!![volumeCoarseOffset++]
							} else if (controllerNumber == MidiController.MAIN_VOLUME_FINE) {
								controllerDelta = packet.buffer!![volumeFineOffset++]
							} else if (controllerNumber == MidiController.PAN_COARSE) {
								controllerDelta = packet.buffer!![panCoarseOffset++]
							} else if (controllerNumber == MidiController.PAN_FINE) {
								controllerDelta = packet.buffer!![panFineOffset++]
							} else if (controllerNumber == MidiController.NON_REGISTERED_PARAM_FINE) {
								controllerDelta = packet.buffer!![nrpnFineOffset++]
							} else if (controllerNumber == MidiController.NON_REGISTERED_PARAM_COARSE) {
								controllerDelta = packet.buffer!![nrpnCoarseOffset++]
							} else if (controllerNumber == MidiController.REGISTERED_PARAM_FINE) {
								controllerDelta = packet.buffer!![rpnFineOffset++]
							} else if (controllerNumber == MidiController.REGISTERED_PARAM_COARSE) {
								controllerDelta = packet.buffer!![rpnCoarseOffset++]
							} else if (controllerNumber == MidiController.HOLD || controllerNumber == MidiController.PORTAMENTO_PEDAL || controllerNumber == MidiController.ALL_SOUND_OFF || controllerNumber == MidiController.ALL_CONTROLLERS_OFF || controllerNumber == MidiController.ALL_NOTES_OFF) {
								controllerDelta = packet.buffer!![switchControllerOffset++]
							} else {
								controllerDelta = packet.buffer!![otherControllerOffset++]
							}
							@Pc(907) val controllerValue: Int = controllerDelta.toInt() + controllerValues[controllerNumber]
							controllerValues[controllerNumber] = controllerValue
							packet2.writeByte(controllerValue and 0x7F)
						} else if (eventType == 3) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.PITCH)
							}
							pitchBend += packet.buffer!![pitchBendLowOffset++].toInt()
							pitchBend += packet.buffer!![pitchBendHighOffset++].toInt() shl 7
							packet2.writeByte(pitchBend and 0x7F)
							packet2.writeByte((pitchBend shr 7) and 0x7F)
						} else if (eventType == 4) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.CHANNEL_AFTERTOUCH)
							}
							channelPressure += packet.buffer!![channelPressureOffset++].toInt()
							packet2.writeByte(channelPressure and 0x7F)
						} else if (eventType == 5) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.NOTE_AFTERTOUCH)
							}
							noteKey += packet.buffer!![noteKeyOffset++].toInt()
							keyPressure += packet.buffer!![keyPressureOffset++].toInt()
							packet2.writeByte(noteKey and 0x7F)
							packet2.writeByte(keyPressure and 0x7F)
						} else if (eventType == 6) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.PROGRAM_CHANGE)
							}
							packet2.writeByte(packet.buffer!![programChangeOffset++].toInt())
						} else {
							throw RuntimeException()
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!dl", name = "a", descriptor = "()V")
	fun buildPatchTable() {
		if (this.table != null) {
			return
		}
		this.table = IterableHashTable(16)
		@Pc(12) val bankSelects: IntArray = IntArray(16)
		@Pc(15) val programs: IntArray = IntArray(16)
		programs[9] = 128
		bankSelects[9] = 128
		@Pc(29) val midiSequence: MidiFileReader = MidiFileReader(this.midi)
		@Pc(32) val trackCount: Int = midiSequence.getTrackCount()
		for (@Pc(34) track in 0 until trackCount) {
			midiSequence.getGotoTrack(track)
			midiSequence.getTrackLength(track)
			midiSequence.markTracePosition(track)
		}
		label53@ do {
			while (true) {
				@Pc(51) val track: Int = midiSequence.getPrioritizedTrack()
				@Pc(56) val tick: Int = midiSequence.trackLengths!![track]
				while (midiSequence.trackLengths!![track] == tick) {
					midiSequence.getGotoTrack(track)
					@Pc(64) val message: Int = midiSequence.readMessage(track)
					if (message == 1) {
						midiSequence.setTrackDone()
						midiSequence.markTracePosition(track)
						continue@label53
					}
					@Pc(80) val status: Int = message and 0xF0
					@Pc(87) var channel: Int
					@Pc(93) var data1: Int
					@Pc(99) var data2: Int
					if (status == MidiStatus.CONTROLLER) {
						channel = message and Midi.CHANNEL_MASK
						data1 = (message shr 8) and 0x7F
						data2 = (message shr 16) and 0x7F
						if (data1 == 0) {
							bankSelects[channel] = (bankSelects[channel] and 0xFFE03FFF.toInt()) + (data2 shl 14)
						}
						if (data1 == 32) {
							bankSelects[channel] = (bankSelects[channel] and 0xFFFFC07F.toInt()) + (data2 shl 7)
						}
					}
					if (status == MidiStatus.PROGRAM_CHANGE) {
						channel = message and Midi.CHANNEL_MASK
						data1 = (message shr 8) and 0x7F
						programs[channel] = bankSelects[channel] + data1
					}
					if (status == MidiStatus.NOTE_ON) {
						channel = message and Midi.CHANNEL_MASK
						data1 = (message shr 8) and 0x7F
						data2 = (message shr 16) and 0x7F
						if (data2 > 0) {
							@Pc(174) val patchId: Int = programs[channel]
							@Pc(182) var midiProgramNode: MidiProgramNode? = this.table!!.get(patchId.toLong()) as MidiProgramNode?
							if (midiProgramNode == null) {
								midiProgramNode = MidiProgramNode(ByteArray(128))
								this.table!!.put(patchId.toLong(), midiProgramNode)
							}
							midiProgramNode.notes!![data1] = 1
						}
					}
					midiSequence.getTrackLength(track)
					midiSequence.markTracePosition(track)
				}
			}
		} while (!midiSequence.isDone())
	}

	@OriginalMember(owner = "client!dl", name = "b", descriptor = "()V")
	fun clear() {
		this.table = null
	}

	companion object {
		@OriginalMember(owner = "client!dl", name = "a", descriptor = "(Lclient!in;II)Lclient!dl;")
		@JvmStatic
		fun loadMusictrack(@OriginalArg(0) resource: Js5ResourceProvider?, @OriginalArg(1) archiveId: Int, @OriginalArg(2) fileId: Int): MusicTrack? {
			@Pc(5) val data: ByteArray? = resource!!.getFile(archiveId, fileId)
			return if (data == null) null else MusicTrack(Packet(data))
		}
	}
}
