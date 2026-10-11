package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.sound.midi.Midi;

@OriginalClass("client!rf")
public final class MidiFileReader {

	@OriginalMember(owner = "client!rf", name = "c", descriptor = "[I")
	private int[] runningStatus;

	@OriginalMember(owner = "client!rf", name = "d", descriptor = "[I")
	private int[] trackStarts;

	@OriginalMember(owner = "client!rf", name = "e", descriptor = "J")
	private long tempoTimeOffset;

	@OriginalMember(owner = "client!rf", name = "f", descriptor = "[I")
	public int[] trackLengths;

	@OriginalMember(owner = "client!rf", name = "g", descriptor = "[I")
	private int[] startPositionTrackBuffer;

	@OriginalMember(owner = "client!rf", name = "h", descriptor = "I")
	public int division;

	@OriginalMember(owner = "client!rf", name = "i", descriptor = "I")
	private int microsecondsPerBeat;

	@OriginalMember(owner = "client!rf", name = "b", descriptor = "Lclient!cea;")
	private final Packet buffer = new Packet(null);

	@OriginalMember(owner = "client!rf", name = "<init>", descriptor = "()V")
	public MidiFileReader() {
	}

	@OriginalMember(owner = "client!rf", name = "<init>", descriptor = "([B)V")
	public MidiFileReader(@OriginalArg(0) byte[] bytes) {
		this.decode(bytes);
	}

	@OriginalMember(owner = "client!rf", name = "a", descriptor = "()Z")
	public boolean isDone() {
		@Pc(3) int count = this.startPositionTrackBuffer.length;
		for (@Pc(5) int i = 0; i < count; i++) {
			if (this.startPositionTrackBuffer[i] >= 0) {
				return false;
			}
		}
		return true;
	}

	@OriginalMember(owner = "client!rf", name = "a", descriptor = "(II)I")
	private int nextEventData(@OriginalArg(0) int track, @OriginalArg(1) int event) {
		@Pc(12) int local12;
		if (event != 255) {
			@Pc(78) byte dataByteCount = MidiFileReader.messageDataByteCount[event - 128];
			int eventData = event;
			if (dataByteCount >= 1) {
				eventData = event | this.buffer.readUByte() << 8;
			}
			if (dataByteCount >= 2) {
				eventData |= this.buffer.readUByte() << 16;
			}
			return eventData;
		}
		@Pc(7) int metaType = this.buffer.readUByte();
		int metaLength = this.buffer.readVarInt();
		if (metaType == 47) {
			this.buffer.currentPosition += metaLength;
			return 1;
		} else if (metaType == 81) {
			@Pc(32) int newMicrosecondsPerBeat = this.buffer.readUMedium();
			metaLength -= 3;
			@Pc(38) int tick = this.trackLengths[track];
			this.tempoTimeOffset += (long) tick * (long) (this.microsecondsPerBeat - newMicrosecondsPerBeat);
			this.microsecondsPerBeat = newMicrosecondsPerBeat;
			this.buffer.currentPosition += metaLength;
			return 2;
		} else {
			this.buffer.currentPosition += metaLength;
			return 3;
		}
	}

	@OriginalMember(owner = "client!rf", name = "a", descriptor = "([B)V")
	public void decode(@OriginalArg(0) byte[] data) {
		this.buffer.buffer = data;
		this.buffer.currentPosition = 10;
		@Pc(12) int trackCount = this.buffer.readUShort();
		this.division = this.buffer.readUShort();
		this.microsecondsPerBeat = 500000;
		this.trackStarts = new int[trackCount];
		@Pc(27) int i = 0;
		@Pc(33) int id;
		while (i < trackCount) {
			id = this.buffer.readInt();
			@Pc(38) int len = this.buffer.readInt();
			if (id == Midi.TRACK_HEADER) {
				this.trackStarts[i] = this.buffer.currentPosition;
				i++;
			}
			this.buffer.currentPosition += len;
		}
		this.tempoTimeOffset = 0L;
		this.startPositionTrackBuffer = new int[trackCount];
		for (id = 0; id < trackCount; id++) {
			this.startPositionTrackBuffer[id] = this.trackStarts[id];
		}
		this.trackLengths = new int[trackCount];
		this.runningStatus = new int[trackCount];
	}

	@OriginalMember(owner = "client!rf", name = "b", descriptor = "()I")
	public int getTrackCount() {
		return this.startPositionTrackBuffer.length;
	}

