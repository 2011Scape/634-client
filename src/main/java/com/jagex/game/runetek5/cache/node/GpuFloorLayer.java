package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.NativeMatrix;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.config.material.MaterialType;
import jaclib.memory.Buffer;
import jaclib.memory.Stream;
import jaclib.memory.heap.NativeHeapBuffer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.scene.ground.GpuFloorModel;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.graphics.GpuIndexBuffer;
import com.jagex.game.runetek5.toolkit.companion.NativeStream;
import com.jagex.game.runetek5.PrimitiveType;
import com.jagex.game.runetek5.TextureTransformMode;

@OriginalClass("client!eu")
public final class GpuFloorLayer extends Node {

	@OriginalMember(owner = "client!eu", name = "u", descriptor = "Lclient!jaclib/memory/heap/NativeHeapBuffer;")
	private NativeHeapBuffer colourHeapBuffer;

	@OriginalMember(owner = "client!eu", name = "w", descriptor = "Lclient!jaclib/memory/Stream;")
	private Stream colourWriter;

	@OriginalMember(owner = "client!eu", name = "z", descriptor = "Lclient!tc;")
	private NativeStream colourStream;

	@OriginalMember(owner = "client!eu", name = "M", descriptor = "I")
	public int triangleCount = 0;

	@OriginalMember(owner = "client!eu", name = "o", descriptor = "Lclient!qm;")
	private final GpuFloorModel gpuFloorModel;

	@OriginalMember(owner = "client!eu", name = "r", descriptor = "I")
	public final int waterIntensity;

	@OriginalMember(owner = "client!eu", name = "E", descriptor = "F")
	public final float textureScale;

	@OriginalMember(owner = "client!eu", name = "N", descriptor = "Lclient!wga;")
	private final AbstractNativeToolkit nativeToolkit;

	@OriginalMember(owner = "client!eu", name = "x", descriptor = "I")
	public final int waterDepth;

	@OriginalMember(owner = "client!eu", name = "p", descriptor = "I")
	public final int materialId;

	@OriginalMember(owner = "client!eu", name = "v", descriptor = "[I")
	private final int[] tileTriangleMasks;

	@OriginalMember(owner = "client!eu", name = "t", descriptor = "I")
	public final int waterFogColour;

	@OriginalMember(owner = "client!eu", name = "<init>", descriptor = "(Lclient!qm;IIIII)V")
	public GpuFloorLayer(@OriginalArg(0) GpuFloorModel gpuFloorModel, @OriginalArg(1) int materialId, @OriginalArg(2) int textureScale, @OriginalArg(3) int waterFogColour, @OriginalArg(4) int waterDepth, @OriginalArg(5) int waterIntensity) {
		this.gpuFloorModel = gpuFloorModel;
		this.waterIntensity = waterIntensity;
		this.textureScale = textureScale;
		this.nativeToolkit = this.gpuFloorModel.nativeToolkit;
		this.waterDepth = waterDepth;
		this.materialId = materialId;
		this.tileTriangleMasks = new int[this.gpuFloorModel.length * this.gpuFloorModel.width];
		this.waterFogColour = waterFogColour;
	}

	@OriginalMember(owner = "client!eu", name = "a", descriptor = "(BI)V")
	public void initColours(@OriginalArg(1) int vertexCount) {
		this.colourHeapBuffer = this.nativeToolkit.generateNativeHeapBuffer(true, vertexCount * 4);
		this.colourWriter = new Stream(this.colourHeapBuffer, 0, vertexCount * 4);
	}

