package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.sound.jagtheora.OggKateDecoder
import com.jagex.game.runetek5.sound.jagtheora.VorbisPcmStream
import jagtheora.ogg.OggPacket
import jagtheora.ogg.OggPage
import jagtheora.ogg.OggStreamState
import jagtheora.ogg.OggSyncState
import java.io.IOException
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.NativeLibraryOwner
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!jfa")
abstract class JagTheoraManager {

	@OriginalMember(owner = "client!jfa", name = "f", descriptor = "Z")
	private var hasBufferedPage: Boolean = false

	@OriginalMember(owner = "client!jfa", name = "n", descriptor = "Z")
	private var isEndOfStream: Boolean = false

	@OriginalMember(owner = "client!jfa", name = "v", descriptor = "Lclient!ts;")
	private var kateDecoder: OggKateDecoder? = null

	@OriginalMember(owner = "client!jfa", name = "x", descriptor = "Lclient!naa;")
	private var theoraDecoder: JagTheoraTheoraNode? = null

	@OriginalMember(owner = "client!jfa", name = "C", descriptor = "Z")
	private var isPaused: Boolean = false

	@OriginalMember(owner = "client!jfa", name = "D", descriptor = "Z")
	private var isShutdown: Boolean = false

	@OriginalMember(owner = "client!jfa", name = "E", descriptor = "Lclient!jm;")
	private var vorbisDecoder: JagTheoraVorbisNode? = null

	@OriginalMember(owner = "client!jfa", name = "F", descriptor = "Ljava/lang/String;")
	private var languageAbbreviation: String? = null

	@OriginalMember(owner = "client!jfa", name = "r", descriptor = "[B")
	private val buffer: ByteArray

	@OriginalMember(owner = "client!jfa", name = "j", descriptor = "Lclient!jagtheora/ogg/OggSyncState;")
	private val syncState: OggSyncState

	@OriginalMember(owner = "client!jfa", name = "d", descriptor = "Lclient!jagtheora/ogg/OggPage;")
	private val page: OggPage

	@OriginalMember(owner = "client!jfa", name = "p", descriptor = "Lclient!jagtheora/ogg/OggPacket;")
	private val packet: OggPacket

	@OriginalMember(owner = "client!jfa", name = "H", descriptor = "Lclient!eq;")
	private val cache: IterableHashTable

