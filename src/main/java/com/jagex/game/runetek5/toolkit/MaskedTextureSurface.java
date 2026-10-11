package com.jagex.game.runetek5.toolkit;

import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.GpuTexture;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.toolkit.opengl.GLActionFormat;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!lp")
public final class MaskedTextureSurface extends ComponentSurface {

	@OriginalMember(owner = "client!lp", name = "f", descriptor = "Lclient!wfa;")
	public final GpuTexture nativeTextureSampler;

	@OriginalMember(owner = "client!lp", name = "<init>", descriptor = "(Lclient!wga;II[B)V")
	public MaskedTextureSurface(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) byte[] bytes) {
		this.nativeTextureSampler = nativeToolkit.createTextureSampler(bytes, width, GLActionFormat.PIXEL_FORMAT, false, height);
		this.nativeTextureSampler.setWarp(false, false);
	}

	@OriginalMember(owner = "client!lp", name = "<init>", descriptor = "(Lclient!wga;II[I)V")
	public MaskedTextureSurface(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) int[] pixels) {
		this.nativeTextureSampler = nativeToolkit.createTexture(height, width, false, pixels);
		this.nativeTextureSampler.setWarp(false, false);
	}
	@OriginalMember(owner = "client!jda", name = "a", descriptor = "(ILclient!wga;[IB[II)Lclient!lp;")
	public static MaskedTextureSurface create(@OriginalArg(0) int height, @OriginalArg(1) AbstractNativeToolkit nativeToolkit, @OriginalArg(2) int[] rowOffsets, @OriginalArg(4) int[] rowLengths, @OriginalArg(5) int width) {
		@Pc(25) int y;
		@Pc(35) int index;
		@Pc(37) int x;
		if (!nativeToolkit.supportsTextureFormat(GLFormat.RGBA, GLActionFormat.PIXEL_FORMAT)) {
			@Pc(71) int[] pixels = new int[height * width];
			for (y = 0; y < height; y++) {
				index = rowOffsets[y] + y * width;
				for (x = 0; x < rowLengths[y]; x++) {
					pixels[index++] = -16777216;
				}
			}
			return new MaskedTextureSurface(nativeToolkit, width, height, pixels);
		}
		@Pc(23) byte[] alphaMask = new byte[height * width];
		for (y = 0; y < height; y++) {
			index = rowOffsets[y] + width * y;
			for (x = 0; x < rowLengths[y]; x++) {
				alphaMask[index++] = -1;
			}
		}
		return new MaskedTextureSurface(nativeToolkit, width, height, alphaMask);
	}
}
