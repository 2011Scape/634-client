package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!kf")
class TextureCombineSource {

	@OriginalMember(owner = "client!kf", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!kf", name = "e", descriptor = "[I")
		@JvmField
		val SINE: IntArray = IntArray(16384)

		@OriginalMember(owner = "client!kf", name = "b", descriptor = "[I")
		@JvmField
		val COSINE: IntArray = IntArray(16384)

		init {
			@Pc(7) val radiansPerUnit: Double = 3.834951969714103E-4
			@Pc(9) var angle: Int = 0
			while (angle < 16384) {
				SINE[angle] = (Math.sin(radiansPerUnit * angle.toDouble()) * 16384.0).toInt()
				COSINE[angle] = (Math.cos(radiansPerUnit * angle.toDouble()) * 16384.0).toInt()
				angle++
			}
		}

		@OriginalMember(owner = "client!fha", name = "H", descriptor = "Lclient!kf;")
		@JvmField
		val PREVIOUS: TextureCombineSource = TextureCombineSource()

		@OriginalMember(owner = "client!ft", name = "M", descriptor = "Lclient!kf;")
		@JvmField
		val CONSTANT_COLOR: TextureCombineSource = TextureCombineSource()
		@OriginalMember(owner = "client!eca", name = "j", descriptor = "Lclient!kf;")
		@JvmField
		val TEXTURE: TextureCombineSource = TextureCombineSource()
		@OriginalMember(owner = "client!mw", name = "G", descriptor = "Lclient!kf;")
		@JvmField
		val PRIMARY_COLOR: TextureCombineSource = TextureCombineSource()
	}
}
