package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.graphics.SpriteDataProvider;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.config.material.texture.TextureTypeList;
import com.jagex.game.runetek5.config.material.texture.TextureType;

@OriginalClass("client!pj")
public class TextureOpSprite extends TextureOperation {

	@OriginalMember(owner = "client!pj", name = "I", descriptor = "[I")
	public int[] spritePixels;

	@OriginalMember(owner = "client!pj", name = "O", descriptor = "I")
	public int spriteWidth;

	@OriginalMember(owner = "client!pj", name = "S", descriptor = "I")
	public int spriteHeight;

	@OriginalMember(owner = "client!pj", name = "U", descriptor = "I")
	private int spriteId = -1;

	@OriginalMember(owner = "client!pj", name = "<init>", descriptor = "()V")
	public TextureOpSprite() {
		super(0, false);
	}

	@OriginalMember(owner = "client!pj", name = "b", descriptor = "(Z)Z")
	public final boolean loadSprite() {
		if (this.spritePixels != null) {
			return true;
		} else if (this.spriteId >= 0) {
			@Pc(35) SpriteType image = TextureType.SPRITE_GROUP_ID >= 0 ? SpriteDataProvider.listSingle(TextureTypeList.textureSpriteResource, TextureType.SPRITE_GROUP_ID, this.spriteId) : SpriteDataProvider.listSingle(TextureTypeList.textureSpriteResource, this.spriteId);
			image.expandToFullSize();
			this.spritePixels = image.getPixels();
			this.spriteWidth = image.width;
			this.spriteHeight = image.height;
			return true;
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!pj", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public final void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.spriteId = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!pj", name = "a", descriptor = "(I)I")
	@Override
	public final int getSpriteId() {
		return this.spriteId;
	}

	@OriginalMember(owner = "client!pj", name = "a", descriptor = "(B)V")
	@Override
	public final void cacheReset() {
		super.cacheReset();
		this.spritePixels = null;
	}

	@OriginalMember(owner = "client!pj", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int count) {
		@Pc(19) int[][] output = super.colorImageCache.get(count);
		if (super.colorImageCache.dirty && this.loadSprite()) {
			@Pc(31) int[] outputRed = output[0];
			@Pc(35) int[] outputGreen = output[1];
			@Pc(39) int[] outputBlue = output[2];
			@Pc(59) int pixelOffset = this.spriteWidth * (TextureOperation.currentHeight == this.spriteHeight ? count : this.spriteHeight * count / TextureOperation.currentHeight);
			@Pc(65) int x;
			@Pc(73) int local73;
			if (EnvironmentLight.textureOpWidth == this.spriteWidth) {
				for (x = 0; x < EnvironmentLight.textureOpWidth; x++) {
					int pixel = this.spritePixels[pixelOffset++];
					outputBlue[x] = (pixel & 0xFF) << 4;
					outputGreen[x] = pixel >> 4 & 0xFF0;
					outputRed[x] = pixel >> 12 & 0xFF0;
				}
			} else {
				for (x = 0; x < EnvironmentLight.textureOpWidth; x++) {
					int sourceX = this.spriteWidth * x / EnvironmentLight.textureOpWidth;
					@Pc(125) int pixel = this.spritePixels[sourceX + pixelOffset];
					outputBlue[x] = (pixel & 0xFF) << 4;
					outputGreen[x] = pixel >> 4 & 0xFF0;
					outputRed[x] = pixel >> 12 & 0xFF0;
				}
			}
		}
		return output;
	}
}
