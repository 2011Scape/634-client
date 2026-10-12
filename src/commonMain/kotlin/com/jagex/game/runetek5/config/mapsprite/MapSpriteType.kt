package com.jagex.game.runetek5.config.mapsprite

import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.SpriteDataProvider
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!rw")
class MapSpriteType {

	@OriginalMember(owner = "client!rw", name = "c", descriptor = "Lclient!oda;")
	@JvmField
	var mapspriteTypeList: MapSpriteTypeList? = null

	@OriginalMember(owner = "client!rw", name = "e", descriptor = "I")
	@JvmField
	var fileId: Int = 0

	@OriginalMember(owner = "client!rw", name = "h", descriptor = "I")
	@JvmField
	var backgroundColor: Int = 0

	@OriginalMember(owner = "client!rw", name = "f", descriptor = "Z")
	@JvmField
	var requiresUpscaling: Boolean = false

	@OriginalMember(owner = "client!rw", name = "a", descriptor = "(I)Z")
	fun ready(): Boolean {
		return this.mapspriteTypeList!!.mapspriteResource!!.isFileReady(this.fileId)
	}

	@OriginalMember(owner = "client!rw", name = "a", descriptor = "(BILclient!cea;)V")
	private fun decode(@OriginalArg(1) opcode: Int, @OriginalArg(2) packet: Packet?) {
		if (opcode == 1) {
			this.fileId = packet!!.readUShort()
		} else if (opcode == 2) {
			this.backgroundColor = packet!!.readUMedium()
		} else if (opcode == 3) {
			this.requiresUpscaling = true
			return
		} else if (opcode == 4) {
			this.fileId = -1
			return
		}
	}

	@OriginalMember(owner = "client!rw", name = "a", descriptor = "(IZILclient!ha;)Lclient!mi;")
	fun createRotatedNativeSprite(@OriginalArg(0) rotation: Int, @OriginalArg(1) flipVertical: Boolean, @OriginalArg(3) toolkit: Toolkit?): NativeSprite? {
		@Pc(29) val cacheKey: Long = ((toolkit!!.index shl 19) or this.fileId or (rotation shl 16) or (if (flipVertical) 262144 else 0)).toLong()
		@Pc(37) var sprite: NativeSprite? = this.mapspriteTypeList!!.mapspriteCache.get(cacheKey) as NativeSprite?
		if (sprite != null) {
			return sprite
		} else if (this.mapspriteTypeList!!.mapspriteResource!!.isFileReady(this.fileId)) {
			@Pc(59) val indexedImage: SpriteType? = SpriteDataProvider.listSingle(this.mapspriteTypeList!!.mapspriteResource, this.fileId, 0)
			if (indexedImage != null) {
				indexedImage.minY = 0
				indexedImage.borderX = 0
				indexedImage.minX = 0
				indexedImage.borderY = 0
				if (flipVertical) {
					indexedImage.flipVertical()
				}
				@Pc(79) var i: Int = 0
				while (i < rotation) {
					indexedImage.rotate90DegLeft()
					i++
				}
			}
			sprite = toolkit.generateSprite(indexedImage, true)
			if (sprite != null) {
				this.mapspriteTypeList!!.mapspriteCache.put(sprite, cacheKey)
			}
			return sprite
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!rw", name = "a", descriptor = "(BLclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet?) {
		while (true) {
			@Pc(9) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(opcode, packet)
		}
	}
}
