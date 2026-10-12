import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import awt.Image
import awt.MediaTracker
import awt.Toolkit
import awt.image.PixelGrabber
import io.ByteArrayInputStream
import io.IOException
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic
import lang.InterruptedException
import awt.getHeight
import awt.getWidth

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
			val raw: JpegPixels? = decodeJpegPixels(imageData)
			if (raw != null) {
				return ClientState.activeToolkit!!.createNativeSprite(raw.width, raw.pixels, raw.width, raw.height)
			}
		} catch (ex: IOException) {
			/* fall back to the Toolkit path */
		} catch (ex: RuntimeException) {
			/* fall back to the Toolkit path */
		}
		while (true) {
			try {
				@Pc(19) val image2: Image = awt.getDefaultToolkit().createImage(imageData)
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
		val v: Int = lang.round(value).toInt()
		return if (v < 0) 0 else if (v > 255) 255 else v
	}
}
