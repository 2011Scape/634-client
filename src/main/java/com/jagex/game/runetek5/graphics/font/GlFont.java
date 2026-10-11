package com.jagex.game.runetek5.graphics.font;

import com.jagex.game.runetek5.config.rsinterface.GlClippingMask;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.toolkit.opengl.GlRenderList;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.OpenGl2DImageTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!th")
public final class GlFont extends AbstractFontRenderer {

	@OriginalMember(owner = "client!th", name = "v", descriptor = "Lclient!qo;")
	private final GlToolkit glToolkit;

	@OriginalMember(owner = "client!th", name = "x", descriptor = "Lclient!eba;")
	private final OpenGl2DImageTexture texture;

	@OriginalMember(owner = "client!th", name = "y", descriptor = "Z")
	private final boolean isAlphaOnlyTexture;

	@OriginalMember(owner = "client!th", name = "w", descriptor = "Lclient!jv;")
	private final GlRenderList glyphLists;

	@OriginalMember(owner = "client!th", name = "<init>", descriptor = "(Lclient!qo;Lclient!oea;[Lclient!rh;Z)V")
	public GlFont(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) FontMetrics fontMetrics, @OriginalArg(2) SpriteType[] indexedImages, @OriginalArg(3) boolean isAlphaOnly) {
		super(glToolkit, fontMetrics);
		this.glToolkit = glToolkit;
		@Pc(8) int maxDimension = 0;
		for (@Pc(10) int i = 0; i < 256; i++) {
			@Pc(15) SpriteType indexedImage = indexedImages[i];
			if (indexedImage.height > maxDimension) {
				maxDimension = indexedImage.height;
			}
			if (indexedImage.width > maxDimension) {
				maxDimension = indexedImage.width;
			}
		}
		@Pc(37) int atlasSize = maxDimension * 16;
		@Pc(44) byte[] pixels = new byte[atlasSize * atlasSize];
		@Pc(57) int local57;
		@Pc(63) int local63;
		@Pc(69) int local69;
		@Pc(75) int local75;
		for (@Pc(46) int j = 0; j < 256; j++) {
			@Pc(51) SpriteType indexedImage2 = indexedImages[j];
			@Pc(54) int height = indexedImage2.height;
			int width = indexedImage2.width;
			int cellX = j % 16 * maxDimension;
			int cellY = j / 16 * maxDimension;
			int destIndex = cellY * atlasSize + cellX;
			@Pc(77) int srcIndex = 0;
			@Pc(83) byte[] srcBytes;
			@Pc(85) int y;
			@Pc(88) int x;
			if (indexedImage2.alpha == null) {
				srcBytes = indexedImage2.pixels;
				for (y = 0; y < height; y++) {
					for (x = 0; x < width; x++) {
						pixels[destIndex++] = (byte) (srcBytes[srcIndex++] == 0 ? 0 : -1);
					}
					destIndex += atlasSize - width;
				}
			} else {
				srcBytes = indexedImage2.alpha;
				for (y = 0; y < height; y++) {
					for (x = 0; x < width; x++) {
						pixels[destIndex++] = srcBytes[srcIndex++];
					}
					destIndex += atlasSize - width;
				}
			}
		}
		this.texture = OpenGl2DImageTexture.createSurface(pixels, atlasSize, glToolkit, atlasSize);
		this.isAlphaOnlyTexture = true;
		this.texture.setLinearFilter(false);
		this.glyphLists = new GlRenderList(glToolkit, 256);
		@Pc(334) float texelScaleS = this.texture.s / (float) this.texture.singleWidth;
		@Pc(343) float texelScaleT = this.texture.t / (float) this.texture.singleLength;
		for (@Pc(345) int k = 0; k < 256; k++) {
			@Pc(350) SpriteType indexedImage3 = indexedImages[k];
			int glyphHeight = indexedImage3.height;
			int glyphWidth = indexedImage3.width;
			int glyphOffsetY = indexedImage3.minY;
			int glyphOffsetX = indexedImage3.minX;
			@Pc(369) float atlasX = (float) (k % 16 * maxDimension);
			@Pc(376) float atlasY = (float) (k / 16 * maxDimension);
			@Pc(380) float u0 = atlasX * texelScaleS;
			@Pc(384) float v0 = atlasY * texelScaleT;
			@Pc(391) float u1 = (atlasX + (float) glyphWidth) * texelScaleS;
			@Pc(398) float v1 = (atlasY + (float) glyphHeight) * texelScaleT;
			this.glyphLists.newList(k);
			OpenGL.glBegin(OpenGLConstants.GL_QUADS);
			OpenGL.glTexCoord2f(u0, this.texture.t - v0);
			OpenGL.glVertex2i(glyphOffsetX, glyphOffsetY);
			OpenGL.glTexCoord2f(u0, this.texture.t - v1);
			OpenGL.glVertex2i(glyphOffsetX, glyphOffsetY + glyphHeight);
			OpenGL.glTexCoord2f(u1, this.texture.t - v1);
			OpenGL.glVertex2i(glyphOffsetX + glyphWidth, glyphOffsetY + glyphHeight);
			OpenGL.glTexCoord2f(u1, this.texture.t - v0);
			OpenGL.glVertex2i(glyphOffsetX + glyphWidth, glyphOffsetY);
			OpenGL.glEnd();
			this.glyphLists.endList();
		}
	}

	@OriginalMember(owner = "client!th", name = "a", descriptor = "(CIIIZLclient!aa;II)V")
	@Override
	public void renderSymbol(@OriginalArg(0) char c, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int colour, @OriginalArg(4) boolean shadow, @OriginalArg(5) ComponentSurface mask, @OriginalArg(6) int offsetX, @OriginalArg(7) int offsetY) {
		@Pc(2) GlClippingMask clippingMask = (GlClippingMask) mask;
		@Pc(5) OpenGl2DImageTexture surface = clippingMask.surface;
		this.glToolkit.setupTextRendering();
		this.glToolkit.setActiveTexture(this.texture);
		if (this.isAlphaOnlyTexture || shadow) {
			this.glToolkit.loadTextureCombineMode(7681, 8448);
			this.glToolkit.setTexture(34168, 768, 0);
		} else {
			this.glToolkit.loadTextureCombineMode(7681, 7681);
		}
		this.glToolkit.activateTexture(1);
		this.glToolkit.setActiveTexture(surface);
		this.glToolkit.loadTextureCombineMode(7681, 8448);
		this.glToolkit.setTexture(34168, 768, 0);
		OpenGL.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR);
		OpenGL.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR);
		@Pc(78) float texelScaleS = surface.s / (float) surface.singleWidth;
		@Pc(85) float texelScaleT = surface.t / (float) surface.singleLength;
		OpenGL.glTexGenfv(OpenGLConstants.GL_S, OpenGLConstants.GL_EYE_PLANE, new float[] { texelScaleS, 0.0F, 0.0F, (float) -offsetX * texelScaleS }, 0);
		OpenGL.glTexGenfv(OpenGLConstants.GL_T, OpenGLConstants.GL_EYE_PLANE, new float[] { 0.0F, texelScaleT, 0.0F, (float) -offsetY * texelScaleT }, 0);
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S);
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T);
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		OpenGL.glTranslatef((float) x, (float) y, 0.0F);
		this.glyphLists.callList(c);
		OpenGL.glLoadIdentity();
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S);
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T);
		this.glToolkit.setTexture(5890, 768, 0);
		this.glToolkit.loadTextureCombineMode(8448, 8448);
		this.glToolkit.setActiveTexture(null);
		this.glToolkit.activateTexture(0);
		if (this.isAlphaOnlyTexture || shadow) {
			this.glToolkit.setTexture(5890, 768, 0);
		}
	}

	@OriginalMember(owner = "client!th", name = "fa", descriptor = "(CIIIZ)V")
	@Override
	public void fa(@OriginalArg(0) char c, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int colour, @OriginalArg(4) boolean shadow) {
		this.glToolkit.setupTextRendering();
		this.glToolkit.setActiveTexture(this.texture);
		if (this.isAlphaOnlyTexture || shadow) {
			this.glToolkit.loadTextureCombineMode(7681, 8448);
			this.glToolkit.setTexture(34168, 768, 0);
		} else {
			this.glToolkit.loadTextureCombineMode(7681, 7681);
		}
		OpenGL.glColor4ub((byte) (colour >> 16), (byte) (colour >> 8), (byte) colour, (byte) (colour >> 24));
		OpenGL.glTranslatef((float) x, (float) y, 0.0F);
		this.glyphLists.callList(c);
		OpenGL.glLoadIdentity();
		if (this.isAlphaOnlyTexture || shadow) {
			this.glToolkit.setTexture(5890, 768, 0);
		}
	}
}
