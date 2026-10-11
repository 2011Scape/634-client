import com.jagex.game.runetek5.JavaComplexBlendedTile;
import com.jagex.game.runetek5.JavaGenericBlendedTile;
import com.jagex.game.runetek5.JavaMatrix;
import com.jagex.game.runetek5.JavaSimpleBlendedTile;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.JavaComplexTile;
import com.jagex.game.runetek5.scene.tile.PlainTile;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.graphics.safe.PureJavaFloorModel;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.ColorUtil;
import com.jagex.game.runetek5.math.ColourUtils;

@OriginalClass("client!cr")
public final class JavaGround extends Ground {

	@OriginalMember(owner = "client!cr", name = "k", descriptor = "F")
	private float cameraE1_1;

	@OriginalMember(owner = "client!cr", name = "l", descriptor = "F")
	private float cameraTx;

	@OriginalMember(owner = "client!cr", name = "m", descriptor = "F")
	private float cameraE2_1;

	@OriginalMember(owner = "client!cr", name = "n", descriptor = "[[Lclient!mga;")
	private PlainTile[][] simpleTiles;

	@OriginalMember(owner = "client!cr", name = "o", descriptor = "F")
	private float cameraE1_2;

	@OriginalMember(owner = "client!cr", name = "q", descriptor = "F")
	private float cameraTz;

	@OriginalMember(owner = "client!cr", name = "r", descriptor = "[[Lclient!rb;")
	private JavaGenericBlendedTile[][] genericBlendedTiles;

	@OriginalMember(owner = "client!cr", name = "t", descriptor = "F")
	private float cameraE2_2;

	@OriginalMember(owner = "client!cr", name = "u", descriptor = "[[Lclient!sp;")
	private JavaComplexTile[][] complexTiles;

	@OriginalMember(owner = "client!cr", name = "w", descriptor = "F")
	private float cameraE1_3;

	@OriginalMember(owner = "client!cr", name = "x", descriptor = "[[Lclient!ofa;")
	private JavaComplexBlendedTile[][] complexBlendedTiles;

	@OriginalMember(owner = "client!cr", name = "y", descriptor = "F")
	private float cameraTy;

	@OriginalMember(owner = "client!cr", name = "z", descriptor = "F")
	private float cameraE3_2;

	@OriginalMember(owner = "client!cr", name = "B", descriptor = "F")
	private float cameraE2_3;

	@OriginalMember(owner = "client!cr", name = "C", descriptor = "[[Lclient!ul;")
	private JavaSimpleBlendedTile[][] simpleBlendedTiles;

	@OriginalMember(owner = "client!cr", name = "D", descriptor = "F")
	private float cameraE3_3;

	@OriginalMember(owner = "client!cr", name = "F", descriptor = "F")
	private float cameraE3_1;

	@OriginalMember(owner = "client!cr", name = "A", descriptor = "I")
	private int depthOverride = -1;

	@OriginalMember(owner = "client!cr", name = "p", descriptor = "Lclient!ao;")
	private final JavaToolkit toolkit;

	@OriginalMember(owner = "client!cr", name = "v", descriptor = "I")
	private final int featureFlags;

	@OriginalMember(owner = "client!cr", name = "s", descriptor = "[[B")
	private byte[][] lightLevels;

	@OriginalMember(owner = "client!cr", name = "E", descriptor = "[[B")
	private byte[][] shadowLevels;

