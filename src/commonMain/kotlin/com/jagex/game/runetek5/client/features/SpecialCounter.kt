package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.Counter
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Image
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.Npc
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
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.HitsplatApplied
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.VarpChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.math.abs

/**
 * Not part of the original client: RuneLite's Special Attack Counter. When the special attack energy
 * (varp 300, 0 to 1000 on Void) drops while a counted weapon is worn, the first hitsplat of yours on the
 * npc you are fighting within the weapon's hit delay is the special's: an infobox per weapon counts the
 * hits (Darklight) or the damage (the others), a new npc starting the counts again, and the hit rises over
 * your head (the spec drop). A threshold set for a weapon colours its count and notifies once reached.
 *
 * The weapons are RuneLite's that exist on 634: Darklight, the Bandos godsword, the Barrelchest anchor, the
 * bone daggers, the Dorgeshuun crossbow and the Seercull. The Dragon warhammer, Elder maul, Arclight,
 * Emberlight, Dinh's bulwark, Accursed sceptre, Tonalztics and the Eye of Ayak are OSRS-only, and so is the
 * defence drain infobox's percentage, which only their drains feed (the setting is kept). RuneLite's party
 * sharing has no server here. A built-in plugin (BuiltinPlugins, docs/PLUGINS.md).
 */
