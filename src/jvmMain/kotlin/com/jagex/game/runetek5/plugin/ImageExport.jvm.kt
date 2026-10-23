package com.jagex.game.runetek5.plugin

import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

/* Not part of the original client: the desktop side of saveImagePng and copyImageToClipboard. */

private fun image(width: Int, height: Int, pixels: IntArray): BufferedImage {
	val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
	image.setRGB(0, 0, width, height, pixels, 0, width)
	return image
}

actual fun saveImagePng(width: Int, height: Int, pixels: IntArray, folder: String, name: String): String? {
	val directory = File(File(System.getProperty("user.home"), "Pictures"), "634-client" + File.separator + folder)
	if (!directory.isDirectory && !directory.mkdirs()) {
		return null
	}
	val file = File(directory, "$name.png")
	return try {
		if (ImageIO.write(image(width, height, pixels), "png", file)) file.path else null
	} catch (error: IOException) {
		lang.printlnErr("Cannot save " + file + ": " + error)
		null
	}
}

/** RuneLite's ImageCapture's clipboard image. */
private class ImageSelection(private val image: Image) : Transferable {
	override fun getTransferDataFlavors(): Array<DataFlavor> = arrayOf(DataFlavor.imageFlavor)

	override fun isDataFlavorSupported(flavor: DataFlavor): Boolean = DataFlavor.imageFlavor.equals(flavor)

	override fun getTransferData(flavor: DataFlavor): Any {
		if (!isDataFlavorSupported(flavor)) {
			throw UnsupportedFlavorException(flavor)
		}
		return image
	}
}

@Suppress("TooGenericExceptionCaught", "SwallowedException") // a headless or locked clipboard is only "not copied"
actual fun copyImageToClipboard(width: Int, height: Int, pixels: IntArray): Boolean {
	return try {
		Toolkit.getDefaultToolkit().systemClipboard.setContents(ImageSelection(image(width, height, pixels)), null)
		true
	} catch (error: Exception) {
		false
	}
}
