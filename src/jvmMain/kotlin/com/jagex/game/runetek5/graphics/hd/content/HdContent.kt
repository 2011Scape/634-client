/*
 * Not part of the original client: 634HD (toolkit 6). The data 634HD's features read, from versioned
 * JSON files keyed by 634 ids and coordinates (Content pipeline in docs/634hd-plan.md):
 *
 *   lights.json            point lights on objects, NPCs, spot animations, projectiles
 *   environments.json      per-area sun, ambient, fog, sky, colour grading, water tint
 *   materials.json         extra maps (normal, ORM, height, emissive) per texture
 *   model_overrides.json   materials, wind and shadows per model, object, face colour or area
 *   foliage.json           objects that sway in the wind
 *
 * Each file is checked against its schema (<name>.schema.json); an entry that does not conform is
 * skipped with a warning naming its file and line, and never stops the client. The files come from
 * the jar (src/jvmMain/resources/634hd/data), or from a folder: client.hd.dataDir, or with
 * client.debug the repository's own src/jvmMain/resources/634hd/data when the client runs from the
 * repository. Files in a folder are watched and loaded again when they change (hot reload).
 */
package com.jagex.game.runetek5.graphics.hd.content

import java.io.File

class HdContent {

    // the debug panel edits these in place, then writes the file (saveLights)
    class LightDef(
        val name: String, val objects: IntArray, val npcs: IntArray, val spotAnimations: IntArray, val projectiles: IntArray,
        var red: Float, var green: Float, var blue: Float, var radius: Float, var intensity: Float,
        var offsetX: Float, var offsetY: Float, var offsetZ: Float, var flicker: Int,
    )

    class EnvironmentDef(val name: String, val line: Int) {
        var regions: IntArray? = null
        var bounds: Array<IntArray>? = null
        var levels: IntArray? = null
        var sunPitch: Float = Float.NaN
        var sunYaw: Float = Float.NaN
        var sunColour: FloatArray? = null
        var sunIntensity: Float = 1.0f
        var ambientColour: FloatArray? = null
        var ambientIntensity: Float = 1.0f
        var fogColour: FloatArray? = null
        var fogStart: Float = Float.NaN
        var fogEnd: Float = Float.NaN
        var skyTop: FloatArray? = null
        var skyHorizon: FloatArray? = null
        var heightFogDensity: Float = 0.0f
        var heightFogTop: Float = 0.0f
        var lut: String? = null
        var waterTint: FloatArray? = null
        /** water: how much of the game's water colour the tint replaces (NaN: 0.45), waves' height, speed, size, glint */
        var waterStrength: Float = Float.NaN
        var waterWaves: Float = 1.0f
        var waterSpeed: Float = 1.0f
        var waterSize: Float = 1.0f
        var waterGlint: Float = 1.0f

        fun contains(x: Int, y: Int, level: Int): Boolean {
            val levels = this.levels
            if (levels != null && levels.indexOf(level) < 0) return false
            val regions = this.regions
            val bounds = this.bounds
            if (regions == null && bounds == null) return true
            if (regions != null && regions.indexOf(((x shr 6) shl 8) or (y shr 6)) >= 0) return true
            if (bounds != null) {
                for (b in bounds) {
                    if (x >= minOf(b[0], b[2]) && x <= maxOf(b[0], b[2]) && y >= minOf(b[1], b[3]) && y <= maxOf(b[1], b[3])) return true
                }
            }
            return false
        }

        val isDefault: Boolean get() = regions == null && bounds == null
    }

    class MaterialDef(val texture: Int, val line: Int) {
        /** A material of 634HD's own (texture CUSTOM_MATERIALS and up): its colour map, in its folder. */
        var albedo: String? = null
        var normal: String? = null
        var orm: String? = null
        var height: String? = null
        var emissive: String? = null
        var scrollU: Float = 0.0f
        var scrollV: Float = 0.0f
        var specular: Float = 0.5f
        var emissiveStrength: Float = 1.0f
        var parallax: Float = 0.04f
    }

