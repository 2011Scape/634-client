package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.ChatLineWriter
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.client.input.keyboard.KeyRecord
import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.menu.MiniMenuEntry
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.ConsoleCommand
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.FrameDraw
import com.jagex.game.runetek5.plugin.api.events.GameStateChanged
import com.jagex.game.runetek5.plugin.api.events.GameTick
import com.jagex.game.runetek5.plugin.api.events.ItemContainerChanged
import com.jagex.game.runetek5.plugin.api.events.KeyPressed
import com.jagex.game.runetek5.plugin.api.events.MenuEntryAdded
import com.jagex.game.runetek5.plugin.api.events.MenuOptionClicked
import com.jagex.game.runetek5.plugin.api.events.MouseClicked
import com.jagex.game.runetek5.plugin.api.events.StatChanged
import com.jagex.game.runetek5.plugin.api.events.ToolkitChanged
import com.jagex.game.runetek5.plugin.api.events.VarpChanged
import com.jagex.game.runetek5.plugin.api.events.ViewportDraw
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

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

	private const val MAX_PLUGINS: Int = 64

	private const val MAX_OVERLAYS: Int = 128

	private const val MAX_COMMANDS: Int = 128

	private const val MAX_MENU_ACTIONS: Int = MiniMenuAction.PLUGIN_LAST - MiniMenuAction.PLUGIN_FIRST + 1

	/** The client's own console commands a plugin from a jar may not take over. */
	private val RESERVED_COMMANDS: Array<String> = arrayOf("plugins", "plugin", "commands", "help", "cls", "displayfps", "renderer", "heap", "tk0", "tk1", "tk2", "tk3", "tk5", "tk6", "grounditems", "npclabels", "xpdrops", "tilemarkers", "shiftdrop", "msaa", "smaa", "renderscale", "hd", "hdsettings")

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

	private val clientTickEvent: ClientTick = ClientTick()

	private val gameTickEvent: GameTick = GameTick()

	private val keyPressedEvent: KeyPressed = KeyPressed()

	private val mouseClickedEvent: MouseClicked = MouseClicked()

	private val menuEntryAddedEvent: MenuEntryAdded = MenuEntryAdded()

	private val menuOptionClickedEvent: MenuOptionClicked = MenuOptionClicked()

	private val consoleCommandEvent: ConsoleCommand = ConsoleCommand()

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

	/** The menu entry target being built, while a MenuEntryAdded listener runs. */
	private var menuBuilding: Boolean = false

	private var menuKind: Int = 0

	private var menuSceneX: Int = 0

	private var menuSceneY: Int = 0

	private var menuEntityUid: Long = 0L

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
			if (registration.state == PluginRegistration.FAILED || registration.plugin == null) {
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
		val context: PluginContextImpl = PluginContextImpl(registration)
		registration.context = context
		registration.error = null
		try {
			registration.plugin!!.start(context)
			registration.state = PluginRegistration.STARTED
		} catch (error: Throwable) {
			fail(registration, error)
		}
	}

	private fun stopPlugin(registration: PluginRegistration) {
		if (registration.context != null && registration.plugin != null) {
			try {
				registration.plugin.stop()
			} catch (error: Throwable) {
				lang.printlnErr("Plugin " + registration.descriptor + " threw while stopping: " + error)
				lang.printlnErr(error.stackTraceToString())
			}
		}
		removeAll(registration)
		registration.context = null
		if (registration.state != PluginRegistration.FAILED) {
			registration.state = PluginRegistration.STOPPED
		}
	}

	/** Takes back everything [registration] registered, without stopping its plugin: for the tests. */
	@JvmStatic
	fun release(registration: PluginRegistration) {
		removeAll(registration)
		registration.context = null
	}

	/** Takes back everything [registration] registered. */
	private fun removeAll(registration: PluginRegistration) {
		bus.removeOwner(registration)
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
			}
		}
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
		Console.printToConsole(message)
		if (ClientState.mainLogicStep === MainLogicStep.STEP_GAME_SCREEN) {
			ChatLineWriter.writeUnfilterableMessage(message)
		}
	}

	fun addOverlay(owner: PluginRegistration, overlay: Overlay) {
		if (overlayCount == MAX_OVERLAYS) {
			lang.printlnErr("Plugin " + owner.descriptor + ": no room for another overlay (the most is " + MAX_OVERLAYS + ")")
			return
		}
		overlays[overlayCount] = overlay
		overlayOwners[overlayCount] = owner
		overlayCount++
	}

	fun removeOverlay(overlay: Overlay) {
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
		clientTickEvent.cycle = ClientState.clientCycle
		bus.post(Events.CLIENT_TICK, clientTickEvent)
	}

	@JvmStatic
	fun gameTick() {
		if (!started) {
			return
		}
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
		bus.post(Events.VIEWPORT_DRAW, viewportDrawEvent)
		if (overlayCount == 0) {
			return
		}
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
			}
			i++
		}
		drawingOverlays = false
		compactOverlays()
	}

	/** After the whole frame is drawn, before it is shown. */
	@JvmStatic
	fun drawFrame(width: Int, height: Int) {
		if (!started || !bus.hasListeners(Events.FRAME_DRAW)) {
			return
		}
		frameDrawEvent.width = width
		frameDrawEvent.height = height
		bus.post(Events.FRAME_DRAW, frameDrawEvent)
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
	fun chatMessage(type: Int, name: String?, message: String?, clan: String?) {
		if (!started || !bus.hasListeners(Events.CHAT_MESSAGE)) {
			return
		}
		bus.post(Events.CHAT_MESSAGE, ChatMessage(type, name, message, clan))
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
		if (!started) {
			return
		}
		bus.post(Events.TOOLKIT_CHANGED, ToolkitChanged(toolkitId))
	}

	@JvmStatic
	fun keyPressed(record: KeyRecord) {
		if (!started || !bus.hasListeners(Events.KEY_PRESSED)) {
			return
		}
		keyPressedEvent.keyCode = record.getKeyCode()
		keyPressedEvent.keyChar = record.getKeyChar()
		keyPressedEvent.modifiers = record.getModifierFlags()
		bus.post(Events.KEY_PRESSED, keyPressedEvent)
	}

	@JvmStatic
	fun mouseClicked(record: MouseRecord, clickType: Int) {
		if (!started || !bus.hasListeners(Events.MOUSE_CLICKED)) {
			return
		}
		mouseClickedEvent.x = record.getMouseX()
		mouseClickedEvent.y = record.getMouseY()
		mouseClickedEvent.button = clickType
		bus.post(Events.MOUSE_CLICKED, mouseClickedEvent)
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
		menuSceneX = sceneX
		menuSceneY = sceneY
		menuEntityUid = entityUid
		menuEntryAddedEvent.target = target
		menuEntryAddedEvent.sceneX = sceneX
		menuEntryAddedEvent.sceneY = sceneY
		menuEntryAddedEvent.level = level
		menuEntryAddedEvent.id = id
		bus.post(Events.MENU_ENTRY_ADDED, menuEntryAddedEvent)
		menuBuilding = false
	}

	/** Adds a plugin's entry for the target being built: the kind goes in the high half of param0, the tile in param1 and the low half. */
	fun addMenuEntry(label: String, clickType: Int) {
		if (!menuBuilding || clickType < MiniMenuAction.PLUGIN_FIRST || clickType > MiniMenuAction.PLUGIN_LAST) {
			return
		}
		if (menuActions[clickType - MiniMenuAction.PLUGIN_FIRST] == null) {
			return
		}
		MiniMenu.addEntry(false, "", menuSceneY, true, (menuSceneX and 0xFFFF) or (menuKind shl 16), -1, true, clickType, 0L, label, menuEntityUid, -1)
	}

	/** A menu entry was clicked; true when it was a plugin's and has been handled. */
	@JvmStatic
	fun menuClicked(entry: MiniMenuEntry, clickType: Int): Boolean {
		if (!started) {
			return false
		}
		if (bus.hasListeners(Events.MENU_OPTION_CLICKED)) {
			menuOptionClickedEvent.clickType = clickType
			menuOptionClickedEvent.option = entry.op
			menuOptionClickedEvent.target = entry.targetName
			menuOptionClickedEvent.param0 = entry.param0
			menuOptionClickedEvent.param1 = entry.param1
			menuOptionClickedEvent.entityUid = entry.entityUid
			bus.post(Events.MENU_OPTION_CLICKED, menuOptionClickedEvent)
		}
		if (clickType < MiniMenuAction.PLUGIN_FIRST || clickType > MiniMenuAction.PLUGIN_LAST) {
			return false
		}
		val index: Int = clickType - MiniMenuAction.PLUGIN_FIRST
		val action: MenuAction = menuActions[index] ?: return true
		val owner: PluginRegistration? = menuActionOwners[index]
		try {
			action.clicked(MenuTarget.ofKind(entry.param0 ushr 16), entry.param0 and 0xFFFF, entry.param1, entry.entityUid.toInt())
		} catch (error: Throwable) {
			fail(owner, error)
		}
		return true
	}

	/**
	 * A console command, before the client's own: `plugins` and `plugin <id> on|off`, then the
	 * commands the plugins registered, then the ConsoleCommand event. True when handled.
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
			line.append(" [").append(registration.stateName())
			if (registration.error != null) {
				line.append(": ").append(registration.error)
			}
			line.append("] ").append(registration.source)
			Console.printToConsole(line.toString())
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
		val on: Boolean = words[2].equals("on", ignoreCase = true)
		if (on) {
			if (registration.context != null) {
				Console.printToConsole(registration.descriptor.name + " is already on")
				return
			}
			if (registration.plugin == null) {
				Console.printToConsole(registration.descriptor.name + " cannot be turned on: " + registration.error)
				return
			}
			registration.state = PluginRegistration.LOADED
			startPlugin(registration)
			if (registration.state == PluginRegistration.STARTED) {
				PluginSettings.setEnabled(registration.descriptor.id, true)
				Console.printToConsole(registration.descriptor.name + " on")
			}
		} else {
			if (registration.context == null) {
				Console.printToConsole(registration.descriptor.name + " is already off")
				return
			}
			stopPlugin(registration)
			registration.state = PluginRegistration.STOPPED
			registration.error = null
			PluginSettings.setEnabled(registration.descriptor.id, false)
			Console.printToConsole(registration.descriptor.name + " off")
		}
	}
}
