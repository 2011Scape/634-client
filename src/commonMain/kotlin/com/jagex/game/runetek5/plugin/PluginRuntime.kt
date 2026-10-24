package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.ActorFilter
import com.jagex.game.runetek5.network.packet.StockMarketOffer
import com.jagex.game.runetek5.plugin.api.AnimationFilter
import com.jagex.game.runetek5.plugin.api.ProjectileFilter
import com.jagex.game.runetek5.plugin.api.events.LocChanged
import com.jagex.game.runetek5.client.input.keyboard.KeyRecordEntry
import com.jagex.game.runetek5.plugin.api.events.KeyInput
import com.jagex.game.runetek5.ChatLineWriter
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.ObjStackEntry
import com.jagex.game.runetek5.config.iftype.SubInterfaceType
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.ComponentType
import com.jagex.game.runetek5.core.datastruct.key.DequeIterator
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.client.features.PluginManagerScreen
import com.jagex.game.runetek5.client.input.keyboard.KeyRecord
import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import com.jagex.game.runetek5.Cursor
import com.jagex.game.runetek5.client.menu.ColoredMiniMenuEntry
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.menu.MiniMenuEntry
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.GrandExchangeOffer
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuEntry
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.AnimationChanged
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.ConsoleCommand
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.GrandExchangeOfferChanged
import com.jagex.game.runetek5.plugin.api.events.GroundItem
import com.jagex.game.runetek5.plugin.api.events.HitsplatApplied
import com.jagex.game.runetek5.plugin.api.events.InterfaceChanged
import com.jagex.game.runetek5.plugin.api.events.ItemContainerChanged
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.KeyReleased
import com.jagex.game.runetek5.plugin.api.events.KeyTyped
import com.jagex.game.runetek5.plugin.api.events.MenuBuilt
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.MenuOptionClicked
import com.jagex.game.runetek5.plugin.api.events.MinimapDraw
import com.jagex.game.runetek5.plugin.api.events.MouseClicked
import com.jagex.game.runetek5.plugin.api.events.MouseWheel
import com.jagex.game.runetek5.plugin.api.events.OverheadTextChanged
import com.jagex.game.runetek5.plugin.api.events.ProjectileSpawned
import com.jagex.game.runetek5.plugin.api.events.ServerTick
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import com.jagex.game.runetek5.plugin.api.events.SpotAnimSpawned
import com.jagex.game.runetek5.plugin.api.events.StatChanged
import com.jagex.game.runetek5.plugin.api.events.ToolkitChanged
import com.jagex.game.runetek5.plugin.api.events.TopInterfaceChanged
import com.jagex.game.runetek5.plugin.api.events.VarpChanged
import com.jagex.game.runetek5.plugin.api.events.ViewportDraw
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.Runnable
import withLock

/**
 * Not part of the original client: the plugins' runtime (docs/PLUGINS.md). The game calls it from a
 * handful of places (the hooks docs/FEATURES.md lists, and a few more), and it turns each into an
 * event on the [bus], runs the overlays, commands and menu actions the plugins registered, and
 * starts and stops the plugins themselves. A plugin that throws anywhere is disabled on the spot,
 * with a line in the console and the chat, and the game goes on without it.
 *
 * The plugins start at loading stage 15 (LoadingStageWork), once the cache's types, the interfaces
 * and the player's variables exist. Everything here runs on the game thread.
 */
@Suppress("TooGenericExceptionCaught") // whatever a plugin throws, it is disabled and the game goes on: that is the point
object PluginRuntime {

	/** The built-ins alone are past 64; room for as many again from jars. */
	private const val MAX_PLUGINS: Int = 128

	private const val MAX_OVERLAYS: Int = 128

	private const val MAX_COMMANDS: Int = 128

	private const val MAX_MENU_ACTIONS: Int = MiniMenuAction.PLUGIN_LAST - MiniMenuAction.PLUGIN_FIRST + 1

	/** The most entries a menu holds (MiniMenu.addEntry). */
	private const val MAX_MENU_ENTRIES: Int = 500

	/**
	 * Console commands the client answers itself, so that a plugin from a jar learns at registration
	 * that the name is taken: the client's own commands run first whatever a plugin registered
	 * (ClientCommandProcessor), so a name missing here is never reached rather than taken over.
	 */
	private val RESERVED_COMMANDS: Array<String> = arrayOf("plugins", "plugin", "commands", "help", "cls", "displayfps", "renderer", "heap", "tk0", "tk1", "tk2", "tk3", "tk5", "tk6", "xpdrops", "shiftdrop", "msaa", "smaa", "renderscale", "hd", "hdsettings", "pluginmanager", "printfps", "occlude", "fpson", "fpsoff", "fps", "systemmem", "rebuild", "zoom", "errortest", "nativememerror", "heapdump", "setoutput", "closeoutput", "runscript", "resetanchors")

	@JvmField
	val bus: EventBusImpl = EventBusImpl()

	@JvmField
	val registrations: Array<PluginRegistration?> = arrayOfNulls<PluginRegistration?>(MAX_PLUGINS)

	@JvmField
	var count: Int = 0

	@JvmField
	var started: Boolean = false

	/** Where the plugins from outside the client came from, for `::plugins`; set by the platform. */
	@JvmField
	var sourceDescription: String? = null

	/** The toolkit the last ToolkitChanged named, -1 before the first. */
	@JvmField
	var toolkitId: Int = -1

	private val viewport: Viewport = Viewport()

	private val viewportDrawEvent: ViewportDraw = ViewportDraw(ToolkitGraphics, viewport)

	private val frameDrawEvent: FrameDraw = FrameDraw(ToolkitGraphics)

	private val minimapDrawEvent: MinimapDraw = MinimapDraw(ToolkitGraphics)

	/** Whether the minimap's map (not its black loading face) was drawn since the last MinimapDraw. */
	private var minimapPending: Boolean = false

	private val clientTickEvent: ClientTick = ClientTick()

	private val gameTickEvent: GameTick = GameTick()

	private val keyPressedEvent: KeyPressed = KeyPressed()

	private val mouseClickedEvent: MouseClicked = MouseClicked()

	private val keyTypedEvent: KeyTyped = KeyTyped()

	private val keyReleasedEvent: KeyReleased = KeyReleased()

	private val mouseWheelEvent: MouseWheel = MouseWheel()

	/** Where the game view was last drawn on the screen, for drawing in its space (ClientApi.locOutline). */
	@JvmStatic
	fun viewX(): Int = viewport.x

	@JvmStatic
	fun viewY(): Int = viewport.y

	@JvmStatic
	fun viewWidth(): Int = viewport.width

	/** The scene tile under the mouse this cycle, -1 for none (ViewportMiniMenuPicking). */
	@JvmField
	var hoveredTileX: Int = -1

	@JvmField
	var hoveredTileY: Int = -1

	/** Where the minimap was last drawn, and the client cycle it was. */
	@JvmField
	var minimapX: Int = 0

	@JvmField
	var minimapY: Int = 0

	@JvmField
	var minimapWidth: Int = 0

	@JvmField
	var minimapHeight: Int = 0

	/** The frame (SceneDrawer.timesSceneRendered) the minimap was last drawn in. */
	@JvmField
	var minimapFrame: Int = -100

	/** A plugin's piece of work: whose, what, and (for the scheduled ones) the game tick it is due. */
	private class PluginTask(@JvmField val owner: PluginRegistration, @JvmField val task: Task, @JvmField val due: Int)

	/** Work for the game thread from anywhere: taken under [taskLock], run at the next client cycle. */
	private val taskLock: Any = Any()

	private var pendingTasks: ArrayList<PluginTask> = ArrayList()

	private var runningTasks: ArrayList<PluginTask> = ArrayList()

	/** Work for a later game tick, game thread only; [gameCycles] counts the cycles logged in, 30 to a tick of the server. */
	private val scheduledTasks: ArrayList<PluginTask> = ArrayList()

	private const val CYCLES_PER_TICK: Int = 30

	private var gameCycles: Int = 0

	private val menuEntryAddedEvent: MenuEntryAdded = MenuEntryAdded()

	private val menuOptionClickedEvent: MenuOptionClicked = MenuOptionClicked()

	private val consoleCommandEvent: ConsoleCommand = ConsoleCommand()

	private val menuBuiltEvent: MenuBuilt = MenuBuilt()

	/** Whether a MenuBuilt listener is running: the only time the menu may be read and edited. */
	private var menuEditing: Boolean = false

	/** The menu's entries from the top while a MenuBuilt listener runs, so that reading entry i costs nothing. */
	private val menuEntries: Array<MiniMenuEntry?> = arrayOfNulls<MiniMenuEntry?>(MAX_MENU_ENTRIES)

	private var menuEntryTotal: Int = 0

	/** Whether a MenuBuilt listener took an entry out or moved one. */
	private var menuEdited: Boolean = false

	/** An entry a plugin promoted that cannot be the top row (a component's options 6 to 10): the left click runs it all the same. */
	private var promotedDefault: MiniMenuEntry? = null

	/** The root interface the last tick saw, for TopInterfaceChanged from whoever sets it. */
	private var lastTopInterface: Int = -1

	private var lastState: GameState = GameState.LOADING

	private val overlays: Array<Overlay?> = arrayOfNulls<Overlay?>(MAX_OVERLAYS)

	private val overlayOwners: Array<PluginRegistration?> = arrayOfNulls<PluginRegistration?>(MAX_OVERLAYS)

	private var overlayCount: Int = 0

	private var overlaysDirty: Boolean = false