	@OriginalMember(owner = "client!eu", name = "a", descriptor = "(B[II)V")
	public void render(@OriginalArg(1) int[] tileIndices, @OriginalArg(2) int tileCount) {
		@Pc(14) GpuIndexBuffer gpuIndexBuffer = this.nativeToolkit.getOrResizeIndexBuffer(this.triangleCount * 3);
		@Pc(19) Buffer buffer = gpuIndexBuffer.lock();
		if (buffer == null) {
			return;
		}
		@Pc(35) Stream stream = this.nativeToolkit.wrapBuffer(buffer);
		@Pc(37) int drawnTriangles = 0;
		@Pc(39) int minIndex = 32767;
		@Pc(41) int maxIndex = -32768;
		@Pc(100) int vertexIndex;
		@Pc(57) short[] vertexIndices;
		@Pc(51) int tileIndex;
		@Pc(62) int mask;
		@Pc(45) int i;
		@Pc(71) int triangle;
		@Pc(73) int pos;
		@Pc(91) int corner;
		if (Stream.c()) {
			label118: for (i = 0; i < tileCount; i++) {
				tileIndex = tileIndices[i];
				vertexIndices = this.gpuFloorModel.tileVertexIndices[tileIndex];
				mask = this.tileTriangleMasks[tileIndex];
				if (mask != 0 && vertexIndices != null) {
					triangle = 0;
					pos = 0;
					while (true) {
						while (true) {
							if (vertexIndices.length <= pos) {
								continue label118;
							}
							if ((mask & 0x1 << triangle++) == 0) {
								pos += 3;
							} else {
								drawnTriangles++;
								for (corner = 0; corner < 3; corner++) {
									vertexIndex = vertexIndices[pos++] & 0xFFFF;
									if (minIndex > vertexIndex) {
										minIndex = vertexIndex;
									}
									stream.d(vertexIndex);
									if (vertexIndex > maxIndex) {
										maxIndex = vertexIndex;
									}
								}
							}
						}
					}
				}
			}
		} else {
			label94: for (i = 0; i < tileCount; i++) {
				tileIndex = tileIndices[i];
				vertexIndices = this.gpuFloorModel.tileVertexIndices[tileIndex];
				mask = this.tileTriangleMasks[tileIndex];
				if (mask != 0 && vertexIndices != null) {
					triangle = 0;
					pos = 0;
					while (true) {
						while (true) {
							if (pos >= vertexIndices.length) {
								continue label94;
							}
							if ((0x1 << triangle++ & mask) == 0) {
								pos += 3;
							} else {
								drawnTriangles++;
								for (corner = 0; corner < 3; corner++) {
									vertexIndex = vertexIndices[pos++] & 0xFFFF;
									if (vertexIndex < minIndex) {
										minIndex = vertexIndex;
									}
									stream.a(vertexIndex);
									if (maxIndex < vertexIndex) {
										maxIndex = vertexIndex;
									}
								}
							}
						}
					}
				}
			}
		}
		stream.a();
		if (!gpuIndexBuffer.unlock() || drawnTriangles <= 0) {
			return;
		}
		this.nativeToolkit.setActiveMaterial((this.gpuFloorModel.featureFlags & 0x8) != 0, (this.gpuFloorModel.featureFlags & 0x7) != 0, this.materialId);
		if (this.nativeToolkit.missingWaterColor) {
			this.nativeToolkit.EA(Integer.MAX_VALUE, this.waterFogColour, this.waterDepth, this.waterIntensity);
		}
		@Pc(296) NativeMatrix textureMatrix = this.nativeToolkit.getCurrentTextureMatrix();
		textureMatrix.setScale(1.0F, 1.0F / this.textureScale, 1.0F / this.textureScale);
		this.nativeToolkit.setTextureState(TextureTransformMode.aClass332_6);
		this.nativeToolkit.setStreamSource(this.colourStream, 1);
		this.nativeToolkit.setVertexDeclaration(this.gpuFloorModel.vertexDeclaration);
		this.nativeToolkit.drawIndexedPrimitive(PrimitiveType.aClass134_6, drawnTriangles, gpuIndexBuffer, minIndex, 0, maxIndex + 1 - minIndex);
		this.nativeToolkit.resetCurrentTextureMatrix();
	}

