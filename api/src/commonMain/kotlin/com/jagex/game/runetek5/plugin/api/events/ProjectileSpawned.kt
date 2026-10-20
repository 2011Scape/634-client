package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * A projectile of type [id] (an id in the cache's spot animation list) flies on floor [level] from
 * ([startX], [startY]) to ([endX], [endY]), in scene units (512 to a tile), leaving at client cycle
 * [startCycle] and landing at [endCycle]. [sourceIndex] and [targetIndex] say who shot and who is
 * hit, as the server encodes them: 0 for nobody, an npc's index plus one, or a player's index plus
 * one, negated.
 */
class ProjectileSpawned(@JvmField val id: Int, @JvmField val level: Int, @JvmField val startX: Int, @JvmField val startY: Int, @JvmField val endX: Int, @JvmField val endY: Int, @JvmField val startCycle: Int, @JvmField val endCycle: Int, @JvmField val sourceIndex: Int, @JvmField val targetIndex: Int)
