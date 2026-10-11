package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.scene.ground.GpuFloorModel;
import com.jagex.game.runetek5.graphics.GpuIndexBuffer;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.toolkit.companion.NativeStream;
import jaclib.memory.Buffer;
import jaclib.memory.Stream;
import jaclib.memory.heap.NativeHeapBuffer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.impl.ShortNode;
import com.jagex.game.runetek5.PrimitiveType;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!sea")
public final class GpuFloorLight extends Node {

	@OriginalMember(owner = "client!sea", name = "B", descriptor = "I")
	private int vertexCount;

	@OriginalMember(owner = "client!sea", name = "v", descriptor = "Lclient!wga;")
	private final AbstractNativeToolkit nativeToolkit;

	@OriginalMember(owner = "client!sea", name = "I", descriptor = "Lclient!qm;")
	private final GpuFloorModel gpuFloorModel;

	@OriginalMember(owner = "client!sea", name = "s", descriptor = "Lclient!ab;")
	private final PointLight pointLight;

	@OriginalMember(owner = "client!sea", name = "M", descriptor = "I")
	private final int minTileX;

	@OriginalMember(owner = "client!sea", name = "F", descriptor = "I")
	private final int maxTileX;

	@OriginalMember(owner = "client!sea", name = "C", descriptor = "I")
	private final int minTileZ;

	@OriginalMember(owner = "client!sea", name = "y", descriptor = "I")
	private final int maxTileZ;

	@OriginalMember(owner = "client!sea", name = "t", descriptor = "[[F")
	private float[][] normalsX;

	@OriginalMember(owner = "client!sea", name = "E", descriptor = "[[F")
	private float[][] normalsY;

	@OriginalMember(owner = "client!sea", name = "p", descriptor = "[[F")
	private float[][] normalsZ;

	@OriginalMember(owner = "client!sea", name = "H", descriptor = "I")
	private int indexCount;

	@OriginalMember(owner = "client!sea", name = "z", descriptor = "Lclient!eq;")
	private IterableHashTable vertexTable;

	@OriginalMember(owner = "client!sea", name = "q", descriptor = "Lclient!uq;")
	private final GpuIndexBuffer indexBuffer;

	@OriginalMember(owner = "client!sea", name = "L", descriptor = "Lclient!jaclib/memory/Stream;")
	private Stream vertexWriter;

	@OriginalMember(owner = "client!sea", name = "K", descriptor = "Lclient!jaclib/memory/Stream;")
	private Stream indexWriter;

	@OriginalMember(owner = "client!sea", name = "J", descriptor = "Lclient!tc;")
	private final NativeStream vertexStream;

