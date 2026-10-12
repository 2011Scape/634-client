package com.jagex.game.runetek5.vardomain

import com.jagex.game.runetek5.cache.node.LongNode
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.varp.VarbitType
import com.jagex.game.runetek5.config.vartype.player.VarPlayerType
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.utils.Utils
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!pia")
class PlayerVarDomain : VarDomain {

	@OriginalMember(owner = "client!pia", name = "i", descriptor = "Lclient!eq;")
	private var updates: IterableHashTable = IterableHashTable(128)

	@OriginalMember(owner = "client!pia", name = "b", descriptor = "[I")
	@JvmField
	val activeVars: IntArray = IntArray(TypeLists.VAR!!.num)

	@OriginalMember(owner = "client!pia", name = "q", descriptor = "[I")
	private val pendingVars: IntArray = IntArray(TypeLists.VAR!!.num)

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(BZ)I")
	fun removeNext(@OriginalArg(1) first: Boolean): Int {
		@Pc(20) val time: Long = Utils.currentTimeMillis()
		@Pc(34) var node: LongNode? = if (first) this.updates.first() as LongNode? else this.updates.next() as LongNode?
		while (node != null) {
			if ((node.value and 0x3FFFFFFFFFFFFFFFL) < time) {
				if ((node.value and 0x4000000000000000L) != 0L) {
					@Pc(58) val key: Int = node.key.toInt()
					this.activeVars[key] = this.pendingVars[key]
					node.unlink()
					return key
				}
				node.unlink()
			}
			node = this.updates.next() as LongNode?
		}
		return -1
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(BII)V")
	fun setVarValueInt(@OriginalArg(1) id: Int, @OriginalArg(2) value: Int) {
		this.activeVars[value] = id
		@Pc(20) var node: LongNode? = this.updates.get(value.toLong()) as LongNode?
		if (node == null) {
			node = LongNode(Utils.currentTimeMillis() + 500L)
			this.updates.put(value.toLong(), node)
		} else {
			node.value = Utils.currentTimeMillis() + 500L
		}
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(II)I")
	override fun getVarBitValue(@OriginalArg(0) id: Int): Int {
		@Pc(8) val type: VarbitType? = TypeLists.VARBIT!!.list(id)
		@Pc(11) val baseVar: Int = type!!.configIndex
		@Pc(19) val startBit: Int = type.startBit
		@Pc(22) val endBit: Int = type.endBit
		@Pc(29) val mask: Int = EmissiveTriangle.BIT_MASKS!![endBit - startBit]
		return (this.activeVars[baseVar] shr startBit) and mask
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(III)V")
	fun setVarBitValue(@OriginalArg(0) varbitId: Int, @OriginalArg(2) value: Int) {
		var value: Int = value
		@Pc(8) val varbitType: VarbitType? = TypeLists.VARBIT!!.list(varbitId)
		@Pc(11) val configIndex: Int = varbitType!!.configIndex
		@Pc(14) val startBit: Int = varbitType.startBit
		@Pc(17) val endBit: Int = varbitType.endBit
		@Pc(24) var bitmask: Int = EmissiveTriangle.BIT_MASKS!![endBit - startBit]
		if (value < 0 || value > bitmask) {
			value = 0
		}
		bitmask = bitmask shl startBit
		this.setVarValueInt(((value shl startBit) and bitmask) or (this.activeVars[configIndex] and bitmask.inv()), configIndex)
	}

	@OriginalMember(owner = "client!pia", name = "b", descriptor = "(BII)V")
	fun updateVarp(@OriginalArg(1) value: Int, @OriginalArg(2) id: Int) {
		this.pendingVars[id] = value
		@Pc(23) var node: LongNode? = this.updates.get(id.toLong()) as LongNode?
		if (node == null) {
			node = LongNode(4611686018427387905L)
			this.updates.put(id.toLong(), node)
		} else if (node.value != 4611686018427387905L) {
			node.value = (Utils.currentTimeMillis() + 500L) or 0x4000000000000000L
		}
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(B)V")
	fun reset() {
		@Pc(3) var i: Int = 0
		while (i < TypeLists.VAR!!.num) {
			@Pc(10) val type: VarPlayerType? = TypeLists.VAR!!.list(i)
			if (type != null && type.clientCode == 0) {
				this.pendingVars[i] = 0
				this.activeVars[i] = 0
			}
			i++
		}
		this.updates = IterableHashTable(128)
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(IB)I")
	override fun getVarValueInt(@OriginalArg(0) id: Int): Int {
		return this.activeVars[id]
	}

	@OriginalMember(owner = "client!pia", name = "c", descriptor = "(BII)V")
	fun setVarBitValueFromServer(@OriginalArg(1) varbitId: Int, @OriginalArg(2) value: Int) {
		var value: Int = value
		@Pc(8) val varbitType: VarbitType? = TypeLists.VARBIT!!.list(varbitId)
		@Pc(11) val configIndex: Int = varbitType!!.configIndex
		@Pc(14) val startBit: Int = varbitType.startBit
		@Pc(21) val endBit: Int = varbitType.endBit
		@Pc(27) var bitmask: Int = EmissiveTriangle.BIT_MASKS!![endBit - startBit]
		if (value < 0 || bitmask < value) {
			value = 0
		}
		bitmask = bitmask shl startBit
		this.updateVarp(((value shl startBit) and bitmask) or (bitmask.inv() and this.pendingVars[configIndex]), configIndex)
	}

	companion object {
		@OriginalMember(owner = "client!kda", name = "ab", descriptor = "Lclient!pia;")
		@JvmField
		var playerVarProvider: PlayerVarDomain? = null

		@OriginalMember(owner = "client!fv", name = "r", descriptor = "Z")
		@JvmField
		var varVerifyIdPending: Boolean = false

		@OriginalMember(owner = "client!saa", name = "e", descriptor = "(B)Z")
		@JvmStatic
		fun requestVarVerifyid(): Boolean {
			PlayerVarDomain.varVerifyId++
			PlayerVarDomain.varVerifyIdPending = true
			return true
		}

		@OriginalMember(owner = "client!vp", name = "k", descriptor = "I")
		@JvmField
		var pulseEventConfigsPtr: Int = 0

		@OriginalMember(owner = "client!jt", name = "w", descriptor = "I")
		@JvmField
		var varVerifyId: Int = 0

		@OriginalMember(owner = "client!de", name = "g", descriptor = "[I")
		@JvmField
		val LOGIN_XTEAS: IntArray = IntArray(32)

		@OriginalMember(owner = "client!ch", name = "a", descriptor = "(I)V")
		@JvmStatic
		fun sendTransmitVarVerifyId() {
			@Pc(18) val packet: TcpPacket? = TcpPacket.createPacket(ClientProt.TRANSMITVAR_VERIFYID, TcpPacket.isaac)
			packet!!.buffer!!.writeShort(PlayerVarDomain.varVerifyId)
			BufferedConnectionContext.queuePacket(packet)
		}
	}
}
