package com.jagex.game.runetek5.toolkit.opengl.effect;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.toolkit.opengl.GlRenderList;
import com.jagex.game.runetek5.graphics.gl.GlTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import com.jagex.game.runetek5.graphics.texture.water.TextureContainerGL;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!gn")
public final class WaterfallEffect extends MaterialEffectGL {

	@OriginalMember(owner = "client!gn", name = "q", descriptor = "Lclient!lc;")
	private final TextureContainerGL textureContainer;

	@OriginalMember(owner = "client!gn", name = "l", descriptor = "Lclient!jv;")
	private final GlRenderList listUtils;

	@OriginalMember(owner = "client!gn", name = "<init>", descriptor = "(Lclient!qo;Lclient!lc;)V")
	public WaterfallEffect(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) TextureContainerGL glWaterNoiseTextureSet) {
		super(glToolkit);
		this.textureContainer = glWaterNoiseTextureSet;
		this.listUtils = new GlRenderList(glToolkit, 2);
		this.listUtils.newList(0);
		super.toolkit.activateTexture(1);
		if (this.textureContainer.texture3dSupported) {
			OpenGL.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_OBJECT_LINEAR);
			OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R);
		}
		OpenGL.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR);
		OpenGL.glTexGeni(OpenGLConstants.GL_T, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR);
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S);
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_T);
		super.toolkit.activateTexture(0);
		this.listUtils.endList();
		this.listUtils.newList(1);
		super.toolkit.activateTexture(1);
		if (this.textureContainer.texture3dSupported) {
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R);
		}
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S);
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_T);
		super.toolkit.activateTexture(0);
		this.listUtils.endList();
	}

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(IIB)V")
	@Override
	public void setEffectParams(@OriginalArg(0) int effectParam1, @OriginalArg(1) int effectParam2) {
		@Pc(14) float flowSpeed = -5.0E-4F * (float) ((effectParam1 & 0x3) + 1);
		@Pc(25) float animationSpeed = (float) ((effectParam1 >> 3 & 0x3) + 1) * 5.0E-4F;
		@Pc(36) float textureScale = (effectParam1 & 0x40) == 0 ? 4.8828125E-4F : 9.765625E-4F;
		@Pc(44) boolean flowAlongX = (effectParam1 & 0x80) != 0;
		super.toolkit.activateTexture(1);
		if (flowAlongX) {
			WaterfallEffect.PLANE_PARAMETERS[1] = 0.0F;
			WaterfallEffect.PLANE_PARAMETERS[0] = textureScale;
			WaterfallEffect.PLANE_PARAMETERS[3] = 0.0F;
			WaterfallEffect.PLANE_PARAMETERS[2] = 0.0F;
		} else {
			WaterfallEffect.PLANE_PARAMETERS[1] = 0.0F;
			WaterfallEffect.PLANE_PARAMETERS[0] = 0.0F;
			WaterfallEffect.PLANE_PARAMETERS[2] = textureScale;
			WaterfallEffect.PLANE_PARAMETERS[3] = 0.0F;
		}
		OpenGL.glTexGenfv(OpenGLConstants.GL_S, OpenGLConstants.GL_EYE_PLANE, WaterfallEffect.PLANE_PARAMETERS, 0);
		WaterfallEffect.PLANE_PARAMETERS[0] = 0.0F;
		WaterfallEffect.PLANE_PARAMETERS[1] = textureScale;
		WaterfallEffect.PLANE_PARAMETERS[3] = flowSpeed * (float) super.toolkit.lastTickMillis % 1.0F;
		WaterfallEffect.PLANE_PARAMETERS[2] = 0.0F;
		OpenGL.glTexGenfv(8193, 9474, WaterfallEffect.PLANE_PARAMETERS, 0);
		if (this.textureContainer.texture3dSupported) {
			WaterfallEffect.PLANE_PARAMETERS[0] = 0.0F;
			WaterfallEffect.PLANE_PARAMETERS[3] = (float) super.toolkit.lastTickMillis * animationSpeed % 1.0F;
			WaterfallEffect.PLANE_PARAMETERS[2] = 0.0F;
			WaterfallEffect.PLANE_PARAMETERS[1] = 0.0F;
			OpenGL.glTexGenfv(OpenGLConstants.GL_R, OpenGLConstants.GL_OBJECT_PLANE, WaterfallEffect.PLANE_PARAMETERS, 0);
		} else {
			@Pc(170) int frame = (int) ((float) super.toolkit.lastTickMillis * 16.0F * animationSpeed);
			super.toolkit.setActiveTexture(this.textureContainer.billowTextures2d[frame % 16]);
		}
		super.toolkit.activateTexture(0);
	}

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(Lclient!ud;BI)V")
	@Override
	public void bindTexture(@OriginalArg(0) GlTexture texture, @OriginalArg(2) int combineMode) {
		super.toolkit.setActiveTexture(texture);
		super.toolkit.setColourOp(combineMode);
	}

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(I)V")
	@Override
	public void disable() {
		this.listUtils.callList('\u0001');
		super.toolkit.activateTexture(1);
		super.toolkit.setActiveTexture(null);
		super.toolkit.activateTexture(0);
	}

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(ZZ)V")
	@Override
	public void enable(@OriginalArg(0) boolean lit) {
		this.listUtils.callList('\u0000');
		if (this.textureContainer.texture3dSupported) {
			super.toolkit.activateTexture(1);
			super.toolkit.setActiveTexture(this.textureContainer.texture3d);
			super.toolkit.activateTexture(0);
		}
	}

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
	}

	@OriginalMember(owner = "client!gn", name = "c", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return true;
	}
	@OriginalMember(owner = "client!mf", name = "v", descriptor = "[F")
	public static final float[] PLANE_PARAMETERS = new float[4];
}
