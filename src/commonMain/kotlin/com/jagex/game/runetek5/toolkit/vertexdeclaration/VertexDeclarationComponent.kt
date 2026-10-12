package com.jagex.game.runetek5.toolkit.vertexdeclaration

import com.jagex.game.runetek5.VertexAttribute
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jl")
class VertexDeclarationComponent {

	@OriginalMember(owner = "client!jl", name = "b", descriptor = "J")
	private var bitpacked: Long = 0L

	@OriginalMember(owner = "client!jl", name = "c", descriptor = "I")
	private var size: Int = 0

	@OriginalMember(owner = "client!jl", name = "<init>", descriptor = "(Lclient!daa;)V")
	constructor(@OriginalArg(0) action: VertexAttribute?) {
		this.bitpacked = action!!.slot.toLong()
		this.size = 1
	}

	@OriginalMember(owner = "client!jl", name = "<init>", descriptor = "([Lclient!daa;)V")
	constructor(@OriginalArg(0) actions: Array<out VertexAttribute?>?) {
		@Pc(5) var i: Int = 0
		while (i < actions!!.size) {
			this.pack(actions[i])
			i++
		}
	}

	@OriginalMember(owner = "client!jl", name = "a", descriptor = "(Lclient!daa;I)V")
	private fun pack(@OriginalArg(0) action: VertexAttribute?) {
		val shift: Int = this.size++ * VertexDeclarationAction.BITS_PER_SLOT
		this.bitpacked = this.bitpacked or (action!!.slot shl shift).toLong()
	}

	@OriginalMember(owner = "client!jl", name = "a", descriptor = "(BI)I")
	private fun getSlotIndex(@OriginalArg(1) index: Int): Int {
		return (this.bitpacked shr VertexDeclarationAction.BITS_PER_SLOT * index).toInt() and 0xF
	}

	@OriginalMember(owner = "client!jl", name = "a", descriptor = "(II)Lclient!daa;")
	fun getAction(@OriginalArg(0) index: Int): VertexAttribute? {
		return VertexDeclarationAction.forIndex(this.getSlotIndex(index))
	}

	@OriginalMember(owner = "client!jl", name = "a", descriptor = "(B)I")
	fun getSize(): Int {
		return this.size
	}
}
