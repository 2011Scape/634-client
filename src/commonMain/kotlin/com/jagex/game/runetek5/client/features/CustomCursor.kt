package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.PluginPaths
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Image
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.ItemContainerChanged
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Custom Cursor. The mouse cursor over the game is one of
 * RuneLite's six pictures (RS3 gold and silver, the dragon daggers, a trout, the dragon scimitar), the
 * worn weapon's icon, or a picture of the player's own (cursor.png in the .634-client folder), instead
 * of the game's. The six pictures are in the client's resources as raw ARGB (/customcursor/cursors.rgba:
 * the count, then each one's width, height and pixels), which the browser build does not carry, nor can
 * it read the player's file. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object CustomCursor : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("customcursor", "Custom Cursor", "1", PluginApi.VERSION, "Replaces the mouse cursor with a gold or silver cursor, a dagger, a trout, a scimitar, your weapon or a picture of your own.", "Pixel")

	/** RuneLite's CustomCursor, in its order: the six pictures, then the worn weapon and the player's own picture. */
	@JvmField
	val STYLES: Array<String> = arrayOf("RS3 Gold", "RS3 Silver", "Dragon Dagger", "Dragon Dagger (p)", "Trout", "Dragon Scimitar", "Equipped weapon", "Custom image")

	const val EQUIPPED_WEAPON: Int = 6
	const val CUSTOM_IMAGE: Int = 7

	const val RESOURCE: String = "/customcursor/cursors.rgba"
	const val CUSTOM_FILE: String = "cursor.png"

	const val WORN: Int = 94
	const val WEAPON_SLOT: Int = 3

	@JvmField var enabled: Boolean = false
	@JvmField var cursorStyle: Int = 0

	/** The style and weapon the cursor shows now (-1 for the game's own), so it is set only on a change. */
	@JvmField var shownStyle: Int = -1
	private var shownWeapon: Int = -1

	private var pictures: Array<Image>? = null

	private var context: PluginContext? = null

	private val tickListener: EventListener<GameTick> = EventListener { _ -> context?.client()?.let { update(it) } }

	private val containerListener: EventListener<ItemContainerChanged> = EventListener { event ->
		if (event.containerId == WORN && cursorStyle == EQUIPPED_WEAPON) {
			// RuneLite's onItemContainerChanged: the weapon's picture again
			shownStyle = -2
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
			shownStyle = -2
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		shownStyle = -2
		context?.client()?.consolePrint(if (enabled) "Custom cursor enabled." else "Custom cursor disabled.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		context.settings().toggle("enabled", "Custom cursor", false)
		// RuneLite's CustomCursorConfig
		context.settings().choice("cursorStyle", "Cursor", STYLES[0], STYLES)
		readSettings(context.config())
		shownStyle = -2
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.ITEM_CONTAINER_CHANGED, containerListener)
		context.registerCommand("customcursor", toggle)
	}

	override fun stop() {
		context?.client()?.resetCursor()
		context = null
		shownStyle = -1
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", false)
		val style: Int = STYLES.indexOf(config.get("cursorStyle", STYLES[0]))
		cursorStyle = if (style < 0) 0 else style
	}

	/** Each client cycle: the cursor set again when the style, the weapon or the plugin's switch changed. */
	@JvmStatic
	fun update(client: ClientApi) {
		val wanted: Int = if (enabled) cursorStyle else -1
		val weapon: Int = if (wanted == EQUIPPED_WEAPON) weapon(client) else -1
		if (wanted == shownStyle && weapon == shownWeapon) {
			return
		}
		if (wanted == -1) {
			client.resetCursor()
			shownStyle = -1
			return
		}
		val picture: Image? = picture(client, wanted, weapon)
		if (picture == null) {
			// RuneLite's resetCursor when there is no picture: no weapon worn, no file, or a model not loaded yet (tried again next cycle)
			client.resetCursor()
			if (wanted != EQUIPPED_WEAPON || weapon < 0) {
				shownStyle = wanted
				shownWeapon = weapon
			}
			return
		}
		client.setCursor(picture, 0, 0)
		shownStyle = wanted
		shownWeapon = weapon
	}

	private fun picture(client: ClientApi, style: Int, weapon: Int): Image? {
		return when (style) {
			EQUIPPED_WEAPON -> if (weapon < 0 || client.gameState() !== GameState.LOGGED_IN) null else client.itemImage(weapon, 1)
			CUSTOM_IMAGE -> customImage(client)
			else -> pictures(client)?.getOrNull(style)
		}
	}

	private fun weapon(client: ClientApi): Int {
		val worn: ItemContainer = client.itemContainer(WORN) ?: return -1
		return if (worn.size() > WEAPON_SLOT) worn.itemId(WEAPON_SLOT) else -1
	}

	/** RuneLite's RUNELITE_DIR/cursor.png: the .634-client folder's. */
	private fun customImage(client: ClientApi): Image? {
		val root: io.File = PluginPaths.root() ?: return null
		val bytes: ByteArray = PluginPaths.readBytes(io.File(root.getPath() + "/" + CUSTOM_FILE)) ?: return null
		return client.loadImage(bytes)
	}

	/** The six pictures from the resource, null when it is missing or broken. */
	@JvmStatic
	fun pictures(client: ClientApi): Array<Image>? {
		val known: Array<Image>? = pictures
		if (known != null) {
			return known
		}
		val bytes: ByteArray = lang.readResource(RESOURCE) ?: return null
		val count: Int = readInt(bytes, 0)
		var at = 4
		val loaded: Array<Image> = Array(count) { _ ->
			if (at + 8 > bytes.size) {
				return null
			}
			val width: Int = readInt(bytes, at)
			val height: Int = readInt(bytes, at + 4)
			at += 8
			if (width <= 0 || height <= 0 || at + width * height * 4 > bytes.size) {
				return null
			}
			val argb = IntArray(width * height) { p -> readInt(bytes, at + p * 4) }
			at += width * height * 4
			client.createImage(width, height, argb) ?: return null
		}
		pictures = loaded
		return loaded
	}

	private fun readInt(bytes: ByteArray, at: Int): Int {
		if (at + 4 > bytes.size) {
			return 0
		}
		return ((bytes[at].toInt() and 0xFF) shl 24) or ((bytes[at + 1].toInt() and 0xFF) shl 16) or ((bytes[at + 2].toInt() and 0xFF) shl 8) or (bytes[at + 3].toInt() and 0xFF)
	}
}
