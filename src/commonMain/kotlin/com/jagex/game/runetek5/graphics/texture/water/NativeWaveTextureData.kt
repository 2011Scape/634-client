package com.jagex.game.runetek5.graphics.texture.water

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!sv")
class NativeWaveTextureData : WaveNoiseGenerator {

	@OriginalMember(owner = "client!sv", name = "P", descriptor = "[B")
	private var texels: ByteArray? = null

	@OriginalMember(owner = "client!sv", name = "<init>", descriptor = "()V")
	constructor() : super(8, 5, 8, 8, 2, 0.1f, 0.55f, 3.0f)

	@OriginalMember(owner = "client!sv", name = "a", descriptor = "(IBB)V")
	override fun writeTexel(@OriginalArg(0) index: Int, @OriginalArg(1) value: Byte) {
		@Pc(7) val offset: Int = index * 2
		@Pc(11) val level: Int = value.toInt() and 0xFF
		@Pc(14) val firstIndex: Int = offset
		@Pc(15) val secondIndex: Int = offset + 1
		this.texels!![firstIndex] = ((level * 3) shr 5).toByte()
		this.texels!![secondIndex] = ((level * 3) shr 5).toByte()
	}

	@OriginalMember(owner = "client!sv", name = "a", descriptor = "(IIBI)[B")
	fun getData(): ByteArray? {
		this.texels = ByteArray(524288)
		this.renderTexels()
		return this.texels
	}
}
