package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import jagdx.IDirect3DDevice;
import jagdx.IDirect3DVertexShader;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.water.NativeWaterTexture;
import com.jagex.game.runetek5.toolkit.D3DToolkit;

@OriginalClass("client!gaa")
public final class D3DTransparentWaterPass extends RenderPass {

	@OriginalMember(owner = "client!gaa", name = "j", descriptor = "Lclient!ur;")
	private final NativeWaterTexture water;

	@OriginalMember(owner = "client!gaa", name = "k", descriptor = "Lclient!bfa;")
	private final D3DToolkit d3DToolkit;

	@OriginalMember(owner = "client!gaa", name = "l", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private final IDirect3DVertexShader vertexShader;

	@OriginalMember(owner = "client!gaa", name = "<init>", descriptor = "(Lclient!bfa;Lclient!in;Lclient!ur;)V")
	public D3DTransparentWaterPass(@OriginalArg(0) D3DToolkit d3DToolkit, @OriginalArg(1) Js5ResourceProvider resourceProvider, @OriginalArg(2) NativeWaterTexture water) {
		super(d3DToolkit);
		this.water = water;
		this.d3DToolkit = d3DToolkit;
		if (resourceProvider != null && this.water.readyToSample() && (this.d3DToolkit.d3DCAPS.VertexShaderVersion & 0xFFFF) >= 257) {
			this.vertexShader = this.d3DToolkit.iDirect3DDevice.b(resourceProvider.getFile("dx", "transparent_water"));
		} else {
			this.vertexShader = null;
		}
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(ILclient!du;I)V")
	@Override
	public void setBaseTexture(@OriginalArg(0) int combineMode, @OriginalArg(1) TextureSampler baseTexture) {
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
		this.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_2, TextureCombineFunction.aClass315_5);
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(III)V")
	@Override
	public void setEffectParams(@OriginalArg(1) int effectParam1, @OriginalArg(2) int effectParam2) {
		@Pc(3) IDirect3DDevice device = this.d3DToolkit.iDirect3DDevice;
		if (this.water.hasDepth) {
			@Pc(16) float phase = (float) (this.nativeToolkit.animatingCycle % 4000) / 4000.0F;
			this.nativeToolkit.setTextureSampler(this.water.waterSampler3d);
			device.a(11, phase, 0.0F, 0.0F, 0.0F);
		} else {
			@Pc(42) int frame = this.nativeToolkit.animatingCycle % 4000 * 16 / 4000;
			this.nativeToolkit.setTextureSampler(this.water.waterTextureSamplers[frame]);
			device.a(11, 0.0F, 0.0F, 0.0F, 0.0F);
		}
	}

	@OriginalMember(owner = "client!gaa", name = "b", descriptor = "(B)V")
	@Override
	public void onModelMatrixChanged() {
		if (this.vertexShader != null) {
			@Pc(6) IDirect3DDevice device = this.d3DToolkit.iDirect3DDevice;
			@Pc(11) NativeMatrix worldViewProjection = this.d3DToolkit.calibrateViewPosition();
			device.a(0, worldViewProjection.copyRowMajorInto(D3DTransparentWaterMatrix.MATRIX_BUFFER));
		}
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(B)V")
	@Override
	public void disable() {
		this.d3DToolkit.setVertexShader(null);
		this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.PRIMARY_COLOR);
		this.nativeToolkit.setTextureRgbStageState(2, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setFogSupported(true);
	}

	@OriginalMember(owner = "client!gaa", name = "b", descriptor = "(I)V")
	@Override
	public void onProjectionChanged() {
		if (this.vertexShader != null) {
			@Pc(6) IDirect3DDevice device = this.d3DToolkit.iDirect3DDevice;
			device.a(4, this.nativeToolkit.getTransposedProjectionMatrix(D3DTransparentWaterMatrix.MATRIX_BUFFER));
		}
	}

	@OriginalMember(owner = "client!gaa", name = "f", descriptor = "(I)V")
	@Override
	public void onTextureMatrixChanged() {
		if (this.vertexShader == null) {
			return;
		}
		@Pc(7) IDirect3DDevice device = this.d3DToolkit.iDirect3DDevice;
		@Pc(12) NativeMatrix textureMatrix = this.nativeToolkit.getTextureMatrix();
		textureMatrix.copyXYInto(D3DTransparentWaterMatrix.MATRIX_BUFFER);
		D3DTransparentWaterMatrix.MATRIX_BUFFER[0] *= 0.25F;
		D3DTransparentWaterMatrix.MATRIX_BUFFER[2] *= 0.25F;
		D3DTransparentWaterMatrix.MATRIX_BUFFER[3] *= 0.25F;
		D3DTransparentWaterMatrix.MATRIX_BUFFER[6] *= 0.25F;
		D3DTransparentWaterMatrix.MATRIX_BUFFER[5] *= 0.25F;
		D3DTransparentWaterMatrix.MATRIX_BUFFER[1] *= 0.25F;
		D3DTransparentWaterMatrix.MATRIX_BUFFER[7] *= 0.25F;
		D3DTransparentWaterMatrix.MATRIX_BUFFER[4] *= 0.25F;
		device.SetVertexShaderConstantF(8, D3DTransparentWaterMatrix.MATRIX_BUFFER, 2);
	}

	@OriginalMember(owner = "client!gaa", name = "e", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return this.vertexShader != null;
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(Z)V")
	@Override
	public void onCameraChanged() {
		if (this.vertexShader != null) {
			@Pc(7) IDirect3DDevice device = this.d3DToolkit.iDirect3DDevice;
			@Pc(12) NativeMatrix worldViewProjection = this.d3DToolkit.calibrateViewPosition();
			device.a(0, worldViewProjection.copyRowMajorInto(D3DTransparentWaterMatrix.MATRIX_BUFFER));
		}
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(I)V")
	@Override
	public void onFogChanged() {
		if (this.vertexShader == null) {
			return;
		}
		@Pc(13) IDirect3DDevice device = this.d3DToolkit.iDirect3DDevice;
		if (this.nativeToolkit.fogDistance <= 0) {
			device.a(10, 0.0F, 0.0F, 0.0F, 0.0F);
		} else {
			@Pc(30) float fogEnd = this.nativeToolkit.fogEnd;
			@Pc(34) float fogStart = this.nativeToolkit.fogStart;
			@Pc(38) float underwaterStart = fogStart - 512.0F;
			device.a(10, underwaterStart, 1.0F / (fogStart - underwaterStart), fogStart, 1.0F / (fogEnd - fogStart));
		}
		this.nativeToolkit.setColourValue(this.nativeToolkit.fogColorRgb);
	}

	@OriginalMember(owner = "client!gaa", name = "a", descriptor = "(IZ)V")
	@Override
	public void enable(@OriginalArg(1) boolean lit) {
		this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setTextureColourSource(false, 2, TextureCombineSource.PRIMARY_COLOR, true);
		this.nativeToolkit.setFogSupported(false);
		this.d3DToolkit.setVertexShader(this.vertexShader);
		this.onModelMatrixChanged();
		this.onTextureMatrixChanged();
		this.onProjectionChanged();
		this.onFogChanged();
	}
}
