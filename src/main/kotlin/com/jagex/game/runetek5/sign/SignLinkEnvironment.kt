package com.jagex.game.runetek5.sign

import com.jagex.game.runetek5.cache.JFS32
import java.lang.reflect.Method
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember

object SignLinkEnvironment {

	@OriginalMember(owner = "client!wea", name = "h", descriptor = "Ljava/lang/reflect/Method;")
	@JvmField
	var setFocusTraversalKeysEnabledMethod: Method? = null

	@OriginalMember(owner = "client!wea", name = "i", descriptor = "Ljava/lang/String;")
	@JvmField
	var operatingSystem: String? = null

	@OriginalMember(owner = "client!wea", name = "k", descriptor = "Ljava/lang/reflect/Method;")
	@JvmField
	var setFocusCycleRootMethod: Method? = null

	@OriginalMember(owner = "client!wea", name = "n", descriptor = "Ljava/lang/String;")
	@JvmField
	var gameName: String? = null

	@OriginalMember(owner = "client!wea", name = "y", descriptor = "Ljava/lang/String;")
	@JvmField
	var preferencesParentFile: String? = null

	@OriginalMember(owner = "client!wea", name = "a", descriptor = "(Ljava/lang/String;I)Lclient!so;")
	@JvmStatic
	fun openGamePreferencesFile(@OriginalArg(0) string: String?): JFS32? {
		return SignLink.openPreferencesFile(string, gameName, SignLink.cacheId)
	}
}
