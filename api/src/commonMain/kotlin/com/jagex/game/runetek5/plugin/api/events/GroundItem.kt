package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * An item on the ground, for [Events.ITEM_SPAWNED] and [Events.ITEM_DESPAWNED]: at tile ([sceneX], [sceneY]) of the
 * scene, the same tiles as an actor's [x][com.jagex.game.runetek5.plugin.api.Actor.x] shifted right 9. A change in a
 * pile's amount is a despawn of the old amount followed at once by a spawn of the new; after a scene rebuild (a
 * new area) the server sends every item again, each a spawn. Nothing says who dropped an item or who picked it up.
 */
class GroundItem(@JvmField val itemId: Int, @JvmField val amount: Int, @JvmField val sceneX: Int, @JvmField val sceneY: Int, @JvmField val level: Int)
