package com.jagex.game.runetek5.plugin

/* Not part of the original client: the Implementation-Version the build writes into the jar's manifest; null when run from classes. */
actual fun clientVersion(): String? {
	return PluginRuntime::class.java.`package`?.implementationVersion
}
