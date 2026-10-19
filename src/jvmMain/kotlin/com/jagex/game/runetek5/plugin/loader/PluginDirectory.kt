package com.jagex.game.runetek5.plugin.loader

import com.jagex.game.runetek5.plugin.PluginPaths
import java.io.File

/**
 * Not part of the original client: where the plugin jars are. `client.plugins` (the launcher's
 * `--plugins <dir>`) names the folder, or `none` for no plugins; otherwise `~/.634-client/plugins`,
 * made if missing, except under the smoke test (client.test), which loads none unless told where.
 */
object PluginDirectory {

	@JvmStatic
	fun resolve(): File? {
		val property: String? = System.getProperty("client.plugins")
		if (property != null) {
			if (property.equals("none", ignoreCase = true) || property.isEmpty()) {
				return null
			}
			return File(expandHome(property))
		}
		val path: String = PluginPaths.pluginsDirectory() ?: return null
		val directory: File = File(path)
		if (!directory.exists() && !directory.mkdirs()) {
			System.err.println("Plugins: cannot make " + directory.path)
			return null
		}
		return directory
	}

	/** `~` and `~/...` as the home folder, as the launcher reads paths. */
	@JvmStatic
	fun expandHome(path: String): String {
		if (path == "~" || path.startsWith("~/")) {
			return System.getProperty("user.home") + path.substring(1)
		}
		return path
	}
}
