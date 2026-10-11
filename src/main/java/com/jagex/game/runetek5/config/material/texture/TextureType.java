package com.jagex.game.runetek5.config.material.texture;

import com.jagex.game.runetek5.cache.node.lru.CacheableNode;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.BrightnessTableBuilder;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpConstant;

@OriginalClass("client!lr")
public final class TextureType extends CacheableNode {

	@OriginalMember(owner = "client!lr", name = "z", descriptor = "[I")
	private final int[] materialFileIds;

	@OriginalMember(owner = "client!lr", name = "A", descriptor = "[I")
	private final int[] textureFileIds;

	@OriginalMember(owner = "client!lr", name = "E", descriptor = "Lclient!tf;")
	private final TextureOperation hdrOperation;

	@OriginalMember(owner = "client!lr", name = "x", descriptor = "Lclient!tf;")
	private final TextureOperation opaqueOperation;

	@OriginalMember(owner = "client!lr", name = "D", descriptor = "Lclient!tf;")
	private final TextureOperation opacityOperation;

	@OriginalMember(owner = "client!lr", name = "w", descriptor = "[Lclient!tf;")
	private final TextureOperation[] textureOperations;

	@OriginalMember(owner = "client!lr", name = "<init>", descriptor = "()V")
	private TextureType() {
		this.materialFileIds = new int[0];
		this.textureFileIds = new int[0];
		this.hdrOperation = new TextureOpConstant(0);
		this.hdrOperation.imageCacheCapacity = 1;
		this.opaqueOperation = new TextureOpConstant();
		this.opaqueOperation.imageCacheCapacity = 1;
		this.opacityOperation = new TextureOpConstant();
		this.textureOperations = new TextureOperation[] { this.opaqueOperation, this.opacityOperation, this.hdrOperation };
		this.opacityOperation.imageCacheCapacity = 1;
	}

	@OriginalMember(owner = "client!lr", name = "<init>", descriptor = "(Lclient!cea;)V")
	public TextureType(@OriginalArg(0) Packet packet) {
		@Pc(7) int operationCount = packet.readUByte();
		@Pc(9) int textureFileCount = 0;
		@Pc(11) int materialFileCount = 0;
		@Pc(14) int[][] childIndices = new int[operationCount][];
		this.textureOperations = new TextureOperation[operationCount];
		@Pc(50) int length;
		for (@Pc(20) int i = 0; i < operationCount; i++) {
			@Pc(26) TextureOperation textureOp = TextureTypeList.decodeTextureOperation(packet);
			if (textureOp.getSpriteId() >= 0) {
				textureFileCount++;
			}
			if (textureOp.getTextureId() >= 0) {
				materialFileCount++;
			}
			@Pc(43) int childCount = textureOp.operations.length;
			childIndices[i] = new int[childCount];
			for (length = 0; length < childCount; length++) {
				childIndices[i][length] = packet.readUByte();
			}
			this.textureOperations[i] = textureOp;
		}
		this.textureFileIds = new int[textureFileCount];
		this.materialFileIds = new int[materialFileCount];
		textureFileCount = 0;
		materialFileCount = 0;
		for (@Pc(91) int j = 0; j < operationCount; j++) {
			@Pc(98) TextureOperation textureOp2 = this.textureOperations[j];
			length = textureOp2.operations.length;
			for (@Pc(104) int k = 0; k < length; k++) {
				textureOp2.operations[k] = this.textureOperations[childIndices[j][k]];
			}
			@Pc(130) int spriteId = textureOp2.getSpriteId();
			@Pc(134) int textureId = textureOp2.getTextureId();
			if (spriteId > 0) {
				this.textureFileIds[textureFileCount++] = spriteId;
			}
			if (textureId > 0) {
				this.materialFileIds[materialFileCount++] = textureId;
			}
			childIndices[j] = null;
		}
		this.opaqueOperation = this.textureOperations[packet.readUByte()];
		this.opacityOperation = this.textureOperations[packet.readUByte()];
		this.hdrOperation = this.textureOperations[packet.readUByte()];
	}

