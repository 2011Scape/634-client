package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the JSON of Discord's local RPC, written by hand (the client
 * has no JSON library). The writer makes the handshake and the SET_ACTIVITY command; the reader
 * turns a frame into maps, lists, strings, longs, booleans and nulls, enough to find the event
 * name, the error and the user in what Discord sends back.
 */
object DiscordJson {

	private const val HEX: String = "0123456789ABCDEF"

	/** The handshake (opcode 0) payload of the application [applicationId]. */
	@JvmStatic
	fun handshake(applicationId: String): String {
		val out: StringBuilder = StringBuilder("{\"v\":1,\"client_id\":\"")
		appendEscaped(out, applicationId)
		return out.append("\"}").toString()
	}

	/**
	 * The SET_ACTIVITY command (opcode 1) showing [activity] for process [pid], or clearing the
	 * presence when it is null. Discord answers with the same [nonce]. An empty state or details
	 * is left out: Discord refuses an empty string ("details" is not allowed to be empty, code 4000).
	 */
	@JvmStatic
	fun setActivity(pid: Long, nonce: String, activity: DiscordActivity?): String {
		val out: StringBuilder = StringBuilder("{\"cmd\":\"SET_ACTIVITY\",\"args\":{\"pid\":").append(pid)
		if (activity != null) {
			out.append(",\"activity\":{")
			if (activity.state.isNotEmpty()) {
				appendString(out, "state", activity.state)
				out.append(',')
			}
			if (activity.details.isNotEmpty()) {
				appendString(out, "details", activity.details)
				out.append(',')
			}
			if (activity.startTimestamp > 0) {
				out.append("\"timestamps\":{\"start\":").append(activity.startTimestamp).append("},")
			}
			out.append("\"assets\":{")
			appendString(out, "large_image", LARGE_IMAGE_KEY)
			out.append(',')
			appendString(out, "large_text", activity.largeImageText)
			val smallImageKey: String? = activity.smallImageKey
			if (smallImageKey != null && smallImageKey.isNotEmpty()) {
				out.append(',')
				appendString(out, "small_image", smallImageKey)
			}
			out.append("},\"instance\":false}")
		}
		out.append("},\"nonce\":\"")
		appendEscaped(out, nonce)
		return out.append("\"}").toString()
	}

	private fun appendString(out: StringBuilder, key: String, value: String) {
		out.append('"').append(key).append("\":\"")
		appendEscaped(out, value)
		out.append('"')
	}

	private fun appendEscaped(out: StringBuilder, text: String) {
		for (c in text) {
			when {
				c == '"' -> out.append("\\\"")
				c == '\\' -> out.append("\\\\")
				c < ' ' -> out.append("\\u00").append(HEX[(c.code shr 4) and 0xF]).append(HEX[c.code and 0xF])
				else -> out.append(c)
			}
		}
	}

	/** The value [text] holds (a map, list, String, Long, Double, Boolean or null), or null when it is not JSON. */
	@JvmStatic
	fun parse(text: String): Any? {
		val reader: Reader = Reader(text)
		val value: Any? = reader.value()
		return if (reader.failed || !reader.atEnd()) null else value
	}

	/** The string under [key] of the object [value], or null when it is not one. */
	@JvmStatic
	fun string(value: Any?, key: String): String? {
		val field: Any? = (value as? Map<*, *>)?.get(key)
		return field as? String
	}

	/** The object under [key] of the object [value], or null when it is not one. */
	@JvmStatic
	fun obj(value: Any?, key: String): Map<*, *>? {
		return (value as? Map<*, *>)?.get(key) as? Map<*, *>
	}

	/** The number under [key] of the object [value], or null when it is not one. */
	@JvmStatic
	fun long(value: Any?, key: String): Long? {
		val field: Any? = (value as? Map<*, *>)?.get(key)
		return if (field is Long) field else if (field is Double) field.toLong() else null
	}

	private class Reader(private val text: String) {
		private var at: Int = 0

		var failed: Boolean = false

		fun atEnd(): Boolean {
			skipSpace()
			return at >= text.length
		}

		private fun skipSpace() {
			while (at < text.length && text[at] <= ' ') {
				at++
			}
		}

		private fun take(c: Char): Boolean {
			skipSpace()
			if (at < text.length && text[at] == c) {
				at++
				return true
			}
			return false
		}

		private fun fail(): Any? {
			failed = true
			return null
		}

		fun value(): Any? {
			skipSpace()
			if (at >= text.length) {
				return fail()
			}
			return when (text[at]) {
				'{' -> obj()
				'[' -> array()
				'"' -> string()
				't' -> literal("true", true)
				'f' -> literal("false", false)
				'n' -> literal("null", null)
				else -> number()
			}
		}

		private fun literal(word: String, value: Any?): Any? {
			if (text.startsWith(word, at)) {
				at += word.length
				return value
			}
			return fail()
		}

		private fun obj(): Any? {
			val fields: HashMap<String, Any?> = HashMap()
			at++
			if (take('}')) {
				return fields
			}
			while (!failed) {
				skipSpace()
				if (at >= text.length || text[at] != '"') {
					return fail()
				}
				val key: String = string() ?: return fail()
				if (!take(':')) {
					return fail()
				}
				fields[key] = value()
				if (failed) {
					return null
				}
				if (take('}')) {
					return fields
				}
				if (!take(',')) {
					return fail()
				}
			}
			return null
		}

		private fun array(): Any? {
			val items: ArrayList<Any?> = ArrayList()
			at++
			if (take(']')) {
				return items
			}
			while (!failed) {
				items.add(value())
				if (failed) {
					return null
				}
				if (take(']')) {
					return items
				}
				if (!take(',')) {
					return fail()
				}
			}
			return null
		}

		private fun number(): Any? {
			val start: Int = at
			while (at < text.length && (text[at] in '0'..'9' || text[at] == '-' || text[at] == '+' || text[at] == '.' || text[at] == 'e' || text[at] == 'E')) {
				at++
			}
			val digits: String = text.substring(start, at)
			if (digits.isEmpty()) {
				return fail()
			}
			val whole: Long? = digits.toLongOrNull()
			if (whole != null) {
				return whole
			}
			return digits.toDoubleOrNull() ?: fail()
		}

		private fun string(): String? {
			at++
			val out: StringBuilder = StringBuilder()
			while (at < text.length) {
				val c: Char = text[at++]
				when (c) {
					'"' -> return out.toString()
					'\\' -> if (!escape(out)) {
						failed = true
						return null
					}
					else -> out.append(c)
				}
			}
			failed = true
			return null
		}

		private fun escape(out: StringBuilder): Boolean {
			if (at >= text.length) {
				return false
			}
			when (val c: Char = text[at++]) {
				'"', '\\', '/' -> out.append(c)
				'b' -> out.append('\b')
				'f' -> out.append('\u000C')
				'n' -> out.append('\n')
				'r' -> out.append('\r')
				't' -> out.append('\t')
				'u' -> {
					if (at + 4 > text.length) {
						return false
					}
					val code: Int = text.substring(at, at + 4).toIntOrNull(16) ?: return false
					out.append(code.toChar())
					at += 4
				}
				else -> return false
			}
			return true
		}
	}

	/** The key of the big icon: the client's own, uploaded to the Discord application under this name. */
	const val LARGE_IMAGE_KEY: String = "default"
}
