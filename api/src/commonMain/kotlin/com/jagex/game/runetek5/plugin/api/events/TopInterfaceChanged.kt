package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** The root interface changed from [previous] to [interfaceId]: 548 is the fixed game screen, 746 the resizable one. */
class TopInterfaceChanged(@JvmField val previous: Int, @JvmField val interfaceId: Int)
