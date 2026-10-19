package com.jagex.game.runetek5.plugin.api.events

import com.jagex.game.runetek5.plugin.api.GameState
import kotlin.jvm.JvmField

/** See [Events.GAME_STATE_CHANGED]. */
class GameStateChanged(@JvmField val previous: GameState, @JvmField val state: GameState)