	private var drawingOverlays: Boolean = false

	private val commandNames: Array<String?> = arrayOfNulls<String?>(MAX_COMMANDS)

	private val commandHandlers: Array<CommandHandler?> = arrayOfNulls<CommandHandler?>(MAX_COMMANDS)

	private val commandOwners: Array<PluginRegistration?> = arrayOfNulls<PluginRegistration?>(MAX_COMMANDS)

	private var commandCount: Int = 0

	private val menuActions: Array<MenuAction?> = arrayOfNulls<MenuAction?>(MAX_MENU_ACTIONS)

	private val menuActionOwners: Array<PluginRegistration?> = arrayOfNulls<PluginRegistration?>(MAX_MENU_ACTIONS)

	/**
	 * How many times each click-type slot has been given back: an entry carries the number of the
	 * tenant that made it (menuParam0), so that a click on an entry built before its plugin stopped
	 * does not go to whichever plugin has the slot now.
	 */
	private val menuActionGenerations: IntArray = IntArray(MAX_MENU_ACTIONS)

	private const val GENERATION_SHIFT: Int = 19

	private const val GENERATION_MASK: Int = 0xFFF

	private const val KIND_SHIFT: Int = 16

	private const val KIND_MASK: Int = 7

	/** The game's "below" flag on a click type (MiniMenuActionExecutor takes it off). */
	private const val DEPRIORITIZED: Int = 2000

	private const val MAX_SUBMENUS: Int = 32

	private const val MAX_SUBMENU_ENTRIES: Int = 40

	/** The plugins' submenus in the menu being built, by index, and the entries that open them. */
	private val submenus: Array<ColoredMiniMenuEntry?> = arrayOfNulls<ColoredMiniMenuEntry?>(MAX_SUBMENUS)

	private val submenuParents: Array<MiniMenuEntry?> = arrayOfNulls<MiniMenuEntry?>(MAX_SUBMENUS)

	private var submenuCount: Int = 0

	/** The menu entry target being built, while a MenuEntryAdded listener runs. */
	private var menuBuilding: Boolean = false

	private var menuKind: Int = 0

	/** The low 16 bits of a plugin entry's param0: the tile's x, or a component's slot. */
	private var menuParam0Low: Int = 0

	/** A plugin entry's param1: the tile's y, or a component's id. */
	private var menuParam1: Int = 0

	private var menuEntityUid: Long = 0L

	private var menuItemId: Int = -1

	private val resetGraphics: Runnable = Runnable { ToolkitGraphics.reset() }

	/* The clicked entry's texts, for apiOneMenuClick. */
	private var clickedOption: String? = null

	private var clickedTarget: String? = null

	private var clickedTargetName: String? = null

	/**
	 * API 1 handed MenuOptionClicked the coloured target as its option and the player's name (null for
	 * anything else) as its target; a plugin that declares API 1 was written to that, and keeps it.
	 */
	private val apiOneMenuClick: EventBusImpl.ListenerHook = EventBusImpl.ListenerHook { owner ->
		if (owner != null && owner.descriptor.apiVersion <= 1) {
			menuOptionClickedEvent.option = clickedTarget
			menuOptionClickedEvent.target = clickedTargetName
		} else {
			menuOptionClickedEvent.option = clickedOption
			menuOptionClickedEvent.target = clickedTarget
		}
	}

	init {
		bus.onFailure = EventBusImpl.FailureHandler { owner, error -> fail(owner, error) }
	}

	/** Loads the built-in plugins and the platform's, and starts the ones that are on. */
	@JvmStatic
	fun start() {
		if (started) {
			return
		}
		started = true
		lastState = ClientApiImpl.stateOf(ClientState.mainLogicStep)
		lastTopInterface = ClientState.BASE_WINDOW_ID
		PluginSettings.load()
		val builtIns: Array<PluginRegistration> = BuiltinPlugins.all()
		for (i in 0 until builtIns.size) {
			add(builtIns[i])
		}
		var found: Array<PluginRegistration> = arrayOf()
		try {
			found = discoverPlugins()
		} catch (error: Throwable) {
			lang.printlnErr("Plugins: cannot look for plugins: " + error)
			lang.printlnErr(error.stackTraceToString())
		}
		for (i in 0 until found.size) {
			add(found[i])
		}
		for (i in 0 until count) {
			val registration: PluginRegistration = registrations[i]!!
			if (registration.state == PluginRegistration.FAILED || !registration.loadable()) {
				continue
			}
			if (registration.builtIn || PluginSettings.isEnabled(registration.descriptor.id)) {
				startPlugin(registration)
			} else {
				registration.state = PluginRegistration.STOPPED
			}
		}
		if (found.size > 0) {
			var on: Int = 0
			for (i in 0 until found.size) {
				if (found[i].state == PluginRegistration.STARTED) {
					on++
				}
			}
			lang.printlnErr("Plugins: " + found.size + " found" + (if (sourceDescription != null) " in " + sourceDescription else "") + ", " + on + " on (::plugins lists them)")
		}
	}

	/** Stops every running plugin, when the client quits. */
	@JvmStatic
	fun stop() {
		for (i in 0 until count) {
			val registration: PluginRegistration = registrations[i]!!
			if (registration.context != null) {
				stopPlugin(registration)
			}
		}
	}

	/** The plugin with [id], or null. */
	@JvmStatic
	fun find(id: String): PluginRegistration? {
		for (i in 0 until count) {
			if (registrations[i]!!.descriptor.id.equals(id, ignoreCase = true)) {
				return registrations[i]
			}
		}
		return null
	}

	private fun add(registration: PluginRegistration) {
		if (count == MAX_PLUGINS) {
			registration.state = PluginRegistration.FAILED
			registration.error = "too many plugins (the most is " + MAX_PLUGINS + ")"
			lang.printlnErr("Plugin " + registration.descriptor + " (" + registration.source + "): " + registration.error)
			return
		}
		if (registration.state != PluginRegistration.FAILED) {
			val existing: PluginRegistration? = find(registration.descriptor.id)
			if (existing != null) {
				registration.state = PluginRegistration.FAILED
				registration.error = "another plugin has the id " + registration.descriptor.id + " (" + existing.source + ")"
			}
		}
		if (registration.state == PluginRegistration.FAILED) {
			lang.printlnErr("Plugin " + registration.descriptor + " (" + registration.source + ") not loaded: " + registration.error)
		}
		registrations[count++] = registration
	}

	private fun startPlugin(registration: PluginRegistration) {
		registration.error = null
		var plugin: Plugin? = registration.plugin
		if (plugin == null) {
			// a plugin from a jar is made when it is first turned on, so one that is off runs nothing
			val factory: PluginFactory = registration.factory ?: return
			try {
				plugin = factory.create()
			} catch (error: Throwable) {
				fail(registration, error)
				return
			}
			registration.plugin = plugin
		}
		val context: PluginContextImpl = PluginContextImpl(registration)
		registration.context = context
		try {
			plugin.start(context)
			registration.state = PluginRegistration.STARTED
		} catch (error: Throwable) {
			fail(registration, error)
		}
	}

	/** The plugins' actor and projectile filters (API 32), and whose. */
	private val actorFilters: ArrayList<ActorFilter> = ArrayList()
	private val actorFilterOwners: ArrayList<PluginRegistration> = ArrayList()
	private val projectileFilters: ArrayList<ProjectileFilter> = ArrayList()
	private val projectileFilterOwners: ArrayList<PluginRegistration> = ArrayList()

	@JvmStatic
	fun addActorFilter(registration: PluginRegistration, filter: ActorFilter) {
		actorFilters.add(filter)
		actorFilterOwners.add(registration)
	}

	@JvmStatic
	fun addProjectileFilter(registration: PluginRegistration, filter: ProjectileFilter) {
		projectileFilters.add(filter)
		projectileFilterOwners.add(registration)
	}

	/** The plugins' animation filters (API 34), one each, and whose. */
	private val animationFilters: ArrayList<AnimationFilter> = ArrayList()
	private val animationFilterOwners: ArrayList<PluginRegistration> = ArrayList()

	/** Rises whenever an animation filter is set or taken back: SeqType keeps its answer until it changes. */
	@JvmField
	var animationFilterGeneration: Int = 0

	@JvmStatic
	fun setAnimationFilter(registration: PluginRegistration, filter: AnimationFilter?) {
		removeAnimationFilter(registration)
		if (filter != null) {
			animationFilters.add(filter)
			animationFilterOwners.add(registration)
		}
		animationFilterGeneration++
	}

	private fun removeAnimationFilter(registration: PluginRegistration) {
		for (i in animationFilters.indices.reversed()) {
			if (animationFilterOwners[i] === registration) {
				animationFilters.removeAt(i)
				animationFilterOwners.removeAt(i)
				animationFilterGeneration++
			}
		}
	}

	/** Whether any plugin smooths the sequence [sequenceId]. */
	@JvmStatic
	fun smoothAnimation(sequenceId: Int): Boolean {
		for (i in animationFilters.indices) {
			try {
				if (animationFilters[i].shouldSmooth(sequenceId)) {
					return true
				}
			} catch (error: Throwable) {
				fail(animationFilterOwners[i], error)
				return false
			}
		}
		return false
	}

	/** Whether every plugin lets [actor] be drawn: its model, or with [drawingUI] its overheads and the like. */
	@JvmStatic
	fun drawActor(actor: Actor, drawingUI: Boolean): Boolean {
		for (i in actorFilters.indices) {
			try {
				if (!actorFilters[i].shouldDraw(actor, drawingUI)) {
					return false
				}
			} catch (error: Throwable) {
				fail(actorFilterOwners[i], error)
				return true
			}
		}
		return true
	}

