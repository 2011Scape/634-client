package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** See [Events.STAT_CHANGED]. [oldXp] is what the client had before this update. */
class StatChanged(@JvmField val skill: Int, @JvmField val oldXp: Int, @JvmField val newXp: Int, @JvmField val level: Int)
