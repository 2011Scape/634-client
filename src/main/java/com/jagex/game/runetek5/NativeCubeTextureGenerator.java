package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.GpuEnvironmentSampler;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!kv")
public final class NativeCubeTextureGenerator extends GpuEnvironmentSampler {

	@OriginalMember(owner = "client!kv", name = "t", descriptor = "Lclient!nj;")
	private CubeTextureSampler nativeCubeTexture;

	@OriginalMember(owner = "client!kv", name = "p", descriptor = "I")
	private final int material3;

	@OriginalMember(owner = "client!kv", name = "i", descriptor = "I")
	private final int material5;

	@OriginalMember(owner = "client!kv", name = "q", descriptor = "I")
	private final int material1;

	@OriginalMember(owner = "client!kv", name = "l", descriptor = "I")
	private final int material4;

	@OriginalMember(owner = "client!kv", name = "r", descriptor = "I")
	private final int material2;

	@OriginalMember(owner = "client!kv", name = "o", descriptor = "I")
	private final int material6;

	@OriginalMember(owner = "client!kv", name = "s", descriptor = "Lclient!wga;")
	private final AbstractNativeToolkit nativeToolkit;

	@OriginalMember(owner = "client!kv", name = "<init>", descriptor = "(Lclient!wga;IIIIII)V")
	public NativeCubeTextureGenerator(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) int material1, @OriginalArg(2) int material2, @OriginalArg(3) int material3, @OriginalArg(4) int material4, @OriginalArg(5) int material5, @OriginalArg(6) int material6) {
		this.material3 = material3;
		this.material5 = material5;
		this.material1 = material1;
		this.material4 = material4;
		this.material2 = material2;
		this.material6 = material6;
		this.nativeToolkit = nativeToolkit;
	}

	@OriginalMember(owner = "client!kv", name = "a", descriptor = "(Z)Lclient!nj;")
	@Override
	public CubeTextureSampler getNativeCubeTexture() {
		if (this.nativeCubeTexture == null) {
			NativeCubeTextureGenerator.faceMaterialIds[3] = this.material4;
			NativeCubeTextureGenerator.faceMaterialIds[1] = this.material2;
			NativeCubeTextureGenerator.faceMaterialIds[4] = this.material5;
			@Pc(32) MaterialProvider textureSource = this.nativeToolkit.materialProvider;
			NativeCubeTextureGenerator.faceMaterialIds[5] = this.material6;
			NativeCubeTextureGenerator.faceMaterialIds[2] = this.material3;
			NativeCubeTextureGenerator.faceMaterialIds[0] = this.material1;
			@Pc(49) boolean mipmapping = false;
			@Pc(51) int largestSize = 0;
			for (@Pc(53) int i = 0; i < 6; i++) {
				if (!textureSource.textureAvailable(NativeCubeTextureGenerator.faceMaterialIds[i])) {
					return null;
				}
				@Pc(71) MaterialType textureMetrics = textureSource.getMetrics(NativeCubeTextureGenerator.faceMaterialIds[i]);
				@Pc(78) int size = textureMetrics.isSmall ? 64 : 128;
				if (size > largestSize) {
					largestSize = size;
				}
				if (textureMetrics.mipmapping > 0) {
					mipmapping = true;
				}
			}
			for (@Pc(103) int j = 0; j < 6; j++) {
				NativeCubeTextureGenerator.facePixels[j] = textureSource.getPixels(false, NativeCubeTextureGenerator.faceMaterialIds[j], 1.0F, largestSize, largestSize);
			}
			this.nativeCubeTexture = this.nativeToolkit.generateNativeCubeTexture(NativeCubeTextureGenerator.facePixels, mipmapping, largestSize);
		}
		return this.nativeCubeTexture;
	}
	@OriginalMember(owner = "client!us", name = "b", descriptor = "[I")
	public static final int[] faceMaterialIds = new int[6];
	@OriginalMember(owner = "client!pp", name = "x", descriptor = "[[I")
	public static final int[][] facePixels = new int[6][];
}
