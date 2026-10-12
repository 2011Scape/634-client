package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.sound.jagtheora.OggStreamDecoder
import com.jagex.game.runetek5.buffer.Packet
import jagtheora.ogg.OggPacket
import jagtheora.ogg.OggStreamState
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!ts")
class OggKateDecoder : OggStreamDecoder {

	@OriginalMember(owner = "client!ts", name = "x", descriptor = "F")
	private var endTime: Float = 0.0f

	@OriginalMember(owner = "client!ts", name = "A", descriptor = "Ljava/lang/String;")
	private var languageAbbreviation: String? = null

	@OriginalMember(owner = "client!ts", name = "D", descriptor = "I")
	private var granuleRateNumber: Int = 0

	@OriginalMember(owner = "client!ts", name = "E", descriptor = "I")
	private var granuleRateDenominator: Int = 0

	@OriginalMember(owner = "client!ts", name = "G", descriptor = "Ljava/lang/String;")
	private var category: String? = null

	@OriginalMember(owner = "client!ts", name = "H", descriptor = "Ljava/lang/String;")
	private var subtitleText: String? = null

	@OriginalMember(owner = "client!ts", name = "L", descriptor = "F")
	private var startTime: Float = 0.0f

	@OriginalMember(owner = "client!ts", name = "<init>", descriptor = "(Lclient!jagtheora/ogg/OggStreamState;)V")
	constructor(@OriginalArg(0) oggStreamState: OggStreamState?) : super(oggStreamState)

	@OriginalMember(owner = "client!ts", name = "a", descriptor = "(B)V")
	override fun stop() {
	}

	@OriginalMember(owner = "client!ts", name = "b", descriptor = "(B)F")
	fun getStartTime(): Float {
		return this.startTime
	}

	@OriginalMember(owner = "client!ts", name = "e", descriptor = "(B)F")
	fun getEndTime(): Float {
		return this.endTime
	}

	@OriginalMember(owner = "client!ts", name = "a", descriptor = "(BLclient!jagtheora/ogg/OggPacket;)V")
	override fun decode(@OriginalArg(1) oggPacket: OggPacket?) {
		if (this.packetCount > 0 && "SUB" != this.category) {
			return
		}
		@Pc(21) val packet: Packet = Packet(oggPacket!!.getData())
		@Pc(25) val packetType: Int = packet.readUByte()
		if (this.packetCount <= 8) {
			if ((packetType or 0x80) == 0) {
				throw IllegalStateException()
			}
			if (this.packetCount == 0) {
				packet.currentPosition += 23
				this.granuleRateDenominator = packet.readUIntLittle()
				this.granuleRateNumber = packet.readUIntLittle()
				if (this.granuleRateDenominator == 0 || this.granuleRateNumber == 0) {
					throw IllegalStateException()
				}
				@Pc(77) val packet2: Packet = Packet(16)
				packet.readBytes(0, 16, packet2.buffer)
				this.languageAbbreviation = packet2.readString()
				packet2.currentPosition = 0
				packet.readBytes(0, 16, packet2.buffer)
				this.category = packet2.readString()
			}
			return
		}
		if (packetType == 0) {
			@Pc(112) val startGranulePos: Long = packet.readULongLittle()
			@Pc(116) val duration: Long = packet.readULongLittle()
			@Pc(120) val backlink: Long = packet.readULongLittle()
			if (startGranulePos < 0L || duration < 0L || backlink < 0L || backlink > startGranulePos) {
				throw IllegalStateException()
			}
			this.startTime = (this.granuleRateNumber.toLong() * startGranulePos).toFloat() / this.granuleRateDenominator.toFloat()
			this.endTime = ((startGranulePos + duration) * this.granuleRateNumber.toLong()).toFloat() / this.granuleRateDenominator.toFloat()
			@Pc(177) val textLength: Int = packet.readUIntLittle()
			if (textLength < 0 || packet.buffer!!.size - packet.currentPosition < textLength) {
				throw IllegalStateException()
			}
			this.subtitleText = Utils.decodeUtf8(packet.buffer, packet.currentPosition, textLength)
		}
		if ((packetType or 0x80) != 0) {
			return
		}
	}

	@OriginalMember(owner = "client!ts", name = "b", descriptor = "(I)Ljava/lang/String;")
	fun getLanguage(): String? {
		return this.languageAbbreviation
	}

	@OriginalMember(owner = "client!ts", name = "d", descriptor = "(I)Ljava/lang/String;")
	fun getSubtitle(): String? {
		return this.subtitleText
	}
}
