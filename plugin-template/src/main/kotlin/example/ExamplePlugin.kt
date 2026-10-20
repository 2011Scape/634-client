package example

import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.events.Events

/**
 * An example plugin: a line of text on the game view (with what the player is fighting), a `::hello`
 * command that writes to the chat, a "Hello" entry on the right-click menu of every npc, a line in
 * the chat for every hit on the player, a "Value" entry on every item in an interface, a log line for
 * every npc that appears and every interface that opens, an outline on the tile under the mouse, the `#`
 * key taken as a hotkey, `::bg` for work on another thread, and a count of how often it was started,
 * kept in the plugin's settings.
 *
 * Every callback runs on the game thread. The event objects of the events that happen every frame
 * are reused, so read them in the listener and keep nothing.
 */
class ExamplePlugin : Plugin {

    private var context: PluginContext? = null

    private var starts: Int = 0

    private var lastHello: String = "nobody yet"
    private val xs = IntArray(4)
    private val ys = IntArray(4)

    override fun start(context: PluginContext) {
        this.context = context
        val client = context.client()

        // the plugin's own settings: plugin-config/example.properties in the plugins folder
        starts = context.config().getInt("starts", 0) + 1
        context.config().setInt("starts", starts)
        context.config().save()
        context.log("started $starts time(s)")

        // drawn every frame on the game view, after the scene and before the interfaces
        context.addOverlay { g, view ->
            if (g.fontReady()) {
                g.text(view.x + 8, view.y + 20, "Example plugin: hello, ${client.localPlayer()?.name() ?: "stranger"} (last ::hello to $lastHello)", 0xFFFFFF00.toInt(), 0xFF000000.toInt())
                if (client.hoveredTileX() >= 0 && client.tilePolygon(client.hoveredTileX(), client.hoveredTileY(), client.localPlayer()?.level() ?: 0, 1, xs, ys)) {
                    g.fillPolygon(xs, ys, 4, 0x40FFFFFF)
                    g.polygon(xs, ys, 4, 0xFFFFFFFF.toInt())
                }
                val target = client.localPlayer()?.interacting()
                if (target != null) {
                    val name = if (target is Npc) "${target.name()} (level ${target.combatLevel()})" else "a player"
                    g.text(view.x + 8, view.y + 36, "Fighting $name, animation ${target.animationId()}, health ${target.healthRatio()}/255", 0xFFFF8080.toInt(), 0xFF000000.toInt())
                }
            }
        }

        // ::hello [name] writes to the chat box
        context.registerCommand("hello") { _, args ->
            lastHello = if (args.isEmpty()) "the world" else args.joinToString(" ")
            client.chatMessage("Hello, $lastHello!")
        }

        // a right-click entry on every npc, with what it does when clicked
        val helloAction = context.registerMenuAction { target, sceneX, sceneY, id ->
            val npc = (0 until client.npcCount()).mapNotNull { client.npc(it) }.firstOrNull { it.index() == id }
            client.chatMessage("Hello, ${npc?.name() ?: target.name} at ($sceneX, $sceneY)!")
        }
        val valueAction = context.registerMenuAction { _, slot, widgetId, itemId ->
            val item = client.item(itemId)
            client.chatMessage("Example plugin: ${item?.name()} in slot $slot of component ${widgetId shr 16}:${widgetId and 0xFFFF} is worth ${item?.value()} coins")
        }
        context.events().subscribe(Events.MENU_ENTRY_ADDED) { event ->
            if (event.target === MenuTarget.NPC && helloAction >= 0) {
                client.addMenuEntry("Hello", helloAction)
            } else if (event.target === MenuTarget.WIDGET && event.itemId >= 0 && valueAction >= 0) {
                client.addMenuEntry("Value", valueAction)
            }
        }
        context.events().subscribe(Events.KEY_TYPED) { event ->
            // a hotkey: the character never reaches the chat box
            if (event.keyChar == '#') {
                event.consume()
                client.chatMessage("Example plugin: # pressed, energy ${client.runEnergy()}%, world ${client.worldId()}, region ${client.regionId()}")
            }
        }
        context.registerCommand("bg") { _, _ ->
            // work off the game thread, and its result back on it
            context.startThread("bg") {
                Thread.sleep(500)
                context.invokeLater { client.chatMessage("Example plugin: hello from a thread, half a second later") }
            }
        }
        context.events().subscribe(Events.INTERFACE_OPENED) { event ->
            context.log("interface ${event.interfaceId} opened in component ${event.parentWidgetId shr 16}:${event.parentWidgetId and 0xFFFF}")
        }

        // a word in the chat when experience goes up
        context.events().subscribe(Events.HITSPLAT_APPLIED) { event ->
            if (event.actor === client.localPlayer()) {
                client.chatMessage("Example plugin: you were hit for ${event.damage}")
            }
        }
        context.events().subscribe(Events.NPC_SPAWNED) { npc ->
            context.log("${npc.name()} (type ${npc.typeId()}) appeared at tile ${npc.x() shr 9}, ${npc.z() shr 9}")
        }
        context.events().subscribe(Events.STAT_CHANGED) { event ->
            if (event.newXp > event.oldXp && client.gameState() == GameState.LOGGED_IN) {
                client.chatMessage("Example plugin: +${event.newXp - event.oldXp} experience in skill ${event.skill}")
            }
        }
    }

    override fun stop() {
        // everything registered through the context is removed by the client; release your own things here
        context = null
    }
}
