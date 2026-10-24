package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.fetchText
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import withLock

/**
 * Not part of the original client: a player's hiscores, RuneLite's HiscoreManager.lookupAsync. The first
 * ask for a name starts a fetch from the website's `index_lite.json` and answers null; later asks answer
 * the result once it is in. A name the website does not know, or a failed fetch, answers null and is asked
 * again after a minute. Used by Opponent Information for a player's lifepoints and its stat comparison,
 * and by the Hiscore panel.
 */
object HiscoreLookup {

	const val DEFAULT_URL: String = "https://2011.rs/m=hiscore/index_lite.json?player="

	private const val RETRY_MILLIS: Long = 60_000L

	/**
	 * A player's levels by skill, -1 for a skill without a rank; with each skill's [ranks] and [xp] (-1
	 * without a rank) and the overall rank, total level and experience (-1 when the answer has none).
	 */
	class Result(
		@JvmField val player: String,
		@JvmField val levels: IntArray,
		@JvmField val ranks: IntArray = IntArray(levels.size).also { it.fill(-1) },
		@JvmField val xp: LongArray = LongArray(levels.size).also { it.fill(-1L) },
	) {
		@JvmField var overallRank: Int = -1
		@JvmField var overallLevel: Int = -1
		@JvmField var overallXp: Long = -1L
	}

	private val lock: Any = Any()

	/** Under [lock]: the results in, by lower-case name, and when each name's last fetch failed (or is out, as 0). */
	private val results: HashMap<String, Result> = HashMap()

	private val tried: HashMap<String, Long> = HashMap()

	/** Where the hiscores come from, or null for nowhere (the smoke test, or `-Dclient.hiscores=none`). */
	@JvmStatic
	fun url(): String? {
		val property: String? = lang.getProperty("client.hiscores")
		if (property != null) {
			val trimmed: String = property.trim()
			return if (trimmed.isEmpty() || trimmed.equals("none", ignoreCase = true)) null else trimmed
		}
		if (lang.getProperty("client.test") != null) {
			return null
		}
		return DEFAULT_URL
	}

	/** [name]'s hiscores, or null until they are in; asks the website the first time. */
	@JvmStatic
	fun lookup(name: String): Result? {
		val key: String = key(name)
		val now: Long = lang.currentTimeMillis()
		var fetch: Boolean = false
		val known: Result? = withLock(lock) {
			val result: Result? = results[key]
			if (result == null) {
				val last: Long? = tried[key]
				// 0 marks a fetch that is out
				if (last == null || (last != 0L && now - last >= RETRY_MILLIS)) {
					tried[key] = 0L
					fetch = true
				}
			}
			result
		}
		if (fetch) {
			val base: String? = url()
			if (base == null) {
				failed(key)
			} else {
				fetchText(base + encode(name)) { body -> arrived(key, body) }
			}
		}
		return known
	}

	/** Whether the last fetch for [name] failed (not known to the website, or no answer), until it is asked again. */
	@JvmStatic
	fun notFound(name: String): Boolean = withLock(lock) { results[key(name)] == null && (tried[key(name)] ?: 0L) != 0L }

	/** Puts [result] in as if fetched: the smoke test's player, whose hiscores are not on the website. */
	@JvmStatic
	fun put(name: String, result: Result) {
		withLock(lock) {
			results[key(name)] = result
			tried.remove(key(name))
		}
	}

	@JvmStatic
	fun clear() {
		withLock(lock) {
			results.clear()
			tried.clear()
		}
	}

	private fun arrived(key: String, body: String?) {
		val result: Result? = if (body == null) null else parse(body)
		if (result == null) {
			failed(key)
			return
		}
		withLock(lock) {
			results[key] = result
			tried.remove(key)
		}
	}

	private fun failed(key: String) {
		withLock(lock) {
			tried[key] = lang.currentTimeMillis()
		}
	}

	private fun key(name: String): String = name.replace(' ', ' ').trim().lowercase()

	/**
	 * The website's answer: `{"player":"Name","skills":{"attack":{"rank":1,"level":99,"xp":13034431},...}}`,
	 * the skills by their lower-case names in [Skill.NAMES]. A skill missing, or with a rank of -1, is -1.
	 */
	@JvmStatic
	fun parse(body: String): Result? {
		val player: String = stringField(body, "player") ?: return null
		val levels: IntArray = IntArray(Skill.COUNT) { -1 }
		val result: Result = Result(player, levels)
		for (skill in 0 until Skill.COUNT) {
			val entry: String = skillEntry(body, Skill.NAMES[skill].lowercase()) ?: continue
			val rank: Long? = numberField(entry, "rank")
			if (rank != -1L) {
				levels[skill] = numberField(entry, "level")?.toInt() ?: -1
				result.ranks[skill] = rank?.toInt() ?: -1
				result.xp[skill] = numberField(entry, "xp") ?: -1L
			}
		}
		val overall: String? = skillEntry(body, "overall")
		if (overall != null && numberField(overall, "rank") != -1L) {
			result.overallRank = numberField(overall, "rank")?.toInt() ?: -1
			result.overallLevel = numberField(overall, "level")?.toInt() ?: -1
			result.overallXp = numberField(overall, "xp") ?: -1L
		}
		return result
	}

	/** The `{"rank":..,"level":..,"xp":..}` of skill [name] ("attack", "overall") in the answer, or null when it is not there. */
	private fun skillEntry(body: String, name: String): String? {
		val at: Int = body.indexOf("\"" + name + "\":{")
		if (at < 0) {
			return null
		}
		val end: Int = body.indexOf('}', at)
		return body.substring(at, if (end < 0) body.length else end)
	}

	private fun stringField(body: String, name: String): String? {
		val key: String = "\"$name\":\""
		val at: Int = body.indexOf(key)
		if (at < 0) {
			return null
		}
		val end: Int = body.indexOf('"', at + key.length)
		return if (end < 0) null else body.substring(at + key.length, end)
	}

	private fun numberField(body: String, name: String): Long? {
		val key: String = "\"$name\":"
		val at: Int = body.indexOf(key)
		if (at < 0) {
			return null
		}
		var i: Int = at + key.length
		val start: Int = i
		while (i < body.length && (body[i] == '-' || body[i].isDigit())) {
			i++
		}
		return body.substring(start, i).toLongOrNull()
	}

	/** [name] for a URL's query: letters and digits as they are, a space as %20, the rest as UTF-8 bytes. */
	@JvmStatic
	fun encode(name: String): String {
		val out: StringBuilder = StringBuilder()
		for (byte in name.replace(' ', ' ').trim().encodeToByteArray()) {
			val b: Int = byte.toInt() and 0xFF
			val c: Char = b.toChar()
			if (b < 0x80 && (c.isLetterOrDigit() || c == '-' || c == '_' || c == '.')) {
				out.append(c)
			} else {
				out.append('%').append(HEX[b shr 4]).append(HEX[b and 0xF])
			}
		}
		return out.toString()
	}

	private const val HEX: String = "0123456789ABCDEF"
}