    /**
     * The bytes of [name] in material [texture]'s folder (634hd/materials/<texture>/): from the data
     * folder's sibling when content comes from a folder, else from the jar.
     */
    fun materialFile(texture: Int, name: String): ByteArray? {
        val folder: File? = this.folder
        val file: File? = if (folder != null) File(folder, "../materials/$texture/$name") else null
        if (file != null && file.isFile) return file.readBytes()
        val stream = HdContent::class.java.getResourceAsStream("/634hd/materials/$texture/$name") ?: return null
        return stream.use { it.readBytes() }
    }

    class OverrideDef(val name: String) {
        var objects: IntArray? = null
        var models: IntArray? = null
        var faceColours: IntArray? = null
        var area: IntArray? = null
        var material: Int = -1
        var wind: Boolean = false
        var castShadows: Boolean = true
        /** How [material] is mapped on the faces: "world" (box mapping in world space) or "model" (the model's own space). */
        var uv: String = "world"
        /** Tiles per repeat of the material. */
        var uvScale: Float = 1.0f
        /** The material replaces the game's textures too, not only untextured faces. */
        var replace: Boolean = false
    }

    /**
     * The override for loc [id] at absolute tile ([x], [z]): one listing the loc (and, if it has an
     * area, covering the tile), else one with only an area covering the tile; null for none.
     */
    fun overrideFor(id: Int, x: Int, z: Int): OverrideDef? {
        val byId: OverrideDef? = overridesByObject[id]
        if (byId != null && (byId.area == null || inArea(byId.area!!, x, z))) return byId
        for (o in overrides) {
            if ((o.objects == null || o.objects!!.isEmpty()) && o.area != null && inArea(o.area!!, x, z)) return o
        }
        return null
    }

    private fun inArea(area: IntArray, x: Int, z: Int): Boolean = area.size >= 4 && x >= area[0] && z >= area[1] && x <= area[2] && z <= area[3]

    /** A tile override (tile_overrides.json): a material for the tiles it matches (worldTextures). */
    class TileOverrideDef(val name: String) {
        var underlays: IntArray? = null
        var overlays: IntArray? = null
        var area: IntArray? = null
        var material: Int = -1
        /** Tiles per repeat of the material. */
        var scale: Float = 1.0f
        /** The material replaces the game's own tile textures too, not only untextured tiles. */
        var replace: Boolean = false
    }

    /** The first tile override matching absolute tile ([x], [z]) with these floor ids, or null. */
    fun tileOverrideFor(x: Int, z: Int, underlayId: Int, overlayId: Int): TileOverrideDef? {
        for (o in tileOverrides) {
            val area: IntArray? = o.area
            if (area != null && !inArea(area, x, z)) continue
            val underlays: IntArray? = o.underlays
            if (underlays != null && underlays.indexOf(underlayId) < 0) continue
            val overlays: IntArray? = o.overlays
            if (overlays != null && overlays.indexOf(overlayId) < 0) continue
            if (area == null && underlays == null && overlays == null) continue
            return o
        }
        return null
    }

    private fun parseTileOverride(o: Json.Obj): TileOverrideDef {
        val d = TileOverrideDef(str(o["name"]) ?: "")
        o["underlays"]?.let { d.underlays = ids(it) }
        o["overlays"]?.let { d.overlays = ids(it) }
        o["area"]?.let { d.area = ids(it) }
        d.material = (o["material"] as? Json.Num)?.value?.toInt() ?: -1
        d.scale = num(o["scale"], 1.0f)
        d.replace = (o["replace"] as? Json.Bool)?.value ?: false
        return d
    }

    class FoliageDef(val objects: IntArray, val strength: Float, val frequency: Float)

    /** Bumped whenever any file is (re)loaded, so features can rebuild what they made from it. */
    @Volatile
    var version: Int = 0
        private set

    var lights: List<LightDef> = emptyList()
        private set
    var environments: List<EnvironmentDef> = emptyList()
        private set
    var materials: Map<Int, MaterialDef> = emptyMap()
        private set
    var overrides: List<OverrideDef> = emptyList()
        private set
    var foliage: Map<Int, FoliageDef> = emptyMap()
        private set
    var tileOverrides: List<TileOverrideDef> = emptyList()
        private set

