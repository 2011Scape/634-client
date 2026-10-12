package com.jagex.game.runetek5.client.features

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/* Not part of the original client: the JVM side of SmokeTest's image files, through ImageIO. */

actual fun writePng(image: BufferedImage, file: File) {
	ImageIO.write(image, "png", file)
}

actual fun readImage(file: File): BufferedImage? {
	return ImageIO.read(file)
}
