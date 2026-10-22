package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField

/**
 * Not part of the original client: which of Bank Organizer's categories an item name falls in, by
 * RuneLite's rules (BankOrganizerPlugin.onItemContainerChanged). The categories go in order; one
 * that is on adds its colour when no exclusion (the hidden defaults and the player's own) is in the
 * lowercase name and a pattern is; Tools & misc matches a pattern as a whole word, plural or not.
 */
class BankOrganizerMatcher {

	/** By category id (1 to [BankCategories.COUNT]; 0 unused): on or off. */
	@JvmField
	val active: BooleanArray = BooleanArray(BankCategories.COUNT + 1)

	/** By category id: the box colour, ARGB. */
	@JvmField
	val colours: IntArray = IntArray(BankCategories.COUNT + 1)

	/** By category id: the player's comma-separated exclusions, split, trimmed and lowercase. */
	private val userExclusions: Array<Array<String>> = Array(BankCategories.COUNT + 1) { NONE }

	/** Tools & misc's patterns as word-boundary regexes, made once. */
	private val wordPatterns: Array<Regex> = Array(BankCategories.patterns(BankCategories.TOOLS).size) { i ->
		Regex("\\b" + Regex.escape(BankCategories.patterns(BankCategories.TOOLS)[i]) + "s?\\b")
	}

	init {
		for (id in 1..BankCategories.COUNT) {
			active[id] = BankCategories.DEFAULT_ACTIVE[id]
			colours[id] = BankCategories.DEFAULT_COLOURS[id]
		}
	}

	/** Sets category [id]'s own exclusions from the settings text [raw] (comma-separated). */
	fun exclude(id: Int, raw: String) {
		userExclusions[id] = split(raw)
	}

	/** The colours of the categories item [name] falls in, in category order; empty for none. */
	fun colours(name: String): IntArray {
		val lower: String = name.lowercase()
		var found: IntArray = EMPTY
		for (id in 1..BankCategories.COUNT) {
			if (active[id] && inCategory(id, lower)) {
				found = found + colours[id]
			}
		}
		return found
	}

	/** Whether lowercase [name] is in category [id], ignoring whether the category is on. */
	fun inCategory(id: Int, name: String): Boolean {
		// RuneLite keeps raw snakeskin out of Range
		if (id == BankCategories.RANGE && name == "snakeskin") {
			return false
		}
		if (excluded(BankCategories.exclusions(id), name) || excluded(userExclusions[id], name)) {
			return false
		}
		if (id == BankCategories.TOOLS) {
			return wordPatterns.any { it.containsMatchIn(name) }
		}
		return BankCategories.patterns(id).any { name.contains(it) }
	}

	private fun excluded(exclusions: Array<String>, name: String): Boolean {
		return exclusions.any { it.isNotEmpty() && name.contains(it) }
	}

	companion object {

		private val NONE: Array<String> = arrayOf()

		private val EMPTY: IntArray = IntArray(0)

		/** [raw] split at commas, each trimmed and lowercase, the empty ones dropped. */
		fun split(raw: String): Array<String> {
			return raw.split(',').map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toTypedArray()
		}
	}
}
