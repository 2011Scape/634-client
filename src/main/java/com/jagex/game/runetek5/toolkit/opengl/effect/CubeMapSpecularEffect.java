package com.jagex.game.runetek5.toolkit.opengl.effect;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.toolkit.opengl.GlRenderList;
import com.jagex.game.runetek5.graphics.gl.GlTexture;
import com.jagex.game.runetek5.graphics.gl.GlCubeTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!wk")
public final class CubeMapSpecularEffect extends MaterialEffectGL {

	@OriginalMember(owner = "client!wk", name = "i", descriptor = "Lclient!jv;")
	private GlRenderList listUtils;

	@OriginalMember(owner = "client!wk", name = "o", descriptor = "Z")
	private boolean enabled = false;

	@OriginalMember(owner = "client!wk", name = "g", descriptor = "Z")
	private boolean isLimitedTextureUnits;

	@OriginalMember(owner = "client!wk", name = "p", descriptor = "[Lclient!q;")
	private GlCubeTexture[] cubeMapTextures;

	@OriginalMember(owner = "client!wk", name = "<init>", descriptor = "(Lclient!qo;)V")
	public CubeMapSpecularEffect(@OriginalArg(0) GlToolkit glToolkit) {
		super(glToolkit);
		if (glToolkit.arbTextureCubeMap) {
			this.isLimitedTextureUnits = glToolkit.maxTextureUnits < 3;
			@Pc(26) int maxIntensity = this.isLimitedTextureUnits ? 48 : 127;
			@Pc(30) byte[][] specular12 = new byte[6][4096];
			@Pc(34) byte[][] specular96 = new byte[6][4096];
			@Pc(38) byte[][] specular36 = new byte[6][4096];
			@Pc(40) int texelIndex = 0;
			for (@Pc(42) int i = 0; i < 64; i++) {
				for (@Pc(46) int j = 0; j < 64; j++) {
					@Pc(57) float rawY = (float) i * 2.0F / 64.0F - 1.0F;
					@Pc(66) float rawX = (float) j * 2.0F / 64.0F - 1.0F;
					@Pc(81) float nz = (float) (1.0D / Math.sqrt((double) (rawY * rawY + rawX * rawX + 1.0F)));
					@Pc(85) float nx = rawX * nz;
					@Pc(89) float ny = rawY * nz;
					for (@Pc(91) int k = 0; k < 6; k++) {
						@Pc(98) float dot;
						if (k == 0) {
							dot = -nx;
						} else if (k == 1) {
							dot = nx;
						} else if (k == 2) {
							dot = ny;
						} else if (k == 3) {
							dot = -ny;
						} else if (k == 4) {
							dot = nz;
						} else {
							dot = -nz;
						}
						@Pc(146) int intensity96;
						@Pc(145) int intensity36;
						@Pc(143) int intensity12;
						if (dot > 0.0F) {
							intensity96 = (int) ((double) maxIntensity * Math.pow((double) dot, 96.0D));
							intensity36 = (int) ((double) maxIntensity * Math.pow((double) dot, 36.0D));
							intensity12 = (int) ((double) maxIntensity * Math.pow((double) dot, 12.0D));
						} else {
							intensity12 = 0;
							intensity36 = 0;
							intensity96 = 0;
						}
						specular96[k][texelIndex] = (byte) intensity96;
						specular36[k][texelIndex] = (byte) intensity36;
						specular12[k][texelIndex] = (byte) intensity12;
					}
					texelIndex++;
				}
			}
			this.cubeMapTextures = new GlCubeTexture[3];
			this.cubeMapTextures[0] = new GlCubeTexture(super.toolkit, OpenGLConstants.GL_ALPHA, 64, false, specular96, OpenGLConstants.GL_ALPHA);
			this.cubeMapTextures[1] = new GlCubeTexture(super.toolkit, OpenGLConstants.GL_ALPHA, 64, false, specular36, OpenGLConstants.GL_ALPHA);
			this.cubeMapTextures[2] = new GlCubeTexture(super.toolkit, OpenGLConstants.GL_ALPHA, 64, false, specular12, OpenGLConstants.GL_ALPHA);
			this.buildDisplayList();
		}
	}

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(I)V")
	@Override
	public void disable() {
		if (this.enabled) {
			if (!this.isLimitedTextureUnits) {
				super.toolkit.activateTexture(2);
				super.toolkit.setActiveTexture(null);
			}
			super.toolkit.activateTexture(1);
			super.toolkit.setActiveTexture(null);
			super.toolkit.activateTexture(0);
			this.listUtils.callList('\u0001');
			this.enabled = false;
		} else {
			super.toolkit.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE);
		}
		super.toolkit.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE);
	}

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(Lclient!ud;BI)V")
	@Override
	public void bindTexture(@OriginalArg(0) GlTexture texture, @OriginalArg(2) int combineMode) {
		super.toolkit.setActiveTexture(texture);
		super.toolkit.setColourOp(combineMode);
	}

	@OriginalMember(owner = "client!wk", name = "e", descriptor = "(I)V")
	private void buildDisplayList() {
		this.listUtils = new GlRenderList(super.toolkit, 2);
		this.listUtils.newList(0);
		super.toolkit.activateTexture(1);
		OpenGL.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_NORMAL_MAP);
		OpenGL.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_NORMAL_MAP);
		OpenGL.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_NORMAL_MAP);
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S);
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T);
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R);
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
		OpenGL.glLoadIdentity();
		OpenGL.glRotatef(22.5F, 1.0F, 0.0F, 0.0F);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		if (this.isLimitedTextureUnits) {
			super.toolkit.loadTextureCombineMode(260, 7681);
			super.toolkit.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_ALPHA, 0);
			super.toolkit.setTextureTransparency(0, 34167);
		} else {
			super.toolkit.loadTextureCombineMode(7681, 8448);
			super.toolkit.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_COLOR, 0);
			super.toolkit.activateTexture(2);
			super.toolkit.loadTextureCombineMode(260, 7681);
			super.toolkit.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_COLOR, 0);
			super.toolkit.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_ALPHA, 1);
			super.toolkit.setTextureTransparency(0, 34167);
		}
		super.toolkit.activateTexture(0);
		this.listUtils.endList();
		this.listUtils.newList(1);
		super.toolkit.activateTexture(1);
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S);
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T);
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R);
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
		OpenGL.glLoadIdentity();
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		if (this.isLimitedTextureUnits) {
			super.toolkit.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE);
			super.toolkit.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0);
			super.toolkit.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE);
		} else {
			super.toolkit.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE);
			super.toolkit.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0);
			super.toolkit.activateTexture(2);
			super.toolkit.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE);
			super.toolkit.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0);
			super.toolkit.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_COLOR, 1);
			super.toolkit.setTextureTransparency(0, OpenGLConstants.GL_TEXTURE);
		}
		super.toolkit.activateTexture(0);
		this.listUtils.endList();
	}

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(IIB)V")
	@Override
	public void setEffectParams(@OriginalArg(0) int effectParam1, @OriginalArg(1) int effectParam2) {
		if (this.enabled) {
			super.toolkit.activateTexture(1);
			super.toolkit.setActiveTexture(this.cubeMapTextures[effectParam1 - 1]);
			super.toolkit.activateTexture(0);
		}
	}

	@OriginalMember(owner = "client!wk", name = "c", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return true;
	}

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(ZZ)V")
	@Override
	public void enable(@OriginalArg(0) boolean lit) {
		if (this.listUtils == null || !lit) {
			super.toolkit.setTextureTransparency(0, OpenGLConstants.GL_PREVIOUS);
			return;
		}
		if (!this.isLimitedTextureUnits) {
			super.toolkit.activateTexture(2);
			super.toolkit.setActiveTexture(super.toolkit.texture2d);
			super.toolkit.activateTexture(0);
		}
		this.listUtils.callList('\u0000');
		this.enabled = true;
	}

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
		super.toolkit.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, 7681);
	}
}
