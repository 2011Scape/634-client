package com.jagex.game.runetek5.graphics.shadow;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.buffer.ElementsBufferGL;
import com.jagex.game.runetek5.scene.ground.GlGround;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlElementsArrayBuffer;
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture;
import com.jagex.game.runetek5.graphics.gl.GlShadowMap;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!sm")
public final class ShadowTileGL {

	@OriginalMember(owner = "client!sm", name = "l", descriptor = "Z")
	public boolean needsTextureRegeneration = true;

	@OriginalMember(owner = "client!sm", name = "m", descriptor = "I")
	private int lastModifiedPixelCount = -1;

	@OriginalMember(owner = "client!sm", name = "c", descriptor = "Lclient!qo;")
	private final GlToolkit toolkit;

	@OriginalMember(owner = "client!sm", name = "b", descriptor = "Lclient!mha;")
	private final GlShadowMap shadowMapper;

	@OriginalMember(owner = "client!sm", name = "f", descriptor = "I")
	private final int tileLength;

	@OriginalMember(owner = "client!sm", name = "e", descriptor = "I")
	private final int tileWidth;

	@OriginalMember(owner = "client!sm", name = "k", descriptor = "I")
	private final int materialCount;

	@OriginalMember(owner = "client!sm", name = "a", descriptor = "Lclient!tp;")
	private OpenGl2DTexture cachedTexture;

	@OriginalMember(owner = "client!sm", name = "d", descriptor = "Lclient!ni;")
	private ElementsBufferGL elementsBuffer;

	@OriginalMember(owner = "client!sm", name = "n", descriptor = "Lclient!wv;")
	private GlElementsArrayBuffer indexBuffer;

