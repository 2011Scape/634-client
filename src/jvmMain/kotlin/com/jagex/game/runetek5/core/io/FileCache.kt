package com.jagex.game.runetek5.core.io

import java.util.Hashtable
import org.openrs2.deob.annotation.OriginalMember

object FileCache {

	@OriginalMember(owner = "client!br", name = "a", descriptor = "I")
	@JvmField
	var cacheId: Int = 0

	@OriginalMember(owner = "client!br", name = "b", descriptor = "Ljava/util/Hashtable;")
	@JvmField
	val foundFiles: Hashtable<Any?, Any?> = Hashtable<Any?, Any?>(16)

	@OriginalMember(owner = "client!br", name = "c", descriptor = "Ljava/lang/String;")
	@JvmField
	var homeDirectory: String? = null

	@OriginalMember(owner = "client!br", name = "d", descriptor = "Z")
	@JvmField
	var initialized: Boolean = false

	@OriginalMember(owner = "client!br", name = "e", descriptor = "Ljava/lang/String;")
	@JvmField
	var game: String? = null
}
