import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import java.awt.Image
import java.awt.MediaTracker
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.awt.image.PixelGrabber
import java.awt.image.Raster
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ClientState

object JpegSpriteDecoder {

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "([BI)Lclient!mi;")
	@JvmStatic
	fun decode(@OriginalArg(0) imageData: ByteArray?): NativeSprite? {
		if (imageData == null) {
			throw RuntimeException("")
		}
		// Not part of the original client: the cache's JPEGs carry four channels, Y, Cb, Cr and alpha,
		// with no marker naming the colour space. Current JDKs cannot decode that through the Toolkit
		// path below, and ImageIO takes it for CMYK, so the samples are read raw and converted here.
		try {
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
					return ClientState.activeToolkit!!.createNativeSprite(width, pixels, width, height)
				}
			} finally {
				reader.dispose()
			}
		} catch (ex: IOException) {
			/* fall back to the Toolkit path */
		} catch (ex: RuntimeException) {
			/* fall back to the Toolkit path */
		}
		while (true) {
			try {
				@Pc(19) val image2: Image = Toolkit.getDefaultToolkit().createImage(imageData)
				@Pc(24) val mediaTracker: MediaTracker = MediaTracker(ActiveClient.activeClient)
				mediaTracker.addImage(image2, 0)
				mediaTracker.waitForAll()
				@Pc(34) val imageWidth: Int = image2.getWidth(ActiveClient.activeClient)
				@Pc(38) val imageHeight: Int = image2.getHeight(ActiveClient.activeClient)
				if (!mediaTracker.isErrorAny() && imageWidth >= 0 && imageHeight >= 0) {
					@Pc(58) val imagePixels: IntArray = IntArray(imageHeight * imageWidth)
					@Pc(70) val pixelGrabber: PixelGrabber = PixelGrabber(image2, 0, 0, imageWidth, imageHeight, imagePixels, 0, imageWidth)
					pixelGrabber.grabPixels()
					return ClientState.activeToolkit!!.createNativeSprite(imageWidth, imagePixels, imageWidth, imageHeight)
				}
				throw RuntimeException("")
			} catch (@Pc(83) ex2: InterruptedException) {
			}
		}
	}

	// Not part of the original client: a colour sample held to 0..255.
	@JvmStatic
	fun clamp(value: Double): Int {
		val v: Int = Math.round(value).toInt()
		return if (v < 0) 0 else if (v > 255) 255 else v
	}
}
