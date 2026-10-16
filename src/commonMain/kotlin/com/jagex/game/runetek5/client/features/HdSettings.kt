package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the switches of 634HD's rendering features (toolkit 6, Phase 4 of
 * docs/634hd-plan.md). Every one defaults to the game as OpenGL draws it; turning one on changes only
 * what it names. They are read from client.hd.<name> at start (--hd name=value in the launcher), and
 * changed while playing with ::hd <name> <value> (::hd alone lists them) or the debug panel.
 *
 * Other toolkits ignore them. [version] counts changes, so the renderer can rebuild what depends on
 * them (shaders, targets, the retained scene) on its next frame.
 */
object HdSettings {

	/** Bumped by every change. */
	@JvmField
	var version: Int = 0

	/** Linear colour, a half-float scene target and tonemapping (0 off, 1 ACES, 2 AgX). */
	@JvmField
	var tonemap: Int = 0

	/** Ordered dithering of the tonemapped output (with tonemap), against banding. */
	@JvmField
	var dither: Boolean = true

	/** Anisotropic filtering of world textures: 0 off, or 2, 4, 8, 16. */
	@JvmField
	var anisotropy: Int = 0

	/** Mipmaps for every world texture, also those the cache draws without. */
	@JvmField
	var mipmaps: Boolean = false

	/** Fog over the far part of the view, from the environment's fog colour and distance. */
	@JvmField
	var fog: Boolean = false

	/** The last stretch before the draw distance fades into the fog or sky colour, hiding things popping in. */
	@JvmField
	var fade: Boolean = false

	/** Ambient occlusion baked into the static scene's vertices when a region is built. */
	@JvmField
	var ambientOcclusion: Boolean = false

	/** Sun and ambient lighting per pixel instead of per vertex. */
	@JvmField
	var pixelLighting: Boolean = false

	/** Sun shadows: 0 off, or the shadow map size (1024, 2048, 4096). */
	@JvmField
	var shadows: Int = 0

	/** Point lights from lights.json (objects, NPCs, spot animations, projectiles). */
	@JvmField
	var lights: Boolean = false

	/** Tile materials blended through a noise mask, and triplanar mapping on steep ground. */
	@JvmField
	var terrainBlend: Boolean = false

	/** Water: 0 as the game draws it, 1 animated normals, fresnel, depth colour and foam. */
	@JvmField
	var water: Int = 0

	/** Cut-out foliage with alpha-to-coverage (when multisampled) and wind sway from foliage.json. */
	@JvmField
	var foliage: Boolean = false

	/** A gradient sky and height fog instead of the flat fog colour. */
	@JvmField
	var sky: Boolean = false

	/** Particles fade where they meet geometry (soft particles) and may light the scene. */
	@JvmField
	var softParticles: Boolean = false

	/** FXAA over the 3D view. */
	@JvmField
	var fxaa: Boolean = false

	/** Bloom from the bright parts of the (tonemapped) view. */
	@JvmField
	var bloom: Boolean = false

	/** A colour grading look-up table per environment (environments.json). */
	@JvmField
	var colourGrading: Boolean = false

	/** The interface drawn at a whole-number scale with nearest filtering: 0 off, or 2, 3, 4. */
	@JvmField
	var interfaceScale: Int = 0

	/** Sprites and text upscaled with an edge-directed filter (xBR-like) when the interface is scaled. */
	@JvmField
	var smoothInterface: Boolean = false

	/** GPU particles: embers over fires and torches, sparks over lava (at the lights of lights.json). */
	@JvmField
	var particles: Boolean = false

	/** Phase 5 material maps (materials.json): normal, occlusion/roughness, emissive and height maps. */
	@JvmField
	var materials: Boolean = false

	/**
	 * The draw distance chosen in the settings panel (ClientFeatures.RENDER_DISTANCE: 1, 1.5 or 2), 0
	 * when none was chosen. Saved; used at start when client.renderDistance was not given, or only
	 * by the launcher's defaults.
	 */
	@JvmField
	var renderDistance: Double = 0.0

	/**
	 * The interface scale chosen in the settings panel for the next start (ClientFeatures.SCALE: 1
	 * to 3), 0 for the launcher's own (by the screen). Saved; used at start when client.scale was not
	 * given, or only by the launcher's defaults.
	 */
	@JvmField
	var scale: Double = 0.0

	/** Cut-out shadows: leaves, fences and grilles cast the shape of their texture, not a solid block. */
	@JvmField
	var cutoutShadows: Boolean = false

