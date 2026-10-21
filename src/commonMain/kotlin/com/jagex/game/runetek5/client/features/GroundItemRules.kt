package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the rules Ground Items (GroundItems) judges an item by, as
 * RuneLite's Ground Items plugin has them: an entry of the highlighted or hidden list (`name`,
 * `name>N`, `name<N`, `*` for any run of characters), a list matched exactly before by wildcard,
 * and the quantities and prices written short (`12.3K`). Nothing here reads the game.
 */
object GroundItemRules {

	/** How a list matched an item: not at all, through a wildcard, or by the item's exact name. */
	const val NONE: Int = 0

	const val WILDCARD: Int = 1

	const val EXACT: Int = 2

	/** One entry of a list: the name (a pattern when [wildcard]), and the quantity the item's must be above, or below when [lessThan]. */
	class Threshold(@JvmField val name: String, @JvmField val quantity: Int, @JvmField val lessThan: Boolean, @JvmField val wildcard: Boolean) {

		fun quantityHolds(count: Int): Boolean {
			return if (lessThan) count < quantity else count > quantity
		}
	}

	/**
	 * An entry as RuneLite's ItemThreshold.fromName reads it: from the end, digits and spaces are
	 * skipped, and a `<` or `>` before them makes them the quantity (one that is not a number leaves
	 * "more than 0"); null for an empty entry.
	 */
	@JvmStatic
	fun threshold(text: String?): Threshold? {
		if (text.isNullOrEmpty()) {
			return null
		}
		var entry: String = text
		var lessThan: Boolean = false
		var quantity: Int = 0
		val wildcard: Boolean = entry.contains('*')
		// from the end: past the quantity's digits and spaces to the character before them
		var i: Int = entry.length - 1
		while (i >= 0 && (entry[i] in '0'..'9' || entry[i].isWhitespace())) {
			i--
		}
		if (i >= 0 && (entry[i] == '<' || entry[i] == '>') && i + 1 < entry.length) {
			val parsed: Int? = entry.substring(i + 1).trim().toIntOrNull()
			if (parsed != null) {
				quantity = parsed
				lessThan = entry[i] == '<'
			}
			entry = entry.substring(0, i)
		}
		return Threshold(entry.trim(), quantity, lessThan, wildcard)
	}

	/** A list as the settings keep it, comma separated, its entries trimmed (RuneLite's Text.fromCSV). */
	@JvmStatic
	fun parseList(csv: String): ArrayList<Threshold> {
		val out: ArrayList<Threshold> = ArrayList()
		for (part in splitCsv(csv)) {
			val threshold: Threshold = threshold(part) ?: continue
			out.add(threshold)
		}
		return out
	}

	/** The entries of a comma-separated list, trimmed, the empty ones left out. */
	@JvmStatic
	fun splitCsv(csv: String): ArrayList<String> {
		val out: ArrayList<String> = ArrayList()
		for (part in csv.split(',')) {
			val trimmed: String = part.trim()
			if (trimmed.isNotEmpty()) {
				out.add(trimmed)
			}
		}
		return out
	}

	/** The entries joined back as the settings keep them (RuneLite's Text.toCSV). */
	@JvmStatic
	fun joinCsv(items: List<String>): String {
		return items.joinToString(",")
	}

	/** How [list] matches an item called [name] of which there are [quantity]: [EXACT], [WILDCARD] or [NONE] (RuneLite's ItemList.matches). */
	@JvmStatic
	fun matches(list: List<Threshold>, name: String, quantity: Int): Int {
		for (t in list) {
			if (!t.wildcard && t.name.equals(name, ignoreCase = true) && t.quantityHolds(quantity)) {
				return EXACT
			}
		}
		for (t in list) {
			if (t.wildcard && wildcardMatches(t.name, name) && t.quantityHolds(quantity)) {
				return WILDCARD
			}
		}
		return NONE
	}

	/** Whether [text] is [pattern] with each run of `*` standing for any characters, ignoring case (RuneLite's WildcardMatcher). */
	@JvmStatic
	fun wildcardMatches(pattern: String, text: String): Boolean {
		val parts: List<String> = pattern.lowercase().split('*')
		val subject: String = text.lowercase()
		if (parts.size == 1) {
			return subject == parts[0]
		}
		if (!subject.startsWith(parts[0])) {
			return false
		}
		var at: Int = parts[0].length
		for (k in 1 until parts.size - 1) {
			val found: Int = subject.indexOf(parts[k], at)
			if (found < 0) {
				return false
			}
			at = found + parts[k].length
		}
		val last: String = parts[parts.size - 1]
		return subject.length - last.length >= at && subject.endsWith(last)
	}

	/**
	 * A quantity written short, as RuneLite's QuantityFormatter.quantityToStackSize writes it: below
	 * 10,000 in full with commas; else in thousands, millions or billions, at most four characters of
	 * number (`12.3K`, `100K`, `1.23M`, `2.14B`).
	 */
	@JvmStatic
	fun quantityToStackSize(quantity: Long): String {
		if (quantity < 0) {
			return "-" + quantityToStackSize(if (quantity == Long.MIN_VALUE) Long.MAX_VALUE else -quantity)
		}
		if (quantity < 10_000L) {
			return withCommas(quantity)
		}
		val suffixes: Array<String> = arrayOf("", "K", "M", "B")
		var power: Int = 0
		var scale: Long = 1L
		while (power < suffixes.size - 1 && quantity / (scale * 1000L) >= 1L) {
			scale *= 1000L
			power++
		}
		var number: String = formatDecimal(quantity, scale)
		if (number.length > 4) {
			number = number.substring(0, 4)
		}
		if (number.endsWith(".")) {
			number = number.substring(0, number.length - 1)
		}
		return number + suffixes[power]
	}

	/** [value] / [scale] with up to three decimals, trailing zeros dropped, grouped with commas as NumberFormat would. */
	private fun formatDecimal(value: Long, scale: Long): String {
		val whole: Long = value / scale
		// three decimals, rounded half-even as NumberFormat rounds
		val remainder: Long = value % scale
		var thousandths: Long = 0L
		var wholePart: Long = whole
		if (scale > 1L) {
			val exact: Double = remainder.toDouble() * 1000.0 / scale.toDouble()
			thousandths = kotlin.math.round(exact).toLong()
			if (thousandths >= 1000L) {
				thousandths -= 1000L
				wholePart++
			}
		}
		var out: String = withCommas(wholePart)
		if (thousandths > 0L) {
			var decimals: String = thousandths.toString().padStart(3, '0')
			decimals = decimals.trimEnd('0')
			out += ".$decimals"
		}
		return out
	}

	@JvmStatic
	fun withCommas(value: Long): String {
		val digits: String = value.toString()
		val out: StringBuilder = StringBuilder()
		for (i in digits.indices) {
			if (i > 0 && (digits.length - i) % 3 == 0) {
				out.append(',')
			}
			out.append(digits[i])
		}
		return out.toString()
	}
}
