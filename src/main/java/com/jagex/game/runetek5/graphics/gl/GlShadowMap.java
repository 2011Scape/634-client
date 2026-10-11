package com.jagex.game.runetek5.graphics.gl;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.GlShadow;
import com.jagex.game.runetek5.graphics.shadow.ShadowTileGL;
import com.jagex.game.runetek5.scene.ground.GlGround;

@OriginalClass("client!mha")
public final class GlShadowMap {

	@OriginalMember(owner = "client!mha", name = "n", descriptor = "[[Lclient!sm;")
	private ShadowTileGL[][] shadowTiles;

	@OriginalMember(owner = "client!mha", name = "g", descriptor = "Lclient!qo;")
	private final GlToolkit glToolkit;

	@OriginalMember(owner = "client!mha", name = "m", descriptor = "Lclient!ed;")
	private final GlGround glGround;

	@OriginalMember(owner = "client!mha", name = "o", descriptor = "I")
	public final int width;

	@OriginalMember(owner = "client!mha", name = "c", descriptor = "I")
	private final int length;

	@OriginalMember(owner = "client!mha", name = "d", descriptor = "I")
	private final int sizeShift;

	@OriginalMember(owner = "client!mha", name = "h", descriptor = "[B")
	public final byte[] shadowMapperData;

	@OriginalMember(owner = "client!mha", name = "i", descriptor = "I")
	private final int sizeX;

	@OriginalMember(owner = "client!mha", name = "p", descriptor = "I")
	private final int sizeY;

