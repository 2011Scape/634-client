package com.jagex.game.runetek5.plugin

/* Not part of the original client: the browser has no synchronous PNG decoder, so plugins show no icon there. */
actual fun decodePngPixels(data: ByteArray): PluginIcon? {
	return null
}
