package recipes

import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.events.Events

/**
 * A ground item overlay with values: every pile in the scene gets a line per item, "name (amount)
 * value", white, or green from 1,000 coins and orange from 10,000, stacked above the tile; a drop
 * worth 10,000 or more also writes to the chat as it lands ([Events.ITEM_SPAWNED]). The piles come
 * from [com.jagex.game.runetek5.plugin.api.ClientApi.groundItems], the values from the item types.
 */
class GroundItemValues : Plugin {
    private val point = IntArray(2)
    private var lastX = -1
    private var lastY = -1
    private var stack = 0

    override fun start(context: PluginContext) {
        val client = context.client()
        context.events().subscribe(Events.ITEM_SPAWNED) { item ->
            val value = (client.item(item.itemId)?.value() ?: 0).toLong() * item.amount
            if (value >= 10_000) {
                client.chatMessage("Valuable drop: " + client.item(item.itemId)?.name() + " (" + value + ")")
            }
        }
        context.addOverlay { g, view ->
            lastX = -1
            lastY = -1
            client.groundItems { sceneX, sceneY, level, itemId, amount ->
                val definition = client.item(itemId) ?: return@groundItems
                val value = definition.value().toLong() * amount
                if (sceneX != lastX || sceneY != lastY) {
                    stack = 0
                    lastX = sceneX
                    lastY = sceneY
                }
                if (client.projectTile(sceneX, sceneY, level, 0, point)) {
                    val colour = when {
                        value >= 10_000 -> 0xFFFF9040.toInt()
                        value >= 1_000 -> 0xFF40FF40.toInt()
                        else -> 0xFFFFFFFF.toInt()
                    }
                    val text = definition.name() + (if (amount > 1) " (" + amount + ")" else "") + " " + value
                    g.textCentered(view.x + point[0], view.y + point[1] - 12 * stack, text, colour, 0xFF000000.toInt())
                    stack++
                }
            }
        }
    }
}
