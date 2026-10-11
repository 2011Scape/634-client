package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import com.jagex.game.runetek5.graphics.GpuTexture;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import jagdx.IDirect3DDevice;
import jagdx.IDirect3DVertexShader;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.toolkit.D3DToolkit;
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode;

@OriginalClass("client!fl")
public final class D3DUnderwaterPass extends RenderPass {

	@OriginalMember(owner = "client!fl", name = "p", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private IDirect3DVertexShader boundShader;

	@OriginalMember(owner = "client!fl", name = "s", descriptor = "Z")
	private boolean lit;

	@OriginalMember(owner = "client!fl", name = "i", descriptor = "Z")
	private boolean whiteTextureBound = false;

	@OriginalMember(owner = "client!fl", name = "k", descriptor = "Lclient!bfa;")
	private final D3DToolkit d3dToolkit;

	@OriginalMember(owner = "client!fl", name = "l", descriptor = "Z")
	private final boolean supported;

	@OriginalMember(owner = "client!fl", name = "o", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private IDirect3DVertexShader modelUnlitShader;

	@OriginalMember(owner = "client!fl", name = "m", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private IDirect3DVertexShader groundLitShader;

	@OriginalMember(owner = "client!fl", name = "q", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private IDirect3DVertexShader unlitModelShader;

	@OriginalMember(owner = "client!fl", name = "r", descriptor = "Lclient!jagdx/IDirect3DVertexShader;")
	private IDirect3DVertexShader modelLitShader;

	@OriginalMember(owner = "client!fl", name = "n", descriptor = "Lclient!wfa;")
	private GpuTexture depthRamp;

	@OriginalMember(owner = "client!fl", name = "<init>", descriptor = "(Lclient!bfa;Lclient!in;)V")
	public D3DUnderwaterPass(@OriginalArg(0) D3DToolkit d3DToolkit, @OriginalArg(1) Js5ResourceProvider resourceProvider) {
		super(d3DToolkit);
		this.d3dToolkit = d3DToolkit;
		if (resourceProvider == null || (this.d3dToolkit.d3DCAPS.VertexShaderVersion & 0xFFFF) < 257) {
			this.supported = false;
		} else {
			this.modelUnlitShader = this.d3dToolkit.iDirect3DDevice.b(resourceProvider.getFile("dx", "uw_ground_unlit"));
			this.groundLitShader = this.d3dToolkit.iDirect3DDevice.b(resourceProvider.getFile("dx", "uw_ground_lit"));
			this.unlitModelShader = this.d3dToolkit.iDirect3DDevice.b(resourceProvider.getFile("dx", "uw_model_unlit"));
			this.modelLitShader = this.d3dToolkit.iDirect3DDevice.b(resourceProvider.getFile("dx", "uw_model_lit"));
			if (this.modelUnlitShader != null & this.groundLitShader != null & this.unlitModelShader != null & this.modelLitShader != null) {
				this.depthRamp = this.nativeToolkit.createTexture(1, 2, false, new int[] { 0, -1 });
				this.depthRamp.setWarp(false, false);
				this.supported = true;
			} else {
				this.supported = false;
			}
		}
	}

	@OriginalMember(owner = "client!fl", name = "c", descriptor = "(I)V")
	@Override
	public void onUnderwaterSettingsChanged() {
		@Pc(9) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
		@Pc(14) int waterHeight = this.nativeToolkit.getWaterHeight();
		@Pc(19) NativeMatrix view = this.nativeToolkit.getFlippedViewMatrix();
		@Pc(33) IDirect3DVertexShader shader;
		if (this.lit) {
			shader = ~waterHeight == Integer.MIN_VALUE ? this.groundLitShader : this.modelLitShader;
		} else {
			shader = ~waterHeight == Integer.MIN_VALUE ? this.modelUnlitShader : this.unlitModelShader;
		}
		if (this.boundShader != shader) {
			this.boundShader = shader;
			this.d3dToolkit.setVertexShader(shader);
			this.uploadLighting();
			this.onProjectionChanged();
			this.onTextureMatrixChanged();
			this.onModelMatrixChanged();
			this.onCameraChanged();
			this.onFogChanged();
		}
		view.transformPlane(0.0F, D3DUnderwaterPassConstants.VECTOR_BUFFER, -1.0F, 0.0F, (float) waterHeight);
		device.a(12, D3DUnderwaterPassConstants.VECTOR_BUFFER);
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(III)V")
	@Override
	public void setEffectParams(@OriginalArg(1) int effectParam1, @OriginalArg(2) int effectParam2) {
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(B)V")
	@Override
	public void disable() {
		this.nativeToolkit.setTextureSlot(1);
		this.nativeToolkit.setTextureSampler(null);
		this.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE);
		this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
		this.nativeToolkit.setTextureRgbStageState(2, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
		this.nativeToolkit.setTextureSlot(0);
		if (this.whiteTextureBound) {
			this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
			this.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
			this.whiteTextureBound = false;
		}
		if (this.boundShader != null) {
			this.d3dToolkit.setVertexShader(null);
			this.boundShader = null;
		}
	}

	@OriginalMember(owner = "client!fl", name = "e", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return this.supported;
	}

	@OriginalMember(owner = "client!fl", name = "b", descriptor = "(B)V")
	@Override
	public void onModelMatrixChanged() {
		if (this.boundShader != null) {
			@Pc(15) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
			@Pc(22) NativeMatrix worldViewProjection = this.d3dToolkit.calibrateViewPosition();
			device.a(0, worldViewProjection.copyRowMajorInto(D3DUnderwaterPassConstants.MATRIX_BUFFER));
		}
	}

	@OriginalMember(owner = "client!fl", name = "b", descriptor = "(I)V")
	@Override
	public void onProjectionChanged() {
		if (this.boundShader != null) {
			@Pc(11) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
			device.a(4, this.nativeToolkit.getTransposedProjectionMatrix(D3DUnderwaterPassConstants.MATRIX_BUFFER));
		}
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(ILclient!du;I)V")
	@Override
	public void setBaseTexture(@OriginalArg(0) int colourType, @OriginalArg(1) TextureSampler baseTexture) {
		if (baseTexture != null) {
			if (this.whiteTextureBound) {
				this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
				this.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
				this.whiteTextureBound = false;
			}
			this.nativeToolkit.setTextureSampler(baseTexture);
			this.nativeToolkit.setColourType(colourType);
		} else if (!this.whiteTextureBound) {
			this.nativeToolkit.setTextureSampler(this.nativeToolkit.defaultTexture);
			this.nativeToolkit.setColourType(1);
			this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS);
			this.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.PREVIOUS);
			this.whiteTextureBound = true;
		}
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(I)V")
	@Override
	public void onFogChanged() {
		if (this.boundShader == null) {
			return;
		}
		@Pc(14) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
		@Pc(18) int far = this.nativeToolkit.XA();
		@Pc(22) int near = this.nativeToolkit.i();
		@Pc(34) float fogFar = -((float) (far - near) * 0.125F) + (float) far;
		@Pc(45) float fogNear = (float) far - (float) (far - near) * 0.25F;
		device.a(10, fogNear, 1.0F / (fogFar - fogNear), fogFar, 1.0F / ((float) far - fogFar));
		device.a(11, 1.0F / (float) this.nativeToolkit.getWaterScale(), (float) this.nativeToolkit.getWaterIntensity() / 255.0F, this.nativeToolkit.fogEnd, 1.0F / (this.nativeToolkit.fogEnd - this.nativeToolkit.fogStart));
		this.nativeToolkit.setColourValue(this.nativeToolkit.getWaterColour());
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(IZ)V")
	@Override
	public void enable(@OriginalArg(1) boolean lit) {
		this.lit = lit;
		this.nativeToolkit.setTextureSlot(1);
		this.nativeToolkit.setTextureSampler(this.depthRamp);
		this.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_5);
		this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setTextureColourSource(true, 2, TextureCombineSource.TEXTURE, false);
		this.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.PREVIOUS);
		this.nativeToolkit.setTextureSlot(0);
		this.onUnderwaterSettingsChanged();
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
	}

	@OriginalMember(owner = "client!fl", name = "g", descriptor = "(I)V")
	private void uploadLighting() {
		if (this.boundShader == null || !this.lit) {
			return;
		}
		@Pc(15) NativeMatrix viewRotation = this.nativeToolkit.getInverseSunMatrix44();
		@Pc(19) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
		device.a(13, this.nativeToolkit.intensity * this.nativeToolkit.baseLightColorRed, this.nativeToolkit.baseLightColorGreen * this.nativeToolkit.intensity, this.nativeToolkit.baseLightColorBlue * this.nativeToolkit.intensity, 1.0F);
		device.a(14, this.nativeToolkit.sunColorIntensity * this.nativeToolkit.baseLightColorRed, this.nativeToolkit.baseLightColorGreen * this.nativeToolkit.sunColorIntensity, this.nativeToolkit.sunColorIntensity * this.nativeToolkit.baseLightColorBlue, 1.0F);
		device.a(16, this.nativeToolkit.baseLightColorRed * this.nativeToolkit.antiSunColorIntensity, this.nativeToolkit.baseLightColorGreen * this.nativeToolkit.antiSunColorIntensity, this.nativeToolkit.baseLightColorBlue * this.nativeToolkit.antiSunColorIntensity, 1.0F);
		viewRotation.transformDirection(this.nativeToolkit.sunPosition[0], this.nativeToolkit.sunPosition[1], this.nativeToolkit.sunPosition[2], D3DUnderwaterPassConstants.VECTOR_BUFFER);
		device.SetVertexShaderConstantF(15, D3DUnderwaterPassConstants.VECTOR_BUFFER, 1);
		viewRotation.transformDirection(this.nativeToolkit.lightPosition[0], this.nativeToolkit.lightPosition[1], this.nativeToolkit.lightPosition[2], D3DUnderwaterPassConstants.VECTOR_BUFFER);
		device.SetVertexShaderConstantF(17, D3DUnderwaterPassConstants.VECTOR_BUFFER, 1);
	}

	@OriginalMember(owner = "client!fl", name = "a", descriptor = "(Z)V")
	@Override
	public void onCameraChanged() {
		if (this.boundShader != null) {
			@Pc(11) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
			@Pc(16) NativeMatrix worldViewProjection = this.d3dToolkit.calibrateViewPosition();
			device.a(0, worldViewProjection.copyRowMajorInto(D3DUnderwaterPassConstants.MATRIX_BUFFER));
		}
	}

	@OriginalMember(owner = "client!fl", name = "f", descriptor = "(I)V")
	@Override
	public void onTextureMatrixChanged() {
		if (this.boundShader != null) {
			@Pc(14) IDirect3DDevice device = this.d3dToolkit.iDirect3DDevice;
			@Pc(19) NativeMatrix textureMatrix = this.nativeToolkit.getTextureMatrix();
			device.SetVertexShaderConstantF(8, textureMatrix.copyXYInto(D3DUnderwaterPassConstants.MATRIX_BUFFER), 2);
		}
	}
}
