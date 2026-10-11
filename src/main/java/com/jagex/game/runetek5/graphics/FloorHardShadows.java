package com.jagex.game.runetek5.graphics;

import com.jagex.game.runetek5.cache.node.lru.GpuShadow;
import com.jagex.game.runetek5.NativeMatrix;
import jaclib.memory.Buffer;
import jaclib.memory.Stream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.scene.ground.GpuFloorModel;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.TextureTransformMode;

@OriginalClass("client!dg")
public final class FloorHardShadows {

	@OriginalMember(owner = "client!dg", name = "m", descriptor = "[[Lclient!mc;")
	private FloorHardShadowsBlock[][] shadowTiles;

	@OriginalMember(owner = "client!dg", name = "b", descriptor = "Lclient!wga;")
	private final AbstractNativeToolkit nativeToolkit;

	@OriginalMember(owner = "client!dg", name = "g", descriptor = "Lclient!qm;")
	private final GpuFloorModel gpuFloorModel;

	@OriginalMember(owner = "client!dg", name = "d", descriptor = "I")
	public final int width;

	@OriginalMember(owner = "client!dg", name = "n", descriptor = "I")
	private final int length;

	@OriginalMember(owner = "client!dg", name = "c", descriptor = "[B")
	public final byte[] shadowMapperData;

	@OriginalMember(owner = "client!dg", name = "o", descriptor = "I")
	private final int sizeShift;

	@OriginalMember(owner = "client!dg", name = "l", descriptor = "I")
	private final int sizeX;

	@OriginalMember(owner = "client!dg", name = "i", descriptor = "I")
	private final int sizeY;

