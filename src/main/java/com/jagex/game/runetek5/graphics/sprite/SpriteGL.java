package com.jagex.game.runetek5.graphics.sprite;

import com.jagex.game.runetek5.config.rsinterface.GlClippingMask;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.OpenGl2DImageTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!nda")
public final class SpriteGL extends NativeSprite {

	@OriginalMember(owner = "client!nda", name = "h", descriptor = "Lclient!eba;")
	private OpenGl2DImageTexture channelMaskTexture;

	@OriginalMember(owner = "client!nda", name = "f", descriptor = "I")
	private int channelMaskIndex = 0;

	@OriginalMember(owner = "client!nda", name = "i", descriptor = "I")
	private int trimBottom = 0;

	@OriginalMember(owner = "client!nda", name = "d", descriptor = "Z")
	private boolean stretched = false;

	@OriginalMember(owner = "client!nda", name = "w", descriptor = "I")
	private int translateY = 0;

	@OriginalMember(owner = "client!nda", name = "x", descriptor = "I")
	private int translateX = 0;

	@OriginalMember(owner = "client!nda", name = "z", descriptor = "I")
	private int trimRight = 0;

	@OriginalMember(owner = "client!nda", name = "g", descriptor = "Lclient!qo;")
	private final GlToolkit toolkit;

	@OriginalMember(owner = "client!nda", name = "k", descriptor = "Lclient!eba;")
	private final OpenGl2DImageTexture texture2d;

