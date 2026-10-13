/*
 * Not part of the original client: 634HD (toolkit 6). A small JSON reader for the 634HD data files
 * (HdContent). Every value keeps the line it starts on, so a warning about a bad entry can name its
 * file and line. No dependency: the client verifies every library it ships.
 */
package com.jagex.game.runetek5.graphics.hd.content

sealed class Json(val line: Int) {
    class Obj(line: Int, val fields: LinkedHashMap<String, Json>) : Json(line) {
        operator fun get(name: String): Json? = fields[name]
    }
    class Arr(line: Int, val items: ArrayList<Json>) : Json(line)
    class Str(line: Int, val value: String) : Json(line)
    class Num(line: Int, val value: Double) : Json(line)
    class Bool(line: Int, val value: Boolean) : Json(line)
    class Null(line: Int) : Json(line)

    fun typeName(): String = when (this) {
        is Obj -> "object"
        is Arr -> "array"
        is Str -> "string"
        is Num -> if (value == Math.floor(value) && !value.isInfinite()) "integer" else "number"
        is Bool -> "boolean"
        is Null -> "null"
    }

    class ParseException(message: String, val line: Int) : Exception(message)

    companion object {
        fun parse(text: String): Json {
            val reader = Reader(text)
            reader.skip()
            val value = reader.value()
            reader.skip()
            if (reader.at < text.length) throw ParseException("unexpected text after the value", reader.line)
            return value
        }
    }

    private class Reader(val text: String) {
        var at: Int = 0
        var line: Int = 1

        fun skip() {
            while (at < text.length) {
                val c = text[at]
                if (c == '\n') {
                    line++
                    at++
                } else if (c == ' ' || c == '\t' || c == '\r') {
                    at++
                } else if (c == '/' && at + 1 < text.length && text[at + 1] == '/') {
                    // comments are allowed, as people write notes in data files
                    while (at < text.length && text[at] != '\n') at++
                } else {
                    return
                }
            }
        }

        fun expect(c: Char) {
            skip()
            if (at >= text.length || text[at] != c) throw ParseException("expected '" + c + "'", line)
            at++
        }

        fun value(): Json {
            skip()
            if (at >= text.length) throw ParseException("unexpected end of file", line)
            val start = line
            return when (text[at]) {
                '{' -> obj()
                '[' -> arr()
                '"' -> Str(start, string())
                't' -> word("true", Bool(start, true))
                'f' -> word("false", Bool(start, false))
                'n' -> word("null", Null(start))
                else -> number()
            }
        }

        fun word(word: String, value: Json): Json {
            if (!text.startsWith(word, at)) throw ParseException("unexpected '" + text[at] + "'", line)
            at += word.length
            return value
        }

        fun obj(): Json {
            val start = line
            at++
            val fields = LinkedHashMap<String, Json>()
            skip()
            if (at < text.length && text[at] == '}') {
                at++
                return Obj(start, fields)
            }
            while (true) {
                skip()
                if (at >= text.length || text[at] != '"') throw ParseException("expected a field name", line)
                val name = string()
                expect(':')
                fields[name] = value()
                skip()
                if (at < text.length && text[at] == ',') {
                    at++
                    skip()
                    // a trailing comma is forgiven
                    if (at < text.length && text[at] == '}') {
                        at++
                        return Obj(start, fields)
                    }
                    continue
                }
                expect('}')
                return Obj(start, fields)
            }
        }

        fun arr(): Json {
            val start = line
            at++
            val items = ArrayList<Json>()
            skip()
            if (at < text.length && text[at] == ']') {
                at++
                return Arr(start, items)
            }
            while (true) {
                items.add(value())
                skip()
                if (at < text.length && text[at] == ',') {
                    at++
                    skip()
                    if (at < text.length && text[at] == ']') {
                        at++
                        return Arr(start, items)
                    }
                    continue
                }
                expect(']')
                return Arr(start, items)
            }
        }

        fun string(): String {
            at++
            val out = StringBuilder()
            while (true) {
                if (at >= text.length) throw ParseException("unterminated string", line)
                val c = text[at++]
                if (c == '"') return out.toString()
                if (c == '\n') throw ParseException("newline in a string", line)
                if (c == '\\') {
                    if (at >= text.length) throw ParseException("unterminated string", line)
                    when (val e = text[at++]) {
                        'n' -> out.append('\n')
                        't' -> out.append('\t')
                        'r' -> out.append('\r')
                        'b' -> out.append('\b')
                        'f' -> out.append('\u000C')
                        'u' -> {
                            if (at + 4 > text.length) throw ParseException("bad \\u escape", line)
                            out.append(text.substring(at, at + 4).toInt(16).toChar())
                            at += 4
                        }
                        else -> out.append(e)
                    }
                } else {
                    out.append(c)
                }
            }
        }

        fun number(): Json {
            val start = at
            if (text[at] == '-' || text[at] == '+') at++
            while (at < text.length && (text[at].isDigit() || text[at] == '.' || text[at] == 'e' || text[at] == 'E' || text[at] == '-' || text[at] == '+')) at++
            val token = text.substring(start, at)
            val value = token.toDoubleOrNull() ?: throw ParseException("unexpected '" + (if (token.isEmpty()) text[start].toString() else token) + "'", line)
            return Num(line, value)
        }
    }
}