    /** Light definitions by kind and id, for the per-frame lookups. */
    val lightsByObject = HashMap<Int, LightDef>()
    val lightsByNpc = HashMap<Int, LightDef>()
    val lightsBySpotAnimation = HashMap<Int, LightDef>()
    val lightsByProjectile = HashMap<Int, LightDef>()
    val overridesByObject = HashMap<Int, OverrideDef>()

    /** The folder the files are read from, or null for the jar. */
    val folder: File? = folder()

    private val modified = HashMap<String, Long>()
    private var lastCheck: Long = 0L

    /** Warnings from the last load, for the debug panel. */
    val warnings = ArrayList<String>()

    companion object {
        val FILES = arrayOf("lights", "environments", "materials", "model_overrides", "foliage", "tile_overrides")

        private fun folder(): File? {
            val configured = System.getProperty("client.hd.dataDir")
            if (configured != null) return File(configured)
            if (System.getProperty("client.debug") != null) {
                val repository = File("src/jvmMain/resources/634hd/data")
                if (repository.isDirectory) return repository.absoluteFile
            }
            return null
        }

        fun colour(text: String): FloatArray {
            val rgb = text.substring(1).toInt(16)
            return floatArrayOf(((rgb shr 16) and 0xFF) / 255.0f, ((rgb shr 8) and 0xFF) / 255.0f, (rgb and 0xFF) / 255.0f)
        }
    }

    init {
        loadAll()
    }

    /** The text of a data file or schema, from the folder or the jar; null if there is none. */
    fun read(name: String): String? {
        val folder = this.folder
        if (folder != null) {
            val file = File(folder, name)
            if (file.isFile) return file.readText()
        }
        val stream = HdContent::class.java.getResourceAsStream("/634hd/data/" + name) ?: return null
        return stream.use { String(it.readBytes(), Charsets.UTF_8) }
    }

    /** Where a data file is written by the debug panel (the folder, or the repository's when running from it). */
    fun writableFile(name: String): File? {
        val folder = this.folder ?: return null
        return File(folder, name)
    }

    /** Called every frame: in a folder, files that changed are loaded again (at most once a second). */
    fun poll() {
        val folder = this.folder ?: return
        val now = System.currentTimeMillis()
        if (now - lastCheck < 1000L) return
        lastCheck = now
        var changed = false
        for (name in FILES) {
            val file = File(folder, "$name.json")
            val time = if (file.isFile) file.lastModified() else 0L
            if (modified[name] != time) {
                modified[name] = time
                changed = true
            }
        }
        if (changed) {
            loadAll()
            println("634HD: data reloaded from " + folder)
        }
    }

    fun loadAll() {
        warnings.clear()
        lights = parseList("lights", "lights", ::parseLight)
        environments = parseList("environments", "environments", ::parseEnvironment)
        val materialList = parseList("materials", "materials", ::parseMaterial)
        materials = materialList.associateBy { it.texture }
        overrides = parseList("model_overrides", "overrides", ::parseOverride)
        tileOverrides = parseList("tile_overrides", "tiles", ::parseTileOverride)
        foliage = HashMap<Int, FoliageDef>().also { map ->
            for (f in parseList("foliage", "foliage", ::parseFoliage)) for (id in f.objects) map[id] = f
        }
        lightsByObject.clear(); lightsByNpc.clear(); lightsBySpotAnimation.clear(); lightsByProjectile.clear()
        for (l in lights) {
            for (id in l.objects) lightsByObject[id] = l
            for (id in l.npcs) lightsByNpc[id] = l
            for (id in l.spotAnimations) lightsBySpotAnimation[id] = l
            for (id in l.projectiles) lightsByProjectile[id] = l
        }
        overridesByObject.clear()
        for (o in overrides) for (id in o.objects ?: IntArray(0)) overridesByObject[id] = o
        for (name in FILES) {
            val file = folder?.let { File(it, "$name.json") }
            modified[name] = if (file != null && file.isFile) file.lastModified() else 0L
        }
        version++
        for (w in warnings) System.err.println("634HD: " + w)
    }

