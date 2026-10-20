package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.PluginConfig

/**
 * Not part of the original client: a plugin's settings, name=value lines in
 * `plugin-config/<id>.properties` under the plugins folder, read when made and written by [save].
 * A backslash, a line break, a tab, and in a name `=`, `#` and a space, are written escaped the
 * way java.util.Properties writes them, so that any text round-trips.
 */
class PluginConfigImpl(private val id: String) : PluginConfig {

	private val values: LinkedHashMap<String, String> = LinkedHashMap()

	init {
		val file: io.File? = PluginPaths.configFile(id)
		if (file != null) {
			val lines: Array<String>? = PluginPaths.readLines(file)
			if (lines != null) {
				for (i in 0 until lines.size) {
					var line: String = lines[i]
					if (line.endsWith("\r")) {
						line = line.substring(0, line.length - 1)
					}
					val at: Int = separator(line)
					if (at <= 0 || line.startsWith("#")) {
						continue
					}
					values[unescape(line.substring(0, at))] = unescape(line.substring(at + 1))
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

	override fun keys(): Array<String> {
		return values.keys.toTypedArray()
	}

	override fun remove(key: String) {
		values.remove(key)
	}

	override fun save(): Boolean {
		val file: io.File = PluginPaths.configFile(id) ?: return false
		val out: StringBuilder = StringBuilder("# Settings of the plugin ").append(id).append(", written by the client\n")
		for (entry in values.entries) {
			out.append(escape(entry.key, true)).append('=').append(escape(entry.value, false)).append('\n')
		}
		return PluginPaths.writeText(file, out.toString())
	}

	companion object {
		/** Where the first `=` that is not escaped is in [line], or -1. */
		private fun separator(line: String): Int {
			var i: Int = 0
			while (i < line.length) {
				val c: Char = line[i]
				if (c == '\\') {
					i += 2
					continue
				}
				if (c == '=') {
					return i
				}
				i++
			}
			return -1
		}

		private fun escape(text: String, key: Boolean): String {
			val out: StringBuilder = StringBuilder(text.length + 8)
			for (i in 0 until text.length) {
				val c: Char = text[i]
				if (c == '\\') {
					out.append("\\\\")
				} else if (c == '\n') {
					out.append("\\n")
				} else if (c == '\r') {
					out.append("\\r")
				} else if (c == '\t') {
					out.append("\\t")
				} else if (key && (c == '=' || c == '#' || c == ' ')) {
					out.append('\\').append(c)
				} else if (!key && c == ' ' && i == 0) {
					out.append("\\ ")
				} else {
					out.append(c)
				}
			}
			return out.toString()
		}

		private fun unescape(text: String): String {
			if (text.indexOf('\\') < 0) {
				return text
			}
			val out: StringBuilder = StringBuilder(text.length)
			var i: Int = 0
			while (i < text.length) {
				val c: Char = text[i]
				if (c != '\\' || i + 1 == text.length) {
					out.append(c)
					i++
					continue
				}
				val next: Char = text[i + 1]
				if (next == 'n') {
					out.append('\n')
				} else if (next == 'r') {
					out.append('\r')
				} else if (next == 't') {
					out.append('\t')
				} else {
					out.append(next)
				}
				i += 2
			}
			return out.toString()
		}
	}

	/** How many settings there are, for the tests. */
	fun size(): Int {
		return values.size
	}
}