	@OriginalMember(owner = "client!mha", name = "<init>", descriptor = "(Lclient!qo;Lclient!ed;)V")
	public GlShadowMap(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) GlGround glGround) {
		this.glToolkit = glToolkit;
		this.glGround = glGround;
		this.width = (this.glGround.tileUnits * this.glGround.width >> this.glToolkit.shadowScaleShift) + 2;
		this.length = (this.glGround.tileUnits * this.glGround.length >> this.glToolkit.shadowScaleShift) + 2;
		this.sizeShift = this.glToolkit.shadowScaleShift + 7 - this.glGround.tileScale;
		this.shadowMapperData = new byte[this.width * this.length];
		this.sizeX = this.glGround.width >> this.sizeShift;
		this.sizeY = this.glGround.length >> this.sizeShift;
	}

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(Lclient!r;III)V")
	public void removeShadow(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int y) {
		@Pc(6) GlShadow glShadow = (GlShadow) shadow;
		x += glShadow.startX + 1;
		y += glShadow.startZ + 1;
		@Pc(27) int dataOffset = this.width * y + x;
		@Pc(29) int srcOffset = 0;
		@Pc(32) int clippedHeight = glShadow.sizeZ;
		@Pc(35) int clippedWidth = glShadow.sizeX;
		@Pc(40) int dstRowStride = this.width - clippedWidth;
		@Pc(42) int srcRowStride = 0;
		@Pc(49) int clippedAmount;
		if (y <= 0) {
			clippedAmount = 1 - y;
			srcOffset = clippedAmount * clippedWidth;
			clippedHeight -= clippedAmount;
			y = 1;
			dataOffset += clippedAmount * this.width;
		}
		if (this.length <= y + clippedHeight) {
			clippedAmount = clippedHeight + y + 1 - this.length;
			clippedHeight -= clippedAmount;
		}
		if (x <= 0) {
			clippedAmount = 1 - x;
			clippedWidth -= clippedAmount;
			dataOffset += clippedAmount;
			x = 1;
			dstRowStride += clippedAmount;
			srcRowStride = clippedAmount;
			srcOffset += clippedAmount;
		}
		if (this.width <= clippedWidth + x) {
			clippedAmount = x + clippedWidth + 1 - this.width;
			srcRowStride += clippedAmount;
			dstRowStride += clippedAmount;
			clippedWidth -= clippedAmount;
		}
		if (clippedWidth > 0 && clippedHeight > 0) {
			GlShadowMap.subtractShadowData(srcOffset, this.shadowMapperData, clippedHeight, dataOffset, dstRowStride, srcRowStride, glShadow.shade, clippedWidth);
			this.invalidateShadowTiles(clippedHeight, clippedWidth, x, y);
		}
	}

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(IIIII)V")
	private void invalidateShadowTiles(@OriginalArg(0) int regionHeight, @OriginalArg(1) int regionWidth, @OriginalArg(2) int startX, @OriginalArg(4) int startY) {
		if (this.shadowTiles == null) {
			return;
		}
		@Pc(15) int minChunkX = startX - 1 >> 7;
		@Pc(26) int maxChunkX = startX + regionWidth - 1 - 1 >> 7;
		@Pc(32) int minChunkY = startY - 1 >> 7;
		@Pc(42) int maxChunkY = regionHeight + startY - 1 - 1 >> 7;
		for (@Pc(44) int i = minChunkX; i <= maxChunkX; i++) {
			@Pc(51) ShadowTileGL[] glShadowMapBlocks = this.shadowTiles[i];
			for (@Pc(53) int j = minChunkY; j <= maxChunkY; j++) {
				glShadowMapBlocks[j].needsTextureRegeneration = true;
			}
		}
	}

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(IBILclient!r;)Z")
	public boolean shouldApplyShadow(@OriginalArg(0) int x, @OriginalArg(2) int y, @OriginalArg(3) Shadow shadow) {
		@Pc(6) GlShadow glShadow = (GlShadow) shadow;
		y += glShadow.startZ + 1;
		x += glShadow.startX + 1;
		@Pc(33) int dataOffset = y * this.width + x;
		@Pc(36) int clippedHeight = glShadow.sizeZ;
		@Pc(39) int clippedWidth = glShadow.sizeX;
		@Pc(45) int rowStride = this.width - clippedWidth;
		@Pc(52) int clippedAmount;
		if (y <= 0) {
			clippedAmount = 1 - y;
			clippedHeight -= clippedAmount;
			y = 1;
			dataOffset += this.width * clippedAmount;
		}
		if (this.length <= clippedHeight + y) {
			clippedAmount = y + clippedHeight + 1 - this.length;
			clippedHeight -= clippedAmount;
		}
		if (x <= 0) {
			clippedAmount = 1 - x;
			clippedWidth -= clippedAmount;
			dataOffset += clippedAmount;
			rowStride += clippedAmount;
			x = 1;
		}
		if (this.width <= x + clippedWidth) {
			clippedAmount = x + clippedWidth + 1 - this.width;
			clippedWidth -= clippedAmount;
			rowStride += clippedAmount;
		}
		if (clippedWidth > 0 && clippedHeight > 0) {
			rowStride += this.width * 7;
			return GlShadowMap.containsUnshadowedBlock(rowStride, clippedHeight, clippedWidth, dataOffset, this.shadowMapperData);
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(ZI[[ZIII)V")
	public void renderShadowOverlay(@OriginalArg(0) boolean belowLowestPlane, @OriginalArg(2) boolean[][] tilesOnScreen, @OriginalArg(3) int visibleRange, @OriginalArg(4) int cameraGridY, @OriginalArg(5) int cameraGridX) {
		this.glToolkit.setLightingEnabled(false);
		this.glToolkit.setDepthMask(false);
		this.glToolkit.renderTexture(-2);
		this.glToolkit.setColourOp(1);
		this.glToolkit.setBlendMode(1);
		@Pc(47) float texCoordScale = 1.0F / (float) (this.glToolkit.shadowScale * 128);
		@Pc(51) int chunkY;
		@Pc(58) int chunkStartY;
		@Pc(65) int chunkEndY;
		@Pc(67) int chunkX;
		@Pc(71) int local71;
		@Pc(76) int local76;
		@Pc(83) int local83;
		if (belowLowestPlane) {
			for (chunkY = 0; chunkY < this.sizeY; chunkY++) {
				chunkStartY = chunkY << this.sizeShift;
				chunkEndY = chunkY + 1 << this.sizeShift;
				label81: for (chunkX = 0; chunkX < this.sizeX; chunkX++) {
					int chunkStartX = chunkX << this.sizeShift;
					int chunkEndX = chunkX + 1 << this.sizeShift;
					for (int tileX = chunkStartX; tileX < chunkEndX; tileX++) {
						if (tileX - cameraGridX >= -visibleRange && visibleRange >= tileX - cameraGridX) {
							for (@Pc(331) int i = chunkStartY; i < chunkEndY; i++) {
								if (-visibleRange <= i - cameraGridY && i - cameraGridY <= visibleRange && tilesOnScreen[visibleRange + tileX - cameraGridX][visibleRange + i - cameraGridY]) {
									OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
									OpenGL.glLoadIdentity();
									OpenGL.glScalef(texCoordScale, texCoordScale, 1.0F);
									OpenGL.glTranslatef((float) -chunkX / texCoordScale, (float) -chunkY / texCoordScale, 1.0F);
									OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
									this.shadowTiles[chunkX][chunkY].drawShadow();
									continue label81;
								}
							}
						}
					}
				}
			}
		} else {
			for (chunkY = 0; chunkY < this.sizeY; chunkY++) {
				chunkStartY = chunkY << this.sizeShift;
				chunkEndY = chunkY + 1 << this.sizeShift;
				for (chunkX = 0; chunkX < this.sizeX; chunkX++) {
					int indexCount = 0;
					int chunkStartX = chunkX << this.sizeShift;
					int chunkEndX = chunkX + 1 << this.sizeShift;
					@Pc(87) GlBitBuffer gpuPacket = this.glToolkit.textureBuffer;
					gpuPacket.currentPosition = 0;
					for (@Pc(92) int j = chunkStartY; j < chunkEndY; j++) {
						if (j - cameraGridY >= -visibleRange && j - cameraGridY <= visibleRange) {
							@Pc(119) int tileIndex = chunkStartX + j * this.glGround.width;
							for (@Pc(121) int k = chunkStartX; k < chunkEndX; k++) {
								if (k - cameraGridX >= -visibleRange && k - cameraGridX <= visibleRange && tilesOnScreen[visibleRange + k - cameraGridX][visibleRange + j - cameraGridY]) {
									@Pc(161) short[] tileVertexIndices = this.glGround.tileVertexIndices[tileIndex];
									if (tileVertexIndices != null) {
										@Pc(169) int index;
										if (this.glToolkit.bigEndianess) {
											for (index = 0; index < tileVertexIndices.length; index++) {
												indexCount++;
												gpuPacket.writeShort(tileVertexIndices[index] & 0xFFFF);
											}
										} else {
											for (index = 0; index < tileVertexIndices.length; index++) {
												gpuPacket.writeShortLE(tileVertexIndices[index] & 0xFFFF);
												indexCount++;
											}
										}
									}
								}
								tileIndex++;
							}
						}
					}
					if (indexCount > 0) {
						OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
						OpenGL.glLoadIdentity();
						OpenGL.glScalef(texCoordScale, texCoordScale, 1.0F);
						OpenGL.glTranslatef((float) -chunkX / texCoordScale, (float) -chunkY / texCoordScale, 1.0F);
						OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
						this.shadowTiles[chunkX][chunkY].drawShadowWithIndices(gpuPacket.buffer, indexCount);
					}
				}
			}
		}
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
		OpenGL.glLoadIdentity();
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
	}

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(I)V")
	public void initializeShadowTiles() {
		this.shadowTiles = new ShadowTileGL[this.sizeX][this.sizeY];
		for (@Pc(14) int chunkY = 0; chunkY < this.sizeY; chunkY++) {
			for (@Pc(18) int chunkX = 0; chunkX < this.sizeX; chunkX++) {
				this.shadowTiles[chunkX][chunkY] = new ShadowTileGL(this.glToolkit, this, this.glGround, chunkX, chunkY, this.sizeShift, chunkX * 128 + 1, chunkY * 128 + 1);
			}
		}
	}

	@OriginalMember(owner = "client!mha", name = "a", descriptor = "(IIBLclient!r;)V")
	public void addShadow(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(3) Shadow shadow) {
		@Pc(6) GlShadow glShadow = (GlShadow) shadow;
		y += glShadow.startZ + 1;
		x += glShadow.startX + 1;
		@Pc(27) int dataOffset = this.width * y + x;
		@Pc(29) int srcOffset = 0;
		@Pc(32) int clippedHeight = glShadow.sizeZ;
		@Pc(35) int clippedWidth = glShadow.sizeX;
		@Pc(46) int dstRowStride = this.width - clippedWidth;
		@Pc(53) int clippedAmount;
		if (y <= 0) {
			clippedAmount = 1 - y;
			srcOffset = clippedWidth * clippedAmount;
			clippedHeight -= clippedAmount;
			y = 1;
			dataOffset += clippedAmount * this.width;
		}
		@Pc(74) int srcRowStride = 0;
		if (y + clippedHeight >= this.length) {
			clippedAmount = y + clippedHeight + 1 - this.length;
			clippedHeight -= clippedAmount;
		}
		if (x <= 0) {
			clippedAmount = 1 - x;
			clippedWidth -= clippedAmount;
			dataOffset += clippedAmount;
			x = 1;
			srcRowStride = clippedAmount;
			srcOffset += clippedAmount;
			dstRowStride += clippedAmount;
		}
		if (this.width <= clippedWidth + x) {
			clippedAmount = clippedWidth + x + 1 - this.width;
			srcRowStride += clippedAmount;
			clippedWidth -= clippedAmount;
			dstRowStride += clippedAmount;
		}
		if (clippedWidth > 0 && clippedHeight > 0) {
			GlShadowMap.addShadowData(srcRowStride, srcOffset, glShadow.shade, clippedWidth, this.shadowMapperData, clippedHeight, dataOffset, dstRowStride);
			this.invalidateShadowTiles(clippedHeight, clippedWidth, x, y);
		}
	}
	@OriginalMember(owner = "client!tha", name = "a", descriptor = "(I[BIIII[BIB)V")
	public static void subtractShadowData(@OriginalArg(0) int srcOffset, @OriginalArg(1) byte[] dst, @OriginalArg(2) int height, @OriginalArg(3) int dstOffset, @OriginalArg(4) int dstRowStride, @OriginalArg(5) int srcRowStride, @OriginalArg(6) byte[] src, @OriginalArg(7) int width) {
		@Pc(10) int blockCount = -(width >> 2);
		@Pc(15) int remainderCount = -(width & 0x3);
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
			for (@Pc(86) int k = remainderCount; k < 0; k++) {
				dstIndex = dstOffset++;
				dst[dstIndex] = (byte) (dst[dstIndex] - src[srcOffset++]);
			}
			dstOffset += dstRowStride;
			srcOffset += srcRowStride;
		}
	}

	@OriginalMember(owner = "client!hk", name = "a", descriptor = "(III[BI[BIII)V")
	public static void addShadowData(@OriginalArg(0) int srcRowStride, @OriginalArg(1) int srcOffset, @OriginalArg(3) byte[] src, @OriginalArg(4) int width, @OriginalArg(5) byte[] dst, @OriginalArg(6) int height, @OriginalArg(7) int dstOffset, @OriginalArg(8) int dstRowStride) {
		@Pc(10) int blockCount = -(width >> 2);
		@Pc(15) int remainder = -(width & 0x3);
		for (@Pc(23) int i = -height; i < 0; i++) {
			@Pc(31) int dstIndex;
			for (@Pc(27) int j = blockCount; j < 0; j++) {
				dstIndex = dstOffset++;
				dst[dstIndex] += src[srcOffset++];
				@Pc(43) int dstIndex2 = dstOffset++;
				dst[dstIndex2] += src[srcOffset++];
				@Pc(55) int dstIndex3 = dstOffset++;
				dst[dstIndex3] += src[srcOffset++];
				@Pc(67) int dstIndex4 = dstOffset++;
				dst[dstIndex4] += src[srcOffset++];
			}
			for (@Pc(82) int k = remainder; k < 0; k++) {
				dstIndex = dstOffset++;
				dst[dstIndex] += src[srcOffset++];
			}
			dstOffset += dstRowStride;
			srcOffset += srcRowStride;
		}
	}

	@OriginalMember(owner = "client!rga", name = "a", descriptor = "(IIBIII[B)Z")
	public static boolean containsUnshadowedBlock(@OriginalArg(0) int rowStride, @OriginalArg(1) int height, @OriginalArg(4) int width, @OriginalArg(5) int offset, @OriginalArg(6) byte[] data) {
		@Pc(9) int remainder = width % 8;
		@Pc(26) int padding;
		if (remainder == 0) {
			padding = 0;
		} else {
			padding = 8 - remainder;
		}
		@Pc(40) int rowBlocks = -((height + 8 - 1) / 8);
		@Pc(49) int columnBlocks = -((width + 8 - 1) / 8);
		for (@Pc(51) int i = rowBlocks; i < 0; i++) {
			for (@Pc(55) int j = columnBlocks; j < 0; j++) {
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
}