	/** World textures: 634HD's own materials on untextured models and tiles (model_overrides, tile_overrides). */
	@JvmField
	var worldTextures: Boolean = false

	/** The environment's sun and ambient colours tint the game's lighting (environments.json sun, ambient). */
	@JvmField
	var environmentLighting: Boolean = false

	/** Carried light: the light of the lit torches, candles, lanterns the player carries or wears (stronger at night). */
	@JvmField
	var playerLight: Boolean = false

	/** Reflections: the scene mirrored in the water (a second draw of the static scene and the sky). */
	@JvmField
	var reflections: Boolean = false

	/** Caustics: rippling light on the ground under water (the game's underwater pass). */
	@JvmField
	var caustics: Boolean = false

	/**
	 * Lighting model: surfaces lit per pixel as a sum of terms (sun on the mapped normal with shadows, a
	 * hemisphere of sky and ground light, the occlusion map, highlights from the roughness map) instead
	 * of the game's light scaled by a clamped ratio.
	 */
	@JvmField
	var lightingModel: Boolean = false

	/** Fresh texture art: a game texture with a replacement albedo in materials.json draws with it. */
	@JvmField
	var textureReplacements: Boolean = false

	/** Grass and plants bend away from the player and NPCs walking through them. */
	@JvmField
	var grassDisplacement: Boolean = false

	/**
	 * Day and night: 0 off (the game's light), -1 following the computer's clock, else a whole day
	 * every that many minutes. Not part of the presets.
	 */
	@JvmField
	var dayNight: Int = 0

	/** Debug panel (debug builds: client.debug), toggled with F10 or ::hdpanel. */
	@JvmField
	var panel: Boolean = false

	private val NAMES: Array<String> = arrayOf(
		"tonemap", "dither", "anisotropy", "mipmaps", "fog", "fade", "ambientOcclusion", "pixelLighting",
		"shadows", "lights", "terrainBlend", "water", "foliage", "sky", "softParticles", "fxaa", "bloom",
		"colourGrading", "interfaceScale", "smoothInterface", "materials", "particles", "panel",
		"cutoutShadows", "worldTextures", "environmentLighting", "caustics", "grassDisplacement", "dayNight", "textureReplacements", "lightingModel", "playerLight", "reflections",
	)

	/** The switches a player sets in the 634HD settings panel (HdSettingsPanel) and that are saved. */
	@JvmField
	val SAVED: Array<String> = arrayOf(
		"tonemap", "dither", "anisotropy", "mipmaps", "fog", "fade", "ambientOcclusion", "pixelLighting",
		"shadows", "lights", "terrainBlend", "water", "foliage", "sky", "softParticles", "fxaa", "bloom",
		"colourGrading", "materials", "particles", "cutoutShadows", "worldTextures", "environmentLighting", "caustics", "grassDisplacement", "dayNight", "textureReplacements", "lightingModel", "playerLight", "reflections",
	)

	/** Saved too, but not part of the presets: the whole client's draw distance and interface scale. */
	@JvmField
	val CLIENT_SAVED: Array<String> = arrayOf("renderDistance", "scale")

	/** The presets, lowest first; "off" is the game as OpenGL draws it. */
	@JvmField
	val PRESETS: Array<String> = arrayOf("off", "low", "medium", "high", "ultra")

	/** Whether the settings came from the saved file or the command line (else the defaults, all off). */
	@JvmField
	var configured: Boolean = false

	init {
		var given: Boolean = false
		for (name in NAMES) {
			val value: String? = lang.getProperty("client.hd." + name)
			if (value != null) {
				given = true
				val error: String? = set(name, value)
				if (error != null) {
					lang.printlnErr("client.hd." + name + ": " + error)
				}
			}
		}
		// the command line wins; without it, what the player chose last time
		if (given) {
			configured = true
		} else if (load()) {
			configured = true
		}
		version = 0
	}