	@OriginalMember(owner = "client!lr", name = "a", descriptor = "(Lclient!d;IIZDLclient!in;B)[I")
	public int[] getPixelsArgb(@OriginalArg(0) MaterialProvider textureSource, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) boolean transposed, @OriginalArg(4) double currentBrightness, @OriginalArg(5) Js5ResourceProvider textureSpriteResource) {
		TextureTypeList.textureSource = textureSource;
		TextureTypeList.textureSpriteResource = textureSpriteResource;
		for (@Pc(11) int i = 0; i < this.textureOperations.length; i++) {
			this.textureOperations[i].initCache(width, height);
		}
		BrightnessTableBuilder.setBrightness(currentBrightness);
		TextureRaster.setDimensions(height, width);
		@Pc(39) int[] pixels = new int[width * height];
		@Pc(41) int writeIndex = 0;
		for (@Pc(43) int j = 0; j < height; j++) {
			@Pc(57) int[] redChannel;
			@Pc(61) int[] greenChannel;
			@Pc(59) int[] blueChannel;
			@Pc(55) int[] local55;
			if (this.opaqueOperation.monochrome) {
				int[] monochromeChannel = this.opaqueOperation.monochromeOutput(j);
				redChannel = monochromeChannel;
				blueChannel = monochromeChannel;
				greenChannel = monochromeChannel;
			} else {
				@Pc(69) int[][] colorOutput = this.opaqueOperation.getColorOutput(j);
				blueChannel = colorOutput[2];
				greenChannel = colorOutput[1];
				redChannel = colorOutput[0];
			}
			if (transposed) {
				writeIndex = j;
			}
			int[] alphaChannel;
			if (this.opacityOperation.monochrome) {
				alphaChannel = this.opacityOperation.monochromeOutput(j);
			} else {
				alphaChannel = this.opacityOperation.getColorOutput(j)[0];
			}
			for (@Pc(111) int k = width - 1; k >= 0; k--) {
				@Pc(119) int red = redChannel[k] >> 4;
				if (red > 255) {
					red = 255;
				}
				if (red < 0) {
					red = 0;
				}
				@Pc(137) int green = greenChannel[k] >> 4;
				if (green > 255) {
					green = 255;
				}
				if (green < 0) {
					green = 0;
				}
				@Pc(152) int blue = blueChannel[k] >> 4;
				if (blue > 255) {
					blue = 255;
				}
				green = TextureTypeList.BRIGHTNESS_TABLE[green];
				if (blue < 0) {
					blue = 0;
				}
				red = TextureTypeList.BRIGHTNESS_TABLE[red];
				blue = TextureTypeList.BRIGHTNESS_TABLE[blue];
				@Pc(184) int alpha;
				if (red == 0 && green == 0 && blue == 0) {
					alpha = 0;
				} else {
					alpha = alphaChannel[k] >> 4;
					if (alpha > 255) {
						alpha = 255;
					}
					if (alpha < 0) {
						alpha = 0;
					}
				}
				pixels[writeIndex++] = blue + (alpha << 24) + (red << 16) + (green << 8);
				if (transposed) {
					writeIndex += width - 1;
				}
			}
		}
		for (@Pc(244) int m = 0; m < this.textureOperations.length; m++) {
			this.textureOperations[m].cacheReset();
		}
		return pixels;
	}

	@OriginalMember(owner = "client!lr", name = "a", descriptor = "(Lclient!d;Lclient!in;I)Z")
	public boolean isReady(@OriginalArg(0) MaterialProvider textureSource, @OriginalArg(1) Js5ResourceProvider spriteResource) {
		@Pc(12) int i;
		if (TextureType.SPRITE_GROUP_ID < 0) {
			for (i = 0; i < this.textureFileIds.length; i++) {
				if (!spriteResource.isFileReady(this.textureFileIds[i])) {
					return false;
				}
			}
		} else {
			for (i = 0; i < this.textureFileIds.length; i++) {
				if (!spriteResource.isFileReady(TextureType.SPRITE_GROUP_ID, this.textureFileIds[i])) {
					return false;
				}
			}
		}
		for (i = 0; i < this.materialFileIds.length; i++) {
			if (!textureSource.textureAvailable(this.materialFileIds[i])) {
				return false;
			}
		}
		return true;
	}

	@OriginalMember(owner = "client!lr", name = "a", descriptor = "(ILclient!d;IZDZLclient!in;I)[I")
	public int[] getPixelsRgb(@OriginalArg(0) int width, @OriginalArg(1) MaterialProvider textureSource, @OriginalArg(3) boolean transposed, @OriginalArg(4) double currentBrightness, @OriginalArg(5) boolean readReversed, @OriginalArg(6) Js5ResourceProvider textureSpriteResource, @OriginalArg(7) int height) {
		TextureTypeList.textureSource = textureSource;
		TextureTypeList.textureSpriteResource = textureSpriteResource;
		for (@Pc(11) int i = 0; i < this.textureOperations.length; i++) {
			this.textureOperations[i].initCache(width, height);
		}
		BrightnessTableBuilder.setBrightness(currentBrightness);
		TextureRaster.setDimensions(height, width);
		@Pc(41) int[] pixels = new int[height * width];
		@Pc(45) int startX;
		@Pc(49) int endX;
		@Pc(47) byte step;
		if (readReversed) {
			startX = width - 1;
			step = -1;
			endX = -1;
		} else {
			startX = 0;
			step = 1;
			endX = width;
		}
		@Pc(61) int writeIndex = 0;
		for (@Pc(63) int j = 0; j < height; j++) {
			@Pc(79) int[] redChannel;
			@Pc(77) int[] greenChannel;
			@Pc(81) int[] blueChannel;
			if (this.opaqueOperation.monochrome) {
				@Pc(75) int[] monochromeOutput = this.opaqueOperation.monochromeOutput(j);
				greenChannel = monochromeOutput;
				redChannel = monochromeOutput;
				blueChannel = monochromeOutput;
			} else {
				@Pc(89) int[][] colorOutput = this.opaqueOperation.getColorOutput(j);
				greenChannel = colorOutput[1];
				redChannel = colorOutput[0];
				blueChannel = colorOutput[2];
			}
			if (transposed) {
				writeIndex = j;
			}
			for (@Pc(107) int k = startX; k != endX; k += step) {
				@Pc(115) int red = redChannel[k] >> 4;
				if (red > 255) {
					red = 255;
				}
				if (red < 0) {
					red = 0;
				}
				@Pc(130) int green = greenChannel[k] >> 4;
				if (green > 255) {
					green = 255;
				}
				if (green < 0) {
					green = 0;
				}
				@Pc(145) int blue = blueChannel[k] >> 4;
				if (blue > 255) {
					blue = 255;
				}
				green = TextureTypeList.BRIGHTNESS_TABLE[green];
				red = TextureTypeList.BRIGHTNESS_TABLE[red];
				if (blue < 0) {
					blue = 0;
				}
				blue = TextureTypeList.BRIGHTNESS_TABLE[blue];
				@Pc(181) int rgb = (green << 8) + (red << 16) + blue;
				if (rgb != 0) {
					rgb |= 0xFF000000;
				}
				pixels[writeIndex++] = rgb;
				if (transposed) {
					writeIndex += width - 1;
				}
			}
		}
		for (@Pc(216) int m = 0; m < this.textureOperations.length; m++) {
			this.textureOperations[m].cacheReset();
		}
		return pixels;
	}

	@OriginalMember(owner = "client!lr", name = "a", descriptor = "(ILclient!d;ZILclient!in;I)[F")
	public float[] getPixelsHdr(@OriginalArg(0) int width, @OriginalArg(1) MaterialProvider textureSource, @OriginalArg(2) boolean transposed, @OriginalArg(3) int height, @OriginalArg(4) Js5ResourceProvider textureSpriteResource) {
		TextureTypeList.textureSpriteResource = textureSpriteResource;
		TextureTypeList.textureSource = textureSource;
		for (@Pc(24) int i = 0; i < this.textureOperations.length; i++) {
			this.textureOperations[i].initCache(width, height);
		}
		TextureRaster.setDimensions(height, width);
		@Pc(51) float[] pixels = new float[width * 4 * height];
		@Pc(53) int writeIndex = 0;
		for (@Pc(55) int j = 0; j < height; j++) {
			@Pc(75) int[] redChannel;
			@Pc(71) int[] greenChannel;
			@Pc(79) int[] blueChannel;
			if (this.opaqueOperation.monochrome) {
				@Pc(87) int[] monochromeOutput = this.opaqueOperation.monochromeOutput(j);
				blueChannel = monochromeOutput;
				greenChannel = monochromeOutput;
				redChannel = monochromeOutput;
			} else {
				@Pc(67) int[][] colorOutput = this.opaqueOperation.getColorOutput(j);
				greenChannel = colorOutput[1];
				redChannel = colorOutput[0];
				blueChannel = colorOutput[2];
			}
			@Pc(103) int[] opacityChannel;
			if (this.opacityOperation.monochrome) {
				opacityChannel = this.opacityOperation.monochromeOutput(j);
			} else {
				opacityChannel = this.opacityOperation.getColorOutput(j)[0];
			}
			@Pc(123) int[] hdrChannel;
			if (this.hdrOperation.monochrome) {
				hdrChannel = this.hdrOperation.monochromeOutput(j);
			} else {
				hdrChannel = this.hdrOperation.getColorOutput(j)[0];
			}
			if (transposed) {
				writeIndex = j << 2;
			}
			for (@Pc(143) int k = width - 1; k >= 0; k--) {
				@Pc(152) float alpha = (float) opacityChannel[k] / 4096.0F;
				@Pc(165) float intensity = ((float) hdrChannel[k] * 31.0F / 4096.0F + 1.0F) / 4096.0F;
				if (alpha < 0.0F) {
					alpha = 0.0F;
				} else if (alpha > 1.0F) {
					alpha = 1.0F;
				}
				pixels[writeIndex++] = (float) redChannel[k] * intensity;
				pixels[writeIndex++] = intensity * (float) greenChannel[k];
				pixels[writeIndex++] = (float) blueChannel[k] * intensity;
				pixels[writeIndex++] = alpha;
				if (transposed) {
					writeIndex += (width << 2) - 4;
				}
			}
		}
		for (@Pc(236) int m = 0; m < this.textureOperations.length; m++) {
			this.textureOperations[m].cacheReset();
		}
		return pixels;
	}
	@OriginalMember(owner = "client!haa", name = "E", descriptor = "I")
	public static final int SPRITE_GROUP_ID = -1;
}
