package com.jagex.game.runetek5.plugin

import withLock
import kotlin.jvm.JvmStatic

/** Not part of the original client: fetches [url] on a thread of its own and hands [done] the body, or null when the request failed. */
expect fun fetchText(url: String, done: (String?) -> Unit)

/**
 * Not part of the original client: the Grand Exchange guide prices behind ClientApi.itemPrice, from the
 * game's website (`/items/prices.json`, a JSON object of item id to price). They are fetched in the
 * background when the plugins start and every hour after, and kept in the client's folder
 * (`prices.json`) so that a run without the website still has the last ones. `client.prices` (the
 * launcher's `--prices`) names another address, or `none` for no prices; a test run fetches only
 * when it names one. Everything but the fetch runs on the game thread.
 */
object ItemPrices {

	const val DEFAULT_URL: String = "https://2011.rs/items/prices.json"

	private const val REFRESH_MILLIS: Long = 60L * 60L * 1000L

	/** After a failed fetch, the next try comes sooner than an hour. */
	private const val RETRY_MILLIS: Long = 5L * 60L * 1000L

	private const val CACHE_FILE: String = "prices.json"

	private var prices: HashMap<Int, Int> = HashMap()

	private val lock: Any = Any()

	/** A body that arrived and has not been read yet; [fetching] while a request is out. Both under [lock]. */
	private var arrived: String? = null

	private var failed: Boolean = false

	private var fetching: Boolean = false

	private var nextFetch: Long = 0L

	private var loadedCache: Boolean = false

	/** The price of one item [id], noted as the item it notes is looked up by the caller; -1 when not known. */
	@JvmStatic
	fun price(id: Int): Int {
		return prices[id] ?: -1
	}

	/** How many prices are known, for `::prices`. */
	@JvmStatic
	fun count(): Int {
		return prices.size
	}

	/** Where the prices come from, or null for nowhere. */
	@JvmStatic
	fun url(): String? {
		val property: String? = lang.getProperty("client.prices")
		if (property != null) {
			val trimmed: String = property.trim()
			return if (trimmed.isEmpty() || trimmed.equals("none", ignoreCase = true)) null else trimmed
		}
		if (lang.getProperty("client.test") != null) {
			return null
		}
		return DEFAULT_URL
	}

	/** Each client cycle: the cached prices once, a body that arrived, and the next fetch when it is due. */
	@JvmStatic
	fun poll() {
		val url: String = url() ?: return
		if (!loadedCache) {
			loadedCache = true
			loadCache()
		}
		var body: String? = null
		var wasFailure: Boolean = false
		var busy: Boolean = false
		withLock(lock) {
			body = arrived
			arrived = null
			wasFailure = failed
			failed = false
			busy = fetching
		}
		val now: Long = lang.currentTimeMillis()
		val text: String? = body
		if (text != null) {
			val parsed: HashMap<Int, Int>? = parse(text)
			if (parsed != null && parsed.isNotEmpty()) {
				prices = parsed
				saveCache(text)
			}
		}
		if (wasFailure) {
			nextFetch = now + RETRY_MILLIS
		}
		if (busy || now < nextFetch) {
			return
		}
		nextFetch = now + REFRESH_MILLIS
		withLock(lock) {
			fetching = true
		}
		fetchText(url) { result ->
			withLock(lock) {
				fetching = false
				if (result == null) {
					failed = true
				} else {
					arrived = result
				}
			}
		}
	}

	private fun loadCache() {
		val root: io.File = PluginPaths.root() ?: return
		val lines: Array<String> = PluginPaths.readLines(io.File(root.getPath() + "/" + CACHE_FILE)) ?: return
		val parsed: HashMap<Int, Int> = parse(lines.joinToString("\n")) ?: return
		if (prices.isEmpty()) {
			prices = parsed
		}
	}

	private fun saveCache(text: String) {
		val root: io.File = PluginPaths.root() ?: return
		PluginPaths.writeText(io.File(root.getPath() + "/" + CACHE_FILE), text)
	}

	/**
	 * The website's document, `{"4151": 1500000, ...}`: item ids as keys, whole prices as values;
	 * anything else in it is skipped. Null when it is not an object at all.
	 */
	@JvmStatic
	fun parse(text: String): HashMap<Int, Int>? {
		val start: Int = text.indexOf('{')
		if (start < 0) {
			return null
		}
		val out: HashMap<Int, Int> = HashMap()
		var i: Int = start + 1
		while (i > 0 && i < text.length) {
			i = parseEntry(text, i, out)
		}
		return out
	}

	/** Reads the `"key": value` pair from [from] on into [out] when both are numbers; where the next one starts, or -1 when there is none. */
	private fun parseEntry(text: String, from: Int, out: HashMap<Int, Int>): Int {
		val keyStart: Int = text.indexOf('"', from)
		val keyEnd: Int = if (keyStart < 0) -1 else text.indexOf('"', keyStart + 1)
		val colon: Int = if (keyEnd < 0) -1 else text.indexOf(':', keyEnd)
		if (colon < 0) {
			return -1
		}
		var j: Int = colon + 1
		while (j < text.length && text[j] == ' ') {
			j++
		}
		val valueStart: Int = j
		while (j < text.length && (text[j].isDigit() || (j == valueStart && text[j] == '-'))) {
			j++
		}
		val id: Int? = text.substring(keyStart + 1, keyEnd).toIntOrNull()
		val value: Long? = text.substring(valueStart, j).toLongOrNull()
		if (id != null && value != null) {
			out[id] = if (value > Int.MAX_VALUE) Int.MAX_VALUE else if (value < 0) 0 else value.toInt()
		}
		return j
	}

	/** Sets the prices outright, for the tests and the smoke run. */
	@JvmStatic
	fun setForTest(values: Map<Int, Int>) {
		prices = HashMap(values)
		loadedCache = true
	}
}
