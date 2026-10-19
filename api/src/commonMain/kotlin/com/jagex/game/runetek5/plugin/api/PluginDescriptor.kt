package com.jagex.game.runetek5.plugin.api

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * What the client knows about a plugin before running it: from its jar's manifest for a plugin in
 * the plugins folder, or declared in code for one built into the client.
 *
 * @property id the plugin's name in commands and files: 2 to 32 of `a-z`, `0-9` and `-`
 * @property name the name shown to the player
 * @property version the plugin's own version, free text
 * @property apiVersion the [PluginApi.VERSION] it was built against
 */
class PluginDescriptor(
	@JvmField val id: String,
	@JvmField val name: String,
	@JvmField val version: String,
	@JvmField val apiVersion: Int,
	@JvmField val description: String,
	@JvmField val author: String,
) {
	override fun toString(): String {
		return name + " " + version + " (" + id + ")"
	}

	companion object {
		/** Whether [id] is a plugin id: 2 to 32 of `a-z`, `0-9` and `-`, starting with a letter. */
		@JvmStatic
		fun isValidId(id: String?): Boolean {
			if (id == null || id.length < 2 || id.length > 32) {
				return false
			}
			for (i in 0 until id.length) {
				val c: Char = id[i]
				val letter: Boolean = c in 'a'..'z'
				val digit: Boolean = c in '0'..'9'
				if (i == 0 && !letter) {
					return false
				}
				if (!letter && !digit && c != '-') {
					return false
				}
			}
			return true
		}
	}
}
