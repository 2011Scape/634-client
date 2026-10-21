package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.PluginConfigImpl
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Object Markers' saved form, a JSON array of
 * ObjectPoint objects as its Gson writes them (fields in the class's order, nulls left out, a colour
 * as `#AARRGGBB`), read back in any order with unknown fields skipped.
 */
object ObjectPointJson {

	@JvmStatic
	fun format(points: List<ObjectIndicators.ObjectPoint>): String {
		val out: StringBuilder = StringBuilder("[")
		for (i in points.indices) {
			if (i > 0) {
				out.append(',')
			}
			val p: ObjectIndicators.ObjectPoint = points[i]
			out.append("{\"id\":").append(p.id).append(",\"name\":")
			quote(out, p.name)
			out.append(",\"regionId\":").append(p.regionId)
			out.append(",\"regionX\":").append(p.regionX)
			out.append(",\"regionY\":").append(p.regionY)
			out.append(",\"z\":").append(p.z)
			p.borderColour?.let { out.append(",\"color\":\"").append(PluginConfigImpl.formatColour(it)).append('"') }
			p.fillColour?.let { out.append(",\"fillColor\":\"").append(PluginConfigImpl.formatColour(it)).append('"') }
			p.hull?.let { out.append(",\"hull\":").append(it) }
			p.outline?.let { out.append(",\"outline\":").append(it) }
			p.clickbox?.let { out.append(",\"clickbox\":").append(it) }
			p.tile?.let { out.append(",\"tile\":").append(it) }
			out.append('}')
		}
		return out.append(']').toString()
	}

	private fun quote(out: StringBuilder, text: String) {
		out.append('"')
		for (c in text) {
			when (c) {
				'"' -> out.append("\\\"")
				'\\' -> out.append("\\\\")
				else -> if (c < ' ') out.append("\\u").append(c.code.toString(16).padStart(4, '0')) else out.append(c)
			}
		}
		out.append('"')
	}

	/** The points in [text]; null when it is not a JSON array of objects. */
	@JvmStatic
	fun parse(text: String): ArrayList<ObjectIndicators.ObjectPoint>? {
		val reader: Reader = Reader(text)
		return reader.points()
	}

	private class Reader(private val text: String) {
		private var at: Int = 0

		fun points(): ArrayList<ObjectIndicators.ObjectPoint>? {
			skip()
			if (!take('[')) {
				return null
			}
			val out: ArrayList<ObjectIndicators.ObjectPoint> = ArrayList()
			while (true) {
				skip()
				if (take(']')) {
					break
				}
				take(',')
				skip()
				out.add(point() ?: return null)
			}
			return out
		}

		private fun point(): ObjectIndicators.ObjectPoint? {
			if (!take('{')) {
				return null
			}
			val fields: HashMap<String, String?> = HashMap()
			while (true) {
				skip()
				if (take('}')) {
					break
				}
				take(',')
				skip()
				val name: String = string() ?: return null
				skip()
				if (!take(':')) {
					return null
				}
				skip()
				fields[name] = value()
			}
			val p: ObjectIndicators.ObjectPoint = ObjectIndicators.ObjectPoint(
				fields["id"]?.toIntOrNull() ?: -1,
				fields["name"] ?: "",
				fields["regionId"]?.toIntOrNull() ?: return null,
				fields["regionX"]?.toIntOrNull() ?: return null,
				fields["regionY"]?.toIntOrNull() ?: return null,
				fields["z"]?.toIntOrNull() ?: 0,
			)
			p.borderColour = fields["color"]?.let { PluginConfigImpl.parseColour(it) }
			p.fillColour = fields["fillColor"]?.let { PluginConfigImpl.parseColour(it) }
			p.hull = fields["hull"]?.toBooleanStrictOrNull()
			p.outline = fields["outline"]?.toBooleanStrictOrNull()
			p.clickbox = fields["clickbox"]?.toBooleanStrictOrNull()
			p.tile = fields["tile"]?.toBooleanStrictOrNull()
			return p
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

		private fun string(): String? {
			if (!take('"')) {
				return null
			}
			val out: StringBuilder = StringBuilder()
			while (at < text.length && text[at] != '"') {
				var c: Char = text[at++]
				if (c == '\\' && at < text.length) {
					c = text[at++]
					if (c == 'u') {
						c = text.substring(at, minOf(at + 4, text.length)).toIntOrNull(16)?.toChar() ?: '?'
						at += 4
					} else if (c == 'n') {
						c = '\n'
					}
				}
				out.append(c)
			}
			at++
			return out.toString()
		}

		/** Whether [c] ends a value at nesting [depth]: a comma or a closing bracket outside any nested value. */
		private fun endsValue(c: Char, depth: Int): Boolean {
			return depth == 0 && (c == ',' || c == '}' || c == ']')
		}

		/** A value as text: a string's contents, a number or true/false as written, null for null; a nested value is skipped. */
		private fun value(): String? {
			if (at < text.length && text[at] == '"') {
				return string()
			}
			val start: Int = at
			var depth: Int = 0
			while (at < text.length && !endsValue(text[at], depth)) {
				val c: Char = text[at]
				if (c == '{' || c == '[') {
					depth++
				} else if (c == '}' || c == ']') {
					depth--
				}
				at++
			}
			val raw: String = text.substring(start, at).trim()
			return if (raw == "null" || raw.startsWith("{") || raw.startsWith("[")) null else raw
		}
	}
}
