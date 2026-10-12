import com.jagex.game.runetek5.cache.node.JavaSurface
import awt.Canvas
import awt.Graphics
import awt.Image
import awt.Point
import awt.Rectangle
import awt.Shape
import awt.image.BufferedImage
import awt.image.DataBufferInt
import awt.image.DirectColorModel
import awt.image.Raster
import awt.image.WritableRaster
import util.Hashtable
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import awt.height
import awt.width
import awt.x
import awt.y

@OriginalClass("client!js")
class JavaDefaultSurface : JavaSurface() {

	@OriginalMember(owner = "client!js", name = "C", descriptor = "Ljava/awt/Shape;")
	private var clip: Shape? = null

	@OriginalMember(owner = "client!js", name = "E", descriptor = "Ljava/awt/Canvas;")
	private var canvas: Canvas? = null

	@OriginalMember(owner = "client!js", name = "F", descriptor = "Ljava/awt/Image;")
	private var image: Image? = null

	@OriginalMember(owner = "client!js", name = "G", descriptor = "Ljava/awt/Rectangle;")
	private var clipBox: Rectangle? = null

	@OriginalMember(owner = "client!js", name = "a", descriptor = "(IIILjava/awt/Graphics;IIII)V")
	override fun clip(@OriginalArg(0) dstY: Int, @OriginalArg(1) x: Int, @OriginalArg(2) height: Int, @OriginalArg(3) graphics: Graphics?, @OriginalArg(5) dstX: Int, @OriginalArg(6) width: Int, @OriginalArg(7) srcY: Int) {
		this.clip = graphics!!.getClip()
		this.clipBox!!.x = dstX
		this.clipBox!!.width = width
		this.clipBox!!.y = dstY
		this.clipBox!!.height = height
		graphics.setClip(this.clipBox)
		graphics.drawImage(this.image, dstX - x, -srcY + dstY, this.canvas)
		graphics.setClip(this.clip)
	}

	@OriginalMember(owner = "client!js", name = "a", descriptor = "(Ljava/awt/Canvas;III)V")
	override fun initialize(@OriginalArg(0) canvas2: Canvas?, @OriginalArg(1) width: Int, @OriginalArg(3) height: Int) {
		this.canvas = canvas2
		this.clipBox = Rectangle()
		this.width = width
		this.height = height
		this.raster = IntArray(this.height * this.width)
		@Pc(30) val dataBufferInt: DataBufferInt = DataBufferInt(this.raster, this.raster!!.size)
		@Pc(48) val directColorModel: DirectColorModel = DirectColorModel(32, 16711680, 65280, 255)
		@Pc(58) val writableRaster: WritableRaster = awt.image.createWritableRaster(directColorModel.createCompatibleSampleModel(this.width, this.height), dataBufferInt, null)
		this.image = BufferedImage(directColorModel, writableRaster, false, Hashtable<Any?, Any?>())
	}
}
