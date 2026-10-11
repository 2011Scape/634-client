package com.jagex.game.runetek5.scene.ground;

import com.jagex.game.runetek5.VertexAttribute;
import com.jagex.game.runetek5.cache.node.lru.GpuShadow;
import com.jagex.game.runetek5.NativeMatrix;
import com.jagex.game.runetek5.cache.node.GpuFloorLight;
import com.jagex.game.runetek5.cache.node.impl.ShortNode;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import jaclib.memory.Buffer;
import jaclib.memory.Stream;
import jaclib.memory.heap.NativeHeapBuffer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.GpuFloorLayer;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent;
import com.jagex.game.runetek5.graphics.FloorHardShadows;
import com.jagex.game.runetek5.graphics.GpuIndexBuffer;
import com.jagex.game.runetek5.toolkit.companion.NativeStream;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration;
import com.jagex.game.runetek5.utils.ColorUtil;
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationAction;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.PrimitiveType;
import com.jagex.game.runetek5.TextureCombineFunction;
import com.jagex.game.runetek5.utils.ArrayUtils;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!qm")
public final class GpuFloorModel extends Ground {

	@OriginalMember(owner = "client!qm", name = "z", descriptor = "I")
	private int indexCount;

	@OriginalMember(owner = "client!qm", name = "B", descriptor = "I")
	private int vertexCount;

	@OriginalMember(owner = "client!qm", name = "M", descriptor = "[[[I")
	private int[][][] tileWaterDepths;

	@OriginalMember(owner = "client!qm", name = "W", descriptor = "Lclient!np;")
	public VertexDeclaration vertexDeclaration;

	@OriginalMember(owner = "client!qm", name = "Y", descriptor = "Lclient!tc;")
	private NativeStream colourStream;

	@OriginalMember(owner = "client!qm", name = "Z", descriptor = "Lclient!tc;")
	private NativeStream vertexStream;

	@OriginalMember(owner = "client!qm", name = "ab", descriptor = "[Lclient!eu;")
	private GpuFloorLayer[] layers;

	@OriginalMember(owner = "client!qm", name = "bb", descriptor = "I")
	private int totalTileVertexCount;

	@OriginalMember(owner = "client!qm", name = "db", descriptor = "I")
	private int maxTileVertexCount;

	@OriginalMember(owner = "client!qm", name = "D", descriptor = "F")
	private float maxHeight = -3.4028235E38F;

	@OriginalMember(owner = "client!qm", name = "E", descriptor = "F")
	private float minHeight = Float.MAX_VALUE;

	@OriginalMember(owner = "client!qm", name = "k", descriptor = "Lclient!uh;")
	private final Deque lights = new Deque();

	@OriginalMember(owner = "client!qm", name = "A", descriptor = "I")
	private final int quarterTileScale = this.tileScale - 2;

	@OriginalMember(owner = "client!qm", name = "U", descriptor = "Lclient!wga;")
	public final AbstractNativeToolkit nativeToolkit;

	@OriginalMember(owner = "client!qm", name = "G", descriptor = "[[[I")
	private int[][][] tileHeightOffsets;

	@OriginalMember(owner = "client!qm", name = "p", descriptor = "[[[I")
	private int[][][] tileBlendColours;

	@OriginalMember(owner = "client!qm", name = "u", descriptor = "[[[I")
	public int[][][] tileColours;

	@OriginalMember(owner = "client!qm", name = "fb", descriptor = "[[F")
	private float[][] normalX;

	@OriginalMember(owner = "client!qm", name = "K", descriptor = "[[[Lclient!eu;")
	private GpuFloorLayer[][][] tileLayers;

	@OriginalMember(owner = "client!qm", name = "P", descriptor = "[[B")
	private final byte[][] tileFlags;

	@OriginalMember(owner = "client!qm", name = "J", descriptor = "I")
	private final int quarterTileUnits;

	@OriginalMember(owner = "client!qm", name = "x", descriptor = "[[S")
	public final short[][] tileVertexIndices;

	@OriginalMember(owner = "client!qm", name = "cb", descriptor = "[[B")
	private byte[][] shadowLevels;

	@OriginalMember(owner = "client!qm", name = "L", descriptor = "[[[I")
	public int[][][] tileOffsetsZ;

	@OriginalMember(owner = "client!qm", name = "eb", descriptor = "[[F")
	private float[][] normalY;

	@OriginalMember(owner = "client!qm", name = "T", descriptor = "[[[I")
	public int[][][] tileOffsetsX;

	@OriginalMember(owner = "client!qm", name = "s", descriptor = "I")
	public final int featureFlags;

	@OriginalMember(owner = "client!qm", name = "V", descriptor = "[[F")
	private float[][] normalZ;

	@OriginalMember(owner = "client!qm", name = "X", descriptor = "Lclient!eq;")
	private IterableHashTable layerTable;

	@OriginalMember(owner = "client!qm", name = "F", descriptor = "Lclient!dg;")
	private FloorHardShadows hardShadows;

