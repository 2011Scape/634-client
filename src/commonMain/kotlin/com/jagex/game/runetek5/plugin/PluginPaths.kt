package com.jagex.game.runetek5.plugin

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: where plugins and their files live, `<home>/.634-client`
 * (docs/PLUGINS.md). Nothing is read or written there under the smoke test (client.test) or without
 * a home folder; the tests point [homeOverride] at a folder of their own, and a smoke run that
 * needs plugin settings names a home of its own with client.test.pluginHome.
 */
object PluginPaths {

	@JvmField
	var homeOverride: String? = null

	/** The folder of the plugin files, or null when there is none to use. */
	@JvmStatic
	fun root(): io.File? {
		var home: String? = homeOverride
		if (home == null) {
			if (lang.getProperty("client.test") != null) {
				home = lang.getProperty("client.test.pluginHome")
				return if (home == null) null else io.File(home + "/.634-client")
			}
			home = lang.getProperty("user.home")
			if (home == null) {
				return null
			}
		}
		return io.File(home + "/.634-client")
	}

	/** Where the plugin jars go, as a path, or null. */
	@JvmStatic
	fun pluginsDirectory(): String? {
		val root: io.File = root() ?: return null
		return root.getPath() + "/plugins"
	}

	/** The on/off choices for the plugins. */
	@JvmStatic
	fun settingsFile(): io.File? {
		val root: io.File = root() ?: return null
		return io.File(root.getPath() + "/plugins.properties")
	}

	/** A plugin's own settings. */
	@JvmStatic
	fun configFile(id: String): io.File? {
		val root: io.File = root() ?: return null
		return io.File(root.getPath() + "/plugin-config/" + id + ".properties")
	}

	/** The lines of [file] (empty without the file), or null when it cannot be read. */
	@JvmStatic
	fun readLines(file: io.File): Array<String>? {
		try {
			if (!file.exists()) {
				return arrayOf()
			}
			val lines: ArrayList<String> = ArrayList()
			val reader: io.BufferedReader = io.BufferedReader(io.InputStreamReader(io.FileInputStream(file)))
			try {
				while (true) {
					val line: String = reader.readLine() ?: break
					lines.add(line)
				}
			} finally {
				reader.close()
			}
			return lines.toTypedArray()
		} catch (ex: io.IOException) {
			lang.printlnErr("Plugins: cannot read " + file.getPath() + ": " + ex)
			return null
		}
	}

	/** The bytes of [file], or null without the file or when it cannot be read (or is over 4 MB). */
	@JvmStatic
	fun readBytes(file: io.File): ByteArray? {
		try {
			if (!file.exists() || file.length() > MAX_READ) {
				return null
			}
			val bytes = ByteArray(file.length().toInt())
			val stream: io.FileInputStream = io.FileInputStream(file)
			try {
				var at = 0
				while (at < bytes.size) {
					val read: Int = stream.read(bytes, at, bytes.size - at)
					if (read < 0) {
						return null
					}
					at += read
				}
			} finally {
				stream.close()
			}
			return bytes
		} catch (ex: io.IOException) {
			lang.printlnErr("Plugins: cannot read " + file.getPath() + ": " + ex)
			return null
		}
	}

	private const val MAX_READ: Long = 4L * 1024L * 1024L

	/** Writes [text] to [file], making its folder; false when it cannot. */
	@JvmStatic
	fun writeText(file: io.File, text: String): Boolean {
		try {
			val parent: io.File? = file.getParentFile()
			if (parent != null && !parent.exists()) {
				parent.mkdirs()
			}
			val stream: io.FileOutputStream = io.FileOutputStream(file)
			try {
				stream.write(text.encodeToByteArray())
			} finally {
				stream.close()
			}
			return true
		} catch (ex: io.IOException) {
			lang.printlnErr("Plugins: cannot write " + file.getPath() + ": " + ex)
			return false
		}
	}
}
