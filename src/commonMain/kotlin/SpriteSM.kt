import com.jagex.game.runetek5.JavaSpriteBlitState
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!nu")
abstract class SpriteSM : NativeSprite {

	@OriginalMember(owner = "client!nu", name = "e", descriptor = "I")
	@JvmField
	protected var trimRight: Int = 0

	@OriginalMember(owner = "client!nu", name = "g", descriptor = "I")
	@JvmField
	protected var trimBottom: Int = 0

	@OriginalMember(owner = "client!nu", name = "l", descriptor = "I")
	@JvmField
	protected var leftMargin: Int = 0

	@OriginalMember(owner = "client!nu", name = "m", descriptor = "[I")
	private var cachedClipping: IntArray? = null

	@OriginalMember(owner = "client!nu", name = "o", descriptor = "I")
	@JvmField
	protected var topMargin: Int = 0

	@OriginalMember(owner = "client!nu", name = "k", descriptor = "Lclient!ao;")
	@JvmField
	protected val toolkit: JavaToolkit?

	@OriginalMember(owner = "client!nu", name = "v", descriptor = "I")
	@JvmField
	val imageWidth: Int

	@OriginalMember(owner = "client!nu", name = "u", descriptor = "I")
	@JvmField
	val imageHeight: Int

	@OriginalMember(owner = "client!nu", name = "<init>", descriptor = "(Lclient!ao;II)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) imageWidth: Int, @OriginalArg(2) imageHeight: Int) : super() {
		this.toolkit = javaToolkit
		this.imageWidth = imageWidth
		this.imageHeight = imageHeight
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(IIIII)V")
	abstract override fun render(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) combineMode: Int, @OriginalArg(3) colorArgb: Int, @OriginalArg(4) blendMode: Int)