	/** Sets every saved switch to the preset [name] (one of PRESETS); null when it worked. */
	@JvmStatic
	fun applyPreset(name: String): String? {
		val level: Int = indexOf(PRESETS, name.lowercase())
		if (level < 0) {
			return "presets are off, low, medium, high and ultra"
		}
		tonemap = if (level >= 2) 1 else 0
		dither = true
		mipmaps = level >= 1
		anisotropy = if (level >= 3) 16 else if (level >= 1) 4 else 0
		fxaa = level >= 1
		fog = level >= 1
		sky = level >= 2
		pixelLighting = level >= 2
		lights = level >= 2
		water = if (level >= 2) 1 else 0
		foliage = level >= 2
		terrainBlend = level >= 2
		colourGrading = level >= 2
		shadows = if (level >= 4) 4096 else if (level >= 3) 2048 else if (level >= 2) 1024 else 0
		ambientOcclusion = level >= 3
		bloom = level >= 3
		materials = level >= 3
		softParticles = level >= 3
		particles = level >= 3
		fade = level >= 3
		cutoutShadows = level >= 3
		worldTextures = level >= 3
		environmentLighting = level >= 2
		caustics = level >= 3
		reflections = level >= 3
		playerLight = level >= 2
		grassDisplacement = level >= 3
		textureReplacements = level >= 3
		lightingModel = level >= 3
		version++
		return null
	}

	/** The preset the switches match now, or null when they have been changed one by one. */
	@JvmStatic
	fun currentPreset(): String? {
		val saved: Array<String> = values()
		for (preset in PRESETS) {
			applyPreset(preset)
			val same: Boolean = sameValues(saved, values())
			restore(saved)
			if (same) {
				return preset
			}
		}
		return null
	}

	private fun indexOf(array: Array<String>, value: String): Int {
		for (i in array.indices) {
			if (array[i] == value) {
				return i
			}
		}
		return -1
	}

	/** The saved switches' values, as set() takes them. */
	@JvmStatic
	fun values(): Array<String> {
		val out: Array<String> = arrayOfNulls<String>(SAVED.size) as Array<String>
		for (i in SAVED.indices) {
			out[i] = valueOf(SAVED[i])
		}
		return out
	}

	private fun sameValues(a: Array<String>, b: Array<String>): Boolean {
		for (i in a.indices) {
			if (a[i] != b[i]) {
				return false
			}
		}
		return true
	}

	private fun restore(values: Array<String>) {
		for (i in SAVED.indices) {
			set(SAVED[i], values[i])
		}
	}

	/** One saved switch's value, as set() takes it. */
	@JvmStatic
	fun valueOf(name: String): String = when (name) {
		"tonemap" -> if (tonemap == 1) "aces" else if (tonemap == 2) "agx" else "off"
		"anisotropy" -> anisotropy.toString()
		"shadows" -> shadows.toString()
		"water" -> water.toString()
		"dither" -> onOff(dither)
		"mipmaps" -> onOff(mipmaps)
		"fog" -> onOff(fog)
		"fade" -> onOff(fade)
		"ambientOcclusion" -> onOff(ambientOcclusion)
		"pixelLighting" -> onOff(pixelLighting)
		"lights" -> onOff(lights)
		"terrainBlend" -> onOff(terrainBlend)
		"foliage" -> onOff(foliage)
		"sky" -> onOff(sky)
		"softParticles" -> onOff(softParticles)
		"fxaa" -> onOff(fxaa)
		"bloom" -> onOff(bloom)
		"colourGrading" -> onOff(colourGrading)
		"materials" -> onOff(materials)
		"particles" -> onOff(particles)
		"cutoutShadows" -> onOff(cutoutShadows)
		"worldTextures" -> onOff(worldTextures)
		"environmentLighting" -> onOff(environmentLighting)
		"caustics" -> onOff(caustics)
		"playerLight" -> onOff(playerLight)
		"reflections" -> onOff(reflections)
		"grassDisplacement" -> onOff(grassDisplacement)
		"textureReplacements" -> onOff(textureReplacements)
		"lightingModel" -> onOff(lightingModel)
		"dayNight" -> if (dayNight == 0) "off" else if (dayNight < 0) "realtime" else dayNight.toString()
		"renderDistance" -> if (renderDistance > 0.0) number(renderDistance) else "auto"
		"scale" -> if (scale > 0.0) number(scale) else "auto"
		else -> ""
	}

	/** [value] as the settings write it: 2 for 2.0, 1.5 for 1.5. */
	@JvmStatic
	fun number(value: Double): String = if (value == kotlin.math.floor(value)) value.toInt().toString() else value.toString()

	/**
	 * Where the player's choices are kept: one name=value a line, in the home folder. Not under the
	 * smoke test (client.test), whose runs must neither read nor change it.
	 */
	private fun file(): io.File? {
		if (lang.getProperty("client.test") != null) {
			return null
		}
		val home: String = lang.getProperty("user.home") ?: return null
		return io.File(home + "/.634hd-settings.txt")
	}