	/** Whether every plugin lets the projectile with spot animation [graphicId] be drawn. */
	@JvmStatic
	fun drawProjectile(graphicId: Int): Boolean {
		for (i in projectileFilters.indices) {
			try {
				if (!projectileFilters[i].shouldDraw(graphicId)) {
					return false
				}
			} catch (error: Throwable) {
				fail(projectileFilterOwners[i], error)
				return true
			}
		}
		return true
	}

	private fun stopPlugin(registration: PluginRegistration) {
		val context: PluginContextImpl? = registration.context
		if (context != null && registration.plugin != null) {
			try {
				registration.plugin!!.stop()
			} catch (error: Throwable) {
				lang.printlnErr("Plugin " + registration.descriptor + " threw while stopping: " + error)
				lang.printlnErr(error.stackTraceToString())
			}
		}
		// whatever the plugin registers from now on, from a thread or a callback it kept, is refused
		context?.closed = true
		removeAll(registration)
		registration.context = null
		if (registration.state != PluginRegistration.FAILED) {
			registration.state = PluginRegistration.STOPPED
		}
	}

	/** Takes back everything [registration] registered, without stopping its plugin: for the tests. */
	@JvmStatic
	fun release(registration: PluginRegistration) {
		registration.context?.closed = true
		removeAll(registration)
		registration.context = null
	}

	/** Takes back everything [registration] registered. */
	private fun removeAll(registration: PluginRegistration) {
		bus.removeOwner(registration)
		withLock(taskLock) {
			pendingTasks.removeAll { it.owner === registration }
		}
		scheduledTasks.removeAll { it.owner === registration }
		OverlayLayout.removeAll(registration)
		InfoBoxes.removeAll(registration)
		for (i in actorFilters.indices.reversed()) {
			if (actorFilterOwners[i] === registration) {
				actorFilters.removeAt(i)
				actorFilterOwners.removeAt(i)
			}
		}
		removeAnimationFilter(registration)
		for (i in projectileFilters.indices.reversed()) {
			if (projectileFilterOwners[i] === registration) {
				projectileFilters.removeAt(i)
				projectileFilterOwners.removeAt(i)
			}
		}
		for (i in 0 until overlayCount) {
			if (overlayOwners[i] === registration) {
				overlays[i] = null
				overlayOwners[i] = null
				overlaysDirty = true
			}
		}
		if (!drawingOverlays) {
			compactOverlays()
		}
		var kept: Int = 0
		for (i in 0 until commandCount) {
			if (commandOwners[i] !== registration) {
				commandNames[kept] = commandNames[i]
				commandHandlers[kept] = commandHandlers[i]
				commandOwners[kept] = commandOwners[i]
				kept++
			}
		}
		for (i in kept until commandCount) {
			commandNames[i] = null
			commandHandlers[i] = null
			commandOwners[i] = null
		}
		commandCount = kept
		for (i in 0 until MAX_MENU_ACTIONS) {
			if (menuActionOwners[i] === registration) {
				menuActions[i] = null
				menuActionOwners[i] = null
				menuActionGenerations[i] = (menuActionGenerations[i] + 1) and GENERATION_MASK
			}
		}
	}

	/** [text] as a plain chat or console line: no markup a plugin's name or error could carry, and no line breaks. */
	private fun plain(text: String): String {
		val out: StringBuilder = StringBuilder(text.length)
		for (i in 0 until text.length) {
			val c: Char = text[i]
			if (c == '<') {
				out.append('(')
			} else if (c == '>') {
				out.append(')')
			} else if (c == '\n' || c == '\r') {
				out.append(' ')
			} else {
				out.append(c)
			}
		}
		return out.toString()
	}

	/** Disables [owner] after it threw [error]: its plugin is stopped and everything it registered removed. */
	@JvmStatic
	fun fail(owner: PluginRegistration?, error: Throwable) {
		if (owner == null) {
			lang.printlnErr("Plugins: a listener without a plugin threw " + error)
			lang.printlnErr(error.stackTraceToString())
			return
		}
		if (owner.state == PluginRegistration.FAILED && owner.context == null) {
			return
		}
		owner.state = PluginRegistration.FAILED
		owner.error = error.toString()
		val message: String = "Plugin " + owner.descriptor.name + " was disabled: " + error
		lang.printlnErr(message)
		lang.printlnErr(error.stackTraceToString())
		stopPlugin(owner)
		val line: String = plain(message)
		Console.printToConsole(line)
		if (ClientState.mainLogicStep === MainLogicStep.STEP_GAME_SCREEN) {
			ChatLineWriter.writeUnfilterableMessage(line)
		}
	}

	fun addOverlay(owner: PluginRegistration, overlay: Overlay) {
		if (overlay is PanelOverlay) {
			OverlayLayout.add(owner, overlay)
			return
		}
		if (overlayCount == MAX_OVERLAYS) {
			lang.printlnErr("Plugin " + owner.descriptor + ": no room for another overlay (the most is " + MAX_OVERLAYS + ")")
			return
		}
		overlays[overlayCount] = overlay
		overlayOwners[overlayCount] = owner
		overlayCount++
	}

	fun removeOverlay(overlay: Overlay) {
		if (overlay is PanelOverlay) {
			OverlayLayout.remove(overlay)
			return
		}
		for (i in 0 until overlayCount) {
			if (overlays[i] === overlay) {
				overlays[i] = null
				overlayOwners[i] = null
				overlaysDirty = true
			}
		}
		if (!drawingOverlays) {
			compactOverlays()
		}
	}

	private fun compactOverlays() {
		if (!overlaysDirty) {
			return
		}
		overlaysDirty = false
		var kept: Int = 0
		for (i in 0 until overlayCount) {
			if (overlays[i] != null) {
				overlays[kept] = overlays[i]
				overlayOwners[kept] = overlayOwners[i]
				kept++
			}
		}
		for (i in kept until overlayCount) {
			overlays[i] = null
			overlayOwners[i] = null
		}
		overlayCount = kept
	}

	private fun findCommand(name: String): Int {
		for (i in 0 until commandCount) {
			if (commandNames[i] == name) {
				return i
			}
		}
		return -1
	}

	fun registerCommand(owner: PluginRegistration, name: String, handler: CommandHandler): Boolean {
		val key: String = name.trim().lowercase()
		if (key.isEmpty() || key.indexOf(' ') >= 0 || commandCount == MAX_COMMANDS || findCommand(key) >= 0) {
			return false
		}
		if (!owner.builtIn) {
			for (i in 0 until RESERVED_COMMANDS.size) {
				if (RESERVED_COMMANDS[i] == key) {
					return false
				}
			}
		}
		commandNames[commandCount] = key
		commandHandlers[commandCount] = handler
		commandOwners[commandCount] = owner
		commandCount++
		return true
	}

	/** [task] for the game thread at the next client cycle; from any thread. */
	fun invokeLater(owner: PluginRegistration, task: Task) {
		val entry: PluginTask = PluginTask(owner, task, 0)
		withLock(taskLock) {
			pendingTasks.add(entry)
		}
	}

	/** [task] for the game thread [ticks] game ticks of 600 ms from now (0: the next cycle). */
	fun schedule(owner: PluginRegistration, ticks: Int, task: Task) {
		scheduledTasks.add(PluginTask(owner, task, gameCycles + (if (ticks < 0) 0 else ticks) * CYCLES_PER_TICK + 1))
	}

	/** [task] on a thread of its own; what it throws disables [owner], on the game thread. */
	fun startThread(owner: PluginRegistration, name: String, task: Task) {
		val thread: lang.Thread = lang.Thread(
			lang.Runnable {
				try {
					task.run()
				} catch (error: Throwable) {
					invokeLater(owner, Task { fail(owner, error) })
				}
			},
			"plugin-" + owner.descriptor.id + "-" + name,
		)
		thread.setDaemon(true)
		thread.start()
	}

	private fun runPendingTasks() {
		withLock(taskLock) {
			if (pendingTasks.isEmpty()) {
				return
			}
			// swap the lists, so that a task may queue another without holding the lock
			val tasks: ArrayList<PluginTask> = pendingTasks
			pendingTasks = runningTasks
			runningTasks = tasks
		}
		for (i in 0 until runningTasks.size) {
			val entry: PluginTask = runningTasks[i]
			if (entry.owner.context == null) {
				continue // stopped since
			}
			try {
				entry.task.run()
			} catch (error: Throwable) {
				fail(entry.owner, error)
			}
		}
		runningTasks.clear()
	}

	private fun runScheduledTasks() {
		gameCycles++
		var i: Int = 0
		while (i < scheduledTasks.size) {
			if (scheduledTasks[i].due > gameCycles) {
				i++
			} else {
				val entry: PluginTask = scheduledTasks.removeAt(i)
				if (entry.owner.context != null) {
					try {
						entry.task.run()
					} catch (error: Throwable) {
						fail(entry.owner, error)
					}
				}
			}
		}
	}

	fun registerMenuAction(owner: PluginRegistration, action: MenuAction): Int {
		for (i in 0 until MAX_MENU_ACTIONS) {
			if (menuActions[i] == null) {
				menuActions[i] = action
				menuActionOwners[i] = owner
				return MiniMenuAction.PLUGIN_FIRST + i
			}
		}
		return -1
	}

	/* The hooks: each turns one moment in the game into an event. */