    /**
     * The entries of [file].json's array [field], each checked against the matching part of its
     * schema and converted; a bad entry is skipped with a warning.
     */
    private fun <T> parseList(file: String, field: String, convert: (Json.Obj) -> T?): List<T> {
        val text = read("$file.json") ?: return emptyList()
        val json: Json
        try {
            json = Json.parse(text)
        } catch (ex: Json.ParseException) {
            warnings.add("$file.json line ${ex.line}: ${ex.message}; the file is ignored")
            return emptyList()
        }
        val schemaText = read("$file.schema.json")
        val schema = if (schemaText == null) null else JsonSchema(Json.parse(schemaText) as Json.Obj)
        val entries = ((json as? Json.Obj)?.get(field) as? Json.Arr)?.items
        if (entries == null) {
            warnings.add("$file.json line ${json.line}: no \"$field\" array; the file is ignored")
            return emptyList()
        }
        val out = ArrayList<T>()
        for (entry in entries) {
            if (schema != null) {
                // the entry alone against the item schema: one bad entry does not cost the others
                val wrapped = Json.Obj(entry.line, linkedMapOf(field to Json.Arr(entry.line, arrayListOf(entry))))
                val problems = schema.check(wrapped)
                if (problems.isNotEmpty()) {
                    for (p in problems) warnings.add("$file.json line ${p.line}: ${p.message.removePrefix("$field[0]: ").removePrefix("$field[0].")}; entry skipped")
                    continue
                }
            }
            val value = try {
                convert(entry as Json.Obj)
            } catch (ex: RuntimeException) {
                warnings.add("$file.json line ${entry.line}: $ex; entry skipped")
                null
            }
            if (value != null) out.add(value)
        }
        return out
    }

    private fun ids(json: Json?): IntArray = (json as? Json.Arr)?.items?.map { (it as Json.Num).value.toInt() }?.toIntArray() ?: IntArray(0)
    private fun num(json: Json?, default: Float): Float = (json as? Json.Num)?.value?.toFloat() ?: default
    private fun str(json: Json?): String? = (json as? Json.Str)?.value
    private fun col(json: Json?): FloatArray? = str(json)?.let { colour(it) }

    private fun parseLight(o: Json.Obj): LightDef {
        val c = colour(str(o["colour"])!!)
        val offset = (o["offset"] as? Json.Arr)?.items?.map { (it as Json.Num).value.toFloat() }
        val flicker = when (str(o["flicker"])) { "flicker" -> 1; "pulse" -> 2; else -> 0 }
        return LightDef(
            str(o["name"]) ?: "", ids(o["objects"]), ids(o["npcs"]), ids(o["spotAnimations"]), ids(o["projectiles"]),
            c[0], c[1], c[2], num(o["radius"], 4.0f), num(o["intensity"], 1.0f),
            offset?.get(0) ?: 0.0f, offset?.get(1) ?: 0.0f, offset?.get(2) ?: 0.0f, flicker,
        )
    }

    private fun parseEnvironment(o: Json.Obj): EnvironmentDef {
        val e = EnvironmentDef(str(o["name"])!!, o.line)
        (o["area"] as? Json.Obj)?.let { area ->
            area["regions"]?.let { e.regions = ids(it) }
            (area["bounds"] as? Json.Arr)?.let { b -> e.bounds = b.items.map { ids(it) }.toTypedArray() }
            area["levels"]?.let { e.levels = ids(it) }
        }
        (o["sun"] as? Json.Obj)?.let { sun ->
            e.sunPitch = num(sun["pitch"], Float.NaN)
            e.sunYaw = num(sun["yaw"], Float.NaN)
            e.sunColour = col(sun["colour"])
            e.sunIntensity = num(sun["intensity"], 1.0f)
        }
        (o["ambient"] as? Json.Obj)?.let { a ->
            e.ambientColour = col(a["colour"])
            e.ambientIntensity = num(a["intensity"], 1.0f)
        }
        (o["fog"] as? Json.Obj)?.let { f ->
            e.fogColour = col(f["colour"])
            e.fogStart = num(f["start"], Float.NaN)
            e.fogEnd = num(f["end"], Float.NaN)
        }
        (o["sky"] as? Json.Obj)?.let { s ->
            e.skyTop = col(s["top"])
            e.skyHorizon = col(s["horizon"])
        }
        (o["heightFog"] as? Json.Obj)?.let { h ->
            e.heightFogDensity = num(h["density"], 0.0f)
            e.heightFogTop = num(h["top"], 0.0f)
        }
        e.lut = str(o["lut"])
        e.waterTint = col(o["waterTint"])
        (o["water"] as? Json.Obj)?.let { w ->
            col(w["tint"])?.let { e.waterTint = it }
            e.waterStrength = num(w["strength"], Float.NaN)
            e.waterWaves = num(w["waves"], 1.0f)
            e.waterSpeed = num(w["speed"], 1.0f)
            e.waterSize = num(w["size"], 1.0f)
            e.waterGlint = num(w["glint"], 1.0f)
        }
        return e
    }

