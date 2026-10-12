package com.jagex.game.runetek5.cache.node.lru

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

@OriginalClass("client!bda")
class StructType : CacheableNode() {

	@OriginalMember(owner = "client!bda", name = "C", descriptor = "Lclient!eq;")
	private var params: IterableHashTable? = null

	@OriginalMember(owner = "client!bda", name = "a", descriptor = "(IIB)I")
	fun getParam(@OriginalArg(0) paramKey: Int, @OriginalArg(1) defaultValue: Int): Int {
		if (this.params == null) {
			return defaultValue
		} else {
			@Pc(16) val intNode: IntNode? = this.params!!.get(paramKey.toLong()) as IntNode?
			return if (intNode == null) defaultValue else intNode.value
		}
	}

	@OriginalMember(owner = "client!bda", name = "a", descriptor = "(ILclient!cea;B)V")
	private fun decode(@OriginalArg(0) opcode: Int, @OriginalArg(1) packet: Packet?) {
		if (opcode != 249) {
			return
		}
		@Pc(18) val count: Int = packet!!.readUByte()
		@Pc(25) var local25: Int
		if (this.params == null) {
			val bucketCount: Int = Utils.nextPowerOfTwo(count)
			this.params = IterableHashTable(bucketCount)
		}
		var paramIndex: Int = 0
		while (paramIndex < count) {
			@Pc(46) val isString: Boolean = packet.readUByte() == 1
			@Pc(50) val paramKey: Int = packet.readUMedium()
			@Pc(59) var node: Node?
			if (isString) {
				node = StringNode(packet.readString())
			} else {
				node = IntNode(packet.readInt())
			}
			this.params!!.put(paramKey.toLong(), node)
			paramIndex++
		}
	}

	@OriginalMember(owner = "client!bda", name = "a", descriptor = "(Lclient!cea;B)V")
	fun decode(@OriginalArg(0) packet: Packet?) {
		while (true) {
			@Pc(5) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(opcode, packet)
		}
	}

	@OriginalMember(owner = "client!bda", name = "a", descriptor = "(ILjava/lang/String;I)Ljava/lang/String;")
	fun getStringParam(@OriginalArg(1) string: String?, @OriginalArg(2) paramKey: Int): String? {
		if (this.params == null) {
			return string
		} else {
			@Pc(21) val stringNode: StringNode? = this.params!!.get(paramKey.toLong()) as StringNode?
			return if (stringNode == null) string else stringNode.value
		}
	}
}
