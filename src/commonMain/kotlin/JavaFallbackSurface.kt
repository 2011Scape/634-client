import com.jagex.game.runetek5.cache.node.JavaSurface
import awt.Canvas
import awt.Graphics
import awt.Image
import awt.Shape
import awt.image.ColorModel
import awt.image.DirectColorModel
import awt.image.ImageConsumer
import awt.image.ImageProducer
import util.Hashtable
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.Synchronized
import awt.image.setProperties

@OriginalClass("client!kc")
class JavaFallbackSurface : JavaSurface(), ImageProducer {

	@OriginalMember(owner = "client!kc", name = "I", descriptor = "Ljava/awt/Canvas;")
	private var canvas: Canvas? = null

	@OriginalMember(owner = "client!kc", name = "K", descriptor = "Ljava/awt/Image;")
	private var image: Image? = null

	@OriginalMember(owner = "client!kc", name = "N", descriptor = "Ljava/awt/image/ColorModel;")
	private var colorModel: ColorModel? = null

	@OriginalMember(owner = "client!kc", name = "S", descriptor = "Ljava/awt/image/ImageConsumer;")
	private var imageConsumer: ImageConsumer? = null

	@OriginalMember(owner = "client!kc", name = "a", descriptor = "(IIIII)V")
	@Synchronized
	private fun setPixelRegion(@OriginalArg(0) height: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) width: Int) {
		if (this.imageConsumer != null) {
			this.imageConsumer!!.setPixels(x, y, width, height, this.colorModel, this.raster, y * this.width + x, this.width)
			this.imageConsumer!!.imageComplete(2)
		}
	}

	@OriginalMember(owner = "client!kc", name = "removeConsumer", descriptor = "(Ljava/awt/image/ImageConsumer;)V")
	@Synchronized
	override fun removeConsumer(@OriginalArg(0) consumer: ImageConsumer?) {
		if (consumer === this.imageConsumer) {
			this.imageConsumer = null
		}
	}

	@OriginalMember(owner = "client!kc", name = "startProduction", descriptor = "(Ljava/awt/image/ImageConsumer;)V")
	override fun startProduction(@OriginalArg(0) consumer: ImageConsumer?) {
		this.addConsumer(consumer)
	}

	@OriginalMember(owner = "client!kc", name = "a", descriptor = "(B)V")
	@Synchronized
	private fun setPixels() {
		if (this.imageConsumer != null) {
			this.imageConsumer!!.setPixels(0, 0, this.width, this.height, this.colorModel, this.raster, 0, this.width)
			this.imageConsumer!!.imageComplete(2)
		}
	}

	@OriginalMember(owner = "client!kc", name = "addConsumer", descriptor = "(Ljava/awt/image/ImageConsumer;)V")
	@Synchronized
	override fun addConsumer(@OriginalArg(0) consumer: ImageConsumer?) {
		this.imageConsumer = consumer
		consumer!!.setDimensions(this.width, this.height)
		consumer.setProperties(null)
		consumer.setColorModel(this.colorModel)
		consumer.setHints(14)
	}

	@OriginalMember(owner = "client!kc", name = "isConsumer", descriptor = "(Ljava/awt/image/ImageConsumer;)Z")
	@Synchronized
	override fun isConsumer(@OriginalArg(0) consumer: ImageConsumer?): Boolean {
		return this.imageConsumer === consumer
	}

	@OriginalMember(owner = "client!kc", name = "a", descriptor = "(IIILjava/awt/Graphics;IIII)V")
	override fun clip(@OriginalArg(0) dstY: Int, @OriginalArg(1) x: Int, @OriginalArg(2) height: Int, @OriginalArg(3) graphics: Graphics?, @OriginalArg(5) dstX: Int, @OriginalArg(6) width: Int, @OriginalArg(7) y: Int) {
		this.setPixelRegion(height, x, y, width)
		@Pc(26) val shape: Shape? = graphics!!.getClip()
		graphics.clipRect(dstX, dstY, width, height)
		graphics.drawImage(this.image, dstX - x, -y + dstY, this.canvas)
		graphics.setClip(shape)
	}

	@OriginalMember(owner = "client!kc", name = "a", descriptor = "(Ljava/awt/Canvas;III)V")
	override fun initialize(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) width: Int, @OriginalArg(3) height: Int) {
		this.height = height
		this.width = width
		this.canvas = canvas
		this.raster = IntArray(this.width * this.height)
		this.colorModel = DirectColorModel(32, 16711680, 65280, 255)
		this.image = this.canvas!!.createImage(this)
		this.setPixels()
		this.canvas!!.prepareImage(this.image, this.canvas)
		this.setPixels()
		this.canvas!!.prepareImage(this.image, this.canvas)
		this.setPixels()
		this.canvas!!.prepareImage(this.image, this.canvas)
	}

	@OriginalMember(owner = "client!kc", name = "requestTopDownLeftRightResend", descriptor = "(Ljava/awt/image/ImageConsumer;)V")
	override fun requestTopDownLeftRightResend(@OriginalArg(0) consumer: ImageConsumer?) {
	}
}
