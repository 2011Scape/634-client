package com.jagex.game.runetek5.config.enumtype

import com.jagex.game.runetek5.cache.node.EnumReverseNode
import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.StringNode
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!nd")
class EnumType {

	@OriginalMember(owner = "client!nd", name = "b", descriptor = "I")
	private var defaultIntValue: Int = 0

	@OriginalMember(owner = "client!nd", name = "e", descriptor = "Lclient!eq;")
	@JvmField
	var parameters: IterableHashTable? = null

	@OriginalMember(owner = "client!nd", name = "h", descriptor = "Lclient!eq;")
	private var reversed: IterableHashTable? = null

	@OriginalMember(owner = "client!nd", name = "p", descriptor = "C")
	@JvmField
	var keyType: Char = '\u0000'

	@OriginalMember(owner = "client!nd", name = "q", descriptor = "C")
	@JvmField
	var valueType: Char = '\u0000'

	@OriginalMember(owner = "client!nd", name = "l", descriptor = "Ljava/lang/String;")
	private var defaultStringValue: String? = "null"

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(Lclient!cea;II)V")
	private fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(2) opcode: Int) {
		if (opcode == 1) {
			this.keyType = Utils.cp1252ToChar(packet!!.readByte())
		} else if (opcode == 2) {
			this.valueType = Utils.cp1252ToChar(packet!!.readByte())
		} else if (opcode == 3) {
			this.defaultStringValue = packet!!.readString()
		} else if (opcode == 4) {
			this.defaultIntValue = packet!!.readInt()
		} else if (opcode == 5 || opcode == 6) {
			@Pc(65) val count: Int = packet!!.readUShort()
			this.parameters = IterableHashTable(Utils.nextPowerOfTwo(count))
			@Pc(75) var i: Int = 0
			while (i < count) {
				@Pc(81) val key: Int = packet.readInt()
				@Pc(91) var node: Node?
				if (opcode == 5) {
					node = StringNode(packet.readString())
				} else {
					node = IntNode(packet.readInt())
				}
				this.parameters!!.put(key.toLong(), node)
				i++
			}
		}
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(ZLjava/lang/String;)Z")
	fun hasOutputString(@OriginalArg(1) value: String?): Boolean {
		if (this.parameters == null) {
			return false
		}
		if (this.reversed == null) {
			this.buildReverseStringTable()
		}
		@Pc(31) var frequency: EnumReverseNode? = this.reversed!!.get(Utils.hashStringLong(value!!)) as EnumReverseNode?
		while (frequency != null) {
			if (frequency.value!!.equals(value)) {
				return true
			}
			frequency = this.reversed!!.nextWithKey() as EnumReverseNode?
		}
		return false
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(ZI)Z")
	fun hasOutput(@OriginalArg(1) key: Int): Boolean {
		if (this.parameters == null) {
			return false
		}
		if (this.reversed == null) {
			this.buildReverseIntTable()
		}
		@Pc(27) val intNode: IntNode? = this.reversed!!.get(key.toLong()) as IntNode?
		return intNode != null
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(B)V")
	private fun buildReverseIntTable() {
		this.reversed = IterableHashTable(this.parameters!!.getBucketCount())
		@Pc(20) var intNode: IntNode? = this.parameters!!.first() as IntNode?
		while (intNode != null) {
			@Pc(29) val intNode2: IntNode = IntNode(intNode.key.toInt())
			this.reversed!!.put(intNode.value.toLong(), intNode2)
			intNode = this.parameters!!.next() as IntNode?
		}
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(ILclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet?) {
		while (true) {
			@Pc(11) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(Z)V")
	private fun buildReverseStringTable() {
		this.reversed = IterableHashTable(this.parameters!!.getBucketCount())
		@Pc(20) var stringNode: StringNode? = this.parameters!!.first() as StringNode?
		while (stringNode != null) {
			@Pc(31) val entry: EnumReverseNode = EnumReverseNode(stringNode.value, stringNode.key.toInt())
			this.reversed!!.put(Utils.hashStringLong(stringNode.value!!), entry)
			stringNode = this.parameters!!.next() as StringNode?
		}
	}

	@OriginalMember(owner = "client!nd", name = "b", descriptor = "(ZI)I")
	fun getInt(@OriginalArg(1) key: Int): Int {
		if (this.parameters == null) {
			return this.defaultIntValue
		} else {
			@Pc(25) val intNode: IntNode? = this.parameters!!.get(key.toLong()) as IntNode?
			return if (intNode == null) this.defaultIntValue else intNode.value
		}
	}

	@OriginalMember(owner = "client!nd", name = "a", descriptor = "(II)Ljava/lang/String;")
	fun getString(@OriginalArg(0) key: Int): String? {
		if (this.parameters == null) {
			return this.defaultStringValue
		} else {
			@Pc(22) val stringNode: StringNode? = this.parameters!!.get(key.toLong()) as StringNode?
			return if (stringNode == null) this.defaultStringValue else stringNode.value
		}
	}
}
