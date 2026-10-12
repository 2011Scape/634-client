package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.sound.midi.Midi
import kotlin.jvm.JvmField

@OriginalClass("client!rf")
class MidiFileReader {

	@OriginalMember(owner = "client!rf", name = "c", descriptor = "[I")
	private var runningStatus: IntArray? = null

	@OriginalMember(owner = "client!rf", name = "d", descriptor = "[I")
	private var trackStarts: IntArray? = null

	@OriginalMember(owner = "client!rf", name = "e", descriptor = "J")
	private var tempoTimeOffset: Long = 0L

	@OriginalMember(owner = "client!rf", name = "f", descriptor = "[I")
	@JvmField
	var trackLengths: IntArray? = null

	@OriginalMember(owner = "client!rf", name = "g", descriptor = "[I")
	private var startPositionTrackBuffer: IntArray? = null

	@OriginalMember(owner = "client!rf", name = "h", descriptor = "I")
	@JvmField
	var division: Int = 0

	@OriginalMember(owner = "client!rf", name = "i", descriptor = "I")
	private var microsecondsPerBeat: Int = 0

	@OriginalMember(owner = "client!rf", name = "b", descriptor = "Lclient!cea;")
	private val buffer: Packet = Packet(null as ByteArray?)

	@OriginalMember(owner = "client!rf", name = "<init>", descriptor = "()V")
	constructor() {
	}

	@OriginalMember(owner = "client!rf", name = "<init>", descriptor = "([B)V")
	constructor(@OriginalArg(0) bytes: ByteArray?) {
		this.decode(bytes)
	}

	@OriginalMember(owner = "client!rf", name = "a", descriptor = "()Z")
	fun isDone(): Boolean {
		@Pc(3) val count: Int = this.startPositionTrackBuffer!!.size
		for (@Pc(5) i in 0 until count) {
			if (this.startPositionTrackBuffer!![i] >= 0) {
				return false
			}
		}
		return true
	}

	@OriginalMember(owner = "client!rf", name = "a", descriptor = "(II)I")
	private fun nextEventData(@OriginalArg(0) track: Int, @OriginalArg(1) event: Int): Int {
		@Pc(12) val local12: Int
		if (event != 255) {
			@Pc(78) val dataByteCount: Byte = MidiFileReader.messageDataByteCount[event - 128]
			var eventData: Int = event
			if (dataByteCount >= 1) {
				eventData = event or (this.buffer.readUByte() shl 8)
			}
			if (dataByteCount >= 2) {
				eventData = eventData or (this.buffer.readUByte() shl 16)
			}
			return eventData
		}
		@Pc(7) val metaType: Int = this.buffer.readUByte()
		var metaLength: Int = this.buffer.readVarInt()
		if (metaType == 47) {
			this.buffer.currentPosition += metaLength
			return 1
		} else if (metaType == 81) {
			@Pc(32) val newMicrosecondsPerBeat: Int = this.buffer.readUMedium()
			metaLength -= 3
			@Pc(38) val tick: Int = this.trackLengths!![track]
			this.tempoTimeOffset += tick.toLong() * (this.microsecondsPerBeat - newMicrosecondsPerBeat).toLong()
			this.microsecondsPerBeat = newMicrosecondsPerBeat
			this.buffer.currentPosition += metaLength
			return 2
		} else {
			this.buffer.currentPosition += metaLength
			return 3
		}
	}

	@OriginalMember(owner = "client!rf", name = "a", descriptor = "([B)V")
	fun decode(@OriginalArg(0) data: ByteArray?) {
		this.buffer.buffer = data
		this.buffer.currentPosition = 10
		@Pc(12) val trackCount: Int = this.buffer.readUShort()
		this.division = this.buffer.readUShort()
		this.microsecondsPerBeat = 500000
		this.trackStarts = IntArray(trackCount)
		@Pc(27) var i: Int = 0
		@Pc(33) var id: Int
		while (i < trackCount) {
			id = this.buffer.readInt()
			@Pc(38) val len: Int = this.buffer.readInt()
			if (id == Midi.TRACK_HEADER) {
				this.trackStarts!![i] = this.buffer.currentPosition
				i++
			}
			this.buffer.currentPosition += len
		}
		this.tempoTimeOffset = 0L
		this.startPositionTrackBuffer = IntArray(trackCount)
		id = 0
		while (id < trackCount) {
			this.startPositionTrackBuffer!![id] = this.trackStarts!![id]
			id++
		}
		this.trackLengths = IntArray(trackCount)
		this.runningStatus = IntArray(trackCount)
	}

