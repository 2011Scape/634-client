import java.awt.image.BufferedImage
import java.awt.image.Raster
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.imageio.ImageIO
import javax.imageio.ImageReader

/* Not part of the original client: the JVM side of decodeJpegPixels, through ImageIO. */
actual fun decodeJpegPixels(imageData: ByteArray): JpegPixels? {
	val reader: ImageReader = ImageIO.getImageReadersByFormatName("jpeg").next()
	try {
		reader.setInput(ImageIO.createImageInputStream(ByteArrayInputStream(imageData)))
		val raster: Raster = reader.readRaster(0, null)
		val width: Int = raster.getWidth()
		val height: Int = raster.getHeight()
		if (width > 0 && height > 0) {
			val pixels: IntArray = IntArray(width * height)
			if (raster.getNumBands() == 4) {
				val samples: IntArray = raster.getPixels(0, 0, width, height, null as IntArray?)
				for (i in 0 until pixels.size) {
					val y: Double = samples[i * 4].toDouble()
					val cb: Double = (samples[i * 4 + 1] - 128).toDouble()
					val cr: Double = (samples[i * 4 + 2] - 128).toDouble()
					val red: Int = JpegSpriteDecoder.clamp(y + 1.402 * cr)
					val green: Int = JpegSpriteDecoder.clamp(y - 0.344136 * cb - 0.714136 * cr)
					val blue: Int = JpegSpriteDecoder.clamp(y + 1.772 * cb)
					pixels[i] = (samples[i * 4 + 3] shl 24) or (red shl 16) or (green shl 8) or blue
				}
			} else {
				val image: BufferedImage? = ImageIO.read(ByteArrayInputStream(imageData))
				if (image == null) {
					throw IOException()
				}
				image.getRGB(0, 0, width, height, pixels, 0, width)
			}
			return JpegPixels(width, pixels, height)
		}
	} finally {
		reader.dispose()
	}
	return null
}
