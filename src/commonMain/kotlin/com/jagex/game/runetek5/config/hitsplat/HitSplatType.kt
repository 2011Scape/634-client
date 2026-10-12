package com.jagex.game.runetek5.config.hitsplat

import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.SpriteDataProvider
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField

@OriginalClass("client!ia")
class HitSplatType {

	@OriginalMember(owner = "client!ia", name = "s", descriptor = "Lclient!sb;")
	@JvmField
	var typelist: HitSplatTypeList? = null

	@OriginalMember(owner = "client!ia", name = "g", descriptor = "I")
	@JvmField
	var fadeStartCycle: Int = -1

	@OriginalMember(owner = "client!ia", name = "j", descriptor = "I")
	private var spriteB: Int = -1

	@OriginalMember(owner = "client!ia", name = "d", descriptor = "Ljava/lang/String;")
	private var placementExampleString: String? = ""

	@OriginalMember(owner = "client!ia", name = "e", descriptor = "I")
	@JvmField
	var scrollOffsetX: Int = 0

	@OriginalMember(owner = "client!ia", name = "n", descriptor = "I")
	private var spriteC: Int = -1

	@OriginalMember(owner = "client!ia", name = "c", descriptor = "I")
	@JvmField
	var scrollOffsetY: Int = 0

	@OriginalMember(owner = "client!ia", name = "t", descriptor = "I")
	private var spriteD: Int = -1

	@OriginalMember(owner = "client!ia", name = "l", descriptor = "I")
	@JvmField
	var displayType: Int = -1

	@OriginalMember(owner = "client!ia", name = "o", descriptor = "I")
	@JvmField
	var cyclesVisible: Int = 70

	@OriginalMember(owner = "client!ia", name = "p", descriptor = "I")
	@JvmField
	var fontId: Int = -1

