import com.jagex.game.runetek5.cache.node.JavaSurface;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!js")
public final class JavaDefaultSurface extends JavaSurface {

	@OriginalMember(owner = "client!js", name = "C", descriptor = "Ljava/awt/Shape;")
	private Shape clip;

	@OriginalMember(owner = "client!js", name = "E", descriptor = "Ljava/awt/Canvas;")
	private Canvas canvas;

	@OriginalMember(owner = "client!js", name = "F", descriptor = "Ljava/awt/Image;")
	private Image image;

	@OriginalMember(owner = "client!js", name = "G", descriptor = "Ljava/awt/Rectangle;")
	private Rectangle clipBox;

	@OriginalMember(owner = "client!js", name = "a", descriptor = "(IIILjava/awt/Graphics;IIII)V")
	@Override
	public void clip(@OriginalArg(0) int dstY, @OriginalArg(1) int x, @OriginalArg(2) int height, @OriginalArg(3) Graphics graphics, @OriginalArg(5) int dstX, @OriginalArg(6) int width, @OriginalArg(7) int srcY) {
		this.clip = graphics.getClip();
		this.clipBox.x = dstX;
		this.clipBox.width = width;
		this.clipBox.y = dstY;
		this.clipBox.height = height;
		graphics.setClip(this.clipBox);
		graphics.drawImage(this.image, dstX - x, -srcY + dstY, this.canvas);
		graphics.setClip(this.clip);
	}

	@OriginalMember(owner = "client!js", name = "a", descriptor = "(Ljava/awt/Canvas;III)V")
	@Override
	public void initialize(@OriginalArg(0) Canvas canvas2, @OriginalArg(1) int width, @OriginalArg(3) int height) {
		this.canvas = canvas2;
		this.clipBox = new Rectangle();
		this.width = width;
		this.height = height;
		this.raster = new int[this.height * this.width];
		@Pc(30) DataBufferInt dataBufferInt = new DataBufferInt(this.raster, this.raster.length);
		@Pc(48) DirectColorModel directColorModel = new DirectColorModel(32, 16711680, 65280, 255);
		@Pc(58) WritableRaster writableRaster = Raster.createWritableRaster(directColorModel.createCompatibleSampleModel(this.width, this.height), dataBufferInt, null);
		this.image = new BufferedImage(directColorModel, writableRaster, false, new Hashtable());
	}
}
