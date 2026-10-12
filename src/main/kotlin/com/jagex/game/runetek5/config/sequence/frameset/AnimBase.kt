package com.jagex.game.runetek5.config.sequence.frameset

import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!raa")
class AnimBase : Node {

	@OriginalMember(owner = "client!raa", name = "s", descriptor = "I")
	@JvmField
	val id: Int

	@OriginalMember(owner = "client!raa", name = "z", descriptor = "I")
	@JvmField
	val numTransformation: Int

	@OriginalMember(owner = "client!raa", name = "t", descriptor = "[[I")
	@JvmField
	val labels: Array<IntArray?>

	@OriginalMember(owner = "client!raa", name = "r", descriptor = "[I")
	@JvmField
	val transformationType: IntArray

	@OriginalMember(owner = "client!raa", name = "u", descriptor = "[I")
	@JvmField
	val submeshes: IntArray

	@OriginalMember(owner = "client!raa", name = "o", descriptor = "[Z")
	@JvmField
	val shadowed: BooleanArray

	@OriginalMember(owner = "client!raa", name = "<init>", descriptor = "(I[B)V")
	constructor(@OriginalArg(0) id: Int, @OriginalArg(1) bytes: ByteArray?) : super() {
		this.id = id
		@Pc(11) val packet: Packet = Packet(bytes)
		this.numTransformation = packet.readUByte()
		this.labels = arrayOfNulls<IntArray>(this.numTransformation)
		this.transformationType = IntArray(this.numTransformation)
		this.submeshes = IntArray(this.numTransformation)
		this.shadowed = BooleanArray(this.numTransformation)
		for (@Pc(38) i in 0 until this.numTransformation) {
			this.transformationType[i] = packet.readUByte()
			if (this.transformationType[i] == 6) {
				this.transformationType[i] = 2
			}
		}
		for (@Pc(69) j in 0 until this.numTransformation) {
			this.shadowed[j] = packet.readUByte() == 1
		}
		for (@Pc(96) k in 0 until this.numTransformation) {
			this.submeshes[k] = packet.readUShort()
		}
		for (@Pc(116) m in 0 until this.numTransformation) {
			this.labels[m] = IntArray(packet.readUByte())
		}
		for (@Pc(137) transform in 0 until this.numTransformation) {
			for (@Pc(141) label in 0 until this.labels[transform]!!.size) {
				this.labels[transform]!![label] = packet.readUByte()
			}
		}
	}
}
