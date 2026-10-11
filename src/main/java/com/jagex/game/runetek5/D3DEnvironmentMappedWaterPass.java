package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import jagdx.IDirect3DDevice;
import jagdx.IDirect3DPixelShader;
import jagdx.IDirect3DVertexShader;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.water.NativeWaterTexture;
import com.jagex.game.runetek5.toolkit.D3DToolkit;

@OriginalClass("client!gg")
public final class D3DEnvironmentMappedWaterPass extends RenderPass {

	@OriginalMember(owner = "client!gg", name = "k", descriptor = "Z")
	private boolean active;

	@OriginalMember(owner = "client!gg", name = "o", descriptor = "Lclient!ur;")
	private final NativeWaterTexture water;

	@OriginalMember(owner = "client!gg", name = "m", descriptor = "Lclient!bfa;")
	private final D3DToolkit d3dToolkit;

	@OriginalMember(owner = "client!gg", name = "l", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private final IDirect3DVertexShader vertexShader;

	@OriginalMember(owner = "client!gg", name = "n", descriptor = "Z")
	private final boolean supported;

	@OriginalMember(owner = "client!gg", name = "j", descriptor = "Lclient!jagdx/IDirect3DPixelShader;")
	private final IDirect3DPixelShader pixelShader;

	@OriginalMember(owner = "client!gg", name = "<init>", descriptor = "(Lclient!bfa;Lclient!in;Lclient!ur;)V")
	public D3DEnvironmentMappedWaterPass(@OriginalArg(0) D3DToolkit d3DToolkit, @OriginalArg(1) Js5ResourceProvider resourceProvider, @OriginalArg(2) NativeWaterTexture water) {
		super(d3DToolkit);
		this.water = water;
		this.d3dToolkit = d3DToolkit;
		if (resourceProvider != null && this.nativeToolkit.cubeMapSupported && this.nativeToolkit.SUPPORTS_TEXTURE3D && (this.d3dToolkit.d3DCAPS.VertexShaderVersion & 0xFFFF) >= 257) {
			this.vertexShader = this.d3dToolkit.iDirect3DDevice.b(resourceProvider.getFile("dx", "environment_mapped_water_v"));
			this.pixelShader = this.d3dToolkit.iDirect3DDevice.a(resourceProvider.getFile("dx", "environment_mapped_water_f"));
			this.supported = this.vertexShader != null && this.pixelShader != null && this.water.loadNormalMap();
		} else {
			this.vertexShader = null;
			this.supported = false;
			this.pixelShader = null;
		}
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(III)V")
	@Override
	public void setEffectParams(@OriginalArg(1) int effectParam1, @OriginalArg(2) int effectParam2) {
		if (!this.active) {
			return;
		}
		@Pc(15) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
		@Pc(21) int waveSpeed = 0x1 << (effectParam1 & 0x3);
		@Pc(32) float waveScale = (float) (0x1 << (effectParam1 >> 3 & 0x7)) / 32.0F;
		@Pc(36) int breakDepth = effectParam2 & 0xFFFF;
		@Pc(45) float breakOffset = (float) (effectParam2 >> 16 & 0x3) / 8.0F;
		device.a(14, (float) (this.nativeToolkit.animatingCycle * waveSpeed % 40000) / 40000.0F, 0.0F, 0.0F, 0.0F);
		device.a(15, waveScale, 0.0F, 0.0F, 0.0F);
		device.b(4, (float) breakDepth, 0.0F, 0.0F, 0.0F);
		device.b(5, breakOffset, 0.0F, 0.0F, 0.0F);
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
	}

	@OriginalMember(owner = "client!gg", name = "f", descriptor = "(I)V")
	@Override
	public void onTextureMatrixChanged() {
		if (this.active) {
			@Pc(6) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
			@Pc(13) NativeMatrix textureMatrix = this.nativeToolkit.getTextureMatrix();
			device.SetVertexShaderConstantF(12, textureMatrix.copyXYInto(D3DEnvironmentMappedWaterMatrix.MATRIX_BUFFER), 2);
		}
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(ILclient!du;I)V")
	@Override
	public void setBaseTexture(@OriginalArg(0) int combineMode, @OriginalArg(1) TextureSampler baseTexture) {
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(I)V")
	@Override
	public void onFogChanged() {
		if (!this.active) {
			return;
		}
		@Pc(6) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
		if (this.nativeToolkit.fogDistance <= 0) {
			device.a(16, 0.0F, 0.0F, 0.0F, 0.0F);
		} else {
			@Pc(23) float fogEnd = this.nativeToolkit.fogEnd;
			@Pc(27) float fogStart = this.nativeToolkit.fogStart;
			device.a(16, fogEnd, 1.0F / (fogEnd - fogStart), 0.0F, 0.0F);
		}
		device.b(0, (float) (this.nativeToolkit.fogColorRgb >> 16 & 0xFF) / 255.0F, (float) (this.nativeToolkit.fogColorRgb >> 8 & 0xFF) / 255.0F, (float) (this.nativeToolkit.fogColorRgb & 0xFF) / 255.0F, 0.0F);
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(IZ)V")
	@Override
	public void enable(@OriginalArg(1) boolean lit) {
		@Pc(8) CubeTextureSampler envMap = this.nativeToolkit.getEnvironmentMapSampler();
		if (!this.supported || envMap == null) {
			return;
		}
		@Pc(18) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
		this.d3dToolkit.setVertexShader(this.vertexShader);
		this.d3dToolkit.setPixelShader(this.pixelShader);
		this.nativeToolkit.setTextureSlot(1);
		this.nativeToolkit.setTextureSampler(envMap);
		this.nativeToolkit.setTextureSlot(0);
		this.nativeToolkit.setTextureSampler(this.water.normalSampler);
		this.active = true;
		this.onCameraChanged();
		this.onTextureMatrixChanged();
		this.onProjectionChanged();
		this.onFogChanged();
		device.b(1, -this.nativeToolkit.sunPosition[0], -this.nativeToolkit.sunPosition[1], -this.nativeToolkit.sunPosition[2], 0.0F);
		device.b(2, this.nativeToolkit.baseLightColorRed, this.nativeToolkit.baseLightColorGreen, this.nativeToolkit.baseLightColorBlue, 1.0F);
		device.b(3, Math.abs(this.nativeToolkit.sunPosition[1]) * 928.0F + 96.0F, 0.0F, 0.0F, 0.0F);
	}

	@OriginalMember(owner = "client!gg", name = "e", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return this.supported;
	}

	@OriginalMember(owner = "client!gg", name = "b", descriptor = "(B)V")
	@Override
	public void onModelMatrixChanged() {
		if (this.active) {
			@Pc(7) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
			@Pc(12) NativeMatrix worldViewProjection = this.d3dToolkit.calibrateViewPosition();
			device.a(0, worldViewProjection.copyRowMajorInto(D3DEnvironmentMappedWaterMatrix.MATRIX_BUFFER));
		}
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(B)V")
	@Override
	public void disable() {
		if (!this.active) {
			return;
		}
		this.d3dToolkit.setVertexShader(null);
		this.d3dToolkit.setPixelShader(null);
		this.nativeToolkit.setTextureSlot(1);
		this.nativeToolkit.setTextureSampler(null);
		this.nativeToolkit.setTextureSlot(0);
		this.nativeToolkit.setTextureSampler(null);
		this.active = false;
	}

	@OriginalMember(owner = "client!gg", name = "b", descriptor = "(I)V")
	@Override
	public void onProjectionChanged() {
		if (this.active) {
			@Pc(10) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
			device.a(8, this.nativeToolkit.getTransposedProjectionMatrix(D3DEnvironmentMappedWaterMatrix.MATRIX_BUFFER));
		}
	}

	@OriginalMember(owner = "client!gg", name = "a", descriptor = "(Z)V")
	@Override
	public void onCameraChanged() {
		if (!this.active) {
			return;
		}
		@Pc(10) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
		@Pc(15) NativeMatrix view = this.d3dToolkit.getFlippedViewMatrix();
		@Pc(20) NativeMatrix worldViewProjection = this.d3dToolkit.calibrateViewPosition();
		device.a(0, worldViewProjection.copyRowMajorInto(D3DEnvironmentMappedWaterMatrix.MATRIX_BUFFER));
		device.a(4, view.copyRotationRowMajorInto(D3DEnvironmentMappedWaterMatrix.MATRIX_BUFFER));
	}
}
