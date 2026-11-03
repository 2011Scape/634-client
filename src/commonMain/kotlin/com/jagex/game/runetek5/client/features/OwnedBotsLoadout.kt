package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.client.features.OwnedBots.Bot
import com.jagex.game.runetek5.client.features.OwnedBots.EQUIPMENT
import com.jagex.game.runetek5.client.features.OwnedBots.EQUIPMENT_NAMES
import com.jagex.game.runetek5.client.features.OwnedBots.INVENTORY
import com.jagex.game.runetek5.client.features.OwnedBots.command
import com.jagex.game.runetek5.client.features.OwnedBots.commandName
import com.jagex.game.runetek5.client.features.OwnedBots.itemAmount
import com.jagex.game.runetek5.client.features.OwnedBots.itemId
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.OverlayMenuEntry
import com.jagex.game.runetek5.plugin.api.OverlayLayer
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * The Owned Bots plugin's Equipment & Inventory window for the selected bot ([OwnedBots.selected]): the worn
 * items laid out as the worn-equipment screen does, the carried ones as the inventory, in the game's stone window.
 * A left click does what the game's would (a worn item comes off, a carried one gets its first option or is
 * picked with Use); the right-click menu lists the item's own options with Use in the game's place, and the
 * bank's amounts for a stack. Everything it does is a `::bot` command.
 */
object OwnedBotsLoadout {

	@JvmField var open: Boolean = false

	/** The carried item (inventory index) picked with Use, waiting for what it goes on; -1 none. */
	@JvmField var useFrom: Int = -1

	/** The item id in [useFrom] when it was picked: the pick lapses when the slot comes to hold something else. */
	@JvmField var useFromItem: Int = -1

	private val size: Size = Size()

	@JvmField
	val overlay: PanelOverlay = object : PanelOverlay("OwnedBotsLoadout") {
		init {
			menuOnRightClick = true
			// a window: in front of the bank, so the player sees what is taking their clicks
			layer = OverlayLayer.ABOVE_WIDGETS
		}

		override fun menuEntries(): List<OverlayMenuEntry> = entries(this)

		override fun onClick(x: Int, y: Int, button: Int): Boolean = click(this, x, y, button)

		override fun render(g: Graphics, view: Viewport): Size? = draw(g, this)
	}

	/** Opens the window on bot [slot]. */
	@JvmStatic
	fun show(slot: Int) {
		OwnedBots.selected = slot
		open = true
		useFrom = -1
	}

	@JvmStatic
	fun close() {
		open = false
		useFrom = -1
	}

	@JvmStatic
	fun pickForUse(index: Int, itemId: Int) {
		useFrom = index
		useFromItem = itemId
	}

	/** The server's variables were read: the pick lapses once its slot holds something else (eaten, banked, moved). */
	@JvmStatic
	fun tick(bot: Bot?) {
		if (useFrom >= 0 && itemId(bot?.inventory?.getOrNull(useFrom) ?: 0) != useFromItem) {
			useFrom = -1
		}
	}

	/** The game's left click on a carried item: its first option among 1-3, Use (null) when it has none. */
	@JvmStatic
	fun leftClickOption(options: Array<String?>): String? {
		for (k in 0 until minOf(USE_AFTER, options.size)) {
			val option: String? = options[k]
			if (option != null) {
				return option
			}
		}
		return null
	}

