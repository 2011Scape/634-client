package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import java.util.Random
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.TextureRaster
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!cc")
class TextureOpIrregularBricks : TextureOperation {

	@OriginalMember(owner = "client!cc", name = "I", descriptor = "I")
	private var cornerRadius: Int = 0

	@OriginalMember(owner = "client!cc", name = "N", descriptor = "I")
	private var offsetVariation: Int = 1024

	@OriginalMember(owner = "client!cc", name = "O", descriptor = "I")
	private var cornerMode: Int = 0

	@OriginalMember(owner = "client!cc", name = "M", descriptor = "I")
	private var brightnessVariation: Int = 1024

	@OriginalMember(owner = "client!cc", name = "Q", descriptor = "I")
	private var heightVariationMultiplier: Int = 1024

	@OriginalMember(owner = "client!cc", name = "U", descriptor = "I")
	private var randomSeed: Int = 0

	@OriginalMember(owner = "client!cc", name = "T", descriptor = "I")
	private var minBrickWidth: Int = 1024

	@OriginalMember(owner = "client!cc", name = "W", descriptor = "I")
	private var maxBrickWidth: Int = 2048

	@OriginalMember(owner = "client!cc", name = "Y", descriptor = "I")
	private var minBrickHeight: Int = 409

	@OriginalMember(owner = "client!cc", name = "Z", descriptor = "I")
	private var maxBrickHeight: Int = 819

	@OriginalMember(owner = "client!cc", name = "<init>", descriptor = "()V")
	constructor() : super(0, true)

