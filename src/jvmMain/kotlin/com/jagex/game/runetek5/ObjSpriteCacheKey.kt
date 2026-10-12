package com.jagex.game.runetek5

import com.jagex.game.runetek5.entity.ComparableGraphNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpDiagonalWaves

@OriginalClass("client!nia")
class ObjSpriteCacheKey : ComparableGraphNode {

	@OriginalMember(owner = "client!nia", name = "a", descriptor = "I")
	@JvmField
	var numMode: Int = 0

	@OriginalMember(owner = "client!nia", name = "b", descriptor = "I")
	@JvmField
	var shadowColor: Int = 0

	@OriginalMember(owner = "client!nia", name = "i", descriptor = "I")
	@JvmField
	var amount: Int = 0

	@OriginalMember(owner = "client!nia", name = "j", descriptor = "Z")
	@JvmField
	var objWearCol: Boolean = false

	@OriginalMember(owner = "client!nia", name = "k", descriptor = "I")
	@JvmField
	var index: Int = 0

	@OriginalMember(owner = "client!nia", name = "l", descriptor = "I")
	@JvmField
	var itemId: Int = 0

	@OriginalMember(owner = "client!nia", name = "m", descriptor = "I")
	@JvmField
	var outlineSize: Int = 0

	@OriginalMember(owner = "client!nia", name = "a", descriptor = "(B)J")
	override fun toLong(): Long {
		@Pc(5) val crc64: LongArray = TextureOpDiagonalWaves.CRC64_TABLE
		@Pc(7) val hash01: Long = -1L
		@Pc(22) val hash02: Long = crc64[((hash01 xor this.index.toLong()) and 0xFFL).toInt()] xor (hash01 ushr 8)
		@Pc(39) val hash03: Long = (hash02 ushr 8) xor crc64[(((this.itemId shr 8).toLong() xor hash02) and 0xFFL).toInt()]
		@Pc(54) val hash04: Long = (hash03 ushr 8) xor crc64[((hash03 xor this.itemId.toLong()) and 0xFFL).toInt()]
		@Pc(71) val hash05: Long = (hash04 ushr 8) xor crc64[((hash04 xor (this.amount shr 24).toLong()) and 0xFFL).toInt()]
		@Pc(88) val hash06: Long = crc64[(((this.amount shr 16).toLong() xor hash05) and 0xFFL).toInt()] xor (hash05 ushr 8)
		@Pc(105) val hash07: Long = (hash06 ushr 8) xor crc64[((hash06 xor (this.amount shr 8).toLong()) and 0xFFL).toInt()]
		@Pc(120) val hash08: Long = (hash07 ushr 8) xor crc64[((hash07 xor this.amount.toLong()) and 0xFFL).toInt()]
		@Pc(135) val hash09: Long = (hash08 ushr 8) xor crc64[((this.outlineSize.toLong() xor hash08) and 0xFFL).toInt()]
		@Pc(152) val hash10: Long = crc64[((hash09 xor (this.shadowColor shr 24).toLong()) and 0xFFL).toInt()] xor (hash09 ushr 8)
		@Pc(169) val hash11: Long = (hash10 ushr 8) xor crc64[((hash10 xor (this.shadowColor shr 16).toLong()) and 0xFFL).toInt()]
		@Pc(191) val hash12: Long = crc64[((hash11 xor (this.shadowColor shr 8).toLong()) and 0xFFL).toInt()] xor (hash11 ushr 8)
		@Pc(206) val hash13: Long = (hash12 ushr 8) xor crc64[((this.shadowColor.toLong() xor hash12) and 0xFFL).toInt()]
		@Pc(221) val hash14: Long = crc64[((hash13 xor this.numMode.toLong()) and 0xFFL).toInt()] xor (hash13 ushr 8)
		return (hash14 ushr 8) xor crc64[(((if (this.objWearCol) 1 else 0).toLong() xor hash14) and 0xFFL).toInt()]
	}

	@OriginalMember(owner = "client!nia", name = "a", descriptor = "(ILclient!we;)Z")
	override fun matches(@OriginalArg(1) other: ComparableGraphNode?): Boolean {
		if (other !is ObjSpriteCacheKey) {
			return false
		}
		@Pc(11) val data: ObjSpriteCacheKey = other
		if (this.index != data.index) {
			return false
		} else if (this.itemId != data.itemId) {
			return false
		} else if (this.amount != data.amount) {
			return false
		} else if (data.outlineSize != this.outlineSize) {
			return false
		} else if (this.shadowColor != data.shadowColor) {
			return false
		} else if (this.numMode == data.numMode) {
			return data.objWearCol == this.objWearCol
		} else {
			return false
		}
	}
}
