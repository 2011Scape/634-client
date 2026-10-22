package com.jagex.game.runetek5.plugin.api

/**
 * A picture of the plugin's own, from [ClientApi.loadImage], for [Graphics.image] and an [InfoBox]: what a
 * RuneLite plugin keeps as a BufferedImage. Since API 12.
 */
interface Image {
	fun width(): Int

	fun height(): Int
}