    private fun parseMaterial(o: Json.Obj): MaterialDef {
        val m = MaterialDef((o["texture"] as Json.Num).value.toInt(), o.line)
        m.albedo = str(o["albedo"])
        m.normal = str(o["normal"])
        m.orm = str(o["orm"])
        m.height = str(o["height"])
        m.emissive = str(o["emissive"])
        (o["uvScroll"] as? Json.Arr)?.let {
            m.scrollU = (it.items[0] as Json.Num).value.toFloat()
            m.scrollV = (it.items[1] as Json.Num).value.toFloat()
        }
        m.specular = num(o["specular"], 0.5f)
        m.emissiveStrength = num(o["emissiveStrength"], 1.0f)
        m.parallax = num(o["parallax"], 0.04f)
        return m
    }

    private fun parseOverride(o: Json.Obj): OverrideDef {
        val d = OverrideDef(str(o["name"]) ?: "")
        o["objects"]?.let { d.objects = ids(it) }
        o["models"]?.let { d.models = ids(it) }
        o["faceColours"]?.let { d.faceColours = ids(it) }
        o["area"]?.let { d.area = ids(it) }
        d.material = (o["material"] as? Json.Num)?.value?.toInt() ?: -1
        d.wind = (o["wind"] as? Json.Bool)?.value ?: false
        d.castShadows = (o["castShadows"] as? Json.Bool)?.value ?: true
        d.uv = str(o["uv"]) ?: "world"
        d.uvScale = num(o["uvScale"], 1.0f)
        d.replace = (o["replace"] as? Json.Bool)?.value ?: false
        return d
    }

    private fun parseFoliage(o: Json.Obj): FoliageDef = FoliageDef(ids(o["objects"]), num(o["strength"], 1.0f), num(o["frequency"], 1.0f))

    // ---- writing the files back (the debug panel's Save buttons) ----

    private fun hex(c: FloatArray): String = String.format("#%02x%02x%02x", (c[0] * 255 + 0.5f).toInt().coerceIn(0, 255), (c[1] * 255 + 0.5f).toInt().coerceIn(0, 255), (c[2] * 255 + 0.5f).toInt().coerceIn(0, 255))

    private fun num(v: Float): String = if (v == Math.round(v).toFloat()) Math.round(v).toString() else String.format(java.util.Locale.ROOT, "%.3f", v).trimEnd('0')

    private fun ids(a: IntArray): String = a.joinToString(", ", "[", "]")

    /** Writes lights.json from [lights]; returns where, or why it could not. */
    fun saveLights(): String {
        val file = writableFile("lights.json") ?: return "no data folder: run with client.debug from the repository, or set client.hd.dataDir"
        val out = StringBuilder("{\n  \"lights\": [\n")
        for ((i, l) in lights.withIndex()) {
            out.append("    {\n      \"name\": \"").append(l.name).append("\",\n")
            if (l.objects.isNotEmpty()) out.append("      \"objects\": ").append(ids(l.objects)).append(",\n")
            if (l.npcs.isNotEmpty()) out.append("      \"npcs\": ").append(ids(l.npcs)).append(",\n")
            if (l.spotAnimations.isNotEmpty()) out.append("      \"spotAnimations\": ").append(ids(l.spotAnimations)).append(",\n")
            if (l.projectiles.isNotEmpty()) out.append("      \"projectiles\": ").append(ids(l.projectiles)).append(",\n")
            out.append("      \"colour\": \"").append(hex(floatArrayOf(l.red, l.green, l.blue))).append("\",\n")
            out.append("      \"radius\": ").append(num(l.radius)).append(",\n")
            out.append("      \"intensity\": ").append(num(l.intensity)).append(",\n")
            out.append("      \"offset\": [").append(num(l.offsetX)).append(", ").append(num(l.offsetY)).append(", ").append(num(l.offsetZ)).append("],\n")
            out.append("      \"flicker\": \"").append(when (l.flicker) { 1 -> "flicker"; 2 -> "pulse"; else -> "none" }).append("\"\n")
            out.append(if (i == lights.size - 1) "    }\n" else "    },\n")
        }
        out.append("  ]\n}\n")
        file.writeText(out.toString())
        return "wrote " + file
    }

