package com.jagex.game.runetek5.graphics.sprite;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;

@OriginalClass("client!mi")
public abstract class NativeSprite implements Sprite {

	@OriginalMember(owner = "client!mi", name = "<init>", descriptor = "()V")
	public NativeSprite() {
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFFFIIIII)V")
	private void drawRotatedWithPivot(@OriginalArg(0) float x, @OriginalArg(1) float y, @OriginalArg(2) float pivotX, @OriginalArg(3) float pivotY, @OriginalArg(4) int zoom, @OriginalArg(5) int rotation, @OriginalArg(6) int mode, @OriginalArg(7) int filter) {
		if (zoom == 0) {
			return;
		}
		@Pc(9) double rotationRadians = (double) (rotation & 0xFFFF) * 9.587379924285257E-5D;
		@Pc(16) float sinRotation = (float) Math.sin(rotationRadians) * (float) zoom;
		@Pc(23) float cosRotation = (float) Math.cos(rotationRadians) * (float) zoom;
		@Pc(37) float topLeftX = (-pivotX * cosRotation + -pivotY * sinRotation) / 4096.0F + x;
		@Pc(50) float topLeftY = (pivotX * sinRotation + -pivotY * cosRotation) / 4096.0F + y;
		@Pc(67) float topRightX = (((float) this.scaleWidth() - pivotX) * cosRotation + -pivotY * sinRotation) / 4096.0F + x;
		@Pc(85) float topRightY = (-((float) this.scaleWidth() - pivotX) * sinRotation + -pivotY * cosRotation) / 4096.0F + y;
		@Pc(102) float bottomLeftX = (-pivotX * cosRotation + ((float) this.scaleHeight() - pivotY) * sinRotation) / 4096.0F + x;
		@Pc(118) float bottomLeftY = (pivotX * sinRotation + ((float) this.scaleHeight() - pivotY) * cosRotation) / 4096.0F + y;
		this.renderParallelogram(topLeftX, topLeftY, topRightX, topRightY, bottomLeftX, bottomLeftY, mode, filter);
	}

