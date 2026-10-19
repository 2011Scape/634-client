package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.loader.PluginDirectory
import com.jagex.game.runetek5.plugin.loader.PluginLoader
import java.io.File

/*
 * Not part of the original client: on the JVM the plugins are the jars in the plugins folder
 * (PluginDirectory, PluginLoader).
 */
actual fun discoverPlugins(): Array<PluginRegistration> {
	val directory: File = PluginDirectory.resolve() ?: return arrayOf()
	PluginRuntime.sourceDescription = directory.path
	return PluginLoader.load(directory)
}
