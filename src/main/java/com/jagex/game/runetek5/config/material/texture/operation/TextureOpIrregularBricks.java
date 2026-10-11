package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import java.util.Random;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!cc")
public final class TextureOpIrregularBricks extends TextureOperation {

	@OriginalMember(owner = "client!cc", name = "I", descriptor = "I")
	private int cornerRadius;

	@OriginalMember(owner = "client!cc", name = "N", descriptor = "I")
	private int offsetVariation = 1024;

	@OriginalMember(owner = "client!cc", name = "O", descriptor = "I")
	private int cornerMode = 0;

	@OriginalMember(owner = "client!cc", name = "M", descriptor = "I")
	private int brightnessVariation = 1024;

	@OriginalMember(owner = "client!cc", name = "Q", descriptor = "I")
	private int heightVariationMultiplier = 1024;

	@OriginalMember(owner = "client!cc", name = "U", descriptor = "I")
	private int randomSeed = 0;

	@OriginalMember(owner = "client!cc", name = "T", descriptor = "I")
	private int minBrickWidth = 1024;

	@OriginalMember(owner = "client!cc", name = "W", descriptor = "I")
	private int maxBrickWidth = 2048;

	@OriginalMember(owner = "client!cc", name = "Y", descriptor = "I")
	private int minBrickHeight = 409;

	@OriginalMember(owner = "client!cc", name = "Z", descriptor = "I")
	private int maxBrickHeight = 819;

	@OriginalMember(owner = "client!cc", name = "<init>", descriptor = "()V")
	public TextureOpIrregularBricks() {
		super(0, true);
	}

	@OriginalMember(owner = "client!cc", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.randomSeed = packet.readUByte();
		} else if (opcode == 1) {
			this.minBrickWidth = packet.readUShort();
		} else if (opcode == 2) {
			this.maxBrickWidth = packet.readUShort();
		} else if (opcode == 3) {
			this.minBrickHeight = packet.readUShort();
		} else if (opcode == 4) {
			this.maxBrickHeight = packet.readUShort();
		} else if (opcode == 5) {
			this.offsetVariation = packet.readUShort();
		} else if (opcode == 6) {
			this.cornerMode = packet.readUByte();
		} else if (opcode == 7) {
			this.heightVariationMultiplier = packet.readUShort();
		} else if (opcode == 8) {
			this.brightnessVariation = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!cc", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
	}