	private fun entries(overlay: PanelOverlay): List<OverlayMenuEntry> {
		val entries: ArrayList<OverlayMenuEntry> = ArrayList()
		val bot: Bot = OwnedBots.state.bot(OwnedBots.selected) ?: return entries
		val client: ClientApi = OwnedBots.client ?: return entries
		val slot: Int = bot.slot + 1
		val hovered: Int = hoveredItem(overlay)
		if (hovered >= 0) {
			val worn: Boolean = hovered < EQUIPMENT
			val packed: Int = if (worn) bot.equipment[hovered] else bot.inventory[hovered - EQUIPMENT]
			val id: Int = itemId(packed)
			if (id >= 0) {
				val name: String = client.itemName(id) ?: id.toString()
				val arg: String = commandName(name)
				val target: String = "<col=ff9040>" + name
				val carried: Int = hovered - EQUIPMENT
				val wieldable: Boolean = client.itemInventoryOptions(id).any { it.equals("Wield", ignoreCase = true) }
				if (wieldable) {
					if (bot.spec == id) {
						entries.add(OverlayMenuEntry("Clear special attack", target, Task { command("bot spec $slot none") }))
					} else {
						entries.add(OverlayMenuEntry(OwnedBots.SPECIAL, target, Task { command("bot spec $slot $arg") }))
					}
				}
				if (worn) {
					entries.add(OverlayMenuEntry("Remove", target, Task { command("bot unequip $slot " + EQUIPMENT_NAMES[hovered]) }))
				} else if (useFrom >= 0) {
					// an item picked with Use goes on this one, as in the game's inventory
					val from: Int = itemId(bot.inventory.getOrElse(useFrom) { -1 })
					if (carried != useFrom && from >= 0) {
						val fromName: String = client.itemName(from) ?: from.toString()
						entries.add(OverlayMenuEntry("Use", "<col=ff9040>$fromName<col=ffffff> -> $target", Task { useOn(bot, carried) }))
					}
					entries.add(OverlayMenuEntry("Cancel", "Use", Task { useFrom = -1 }))
				} else {
					// the game's inventory order: options 1-3, Use, options 4-5
					val options: Array<String?> = client.itemInventoryOptions(id)
					for (k in options.indices) {
						if (k == USE_AFTER) {
							entries.add(OverlayMenuEntry("Use", target, Task { pickForUse(carried, id) }))
						}
						val option: String = options[k] ?: continue
						entries.add(OverlayMenuEntry(option, target, Task { itemOption(bot, carried, option, arg) }))
					}
					// a stack: how many, as the bank asks
					val amount: Int = itemAmount(packed)
					if (amount > 1) {
						for (choice in OwnedBots.AMOUNTS) {
							val label: String = if (choice == OwnedBots.ALL) "$choice ($amount)" else choice
							val count: String = choice.lowercase()
							entries.add(OverlayMenuEntry(label, target, Task { command("bot take $slot $arg $count") }, "Take"))
							entries.add(OverlayMenuEntry(label, target, Task { command("bot take $slot $arg $count bank") }, "Bank"))
						}
					} else {
						entries.add(OverlayMenuEntry("Take", target, Task { command("bot take $slot $arg") }))
						entries.add(OverlayMenuEntry("Bank", target, Task { command("bot take $slot $arg bank") }))
					}
				}
			}
		}
		entries.add(OverlayMenuEntry("Bank everything", bot.name, Task { command("bot bank $slot") }))
		for (preset in OwnedBots.presets) {
			entries.add(OverlayMenuEntry(preset.first, bot.name, Task { command("bot loadout load $slot " + preset.first) }, OwnedBots.LOAD_PRESET))
		}
		entries.add(OverlayMenuEntry("Save as preset...", bot.name + "'s gear", Task { command("bot loadout save $slot") }))
		entries.add(OverlayMenuEntry("Save as preset...", "my gear", Task { command("bot loadout save") }))
		for (preset in OwnedBots.presets) {
			entries.add(OverlayMenuEntry(preset.first, "", Task { command("bot loadout delete " + preset.first) }, OwnedBots.DELETE_PRESET))
		}
		entries.add(OverlayMenuEntry("Close", "Loadout", Task { close() }))
		return entries
	}

	/**
	 * A left click in the window, at ([x], [y]) from its corner: the close button shuts it, a worn item comes
	 * off, a carried one is worn or gets its first option (Eat, Drink...), as the game's left click does; with
	 * an item picked by Use, it goes on the clicked one. Every left click inside the window stays out of the game.
	 */
	private fun click(overlay: PanelOverlay, x: Int, y: Int, button: Int): Boolean {
		val client: ClientApi = OwnedBots.client ?: return false
		if (button != 0 || !OwnedBots.enabled || !open) {
			return false
		}
		if (onCloseButton(overlay, x, y)) {
			close()
			return true
		}
		val bot: Bot = OwnedBots.state.bot(OwnedBots.selected) ?: return true
		val hovered: Int = itemAt(x, y)
		if (hovered < 0) {
			useFrom = -1
			return true
		}
		if (hovered < EQUIPMENT) {
			useFrom = -1
			if (itemId(bot.equipment[hovered]) >= 0) {
				command("bot unequip " + (bot.slot + 1) + " " + EQUIPMENT_NAMES[hovered])
			}
			return true
		}
		val carried: Int = hovered - EQUIPMENT
		val id: Int = itemId(bot.inventory[carried])
		if (useFrom >= 0) {
			if (carried != useFrom && id >= 0) {
				useOn(bot, carried)
			}
			useFrom = -1
			return true
		}
		if (id < 0) {
			return true
		}
		val name: String = client.itemName(id) ?: return true
		val option: String? = leftClickOption(client.itemInventoryOptions(id))
		if (option == null) {
			pickForUse(carried, id)
		} else {
			itemOption(bot, carried, option, commandName(name))
		}
		return true
	}

