package com.jagex.game.runetek5.graphics.sprite

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface

@OriginalClass("client!mi")
abstract class NativeSprite @OriginalMember(owner = "client!mi", name = "<init>", descriptor = "()V") constructor() : Sprite {

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFFFIIIII)V")
	private fun drawRotatedWithPivot(@OriginalArg(0) x: Float, @OriginalArg(1) y: Float, @OriginalArg(2) pivotX: Float, @OriginalArg(3) pivotY: Float, @OriginalArg(4) zoom: Int, @OriginalArg(5) rotation: Int, @OriginalArg(6) mode: Int, @OriginalArg(7) filter: Int) {
		if (zoom == 0) {
			return
		}
		@Pc(9) val rotationRadians: Double = (rotation and 0xFFFF).toDouble() * 9.587379924285257E-5
		@Pc(16) val sinRotation: Float = kotlin.math.sin(rotationRadians).toFloat() * zoom.toFloat()
		@Pc(23) val cosRotation: Float = kotlin.math.cos(rotationRadians).toFloat() * zoom.toFloat()
		@Pc(37) val topLeftX: Float = (-pivotX * cosRotation + -pivotY * sinRotation) / 4096.0f + x
		@Pc(50) val topLeftY: Float = (pivotX * sinRotation + -pivotY * cosRotation) / 4096.0f + y
		@Pc(67) val topRightX: Float = ((this.scaleWidth().toFloat() - pivotX) * cosRotation + -pivotY * sinRotation) / 4096.0f + x
		@Pc(85) val topRightY: Float = (-(this.scaleWidth().toFloat() - pivotX) * sinRotation + -pivotY * cosRotation) / 4096.0f + y
		@Pc(102) val bottomLeftX: Float = (-pivotX * cosRotation + (this.scaleHeight().toFloat() - pivotY) * sinRotation) / 4096.0f + x
		@Pc(118) val bottomLeftY: Float = (pivotX * sinRotation + (this.scaleHeight().toFloat() - pivotY) * cosRotation) / 4096.0f + y
		this.renderParallelogram(topLeftX, topLeftY, topRightX, topRightY, bottomLeftX, bottomLeftY, mode, filter)
	}

	/** Draws the sprite into the parallelogram given by its top left, top right and bottom left corners, with the combine mode, colour, blend mode and filter. */
	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFFFFFIIII)V")
	abstract fun method6201(@OriginalArg(0) topLeftX: Float, @OriginalArg(1) topLeftY: Float, @OriginalArg(2) topRightX: Float, @OriginalArg(3) topRightY: Float, @OriginalArg(4) bottomLeftX: Float, @OriginalArg(5) bottomLeftY: Float, @OriginalArg(6) combineMode: Int, @OriginalArg(7) colorArgb: Int)

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(IILclient!aa;II)V")
	abstract fun drawWithSurface(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) surface: ComponentSurface?, @OriginalArg(3) surfaceOffsetX: Int, @OriginalArg(4) surfaceOffsetY: Int)

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(IIIII)V")
	abstract fun render(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) combineMode: Int, @OriginalArg(3) colorArgb: Int, @OriginalArg(4) blendMode: Int)

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(IIIIIII)V")
	abstract fun drawScaledColored(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) combineMode: Int, @OriginalArg(5) colorArgb: Int, @OriginalArg(6) blendMode: Int)

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "()I")
	abstract fun scaleWidth(): Int

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFFFIILclient!aa;II)V")
	fun rotate(@OriginalArg(0) x: Float, @OriginalArg(1) y: Float, @OriginalArg(2) pivotX: Float, @OriginalArg(3) pivotY: Float, @OriginalArg(4) zoom: Int, @OriginalArg(5) rotation: Int, @OriginalArg(6) surface: ComponentSurface?, @OriginalArg(7) surfaceOffsetX: Int, @OriginalArg(8) surfaceOffsetY: Int) {
		if (zoom == 0) {
			return
		}
		@Pc(9) val rotationRadians: Double = (rotation and 0xFFFF).toDouble() * 9.587379924285257E-5
		@Pc(16) val sinRotation: Float = kotlin.math.sin(rotationRadians).toFloat() * zoom.toFloat()
		@Pc(23) val cosRotation: Float = kotlin.math.cos(rotationRadians).toFloat() * zoom.toFloat()
		@Pc(37) val topLeftX: Float = (-pivotX * cosRotation + -pivotY * sinRotation) / 4096.0f + x
		@Pc(50) val topLeftY: Float = (pivotX * sinRotation + -pivotY * cosRotation) / 4096.0f + y
		@Pc(67) val topRightX: Float = ((this.scaleWidth().toFloat() - pivotX) * cosRotation + -pivotY * sinRotation) / 4096.0f + x
		@Pc(85) val topRightY: Float = (-(this.scaleWidth().toFloat() - pivotX) * sinRotation + -pivotY * cosRotation) / 4096.0f + y
		@Pc(102) val bottomLeftX: Float = (-pivotX * cosRotation + (this.scaleHeight().toFloat() - pivotY) * sinRotation) / 4096.0f + x
		@Pc(118) val bottomLeftY: Float = (pivotX * sinRotation + (this.scaleHeight().toFloat() - pivotY) * cosRotation) / 4096.0f + y
		this.drawAffineTransformedMasked(topLeftX, topLeftY, topRightX, topRightY, bottomLeftX, bottomLeftY, surface, surfaceOffsetX, surfaceOffsetY)
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(III)V")
	abstract fun copyAlpha(@OriginalArg(2) channel: Int)

	@OriginalMember(owner = "client!mi", name = "b", descriptor = "()I")
	abstract fun getHeight(): Int

	@OriginalMember(owner = "client!mi", name = "b", descriptor = "(IIIIIII)V")
	fun drawCircle(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) combineMode: Int, @OriginalArg(5) colorArgb: Int, @OriginalArg(6) blendMode: Int) {
		this.renderImpl(x, y, width, height, combineMode, colorArgb, blendMode)
	}

	@OriginalMember(owner = "client!mi", name = "c", descriptor = "()I")
	abstract fun getWidth(): Int

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(IIII)V")
	fun drawTiled(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int) {
		this.drawScaledColored(x, y, width, height, 1, 0, 1)
	}

	@OriginalMember(owner = "client!mi", name = "b", descriptor = "(IIII)V")
	fun drawTintedScaled(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int) {
		this.renderImpl(x, y, width, height, 1, 0, 1)
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(II)V")
	fun render(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int) {
		this.render(x, y, 1, 0, 1)
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFFFFFLclient!aa;II)V")
	private fun drawAffineTransformedMasked(@OriginalArg(0) topLeftX: Float, @OriginalArg(1) topLeftY: Float, @OriginalArg(2) topRightX: Float, @OriginalArg(3) topRightY: Float, @OriginalArg(4) bottomLeftX: Float, @OriginalArg(5) bottomLeftY: Float, @OriginalArg(6) surface: ComponentSurface?, @OriginalArg(7) surfaceOffsetX: Int, @OriginalArg(8) surfaceOffsetY: Int) {
		this.renderParallelogramImpl(topLeftX, topLeftY, topRightX, topRightY, bottomLeftX, bottomLeftY, surface, surfaceOffsetX, surfaceOffsetY)
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFIILclient!aa;II)V")
	fun drawRotatedSprite(@OriginalArg(0) x: Float, @OriginalArg(1) y: Float, @OriginalArg(3) rotation: Int, @OriginalArg(4) clippingMask: ComponentSurface?, @OriginalArg(5) surfaceOffsetX: Int, @OriginalArg(6) surfaceOffsetY: Int) {
		this.rotate(x, y, this.scaleWidth().toFloat() / 2.0f, this.scaleHeight().toFloat() / 2.0f, 4096, rotation, clippingMask, surfaceOffsetX, surfaceOffsetY)
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFIIIII)V")
	fun drawRotatedCenterColored(@OriginalArg(0) x: Float, @OriginalArg(1) y: Float, @OriginalArg(2) zoom: Int, @OriginalArg(3) rotation: Int, @OriginalArg(5) filter: Int) {
		this.drawRotatedWithPivot(x, y, this.scaleWidth().toFloat() / 2.0f, this.scaleHeight().toFloat() / 2.0f, zoom, rotation, 0, filter)
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFFFFFIII)V")
	private fun renderParallelogram(@OriginalArg(0) x: Float, @OriginalArg(1) y: Float, @OriginalArg(2) width: Float, @OriginalArg(3) height: Float, @OriginalArg(4) op: Float, @OriginalArg(5) colour: Float, @OriginalArg(6) mode: Int, @OriginalArg(7) filter: Int) {
		this.method6201(x, y, width, height, op, colour, mode, filter)
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(IIIIII)V")
	abstract fun captureScreenRegion(@OriginalArg(0) dstX: Int, @OriginalArg(1) dstY: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) srcX: Int, @OriginalArg(5) srcY: Int)

	@OriginalMember(owner = "client!mi", name = "d", descriptor = "()I")
	abstract fun scaleHeight(): Int

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFII)V")
	fun drawRotatedCenter(@OriginalArg(0) x: Float, @OriginalArg(1) y: Float, @OriginalArg(2) zoom: Int, @OriginalArg(3) rotation: Int) {
		this.drawRotatedWithPivot(x, y, this.scaleWidth().toFloat() / 2.0f, this.scaleHeight().toFloat() / 2.0f, zoom, rotation, 1, 0)
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(IIIIIIII)V")
	abstract fun renderImpl(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) combineMode: Int, @OriginalArg(5) colorArgb: Int, @OriginalArg(6) blendMode: Int)

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFFFFFILclient!aa;II)V")
	abstract fun renderParallelogramImpl(@OriginalArg(0) topLeftX: Float, @OriginalArg(1) topLeftY: Float, @OriginalArg(2) topRightX: Float, @OriginalArg(3) topRightY: Float, @OriginalArg(4) bottomLeftX: Float, @OriginalArg(5) bottomLeftY: Float, @OriginalArg(7) surface: ComponentSurface?, @OriginalArg(8) surfaceOffsetX: Int, @OriginalArg(9) surfaceOffsetY: Int)

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "([I)V")
	abstract fun projectOffsets(@OriginalArg(0) offsets: IntArray?)

	@OriginalMember(owner = "client!mi", name = "c", descriptor = "(IIII)V")
	abstract fun setOffsets(@OriginalArg(0) translateX: Int, @OriginalArg(1) translateY: Int, @OriginalArg(2) trimRight: Int, @OriginalArg(3) trimBottom: Int)
}