	/** Saves the switches the panel sets; false if the file cannot be written. */
	@JvmStatic
	fun save(): Boolean {
		val file: io.File = file() ?: return false
		try {
			val out: StringBuilder = StringBuilder("# 634HD settings, written by the client (HdSettingsPanel)\n")
			for (name in SAVED) {
				out.append(name).append('=').append(valueOf(name)).append('\n')
			}
			for (name in CLIENT_SAVED) {
				out.append(name).append('=').append(valueOf(name)).append('\n')
			}
			val stream: io.FileOutputStream = io.FileOutputStream(file)
			try {
				stream.write(out.toString().encodeToByteArray())
			} finally {
				stream.close()
			}
			configured = true
			return true
		} catch (ex: Exception) {
			lang.printlnErr("634HD: cannot save the settings: " + ex)
			return false
		}
	}

	/** Loads the saved switches; false when there is no file. */
	@JvmStatic
	fun load(): Boolean {
		val file: io.File = file() ?: return false
		try {
			if (!file.exists()) {
				return false
			}
			val reader: io.BufferedReader = io.BufferedReader(io.InputStreamReader(io.FileInputStream(file)))
			try {
				while (true) {
					val line: String = reader.readLine() ?: break
					val at: Int = line.indexOf('=')
					if (at <= 0 || line.startsWith("#")) {
						continue
					}
					set(line.substring(0, at).trim(), line.substring(at + 1).trim())
				}
			} finally {
				reader.close()
			}
			return true
		} catch (ex: Exception) {
			lang.printlnErr("634HD: cannot read the settings: " + ex)
			return false
		}
	}

	/** Whether the 3D view needs its own framebuffer for these settings (post-processing). */
	@JvmStatic
	fun postProcessing(): Boolean = tonemap != 0 || fxaa || bloom || colourGrading || softParticles || water != 0

	/** Sets [name] to [value]; null when it worked, else what was wrong. */
	@JvmStatic
	fun set(name: String, value: String): String? {
		val text: String = value.lowercase()
		val on: Boolean = text == "on" || text == "true" || text == "1" || text == "yes"
		val off: Boolean = text == "off" || text == "false" || text == "0" || text == "no"
		val number: Int = text.toIntOrNull() ?: -1
		when (name.lowercase()) {
			"tonemap" -> tonemap = if (text == "aces") 1 else if (text == "agx") 2 else if (off) 0 else if (number in 0..2) number else return "takes off, aces or agx"
			"dither" -> dither = flag(on, off) ?: return "takes on or off"
			"anisotropy" -> {
				if (number != 0 && number != 2 && number != 4 && number != 8 && number != 16 && !off) return "takes 0, 2, 4, 8 or 16"
				anisotropy = if (off) 0 else number
			}
			"mipmaps" -> mipmaps = flag(on, off) ?: return "takes on or off"
			"fog" -> fog = flag(on, off) ?: return "takes on or off"
			"fade" -> fade = flag(on, off) ?: return "takes on or off"
			"ambientocclusion", "ao" -> ambientOcclusion = flag(on, off) ?: return "takes on or off"
			"pixellighting" -> pixelLighting = flag(on, off) ?: return "takes on or off"
			"shadows" -> {
				if (number != 0 && number != 1024 && number != 2048 && number != 4096 && !off && !on) return "takes off, on, 1024, 2048 or 4096"
				shadows = if (off) 0 else if (on) 2048 else number
			}
			"lights" -> lights = flag(on, off) ?: return "takes on or off"
			"terrainblend" -> terrainBlend = flag(on, off) ?: return "takes on or off"
			"water" -> water = if (off) 0 else if (on) 1 else if (number in 0..1) number else return "takes 0 or 1"
			"foliage" -> foliage = flag(on, off) ?: return "takes on or off"
			"sky" -> sky = flag(on, off) ?: return "takes on or off"
			"softparticles" -> softParticles = flag(on, off) ?: return "takes on or off"
			"fxaa" -> fxaa = flag(on, off) ?: return "takes on or off"
			"bloom" -> bloom = flag(on, off) ?: return "takes on or off"
			"colourgrading", "colorgrading" -> colourGrading = flag(on, off) ?: return "takes on or off"
			"interfacescale" -> interfaceScale = if (off) 0 else if (number in 2..4) number else return "takes off, 2, 3 or 4"
			"smoothinterface" -> smoothInterface = flag(on, off) ?: return "takes on or off"
			"materials" -> materials = flag(on, off) ?: return "takes on or off"
			"particles" -> particles = flag(on, off) ?: return "takes on or off"
			"panel" -> panel = flag(on, off) ?: return "takes on or off"
			"cutoutshadows" -> cutoutShadows = flag(on, off) ?: return "takes on or off"
			"worldtextures" -> worldTextures = flag(on, off) ?: return "takes on or off"
			"environmentlighting" -> environmentLighting = flag(on, off) ?: return "takes on or off"
			"caustics" -> caustics = flag(on, off) ?: return "takes on or off"
			"playerlight" -> playerLight = flag(on, off) ?: return "takes on or off"
			"reflections" -> reflections = flag(on, off) ?: return "takes on or off"
			"grassdisplacement" -> grassDisplacement = flag(on, off) ?: return "takes on or off"
			"texturereplacements" -> textureReplacements = flag(on, off) ?: return "takes on or off"
			"lightingmodel" -> lightingModel = flag(on, off) ?: return "takes on or off"
			"daynight" -> dayNight = if (off) 0 else if (text == "realtime") -1 else if (number in 1..1440) number else return "takes off, realtime, or the minutes a day lasts (1 to 1440)"
			"renderdistance" -> {
				val distance: Double = if (text == "auto") 0.0 else text.toDoubleOrNull() ?: -1.0
				if (distance != 0.0 && distance != 1.0 && distance != 1.5 && distance != 2.0) return "takes 1, 1.5 or 2"
				renderDistance = distance
			}
			"scale" -> {
				val wanted: Double = if (text == "auto") 0.0 else text.toDoubleOrNull() ?: -1.0
				if (wanted != 0.0 && (wanted < 1.0 || wanted > 3.0 || wanted * 2.0 != kotlin.math.floor(wanted * 2.0))) return "takes auto, 1, 1.5, 2, 2.5 or 3"
				scale = wanted
			}
			"all" -> {
				// every feature at its usual setting, for a quick look (::hd all on|off)
				val enable: Boolean = flag(on, off) ?: return "takes on or off"
				tonemap = if (enable) 1 else 0
				anisotropy = if (enable) 16 else 0
				mipmaps = enable
				fog = enable
				fade = enable
				ambientOcclusion = enable
				pixelLighting = enable
				shadows = if (enable) 2048 else 0
				lights = enable
				terrainBlend = enable
				water = if (enable) 1 else 0
				foliage = enable
				sky = enable
				softParticles = enable
				materials = enable
				particles = enable
				fxaa = enable
				bloom = enable
				colourGrading = enable
				cutoutShadows = enable
				worldTextures = enable
				environmentLighting = enable
				caustics = enable
				playerLight = enable
				reflections = enable
				grassDisplacement = enable
				textureReplacements = enable
				lightingModel = enable
			}
			else -> return "unknown setting " + name
		}
		version++
		return null
	}

