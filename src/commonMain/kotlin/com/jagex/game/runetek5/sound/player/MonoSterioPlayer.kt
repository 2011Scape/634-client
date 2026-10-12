package com.jagex.game.runetek5.sound.player

import com.jagex.game.runetek5.config.rsinterface.Component
import sound.AudioFormat
import sound.AudioSystem
import sound.DataLineInfo
import sound.MixerInfo
import sound.LineUnavailableException
import sound.SourceDataLine
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmStatic
import util.Locale
import lang.jClass
import util.lowercase

@OriginalClass("client!bha")
class MonoSterioPlayer : PcmPlayer() {

	@OriginalMember(owner = "client!bha", name = "F", descriptor = "I")
	private var bufferLength: Int = 0

	@OriginalMember(owner = "client!bha", name = "G", descriptor = "[B")
	private var buffer: ByteArray? = null

	@OriginalMember(owner = "client!bha", name = "H", descriptor = "Ljavax/sound/sampled/SourceDataLine;")
	private var sourceDataLine: SourceDataLine? = null

	@OriginalMember(owner = "client!bha", name = "J", descriptor = "Ljavax/sound/sampled/AudioFormat;")
	private var audioFormat: AudioFormat? = null

	@OriginalMember(owner = "client!bha", name = "I", descriptor = "Z")
	private var hasSoundmax: Boolean = false

	@OriginalMember(owner = "client!bha", name = "c", descriptor = "()V")
	override fun close() {
		if (this.sourceDataLine != null) {
			this.sourceDataLine!!.close()
			this.sourceDataLine = null
		}
	}

	@OriginalMember(owner = "client!bha", name = "a", descriptor = "(I)V")
	@Throws(LineUnavailableException::class)
	override fun play(@OriginalArg(0) bufferLength: Int) {
		try {
			@Pc(20) val info: DataLineInfo = DataLineInfo(this.audioFormat, bufferLength shl (if (PcmPlayerManager.stereo) 2 else 1))
			this.sourceDataLine = AudioSystem.getLine(info) as SourceDataLine?
			this.sourceDataLine!!.open()
			this.sourceDataLine!!.start()
			this.bufferLength = bufferLength
		} catch (@Pc(36) ex: LineUnavailableException) {
			if (MonoSterioPlayer.bitCount(bufferLength) == 1) {
				this.sourceDataLine = null
				throw ex
			} else {
				this.play(Utils.nextPowerOfTwo(bufferLength))
			}
		}
	}

	@OriginalMember(owner = "client!bha", name = "a", descriptor = "(Ljava/awt/Component;)V")
	override fun init(@OriginalArg(0) component: awt.Component?) {
		@Pc(1) val infos: Array<MixerInfo>? = AudioSystem.getMixerInfo()
		if (infos != null) {
			@Pc(8) var i: Int = 0
			while (i < infos.size) {
				@Pc(16) val info: MixerInfo? = infos[i]
				if (info != null) {
					@Pc(21) val string: String? = info.getName()
					if (string != null && string.lowercase(util.defaultLocale()).indexOf("soundmax") >= 0) {
						this.hasSoundmax = true
					}
				}
				i++
			}
		}
		this.audioFormat = AudioFormat(PcmPlayerManager.sampleRate.toFloat(), 16, if (PcmPlayerManager.stereo) 2 else 1, true, false)
		this.buffer = ByteArray(0x100 shl (if (PcmPlayerManager.stereo) 2 else 1))
	}

	@OriginalMember(owner = "client!bha", name = "a", descriptor = "()I")
	override fun dataLeft(): Int {
		return this.bufferLength - (this.sourceDataLine!!.available() shr (if (PcmPlayerManager.stereo) 2 else 1))
	}

	@OriginalMember(owner = "client!bha", name = "b", descriptor = "()V")
	@Throws(LineUnavailableException::class)
	override fun open() {
		this.sourceDataLine!!.flush()
		if (!this.hasSoundmax) {
			return
		}
		this.sourceDataLine!!.close()
		this.sourceDataLine = null
		@Pc(34) val info: DataLineInfo = DataLineInfo(this.audioFormat, this.bufferLength shl (if (PcmPlayerManager.stereo) 2 else 1))
		this.sourceDataLine = AudioSystem.getLine(info) as SourceDataLine?
		this.sourceDataLine!!.open()
		this.sourceDataLine!!.start()
	}

	@OriginalMember(owner = "client!bha", name = "d", descriptor = "()V")
	override fun writeToBuffer() {
		@Pc(1) var sampleCount: Short = 256
		if (PcmPlayerManager.stereo) {
			sampleCount = 512
		}
		@Pc(9) var i: Int = 0
		while (i < sampleCount) {
			@Pc(17) var sample: Int = this.bitpackedSoundData!![i]
			if (((sample + 8388608) and 0xFF000000.toInt()) != 0) {
				sample = (sample shr 31) xor 0x7FFFFF
			}
			this.buffer!![i * 2] = (sample shr 8).toByte()
			this.buffer!![i * 2 + 1] = (sample shr 16).toByte()
			i++
		}
		this.sourceDataLine!!.write(this.buffer, 0, sampleCount.toInt() shl 1)
	}

	companion object {
		@OriginalMember(owner = "client!er", name = "a", descriptor = "(IB)I")
		@JvmStatic
		fun bitCount(@OriginalArg(0) value: Int): Int {
			@Pc(14) val pairSums: Int = (value and 0x55555555) + ((value and 0xAAAAAAAB.toInt()) ushr 1)
			@Pc(28) val nibbleSums: Int = ((pairSums ushr 2) and 0x33333333) + (pairSums and 0x33333333)
			@Pc(36) val byteSums: Int = ((nibbleSums ushr 4) + nibbleSums) and 0xF0F0F0F
			@Pc(42) val shortSums: Int = byteSums + (byteSums ushr 8)
			@Pc(48) val total: Int = shortSums + (shortSums ushr 16)
			return total and 0xFF
		}
	}
}
