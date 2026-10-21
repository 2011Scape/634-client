package recipes

import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.events.Events

/**
 * Npc indicators: "Tag" on an npc's right-click menu (or `::tag <name>`) outlines every npc of
 * that name on its tiles and names it over its head, red while it fights you and cyan otherwise;
 * "Untag" (or `::untag <name>`) forgets it. The menu entry comes from [Events.MENU_ENTRY_ADDED] for
 * [MenuTarget.NPC], whose id is the npc's index. The npcs are walked each frame through
 * [com.jagex.game.runetek5.plugin.api.ClientApi.npc], their tiles come from `tilePolygon`, their
 * heads from `projectActor`.
 */
class NpcIndicators : Plugin {
    private val names = ArrayList<String>()
    private val xs = IntArray(4)
    private val ys = IntArray(4)
    private val head = IntArray(2)

    override fun start(context: PluginContext) {
        val client = context.client()
        names.addAll(context.config().get("names", "").split(';').filter { it.isNotEmpty() })
        fun save() {
            context.config().set("names", names.joinToString(";"))
            context.config().save()
        }
        fun tagged(name: String) = names.any { it.equals(name, ignoreCase = true) }
        fun tag(name: String) { if (!tagged(name)) { names.add(name); save() } }
        fun untag(name: String) { names.removeAll { it.equals(name, ignoreCase = true) }; save() }
        context.registerCommand("tag") { _, args -> if (args.isNotEmpty()) tag(args.joinToString(" ")) }
        context.registerCommand("untag") { _, args -> untag(args.joinToString(" ")) }
        // the right-click menu: Tag on an npc not yet tagged, Untag on one that is; the id is Npc.index, so npcByIndex, not npc(i)
        val tagAction = context.registerMenuAction { _, _, _, index -> client.npcByIndex(index)?.name()?.let { tag(it) } }
        val untagAction = context.registerMenuAction { _, _, _, index -> client.npcByIndex(index)?.name()?.let { untag(it) } }
        context.events().subscribe(Events.MENU_ENTRY_ADDED) { event ->
            if (event.target === MenuTarget.NPC) {
                val name = client.npcByIndex(event.id)?.name()
                if (name != null) client.addMenuEntry(if (tagged(name)) "Untag" else "Tag", if (tagged(name)) untagAction else tagAction)
            }
        }
        context.addOverlay { g, view ->
            val me = client.localPlayer()
            for (i in 0 until client.npcCount()) {
                val npc = client.npc(i) ?: continue
                val name = npc.name() ?: continue
                if (!tagged(name)) continue
                val colour = if (npc.interacting() === me) 0xFFFF4040.toInt() else 0xFF40FFFF.toInt()
                val tileX = npc.x() shr 9
                val tileY = npc.z() shr 9
                if (client.tilePolygon(tileX, tileY, npc.level(), npc.sizeInTiles(), xs, ys)) {
                    for (c in 0 until 4) { // view-relative corners onto the screen
                        xs[c] += view.x
                        ys[c] += view.y
                    }
                    g.fillPolygon(xs, ys, 4, (colour and 0x00FFFFFF) or 0x30000000)
                    g.polygon(xs, ys, 4, colour)
                }
                if (client.projectActor(npc, npc.overheadHeight() + 40, head)) {
                    g.textCentered(view.x + head[0], view.y + head[1], name + " (" + npc.combatLevel() + ")", colour, 0xFF000000.toInt())
                }
            }
        }
    }
}
