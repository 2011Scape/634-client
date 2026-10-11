package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.texture.sampler.TextureSampler;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode;

@OriginalClass("client!bk")
public final class FixedFunctionSpecularPass extends RenderPass {

	@OriginalMember(owner = "client!bk", name = "r", descriptor = "Z")
	private boolean active = false;

	@OriginalMember(owner = "client!bk", name = "l", descriptor = "Z")
	private boolean singleStage;

	@OriginalMember(owner = "client!bk", name = "p", descriptor = "[Lclient!nj;")
	private CubeTextureSampler[] specularMaps;

	@OriginalMember(owner = "client!bk", name = "<init>", descriptor = "(Lclient!wga;)V")
	public FixedFunctionSpecularPass(@OriginalArg(0) AbstractNativeToolkit nativeToolkit) {
		super(nativeToolkit);
		if (nativeToolkit.cubeMapSupported) {
			this.singleStage = nativeToolkit.maxConcurrentTextures < 3;
			@Pc(28) int maxIntensity = this.singleStage ? 48 : 127;
			@Pc(32) int[][] broadSpecularFaces = new int[6][4096];
			@Pc(36) int[][] sharpSpecularFaces = new int[6][4096];
			@Pc(40) int[][] mediumSpecularFaces = new int[6][4096];
			@Pc(42) int texelIndex = 0;
			for (@Pc(44) int i = 0; i < 64; i++) {
				for (@Pc(48) int j = 0; j < 64; j++) {
					@Pc(59) float u = (float) i * 2.0F / 64.0F - 1.0F;
					@Pc(68) float v = (float) j * 2.0F / 64.0F - 1.0F;
					@Pc(83) float normalZ = (float) (1.0D / Math.sqrt((double) (u * u + v * v + 1.0F)));
					@Pc(87) float normalX = u * normalZ;
					@Pc(91) float normalY = v * normalZ;
					for (@Pc(93) int k = 0; k < 6; k++) {
						@Pc(109) float facing;
						if (k == 0) {
							facing = -normalY;
						} else if (k == 1) {
							facing = normalY;
						} else if (k == 2) {
							facing = normalX;
						} else if (k == 3) {
							facing = -normalX;
						} else if (k == 4) {
							facing = normalZ;
						} else {
							facing = -normalZ;
						}
						@Pc(150) int sharpIntensity;
						@Pc(149) int mediumIntensity;
						@Pc(147) int broadIntensity;
						if (facing > 0.0F) {
							sharpIntensity = (int) ((double) maxIntensity * Math.pow((double) facing, 96.0D));
							mediumIntensity = (int) ((double) maxIntensity * Math.pow((double) facing, 36.0D));
							broadIntensity = (int) (Math.pow((double) facing, 12.0D) * (double) maxIntensity);
						} else {
							broadIntensity = 0;
							mediumIntensity = 0;
							sharpIntensity = 0;
						}
						sharpSpecularFaces[k][texelIndex] = sharpIntensity << 24;
						mediumSpecularFaces[k][texelIndex] = mediumIntensity << 24;
						broadSpecularFaces[k][texelIndex] = broadIntensity << 24;
					}
					texelIndex++;
				}
			}
			this.specularMaps = new CubeTextureSampler[3];
			this.specularMaps[0] = super.nativeToolkit.generateNativeCubeTexture(sharpSpecularFaces, false, 64);
			this.specularMaps[1] = super.nativeToolkit.generateNativeCubeTexture(mediumSpecularFaces, false, 64);
			this.specularMaps[2] = super.nativeToolkit.generateNativeCubeTexture(broadSpecularFaces, false, 64);
		}
	}

	@OriginalMember(owner = "client!bk", name = "e", descriptor = "(I)Z")
	@Override
	public boolean isSupported() {
		return true;
	}

	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(IZ)V")
	@Override
	public void enable(@OriginalArg(1) boolean lit) {
		if (this.specularMaps == null || !lit) {
			super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.PRIMARY_COLOR);
			return;
		}
		super.nativeToolkit.setTextureSlot(1);
		super.nativeToolkit.setTexCoordGeneration(TexCoordGenMode.aClass31_2);
		@Pc(29) NativeMatrix textureMatrix = super.nativeToolkit.getCurrentTextureMatrix();
		textureMatrix.makeRotationX(1024);
		super.nativeToolkit.setTextureState(TextureTransformMode.aClass332_2);
		if (this.singleStage) {
			super.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_2);
			super.nativeToolkit.setTextureColourSource(true, 0, TextureCombineSource.TEXTURE, false);
			super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.PRIMARY_COLOR);
		} else {
			super.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineFunction.aClass315_4);
			super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS);
			super.nativeToolkit.setTextureSlot(2);
			super.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineFunction.aClass315_2);
			super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.PREVIOUS);
			super.nativeToolkit.setTextureColourSource(true, 1, TextureCombineSource.PREVIOUS, false);
			super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.PRIMARY_COLOR);
			super.nativeToolkit.setTextureSampler(super.nativeToolkit.defaultTexture);
		}
		super.nativeToolkit.setTextureSlot(0);
		this.active = true;
	}

	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(ILclient!du;I)V")
	@Override
	public void setBaseTexture(@OriginalArg(0) int colourType, @OriginalArg(1) TextureSampler baseTexture) {
		super.nativeToolkit.setTextureSampler(baseTexture);
		super.nativeToolkit.setColourType(colourType);
	}

	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(B)V")
	@Override
	public void disable() {
		if (this.active) {
			super.nativeToolkit.setTextureSlot(1);
			super.nativeToolkit.setTextureSampler(null);
			super.nativeToolkit.setTexCoordGeneration(TexCoordGenMode.aClass31_3);
			super.nativeToolkit.resetCurrentTextureMatrix();
			if (this.singleStage) {
				super.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE);
				super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
				super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
			} else {
				super.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE);
				super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
				super.nativeToolkit.setTextureSlot(2);
				super.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE);
				super.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
				super.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.PREVIOUS);
				super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
				super.nativeToolkit.setTextureSampler(null);
			}
			super.nativeToolkit.setTextureSlot(0);
			this.active = false;
		} else {
			super.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
		}
		super.nativeToolkit.setTextureCombineModes(TextureCombineMode.MODULATE, TextureCombineMode.MODULATE);
	}

	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(ZB)V")
	@Override
	public void applyTextureCombine(@OriginalArg(0) boolean lit) {
		super.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineMode.MODULATE);
	}

	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(III)V")
	@Override
	public void setEffectParams(@OriginalArg(1) int effectParam1, @OriginalArg(2) int effectParam2) {
		if (this.active) {
			super.nativeToolkit.setTextureSlot(1);
			super.nativeToolkit.setTextureSampler(this.specularMaps[effectParam1 - 1]);
			super.nativeToolkit.setTextureSlot(0);
		}
	}
}
