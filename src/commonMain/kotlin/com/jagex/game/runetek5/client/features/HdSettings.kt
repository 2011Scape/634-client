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

	/** Locs fade in over a short band at the edge of the view instead of appearing (dithered). */
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

	/** Debug panel (debug builds: client.debug), toggled with F10 or ::hdpanel. */
	@JvmField
	var panel: Boolean = false

	private val NAMES: Array<String> = arrayOf(
		"tonemap", "dither", "anisotropy", "mipmaps", "fog", "fade", "ambientOcclusion", "pixelLighting",
		"shadows", "lights", "terrainBlend", "water", "foliage", "sky", "softParticles", "fxaa", "bloom",
		"colourGrading", "interfaceScale", "smoothInterface", "materials", "particles", "panel",
	)

	init {
		for (name in NAMES) {
			val value: String? = lang.getProperty("client.hd." + name)
			if (value != null) {
				val error: String? = set(name, value)
				if (error != null) {
					lang.printlnErr("client.hd." + name + ": " + error)
				}
			}
		}
		version = 0
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
	)

	private fun onOff(value: Boolean): String = if (value) "on" else "off"
}
