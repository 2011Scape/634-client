package com.jagex.game.runetek5.scene.ground;

import com.jagex.game.runetek5.cache.node.impl.ShortNode;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import jaclib.memory.Stream;
import jaclib.memory.heap.NativeHeapBuffer;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.buffer.ArrayBufferGL;
import com.jagex.game.runetek5.cache.node.GlGroundLayer;
import com.jagex.game.runetek5.cache.node.lru.GlShadow;
import com.jagex.game.runetek5.graphics.light.LightGL;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlElementsArrayBuffer;
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer;
import com.jagex.game.runetek5.graphics.gl.GlShadowMap;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import com.jagex.game.runetek5.utils.ColorUtil;
import com.jagex.game.runetek5.utils.ArrayUtils;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!ed")
public final class GlGround extends Ground {

	@OriginalMember(owner = "client!ed", name = "w", descriptor = "I")
	private int vertexCount;

	@OriginalMember(owner = "client!ed", name = "C", descriptor = "[[[I")
	private int[][][] tileWaterDepths;

	@OriginalMember(owner = "client!ed", name = "H", descriptor = "I")
	private int indexCount;

	@OriginalMember(owner = "client!ed", name = "Y", descriptor = "Lclient!nh;")
	private OpenGlArrayDrawBuffer colorBuffer;

	@OriginalMember(owner = "client!ed", name = "Z", descriptor = "Lclient!nh;")
	public OpenGlArrayDrawBuffer normalsBuffer;

	@OriginalMember(owner = "client!ed", name = "ab", descriptor = "Lclient!nh;")
	public OpenGlArrayDrawBuffer vertexBuffer;

	@OriginalMember(owner = "client!ed", name = "bb", descriptor = "Lclient!nh;")
	public OpenGlArrayDrawBuffer textureCoordinatesBuffer;

	@OriginalMember(owner = "client!ed", name = "db", descriptor = "Lclient!hca;")
	private ArrayBufferGL dataArrayBuffer;

	@OriginalMember(owner = "client!ed", name = "eb", descriptor = "I")
	private int maxTileVertexCount;

	@OriginalMember(owner = "client!ed", name = "gb", descriptor = "[Lclient!jn;")
	private GlGroundLayer[] layers;

	@OriginalMember(owner = "client!ed", name = "ib", descriptor = "I")
	private int totalTileVertexCount;

	@OriginalMember(owner = "client!ed", name = "G", descriptor = "Lclient!uh;")
	private final Deque lights = new Deque();

	@OriginalMember(owner = "client!ed", name = "W", descriptor = "Lclient!qo;")
	public final GlToolkit glToolkit;

	@OriginalMember(owner = "client!ed", name = "O", descriptor = "I")
	private final int quarterTileScale;

	@OriginalMember(owner = "client!ed", name = "v", descriptor = "[[[I")
	private int[][][] tileBlendColours;

	@OriginalMember(owner = "client!ed", name = "q", descriptor = "[[B")
	private final byte[][] tileFlags;

	@OriginalMember(owner = "client!ed", name = "S", descriptor = "[[[I")
	public int[][][] tileOffsetsX;

	@OriginalMember(owner = "client!ed", name = "D", descriptor = "[[[I")
	public int[][][] tileColours;

	@OriginalMember(owner = "client!ed", name = "jb", descriptor = "[[F")
	private float[][] normalX;

	@OriginalMember(owner = "client!ed", name = "t", descriptor = "[[[Lclient!jn;")
	private GlGroundLayer[][][] tileLayers;

	@OriginalMember(owner = "client!ed", name = "I", descriptor = "[[[I")
	private int[][][] tileHeightOffsets;

	@OriginalMember(owner = "client!ed", name = "fb", descriptor = "[[F")
	private float[][] normalY;

	@OriginalMember(owner = "client!ed", name = "X", descriptor = "[[B")
	private byte[][] shadowLevels;

	@OriginalMember(owner = "client!ed", name = "R", descriptor = "[[S")
	public final short[][] tileVertexIndices;

	@OriginalMember(owner = "client!ed", name = "K", descriptor = "I")
	private final int quarterTileUnits;

	@OriginalMember(owner = "client!ed", name = "k", descriptor = "[[[I")
	public int[][][] tileOffsetsZ;

	@OriginalMember(owner = "client!ed", name = "hb", descriptor = "[[F")
	private float[][] normalZ;

	@OriginalMember(owner = "client!ed", name = "l", descriptor = "I")
	public final int featureFlags;

	@OriginalMember(owner = "client!ed", name = "cb", descriptor = "Lclient!eq;")
	private IterableHashTable layerTable;

	@OriginalMember(owner = "client!ed", name = "Q", descriptor = "Lclient!mha;")
	private GlShadowMap shadowMap;

