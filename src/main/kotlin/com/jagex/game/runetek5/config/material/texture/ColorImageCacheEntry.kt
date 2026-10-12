package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.cache.node.Node
import java.io.File
import java.util.Locale
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ls")
class ColorImageCacheEntry : Node {

	@OriginalMember(owner = "client!ls", name = "p", descriptor = "I")
	@JvmField
	val row: Int

	@OriginalMember(owner = "client!ls", name = "s", descriptor = "I")
	@JvmField
	val index: Int

	@OriginalMember(owner = "client!ls", name = "<init>", descriptor = "(II)V")
	constructor(@OriginalArg(0) row: Int, @OriginalArg(1) index: Int) : super() {
		this.row = row
		this.index = index
	}

	companion object {
		@OriginalMember(owner = "client!ls", name = "u", descriptor = "Ljava/lang/String;")
		@JvmField
		val osName: String

		@OriginalMember(owner = "client!ls", name = "t", descriptor = "Ljava/lang/String;")
		@JvmField
		val osArch: String

		init {
			@Pc(1) var property: String = "Unknown"
			try {
				property = System.getProperty("java.vendor").lowercase(Locale.getDefault())
			} catch (@Pc(7) ex: Exception) {
			}
			property.lowercase(Locale.getDefault())
			property = "Unknown"
			try {
				property = System.getProperty("java.version").lowercase(Locale.getDefault())
			} catch (@Pc(18) ex: Exception) {
			}
			property.lowercase(Locale.getDefault())
			property = "Unknown"
			try {
				property = System.getProperty("os.name").lowercase(Locale.getDefault())
			} catch (@Pc(29) ex: Exception) {
			}
			osName = property.lowercase(Locale.getDefault())
			property = "Unknown"
			try {
				property = System.getProperty("os.arch").lowercase(Locale.getDefault())
			} catch (@Pc(40) ex: Exception) {
			}
			osArch = property.lowercase(Locale.getDefault())
			property = "Unknown"
			try {
				property = System.getProperty("os.version").lowercase(Locale.getDefault())
			} catch (@Pc(51) ex: Exception) {
			}
			property.lowercase(Locale.getDefault())
			property = "~/"
			try {
				property = System.getProperty("user.home").lowercase(Locale.getDefault())
			} catch (@Pc(62) ex: Exception) {
			}
			File(property)
		}
	}
}
