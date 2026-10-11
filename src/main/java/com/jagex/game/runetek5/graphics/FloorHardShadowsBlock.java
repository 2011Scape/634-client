package com.jagex.game.runetek5.graphics;

import jaclib.memory.Buffer;
import jaclib.memory.Stream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.scene.ground.GpuFloorModel;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.toolkit.opengl.GLActionFormat;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;
import com.jagex.game.runetek5.PrimitiveType;

@OriginalClass("client!mc")
public final class FloorHardShadowsBlock {

	@OriginalMember(owner = "client!mc", name = "o", descriptor = "I")
	private int lastModifiedPixelCount = -1;

	@OriginalMember(owner = "client!mc", name = "s", descriptor = "Z")
	public boolean needsTextureRegeneration = true;

	@OriginalMember(owner = "client!mc", name = "r", descriptor = "Lclient!wga;")
	private final AbstractNativeToolkit nativeToolkit;

	@OriginalMember(owner = "client!mc", name = "a", descriptor = "I")
	private final int offsetX;

	@OriginalMember(owner = "client!mc", name = "p", descriptor = "I")
	private final int offsetZ;

	@OriginalMember(owner = "client!mc", name = "q", descriptor = "Lclient!dg;")
	private final FloorHardShadows floorHardShadows;

	@OriginalMember(owner = "client!mc", name = "t", descriptor = "I")
	public final int triangleCount;

	@OriginalMember(owner = "client!mc", name = "m", descriptor = "Lclient!wfa;")
	private GpuTexture cachedTexture;

	@OriginalMember(owner = "client!mc", name = "c", descriptor = "I")
	private int maxIndex;

	@OriginalMember(owner = "client!mc", name = "d", descriptor = "I")
	private int minIndex;

	@OriginalMember(owner = "client!mc", name = "h", descriptor = "Lclient!uq;")
	private GpuIndexBuffer indexBuffer;

