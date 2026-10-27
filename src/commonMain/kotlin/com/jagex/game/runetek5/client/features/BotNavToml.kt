package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the TOML the Bot Nav Editor reads and writes, the part of it
 * Void's `*.nav-edges.toml` and `*.areas.toml` use: strings, integers, booleans, arrays and inline
 * tables, which Void lets run over several lines with comments between their entries.
 */
sealed class TomlValue {
	/** The value as Void writes it on one line: `{ key = value, ... }`, `[a, b]`, `"text"`, `12`, `true`. */
	abstract fun format(out: StringBuilder)

	fun format(): String {
		val out: StringBuilder = StringBuilder()
		format(out)
		return out.toString()
	}

	abstract fun copy(): TomlValue
}

class TomlString(@JvmField val value: String) : TomlValue() {
	override fun format(out: StringBuilder) {
		out.append('"')
		for (c in value) {
			when {
				c == '"' -> out.append("\\\"")
				c == '\\' -> out.append("\\\\")
				c == '\n' -> out.append("\\n")
				c == '\t' -> out.append("\\t")
				else -> out.append(c)
			}
		}
		out.append('"')
	}

	override fun copy(): TomlValue = this
}

class TomlInt(@JvmField val value: Long) : TomlValue() {
	override fun format(out: StringBuilder) {
		out.append(value)
	}

	override fun copy(): TomlValue = this
}

class TomlBool(@JvmField val value: Boolean) : TomlValue() {
	override fun format(out: StringBuilder) {
		out.append(value)
	}

	override fun copy(): TomlValue = this
}

class TomlArray(@JvmField val items: ArrayList<TomlValue>) : TomlValue() {
	override fun format(out: StringBuilder) {
		out.append('[')
		for (i in items.indices) {
			if (i > 0) {
				out.append(", ")
			}
			items[i].format(out)
		}
		out.append(']')
	}

	override fun copy(): TomlValue {
		val items: ArrayList<TomlValue> = ArrayList(items.size)
		for (item in this.items) {
			items.add(item.copy())
		}
		return TomlArray(items)
	}
}

class TomlTable(@JvmField val entries: LinkedHashMap<String, TomlValue>) : TomlValue() {
	override fun format(out: StringBuilder) {
		if (entries.isEmpty()) {
			out.append("{}")
			return
		}
		out.append("{ ")
		var first = true
		for ((key, value) in entries) {
			if (!first) {
				out.append(", ")
			}
			first = false
			BotNavToml.appendKey(out, key)
			out.append(" = ")
			value.format(out)
		}
		out.append(" }")
	}

	override fun copy(): TomlValue {
		val entries: LinkedHashMap<String, TomlValue> = LinkedHashMap()
		for ((key, value) in this.entries) {
			entries[key] = value.copy()
		}
		return TomlTable(entries)
	}

	fun int(key: String): Long? = (entries[key] as? TomlInt)?.value

	fun string(key: String): String? = (entries[key] as? TomlString)?.value

	fun table(key: String): TomlTable? = entries[key] as? TomlTable

	fun array(key: String): TomlArray? = entries[key] as? TomlArray
}

/** A value of the TOML that could not be read, with where. */
class TomlException(message: String) : RuntimeException(message)

object BotNavToml {

	/** Writes a key bare when TOML lets it, quoted otherwise. */
	@JvmStatic
	fun appendKey(out: StringBuilder, key: String) {
		var bare: Boolean = key.isNotEmpty()
		for (c in key) {
			if (!(c in 'a'..'z' || c in 'A'..'Z' || c in '0'..'9' || c == '_' || c == '-')) {
				bare = false
			}
		}
		if (bare) {
			out.append(key)
		} else {
			TomlString(key).format(out)
		}
	}

	/** One value from the whole of [text]; anything but blanks and comments after it fails. */
	@JvmStatic
	fun parseValue(text: String): TomlValue {
		val reader = Reader(text, 0)
		val value: TomlValue = reader.value()
		reader.skipBlank()
		if (reader.at < text.length) {
			throw reader.fail("unexpected text after the value")
		}
		return value
	}

	/** An edge of a nav-edges file: its table, where it is in the text, and the comment after it on its line. */
	class EdgeSource(
		@JvmField val table: TomlTable,
		/** The start of the region the edge owns: its line's start when only blanks come before it on the line. */
		@JvmField val regionStart: Int,
		/** The end of the region: past the line's end when only a comma, blanks and a comment follow it. */
		@JvmField val regionEnd: Int,
		@JvmField val comment: String?,
		/** Whether a comma follows the edge in the array. */
		@JvmField val comma: Boolean,
	)

