package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.ItemDefinition
import com.jagex.game.runetek5.plugin.api.KeyCode
import com.jagex.game.runetek5.plugin.api.LocDefinition
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuEntry
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.NpcDefinition
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.MenuBuilt
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the left click and the Shift-click of npcs, locations, ground
 * items and items chosen by the player, after RuneLite's Menu Entry Swapper. Hold Shift and
 * right-click a thing for "Swap left click: <option>" and "Swap shift click: <option>" over each of
 * its options (and Walk here), and "Reset left click" / "Reset shift click" once one is chosen;
 * the choice is kept in the settings by the thing's id. Shift-click Walk here for each kind and
 * the removal of a dying npc's entries are switches. Each game tick the
 * finished menu is read at MenuBuilt and the chosen entry promoted to the top, which the runtime
 * also makes the left click whatever the client's own Shift rules would pick.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md) on API 7.
 */
object MenuEntrySwapper : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("menuswapper", "Menu Entry Swapper", "2", PluginApi.VERSION, "Choose what left click and Shift-click do on npcs, objects, ground items and items (hold Shift and right-click one). Can also make Shift-click walk and hide the menus of dead npcs.", "Pixel")

	/** The kinds a swap is kept by: the settings key's first word. */
	const val KIND_NPC: Int = 0

	const val KIND_LOC: Int = 1

	const val KIND_GROUND: Int = 2

	const val KIND_ITEM: Int = 3

	const val KIND_WIDGET: Int = 4

	private val KIND_NAMES: Array<String> = arrayOf("npc", "loc", "ground", "item", "ui")

	/** The client's click types by kind (MiniMenuAction: the npc, loc and ground item actions and their targets); an attack on a higher level adds [LEVEL_PENALTY]. */
	private val NPC_TYPES: IntArray = intArrayOf(MiniMenuAction.OPNPC1, MiniMenuAction.OPNPC2, MiniMenuAction.OPNPC3, MiniMenuAction.OPNPC4, MiniMenuAction.OPNPC5, MiniMenuAction.OPNPC6, MiniMenuAction.TGT_NPC)

	private val LOC_TYPES: IntArray = intArrayOf(MiniMenuAction.OPLOC1, MiniMenuAction.OPLOC2, MiniMenuAction.OPLOC3, MiniMenuAction.OPLOC4, MiniMenuAction.OPLOC5, MiniMenuAction.OPLOC6, MiniMenuAction.TGT_LOC)

	private val GROUND_TYPES: IntArray = intArrayOf(MiniMenuAction.OPOBJ1, MiniMenuAction.OPOBJ2, MiniMenuAction.OPOBJ3, MiniMenuAction.OPOBJ4, MiniMenuAction.OPOBJ5, MiniMenuAction.TGT_GROUND)

	private const val LEVEL_PENALTY: Int = 2000

	/** The backpack's interface: its items' options are the item's own. */
	private const val BACKPACK: Int = 149

	const val WALK_HERE: String = "Walk here"

	/** The most options a thing is offered swaps for: an npc, loc or item has five, plus Use and Drop. */
	private const val MAX_OPTIONS: Int = 8

	/** Targets seen while the menu was built, oldest first; the menu's subject is among them. */
	private const val MAX_SUBJECTS: Int = 8

	@JvmField
	var enabled: Boolean = true

	private var leftClickCustomization: Boolean = true

	private var shiftClickCustomization: Boolean = true

	private var npcCustomization: Boolean = true

	private var objectCustomization: Boolean = true

	private var groundItemCustomization: Boolean = true

	private var npcShiftClickWalkHere: Boolean = false

	private var objectShiftClickWalkHere: Boolean = false

	private var groundItemShiftClickWalkHere: Boolean = false

	private var removeDeadNpcMenus: Boolean = false

	/** How many entries were promoted, for the smoke test. */
	@JvmField
	var swaps: Int = 0

	private var context: PluginContext? = null

	private val subjectKinds: IntArray = IntArray(MAX_SUBJECTS)

	private val subjectIds: IntArray = IntArray(MAX_SUBJECTS)

	private val subjectWidgets: IntArray = IntArray(MAX_SUBJECTS)

	private var subjectCount: Int = 0

	private val swapLeft: IntArray = IntArray(MAX_OPTIONS)

	private val swapShift: IntArray = IntArray(MAX_OPTIONS)

	private var walkLeft: Int = -1

	private var walkShift: Int = -1

	private var resetLeft: Int = -1

	private var resetShift: Int = -1

	/** The options of the thing a swap entry was offered for, filled when the entries are added and read when one is clicked. */
	private val options: Array<String?> = arrayOfNulls(MAX_OPTIONS)

	private val menuListener: EventListener<MenuBuilt> = EventListener { _ -> apply() }

	private val entryListener: EventListener<MenuEntryAdded> = EventListener { event -> offer(event) }

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
		context.client().consolePrint(if (enabled) "Menu entry swapper on." else "Menu entry swapper off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		readSettings(context.config())
		context.settings().toggle("enabled", "Menu entry swapper", true)
		context.settings().toggle("leftClickCustomization", "Customisable left click", true)
		context.settings().toggle("shiftClickCustomization", "Customisable Shift-click", true)
		context.settings().toggle("npcCustomization", "Customise npcs", true)
		context.settings().toggle("objectCustomization", "Customise objects", true)
		context.settings().toggle("groundItemCustomization", "Customise ground items", true)
		context.settings().toggle("npcShiftClickWalkHere", "Npcs: Shift-click Walk here", false)
		context.settings().toggle("objectShiftClickWalkHere", "Objects: Shift-click Walk here", false)
		context.settings().toggle("groundItemShiftClickWalkHere", "Ground items: Shift-click Walk here", false)
		context.settings().toggle("removeDeadNpcMenus", "Remove dead npc menus", false)
		for (k in 0 until MAX_OPTIONS) {
			swapLeft[k] = context.registerMenuAction(MenuAction { target, a, b, id -> choose(target, a, b, id, k, false) })
			swapShift[k] = context.registerMenuAction(MenuAction { target, a, b, id -> choose(target, a, b, id, k, true) })
		}
		walkLeft = context.registerMenuAction(MenuAction { target, a, b, id -> choose(target, a, b, id, -1, false) })
		walkShift = context.registerMenuAction(MenuAction { target, a, b, id -> choose(target, a, b, id, -1, true) })
		resetLeft = context.registerMenuAction(MenuAction { target, a, b, id -> reset(target, a, b, id, false) })
		resetShift = context.registerMenuAction(MenuAction { target, a, b, id -> reset(target, a, b, id, true) })
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.MENU_ENTRY_ADDED, entryListener)
		context.events().subscribe(Events.MENU_BUILT, menuListener)
		context.registerCommand("menuswapper", toggle)
	}

	override fun stop() {
		context = null
		subjectCount = 0
	}

	private fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		leftClickCustomization = config.getBoolean("leftClickCustomization", true)
		shiftClickCustomization = config.getBoolean("shiftClickCustomization", true)
		npcCustomization = config.getBoolean("npcCustomization", true)
		objectCustomization = config.getBoolean("objectCustomization", true)
		groundItemCustomization = config.getBoolean("groundItemCustomization", true)
		npcShiftClickWalkHere = config.getBoolean("npcShiftClickWalkHere", false)
		objectShiftClickWalkHere = config.getBoolean("objectShiftClickWalkHere", false)
		groundItemShiftClickWalkHere = config.getBoolean("groundItemShiftClickWalkHere", false)
		removeDeadNpcMenus = config.getBoolean("removeDeadNpcMenus", false)
	}

	// --- what is under the mouse ---

	/** The settings key of a thing's left or Shift click: `npc_12`, `npc_shift_12`, `ui_9764864_995`. */
	@JvmStatic
	fun key(kind: Int, id: Int, widgetId: Int, shift: Boolean): String {
		val out: StringBuilder = StringBuilder(KIND_NAMES[kind])
		if (shift) {
			out.append("_shift")
		}
		if (kind == KIND_WIDGET) {
			out.append('_').append(widgetId)
		}
		return out.append('_').append(id).toString()
	}

	/** The kind of a menu entry of the client's: by its click type, or by the component it is on for an item in one; -1 for none of ours. */
	@JvmStatic
	fun kindOf(clickType: Int, param1: Int, itemId: Int): Int {
		val type: Int = if (clickType >= LEVEL_PENALTY) clickType - LEVEL_PENALTY else clickType
		if (NPC_TYPES.contains(type)) {
			return KIND_NPC
		}
		if (LOC_TYPES.contains(type)) {
			return KIND_LOC
		}
		if (GROUND_TYPES.contains(type)) {
			return KIND_GROUND
		}
		if (itemId >= 0 && (type == MiniMenuAction.IF_BUTTONX1 || type == MiniMenuAction.IF_BUTTONX2 || type == MiniMenuAction.IF_BUTTONT)) {
			return if (param1 ushr 16 == BACKPACK) KIND_ITEM else KIND_WIDGET
		}
		return -1
	}

	private fun kindOf(target: MenuTarget, widgetId: Int): Int {
		return when {
			target === MenuTarget.NPC -> KIND_NPC
			target === MenuTarget.LOC -> KIND_LOC
			target === MenuTarget.GROUND_ITEM -> KIND_GROUND
			target === MenuTarget.WIDGET -> if (widgetId ushr 16 == BACKPACK) KIND_ITEM else KIND_WIDGET
			else -> -1
		}
	}

	private fun customisable(kind: Int): Boolean {
		return when (kind) {
			KIND_NPC -> npcCustomization
			KIND_LOC -> objectCustomization
			KIND_GROUND -> groundItemCustomization
			else -> true
		}
	}

	/** The options a thing offers, in [options]; how many. The npc index, loc id, item id or (widget id, item id) name it. */
	private fun optionsOf(client: ClientApi, kind: Int, id: Int, widgetId: Int): Int {
		var n: Int = 0
		fun add(option: String?) {
			if (option != null && option.isNotEmpty() && n < MAX_OPTIONS && !option.equals("Examine", ignoreCase = true)) {
				options[n++] = option
			}
		}
		when (kind) {
			KIND_NPC -> {
				val type: NpcDefinition = client.npcType(id) ?: return 0
				for (i in 0 until 5) {
					add(type.action(i))
				}
			}
			KIND_LOC -> {
				val type: LocDefinition = client.locType(id) ?: return 0
				for (i in 0 until 5) {
					add(type.action(i))
				}
			}
			KIND_GROUND -> {
				val type: ItemDefinition = client.item(id) ?: return 0
				for (i in 0 until 5) {
					add(type.groundAction(i))
				}
			}
			KIND_ITEM -> {
				val type: ItemDefinition = client.item(id) ?: return 0
				for (i in 0 until 5) {
					add(type.inventoryAction(i))
				}
				add("Use")
				add("Drop")
			}
			KIND_WIDGET -> {
				val widget: Widget = client.widgetById(widgetId) ?: return 0
				for (i in 0 until widget.actionCount()) {
					add(widget.action(i))
				}
			}
		}
		return n
	}

	/** The name of the thing, for the chat line. */
	private fun nameOf(client: ClientApi, kind: Int, id: Int): String {
		val name: String? = when (kind) {
			KIND_NPC -> client.npcType(id)?.name()
			KIND_LOC -> client.locType(id)?.name()
			else -> client.item(id)?.name()
		}
		return name ?: "it"
	}

	// --- the Shift menu ---

	/** While the menu is built: the target noted for [apply], and with Shift held the swap entries for it. */
	private fun offer(event: MenuEntryAdded) {
		val client: ClientApi = context?.client() ?: return
		if (!enabled) {
			return
		}
		val kind: Int = kindOf(event.target, event.widgetId)
		if (kind < 0 || (event.target === MenuTarget.WIDGET && event.itemId < 0)) {
			return
		}
		// the thing's id: an npc by its type, so a swap holds for every banker; the others as the menu names them
		val id: Int = if (kind == KIND_NPC) (client.npcByIndex(event.id)?.typeId() ?: return) else if (event.target === MenuTarget.WIDGET) event.itemId else event.id
		note(kind, id, event.widgetId)
		if (!client.isKeyHeld(KeyCode.SHIFT) || !customisable(kind) || (!leftClickCustomization && !shiftClickCustomization)) {
			return
		}
		val config: PluginConfig = context?.config() ?: return
		val count: Int = optionsOf(client, kind, id, event.widgetId)
		// the last entry added is the highest: Reset first, then the Shift-click swaps, then the left-click swaps, each from the last option up
		if (shiftClickCustomization && config.get(key(kind, id, event.widgetId, true), "").isNotEmpty()) {
			client.addMenuEntry("Reset shift click", resetShift)
		}
		if (leftClickCustomization && config.get(key(kind, id, event.widgetId, false), "").isNotEmpty()) {
			client.addMenuEntry("Reset left click", resetLeft)
		}
		if (shiftClickCustomization) {
			if (kind != KIND_ITEM && kind != KIND_WIDGET) {
				client.addMenuEntry("Swap shift click: " + WALK_HERE, walkShift)
			}
			for (k in count - 1 downTo 0) {
				client.addMenuEntry("Swap shift click: " + options[k], swapShift[k])
			}
		}
		if (leftClickCustomization) {
			if (kind != KIND_ITEM && kind != KIND_WIDGET) {
				client.addMenuEntry("Swap left click: " + WALK_HERE, walkLeft)
			}
			for (k in count - 1 downTo 0) {
				client.addMenuEntry("Swap left click: " + options[k], swapLeft[k])
			}
		}
	}

	private fun note(kind: Int, id: Int, widgetId: Int) {
		for (i in 0 until subjectCount) {
			if (subjectKinds[i] == kind && subjectIds[i] == id && subjectWidgets[i] == widgetId) {
				return
			}
		}
		if (subjectCount == MAX_SUBJECTS) {
			for (i in 1 until MAX_SUBJECTS) {
				subjectKinds[i - 1] = subjectKinds[i]
				subjectIds[i - 1] = subjectIds[i]
				subjectWidgets[i - 1] = subjectWidgets[i]
			}
			subjectCount--
		}
		subjectKinds[subjectCount] = kind
		subjectIds[subjectCount] = id
		subjectWidgets[subjectCount] = widgetId
		subjectCount++
	}

	/** A swap entry was clicked: the action's arguments are the target's, with a component's id and slot for an item in one. */
	@Suppress("UNUSED_PARAMETER") // the action's scene x (a component's slot) is not part of a swap's key
	private fun choose(target: MenuTarget, a: Int, b: Int, id: Int, slot: Int, shift: Boolean) {
		val client: ClientApi = context?.client() ?: return
		val config: PluginConfig = context?.config() ?: return
		val widgetId: Int = if (target === MenuTarget.WIDGET) b else 0
		val kind: Int = kindOf(target, widgetId)
		if (kind < 0) {
			return
		}
		val thing: Int = if (kind == KIND_NPC) (client.npcByIndex(id)?.typeId() ?: return) else id
		val option: String = if (slot < 0) WALK_HERE else (if (slot < optionsOf(client, kind, thing, widgetId)) options[slot] else null) ?: return
		config.set(key(kind, thing, widgetId, shift), option)
		config.save()
		client.chatMessage((if (shift) "Shift-click on " else "Left click on ") + nameOf(client, kind, thing) + " is now " + option + ".")
	}

	@Suppress("UNUSED_PARAMETER") // as choose
	private fun reset(target: MenuTarget, a: Int, b: Int, id: Int, shift: Boolean) {
		val client: ClientApi = context?.client() ?: return
		val config: PluginConfig = context?.config() ?: return
		val widgetId: Int = if (target === MenuTarget.WIDGET) b else 0
		val kind: Int = kindOf(target, widgetId)
		if (kind < 0) {
			return
		}
		val thing: Int = if (kind == KIND_NPC) (client.npcByIndex(id)?.typeId() ?: return) else id
		config.remove(key(kind, thing, widgetId, shift))
		config.save()
		client.chatMessage((if (shift) "Shift-click on " else "Left click on ") + nameOf(client, kind, thing) + " has been reset.")
	}

	// --- the finished menu ---

	/** At MenuBuilt: a chosen click, else Shift-click Walk here; and a dying npc's entries gone. */
	@JvmStatic
	fun apply() {
		val client: ClientApi = context?.client() ?: return
		if (!enabled) {
			subjectCount = 0
			return
		}
		val config: PluginConfig? = context?.config()
		val shift: Boolean = client.isKeyHeld(KeyCode.SHIFT)
		if (removeDeadNpcMenus) {
			removeDying(client)
		}
		val done: Boolean = config != null && applyChosen(client, config, shift)
		if (!done && shift) {
			applyWalkHere(client)
		}
		subjectCount = 0
	}

	private fun removeDying(client: ClientApi) {
		var i: Int = client.menuEntryCount() - 1
		while (i >= 0) {
			val entry: MenuEntry? = client.menuEntry(i)
			if (entry != null && kindOf(entry.clickType(), entry.param1(), entry.itemId()) == KIND_NPC) {
				val npc: Npc? = client.npcByIndex(entry.entityUid().toInt())
				if (npc != null && npc.isDying()) {
					client.removeMenuEntry(i)
				}
			}
			i--
		}
	}

	/** The subjects' chosen clicks, newest subject first (the thing whose entries were added last is on top). */
	private fun applyChosen(client: ClientApi, config: PluginConfig, shift: Boolean): Boolean {
		var s: Int = subjectCount - 1
		while (s >= 0) {
			val kind: Int = subjectKinds[s]
			val chosen: String = config.get(key(kind, subjectIds[s], subjectWidgets[s], shift), "")
			if (chosen.isNotEmpty() && promote(client, kind, subjectWidgets[s], chosen)) {
				return true
			}
			s--
		}
		return false
	}

	/** With Shift held: Walk here on top for the kinds the player asked it of, when the menu has such a subject. */
	private fun applyWalkHere(client: ClientApi) {
		for (s in 0 until subjectCount) {
			val wanted: Boolean = when (subjectKinds[s]) {
				KIND_NPC -> npcShiftClickWalkHere
				KIND_LOC -> objectShiftClickWalkHere
				KIND_GROUND -> groundItemShiftClickWalkHere
				else -> false
			}
			if (wanted && promote(client, subjectKinds[s], 0, WALK_HERE)) {
				return
			}
		}
	}

	/** Promotes the entry of [kind] whose option is [option] (or the Walk here entry); false when the menu has none. */
	private fun promote(client: ClientApi, kind: Int, widgetId: Int, option: String): Boolean {
		val walk: Boolean = option.equals(WALK_HERE, ignoreCase = true)
		for (i in 0 until client.menuEntryCount()) {
			val entry: MenuEntry? = client.menuEntry(i)
			if (entry != null && (if (walk) entry.clickType() == MiniMenuAction.WALK else entryIs(entry, kind, widgetId, option))) {
				return promoteAt(client, i)
			}
		}
		return false
	}

	private fun entryIs(entry: MenuEntry, kind: Int, widgetId: Int, option: String): Boolean {
		return kindOf(entry.clickType(), entry.param1(), entry.itemId()) == kind && (kind != KIND_WIDGET || entry.param1() == widgetId) && ClientFeatures.plainText(entry.option()).equals(option, ignoreCase = true)
	}

	private fun promoteAt(client: ClientApi, i: Int): Boolean {
		if (i == 0) {
			return true
		}
		if (client.promoteMenuEntry(i)) {
			swaps++
			return true
		}
		return false
	}
}
