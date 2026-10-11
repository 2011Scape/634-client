package com.jagex.game.runetek5.graphics.model;

import com.jagex.game.runetek5.graphics.texture.TextureTransformationMatrix;
import com.jagex.game.runetek5.GpuVertexNormal;
import com.jagex.game.runetek5.GpuFaceNormal;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.GpuModelIndexStream;
import com.jagex.game.runetek5.GpuModelVertexStream;
import com.jagex.game.runetek5.cache.node.lru.GpuShadow;
import com.jagex.game.runetek5.graphics.model.billboard.BillboardType;
import com.jagex.game.runetek5.NativeMatrix;
import com.jagex.game.runetek5.NativeBillboardAttributes;
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.normal.MeshBillboard;
import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import jaclib.memory.Buffer;
import jaclib.memory.Stream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.entity.BoundingBoxPolygon;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.graphics.GpuModelNormals;
import com.jagex.game.runetek5.graphics.GpuIndexBuffer;
import com.jagex.game.runetek5.graphics.NativeBillboardFace;
import com.jagex.game.runetek5.toolkit.companion.NativeStream;
import com.jagex.game.runetek5.utils.ColorUtil;
import com.jagex.game.runetek5.PrimitiveType;
import com.jagex.game.runetek5.utils.ArrayUtils;
import com.jagex.game.runetek5.graphics.model.billboard.BillboardTypeList;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!nca")
public final class GpuModel extends Model {

	@OriginalMember(owner = "client!nca", name = "o", descriptor = "I")
	private int size3d;

	@OriginalMember(owner = "client!nca", name = "p", descriptor = "Z")
	private boolean updateRequired;

	@OriginalMember(owner = "client!nca", name = "F", descriptor = "I")
	private int minZ;

	@OriginalMember(owner = "client!nca", name = "G", descriptor = "Lclient!wga;")
	private final AbstractNativeToolkit nativeToolkit;

	@OriginalMember(owner = "client!nca", name = "L", descriptor = "Lclient!cd;")
	private GpuModelVertexStream normalStream;

	@OriginalMember(owner = "client!nca", name = "eb", descriptor = "I")
	private int maxZ;

	@OriginalMember(owner = "client!nca", name = "vb", descriptor = "Lclient!cd;")
	private GpuModelVertexStream texCoordStream;

	@OriginalMember(owner = "client!nca", name = "wb", descriptor = "I")
	private int minY;

	@OriginalMember(owner = "client!nca", name = "xb", descriptor = "Z")
	private boolean dynamic;

	@OriginalMember(owner = "client!nca", name = "yb", descriptor = "I")
	private int functionMask;

	@OriginalMember(owner = "client!nca", name = "Cb", descriptor = "I")
	private int maxX;

	@OriginalMember(owner = "client!nca", name = "Hb", descriptor = "I")
	private int preferenceFlags;

	@OriginalMember(owner = "client!nca", name = "Kb", descriptor = "I")
	private int numNormals;

	@OriginalMember(owner = "client!nca", name = "Mb", descriptor = "Z")
	private boolean boundsCalculated;

	@OriginalMember(owner = "client!nca", name = "Rb", descriptor = "Lclient!cd;")
	private GpuModelVertexStream colourStream;

	@OriginalMember(owner = "client!nca", name = "Ub", descriptor = "Lclient!qq;")
	private GpuModelIndexStream indexStream;

	@OriginalMember(owner = "client!nca", name = "mc", descriptor = "Lclient!ok;")
	private GpuModelNormals mergedNormals;

	@OriginalMember(owner = "client!nca", name = "qc", descriptor = "I")
	private int minX;

	@OriginalMember(owner = "client!nca", name = "sc", descriptor = "I")
	private int maxY;

	@OriginalMember(owner = "client!nca", name = "tc", descriptor = "I")
	private int size2d;

	@OriginalMember(owner = "client!nca", name = "Dc", descriptor = "Lclient!cd;")
	private GpuModelVertexStream positionStream;

	@OriginalMember(owner = "client!nca", name = "qb", descriptor = "[I")
	private int[] triangles;

	@OriginalMember(owner = "client!nca", name = "Nb", descriptor = "I")
	private int numFaces;

	@OriginalMember(owner = "client!nca", name = "zc", descriptor = "I")
	private int renderedFaceCount;

	@OriginalMember(owner = "client!nca", name = "P", descriptor = "Z")
	private boolean hasAlphaTransition;

	@OriginalMember(owner = "client!nca", name = "lc", descriptor = "Z")
	private boolean textureAnimating;

	@OriginalMember(owner = "client!nca", name = "gc", descriptor = "I")
	private int numVertices;

	@OriginalMember(owner = "client!nca", name = "Q", descriptor = "[I")
	private int[] verticesZ;

	@OriginalMember(owner = "client!nca", name = "Cc", descriptor = "[I")
	private int[] verticesY;

	@OriginalMember(owner = "client!nca", name = "dc", descriptor = "[S")
	private short[] verticesSubmeshes;

	@OriginalMember(owner = "client!nca", name = "U", descriptor = "I")
	private int maxDepth;

	@OriginalMember(owner = "client!nca", name = "uc", descriptor = "[I")
	private int[] verticesX;

	@OriginalMember(owner = "client!nca", name = "Jb", descriptor = "[Lclient!eca;")
	private EffectiveVertex[] effectiveVertices;

	@OriginalMember(owner = "client!nca", name = "sb", descriptor = "[Lclient!nn;")
	private EmissiveTriangle[] emissiveTriangles;

	@OriginalMember(owner = "client!nca", name = "t", descriptor = "I")
	private int billboardCount;

	@OriginalMember(owner = "client!nca", name = "Hc", descriptor = "[Lclient!en;")
	private NativeBillboardFace[] billboardFaces;

	@OriginalMember(owner = "client!nca", name = "db", descriptor = "[Lclient!dk;")
	private NativeBillboardAttributes[] billboardAttributes;

	@OriginalMember(owner = "client!nca", name = "ab", descriptor = "[S")
	private short[] vertexNormalIndices;

	@OriginalMember(owner = "client!nca", name = "rc", descriptor = "[S")
	private short[] facesA;

	@OriginalMember(owner = "client!nca", name = "K", descriptor = "[S")
	private short[] facesMaterial;

	@OriginalMember(owner = "client!nca", name = "pc", descriptor = "[F")
	private float[] textureV;

	@OriginalMember(owner = "client!nca", name = "X", descriptor = "[S")
	private short[] normalsZ;

	@OriginalMember(owner = "client!nca", name = "Vb", descriptor = "[S")
	private short[] facesColor;

	@OriginalMember(owner = "client!nca", name = "ic", descriptor = "[B")
	private byte[] magnitudes;

	@OriginalMember(owner = "client!nca", name = "vc", descriptor = "[S")
	private short[] facesC;

	@OriginalMember(owner = "client!nca", name = "cc", descriptor = "[S")
	private short[] normalsX;

	@OriginalMember(owner = "client!nca", name = "T", descriptor = "[S")
	private short[] facesSubmeshes;

	@OriginalMember(owner = "client!nca", name = "Fb", descriptor = "[S")
	private short[] normalVertices;

	@OriginalMember(owner = "client!nca", name = "D", descriptor = "[S")
	private short[] normalFaces;

	@OriginalMember(owner = "client!nca", name = "ec", descriptor = "[S")
	private short[] facesB;

	@OriginalMember(owner = "client!nca", name = "nb", descriptor = "S")
	private short ambient;

	@OriginalMember(owner = "client!nca", name = "yc", descriptor = "[B")
	private byte[] facesAlpha;

	@OriginalMember(owner = "client!nca", name = "Lb", descriptor = "[F")
	private float[] textureU;

	@OriginalMember(owner = "client!nca", name = "O", descriptor = "S")
	private short contrast;

	@OriginalMember(owner = "client!nca", name = "Z", descriptor = "[S")
	private short[] normalsY;

	@OriginalMember(owner = "client!nca", name = "C", descriptor = "[I")
	private int[] materialBatchMinIndex;

	@OriginalMember(owner = "client!nca", name = "r", descriptor = "[I")
	private int[] materialBatchOffsets;

	@OriginalMember(owner = "client!nca", name = "Ib", descriptor = "[I")
	private int[] materialBatchVertexCount;

	@OriginalMember(owner = "client!nca", name = "A", descriptor = "[[I")
	private int[][] vertexGroups;

	@OriginalMember(owner = "client!nca", name = "z", descriptor = "[[I")
	private int[][] billboardGroups;

	@OriginalMember(owner = "client!nca", name = "bc", descriptor = "[[I")
	private int[][] faceGroups;

