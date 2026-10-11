package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.graphics.EnvironmentLight;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!cl")
public final class TextureOpTiledSprite extends TextureOpSprite {

	@OriginalMember(owner = "client!cl", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int count) {
		@Pc(16) int[][] output = super.colorImageCache.get(count);
		if (super.colorImageCache.dirty && this.loadSprite()) {
			@Pc(28) int[] outputRed = output[0];
			@Pc(32) int[] outputGreen = output[1];
			@Pc(36) int[] outputBlue = output[2];
			@Pc(44) int rowOffset = super.spriteHeight * (count % super.spriteHeight);
			for (@Pc(46) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(58) int pixel = super.spritePixels[i % super.spriteWidth + rowOffset];
				outputBlue[i] = (pixel & 0xFF) << 4;
				outputGreen[i] = pixel >> 4 & 0xFF0;
				outputRed[i] = pixel >> 12 & 0xFF0;
			}
		}
		return output;
	}
}
