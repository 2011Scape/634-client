package com.jagex.game.runetek5.config.cursor;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.cursor.CursorTypeList;
import com.jagex.game.runetek5.graphics.SpriteDataProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;

@OriginalClass("client!sda")
public final class CursorType {

	@OriginalMember(owner = "client!sda", name = "a", descriptor = "I")
	public int hotspotPointX;

	@OriginalMember(owner = "client!sda", name = "d", descriptor = "Lclient!pg;")
	public CursorTypeList cursorTypeList;

	@OriginalMember(owner = "client!sda", name = "h", descriptor = "I")
	public int hotspotPointY;

	@OriginalMember(owner = "client!sda", name = "i", descriptor = "I")
	private int spriteId;

	@OriginalMember(owner = "client!sda", name = "a", descriptor = "(ILclient!cea;B)V")
	private void decode(@OriginalArg(0) int opcode, @OriginalArg(1) Packet packet) {
		if (opcode == 1) {
			this.spriteId = packet.readUShort();
		} else if (opcode == 2) {
			this.hotspotPointX = packet.readUByte();
			this.hotspotPointY = packet.readUByte();
		}
	}

	@OriginalMember(owner = "client!sda", name = "b", descriptor = "(B)Lclient!rh;")
	public synchronized SpriteType getSprite() {
		@Pc(13) SpriteType indexedImage = (SpriteType) this.cursorTypeList.spriteCache.get((long) this.spriteId);
		if (indexedImage != null) {
			return indexedImage;
		}
		indexedImage = SpriteDataProvider.listSingle(this.cursorTypeList.spriteResource, this.spriteId, 0);
		if (indexedImage != null) {
			this.cursorTypeList.spriteCache.put(indexedImage, (long) this.spriteId);
		}
		return indexedImage;
	}

	@OriginalMember(owner = "client!sda", name = "a", descriptor = "(Lclient!cea;Z)V")
	public void decode(@OriginalArg(0) Packet packet) {
		while (true) {
			@Pc(5) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, packet);
		}
	}
}