	@OriginalMember(owner = "client!nu", name = "c", descriptor = "()I")
	final override fun getWidth(): Int {
		return this.imageWidth
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "([I)V")
	final override fun projectOffsets(@OriginalArg(0) destination: IntArray?) {
		destination!![0] = this.leftMargin
		destination[1] = this.topMargin
		destination[2] = this.trimRight
		destination[3] = this.trimBottom
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(IIIIIII)V")
	final override fun drawScaledColored(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) combineMode: Int, @OriginalArg(5) colorArgb: Int, @OriginalArg(6) blendMode: Int) {
		if (this.toolkit!!.stopped()) {
			throw IllegalStateException()
		}
		if (this.cachedClipping == null) {
			this.cachedClipping = IntArray(4)
		}
		this.toolkit.K(this.cachedClipping)
		this.toolkit.T(this.toolkit.clipX1, this.toolkit.clipY1, x + width, y + height)
		@Pc(37) val tileWidth: Int = this.scaleWidth()
		@Pc(40) val tileHeight: Int = this.scaleHeight()
		@Pc(48) val columns: Int = (width + tileWidth - 1) / tileWidth
		@Pc(56) val rows: Int = (height + tileHeight - 1) / tileHeight
		@Pc(58) var row: Int = 0
		while (row < rows) {
			@Pc(63) val offsetY: Int = row * tileHeight
			@Pc(65) var column: Int = 0
			while (column < columns) {
				this.render(x + column * tileWidth, y + offsetY, combineMode, colorArgb, blendMode)
				column++
			}
			row++
		}
		this.toolkit.KA(this.cachedClipping!![0], this.cachedClipping!![1], this.cachedClipping!![2], this.cachedClipping!![3])
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(FFFFFFILclient!aa;II)V")
	final override fun renderParallelogramImpl(@OriginalArg(0) centerX: Float, @OriginalArg(1) centerY: Float, @OriginalArg(2) x1: Float, @OriginalArg(3) y1: Float, @OriginalArg(4) x2: Float, @OriginalArg(5) y2: Float, @OriginalArg(7) mask: ComponentSurface?, @OriginalArg(8) maskX: Int, @OriginalArg(9) maskY: Int) {
		if (this.toolkit!!.stopped()) {
			throw IllegalStateException()
		} else if (this.computeAffineTransform(centerX, centerY, x1, y1, x2, y2)) {
			@Pc(20) val clippingMask: JavaClippingMask = mask as JavaClippingMask
			this.blitParallelogramMasked(clippingMask.lineWidths, clippingMask.lineOffsets, JavaSpriteBlitState.drawMinX - maskX, -maskY - (JavaSpriteBlitState.drawHeight - JavaSpriteBlitState.drawMinY))
		}
	}

	@OriginalMember(owner = "client!nu", name = "c", descriptor = "(IIII)V")
	final override fun setOffsets(@OriginalArg(0) leftMargin: Int, @OriginalArg(1) topMargin: Int, @OriginalArg(2) trimRight: Int, @OriginalArg(3) trimBottom: Int) {
		this.leftMargin = leftMargin
		this.topMargin = topMargin
		this.trimRight = trimRight
		this.trimBottom = trimBottom
	}

	@OriginalMember(owner = "client!nu", name = "b", descriptor = "(II)V")
	protected abstract fun blitParallelogram(@OriginalArg(0) op: Int)

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "([I[III)V")
	protected abstract fun blitParallelogramMasked(@OriginalArg(0) rowStartX: IntArray?, @OriginalArg(1) rowWidth: IntArray?, @OriginalArg(2) maskOffsetX: Int, @OriginalArg(3) maskOffsetY: Int)

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(FFFFFFIIII)V")
	final override fun method6201(@OriginalArg(0) topLeftX: Float, @OriginalArg(1) topLeftY: Float, @OriginalArg(2) topRightX: Float, @OriginalArg(3) topRightY: Float, @OriginalArg(4) bottomLeftX: Float, @OriginalArg(5) bottomLeftY: Float, @OriginalArg(6) combineMode: Int, @OriginalArg(7) colour: Int) {
		if (this.toolkit!!.stopped()) {
			throw IllegalStateException()
		} else if (this.computeAffineTransform(topLeftX, topLeftY, topRightX, topRightY, bottomLeftX, bottomLeftY)) {
			JavaSpriteBlitState.colour = colour
			if (combineMode != 1) {
				JavaSpriteBlitState.alpha = colour ushr 24
				JavaSpriteBlitState.invAlpha = 256 - JavaSpriteBlitState.alpha
				if (combineMode == 0) {
					JavaSpriteBlitState.red = colour shr 16 and 0xFF
					JavaSpriteBlitState.green = colour shr 8 and 0xFF
					JavaSpriteBlitState.blue = colour and 0xFF
				} else if (combineMode == 2) {
					JavaSpriteBlitState.lerpAlpha = colour ushr 24
					JavaSpriteBlitState.lerpInvAlpha = 256 - JavaSpriteBlitState.lerpAlpha
					@Pc(68) val redBlue: Int = (colour and 0xFF00FF) * JavaSpriteBlitState.lerpInvAlpha and 0xFF00FF00.toInt()
					@Pc(76) val green: Int = (colour and 0xFF00) * JavaSpriteBlitState.lerpInvAlpha and 0xFF0000
					JavaSpriteBlitState.lerpColour = (redBlue or green) ushr 8
				}
			}
			if (combineMode == 1) {
				this.blitParallelogram(1)
			} else if (combineMode == 0) {
				this.blitParallelogram(0)
			} else if (combineMode == 3) {
				this.blitParallelogram(3)
			} else if (combineMode == 2) {
				this.blitParallelogram(2)
			}
		}
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(IIIIIIIII)V")
	abstract fun renderDepthTestedRgb(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int, @OriginalArg(5) op: Int, @OriginalArg(6) colour: Int, @OriginalArg(7) mode: Int)

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(IIIIIIII)V")
	abstract override fun renderImpl(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) combineMode: Int, @OriginalArg(5) colorArgb: Int, @OriginalArg(6) blendMode: Int)