	@JvmStatic
	fun clientTick() {
		if (!started) {
			return
		}
		if (ClientState.BASE_WINDOW_ID != lastTopInterface) {
			// login, the lobby and the logout set the root interface without a packet
			topInterfaceChanged(lastTopInterface, ClientState.BASE_WINDOW_ID)
		}
		hoveredTileX = -1
		hoveredTileY = -1
		ToolkitGraphics.cycle()
		ItemPrices.poll()
		runPendingTasks()
		clientTickEvent.cycle = ClientState.clientCycle
		bus.post(Events.CLIENT_TICK, clientTickEvent)
	}

	@JvmStatic
	fun gameTick() {
		if (!started) {
			return
		}
		runScheduledTasks()
		gameTickEvent.cycle = ClientState.clientCycle
		gameTickEvent.serverTick = ClientState.serverTick
		bus.post(Events.GAME_TICK, gameTickEvent)
	}

	/** After the scene and the client's overlays are drawn into the game view at ([areaX], [areaY]). */
	@JvmStatic
	fun drawViewport(areaX: Int, areaY: Int, width: Int, height: Int) {
		if (!started) {
			return
		}
		viewport.x = areaX
		viewport.y = areaY
		viewport.width = width
		viewport.height = height
		bus.afterListener = resetGraphics
		bus.post(Events.VIEWPORT_DRAW, viewportDrawEvent)
		bus.afterListener = null
		drawingOverlays = true
		var i: Int = 0
		while (i < overlayCount) {
			val overlay: Overlay? = overlays[i]
			if (overlay != null) {
				try {
					overlay.draw(ToolkitGraphics, viewport)
				} catch (error: Throwable) {
					fail(overlayOwners[i], error)
				}
				ToolkitGraphics.reset()
			}
			i++
		}
		// the panels after the overlays that draw where they like, as RuneLite's dynamic overlays come first
		InfoBoxes.cull()
		OverlayLayout.draw(viewport)
		drawingOverlays = false
		compactOverlays()
	}

	/** After the whole frame is drawn, before it is shown. */
	@JvmStatic
	fun drawFrame(width: Int, height: Int) {
		if (!started) {
			return
		}
		ClientState.activeToolkit?.beginOverlayFrame()
		// the tooltips over everything, the frame's interfaces included
		Tooltips.draw(width, height)
		if (!bus.hasListeners(Events.FRAME_DRAW)) {
			return
		}
		frameDrawEvent.width = width
		frameDrawEvent.height = height
		bus.afterListener = resetGraphics
		bus.post(Events.FRAME_DRAW, frameDrawEvent)
		bus.afterListener = null
	}

	@JvmStatic
	fun statChanged(skill: Int, oldXp: Int, newXp: Int, level: Int) {
		if (!started || !bus.hasListeners(Events.STAT_CHANGED)) {
			return
		}
		bus.post(Events.STAT_CHANGED, StatChanged(skill, oldXp, newXp, level))
	}

	@JvmStatic
	fun varpChanged(id: Int, value: Int) {
		if (!started || !bus.hasListeners(Events.VARP_CHANGED)) {
			return
		}
		bus.post(Events.VARP_CHANGED, VarpChanged(id, value))
	}

	@JvmStatic
	fun chatMessage(type: Int, name: String?, message: String?, clan: String?): String? {
		return chatMessageEvent(type, name, message, clan)?.message ?: message
	}

	/** The plugins' ChatMessage for a line about to be stored; null when no plugin listens. */
	@JvmStatic
	fun chatMessageEvent(type: Int, name: String?, message: String?, clan: String?): ChatMessage? {
		if (!started || !bus.hasListeners(Events.CHAT_MESSAGE)) {
			return null
		}
		val event: ChatMessage = ChatMessage(type, name, message, clan)
		bus.post(Events.CHAT_MESSAGE, event)
		return event
	}

	@JvmStatic
	fun containerChanged(containerId: Int) {
		if (!started || !bus.hasListeners(Events.ITEM_CONTAINER_CHANGED)) {
			return
		}
		bus.post(Events.ITEM_CONTAINER_CHANGED, ItemContainerChanged(containerId))
	}

	/** After the main logic step changed. */
	@JvmStatic
	fun stepChanged() {
		if (!started) {
			return
		}
		val state: GameState = ClientApiImpl.stateOf(ClientState.mainLogicStep)
		if (state === lastState) {
			return
		}
		val previous: GameState = lastState
		lastState = state
		bus.post(Events.GAME_STATE_CHANGED, GameStateChanged(previous, state))
	}

	/** After a toolkit loaded its fonts and sprites. */
	@JvmStatic
	fun toolkitChanged(toolkitId: Int) {
		this.toolkitId = toolkitId
		ToolkitGraphics.toolkitChanged()
		if (!started) {
			return
		}
		bus.post(Events.TOOLKIT_CHANGED, ToolkitChanged(toolkitId))
	}

	/** A key went down; true when a plugin consumed it, and the client must not send it to the server. */
	@JvmStatic
	fun keyPressed(record: KeyRecord): Boolean {
		if (!started || !bus.hasListeners(Events.KEY_PRESSED)) {
			return false
		}
		keyPressedEvent.keyCode = record.getKeyCode()
		keyPressedEvent.keyChar = record.getKeyChar()
		keyPressedEvent.modifiers = record.getModifierFlags()
		keyPressedEvent.consumed = false
		bus.post(Events.KEY_PRESSED, keyPressedEvent)
		return keyPressedEvent.consumed
	}

	/** A character for the interfaces (a held key repeats it); true when a plugin consumed it. Not while the developer console is open: what is typed there is the player's. */
	@JvmStatic
	fun keyTyped(record: KeyRecord): Boolean {
		if (!started || Console.isOpen() || !bus.hasListeners(Events.KEY_TYPED)) {
			return false
		}
		keyTypedEvent.keyCode = record.getKeyCode()
		keyTypedEvent.keyChar = record.getKeyChar()
		keyTypedEvent.modifiers = record.getModifierFlags()
		keyTypedEvent.consumed = false
		bus.post(Events.KEY_TYPED, keyTypedEvent)
		return keyTypedEvent.consumed
	}

	private val keyInputEvent: KeyInput = KeyInput()

	/**
	 * A key as the keyboard gave it, before the client reads it ([Events.KEY_INPUT]): what the listeners
	 * left is written back into [entry] (a press or release's code, a character); true when one dropped it.
	 */
	@JvmStatic
	fun keyInput(entry: KeyRecordEntry): Boolean {
		// not while the developer console or a plugin manager box has the keyboard: what is typed there is the player's
		if (!started || !bus.hasListeners(Events.KEY_INPUT) || Console.isOpen() || PluginManagerScreen.hasKeyboard()) {
			return false
		}
		val type: Int = entry.type
		if (type != KeyInput.PRESSED && type != KeyInput.RELEASED && type != KeyInput.TYPED) {
			return false
		}
		keyInputEvent.type = type
		keyInputEvent.keyCode = entry.keyCode
		keyInputEvent.keyChar = entry.keyCharacter
		keyInputEvent.modifiers = entry.modifierFlags
		keyInputEvent.consumed = false
		bus.post(Events.KEY_INPUT, keyInputEvent)
		if (keyInputEvent.consumed) {
			return true
		}
		if (type == KeyInput.TYPED) {
			entry.keyCharacter = keyInputEvent.keyChar
		} else if (keyInputEvent.keyCode in 1 until MAX_KEY_CODE) {
			entry.keyCode = keyInputEvent.keyCode
		}
		return false
	}

	/** The key codes the client holds (CanvasKeyRecorder.keysHeld). */
	private const val MAX_KEY_CODE: Int = 112

	@JvmStatic
	fun keyReleased(record: KeyRecord) {
		if (!started || !bus.hasListeners(Events.KEY_RELEASED)) {
			return
		}
		keyReleasedEvent.keyCode = record.getKeyCode()
		keyReleasedEvent.modifiers = record.getModifierFlags()
		bus.post(Events.KEY_RELEASED, keyReleasedEvent)
	}

	/** A mouse button went down; true when a plugin consumed it, and the client must not act on it. */
	@JvmStatic
	fun mouseClicked(record: MouseRecord, clickType: Int): Boolean {
		if (!started || !bus.hasListeners(Events.MOUSE_CLICKED)) {
			return false
		}
		mouseClickedEvent.x = record.getMouseX()
		mouseClickedEvent.y = record.getMouseY()
		mouseClickedEvent.button = clickType
		mouseClickedEvent.consumed = false
		bus.post(Events.MOUSE_CLICKED, mouseClickedEvent)
		return mouseClickedEvent.consumed
	}

	/** The wheel turned by [rotation]; true when a plugin consumed it. */
	@JvmStatic
	fun mouseWheel(rotation: Int): Boolean {
		if (!started || !bus.hasListeners(Events.MOUSE_WHEEL)) {
			return false
		}
		mouseWheelEvent.rotation = rotation
		mouseWheelEvent.consumed = false
		bus.post(Events.MOUSE_WHEEL, mouseWheelEvent)
		return mouseWheelEvent.consumed
	}

	/** The ground under the mouse this cycle is scene tile ([tileX], [tileY]). */
	@JvmStatic
	fun hoveredTile(tileX: Int, tileY: Int) {
		hoveredTileX = tileX
		hoveredTileY = tileY
	}