	@OriginalMember(owner = "client!ia", name = "b", descriptor = "I")
	private var spriteA: Int = -1

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "I")
	@JvmField
	var textOffsetY: Int = 0

	@OriginalMember(owner = "client!ia", name = "y", descriptor = "I")
	@JvmField
	var color: Int = 16777215

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(Lclient!ha;Z)Lclient!mi;")
	fun getInnerLeftSprite(@OriginalArg(0) toolkit: Toolkit?): NativeSprite? {
		if (this.spriteB < 0) {
			return null
		}
		@Pc(23) var sprite: NativeSprite? = this.typelist!!.spriteCache.get(this.spriteB.toLong()) as NativeSprite?
		if (sprite == null) {
			this.generateSprites(toolkit)
			sprite = this.typelist!!.spriteCache.get(this.spriteB.toLong()) as NativeSprite?
		}
		return sprite
	}

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(Lclient!ha;I)Lclient!mi;")
	fun getRightCapSprite(@OriginalArg(0) toolkit: Toolkit?): NativeSprite? {
		if (this.spriteD < 0) {
			return null
		}
		@Pc(18) var sprite: NativeSprite? = this.typelist!!.spriteCache.get(this.spriteD.toLong()) as NativeSprite?
		if (sprite == null) {
			this.generateSprites(toolkit)
			sprite = this.typelist!!.spriteCache.get(this.spriteD.toLong()) as NativeSprite?
		}
		return sprite
	}

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(Lclient!ha;B)Lclient!mi;")
	fun getLeftCapSprite(@OriginalArg(0) toolkit: Toolkit?): NativeSprite? {
		if (this.spriteA < 0) {
			return null
		}
		@Pc(18) var sprite: NativeSprite? = this.typelist!!.spriteCache.get(this.spriteA.toLong()) as NativeSprite?
		if (sprite == null) {
			this.generateSprites(toolkit)
			sprite = this.typelist!!.spriteCache.get(this.spriteA.toLong()) as NativeSprite?
		}
		return sprite
	}

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(Lclient!cea;II)V")
	private fun decode(@OriginalArg(0) packet: Packet, @OriginalArg(2) opcode: Int) {
		if (opcode == 1) {
			this.fontId = packet.readUShort()
		} else if (opcode == 2) {
			this.color = packet.readUMedium()
		} else if (opcode == 3) {
			this.spriteA = packet.readUShort()
		} else if (opcode == 4) {
			this.spriteB = packet.readUShort()
		} else if (opcode == 5) {
			this.spriteC = packet.readUShort()
		} else if (opcode == 6) {
			this.spriteD = packet.readUShort()
		} else if (opcode == 7) {
			this.scrollOffsetX = packet.readShort()
		} else if (opcode == 8) {
			this.placementExampleString = packet.readJagString()
		} else if (opcode == 9) {
			this.cyclesVisible = packet.readUShort()
		} else if (opcode == 10) {
			this.scrollOffsetY = packet.readShort()
		} else if (opcode == 11) {
			this.fadeStartCycle = 0
		} else if (opcode == 12) {
			this.displayType = packet.readUByte()
		} else if (opcode == 13) {
			this.textOffsetY = packet.readShort()
		} else if (opcode == 14) {
			this.fadeStartCycle = packet.readUShort()
		}
	}

	@OriginalMember(owner = "client!ia", name = "b", descriptor = "(Lclient!ha;I)V")
	private fun generateSprites(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(7) val spriteResource: Js5ResourceProvider? = this.typelist!!.spriteResource
		@Pc(30) var indexedImage: SpriteType?
		if (this.spriteA >= 0 && this.typelist!!.spriteCache.get(this.spriteA.toLong()) == null && spriteResource!!.isFileReady(this.spriteA)) {
			indexedImage = SpriteDataProvider.listSingle(spriteResource, this.spriteA)
			this.typelist!!.spriteCache.put(toolkit!!.generateSprite(indexedImage, true), this.spriteA.toLong())
		}
		if (this.spriteC >= 0 && this.typelist!!.spriteCache.get(this.spriteC.toLong()) == null && spriteResource!!.isFileReady(this.spriteC)) {
			indexedImage = SpriteDataProvider.listSingle(spriteResource, this.spriteC)
			this.typelist!!.spriteCache.put(toolkit!!.generateSprite(indexedImage, true), this.spriteC.toLong())
		}
		if (this.spriteB >= 0 && this.typelist!!.spriteCache.get(this.spriteB.toLong()) == null && spriteResource!!.isFileReady(this.spriteB)) {
			indexedImage = SpriteDataProvider.listSingle(spriteResource, this.spriteB)
			this.typelist!!.spriteCache.put(toolkit!!.generateSprite(indexedImage, true), this.spriteB.toLong())
		}
		if (this.spriteD >= 0 && this.typelist!!.spriteCache.get(this.spriteD.toLong()) == null && spriteResource!!.isFileReady(this.spriteD)) {
			indexedImage = SpriteDataProvider.listSingle(spriteResource, this.spriteD)
			this.typelist!!.spriteCache.put(toolkit!!.generateSprite(indexedImage, true), this.spriteD.toLong())
		}
	}

	@OriginalMember(owner = "client!ia", name = "b", descriptor = "(Lclient!ha;B)Lclient!mi;")
	fun getMiddleFillSprite(@OriginalArg(0) toolkit: Toolkit?): NativeSprite? {
		if (this.spriteC < 0) {
			return null
		}
		@Pc(29) var sprite: NativeSprite? = this.typelist!!.spriteCache.get(this.spriteC.toLong()) as NativeSprite?
		if (sprite == null) {
			this.generateSprites(toolkit)
			sprite = this.typelist!!.spriteCache.get(this.spriteC.toLong()) as NativeSprite?
		}
		return sprite
	}

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(ILclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet) {
		while (true) {
			@Pc(13) val opcode: Int = packet.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(II)Ljava/lang/String;")
	fun getAmountText(@OriginalArg(0) amount: Int): String? {
		@Pc(8) var string: String? = this.placementExampleString
		while (true) {
			@Pc(16) val placeholderIndex: Int = string!!.indexOf("%1")
			if (placeholderIndex < 0) {
				return string
			}
			string = string.substring(0, placeholderIndex) + Utils.toSignedString(amount, false) + string.substring(placeholderIndex + 2)
		}
	}
}