	@OriginalMember(owner = "client!rf", name = "b", descriptor = "()I")
	fun getTrackCount(): Int {
		return this.startPositionTrackBuffer!!.size
	}

	@OriginalMember(owner = "client!rf", name = "a", descriptor = "(J)V")
	fun resetTracksFromTimestamp(@OriginalArg(0) timestamp: Long) {
		this.tempoTimeOffset = timestamp
		@Pc(6) val count: Int = this.startPositionTrackBuffer!!.size
		for (@Pc(8) i in 0 until count) {
			this.trackLengths!![i] = 0
			this.runningStatus!![i] = 0
			this.buffer.currentPosition = this.trackStarts!![i]
			this.getTrackLength(i)
			this.startPositionTrackBuffer!![i] = this.buffer.currentPosition
		}
	}

	@OriginalMember(owner = "client!rf", name = "a", descriptor = "(I)V")
	fun getTrackLength(@OriginalArg(0) track: Int) {
		@Pc(4) val deltaTime: Int = this.buffer.readVarInt()
		this.trackLengths!![track] += deltaTime
	}

	@OriginalMember(owner = "client!rf", name = "b", descriptor = "(I)V")
	fun markTracePosition(@OriginalArg(0) track: Int) {
		this.startPositionTrackBuffer!![track] = this.buffer.currentPosition
	}

	@OriginalMember(owner = "client!rf", name = "c", descriptor = "()V")
	fun clear() {
		this.buffer.buffer = null
		this.trackStarts = null
		this.startPositionTrackBuffer = null
		this.trackLengths = null
		this.runningStatus = null
	}

	@OriginalMember(owner = "client!rf", name = "c", descriptor = "(I)J")
	fun getTickTimestamp(@OriginalArg(0) tickLength: Int): Long {
		return this.tempoTimeOffset + tickLength.toLong() * this.microsecondsPerBeat.toLong()
	}

	@OriginalMember(owner = "client!rf", name = "d", descriptor = "(I)I")
	private fun getNextEventInternal(@OriginalArg(0) track: Int): Int {
		@Pc(7) val statusByte: Byte = this.buffer.buffer!![this.buffer.currentPosition]
		@Pc(13) val status: Int
		if (statusByte < 0) {
			status = statusByte.toInt() and 0xFF
			this.runningStatus!![track] = status
			this.buffer.currentPosition++
		} else {
			status = this.runningStatus!![track]
		}
		if (status != 240 && status != 247) {
			return this.nextEventData(track, status)
		}
		@Pc(42) val len: Int = this.buffer.readVarInt()
		if (status == 247 && len > 0) {
			@Pc(57) val status2: Int = this.buffer.buffer!![this.buffer.currentPosition].toInt() and 0xFF
			if (status2 >= 241 && status2 <= 243 || status2 == 246 || status2 == 248 || status2 >= 250 && status2 <= 252 || status2 == 254) {
				this.buffer.currentPosition++
				this.runningStatus!![track] = status2
				return this.nextEventData(track, status2)
			}
		}
		this.buffer.currentPosition += len
		return 0
	}

	@OriginalMember(owner = "client!rf", name = "d", descriptor = "()I")
	fun getPrioritizedTrack(): Int {
		@Pc(3) val count: Int = this.startPositionTrackBuffer!!.size
		@Pc(5) var track: Int = -1
		@Pc(7) var min: Int = Int.MAX_VALUE
		for (@Pc(9) i in 0 until count) {
			if (this.startPositionTrackBuffer!![i] >= 0 && this.trackLengths!![i] < min) {
				track = i
				min = this.trackLengths!![i]
			}
		}
		return track
	}

	@OriginalMember(owner = "client!rf", name = "e", descriptor = "()Z")
	fun dataLoaded(): Boolean {
		return this.buffer.buffer != null
	}

	@OriginalMember(owner = "client!rf", name = "e", descriptor = "(I)V")
	fun getGotoTrack(@OriginalArg(0) track: Int) {
		this.buffer.currentPosition = this.startPositionTrackBuffer!![track]
	}

	@OriginalMember(owner = "client!rf", name = "f", descriptor = "(I)I")
	fun readMessage(@OriginalArg(0) track: Int): Int {
		return this.getNextEventInternal(track)
	}

	@OriginalMember(owner = "client!rf", name = "g", descriptor = "()V")
	fun setTrackDone() {
		this.buffer.currentPosition = -1
	}

	companion object {
		@OriginalMember(owner = "client!rf", name = "a", descriptor = "[B")
		@JvmField
		val messageDataByteCount: ByteArray = byteArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
	}
}