	@OriginalMember(owner = "client!nca", name = "<init>", descriptor = "(Lclient!wga;Lclient!bh;IIII)V")
	public GpuModel(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) Mesh mesh, @OriginalArg(2) int functionMask, @OriginalArg(3) int ambient, @OriginalArg(4) int contrast, @OriginalArg(5) int preferenceFlags) {
		this(nativeToolkit, functionMask, preferenceFlags, true, false);
		@Pc(11) MaterialProvider textureSource = nativeToolkit.materialProvider;
		@Pc(15) int[] faces = new int[mesh.numFaces];
		this.triangles = new int[mesh.maxDepth + 1];
		for (@Pc(24) int i = 0; i < mesh.numFaces; i++) {
			if (mesh.faceRenderType == null || mesh.faceRenderType[i] != 2) {
				if (mesh.facesMaterial != null && mesh.facesMaterial[i] != -1) {
					@Pc(56) MaterialType textureMetrics = textureSource.getMetrics(mesh.facesMaterial[i] & 0xFFFF);
					if (((this.preferenceFlags & 0x40) == 0 || !textureMetrics.detailsOnly) && textureMetrics.skipTriangles) {
						continue;
					}
				}
				faces[this.numFaces++] = i;
				this.triangles[mesh.facesA[i]]++;
				this.triangles[mesh.facesB[i]]++;
				this.triangles[mesh.facesC[i]]++;
			}
		}
		this.renderedFaceCount = this.numFaces;
		@Pc(125) long[] sortKeys = new long[this.numFaces];
		@Pc(137) boolean modifiesAlpha = (this.functionMask & 0x100) != 0;
		@Pc(149) int local149;
		@Pc(162) int local162;
		@Pc(322) int local322;
		for (@Pc(139) int j = 0; j < this.numFaces; j++) {
			@Pc(145) int face = faces[j];
			@Pc(147) MaterialType textureMetrics2 = null;
			int sortKeyHigh = 0;
			@Pc(151) byte renderFlags = 0;
			@Pc(153) byte effectType = 0;
			@Pc(155) byte effectParam1 = 0;
			if (mesh.billboards != null) {
				@Pc(160) boolean hasUid = false;
				for (int billboardIndex = 0; billboardIndex < mesh.billboards.length; billboardIndex++) {
					@Pc(169) MeshBillboard meshBillboard = mesh.billboards[billboardIndex];
					if (meshBillboard.appliesOnFace == face) {
						@Pc(178) BillboardType lightType = BillboardTypeList.list(meshBillboard.billboardId);
						if (lightType.hasUid) {
							hasUid = true;
						}
						if (lightType.materialId != -1) {
							@Pc(194) MaterialType textureMetrics3 = textureSource.getMetrics(lightType.materialId);
							if (textureMetrics3.effectCombiner == 2) {
								this.hasAlphaTransition = true;
							}
						}
					}
				}
				if (hasUid) {
					sortKeys[j] = Long.MAX_VALUE;
					this.renderedFaceCount--;
					continue;
				}
			}
			@Pc(223) short materialId = -1;
			if (mesh.facesMaterial != null) {
				materialId = mesh.facesMaterial[face];
				if (materialId != -1) {
					textureMetrics2 = textureSource.getMetrics(materialId & 0xFFFF);
					if ((this.preferenceFlags & 0x40) != 0 && textureMetrics2.detailsOnly) {
						materialId = -1;
						textureMetrics2 = null;
					} else {
						effectParam1 = textureMetrics2.effectParam1;
						effectType = textureMetrics2.effectType;
					}
				}
			}
			@Pc(284) boolean alphaTransition = mesh.facesAlpha != null && mesh.facesAlpha[face] != 0 || textureMetrics2 != null && textureMetrics2.effectCombiner != 0;
			if ((modifiesAlpha || alphaTransition) && mesh.facesLayer != null) {
				sortKeyHigh += mesh.facesLayer[face] << 17;
			}
			if (alphaTransition) {
				sortKeyHigh += 65536;
			}
			sortKeyHigh += (effectType & 0xFF) << 8;
			int materialKey = renderFlags + ((materialId & 0xFFFF) << 16);
			sortKeyHigh += effectParam1 & 0xFF;
			@Pc(334) int sortKeyLow = materialKey + (j & 0xFFFF);
			sortKeys[j] = ((long) sortKeyHigh << 32) + (long) sortKeyLow;
			this.textureAnimating |= textureMetrics2 != null && (textureMetrics2.speedU != 0 || textureMetrics2.speedV != 0);
			this.hasAlphaTransition |= alphaTransition;
		}
		ArrayUtils.quicksort(faces, sortKeys);
		this.numVertices = mesh.numVertices;
		this.verticesZ = mesh.verticesZ;
		this.verticesY = mesh.verticesY;
		this.verticesSubmeshes = mesh.verticesSubmeshes;
		this.maxDepth = mesh.maxDepth;
		this.verticesX = mesh.verticesX;
		this.effectiveVertices = mesh.particleEffectors;
		@Pc(414) GpuVertexNormal[] vertexNormals = new GpuVertexNormal[this.maxDepth];
		this.emissiveTriangles = mesh.particleEmitters;
		@Pc(438) int local438;
		@Pc(452) int local452;
		@Pc(491) int local491;
		if (mesh.billboards != null) {
			this.billboardCount = mesh.billboards.length;
			this.billboardFaces = new NativeBillboardFace[this.billboardCount];
			this.billboardAttributes = new NativeBillboardAttributes[this.billboardCount];
			for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
				@Pc(445) MeshBillboard meshBillboard2 = mesh.billboards[billboardIndex];
				@Pc(450) BillboardType lightType2 = BillboardTypeList.list(meshBillboard2.billboardId);
				int sortedFaceIndex = -1;
				for (@Pc(454) int k = 0; k < this.numFaces; k++) {
					if (meshBillboard2.appliesOnFace == faces[k]) {
						sortedFaceIndex = k;
						break;
					}
				}
				if (sortedFaceIndex == -1) {
					throw new RuntimeException();
				}
				int rgb = ColorUtil.hslToRgb[mesh.facesColor[meshBillboard2.appliesOnFace] & 0xFFFF] & 0xFFFFFF;
				@Pc(509) int diffuseColor = rgb | 255 - (mesh.facesAlpha == null ? 0 : mesh.facesAlpha[meshBillboard2.appliesOnFace]) << 24;
				this.billboardFaces[billboardIndex] = new NativeBillboardFace(sortedFaceIndex, mesh.facesA[meshBillboard2.appliesOnFace], mesh.facesB[meshBillboard2.appliesOnFace], mesh.facesC[meshBillboard2.appliesOnFace], lightType2.size2d, lightType2.size3d, lightType2.materialId, lightType2.blendType, lightType2.shape, lightType2.hasUid, lightType2.stationary, meshBillboard2.distance);
				this.billboardAttributes[billboardIndex] = new NativeBillboardAttributes(diffuseColor);
			}
		}
		int maxNormals = this.numFaces * 3;
		this.vertexNormalIndices = new short[maxNormals];
		this.facesA = new short[this.numFaces];
		this.facesMaterial = new short[this.numFaces];
		this.textureV = new float[maxNormals];
		this.normalsZ = new short[maxNormals];
		this.facesColor = new short[this.numFaces];
		this.magnitudes = new byte[maxNormals];
		this.facesC = new short[this.numFaces];
		this.normalsX = new short[maxNormals];
		if (mesh.facesSubmeshes != null) {
			this.facesSubmeshes = new short[this.numFaces];
		}
		this.normalVertices = new short[maxNormals];
		this.normalFaces = new short[maxNormals];
		GpuModel.normalsUid = new long[maxNormals];
		this.facesB = new short[this.numFaces];
		this.ambient = (short) ambient;
		this.facesAlpha = new byte[this.numFaces];
		this.textureU = new float[maxNormals];
		this.contrast = (short) contrast;
		this.normalsY = new short[maxNormals];
		int offset = 0;
		for (int vertexIndex = 0; vertexIndex < mesh.maxDepth; vertexIndex++) {
			int normalCount = this.triangles[vertexIndex];
			this.triangles[vertexIndex] = offset;
			offset += normalCount;
			vertexNormals[vertexIndex] = new GpuVertexNormal();
		}
		this.triangles[mesh.maxDepth] = offset;
		@Pc(703) TextureTransformationMatrix textureMatrix = TextureTransformationMatrix.generateTextureMatrix(this.numFaces, mesh, faces);
		@Pc(707) GpuFaceNormal[] faceNormals = new GpuFaceNormal[mesh.numFaces];
		@Pc(747) int local747;
		@Pc(757) int local757;
		@Pc(768) int local768;
		@Pc(726) short local726;
		@Pc(736) int local736;
		for (int faceIndex = 0; faceIndex < mesh.numFaces; faceIndex++) {
			@Pc(716) short faceA = mesh.facesA[faceIndex];
			@Pc(721) short faceB = mesh.facesB[faceIndex];
			short faceC = mesh.facesC[faceIndex];
			int deltaX1 = this.verticesX[faceB] - this.verticesX[faceA];
			int deltaY1 = this.verticesY[faceB] - this.verticesY[faceA];
			int deltaZ1 = this.verticesZ[faceB] - this.verticesZ[faceA];
			int deltaX2 = this.verticesX[faceC] - this.verticesX[faceA];
			@Pc(779) int deltaY2 = this.verticesY[faceC] - this.verticesY[faceA];
			@Pc(790) int deltaZ2 = this.verticesZ[faceC] - this.verticesZ[faceA];
			@Pc(799) int normalX = deltaY1 * deltaZ2 - deltaY2 * deltaZ1;
			@Pc(808) int normalY = deltaZ1 * deltaX2 - deltaX1 * deltaZ2;
			@Pc(816) int normalZ;
			for (normalZ = deltaX1 * deltaY2 - deltaX2 * deltaY1; normalX > 8192 || normalY > 8192 || normalZ > 8192 || normalX < -8192 || normalY < -8192 || normalZ < -8192; normalZ >>= 0x1) {
				normalY >>= 0x1;
				normalX >>= 0x1;
			}
			@Pc(870) int magnitude = (int) Math.sqrt((double) (normalY * normalY + normalX * normalX + normalZ * normalZ));
			if (magnitude <= 0) {
				magnitude = 1;
			}
			normalZ = normalZ * 256 / magnitude;
			normalY = normalY * 256 / magnitude;
			normalX = normalX * 256 / magnitude;
			@Pc(905) byte renderType = mesh.faceRenderType == null ? 0 : mesh.faceRenderType[faceIndex];
			if (renderType == 0) {
				@Pc(936) GpuVertexNormal vertexNormalA = vertexNormals[faceA];
				vertexNormalA.magnitude++;
				vertexNormalA.y += normalY;
				vertexNormalA.x += normalX;
				vertexNormalA.z += normalZ;
				@Pc(964) GpuVertexNormal vertexNormalB = vertexNormals[faceB];
				vertexNormalB.x += normalX;
				vertexNormalB.magnitude++;
				vertexNormalB.y += normalY;
				vertexNormalB.z += normalZ;
				@Pc(992) GpuVertexNormal vertexNormalC = vertexNormals[faceC];
				vertexNormalC.magnitude++;
				vertexNormalC.x += normalX;
				vertexNormalC.y += normalY;
				vertexNormalC.z += normalZ;
			} else if (renderType == 1) {
				@Pc(921) GpuFaceNormal faceNormal = faceNormals[faceIndex] = new GpuFaceNormal();
				faceNormal.x = normalX;
				faceNormal.y = normalY;
				faceNormal.z = normalZ;
			}
		}
		@Pc(1033) int local1033;
		@Pc(1076) short local1076;
		for (int faceIndex = 0; faceIndex < this.numFaces; faceIndex++) {
			int face = faces[faceIndex];
			@Pc(1040) int color = mesh.facesColor[face] & 0xFFFF;
			int faceTexture;
			if (mesh.facesTexture == null) {
				faceTexture = -1;
			} else {
				faceTexture = mesh.facesTexture[face];
			}
			int faceAlpha;
			if (mesh.facesAlpha == null) {
				faceAlpha = 0;
			} else {
				faceAlpha = mesh.facesAlpha[face] & 0xFF;
			}
			short materialId = mesh.facesMaterial == null ? -1 : mesh.facesMaterial[face];
			if (materialId != -1 && (this.preferenceFlags & 0x40) != 0) {
				@Pc(1095) MaterialType textureMetrics4 = textureSource.getMetrics(materialId & 0xFFFF);
				if (textureMetrics4.detailsOnly) {
					materialId = -1;
				}
			}
			@Pc(1102) float aU = 0.0F;
			@Pc(1104) float aV = 0.0F;
			@Pc(1106) float bU = 0.0F;
			@Pc(1108) float bV = 0.0F;
			@Pc(1110) float cU = 0.0F;
			@Pc(1112) float cV = 0.0F;
			@Pc(1114) byte uidOffsetB = 0;
			@Pc(1116) byte uidOffsetC = 0;
			@Pc(1118) int cubeFaceIndex = 0;
			@Pc(1133) byte local1133;
			@Pc(1153) short local1153;
			@Pc(1952) short local1952;
			@Pc(1957) short local1957;
			if (materialId != -1) {
				if (faceTexture == -1) {
					bU = 1.0F;
					cV = 0.0F;
					uidOffsetB = 1;
					uidOffsetC = 2;
					cU = 0.0F;
					aU = 0.0F;
					bV = 1.0F;
					aV = 1.0F;
				} else {
					faceTexture &= 0xFF;
					byte mappingType = mesh.texturesMapping[faceTexture];
					@Pc(1143) short vertexA;
					@Pc(1148) short vertexB;
					@Pc(1186) float local1186;
					@Pc(1200) float local1200;
					@Pc(1208) float local1208;
					@Pc(1308) float local1308;
					@Pc(1316) float local1316;
					@Pc(1324) float local1324;
					@Pc(1347) float local1347;
					@Pc(1370) float local1370;
					@Pc(1393) float local1393;
					if (mappingType == 0) {
						vertexA = mesh.facesA[face];
						vertexB = mesh.facesB[face];
						short vertexC = mesh.facesC[face];
						short texVertexA = mesh.textureFaceX[faceTexture];
						short texVertexB = mesh.textureFaceY[faceTexture];
						@Pc(1962) short texVertexC = mesh.textureFaceZ[faceTexture];
						@Pc(1968) float texOriginX = (float) mesh.verticesX[texVertexA];
						@Pc(1974) float texOriginY = (float) mesh.verticesY[texVertexA];
						float texOriginZ = mesh.verticesZ[texVertexA];
						float texEdge1X = (float) mesh.verticesX[texVertexB] - texOriginX;
						float texEdge1Y = (float) mesh.verticesY[texVertexB] - texOriginY;
						@Pc(2006) float texEdge1Z = (float) mesh.verticesZ[texVertexB] - texOriginZ;
						@Pc(2015) float texEdge2X = (float) mesh.verticesX[texVertexC] - texOriginX;
						@Pc(2023) float texEdge2Y = (float) mesh.verticesY[texVertexC] - texOriginY;
						@Pc(2032) float texEdge2Z = (float) mesh.verticesZ[texVertexC] - texOriginZ;
						@Pc(2041) float vertexAOffX = (float) mesh.verticesX[vertexA] - texOriginX;
						@Pc(2049) float vertexAOffY = (float) mesh.verticesY[vertexA] - texOriginY;
						@Pc(2058) float vertexAOffZ = (float) mesh.verticesZ[vertexA] - texOriginZ;
						@Pc(2067) float vertexBOffX = (float) mesh.verticesX[vertexB] - texOriginX;
						@Pc(2075) float vertexBOffY = (float) mesh.verticesY[vertexB] - texOriginY;
						float vertexBOffZ = (float) mesh.verticesZ[vertexB] - texOriginZ;
						float vertexCOffX = (float) mesh.verticesX[vertexC] - texOriginX;
						float vertexCOffY = (float) mesh.verticesY[vertexC] - texOriginY;
						float vertexCOffZ = (float) mesh.verticesZ[vertexC] - texOriginZ;
						float texCrossX = texEdge1Y * texEdge2Z - texEdge2Y * texEdge1Z;
						float texCrossY = texEdge2X * texEdge1Z - texEdge1X * texEdge2Z;
						@Pc(2134) float texCrossZ = texEdge2Y * texEdge1X - texEdge2X * texEdge1Y;
						@Pc(2143) float uBasisX = texCrossZ * texEdge2Y - texCrossY * texEdge2Z;
						@Pc(2152) float uBasisY = texCrossX * texEdge2Z - texCrossZ * texEdge2X;
						@Pc(2161) float uBasisZ = texCrossY * texEdge2X - texCrossX * texEdge2Y;
						@Pc(2175) float uInvDeterminant = 1.0F / (uBasisY * texEdge1Y + uBasisX * texEdge1X + texEdge1Z * uBasisZ);
						aU = uInvDeterminant * (uBasisZ * vertexAOffZ + uBasisX * vertexAOffX + uBasisY * vertexAOffY);
						cU = (uBasisZ * vertexCOffZ + uBasisX * vertexCOffX + vertexCOffY * uBasisY) * uInvDeterminant;
						bU = uInvDeterminant * (vertexBOffZ * uBasisZ + uBasisY * vertexBOffY + uBasisX * vertexBOffX);
						@Pc(2225) float vBasisX = texCrossZ * texEdge1Y - texCrossY * texEdge1Z;
						@Pc(2234) float vBasisY = texEdge1Z * texCrossX - texEdge1X * texCrossZ;
						@Pc(2243) float vBasisZ = texCrossY * texEdge1X - texCrossX * texEdge1Y;
						@Pc(2257) float vInvDeterminant = 1.0F / (vBasisX * texEdge2X + texEdge2Y * vBasisY + texEdge2Z * vBasisZ);
						bV = (vertexBOffX * vBasisX + vertexBOffY * vBasisY + vBasisZ * vertexBOffZ) * vInvDeterminant;
						cV = (vBasisX * vertexCOffX + vBasisY * vertexCOffY + vBasisZ * vertexCOffZ) * vInvDeterminant;
						aV = vInvDeterminant * (vertexAOffZ * vBasisZ + vertexAOffY * vBasisY + vertexAOffX * vBasisX);
					} else {
						vertexA = mesh.facesA[face];
						vertexB = mesh.facesB[face];
						short vertexC = mesh.facesC[face];
						@Pc(1158) int centerX = textureMatrix.centerX[faceTexture];
						@Pc(1163) int centerY = textureMatrix.centerY[faceTexture];
						@Pc(1168) int centerZ = textureMatrix.centerZ[faceTexture];
						@Pc(1173) float[] matrix = textureMatrix.matrix[faceTexture];
						@Pc(1178) byte direction = mesh.texturesDirection[faceTexture];
						float speedOffset = (float) mesh.texturesSpeed[faceTexture] / 256.0F;
						if (mappingType == 1) {
							float uScale = (float) mesh.texturesScaleZ[faceTexture] / 1024.0F;
							Model.mapCylindricalUv(centerZ, mesh.verticesZ[vertexA], direction, mesh.verticesX[vertexA], GpuModel.UV_RESULT, mesh.verticesY[vertexA], speedOffset, centerY, centerX, uScale, matrix);
							aV = GpuModel.UV_RESULT[1];
							aU = GpuModel.UV_RESULT[0];
							Model.mapCylindricalUv(centerZ, mesh.verticesZ[vertexB], direction, mesh.verticesX[vertexB], GpuModel.UV_RESULT, mesh.verticesY[vertexB], speedOffset, centerY, centerX, uScale, matrix);
							bV = GpuModel.UV_RESULT[1];
							bU = GpuModel.UV_RESULT[0];
							Model.mapCylindricalUv(centerZ, mesh.verticesZ[vertexC], direction, mesh.verticesX[vertexC], GpuModel.UV_RESULT, mesh.verticesY[vertexC], speedOffset, centerY, centerX, uScale, matrix);
							cU = GpuModel.UV_RESULT[0];
							cV = GpuModel.UV_RESULT[1];
							float halfUScale = uScale / 2.0F;
							if ((direction & 0x1) == 0) {
								if (halfUScale < bU - aU) {
									uidOffsetB = 1;
									bU -= uScale;
								} else if (halfUScale < aU - bU) {
									bU += uScale;
									uidOffsetB = 2;
								}
								if (halfUScale < cU - aU) {
									cU -= uScale;
									uidOffsetC = 1;
								} else if (aU - cU > halfUScale) {
									uidOffsetC = 2;
									cU += uScale;
								}
							} else {
								if (halfUScale < cV - aV) {
									cV -= uScale;
									uidOffsetC = 1;
								} else if (aV - cV > halfUScale) {
									uidOffsetC = 2;
									cV += uScale;
								}
								if (bV - aV > halfUScale) {
									bV -= uScale;
									uidOffsetB = 1;
								} else if (aV - bV > halfUScale) {
									bV += uScale;
									uidOffsetB = 2;
								}
							}
						} else if (mappingType == 2) {
							float uTrans = (float) mesh.texturesUtrans[faceTexture] / 256.0F;
							float vTrans = (float) mesh.texturesVtrans[faceTexture] / 256.0F;
							@Pc(1219) int edge1X = mesh.verticesX[vertexB] - mesh.verticesX[vertexA];
							@Pc(1230) int edge1Y = mesh.verticesY[vertexB] - mesh.verticesY[vertexA];
							@Pc(1241) int edge1Z = mesh.verticesZ[vertexB] - mesh.verticesZ[vertexA];
							@Pc(1252) int edge2X = mesh.verticesX[vertexC] - mesh.verticesX[vertexA];
							@Pc(1262) int edge2Y = mesh.verticesY[vertexC] - mesh.verticesY[vertexA];
							@Pc(1273) int edge2Z = mesh.verticesZ[vertexC] - mesh.verticesZ[vertexA];
							@Pc(1282) int crossX = edge1Y * edge2Z - edge1Z * edge2Y;
							@Pc(1291) int crossY = edge2X * edge1Z - edge1X * edge2Z;
							@Pc(1300) int crossZ = edge2Y * edge1X - edge1Y * edge2X;
							float invScaleX = 64.0F / (float) mesh.texturesScaleX[faceTexture];
							float invScaleY = 64.0F / (float) mesh.texturesScaleY[faceTexture];
							float invScaleZ = 64.0F / (float) mesh.texturesScaleZ[faceTexture];
							float cubeZ = ((float) crossZ * matrix[2] + (float) crossX * matrix[0] + (float) crossY * matrix[1]) / invScaleX;
							float cubeX = (matrix[5] * (float) crossZ + matrix[3] * (float) crossX + (float) crossY * matrix[4]) / invScaleY;
							float cubeY = ((float) crossY * matrix[7] + matrix[6] * (float) crossX + matrix[8] * (float) crossZ) / invScaleZ;
							cubeFaceIndex = Model.getCubeFace(cubeX, cubeY, cubeZ);
							Model.mapCubeUv(vTrans, speedOffset, matrix, mesh.verticesZ[vertexA], centerZ, direction, centerX, mesh.verticesX[vertexA], mesh.verticesY[vertexA], uTrans, GpuModel.UV_RESULT, centerY, cubeFaceIndex);
							aV = GpuModel.UV_RESULT[1];
							aU = GpuModel.UV_RESULT[0];
							Model.mapCubeUv(vTrans, speedOffset, matrix, mesh.verticesZ[vertexB], centerZ, direction, centerX, mesh.verticesX[vertexB], mesh.verticesY[vertexB], uTrans, GpuModel.UV_RESULT, centerY, cubeFaceIndex);
							bV = GpuModel.UV_RESULT[1];
							bU = GpuModel.UV_RESULT[0];
							Model.mapCubeUv(vTrans, speedOffset, matrix, mesh.verticesZ[vertexC], centerZ, direction, centerX, mesh.verticesX[vertexC], mesh.verticesY[vertexC], uTrans, GpuModel.UV_RESULT, centerY, cubeFaceIndex);
							cU = GpuModel.UV_RESULT[0];
							cV = GpuModel.UV_RESULT[1];
						} else if (mappingType == 3) {
							Model.mapSphericalUv(centerZ, direction, speedOffset, mesh.verticesX[vertexA], GpuModel.UV_RESULT, mesh.verticesZ[vertexA], centerX, mesh.verticesY[vertexA], centerY, matrix);
							aU = GpuModel.UV_RESULT[0];
							aV = GpuModel.UV_RESULT[1];
							Model.mapSphericalUv(centerZ, direction, speedOffset, mesh.verticesX[vertexB], GpuModel.UV_RESULT, mesh.verticesZ[vertexB], centerX, mesh.verticesY[vertexB], centerY, matrix);
							bU = GpuModel.UV_RESULT[0];
							bV = GpuModel.UV_RESULT[1];
							Model.mapSphericalUv(centerZ, direction, speedOffset, mesh.verticesX[vertexC], GpuModel.UV_RESULT, mesh.verticesZ[vertexC], centerX, mesh.verticesY[vertexC], centerY, matrix);
							cU = GpuModel.UV_RESULT[0];
							cV = GpuModel.UV_RESULT[1];
							if ((direction & 0x1) == 0) {
								if (cU - aU > 0.5F) {
									cU--;
									uidOffsetC = 1;
								} else if (aU - cU > 0.5F) {
									cU++;
									uidOffsetC = 2;
								}
								if (bU - aU > 0.5F) {
									bU--;
									uidOffsetB = 1;
								} else if (aU - bU > 0.5F) {
									bU++;
									uidOffsetB = 2;
								}
							} else {
								if (cV - aV > 0.5F) {
									uidOffsetC = 1;
									cV--;
								} else if (aV - cV > 0.5F) {
									uidOffsetC = 2;
									cV++;
								}
								if (bV - aV > 0.5F) {
									bV--;
									uidOffsetB = 1;
								} else if (aV - bV > 0.5F) {
									uidOffsetB = 2;
									bV++;
								}
							}
						}
					}
				}
			}
			byte faceRenderType;
			if (mesh.faceRenderType == null) {
				faceRenderType = 0;
			} else {
				faceRenderType = mesh.faceRenderType[face];
			}
			if (faceRenderType == 0) {
				@Pc(2481) long uid = (long) (faceTexture << 2) + ((long) (color << 8) + (long) (cubeFaceIndex << 24) + (long) faceAlpha << 32);
				short vertexA = mesh.facesA[face];
				short vertexB = mesh.facesB[face];
				short vertexC = mesh.facesC[face];
				@Pc(2500) GpuVertexNormal vertexNormalA = vertexNormals[vertexA];
				this.facesA[faceIndex] = this.connectNormal(vertexNormalA.z, uid, faceIndex, vertexNormalA.magnitude, vertexNormalA.x, aU, vertexA, aV, mesh, vertexNormalA.y);
				@Pc(2525) GpuVertexNormal vertexNormalB = vertexNormals[vertexB];
				this.facesB[faceIndex] = this.connectNormal(vertexNormalB.z, (long) uidOffsetB + uid, faceIndex, vertexNormalB.magnitude, vertexNormalB.x, bU, vertexB, bV, mesh, vertexNormalB.y);
				@Pc(2553) GpuVertexNormal vertexNormalC = vertexNormals[vertexC];
				this.facesC[faceIndex] = this.connectNormal(vertexNormalC.z, (long) uidOffsetC + uid, faceIndex, vertexNormalC.magnitude, vertexNormalC.x, cU, vertexC, cV, mesh, vertexNormalC.y);
			} else if (faceRenderType == 1) {
				@Pc(2338) GpuFaceNormal faceNormal = faceNormals[face];
				@Pc(2383) long uid = ((long) faceAlpha + (long) (cubeFaceIndex << 24) + (long) (color << 8) << 32) + (long) ((faceNormal.y + 256 << 12) + (faceNormal.x > 0 ? 1024 : 2048) + (faceTexture << 2) + (faceNormal.z - -256 << 22));
				this.facesA[faceIndex] = this.connectNormal(faceNormal.z, uid, faceIndex, 0, faceNormal.x, aU, mesh.facesA[face], aV, mesh, faceNormal.y);
				this.facesB[faceIndex] = this.connectNormal(faceNormal.z, uid + (long) uidOffsetB, faceIndex, 0, faceNormal.x, bU, mesh.facesB[face], bV, mesh, faceNormal.y);
				this.facesC[faceIndex] = this.connectNormal(faceNormal.z, uid + (long) uidOffsetC, faceIndex, 0, faceNormal.x, cU, mesh.facesC[face], cV, mesh, faceNormal.y);
			}
			if (mesh.facesAlpha != null) {
				this.facesAlpha[faceIndex] = mesh.facesAlpha[face];
			}
			if (mesh.facesSubmeshes != null) {
				this.facesSubmeshes[faceIndex] = mesh.facesSubmeshes[face];
			}
			this.facesColor[faceIndex] = mesh.facesColor[face];
			this.facesMaterial[faceIndex] = materialId;
		}
		if (this.renderedFaceCount > 0) {
			int batchCount = 1;
			short batchMaterial = this.facesMaterial[0];
			for (int faceIndex = 0; faceIndex < this.renderedFaceCount; faceIndex++) {
				@Pc(2640) short material = this.facesMaterial[faceIndex];
				if (batchMaterial != material) {
					batchCount++;
					batchMaterial = material;
				}
			}
			this.materialBatchMinIndex = new int[batchCount];
			this.materialBatchOffsets = new int[batchCount + 1];
			this.materialBatchVertexCount = new int[batchCount];
			this.materialBatchOffsets[0] = 0;
			int minIndex = this.numNormals;
			batchCount = 0;
			short maxIndex = 0;
			batchMaterial = this.facesMaterial[0];
			for (int faceIndex = 0; faceIndex < this.renderedFaceCount; faceIndex++) {
				@Pc(2699) short material = this.facesMaterial[faceIndex];
				if (material != batchMaterial) {
					this.materialBatchMinIndex[batchCount] = minIndex;
					this.materialBatchVertexCount[batchCount] = maxIndex + 1 - minIndex;
					batchCount++;
					this.materialBatchOffsets[batchCount] = faceIndex;
					batchMaterial = material;
					maxIndex = 0;
					minIndex = this.numNormals;
				}
				@Pc(2735) short faceVertex = this.facesA[faceIndex];
				if (maxIndex < faceVertex) {
					maxIndex = faceVertex;
				}
				if (minIndex > faceVertex) {
					minIndex = faceVertex;
				}
				faceVertex = this.facesB[faceIndex];
				if (faceVertex > maxIndex) {
					maxIndex = faceVertex;
				}
				if (faceVertex < minIndex) {
					minIndex = faceVertex;
				}
				faceVertex = this.facesC[faceIndex];
				if (faceVertex > maxIndex) {
					maxIndex = faceVertex;
				}
				if (faceVertex < minIndex) {
					minIndex = faceVertex;
				}
			}
			this.materialBatchMinIndex[batchCount] = minIndex;
			this.materialBatchVertexCount[batchCount] = maxIndex + 1 - minIndex;
			batchCount++;
			this.materialBatchOffsets[batchCount] = this.renderedFaceCount;
		}
		GpuModel.normalsUid = null;
		this.normalVertices = ArrayUtils.copyOf(this.normalVertices, this.numNormals);
		this.normalFaces = ArrayUtils.copyOf(this.normalFaces, this.numNormals);
		this.normalsX = ArrayUtils.copyOf(this.normalsX, this.numNormals);
		this.normalsY = ArrayUtils.copyOf(this.normalsY, this.numNormals);
		this.normalsZ = ArrayUtils.copyOf(this.normalsZ, this.numNormals);
		this.magnitudes = ArrayUtils.copyOf(this.magnitudes, this.numNormals);
		this.textureU = ArrayUtils.copyOf(this.numNormals, this.textureU);
		this.textureV = ArrayUtils.copyOf(this.numNormals, this.textureV);
		if (mesh.vertexGroups != null && GpuModelRetainMasks.altersVertices(this.preferenceFlags, functionMask)) {
			this.vertexGroups = mesh.createVertexGroups(false);
		}
		if (mesh.billboards != null && GpuModelRetainMasks.retainBillboards(this.preferenceFlags, functionMask)) {
			this.billboardGroups = mesh.createBillboardGroups();
		}
		if (mesh.faceGroups != null && GpuModelRetainMasks.altersFaces(this.preferenceFlags, functionMask)) {
			int maxFaceGroup = 0;
			@Pc(2927) int[] groupCounts = new int[256];
			for (int faceIndex = 0; faceIndex < this.numFaces; faceIndex++) {
				int faceGroup = mesh.faceGroups[faces[faceIndex]];
				if (faceGroup >= 0) {
					@Pc(2947) int previousCount = groupCounts[faceGroup]++;
					if (maxFaceGroup < faceGroup) {
						maxFaceGroup = faceGroup;
					}
				}
			}
			this.faceGroups = new int[maxFaceGroup + 1][];
			for (int group = 0; group <= maxFaceGroup; group++) {
				this.faceGroups[group] = new int[groupCounts[group]];
				groupCounts[group] = 0;
			}
			for (int faceIndex = 0; faceIndex < this.numFaces; faceIndex++) {
				int faceGroup = mesh.faceGroups[faces[faceIndex]];
				if (faceGroup >= 0) {
					this.faceGroups[faceGroup][groupCounts[faceGroup]++] = faceIndex;
				}
			}
		}
	}

	@OriginalMember(owner = "client!nca", name = "<init>", descriptor = "(Lclient!wga;IIZZ)V")
	public GpuModel(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) int functionMask, @OriginalArg(2) int preferenceFlags, @OriginalArg(3) boolean createAllStreams, @OriginalArg(4) boolean dynamic) {
		this.updateRequired = true;
		this.maxDepth = 0;
		this.hasAlphaTransition = false;
		this.boundsCalculated = false;
		this.numFaces = 0;
		this.numNormals = 0;
		this.renderedFaceCount = 0;
		this.textureAnimating = false;
		this.numVertices = 0;
		this.dynamic = false;
		this.functionMask = functionMask;
		this.dynamic = dynamic;
		this.preferenceFlags = preferenceFlags;
		this.nativeToolkit = nativeToolkit;
		if (createAllStreams || GpuModelRetainMasks.hasVertices(this.functionMask, this.preferenceFlags)) {
			this.positionStream = new GpuModelVertexStream(GpuModelRetainMasks.altersAnyVertices(this.functionMask, this.preferenceFlags));
		}
		if (createAllStreams || GpuModelRetainMasks.hasTextureCoordinates(this.preferenceFlags, this.functionMask)) {
			this.texCoordStream = new GpuModelVertexStream(GpuModelRetainMasks.requiresTexCoordBufferRebuild(this.preferenceFlags, this.functionMask));
		}
		if (createAllStreams || GpuModelRetainMasks.hasColors(this.functionMask, this.preferenceFlags)) {
			this.colourStream = new GpuModelVertexStream(GpuModelRetainMasks.requiresColorBufferRebuild(this.functionMask, this.preferenceFlags));
		}
		if (createAllStreams || GpuModelRetainMasks.hasNormals(this.preferenceFlags, this.functionMask)) {
			this.normalStream = new GpuModelVertexStream(GpuModelRetainMasks.requiresNormalsBufferRebuild(this.functionMask, this.preferenceFlags));
		}
		if (createAllStreams || GpuModelRetainMasks.hasElementsBuffer(this.functionMask, this.preferenceFlags)) {
			this.indexStream = new GpuModelIndexStream(GpuModelRetainMasks.copyElementsBuffer(this.preferenceFlags, this.functionMask));
		}
	}

	@OriginalMember(owner = "client!nca", name = "b", descriptor = "(I)V")
	private void renderBillboards() {
		if (this.billboardFaces == null) {
			return;
		}
		this.nativeToolkit.C(!this.hasAlphaTransition);
		this.nativeToolkit.toggleLights(false);
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.CONSTANT_COLOR);
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.CONSTANT_COLOR);
		for (@Pc(32) int i = 0; i < this.billboardCount; i++) {
			@Pc(39) NativeBillboardFace nativeBillboardFace = this.billboardFaces[i];
			@Pc(44) NativeBillboardAttributes nativeBillboardAttributes = this.billboardAttributes[i];
			if (!nativeBillboardFace.hideWithBloom || !this.nativeToolkit.bloom()) {
				@Pc(73) float centerX = (float) (this.verticesX[nativeBillboardFace.vertexC] + this.verticesX[nativeBillboardFace.vertexB] + this.verticesX[nativeBillboardFace.vertexA]) * 0.3333333F;
				@Pc(94) float centerY = (float) (this.verticesY[nativeBillboardFace.vertexB] + this.verticesY[nativeBillboardFace.vertexC] + this.verticesY[nativeBillboardFace.vertexA]) * 0.3333333F;
				@Pc(115) float centerZ = (float) (this.verticesZ[nativeBillboardFace.vertexA] + this.verticesZ[nativeBillboardFace.vertexB] + this.verticesZ[nativeBillboardFace.vertexC]) * 0.3333333F;
				@Pc(129) float viewX = centerZ * GpuModel.modelViewXC + GpuModel.modelViewXA * centerX + centerY * GpuModel.modelViewXB + GpuModel.modelViewXD;
				@Pc(143) float viewY = GpuModel.modelViewYD + GpuModel.modelViewYC * centerZ + GpuModel.modelViewYA * centerX + centerY * GpuModel.modelViewYB;
				@Pc(157) float viewZ = GpuModel.modelViewZD + GpuModel.modelViewZB * centerY + centerX * GpuModel.modelViewZA + centerZ * GpuModel.modelViewZC;
				@Pc(178) float scale = (float) (1.0D / Math.sqrt((double) (viewZ * viewZ + viewY * viewY + viewX * viewX))) * (float) nativeBillboardFace.distance;
				@Pc(183) NativeMatrix modelMatrix = this.nativeToolkit.getModelMatrix();
				modelMatrix.setTextureCoordTransform(nativeBillboardAttributes.angle, nativeBillboardFace.width * nativeBillboardAttributes.scaleY >> 7, (float) nativeBillboardAttributes.offsetX + viewX - viewX * scale, -(scale * viewZ) + viewZ, (float) nativeBillboardAttributes.offsetY + viewY - viewY * scale, nativeBillboardAttributes.scaleX * nativeBillboardFace.height >> 7);
				modelMatrix.multiply(this.nativeToolkit.inverseSunMatrix43);
				this.nativeToolkit.commitModelMatrix();
				@Pc(240) int colour = nativeBillboardAttributes.colour;
				this.nativeToolkit.setActiveMaterial(false, false, nativeBillboardFace.texture);
				this.nativeToolkit.toggleBlending(nativeBillboardFace.blendMode);
				this.nativeToolkit.setColourValue(colour);
				this.nativeToolkit.drawQuadTriangleFan();
			}
		}
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.PREVIOUS);
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.PREVIOUS);
		this.nativeToolkit.C(true);
	}

	@OriginalMember(owner = "client!nca", name = "c", descriptor = "(I)Z")
	private boolean uploadStreams() {
		@Pc(13) boolean uploadColours = !this.colourStream.uploaded;
		@Pc(29) boolean uploadNormals = (this.preferenceFlags & 0x37) != 0 && !this.normalStream.uploaded;
		@Pc(37) boolean uploadPositions = !this.positionStream.uploaded;
		@Pc(45) boolean uploadTexCoords = !this.texCoordStream.uploaded;
		if (!uploadPositions && !uploadColours && !uploadNormals && !uploadTexCoords) {
			return true;
		}
		@Pc(57) boolean success = true;
		@Pc(76) NativeStream vertexBuffer;
		@Pc(90) Buffer buffer;
		if (uploadPositions) {
			if (this.positionStream.buffer == null) {
				this.positionStream.buffer = this.nativeToolkit.createStream(this.dynamic);
			}
			vertexBuffer = this.positionStream.buffer;
			vertexBuffer.create(this.numNormals * 12, 12);
			buffer = vertexBuffer.lock();
			if (buffer == null) {
				success = false;
			} else {
				this.nativeToolkit.nativeInterface.copyPositions(this.verticesX, this.verticesY, this.verticesZ, this.normalVertices, 0, 12, this.numNormals, buffer.getAddress());
				if (vertexBuffer.unlock()) {
					this.positionStream.activeBuffer = vertexBuffer;
					this.positionStream.uploaded = true;
				} else {
					success = false;
				}
			}
		}
		@Pc(186) short[] srcNormalsX;
		@Pc(189) short[] srcNormalsY;
		@Pc(183) short[] srcNormalsZ;
		@Pc(180) byte[] srcMagnitudes;
		if (uploadColours) {
			if (this.colourStream.buffer == null) {
				this.colourStream.buffer = this.nativeToolkit.createStream(this.dynamic);
			}
			vertexBuffer = this.colourStream.buffer;
			vertexBuffer.create(this.numNormals * 4, 4);
			buffer = vertexBuffer.lock();
			if (buffer == null) {
				success = false;
			} else {
				if ((this.preferenceFlags & 0x37) == 0) {
					if (this.mergedNormals == null) {
						srcMagnitudes = this.magnitudes;
						srcNormalsZ = this.normalsZ;
						srcNormalsX = this.normalsX;
						srcNormalsY = this.normalsY;
					} else {
						srcNormalsZ = this.mergedNormals.normalsZ;
						srcNormalsY = this.mergedNormals.normalsY;
						srcMagnitudes = this.mergedNormals.magnitudes;
						srcNormalsX = this.mergedNormals.normalsX;
					}
					this.nativeToolkit.nativeInterface.copyLighting(this.facesColor, this.facesAlpha, this.facesMaterial, srcNormalsX, srcNormalsY, srcNormalsZ, srcMagnitudes, this.ambient, this.contrast, this.normalFaces, 0, 4, this.numNormals, buffer.getAddress());
				} else {
					this.nativeToolkit.nativeInterface.copyColours(this.facesColor, this.facesAlpha, this.facesMaterial, this.ambient, this.normalFaces, 0, 4, this.numNormals, buffer.getAddress());
				}
				if (vertexBuffer.unlock()) {
					this.colourStream.uploaded = true;
					this.colourStream.activeBuffer = vertexBuffer;
				} else {
					success = false;
				}
			}
		}
		if (uploadNormals) {
			if (this.normalStream.buffer == null) {
				this.normalStream.buffer = this.nativeToolkit.createStream(this.dynamic);
			}
			vertexBuffer = this.normalStream.buffer;
			vertexBuffer.create(this.numNormals * 12, 12);
			buffer = vertexBuffer.lock();
			if (buffer == null) {
				success = false;
			} else {
				if (this.mergedNormals == null) {
					srcNormalsZ = this.normalsZ;
					srcNormalsX = this.normalsX;
					srcMagnitudes = this.magnitudes;
					srcNormalsY = this.normalsY;
				} else {
					srcMagnitudes = this.mergedNormals.magnitudes;
					srcNormalsZ = this.mergedNormals.normalsZ;
					srcNormalsX = this.mergedNormals.normalsX;
					srcNormalsY = this.mergedNormals.normalsY;
				}
				this.nativeToolkit.nativeInterface.copyNormals(srcNormalsX, srcNormalsY, srcNormalsZ, srcMagnitudes, 3.0F / (float) this.contrast, 3.0F / (float) (this.contrast + this.contrast / 2), 0, 12, this.numNormals, buffer.getAddress());
				if (vertexBuffer.unlock()) {
					this.normalStream.uploaded = true;
					this.normalStream.activeBuffer = vertexBuffer;
				} else {
					success = false;
				}
			}
		}
		if (uploadTexCoords) {
			if (this.texCoordStream.buffer == null) {
				this.texCoordStream.buffer = this.nativeToolkit.createStream(this.dynamic);
			}
			vertexBuffer = this.texCoordStream.buffer;
			vertexBuffer.create(this.numNormals * 8, 8);
			buffer = vertexBuffer.lock();
			if (buffer == null) {
				success = false;
			} else {
				this.nativeToolkit.nativeInterface.copyTexCoords(this.textureU, this.textureV, 0, 8, this.numNormals, buffer.getAddress());
				if (vertexBuffer.unlock()) {
					this.texCoordStream.activeBuffer = vertexBuffer;
					this.texCoordStream.uploaded = true;
				} else {
					success = false;
				}
			}
		}
		return success;
	}

	@OriginalMember(owner = "client!nca", name = "na", descriptor = "()I")
	@Override
	public int na() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.size2d;
	}

	@OriginalMember(owner = "client!nca", name = "NA", descriptor = "()Z")
	@Override
	public boolean NA() {
		if (this.vertexGroups == null) {
			return false;
		}
		for (@Pc(12) int i = 0; i < this.numVertices; i++) {
			this.verticesX[i] <<= 0x4;
			this.verticesY[i] <<= 0x4;
			this.verticesZ[i] <<= 0x4;
		}
		GpuModel.originX = 0;
		GpuModel.originZ = 0;
		GpuModel.originY = 0;
		return true;
	}

	@OriginalMember(owner = "client!nca", name = "f", descriptor = "()V")
	@Override
	public void lock() {
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "()[Lclient!eca;")
	@Override
	public EffectiveVertex[] getEffectiveVertices() {
		return this.effectiveVertices;
	}

	@OriginalMember(owner = "client!nca", name = "LA", descriptor = "(I)V")
	@Override
	public void LA(@OriginalArg(0) int contrast) {
		this.contrast = (short) contrast;
		this.invalidatePositions();
		this.invalidateLighting();
	}

	@OriginalMember(owner = "client!nca", name = "VA", descriptor = "(I)V")
	@Override
	public void VA(@OriginalArg(0) int angle) {
		@Pc(9) int sin = TextureCombineSource.SINE[angle];
		@Pc(13) int cos = TextureCombineSource.COSINE[angle];
		for (@Pc(15) int i = 0; i < this.maxDepth; i++) {
			@Pc(33) int rotatedX = this.verticesX[i] * cos + sin * this.verticesY[i] >> 14;
			this.verticesY[i] = cos * this.verticesY[i] - sin * this.verticesX[i] >> 14;
			this.verticesX[i] = rotatedX;
		}
		this.invalidatePositions();
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(IILclient!bca;ZI)Z")
	@Override
	public boolean picked(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) Matrix matrix, @OriginalArg(3) boolean quick, @OriginalArg(4) int sizeShift) {
		return this.pick(x, sizeShift, -1, y, quick, matrix);
	}

	@OriginalMember(owner = "client!nca", name = "ba", descriptor = "(Lclient!r;)Lclient!r;")
	@Override
	public Shadow ba(@OriginalArg(0) Shadow shadow) {
		if (this.numNormals == 0) {
			return null;
		}
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		@Pc(43) int shadowMinX;
		@Pc(59) int shadowMaxX;
		if (this.nativeToolkit.shadowOffsetX <= 0) {
			shadowMinX = this.minX - (this.minY * this.nativeToolkit.shadowOffsetX >> 8) >> this.nativeToolkit.shadowSizeShift;
			shadowMaxX = this.maxX - (this.nativeToolkit.shadowOffsetX * this.maxY >> 8) >> this.nativeToolkit.shadowSizeShift;
		} else {
			shadowMinX = this.minX - (this.maxY * this.nativeToolkit.shadowOffsetX >> 8) >> this.nativeToolkit.shadowSizeShift;
			shadowMaxX = this.maxX - (this.minY * this.nativeToolkit.shadowOffsetX >> 8) >> this.nativeToolkit.shadowSizeShift;
		}
		@Pc(114) int shadowMinZ;
		@Pc(131) int shadowMaxZ;
		if (this.nativeToolkit.shadowOffsetZ > 0) {
			shadowMinZ = this.minZ - (this.maxY * this.nativeToolkit.shadowOffsetZ >> 8) >> this.nativeToolkit.shadowSizeShift;
			shadowMaxZ = this.maxZ - (this.minY * this.nativeToolkit.shadowOffsetZ >> 8) >> this.nativeToolkit.shadowSizeShift;
		} else {
			shadowMinZ = this.minZ - (this.minY * this.nativeToolkit.shadowOffsetZ >> 8) >> this.nativeToolkit.shadowSizeShift;
			shadowMaxZ = this.maxZ - (this.maxY * this.nativeToolkit.shadowOffsetZ >> 8) >> this.nativeToolkit.shadowSizeShift;
		}
		@Pc(172) int width = shadowMaxX + 1 - shadowMinX;
		@Pc(179) int height = shadowMaxZ + 1 - shadowMinZ;
		@Pc(182) GpuShadow nativeShadow = (GpuShadow) shadow;
		@Pc(198) GpuShadow result;
		if (nativeShadow != null && nativeShadow.fitsDimension(width, height)) {
			result = nativeShadow;
			nativeShadow.reset();
		} else {
			result = new GpuShadow(this.nativeToolkit, width, height);
		}
		result.setDimension(shadowMaxX, shadowMaxZ, shadowMinZ, shadowMinX);
		this.projectShadow(result);
		return result;
	}

	@OriginalMember(owner = "client!nca", name = "wa", descriptor = "()V")
	@Override
	public void wa() {
		for (@Pc(7) int i = 0; i < this.numVertices; i++) {
			this.verticesX[i] = this.verticesX[i] + 7 >> 4;
			this.verticesY[i] = this.verticesY[i] + 7 >> 4;
			this.verticesZ[i] = this.verticesZ[i] + 7 >> 4;
		}
		this.invalidatePositions();
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!nca", name = "EA", descriptor = "()I")
	@Override
	public int EA() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.maxY;
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(IIIIIZLclient!bca;)Z")
	private boolean pick(@OriginalArg(0) int x, @OriginalArg(1) int sizeShift, @OriginalArg(2) int zoom, @OriginalArg(4) int y, @OriginalArg(5) boolean quick, @OriginalArg(6) Matrix matrix) {
		@Pc(8) NativeMatrix modelMatrix = (NativeMatrix) matrix;
		@Pc(12) NativeMatrix viewMatrix43 = this.nativeToolkit.viewMatrix43;
		@Pc(33) float translateX = viewMatrix43.xD + viewMatrix43.xA * modelMatrix.xD + viewMatrix43.xB * modelMatrix.yD + viewMatrix43.xC * modelMatrix.zD;
		@Pc(54) float translateY = viewMatrix43.yC * modelMatrix.zD + modelMatrix.yD * viewMatrix43.yB + modelMatrix.xD * viewMatrix43.yA + viewMatrix43.yD;
		GpuModel.modelViewXC = viewMatrix43.xC * modelMatrix.zC + modelMatrix.xC * viewMatrix43.xA + modelMatrix.yC * viewMatrix43.xB;
		GpuModel.modelViewZA = modelMatrix.zA * viewMatrix43.zC + viewMatrix43.zB * modelMatrix.yA + modelMatrix.xA * viewMatrix43.zA;
		GpuModel.modelViewYB = modelMatrix.yB * viewMatrix43.yB + viewMatrix43.yA * modelMatrix.xB + viewMatrix43.yC * modelMatrix.zB;
		GpuModel.modelViewZC = modelMatrix.xC * viewMatrix43.zA + viewMatrix43.zB * modelMatrix.yC + modelMatrix.zC * viewMatrix43.zC;
		GpuModel.modelViewXB = modelMatrix.zB * viewMatrix43.xC + viewMatrix43.xA * modelMatrix.xB + viewMatrix43.xB * modelMatrix.yB;
		GpuModel.modelViewYA = viewMatrix43.yC * modelMatrix.zA + modelMatrix.xA * viewMatrix43.yA + modelMatrix.yA * viewMatrix43.yB;
		GpuModel.modelViewXA = viewMatrix43.xC * modelMatrix.zA + viewMatrix43.xB * modelMatrix.yA + modelMatrix.xA * viewMatrix43.xA;
		@Pc(201) float translateZ = viewMatrix43.zD + viewMatrix43.zC * modelMatrix.zD + viewMatrix43.zA * modelMatrix.xD + modelMatrix.yD * viewMatrix43.zB;
		GpuModel.modelViewYC = viewMatrix43.yB * modelMatrix.yC + modelMatrix.xC * viewMatrix43.yA + modelMatrix.zC * viewMatrix43.yC;
		GpuModel.modelViewZB = viewMatrix43.zB * modelMatrix.yB + modelMatrix.xB * viewMatrix43.zA + modelMatrix.zB * viewMatrix43.zC;
		@Pc(239) boolean anyProjected = false;
		@Pc(241) float minScreenX = Float.MAX_VALUE;
		@Pc(243) float maxScreenX = -3.4028235E38F;
		@Pc(245) float minScreenY = Float.MAX_VALUE;
		@Pc(247) float maxScreenY = -3.4028235E38F;
		@Pc(251) int scaleX = this.nativeToolkit.projectionScaleX;
		@Pc(255) int scaleY = this.nativeToolkit.projectionScaleY;
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		@Pc(270) int extentX = this.maxX - this.minX >> 1;
		@Pc(279) int extentY = this.maxY - this.minY >> 1;
		@Pc(296) int extentZ = this.maxZ - this.minZ >> 1;
		@Pc(301) int centerX = extentX + this.minX;
		@Pc(306) int centerY = extentY + this.minY;
		@Pc(311) int centerZ = extentZ + this.minZ;
		@Pc(317) int boxMinX = centerX - (extentX << sizeShift);
		@Pc(323) int boxMinY = centerY - (extentY << sizeShift);
		@Pc(330) int boxMinZ = centerZ - (extentZ << sizeShift);
		@Pc(336) int boxMaxX = (extentX << sizeShift) + centerX;
		@Pc(342) int boxMaxY = centerY + (extentY << sizeShift);
		GpuModel.BOX_CORNERS_X[0] = boxMinX;
		@Pc(352) int boxMaxZ = (extentZ << sizeShift) + centerZ;
		GpuModel.BOX_CORNERS_Y[0] = boxMinY;
		GpuModel.BOX_CORNERS_X[1] = boxMaxX;
		GpuModel.BOX_CORNERS_Z[0] = boxMinZ;
		GpuModel.BOX_CORNERS_Y[1] = boxMinY;
		GpuModel.BOX_CORNERS_Z[1] = boxMinZ;
		GpuModel.BOX_CORNERS_X[2] = boxMinX;
		GpuModel.BOX_CORNERS_Y[2] = boxMaxY;
		GpuModel.BOX_CORNERS_Z[2] = boxMinZ;
		GpuModel.BOX_CORNERS_X[3] = boxMaxX;
		GpuModel.BOX_CORNERS_Y[3] = boxMaxY;
		GpuModel.BOX_CORNERS_Z[3] = boxMinZ;
		GpuModel.BOX_CORNERS_X[4] = boxMinX;
		GpuModel.BOX_CORNERS_Y[4] = boxMinY;
		GpuModel.BOX_CORNERS_Z[4] = boxMaxZ;
		GpuModel.BOX_CORNERS_X[5] = boxMaxX;
		GpuModel.BOX_CORNERS_Y[5] = boxMinY;
		GpuModel.BOX_CORNERS_Z[5] = boxMaxZ;
		GpuModel.BOX_CORNERS_X[6] = boxMinX;
		GpuModel.BOX_CORNERS_Y[6] = boxMaxY;
		GpuModel.BOX_CORNERS_X[7] = boxMaxX;
		GpuModel.BOX_CORNERS_Z[6] = boxMaxZ;
		GpuModel.BOX_CORNERS_Y[7] = boxMaxY;
		GpuModel.BOX_CORNERS_Z[7] = boxMaxZ;
		@Pc(491) float viewX;
		@Pc(477) float viewY;
		@Pc(505) float viewZ;
		@Pc(453) float pointX;
		@Pc(463) float pointY;
		@Pc(458) float pointZ;
		for (@Pc(446) int i = 0; i < 8; i++) {
			pointX = GpuModel.BOX_CORNERS_X[i];
			pointZ = GpuModel.BOX_CORNERS_Z[i];
			pointY = GpuModel.BOX_CORNERS_Y[i];
			viewY = pointX * GpuModel.modelViewYA + pointY * GpuModel.modelViewYB + GpuModel.modelViewYC * pointZ + translateY;
			viewX = GpuModel.modelViewXA * pointX + pointY * GpuModel.modelViewXB + pointZ * GpuModel.modelViewXC + translateX;
			viewZ = translateZ + pointZ * GpuModel.modelViewZC + GpuModel.modelViewZB * pointY + GpuModel.modelViewZA * pointX;
			if (viewZ >= (float) this.nativeToolkit.camNear) {
				if (zoom > 0) {
					viewZ = zoom;
				}
				@Pc(532) float screenX = viewX * (float) scaleX / viewZ + (float) this.nativeToolkit.projectionCenterX;
				@Pc(544) float screenY = (float) this.nativeToolkit.projectionCenterY + (float) scaleY * viewY / viewZ;
				if (screenX < minScreenX) {
					minScreenX = screenX;
				}
				if (screenX > maxScreenX) {
					maxScreenX = screenX;
				}
				if (screenY < minScreenY) {
					minScreenY = screenY;
				}
				anyProjected = true;
				if (maxScreenY < screenY) {
					maxScreenY = screenY;
				}
			}
		}
		if (anyProjected && minScreenX < (float) x && maxScreenX > (float) x && minScreenY < (float) y && maxScreenY > (float) y) {
			if (quick) {
				return true;
			}
			if (GpuModel.projectedVerticesX.length < this.numNormals) {
				GpuModel.projectedVerticesY = new int[this.numNormals];
				GpuModel.projectedVerticesX = new int[this.numNormals];
			}
			@Pc(708) int local708;
			for (@Pc(621) int j = 0; j < this.maxDepth; j++) {
				pointZ = this.verticesZ[j];
				pointY = this.verticesY[j];
				pointX = this.verticesX[j];
				viewZ = GpuModel.modelViewZC * pointZ + pointX * GpuModel.modelViewZA + GpuModel.modelViewZB * pointY + translateZ;
				viewX = translateX + GpuModel.modelViewXB * pointY + GpuModel.modelViewXA * pointX + pointZ * GpuModel.modelViewXC;
				viewY = GpuModel.modelViewYC * pointZ + pointX * GpuModel.modelViewYA + GpuModel.modelViewYB * pointY + translateY;
				@Pc(721) int local721;
				@Pc(726) int local726;
				@Pc(733) int local733;
				if (viewZ >= (float) this.nativeToolkit.camNear) {
					if (zoom > 0) {
						viewZ = zoom;
					}
					int projectedX = (int) ((float) scaleX * viewX / viewZ + (float) this.nativeToolkit.projectionCenterX);
					int projectedY = (int) ((float) this.nativeToolkit.projectionCenterY + viewY * (float) scaleY / viewZ);
					int normalStart = this.triangles[j];
					int normalEnd = this.triangles[j + 1];
					for (@Pc(735) int k = normalStart; k < normalEnd; k++) {
						@Pc(744) int normalIndex = this.vertexNormalIndices[k] - 1;
						if (normalIndex == -1) {
							break;
						}
						GpuModel.projectedVerticesX[normalIndex] = projectedX;
						GpuModel.projectedVerticesY[normalIndex] = projectedY;
					}
				} else {
					int normalStart = this.triangles[j];
					int normalEnd = this.triangles[j + 1];
					for (int normalSlot = normalStart; normalSlot < normalEnd; normalSlot++) {
						int normalIndex = this.vertexNormalIndices[normalSlot] - 1;
						if (normalIndex == -1) {
							break;
						}
						GpuModel.projectedVerticesX[this.vertexNormalIndices[normalSlot] - 1] = -999999;
					}
				}
			}
			for (int faceIndex = 0; faceIndex < this.numFaces; faceIndex++) {
				if (GpuModel.projectedVerticesX[this.facesA[faceIndex]] != -999999 && GpuModel.projectedVerticesX[this.facesB[faceIndex]] != -999999 && GpuModel.projectedVerticesX[this.facesC[faceIndex]] != -999999 && this.isHovering(x, GpuModel.projectedVerticesX[this.facesC[faceIndex]], GpuModel.projectedVerticesX[this.facesB[faceIndex]], GpuModel.projectedVerticesY[this.facesC[faceIndex]], GpuModel.projectedVerticesY[this.facesA[faceIndex]], y, GpuModel.projectedVerticesX[this.facesA[faceIndex]], GpuModel.projectedVerticesY[this.facesB[faceIndex]])) {
					return true;
				}
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!nca", name = "d", descriptor = "(I)V")
	private void releaseUploadedData() {
		if (!this.updateRequired) {
			return;
		}
		this.updateRequired = false;
		if (this.emissiveTriangles == null && this.effectiveVertices == null && this.billboardFaces == null) {
			if (this.verticesX != null && !GpuModelRetainMasks.retainVerticesX(this.preferenceFlags, this.functionMask)) {
				if (this.positionStream == null || this.positionStream.canReleaseSourceData()) {
					if (!this.boundsCalculated) {
						this.calculateBounds();
					}
					this.verticesX = null;
				} else {
					this.updateRequired = true;
				}
			}
			if (this.verticesY != null && !GpuModelRetainMasks.retainVerticesY(this.preferenceFlags, this.functionMask)) {
				if (this.positionStream == null || this.positionStream.canReleaseSourceData()) {
					if (!this.boundsCalculated) {
						this.calculateBounds();
					}
					this.verticesY = null;
				} else {
					this.updateRequired = true;
				}
			}
			if (this.verticesZ != null && !GpuModelRetainMasks.retainVerticesZ(this.preferenceFlags, this.functionMask)) {
				if (this.positionStream == null || this.positionStream.canReleaseSourceData()) {
					if (!this.boundsCalculated) {
						this.calculateBounds();
					}
					this.verticesZ = null;
				} else {
					this.updateRequired = true;
				}
			}
		}
		if (this.vertexNormalIndices != null && this.verticesX == null && this.verticesY == null && this.verticesZ == null) {
			this.triangles = null;
			this.vertexNormalIndices = null;
		}
		if (this.magnitudes != null && !GpuModelRetainMasks.retainNormals(this.preferenceFlags, this.functionMask)) {
			label197: {
				label196: {
					@Pc(161) boolean canRelease;
					if ((this.preferenceFlags & 0x37) == 0) {
						if (this.colourStream == null || this.colourStream.canReleaseSourceData()) {
							break label196;
						}
						canRelease = false;
					} else {
						if (this.normalStream == null || this.normalStream.canReleaseSourceData()) {
							break label196;
						}
						canRelease = false;
					}
					if (!canRelease) {
						this.updateRequired = true;
						break label197;
					}
				}
				this.normalsX = this.normalsY = this.normalsZ = null;
				this.magnitudes = null;
			}
		}
		if (this.facesColor != null && !GpuModelRetainMasks.retainFaceColors(this.preferenceFlags, this.functionMask)) {
			if (this.colourStream == null || this.colourStream.canReleaseSourceData()) {
				this.facesColor = null;
			} else {
				this.updateRequired = true;
			}
		}
		if (this.facesAlpha != null && !GpuModelRetainMasks.retainFaceAlphas(this.preferenceFlags, this.functionMask)) {
			if (this.colourStream == null || this.colourStream.canReleaseSourceData()) {
				this.facesAlpha = null;
			} else {
				this.updateRequired = true;
			}
		}
		if (this.textureU != null && !GpuModelRetainMasks.retainTextureCoordinates(this.preferenceFlags, this.functionMask)) {
			if (this.texCoordStream == null || this.texCoordStream.canReleaseSourceData()) {
				this.textureU = this.textureV = null;
			} else {
				this.updateRequired = true;
			}
		}
		if (this.facesMaterial != null && !Model.retainFaceMaterials(this.functionMask, this.preferenceFlags)) {
			if (this.colourStream == null || this.colourStream.canReleaseSourceData()) {
				this.facesMaterial = null;
			} else {
				this.updateRequired = true;
			}
		}
		if (this.facesA != null && !GpuModelRetainMasks.retainFaceIndices(this.functionMask, this.preferenceFlags)) {
			if ((this.indexStream == null || this.indexStream.canReleaseSourceData()) && (this.colourStream == null || this.colourStream.canReleaseSourceData())) {
				this.facesA = this.facesB = this.facesC = null;
			} else {
				this.updateRequired = true;
			}
		}
		if (this.normalVertices != null) {
			if (this.positionStream == null || this.positionStream.canReleaseSourceData()) {
				this.normalVertices = null;
			} else {
				this.updateRequired = true;
			}
		}
		if (this.normalFaces != null) {
			if (this.colourStream == null || this.colourStream.canReleaseSourceData()) {
				this.normalFaces = null;
			} else {
				this.updateRequired = true;
			}
		}
		if (this.faceGroups != null && !GpuModelRetainMasks.altersFaces(this.preferenceFlags, this.functionMask)) {
			this.facesSubmeshes = null;
			this.faceGroups = null;
		}
		if (this.vertexGroups != null && !GpuModelRetainMasks.altersVertices(this.preferenceFlags, this.functionMask)) {
			this.vertexGroups = null;
			this.verticesSubmeshes = null;
		}
		if (this.billboardGroups != null && !GpuModelRetainMasks.retainBillboards(this.preferenceFlags, this.functionMask)) {
			this.billboardGroups = null;
		}
		if (this.materialBatchOffsets != null && (this.functionMask & 0x800) == 0 && (this.functionMask & 0x40000) == 0) {
			this.materialBatchOffsets = null;
			this.materialBatchVertexCount = null;
			this.materialBatchMinIndex = null;
		}
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(I)V")
	@Override
	public void a(@OriginalArg(0) int angle) {
		@Pc(9) int sin = TextureCombineSource.SINE[angle];
		@Pc(13) int cos = TextureCombineSource.COSINE[angle];
		for (@Pc(15) int i = 0; i < this.maxDepth; i++) {
			@Pc(33) int rotatedX = cos * this.verticesX[i] + this.verticesZ[i] * sin >> 14;
			this.verticesZ[i] = cos * this.verticesZ[i] - sin * this.verticesX[i] >> 14;
			this.verticesX[i] = rotatedX;
		}
		this.invalidatePositions();
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!nca", name = "FA", descriptor = "(I)V")
	@Override
	public void FA(@OriginalArg(0) int angle) {
		@Pc(9) int sin = TextureCombineSource.SINE[angle];
		@Pc(13) int cos = TextureCombineSource.COSINE[angle];
		for (@Pc(15) int i = 0; i < this.maxDepth; i++) {
			@Pc(34) int rotatedY = this.verticesY[i] * cos - this.verticesZ[i] * sin >> 14;
			this.verticesZ[i] = this.verticesZ[i] * cos + this.verticesY[i] * sin >> 14;
			this.verticesY[i] = rotatedY;
		}
		this.invalidatePositions();
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!nca", name = "ua", descriptor = "()I")
	@Override
	public int ua() {
		return this.functionMask;
	}

	@OriginalMember(owner = "client!nca", name = "G", descriptor = "()I")
	@Override
	public int G() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.maxZ;
	}

	@OriginalMember(owner = "client!nca", name = "e", descriptor = "(I)V")
	public void releaseStreams() {
		if (this.positionStream != null) {
			this.positionStream.release();
		}
		if (this.texCoordStream != null) {
			this.texCoordStream.release();
		}
		if (this.colourStream != null) {
			this.colourStream.release();
		}
		if (this.normalStream != null) {
			this.normalStream.release();
		}
		if (this.indexStream != null) {
			this.indexStream.release();
		}
	}

	@OriginalMember(owner = "client!nca", name = "d", descriptor = "()[Lclient!nn;")
	@Override
	public EmissiveTriangle[] getEmissiveTriangles() {
		return this.emissiveTriangles;
	}

	@OriginalMember(owner = "client!nca", name = "P", descriptor = "(IIII)V")
	@Override
	public void P(@OriginalArg(0) int type, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int z) {
		@Pc(9) int local9;
		@Pc(17) int local17;
		if (type == 0) {
			int count = 0;
			GpuModel.originX = 0;
			GpuModel.originZ = 0;
			GpuModel.originY = 0;
			for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
				GpuModel.originX += this.verticesX[vertexIndex];
				GpuModel.originY += this.verticesY[vertexIndex];
				GpuModel.originZ += this.verticesZ[vertexIndex];
				count++;
			}
			if (count > 0) {
				GpuModel.originZ = z + GpuModel.originZ / count;
				GpuModel.originY = y + GpuModel.originY / count;
				GpuModel.originX = x + GpuModel.originX / count;
			} else {
				GpuModel.originY = y;
				GpuModel.originZ = z;
				GpuModel.originX = x;
			}
		} else if (type == 1) {
			for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
				this.verticesX[vertexIndex] += x;
				this.verticesY[vertexIndex] += y;
				this.verticesZ[vertexIndex] += z;
			}
		} else {
			@Pc(158) int local158;
			@Pc(176) int local176;
			if (type == 2) {
				for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
					this.verticesX[vertexIndex] -= GpuModel.originX;
					this.verticesY[vertexIndex] -= GpuModel.originY;
					this.verticesZ[vertexIndex] -= GpuModel.originZ;
					if (z != 0) {
						int sin = TextureCombineSource.SINE[z];
						int cos = TextureCombineSource.COSINE[z];
						int rotatedX = cos * this.verticesX[vertexIndex] + this.verticesY[vertexIndex] * sin + 16383 >> 14;
						this.verticesY[vertexIndex] = this.verticesY[vertexIndex] * cos + 16383 - this.verticesX[vertexIndex] * sin >> 14;
						this.verticesX[vertexIndex] = rotatedX;
					}
					if (x != 0) {
						int sin = TextureCombineSource.SINE[x];
						int cos = TextureCombineSource.COSINE[x];
						int rotatedY = this.verticesY[vertexIndex] * cos + 16383 - sin * this.verticesZ[vertexIndex] >> 14;
						this.verticesZ[vertexIndex] = this.verticesZ[vertexIndex] * cos + this.verticesY[vertexIndex] * sin + 16383 >> 14;
						this.verticesY[vertexIndex] = rotatedY;
					}
					if (y != 0) {
						int sin = TextureCombineSource.SINE[y];
						int cos = TextureCombineSource.COSINE[y];
						int rotatedX = cos * this.verticesX[vertexIndex] + this.verticesZ[vertexIndex] * sin + 16383 >> 14;
						this.verticesZ[vertexIndex] = cos * this.verticesZ[vertexIndex] + 16383 - sin * this.verticesX[vertexIndex] >> 14;
						this.verticesX[vertexIndex] = rotatedX;
					}
					this.verticesX[vertexIndex] += GpuModel.originX;
					this.verticesY[vertexIndex] += GpuModel.originY;
					this.verticesZ[vertexIndex] += GpuModel.originZ;
				}
			} else if (type == 3) {
				for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
					this.verticesX[vertexIndex] -= GpuModel.originX;
					this.verticesY[vertexIndex] -= GpuModel.originY;
					this.verticesZ[vertexIndex] -= GpuModel.originZ;
					this.verticesX[vertexIndex] = this.verticesX[vertexIndex] * x / 128;
					this.verticesY[vertexIndex] = this.verticesY[vertexIndex] * y / 128;
					this.verticesZ[vertexIndex] = this.verticesZ[vertexIndex] * z / 128;
					this.verticesX[vertexIndex] += GpuModel.originX;
					this.verticesY[vertexIndex] += GpuModel.originY;
					this.verticesZ[vertexIndex] += GpuModel.originZ;
				}
			} else {
				@Pc(497) NativeBillboardFace nativeBillboardFace;
				@Pc(502) NativeBillboardAttributes nativeBillboardAttributes;
				if (type == 5) {
					for (int faceIndex = 0; faceIndex < this.numFaces; faceIndex++) {
						int alpha = x * 8 + (this.facesAlpha[faceIndex] & 0xFF);
						if (alpha < 0) {
							alpha = 0;
						} else if (alpha > 255) {
							alpha = 255;
						}
						this.facesAlpha[faceIndex] = (byte) alpha;
					}
					if (this.billboardFaces != null) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							nativeBillboardFace = this.billboardFaces[billboardIndex];
							nativeBillboardAttributes = this.billboardAttributes[billboardIndex];
							nativeBillboardAttributes.colour = nativeBillboardAttributes.colour & 0xFFFFFF | 255 - (this.facesAlpha[nativeBillboardFace.face] & 0xFF) << 24;
						}
					}
					this.invalidateColours();
				} else if (type == 7) {
					for (int faceIndex = 0; faceIndex < this.numFaces; faceIndex++) {
						int faceColour = this.facesColor[faceIndex] & 0xFFFF;
						int hue = faceColour >> 10 & 0x3F;
						int saturation = faceColour >> 7 & 0x7;
						saturation += y / 4;
						@Pc(571) int newLightness = faceColour & 0x7F;
						@Pc(577) int newHue = hue + x & 0x3F;
						newLightness += z;
						if (saturation < 0) {
							saturation = 0;
						} else if (saturation > 7) {
							saturation = 7;
						}
						if (newLightness < 0) {
							newLightness = 0;
						} else if (newLightness > 127) {
							newLightness = 127;
						}
						this.facesColor[faceIndex] = (short) (saturation << 7 | newHue << 10 | newLightness);
					}
					if (this.billboardFaces != null) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							nativeBillboardFace = this.billboardFaces[billboardIndex];
							nativeBillboardAttributes = this.billboardAttributes[billboardIndex];
							nativeBillboardAttributes.colour = ColorUtil.hslToRgb[this.facesColor[nativeBillboardFace.face] & 0xFFFF] & 0xFFFFFF | nativeBillboardAttributes.colour & 0xFF000000;
						}
					}
					this.invalidateColours();
				} else {
					@Pc(687) NativeBillboardAttributes nativeBillboardAttributes2;
					if (type == 8) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							nativeBillboardAttributes2 = this.billboardAttributes[billboardIndex];
							nativeBillboardAttributes2.offsetY += y;
							nativeBillboardAttributes2.offsetX += x;
						}
					} else if (type == 10) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							nativeBillboardAttributes2 = this.billboardAttributes[billboardIndex];
							nativeBillboardAttributes2.scaleY = nativeBillboardAttributes2.scaleY * y >> 7;
							nativeBillboardAttributes2.scaleX = nativeBillboardAttributes2.scaleX * x >> 7;
						}
					} else if (type == 9) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							nativeBillboardAttributes2 = this.billboardAttributes[billboardIndex];
							nativeBillboardAttributes2.angle = nativeBillboardAttributes2.angle + x & 0x3FFF;
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!nca", name = "O", descriptor = "(III)V")
	@Override
	public void O(@OriginalArg(0) int scaleX, @OriginalArg(1) int scaleY, @OriginalArg(2) int scaleZ) {
		for (@Pc(3) int i = 0; i < this.maxDepth; i++) {
			if (scaleX != 128) {
				this.verticesX[i] = scaleX * this.verticesX[i] >> 7;
			}
			if (scaleY != 128) {
				this.verticesY[i] = scaleY * this.verticesY[i] >> 7;
			}
			if (scaleZ != 128) {
				this.verticesZ[i] = scaleZ * this.verticesZ[i] >> 7;
			}
		}
		this.invalidatePositions();
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(ILclient!fc;)V")
	private void projectShadow(@OriginalArg(1) GpuShadow shadow) {
		if (this.numNormals > GpuModel.projectedVerticesX.length) {
			GpuModel.projectedVerticesY = new int[this.numNormals];
			GpuModel.projectedVerticesX = new int[this.numNormals];
		}
		@Pc(50) int local50;
		@Pc(89) int local89;
		@Pc(98) int local98;
		for (@Pc(23) int i = 0; i < this.maxDepth; i++) {
			int shadowX = (this.verticesX[i] - (this.nativeToolkit.shadowOffsetX * this.verticesY[i] >> 8) >> this.nativeToolkit.shadowSizeShift) - shadow.startX;
			@Pc(75) int vertexShadowZ = (this.verticesZ[i] - (this.nativeToolkit.shadowOffsetZ * this.verticesY[i] >> 8) >> this.nativeToolkit.shadowSizeShift) - shadow.startZ;
			@Pc(80) int start = this.triangles[i];
			@Pc(87) int end = this.triangles[i + 1];
			for (int normalSlot = start; normalSlot < end; normalSlot++) {
				int normalIndex = this.vertexNormalIndices[normalSlot] - 1;
				if (normalIndex == -1) {
					break;
				}
				GpuModel.projectedVerticesX[normalIndex] = shadowX;
				GpuModel.projectedVerticesY[normalIndex] = vertexShadowZ;
			}
		}
		for (int faceIndex = 0; faceIndex < this.renderedFaceCount; faceIndex++) {
			if (this.facesAlpha == null || this.facesAlpha[faceIndex] <= 128) {
				@Pc(138) short vertexA = this.facesA[faceIndex];
				@Pc(143) short vertexB = this.facesB[faceIndex];
				@Pc(148) short vertexC = this.facesC[faceIndex];
				int xA = GpuModel.projectedVerticesX[vertexA];
				int xB = GpuModel.projectedVerticesX[vertexB];
				@Pc(160) int xC = GpuModel.projectedVerticesX[vertexC];
				@Pc(164) int zA = GpuModel.projectedVerticesY[vertexA];
				@Pc(168) int zB = GpuModel.projectedVerticesY[vertexB];
				@Pc(172) int zC = GpuModel.projectedVerticesY[vertexC];
				if (-((xC - xB) * (-zA + zB)) + (zB - zC) * (xA - xB) > 0) {
					shadow.fillTriangle(zA, xA, xB, zB, zC, xC);
				}
			}
		}
	}

	@OriginalMember(owner = "client!nca", name = "k", descriptor = "(I)V")
	@Override
	public void k(@OriginalArg(0) int angle) {
		@Pc(9) int sin = TextureCombineSource.SINE[angle];
		@Pc(13) int cos = TextureCombineSource.COSINE[angle];
		@Pc(33) int local33;
		for (@Pc(15) int i = 0; i < this.maxDepth; i++) {
			int rotatedX = this.verticesX[i] * cos + this.verticesZ[i] * sin >> 14;
			this.verticesZ[i] = cos * this.verticesZ[i] - sin * this.verticesX[i] >> 14;
			this.verticesX[i] = rotatedX;
		}
		for (int normalIndex = 0; normalIndex < this.numNormals; normalIndex++) {
			@Pc(84) int rotatedNormalX = this.normalsZ[normalIndex] * sin + this.normalsX[normalIndex] * cos >> 14;
			this.normalsZ[normalIndex] = (short) (cos * this.normalsZ[normalIndex] - sin * this.normalsX[normalIndex] >> 14);
			this.normalsX[normalIndex] = (short) rotatedNormalX;
		}
		this.invalidatePositions();
		this.invalidateLighting();
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!nca", name = "f", descriptor = "(I)Z")
	private boolean buildElementsBuffer() {
		if (this.indexStream.uploaded) {
			return true;
		}
		if (this.indexStream.buffer == null) {
			this.indexStream.buffer = this.nativeToolkit.createIndexBuffer(this.dynamic);
		}
		@Pc(28) GpuIndexBuffer gpuIndexBuffer = this.indexStream.buffer;
		gpuIndexBuffer.allocate(this.renderedFaceCount * 6);
		@Pc(40) Buffer buffer = gpuIndexBuffer.lock();
		if (buffer != null) {
			@Pc(56) Stream stream = this.nativeToolkit.wrapBuffer(buffer);
			@Pc(60) int face;
			if (Stream.c()) {
				for (face = 0; face < this.renderedFaceCount; face++) {
					stream.d(this.facesA[face]);
					stream.d(this.facesB[face]);
					stream.d(this.facesC[face]);
				}
			} else {
				for (face = 0; face < this.renderedFaceCount; face++) {
					stream.a(this.facesA[face]);
					stream.a(this.facesB[face]);
					stream.a(this.facesC[face]);
				}
			}
			stream.a();
			if (gpuIndexBuffer.unlock()) {
				this.indexStream.uploaded = true;
				this.updateRequired = true;
				this.indexStream.activeBuffer = gpuIndexBuffer;
				return true;
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(Lclient!bca;IZ)V")
	@Override
	public void transform(@OriginalArg(0) Matrix matrix, @OriginalArg(1) int submesh, @OriginalArg(2) boolean relative) {
		if (this.verticesSubmeshes == null) {
			return;
		}
		@Pc(12) int[] result = new int[3];
		for (@Pc(14) int i = 0; i < this.maxDepth; i++) {
			if ((submesh & this.verticesSubmeshes[i]) != 0) {
				if (relative) {
					matrix.projectRelative(this.verticesX[i], this.verticesY[i], this.verticesZ[i], result);
				} else {
					matrix.method4652(this.verticesX[i], this.verticesY[i], this.verticesZ[i], result);
				}
				this.verticesX[i] = result[0];
				this.verticesY[i] = result[1];
				this.verticesZ[i] = result[2];
			}
		}
	}

	@OriginalMember(owner = "client!nca", name = "g", descriptor = "(I)V")
	private void invalidatePositions() {
		if (this.positionStream != null) {
			this.positionStream.uploaded = false;
		}
	}

	@OriginalMember(owner = "client!nca", name = "H", descriptor = "(III)V")
	@Override
	public void H(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z) {
		for (@Pc(7) int i = 0; i < this.maxDepth; i++) {
			if (x != 0) {
				this.verticesX[i] += x;
			}
			if (y != 0) {
				this.verticesY[i] += y;
			}
			if (z != 0) {
				this.verticesZ[i] += z;
			}
		}
		this.invalidatePositions();
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(ZLclient!nca;Lclient!nca;IZI)Lclient!ka;")
	private Model copyModelData(@OriginalArg(0) boolean usesPreallocated, @OriginalArg(1) GpuModel gpuModel, @OriginalArg(2) GpuModel gpuModel2, @OriginalArg(3) int functionMask, @OriginalArg(4) boolean ensureLit) {
		gpuModel2.numVertices = this.numVertices;
		gpuModel2.numFaces = this.numFaces;
		gpuModel2.numNormals = this.numNormals;
		gpuModel2.contrast = this.contrast;
		gpuModel2.maxDepth = this.maxDepth;
		gpuModel2.renderedFaceCount = this.renderedFaceCount;
		gpuModel2.functionMask = functionMask;
		gpuModel2.preferenceFlags = this.preferenceFlags;
		gpuModel2.billboardCount = this.billboardCount;
		gpuModel2.textureAnimating = this.textureAnimating;
		if ((functionMask & 0x100) == 0) {
			gpuModel2.hasAlphaTransition = this.hasAlphaTransition;
		} else {
			gpuModel2.hasAlphaTransition = true;
		}
		gpuModel2.ambient = this.ambient;
		@Pc(70) boolean copyVerticesX = GpuModelRetainMasks.altersVerticesX(this.preferenceFlags, functionMask);
		@Pc(78) boolean copyVerticesY = GpuModelRetainMasks.altersVerticesY(functionMask, this.preferenceFlags);
		@Pc(84) boolean copyVerticesZ = GpuModelRetainMasks.altersVerticesZ(this.preferenceFlags, functionMask);
		@Pc(90) boolean copyVertices = copyVerticesX | copyVerticesY | copyVerticesZ;
		@Pc(209) int billboardCount;
		if (copyVertices) {
			if (!copyVerticesX) {
				gpuModel2.verticesX = this.verticesX;
			} else if (gpuModel.verticesX == null || gpuModel.verticesX.length < this.numVertices) {
				gpuModel2.verticesX = gpuModel.verticesX = new int[this.numVertices];
			} else {
				gpuModel2.verticesX = gpuModel.verticesX;
			}
			if (!copyVerticesY) {
				gpuModel2.verticesY = this.verticesY;
			} else if (gpuModel.verticesY == null || this.numVertices > gpuModel.verticesY.length) {
				gpuModel2.verticesY = gpuModel.verticesY = new int[this.numVertices];
			} else {
				gpuModel2.verticesY = gpuModel.verticesY;
			}
			if (!copyVerticesZ) {
				gpuModel2.verticesZ = this.verticesZ;
			} else if (gpuModel.verticesZ == null || gpuModel.verticesZ.length < this.numVertices) {
				gpuModel2.verticesZ = gpuModel.verticesZ = new int[this.numVertices];
			} else {
				gpuModel2.verticesZ = gpuModel.verticesZ;
			}
			for (billboardCount = 0; billboardCount < this.numVertices; billboardCount++) {
				if (copyVerticesX) {
					gpuModel2.verticesX[billboardCount] = this.verticesX[billboardCount];
				}
				if (copyVerticesY) {
					gpuModel2.verticesY[billboardCount] = this.verticesY[billboardCount];
				}
				if (copyVerticesZ) {
					gpuModel2.verticesZ[billboardCount] = this.verticesZ[billboardCount];
				}
			}
		} else {
			gpuModel2.verticesX = this.verticesX;
			gpuModel2.verticesZ = this.verticesZ;
			gpuModel2.verticesY = this.verticesY;
		}
		if (GpuModelRetainMasks.altersAnyVertices(functionMask, this.preferenceFlags)) {
			gpuModel2.positionStream = gpuModel.positionStream;
			gpuModel2.positionStream.activeBuffer = this.positionStream.activeBuffer;
			gpuModel2.positionStream.uploaded = this.positionStream.uploaded;
			gpuModel2.positionStream.dynamic = true;
		} else if (GpuModelRetainMasks.hasVertices(functionMask, this.preferenceFlags)) {
			gpuModel2.positionStream = this.positionStream;
		} else {
			gpuModel2.positionStream = null;
		}
		if (GpuModelRetainMasks.altersColor(functionMask, this.preferenceFlags)) {
			if (gpuModel.facesColor == null || gpuModel.facesColor.length < this.numFaces) {
				gpuModel2.facesColor = gpuModel.facesColor = new short[this.numFaces];
			} else {
				gpuModel2.facesColor = gpuModel.facesColor;
			}
			for (billboardCount = 0; billboardCount < this.numFaces; billboardCount++) {
				gpuModel2.facesColor[billboardCount] = this.facesColor[billboardCount];
			}
		} else {
			gpuModel2.facesColor = this.facesColor;
		}
		if (Model.retainFaceAlpha(this.preferenceFlags, functionMask)) {
			if (gpuModel.facesAlpha == null || gpuModel.facesAlpha.length < this.numFaces) {
				gpuModel2.facesAlpha = gpuModel.facesAlpha = new byte[this.numFaces];
			} else {
				gpuModel2.facesAlpha = gpuModel.facesAlpha;
			}
			for (billboardCount = 0; billboardCount < this.numFaces; billboardCount++) {
				gpuModel2.facesAlpha[billboardCount] = this.facesAlpha[billboardCount];
			}
		} else {
			gpuModel2.facesAlpha = this.facesAlpha;
		}
		if (GpuModelRetainMasks.requiresColorBufferRebuild(functionMask, this.preferenceFlags)) {
			gpuModel2.colourStream = gpuModel.colourStream;
			gpuModel2.colourStream.dynamic = true;
			gpuModel2.colourStream.uploaded = this.colourStream.uploaded;
			gpuModel2.colourStream.activeBuffer = this.colourStream.activeBuffer;
		} else if (GpuModelRetainMasks.hasColors(functionMask, this.preferenceFlags)) {
			gpuModel2.colourStream = this.colourStream;
		} else {
			gpuModel2.colourStream = null;
		}
		@Pc(598) int local598;
		if (GpuModelRetainMasks.altersLight(functionMask, this.preferenceFlags)) {
			if (gpuModel.normalsX == null || gpuModel.normalsX.length < this.numNormals) {
				billboardCount = this.numNormals;
				gpuModel2.normalsZ = gpuModel.normalsZ = new short[billboardCount];
				gpuModel2.normalsX = gpuModel.normalsX = new short[billboardCount];
				gpuModel2.normalsY = gpuModel.normalsY = new short[billboardCount];
			} else {
				gpuModel2.normalsZ = gpuModel.normalsZ;
				gpuModel2.normalsX = gpuModel.normalsX;
				gpuModel2.normalsY = gpuModel.normalsY;
			}
			if (this.mergedNormals == null) {
				gpuModel2.mergedNormals = null;
				for (billboardCount = 0; billboardCount < this.numNormals; billboardCount++) {
					gpuModel2.normalsX[billboardCount] = this.normalsX[billboardCount];
					gpuModel2.normalsY[billboardCount] = this.normalsY[billboardCount];
					gpuModel2.normalsZ[billboardCount] = this.normalsZ[billboardCount];
				}
			} else {
				if (gpuModel.mergedNormals == null) {
					gpuModel.mergedNormals = new GpuModelNormals();
				}
				@Pc(582) GpuModelNormals targetNormals = gpuModel2.mergedNormals = gpuModel.mergedNormals;
				if (targetNormals.normalsX == null || this.numNormals > targetNormals.normalsX.length) {
					int normalCount = this.numNormals;
					targetNormals.magnitudes = new byte[normalCount];
					targetNormals.normalsX = new short[normalCount];
					targetNormals.normalsZ = new short[normalCount];
					targetNormals.normalsY = new short[normalCount];
				}
				for (int normalIndex = 0; normalIndex < this.numNormals; normalIndex++) {
					gpuModel2.normalsX[normalIndex] = this.normalsX[normalIndex];
					gpuModel2.normalsY[normalIndex] = this.normalsY[normalIndex];
					gpuModel2.normalsZ[normalIndex] = this.normalsZ[normalIndex];
					targetNormals.normalsX[normalIndex] = this.mergedNormals.normalsX[normalIndex];
					targetNormals.normalsY[normalIndex] = this.mergedNormals.normalsY[normalIndex];
					targetNormals.normalsZ[normalIndex] = this.mergedNormals.normalsZ[normalIndex];
					targetNormals.magnitudes[normalIndex] = this.mergedNormals.magnitudes[normalIndex];
				}
			}
			gpuModel2.magnitudes = this.magnitudes;
		} else {
			gpuModel2.magnitudes = this.magnitudes;
			gpuModel2.mergedNormals = this.mergedNormals;
			gpuModel2.normalsZ = this.normalsZ;
			gpuModel2.normalsX = this.normalsX;
			gpuModel2.normalsY = this.normalsY;
		}
		if (GpuModelRetainMasks.requiresNormalsBufferRebuild(functionMask, this.preferenceFlags)) {
			gpuModel2.normalStream = gpuModel.normalStream;
			gpuModel2.normalStream.activeBuffer = this.normalStream.activeBuffer;
			gpuModel2.normalStream.dynamic = true;
			gpuModel2.normalStream.uploaded = this.normalStream.uploaded;
		} else if (GpuModelRetainMasks.hasNormals(this.preferenceFlags, functionMask)) {
			gpuModel2.normalStream = this.normalStream;
		} else {
			gpuModel2.normalStream = null;
		}
		if (GpuModelRetainMasks.altersTextureCoordinates(functionMask, this.preferenceFlags)) {
			if (gpuModel.textureU == null || this.numFaces > gpuModel.textureU.length) {
				billboardCount = this.numNormals;
				gpuModel2.textureV = gpuModel.textureV = new float[billboardCount];
				gpuModel2.textureU = gpuModel.textureU = new float[billboardCount];
			} else {
				gpuModel2.textureU = gpuModel.textureU;
				gpuModel2.textureV = gpuModel.textureV;
			}
			for (billboardCount = 0; billboardCount < this.numNormals; billboardCount++) {
				gpuModel2.textureU[billboardCount] = this.textureU[billboardCount];
				gpuModel2.textureV[billboardCount] = this.textureV[billboardCount];
			}
		} else {
			gpuModel2.textureU = this.textureU;
			gpuModel2.textureV = this.textureV;
		}
		if (GpuModelRetainMasks.requiresTexCoordBufferRebuild(this.preferenceFlags, functionMask)) {
			gpuModel2.texCoordStream = gpuModel.texCoordStream;
			gpuModel2.texCoordStream.activeBuffer = this.texCoordStream.activeBuffer;
			gpuModel2.texCoordStream.uploaded = this.texCoordStream.uploaded;
			gpuModel2.texCoordStream.dynamic = true;
		} else if (GpuModelRetainMasks.hasTextureCoordinates(this.preferenceFlags, functionMask)) {
			gpuModel2.texCoordStream = this.texCoordStream;
		} else {
			gpuModel2.texCoordStream = null;
		}
		if (GpuModelRetainMasks.altersFacing(functionMask, this.preferenceFlags)) {
			if (gpuModel.facesA == null || this.numFaces > gpuModel.facesA.length) {
				billboardCount = this.numFaces;
				gpuModel2.facesC = gpuModel.facesC = new short[billboardCount];
				gpuModel2.facesB = gpuModel.facesB = new short[billboardCount];
				gpuModel2.facesA = gpuModel.facesA = new short[billboardCount];
			} else {
				gpuModel2.facesA = gpuModel.facesA;
				gpuModel2.facesB = gpuModel.facesB;
				gpuModel2.facesC = gpuModel.facesC;
			}
			for (billboardCount = 0; billboardCount < this.numFaces; billboardCount++) {
				gpuModel2.facesA[billboardCount] = this.facesA[billboardCount];
				gpuModel2.facesB[billboardCount] = this.facesB[billboardCount];
				gpuModel2.facesC[billboardCount] = this.facesC[billboardCount];
			}
		} else {
			gpuModel2.facesB = this.facesB;
			gpuModel2.facesC = this.facesC;
			gpuModel2.facesA = this.facesA;
		}
		if (GpuModelRetainMasks.copyElementsBuffer(this.preferenceFlags, functionMask)) {
			gpuModel2.indexStream = gpuModel.indexStream;
			gpuModel2.indexStream.uploaded = this.indexStream.uploaded;
			gpuModel2.indexStream.dynamic = true;
			gpuModel2.indexStream.activeBuffer = this.indexStream.activeBuffer;
		} else if (GpuModelRetainMasks.hasElementsBuffer(functionMask, this.preferenceFlags)) {
			gpuModel2.indexStream = this.indexStream;
		} else {
			gpuModel2.indexStream = null;
		}
		if (GpuModelRetainMasks.altersTextures(this.preferenceFlags, functionMask)) {
			if (gpuModel.facesMaterial == null || gpuModel.facesMaterial.length < this.numFaces) {
				billboardCount = this.numFaces;
				gpuModel2.facesMaterial = gpuModel.facesMaterial = new short[billboardCount];
			} else {
				gpuModel2.facesMaterial = gpuModel.facesMaterial;
			}
			for (billboardCount = 0; billboardCount < this.numFaces; billboardCount++) {
				gpuModel2.facesMaterial[billboardCount] = this.facesMaterial[billboardCount];
			}
		} else {
			gpuModel2.facesMaterial = this.facesMaterial;
		}
		if (!GpuModelRetainMasks.copyBillboards(functionMask, this.preferenceFlags)) {
			gpuModel2.billboardAttributes = this.billboardAttributes;
		} else if (gpuModel.billboardAttributes == null || this.billboardCount > gpuModel.billboardAttributes.length) {
			billboardCount = this.billboardCount;
			gpuModel2.billboardAttributes = gpuModel.billboardAttributes = new NativeBillboardAttributes[billboardCount];
			for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
				gpuModel2.billboardAttributes[billboardIndex] = this.billboardAttributes[billboardIndex].copy();
			}
		} else {
			gpuModel2.billboardAttributes = gpuModel.billboardAttributes;
			for (billboardCount = 0; billboardCount < this.billboardCount; billboardCount++) {
				gpuModel2.billboardAttributes[billboardCount].copyFrom(this.billboardAttributes[billboardCount]);
			}
		}
		if (this.boundsCalculated) {
			gpuModel2.minZ = this.minZ;
			gpuModel2.size2d = this.size2d;
			gpuModel2.maxX = this.maxX;
			gpuModel2.maxY = this.maxY;
			gpuModel2.boundsCalculated = true;
			gpuModel2.size3d = this.size3d;
			gpuModel2.minY = this.minY;
			gpuModel2.minX = this.minX;
			gpuModel2.maxZ = this.maxZ;
		} else {
			gpuModel2.boundsCalculated = false;
		}
		gpuModel2.effectiveVertices = this.effectiveVertices;
		gpuModel2.facesSubmeshes = this.facesSubmeshes;
		gpuModel2.faceGroups = this.faceGroups;
		gpuModel2.triangles = this.triangles;
		gpuModel2.normalFaces = this.normalFaces;
		gpuModel2.verticesSubmeshes = this.verticesSubmeshes;
		gpuModel2.materialBatchOffsets = this.materialBatchOffsets;
		gpuModel2.vertexNormalIndices = this.vertexNormalIndices;
		gpuModel2.materialBatchVertexCount = this.materialBatchVertexCount;
		gpuModel2.billboardFaces = this.billboardFaces;
		gpuModel2.vertexGroups = this.vertexGroups;
		gpuModel2.materialBatchMinIndex = this.materialBatchMinIndex;
		gpuModel2.emissiveTriangles = this.emissiveTriangles;
		gpuModel2.billboardGroups = this.billboardGroups;
		gpuModel2.normalVertices = this.normalVertices;
		return gpuModel2;
	}

	@OriginalMember(owner = "client!nca", name = "ma", descriptor = "()I")
	@Override
	public int ma() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.size3d;
	}

	@OriginalMember(owner = "client!nca", name = "WA", descriptor = "()I")
	@Override
	public int WA() {
		return this.ambient;
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(Lclient!bca;)V")
	@Override
	public void updateParticlePosition(@OriginalArg(0) Matrix matrix) {
		@Pc(8) NativeMatrix modelMatrix = (NativeMatrix) matrix;
		@Pc(13) int local13;
		if (this.emissiveTriangles != null) {
			for (int emitterIndex = 0; emitterIndex < this.emissiveTriangles.length; emitterIndex++) {
				@Pc(20) EmissiveTriangle modelParticleEmitter = this.emissiveTriangles[emitterIndex];
				@Pc(22) EmissiveTriangle modelParticleEmitter2 = modelParticleEmitter;
				if (modelParticleEmitter.child != null) {
					modelParticleEmitter2 = modelParticleEmitter.child;
				}
				modelParticleEmitter2.verticeZC = (int) (modelMatrix.xD + (float) this.verticesZ[modelParticleEmitter.faceA] * modelMatrix.xC + (float) this.verticesX[modelParticleEmitter.faceA] * modelMatrix.xA + modelMatrix.xB * (float) this.verticesY[modelParticleEmitter.faceA]);
				modelParticleEmitter2.verticeXC = (int) (modelMatrix.yD + (float) this.verticesZ[modelParticleEmitter.faceA] * modelMatrix.yC + (float) this.verticesX[modelParticleEmitter.faceA] * modelMatrix.yA + (float) this.verticesY[modelParticleEmitter.faceA] * modelMatrix.yB);
				modelParticleEmitter2.verticeYA = (int) ((float) this.verticesY[modelParticleEmitter.faceA] * modelMatrix.zB + modelMatrix.zA * (float) this.verticesX[modelParticleEmitter.faceA] + (float) this.verticesZ[modelParticleEmitter.faceA] * modelMatrix.zC + modelMatrix.zD);
				modelParticleEmitter2.verticeZA = (int) (modelMatrix.xA * (float) this.verticesX[modelParticleEmitter.faceB] + (float) this.verticesY[modelParticleEmitter.faceB] * modelMatrix.xB + (float) this.verticesZ[modelParticleEmitter.faceB] * modelMatrix.xC + modelMatrix.xD);
				modelParticleEmitter2.verticeXA = (int) (modelMatrix.yD + (float) this.verticesZ[modelParticleEmitter.faceB] * modelMatrix.yC + modelMatrix.yA * (float) this.verticesX[modelParticleEmitter.faceB] + modelMatrix.yB * (float) this.verticesY[modelParticleEmitter.faceB]);
				modelParticleEmitter2.verticeZB = (int) (modelMatrix.zD + modelMatrix.zC * (float) this.verticesZ[modelParticleEmitter.faceB] + modelMatrix.zB * (float) this.verticesY[modelParticleEmitter.faceB] + modelMatrix.zA * (float) this.verticesX[modelParticleEmitter.faceB]);
				modelParticleEmitter2.verticeYB = (int) (modelMatrix.xD + modelMatrix.xB * (float) this.verticesY[modelParticleEmitter.vertexC] + (float) this.verticesX[modelParticleEmitter.vertexC] * modelMatrix.xA + (float) this.verticesZ[modelParticleEmitter.vertexC] * modelMatrix.xC);
				modelParticleEmitter2.transformedCY = (int) ((float) this.verticesZ[modelParticleEmitter.vertexC] * modelMatrix.yC + (float) this.verticesY[modelParticleEmitter.vertexC] * modelMatrix.yB + modelMatrix.yA * (float) this.verticesX[modelParticleEmitter.vertexC] + modelMatrix.yD);
				modelParticleEmitter2.verticeYC = (int) (modelMatrix.zA * (float) this.verticesX[modelParticleEmitter.vertexC] + modelMatrix.zB * (float) this.verticesY[modelParticleEmitter.vertexC] + (float) this.verticesZ[modelParticleEmitter.vertexC] * modelMatrix.zC + modelMatrix.zD);
			}
		}
		if (this.effectiveVertices == null) {
			return;
		}
		for (int effectorIndex = 0; effectorIndex < this.effectiveVertices.length; effectorIndex++) {
			@Pc(365) EffectiveVertex modelParticleEffector = this.effectiveVertices[effectorIndex];
			@Pc(367) EffectiveVertex modelParticleEffector2 = modelParticleEffector;
			if (modelParticleEffector.child != null) {
				modelParticleEffector2 = modelParticleEffector.child;
			}
			if (modelParticleEffector.matrix == null) {
				modelParticleEffector.matrix = modelMatrix.copy();
			} else {
				modelParticleEffector.matrix.apply(modelMatrix);
			}
			modelParticleEffector2.verticeZ = (int) ((float) this.verticesY[modelParticleEffector.appliesOnVertex] * modelMatrix.xB + (float) this.verticesX[modelParticleEffector.appliesOnVertex] * modelMatrix.xA + (float) this.verticesZ[modelParticleEffector.appliesOnVertex] * modelMatrix.xC + modelMatrix.xD);
			modelParticleEffector2.verticeX = (int) ((float) this.verticesZ[modelParticleEffector.appliesOnVertex] * modelMatrix.yC + modelMatrix.yB * (float) this.verticesY[modelParticleEffector.appliesOnVertex] + (float) this.verticesX[modelParticleEffector.appliesOnVertex] * modelMatrix.yA + modelMatrix.yD);
			modelParticleEffector2.verticeY = (int) ((float) this.verticesY[modelParticleEffector.appliesOnVertex] * modelMatrix.zB + (float) this.verticesX[modelParticleEffector.appliesOnVertex] * modelMatrix.zA + (float) this.verticesZ[modelParticleEffector.appliesOnVertex] * modelMatrix.zC + modelMatrix.zD);
		}
	}

	@OriginalMember(owner = "client!nca", name = "HA", descriptor = "()I")
	@Override
	public int HA() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.minZ;
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(Lclient!bca;Lclient!iw;II)V")
	@Override
	public void renderOrtho(@OriginalArg(0) Matrix matrix, @OriginalArg(1) BoundingBoxPolygon cylinder, @OriginalArg(2) int zoom, @OriginalArg(3) int flags) {
		if (this.numNormals == 0) {
			return;
		}
		@Pc(13) NativeMatrix viewMatrix43 = this.nativeToolkit.viewMatrix43;
		@Pc(16) NativeMatrix modelMatrix = (NativeMatrix) matrix;
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		GpuModel.modelViewZD = viewMatrix43.zD + modelMatrix.yD * viewMatrix43.zB + modelMatrix.xD * viewMatrix43.zA + modelMatrix.zD * viewMatrix43.zC;
		GpuModel.modelViewZB = viewMatrix43.zB * modelMatrix.yB + viewMatrix43.zA * modelMatrix.xB + viewMatrix43.zC * modelMatrix.zB;
		@Pc(69) float depthAtMinY = GpuModel.modelViewZD + (float) this.minY * GpuModel.modelViewZB;
		@Pc(77) float depthAtMaxY = GpuModel.modelViewZB * (float) this.maxY + GpuModel.modelViewZD;
		@Pc(94) float nearDepth;
		@Pc(87) float farDepth;
		if (depthAtMaxY < depthAtMinY) {
			farDepth = depthAtMinY + (float) this.size2d;
			nearDepth = (float) -this.size2d + depthAtMaxY;
		} else {
			nearDepth = (float) -this.size2d + depthAtMinY;
			farDepth = (float) this.size2d + depthAtMaxY;
		}
		if (this.nativeToolkit.orthoFar <= nearDepth || (float) this.nativeToolkit.camNear >= farDepth) {
			return;
		}
		GpuModel.modelViewXB = viewMatrix43.xA * modelMatrix.xB + modelMatrix.yB * viewMatrix43.xB + modelMatrix.zB * viewMatrix43.xC;
		GpuModel.modelViewXD = modelMatrix.yD * viewMatrix43.xB + viewMatrix43.xA * modelMatrix.xD + viewMatrix43.xC * modelMatrix.zD + viewMatrix43.xD;
		@Pc(171) float xAtMinY = (float) this.minY * GpuModel.modelViewXB + GpuModel.modelViewXD;
		@Pc(179) float xAtMaxY = GpuModel.modelViewXB * (float) this.maxY + GpuModel.modelViewXD;
		@Pc(206) float screenLeft;
		@Pc(194) float screenRight;
		if (xAtMaxY < xAtMinY) {
			screenRight = (float) this.nativeToolkit.projectionScaleX * ((float) this.size2d + xAtMinY);
			screenLeft = (float) this.nativeToolkit.projectionScaleX * (xAtMaxY - (float) this.size2d);
		} else {
			screenRight = (float) this.nativeToolkit.projectionScaleX * ((float) this.size2d + xAtMaxY);
			screenLeft = ((float) -this.size2d + xAtMinY) * (float) this.nativeToolkit.projectionScaleX;
		}
		if (this.nativeToolkit.viewX2 <= screenLeft / (float) zoom || screenRight / (float) zoom <= this.nativeToolkit.viewX1) {
			return;
		}
		GpuModel.modelViewYB = modelMatrix.zB * viewMatrix43.yC + viewMatrix43.yB * modelMatrix.yB + modelMatrix.xB * viewMatrix43.yA;
		GpuModel.modelViewYD = viewMatrix43.yD + viewMatrix43.yA * modelMatrix.xD + modelMatrix.yD * viewMatrix43.yB + modelMatrix.zD * viewMatrix43.yC;
		@Pc(297) float yAtMinY = GpuModel.modelViewYD + GpuModel.modelViewYB * (float) this.minY;
		@Pc(305) float yAtMaxY = (float) this.maxY * GpuModel.modelViewYB + GpuModel.modelViewYD;
		@Pc(332) float screenTop;
		@Pc(320) float screenBottom;
		if (yAtMaxY < yAtMinY) {
			screenBottom = (float) this.nativeToolkit.projectionScaleY * (yAtMinY + (float) this.size2d);
			screenTop = ((float) -this.size2d + yAtMaxY) * (float) this.nativeToolkit.projectionScaleY;
		} else {
			screenBottom = (yAtMaxY + (float) this.size2d) * (float) this.nativeToolkit.projectionScaleY;
			screenTop = (float) this.nativeToolkit.projectionScaleY * (yAtMinY - (float) this.size2d);
		}
		if (screenTop / (float) zoom >= this.nativeToolkit.viewY2 || screenBottom / (float) zoom <= this.nativeToolkit.viewY1) {
			return;
		}
		if (cylinder != null || this.billboardFaces != null) {
			GpuModel.modelViewXA = viewMatrix43.xB * modelMatrix.yA + viewMatrix43.xA * modelMatrix.xA + viewMatrix43.xC * modelMatrix.zA;
			GpuModel.modelViewXC = viewMatrix43.xA * modelMatrix.xC + modelMatrix.yC * viewMatrix43.xB + modelMatrix.zC * viewMatrix43.xC;
			GpuModel.modelViewYC = viewMatrix43.yA * modelMatrix.xC + viewMatrix43.yB * modelMatrix.yC + viewMatrix43.yC * modelMatrix.zC;
			GpuModel.modelViewZA = modelMatrix.zA * viewMatrix43.zC + viewMatrix43.zB * modelMatrix.yA + viewMatrix43.zA * modelMatrix.xA;
			GpuModel.modelViewYA = modelMatrix.xA * viewMatrix43.yA + modelMatrix.yA * viewMatrix43.yB + viewMatrix43.yC * modelMatrix.zA;
			GpuModel.modelViewZC = viewMatrix43.zA * modelMatrix.xC + viewMatrix43.zB * modelMatrix.yC + modelMatrix.zC * viewMatrix43.zC;
		}
		if (cylinder != null) {
			@Pc(499) int centerX = this.maxX + this.minX >> 1;
			@Pc(507) int centerZ = this.minZ + this.maxZ >> 1;
			@Pc(526) int bottomX = (int) (GpuModel.modelViewXD + (float) centerX * GpuModel.modelViewXA + GpuModel.modelViewXB * (float) this.minY + (float) centerZ * GpuModel.modelViewXC);
			@Pc(545) int bottomY = (int) (GpuModel.modelViewYD + GpuModel.modelViewYA * (float) centerX + (float) this.minY * GpuModel.modelViewYB + GpuModel.modelViewYC * (float) centerZ);
			@Pc(564) int bottomZ = (int) ((float) this.minY * GpuModel.modelViewZB + GpuModel.modelViewZA * (float) centerX + GpuModel.modelViewZD + (float) centerZ * GpuModel.modelViewZC);
			@Pc(583) int topX = (int) ((float) this.maxY * GpuModel.modelViewXB + GpuModel.modelViewXD + GpuModel.modelViewXA * (float) centerX + GpuModel.modelViewXC * (float) centerZ);
			@Pc(602) int topY = (int) (GpuModel.modelViewYC * (float) centerZ + GpuModel.modelViewYD + GpuModel.modelViewYA * (float) centerX + GpuModel.modelViewYB * (float) this.maxY);
			cylinder.bottom = bottomY * this.nativeToolkit.projectionScaleY / zoom + this.nativeToolkit.projectionCenterY;
			cylinder.left = this.nativeToolkit.projectionCenterX + bottomX * this.nativeToolkit.projectionScaleX / zoom;
			cylinder.top = this.nativeToolkit.projectionCenterY + this.nativeToolkit.projectionScaleY * topY / zoom;
			cylinder.right = topX * this.nativeToolkit.projectionScaleX / zoom + this.nativeToolkit.projectionCenterX;
			@Pc(675) int topZ = (int) (GpuModel.modelViewZC * (float) centerZ + (float) this.maxY * GpuModel.modelViewZB + GpuModel.modelViewZA * (float) centerX + GpuModel.modelViewZD);
			if (this.nativeToolkit.camNear <= bottomZ || topZ >= this.nativeToolkit.camNear) {
				cylinder.size = this.nativeToolkit.projectionCenterX + (this.size2d + bottomX) * this.nativeToolkit.projectionScaleX / zoom - cylinder.left;
				cylinder.positionCalculated = true;
			}
		}
		this.nativeToolkit.setOrthoZoom((float) zoom);
		this.nativeToolkit.setupOrthoSceneRenderState();
		this.nativeToolkit.setModelMatrix(modelMatrix);
		this.drawOpaqueFaces();
		this.renderBillboards();
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(IJIIIIFIFLclient!bh;I)S")
	private short connectNormal(@OriginalArg(0) int z, @OriginalArg(1) long uid, @OriginalArg(2) int face, @OriginalArg(3) int magnitude, @OriginalArg(4) int x, @OriginalArg(6) float u, @OriginalArg(7) int vertex, @OriginalArg(8) float v, @OriginalArg(9) Mesh mesh, @OriginalArg(10) int y) {
		@Pc(16) int start = this.triangles[vertex];
		@Pc(23) int end = this.triangles[vertex + 1];
		@Pc(25) int freeSlot = 0;
		for (@Pc(27) int i = start; i < end; i++) {
			@Pc(34) short normalIndex = this.vertexNormalIndices[i];
			if (normalIndex == 0) {
				freeSlot = i;
				break;
			}
			if (uid == GpuModel.normalsUid[i]) {
				return (short) (normalIndex - 1);
			}
		}
		this.vertexNormalIndices[freeSlot] = (short) (this.numNormals + 1);
		GpuModel.normalsUid[freeSlot] = uid;
		this.normalFaces[this.numNormals] = (short) face;
		this.normalVertices[this.numNormals] = (short) vertex;
		this.normalsX[this.numNormals] = (short) x;
		this.normalsY[this.numNormals] = (short) y;
		this.normalsZ[this.numNormals] = (short) z;
		this.magnitudes[this.numNormals] = (byte) magnitude;
		this.textureU[this.numNormals] = u;
		this.textureV[this.numNormals] = v;
		return (short) this.numNormals++;
	}

	@OriginalMember(owner = "client!nca", name = "r", descriptor = "()Z")
	@Override
	public boolean r() {
		return this.textureAnimating;
	}

	@OriginalMember(owner = "client!nca", name = "da", descriptor = "()I")
	@Override
	public int da() {
		return this.contrast;
	}

	@OriginalMember(owner = "client!nca", name = "RA", descriptor = "()I")
	@Override
	public int RA() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.maxX;
	}

	@OriginalMember(owner = "client!nca", name = "fa", descriptor = "()I")
	@Override
	public int fa() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.minY;
	}

	@OriginalMember(owner = "client!nca", name = "h", descriptor = "(I)V")
	private void invalidateColours() {
		if (this.colourStream != null) {
			this.colourStream.uploaded = false;
		}
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(I[IIIIIZ)V")
	@Override
	public void applyTransformUnmasked(@OriginalArg(0) int type, @OriginalArg(1) int[] labels, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int z, @OriginalArg(5) int rotation, @OriginalArg(6) boolean lights) {
		@Pc(8) int length = labels.length;
		@Pc(28) int local28;
		@Pc(32) int local32;
		@Pc(60) int local60;
		@Pc(18) int scaledX;
		@Pc(14) int scaledY;
		@Pc(22) int scaledZ;
		@Pc(38) int local38;
		@Pc(52) int[] groupMembers;
		@Pc(54) int local54;
		if (type == 0) {
			scaledY = y << 4;
			scaledX = x << 4;
			scaledZ = z << 4;
			GpuModel.originY = 0;
			GpuModel.originX = 0;
			int count = 0;
			GpuModel.originZ = 0;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int group = labels[labelIndex];
				if (this.vertexGroups.length > group) {
					groupMembers = this.vertexGroups[group];
					for (int groupMemberIndex = 0; groupMemberIndex < groupMembers.length; groupMemberIndex++) {
						int vertexIndex = groupMembers[groupMemberIndex];
						GpuModel.originX += this.verticesX[vertexIndex];
						GpuModel.originY += this.verticesY[vertexIndex];
						GpuModel.originZ += this.verticesZ[vertexIndex];
						count++;
					}
				}
			}
			if (count > 0) {
				GpuModel.originY = GpuModel.originY / count + scaledY;
				GpuModel.originZ = GpuModel.originZ / count + scaledZ;
				GpuModel.originX = GpuModel.originX / count + scaledX;
			} else {
				GpuModel.originY = scaledY;
				GpuModel.originZ = scaledZ;
				GpuModel.originX = scaledX;
			}
			return;
		}
		@Pc(162) int[] members;
		@Pc(164) int memberIndex;
		if (type == 1) {
			scaledY = y << 4;
			scaledX = x << 4;
			scaledZ = z << 4;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int group = labels[labelIndex];
				if (group < this.vertexGroups.length) {
					members = this.vertexGroups[group];
					for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
						int vertexIndex = members[memberIndex];
						this.verticesX[vertexIndex] += scaledX;
						this.verticesY[vertexIndex] += scaledY;
						this.verticesZ[vertexIndex] += scaledZ;
					}
				}
			}
			return;
		}
		@Pc(287) int local287;
		@Pc(305) int local305;
		@Pc(762) int local762;
		@Pc(771) int local771;
		if (type == 2) {
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int group = labels[labelIndex];
				if (this.vertexGroups.length > group) {
					members = this.vertexGroups[group];
					if ((rotation & 0x1) == 0) {
						for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
							int vertexIndex = members[memberIndex];
							this.verticesX[vertexIndex] -= GpuModel.originX;
							this.verticesY[vertexIndex] -= GpuModel.originY;
							this.verticesZ[vertexIndex] -= GpuModel.originZ;
							if (z != 0) {
								int sin = TextureCombineSource.SINE[z];
								int cos = TextureCombineSource.COSINE[z];
								int rotatedX = cos * this.verticesX[vertexIndex] + this.verticesY[vertexIndex] * sin + 16383 >> 14;
								this.verticesY[vertexIndex] = cos * this.verticesY[vertexIndex] + 16383 - sin * this.verticesX[vertexIndex] >> 14;
								this.verticesX[vertexIndex] = rotatedX;
							}
							if (x != 0) {
								int sin = TextureCombineSource.SINE[x];
								int cos = TextureCombineSource.COSINE[x];
								int rotatedY = cos * this.verticesY[vertexIndex] + 16383 - sin * this.verticesZ[vertexIndex] >> 14;
								this.verticesZ[vertexIndex] = sin * this.verticesY[vertexIndex] + this.verticesZ[vertexIndex] * cos + 16383 >> 14;
								this.verticesY[vertexIndex] = rotatedY;
							}
							if (y != 0) {
								int sin = TextureCombineSource.SINE[y];
								int cos = TextureCombineSource.COSINE[y];
								int rotatedX = sin * this.verticesZ[vertexIndex] + cos * this.verticesX[vertexIndex] + 16383 >> 14;
								this.verticesZ[vertexIndex] = cos * this.verticesZ[vertexIndex] + 16383 - sin * this.verticesX[vertexIndex] >> 14;
								this.verticesX[vertexIndex] = rotatedX;
							}
							this.verticesX[vertexIndex] += GpuModel.originX;
							this.verticesY[vertexIndex] += GpuModel.originY;
							this.verticesZ[vertexIndex] += GpuModel.originZ;
						}
					} else {
						for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
							int vertexIndex = members[memberIndex];
							this.verticesX[vertexIndex] -= GpuModel.originX;
							this.verticesY[vertexIndex] -= GpuModel.originY;
							this.verticesZ[vertexIndex] -= GpuModel.originZ;
							if (x != 0) {
								int sin = TextureCombineSource.SINE[x];
								int cos = TextureCombineSource.COSINE[x];
								int rotatedY = this.verticesY[vertexIndex] * cos + 16383 - sin * this.verticesZ[vertexIndex] >> 14;
								this.verticesZ[vertexIndex] = this.verticesZ[vertexIndex] * cos + sin * this.verticesY[vertexIndex] + 16383 >> 14;
								this.verticesY[vertexIndex] = rotatedY;
							}
							if (z != 0) {
								int sin = TextureCombineSource.SINE[z];
								int cos = TextureCombineSource.COSINE[z];
								int rotatedX = cos * this.verticesX[vertexIndex] + sin * this.verticesY[vertexIndex] + 16383 >> 14;
								this.verticesY[vertexIndex] = this.verticesY[vertexIndex] * cos + 16383 - this.verticesX[vertexIndex] * sin >> 14;
								this.verticesX[vertexIndex] = rotatedX;
							}
							if (y != 0) {
								int sin = TextureCombineSource.SINE[y];
								int cos = TextureCombineSource.COSINE[y];
								int rotatedX = cos * this.verticesX[vertexIndex] + sin * this.verticesZ[vertexIndex] + 16383 >> 14;
								this.verticesZ[vertexIndex] = this.verticesZ[vertexIndex] * cos + 16383 - this.verticesX[vertexIndex] * sin >> 14;
								this.verticesX[vertexIndex] = rotatedX;
							}
							this.verticesX[vertexIndex] += GpuModel.originX;
							this.verticesY[vertexIndex] += GpuModel.originY;
							this.verticesZ[vertexIndex] += GpuModel.originZ;
						}
					}
				}
			}
			if (lights) {
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					int group = labels[labelIndex];
					if (group < this.vertexGroups.length) {
						groupMembers = this.vertexGroups[group];
						for (int groupMemberIndex = 0; groupMemberIndex < groupMembers.length; groupMemberIndex++) {
							int vertexIndex = groupMembers[groupMemberIndex];
							int normalStart = this.triangles[vertexIndex];
							int normalEnd = this.triangles[vertexIndex + 1];
							for (int normalSlot = normalStart; normalSlot < normalEnd; normalSlot++) {
								int normalIndex = this.vertexNormalIndices[normalSlot] - 1;
								if (normalIndex == -1) {
									break;
								}
								@Pc(783) int sin;
								@Pc(787) int cos;
								@Pc(805) int rotated;
								if (z != 0) {
									sin = TextureCombineSource.SINE[z];
									cos = TextureCombineSource.COSINE[z];
									rotated = this.normalsY[normalIndex] * sin + this.normalsX[normalIndex] * cos + 16383 >> 14;
									this.normalsY[normalIndex] = (short) (cos * this.normalsY[normalIndex] + 16383 - this.normalsX[normalIndex] * sin >> 14);
									this.normalsX[normalIndex] = (short) rotated;
								}
								if (x != 0) {
									sin = TextureCombineSource.SINE[x];
									cos = TextureCombineSource.COSINE[x];
									rotated = this.normalsY[normalIndex] * cos + 16383 - sin * this.normalsZ[normalIndex] >> 14;
									this.normalsZ[normalIndex] = (short) (cos * this.normalsZ[normalIndex] + this.normalsY[normalIndex] * sin + 16383 >> 14);
									this.normalsY[normalIndex] = (short) rotated;
								}
								if (y != 0) {
									sin = TextureCombineSource.SINE[y];
									cos = TextureCombineSource.COSINE[y];
									rotated = cos * this.normalsX[normalIndex] + sin * this.normalsZ[normalIndex] + 16383 >> 14;
									this.normalsZ[normalIndex] = (short) (this.normalsZ[normalIndex] * cos + 16383 - sin * this.normalsX[normalIndex] >> 14);
									this.normalsX[normalIndex] = (short) rotated;
								}
							}
						}
					}
				}
				this.invalidateLighting();
			}
		} else if (type == 3) {
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int group = labels[labelIndex];
				if (this.vertexGroups.length > group) {
					members = this.vertexGroups[group];
					for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
						int vertexIndex = members[memberIndex];
						this.verticesX[vertexIndex] -= GpuModel.originX;
						this.verticesY[vertexIndex] -= GpuModel.originY;
						this.verticesZ[vertexIndex] -= GpuModel.originZ;
						this.verticesX[vertexIndex] = this.verticesX[vertexIndex] * x >> 7;
						this.verticesY[vertexIndex] = this.verticesY[vertexIndex] * y >> 7;
						this.verticesZ[vertexIndex] = z * this.verticesZ[vertexIndex] >> 7;
						this.verticesX[vertexIndex] += GpuModel.originX;
						this.verticesY[vertexIndex] += GpuModel.originY;
						this.verticesZ[vertexIndex] += GpuModel.originZ;
					}
				}
			}
		} else {
			@Pc(1207) NativeBillboardAttributes nativeBillboardAttributes;
			@Pc(1115) boolean modified;
			@Pc(1202) NativeBillboardFace nativeBillboardFace;
			if (type == 5) {
				if (this.faceGroups != null) {
					modified = false;
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int group = labels[labelIndex];
						if (this.faceGroups.length > group) {
							groupMembers = this.faceGroups[group];
							for (int groupMemberIndex = 0; groupMemberIndex < groupMembers.length; groupMemberIndex++) {
								int faceIndex = groupMembers[groupMemberIndex];
								int alpha = (this.facesAlpha[faceIndex] & 0xFF) + (x * 8);
								if (alpha < 0) {
									alpha = 0;
								} else if (alpha > 255) {
									alpha = 255;
								}
								this.facesAlpha[faceIndex] = (byte) alpha;
							}
							modified |= groupMembers.length > 0;
						}
					}
					if (modified) {
						if (this.billboardFaces != null) {
							for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
								nativeBillboardFace = this.billboardFaces[billboardIndex];
								nativeBillboardAttributes = this.billboardAttributes[billboardIndex];
								nativeBillboardAttributes.colour = nativeBillboardAttributes.colour & 0xFFFFFF | 255 - (this.facesAlpha[nativeBillboardFace.face] & 0xFF) << 24;
							}
						}
						this.invalidateColours();
					}
				}
			} else if (type == 7) {
				if (this.faceGroups != null) {
					modified = false;
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int group = labels[labelIndex];
						if (group < this.faceGroups.length) {
							groupMembers = this.faceGroups[group];
							for (int groupMemberIndex = 0; groupMemberIndex < groupMembers.length; groupMemberIndex++) {
								int faceIndex = groupMembers[groupMemberIndex];
								int faceColour = this.facesColor[faceIndex] & 0xFFFF;
								int hue = faceColour >> 10 & 0x3F;
								int saturation = faceColour >> 7 & 0x7;
								int lightness = faceColour & 0x7F;
								saturation += y / 4;
								@Pc(1310) int newHue = x + hue & 0x3F;
								if (saturation < 0) {
									saturation = 0;
								} else if (saturation > 7) {
									saturation = 7;
								}
								lightness += z;
								if (lightness < 0) {
									lightness = 0;
								} else if (lightness > 127) {
									lightness = 127;
								}
								this.facesColor[faceIndex] = (short) (lightness | newHue << 10 | saturation << 7);
							}
							modified |= groupMembers.length > 0;
						}
					}
					if (modified) {
						if (this.billboardFaces != null) {
							for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
								nativeBillboardFace = this.billboardFaces[billboardIndex];
								nativeBillboardAttributes = this.billboardAttributes[billboardIndex];
								nativeBillboardAttributes.colour = ColorUtil.hslToRgb[this.facesColor[nativeBillboardFace.face] & 0xFFFF] & 0xFFFFFF | nativeBillboardAttributes.colour & 0xFF000000;
							}
						}
						this.invalidateColours();
					}
				}
			} else if (type == 8) {
				if (this.billboardGroups != null) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int group = labels[labelIndex];
						if (group < this.billboardGroups.length) {
							members = this.billboardGroups[group];
							for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
								nativeBillboardAttributes = this.billboardAttributes[members[memberIndex]];
								nativeBillboardAttributes.offsetX += x;
								nativeBillboardAttributes.offsetY += y;
							}
						}
					}
				}
			} else if (type == 10) {
				if (this.billboardGroups != null) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int group = labels[labelIndex];
						if (group < this.billboardGroups.length) {
							members = this.billboardGroups[group];
							for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
								nativeBillboardAttributes = this.billboardAttributes[members[memberIndex]];
								nativeBillboardAttributes.scaleX = x * nativeBillboardAttributes.scaleX >> 7;
								nativeBillboardAttributes.scaleY = y * nativeBillboardAttributes.scaleY >> 7;
							}
						}
					}
				}
			} else if (type == 9 && this.billboardGroups != null) {
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					int group = labels[labelIndex];
					if (this.billboardGroups.length > group) {
						members = this.billboardGroups[group];
						for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
							nativeBillboardAttributes = this.billboardAttributes[members[memberIndex]];
							nativeBillboardAttributes.angle = x + nativeBillboardAttributes.angle & 0x3FFF;
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(Lclient!bca;Lclient!iw;I)V")
	@Override
	public void render(@OriginalArg(0) Matrix matrix, @OriginalArg(1) BoundingBoxPolygon cylinder, @OriginalArg(2) int flags) {
		if (this.numNormals == 0) {
			return;
		}
		@Pc(16) NativeMatrix viewMatrix43 = this.nativeToolkit.viewMatrix43;
		@Pc(19) NativeMatrix modelMatrix = (NativeMatrix) matrix;
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		GpuModel.modelViewZB = viewMatrix43.zA * modelMatrix.xB + viewMatrix43.zB * modelMatrix.yB + modelMatrix.zB * viewMatrix43.zC;
		GpuModel.modelViewZD = modelMatrix.zD * viewMatrix43.zC + viewMatrix43.zB * modelMatrix.yD + modelMatrix.xD * viewMatrix43.zA + viewMatrix43.zD;
		@Pc(72) float depthAtMinY = GpuModel.modelViewZB * (float) this.minY + GpuModel.modelViewZD;
		@Pc(80) float depthAtMaxY = (float) this.maxY * GpuModel.modelViewZB + GpuModel.modelViewZD;
		@Pc(96) float nearDepth;
		@Pc(90) float farDepth;
		if (depthAtMinY > depthAtMaxY) {
			farDepth = (float) this.size2d + depthAtMinY;
			nearDepth = depthAtMaxY - (float) this.size2d;
		} else {
			farDepth = (float) this.size2d + depthAtMaxY;
			nearDepth = depthAtMinY - (float) this.size2d;
		}
		if (this.nativeToolkit.perspectiveFar <= nearDepth || farDepth <= (float) this.nativeToolkit.camNear) {
			return;
		}
		GpuModel.modelViewXD = viewMatrix43.xD + viewMatrix43.xB * modelMatrix.yD + viewMatrix43.xA * modelMatrix.xD + viewMatrix43.xC * modelMatrix.zD;
		GpuModel.modelViewXB = viewMatrix43.xC * modelMatrix.zB + modelMatrix.xB * viewMatrix43.xA + viewMatrix43.xB * modelMatrix.yB;
		@Pc(172) float xAtMinY = (float) this.minY * GpuModel.modelViewXB + GpuModel.modelViewXD;
		@Pc(180) float xAtMaxY = GpuModel.modelViewXD + GpuModel.modelViewXB * (float) this.maxY;
		@Pc(207) float screenLeft;
		@Pc(195) float screenRight;
		if (xAtMinY > xAtMaxY) {
			screenRight = (xAtMinY + (float) this.size2d) * (float) this.nativeToolkit.projectionScaleX;
			screenLeft = (float) this.nativeToolkit.projectionScaleX * ((float) -this.size2d + xAtMaxY);
		} else {
			screenRight = (xAtMaxY + (float) this.size2d) * (float) this.nativeToolkit.projectionScaleX;
			screenLeft = (float) this.nativeToolkit.projectionScaleX * ((float) -this.size2d + xAtMinY);
		}
		if (this.nativeToolkit.viewX2 <= screenLeft / farDepth || screenRight / farDepth <= this.nativeToolkit.viewX1) {
			return;
		}
		GpuModel.modelViewYB = modelMatrix.zB * viewMatrix43.yC + viewMatrix43.yB * modelMatrix.yB + viewMatrix43.yA * modelMatrix.xB;
		GpuModel.modelViewYD = modelMatrix.zD * viewMatrix43.yC + modelMatrix.xD * viewMatrix43.yA + modelMatrix.yD * viewMatrix43.yB + viewMatrix43.yD;
		@Pc(297) float yAtMinY = (float) this.minY * GpuModel.modelViewYB + GpuModel.modelViewYD;
		@Pc(305) float yAtMaxY = GpuModel.modelViewYB * (float) this.maxY + GpuModel.modelViewYD;
		@Pc(332) float screenTop;
		@Pc(320) float screenBottom;
		if (yAtMaxY < yAtMinY) {
			screenBottom = (float) this.nativeToolkit.projectionScaleY * ((float) this.size2d + yAtMinY);
			screenTop = (float) this.nativeToolkit.projectionScaleY * ((float) -this.size2d + yAtMaxY);
		} else {
			screenBottom = ((float) this.size2d + yAtMaxY) * (float) this.nativeToolkit.projectionScaleY;
			screenTop = (yAtMinY - (float) this.size2d) * (float) this.nativeToolkit.projectionScaleY;
		}
		if (this.nativeToolkit.viewY2 <= screenTop / farDepth || screenBottom / farDepth <= this.nativeToolkit.viewY1) {
			return;
		}
		if (cylinder != null || this.billboardFaces != null) {
			GpuModel.modelViewZA = viewMatrix43.zC * modelMatrix.zA + modelMatrix.xA * viewMatrix43.zA + viewMatrix43.zB * modelMatrix.yA;
			GpuModel.modelViewYC = viewMatrix43.yA * modelMatrix.xC + modelMatrix.yC * viewMatrix43.yB + viewMatrix43.yC * modelMatrix.zC;
			GpuModel.modelViewXA = viewMatrix43.xC * modelMatrix.zA + viewMatrix43.xB * modelMatrix.yA + viewMatrix43.xA * modelMatrix.xA;
			GpuModel.modelViewXC = viewMatrix43.xA * modelMatrix.xC + modelMatrix.yC * viewMatrix43.xB + viewMatrix43.xC * modelMatrix.zC;
			GpuModel.modelViewYA = viewMatrix43.yA * modelMatrix.xA + viewMatrix43.yB * modelMatrix.yA + viewMatrix43.yC * modelMatrix.zA;
			GpuModel.modelViewZC = modelMatrix.zC * viewMatrix43.zC + modelMatrix.xC * viewMatrix43.zA + modelMatrix.yC * viewMatrix43.zB;
		}
		if (cylinder != null) {
			@Pc(491) boolean clipped = false;
			@Pc(493) boolean visible = true;
			@Pc(502) int centerX = this.minX + this.maxX >> 1;
			@Pc(510) int centerZ = this.minZ + this.maxZ >> 1;
			@Pc(529) int bottomX = (int) ((float) this.minY * GpuModel.modelViewXB + GpuModel.modelViewXD + GpuModel.modelViewXA * (float) centerX + GpuModel.modelViewXC * (float) centerZ);
			@Pc(548) int bottomY = (int) (GpuModel.modelViewYC * (float) centerZ + (float) centerX * GpuModel.modelViewYA + GpuModel.modelViewYD + GpuModel.modelViewYB * (float) this.minY);
			@Pc(567) int bottomZ = (int) (GpuModel.modelViewZD + (float) centerX * GpuModel.modelViewZA + (float) this.minY * GpuModel.modelViewZB + (float) centerZ * GpuModel.modelViewZC);
			if (bottomZ < this.nativeToolkit.camNear) {
				clipped = true;
			} else {
				cylinder.bottom = this.nativeToolkit.projectionCenterY + bottomY * this.nativeToolkit.projectionScaleY / bottomZ;
				cylinder.left = this.nativeToolkit.projectionScaleX * bottomX / bottomZ + this.nativeToolkit.projectionCenterX;
			}
			@Pc(626) int topX = (int) (GpuModel.modelViewXB * (float) this.maxY + GpuModel.modelViewXD + GpuModel.modelViewXA * (float) centerX + (float) centerZ * GpuModel.modelViewXC);
			@Pc(645) int topY = (int) (GpuModel.modelViewYB * (float) this.maxY + GpuModel.modelViewYD + (float) centerX * GpuModel.modelViewYA + (float) centerZ * GpuModel.modelViewYC);
			@Pc(664) int topZ = (int) ((float) this.maxY * GpuModel.modelViewZB + GpuModel.modelViewZD + (float) centerX * GpuModel.modelViewZA + GpuModel.modelViewZC * (float) centerZ);
			if (this.nativeToolkit.camNear > topZ) {
				clipped = true;
			} else {
				cylinder.right = this.nativeToolkit.projectionCenterX + topX * this.nativeToolkit.projectionScaleX / topZ;
				cylinder.top = this.nativeToolkit.projectionCenterY + topY * this.nativeToolkit.projectionScaleY / topZ;
			}
			if (clipped) {
				if (this.nativeToolkit.camNear > bottomZ && topZ < this.nativeToolkit.camNear) {
					visible = false;
				} else {
					@Pc(748) int clipRatio;
					@Pc(758) int clippedX;
					@Pc(769) int clippedY;
					if (this.nativeToolkit.camNear > bottomZ) {
						clipRatio = (topZ - this.nativeToolkit.camNear << 16) / (topZ - bottomZ);
						clippedX = (clipRatio * (topX - bottomX) >> 16) + topX;
						clippedY = topY + (clipRatio * (topY - bottomY) >> 16);
						cylinder.left = this.nativeToolkit.projectionCenterX + clippedX * this.nativeToolkit.projectionScaleX / this.nativeToolkit.camNear;
						cylinder.bottom = clippedY * this.nativeToolkit.projectionScaleY / this.nativeToolkit.camNear + this.nativeToolkit.projectionCenterY;
					} else if (this.nativeToolkit.camNear > topZ) {
						clipRatio = (bottomZ - this.nativeToolkit.camNear << 16) / (bottomZ - topZ);
						clippedX = bottomX + ((bottomX - topX) * clipRatio >> 16);
						clippedY = ((bottomY - topY) * clipRatio >> 16) + bottomY;
						cylinder.left = this.nativeToolkit.projectionCenterX + this.nativeToolkit.projectionScaleX * clippedX / this.nativeToolkit.camNear;
						cylinder.bottom = this.nativeToolkit.projectionCenterY + clippedY * this.nativeToolkit.projectionScaleY / this.nativeToolkit.camNear;
					}
				}
			}
			if (visible) {
				if (topZ >= bottomZ) {
					cylinder.size = this.nativeToolkit.projectionScaleX * (topX + this.size2d) / topZ + this.nativeToolkit.projectionCenterX - cylinder.right;
				} else {
					cylinder.size = (bottomX + this.size2d) * this.nativeToolkit.projectionScaleX / bottomZ + this.nativeToolkit.projectionCenterX - cylinder.left;
				}
				cylinder.positionCalculated = true;
			}
		}
		this.nativeToolkit.setupSceneRenderState();
		this.nativeToolkit.setModelMatrix(modelMatrix);
		this.drawOpaqueFaces();
		this.renderBillboards();
	}

	@OriginalMember(owner = "client!nca", name = "s", descriptor = "(I)V")
	@Override
	public void s(@OriginalArg(0) int functionMask) {
		if (this.positionStream != null) {
			this.positionStream.dynamic = GpuModelRetainMasks.altersAnyVertices(functionMask, this.preferenceFlags);
		}
		if (this.texCoordStream != null) {
			this.texCoordStream.dynamic = GpuModelRetainMasks.requiresTexCoordBufferRebuild(this.preferenceFlags, functionMask);
		}
		if (this.colourStream != null) {
			this.colourStream.dynamic = GpuModelRetainMasks.requiresColorBufferRebuild(functionMask, this.preferenceFlags);
		}
		if (this.normalStream != null) {
			this.normalStream.dynamic = GpuModelRetainMasks.requiresNormalsBufferRebuild(functionMask, this.preferenceFlags);
		}
		this.functionMask = functionMask;
		this.updateRequired = true;
		if (this.mergedNormals != null && (this.functionMask & 0x10000) == 0) {
			this.normalsY = this.mergedNormals.normalsY;
			this.normalsZ = this.mergedNormals.normalsZ;
			this.normalsX = this.mergedNormals.normalsX;
			this.magnitudes = this.mergedNormals.magnitudes;
			this.mergedNormals = null;
		}
		this.releaseUploadedData();
	}

	@OriginalMember(owner = "client!nca", name = "V", descriptor = "()I")
	@Override
	public int V() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.minX;
	}

	@OriginalMember(owner = "client!nca", name = "c", descriptor = "()Z")
	@Override
	public boolean loadedTextures() {
		if (this.facesMaterial == null) {
			return true;
		}
		for (@Pc(12) int i = 0; i < this.facesMaterial.length; i++) {
			if (this.facesMaterial[i] != -1 && !this.nativeToolkit.materialProvider.textureAvailable(this.facesMaterial[i])) {
				return false;
			}
		}
		return true;
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(IIIIIIIII)Z")
	private boolean isHovering(@OriginalArg(0) int mouseX, @OriginalArg(1) int xC, @OriginalArg(2) int xB, @OriginalArg(3) int yC, @OriginalArg(4) int yA, @OriginalArg(5) int mouseY, @OriginalArg(7) int xA, @OriginalArg(8) int yB) {
		if (mouseY < yA && mouseY < yB && mouseY < yC) {
			return false;
		} else if (mouseY > yA && yB < mouseY && mouseY > yC) {
			return false;
		} else if (xA > mouseX && xB > mouseX && mouseX < xC) {
			return false;
		} else {
			return xA >= mouseX || xB >= mouseX || mouseX <= xC;
		}
	}

	@OriginalMember(owner = "client!nca", name = "b", descriptor = "()V")
	@Override
	public void uploadToGpu() {
		if (this.numNormals > 0 && this.renderedFaceCount > 0) {
			this.uploadStreams();
			this.buildElementsBuffer();
			this.releaseUploadedData();
		}
	}

	@OriginalMember(owner = "client!nca", name = "i", descriptor = "(I)V")
	private void drawOpaqueFaces() {
		if (this.renderedFaceCount == 0) {
			return;
		}
		if (this.uploadStreams() && this.buildElementsBuffer()) {
			this.nativeToolkit.setStreamSource(this.positionStream.activeBuffer, 0);
			this.nativeToolkit.setStreamSource(this.colourStream.activeBuffer, 1);
			this.nativeToolkit.setStreamSource(this.texCoordStream.activeBuffer, 2);
			@Pc(61) boolean lit;
			if ((this.preferenceFlags & 0x37) == 0) {
				lit = false;
				this.nativeToolkit.toggleLights(false);
				this.nativeToolkit.setVertexDeclaration(this.nativeToolkit.positionColorTexCoordDeclaration);
			} else {
				lit = true;
				this.nativeToolkit.toggleLights(true);
				this.nativeToolkit.setStreamSource(this.normalStream.activeBuffer, 3);
				this.nativeToolkit.setVertexDeclaration(this.nativeToolkit.positionColorTexCoordNormalDeclaration);
			}
			for (@Pc(101) int i = 0; i < this.materialBatchMinIndex.length; i++) {
				@Pc(108) int start = this.materialBatchOffsets[i];
				@Pc(115) int end = this.materialBatchOffsets[i + 1];
				@Pc(122) int materialId = this.facesMaterial[start] & 0xFFFF;
				if (materialId == 65535) {
					materialId = -1;
				}
				this.nativeToolkit.setActiveMaterial(true, lit, materialId);
				this.nativeToolkit.drawIndexedPrimitive(PrimitiveType.aClass134_6, end - start, this.indexStream.activeBuffer, this.materialBatchMinIndex[i], start * 3, this.materialBatchVertexCount[i]);
			}
		}
		this.releaseUploadedData();
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(IILclient!bca;ZII)Z")
	@Override
	public boolean pickedOrtho(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) Matrix matrix, @OriginalArg(3) boolean quick, @OriginalArg(4) int sizeShift, @OriginalArg(5) int angle) {
		return this.pick(x, sizeShift, angle, y, quick, matrix);
	}

	@OriginalMember(owner = "client!nca", name = "c", descriptor = "(B)V")
	private void invalidateLighting() {
		if ((this.preferenceFlags & 0x37) == 0) {
			if (this.colourStream != null) {
				this.colourStream.uploaded = false;
			}
		} else if (this.normalStream != null) {
			this.normalStream.uploaded = false;
		}
	}

	@OriginalMember(owner = "client!nca", name = "j", descriptor = "(I)V")
	private void invalidateIndices() {
		if (this.indexStream != null) {
			this.indexStream.uploaded = false;
		}
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(Lclient!ka;IIIZ)V")
	@Override
	public void shareLight(@OriginalArg(0) Model model, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int z, @OriginalArg(4) boolean renderTypeAdjustment) {
		@Pc(8) GpuModel gpuModel = (GpuModel) model;
		if (this.numFaces == 0 || gpuModel.numFaces == 0) {
			return;
		}
		@Pc(24) int otherVertexCount = gpuModel.maxDepth;
		@Pc(27) int[] otherVerticesX = gpuModel.verticesX;
		@Pc(30) int[] otherVerticesY = gpuModel.verticesY;
		@Pc(33) int[] otherVerticesZ = gpuModel.verticesZ;
		@Pc(36) short[] otherNormalsX = gpuModel.normalsX;
		@Pc(39) short[] otherNormalsY = gpuModel.normalsY;
		@Pc(42) short[] otherNormalsZ = gpuModel.normalsZ;
		@Pc(45) byte[] otherMagnitudes = gpuModel.magnitudes;
		@Pc(60) short[] mergedNormalsX;
		@Pc(56) short[] mergedNormalsY;
		@Pc(64) short[] mergedNormalsZ;
		@Pc(52) byte[] mergedMagnitudes;
		if (this.mergedNormals == null) {
			mergedNormalsY = null;
			mergedNormalsX = null;
			mergedMagnitudes = null;
			mergedNormalsZ = null;
		} else {
			mergedMagnitudes = this.mergedNormals.magnitudes;
			mergedNormalsY = this.mergedNormals.normalsY;
			mergedNormalsX = this.mergedNormals.normalsX;
			mergedNormalsZ = this.mergedNormals.normalsZ;
		}
		@Pc(93) short[] otherMergedNormalsX;
		@Pc(89) short[] otherMergedNormalsY;
		@Pc(81) short[] otherMergedNormalsZ;
		@Pc(85) byte[] otherMergedMagnitudes;
		if (gpuModel.mergedNormals == null) {
			otherMergedNormalsY = null;
			otherMergedMagnitudes = null;
			otherMergedNormalsZ = null;
			otherMergedNormalsX = null;
		} else {
			otherMergedNormalsZ = gpuModel.mergedNormals.normalsZ;
			otherMergedMagnitudes = gpuModel.mergedNormals.magnitudes;
			otherMergedNormalsY = gpuModel.mergedNormals.normalsY;
			otherMergedNormalsX = gpuModel.mergedNormals.normalsX;
		}
		@Pc(106) int[] otherTriangles = gpuModel.triangles;
		@Pc(109) short[] otherVertexNormalIndices = gpuModel.vertexNormalIndices;
		if (!gpuModel.boundsCalculated) {
			gpuModel.calculateBounds();
		}
		@Pc(118) int otherMinY = gpuModel.minY;
		@Pc(121) int otherMaxY = gpuModel.maxY;
		@Pc(124) int otherMinX = gpuModel.minX;
		@Pc(127) int otherMaxX = gpuModel.maxX;
		@Pc(130) int otherMinZ = gpuModel.minZ;
		@Pc(133) int otherMaxZ = gpuModel.maxZ;
		for (@Pc(135) int i = 0; i < this.maxDepth; i++) {
			@Pc(144) int relY = this.verticesY[i] - y;
			if (otherMinY <= relY && relY <= otherMaxY) {
				@Pc(160) int relX = this.verticesX[i] - x;
				if (relX >= otherMinX && relX <= otherMaxX) {
					@Pc(175) int relZ = this.verticesZ[i] - z;
					if (otherMinZ <= relZ && otherMaxZ >= relZ) {
						@Pc(189) int normalIndex = -1;
						@Pc(194) int start = this.triangles[i];
						@Pc(201) int end = this.triangles[i + 1];
						for (@Pc(203) int j = start; j < end; j++) {
							normalIndex = this.vertexNormalIndices[j] - 1;
							if (normalIndex == -1 || this.magnitudes[normalIndex] != 0) {
								break;
							}
						}
						if (normalIndex != -1) {
							for (@Pc(233) int k = 0; k < otherVertexCount; k++) {
								if (relX == otherVerticesX[k] && relZ == otherVerticesZ[k] && otherVerticesY[k] == relY) {
									@Pc(264) int otherNormalIndex = -1;
									start = otherTriangles[k];
									end = otherTriangles[k + 1];
									for (@Pc(276) int m = start; m < end; m++) {
										otherNormalIndex = otherVertexNormalIndices[m] - 1;
										if (otherNormalIndex == -1 || otherMagnitudes[otherNormalIndex] != 0) {
											break;
										}
									}
									if (otherNormalIndex != -1) {
										if (mergedNormalsX == null) {
											this.mergedNormals = new GpuModelNormals();
											mergedNormalsX = this.mergedNormals.normalsX = Arrays.copyOf(this.normalsX);
											mergedNormalsY = this.mergedNormals.normalsY = Arrays.copyOf(this.normalsY);
											mergedNormalsZ = this.mergedNormals.normalsZ = Arrays.copyOf(this.normalsZ);
											mergedMagnitudes = this.mergedNormals.magnitudes = Arrays.copyOf(this.magnitudes);
										}
										if (otherMergedNormalsX == null) {
											@Pc(352) GpuModelNormals otherMergedNormals = gpuModel.mergedNormals = new GpuModelNormals();
											otherMergedNormalsX = otherMergedNormals.normalsX = Arrays.copyOf(otherNormalsX);
											otherMergedNormalsY = otherMergedNormals.normalsY = Arrays.copyOf(otherNormalsY);
											otherMergedNormalsZ = otherMergedNormals.normalsZ = Arrays.copyOf(otherNormalsZ);
											otherMergedMagnitudes = otherMergedNormals.magnitudes = Arrays.copyOf(otherMagnitudes);
										}
										@Pc(385) short normalX = this.normalsX[normalIndex];
										@Pc(390) short normalY = this.normalsY[normalIndex];
										@Pc(395) short normalZ = this.normalsZ[normalIndex];
										start = otherTriangles[k];
										end = otherTriangles[k + 1];
										@Pc(410) byte magnitude = this.magnitudes[normalIndex];
										@Pc(420) int local420;
										for (@Pc(412) int index = start; index < end; index++) {
											int otherTargetNormalIndex = otherVertexNormalIndices[index] - 1;
											if (otherTargetNormalIndex == -1) {
												break;
											}
											if (otherMergedMagnitudes[otherTargetNormalIndex] != 0) {
												otherMergedNormalsX[otherTargetNormalIndex] += normalX;
												otherMergedNormalsY[otherTargetNormalIndex] += normalY;
												otherMergedNormalsZ[otherTargetNormalIndex] += normalZ;
												otherMergedMagnitudes[otherTargetNormalIndex] += magnitude;
											}
										}
										start = this.triangles[i];
										normalZ = otherNormalsZ[otherNormalIndex];
										magnitude = otherMagnitudes[otherNormalIndex];
										normalX = otherNormalsX[otherNormalIndex];
										normalY = otherNormalsY[otherNormalIndex];
										end = this.triangles[i + 1];
										for (int normalSlot = start; normalSlot < end; normalSlot++) {
											@Pc(506) int targetNormalIndex = this.vertexNormalIndices[normalSlot] - 1;
											if (targetNormalIndex == -1) {
												break;
											}
											if (mergedMagnitudes[targetNormalIndex] != 0) {
												mergedNormalsX[targetNormalIndex] += normalX;
												mergedNormalsY[targetNormalIndex] += normalY;
												mergedNormalsZ[targetNormalIndex] += normalZ;
												mergedMagnitudes[targetNormalIndex] += magnitude;
											}
										}
										gpuModel.invalidateLighting();
										this.invalidateLighting();
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!nca", name = "ia", descriptor = "(SS)V")
	@Override
	public void ia(@OriginalArg(0) short src, @OriginalArg(1) short dst) {
		for (@Pc(7) int i = 0; i < this.numFaces; i++) {
			if (this.facesColor[i] == src) {
				this.facesColor[i] = dst;
			}
		}
		if (this.billboardFaces != null) {
			for (@Pc(34) int j = 0; j < this.billboardCount; j++) {
				@Pc(41) NativeBillboardFace nativeBillboardFace = this.billboardFaces[j];
				@Pc(46) NativeBillboardAttributes nativeBillboardAttributes = this.billboardAttributes[j];
				nativeBillboardAttributes.colour = ColorUtil.hslToRgb[this.facesColor[nativeBillboardFace.face] & 0xFFFF] & 0xFFFFFF | nativeBillboardAttributes.colour & 0xFF000000;
			}
		}
		this.invalidateColours();
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(BIZ)Lclient!ka;")
	@Override
	public Model copy(@OriginalArg(0) byte copyType, @OriginalArg(1) int functionMask, @OriginalArg(2) boolean ensureLit) {
		@Pc(32) GpuModel gpuModel;
		@Pc(36) GpuModel gpuModel2;
		if (copyType == 1) {
			gpuModel2 = this.nativeToolkit.copyType1Scratch;
			gpuModel = this.nativeToolkit.copyType1Result;
		} else if (copyType == 2) {
			gpuModel2 = this.nativeToolkit.copyType2Scratch;
			gpuModel = this.nativeToolkit.copyType2Result;
		} else if (copyType == 3) {
			gpuModel2 = this.nativeToolkit.copyType3Scratch;
			gpuModel = this.nativeToolkit.copyType3Result;
		} else if (copyType == 4) {
			gpuModel2 = this.nativeToolkit.copyType4Scratch;
			gpuModel = this.nativeToolkit.copyType4Result;
		} else if (copyType == 5) {
			gpuModel = this.nativeToolkit.copyType5Result;
			gpuModel2 = this.nativeToolkit.copyType5Scratch;
		} else {
			gpuModel = gpuModel2 = new GpuModel(this.nativeToolkit, 0, 0, true, false);
		}
		return this.copyModelData(copyType != 0, gpuModel2, gpuModel, functionMask, ensureLit);
	}

	@OriginalMember(owner = "client!nca", name = "p", descriptor = "(IILclient!s;Lclient!s;III)V")
	@Override
	public void p(@OriginalArg(0) int contourType, @OriginalArg(1) int contourModifier, @OriginalArg(2) Ground ground, @OriginalArg(3) Ground ground2, @OriginalArg(4) int x, @OriginalArg(5) int y, @OriginalArg(6) int z) {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		@Pc(17) int startX = x + this.minX;
		@Pc(22) int endX = this.maxX + x;
		@Pc(27) int startZ = this.minZ + z;
		@Pc(33) int endZ = z + this.maxZ;
		if ((contourType == 1 || contourType == 2 || contourType == 3 || contourType == 5) && (startX < 0 || ground.width <= endX + ground.tileUnits >> ground.tileScale || startZ < 0 || ground.length <= ground.tileUnits + endZ >> ground.tileScale)) {
			return;
		}
		if (contourType == 4 || contourType == 5) {
			if (ground2 == null) {
				return;
			}
			if (startX < 0 || ground2.width <= ground2.tileUnits + endX >> ground2.tileScale || startZ < 0 || ground2.length <= endZ + ground2.tileUnits >> ground2.tileScale) {
				return;
			}
		} else {
			startX >>= ground.tileScale;
			endX = ground.tileUnits + endX - 1 >> ground.tileScale;
			startZ >>= ground.tileScale;
			endZ = ground.tileUnits + endZ - 1 >> ground.tileScale;
			if (y == ground.getHeight(startZ, startX) && y == ground.getHeight(startZ, endX) && ground.getHeight(endZ, startX) == y && ground.getHeight(endZ, endX) == y) {
				return;
			}
		}
		@Pc(192) int local192;
		if (contourType == 1) {
			for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
				this.verticesY[vertexIndex] += ground.averageHeight(this.verticesX[vertexIndex] + x, this.verticesZ[vertexIndex] + z) - y;
			}
		} else {
			@Pc(197) int local197;
			@Pc(208) int local208;
			if (contourType == 2) {
				int minHeight = this.minY;
				if (minHeight == 0) {
					return;
				}
				for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
					int heightRatio = (this.verticesY[vertexIndex] << 16) / minHeight;
					if (heightRatio < contourModifier) {
						this.verticesY[vertexIndex] -= -((contourModifier - heightRatio) * (-y + ground.averageHeight(x + this.verticesX[vertexIndex], this.verticesZ[vertexIndex] - -z)) / contourModifier);
					}
				}
			} else {
				@Pc(296) int local296;
				if (contourType == 3) {
					int sizeX = (contourModifier & 0xFF) * 4;
					int sizeZ = (contourModifier >> 8 & 0xFF) * 4;
					int maxRotateX = (contourModifier >> 16 & 0xFF) << 6;
					int maxRotateZ = (contourModifier >> 24 & 0xFF) << 6;
					if (x - (sizeX >> 1) < 0 || ground.width << ground.tileScale <= ground.tileUnits + (sizeX >> 1) + x || z - (sizeZ >> 1) < 0 || ground.length << ground.tileScale <= ground.tileUnits + (sizeZ >> 1) + z) {
						return;
					}
					this.hillChange(sizeX, maxRotateZ, x, sizeZ, y, maxRotateX, ground, z);
				} else if (contourType == 4) {
					int modelHeight = this.maxY - this.minY;
					for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
						this.verticesY[vertexIndex] = modelHeight + this.verticesY[vertexIndex] + ground2.averageHeight(this.verticesX[vertexIndex] + x, this.verticesZ[vertexIndex] - -z) - y;
					}
				} else if (contourType == 5) {
					int modelHeight = this.maxY - this.minY;
					for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
						int worldX = x + this.verticesX[vertexIndex];
						int worldZ = z + this.verticesZ[vertexIndex];
						@Pc(302) int height = ground.averageHeight(worldX, worldZ);
						@Pc(308) int upperHeight = ground2.averageHeight(worldX, worldZ);
						@Pc(315) int heightDelta = height - upperHeight - contourModifier;
						this.verticesY[vertexIndex] = (heightDelta * ((this.verticesY[vertexIndex] << 8) / modelHeight) >> 8) + height - y;
					}
				}
			}
		}
		this.invalidatePositions();
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!nca", name = "F", descriptor = "()Z")
	@Override
	public boolean F() {
		return this.hasAlphaTransition;
	}

	@OriginalMember(owner = "client!nca", name = "e", descriptor = "()V")
	@Override
	public void unlock() {
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(IIII)V")
	@Override
	public void adjustColours(@OriginalArg(0) int hue, @OriginalArg(1) int saturation, @OriginalArg(2) int lightness, @OriginalArg(3) int opacity) {
		@Pc(16) int local16;
		for (@Pc(7) int i = 0; i < this.numFaces; i++) {
			int faceColour = this.facesColor[i] & 0xFFFF;
			@Pc(22) int faceHue = faceColour >> 10 & 0x3F;
			@Pc(28) int faceSaturation = faceColour >> 7 & 0x7;
			if (hue != -1) {
				faceHue += opacity * (hue - faceHue) >> 7;
			}
			@Pc(46) int faceLightness = faceColour & 0x7F;
			if (saturation != -1) {
				faceSaturation += (saturation - faceSaturation) * opacity >> 7;
			}
			if (lightness != -1) {
				faceLightness += (lightness - faceLightness) * opacity >> 7;
			}
			this.facesColor[i] = (short) (faceHue << 10 | faceSaturation << 7 | faceLightness);
		}
		if (this.billboardFaces != null) {
			for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
				@Pc(109) NativeBillboardFace nativeBillboardFace = this.billboardFaces[billboardIndex];
				@Pc(114) NativeBillboardAttributes nativeBillboardAttributes = this.billboardAttributes[billboardIndex];
				nativeBillboardAttributes.colour = nativeBillboardAttributes.colour & 0xFF000000 | ColorUtil.hslToRgb[this.facesColor[nativeBillboardFace.face] & 0xFFFF] & 0xFFFFFF;
			}
		}
		this.invalidateColours();
	}

	@OriginalMember(owner = "client!nca", name = "v", descriptor = "()V")
	@Override
	public void v() {
		for (@Pc(7) int i = 0; i < this.maxDepth; i++) {
			this.verticesZ[i] = -this.verticesZ[i];
		}
		for (@Pc(29) int j = 0; j < this.numNormals; j++) {
			this.normalsZ[j] = (short) -this.normalsZ[j];
		}
		for (@Pc(48) int k = 0; k < this.numFaces; k++) {
			@Pc(55) short temp = this.facesA[k];
			this.facesA[k] = this.facesC[k];
			this.facesC[k] = temp;
		}
		this.invalidatePositions();
		this.invalidateLighting();
		this.invalidateIndices();
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!nca", name = "aa", descriptor = "(SS)V")
	@Override
	public void aa(@OriginalArg(0) short src, @OriginalArg(1) short dst) {
		@Pc(9) MaterialProvider textureSource = this.nativeToolkit.materialProvider;
		for (@Pc(11) int i = 0; i < this.numFaces; i++) {
			if (this.facesMaterial[i] == src) {
				this.facesMaterial[i] = dst;
			}
		}
		@Pc(35) byte alpha = 0;
		@Pc(37) byte brightness = 0;
		if (src != -1) {
			@Pc(48) MaterialType textureMetrics = textureSource.getMetrics(src & 0xFFFF);
			brightness = textureMetrics.brightness;
			alpha = textureMetrics.alpha;
		}
		@Pc(56) byte newAlpha = 0;
		@Pc(58) byte newBrightness = 0;
		if (dst != -1) {
			@Pc(68) MaterialType textureMetrics2 = textureSource.getMetrics(dst & 0xFFFF);
			newBrightness = textureMetrics2.brightness;
			if (textureMetrics2.speedU != 0 || textureMetrics2.speedV != 0) {
				this.textureAnimating = true;
			}
			newAlpha = textureMetrics2.alpha;
		}
		if (!(newBrightness != brightness | alpha != newAlpha)) {
			return;
		}
		if (this.billboardFaces != null) {
			for (@Pc(105) int j = 0; j < this.billboardCount; j++) {
				@Pc(112) NativeBillboardFace nativeBillboardFace = this.billboardFaces[j];
				@Pc(117) NativeBillboardAttributes nativeBillboardAttributes = this.billboardAttributes[j];
				nativeBillboardAttributes.colour = nativeBillboardAttributes.colour & 0xFF000000 | ColorUtil.hslToRgb[this.facesColor[nativeBillboardFace.face] & 0xFFFF] & 0xFFFFFF;
			}
		}
		this.invalidateColours();
	}

	@OriginalMember(owner = "client!nca", name = "C", descriptor = "(I)V")
	@Override
	public void C(@OriginalArg(0) int ambient) {
		this.ambient = (short) ambient;
		this.invalidateColours();
	}

	@OriginalMember(owner = "client!nca", name = "l", descriptor = "(I)V")
	private void calculateBounds() {
		@Pc(7) int minVertexX = 32767;
		@Pc(9) int minVertexY = 32767;
		@Pc(11) int minVertexZ = 32767;
		@Pc(13) int maxVertexX = -32768;
		@Pc(15) int maxVertexY = -32768;
		@Pc(17) int maxVertexZ = -32768;
		@Pc(19) int maxSquaredRadius2d = 0;
		@Pc(21) int maxSquaredRadius3d = 0;
		for (@Pc(23) int i = 0; i < this.maxDepth; i++) {
			@Pc(30) int x = this.verticesX[i];
			@Pc(35) int y = this.verticesY[i];
			@Pc(40) int z = this.verticesZ[i];
			if (maxVertexX < x) {
				maxVertexX = x;
			}
			if (minVertexY > y) {
				minVertexY = y;
			}
			if (y > maxVertexY) {
				maxVertexY = y;
			}
			if (x < minVertexX) {
				minVertexX = x;
			}
			if (minVertexZ > z) {
				minVertexZ = z;
			}
			if (z > maxVertexZ) {
				maxVertexZ = z;
			}
			@Pc(82) int squaredDistance = z * z + x * x;
			if (squaredDistance > maxSquaredRadius2d) {
				maxSquaredRadius2d = squaredDistance;
			}
			squaredDistance = y * y + x * x + z * z;
			if (maxSquaredRadius3d < squaredDistance) {
				maxSquaredRadius3d = squaredDistance;
			}
		}
		this.minY = minVertexY;
		this.minZ = minVertexZ;
		this.minX = minVertexX;
		this.maxX = maxVertexX;
		this.maxY = maxVertexY;
		this.maxZ = maxVertexZ;
		this.size2d = (int) (Math.sqrt((double) maxSquaredRadius2d) + 0.99D);
		this.size3d = (int) (Math.sqrt((double) maxSquaredRadius3d) + 0.99D);
		this.boundsCalculated = true;
	}

	@OriginalMember(owner = "client!nca", name = "I", descriptor = "(I[IIIIZI[I)V")
	@Override
	public void I(@OriginalArg(0) int type, @OriginalArg(1) int[] labels, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int z, @OriginalArg(5) boolean lights, @OriginalArg(6) int submesh, @OriginalArg(7) int[] matrix) {
		@Pc(8) int length = labels.length;
		@Pc(29) int local29;
		@Pc(35) int local35;
		@Pc(59) int local59;
		@Pc(41) int local41;
		@Pc(51) int[] groupMembers;
		@Pc(53) int local53;
		if (type == 0) {
			x <<= 0x4;
			y <<= 0x4;
			z <<= 0x4;
			GpuModel.originY = 0;
			int vertexCount = 0;
			GpuModel.originZ = 0;
			GpuModel.originX = 0;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int group = labels[labelIndex];
				if (group < this.vertexGroups.length) {
					groupMembers = this.vertexGroups[group];
					for (int memberIndex = 0; memberIndex < groupMembers.length; memberIndex++) {
						int vertex = groupMembers[memberIndex];
						if (this.verticesSubmeshes == null || (this.verticesSubmeshes[vertex] & submesh) != 0) {
							GpuModel.originX += this.verticesX[vertex];
							GpuModel.originY += this.verticesY[vertex];
							GpuModel.originZ += this.verticesZ[vertex];
							vertexCount++;
						}
					}
				}
			}
			if (vertexCount > 0) {
				GpuModel.originZ = GpuModel.originZ / vertexCount + z;
				GpuModel.withinOrigin = true;
				GpuModel.originY = GpuModel.originY / vertexCount + y;
				GpuModel.originX = x + GpuModel.originX / vertexCount;
			} else {
				GpuModel.originZ = z;
				GpuModel.originX = x;
				GpuModel.originY = y;
			}
			return;
		}
		@Pc(244) int[] members;
		@Pc(246) int local246;
		if (type == 1) {
			if (matrix != null) {
				int rotatedX = matrix[0] * x + matrix[1] * y + z * matrix[2] + 8192 >> 14;
				int rotatedY = z * matrix[5] + matrix[3] * x + matrix[4] * y + 8192 >> 14;
				int rotatedZ = matrix[8] * z + matrix[6] * x + y * matrix[7] + 8192 >> 14;
				z = rotatedZ;
				y = rotatedY;
				x = rotatedX;
			}
			x <<= 0x4;
			z <<= 0x4;
			y <<= 0x4;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int group = labels[labelIndex];
				if (this.vertexGroups.length > group) {
					members = this.vertexGroups[group];
					for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
						int vertex = members[memberIndex];
						if (this.verticesSubmeshes == null || (this.verticesSubmeshes[vertex] & submesh) != 0) {
							this.verticesX[vertex] += x;
							this.verticesY[vertex] += y;
							this.verticesZ[vertex] += z;
						}
					}
				}
			}
			return;
		}
		@Pc(367) int local367;
		@Pc(390) int local390;
		@Pc(416) int local416;
		@Pc(447) int local447;
		@Pc(451) int local451;
		@Pc(455) int local455;
		@Pc(459) int local459;
		@Pc(467) int local467;
		@Pc(475) int local475;
		@Pc(630) int local630;
		@Pc(655) int local655;
		@Pc(659) int local659;
		@Pc(668) int local668;
		@Pc(673) int local673;
		@Pc(677) int local677;
		@Pc(681) int local681;
		@Pc(683) int local683;
		@Pc(815) int[] local815;
		@Pc(817) int local817;
		@Pc(821) int local821;
		@Pc(825) int local825;
		@Pc(827) int local827;
		@Pc(954) int local954;
		if (type == 2) {
			if (matrix == null) {
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					int group = labels[labelIndex];
					if (this.vertexGroups.length > group) {
						members = this.vertexGroups[group];
						for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
							int vertex = members[memberIndex];
							if (this.verticesSubmeshes == null || (this.verticesSubmeshes[vertex] & submesh) != 0) {
								this.verticesX[vertex] -= GpuModel.originX;
								this.verticesY[vertex] -= GpuModel.originY;
								this.verticesZ[vertex] -= GpuModel.originZ;
								if (z != 0) {
									int sin = TextureCombineSource.SINE[z];
									int cos = TextureCombineSource.COSINE[z];
									int rotated = sin * this.verticesY[vertex] + cos * this.verticesX[vertex] + 16383 >> 14;
									this.verticesY[vertex] = this.verticesY[vertex] * cos + 16383 - this.verticesX[vertex] * sin >> 14;
									this.verticesX[vertex] = rotated;
								}
								if (x != 0) {
									int sin = TextureCombineSource.SINE[x];
									int cos = TextureCombineSource.COSINE[x];
									int rotated = cos * this.verticesY[vertex] + 16383 - sin * this.verticesZ[vertex] >> 14;
									this.verticesZ[vertex] = cos * this.verticesZ[vertex] + sin * this.verticesY[vertex] + 16383 >> 14;
									this.verticesY[vertex] = rotated;
								}
								if (y != 0) {
									int sin = TextureCombineSource.SINE[y];
									int cos = TextureCombineSource.COSINE[y];
									int rotated = this.verticesZ[vertex] * sin + cos * this.verticesX[vertex] + 16383 >> 14;
									this.verticesZ[vertex] = cos * this.verticesZ[vertex] + 16383 - sin * this.verticesX[vertex] >> 14;
									this.verticesX[vertex] = rotated;
								}
								this.verticesX[vertex] += GpuModel.originX;
								this.verticesY[vertex] += GpuModel.originY;
								this.verticesZ[vertex] += GpuModel.originZ;
							}
						}
					}
				}
				if (lights) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int group = labels[labelIndex];
						if (this.vertexGroups.length > group) {
							groupMembers = this.vertexGroups[group];
							for (int memberIndex = 0; memberIndex < groupMembers.length; memberIndex++) {
								int vertex = groupMembers[memberIndex];
								if (this.verticesSubmeshes == null || (this.verticesSubmeshes[vertex] & submesh) != 0) {
									int start = this.triangles[vertex];
									int end = this.triangles[vertex + 1];
									for (int index = start; index < end; index++) {
										int normalIndex = this.vertexNormalIndices[index] - 1;
										if (normalIndex == -1) {
											break;
										}
										if (z != 0) {
											int sin = TextureCombineSource.SINE[z];
											int cos = TextureCombineSource.COSINE[z];
											int rotated = cos * this.normalsX[normalIndex] + this.normalsY[normalIndex] * sin + 16383 >> 14;
											this.normalsY[normalIndex] = (short) (cos * this.normalsY[normalIndex] + 16383 - this.normalsX[normalIndex] * sin >> 14);
											this.normalsX[normalIndex] = (short) rotated;
										}
										if (x != 0) {
											int sin = TextureCombineSource.SINE[x];
											int cos = TextureCombineSource.COSINE[x];
											int rotated = this.normalsY[normalIndex] * cos + 16383 - this.normalsZ[normalIndex] * sin >> 14;
											this.normalsZ[normalIndex] = (short) (sin * this.normalsY[normalIndex] + cos * this.normalsZ[normalIndex] + 16383 >> 14);
											this.normalsY[normalIndex] = (short) rotated;
										}
										if (y != 0) {
											int sin = TextureCombineSource.SINE[y];
											int cos = TextureCombineSource.COSINE[y];
											int rotated = this.normalsZ[normalIndex] * sin + cos * this.normalsX[normalIndex] + 16383 >> 14;
											this.normalsZ[normalIndex] = (short) (cos * this.normalsZ[normalIndex] + 16383 - sin * this.normalsX[normalIndex] >> 14);
											this.normalsX[normalIndex] = (short) rotated;
										}
									}
								}
							}
						}
					}
					this.invalidateLighting();
				}
			} else {
				int matrixTranslateX = matrix[9] << 4;
				int matrixTranslateY = matrix[10] << 4;
				int matrixTranslateZ = matrix[11] << 4;
				int originOffsetX = matrix[12] << 4;
				int originOffsetY = matrix[13] << 4;
				int originOffsetZ = matrix[14] << 4;
				if (GpuModel.withinOrigin) {
					int transformedOriginX = matrix[6] * GpuModel.originZ + GpuModel.originX * matrix[0] + GpuModel.originY * matrix[3] + 8192 >> 14;
					int transformedOriginY = matrix[7] * GpuModel.originZ + matrix[1] * GpuModel.originX + matrix[4] * GpuModel.originY + 8192 >> 14;
					transformedOriginX += originOffsetX;
					int transformedOriginZ = GpuModel.originZ * matrix[8] + GpuModel.originX * matrix[2] + GpuModel.originY * matrix[5] + 8192 >> 14;
					transformedOriginY += originOffsetY;
					transformedOriginZ += originOffsetZ;
					GpuModel.originX = transformedOriginX;
					GpuModel.originY = transformedOriginY;
					GpuModel.withinOrigin = false;
					GpuModel.originZ = transformedOriginZ;
				}
				@Pc(435) int[] rotationMatrix = new int[9];
				int cosX = TextureCombineSource.COSINE[x];
				int sinX = TextureCombineSource.SINE[x];
				int cosY = TextureCombineSource.COSINE[y];
				int sinY = TextureCombineSource.SINE[y];
				int cosZ = TextureCombineSource.COSINE[z];
				int sinZ = TextureCombineSource.SINE[z];
				int sinXCosZ = sinX * cosZ + 8192 >> 14;
				int sinXSinZ = sinX * sinZ + 8192 >> 14;
				rotationMatrix[5] = -sinX;
				rotationMatrix[4] = cosX * cosZ + 8192 >> 14;
				rotationMatrix[1] = -cosY * sinZ + sinY * sinXCosZ + 8192 >> 14;
				rotationMatrix[6] = -sinY * cosZ + sinXSinZ * cosY + 8192 >> 14;
				rotationMatrix[7] = sinZ * sinY + cosY * sinXCosZ + 8192 >> 14;
				rotationMatrix[0] = sinXSinZ * sinY + cosZ * cosY + 8192 >> 14;
				rotationMatrix[2] = cosX * sinY + 8192 >> 14;
				rotationMatrix[8] = cosY * cosX + 8192 >> 14;
				rotationMatrix[3] = sinZ * cosX + 8192 >> 14;
				@Pc(604) int rotatedPivotX = -GpuModel.originZ * rotationMatrix[2] + rotationMatrix[1] * -GpuModel.originY + rotationMatrix[0] * -GpuModel.originX + 8192 >> 14;
				int rotatedPivotY = -GpuModel.originZ * rotationMatrix[5] + -GpuModel.originX * rotationMatrix[3] + rotationMatrix[4] * -GpuModel.originY + 8192 >> 14;
				int rotatedPivotZ = -GpuModel.originZ * rotationMatrix[8] + -GpuModel.originX * rotationMatrix[6] + -GpuModel.originY * rotationMatrix[7] + 8192 >> 14;
				int pivotTranslateX = GpuModel.originX + rotatedPivotX;
				@Pc(663) int pivotTranslateY = rotatedPivotY + GpuModel.originY;
				int pivotTranslateZ = rotatedPivotZ + GpuModel.originZ;
				@Pc(671) int[] combinedMatrix = new int[9];
				for (int row = 0; row < 3; row++) {
					for (int column = 0; column < 3; column++) {
						int sum = 0;
						for (int k = 0; k < 3; k++) {
							sum += matrix[column * 3 + k] * rotationMatrix[row * 3 + k];
						}
						combinedMatrix[row * 3 + column] = sum + 8192 >> 14;
					}
				}
				int translateX = rotationMatrix[2] * originOffsetZ + rotationMatrix[0] * originOffsetX + originOffsetY * rotationMatrix[1] + 8192 >> 14;
				int translateY = rotationMatrix[4] * originOffsetY + rotationMatrix[3] * originOffsetX + originOffsetZ * rotationMatrix[5] + 8192 >> 14;
				translateX += pivotTranslateX;
				int translateZ = originOffsetZ * rotationMatrix[8] + rotationMatrix[6] * originOffsetX + rotationMatrix[7] * originOffsetY + 8192 >> 14;
				translateY += pivotTranslateY;
				translateZ += pivotTranslateZ;
				int[] transformMatrix = new int[9];
				for (int row = 0; row < 3; row++) {
					for (int column = 0; column < 3; column++) {
						int sum = 0;
						for (int k = 0; k < 3; k++) {
							sum += matrix[k + row * 3] * combinedMatrix[k * 3 + column];
						}
						transformMatrix[row * 3 + column] = sum + 8192 >> 14;
					}
				}
				int finalTranslateX = translateY * matrix[1] + matrix[0] * translateX + translateZ * matrix[2] + 8192 >> 14;
				int finalTranslateY = matrix[5] * translateZ + matrix[4] * translateY + translateX * matrix[3] + 8192 >> 14;
				int finalTranslateZ = matrix[7] * translateY + matrix[6] * translateX + translateZ * matrix[8] + 8192 >> 14;
				finalTranslateX += matrixTranslateX;
				finalTranslateY += matrixTranslateY;
				finalTranslateZ += matrixTranslateZ;
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					@Pc(960) int group = labels[labelIndex];
					if (group < this.vertexGroups.length) {
						@Pc(970) int[] vertexGroup = this.vertexGroups[group];
						for (@Pc(972) int i = 0; i < vertexGroup.length; i++) {
							@Pc(978) int vertex = vertexGroup[i];
							if (this.verticesSubmeshes == null || (submesh & this.verticesSubmeshes[vertex]) != 0) {
								@Pc(1023) int rotatedX = this.verticesY[vertex] * transformMatrix[1] + this.verticesX[vertex] * transformMatrix[0] + transformMatrix[2] * this.verticesZ[vertex] + 8192 >> 14;
								@Pc(1054) int rotatedY = transformMatrix[3] * this.verticesX[vertex] + transformMatrix[4] * this.verticesY[vertex] + this.verticesZ[vertex] * transformMatrix[5] + 8192 >> 14;
								@Pc(1058) int newX = rotatedX + finalTranslateX;
								@Pc(1089) int rotatedZ = transformMatrix[6] * this.verticesX[vertex] + this.verticesY[vertex] * transformMatrix[7] + this.verticesZ[vertex] * transformMatrix[8] + 8192 >> 14;
								@Pc(1093) int newY = rotatedY + finalTranslateY;
								@Pc(1097) int newZ = rotatedZ + finalTranslateZ;
								this.verticesX[vertex] = newX;
								this.verticesY[vertex] = newY;
								this.verticesZ[vertex] = newZ;
							}
						}
					}
				}
			}
		} else if (type != 3) {
			@Pc(2573) NativeBillboardAttributes nativeBillboardAttributes;
			@Pc(2457) boolean modified;
			@Pc(2568) NativeBillboardFace nativeBillboardFace;
			if (type == 5) {
				if (this.faceGroups != null) {
					modified = false;
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int group = labels[labelIndex];
						if (this.faceGroups.length > group) {
							groupMembers = this.faceGroups[group];
							for (int memberIndex = 0; memberIndex < groupMembers.length; memberIndex++) {
								int face = groupMembers[memberIndex];
								if (this.facesSubmeshes == null || (this.facesSubmeshes[face] & submesh) != 0) {
									int alpha = (this.facesAlpha[face] & 0xFF) + (x * 8);
									if (alpha < 0) {
										alpha = 0;
									} else if (alpha > 255) {
										alpha = 255;
									}
									this.facesAlpha[face] = (byte) alpha;
								}
							}
							modified |= groupMembers.length > 0;
						}
					}
					if (modified) {
						if (this.billboardFaces != null) {
							for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
								nativeBillboardFace = this.billboardFaces[billboardIndex];
								nativeBillboardAttributes = this.billboardAttributes[billboardIndex];
								nativeBillboardAttributes.colour = 255 - (this.facesAlpha[nativeBillboardFace.face] & 0xFF) << 24 | nativeBillboardAttributes.colour & 0xFFFFFF;
							}
						}
						this.invalidateColours();
					}
				}
			} else if (type == 7) {
				if (this.faceGroups != null) {
					modified = false;
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int group = labels[labelIndex];
						if (group < this.faceGroups.length) {
							groupMembers = this.faceGroups[group];
							for (int memberIndex = 0; memberIndex < groupMembers.length; memberIndex++) {
								int face = groupMembers[memberIndex];
								if (this.facesSubmeshes == null || (this.facesSubmeshes[face] & submesh) != 0) {
									int color = this.facesColor[face] & 0xFFFF;
									int hue = color >> 10 & 0x3F;
									int saturation = color >> 7 & 0x7;
									saturation += y / 4;
									@Pc(2680) int newHue = x + hue & 0x3F;
									int lightness = color & 0x7F;
									if (saturation < 0) {
										saturation = 0;
									} else if (saturation > 7) {
										saturation = 7;
									}
									lightness += z;
									if (lightness < 0) {
										lightness = 0;
									} else if (lightness > 127) {
										lightness = 127;
									}
									this.facesColor[face] = (short) (saturation << 7 | newHue << 10 | lightness);
								}
							}
							modified |= groupMembers.length > 0;
						}
					}
					if (modified) {
						if (this.billboardFaces != null) {
							for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
								nativeBillboardFace = this.billboardFaces[billboardIndex];
								nativeBillboardAttributes = this.billboardAttributes[billboardIndex];
								nativeBillboardAttributes.colour = nativeBillboardAttributes.colour & 0xFF000000 | ColorUtil.hslToRgb[this.facesColor[nativeBillboardFace.face] & 0xFFFF] & 0xFFFFFF;
							}
						}
						this.invalidateColours();
					}
				}
			} else if (type == 8) {
				if (this.billboardGroups != null) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int group = labels[labelIndex];
						if (group < this.billboardGroups.length) {
							members = this.billboardGroups[group];
							for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
								nativeBillboardAttributes = this.billboardAttributes[members[memberIndex]];
								nativeBillboardAttributes.offsetX += x;
								nativeBillboardAttributes.offsetY += y;
							}
						}
					}
				}
			} else if (type == 10) {
				if (this.billboardGroups != null) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int group = labels[labelIndex];
						if (this.billboardGroups.length > group) {
							members = this.billboardGroups[group];
							for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
								nativeBillboardAttributes = this.billboardAttributes[members[memberIndex]];
								nativeBillboardAttributes.scaleY = y * nativeBillboardAttributes.scaleY >> 7;
								nativeBillboardAttributes.scaleX = x * nativeBillboardAttributes.scaleX >> 7;
							}
						}
					}
				}
			} else if (type == 9 && this.billboardGroups != null) {
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					int group = labels[labelIndex];
					if (this.billboardGroups.length > group) {
						members = this.billboardGroups[group];
						for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
							nativeBillboardAttributes = this.billboardAttributes[members[memberIndex]];
							nativeBillboardAttributes.angle = x + nativeBillboardAttributes.angle & 0x3FFF;
						}
					}
				}
			}
		} else if (matrix == null) {
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int group = labels[labelIndex];
				if (group < this.vertexGroups.length) {
					members = this.vertexGroups[group];
					for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
						int vertex = members[memberIndex];
						if (this.verticesSubmeshes == null || (submesh & this.verticesSubmeshes[vertex]) != 0) {
							this.verticesX[vertex] -= GpuModel.originX;
							this.verticesY[vertex] -= GpuModel.originY;
							this.verticesZ[vertex] -= GpuModel.originZ;
							this.verticesX[vertex] = x * this.verticesX[vertex] >> 7;
							this.verticesY[vertex] = this.verticesY[vertex] * y >> 7;
							this.verticesZ[vertex] = z * this.verticesZ[vertex] >> 7;
							this.verticesX[vertex] += GpuModel.originX;
							this.verticesY[vertex] += GpuModel.originY;
							this.verticesZ[vertex] += GpuModel.originZ;
						}
					}
				}
			}
		} else {
			int matrixTranslateX = matrix[9] << 4;
			int matrixTranslateY = matrix[10] << 4;
			int matrixTranslateZ = matrix[11] << 4;
			int originOffsetX = matrix[12] << 4;
			int originOffsetY = matrix[13] << 4;
			int originOffsetZ = matrix[14] << 4;
			if (GpuModel.withinOrigin) {
				int transformedOriginX = GpuModel.originZ * matrix[6] + GpuModel.originX * matrix[0] + matrix[3] * GpuModel.originY + 8192 >> 14;
				int transformedOriginY = GpuModel.originX * matrix[1] + GpuModel.originY * matrix[4] + matrix[7] * GpuModel.originZ + 8192 >> 14;
				transformedOriginX += originOffsetX;
				transformedOriginY += originOffsetY;
				int transformedOriginZ = matrix[2] * GpuModel.originX + matrix[5] * GpuModel.originY + GpuModel.originZ * matrix[8] + 8192 >> 14;
				transformedOriginZ += originOffsetZ;
				GpuModel.originX = transformedOriginX;
				GpuModel.originY = transformedOriginY;
				GpuModel.withinOrigin = false;
				GpuModel.originZ = transformedOriginZ;
			}
			int scaleX = x << 15 >> 7;
			int scaleY = y << 15 >> 7;
			int scaleZ = z << 15 >> 7;
			int scaledOriginX = -GpuModel.originX * scaleX + 8192 >> 14;
			int scaledOriginY = -GpuModel.originY * scaleY + 8192 >> 14;
			int scaledOriginZ = -GpuModel.originZ * scaleZ + 8192 >> 14;
			int pivotTranslateX = scaledOriginX + GpuModel.originX;
			int pivotTranslateY = scaledOriginY + GpuModel.originY;
			int pivotTranslateZ = GpuModel.originZ + scaledOriginZ;
			@Pc(1987) int[] scaleMatrix = new int[] { scaleX * matrix[0] + 8192 >> 14, matrix[3] * scaleX + 8192 >> 14, matrix[6] * scaleX + 8192 >> 14, scaleY * matrix[1] + 8192 >> 14, scaleY * matrix[4] + 8192 >> 14, scaleY * matrix[7] + 8192 >> 14, scaleZ * matrix[2] + 8192 >> 14, scaleZ * matrix[5] + 8192 >> 14, scaleZ * matrix[8] + 8192 >> 14 };
			int scaledOffsetX = originOffsetX * scaleX + 8192 >> 14;
			int scaledOffsetY = originOffsetY * scaleY + 8192 >> 14;
			@Pc(2115) int translateX = scaledOffsetX + pivotTranslateX;
			int scaledOffsetZ = scaleZ * originOffsetZ + 8192 >> 14;
			@Pc(2127) int translateY = scaledOffsetY + pivotTranslateY;
			@Pc(2131) int translateZ = scaledOffsetZ + pivotTranslateZ;
			@Pc(2134) int[] combinedMatrix = new int[9];
			@Pc(2140) int local2140;
			for (int row = 0; row < 3; row++) {
				for (int column = 0; column < 3; column++) {
					int sum = 0;
					for (int k = 0; k < 3; k++) {
						sum += scaleMatrix[column + k * 3] * matrix[k + row * 3];
					}
					combinedMatrix[row * 3 + column] = sum + 8192 >> 14;
				}
			}
			int finalTranslateX = translateY * matrix[1] + translateX * matrix[0] + matrix[2] * translateZ + 8192 >> 14;
			int finalTranslateY = translateZ * matrix[5] + matrix[3] * translateX + translateY * matrix[4] + 8192 >> 14;
			int finalTranslateZ = translateX * matrix[6] + translateY * matrix[7] + matrix[8] * translateZ + 8192 >> 14;
			finalTranslateY += matrixTranslateY;
			finalTranslateX += matrixTranslateX;
			finalTranslateZ += matrixTranslateZ;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int group = labels[labelIndex];
				if (group < this.vertexGroups.length) {
					int[] vertexGroup = this.vertexGroups[group];
					for (int groupVertexIndex = 0; groupVertexIndex < vertexGroup.length; groupVertexIndex++) {
						int vertex = vertexGroup[groupVertexIndex];
						if (this.verticesSubmeshes == null || (submesh & this.verticesSubmeshes[vertex]) != 0) {
							int rotatedX = combinedMatrix[1] * this.verticesY[vertex] + this.verticesX[vertex] * combinedMatrix[0] + this.verticesZ[vertex] * combinedMatrix[2] + 8192 >> 14;
							int rotatedY = combinedMatrix[5] * this.verticesZ[vertex] + combinedMatrix[4] * this.verticesY[vertex] + combinedMatrix[3] * this.verticesX[vertex] + 8192 >> 14;
							@Pc(2382) int newY = rotatedY + finalTranslateY;
							@Pc(2386) int newX = rotatedX + finalTranslateX;
							int rotatedZ = this.verticesY[vertex] * combinedMatrix[7] + this.verticesX[vertex] * combinedMatrix[6] + combinedMatrix[8] * this.verticesZ[vertex] + 8192 >> 14;
							this.verticesX[vertex] = newX;
							@Pc(2427) int newZ = rotatedZ + finalTranslateZ;
							this.verticesY[vertex] = newY;
							this.verticesZ[vertex] = newZ;
						}
					}
				}
			}
		}
	}
	@OriginalMember(owner = "client!ef", name = "a", descriptor = "F")
	public static float modelViewXD;

	@OriginalMember(owner = "client!fd", name = "Q", descriptor = "[J")
	public static long[] normalsUid;

	@OriginalMember(owner = "client!jfa", name = "G", descriptor = "F")
	public static float modelViewXA;
	@OriginalMember(owner = "client!bca", name = "a", descriptor = "[I")
	public static int[] projectedVerticesY = new int[1];

	@OriginalMember(owner = "client!jn", name = "y", descriptor = "F")
	public static float modelViewYA;

	@OriginalMember(owner = "client!ed", name = "T", descriptor = "[I")
	public static final int[] BOX_CORNERS_X = new int[8];

	@OriginalMember(owner = "client!paa", name = "Sb", descriptor = "Z")
	public static boolean withinOrigin;

	@OriginalMember(owner = "client!ha", name = "v", descriptor = "F")
	public static float modelViewZC;
	@OriginalMember(owner = "client!cd", name = "e", descriptor = "F")
	public static float modelViewYB;

	@OriginalMember(owner = "client!db", name = "S", descriptor = "[I")
	public static final int[] BOX_CORNERS_Y = new int[8];

	@OriginalMember(owner = "client!efa", name = "a", descriptor = "I")
	public static int originZ;

	@OriginalMember(owner = "client!ta", name = "b", descriptor = "F")
	public static float modelViewZD;

	@OriginalMember(owner = "client!ar", name = "G", descriptor = "F")
	public static float modelViewYD;

	@OriginalMember(owner = "client!nv", name = "i", descriptor = "I")
	public static int originX;
	@OriginalMember(owner = "client!lm", name = "C", descriptor = "F")
	public static float modelViewXC;

	@OriginalMember(owner = "client!hs", name = "n", descriptor = "I")
	public static int originY;

	@OriginalMember(owner = "client!rl", name = "h", descriptor = "F")
	public static float modelViewZA;

	@OriginalMember(owner = "client!fga", name = "c", descriptor = "[I")
	public static final int[] BOX_CORNERS_Z = new int[8];
	@OriginalMember(owner = "client!saa", name = "N", descriptor = "[I")
	public static int[] projectedVerticesX = new int[1];

	@OriginalMember(owner = "client!ql", name = "o", descriptor = "F")
	public static float modelViewYC;

	@OriginalMember(owner = "client!en", name = "a", descriptor = "F")
	public static float modelViewZB;

	@OriginalMember(owner = "client!vh", name = "c", descriptor = "[F")
	public static final float[] UV_RESULT = new float[2];
	@OriginalMember(owner = "client!mq", name = "eb", descriptor = "F")
	public static float modelViewXB;
}