	/**
	 * The minimap is being drawn at ([x], [y]), [width] by [height], inside the mask its sprite makes: on
	 * each row from its top, [rowStart] pixels in for [rowWidth] pixels (Component.getClippingMask).
	 */
	@JvmStatic
	fun minimapDrawn(x: Int, y: Int, width: Int, height: Int, rowStart: IntArray?, rowWidth: IntArray?) {
		minimapX = x
		minimapY = y
		minimapWidth = width
		minimapHeight = height
		minimapRowStart = rowStart
		minimapRowWidth = rowWidth
		minimapFrame = SceneDrawer.timesSceneRendered
		minimapPending = true
	}

	/** The minimap component was drawn: the plugins' MinimapDraw, while its box is the clip. Since API 38. */
	@JvmStatic
	fun drawMinimap() {
		if (!started || !minimapPending) {
			return
		}
		minimapPending = false
		if (!bus.hasListeners(Events.MINIMAP_DRAW)) {
			return
		}
		minimapDrawEvent.x = minimapX
		minimapDrawEvent.y = minimapY
		minimapDrawEvent.width = minimapWidth
		minimapDrawEvent.height = minimapHeight
		bus.afterListener = resetGraphics
		bus.post(Events.MINIMAP_DRAW, minimapDrawEvent)
		bus.afterListener = null
	}

	/** The minimap's mask rows, null for none (all of its box shows). */
	private var minimapRowStart: IntArray? = null
	private var minimapRowWidth: IntArray? = null

	/** Whether screen pixel ([x], [y]) is inside the minimap's mask: where the game draws the map, not its box's corners. Since API 38. */
	@JvmStatic
	fun onMinimap(x: Int, y: Int): Boolean {
		val start: IntArray = minimapRowStart ?: return true
		val width: IntArray = minimapRowWidth ?: return true
		val row: Int = y - minimapY
		if (row < 0 || row >= start.size || row >= width.size) {
			return false
		}
		val column: Int = x - minimapX
		return column >= start[row] && column < start[row] + width[row]
	}

	/** An npc entered the scene, with its type set. */
	@JvmStatic
	fun npcSpawned(npc: Npc?) {
		if (!started || npc == null || !bus.hasListeners(Events.NPC_SPAWNED)) {
			return
		}
		bus.post(Events.NPC_SPAWNED, npc)
	}

	/** An npc is about to leave the scene, its type still set. */
	@JvmStatic
	fun npcDespawned(npc: Npc?) {
		if (!started || npc == null || !bus.hasListeners(Events.NPC_DESPAWNED)) {
			return
		}
		bus.post(Events.NPC_DESPAWNED, npc)
	}

	/** A player entered the scene, placed and with its appearance. */
	@JvmStatic
	fun playerSpawned(player: Player?) {
		if (!started || player == null || !bus.hasListeners(Events.PLAYER_SPAWNED)) {
			return
		}
		bus.post(Events.PLAYER_SPAWNED, player)
	}

	/** A player is about to leave the scene. */
	@JvmStatic
	fun playerDespawned(player: Player?) {
		if (!started || player == null || !bus.hasListeners(Events.PLAYER_DESPAWNED)) {
			return
		}
		bus.post(Events.PLAYER_DESPAWNED, player)
	}

	/** An item was put on the ground at scene tile ([sceneX], [sceneY]); [ownership] is a GroundItem.OWNERSHIP_ value. */
	@JvmStatic
	fun groundItemSpawned(itemId: Int, amount: Int, sceneX: Int, sceneY: Int, level: Int, ownership: Int) {
		if (!started || !bus.hasListeners(Events.ITEM_SPAWNED)) {
			return
		}
		val item: GroundItem = GroundItem(itemId, amount, sceneX, sceneY, level)
		item.ownership = ownership
		bus.post(Events.ITEM_SPAWNED, item)
	}

	/** An item was put on the ground by the server with nothing said of whose it is. */
	@JvmStatic
	fun groundItemSpawned(itemId: Int, amount: Int, sceneX: Int, sceneY: Int, level: Int) {
		groundItemSpawned(itemId, amount, sceneX, sceneY, level, GroundItem.OWNERSHIP_NONE)
	}

	/** An item is about to leave the ground at scene tile ([sceneX], [sceneY]). */
	@JvmStatic
	fun groundItemDespawned(itemId: Int, amount: Int, sceneX: Int, sceneY: Int, level: Int) {
		if (!started || !bus.hasListeners(Events.ITEM_DESPAWNED)) {
			return
		}
		bus.post(Events.ITEM_DESPAWNED, GroundItem(itemId, amount, sceneX, sceneY, level))
	}

	/** A whole pile is about to leave the ground: one ItemDespawned for each item in it. */
	@JvmStatic
	fun groundPileDespawned(pile: ObjStack, sceneX: Int, sceneY: Int, level: Int) {
		if (!started || !bus.hasListeners(Events.ITEM_DESPAWNED)) {
			return
		}
		// a cursor of its own: the caller is walking the piles with the shared one, and a listener may walk them too
		val entries: DequeIterator = DequeIterator(pile.groundItems)
		var entry: ObjStackEntry? = entries.first() as ObjStackEntry?
		while (entry != null) {
			bus.post(Events.ITEM_DESPAWNED, GroundItem(entry.id, entry.count, sceneX, sceneY, level))
			entry = entries.next() as ObjStackEntry?
		}
	}

	/** The server hit [actor]; see HitsplatApplied. */
	@JvmStatic
	fun hitsplatApplied(actor: Actor, type: Int, damage: Int, soakType: Int, soakDamage: Int, delay: Int) {
		if (!started || !bus.hasListeners(Events.HITSPLAT_APPLIED)) {
			return
		}
		bus.post(Events.HITSPLAT_APPLIED, HitsplatApplied(actor, type, damage, soakType, soakDamage, delay))
	}

	/** The server set [actor]'s animations: [animationIds] is one id for each movement type, the first for standing. */
	@JvmStatic
	fun animationChanged(actor: Actor?, animationIds: IntArray?) {
		if (!started || actor == null || !bus.hasListeners(Events.ANIMATION_CHANGED)) {
			return
		}
		val animationId: Int = if (animationIds == null || animationIds.isEmpty()) -1 else animationIds[0]
		bus.post(Events.ANIMATION_CHANGED, AnimationChanged(actor, animationId))
	}

	/** [actor]'s target changed; the actor itself is the event. */
	@JvmStatic
	fun interactingChanged(actor: Actor?) {
		if (!started || actor == null || !bus.hasListeners(Events.INTERACTING_CHANGED)) {
			return
		}
		bus.post(Events.INTERACTING_CHANGED, actor)
	}

	private val locChangedEvent: LocChanged = LocChanged()

	/** The player update was read: the plugins' ServerTick, once each server tick. */
	@JvmStatic
	fun serverTick() {
		serverTickEvent.tick++
		if (!started || !bus.hasListeners(Events.SERVER_TICK)) {
			return
		}
		bus.post(Events.SERVER_TICK, serverTickEvent)
	}

	private val serverTickEvent: ServerTick = ServerTick()

	/** The server ticks counted so far, for the smoke test. */
	@JvmStatic
	fun serverTicks(): Int = serverTickEvent.tick

	/** The server changed the location on scene tile ([x], [y]) of [level] in [layer] from [oldId] to [newId] (-1 for none). */
	@JvmStatic
	fun locChanged(x: Int, y: Int, level: Int, layer: Int, oldId: Int, newId: Int, shape: Int, rotation: Int) {
		if (!started || !bus.hasListeners(Events.LOC_CHANGED)) {
			return
		}
		locChangedEvent.sceneX = x
		locChangedEvent.sceneY = y
		locChangedEvent.level = level
		locChangedEvent.layer = layer
		locChangedEvent.oldId = oldId
		locChangedEvent.newId = newId
		locChangedEvent.shape = shape
		locChangedEvent.rotation = rotation
		bus.post(Events.LOC_CHANGED, locChangedEvent)
	}

	/** The server sent Grand Exchange offer [slot] (UPDATE_GE_SLOT): the plugins' GrandExchangeOfferChanged. */
	@JvmStatic
	fun grandExchangeOfferChanged(slot: Int) {
		if (!started || !bus.hasListeners(Events.GRAND_EXCHANGE_OFFER_CHANGED)) {
			return
		}
		readGrandExchangeOffer(slot, grandExchangeEvent.offer)
		bus.post(Events.GRAND_EXCHANGE_OFFER_CHANGED, grandExchangeEvent)
	}

	private val grandExchangeEvent: GrandExchangeOfferChanged = GrandExchangeOfferChanged()

	/** Offer [slot] as the client holds it, into [out]; bit 8 of its state is a sell (Void's OfferState: 9, 10, 13). */
	@JvmStatic
	fun readGrandExchangeOffer(slot: Int, out: GrandExchangeOffer) {
		val offer: StockMarketOffer? = StockMarketOffer.GRAND_EXCHANGE_SLOTS[slot]
		out.slot = slot
		if (offer == null || offer.getSlotState() == GrandExchangeOffer.EMPTY) {
			out.state = GrandExchangeOffer.EMPTY
			out.sell = false
			out.itemId = -1
			out.price = 0
			out.quantity = 0
			out.quantityDone = 0
			out.coins = 0
			return
		}
		out.state = offer.getSlotState()
		out.sell = offer.isBuyOffer() == 1 // the deob's name: the bit is set for a sell
		out.itemId = offer.itemId
		out.price = offer.price
		out.quantity = offer.amount
		out.quantityDone = offer.currentAmount
		out.coins = offer.totalPrice
	}

	/** [text] appeared over [actor]'s head. */
	@JvmStatic
	fun overheadTextChanged(actor: Actor?, text: String?) {
		if (!started || actor == null || text == null || !bus.hasListeners(Events.OVERHEAD_TEXT_CHANGED)) {
			return
		}
		bus.post(Events.OVERHEAD_TEXT_CHANGED, OverheadTextChanged(actor, text))
	}

