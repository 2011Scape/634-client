package com.jagex.game.runetek5.config.material.texture;

import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import org.openrs2.deob.annotation.OriginalArg;

public final class TextureRaster {
	@OriginalMember(owner = "client!tl", name = "u", descriptor = "[I")
	public static int[] verticalTable;

	private TextureRaster() {
		/* empty */
	}
	@OriginalMember(owner = "client!tha", name = "q", descriptor = "I")
	public static int scaledWidth;
	@OriginalMember(owner = "client!bv", name = "s", descriptor = "[I")
	public static int[] PALETTE_SIN;
	@OriginalMember(owner = "client!nl", name = "c", descriptor = "[I")
	public static int[] PALETTE_COS;

	@OriginalMember(owner = "client!sca", name = "a", descriptor = "(I)V")
	public static void calculateTrigTables() {
		if (TextureRaster.PALETTE_SIN != null && TextureRaster.PALETTE_COS != null) {
			return;
		}
		TextureRaster.PALETTE_COS = new int[256];
		TextureRaster.PALETTE_SIN = new int[256];
		for (@Pc(13) int i = 0; i < 256; i++) {
			@Pc(22) double angle = (double) i / 255.0D * 6.283185307179586D;
			TextureRaster.PALETTE_SIN[i] = (int) (Math.sin(angle) * 4096.0D);
			TextureRaster.PALETTE_COS[i] = (int) (Math.cos(angle) * 4096.0D);
		}
	}
	@OriginalMember(owner = "client!wca", name = "u", descriptor = "I")
	public static int widthMask;

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(IIB)V")
	public static void setDimensions(@OriginalArg(0) int textureOpHeight, @OriginalArg(1) int textureOpWidth) {
		@Pc(17) int idx;
		if (textureOpWidth != EnvironmentLight.textureOpWidth) {
			EnvironmentLight.horizontalTable = new int[textureOpWidth];
			for (idx = 0; idx < textureOpWidth; idx++) {
				EnvironmentLight.horizontalTable[idx] = (idx << 12) / textureOpWidth;
			}
			TextureRaster.widthMask = textureOpWidth - 1;
			EnvironmentLight.textureOpWidth = textureOpWidth;
			TextureRaster.scaledWidth = textureOpWidth * 32;
		}
		if (textureOpHeight == TextureOperation.currentHeight) {
			return;
		}
		if (EnvironmentLight.textureOpWidth == textureOpHeight) {
			TextureRaster.verticalTable = EnvironmentLight.horizontalTable;
		} else {
			TextureRaster.verticalTable = new int[textureOpHeight];
			for (idx = 0; idx < textureOpHeight; idx++) {
				TextureRaster.verticalTable[idx] = (idx << 12) / textureOpHeight;
			}
		}
		TextureOperation.currentHeight = textureOpHeight;
		TextureOperation.columnEnd = textureOpHeight - 1;
	}
}
