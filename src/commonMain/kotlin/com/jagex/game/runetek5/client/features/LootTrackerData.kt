package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the Loot Tracker's records, as RuneLite's LootTrackerRecord,
 * LootTrackerItem and ConfigLoot keep them. A record is one source's loot: an npc by name, a
 * player, an event (a chest, a clue scroll), or a pickpocketing target; with its kills and its items,
 * each priced at the Grand Exchange and by High Level Alchemy and marked when the player ignores it.
 */
object LootTrackerData {

	/** RuneLite's LootRecordType, by the names its saved loot uses. */
	const val NPC: String = "NPC"

	const val PLAYER: String = "PLAYER"

	const val EVENT: String = "EVENT"

	const val PICKPOCKET: String = "PICKPOCKET"

	/** One item of a record: how many, and the price of one at the Grand Exchange and by High Alchemy. */
	class Item(@JvmField val id: Int, @JvmField val name: String, @JvmField var quantity: Int, @JvmField val gePrice: Int, @JvmField val haPrice: Int, @JvmField var ignored: Boolean) {
		fun totalGe(): Long = gePrice.toLong() * quantity

		fun totalHa(): Long = haPrice.toLong() * quantity
	}

	/** One source's loot: [title] (an npc's name, an event), [subTitle] ("(lvl-2)" or ""), its type, its items and its kills. */
	class Record(@JvmField val title: String, @JvmField val subTitle: String, @JvmField val type: String, @JvmField var items: ArrayList<Item>, @JvmField var kills: Int) {
		fun matches(title: String?, type: String?): Boolean {
			return title == null || (this.title == title && this.type == type)
		}
	}

	/**
	 * RuneLite's ConfigLoot, what a source's loot is kept as: the kills, when the first and the last
	 * came (epoch seconds and nanos, as RuneLite's Gson writes an Instant), and the drops, an item id
	 * then its quantity, one pair after another.
	 */
	class ConfigLoot(@JvmField val type: String, @JvmField val name: String) {
		@JvmField var kills: Int = 0
		@JvmField var firstSeconds: Long = 0L
		@JvmField var firstNanos: Int = 0
		@JvmField var lastSeconds: Long = 0L
		@JvmField var lastNanos: Int = 0
		@JvmField var drops: IntArray = IntArray(0)

		/** Adds [qty] of item [id], held to the largest int as RuneLite holds it. */
		fun add(id: Int, qty: Int) {
			var i: Int = 0
			while (i < drops.size) {
				if (drops[i] == id) {
					val sum: Long = drops[i + 1].toLong() + qty
					drops[i + 1] = if (sum > Int.MAX_VALUE || sum < 0) Int.MAX_VALUE else sum.toInt()
					return
				}
				i += 2
			}
			drops = drops.copyOf(drops.size + 2)
			drops[drops.size - 2] = id
			drops[drops.size - 1] = qty
		}

		fun numDrops(): Int = drops.size / 2
	}

	/** The settings key a source's loot is kept under (RuneLite's "drops_" + type + "_" + name). */
	@JvmStatic
	fun key(type: String, name: String): String {
		return "drops_" + type + "_" + name
	}

	/** [loot] as RuneLite's Gson writes a ConfigLoot. */
	@JvmStatic
	fun format(loot: ConfigLoot): String {
		val out: StringBuilder = StringBuilder()
		out.append("{\"type\":\"").append(loot.type).append("\",\"name\":")
		quote(out, loot.name)
		out.append(",\"kills\":").append(loot.kills)
		out.append(",\"first\":{\"seconds\":").append(loot.firstSeconds).append(",\"nanos\":").append(loot.firstNanos).append('}')
		out.append(",\"last\":{\"seconds\":").append(loot.lastSeconds).append(",\"nanos\":").append(loot.lastNanos).append('}')
		out.append(",\"drops\":[")
		for (i in loot.drops.indices) {
			if (i > 0) {
				out.append(',')
			}
			out.append(loot.drops[i])
		}
		out.append("]}")
		return out.toString()
	}

	private fun quote(out: StringBuilder, text: String) {
		out.append('"')
		for (c in text) {
			when (c) {
				'"' -> out.append("\\\"")
				'\\' -> out.append("\\\\")
				'\n' -> out.append("\\n")
				else -> if (c < ' ') out.append("\\u").append(c.code.toString(16).padStart(4, '0')) else out.append(c)
			}
		}
		out.append('"')
	}