	@OriginalMember(owner = "client!ed", name = "<init>", descriptor = "(Lclient!qo;IIII[[I[[II)V")
	public GlGround(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int groundFlags, @OriginalArg(2) int featureFlags, @OriginalArg(3) int width, @OriginalArg(4) int length, @OriginalArg(5) int[][] tileHeights, @OriginalArg(6) int[][] normalHeights, @OriginalArg(7) int scaling) {
		super(width, length, scaling, tileHeights);
		this.glToolkit = glToolkit;
		this.quarterTileScale = super.tileScale - 2;
		this.tileBlendColours = new int[width][length][];
		this.tileFlags = new byte[width][length];
		this.tileOffsetsX = new int[width][length][];
		this.tileColours = new int[width][length][];
		this.normalX = new float[super.width + 1][super.length + 1];
		this.tileLayers = new GlGroundLayer[width][length][];
		this.tileHeightOffsets = new int[width][length][];
		this.normalY = new float[super.width + 1][super.length + 1];
		this.shadowLevels = new byte[width + 1][length + 1];
		this.tileVertexIndices = new short[length * width][];
		this.quarterTileUnits = 0x1 << this.quarterTileScale;
		this.tileOffsetsZ = new int[width][length][];
		this.normalZ = new float[super.width + 1][super.length + 1];
		this.featureFlags = featureFlags;
		for (@Pc(115) int i = 1; super.length > i; i++) {
			for (@Pc(119) int j = 1; super.width > j; j++) {
				@Pc(138) int heightDeltaX = normalHeights[j + 1][i] - normalHeights[j - 1][i];
				@Pc(155) int heightDeltaZ = normalHeights[j][i + 1] - normalHeights[j][i - 1];
				@Pc(174) float normalScale = (float) (1.0D / Math.sqrt((double) (heightDeltaZ * heightDeltaZ + scaling * scaling * 4 + heightDeltaX * heightDeltaX)));
				this.normalX[j][i] = normalScale * (float) heightDeltaX;
				this.normalY[j][i] = normalScale * (float) (-scaling * 2);
				this.normalZ[j][i] = (float) heightDeltaZ * normalScale;
			}
		}
		this.layerTable = new IterableHashTable(128);
		if ((this.featureFlags & 0x10) != 0) {
			this.shadowMap = new GlShadowMap(this.glToolkit, this);
		}
	}