	@OriginalMember(owner = "client!sm", name = "<init>", descriptor = "(Lclient!qo;Lclient!mha;Lclient!ed;IIIII)V")
	public ShadowTileGL(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) GlShadowMap glShadowMap, @OriginalArg(2) GlGround glGround, @OriginalArg(3) int chunkX, @OriginalArg(4) int chunkY, @OriginalArg(5) int sizeShift, @OriginalArg(6) int tileLength, @OriginalArg(7) int tileWidth) {
		this.toolkit = glToolkit;
		this.shadowMapper = glShadowMap;
		this.tileLength = tileLength;
		this.tileWidth = tileWidth;
		@Pc(25) int chunkSize = 0x1 << sizeShift;
		@Pc(27) int materialCount = 0;
		@Pc(31) int chunkStartX = chunkX << sizeShift;
		@Pc(35) int chunkStartY = chunkY << sizeShift;
		@Pc(52) int local52;
		for (@Pc(37) int i = 0; i < chunkSize; i++) {
			@Pc(50) int materialOffset = glGround.width * (chunkStartY + i) + chunkStartX;
			for (int column = 0; column < chunkSize; column++) {
				@Pc(60) short[] materialIndices = glGround.tileVertexIndices[materialOffset++];
				if (materialIndices != null) {
					materialCount += materialIndices.length;
				}
			}
		}
		this.materialCount = materialCount;
		if (materialCount <= 0) {
			this.cachedTexture = null;
		} else {
			@Pc(99) Packet packet = new Packet(materialCount * 2);
			@Pc(120) int dx;
			@Pc(128) short[] materialIndices;
			@Pc(132) int matIdx;
			@Pc(118) int materialOffset;
			if (this.toolkit.bigEndianess) {
				for (int row = 0; row < chunkSize; row++) {
					materialOffset = chunkStartX + (row + chunkStartY) * glGround.width;
					for (dx = 0; dx < chunkSize; dx++) {
						materialIndices = glGround.tileVertexIndices[materialOffset++];
						if (materialIndices != null) {
							for (matIdx = 0; matIdx < materialIndices.length; matIdx++) {
								packet.writeShort(materialIndices[matIdx] & 0xFFFF);
							}
						}
					}
				}
			} else {
				for (int row = 0; row < chunkSize; row++) {
					materialOffset = (chunkStartY + row) * glGround.width + chunkStartX;
					for (dx = 0; dx < chunkSize; dx++) {
						materialIndices = glGround.tileVertexIndices[materialOffset++];
						if (materialIndices != null) {
							for (matIdx = 0; matIdx < materialIndices.length; matIdx++) {
								packet.writeShortLE(materialIndices[matIdx] & 0xFFFF);
							}
						}
					}
				}
			}
			this.elementsBuffer = this.toolkit.createElementsArrayBuffer(packet.currentPosition, packet.buffer, false);
			this.indexBuffer = new GlElementsArrayBuffer(this.toolkit, 5123, null, 1);
		}
	}

	@OriginalMember(owner = "client!sm", name = "a", descriptor = "([BIIB)V")
	public void drawShadowWithIndices(@OriginalArg(0) byte[] data, @OriginalArg(2) int indexCount) {
		this.indexBuffer.putElements(data, this.toolkit.getDataTypeSize(5123) * indexCount);
		this.drawShadowElements(this.indexBuffer, indexCount);
	}

	@OriginalMember(owner = "client!sm", name = "a", descriptor = "(B)V")
	public void drawShadow() {
		this.drawShadowElements(this.elementsBuffer, this.materialCount);
	}

	@OriginalMember(owner = "client!sm", name = "a", descriptor = "(Lclient!ni;II)V")
	private void drawShadowElements(@OriginalArg(0) ElementsBufferGL glBinding, @OriginalArg(2) int indexCount) {
		if (indexCount != 0) {
			this.updateTexture();
			this.toolkit.setActiveTexture(this.cachedTexture);
			this.toolkit.drawElements(indexCount, glBinding, 0);
		}
	}

	@OriginalMember(owner = "client!sm", name = "b", descriptor = "(B)V")
	private void updateTexture() {
		if (!this.needsTextureRegeneration) {
			return;
		}
		this.needsTextureRegeneration = false;
		@Pc(16) byte[] shadowMapperData = this.shadowMapper.shadowMapperData;
		@Pc(20) byte[] shadowTextureData = this.toolkit.shadowTextureData;
		@Pc(26) int modifiedPixelCount = 0;
		@Pc(30) int shadowMapperWidth = this.shadowMapper.width;
		@Pc(41) int shadowOffset = this.tileLength + this.tileWidth * this.shadowMapper.width;
		@Pc(54) int local54;
		for (@Pc(43) int i = -128; i < 0; i++) {
			modifiedPixelCount = (modifiedPixelCount << 8) - modifiedPixelCount;
			for (int column = -128; column < 0; column++) {
				if (shadowMapperData[shadowOffset++] != 0) {
					modifiedPixelCount++;
				}
			}
			shadowOffset += shadowMapperWidth - 128;
		}
		if (this.cachedTexture != null && modifiedPixelCount == this.lastModifiedPixelCount) {
			this.needsTextureRegeneration = false;
			return;
		}
		this.lastModifiedPixelCount = modifiedPixelCount;
		shadowOffset = this.tileLength + shadowMapperWidth * this.tileWidth;
		int texelIndex = 0;
		for (@Pc(104) int j = -128; j < 0; j++) {
			for (@Pc(108) int k = -128; k < 0; k++) {
				if (shadowMapperData[shadowOffset] == 0) {
					@Pc(126) int neighborCount = 0;
					if (shadowMapperData[shadowOffset - 1] != 0) {
						neighborCount++;
					}
					if (shadowMapperData[shadowOffset + 1] != 0) {
						neighborCount++;
					}
					if (shadowMapperData[shadowOffset - shadowMapperWidth] != 0) {
						neighborCount++;
					}
					if (shadowMapperData[shadowMapperWidth + shadowOffset] != 0) {
						neighborCount++;
					}
					shadowTextureData[texelIndex++] = (byte) (neighborCount * 17);
				} else {
					shadowTextureData[texelIndex++] = 68;
				}
				shadowOffset++;
			}
			shadowOffset += this.shadowMapper.width - 128;
		}
		if (this.cachedTexture == null) {
			this.cachedTexture = new OpenGl2DTexture(this.toolkit, 3553, 6406, 128, 128, false, this.toolkit.shadowTextureData, 6406, false);
			this.cachedTexture.setRepeat(false, false);
			this.cachedTexture.setLinearFilter(true);
		} else {
			this.cachedTexture.uploadTextureData(128, false, 128, 6406, this.toolkit.shadowTextureData);
		}
	}
}
