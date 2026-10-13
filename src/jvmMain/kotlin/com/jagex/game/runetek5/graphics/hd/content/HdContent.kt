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

    class LightDef(
        val name: String, val objects: IntArray, val npcs: IntArray, val spotAnimations: IntArray, val projectiles: IntArray,
        val red: Float, val green: Float, val blue: Float, val radius: Float, val intensity: Float,
        val offsetX: Float, val offsetY: Float, val offsetZ: Float, val flicker: Int,
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

    class OverrideDef(val name: String) {
        var objects: IntArray? = null
        var models: IntArray? = null
        var faceColours: IntArray? = null
        var area: IntArray? = null
        var material: Int = -1
        var wind: Boolean = false
        var castShadows: Boolean = true
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
        val FILES = arrayOf("lights", "environments", "materials", "model_overrides", "foliage")

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
        return e
    }

    private fun parseMaterial(o: Json.Obj): MaterialDef {
        val m = MaterialDef((o["texture"] as Json.Num).value.toInt(), o.line)
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
        return d
    }

    private fun parseFoliage(o: Json.Obj): FoliageDef = FoliageDef(ids(o["objects"]), num(o["strength"], 1.0f), num(o["frequency"], 1.0f))

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
