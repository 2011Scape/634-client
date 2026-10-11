import com.jagex.game.runetek5.cache.node.JavaSurface;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Shape;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.Hashtable;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!kc")
public final class JavaFallbackSurface extends JavaSurface implements ImageProducer {

	@OriginalMember(owner = "client!kc", name = "I", descriptor = "Ljava/awt/Canvas;")
	private Canvas canvas;

	@OriginalMember(owner = "client!kc", name = "K", descriptor = "Ljava/awt/Image;")
	private Image image;

	@OriginalMember(owner = "client!kc", name = "N", descriptor = "Ljava/awt/image/ColorModel;")
	private ColorModel colorModel;

	@OriginalMember(owner = "client!kc", name = "S", descriptor = "Ljava/awt/image/ImageConsumer;")
	private ImageConsumer imageConsumer;

	@OriginalMember(owner = "client!kc", name = "a", descriptor = "(IIIII)V")
	private synchronized void setPixelRegion(@OriginalArg(0) int height, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int width) {
		if (this.imageConsumer != null) {
			this.imageConsumer.setPixels(x, y, width, height, this.colorModel, super.raster, y * super.width + x, super.width);
			this.imageConsumer.imageComplete(2);
		}
	}

	@OriginalMember(owner = "client!kc", name = "removeConsumer", descriptor = "(Ljava/awt/image/ImageConsumer;)V")
	@Override
	public synchronized void removeConsumer(@OriginalArg(0) ImageConsumer consumer) {
		if (consumer == this.imageConsumer) {
			this.imageConsumer = null;
		}
	}

	@OriginalMember(owner = "client!kc", name = "startProduction", descriptor = "(Ljava/awt/image/ImageConsumer;)V")
	@Override
	public void startProduction(@OriginalArg(0) ImageConsumer consumer) {
		this.addConsumer(consumer);
	}

	@OriginalMember(owner = "client!kc", name = "a", descriptor = "(B)V")
	private synchronized void setPixels() {
		if (this.imageConsumer != null) {
			this.imageConsumer.setPixels(0, 0, super.width, super.height, this.colorModel, super.raster, 0, super.width);
			this.imageConsumer.imageComplete(2);
		}
	}

	@OriginalMember(owner = "client!kc", name = "addConsumer", descriptor = "(Ljava/awt/image/ImageConsumer;)V")
	@Override
	public synchronized void addConsumer(@OriginalArg(0) ImageConsumer consumer) {
		this.imageConsumer = consumer;
		consumer.setDimensions(super.width, super.height);
		consumer.setProperties(null);
		consumer.setColorModel(this.colorModel);
		consumer.setHints(14);
	}

	@OriginalMember(owner = "client!kc", name = "isConsumer", descriptor = "(Ljava/awt/image/ImageConsumer;)Z")
	@Override
	public synchronized boolean isConsumer(@OriginalArg(0) ImageConsumer consumer) {
		return this.imageConsumer == consumer;
	}

	@OriginalMember(owner = "client!kc", name = "a", descriptor = "(IIILjava/awt/Graphics;IIII)V")
	@Override
	public void clip(@OriginalArg(0) int dstY, @OriginalArg(1) int x, @OriginalArg(2) int height, @OriginalArg(3) Graphics graphics, @OriginalArg(5) int dstX, @OriginalArg(6) int width, @OriginalArg(7) int y) {
		this.setPixelRegion(height, x, y, width);
		@Pc(26) Shape shape = graphics.getClip();
		graphics.clipRect(dstX, dstY, width, height);
		graphics.drawImage(this.image, dstX - x, -y + dstY, this.canvas);
		graphics.setClip(shape);
	}

	@OriginalMember(owner = "client!kc", name = "a", descriptor = "(Ljava/awt/Canvas;III)V")
	@Override
	public void initialize(@OriginalArg(0) Canvas canvas, @OriginalArg(1) int width, @OriginalArg(3) int height) {
		super.height = height;
		super.width = width;
		this.canvas = canvas;
		super.raster = new int[super.width * super.height];
		this.colorModel = new DirectColorModel(32, 16711680, 65280, 255);
		this.image = this.canvas.createImage(this);
		this.setPixels();
		this.canvas.prepareImage(this.image, this.canvas);
		this.setPixels();
		this.canvas.prepareImage(this.image, this.canvas);
		this.setPixels();
		this.canvas.prepareImage(this.image, this.canvas);
	}

	@OriginalMember(owner = "client!kc", name = "requestTopDownLeftRightResend", descriptor = "(Ljava/awt/image/ImageConsumer;)V")
	@Override
	public void requestTopDownLeftRightResend(@OriginalArg(0) ImageConsumer consumer) {
	}
}