	/** The bot clicks [option] on its carried item [index]; wearing goes through `bot equip` for its messages. */
	private fun itemOption(bot: Bot, index: Int, option: String, arg: String) {
		val slot: Int = bot.slot + 1
		if (option.lowercase() in EQUIP_OPTIONS) {
			command("bot equip $slot $arg")
		} else {
			command("bot item $slot $index $option")
		}
	}

	private fun useOn(bot: Bot, to: Int) {
		val from: Int = useFrom
		useFrom = -1
		if (from >= 0 && from != to) {
			command("bot use " + (bot.slot + 1) + " $from $to")
		}
	}

	/** Whether ([x], [y]) from the window's corner is on its close button (top right, where [draw] puts it). */
	private fun onCloseButton(overlay: PanelOverlay, x: Int, y: Int): Boolean {
		val left: Int = overlay.boundsWidth - CLOSE_RIGHT - CLOSE_SIZE
		return overlay.boundsWidth > 0 && x >= left && x < left + CLOSE_SIZE && y >= CLOSE_TOP && y < CLOSE_TOP + CLOSE_SIZE
	}

	/** The item cell under the mouse: 0-13 worn (by equipment index), 14-41 carried, -1 none. */
	private fun hoveredItem(overlay: PanelOverlay): Int {
		val client: ClientApi = OwnedBots.client ?: return -1
		return itemAt(client.mouseX() - overlay.boundsX, client.mouseY() - overlay.boundsY)
	}

	/** The item cell at ([x], [y]) from the window's corner: as [hoveredItem]. */
	private fun itemAt(x: Int, y: Int): Int {
		for (index in 0 until EQUIPMENT) {
			val sx: Int = SLOT_X[index]
			if (sx < 0) {
				continue
			}
			val left: Int = WORN_X + sx
			val top: Int = OwnedBots.CONTENT_Y + SLOT_Y[index]
			if (x >= left && x < left + SLOT && y >= top && y < top + SLOT) {
				return index
			}
		}
		val column: Int = (x - CARRIED_X) / INV_W
		val row: Int = (y - OwnedBots.CONTENT_Y) / INV_H
		if (x < CARRIED_X || y < OwnedBots.CONTENT_Y || column !in 0 until INV_COLUMNS || row !in 0 until INV_ROWS) {
			return -1
		}
		return EQUIPMENT + row * INV_COLUMNS + column
	}

	private fun draw(g: Graphics, overlay: PanelOverlay): Size? {
		val bot: Bot = OwnedBots.state.bot(OwnedBots.selected) ?: return null
		if (!OwnedBots.enabled || !open || !bot.online || !g.fontReady()) {
			return null
		}
		val width: Int = CARRIED_X + INV_COLUMNS * INV_W + OwnedBots.PAD + OwnedBots.EDGE
		val height: Int = OwnedBots.CONTENT_Y + INV_ROWS * INV_H + OwnedBots.PAD + OwnedBots.EDGE
		OwnedBots.window(g, width, height, bot.name + "'s Equipment & Inventory")
		val client: ClientApi? = OwnedBots.client
		val overClose: Boolean = client != null && onCloseButton(overlay, client.mouseX() - overlay.boundsX, client.mouseY() - overlay.boundsY)
		g.sprite(if (overClose) CLOSE_HOVER else CLOSE, width - CLOSE_RIGHT - CLOSE_SIZE, CLOSE_TOP)
		val hovered: Int = hoveredItem(overlay)
		// the equipment tab's layout: its bars behind the slots (only the gaps between slots show them), then a slot sprite and the item in each
		for (i in BAR_X.indices) {
			val x: Int = WORN_X + BAR_X[i]
			val y: Int = OwnedBots.CONTENT_Y + BAR_Y[i]
			// the strips are 32 long and carry a 13 pixel offset in the cache: tiled along the bar, clipped to it
			if (BAR_VERTICAL[i]) {
				g.clip(x, y, BAR, BAR_LENGTH[i])
				var at: Int = y
				while (at < y + BAR_LENGTH[i]) {
					g.sprite(BAR_V, x - OwnedBots.EDGE_OFFSET, at)
					at += BAR_TILE
				}
			} else {
				g.clip(x, y, BAR_LENGTH[i], BAR)
				var at: Int = x
				while (at < x + BAR_LENGTH[i]) {
					g.sprite(BAR_H, at, y - OwnedBots.EDGE_OFFSET)
					at += BAR_TILE
				}
			}
			g.unclip()
		}
		for (index in 0 until EQUIPMENT) {
			val sx: Int = SLOT_X[index]
			if (sx < 0) {
				continue
			}
			val x: Int = WORN_X + sx
			val y: Int = OwnedBots.CONTENT_Y + SLOT_Y[index]
			g.sprite(SLOT_SPRITE, x, y)
			if (index == hovered) {
				g.fillRect(x + 1, y + 1, SLOT - 2, SLOT - 2, OwnedBots.HOVER)
			}
			val id: Int = itemId(bot.equipment[index])
			if (id >= 0) {
				g.itemIcon(id, itemAmount(bot.equipment[index]), x, y + 2)
			} else {
				// an empty slot shows its outline, as the equipment tab does
				g.sprite(SLOT_PLACEHOLDER[index], x + (SLOT - PLACEHOLDER_SIZE) / 2, y + (SLOT - PLACEHOLDER_SIZE) / 2)
			}
		}
		for (index in 0 until INVENTORY) {
			val x: Int = CARRIED_X + (index % INV_COLUMNS) * INV_W
			val y: Int = OwnedBots.CONTENT_Y + (index / INV_COLUMNS) * INV_H
			if (EQUIPMENT + index == hovered) {
				g.fillRect(x, y, INV_W, INV_H, OwnedBots.HOVER)
			}
			val id: Int = itemId(bot.inventory[index])
			if (index == useFrom) {
				if (id < 0) {
					useFrom = -1
				} else {
					// picked with Use: outlined, as the game outlines a selected item
					g.drawRect(x + 2, y + 1, INV_W - 5, INV_H - 3, OwnedBots.WHITE)
				}
			}
			if (id >= 0) {
				g.itemIcon(id, itemAmount(bot.inventory[index]), x + (INV_W - 36) / 2, y + (INV_H - 32) / 2)
			}
		}
		return size.set(width, height)
	}

