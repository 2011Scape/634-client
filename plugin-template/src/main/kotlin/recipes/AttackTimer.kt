package recipes

import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.events.Events

/**
 * Attack and idle timers: a bar under the player counts down the weapon's cooldown from the
 * last attack animation (`::attackspeed <ticks>` sets it, 4 by default), and once the player has
 * neither animated nor fought for a while the bar says "Idle" and a sound plays. Everything is
 * read from the local player each [Events.GAME_TICK], which is a client cycle of 20 ms (a tick of
 * the server is 30 of them); the animation comes as [Events.ANIMATION_CHANGED].
 */
class AttackTimer : Plugin {
    private val cyclesPerTick = 30
    private val idleCycles = 15 * 50
    private var attackTicks = 4
    private var cooldown = 0
    private var idleTicks = 0
    private var warned = false
    private val point = IntArray(2)

    override fun start(context: PluginContext) {
        val client = context.client()
        attackTicks = context.config().getInt("attackTicks", 4)
        context.registerCommand("attackspeed") { _, args ->
            attackTicks = args.firstOrNull()?.toIntOrNull() ?: 4
            context.config().setInt("attackTicks", attackTicks)
            context.config().save()
        }
        context.events().subscribe(Events.ANIMATION_CHANGED) { event ->
            if (event.actor === client.localPlayer() && event.animationId != -1) {
                cooldown = attackTicks * cyclesPerTick
                idleTicks = 0
                warned = false
            }
        }
        context.events().subscribe(Events.GAME_TICK) { _ ->
            if (cooldown > 0) cooldown--
            val me = client.localPlayer()
            if (me != null && me.animationId() == -1 && me.interacting() == null) {
                idleTicks++
                if (idleTicks == idleCycles && !warned) {
                    warned = true
                    client.chatMessage("You have been idle for 15 seconds.")
                    client.playSound(2266, 255)
                }
            } else {
                idleTicks = 0
            }
        }
        context.addOverlay { g, _ ->
            val me = client.localPlayer() ?: return@addOverlay
            if (!client.projectActor(me, 0, point)) return@addOverlay
            val x = point[0] - 20
            val y = point[1] + 4
            g.fillRect(x, y, 40, 5, 0xC0000000.toInt())
            if (cooldown > 0) {
                g.fillRect(x, y, 40 * cooldown / (attackTicks * cyclesPerTick), 5, 0xFFFF4040.toInt())
            } else if (idleTicks >= idleCycles) {
                g.font(Font.BOLD)
                g.textCentered(point[0], y - 4, "Idle", 0xFFFF4040.toInt(), 0xFF000000.toInt())
            } else {
                g.fillRect(x, y, 40, 5, 0xFF40FF40.toInt())
            }
        }
    }
}