	@OriginalMember(owner = "client!rf", name = "a", descriptor = "(J)V")
	public void resetTracksFromTimestamp(@OriginalArg(0) long timestamp) {
		this.tempoTimeOffset = timestamp;
		@Pc(6) int count = this.startPositionTrackBuffer.length;
		for (@Pc(8) int i = 0; i < count; i++) {
			this.trackLengths[i] = 0;
			this.runningStatus[i] = 0;
			this.buffer.currentPosition = this.trackStarts[i];
			this.getTrackLength(i);
			this.startPositionTrackBuffer[i] = this.buffer.currentPosition;
		}
	}

	@OriginalMember(owner = "client!rf", name = "a", descriptor = "(I)V")
	public void getTrackLength(@OriginalArg(0) int track) {
		@Pc(4) int deltaTime = this.buffer.readVarInt();
		this.trackLengths[track] += deltaTime;
	}

	@OriginalMember(owner = "client!rf", name = "b", descriptor = "(I)V")
	public void markTracePosition(@OriginalArg(0) int track) {
		this.startPositionTrackBuffer[track] = this.buffer.currentPosition;
	}

	@OriginalMember(owner = "client!rf", name = "c", descriptor = "()V")
	public void clear() {
		this.buffer.buffer = null;
		this.trackStarts = null;
		this.startPositionTrackBuffer = null;
		this.trackLengths = null;
		this.runningStatus = null;
	}

	@OriginalMember(owner = "client!rf", name = "c", descriptor = "(I)J")
	public long getTickTimestamp(@OriginalArg(0) int tickLength) {
		return this.tempoTimeOffset + (long) tickLength * (long) this.microsecondsPerBeat;
	}

	@OriginalMember(owner = "client!rf", name = "d", descriptor = "(I)I")
	private int getNextEventInternal(@OriginalArg(0) int track) {
		@Pc(7) byte statusByte = this.buffer.buffer[this.buffer.currentPosition];
		@Pc(13) int status;
		if (statusByte < 0) {
			status = statusByte & 0xFF;
			this.runningStatus[track] = status;
			this.buffer.currentPosition++;
		} else {
			status = this.runningStatus[track];
		}
		if (status != 240 && status != 247) {
			return this.nextEventData(track, status);
		}
		@Pc(42) int len = this.buffer.readVarInt();
		if (status == 247 && len > 0) {
			@Pc(57) int status2 = this.buffer.buffer[this.buffer.currentPosition] & 0xFF;
			if (status2 >= 241 && status2 <= 243 || status2 == 246 || status2 == 248 || status2 >= 250 && status2 <= 252 || status2 == 254) {
				this.buffer.currentPosition++;
				this.runningStatus[track] = status2;
				return this.nextEventData(track, status2);
			}
		}
		this.buffer.currentPosition += len;
		return 0;
	}

	@OriginalMember(owner = "client!rf", name = "d", descriptor = "()I")
	public int getPrioritizedTrack() {
		@Pc(3) int count = this.startPositionTrackBuffer.length;
		@Pc(5) int track = -1;
		@Pc(7) int min = Integer.MAX_VALUE;
		for (@Pc(9) int i = 0; i < count; i++) {
			if (this.startPositionTrackBuffer[i] >= 0 && this.trackLengths[i] < min) {
				track = i;
				min = this.trackLengths[i];
			}
		}
		return track;
	}

	@OriginalMember(owner = "client!rf", name = "e", descriptor = "()Z")
	public boolean dataLoaded() {
		return this.buffer.buffer != null;
	}

	@OriginalMember(owner = "client!rf", name = "e", descriptor = "(I)V")
	public void getGotoTrack(@OriginalArg(0) int track) {
		this.buffer.currentPosition = this.startPositionTrackBuffer[track];
	}

	@OriginalMember(owner = "client!rf", name = "f", descriptor = "(I)I")
	public int readMessage(@OriginalArg(0) int track) {
		return this.getNextEventInternal(track);
	}

	@OriginalMember(owner = "client!rf", name = "g", descriptor = "()V")
	public void setTrackDone() {
		this.buffer.currentPosition = -1;
	}

	@OriginalMember(owner = "client!rf", name = "a", descriptor = "[B")
	public static final byte[] messageDataByteCount = new byte[] { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
}
