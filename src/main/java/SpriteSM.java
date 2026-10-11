import com.jagex.game.runetek5.JavaSpriteBlitState;
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!nu")
public abstract class SpriteSM extends NativeSprite {

	@OriginalMember(owner = "client!nu", name = "e", descriptor = "I")
	protected int trimRight;

	@OriginalMember(owner = "client!nu", name = "g", descriptor = "I")
	protected int trimBottom;

	@OriginalMember(owner = "client!nu", name = "l", descriptor = "I")
	protected int leftMargin;

	@OriginalMember(owner = "client!nu", name = "m", descriptor = "[I")
	private int[] cachedClipping;

	@OriginalMember(owner = "client!nu", name = "o", descriptor = "I")
	protected int topMargin;

	@OriginalMember(owner = "client!nu", name = "k", descriptor = "Lclient!ao;")
	protected final JavaToolkit toolkit;

	@OriginalMember(owner = "client!nu", name = "v", descriptor = "I")
	public final int imageWidth;

	@OriginalMember(owner = "client!nu", name = "u", descriptor = "I")
	public final int imageHeight;

	@OriginalMember(owner = "client!nu", name = "<init>", descriptor = "(Lclient!ao;II)V")
	public SpriteSM(@OriginalArg(0) JavaToolkit javaToolkit, @OriginalArg(1) int imageWidth, @OriginalArg(2) int imageHeight) {
		this.toolkit = javaToolkit;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(IIIII)V")
	public abstract void render(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int combineMode, @OriginalArg(3) int colorArgb, @OriginalArg(4) int blendMode);

	@OriginalMember(owner = "client!nu", name = "c", descriptor = "()I")
	@Override
	public final int getWidth() {
		return this.imageWidth;
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "([I)V")
	@Override
	public final void projectOffsets(@OriginalArg(0) int[] destination) {
		destination[0] = this.leftMargin;
		destination[1] = this.topMargin;
		destination[2] = this.trimRight;
		destination[3] = this.trimBottom;
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(IIIIIII)V")
	@Override
	public final void drawScaledColored(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int combineMode, @OriginalArg(5) int colorArgb, @OriginalArg(6) int blendMode) {
		if (this.toolkit.stopped()) {
			throw new IllegalStateException();
		}
		if (this.cachedClipping == null) {
			this.cachedClipping = new int[4];
		}
		this.toolkit.K(this.cachedClipping);
		this.toolkit.T(this.toolkit.clipX1, this.toolkit.clipY1, x + width, y + height);
		@Pc(37) int tileWidth = this.scaleWidth();
		@Pc(40) int tileHeight = this.scaleHeight();
		@Pc(48) int columns = (width + tileWidth - 1) / tileWidth;
		@Pc(56) int rows = (height + tileHeight - 1) / tileHeight;
		for (@Pc(58) int row = 0; row < rows; row++) {
			@Pc(63) int offsetY = row * tileHeight;
			for (@Pc(65) int column = 0; column < columns; column++) {
				this.render(x + column * tileWidth, y + offsetY, combineMode, colorArgb, blendMode);
			}
		}
		this.toolkit.KA(this.cachedClipping[0], this.cachedClipping[1], this.cachedClipping[2], this.cachedClipping[3]);
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(FFFFFFILclient!aa;II)V")
	@Override
	public final void renderParallelogramImpl(@OriginalArg(0) float centerX, @OriginalArg(1) float centerY, @OriginalArg(2) float x1, @OriginalArg(3) float y1, @OriginalArg(4) float x2, @OriginalArg(5) float y2, @OriginalArg(7) ComponentSurface mask, @OriginalArg(8) int maskX, @OriginalArg(9) int maskY) {
		if (this.toolkit.stopped()) {
			throw new IllegalStateException();
		} else if (this.computeAffineTransform(centerX, centerY, x1, y1, x2, y2)) {
			@Pc(20) JavaClippingMask clippingMask = (JavaClippingMask) mask;
			this.blitParallelogramMasked(clippingMask.lineWidths, clippingMask.lineOffsets, JavaSpriteBlitState.drawMinX - maskX, -maskY - (JavaSpriteBlitState.drawHeight - JavaSpriteBlitState.drawMinY));
		}
	}

	@OriginalMember(owner = "client!nu", name = "c", descriptor = "(IIII)V")
	@Override
	public final void setOffsets(@OriginalArg(0) int leftMargin, @OriginalArg(1) int topMargin, @OriginalArg(2) int trimRight, @OriginalArg(3) int trimBottom) {
		this.leftMargin = leftMargin;
		this.topMargin = topMargin;
		this.trimRight = trimRight;
		this.trimBottom = trimBottom;
	}

	@OriginalMember(owner = "client!nu", name = "b", descriptor = "(II)V")
	protected abstract void blitParallelogram(@OriginalArg(0) int op);

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "([I[III)V")
	protected abstract void blitParallelogramMasked(@OriginalArg(0) int[] rowStartX, @OriginalArg(1) int[] rowWidth, @OriginalArg(2) int maskOffsetX, @OriginalArg(3) int maskOffsetY);

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(FFFFFFIIII)V")
	@Override
	public final void method6201(@OriginalArg(0) float topLeftX, @OriginalArg(1) float topLeftY, @OriginalArg(2) float topRightX, @OriginalArg(3) float topRightY, @OriginalArg(4) float bottomLeftX, @OriginalArg(5) float bottomLeftY, @OriginalArg(6) int combineMode, @OriginalArg(7) int colour) {
		if (this.toolkit.stopped()) {
			throw new IllegalStateException();
		} else if (this.computeAffineTransform(topLeftX, topLeftY, topRightX, topRightY, bottomLeftX, bottomLeftY)) {
			JavaSpriteBlitState.colour = colour;
			if (combineMode != 1) {
				JavaSpriteBlitState.alpha = colour >>> 24;
				JavaSpriteBlitState.invAlpha = 256 - JavaSpriteBlitState.alpha;
				if (combineMode == 0) {
					JavaSpriteBlitState.red = colour >> 16 & 0xFF;
					JavaSpriteBlitState.green = colour >> 8 & 0xFF;
					JavaSpriteBlitState.blue = colour & 0xFF;
				} else if (combineMode == 2) {
					JavaSpriteBlitState.lerpAlpha = colour >>> 24;
					JavaSpriteBlitState.lerpInvAlpha = 256 - JavaSpriteBlitState.lerpAlpha;
					@Pc(68) int redBlue = (colour & 0xFF00FF) * JavaSpriteBlitState.lerpInvAlpha & 0xFF00FF00;
					@Pc(76) int green = (colour & 0xFF00) * JavaSpriteBlitState.lerpInvAlpha & 0xFF0000;
					JavaSpriteBlitState.lerpColour = (redBlue | green) >>> 8;
				}
			}
			if (combineMode == 1) {
				this.blitParallelogram(1);
			} else if (combineMode == 0) {
				this.blitParallelogram(0);
			} else if (combineMode == 3) {
				this.blitParallelogram(3);
			} else if (combineMode == 2) {
				this.blitParallelogram(2);
			}
		}
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(IIIIIIIII)V")
	public abstract void renderDepthTestedRgb(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int width, @OriginalArg(4) int height, @OriginalArg(5) int op, @OriginalArg(6) int colour, @OriginalArg(7) int mode);

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(IIIIIIII)V")
	public abstract void renderImpl(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int combineMode, @OriginalArg(5) int colorArgb, @OriginalArg(6) int blendMode);

	@OriginalMember(owner = "client!nu", name = "b", descriptor = "()I")
	@Override
	public final int getHeight() {
		return this.imageHeight;
	}

	@OriginalMember(owner = "client!nu", name = "d", descriptor = "()I")
	@Override
	public final int scaleHeight() {
		return this.topMargin + this.imageHeight + this.trimBottom;
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "()I")
	@Override
	public final int scaleWidth() {
		return this.leftMargin + this.imageWidth + this.trimRight;
	}

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(IILclient!aa;II)V")
	public abstract void drawWithSurface(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) ComponentSurface surface, @OriginalArg(3) int surfaceOffsetX, @OriginalArg(4) int surfaceOffsetY);

	@OriginalMember(owner = "client!nu", name = "a", descriptor = "(FFFFFF)Z")
	private boolean computeAffineTransform(@OriginalArg(0) float topLeftX, @OriginalArg(1) float topLeftY, @OriginalArg(2) float topRightX, @OriginalArg(3) float topRightY, @OriginalArg(4) float bottomLeftX, @OriginalArg(5) float bottomLeftY) {
		@Pc(8) int scaleWidth = this.leftMargin + this.imageWidth + this.trimRight;
		@Pc(17) int scaleHeight = this.topMargin + this.imageHeight + this.trimBottom;
		@Pc(32) float local32;
		@Pc(39) float local39;
		@Pc(46) float local46;
		@Pc(53) float local53;
		@Pc(59) float local59;
		@Pc(65) float local65;
		@Pc(71) float local71;
		@Pc(77) float local77;
		if (scaleWidth != this.imageWidth || scaleHeight != this.imageHeight) {
			float columnStepX = (topRightX - topLeftX) / (float) scaleWidth;
			float columnStepY = (topRightY - topLeftY) / (float) scaleWidth;
			float rowStepX = (bottomLeftX - topLeftX) / (float) scaleHeight;
			float rowStepY = (bottomLeftY - topLeftY) / (float) scaleHeight;
			float topInsetX = rowStepX * (float) this.topMargin;
			float topInsetY = rowStepY * (float) this.topMargin;
			float leftInsetX = columnStepX * (float) this.leftMargin;
			float leftInsetY = columnStepY * (float) this.leftMargin;
			@Pc(84) float rightX = -columnStepX * (float) this.trimRight;
			@Pc(91) float rightY = -columnStepY * (float) this.trimRight;
			@Pc(98) float bottomX = -rowStepX * (float) this.trimBottom;
			@Pc(105) float bottomY = -rowStepY * (float) this.trimBottom;
			topLeftX += leftInsetX + topInsetX;
			topLeftY += leftInsetY + topInsetY;
			topRightX += rightX + topInsetX;
			topRightY += rightY + topInsetY;
			bottomLeftX += leftInsetX + bottomX;
			bottomLeftY += leftInsetY + bottomY;
		}
		float bottomRightX = bottomLeftX + topRightX - topLeftX;
		float bottomRightY = topRightY + bottomLeftY - topLeftY;
		float minX;
		float maxX;
		if (topLeftX < topRightX) {
			minX = topLeftX;
			maxX = topRightX;
		} else {
			minX = topRightX;
			maxX = topLeftX;
		}
		if (bottomLeftX < minX) {
			minX = bottomLeftX;
		}
		if (bottomRightX < minX) {
			minX = bottomRightX;
		}
		if (bottomLeftX > maxX) {
			maxX = bottomLeftX;
		}
		if (bottomRightX > maxX) {
			maxX = bottomRightX;
		}
		float minY;
		float maxY;
		if (topLeftY < topRightY) {
			minY = topLeftY;
			maxY = topRightY;
		} else {
			minY = topRightY;
			maxY = topLeftY;
		}
		if (bottomLeftY < minY) {
			minY = bottomLeftY;
		}
		if (bottomRightY < minY) {
			minY = bottomRightY;
		}
		if (bottomLeftY > maxY) {
			maxY = bottomLeftY;
		}
		if (bottomRightY > maxY) {
			maxY = bottomRightY;
		}
		if (minX < (float) this.toolkit.clipX1) {
			minX = this.toolkit.clipX1;
		}
		if (maxX > (float) this.toolkit.clipX2) {
			maxX = this.toolkit.clipX2;
		}
		if (minY < (float) this.toolkit.clipY1) {
			minY = this.toolkit.clipY1;
		}
		if (maxY > (float) this.toolkit.clipY2) {
			maxY = this.toolkit.clipY2;
		}
		maxX = minX - maxX;
		if (maxX >= 0.0F) {
			return false;
		}
		maxY = minY - maxY;
		if (maxY >= 0.0F) {
			return false;
		}
		JavaSpriteBlitState.viewportStride = this.toolkit.surfaceWidth;
		JavaSpriteBlitState.pixelOffset = (int) ((float) ((int) minY * JavaSpriteBlitState.viewportStride) + minX);
		float uDeterminant = (topRightX - topLeftX) * (bottomLeftY - topLeftY) - (topRightY - topLeftY) * (bottomLeftX - topLeftX);
		float vDeterminant = (bottomLeftX - topLeftX) * (topRightY - topLeftY) - (bottomLeftY - topLeftY) * (topRightX - topLeftX);
		JavaSpriteBlitState.dudx = (int) ((bottomLeftY - topLeftY) * 4096.0F * (float) this.imageWidth / uDeterminant);
		JavaSpriteBlitState.dvdx = (int) ((topRightY - topLeftY) * 4096.0F * (float) this.imageHeight / vDeterminant);
		JavaSpriteBlitState.dudy = (int) ((bottomLeftX - topLeftX) * 4096.0F * (float) this.imageWidth / vDeterminant);
		JavaSpriteBlitState.dvdy = (int) ((topRightX - topLeftX) * 4096.0F * (float) this.imageHeight / uDeterminant);
		JavaSpriteBlitState.subpixelOffsetX = (int) (minX * 16.0F + 8.0F - (topLeftX + topRightX + bottomLeftX + bottomRightX) / 4.0F * 16.0F);
		JavaSpriteBlitState.subpixelOffsetY = (int) (minY * 16.0F + 8.0F - (topLeftY + topRightY + bottomLeftY + bottomRightY) / 4.0F * 16.0F);
		JavaSpriteBlitState.uBase = (this.imageWidth >> 1 << 12) + (JavaSpriteBlitState.subpixelOffsetY * JavaSpriteBlitState.dudy >> 4);
		JavaSpriteBlitState.vBase = (this.imageHeight >> 1 << 12) + (JavaSpriteBlitState.subpixelOffsetY * JavaSpriteBlitState.dvdy >> 4);
		JavaSpriteBlitState.uOffsetX = JavaSpriteBlitState.subpixelOffsetX * JavaSpriteBlitState.dudx >> 4;
		JavaSpriteBlitState.vOffsetX = JavaSpriteBlitState.subpixelOffsetX * JavaSpriteBlitState.dvdx >> 4;
		JavaSpriteBlitState.drawMinX = (int) minX;
		JavaSpriteBlitState.drawWidth = (int) maxX;
		JavaSpriteBlitState.drawMinY = (int) minY;
		JavaSpriteBlitState.drawHeight = (int) maxY;
		return true;
	}
}
