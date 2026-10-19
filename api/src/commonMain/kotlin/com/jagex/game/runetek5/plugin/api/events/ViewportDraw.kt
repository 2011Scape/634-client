package com.jagex.game.runetek5.plugin.api.events

import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField

/** See [Events.VIEWPORT_DRAW]: the game view's place, and what to draw with. One reused instance. */
class ViewportDraw(@JvmField val graphics: Graphics, @JvmField val view: Viewport)
