package recipes

import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.events.Events

/**
 * A menu entry swapper: "Bank" is the left click on bankers, booths and chests, and "Examine"
 * entries are gone from the menu. The menu is read and edited at [Events.MENU_BUILT], every game
 * tick, once the client has built it for what is under the mouse and before the left click is
 * resolved. Entry 0 is the top row and the left click.
 */
class LeftClickBank : Plugin {
    override fun start(context: PluginContext) {
        val client = context.client()
        context.events().subscribe(Events.MENU_BUILT) { _ ->
            // from the bottom up, so that removing an entry never skips the next one
            var i = client.menuEntryCount() - 1
            while (i >= 0) {
                if (client.menuEntry(i)?.option().equals("Examine", ignoreCase = true)) {
                    client.removeMenuEntry(i)
                }
                i--
            }
            for (j in 1 until client.menuEntryCount()) {
                val option = client.menuEntry(j)?.option() ?: continue
                if (option.equals("Bank", ignoreCase = true) || option.equals("Use-quickly", ignoreCase = true)) {
                    client.promoteMenuEntry(j)
                    break
                }
            }
        }
    }
}
