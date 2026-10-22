package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.plugin.api.Image
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: a plugin's picture (plugin.api.Image), its ARGB pixels and the sprite
 * the active toolkit made of them, made again after a toolkit change.
 */
class PluginImage(@JvmField val width: Int, @JvmField val height: Int, @JvmField val pixels: IntArray) : Image {

	private var sprite: NativeSprite? = null

	private var spriteToolkit: Toolkit? = null

	override fun width(): Int = width

	override fun height(): Int = height

	/** The sprite for the active toolkit, or null when there is none. */
	fun sprite(): NativeSprite? {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return null
		if (toolkit !== spriteToolkit) {
			sprite = toolkit.createNativeSprite(pixels, width, width, height, false)
			spriteToolkit = toolkit
		}
		return sprite
	}

	companion object {
		/** The biggest picture accepted, either way. */
		const val MAX_SIZE: Int = 512

		/** The picture in [png], or null when it is not a PNG the platform can decode (the browser has no synchronous decoder) or is too big. */
		@JvmStatic
		fun fromPng(png: ByteArray): PluginImage? {
			if (png.isEmpty()) {
				return null
			}
			val decoded: PluginIcon = decodePngPixels(png) ?: return null
			return of(decoded.width, decoded.height, decoded.pixels)
		}

		/**
		 * The picture in one of the client's own resources: two big-endian ints, width and height, then
		 * width * height ARGB pixels, which every platform reads (the icons' format); null when missing.
		 */
		@JvmStatic
		fun fromResource(path: String): PluginImage? {
			val bytes: ByteArray = lang.readResource(path) ?: return null
			if (bytes.size < 8) {
				return null
			}
			val width: Int = int(bytes, 0)
			val height: Int = int(bytes, 4)
			if (width <= 0 || height <= 0 || width > MAX_SIZE || height > MAX_SIZE || bytes.size < 8 + width * height * 4) {
				return null
			}
			val pixels: IntArray = IntArray(width * height)
			for (i in 0 until pixels.size) {
				pixels[i] = int(bytes, 8 + i * 4)
			}
			return PluginImage(width, height, pixels)
		}

		private fun of(width: Int, height: Int, pixels: IntArray): PluginImage? {
			if (width <= 0 || height <= 0 || width > MAX_SIZE || height > MAX_SIZE || pixels.size < width * height) {
				return null
			}
			return PluginImage(width, height, pixels)
		}

		private fun int(bytes: ByteArray, at: Int): Int {
			return ((bytes[at].toInt() and 0xFF) shl 24) or ((bytes[at + 1].toInt() and 0xFF) shl 16) or ((bytes[at + 2].toInt() and 0xFF) shl 8) or (bytes[at + 3].toInt() and 0xFF)
		}
	}
}
