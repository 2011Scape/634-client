package com.jagex.game.runetek5.graphics.model;

import com.jagex.game.runetek5.graphics.texture.TextureTransformationMatrix;
import com.jagex.game.runetek5.VertexNormalGL;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.FaceNormalGL;
import com.jagex.game.runetek5.graphics.model.billboard.BillboardSurfaceGL;
import com.jagex.game.runetek5.graphics.model.billboard.BillboardType;
import com.jagex.game.runetek5.Matrix43;
import com.jagex.game.runetek5.Protocol;
import com.jagex.game.runetek5.load.ModelTransformOriginY;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.normal.MeshBillboard;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.entity.BoundingBoxPolygon;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import com.jagex.game.runetek5.js5.ModelTexCoordFlag;
import jaclib.memory.Stream;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.buffer.ArrayBufferGL;
import com.jagex.game.runetek5.buffer.ElementsBufferGL;
import com.jagex.game.runetek5.cache.node.lru.GlShadow;
import com.jagex.game.runetek5.graphics.model.buffer.ModelElementsBufferGL;
import com.jagex.game.runetek5.graphics.model.normal.ModelNormalsGL;
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer;
import com.jagex.game.runetek5.graphics.gl.GlBillboardFace;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import com.jagex.game.runetek5.utils.ColorUtil;
import com.jagex.game.runetek5.utils.ArrayUtils;
import com.jagex.game.runetek5.graphics.model.billboard.BillboardTypeList;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks;

@OriginalClass("client!paa")
public final class GlModel extends Model {

	@OriginalMember(owner = "client!paa", name = "p", descriptor = "[I")
	private int[] triangles;

	@OriginalMember(owner = "client!paa", name = "x", descriptor = "I")
	private int billboardCount;

	@OriginalMember(owner = "client!paa", name = "A", descriptor = "[[I")
	private int[][] vertexGroups;

	@OriginalMember(owner = "client!paa", name = "B", descriptor = "S")
	private short minX;

	@OriginalMember(owner = "client!paa", name = "C", descriptor = "[Lclient!eca;")
	private EffectiveVertex[] effectiveVertices;

	@OriginalMember(owner = "client!paa", name = "D", descriptor = "[B")
	private byte[] facesAlpha;

	@OriginalMember(owner = "client!paa", name = "E", descriptor = "[I")
	private int[] verticesX;

	@OriginalMember(owner = "client!paa", name = "F", descriptor = "S")
	private short ambient;

	@OriginalMember(owner = "client!paa", name = "N", descriptor = "[F")
	private float[] textureU;

	@OriginalMember(owner = "client!paa", name = "O", descriptor = "[[I")
	private int[][] faceGroups;

	@OriginalMember(owner = "client!paa", name = "P", descriptor = "Lclient!hca;")
	private ArrayBufferGL dynamicArrayBuffer;

	@OriginalMember(owner = "client!paa", name = "R", descriptor = "I")
	private int preferenceFlags;

	@OriginalMember(owner = "client!paa", name = "U", descriptor = "S")
	private short size3d;

	@OriginalMember(owner = "client!paa", name = "Z", descriptor = "[S")
	private short[] normalsX;

	@OriginalMember(owner = "client!paa", name = "bb", descriptor = "[S")
	private short[] facesC;

	@OriginalMember(owner = "client!paa", name = "gb", descriptor = "[F")
	private float[] textureV;

	@OriginalMember(owner = "client!paa", name = "hb", descriptor = "Lclient!oba;")
	private ModelNormalsGL mergedNormals;

	@OriginalMember(owner = "client!paa", name = "ib", descriptor = "[S")
	private short[] facesSubmeshes;

	@OriginalMember(owner = "client!paa", name = "lb", descriptor = "[S")
	private short[] normalsZ;

	@OriginalMember(owner = "client!paa", name = "ob", descriptor = "[S")
	private short[] facesB;

	@OriginalMember(owner = "client!paa", name = "pb", descriptor = "[S")
	private short[] facesColor;

	@OriginalMember(owner = "client!paa", name = "qb", descriptor = "B")
	private byte renderState;

	@OriginalMember(owner = "client!paa", name = "sb", descriptor = "[S")
	private short[] normalsY;

	@OriginalMember(owner = "client!paa", name = "vb", descriptor = "S")
	private short maxX;

	@OriginalMember(owner = "client!paa", name = "Ab", descriptor = "S")
	private short minY;

	@OriginalMember(owner = "client!paa", name = "Bb", descriptor = "[S")
	private short[] facesA;

	@OriginalMember(owner = "client!paa", name = "Cb", descriptor = "[I")
	private int[] verticesY;

	@OriginalMember(owner = "client!paa", name = "Db", descriptor = "[B")
	private byte[] magnitudes;

	@OriginalMember(owner = "client!paa", name = "Fb", descriptor = "[Lclient!iq;")
	private BillboardSurfaceGL[] billboardAttributes;

	@OriginalMember(owner = "client!paa", name = "Kb", descriptor = "[S")
	private short[] facesMaterial;

	@OriginalMember(owner = "client!paa", name = "Rb", descriptor = "[S")
	private short[] verticesSubmeshes;

	@OriginalMember(owner = "client!paa", name = "ac", descriptor = "S")
	private short minZ;

	@OriginalMember(owner = "client!paa", name = "ec", descriptor = "[Lclient!nea;")
	private GlBillboardFace[] billboardFaces;

	@OriginalMember(owner = "client!paa", name = "jc", descriptor = "[I")
	private int[] materialBatchOffsets;

	@OriginalMember(owner = "client!paa", name = "kc", descriptor = "[[I")
	private int[][] billboardGroups;

	@OriginalMember(owner = "client!paa", name = "mc", descriptor = "S")
	private short maxY;

	@OriginalMember(owner = "client!paa", name = "rc", descriptor = "S")
	private short size2d;

	@OriginalMember(owner = "client!paa", name = "xc", descriptor = "[Lclient!nn;")
	private EmissiveTriangle[] emissiveTriangles;

	@OriginalMember(owner = "client!paa", name = "Bc", descriptor = "[I")
	private int[] verticesZ;

	@OriginalMember(owner = "client!paa", name = "Cc", descriptor = "S")
	private short contrast;

	@OriginalMember(owner = "client!paa", name = "Dc", descriptor = "S")
	private short maxZ;

	@OriginalMember(owner = "client!paa", name = "Ec", descriptor = "Lclient!ni;")
	private ElementsBufferGL dynamicElementsBuffer;

	@OriginalMember(owner = "client!paa", name = "Fc", descriptor = "I")
	private int functionMask;

	@OriginalMember(owner = "client!paa", name = "Gc", descriptor = "[S")
	private short[] vertexNormalIndices;

	@OriginalMember(owner = "client!paa", name = "y", descriptor = "I")
	private int renderedFaceCount = 0;

	@OriginalMember(owner = "client!paa", name = "Q", descriptor = "Z")
	private boolean updateRequired = true;

	@OriginalMember(owner = "client!paa", name = "Eb", descriptor = "Z")
	private boolean hasAlphaTransition = false;

	@OriginalMember(owner = "client!paa", name = "o", descriptor = "Z")
	private boolean boundsCalculated = false;

	@OriginalMember(owner = "client!paa", name = "q", descriptor = "I")
	private int numNormals = 0;

	@OriginalMember(owner = "client!paa", name = "pc", descriptor = "I")
	private int numFaces = 0;

	@OriginalMember(owner = "client!paa", name = "S", descriptor = "I")
	private int maxDepth = 0;

	@OriginalMember(owner = "client!paa", name = "zc", descriptor = "I")
	private int numVertices = 0;

	@OriginalMember(owner = "client!paa", name = "vc", descriptor = "Z")
	private boolean textureAnimating = false;

	@OriginalMember(owner = "client!paa", name = "Hb", descriptor = "Lclient!qo;")
	private final GlToolkit glToolkit;

	@OriginalMember(owner = "client!paa", name = "Ob", descriptor = "Lclient!nh;")
	private OpenGlArrayDrawBuffer vertexBuffer;

	@OriginalMember(owner = "client!paa", name = "dc", descriptor = "Lclient!nh;")
	private OpenGlArrayDrawBuffer textureCoordinatesBuffer;

	@OriginalMember(owner = "client!paa", name = "Y", descriptor = "Lclient!nh;")
	private OpenGlArrayDrawBuffer normalsBuffer;

	@OriginalMember(owner = "client!paa", name = "Tb", descriptor = "Lclient!nh;")
	private OpenGlArrayDrawBuffer colorsBuffer;

	@OriginalMember(owner = "client!paa", name = "kb", descriptor = "Lclient!us;")
	private ModelElementsBufferGL modelElementsBuffer;

	@OriginalMember(owner = "client!paa", name = "<init>", descriptor = "(Lclient!qo;)V")
	public GlModel(@OriginalArg(0) GlToolkit glToolkit) {
		this.glToolkit = glToolkit;
		this.vertexBuffer = new OpenGlArrayDrawBuffer(null, 5126, 3, 0);
		this.textureCoordinatesBuffer = new OpenGlArrayDrawBuffer(null, 5126, 2, 0);
		this.normalsBuffer = new OpenGlArrayDrawBuffer(null, 5126, 3, 0);
		this.colorsBuffer = new OpenGlArrayDrawBuffer(null, 5121, 4, 0);
		this.modelElementsBuffer = new ModelElementsBufferGL();
	}