	// the worn-equipment screen's close button (667:74): 16 by 16, 10 in from the right and 3 down, 832 on hover
	private const val CLOSE: Int = 831
	private const val CLOSE_HOVER: Int = 832
	private const val CLOSE_SIZE: Int = 16
	private const val CLOSE_RIGHT: Int = 10
	private const val CLOSE_TOP: Int = 3

	// the equipment tab's slot layout (interface 387, measured in game): slot sprite 170 at each, by equipment index;
	// a column 36 wide with 4 between (cape 16, hat 57, ammo 97) and the weapon and shield columns 57 out from the middle
	private const val SLOT_SPRITE: Int = 170
	private const val SLOT: Int = 36
	private const val WORN_X: Int = OwnedBots.EDGE + OwnedBots.PAD
	private const val WORN_W: Int = 149
	private val SLOT_X: IntArray = intArrayOf(57, 16, 57, 0, 57, 113, -1, 57, -1, 0, 57, -1, 113, 97)
	private val SLOT_Y: IntArray = intArrayOf(0, 38, 38, 78, 78, 78, -1, 117, -1, 157, 157, -1, 157, 38)

	// the tab's bars (821 upright, 822 flat, both 7 thick) behind the slots: down the middle column, down the weapon
	// and shield columns, and along the cape and the weapon rows, centre to centre
	private const val BAR_V: Int = 821
	private const val BAR_H: Int = 822
	private const val BAR: Int = 7
	private const val BAR_TILE: Int = 32
	private val BAR_VERTICAL: BooleanArray = booleanArrayOf(true, true, true, false, false)
	private val BAR_X: IntArray = intArrayOf(72, 15, 128, 34, 18)
	private val BAR_Y: IntArray = intArrayOf(18, 96, 96, 53, 93)
	private val BAR_LENGTH: IntArray = intArrayOf(157, 79, 79, 81, 113)

	// the equipment tab's outlines for empty slots (32 by 32), by equipment index; -1 where no slot is drawn
	private const val PLACEHOLDER_SIZE: Int = 32
	private val SLOT_PLACEHOLDER: IntArray = intArrayOf(156, 157, 158, 159, 161, 162, -1, 163, -1, 164, 165, -1, 160, 166)

	// the game's inventory menu puts Use after an item's third option
	private const val USE_AFTER: Int = 3
	private val EQUIP_OPTIONS: Set<String> = setOf("wield", "wear", "equip")

	// the inventory's 4 by 7 grid, 42 by 36 a slot (interface 149)
	private const val INV_COLUMNS: Int = 4
	private const val INV_ROWS: Int = 7
	private const val INV_W: Int = 42
	private const val INV_H: Int = 36
	private const val CARRIED_X: Int = WORN_X + WORN_W + 14
}
