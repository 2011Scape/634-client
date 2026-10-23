package com.jagex.game.runetek5.plugin

import kotlinx.browser.document
import kotlinx.browser.window
import org.khronos.webgl.Uint8ClampedArray
import org.khronos.webgl.set
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.url.URL

/* Not part of the original client: the browser side of saveImagePng and copyImageToClipboard; a page has no disk. */

private fun canvas(width: Int, height: Int, pixels: IntArray): HTMLCanvasElement {
	val canvas = document.createElement("canvas") as HTMLCanvasElement
	canvas.width = width
	canvas.height = height
	val context = canvas.getContext("2d") as CanvasRenderingContext2D
	val data = context.createImageData(width.toDouble(), height.toDouble())
	val bytes: Uint8ClampedArray = data.data
	for (i in pixels.indices) {
		val argb: Int = pixels[i]
		bytes[i * 4] = ((argb ushr 16) and 0xFF).toByte()
		bytes[i * 4 + 1] = ((argb ushr 8) and 0xFF).toByte()
		bytes[i * 4 + 2] = (argb and 0xFF).toByte()
		bytes[i * 4 + 3] = 0xFF.toByte()
	}
	context.putImageData(data, 0.0, 0.0)
	return canvas
}

/** A download named after the folder and the name, as the browser keeps no folders for a page. */
actual fun saveImagePng(width: Int, height: Int, pixels: IntArray, folder: String, name: String): String? {
	val fileName: String = (folder.replace('/', ' ') + " " + name + ".png").trim()
	canvas(width, height, pixels).toBlob({ blob ->
		if (blob != null) {
			val url: String = URL.createObjectURL(blob)
			val anchor = document.createElement("a") as HTMLAnchorElement
			anchor.href = url
			anchor.download = fileName
			anchor.click()
			window.setTimeout({ URL.revokeObjectURL(url) }, REVOKE_MILLIS)
		}
	}, "image/png")
	return fileName
}

/** The asynchronous Clipboard API, where the page may write an image; true when it was asked. */
actual fun copyImageToClipboard(width: Int, height: Int, pixels: IntArray): Boolean {
	val clipboard: dynamic = window.navigator.asDynamic().clipboard
	val item: dynamic = js("typeof ClipboardItem === 'undefined' ? null : ClipboardItem")
	if (clipboard == null || clipboard.write == null || item == null) {
		return false
	}
	canvas(width, height, pixels).toBlob({ blob ->
		if (blob != null) {
			val entries: dynamic = js("({})")
			entries["image/png"] = blob
			val clipboardItem: dynamic = js("Reflect").construct(item, arrayOf(entries))
			clipboard.write(arrayOf(clipboardItem)).catch { _: dynamic -> }
		}
	}, "image/png")
	return true
}

private const val REVOKE_MILLIS: Int = 10_000
