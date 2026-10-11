package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureTypeList;

@OriginalClass("client!vf")
public final class TextureOpTexture extends TextureOperation {

	@OriginalMember(owner = "client!vf", name = "M", descriptor = "I")
	private int textureWidth;

	@OriginalMember(owner = "client!vf", name = "N", descriptor = "[I")
	private int[] texturePixels;

	@OriginalMember(owner = "client!vf", name = "R", descriptor = "I")
	private int textureHeight;

	@OriginalMember(owner = "client!vf", name = "S", descriptor = "I")
	private int materialId = -1;

	@OriginalMember(owner = "client!vf", name = "<init>", descriptor = "()V")
	public TextureOpTexture() {
		super(0, false);
	}

	@OriginalMember(owner = "client!vf", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int count) {
		@Pc(16) int[][] output = super.colorImageCache.get(count);
		if (super.colorImageCache.dirty) {
			@Pc(40) int pixelOffset = this.textureWidth * (this.textureHeight == TextureOperation.currentHeight ? count : this.textureHeight * count / TextureOperation.currentHeight);
			@Pc(44) int[] outputRed = output[0];
			@Pc(48) int[] outputGreen = output[1];
			@Pc(52) int[] outputBlue = output[2];
			@Pc(62) int x;
			@Pc(71) int local71;
			if (this.textureWidth == EnvironmentLight.textureOpWidth) {
				for (x = 0; x < EnvironmentLight.textureOpWidth; x++) {
					int pixel = this.texturePixels[pixelOffset++];
					outputBlue[x] = (pixel & 0xFF) << 4;
					outputGreen[x] = pixel >> 4 & 0xFF0;
					outputRed[x] = pixel >> 12 & 0xFF0;
				}
			} else {
				for (x = 0; x < EnvironmentLight.textureOpWidth; x++) {
					int sourceX = this.textureWidth * x / EnvironmentLight.textureOpWidth;
					@Pc(78) int pixel = this.texturePixels[sourceX + pixelOffset];
					outputBlue[x] = (pixel & 0xFF) << 4;
					outputGreen[x] = pixel >> 4 & 0xFF0;
					outputRed[x] = pixel >> 12 & 0xFF0;
				}
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!vf", name = "b", descriptor = "(III)V")
	@Override
	public void initCache(@OriginalArg(0) int height, @OriginalArg(1) int width) {
		super.initCache(height, width);
		if (this.materialId >= 0 && TextureTypeList.textureSource != null) {
			@Pc(27) int size = TextureTypeList.textureSource.getMetrics(this.materialId).isSmall ? 64 : 128;
			this.texturePixels = TextureTypeList.textureSource.getPixels(false, this.materialId, 1.0F, size, size);
			this.textureHeight = size;
			this.textureWidth = size;
		}
	}

	@OriginalMember(owner = "client!vf", name = "a", descriptor = "(B)V")
	@Override
	public void cacheReset() {
		super.cacheReset();
		this.texturePixels = null;
	}

	@OriginalMember(owner = "client!vf", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.materialId = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!vf", name = "d", descriptor = "(I)I")
	@Override
	public int getTextureId() {
		return this.materialId;
	}
}
