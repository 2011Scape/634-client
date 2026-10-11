package com.jagex.game.runetek5.config.mapsprite;

import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.SpriteDataProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!rw")
public final class MapSpriteType {

	@OriginalMember(owner = "client!rw", name = "c", descriptor = "Lclient!oda;")
	public MapSpriteTypeList mapspriteTypeList;

	@OriginalMember(owner = "client!rw", name = "e", descriptor = "I")
	public int fileId;

	@OriginalMember(owner = "client!rw", name = "h", descriptor = "I")
	public int backgroundColor;

	@OriginalMember(owner = "client!rw", name = "f", descriptor = "Z")
	public boolean requiresUpscaling = false;

	@OriginalMember(owner = "client!rw", name = "a", descriptor = "(I)Z")
	public boolean ready() {
		return this.mapspriteTypeList.mapspriteResource.isFileReady(this.fileId);
	}

	@OriginalMember(owner = "client!rw", name = "a", descriptor = "(BILclient!cea;)V")
	private void decode(@OriginalArg(1) int opcode, @OriginalArg(2) Packet packet) {
		if (opcode == 1) {
			this.fileId = packet.readUShort();
		} else if (opcode == 2) {
			this.backgroundColor = packet.readUMedium();
		} else if (opcode == 3) {
			this.requiresUpscaling = true;
			return;
		} else if (opcode == 4) {
			this.fileId = -1;
			return;
		}
	}

	@OriginalMember(owner = "client!rw", name = "a", descriptor = "(IZILclient!ha;)Lclient!mi;")
	public NativeSprite createRotatedNativeSprite(@OriginalArg(0) int rotation, @OriginalArg(1) boolean flipVertical, @OriginalArg(3) Toolkit toolkit) {
		@Pc(29) long cacheKey = (long) (toolkit.index << 19 | this.fileId | rotation << 16 | (flipVertical ? 262144 : 0));
		@Pc(37) NativeSprite sprite = (NativeSprite) this.mapspriteTypeList.mapspriteCache.get(cacheKey);
		if (sprite != null) {
			return sprite;
		} else if (this.mapspriteTypeList.mapspriteResource.isFileReady(this.fileId)) {
			@Pc(59) SpriteType indexedImage = SpriteDataProvider.listSingle(this.mapspriteTypeList.mapspriteResource, this.fileId, 0);
			if (indexedImage != null) {
				indexedImage.borderY = indexedImage.minX = indexedImage.borderX = indexedImage.minY = 0;
				if (flipVertical) {
					indexedImage.flipVertical();
				}
				for (@Pc(79) int i = 0; i < rotation; i++) {
					indexedImage.rotate90DegLeft();
				}
			}
			sprite = toolkit.generateSprite(indexedImage, true);
			if (sprite != null) {
				this.mapspriteTypeList.mapspriteCache.put(sprite, cacheKey);
			}
			return sprite;
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!rw", name = "a", descriptor = "(BLclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(9) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, packet);
		}
	}
}
