package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** An item on the ground, for [Events.ITEM_SPAWNED] and [Events.ITEM_DESPAWNED]: at tile ([sceneX], [sceneY]) of the scene. */
class GroundItem(@JvmField val itemId: Int, @JvmField val amount: Int, @JvmField val sceneX: Int, @JvmField val sceneY: Int, @JvmField val level: Int)
