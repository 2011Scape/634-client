package com.jagex.game.runetek5.toolkit.opengl.effect;

import com.jagex.game.runetek5.graphics.texture.OpenGl1DTexture;
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

@OriginalClass("client!tn")
public final class RipplingWaterEffect extends MaterialEffectGL {

	@OriginalMember(owner = "client!tn", name = "l", descriptor = "Lclient!jv;")
	private GlRenderList glLists;

	@OriginalMember(owner = "client!tn", name = "f", descriptor = "Lclient!lc;")
	private final TextureContainerGL textureContainer;

	@OriginalMember(owner = "client!tn", name = "m", descriptor = "Lclient!wha;")
	private final OpenGl1DTexture texture1d;

	@OriginalMember(owner = "client!tn", name = "<init>", descriptor = "(Lclient!qo;Lclient!lc;)V")
	public RipplingWaterEffect(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) TextureContainerGL glWaterNoiseTextureSet) {
		super(glToolkit);
		this.textureContainer = glWaterNoiseTextureSet;
		this.buildDisplayList();
		this.texture1d = new OpenGl1DTexture(super.toolkit, OpenGLConstants.GL_ALPHA, 2, new byte[] { 0, -1 }, OpenGLConstants.GL_ALPHA);
		this.texture1d.setClampToEdge();
	}

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(IIB)V")
	@Override
	public void setEffectParams(@OriginalArg(0) int effectParam1, @OriginalArg(1) int effectParam2) {
		if ((effectParam1 & 0x1) == 1) {
			if (this.textureContainer.texture3dSupported) {
				super.toolkit.setActiveTexture(this.textureContainer.waveTexture3d);
				RipplingWaterEffect.texGenPlane[2] = 0.0F;
				RipplingWaterEffect.texGenPlane[3] = (float) (super.toolkit.lastTickMillis % 4000) / 4000.0F;
				RipplingWaterEffect.texGenPlane[0] = 0.0F;
				RipplingWaterEffect.texGenPlane[1] = 0.0F;
				OpenGL.glTexGenfv(OpenGLConstants.GL_R, OpenGLConstants.GL_OBJECT_PLANE, RipplingWaterEffect.texGenPlane, 0);
			} else {
				@Pc(26) int frame = super.toolkit.lastTickMillis % 4000 * 16 / 4000;
				super.toolkit.setActiveTexture(this.textureContainer.waveTextures2d[frame]);
			}
		} else if (this.textureContainer.texture3dSupported) {
			super.toolkit.setActiveTexture(this.textureContainer.waveTexture3d);
			RipplingWaterEffect.texGenPlane[0] = 0.0F;
			RipplingWaterEffect.texGenPlane[2] = 0.0F;
			RipplingWaterEffect.texGenPlane[1] = 0.0F;
			RipplingWaterEffect.texGenPlane[3] = 0.0F;
			OpenGL.glTexGenfv(OpenGLConstants.GL_R, OpenGLConstants.GL_OBJECT_PLANE, RipplingWaterEffect.texGenPlane, 0);
		} else {
			super.toolkit.setActiveTexture(this.textureContainer.waveTextures2d[0]);
		}
	}

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(ZZ)V")
	@Override
	public void enable(@OriginalArg(0) boolean lit) {
		if (super.toolkit.fogDensity > 0) {
			@Pc(10) float depthScale = -0.5F / (float) super.toolkit.fogDensity;
			super.toolkit.activateTexture(1);
			RipplingWaterEffect.texGenPlane[3] = super.toolkit.fogStartPosition * depthScale + 0.25F;
			RipplingWaterEffect.texGenPlane[0] = 0.0F;
			RipplingWaterEffect.texGenPlane[2] = depthScale;
			RipplingWaterEffect.texGenPlane[1] = 0.0F;
			OpenGL.glPushMatrix();
			OpenGL.glLoadIdentity();
			OpenGL.glTexGenfv(OpenGLConstants.GL_S, OpenGLConstants.GL_EYE_PLANE, RipplingWaterEffect.texGenPlane, 0);
			OpenGL.glPopMatrix();
			super.toolkit.adjustFog((float) super.toolkit.fogDensity, 0.5F);
			super.toolkit.setActiveTexture(this.texture1d);
			super.toolkit.activateTexture(0);
		}
		this.glLists.callList('\u0000');
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
		OpenGL.glPushMatrix();
		OpenGL.glScalef(0.25F, 0.25F, 1.0F);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
	}

	@OriginalMember(owner = "client!tn", name = "f", descriptor = "(I)V")
	private void buildDisplayList() {
		this.glLists = new GlRenderList(super.toolkit, 2);
		this.glLists.newList(0);
		super.toolkit.activateTexture(1);
		super.toolkit.loadTextureCombineMode(7681, 260);
		super.toolkit.setTexture(OpenGLConstants.GL_PREVIOUS, OpenGLConstants.GL_SRC_COLOR, 0);
		OpenGL.glTexGeni(OpenGLConstants.GL_S, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_EYE_LINEAR);
		OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_S);
		super.toolkit.activateTexture(0);
		OpenGL.glTexEnvf(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_RGB_SCALE, 2.0F);
		if (this.textureContainer.texture3dSupported) {
			OpenGL.glTexGeni(OpenGLConstants.GL_R, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_OBJECT_LINEAR);
			OpenGL.glTexGeni(OpenGLConstants.GL_Q, OpenGLConstants.GL_TEXTURE_GEN_MODE, OpenGLConstants.GL_OBJECT_LINEAR);
			OpenGL.glTexGenfv(OpenGLConstants.GL_Q, OpenGLConstants.GL_OBJECT_PLANE, new float[] { 0.0F, 0.0F, 0.0F, 1.0F }, 0);
			OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_R);
			OpenGL.glEnable(OpenGLConstants.GL_TEXTURE_GEN_Q);
		}
		this.glLists.endList();
		this.glLists.newList(1);
		super.toolkit.activateTexture(1);
		super.toolkit.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE);
		super.toolkit.setTexture(OpenGLConstants.GL_TEXTURE, OpenGLConstants.GL_SRC_COLOR, 0);
		OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_S);
		super.toolkit.activateTexture(0);
		OpenGL.glTexEnvf(OpenGLConstants.GL_TEXTURE_ENV, OpenGLConstants.GL_RGB_SCALE, 1.0F);
		if (this.textureContainer.texture3dSupported) {
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_R);
			OpenGL.glDisable(OpenGLConstants.GL_TEXTURE_GEN_Q);
		}
		this.glLists.endList();
	}

	@OriginalMember(owner = "client!tn", name = "c", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return true;
	}

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(I)V")
	@Override
	public void disable() {
		this.glLists.callList('\u0001');
		if (super.toolkit.fogDensity > 0) {
			super.toolkit.activateTexture(1);
			super.toolkit.setActiveTexture(null);
			super.toolkit.adjustFog(0.0F, 1.0F);
			super.toolkit.activateTexture(0);
		}
		super.toolkit.loadTextureCombineMode(OpenGLConstants.GL_MODULATE, OpenGLConstants.GL_MODULATE);
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
		OpenGL.glPopMatrix();
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
	}

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(Lclient!ud;BI)V")
	@Override
	public void bindTexture(@OriginalArg(0) GlTexture texture, @OriginalArg(2) int combineMode) {
	}

	@OriginalMember(owner = "client!tn", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
		super.toolkit.loadTextureCombineMode(260, 8448);
	}
	@OriginalMember(owner = "client!pa", name = "c", descriptor = "[F")
	public static final float[] texGenPlane = new float[4];
}
