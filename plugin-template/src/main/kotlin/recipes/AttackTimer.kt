package recipes

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Font
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.events.Events

/**
 * Attack and idle timers: a bar under the player counts down the equipped weapon's cooldown from
 * the last attack animation, and once the player has neither animated nor fought for a while the
 * bar says "Idle" and a sound plays. The weapon is slot 3 of the worn equipment (container 94), its
 * speed the item's parameter 14 in ticks (4 when unarmed or unknown), read again whenever the
 * equipment changes. Only an attack restarts the bar: an animation that arrives in the same cycle
 * as a hit on the player while the player is fighting nobody is the block animation, remembered by
 * id and ignored from then on. Everything else is read from the local player each
 * [Events.GAME_TICK], which is a client cycle of 20 ms (a tick of the server is 30 of them).
 */
class AttackTimer : Plugin {
    private val equipment = 94
    private val weaponSlot = 3
    private val attackSpeedParam = 14
    private val defaultTicks = 4
    private val cyclesPerTick = 30
    private val idleCycles = 15 * 50
    private var attackTicks = defaultTicks
    private var weaponName = "Unarmed"
    private val blockAnimations = HashSet<Int>()
    private var hitCycle = -100
    private var cooldown = 0
    private var idleTicks = 0
    private var warned = false
    private val point = IntArray(2)

    override fun start(context: PluginContext) {
        val client = context.client()
        readWeapon(client, context)
        context.events().subscribe(Events.ITEM_CONTAINER_CHANGED) { event ->
            if (event.containerId == equipment) readWeapon(client, context)
        }
        context.events().subscribe(Events.HITSPLAT_APPLIED) { event ->
            if (event.actor === client.localPlayer()) hitCycle = client.clientCycle()
        }
        context.events().subscribe(Events.ANIMATION_CHANGED) { event ->
            val me = client.localPlayer()
            if (event.actor !== me || event.animationId == -1 || me == null) return@subscribe
            idleTicks = 0
            warned = false
            if (event.animationId in blockAnimations) return@subscribe
            if (client.clientCycle() - hitCycle <= 1 && me.interacting() == null) {
                blockAnimations.add(event.animationId) // hit while fighting nobody: this is the block animation
                return@subscribe
            }
            cooldown = attackTicks * cyclesPerTick
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
        context.addOverlay { g, view ->
            val me = client.localPlayer() ?: return@addOverlay
            if (!client.projectActor(me, 0, point)) return@addOverlay
            val x = view.x + point[0] - 20
            val y = view.y + point[1] + 4
            g.fillRect(x, y, 40, 5, 0xC0000000.toInt())
            if (cooldown > 0) {
                g.fillRect(x, y, 40 * cooldown / (attackTicks * cyclesPerTick), 5, 0xFFFF4040.toInt())
            } else if (idleTicks >= idleCycles) {
                g.font(Font.BOLD)
                g.textCentered(view.x + point[0], y - 4, "Idle", 0xFFFF4040.toInt(), 0xFF000000.toInt())
            } else {
                g.fillRect(x, y, 40, 5, 0xFF40FF40.toInt())
            }
        }
    }

    /** The equipped weapon's name and speed in ticks; unarmed, or a weapon without the parameter, is 4. */
    private fun readWeapon(client: ClientApi, context: PluginContext) {
        val weaponId = client.itemContainer(equipment)?.itemId(weaponSlot) ?: -1
        val weapon = if (weaponId >= 0) client.item(weaponId) else null
        weaponName = weapon?.name() ?: "Unarmed"
        val ticks = weapon?.intParam(attackSpeedParam, defaultTicks) ?: defaultTicks
        attackTicks = if (ticks in 1..10) ticks else defaultTicks
        context.log("weapon " + weaponName + ": " + attackTicks + " ticks")
    }
}