	@OriginalMember(owner = "client!eu", name = "c", descriptor = "(II)V")
	public void finishColours(@OriginalArg(1) int vertexCount) {
		this.colourWriter.a();
		this.colourStream = this.nativeToolkit.createStream(false);
		this.colourStream.createFromSource(this.colourHeapBuffer, 4, vertexCount * 4);
		this.colourHeapBuffer = null;
		this.colourWriter = null;
	}

	@OriginalMember(owner = "client!eu", name = "a", descriptor = "(IFIII)V")
	public void setVertexColour(@OriginalArg(0) int vertex, @OriginalArg(1) float brightness, @OriginalArg(3) int lightness, @OriginalArg(4) int colour) {
		@Pc(19) int local19;
		@Pc(35) int local35;
		if (this.materialId != -1) {
			@Pc(14) MaterialType textureMetrics = this.nativeToolkit.materialProvider.getMetrics(this.materialId);
			int alpha = textureMetrics.alpha & 0xFF;
			@Pc(60) int local60;
			if (alpha != 0 && textureMetrics.effectType != 4) {
				int lightnessColour;
				if (lightness < 0) {
					lightnessColour = 0;
				} else if (lightness <= 127) {
					lightnessColour = lightness * 131586;
				} else {
					lightnessColour = 16777215;
				}
				if (alpha == 256) {
					colour = lightnessColour;
				} else {
					int inverseAlpha = 256 - alpha;
					colour = (inverseAlpha * (colour & 0xFF00) + alpha * (lightnessColour & 0xFF00) & 0xFF0000) + (alpha * (lightnessColour & 0xFF00FF) + ((colour & 0xFF00FF) * inverseAlpha) & 0xFF00FF00) >> 8;
				}
			}
			int materialBrightness = textureMetrics.brightness & 0xFF;
			if (materialBrightness != 0) {
				materialBrightness += 256;
				@Pc(114) int red = (colour >> 16 & 0xFF) * materialBrightness;
				if (red > 65535) {
					red = 65535;
				}
				int green = (colour >> 8 & 0xFF) * materialBrightness;
				if (green > 65535) {
					green = 65535;
				}
				@Pc(138) int blue = materialBrightness * (colour & 0xFF);
				if (blue > 65535) {
					blue = 65535;
				}
				colour = (blue >> 8) + (green & 0xFF00) + ((red & 0x5400FF00) << 8);
			}
		}
		this.colourWriter.e(vertex * 4);
		if (brightness != 1.0F) {
			@Pc(177) int red = colour >> 16 & 0xFF;
			int green = colour >> 8 & 0xFF;
			red = (int) ((float) red * brightness);
			int blue = colour & 0xFF;
			if (red < 0) {
				red = 0;
			} else if (red > 255) {
				red = 255;
			}
			green = (int) ((float) green * brightness);
			if (green < 0) {
				green = 0;
			} else if (green > 255) {
				green = 255;
			}
			blue = (int) ((float) blue * brightness);
			if (blue < 0) {
				blue = 0;
			} else if (blue > 255) {
				blue = 255;
			}
			colour = blue | red << 16 | green << 8;
		}
		if (this.nativeToolkit.endiannessType == 0) {
			this.colourWriter.f((byte) colour);
			this.colourWriter.f((byte) (colour >> 8));
			this.colourWriter.f((byte) (colour >> 16));
		} else {
			this.colourWriter.f((byte) (colour >> 16));
			this.colourWriter.f((byte) (colour >> 8));
			this.colourWriter.f((byte) colour);
		}
	}

	@OriginalMember(owner = "client!eu", name = "a", descriptor = "(IIIB)V")
	public void addTriangle(@OriginalArg(0) int triangle, @OriginalArg(1) int tileZ, @OriginalArg(2) int tileX) {
		this.tileTriangleMasks[tileZ * this.gpuFloorModel.width + tileX] |= 0x1 << triangle;
		this.triangleCount++;
	}

	@OriginalMember(owner = "client!eu", name = "b", descriptor = "(BI)V")
	public void setVertexOpaque(@OriginalArg(1) int vertex) {
		this.colourWriter.e(vertex * 4 + 3);
		this.colourWriter.f(-1);
	}
}