	/** A projectile was fired; the coordinates are in scene units, the cycles client cycles. */
	@JvmStatic
	fun projectileSpawned(id: Int, level: Int, startX: Int, startY: Int, endX: Int, endY: Int, startCycle: Int, endCycle: Int, sourceIndex: Int, targetIndex: Int) {
		if (!started || !bus.hasListeners(Events.PROJECTILE_SPAWNED)) {
			return
		}
		bus.post(Events.PROJECTILE_SPAWNED, ProjectileSpawned(id, level, startX, startY, endX, endY, startCycle, endCycle, sourceIndex, targetIndex))
	}

	/** A graphic plays on scene tile ([sceneX], [sceneY]). */
	@JvmStatic
	fun spotAnimSpawned(id: Int, sceneX: Int, sceneY: Int, level: Int) {
		if (!started || !bus.hasListeners(Events.SPOT_ANIM_SPAWNED)) {
			return
		}
		bus.post(Events.SPOT_ANIM_SPAWNED, SpotAnimSpawned(id, sceneX, sceneY, level))
	}

	/**
	 * The menu is being built for [target] at scene tile ([sceneX], [sceneY]): the plugins may add
	 * entries. [entityUid] is what the client's own entries for the target carry.
	 */
	@JvmStatic
	fun menuTarget(target: MenuTarget, sceneX: Int, sceneY: Int, level: Int, id: Int, entityUid: Long) {
		if (!started || !bus.hasListeners(Events.MENU_ENTRY_ADDED)) {
			return
		}
		menuBuilding = true
		menuKind = target.kind
		menuParam0Low = sceneX
		menuParam1 = sceneY
		menuEntityUid = entityUid
		menuItemId = if (target === MenuTarget.GROUND_ITEM) id else -1
		menuEntryAddedEvent.target = target
		menuEntryAddedEvent.sceneX = sceneX
		menuEntryAddedEvent.sceneY = sceneY
		menuEntryAddedEvent.level = level
		menuEntryAddedEvent.id = id
		menuEntryAddedEvent.widgetId = 0
		menuEntryAddedEvent.slot = -1
		menuEntryAddedEvent.itemId = menuItemId
		bus.post(Events.MENU_ENTRY_ADDED, menuEntryAddedEvent)
		menuBuilding = false
	}

	/** The menu is being built for an interface [component]: the plugins may add entries. */
	@JvmStatic
	fun menuTargetWidget(component: Component?) {
		if (!started || component == null || !bus.hasListeners(Events.MENU_ENTRY_ADDED)) {
			return
		}
		if (component.contentType == Component.CONTENT_TYPE_WORLDMAP) {
			menuTargetWorldMap()
			return
		}
		if (component.contentType == Component.CONTENT_TYPE_MINIMAP) {
			menuTargetMinimap()
			return
		}
		if (component.containerItemId == -1 && !hasOption(component) && !isText(component)) {
			return // a layer or a picture under the mouse: no entries of the client's, none of the plugins' either
		}
		menuBuilding = true
		menuKind = MenuTarget.WIDGET.kind
		menuParam0Low = component.slotId and 0xFFFF
		menuParam1 = component.idHash
		menuEntityUid = component.containerItemId.toLong()
		menuItemId = component.containerItemId
		menuEntryAddedEvent.target = MenuTarget.WIDGET
		menuEntryAddedEvent.sceneX = 0
		menuEntryAddedEvent.sceneY = 0
		menuEntryAddedEvent.level = 0
		menuEntryAddedEvent.id = 0
		menuEntryAddedEvent.widgetId = component.idHash
		menuEntryAddedEvent.slot = component.slotId
		menuEntryAddedEvent.itemId = component.containerItemId
		bus.post(Events.MENU_ENTRY_ADDED, menuEntryAddedEvent)
		menuBuilding = false
	}

	/** The mouse is over the world map: the plugins' MenuEntryAdded for the world tile under it. Since API 37. */
	private fun menuTargetWorldMap() {
		val mouse = MouseRecord.mouseRecorder ?: return
		if (!PluginWorldMap.tileAt(mouse.getMouseX(), mouse.getMouseY(), worldMapTile)) {
			return
		}
		val before: Int = MiniMenu.entryCount
		menuBuilding = true
		menuKind = MenuTarget.WORLD_MAP.kind
		menuParam0Low = worldMapTile[0]
		menuParam1 = worldMapTile[1]
		menuEntityUid = worldMapTile[2].toLong()
		menuItemId = -1
		menuEntryAddedEvent.target = MenuTarget.WORLD_MAP
		menuEntryAddedEvent.sceneX = worldMapTile[0]
		menuEntryAddedEvent.sceneY = worldMapTile[1]
		menuEntryAddedEvent.level = worldMapTile[2]
		menuEntryAddedEvent.id = worldMapTile[2]
		menuEntryAddedEvent.widgetId = 0
		menuEntryAddedEvent.slot = -1
		menuEntryAddedEvent.itemId = -1
		postSince(API_WORLD_MAP)
		menuBuilding = false
		// the game puts no entry of its own over the map: a left click (a drag) must not take a plugin's
		worldMapEntries = MiniMenu.entryCount > before
	}

	/** Whether plugins added entries over the world map this cycle: the left click is then Cancel's. */
	private var worldMapEntries: Boolean = false

	/** The API versions that brought the world map's and the minimap's menu targets: older plugins never see them. */
	private const val API_WORLD_MAP: Int = 37
	private const val API_MINIMAP: Int = 38

	/** MenuEntryAdded to the plugins that declare API [since] or later only. */
	private fun postSince(since: Int) {
		bus.listenerFilter = EventBusImpl.ListenerFilter { owner -> owner == null || owner.descriptor.apiVersion >= since }
		bus.post(Events.MENU_ENTRY_ADDED, menuEntryAddedEvent)
		bus.listenerFilter = null
	}

	private val worldMapTile: IntArray = IntArray(3)

	/** The mouse is over the minimap's map (inside its round mask, the map drawn this frame): the plugins' MenuEntryAdded for it. Since API 38. */
	private fun menuTargetMinimap() {
		val mouse = MouseRecord.mouseRecorder ?: return
		if (SceneDrawer.timesSceneRendered - minimapFrame > 2 || minimapRowStart == null || !onMinimap(mouse.getMouseX(), mouse.getMouseY())) {
			return // the loading minimap, the box's corners, or no minimap drawn
		}
		menuBuilding = true
		menuKind = MenuTarget.MINIMAP.kind
		menuParam0Low = 0
		menuParam1 = 0
		menuEntityUid = 0L
		menuItemId = -1
		menuEntryAddedEvent.target = MenuTarget.MINIMAP
		menuEntryAddedEvent.sceneX = 0
		menuEntryAddedEvent.sceneY = 0
		menuEntryAddedEvent.level = 0
		menuEntryAddedEvent.id = 0
		menuEntryAddedEvent.widgetId = 0
		menuEntryAddedEvent.slot = -1
		menuEntryAddedEvent.itemId = -1
		postSince(API_MINIMAP)
		menuBuilding = false
	}

	/** Adds a plugin's entry for the target being built: the tile's y in param1, and [menuParam0] in param0. */
	fun addMenuEntry(label: String, clickType: Int) {
		if (!menuBuilding || clickType < MiniMenuAction.PLUGIN_FIRST || clickType > MiniMenuAction.PLUGIN_LAST) {
			return
		}
		if (menuActions[clickType - MiniMenuAction.PLUGIN_FIRST] == null) {
			return
		}
		MiniMenu.addEntry(false, "", menuParam1, true, menuParam0(menuParam0Low, menuKind, clickType), menuItemId, true, clickType, 0L, label, menuEntityUid, -1)
	}

	/** A plugin entry's param0: the tile's x in the low 16 bits, the target's kind above it, and the click-type slot's generation above that. */
	@JvmStatic
	fun menuParam0(sceneX: Int, kind: Int, clickType: Int): Int {
		val generation: Int = menuActionGenerations[clickType - MiniMenuAction.PLUGIN_FIRST]
		return (sceneX and 0xFFFF) or ((kind and KIND_MASK) shl KIND_SHIFT) or (generation shl GENERATION_SHIFT)
	}

	/** A menu entry was clicked; true when it was a plugin's and has been handled. */
	@JvmStatic
	fun menuClicked(entry: MiniMenuEntry, clickType: Int): Boolean {
		if (!started) {
			return false
		}
		if (bus.hasListeners(Events.MENU_OPTION_CLICKED)) {
			menuOptionClickedEvent.clickType = clickType
			// opBase is the verb and op the coloured target, whatever their names say; apiOneMenuClick sets them per plugin
			clickedOption = entry.opBase
			clickedTarget = entry.op
			clickedTargetName = entry.targetName
			menuOptionClickedEvent.param0 = entry.param0
			menuOptionClickedEvent.param1 = entry.param1
			menuOptionClickedEvent.entityUid = entry.entityUid
			menuOptionClickedEvent.itemId = entry.itemId
			menuOptionClickedEvent.consumed = false
			bus.beforeListener = apiOneMenuClick
			bus.post(Events.MENU_OPTION_CLICKED, menuOptionClickedEvent)
			bus.beforeListener = null
			if (menuOptionClickedEvent.consumed) {
				return true
			}
		}
		if (clickType < MiniMenuAction.PLUGIN_FIRST || clickType > MiniMenuAction.PLUGIN_LAST) {
			return false
		}
		val index: Int = clickType - MiniMenuAction.PLUGIN_FIRST
		val action: MenuAction = menuActions[index] ?: return true
		if ((entry.param0 ushr GENERATION_SHIFT) and GENERATION_MASK != menuActionGenerations[index]) {
			// an entry from before the plugin that made it stopped: the slot's new plugin never asked for this click
			return true
		}
		val owner: PluginRegistration? = menuActionOwners[index]
		val target: MenuTarget = MenuTarget.ofKind((entry.param0 ushr KIND_SHIFT) and KIND_MASK)
		var low: Int = entry.param0 and 0xFFFF
		if (target === MenuTarget.WIDGET && low == 0xFFFF) {
			low = -1 // a component from the cache has no slot
		}
		try {
			action.clicked(target, low, entry.param1, entry.entityUid.toInt())
		} catch (error: Throwable) {
			fail(owner, error)
		}
		return true
	}

