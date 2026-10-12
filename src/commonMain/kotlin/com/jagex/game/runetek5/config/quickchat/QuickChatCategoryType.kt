package com.jagex.game.runetek5.config.quickchat

import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField

@OriginalClass("client!ug")
class QuickChatCategoryType : CacheableNode() {

	@OriginalMember(owner = "client!ug", name = "x", descriptor = "[I")
	@JvmField
	var messages: IntArray? = null

	@OriginalMember(owner = "client!ug", name = "z", descriptor = "[C")
	@JvmField
	var messageHotkeys: CharArray? = null

	@OriginalMember(owner = "client!ug", name = "E", descriptor = "Ljava/lang/String;")
	@JvmField
	var name: String? = null

	@OriginalMember(owner = "client!ug", name = "F", descriptor = "[C")
	@JvmField
	var subCategoryHotkeys: CharArray? = null

	@OriginalMember(owner = "client!ug", name = "J", descriptor = "[I")
	@JvmField
	var subCategories: IntArray? = null

	@OriginalMember(owner = "client!ug", name = "a", descriptor = "(ILclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet) {
		while (true) {
			@Pc(17) val opcode: Int = packet.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}

	@OriginalMember(owner = "client!ug", name = "a", descriptor = "(BC)I")
	fun findPhraseByShortcut(@OriginalArg(1) shortcut: Char): Int {
		if (this.messages == null) {
			return -1
		}
		@Pc(12) var i: Int = 0
		while (i < this.messages!!.size) {
			if (shortcut == this.messageHotkeys!![i]) {
				return this.messages!![i]
			}
			i++
		}
		return -1
	}

	@OriginalMember(owner = "client!ug", name = "f", descriptor = "(B)V")
	fun postDecode() {
		@Pc(6) var i: Int
		if (this.messages != null) {
			i = 0
			while (i < this.messages!!.size) {
				this.messages!![i] = this.messages!![i] or 0x8000
				i++
			}
		}
		if (this.subCategories != null) {
			i = 0
			while (i < this.subCategories!!.size) {
				this.subCategories!![i] = this.subCategories!![i] or 0x8000
				i++
			}
		}
	}

	@OriginalMember(owner = "client!ug", name = "a", descriptor = "(CI)I")
	fun findSubcategoryByShortcut(@OriginalArg(0) shortcut: Char): Int {
		if (this.subCategories == null) {
			return -1
		}
		@Pc(12) var i: Int = 0
		while (i < this.subCategories!!.size) {
			if (this.subCategoryHotkeys!![i] == shortcut) {
				return this.subCategories!![i]
			}
			i++
		}
		return -1
	}

	@OriginalMember(owner = "client!ug", name = "a", descriptor = "(ILclient!cea;I)V")
	private fun decode(@OriginalArg(1) packet: Packet, @OriginalArg(2) opcode: Int) {
		if (opcode == 1) {
			this.name = packet.readString()
			return
		}
		@Pc(15) var count: Int
		@Pc(25) var index: Int
		@Pc(38) var hotkey: Byte
		if (opcode == 2) {
			count = packet.readUByte()
			this.subCategories = IntArray(count)
			this.subCategoryHotkeys = CharArray(count)
			index = 0
			while (index < count) {
				this.subCategories!![index] = packet.readUShort()
				hotkey = packet.readByte()
				this.subCategoryHotkeys!![index] = if (hotkey.toInt() == 0) 0.toChar() else Utils.cp1252ToChar(hotkey)
				index++
			}
		} else if (opcode == 3) {
			count = packet.readUByte()
			this.messageHotkeys = CharArray(count)
			this.messages = IntArray(count)
			index = 0
			while (index < count) {
				this.messages!![index] = packet.readUShort()
				hotkey = packet.readByte()
				this.messageHotkeys!![index] = if (hotkey.toInt() == 0) 0.toChar() else Utils.cp1252ToChar(hotkey)
				index++
			}
		}
	}
}
