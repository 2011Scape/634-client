package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.cache.node.MidiProgramNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.sound.midi.Midi;
import com.jagex.game.runetek5.sound.midi.MidiController;
import com.jagex.game.runetek5.sound.midi.MidiStatus;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;

@OriginalClass("client!dl")
public final class MusicTrack extends Node {

	@OriginalMember(owner = "client!dl", name = "p", descriptor = "Lclient!eq;")
	public IterableHashTable table;

	@OriginalMember(owner = "client!dl", name = "o", descriptor = "[B")
	public final byte[] midi;

	@OriginalMember(owner = "client!dl", name = "<init>", descriptor = "(Lclient!cea;)V")
	public MusicTrack(@OriginalArg(0) Packet packet) {
		packet.currentPosition = packet.buffer.length - 3;
		@Pc(12) int trackCount = packet.readUByte();
		@Pc(16) int division = packet.readUShort();
		@Pc(22) int midiOutputSize = trackCount * 10 + 14;
		packet.currentPosition = 0;
		@Pc(27) int tempoEventCount = 0;
		@Pc(29) int controllerChangeCount = 0;
		@Pc(31) int noteOnCount = 0;
		@Pc(33) int noteOffCount = 0;
		@Pc(35) int pitchBendCount = 0;
		@Pc(37) int channelPressureCount = 0;
		@Pc(39) int keyPressureCount = 0;
		@Pc(41) int programChangeCount = 0;
		@Pc(46) int local46;
		@Pc(50) int local50;
		for (@Pc(43) int i = 0; i < trackCount; i++) {
			int lastStatus = -1;
			while (true) {
				int opcode = packet.readUByte();
				if (opcode != lastStatus) {
					midiOutputSize++;
				}
				lastStatus = opcode & Midi.CHANNEL_MASK;
				if (opcode == 7) {
					break;
				}
				if (opcode == 23) {
					tempoEventCount++;
				} else if (lastStatus == 0) {
					noteOnCount++;
				} else if (lastStatus == 1) {
					noteOffCount++;
				} else if (lastStatus == 2) {
					controllerChangeCount++;
				} else if (lastStatus == 3) {
					pitchBendCount++;
				} else if (lastStatus == 4) {
					channelPressureCount++;
				} else if (lastStatus == 5) {
					keyPressureCount++;
				} else if (lastStatus == 6) {
					programChangeCount++;
				} else {
					throw new RuntimeException();
				}
			}
		}
		midiOutputSize += tempoEventCount * 5;
		midiOutputSize += (noteOnCount + noteOffCount + controllerChangeCount + pitchBendCount + keyPressureCount) * 2;
		midiOutputSize += channelPressureCount + programChangeCount;
		int deltaTimeStart = packet.currentPosition;
		int eventCount = trackCount + tempoEventCount + controllerChangeCount + noteOnCount + noteOffCount + pitchBendCount + channelPressureCount + keyPressureCount + programChangeCount;
		for (@Pc(157) int j = 0; j < eventCount; j++) {
			packet.readVarInt();
		}
		midiOutputSize += packet.currentPosition - deltaTimeStart;
		@Pc(176) int currentPosition = packet.currentPosition;
		@Pc(178) int modWheelCoarseCount = 0;
		@Pc(180) int modWheelFineCount = 0;
		@Pc(182) int volumeCoarseCount = 0;
		@Pc(184) int volumeFineCount = 0;
		@Pc(186) int panCoarseCount = 0;
		@Pc(188) int panFineCount = 0;
		@Pc(190) int nrpnFineCount = 0;
		@Pc(192) int nrpnCoarseCount = 0;
		@Pc(194) int rpnFineCount = 0;
		@Pc(196) int rpnCoarseCount = 0;
		@Pc(198) int switchControllerCount = 0;
		@Pc(200) int otherControllerCount = 0;
		@Pc(202) int controllerNumber = 0;
		for (@Pc(204) int k = 0; k < controllerChangeCount; k++) {
			controllerNumber = controllerNumber + packet.readUByte() & 0x7F;
			if (controllerNumber == MidiController.BANK_SELECT_COARSE || controllerNumber == MidiController.BANK_SELECT_FINE) {
				programChangeCount++;
			} else if (controllerNumber == MidiController.MODULATION_WHEEL_COARSE) {
				modWheelCoarseCount++;
			} else if (controllerNumber == MidiController.MODULATION_WHEEL_FINE) {
				modWheelFineCount++;
			} else if (controllerNumber == MidiController.MAIN_VOLUME_COARSE) {
				volumeCoarseCount++;
			} else if (controllerNumber == MidiController.MAIN_VOLUME_FINE) {
				volumeFineCount++;
			} else if (controllerNumber == MidiController.PAN_COARSE) {
				panCoarseCount++;
			} else if (controllerNumber == MidiController.PAN_FINE) {
				panFineCount++;
			} else if (controllerNumber == MidiController.NON_REGISTERED_PARAM_FINE) {
				nrpnFineCount++;
			} else if (controllerNumber == MidiController.NON_REGISTERED_PARAM_COARSE) {
				nrpnCoarseCount++;
			} else if (controllerNumber == MidiController.REGISTERED_PARAM_FINE) {
				rpnFineCount++;
			} else if (controllerNumber == MidiController.REGISTERED_PARAM_COARSE) {
				rpnCoarseCount++;
			} else if (controllerNumber == MidiController.HOLD || controllerNumber == MidiController.PORTAMENTO_PEDAL || controllerNumber == MidiController.ALL_SOUND_OFF || controllerNumber == MidiController.ALL_CONTROLLERS_OFF || controllerNumber == MidiController.ALL_NOTES_OFF) {
				switchControllerCount++;
			} else {
				otherControllerCount++;
			}
		}
		@Pc(294) int opcodePosition = 0;
		@Pc(297) int switchControllerOffset = packet.currentPosition;
		packet.currentPosition += switchControllerCount;
		@Pc(306) int keyPressureOffset = packet.currentPosition;
		packet.currentPosition += keyPressureCount;
		@Pc(315) int channelPressureOffset = packet.currentPosition;
		packet.currentPosition += channelPressureCount;
		@Pc(324) int pitchBendHighOffset = packet.currentPosition;
		packet.currentPosition += pitchBendCount;
		@Pc(333) int modWheelCoarseOffset = packet.currentPosition;
		packet.currentPosition += modWheelCoarseCount;
		@Pc(342) int volumeCoarseOffset = packet.currentPosition;
		packet.currentPosition += volumeCoarseCount;
		@Pc(351) int panCoarseOffset = packet.currentPosition;
		packet.currentPosition += panCoarseCount;
		@Pc(360) int noteKeyOffset = packet.currentPosition;
		packet.currentPosition += noteOnCount + noteOffCount + keyPressureCount;
		@Pc(373) int noteOnVelocityOffset = packet.currentPosition;
		packet.currentPosition += noteOnCount;
		@Pc(382) int otherControllerOffset = packet.currentPosition;
		packet.currentPosition += otherControllerCount;
		@Pc(391) int noteOffVelocityOffset = packet.currentPosition;
		packet.currentPosition += noteOffCount;
		@Pc(400) int modWheelFineOffset = packet.currentPosition;
		packet.currentPosition += modWheelFineCount;
		@Pc(409) int volumeFineOffset = packet.currentPosition;
		packet.currentPosition += volumeFineCount;
		@Pc(418) int panFineOffset = packet.currentPosition;
		packet.currentPosition += panFineCount;
		@Pc(427) int programChangeOffset = packet.currentPosition;
		packet.currentPosition += programChangeCount;
		@Pc(436) int pitchBendLowOffset = packet.currentPosition;
		packet.currentPosition += pitchBendCount;
		@Pc(445) int nrpnFineOffset = packet.currentPosition;
		packet.currentPosition += nrpnFineCount;
		@Pc(454) int nrpnCoarseOffset = packet.currentPosition;
		packet.currentPosition += nrpnCoarseCount;
		@Pc(463) int rpnFineOffset = packet.currentPosition;
		packet.currentPosition += rpnFineCount;
		@Pc(472) int rpnCoarseOffset = packet.currentPosition;
		packet.currentPosition += rpnCoarseCount;
		@Pc(481) int tempoOffset = packet.currentPosition;
		packet.currentPosition += tempoEventCount * 3;
		this.midi = new byte[midiOutputSize];
		@Pc(499) Packet packet2 = new Packet(this.midi);
		packet2.writeInt(Midi.FILE_HEADER);
		packet2.writeInt(6);
		packet2.writeShort(trackCount > 1 ? 1 : 0);
		packet2.writeShort(trackCount);
		packet2.writeShort(division);
		packet.currentPosition = deltaTimeStart;
		@Pc(529) int channel = 0;
		@Pc(531) int noteKey = 0;
		@Pc(533) int noteOnVelocity = 0;
		@Pc(535) int noteOffVelocity = 0;
		@Pc(537) int pitchBend = 0;
		@Pc(539) int channelPressure = 0;
		@Pc(541) int keyPressure = 0;
		@Pc(544) int[] controllerValues = new int[128];
		controllerNumber = 0;
		label220: for (@Pc(548) int m = 0; m < trackCount; m++) {
			packet2.writeInt(Midi.TRACK_HEADER);
			packet2.currentPosition += 4;
			@Pc(562) int trackStartPosition = packet2.currentPosition;
			@Pc(564) int eventType = -1;
			while (true) {
				while (true) {
					@Pc(568) int deltaTime = packet.readVarInt();
					packet2.writeFlags(deltaTime);
					@Pc(580) int encodedEvent = packet.buffer[opcodePosition++] & 0xFF;
					@Pc(587) boolean eventTypeChanged = encodedEvent != eventType;
					eventType = encodedEvent & Midi.CHANNEL_MASK;
					if (encodedEvent == 7) {
						if (eventTypeChanged) {
							packet2.writeByte(255);
						}
						packet2.writeByte(47);
						packet2.writeByte(0);
						packet2.writeIntLength(packet2.currentPosition - trackStartPosition);
						continue label220;
					}
					if (encodedEvent == 23) {
						if (eventTypeChanged) {
							packet2.writeByte(255);
						}
						packet2.writeByte(81);
						packet2.writeByte(3);
						packet2.writeByte(packet.buffer[tempoOffset++]);
						packet2.writeByte(packet.buffer[tempoOffset++]);
						packet2.writeByte(packet.buffer[tempoOffset++]);
					} else {
						channel ^= encodedEvent >> 4;
						if (eventType == 0) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.NOTE_ON);
							}
							noteKey += packet.buffer[noteKeyOffset++];
							noteOnVelocity += packet.buffer[noteOnVelocityOffset++];
							packet2.writeByte(noteKey & 0x7F);
							packet2.writeByte(noteOnVelocity & 0x7F);
						} else if (eventType == 1) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.NOTE_OFF);
							}
							noteKey += packet.buffer[noteKeyOffset++];
							noteOffVelocity += packet.buffer[noteOffVelocityOffset++];
							packet2.writeByte(noteKey & 0x7F);
							packet2.writeByte(noteOffVelocity & 0x7F);
						} else if (eventType == 2) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.CONTROLLER);
							}
							controllerNumber = controllerNumber + packet.buffer[currentPosition++] & 0x7F;
							packet2.writeByte(controllerNumber);
							@Pc(772) byte controllerDelta;
							if (controllerNumber == MidiController.BANK_SELECT_COARSE || controllerNumber == MidiController.BANK_SELECT_FINE) {
								controllerDelta = packet.buffer[programChangeOffset++];
							} else if (controllerNumber == MidiController.MODULATION_WHEEL_COARSE) {
								controllerDelta = packet.buffer[modWheelCoarseOffset++];
							} else if (controllerNumber == MidiController.MODULATION_WHEEL_FINE) {
								controllerDelta = packet.buffer[modWheelFineOffset++];
							} else if (controllerNumber == MidiController.MAIN_VOLUME_COARSE) {
								controllerDelta = packet.buffer[volumeCoarseOffset++];
							} else if (controllerNumber == MidiController.MAIN_VOLUME_FINE) {
								controllerDelta = packet.buffer[volumeFineOffset++];
							} else if (controllerNumber == MidiController.PAN_COARSE) {
								controllerDelta = packet.buffer[panCoarseOffset++];
							} else if (controllerNumber == MidiController.PAN_FINE) {
								controllerDelta = packet.buffer[panFineOffset++];
							} else if (controllerNumber == MidiController.NON_REGISTERED_PARAM_FINE) {
								controllerDelta = packet.buffer[nrpnFineOffset++];
							} else if (controllerNumber == MidiController.NON_REGISTERED_PARAM_COARSE) {
								controllerDelta = packet.buffer[nrpnCoarseOffset++];
							} else if (controllerNumber == MidiController.REGISTERED_PARAM_FINE) {
								controllerDelta = packet.buffer[rpnFineOffset++];
							} else if (controllerNumber == MidiController.REGISTERED_PARAM_COARSE) {
								controllerDelta = packet.buffer[rpnCoarseOffset++];
							} else if (controllerNumber == MidiController.HOLD || controllerNumber == MidiController.PORTAMENTO_PEDAL || controllerNumber == MidiController.ALL_SOUND_OFF || controllerNumber == MidiController.ALL_CONTROLLERS_OFF || controllerNumber == MidiController.ALL_NOTES_OFF) {
								controllerDelta = packet.buffer[switchControllerOffset++];
							} else {
								controllerDelta = packet.buffer[otherControllerOffset++];
							}
							@Pc(907) int controllerValue = controllerDelta + controllerValues[controllerNumber];
							controllerValues[controllerNumber] = controllerValue;
							packet2.writeByte(controllerValue & 0x7F);
						} else if (eventType == 3) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.PITCH);
							}
							pitchBend += packet.buffer[pitchBendLowOffset++];
							pitchBend += packet.buffer[pitchBendHighOffset++] << 7;
							packet2.writeByte(pitchBend & 0x7F);
							packet2.writeByte(pitchBend >> 7 & 0x7F);
						} else if (eventType == 4) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.CHANNEL_AFTERTOUCH);
							}
							channelPressure += packet.buffer[channelPressureOffset++];
							packet2.writeByte(channelPressure & 0x7F);
						} else if (eventType == 5) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.NOTE_AFTERTOUCH);
							}
							noteKey += packet.buffer[noteKeyOffset++];
							keyPressure += packet.buffer[keyPressureOffset++];
							packet2.writeByte(noteKey & 0x7F);
							packet2.writeByte(keyPressure & 0x7F);
						} else if (eventType == 6) {
							if (eventTypeChanged) {
								packet2.writeByte(channel + MidiStatus.PROGRAM_CHANGE);
							}
							packet2.writeByte(packet.buffer[programChangeOffset++]);
						} else {
							throw new RuntimeException();
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!dl", name = "a", descriptor = "()V")
	public void buildPatchTable() {
		if (this.table != null) {
			return;
		}
		this.table = new IterableHashTable(16);
		@Pc(12) int[] bankSelects = new int[16];
		@Pc(15) int[] programs = new int[16];
		bankSelects[9] = programs[9] = 128;
		@Pc(29) MidiFileReader midiSequence = new MidiFileReader(this.midi);
		@Pc(32) int trackCount = midiSequence.getTrackCount();
		for (@Pc(34) int track = 0; track < trackCount; track++) {
			midiSequence.getGotoTrack(track);
			midiSequence.getTrackLength(track);
			midiSequence.markTracePosition(track);
		}
		label53: do {
			while (true) {
				@Pc(51) int track = midiSequence.getPrioritizedTrack();
				@Pc(56) int tick = midiSequence.trackLengths[track];
				while (midiSequence.trackLengths[track] == tick) {
					midiSequence.getGotoTrack(track);
					@Pc(64) int message = midiSequence.readMessage(track);
					if (message == 1) {
						midiSequence.setTrackDone();
						midiSequence.markTracePosition(track);
						continue label53;
					}
					@Pc(80) int status = message & 0xF0;
					@Pc(87) int channel;
					@Pc(93) int data1;
					@Pc(99) int data2;
					if (status == MidiStatus.CONTROLLER) {
						channel = message & Midi.CHANNEL_MASK;
						data1 = message >> 8 & 0x7F;
						data2 = message >> 16 & 0x7F;
						if (data1 == 0) {
							bankSelects[channel] = (bankSelects[channel] & 0xFFE03FFF) + (data2 << 14);
						}
						if (data1 == 32) {
							bankSelects[channel] = (bankSelects[channel] & 0xFFFFC07F) + (data2 << 7);
						}
					}
					if (status == MidiStatus.PROGRAM_CHANGE) {
						channel = message & Midi.CHANNEL_MASK;
						data1 = message >> 8 & 0x7F;
						programs[channel] = bankSelects[channel] + data1;
					}
					if (status == MidiStatus.NOTE_ON) {
						channel = message & Midi.CHANNEL_MASK;
						data1 = message >> 8 & 0x7F;
						data2 = message >> 16 & 0x7F;
						if (data2 > 0) {
							@Pc(174) int patchId = programs[channel];
							@Pc(182) MidiProgramNode midiProgramNode = (MidiProgramNode) this.table.get((long) patchId);
							if (midiProgramNode == null) {
								midiProgramNode = new MidiProgramNode(new byte[128]);
								this.table.put((long) patchId, midiProgramNode);
							}
							midiProgramNode.notes[data1] = 1;
						}
					}
					midiSequence.getTrackLength(track);
					midiSequence.markTracePosition(track);
				}
			}
		} while (!midiSequence.isDone());
	}

	@OriginalMember(owner = "client!dl", name = "b", descriptor = "()V")
	public void clear() {
		this.table = null;
	}
	@OriginalMember(owner = "client!dl", name = "a", descriptor = "(Lclient!in;II)Lclient!dl;")
	public static MusicTrack loadMusictrack(@OriginalArg(0) Js5ResourceProvider resource, @OriginalArg(1) int archiveId, @OriginalArg(2) int fileId) {
		@Pc(5) byte[] data = resource.getFile(archiveId, fileId);
		return data == null ? null : new MusicTrack(new Packet(data));
	}
}