	@OriginalMember(owner = "client!jfa", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) bufferSize: Int) {
		if (!NativeLibraryOwner.loadLibrary("jagtheora")) {
			throw RuntimeException("Failed to load jagtheora library")
		}
		this.buffer = ByteArray(bufferSize)
		this.syncState = OggSyncState()
		this.page = OggPage()
		this.packet = OggPacket()
		this.cache = IterableHashTable(8)
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(I)V")
	private fun tickSubtitles() {
		@Pc(17) var stream: OggStreamDecoder? = this.cache.first() as OggStreamDecoder?
		while (stream != null) {
			if (stream is OggKateDecoder) {
				@Pc(25) val kateStream: OggKateDecoder = stream
				while ((kateStream.packetCount <= 8 || this.getTime() > kateStream.getEndTime().toDouble()) && kateStream.streamState!!.packetOut(this.packet) == 1) {
					kateStream.processPacket(this.packet)
				}
			}
			stream = this.cache.next() as OggStreamDecoder?
		}
		this.setLanguage(this.languageAbbreviation)
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(B)V")
	@Throws(IOException::class)
	private fun tickVideo() {
		while (this.theoraDecoder!!.streamState!!.packetOut(this.packet) != 1) {
			@Pc(11) val stream: OggStreamDecoder? = this.readNextPage()
			if (stream == null) {
				if (this.isEndOfStream) {
					this.handle()
				}
				return
			}
			if (stream === this.kateDecoder) {
				this.tickSubtitles()
			}
		}
		this.theoraDecoder!!.processPacket(this.packet)
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(Z)Lclient!naa;")
	fun getTheoraDecoder(): JagTheoraTheoraNode? {
		return this.theoraDecoder
	}

	@OriginalMember(owner = "client!jfa", name = "b", descriptor = "(Z)Z")
	private fun isVideoBehind(): Boolean {
		if (this.vorbisDecoder == null) {
			@Pc(36) val frameRate: Double = this.theoraDecoder!!.frameRate().toDouble()
			return frameRate == 0.0 || Utils.currentTimeMillis().toDouble() >= 1000.0 / frameRate + this.theoraDecoder!!.getLastDecodeTime().toDouble()
		} else {
			return !this.theoraDecoder!!.isSetup() || this.getTime() > this.theoraDecoder!!.getTime()
		}
	}

	@OriginalMember(owner = "client!jfa", name = "b", descriptor = "(I)V")
	fun stop() {
		if (this.isShutdown) {
			return
		}
		@Pc(15) var stream: OggStreamDecoder? = this.cache.first() as OggStreamDecoder?
		while (stream != null) {
			stream.stop()
			stream.streamState!!.a()
			stream = this.cache.next() as OggStreamDecoder?
		}
		this.packet.a()
		this.page.a()
		this.syncState.a()
		this.isShutdown = true
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(ILjava/lang/String;)V")
	fun setLanguage(@OriginalArg(1) string: String?) {
		this.languageAbbreviation = string
		if (this.languageAbbreviation == null) {
			this.kateDecoder = null
			return
		}
		if (this.kateDecoder != null && !this.languageAbbreviation!!.equals(this.kateDecoder!!.getLanguage())) {
			this.kateDecoder = null
		}
		if (this.kateDecoder != null) {
			return
		}
		@Pc(43) var stream: OggStreamDecoder? = this.cache.first() as OggStreamDecoder?
		while (stream != null) {
			if (stream is OggKateDecoder) {
				@Pc(51) val kateStream: OggKateDecoder = stream
				if (this.languageAbbreviation!!.equals(kateStream.getLanguage())) {
					this.kateDecoder = kateStream
					return
				}
			}
			stream = this.cache.next() as OggStreamDecoder?
		}
	}

	@OriginalMember(owner = "client!jfa", name = "b", descriptor = "(B)Z")
	fun isPlaybackComplete(): Boolean {
		if (this.isShutdown || this.isEndOfStream) {
			return this.vorbisDecoder == null || this.vorbisDecoder!!.getBufferedSampleCount() <= 0
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!jfa", name = "c", descriptor = "(I)V")
	private fun handle() {
		@Pc(11) var stream: OggStreamDecoder? = this.cache.first() as OggStreamDecoder?
		while (stream != null) {
			if (stream !== this.theoraDecoder) {
				while (stream.streamState!!.packetOut() == 1) {
					stream.processPacket(this.packet)
				}
			}
			stream = this.cache.next() as OggStreamDecoder?
		}
		if (this.theoraDecoder == null) {
			return
		}
		for (@Pc(53) tick in 0 until 10) {
			if (!this.isVideoBehind()) {
				return
			}
			if (this.theoraDecoder!!.streamState!!.packetOut() != 1) {
				this.stop()
				return
			}
			this.theoraDecoder!!.processPacket(this.packet)
		}
	}

	@OriginalMember(owner = "client!jfa", name = "d", descriptor = "(I)Lclient!jm;")
	fun getVorbisDecoder(): JagTheoraVorbisNode? {
		return this.vorbisDecoder
	}

	@OriginalMember(owner = "client!jfa", name = "e", descriptor = "(I)Lclient!ts;")
	fun getKateDecoder(): OggKateDecoder? {
		return this.kateDecoder
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(BZ)V")
	fun togglePause(@OriginalArg(1) isMuted: Boolean) {
		if (this.vorbisDecoder != null) {
			@Pc(11) val queueBuss: VorbisPcmStream? = this.vorbisDecoder!!.getPcmStream()
			if (queueBuss != null) {
				queueBuss.setPaused(isMuted)
			}
		}
		this.isPaused = !this.isPaused
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(B[B)I")
	@Throws(IOException::class)
	abstract fun readPage(@OriginalArg(1) buffer: ByteArray?): Int

	@OriginalMember(owner = "client!jfa", name = "f", descriptor = "(I)V")
	@Throws(IOException::class)
	fun tick() {
		if (this.isPaused) {
			return
		}
		while (!this.isShutdown) {
			@Pc(20) var stream: OggStreamDecoder?
			if (this.hasBufferedPage) {
				stream = this.cache.get(this.page.getSerialNumber().toLong()) as OggStreamDecoder?
			} else {
				stream = this.readNextPage()
				if (stream == null) {
					if (this.isEndOfStream) {
						this.handle()
					}
					return
				}
				if (stream == null) {
					throw IllegalStateException()
				}
				this.hasBufferedPage = true
			}
			if (this.vorbisDecoder === stream) {
				if (this.vorbisDecoder!!.getBufferedSampleCount() >= 50) {
					return
				}
				while (this.vorbisDecoder!!.streamState!!.packetOut(this.packet) == 1) {
					this.vorbisDecoder!!.processPacket(this.packet)
					this.tickSubtitles()
					if (this.theoraDecoder != null) {
						@Pc(80) val time: Double = this.theoraDecoder!!.getTime()
						@Pc(82) var tick: Int = 0
						while (tick < 10 && this.isVideoBehind()) {
							this.tickVideo()
							if (this.isShutdown) {
								return
							}
							tick++
						}
						if (this.theoraDecoder!!.getTime() > time) {
							return
						}
					}
					if (this.vorbisDecoder!!.getBufferedSampleCount() >= 50) {
						return
					}
				}
			} else if (stream is OggKateDecoder) {
				this.tickSubtitles()
			} else if (stream !== this.theoraDecoder) {
				while (stream!!.streamState!!.packetOut(this.packet) == 1) {
					if (stream.packetCount == 1 && stream is OggKateDecoder) {
						this.setLanguage(this.languageAbbreviation)
					}
					stream.processPacket(this.packet)
				}
			} else if (this.vorbisDecoder == null && !this.isPaused) {
				@Pc(143) var i: Int = 0
				while (i < 10 && this.isVideoBehind()) {
					this.tickVideo()
					if (this.isShutdown) {
						return
					}
					i++
				}
				return
			}
			this.hasBufferedPage = false
		}
	}

	@OriginalMember(owner = "client!jfa", name = "g", descriptor = "(I)D")
	fun getTime(): Double {
		if (this.vorbisDecoder == null) {
			return if (this.theoraDecoder == null) 0.0 else this.theoraDecoder!!.getTime()
		} else {
			return this.vorbisDecoder!!.getTime()
		}
	}

	@OriginalMember(owner = "client!jfa", name = "h", descriptor = "(I)Lclient!au;")
	@Throws(IOException::class)
	private fun readNextPage(): OggStreamDecoder? {
		if (this.isShutdown) {
			throw IllegalStateException()
		} else if (this.isEndOfStream) {
			return null
		} else {
			@Pc(23) val local23: Int
			while (this.syncState.pageOut(this.page) <= 0) {
				val bytesRead: Int = this.readPage(this.buffer)
				if (bytesRead == -1) {
					this.isEndOfStream = true
					return null
				}
				if (bytesRead == 0) {
					return null
				}
				if (!this.syncState.write(this.buffer, bytesRead)) {
					throw RuntimeException("")
				}
			}
			val serialNumber: Int = this.page.getSerialNumber()
			if (!this.page.isBOS()) {
				@Pc(79) val decoder: OggStreamDecoder? = this.cache.get(serialNumber.toLong()) as OggStreamDecoder?
				if (!decoder!!.streamState!!.pageIn(this.page)) {
					throw IllegalStateException()
				}
				return decoder
			}
			@Pc(96) val oggStreamState: OggStreamState = OggStreamState(serialNumber)
			if (!oggStreamState.pageIn(this.page)) {
				throw IllegalStateException()
			} else if (oggStreamState.packetPeek(this.packet) == 1) {
				@Pc(131) val newDecoder: OggStreamDecoder
				if (this.theoraDecoder == null && this.packet.isTheora()) {
					this.theoraDecoder = JagTheoraTheoraNode(oggStreamState)
					newDecoder = this.theoraDecoder!!
				} else if (this.vorbisDecoder == null && this.packet.isVorbis()) {
					this.vorbisDecoder = JagTheoraVorbisNode(oggStreamState)
					newDecoder = this.vorbisDecoder!!
				} else {
					@Pc(155) val packetData: ByteArray = this.packet.getData()
					@Pc(159) val stringBuffer: StringBuffer = StringBuffer()
					@Pc(161) var i: Int = 1
					while (packetData.size > i && Character.isLetterOrDigit(packetData[i].toInt().toChar())) {
						stringBuffer.append(packetData[i].toInt().toChar())
						i++
					}
					@Pc(184) val string: String = stringBuffer.toString()
					if (string == "kate") {
						newDecoder = OggKateDecoder(oggStreamState)
					} else {
						newDecoder = OggOtherStream(oggStreamState)
					}
				}
				this.cache.put(serialNumber.toLong(), newDecoder)
				return newDecoder
			} else {
				throw IllegalStateException()
			}
		}
	}

	companion object {
		@OriginalMember(owner = "client!iea", name = "n", descriptor = "Z")
		@JvmField
		var jagtheoraLoaded: Boolean = false
	}
}