	@OriginalMember(owner = "client!dg", name = "<init>", descriptor = "(Lclient!wga;Lclient!qm;)V")
	public FloorHardShadows(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) GpuFloorModel gpuFloorModel) {
		this.nativeToolkit = nativeToolkit;
		this.gpuFloorModel = gpuFloorModel;
		this.width = (this.gpuFloorModel.width * this.gpuFloorModel.tileUnits >> this.nativeToolkit.shadowSizeShift) + 2;
		this.length = (this.gpuFloorModel.tileUnits * this.gpuFloorModel.length >> this.nativeToolkit.shadowSizeShift) + 2;
		this.shadowMapperData = new byte[this.width * this.length];
		this.sizeShift = this.nativeToolkit.shadowSizeShift + 7 - this.gpuFloorModel.tileScale;
		this.sizeX = this.gpuFloorModel.width >> this.sizeShift;
		this.sizeY = this.gpuFloorModel.length >> this.sizeShift;
	}

	@OriginalMember(owner = "client!dg", name = "a", descriptor = "(IBLclient!r;I)Z")
	public boolean applyShadow(@OriginalArg(0) int x, @OriginalArg(2) Shadow shadow, @OriginalArg(3) int z) {
		@Pc(6) GpuShadow nativeShadow = (GpuShadow) shadow;
		z += nativeShadow.startZ + 1;
		x += nativeShadow.startX + 1;
		@Pc(35) int dataOffset = z * this.width + x;
		@Pc(38) int clippedHeight = nativeShadow.sizeZ;
		@Pc(41) int clippedWidth = nativeShadow.sizeX;
		@Pc(47) int rowStride = this.width - clippedWidth;
		@Pc(57) int clippedAmount;
		if (z <= 0) {
			clippedAmount = 1 - z;
			clippedHeight -= clippedAmount;
			dataOffset += this.width * clippedAmount;
			z = 1;
		}
		if (clippedHeight + z >= this.length) {
			clippedAmount = clippedHeight + z + 1 - this.length;
			clippedHeight -= clippedAmount;
		}
		if (x <= 0) {
			clippedAmount = 1 - x;
			x = 1;
			rowStride += clippedAmount;
			clippedWidth -= clippedAmount;
			dataOffset += clippedAmount;
		}
		if (clippedWidth + x >= this.width) {
			clippedAmount = x + clippedWidth + 1 - this.width;
			clippedWidth -= clippedAmount;
			rowStride += clippedAmount;
		}
		if (clippedWidth > 0 && clippedHeight > 0) {
			rowStride += this.width * 7;
			return FloorHardShadows.hasUnshadedSample(rowStride, dataOffset, this.shadowMapperData, clippedWidth, clippedHeight);
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!dg", name = "a", descriptor = "(ILclient!r;II)V")
	public void removeShadow(@OriginalArg(0) int z, @OriginalArg(1) Shadow shadow, @OriginalArg(3) int x) {
		@Pc(6) GpuShadow nativeShadow = (GpuShadow) shadow;
		x += nativeShadow.startX + 1;
		z += nativeShadow.startZ + 1;
		@Pc(27) int dataOffset = z * this.width + x;
		@Pc(29) int srcOffset = 0;
		@Pc(32) int clippedHeight = nativeShadow.sizeZ;
		@Pc(35) int clippedWidth = nativeShadow.sizeX;
		@Pc(41) int dstRowStride = this.width - clippedWidth;
		@Pc(48) int clippedAmount;
		if (z <= 0) {
			clippedAmount = 1 - z;
			dataOffset += this.width * clippedAmount;
			clippedHeight -= clippedAmount;
			z = 1;
			srcOffset = clippedAmount * clippedWidth;
		}
		@Pc(69) int srcRowStride = 0;
		if (clippedHeight + z >= this.length) {
			clippedAmount = z + clippedHeight + 1 - this.length;
			clippedHeight -= clippedAmount;
		}
		if (x <= 0) {
			clippedAmount = 1 - x;
			srcRowStride = clippedAmount;
			dstRowStride += clippedAmount;
			x = 1;
			dataOffset += clippedAmount;
			srcOffset += clippedAmount;
			clippedWidth -= clippedAmount;
		}
		if (this.width <= x + clippedWidth) {
			clippedAmount = clippedWidth + x + 1 - this.width;
			dstRowStride += clippedAmount;
			clippedWidth -= clippedAmount;
			srcRowStride += clippedAmount;
		}
		if (clippedWidth > 0 && clippedHeight > 0) {
			FloorHardShadows.subtractShadowData(dstRowStride, clippedWidth, srcRowStride, clippedHeight, dataOffset, this.shadowMapperData, nativeShadow.shade, srcOffset);
			this.invalidateShadowTiles(z, x, clippedHeight, clippedWidth);
		}
	}

	@OriginalMember(owner = "client!dg", name = "a", descriptor = "(IIIZ[[ZI)V")
	public void renderShadowOverlay(@OriginalArg(1) int radius, @OriginalArg(2) int x, @OriginalArg(3) boolean belowLowestPlane, @OriginalArg(4) boolean[][] visibility, @OriginalArg(5) int z) {
		this.nativeToolkit.toggleLights(false);
		this.nativeToolkit.setDepthMaskEnabled(false);
		this.nativeToolkit.toggleBlending(1);
		this.nativeToolkit.setColourType(1);
		this.nativeToolkit.setActiveMaterial(false, false, -2);
		@Pc(41) float texCoordScale = 1.0F / (float) (this.nativeToolkit.activeFlags * 128);
		@Pc(45) int chunkZ;
		@Pc(52) int chunkStartZ;
		@Pc(59) int chunkEndZ;
		@Pc(61) int chunkX;
		if (belowLowestPlane) {
			for (chunkZ = 0; chunkZ < this.sizeY; chunkZ++) {
				chunkStartZ = chunkZ << this.sizeShift;
				chunkEndZ = chunkZ + 1 << this.sizeShift;
				label138: for (chunkX = 0; chunkX < this.sizeX; chunkX++) {
					if (this.shadowTiles[chunkX][chunkZ] != null) {
						@Pc(75) int chunkStartX = chunkX << this.sizeShift;
						@Pc(82) int chunkEndX = chunkX + 1 << this.sizeShift;
						for (@Pc(84) int i = chunkStartX; i < chunkEndX; i++) {
							if (-radius <= i - x && radius >= i - x) {
								for (@Pc(105) int j = chunkStartZ; j < chunkEndZ; j++) {
									if (-radius <= j - z && radius >= j - z && visibility[radius + i - x][j + radius - z]) {
										@Pc(141) NativeMatrix textureMatrix = this.nativeToolkit.getCurrentTextureMatrix();
										textureMatrix.setScale(1.0F, texCoordScale, texCoordScale);
										textureMatrix.translate(-chunkX, -chunkZ, 0);
										this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
										this.shadowTiles[chunkX][chunkZ].drawShadow();
										continue label138;
									}
								}
							}
						}
					}
				}
			}
		} else {
			for (chunkZ = 0; chunkZ < this.sizeY; chunkZ++) {
				chunkStartZ = chunkZ << this.sizeShift;
				chunkEndZ = chunkZ + 1 << this.sizeShift;
				for (chunkX = 0; chunkX < this.sizeX; chunkX++) {
					@Pc(226) FloorHardShadowsBlock floorHardShadowsBlock = this.shadowTiles[chunkX][chunkZ];
					if (floorHardShadowsBlock != null) {
						@Pc(237) GpuIndexBuffer gpuIndexBuffer = this.nativeToolkit.getOrResizeIndexBuffer(floorHardShadowsBlock.triangleCount * 3);
						@Pc(242) Buffer buffer = gpuIndexBuffer.lock();
						if (buffer != null) {
							@Pc(250) Stream stream = this.nativeToolkit.wrapBuffer(buffer);
							@Pc(252) int indexCount = 0;
							@Pc(257) int chunkStartX = chunkX << this.sizeShift;
							@Pc(264) int chunkEndX = chunkX + 1 << this.sizeShift;
							for (@Pc(266) int k = chunkStartZ; k < chunkEndZ; k++) {
								if (k - z >= -radius && radius >= k - z) {
									@Pc(293) int tileIndex = k * this.gpuFloorModel.width + chunkStartX;
									for (@Pc(295) int m = chunkStartX; m < chunkEndX; m++) {
										if (-radius <= m - x && radius >= m - x && visibility[radius + m - x][radius + k - z]) {
											@Pc(335) short[] tileIndices = this.gpuFloorModel.tileVertexIndices[tileIndex];
											if (tileIndices != null) {
												@Pc(341) int index;
												if (Stream.c()) {
													for (index = 0; index < tileIndices.length; index++) {
														stream.d(tileIndices[index] & 0xFFFF);
														indexCount++;
													}
												} else {
													for (index = 0; index < tileIndices.length; index++) {
														indexCount++;
														stream.a(tileIndices[index] & 0xFFFF);
													}
												}
											}
										}
										tileIndex++;
									}
								}
							}
							stream.a();
							if (gpuIndexBuffer.unlock() && indexCount > 0) {
								@Pc(405) NativeMatrix textureMatrix = this.nativeToolkit.getCurrentTextureMatrix();
								textureMatrix.setScale(1.0F, texCoordScale, texCoordScale);
								textureMatrix.translate(-chunkX, -chunkZ, 0);
								this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
								floorHardShadowsBlock.drawShadowElements(gpuIndexBuffer, indexCount / 3);
							}
						}
					}
				}
			}
		}
		this.nativeToolkit.resetCurrentTextureMatrix();
	}

	@OriginalMember(owner = "client!dg", name = "b", descriptor = "(I)V")
	public void initializeShadowTiles() {
		this.shadowTiles = new FloorHardShadowsBlock[this.sizeX][this.sizeY];
		for (@Pc(25) int i = 0; i < this.sizeY; i++) {
			for (@Pc(29) int j = 0; j < this.sizeX; j++) {
				this.shadowTiles[j][i] = new FloorHardShadowsBlock(this.nativeToolkit, this, this.gpuFloorModel, j, i, this.sizeShift, j * 128 + 1, i * 128 + 1);
				if (this.shadowTiles[j][i].triangleCount == 0) {
					this.shadowTiles[j][i] = null;
				}
			}
		}
	}

	@OriginalMember(owner = "client!dg", name = "a", descriptor = "(IILclient!r;I)V")
	public void addShadow(@OriginalArg(1) int x, @OriginalArg(2) Shadow shadow, @OriginalArg(3) int z) {
		@Pc(6) GpuShadow nativeShadow = (GpuShadow) shadow;
		z += nativeShadow.startZ + 1;
		x += nativeShadow.startX + 1;
		@Pc(37) int dataOffset = x + this.width * z;
		@Pc(39) int srcOffset = 0;
		@Pc(42) int clippedHeight = nativeShadow.sizeZ;
		@Pc(45) int clippedWidth = nativeShadow.sizeX;
		@Pc(51) int dstRowStride = this.width - clippedWidth;
		@Pc(58) int clippedAmount;
		if (z <= 0) {
			clippedAmount = 1 - z;
			dataOffset += this.width * clippedAmount;
			clippedHeight -= clippedAmount;
			srcOffset = clippedAmount * clippedWidth;
			z = 1;
		}
		@Pc(79) int srcRowStride = 0;
		if (this.length <= clippedHeight + z) {
			clippedAmount = z + clippedHeight + 1 - this.length;
			clippedHeight -= clippedAmount;
		}
		if (x <= 0) {
			clippedAmount = 1 - x;
			srcOffset += clippedAmount;
			dstRowStride += clippedAmount;
			srcRowStride = clippedAmount;
			dataOffset += clippedAmount;
			x = 1;
			clippedWidth -= clippedAmount;
		}
		if (this.width <= clippedWidth + x) {
			clippedAmount = clippedWidth + x + 1 - this.width;
			srcRowStride += clippedAmount;
			clippedWidth -= clippedAmount;
			dstRowStride += clippedAmount;
		}
		if (clippedWidth > 0 && clippedHeight > 0) {
			FloorHardShadows.addShadowData(clippedWidth, this.shadowMapperData, nativeShadow.shade, srcOffset, clippedHeight, dataOffset, dstRowStride, srcRowStride);
			this.invalidateShadowTiles(z, x, clippedHeight, clippedWidth);
		}
	}

	@OriginalMember(owner = "client!dg", name = "a", descriptor = "(IIIII)V")
	private void invalidateShadowTiles(@OriginalArg(0) int startZ, @OriginalArg(2) int startX, @OriginalArg(3) int regionLength, @OriginalArg(4) int regionWidth) {
		if (this.shadowTiles == null) {
			return;
		}
		@Pc(15) int minChunkX = startX - 1 >> 7;
		@Pc(25) int maxChunkX = regionWidth + startX - 1 - 1 >> 7;
		@Pc(31) int minChunkZ = startZ - 1 >> 7;
		@Pc(42) int maxChunkZ = startZ + regionLength - 1 - 1 >> 7;
		for (@Pc(44) int i = minChunkX; i <= maxChunkX; i++) {
			@Pc(51) FloorHardShadowsBlock[] floorHardShadowsBlocks = this.shadowTiles[i];
			for (@Pc(53) int j = minChunkZ; j <= maxChunkZ; j++) {
				if (floorHardShadowsBlocks[j] != null) {
					floorHardShadowsBlocks[j].needsTextureRegeneration = true;
				}
			}
		}
	}
	@OriginalMember(owner = "client!fd", name = "a", descriptor = "(IIBIII[B[BI)V")
	public static void subtractShadowData(@OriginalArg(0) int dstRowStride, @OriginalArg(1) int width, @OriginalArg(3) int srcRowStride, @OriginalArg(4) int height, @OriginalArg(5) int dstOffset, @OriginalArg(6) byte[] dst, @OriginalArg(7) byte[] src, @OriginalArg(8) int srcOffset) {
		@Pc(10) int blockCount = -(width >> 2);
		@Pc(15) int remainder = -(width & 0x3);
		for (@Pc(23) int i = -height; i < 0; i++) {
			@Pc(31) int dstIndex;
			for (@Pc(27) int j = blockCount; j < 0; j++) {
				dstIndex = dstOffset++;
				dst[dstIndex] = (byte) (dst[dstIndex] - src[srcOffset++]);
				@Pc(44) int dstIndex2 = dstOffset++;
				dst[dstIndex2] = (byte) (dst[dstIndex2] - src[srcOffset++]);
				@Pc(57) int dstIndex3 = dstOffset++;
				dst[dstIndex3] = (byte) (dst[dstIndex3] - src[srcOffset++]);
				@Pc(70) int dstIndex4 = dstOffset++;
				dst[dstIndex4] = (byte) (dst[dstIndex4] - src[srcOffset++]);
			}
			for (@Pc(86) int k = remainder; k < 0; k++) {
				dstIndex = dstOffset++;
				dst[dstIndex] = (byte) (dst[dstIndex] - src[srcOffset++]);
			}
			dstOffset += dstRowStride;
			srcOffset += srcRowStride;
		}
	}

	@OriginalMember(owner = "client!jda", name = "a", descriptor = "(III[BIII)Z")
	public static boolean hasUnshadedSample(@OriginalArg(0) int rowStride, @OriginalArg(1) int offset, @OriginalArg(3) byte[] data, @OriginalArg(4) int width, @OriginalArg(6) int height) {
		@Pc(9) int widthRemainder = width % 8;
		@Pc(15) int padding;
		if (widthRemainder == 0) {
			padding = 0;
		} else {
			padding = 8 - widthRemainder;
		}
		@Pc(28) int rowBlocks = -((height + 8 - 1) / 8);
		@Pc(48) int columnBlocks = -((width + 8 - 1) / 8);
		for (@Pc(50) int i = rowBlocks; i < 0; i++) {
			for (@Pc(54) int j = columnBlocks; j < 0; j++) {
				if (data[offset] == 0) {
					return true;
				}
				offset += 8;
			}
			offset -= padding;
			if (data[offset - 1] == 0) {
				return true;
			}
			offset += rowStride;
		}
		return false;
	}
	@OriginalMember(owner = "client!me", name = "a", descriptor = "(I[B[BIIIIII)V")
	public static void addShadowData(@OriginalArg(0) int width, @OriginalArg(1) byte[] dst, @OriginalArg(2) byte[] src, @OriginalArg(3) int srcOffset, @OriginalArg(5) int height, @OriginalArg(6) int dstOffset, @OriginalArg(7) int dstStride, @OriginalArg(8) int srcStride) {
		@Pc(10) int blockCount = -(width >> 2);
		@Pc(15) int remainder = -(width & 0x3);
		for (@Pc(31) int i = -height; i < 0; i++) {
			@Pc(39) int dstIndex;
			for (@Pc(35) int j = blockCount; j < 0; j++) {
				dstIndex = dstOffset++;
				dst[dstIndex] += src[srcOffset++];
				@Pc(51) int dstIndex2 = dstOffset++;
				dst[dstIndex2] += src[srcOffset++];
				@Pc(63) int dstIndex3 = dstOffset++;
				dst[dstIndex3] += src[srcOffset++];
				@Pc(75) int dstIndex4 = dstOffset++;
				dst[dstIndex4] += src[srcOffset++];
			}
			for (@Pc(93) int k = remainder; k < 0; k++) {
				dstIndex = dstOffset++;
				dst[dstIndex] += src[srcOffset++];
			}
			dstOffset += dstStride;
			srcOffset += srcStride;
		}
	}
}