	/** A text shown, which a plugin may give entries of its own (a chat line's "Copy to clipboard"). Since API 27. */
	private fun isText(component: Component): Boolean {
		val text: String? = component.text
		return component.type === ComponentType.TEXT && text != null && text.isNotEmpty()
	}

	private fun hasOption(component: Component): Boolean {
		val options: Array<String?> = component.options ?: return false
		for (i in 0 until options.size) {
			if (options[i] != null) {
				return true
			}
		}
		return false
	}

	/**
	 * The menu for what is under the mouse is complete and nothing has been done with it: the plugins
	 * may edit it. Not while the menu is open, when the entries are frozen. True when a listener took
	 * an entry out or moved one.
	 */
	@JvmStatic
	fun menuBuilt(): Boolean {
		promotedDefault = null
		if (worldMapEntries) {
			// only plugins' entries over the world map: Cancel takes the left click, which stays the map's drag
			worldMapEntries = false
			promotedDefault = MiniMenu.cancelOption
		}
		if (!started || MiniMenu.isOpen) {
			return false
		}
		val edited: Boolean = bus.hasListeners(Events.MENU_BUILT) && postMenuBuilt()
		recordTopEntry()
		return edited
	}

	/** What the left click would do, as the menu was last built (ClientApi.hoveredTarget): the top entry, or the one a plugin promoted. */
	@JvmField var topClickType: Int = -1
	@JvmField var topParam0: Int = 0
	@JvmField var topParam1: Int = 0
	@JvmField var topUid: Long = 0L
	@JvmField var topOption: String? = null
	@JvmField var topCycle: Int = -1

	private fun recordTopEntry() {
		var entry: MiniMenuEntry? = promotedDefault
		var node: Node? = MiniMenu.entryList.sentinel.previous
		while (entry == null && node != null && node !== MiniMenu.entryList.sentinel) {
			entry = node as? MiniMenuEntry
			node = node.previous
		}
		topCycle = ClientState.clientCycle
		if (entry == null) {
			topClickType = -1
			return
		}
		val view: MenuEntry = entry
		topClickType = view.clickType()
		topParam0 = view.param0()
		topParam1 = view.param1()
		topUid = view.entityUid()
		topOption = view.option()
	}

	/** The plugins' MenuBuilt, on the finished menu; true when a listener took an entry out or moved one. */
	private fun postMenuBuilt(): Boolean {
		// the entries from the top: the last of the list draws first and takes the left click
		menuEntryTotal = 0
		var node: Node? = MiniMenu.entryList.sentinel.previous
		while (node != null && node !== MiniMenu.entryList.sentinel && menuEntryTotal < MAX_MENU_ENTRIES) {
			if (node is MiniMenuEntry) {
				menuEntries[menuEntryTotal++] = node
			}
			node = node.previous
		}
		menuBuiltEvent.entryCount = menuEntryTotal
		menuEdited = false
		menuEditing = true
		bus.post(Events.MENU_BUILT, menuBuiltEvent)
		menuEditing = false
		for (i in 0 until menuEntryTotal) {
			menuEntries[i] = null
		}
		menuEntryTotal = 0
		return menuEdited
	}

	/** The entry a plugin promoted at MenuBuilt that the top row cannot show, for the left click; null for none. */
	@JvmStatic
	fun promotedDefault(): MiniMenuEntry? {
		return promotedDefault
	}

	/** The menu's entry [i] from the top, while a MenuBuilt listener runs. */
	private fun menuEntryAt(i: Int): MiniMenuEntry? {
		return if (menuEditing && i >= 0 && i < menuEntryTotal) menuEntries[i] else null
	}

	fun menuEntryCount(): Int {
		return if (menuEditing) menuEntryTotal else 0
	}

	fun menuEntry(i: Int): MenuEntry? {
		return menuEntryAt(i)
	}

	fun removeMenuEntry(i: Int): Boolean {
		val entry: MiniMenuEntry = menuEntryAt(i) ?: return false
		if (entry.clickType == MiniMenuAction.CANCEL) {
			return false
		}
		MiniMenu.removeEntry(entry)
		for (j in i until menuEntryTotal - 1) {
			menuEntries[j] = menuEntries[j + 1]
		}
		menuEntries[--menuEntryTotal] = null
		if (promotedDefault === entry) {
			promotedDefault = null
		}
		menuEdited = true
		return true
	}

	fun promoteMenuEntry(i: Int): Boolean {
		val entry: MiniMenuEntry = menuEntryAt(i) ?: return false
		if (entry.clickType == MiniMenuAction.CANCEL) {
			return false
		}
		if (i == 0) {
			return true
		}
		if (entry.clickType == MiniMenuAction.IF_BUTTONX2) {
			// a component's options 6 to 10 on the top row would turn the left click into a right click
			// (MiniMenu.isTopEntryInterfaceAction): the row stays where it is and the left click runs it
			promotedDefault = entry
			return true
		}
		// out and in again: addEntry appends, and the last entry is the top row and the left click
		MiniMenu.removeEntry(entry)
		MiniMenu.addEntry(entry)
		for (j in i downTo 1) {
			menuEntries[j] = menuEntries[j - 1]
		}
		menuEntries[0] = entry
		// and the left click whatever the client's own Shift rules would pick (MiniMenuInputTick)
		promotedDefault = entry
		menuEdited = true
		return true
	}

	fun renameMenuEntry(i: Int, option: String): Boolean {
		val entry: MiniMenuEntry = menuEntryAt(i) ?: return false
		entry.opBase = option
		return true
	}

	/**
	 * Gives entry [i] another target text. MiniMenu groups entries by that text (ColoredMiniMenuEntry,
	 * looked up by it again to remove one), so the entry comes out of its group first and goes back
	 * in under the new text, with the entries above it taken out and put back after it, since the
	 * last entry added is the top row.
	 */
	fun retargetMenuEntry(i: Int, target: String): Boolean {
		val entry: MiniMenuEntry = menuEntryAt(i) ?: return false
		if (entry.op == target) {
			return true
		}
		for (j in 0..i) {
			MiniMenu.removeEntry(menuEntries[j])
		}
		entry.op = target
		for (j in i downTo 0) {
			MiniMenu.addEntry(menuEntries[j])
		}
		menuEdited = true
		return true
	}

	/**
	 * Sinks entry [i] below every entry that is not sunk: its click type gets the game's 2000 (the flag
	 * MiniMenuActionExecutor takes off again, as for Attack on a weaker player) and it moves to the
	 * bottom of the list, which draws last and is never the left click while another entry is above it.
	 */
	fun deprioritizeMenuEntry(i: Int): Boolean {
		val entry: MiniMenuEntry = menuEntryAt(i) ?: return false
		if (entry.clickType == MiniMenuAction.CANCEL) {
			return false
		}
		if (entry.clickType < DEPRIORITIZED) {
			entry.clickType += DEPRIORITIZED
		}
		// out of its group and in again (addEntry appends), then to the head of the list: the bottom row
		MiniMenu.removeEntry(entry)
		MiniMenu.addEntry(entry)
		entry.unlink()
		MiniMenu.entryList.addFirst(entry)
		for (j in i until menuEntryTotal - 1) {
			menuEntries[j] = menuEntries[j + 1]
		}
		menuEntries[menuEntryTotal - 1] = entry
		if (promotedDefault === entry) {
			promotedDefault = null
		}
		menuEdited = true
		return true
	}

	/** Adds a plugin's entry that opens submenu [submenus] index when hovered; the submenu's index, or -1 when no menu is being built. */
	fun addSubmenu(label: String): Int {
		if (!menuBuilding || MiniMenu.isOpen || submenuCount >= MAX_SUBMENUS) {
			return -1
		}
		val index: Int = submenuCount++
		val submenu: ColoredMiniMenuEntry = ColoredMiniMenuEntry(label)
		submenus[index] = submenu
		val before: Int = MiniMenu.entryCount
		MiniMenu.addEntry(false, "", menuParam1, true, (menuParam0Low and 0xFFFF) or ((menuKind and KIND_MASK) shl KIND_SHIFT), menuItemId, true, MiniMenuAction.PLUGIN_SUBMENU, 0L, label, index.toLong(), -1)
		if (MiniMenu.entryCount == before) {
			submenus[index] = null
			submenuCount--
			return -1
		}
		submenuParents[index] = MiniMenu.entryList.last() as MiniMenuEntry?
		return index
	}