	class EdgesFile(
		@JvmField val edges: ArrayList<EdgeSource>,
		/** Where new edges go: the start of the closing bracket's line (or the bracket when the line holds more). */
		@JvmField val insertAt: Int,
	)

	/** Reads a nav-edges file: one `edges = [ ... ]` array of inline tables. */
	@JvmStatic
	fun parseEdges(text: String): EdgesFile {
		val reader = Reader(text, 0)
		reader.skipBlank()
		reader.require(reader.key() == "edges", "expected edges = [")
		reader.skipSpaces()
		reader.expect('=')
		reader.skipBlank()
		reader.expect('[')
		val edges: ArrayList<EdgeSource> = ArrayList()
		while (true) {
			reader.skipBlank()
			if (reader.peek() == ']') {
				break
			}
			val start: Int = reader.at
			val value: TomlTable = reader.edge()
			val end: Int = reader.at
			reader.skipSpaces()
			var comma = false
			if (reader.peek() == ',') {
				comma = true
				reader.at++
			}
			reader.skipSpaces()
			var comment: String? = null
			var regionEnd: Int = end
			if (reader.peek() == '#') {
				val lineEnd: Int = lineEnd(text, reader.at)
				comment = text.substring(reader.at + 1, lineEnd).trim()
				reader.at = lineEnd
			}
			if (reader.at >= text.length || text[reader.at] == '\n' || text[reader.at] == '\r') {
				regionEnd = afterNewline(text, reader.at)
				reader.at = regionEnd
			} else if (comma) {
				regionEnd = reader.at
			}
			edges.add(EdgeSource(value, regionStartOf(text, start), regionEnd, comment, comma))
		}
		val close: Int = reader.at
		reader.at++
		reader.skipBlank()
		reader.require(reader.at >= text.length, "unexpected text after the edges")
		return EdgesFile(edges, regionStartOf(text, close))
	}

	/** A `[name]` table of an areas file and the region of the text it owns, from its header to the end of its last value's line. */
	class AreaSource(
		@JvmField val name: String,
		@JvmField val table: TomlTable,
		@JvmField val regionStart: Int,
		@JvmField val regionEnd: Int,
	)

	/** Reads an areas file: `[name]` tables of `key = value` lines. */
	@JvmStatic
	fun parseAreas(text: String): ArrayList<AreaSource> {
		val reader = Reader(text, 0)
		val areas: ArrayList<AreaSource> = ArrayList()
		while (true) {
			reader.skipBlank()
			if (reader.at >= text.length) {
				return areas
			}
			val start: Int = reader.at
			if (start != lineStart(text, start)) {
				throw reader.fail("a table header must start its line")
			}
			reader.expect('[')
			reader.skipSpaces()
			val name: String = reader.key()
			reader.skipSpaces()
			reader.expect(']')
			reader.endOfLine()
			val entries: LinkedHashMap<String, TomlValue> = LinkedHashMap()
			var end: Int = reader.at
			while (true) {
				reader.skipBlank()
				if (reader.at >= text.length || reader.peek() == '[') {
					break
				}
				val key: String = reader.key()
				reader.skipSpaces()
				reader.expect('=')
				reader.skipSpaces()
				if (entries.containsKey(key)) {
					throw reader.fail("$key twice in [$name]")
				}
				entries[key] = reader.value()
				reader.endOfLine()
				end = reader.at
			}
			areas.add(AreaSource(name, TomlTable(entries), start, end))
		}
	}

	private fun lineStart(text: String, at: Int): Int {
		var i: Int = at
		while (i > 0 && text[i - 1] != '\n') {
			i--
		}
		return i
	}

	private fun lineEnd(text: String, at: Int): Int {
		var i: Int = at
		while (i < text.length && text[i] != '\n' && text[i] != '\r') {
			i++
		}
		return i
	}

	private fun afterNewline(text: String, at: Int): Int {
		var i: Int = at
		if (i < text.length && text[i] == '\r') {
			i++
		}
		if (i < text.length && text[i] == '\n') {
			i++
		}
		return i
	}

	/** [at]'s line start when only blanks come before it on the line, else [at]. */
	private fun regionStartOf(text: String, at: Int): Int {
		val line: Int = lineStart(text, at)
		for (i in line until at) {
			if (text[i] != ' ' && text[i] != '\t') {
				return at
			}
		}
		return line
	}

	/** The line and column of [at], from 1, for messages. */
	@JvmStatic
	fun position(text: String, at: Int): String {
		var line = 1
		var column = 1
		for (i in 0 until minOf(at, text.length)) {
			if (text[i] == '\n') {
				line++
				column = 1
			} else {
				column++
			}
		}
		return "line $line column $column"
	}

	private class Reader(val text: String, var at: Int) {

		fun fail(message: String): TomlException = TomlException(message + " at " + position(text, at))

