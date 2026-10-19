package com.jagex.game.runetek5.plugin.loader

import java.io.File
import java.net.URLClassLoader

/**
 * Not part of the original client: one plugin jar's classes. The client is the parent and is asked
 * first, so the API and the Kotlin standard library are the client's own copies and a plugin cannot
 * replace a client class.
 */
class PluginClassLoader(@JvmField val jar: File, parent: ClassLoader?) : URLClassLoader(arrayOf(jar.toURI().toURL()), parent) {

	override fun toString(): String {
		return "PluginClassLoader(" + jar.name + ")"
	}
}
