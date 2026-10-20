package recipes

import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.plugin.api.events.Events

/**
 * Inventory and bank tags: right-click an item anywhere in an interface and choose "Tag" to give
 * it a colour, "Untag" to take it away; every slot showing a tagged item, in the backpack (149:0)
 * or the bank's item grid (762:95), is outlined in it. The slots are the containers' dynamic
 * children ([Widget.child]), placed with [Widget.screenX]; the entry comes from
 * [Events.MENU_ENTRY_ADDED] for [MenuTarget.WIDGET].
 */
class ItemTags : Plugin {
    private val colours = HashMap<Int, Int>()
    private val palette = intArrayOf(0xFFFF4040.toInt(), 0xFF40FF40.toInt(), 0xFF4080FF.toInt(), 0xFFFFFF40.toInt())
    private var next = 0

    override fun start(context: PluginContext) {
        val client = context.client()
        for (entry in context.config().get("tags", "").split(';')) {
            val parts = entry.split('=')
            if (parts.size == 2) colours[parts[0].toInt()] = parts[1].toInt()
        }
        fun save() {
            context.config().set("tags", colours.entries.joinToString(";") { it.key.toString() + "=" + it.value })
            context.config().save()
        }
        val tag = context.registerMenuAction { _, _, _, itemId ->
            colours[itemId] = palette[next++ % palette.size]
            save()
        }
        val untag = context.registerMenuAction { _, _, _, itemId ->
            colours.remove(itemId)
            save()
        }
        context.events().subscribe(Events.MENU_ENTRY_ADDED) { event ->
            if (event.target === MenuTarget.WIDGET && event.itemId >= 0) {
                client.addMenuEntry(if (colours.containsKey(event.itemId)) "Untag" else "Tag", if (colours.containsKey(event.itemId)) untag else tag)
            }
        }
        context.addOverlay { g, _ ->
            if (colours.isEmpty()) return@addOverlay
            outline(client.widget(149, 0), g)
            outline(client.widget(762, 95), g)
        }
    }

    private fun outline(container: Widget?, g: com.jagex.game.runetek5.plugin.api.Graphics) {
        if (container == null || container.isHidden()) return
        for (i in 0 until container.childCount()) {
            val slot = container.child(i) ?: continue
            val colour = colours[slot.itemId()] ?: continue
            g.drawRect(slot.screenX(), slot.screenY(), slot.width(), slot.height(), colour)
        }
    }
}
