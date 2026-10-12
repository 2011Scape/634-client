package com.jagex.game.runetek5

import com.jagex.game.runetek5.toolkit.opengl.GLActionFormat
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!baa")
class DataType @OriginalMember(owner = "client!baa", name = "<init>", descriptor = "(I)V") constructor(@OriginalArg(0) bytesPerEntry: Int) {

	@OriginalMember(owner = "client!baa", name = "a", descriptor = "I")
	@JvmField
	val bytesPerEntry: Int = bytesPerEntry

	@OriginalMember(owner = "client!baa", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!baa", name = "e", descriptor = "Lclient!baa;")
		@JvmField
		val aClass26_3: DataType = DataType(4)

		@OriginalMember(owner = "client!baa", name = "b", descriptor = "Lclient!baa;")
		@JvmField
		val aClass26_1: DataType = DataType(1)

		@OriginalMember(owner = "client!baa", name = "i", descriptor = "Lclient!baa;")
		@JvmField
		val aClass26_7: DataType = DataType(2)

		@OriginalMember(owner = "client!baa", name = "h", descriptor = "Lclient!baa;")
		@JvmField
		val aClass26_6: DataType = DataType(4)

		@OriginalMember(owner = "client!baa", name = "g", descriptor = "Lclient!baa;")
		@JvmField
		val aClass26_5: DataType = DataType(2)

		@OriginalMember(owner = "client!baa", name = "j", descriptor = "Lclient!baa;")
		@JvmField
		val aClass26_8: DataType = DataType(4)

		@OriginalMember(owner = "client!np", name = "a", descriptor = "(ILclient!baa;)I")
		@JvmStatic
		fun getGlType(@OriginalArg(1) pixelActionFormat: DataType?): Int {
			if (DataType.aClass26_1 === pixelActionFormat) {
				return 5120
			} else if (pixelActionFormat === GLActionFormat.DEPTH_COMPONENT_24) {
				return 5122
			} else if (pixelActionFormat === DataType.aClass26_3) {
				return 5124
			} else if (GLFormat.RGBA === pixelActionFormat) {
				return 5121
			} else if (pixelActionFormat === DataType.aClass26_5) {
				return 5123
			} else if (pixelActionFormat === DataType.aClass26_6) {
				return 5125
			} else if (DataType.aClass26_7 === pixelActionFormat) {
				return 5131
			} else if (DataType.aClass26_8 === pixelActionFormat) {
				return 5126
			} else {
				throw IllegalArgumentException("")
			}
		}
	}
}
