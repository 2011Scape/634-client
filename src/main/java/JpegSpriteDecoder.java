import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

public final class JpegSpriteDecoder {

	@OriginalMember(owner = "client!tp", name = "a", descriptor = "([BI)Lclient!mi;")
	public static NativeSprite decode(@OriginalArg(0) byte[] imageData) {
		if (imageData == null) {
			throw new RuntimeException("");
		}
		// Not part of the original client: the cache's JPEGs carry four channels, Y, Cb, Cr and alpha,
		// with no marker naming the colour space. Current JDKs cannot decode that through the Toolkit
		// path below, and ImageIO takes it for CMYK, so the samples are read raw and converted here.
		try {
			ImageReader reader = ImageIO.getImageReadersByFormatName("jpeg").next();
			try {
				reader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(imageData)));
				Raster raster = reader.readRaster(0, null);
				int width = raster.getWidth();
				int height = raster.getHeight();
				if (width > 0 && height > 0) {
					int[] pixels = new int[width * height];
					if (raster.getNumBands() == 4) {
						int[] samples = raster.getPixels(0, 0, width, height, (int[]) null);
						for (int i = 0; i < pixels.length; i++) {
							double y = samples[i * 4];
							double cb = samples[i * 4 + 1] - 128;
							double cr = samples[i * 4 + 2] - 128;
							int red = JpegSpriteDecoder.clamp(y + 1.402D * cr);
							int green = JpegSpriteDecoder.clamp(y - 0.344136D * cb - 0.714136D * cr);
							int blue = JpegSpriteDecoder.clamp(y + 1.772D * cb);
							pixels[i] = samples[i * 4 + 3] << 24 | red << 16 | green << 8 | blue;
						}
					} else {
						BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
						if (image == null) {
							throw new IOException();
						}
						image.getRGB(0, 0, width, height, pixels, 0, width);
					}
					return ClientState.activeToolkit.createNativeSprite(width, pixels, width, height);
				}
			} finally {
				reader.dispose();
			}
		} catch (IOException | RuntimeException ex) {
			/* fall back to the Toolkit path */
		}
		while (true) {
			try {
				@Pc(19) Image image2 = Toolkit.getDefaultToolkit().createImage(imageData);
				@Pc(24) MediaTracker mediaTracker = new MediaTracker(ActiveClient.activeClient);
				mediaTracker.addImage(image2, 0);
				mediaTracker.waitForAll();
				@Pc(34) int imageWidth = image2.getWidth(ActiveClient.activeClient);
				@Pc(38) int imageHeight = image2.getHeight(ActiveClient.activeClient);
				if (!mediaTracker.isErrorAny() && imageWidth >= 0 && imageHeight >= 0) {
					@Pc(58) int[] imagePixels = new int[imageHeight * imageWidth];
					@Pc(70) PixelGrabber pixelGrabber = new PixelGrabber(image2, 0, 0, imageWidth, imageHeight, imagePixels, 0, imageWidth);
					pixelGrabber.grabPixels();
					return ClientState.activeToolkit.createNativeSprite(imageWidth, imagePixels, imageWidth, imageHeight);
				}
				throw new RuntimeException("");
			} catch (@Pc(83) InterruptedException ex2) {
			}
		}
	}

	// Not part of the original client: a colour sample held to 0..255.
	public static int clamp(double value) {
		int v = (int) Math.round(value);
		return v < 0 ? 0 : v > 255 ? 255 : v;
	}
}
