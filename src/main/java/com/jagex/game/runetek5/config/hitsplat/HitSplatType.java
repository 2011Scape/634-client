package com.jagex.game.runetek5.config.hitsplat;

import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.SpriteDataProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!ia")
public final class HitSplatType {

	@OriginalMember(owner = "client!ia", name = "s", descriptor = "Lclient!sb;")
	public HitSplatTypeList typelist;

	@OriginalMember(owner = "client!ia", name = "g", descriptor = "I")
	public int fadeStartCycle = -1;

	@OriginalMember(owner = "client!ia", name = "j", descriptor = "I")
	private int spriteB = -1;

	@OriginalMember(owner = "client!ia", name = "d", descriptor = "Ljava/lang/String;")
	private String placementExampleString = "";

	@OriginalMember(owner = "client!ia", name = "e", descriptor = "I")
	public int scrollOffsetX = 0;

	@OriginalMember(owner = "client!ia", name = "n", descriptor = "I")
	private int spriteC = -1;

	@OriginalMember(owner = "client!ia", name = "c", descriptor = "I")
	public int scrollOffsetY = 0;

	@OriginalMember(owner = "client!ia", name = "t", descriptor = "I")
	private int spriteD = -1;

	@OriginalMember(owner = "client!ia", name = "l", descriptor = "I")
	public int displayType = -1;

	@OriginalMember(owner = "client!ia", name = "o", descriptor = "I")
	public int cyclesVisible = 70;

	@OriginalMember(owner = "client!ia", name = "p", descriptor = "I")
	public int fontId = -1;

	@OriginalMember(owner = "client!ia", name = "b", descriptor = "I")
	private int spriteA = -1;

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "I")
	public int textOffsetY = 0;

	@OriginalMember(owner = "client!ia", name = "y", descriptor = "I")
	public int color = 16777215;

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(Lclient!ha;Z)Lclient!mi;")
	public NativeSprite getInnerLeftSprite(@OriginalArg(0) Toolkit toolkit) {
		if (this.spriteB < 0) {
			return null;
		}
		@Pc(23) NativeSprite sprite = (NativeSprite) this.typelist.spriteCache.get((long) this.spriteB);
		if (sprite == null) {
			this.generateSprites(toolkit);
			sprite = (NativeSprite) this.typelist.spriteCache.get((long) this.spriteB);
		}
		return sprite;
	}

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(Lclient!ha;I)Lclient!mi;")
	public NativeSprite getRightCapSprite(@OriginalArg(0) Toolkit toolkit) {
		if (this.spriteD < 0) {
			return null;
		}
		@Pc(18) NativeSprite sprite = (NativeSprite) this.typelist.spriteCache.get((long) this.spriteD);
		if (sprite == null) {
			this.generateSprites(toolkit);
			sprite = (NativeSprite) this.typelist.spriteCache.get((long) this.spriteD);
		}
		return sprite;
	}

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(Lclient!ha;B)Lclient!mi;")
	public NativeSprite getLeftCapSprite(@OriginalArg(0) Toolkit toolkit) {
		if (this.spriteA < 0) {
			return null;
		}
		@Pc(18) NativeSprite sprite = (NativeSprite) this.typelist.spriteCache.get((long) this.spriteA);
		if (sprite == null) {
			this.generateSprites(toolkit);
			sprite = (NativeSprite) this.typelist.spriteCache.get((long) this.spriteA);
		}
		return sprite;
	}

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(Lclient!cea;II)V")
	private void decode(@OriginalArg(0) Packet packet, @OriginalArg(2) int opcode) {
		if (opcode == 1) {
			this.fontId = packet.readUShort();
		} else if (opcode == 2) {
			this.color = packet.readUMedium();
		} else if (opcode == 3) {
			this.spriteA = packet.readUShort();
		} else if (opcode == 4) {
			this.spriteB = packet.readUShort();
		} else if (opcode == 5) {
			this.spriteC = packet.readUShort();
		} else if (opcode == 6) {
			this.spriteD = packet.readUShort();
		} else if (opcode == 7) {
			this.scrollOffsetX = packet.readShort();
		} else if (opcode == 8) {
			this.placementExampleString = packet.readJagString();
		} else if (opcode == 9) {
			this.cyclesVisible = packet.readUShort();
		} else if (opcode == 10) {
			this.scrollOffsetY = packet.readShort();
		} else if (opcode == 11) {
			this.fadeStartCycle = 0;
		} else if (opcode == 12) {
			this.displayType = packet.readUByte();
		} else if (opcode == 13) {
			this.textOffsetY = packet.readShort();
		} else if (opcode == 14) {
			this.fadeStartCycle = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!ia", name = "b", descriptor = "(Lclient!ha;I)V")
	private void generateSprites(@OriginalArg(0) Toolkit toolkit) {
		@Pc(7) Js5ResourceProvider spriteResource = this.typelist.spriteResource;
		@Pc(30) SpriteType indexedImage;
		if (this.spriteA >= 0 && this.typelist.spriteCache.get((long) this.spriteA) == null && spriteResource.isFileReady(this.spriteA)) {
			indexedImage = SpriteDataProvider.listSingle(spriteResource, this.spriteA);
			this.typelist.spriteCache.put(toolkit.generateSprite(indexedImage, true), (long) this.spriteA);
		}
		if (this.spriteC >= 0 && this.typelist.spriteCache.get((long) this.spriteC) == null && spriteResource.isFileReady(this.spriteC)) {
			indexedImage = SpriteDataProvider.listSingle(spriteResource, this.spriteC);
			this.typelist.spriteCache.put(toolkit.generateSprite(indexedImage, true), (long) this.spriteC);
		}
		if (this.spriteB >= 0 && this.typelist.spriteCache.get((long) this.spriteB) == null && spriteResource.isFileReady(this.spriteB)) {
			indexedImage = SpriteDataProvider.listSingle(spriteResource, this.spriteB);
			this.typelist.spriteCache.put(toolkit.generateSprite(indexedImage, true), (long) this.spriteB);
		}
		if (this.spriteD >= 0 && this.typelist.spriteCache.get((long) this.spriteD) == null && spriteResource.isFileReady(this.spriteD)) {
			indexedImage = SpriteDataProvider.listSingle(spriteResource, this.spriteD);
			this.typelist.spriteCache.put(toolkit.generateSprite(indexedImage, true), (long) this.spriteD);
		}
	}

	@OriginalMember(owner = "client!ia", name = "b", descriptor = "(Lclient!ha;B)Lclient!mi;")
	public NativeSprite getMiddleFillSprite(@OriginalArg(0) Toolkit toolkit) {
		if (this.spriteC < 0) {
			return null;
		}
		@Pc(29) NativeSprite sprite = (NativeSprite) this.typelist.spriteCache.get((long) this.spriteC);
		if (sprite == null) {
			this.generateSprites(toolkit);
			sprite = (NativeSprite) this.typelist.spriteCache.get((long) this.spriteC);
		}
		return sprite;
	}

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(ILclient!cea;)V")
	public void decode(@OriginalArg(1) Packet packet) {
		while (true) {
			@Pc(13) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(packet, opcode);
		}
	}

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(II)Ljava/lang/String;")
	public String getAmountText(@OriginalArg(0) int amount) {
		@Pc(8) String string = this.placementExampleString;
		while (true) {
			@Pc(16) int placeholderIndex = string.indexOf("%1");
			if (placeholderIndex < 0) {
				return string;
			}
			string = string.substring(0, placeholderIndex) + Utils.toSignedString(amount, false) + string.substring(placeholderIndex + 2);
		}
	}
}
