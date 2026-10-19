package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.XpDrops
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the plugins compiled into the client, on the same API as the
 * ones from the plugins folder. They run on every platform, the browser included, and are always
 * on (each has its own switch, as the experience drops have `::xpdrops`).
 */
object BuiltinPlugins {

	@JvmStatic
	fun all(): Array<PluginRegistration> {
		return arrayOf(
			PluginRegistration(XpDrops.DESCRIPTOR, XpDrops, true, "built in"),
		)
	}
}
