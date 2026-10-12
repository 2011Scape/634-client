package com.jagex.game.runetek5.client.features

import awt.image.BUFFERED_IMAGE_TYPE_INT_RGB
import awt.image.BufferedImage
import io.File
import io.IOException
import io.MemFile
import io.MemFs
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.Uint8ClampedArray
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.fetch.RequestInit
import kotlin.js.Promise

/*
 * Not part of the original client: SmokeTest's image files in a browser (milestone 6). A page has no
 * disk. A saved frame is uploaded to the development server (tools/serve.mjs keeps it in
 * build/smoke-browser), and the reference frames are fetched from beside the page before the client
 * starts (preloadSmokeReferences), decoded, and listed in the in-memory file system so that
 * SmokeTest's File(REFERENCE, ...).isFile() finds them.
 */

/* The decoded reference frames, by their normalised path. */
private val references: MutableMap<String, BufferedImage> = mutableMapOf()

actual fun writePng(image: BufferedImage, file: File) {
	val canvas: HTMLCanvasElement = image.source.unsafeCast<HTMLCanvasElement>()
	val name: String = file.getName()
	canvas.toBlob({ blob ->
		if (blob != null) {
			window.fetch("smoke-shots/" + name, RequestInit(method = "PUT", body = blob)).catch { }
		}
	}, "image/png")
}

actual fun readImage(file: File): BufferedImage? {
	return references[MemFs.normalise(file.getPath())] ?: throw IOException("no reference frame " + file.getPath() + " was fetched")
}

/*
 * Fetches the reference frames SmokeTest may look for in dir (served at the same path beside the
 * page): smoke-toolkit<id><suffix>.png and its alternates .2.png, .3.png, ... for the toolkits a
 * browser has (0, 1, 2), stopping a series at the first one missing.
 */
suspend fun preloadSmokeReferences(dir: String) {
	val suffixes: Array<String> = arrayOf("", "-grounditems", "-npclabels", "-tilemarker", "-xpdrop")
	for (toolkit in 0..2) {
		for (suffix in suffixes) {
			var n: Int = 1
			while (true) {
				val name: String = "smoke-toolkit" + toolkit + suffix + (if (n == 1) "" else "." + n) + ".png"
				val image: BufferedImage = fetchImage(dir.removePrefix("/") + "/" + name) ?: break
				val path: String = MemFs.normalise(dir + "/" + name)
				references[path] = image
				MemFs.mkdirs(MemFs.parentOf(path))
				MemFs.files[path] = MemFile()
				n++
			}
		}
	}
	println("SMOKE " + references.size + " reference frames fetched from " + dir)
}

private suspend fun fetchImage(url: String): BufferedImage? {
	try {
		val response: dynamic = window.fetch(url).await()
		if (response.ok != true) {
			return null
		}
		val blob: dynamic = (response.blob() as Promise<dynamic>).await()
		val bitmap: dynamic = (window.asDynamic().createImageBitmap(blob) as Promise<dynamic>).await()
		val width: Int = bitmap.width as Int
		val height: Int = bitmap.height as Int
		val canvas: HTMLCanvasElement = document.createElement("canvas") as HTMLCanvasElement
		canvas.width = width
		canvas.height = height
		val context: CanvasRenderingContext2D = canvas.getContext("2d") as CanvasRenderingContext2D
		context.asDynamic().drawImage(bitmap, 0, 0)
		val rgba: Uint8ClampedArray = context.getImageData(0.0, 0.0, width.toDouble(), height.toDouble()).data
		val image: BufferedImage = BufferedImage(width, height, BUFFERED_IMAGE_TYPE_INT_RGB)
		var i: Int = 0
		for (y in 0 until height) {
			for (x in 0 until width) {
				val r: Int = rgba[i].toInt() and 0xFF
				val g: Int = rgba[i + 1].toInt() and 0xFF
				val b: Int = rgba[i + 2].toInt() and 0xFF
				image.setRGB(x, y, (0xFF shl 24) or (r shl 16) or (g shl 8) or b)
				i += 4
			}
		}
		return image
	} catch (e: Throwable) {
		return null
	}
}
