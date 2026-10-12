package com.jagex.game.runetek5.config.quickchat

import com.jagex.game.runetek5.QuickChatValueType
import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!td")
class QuickchatMessageType : CacheableNode() {

	@OriginalMember(owner = "client!td", name = "A", descriptor = "[Ljava/lang/String;")
	private var message: Array<String?>? = null

	@OriginalMember(owner = "client!td", name = "B", descriptor = "[[I")
	private var configs: Array<IntArray?>? = null

	@OriginalMember(owner = "client!td", name = "C", descriptor = "[I")
	@JvmField
	var responses: IntArray? = null

	@OriginalMember(owner = "client!td", name = "D", descriptor = "Lclient!aha;")
	@JvmField
	var quickchatMessageTypelist: QuickchatMessageTypeList? = null

	@OriginalMember(owner = "client!td", name = "K", descriptor = "[I")
	private var valueTypes: IntArray? = null

	@OriginalMember(owner = "client!td", name = "y", descriptor = "Z")
	@JvmField
	var searchable: Boolean = true

	@OriginalMember(owner = "client!td", name = "b", descriptor = "(Z)V")
	fun postDecode() {
		if (this.responses != null) {
			@Pc(15) var i: Int = 0
			while (i < this.responses!!.size) {
				this.responses!![i] = this.responses!![i] or 0x8000
				i++
			}
		}
	}

	@OriginalMember(owner = "client!td", name = "a", descriptor = "(B[ILclient!cea;)V")
	fun encodeValues(@OriginalArg(1) values: IntArray?, @OriginalArg(2) packet: Packet?) {
		if (this.valueTypes == null) {
			return
		}
		@Pc(19) var i: Int = 0
		while (i < this.valueTypes!!.size) {
			if (values!!.size <= i) {
				return
			}
			@Pc(27) val valueSize: Int = this.getValueType(i)!!.clientTransmitSize
			if (valueSize > 0) {
				packet!!.writeVariableLengthLong(valueSize, values[i].toLong())
			}
			i++
		}
	}

	@OriginalMember(owner = "client!td", name = "c", descriptor = "(II)Lclient!oaa;")
	fun getValueType(@OriginalArg(1) index: Int): QuickChatValueType? {
		return if (this.valueTypes == null || index < 0 || this.valueTypes!!.size < index) null else QuickChatValueType.forId(this.valueTypes!![index])
	}

	@OriginalMember(owner = "client!td", name = "a", descriptor = "(IIZ)I")
	fun getDynamicCommandParam(@OriginalArg(0) type: Int, @OriginalArg(1) index: Int): Int {
		if (this.valueTypes == null || index < 0 || this.valueTypes!!.size < index) {
			return -1
		} else if (this.configs!![index] == null || type < 0 || this.configs!![index]!!.size < type) {
			return -1
		} else {
			return this.configs!![index]!![type]
		}
	}

	@OriginalMember(owner = "client!td", name = "a", descriptor = "(ILclient!cea;I)V")
	private fun decode(@OriginalArg(0) opcode: Int, @OriginalArg(1) packet: Packet) {
		if (opcode == 1) {
			this.message = Utils.split('<', packet.readString()!!)
			return
		}
		@Pc(17) var count: Int
		@Pc(23) var index: Int
		if (opcode == 2) {
			count = packet.readUByte()
			this.responses = IntArray(count)
			index = 0
			while (index < count) {
				this.responses!![index] = packet.readUShort()
				index++
			}
		} else if (opcode == 3) {
			count = packet.readUByte()
			this.configs = arrayOfNulls<IntArray>(count)
			this.valueTypes = IntArray(count)
			index = 0
			while (index < count) {
				@Pc(69) val valueTypeId: Int = packet.readUShort()
				@Pc(75) val valueType: QuickChatValueType? = QuickChatValueType.forId(valueTypeId)
				if (valueType != null) {
					this.valueTypes!![index] = valueTypeId
					this.configs!![index] = IntArray(valueType.parameterCount)
					@Pc(91) var i: Int = 0
					while (i < valueType.parameterCount) {
						this.configs!![index]!![i] = packet.readUShort()
						i++
					}
				}
				index++
			}
		} else if (opcode == 4) {
			this.searchable = false
		}
	}

	@OriginalMember(owner = "client!td", name = "f", descriptor = "(B)I")
	fun getDynamicCommandCount(): Int {
		return if (this.valueTypes == null) 0 else this.valueTypes!!.size
	}

	@OriginalMember(owner = "client!td", name = "a", descriptor = "(Lclient!cea;B)Ljava/lang/String;")
	fun fillDynamicValues(@OriginalArg(0) packet: Packet?): String {
		@Pc(17) val stringBuffer: StringBuffer = StringBuffer(80)
		if (this.valueTypes != null) {
			@Pc(22) var i: Int = 0
			while (i < this.valueTypes!!.size) {
				stringBuffer.append(this.message!![i])
				stringBuffer.append(this.quickchatMessageTypelist!!.getValueDisplay(packet!!.bitpackLong(QuickChatValueType.forId(this.valueTypes!![i])!!.serverTransmitSize), this.configs!![i], this.getValueType(i)))
				i++
			}
		}
		stringBuffer.append(this.message!![this.message!!.size - 1])
		return stringBuffer.toString()
	}

	@OriginalMember(owner = "client!td", name = "a", descriptor = "(Lclient!cea;I)V")
	fun decode(@OriginalArg(0) packet: Packet) {
		while (true) {
			@Pc(11) val opcode: Int = packet.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(opcode, packet)
		}
	}

	@OriginalMember(owner = "client!td", name = "a", descriptor = "(I)Ljava/lang/String;")
	fun getText(): String {
		@Pc(16) val buffer: StringBuffer = StringBuffer(80)
		if (this.message == null) {
			return ""
		}
		buffer.append(this.message!![0])
		@Pc(30) var i: Int = 1
		while (i < this.message!!.size) {
			buffer.append("...")
			buffer.append(this.message!![i])
			i++
		}
		return buffer.toString()
	}
}
