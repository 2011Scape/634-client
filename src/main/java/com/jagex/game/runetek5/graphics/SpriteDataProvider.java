package com.jagex.game.runetek5.graphics;

import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class SpriteDataProvider {

	@OriginalMember(owner = "client!rh", name = "a", descriptor = "(Lclient!in;I)Lclient!rh;")
	public static SpriteType listSingle(@OriginalArg(0) Js5ResourceProvider resource, @OriginalArg(1) int fileId) {
		@Pc(4) byte[] data = resource.getFile(fileId);
		return data == null ? null : load(data)[0];
	}

	@OriginalMember(owner = "client!rh", name = "a", descriptor = "([B)[Lclient!rh;")
	public static SpriteType[] load(@OriginalArg(0) byte[] data) {
		@Pc(4) Packet packet = new Packet(data);
		packet.currentPosition = data.length - 2;
		@Pc(14) int count = packet.readUShort();
		@Pc(17) SpriteType[] indexedImages = new SpriteType[count];
		for (@Pc(19) int i = 0; i < count; i++) {
			indexedImages[i] = new SpriteType();
		}
		packet.currentPosition = data.length - count * 8 - 7;
		@Pc(44) int biggestWidth = packet.readUShort();
		@Pc(48) int biggestHeight = packet.readUShort();
		@Pc(56) int palleteLength = (packet.readUByte() & 0xFF) + 1;
		for (@Pc(58) int j = 0; j < count; j++) {
			indexedImages[j].minX = packet.readUShort();
		}
		for (@Pc(72) int k = 0; k < count; k++) {
			indexedImages[k].minY = packet.readUShort();
		}
		for (@Pc(86) int m = 0; m < count; m++) {
			indexedImages[m].width = packet.readUShort();
		}
		for (@Pc(100) int i = 0; i < count; i++) {
			indexedImages[i].height = packet.readUShort();
		}
		for (@Pc(114) int i = 0; i < count; i++) {
			@Pc(119) SpriteType indexedImage = indexedImages[i];
			indexedImage.borderX = biggestWidth - indexedImage.width - indexedImage.minX;
			indexedImage.borderY = biggestHeight - indexedImage.height - indexedImage.minY;
		}
		packet.currentPosition = data.length - count * 8 - (palleteLength - 1) * 3 - 7;
		@Pc(160) int[] pallete = new int[palleteLength];
		for (@Pc(162) int i = 1; i < palleteLength; i++) {
			pallete[i] = packet.readUMedium();
			if (pallete[i] == 0) {
				pallete[i] = 1;
			}
		}
		for (@Pc(183) int i = 0; i < count; i++) {
			indexedImages[i].pallete = pallete;
		}
		packet.currentPosition = 0;
		for (@Pc(198) int i = 0; i < count; i++) {
			@Pc(203) SpriteType indexedImage2 = indexedImages[i];
			@Pc(209) int numPixels = indexedImage2.width * indexedImage2.height;
			indexedImage2.pixels = new byte[numPixels];
			@Pc(217) int flags = packet.readUByte();
			@Pc(245) int local245;
			if ((flags & 0x2) == 0) {
				@Pc(227) int local227;
				if ((flags & 0x1) == 0) {
					for (int pixelIndex = 0; pixelIndex < numPixels; pixelIndex++) {
						indexedImage2.pixels[pixelIndex] = packet.readByte();
					}
				} else {
					for (int x = 0; x < indexedImage2.width; x++) {
						for (int y = 0; y < indexedImage2.height; y++) {
							indexedImage2.pixels[x + y * indexedImage2.width] = packet.readByte();
						}
					}
				}
			} else {
				@Pc(271) boolean usesAlpha = false;
				indexedImage2.alpha = new byte[numPixels];
				@Pc(295) int local295;
				if ((flags & 0x1) == 0) {
					for (int pixelIndex = 0; pixelIndex < numPixels; pixelIndex++) {
						indexedImage2.pixels[pixelIndex] = packet.readByte();
					}
					for (int pixelIndex = 0; pixelIndex < numPixels; pixelIndex++) {
						@Pc(305) byte alphaValue = indexedImage2.alpha[pixelIndex] = packet.readByte();
						usesAlpha |= alphaValue != -1;
					}
				} else {
					for (int x = 0; x < indexedImage2.width; x++) {
						for (int y = 0; y < indexedImage2.height; y++) {
							indexedImage2.pixels[x + y * indexedImage2.width] = packet.readByte();
						}
					}
					for (int x = 0; x < indexedImage2.width; x++) {
						for (@Pc(352) int y = 0; y < indexedImage2.height; y++) {
							@Pc(367) byte alphaValue = indexedImage2.alpha[x + y * indexedImage2.width] = packet.readByte();
							usesAlpha |= alphaValue != -1;
						}
					}
				}
				if (!usesAlpha) {
					indexedImage2.alpha = null;
				}
			}
		}
		return indexedImages;
	}

	@OriginalMember(owner = "client!rh", name = "b", descriptor = "(Lclient!in;II)Lclient!rh;")
	public static SpriteType listSingle(@OriginalArg(0) Js5ResourceProvider resource, @OriginalArg(1) int archiveId, @OriginalArg(2) int fileId) {
		@Pc(5) byte[] data = resource.getFile(archiveId, fileId);
		return data == null ? null : load(data)[0];
	}
}