	/** Draws the sprite into the parallelogram given by its top left, top right and bottom left corners, with the combine mode, colour, blend mode and filter. */
	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFFFFFIIII)V")
	public abstract void method6201(@OriginalArg(0) float topLeftX, @OriginalArg(1) float topLeftY, @OriginalArg(2) float topRightX, @OriginalArg(3) float topRightY, @OriginalArg(4) float bottomLeftX, @OriginalArg(5) float bottomLeftY, @OriginalArg(6) int combineMode, @OriginalArg(7) int colorArgb);

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(IILclient!aa;II)V")
	public abstract void drawWithSurface(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) ComponentSurface surface, @OriginalArg(3) int surfaceOffsetX, @OriginalArg(4) int surfaceOffsetY);

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(IIIII)V")
	public abstract void render(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int combineMode, @OriginalArg(3) int colorArgb, @OriginalArg(4) int blendMode);

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(IIIIIII)V")
	public abstract void drawScaledColored(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int combineMode, @OriginalArg(5) int colorArgb, @OriginalArg(6) int blendMode);

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "()I")
	public abstract int scaleWidth();

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFFFIILclient!aa;II)V")
	public final void rotate(@OriginalArg(0) float x, @OriginalArg(1) float y, @OriginalArg(2) float pivotX, @OriginalArg(3) float pivotY, @OriginalArg(4) int zoom, @OriginalArg(5) int rotation, @OriginalArg(6) ComponentSurface surface, @OriginalArg(7) int surfaceOffsetX, @OriginalArg(8) int surfaceOffsetY) {
		if (zoom == 0) {
			return;
		}
		@Pc(9) double rotationRadians = (double) (rotation & 0xFFFF) * 9.587379924285257E-5D;
		@Pc(16) float sinRotation = (float) Math.sin(rotationRadians) * (float) zoom;
		@Pc(23) float cosRotation = (float) Math.cos(rotationRadians) * (float) zoom;
		@Pc(37) float topLeftX = (-pivotX * cosRotation + -pivotY * sinRotation) / 4096.0F + x;
		@Pc(50) float topLeftY = (pivotX * sinRotation + -pivotY * cosRotation) / 4096.0F + y;
		@Pc(67) float topRightX = (((float) this.scaleWidth() - pivotX) * cosRotation + -pivotY * sinRotation) / 4096.0F + x;
		@Pc(85) float topRightY = (-((float) this.scaleWidth() - pivotX) * sinRotation + -pivotY * cosRotation) / 4096.0F + y;
		@Pc(102) float bottomLeftX = (-pivotX * cosRotation + ((float) this.scaleHeight() - pivotY) * sinRotation) / 4096.0F + x;
		@Pc(118) float bottomLeftY = (pivotX * sinRotation + ((float) this.scaleHeight() - pivotY) * cosRotation) / 4096.0F + y;
		this.drawAffineTransformedMasked(topLeftX, topLeftY, topRightX, topRightY, bottomLeftX, bottomLeftY, surface, surfaceOffsetX, surfaceOffsetY);
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(III)V")
	public abstract void copyAlpha(@OriginalArg(2) int channel);

	@OriginalMember(owner = "client!mi", name = "b", descriptor = "()I")
	public abstract int getHeight();

	@OriginalMember(owner = "client!mi", name = "b", descriptor = "(IIIIIII)V")
	public final void drawCircle(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int combineMode, @OriginalArg(5) int colorArgb, @OriginalArg(6) int blendMode) {
		this.renderImpl(x, y, width, height, combineMode, colorArgb, blendMode);
	}

	@OriginalMember(owner = "client!mi", name = "c", descriptor = "()I")
	public abstract int getWidth();

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(IIII)V")
	public final void drawTiled(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height) {
		this.drawScaledColored(x, y, width, height, 1, 0, 1);
	}

	@OriginalMember(owner = "client!mi", name = "b", descriptor = "(IIII)V")
	public final void drawTintedScaled(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height) {
		this.renderImpl(x, y, width, height, 1, 0, 1);
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(II)V")
	public final void render(@OriginalArg(0) int x, @OriginalArg(1) int y) {
		this.render(x, y, 1, 0, 1);
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFFFFFLclient!aa;II)V")
	private void drawAffineTransformedMasked(@OriginalArg(0) float topLeftX, @OriginalArg(1) float topLeftY, @OriginalArg(2) float topRightX, @OriginalArg(3) float topRightY, @OriginalArg(4) float bottomLeftX, @OriginalArg(5) float bottomLeftY, @OriginalArg(6) ComponentSurface surface, @OriginalArg(7) int surfaceOffsetX, @OriginalArg(8) int surfaceOffsetY) {
		this.renderParallelogramImpl(topLeftX, topLeftY, topRightX, topRightY, bottomLeftX, bottomLeftY, surface, surfaceOffsetX, surfaceOffsetY);
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFIILclient!aa;II)V")
	public final void drawRotatedSprite(@OriginalArg(0) float x, @OriginalArg(1) float y, @OriginalArg(3) int rotation, @OriginalArg(4) ComponentSurface clippingMask, @OriginalArg(5) int surfaceOffsetX, @OriginalArg(6) int surfaceOffsetY) {
		this.rotate(x, y, (float) this.scaleWidth() / 2.0F, (float) this.scaleHeight() / 2.0F, 4096, rotation, clippingMask, surfaceOffsetX, surfaceOffsetY);
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFIIIII)V")
	public final void drawRotatedCenterColored(@OriginalArg(0) float x, @OriginalArg(1) float y, @OriginalArg(2) int zoom, @OriginalArg(3) int rotation, @OriginalArg(5) int filter) {
		this.drawRotatedWithPivot(x, y, (float) this.scaleWidth() / 2.0F, (float) this.scaleHeight() / 2.0F, zoom, rotation, 0, filter);
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFFFFFIII)V")
	private void renderParallelogram(@OriginalArg(0) float x, @OriginalArg(1) float y, @OriginalArg(2) float width, @OriginalArg(3) float height, @OriginalArg(4) float op, @OriginalArg(5) float colour, @OriginalArg(6) int mode, @OriginalArg(7) int filter) {
		this.method6201(x, y, width, height, op, colour, mode, filter);
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(IIIIII)V")
	public abstract void captureScreenRegion(@OriginalArg(0) int dstX, @OriginalArg(1) int dstY, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int srcX, @OriginalArg(5) int srcY);

	@OriginalMember(owner = "client!mi", name = "d", descriptor = "()I")
	public abstract int scaleHeight();

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFII)V")
	public final void drawRotatedCenter(@OriginalArg(0) float x, @OriginalArg(1) float y, @OriginalArg(2) int zoom, @OriginalArg(3) int rotation) {
		this.drawRotatedWithPivot(x, y, (float) this.scaleWidth() / 2.0F, (float) this.scaleHeight() / 2.0F, zoom, rotation, 1, 0);
	}

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(IIIIIIII)V")
	public abstract void renderImpl(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int combineMode, @OriginalArg(5) int colorArgb, @OriginalArg(6) int blendMode);

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "(FFFFFFILclient!aa;II)V")
	public abstract void renderParallelogramImpl(@OriginalArg(0) float topLeftX, @OriginalArg(1) float topLeftY, @OriginalArg(2) float topRightX, @OriginalArg(3) float topRightY, @OriginalArg(4) float bottomLeftX, @OriginalArg(5) float bottomLeftY, @OriginalArg(7) ComponentSurface surface, @OriginalArg(8) int surfaceOffsetX, @OriginalArg(9) int surfaceOffsetY);

	@OriginalMember(owner = "client!mi", name = "a", descriptor = "([I)V")
	public abstract void projectOffsets(@OriginalArg(0) int[] offsets);

	@OriginalMember(owner = "client!mi", name = "c", descriptor = "(IIII)V")
	public abstract void setOffsets(@OriginalArg(0) int translateX, @OriginalArg(1) int translateY, @OriginalArg(2) int trimRight, @OriginalArg(3) int trimBottom);
}
