package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Tile Indicators. The tile under the mouse, the tile the
 * player is walking to (the minimap's flag, ClientApi.destinationTileX) and the player's true tile (where
 * the server has it, ahead of the drawn model), each with its own switch, border colour, fill colour and
 * border width, drawn in RuneLite's order on the game view. 634 has no cutscene flag RuneLite checks.
 */
object TileIndicators : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("tileindicators", "Tile Indicators", "1", PluginApi.VERSION, "Highlight the tile you are walking to, the tile under the mouse and your true tile (::tileindicators turns it on and off).", "Pixel")

	/** RuneLite's defaults: grey and cyan borders, fills black at alpha 50, the hovered tile's border invisible. */
	const val GRAY: Int = 0xFF808080.toInt()
	const val CYAN: Int = 0xFF00FFFF.toInt()
	const val FILL: Int = 50 shl 24
	const val NONE: Int = 0
	const val DEFAULT_WIDTH: Int = 2

	private const val MAX_WIDTH: Int = 10

	@JvmField var enabled: Boolean = false

	@JvmField var highlightDestinationTile: Boolean = true
	@JvmField var destinationColour: Int = GRAY
	@JvmField var destinationFill: Int = FILL
	@JvmField var destinationWidth: Int = DEFAULT_WIDTH

	@JvmField var highlightHoveredTile: Boolean = false
	@JvmField var hoveredColour: Int = NONE
	@JvmField var hoveredFill: Int = FILL
	@JvmField var hoveredWidth: Int = DEFAULT_WIDTH

	@JvmField var highlightCurrentTile: Boolean = false
	@JvmField var currentColour: Int = CYAN
	@JvmField var currentFill: Int = FILL
	@JvmField var currentWidth: Int = DEFAULT_WIDTH

	/** The tiles drawn last frame, by kind (destination, hovered, true tile), for the smoke test. */
	@JvmField
	val drawn: BooleanArray = BooleanArray(3)

	private val xs: IntArray = IntArray(4)

	private val ys: IntArray = IntArray(4)

	private var context: PluginContext? = null

	private val overlay: Overlay = Overlay { g, view -> draw(g, view) }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		enabled = !enabled
		context.config().setBoolean("enabled", enabled)
		context.config().save()
		context.client().consolePrint(if (enabled) "Tile indicators on." else "Tile indicators off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		declareSettings(context.settings())
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.addOverlay(overlay)
		context.registerCommand("tileindicators", toggle)
	}

	override fun stop() {
		context = null
		drawn.fill(false)
	}

	private fun declareSettings(s: SettingsSchema) {
		s.toggle("enabled", "Tile indicators", false)
		s.toggle("highlightDestinationTile", "Highlight destination tile", true)
		s.colour("highlightDestinationColor", "Destination tile colour", GRAY, true)
		s.colour("destinationTileFillColor", "Destination tile fill colour", FILL, true)
		s.number("destinationTileBorderWidth", "Destination tile border width", DEFAULT_WIDTH, 0, MAX_WIDTH, 1)
		s.toggle("highlightHoveredTile", "Highlight hovered tile", false)
		s.colour("highlightHoveredColor", "Hovered tile colour", NONE, true)
		s.colour("hoveredTileFillColor", "Hovered tile fill colour", FILL, true)
		s.number("hoveredTileBorderWidth", "Hovered tile border width", DEFAULT_WIDTH, 0, MAX_WIDTH, 1)
		s.toggle("highlightCurrentTile", "Highlight true tile", false)
		s.colour("highlightCurrentColor", "True tile colour", CYAN, true)
		s.colour("currentTileFillColor", "True tile fill colour", FILL, true)
		s.number("currentTileBorderWidth", "True tile border width", DEFAULT_WIDTH, 0, MAX_WIDTH, 1)
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		highlightDestinationTile = config.getBoolean("highlightDestinationTile", true)
		destinationColour = config.getColour("highlightDestinationColor", GRAY)
		destinationFill = config.getColour("destinationTileFillColor", FILL)
		destinationWidth = config.getInt("destinationTileBorderWidth", DEFAULT_WIDTH).coerceIn(0, MAX_WIDTH)
		highlightHoveredTile = config.getBoolean("highlightHoveredTile", false)
		hoveredColour = config.getColour("highlightHoveredColor", NONE)
		hoveredFill = config.getColour("hoveredTileFillColor", FILL)
		hoveredWidth = config.getInt("hoveredTileBorderWidth", DEFAULT_WIDTH).coerceIn(0, MAX_WIDTH)
		highlightCurrentTile = config.getBoolean("highlightCurrentTile", false)
		currentColour = config.getColour("highlightCurrentColor", CYAN)
		currentFill = config.getColour("currentTileFillColor", FILL)
		currentWidth = config.getInt("currentTileBorderWidth", DEFAULT_WIDTH).coerceIn(0, MAX_WIDTH)
	}

	/** RuneLite's TileIndicatorsOverlay.render: the hovered tile, then the destination, then the true tile. */
	private fun draw(g: Graphics, view: Viewport) {
		drawn.fill(false)
		val client: ClientApi = context?.client() ?: return
		val player: Player = client.localPlayer() ?: return
		if (!enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val level: Int = player.level()
		if (highlightHoveredTile && client.hoveredTileX() >= 0) {
			drawn[1] = TileOutline.draw(g, view, client, client.hoveredTileX(), client.hoveredTileY(), level, 1, hoveredColour, hoveredFill, hoveredWidth, xs, ys)
		}
		if (highlightDestinationTile && client.destinationTileX() >= 0) {
			drawn[0] = TileOutline.draw(g, view, client, client.destinationTileX(), client.destinationTileY(), level, 1, destinationColour, destinationFill, destinationWidth, xs, ys)
		}
		if (highlightCurrentTile) {
			drawn[2] = TileOutline.draw(g, view, client, player.serverTileX(), player.serverTileY(), level, 1, currentColour, currentFill, currentWidth, xs, ys)
		}
	}
}
