package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.StatChanged
import com.jagex.game.runetek5.rendering.Renderers
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: when a skill's experience goes up, a "+N Skill" drop rises
 * near the top of the game view, left of the minimap, and fades. The first update of each skill after login only
 * records its experience, so logging in shows no drops. On only with `client.xpDrops=true`;
 * the console command `xpdrops` turns it on and off while playing.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md): it hears of each stat update, the logout
 * and the frame through the plugin API, as a plugin from a jar would, and shows what the API is for.
 */
object XpDrops : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("xpdrops", "Experience drops", "1", PluginApi.VERSION, "A \"+N Skill\" drop near the minimap when experience goes up (::xpdrops turns it on and off).", "634-client")

	@JvmField
	var enabled: Boolean = lang.getBoolean("client.xpDrops")

	/** The skills, in the order the server numbers them. */
	private val SKILL_NAMES: Array<String?> = arrayOf("Attack", "Defence", "Strength", "Constitution", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction", "Summoning", "Dungeoneering")

	/** How many client cycles a drop stays up (50 a second). */
	const val LIFETIME: Int = 100

	/** How far a drop rises over its life, in pixels. */
	private const val RISE: Int = 40

	private const val MAX_DROPS: Int = 8

	private const val COLOUR: Int = 0xFFFFFFFF.toInt()

	private const val SHADOW: Int = 0xFF000000.toInt()

	/** Whether each skill's first update since login has been seen. */
	private val primed: BooleanArray = BooleanArray(SKILL_NAMES.size)

	private val dropSkill: IntArray = IntArray(MAX_DROPS)

	private val dropAmount: IntArray = IntArray(MAX_DROPS)

	private val dropCycle: IntArray = IntArray(MAX_DROPS)

	private var dropCount: Int = 0

	/** The drops the last frame drew, for the smoke test. */
	@JvmField
	var drawnLastFrame: Int = 0

	/** The text of the newest drop, for the smoke test. */
	@JvmField
	var lastDropText: String? = null

	private var context: PluginContext? = null

	/* The plugin's listeners, made once. */

	private val statListener: EventListener<StatChanged> = EventListener { event -> statUpdated(event.skill, event.oldXp, event.newXp) }

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.previous === GameState.LOGGED_IN) {
			reset()
		}
	}

	private val overlay: Overlay = Overlay { _, view -> draw(view.x, view.y, view.width) }

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.client()?.consolePrint(if (enabled) "Experience drops enabled." else "Experience drops disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		context.events().subscribe(Events.STAT_CHANGED, statListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(overlay)
		context.registerCommand("xpdrops", toggle)
	}

	override fun stop() {
		context = null
	}

	/**
	 * Called with each stat update the server sends, before the client stores the new experience.
	 */
	@JvmStatic
	fun statUpdated(skillId: Int, oldXp: Int, newXp: Int) {
		if (skillId < 0 || skillId >= SKILL_NAMES.size) {
			return
		}
		if (!primed[skillId]) {
			primed[skillId] = true
			return
		}
		if (!enabled || newXp <= oldXp) {
			return
		}
		if (dropCount == MAX_DROPS) {
			lang.arraycopy(dropSkill, 1, dropSkill, 0, MAX_DROPS - 1)
			lang.arraycopy(dropAmount, 1, dropAmount, 0, MAX_DROPS - 1)
			lang.arraycopy(dropCycle, 1, dropCycle, 0, MAX_DROPS - 1)
			dropCount--
		}
		dropSkill[dropCount] = skillId
		dropAmount[dropCount] = newXp - oldXp
		dropCycle[dropCount] = ClientState.clientCycle
		dropCount++
		lastDropText = text(dropCount - 1)
	}

	/** Forgets every skill's experience and the drops on screen, at logout. */
	@JvmStatic
	fun reset() {
		for (i in 0 until primed.size) {
			primed[i] = false
		}
		dropCount = 0
		lastDropText = null
	}

	@JvmStatic
	fun dropCount(): Int {
		return dropCount
	}

	private fun text(index: Int): String {
		return "+" + dropAmount[index] + " " + SKILL_NAMES[dropSkill[index]]
	}

	/** Draws the live drops in the game view and lets the expired ones go. */
	@JvmStatic
	fun draw(areaX: Int, areaY: Int, width: Int) {
		drawnLastFrame = 0
		var live: Int = 0
		for (i in 0 until dropCount) {
			if (ClientState.clientCycle - dropCycle[i] < LIFETIME) {
				dropSkill[live] = dropSkill[i]
				dropAmount[live] = dropAmount[i]
				dropCycle[live] = dropCycle[i]
				live++
			}
		}
		dropCount = live
		if (!enabled || dropCount == 0 || Renderers.FONT_RENDERER == null) {
			return
		}
		// left of the minimap and the orbs, which the interfaces draw over the game view
		val x: Int = areaX + width - 300
		for (i in 0 until dropCount) {
			val age: Int = ClientState.clientCycle - dropCycle[i]
			// below the debug overlay's lines; newer drops sit lower, so a burst of drops reads as a column
			val y: Int = areaY + 140 + (dropCount - 1 - i) * 14 - age * RISE / LIFETIME
			val alpha: Int = 255 - age * 255 / LIFETIME
			Renderers.FONT_RENDERER!!.renderCentered(x, (alpha shl 24) or (COLOUR and 0xFFFFFF), text(i), (alpha shl 24) or (SHADOW and 0xFFFFFF), y)
			drawnLastFrame++
		}
	}
}