	@OriginalMember(owner = "client!mc", name = "<init>", descriptor = "(Lclient!wga;Lclient!dg;Lclient!qm;IIIII)V")
	public FloorHardShadowsBlock(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) FloorHardShadows floorHardShadows, @OriginalArg(2) GpuFloorModel gpuFloorModel, @OriginalArg(3) int chunkX, @OriginalArg(4) int chunkZ, @OriginalArg(5) int sizeShift, @OriginalArg(6) int offsetX, @OriginalArg(7) int offsetZ) {
		this.nativeToolkit = nativeToolkit;
		this.offsetX = offsetX;
		this.offsetZ = offsetZ;
		this.floorHardShadows = floorHardShadows;
		@Pc(25) int chunkSize = 0x1 << sizeShift;
		@Pc(27) int indexCount = 0;
		@Pc(31) int chunkStartX = chunkX << sizeShift;
		@Pc(35) int chunkStartZ = chunkZ << sizeShift;
		@Pc(48) int local48;
		for (@Pc(37) int i = 0; i < chunkSize; i++) {
			int tileOffset = chunkStartX + (i + chunkStartZ) * gpuFloorModel.width;
			for (@Pc(50) int j = 0; j < chunkSize; j++) {
				@Pc(58) short[] tileIndices = gpuFloorModel.tileVertexIndices[tileOffset++];
				if (tileIndices != null) {
					indexCount += tileIndices.length;
				}
			}
		}
		if (indexCount <= 0) {
			this.triangleCount = 0;
			this.cachedTexture = null;
		} else {
			this.maxIndex = Integer.MIN_VALUE;
			this.minIndex = Integer.MAX_VALUE;
			this.indexBuffer = this.nativeToolkit.createIndexBuffer(false);
			this.indexBuffer.allocate(indexCount);
			for (int attempt = 0; attempt < 4; attempt++) {
				@Pc(110) Buffer buffer = this.indexBuffer.lock();
				if (buffer != null) {
					@Pc(118) Stream stream = this.nativeToolkit.wrapBuffer(buffer);
					@Pc(122) int dz;
					@Pc(134) int tileOffset;
					@Pc(136) int dx;
					@Pc(144) short[] tileIndices;
					@Pc(148) int index;
					@Pc(156) int vertexIndex;
					if (Stream.c()) {
						for (dz = 0; dz < chunkSize; dz++) {
							tileOffset = gpuFloorModel.width * (dz + chunkStartZ) + chunkStartX;
							for (dx = 0; dx < chunkSize; dx++) {
								tileIndices = gpuFloorModel.tileVertexIndices[tileOffset++];
								if (tileIndices != null) {
									for (index = 0; index < tileIndices.length; index++) {
										vertexIndex = tileIndices[index] & 0xFFFF;
										if (this.maxIndex < vertexIndex) {
											this.maxIndex = vertexIndex;
										}
										if (vertexIndex < this.minIndex) {
											this.minIndex = vertexIndex;
										}
										stream.d(vertexIndex);
									}
								}
							}
						}
					} else {
						for (dz = 0; dz < chunkSize; dz++) {
							tileOffset = gpuFloorModel.width * (chunkStartZ + dz) + chunkStartX;
							for (dx = 0; dx < chunkSize; dx++) {
								tileIndices = gpuFloorModel.tileVertexIndices[tileOffset++];
								if (tileIndices != null) {
									for (index = 0; index < tileIndices.length; index++) {
										vertexIndex = tileIndices[index] & 0xFFFF;
										if (vertexIndex < this.minIndex) {
											this.minIndex = vertexIndex;
										}
										if (vertexIndex > this.maxIndex) {
											this.maxIndex = vertexIndex;
										}
										stream.a(vertexIndex);
									}
								}
							}
						}
					}
					stream.a();
					if (this.indexBuffer.unlock()) {
						break;
					}
				}
			}
			this.triangleCount = indexCount / 3;
		}
	}

	@OriginalMember(owner = "client!mc", name = "a", descriptor = "(B)V")
	public void drawShadow() {
		this.drawShadowElements(this.indexBuffer, this.triangleCount);
	}

	@OriginalMember(owner = "client!mc", name = "a", descriptor = "(I)V")
	private void updateTexture() {
		if (!this.needsTextureRegeneration) {
			return;
		}
		this.needsTextureRegeneration = false;
		@Pc(16) byte[] shadowData = this.floorHardShadows.shadowMapperData;
		@Pc(18) int modifiedPixelCount = 0;
		@Pc(22) int shadowWidth = this.floorHardShadows.width;
		@Pc(32) int shadowOffset = this.floorHardShadows.width * this.offsetZ + this.offsetX;
		@Pc(44) int local44;
		for (@Pc(34) int i = -128; i < 0; i++) {
			modifiedPixelCount = (modifiedPixelCount << 8) - modifiedPixelCount;
			for (int column = -128; column < 0; column++) {
				if (shadowData[shadowOffset++] != 0) {
					modifiedPixelCount++;
				}
			}
			shadowOffset += shadowWidth - 128;
		}
		if (this.cachedTexture != null && this.lastModifiedPixelCount == modifiedPixelCount) {
			this.needsTextureRegeneration = false;
			return;
		}
		this.lastModifiedPixelCount = modifiedPixelCount;
		int pixelIndex = 0;
		shadowOffset = this.offsetX + this.offsetZ * shadowWidth;
		@Pc(120) int y;
		@Pc(124) int x;
		@Pc(135) int neighborCount;
		if (!this.nativeToolkit.supportsTextureFormat(GLFormat.RGBA, GLActionFormat.PIXEL_FORMAT)) {
			if (FloorHardShadowsBlock.shadowPixels == null) {
				FloorHardShadowsBlock.shadowPixels = new int[16384];
			}
			@Pc(254) int[] pixels = FloorHardShadowsBlock.shadowPixels;
			for (y = -128; y < 0; y++) {
				for (x = -128; x < 0; x++) {
					if (shadowData[shadowOffset] == 0) {
						neighborCount = 0;
						if (shadowData[shadowOffset - 1] != 0) {
							neighborCount++;
						}
						if (shadowData[shadowOffset + 1] != 0) {
							neighborCount++;
						}
						if (shadowData[shadowOffset - shadowWidth] != 0) {
							neighborCount++;
						}
						if (shadowData[shadowWidth + shadowOffset] != 0) {
							neighborCount++;
						}
						pixels[pixelIndex++] = neighborCount * 17 << 24;
					} else {
						pixels[pixelIndex++] = 1140850688;
					}
					shadowOffset++;
				}
				shadowOffset += this.floorHardShadows.width - 128;
			}
			if (this.cachedTexture == null) {
				this.cachedTexture = this.nativeToolkit.createTexture(128, 128, false, FloorHardShadowsBlock.shadowPixels);
				this.cachedTexture.setWarp(false, false);
			} else {
				this.cachedTexture.updateTextureRegion(128, FloorHardShadowsBlock.shadowPixels, 0, 128, 0, 128);
			}
			return;
		}
		if (FloorHardShadowsBlock.shadowTexels == null) {
			FloorHardShadowsBlock.shadowTexels = new byte[16384];
		}
		@Pc(118) byte[] texels = FloorHardShadowsBlock.shadowTexels;
		for (y = -128; y < 0; y++) {
			for (x = -128; x < 0; x++) {
				if (shadowData[shadowOffset] == 0) {
					neighborCount = 0;
					if (shadowData[shadowOffset - 1] != 0) {
						neighborCount++;
					}
					if (shadowData[shadowOffset + 1] != 0) {
						neighborCount++;
					}
					if (shadowData[shadowOffset - shadowWidth] != 0) {
						neighborCount++;
					}
					if (shadowData[shadowWidth + shadowOffset] != 0) {
						neighborCount++;
					}
					texels[pixelIndex++] = (byte) (neighborCount * 17);
				} else {
					texels[pixelIndex++] = 68;
				}
				shadowOffset++;
			}
			shadowOffset += this.floorHardShadows.width - 128;
		}
		if (this.cachedTexture == null) {
			this.cachedTexture = this.nativeToolkit.createTextureSampler(FloorHardShadowsBlock.shadowTexels, 128, GLActionFormat.PIXEL_FORMAT, false, 128);
			this.cachedTexture.setWarp(false, false);
		} else {
			this.cachedTexture.upload(128, 128, 128, FloorHardShadowsBlock.shadowTexels, GLActionFormat.PIXEL_FORMAT);
		}
	}

	@OriginalMember(owner = "client!mc", name = "a", descriptor = "(ILclient!uq;I)V")
	public void drawShadowElements(@OriginalArg(1) GpuIndexBuffer gpuIndexBuffer, @OriginalArg(2) int triangleCount) {
		if (triangleCount > 0) {
			this.updateTexture();
			this.nativeToolkit.setTextureSampler(this.cachedTexture);
			this.nativeToolkit.drawIndexedPrimitive(PrimitiveType.aClass134_6, triangleCount, gpuIndexBuffer, this.minIndex, 0, this.maxIndex + 1 - this.minIndex);
		}
	}
	@OriginalMember(owner = "client!ig", name = "v", descriptor = "[I")
	public static int[] shadowPixels;
	@OriginalMember(owner = "client!or", name = "m", descriptor = "[B")
	public static byte[] shadowTexels;
}
