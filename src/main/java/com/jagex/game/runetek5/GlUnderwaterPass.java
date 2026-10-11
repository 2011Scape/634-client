package com.jagex.game.runetek5;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import com.jagex.game.runetek5.graphics.GpuTexture;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.toolkit.GlxToolkit;
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode;

@OriginalClass("client!or")
public final class GlUnderwaterPass extends RenderPass {

	@OriginalMember(owner = "client!or", name = "y", descriptor = "Z")
	private boolean programBound;

	@OriginalMember(owner = "client!or", name = "E", descriptor = "Z")
	private boolean lit;

	@OriginalMember(owner = "client!or", name = "r", descriptor = "Z")
	private boolean whiteTextureBound = false;

	@OriginalMember(owner = "client!or", name = "G", descriptor = "[F")
	private final float[] waterPlane = new float[4];

	@OriginalMember(owner = "client!or", name = "n", descriptor = "Lclient!qr;")
	private GlxArbProgram groundUnlitProgram;

	@OriginalMember(owner = "client!or", name = "k", descriptor = "Lclient!qr;")
	private GlxArbProgram groundLitProgram;

	@OriginalMember(owner = "client!or", name = "w", descriptor = "Lclient!qr;")
	private GlxArbProgram modelUnlitProgram;

	@OriginalMember(owner = "client!or", name = "z", descriptor = "Lclient!qr;")
	private GlxArbProgram modelLitProgram;

	@OriginalMember(owner = "client!or", name = "j", descriptor = "Z")
	private final boolean supported;

	@OriginalMember(owner = "client!or", name = "i", descriptor = "Lclient!wfa;")
	private GpuTexture depthRamp;

	@OriginalMember(owner = "client!or", name = "<init>", descriptor = "(Lclient!bga;Lclient!in;)V")
	public GlUnderwaterPass(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) Js5ResourceProvider resourceProvider) {
		super(glxToolkit);
		if (resourceProvider != null && glxToolkit.GLARBVertexProgram) {
			this.groundUnlitProgram = GlxArbProgram.createVertexProgram(resourceProvider.getFile("gl", "uw_ground_unlit"), glxToolkit);
			this.groundLitProgram = GlxArbProgram.createVertexProgram(resourceProvider.getFile("gl", "uw_ground_lit"), glxToolkit);
			this.modelUnlitProgram = GlxArbProgram.createVertexProgram(resourceProvider.getFile("gl", "uw_model_unlit"), glxToolkit);
			this.modelLitProgram = GlxArbProgram.createVertexProgram(resourceProvider.getFile("gl", "uw_model_lit"), glxToolkit);
			if (this.groundUnlitProgram != null & this.groundLitProgram != null & this.modelUnlitProgram != null & this.modelLitProgram != null) {
				this.depthRamp = super.nativeToolkit.createTexture(1, 2, false, new int[] { 0, -1 });
				this.depthRamp.setWarp(false, false);
				this.supported = true;
			} else {
				this.supported = false;
			}
		} else {
			this.supported = false;
		}
	}

	@OriginalMember(owner = "client!or", name = "c", descriptor = "(I)V")
	@Override
	public void onUnderwaterSettingsChanged() {
		@Pc(13) int waterHeight = super.nativeToolkit.getWaterHeight();
		@Pc(18) NativeMatrix view = super.nativeToolkit.getFlippedViewMatrix();
		if (this.lit) {
			OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, waterHeight == Integer.MAX_VALUE ? this.groundLitProgram.programId : this.modelLitProgram.programId);
		} else {
			OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, ~waterHeight == Integer.MIN_VALUE ? this.groundUnlitProgram.programId : this.modelUnlitProgram.programId);
		}
		OpenGL.glEnable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB);
		this.programBound = true;
		view.transformPlane(0.0F, this.waterPlane, -1.0F, 0.0F, (float) waterHeight);
		OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 1, this.waterPlane[0], this.waterPlane[1], this.waterPlane[2], this.waterPlane[3]);
		this.onFogChanged();
	}

	@OriginalMember(owner = "client!or", name = "e", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return this.supported;
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(B)V")
	@Override
	public void disable() {
		super.nativeToolkit.setTextureSlot(1);
		super.nativeToolkit.setTextureSampler(null);
		super.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE);
		super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
		super.nativeToolkit.setTextureRgbStageState(2, TextureCombineSource.CONSTANT_COLOR);
		super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
		super.nativeToolkit.setTextureSlot(0);
		if (this.whiteTextureBound) {
			super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
			super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
			this.whiteTextureBound = false;
		}
		if (this.programBound) {
			OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0);
			OpenGL.glDisable(OpenGLConstants.GL_FRAGMENT_PROGRAM_ARB);
			OpenGL.glDisable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB);
			this.programBound = false;
		}
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(III)V")
	@Override
	public void setEffectParams(@OriginalArg(1) int effectParam1, @OriginalArg(2) int effectParam2) {
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(ILclient!du;I)V")
	@Override
	public void setBaseTexture(@OriginalArg(0) int colourType, @OriginalArg(1) TextureSampler baseTexture) {
		if (baseTexture != null) {
			if (this.whiteTextureBound) {
				super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
				super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
				this.whiteTextureBound = false;
			}
			super.nativeToolkit.setTextureSampler(baseTexture);
			super.nativeToolkit.setColourType(colourType);
		} else if (!this.whiteTextureBound) {
			super.nativeToolkit.setTextureSampler(super.nativeToolkit.defaultTexture);
			super.nativeToolkit.setColourType(1);
			super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS);
			super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.PREVIOUS);
			this.whiteTextureBound = true;
		}
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(I)V")
	@Override
	public void onFogChanged() {
		if (!this.programBound) {
			return;
		}
		@Pc(12) int far = super.nativeToolkit.XA();
		@Pc(16) int near = super.nativeToolkit.i();
		@Pc(27) float fogFar = (float) far - (float) (far - near) * 0.125F;
		@Pc(39) float fogNear = -((float) (far - near) * 0.25F) + (float) far;
		OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0, fogNear, fogFar, 1.0F / (float) super.nativeToolkit.getWaterScale(), (float) super.nativeToolkit.getWaterIntensity() / 255.0F);
		super.nativeToolkit.setTextureSlot(1);
		super.nativeToolkit.setColourValue(super.nativeToolkit.getWaterColour());
		super.nativeToolkit.setTextureSlot(0);
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(IZ)V")
	@Override
	public void enable(@OriginalArg(1) boolean lit) {
		this.lit = lit;
		super.nativeToolkit.setTextureSlot(1);
		super.nativeToolkit.setTextureSampler(this.depthRamp);
		super.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_5);
		super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR);
		super.nativeToolkit.setTextureColourSource(true, 2, TextureCombineSource.TEXTURE, false);
		super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.PREVIOUS);
		super.nativeToolkit.setTextureSlot(0);
		this.onUnderwaterSettingsChanged();
	}

	@OriginalMember(owner = "client!or", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
	}
}
