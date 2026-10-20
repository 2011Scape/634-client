package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.SettingsSchema
import kotlin.jvm.JvmField

/**
 * Not part of the original client: the settings a plugin declared (plugin.api.SettingsSchema),
 * kept on its context for the plugin manager to show. A key declared twice keeps its first entry.
 */
class PluginSchemaImpl : SettingsSchema {

	/** One declared setting: its [kind], [key], [label], and the bounds that apply to the kind. */
	class Entry(@JvmField val kind: Int, @JvmField val key: String, @JvmField val label: String, @JvmField val defaultText: String, @JvmField val defaultInt: Int, @JvmField val min: Int, @JvmField val max: Int, @JvmField val step: Int, @JvmField val options: Array<String>?)

	@JvmField
	val entries: ArrayList<Entry> = ArrayList()

	private fun has(key: String): Boolean {
		for (i in 0 until entries.size) {
			if (entries[i].key == key) {
				return true
			}
		}
		return false
	}

	override fun toggle(key: String, label: String, default: Boolean) {
		if (!has(key)) {
			entries.add(Entry(TOGGLE, key, label, default.toString(), if (default) 1 else 0, 0, 1, 1, null))
		}
	}

	override fun number(key: String, label: String, default: Int, min: Int, max: Int, step: Int) {
		if (!has(key)) {
			entries.add(Entry(NUMBER, key, label, default.toString(), default, min, max, if (step < 1) 1 else step, null))
		}
	}

	override fun choice(key: String, label: String, default: String, options: Array<String>) {
		if (!has(key) && options.isNotEmpty()) {
			entries.add(Entry(CHOICE, key, label, default, 0, 0, options.size - 1, 1, options))
		}
	}

	companion object {
		const val TOGGLE: Int = 0

		const val NUMBER: Int = 1

		const val CHOICE: Int = 2
	}
}
