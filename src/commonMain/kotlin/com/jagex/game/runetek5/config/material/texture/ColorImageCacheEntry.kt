package com.jagex.game.runetek5.config.material.texture

import com.jagex.game.runetek5.cache.node.Node
import io.File
import util.Locale
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import util.lowercase

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
				property = lang.getProperty("java.vendor")!!.lowercase(util.defaultLocale())
			} catch (@Pc(7) ex: Exception) {
			}
			property.lowercase(util.defaultLocale())
			property = "Unknown"
			try {
				property = lang.getProperty("java.version")!!.lowercase(util.defaultLocale())
			} catch (@Pc(18) ex: Exception) {
			}
			property.lowercase(util.defaultLocale())
			property = "Unknown"
			try {
				property = lang.getProperty("os.name")!!.lowercase(util.defaultLocale())
			} catch (@Pc(29) ex: Exception) {
			}
			osName = property.lowercase(util.defaultLocale())
			property = "Unknown"
			try {
				property = lang.getProperty("os.arch")!!.lowercase(util.defaultLocale())
			} catch (@Pc(40) ex: Exception) {
			}
			osArch = property.lowercase(util.defaultLocale())
			property = "Unknown"
			try {
				property = lang.getProperty("os.version")!!.lowercase(util.defaultLocale())
			} catch (@Pc(51) ex: Exception) {
			}
			property.lowercase(util.defaultLocale())
			property = "~/"
			try {
				property = lang.getProperty("user.home")!!.lowercase(util.defaultLocale())
			} catch (@Pc(62) ex: Exception) {
			}
			File(property)
		}
	}
}
