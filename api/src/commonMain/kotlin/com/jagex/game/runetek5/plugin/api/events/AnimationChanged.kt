package com.jagex.game.runetek5.plugin.api.events

import com.jagex.game.runetek5.plugin.api.Actor
import kotlin.jvm.JvmField

/**
 * The server told [actor] to play [animationId] (an id in the cache's sequence list, or -1 to
 * stop). Posted before the actor takes it: the actor's own [Actor.animationId] changes on its next
 * movement update, and a lower-priority animation may not replace what plays.
 */
class AnimationChanged(@JvmField val actor: Actor, @JvmField val animationId: Int)
