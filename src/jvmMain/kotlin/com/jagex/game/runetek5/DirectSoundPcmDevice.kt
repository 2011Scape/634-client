package com.jagex.game.runetek5

import com.ms.com.ComFailException
import com.ms.directX.DSBufferDesc
import com.ms.directX.DSCursors
import com.ms.directX.DirectSound
import com.ms.directX.DirectSoundBuffer
import com.ms.directX.WaveFormatEx
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ac")
class DirectSoundPcmDevice : PcmDevice {

	@OriginalMember(owner = "client!ac", name = "c", descriptor = "[I")
	private var silence: IntArray? = null

	@OriginalMember(owner = "client!ac", name = "f", descriptor = "I")
	private var channels: Int = 0

	@OriginalMember(owner = "client!ac", name = "g", descriptor = "I")
	private var sampleRate: Int = 0

	@OriginalMember(owner = "client!ac", name = "j", descriptor = "I")
	private var frameBytes: Int = 0

	@OriginalMember(owner = "client!ac", name = "d", descriptor = "[Lcom/ms/directX/DSCursors;")
	private val cursors: Array<DSCursors?> = arrayOfNulls<DSCursors>(2)

	@OriginalMember(owner = "client!ac", name = "a", descriptor = "[Lcom/ms/directX/DSBufferDesc;")
	private val bufferDescs: Array<DSBufferDesc?> = arrayOfNulls<DSBufferDesc>(2)

	@OriginalMember(owner = "client!ac", name = "e", descriptor = "[Z")
	private val playing: BooleanArray = BooleanArray(2)

	@OriginalMember(owner = "client!ac", name = "b", descriptor = "[Lcom/ms/directX/DirectSoundBuffer;")
	private val buffers: Array<DirectSoundBuffer?> = arrayOfNulls<DirectSoundBuffer>(2)

	@OriginalMember(owner = "client!ac", name = "h", descriptor = "[[B")
	private val bytes: Array<ByteArray?> = arrayOfNulls<ByteArray>(2)

	@OriginalMember(owner = "client!ac", name = "i", descriptor = "[I")
	private val writePositions: IntArray = IntArray(2)

	@OriginalMember(owner = "client!ac", name = "m", descriptor = "[I")
	private val bufferSizes: IntArray = IntArray(2)

	@OriginalMember(owner = "client!ac", name = "k", descriptor = "Lcom/ms/directX/DirectSound;")
	private val directSound: DirectSound = DirectSound()

	@OriginalMember(owner = "client!ac", name = "l", descriptor = "Lcom/ms/directX/WaveFormatEx;")
	private val waveFormat: WaveFormatEx = WaveFormatEx()

	@OriginalMember(owner = "client!ac", name = "<init>", descriptor = "()V")
	@Throws(Exception::class)
	constructor() {
		for (@Pc(41) i in 0 until 2) {
			this.bufferDescs[i] = DSBufferDesc()
		}
		for (@Pc(57) j in 0 until 2) {
			this.cursors[j] = DSCursors()
		}
	}

	@OriginalMember(owner = "client!ac", name = "b", descriptor = "(BI)V")
	override fun discardBuffer(@OriginalArg(1) index: Int) {
		this.buffers[index]!!.stop()
		this.playing[index] = false
		this.buffers[index]!!.setCurrentPosition(0)
		this.writePositions[index] = 0
	}

	@OriginalMember(owner = "client!ac", name = "a", descriptor = "(IB)V")
	override fun close(@OriginalArg(0) index: Int) {
		if (this.buffers[index] == null) {
			return
		}
		try {
			this.buffers[index]!!.stop()
		} catch (@Pc(11) ignored: ComFailException) {
		}
		this.buffers[index] = null
	}