		fun peek(): Char = if (at < text.length) text[at] else '\u0000'

		fun require(ok: Boolean, message: String) {
			if (!ok) {
				throw fail(message)
			}
		}

		/** An edge of the edges array: an inline table. */
		fun edge(): TomlTable = value() as? TomlTable ?: throw fail("an edge must be a table")

		fun expect(c: Char) {
			if (peek() != c) {
				throw fail("expected '$c'")
			}
			at++
		}

		fun skipSpaces() {
			while (at < text.length && (text[at] == ' ' || text[at] == '\t')) {
				at++
			}
		}

		/** Spaces, newlines and comments. */
		fun skipBlank() {
			while (at < text.length) {
				val c: Char = text[at]
				if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
					at++
				} else if (c == '#') {
					at = lineEnd(text, at)
				} else {
					return
				}
			}
		}

		/** Spaces and a comment up to the end of the line, then the newline. */
		fun endOfLine() {
			skipSpaces()
			if (peek() == '#') {
				at = lineEnd(text, at)
			}
			if (at < text.length && text[at] != '\n' && text[at] != '\r') {
				throw fail("expected the end of the line")
			}
			at = afterNewline(text, at)
		}

		fun key(): String {
			if (peek() == '"') {
				return string()
			}
			val start: Int = at
			while (at < text.length) {
				val c: Char = text[at]
				if (c in 'a'..'z' || c in 'A'..'Z' || c in '0'..'9' || c == '_' || c == '-') {
					at++
				} else {
					break
				}
			}
			if (at == start) {
				throw fail("expected a key")
			}
			return text.substring(start, at)
		}

		fun value(): TomlValue {
			val c: Char = peek()
			return when {
				c == '"' -> TomlString(string())
				c == '{' -> table()
				c == '[' -> array()
				c == '-' || c == '+' || c in '0'..'9' -> number()
				text.startsWith("true", at) -> {
					at += 4
					TomlBool(true)
				}
				text.startsWith("false", at) -> {
					at += 5
					TomlBool(false)
				}
				else -> throw fail("expected a value")
			}
		}

		private fun number(): TomlValue {
			val start: Int = at
			if (peek() == '-' || peek() == '+') {
				at++
			}
			while (at < text.length && (text[at] in '0'..'9' || text[at] == '_')) {
				at++
			}
			val value: Long = text.substring(start, at).replace("_", "").removePrefix("+").toLongOrNull() ?: throw fail("bad number")
			return TomlInt(value)
		}

		private fun table(): TomlTable {
			expect('{')
			val entries: LinkedHashMap<String, TomlValue> = LinkedHashMap()
			skipBlank()
			if (peek() == '}') {
				at++
				return TomlTable(entries)
			}
			while (true) {
				skipBlank()
				val key: String = key()
				skipBlank()
				expect('=')
				skipBlank()
				if (entries.containsKey(key)) {
					throw fail("$key twice in a table")
				}
				entries[key] = value()
				skipBlank()
				when (peek()) {
					',' -> {
						at++
						skipBlank()
						// Void's multi-line tables may end with a comma
						if (peek() == '}') {
							at++
							return TomlTable(entries)
						}
					}
					'}' -> {
						at++
						return TomlTable(entries)
					}
					else -> throw fail("expected ',' or '}'")
				}
			}
		}

		private fun array(): TomlArray {
			expect('[')
			val items: ArrayList<TomlValue> = ArrayList()
			while (true) {
				skipBlank()
				if (peek() == ']') {
					at++
					return TomlArray(items)
				}
				items.add(value())
				skipBlank()
				when (peek()) {
					',' -> at++
					']' -> {
						at++
						return TomlArray(items)
					}
					else -> throw fail("expected ',' or ']'")
				}
			}
		}

		fun string(): String {
			expect('"')
			val out: StringBuilder = StringBuilder()
			while (at < text.length) {
				val c: Char = text[at++]
				when (c) {
					'"' -> return out.toString()
					'\n' -> throw fail("a string runs past the end of its line")
					'\\' -> escape(out)
					else -> out.append(c)
				}
			}
			throw fail("a string is not closed")
		}

		private fun escape(out: StringBuilder) {
			require(at < text.length, "bad escape")
			when (val c: Char = text[at++]) {
				'"', '\\' -> out.append(c)
				'n' -> out.append('\n')
				't' -> out.append('\t')
				'r' -> out.append('\r')
				'u' -> {
					val code: Int? = if (at + 4 <= text.length) text.substring(at, at + 4).toIntOrNull(16) else null
					require(code != null, "bad escape")
					out.append(code!!.toChar())
					at += 4
				}
				else -> throw fail("bad escape \\$c")
			}
		}
	}
}