    /** Writes environments.json from [environments]; returns where, or why it could not. */
    fun saveEnvironments(): String {
        val file = writableFile("environments.json") ?: return "no data folder: run with client.debug from the repository, or set client.hd.dataDir"
        val out = StringBuilder("{\n  \"environments\": [\n")
        for ((i, e) in environments.withIndex()) {
            val fields = ArrayList<String>()
            fields.add("\"name\": \"" + e.name + "\"")
            val area = ArrayList<String>()
            e.regions?.let { area.add("\"regions\": " + ids(it)) }
            e.bounds?.let { b -> area.add("\"bounds\": " + b.joinToString(", ", "[", "]") { ids(it) }) }
            e.levels?.let { area.add("\"levels\": " + ids(it)) }
            if (area.isNotEmpty()) fields.add("\"area\": { " + area.joinToString(", ") + " }")
            val sun = ArrayList<String>()
            if (!e.sunPitch.isNaN()) sun.add("\"pitch\": " + num(e.sunPitch))
            if (!e.sunYaw.isNaN()) sun.add("\"yaw\": " + num(e.sunYaw))
            e.sunColour?.let { sun.add("\"colour\": \"" + hex(it) + "\"") }
            if (e.sunIntensity != 1.0f) sun.add("\"intensity\": " + num(e.sunIntensity))
            if (sun.isNotEmpty()) fields.add("\"sun\": { " + sun.joinToString(", ") + " }")
            val ambient = ArrayList<String>()
            e.ambientColour?.let { ambient.add("\"colour\": \"" + hex(it) + "\"") }
            if (e.ambientIntensity != 1.0f) ambient.add("\"intensity\": " + num(e.ambientIntensity))
            if (ambient.isNotEmpty()) fields.add("\"ambient\": { " + ambient.joinToString(", ") + " }")
            val fog = ArrayList<String>()
            e.fogColour?.let { fog.add("\"colour\": \"" + hex(it) + "\"") }
            if (!e.fogStart.isNaN()) fog.add("\"start\": " + num(e.fogStart))
            if (!e.fogEnd.isNaN()) fog.add("\"end\": " + num(e.fogEnd))
            if (fog.isNotEmpty()) fields.add("\"fog\": { " + fog.joinToString(", ") + " }")
            val sky = ArrayList<String>()
            e.skyTop?.let { sky.add("\"top\": \"" + hex(it) + "\"") }
            e.skyHorizon?.let { sky.add("\"horizon\": \"" + hex(it) + "\"") }
            if (sky.isNotEmpty()) fields.add("\"sky\": { " + sky.joinToString(", ") + " }")
            if (e.heightFogDensity != 0.0f) fields.add("\"heightFog\": { \"density\": " + num(e.heightFogDensity) + ", \"top\": " + num(e.heightFogTop) + " }")
            e.lut?.let { fields.add("\"lut\": \"" + it + "\"") }
            e.waterTint?.let { fields.add("\"waterTint\": \"" + hex(it) + "\"") }
            out.append("    {\n      ").append(fields.joinToString(",\n      ")).append(if (i == environments.size - 1) "\n    }\n" else "\n    },\n")
        }
        out.append("  ]\n}\n")
        file.writeText(out.toString())
        return "wrote " + file
    }

    /** The environment for world tile ([x], [y]) on [level]: the first area holding it, else the default. */
    fun environmentAt(x: Int, y: Int, level: Int): EnvironmentDef? {
        var fallback: EnvironmentDef? = null
        for (e in environments) {
            if (e.isDefault) {
                if (fallback == null) fallback = e
            } else if (e.contains(x, y, level)) {
                return e
            }
        }
        return fallback
    }
}
