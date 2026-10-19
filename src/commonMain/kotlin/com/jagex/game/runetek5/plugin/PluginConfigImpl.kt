package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.PluginConfig

/**
 * Not part of the original client: a plugin's settings, name=value lines in
 * `plugin-config/<id>.properties` under the plugins folder, read when made and written by [save].
 */
class PluginConfigImpl(private val id: String) : PluginConfig {

	private val values: LinkedHashMap<String, String> = LinkedHashMap()

	init {
		val file: io.File? = PluginPaths.configFile(id)
		if (file != null) {
			val lines: Array<String>? = PluginPaths.readLines(file)
			if (lines != null) {
				for (i in 0 until lines.size) {
					val line: String = lines[i]
					val at: Int = line.indexOf('=')
					if (at <= 0 || line.startsWith("#")) {
						continue
					}
					values[line.substring(0, at).trim()] = line.substring(at + 1).trim()
				}
			}
		}
	}

	override fun get(key: String, default: String): String {
		return values[key] ?: default
	}

	override fun getInt(key: String, default: Int): Int {
		val value: String = values[key] ?: return default
		return value.toIntOrNull() ?: default
	}

	override fun getBoolean(key: String, default: Boolean): Boolean {
		val value: String = values[key] ?: return default
		if (value.equals("true", ignoreCase = true) || value.equals("on", ignoreCase = true) || value == "1") {
			return true
		}
		if (value.equals("false", ignoreCase = true) || value.equals("off", ignoreCase = true) || value == "0") {
			return false
		}
		return default
	}

	override fun set(key: String, value: String) {
		values[key] = value
	}

	override fun setInt(key: String, value: Int) {
		values[key] = value.toString()
	}

	override fun setBoolean(key: String, value: Boolean) {
		values[key] = if (value) "true" else "false"
	}

	override fun remove(key: String) {
		values.remove(key)
	}

	override fun save(): Boolean {
		val file: io.File = PluginPaths.configFile(id) ?: return false
		val out: StringBuilder = StringBuilder("# Settings of the plugin ").append(id).append(", written by the client\n")
		for (entry in values.entries) {
			out.append(entry.key).append('=').append(entry.value).append('\n')
		}
		return PluginPaths.writeText(file, out.toString())
	}

	/** How many settings there are, for the tests. */
	fun size(): Int {
		return values.size
	}
}