	/** A ConfigLoot from RuneLite's JSON (its fields in any order, unknown ones skipped); null when it is not one. */
	@JvmStatic
	fun parse(text: String): ConfigLoot? {
		val reader: Reader = Reader(text)
		return reader.loot()
	}

	/** A small reader for the one shape ConfigLoot has: an object of strings, numbers, a numbers array and two {seconds, nanos} objects. */
	private class Reader(private val text: String) {
		private var at: Int = 0

		fun loot(): ConfigLoot? {
			skip()
			if (!take('{')) {
				return null
			}
			var type: String? = null
			var name: String? = null
			var kills: Int = 0
			val first: LongArray = LongArray(2)
			val last: LongArray = LongArray(2)
			var drops: IntArray = IntArray(0)
			while (true) {
				skip()
				if (take('}')) {
					break
				}
				take(',')
				skip()
				val field: String = string() ?: return null
				skip()
				if (!take(':')) {
					return null
				}
				skip()
				when (field) {
					"type" -> type = string()
					"name" -> name = string()
					"kills" -> kills = number()?.toInt() ?: 0
					"first" -> instant(first)
					"last" -> instant(last)
					"drops" -> drops = numbers() ?: return null
					else -> skipValue()
				}
			}
			val loot: ConfigLoot = ConfigLoot(type ?: return null, name ?: return null)
			loot.kills = kills
			loot.firstSeconds = first[0]
			loot.firstNanos = first[1].toInt()
			loot.lastSeconds = last[0]
			loot.lastNanos = last[1].toInt()
			loot.drops = drops
			return loot
		}

		private fun skip() {
			while (at < text.length && text[at].isWhitespace()) {
				at++
			}
		}

		private fun take(c: Char): Boolean {
			if (at < text.length && text[at] == c) {
				at++
				return true
			}
			return false
		}

		fun string(): String? {
			if (!take('"')) {
				return null
			}
			val out: StringBuilder = StringBuilder()
			while (at < text.length && text[at] != '"') {
				var c: Char = text[at++]
				if (c == '\\' && at < text.length) {
					c = text[at++]
					when (c) {
						'n' -> c = '\n'
						't' -> c = '\t'
						'u' -> {
							c = text.substring(at, minOf(at + 4, text.length)).toIntOrNull(16)?.toChar() ?: '?'
							at += 4
						}
					}
				}
				out.append(c)
			}
			at++
			return out.toString()
		}

		fun number(): Long? {
			val start: Int = at
			while (at < text.length && (text[at].isDigit() || text[at] == '-' || text[at] == '.' || text[at] == 'e' || text[at] == 'E' || text[at] == '+')) {
				at++
			}
			return text.substring(start, at).substringBefore('.').toLongOrNull()
		}

		private fun numbers(): IntArray? {
			if (!take('[')) {
				return null
			}
			val out: ArrayList<Int> = ArrayList()
			while (true) {
				skip()
				if (take(']')) {
					break
				}
				take(',')
				skip()
				out.add(number()?.toInt() ?: return null)
			}
			return out.toIntArray()
		}

		/** `{"seconds":N,"nanos":N}`, or an ISO string RuneLite's config writes elsewhere (its seconds left 0). */
		private fun instant(into: LongArray) {
			if (!take('{')) {
				skipValue()
				return
			}
			while (true) {
				skip()
				if (take('}')) {
					return
				}
				take(',')
				skip()
				val field: String = string() ?: return
				skip()
				take(':')
				skip()
				val value: Long = number() ?: 0L
				if (field == "seconds") {
					into[0] = value
				} else if (field == "nanos") {
					into[1] = value
				}
			}
		}

		private fun skipValue() {
			var depth: Int = 0
			var inString: Boolean = false
			while (at < text.length) {
				val c: Char = text[at]
				if (inString) {
					if (c == '\\') {
						at++
					} else if (c == '"') {
						inString = false
					}
				} else if (c == '"') {
					inString = true
				} else if (c == '{' || c == '[') {
					depth++
				} else if (c == '}' || c == ']') {
					if (depth == 0) {
						return
					}
					depth--
				} else if (c == ',' && depth == 0) {
					return
				}
				at++
			}
		}
	}
}