	@OriginalMember(owner = "client!sea", name = "<init>", descriptor = "(Lclient!wga;Lclient!qm;Lclient!ab;[I)V")
	public GpuFloorLight(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) GpuFloorModel gpuFloorModel, @OriginalArg(2) PointLight pointLight, @OriginalArg(3) int[] tileMask) {
		this.nativeToolkit = nativeToolkit;
		this.gpuFloorModel = gpuFloorModel;
		this.pointLight = pointLight;
		@Pc(23) int extent = this.pointLight.getRadius() - (gpuFloorModel.tileUnits >> 1);
		this.minTileX = this.pointLight.getX() - extent >> gpuFloorModel.tileScale;
		this.maxTileX = this.pointLight.getX() + extent >> gpuFloorModel.tileScale;
		this.minTileZ = this.pointLight.getZ() - extent >> gpuFloorModel.tileScale;
		this.maxTileZ = this.pointLight.getZ() + extent >> gpuFloorModel.tileScale;
		@Pc(77) int spanX = this.maxTileX + 1 - this.minTileX;
		@Pc(86) int spanZ = this.maxTileZ + 1 - this.minTileZ;
		this.normalsX = new float[spanX + 1][spanZ + 1];
		this.normalsY = new float[spanX + 1][spanZ + 1];
		this.normalsZ = new float[spanX + 1][spanZ + 1];
		@Pc(122) int local122;
		@Pc(137) int local137;
		@Pc(144) int tileX;
		@Pc(172) int local172;
		@Pc(189) int local189;
		for (@Pc(115) int i = 0; i <= spanZ; i++) {
			int tileZ = this.minTileZ + i;
			if (tileZ > 0 && tileZ < this.gpuFloorModel.length - 1) {
				for (int localTileX = 0; localTileX <= spanX; localTileX++) {
					tileX = this.minTileX + localTileX;
					if (tileX > 0 && tileX < this.gpuFloorModel.width - 1) {
						int heightDeltaX = gpuFloorModel.getHeight(tileZ, tileX + 1) - gpuFloorModel.getHeight(tileZ, tileX - 1);
						int heightDeltaZ = gpuFloorModel.getHeight(tileZ + 1, tileX) - gpuFloorModel.getHeight(tileZ - 1, tileX);
						@Pc(204) float invLength = (float) (1.0D / Math.sqrt((double) (heightDeltaZ * heightDeltaZ + heightDeltaX * heightDeltaX + 65536)));
						this.normalsX[localTileX][i] = (float) heightDeltaX * invLength;
						this.normalsY[localTileX][i] = -256.0F * invLength;
						this.normalsZ[localTileX][i] = (float) heightDeltaZ * invLength;
					}
				}
			}
		}
		int maskIndex = 0;
		@Pc(299) int local299;
		for (int tileZ = this.minTileZ; tileZ <= this.maxTileZ; tileZ++) {
			if (tileZ >= 0 && gpuFloorModel.length > tileZ) {
				for (tileX = this.minTileX; tileX <= this.maxTileX; tileX++) {
					if (tileX >= 0 && gpuFloorModel.width > tileX) {
						int mask = tileMask[maskIndex];
						@Pc(290) int[] colours = gpuFloorModel.tileColours[tileX][tileZ];
						if (colours != null && mask != 0) {
							if (mask == 1) {
								int colourIndex = 0;
								while (colours.length > colourIndex) {
									if (colours[colourIndex++] != -1 && colours[colourIndex++] != -1 && colours[colourIndex++] != -1) {
										this.indexCount += 3;
									}
								}
							} else {
								this.indexCount += 3;
							}
						}
					}
					maskIndex++;
				}
			} else {
				maskIndex += this.maxTileX - this.minTileX;
			}
		}
		if (this.indexCount > 0) {
			this.vertexTable = new IterableHashTable(Utils.nextPowerOfTwo(this.indexCount));
			this.indexBuffer = this.nativeToolkit.createIndexBuffer(false);
			this.indexBuffer.allocate(this.indexCount);
			@Pc(403) NativeHeapBuffer nativeHeapBuffer = this.nativeToolkit.generateNativeHeapBuffer(false, this.indexCount * 16);
			this.vertexWriter = new Stream(nativeHeapBuffer);
			while (true) {
				@Pc(415) Buffer buffer;
				do {
					buffer = this.indexBuffer.lock();
				} while (buffer == null);
				this.indexWriter = new Stream(buffer);
				maskIndex = 0;
				int localZ = 0;
				for (int tileZ = this.minTileZ; tileZ <= this.maxTileZ; tileZ++) {
					if (tileZ >= 0 && gpuFloorModel.length > tileZ) {
						@Pc(444) int localX = 0;
						for (@Pc(447) int j = this.minTileX; j <= this.maxTileX; j++) {
							if (j >= 0 && gpuFloorModel.width > j) {
								@Pc(466) int mask = tileMask[maskIndex];
								@Pc(473) int[] colours = gpuFloorModel.tileColours[j][tileZ];
								if (colours != null && mask != 0) {
									if (mask == 1) {
										@Pc(631) int[] offsetsX = gpuFloorModel.tileOffsetsX[j][tileZ];
										@Pc(638) int[] offsetsZ = gpuFloorModel.tileOffsetsZ[j][tileZ];
										@Pc(640) int index = 0;
										label119: while (true) {
											while (true) {
												if (index >= colours.length) {
													break label119;
												}
												if (colours[index] == -1 || colours[index + 1] == -1 || colours[index + 2] == -1) {
													index += 3;
												} else {
													this.addVertex(localZ, j, offsetsZ[index], tileZ, offsetsX[index], localX);
													index++;
													this.addVertex(localZ, j, offsetsZ[index], tileZ, offsetsX[index], localX);
													index++;
													this.addVertex(localZ, j, offsetsZ[index], tileZ, offsetsX[index], localX);
													index++;
												}
											}
										}
									} else if (mask == 3) {
										this.addVertex(localZ, j, 0, tileZ, 0, localX);
										this.addVertex(localZ, j, 0, tileZ, gpuFloorModel.tileUnits, localX);
										this.addVertex(localZ, j, gpuFloorModel.tileUnits, tileZ, 0, localX);
									} else if (mask == 2) {
										this.addVertex(localZ, j, 0, tileZ, gpuFloorModel.tileUnits, localX);
										this.addVertex(localZ, j, gpuFloorModel.tileUnits, tileZ, gpuFloorModel.tileUnits, localX);
										this.addVertex(localZ, j, 0, tileZ, 0, localX);
									} else if (mask == 5) {
										this.addVertex(localZ, j, gpuFloorModel.tileUnits, tileZ, gpuFloorModel.tileUnits, localX);
										this.addVertex(localZ, j, gpuFloorModel.tileUnits, tileZ, 0, localX);
										this.addVertex(localZ, j, 0, tileZ, gpuFloorModel.tileUnits, localX);
									} else if (mask == 4) {
										this.addVertex(localZ, j, gpuFloorModel.tileUnits, tileZ, 0, localX);
										this.addVertex(localZ, j, 0, tileZ, 0, localX);
										this.addVertex(localZ, j, gpuFloorModel.tileUnits, tileZ, gpuFloorModel.tileUnits, localX);
									}
								}
							}
							maskIndex++;
							localX++;
						}
					} else {
						maskIndex += this.maxTileX - this.minTileX;
					}
					localZ++;
				}
				this.indexWriter.a();
				if (this.indexBuffer.unlock()) {
					this.vertexWriter.a();
					this.vertexStream = this.nativeToolkit.createStream(false);
					this.vertexStream.createFromSource(nativeHeapBuffer, 16, this.vertexCount * 16);
					break;
				}
				this.vertexWriter.e(0);
				this.vertexTable.clear();
			}
		} else {
			this.vertexStream = null;
			this.indexBuffer = null;
		}
		this.normalsX = this.normalsY = this.normalsZ = null;
		this.vertexWriter = null;
		this.indexWriter = null;
		this.vertexTable = null;
	}

	@OriginalMember(owner = "client!sea", name = "a", descriptor = "(II[[ZZI)V")
	public void render(@OriginalArg(0) int tileZ, @OriginalArg(1) int radius, @OriginalArg(2) boolean[][] visibility, @OriginalArg(4) int tileX) {
		if (this.indexBuffer == null || (this.minTileX > radius + tileX || (tileX - radius > this.maxTileX || (tileZ + radius < this.minTileZ || tileZ - radius > this.maxTileZ)))) {
			return;
		}
		for (@Pc(57) int i = this.minTileZ; i <= this.maxTileZ; i++) {
			for (@Pc(62) int j = this.minTileX; j <= this.maxTileX; j++) {
				@Pc(69) int dx = j - tileX;
				@Pc(74) int dz = i - tileZ;
				if (dx > -radius && dx < radius && dz > -radius && radius > dz && visibility[dx + radius][radius + dz]) {
					this.nativeToolkit.setUniformColourValue((byte) (this.pointLight.getIntensity() * 255.0F));
					this.nativeToolkit.setStreamSource(this.vertexStream, 0);
					this.nativeToolkit.setVertexDeclaration(this.nativeToolkit.positionColorDeclaration);
					this.nativeToolkit.drawIndexedPrimitive(PrimitiveType.aClass134_6, this.indexCount / 3, this.indexBuffer, 0, 0, this.vertexCount);
					return;
				}
			}
		}
	}

	@OriginalMember(owner = "client!sea", name = "a", descriptor = "(IIIIIII)V")
	private void addVertex(@OriginalArg(0) int localZ, @OriginalArg(1) int tileX, @OriginalArg(3) int offsetZ, @OriginalArg(4) int tileZ, @OriginalArg(5) int offsetX, @OriginalArg(6) int localX) {
		@Pc(7) long key = -1L;
		@Pc(15) int x = offsetX + (tileX << this.gpuFloorModel.tileScale);
		@Pc(24) int z = offsetZ + (tileZ << this.gpuFloorModel.tileScale);
		@Pc(31) int y = this.gpuFloorModel.averageHeight(x, z);
		if ((offsetX & 0x7F) == 0 || (offsetZ & 0x7F) == 0) {
			key = ((long) z & 0xFFFFL) << 16 | (long) x & 0xFFFFL;
			@Pc(65) Node node = this.vertexTable.get(key);
			if (node != null) {
				this.writeIndex(((ShortNode) node).value);
				return;
			}
		}
		@Pc(83) short vertexIndex = (short) this.vertexCount++;
		if (key != -1L) {
			this.vertexTable.put(key, new ShortNode(vertexIndex));
		}
		@Pc(116) float normalX;
		@Pc(109) float normalY;
		@Pc(123) float normalZ;
		@Pc(197) float local197;
		@Pc(205) float local205;
		@Pc(212) float local212;
		@Pc(219) float local219;
		@Pc(226) float local226;
		@Pc(235) float local235;
		@Pc(244) float local244;
		@Pc(301) float local301;
		if (offsetX == 0 && offsetZ == 0) {
			normalY = this.normalsY[localX][localZ];
			normalX = this.normalsX[localX][localZ];
			normalZ = this.normalsZ[localX][localZ];
		} else if (offsetX == this.gpuFloorModel.tileUnits && offsetZ == 0) {
			normalY = this.normalsY[localX + 1][localZ];
			normalZ = this.normalsZ[localX + 1][localZ];
			normalX = this.normalsX[localX + 1][localZ];
		} else if (this.gpuFloorModel.tileUnits == offsetX && this.gpuFloorModel.tileUnits == offsetZ) {
			normalX = this.normalsX[localX + 1][localZ + 1];
			normalZ = this.normalsZ[localX + 1][localZ + 1];
			normalY = this.normalsY[localX + 1][localZ + 1];
		} else if (offsetX == 0 && offsetZ == this.gpuFloorModel.tileUnits) {
			normalZ = this.normalsZ[localX][localZ + 1];
			normalY = this.normalsY[localX][localZ + 1];
			normalX = this.normalsX[localX][localZ + 1];
		} else {
			float fracX = (float) offsetX / (float) this.gpuFloorModel.tileUnits;
			float fracZ = (float) offsetZ / (float) this.gpuFloorModel.tileUnits;
			float normalX00 = this.normalsX[localX][localZ];
			float normalY00 = this.normalsY[localX][localZ];
			float normalZ00 = this.normalsZ[localX][localZ];
			float normalX10 = this.normalsX[localX + 1][localZ];
			float normalY10 = this.normalsY[localX + 1][localZ];
			@Pc(259) float normalZ0 = normalZ00 + fracX * (this.normalsZ[localX][localZ + 1] - normalZ00);
			@Pc(274) float normalY0 = normalY00 + (this.normalsY[localX][localZ + 1] - normalY00) * fracX;
			@Pc(292) float normalY1 = normalY10 + fracX * (this.normalsY[localX + 1][localZ + 1] - normalY10);
			float normalZ10 = this.normalsZ[localX + 1][localZ];
			@Pc(316) float normalX0 = normalX00 + (this.normalsX[localX][localZ + 1] - normalX00) * fracX;
			@Pc(333) float normalX1 = normalX10 + fracX * (this.normalsX[localX + 1][localZ + 1] - normalX10);
			normalX = normalX0 + (normalX1 - normalX0) * fracZ;
			normalY = (normalY1 - normalY0) * fracZ + normalY0;
			@Pc(366) float normalZ1 = normalZ10 + fracX * (this.normalsZ[localX + 1][localZ + 1] - normalZ10);
			normalZ = normalZ0 + (normalZ1 - normalZ0) * fracZ;
		}
		float lightDirX = this.pointLight.getX() - x;
		float lightDirY = this.pointLight.getY() - y;
		float lightDirZ = this.pointLight.getZ() - z;
		float distance = (float) Math.sqrt((double) (lightDirZ * lightDirZ + lightDirX * lightDirX + lightDirY * lightDirY));
		float invDistance = 1.0F / distance;
		lightDirX *= invDistance;
		lightDirY *= invDistance;
		lightDirZ *= invDistance;
		float distanceRatio = distance / (float) this.pointLight.getRadius();
		float attenuation = 1.0F - distanceRatio * distanceRatio;
		if (attenuation < 0.0F) {
			attenuation = 0.0F;
		}
		float diffuse = lightDirZ * normalZ + lightDirX * normalX + normalY * lightDirY;
		if (diffuse < 0.0F) {
			diffuse = 0.0F;
		}
		@Pc(545) float lighting = attenuation * diffuse * 2.0F;
		if (lighting > 1.0F) {
			lighting = 1.0F;
		}
		@Pc(556) int colour = this.pointLight.getColour();
		@Pc(566) int red = (int) (lighting * (float) (colour >> 16 & 0xFF));
		if (red > 255) {
			red = 255;
		}
		@Pc(583) int green = (int) (lighting * (float) (colour >> 8 & 0xFF));
		if (green > 255) {
			green = 255;
		}
		@Pc(596) int blue = (int) ((float) (colour & 0xFF) * lighting);
		if (blue > 255) {
			blue = 255;
		}
		if (Stream.c()) {
			this.vertexWriter.a((float) x);
			this.vertexWriter.a((float) y);
			this.vertexWriter.a((float) z);
		} else {
			this.vertexWriter.b((float) x);
			this.vertexWriter.b((float) y);
			this.vertexWriter.b((float) z);
		}
		if (this.nativeToolkit.endiannessType == 0) {
			this.vertexWriter.f(blue);
			this.vertexWriter.f(green);
			this.vertexWriter.f(red);
		} else {
			this.vertexWriter.f(red);
			this.vertexWriter.f(green);
			this.vertexWriter.f(blue);
		}
		this.vertexWriter.f(255);
		this.writeIndex(vertexIndex);
	}

	@OriginalMember(owner = "client!sea", name = "a", descriptor = "(SB)V")
	private void writeIndex(@OriginalArg(0) short index) {
		if (Stream.c()) {
			this.indexWriter.d(index);
		} else {
			this.indexWriter.a(index);
		}
	}
}
