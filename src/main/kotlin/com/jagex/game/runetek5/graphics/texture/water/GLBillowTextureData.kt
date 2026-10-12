package com.jagex.game.runetek5.graphics.texture.water

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!dha")
class GLBillowTextureData : BillowNoiseGenerator {

	@OriginalMember(owner = "client!dha", name = "H", descriptor = "[B")
	private var data: ByteArray? = null

	@OriginalMember(owner = "client!dha", name = "<init>", descriptor = "()V")
	constructor() : super(12, 5, 16, 2, 2, 0.45f)

	@OriginalMember(owner = "client!dha", name = "a", descriptor = "(BIB)V")
	override fun writeTexel(@OriginalArg(1) index: Int, @OriginalArg(2) value: Byte) {
		@Pc(16) val sample: Byte = (((value.toInt() shr 1) and 0x7F) + 127).toByte()
		@Pc(20) val offset: Int = index * 2
		@Pc(23) val firstIndex: Int = offset
		@Pc(24) val secondIndex: Int = offset + 1
		this.data!![firstIndex] = sample
		this.data!![secondIndex] = sample
	}

	@OriginalMember(owner = "client!dha", name = "a", descriptor = "(IBII)[B")
	fun getData(): ByteArray? {
		this.data = ByteArray(524288)
		this.renderTexels()
		return this.data
	}
}
