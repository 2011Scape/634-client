package com.jagex.game.runetek5;

import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.gl.GlCubeTexture;
import com.jagex.game.runetek5.graphics.gl.GlEnvironmentSampler;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!dq")
public final class OpenGLArbTextureCubeMapGenerator extends GlEnvironmentSampler {

	@OriginalMember(owner = "client!dq", name = "j", descriptor = "Lclient!q;")
	private GlCubeTexture cubeTexture;

	@OriginalMember(owner = "client!dq", name = "m", descriptor = "I")
	private final int material3;

	@OriginalMember(owner = "client!dq", name = "l", descriptor = "I")
	private final int material4;

	@OriginalMember(owner = "client!dq", name = "s", descriptor = "I")
	private final int material6;

	@OriginalMember(owner = "client!dq", name = "k", descriptor = "I")
	private final int material1;

	@OriginalMember(owner = "client!dq", name = "q", descriptor = "I")
	private final int material2;

	@OriginalMember(owner = "client!dq", name = "h", descriptor = "I")
	private final int material5;

	@OriginalMember(owner = "client!dq", name = "o", descriptor = "Lclient!qo;")
	private final GlToolkit glToolkit;

	@OriginalMember(owner = "client!dq", name = "<init>", descriptor = "(Lclient!qo;IIIIII)V")
	public OpenGLArbTextureCubeMapGenerator(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int material1, @OriginalArg(2) int material2, @OriginalArg(3) int material3, @OriginalArg(4) int material4, @OriginalArg(5) int material5, @OriginalArg(6) int material6) {
		this.material3 = material3;
		this.material4 = material4;
		this.material6 = material6;
		this.material1 = material1;
		this.material2 = material2;
		this.material5 = material5;
		this.glToolkit = glToolkit;
	}

	@OriginalMember(owner = "client!dq", name = "a", descriptor = "(B)Lclient!q;")
	@Override
	public GlCubeTexture getCubeTexture() {
		if (this.cubeTexture == null) {
			@Pc(19) MaterialProvider textureSource = this.glToolkit.materialProvider;
			OpenGLArbTextureCubeMapGenerator.faceMaterialIds[3] = this.material4;
			OpenGLArbTextureCubeMapGenerator.faceMaterialIds[4] = this.material5;
			OpenGLArbTextureCubeMapGenerator.faceMaterialIds[2] = this.material3;
			OpenGLArbTextureCubeMapGenerator.faceMaterialIds[5] = this.material6;
			OpenGLArbTextureCubeMapGenerator.faceMaterialIds[1] = this.material2;
			OpenGLArbTextureCubeMapGenerator.faceMaterialIds[0] = this.material1;
			@Pc(51) boolean mipmapping = false;
			@Pc(53) int largestSize = 0;
			for (@Pc(55) int i = 0; i < 6; i++) {
				if (!textureSource.textureAvailable(OpenGLArbTextureCubeMapGenerator.faceMaterialIds[i])) {
					return null;
				}
				@Pc(75) MaterialType textureMetrics = textureSource.getMetrics(OpenGLArbTextureCubeMapGenerator.faceMaterialIds[i]);
				@Pc(82) int size = textureMetrics.isSmall ? 64 : 128;
				if (largestSize < size) {
					largestSize = size;
				}
				if (textureMetrics.mipmapping > 0) {
					mipmapping = true;
				}
			}
			for (@Pc(101) int j = 0; j < 6; j++) {
				OpenGLArbTextureCubeMapGenerator.faceTexturePixels[j] = textureSource.getPixels(false, OpenGLArbTextureCubeMapGenerator.faceMaterialIds[j], 1.0F, largestSize, largestSize);
			}
			this.cubeTexture = new GlCubeTexture(this.glToolkit, 6407, largestSize, mipmapping, OpenGLArbTextureCubeMapGenerator.faceTexturePixels);
		}
		return this.cubeTexture;
	}
	@OriginalMember(owner = "client!ec", name = "h", descriptor = "[[I")
	public static final int[][] faceTexturePixels = new int[6][];
	@OriginalMember(owner = "client!wu", name = "e", descriptor = "[I")
	public static final int[] faceMaterialIds = new int[6];
}
