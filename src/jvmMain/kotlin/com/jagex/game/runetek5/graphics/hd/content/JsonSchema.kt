/*
 * Not part of the original client: 634HD (toolkit 6). Checks a 634HD data file against its schema
 * (src/jvmMain/resources/634hd/data/<name>.schema.json), a subset of JSON Schema: type, properties,
 * required, additionalProperties, items, minItems, maxItems, enum, minimum, maximum, pattern and
 * local $ref ("#/$defs/name"). Each problem names the line of the value it is about.
 */
package com.jagex.game.runetek5.graphics.hd.content

class JsonSchema(private val root: Json.Obj) {

    class Problem(val line: Int, val message: String)

    /** The problems with [value]; empty when it conforms. */
    fun check(value: Json): List<Problem> {
        val problems = ArrayList<Problem>()
        check(value, root, "", problems)
        return problems
    }

    private fun resolve(schema: Json.Obj): Json.Obj {
        val ref = (schema["\$ref"] as? Json.Str)?.value ?: return schema
        var node: Json = root
        for (part in ref.removePrefix("#/").split('/')) {
            node = (node as? Json.Obj)?.get(part) ?: return schema
        }
        return node as? Json.Obj ?: schema
    }

    private fun check(value: Json, schemaIn: Json.Obj, path: String, problems: MutableList<Problem>) {
        val schema = resolve(schemaIn)
        val where = if (path.isEmpty()) "" else "$path: "
        val type = schema["type"]
        if (type != null) {
            val allowed = when (type) {
                is Json.Str -> listOf(type.value)
                is Json.Arr -> type.items.mapNotNull { (it as? Json.Str)?.value }
                else -> emptyList()
            }
            val actual = value.typeName()
            val ok = allowed.any { it == actual || (it == "number" && actual == "integer") }
            if (!ok) {
                problems.add(Problem(value.line, where + "expected " + allowed.joinToString(" or ") + ", found " + actual))
                return
            }
        }
        val enum = schema["enum"] as? Json.Arr
        if (enum != null && enum.items.none { same(it, value) }) {
            problems.add(Problem(value.line, where + "must be one of " + enum.items.joinToString(", ") { text(it) }))
        }
        if (value is Json.Num) {
            val minimum = (schema["minimum"] as? Json.Num)?.value
            val maximum = (schema["maximum"] as? Json.Num)?.value
            if (minimum != null && value.value < minimum) problems.add(Problem(value.line, where + "must be at least " + text(schema["minimum"]!!)))
            if (maximum != null && value.value > maximum) problems.add(Problem(value.line, where + "must be at most " + text(schema["maximum"]!!)))
        }
        if (value is Json.Str) {
            val pattern = (schema["pattern"] as? Json.Str)?.value
            if (pattern != null && !Regex(pattern).containsMatchIn(value.value)) {
                problems.add(Problem(value.line, where + "\"" + value.value + "\" does not match " + pattern))
            }
        }
        if (value is Json.Arr) {
            val minItems = (schema["minItems"] as? Json.Num)?.value?.toInt()
            val maxItems = (schema["maxItems"] as? Json.Num)?.value?.toInt()
            if (minItems != null && value.items.size < minItems) problems.add(Problem(value.line, where + "needs at least " + minItems + " items"))
            if (maxItems != null && value.items.size > maxItems) problems.add(Problem(value.line, where + "takes at most " + maxItems + " items"))
            val items = schema["items"] as? Json.Obj
            if (items != null) {
                for (i in value.items.indices) check(value.items[i], items, "$path[$i]", problems)
            }
        }
        if (value is Json.Obj) {
            val properties = schema["properties"] as? Json.Obj
            val required = schema["required"] as? Json.Arr
            if (required != null) {
                for (name in required.items) {
                    val field = (name as? Json.Str)?.value ?: continue
                    if (value[field] == null) problems.add(Problem(value.line, where + "missing \"" + field + "\""))
                }
            }
            val additional = schema["additionalProperties"]
            for ((name, field) in value.fields) {
                val propertySchema = properties?.get(name) as? Json.Obj
                if (propertySchema != null) {
                    check(field, propertySchema, if (path.isEmpty()) name else "$path.$name", problems)
                } else if (additional is Json.Bool && !additional.value) {
                    problems.add(Problem(field.line, where + "unknown field \"" + name + "\""))
                } else if (additional is Json.Obj) {
                    check(field, additional, if (path.isEmpty()) name else "$path.$name", problems)
                }
            }
        }
    }

    private fun same(a: Json, b: Json): Boolean = when {
        a is Json.Str && b is Json.Str -> a.value == b.value
        a is Json.Num && b is Json.Num -> a.value == b.value
        a is Json.Bool && b is Json.Bool -> a.value == b.value
        else -> false
    }

    private fun text(value: Json): String = when (value) {
        is Json.Str -> "\"" + value.value + "\""
        is Json.Num -> if (value.value == Math.floor(value.value)) value.value.toLong().toString() else value.value.toString()
        is Json.Bool -> value.value.toString()
        else -> value.typeName()
    }
}
