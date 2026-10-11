package com.jagex.game.runetek5;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.water.NativeWaterTexture;
import com.jagex.game.runetek5.toolkit.GlxToolkit;

@OriginalClass("client!ov")
public final class GlTransparentWaterPass extends RenderPass {

	@OriginalMember(owner = "client!ov", name = "s", descriptor = "Lclient!bga;")
	private final GlxToolkit glxToolkit;

	@OriginalMember(owner = "client!ov", name = "q", descriptor = "Lclient!ur;")
	private final NativeWaterTexture water;

	@OriginalMember(owner = "client!ov", name = "p", descriptor = "Lclient!qr;")
	private final GlxArbProgram vertexProgram;

	@OriginalMember(owner = "client!ov", name = "<init>", descriptor = "(Lclient!bga;Lclient!in;Lclient!ur;)V")
	public GlTransparentWaterPass(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) Js5ResourceProvider resourceProvider, @OriginalArg(2) NativeWaterTexture water) {
		super(glxToolkit);
		this.glxToolkit = glxToolkit;
		this.water = water;
		if (resourceProvider != null && this.water.readyToSample() && this.glxToolkit.GLARBVertexProgram) {
			this.vertexProgram = GlxArbProgram.createVertexProgram(resourceProvider.getFile("gl", "transparent_water"), this.glxToolkit);
		} else {
			this.vertexProgram = null;
		}
	}

	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(ILclient!du;I)V")
	@Override
	public void setBaseTexture(@OriginalArg(0) int combineMode, @OriginalArg(1) TextureSampler baseTexture) {
	}

	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(IZ)V")
	@Override
	public void enable(@OriginalArg(1) boolean lit) {
		OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, this.vertexProgram.programId);
		OpenGL.glEnable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB);
		super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS);
	}

	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
		super.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_2, TextureCombineFunction.aClass315_4);
	}

	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(B)V")
	@Override
	public void disable() {
		super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
		OpenGL.glBindProgramARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0);
		OpenGL.glDisable(OpenGLConstants.GL_FRAGMENT_PROGRAM_ARB);
		OpenGL.glDisable(OpenGLConstants.GL_VERTEX_PROGRAM_ARB);
	}

	@OriginalMember(owner = "client!ov", name = "a", descriptor = "(III)V")
	@Override
	public void setEffectParams(@OriginalArg(1) int effectParam1, @OriginalArg(2) int effectParam2) {
		if (this.water.hasDepth) {
			@Pc(47) float phase = (float) (super.nativeToolkit.animatingCycle % 4000) / 4000.0F;
			super.nativeToolkit.setTextureSampler(this.water.waterSampler3d);
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0, phase, 0.0F, 0.0F, 1.0F);
		} else {
			@Pc(21) int frame = super.nativeToolkit.animatingCycle % 4000 * 16 / 4000;
			super.nativeToolkit.setTextureSampler(this.water.waterTextureSamplers[frame]);
			OpenGL.glProgramLocalParameter4fARB(OpenGLConstants.GL_VERTEX_PROGRAM_ARB, 0, 0.0F, 0.0F, 0.0F, 1.0F);
		}
	}

	@OriginalMember(owner = "client!ov", name = "e", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return this.vertexProgram != null;
	}
}
