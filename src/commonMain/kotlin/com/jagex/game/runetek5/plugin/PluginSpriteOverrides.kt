package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the plugins' sprite overrides (ClientApi.overrideSprite), RuneLite's
 * Client.getSpriteOverrides. Component.getSprite asks here before the cache, so an interface showing an
 * overridden sprite draws the plugin's picture instead (made for the active toolkit by PluginImage).
 */
object PluginSpriteOverrides {

	private val overrides: HashMap<Int, PluginImage> = HashMap()

	/** [image] in place of sprite [spriteId], or the cache's own back for null. */
	@JvmStatic
	fun set(spriteId: Int, image: PluginImage?) {
		val changed: Boolean = if (image == null) overrides.remove(spriteId) != null else overrides.put(spriteId, image) !== image
		if (changed) {
			// the interfaces keep the sprites they made: drop them so the change shows
			Component.NATIVE_SPRITE_CACHE.reset()
		}
	}

	/** The sprite to draw for [spriteId], or null for the cache's own. */
	@JvmStatic
	fun sprite(spriteId: Int): NativeSprite? {
		if (overrides.isEmpty()) {
			return null
		}
		return overrides[spriteId]?.sprite()
	}

	@JvmStatic
	fun clear() {
		if (overrides.isNotEmpty()) {
			overrides.clear()
			Component.NATIVE_SPRITE_CACHE.reset()
		}
	}
}
