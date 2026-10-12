package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!tr")
class TextureTransformMode {

	@OriginalMember(owner = "client!tr", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!fi", name = "R", descriptor = "Lclient!tr;")
		@JvmField
		val aClass332_2: TextureTransformMode = TextureTransformMode()
		@OriginalMember(owner = "client!b", name = "f", descriptor = "Lclient!tr;")
		@JvmField
		val aClass332_1: TextureTransformMode = TextureTransformMode()
		@OriginalMember(owner = "client!tl", name = "p", descriptor = "Lclient!tr;")
		@JvmField
		val aClass332_6: TextureTransformMode = TextureTransformMode()
		@OriginalMember(owner = "client!kaa", name = "m", descriptor = "Lclient!tr;")
		@JvmField
		val aClass332_4: TextureTransformMode = TextureTransformMode()

		@OriginalMember(owner = "client!pp", name = "v", descriptor = "Lclient!tr;")
		@JvmField
		val aClass332_5: TextureTransformMode = TextureTransformMode()
	}
}