	@OriginalMember(owner = "client!cr", name = "<init>", descriptor = "(Lclient!ao;IIII[[I[[II)V")
	public JavaGround(@OriginalArg(0) JavaToolkit javaToolkit, @OriginalArg(1) int groundFlags, @OriginalArg(2) int featureFlags, @OriginalArg(3) int width, @OriginalArg(4) int length, @OriginalArg(5) int[][] tileHeights, @OriginalArg(6) int[][] heights, @OriginalArg(7) int scaling) {
		super(width, length, scaling, tileHeights);
		this.toolkit = javaToolkit;
		this.featureFlags = featureFlags;
		this.lightLevels = new byte[width + 1][length + 1];
		@Pc(29) int ambient = this.toolkit.ambient >> 9;
		for (@Pc(31) int i = 1; i < length; i++) {
			for (@Pc(34) int j = 1; j < width; j++) {
				@Pc(53) int dx = heights[j + 1][i] - heights[j - 1][i];
				@Pc(69) int dz = heights[j][i + 1] - heights[j][i - 1];
				@Pc(84) int magnitude = (int) Math.sqrt((double) (dx * dx + scaling * 512 + dz * dz));
				@Pc(90) int normalX = (dx << 8) / magnitude;
				@Pc(96) int normalY = scaling * -512 / magnitude;
				@Pc(102) int normalZ = (dz << 8) / magnitude;
				@Pc(124) int light = ambient + (this.toolkit.sunX * normalX + this.toolkit.sunY * normalY + this.toolkit.sunZ * normalZ >> 17);
				light >>= 0x1;
				if (light < 2) {
					light = 2;
				} else if (light > 126) {
					light = 126;
				}
				this.lightLevels[j][i] = (byte) light;
			}
		}
		this.shadowLevels = new byte[width + 1][length + 1];
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(Lclient!ab;[I)V")
	@Override
	public void addLight(@OriginalArg(0) PointLight pointLight, @OriginalArg(1) int[] tileMask) {
	}

	@OriginalMember(owner = "client!cr", name = "U", descriptor = "(II[I[I[I[I[I[I[I[IIIIZ)V")
	@Override
	public void U(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int[] offsetsX, @OriginalArg(3) int[] heightOffsets, @OriginalArg(4) int[] offsetsZ, @OriginalArg(5) int[] waterDepths, @OriginalArg(6) int[] colours, @OriginalArg(7) int[] blendedColours, @OriginalArg(8) int[] textures, @OriginalArg(9) int[] sizes, @OriginalArg(10) int fogColour, @OriginalArg(11) int maxWaterDepth, @OriginalArg(12) int waterBias, @OriginalArg(13) boolean hasShadow) {
		@Pc(8) boolean generic = (this.featureFlags & 0x20) == 0;
		if (this.simpleBlendedTiles == null && !generic) {
			this.simpleBlendedTiles = new JavaSimpleBlendedTile[super.width][super.length];
			this.complexBlendedTiles = new JavaComplexBlendedTile[super.width][super.length];
		} else if (this.genericBlendedTiles == null && generic) {
			this.genericBlendedTiles = new JavaGenericBlendedTile[super.width][super.length];
		} else if (this.simpleTiles != null) {
			throw new IllegalStateException();
		}
		if (offsetsX == null || offsetsX.length == 0) {
			return;
		}
		for (@Pc(56) int i = 0; i < colours.length; i++) {
			if (colours[i] == -1) {
				colours[i] = 0;
			} else {
				colours[i] = ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(colours[i]) & 0xFFFF] << 8 | 0xFF;
			}
		}
		if (blendedColours != null) {
			for (@Pc(92) int j = 0; j < blendedColours.length; j++) {
				if (blendedColours[j] == -1) {
					blendedColours[j] = 0;
				} else {
					blendedColours[j] = ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(blendedColours[j]) & 0xFFFF] << 8 | 0xFF;
				}
			}
		}
		@Pc(186) int local186;
		@Pc(191) int local191;
		@Pc(195) int local195;
		@Pc(215) int local215;
		@Pc(336) int local336;
		@Pc(383) int local383;
		@Pc(467) int local467;
		@Pc(499) int local499;
		if (generic) {
			@Pc(130) JavaGenericBlendedTile javaGenericBlendedTile = new JavaGenericBlendedTile();
			javaGenericBlendedTile.vertexCount = (short) offsetsX.length;
			javaGenericBlendedTile.faceCount = (short) (offsetsX.length / 3);
			javaGenericBlendedTile.verticesX = new short[javaGenericBlendedTile.vertexCount];
			javaGenericBlendedTile.verticesY = new short[javaGenericBlendedTile.vertexCount];
			javaGenericBlendedTile.verticesZ = new short[javaGenericBlendedTile.vertexCount];
			javaGenericBlendedTile.vertexColours = new int[javaGenericBlendedTile.vertexCount];
			javaGenericBlendedTile.vertexTextures = new short[javaGenericBlendedTile.vertexCount];
			javaGenericBlendedTile.vertexSizes = new short[javaGenericBlendedTile.vertexCount];
			javaGenericBlendedTile.vertexLight = new byte[javaGenericBlendedTile.vertexCount];
			if (waterDepths != null) {
				javaGenericBlendedTile.waterDepths = new short[javaGenericBlendedTile.vertexCount];
			}
			for (int vertexIndex = 0; vertexIndex < javaGenericBlendedTile.vertexCount; vertexIndex++) {
				int vertexX = offsetsX[vertexIndex];
				int vertexZ = offsetsZ[vertexIndex];
				int vertexLightLevel;
				if (vertexX == 0 && vertexZ == 0) {
					vertexLightLevel = this.lightLevels[x][z] - this.shadowLevels[x][z];
				} else if (vertexX == 0 && vertexZ == super.tileUnits) {
					vertexLightLevel = this.lightLevels[x][z + 1] - this.shadowLevels[x][z + 1];
				} else if (vertexX == super.tileUnits && vertexZ == super.tileUnits) {
					vertexLightLevel = this.lightLevels[x + 1][z + 1] - this.shadowLevels[x + 1][z + 1];
				} else if (vertexX == super.tileUnits && vertexZ == 0) {
					vertexLightLevel = this.lightLevels[x + 1][z] - this.shadowLevels[x + 1][z];
				} else {
					int southLight = (this.lightLevels[x][z] - this.shadowLevels[x][z]) * (super.tileUnits - vertexX) + (this.lightLevels[x + 1][z] - this.shadowLevels[x + 1][z]) * vertexX;
					int northLight = (this.lightLevels[x][z + 1] - this.shadowLevels[x][z + 1]) * (super.tileUnits - vertexX) + (this.lightLevels[x + 1][z + 1] - this.shadowLevels[x + 1][z + 1]) * vertexX;
					vertexLightLevel = southLight * (super.tileUnits - vertexZ) + northLight * vertexZ >> super.tileScale * 2;
				}
				int worldX = (x << super.tileScale) + vertexX;
				int worldZ = (z << super.tileScale) + vertexZ;
				javaGenericBlendedTile.verticesX[vertexIndex] = (short) vertexX;
				javaGenericBlendedTile.verticesZ[vertexIndex] = (short) vertexZ;
				javaGenericBlendedTile.verticesY[vertexIndex] = (short) (this.averageHeight(worldX, worldZ) + (heightOffsets == null ? 0 : heightOffsets[vertexIndex]));
				if (vertexLightLevel < 0) {
					vertexLightLevel = 0;
				}
				if (colours[vertexIndex] == 0) {
					javaGenericBlendedTile.vertexColours[vertexIndex] = 0;
					if (blendedColours != null) {
						javaGenericBlendedTile.vertexLight[vertexIndex] = (byte) vertexLightLevel;
					}
				} else {
					int fogAlpha = 0;
					if (waterDepths != null) {
						@Pc(479) short waterDepth = javaGenericBlendedTile.waterDepths[vertexIndex] = (short) waterDepths[vertexIndex];
						if (maxWaterDepth != 0) {
							fogAlpha = waterDepth * 255 / maxWaterDepth;
							if (fogAlpha < 0) {
								fogAlpha = 0;
							} else if (fogAlpha > 255) {
								fogAlpha = 255;
							}
						}
					}
					int colourAlpha = -16777216;
					if (textures[vertexIndex] != -1 && this.isWaterEffect(this.toolkit.materialProvider.getMetrics(textures[vertexIndex]).effectType)) {
						colourAlpha = -1694498816;
					}
					javaGenericBlendedTile.vertexColours[vertexIndex] = colourAlpha | ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[vertexIndex] >> 8, vertexLightLevel), fogAlpha, fogColour);
					if (blendedColours != null) {
						javaGenericBlendedTile.vertexLight[vertexIndex] = (byte) vertexLightLevel;
					}
				}
				javaGenericBlendedTile.vertexTextures[vertexIndex] = (short) textures[vertexIndex];
				javaGenericBlendedTile.vertexSizes[vertexIndex] = (short) sizes[vertexIndex];
			}
			if (blendedColours != null) {
				javaGenericBlendedTile.faceBlendedColours = new int[javaGenericBlendedTile.faceCount];
			}
			for (int faceIndex = 0; faceIndex < javaGenericBlendedTile.faceCount; faceIndex++) {
				int vertexA = faceIndex * 3;
				if (blendedColours != null && blendedColours[vertexA] != 0) {
					javaGenericBlendedTile.faceBlendedColours[faceIndex] = blendedColours[vertexA] >> 8 | 0xFF000000;
				}
			}
			this.genericBlendedTiles[x][z] = javaGenericBlendedTile;
			return;
		}
		@Pc(610) boolean simple = true;
		int southWestVertex = -1;
		int southEastVertex = -1;
		int northEastVertex = -1;
		int northWestVertex = -1;
		if (offsetsX.length == 6) {
			for (int vertexIndex = 0; vertexIndex < 6; vertexIndex++) {
				if (offsetsX[vertexIndex] == 0 && offsetsZ[vertexIndex] == 0) {
					if (southWestVertex != -1 && colours[southWestVertex] != colours[vertexIndex]) {
						simple = false;
						break;
					}
					southWestVertex = vertexIndex;
				} else if (offsetsX[vertexIndex] == super.tileUnits && offsetsZ[vertexIndex] == 0) {
					if (southEastVertex != -1 && colours[southEastVertex] != colours[vertexIndex]) {
						simple = false;
						break;
					}
					southEastVertex = vertexIndex;
				} else if (offsetsX[vertexIndex] == super.tileUnits && offsetsZ[vertexIndex] == super.tileUnits) {
					if (northEastVertex != -1 && colours[northEastVertex] != colours[vertexIndex]) {
						simple = false;
						break;
					}
					northEastVertex = vertexIndex;
				} else if (offsetsX[vertexIndex] == 0 && offsetsZ[vertexIndex] == super.tileUnits) {
					if (northWestVertex != -1 && colours[northWestVertex] != colours[vertexIndex]) {
						simple = false;
						break;
					}
					northWestVertex = vertexIndex;
				}
			}
			if (southWestVertex == -1 || southEastVertex == -1 || northEastVertex == -1 || northWestVertex == -1) {
				simple = false;
			}
			if (simple) {
				if (heightOffsets != null) {
					for (int vertexIndex = 0; vertexIndex < 4; vertexIndex++) {
						if (heightOffsets[vertexIndex] != 0) {
							simple = false;
							break;
						}
					}
				}
				if (simple) {
					for (int vertexIndex = 1; vertexIndex < 4; vertexIndex++) {
						if (offsetsX[vertexIndex] != offsetsX[0] && offsetsX[vertexIndex] != offsetsX[0] + super.tileUnits && offsetsX[vertexIndex] != offsetsX[0] - super.tileUnits) {
							simple = false;
							break;
						}
						if (offsetsZ[vertexIndex] != offsetsZ[0] && offsetsZ[vertexIndex] != offsetsZ[0] + super.tileUnits && offsetsZ[vertexIndex] != offsetsZ[0] - super.tileUnits) {
							simple = false;
							break;
						}
					}
				}
			}
		} else {
			simple = false;
		}
		if (!simple) {
			@Pc(1623) JavaComplexBlendedTile javaComplexBlendedTile = new JavaComplexBlendedTile();
			javaComplexBlendedTile.vertexCount = (short) offsetsX.length;
			javaComplexBlendedTile.faceCount = (short) (offsetsX.length / 3);
			javaComplexBlendedTile.verticesX = new short[javaComplexBlendedTile.vertexCount];
			javaComplexBlendedTile.verticesY = new short[javaComplexBlendedTile.vertexCount];
			javaComplexBlendedTile.verticesZ = new short[javaComplexBlendedTile.vertexCount];
			javaComplexBlendedTile.vertexColours = new int[javaComplexBlendedTile.vertexCount];
			if (waterDepths != null) {
				javaComplexBlendedTile.waterDepths = new short[javaComplexBlendedTile.vertexCount];
			}
			@Pc(1693) int local1693;
			@Pc(1814) int local1814;
			@Pc(1861) int local1861;
			@Pc(1947) int local1947;
			for (int vertexIndex = 0; vertexIndex < javaComplexBlendedTile.vertexCount; vertexIndex++) {
				int vertexX = offsetsX[vertexIndex];
				int vertexZ = offsetsZ[vertexIndex];
				int vertexLightLevel;
				if (vertexX == 0 && vertexZ == 0) {
					vertexLightLevel = this.lightLevels[x][z] - this.shadowLevels[x][z];
				} else if (vertexX == 0 && vertexZ == super.tileUnits) {
					vertexLightLevel = this.lightLevels[x][z + 1] - this.shadowLevels[x][z + 1];
				} else if (vertexX == super.tileUnits && vertexZ == super.tileUnits) {
					vertexLightLevel = this.lightLevels[x + 1][z + 1] - this.shadowLevels[x + 1][z + 1];
				} else if (vertexX == super.tileUnits && vertexZ == 0) {
					vertexLightLevel = this.lightLevels[x + 1][z] - this.shadowLevels[x + 1][z];
				} else {
					int southLight = (this.lightLevels[x][z] - this.shadowLevels[x][z]) * (super.tileUnits - vertexX) + (this.lightLevels[x + 1][z] - this.shadowLevels[x + 1][z]) * vertexX;
					int northLight = (this.lightLevels[x][z + 1] - this.shadowLevels[x][z + 1]) * (super.tileUnits - vertexX) + (this.lightLevels[x + 1][z + 1] - this.shadowLevels[x + 1][z + 1]) * vertexX;
					vertexLightLevel = southLight * (super.tileUnits - vertexZ) + northLight * vertexZ >> super.tileScale * 2;
				}
				int worldX = (x << super.tileScale) + vertexX;
				int worldZ = (z << super.tileScale) + vertexZ;
				javaComplexBlendedTile.verticesX[vertexIndex] = (short) vertexX;
				javaComplexBlendedTile.verticesZ[vertexIndex] = (short) vertexZ;
				javaComplexBlendedTile.verticesY[vertexIndex] = (short) (this.averageHeight(worldX, worldZ) + (heightOffsets == null ? 0 : heightOffsets[vertexIndex]));
				if (vertexLightLevel < 0) {
					vertexLightLevel = 0;
				}
				if (colours[vertexIndex] != 0) {
					int fogAlpha = 0;
					if (waterDepths != null) {
						@Pc(1959) short waterDepth = javaComplexBlendedTile.waterDepths[vertexIndex] = (short) waterDepths[vertexIndex];
						if (maxWaterDepth != 0) {
							fogAlpha = waterDepth * 255 / maxWaterDepth;
							if (fogAlpha < 0) {
								fogAlpha = 0;
							} else if (fogAlpha > 255) {
								fogAlpha = 255;
							}
						}
					}
					javaComplexBlendedTile.vertexColours[vertexIndex] = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[vertexIndex] >> 8, vertexLightLevel), fogAlpha, fogColour);
					if (blendedColours != null) {
						javaComplexBlendedTile.vertexColours[vertexIndex] |= vertexLightLevel << 25;
					}
				} else if (blendedColours == null) {
					javaComplexBlendedTile.vertexColours[vertexIndex] = 0;
				} else {
					javaComplexBlendedTile.vertexColours[vertexIndex] = vertexLightLevel << 25;
				}
			}
			@Pc(2011) boolean hasTextures = false;
			for (int faceIndex = 0; faceIndex < javaComplexBlendedTile.faceCount; faceIndex++) {
				if (textures[faceIndex * 3] != -1 && !this.toolkit.materialProvider.getMetrics(textures[faceIndex * 3]).detailsOnly) {
					hasTextures = true;
				}
			}
			if (blendedColours != null) {
				javaComplexBlendedTile.faceBlendedColours = new int[javaComplexBlendedTile.faceCount];
			}
			if (hasTextures) {
				javaComplexBlendedTile.faceTextures = new short[javaComplexBlendedTile.faceCount];
				javaComplexBlendedTile.faceSizes = new short[javaComplexBlendedTile.faceCount];
			}
			for (int faceIndex = 0; faceIndex < javaComplexBlendedTile.faceCount; faceIndex++) {
				int vertexA = faceIndex * 3;
				if (blendedColours != null && blendedColours[vertexA] != 0) {
					javaComplexBlendedTile.faceBlendedColours[faceIndex] = blendedColours[vertexA] >> 8;
				}
				if (hasTextures) {
					int vertexB = vertexA + 1;
					int vertexC = vertexB + 1;
					@Pc(2093) boolean anyTextured = false;
					@Pc(2095) boolean allTextured = true;
					@Pc(2099) int texture = textures[vertexA];
					if (texture == -1 || this.toolkit.materialProvider.getMetrics(texture).detailsOnly) {
						allTextured = false;
					} else {
						anyTextured = true;
					}
					texture = textures[vertexB];
					if (texture == -1 || this.toolkit.materialProvider.getMetrics(texture).detailsOnly) {
						allTextured = false;
					} else {
						anyTextured = true;
					}
					texture = textures[vertexC];
					if (texture == -1 || this.toolkit.materialProvider.getMetrics(texture).detailsOnly) {
						allTextured = false;
					} else {
						anyTextured = true;
					}
					if (allTextured) {
						javaComplexBlendedTile.faceTextures[faceIndex] = (short) texture;
						javaComplexBlendedTile.faceSizes[faceIndex] = (short) sizes[vertexA];
					} else {
						if (anyTextured) {
							texture = textures[vertexA];
							if (texture != -1 && !this.toolkit.materialProvider.getMetrics(texture).detailsOnly) {
								javaComplexBlendedTile.vertexColours[vertexA] = ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(this.toolkit.materialProvider.getMetrics(texture).colorHsl & 0xFFFF) & 0xFFFF];
							}
							texture = textures[vertexB];
							if (texture != -1 && !this.toolkit.materialProvider.getMetrics(texture).detailsOnly) {
								javaComplexBlendedTile.vertexColours[vertexB] = ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(this.toolkit.materialProvider.getMetrics(texture).colorHsl & 0xFFFF) & 0xFFFF];
							}
							texture = textures[vertexC];
							if (texture != -1 && !this.toolkit.materialProvider.getMetrics(texture).detailsOnly) {
								javaComplexBlendedTile.vertexColours[vertexC] = ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(this.toolkit.materialProvider.getMetrics(texture).colorHsl & 0xFFFF) & 0xFFFF];
							}
						}
						javaComplexBlendedTile.faceTextures[faceIndex] = -1;
					}
				}
			}
			this.complexBlendedTiles[x][z] = javaComplexBlendedTile;
			return;
		}
		@Pc(842) JavaSimpleBlendedTile javaSimpleBlendedTile = new JavaSimpleBlendedTile();
		int colour = colours[0];
		int texture = textures[0];
		if (blendedColours != null) {
			javaSimpleBlendedTile.blendedColour = blendedColours[0] >> 8;
			if (colour == 0) {
				javaSimpleBlendedTile.flags = (byte) (javaSimpleBlendedTile.flags | 0x2);
			}
		} else if (colour == 0) {
			return;
		}
		if (super.tileHeights[x][z] == super.tileHeights[x + 1][z] && super.tileHeights[x][z] == super.tileHeights[x + 1][z + 1] && super.tileHeights[x][z] == super.tileHeights[x][z + 1]) {
			javaSimpleBlendedTile.flags = (byte) (javaSimpleBlendedTile.flags | 0x1);
		}
		if (texture == -1 || (javaSimpleBlendedTile.flags & 0x2) != 0 || this.toolkit.materialProvider.getMetrics(texture).detailsOnly) {
			int fogAlpha;
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0;
			} else {
				fogAlpha = waterDepths[southWestVertex] * 255 / maxWaterDepth;
				if (fogAlpha < 0) {
					fogAlpha = 0;
				} else if (fogAlpha > 255) {
					fogAlpha = 255;
				}
			}
			javaSimpleBlendedTile.colourSw = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[southWestVertex] >> 8, this.lightLevels[x][z] - this.shadowLevels[x][z]), fogAlpha, fogColour);
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourSw |= this.shadowLevels[x][z] + 255 - this.lightLevels[x][z] << 25;
			}
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0;
			} else {
				fogAlpha = waterDepths[southEastVertex] * 255 / maxWaterDepth;
				if (fogAlpha < 0) {
					fogAlpha = 0;
				} else if (fogAlpha > 255) {
					fogAlpha = 255;
				}
			}
			javaSimpleBlendedTile.colourSe = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[southEastVertex] >> 8, this.lightLevels[x + 1][z] - this.shadowLevels[x + 1][z]), fogAlpha, fogColour);
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourSe |= this.shadowLevels[x + 1][z] + 255 - this.lightLevels[x + 1][z] << 25;
			}
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0;
			} else {
				fogAlpha = waterDepths[northEastVertex] * 255 / maxWaterDepth;
				if (fogAlpha < 0) {
					fogAlpha = 0;
				} else if (fogAlpha > 255) {
					fogAlpha = 255;
				}
			}
			javaSimpleBlendedTile.colourNe = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[northEastVertex] >> 8, this.lightLevels[x + 1][z + 1] - this.shadowLevels[x + 1][z + 1]), fogAlpha, fogColour);
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourNe |= this.shadowLevels[x + 1][z + 1] + 255 - this.lightLevels[x + 1][z + 1] << 25;
			}
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0;
			} else {
				fogAlpha = waterDepths[northWestVertex] * 255 / maxWaterDepth;
				if (fogAlpha < 0) {
					fogAlpha = 0;
				} else if (fogAlpha > 255) {
					fogAlpha = 255;
				}
			}
			javaSimpleBlendedTile.colourNw = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[northWestVertex] >> 8, this.lightLevels[x][z + 1] - this.shadowLevels[x][z + 1]), fogAlpha, fogColour);
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourNw |= this.shadowLevels[x][z + 1] + 255 - this.lightLevels[x][z + 1] << 25;
			}
			javaSimpleBlendedTile.texture = -1;
		} else {
			int fogAlpha;
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0;
			} else {
				fogAlpha = waterDepths[southWestVertex] * 255 / maxWaterDepth;
				if (fogAlpha < 0) {
					fogAlpha = 0;
				} else if (fogAlpha > 255) {
					fogAlpha = 255;
				}
			}
			javaSimpleBlendedTile.colourSw = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[southWestVertex] >> 8, this.lightLevels[x][z] - this.shadowLevels[x][z]), fogAlpha, fogColour);
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourSw |= this.shadowLevels[x][z] + 255 - this.lightLevels[x][z] << 25;
			}
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0;
			} else {
				fogAlpha = waterDepths[southEastVertex] * 255 / maxWaterDepth;
				if (fogAlpha < 0) {
					fogAlpha = 0;
				} else if (fogAlpha > 255) {
					fogAlpha = 255;
				}
			}
			javaSimpleBlendedTile.colourSe = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[southEastVertex] >> 8, this.lightLevels[x + 1][z] - this.shadowLevels[x + 1][z]), fogAlpha, fogColour);
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourSe |= this.shadowLevels[x + 1][z] + 255 - this.lightLevels[x + 1][z] << 25;
			}
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0;
			} else {
				fogAlpha = waterDepths[northEastVertex] * 255 / maxWaterDepth;
				if (fogAlpha < 0) {
					fogAlpha = 0;
				} else if (fogAlpha > 255) {
					fogAlpha = 255;
				}
			}
			javaSimpleBlendedTile.colourNe = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[northEastVertex] >> 8, this.lightLevels[x + 1][z + 1] - this.shadowLevels[x + 1][z + 1]), fogAlpha, fogColour);
			if (javaSimpleBlendedTile.blendedColour != 0) {
				javaSimpleBlendedTile.colourNe |= this.shadowLevels[x + 1][z + 1] + 255 - this.lightLevels[x + 1][z + 1] << 25;
			}
			if (waterDepths == null || maxWaterDepth == 0) {
				fogAlpha = 0;
			} else {
				fogAlpha = waterDepths[northWestVertex] * 255 / maxWaterDepth;
				if (fogAlpha < 0) {
					fogAlpha = 0;
				} else if (fogAlpha > 255) {
					fogAlpha = 255;
				}
			}
			javaSimpleBlendedTile.colourNw = ColourUtils.blendColours(PureJavaFloorModel.brighten(colours[northWestVertex] >> 8, this.lightLevels[x][z + 1] - this.shadowLevels[x][z + 1]), fogAlpha, fogColour);
			javaSimpleBlendedTile.texture = (short) texture;
		}
		if (waterDepths != null) {
			javaSimpleBlendedTile.waterDepthNe = (short) waterDepths[northEastVertex];
			javaSimpleBlendedTile.waterDepthNw = (short) waterDepths[northWestVertex];
			javaSimpleBlendedTile.waterDepthSe = (short) waterDepths[southEastVertex];
			javaSimpleBlendedTile.waterDepthSw = (short) waterDepths[southWestVertex];
		}
		this.simpleBlendedTiles[x][z] = javaSimpleBlendedTile;
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(II[I[I[I[I[I[I[I[I[I[I[IIIIZ)V")
	@Override
	public void addTile(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int[] offsetX, @OriginalArg(3) int[] offsetLevel, @OriginalArg(4) int[] offsetY, @OriginalArg(5) int[] depths, @OriginalArg(6) int[] faceA, @OriginalArg(7) int[] faceB, @OriginalArg(8) int[] faceC, @OriginalArg(9) int[] colours, @OriginalArg(10) int[] blendedColours, @OriginalArg(11) int[] textures, @OriginalArg(12) int[] sizes, @OriginalArg(13) int waterColour, @OriginalArg(14) int waterDepth, @OriginalArg(15) int waterBias) {
		if (this.simpleTiles == null) {
			this.simpleTiles = new PlainTile[super.width][super.length];
			this.complexTiles = new JavaComplexTile[super.width][super.length];
		} else if (this.simpleBlendedTiles != null || this.genericBlendedTiles != null) {
			throw new IllegalStateException();
		}
		@Pc(29) boolean simple = false;
		@Pc(67) int local67;
		@Pc(73) int local73;
		if (colours.length == 2 && faceA.length == 2 && (colours[0] == colours[1] || textures[0] != -1 && textures[0] == textures[1])) {
			simple = true;
			for (@Pc(60) int i = 1; i < 2; i++) {
				int vertexX = offsetX[faceA[i]];
				int vertexZ = offsetY[faceA[i]];
				if (vertexX != 0 && vertexX != super.tileUnits || vertexZ != 0 && vertexZ != super.tileUnits) {
					simple = false;
					break;
				}
			}
		}
		if (!simple) {
			@Pc(98) JavaComplexTile javaComplexTile = new JavaComplexTile();
			@Pc(102) short vertexCount = (short) offsetX.length;
			@Pc(106) short length = (short) colours.length;
			javaComplexTile.vertexCount = vertexCount;
			javaComplexTile.verticesLight = new short[vertexCount];
			javaComplexTile.verticesX = new short[vertexCount];
			javaComplexTile.verticesY = new short[vertexCount];
			javaComplexTile.verticesZ = new short[vertexCount];
			@Pc(136) int local136;
			@Pc(291) int local291;
			@Pc(338) int local338;
			for (@Pc(127) int j = 0; j < vertexCount; j++) {
				@Pc(132) int vertexX = offsetX[j];
				int vertexZ = offsetY[j];
				if (vertexX == 0 && vertexZ == 0) {
					javaComplexTile.verticesLight[j] = (short) (this.lightLevels[x][z] - this.shadowLevels[x][z]);
				} else if (vertexX == 0 && vertexZ == super.tileUnits) {
					javaComplexTile.verticesLight[j] = (short) (this.lightLevels[x][z + 1] - this.shadowLevels[x][z + 1]);
				} else if (vertexX == super.tileUnits && vertexZ == super.tileUnits) {
					javaComplexTile.verticesLight[j] = (short) (this.lightLevels[x + 1][z + 1] - this.shadowLevels[x + 1][z + 1]);
				} else if (vertexX == super.tileUnits && vertexZ == 0) {
					javaComplexTile.verticesLight[j] = (short) (this.lightLevels[x + 1][z] - this.shadowLevels[x + 1][z]);
				} else {
					int southLight = (this.lightLevels[x][z] - this.shadowLevels[x][z]) * (super.tileUnits - vertexX) + (this.lightLevels[x + 1][z] - this.shadowLevels[x + 1][z]) * vertexX;
					int northLight = (this.lightLevels[x][z + 1] - this.shadowLevels[x][z + 1]) * (super.tileUnits - vertexX) + (this.lightLevels[x + 1][z + 1] - this.shadowLevels[x + 1][z + 1]) * vertexX;
					javaComplexTile.verticesLight[j] = (short) (southLight * (super.tileUnits - vertexZ) + northLight * vertexZ >> super.tileScale * 2);
				}
				int worldX = (x << super.tileScale) + vertexX;
				int worldZ = (z << super.tileScale) + vertexZ;
				javaComplexTile.verticesX[j] = (short) vertexX;
				javaComplexTile.verticesZ[j] = (short) vertexZ;
				javaComplexTile.verticesY[j] = (short) (this.averageHeight(worldX, worldZ) + (offsetLevel == null ? 0 : offsetLevel[j]));
				if (javaComplexTile.verticesLight[j] < 2) {
					javaComplexTile.verticesLight[j] = 2;
				}
			}
			@Pc(419) boolean textured = false;
			int faceCount = 0;
			for (int faceIndex = 0; faceIndex < length; faceIndex++) {
				if (colours[faceIndex] >= 0 || blendedColours != null && blendedColours[faceIndex] >= 0) {
					faceCount++;
				}
				int texture = textures[faceIndex];
				if (texture != -1) {
					@Pc(449) MaterialType textureMetrics = this.toolkit.materialProvider.getMetrics(texture);
					if (!textureMetrics.detailsOnly) {
						textured = true;
						if (this.isWaterEffect(textureMetrics.effectType) || textureMetrics.speedU != 0 || textureMetrics.speedV != 0) {
							javaComplexTile.flags = (byte) (javaComplexTile.flags | 0x4);
						}
					}
				}
			}
			javaComplexTile.faceColours = new int[faceCount];
			if (blendedColours != null) {
				javaComplexTile.faceBlendedColours = new int[faceCount];
			}
			javaComplexTile.faceA = new short[faceCount];
			javaComplexTile.faceB = new short[faceCount];
			javaComplexTile.faceC = new short[faceCount];
			if (textured) {
				javaComplexTile.faceTextures = new short[faceCount];
				javaComplexTile.faceSizes = new short[faceCount];
			}
			for (int faceIndex = 0; faceIndex < length; faceIndex++) {
				if (colours[faceIndex] >= 0 || blendedColours != null && blendedColours[faceIndex] >= 0) {
					if (colours[faceIndex] >= 0) {
						javaComplexTile.faceColours[javaComplexTile.faceCount] = ColorUtil.hslToHsv(colours[faceIndex]);
					} else {
						javaComplexTile.faceColours[javaComplexTile.faceCount] = -1;
					}
					if (blendedColours != null) {
						if (blendedColours[faceIndex] == -1) {
							javaComplexTile.faceBlendedColours[javaComplexTile.faceCount] = -1;
						} else {
							javaComplexTile.faceBlendedColours[javaComplexTile.faceCount] = ColorUtil.hslToHsv(blendedColours[faceIndex]);
						}
					}
					javaComplexTile.faceA[javaComplexTile.faceCount] = (short) faceA[faceIndex];
					javaComplexTile.faceB[javaComplexTile.faceCount] = (short) faceB[faceIndex];
					javaComplexTile.faceC[javaComplexTile.faceCount] = (short) faceC[faceIndex];
					if (textured) {
						if (textures[faceIndex] == -1 || this.toolkit.materialProvider.getMetrics(textures[faceIndex]).detailsOnly) {
							javaComplexTile.faceTextures[javaComplexTile.faceCount] = -1;
						} else {
							javaComplexTile.faceTextures[javaComplexTile.faceCount] = (short) textures[faceIndex];
							javaComplexTile.faceSizes[javaComplexTile.faceCount] = (short) sizes[faceIndex];
						}
					}
					javaComplexTile.faceCount++;
				}
			}
			this.complexTiles[x][z] = javaComplexTile;
		} else if (colours[0] >= 0 || blendedColours != null && blendedColours[0] >= 0) {
			@Pc(668) PlainTile javaSimpleTile = new PlainTile();
			int colour = colours[0];
			int texture = textures[0];
			if (blendedColours != null) {
				javaSimpleTile.color = JavaModel.applyLightness(this.lightLevels[x][z] - this.shadowLevels[x][z], ColorUtil.hslToHsv(blendedColours[0]));
				if (colour == -1) {
					javaSimpleTile.functionMask = (byte) (javaSimpleTile.functionMask | 0x2);
				}
			}
			if (super.tileHeights[x][z] == super.tileHeights[x + 1][z] && super.tileHeights[x][z] == super.tileHeights[x + 1][z + 1] && super.tileHeights[x][z] == super.tileHeights[x][z + 1]) {
				javaSimpleTile.functionMask = (byte) (javaSimpleTile.functionMask | 0x1);
			}
			@Pc(766) MaterialType textureMetrics2 = null;
			if (texture != -1) {
				textureMetrics2 = this.toolkit.materialProvider.getMetrics(texture);
			}
			if (textureMetrics2 == null || (javaSimpleTile.functionMask & 0x2) != 0 || textureMetrics2.detailsOnly) {
				@Pc(893) short hsl = ColorUtil.hslToHsv(colour);
				javaSimpleTile.hslSw = (short) JavaModel.applyLightness(this.lightLevels[x][z] - this.shadowLevels[x][z], hsl);
				javaSimpleTile.hslSe = (short) JavaModel.applyLightness(this.lightLevels[x + 1][z] - this.shadowLevels[x + 1][z], hsl);
				javaSimpleTile.hslNe = (short) JavaModel.applyLightness(this.lightLevels[x + 1][z + 1] - this.shadowLevels[x + 1][z + 1], hsl);
				javaSimpleTile.hslNw = (short) JavaModel.applyLightness(this.lightLevels[x][z + 1] - this.shadowLevels[x][z + 1], hsl);
				javaSimpleTile.texture = -1;
			} else {
				javaSimpleTile.hslSw = (short) (this.lightLevels[x][z] - this.shadowLevels[x][z]);
				javaSimpleTile.hslSe = (short) (this.lightLevels[x + 1][z] - this.shadowLevels[x + 1][z]);
				javaSimpleTile.hslNe = (short) (this.lightLevels[x + 1][z + 1] - this.shadowLevels[x + 1][z + 1]);
				javaSimpleTile.hslNw = (short) (this.lightLevels[x][z + 1] - this.shadowLevels[x][z + 1]);
				javaSimpleTile.texture = (short) texture;
				if (this.isWaterEffect(textureMetrics2.effectType) || textureMetrics2.speedU != 0 || textureMetrics2.speedV != 0) {
					javaSimpleTile.functionMask = (byte) (javaSimpleTile.functionMask | 0x4);
				}
			}
			this.simpleTiles[x][z] = javaSimpleTile;
		}
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(III[[ZZII)V")
	@Override
	public void renderFloor(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int radius, @OriginalArg(3) boolean[][] visibility, @OriginalArg(4) boolean belowLowestPlane, @OriginalArg(5) int depth, @OriginalArg(6) int skipFlags) {
		@Pc(3) JavaMatrix javaMatrix = this.toolkit.camera;
		this.depthOverride = depth;
		this.cameraE1_1 = javaMatrix.e1_1;
		this.cameraE1_2 = javaMatrix.e1_2;
		this.cameraE1_3 = javaMatrix.e1_3;
		this.cameraTx = javaMatrix.tx;
		this.cameraE2_1 = javaMatrix.e2_1;
		this.cameraE2_2 = javaMatrix.e2_2;
		this.cameraE2_3 = javaMatrix.e2_3;
		this.cameraTy = javaMatrix.ty;
		this.cameraE3_1 = javaMatrix.e3_1;
		this.cameraE3_2 = javaMatrix.e3_2;
		this.cameraE3_3 = javaMatrix.e3_3;
		this.cameraTz = javaMatrix.tz;
		for (@Pc(56) int i = 0; i < radius + radius; i++) {
			for (@Pc(59) int j = 0; j < radius + radius; j++) {
				if (visibility[i][j]) {
					@Pc(72) int tileX = x + i - radius;
					@Pc(78) int tileZ = z + j - radius;
					if (tileX >= 0 && tileX < super.width && tileZ >= 0 && tileZ < super.length) {
						this.renderTile(tileX, tileZ, skipFlags);
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!cr", name = "ka", descriptor = "(III)V")
	@Override
	public void ka(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int shadowLevel) {
		if (this.shadowLevels[x][z] < shadowLevel) {
			this.shadowLevels[x][z] = (byte) shadowLevel;
		}
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(IIZLclient!pga;Lclient!mp;[I[I[I[II)V")
	private void renderBlendedTile(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) boolean water, @OriginalArg(3) JavaThreadResource resource, @OriginalArg(4) Rasterizer rasterizer, @OriginalArg(5) int[] screenX, @OriginalArg(6) int[] screenY, @OriginalArg(7) int[] depths, @OriginalArg(8) int[] fogLevels, @OriginalArg(9) int skipFlags) {
		@Pc(6) JavaSimpleBlendedTile javaSimpleBlendedTile = this.simpleBlendedTiles[x][z];
		@Pc(41) int local41;
		@Pc(46) int local46;
		@Pc(51) int local51;
		@Pc(544) int local544;
		@Pc(394) int local394;
		@Pc(454) int local454;
		@Pc(514) int local514;
		@Pc(574) int local574;
		if (javaSimpleBlendedTile == null) {
			@Pc(3030) JavaComplexBlendedTile javaComplexBlendedTile = this.complexBlendedTiles[x][z];
			if (javaComplexBlendedTile != null) {
				if (skipFlags != 0) {
					if ((javaComplexBlendedTile.flags & 0x4) == 0) {
						if ((skipFlags & 0x2) != 0) {
							return;
						}
					} else if ((skipFlags & 0x1) != 0) {
						return;
					}
				}
				@Pc(3196) float viewX;
				@Pc(3217) float viewY;
				@Pc(3102) float viewZ;
				if (this.depthOverride == -1) {
					for (int vertexIndex = 0; vertexIndex < javaComplexBlendedTile.vertexCount; vertexIndex++) {
						int vertexX = javaComplexBlendedTile.verticesX[vertexIndex] + (x << super.tileScale);
						int vertexY = javaComplexBlendedTile.verticesY[vertexIndex];
						int vertexZ = javaComplexBlendedTile.verticesZ[vertexIndex] + (z << super.tileScale);
						viewZ = this.cameraTz + this.cameraE3_1 * (float) vertexX + this.cameraE3_2 * (float) vertexY + this.cameraE3_3 * (float) vertexZ;
						if (viewZ <= (float) this.toolkit.zNear) {
							return;
						}
						fogLevels[vertexIndex] = 0;
						if (water) {
							int fogLevel = (int) (viewZ - (float) resource.fogPlane);
							if (fogLevel > 255) {
								fogLevel = 255;
							}
							if (fogLevel > 0) {
								fogLevels[vertexIndex] = fogLevel;
								int waterDepthOffset = javaComplexBlendedTile.waterDepths[vertexIndex] * fogLevel / 255;
								if (waterDepthOffset > 0) {
									vertexY -= waterDepthOffset;
								}
							}
						} else if (resource.fogActive) {
							int fogLevel = (int) (viewZ - (float) resource.fogPlane);
							if (fogLevel > 0) {
								fogLevels[vertexIndex] = fogLevel;
								if (fogLevels[vertexIndex] > 255) {
									fogLevels[vertexIndex] = 255;
								}
							}
						}
						viewX = this.cameraTx + this.cameraE1_1 * (float) vertexX + this.cameraE1_2 * (float) vertexY + this.cameraE1_3 * (float) vertexZ;
						viewY = this.cameraTy + this.cameraE2_1 * (float) vertexX + this.cameraE2_2 * (float) vertexY + this.cameraE2_3 * (float) vertexZ;
						screenX[vertexIndex] = rasterizer.minX + (int) (viewX * (float) this.toolkit.projectionScaleX / viewZ);
						screenY[vertexIndex] = rasterizer.minY + (int) (viewY * (float) this.toolkit.projectionScaleY / viewZ);
						depths[vertexIndex] = (int) viewZ;
					}
				} else {
					for (int vertexIndex = 0; vertexIndex < javaComplexBlendedTile.vertexCount; vertexIndex++) {
						int vertexX = javaComplexBlendedTile.verticesX[vertexIndex] + (x << super.tileScale);
						int vertexY = javaComplexBlendedTile.verticesY[vertexIndex];
						int vertexZ = javaComplexBlendedTile.verticesZ[vertexIndex] + (z << super.tileScale);
						viewZ = this.cameraTz + this.cameraE3_1 * (float) vertexX + this.cameraE3_2 * (float) vertexY + this.cameraE3_3 * (float) vertexZ;
						fogLevels[vertexIndex] = 0;
						if (water) {
							int fogLevel = this.depthOverride - resource.fogPlane;
							if (fogLevel > 255) {
								fogLevel = 255;
							}
							if (fogLevel > 0) {
								fogLevels[vertexIndex] = fogLevel;
								int waterDepthOffset = javaComplexBlendedTile.waterDepths[vertexIndex] * fogLevel / 255;
								if (waterDepthOffset > 0) {
									vertexY -= waterDepthOffset;
								}
							}
						} else if (resource.fogActive) {
							int fogLevel = this.depthOverride - resource.fogPlane;
							if (fogLevel > 0) {
								fogLevels[vertexIndex] = fogLevel;
								if (fogLevels[vertexIndex] > 255) {
									fogLevels[vertexIndex] = 255;
								}
							}
						}
						viewX = this.cameraTx + this.cameraE1_1 * (float) vertexX + this.cameraE1_2 * (float) vertexY + this.cameraE1_3 * (float) vertexZ;
						viewY = this.cameraTy + this.cameraE2_1 * (float) vertexX + this.cameraE2_2 * (float) vertexY + this.cameraE2_3 * (float) vertexZ;
						screenX[vertexIndex] = rasterizer.minX + (int) (viewX * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
						screenY[vertexIndex] = rasterizer.minY + (int) (viewY * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
						depths[vertexIndex] = (int) viewZ;
					}
				}
				@Pc(3486) int xB;
				@Pc(3490) int xC;
				@Pc(3494) int yA;
				@Pc(3498) int yB;
				@Pc(3502) int yC;
				@Pc(3514) int fogTotal;
				if (javaComplexBlendedTile.faceTextures != null) {
					@Pc(3558) short texture;
					@Pc(3568) int alphaMask;
					if (this.depthOverride == -1) {
						for (int faceIndex = 0; faceIndex < javaComplexBlendedTile.faceCount; faceIndex++) {
							int vertexA = faceIndex * 3;
							int vertexB = vertexA + 1;
							int vertexC = vertexB + 1;
							int xA = screenX[vertexA];
							xB = screenX[vertexB];
							xC = screenX[vertexC];
							yA = screenY[vertexA];
							yB = screenY[vertexB];
							yC = screenY[vertexC];
							fogTotal = fogLevels[vertexA] + fogLevels[vertexB] + fogLevels[vertexC];
							if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
								rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width;
								texture = javaComplexBlendedTile.faceTextures[faceIndex];
								if (fogTotal >= 765) {
									rasterizer.renderFlatTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], resource.fogColour);
								} else if (fogTotal > 0) {
									if (texture != -1) {
										alphaMask = -16777216;
										if (texture != -1 && this.isWaterEffect(this.toolkit.materialProvider.getMetrics(texture).effectType)) {
											alphaMask = -1694498816;
										}
										rasterizer.renderTexturedTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], (float) javaComplexBlendedTile.verticesX[vertexA] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesX[vertexB] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesX[vertexC] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesZ[vertexA] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesZ[vertexB] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesZ[vertexC] / (float) super.tileUnits, alphaMask | javaComplexBlendedTile.vertexColours[vertexA] & 0xFFFFFF, alphaMask | javaComplexBlendedTile.vertexColours[vertexB] & 0xFFFFFF, alphaMask | javaComplexBlendedTile.vertexColours[vertexC] & 0xFFFFFF, resource.fogColour, fogLevels[vertexA], fogLevels[vertexB], fogLevels[vertexC], texture);
									} else if ((javaComplexBlendedTile.vertexColours[vertexA] & 0xFFFFFF) != 0) {
										if (texture != -1 && this.isWaterEffect(this.toolkit.materialProvider.getMetrics(texture).effectType)) {
											rasterizer.alpha = -1694498816;
										}
										rasterizer.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], ColorUtil.alphaBlend(javaComplexBlendedTile.vertexColours[vertexA], fogLevels[vertexA] << 24 | resource.fogColour), ColorUtil.alphaBlend(javaComplexBlendedTile.vertexColours[vertexB], fogLevels[vertexB] << 24 | resource.fogColour), ColorUtil.alphaBlend(javaComplexBlendedTile.vertexColours[vertexC], fogLevels[vertexC] << 24 | resource.fogColour));
										rasterizer.alpha = 0;
									}
								} else if (texture != -1) {
									alphaMask = -16777216;
									if (texture != -1 && this.isWaterEffect(this.toolkit.materialProvider.getMetrics(texture).effectType)) {
										alphaMask = -1694498816;
									}
									rasterizer.renderTexturedTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], (float) javaComplexBlendedTile.verticesX[vertexA] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesX[vertexB] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesX[vertexC] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesZ[vertexA] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesZ[vertexB] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesZ[vertexC] / (float) super.tileUnits, alphaMask | javaComplexBlendedTile.vertexColours[vertexA] & 0xFFFFFF, alphaMask | javaComplexBlendedTile.vertexColours[vertexB] & 0xFFFFFF, alphaMask | javaComplexBlendedTile.vertexColours[vertexC] & 0xFFFFFF, 0, 0, 0, 0, texture);
								} else if ((javaComplexBlendedTile.vertexColours[vertexA] & 0xFFFFFF) != 0) {
									if (texture != -1 && this.isWaterEffect(this.toolkit.materialProvider.getMetrics(texture).effectType)) {
										rasterizer.alpha = -1694498816;
									}
									rasterizer.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], javaComplexBlendedTile.vertexColours[vertexA], javaComplexBlendedTile.vertexColours[vertexB], javaComplexBlendedTile.vertexColours[vertexC]);
									rasterizer.alpha = 0;
								}
							}
						}
						return;
					}
					for (int faceIndex = 0; faceIndex < javaComplexBlendedTile.faceCount; faceIndex++) {
						int vertexA = faceIndex * 3;
						int vertexB = vertexA + 1;
						int vertexC = vertexB + 1;
						int xA = screenX[vertexA];
						xB = screenX[vertexB];
						xC = screenX[vertexC];
						yA = screenY[vertexA];
						yB = screenY[vertexB];
						yC = screenY[vertexC];
						fogTotal = fogLevels[vertexA] + fogLevels[vertexB] + fogLevels[vertexC];
						if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
							rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width;
							texture = javaComplexBlendedTile.faceTextures[faceIndex];
							if (fogTotal < 765) {
								if (texture != -1 && this.isWaterEffect(this.toolkit.materialProvider.getMetrics(texture).effectType)) {
									rasterizer.alpha = -1694498816;
								}
								if (fogTotal > 0) {
									if (texture != -1) {
										alphaMask = -16777216;
										if (texture != -1 && this.isWaterEffect(this.toolkit.materialProvider.getMetrics(texture).effectType)) {
											alphaMask = -1694498816;
										}
										rasterizer.renderTexturedTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], (float) javaComplexBlendedTile.verticesX[vertexA] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesX[vertexB] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesX[vertexC] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesZ[vertexA] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesZ[vertexB] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesZ[vertexC] / (float) super.tileUnits, alphaMask | javaComplexBlendedTile.vertexColours[vertexA] & 0xFFFFFF, alphaMask | javaComplexBlendedTile.vertexColours[vertexB] & 0xFFFFFF, alphaMask | javaComplexBlendedTile.vertexColours[vertexC] & 0xFFFFFF, resource.fogColour, fogLevels[vertexA], fogLevels[vertexB], fogLevels[vertexC], texture);
									} else if ((javaComplexBlendedTile.vertexColours[vertexA] & 0xFFFFFF) != 0) {
										if (texture != -1 && this.isWaterEffect(this.toolkit.materialProvider.getMetrics(texture).effectType)) {
											rasterizer.alpha = -1694498816;
										}
										rasterizer.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], ColorUtil.alphaBlend(javaComplexBlendedTile.vertexColours[vertexA], fogLevels[vertexA] << 24 | resource.fogColour), ColorUtil.alphaBlend(javaComplexBlendedTile.vertexColours[vertexB], fogLevels[vertexB] << 24 | resource.fogColour), ColorUtil.alphaBlend(javaComplexBlendedTile.vertexColours[vertexC], fogLevels[vertexC] << 24 | resource.fogColour));
										rasterizer.alpha = 0;
									}
								} else if (texture != -1) {
									alphaMask = -16777216;
									if (texture != -1 && this.isWaterEffect(this.toolkit.materialProvider.getMetrics(texture).effectType)) {
										alphaMask = -1694498816;
									}
									rasterizer.renderTexturedTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], (float) javaComplexBlendedTile.verticesX[vertexA] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesX[vertexB] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesX[vertexC] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesZ[vertexA] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesZ[vertexB] / (float) super.tileUnits, (float) javaComplexBlendedTile.verticesZ[vertexC] / (float) super.tileUnits, alphaMask | javaComplexBlendedTile.vertexColours[vertexA] & 0xFFFFFF, alphaMask | javaComplexBlendedTile.vertexColours[vertexB] & 0xFFFFFF, alphaMask | javaComplexBlendedTile.vertexColours[vertexC] & 0xFFFFFF, 0, 0, 0, 0, texture);
								} else if ((javaComplexBlendedTile.vertexColours[vertexA] & 0xFFFFFF) != 0) {
									if (texture != -1 && this.isWaterEffect(this.toolkit.materialProvider.getMetrics(texture).effectType)) {
										rasterizer.alpha = -1694498816;
									}
									rasterizer.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], javaComplexBlendedTile.vertexColours[vertexA], javaComplexBlendedTile.vertexColours[vertexB], javaComplexBlendedTile.vertexColours[vertexC]);
									rasterizer.alpha = 0;
								}
								rasterizer.alpha = 0;
							} else {
								rasterizer.renderFlatTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], resource.fogColour);
							}
						}
					}
					return;
				}
				for (int faceIndex = 0; faceIndex < javaComplexBlendedTile.faceCount; faceIndex++) {
					int vertexA = faceIndex * 3;
					int vertexB = vertexA + 1;
					int vertexC = vertexB + 1;
					int xA = screenX[vertexA];
					xB = screenX[vertexB];
					xC = screenX[vertexC];
					yA = screenY[vertexA];
					yB = screenY[vertexB];
					yC = screenY[vertexC];
					fogTotal = fogLevels[vertexA] + fogLevels[vertexB] + fogLevels[vertexC];
					if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
						rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width;
						if (fogTotal >= 765) {
							rasterizer.renderFlatTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], resource.fogColour);
						} else if (fogTotal > 0) {
							if ((javaComplexBlendedTile.vertexColours[vertexA] & 0xFFFFFF) != 0) {
								rasterizer.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], ColourUtils.blendColours(javaComplexBlendedTile.vertexColours[vertexA], fogLevels[vertexA], resource.fogColour), ColourUtils.blendColours(javaComplexBlendedTile.vertexColours[vertexB], fogLevels[vertexB], resource.fogColour), ColourUtils.blendColours(javaComplexBlendedTile.vertexColours[vertexC], fogLevels[vertexC], resource.fogColour));
							}
						} else if ((javaComplexBlendedTile.vertexColours[vertexA] & 0xFFFFFF) != 0) {
							rasterizer.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[vertexA], (float) depths[vertexB], (float) depths[vertexC], javaComplexBlendedTile.vertexColours[vertexA], javaComplexBlendedTile.vertexColours[vertexB], javaComplexBlendedTile.vertexColours[vertexC]);
						}
					}
				}
			}
		} else if ((javaSimpleBlendedTile.flags & 0x2) == 0) {
			if (skipFlags != 0) {
				if ((javaSimpleBlendedTile.flags & 0x4) == 0) {
					if ((skipFlags & 0x2) != 0) {
						return;
					}
				} else if ((skipFlags & 0x1) != 0) {
					return;
				}
			}
			@Pc(36) int minX = x * super.tileUnits;
			int maxX = minX + super.tileUnits;
			int minZ = z * super.tileUnits;
			int maxZ = minZ + super.tileUnits;
			@Pc(53) int fogSw = 0;
			@Pc(55) int fogSe = 0;
			@Pc(57) int fogNe = 0;
			@Pc(59) int fogNw = 0;
			@Pc(100) float depthSw;
			@Pc(125) float depthSe;
			@Pc(150) float depthNe;
			@Pc(175) float depthNw;
			@Pc(261) int fog;
			@Pc(73) int heightSw;
			@Pc(848) int local848;
			@Pc(859) int local859;
			@Pc(364) int screenXSw;
			@Pc(424) int screenXSe;
			@Pc(484) int screenXNe;
			@Pc(351) float viewXSw;
			@Pc(411) float viewXSe;
			@Pc(471) float viewXNe;
			@Pc(531) float viewXNw;
			@Pc(381) float viewYSw;
			@Pc(441) float viewYSe;
			@Pc(501) float viewYNe;
			@Pc(561) float viewYNw;
			int screenXNw;
			int screenYSw;
			int screenYSe;
			int screenYNe;
			int screenYNw;
			if ((javaSimpleBlendedTile.flags & 0x1) == 0 || water) {
				heightSw = super.tileHeights[x][z];
				int heightSe = super.tileHeights[x + 1][z];
				int heightNe = super.tileHeights[x + 1][z + 1];
				@Pc(868) int heightNw = super.tileHeights[x][z + 1];
				if (this.depthOverride == -1) {
					depthSw = this.cameraTz + this.cameraE3_1 * (float) minX + this.cameraE3_2 * (float) heightSw + this.cameraE3_3 * (float) minZ;
					if (depthSw <= (float) this.toolkit.zNear) {
						return;
					}
					depthSe = this.cameraTz + this.cameraE3_1 * (float) maxX + this.cameraE3_2 * (float) heightSe + this.cameraE3_3 * (float) minZ;
					if (depthSe <= (float) this.toolkit.zNear) {
						return;
					}
					depthNe = this.cameraTz + this.cameraE3_1 * (float) maxX + this.cameraE3_2 * (float) heightNe + this.cameraE3_3 * (float) maxZ;
					if (depthNe <= (float) this.toolkit.zNear) {
						return;
					}
					depthNw = this.cameraTz + this.cameraE3_1 * (float) minX + this.cameraE3_2 * (float) heightNw + this.cameraE3_3 * (float) maxZ;
					if (depthNw <= (float) this.toolkit.zNear) {
						return;
					}
				} else {
					depthSw = this.cameraTz + this.cameraE3_1 * (float) minX + this.cameraE3_2 * (float) heightSw + this.cameraE3_3 * (float) minZ;
					depthSe = this.cameraTz + this.cameraE3_1 * (float) maxX + this.cameraE3_2 * (float) heightSe + this.cameraE3_3 * (float) minZ;
					depthNe = this.cameraTz + this.cameraE3_1 * (float) maxX + this.cameraE3_2 * (float) heightNe + this.cameraE3_3 * (float) maxZ;
					depthNw = this.cameraTz + this.cameraE3_1 * (float) minX + this.cameraE3_2 * (float) heightNw + this.cameraE3_3 * (float) maxZ;
				}
				if (water) {
					fog = (int) (depthSw - (float) resource.fogPlane);
					if (fog > 255) {
						fog = 255;
					}
					@Pc(1097) int waterDepth;
					if (fog > 0) {
						fogSw = fog;
						waterDepth = javaSimpleBlendedTile.waterDepthSw * fog / 255;
						if (waterDepth > 0) {
							heightSw -= waterDepth;
						}
					}
					fog = (int) (depthSe - (float) resource.fogPlane);
					if (fog > 255) {
						fog = 255;
					}
					if (fog > 0) {
						fogSe = fog;
						waterDepth = javaSimpleBlendedTile.waterDepthSe * fog / 255;
						if (waterDepth > 0) {
							heightSe -= waterDepth;
						}
					}
					fog = (int) (depthNe - (float) resource.fogPlane);
					if (fog > 255) {
						fog = 255;
					}
					if (fog > 0) {
						fogNe = fog;
						waterDepth = javaSimpleBlendedTile.waterDepthNe * fog / 255;
						if (waterDepth > 0) {
							heightNe -= waterDepth;
						}
					}
					fog = (int) (depthNw - (float) resource.fogPlane);
					if (fog > 255) {
						fog = 255;
					}
					if (fog > 0) {
						fogNw = fog;
						waterDepth = javaSimpleBlendedTile.waterDepthNw * fog / 255;
						if (waterDepth > 0) {
							heightNw -= waterDepth;
						}
					}
				} else if (resource.fogActive) {
					fog = (int) (depthSw - (float) resource.fogPlane);
					if (fog > 0) {
						fogSw = fog;
						if (fog > 255) {
							fogSw = 255;
						}
					}
					fog = (int) (depthSe - (float) resource.fogPlane);
					if (fog > 0) {
						fogSe = fog;
						if (fog > 255) {
							fogSe = 255;
						}
					}
					fog = (int) (depthNe - (float) resource.fogPlane);
					if (fog > 0) {
						fogNe = fog;
						if (fog > 255) {
							fogNe = 255;
						}
					}
					fog = (int) (depthNw - (float) resource.fogPlane);
					if (fog > 0) {
						fogNw = fog;
						if (fog > 255) {
							fogNw = 255;
						}
					}
				}
				if (this.depthOverride == -1) {
					viewXSw = this.cameraTx + this.cameraE1_1 * (float) minX + this.cameraE1_2 * (float) heightSw + this.cameraE1_3 * (float) minZ;
					screenXSw = rasterizer.minX + (int) (viewXSw * (float) this.toolkit.projectionScaleX / depthSw);
					viewYSw = this.cameraTy + this.cameraE2_1 * (float) minX + this.cameraE2_2 * (float) heightSw + this.cameraE2_3 * (float) minZ;
					screenYSw = rasterizer.minY + (int) (viewYSw * (float) this.toolkit.projectionScaleY / depthSw);
					viewXSe = this.cameraTx + this.cameraE1_1 * (float) maxX + this.cameraE1_2 * (float) heightSe + this.cameraE1_3 * (float) minZ;
					screenXSe = rasterizer.minX + (int) (viewXSe * (float) this.toolkit.projectionScaleX / depthSe);
					viewYSe = this.cameraTy + this.cameraE2_1 * (float) maxX + this.cameraE2_2 * (float) heightSe + this.cameraE2_3 * (float) minZ;
					screenYSe = rasterizer.minY + (int) (viewYSe * (float) this.toolkit.projectionScaleY / depthSe);
					viewXNe = this.cameraTx + this.cameraE1_1 * (float) maxX + this.cameraE1_2 * (float) heightNe + this.cameraE1_3 * (float) maxZ;
					screenXNe = rasterizer.minX + (int) (viewXNe * (float) this.toolkit.projectionScaleX / depthNe);
					viewYNe = this.cameraTy + this.cameraE2_1 * (float) maxX + this.cameraE2_2 * (float) heightNe + this.cameraE2_3 * (float) maxZ;
					screenYNe = rasterizer.minY + (int) (viewYNe * (float) this.toolkit.projectionScaleY / depthNe);
					viewXNw = this.cameraTx + this.cameraE1_1 * (float) minX + this.cameraE1_2 * (float) heightNw + this.cameraE1_3 * (float) maxZ;
					screenXNw = rasterizer.minX + (int) (viewXNw * (float) this.toolkit.projectionScaleX / depthNw);
					viewYNw = this.cameraTy + this.cameraE2_1 * (float) minX + this.cameraE2_2 * (float) heightNw + this.cameraE2_3 * (float) maxZ;
					screenYNw = rasterizer.minY + (int) (viewYNw * (float) this.toolkit.projectionScaleY / depthNw);
				} else {
					viewXSw = this.cameraTx + this.cameraE1_1 * (float) minX + this.cameraE1_2 * (float) heightSw + this.cameraE1_3 * (float) minZ;
					screenXSw = rasterizer.minX + (int) (viewXSw * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYSw = this.cameraTy + this.cameraE2_1 * (float) minX + this.cameraE2_2 * (float) heightSw + this.cameraE2_3 * (float) minZ;
					screenYSw = rasterizer.minY + (int) (viewYSw * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
					viewXSe = this.cameraTx + this.cameraE1_1 * (float) maxX + this.cameraE1_2 * (float) heightSe + this.cameraE1_3 * (float) minZ;
					screenXSe = rasterizer.minX + (int) (viewXSe * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYSe = this.cameraTy + this.cameraE2_1 * (float) maxX + this.cameraE2_2 * (float) heightSe + this.cameraE2_3 * (float) minZ;
					screenYSe = rasterizer.minY + (int) (viewYSe * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
					viewXNe = this.cameraTx + this.cameraE1_1 * (float) maxX + this.cameraE1_2 * (float) heightNe + this.cameraE1_3 * (float) maxZ;
					screenXNe = rasterizer.minX + (int) (viewXNe * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYNe = this.cameraTy + this.cameraE2_1 * (float) maxX + this.cameraE2_2 * (float) heightNe + this.cameraE2_3 * (float) maxZ;
					screenYNe = rasterizer.minY + (int) (viewYNe * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
					viewXNw = this.cameraTx + this.cameraE1_1 * (float) minX + this.cameraE1_2 * (float) heightNw + this.cameraE1_3 * (float) maxZ;
					screenXNw = rasterizer.minX + (int) (viewXNw * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYNw = this.cameraTy + this.cameraE2_1 * (float) minX + this.cameraE2_2 * (float) heightNw + this.cameraE2_3 * (float) maxZ;
					screenYNw = rasterizer.minY + (int) (viewYNw * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
				}
			} else {
				heightSw = super.tileHeights[x][z];
				@Pc(79) float heightZ = this.cameraE3_2 * (float) heightSw;
				if (this.depthOverride == -1) {
					depthSw = this.cameraTz + this.cameraE3_1 * (float) minX + heightZ + this.cameraE3_3 * (float) minZ;
					if (depthSw <= (float) this.toolkit.zNear) {
						return;
					}
					depthSe = this.cameraTz + this.cameraE3_1 * (float) maxX + heightZ + this.cameraE3_3 * (float) minZ;
					if (depthSe <= (float) this.toolkit.zNear) {
						return;
					}
					depthNe = this.cameraTz + this.cameraE3_1 * (float) maxX + heightZ + this.cameraE3_3 * (float) maxZ;
					if (depthNe <= (float) this.toolkit.zNear) {
						return;
					}
					depthNw = this.cameraTz + this.cameraE3_1 * (float) minX + heightZ + this.cameraE3_3 * (float) maxZ;
					if (depthNw <= (float) this.toolkit.zNear) {
						return;
					}
				} else {
					depthSw = this.cameraTz + this.cameraE3_1 * (float) minX + heightZ + this.cameraE3_3 * (float) minZ;
					depthSe = this.cameraTz + this.cameraE3_1 * (float) maxX + heightZ + this.cameraE3_3 * (float) minZ;
					depthNe = this.cameraTz + this.cameraE3_1 * (float) maxX + heightZ + this.cameraE3_3 * (float) maxZ;
					depthNw = this.cameraTz + this.cameraE3_1 * (float) minX + heightZ + this.cameraE3_3 * (float) maxZ;
				}
				if (resource.fogActive) {
					fog = (int) (depthSw - (float) resource.fogPlane);
					if (fog > 0) {
						fogSw = fog;
						if (fog > 255) {
							fogSw = 255;
						}
					}
					fog = (int) (depthSe - (float) resource.fogPlane);
					if (fog > 0) {
						fogSe = fog;
						if (fog > 255) {
							fogSe = 255;
						}
					}
					fog = (int) (depthNe - (float) resource.fogPlane);
					if (fog > 0) {
						fogNe = fog;
						if (fog > 255) {
							fogNe = 255;
						}
					}
					fog = (int) (depthNw - (float) resource.fogPlane);
					if (fog > 0) {
						fogNw = fog;
						if (fog > 255) {
							fogNw = 255;
						}
					}
				}
				@Pc(324) float heightX = this.cameraE1_2 * (float) heightSw;
				@Pc(330) float heightY = this.cameraE2_2 * (float) heightSw;
				if (this.depthOverride == -1) {
					viewXSw = this.cameraTx + this.cameraE1_1 * (float) minX + heightX + this.cameraE1_3 * (float) minZ;
					screenXSw = rasterizer.minX + (int) (viewXSw * (float) this.toolkit.projectionScaleX / depthSw);
					viewYSw = this.cameraTy + this.cameraE2_1 * (float) minX + heightY + this.cameraE2_3 * (float) minZ;
					screenYSw = rasterizer.minY + (int) (viewYSw * (float) this.toolkit.projectionScaleY / depthSw);
					viewXSe = this.cameraTx + this.cameraE1_1 * (float) maxX + heightX + this.cameraE1_3 * (float) minZ;
					screenXSe = rasterizer.minX + (int) (viewXSe * (float) this.toolkit.projectionScaleX / depthSe);
					viewYSe = this.cameraTy + this.cameraE2_1 * (float) maxX + heightY + this.cameraE2_3 * (float) minZ;
					screenYSe = rasterizer.minY + (int) (viewYSe * (float) this.toolkit.projectionScaleY / depthSe);
					viewXNe = this.cameraTx + this.cameraE1_1 * (float) maxX + heightX + this.cameraE1_3 * (float) maxZ;
					screenXNe = rasterizer.minX + (int) (viewXNe * (float) this.toolkit.projectionScaleX / depthNe);
					viewYNe = this.cameraTy + this.cameraE2_1 * (float) maxX + heightY + this.cameraE2_3 * (float) maxZ;
					screenYNe = rasterizer.minY + (int) (viewYNe * (float) this.toolkit.projectionScaleY / depthNe);
					viewXNw = this.cameraTx + this.cameraE1_1 * (float) minX + heightX + this.cameraE1_3 * (float) maxZ;
					screenXNw = rasterizer.minX + (int) (viewXNw * (float) this.toolkit.projectionScaleX / depthNw);
					viewYNw = this.cameraTy + this.cameraE2_1 * (float) minX + heightY + this.cameraE2_3 * (float) maxZ;
					screenYNw = rasterizer.minY + (int) (viewYNw * (float) this.toolkit.projectionScaleY / depthNw);
				} else {
					viewXSw = this.cameraTx + this.cameraE1_1 * (float) minX + heightX + this.cameraE1_3 * (float) minZ;
					screenXSw = rasterizer.minX + (int) (viewXSw * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYSw = this.cameraTy + this.cameraE2_1 * (float) minX + heightY + this.cameraE2_3 * (float) minZ;
					screenYSw = rasterizer.minY + (int) (viewYSw * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
					viewXSe = this.cameraTx + this.cameraE1_1 * (float) maxX + heightX + this.cameraE1_3 * (float) minZ;
					screenXSe = rasterizer.minX + (int) (viewXSe * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYSe = this.cameraTy + this.cameraE2_1 * (float) maxX + heightY + this.cameraE2_3 * (float) minZ;
					screenYSe = rasterizer.minY + (int) (viewYSe * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
					viewXNe = this.cameraTx + this.cameraE1_1 * (float) maxX + heightX + this.cameraE1_3 * (float) maxZ;
					screenXNe = rasterizer.minX + (int) (viewXNe * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYNe = this.cameraTy + this.cameraE2_1 * (float) maxX + heightY + this.cameraE2_3 * (float) maxZ;
					screenYNe = rasterizer.minY + (int) (viewYNe * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
					viewXNw = this.cameraTx + this.cameraE1_1 * (float) minX + heightX + this.cameraE1_3 * (float) maxZ;
					screenXNw = rasterizer.minX + (int) (viewXNw * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYNw = this.cameraTy + this.cameraE2_1 * (float) minX + heightY + this.cameraE2_3 * (float) maxZ;
					screenYNw = rasterizer.minY + (int) (viewYNw * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
				}
			}
			@Pc(1842) boolean waterEffect = javaSimpleBlendedTile.texture != -1 && this.isWaterEffect(this.toolkit.materialProvider.getMetrics(javaSimpleBlendedTile.texture).effectType);
			if (this.depthOverride == -1) {
				int fogTotal = fogSe + fogNe + fogNw;
				if ((screenXNe - screenXNw) * (screenYSe - screenYNw) - (screenYNe - screenYNw) * (screenXSe - screenXNw) > 0) {
					rasterizer.clamp = screenXNe < 0 || screenXNw < 0 || screenXSe < 0 || screenXNe > rasterizer.width || screenXNw > rasterizer.width || screenXSe > rasterizer.width;
					if (fogTotal >= 765) {
						rasterizer.renderFlatTriangleRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, (float) depthNe, (float) depthNw, (float) depthSe, resource.fogColour);
					} else if (fogTotal > 0) {
						if (javaSimpleBlendedTile.texture >= 0) {
							int alphaMask = -16777216;
							if (waterEffect) {
								alphaMask = -1694498816;
							}
							rasterizer.renderTexturedTriangleRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, depthNe, depthNw, depthSe, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, alphaMask | javaSimpleBlendedTile.colourNe & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourNw & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourSe & 0xFFFFFF, resource.fogColour, fogNe, fogNw, fogSe, javaSimpleBlendedTile.texture);
						} else {
							if (waterEffect) {
								rasterizer.alpha = 100;
							}
							rasterizer.renderTriangleRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, (float) depthNe, (float) depthNw, (float) depthSe, ColorUtil.alphaBlend(javaSimpleBlendedTile.colourNe, fogNe << 24 | resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourNw, fogNw << 24 | resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourSe, fogSe << 24 | resource.fogColour));
							rasterizer.alpha = 0;
						}
					} else if (javaSimpleBlendedTile.texture >= 0) {
						int alphaMask = -16777216;
						if (waterEffect) {
							alphaMask = -1694498816;
						}
						rasterizer.renderTexturedTriangleRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, depthNe, depthNw, depthSe, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, alphaMask | javaSimpleBlendedTile.colourNe & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourNw & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourSe & 0xFFFFFF, 0, 0, 0, 0, javaSimpleBlendedTile.texture);
					} else {
						if (waterEffect) {
							rasterizer.alpha = 100;
						}
						rasterizer.renderTriangleRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, (float) depthNe, (float) depthNw, (float) depthSe, javaSimpleBlendedTile.colourNe, javaSimpleBlendedTile.colourNw, javaSimpleBlendedTile.colourSe);
						rasterizer.alpha = 0;
					}
				}
				fogTotal = fogSw + fogSe + fogNw;
				if ((screenXSw - screenXSe) * (screenYNw - screenYSe) - (screenYSw - screenYSe) * (screenXNw - screenXSe) > 0) {
					rasterizer.clamp = screenXSw < 0 || screenXSe < 0 || screenXNw < 0 || screenXSw > rasterizer.width || screenXSe > rasterizer.width || screenXNw > rasterizer.width;
					if (fogTotal < 765) {
						if (waterEffect) {
							rasterizer.alpha = -1694498816;
						}
						if (fogTotal > 0) {
							if (javaSimpleBlendedTile.texture >= 0) {
								int alphaMask = -16777216;
								if (waterEffect) {
									alphaMask = -1694498816;
								}
								rasterizer.renderTexturedTriangleRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, depthSw, depthSe, depthNw, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, alphaMask | javaSimpleBlendedTile.colourSw & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourSe & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourNw & 0xFFFFFF, resource.fogColour, fogSw, fogSe, fogNw, javaSimpleBlendedTile.texture);
								return;
							}
							if (waterEffect) {
								rasterizer.alpha = 100;
							}
							rasterizer.renderTriangleRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, (float) depthSw, (float) depthSe, (float) depthNw, ColorUtil.alphaBlend(javaSimpleBlendedTile.colourSw, fogSw << 24 | resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourSe, fogSe << 24 | resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourNw, fogNw << 24 | resource.fogColour));
							rasterizer.alpha = 0;
							return;
						}
						if (javaSimpleBlendedTile.texture >= 0) {
							int alphaMask = -16777216;
							if (waterEffect) {
								alphaMask = -1694498816;
							}
							rasterizer.renderTexturedTriangleRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, depthSw, depthSe, depthNw, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, alphaMask | javaSimpleBlendedTile.colourSw & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourSe & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourNw & 0xFFFFFF, 0, 0, 0, 0, javaSimpleBlendedTile.texture);
							return;
						}
						if (waterEffect) {
							rasterizer.alpha = 100;
						}
						rasterizer.renderTriangleRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, (float) depthSw, (float) depthSe, (float) depthNw, javaSimpleBlendedTile.colourSw, javaSimpleBlendedTile.colourSe, javaSimpleBlendedTile.colourNw);
						rasterizer.alpha = 0;
						return;
					}
					rasterizer.renderFlatTriangleRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, (float) depthSw, (float) depthSe, (float) depthNw, resource.fogColour);
					return;
				}
			} else {
				int fogTotal = fogSe + fogNe + fogNw;
				if ((screenXNe - screenXNw) * (screenYSe - screenYNw) - (screenYNe - screenYNw) * (screenXSe - screenXNw) > 0) {
					rasterizer.clamp = screenXNe < 0 || screenXNw < 0 || screenXSe < 0 || screenXNe > rasterizer.width || screenXNw > rasterizer.width || screenXSe > rasterizer.width;
					if (fogTotal < 765) {
						if (waterEffect) {
							rasterizer.alpha = -1694498816;
						}
						if (fogTotal > 0) {
							if (javaSimpleBlendedTile.texture >= 0) {
								int alphaMask = -16777216;
								if (waterEffect) {
									alphaMask = -1694498816;
								}
								rasterizer.renderTexturedTriangleRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, depthNe, depthNw, depthSe, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, alphaMask | javaSimpleBlendedTile.colourNe & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourNw & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourSe & 0xFFFFFF, resource.fogColour, fogNe, fogNw, fogSe, javaSimpleBlendedTile.texture);
							} else {
								if (waterEffect) {
									rasterizer.alpha = 100;
								}
								rasterizer.renderTriangleRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, (float) depthNe, (float) depthNw, (float) depthSe, ColorUtil.alphaBlend(javaSimpleBlendedTile.colourNe, fogNe << 24 | resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourNw, fogNw << 24 | resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourSe, fogSe << 24 | resource.fogColour));
								rasterizer.alpha = 0;
							}
						} else if (javaSimpleBlendedTile.texture >= 0) {
							int alphaMask = -16777216;
							if (waterEffect) {
								alphaMask = -1694498816;
							}
							rasterizer.renderTexturedTriangleRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, depthNe, depthNw, depthSe, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, alphaMask | javaSimpleBlendedTile.colourNe & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourNw & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourSe & 0xFFFFFF, 0, 0, 0, 0, javaSimpleBlendedTile.texture);
						} else {
							if (waterEffect) {
								rasterizer.alpha = 100;
							}
							rasterizer.renderTriangleRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, (float) depthNe, (float) depthNw, (float) depthSe, javaSimpleBlendedTile.colourNe, javaSimpleBlendedTile.colourNw, javaSimpleBlendedTile.colourSe);
							rasterizer.alpha = 0;
						}
					} else {
						rasterizer.renderFlatTriangleRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, (float) depthNe, (float) depthNw, (float) depthSe, resource.fogColour);
					}
				}
				fogTotal = fogSw + fogSe + fogNw;
				if ((screenXSw - screenXSe) * (screenYNw - screenYSe) - (screenYSw - screenYSe) * (screenXNw - screenXSe) > 0) {
					rasterizer.clamp = screenXSw < 0 || screenXSe < 0 || screenXNw < 0 || screenXSw > rasterizer.width || screenXSe > rasterizer.width || screenXNw > rasterizer.width;
					if (fogTotal < 765) {
						if (waterEffect) {
							rasterizer.alpha = -1694498816;
						}
						if (fogTotal > 0) {
							if (javaSimpleBlendedTile.texture >= 0) {
								int alphaMask = -16777216;
								if (waterEffect) {
									alphaMask = -1694498816;
								}
								rasterizer.renderTexturedTriangleRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, depthSw, depthSe, depthNw, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, alphaMask | javaSimpleBlendedTile.colourSw & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourSe & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourNw & 0xFFFFFF, resource.fogColour, fogSw, fogSe, fogNw, javaSimpleBlendedTile.texture);
								return;
							}
							if (waterEffect) {
								rasterizer.alpha = 100;
							}
							rasterizer.renderTriangleRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, (float) depthSw, (float) depthSe, (float) depthNw, ColorUtil.alphaBlend(javaSimpleBlendedTile.colourSw, fogSw << 24 | resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourSe, fogSe << 24 | resource.fogColour), ColorUtil.alphaBlend(javaSimpleBlendedTile.colourNw, fogNw << 24 | resource.fogColour));
							rasterizer.alpha = 0;
							return;
						}
						if (javaSimpleBlendedTile.texture >= 0) {
							int alphaMask = -16777216;
							if (waterEffect) {
								alphaMask = -1694498816;
							}
							rasterizer.renderTexturedTriangleRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, depthSw, depthSe, depthNw, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, alphaMask | javaSimpleBlendedTile.colourSw & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourSe & 0xFFFFFF, alphaMask | javaSimpleBlendedTile.colourNw & 0xFFFFFF, 0, 0, 0, 0, javaSimpleBlendedTile.texture);
							return;
						}
						if (waterEffect) {
							rasterizer.alpha = 100;
						}
						rasterizer.renderTriangleRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, (float) depthSw, (float) depthSe, (float) depthNw, javaSimpleBlendedTile.colourSw, javaSimpleBlendedTile.colourSe, javaSimpleBlendedTile.colourNw);
						rasterizer.alpha = 0;
						return;
					}
					rasterizer.renderFlatTriangleRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, (float) depthSw, (float) depthSe, (float) depthNw, resource.fogColour);
				}
			}
		}
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(III[[ZZI)V")
	@Override
	public void renderTiles(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int radius, @OriginalArg(3) boolean[][] visibility, @OriginalArg(4) boolean belowLowestPlane, @OriginalArg(5) int skipFlags) {
		@Pc(3) JavaMatrix javaMatrix = this.toolkit.camera;
		this.depthOverride = -1;
		this.cameraE1_1 = javaMatrix.e1_1;
		this.cameraE1_2 = javaMatrix.e1_2;
		this.cameraE1_3 = javaMatrix.e1_3;
		this.cameraTx = javaMatrix.tx;
		this.cameraE2_1 = javaMatrix.e2_1;
		this.cameraE2_2 = javaMatrix.e2_2;
		this.cameraE2_3 = javaMatrix.e2_3;
		this.cameraTy = javaMatrix.ty;
		this.cameraE3_1 = javaMatrix.e3_1;
		this.cameraE3_2 = javaMatrix.e3_2;
		this.cameraE3_3 = javaMatrix.e3_3;
		this.cameraTz = javaMatrix.tz;
		for (@Pc(56) int i = 0; i < radius + radius; i++) {
			for (@Pc(59) int j = 0; j < radius + radius; j++) {
				if (visibility[i][j]) {
					@Pc(72) int tileX = x + i - radius;
					@Pc(78) int tileZ = z + j - radius;
					if (tileX >= 0 && tileX < super.width && tileZ >= 0 && tileZ < super.length) {
						this.renderTile(tileX, tileZ, skipFlags);
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(III)V")
	private void renderTile(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int skipFlags) {
		@Pc(4) JavaThreadResource javaThreadResource = this.toolkit.threadResource(Thread.currentThread());
		javaThreadResource.rasterizer.alpha = 0;
		if (this.simpleBlendedTiles != null) {
			this.renderBlendedTile(x, z, javaThreadResource.water, javaThreadResource, javaThreadResource.rasterizer, javaThreadResource.screenX, javaThreadResource.screenY, javaThreadResource.depths, javaThreadResource.fogLevels, skipFlags);
		} else if (this.simpleTiles != null) {
			this.renderUnblendedTile(x, z, javaThreadResource.rasterizer, javaThreadResource.screenX, javaThreadResource.screenY, javaThreadResource.depths, javaThreadResource.fogLevels, skipFlags);
		} else if (this.genericBlendedTiles != null) {
			this.renderGenericBlendedTile(x, z, javaThreadResource.water, javaThreadResource, javaThreadResource.rasterizer, javaThreadResource.screenX, javaThreadResource.screenY, javaThreadResource.depths, javaThreadResource.fogLevels, skipFlags);
		}
	}

	@OriginalMember(owner = "client!cr", name = "wa", descriptor = "(Lclient!r;IIIIZ)V")
	@Override
	public void wa(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int heightOffset, @OriginalArg(3) int z, @OriginalArg(4) int arg4, @OriginalArg(5) boolean arg5) {
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(IIIIIII[[ZLclient!pga;Lclient!mp;[I[I)V")
	private void drawBlendedMinimapTiles(@OriginalArg(3) int x1, @OriginalArg(4) int z1, @OriginalArg(5) int x2, @OriginalArg(6) int z2, @OriginalArg(7) boolean[][] visibility, @OriginalArg(8) JavaThreadResource resource, @OriginalArg(9) Rasterizer rasterizer, @OriginalArg(10) int[] screenX, @OriginalArg(11) int[] screenY) {
		@Pc(7) int mapHeight = (z2 - z1) * 1024 / 256;
		@Pc(14) boolean zWrite = resource.zWrite;
		this.toolkit.C(false);
		rasterizer.fastScanline = false;
		rasterizer.halfBlend = false;
		@Pc(26) int drawX = 0;
		@Pc(30) int drawY = mapHeight;
		for (@Pc(32) int i = x1; i < x2; i++) {
			for (@Pc(35) int j = z1; j < z2; j++) {
				if (visibility[i - x1][j - z1]) {
					@Pc(81) int local81;
					@Pc(370) int face;
					@Pc(375) int a;
					@Pc(379) int b;
					@Pc(383) int c;
					@Pc(387) int xA;
					@Pc(391) int xB;
					@Pc(395) int xC;
					@Pc(399) int yA;
					@Pc(403) int yB;
					@Pc(407) int yC;
					@Pc(432) int colour;
					if (this.simpleBlendedTiles == null) {
						if (this.genericBlendedTiles[i][j] != null) {
							@Pc(573) JavaGenericBlendedTile javaGenericBlendedTile = this.genericBlendedTiles[i][j];
							for (int vertexIndex = 0; vertexIndex < javaGenericBlendedTile.vertexCount; vertexIndex++) {
								screenX[vertexIndex] = drawX + javaGenericBlendedTile.verticesX[vertexIndex] * 4 / super.tileUnits;
								screenY[vertexIndex] = drawY - javaGenericBlendedTile.verticesZ[vertexIndex] * 4 / super.tileUnits;
							}
							for (face = 0; face < javaGenericBlendedTile.faceCount; face++) {
								a = face * 3;
								b = a + 1;
								c = b + 1;
								xA = screenX[a];
								xB = screenX[b];
								xC = screenX[c];
								yA = screenY[a];
								yB = screenY[b];
								yC = screenY[c];
								if (javaGenericBlendedTile.faceBlendedColours == null || javaGenericBlendedTile.faceBlendedColours[face] == 0) {
									rasterizer.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, 100.0F, 100.0F, 100.0F, javaGenericBlendedTile.vertexColours[a], javaGenericBlendedTile.vertexColours[b], javaGenericBlendedTile.vertexColours[c]);
								} else {
									colour = javaGenericBlendedTile.faceBlendedColours[face];
									rasterizer.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, 100.0F, 100.0F, 100.0F, colour, colour, colour);
								}
							}
						}
					} else if (this.simpleBlendedTiles[i][j] != null) {
						@Pc(63) JavaSimpleBlendedTile javaSimpleBlendedTile = this.simpleBlendedTiles[i][j];
						if (javaSimpleBlendedTile.texture != -1 && (javaSimpleBlendedTile.flags & 0x2) == 0 && javaSimpleBlendedTile.blendedColour == 0) {
							int textureColour = this.toolkit.textureHsl(javaSimpleBlendedTile.texture);
							rasterizer.renderTriangleHslRgb((float) (drawY - 4), (float) (drawY - 4), (float) drawY, (float) (drawX + 4), (float) drawX, (float) (drawX + 4), 100.0F, 100.0F, 100.0F, (float) JavaModel.applyLightness(javaSimpleBlendedTile.colourNe, textureColour), (float) JavaModel.applyLightness(javaSimpleBlendedTile.colourNw, textureColour), (float) JavaModel.applyLightness(javaSimpleBlendedTile.colourSe, textureColour));
							rasterizer.renderTriangleHslRgb((float) drawY, (float) drawY, (float) (drawY - 4), (float) drawX, (float) (drawX + 4), (float) drawX, 100.0F, 100.0F, 100.0F, (float) JavaModel.applyLightness(javaSimpleBlendedTile.colourSw, textureColour), (float) JavaModel.applyLightness(javaSimpleBlendedTile.colourSe, textureColour), (float) JavaModel.applyLightness(javaSimpleBlendedTile.colourNw, textureColour));
						} else if (javaSimpleBlendedTile.blendedColour == 0) {
							rasterizer.renderTriangleRgb((float) (drawY - 4), (float) (drawY - 4), (float) drawY, (float) (drawX + 4), (float) drawX, (float) (drawX + 4), 100.0F, 100.0F, 100.0F, javaSimpleBlendedTile.colourNe, javaSimpleBlendedTile.colourNw, javaSimpleBlendedTile.colourSe);
							rasterizer.renderTriangleRgb((float) drawY, (float) drawY, (float) (drawY - 4), (float) drawX, (float) (drawX + 4), (float) drawX, 100.0F, 100.0F, 100.0F, javaSimpleBlendedTile.colourSw, javaSimpleBlendedTile.colourSe, javaSimpleBlendedTile.colourNw);
						} else {
							int blendedColour = javaSimpleBlendedTile.blendedColour;
							rasterizer.renderTriangleRgb((float) (drawY - 4), (float) (drawY - 4), (float) drawY, (float) (drawX + 4), (float) drawX, (float) (drawX + 4), 100.0F, 100.0F, 100.0F, ColorUtil.alphaBlend(blendedColour, javaSimpleBlendedTile.colourNe & 0xFF000000), ColorUtil.alphaBlend(blendedColour, javaSimpleBlendedTile.colourNw & 0xFF000000), ColorUtil.alphaBlend(blendedColour, javaSimpleBlendedTile.colourSe & 0xFF000000));
							rasterizer.renderTriangleRgb((float) drawY, (float) drawY, (float) (drawY - 4), (float) drawX, (float) (drawX + 4), (float) drawX, 100.0F, 100.0F, 100.0F, ColorUtil.alphaBlend(blendedColour, javaSimpleBlendedTile.colourSw & 0xFF000000), ColorUtil.alphaBlend(blendedColour, javaSimpleBlendedTile.colourSe & 0xFF000000), ColorUtil.alphaBlend(blendedColour, javaSimpleBlendedTile.colourNw & 0xFF000000));
						}
					} else if (this.complexBlendedTiles[i][j] != null) {
						@Pc(332) JavaComplexBlendedTile javaComplexBlendedTile = this.complexBlendedTiles[i][j];
						for (int vertexIndex = 0; vertexIndex < javaComplexBlendedTile.vertexCount; vertexIndex++) {
							screenX[vertexIndex] = drawX + javaComplexBlendedTile.verticesX[vertexIndex] * 4 / super.tileUnits;
							screenY[vertexIndex] = drawY - javaComplexBlendedTile.verticesZ[vertexIndex] * 4 / super.tileUnits;
						}
						for (face = 0; face < javaComplexBlendedTile.faceCount; face++) {
							a = face * 3;
							b = a + 1;
							c = b + 1;
							xA = screenX[a];
							xB = screenX[b];
							xC = screenX[c];
							yA = screenY[a];
							yB = screenY[b];
							yC = screenY[c];
							if (javaComplexBlendedTile.faceBlendedColours != null && javaComplexBlendedTile.faceBlendedColours[face] != 0 && (javaComplexBlendedTile.faceTextures == null || javaComplexBlendedTile.faceTextures != null && javaComplexBlendedTile.faceTextures[face] == -1)) {
								colour = javaComplexBlendedTile.faceBlendedColours[face];
								rasterizer.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, 100.0F, 100.0F, 100.0F, ColorUtil.alphaBlend(colour, -(javaComplexBlendedTile.vertexColours[a] & -16777216) - 16777216), ColorUtil.alphaBlend(colour, -(javaComplexBlendedTile.vertexColours[b] & -16777216) - 16777216), ColorUtil.alphaBlend(colour, -(javaComplexBlendedTile.vertexColours[c] & -16777216) - 16777216));
							} else if (javaComplexBlendedTile.faceTextures == null || javaComplexBlendedTile.faceTextures[face] == -1) {
								rasterizer.renderTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, 100.0F, 100.0F, 100.0F, javaComplexBlendedTile.vertexColours[a], javaComplexBlendedTile.vertexColours[b], javaComplexBlendedTile.vertexColours[c]);
							} else {
								colour = this.toolkit.textureHsl(javaComplexBlendedTile.faceTextures[face]);
								rasterizer.renderTriangleHslRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, 100.0F, 100.0F, 100.0F, (float) colour, (float) colour, (float) colour);
							}
						}
					}
				}
				drawY -= 4;
			}
			drawY = mapHeight;
			drawX += 4;
		}
		rasterizer.fastScanline = true;
		this.toolkit.C(zWrite);
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(IILclient!mp;[I[I[I[II)V")
	private void renderUnblendedTile(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) Rasterizer rasterizer, @OriginalArg(3) int[] screenX, @OriginalArg(4) int[] screenY, @OriginalArg(5) int[] depths, @OriginalArg(6) int[] fogLevels, @OriginalArg(7) int skipFlags) {
		@Pc(6) PlainTile javaSimpleTile = this.simpleTiles[x][z];
		@Pc(41) int local41;
		@Pc(51) int local51;
		@Pc(467) int local467;
		@Pc(497) int local497;
		if (javaSimpleTile == null) {
			@Pc(2129) JavaComplexTile javaComplexTile = this.complexTiles[x][z];
			if (javaComplexTile != null) {
				if (skipFlags != 0) {
					if ((javaComplexTile.flags & 0x4) == 0) {
						if ((skipFlags & 0x2) != 0) {
							return;
						}
					} else if ((skipFlags & 0x1) != 0) {
						return;
					}
				}
				@Pc(2170) short vertexY;
				@Pc(2230) float viewX;
				@Pc(2251) float viewY;
				@Pc(2201) float viewZ;
				if (this.depthOverride == -1) {
					for (int vertexIndex = 0; vertexIndex < javaComplexTile.vertexCount; vertexIndex++) {
						int vertexX = javaComplexTile.verticesX[vertexIndex] + (x << super.tileScale);
						vertexY = javaComplexTile.verticesY[vertexIndex];
						int vertexZ = javaComplexTile.verticesZ[vertexIndex] + (z << super.tileScale);
						viewZ = this.cameraTz + this.cameraE3_1 * (float) vertexX + this.cameraE3_2 * (float) vertexY + this.cameraE3_3 * (float) vertexZ;
						if (viewZ <= (float) this.toolkit.zNear) {
							return;
						}
						viewX = this.cameraTx + this.cameraE1_1 * (float) vertexX + this.cameraE1_2 * (float) vertexY + this.cameraE1_3 * (float) vertexZ;
						viewY = this.cameraTy + this.cameraE2_1 * (float) vertexX + this.cameraE2_2 * (float) vertexY + this.cameraE2_3 * (float) vertexZ;
						screenX[vertexIndex] = rasterizer.minX + (int) (viewX * (float) this.toolkit.projectionScaleX / viewZ);
						screenY[vertexIndex] = rasterizer.minY + (int) (viewY * (float) this.toolkit.projectionScaleY / viewZ);
						depths[vertexIndex] = (int) viewZ;
					}
				} else {
					for (int vertexIndex = 0; vertexIndex < javaComplexTile.vertexCount; vertexIndex++) {
						int vertexX = javaComplexTile.verticesX[vertexIndex] + (x << super.tileScale);
						vertexY = javaComplexTile.verticesY[vertexIndex];
						int vertexZ = javaComplexTile.verticesZ[vertexIndex] + (z << super.tileScale);
						viewZ = this.cameraTz + this.cameraE3_1 * (float) vertexX + this.cameraE3_2 * (float) vertexY + this.cameraE3_3 * (float) vertexZ;
						viewX = this.cameraTx + this.cameraE1_1 * (float) vertexX + this.cameraE1_2 * (float) vertexY + this.cameraE1_3 * (float) vertexZ;
						viewY = this.cameraTy + this.cameraE2_1 * (float) vertexX + this.cameraE2_2 * (float) vertexY + this.cameraE2_3 * (float) vertexZ;
						screenX[vertexIndex] = rasterizer.minX + (int) (viewX * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
						screenY[vertexIndex] = rasterizer.minY + (int) (viewY * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
						depths[vertexIndex] = (int) viewZ;
					}
				}
				@Pc(2442) short a;
				@Pc(2447) short b;
				@Pc(2452) short c;
				@Pc(2460) int xB;
				@Pc(2464) int xC;
				@Pc(2468) int yA;
				@Pc(2472) int yB;
				@Pc(2476) int yC;
				if (javaComplexTile.faceTextures != null) {
					@Pc(2650) int colour;
					@Pc(2520) short texture;
					if (this.depthOverride == -1) {
						for (int faceIndex = 0; faceIndex < javaComplexTile.faceCount; faceIndex++) {
							a = javaComplexTile.faceA[faceIndex];
							b = javaComplexTile.faceB[faceIndex];
							c = javaComplexTile.faceC[faceIndex];
							int xA = screenX[a];
							xB = screenX[b];
							xC = screenX[c];
							yA = screenY[a];
							yB = screenY[b];
							yC = screenY[c];
							if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
								rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width;
								texture = javaComplexTile.faceTextures[faceIndex];
								if (texture == -1) {
									colour = javaComplexTile.faceColours[faceIndex];
									if (colour != -1) {
										rasterizer.renderTriangleHslRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[a], (float) depths[b], (float) depths[c], (float) JavaModel.applyLightness(javaComplexTile.verticesLight[a], colour), (float) JavaModel.applyLightness(javaComplexTile.verticesLight[b], colour), (float) JavaModel.applyLightness(javaComplexTile.verticesLight[c], colour));
									}
								} else {
									rasterizer.renderTexturedTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[a], (float) depths[b], (float) depths[c], (float) javaComplexTile.verticesX[a] / (float) super.tileUnits, (float) javaComplexTile.verticesX[b] / (float) super.tileUnits, (float) javaComplexTile.verticesX[c] / (float) super.tileUnits, (float) javaComplexTile.verticesZ[a] / (float) super.tileUnits, (float) javaComplexTile.verticesZ[b] / (float) super.tileUnits, (float) javaComplexTile.verticesZ[c] / (float) super.tileUnits, ColorUtil.hsvToRgbTable[javaComplexTile.verticesLight[a] & 0xFFFF] & 0xFFFFFF | 0xFF000000, ColorUtil.hsvToRgbTable[javaComplexTile.verticesLight[b] & 0xFFFF] & 0xFFFFFF | 0xFF000000, ColorUtil.hsvToRgbTable[javaComplexTile.verticesLight[c] & 0xFFFF] & 0xFFFFFF | 0xFF000000, 0, 0, 0, 0, texture);
								}
							}
						}
						return;
					}
					for (int faceIndex = 0; faceIndex < javaComplexTile.faceCount; faceIndex++) {
						a = javaComplexTile.faceA[faceIndex];
						b = javaComplexTile.faceB[faceIndex];
						c = javaComplexTile.faceC[faceIndex];
						int xA = screenX[a];
						xB = screenX[b];
						xC = screenX[c];
						yA = screenY[a];
						yB = screenY[b];
						yC = screenY[c];
						if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
							rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width;
							texture = javaComplexTile.faceTextures[faceIndex];
							if (texture == -1) {
								colour = javaComplexTile.faceColours[faceIndex];
								if (colour != -1) {
									rasterizer.renderTriangleHslRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[a], (float) depths[b], (float) depths[c], (float) JavaModel.applyLightness(javaComplexTile.verticesLight[a], colour), (float) JavaModel.applyLightness(javaComplexTile.verticesLight[b], colour), (float) JavaModel.applyLightness(javaComplexTile.verticesLight[c], colour));
								}
							} else {
								rasterizer.renderTexturedTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[a], (float) depths[b], (float) depths[c], (float) javaComplexTile.verticesX[a] / (float) super.tileUnits, (float) javaComplexTile.verticesX[b] / (float) super.tileUnits, (float) javaComplexTile.verticesX[c] / (float) super.tileUnits, (float) javaComplexTile.verticesZ[a] / (float) super.tileUnits, (float) javaComplexTile.verticesZ[b] / (float) super.tileUnits, (float) javaComplexTile.verticesZ[c] / (float) super.tileUnits, ColorUtil.hsvToRgbTable[javaComplexTile.verticesLight[a] & 0xFFFF] & 0xFFFFFF | 0xFF000000, ColorUtil.hsvToRgbTable[javaComplexTile.verticesLight[b] & 0xFFFF] & 0xFFFFFF | 0xFF000000, ColorUtil.hsvToRgbTable[javaComplexTile.verticesLight[c] & 0xFFFF] & 0xFFFFFF | 0xFF000000, 0, 0, 0, 0, texture);
							}
						}
					}
					return;
				}
				for (int faceIndex = 0; faceIndex < javaComplexTile.faceCount; faceIndex++) {
					a = javaComplexTile.faceA[faceIndex];
					b = javaComplexTile.faceB[faceIndex];
					c = javaComplexTile.faceC[faceIndex];
					int xA = screenX[a];
					xB = screenX[b];
					xC = screenX[c];
					yA = screenY[a];
					yB = screenY[b];
					yC = screenY[c];
					if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
						@Pc(3047) int faceColour = javaComplexTile.faceColours[faceIndex];
						if (faceColour != -1) {
							rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width;
							rasterizer.renderTriangleHslRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[a], (float) depths[b], (float) depths[c], (float) JavaModel.applyLightness(javaComplexTile.verticesLight[a], faceColour), (float) JavaModel.applyLightness(javaComplexTile.verticesLight[b], faceColour), (float) JavaModel.applyLightness(javaComplexTile.verticesLight[c], faceColour));
						}
					}
				}
			}
		} else if ((javaSimpleTile.functionMask & 0x2) == 0) {
			if (skipFlags != 0) {
				if ((javaSimpleTile.functionMask & 0x4) == 0) {
					if ((skipFlags & 0x2) != 0) {
						return;
					}
				} else if ((skipFlags & 0x1) != 0) {
					return;
				}
			}
			@Pc(36) int minX = x * super.tileUnits;
			int maxX = minX + super.tileUnits;
			@Pc(46) int minZ = z * super.tileUnits;
			int maxZ = minZ + super.tileUnits;
			@Pc(484) float viewYNw;
			@Pc(90) float depthSw;
			@Pc(115) float depthSe;
			@Pc(140) float depthNe;
			@Pc(165) float depthNw;
			@Pc(63) int heightSw;
			@Pc(287) int screenXSw;
			@Pc(347) int screenXSe;
			@Pc(407) int screenXNe;
			@Pc(317) int screenYSw;
			@Pc(377) int screenYSe;
			@Pc(437) int screenYNe;
			@Pc(274) float viewXSw;
			@Pc(334) float viewXSe;
			@Pc(394) float viewXNe;
			@Pc(454) float viewXNw;
			@Pc(304) float viewYSw;
			@Pc(364) float viewYSe;
			@Pc(424) float viewYNe;
			int screenXNw;
			int screenYNw;
			if ((javaSimpleTile.functionMask & 0x1) == 0) {
				heightSw = super.tileHeights[x][z];
				@Pc(771) int heightSe = super.tileHeights[x + 1][z];
				@Pc(782) int heightNe = super.tileHeights[x + 1][z + 1];
				@Pc(791) int heightNw = super.tileHeights[x][z + 1];
				if (this.depthOverride == -1) {
					depthSw = this.cameraTz + this.cameraE3_1 * (float) minX + this.cameraE3_2 * (float) heightSw + this.cameraE3_3 * (float) minZ;
					if (depthSw <= (float) this.toolkit.zNear) {
						return;
					}
					depthSe = this.cameraTz + this.cameraE3_1 * (float) maxX + this.cameraE3_2 * (float) heightSe + this.cameraE3_3 * (float) minZ;
					if (depthSe <= (float) this.toolkit.zNear) {
						return;
					}
					depthNe = this.cameraTz + this.cameraE3_1 * (float) maxX + this.cameraE3_2 * (float) heightNe + this.cameraE3_3 * (float) maxZ;
					if (depthNe <= (float) this.toolkit.zNear) {
						return;
					}
					depthNw = this.cameraTz + this.cameraE3_1 * (float) minX + this.cameraE3_2 * (float) heightNw + this.cameraE3_3 * (float) maxZ;
					if (depthNw <= (float) this.toolkit.zNear) {
						return;
					}
					viewXSw = this.cameraTx + this.cameraE1_1 * (float) minX + this.cameraE1_2 * (float) heightSw + this.cameraE1_3 * (float) minZ;
					screenXSw = rasterizer.minX + (int) (viewXSw * (float) this.toolkit.projectionScaleX / depthSw);
					viewYSw = this.cameraTy + this.cameraE2_1 * (float) minX + this.cameraE2_2 * (float) heightSw + this.cameraE2_3 * (float) minZ;
					screenYSw = rasterizer.minY + (int) (viewYSw * (float) this.toolkit.projectionScaleY / depthSw);
					viewXSe = this.cameraTx + this.cameraE1_1 * (float) maxX + this.cameraE1_2 * (float) heightSe + this.cameraE1_3 * (float) minZ;
					screenXSe = rasterizer.minX + (int) (viewXSe * (float) this.toolkit.projectionScaleX / depthSe);
					viewYSe = this.cameraTy + this.cameraE2_1 * (float) maxX + this.cameraE2_2 * (float) heightSe + this.cameraE2_3 * (float) minZ;
					screenYSe = rasterizer.minY + (int) (viewYSe * (float) this.toolkit.projectionScaleY / depthSe);
					viewXNe = this.cameraTx + this.cameraE1_1 * (float) maxX + this.cameraE1_2 * (float) heightNe + this.cameraE1_3 * (float) maxZ;
					screenXNe = rasterizer.minX + (int) (viewXNe * (float) this.toolkit.projectionScaleX / depthNe);
					viewYNe = this.cameraTy + this.cameraE2_1 * (float) maxX + this.cameraE2_2 * (float) heightNe + this.cameraE2_3 * (float) maxZ;
					screenYNe = rasterizer.minY + (int) (viewYNe * (float) this.toolkit.projectionScaleY / depthNe);
					viewXNw = this.cameraTx + this.cameraE1_1 * (float) minX + this.cameraE1_2 * (float) heightNw + this.cameraE1_3 * (float) maxZ;
					screenXNw = rasterizer.minX + (int) (viewXNw * (float) this.toolkit.projectionScaleX / depthNw);
					viewYNw = this.cameraTy + this.cameraE2_1 * (float) minX + this.cameraE2_2 * (float) heightNw + this.cameraE2_3 * (float) maxZ;
					screenYNw = rasterizer.minY + (int) (viewYNw * (float) this.toolkit.projectionScaleY / depthNw);
				} else {
					depthSw = this.cameraTz + this.cameraE3_1 * (float) minX + this.cameraE3_2 * (float) heightSw + this.cameraE3_3 * (float) minZ;
					depthSe = this.cameraTz + this.cameraE3_1 * (float) maxX + this.cameraE3_2 * (float) heightSe + this.cameraE3_3 * (float) minZ;
					depthNe = this.cameraTz + this.cameraE3_1 * (float) maxX + this.cameraE3_2 * (float) heightNe + this.cameraE3_3 * (float) maxZ;
					depthNw = this.cameraTz + this.cameraE3_1 * (float) minX + this.cameraE3_2 * (float) heightNw + this.cameraE3_3 * (float) maxZ;
					viewXSw = this.cameraTx + this.cameraE1_1 * (float) minX + this.cameraE1_2 * (float) heightSw + this.cameraE1_3 * (float) minZ;
					screenXSw = rasterizer.minX + (int) (viewXSw * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYSw = this.cameraTy + this.cameraE2_1 * (float) minX + this.cameraE2_2 * (float) heightSw + this.cameraE2_3 * (float) minZ;
					screenYSw = rasterizer.minY + (int) (viewYSw * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
					viewXSe = this.cameraTx + this.cameraE1_1 * (float) maxX + this.cameraE1_2 * (float) heightSe + this.cameraE1_3 * (float) minZ;
					screenXSe = rasterizer.minX + (int) (viewXSe * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYSe = this.cameraTy + this.cameraE2_1 * (float) maxX + this.cameraE2_2 * (float) heightSe + this.cameraE2_3 * (float) minZ;
					screenYSe = rasterizer.minY + (int) (viewYSe * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
					viewXNe = this.cameraTx + this.cameraE1_1 * (float) maxX + this.cameraE1_2 * (float) heightNe + this.cameraE1_3 * (float) maxZ;
					screenXNe = rasterizer.minX + (int) (viewXNe * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYNe = this.cameraTy + this.cameraE2_1 * (float) maxX + this.cameraE2_2 * (float) heightNe + this.cameraE2_3 * (float) maxZ;
					screenYNe = rasterizer.minY + (int) (viewYNe * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
					viewXNw = this.cameraTx + this.cameraE1_1 * (float) minX + this.cameraE1_2 * (float) heightNw + this.cameraE1_3 * (float) maxZ;
					screenXNw = rasterizer.minX + (int) (viewXNw * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYNw = this.cameraTy + this.cameraE2_1 * (float) minX + this.cameraE2_2 * (float) heightNw + this.cameraE2_3 * (float) maxZ;
					screenYNw = rasterizer.minY + (int) (viewYNw * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
				}
			} else {
				heightSw = super.tileHeights[x][z];
				@Pc(69) float heightZ = this.cameraE3_2 * (float) heightSw;
				if (this.depthOverride == -1) {
					depthSw = this.cameraTz + this.cameraE3_1 * (float) minX + heightZ + this.cameraE3_3 * (float) minZ;
					if (depthSw <= (float) this.toolkit.zNear) {
						return;
					}
					depthSe = this.cameraTz + this.cameraE3_1 * (float) maxX + heightZ + this.cameraE3_3 * (float) minZ;
					if (depthSe <= (float) this.toolkit.zNear) {
						return;
					}
					depthNe = this.cameraTz + this.cameraE3_1 * (float) maxX + heightZ + this.cameraE3_3 * (float) maxZ;
					if (depthNe <= (float) this.toolkit.zNear) {
						return;
					}
					depthNw = this.cameraTz + this.cameraE3_1 * (float) minX + heightZ + this.cameraE3_3 * (float) maxZ;
					if (depthNw <= (float) this.toolkit.zNear) {
						return;
					}
				} else {
					depthSw = this.cameraTz + this.cameraE3_1 * (float) minX + heightZ + this.cameraE3_3 * (float) minZ;
					depthSe = this.cameraTz + this.cameraE3_1 * (float) maxX + heightZ + this.cameraE3_3 * (float) minZ;
					depthNe = this.cameraTz + this.cameraE3_1 * (float) maxX + heightZ + this.cameraE3_3 * (float) maxZ;
					depthNw = this.cameraTz + this.cameraE3_1 * (float) minX + heightZ + this.cameraE3_3 * (float) maxZ;
				}
				@Pc(247) float heightX = this.cameraE1_2 * (float) heightSw;
				@Pc(253) float heightY = this.cameraE2_2 * (float) heightSw;
				if (this.depthOverride == -1) {
					viewXSw = this.cameraTx + this.cameraE1_1 * (float) minX + heightX + this.cameraE1_3 * (float) minZ;
					screenXSw = rasterizer.minX + (int) (viewXSw * (float) this.toolkit.projectionScaleX / depthSw);
					viewYSw = this.cameraTy + this.cameraE2_1 * (float) minX + heightY + this.cameraE2_3 * (float) minZ;
					screenYSw = rasterizer.minY + (int) (viewYSw * (float) this.toolkit.projectionScaleY / depthSw);
					viewXSe = this.cameraTx + this.cameraE1_1 * (float) maxX + heightX + this.cameraE1_3 * (float) minZ;
					screenXSe = rasterizer.minX + (int) (viewXSe * (float) this.toolkit.projectionScaleX / depthSe);
					viewYSe = this.cameraTy + this.cameraE2_1 * (float) maxX + heightY + this.cameraE2_3 * (float) minZ;
					screenYSe = rasterizer.minY + (int) (viewYSe * (float) this.toolkit.projectionScaleY / depthSe);
					viewXNe = this.cameraTx + this.cameraE1_1 * (float) maxX + heightX + this.cameraE1_3 * (float) maxZ;
					screenXNe = rasterizer.minX + (int) (viewXNe * (float) this.toolkit.projectionScaleX / depthNe);
					viewYNe = this.cameraTy + this.cameraE2_1 * (float) maxX + heightY + this.cameraE2_3 * (float) maxZ;
					screenYNe = rasterizer.minY + (int) (viewYNe * (float) this.toolkit.projectionScaleY / depthNe);
					viewXNw = this.cameraTx + this.cameraE1_1 * (float) minX + heightX + this.cameraE1_3 * (float) maxZ;
					screenXNw = rasterizer.minX + (int) (viewXNw * (float) this.toolkit.projectionScaleX / depthNw);
					viewYNw = this.cameraTy + this.cameraE2_1 * (float) minX + heightY + this.cameraE2_3 * (float) maxZ;
					screenYNw = rasterizer.minY + (int) (viewYNw * (float) this.toolkit.projectionScaleY / depthNw);
				} else {
					viewXSw = this.cameraTx + this.cameraE1_1 * (float) minX + heightX + this.cameraE1_3 * (float) minZ;
					screenXSw = rasterizer.minX + (int) (viewXSw * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYSw = this.cameraTy + this.cameraE2_1 * (float) minX + heightY + this.cameraE2_3 * (float) minZ;
					screenYSw = rasterizer.minY + (int) (viewYSw * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
					viewXSe = this.cameraTx + this.cameraE1_1 * (float) maxX + heightX + this.cameraE1_3 * (float) minZ;
					screenXSe = rasterizer.minX + (int) (viewXSe * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYSe = this.cameraTy + this.cameraE2_1 * (float) maxX + heightY + this.cameraE2_3 * (float) minZ;
					screenYSe = rasterizer.minY + (int) (viewYSe * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
					viewXNe = this.cameraTx + this.cameraE1_1 * (float) maxX + heightX + this.cameraE1_3 * (float) maxZ;
					screenXNe = rasterizer.minX + (int) (viewXNe * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYNe = this.cameraTy + this.cameraE2_1 * (float) maxX + heightY + this.cameraE2_3 * (float) maxZ;
					screenYNe = rasterizer.minY + (int) (viewYNe * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
					viewXNw = this.cameraTx + this.cameraE1_1 * (float) minX + heightX + this.cameraE1_3 * (float) maxZ;
					screenXNw = rasterizer.minX + (int) (viewXNw * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
					viewYNw = this.cameraTy + this.cameraE2_1 * (float) minX + heightY + this.cameraE2_3 * (float) maxZ;
					screenYNw = rasterizer.minY + (int) (viewYNw * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
				}
			}
			if (this.depthOverride == -1) {
				if ((screenXNe - screenXNw) * (screenYSe - screenYNw) - (screenYNe - screenYNw) * (screenXSe - screenXNw) > 0) {
					rasterizer.clamp = screenXNe < 0 || screenXNw < 0 || screenXSe < 0 || screenXNe > rasterizer.width || screenXNw > rasterizer.width || screenXSe > rasterizer.width;
					if (javaSimpleTile.texture >= 0) {
						rasterizer.renderTexturedTriangleRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, depthNe, depthNw, depthSe, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, ColorUtil.hsvToRgbTable[javaSimpleTile.hslNe & 0xFFFF] & 0xFFFFFF | 0xFF000000, ColorUtil.hsvToRgbTable[javaSimpleTile.hslNw & 0xFFFF] & 0xFFFFFF | 0xFF000000, ColorUtil.hsvToRgbTable[javaSimpleTile.hslSe & 0xFFFF] & 0xFFFFFF | 0xFF000000, 0, 0, 0, 0, javaSimpleTile.texture);
					} else {
						rasterizer.renderTriangleHslRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, (float) depthNe, (float) depthNw, (float) depthSe, (float) (javaSimpleTile.hslNe & 0xFFFF), (float) (javaSimpleTile.hslNw & 0xFFFF), (float) (javaSimpleTile.hslSe & 0xFFFF));
					}
				}
				if ((screenXSw - screenXSe) * (screenYNw - screenYSe) - (screenYSw - screenYSe) * (screenXNw - screenXSe) > 0) {
					rasterizer.clamp = screenXSw < 0 || screenXSe < 0 || screenXNw < 0 || screenXSw > rasterizer.width || screenXSe > rasterizer.width || screenXNw > rasterizer.width;
					if (javaSimpleTile.texture >= 0) {
						rasterizer.renderTexturedTriangleRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, depthSw, depthSe, depthNw, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, ColorUtil.hsvToRgbTable[javaSimpleTile.hslSw & 0xFFFF] & 0xFFFFFF | 0xFF000000, ColorUtil.hsvToRgbTable[javaSimpleTile.hslSe & 0xFFFF] & 0xFFFFFF | 0xFF000000, ColorUtil.hsvToRgbTable[javaSimpleTile.hslNw & 0xFFFF] & 0xFFFFFF | 0xFF000000, 0, 0, 0, 0, javaSimpleTile.texture);
						return;
					}
					rasterizer.renderTriangleHslRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, (float) depthSw, (float) depthSe, (float) depthNw, (float) (javaSimpleTile.hslSw & 0xFFFF), (float) (javaSimpleTile.hslSe & 0xFFFF), (float) (javaSimpleTile.hslNw & 0xFFFF));
					return;
				}
			} else {
				if ((screenXNe - screenXNw) * (screenYSe - screenYNw) - (screenYNe - screenYNw) * (screenXSe - screenXNw) > 0) {
					rasterizer.clamp = screenXNe < 0 || screenXNw < 0 || screenXSe < 0 || screenXNe > rasterizer.width || screenXNw > rasterizer.width || screenXSe > rasterizer.width;
					if (javaSimpleTile.texture >= 0) {
						rasterizer.renderTexturedTriangleRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, depthNe, depthNw, depthSe, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, ColorUtil.hsvToRgbTable[javaSimpleTile.hslNe & 0xFFFF] & 0xFFFFFF | 0xFF000000, ColorUtil.hsvToRgbTable[javaSimpleTile.hslNw & 0xFFFF] & 0xFFFFFF | 0xFF000000, ColorUtil.hsvToRgbTable[javaSimpleTile.hslSe & 0xFFFF] & 0xFFFFFF | 0xFF000000, 0, 0, 0, 0, javaSimpleTile.texture);
					} else {
						rasterizer.renderTriangleHslRgb((float) screenYNe, (float) screenYNw, (float) screenYSe, (float) screenXNe, (float) screenXNw, (float) screenXSe, (float) depthNe, (float) depthNw, (float) depthSe, (float) (javaSimpleTile.hslNe & 0xFFFF), (float) (javaSimpleTile.hslNw & 0xFFFF), (float) (javaSimpleTile.hslSe & 0xFFFF));
					}
				}
				if ((screenXSw - screenXSe) * (screenYNw - screenYSe) - (screenYSw - screenYSe) * (screenXNw - screenXSe) > 0) {
					rasterizer.clamp = screenXSw < 0 || screenXSe < 0 || screenXNw < 0 || screenXSw > rasterizer.width || screenXSe > rasterizer.width || screenXNw > rasterizer.width;
					if (javaSimpleTile.texture >= 0) {
						rasterizer.renderTexturedTriangleRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, depthSw, depthSe, depthNw, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, ColorUtil.hsvToRgbTable[javaSimpleTile.hslSw & 0xFFFF] & 0xFFFFFF | 0xFF000000, ColorUtil.hsvToRgbTable[javaSimpleTile.hslSe & 0xFFFF] & 0xFFFFFF | 0xFF000000, ColorUtil.hsvToRgbTable[javaSimpleTile.hslNw & 0xFFFF] & 0xFFFFFF | 0xFF000000, 0, 0, 0, 0, javaSimpleTile.texture);
						return;
					}
					rasterizer.renderTriangleHslRgb((float) screenYSw, (float) screenYSe, (float) screenYNw, (float) screenXSw, (float) screenXSe, (float) screenXNw, (float) depthSw, (float) depthSe, (float) depthNw, (float) (javaSimpleTile.hslSw & 0xFFFF), (float) (javaSimpleTile.hslSe & 0xFFFF), (float) (javaSimpleTile.hslNw & 0xFFFF));
				}
			}
		}
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(Lclient!r;IIIIZ)Z")
	@Override
	public boolean method8002(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int heightOffset, @OriginalArg(3) int z) {
		return false;
	}

	@OriginalMember(owner = "client!cr", name = "fa", descriptor = "(IILclient!r;)Lclient!r;")
	@Override
	public Shadow fa(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) Shadow shadow) {
		return null;
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(IIIIIII[[Z)V")
	@Override
	public void drawMinimap(@OriginalArg(3) int x1, @OriginalArg(4) int y1, @OriginalArg(5) int x2, @OriginalArg(6) int y2, @OriginalArg(7) boolean[][] visibility) {
		@Pc(4) JavaThreadResource javaThreadResource = this.toolkit.threadResource(Thread.currentThread());
		@Pc(7) Rasterizer rasterizer = javaThreadResource.rasterizer;
		rasterizer.alpha = 0;
		rasterizer.clamp = true;
		this.toolkit.ya();
		if (this.simpleBlendedTiles != null || this.genericBlendedTiles != null) {
			this.drawBlendedMinimapTiles(x1, y1, x2, y2, visibility, javaThreadResource, rasterizer, javaThreadResource.screenX, javaThreadResource.screenY);
		} else if (this.simpleTiles != null) {
			this.drawUnblendedMinimapTiles(x1, y1, x2, y2, visibility, javaThreadResource, rasterizer, javaThreadResource.screenX, javaThreadResource.screenY);
		}
	}

	@OriginalMember(owner = "client!cr", name = "b", descriptor = "(IIZLclient!pga;Lclient!mp;[I[I[I[II)V")
	private void renderGenericBlendedTile(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) boolean water, @OriginalArg(3) JavaThreadResource resource, @OriginalArg(4) Rasterizer rasterizer, @OriginalArg(5) int[] screenX, @OriginalArg(6) int[] screenY, @OriginalArg(7) int[] depths, @OriginalArg(8) int[] fogLevels, @OriginalArg(9) int skipFlags) {
		@Pc(6) JavaGenericBlendedTile javaGenericBlendedTile = this.genericBlendedTiles[x][z];
		if (skipFlags != 0 && (skipFlags & 0x2) != 0 || javaGenericBlendedTile == null) {
			return;
		}
		@Pc(32) int vertexX;
		@Pc(37) int vertexY;
		@Pc(47) int vertexZ;
		@Pc(162) float viewX;
		@Pc(183) float viewY;
		@Pc(68) float viewZ;
		@Pc(21) int vertex;
		@Pc(89) int local89;
		@Pc(109) int local109;
		if (this.depthOverride == -1) {
			for (vertex = 0; vertex < javaGenericBlendedTile.vertexCount; vertex++) {
				vertexX = javaGenericBlendedTile.verticesX[vertex] + (x << super.tileScale);
				vertexY = javaGenericBlendedTile.verticesY[vertex];
				vertexZ = javaGenericBlendedTile.verticesZ[vertex] + (z << super.tileScale);
				viewZ = this.cameraTz + this.cameraE3_1 * (float) vertexX + this.cameraE3_2 * (float) vertexY + this.cameraE3_3 * (float) vertexZ;
				if (viewZ <= (float) this.toolkit.zNear) {
					return;
				}
				fogLevels[vertex] = 0;
				if (water) {
					int fogLevel = (int) (viewZ - (float) resource.fogPlane);
					if (fogLevel > 255) {
						fogLevel = 255;
					}
					if (fogLevel > 0) {
						fogLevels[vertex] = fogLevel;
						int waterDepth = javaGenericBlendedTile.waterDepths[vertex] * fogLevel / 255;
						if (waterDepth > 0) {
							vertexY -= waterDepth;
						}
					}
				} else if (resource.fogActive) {
					int fogLevel = (int) (viewZ - (float) resource.fogPlane);
					if (fogLevel > 0) {
						fogLevels[vertex] = fogLevel;
						if (fogLevels[vertex] > 255) {
							fogLevels[vertex] = 255;
						}
					}
				}
				viewX = this.cameraTx + this.cameraE1_1 * (float) vertexX + this.cameraE1_2 * (float) vertexY + this.cameraE1_3 * (float) vertexZ;
				viewY = this.cameraTy + this.cameraE2_1 * (float) vertexX + this.cameraE2_2 * (float) vertexY + this.cameraE2_3 * (float) vertexZ;
				screenX[vertex] = rasterizer.minX + (int) (viewX * (float) this.toolkit.projectionScaleX / viewZ);
				screenY[vertex] = rasterizer.minY + (int) (viewY * (float) this.toolkit.projectionScaleY / viewZ);
				depths[vertex] = (int) viewZ;
			}
		} else {
			for (vertex = 0; vertex < javaGenericBlendedTile.vertexCount; vertex++) {
				vertexX = javaGenericBlendedTile.verticesX[vertex] + (x << super.tileScale);
				vertexY = javaGenericBlendedTile.verticesY[vertex];
				vertexZ = javaGenericBlendedTile.verticesZ[vertex] + (z << super.tileScale);
				viewZ = this.cameraTz + this.cameraE3_1 * (float) vertexX + this.cameraE3_2 * (float) vertexY + this.cameraE3_3 * (float) vertexZ;
				fogLevels[vertex] = 0;
				if (water) {
					int fogLevel = this.depthOverride - resource.fogPlane;
					if (fogLevel > 255) {
						fogLevel = 255;
					}
					if (fogLevel > 0) {
						fogLevels[vertex] = fogLevel;
						int waterDepth = javaGenericBlendedTile.waterDepths[vertex] * fogLevel / 255;
						if (waterDepth > 0) {
							vertexY -= waterDepth;
						}
					}
				} else if (resource.fogActive) {
					int fogLevel = this.depthOverride - resource.fogPlane;
					if (fogLevel > 0) {
						fogLevels[vertex] = fogLevel;
						if (fogLevels[vertex] > 255) {
							fogLevels[vertex] = 255;
						}
					}
				}
				viewX = this.cameraTx + this.cameraE1_1 * (float) vertexX + this.cameraE1_2 * (float) vertexY + this.cameraE1_3 * (float) vertexZ;
				viewY = this.cameraTy + this.cameraE2_1 * (float) vertexX + this.cameraE2_2 * (float) vertexY + this.cameraE2_3 * (float) vertexZ;
				screenX[vertex] = rasterizer.minX + (int) (viewX * (float) this.toolkit.projectionScaleX / (float) this.depthOverride);
				screenY[vertex] = rasterizer.minY + (int) (viewY * (float) this.toolkit.projectionScaleY / (float) this.depthOverride);
				depths[vertex] = (int) viewZ;
			}
		}
		@Pc(426) float tileUnits = (float) super.tileUnits;
		for (int faceIndex = 0; faceIndex < javaGenericBlendedTile.faceCount; faceIndex++) {
			int a = faceIndex * 3;
			@Pc(437) int b = a + 1;
			@Pc(441) int c = b + 1;
			@Pc(445) int xA = screenX[a];
			@Pc(449) int xB = screenX[b];
			@Pc(453) int xC = screenX[c];
			@Pc(457) int yA = screenY[a];
			@Pc(461) int yB = screenY[b];
			@Pc(465) int yC = screenY[c];
			if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
				rasterizer.clamp = xA < 0 || xB < 0 || xC < 0 || xA > rasterizer.width || xB > rasterizer.width || xC > rasterizer.width;
				if (fogLevels[a] + fogLevels[b] + fogLevels[c] < 765) {
					@Pc(522) int originX = x << super.tileScale;
					@Pc(527) int originZ = z << super.tileScale;
					if ((javaGenericBlendedTile.vertexColours[a] & 0xFFFFFF) != 0) {
						if (javaGenericBlendedTile.vertexTextures[a] == javaGenericBlendedTile.vertexTextures[b] && javaGenericBlendedTile.vertexTextures[a] == javaGenericBlendedTile.vertexTextures[c] && javaGenericBlendedTile.vertexSizes[a] == javaGenericBlendedTile.vertexSizes[b] && javaGenericBlendedTile.vertexSizes[a] == javaGenericBlendedTile.vertexSizes[c]) {
							rasterizer.renderTexturedTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[a], (float) depths[b], (float) depths[c], (float) (originX + javaGenericBlendedTile.verticesX[a]) / (float) javaGenericBlendedTile.vertexSizes[a], (float) (originX + javaGenericBlendedTile.verticesX[b]) / (float) javaGenericBlendedTile.vertexSizes[b], (float) (originX + javaGenericBlendedTile.verticesX[c]) / (float) javaGenericBlendedTile.vertexSizes[c], (float) (originZ + javaGenericBlendedTile.verticesZ[a]) / (float) javaGenericBlendedTile.vertexSizes[a], (float) (originZ + javaGenericBlendedTile.verticesZ[b]) / (float) javaGenericBlendedTile.vertexSizes[b], (float) (originZ + javaGenericBlendedTile.verticesZ[c]) / (float) javaGenericBlendedTile.vertexSizes[c], javaGenericBlendedTile.vertexColours[a], javaGenericBlendedTile.vertexColours[b], javaGenericBlendedTile.vertexColours[c], resource.fogColour, fogLevels[a], fogLevels[b], fogLevels[c], javaGenericBlendedTile.vertexTextures[a]);
						} else {
							rasterizer.renderBlendedTexturedTriangle((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[a], (float) depths[b], (float) depths[c], (float) (originX + javaGenericBlendedTile.verticesX[a]) / tileUnits, (float) (originX + javaGenericBlendedTile.verticesX[b]) / tileUnits, (float) (originX + javaGenericBlendedTile.verticesX[c]) / tileUnits, (float) (originZ + javaGenericBlendedTile.verticesZ[a]) / tileUnits, (float) (originZ + javaGenericBlendedTile.verticesZ[b]) / tileUnits, (float) (originZ + javaGenericBlendedTile.verticesZ[c]) / tileUnits, javaGenericBlendedTile.vertexColours[a], javaGenericBlendedTile.vertexColours[b], javaGenericBlendedTile.vertexColours[c], resource.fogColour, fogLevels[a], fogLevels[b], fogLevels[c], javaGenericBlendedTile.vertexTextures[a], tileUnits / (float) javaGenericBlendedTile.vertexSizes[a], javaGenericBlendedTile.vertexTextures[b], tileUnits / (float) javaGenericBlendedTile.vertexSizes[b], javaGenericBlendedTile.vertexTextures[c], tileUnits / (float) javaGenericBlendedTile.vertexSizes[c]);
						}
					}
				} else {
					rasterizer.renderFlatTriangleRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, (float) depths[a], (float) depths[b], (float) depths[c], resource.fogColour);
				}
			}
		}
	}

	@OriginalMember(owner = "client!cr", name = "a", descriptor = "(II)V")
	@Override
	public void renderTile(@OriginalArg(0) int x, @OriginalArg(1) int z) {
		this.renderTile(x, z, 0);
	}

	@OriginalMember(owner = "client!cr", name = "b", descriptor = "(IIIIIII[[ZLclient!pga;Lclient!mp;[I[I)V")
	private void drawUnblendedMinimapTiles(@OriginalArg(3) int x1, @OriginalArg(4) int z1, @OriginalArg(5) int x2, @OriginalArg(6) int z2, @OriginalArg(7) boolean[][] visibility, @OriginalArg(8) JavaThreadResource resource, @OriginalArg(9) Rasterizer rasterizer, @OriginalArg(10) int[] screenX, @OriginalArg(11) int[] screenY) {
		@Pc(7) int mapHeight = (z2 - z1) * 1024 / 256;
		@Pc(14) boolean zWrite = resource.zWrite;
		this.toolkit.C(false);
		rasterizer.fastScanline = false;
		rasterizer.halfBlend = false;
		@Pc(26) int drawX = 0;
		@Pc(30) int drawY = mapHeight;
		for (@Pc(32) int i = x1; i < x2; i++) {
			for (@Pc(35) int j = z1; j < z2; j++) {
				if (visibility[i - x1][j - z1]) {
					@Pc(79) int local79;
					if (this.simpleTiles[i][j] != null) {
						@Pc(60) PlainTile javaSimpleTile = this.simpleTiles[i][j];
						if (javaSimpleTile.texture != -1 && (javaSimpleTile.functionMask & 0x2) == 0 && javaSimpleTile.color == -1) {
							int textureColour = this.toolkit.textureHsl(javaSimpleTile.texture);
							rasterizer.renderTriangleHslRgb((float) (drawY - 4), (float) (drawY - 4), (float) drawY, (float) (drawX + 4), (float) drawX, (float) (drawX + 4), 100.0F, 100.0F, 100.0F, (float) JavaModel.applyLightness(javaSimpleTile.hslNe & 0xFFFF, textureColour), (float) JavaModel.applyLightness(javaSimpleTile.hslNw & 0xFFFF, textureColour), (float) JavaModel.applyLightness(javaSimpleTile.hslSe & 0xFFFF, textureColour));
							rasterizer.renderTriangleHslRgb((float) drawY, (float) drawY, (float) (drawY - 4), (float) drawX, (float) (drawX + 4), (float) drawX, 100.0F, 100.0F, 100.0F, (float) JavaModel.applyLightness(javaSimpleTile.hslSw & 0xFFFF, textureColour), (float) JavaModel.applyLightness(javaSimpleTile.hslSe & 0xFFFF, textureColour), (float) JavaModel.applyLightness(javaSimpleTile.hslNw & 0xFFFF, textureColour));
						} else if (javaSimpleTile.color == -1) {
							rasterizer.renderTriangleHslRgb((float) (drawY - 4), (float) (drawY - 4), (float) drawY, (float) (drawX + 4), (float) drawX, (float) (drawX + 4), 100.0F, 100.0F, 100.0F, (float) (javaSimpleTile.hslNe & 0xFFFF), (float) (javaSimpleTile.hslNw & 0xFFFF), (float) (javaSimpleTile.hslSe & 0xFFFF));
							rasterizer.renderTriangleHslRgb((float) drawY, (float) drawY, (float) (drawY - 4), (float) drawX, (float) (drawX + 4), (float) drawX, 100.0F, 100.0F, 100.0F, (float) (javaSimpleTile.hslSw & 0xFFFF), (float) (javaSimpleTile.hslSe & 0xFFFF), (float) (javaSimpleTile.hslNw & 0xFFFF));
						} else {
							int colour = javaSimpleTile.color;
							rasterizer.renderTriangleHslRgb((float) (drawY - 4), (float) (drawY - 4), (float) drawY, (float) (drawX + 4), (float) drawX, (float) (drawX + 4), 100.0F, 100.0F, 100.0F, (float) colour, (float) colour, (float) colour);
							rasterizer.renderTriangleHslRgb((float) drawY, (float) drawY, (float) (drawY - 4), (float) drawX, (float) (drawX + 4), (float) drawX, 100.0F, 100.0F, 100.0F, (float) colour, (float) colour, (float) colour);
						}
					} else if (this.complexTiles[i][j] != null) {
						@Pc(331) JavaComplexTile javaComplexTile = this.complexTiles[i][j];
						for (int vertexIndex = 0; vertexIndex < javaComplexTile.vertexCount; vertexIndex++) {
							screenX[vertexIndex] = drawX + javaComplexTile.verticesX[vertexIndex] * 4 / super.tileUnits;
							screenY[vertexIndex] = drawY - javaComplexTile.verticesZ[vertexIndex] * 4 / super.tileUnits;
						}
						for (@Pc(369) int k = 0; k < javaComplexTile.faceCount; k++) {
							@Pc(375) short a = javaComplexTile.faceA[k];
							@Pc(380) short b = javaComplexTile.faceB[k];
							@Pc(385) short c = javaComplexTile.faceC[k];
							@Pc(389) int xA = screenX[a];
							@Pc(393) int xB = screenX[b];
							@Pc(397) int xC = screenX[c];
							@Pc(401) int yA = screenY[a];
							@Pc(405) int yB = screenY[b];
							@Pc(409) int yC = screenY[c];
							@Pc(423) int colour;
							if (javaComplexTile.faceBlendedColours != null && javaComplexTile.faceBlendedColours[k] != -1) {
								colour = javaComplexTile.faceBlendedColours[k];
								rasterizer.renderTriangleHslRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, 100.0F, 100.0F, 100.0F, (float) JavaModel.applyLightness(javaComplexTile.verticesLight[a], colour), (float) JavaModel.applyLightness(javaComplexTile.verticesLight[b], colour), (float) JavaModel.applyLightness(javaComplexTile.verticesLight[c], colour));
							} else if (javaComplexTile.faceTextures == null || javaComplexTile.faceTextures[k] == -1) {
								colour = javaComplexTile.faceColours[k];
								rasterizer.renderTriangleHslRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, 100.0F, 100.0F, 100.0F, (float) JavaModel.applyLightness(javaComplexTile.verticesLight[a], colour), (float) JavaModel.applyLightness(javaComplexTile.verticesLight[b], colour), (float) JavaModel.applyLightness(javaComplexTile.verticesLight[c], colour));
							} else {
								colour = this.toolkit.textureHsl(javaComplexTile.faceTextures[k]);
								rasterizer.renderTriangleHslRgb((float) yA, (float) yB, (float) yC, (float) xA, (float) xB, (float) xC, 100.0F, 100.0F, 100.0F, (float) JavaModel.applyLightness(javaComplexTile.verticesLight[a], colour), (float) JavaModel.applyLightness(javaComplexTile.verticesLight[b], colour), (float) JavaModel.applyLightness(javaComplexTile.verticesLight[c], colour));
							}
						}
					}
				}
				drawY -= 4;
			}
			drawY = mapHeight;
			drawX += 4;
		}
		rasterizer.fastScanline = true;
		this.toolkit.C(zWrite);
	}

	@OriginalMember(owner = "client!cr", name = "YA", descriptor = "()V")
	@Override
	public void YA() {
		this.lightLevels = null;
		this.shadowLevels = null;
	}

	@OriginalMember(owner = "client!cr", name = "CA", descriptor = "(Lclient!r;IIIIZ)V")
	@Override
	public void CA(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int heightOffset, @OriginalArg(3) int z, @OriginalArg(4) int arg4, @OriginalArg(5) boolean arg5) {
	}

	@OriginalMember(owner = "client!cr", name = "b", descriptor = "(I)Z")
	private boolean isWaterEffect(@OriginalArg(0) int effectType) {
		if ((this.featureFlags & 0x8) == 0) {
			return false;
		} else if (effectType == 4) {
			return true;
		} else if (effectType == 8) {
			return true;
		} else {
			return effectType == 9;
		}
	}
}
