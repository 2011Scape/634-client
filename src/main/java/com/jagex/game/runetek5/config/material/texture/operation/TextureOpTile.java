package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!vn")
public final class TextureOpTile extends TextureOperation {

	@OriginalMember(owner = "client!vn", name = "J", descriptor = "I")
	private int tileCountX = 4;

	@OriginalMember(owner = "client!vn", name = "O", descriptor = "I")
	private int tileCountY = 4;

	@OriginalMember(owner = "client!vn", name = "<init>", descriptor = "()V")
	public TextureOpTile() {
		super(1, false);
	}

	@OriginalMember(owner = "client!vn", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int count) {
		@Pc(16) int[][] output = super.colorImageCache.get(count);
		if (super.colorImageCache.dirty) {
			@Pc(25) int tileWidth = EnvironmentLight.textureOpWidth / this.tileCountX;
			@Pc(30) int tileHeight = TextureOperation.currentHeight / this.tileCountY;
			@Pc(46) int[][] input;
			if (tileHeight > 0) {
				@Pc(36) int tileY = count % tileHeight;
				input = this.getChildColorOutput(TextureOperation.currentHeight * tileY / tileHeight, 0);
			} else {
				input = this.getChildColorOutput(0, 0);
			}
			@Pc(58) int[] inputRed = input[0];
			@Pc(62) int[] inputGreen = input[1];
			@Pc(66) int[] inputBlue = input[2];
			@Pc(70) int[] outputRed = output[0];
			@Pc(74) int[] outputGreen = output[1];
			@Pc(78) int[] outputBlue = output[2];
			for (@Pc(80) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				@Pc(89) int sourceX;
				if (tileWidth <= 0) {
					sourceX = 0;
				} else {
					@Pc(95) int tileX = i % tileWidth;
					sourceX = EnvironmentLight.textureOpWidth * tileX / tileWidth;
				}
				outputRed[i] = inputRed[sourceX];
				outputGreen[i] = inputGreen[sourceX];
				outputBlue[i] = inputBlue[sourceX];
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!vn", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.tileCountX = packet.readUByte();
		} else if (opcode == 1) {
			this.tileCountY = packet.readUByte();
		}
	}

	@OriginalMember(owner = "client!vn", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(16) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(25) int tileWidth = EnvironmentLight.textureOpWidth / this.tileCountX;
			@Pc(30) int tileHeight = TextureOperation.currentHeight / this.tileCountY;
			@Pc(49) int[] input;
			@Pc(39) int local39;
			if (tileHeight > 0) {
				int tileY = row % tileHeight;
				input = this.getChildMonochromeOutput(tileY * TextureOperation.currentHeight / tileHeight, 0);
			} else {
				input = this.getChildMonochromeOutput(0, 0);
			}
			for (int column = 0; column < EnvironmentLight.textureOpWidth; column++) {
				if (tileWidth <= 0) {
					output[column] = input[0];
				} else {
					@Pc(75) int tileX = column % tileWidth;
					output[column] = input[EnvironmentLight.textureOpWidth * tileX / tileWidth];
				}
			}
		}
		return output;
	}
}