	@OriginalMember(owner = "client!paa", name = "<init>", descriptor = "(Lclient!qo;Lclient!bh;IIII)V")
	public GlModel(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) Mesh mesh, @OriginalArg(2) int functionMask, @OriginalArg(3) int ambient, @OriginalArg(4) int contrast, @OriginalArg(5) int preferenceFlags) {
		this.preferenceFlags = preferenceFlags;
		this.functionMask = functionMask;
		this.glToolkit = glToolkit;
		if (GlModel.hasVertices(functionMask, preferenceFlags)) {
			this.vertexBuffer = new OpenGlArrayDrawBuffer(null, 5126, 3, 0);
		}
		if (ModelTexCoordFlag.hasTextureCoordinates(preferenceFlags, functionMask)) {
			this.textureCoordinatesBuffer = new OpenGlArrayDrawBuffer(null, 5126, 2, 0);
		}
		if (GlModel.hasNormals(preferenceFlags, functionMask)) {
			this.normalsBuffer = new OpenGlArrayDrawBuffer(null, 5126, 3, 0);
		}
		if (GlModel.hasColors(preferenceFlags, functionMask)) {
			this.colorsBuffer = new OpenGlArrayDrawBuffer(null, 5121, 4, 0);
		}
		if (GlModel.hasElementsBuffer(functionMask, preferenceFlags)) {
			this.modelElementsBuffer = new ModelElementsBufferGL();
		}
		@Pc(108) MaterialProvider textureSource = glToolkit.materialProvider;
		this.triangles = new int[mesh.maxDepth + 1];
		@Pc(119) int[] faces = new int[mesh.numFaces];
		for (@Pc(121) int i = 0; i < mesh.numFaces; i++) {
			if (mesh.faceRenderType == null || mesh.faceRenderType[i] != 2) {
				if (mesh.facesMaterial != null && mesh.facesMaterial[i] != -1) {
					@Pc(151) MaterialType textureMetrics = textureSource.getMetrics(mesh.facesMaterial[i] & 0xFFFF);
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
		@Pc(227) long[] sortKeys = new long[this.numFaces];
		@Pc(236) boolean modifiesAlpha = (this.functionMask & 0x100) != 0;
		@Pc(248) int local248;
		@Pc(261) int local261;
		@Pc(436) int local436;
		for (@Pc(238) int j = 0; j < this.numFaces; j++) {
			@Pc(244) int face = faces[j];
			@Pc(246) MaterialType textureMetrics2 = null;
			int sortKeyHigh = 0;
			@Pc(250) byte renderFlags = 0;
			@Pc(252) byte effectType = 0;
			@Pc(254) byte effectParam1 = 0;
			if (mesh.billboards != null) {
				@Pc(259) boolean hasUid = false;
				for (int billboardIndex = 0; billboardIndex < mesh.billboards.length; billboardIndex++) {
					@Pc(268) MeshBillboard meshBillboard = mesh.billboards[billboardIndex];
					if (face == meshBillboard.appliesOnFace) {
						@Pc(277) BillboardType lightType = BillboardTypeList.list(meshBillboard.billboardId);
						if (lightType.hasUid) {
							hasUid = true;
						}
						if (lightType.materialId != -1) {
							@Pc(292) MaterialType textureMetrics3 = textureSource.getMetrics(lightType.materialId);
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
			@Pc(323) short materialId = -1;
			if (mesh.facesMaterial != null) {
				materialId = mesh.facesMaterial[face];
				if (materialId != -1) {
					textureMetrics2 = textureSource.getMetrics(materialId & 0xFFFF);
					if ((this.preferenceFlags & 0x40) != 0 && textureMetrics2.detailsOnly) {
						materialId = -1;
						textureMetrics2 = null;
					} else {
						effectParam1 = textureMetrics2.effectParam1;
						if (textureMetrics2.speedU != 0 || textureMetrics2.speedV != 0) {
							this.textureAnimating = true;
						}
						effectType = textureMetrics2.effectType;
					}
				}
			}
			@Pc(392) boolean alphaTransition = mesh.facesAlpha != null && mesh.facesAlpha[face] != 0 || textureMetrics2 != null && textureMetrics2.effectCombiner != 0;
			if ((modifiesAlpha || alphaTransition) && mesh.facesLayer != null) {
				sortKeyHigh += mesh.facesLayer[face] << 17;
			}
			if (alphaTransition) {
				sortKeyHigh += 65536;
			}
			sortKeyHigh += (effectType & 0xFF) << 8;
			sortKeyHigh += effectParam1 & 0xFF;
			int materialKey = renderFlags + ((materialId & 0xFFFF) << 16);
			@Pc(442) int sortKeyLow = materialKey + (j & 0xFFFF);
			sortKeys[j] = ((long) sortKeyHigh << 32) + ((long) sortKeyLow);
			this.textureAnimating |= textureMetrics2 != null && (textureMetrics2.speedU != 0 || textureMetrics2.speedV != 0);
			this.hasAlphaTransition |= alphaTransition;
		}
		ArrayUtils.quicksort(faces, sortKeys);
		this.verticesZ = mesh.verticesZ;
		this.verticesY = mesh.verticesY;
		this.numVertices = mesh.numVertices;
		this.verticesX = mesh.verticesX;
		this.verticesSubmeshes = mesh.verticesSubmeshes;
		this.maxDepth = mesh.maxDepth;
		this.effectiveVertices = mesh.particleEffectors;
		@Pc(519) VertexNormalGL[] vertexNormals = new VertexNormalGL[this.maxDepth];
		this.emissiveTriangles = mesh.particleEmitters;
		@Pc(543) int local543;
		@Pc(557) int local557;
		@Pc(603) int local603;
		if (mesh.billboards != null) {
			this.billboardCount = mesh.billboards.length;
			this.billboardAttributes = new BillboardSurfaceGL[this.billboardCount];
			this.billboardFaces = new GlBillboardFace[this.billboardCount];
			for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
				@Pc(550) MeshBillboard meshBillboard2 = mesh.billboards[billboardIndex];
				@Pc(555) BillboardType lightType2 = BillboardTypeList.list(meshBillboard2.billboardId);
				int sortedFaceIndex = -1;
				for (@Pc(559) int k = 0; k < this.numFaces; k++) {
					if (faces[k] == meshBillboard2.appliesOnFace) {
						sortedFaceIndex = k;
						break;
					}
				}
				if (sortedFaceIndex == -1) {
					throw new RuntimeException();
				}
				int faceRgb = ColorUtil.hslToRgb[mesh.facesColor[meshBillboard2.appliesOnFace] & 0xFFFF] & 0xFFFFFF;
				@Pc(621) int diffuseColor = faceRgb | 255 - (mesh.facesAlpha == null ? 0 : mesh.facesAlpha[meshBillboard2.appliesOnFace]) << 24;
				this.billboardFaces[billboardIndex] = new GlBillboardFace(sortedFaceIndex, mesh.facesA[meshBillboard2.appliesOnFace], mesh.facesB[meshBillboard2.appliesOnFace], mesh.facesC[meshBillboard2.appliesOnFace], lightType2.size2d, lightType2.size3d, lightType2.materialId, lightType2.blendType, lightType2.shape, lightType2.hasUid, lightType2.stationary, meshBillboard2.distance);
				this.billboardAttributes[billboardIndex] = new BillboardSurfaceGL(diffuseColor);
			}
		}
		int faceVertexCount = this.numFaces * 3;
		this.vertexNormalIndices = new short[faceVertexCount];
		GlModel.normalsUid = new long[faceVertexCount];
		this.facesMaterial = new short[this.numFaces];
		this.textureV = new float[faceVertexCount];
		this.normalsX = new short[faceVertexCount];
		this.normalsZ = new short[faceVertexCount];
		this.magnitudes = new byte[faceVertexCount];
		this.normalsY = new short[faceVertexCount];
		if (mesh.facesSubmeshes != null) {
			this.facesSubmeshes = new short[this.numFaces];
		}
		this.ambient = (short) ambient;
		this.facesColor = new short[this.numFaces];
		this.facesB = new short[this.numFaces];
		this.facesA = new short[this.numFaces];
		this.contrast = (short) contrast;
		this.facesAlpha = new byte[this.numFaces];
		this.textureU = new float[faceVertexCount];
		this.facesC = new short[this.numFaces];
		int triangleOffset = 0;
		for (int vertexIndex = 0; vertexIndex < mesh.maxDepth; vertexIndex++) {
			int vertexFaceCount = this.triangles[vertexIndex];
			this.triangles[vertexIndex] = triangleOffset;
			vertexNormals[vertexIndex] = new VertexNormalGL();
			triangleOffset += vertexFaceCount;
		}
		this.triangles[mesh.maxDepth] = triangleOffset;
		@Pc(803) TextureTransformationMatrix textureMatrix = TextureTransformationMatrix.generateTextureMatrix(this.numFaces, mesh, faces);
		@Pc(807) FaceNormalGL[] faceNormals = new FaceNormalGL[mesh.numFaces];
		@Pc(847) int local847;
		@Pc(858) int local858;
		@Pc(880) int local880;
		@Pc(891) int local891;
		@Pc(900) int local900;
		@Pc(909) int local909;
		@Pc(826) short local826;
		@Pc(837) int local837;
		for (int meshFaceIndex = 0; meshFaceIndex < mesh.numFaces; meshFaceIndex++) {
			@Pc(816) short faceA = mesh.facesA[meshFaceIndex];
			@Pc(821) short faceB = mesh.facesB[meshFaceIndex];
			short faceC = mesh.facesC[meshFaceIndex];
			int deltaX1 = this.verticesX[faceB] - this.verticesX[faceA];
			int deltaY1 = this.verticesY[faceB] - this.verticesY[faceA];
			int deltaZ1 = this.verticesZ[faceB] - this.verticesZ[faceA];
			@Pc(869) int deltaX2 = this.verticesX[faceC] - this.verticesX[faceA];
			int deltaY2 = this.verticesY[faceC] - this.verticesY[faceA];
			int deltaZ2 = this.verticesZ[faceC] - this.verticesZ[faceA];
			int normalX = deltaY1 * deltaZ2 - deltaY2 * deltaZ1;
			int normalY = deltaX2 * deltaZ1 - deltaZ2 * deltaX1;
			@Pc(918) int normalZ = deltaY2 * deltaX1 - deltaY1 * deltaX2;
			while (normalX > 8192 || normalY > 8192 || normalZ > 8192 || normalX < -8192 || normalY < -8192 || normalZ < -8192) {
				normalX >>= 0x1;
				normalZ >>= 0x1;
				normalY >>= 0x1;
			}
			@Pc(969) int magnitude = (int) Math.sqrt((double) (normalZ * normalZ + normalY * normalY + normalX * normalX));
			if (magnitude <= 0) {
				magnitude = 1;
			}
			normalZ = normalZ * 256 / magnitude;
			normalX = normalX * 256 / magnitude;
			normalY = normalY * 256 / magnitude;
			@Pc(1004) byte renderType = mesh.faceRenderType == null ? 0 : mesh.faceRenderType[meshFaceIndex];
			if (renderType == 0) {
				@Pc(1010) VertexNormalGL vertexNormalA = vertexNormals[faceA];
				vertexNormalA.y += normalY;
				vertexNormalA.z += normalZ;
				vertexNormalA.magnitude++;
				vertexNormalA.x += normalX;
				@Pc(1038) VertexNormalGL vertexNormalB = vertexNormals[faceB];
				vertexNormalB.x += normalX;
				vertexNormalB.y += normalY;
				vertexNormalB.magnitude++;
				vertexNormalB.z += normalZ;
				@Pc(1066) VertexNormalGL vertexNormalC = vertexNormals[faceC];
				vertexNormalC.magnitude++;
				vertexNormalC.z += normalZ;
				vertexNormalC.y += normalY;
				vertexNormalC.x += normalX;
			} else if (renderType == 1) {
				@Pc(1103) FaceNormalGL faceNormal = faceNormals[meshFaceIndex] = new FaceNormalGL();
				faceNormal.z = normalZ;
				faceNormal.y = normalY;
				faceNormal.x = normalX;
			}
		}
		@Pc(1125) int local1125;
		@Pc(1168) short faceMaterialId;
		for (int faceIndex = 0; faceIndex < this.numFaces; faceIndex++) {
			int meshFace = faces[faceIndex];
			@Pc(1132) int color = mesh.facesColor[meshFace] & 0xFFFF;
			int faceTexture;
			if (mesh.facesTexture == null) {
				faceTexture = -1;
			} else {
				faceTexture = mesh.facesTexture[meshFace];
			}
			int faceAlpha;
			if (mesh.facesAlpha == null) {
				faceAlpha = 0;
			} else {
				faceAlpha = mesh.facesAlpha[meshFace] & 0xFF;
			}
			faceMaterialId = mesh.facesMaterial == null ? -1 : mesh.facesMaterial[meshFace];
			if (faceMaterialId != -1 && (this.preferenceFlags & 0x40) != 0) {
				@Pc(1187) MaterialType textureMetrics4 = textureSource.getMetrics(faceMaterialId & 0xFFFF);
				if (textureMetrics4.detailsOnly) {
					faceMaterialId = -1;
				}
			}
			@Pc(1194) float aU = 0.0F;
			@Pc(1196) float aV = 0.0F;
			@Pc(1198) float bU = 0.0F;
			@Pc(1200) float bV = 0.0F;
			@Pc(1202) float cU = 0.0F;
			@Pc(1204) float cV = 0.0F;
			@Pc(1206) byte uidOffsetB = 0;
			@Pc(1208) byte uidOffsetC = 0;
			@Pc(1210) int cubeFaceIndex = 0;
			@Pc(1245) byte local1245;
			@Pc(1265) short local1265;
			@Pc(1270) short local1270;
			@Pc(1275) short local1275;
			if (faceMaterialId != -1) {
				if (faceTexture == -1) {
					bV = 1.0F;
					uidOffsetC = 2;
					cV = 0.0F;
					uidOffsetB = 1;
					aV = 1.0F;
					aU = 0.0F;
					cU = 0.0F;
					bU = 1.0F;
				} else {
					faceTexture &= 0xFF;
					byte mappingType = mesh.texturesMapping[faceTexture];
					@Pc(1255) short vertexA;
					@Pc(1260) short vertexB;
					@Pc(1298) float local1298;
					@Pc(1306) float local1306;
					@Pc(1314) float local1314;
					@Pc(1401) float local1401;
					@Pc(1410) float local1410;
					@Pc(1419) float local1419;
					@Pc(1428) float local1428;
					@Pc(1437) float local1437;
					@Pc(1446) float local1446;
					if (mappingType == 0) {
						vertexA = mesh.facesA[meshFace];
						vertexB = mesh.facesB[meshFace];
						short vertexC = mesh.facesC[meshFace];
						short texVertexA = mesh.textureFaceX[faceTexture];
						short texVertexB = mesh.textureFaceY[faceTexture];
						@Pc(1280) short texVertexC = mesh.textureFaceZ[faceTexture];
						@Pc(1286) float texOriginX = (float) mesh.verticesX[texVertexA];
						@Pc(1292) float texOriginY = (float) mesh.verticesY[texVertexA];
						float texOriginZ = mesh.verticesZ[texVertexA];
						float texEdge1X = (float) mesh.verticesX[texVertexB] - texOriginX;
						float texEdge1Y = (float) mesh.verticesY[texVertexB] - texOriginY;
						@Pc(1323) float texEdge1Z = (float) mesh.verticesZ[texVertexB] - texOriginZ;
						@Pc(1331) float texEdge2X = (float) mesh.verticesX[texVertexC] - texOriginX;
						@Pc(1340) float texEdge2Y = (float) mesh.verticesY[texVertexC] - texOriginY;
						@Pc(1349) float texEdge2Z = (float) mesh.verticesZ[texVertexC] - texOriginZ;
						@Pc(1358) float vertexAOffX = (float) mesh.verticesX[vertexA] - texOriginX;
						@Pc(1366) float vertexAOffY = (float) mesh.verticesY[vertexA] - texOriginY;
						@Pc(1374) float vertexAOffZ = (float) mesh.verticesZ[vertexA] - texOriginZ;
						@Pc(1383) float vertexBOffX = (float) mesh.verticesX[vertexB] - texOriginX;
						@Pc(1392) float vertexBOffY = (float) mesh.verticesY[vertexB] - texOriginY;
						float vertexBOffZ = (float) mesh.verticesZ[vertexB] - texOriginZ;
						float vertexCOffX = (float) mesh.verticesX[vertexC] - texOriginX;
						float vertexCOffY = (float) mesh.verticesY[vertexC] - texOriginY;
						float vertexCOffZ = (float) mesh.verticesZ[vertexC] - texOriginZ;
						float texCrossX = texEdge1Y * texEdge2Z - texEdge1Z * texEdge2Y;
						float texCrossY = texEdge2X * texEdge1Z - texEdge2Z * texEdge1X;
						@Pc(1454) float texCrossZ = texEdge2Y * texEdge1X - texEdge2X * texEdge1Y;
						@Pc(1463) float uBasisX = texCrossZ * texEdge2Y - texEdge2Z * texCrossY;
						@Pc(1472) float uBasisY = texEdge2Z * texCrossX - texEdge2X * texCrossZ;
						@Pc(1480) float uBasisZ = texCrossY * texEdge2X - texEdge2Y * texCrossX;
						@Pc(1494) float uInvDeterminant = 1.0F / (texEdge1Z * uBasisZ + texEdge1Y * uBasisY + uBasisX * texEdge1X);
						cU = uInvDeterminant * (vertexCOffX * uBasisX + uBasisY * vertexCOffY + vertexCOffZ * uBasisZ);
						aU = uInvDeterminant * (uBasisX * vertexAOffX + uBasisY * vertexAOffY + uBasisZ * vertexAOffZ);
						bU = uInvDeterminant * (vertexBOffZ * uBasisZ + vertexBOffX * uBasisX + uBasisY * vertexBOffY);
						@Pc(1545) float vBasisZ = texEdge1X * texCrossY - texCrossX * texEdge1Y;
						@Pc(1553) float vBasisX = texEdge1Y * texCrossZ - texCrossY * texEdge1Z;
						@Pc(1561) float vBasisY = texEdge1Z * texCrossX - texCrossZ * texEdge1X;
						@Pc(1575) float vInvDeterminant = 1.0F / (texEdge2Z * vBasisZ + texEdge2X * vBasisX + vBasisY * texEdge2Y);
						bV = (vertexBOffX * vBasisX + vBasisY * vertexBOffY + vBasisZ * vertexBOffZ) * vInvDeterminant;
						aV = (vBasisZ * vertexAOffZ + vBasisY * vertexAOffY + vertexAOffX * vBasisX) * vInvDeterminant;
						cV = (vBasisY * vertexCOffY + vBasisX * vertexCOffX + vertexCOffZ * vBasisZ) * vInvDeterminant;
					} else {
						vertexA = mesh.facesA[meshFace];
						vertexB = mesh.facesB[meshFace];
						short vertexC = mesh.facesC[meshFace];
						@Pc(1639) int centerX = textureMatrix.centerX[faceTexture];
						@Pc(1644) int centerY = textureMatrix.centerY[faceTexture];
						@Pc(1649) int centerZ = textureMatrix.centerZ[faceTexture];
						@Pc(1654) float[] matrix = textureMatrix.matrix[faceTexture];
						@Pc(1659) byte direction = mesh.texturesDirection[faceTexture];
						float speedOffset = (float) mesh.texturesSpeed[faceTexture] / 256.0F;
						if (mappingType == 1) {
							float uScale = (float) mesh.texturesScaleZ[faceTexture] / 1024.0F;
							Model.mapCylindricalUv(centerZ, mesh.verticesZ[vertexA], direction, mesh.verticesX[vertexA], GlModel.textureCoordsOut, mesh.verticesY[vertexA], speedOffset, centerY, centerX, uScale, matrix);
							aU = GlModel.textureCoordsOut[0];
							aV = GlModel.textureCoordsOut[1];
							Model.mapCylindricalUv(centerZ, mesh.verticesZ[vertexB], direction, mesh.verticesX[vertexB], GlModel.textureCoordsOut, mesh.verticesY[vertexB], speedOffset, centerY, centerX, uScale, matrix);
							bU = GlModel.textureCoordsOut[0];
							bV = GlModel.textureCoordsOut[1];
							Model.mapCylindricalUv(centerZ, mesh.verticesZ[vertexC], direction, mesh.verticesX[vertexC], GlModel.textureCoordsOut, mesh.verticesY[vertexC], speedOffset, centerY, centerX, uScale, matrix);
							cU = GlModel.textureCoordsOut[0];
							cV = GlModel.textureCoordsOut[1];
							float halfUScale = uScale / 2.0F;
							if ((direction & 0x1) == 0) {
								if (halfUScale < cU - aU) {
									cU -= uScale;
									uidOffsetC = 1;
								} else if (halfUScale < aU - cU) {
									cU += uScale;
									uidOffsetC = 2;
								}
								if (bU - aU > halfUScale) {
									uidOffsetB = 1;
									bU -= uScale;
								} else if (aU - bU > halfUScale) {
									uidOffsetB = 2;
									bU += uScale;
								}
							} else {
								if (bV - aV > halfUScale) {
									bV -= uScale;
									uidOffsetB = 1;
								} else if (aV - bV > halfUScale) {
									uidOffsetB = 2;
									bV += uScale;
								}
								if (halfUScale < cV - aV) {
									uidOffsetC = 1;
									cV -= uScale;
								} else if (aV - cV > halfUScale) {
									uidOffsetC = 2;
									cV += uScale;
								}
							}
						} else if (mappingType == 2) {
							float uTrans = (float) mesh.texturesUtrans[faceTexture] / 256.0F;
							float vTrans = (float) mesh.texturesVtrans[faceTexture] / 256.0F;
							@Pc(1912) int edge1X = mesh.verticesX[vertexB] - mesh.verticesX[vertexA];
							@Pc(1923) int edge1Y = mesh.verticesY[vertexB] - mesh.verticesY[vertexA];
							@Pc(1934) int edge1Z = mesh.verticesZ[vertexB] - mesh.verticesZ[vertexA];
							@Pc(1944) int edge2X = mesh.verticesX[vertexC] - mesh.verticesX[vertexA];
							@Pc(1955) int edge2Y = mesh.verticesY[vertexC] - mesh.verticesY[vertexA];
							@Pc(1966) int edge2Z = mesh.verticesZ[vertexC] - mesh.verticesZ[vertexA];
							@Pc(1975) int crossX = edge1Y * edge2Z - edge1Z * edge2Y;
							@Pc(1984) int crossY = edge2X * edge1Z - edge2Z * edge1X;
							@Pc(1993) int crossZ = edge2Y * edge1X - edge2X * edge1Y;
							float inverseScaleX = 64.0F / (float) mesh.texturesScaleX[faceTexture];
							float inverseScaleY = 64.0F / (float) mesh.texturesScaleY[faceTexture];
							float inverseScaleZ = 64.0F / (float) mesh.texturesScaleZ[faceTexture];
							float scaledNormalX = (matrix[2] * (float) crossZ + (float) crossY * matrix[1] + (float) crossX * matrix[0]) / inverseScaleX;
							float scaledNormalY = (matrix[5] * (float) crossZ + (float) crossY * matrix[4] + matrix[3] * (float) crossX) / inverseScaleY;
							float scaledNormalZ = ((float) crossZ * matrix[8] + matrix[7] * (float) crossY + (float) crossX * matrix[6]) / inverseScaleZ;
							cubeFaceIndex = Model.getCubeFace(scaledNormalY, scaledNormalZ, scaledNormalX);
							Model.mapCubeUv(vTrans, speedOffset, matrix, mesh.verticesZ[vertexA], centerZ, direction, centerX, mesh.verticesX[vertexA], mesh.verticesY[vertexA], uTrans, GlModel.textureCoordsOut, centerY, cubeFaceIndex);
							aV = GlModel.textureCoordsOut[1];
							aU = GlModel.textureCoordsOut[0];
							Model.mapCubeUv(vTrans, speedOffset, matrix, mesh.verticesZ[vertexB], centerZ, direction, centerX, mesh.verticesX[vertexB], mesh.verticesY[vertexB], uTrans, GlModel.textureCoordsOut, centerY, cubeFaceIndex);
							bV = GlModel.textureCoordsOut[1];
							bU = GlModel.textureCoordsOut[0];
							Model.mapCubeUv(vTrans, speedOffset, matrix, mesh.verticesZ[vertexC], centerZ, direction, centerX, mesh.verticesX[vertexC], mesh.verticesY[vertexC], uTrans, GlModel.textureCoordsOut, centerY, cubeFaceIndex);
							cU = GlModel.textureCoordsOut[0];
							cV = GlModel.textureCoordsOut[1];
						} else if (mappingType == 3) {
							Model.mapSphericalUv(centerZ, direction, speedOffset, mesh.verticesX[vertexA], GlModel.textureCoordsOut, mesh.verticesZ[vertexA], centerX, mesh.verticesY[vertexA], centerY, matrix);
							aV = GlModel.textureCoordsOut[1];
							aU = GlModel.textureCoordsOut[0];
							Model.mapSphericalUv(centerZ, direction, speedOffset, mesh.verticesX[vertexB], GlModel.textureCoordsOut, mesh.verticesZ[vertexB], centerX, mesh.verticesY[vertexB], centerY, matrix);
							bU = GlModel.textureCoordsOut[0];
							bV = GlModel.textureCoordsOut[1];
							Model.mapSphericalUv(centerZ, direction, speedOffset, mesh.verticesX[vertexC], GlModel.textureCoordsOut, mesh.verticesZ[vertexC], centerX, mesh.verticesY[vertexC], centerY, matrix);
							cV = GlModel.textureCoordsOut[1];
							cU = GlModel.textureCoordsOut[0];
							if ((direction & 0x1) == 0) {
								if (cU - aU > 0.5F) {
									uidOffsetC = 1;
									cU--;
								} else if (aU - cU > 0.5F) {
									uidOffsetC = 2;
									cU++;
								}
								if (bU - aU > 0.5F) {
									uidOffsetB = 1;
									bU--;
								} else if (aU - bU > 0.5F) {
									uidOffsetB = 2;
									bU++;
								}
							} else {
								if (bV - aV > 0.5F) {
									uidOffsetB = 1;
									bV--;
								} else if (aV - bV > 0.5F) {
									bV++;
									uidOffsetB = 2;
								}
								if (cV - aV > 0.5F) {
									cV--;
									uidOffsetC = 1;
								} else if (aV - cV > 0.5F) {
									uidOffsetC = 2;
									cV++;
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
				faceRenderType = mesh.faceRenderType[meshFace];
			}
			if (faceRenderType == 0) {
				@Pc(2565) long uid = ((long) (color << 8) + (long) (cubeFaceIndex << 24) + (long) faceAlpha << 32) + (long) (faceTexture << 2);
				short vertexA = mesh.facesA[meshFace];
				short vertexB = mesh.facesB[meshFace];
				short vertexC = mesh.facesC[meshFace];
				@Pc(2584) VertexNormalGL vertexNormalA = vertexNormals[vertexA];
				this.facesA[faceIndex] = this.connectNormal(vertexNormalA.y, vertexNormalA.z, uid, vertexNormalA.x, mesh, vertexNormalA.magnitude, aU, vertexA, aV);
				@Pc(2608) VertexNormalGL vertexNormalB = vertexNormals[vertexB];
				this.facesB[faceIndex] = this.connectNormal(vertexNormalB.y, vertexNormalB.z, (long) uidOffsetB + uid, vertexNormalB.x, mesh, vertexNormalB.magnitude, bU, vertexB, bV);
				@Pc(2635) VertexNormalGL vertexNormalC = vertexNormals[vertexC];
				this.facesC[faceIndex] = this.connectNormal(vertexNormalC.y, vertexNormalC.z, uid + (long) uidOffsetC, vertexNormalC.x, mesh, vertexNormalC.magnitude, cU, vertexC, cV);
			} else if (faceRenderType == 1) {
				@Pc(2429) FaceNormalGL faceNormal = faceNormals[meshFace];
				@Pc(2471) long uid = ((long) (color << 8) + (long) (cubeFaceIndex << 24) + (long) faceAlpha << 32) + (long) ((faceNormal.x > 0 ? 1024 : 2048) + (faceTexture << 2) + (faceNormal.y + 256 << 12) + (faceNormal.z + 256 << 22));
				this.facesA[faceIndex] = this.connectNormal(faceNormal.y, faceNormal.z, uid, faceNormal.x, mesh, 0, aU, mesh.facesA[meshFace], aV);
				this.facesB[faceIndex] = this.connectNormal(faceNormal.y, faceNormal.z, uid + (long) uidOffsetB, faceNormal.x, mesh, 0, bU, mesh.facesB[meshFace], bV);
				this.facesC[faceIndex] = this.connectNormal(faceNormal.y, faceNormal.z, uid + (long) uidOffsetC, faceNormal.x, mesh, 0, cU, mesh.facesC[meshFace], cV);
			}
			if (mesh.facesAlpha != null) {
				this.facesAlpha[faceIndex] = mesh.facesAlpha[meshFace];
			}
			if (mesh.facesSubmeshes != null) {
				this.facesSubmeshes[faceIndex] = mesh.facesSubmeshes[meshFace];
			}
			this.facesColor[faceIndex] = mesh.facesColor[meshFace];
			this.facesMaterial[faceIndex] = faceMaterialId;
		}
		int batchCount = 0;
		short previousMaterial = -10000;
		for (int faceIndex = 0; faceIndex < this.renderedFaceCount; faceIndex++) {
			@Pc(2711) short material = this.facesMaterial[faceIndex];
			if (previousMaterial != material) {
				batchCount++;
				previousMaterial = material;
			}
		}
		this.materialBatchOffsets = new int[batchCount + 1];
		batchCount = 0;
		previousMaterial = -10000;
		for (int faceIndex = 0; faceIndex < this.renderedFaceCount; faceIndex++) {
			faceMaterialId = this.facesMaterial[faceIndex];
			if (previousMaterial != faceMaterialId) {
				this.materialBatchOffsets[batchCount++] = faceIndex;
				previousMaterial = faceMaterialId;
			}
		}
		this.materialBatchOffsets[batchCount] = this.renderedFaceCount;
		GlModel.normalsUid = null;
		this.normalsX = ArrayUtils.method9229(this.normalsX, this.numNormals);
		this.normalsY = ArrayUtils.method9229(this.normalsY, this.numNormals);
		this.normalsZ = ArrayUtils.method9229(this.normalsZ, this.numNormals);
		this.magnitudes = Arrays.copyOf(this.numNormals, this.magnitudes);
		this.textureU = ArrayUtils.method7862(this.numNormals, this.textureU);
		this.textureV = ArrayUtils.method7862(this.numNormals, this.textureV);
		if (mesh.vertexGroups != null && GlModel.altersVertices(this.preferenceFlags, functionMask)) {
			this.vertexGroups = mesh.createVertexGroups(false);
		}
		if (mesh.billboards != null && GlModel.altersBillboards(functionMask, this.preferenceFlags)) {
			this.billboardGroups = mesh.createBillboardGroups();
		}
		if (mesh.faceGroups != null && GlModel.altersFaces(functionMask, this.preferenceFlags)) {
			int maxFaceGroup = 0;
			@Pc(2860) int[] groupCounts = new int[256];
			for (int faceIndex = 0; faceIndex < this.numFaces; faceIndex++) {
				int faceGroup = mesh.faceGroups[faces[faceIndex]];
				if (faceGroup >= 0) {
					if (faceGroup > maxFaceGroup) {
						maxFaceGroup = faceGroup;
					}
					@Pc(2889) int previousCount = groupCounts[faceGroup]++;
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

	@OriginalMember(owner = "client!paa", name = "FA", descriptor = "(I)V")
	@Override
	public void FA(@OriginalArg(0) int angle) {
		@Pc(9) int sin = TextureCombineSource.SINE[angle];
		@Pc(13) int cos = TextureCombineSource.COSINE[angle];
		for (@Pc(15) int i = 0; i < this.maxDepth; i++) {
			@Pc(34) int rotatedY = this.verticesY[i] * cos - sin * this.verticesZ[i] >> 14;
			this.verticesZ[i] = this.verticesY[i] * sin + this.verticesZ[i] * cos >> 14;
			this.verticesY[i] = rotatedY;
		}
		if (this.vertexBuffer != null) {
			this.vertexBuffer.buffer = null;
		}
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(BIZ)Lclient!ka;")
	@Override
	public Model copy(@OriginalArg(0) byte copyType, @OriginalArg(1) int functionMask, @OriginalArg(2) boolean ensureLit) {
		@Pc(12) GlModel glModel;
		@Pc(16) GlModel glModel2;
		if (copyType == 1) {
			glModel = this.glToolkit.copyTarget1;
			glModel2 = this.glToolkit.copyBuffer1;
		} else if (copyType == 2) {
			glModel2 = this.glToolkit.copyBuffer2;
			glModel = this.glToolkit.copyTarget2;
		} else if (copyType == 3) {
			glModel = this.glToolkit.copyTarget3;
			glModel2 = this.glToolkit.copyBuffer3;
		} else if (copyType == 4) {
			glModel = this.glToolkit.copyTarget4;
			glModel2 = this.glToolkit.copyBuffer4;
		} else if (copyType == 5) {
			glModel = this.glToolkit.copyTarget5;
			glModel2 = this.glToolkit.copyBuffer5;
		} else {
			glModel = glModel2 = new GlModel(this.glToolkit);
		}
		return this.copyModelData(glModel, functionMask, ensureLit, glModel2, copyType != 0);
	}

	@OriginalMember(owner = "client!paa", name = "b", descriptor = "(B)V")
	private void drawOpaqueFaces() {
		if (this.renderedFaceCount == 0) {
			return;
		}
		if (this.renderState != 0) {
			this.upload(true);
		}
		this.upload(false);
		if (this.modelElementsBuffer != null) {
			if (this.modelElementsBuffer.openglElementsBuffer == null) {
				this.buildElementsBuffer((this.renderState & 0x10) != 0);
			}
			if (this.modelElementsBuffer.openglElementsBuffer != null) {
				this.glToolkit.setLightingEnabled(this.normalsBuffer != null);
				this.glToolkit.toggleDrawingClientstates(this.vertexBuffer, this.colorsBuffer, this.textureCoordinatesBuffer, this.normalsBuffer);
				@Pc(77) int batchCount = this.materialBatchOffsets.length - 1;
				for (@Pc(79) int i = 0; i < batchCount; i++) {
					@Pc(86) int start = this.materialBatchOffsets[i];
					@Pc(93) int end = this.materialBatchOffsets[i + 1];
					@Pc(100) int materialId = this.facesMaterial[start] & 0xFFFF;
					if (materialId == 65535) {
						materialId = -1;
					}
					this.glToolkit.renderTexture(this.normalsBuffer != null, materialId);
					this.glToolkit.drawElements((end - start) * 3, this.modelElementsBuffer.openglElementsBuffer, start * 3);
				}
			}
		}
		this.releaseUploadedData();
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(ZB)V")
	private void buildElementsBuffer(@OriginalArg(0) boolean reuseBuffer) {
		if (this.renderedFaceCount * 6 > this.glToolkit.textureBuffer.buffer.length) {
			this.glToolkit.textureBuffer = new GlBitBuffer((this.renderedFaceCount + 100) * 6);
		} else {
			this.glToolkit.textureBuffer.currentPosition = 0;
		}
		@Pc(44) GlBitBuffer gpuPacket = this.glToolkit.textureBuffer;
		@Pc(50) int face;
		if (this.glToolkit.bigEndianess) {
			for (face = 0; face < this.renderedFaceCount; face++) {
				gpuPacket.writeShort(this.facesA[face]);
				gpuPacket.writeShort(this.facesB[face]);
				gpuPacket.writeShort(this.facesC[face]);
			}
		} else {
			for (face = 0; face < this.renderedFaceCount; face++) {
				gpuPacket.writeShortLE(this.facesA[face]);
				gpuPacket.writeShortLE(this.facesB[face]);
				gpuPacket.writeShortLE(this.facesC[face]);
			}
		}
		if (gpuPacket.currentPosition == 0) {
			return;
		}
		if (reuseBuffer) {
			if (this.dynamicElementsBuffer == null) {
				this.dynamicElementsBuffer = this.glToolkit.createElementsArrayBuffer(gpuPacket.currentPosition, gpuPacket.buffer, true);
			} else {
				this.dynamicElementsBuffer.putElements(gpuPacket.buffer, gpuPacket.currentPosition);
			}
			this.modelElementsBuffer.openglElementsBuffer = this.dynamicElementsBuffer;
		} else {
			this.modelElementsBuffer.openglElementsBuffer = this.glToolkit.createElementsArrayBuffer(gpuPacket.currentPosition, gpuPacket.buffer, false);
		}
		if (!reuseBuffer) {
			this.updateRequired = true;
		}
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(IILclient!bca;ZI)Z")
	@Override
	public boolean picked(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) Matrix matrix, @OriginalArg(3) boolean quick, @OriginalArg(4) int sizeShift) {
		return this.pick(quick, -1, y, sizeShift, x, matrix);
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(IIIIIIIII)Z")
	private boolean isHovering(@OriginalArg(0) int mouseY, @OriginalArg(1) int xB, @OriginalArg(2) int xC, @OriginalArg(3) int yB, @OriginalArg(4) int xA, @OriginalArg(5) int yA, @OriginalArg(7) int mouseX, @OriginalArg(8) int yC) {
		if (yA > mouseY && mouseY < yB && mouseY < yC) {
			return false;
		} else if (mouseY > yA && yB < mouseY && yC < mouseY) {
			return false;
		} else if (xA > mouseX && xB > mouseX && mouseX < xC) {
			return false;
		} else {
			return xA >= mouseX || mouseX <= xB || mouseX <= xC;
		}
	}

	@OriginalMember(owner = "client!paa", name = "VA", descriptor = "(I)V")
	@Override
	public void VA(@OriginalArg(0) int angle) {
		@Pc(9) int sin = TextureCombineSource.SINE[angle];
		@Pc(13) int cos = TextureCombineSource.COSINE[angle];
		for (@Pc(15) int i = 0; i < this.maxDepth; i++) {
			@Pc(33) int rotatedX = this.verticesX[i] * cos + sin * this.verticesY[i] >> 14;
			this.verticesY[i] = this.verticesY[i] * cos - sin * this.verticesX[i] >> 14;
			this.verticesX[i] = rotatedX;
		}
		if (this.vertexBuffer != null) {
			this.vertexBuffer.buffer = null;
		}
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(IIBSI)I")
	private int hslToRgb(@OriginalArg(1) int ambient, @OriginalArg(2) byte alpha, @OriginalArg(3) short materialId, @OriginalArg(4) int color) {
		@Pc(19) int rgb = ColorUtil.hslToRgb[GlModel.adjustLuminance(color, ambient)];
		if (materialId != -1) {
			@Pc(32) MaterialType textureMetrics = this.glToolkit.materialProvider.getMetrics(materialId & 0xFFFF);
			@Pc(37) int materialAlpha = textureMetrics.alpha & 0xFF;
			@Pc(46) int local46;
			@Pc(68) int local68;
			if (materialAlpha != 0) {
				int ambientColour;
				if (ambient < 0) {
					ambientColour = 0;
				} else if (ambient > 127) {
					ambientColour = 16777215;
				} else {
					ambientColour = ambient * 131586;
				}
				if (materialAlpha == 256) {
					rgb = ambientColour;
				} else {
					int inverseAlpha = 256 - materialAlpha;
					rgb = ((ambientColour & 0xFF00FF) * materialAlpha + inverseAlpha * (rgb & 0xFF00FF) & 0xFF00FF00) + ((ambientColour & 0xFF00) * materialAlpha + (rgb & 0xFF00) * inverseAlpha & 0xFF0000) >> 8;
				}
			}
			int brightness = textureMetrics.brightness & 0xFF;
			if (brightness != 0) {
				brightness += 256;
				@Pc(123) int red = (rgb >> 16 & 0xFF) * brightness;
				if (red > 65535) {
					red = 65535;
				}
				int green = brightness * (rgb >> 8 & 0xFF);
				if (green > 65535) {
					green = 65535;
				}
				@Pc(147) int blue = (rgb & 0xFF) * brightness;
				if (blue > 65535) {
					blue = 65535;
				}
				rgb = (blue >> 8) + (((red & 0xA400FF00) << 8) + (green & 0xFF00));
			}
		}
		return (rgb << 8) + 255 - (alpha & 0xFF);
	}

	@OriginalMember(owner = "client!paa", name = "c", descriptor = "()Z")
	@Override
	public boolean loadedTextures() {
		if (this.facesMaterial == null) {
			return true;
		}
		for (@Pc(12) int i = 0; i < this.facesMaterial.length; i++) {
			if (this.facesMaterial[i] != -1 && !this.glToolkit.materialProvider.textureAvailable(this.facesMaterial[i])) {
				return false;
			}
		}
		return true;
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(Lclient!ka;IIIZ)V")
	@Override
	public void shareLight(@OriginalArg(0) Model model, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int z, @OriginalArg(4) boolean renderTypeAdjustment) {
		@Pc(8) GlModel glModel = (GlModel) model;
		if (this.numFaces == 0 || glModel.numFaces == 0) {
			return;
		}
		@Pc(24) int otherVertexCount = glModel.maxDepth;
		@Pc(27) int[] otherVerticesX = glModel.verticesX;
		@Pc(30) int[] otherVerticesY = glModel.verticesY;
		@Pc(33) int[] otherVerticesZ = glModel.verticesZ;
		@Pc(36) short[] otherNormalsX = glModel.normalsX;
		@Pc(39) short[] otherNormalsY = glModel.normalsY;
		@Pc(42) short[] otherNormalsZ = glModel.normalsZ;
		@Pc(45) byte[] otherMagnitudes = glModel.magnitudes;
		@Pc(52) short[] mergedNormalsX;
		@Pc(60) short[] mergedNormalsY;
		@Pc(64) short[] mergedNormalsZ;
		@Pc(56) byte[] mergedMagnitudes;
		if (this.mergedNormals == null) {
			mergedNormalsZ = null;
			mergedNormalsY = null;
			mergedMagnitudes = null;
			mergedNormalsX = null;
		} else {
			mergedNormalsX = this.mergedNormals.normalsX;
			mergedMagnitudes = this.mergedNormals.magnitude;
			mergedNormalsY = this.mergedNormals.normalsY;
			mergedNormalsZ = this.mergedNormals.normalsZ;
		}
		@Pc(89) short[] otherMergedNormalsX;
		@Pc(93) short[] otherMergedNormalsY;
		@Pc(81) short[] otherMergedNormalsZ;
		@Pc(85) byte[] otherMergedMagnitudes;
		if (glModel.mergedNormals == null) {
			otherMergedMagnitudes = null;
			otherMergedNormalsY = null;
			otherMergedNormalsX = null;
			otherMergedNormalsZ = null;
		} else {
			otherMergedNormalsZ = glModel.mergedNormals.normalsZ;
			otherMergedMagnitudes = glModel.mergedNormals.magnitude;
			otherMergedNormalsX = glModel.mergedNormals.normalsX;
			otherMergedNormalsY = glModel.mergedNormals.normalsY;
		}
		@Pc(106) int[] otherTriangles = glModel.triangles;
		@Pc(109) short[] otherVertexNormalIndices = glModel.vertexNormalIndices;
		if (!glModel.boundsCalculated) {
			glModel.calculateBounds();
		}
		@Pc(118) short otherMinY = glModel.minY;
		@Pc(121) short otherMaxY = glModel.maxY;
		@Pc(124) short otherMinX = glModel.minX;
		@Pc(127) short otherMaxX = glModel.maxX;
		@Pc(130) short otherMinZ = glModel.minZ;
		@Pc(133) short otherMaxZ = glModel.maxZ;
		for (@Pc(135) int i = 0; i < this.maxDepth; i++) {
			@Pc(145) int relY = this.verticesY[i] - y;
			if (relY >= otherMinY && relY <= otherMaxY) {
				@Pc(165) int relX = this.verticesX[i] - x;
				if (relX >= otherMinX && relX <= otherMaxX) {
					@Pc(181) int relZ = this.verticesZ[i] - z;
					if (otherMinZ <= relZ && otherMaxZ >= relZ) {
						@Pc(195) int normalIndex = -1;
						@Pc(200) int start = this.triangles[i];
						@Pc(207) int end = this.triangles[i + 1];
						for (@Pc(209) int j = start; j < end; j++) {
							normalIndex = this.vertexNormalIndices[j] - 1;
							if (normalIndex == -1 || this.magnitudes[normalIndex] != 0) {
								break;
							}
						}
						if (normalIndex != -1) {
							for (@Pc(244) int k = 0; k < otherVertexCount; k++) {
								if (otherVerticesX[k] == relX && relZ == otherVerticesZ[k] && otherVerticesY[k] == relY) {
									end = otherTriangles[k + 1];
									@Pc(269) int otherNormalIndex = -1;
									start = otherTriangles[k];
									for (@Pc(275) int m = start; m < end; m++) {
										otherNormalIndex = otherVertexNormalIndices[m] - 1;
										if (otherNormalIndex == -1 || otherMagnitudes[otherNormalIndex] != 0) {
											break;
										}
									}
									if (otherNormalIndex != -1) {
										if (mergedNormalsX == null) {
											this.mergedNormals = new ModelNormalsGL();
											mergedNormalsX = this.mergedNormals.normalsX = Arrays.copyOf(this.normalsX);
											mergedNormalsY = this.mergedNormals.normalsY = Arrays.copyOf(this.normalsY);
											mergedNormalsZ = this.mergedNormals.normalsZ = Arrays.copyOf(this.normalsZ);
											mergedMagnitudes = this.mergedNormals.magnitude = Arrays.copyOf(this.magnitudes);
										}
										if (otherMergedNormalsX == null) {
											@Pc(352) ModelNormalsGL otherMergedNormals = glModel.mergedNormals = new ModelNormalsGL();
											otherMergedNormalsX = otherMergedNormals.normalsX = Arrays.copyOf(otherNormalsX);
											otherMergedNormalsY = otherMergedNormals.normalsY = Arrays.copyOf(otherNormalsY);
											otherMergedNormalsZ = otherMergedNormals.normalsZ = Arrays.copyOf(otherNormalsZ);
											otherMergedMagnitudes = otherMergedNormals.magnitude = Arrays.copyOf(otherMagnitudes);
										}
										@Pc(385) short normalX = this.normalsX[normalIndex];
										@Pc(390) short normalY = this.normalsY[normalIndex];
										@Pc(395) short normalZ = this.normalsZ[normalIndex];
										@Pc(400) byte magnitude = this.magnitudes[normalIndex];
										start = otherTriangles[k];
										end = otherTriangles[k + 1];
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
										normalY = otherNormalsY[otherNormalIndex];
										normalZ = otherNormalsZ[otherNormalIndex];
										magnitude = otherMagnitudes[otherNormalIndex];
										start = this.triangles[i];
										end = this.triangles[i + 1];
										normalX = otherNormalsX[otherNormalIndex];
										for (int index = start; index < end; index++) {
											@Pc(507) int targetNormalIndex = this.vertexNormalIndices[index] - 1;
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
										if (this.normalsBuffer == null && this.colorsBuffer != null) {
											this.colorsBuffer.buffer = null;
										}
										if (this.normalsBuffer != null) {
											this.normalsBuffer.buffer = null;
										}
										if (glModel.normalsBuffer == null && glModel.colorsBuffer != null) {
											glModel.colorsBuffer.buffer = null;
										}
										if (glModel.normalsBuffer != null) {
											glModel.normalsBuffer.buffer = null;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(IILclient!bca;ZII)Z")
	@Override
	public boolean pickedOrtho(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) Matrix matrix, @OriginalArg(3) boolean quick, @OriginalArg(4) int sizeShift, @OriginalArg(5) int zoom) {
		return this.pick(quick, zoom, y, sizeShift, x, matrix);
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(Z)V")
	private void renderBillboards() {
		if (this.billboardFaces == null) {
			return;
		}
		@Pc(8) Matrix43 billboardMatrix = this.glToolkit.billboardMatrix;
		this.glToolkit.pushMatrix();
		this.glToolkit.C(!this.hasAlphaTransition);
		this.glToolkit.setLightingEnabled(false);
		this.glToolkit.toggleDrawingClientstates(this.glToolkit.quadVertexBuffer, null, this.glToolkit.quadTexCoordBuffer, null);
		for (@Pc(40) int i = 0; i < this.billboardCount; i++) {
			@Pc(47) GlBillboardFace glBillboardFace = this.billboardFaces[i];
			@Pc(52) BillboardSurfaceGL glBillboardAttributes = this.billboardAttributes[i];
			if (!glBillboardFace.hideWithBloom || !this.glToolkit.bloom()) {
				@Pc(80) float centerX = (float) (this.verticesX[glBillboardFace.vertexC] + this.verticesX[glBillboardFace.vertexA] + this.verticesX[glBillboardFace.vertexB]) * 0.3333333F;
				@Pc(101) float centerY = (float) (this.verticesY[glBillboardFace.vertexA] + this.verticesY[glBillboardFace.vertexB] + this.verticesY[glBillboardFace.vertexC]) * 0.3333333F;
				@Pc(122) float centerZ = (float) (this.verticesZ[glBillboardFace.vertexA] + this.verticesZ[glBillboardFace.vertexB] + this.verticesZ[glBillboardFace.vertexC]) * 0.3333333F;
				@Pc(136) float viewX = GlModel.modelViewXW + centerZ * GlModel.modelViewXZ + centerY * GlModel.modelViewXY + GlModel.modelViewXX * centerX;
				@Pc(150) float viewY = GlModel.modelViewYW + GlModel.modelViewYZ * centerZ + centerY * GlModel.modelViewYY + GlModel.modelViewYX * centerX;
				@Pc(164) float viewZ = centerX * GlModel.modelViewZX + centerY * GlModel.modelViewZY + centerZ * GlModel.modelViewZZ + GlModel.modelViewZW;
				@Pc(185) float scale = (float) (1.0D / Math.sqrt((double) (viewZ * viewZ + viewY * viewY + viewX * viewX))) * (float) glBillboardFace.distance;
				billboardMatrix.setBillboardTransform(viewZ * scale - viewZ, glBillboardFace.width * glBillboardAttributes.scaleX >> 7, viewX + (float) glBillboardAttributes.offsetX - viewX * scale, glBillboardFace.height * glBillboardAttributes.scaleY >> 7, glBillboardAttributes.angle, scale * viewY + (float) glBillboardAttributes.offsetY - viewY);
				this.glToolkit.loadMatrix(billboardMatrix);
				@Pc(238) int diffuseColor = glBillboardAttributes.diffuseColor;
				OpenGL.glColor4ub((byte) (diffuseColor >> 16), (byte) (diffuseColor >> 8), (byte) diffuseColor, (byte) (diffuseColor >> 24));
				this.glToolkit.renderTexture(glBillboardFace.texture);
				this.glToolkit.setBlendMode(glBillboardFace.blendMode);
				this.glToolkit.drawArrays(4);
			}
		}
		this.glToolkit.C(true);
		this.glToolkit.popMatrix();
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(IIJILclient!bh;IFIIF)S")
	private short connectNormal(@OriginalArg(0) int y, @OriginalArg(1) int z, @OriginalArg(2) long uid, @OriginalArg(3) int x, @OriginalArg(4) Mesh mesh, @OriginalArg(5) int magnitude, @OriginalArg(6) float u, @OriginalArg(8) int vertex, @OriginalArg(9) float v) {
		@Pc(10) int start = this.triangles[vertex];
		@Pc(17) int end = this.triangles[vertex + 1];
		@Pc(19) int freeSlot = 0;
		for (@Pc(21) int i = start; i < end; i++) {
			@Pc(28) short normalIndex = this.vertexNormalIndices[i];
			if (normalIndex == 0) {
				freeSlot = i;
				break;
			}
			if (uid == GlModel.normalsUid[i]) {
				return (short) (normalIndex - 1);
			}
		}
		this.vertexNormalIndices[freeSlot] = (short) (this.numNormals + 1);
		GlModel.normalsUid[freeSlot] = uid;
		this.normalsX[this.numNormals] = (short) x;
		this.normalsY[this.numNormals] = (short) y;
		this.normalsZ[this.numNormals] = (short) z;
		this.magnitudes[this.numNormals] = (byte) magnitude;
		this.textureU[this.numNormals] = u;
		this.textureV[this.numNormals] = v;
		return (short) this.numNormals++;
	}

	@OriginalMember(owner = "client!paa", name = "EA", descriptor = "()I")
	@Override
	public int EA() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.maxY;
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(ZIIIIILclient!bca;)Z")
	private boolean pick(@OriginalArg(0) boolean quick, @OriginalArg(2) int zoom, @OriginalArg(3) int y, @OriginalArg(4) int sizeShift, @OriginalArg(5) int x, @OriginalArg(6) Matrix matrix) {
		@Pc(8) Matrix43 modelMatrix = (Matrix43) matrix;
		@Pc(12) Matrix43 viewMatrix = this.glToolkit.viewMatrix43;
		@Pc(33) float translateX = viewMatrix.xD + modelMatrix.zD * viewMatrix.xC + viewMatrix.xA * modelMatrix.xD + modelMatrix.yD * viewMatrix.xB;
		@Pc(54) float translateY = viewMatrix.yD + modelMatrix.zD * viewMatrix.yC + modelMatrix.xD * viewMatrix.yA + modelMatrix.yD * viewMatrix.yB;
		GlModel.modelViewXY = viewMatrix.xC * modelMatrix.zB + modelMatrix.yB * viewMatrix.xB + viewMatrix.xA * modelMatrix.xB;
		GlModel.modelViewYZ = modelMatrix.xC * viewMatrix.yA + viewMatrix.yB * modelMatrix.yC + viewMatrix.yC * modelMatrix.zC;
		GlModel.modelViewZX = viewMatrix.zB * modelMatrix.yA + modelMatrix.xA * viewMatrix.zA + modelMatrix.zA * viewMatrix.zC;
		GlModel.modelViewZZ = modelMatrix.xC * viewMatrix.zA + modelMatrix.yC * viewMatrix.zB + viewMatrix.zC * modelMatrix.zC;
		@Pc(147) float translateZ = viewMatrix.zA * modelMatrix.xD + viewMatrix.zB * modelMatrix.yD + modelMatrix.zD * viewMatrix.zC + viewMatrix.zD;
		GlModel.modelViewYY = viewMatrix.yA * modelMatrix.xB + viewMatrix.yB * modelMatrix.yB + viewMatrix.yC * modelMatrix.zB;
		GlModel.modelViewYX = modelMatrix.zA * viewMatrix.yC + modelMatrix.yA * viewMatrix.yB + viewMatrix.yA * modelMatrix.xA;
		GlModel.modelViewXZ = viewMatrix.xB * modelMatrix.yC + viewMatrix.xA * modelMatrix.xC + modelMatrix.zC * viewMatrix.xC;
		GlModel.modelViewXX = modelMatrix.zA * viewMatrix.xC + modelMatrix.xA * viewMatrix.xA + modelMatrix.yA * viewMatrix.xB;
		GlModel.modelViewZY = viewMatrix.zC * modelMatrix.zB + modelMatrix.yB * viewMatrix.zB + modelMatrix.xB * viewMatrix.zA;
		@Pc(239) boolean anyProjected = false;
		@Pc(241) float minScreenX = Float.MAX_VALUE;
		@Pc(243) float maxScreenX = -3.4028235E38F;
		@Pc(245) float minScreenY = Float.MAX_VALUE;
		@Pc(247) float maxScreenY = -3.4028235E38F;
		@Pc(251) int scaleX = this.glToolkit.projectionScaleX;
		@Pc(255) int scaleY = this.glToolkit.projectionScaleY;
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		@Pc(270) int extentX = this.maxX - this.minX >> 1;
		@Pc(279) int extentY = this.maxY - this.minY >> 1;
		@Pc(288) int extentZ = this.maxZ - this.minZ >> 1;
		@Pc(293) int centerX = this.minX + extentX;
		@Pc(299) int centerY = this.minY + extentY;
		@Pc(304) int centerZ = extentZ + this.minZ;
		@Pc(311) int boxMinX = centerX - (extentX << sizeShift);
		@Pc(318) int boxMinY = centerY - (extentY << sizeShift);
		@Pc(324) int boxMinZ = centerZ - (extentZ << sizeShift);
		@Pc(330) int boxMaxX = centerX + (extentX << sizeShift);
		@Pc(336) int boxMaxY = (extentY << sizeShift) + centerY;
		@Pc(342) int boxMaxZ = (extentZ << sizeShift) + centerZ;
		GlModel.matrixX[0] = boxMinX;
		GlModel.matrixY[0] = boxMinY;
		GlModel.matrixX[1] = boxMaxX;
		GlModel.matrixZ[0] = boxMinZ;
		GlModel.matrixY[1] = boxMinY;
		GlModel.matrixX[2] = boxMinX;
		GlModel.matrixZ[1] = boxMinZ;
		GlModel.matrixY[2] = boxMaxY;
		GlModel.matrixZ[2] = boxMinZ;
		GlModel.matrixX[3] = boxMaxX;
		GlModel.matrixY[3] = boxMaxY;
		GlModel.matrixX[4] = boxMinX;
		GlModel.matrixZ[3] = boxMinZ;
		GlModel.matrixY[4] = boxMinY;
		GlModel.matrixZ[4] = boxMaxZ;
		GlModel.matrixX[5] = boxMaxX;
		GlModel.matrixY[5] = boxMinY;
		GlModel.matrixZ[5] = boxMaxZ;
		GlModel.matrixX[6] = boxMinX;
		GlModel.matrixY[6] = boxMaxY;
		GlModel.matrixX[7] = boxMaxX;
		GlModel.matrixZ[6] = boxMaxZ;
		GlModel.matrixY[7] = boxMaxY;
		GlModel.matrixZ[7] = boxMaxZ;
		@Pc(504) float viewX;
		@Pc(490) float viewY;
		@Pc(476) float viewZ;
		@Pc(462) float pointX;
		@Pc(457) float pointY;
		@Pc(452) float pointZ;
		for (@Pc(445) int i = 0; i < 8; i++) {
			pointZ = GlModel.matrixZ[i];
			pointY = GlModel.matrixY[i];
			pointX = GlModel.matrixX[i];
			viewZ = GlModel.modelViewZZ * pointZ + pointX * GlModel.modelViewZX + GlModel.modelViewZY * pointY + translateZ;
			viewY = translateY + GlModel.modelViewYX * pointX + GlModel.modelViewYY * pointY + GlModel.modelViewYZ * pointZ;
			viewX = translateX + GlModel.modelViewXY * pointY + pointX * GlModel.modelViewXX + GlModel.modelViewXZ * pointZ;
			if ((float) this.glToolkit.zNear <= viewZ) {
				if (zoom > 0) {
					viewZ = zoom;
				}
				@Pc(531) float screenX = viewX * (float) scaleX / viewZ + (float) this.glToolkit.projectionCenterX;
				if (screenX < minScreenX) {
					minScreenX = screenX;
				}
				if (screenX > maxScreenX) {
					maxScreenX = screenX;
				}
				@Pc(555) float screenY = viewY * (float) scaleY / viewZ + (float) this.glToolkit.projectionCenterY;
				if (screenY > maxScreenY) {
					maxScreenY = screenY;
				}
				anyProjected = true;
				if (screenY < minScreenY) {
					minScreenY = screenY;
				}
			}
		}
		if (anyProjected && (float) x > minScreenX && (float) x < maxScreenX && (float) y > minScreenY && (float) y < maxScreenY) {
			if (quick) {
				return true;
			}
			if (this.glToolkit.scratchVerticesX.length < this.numNormals) {
				this.glToolkit.scratchVerticesY = new int[this.numNormals];
				this.glToolkit.scratchVerticesX = new int[this.numNormals];
			}
			@Pc(626) int[] projectedX = this.glToolkit.scratchVerticesX;
			@Pc(630) int[] projectedY = this.glToolkit.scratchVerticesY;
			@Pc(706) int local706;
			for (@Pc(632) int j = 0; j < this.maxDepth; j++) {
				pointX = this.verticesX[j];
				pointZ = this.verticesZ[j];
				pointY = this.verticesY[j];
				viewZ = translateZ + pointZ * GlModel.modelViewZZ + GlModel.modelViewZX * pointX + pointY * GlModel.modelViewZY;
				viewX = translateX + pointY * GlModel.modelViewXY + pointX * GlModel.modelViewXX + GlModel.modelViewXZ * pointZ;
				viewY = translateY + GlModel.modelViewYY * pointY + pointX * GlModel.modelViewYX + pointZ * GlModel.modelViewYZ;
				@Pc(713) int local713;
				@Pc(715) int local715;
				@Pc(724) int local724;
				if (viewZ >= (float) this.glToolkit.zNear) {
					if (zoom > 0) {
						viewZ = zoom;
					}
					int screenX = (int) ((float) this.glToolkit.projectionCenterX + viewX * (float) scaleX / viewZ);
					int screenY = (int) ((float) this.glToolkit.projectionCenterY + (float) scaleY * viewY / viewZ);
					int start = this.triangles[j];
					int end = this.triangles[j + 1];
					for (@Pc(791) int k = start; k < end; k++) {
						@Pc(800) int normalIndex = this.vertexNormalIndices[k] - 1;
						if (normalIndex == -1) {
							break;
						}
						projectedX[normalIndex] = screenX;
						projectedY[normalIndex] = screenY;
					}
				} else {
					int start = this.triangles[j];
					int end = this.triangles[j + 1];
					for (int index = start; index < end; index++) {
						int normalIndex = this.vertexNormalIndices[index] - 1;
						if (normalIndex == -1) {
							break;
						}
						projectedX[this.vertexNormalIndices[index] - 1] = -999999;
					}
				}
			}
			for (int faceIndex = 0; faceIndex < this.numFaces; faceIndex++) {
				if (projectedX[this.facesA[faceIndex]] != -999999 && projectedX[this.facesB[faceIndex]] != -999999 && projectedX[this.facesC[faceIndex]] != -999999 && this.isHovering(y, projectedX[this.facesB[faceIndex]], projectedX[this.facesC[faceIndex]], projectedY[this.facesB[faceIndex]], projectedX[this.facesA[faceIndex]], projectedY[this.facesA[faceIndex]], x, projectedY[this.facesC[faceIndex]])) {
					return true;
				}
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!paa", name = "O", descriptor = "(III)V")
	@Override
	public void O(@OriginalArg(0) int scaleX, @OriginalArg(1) int scaleY, @OriginalArg(2) int scaleZ) {
		for (@Pc(7) int i = 0; i < this.maxDepth; i++) {
			if (scaleX != 128) {
				this.verticesX[i] = scaleX * this.verticesX[i] >> 7;
			}
			if (scaleY != 128) {
				this.verticesY[i] = this.verticesY[i] * scaleY >> 7;
			}
			if (scaleZ != 128) {
				this.verticesZ[i] = this.verticesZ[i] * scaleZ >> 7;
			}
		}
		if (this.vertexBuffer != null) {
			this.vertexBuffer.buffer = null;
		}
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!paa", name = "ba", descriptor = "(Lclient!r;)Lclient!r;")
	@Override
	public Shadow ba(@OriginalArg(0) Shadow shadow) {
		if (this.numNormals == 0) {
			return null;
		}
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		@Pc(43) int shadowMinX;
		@Pc(60) int shadowMaxX;
		if (this.glToolkit.sunSlopeX > 0) {
			shadowMinX = this.minX - (this.maxY * this.glToolkit.sunSlopeX >> 8) >> this.glToolkit.shadowScaleShift;
			shadowMaxX = this.maxX - (this.minY * this.glToolkit.sunSlopeX >> 8) >> this.glToolkit.shadowScaleShift;
		} else {
			shadowMinX = this.minX - (this.minY * this.glToolkit.sunSlopeX >> 8) >> this.glToolkit.shadowScaleShift;
			shadowMaxX = this.maxX - (this.glToolkit.sunSlopeX * this.maxY >> 8) >> this.glToolkit.shadowScaleShift;
		}
		@Pc(117) int shadowMinZ;
		@Pc(133) int shadowMaxZ;
		if (this.glToolkit.sunSlopeZ > 0) {
			shadowMinZ = this.minZ - (this.glToolkit.sunSlopeZ * this.maxY >> 8) >> this.glToolkit.shadowScaleShift;
			shadowMaxZ = this.maxZ - (this.minY * this.glToolkit.sunSlopeZ >> 8) >> this.glToolkit.shadowScaleShift;
		} else {
			shadowMinZ = this.minZ - (this.glToolkit.sunSlopeZ * this.minY >> 8) >> this.glToolkit.shadowScaleShift;
			shadowMaxZ = this.maxZ - (this.glToolkit.sunSlopeZ * this.maxY >> 8) >> this.glToolkit.shadowScaleShift;
		}
		@Pc(175) int width = shadowMaxX + 1 - shadowMinX;
		@Pc(182) int height = shadowMaxZ + 1 - shadowMinZ;
		@Pc(185) GlShadow glShadow = (GlShadow) shadow;
		@Pc(201) GlShadow glShadow2;
		if (glShadow != null && glShadow.fitsDimension(height, width)) {
			glShadow2 = glShadow;
			glShadow.reset();
		} else {
			glShadow2 = new GlShadow(this.glToolkit, width, height);
		}
		glShadow2.setDimension(shadowMaxZ, shadowMaxX, shadowMinZ, shadowMinX);
		this.projectShadow(glShadow2);
		return glShadow2;
	}

	@OriginalMember(owner = "client!paa", name = "b", descriptor = "()V")
	@Override
	public void uploadToGpu() {
		if (this.numNormals <= 0 || this.renderedFaceCount <= 0) {
			return;
		}
		this.upload(false);
		if ((this.renderState & 0x10) == 0 && this.modelElementsBuffer.openglElementsBuffer == null) {
			this.buildElementsBuffer(false);
		}
		this.releaseUploadedData();
	}

	@OriginalMember(owner = "client!paa", name = "na", descriptor = "()I")
	@Override
	public int na() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.size2d;
	}

	@OriginalMember(owner = "client!paa", name = "k", descriptor = "(I)V")
	@Override
	public void k(@OriginalArg(0) int angle) {
		@Pc(9) int sin = TextureCombineSource.SINE[angle];
		@Pc(13) int cos = TextureCombineSource.COSINE[angle];
		@Pc(33) int local33;
		for (@Pc(15) int i = 0; i < this.maxDepth; i++) {
			int rotatedX = this.verticesX[i] * cos + this.verticesZ[i] * sin >> 14;
			this.verticesZ[i] = this.verticesZ[i] * cos - this.verticesX[i] * sin >> 14;
			this.verticesX[i] = rotatedX;
		}
		for (int normalIndex = 0; normalIndex < this.numNormals; normalIndex++) {
			@Pc(87) int rotatedNormalX = this.normalsX[normalIndex] * cos + this.normalsZ[normalIndex] * sin >> 14;
			this.normalsZ[normalIndex] = (short) (cos * this.normalsZ[normalIndex] - sin * this.normalsX[normalIndex] >> 14);
			this.normalsX[normalIndex] = (short) rotatedNormalX;
		}
		if (this.normalsBuffer == null && this.colorsBuffer != null) {
			this.colorsBuffer.buffer = null;
		}
		if (this.normalsBuffer != null) {
			this.normalsBuffer.buffer = null;
		}
		if (this.vertexBuffer != null) {
			this.vertexBuffer.buffer = null;
		}
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(BLclient!uc;)V")
	private void projectShadow(@OriginalArg(1) GlShadow shadow) {
		if (this.numNormals > this.glToolkit.scratchVerticesX.length) {
			this.glToolkit.scratchVerticesY = new int[this.numNormals];
			this.glToolkit.scratchVerticesX = new int[this.numNormals];
		}
		@Pc(32) int[] shadowX = this.glToolkit.scratchVerticesX;
		@Pc(42) int[] shadowZ = this.glToolkit.scratchVerticesY;
		@Pc(70) int local70;
		@Pc(109) int local109;
		@Pc(118) int local118;
		for (@Pc(44) int i = 0; i < this.maxDepth; i++) {
			int vertexShadowX = (this.verticesX[i] - (this.glToolkit.sunSlopeX * this.verticesY[i] >> 8) >> this.glToolkit.shadowScaleShift) - shadow.startX;
			@Pc(95) int vertexShadowZ = (this.verticesZ[i] - (this.glToolkit.sunSlopeZ * this.verticesY[i] >> 8) >> this.glToolkit.shadowScaleShift) - shadow.startZ;
			@Pc(100) int start = this.triangles[i];
			@Pc(107) int end = this.triangles[i + 1];
			for (int index = start; index < end; index++) {
				int normalIndex = this.vertexNormalIndices[index] - 1;
				if (normalIndex == -1) {
					break;
				}
				shadowX[normalIndex] = vertexShadowX;
				shadowZ[normalIndex] = vertexShadowZ;
			}
		}
		for (int faceIndex = 0; faceIndex < this.renderedFaceCount; faceIndex++) {
			if (this.facesAlpha == null || this.facesAlpha[faceIndex] <= 128) {
				@Pc(162) short vertexA = this.facesA[faceIndex];
				@Pc(167) short vertexB = this.facesB[faceIndex];
				@Pc(172) short vertexC = this.facesC[faceIndex];
				int xA = shadowX[vertexA];
				int xB = shadowX[vertexB];
				@Pc(184) int xC = shadowX[vertexC];
				@Pc(188) int zA = shadowZ[vertexA];
				@Pc(192) int zB = shadowZ[vertexB];
				@Pc(196) int zC = shadowZ[vertexC];
				if ((xA - xB) * (zB - zC) - (xC - xB) * (zB - zA) > 0) {
					shadow.fillTriangle(xC, xA, zA, xB, zC, zB);
				}
			}
		}
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(IIII)V")
	@Override
	public void adjustColours(@OriginalArg(0) int hue, @OriginalArg(1) int saturation, @OriginalArg(2) int lightness, @OriginalArg(3) int opacity) {
		@Pc(16) int local16;
		for (@Pc(7) int i = 0; i < this.numFaces; i++) {
			int faceColor = this.facesColor[i] & 0xFFFF;
			@Pc(22) int faceHue = faceColor >> 10 & 0x3F;
			@Pc(28) int faceSaturation = faceColor >> 7 & 0x7;
			if (hue != -1) {
				faceHue += opacity * (hue - faceHue) >> 7;
			}
			@Pc(47) int faceLightness = faceColor & 0x7F;
			if (saturation != -1) {
				faceSaturation += opacity * (saturation - faceSaturation) >> 7;
			}
			if (lightness != -1) {
				faceLightness += (lightness - faceLightness) * opacity >> 7;
			}
			this.facesColor[i] = (short) (faceSaturation << 7 | faceHue << 10 | faceLightness);
		}
		if (this.billboardFaces != null) {
			for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
				@Pc(106) GlBillboardFace glBillboardFace = this.billboardFaces[billboardIndex];
				@Pc(111) BillboardSurfaceGL glBillboardAttributes = this.billboardAttributes[billboardIndex];
				glBillboardAttributes.diffuseColor = ColorUtil.hslToRgb[this.facesColor[glBillboardFace.face] & 0xFFFF] & 0xFFFFFF | glBillboardAttributes.diffuseColor & 0xFF000000;
			}
		}
		if (this.colorsBuffer != null) {
			this.colorsBuffer.buffer = null;
		}
	}

	@OriginalMember(owner = "client!paa", name = "H", descriptor = "(III)V")
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
		this.boundsCalculated = false;
		if (this.vertexBuffer != null) {
			this.vertexBuffer.buffer = null;
		}
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(I)V")
	@Override
	public void a(@OriginalArg(0) int angle) {
		@Pc(9) int sin = TextureCombineSource.SINE[angle];
		@Pc(13) int cos = TextureCombineSource.COSINE[angle];
		for (@Pc(15) int i = 0; i < this.maxDepth; i++) {
			@Pc(33) int rotatedX = cos * this.verticesX[i] + sin * this.verticesZ[i] >> 14;
			this.verticesZ[i] = this.verticesZ[i] * cos - this.verticesX[i] * sin >> 14;
			this.verticesX[i] = rotatedX;
		}
		this.boundsCalculated = false;
		if (this.vertexBuffer != null) {
			this.vertexBuffer.buffer = null;
		}
	}

	@OriginalMember(owner = "client!paa", name = "F", descriptor = "()Z")
	@Override
	public boolean F() {
		return this.hasAlphaTransition;
	}

	@OriginalMember(owner = "client!paa", name = "f", descriptor = "()V")
	@Override
	public void lock() {
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(Lclient!paa;IIZLclient!paa;Z)Lclient!ka;")
	private Model copyModelData(@OriginalArg(0) GlModel glModel, @OriginalArg(1) int functionMask, @OriginalArg(3) boolean ensureLit, @OriginalArg(4) GlModel glModel2, @OriginalArg(5) boolean usesPreallocated) {
		glModel.numVertices = this.numVertices;
		glModel.preferenceFlags = this.preferenceFlags;
		glModel.billboardCount = this.billboardCount;
		glModel.renderState = 0;
		glModel.ambient = this.ambient;
		glModel.numNormals = this.numNormals;
		glModel.textureAnimating = this.textureAnimating;
		glModel.functionMask = functionMask;
		glModel.contrast = this.contrast;
		glModel.numFaces = this.numFaces;
		glModel.renderedFaceCount = this.renderedFaceCount;
		glModel.maxDepth = this.maxDepth;
		if ((functionMask & 0x100) == 0) {
			glModel.hasAlphaTransition = this.hasAlphaTransition;
		} else {
			glModel.hasAlphaTransition = true;
		}
		@Pc(70) boolean copyVerticesX = GlModel.altersVerticesX(this.preferenceFlags, functionMask);
		@Pc(76) boolean copyVerticesY = GlModel.altersVerticesY(this.preferenceFlags, functionMask);
		@Pc(82) boolean copyVerticesZ = GlModel.altersVerticesZ(this.preferenceFlags, functionMask);
		@Pc(88) boolean copyVertices = copyVerticesZ | copyVerticesY | copyVerticesX;
		@Pc(203) int billboardCount;
		if (copyVertices) {
			if (!copyVerticesX) {
				glModel.verticesX = this.verticesX;
			} else if (glModel2.verticesX == null || glModel2.verticesX.length < this.numVertices) {
				glModel.verticesX = glModel2.verticesX = new int[this.numVertices];
			} else {
				glModel.verticesX = glModel2.verticesX;
			}
			if (!copyVerticesY) {
				glModel.verticesY = this.verticesY;
			} else if (glModel2.verticesY == null || this.numVertices > glModel2.verticesY.length) {
				glModel.verticesY = glModel2.verticesY = new int[this.numVertices];
			} else {
				glModel.verticesY = glModel2.verticesY;
			}
			if (!copyVerticesZ) {
				glModel.verticesZ = this.verticesZ;
			} else if (glModel2.verticesZ == null || this.numVertices > glModel2.verticesZ.length) {
				glModel.verticesZ = glModel2.verticesZ = new int[this.numVertices];
			} else {
				glModel.verticesZ = glModel2.verticesZ;
			}
			for (billboardCount = 0; billboardCount < this.numVertices; billboardCount++) {
				if (copyVerticesX) {
					glModel.verticesX[billboardCount] = this.verticesX[billboardCount];
				}
				if (copyVerticesY) {
					glModel.verticesY[billboardCount] = this.verticesY[billboardCount];
				}
				if (copyVerticesZ) {
					glModel.verticesZ[billboardCount] = this.verticesZ[billboardCount];
				}
			}
		} else {
			glModel.verticesZ = this.verticesZ;
			glModel.verticesY = this.verticesY;
			glModel.verticesX = this.verticesX;
		}
		if (GlModel.altersAnyVerticeType(functionMask, this.preferenceFlags)) {
			glModel.vertexBuffer = glModel2.vertexBuffer;
			if (usesPreallocated) {
				glModel.renderState = (byte) (glModel.renderState | 0x1);
			}
			glModel.vertexBuffer.buffer = this.vertexBuffer.buffer;
			glModel.vertexBuffer.offset = this.vertexBuffer.offset;
		} else if (GlModel.hasVertices(functionMask, this.preferenceFlags)) {
			glModel.vertexBuffer = this.vertexBuffer;
		} else {
			glModel.vertexBuffer = null;
		}
		if (GpuModelRetainMasks.altersFaceColors(this.preferenceFlags, functionMask)) {
			if (glModel2.facesColor == null || this.numFaces > glModel2.facesColor.length) {
				glModel.facesColor = glModel2.facesColor = new short[this.numFaces];
			} else {
				glModel.facesColor = glModel2.facesColor;
			}
			for (billboardCount = 0; billboardCount < this.numFaces; billboardCount++) {
				glModel.facesColor[billboardCount] = this.facesColor[billboardCount];
			}
		} else {
			glModel.facesColor = this.facesColor;
		}
		if (GlModel.altersAlpha(functionMask, this.preferenceFlags)) {
			if (glModel2.facesAlpha == null || this.numFaces > glModel2.facesAlpha.length) {
				glModel.facesAlpha = glModel2.facesAlpha = new byte[this.numFaces];
			} else {
				glModel.facesAlpha = glModel2.facesAlpha;
			}
			for (billboardCount = 0; billboardCount < this.numFaces; billboardCount++) {
				glModel.facesAlpha[billboardCount] = this.facesAlpha[billboardCount];
			}
		} else {
			glModel.facesAlpha = this.facesAlpha;
		}
		if (GlModel.requiresColorBufferRebuild(functionMask, this.preferenceFlags)) {
			glModel.colorsBuffer = glModel2.colorsBuffer;
			if (usesPreallocated) {
				glModel.renderState = (byte) (glModel.renderState | 0x2);
			}
			glModel.colorsBuffer.buffer = this.colorsBuffer.buffer;
			glModel.colorsBuffer.offset = this.colorsBuffer.offset;
		} else if (GlModel.hasColors(this.preferenceFlags, functionMask)) {
			glModel.colorsBuffer = this.colorsBuffer;
		} else {
			glModel.colorsBuffer = null;
		}
		@Pc(593) int local593;
		if (GlModel.altersLight(this.preferenceFlags, functionMask)) {
			if (glModel2.normalsX == null || glModel2.normalsX.length < this.numNormals) {
				billboardCount = this.numNormals;
				glModel.normalsY = glModel2.normalsY = new short[billboardCount];
				glModel.normalsX = glModel2.normalsX = new short[billboardCount];
				glModel.normalsZ = glModel2.normalsZ = new short[billboardCount];
			} else {
				glModel.normalsX = glModel2.normalsX;
				glModel.normalsY = glModel2.normalsY;
				glModel.normalsZ = glModel2.normalsZ;
			}
			if (this.mergedNormals == null) {
				for (billboardCount = 0; billboardCount < this.numNormals; billboardCount++) {
					glModel.normalsX[billboardCount] = this.normalsX[billboardCount];
					glModel.normalsY[billboardCount] = this.normalsY[billboardCount];
					glModel.normalsZ[billboardCount] = this.normalsZ[billboardCount];
				}
			} else {
				if (glModel2.mergedNormals == null) {
					glModel2.mergedNormals = new ModelNormalsGL();
				}
				@Pc(581) ModelNormalsGL targetNormals = glModel.mergedNormals = glModel2.mergedNormals;
				if (targetNormals.normalsX == null || this.numNormals > targetNormals.normalsX.length) {
					int normalCount = this.numNormals;
					targetNormals.normalsY = new short[normalCount];
					targetNormals.normalsZ = new short[normalCount];
					targetNormals.normalsX = new short[normalCount];
					targetNormals.magnitude = new byte[normalCount];
				}
				for (int normalIndex = 0; normalIndex < this.numNormals; normalIndex++) {
					glModel.normalsX[normalIndex] = this.normalsX[normalIndex];
					glModel.normalsY[normalIndex] = this.normalsY[normalIndex];
					glModel.normalsZ[normalIndex] = this.normalsZ[normalIndex];
					targetNormals.normalsX[normalIndex] = this.mergedNormals.normalsX[normalIndex];
					targetNormals.normalsY[normalIndex] = this.mergedNormals.normalsY[normalIndex];
					targetNormals.normalsZ[normalIndex] = this.mergedNormals.normalsZ[normalIndex];
					targetNormals.magnitude[normalIndex] = this.mergedNormals.magnitude[normalIndex];
				}
			}
			glModel.magnitudes = this.magnitudes;
		} else {
			glModel.normalsX = this.normalsX;
			glModel.normalsY = this.normalsY;
			glModel.mergedNormals = this.mergedNormals;
			glModel.magnitudes = this.magnitudes;
			glModel.normalsZ = this.normalsZ;
		}
		if (GlModel.requiresNormalsBufferRebuild(functionMask, this.preferenceFlags)) {
			if (usesPreallocated) {
				glModel.renderState = (byte) (glModel.renderState | 0x4);
			}
			glModel.normalsBuffer = glModel2.normalsBuffer;
			glModel.normalsBuffer.buffer = this.normalsBuffer.buffer;
			glModel.normalsBuffer.offset = this.normalsBuffer.offset;
		} else if (GlModel.hasNormals(this.preferenceFlags, functionMask)) {
			glModel.normalsBuffer = this.normalsBuffer;
		} else {
			glModel.normalsBuffer = null;
		}
		if (GlModel.altersTextureCoordinates(functionMask, this.preferenceFlags)) {
			if (glModel2.textureU == null || this.numFaces > glModel2.textureU.length) {
				billboardCount = this.numNormals;
				glModel.textureV = glModel2.textureV = new float[billboardCount];
				glModel.textureU = glModel2.textureU = new float[billboardCount];
			} else {
				glModel.textureV = glModel2.textureV;
				glModel.textureU = glModel2.textureU;
			}
			for (billboardCount = 0; billboardCount < this.numNormals; billboardCount++) {
				glModel.textureU[billboardCount] = this.textureU[billboardCount];
				glModel.textureV[billboardCount] = this.textureV[billboardCount];
			}
		} else {
			glModel.textureV = this.textureV;
			glModel.textureU = this.textureU;
		}
		if (GlModel.altersTextureUV(this.preferenceFlags, functionMask)) {
			glModel.textureCoordinatesBuffer = glModel2.textureCoordinatesBuffer;
			if (usesPreallocated) {
				glModel.renderState = (byte) (glModel.renderState | 0x8);
			}
			glModel.textureCoordinatesBuffer.buffer = this.textureCoordinatesBuffer.buffer;
			glModel.textureCoordinatesBuffer.offset = this.textureCoordinatesBuffer.offset;
		} else if (ModelTexCoordFlag.hasTextureCoordinates(this.preferenceFlags, functionMask)) {
			glModel.textureCoordinatesBuffer = this.textureCoordinatesBuffer;
		} else {
			glModel.textureCoordinatesBuffer = null;
		}
		if (GlModel.altersFacing(this.preferenceFlags, functionMask)) {
			if (glModel2.facesA == null || glModel2.facesA.length < this.numFaces) {
				billboardCount = this.numFaces;
				glModel.facesC = glModel2.facesC = new short[billboardCount];
				glModel.facesA = glModel2.facesA = new short[billboardCount];
				glModel.facesB = glModel2.facesB = new short[billboardCount];
			} else {
				glModel.facesB = glModel2.facesB;
				glModel.facesA = glModel2.facesA;
				glModel.facesC = glModel2.facesC;
			}
			for (billboardCount = 0; billboardCount < this.numFaces; billboardCount++) {
				glModel.facesA[billboardCount] = this.facesA[billboardCount];
				glModel.facesB[billboardCount] = this.facesB[billboardCount];
				glModel.facesC[billboardCount] = this.facesC[billboardCount];
			}
		} else {
			glModel.facesC = this.facesC;
			glModel.facesB = this.facesB;
			glModel.facesA = this.facesA;
		}
		if (GlModel.copyElementsBuffer(functionMask, this.preferenceFlags)) {
			if (usesPreallocated) {
				glModel.renderState = (byte) (glModel.renderState | 0x10);
			}
			glModel.modelElementsBuffer = glModel2.modelElementsBuffer;
			glModel.modelElementsBuffer.openglElementsBuffer = this.modelElementsBuffer.openglElementsBuffer;
		} else if (GlModel.hasElementsBuffer(functionMask, this.preferenceFlags)) {
			glModel.modelElementsBuffer = this.modelElementsBuffer;
		} else {
			glModel.modelElementsBuffer = null;
		}
		if (GlModel.altersTextures(this.preferenceFlags, functionMask)) {
			if (glModel2.facesMaterial == null || glModel2.facesMaterial.length < this.numFaces) {
				billboardCount = this.numFaces;
				glModel.facesMaterial = glModel2.facesMaterial = new short[billboardCount];
			} else {
				glModel.facesMaterial = glModel2.facesMaterial;
			}
			for (billboardCount = 0; billboardCount < this.numFaces; billboardCount++) {
				glModel.facesMaterial[billboardCount] = this.facesMaterial[billboardCount];
			}
		} else {
			glModel.facesMaterial = this.facesMaterial;
		}
		if (!GlModel.copyBillboards(functionMask, this.preferenceFlags)) {
			glModel.billboardAttributes = this.billboardAttributes;
		} else if (glModel2.billboardAttributes == null || this.billboardCount > glModel2.billboardAttributes.length) {
			billboardCount = this.billboardCount;
			glModel.billboardAttributes = glModel2.billboardAttributes = new BillboardSurfaceGL[billboardCount];
			for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
				glModel.billboardAttributes[billboardIndex] = this.billboardAttributes[billboardIndex].copy();
			}
		} else {
			glModel.billboardAttributes = glModel2.billboardAttributes;
			for (billboardCount = 0; billboardCount < this.billboardCount; billboardCount++) {
				glModel.billboardAttributes[billboardCount].copyFrom(this.billboardAttributes[billboardCount]);
			}
		}
		glModel.triangles = this.triangles;
		glModel.effectiveVertices = this.effectiveVertices;
		glModel.materialBatchOffsets = this.materialBatchOffsets;
		if (this.boundsCalculated) {
			glModel.minX = this.minX;
			glModel.size3d = this.size3d;
			glModel.minZ = this.minZ;
			glModel.maxX = this.maxX;
			glModel.maxY = this.maxY;
			glModel.minY = this.minY;
			glModel.boundsCalculated = true;
			glModel.size2d = this.size2d;
			glModel.maxZ = this.maxZ;
		} else {
			glModel.boundsCalculated = false;
		}
		glModel.billboardFaces = this.billboardFaces;
		glModel.verticesSubmeshes = this.verticesSubmeshes;
		glModel.vertexGroups = this.vertexGroups;
		glModel.vertexNormalIndices = this.vertexNormalIndices;
		glModel.faceGroups = this.faceGroups;
		glModel.facesSubmeshes = this.facesSubmeshes;
		glModel.billboardGroups = this.billboardGroups;
		glModel.emissiveTriangles = this.emissiveTriangles;
		return glModel;
	}

	@OriginalMember(owner = "client!paa", name = "LA", descriptor = "(I)V")
	@Override
	public void LA(@OriginalArg(0) int contrast) {
		if (this.colorsBuffer != null) {
			this.colorsBuffer.buffer = null;
		}
		this.contrast = (short) contrast;
		if (this.normalsBuffer != null) {
			this.normalsBuffer.buffer = null;
		}
	}

	@OriginalMember(owner = "client!paa", name = "wa", descriptor = "()V")
	@Override
	public void wa() {
		for (@Pc(3) int i = 0; i < this.numVertices; i++) {
			this.verticesX[i] = this.verticesX[i] + 7 >> 4;
			this.verticesY[i] = this.verticesY[i] + 7 >> 4;
			this.verticesZ[i] = this.verticesZ[i] + 7 >> 4;
		}
		if (this.vertexBuffer != null) {
			this.vertexBuffer.buffer = null;
		}
		this.boundsCalculated = false;
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(Lclient!bca;Lclient!iw;II)V")
	@Override
	public void renderOrtho(@OriginalArg(0) Matrix matrix, @OriginalArg(1) BoundingBoxPolygon cylinder, @OriginalArg(2) int zoom, @OriginalArg(3) int flags) {
		if (this.numNormals == 0) {
			return;
		}
		@Pc(13) Matrix43 viewMatrix = this.glToolkit.viewMatrix43;
		@Pc(16) Matrix43 modelMatrix = (Matrix43) matrix;
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		GlModel.modelViewZY = modelMatrix.xB * viewMatrix.zA + viewMatrix.zB * modelMatrix.yB + viewMatrix.zC * modelMatrix.zB;
		GlModel.modelViewZW = modelMatrix.zD * viewMatrix.zC + viewMatrix.zA * modelMatrix.xD + modelMatrix.yD * viewMatrix.zB + viewMatrix.zD;
		@Pc(69) float depthAtMinY = GlModel.modelViewZW + GlModel.modelViewZY * (float) this.minY;
		@Pc(77) float depthAtMaxY = GlModel.modelViewZY * (float) this.maxY + GlModel.modelViewZW;
		@Pc(93) float nearDepth;
		@Pc(87) float farDepth;
		if (depthAtMinY > depthAtMaxY) {
			farDepth = depthAtMinY + (float) this.size2d;
			nearDepth = (float) -this.size2d + depthAtMaxY;
		} else {
			farDepth = (float) this.size2d + depthAtMaxY;
			nearDepth = depthAtMinY - (float) this.size2d;
		}
		if (nearDepth >= this.glToolkit.farClipDistance || (float) this.glToolkit.zNear >= farDepth) {
			return;
		}
		GlModel.modelViewXY = viewMatrix.xC * modelMatrix.zB + modelMatrix.yB * viewMatrix.xB + modelMatrix.xB * viewMatrix.xA;
		GlModel.modelViewXW = viewMatrix.xC * modelMatrix.zD + modelMatrix.yD * viewMatrix.xB + viewMatrix.xA * modelMatrix.xD + viewMatrix.xD;
		@Pc(170) float xAtMinY = GlModel.modelViewXW + (float) this.minY * GlModel.modelViewXY;
		@Pc(178) float xAtMaxY = GlModel.modelViewXW + (float) this.maxY * GlModel.modelViewXY;
		@Pc(194) float screenLeft;
		@Pc(205) float screenRight;
		if (xAtMinY > xAtMaxY) {
			screenRight = (float) this.glToolkit.projectionScaleX * (xAtMinY + (float) this.size2d);
			screenLeft = (float) this.glToolkit.projectionScaleX * (xAtMaxY - (float) this.size2d);
		} else {
			screenLeft = (float) this.glToolkit.projectionScaleX * ((float) -this.size2d + xAtMinY);
			screenRight = (float) this.glToolkit.projectionScaleX * (xAtMaxY + (float) this.size2d);
		}
		if (this.glToolkit.viewX2 <= screenLeft / (float) zoom || screenRight / (float) zoom <= this.glToolkit.viewX1) {
			return;
		}
		GlModel.modelViewYY = viewMatrix.yB * modelMatrix.yB + modelMatrix.xB * viewMatrix.yA + modelMatrix.zB * viewMatrix.yC;
		GlModel.modelViewYW = viewMatrix.yD + viewMatrix.yC * modelMatrix.zD + viewMatrix.yB * modelMatrix.yD + viewMatrix.yA * modelMatrix.xD;
		@Pc(296) float yAtMinY = GlModel.modelViewYY * (float) this.minY + GlModel.modelViewYW;
		@Pc(304) float yAtMaxY = GlModel.modelViewYW + (float) this.maxY * GlModel.modelViewYY;
		@Pc(320) float screenTop;
		@Pc(331) float screenBottom;
		if (yAtMaxY < yAtMinY) {
			screenTop = ((float) -this.size2d + yAtMaxY) * (float) this.glToolkit.projectionScaleY;
			screenBottom = (float) this.glToolkit.projectionScaleY * ((float) this.size2d + yAtMinY);
		} else {
			screenBottom = (float) this.glToolkit.projectionScaleY * (yAtMaxY + (float) this.size2d);
			screenTop = (float) this.glToolkit.projectionScaleY * (yAtMinY - (float) this.size2d);
		}
		if (screenTop / (float) zoom >= this.glToolkit.viewY2 || this.glToolkit.viewY1 >= screenBottom / (float) zoom) {
			return;
		}
		if (cylinder != null || this.billboardFaces != null) {
			GlModel.modelViewYX = modelMatrix.xA * viewMatrix.yA + viewMatrix.yB * modelMatrix.yA + modelMatrix.zA * viewMatrix.yC;
			GlModel.modelViewXZ = viewMatrix.xC * modelMatrix.zC + modelMatrix.xC * viewMatrix.xA + viewMatrix.xB * modelMatrix.yC;
			GlModel.modelViewZX = viewMatrix.zC * modelMatrix.zA + modelMatrix.xA * viewMatrix.zA + viewMatrix.zB * modelMatrix.yA;
			GlModel.modelViewYZ = modelMatrix.yC * viewMatrix.yB + modelMatrix.xC * viewMatrix.yA + viewMatrix.yC * modelMatrix.zC;
			GlModel.modelViewZZ = viewMatrix.zC * modelMatrix.zC + modelMatrix.yC * viewMatrix.zB + modelMatrix.xC * viewMatrix.zA;
			GlModel.modelViewXX = modelMatrix.zA * viewMatrix.xC + modelMatrix.yA * viewMatrix.xB + modelMatrix.xA * viewMatrix.xA;
		}
		if (cylinder != null) {
			@Pc(498) int centerX = this.maxX + this.minX >> 1;
			@Pc(506) int centerZ = this.minZ + this.maxZ >> 1;
			@Pc(525) int bottomX = (int) ((float) this.minY * GlModel.modelViewXY + (float) centerX * GlModel.modelViewXX + GlModel.modelViewXW + GlModel.modelViewXZ * (float) centerZ);
			@Pc(544) int bottomY = (int) (GlModel.modelViewYW + (float) centerX * GlModel.modelViewYX + GlModel.modelViewYY * (float) this.minY + GlModel.modelViewYZ * (float) centerZ);
			@Pc(563) int bottomZ = (int) (GlModel.modelViewZW + (float) centerX * GlModel.modelViewZX + GlModel.modelViewZY * (float) this.minY + GlModel.modelViewZZ * (float) centerZ);
			@Pc(582) int topX = (int) (GlModel.modelViewXZ * (float) centerZ + GlModel.modelViewXY * (float) this.maxY + GlModel.modelViewXW + GlModel.modelViewXX * (float) centerX);
			@Pc(601) int topY = (int) (GlModel.modelViewYW + GlModel.modelViewYX * (float) centerX + GlModel.modelViewYY * (float) this.maxY + (float) centerZ * GlModel.modelViewYZ);
			cylinder.left = this.glToolkit.projectionCenterX + bottomX * this.glToolkit.projectionScaleX / zoom;
			cylinder.bottom = bottomY * this.glToolkit.projectionScaleY / zoom + this.glToolkit.projectionCenterY;
			cylinder.top = this.glToolkit.projectionCenterY + topY * this.glToolkit.projectionScaleY / zoom;
			@Pc(660) int topZ = (int) ((float) centerZ * GlModel.modelViewZZ + GlModel.modelViewZY * (float) this.maxY + GlModel.modelViewZX * (float) centerX + GlModel.modelViewZW);
			cylinder.right = this.glToolkit.projectionCenterX + topX * this.glToolkit.projectionScaleX / zoom;
			if (this.glToolkit.zNear <= bottomZ || topZ >= this.glToolkit.zNear) {
				cylinder.positionCalculated = true;
				cylinder.size = this.glToolkit.projectionCenterX + this.glToolkit.projectionScaleX * (this.size2d + bottomX) / zoom - cylinder.left;
			}
		}
		this.glToolkit.setOrthoZoom((float) zoom);
		this.glToolkit.setupOrthoMatrixRendering();
		this.glToolkit.multiMatrix(modelMatrix);
		this.drawOpaqueFaces();
		this.glToolkit.popMatrix();
		this.renderBillboards();
	}

	@OriginalMember(owner = "client!paa", name = "da", descriptor = "()I")
	@Override
	public int da() {
		return this.contrast;
	}

	@OriginalMember(owner = "client!paa", name = "NA", descriptor = "()Z")
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
		ModelTransformOriginY.originY = 0;
		GlModel.originX = 0;
		GlModel.originZ = 0;
		return true;
	}

	@OriginalMember(owner = "client!paa", name = "RA", descriptor = "()I")
	@Override
	public int RA() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.maxX;
	}

	@OriginalMember(owner = "client!paa", name = "G", descriptor = "()I")
	@Override
	public int G() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.maxZ;
	}

	@OriginalMember(owner = "client!paa", name = "p", descriptor = "(IILclient!s;Lclient!s;III)V")
	@Override
	public void p(@OriginalArg(0) int contourType, @OriginalArg(1) int contourModifier, @OriginalArg(2) Ground ground, @OriginalArg(3) Ground ground2, @OriginalArg(4) int x, @OriginalArg(5) int y, @OriginalArg(6) int z) {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		@Pc(16) int startX = this.minX + x;
		@Pc(22) int endX = x + this.maxX;
		@Pc(28) int startZ = z + this.minZ;
		@Pc(33) int endZ = this.maxZ + z;
		if ((contourType == 1 || contourType == 2 || contourType == 3 || contourType == 5) && (startX < 0 || ground.width <= ground.tileUnits + endX >> ground.tileScale || startZ < 0 || ground.length <= endZ + ground.tileUnits >> ground.tileScale)) {
			return;
		}
		if (contourType == 4 || contourType == 5) {
			if (ground2 == null) {
				return;
			}
			if (startX < 0 || ground2.width <= endX + ground2.tileUnits >> ground2.tileScale || startZ < 0 || ground2.length <= ground2.tileUnits + endZ >> ground2.tileScale) {
				return;
			}
		} else {
			startX >>= ground.tileScale;
			endX = endX + ground.tileUnits - 1 >> ground.tileScale;
			startZ >>= ground.tileScale;
			endZ = endZ + ground.tileUnits - 1 >> ground.tileScale;
			if (ground.getHeight(startZ, startX) == y && ground.getHeight(startZ, endX) == y && y == ground.getHeight(endZ, startX) && y == ground.getHeight(endZ, endX)) {
				return;
			}
		}
		@Pc(199) int local199;
		if (contourType == 1) {
			for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
				this.verticesY[vertexIndex] = this.verticesY[vertexIndex] + ground.averageHeight(this.verticesX[vertexIndex] + x, z + this.verticesZ[vertexIndex]) - y;
			}
		} else {
			@Pc(257) int local257;
			@Pc(265) int local265;
			if (contourType == 2) {
				@Pc(463) short modelMinY = this.minY;
				if (modelMinY == 0) {
					return;
				}
				for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
					int heightRatio = (this.verticesY[vertexIndex] << 16) / modelMinY;
					if (contourModifier > heightRatio) {
						this.verticesY[vertexIndex] -= -((ground.averageHeight(this.verticesX[vertexIndex] + x, z + this.verticesZ[vertexIndex]) - y) * (-heightRatio + contourModifier) / contourModifier);
					}
				}
			} else {
				@Pc(273) int local273;
				if (contourType == 3) {
					int sizeX = (contourModifier & 0xFF) * 4;
					int sizeZ = (contourModifier >> 8 & 0xFF) * 4;
					int maxRotateX = (contourModifier >> 16 & 0xFF) << 6;
					int maxRotateZ = (contourModifier >> 24 & 0xFF) << 6;
					if (x - (sizeX >> 1) < 0 || (sizeX >> 1) + (x + ground.tileUnits) >= ground.width << ground.tileScale || z - (sizeZ >> 1) < 0 || (sizeZ >> 1) + z + ground.tileUnits >= ground.length << ground.tileScale) {
						return;
					}
					this.hillChange(sizeX, maxRotateZ, x, sizeZ, y, maxRotateX, ground, z);
				} else if (contourType == 4) {
					int modelHeight = this.maxY - this.minY;
					for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
						this.verticesY[vertexIndex] -= y - ground2.averageHeight(this.verticesX[vertexIndex] + x, this.verticesZ[vertexIndex] + z) - modelHeight;
					}
				} else if (contourType == 5) {
					int modelHeight = this.maxY - this.minY;
					for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
						int worldX = this.verticesX[vertexIndex] + x;
						int worldZ = this.verticesZ[vertexIndex] + z;
						@Pc(371) int height = ground.averageHeight(worldX, worldZ);
						@Pc(377) int upperHeight = ground2.averageHeight(worldX, worldZ);
						@Pc(385) int heightDelta = height - contourModifier - upperHeight;
						this.verticesY[vertexIndex] = height + ((this.verticesY[vertexIndex] << 8) / modelHeight * heightDelta >> 8) - y;
					}
				}
			}
		}
		this.boundsCalculated = false;
		if (this.vertexBuffer != null) {
			this.vertexBuffer.buffer = null;
		}
	}

	@OriginalMember(owner = "client!paa", name = "aa", descriptor = "(SS)V")
	@Override
	public void aa(@OriginalArg(0) short src, @OriginalArg(1) short dst) {
		@Pc(9) MaterialProvider textureSource = this.glToolkit.materialProvider;
		for (@Pc(11) int i = 0; i < this.numFaces; i++) {
			if (this.facesMaterial[i] == src) {
				this.facesMaterial[i] = dst;
			}
		}
		@Pc(31) byte alpha = 0;
		@Pc(33) byte brightness = 0;
		if (src != -1) {
			@Pc(44) MaterialType textureMetrics = textureSource.getMetrics(src & 0xFFFF);
			brightness = textureMetrics.brightness;
			alpha = textureMetrics.alpha;
		}
		@Pc(52) byte newAlpha = 0;
		@Pc(54) byte newBrightness = 0;
		if (dst != -1) {
			@Pc(64) MaterialType textureMetrics2 = textureSource.getMetrics(dst & 0xFFFF);
			if (textureMetrics2.speedU != 0 || textureMetrics2.speedV != 0) {
				this.textureAnimating = true;
			}
			newAlpha = textureMetrics2.alpha;
			newBrightness = textureMetrics2.brightness;
		}
		if (!(newBrightness != brightness | newAlpha != alpha)) {
			return;
		}
		if (this.billboardFaces != null) {
			for (@Pc(106) int j = 0; j < this.billboardCount; j++) {
				@Pc(113) GlBillboardFace glBillboardFace = this.billboardFaces[j];
				@Pc(118) BillboardSurfaceGL glBillboardAttributes = this.billboardAttributes[j];
				glBillboardAttributes.diffuseColor = ColorUtil.hslToRgb[this.facesColor[glBillboardFace.face] & 0xFFFF] & 0xFFFFFF | glBillboardAttributes.diffuseColor & 0xFF000000;
			}
		}
		if (this.colorsBuffer != null) {
			this.colorsBuffer.buffer = null;
			return;
		}
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(IZ)V")
	private void upload(@OriginalArg(1) boolean checkFlags) {
		@Pc(16) boolean uploadColours = this.colorsBuffer != null && this.colorsBuffer.buffer == null;
		@Pc(27) boolean uploadNormals = this.normalsBuffer != null && this.normalsBuffer.buffer == null;
		@Pc(38) boolean uploadVertices = this.vertexBuffer != null && this.vertexBuffer.buffer == null;
		@Pc(49) boolean uploadTexCoords = this.textureCoordinatesBuffer != null && this.textureCoordinatesBuffer.buffer == null;
		if (checkFlags) {
			uploadTexCoords &= (this.renderState & 0x8) != 0;
			uploadNormals &= (this.renderState & 0x4) != 0;
			uploadColours &= (this.renderState & 0x2) != 0;
			uploadVertices &= (this.renderState & 0x1) != 0;
		}
		@Pc(97) byte stride = 0;
		@Pc(105) byte coloursOffset = 0;
		@Pc(107) byte normalsOffset = 0;
		if (uploadVertices) {
			stride = 12;
		}
		@Pc(118) byte texCoordsOffset = 0;
		if (uploadColours) {
			coloursOffset = stride;
			stride = (byte) (stride + 4);
		}
		if (uploadNormals) {
			normalsOffset = stride;
			stride = (byte) (stride + 12);
		}
		if (uploadTexCoords) {
			texCoordsOffset = stride;
			stride = (byte) (stride + 8);
		}
		if (stride == 0) {
			return;
		}
		if (this.glToolkit.textureBuffer.buffer.length < this.numNormals * stride) {
			this.glToolkit.textureBuffer = new GlBitBuffer(stride * (this.numNormals + 100));
		} else {
			this.glToolkit.textureBuffer.currentPosition = 0;
		}
		@Pc(185) GlBitBuffer gpuPacket = this.glToolkit.textureBuffer;
		@Pc(193) int local193;
		@Pc(202) int local202;
		@Pc(230) int local230;
		@Pc(239) int local239;
		if (uploadVertices) {
			@Pc(209) int yBits;
			@Pc(216) int zBits;
			@Pc(221) int start;
			@Pc(228) int end;
			if (this.glToolkit.bigEndianess) {
				for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
					int xBits = Stream.floatToRawIntBits((float) this.verticesX[vertexIndex]);
					yBits = Stream.floatToRawIntBits((float) this.verticesY[vertexIndex]);
					zBits = Stream.floatToRawIntBits((float) this.verticesZ[vertexIndex]);
					start = this.triangles[vertexIndex];
					end = this.triangles[vertexIndex + 1];
					for (int index = start; index < end; index++) {
						int normalIndex = this.vertexNormalIndices[index] - 1;
						if (normalIndex == -1) {
							break;
						}
						gpuPacket.currentPosition = stride * normalIndex;
						gpuPacket.writeInt(xBits);
						gpuPacket.writeInt(yBits);
						gpuPacket.writeInt(zBits);
					}
				}
			} else {
				for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
					int xBits = Stream.floatToRawIntBits((float) this.verticesX[vertexIndex]);
					yBits = Stream.floatToRawIntBits((float) this.verticesY[vertexIndex]);
					zBits = Stream.floatToRawIntBits((float) this.verticesZ[vertexIndex]);
					start = this.triangles[vertexIndex];
					end = this.triangles[vertexIndex + 1];
					for (int index = start; index < end; index++) {
						int normalIndex = this.vertexNormalIndices[index] - 1;
						if (normalIndex == -1) {
							break;
						}
						gpuPacket.currentPosition = stride * normalIndex;
						gpuPacket.writeIntLE(xBits);
						gpuPacket.writeIntLE(yBits);
						gpuPacket.writeIntLE(zBits);
					}
				}
			}
		}
		@Pc(487) float local487;
		@Pc(366) short[] srcNormalsX;
		@Pc(374) short[] srcNormalsY;
		@Pc(370) short[] srcNormalsZ;
		@Pc(378) byte[] magnitude;
		@Pc(528) float local528;
		if (uploadColours) {
			if (this.normalsBuffer == null) {
				if (this.mergedNormals == null) {
					srcNormalsZ = this.normalsZ;
					magnitude = this.magnitudes;
					srcNormalsY = this.normalsY;
					srcNormalsX = this.normalsX;
				} else {
					srcNormalsX = this.mergedNormals.normalsX;
					srcNormalsZ = this.mergedNormals.normalsZ;
					srcNormalsY = this.mergedNormals.normalsY;
					magnitude = this.mergedNormals.magnitude;
				}
				@Pc(398) float sunX = this.glToolkit.sunDirection[0];
				@Pc(404) float sunY = this.glToolkit.sunDirection[1];
				@Pc(410) float sunZ = this.glToolkit.sunDirection[2];
				@Pc(414) float sunBrightness = this.glToolkit.sunBrightness;
				@Pc(424) float lightScale = this.glToolkit.sunColorIntensity * 768.0F / (float) this.contrast;
				@Pc(434) float antiLightScale = this.glToolkit.antiSunColorIntensity * 768.0F / (float) this.contrast;
				for (@Pc(436) int i = 0; i < this.numFaces; i++) {
					@Pc(456) int color = this.hslToRgb(this.ambient, this.facesAlpha[i], this.facesMaterial[i], this.facesColor[i]);
					@Pc(465) float baseRed = this.glToolkit.sunColorRed * (float) (color >>> 24);
					@Pc(476) float baseBlue = (float) (color >> 8 & 0xFF) * this.glToolkit.sunColorBlue;
					float baseGreen = (float) (color >> 16 & 0xFF) * this.glToolkit.sunColorGreen;
					@Pc(492) short normalIndex = this.facesA[i];
					@Pc(497) short normalMagnitude = (short) magnitude[normalIndex];
					float lightDot;
					if (normalMagnitude == 0) {
						lightDot = ((float) srcNormalsZ[normalIndex] * sunZ + sunX * (float) srcNormalsX[normalIndex] + (float) srcNormalsY[normalIndex] * sunY) * 0.0026041667F;
					} else {
						lightDot = (sunY * (float) srcNormalsY[normalIndex] + sunX * (float) srcNormalsX[normalIndex] + sunZ * (float) srcNormalsZ[normalIndex]) / (float) (normalMagnitude * 256);
					}
					@Pc(565) float intensity = sunBrightness + lightDot * (lightDot < 0.0F ? antiLightScale : lightScale);
					@Pc(570) int red = (int) (intensity * baseRed);
					@Pc(575) int green = (int) (intensity * baseGreen);
					if (red < 0) {
						red = 0;
					} else if (red > 255) {
						red = 255;
					}
					@Pc(596) int blue = (int) (intensity * baseBlue);
					if (green < 0) {
						green = 0;
					} else if (green > 255) {
						green = 255;
					}
					gpuPacket.currentPosition = normalIndex * stride + coloursOffset;
					if (blue < 0) {
						blue = 0;
					} else if (blue > 255) {
						blue = 255;
					}
					gpuPacket.writeByte(red);
					gpuPacket.writeByte(green);
					gpuPacket.writeByte(blue);
					gpuPacket.writeByte(255 - (this.facesAlpha[i] & 0xFF));
					normalIndex = this.facesB[i];
					normalMagnitude = magnitude[normalIndex];
					if (normalMagnitude == 0) {
						lightDot = (sunX * (float) srcNormalsX[normalIndex] + (float) srcNormalsY[normalIndex] * sunY + sunZ * (float) srcNormalsZ[normalIndex]) * 0.0026041667F;
					} else {
						lightDot = ((float) srcNormalsZ[normalIndex] * sunZ + sunX * (float) srcNormalsX[normalIndex] + (float) srcNormalsY[normalIndex] * sunY) / (float) (normalMagnitude * 256);
					}
					intensity = sunBrightness + lightDot * (lightDot < 0.0F ? antiLightScale : lightScale);
					red = (int) (baseRed * intensity);
					green = (int) (intensity * baseGreen);
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
					blue = (int) (intensity * baseBlue);
					gpuPacket.currentPosition = coloursOffset + normalIndex * stride;
					if (blue < 0) {
						blue = 0;
					} else if (blue > 255) {
						blue = 255;
					}
					gpuPacket.writeByte(red);
					gpuPacket.writeByte(green);
					gpuPacket.writeByte(blue);
					gpuPacket.writeByte(255 - (this.facesAlpha[i] & 0xFF));
					normalIndex = this.facesC[i];
					normalMagnitude = magnitude[normalIndex];
					if (normalMagnitude == 0) {
						lightDot = ((float) srcNormalsZ[normalIndex] * sunZ + (float) srcNormalsY[normalIndex] * sunY + sunX * (float) srcNormalsX[normalIndex]) * 0.0026041667F;
					} else {
						lightDot = ((float) srcNormalsZ[normalIndex] * sunZ + (float) srcNormalsY[normalIndex] * sunY + (float) srcNormalsX[normalIndex] * sunX) / (float) (normalMagnitude * 256);
					}
					intensity = sunBrightness + (lightDot < 0.0F ? antiLightScale : lightScale) * lightDot;
					red = (int) (intensity * baseRed);
					green = (int) (intensity * baseGreen);
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
					blue = (int) (baseBlue * intensity);
					if (blue < 0) {
						blue = 0;
					} else if (blue > 255) {
						blue = 255;
					}
					gpuPacket.currentPosition = stride * normalIndex + coloursOffset;
					gpuPacket.writeByte(red);
					gpuPacket.writeByte(green);
					gpuPacket.writeByte(blue);
					gpuPacket.writeByte(255 - (this.facesAlpha[i] & 0xFF));
				}
			} else {
				for (int faceIndex = 0; faceIndex < this.numFaces; faceIndex++) {
					int color = this.hslToRgb(this.ambient, this.facesAlpha[faceIndex], this.facesMaterial[faceIndex], this.facesColor[faceIndex]);
					gpuPacket.currentPosition = coloursOffset + this.facesA[faceIndex] * stride;
					gpuPacket.writeInt(color);
					gpuPacket.currentPosition = coloursOffset + this.facesB[faceIndex] * stride;
					gpuPacket.writeInt(color);
					gpuPacket.currentPosition = coloursOffset + stride * this.facesC[faceIndex];
					gpuPacket.writeInt(color);
				}
			}
		}
		if (uploadNormals) {
			if (this.mergedNormals == null) {
				srcNormalsX = this.normalsX;
				srcNormalsY = this.normalsY;
				magnitude = this.magnitudes;
				srcNormalsZ = this.normalsZ;
			} else {
				magnitude = this.mergedNormals.magnitude;
				srcNormalsY = this.mergedNormals.normalsY;
				srcNormalsZ = this.mergedNormals.normalsZ;
				srcNormalsX = this.mergedNormals.normalsX;
			}
			@Pc(1097) float normalScale = 3.0F / (float) this.contrast;
			gpuPacket.currentPosition = normalsOffset;
			float defaultNormalScale = 3.0F / (float) (this.contrast / 2 + this.contrast);
			if (this.glToolkit.bigEndianess) {
				for (int normalIndex = 0; normalIndex < this.numNormals; normalIndex++) {
					int normalMagnitude = magnitude[normalIndex] & 0xFF;
					if (normalMagnitude == 0) {
						gpuPacket.writeFloatBigEndian(defaultNormalScale * (float) srcNormalsX[normalIndex]);
						gpuPacket.writeFloatBigEndian((float) srcNormalsY[normalIndex] * defaultNormalScale);
						gpuPacket.writeFloatBigEndian(defaultNormalScale * (float) srcNormalsZ[normalIndex]);
					} else {
						float magnitudeScale = normalScale / (float) normalMagnitude;
						gpuPacket.writeFloatBigEndian((float) srcNormalsX[normalIndex] * magnitudeScale);
						gpuPacket.writeFloatBigEndian(magnitudeScale * (float) srcNormalsY[normalIndex]);
						gpuPacket.writeFloatBigEndian((float) srcNormalsZ[normalIndex] * magnitudeScale);
					}
					gpuPacket.currentPosition += stride - 12;
				}
			} else {
				for (int normalIndex = 0; normalIndex < this.numNormals; normalIndex++) {
					int normalMagnitude = magnitude[normalIndex] & 0xFF;
					if (normalMagnitude == 0) {
						gpuPacket.writeFloatLittleEndian((float) srcNormalsX[normalIndex] * defaultNormalScale);
						gpuPacket.writeFloatLittleEndian((float) srcNormalsY[normalIndex] * defaultNormalScale);
						gpuPacket.writeFloatLittleEndian(defaultNormalScale * (float) srcNormalsZ[normalIndex]);
					} else {
						float magnitudeScale = normalScale / (float) normalMagnitude;
						gpuPacket.writeFloatLittleEndian((float) srcNormalsX[normalIndex] * magnitudeScale);
						gpuPacket.writeFloatLittleEndian(magnitudeScale * (float) srcNormalsY[normalIndex]);
						gpuPacket.writeFloatLittleEndian(magnitudeScale * (float) srcNormalsZ[normalIndex]);
					}
					gpuPacket.currentPosition += stride - 12;
				}
			}
		}
		if (uploadTexCoords) {
			gpuPacket.currentPosition = texCoordsOffset;
			if (this.glToolkit.bigEndianess) {
				for (int normalIndex = 0; normalIndex < this.numNormals; normalIndex++) {
					gpuPacket.writeFloatBigEndian(this.textureU[normalIndex]);
					gpuPacket.writeFloatBigEndian(this.textureV[normalIndex]);
					gpuPacket.currentPosition += stride - 8;
				}
			} else {
				for (int normalIndex = 0; normalIndex < this.numNormals; normalIndex++) {
					gpuPacket.writeFloatLittleEndian(this.textureU[normalIndex]);
					gpuPacket.writeFloatLittleEndian(this.textureV[normalIndex]);
					gpuPacket.currentPosition += stride - 8;
				}
			}
		}
		gpuPacket.currentPosition = this.numNormals * stride;
		@Pc(1413) ArrayBufferGL arrayBuffer;
		if (checkFlags) {
			if (this.dynamicArrayBuffer == null) {
				this.dynamicArrayBuffer = this.glToolkit.createArrayBuffer(true, stride, gpuPacket.buffer, gpuPacket.currentPosition);
			} else {
				this.dynamicArrayBuffer.putArray(stride, gpuPacket.currentPosition, gpuPacket.buffer);
			}
			arrayBuffer = this.dynamicArrayBuffer;
			this.renderState = 0;
		} else {
			arrayBuffer = this.glToolkit.createArrayBuffer(false, stride, gpuPacket.buffer, gpuPacket.currentPosition);
			this.updateRequired = true;
		}
		if (uploadVertices) {
			this.vertexBuffer.offset = 0;
			this.vertexBuffer.buffer = arrayBuffer;
		}
		if (uploadTexCoords) {
			this.textureCoordinatesBuffer.offset = texCoordsOffset;
			this.textureCoordinatesBuffer.buffer = arrayBuffer;
		}
		if (uploadColours) {
			this.colorsBuffer.offset = coloursOffset;
			this.colorsBuffer.buffer = arrayBuffer;
		}
		if (uploadNormals) {
			this.normalsBuffer.offset = normalsOffset;
			this.normalsBuffer.buffer = arrayBuffer;
		}
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(Lclient!bca;Lclient!iw;I)V")
	@Override
	public void render(@OriginalArg(0) Matrix matrix, @OriginalArg(1) BoundingBoxPolygon cylinder, @OriginalArg(2) int flags) {
		if (this.numNormals == 0) {
			return;
		}
		@Pc(13) Matrix43 viewMatrix = this.glToolkit.viewMatrix43;
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		@Pc(22) Matrix43 modelMatrix = (Matrix43) matrix;
		GlModel.modelViewZY = modelMatrix.zB * viewMatrix.zC + modelMatrix.xB * viewMatrix.zA + modelMatrix.yB * viewMatrix.zB;
		GlModel.modelViewZW = viewMatrix.zD + viewMatrix.zA * modelMatrix.xD + modelMatrix.yD * viewMatrix.zB + modelMatrix.zD * viewMatrix.zC;
		@Pc(69) float depthAtMinY = GlModel.modelViewZW + GlModel.modelViewZY * (float) this.minY;
		@Pc(77) float depthAtMaxY = GlModel.modelViewZY * (float) this.maxY + GlModel.modelViewZW;
		@Pc(87) float nearDepth;
		@Pc(93) float farDepth;
		if (depthAtMinY > depthAtMaxY) {
			nearDepth = depthAtMaxY - (float) this.size2d;
			farDepth = depthAtMinY + (float) this.size2d;
		} else {
			nearDepth = (float) -this.size2d + depthAtMinY;
			farDepth = depthAtMaxY + (float) this.size2d;
		}
		if (this.glToolkit.projectedFarClipDistance <= nearDepth || (float) this.glToolkit.zNear >= farDepth) {
			return;
		}
		GlModel.modelViewXY = viewMatrix.xB * modelMatrix.yB + modelMatrix.xB * viewMatrix.xA + viewMatrix.xC * modelMatrix.zB;
		GlModel.modelViewXW = viewMatrix.xD + viewMatrix.xC * modelMatrix.zD + viewMatrix.xA * modelMatrix.xD + modelMatrix.yD * viewMatrix.xB;
		@Pc(170) float xAtMinY = (float) this.minY * GlModel.modelViewXY + GlModel.modelViewXW;
		@Pc(178) float xAtMaxY = (float) this.maxY * GlModel.modelViewXY + GlModel.modelViewXW;
		@Pc(205) float screenLeft;
		@Pc(193) float screenRight;
		if (xAtMinY > xAtMaxY) {
			screenLeft = (float) this.glToolkit.projectionScaleX * ((float) -this.size2d + xAtMaxY);
			screenRight = (float) this.glToolkit.projectionScaleX * ((float) this.size2d + xAtMinY);
		} else {
			screenRight = (float) this.glToolkit.projectionScaleX * (xAtMaxY + (float) this.size2d);
			screenLeft = ((float) -this.size2d + xAtMinY) * (float) this.glToolkit.projectionScaleX;
		}
		if (this.glToolkit.viewX2 <= screenLeft / farDepth || screenRight / farDepth <= this.glToolkit.viewX1) {
			return;
		}
		GlModel.modelViewYW = modelMatrix.yD * viewMatrix.yB + modelMatrix.xD * viewMatrix.yA + viewMatrix.yC * modelMatrix.zD + viewMatrix.yD;
		GlModel.modelViewYY = viewMatrix.yA * modelMatrix.xB + viewMatrix.yB * modelMatrix.yB + viewMatrix.yC * modelMatrix.zB;
		@Pc(295) float yAtMinY = GlModel.modelViewYW + GlModel.modelViewYY * (float) this.minY;
		@Pc(303) float yAtMaxY = GlModel.modelViewYW + (float) this.maxY * GlModel.modelViewYY;
		@Pc(329) float screenTop;
		@Pc(318) float screenBottom;
		if (yAtMinY > yAtMaxY) {
			screenBottom = ((float) this.size2d + yAtMinY) * (float) this.glToolkit.projectionScaleY;
			screenTop = (float) this.glToolkit.projectionScaleY * (yAtMaxY - (float) this.size2d);
		} else {
			screenBottom = ((float) this.size2d + yAtMaxY) * (float) this.glToolkit.projectionScaleY;
			screenTop = (float) this.glToolkit.projectionScaleY * (yAtMinY - (float) this.size2d);
		}
		if (screenTop / farDepth >= this.glToolkit.viewY2 || this.glToolkit.viewY1 >= screenBottom / farDepth) {
			return;
		}
		if (cylinder != null || this.billboardFaces != null) {
			GlModel.modelViewZZ = modelMatrix.yC * viewMatrix.zB + modelMatrix.xC * viewMatrix.zA + modelMatrix.zC * viewMatrix.zC;
			GlModel.modelViewZX = viewMatrix.zC * modelMatrix.zA + modelMatrix.xA * viewMatrix.zA + viewMatrix.zB * modelMatrix.yA;
			GlModel.modelViewXZ = viewMatrix.xC * modelMatrix.zC + modelMatrix.yC * viewMatrix.xB + modelMatrix.xC * viewMatrix.xA;
			GlModel.modelViewYX = viewMatrix.yA * modelMatrix.xA + modelMatrix.yA * viewMatrix.yB + modelMatrix.zA * viewMatrix.yC;
			GlModel.modelViewXX = modelMatrix.yA * viewMatrix.xB + viewMatrix.xA * modelMatrix.xA + viewMatrix.xC * modelMatrix.zA;
			GlModel.modelViewYZ = modelMatrix.zC * viewMatrix.yC + viewMatrix.yB * modelMatrix.yC + viewMatrix.yA * modelMatrix.xC;
		}
		if (cylinder != null) {
			@Pc(488) boolean clipped = false;
			@Pc(490) boolean visible = true;
			@Pc(499) int centerX = this.minX + this.maxX >> 1;
			@Pc(508) int centerZ = this.minZ + this.maxZ >> 1;
			@Pc(527) int bottomX = (int) ((float) centerZ * GlModel.modelViewXZ + GlModel.modelViewXX * (float) centerX + GlModel.modelViewXW + (float) this.minY * GlModel.modelViewXY);
			@Pc(546) int bottomY = (int) (GlModel.modelViewYW + (float) centerX * GlModel.modelViewYX + (float) this.minY * GlModel.modelViewYY + (float) centerZ * GlModel.modelViewYZ);
			@Pc(565) int bottomZ = (int) (GlModel.modelViewZX * (float) centerX + GlModel.modelViewZW + GlModel.modelViewZY * (float) this.minY + GlModel.modelViewZZ * (float) centerZ);
			if (bottomZ >= this.glToolkit.zNear) {
				cylinder.left = bottomX * this.glToolkit.projectionScaleX / bottomZ + this.glToolkit.projectionCenterX;
				cylinder.bottom = bottomY * this.glToolkit.projectionScaleY / bottomZ + this.glToolkit.projectionCenterY;
			} else {
				clipped = true;
			}
			@Pc(623) int topX = (int) (GlModel.modelViewXW + GlModel.modelViewXX * (float) centerX + GlModel.modelViewXY * (float) this.maxY + GlModel.modelViewXZ * (float) centerZ);
			@Pc(642) int topY = (int) ((float) centerZ * GlModel.modelViewYZ + GlModel.modelViewYY * (float) this.maxY + GlModel.modelViewYW + GlModel.modelViewYX * (float) centerX);
			@Pc(661) int topZ = (int) (GlModel.modelViewZZ * (float) centerZ + (float) centerX * GlModel.modelViewZX + GlModel.modelViewZW + (float) this.maxY * GlModel.modelViewZY);
			if (topZ >= this.glToolkit.zNear) {
				cylinder.right = topX * this.glToolkit.projectionScaleX / topZ + this.glToolkit.projectionCenterX;
				cylinder.top = topY * this.glToolkit.projectionScaleY / topZ + this.glToolkit.projectionCenterY;
			} else {
				clipped = true;
			}
			if (clipped) {
				if (this.glToolkit.zNear > bottomZ && topZ < this.glToolkit.zNear) {
					visible = false;
				} else {
					@Pc(735) int clipRatio;
					@Pc(746) int clippedX;
					@Pc(772) int clippedY;
					if (this.glToolkit.zNear > bottomZ) {
						clipRatio = (topZ - this.glToolkit.zNear << 16) / (topZ - bottomZ);
						clippedX = ((topX - bottomX) * clipRatio >> 16) + topX;
						cylinder.left = this.glToolkit.projectionCenterX + clippedX * this.glToolkit.projectionScaleX / this.glToolkit.zNear;
						clippedY = (clipRatio * (topY - bottomY) >> 16) + topY;
						cylinder.bottom = clippedY * this.glToolkit.projectionScaleY / this.glToolkit.zNear + this.glToolkit.projectionCenterY;
					} else if (this.glToolkit.zNear > topZ) {
						clipRatio = (bottomZ - this.glToolkit.zNear << 16) / (bottomZ - topZ);
						clippedX = bottomX + (clipRatio * (bottomX - topX) >> 16);
						clippedY = ((bottomY - topY) * clipRatio >> 16) + bottomY;
						cylinder.left = clippedX * this.glToolkit.projectionScaleX / this.glToolkit.zNear + this.glToolkit.projectionCenterX;
						cylinder.bottom = this.glToolkit.projectionCenterY + this.glToolkit.projectionScaleY * clippedY / this.glToolkit.zNear;
					}
				}
			}
			if (visible) {
				if (topZ < bottomZ) {
					cylinder.size = this.glToolkit.projectionCenterX + this.glToolkit.projectionScaleX * (bottomX + this.size2d) / bottomZ - cylinder.left;
				} else {
					cylinder.size = this.glToolkit.projectionCenterX + this.glToolkit.projectionScaleX * (this.size2d + topX) / topZ - cylinder.right;
				}
				cylinder.positionCalculated = true;
			}
		}
		this.glToolkit.setupMatrixRendering();
		this.glToolkit.multiMatrix(modelMatrix);
		this.drawOpaqueFaces();
		this.glToolkit.popMatrix();
		this.renderBillboards();
	}

	@OriginalMember(owner = "client!paa", name = "WA", descriptor = "()I")
	@Override
	public int WA() {
		return this.ambient;
	}

	@OriginalMember(owner = "client!paa", name = "c", descriptor = "(B)V")
	private void releaseUploadedData() {
		if (!this.updateRequired) {
			return;
		}
		this.updateRequired = false;
		if (this.emissiveTriangles == null && this.effectiveVertices == null && this.billboardFaces == null) {
			if (this.verticesX != null && !GlModel.retainsVerticesX(this.functionMask, this.preferenceFlags)) {
				if (this.vertexBuffer != null && this.vertexBuffer.buffer == null) {
					this.updateRequired = true;
				} else {
					if (!this.boundsCalculated) {
						this.calculateBounds();
					}
					this.verticesX = null;
				}
			}
			if (this.verticesY != null && !GlModel.retainsVerticesY(this.preferenceFlags, this.functionMask)) {
				if (this.vertexBuffer != null && this.vertexBuffer.buffer == null) {
					this.updateRequired = true;
				} else {
					if (!this.boundsCalculated) {
						this.calculateBounds();
					}
					this.verticesY = null;
				}
			}
			if (this.verticesZ != null && !GlModel.retainsVerticesZ(this.functionMask, this.preferenceFlags)) {
				if (this.vertexBuffer != null && this.vertexBuffer.buffer == null) {
					this.updateRequired = true;
				} else {
					if (!this.boundsCalculated) {
						this.calculateBounds();
					}
					this.verticesZ = null;
				}
			}
		}
		if (this.vertexNormalIndices != null && this.verticesX == null && this.verticesY == null && this.verticesZ == null) {
			this.vertexNormalIndices = null;
			this.triangles = null;
		}
		if (this.magnitudes != null && !GlModel.retainsNormals(this.functionMask, this.preferenceFlags)) {
			if (this.normalsBuffer == null) {
				if (this.colorsBuffer != null && this.colorsBuffer.buffer == null) {
					this.updateRequired = true;
				} else {
					this.magnitudes = null;
					this.normalsX = this.normalsY = this.normalsZ = null;
				}
			} else if (this.normalsBuffer.buffer == null) {
				this.updateRequired = true;
			} else {
				this.magnitudes = null;
				this.normalsX = this.normalsY = this.normalsZ = null;
			}
		}
		if (this.facesColor != null && !GlModel.retainsFaceColors(this.functionMask, this.preferenceFlags)) {
			if (this.colorsBuffer != null && this.colorsBuffer.buffer == null) {
				this.updateRequired = true;
			} else {
				this.facesColor = null;
			}
		}
		if (this.facesAlpha != null && !GlModel.retainsFaceAlphas(this.preferenceFlags, this.functionMask)) {
			if (this.colorsBuffer != null && this.colorsBuffer.buffer == null) {
				this.updateRequired = true;
			} else {
				this.facesAlpha = null;
			}
		}
		if (this.textureU != null && !GlModel.retainsTextureCoordinates(this.functionMask, this.preferenceFlags)) {
			if (this.textureCoordinatesBuffer != null && this.textureCoordinatesBuffer.buffer == null) {
				this.updateRequired = true;
			} else {
				this.textureU = this.textureV = null;
			}
		}
		if (this.facesMaterial != null && !GlModel.retainsFaceMaterials(this.preferenceFlags, this.functionMask)) {
			if (this.colorsBuffer != null && this.colorsBuffer.buffer == null) {
				this.updateRequired = true;
			} else {
				this.facesMaterial = null;
			}
		}
		if (this.facesA != null && !GlModel.retainsFaceIndices(this.functionMask, this.preferenceFlags)) {
			if ((this.modelElementsBuffer == null || this.modelElementsBuffer.openglElementsBuffer != null) && (this.colorsBuffer == null || this.colorsBuffer.buffer != null)) {
				this.facesA = this.facesB = this.facesC = null;
			} else {
				this.updateRequired = true;
			}
		}
		if (this.faceGroups != null && !GlModel.altersFaces(this.functionMask, this.preferenceFlags)) {
			this.facesSubmeshes = null;
			this.faceGroups = null;
		}
		if (this.vertexGroups != null && !GlModel.altersVertices(this.preferenceFlags, this.functionMask)) {
			this.vertexGroups = null;
			this.verticesSubmeshes = null;
		}
		if (this.billboardGroups != null && !GlModel.altersBillboards(this.functionMask, this.preferenceFlags)) {
			this.billboardGroups = null;
		}
		if (this.materialBatchOffsets != null && (this.functionMask & 0x800) == 0 && (this.functionMask & 0x40000) == 0) {
			this.materialBatchOffsets = null;
		}
	}

	@OriginalMember(owner = "client!paa", name = "e", descriptor = "()V")
	@Override
	public void unlock() {
	}

	@OriginalMember(owner = "client!paa", name = "ia", descriptor = "(SS)V")
	@Override
	public void ia(@OriginalArg(0) short src, @OriginalArg(1) short dst) {
		for (@Pc(7) int i = 0; i < this.numFaces; i++) {
			if (src == this.facesColor[i]) {
				this.facesColor[i] = dst;
			}
		}
		if (this.billboardFaces != null) {
			for (@Pc(30) int j = 0; j < this.billboardCount; j++) {
				@Pc(37) GlBillboardFace glBillboardFace = this.billboardFaces[j];
				@Pc(42) BillboardSurfaceGL glBillboardAttributes = this.billboardAttributes[j];
				glBillboardAttributes.diffuseColor = ColorUtil.hslToRgb[this.facesColor[glBillboardFace.face] & 0xFFFF] & 0xFFFFFF | glBillboardAttributes.diffuseColor & 0xFF000000;
			}
		}
		if (this.colorsBuffer != null) {
			this.colorsBuffer.buffer = null;
		}
	}

	@OriginalMember(owner = "client!paa", name = "d", descriptor = "()[Lclient!nn;")
	@Override
	public EmissiveTriangle[] getEmissiveTriangles() {
		return this.emissiveTriangles;
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "()[Lclient!eca;")
	@Override
	public EffectiveVertex[] getEffectiveVertices() {
		return this.effectiveVertices;
	}

	@OriginalMember(owner = "client!paa", name = "V", descriptor = "()I")
	@Override
	public int V() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.minX;
	}

	@OriginalMember(owner = "client!paa", name = "r", descriptor = "()Z")
	@Override
	public boolean r() {
		return this.textureAnimating;
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(Lclient!bca;)V")
	@Override
	public void updateParticlePosition(@OriginalArg(0) Matrix matrix) {
		@Pc(8) Matrix43 modelMatrix = (Matrix43) matrix;
		@Pc(13) int local13;
		if (this.emissiveTriangles != null) {
			for (int emitterIndex = 0; emitterIndex < this.emissiveTriangles.length; emitterIndex++) {
				@Pc(20) EmissiveTriangle modelParticleEmitter = this.emissiveTriangles[emitterIndex];
				@Pc(22) EmissiveTriangle modelParticleEmitter2 = modelParticleEmitter;
				if (modelParticleEmitter.child != null) {
					modelParticleEmitter2 = modelParticleEmitter.child;
				}
				modelParticleEmitter2.verticeZC = (int) (modelMatrix.xD + (float) this.verticesY[modelParticleEmitter.faceA] * modelMatrix.xB + modelMatrix.xA * (float) this.verticesX[modelParticleEmitter.faceA] + (float) this.verticesZ[modelParticleEmitter.faceA] * modelMatrix.xC);
				modelParticleEmitter2.verticeXC = (int) (modelMatrix.yD + (float) this.verticesY[modelParticleEmitter.faceA] * modelMatrix.yB + modelMatrix.yA * (float) this.verticesX[modelParticleEmitter.faceA] + (float) this.verticesZ[modelParticleEmitter.faceA] * modelMatrix.yC);
				modelParticleEmitter2.verticeYA = (int) (modelMatrix.zD + (float) this.verticesX[modelParticleEmitter.faceA] * modelMatrix.zA + modelMatrix.zB * (float) this.verticesY[modelParticleEmitter.faceA] + (float) this.verticesZ[modelParticleEmitter.faceA] * modelMatrix.zC);
				modelParticleEmitter2.verticeZA = (int) (modelMatrix.xD + modelMatrix.xA * (float) this.verticesX[modelParticleEmitter.faceB] + (float) this.verticesY[modelParticleEmitter.faceB] * modelMatrix.xB + (float) this.verticesZ[modelParticleEmitter.faceB] * modelMatrix.xC);
				modelParticleEmitter2.verticeXA = (int) (modelMatrix.yD + modelMatrix.yC * (float) this.verticesZ[modelParticleEmitter.faceB] + (float) this.verticesX[modelParticleEmitter.faceB] * modelMatrix.yA + (float) this.verticesY[modelParticleEmitter.faceB] * modelMatrix.yB);
				modelParticleEmitter2.verticeZB = (int) (modelMatrix.zD + modelMatrix.zC * (float) this.verticesZ[modelParticleEmitter.faceB] + modelMatrix.zB * (float) this.verticesY[modelParticleEmitter.faceB] + (float) this.verticesX[modelParticleEmitter.faceB] * modelMatrix.zA);
				modelParticleEmitter2.verticeYB = (int) (modelMatrix.xD + (float) this.verticesY[modelParticleEmitter.vertexC] * modelMatrix.xB + (float) this.verticesX[modelParticleEmitter.vertexC] * modelMatrix.xA + modelMatrix.xC * (float) this.verticesZ[modelParticleEmitter.vertexC]);
				modelParticleEmitter2.transformedCY = (int) (modelMatrix.yD + (float) this.verticesZ[modelParticleEmitter.vertexC] * modelMatrix.yC + modelMatrix.yA * (float) this.verticesX[modelParticleEmitter.vertexC] + modelMatrix.yB * (float) this.verticesY[modelParticleEmitter.vertexC]);
				modelParticleEmitter2.verticeYC = (int) (modelMatrix.zB * (float) this.verticesY[modelParticleEmitter.vertexC] + modelMatrix.zA * (float) this.verticesX[modelParticleEmitter.vertexC] + (float) this.verticesZ[modelParticleEmitter.vertexC] * modelMatrix.zC + modelMatrix.zD);
			}
		}
		if (this.effectiveVertices == null) {
			return;
		}
		for (int effectorIndex = 0; effectorIndex < this.effectiveVertices.length; effectorIndex++) {
			@Pc(361) EffectiveVertex modelParticleEffector = this.effectiveVertices[effectorIndex];
			@Pc(363) EffectiveVertex modelParticleEffector2 = modelParticleEffector;
			if (modelParticleEffector.child != null) {
				modelParticleEffector2 = modelParticleEffector.child;
			}
			if (modelParticleEffector.matrix == null) {
				modelParticleEffector.matrix = modelMatrix.copy();
			} else {
				modelParticleEffector.matrix.apply(modelMatrix);
			}
			modelParticleEffector2.verticeZ = (int) (modelMatrix.xD + (float) this.verticesZ[modelParticleEffector.appliesOnVertex] * modelMatrix.xC + (float) this.verticesX[modelParticleEffector.appliesOnVertex] * modelMatrix.xA + modelMatrix.xB * (float) this.verticesY[modelParticleEffector.appliesOnVertex]);
			modelParticleEffector2.verticeX = (int) (modelMatrix.yD + (float) this.verticesY[modelParticleEffector.appliesOnVertex] * modelMatrix.yB + modelMatrix.yA * (float) this.verticesX[modelParticleEffector.appliesOnVertex] + (float) this.verticesZ[modelParticleEffector.appliesOnVertex] * modelMatrix.yC);
			modelParticleEffector2.verticeY = (int) (modelMatrix.zD + (float) this.verticesZ[modelParticleEffector.appliesOnVertex] * modelMatrix.zC + (float) this.verticesX[modelParticleEffector.appliesOnVertex] * modelMatrix.zA + (float) this.verticesY[modelParticleEffector.appliesOnVertex] * modelMatrix.zB);
		}
	}

	@OriginalMember(owner = "client!paa", name = "ma", descriptor = "()I")
	@Override
	public int ma() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.size3d;
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(I[IIIIIZ)V")
	@Override
	public void applyTransformUnmasked(@OriginalArg(0) int type, @OriginalArg(1) int[] labels, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int z, @OriginalArg(5) int rotation, @OriginalArg(6) boolean lights) {
		@Pc(8) int length = labels.length;
		@Pc(29) int local29;
		@Pc(35) int local35;
		@Pc(59) int local59;
		@Pc(25) int scaledX;
		@Pc(21) int scaledY;
		@Pc(17) int scaledZ;
		@Pc(41) int group;
		@Pc(53) int local53;
		@Pc(51) int[] groupVertices;
		if (type == 0) {
			scaledZ = z << 4;
			scaledY = y << 4;
			scaledX = x << 4;
			GlModel.originZ = 0;
			int vertexCount = 0;
			ModelTransformOriginY.originY = 0;
			GlModel.originX = 0;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				group = labels[labelIndex];
				if (group < this.vertexGroups.length) {
					groupVertices = this.vertexGroups[group];
					for (int groupMemberIndex = 0; groupMemberIndex < groupVertices.length; groupMemberIndex++) {
						int vertex = groupVertices[groupMemberIndex];
						GlModel.originX += this.verticesX[vertex];
						ModelTransformOriginY.originY += this.verticesY[vertex];
						vertexCount++;
						GlModel.originZ += this.verticesZ[vertex];
					}
				}
			}
			if (vertexCount <= 0) {
				ModelTransformOriginY.originY = scaledY;
				GlModel.originZ = scaledZ;
				GlModel.originX = scaledX;
			} else {
				ModelTransformOriginY.originY = scaledY + ModelTransformOriginY.originY / vertexCount;
				GlModel.originX = GlModel.originX / vertexCount + scaledX;
				GlModel.originZ = scaledZ + GlModel.originZ / vertexCount;
			}
			return;
		}
		@Pc(157) int[] members;
		@Pc(159) int memberIndex;
		if (type == 1) {
			scaledX = x << 4;
			scaledY = y << 4;
			scaledZ = z << 4;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int groupId = labels[labelIndex];
				if (groupId < this.vertexGroups.length) {
					members = this.vertexGroups[groupId];
					for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
						int vertex = members[memberIndex];
						this.verticesX[vertex] += scaledX;
						this.verticesY[vertex] += scaledY;
						this.verticesZ[vertex] += scaledZ;
					}
				}
			}
			return;
		}
		@Pc(269) int local269;
		@Pc(288) int local288;
		@Pc(746) int local746;
		if (type == 2) {
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int groupId = labels[labelIndex];
				if (this.vertexGroups.length > groupId) {
					members = this.vertexGroups[groupId];
					if ((rotation & 0x1) == 0) {
						for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
							int vertex = members[memberIndex];
							this.verticesX[vertex] -= GlModel.originX;
							this.verticesY[vertex] -= ModelTransformOriginY.originY;
							this.verticesZ[vertex] -= GlModel.originZ;
							if (z != 0) {
								int sin = TextureCombineSource.SINE[z];
								int cos = TextureCombineSource.COSINE[z];
								int rotated = this.verticesX[vertex] * cos + sin * this.verticesY[vertex] + 16383 >> 14;
								this.verticesY[vertex] = this.verticesY[vertex] * cos + 16383 - sin * this.verticesX[vertex] >> 14;
								this.verticesX[vertex] = rotated;
							}
							if (x != 0) {
								int sin = TextureCombineSource.SINE[x];
								int cos = TextureCombineSource.COSINE[x];
								int rotated = cos * this.verticesY[vertex] + 16383 - this.verticesZ[vertex] * sin >> 14;
								this.verticesZ[vertex] = cos * this.verticesZ[vertex] + sin * this.verticesY[vertex] + 16383 >> 14;
								this.verticesY[vertex] = rotated;
							}
							if (y != 0) {
								int sin = TextureCombineSource.SINE[y];
								int cos = TextureCombineSource.COSINE[y];
								int rotated = sin * this.verticesZ[vertex] + cos * this.verticesX[vertex] + 16383 >> 14;
								this.verticesZ[vertex] = this.verticesZ[vertex] * cos + 16383 - this.verticesX[vertex] * sin >> 14;
								this.verticesX[vertex] = rotated;
							}
							this.verticesX[vertex] += GlModel.originX;
							this.verticesY[vertex] += ModelTransformOriginY.originY;
							this.verticesZ[vertex] += GlModel.originZ;
						}
					} else {
						for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
							int vertex = members[memberIndex];
							this.verticesX[vertex] -= GlModel.originX;
							this.verticesY[vertex] -= ModelTransformOriginY.originY;
							this.verticesZ[vertex] -= GlModel.originZ;
							if (x != 0) {
								int sin = TextureCombineSource.SINE[x];
								int cos = TextureCombineSource.COSINE[x];
								int rotated = cos * this.verticesY[vertex] + 16383 - this.verticesZ[vertex] * sin >> 14;
								this.verticesZ[vertex] = this.verticesY[vertex] * sin + cos * this.verticesZ[vertex] + 16383 >> 14;
								this.verticesY[vertex] = rotated;
							}
							if (z != 0) {
								int sin = TextureCombineSource.SINE[z];
								int cos = TextureCombineSource.COSINE[z];
								int rotated = this.verticesX[vertex] * cos + sin * this.verticesY[vertex] + 16383 >> 14;
								this.verticesY[vertex] = this.verticesY[vertex] * cos + 16383 - sin * this.verticesX[vertex] >> 14;
								this.verticesX[vertex] = rotated;
							}
							if (y != 0) {
								int sin = TextureCombineSource.SINE[y];
								int cos = TextureCombineSource.COSINE[y];
								int rotated = sin * this.verticesZ[vertex] + this.verticesX[vertex] * cos + 16383 >> 14;
								this.verticesZ[vertex] = this.verticesZ[vertex] * cos + 16383 - sin * this.verticesX[vertex] >> 14;
								this.verticesX[vertex] = rotated;
							}
							this.verticesX[vertex] += GlModel.originX;
							this.verticesY[vertex] += ModelTransformOriginY.originY;
							this.verticesZ[vertex] += GlModel.originZ;
						}
					}
				}
			}
			if (lights) {
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					group = labels[labelIndex];
					if (group < this.vertexGroups.length) {
						groupVertices = this.vertexGroups[group];
						for (int groupMemberIndex = 0; groupMemberIndex < groupVertices.length; groupMemberIndex++) {
							int vertex = groupVertices[groupMemberIndex];
							int start = this.triangles[vertex];
							int end = this.triangles[vertex + 1];
							for (int index = start; index < end; index++) {
								@Pc(755) int normalIndex = this.vertexNormalIndices[index] - 1;
								if (normalIndex == -1) {
									break;
								}
								@Pc(768) int sin;
								@Pc(772) int cos;
								@Pc(790) int rotated;
								if (z != 0) {
									sin = TextureCombineSource.SINE[z];
									cos = TextureCombineSource.COSINE[z];
									rotated = cos * this.normalsX[normalIndex] + this.normalsY[normalIndex] * sin + 16383 >> 14;
									this.normalsY[normalIndex] = (short) (cos * this.normalsY[normalIndex] + 16383 - this.normalsX[normalIndex] * sin >> 14);
									this.normalsX[normalIndex] = (short) rotated;
								}
								if (x != 0) {
									sin = TextureCombineSource.SINE[x];
									cos = TextureCombineSource.COSINE[x];
									rotated = cos * this.normalsY[normalIndex] + 16383 - this.normalsZ[normalIndex] * sin >> 14;
									this.normalsZ[normalIndex] = (short) (cos * this.normalsZ[normalIndex] + sin * this.normalsY[normalIndex] + 16383 >> 14);
									this.normalsY[normalIndex] = (short) rotated;
								}
								if (y != 0) {
									sin = TextureCombineSource.SINE[y];
									cos = TextureCombineSource.COSINE[y];
									rotated = sin * this.normalsZ[normalIndex] + this.normalsX[normalIndex] * cos + 16383 >> 14;
									this.normalsZ[normalIndex] = (short) (this.normalsZ[normalIndex] * cos + 16383 - this.normalsX[normalIndex] * sin >> 14);
									this.normalsX[normalIndex] = (short) rotated;
								}
							}
						}
					}
				}
				if (this.normalsBuffer == null && this.colorsBuffer != null) {
					this.colorsBuffer.buffer = null;
				}
				if (this.normalsBuffer != null) {
					this.normalsBuffer.buffer = null;
				}
			}
		} else if (type == 3) {
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int groupId = labels[labelIndex];
				if (this.vertexGroups.length > groupId) {
					members = this.vertexGroups[groupId];
					for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
						int vertex = members[memberIndex];
						this.verticesX[vertex] -= GlModel.originX;
						this.verticesY[vertex] -= ModelTransformOriginY.originY;
						this.verticesZ[vertex] -= GlModel.originZ;
						this.verticesX[vertex] = x * this.verticesX[vertex] >> 7;
						this.verticesY[vertex] = this.verticesY[vertex] * y >> 7;
						this.verticesZ[vertex] = this.verticesZ[vertex] * z >> 7;
						this.verticesX[vertex] += GlModel.originX;
						this.verticesY[vertex] += ModelTransformOriginY.originY;
						this.verticesZ[vertex] += GlModel.originZ;
					}
				}
			}
		} else {
			@Pc(1207) GlBillboardFace glBillboardFace;
			@Pc(1212) BillboardSurfaceGL glBillboardAttributes;
			if (type == 5) {
				if (this.faceGroups != null) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int groupId = labels[labelIndex];
						if (this.faceGroups.length > groupId) {
							members = this.faceGroups[groupId];
							for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
								int face = members[memberIndex];
								int alpha = (this.facesAlpha[face] & 0xFF) + x * 8;
								if (alpha < 0) {
									alpha = 0;
								} else if (alpha > 255) {
									alpha = 255;
								}
								this.facesAlpha[face] = (byte) alpha;
							}
							if (members.length > 0 && this.colorsBuffer != null) {
								this.colorsBuffer.buffer = null;
							}
						}
					}
					if (this.billboardFaces != null) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							glBillboardFace = this.billboardFaces[billboardIndex];
							glBillboardAttributes = this.billboardAttributes[billboardIndex];
							glBillboardAttributes.diffuseColor = 255 - (this.facesAlpha[glBillboardFace.face] & 0xFF) << 24 | glBillboardAttributes.diffuseColor & 0xFFFFFF;
						}
					}
				}
			} else if (type != 7) {
				@Pc(1463) BillboardSurfaceGL glBillboardAttributes2;
				if (type == 8) {
					if (this.billboardGroups != null) {
						for (int labelIndex = 0; labelIndex < length; labelIndex++) {
							int groupId = labels[labelIndex];
							if (this.billboardGroups.length > groupId) {
								members = this.billboardGroups[groupId];
								for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
									glBillboardAttributes2 = this.billboardAttributes[members[memberIndex]];
									glBillboardAttributes2.offsetX += x;
									glBillboardAttributes2.offsetY += y;
								}
							}
						}
					}
				} else if (type == 10) {
					if (this.billboardGroups != null) {
						for (int labelIndex = 0; labelIndex < length; labelIndex++) {
							int groupId = labels[labelIndex];
							if (groupId < this.billboardGroups.length) {
								members = this.billboardGroups[groupId];
								for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
									glBillboardAttributes2 = this.billboardAttributes[members[memberIndex]];
									glBillboardAttributes2.scaleX = x * glBillboardAttributes2.scaleX >> 7;
									glBillboardAttributes2.scaleY = glBillboardAttributes2.scaleY * y >> 7;
								}
							}
						}
					}
				} else if (type == 9 && this.billboardGroups != null) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int groupId = labels[labelIndex];
						if (groupId < this.billboardGroups.length) {
							members = this.billboardGroups[groupId];
							for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
								glBillboardAttributes2 = this.billboardAttributes[members[memberIndex]];
								glBillboardAttributes2.angle = glBillboardAttributes2.angle + x & 0x3FFF;
							}
						}
					}
				}
			} else if (this.faceGroups != null) {
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					int groupId = labels[labelIndex];
					if (groupId < this.faceGroups.length) {
						members = this.faceGroups[groupId];
						for (memberIndex = 0; memberIndex < members.length; memberIndex++) {
							int face = members[memberIndex];
							int color = this.facesColor[face] & 0xFFFF;
							int hue = color >> 10 & 0x3F;
							int saturation = color >> 7 & 0x7;
							saturation += y / 4;
							@Pc(1302) int newHue = hue + x & 0x3F;
							int lightness = color & 0x7F;
							lightness += z;
							if (saturation < 0) {
								saturation = 0;
							} else if (saturation > 7) {
								saturation = 7;
							}
							if (lightness < 0) {
								lightness = 0;
							} else if (lightness > 127) {
								lightness = 127;
							}
							this.facesColor[face] = (short) (saturation << 7 | newHue << 10 | lightness);
						}
						if (members.length > 0 && this.colorsBuffer != null) {
							this.colorsBuffer.buffer = null;
						}
					}
				}
				if (this.billboardFaces != null) {
					for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
						glBillboardFace = this.billboardFaces[billboardIndex];
						glBillboardAttributes = this.billboardAttributes[billboardIndex];
						glBillboardAttributes.diffuseColor = ColorUtil.hslToRgb[this.facesColor[glBillboardFace.face] & 0xFFFF] & 0xFFFFFF | glBillboardAttributes.diffuseColor & 0xFF000000;
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!paa", name = "HA", descriptor = "()I")
	@Override
	public int HA() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.minZ;
	}

	@OriginalMember(owner = "client!paa", name = "ua", descriptor = "()I")
	@Override
	public int ua() {
		return this.functionMask;
	}

	@OriginalMember(owner = "client!paa", name = "P", descriptor = "(IIII)V")
	@Override
	public void P(@OriginalArg(0) int type, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int z) {
		@Pc(15) int local15;
		@Pc(17) int local17;
		if (type == 0) {
			GlModel.originX = 0;
			GlModel.originZ = 0;
			ModelTransformOriginY.originY = 0;
			int vertexCount = 0;
			for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
				GlModel.originX += this.verticesX[vertexIndex];
				ModelTransformOriginY.originY += this.verticesY[vertexIndex];
				vertexCount++;
				GlModel.originZ += this.verticesZ[vertexIndex];
			}
			if (vertexCount <= 0) {
				ModelTransformOriginY.originY = y;
				GlModel.originZ = z;
				GlModel.originX = x;
			} else {
				ModelTransformOriginY.originY = ModelTransformOriginY.originY / vertexCount + y;
				GlModel.originX = x + GlModel.originX / vertexCount;
				GlModel.originZ = GlModel.originZ / vertexCount + z;
			}
		} else if (type == 1) {
			for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
				this.verticesX[vertexIndex] += x;
				this.verticesY[vertexIndex] += y;
				this.verticesZ[vertexIndex] += z;
			}
		} else {
			@Pc(155) int local155;
			@Pc(173) int local173;
			if (type == 2) {
				for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
					this.verticesX[vertexIndex] -= GlModel.originX;
					this.verticesY[vertexIndex] -= ModelTransformOriginY.originY;
					this.verticesZ[vertexIndex] -= GlModel.originZ;
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
						int rotatedY = cos * this.verticesY[vertexIndex] + 16383 - this.verticesZ[vertexIndex] * sin >> 14;
						this.verticesZ[vertexIndex] = sin * this.verticesY[vertexIndex] + cos * this.verticesZ[vertexIndex] + 16383 >> 14;
						this.verticesY[vertexIndex] = rotatedY;
					}
					if (y != 0) {
						int sin = TextureCombineSource.SINE[y];
						int cos = TextureCombineSource.COSINE[y];
						int rotatedX = this.verticesZ[vertexIndex] * sin + cos * this.verticesX[vertexIndex] + 16383 >> 14;
						this.verticesZ[vertexIndex] = cos * this.verticesZ[vertexIndex] + 16383 - this.verticesX[vertexIndex] * sin >> 14;
						this.verticesX[vertexIndex] = rotatedX;
					}
					this.verticesX[vertexIndex] += GlModel.originX;
					this.verticesY[vertexIndex] += ModelTransformOriginY.originY;
					this.verticesZ[vertexIndex] += GlModel.originZ;
				}
			} else if (type == 3) {
				for (int vertexIndex = 0; vertexIndex < this.maxDepth; vertexIndex++) {
					this.verticesX[vertexIndex] -= GlModel.originX;
					this.verticesY[vertexIndex] -= ModelTransformOriginY.originY;
					this.verticesZ[vertexIndex] -= GlModel.originZ;
					this.verticesX[vertexIndex] = x * this.verticesX[vertexIndex] / 128;
					this.verticesY[vertexIndex] = y * this.verticesY[vertexIndex] / 128;
					this.verticesZ[vertexIndex] = this.verticesZ[vertexIndex] * z / 128;
					this.verticesX[vertexIndex] += GlModel.originX;
					this.verticesY[vertexIndex] += ModelTransformOriginY.originY;
					this.verticesZ[vertexIndex] += GlModel.originZ;
				}
			} else {
				@Pc(507) GlBillboardFace glBillboardFace;
				@Pc(512) BillboardSurfaceGL glBillboardAttributes;
				if (type == 5) {
					for (int faceIndex = 0; faceIndex < this.numFaces; faceIndex++) {
						int alpha = (this.facesAlpha[faceIndex] & 0xFF) + x * 8;
						if (alpha < 0) {
							alpha = 0;
						} else if (alpha > 255) {
							alpha = 255;
						}
						this.facesAlpha[faceIndex] = (byte) alpha;
					}
					if (this.colorsBuffer != null) {
						this.colorsBuffer.buffer = null;
					}
					if (this.billboardFaces != null) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							glBillboardFace = this.billboardFaces[billboardIndex];
							glBillboardAttributes = this.billboardAttributes[billboardIndex];
							glBillboardAttributes.diffuseColor = glBillboardAttributes.diffuseColor & 0xFFFFFF | 255 - (this.facesAlpha[glBillboardFace.face] & 0xFF) << 24;
						}
					}
				} else if (type == 7) {
					for (int faceIndex = 0; faceIndex < this.numFaces; faceIndex++) {
						int hsl = this.facesColor[faceIndex] & 0xFFFF;
						int hue = hsl >> 10 & 0x3F;
						int saturation = hsl >> 7 & 0x7;
						@Pc(573) int newHue = hue + x & 0x3F;
						@Pc(577) int newLightness = hsl & 0x7F;
						saturation += y / 4;
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
						this.facesColor[faceIndex] = (short) (newLightness | saturation << 7 | newHue << 10);
					}
					if (this.colorsBuffer != null) {
						this.colorsBuffer.buffer = null;
					}
					if (this.billboardFaces != null) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							glBillboardFace = this.billboardFaces[billboardIndex];
							glBillboardAttributes = this.billboardAttributes[billboardIndex];
							glBillboardAttributes.diffuseColor = ColorUtil.hslToRgb[this.facesColor[glBillboardFace.face] & 0xFFFF] & 0xFFFFFF | glBillboardAttributes.diffuseColor & 0xFF000000;
						}
					}
				} else {
					@Pc(694) BillboardSurfaceGL glBillboardAttributes2;
					if (type == 8) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							glBillboardAttributes2 = this.billboardAttributes[billboardIndex];
							glBillboardAttributes2.offsetY += y;
							glBillboardAttributes2.offsetX += x;
						}
					} else if (type == 10) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							glBillboardAttributes2 = this.billboardAttributes[billboardIndex];
							glBillboardAttributes2.scaleX = glBillboardAttributes2.scaleX * x >> 7;
							glBillboardAttributes2.scaleY = y * glBillboardAttributes2.scaleY >> 7;
						}
					} else if (type == 9) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							glBillboardAttributes2 = this.billboardAttributes[billboardIndex];
							glBillboardAttributes2.angle = glBillboardAttributes2.angle + x & 0x3FFF;
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!paa", name = "s", descriptor = "(I)V")
	@Override
	public void s(@OriginalArg(0) int functionMask) {
		this.functionMask = functionMask;
		if (this.mergedNormals != null && (this.functionMask & 0x10000) == 0) {
			this.normalsZ = this.mergedNormals.normalsZ;
			this.magnitudes = this.mergedNormals.magnitude;
			this.normalsX = this.mergedNormals.normalsX;
			this.normalsY = this.mergedNormals.normalsY;
			this.mergedNormals = null;
		}
		this.updateRequired = true;
		this.releaseUploadedData();
	}

	@OriginalMember(owner = "client!paa", name = "C", descriptor = "(I)V")
	@Override
	public void C(@OriginalArg(0) int ambient) {
		this.ambient = (short) ambient;
		if (this.colorsBuffer != null) {
			this.colorsBuffer.buffer = null;
		}
	}

	@OriginalMember(owner = "client!paa", name = "v", descriptor = "()V")
	@Override
	public void v() {
		for (@Pc(7) int i = 0; i < this.maxDepth; i++) {
			this.verticesZ[i] = -this.verticesZ[i];
		}
		for (@Pc(29) int j = 0; j < this.numNormals; j++) {
			this.normalsZ[j] = (short) -this.normalsZ[j];
		}
		for (@Pc(52) int k = 0; k < this.numFaces; k++) {
			@Pc(59) short temp = this.facesA[k];
			this.facesA[k] = this.facesC[k];
			this.facesC[k] = temp;
		}
		if (this.normalsBuffer == null && this.colorsBuffer != null) {
			this.colorsBuffer.buffer = null;
		}
		if (this.normalsBuffer != null) {
			this.normalsBuffer.buffer = null;
		}
		this.boundsCalculated = false;
		if (this.modelElementsBuffer != null) {
			this.modelElementsBuffer.openglElementsBuffer = null;
		}
		if (this.vertexBuffer != null) {
			this.vertexBuffer.buffer = null;
		}
	}

	@OriginalMember(owner = "client!paa", name = "a", descriptor = "(Lclient!bca;IZ)V")
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

	@OriginalMember(owner = "client!paa", name = "I", descriptor = "(I[IIIIZI[I)V")
	@Override
	public void I(@OriginalArg(0) int type, @OriginalArg(1) int[] labels, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int z, @OriginalArg(5) boolean lights, @OriginalArg(6) int submesh, @OriginalArg(7) int[] matrix) {
		@Pc(8) int length = labels.length;
		@Pc(24) int local24;
		@Pc(32) int local32;
		@Pc(60) int local60;
		@Pc(38) int local38;
		@Pc(54) int local54;
		@Pc(52) int[] groupVertices;
		if (type == 0) {
			y <<= 0x4;
			z <<= 0x4;
			x <<= 0x4;
			int vertexCount = 0;
			GlModel.originX = 0;
			ModelTransformOriginY.originY = 0;
			GlModel.originZ = 0;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int label = labels[labelIndex];
				if (label < this.vertexGroups.length) {
					groupVertices = this.vertexGroups[label];
					for (int memberIndex = 0; memberIndex < groupVertices.length; memberIndex++) {
						int vertexIndex = groupVertices[memberIndex];
						if (this.verticesSubmeshes == null || (submesh & this.verticesSubmeshes[vertexIndex]) != 0) {
							GlModel.originX += this.verticesX[vertexIndex];
							ModelTransformOriginY.originY += this.verticesY[vertexIndex];
							vertexCount++;
							GlModel.originZ += this.verticesZ[vertexIndex];
						}
					}
				}
			}
			if (vertexCount <= 0) {
				GlModel.originX = x;
				GlModel.originZ = z;
				ModelTransformOriginY.originY = y;
			} else {
				GlModel.originX = x + GlModel.originX / vertexCount;
				ModelTransformOriginY.originY = ModelTransformOriginY.originY / vertexCount + y;
				GlModel.originZ = z + GlModel.originZ / vertexCount;
				Protocol.originPending = true;
			}
			return;
		}
		@Pc(243) int[] members;
		@Pc(245) int local245;
		if (type == 1) {
			if (matrix != null) {
				int transformedX = matrix[2] * z + matrix[0] * x + y * matrix[1] + 8192 >> 14;
				int transformedY = y * matrix[4] + x * matrix[3] + matrix[5] * z + 8192 >> 14;
				int transformedZ = z * matrix[8] + x * matrix[6] + y * matrix[7] + 8192 >> 14;
				x = transformedX;
				z = transformedZ;
				y = transformedY;
			}
			x <<= 0x4;
			z <<= 0x4;
			y <<= 0x4;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int label = labels[labelIndex];
				if (label < this.vertexGroups.length) {
					members = this.vertexGroups[label];
					for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
						int vertexIndex = members[memberIndex];
						if (this.verticesSubmeshes == null || (this.verticesSubmeshes[vertexIndex] & submesh) != 0) {
							this.verticesX[vertexIndex] += x;
							this.verticesY[vertexIndex] += y;
							this.verticesZ[vertexIndex] += z;
						}
					}
				}
			}
			return;
		}
		@Pc(379) int local379;
		@Pc(398) int local398;
		@Pc(628) int local628;
		@Pc(637) int local637;
		@Pc(649) int local649;
		@Pc(653) int local653;
		@Pc(671) int local671;
		@Pc(1011) int local1011;
		@Pc(1019) int local1019;
		@Pc(1175) int local1175;
		@Pc(1201) int local1201;
		@Pc(1206) int local1206;
		@Pc(1215) int local1215;
		@Pc(1220) int local1220;
		@Pc(1224) int local1224;
		@Pc(1228) int local1228;
		@Pc(1230) int local1230;
		@Pc(1359) int[] local1359;
		@Pc(1361) int local1361;
		@Pc(1365) int local1365;
		@Pc(1369) int local1369;
		@Pc(1371) int local1371;
		@Pc(1502) int local1502;
		if (type == 2) {
			if (matrix == null) {
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					int label = labels[labelIndex];
					if (label < this.vertexGroups.length) {
						members = this.vertexGroups[label];
						for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
							int vertexIndex = members[memberIndex];
							if (this.verticesSubmeshes == null || (submesh & this.verticesSubmeshes[vertexIndex]) != 0) {
								this.verticesX[vertexIndex] -= GlModel.originX;
								this.verticesY[vertexIndex] -= ModelTransformOriginY.originY;
								this.verticesZ[vertexIndex] -= GlModel.originZ;
								if (z != 0) {
									int sin = TextureCombineSource.SINE[z];
									int cos = TextureCombineSource.COSINE[z];
									int rotatedX = sin * this.verticesY[vertexIndex] + this.verticesX[vertexIndex] * cos + 16383 >> 14;
									this.verticesY[vertexIndex] = this.verticesY[vertexIndex] * cos + 16383 - this.verticesX[vertexIndex] * sin >> 14;
									this.verticesX[vertexIndex] = rotatedX;
								}
								if (x != 0) {
									int sin = TextureCombineSource.SINE[x];
									int cos = TextureCombineSource.COSINE[x];
									int rotatedY = cos * this.verticesY[vertexIndex] + 16383 - this.verticesZ[vertexIndex] * sin >> 14;
									this.verticesZ[vertexIndex] = sin * this.verticesY[vertexIndex] + this.verticesZ[vertexIndex] * cos + 16383 >> 14;
									this.verticesY[vertexIndex] = rotatedY;
								}
								if (y != 0) {
									int sin = TextureCombineSource.SINE[y];
									int cos = TextureCombineSource.COSINE[y];
									int rotatedX = sin * this.verticesZ[vertexIndex] + cos * this.verticesX[vertexIndex] + 16383 >> 14;
									this.verticesZ[vertexIndex] = this.verticesZ[vertexIndex] * cos + 16383 - this.verticesX[vertexIndex] * sin >> 14;
									this.verticesX[vertexIndex] = rotatedX;
								}
								this.verticesX[vertexIndex] += GlModel.originX;
								this.verticesY[vertexIndex] += ModelTransformOriginY.originY;
								this.verticesZ[vertexIndex] += GlModel.originZ;
							}
						}
					}
				}
				if (lights) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int label = labels[labelIndex];
						if (this.vertexGroups.length > label) {
							groupVertices = this.vertexGroups[label];
							for (int memberIndex = 0; memberIndex < groupVertices.length; memberIndex++) {
								int vertexIndex = groupVertices[memberIndex];
								if (this.verticesSubmeshes == null || (this.verticesSubmeshes[vertexIndex] & submesh) != 0) {
									int normalStart = this.triangles[vertexIndex];
									int normalEnd = this.triangles[vertexIndex + 1];
									for (int normalSlot = normalStart; normalSlot < normalEnd; normalSlot++) {
										int normalIndex = this.vertexNormalIndices[normalSlot] - 1;
										if (normalIndex == -1) {
											break;
										}
										if (z != 0) {
											int sin = TextureCombineSource.SINE[z];
											int cos = TextureCombineSource.COSINE[z];
											int rotatedX = this.normalsX[normalIndex] * cos + this.normalsY[normalIndex] * sin + 16383 >> 14;
											this.normalsY[normalIndex] = (short) (cos * this.normalsY[normalIndex] + 16383 - sin * this.normalsX[normalIndex] >> 14);
											this.normalsX[normalIndex] = (short) rotatedX;
										}
										if (x != 0) {
											int sin = TextureCombineSource.SINE[x];
											int cos = TextureCombineSource.COSINE[x];
											int rotatedY = this.normalsY[normalIndex] * cos + 16383 - sin * this.normalsZ[normalIndex] >> 14;
											this.normalsZ[normalIndex] = (short) (sin * this.normalsY[normalIndex] + cos * this.normalsZ[normalIndex] + 16383 >> 14);
											this.normalsY[normalIndex] = (short) rotatedY;
										}
										if (y != 0) {
											int sin = TextureCombineSource.SINE[y];
											int cos = TextureCombineSource.COSINE[y];
											int rotatedX = cos * this.normalsX[normalIndex] + sin * this.normalsZ[normalIndex] + 16383 >> 14;
											this.normalsZ[normalIndex] = (short) (this.normalsZ[normalIndex] * cos + 16383 - sin * this.normalsX[normalIndex] >> 14);
											this.normalsX[normalIndex] = (short) rotatedX;
										}
									}
								}
							}
						}
					}
					if (this.normalsBuffer == null && this.colorsBuffer != null) {
						this.colorsBuffer.buffer = null;
					}
					if (this.normalsBuffer != null) {
						this.normalsBuffer.buffer = null;
						return;
					}
				}
			} else {
				int parentOriginX = matrix[9] << 4;
				int parentOriginY = matrix[10] << 4;
				int parentOriginZ = matrix[11] << 4;
				int parentTranslateX = matrix[12] << 4;
				int parentTranslateY = matrix[13] << 4;
				int parentTranslateZ = matrix[14] << 4;
				if (Protocol.originPending) {
					int newOriginX = GlModel.originX * matrix[0] + matrix[3] * ModelTransformOriginY.originY + matrix[6] * GlModel.originZ + 8192 >> 14;
					int newOriginY = matrix[1] * GlModel.originX + ModelTransformOriginY.originY * matrix[4] + matrix[7] * GlModel.originZ + 8192 >> 14;
					newOriginX += parentTranslateX;
					newOriginY += parentTranslateY;
					int newOriginZ = GlModel.originZ * matrix[8] + matrix[2] * GlModel.originX + matrix[5] * ModelTransformOriginY.originY + 8192 >> 14;
					newOriginZ += parentTranslateZ;
					GlModel.originX = newOriginX;
					ModelTransformOriginY.originY = newOriginY;
					GlModel.originZ = newOriginZ;
					Protocol.originPending = false;
				}
				@Pc(979) int[] rotationMatrix = new int[9];
				int cosX = TextureCombineSource.COSINE[x];
				int sinX = TextureCombineSource.SINE[x];
				int cosY = TextureCombineSource.COSINE[y];
				int sinY = TextureCombineSource.SINE[y];
				int cosZ = TextureCombineSource.COSINE[z];
				int sinZ = TextureCombineSource.SINE[z];
				int sinXCosZ = cosZ * sinX + 8192 >> 14;
				int sinXSinZ = sinX * sinZ + 8192 >> 14;
				rotationMatrix[6] = sinXSinZ * cosY + cosZ * -sinY + 8192 >> 14;
				rotationMatrix[8] = cosX * cosY + 8192 >> 14;
				rotationMatrix[4] = cosX * cosZ + 8192 >> 14;
				rotationMatrix[1] = sinZ * -cosY + sinXCosZ * sinY + 8192 >> 14;
				rotationMatrix[3] = cosX * sinZ + 8192 >> 14;
				rotationMatrix[5] = -sinX;
				rotationMatrix[0] = sinY * sinXSinZ + cosZ * cosY + 8192 >> 14;
				rotationMatrix[7] = cosY * sinXCosZ + sinZ * sinY + 8192 >> 14;
				rotationMatrix[2] = sinY * cosX + 8192 >> 14;
				@Pc(1149) int rotatedPivotX = rotationMatrix[1] * -ModelTransformOriginY.originY + -GlModel.originX * rotationMatrix[0] + -GlModel.originZ * rotationMatrix[2] + 8192 >> 14;
				int rotatedPivotY = -GlModel.originX * rotationMatrix[3] + rotationMatrix[4] * -ModelTransformOriginY.originY + rotationMatrix[5] * -GlModel.originZ + 8192 >> 14;
				int rotatedPivotZ = -GlModel.originZ * rotationMatrix[8] + rotationMatrix[6] * -GlModel.originX + rotationMatrix[7] * -ModelTransformOriginY.originY + 8192 >> 14;
				int pivotTranslateX = rotatedPivotX + GlModel.originX;
				@Pc(1211) int pivotTranslateY = rotatedPivotY + ModelTransformOriginY.originY;
				int pivotTranslateZ = GlModel.originZ + rotatedPivotZ;
				@Pc(1218) int[] combinedMatrix = new int[9];
				for (int row = 0; row < 3; row++) {
					for (int column = 0; column < 3; column++) {
						int dotProduct = 0;
						for (int k = 0; k < 3; k++) {
							dotProduct += matrix[column * 3 + k] * rotationMatrix[row * 3 + k];
						}
						combinedMatrix[column + row * 3] = dotProduct + 8192 >> 14;
					}
				}
				int translatedX = rotationMatrix[2] * parentTranslateZ + parentTranslateX * rotationMatrix[0] + parentTranslateY * rotationMatrix[1] + 8192 >> 14;
				int translatedY = parentTranslateY * rotationMatrix[4] + rotationMatrix[3] * parentTranslateX + rotationMatrix[5] * parentTranslateZ + 8192 >> 14;
				int translatedZ = rotationMatrix[8] * parentTranslateZ + rotationMatrix[7] * parentTranslateY + rotationMatrix[6] * parentTranslateX + 8192 >> 14;
				translatedY += pivotTranslateY;
				translatedX += pivotTranslateX;
				translatedZ += pivotTranslateZ;
				int[] finalMatrix = new int[9];
				for (int row = 0; row < 3; row++) {
					for (int column = 0; column < 3; column++) {
						int dotProduct = 0;
						for (int k = 0; k < 3; k++) {
							dotProduct += matrix[row * 3 + k] * combinedMatrix[k * 3 + column];
						}
						finalMatrix[row * 3 + column] = dotProduct + 8192 >> 14;
					}
				}
				int finalTranslateX = matrix[2] * translatedZ + translatedY * matrix[1] + translatedX * matrix[0] + 8192 >> 14;
				int finalTranslateY = matrix[3] * translatedX + matrix[4] * translatedY + matrix[5] * translatedZ + 8192 >> 14;
				finalTranslateY += parentOriginY;
				int finalTranslateZ = translatedZ * matrix[8] + matrix[7] * translatedY + translatedX * matrix[6] + 8192 >> 14;
				finalTranslateX += parentOriginX;
				finalTranslateZ += parentOriginZ;
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					@Pc(1508) int group = labels[labelIndex];
					if (group < this.vertexGroups.length) {
						@Pc(1518) int[] vertexGroup = this.vertexGroups[group];
						for (@Pc(1520) int i = 0; i < vertexGroup.length; i++) {
							@Pc(1526) int vertex = vertexGroup[i];
							if (this.verticesSubmeshes == null || (this.verticesSubmeshes[vertex] & submesh) != 0) {
								@Pc(1570) int rotatedX = finalMatrix[2] * this.verticesZ[vertex] + finalMatrix[1] * this.verticesY[vertex] + finalMatrix[0] * this.verticesX[vertex] + 8192 >> 14;
								@Pc(1601) int rotatedY = finalMatrix[5] * this.verticesZ[vertex] + this.verticesX[vertex] * finalMatrix[3] + finalMatrix[4] * this.verticesY[vertex] + 8192 >> 14;
								@Pc(1605) int newX = rotatedX + finalTranslateX;
								@Pc(1609) int newY = rotatedY + finalTranslateY;
								@Pc(1640) int rotatedZ = finalMatrix[8] * this.verticesZ[vertex] + this.verticesX[vertex] * finalMatrix[6] + finalMatrix[7] * this.verticesY[vertex] + 8192 >> 14;
								@Pc(1644) int newZ = rotatedZ + finalTranslateZ;
								this.verticesX[vertex] = newX;
								this.verticesY[vertex] = newY;
								this.verticesZ[vertex] = newZ;
							}
						}
					}
				}
			}
		} else if (type != 3) {
			@Pc(2581) GlBillboardFace glBillboardFace;
			@Pc(2586) BillboardSurfaceGL glBillboardAttributes;
			if (type == 5) {
				if (this.faceGroups != null) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int label = labels[labelIndex];
						if (label < this.faceGroups.length) {
							members = this.faceGroups[label];
							for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
								int faceIndex = members[memberIndex];
								if (this.facesSubmeshes == null || (this.facesSubmeshes[faceIndex] & submesh) != 0) {
									int alpha = (this.facesAlpha[faceIndex] & 0xFF) + (x * 8);
									if (alpha < 0) {
										alpha = 0;
									} else if (alpha > 255) {
										alpha = 255;
									}
									this.facesAlpha[faceIndex] = (byte) alpha;
									if (this.colorsBuffer != null) {
										this.colorsBuffer.buffer = null;
									}
								}
							}
						}
					}
					if (this.billboardFaces != null) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							glBillboardFace = this.billboardFaces[billboardIndex];
							glBillboardAttributes = this.billboardAttributes[billboardIndex];
							glBillboardAttributes.diffuseColor = 255 - (this.facesAlpha[glBillboardFace.face] & 0xFF) << 24 | glBillboardAttributes.diffuseColor & 0xFFFFFF;
						}
					}
				}
			} else if (type != 7) {
				@Pc(2841) BillboardSurfaceGL glBillboardAttributes2;
				if (type == 8) {
					if (this.billboardGroups != null) {
						for (int labelIndex = 0; labelIndex < length; labelIndex++) {
							int label = labels[labelIndex];
							if (label < this.billboardGroups.length) {
								members = this.billboardGroups[label];
								for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
									glBillboardAttributes2 = this.billboardAttributes[members[memberIndex]];
									glBillboardAttributes2.offsetY += y;
									glBillboardAttributes2.offsetX += x;
								}
							}
						}
					}
				} else if (type == 10) {
					if (this.billboardGroups != null) {
						for (int labelIndex = 0; labelIndex < length; labelIndex++) {
							int label = labels[labelIndex];
							if (label < this.billboardGroups.length) {
								members = this.billboardGroups[label];
								for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
									glBillboardAttributes2 = this.billboardAttributes[members[memberIndex]];
									glBillboardAttributes2.scaleY = y * glBillboardAttributes2.scaleY >> 7;
									glBillboardAttributes2.scaleX = x * glBillboardAttributes2.scaleX >> 7;
								}
							}
						}
					}
				} else if (type == 9 && this.billboardGroups != null) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int label = labels[labelIndex];
						if (this.billboardGroups.length > label) {
							members = this.billboardGroups[label];
							for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
								glBillboardAttributes2 = this.billboardAttributes[members[memberIndex]];
								glBillboardAttributes2.angle = glBillboardAttributes2.angle + x & 0x3FFF;
							}
						}
					}
				}
			} else if (this.faceGroups != null) {
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					int label = labels[labelIndex];
					if (this.faceGroups.length > label) {
						members = this.faceGroups[label];
						for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
							int faceIndex = members[memberIndex];
							if (this.facesSubmeshes == null || (this.facesSubmeshes[faceIndex] & submesh) != 0) {
								int hsl = this.facesColor[faceIndex] & 0xFFFF;
								int hue = hsl >> 10 & 0x3F;
								int saturation = hsl >> 7 & 0x7;
								saturation += y / 4;
								int lightness = hsl & 0x7F;
								@Pc(2691) int newHue = hue + x & 0x3F;
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
								if (this.colorsBuffer != null) {
									this.colorsBuffer.buffer = null;
								}
							}
						}
					}
				}
				if (this.billboardFaces != null) {
					for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
						glBillboardFace = this.billboardFaces[billboardIndex];
						glBillboardAttributes = this.billboardAttributes[billboardIndex];
						glBillboardAttributes.diffuseColor = ColorUtil.hslToRgb[this.facesColor[glBillboardFace.face] & 0xFFFF] & 0xFFFFFF | glBillboardAttributes.diffuseColor & 0xFF000000;
					}
				}
			}
		} else if (matrix == null) {
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int label = labels[labelIndex];
				if (this.vertexGroups.length > label) {
					members = this.vertexGroups[label];
					for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
						int vertexIndex = members[memberIndex];
						if (this.verticesSubmeshes == null || (submesh & this.verticesSubmeshes[vertexIndex]) != 0) {
							this.verticesX[vertexIndex] -= GlModel.originX;
							this.verticesY[vertexIndex] -= ModelTransformOriginY.originY;
							this.verticesZ[vertexIndex] -= GlModel.originZ;
							this.verticesX[vertexIndex] = this.verticesX[vertexIndex] * x >> 7;
							this.verticesY[vertexIndex] = this.verticesY[vertexIndex] * y >> 7;
							this.verticesZ[vertexIndex] = z * this.verticesZ[vertexIndex] >> 7;
							this.verticesX[vertexIndex] += GlModel.originX;
							this.verticesY[vertexIndex] += ModelTransformOriginY.originY;
							this.verticesZ[vertexIndex] += GlModel.originZ;
						}
					}
				}
			}
		} else {
			int parentOriginX = matrix[9] << 4;
			int parentOriginY = matrix[10] << 4;
			int parentOriginZ = matrix[11] << 4;
			int parentTranslateX = matrix[12] << 4;
			int parentTranslateY = matrix[13] << 4;
			int parentTranslateZ = matrix[14] << 4;
			if (Protocol.originPending) {
				int newOriginX = GlModel.originX * matrix[0] + ModelTransformOriginY.originY * matrix[3] + matrix[6] * GlModel.originZ + 8192 >> 14;
				int newOriginY = matrix[7] * GlModel.originZ + ModelTransformOriginY.originY * matrix[4] + matrix[1] * GlModel.originX + 8192 >> 14;
				newOriginY += parentTranslateY;
				int newOriginZ = matrix[5] * ModelTransformOriginY.originY + GlModel.originX * matrix[2] + matrix[8] * GlModel.originZ + 8192 >> 14;
				newOriginX += parentTranslateX;
				ModelTransformOriginY.originY = newOriginY;
				newOriginZ += parentTranslateZ;
				GlModel.originX = newOriginX;
				Protocol.originPending = false;
				GlModel.originZ = newOriginZ;
			}
			int scaleX = x << 15 >> 7;
			int scaleY = y << 15 >> 7;
			int scaleZ = z << 15 >> 7;
			int scaledPivotX = -GlModel.originX * scaleX + 8192 >> 14;
			int scaledPivotY = scaleY * -ModelTransformOriginY.originY + 8192 >> 14;
			int scaledPivotZ = -GlModel.originZ * scaleZ + 8192 >> 14;
			int pivotOffsetX = scaledPivotX + GlModel.originX;
			int pivotOffsetY = scaledPivotY + ModelTransformOriginY.originY;
			int pivotOffsetZ = GlModel.originZ + scaledPivotZ;
			@Pc(1869) int[] scaleMatrix = new int[] { matrix[0] * scaleX + 8192 >> 14, matrix[3] * scaleX + 8192 >> 14, scaleX * matrix[6] + 8192 >> 14, scaleY * matrix[1] + 8192 >> 14, scaleY * matrix[4] + 8192 >> 14, scaleY * matrix[7] + 8192 >> 14, scaleZ * matrix[2] + 8192 >> 14, scaleZ * matrix[5] + 8192 >> 14, scaleZ * matrix[8] + 8192 >> 14 };
			int scaledTranslateX = scaleX * parentTranslateX + 8192 >> 14;
			int scaledTranslateY = scaleY * parentTranslateY + 8192 >> 14;
			@Pc(1997) int translateY = scaledTranslateY + pivotOffsetY;
			int scaledTranslateZ = parentTranslateZ * scaleZ + 8192 >> 14;
			@Pc(2009) int translateX = scaledTranslateX + pivotOffsetX;
			@Pc(2013) int translateZ = scaledTranslateZ + pivotOffsetZ;
			@Pc(2016) int[] combinedMatrix = new int[9];
			@Pc(2022) int local2022;
			for (int row = 0; row < 3; row++) {
				for (int column = 0; column < 3; column++) {
					int dotProduct = 0;
					for (int k = 0; k < 3; k++) {
						dotProduct += matrix[row * 3 + k] * scaleMatrix[k * 3 + column];
					}
					combinedMatrix[row * 3 + column] = dotProduct + 8192 >> 14;
				}
			}
			int finalTranslateX = matrix[1] * translateY + matrix[0] * translateX + translateZ * matrix[2] + 8192 >> 14;
			int finalTranslateY = translateZ * matrix[5] + matrix[3] * translateX + matrix[4] * translateY + 8192 >> 14;
			int finalTranslateZ = translateX * matrix[6] + translateY * matrix[7] + matrix[8] * translateZ + 8192 >> 14;
			finalTranslateX += parentOriginX;
			finalTranslateY += parentOriginY;
			finalTranslateZ += parentOriginZ;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int label = labels[labelIndex];
				if (label < this.vertexGroups.length) {
					int[] vertexGroup = this.vertexGroups[label];
					for (int memberIndex = 0; memberIndex < vertexGroup.length; memberIndex++) {
						int vertexIndex = vertexGroup[memberIndex];
						if (this.verticesSubmeshes == null || (this.verticesSubmeshes[vertexIndex] & submesh) != 0) {
							int rotatedX = combinedMatrix[1] * this.verticesY[vertexIndex] + combinedMatrix[0] * this.verticesX[vertexIndex] + combinedMatrix[2] * this.verticesZ[vertexIndex] + 8192 >> 14;
							int rotatedY = combinedMatrix[4] * this.verticesY[vertexIndex] + this.verticesX[vertexIndex] * combinedMatrix[3] + combinedMatrix[5] * this.verticesZ[vertexIndex] + 8192 >> 14;
							@Pc(2265) int newX = rotatedX + finalTranslateX;
							@Pc(2269) int newY = rotatedY + finalTranslateY;
							int rotatedZ = combinedMatrix[7] * this.verticesY[vertexIndex] + combinedMatrix[6] * this.verticesX[vertexIndex] + combinedMatrix[8] * this.verticesZ[vertexIndex] + 8192 >> 14;
							this.verticesX[vertexIndex] = newX;
							@Pc(2310) int newZ = rotatedZ + finalTranslateZ;
							this.verticesY[vertexIndex] = newY;
							this.verticesZ[vertexIndex] = newZ;
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!paa", name = "d", descriptor = "(B)V")
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
			if (y > maxVertexY) {
				maxVertexY = y;
			}
			if (y < minVertexY) {
				minVertexY = y;
			}
			@Pc(50) int z = this.verticesZ[i];
			if (maxVertexX < x) {
				maxVertexX = x;
			}
			if (minVertexX > x) {
				minVertexX = x;
			}
			if (z > maxVertexZ) {
				maxVertexZ = z;
			}
			if (minVertexZ > z) {
				minVertexZ = z;
			}
			@Pc(78) int squaredDistance = x * x + z * z;
			if (maxSquaredRadius2d < squaredDistance) {
				maxSquaredRadius2d = squaredDistance;
			}
			squaredDistance = z * z + x * x + y * y;
			if (squaredDistance > maxSquaredRadius3d) {
				maxSquaredRadius3d = squaredDistance;
			}
		}
		this.minY = (short) minVertexY;
		this.minZ = (short) minVertexZ;
		this.maxZ = (short) maxVertexZ;
		this.minX = (short) minVertexX;
		this.maxY = (short) maxVertexY;
		this.maxX = (short) maxVertexX;
		this.size2d = (short) (Math.sqrt((double) maxSquaredRadius2d) + 0.99D);
		this.size3d = (short) (Math.sqrt((double) maxSquaredRadius3d) + 0.99D);
		this.boundsCalculated = true;
	}

	@OriginalMember(owner = "client!paa", name = "fa", descriptor = "()I")
	@Override
	public int fa() {
		if (!this.boundsCalculated) {
			this.calculateBounds();
		}
		return this.minY;
	}
	@OriginalMember(owner = "client!dq", name = "r", descriptor = "F")
	public static float modelViewXY;
	@OriginalMember(owner = "client!za", name = "p", descriptor = "I")
	public static int originX;

	@OriginalMember(owner = "client!q", name = "r", descriptor = "[J")
	public static long[] normalsUid;

	@OriginalMember(owner = "client!uv", name = "q", descriptor = "F")
	public static float modelViewZZ;
	@OriginalMember(owner = "client!qb", name = "z", descriptor = "I")
	public static int originZ;
	@OriginalMember(owner = "client!pga", name = "m", descriptor = "F")
	public static float modelViewXX;

	@OriginalMember(owner = "client!rs", name = "c", descriptor = "F")
	public static float modelViewYZ;

	@OriginalMember(owner = "client!it", name = "b", descriptor = "F")
	public static float modelViewZW;

	@OriginalMember(owner = "client!cp", name = "c", descriptor = "F")
	public static float modelViewZY;

	@OriginalMember(owner = "client!ch", name = "c", descriptor = "F")
	public static float modelViewXZ;
	@OriginalMember(owner = "client!cga", name = "a", descriptor = "(IBI)Z")
	public static boolean altersVertices(@OriginalArg(0) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x20) != 0;
	}

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(III)Z")
	public static boolean hasVertices(@OriginalArg(1) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0x800) != 0;
	}

	@OriginalMember(owner = "client!bja", name = "a", descriptor = "(III)Z")
	public static boolean altersFacing(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x10) != 0;
	}

	@OriginalMember(owner = "client!vo", name = "i", descriptor = "[I")
	public static final int[] matrixX = new int[8];

	@OriginalMember(owner = "client!dw", name = "a", descriptor = "(BII)Z")
	public static boolean retainsVerticesX(@OriginalArg(1) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0x70000) != 0 | GlModel.altersVerticesX(preferenceFlags, functionMask) || GlModel.altersAnyVerticeType(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!ig", name = "a", descriptor = "(III)Z")
	public static boolean altersFaces(@OriginalArg(0) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0x180) != 0;
	}

	@OriginalMember(owner = "client!os", name = "a", descriptor = "(III)Z")
	public static boolean hasElementsBuffer(@OriginalArg(0) int functionMask, @OriginalArg(1) int preferenceFlags) {
		return (functionMask & 0x800) != 0;
	}

	@OriginalMember(owner = "client!qu", name = "a", descriptor = "(IZI)Z")
	public static boolean retainsFaceMaterials(@OriginalArg(0) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return GlModel.altersTextures(preferenceFlags, functionMask) | (functionMask & 0x800) != 0 || GlModel.requiresColorBufferRebuild(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!sj", name = "a", descriptor = "(III)Z")
	public static boolean altersTextures(@OriginalArg(0) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x8000) != 0;
	}

	@OriginalMember(owner = "client!uw", name = "a", descriptor = "(III)Z")
	public static boolean requiresColorBufferRebuild(@OriginalArg(0) int functionMask, @OriginalArg(2) int preferenceFlags) {
		if (GlModel.hasColors(preferenceFlags, functionMask)) {
			return GpuModelRetainMasks.altersFaceColors(preferenceFlags, functionMask) | (functionMask & 0xB000) != 0 | GlModel.altersAlpha(functionMask, preferenceFlags) ? true : (GlModel.altersLight(preferenceFlags, functionMask) | GpuModelRetainMasks.altersShading(functionMask, preferenceFlags)) & (preferenceFlags & 0x37) == 0;
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!ci", name = "f", descriptor = "F")
	public static float modelViewYX;

	@OriginalMember(owner = "client!tda", name = "o", descriptor = "[I")
	public static final int[] matrixZ = new int[8];

	@OriginalMember(owner = "client!ev", name = "n", descriptor = "F")
	public static float modelViewZX;

	@OriginalMember(owner = "client!wm", name = "a", descriptor = "(IIZ)Z")
	public static boolean requiresNormalsBufferRebuild(@OriginalArg(0) int functionMask, @OriginalArg(1) int preferenceFlags) {
		return ((functionMask & 0x2000) != 0 | GlModel.altersLight(preferenceFlags, functionMask) | GpuModelRetainMasks.altersShading(functionMask, preferenceFlags)) & GlModel.hasNormals(preferenceFlags, functionMask);
	}

	@OriginalMember(owner = "client!dp", name = "a", descriptor = "(III)Z")
	public static boolean altersVerticesZ(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x34) != 0;
	}

	@OriginalMember(owner = "client!vr", name = "a", descriptor = "(III)Z")
	public static boolean altersAnyVerticeType(@OriginalArg(0) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (GlModel.altersVerticesX(preferenceFlags, functionMask) | GlModel.altersVerticesY(preferenceFlags, functionMask) | GlModel.altersVerticesZ(preferenceFlags, functionMask)) & GlModel.hasVertices(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!wk", name = "a", descriptor = "(BII)Z")
	public static boolean altersTextureCoordinates(@OriginalArg(1) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return false;
	}

	@OriginalMember(owner = "client!on", name = "a", descriptor = "(III)Z")
	public static boolean retainsVerticesZ(@OriginalArg(1) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return GlModel.altersVerticesZ(preferenceFlags, functionMask) | (functionMask & 0x70000) != 0 || GlModel.altersAnyVerticeType(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!kea", name = "a", descriptor = "(IIB)Z")
	public static boolean copyElementsBuffer(@OriginalArg(0) int functionMask, @OriginalArg(1) int preferenceFlags) {
		return GlModel.altersFacing(preferenceFlags, functionMask) & GlModel.hasElementsBuffer(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(III)Z")
	public static boolean retainsVerticesY(@OriginalArg(0) int preferenceFlags, @OriginalArg(1) int functionMask) {
		return (functionMask & 0x70000) != 0 | GlModel.altersVerticesY(preferenceFlags, functionMask) || GlModel.altersAnyVerticeType(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!re", name = "a", descriptor = "(BII)Z")
	public static boolean altersTextureUV(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return GlModel.altersTextureCoordinates(functionMask, preferenceFlags) & ModelTexCoordFlag.hasTextureCoordinates(preferenceFlags, functionMask);
	}

	@OriginalMember(owner = "client!bp", name = "u", descriptor = "F")
	public static float modelViewXW;

	@OriginalMember(owner = "client!kea", name = "a", descriptor = "(III)Z")
	public static boolean retainsFaceIndices(@OriginalArg(0) int functionMask, @OriginalArg(1) int preferenceFlags) {
		return GlModel.altersFacing(preferenceFlags, functionMask) | (functionMask & 0x60000) != 0 || GlModel.requiresColorBufferRebuild(functionMask, preferenceFlags) || copyElementsBuffer(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!eh", name = "a", descriptor = "(III)Z")
	public static boolean altersVerticesX(@OriginalArg(0) int preferenceFlags, @OriginalArg(1) int functionMask) {
		return (functionMask & 0x21) != 0;
	}

	@OriginalMember(owner = "client!os", name = "b", descriptor = "(III)Z")
	public static boolean altersLight(@OriginalArg(0) int preferenceFlags, @OriginalArg(1) int functionMask) {
		return (functionMask & 0x18) != 0 | (functionMask & 0x220) == 544;
	}

	@OriginalMember(owner = "client!ol", name = "f", descriptor = "[F")
	public static final float[] textureCoordsOut = new float[2];

	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(III)Z")
	public static boolean retainsFaceColors(@OriginalArg(1) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return GpuModelRetainMasks.altersFaceColors(preferenceFlags, functionMask) || GlModel.requiresColorBufferRebuild(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!sb", name = "a", descriptor = "(BII)Z")
	public static boolean copyBillboards(@OriginalArg(1) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0xC580) != 0;
	}

	@OriginalMember(owner = "client!gv", name = "f", descriptor = "[I")
	public static final int[] matrixY = new int[8];

	@OriginalMember(owner = "client!vda", name = "a", descriptor = "(III)Z")
	public static boolean altersAlpha(@OriginalArg(0) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0x100100) != 0;
	}

	@OriginalMember(owner = "client!os", name = "a", descriptor = "F")
	public static float modelViewYW;

	@OriginalMember(owner = "client!sia", name = "a", descriptor = "(ZII)Z")
	public static boolean altersBillboards(@OriginalArg(1) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0x400) != 0;
	}

	@OriginalMember(owner = "client!nba", name = "a", descriptor = "(BII)Z")
	public static boolean hasNormals(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x800) != 0 && (preferenceFlags & 0x37) != 0;
	}

	@OriginalMember(owner = "client!ad", name = "a", descriptor = "(ZII)Z")
	public static boolean altersVerticesY(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x22) != 0;
	}

	@OriginalMember(owner = "client!tu", name = "f", descriptor = "F")
	public static float modelViewYY;
	@OriginalMember(owner = "client!bia", name = "a", descriptor = "(IIZ)Z")
	public static boolean hasColors(@OriginalArg(0) int preferenceFlags, @OriginalArg(1) int functionMask) {
		return (functionMask & 0x800) != 0;
	}

	@OriginalMember(owner = "client!qea", name = "a", descriptor = "(III)I")
	public static int adjustLuminance(@OriginalArg(0) int hsl, @OriginalArg(2) int luminanceFactor) {
		luminanceFactor = (hsl & 0x7F) * luminanceFactor >> 7;
		if (luminanceFactor < 2) {
			luminanceFactor = 2;
		} else if (luminanceFactor > 126) {
			luminanceFactor = 126;
		}
		return luminanceFactor + (hsl & 0xFF80);
	}

	@OriginalMember(owner = "client!fs", name = "a", descriptor = "(III)Z")
	public static boolean retainsFaceAlphas(@OriginalArg(0) int preferenceFlags, @OriginalArg(1) int functionMask) {
		return GlModel.altersAlpha(functionMask, preferenceFlags) | (functionMask & 0x40000) != 0 || GlModel.requiresColorBufferRebuild(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!jd", name = "c", descriptor = "(III)Z")
	public static boolean retainsNormals(@OriginalArg(0) int functionMask, @OriginalArg(2) int preferenceFlags) {
		if (GlModel.altersLight(preferenceFlags, functionMask) | (functionMask & 0x10000) != 0 || GlModel.requiresNormalsBufferRebuild(functionMask, preferenceFlags)) {
			return true;
		} else {
			return (preferenceFlags & 0x37) == 0 && GlModel.requiresColorBufferRebuild(functionMask, preferenceFlags);
		}
	}

	@OriginalMember(owner = "client!mda", name = "a", descriptor = "(IIB)Z")
	public static boolean retainsTextureCoordinates(@OriginalArg(0) int functionMask, @OriginalArg(1) int preferenceFlags) {
		return GlModel.altersTextureCoordinates(functionMask, preferenceFlags) || GlModel.altersTextureUV(preferenceFlags, functionMask);
	}
}