	/** Adds a plugin's entry to submenu [submenu], after the ones already in it. */
	fun addSubmenuEntry(submenu: Int, label: String, clickType: Int) {
		if (!menuBuilding || submenu < 0 || submenu >= submenuCount || clickType < MiniMenuAction.PLUGIN_FIRST || clickType > MiniMenuAction.PLUGIN_LAST) {
			return
		}
		val parent: ColoredMiniMenuEntry = submenus[submenu] ?: return
		if (menuActions[clickType - MiniMenuAction.PLUGIN_FIRST] == null || parent.queueSize >= MAX_SUBMENU_ENTRIES) {
			return
		}
		val child: MiniMenuEntry = MiniMenuEntry(label, "", Cursor.interfaceCursorId, clickType, menuItemId, menuEntityUid, menuParam0(menuParam0Low, menuKind, clickType), menuParam1, true, false, 0L, true)
		// appended: the menu's own ordering (ColoredMiniMenuEntry.addEntry) puts each new plugin entry first
		parent.nodeQueue.insert(child)
		parent.queueSize++
	}

	/**
	 * The client's own entry opening a submenu (the panels' Overlay Origin, OverlayLayout), outside the
	 * building of a target's entries: [label] with [target] after it, its children's param0 [param0]. The
	 * submenu's index, or -1 when there is no room.
	 */
	fun clientSubmenu(label: String, target: String, param0: Int): Int {
		if (MiniMenu.isOpen || submenuCount >= MAX_SUBMENUS) {
			return -1
		}
		val index: Int = submenuCount++
		submenus[index] = ColoredMiniMenuEntry(label)
		val before: Int = MiniMenu.entryCount
		MiniMenu.addEntry(false, target, 0, true, param0, -1, true, MiniMenuAction.PLUGIN_SUBMENU, 0L, label, index.toLong(), -1)
		if (MiniMenu.entryCount == before) {
			submenus[index] = null
			submenuCount--
			return -1
		}
		submenuParents[index] = MiniMenu.entryList.last() as MiniMenuEntry?
		return index
	}

	/** An entry of the client's own in [submenu] (clientSubmenu), clicked as MiniMenuAction.OVERLAY_MENU with [param0] and [param1]. */
	fun clientSubmenuEntry(submenu: Int, label: String, param0: Int, param1: Int) {
		if (submenu < 0 || submenu >= submenuCount) {
			return
		}
		val parent: ColoredMiniMenuEntry = submenus[submenu] ?: return
		if (parent.queueSize >= MAX_SUBMENU_ENTRIES) {
			return
		}
		val child: MiniMenuEntry = MiniMenuEntry(label, "", Cursor.interfaceCursorId, MiniMenuAction.OVERLAY_MENU, -1, 0L, param0, param1, true, false, 0L, true)
		parent.nodeQueue.insert(child)
		parent.queueSize++
	}

	/** The submenu a hovered plugin entry opens, or null when [entry] is not one of those. */
	@JvmStatic
	fun submenuOf(entry: MiniMenuEntry?): ColoredMiniMenuEntry? {
		if (entry == null || entry.clickType != MiniMenuAction.PLUGIN_SUBMENU) {
			return null
		}
		val index: Int = entry.entityUid.toInt()
		if (index < 0 || index >= submenuCount || submenuParents[index] !== entry) {
			return null
		}
		val submenu: ColoredMiniMenuEntry = submenus[index] ?: return null
		return if (submenu.queueSize > 0) submenu else null
	}

	/** The menu is being built again (MiniMenu.reset): the submenus of the last one are gone. */
	@JvmStatic
	fun menuReset() {
		for (i in 0 until submenuCount) {
			submenus[i] = null
			submenuParents[i] = null
		}
		submenuCount = 0
	}

	/** An interface opened inside the component [parentHash]. */
	@JvmStatic
	fun interfaceOpened(interfaceId: Int, parentHash: Int, openType: SubInterfaceType?) {
		if (!started || !bus.hasListeners(Events.INTERFACE_OPENED)) {
			return
		}
		bus.post(Events.INTERFACE_OPENED, InterfaceChanged(interfaceId, parentHash, openType?.id ?: -1))
	}

	/** An interface is closing, its components still there. */
	@JvmStatic
	fun interfaceClosed(interfaceId: Int, parentHash: Int, openType: SubInterfaceType?) {
		if (!started || !bus.hasListeners(Events.INTERFACE_CLOSED)) {
			return
		}
		bus.post(Events.INTERFACE_CLOSED, InterfaceChanged(interfaceId, parentHash, openType?.id ?: -1))
	}

	/** The root interface is about to change from [previous] to [interfaceId]. */
	@JvmStatic
	fun topInterfaceChanged(previous: Int, interfaceId: Int) {
		lastTopInterface = interfaceId
		if (!started || previous == interfaceId || !bus.hasListeners(Events.TOP_INTERFACE_CHANGED)) {
			return
		}
		bus.post(Events.TOP_INTERFACE_CHANGED, TopInterfaceChanged(previous, interfaceId))
	}

	/**
	 * A console command the client did not answer itself, before it would go to the server: `plugins`
	 * and `plugin <id> on|off`, then the commands the plugins registered, then the ConsoleCommand
	 * event. True when handled. Not called for Tab completion.
	 */
	@JvmStatic
	fun consoleCommand(line: String): Boolean {
		if (!started) {
			return false
		}
		val trimmed: String = line.trim()
		if (trimmed.isEmpty()) {
			return false
		}
		val words: ArrayList<String> = ArrayList()
		for (word in trimmed.split(' ')) {
			if (word.isNotEmpty()) {
				words.add(word)
			}
		}
		val first: String = words[0].lowercase()
		if (first == "resetanchors") {
			// RuneLite's ::resetanchors: the snap corners back where they were
			OverlayLayout.resetAnchors()
			Console.printToConsole("Snap corners reset.")
			return true
		}
		if (first == "plugins") {
			listPlugins()
			return true
		}
		if (first == "plugin") {
			pluginCommand(words)
			return true
		}
		val index: Int = findCommand(first)
		if (index >= 0) {
			val args: Array<String> = Array(words.size - 1) { words[it + 1] }
			val handler: CommandHandler = commandHandlers[index]!!
			val owner: PluginRegistration? = commandOwners[index]
			try {
				handler.run(words[0], args)
			} catch (error: Throwable) {
				fail(owner, error)
			}
			return true
		}
		if (!bus.hasListeners(Events.CONSOLE_COMMAND)) {
			return false
		}
		consoleCommandEvent.line = trimmed
		consoleCommandEvent.consumed = false
		bus.post(Events.CONSOLE_COMMAND, consoleCommandEvent)
		return consoleCommandEvent.consumed
	}

	private fun listPlugins() {
		Console.printToConsole("Plugins: " + count + (if (sourceDescription != null) " (jars from " + sourceDescription + ")" else ""))
		for (i in 0 until count) {
			val registration: PluginRegistration = registrations[i]!!
			val line: StringBuilder = StringBuilder("  ")
			line.append(registration.descriptor.id).append(": ").append(registration.descriptor.name).append(' ').append(registration.descriptor.version)
			if (registration.descriptor.author.isNotEmpty()) {
				line.append(" by ").append(registration.descriptor.author)
			}
			line.append(" [").append(registration.stateName())
			if (registration.error != null) {
				line.append(": ").append(registration.error)
			}
			line.append("] ").append(registration.source)
			Console.printToConsole(plain(line.toString()))
		}
		Console.printToConsole("plugin <id> on|off turns one on or off; a built-in plugin has its own command")
	}

	private fun pluginCommand(words: ArrayList<String>) {
		if (words.size != 3 || !(words[2].equals("on", ignoreCase = true) || words[2].equals("off", ignoreCase = true))) {
			Console.printToConsole("Usage: plugin <id> on|off (plugins lists the ids)")
			return
		}
		val id: String = words[1]
		val registration: PluginRegistration? = find(id)
		if (registration == null) {
			Console.printToConsole("No plugin " + id + " (plugins lists them)")
			return
		}
		if (registration.builtIn) {
			Console.printToConsole(registration.descriptor.name + " is built in; it has its own command")
			return
		}
		Console.printToConsole(setPluginEnabled(registration, words[2].equals("on", ignoreCase = true)))
	}

	/**
	 * Turns a plugin from a jar on or off, now and on later starts, for the console and the plugin
	 * manager alike; the line to tell the player. A built-in is never stopped: its own setting is its switch.
	 */
	fun setPluginEnabled(registration: PluginRegistration, on: Boolean): String {
		val name: String = registration.descriptor.name
		if (registration.builtIn) {
			return name + " is built in; its own setting turns it off"
		}
		if (on) {
			if (registration.context != null) {
				return name + " is already on"
			}
			if (!registration.loadable()) {
				return name + " cannot be turned on: " + registration.error
			}
			registration.state = PluginRegistration.LOADED
			startPlugin(registration)
			if (registration.state != PluginRegistration.STARTED) {
				return name + " failed: " + registration.error
			}
			PluginSettings.setEnabled(registration.descriptor.id, true)
			return name + " on"
		}
		if (registration.context == null) {
			return name + " is already off"
		}
		stopPlugin(registration)
		registration.state = PluginRegistration.STOPPED
		registration.error = null
		PluginSettings.setEnabled(registration.descriptor.id, false)
		return name + " off"
	}

	/** The plugin manager wrote [key] of [registration]'s settings and saved it: SettingChanged, for its plugin and anyone else listening. */
	@JvmStatic
	fun settingChanged(registration: PluginRegistration, key: String) {
		if (!started || !bus.hasListeners(Events.SETTING_CHANGED)) {
			return
		}
		bus.post(Events.SETTING_CHANGED, SettingChanged(registration.descriptor.id, key))
	}
}
