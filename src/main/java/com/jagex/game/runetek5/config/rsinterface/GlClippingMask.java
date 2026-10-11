package com.jagex.game.runetek5.config.rsinterface;

import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.texture.OpenGl2DImageTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ag")
public final class GlClippingMask extends ComponentSurface {

	@OriginalMember(owner = "client!ag", name = "f", descriptor = "Lclient!eba;")
	public final OpenGl2DImageTexture surface;

	@OriginalMember(owner = "client!ag", name = "<init>", descriptor = "(Lclient!qo;II[B)V")
	public GlClippingMask(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) byte[] maskData) {
		this.surface = OpenGl2DImageTexture.createSurface(maskData, height, glToolkit, width);
		this.surface.setRepeat(false, false);
	}
	@OriginalMember(owner = "client!kg", name = "a", descriptor = "(I[I[IILclient!qo;I)Lclient!ag;")
	public static GlClippingMask create(@OriginalArg(0) int width, @OriginalArg(1) int[] rowOffsets, @OriginalArg(2) int[] rowWidths, @OriginalArg(4) GlToolkit glToolkit, @OriginalArg(5) int height) {
		@Pc(10) byte[] maskData = new byte[width * height];
		for (@Pc(12) int i = 0; i < height; i++) {
			@Pc(23) int offset = width * i + rowOffsets[i];
			for (@Pc(25) int j = 0; j < rowWidths[i]; j++) {
				maskData[offset++] = -1;
			}
		}
		return new GlClippingMask(glToolkit, width, height, maskData);
	}
}
