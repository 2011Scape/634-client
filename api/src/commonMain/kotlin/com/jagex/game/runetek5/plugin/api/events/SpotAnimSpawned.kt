package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/** A graphic of type [id] (an id in the cache's spot animation list) plays on tile ([sceneX], [sceneY]) of floor [level]. */
class SpotAnimSpawned(@JvmField val id: Int, @JvmField val sceneX: Int, @JvmField val sceneY: Int, @JvmField val level: Int)
