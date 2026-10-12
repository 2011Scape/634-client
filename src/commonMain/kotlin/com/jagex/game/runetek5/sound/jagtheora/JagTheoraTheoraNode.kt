package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.Toolkit
import jagtheora.ogg.OggPacket
import jagtheora.ogg.OggStreamState
import jagtheora.theora.DecoderContext
import jagtheora.theora.Frame
import jagtheora.theora.GranulePos
import jagtheora.theora.SetupInfo
import jagtheora.theora.TheoraComment
import jagtheora.theora.TheoraInfo
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!naa")
class JagTheoraTheoraNode : OggStreamDecoder {

	@OriginalMember(owner = "client!naa", name = "x", descriptor = "Z")
	private var skipUntilKeyFrame: Boolean = false

	@OriginalMember(owner = "client!naa", name = "y", descriptor = "I")
	private var currentPostProcessingLevel: Int = 0

	@OriginalMember(owner = "client!naa", name = "D", descriptor = "Z")
	private var frameDecoded: Boolean = false

	@OriginalMember(owner = "client!naa", name = "E", descriptor = "D")
	private var granuleTime: Double = 0.0

	@OriginalMember(owner = "client!naa", name = "F", descriptor = "Z")
	private var headerDecoded: Boolean = false

	@OriginalMember(owner = "client!naa", name = "H", descriptor = "J")
	private var packetDecodeTime: Long = 0L

	@OriginalMember(owner = "client!naa", name = "I", descriptor = "Lclient!jagtheora/theora/GranulePos;")
	private var granulePos: GranulePos? = null

	@OriginalMember(owner = "client!naa", name = "J", descriptor = "Lclient!mi;")
	private var frameSprite: NativeSprite? = null

	@OriginalMember(owner = "client!naa", name = "N", descriptor = "Lclient!jagtheora/theora/Frame;")
	private var frame: Frame? = null

	@OriginalMember(owner = "client!naa", name = "Q", descriptor = "I")
	private var maxPostProcessingLevel: Int = 0

	@OriginalMember(owner = "client!naa", name = "R", descriptor = "Z")
	private var keyFramesOnly: Boolean = false

	@OriginalMember(owner = "client!naa", name = "W", descriptor = "Lclient!jagtheora/theora/DecoderContext;")
	private var decoderContext: DecoderContext? = null

	@OriginalMember(owner = "client!naa", name = "Z", descriptor = "Lclient!jagtheora/theora/SetupInfo;")
	private val setupInfo: SetupInfo = SetupInfo()

	@OriginalMember(owner = "client!naa", name = "w", descriptor = "Lclient!jagtheora/theora/TheoraInfo;")
	private val info: TheoraInfo = TheoraInfo()

	@OriginalMember(owner = "client!naa", name = "z", descriptor = "Lclient!jagtheora/theora/TheoraComment;")
	private val comment: TheoraComment = TheoraComment()

	@OriginalMember(owner = "client!naa", name = "<init>", descriptor = "(Lclient!jagtheora/ogg/OggStreamState;)V")
	constructor(@OriginalArg(0) oggStreamState: OggStreamState?) : super(oggStreamState)

	@OriginalMember(owner = "client!naa", name = "a", descriptor = "(Lclient!ha;I)Lclient!mi;")
	fun getFrameSprite(@OriginalArg(0) toolkit: Toolkit?): NativeSprite? {
		if (this.frame == null) {
			return null
		} else if (this.frameDecoded || this.frameSprite == null) {
			this.frameSprite = toolkit!!.createNativeSprite(this.frame!!.pixels, this.frame!!.a, this.frame!!.a, this.frame!!.b, false)
			this.frameDecoded = false
			return this.frameSprite
		} else {
			return this.frameSprite
		}
	}

	@OriginalMember(owner = "client!naa", name = "a", descriptor = "(B)V")
	override fun stop() {
		if (this.frame != null) {
			this.frame!!.a()
		}
		if (this.decoderContext != null) {
			this.decoderContext!!.a()
			this.decoderContext = null
		}
		if (this.granulePos != null) {
			this.granulePos!!.a()
			this.granulePos = null
		}
		this.info.a()
		this.comment.a()
		this.setupInfo.a()
	}

	@OriginalMember(owner = "client!naa", name = "d", descriptor = "(I)Z")
	fun isSetup(): Boolean {
		return this.headerDecoded
	}

	@OriginalMember(owner = "client!naa", name = "e", descriptor = "(I)F")
	fun frameRate(): Float {
		return if (this.headerDecoded && !this.info.b()) this.info.fpsNumerator.toFloat() / this.info.fpsDenominator.toFloat() else 0.0f
	}

	@OriginalMember(owner = "client!naa", name = "a", descriptor = "(IB)V")
	private fun setPostProcessingLevel(@OriginalArg(0) level: Int) {
		this.currentPostProcessingLevel = level
		if (!this.headerDecoded) {
			return
		}
		if (this.currentPostProcessingLevel > this.maxPostProcessingLevel) {
			this.currentPostProcessingLevel = this.maxPostProcessingLevel
		}
		if (this.currentPostProcessingLevel < 0) {
			this.currentPostProcessingLevel = 0
		}
		this.decoderContext!!.setPostProcessingLevel(this.currentPostProcessingLevel)
	}

	@OriginalMember(owner = "client!naa", name = "f", descriptor = "(I)J")
	fun getLastDecodeTime(): Long {
		return this.packetDecodeTime
	}

	@OriginalMember(owner = "client!naa", name = "b", descriptor = "(B)D")
	fun getTime(): Double {
		return this.granuleTime
	}

	@OriginalMember(owner = "client!naa", name = "a", descriptor = "(BLclient!jagtheora/ogg/OggPacket;)V")
	override fun decode(@OriginalArg(1) oggPacket: OggPacket?) {
		@Pc(21) var result: Int
		if (!this.headerDecoded) {
			result = this.setupInfo.decodeHeader(this.info, this.comment, oggPacket)
			if (result == 0) {
				this.headerDecoded = true
				if (this.info.frameWidth > 2048 || this.info.frameHeight > 1024) {
					throw IllegalStateException()
				}
				this.decoderContext = DecoderContext(this.info, this.setupInfo)
				this.granulePos = GranulePos()
				this.frame = Frame(this.info.frameWidth, this.info.frameHeight)
				this.maxPostProcessingLevel = this.decoderContext!!.getMaxPostProcessingLevel()
				this.setPostProcessingLevel(this.currentPostProcessingLevel)
			} else if (result < 0) {
				throw IllegalStateException(result.toString())
			}
			return
		}
		this.packetDecodeTime = Utils.currentTimeMillis()
		result = this.decoderContext!!.decodePacketIn(oggPacket, this.granulePos)
		if (result < 0) {
			throw IllegalStateException(result.toString())
		}
		this.decoderContext!!.granuleFrame(this.granulePos)
		this.granuleTime = this.decoderContext!!.granuleTime(this.granulePos)
		if (this.skipUntilKeyFrame) {
			@Pc(53) val keyFrame: Boolean = oggPacket!!.isKeyFrame() == 1
			if (!keyFrame) {
				return
			}
			this.skipUntilKeyFrame = false
		}
		if (!this.keyFramesOnly || oggPacket!!.isKeyFrame() == 1) {
			if (this.decoderContext!!.decodeFrame(this.frame) != 0) {
				throw IllegalStateException(result.toString())
			}
			this.frameDecoded = true
		}
	}
}
