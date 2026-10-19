package com.jagex.game.runetek5.plugin

import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: which plugins the player turned off (`::plugin <id> off`), one
 * `<id>=off` a line in `plugins.properties`. A plugin not in the file is on.
 */
object PluginSettings {

	private val disabled: ArrayList<String> = ArrayList()

	private var loaded: Boolean = false

	@JvmStatic
	fun load() {
		disabled.clear()
		loaded = true
		val file: io.File = PluginPaths.settingsFile() ?: return
		val lines: Array<String> = PluginPaths.readLines(file) ?: return
		for (i in 0 until lines.size) {
			val line: String = lines[i]
			val at: Int = line.indexOf('=')
			if (at <= 0 || line.startsWith("#")) {
				continue
			}
			if (line.substring(at + 1).trim().equals("off", ignoreCase = true)) {
				disabled.add(line.substring(0, at).trim())
			}
		}
	}

	@JvmStatic
	fun isEnabled(id: String): Boolean {
		if (!loaded) {
			load()
		}
		return !disabled.contains(id)
	}

	/** Records the choice and saves it; false when it cannot be saved (the choice still holds for this run). */
	@JvmStatic
	fun setEnabled(id: String, enabled: Boolean): Boolean {
		if (!loaded) {
			load()
		}
		if (enabled) {
			disabled.remove(id)
		} else if (!disabled.contains(id)) {
			disabled.add(id)
		}
		val file: io.File = PluginPaths.settingsFile() ?: return false
		val out: StringBuilder = StringBuilder("# Plugins turned off with ::plugin <id> off; a plugin not listed is on\n")
		for (i in 0 until disabled.size) {
			out.append(disabled[i]).append("=off\n")
		}
		return PluginPaths.writeText(file, out.toString())
	}

	/** Forgets what was loaded, for the tests. */
	@JvmStatic
	fun reset() {
		disabled.clear()
		loaded = false
	}
}
