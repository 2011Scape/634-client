package com.jagex.game.runetek5.config.particle

import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import kotlin.jvm.JvmField

@OriginalClass("client!po")
class ParticleType {

	@OriginalMember(owner = "client!po", name = "c", descriptor = "I")
	@JvmField
	var effectType: Int = 0

	@OriginalMember(owner = "client!po", name = "d", descriptor = "I")
	@JvmField
	var offsetY: Int = 0

	@OriginalMember(owner = "client!po", name = "e", descriptor = "I")
	@JvmField
	var offsetX: Int = 0

	@OriginalMember(owner = "client!po", name = "h", descriptor = "I")
	@JvmField
	var offsetZ: Int = 0

	@OriginalMember(owner = "client!po", name = "j", descriptor = "I")
	@JvmField
	var particleHandlingType: Int = 0

	@OriginalMember(owner = "client!po", name = "l", descriptor = "I")
	@JvmField
	var zan: Int = 0

	@OriginalMember(owner = "client!po", name = "m", descriptor = "I")
	@JvmField
	var size3d: Int = 0

	@OriginalMember(owner = "client!po", name = "o", descriptor = "J")
	@JvmField
	var uid: Long = 0L

	@OriginalMember(owner = "client!po", name = "p", descriptor = "I")
	private var rotation: Int = 0

	@OriginalMember(owner = "client!po", name = "q", descriptor = "I")
	@JvmField
	var partileFileId: Int = 0

	@OriginalMember(owner = "client!po", name = "r", descriptor = "I")
	@JvmField
	var sizeMultiplier: Int = 0

	@OriginalMember(owner = "client!po", name = "k", descriptor = "I")
	@JvmField
	var currentOffset: Int = 0

	@OriginalMember(owner = "client!po", name = "f", descriptor = "I")
	@JvmField
	var verticeCalculationType: Int = 0

	@OriginalMember(owner = "client!po", name = "v", descriptor = "Z")
	private var invert: Boolean = false

	@OriginalMember(owner = "client!po", name = "a", descriptor = "(Lclient!cea;IB)V")
	private fun decode(@OriginalArg(0) packet: Packet, @OriginalArg(1) opcode: Int) {
		if (opcode == 1) {
			this.rotation = packet.readUShort()
		} else if (opcode == 2) {
			packet.readUByte()
		} else if (opcode == 3) {
			this.offsetX = packet.readInt()
			this.offsetY = packet.readInt()
			this.offsetZ = packet.readInt()
		} else if (opcode == 4) {
			this.effectType = packet.readUByte()
			this.sizeMultiplier = packet.readInt()
		} else if (opcode == 6) {
			this.particleHandlingType = packet.readUByte()
		} else if (opcode == 8) {
			this.currentOffset = 1
		} else if (opcode == 9) {
			this.verticeCalculationType = 1
		} else if (opcode == 10) {
			this.invert = true
		}
	}

	@OriginalMember(owner = "client!po", name = "a", descriptor = "(ILclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet) {
		while (true) {
			@Pc(5) val opcode: Int = packet.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}

	@OriginalMember(owner = "client!po", name = "a", descriptor = "(I)V")
	fun postDecode() {
		this.zan = TextureCombineSource.COSINE[this.rotation shl 3]
		@Pc(17) val x: Long = this.offsetX.toLong()
		@Pc(21) val y: Long = this.offsetY.toLong()
		@Pc(25) val z: Long = this.offsetZ.toLong()
		this.size3d = kotlin.math.sqrt((y * y + x * x + z * z).toDouble()).toInt()
		if (this.sizeMultiplier == 0) {
			this.sizeMultiplier = 1
		}
		if (this.effectType == 0) {
			this.uid = 2147483647L
		} else if (this.effectType == 1) {
			this.uid = (this.size3d * 8 / this.sizeMultiplier).toLong()
			this.uid *= this.uid
		} else if (this.effectType == 2) {
			this.uid = (this.size3d * 8 / this.sizeMultiplier).toLong()
		}
		if (this.invert) {
			this.size3d *= -1
		}
	}

	companion object {
		@OriginalMember(owner = "client!wd", name = "F", descriptor = "Lclient!eq;")
		@JvmField
		val particleTypeCache: IterableHashTable = IterableHashTable(16)

		@OriginalMember(owner = "client!hi", name = "h", descriptor = "[Lclient!po;")
		@JvmField
		val particleTypes: Array<ParticleType?> = arrayOfNulls<ParticleType>(16)

		@OriginalMember(owner = "client!fha", name = "C", descriptor = "Lclient!jr;")
		@JvmField
		val particleCache: LRUCache = LRUCache(64)
	}
}