	@OriginalMember(owner = "client!ed", name = "wa", descriptor = "(Lclient!r;IIIIZ)V")
	@Override
	public void wa(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int heightOffset, @OriginalArg(3) int z, @OriginalArg(4) int arg4, @OriginalArg(5) boolean arg5) {
		if (this.shadowMap != null && shadow != null) {
			@Pc(23) int shadowX = x - (this.glToolkit.sunSlopeX * heightOffset >> 8) >> this.glToolkit.shadowScaleShift;
			@Pc(38) int shadowZ = z - (heightOffset * this.glToolkit.sunSlopeZ >> 8) >> this.glToolkit.shadowScaleShift;
			this.shadowMap.removeShadow(shadow, shadowX, shadowZ);
		}
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(II[I[I[I[I[I[I[I[I[I[I[IIIIZ)V")
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

	@OriginalMember(owner = "client!ed", name = "YA", descriptor = "()V")
	@Override
	public void YA() {
		if (this.totalTileVertexCount <= 0) {
			this.shadowMap = null;
		} else {
			@Pc(22) byte[][] smoothedShadowLevels = new byte[super.width + 1][super.length + 1];
			@Pc(28) int local28;
			for (@Pc(24) int i = 1; super.width > i; i++) {
				for (int tileZ = 1; tileZ < super.length; tileZ++) {
					smoothedShadowLevels[i][tileZ] = (byte) ((this.shadowLevels[i][tileZ + 1] >> 3) + (this.shadowLevels[i][tileZ - 1] >> 2) + (this.shadowLevels[i + 1][tileZ] >> 3) + (this.shadowLevels[i - 1][tileZ] >> 2) + (this.shadowLevels[i][tileZ] >> 1));
				}
			}
			this.layers = new GlGroundLayer[this.layerTable.size()];
			this.layerTable.toArray(this.layers);
			for (int layerIndex = 0; layerIndex < this.layers.length; layerIndex++) {
				this.layers[layerIndex].initColours(this.totalTileVertexCount);
			}
			@Pc(136) int vertexStride = 24;
			if (this.tileWaterDepths != null) {
				vertexStride += 4;
			}
			if ((this.featureFlags & 0x7) != 0) {
				vertexStride += 12;
			}
			@Pc(156) NativeHeapBuffer nativeHeapBuffer = this.glToolkit.nativeHeap.a(this.totalTileVertexCount * vertexStride, false);
			@Pc(161) Stream stream = new Stream(nativeHeapBuffer);
			@Pc(165) GlGroundLayer[] glGroundLayers = new GlGroundLayer[this.totalTileVertexCount];
			@Pc(172) int hashCapacity = Utils.floorPowerOfTwo(this.totalTileVertexCount / 4);
			if (hashCapacity < 1) {
				hashCapacity = 1;
			}
			@Pc(184) IterableHashTable iterableHashTable = new IterableHashTable(hashCapacity);
			@Pc(188) GlGroundLayer[] glGroundLayers2 = new GlGroundLayer[this.maxTileVertexCount];
			@Pc(194) int local194;
			for (@Pc(190) int j = 0; j < super.width; j++) {
				for (int tileZ = 0; tileZ < super.length; tileZ++) {
					if (this.tileColours[j][tileZ] != null) {
						@Pc(210) GlGroundLayer[] glGroundLayers3 = this.tileLayers[j][tileZ];
						@Pc(217) int[] offsetsX = this.tileOffsetsX[j][tileZ];
						@Pc(224) int[] offsetsZ = this.tileOffsetsZ[j][tileZ];
						@Pc(231) int[] blendedColours = this.tileBlendColours[j][tileZ];
						@Pc(238) int[] colours = this.tileColours[j][tileZ];
						@Pc(250) int[] heightOffsets = this.tileHeightOffsets == null ? null : this.tileHeightOffsets[j][tileZ];
						if (blendedColours == null) {
							blendedColours = colours;
						}
						@Pc(266) int[] waterDepths = this.tileWaterDepths == null ? null : this.tileWaterDepths[j][tileZ];
						@Pc(273) float normalX00 = this.normalX[j][tileZ];
						@Pc(280) float normalY00 = this.normalY[j][tileZ];
						@Pc(287) float normalZ00 = this.normalZ[j][tileZ];
						@Pc(296) float normalX01 = this.normalX[j][tileZ + 1];
						@Pc(305) float normalY01 = this.normalY[j][tileZ + 1];
						@Pc(314) float normalZ01 = this.normalZ[j][tileZ + 1];
						@Pc(325) float normalX11 = this.normalX[j + 1][tileZ + 1];
						@Pc(336) float normalY11 = this.normalY[j + 1][tileZ + 1];
						@Pc(347) float normalZ11 = this.normalZ[j + 1][tileZ + 1];
						@Pc(356) float normalX10 = this.normalX[j + 1][tileZ];
						@Pc(365) float normalY10 = this.normalY[j + 1][tileZ];
						@Pc(374) float normalZ10 = this.normalZ[j + 1][tileZ];
						@Pc(382) int shadow00 = smoothedShadowLevels[j][tileZ] & 0xFF;
						@Pc(392) int shadow01 = smoothedShadowLevels[j][tileZ + 1] & 0xFF;
						@Pc(404) int shadow11 = smoothedShadowLevels[j + 1][tileZ + 1] & 0xFF;
						@Pc(414) int shadow10 = smoothedShadowLevels[j + 1][tileZ] & 0xFF;
						@Pc(416) int uniqueLayerCount = 0;
						@Pc(426) int local426;
						label337: for (@Pc(418) int k = 0; k < colours.length; k++) {
							@Pc(424) GlGroundLayer glGroundLayer = glGroundLayers3[k];
							for (int uniqueIndex = 0; uniqueIndex < uniqueLayerCount; uniqueIndex++) {
								if (glGroundLayers2[uniqueIndex] == glGroundLayer) {
									continue label337;
								}
							}
							glGroundLayers2[uniqueLayerCount++] = glGroundLayer;
						}
						@Pc(463) short[] vertexIndices = this.tileVertexIndices[super.width * tileZ + j] = new short[colours.length];
						for (int tileVertex = 0; tileVertex < colours.length; tileVertex++) {
							@Pc(476) int vertexX = (j << super.tileScale) + offsetsX[tileVertex];
							@Pc(486) int vertexZ = (tileZ << super.tileScale) + offsetsZ[tileVertex];
							@Pc(491) int gridX = vertexX >> this.quarterTileScale;
							@Pc(496) int gridZ = vertexZ >> this.quarterTileScale;
							@Pc(500) int colour = colours[tileVertex];
							@Pc(504) int blendedColour = blendedColours[tileVertex];
							@Pc(512) int heightOffset = heightOffsets == null ? 0 : heightOffsets[tileVertex];
							@Pc(530) long vertexKey = (long) (gridX << 16) | (long) colour << 32 | (long) blendedColour << 48 | (long) gridZ;
							@Pc(534) int offsetX = offsetsX[tileVertex];
							@Pc(538) int offsetZ = offsetsZ[tileVertex];
							@Pc(540) byte baseLightness = 74;
							@Pc(542) int rgb = 0;
							@Pc(589) float vertexNormalX;
							@Pc(585) float vertexNormalY;
							@Pc(587) float vertexNormalZ;
							@Pc(626) float local626;
							@Pc(593) int lightness;
							if (offsetX == 0 && offsetZ == 0) {
								vertexNormalY = normalY00;
								vertexNormalX = normalX00;
								lightness = baseLightness - shadow00;
								vertexNormalZ = normalZ00;
							} else if (offsetX == 0 && offsetZ == super.tileUnits) {
								vertexNormalZ = normalZ01;
								vertexNormalY = normalY01;
								lightness = baseLightness - shadow01;
								vertexNormalX = normalX01;
							} else if (super.tileUnits == offsetX && offsetZ == super.tileUnits) {
								vertexNormalX = normalX11;
								vertexNormalY = normalY11;
								vertexNormalZ = normalZ11;
								lightness = baseLightness - shadow11;
							} else if (super.tileUnits == offsetX && offsetZ == 0) {
								vertexNormalY = normalY10;
								vertexNormalZ = normalZ10;
								vertexNormalX = normalX10;
								lightness = baseLightness - shadow10;
							} else {
								@Pc(602) float fracX = (float) offsetX / (float) super.tileUnits;
								@Pc(609) float fracZ = (float) offsetZ / (float) super.tileUnits;
								@Pc(618) float lerpNormalX0 = (normalX10 - normalX00) * fracX + normalX00;
								float lerpNormalY0 = (normalY10 - normalY00) * fracX + normalY00;
								@Pc(635) float lerpNormalZ0 = fracX * (normalZ10 - normalZ00) + normalZ00;
								@Pc(643) float lerpNormalX1 = fracX * (normalX11 - normalX01) + normalX01;
								@Pc(652) float lerpNormalY1 = normalY01 + (normalY11 - normalY01) * fracX;
								@Pc(660) float lerpNormalZ1 = (normalZ11 - normalZ01) * fracX + normalZ01;
								vertexNormalY = lerpNormalY0 + (lerpNormalY1 - lerpNormalY0) * fracZ;
								vertexNormalX = fracZ * (lerpNormalX1 - lerpNormalX0) + lerpNormalX0;
								vertexNormalZ = lerpNormalZ0 + (lerpNormalZ1 - lerpNormalZ0) * fracZ;
								@Pc(698) int lerpShadow0 = ((shadow10 - shadow00) * offsetX >> super.tileScale) + shadow00;
								@Pc(710) int lerpShadow1 = (offsetX * (shadow11 - shadow01) >> super.tileScale) + shadow01;
								lightness = baseLightness - lerpShadow0 - ((lerpShadow1 - lerpShadow0) * offsetZ >> super.tileScale);
							}
							@Pc(763) float lightIntensity = 1.0F;
							if (colour != -1) {
								@Pc(775) int adjustedLightness = (colour & 0x7F) * lightness >> 7;
								if (adjustedLightness < 2) {
									adjustedLightness = 2;
								} else if (adjustedLightness > 126) {
									adjustedLightness = 126;
								}
								if ((this.featureFlags & 0x7) == 0) {
									lightIntensity = this.glToolkit.sunDirection[2] * vertexNormalZ + vertexNormalX * this.glToolkit.sunDirection[0] + vertexNormalY * this.glToolkit.sunDirection[1];
									lightIntensity = this.glToolkit.sunBrightness + (lightIntensity > 0.0F ? this.glToolkit.sunColorIntensity : this.glToolkit.antiSunColorIntensity) * lightIntensity;
								}
								rgb = ColorUtil.hslToRgb[colour & 0xFF80 | adjustedLightness];
							}
							@Pc(849) Node node = null;
							if ((vertexX & this.quarterTileUnits - 1) == 0 && (this.quarterTileUnits - 1 & vertexZ) == 0) {
								node = iterableHashTable.get(vertexKey);
							}
							@Pc(882) int vertexIndex;
							@Pc(937) int local937;
							if (node == null) {
								int blendedRgb;
								if (colour == blendedColour) {
									blendedRgb = rgb;
								} else {
									@Pc(915) int blendedLightness = (blendedColour & 0x7F) * lightness >> 7;
									if (blendedLightness < 2) {
										blendedLightness = 2;
									} else if (blendedLightness > 126) {
										blendedLightness = 126;
									}
									blendedRgb = ColorUtil.hslToRgb[blendedColour & 0xFF80 | blendedLightness];
									if ((this.featureFlags & 0x7) == 0) {
										float blendedLightIntensity = this.glToolkit.sunDirection[0] * vertexNormalX + vertexNormalY * this.glToolkit.sunDirection[1] + vertexNormalZ * this.glToolkit.sunDirection[2];
										blendedLightIntensity = (lightIntensity > 0.0F ? this.glToolkit.sunColorIntensity : this.glToolkit.antiSunColorIntensity) * lightIntensity + this.glToolkit.sunBrightness;
										@Pc(993) int red = blendedRgb >> 16 & 0xFF;
										@Pc(999) int green = blendedRgb >> 8 & 0xFF;
										@Pc(1003) int blue = blendedRgb & 0xFF;
										red = (int) ((float) red * blendedLightIntensity);
										green = (int) ((float) green * blendedLightIntensity);
										if (red < 0) {
											red = 0;
										} else if (red > 255) {
											red = 255;
										}
										if (green < 0) {
											green = 0;
										} else if (green > 255) {
											green = 255;
										}
										blue = (int) ((float) blue * blendedLightIntensity);
										if (blue < 0) {
											blue = 0;
										} else if (blue > 255) {
											blue = 255;
										}
										blendedRgb = red << 16 | green << 8 | blue;
									}
								}
								if (this.glToolkit.bigEndianess) {
									stream.a((float) vertexX);
									stream.a((float) (this.averageHeight(vertexX, vertexZ) + heightOffset));
									stream.a((float) vertexZ);
									stream.f((byte) (blendedRgb >> 16));
									stream.f((byte) (blendedRgb >> 8));
									stream.f((byte) blendedRgb);
									stream.f(-1);
									stream.a((float) vertexX);
									stream.a((float) vertexZ);
									if (this.tileWaterDepths != null) {
										stream.a((float) (waterDepths == null ? 0 : waterDepths[tileVertex] - 1));
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
									stream.f((byte) (blendedRgb >> 16));
									stream.f((byte) (blendedRgb >> 8));
									stream.f((byte) blendedRgb);
									stream.f(-1);
									stream.b((float) vertexX);
									stream.b((float) vertexZ);
									if (this.tileWaterDepths != null) {
										stream.b((float) (waterDepths == null ? 0 : waterDepths[tileVertex] - 1));
									}
									if ((this.featureFlags & 0x7) != 0) {
										stream.b(vertexNormalX);
										stream.b(vertexNormalY);
										stream.b(vertexNormalZ);
									}
								}
								vertexIndex = this.vertexCount++;
								vertexIndices[tileVertex] = (short) vertexIndex;
								if (colour != -1) {
									glGroundLayers[vertexIndex] = glGroundLayers3[tileVertex];
								}
								iterableHashTable.put(vertexKey, new ShortNode(vertexIndices[tileVertex]));
							} else {
								vertexIndices[tileVertex] = ((ShortNode) node).value;
								vertexIndex = vertexIndices[tileVertex] & 0xFFFF;
								if (colour != -1 && glGroundLayers[vertexIndex].key > glGroundLayers3[tileVertex].key) {
									glGroundLayers[vertexIndex] = glGroundLayers3[tileVertex];
								}
							}
							for (int uniqueIndex = 0; uniqueIndex < uniqueLayerCount; uniqueIndex++) {
								glGroundLayers2[uniqueIndex].setVertexColour(vertexIndex, lightness, rgb, lightIntensity);
							}
							this.indexCount++;
						}
					}
				}
			}
			for (int vertex = 0; vertex < this.vertexCount; vertex++) {
				@Pc(1327) GlGroundLayer glGroundLayer2 = glGroundLayers[vertex];
				if (glGroundLayer2 != null) {
					glGroundLayer2.setVertexOpaque(vertex);
				}
			}
			@Pc(1360) int local1360;
			for (@Pc(1340) int m = 0; m < super.width; m++) {
				for (@Pc(1344) int z = 0; z < super.length; z++) {
					@Pc(1356) short[] triangleVertices = this.tileVertexIndices[z * super.width + m];
					if (triangleVertices != null) {
						int triangleIndex = 0;
						@Pc(1362) int position = 0;
						while (triangleVertices.length > position) {
							@Pc(1371) int vertexA = triangleVertices[position++] & 0xFFFF;
							@Pc(1378) int vertexB = triangleVertices[position++] & 0xFFFF;
							@Pc(1385) int vertexC = triangleVertices[position++] & 0xFFFF;
							@Pc(1389) GlGroundLayer glGroundLayer3 = glGroundLayers[vertexA];
							@Pc(1393) GlGroundLayer glGroundLayer4 = glGroundLayers[vertexB];
							@Pc(1397) GlGroundLayer glGroundLayer5 = glGroundLayers[vertexC];
							@Pc(1399) GlGroundLayer glGroundLayer6 = null;
							if (glGroundLayer3 != null) {
								glGroundLayer3.addTriangle(m, z, triangleIndex);
								glGroundLayer6 = glGroundLayer3;
							}
							if (glGroundLayer4 != null) {
								glGroundLayer4.addTriangle(m, z, triangleIndex);
								if (glGroundLayer6 == null || glGroundLayer6.key > glGroundLayer4.key) {
									glGroundLayer6 = glGroundLayer4;
								}
							}
							if (glGroundLayer5 != null) {
								glGroundLayer5.addTriangle(m, z, triangleIndex);
								if (glGroundLayer6 == null || glGroundLayer5.key < glGroundLayer6.key) {
									glGroundLayer6 = glGroundLayer5;
								}
							}
							if (glGroundLayer6 != null) {
								if (glGroundLayer3 != null) {
									glGroundLayer6.setVertexOpaque(vertexA);
								}
								if (glGroundLayer4 != null) {
									glGroundLayer6.setVertexOpaque(vertexB);
								}
								if (glGroundLayer5 != null) {
									glGroundLayer6.setVertexOpaque(vertexC);
								}
								glGroundLayer6.addTriangle(m, z, triangleIndex);
							}
							triangleIndex++;
						}
					}
				}
			}
			stream.a();
			this.dataArrayBuffer = this.glToolkit.createDataArrayBuffer(nativeHeapBuffer, vertexStride, stream.b());
			this.vertexBuffer = new OpenGlArrayDrawBuffer(this.dataArrayBuffer, 5126, 3, 0);
			this.colorBuffer = new OpenGlArrayDrawBuffer(this.dataArrayBuffer, 5121, 4, 12);
			@Pc(1528) byte normalsOffset;
			if (this.tileWaterDepths == null) {
				normalsOffset = 24;
				this.textureCoordinatesBuffer = new OpenGlArrayDrawBuffer(this.dataArrayBuffer, 5126, 2, 16);
			} else {
				normalsOffset = 28;
				this.textureCoordinatesBuffer = new OpenGlArrayDrawBuffer(this.dataArrayBuffer, 5126, 3, 16);
			}
			if ((this.featureFlags & 0x7) != 0) {
				this.normalsBuffer = new OpenGlArrayDrawBuffer(this.dataArrayBuffer, 5126, 3, normalsOffset);
			}
			@Pc(1572) long[] layerPointers = new long[this.layers.length];
			for (int layerIndex = 0; layerIndex < this.layers.length; layerIndex++) {
				@Pc(1581) GlGroundLayer glGroundLayer7 = this.layers[layerIndex];
				layerPointers[layerIndex] = glGroundLayer7.key;
				glGroundLayer7.finishColours(this.vertexCount);
			}
			ArrayUtils.quicksort(this.layers, layerPointers);
			if (this.shadowMap != null) {
				this.shadowMap.initializeShadowTiles();
			}
		}
		this.tileHeightOffsets = null;
		this.normalX = this.normalY = this.normalZ = null;
		this.tileWaterDepths = null;
		this.tileLayers = null;
		this.tileColours = null;
		this.shadowLevels = null;
		this.layerTable = null;
		this.tileBlendColours = null;
		this.tileOffsetsX = this.tileOffsetsZ = null;
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(BIILclient!uc;)V")
	private void projectShadow(@OriginalArg(1) int tileX, @OriginalArg(2) int tileZ, @OriginalArg(3) GlShadow shadow) {
		@Pc(12) int[] offsetsX = this.tileOffsetsX[tileX][tileZ];
		@Pc(26) int[] offsetsZ = this.tileOffsetsZ[tileX][tileZ];
		@Pc(29) int length = offsetsX.length;
		if (this.glToolkit.scratchVerticesX.length < length) {
			this.glToolkit.scratchVerticesY = new int[length];
			this.glToolkit.scratchVerticesX = new int[length];
		}
		@Pc(49) int[] projectedX = this.glToolkit.scratchVerticesX;
		@Pc(53) int[] projectedZ = this.glToolkit.scratchVerticesY;
		for (@Pc(55) int i = 0; i < length; i++) {
			projectedX[i] = offsetsX[i] >> this.glToolkit.shadowScaleShift;
			projectedZ[i] = offsetsZ[i] >> this.glToolkit.shadowScaleShift;
		}
		@Pc(83) int index = 0;
		while (index < length) {
			@Pc(89) int xA = projectedX[index];
			@Pc(94) int zA = projectedZ[index++];
			@Pc(98) int xB = projectedX[index];
			@Pc(103) int zB = projectedZ[index++];
			@Pc(107) int xC = projectedX[index];
			@Pc(112) int zC = projectedZ[index++];
			if ((xA - xB) * (zB - zC) - (xC - xB) * (zB - zA) > 0) {
				shadow.fillTriangle(xC, xA, zA, xB, zC, zB);
			}
		}
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(II)V")
	@Override
	public void renderTile(@OriginalArg(0) int x, @OriginalArg(1) int z) {
	}

	@OriginalMember(owner = "client!ed", name = "CA", descriptor = "(Lclient!r;IIIIZ)V")
	@Override
	public void CA(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int heightOffset, @OriginalArg(3) int z, @OriginalArg(4) int arg4, @OriginalArg(5) boolean arg5) {
		if (this.shadowMap != null && shadow != null) {
			@Pc(23) int shadowX = x - (heightOffset * this.glToolkit.sunSlopeX >> 8) >> this.glToolkit.shadowScaleShift;
			@Pc(38) int shadowZ = z - (this.glToolkit.sunSlopeZ * heightOffset >> 8) >> this.glToolkit.shadowScaleShift;
			this.shadowMap.addShadow(shadowX, shadowZ, shadow);
		}
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(Lclient!r;IIIIZ)Z")
	@Override
	public boolean method8002(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int heightOffset, @OriginalArg(3) int z) {
		if (this.shadowMap == null || shadow == null) {
			return false;
		} else {
			@Pc(25) int shadowX = x - (heightOffset * this.glToolkit.sunSlopeX >> 8) >> this.glToolkit.shadowScaleShift;
			@Pc(40) int shadowZ = z - (this.glToolkit.sunSlopeZ * heightOffset >> 8) >> this.glToolkit.shadowScaleShift;
			return this.shadowMap.shouldApplyShadow(shadowX, shadowZ, shadow);
		}
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(III[[ZZI)V")
	@Override
	public void renderTiles(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int radius, @OriginalArg(3) boolean[][] visibility, @OriginalArg(4) boolean belowLowestPlane, @OriginalArg(5) int skipFlags) {
		this.renderGround(-1, radius, belowLowestPlane, x, skipFlags, visibility, z);
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(IIIIIII[[Z)V")
	@Override
	public void drawMinimap(@OriginalArg(3) int x1, @OriginalArg(4) int y1, @OriginalArg(5) int x2, @OriginalArg(6) int y2, @OriginalArg(7) boolean[][] visibility) {
		if (this.totalTileVertexCount <= 0) {
			return;
		}
		this.glToolkit.setOrthoMatrixMode();
		this.glToolkit.toggleFog(false);
		this.glToolkit.setLightingEnabled(false);
		this.glToolkit.toggleZbuffer(false);
		this.glToolkit.setDepthMask(false);
		this.glToolkit.setBlendMode(0);
		this.glToolkit.renderTexture(-2);
		this.glToolkit.setActiveTexture(null);
		GlGround.minimapMatrix[0] = (float) 1024 / ((float) this.glToolkit.viewportWidth * (float) super.tileUnits * 128.0F);
		GlGround.minimapMatrix[8] = 0.0F;
		GlGround.minimapMatrix[13] = 1.0F - ((float) (y2 * 1024) / 128.0F + (float) 0) / (float) this.glToolkit.viewportHeight;
		GlGround.minimapMatrix[7] = 0.0F;
		GlGround.minimapMatrix[6] = 0.0F;
		GlGround.minimapMatrix[5] = (float) 1024 / ((float) this.glToolkit.viewportHeight * (float) super.tileUnits * 128.0F);
		GlGround.minimapMatrix[1] = 0.0F;
		GlGround.minimapMatrix[4] = 0.0F;
		GlGround.minimapMatrix[14] = 0.0F;
		GlGround.minimapMatrix[12] = -(((float) (x1 * 1024) / 128.0F - (float) 0) / (float) this.glToolkit.viewportWidth) - 1.0F;
		GlGround.minimapMatrix[10] = 0.0F;
		GlGround.minimapMatrix[15] = 1.0F;
		GlGround.minimapMatrix[3] = 0.0F;
		GlGround.minimapMatrix[2] = 0.0F;
		GlGround.minimapMatrix[9] = 0.0F;
		GlGround.minimapMatrix[11] = 0.0F;
		OpenGL.glMatrixMode(OpenGLConstants.GL_PROJECTION);
		OpenGL.glLoadMatrixf(GlGround.minimapMatrix, 0);
		GlGround.minimapMatrix[12] = 0.0F;
		GlGround.minimapMatrix[4] = 0.0F;
		GlGround.minimapMatrix[0] = 1.0F;
		GlGround.minimapMatrix[14] = 0.0F;
		GlGround.minimapMatrix[6] = 1.0F;
		GlGround.minimapMatrix[8] = 0.0F;
		GlGround.minimapMatrix[11] = 0.0F;
		GlGround.minimapMatrix[13] = 0.0F;
		GlGround.minimapMatrix[7] = 0.0F;
		GlGround.minimapMatrix[5] = 0.0F;
		GlGround.minimapMatrix[15] = 1.0F;
		GlGround.minimapMatrix[2] = 0.0F;
		GlGround.minimapMatrix[3] = 0.0F;
		GlGround.minimapMatrix[9] = 1.0F;
		GlGround.minimapMatrix[10] = 0.0F;
		GlGround.minimapMatrix[1] = 0.0F;
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		OpenGL.glLoadMatrixf(GlGround.minimapMatrix, 0);
		if ((this.featureFlags & 0x7) == 0) {
			this.glToolkit.setLightingEnabled(false);
		} else {
			this.glToolkit.setLightingEnabled(true);
			this.glToolkit.setLightingSourceParams();
		}
		this.glToolkit.toggleDrawingClientstates(this.vertexBuffer, this.colorBuffer, this.textureCoordinatesBuffer, this.normalsBuffer);
		if (this.indexCount * 2 > this.glToolkit.textureBuffer.buffer.length) {
			this.glToolkit.textureBuffer = new GlBitBuffer(this.indexCount * 2);
		} else {
			this.glToolkit.textureBuffer.currentPosition = 0;
		}
		@Pc(314) int elementCount = 0;
		@Pc(318) GlBitBuffer gpuPacket = this.glToolkit.textureBuffer;
		@Pc(324) int z;
		@Pc(333) int tileIndex;
		@Pc(335) int x;
		@Pc(354) short[] vertexIndices;
		@Pc(358) int i;
		if (this.glToolkit.bigEndianess) {
			for (z = y1; z < y2; z++) {
				tileIndex = z * super.width + x1;
				for (x = x1; x < x2; x++) {
					if (visibility[x - x1][z - y1]) {
						vertexIndices = this.tileVertexIndices[tileIndex];
						if (vertexIndices != null) {
							for (i = 0; i < vertexIndices.length; i++) {
								gpuPacket.writeShort(vertexIndices[i] & 0xFFFF);
								elementCount++;
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
							for (i = 0; i < vertexIndices.length; i++) {
								gpuPacket.writeShortLE(vertexIndices[i] & 0xFFFF);
								elementCount++;
							}
						}
					}
					tileIndex++;
				}
			}
		}
		if (elementCount > 0) {
			@Pc(470) GlElementsArrayBuffer indexBuffer = new GlElementsArrayBuffer(this.glToolkit, 5123, gpuPacket.buffer, gpuPacket.currentPosition);
			this.glToolkit.drawElements(elementCount, indexBuffer, 0);
		}
	}

	@OriginalMember(owner = "client!ed", name = "fa", descriptor = "(IILclient!r;)Lclient!r;")
	@Override
	public Shadow fa(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) Shadow shadow) {
		if ((this.tileFlags[x][z] & 0x1) == 0) {
			return null;
		}
		@Pc(24) int shadowSize = super.tileUnits >> this.glToolkit.shadowScaleShift;
		@Pc(27) GlShadow glShadow = (GlShadow) shadow;
		@Pc(43) GlShadow glShadow2;
		if (glShadow != null && glShadow.fitsDimension(shadowSize, shadowSize)) {
			glShadow2 = glShadow;
			glShadow.reset();
		} else {
			glShadow2 = new GlShadow(this.glToolkit, shadowSize, shadowSize);
		}
		glShadow2.setDimension(shadowSize, shadowSize, 0, 0);
		this.projectShadow(x, z, glShadow2);
		return glShadow2;
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(III[[ZZII)V")
	@Override
	public void renderFloor(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int radius, @OriginalArg(3) boolean[][] visibility, @OriginalArg(4) boolean belowLowestPlane, @OriginalArg(5) int orthoZoom, @OriginalArg(6) int skipFlags) {
		this.renderGround(orthoZoom, radius, belowLowestPlane, x, skipFlags, visibility, z);
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(Lclient!ab;[I)V")
	@Override
	public void addLight(@OriginalArg(0) PointLight pointLight, @OriginalArg(1) int[] tileMask) {
		this.lights.addLast(new LightGL(this.glToolkit, this, pointLight, tileMask));
	}

	@OriginalMember(owner = "client!ed", name = "U", descriptor = "(II[I[I[I[I[I[I[I[IIIIZ)V")
	@Override
	public void U(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int[] offsetsX, @OriginalArg(3) int[] heightOffsets, @OriginalArg(4) int[] offsetsZ, @OriginalArg(5) int[] waterDepths, @OriginalArg(6) int[] colours, @OriginalArg(7) int[] blendedColours, @OriginalArg(8) int[] materialIds, @OriginalArg(9) int[] textureScales, @OriginalArg(10) int waterColour, @OriginalArg(11) int waterDepth, @OriginalArg(12) int waterBias, @OriginalArg(13) boolean hasShadow) {
		if (heightOffsets != null && this.tileHeightOffsets == null) {
			this.tileHeightOffsets = new int[super.width][super.length][];
		}
		@Pc(21) MaterialProvider textureSource = this.glToolkit.materialProvider;
		if (waterDepths != null && this.tileWaterDepths == null) {
			this.tileWaterDepths = new int[super.width][super.length][];
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
		@Pc(92) GlGroundLayer[] glGroundLayers = this.tileLayers[x][z] = new GlGroundLayer[colours.length];
		for (@Pc(94) int i = 0; i < colours.length; i++) {
			@Pc(100) int materialId = materialIds[i];
			@Pc(104) int textureScale = textureScales[i];
			if ((this.featureFlags & 0x20) != 0 && materialId != -1 && textureSource.getMetrics(materialId).detailsOnly) {
				materialId = -1;
				textureScale = 128;
			}
			@Pc(148) long layerKey = (long) waterColour << 28 | (long) waterDepth << 42 | (long) waterBias << 48 | (long) (textureScale << 14) | (long) materialId;
			@Pc(154) Node node;
			for (node = this.layerTable.get(layerKey); node != null; node = this.layerTable.nextWithKey()) {
				@Pc(159) GlGroundLayer glGroundLayer = (GlGroundLayer) node;
				if (materialId == glGroundLayer.materialId && glGroundLayer.textureScale == (float) textureScale && glGroundLayer.waterFogColour == waterColour && waterDepth == glGroundLayer.waterDepth && waterBias == glGroundLayer.waterIntensity) {
					break;
				}
			}
			if (node == null) {
				glGroundLayers[i] = new GlGroundLayer(this, materialId, textureScale, waterColour, waterDepth, waterBias);
				this.layerTable.put(layerKey, glGroundLayers[i]);
			} else {
				glGroundLayers[i] = (GlGroundLayer) node;
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

	@OriginalMember(owner = "client!ed", name = "ka", descriptor = "(III)V")
	@Override
	public void ka(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int shadowLevel) {
		if (shadowLevel > (this.shadowLevels[x][z] & 0xFF)) {
			this.shadowLevels[x][z] = (byte) shadowLevel;
		}
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(IIZIII[[ZI)V")
	private void renderGround(@OriginalArg(0) int orthoZoom, @OriginalArg(1) int radius, @OriginalArg(2) boolean belowLowestPlane, @OriginalArg(4) int x, @OriginalArg(5) int skipFlags, @OriginalArg(6) boolean[][] visibility, @OriginalArg(7) int z) {
		if (this.layers == null) {
			return;
		}
		@Pc(20) int diameter = radius + radius + 1;
		@Pc(24) int maxVisibleTiles = diameter * diameter;
		if (this.glToolkit.visibleTileIndices.length < maxVisibleTiles) {
			this.glToolkit.visibleTileIndices = new int[maxVisibleTiles];
		}
		if (this.glToolkit.textureBuffer.buffer.length < this.indexCount * 2) {
			this.glToolkit.textureBuffer = new GlBitBuffer(this.indexCount * 2);
		}
		@Pc(64) int minX = x - radius;
		@Pc(66) int originX = minX;
		if (minX < 0) {
			minX = 0;
		}
		@Pc(74) int minZ = z - radius;
		@Pc(76) int originZ = minZ;
		if (minZ < 0) {
			minZ = 0;
		}
		@Pc(87) int maxX = radius + x;
		if (maxX > super.width - 1) {
			maxX = super.width - 1;
		}
		@Pc(102) int maxZ = z + radius;
		if (maxZ > super.length - 1) {
			maxZ = super.length - 1;
		}
		@Pc(119) int visibleTileCount = 0;
		@Pc(123) int[] visibleTileIndices = this.glToolkit.visibleTileIndices;
		@Pc(136) int local136;
		for (@Pc(125) int i = minX; i <= maxX; i++) {
			@Pc(134) boolean[] visibilityRow = visibility[i - originX];
			for (int tileZ = minZ; tileZ <= maxZ; tileZ++) {
				if (visibilityRow[tileZ - originZ]) {
					visibleTileIndices[visibleTileCount++] = i + tileZ * super.width;
				}
			}
		}
		if (orthoZoom == -1) {
			this.glToolkit.setupMatrixRendering();
		} else {
			this.glToolkit.setOrthoZoom((float) orthoZoom);
			this.glToolkit.setupOrthoMatrixRendering();
		}
		this.glToolkit.setLightingEnabled((this.featureFlags & 0x7) != 0);
		for (@Pc(207) int j = 0; j < this.layers.length; j++) {
			this.layers[j].render(visibleTileCount, visibleTileIndices);
		}
		if (!this.lights.isEmpty()) {
			int fogColour = this.glToolkit.fogColor;
			@Pc(236) int fogDensity = this.glToolkit.fogDensity;
			this.glToolkit.L(0, fogDensity, this.glToolkit.fogOffset);
			this.glToolkit.setLightingEnabled(false);
			this.glToolkit.setDepthMask(false);
			this.glToolkit.setBlendMode(128);
			this.glToolkit.renderTexture(-2);
			this.glToolkit.setActiveTexture(this.glToolkit.texture2d);
			this.glToolkit.loadTextureCombineMode(8448, 7681);
			this.glToolkit.setTexture(34166, 770, 0);
			this.glToolkit.setTextureTransparency(0, 34167);
			for (@Pc(296) Node node = this.lights.first(); node != null; node = this.lights.next()) {
				@Pc(301) LightGL glGroundLight = (LightGL) node;
				glGroundLight.drawLightMesh(visibility, x, z, radius);
			}
			this.glToolkit.setTexture(5890, 768, 0);
			this.glToolkit.setTextureTransparency(0, 5890);
			this.glToolkit.setActiveTexture(null);
			this.glToolkit.L(fogColour, fogDensity, this.glToolkit.fogOffset);
		}
		if (this.shadowMap == null) {
			return;
		}
		OpenGL.glPushMatrix();
		OpenGL.glTranslatef(0.0F, -1.0F, 0.0F);
		this.glToolkit.toggleDrawingClientstates(this.vertexBuffer, null, this.textureCoordinatesBuffer, null);
		this.shadowMap.renderShadowOverlay(belowLowestPlane, visibility, radius, z, x);
		OpenGL.glPopMatrix();
		return;
	}
	@OriginalMember(owner = "client!kk", name = "a", descriptor = "[F")
	public static final float[] minimapMatrix = new float[16];
}
