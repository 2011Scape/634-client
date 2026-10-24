package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Hiscore plugin. `::hiscore <name>`, or Lookup on a
 * player's right-click menu, looks a player up on the game's website (HiscoreLookup, its
 * `index_lite.json`) and shows a panel with their levels laid out as the Stats tab has them, the total
 * level and overall rank; the mouse over a skill shows its rank and experience. The panel stays until
 * closed (`::hiscore` alone, or Close on shift-right-clicking it). A built-in plugin (BuiltinPlugins,
 * docs/PLUGINS.md).
 */
object Hiscore : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("hiscore", "Hiscore", "1", PluginApi.VERSION, "Look players up on the hiscores (::hiscore <name>, or Lookup on a player) and see their levels in a panel.", "Pixel")

	/** The Stats tab's layout: three skills a row, by skill number. */
	@JvmField
	val GRID: IntArray = intArrayOf(0, 3, 14, 2, 16, 13, 1, 15, 10, 4, 17, 7, 5, 12, 11, 6, 9, 8, 20, 18, 19, 22, 21, 23, 24)

	const val COLUMNS: Int = 3

	const val CELL_WIDTH: Int = 62

	const val CELL_HEIGHT: Int = 26

	private const val WIDTH: Int = 8 + COLUMNS * CELL_WIDTH

	private const val TITLE_COLOUR: Int = 0xFFFF981F.toInt()

	private const val TEXT_COLOUR: Int = 0xFFFFFF00.toInt()

	private const val WHITE: Int = 0xFFFFFFFF.toInt()

	private const val SHADOW: Int = 0xFF000000.toInt()

	@JvmField var enabled: Boolean = true
	@JvmField var menuOption: Boolean = true

	/** The player shown, or null for no panel. */
	@JvmField var shown: String? = null

	private var context: PluginContext? = null

	private var lookupAction: Int = -1

	private val size: Size = Size()

	private val menuListener: EventListener<MenuEntryAdded> = EventListener { event ->
		val client: ClientApi = context?.client() ?: return@EventListener
		if (!enabled || !menuOption || event.target !== MenuTarget.PLAYER) {
			return@EventListener
		}
		client.addMenuEntry("Lookup", lookupAction)
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val command: CommandHandler = CommandHandler { _, args ->
		val context: PluginContext = this.context ?: return@CommandHandler
		if (args.isEmpty()) {
			shown = null
			context.client().consolePrint("Hiscore panel closed.")
			return@CommandHandler
		}
		show(args.joinToString(" "))
		context.client().consolePrint("Looking up " + shown + ".")
	}

	/** The levels, at the top left of the game view unless the player moved it. */
	@JvmField
	val panel: PanelOverlay = object : PanelOverlay("HiscorePanel") {
		override fun render(g: Graphics, view: Viewport): Size? = draw(g, this)
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Hiscore", true)
		s.toggle("menuOption", "Lookup on players", true)
		readSettings(context.config())
		lookupAction = context.registerMenuAction(MenuAction { _, _, _, index ->
			val player: Player? = this.context?.client()?.playerByIndex(index)
			player?.name()?.let { show(it) }
		})
		panel.addMenuEntry("Close", "Hiscore") { shown = null }
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, menuListener)
		context.addOverlay(panel)
		context.registerCommand("hiscore", command)
	}

	override fun stop() {
		context = null
		shown = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		menuOption = config.getBoolean("menuOption", true)
	}

	/** Shows [name]'s hiscores, asking the website for them. */
	@JvmStatic
	fun show(name: String) {
		// names show with no-break spaces in the game
		shown = name.replace(' ', ' ').trim()
		HiscoreLookup.lookup(shown!!)
	}

	/** The grid cell (index into [GRID]) at ([x], [y]) on the panel, -1 for none. */
	@JvmStatic
	fun cellAt(x: Int, y: Int, top: Int): Int {
		if (x < 4 || y < top) {
			return -1
		}
		val column: Int = (x - 4) / CELL_WIDTH
		val row: Int = (y - top) / CELL_HEIGHT
		val i: Int = row * COLUMNS + column
		return if (column < COLUMNS && i < GRID.size) i else -1
	}

	/** Skill [skill]'s tooltip in [result]: "Attack: rank 3, 15,055,504 xp", or that it has no rank. */
	@JvmStatic
	fun tooltip(result: HiscoreLookup.Result, skill: Int): String {
		if (result.ranks[skill] < 0) {
			return Skill.name(skill) + ": not ranked"
		}
		return Skill.name(skill) + ": rank " + InventoryValue.withCommas(result.ranks[skill].toLong()) + ", " +
			InventoryValue.withCommas(result.xp[skill]) + " xp"
	}

	private fun draw(g: Graphics, overlay: PanelOverlay): Size? {
		val client: ClientApi = context?.client() ?: return null
		val name: String = shown ?: return null
		if (!enabled || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return null
		}
		val result: HiscoreLookup.Result? = HiscoreLookup.lookup(name)
		g.font(Font.BOLD)
		val line: Int = g.fontHeight()
		if (result == null) {
			val text: String = if (HiscoreLookup.notFound(name)) "No hiscores for " + name + "." else "Looking up " + name + "..."
			val width: Int = maxOf(WIDTH, g.textWidth(text) + 8)
			g.fillRect(0, 0, width, line + 8, overlay.backgroundColour)
			g.text(4, line + 2, text, WHITE, SHADOW)
			return size.set(width, line + 8)
		}
		val top: Int = line + 6
		val rows: Int = (GRID.size + COLUMNS - 1) / COLUMNS
		val height: Int = top + rows * CELL_HEIGHT + line + 8
		g.fillRect(0, 0, WIDTH, height, overlay.backgroundColour)
		g.text(4, line + 1, result.player, TITLE_COLOUR, SHADOW)
		g.font(Font.SMALL)
		for (i in GRID.indices) {
			val skill: Int = GRID[i]
			val x: Int = 4 + (i % COLUMNS) * CELL_WIDTH
			val y: Int = top + (i / COLUMNS) * CELL_HEIGHT
			g.spriteScaled(SkillSprites.ICONS[skill], x, y + 2, 22, 22)
			val level: Int = result.levels[skill]
			g.text(x + 28, y + 18, if (level < 0) "--" else level.toString(), TEXT_COLOUR, SHADOW)
		}
		val total: String = "Total: " + (if (result.overallLevel < 0) "--" else result.overallLevel.toString()) +
			(if (result.overallRank < 0) "" else "   Rank: " + InventoryValue.withCommas(result.overallRank.toLong()))
		g.text(4, height - 6, total, WHITE, SHADOW)
		// the skill under the mouse
		val cell: Int = cellAt(client.mouseX() - overlay.boundsX, client.mouseY() - overlay.boundsY, top)
		if (cell >= 0 && client.mouseY() - overlay.boundsY < top + rows * CELL_HEIGHT) {
			client.addTooltip(tooltip(result, GRID[cell]))
		}
		return size.set(WIDTH, height)
	}
}