object SpecialCounter : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("specialcounter", "Special Attack Counter", "1", PluginApi.VERSION, "Counts your special attack hits and damage on the npc you fight, per weapon.", "Pixel")

	/** RuneLite's SpecialWeapon, the ones 634 has: its name, item ids, whether it counts damage, and its ranged hit delay in client cycles by distance. */
	class Weapon(@JvmField val name: String, @JvmField val itemIds: IntArray, @JvmField val damage: Boolean, @JvmField val delayBase: Int, @JvmField val delayPerTile: Int, @JvmField val thresholdKey: String?) {
		/** RuneLite's getHitDelay: the server ticks after the energy drops that the hitsplat lands. */
		fun hitDelay(distance: Int): Int = (delayBase + delayPerTile * distance) / CYCLES_PER_TICK + 1
	}

	@JvmField
	val WEAPONS: Array<Weapon> = arrayOf(
		Weapon("Darklight", intArrayOf(6746), false, 0, 0, "darklightThreshold"),
		Weapon("Bandos Godsword", intArrayOf(11696), true, 0, 0, "bandosGodswordThreshold"),
		Weapon("Barrelchest Anchor", intArrayOf(10887), true, 0, 0, null),
		Weapon("Bone Dagger", intArrayOf(8872, 8874, 8876, 8878), true, 0, 0, null),
		Weapon("Dorgeshuun Crossbow", intArrayOf(8880), true, 60, 3, null),
		Weapon("Seercull", intArrayOf(6724), true, 46, 5, null),
	)

	const val SPECIAL_VARP: Int = 300
	const val WORN: Int = 94
	const val WEAPON_SLOT: Int = 3

	/** Hitsplats dealt by someone else carry this added to their type (Void's HitSplat). */
	private const val OTHERS_OFFSET: Int = 14

	/** How long a spec drop rises, in client cycles (RuneLite's 100). */
	const val DROP_CYCLES: Int = 100

	@JvmField var enabled: Boolean = true
	@JvmField var thresholdNotification: Boolean = false
	@JvmField var specDrops: Boolean = true
	@JvmField var specDropMisses: Boolean = false
	@JvmField var specDropColor: Int = WHITE
	@JvmField var infobox: Boolean = true
	@JvmField var defenceDrainInfobox: Boolean = true
	@JvmField val thresholds: IntArray = IntArray(WEAPONS.size)

	/** RuneLite's SpecialCounter infobox: a weapon's hits or damage, green or red against its threshold. */
	class SpecCounter(@JvmField val weapon: Weapon, @JvmField val index: Int, hit: Int) : Counter(weapon.name, hit) {
		init {
			itemId = weapon.itemIds[0]
		}

		override fun tooltip(): String {
			return if (weapon.damage) {
				weapon.name + " special has hit " + count + " total."
			} else {
				weapon.name + " special has hit " + count + (if (count == 1) " time." else " times.")
			}
		}

		override fun textColour(): Int {
			val threshold: Int = thresholds[index]
			return if (threshold <= 0) WHITE else if (count >= threshold) GREEN else RED
		}
	}

	/** RuneLite's PlayerInfoDrop: the hit over your head, from [startCycle] for DROP_CYCLES. */
	class Drop(@JvmField val text: String, @JvmField val itemId: Int, @JvmField val startCycle: Int, @JvmField val miss: Boolean)

	@JvmField val counters: Array<SpecCounter?> = arrayOfNulls(WEAPONS.size)
	@JvmField val drops: ArrayList<Drop> = ArrayList()

	/** The weapon of the special waiting for its hitsplat, -1 for none, and the tick it is due by. */
	@JvmField var pendingWeapon: Int = -1
	@JvmField var pendingTarget: Int = -1
	@JvmField var hitsplatTick: Int = -1
	@JvmField var energy: Int = -1

	/** The npcs the counts are for: a special on another starts them again. */
	@JvmField val interacted: HashSet<Int> = HashSet()

	/** For the smoke test and the tests: the notification last sent. */
	@JvmField var lastNotification: String? = null

	private var context: PluginContext? = null

	private val point: IntArray = IntArray(2)

	/** RuneLite's PlayerInfoDropOverlay, under the interfaces. */
	private val overlay: Overlay = Overlay { g, view -> context?.client()?.let { drawDrops(g, view, it) } }

	private val varpListener: EventListener<VarpChanged> = EventListener { event ->
		if (event.id == SPECIAL_VARP) {
			context?.client()?.let { energyChanged(it, event.value) }
		}
	}

	private val hitsplatListener: EventListener<HitsplatApplied> = EventListener { event -> context?.client()?.let { hitsplat(it, event) } }

	private val tickListener: EventListener<GameTick> = EventListener { event -> tick(event.serverTick / CYCLES_PER_TICK) }

	private val npcDespawnListener: EventListener<Npc> = EventListener { npc ->
		if (npc.isDying() && interacted.contains(npc.index())) {
			removeCounters()
		}
	}

	private val stateListener: EventListener<GameStateChanged> = EventListener { event ->
		if (event.state === GameState.LOGIN_SCREEN) {
			removeCounters()
			energy = -1
		}
	}

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		enabled = !enabled
		context?.config()?.setBoolean("enabled", enabled)
		context?.config()?.save()
		if (!enabled) {
			removeCounters()
		}
		context?.client()?.consolePrint(if (enabled) "Special attack counter on." else "Special attack counter off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Special Attack Counter", true)
		// RuneLite's SpecialCounterConfig, in its order, the thresholds of the weapons 634 has
		s.toggle("thresholdNotification", "Threshold notifications", false)
		s.toggle("specDrops", "Spec drops", true)
		s.toggle("specDropMisses", "Spec drop misses", false)
		s.colour("specDropColor", "Spec drop color", WHITE, false)
		s.toggle("infobox", "Infobox", true)
		s.toggle("defenceDrainInfobox", "Show defence drain infobox", true)
		s.number("darklightThreshold", "Darklight", 0, 0, 100, 1)
		s.number("bandosGodswordThreshold", "Bandos godsword", 0, 0, 1000, 1)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.VARP_CHANGED, varpListener)
		context.events().subscribe(Events.HITSPLAT_APPLIED, hitsplatListener)
		context.events().subscribe(Events.GAME_TICK, tickListener)
		context.events().subscribe(Events.NPC_DESPAWNED, npcDespawnListener)
		context.events().subscribe(Events.GAME_STATE_CHANGED, stateListener)
		context.addOverlay(overlay)
		context.registerCommand("speccounter", toggle)
	}

	override fun stop() {
		removeCounters()
		drops.clear()
		pendingWeapon = -1
		energy = -1
		context = null
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		thresholdNotification = config.getBoolean("thresholdNotification", false)
		specDrops = config.getBoolean("specDrops", true)
		specDropMisses = config.getBoolean("specDropMisses", false)
		specDropColor = config.getColour("specDropColor", WHITE)
		infobox = config.getBoolean("infobox", true)
		defenceDrainInfobox = config.getBoolean("defenceDrainInfobox", true)
		for (i in WEAPONS.indices) {
			val key: String? = WEAPONS[i].thresholdKey
			thresholds[i] = if (key == null) 0 else config.getInt(key, 0)
		}
	}

	/** The counted weapon worn now, its index in WEAPONS, or -1. */
	@JvmStatic
	fun wornWeapon(client: ClientApi): Int {
		val worn: ItemContainer = client.itemContainer(WORN) ?: return -1
		if (worn.size() <= WEAPON_SLOT) {
			return -1
		}
		val id: Int = worn.itemId(WEAPON_SLOT)
		for (i in WEAPONS.indices) {
			if (id in WEAPONS[i].itemIds) {
				return i
			}
		}
		return -1
	}

	/** RuneLite's onVarbitChanged(SA_ENERGY): a drop with a counted weapon worn waits for its hitsplat. */
	@JvmStatic
	fun energyChanged(client: ClientApi, value: Int) {
		val before: Int = energy
		energy = value
		if (!enabled || before == -1 || value >= before) {
			return
		}
		val weapon: Int = wornWeapon(client)
		if (weapon < 0) {
			return
		}
		val player: Player = client.localPlayer() ?: return
		val target: Actor? = player.interacting()
		val tick: Int = client.serverTick() / CYCLES_PER_TICK
		pendingWeapon = weapon
		pendingTarget = (target as? Npc)?.index() ?: -1
		val distance: Int = if (target == null) 1 else maxOf(abs(target.tileX() - player.tileX()), abs(target.tileY() - player.tileY()))
		hitsplatTick = tick + WEAPONS[weapon].hitDelay(distance)
		if (pendingTarget >= 0 && !interacted.contains(pendingTarget)) {
			// a new npc: the counts start again
			removeCounters()
			interacted.add(pendingTarget)
		}
	}

	/** RuneLite's onHitsplatApplied: the first hitsplat of yours on the target (any npc when it was not known) is the special's. */
	@JvmStatic
	fun hitsplat(client: ClientApi, event: HitsplatApplied) {
		if (pendingWeapon < 0 || event.type >= OTHERS_OFFSET) {
			return
		}
		val npc: Npc = event.actor as? Npc ?: return
		if (pendingTarget >= 0 && npc.index() != pendingTarget) {
			return
		}
		if (pendingTarget < 0) {
			pendingTarget = npc.index()
			if (!interacted.contains(pendingTarget)) {
				removeCounters()
				interacted.add(pendingTarget)
			}
		}
		val weapon: Int = pendingWeapon
		pendingWeapon = -1
		specialAttackHit(client, weapon, if (event.type == MISS) 0 else event.damage)
	}

	/** RuneLite's onGameTick: a special whose hitsplat never came is dropped. */
	@JvmStatic
	fun tick(tick: Int) {
		if (pendingWeapon >= 0 && tick > hitsplatTick + 1) {
			pendingWeapon = -1
		}
	}

	/** RuneLite's specialAttackHit: the count, the threshold's notification and the drop. */
	@JvmStatic
	fun specialAttackHit(client: ClientApi, weapon: Int, hit: Int) {
		val spec: Weapon = WEAPONS[weapon]
		val counterHit: Int = if (spec.damage) hit else minOf(hit, 1)
		if (infobox && counterHit > 0) {
			updateCounter(client, weapon, counterHit)
		}
		if (counterHit > 0 || specDropMisses) {
			drops.add(Drop(counterHit.toString(), spec.itemIds[0], client.clientCycle(), counterHit == 0))
		}
	}

	private fun updateCounter(client: ClientApi, weapon: Int, hit: Int) {
		var counter: SpecCounter? = counters[weapon]
		if (counter == null) {
			counter = SpecCounter(WEAPONS[weapon], weapon, hit)
			counters[weapon] = counter
			context?.addInfoBox(counter)
		} else {
			counter.count += hit
		}
		val threshold: Int = thresholds[weapon]
		if (thresholdNotification && threshold > 0 && counter.count >= threshold) {
			val text: String = WEAPONS[weapon].name + " special attack threshold reached!"
			lastNotification = text
			client.notify(text)
		}
	}

	@JvmStatic
	fun removeCounters() {
		interacted.clear()
		for (i in counters.indices) {
			val counter: SpecCounter = counters[i] ?: continue
			context?.removeInfoBox(counter)
			counters[i] = null
		}
	}

	private val icons: HashMap<Int, Image> = HashMap()

	/** The weapon's icon for the drops, once its model is loaded. */
	private fun icon(client: ClientApi, itemId: Int): Image? {
		val cached: Image? = icons[itemId]
		if (cached != null) {
			return cached
		}
		val image: Image = client.itemImage(itemId, 1) ?: return null
		icons[itemId] = image
		return image
	}

	/** RuneLite's PlayerInfoDropOverlay: each drop rising from 100 to 400 above the player's head and fading. */
	private fun drawDrops(g: Graphics, view: Viewport, client: ClientApi) {
		val cycle: Int = client.clientCycle()
		drops.removeAll { cycle - it.startCycle > DROP_CYCLES }
		val player: Player = client.localPlayer() ?: return
		if (!enabled || !specDrops || drops.isEmpty() || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		for (drop in drops) {
			val percent: Int = (cycle - drop.startCycle) * 100 / DROP_CYCLES
			val height: Int = player.overheadHeight() + DROP_START + (DROP_END - DROP_START) * percent / 100
			if (!client.projectActor(player, height, point)) {
				continue
			}
			val alpha: Int = 255 - 255 * percent / 100
			val x: Int = view.x + point[0]
			val y: Int = view.y + point[1]
			val colour: Int = if (drop.miss) MISS_COLOUR else specDropColor
			val image: Image? = icon(client, drop.itemId)
			if (image != null) {
				g.imageScaled(image, x - ICON_SIZE - 1, y - g.fontHeight() / 2 - ICON_SIZE / 2, ICON_SIZE, ICON_SIZE)
			}
			g.text(x + 1, y, drop.text, (alpha shl 24) or (colour and 0xFFFFFF), alpha shl 24)
		}
	}

	private const val CYCLES_PER_TICK: Int = 30
	private const val MISS: Int = 8
	private const val DROP_START: Int = 100
	private const val DROP_END: Int = 400
	private const val ICON_SIZE: Int = 24

	private const val WHITE: Int = -0x1
	private const val GREEN: Int = 0xFF00FF00.toInt()
	private const val RED: Int = 0xFFFF0000.toInt()
	private const val MISS_COLOUR: Int = 0xFF4080FF.toInt()
}
