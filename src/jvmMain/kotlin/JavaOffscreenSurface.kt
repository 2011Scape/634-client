import com.jagex.game.runetek5.JavaDepthBuffer
import com.jagex.game.runetek5.graphics.OffscreenSurface
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!it")
class JavaOffscreenSurface @OriginalMember(owner = "client!it", name = "<init>", descriptor = "(Lclient!ao;Lclient!mi;Lclient!ru;)V") constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) sprite: NativeSprite?, @OriginalArg(2) javaDepthBuffer: JavaDepthBuffer?) : OffscreenSurface {

	@OriginalMember(owner = "client!it", name = "k", descriptor = "Lclient!ao;")
	private val toolkit: JavaToolkit?

	@OriginalMember(owner = "client!it", name = "g", descriptor = "I")
	@JvmField
	val width: Int

	@OriginalMember(owner = "client!it", name = "d", descriptor = "I")
	@JvmField
	val height: Int

	@OriginalMember(owner = "client!it", name = "m", descriptor = "[I")
	@JvmField
	val raster: IntArray?

	@OriginalMember(owner = "client!it", name = "f", descriptor = "Lclient!ru;")
	private var depth: JavaDepthBuffer? = null

	@OriginalMember(owner = "client!it", name = "a", descriptor = "[F")
	@JvmField
	var depthBuffer: FloatArray? = null

	init {
		this.toolkit = javaToolkit
		if (sprite is OpaqueSpriteSM) {
			@Pc(34) val javaRgbSprite: OpaqueSpriteSM = sprite
			this.raster = javaRgbSprite.pixels
			this.height = javaRgbSprite.imageHeight
			this.width = javaRgbSprite.imageWidth
		} else if (sprite is TransparentSpriteSM) {
			@Pc(17) val javaArgbSprite: TransparentSpriteSM = sprite
			this.width = javaArgbSprite.imageWidth
			this.height = javaArgbSprite.imageHeight
			this.raster = javaArgbSprite.pixels
		} else {
			throw RuntimeException()
		}
		if (javaDepthBuffer != null) {
			this.depth = javaDepthBuffer
			if (this.depth!!.height != this.width || this.height != this.depth!!.width) {
				throw RuntimeException()
			}
			this.depthBuffer = this.depth!!.depths
		}
	}

	@OriginalMember(owner = "client!it", name = "a", descriptor = "(IIIIIIZZ)V")
	override fun copyToSurface(@OriginalArg(0) srcX: Int, @OriginalArg(1) srcY: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) dstX: Int, @OriginalArg(5) dstY: Int) {
		JavaOffscreenSurface.copyRasterRegion(this.toolkit!!.canvasSurface!!.raster, srcY, srcX, dstY, this.toolkit.mainDepthBuffer, this.raster, width, height, this.toolkit.canvasSurface!!.width, this.width, dstX, this.depthBuffer)
	}

	@OriginalMember(owner = "client!it", name = "b", descriptor = "(IIIIIIZZ)V")
	override fun copyFromSurface(@OriginalArg(0) srcX: Int, @OriginalArg(1) srcY: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(6) copyRaster: Boolean) {
		JavaOffscreenSurface.copyRasterRegion(this.raster, srcY, srcX, 0, this.depthBuffer, if (copyRaster) this.toolkit!!.canvasSurface!!.raster else null, width, height, this.width, this.toolkit!!.canvasSurface!!.width, 0, this.toolkit.mainDepthBuffer)
	}

	companion object {
		@OriginalMember(owner = "client!rq", name = "a", descriptor = "([IIII[F[IIIIIII[F)V")
		@JvmStatic
		fun copyRasterRegion(@OriginalArg(0) raster: IntArray?, @OriginalArg(1) srcY: Int, @OriginalArg(2) srcX: Int, @OriginalArg(3) dstY: Int, @OriginalArg(4) dstDepth: FloatArray?, @OriginalArg(5) srcRaster: IntArray?, @OriginalArg(6) copyWidth: Int, @OriginalArg(8) copyHeight: Int, @OriginalArg(9) width: Int, @OriginalArg(10) srcWidth: Int, @OriginalArg(11) dstX: Int, @OriginalArg(12) srcDepth: FloatArray?) {
			@Pc(17) var srcOffset: Int = srcY * srcWidth + srcX
			@Pc(23) var dstOffset: Int = dstX + width * dstY
			@Pc(28) val srcStep: Int = srcWidth - copyWidth
			@Pc(33) val dstStep: Int = width - copyWidth
			@Pc(37) var row: Int
			@Pc(44) var rowEnd: Int
			if (srcRaster == null) {
				row = 0
				while (row < copyHeight) {
					rowEnd = srcOffset + copyWidth
					while (rowEnd > srcOffset) {
						dstDepth!![dstOffset++] = srcDepth!![srcOffset++]
					}
					srcOffset += srcStep
					dstOffset += dstStep
					row++
				}
			} else if (srcDepth == null) {
				row = 0
				while (row < copyHeight) {
					rowEnd = copyWidth + srcOffset
					while (rowEnd > srcOffset) {
						raster!![dstOffset++] = srcRaster[srcOffset++]
					}
					srcOffset += srcStep
					dstOffset += dstStep
					row++
				}
			} else {
				row = 0
				while (row < copyHeight) {
					rowEnd = srcOffset + copyWidth
					while (rowEnd > srcOffset) {
						raster!![dstOffset] = srcRaster[srcOffset]
						dstDepth!![dstOffset++] = srcDepth[srcOffset++]
					}
					srcOffset += srcStep
					dstOffset += dstStep
					row++
				}
			}
		}
	}
}
