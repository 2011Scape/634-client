package com.jagex.game.runetek5.config.cursor

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.cursor.CursorTypeList
import com.jagex.game.runetek5.graphics.SpriteDataProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import kotlin.jvm.JvmField
import lang.Synchronized

@OriginalClass("client!sda")
class CursorType {

	@OriginalMember(owner = "client!sda", name = "a", descriptor = "I")
	@JvmField
	var hotspotPointX: Int = 0

	@OriginalMember(owner = "client!sda", name = "d", descriptor = "Lclient!pg;")
	@JvmField
	var cursorTypeList: CursorTypeList? = null

	@OriginalMember(owner = "client!sda", name = "h", descriptor = "I")
	@JvmField
	var hotspotPointY: Int = 0

	@OriginalMember(owner = "client!sda", name = "i", descriptor = "I")
	private var spriteId: Int = 0

	@OriginalMember(owner = "client!sda", name = "a", descriptor = "(ILclient!cea;B)V")
	private fun decode(@OriginalArg(0) opcode: Int, @OriginalArg(1) packet: Packet?) {
		if (opcode == 1) {
			this.spriteId = packet!!.readUShort()
		} else if (opcode == 2) {
			this.hotspotPointX = packet!!.readUByte()
			this.hotspotPointY = packet.readUByte()
		}
	}

	@OriginalMember(owner = "client!sda", name = "b", descriptor = "(B)Lclient!rh;")
	@Synchronized
	fun getSprite(): SpriteType? {
		@Pc(13) var indexedImage: SpriteType? = this.cursorTypeList!!.spriteCache.get(this.spriteId.toLong()) as SpriteType?
		if (indexedImage != null) {
			return indexedImage
		}
		indexedImage = SpriteDataProvider.listSingle(this.cursorTypeList!!.spriteResource, this.spriteId, 0)
		if (indexedImage != null) {
			this.cursorTypeList!!.spriteCache.put(indexedImage, this.spriteId.toLong())
		}
		return indexedImage
	}

	@OriginalMember(owner = "client!sda", name = "a", descriptor = "(Lclient!cea;Z)V")
	fun decode(@OriginalArg(0) packet: Packet?) {
		while (true) {
			@Pc(5) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(opcode, packet)
		}
	}
}