	@OriginalMember(owner = "client!ac", name = "a", descriptor = "(I[I)V")
	override fun write(@OriginalArg(0) index: Int, @OriginalArg(1) samples: IntArray?) {
		@Pc(2) val count: Int = samples!!.size
		if (count != this.channels * 256) {
			throw IllegalArgumentException()
		}
		@Pc(20) val offset: Int = this.writePositions[index] * this.frameBytes
		for (@Pc(22) sampleIndex in 0 until count) {
			@Pc(27) var sample: Int = samples[sampleIndex]
			if (((sample + 8388608) and 0xFF000000.toInt()) != 0) {
				sample = (sample shr 31) xor 0x7FFFFF
			}
			this.bytes[index]!![offset + sampleIndex * 2] = (sample shr 8).toByte()
			this.bytes[index]!![offset + sampleIndex * 2 + 1] = (sample shr 16).toByte()
		}
		this.buffers[index]!!.writeBuffer(offset, count * 2, this.bytes[index], 0)
		this.writePositions[index] = (this.writePositions[index] + count / this.channels) and 0xFFFF
		if (!this.playing[index]) {
			this.buffers[index]!!.play(1)
			this.playing[index] = true
		}
	}

	@OriginalMember(owner = "client!ac", name = "a", descriptor = "(BI)I")
	override fun position(@OriginalArg(1) index: Int): Int {
		if (!this.playing[index]) {
			return 0
		}
		this.buffers[index]!!.getCurrentPosition(this.cursors[index])
		@Pc(29) val playPosition: Int = this.cursors[index]!!.write / this.frameBytes
		@Pc(39) var queued: Int = (this.writePositions[index] - playPosition) and 0xFFFF
		if (this.bufferSizes[index] < queued) {
			@Pc(59) var gap: Int = (playPosition - this.writePositions[index]) and 0xFFFF
			while (gap > 0) {
				this.write(index, this.silence)
				gap -= 256
			}
			queued = (this.writePositions[index] - playPosition) and 0xFFFF
		}
		return queued
	}

	@OriginalMember(owner = "client!ac", name = "a", descriptor = "(IZLjava/awt/Component;I)V")
	@Throws(Exception::class)
	override fun init(@OriginalArg(0) sampleRate: Int, @OriginalArg(1) stereo: Boolean, @OriginalArg(2) component: java.awt.Component?) {
		if (this.sampleRate != 0) {
			return
		}
		if (sampleRate < 8000 || sampleRate > 48000) {
			throw IllegalArgumentException()
		}
		this.channels = if (stereo) 2 else 1
		this.frameBytes = if (stereo) 4 else 2
		this.silence = IntArray(this.channels * 256)
		this.directSound.initialize(null)
		this.directSound.setCooperativeLevel(component, 2)
		for (@Pc(53) index in 0 until 2) {
			this.bufferDescs[index]!!.flags = 16384
		}
		this.waveFormat.avgBytesPerSec = this.frameBytes * sampleRate
		this.waveFormat.formatTag = 1
		this.waveFormat.bitsPerSample = 16
		this.waveFormat.blockAlign = this.frameBytes
		this.waveFormat.channels = this.channels
		this.sampleRate = sampleRate
		this.waveFormat.samplesPerSec = sampleRate
	}

	@OriginalMember(owner = "client!ac", name = "a", descriptor = "(IIB)V")
	@Throws(Exception::class)
	override fun open(@OriginalArg(0) bufferSize: Int, @OriginalArg(1) index: Int) {
		if (this.sampleRate == 0 || this.buffers[index] != null) {
			throw IllegalStateException()
		}
		@Pc(16) val byteCount: Int = this.frameBytes * 65536
		if (this.bytes[index] == null || byteCount != this.bytes[index]!!.size) {
			this.bytes[index] = ByteArray(byteCount)
			this.bufferDescs[index]!!.bufferBytes = byteCount
		}
		this.buffers[index] = this.directSound.createSoundBuffer(this.bufferDescs[index], this.waveFormat)
		this.playing[index] = false
		this.writePositions[index] = 0
		this.bufferSizes[index] = bufferSize
	}
}