	@OriginalMember(owner = "client!cc", name = "a", descriptor = "(II[[ILjava/util/Random;III)V")
	private void drawBrick(@OriginalArg(1) int brickHeight, @OriginalArg(2) int[][] pixelRows, @OriginalArg(3) Random random, @OriginalArg(4) int startX, @OriginalArg(5) int startY, @OriginalArg(6) int startPosition) {
		@Pc(21) int brightness = this.brightnessVariation <= 0 ? 4096 : 4096 - Utils.randomInt(this.brightnessVariation, random);
		@Pc(37) int cornerVariation = this.cornerRadius * this.heightVariationMultiplier >> 12;
		@Pc(52) int effectiveCornerRadius = this.cornerRadius - (cornerVariation > 0 ? Utils.randomInt(cornerVariation, random) : 0);
		if (EnvironmentLight.textureOpWidth <= startX) {
			startX -= EnvironmentLight.textureOpWidth;
		}
		@Pc(75) int local75;
		@Pc(77) int local77;
		if (effectiveCornerRadius <= 0) {
			if (startX + startPosition > EnvironmentLight.textureOpWidth) {
				int wrapWidth = EnvironmentLight.textureOpWidth - startX;
				for (int row = 0; row < brickHeight; row++) {
					@Pc(85) int[] rowPixels = pixelRows[row + startY];
					Arrays.setVariables(rowPixels, startX, wrapWidth, brightness);
					Arrays.setVariables(rowPixels, 0, startPosition - wrapWidth, brightness);
				}
			} else {
				for (int row = 0; row < brickHeight; row++) {
					Arrays.setVariables(pixelRows[row + startY], startX, startPosition, brightness);
				}
			}
		} else if (brickHeight > 0 && startPosition > 0) {
			int halfWidth = startPosition / 2;
			int halfHeight = brickHeight / 2;
			@Pc(150) int cornerWidth = halfWidth < effectiveCornerRadius ? halfWidth : effectiveCornerRadius;
			@Pc(157) int cornerHeight = halfHeight >= effectiveCornerRadius ? effectiveCornerRadius : halfHeight;
			@Pc(162) int innerStartX = startX + cornerWidth;
			@Pc(168) int innerWidth = startPosition - cornerWidth * 2;
			for (@Pc(170) int i = 0; i < brickHeight; i++) {
				@Pc(179) int[] rowPixels = pixelRows[i + startY];
				@Pc(192) int local192;
				@Pc(200) int local200;
				@Pc(208) int local208;
				if (cornerHeight > i) {
					int verticalFade = brightness * i / cornerHeight;
					if (this.cornerMode == 0) {
						for (int cornerX = 0; cornerX < cornerWidth; cornerX++) {
							int horizontalFade = brightness * cornerX / cornerWidth;
							rowPixels[cornerX + startX & TextureRaster.widthMask] = rowPixels[TextureRaster.widthMask & startX + startPosition - cornerX - 1] = verticalFade * horizontalFade >> 12;
						}
					} else {
						for (int cornerX = 0; cornerX < cornerWidth; cornerX++) {
							int horizontalFade = brightness * cornerX / cornerWidth;
							rowPixels[TextureRaster.widthMask & cornerX + startX] = rowPixels[startX + startPosition - cornerX - 1 & TextureRaster.widthMask] = horizontalFade < verticalFade ? horizontalFade : verticalFade;
						}
					}
					if (EnvironmentLight.textureOpWidth >= innerWidth + innerStartX) {
						Arrays.setVariables(rowPixels, innerStartX, innerWidth, verticalFade);
					} else {
						int wrapWidth = EnvironmentLight.textureOpWidth - innerStartX;
						Arrays.setVariables(rowPixels, innerStartX, wrapWidth, verticalFade);
						Arrays.setVariables(rowPixels, 0, innerWidth - wrapWidth, verticalFade);
					}
				} else {
					int distanceFromBottom = brickHeight - i - 1;
					if (distanceFromBottom < cornerHeight) {
						int verticalFade = distanceFromBottom * brightness / cornerHeight;
						@Pc(352) int horizontalFade;
						if (this.cornerMode == 0) {
							for (int cornerX = 0; cornerX < cornerWidth; cornerX++) {
								horizontalFade = brightness * cornerX / cornerWidth;
								rowPixels[TextureRaster.widthMask & cornerX + startX] = rowPixels[TextureRaster.widthMask & startPosition + startX - cornerX - 1] = horizontalFade * verticalFade >> 12;
							}
						} else {
							for (int cornerX = 0; cornerX < cornerWidth; cornerX++) {
								horizontalFade = cornerX * brightness / cornerWidth;
								rowPixels[TextureRaster.widthMask & startX + cornerX] = rowPixels[TextureRaster.widthMask & startPosition + startX - cornerX - 1] = verticalFade <= horizontalFade ? verticalFade : horizontalFade;
							}
						}
						if (innerWidth + innerStartX <= EnvironmentLight.textureOpWidth) {
							Arrays.setVariables(rowPixels, innerStartX, innerWidth, verticalFade);
						} else {
							int wrapWidth = EnvironmentLight.textureOpWidth - innerStartX;
							Arrays.setVariables(rowPixels, innerStartX, wrapWidth, verticalFade);
							Arrays.setVariables(rowPixels, 0, innerWidth - wrapWidth, verticalFade);
						}
					} else {
						for (int cornerX = 0; cornerX < cornerWidth; cornerX++) {
							rowPixels[cornerX + startX & TextureRaster.widthMask] = rowPixels[TextureRaster.widthMask & startX + startPosition - cornerX - 1] = brightness * cornerX / cornerWidth;
						}
						if (EnvironmentLight.textureOpWidth >= innerStartX + innerWidth) {
							Arrays.setVariables(rowPixels, innerStartX, innerWidth, brightness);
						} else {
							int wrapWidth = EnvironmentLight.textureOpWidth - innerStartX;
							Arrays.setVariables(rowPixels, innerStartX, wrapWidth, brightness);
							Arrays.setVariables(rowPixels, 0, innerWidth - wrapWidth, brightness);
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!cc", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(17) int[] output = super.monochromeImageCache.get(row);
		if (!super.monochromeImageCache.dirty) {
			return output;
		}
		@Pc(26) int[][] pixelRows = super.monochromeImageCache.getAllPaletteData();
		@Pc(28) int rowOffsetDelta = 0;
		@Pc(30) int currentRowOffset = 0;
		@Pc(32) int previousRowOffset = 0;
		@Pc(34) int brickStartX = 0;
		@Pc(36) int prevRowSlotIndex = 0;
		@Pc(38) boolean lastRow = true;
		@Pc(40) boolean firstRow = true;
		@Pc(42) int prevRowSlotCount = 0;
		@Pc(44) int currentSlotCount = 0;
		@Pc(51) int minWidthPixels = this.minBrickWidth * EnvironmentLight.textureOpWidth >> 12;
		@Pc(58) int maxWidthPixels = EnvironmentLight.textureOpWidth * this.maxBrickWidth >> 12;
		@Pc(65) int minHeightPixels = this.minBrickHeight * TextureOperation.currentHeight >> 12;
		@Pc(72) int maxHeightPixels = TextureOperation.currentHeight * this.maxBrickHeight >> 12;
		if (maxHeightPixels <= 1) {
			return pixelRows[row];
		}
		this.cornerRadius = EnvironmentLight.textureOpWidth / 8 * this.offsetVariation >> 12;
		@Pc(95) int maxSlotsPerRow = EnvironmentLight.textureOpWidth / minWidthPixels + 1;
		@Pc(99) int[][] currentRowSlots = new int[maxSlotsPerRow][3];
		@Pc(103) int[][] prevRowSlots = new int[maxSlotsPerRow][3];
		@Pc(110) Random random = new Random((long) this.randomSeed);
		while (true) {
			while (true) {
				@Pc(119) int brickWidth = minWidthPixels + Utils.randomInt(maxWidthPixels - minWidthPixels, random);
				@Pc(129) int brickHeight = Utils.randomInt(maxHeightPixels - minHeightPixels, random) + minHeightPixels;
				@Pc(133) int brickEndX = brickStartX + brickWidth;
				if (brickEndX > EnvironmentLight.textureOpWidth) {
					brickEndX = EnvironmentLight.textureOpWidth;
					brickWidth = EnvironmentLight.textureOpWidth - brickStartX;
				}
				@Pc(146) int brickStartY;
				@Pc(156) int local156;
				if (firstRow) {
					brickStartY = 0;
				} else {
					@Pc(150) int slotIndex = prevRowSlotIndex;
					@Pc(154) int[] startSlot = prevRowSlots[prevRowSlotIndex];
					int skippedSlotCount = 0;
					@Pc(160) int wrappedEndX = rowOffsetDelta + brickEndX;
					if (wrappedEndX < 0) {
						wrappedEndX += EnvironmentLight.textureOpWidth;
					}
					if (EnvironmentLight.textureOpWidth < wrappedEndX) {
						wrappedEndX -= EnvironmentLight.textureOpWidth;
					}
					while (true) {
						@Pc(181) int[] slot = prevRowSlots[slotIndex];
						if (wrappedEndX >= slot[0] && slot[1] >= wrappedEndX) {
							brickStartY = startSlot[2];
							if (slotIndex != prevRowSlotIndex) {
								@Pc(216) int wrappedStartX = brickStartX + rowOffsetDelta;
								if (wrappedStartX < 0) {
									wrappedStartX += EnvironmentLight.textureOpWidth;
								}
								if (wrappedStartX > EnvironmentLight.textureOpWidth) {
									wrappedStartX -= EnvironmentLight.textureOpWidth;
								}
								for (@Pc(235) int i = 1; i <= skippedSlotCount; i++) {
									@Pc(245) int[] gapSlot = prevRowSlots[(i + prevRowSlotIndex) % prevRowSlotCount];
									brickStartY = Math.max(brickStartY, gapSlot[2]);
								}
								for (@Pc(261) int j = 0; j <= skippedSlotCount; j++) {
									@Pc(272) int[] gapSlot = prevRowSlots[(prevRowSlotIndex + j) % prevRowSlotCount];
									@Pc(276) int gapTop = gapSlot[2];
									if (gapTop != brickStartY) {
										@Pc(287) int gapLeft = gapSlot[0];
										@Pc(291) int gapRight = gapSlot[1];
										@Pc(303) int fillLeft;
										@Pc(305) int fillRight;
										if (wrappedStartX < wrappedEndX) {
											fillLeft = Math.max(wrappedStartX, gapLeft);
											fillRight = Math.min(wrappedEndX, gapRight);
										} else if (gapLeft == 0) {
											fillLeft = 0;
											fillRight = Math.min(wrappedEndX, gapRight);
										} else {
											fillLeft = Math.max(wrappedStartX, gapLeft);
											fillRight = EnvironmentLight.textureOpWidth;
										}
										this.drawBrick(brickStartY - gapTop, pixelRows, random, previousRowOffset + fillLeft, gapTop, fillRight - fillLeft);
									}
								}
							}
							prevRowSlotIndex = slotIndex;
							break;
						}
						slotIndex++;
						if (slotIndex >= prevRowSlotCount) {
							slotIndex = 0;
						}
						skippedSlotCount++;
					}
				}
				if (brickHeight + brickStartY <= TextureOperation.currentHeight) {
					lastRow = false;
				} else {
					brickHeight = TextureOperation.currentHeight - brickStartY;
				}
				@Pc(379) int[] newSlot;
				if (EnvironmentLight.textureOpWidth == brickEndX) {
					this.drawBrick(brickHeight, pixelRows, random, brickStartX + currentRowOffset, brickStartY, brickWidth);
					if (lastRow) {
						return output;
					}
					lastRow = true;
					newSlot = currentRowSlots[currentSlotCount++];
					newSlot[1] = brickEndX;
					newSlot[2] = brickHeight + brickStartY;
					newSlot[0] = brickStartX;
					@Pc(444) int[][] swapSlots = prevRowSlots;
					prevRowSlots = currentRowSlots;
					currentRowSlots = swapSlots;
					prevRowSlotCount = currentSlotCount;
					previousRowOffset = currentRowOffset;
					currentSlotCount = 0;
					currentRowOffset = Utils.randomInt(EnvironmentLight.textureOpWidth, random);
					rowOffsetDelta = currentRowOffset - previousRowOffset;
					brickStartX = 0;
					int wrappedOffset = rowOffsetDelta;
					if (rowOffsetDelta < 0) {
						wrappedOffset = rowOffsetDelta + EnvironmentLight.textureOpWidth;
					}
					prevRowSlotIndex = 0;
					if (EnvironmentLight.textureOpWidth < wrappedOffset) {
						wrappedOffset -= EnvironmentLight.textureOpWidth;
					}
					firstRow = false;
					while (true) {
						@Pc(496) int[] searchSlot = prevRowSlots[prevRowSlotIndex];
						if (wrappedOffset >= searchSlot[0] && wrappedOffset <= searchSlot[1]) {
							break;
						}
						prevRowSlotIndex++;
						if (prevRowSlotIndex >= prevRowSlotCount) {
							prevRowSlotIndex = 0;
						}
					}
				} else {
					newSlot = currentRowSlots[currentSlotCount++];
					newSlot[1] = brickEndX;
					newSlot[0] = brickStartX;
					newSlot[2] = brickHeight + brickStartY;
					this.drawBrick(brickHeight, pixelRows, random, brickStartX + currentRowOffset, brickStartY, brickWidth);
					brickStartX = brickEndX;
				}
			}
		}
	}
}
