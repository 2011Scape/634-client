package com.jagex.game.runetek5.plugin.api.events

import com.jagex.game.runetek5.plugin.api.Actor
import kotlin.jvm.JvmField

/**
 * The server hit [actor]: a splat of [type] (an id in the cache's hit mark list, -1 for none) for
 * [damage], with a second splat of [soakType] for the [soakDamage] absorbed (-1 and 0 when none),
 * shown after [delay] client cycles. Posted before the actor's hit splat slots take it, which they
 * may not when all four show a bigger hit.
 */
class HitsplatApplied(@JvmField val actor: Actor, @JvmField val type: Int, @JvmField val damage: Int, @JvmField val soakType: Int, @JvmField val soakDamage: Int, @JvmField val delay: Int)
