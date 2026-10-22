package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the plugins' chat icons (ClientApi.addChatIcon), RuneLite's
 * ChatIconManager. They follow the game's own name icons (the crowns) in StaticMedia.nameIconSprites,
 * which interface text draws `<img=N>` from, and so in the chat icons the menu and the mouse-over text use
 * (StaticMedia.initializeChatIcons copies them, the quest icons going after). The game makes its name
 * icons again for each toolkit (StaticMedia.loadSprites), and [loaded] puts the plugins' back after them.
 */
object PluginChatIcons {

	/** The most icons the plugins may add. */
	private const val MAX_ICONS: Int = 1024

	private val images: ArrayList<PluginImage> = ArrayList()

	/** How many name icons the game has, -1 before they are loaded. */
	private var gameIcons: Int = -1

	/** The game's name icons were made (for a toolkit): the plugins' are added after them. */
	@JvmStatic
	fun loaded() {
		gameIcons = StaticMedia.nameIconSprites?.size ?: return
		extend()
	}

	/** [image] added; its `<img=N>` number, or -1 while the game's icons are not loaded or there are too many. */
	@JvmStatic
	fun add(image: PluginImage): Int {
		if (gameIcons < 0 || StaticMedia.nameIconSprites == null || images.size >= MAX_ICONS) {
			return -1
		}
		images.add(image)
		extend()
		StaticMedia.initializeChatIcons(StaticMedia.nameIconSprites)
		return gameIcons + images.size - 1
	}

	/** The name icons: the game's, then the plugins' made for the active toolkit. */
	private fun extend() {
		val current: Array<NativeSprite?> = StaticMedia.nameIconSprites ?: return
		val icons: Array<NativeSprite?> = arrayOfNulls(gameIcons + images.size)
		current.copyInto(icons, 0, 0, minOf(gameIcons, current.size))
		for (i in images.indices) {
			icons[gameIcons + i] = images[i].sprite()
		}
		StaticMedia.nameIconSprites = icons
	}
}