	@OriginalMember(owner = "client!nda", name = "<init>", descriptor = "(Lclient!qo;IIZ)V")
	public SpriteGL(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) boolean hasAlpha) {
		this.toolkit = glToolkit;
		this.texture2d = OpenGl2DImageTexture.createEmpty(glToolkit, hasAlpha ? 6408 : 6407, width, height);
	}

	@OriginalMember(owner = "client!nda", name = "<init>", descriptor = "(Lclient!qo;IIII)V")
	public SpriteGL(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int width, @OriginalArg(4) int height) {
		this.toolkit = glToolkit;
		this.texture2d = OpenGl2DImageTexture.createFromScreenCapture(glToolkit, x, width, y, height);
	}

	@OriginalMember(owner = "client!nda", name = "<init>", descriptor = "(Lclient!qo;II[III)V")
	public SpriteGL(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) int[] pixels, @OriginalArg(4) int pixelOffset, @OriginalArg(5) int rowLength) {
		this.toolkit = glToolkit;
		this.texture2d = OpenGl2DImageTexture.createFromPixels(pixels, pixelOffset, rowLength, height, width, glToolkit);
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(FFFFFFIIII)V")
	@Override
	public void method6201(@OriginalArg(0) float topLeftX, @OriginalArg(1) float topLeftY, @OriginalArg(2) float topRightX, @OriginalArg(3) float topRightY, @OriginalArg(4) float bottomLeftX, @OriginalArg(5) float bottomLeftY, @OriginalArg(6) int combineMode, @OriginalArg(7) int colorArgb) {
		@Pc(10) float local10;
		@Pc(14) float local14;
		if (this.stretched) {
			float spriteWidth = this.scaleWidth();
			float spriteHeight = this.scaleHeight();
			@Pc(20) float uX = (topRightX - topLeftX) / spriteWidth;
			@Pc(26) float uY = (topRightY - topLeftY) / spriteWidth;
			@Pc(33) float vX = (bottomLeftX - topLeftX) / spriteHeight;
			@Pc(40) float vY = (bottomLeftY - topLeftY) / spriteHeight;
			@Pc(46) float padTopX = (float) this.translateY * vX;
			@Pc(52) float padTopY = (float) this.translateY * vY;
			@Pc(58) float padLeftX = (float) this.translateX * uX;
			@Pc(64) float padLeftY = (float) this.translateX * uY;
			@Pc(71) float trimRightX = -uX * (float) this.trimRight;
			@Pc(78) float trimRightY = (float) this.trimRight * -uY;
			@Pc(85) float trimBottomX = -vX * (float) this.trimBottom;
			bottomLeftX = trimBottomX + bottomLeftX + padLeftX;
			topRightX = topRightX + trimRightX + padTopX;
			@Pc(104) float trimBottomY = (float) this.trimBottom * -vY;
			topRightY = topRightY + trimRightY + padTopY;
			topLeftX = topLeftX + padLeftX + padTopX;
			topLeftY = padTopY + padLeftY + topLeftY;
			bottomLeftY = trimBottomY + bottomLeftY + padLeftY;
		}
		float bottomRightX = topRightX + bottomLeftX - topLeftX;
		float bottomRightY = bottomLeftY + topRightY - topLeftY;
		this.texture2d.setLinearFilter(true);
		this.toolkit.setupSpriteRendering();
		this.toolkit.setActiveTexture(this.texture2d);
		this.toolkit.setBlendMode(1);
		this.toolkit.setColourOp(combineMode);
		OpenGL.glColor4ub((byte) (colorArgb >> 16), (byte) (colorArgb >> 8), (byte) colorArgb, (byte) (colorArgb >> 24));
		OpenGL.glBegin(OpenGLConstants.GL_QUADS);
		OpenGL.glTexCoord2f(0.0F, this.texture2d.t);
		OpenGL.glVertex2f(topLeftX, topLeftY);
		OpenGL.glTexCoord2f(0.0F, 0.0F);
		OpenGL.glVertex2f(bottomLeftX, bottomLeftY);
		OpenGL.glTexCoord2f(this.texture2d.s, 0.0F);
		OpenGL.glVertex2f(bottomRightX, bottomRightY);
		OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t);
		OpenGL.glVertex2f(topRightX, topRightY);
		OpenGL.glEnd();
	}

	@OriginalMember(owner = "client!nda", name = "b", descriptor = "()I")
	@Override
	public int getHeight() {
		return this.texture2d.singleLength;
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(III)V")
	@Override
	public void copyAlpha(@OriginalArg(2) int channel) {
		OpenGL.glPixelTransferf(OpenGLConstants.GL_RED_SCALE, 0.5F);
		OpenGL.glPixelTransferf(OpenGLConstants.GL_RED_BIAS, 0.499F);
		OpenGL.glPixelTransferf(OpenGLConstants.GL_GREEN_SCALE, 0.5F);
		OpenGL.glPixelTransferf(OpenGLConstants.GL_GREEN_BIAS, 0.499F);
		OpenGL.glPixelTransferf(OpenGLConstants.GL_BLUE_SCALE, 0.5F);
		OpenGL.glPixelTransferf(OpenGLConstants.GL_BLUE_BIAS, 0.499F);
		this.channelMaskTexture = OpenGl2DImageTexture.createFromScreenCapture(this.toolkit, 0, this.texture2d.singleWidth, 0, this.texture2d.singleLength);
		this.channelMaskIndex = channel;
		OpenGL.glPixelTransferf(OpenGLConstants.GL_RED_SCALE, 1.0F);
		OpenGL.glPixelTransferf(OpenGLConstants.GL_RED_BIAS, 0.0F);
		OpenGL.glPixelTransferf(OpenGLConstants.GL_GREEN_SCALE, 1.0F);
		OpenGL.glPixelTransferf(OpenGLConstants.GL_GREEN_BIAS, 0.0F);
		OpenGL.glPixelTransferf(OpenGLConstants.GL_BLUE_SCALE, 1.0F);
		OpenGL.glPixelTransferf(OpenGLConstants.GL_BLUE_BIAS, 0.0F);
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "([I)V")
	@Override
	public void projectOffsets(@OriginalArg(0) int[] destination) {
		destination[1] = this.translateY;
		destination[3] = this.trimBottom;
		destination[0] = this.translateX;
		destination[2] = this.trimRight;
	}

	@OriginalMember(owner = "client!nda", name = "d", descriptor = "()I")
	@Override
	public int scaleHeight() {
		return this.translateY + this.texture2d.singleLength + this.trimBottom;
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(IIIIII)V")
	@Override
	public void captureScreenRegion(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int op, @OriginalArg(5) int colour) {
		this.texture2d.copyTextureSubImage(x, height, colour, y, op, width);
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(IILclient!aa;II)V")
	@Override
	public void drawWithSurface(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) ComponentSurface clippingMask, @OriginalArg(3) int surfaceOffsetX, @OriginalArg(4) int surfaceOffsetY) {
		@Pc(6) GlClippingMask glMask = (GlClippingMask) clippingMask;
		@Pc(9) OpenGl2DImageTexture surface = glMask.surface;
		this.texture2d.setLinearFilter(false);
		this.toolkit.setupSpriteRendering();
		this.toolkit.setActiveTexture(this.texture2d);
		this.toolkit.setColourOp(1);
		this.toolkit.activateTexture(1);
		this.toolkit.setActiveTexture(surface);
		this.toolkit.loadTextureCombineMode(7681, 8448);
		this.toolkit.setTexture(34168, 768, 0);
		this.toolkit.setBlendMode(1);
		@Pc(62) int drawX = x + this.translateX;
		@Pc(67) int drawY = y + this.translateY;
		@Pc(73) int endX = this.texture2d.singleWidth + drawX;
		@Pc(80) int endY = drawY + this.texture2d.singleLength;
		@Pc(87) float sPerPixel = surface.s / (float) surface.singleWidth;
		@Pc(94) float tPerPixel = surface.t / (float) surface.singleLength;
		@Pc(102) float s0 = sPerPixel * (float) (drawX - surfaceOffsetX);
		@Pc(109) float s1 = sPerPixel * (float) (endX - surfaceOffsetX);
		@Pc(119) float t0 = surface.t - tPerPixel * (float) (drawY - surfaceOffsetY);
		@Pc(131) float t1 = surface.t - (float) (endY - surfaceOffsetY) * tPerPixel;
		OpenGL.glBegin(OpenGLConstants.GL_QUADS);
		OpenGL.glColor3f(1.0F, 1.0F, 1.0F);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, 0.0F, this.texture2d.t);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, s0, t0);
		OpenGL.glVertex2i(drawX, drawY);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, 0.0F, 0.0F);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, s0, t1);
		OpenGL.glVertex2i(drawX, drawY + this.texture2d.singleLength);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, this.texture2d.s, 0.0F);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, s1, t1);
		OpenGL.glVertex2i(drawX + this.texture2d.singleWidth, this.texture2d.singleLength + drawY);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, this.texture2d.s, this.texture2d.t);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, s1, t0);
		OpenGL.glVertex2i(this.texture2d.singleWidth + drawX, drawY);
		OpenGL.glEnd();
		this.toolkit.setTexture(5890, 768, 0);
		this.toolkit.setColourOp(0);
		this.toolkit.setActiveTexture(null);
		this.toolkit.activateTexture(0);
	}

	@OriginalMember(owner = "client!nda", name = "c", descriptor = "(IIII)V")
	@Override
	public void setOffsets(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int x2, @OriginalArg(3) int y2) {
		this.translateX = x1;
		this.trimRight = x2;
		this.translateY = y1;
		this.trimBottom = y2;
		this.stretched = this.translateX != 0 || this.translateY != 0 || this.trimRight != 0 || this.trimBottom != 0;
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(B)V")
	private void resetChannelMaskState() {
		this.toolkit.activateTexture(1);
		this.toolkit.setActiveTexture(null);
		this.toolkit.loadTextureCombineMode(8448, 8448);
		this.toolkit.setTexture(34168, 768, 1);
		this.toolkit.setTextureTransparency(0, 5890);
		this.toolkit.activateTexture(0);
		this.toolkit.setTexture(34168, 768, 1);
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(FFFFFFILclient!aa;II)V")
	@Override
	public void renderParallelogramImpl(@OriginalArg(0) float x0, @OriginalArg(1) float y0, @OriginalArg(2) float x1, @OriginalArg(3) float y1, @OriginalArg(4) float x2, @OriginalArg(5) float y2, @OriginalArg(7) ComponentSurface mask, @OriginalArg(8) int maskX, @OriginalArg(9) int maskY) {
		@Pc(7) OpenGl2DImageTexture surface = ((GlClippingMask) mask).surface;
		@Pc(14) float local14;
		@Pc(18) float local18;
		@Pc(25) float local25;
		@Pc(32) float local32;
		if (this.stretched) {
			float spriteWidth = this.scaleWidth();
			float spriteHeight = this.scaleHeight();
			float uX = (x1 - x0) / spriteWidth;
			float uY = (y1 - y0) / spriteWidth;
			@Pc(39) float vX = (x2 - x0) / spriteHeight;
			@Pc(46) float vY = (y2 - y0) / spriteHeight;
			@Pc(52) float padTopX = vX * (float) this.translateY;
			@Pc(58) float padTopY = vY * (float) this.translateY;
			@Pc(64) float padLeftX = (float) this.translateX * uX;
			@Pc(70) float padLeftY = (float) this.translateX * uY;
			@Pc(77) float trimRightX = -uX * (float) this.trimRight;
			@Pc(84) float trimRightY = -uY * (float) this.trimRight;
			@Pc(91) float trimBottomX = -vX * (float) this.trimBottom;
			y1 = y1 + trimRightY + padTopY;
			x1 = trimRightX + x1 + padTopX;
			x2 = trimBottomX + padLeftX + x2;
			x0 = padLeftX + x0 + padTopX;
			y0 = padTopY + y0 + padLeftY;
			@Pc(128) float trimBottomY = -vY * (float) this.trimBottom;
			y2 = padLeftY + y2 + trimBottomY;
		}
		float x3 = x1 + x2 - x0;
		this.texture2d.setLinearFilter(true);
		float y3 = y2 + y1 - y0;
		this.toolkit.setupSpriteRendering();
		this.toolkit.setActiveTexture(this.texture2d);
		this.toolkit.setColourOp(1);
		this.toolkit.activateTexture(1);
		this.toolkit.setActiveTexture(surface);
		this.toolkit.loadTextureCombineMode(7681, 8448);
		this.toolkit.setTexture(34168, 768, 0);
		this.toolkit.setBlendMode(1);
		float maskScaleS = surface.s / (float) surface.singleWidth;
		float maskScaleT = surface.t / (float) surface.singleLength;
		OpenGL.glBegin(OpenGLConstants.GL_QUADS);
		OpenGL.glColor3f(1.0F, 1.0F, 1.0F);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, 0.0F, this.texture2d.t);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, maskScaleS * (x0 - (float) maskX), surface.t - maskScaleT * ((float) -maskY + y0));
		OpenGL.glVertex2f(x0, y0);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, 0.0F, 0.0F);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, ((float) -maskX + x2) * maskScaleS, surface.t - maskScaleT * (y2 - (float) maskY));
		OpenGL.glVertex2f(x2, y2);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, this.texture2d.s, 0.0F);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, (x3 - (float) maskX) * maskScaleS, surface.t - maskScaleT * (y3 - (float) maskY));
		OpenGL.glVertex2f(x3, y3);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE0, this.texture2d.s, this.texture2d.t);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, maskScaleS * ((float) -maskX + x1), surface.t - maskScaleT * (y1 - (float) maskY));
		OpenGL.glVertex2f(x1, y1);
		OpenGL.glEnd();
		this.toolkit.setTexture(5890, 768, 0);
		this.toolkit.setColourOp(0);
		this.toolkit.setActiveTexture(null);
		this.toolkit.activateTexture(0);
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "()I")
	@Override
	public int scaleWidth() {
		return this.translateX + this.texture2d.singleWidth + this.trimRight;
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(IIIII)V")
	@Override
	public void render(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int op, @OriginalArg(3) int color, @OriginalArg(4) int mode) {
		this.texture2d.setLinearFilter(false);
		this.toolkit.setupSpriteRendering();
		this.toolkit.setBlendMode(mode);
		OpenGL.glColor4ub((byte) (color >> 16), (byte) (color >> 8), (byte) color, (byte) (color >> 24));
		@Pc(37) int drawX = x + this.translateX;
		@Pc(42) int drawY = y + this.translateY;
		if (this.channelMaskTexture == null) {
			this.toolkit.setActiveTexture(this.texture2d);
			this.toolkit.setColourOp(op);
			OpenGL.glBegin(OpenGLConstants.GL_QUADS);
			OpenGL.glTexCoord2f(0.0F, this.texture2d.t);
			OpenGL.glVertex2i(drawX, drawY);
			OpenGL.glTexCoord2f(0.0F, 0.0F);
			OpenGL.glVertex2i(drawX, drawY + this.texture2d.singleLength);
			OpenGL.glTexCoord2f(this.texture2d.s, 0.0F);
			OpenGL.glVertex2i(this.texture2d.singleWidth + drawX, this.texture2d.singleLength + drawY);
			OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t);
			OpenGL.glVertex2i(this.texture2d.singleWidth + drawX, drawY);
			OpenGL.glEnd();
			return;
		}
		this.setupChannelMaskRendering(op);
		this.channelMaskTexture.setLinearFilter(false);
		OpenGL.glBegin(OpenGLConstants.GL_QUADS);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0F, this.texture2d.t);
		OpenGL.glTexCoord2f(0.0F, this.texture2d.t);
		OpenGL.glVertex2i(drawX, drawY);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0F, 0.0F);
		OpenGL.glTexCoord2f(0.0F, 0.0F);
		OpenGL.glVertex2i(drawX, drawY + this.texture2d.singleLength);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, 0.0F);
		OpenGL.glTexCoord2f(this.texture2d.s, 0.0F);
		OpenGL.glVertex2i(this.texture2d.singleWidth + drawX, this.texture2d.singleLength + drawY);
		OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, this.texture2d.t);
		OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t);
		OpenGL.glVertex2i(drawX + this.texture2d.singleWidth, drawY);
		OpenGL.glEnd();
		this.resetChannelMaskState();
	}

	@OriginalMember(owner = "client!nda", name = "b", descriptor = "(II)V")
	private void setupChannelMaskRendering(@OriginalArg(0) int combineMode) {
		this.toolkit.activateTexture(1);
		this.toolkit.setActiveTexture(this.texture2d);
		this.toolkit.loadTextureCombineMode(this.toolkit.getCombineModeEffectType(combineMode), 7681);
		this.toolkit.setTexture(34167, 768, 1);
		this.toolkit.setTextureTransparency(0, 34168);
		this.toolkit.activateTexture(0);
		this.toolkit.setActiveTexture(this.channelMaskTexture);
		this.toolkit.loadTextureCombineMode(34479, 7681);
		this.toolkit.setTexture(34166, 768, 1);
		if (this.channelMaskIndex == 0) {
			this.toolkit.updatePointSizeRange(0.0F, 1.0F, 0.5F, 0.5F);
		} else if (this.channelMaskIndex == 1) {
			this.toolkit.updatePointSizeRange(0.0F, 0.5F, 0.5F, 1.0F);
		} else if (this.channelMaskIndex == 2) {
			this.toolkit.updatePointSizeRange(0.0F, 0.5F, 1.0F, 0.5F);
		} else if (this.channelMaskIndex == 3) {
			this.toolkit.updatePointSizeRange(0.0F, 128.5F, 128.5F, 128.5F);
			return;
		}
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(IIIIIII)V")
	@Override
	public void drawScaledColored(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int op, @OriginalArg(5) int colour, @OriginalArg(6) int mode) {
		@Pc(9) int endX = width + x;
		@Pc(13) int endY = height + y;
		this.texture2d.setLinearFilter(false);
		this.toolkit.setupSpriteRendering();
		this.toolkit.setActiveTexture(this.texture2d);
		this.toolkit.setBlendMode(mode);
		this.toolkit.setColourOp(op);
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		if (this.texture2d.tiled && !this.stretched) {
			@Pc(72) float tileT = this.texture2d.t * (float) height / (float) this.texture2d.singleLength;
			@Pc(84) float tileS = (float) width * this.texture2d.s / (float) this.texture2d.singleWidth;
			OpenGL.glBegin(OpenGLConstants.GL_QUADS);
			OpenGL.glTexCoord2f(0.0F, tileT);
			OpenGL.glVertex2i(x, y);
			OpenGL.glTexCoord2f(0.0F, 0.0F);
			OpenGL.glVertex2i(x, endY);
			OpenGL.glTexCoord2f(tileS, 0.0F);
			OpenGL.glVertex2i(endX, endY);
			OpenGL.glTexCoord2f(tileS, tileT);
			OpenGL.glVertex2i(endX, y);
			OpenGL.glEnd();
			return;
		}
		OpenGL.glPushMatrix();
		OpenGL.glTranslatef((float) this.translateX, (float) this.translateY, 0.0F);
		@Pc(124) int spriteWidth = this.scaleWidth();
		@Pc(127) int spriteHeight = this.scaleHeight();
		@Pc(134) int tileEndY = y + this.texture2d.singleLength;
		OpenGL.glBegin(OpenGLConstants.GL_QUADS);
		@Pc(138) int tileY = y;
		@Pc(146) int local146;
		while (tileEndY <= endY) {
			int tileRightX = x + this.texture2d.singleWidth;
			@Pc(148) int tileX = x;
			while (endX >= tileRightX) {
				OpenGL.glTexCoord2f(0.0F, this.texture2d.t);
				OpenGL.glVertex2i(tileX, tileY);
				OpenGL.glTexCoord2f(0.0F, 0.0F);
				OpenGL.glVertex2i(tileX, tileEndY);
				OpenGL.glTexCoord2f(this.texture2d.s, 0.0F);
				OpenGL.glVertex2i(tileRightX, tileEndY);
				OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t);
				OpenGL.glVertex2i(tileRightX, tileY);
				tileX += spriteWidth;
				tileRightX += spriteWidth;
			}
			if (tileX < endX) {
				@Pc(211) float partialS = (float) (endX - tileX) * this.texture2d.s / (float) this.texture2d.singleWidth;
				OpenGL.glTexCoord2f(0.0F, this.texture2d.t);
				OpenGL.glVertex2i(tileX, tileY);
				OpenGL.glTexCoord2f(0.0F, 0.0F);
				OpenGL.glVertex2i(tileX, tileEndY);
				OpenGL.glTexCoord2f(partialS, 0.0F);
				OpenGL.glVertex2i(endX, tileEndY);
				OpenGL.glTexCoord2f(partialS, this.texture2d.t);
				OpenGL.glVertex2i(endX, tileY);
			}
			tileY += spriteHeight;
			tileEndY += spriteHeight;
		}
		if (endY > tileY) {
			@Pc(276) float partialT = this.texture2d.t * (float) (this.texture2d.singleLength + tileY - endY) / (float) this.texture2d.singleLength;
			@Pc(282) int tileEndX = x + this.texture2d.singleWidth;
			int tileX = x;
			while (tileEndX <= endX) {
				OpenGL.glTexCoord2f(0.0F, this.texture2d.t);
				OpenGL.glVertex2i(tileX, tileY);
				OpenGL.glTexCoord2f(0.0F, partialT);
				OpenGL.glVertex2i(tileX, endY);
				OpenGL.glTexCoord2f(this.texture2d.s, partialT);
				OpenGL.glVertex2i(tileEndX, endY);
				OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t);
				OpenGL.glVertex2i(tileEndX, tileY);
				tileEndX += spriteWidth;
				tileX += spriteWidth;
			}
			if (endX > tileX) {
				@Pc(350) float partialS = (float) (endX - tileX) * this.texture2d.s / (float) this.texture2d.singleWidth;
				OpenGL.glTexCoord2f(0.0F, this.texture2d.t);
				OpenGL.glVertex2i(tileX, tileY);
				OpenGL.glTexCoord2f(0.0F, partialT);
				OpenGL.glVertex2i(tileX, endY);
				OpenGL.glTexCoord2f(partialS, partialT);
				OpenGL.glVertex2i(endX, endY);
				OpenGL.glTexCoord2f(partialS, this.texture2d.t);
				OpenGL.glVertex2i(endX, tileY);
			}
		}
		OpenGL.glEnd();
		OpenGL.glPopMatrix();
	}

	@OriginalMember(owner = "client!nda", name = "c", descriptor = "()I")
	@Override
	public int getWidth() {
		return this.texture2d.singleWidth;
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(IIIIIIII)V")
	@Override
	public void renderImpl(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int combineMode, @OriginalArg(5) int colorArgb, @OriginalArg(6) int blendMode) {
		this.texture2d.setLinearFilter(true);
		this.toolkit.setupSpriteRendering();
		this.toolkit.setBlendMode(blendMode);
		OpenGL.glColor4ub((byte) (colorArgb >> 16), (byte) (colorArgb >> 8), (byte) colorArgb, (byte) (colorArgb >> 24));
		if (this.stretched) {
			@Pc(48) float scaleX = (float) width / (float) this.scaleWidth();
			@Pc(55) float scaleY = (float) height / (float) this.scaleHeight();
			@Pc(64) float x0 = (float) x + scaleX * (float) this.translateX;
			@Pc(73) float y0 = (float) this.translateY * scaleY + (float) y;
			@Pc(82) float x1 = x0 + scaleX * (float) this.texture2d.singleWidth;
			@Pc(91) float y1 = y0 + (float) this.texture2d.singleLength * scaleY;
			if (this.channelMaskTexture == null) {
				this.toolkit.setActiveTexture(this.texture2d);
				this.toolkit.setColourOp(combineMode);
				OpenGL.glBegin(OpenGLConstants.GL_QUADS);
				OpenGL.glTexCoord2f(0.0F, this.texture2d.t);
				OpenGL.glVertex2f(x0, y0);
				OpenGL.glTexCoord2f(0.0F, 0.0F);
				OpenGL.glVertex2f(x0, y1);
				OpenGL.glTexCoord2f(this.texture2d.s, 0.0F);
				OpenGL.glVertex2f(x1, y1);
				OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t);
				OpenGL.glVertex2f(x1, y0);
				OpenGL.glEnd();
			} else {
				this.setupChannelMaskRendering(combineMode);
				this.channelMaskTexture.setLinearFilter(true);
				OpenGL.glBegin(OpenGLConstants.GL_QUADS);
				OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0F, this.texture2d.t);
				OpenGL.glTexCoord2f(0.0F, this.texture2d.t);
				OpenGL.glVertex2f(x0, y0);
				OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0F, 0.0F);
				OpenGL.glTexCoord2f(0.0F, 0.0F);
				OpenGL.glVertex2f(x0, y1);
				OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, 0.0F);
				OpenGL.glTexCoord2f(this.texture2d.s, 0.0F);
				OpenGL.glVertex2f(x1, y1);
				OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, this.texture2d.t);
				OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t);
				OpenGL.glVertex2f(x1, y0);
				OpenGL.glEnd();
				this.resetChannelMaskState();
			}
		} else if (this.channelMaskTexture == null) {
			this.toolkit.setActiveTexture(this.texture2d);
			this.toolkit.setColourOp(combineMode);
			OpenGL.glBegin(OpenGLConstants.GL_QUADS);
			OpenGL.glTexCoord2f(0.0F, this.texture2d.t);
			OpenGL.glVertex2i(x, y);
			OpenGL.glTexCoord2f(0.0F, 0.0F);
			OpenGL.glVertex2i(x, y + height);
			OpenGL.glTexCoord2f(this.texture2d.s, 0.0F);
			OpenGL.glVertex2i(width + x, height + y);
			OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t);
			OpenGL.glVertex2i(x + width, y);
			OpenGL.glEnd();
		} else {
			this.setupChannelMaskRendering(combineMode);
			this.channelMaskTexture.setLinearFilter(true);
			OpenGL.glBegin(OpenGLConstants.GL_QUADS);
			OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0F, this.texture2d.t);
			OpenGL.glTexCoord2f(0.0F, this.texture2d.t);
			OpenGL.glVertex2i(x, y);
			OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, 0.0F, 0.0F);
			OpenGL.glTexCoord2f(0.0F, 0.0F);
			OpenGL.glVertex2i(x, height + y);
			OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, 0.0F);
			OpenGL.glTexCoord2f(this.texture2d.s, 0.0F);
			OpenGL.glVertex2i(x + width, y + height);
			OpenGL.glMultiTexCoord2f(OpenGLConstants.GL_TEXTURE1, this.texture2d.s, this.texture2d.t);
			OpenGL.glTexCoord2f(this.texture2d.s, this.texture2d.t);
			OpenGL.glVertex2i(width + x, y);
			OpenGL.glEnd();
			this.resetChannelMaskState();
		}
	}
}
