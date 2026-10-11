package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.water.NativeWaterTexture;
import com.jagex.game.runetek5.toolkit.GlxToolkit;

@OriginalClass("client!ko")
public final class GlslEnvironmentMappedWaterPass extends RenderPass {

	@OriginalMember(owner = "client!ko", name = "p", descriptor = "Z")
	private boolean active;

	@OriginalMember(owner = "client!ko", name = "r", descriptor = "Lclient!ur;")
	private final NativeWaterTexture water;

	@OriginalMember(owner = "client!ko", name = "l", descriptor = "Lclient!lha;")
	private GlxProgram program;

	@OriginalMember(owner = "client!ko", name = "s", descriptor = "Z")
	private final boolean supported;

	@OriginalMember(owner = "client!ko", name = "<init>", descriptor = "(Lclient!bga;Lclient!in;Lclient!ur;)V")
	public GlslEnvironmentMappedWaterPass(@OriginalArg(0) GlxToolkit glxToolkit, @OriginalArg(1) Js5ResourceProvider resourceProvider, @OriginalArg(2) NativeWaterTexture water) {
		super(glxToolkit);
		this.water = water;
		if (resourceProvider != null && glxToolkit.GLARBVertexShader && glxToolkit.GLARBFragmentShader) {
			@Pc(23) GlxShader vertexShader = GlxShader.compile(glxToolkit, resourceProvider.getFile("gl", "environment_mapped_water_v"), 35633);
			@Pc(33) GlxShader fragmentShader = GlxShader.compile(glxToolkit, resourceProvider.getFile("gl", "environment_mapped_water_f"), 35632);
			this.program = GlxProgram.create(glxToolkit, new GlxShader[] { vertexShader, fragmentShader });
			this.supported = this.program != null && this.water.loadNormalMap();
		} else {
			this.supported = false;
		}
	}

	@OriginalMember(owner = "client!ko", name = "a", descriptor = "(III)V")
	@Override
	public void setEffectParams(@OriginalArg(1) int effectParam1, @OriginalArg(2) int effectParam2) {
		if (!this.active) {
			return;
		}
		@Pc(12) int waveSpeed = 0x1 << (effectParam1 & 0x3);
		@Pc(23) float waveScale = (float) (0x1 << (effectParam1 >> 3 & 0x7)) / 32.0F;
		@Pc(27) int breakDepth = effectParam2 & 0xFFFF;
		@Pc(36) float breakOffset = (float) (effectParam2 >> 16 & 0x3) / 8.0F;
		@Pc(40) long handle = this.program.handle;
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "time"), (float) (super.nativeToolkit.animatingCycle * waveSpeed % 40000) / 40000.0F);
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "scale"), waveScale);
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "breakWaterDepth"), (float) breakDepth);
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "breakWaterOffset"), breakOffset);
	}

	@OriginalMember(owner = "client!ko", name = "a", descriptor = "(B)V")
	@Override
	public void disable() {
		if (!this.active) {
			return;
		}
		super.nativeToolkit.setTextureSlot(1);
		super.nativeToolkit.setTextureSampler(null);
		super.nativeToolkit.setTextureSlot(0);
		super.nativeToolkit.setTextureSampler(null);
		OpenGL.glUseProgramObjectARB(0L);
		this.active = false;
	}

	@OriginalMember(owner = "client!ko", name = "e", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return this.supported;
	}

	@OriginalMember(owner = "client!ko", name = "a", descriptor = "(ILclient!du;I)V")
	@Override
	public void setBaseTexture(@OriginalArg(0) int colourType, @OriginalArg(1) TextureSampler baseTexture) {
		if (!this.active) {
			super.nativeToolkit.setTextureSampler(baseTexture);
			super.nativeToolkit.setColourType(colourType);
		}
	}

	@OriginalMember(owner = "client!ko", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
	}

	@OriginalMember(owner = "client!ko", name = "a", descriptor = "(IZ)V")
	@Override
	public void enable(@OriginalArg(1) boolean lit) {
		@Pc(17) CubeTextureSampler envMap = super.nativeToolkit.getEnvironmentMapSampler();
		if (!this.supported || envMap == null) {
			return;
		}
		super.nativeToolkit.setTextureSlot(1);
		super.nativeToolkit.setTextureSampler(envMap);
		super.nativeToolkit.setTextureSlot(0);
		super.nativeToolkit.setTextureSampler(this.water.normalSampler);
		@Pc(50) long handle = this.program.handle;
		OpenGL.glUseProgramObjectARB(handle);
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(handle, "normalSampler"), 0);
		OpenGL.glUniform1iARB(OpenGL.glGetUniformLocationARB(handle, "envMapSampler"), 1);
		OpenGL.glUniform3fARB(OpenGL.glGetUniformLocationARB(handle, "sunDir"), -super.nativeToolkit.sunPosition[0], -super.nativeToolkit.sunPosition[1], -super.nativeToolkit.sunPosition[2]);
		OpenGL.glUniform4fARB(OpenGL.glGetUniformLocationARB(handle, "sunColour"), super.nativeToolkit.baseLightColorRed, super.nativeToolkit.baseLightColorGreen, super.nativeToolkit.baseLightColorBlue, 1.0F);
		OpenGL.glUniform1fARB(OpenGL.glGetUniformLocationARB(handle, "sunExponent"), Math.abs(super.nativeToolkit.sunPosition[1]) * 928.0F + 96.0F);
		this.active = true;
	}
}
