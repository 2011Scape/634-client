package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode
import org.openrs2.deob.annotation.OriginalArg
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!sia")
class TextureCombineFunction {
	companion object {
		@OriginalMember(owner = "client!rl", name = "d", descriptor = "Lclient!sia;")
		@JvmField
		val aClass315_4: TextureCombineFunction = TextureCombineFunction()
		@OriginalMember(owner = "client!tha", name = "l", descriptor = "Lclient!sia;")
		@JvmField
		val aClass315_5: TextureCombineFunction = TextureCombineFunction()
		@OriginalMember(owner = "client!gda", name = "r", descriptor = "Lclient!sia;")
		@JvmField
		val aClass315_1: TextureCombineFunction = TextureCombineFunction()
		@OriginalMember(owner = "client!naa", name = "A", descriptor = "Lclient!sia;")
		@JvmField
		val aClass315_2: TextureCombineFunction = TextureCombineFunction()
		@OriginalMember(owner = "client!qw", name = "a", descriptor = "(BLclient!sia;)I")
		@JvmStatic
		fun toGlCombineFunction(@OriginalArg(1) function: TextureCombineFunction?): Int {
			if (function === TextureCombineFunction.aClass315_4) {
				return 7681
			} else if (TextureCombineMode.MODULATE === function) {
				return 8448
			} else if (function === TextureCombineFunction.aClass315_5) {
				return 34165
			} else if (TextureCombineFunction.aClass315_2 === function) {
				return 260
			} else if (function === TextureCombineFunction.aClass315_1) {
				return 34023
			} else {
				throw IllegalArgumentException()
			}
		}
	}
}