	@OriginalMember(owner = "client!nu", name = "b", descriptor = "()I")
	final override fun getHeight(): Int {
		return this.imageHeight
	}

	@OriginalMember(owner = "client!nu", name = "d", descriptor = "()I")
	final override fun scaleHeight(): Int {
		return this.topMargin + this.imageHeight + this.trimBottom
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "()I")
	final override fun scaleWidth(): Int {
		return this.leftMargin + this.imageWidth + this.trimRight
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(IILclient!aa;II)V")
	abstract override fun drawWithSurface(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) surface: ComponentSurface?, @OriginalArg(3) surfaceOffsetX: Int, @OriginalArg(4) surfaceOffsetY: Int)

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(FFFFFF)Z")
	private fun computeAffineTransform(@OriginalArg(0) topLeftX: Float, @OriginalArg(1) topLeftY: Float, @OriginalArg(2) topRightX: Float, @OriginalArg(3) topRightY: Float, @OriginalArg(4) bottomLeftX: Float, @OriginalArg(5) bottomLeftY: Float): Boolean {
		var topLeftX: Float = topLeftX
		var topLeftY: Float = topLeftY
		var topRightX: Float = topRightX
		var topRightY: Float = topRightY
		var bottomLeftX: Float = bottomLeftX
		var bottomLeftY: Float = bottomLeftY
		@Pc(8) val scaleWidth: Int = this.leftMargin + this.imageWidth + this.trimRight
		@Pc(17) val scaleHeight: Int = this.topMargin + this.imageHeight + this.trimBottom
		@Pc(32) var local32: Float
		@Pc(39) var local39: Float
		@Pc(46) var local46: Float
		@Pc(53) var local53: Float
		@Pc(59) var local59: Float
		@Pc(65) var local65: Float
		@Pc(71) var local71: Float
		@Pc(77) var local77: Float
		if (scaleWidth != this.imageWidth || scaleHeight != this.imageHeight) {
			val columnStepX: Float = (topRightX - topLeftX) / scaleWidth.toFloat()
			val columnStepY: Float = (topRightY - topLeftY) / scaleWidth.toFloat()
			val rowStepX: Float = (bottomLeftX - topLeftX) / scaleHeight.toFloat()
			val rowStepY: Float = (bottomLeftY - topLeftY) / scaleHeight.toFloat()
			val topInsetX: Float = rowStepX * this.topMargin.toFloat()
			val topInsetY: Float = rowStepY * this.topMargin.toFloat()
			val leftInsetX: Float = columnStepX * this.leftMargin.toFloat()
			val leftInsetY: Float = columnStepY * this.leftMargin.toFloat()
			@Pc(84) val rightX: Float = -columnStepX * this.trimRight.toFloat()
			@Pc(91) val rightY: Float = -columnStepY * this.trimRight.toFloat()
			@Pc(98) val bottomX: Float = -rowStepX * this.trimBottom.toFloat()
			@Pc(105) val bottomY: Float = -rowStepY * this.trimBottom.toFloat()
			topLeftX += leftInsetX + topInsetX
			topLeftY += leftInsetY + topInsetY
			topRightX += rightX + topInsetX
			topRightY += rightY + topInsetY
			bottomLeftX += leftInsetX + bottomX
			bottomLeftY += leftInsetY + bottomY
		}
		val bottomRightX: Float = bottomLeftX + topRightX - topLeftX
		val bottomRightY: Float = topRightY + bottomLeftY - topLeftY
		var minX: Float
		var maxX: Float
		if (topLeftX < topRightX) {
			minX = topLeftX
			maxX = topRightX
		} else {
			minX = topRightX
			maxX = topLeftX
		}
		if (bottomLeftX < minX) {
			minX = bottomLeftX
		}
		if (bottomRightX < minX) {
			minX = bottomRightX
		}
		if (bottomLeftX > maxX) {
			maxX = bottomLeftX
		}
		if (bottomRightX > maxX) {
			maxX = bottomRightX
		}
		var minY: Float
		var maxY: Float
		if (topLeftY < topRightY) {
			minY = topLeftY
			maxY = topRightY
		} else {
			minY = topRightY
			maxY = topLeftY
		}
		if (bottomLeftY < minY) {
			minY = bottomLeftY
		}
		if (bottomRightY < minY) {
			minY = bottomRightY
		}
		if (bottomLeftY > maxY) {
			maxY = bottomLeftY
		}
		if (bottomRightY > maxY) {
			maxY = bottomRightY
		}
		if (minX < this.toolkit!!.clipX1.toFloat()) {
			minX = this.toolkit.clipX1.toFloat()
		}
		if (maxX > this.toolkit.clipX2.toFloat()) {
			maxX = this.toolkit.clipX2.toFloat()
		}
		if (minY < this.toolkit.clipY1.toFloat()) {
			minY = this.toolkit.clipY1.toFloat()
		}
		if (maxY > this.toolkit.clipY2.toFloat()) {
			maxY = this.toolkit.clipY2.toFloat()
		}
		maxX = minX - maxX
		if (maxX >= 0.0f) {
			return false
		}
		maxY = minY - maxY
		if (maxY >= 0.0f) {
			return false
		}
		JavaSpriteBlitState.viewportStride = this.toolkit.surfaceWidth
		JavaSpriteBlitState.pixelOffset = ((minY.toInt() * JavaSpriteBlitState.viewportStride).toFloat() + minX).toInt()
		val uDeterminant: Float = (topRightX - topLeftX) * (bottomLeftY - topLeftY) - (topRightY - topLeftY) * (bottomLeftX - topLeftX)
		val vDeterminant: Float = (bottomLeftX - topLeftX) * (topRightY - topLeftY) - (bottomLeftY - topLeftY) * (topRightX - topLeftX)
		JavaSpriteBlitState.dudx = ((bottomLeftY - topLeftY) * 4096.0f * this.imageWidth.toFloat() / uDeterminant).toInt()
		JavaSpriteBlitState.dvdx = ((topRightY - topLeftY) * 4096.0f * this.imageHeight.toFloat() / vDeterminant).toInt()
		JavaSpriteBlitState.dudy = ((bottomLeftX - topLeftX) * 4096.0f * this.imageWidth.toFloat() / vDeterminant).toInt()
		JavaSpriteBlitState.dvdy = ((topRightX - topLeftX) * 4096.0f * this.imageHeight.toFloat() / uDeterminant).toInt()
		JavaSpriteBlitState.subpixelOffsetX = (minX * 16.0f + 8.0f - (topLeftX + topRightX + bottomLeftX + bottomRightX) / 4.0f * 16.0f).toInt()
		JavaSpriteBlitState.subpixelOffsetY = (minY * 16.0f + 8.0f - (topLeftY + topRightY + bottomLeftY + bottomRightY) / 4.0f * 16.0f).toInt()
		JavaSpriteBlitState.uBase = (this.imageWidth shr 1 shl 12) + (JavaSpriteBlitState.subpixelOffsetY * JavaSpriteBlitState.dudy shr 4)
		JavaSpriteBlitState.vBase = (this.imageHeight shr 1 shl 12) + (JavaSpriteBlitState.subpixelOffsetY * JavaSpriteBlitState.dvdy shr 4)
		JavaSpriteBlitState.uOffsetX = JavaSpriteBlitState.subpixelOffsetX * JavaSpriteBlitState.dudx shr 4
		JavaSpriteBlitState.vOffsetX = JavaSpriteBlitState.subpixelOffsetX * JavaSpriteBlitState.dvdx shr 4
		JavaSpriteBlitState.drawMinX = minX.toInt()
		JavaSpriteBlitState.drawWidth = maxX.toInt()
		JavaSpriteBlitState.drawMinY = minY.toInt()
		JavaSpriteBlitState.drawHeight = maxY.toInt()
		return true
	}
}