	@OriginalMember(owner = "client!qm", name = "<init>", descriptor = "(Lclient!wga;IIII[[I[[II)V")
	public GpuFloorModel(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) int groundFlags, @OriginalArg(2) int featureFlags, @OriginalArg(3) int width, @OriginalArg(4) int length, @OriginalArg(5) int[][] tileHeights, @OriginalArg(6) int[][] normalHeights, @OriginalArg(7) int scaling) {
		super(width, length, scaling, tileHeights);
		this.nativeToolkit = nativeToolkit;
		this.tileHeightOffsets = new int[width][length][];
		this.tileBlendColours = new int[width][length][];
		this.tileColours = new int[width][length][];
		this.normalX = new float[super.width + 1][super.length + 1];
		this.tileLayers = new GpuFloorLayer[width][length][];
		this.tileFlags = new byte[width][length];
		this.quarterTileUnits = 0x1 << this.quarterTileScale;
		this.tileVertexIndices = new short[width * length][];
		this.shadowLevels = new byte[width + 1][length + 1];
		this.tileOffsetsZ = new int[width][length][];
		this.normalY = new float[super.width + 1][super.length + 1];
		this.tileOffsetsX = new int[width][length][];
		this.featureFlags = featureFlags;
		this.normalZ = new float[super.width + 1][super.length + 1];
		for (@Pc(121) int i = 0; i <= super.length; i++) {
			for (@Pc(125) int j = 0; super.width >= j; j++) {
				@Pc(134) int height = super.tileHeights[j][i];
				if ((float) height > this.maxHeight) {
					this.maxHeight = height;
				}
				if ((float) height < this.minHeight) {
					this.minHeight = height;
				}
				if (j > 0 && i > 0 && super.width > j && i < super.length) {
					@Pc(191) int heightDeltaX = normalHeights[j + 1][i] - normalHeights[j - 1][i];
					@Pc(208) int heightDeltaZ = normalHeights[j][i + 1] - normalHeights[j][i - 1];
					@Pc(228) float normalScale = (float) (1.0D / Math.sqrt((double) (scaling * 4 * scaling + heightDeltaX * heightDeltaX + heightDeltaZ * heightDeltaZ)));
					this.normalX[j][i] = (float) heightDeltaX * normalScale;
					this.normalY[j][i] = (float) (-scaling * 2) * normalScale;
					this.normalZ[j][i] = normalScale * (float) heightDeltaZ;
				}
			}
		}
		this.minHeight--;
		this.maxHeight++;
		this.layerTable = new IterableHashTable(128);
		if ((this.featureFlags & 0x10) != 0) {
			this.hardShadows = new FloorHardShadows(this.nativeToolkit, this);
		}
	}

	@OriginalMember(owner = "client!qm", name = "fa", descriptor = "(IILclient!r;)Lclient!r;")
	@Override
	public Shadow fa(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) Shadow shadow) {
		if ((this.tileFlags[x][z] & 0x1) == 0) {
			return null;
		}
		@Pc(24) int shadowSize = super.tileUnits >> this.nativeToolkit.shadowSizeShift;
		@Pc(27) GpuShadow reusableShadow = (GpuShadow) shadow;
		@Pc(37) GpuShadow tileShadow;
		if (reusableShadow != null && reusableShadow.fitsDimension(shadowSize, shadowSize)) {
			tileShadow = reusableShadow;
			reusableShadow.reset();
		} else {
			tileShadow = new GpuShadow(this.nativeToolkit, shadowSize, shadowSize);
		}
		tileShadow.setDimension(shadowSize, shadowSize, 0, 0);
		this.projectShadow(tileShadow, z, x);
		return tileShadow;
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(Lclient!fc;III)V")
	private void projectShadow(@OriginalArg(0) GpuShadow shadow, @OriginalArg(1) int tileZ, @OriginalArg(2) int tileX) {
		@Pc(21) int[] offsetsX = this.tileOffsetsX[tileX][tileZ];
		@Pc(28) int[] offsetsZ = this.tileOffsetsZ[tileX][tileZ];
		@Pc(31) int length = offsetsX.length;
		if (GpuFloorModel.shadowVerticesX.length < length) {
			GpuFloorModel.shadowVerticesX = new int[length];
			GpuFloorModel.shadowVerticesZ = new int[length];
		}
		for (@Pc(43) int i = 0; i < length; i++) {
			GpuFloorModel.shadowVerticesX[i] = offsetsX[i] >> this.nativeToolkit.shadowSizeShift;
			GpuFloorModel.shadowVerticesZ[i] = offsetsZ[i] >> this.nativeToolkit.shadowSizeShift;
		}
		@Pc(75) int index = 0;
		while (length > index) {
			@Pc(81) int xA = GpuFloorModel.shadowVerticesX[index];
			@Pc(86) int zA = GpuFloorModel.shadowVerticesZ[index++];
			@Pc(90) int xB = GpuFloorModel.shadowVerticesX[index];
			@Pc(95) int zB = GpuFloorModel.shadowVerticesZ[index++];
			@Pc(99) int xC = GpuFloorModel.shadowVerticesX[index];
			@Pc(104) int zC = GpuFloorModel.shadowVerticesZ[index++];
			if (-((xC - xB) * (zB - zA)) + (xA - xB) * (zB - zC) > 0) {
				shadow.fillTriangle(zA, xA, xB, zB, zC, xC);
			}
		}
	}

	@OriginalMember(owner = "client!qm", name = "wa", descriptor = "(Lclient!r;IIIIZ)V")
	@Override
	public void wa(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int heightOffset, @OriginalArg(3) int z, @OriginalArg(4) int arg4, @OriginalArg(5) boolean arg5) {
		if (this.hardShadows != null && shadow != null) {
			@Pc(24) int shadowX = x - (heightOffset * this.nativeToolkit.shadowOffsetX >> 8) >> this.nativeToolkit.shadowSizeShift;
			@Pc(38) int shadowZ = z - (this.nativeToolkit.shadowOffsetZ * heightOffset >> 8) >> this.nativeToolkit.shadowSizeShift;
			this.hardShadows.removeShadow(shadowZ, shadow, shadowX);
		}
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(II)V")
	@Override
	public void renderTile(@OriginalArg(0) int x, @OriginalArg(1) int z) {
	}

	@OriginalMember(owner = "client!qm", name = "CA", descriptor = "(Lclient!r;IIIIZ)V")
	@Override
	public void CA(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int heightOffset, @OriginalArg(3) int z, @OriginalArg(4) int arg4, @OriginalArg(5) boolean arg5) {
		if (this.hardShadows != null && shadow != null) {
			@Pc(24) int shadowX = x - (this.nativeToolkit.shadowOffsetX * heightOffset >> 8) >> this.nativeToolkit.shadowSizeShift;
			@Pc(39) int shadowZ = z - (this.nativeToolkit.shadowOffsetZ * heightOffset >> 8) >> this.nativeToolkit.shadowSizeShift;
			this.hardShadows.addShadow(shadowX, shadow, shadowZ);
		}
	}

	@OriginalMember(owner = "client!qm", name = "YA", descriptor = "()V")
	@Override
	public void YA() {
		if (this.totalTileVertexCount <= 0) {
			this.hardShadows = null;
		} else {
			@Pc(19) byte[][] smoothedShadowLevels = new byte[super.width + 1][super.length + 1];
			for (@Pc(21) int i = 1; i < super.width; i++) {
				for (@Pc(25) int j = 1; j < super.length; j++) {
					smoothedShadowLevels[i][j] = (byte) ((this.shadowLevels[i][j - 1] >> 2) + (this.shadowLevels[i + 1][j] >> 3) + (this.shadowLevels[i + -1][j] >> 2) + (this.shadowLevels[i][j + 1] >> 3) + (this.shadowLevels[i][j] >> 1));
				}
			}
			@Pc(105) GpuFloorLayer[] allLayers = new GpuFloorLayer[this.layerTable.size()];
			this.layerTable.toArray(allLayers);
			for (@Pc(113) int k = 0; k < allLayers.length; k++) {
				allLayers[k].initColours(this.totalTileVertexCount);
			}
			@Pc(129) int vertexStride = 20;
			if (this.tileWaterDepths != null) {
				vertexStride += 4;
			}
			if ((this.featureFlags & 0x7) != 0) {
				vertexStride += 12;
			}
			@Pc(152) NativeHeapBuffer nativeHeapBuffer = this.nativeToolkit.nativeHeap.a(this.totalTileVertexCount * 4, false);
			@Pc(162) NativeHeapBuffer nativeHeapBuffer2 = this.nativeToolkit.nativeHeap.a(this.totalTileVertexCount * vertexStride, false);
			@Pc(167) Stream stream = new Stream(nativeHeapBuffer2);
			@Pc(172) Stream stream2 = new Stream(nativeHeapBuffer);
			@Pc(176) GpuFloorLayer[] vertexLayers = new GpuFloorLayer[this.totalTileVertexCount];
			@Pc(183) int hashCapacity = Utils.floorPowerOfTwo(this.totalTileVertexCount / 4);
			if (hashCapacity < 1) {
				hashCapacity = 1;
			}
			@Pc(193) IterableHashTable iterableHashTable = new IterableHashTable(hashCapacity);
			@Pc(197) GpuFloorLayer[] tileUniqueLayers = new GpuFloorLayer[this.maxTileVertexCount];
			@Pc(203) int local203;
			for (@Pc(199) int m = 0; m < super.width; m++) {
				for (int tileZ = 0; super.length > tileZ; tileZ++) {
					if (this.tileColours[m][tileZ] != null) {
						@Pc(219) GpuFloorLayer[] tileVertexLayers = this.tileLayers[m][tileZ];
						@Pc(226) int[] offsetsX = this.tileOffsetsX[m][tileZ];
						@Pc(233) int[] offsetsZ = this.tileOffsetsZ[m][tileZ];
						@Pc(240) int[] blendedColours = this.tileBlendColours[m][tileZ];
						@Pc(247) int[] colours = this.tileColours[m][tileZ];
						@Pc(259) int[] heightOffsets = this.tileHeightOffsets == null ? null : this.tileHeightOffsets[m][tileZ];
						@Pc(271) int[] waterDepths = this.tileWaterDepths == null ? null : this.tileWaterDepths[m][tileZ];
						if (blendedColours == null) {
							blendedColours = colours;
						}
						@Pc(282) float normalX00 = this.normalX[m][tileZ];
						@Pc(289) float normalY00 = this.normalY[m][tileZ];
						@Pc(296) float normalZ00 = this.normalZ[m][tileZ];
						@Pc(305) float normalX01 = this.normalX[m][tileZ + 1];
						@Pc(314) float normalY01 = this.normalY[m][tileZ + 1];
						@Pc(323) float normalZ01 = this.normalZ[m][tileZ + 1];
						@Pc(334) float normalX11 = this.normalX[m + 1][tileZ + 1];
						@Pc(345) float normalY11 = this.normalY[m + 1][tileZ + 1];
						@Pc(356) float normalZ11 = this.normalZ[m + 1][tileZ + 1];
						@Pc(365) float normalX10 = this.normalX[m + 1][tileZ];
						@Pc(374) float normalY10 = this.normalY[m + 1][tileZ];
						@Pc(383) float normalZ10 = this.normalZ[m + 1][tileZ];
						@Pc(391) int shadow00 = smoothedShadowLevels[m][tileZ] & 0xFF;
						@Pc(401) int shadow01 = smoothedShadowLevels[m][tileZ + 1] & 0xFF;
						@Pc(413) int shadow11 = smoothedShadowLevels[m + 1][tileZ + 1] & 0xFF;
						@Pc(423) int shadow10 = smoothedShadowLevels[m + 1][tileZ] & 0xFF;
						@Pc(425) int uniqueLayerCount = 0;
						@Pc(435) int local435;
						label355: for (@Pc(427) int tileVertex = 0; tileVertex < colours.length; tileVertex++) {
							@Pc(433) GpuFloorLayer layer = tileVertexLayers[tileVertex];
							for (int layerSlot = 0; layerSlot < uniqueLayerCount; layerSlot++) {
								if (layer == tileUniqueLayers[layerSlot]) {
									continue label355;
								}
							}
							tileUniqueLayers[uniqueLayerCount++] = layer;
						}
						@Pc(477) short[] vertexIndices = this.tileVertexIndices[tileZ * super.width + m] = new short[colours.length];
						for (int tileVertexIndex = 0; tileVertexIndex < colours.length; tileVertexIndex++) {
							@Pc(490) int vertexX = (m << super.tileScale) + offsetsX[tileVertexIndex];
							@Pc(500) int vertexZ = (tileZ << super.tileScale) + offsetsZ[tileVertexIndex];
							@Pc(505) int gridX = vertexX >> this.quarterTileScale;
							@Pc(510) int gridZ = vertexZ >> this.quarterTileScale;
							@Pc(514) int colour = colours[tileVertexIndex];
							@Pc(518) int blendedColour = blendedColours[tileVertexIndex];
							@Pc(526) int heightOffset = heightOffsets == null ? 0 : heightOffsets[tileVertexIndex];
							@Pc(544) long vertexKey = (long) gridZ | (long) colour << 32 | (long) blendedColour << 48 | (long) (gridX << 16);
							@Pc(548) int offsetX = offsetsX[tileVertexIndex];
							@Pc(552) int offsetZ = offsetsZ[tileVertexIndex];
							@Pc(554) byte baseLightness = 74;
							@Pc(556) int rgb = 0;
							@Pc(587) float vertexNormalX;
							@Pc(591) float vertexNormalY;
							@Pc(589) float vertexNormalZ;
							@Pc(641) float local641;
							@Pc(595) int lightness;
							if (offsetX == 0 && offsetZ == 0) {
								lightness = baseLightness - shadow00;
								vertexNormalY = normalY00;
								vertexNormalX = normalX00;
								vertexNormalZ = normalZ00;
							} else if (offsetX == 0 && offsetZ == super.tileUnits) {
								lightness = baseLightness - shadow01;
								vertexNormalX = normalX01;
								vertexNormalY = normalY01;
								vertexNormalZ = normalZ01;
							} else if (super.tileUnits == offsetX && super.tileUnits == offsetZ) {
								vertexNormalX = normalX11;
								vertexNormalZ = normalZ11;
								vertexNormalY = normalY11;
								lightness = baseLightness - shadow11;
							} else if (offsetX == super.tileUnits && offsetZ == 0) {
								vertexNormalZ = normalZ10;
								vertexNormalX = normalX10;
								vertexNormalY = normalY10;
								lightness = baseLightness - shadow10;
							} else {
								@Pc(617) float fracX = (float) offsetX / (float) super.tileUnits;
								@Pc(624) float fracZ = (float) offsetZ / (float) super.tileUnits;
								@Pc(632) float lerpNormalX0 = normalX00 + (normalX10 - normalX00) * fracX;
								float lerpNormalY0 = normalY00 + (normalY10 - normalY00) * fracX;
								@Pc(649) float lerpNormalZ0 = fracX * (normalZ10 - normalZ00) + normalZ00;
								@Pc(658) float lerpNormalX1 = fracX * (normalX11 - normalX01) + normalX01;
								@Pc(666) float lerpNormalY1 = normalY01 + (normalY11 - normalY01) * fracX;
								vertexNormalX = lerpNormalX0 + fracZ * (lerpNormalX1 - lerpNormalX0);
								vertexNormalY = lerpNormalY0 + fracZ * (lerpNormalY1 - lerpNormalY0);
								@Pc(692) float lerpNormalZ1 = normalZ01 + (normalZ11 - normalZ01) * fracX;
								vertexNormalZ = lerpNormalZ0 + fracZ * (lerpNormalZ1 - lerpNormalZ0);
								@Pc(712) int lerpShadow0 = ((shadow10 - shadow00) * offsetX >> super.tileScale) + shadow00;
								@Pc(724) int lerpShadow1 = shadow01 + (offsetX * (shadow11 - shadow01) >> super.tileScale);
								lightness = baseLightness - lerpShadow0 - (offsetZ * (lerpShadow1 - lerpShadow0) >> super.tileScale);
							}
							@Pc(777) float lightIntensity = 1.0F;
							if (colour != -1) {
								@Pc(788) int adjustedLightness = lightness * (colour & 0x7F) >> 7;
								if (adjustedLightness < 2) {
									adjustedLightness = 2;
								} else if (adjustedLightness > 126) {
									adjustedLightness = 126;
								}
								rgb = ColorUtil.hslToRgb[adjustedLightness | colour & 0xFF80];
								if ((this.featureFlags & 0x7) == 0) {
									lightIntensity = vertexNormalX * this.nativeToolkit.sunPosition[0] + vertexNormalY * this.nativeToolkit.sunPosition[1] + this.nativeToolkit.sunPosition[2] * vertexNormalZ;
									lightIntensity = lightIntensity * (lightIntensity > 0.0F ? this.nativeToolkit.sunColorIntensity : this.nativeToolkit.antiSunColorIntensity) + this.nativeToolkit.intensity;
								}
							}
							@Pc(864) Node node = null;
							if ((vertexX & this.quarterTileUnits - 1) == 0 && (this.quarterTileUnits - 1 & vertexZ) == 0) {
								node = iterableHashTable.get(vertexKey);
							}
							@Pc(1197) int vertexIndex;
							@Pc(918) int local918;
							if (node == null) {
								int blendedRgb;
								if (blendedColour == colour) {
									blendedRgb = rgb;
								} else {
									@Pc(896) int blendedLightness = (blendedColour & 0x7F) * lightness >> 7;
									if (blendedLightness < 2) {
										blendedLightness = 2;
									} else if (blendedLightness > 126) {
										blendedLightness = 126;
									}
									blendedRgb = ColorUtil.hslToRgb[blendedLightness | blendedColour & 0xFF80];
									if ((this.featureFlags & 0x7) == 0) {
										float blendedIntensity = this.nativeToolkit.sunPosition[2] * vertexNormalZ + vertexNormalY * this.nativeToolkit.sunPosition[1] + this.nativeToolkit.sunPosition[0] * vertexNormalX;
										blendedIntensity = this.nativeToolkit.intensity + (lightIntensity > 0.0F ? this.nativeToolkit.sunColorIntensity : this.nativeToolkit.antiSunColorIntensity) * lightIntensity;
										@Pc(974) int red = blendedRgb >> 16 & 0xFF;
										@Pc(980) int green = blendedRgb >> 8 & 0xFF;
										@Pc(984) int blue = blendedRgb & 0xFF;
										red = (int) ((float) red * blendedIntensity);
										green = (int) ((float) green * blendedIntensity);
										if (red < 0) {
											red = 0;
										} else if (red > 255) {
											red = 255;
										}
										blue = (int) ((float) blue * blendedIntensity);
										if (green < 0) {
											green = 0;
										} else if (green > 255) {
											green = 255;
										}
										if (blue < 0) {
											blue = 0;
										} else if (blue > 255) {
											blue = 255;
										}
										blendedRgb = red << 16 | green << 8 | blue;
									}
								}
								if (Stream.c()) {
									stream.a((float) vertexX);
									stream.a((float) (heightOffset + this.averageHeight(vertexX, vertexZ)));
									stream.a((float) vertexZ);
									stream.a((float) vertexX);
									stream.a((float) vertexZ);
									if (this.tileWaterDepths != null) {
										stream.a((float) (waterDepths == null ? 0 : waterDepths[tileVertexIndex] - 1));
									}
									if ((this.featureFlags & 0x7) != 0) {
										stream.a(vertexNormalX);
										stream.a(vertexNormalY);
										stream.a(vertexNormalZ);
									}
								} else {
									stream.b((float) vertexX);
									stream.b((float) (this.averageHeight(vertexX, vertexZ) + heightOffset));
									stream.b((float) vertexZ);
									stream.b((float) vertexX);
									stream.b((float) vertexZ);
									if (this.tileWaterDepths != null) {
										stream.b((float) (waterDepths == null ? 0 : waterDepths[tileVertexIndex] - 1));
									}
									if ((this.featureFlags & 0x7) != 0) {
										stream.b(vertexNormalX);
										stream.b(vertexNormalY);
										stream.b(vertexNormalZ);
									}
								}
								if (this.nativeToolkit.endiannessType == 0) {
									stream2.b(blendedRgb | 0xFF000000);
								} else {
									stream2.c(blendedRgb | 0xFF000000);
								}
								vertexIndex = this.vertexCount++;
								vertexIndices[tileVertexIndex] = (short) vertexIndex;
								if (colour != -1) {
									vertexLayers[vertexIndex] = tileVertexLayers[tileVertexIndex];
								}
								iterableHashTable.put(vertexKey, new ShortNode(vertexIndices[tileVertexIndex]));
							} else {
								vertexIndices[tileVertexIndex] = ((ShortNode) node).value;
								vertexIndex = vertexIndices[tileVertexIndex] & 0xFFFF;
								if (colour != -1 && tileVertexLayers[tileVertexIndex].key < vertexLayers[vertexIndex].key) {
									vertexLayers[vertexIndex] = tileVertexLayers[tileVertexIndex];
								}
							}
							for (int uniqueLayer = 0; uniqueLayer < uniqueLayerCount; uniqueLayer++) {
								tileUniqueLayers[uniqueLayer].setVertexColour(vertexIndex, lightIntensity, lightness, rgb);
							}
							this.indexCount++;
						}
					}
				}
			}
			for (int vertex = 0; vertex < this.vertexCount; vertex++) {
				@Pc(1312) GpuFloorLayer vertexLayer = vertexLayers[vertex];
				if (vertexLayer != null) {
					vertexLayer.setVertexOpaque(vertex);
				}
			}
			@Pc(1333) int local1333;
			@Pc(1352) int local1352;
			for (@Pc(1329) int x = 0; x < super.width; x++) {
				for (int tileZ = 0; tileZ < super.length; tileZ++) {
					@Pc(1346) short[] triangleVertices = this.tileVertexIndices[tileZ * super.width + x];
					if (triangleVertices != null) {
						@Pc(1350) int triangleIndex = 0;
						int triangleVertexOffset = 0;
						while (triangleVertices.length > triangleVertexOffset) {
							@Pc(1361) int vertexA = triangleVertices[triangleVertexOffset++] & 0xFFFF;
							@Pc(1368) int vertexB = triangleVertices[triangleVertexOffset++] & 0xFFFF;
							@Pc(1375) int vertexC = triangleVertices[triangleVertexOffset++] & 0xFFFF;
							@Pc(1379) GpuFloorLayer layerA = vertexLayers[vertexA];
							@Pc(1383) GpuFloorLayer layerB = vertexLayers[vertexB];
							@Pc(1387) GpuFloorLayer layerC = vertexLayers[vertexC];
							@Pc(1389) GpuFloorLayer primaryLayer = null;
							if (layerA != null) {
								primaryLayer = layerA;
								layerA.addTriangle(triangleIndex, tileZ, x);
							}
							if (layerB != null) {
								layerB.addTriangle(triangleIndex, tileZ, x);
								if (primaryLayer == null || primaryLayer.key > layerB.key) {
									primaryLayer = layerB;
								}
							}
							if (layerC != null) {
								layerC.addTriangle(triangleIndex, tileZ, x);
								if (primaryLayer == null || primaryLayer.key > layerC.key) {
									primaryLayer = layerC;
								}
							}
							if (primaryLayer != null) {
								if (layerA != null) {
									primaryLayer.setVertexOpaque(vertexA);
								}
								if (layerB != null) {
									primaryLayer.setVertexOpaque(vertexB);
								}
								if (layerC != null) {
									primaryLayer.setVertexOpaque(vertexC);
								}
								primaryLayer.addTriangle(triangleIndex, tileZ, x);
							}
							triangleIndex++;
						}
					}
				}
			}
			stream.a();
			stream2.a();
			this.colourStream = this.nativeToolkit.createStream(false);
			this.colourStream.createFromSource(nativeHeapBuffer, 4, this.vertexCount * 4);
			this.vertexStream = this.nativeToolkit.createStream(false);
			this.vertexStream.createFromSource(nativeHeapBuffer2, vertexStride, vertexStride * this.vertexCount);
			if ((this.featureFlags & 0x7) == 0) {
				if (this.tileWaterDepths == null) {
					this.vertexDeclaration = this.nativeToolkit.createVertexDeclaration(new VertexDeclarationComponent[] { new VertexDeclarationComponent(new VertexAttribute[] { VertexDeclarationAction.POSITION, VertexDeclarationAction.TEXCOORD }), new VertexDeclarationComponent(VertexDeclarationAction.COLOR) });
				} else {
					this.vertexDeclaration = this.nativeToolkit.createVertexDeclaration(new VertexDeclarationComponent[] { new VertexDeclarationComponent(new VertexAttribute[] { VertexDeclarationAction.POSITION, VertexDeclarationAction.TEXCOORD, VertexDeclarationAction.aClass69_4 }), new VertexDeclarationComponent(VertexDeclarationAction.COLOR) });
				}
			} else if (this.tileWaterDepths == null) {
				this.vertexDeclaration = this.nativeToolkit.createVertexDeclaration(new VertexDeclarationComponent[] { new VertexDeclarationComponent(new VertexAttribute[] { VertexDeclarationAction.POSITION, VertexDeclarationAction.TEXCOORD, VertexDeclarationAction.NORMAL_POINTER_ACTION }), new VertexDeclarationComponent(VertexDeclarationAction.COLOR) });
			} else {
				this.vertexDeclaration = this.nativeToolkit.createVertexDeclaration(new VertexDeclarationComponent[] { new VertexDeclarationComponent(new VertexAttribute[] { VertexDeclarationAction.POSITION, VertexDeclarationAction.TEXCOORD, VertexDeclarationAction.aClass69_4, VertexDeclarationAction.NORMAL_POINTER_ACTION }), new VertexDeclarationComponent(VertexDeclarationAction.COLOR) });
			}
			int activeLayerCount = 0;
			for (@Pc(1684) int layerIndex = 0; layerIndex < allLayers.length; layerIndex++) {
				if (allLayers[layerIndex].triangleCount > 0) {
					allLayers[activeLayerCount++] = allLayers[layerIndex];
				}
			}
			this.layers = new GpuFloorLayer[activeLayerCount];
			@Pc(1713) long[] layerPointers = new long[activeLayerCount];
			for (int layerSlot = 0; layerSlot < activeLayerCount; layerSlot++) {
				@Pc(1721) GpuFloorLayer activeLayer = allLayers[layerSlot];
				layerPointers[layerSlot] = activeLayer.key;
				this.layers[layerSlot] = activeLayer;
				activeLayer.finishColours(this.vertexCount);
			}
			ArrayUtils.quicksort(this.layers, layerPointers);
			if (this.hardShadows != null) {
				this.hardShadows.initializeShadowTiles();
			}
		}
		this.tileHeightOffsets = null;
		this.layerTable = null;
		this.tileBlendColours = null;
		this.tileLayers = null;
		this.normalX = this.normalY = this.normalZ = null;
		this.tileColours = null;
		this.shadowLevels = null;
		this.tileWaterDepths = null;
		this.tileOffsetsX = this.tileOffsetsZ = null;
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(Lclient!r;IIIIZ)Z")
	@Override
	public boolean method8002(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int heightOffset, @OriginalArg(3) int z) {
		if (this.hardShadows == null || shadow == null) {
			return false;
		} else {
			@Pc(25) int shadowX = x - (this.nativeToolkit.shadowOffsetX * heightOffset >> 8) >> this.nativeToolkit.shadowSizeShift;
			@Pc(40) int shadowZ = z - (this.nativeToolkit.shadowOffsetZ * heightOffset >> 8) >> this.nativeToolkit.shadowSizeShift;
			return this.hardShadows.applyShadow(shadowX, shadow, shadowZ);
		}
	}

	@OriginalMember(owner = "client!qm", name = "ka", descriptor = "(III)V")
	@Override
	public void ka(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int shadowLevel) {
		if (shadowLevel > (this.shadowLevels[x][z] & 0xFF)) {
			this.shadowLevels[x][z] = (byte) shadowLevel;
		}
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(III[[ZZII)V")
	@Override
	public void renderFloor(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int radius, @OriginalArg(3) boolean[][] visibility, @OriginalArg(4) boolean belowLowestPlane, @OriginalArg(5) int orthoZoom, @OriginalArg(6) int skipFlags) {
		this.renderGround(x, z, orthoZoom, belowLowestPlane, visibility, skipFlags, radius);
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(IIIZ[[ZIII)V")
	private void renderGround(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int orthoZoom, @OriginalArg(3) boolean belowLowestPlane, @OriginalArg(4) boolean[][] visibility, @OriginalArg(6) int skipFlags, @OriginalArg(7) int radius) {
		if (this.layers == null) {
			return;
		}
		@Pc(15) int diameter = radius + radius + 1;
		@Pc(19) int maxVisibleTiles = diameter * diameter;
		if (GpuFloorModel.visibleTileIndices.length < maxVisibleTiles) {
			GpuFloorModel.visibleTileIndices = new int[maxVisibleTiles];
		}
		@Pc(35) int minX = x - radius;
		@Pc(37) int originX = minX;
		if (minX < 0) {
			minX = 0;
		}
		@Pc(46) int minZ = z - radius;
		@Pc(48) int originZ = minZ;
		if (minZ < 0) {
			minZ = 0;
		}
		@Pc(56) int maxX = radius + x;
		if (super.width - 1 < maxX) {
			maxX = super.width - 1;
		}
		@Pc(71) int maxZ = z + radius;
		if (super.length - 1 < maxZ) {
			maxZ = super.length - 1;
		}
		GpuFloorModel.visibleTileCount = 0;
		for (@Pc(90) int i = minX; i <= maxX; i++) {
			@Pc(98) boolean[] visibilityRow = visibility[i - originX];
			for (@Pc(100) int j = minZ; j <= maxZ; j++) {
				if (visibilityRow[j - originZ]) {
					GpuFloorModel.visibleTileIndices[GpuFloorModel.visibleTileCount++] = i + super.width * j;
				}
			}
		}
		if (orthoZoom == -1) {
			this.nativeToolkit.setupSceneRenderState();
		} else {
			this.nativeToolkit.setOrthoZoom((float) orthoZoom);
			this.nativeToolkit.setupOrthoSceneRenderState();
		}
		this.nativeToolkit.resetModelMatrix();
		this.nativeToolkit.toggleLights((this.featureFlags & 0x7) != 0);
		this.nativeToolkit.setActiveMaterial(false, false, -1);
		this.nativeToolkit.setStreamSource(this.vertexStream, 0);
		for (@Pc(192) int k = 0; k < this.layers.length; k++) {
			this.layers[k].render(GpuFloorModel.visibleTileIndices, GpuFloorModel.visibleTileCount);
		}
		@Pc(213) NativeMatrix modelMatrix = this.nativeToolkit.getModelMatrix();
		modelMatrix.applyTranslation(0, -1, 0);
		this.nativeToolkit.commitModelMatrix();
		if (!this.lights.isEmpty()) {
			@Pc(231) int fogColour = this.nativeToolkit.fogColorRgb;
			@Pc(235) int fogDistance = this.nativeToolkit.fogDistance;
			this.nativeToolkit.L(0, fogDistance, this.nativeToolkit.fogOffset);
			this.nativeToolkit.toggleLights(false);
			this.nativeToolkit.setDepthMaskEnabled(false);
			this.nativeToolkit.toggleBlending(128);
			this.nativeToolkit.setActiveMaterial(false, false, -2);
			this.nativeToolkit.setTextureSampler(this.nativeToolkit.defaultTexture);
			this.nativeToolkit.setTextureCombineModes(TextureCombineFunction.aClass315_4, TextureCombineMode.MODULATE);
			this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.CONSTANT_COLOR);
			this.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.PRIMARY_COLOR);
			for (@Pc(297) Node node = this.lights.first(); node != null; node = this.lights.next()) {
				@Pc(302) GpuFloorLight light = (GpuFloorLight) node;
				light.render(z, radius, visibility, x);
			}
			this.nativeToolkit.setTextureRgbStageState(0, TextureCombineSource.TEXTURE);
			this.nativeToolkit.setTextureAlphaStageState(0, TextureCombineSource.TEXTURE);
			this.nativeToolkit.setTextureSampler(null);
			this.nativeToolkit.L(fogColour, fogDistance, this.nativeToolkit.fogOffset);
		}
		if (this.hardShadows != null) {
			this.nativeToolkit.setStreamSource(this.vertexStream, 0);
			this.nativeToolkit.setStreamSource(this.colourStream, 1);
			this.nativeToolkit.setVertexDeclaration(this.vertexDeclaration);
			this.hardShadows.renderShadowOverlay(radius, x, belowLowestPlane, visibility, z);
		}
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(Lclient!ab;[I)V")
	@Override
	public void addLight(@OriginalArg(0) PointLight pointLight, @OriginalArg(1) int[] tileMask) {
		this.lights.addLast(new GpuFloorLight(this.nativeToolkit, this, pointLight, tileMask));
	}

	@OriginalMember(owner = "client!qm", name = "U", descriptor = "(II[I[I[I[I[I[I[I[IIIIZ)V")
	@Override
	public void U(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int[] offsetsX, @OriginalArg(3) int[] heightOffsets, @OriginalArg(4) int[] offsetsZ, @OriginalArg(5) int[] waterDepths, @OriginalArg(6) int[] colours, @OriginalArg(7) int[] blendedColours, @OriginalArg(8) int[] materialIds, @OriginalArg(9) int[] textureScales, @OriginalArg(10) int waterColour, @OriginalArg(11) int waterDepth, @OriginalArg(12) int waterBias, @OriginalArg(13) boolean hasShadow) {
		@Pc(9) MaterialProvider textureSource = this.nativeToolkit.materialProvider;
		if (waterDepths != null && this.tileWaterDepths == null) {
			this.tileWaterDepths = new int[super.width][super.length][];
		}
		if (heightOffsets != null && this.tileHeightOffsets == null) {
			this.tileHeightOffsets = new int[super.width][super.length][];
		}
		this.tileOffsetsX[x][z] = offsetsX;
		this.tileOffsetsZ[x][z] = offsetsZ;
		this.tileColours[x][z] = colours;
		this.tileBlendColours[x][z] = blendedColours;
		if (this.tileWaterDepths != null) {
			this.tileWaterDepths[x][z] = waterDepths;
		}
		if (this.tileHeightOffsets != null) {
			this.tileHeightOffsets[x][z] = heightOffsets;
		}
		@Pc(92) GpuFloorLayer[] tileVertexLayers = this.tileLayers[x][z] = new GpuFloorLayer[colours.length];
		for (@Pc(94) int i = 0; i < colours.length; i++) {
			@Pc(100) int materialId = materialIds[i];
			@Pc(104) int textureScale = textureScales[i];
			if ((this.featureFlags & 0x20) != 0 && materialId != -1 && textureSource.getMetrics(materialId).detailsOnly) {
				textureScale = 128;
				materialId = -1;
			}
			@Pc(146) long layerKey = (long) materialId | (long) (textureScale << 14) | (long) waterDepth << 42 | (long) waterBias << 48 | (long) waterColour << 28;
			@Pc(152) Node node;
			for (node = this.layerTable.get(layerKey); node != null; node = this.layerTable.nextWithKey()) {
				@Pc(157) GpuFloorLayer layer = (GpuFloorLayer) node;
				if (materialId == layer.materialId && layer.textureScale == (float) textureScale && layer.waterFogColour == waterColour && layer.waterDepth == waterDepth && layer.waterIntensity == waterBias) {
					break;
				}
			}
			if (node == null) {
				tileVertexLayers[i] = new GpuFloorLayer(this, materialId, textureScale, waterColour, waterDepth, waterBias);
				this.layerTable.put(layerKey, tileVertexLayers[i]);
			} else {
				tileVertexLayers[i] = (GpuFloorLayer) node;
			}
		}
		if (hasShadow) {
			this.tileFlags[x][z] = (byte) (this.tileFlags[x][z] | 0x1);
		}
		if (this.maxTileVertexCount < colours.length) {
			this.maxTileVertexCount = colours.length;
		}
		this.totalTileVertexCount += colours.length;
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(III[[ZZI)V")
	@Override
	public void renderTiles(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int radius, @OriginalArg(3) boolean[][] visibility, @OriginalArg(4) boolean belowLowestPlane, @OriginalArg(5) int skipFlags) {
		this.renderGround(x, z, -1, belowLowestPlane, visibility, skipFlags, radius);
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(II[I[I[I[I[I[I[I[I[I[I[IIIIZ)V")
	@Override
	public void addTile(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int[] offsetsX, @OriginalArg(3) int[] heightOffsets, @OriginalArg(4) int[] offsetsZ, @OriginalArg(5) int[] waterDepths, @OriginalArg(6) int[] faceA, @OriginalArg(7) int[] faceB, @OriginalArg(8) int[] faceC, @OriginalArg(9) int[] colours, @OriginalArg(10) int[] blendedColours, @OriginalArg(11) int[] materialIds, @OriginalArg(12) int[] textureScales, @OriginalArg(13) int waterColour, @OriginalArg(14) int waterDepth, @OriginalArg(15) int waterBias) {
		@Pc(8) int length = colours.length;
		@Pc(13) int[] expandedOffsetsX = new int[length * 3];
		@Pc(18) int[] expandedOffsetsZ = new int[length * 3];
		@Pc(23) int[] expandedColours = new int[length * 3];
		@Pc(28) int[] expandedBlendedColours = new int[length * 3];
		@Pc(33) int[] expandedMaterialIds = new int[length * 3];
		@Pc(38) int[] expandedTextureScales = new int[length * 3];
		@Pc(47) int[] expandedHeightOffsets = heightOffsets == null ? null : new int[length * 3];
		@Pc(56) int[] expandedWaterDepths = waterDepths == null ? null : new int[length * 3];
		@Pc(58) int vertexIndex = 0;
		for (@Pc(60) int i = 0; i < length; i++) {
			@Pc(66) int vertexA = faceA[i];
			@Pc(70) int vertexB = faceB[i];
			@Pc(74) int vertexC = faceC[i];
			expandedOffsetsX[vertexIndex] = offsetsX[vertexA];
			expandedOffsetsZ[vertexIndex] = offsetsZ[vertexA];
			expandedColours[vertexIndex] = colours[i];
			expandedMaterialIds[vertexIndex] = materialIds[i];
			expandedTextureScales[vertexIndex] = textureScales[i];
			expandedBlendedColours[vertexIndex] = blendedColours == null ? colours[i] : blendedColours[i];
			if (heightOffsets != null) {
				expandedHeightOffsets[vertexIndex] = heightOffsets[vertexA];
			}
			if (waterDepths != null) {
				expandedWaterDepths[vertexIndex] = waterDepths[vertexA];
			}
			vertexIndex++;
			expandedOffsetsX[vertexIndex] = offsetsX[vertexB];
			expandedOffsetsZ[vertexIndex] = offsetsZ[vertexB];
			expandedColours[vertexIndex] = colours[i];
			expandedMaterialIds[vertexIndex] = materialIds[i];
			expandedTextureScales[vertexIndex] = textureScales[i];
			expandedBlendedColours[vertexIndex] = blendedColours == null ? colours[i] : blendedColours[i];
			if (heightOffsets != null) {
				expandedHeightOffsets[vertexIndex] = heightOffsets[vertexB];
			}
			if (waterDepths != null) {
				expandedWaterDepths[vertexIndex] = waterDepths[vertexB];
			}
			vertexIndex++;
			expandedOffsetsX[vertexIndex] = offsetsX[vertexC];
			expandedOffsetsZ[vertexIndex] = offsetsZ[vertexC];
			expandedColours[vertexIndex] = colours[i];
			expandedMaterialIds[vertexIndex] = materialIds[i];
			expandedTextureScales[vertexIndex] = textureScales[i];
			expandedBlendedColours[vertexIndex] = blendedColours == null ? colours[i] : blendedColours[i];
			if (heightOffsets != null) {
				expandedHeightOffsets[vertexIndex] = heightOffsets[vertexC];
			}
			if (waterDepths != null) {
				expandedWaterDepths[vertexIndex] = waterDepths[vertexC];
			}
			vertexIndex++;
		}
		this.U(x, z, expandedOffsetsX, expandedHeightOffsets, expandedOffsetsZ, expandedWaterDepths, expandedColours, expandedBlendedColours, expandedMaterialIds, expandedTextureScales, waterColour, waterDepth, waterBias, false);
	}

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(IIIIIII[[Z)V")
	@Override
	public void drawMinimap(@OriginalArg(3) int x1, @OriginalArg(4) int y1, @OriginalArg(5) int x2, @OriginalArg(6) int y2, @OriginalArg(7) boolean[][] visibility) {
		if (this.totalTileVertexCount <= 0) {
			return;
		}
		@Pc(16) GpuIndexBuffer gpuIndexBuffer = this.nativeToolkit.getOrResizeIndexBuffer(this.indexCount);
		@Pc(18) int elementCount = 0;
		@Pc(20) int minVertexIndex = 32767;
		@Pc(22) int maxVertexIndex = -32768;
		for (@Pc(24) int i = 0; i < 4; i++) {
			elementCount = 0;
			@Pc(33) Buffer buffer = gpuIndexBuffer.lock();
			if (buffer != null) {
				@Pc(41) Stream stream = this.nativeToolkit.wrapBuffer(buffer);
				@Pc(45) int z;
				@Pc(54) int tileIndex;
				@Pc(56) int x;
				@Pc(74) short[] vertexIndices;
				@Pc(78) int k;
				@Pc(86) int vertexIndex;
				if (Stream.c()) {
					for (z = y1; z < y2; z++) {
						tileIndex = z * super.width + x1;
						for (x = x1; x < x2; x++) {
							if (visibility[x - x1][z - y1]) {
								vertexIndices = this.tileVertexIndices[tileIndex];
								if (vertexIndices != null) {
									for (k = 0; k < vertexIndices.length; k++) {
										vertexIndex = vertexIndices[k] & 0xFFFF;
										stream.d(vertexIndex);
										if (vertexIndex > maxVertexIndex) {
											maxVertexIndex = vertexIndex;
										}
										elementCount++;
										if (vertexIndex < minVertexIndex) {
											minVertexIndex = vertexIndex;
										}
									}
								}
							}
							tileIndex++;
						}
					}
				} else {
					for (z = y1; z < y2; z++) {
						tileIndex = x1 + z * super.width;
						for (x = x1; x < x2; x++) {
							if (visibility[x - x1][z - y1]) {
								vertexIndices = this.tileVertexIndices[tileIndex];
								if (vertexIndices != null) {
									for (k = 0; k < vertexIndices.length; k++) {
										vertexIndex = vertexIndices[k] & 0xFFFF;
										stream.a(vertexIndex);
										if (maxVertexIndex < vertexIndex) {
											maxVertexIndex = vertexIndex;
										}
										elementCount++;
										if (vertexIndex < minVertexIndex) {
											minVertexIndex = vertexIndex;
										}
									}
								}
							}
							tileIndex++;
						}
					}
				}
				stream.a();
				if (gpuIndexBuffer.unlock()) {
					break;
				}
			}
		}
		if (elementCount <= 0) {
			return;
		}
		this.nativeToolkit.setOrthoMatrixMode();
		this.nativeToolkit.toggleFog(false);
		this.nativeToolkit.toggleLights(false);
		this.nativeToolkit.toggleDepthTest(false);
		this.nativeToolkit.setDepthMaskEnabled(false);
		this.nativeToolkit.toggleBlending(0);
		this.nativeToolkit.setActiveMaterial(false, false, -2);
		this.nativeToolkit.setTextureSampler(null);
		@Pc(260) NativeMatrix modelMatrix = this.nativeToolkit.getModelMatrix();
		@Pc(265) float[] projectionMatrix = this.nativeToolkit.getOrthoProjectionMatrix();
		projectionMatrix[6] = 0.0F;
		projectionMatrix[13] = 1.0F - ((float) 0 + (float) (y2 * 1024) / 128.0F) / (float) this.nativeToolkit.surfaceHeight;
		projectionMatrix[11] = 0.0F;
		projectionMatrix[0] = (float) 1024 / ((float) this.nativeToolkit.surfaceWidth * (float) super.tileUnits * 128.0F);
		projectionMatrix[14] = -this.minHeight / (this.maxHeight - this.minHeight);
		projectionMatrix[2] = 0.0F;
		projectionMatrix[15] = 1.0F;
		projectionMatrix[5] = (float) 1024 / ((float) super.tileUnits * 128.0F * (float) this.nativeToolkit.surfaceHeight);
		projectionMatrix[4] = 0.0F;
		projectionMatrix[3] = 0.0F;
		projectionMatrix[8] = 0.0F;
		projectionMatrix[7] = 0.0F;
		projectionMatrix[10] = 1.0F / (this.maxHeight - this.minHeight);
		projectionMatrix[9] = 0.0F;
		projectionMatrix[12] = -1.0F - ((float) (x1 * 1024) / 128.0F - (float) 0) / (float) this.nativeToolkit.surfaceWidth;
		projectionMatrix[1] = 0.0F;
		modelMatrix.setRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F);
		this.nativeToolkit.applyOrthoMatrixMode();
		this.nativeToolkit.commitModelMatrix();
		if ((this.featureFlags & 0x7) == 0) {
			this.nativeToolkit.toggleLights(false);
		} else {
			this.nativeToolkit.toggleLights(true);
			this.nativeToolkit.setLightPosition();
		}
		this.nativeToolkit.toggleMultisampling(false);
		this.nativeToolkit.setStreamSource(this.vertexStream, 0);
		this.nativeToolkit.setStreamSource(this.colourStream, 1);
		this.nativeToolkit.setVertexDeclaration(this.vertexDeclaration);
		this.nativeToolkit.drawIndexedPrimitive(PrimitiveType.aClass134_6, elementCount / 3, gpuIndexBuffer, minVertexIndex, 0, maxVertexIndex + 1 - minVertexIndex);
		this.nativeToolkit.toggleMultisampling(true);
	}
	@OriginalMember(owner = "client!an", name = "k", descriptor = "[I")
	public static int[] visibleTileIndices = new int[1];

	@OriginalMember(owner = "client!sm", name = "q", descriptor = "[I")
	public static int[] shadowVerticesZ = new int[1];

	@OriginalMember(owner = "client!hw", name = "d", descriptor = "[I")
	public static int[] shadowVerticesX = new int[1];
	@OriginalMember(owner = "client!bw", name = "v", descriptor = "I")
	public static int visibleTileCount;
}