	@OriginalMember(owner = "client!cc", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.randomSeed = packet!!.readUByte()
		} else if (opcode == 1) {
			this.minBrickWidth = packet!!.readUShort()
		} else if (opcode == 2) {
			this.maxBrickWidth = packet!!.readUShort()
		} else if (opcode == 3) {
			this.minBrickHeight = packet!!.readUShort()
		} else if (opcode == 4) {
			this.maxBrickHeight = packet!!.readUShort()
		} else if (opcode == 5) {
			this.offsetVariation = packet!!.readUShort()
		} else if (opcode == 6) {
			this.cornerMode = packet!!.readUByte()
		} else if (opcode == 7) {
			this.heightVariationMultiplier = packet!!.readUShort()
		} else if (opcode == 8) {
			this.brightnessVariation = packet!!.readUShort()
		}
	}

	@OriginalMember(owner = "client!cc", name = "e", descriptor = "(I)V")
	override fun postDecode() {
	}

	@OriginalMember(owner = "client!cc", name = "a", descriptor = "(II[[ILjava/util/Random;III)V")
	private fun drawBrick(@OriginalArg(1) brickHeight: Int, @OriginalArg(2) pixelRows: Array<IntArray?>?, @OriginalArg(3) random: Random?, @OriginalArg(4) startX: Int, @OriginalArg(5) startY: Int, @OriginalArg(6) startPosition: Int) {
		var startX: Int = startX
		@Pc(21) val brightness: Int = if (this.brightnessVariation <= 0) 4096 else 4096 - Utils.randomInt(this.brightnessVariation, random)
		@Pc(37) val cornerVariation: Int = (this.cornerRadius * this.heightVariationMultiplier) shr 12
		@Pc(52) val effectiveCornerRadius: Int = this.cornerRadius - (if (cornerVariation > 0) Utils.randomInt(cornerVariation, random) else 0)
		if (EnvironmentLight.textureOpWidth <= startX) {
			startX -= EnvironmentLight.textureOpWidth
		}
		@Pc(75) var local75: Int
		@Pc(77) var local77: Int
		if (effectiveCornerRadius <= 0) {
			if (startX + startPosition > EnvironmentLight.textureOpWidth) {
				val wrapWidth: Int = EnvironmentLight.textureOpWidth - startX
				var row: Int = 0
				while (row < brickHeight) {
					@Pc(85) val rowPixels: IntArray? = pixelRows!![row + startY]
					Arrays.setVariables(rowPixels, startX, wrapWidth, brightness)
					Arrays.setVariables(rowPixels, 0, startPosition - wrapWidth, brightness)
					row++
				}
			} else {
				var row: Int = 0
				while (row < brickHeight) {
					Arrays.setVariables(pixelRows!![row + startY], startX, startPosition, brightness)
					row++
				}
			}
		} else if (brickHeight > 0 && startPosition > 0) {
			val halfWidth: Int = startPosition / 2
			val halfHeight: Int = brickHeight / 2
			@Pc(150) val cornerWidth: Int = if (halfWidth < effectiveCornerRadius) halfWidth else effectiveCornerRadius
			@Pc(157) val cornerHeight: Int = if (halfHeight >= effectiveCornerRadius) effectiveCornerRadius else halfHeight
			@Pc(162) val innerStartX: Int = startX + cornerWidth
			@Pc(168) val innerWidth: Int = startPosition - cornerWidth * 2
			@Pc(170) var i: Int = 0
			while (i < brickHeight) {
				@Pc(179) val rowPixels: IntArray = pixelRows!![i + startY]!!
				@Pc(192) var local192: Int
				@Pc(200) var local200: Int
				@Pc(208) var local208: Int
				if (cornerHeight > i) {
					val verticalFade: Int = brightness * i / cornerHeight
					if (this.cornerMode == 0) {
						var cornerX: Int = 0
						while (cornerX < cornerWidth) {
							val horizontalFade: Int = brightness * cornerX / cornerWidth
							val value: Int = (verticalFade * horizontalFade) shr 12
							rowPixels[TextureRaster.widthMask and (startX + startPosition - cornerX - 1)] = value
							rowPixels[(cornerX + startX) and TextureRaster.widthMask] = value
							cornerX++
						}
					} else {
						var cornerX: Int = 0
						while (cornerX < cornerWidth) {
							val horizontalFade: Int = brightness * cornerX / cornerWidth
							val value: Int = if (horizontalFade < verticalFade) horizontalFade else verticalFade
							rowPixels[(startX + startPosition - cornerX - 1) and TextureRaster.widthMask] = value
							rowPixels[TextureRaster.widthMask and (cornerX + startX)] = value
							cornerX++
						}
					}
					if (EnvironmentLight.textureOpWidth >= innerWidth + innerStartX) {
						Arrays.setVariables(rowPixels, innerStartX, innerWidth, verticalFade)
					} else {
						val wrapWidth: Int = EnvironmentLight.textureOpWidth - innerStartX
						Arrays.setVariables(rowPixels, innerStartX, wrapWidth, verticalFade)
						Arrays.setVariables(rowPixels, 0, innerWidth - wrapWidth, verticalFade)
					}
				} else {
					val distanceFromBottom: Int = brickHeight - i - 1
					if (distanceFromBottom < cornerHeight) {
						val verticalFade: Int = distanceFromBottom * brightness / cornerHeight
						@Pc(352) var horizontalFade: Int
						if (this.cornerMode == 0) {
							var cornerX: Int = 0
							while (cornerX < cornerWidth) {
								horizontalFade = brightness * cornerX / cornerWidth
								val value: Int = (horizontalFade * verticalFade) shr 12
								rowPixels[TextureRaster.widthMask and (startPosition + startX - cornerX - 1)] = value
								rowPixels[TextureRaster.widthMask and (cornerX + startX)] = value
								cornerX++
							}
						} else {
							var cornerX: Int = 0
							while (cornerX < cornerWidth) {
								horizontalFade = cornerX * brightness / cornerWidth
								val value: Int = if (verticalFade <= horizontalFade) verticalFade else horizontalFade
								rowPixels[TextureRaster.widthMask and (startPosition + startX - cornerX - 1)] = value
								rowPixels[TextureRaster.widthMask and (startX + cornerX)] = value
								cornerX++
							}
						}
						if (innerWidth + innerStartX <= EnvironmentLight.textureOpWidth) {
							Arrays.setVariables(rowPixels, innerStartX, innerWidth, verticalFade)
						} else {
							val wrapWidth: Int = EnvironmentLight.textureOpWidth - innerStartX
							Arrays.setVariables(rowPixels, innerStartX, wrapWidth, verticalFade)
							Arrays.setVariables(rowPixels, 0, innerWidth - wrapWidth, verticalFade)
						}
					} else {
						var cornerX: Int = 0
						while (cornerX < cornerWidth) {
							val value: Int = brightness * cornerX / cornerWidth
							rowPixels[TextureRaster.widthMask and (startX + startPosition - cornerX - 1)] = value
							rowPixels[(cornerX + startX) and TextureRaster.widthMask] = value
							cornerX++
						}
						if (EnvironmentLight.textureOpWidth >= innerStartX + innerWidth) {
							Arrays.setVariables(rowPixels, innerStartX, innerWidth, brightness)
						} else {
							val wrapWidth: Int = EnvironmentLight.textureOpWidth - innerStartX
							Arrays.setVariables(rowPixels, innerStartX, wrapWidth, brightness)
							Arrays.setVariables(rowPixels, 0, innerWidth - wrapWidth, brightness)
						}
					}
				}
				i++
			}
		}
	}

	@OriginalMember(owner = "client!cc", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray? {
		@Pc(17) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (!this.monochromeImageCache!!.dirty) {
			return output
		}
		@Pc(26) val pixelRows: Array<IntArray?> = this.monochromeImageCache!!.getAllPaletteData()!!
		@Pc(28) var rowOffsetDelta: Int = 0
		@Pc(30) var currentRowOffset: Int = 0
		@Pc(32) var previousRowOffset: Int = 0
		@Pc(34) var brickStartX: Int = 0
		@Pc(36) var prevRowSlotIndex: Int = 0
		@Pc(38) var lastRow: Boolean = true
		@Pc(40) var firstRow: Boolean = true
		@Pc(42) var prevRowSlotCount: Int = 0
		@Pc(44) var currentSlotCount: Int = 0
		@Pc(51) val minWidthPixels: Int = (this.minBrickWidth * EnvironmentLight.textureOpWidth) shr 12
		@Pc(58) val maxWidthPixels: Int = (EnvironmentLight.textureOpWidth * this.maxBrickWidth) shr 12
		@Pc(65) val minHeightPixels: Int = (this.minBrickHeight * TextureOperation.currentHeight) shr 12
		@Pc(72) val maxHeightPixels: Int = (TextureOperation.currentHeight * this.maxBrickHeight) shr 12
		if (maxHeightPixels <= 1) {
			return pixelRows[row]
		}
		this.cornerRadius = (EnvironmentLight.textureOpWidth / 8 * this.offsetVariation) shr 12
		@Pc(95) val maxSlotsPerRow: Int = EnvironmentLight.textureOpWidth / minWidthPixels + 1
		@Pc(99) var currentRowSlots: Array<IntArray?> = Array<IntArray?>(maxSlotsPerRow) { IntArray(3) }
		@Pc(103) var prevRowSlots: Array<IntArray?> = Array<IntArray?>(maxSlotsPerRow) { IntArray(3) }
		@Pc(110) val random: Random = Random(this.randomSeed.toLong())
		while (true) {
			while (true) {
				@Pc(119) var brickWidth: Int = minWidthPixels + Utils.randomInt(maxWidthPixels - minWidthPixels, random)
				@Pc(129) var brickHeight: Int = Utils.randomInt(maxHeightPixels - minHeightPixels, random) + minHeightPixels
				@Pc(133) var brickEndX: Int = brickStartX + brickWidth
				if (brickEndX > EnvironmentLight.textureOpWidth) {
					brickEndX = EnvironmentLight.textureOpWidth
					brickWidth = EnvironmentLight.textureOpWidth - brickStartX
				}
				@Pc(146) var brickStartY: Int
				@Pc(156) var local156: Int
				if (firstRow) {
					brickStartY = 0
				} else {
					@Pc(150) var slotIndex: Int = prevRowSlotIndex
					@Pc(154) val startSlot: IntArray = prevRowSlots[prevRowSlotIndex]!!
					var skippedSlotCount: Int = 0
					@Pc(160) var wrappedEndX: Int = rowOffsetDelta + brickEndX
					if (wrappedEndX < 0) {
						wrappedEndX += EnvironmentLight.textureOpWidth
					}
					if (EnvironmentLight.textureOpWidth < wrappedEndX) {
						wrappedEndX -= EnvironmentLight.textureOpWidth
					}
					while (true) {
						@Pc(181) val slot: IntArray = prevRowSlots[slotIndex]!!
						if (wrappedEndX >= slot[0] && slot[1] >= wrappedEndX) {
							brickStartY = startSlot[2]
							if (slotIndex != prevRowSlotIndex) {
								@Pc(216) var wrappedStartX: Int = brickStartX + rowOffsetDelta
								if (wrappedStartX < 0) {
									wrappedStartX += EnvironmentLight.textureOpWidth
								}
								if (wrappedStartX > EnvironmentLight.textureOpWidth) {
									wrappedStartX -= EnvironmentLight.textureOpWidth
								}
								@Pc(235) var i: Int = 1
								while (i <= skippedSlotCount) {
									@Pc(245) val gapSlot: IntArray = prevRowSlots[(i + prevRowSlotIndex) % prevRowSlotCount]!!
									brickStartY = Math.max(brickStartY, gapSlot[2])
									i++
								}
								@Pc(261) var j: Int = 0
								while (j <= skippedSlotCount) {
									@Pc(272) val gapSlot: IntArray = prevRowSlots[(prevRowSlotIndex + j) % prevRowSlotCount]!!
									@Pc(276) val gapTop: Int = gapSlot[2]
									if (gapTop != brickStartY) {
										@Pc(287) val gapLeft: Int = gapSlot[0]
										@Pc(291) val gapRight: Int = gapSlot[1]
										@Pc(303) var fillLeft: Int
										@Pc(305) var fillRight: Int
										if (wrappedStartX < wrappedEndX) {
											fillLeft = Math.max(wrappedStartX, gapLeft)
											fillRight = Math.min(wrappedEndX, gapRight)
										} else if (gapLeft == 0) {
											fillLeft = 0
											fillRight = Math.min(wrappedEndX, gapRight)
										} else {
											fillLeft = Math.max(wrappedStartX, gapLeft)
											fillRight = EnvironmentLight.textureOpWidth
										}
										this.drawBrick(brickStartY - gapTop, pixelRows, random, previousRowOffset + fillLeft, gapTop, fillRight - fillLeft)
									}
									j++
								}
							}
							prevRowSlotIndex = slotIndex
							break
						}
						slotIndex++
						if (slotIndex >= prevRowSlotCount) {
							slotIndex = 0
						}
						skippedSlotCount++
					}
				}
				if (brickHeight + brickStartY <= TextureOperation.currentHeight) {
					lastRow = false
				} else {
					brickHeight = TextureOperation.currentHeight - brickStartY
				}
				@Pc(379) var newSlot: IntArray
				if (EnvironmentLight.textureOpWidth == brickEndX) {
					this.drawBrick(brickHeight, pixelRows, random, brickStartX + currentRowOffset, brickStartY, brickWidth)
					if (lastRow) {
						return output
					}
					lastRow = true
					newSlot = currentRowSlots[currentSlotCount++]!!
					newSlot[1] = brickEndX
					newSlot[2] = brickHeight + brickStartY
					newSlot[0] = brickStartX
					@Pc(444) val swapSlots: Array<IntArray?> = prevRowSlots
					prevRowSlots = currentRowSlots
					currentRowSlots = swapSlots
					prevRowSlotCount = currentSlotCount
					previousRowOffset = currentRowOffset
					currentSlotCount = 0
					currentRowOffset = Utils.randomInt(EnvironmentLight.textureOpWidth, random)
					rowOffsetDelta = currentRowOffset - previousRowOffset
					brickStartX = 0
					var wrappedOffset: Int = rowOffsetDelta
					if (rowOffsetDelta < 0) {
						wrappedOffset = rowOffsetDelta + EnvironmentLight.textureOpWidth
					}
					prevRowSlotIndex = 0
					if (EnvironmentLight.textureOpWidth < wrappedOffset) {
						wrappedOffset -= EnvironmentLight.textureOpWidth
					}
					firstRow = false
					while (true) {
						@Pc(496) val searchSlot: IntArray = prevRowSlots[prevRowSlotIndex]!!
						if (wrappedOffset >= searchSlot[0] && wrappedOffset <= searchSlot[1]) {
							break
						}
						prevRowSlotIndex++
						if (prevRowSlotIndex >= prevRowSlotCount) {
							prevRowSlotIndex = 0
						}
					}
				} else {
					newSlot = currentRowSlots[currentSlotCount++]!!
					newSlot[1] = brickEndX
					newSlot[0] = brickStartX
					newSlot[2] = brickHeight + brickStartY
					this.drawBrick(brickHeight, pixelRows, random, brickStartX + currentRowOffset, brickStartY, brickWidth)
					brickStartX = brickEndX
				}
			}
		}
	}
}