	private fun flag(on: Boolean, off: Boolean): Boolean? = if (on) true else if (off) false else null

	/** One line per setting, for ::hd. */
	@JvmStatic
	fun describe(): Array<String> = arrayOf(
		"tonemap " + (if (tonemap == 1) "aces" else if (tonemap == 2) "agx" else "off") + ", dither " + onOff(dither),
		"anisotropy " + anisotropy + ", mipmaps " + onOff(mipmaps),
		"fog " + onOff(fog) + ", fade " + onOff(fade) + ", sky " + onOff(sky),
		"ambientOcclusion " + onOff(ambientOcclusion) + ", pixelLighting " + onOff(pixelLighting) + ", shadows " + shadows + ", lights " + onOff(lights),
		"terrainBlend " + onOff(terrainBlend) + ", water " + water + ", foliage " + onOff(foliage) + ", softParticles " + onOff(softParticles),
		"fxaa " + onOff(fxaa) + ", bloom " + onOff(bloom) + ", colourGrading " + onOff(colourGrading),
		"interfaceScale " + interfaceScale + ", smoothInterface " + onOff(smoothInterface) + ", materials " + onOff(materials) + ", particles " + onOff(particles),
		"cutoutShadows " + onOff(cutoutShadows) + ", worldTextures " + onOff(worldTextures) + ", environmentLighting " + onOff(environmentLighting) + ", caustics " + onOff(caustics) + ", playerLight " + onOff(playerLight) + ", reflections " + onOff(reflections) + ", grassDisplacement " + onOff(grassDisplacement) + ", textureReplacements " + onOff(textureReplacements) + ", lightingModel " + onOff(lightingModel) + ", dayNight " + valueOf("dayNight"),
		"renderDistance " + valueOf("renderDistance") + ", scale " + valueOf("scale") + " (from the next start)",
	)

	private fun onOff(value: Boolean): String = if (value) "on" else "off"
}
