package com.jagex.game.runetek5.client.features

import awt.image.BufferedImage
import io.File
import io.IOException

/*
 * Not part of the original client: the browser side of SmokeTest's image files (milestone 4). A page
 * cannot write or read PNG files on disk; the browser smoke test (milestone 6) will report its frame
 * another way. Until then a smoke test that needs them fails with this.
 */
actual fun writePng(image: BufferedImage, file: File) {
	throw IOException("cannot save " + file.getPath() + " in a browser")
}

actual fun readImage(file: File): BufferedImage? {
	throw IOException("cannot read " + file.getPath() + " in a browser")
}
