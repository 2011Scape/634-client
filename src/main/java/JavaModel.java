import com.jagex.game.runetek5.graphics.texture.TextureTransformationMatrix;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.JavaMatrix;
import com.jagex.game.runetek5.graphics.model.billboard.BillboardType;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.JavaBillboardAttributes;
import com.jagex.game.runetek5.graphics.JavaBillboardFace;
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
import com.jagex.game.runetek5.graphics.model.normal.FaceNormal;
import com.jagex.game.runetek5.graphics.safe.PureJavaModel;
import com.jagex.game.runetek5.graphics.model.normal.VertexNormal;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.ColorUtil;
import com.jagex.game.runetek5.utils.ArrayUtils;
import com.jagex.game.runetek5.graphics.model.billboard.BillboardTypeList;

@OriginalClass("client!jga")
public final class JavaModel extends Model {

	@OriginalMember(owner = "client!jga", name = "o", descriptor = "[S")
	private short[] faceColour;

	@OriginalMember(owner = "client!jga", name = "p", descriptor = "[I")
	private int[] vertexZ;

	@OriginalMember(owner = "client!jga", name = "q", descriptor = "[Lclient!fb;")
	private VertexNormal[] vertexNormalsOffset;

	@OriginalMember(owner = "client!jga", name = "r", descriptor = "[[F")
	private float[][] texCoordU;

	@OriginalMember(owner = "client!jga", name = "s", descriptor = "[I")
	private int[] clippedX;

	@OriginalMember(owner = "client!jga", name = "t", descriptor = "I")
	private int functionMask;

	@OriginalMember(owner = "client!jga", name = "u", descriptor = "[S")
	private short[] faceA;

	@OriginalMember(owner = "client!jga", name = "v", descriptor = "[I")
	private int[] otherMergeStamps;

	@OriginalMember(owner = "client!jga", name = "w", descriptor = "Lclient!jga;")
	private JavaModel copySrcModel5;

	@OriginalMember(owner = "client!jga", name = "x", descriptor = "Lclient!gq;")
	private JavaMatrix modelMatrix;

	@OriginalMember(owner = "client!jga", name = "y", descriptor = "[I")
	private int[] screenX;

	@OriginalMember(owner = "client!jga", name = "z", descriptor = "[Lclient!nn;")
	private EmissiveTriangle[] emitters;

	@OriginalMember(owner = "client!jga", name = "B", descriptor = "S")
	private short radius;

	@OriginalMember(owner = "client!jga", name = "C", descriptor = "[B")
	private byte[] faceAlpha;

	@OriginalMember(owner = "client!jga", name = "D", descriptor = "[I")
	private int[] mergeStamps;

	@OriginalMember(owner = "client!jga", name = "E", descriptor = "[S")
	private short[] faceIndices;

	@OriginalMember(owner = "client!jga", name = "F", descriptor = "Lclient!jga;")
	private JavaModel copySrcModel2;

	@OriginalMember(owner = "client!jga", name = "G", descriptor = "S")
	private short minY;

	@OriginalMember(owner = "client!jga", name = "H", descriptor = "[[I")
	private int[][] faceLabels;

	@OriginalMember(owner = "client!jga", name = "I", descriptor = "S")
	private short maxZ;

	@OriginalMember(owner = "client!jga", name = "J", descriptor = "[I")
	private int[] vertexY;

	@OriginalMember(owner = "client!jga", name = "K", descriptor = "[S")
	private short[] originModels;

	@OriginalMember(owner = "client!jga", name = "L", descriptor = "[[I")
	private int[][] vertexLabels;

	@OriginalMember(owner = "client!jga", name = "M", descriptor = "[Lclient!eca;")
	private EffectiveVertex[] effectors;

	@OriginalMember(owner = "client!jga", name = "N", descriptor = "Lclient!pga;")
	private JavaThreadResource copyThreadResource;

	@OriginalMember(owner = "client!jga", name = "O", descriptor = "[I")
	private int[] faceColourB;

	@OriginalMember(owner = "client!jga", name = "P", descriptor = "I")
	private int pivotX;

	@OriginalMember(owner = "client!jga", name = "Q", descriptor = "Lclient!jga;")
	private JavaModel copyDstModel2;

	@OriginalMember(owner = "client!jga", name = "T", descriptor = "I")
	private int pivotZ;

	@OriginalMember(owner = "client!jga", name = "U", descriptor = "[I")
	private int[] screenY;

	@OriginalMember(owner = "client!jga", name = "V", descriptor = "I")
	private int ambient;

	@OriginalMember(owner = "client!jga", name = "W", descriptor = "[[F")
	private float[][] texCoordV;

	@OriginalMember(owner = "client!jga", name = "Y", descriptor = "Lclient!jga;")
	private JavaModel copyDstModel4;

	@OriginalMember(owner = "client!jga", name = "Z", descriptor = "S")
	private short sphereRadius;

	@OriginalMember(owner = "client!jga", name = "ab", descriptor = "I")
	private int contrast;

	@OriginalMember(owner = "client!jga", name = "db", descriptor = "S")
	private short minZ;

	@OriginalMember(owner = "client!jga", name = "gb", descriptor = "[I")
	private int[] screenZ;

	@OriginalMember(owner = "client!jga", name = "hb", descriptor = "[I")
	private int[] vertexX;

	@OriginalMember(owner = "client!jga", name = "jb", descriptor = "[B")
	private byte[] facePriority;

	@OriginalMember(owner = "client!jga", name = "kb", descriptor = "[I")
	private int[] boundsCornerX;

	@OriginalMember(owner = "client!jga", name = "lb", descriptor = "[Lclient!fb;")
	private VertexNormal[] lb;

	@OriginalMember(owner = "client!jga", name = "mb", descriptor = "[Lclient!go;")
	private JavaBillboardFace[] billboardFaces;

	@OriginalMember(owner = "client!jga", name = "nb", descriptor = "[I")
	private int[] worldY;

	@OriginalMember(owner = "client!jga", name = "ob", descriptor = "[Lclient!ei;")
	private JavaBillboardAttributes[] billboardAttributes;

	@OriginalMember(owner = "client!jga", name = "pb", descriptor = "[S")
	private short[] faceC;

	@OriginalMember(owner = "client!jga", name = "qb", descriptor = "S")
	private short maxY;

	@OriginalMember(owner = "client!jga", name = "rb", descriptor = "[I")
	private int[] faceColourC;

	@OriginalMember(owner = "client!jga", name = "sb", descriptor = "Lclient!pga;")
	private JavaThreadResource threadResource;

	@OriginalMember(owner = "client!jga", name = "tb", descriptor = "[I")
	private int[] faceColourA;

	@OriginalMember(owner = "client!jga", name = "vb", descriptor = "[S")
	private short[] faceOriginModels;

	@OriginalMember(owner = "client!jga", name = "wb", descriptor = "[I")
	private int[] clippedY;

	@OriginalMember(owner = "client!jga", name = "yb", descriptor = "[I")
	private int[] boundsCornerY;

	@OriginalMember(owner = "client!jga", name = "zb", descriptor = "Lclient!jga;")
	private JavaModel copySrcModel1;

	@OriginalMember(owner = "client!jga", name = "Ab", descriptor = "I")
	private int pivotY;

	@OriginalMember(owner = "client!jga", name = "Bb", descriptor = "Lclient!jga;")
	private JavaModel copySrcModel3;

	@OriginalMember(owner = "client!jga", name = "Cb", descriptor = "[I")
	private int[] clippedColour;

	@OriginalMember(owner = "client!jga", name = "Db", descriptor = "Lclient!jga;")
	private JavaModel copyDstModel1;

	@OriginalMember(owner = "client!jga", name = "Eb", descriptor = "[[I")
	private int[][] billboardLabels;

	@OriginalMember(owner = "client!jga", name = "Fb", descriptor = "Z")
	private boolean recoloured;

	@OriginalMember(owner = "client!jga", name = "Gb", descriptor = "[I")
	private int[] clippedZ;

	@OriginalMember(owner = "client!jga", name = "Ib", descriptor = "Lclient!mp;")
	private Rasterizer rasterizer;

	@OriginalMember(owner = "client!jga", name = "Jb", descriptor = "[I")
	private int[] cameraY;

	@OriginalMember(owner = "client!jga", name = "Kb", descriptor = "[Lclient!iga;")
	private FaceNormal[] faceNormals;

	@OriginalMember(owner = "client!jga", name = "Lb", descriptor = "[B")
	private byte[] shadingType;

	@OriginalMember(owner = "client!jga", name = "Nb", descriptor = "[S")
	private short[] faceTextures;

	@OriginalMember(owner = "client!jga", name = "Ob", descriptor = "I")
	private int billboardCount;

	@OriginalMember(owner = "client!jga", name = "Pb", descriptor = "Lclient!jga;")
	private JavaModel copyDstModel5;

	@OriginalMember(owner = "client!jga", name = "Rb", descriptor = "[I")
	private int[] cameraZ;

	@OriginalMember(owner = "client!jga", name = "Sb", descriptor = "S")
	private short maxX;

	@OriginalMember(owner = "client!jga", name = "Tb", descriptor = "[S")
	private short[] faceB;

	@OriginalMember(owner = "client!jga", name = "Ub", descriptor = "S")
	private short minX;

	@OriginalMember(owner = "client!jga", name = "Vb", descriptor = "Lclient!jga;")
	private JavaModel copyDstModel3;

	@OriginalMember(owner = "client!jga", name = "Wb", descriptor = "Lclient!jga;")
	private JavaModel copySrcModel4;

	@OriginalMember(owner = "client!jga", name = "Xb", descriptor = "[I")
	private int[] boundsCornerZ;

	@OriginalMember(owner = "client!jga", name = "Yb", descriptor = "[I")
	private int[] cameraX;

	@OriginalMember(owner = "client!jga", name = "Zb", descriptor = "[I")
	private int[] faceBillboard;

	@OriginalMember(owner = "client!jga", name = "A", descriptor = "Z")
	private boolean boundsValid = false;

	@OriginalMember(owner = "client!jga", name = "R", descriptor = "I")
	private int vertexCount = 0;

	@OriginalMember(owner = "client!jga", name = "ib", descriptor = "Z")
	private boolean rendering = false;

	@OriginalMember(owner = "client!jga", name = "fb", descriptor = "I")
	private int lightingState = 0;

	@OriginalMember(owner = "client!jga", name = "ub", descriptor = "Z")
	private boolean pivotDirty = false;

	@OriginalMember(owner = "client!jga", name = "cb", descriptor = "I")
	private int faceCount = 0;

	@OriginalMember(owner = "client!jga", name = "xb", descriptor = "Z")
	private boolean highPrecisionVertices = false;

	@OriginalMember(owner = "client!jga", name = "Qb", descriptor = "Z")
	private boolean movingTextures = false;

	@OriginalMember(owner = "client!jga", name = "Mb", descriptor = "I")
	private int maxVertex = 0;

	@OriginalMember(owner = "client!jga", name = "Hb", descriptor = "Z")
	private boolean transparent = false;

	@OriginalMember(owner = "client!jga", name = "eb", descriptor = "Lclient!ao;")
	private final JavaToolkit toolkit;

	@OriginalMember(owner = "client!jga", name = "<init>", descriptor = "(Lclient!ao;)V")
	public JavaModel(@OriginalArg(0) JavaToolkit javaToolkit) {
		this.toolkit = javaToolkit;
	}

	@OriginalMember(owner = "client!jga", name = "<init>", descriptor = "(Lclient!ao;Lclient!bh;IIII)V")
	public JavaModel(@OriginalArg(0) JavaToolkit javaToolkit, @OriginalArg(1) Mesh mesh, @OriginalArg(2) int functionMask, @OriginalArg(3) int ambient, @OriginalArg(4) int contrast, @OriginalArg(5) int featureMask) {
		this.toolkit = javaToolkit;
		this.functionMask = functionMask;
		this.ambient = ambient;
		this.contrast = contrast;
		@Pc(47) MaterialProvider textureSource = this.toolkit.materialProvider;
		this.vertexCount = mesh.numVertices;
		this.maxVertex = mesh.maxDepth;
		this.vertexX = mesh.verticesX;
		this.vertexY = mesh.verticesY;
		this.vertexZ = mesh.verticesZ;
		this.faceCount = mesh.numFaces;
		this.faceA = mesh.facesA;
		this.faceB = mesh.facesB;
		this.faceC = mesh.facesC;
		this.facePriority = mesh.facesLayer;
		this.faceColour = mesh.facesColor;
		this.faceAlpha = mesh.facesAlpha;
		this.faceOriginModels = mesh.facesSubmeshes;
		this.shadingType = mesh.faceRenderType;
		this.emitters = mesh.particleEmitters;
		this.effectors = mesh.particleEffectors;
		this.originModels = mesh.verticesSubmeshes;
		@Pc(119) int[] faceOrder = new int[this.faceCount];
		@Pc(121) int index = 0;
		while (index < this.faceCount) {
			faceOrder[index] = index++;
		}
		@Pc(135) long[] sortKeys = new long[this.faceCount];
		@Pc(144) boolean usePriority = (this.functionMask & 0x100) != 0;
		@Pc(151) int local151;
		@Pc(315) int local315;
		for (@Pc(146) int i = 0; i < this.faceCount; i++) {
			int face = faceOrder[i];
			@Pc(153) MaterialType textureMetrics = null;
			@Pc(155) int keyHigh = 0;
			@Pc(157) byte materialKeyBase = 0;
			@Pc(159) byte effectType = 0;
			@Pc(161) byte effectParam1 = 0;
			if (mesh.billboards != null) {
				@Pc(166) boolean hasUidBillboard = false;
				for (@Pc(168) int j = 0; j < mesh.billboards.length; j++) {
					@Pc(174) MeshBillboard meshBillboard = mesh.billboards[j];
					if (face == meshBillboard.appliesOnFace) {
						@Pc(183) BillboardType lightType = BillboardTypeList.list(meshBillboard.billboardId);
						if (lightType.hasUid) {
							hasUidBillboard = true;
						}
						if (lightType.materialId != -1) {
							@Pc(198) MaterialType textureMetrics2 = textureSource.getMetrics(lightType.materialId);
							if (textureMetrics2.effectCombiner == 2) {
								this.transparent = true;
							}
						}
					}
				}
				if (hasUidBillboard) {
					sortKeys[i] = Long.MAX_VALUE;
				}
			}
			@Pc(219) short material = -1;
			if (mesh.facesMaterial != null) {
				material = mesh.facesMaterial[face];
				if (material != -1) {
					textureMetrics = textureSource.getMetrics(material & 0xFFFF);
					if ((featureMask & 0x40) != 0 && textureMetrics.detailsOnly) {
						material = -1;
					} else {
						effectType = textureMetrics.effectType;
						effectParam1 = textureMetrics.effectParam1;
					}
				}
			}
			@Pc(271) boolean transparentFace = this.faceAlpha != null && this.faceAlpha[face] != 0 || textureMetrics != null && textureMetrics.effectCombiner == 2;
			if ((usePriority || transparentFace) && this.facePriority != null) {
				keyHigh += this.facePriority[face] << 17;
			}
			if (transparentFace) {
				keyHigh += 65536;
			}
			keyHigh += (effectType & 0xFF) << 8;
			keyHigh += effectParam1 & 0xFF;
			int materialKey = materialKeyBase + ((material & 0xFFFF) << 16);
			@Pc(321) int keyLow = materialKey + (i & 0xFFFF);
			sortKeys[i] = ((long) keyHigh << 32) + (long) keyLow;
			this.transparent |= transparentFace;
		}
		ArrayUtils.quicksort(faceOrder, sortKeys);
		if (mesh.billboards != null) {
			this.billboardCount = mesh.billboards.length;
			this.billboardFaces = new JavaBillboardFace[this.billboardCount];
			this.billboardAttributes = new JavaBillboardAttributes[this.billboardCount];
			for (int billboardIndex = 0; billboardIndex < mesh.billboards.length; billboardIndex++) {
				@Pc(372) MeshBillboard meshBillboard2 = mesh.billboards[billboardIndex];
				@Pc(377) BillboardType lightType2 = BillboardTypeList.list(meshBillboard2.billboardId);
				int billboardColour = ColorUtil.hsvToRgbTable[mesh.facesColor[meshBillboard2.appliesOnFace] & 0xFFFF] & 0xFFFFFF;
				billboardColour |= 255 - (mesh.facesAlpha == null ? 0 : mesh.facesAlpha[meshBillboard2.appliesOnFace] & 0xFF) << 24;
				this.billboardFaces[billboardIndex] = new JavaBillboardFace(meshBillboard2.appliesOnFace, mesh.facesA[meshBillboard2.appliesOnFace], mesh.facesB[meshBillboard2.appliesOnFace], mesh.facesC[meshBillboard2.appliesOnFace], lightType2.size2d, lightType2.size3d, lightType2.materialId, lightType2.blendType, lightType2.shape, lightType2.hasUid, meshBillboard2.distance);
				this.billboardAttributes[billboardIndex] = new JavaBillboardAttributes(billboardColour);
			}
		}
		this.texCoordU = new float[this.faceCount][];
		this.texCoordV = new float[this.faceCount][];
		@Pc(477) TextureTransformationMatrix textureMatrix = TextureTransformationMatrix.generateTextureMatrix(this.faceCount, mesh, faceOrder);
		@Pc(482) JavaThreadResource javaThreadResource = this.toolkit.threadResource(Thread.currentThread());
		@Pc(485) float[] texCoordScratch = javaThreadResource.texCoordScratch;
		@Pc(487) boolean textured = false;
		@Pc(494) int face;
		@Pc(515) short material;
		@Pc(529) MaterialType textureMetrics3;
		@Pc(589) int local589;
		for (@Pc(489) int k = 0; k < this.faceCount; k++) {
			face = faceOrder[k];
			@Pc(499) byte mappingIndex;
			if (mesh.facesTexture == null) {
				mappingIndex = -1;
			} else {
				mappingIndex = mesh.facesTexture[face];
			}
			material = mesh.facesMaterial == null ? -1 : mesh.facesMaterial[face];
			if (material != -1 && (featureMask & 0x40) != 0) {
				textureMetrics3 = textureSource.getMetrics(material & 0xFFFF);
				if (textureMetrics3.detailsOnly) {
					material = -1;
				}
			}
			if (material != -1) {
				textured = true;
				@Pc(547) float[] u = this.texCoordU[face] = new float[3];
				@Pc(555) float[] v = this.texCoordV[face] = new float[3];
				if (mappingIndex == -1) {
					u[0] = 0.0F;
					v[0] = 1.0F;
					u[1] = 1.0F;
					v[1] = 1.0F;
					u[2] = 0.0F;
					v[2] = 0.0F;
				} else {
					int textureIndex = mappingIndex & 0xFF;
					@Pc(594) byte mapping = mesh.texturesMapping[textureIndex];
					@Pc(601) short a;
					@Pc(606) short b;
					@Pc(611) short c;
					@Pc(644) float local644;
					@Pc(652) float local652;
					@Pc(660) float local660;
					@Pc(740) float local740;
					@Pc(748) float local748;
					@Pc(756) float local756;
					@Pc(764) float local764;
					@Pc(772) float local772;
					@Pc(780) float local780;
					if (mapping == 0) {
						a = this.faceA[face];
						b = this.faceB[face];
						c = this.faceC[face];
						@Pc(616) short texP = mesh.textureFaceX[textureIndex];
						@Pc(621) short texM = mesh.textureFaceY[textureIndex];
						@Pc(626) short texN = mesh.textureFaceZ[textureIndex];
						@Pc(632) float originX = (float) this.vertexX[texP];
						@Pc(638) float originY = (float) this.vertexY[texP];
						float originZ = this.vertexZ[texP];
						float mX = (float) this.vertexX[texM] - originX;
						float mY = (float) this.vertexY[texM] - originY;
						@Pc(668) float mZ = (float) this.vertexZ[texM] - originZ;
						@Pc(676) float nX = (float) this.vertexX[texN] - originX;
						@Pc(684) float nY = (float) this.vertexY[texN] - originY;
						@Pc(692) float nZ = (float) this.vertexZ[texN] - originZ;
						@Pc(700) float aX = (float) this.vertexX[a] - originX;
						@Pc(708) float aY = (float) this.vertexY[a] - originY;
						@Pc(716) float aZ = (float) this.vertexZ[a] - originZ;
						@Pc(724) float bX = (float) this.vertexX[b] - originX;
						@Pc(732) float bY = (float) this.vertexY[b] - originY;
						float bZ = (float) this.vertexZ[b] - originZ;
						float cX = (float) this.vertexX[c] - originX;
						float cY = (float) this.vertexY[c] - originY;
						float cZ = (float) this.vertexZ[c] - originZ;
						float crossX = mY * nZ - mZ * nY;
						float crossY = mZ * nX - mX * nZ;
						@Pc(788) float crossZ = mX * nY - mY * nX;
						@Pc(796) float uBasisX = nY * crossZ - nZ * crossY;
						@Pc(804) float uBasisY = nZ * crossX - nX * crossZ;
						@Pc(812) float uBasisZ = nX * crossY - nY * crossX;
						@Pc(826) float uScale = 1.0F / (uBasisX * mX + uBasisY * mY + uBasisZ * mZ);
						u[0] = (uBasisX * aX + uBasisY * aY + uBasisZ * aZ) * uScale;
						u[1] = (uBasisX * bX + uBasisY * bY + uBasisZ * bZ) * uScale;
						u[2] = (uBasisX * cX + uBasisY * cY + uBasisZ * cZ) * uScale;
						@Pc(882) float vBasisX = mY * crossZ - mZ * crossY;
						@Pc(890) float vBasisY = mZ * crossX - mX * crossZ;
						@Pc(898) float vBasisZ = mX * crossY - mY * crossX;
						@Pc(912) float vScale = 1.0F / (vBasisX * nX + vBasisY * nY + vBasisZ * nZ);
						v[0] = (vBasisX * aX + vBasisY * aY + vBasisZ * aZ) * vScale;
						v[1] = (vBasisX * bX + vBasisY * bY + vBasisZ * bZ) * vScale;
						v[2] = (vBasisX * cX + vBasisY * cY + vBasisZ * cZ) * vScale;
					} else {
						a = this.faceA[face];
						b = this.faceB[face];
						c = this.faceC[face];
						@Pc(981) int centerX = textureMatrix.centerX[textureIndex];
						@Pc(986) int centerY = textureMatrix.centerY[textureIndex];
						@Pc(991) int centerZ = textureMatrix.centerZ[textureIndex];
						@Pc(996) float[] matrix = textureMatrix.matrix[textureIndex];
						@Pc(1001) byte direction = mesh.texturesDirection[textureIndex];
						float speedOffset = (float) mesh.texturesSpeed[textureIndex] / 256.0F;
						if (mapping == 1) {
							float uScale = (float) mesh.texturesScaleZ[textureIndex] / 1024.0F;
							Model.mapCylindricalUv(centerZ, this.vertexZ[a], direction, this.vertexX[a], texCoordScratch, this.vertexY[a], speedOffset, centerY, centerX, uScale, matrix);
							u[0] = texCoordScratch[0];
							v[0] = texCoordScratch[1];
							Model.mapCylindricalUv(centerZ, this.vertexZ[b], direction, this.vertexX[b], texCoordScratch, this.vertexY[b], speedOffset, centerY, centerX, uScale, matrix);
							u[1] = texCoordScratch[0];
							v[1] = texCoordScratch[1];
							Model.mapCylindricalUv(centerZ, this.vertexZ[c], direction, this.vertexX[c], texCoordScratch, this.vertexY[c], speedOffset, centerY, centerX, uScale, matrix);
							u[2] = texCoordScratch[0];
							v[2] = texCoordScratch[1];
							float halfUScale = uScale / 2.0F;
							if ((direction & 0x1) == 0) {
								if (u[1] - u[0] > halfUScale) {
									u[1] -= uScale;
								} else if (u[0] - u[1] > halfUScale) {
									u[1] += uScale;
								}
								if (u[2] - u[0] > halfUScale) {
									u[2] -= uScale;
								} else if (u[0] - u[2] > halfUScale) {
									u[2] += uScale;
								}
							} else {
								if (v[1] - v[0] > halfUScale) {
									v[1] -= uScale;
								} else if (v[0] - v[1] > halfUScale) {
									v[1] += uScale;
								}
								if (v[2] - v[0] > halfUScale) {
									v[2] -= uScale;
								} else if (v[0] - v[2] > halfUScale) {
									v[2] += uScale;
								}
							}
						} else if (mapping == 2) {
							float uTrans = (float) mesh.texturesUtrans[textureIndex] / 256.0F;
							float vTrans = (float) mesh.texturesVtrans[textureIndex] / 256.0F;
							@Pc(1301) int edge1X = this.vertexX[b] - this.vertexX[a];
							@Pc(1311) int edge1Y = this.vertexY[b] - this.vertexY[a];
							@Pc(1321) int edge1Z = this.vertexZ[b] - this.vertexZ[a];
							@Pc(1331) int edge2X = this.vertexX[c] - this.vertexX[a];
							@Pc(1341) int edge2Y = this.vertexY[c] - this.vertexY[a];
							@Pc(1351) int edge2Z = this.vertexZ[c] - this.vertexZ[a];
							@Pc(1359) int normalX = edge1Y * edge2Z - edge2Y * edge1Z;
							@Pc(1367) int normalY = edge1Z * edge2X - edge2Z * edge1X;
							@Pc(1375) int normalZ = edge1X * edge2Y - edge2X * edge1Y;
							float scaleX = 64.0F / (float) mesh.texturesScaleX[textureIndex];
							float scaleY = 64.0F / (float) mesh.texturesScaleY[textureIndex];
							float scaleZ = 64.0F / (float) mesh.texturesScaleZ[textureIndex];
							float scaledNormalX = ((float) normalX * matrix[0] + (float) normalY * matrix[1] + (float) normalZ * matrix[2]) / scaleX;
							float scaledNormalY = ((float) normalX * matrix[3] + (float) normalY * matrix[4] + (float) normalZ * matrix[5]) / scaleY;
							float scaledNormalZ = ((float) normalX * matrix[6] + (float) normalY * matrix[7] + (float) normalZ * matrix[8]) / scaleZ;
							@Pc(1474) int cubeFace = Model.getCubeFace(scaledNormalY, scaledNormalZ, scaledNormalX);
							Model.mapCubeUv(vTrans, speedOffset, matrix, this.vertexZ[a], centerZ, direction, centerX, this.vertexX[a], this.vertexY[a], uTrans, texCoordScratch, centerY, cubeFace);
							u[0] = texCoordScratch[0];
							v[0] = texCoordScratch[1];
							Model.mapCubeUv(vTrans, speedOffset, matrix, this.vertexZ[b], centerZ, direction, centerX, this.vertexX[b], this.vertexY[b], uTrans, texCoordScratch, centerY, cubeFace);
							u[1] = texCoordScratch[0];
							v[1] = texCoordScratch[1];
							Model.mapCubeUv(vTrans, speedOffset, matrix, this.vertexZ[c], centerZ, direction, centerX, this.vertexX[c], this.vertexY[c], uTrans, texCoordScratch, centerY, cubeFace);
							u[2] = texCoordScratch[0];
							v[2] = texCoordScratch[1];
						} else if (mapping == 3) {
							Model.mapSphericalUv(centerZ, direction, speedOffset, this.vertexX[a], texCoordScratch, this.vertexZ[a], centerX, this.vertexY[a], centerY, matrix);
							u[0] = texCoordScratch[0];
							v[0] = texCoordScratch[1];
							Model.mapSphericalUv(centerZ, direction, speedOffset, this.vertexX[b], texCoordScratch, this.vertexZ[b], centerX, this.vertexY[b], centerY, matrix);
							u[1] = texCoordScratch[0];
							v[1] = texCoordScratch[1];
							Model.mapSphericalUv(centerZ, direction, speedOffset, this.vertexX[c], texCoordScratch, this.vertexZ[c], centerX, this.vertexY[c], centerY, matrix);
							u[2] = texCoordScratch[0];
							v[2] = texCoordScratch[1];
							@Pc(1703) int local1703;
							if ((direction & 0x1) == 0) {
								if (u[1] - u[0] > 0.5F) {
									u[1]--;
								} else if (u[0] - u[1] > 0.5F) {
									u[1]++;
								}
								if (u[2] - u[0] > 0.5F) {
									u[2]--;
								} else if (u[0] - u[2] > 0.5F) {
									u[2]++;
								}
							} else {
								if (v[1] - v[0] > 0.5F) {
									v[1]--;
								} else if (v[0] - v[1] > 0.5F) {
									v[1]++;
								}
								if (v[2] - v[0] > 0.5F) {
									v[2]--;
								} else if (v[0] - v[2] > 0.5F) {
									v[2]++;
								}
							}
						}
					}
				}
			}
		}
		if (!textured) {
			this.texCoordU = this.texCoordV = null;
		}
		if (mesh.vertexGroups != null && (this.functionMask & 0x20) != 0) {
			this.vertexLabels = mesh.createVertexGroups(true);
		}
		if (mesh.faceGroups != null && (this.functionMask & 0x180) != 0) {
			this.faceLabels = mesh.createFaceGroups();
		}
		if (mesh.billboards != null && (this.functionMask & 0x400) != 0) {
			this.billboardLabels = mesh.createBillboardGroups();
		}
		if (mesh.facesMaterial == null) {
			this.faceTextures = null;
		} else {
			this.faceTextures = new short[this.faceCount];
			@Pc(1893) boolean hasTextures = false;
			for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
				material = mesh.facesMaterial[faceIndex];
				if (material == -1) {
					this.faceTextures[faceIndex] = -1;
				} else {
					textureMetrics3 = this.toolkit.materialProvider.getMetrics(material);
					if ((featureMask & 0x40) != 0 && textureMetrics3.detailsOnly) {
						this.faceTextures[faceIndex] = -1;
					} else {
						this.faceTextures[faceIndex] = material;
						hasTextures = true;
						if (textureMetrics3.effectCombiner == 2) {
							this.transparent = true;
						}
						if (textureMetrics3.speedU != 0 || textureMetrics3.speedV != 0) {
							this.movingTextures = true;
						}
					}
				}
			}
			if (!hasTextures) {
				this.faceTextures = null;
			}
		}
		if (this.transparent || this.billboardFaces != null) {
			this.faceIndices = new short[this.faceCount];
			for (face = 0; face < this.faceCount; face++) {
				this.faceIndices[face] = (short) faceOrder[face];
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "b", descriptor = "(I)Z")
	private boolean isTranslucent(@OriginalArg(0) int face) {
		if (this.faceAlpha == null) {
			return false;
		} else {
			return this.faceAlpha[face] != 0;
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(IIIIIIII)Z")
	private boolean pointInFaceBounds(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int y0, @OriginalArg(3) int y1, @OriginalArg(4) int y2, @OriginalArg(5) int x0, @OriginalArg(6) int x1, @OriginalArg(7) int x2) {
		if (y < y0 && y < y1 && y < y2) {
			return false;
		} else if (y > y0 && y > y1 && y > y2) {
			return false;
		} else if (x < x0 && x < x1 && x < x2) {
			return false;
		} else {
			return x <= x0 || x <= x1 || x <= x2;
		}
	}

	@OriginalMember(owner = "client!jga", name = "g", descriptor = "()V")
	private void invalidateNormals() {
		this.lb = null;
		this.vertexNormalsOffset = null;
		this.faceNormals = null;
		this.boundsValid = false;
	}

	@OriginalMember(owner = "client!jga", name = "EA", descriptor = "()I")
	@Override
	public int EA() {
		if (!this.boundsValid) {
			this.calculateBounds();
		}
		return this.maxY;
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Ljava/lang/Thread;)V")
	private void useThreadBuffers(@OriginalArg(0) Thread thread) {
		@Pc(4) JavaThreadResource resource = this.toolkit.threadResource(thread);
		this.rasterizer = resource.rasterizer;
		if (resource == this.threadResource) {
			return;
		}
		this.threadResource = resource;
		this.worldY = this.threadResource.worldY;
		this.cameraX = this.threadResource.cameraX;
		this.cameraY = this.threadResource.cameraY;
		this.cameraZ = this.threadResource.cameraZ;
		this.screenX = this.threadResource.vertexScreenX;
		this.screenY = this.threadResource.vertexScreenY;
		this.screenZ = this.threadResource.vertexScreenZ;
		this.boundsCornerX = this.threadResource.boundsCornerX;
		this.boundsCornerY = this.threadResource.boundsCornerY;
		this.boundsCornerZ = this.threadResource.boundsCornerZ;
		this.clippedX = this.threadResource.clippedX;
		this.clippedY = this.threadResource.clippedY;
		this.clippedZ = this.threadResource.clippedZ;
		this.clippedColour = this.threadResource.clippedColour;
		this.mergeStamps = this.threadResource.mergeStamps;
		this.otherMergeStamps = this.threadResource.otherMergeStamps;
		this.faceBillboard = this.threadResource.faceBillboard;
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Lclient!jga;Lclient!jga;IZZ)Lclient!ka;")
	private Model copyTo(@OriginalArg(0) JavaModel target, @OriginalArg(1) JavaModel buffers, @OriginalArg(2) int functionMask, @OriginalArg(3) boolean reserve, @OriginalArg(4) boolean ensureLit) {
		target.boundsValid = this.boundsValid;
		if (this.boundsValid) {
			target.maxX = this.maxX;
			target.maxY = this.maxY;
			target.maxZ = this.maxZ;
			target.minX = this.minX;
			target.minY = this.minY;
			target.minZ = this.minZ;
			target.radius = this.radius;
			target.sphereRadius = this.sphereRadius;
		}
		target.ambient = this.ambient;
		target.contrast = this.contrast;
		target.vertexCount = this.vertexCount;
		target.maxVertex = this.maxVertex;
		target.faceCount = this.faceCount;
		target.billboardCount = this.billboardCount;
		if ((functionMask & 0x100) == 0) {
			target.transparent = this.transparent;
		} else {
			target.transparent = true;
		}
		target.movingTextures = this.movingTextures;
		@Pc(95) boolean copyAll = (functionMask & 0x7) == 7 | (functionMask & 0x20) != 0;
		@Pc(105) boolean copyX = copyAll || (functionMask & 0x1) != 0;
		@Pc(115) boolean copyY = copyAll || (functionMask & 0x2) != 0;
		@Pc(129) boolean copyZ = copyAll || (functionMask & 0x4) != 0 || (functionMask & 0x10) != 0;
		@Pc(161) int local161;
		if (copyX || copyY || copyZ) {
			if (copyX) {
				if (buffers.vertexX == null || buffers.vertexX.length < this.vertexCount) {
					target.vertexX = buffers.vertexX = new int[this.vertexCount];
				} else {
					target.vertexX = buffers.vertexX;
				}
				for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
					target.vertexX[vertexIndex] = this.vertexX[vertexIndex];
				}
			} else {
				target.vertexX = this.vertexX;
			}
			if (copyY) {
				if (buffers.vertexY == null || buffers.vertexY.length < this.vertexCount) {
					target.vertexY = buffers.vertexY = new int[this.vertexCount];
				} else {
					target.vertexY = buffers.vertexY;
				}
				for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
					target.vertexY[vertexIndex] = this.vertexY[vertexIndex];
				}
			} else {
				target.vertexY = this.vertexY;
			}
			if (copyZ) {
				if (buffers.vertexZ == null || buffers.vertexZ.length < this.vertexCount) {
					target.vertexZ = buffers.vertexZ = new int[this.vertexCount];
				} else {
					target.vertexZ = buffers.vertexZ;
				}
				for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
					target.vertexZ[vertexIndex] = this.vertexZ[vertexIndex];
				}
			} else {
				target.vertexZ = this.vertexZ;
			}
		} else {
			target.vertexX = this.vertexX;
			target.vertexY = this.vertexY;
			target.vertexZ = this.vertexZ;
		}
		if ((functionMask & 0x84080) == 0) {
			target.faceColour = this.faceColour;
		} else {
			if (buffers.faceColour == null || buffers.faceColour.length < this.faceCount) {
				int capacity = this.faceCount;
				target.faceColour = buffers.faceColour = new short[capacity];
			} else {
				target.faceColour = buffers.faceColour;
			}
			for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
				target.faceColour[faceIndex] = this.faceColour[faceIndex];
			}
		}
		if ((functionMask & 0x97018) != 0) {
			target.lightingState = 0;
			target.faceColourA = target.faceColourB = target.faceColourC = null;
		} else if ((functionMask & 0x80) == 0) {
			if (ensureLit) {
				this.applyLighting(false);
			}
			target.faceColourA = this.faceColourA;
			target.faceColourB = this.faceColourB;
			target.faceColourC = this.faceColourC;
			target.lightingState = this.lightingState;
		} else {
			if (ensureLit) {
				this.applyLighting(false);
			}
			if (this.faceColourA != null) {
				if (buffers.faceColourA == null || buffers.faceColourA.length < this.faceCount) {
					int capacity = this.faceCount;
					target.faceColourA = buffers.faceColourA = new int[capacity];
					target.faceColourB = buffers.faceColourB = new int[capacity];
					target.faceColourC = buffers.faceColourC = new int[capacity];
				} else {
					target.faceColourA = buffers.faceColourA;
					target.faceColourB = buffers.faceColourB;
					target.faceColourC = buffers.faceColourC;
				}
				for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
					target.faceColourA[faceIndex] = this.faceColourA[faceIndex];
					target.faceColourB[faceIndex] = this.faceColourB[faceIndex];
					target.faceColourC[faceIndex] = this.faceColourC[faceIndex];
				}
			}
			target.lightingState = this.lightingState;
		}
		if ((functionMask & 0x100) == 0) {
			target.faceAlpha = this.faceAlpha;
		} else {
			if (buffers.faceAlpha == null || buffers.faceAlpha.length < this.faceCount) {
				int capacity = this.faceCount;
				target.faceAlpha = buffers.faceAlpha = new byte[capacity];
			} else {
				target.faceAlpha = buffers.faceAlpha;
			}
			if (this.faceAlpha == null) {
				for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
					target.faceAlpha[faceIndex] = 0;
				}
			} else {
				for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
					target.faceAlpha[faceIndex] = this.faceAlpha[faceIndex];
				}
			}
		}
		if ((functionMask & 0x8) == 0 && (functionMask & 0x10) == 0) {
			if (ensureLit) {
				this.ensureNormals();
			}
			target.lb = this.lb;
			target.faceNormals = this.faceNormals;
		} else {
			if (buffers.lb == null || buffers.lb.length < this.maxVertex) {
				int capacity = this.maxVertex;
				target.lb = buffers.lb = new VertexNormal[capacity];
			} else {
				target.lb = buffers.lb;
			}
			if (this.lb == null) {
				target.lb = null;
			} else {
				for (int vertexIndex = 0; vertexIndex < this.maxVertex; vertexIndex++) {
					target.lb[vertexIndex] = new VertexNormal(this.lb[vertexIndex]);
				}
			}
			if (this.faceNormals == null) {
				target.faceNormals = null;
			} else {
				if (buffers.faceNormals == null || buffers.faceNormals.length < this.faceCount) {
					int capacity = this.faceCount;
					target.faceNormals = buffers.faceNormals = new FaceNormal[capacity];
				} else {
					target.faceNormals = buffers.faceNormals;
				}
				for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
					target.faceNormals[faceIndex] = this.faceNormals[faceIndex] == null ? null : new FaceNormal(this.faceNormals[faceIndex]);
				}
			}
		}
		if ((functionMask & 0x8000) == 0) {
			target.faceTextures = this.faceTextures;
		} else if (this.faceTextures == null) {
			target.faceTextures = null;
		} else {
			if (buffers.faceTextures == null || buffers.faceTextures.length < this.faceCount) {
				int capacity = this.faceCount;
				target.faceTextures = buffers.faceTextures = new short[capacity];
			} else {
				target.faceTextures = buffers.faceTextures;
			}
			for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
				target.faceTextures[faceIndex] = this.faceTextures[faceIndex];
			}
		}
		if ((functionMask & 0x10000) == 0) {
			target.shadingType = this.shadingType;
		} else if (this.shadingType == null) {
			target.shadingType = null;
		} else {
			if (buffers.shadingType == null || buffers.shadingType.length < this.faceCount) {
				int capacity = reserve ? this.faceCount + 100 : this.faceCount;
				target.shadingType = buffers.shadingType = new byte[capacity];
			} else {
				target.shadingType = buffers.shadingType;
			}
			for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
				target.shadingType[faceIndex] = this.shadingType[faceIndex];
			}
		}
		@Pc(803) int local803;
		if ((functionMask & 0xC580) == 0) {
			target.billboardAttributes = this.billboardAttributes;
		} else if (buffers.billboardAttributes == null || buffers.billboardAttributes.length < this.billboardCount) {
			int capacity = this.billboardCount;
			target.billboardAttributes = buffers.billboardAttributes = new JavaBillboardAttributes[capacity];
			for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
				target.billboardAttributes[billboardIndex] = this.billboardAttributes[billboardIndex].copy();
			}
		} else {
			target.billboardAttributes = buffers.billboardAttributes;
			for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
				target.billboardAttributes[billboardIndex].copyFrom(this.billboardAttributes[billboardIndex]);
			}
		}
		if (this.texCoordU == null || (functionMask & 0x10) == 0) {
			target.texCoordU = this.texCoordU;
			target.texCoordV = this.texCoordV;
		} else {
			if (buffers.texCoordU == null || buffers.texCoordU.length < this.faceCount) {
				int capacity = reserve ? this.faceCount + 100 : this.faceCount;
				target.texCoordU = buffers.texCoordU = new float[capacity][3];
			} else {
				target.texCoordU = buffers.texCoordU;
			}
			for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
				if (this.texCoordU[faceIndex] != null) {
					target.texCoordU[faceIndex][0] = this.texCoordU[faceIndex][0];
					target.texCoordU[faceIndex][1] = this.texCoordU[faceIndex][1];
					target.texCoordU[faceIndex][2] = this.texCoordU[faceIndex][2];
				}
			}
			if (buffers.texCoordV == null || buffers.texCoordV.length < this.faceCount) {
				int capacity = reserve ? this.faceCount + 100 : this.faceCount;
				target.texCoordV = buffers.texCoordV = new float[capacity][3];
			} else {
				target.texCoordV = buffers.texCoordV;
			}
			for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
				if (this.texCoordV[faceIndex] != null) {
					target.texCoordV[faceIndex][0] = this.texCoordV[faceIndex][0];
					target.texCoordV[faceIndex][1] = this.texCoordV[faceIndex][1];
					target.texCoordV[faceIndex][2] = this.texCoordV[faceIndex][2];
				}
			}
		}
		target.vertexLabels = this.vertexLabels;
		target.faceLabels = this.faceLabels;
		target.billboardLabels = this.billboardLabels;
		target.originModels = this.originModels;
		target.faceOriginModels = this.faceOriginModels;
		target.facePriority = this.facePriority;
		target.faceA = this.faceA;
		target.faceB = this.faceB;
		target.faceC = this.faceC;
		target.emitters = this.emitters;
		target.effectors = this.effectors;
		target.billboardFaces = this.billboardFaces;
		target.faceIndices = this.faceIndices;
		target.functionMask = functionMask;
		return target;
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Z)V")
	private void applyLighting(@OriginalArg(0) boolean discardShading) {
		if (this.toolkit.threadCount > 1) {
			synchronized (this) {
				this.calculateLighting(discardShading);
			}
		} else {
			this.calculateLighting(discardShading);
		}
	}

	@OriginalMember(owner = "client!jga", name = "d", descriptor = "()[Lclient!nn;")
	@Override
	public EmissiveTriangle[] getEmissiveTriangles() {
		return this.emitters;
	}

	@OriginalMember(owner = "client!jga", name = "V", descriptor = "()I")
	@Override
	public int V() {
		if (!this.boundsValid) {
			this.calculateBounds();
		}
		return this.minX;
	}

	@OriginalMember(owner = "client!jga", name = "c", descriptor = "(I)V")
	private void drawTriangleRgb(@OriginalArg(0) int face) {
		@Pc(4) short a = this.faceA[face];
		@Pc(9) short b = this.faceB[face];
		@Pc(14) short c = this.faceC[face];
		if (this.faceTextures != null && this.faceTextures[face] != -1) {
			@Pc(175) int alphaBits = -16777216;
			if (this.faceAlpha != null) {
				alphaBits = 255 - (this.faceAlpha[face] & 0xFF) << 24;
			}
			if (this.faceColourC[face] == -1) {
				@Pc(204) int argb = alphaBits | this.faceColourA[face] & 0xFFFFFF;
				this.rasterizer.renderTexturedTriangleRgb((float) this.screenY[a], (float) this.screenY[b], (float) this.screenY[c], (float) this.screenX[a], (float) this.screenX[b], (float) this.screenX[c], (float) this.screenZ[a], (float) this.screenZ[b], (float) this.screenZ[c], this.texCoordU[face][0], this.texCoordU[face][1], this.texCoordU[face][2], this.texCoordV[face][0], this.texCoordV[face][1], this.texCoordV[face][2], argb, argb, argb, this.threadResource.fogColour, 0, 0, 0, this.faceTextures[face]);
			} else {
				this.rasterizer.renderTexturedTriangleRgb((float) this.screenY[a], (float) this.screenY[b], (float) this.screenY[c], (float) this.screenX[a], (float) this.screenX[b], (float) this.screenX[c], (float) this.screenZ[a], (float) this.screenZ[b], (float) this.screenZ[c], this.texCoordU[face][0], this.texCoordU[face][1], this.texCoordU[face][2], this.texCoordV[face][0], this.texCoordV[face][1], this.texCoordV[face][2], alphaBits | this.faceColourA[face] & 0xFFFFFF, alphaBits | this.faceColourB[face] & 0xFFFFFF, alphaBits | this.faceColourC[face] & 0xFFFFFF, this.threadResource.fogColour, 0, 0, 0, this.faceTextures[face]);
			}
			return;
		}
		if (this.faceAlpha == null) {
			this.rasterizer.alpha = 0;
		} else {
			this.rasterizer.alpha = this.faceAlpha[face] & 0xFF;
		}
		if (this.faceColourC[face] == -1) {
			this.rasterizer.renderFlatTriangleRgb((float) this.screenY[a], (float) this.screenY[b], (float) this.screenY[c], (float) this.screenX[a], (float) this.screenX[b], (float) this.screenX[c], (float) this.screenZ[a], (float) this.screenZ[b], (float) this.screenZ[c], ColorUtil.hsvToRgbTable[this.faceColourA[face] & 0xFFFF]);
		} else {
			this.rasterizer.renderTriangleHslRgb((float) this.screenY[a], (float) this.screenY[b], (float) this.screenY[c], (float) this.screenX[a], (float) this.screenX[b], (float) this.screenX[c], (float) this.screenZ[a], (float) this.screenZ[b], (float) this.screenZ[c], (float) (this.faceColourA[face] & 0xFFFF), (float) (this.faceColourB[face] & 0xFFFF), (float) (this.faceColourC[face] & 0xFFFF));
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Lclient!bca;Lclient!iw;I)V")
	@Override
	public void render(@OriginalArg(0) Matrix matrix, @OriginalArg(1) BoundingBoxPolygon cylinder, @OriginalArg(2) int flags) {
		this.draw(matrix, cylinder, -1, flags);
	}

	@OriginalMember(owner = "client!jga", name = "HA", descriptor = "()I")
	@Override
	public int HA() {
		if (!this.boundsValid) {
			this.calculateBounds();
		}
		return this.minZ;
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(I[IIIIIZ)V")
	@Override
	public void applyTransformUnmasked(@OriginalArg(0) int type, @OriginalArg(1) int[] labels, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int z, @OriginalArg(5) int rotation, @OriginalArg(6) boolean lights) {
		@Pc(2) int length = labels.length;
		@Pc(21) int local21;
		@Pc(67) int local67;
		@Pc(89) int local89;
		@Pc(8) int scaledX;
		@Pc(12) int scaledY;
		@Pc(16) int scaledZ;
		@Pc(84) int local84;
		if (type == 0) {
			scaledX = x << 4;
			scaledY = y << 4;
			scaledZ = z << 4;
			if (!this.highPrecisionVertices) {
				for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
					this.vertexX[vertexIndex] <<= 0x4;
					this.vertexY[vertexIndex] <<= 0x4;
					this.vertexZ[vertexIndex] <<= 0x4;
				}
				this.highPrecisionVertices = true;
			}
			int pivotCount = 0;
			this.pivotX = 0;
			this.pivotY = 0;
			this.pivotZ = 0;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				@Pc(72) int label = labels[labelIndex];
				if (label < this.vertexLabels.length) {
					@Pc(82) int[] vertexGroup = this.vertexLabels[label];
					for (int groupIndex = 0; groupIndex < vertexGroup.length; groupIndex++) {
						int vertex = vertexGroup[groupIndex];
						this.pivotX += this.vertexX[vertex];
						this.pivotY += this.vertexY[vertex];
						this.pivotZ += this.vertexZ[vertex];
						pivotCount++;
					}
				}
			}
			if (pivotCount > 0) {
				this.pivotX = this.pivotX / pivotCount + scaledX;
				this.pivotY = this.pivotY / pivotCount + scaledY;
				this.pivotZ = this.pivotZ / pivotCount + scaledZ;
			} else {
				this.pivotX = scaledX;
				this.pivotY = scaledY;
				this.pivotZ = scaledZ;
			}
			return;
		}
		@Pc(233) int[] group;
		@Pc(235) int j;
		if (type == 1) {
			scaledX = x << 4;
			scaledY = y << 4;
			scaledZ = z << 4;
			if (!this.highPrecisionVertices) {
				for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
					this.vertexX[vertexIndex] <<= 0x4;
					this.vertexY[vertexIndex] <<= 0x4;
					this.vertexZ[vertexIndex] <<= 0x4;
				}
				this.highPrecisionVertices = true;
			}
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int label = labels[labelIndex];
				if (label < this.vertexLabels.length) {
					group = this.vertexLabels[label];
					for (j = 0; j < group.length; j++) {
						int vertex = group[j];
						this.vertexX[vertex] += scaledX;
						this.vertexY[vertex] += scaledY;
						this.vertexZ[vertex] += scaledZ;
					}
				}
			}
			return;
		}
		@Pc(342) int local342;
		@Pc(360) int local360;
		if (type == 2) {
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int label = labels[labelIndex];
				if (label < this.vertexLabels.length) {
					group = this.vertexLabels[label];
					if ((rotation & 0x1) == 0) {
						for (j = 0; j < group.length; j++) {
							int vertex = group[j];
							this.vertexX[vertex] -= this.pivotX;
							this.vertexY[vertex] -= this.pivotY;
							this.vertexZ[vertex] -= this.pivotZ;
							if (z != 0) {
								int sin = TextureCombineSource.SINE[z];
								int cos = TextureCombineSource.COSINE[z];
								int rotatedX = this.vertexY[vertex] * sin + this.vertexX[vertex] * cos + 16383 >> 14;
								this.vertexY[vertex] = this.vertexY[vertex] * cos + 16383 - this.vertexX[vertex] * sin >> 14;
								this.vertexX[vertex] = rotatedX;
							}
							if (x != 0) {
								int sin = TextureCombineSource.SINE[x];
								int cos = TextureCombineSource.COSINE[x];
								int rotatedY = this.vertexY[vertex] * cos + 16383 - this.vertexZ[vertex] * sin >> 14;
								this.vertexZ[vertex] = this.vertexY[vertex] * sin + this.vertexZ[vertex] * cos + 16383 >> 14;
								this.vertexY[vertex] = rotatedY;
							}
							if (y != 0) {
								int sin = TextureCombineSource.SINE[y];
								int cos = TextureCombineSource.COSINE[y];
								int rotatedX = this.vertexZ[vertex] * sin + this.vertexX[vertex] * cos + 16383 >> 14;
								this.vertexZ[vertex] = this.vertexZ[vertex] * cos + 16383 - this.vertexX[vertex] * sin >> 14;
								this.vertexX[vertex] = rotatedX;
							}
							this.vertexX[vertex] += this.pivotX;
							this.vertexY[vertex] += this.pivotY;
							this.vertexZ[vertex] += this.pivotZ;
						}
					} else {
						for (j = 0; j < group.length; j++) {
							int vertex = group[j];
							this.vertexX[vertex] -= this.pivotX;
							this.vertexY[vertex] -= this.pivotY;
							this.vertexZ[vertex] -= this.pivotZ;
							if (x != 0) {
								int sin = TextureCombineSource.SINE[x];
								int cos = TextureCombineSource.COSINE[x];
								int rotatedY = this.vertexY[vertex] * cos + 16383 - this.vertexZ[vertex] * sin >> 14;
								this.vertexZ[vertex] = this.vertexY[vertex] * sin + this.vertexZ[vertex] * cos + 16383 >> 14;
								this.vertexY[vertex] = rotatedY;
							}
							if (z != 0) {
								int sin = TextureCombineSource.SINE[z];
								int cos = TextureCombineSource.COSINE[z];
								int rotatedX = this.vertexY[vertex] * sin + this.vertexX[vertex] * cos + 16383 >> 14;
								this.vertexY[vertex] = this.vertexY[vertex] * cos + 16383 - this.vertexX[vertex] * sin >> 14;
								this.vertexX[vertex] = rotatedX;
							}
							if (y != 0) {
								int sin = TextureCombineSource.SINE[y];
								int cos = TextureCombineSource.COSINE[y];
								int rotatedX = this.vertexZ[vertex] * sin + this.vertexX[vertex] * cos + 16383 >> 14;
								this.vertexZ[vertex] = this.vertexZ[vertex] * cos + 16383 - this.vertexX[vertex] * sin >> 14;
								this.vertexX[vertex] = rotatedX;
							}
							this.vertexX[vertex] += this.pivotX;
							this.vertexY[vertex] += this.pivotY;
							this.vertexZ[vertex] += this.pivotZ;
						}
					}
				}
			}
		} else if (type == 3) {
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int label = labels[labelIndex];
				if (label < this.vertexLabels.length) {
					group = this.vertexLabels[label];
					for (j = 0; j < group.length; j++) {
						int vertex = group[j];
						this.vertexX[vertex] -= this.pivotX;
						this.vertexY[vertex] -= this.pivotY;
						this.vertexZ[vertex] -= this.pivotZ;
						this.vertexX[vertex] = this.vertexX[vertex] * x / 128;
						this.vertexY[vertex] = this.vertexY[vertex] * y / 128;
						this.vertexZ[vertex] = this.vertexZ[vertex] * z / 128;
						this.vertexX[vertex] += this.pivotX;
						this.vertexY[vertex] += this.pivotY;
						this.vertexZ[vertex] += this.pivotZ;
					}
				}
			}
		} else {
			@Pc(967) JavaBillboardFace javaBillboardFace;
			@Pc(972) JavaBillboardAttributes javaBillboardAttributes;
			if (type == 5) {
				if (this.faceLabels != null && this.faceAlpha != null) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int label = labels[labelIndex];
						if (label < this.faceLabels.length) {
							group = this.faceLabels[label];
							for (j = 0; j < group.length; j++) {
								int face = group[j];
								int alpha = (this.faceAlpha[face] & 0xFF) + x * 8;
								if (alpha < 0) {
									alpha = 0;
								} else if (alpha > 255) {
									alpha = 255;
								}
								this.faceAlpha[face] = (byte) alpha;
							}
						}
					}
					if (this.billboardFaces != null) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							javaBillboardFace = this.billboardFaces[billboardIndex];
							javaBillboardAttributes = this.billboardAttributes[billboardIndex];
							javaBillboardAttributes.diffuseColor = javaBillboardAttributes.diffuseColor & 0xFFFFFF | 255 - (this.faceAlpha[javaBillboardFace.face] & 0xFF) << 24;
						}
					}
				}
			} else if (type != 7) {
				@Pc(1186) JavaBillboardAttributes javaBillboardAttributes2;
				if (type == 8) {
					if (this.billboardLabels != null) {
						for (int labelIndex = 0; labelIndex < length; labelIndex++) {
							int label = labels[labelIndex];
							if (label < this.billboardLabels.length) {
								group = this.billboardLabels[label];
								for (j = 0; j < group.length; j++) {
									javaBillboardAttributes2 = this.billboardAttributes[group[j]];
									javaBillboardAttributes2.offsetX += x;
									javaBillboardAttributes2.offsetY += y;
								}
							}
						}
					}
				} else if (type == 10) {
					if (this.billboardLabels != null) {
						for (int labelIndex = 0; labelIndex < length; labelIndex++) {
							int label = labels[labelIndex];
							if (label < this.billboardLabels.length) {
								group = this.billboardLabels[label];
								for (j = 0; j < group.length; j++) {
									javaBillboardAttributes2 = this.billboardAttributes[group[j]];
									javaBillboardAttributes2.scaleX = javaBillboardAttributes2.scaleX * x >> 7;
									javaBillboardAttributes2.scaleY = javaBillboardAttributes2.scaleY * y >> 7;
								}
							}
						}
					}
				} else if (type == 9 && this.billboardLabels != null) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int label = labels[labelIndex];
						if (label < this.billboardLabels.length) {
							group = this.billboardLabels[label];
							for (j = 0; j < group.length; j++) {
								javaBillboardAttributes2 = this.billboardAttributes[group[j]];
								javaBillboardAttributes2.rotation = javaBillboardAttributes2.rotation + x & 0x3FFF;
							}
						}
					}
				}
			} else if (this.faceLabels != null) {
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					int label = labels[labelIndex];
					if (label < this.faceLabels.length) {
						group = this.faceLabels[label];
						for (j = 0; j < group.length; j++) {
							int face = group[j];
							int hsl = this.faceColour[face] & 0xFFFF;
							int oldHue = hsl >> 10 & 0x3F;
							int saturation = hsl >> 7 & 0x7;
							@Pc(1049) int lightness = hsl & 0x7F;
							@Pc(1055) int hue = oldHue + x & 0x3F;
							saturation += y;
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
							this.faceColour[face] = (short) (hue << 10 | saturation << 7 | lightness);
						}
						this.recoloured = true;
					}
				}
				if (this.billboardFaces != null) {
					for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
						javaBillboardFace = this.billboardFaces[billboardIndex];
						javaBillboardAttributes = this.billboardAttributes[billboardIndex];
						javaBillboardAttributes.diffuseColor = javaBillboardAttributes.diffuseColor & 0xFF000000 | ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(this.faceColour[javaBillboardFace.face] & 0xFFFF) & 0xFFFF] & 0xFFFFFF;
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "FA", descriptor = "(I)V")
	@Override
	public void FA(@OriginalArg(0) int angle) {
		if ((this.functionMask & 0x6) != 6) {
			throw new IllegalStateException();
		}
		@Pc(13) int sin = TextureCombineSource.SINE[angle];
		@Pc(17) int cos = TextureCombineSource.COSINE[angle];
		synchronized (this) {
			for (@Pc(23) int i = 0; i < this.vertexCount; i++) {
				@Pc(40) int newY = this.vertexY[i] * cos - this.vertexZ[i] * sin >> 14;
				this.vertexZ[i] = this.vertexY[i] * sin + this.vertexZ[i] * cos >> 14;
				this.vertexY[i] = newY;
			}
			this.invalidateNormals();
		}
	}

	@OriginalMember(owner = "client!jga", name = "f", descriptor = "()V")
	@Override
	public void lock() {
		if (this.toolkit.threadCount <= 1) {
			return;
		}
		synchronized (this) {
			while (super.isLocked) {
				try {
					this.wait();
				} catch (@Pc(13) InterruptedException ex) {
				}
			}
			super.isLocked = true;
		}
	}

	@OriginalMember(owner = "client!jga", name = "G", descriptor = "()I")
	@Override
	public int G() {
		if (!this.boundsValid) {
			this.calculateBounds();
		}
		return this.maxZ;
	}

	@OriginalMember(owner = "client!jga", name = "I", descriptor = "(I[IIIIZI[I)V")
	@Override
	public void I(@OriginalArg(0) int type, @OriginalArg(1) int[] labels, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int z, @OriginalArg(5) boolean lights, @OriginalArg(6) int submesh, @OriginalArg(7) int[] transform) {
		@Pc(2) int length = labels.length;
		@Pc(21) int local21;
		@Pc(67) int local67;
		@Pc(89) int local89;
		@Pc(72) int local72;
		@Pc(84) int local84;
		if (type == 0) {
			x <<= 0x4;
			y <<= 0x4;
			z <<= 0x4;
			if (!this.highPrecisionVertices) {
				for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
					this.vertexX[vertexIndex] <<= 0x4;
					this.vertexY[vertexIndex] <<= 0x4;
					this.vertexZ[vertexIndex] <<= 0x4;
				}
				this.highPrecisionVertices = true;
			}
			int pivotCount = 0;
			this.pivotX = 0;
			this.pivotY = 0;
			this.pivotZ = 0;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int label = labels[labelIndex];
				if (label < this.vertexLabels.length) {
					@Pc(82) int[] vertexGroup = this.vertexLabels[label];
					for (int groupIndex = 0; groupIndex < vertexGroup.length; groupIndex++) {
						int vertex = vertexGroup[groupIndex];
						if (this.originModels == null || (submesh & this.originModels[vertex]) != 0) {
							this.pivotX += this.vertexX[vertex];
							this.pivotY += this.vertexY[vertex];
							this.pivotZ += this.vertexZ[vertex];
							pivotCount++;
						}
					}
				}
			}
			if (pivotCount > 0) {
				this.pivotX = this.pivotX / pivotCount + x;
				this.pivotY = this.pivotY / pivotCount + y;
				this.pivotZ = this.pivotZ / pivotCount + z;
				this.pivotDirty = true;
			} else {
				this.pivotX = x;
				this.pivotY = y;
				this.pivotZ = z;
			}
			return;
		}
		@Pc(320) int[] group;
		@Pc(322) int local322;
		if (type == 1) {
			if (transform != null) {
				int transformedX = transform[0] * x + transform[1] * y + transform[2] * z + 8192 >> 14;
				int transformedY = transform[3] * x + transform[4] * y + transform[5] * z + 8192 >> 14;
				int transformedZ = transform[6] * x + transform[7] * y + transform[8] * z + 8192 >> 14;
				x = transformedX;
				y = transformedY;
				z = transformedZ;
			}
			x <<= 0x4;
			y <<= 0x4;
			z <<= 0x4;
			if (!this.highPrecisionVertices) {
				for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
					this.vertexX[vertexIndex] <<= 0x4;
					this.vertexY[vertexIndex] <<= 0x4;
					this.vertexZ[vertexIndex] <<= 0x4;
				}
				this.highPrecisionVertices = true;
			}
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int label = labels[labelIndex];
				if (label < this.vertexLabels.length) {
					group = this.vertexLabels[label];
					for (int groupIndex = 0; groupIndex < group.length; groupIndex++) {
						int vertex = group[groupIndex];
						if (this.originModels == null || (submesh & this.originModels[vertex]) != 0) {
							this.vertexX[vertex] += x;
							this.vertexY[vertex] += y;
							this.vertexZ[vertex] += z;
						}
					}
				}
			}
			return;
		}
		@Pc(478) int local478;
		@Pc(503) int local503;
		@Pc(528) int local528;
		@Pc(567) int local567;
		@Pc(571) int local571;
		@Pc(575) int local575;
		@Pc(579) int local579;
		@Pc(587) int local587;
		@Pc(595) int local595;
		@Pc(754) int local754;
		@Pc(782) int local782;
		@Pc(787) int local787;
		@Pc(797) int local797;
		@Pc(802) int local802;
		@Pc(805) int local805;
		@Pc(808) int local808;
		@Pc(810) int local810;
		@Pc(934) int[] local934;
		@Pc(936) int local936;
		@Pc(939) int local939;
		@Pc(942) int local942;
		@Pc(944) int local944;
		@Pc(1067) int local1067;
		if (type == 2) {
			if (transform == null) {
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					int label = labels[labelIndex];
					if (label < this.vertexLabels.length) {
						group = this.vertexLabels[label];
						for (int groupIndex = 0; groupIndex < group.length; groupIndex++) {
							int vertex = group[groupIndex];
							if (this.originModels == null || (submesh & this.originModels[vertex]) != 0) {
								this.vertexX[vertex] -= this.pivotX;
								this.vertexY[vertex] -= this.pivotY;
								this.vertexZ[vertex] -= this.pivotZ;
								if (z != 0) {
									int sin = TextureCombineSource.SINE[z];
									int cos = TextureCombineSource.COSINE[z];
									int rotatedX = this.vertexY[vertex] * sin + this.vertexX[vertex] * cos + 16383 >> 14;
									this.vertexY[vertex] = this.vertexY[vertex] * cos + 16383 - this.vertexX[vertex] * sin >> 14;
									this.vertexX[vertex] = rotatedX;
								}
								if (x != 0) {
									int sin = TextureCombineSource.SINE[x];
									int cos = TextureCombineSource.COSINE[x];
									int rotatedY = this.vertexY[vertex] * cos + 16383 - this.vertexZ[vertex] * sin >> 14;
									this.vertexZ[vertex] = this.vertexY[vertex] * sin + this.vertexZ[vertex] * cos + 16383 >> 14;
									this.vertexY[vertex] = rotatedY;
								}
								if (y != 0) {
									int sin = TextureCombineSource.SINE[y];
									int cos = TextureCombineSource.COSINE[y];
									int rotatedX = this.vertexZ[vertex] * sin + this.vertexX[vertex] * cos + 16383 >> 14;
									this.vertexZ[vertex] = this.vertexZ[vertex] * cos + 16383 - this.vertexX[vertex] * sin >> 14;
									this.vertexX[vertex] = rotatedX;
								}
								this.vertexX[vertex] += this.pivotX;
								this.vertexY[vertex] += this.pivotY;
								this.vertexZ[vertex] += this.pivotZ;
							}
						}
					}
				}
			} else {
				if (!this.highPrecisionVertices) {
					for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
						this.vertexX[vertexIndex] <<= 0x4;
						this.vertexY[vertexIndex] <<= 0x4;
						this.vertexZ[vertexIndex] <<= 0x4;
					}
					this.highPrecisionVertices = true;
				}
				int translationX = transform[9] << 4;
				int translationY = transform[10] << 4;
				int translationZ = transform[11] << 4;
				int deltaX = transform[12] << 4;
				int deltaY = transform[13] << 4;
				int deltaZ = transform[14] << 4;
				if (this.pivotDirty) {
					int newPivotX = transform[0] * this.pivotX + transform[3] * this.pivotY + transform[6] * this.pivotZ + 8192 >> 14;
					int newPivotY = transform[1] * this.pivotX + transform[4] * this.pivotY + transform[7] * this.pivotZ + 8192 >> 14;
					int newPivotZ = transform[2] * this.pivotX + transform[5] * this.pivotY + transform[8] * this.pivotZ + 8192 >> 14;
					newPivotX += deltaX;
					newPivotY += deltaY;
					newPivotZ += deltaZ;
					this.pivotX = newPivotX;
					this.pivotY = newPivotY;
					this.pivotZ = newPivotZ;
					this.pivotDirty = false;
				}
				@Pc(555) int[] matrix = new int[9];
				int cosX = TextureCombineSource.COSINE[x];
				int sinX = TextureCombineSource.SINE[x];
				int cosY = TextureCombineSource.COSINE[y];
				int sinY = TextureCombineSource.SINE[y];
				int cosZ = TextureCombineSource.COSINE[z];
				int sinZ = TextureCombineSource.SINE[z];
				int sinXCosZ = sinX * cosZ + 8192 >> 14;
				int sinXSinZ = sinX * sinZ + 8192 >> 14;
				matrix[0] = cosY * cosZ + sinY * sinXSinZ + 8192 >> 14;
				matrix[1] = -cosY * sinZ + sinY * sinXCosZ + 8192 >> 14;
				matrix[2] = sinY * cosX + 8192 >> 14;
				matrix[3] = cosX * sinZ + 8192 >> 14;
				matrix[4] = cosX * cosZ + 8192 >> 14;
				matrix[5] = -sinX;
				matrix[6] = -sinY * cosZ + cosY * sinXSinZ + 8192 >> 14;
				matrix[7] = sinY * sinZ + cosY * sinXCosZ + 8192 >> 14;
				matrix[8] = cosY * cosX + 8192 >> 14;
				@Pc(726) int rotatedNegPivotX = matrix[0] * -this.pivotX + matrix[1] * -this.pivotY + matrix[2] * -this.pivotZ + 8192 >> 14;
				int rotatedNegPivotY = matrix[3] * -this.pivotX + matrix[4] * -this.pivotY + matrix[5] * -this.pivotZ + 8192 >> 14;
				int rotatedNegPivotZ = matrix[6] * -this.pivotX + matrix[7] * -this.pivotY + matrix[8] * -this.pivotZ + 8192 >> 14;
				int translateX = rotatedNegPivotX + this.pivotX;
				@Pc(792) int translateY = rotatedNegPivotY + this.pivotY;
				int translateZ = rotatedNegPivotZ + this.pivotZ;
				@Pc(800) int[] combinedMatrix = new int[9];
				for (int row = 0; row < 3; row++) {
					for (int column = 0; column < 3; column++) {
						int sum = 0;
						for (int k = 0; k < 3; k++) {
							sum += matrix[row * 3 + k] * transform[column * 3 + k];
						}
						combinedMatrix[row * 3 + column] = sum + 8192 >> 14;
					}
				}
				int fullOffsetX = matrix[0] * deltaX + matrix[1] * deltaY + matrix[2] * deltaZ + 8192 >> 14;
				int fullOffsetY = matrix[3] * deltaX + matrix[4] * deltaY + matrix[5] * deltaZ + 8192 >> 14;
				int fullOffsetZ = matrix[6] * deltaX + matrix[7] * deltaY + matrix[8] * deltaZ + 8192 >> 14;
				fullOffsetX += translateX;
				fullOffsetY += translateY;
				fullOffsetZ += translateZ;
				int[] finalMatrix = new int[9];
				for (int row = 0; row < 3; row++) {
					for (int column = 0; column < 3; column++) {
						int sum = 0;
						for (int k = 0; k < 3; k++) {
							sum += transform[row * 3 + k] * combinedMatrix[column + k * 3];
						}
						finalMatrix[row * 3 + column] = sum + 8192 >> 14;
					}
				}
				int vertexOffsetX = transform[0] * fullOffsetX + transform[1] * fullOffsetY + transform[2] * fullOffsetZ + 8192 >> 14;
				int vertexOffsetY = transform[3] * fullOffsetX + transform[4] * fullOffsetY + transform[5] * fullOffsetZ + 8192 >> 14;
				int vertexOffsetZ = transform[6] * fullOffsetX + transform[7] * fullOffsetY + transform[8] * fullOffsetZ + 8192 >> 14;
				vertexOffsetX += translationX;
				vertexOffsetY += translationY;
				vertexOffsetZ += translationZ;
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					@Pc(1072) int label = labels[labelIndex];
					if (label < this.vertexLabels.length) {
						@Pc(1082) int[] vertices = this.vertexLabels[label];
						for (@Pc(1084) int i = 0; i < vertices.length; i++) {
							@Pc(1089) int vertex = vertices[i];
							if (this.originModels == null || (submesh & this.originModels[vertex]) != 0) {
								@Pc(1130) int rotatedX = finalMatrix[0] * this.vertexX[vertex] + finalMatrix[1] * this.vertexY[vertex] + finalMatrix[2] * this.vertexZ[vertex] + 8192 >> 14;
								@Pc(1161) int rotatedY = finalMatrix[3] * this.vertexX[vertex] + finalMatrix[4] * this.vertexY[vertex] + finalMatrix[5] * this.vertexZ[vertex] + 8192 >> 14;
								@Pc(1192) int rotatedZ = finalMatrix[6] * this.vertexX[vertex] + finalMatrix[7] * this.vertexY[vertex] + finalMatrix[8] * this.vertexZ[vertex] + 8192 >> 14;
								@Pc(1196) int newX = rotatedX + vertexOffsetX;
								@Pc(1200) int newY = rotatedY + vertexOffsetY;
								@Pc(1204) int newZ = rotatedZ + vertexOffsetZ;
								this.vertexX[vertex] = newX;
								this.vertexY[vertex] = newY;
								this.vertexZ[vertex] = newZ;
							}
						}
					}
				}
			}
		} else if (type != 3) {
			@Pc(2402) JavaBillboardFace javaBillboardFace;
			@Pc(2407) JavaBillboardAttributes javaBillboardAttributes;
			if (type == 5) {
				if (this.faceLabels != null && this.faceAlpha != null) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int label = labels[labelIndex];
						if (label < this.faceLabels.length) {
							group = this.faceLabels[label];
							for (int groupIndex = 0; groupIndex < group.length; groupIndex++) {
								int face = group[groupIndex];
								if (this.faceOriginModels == null || (submesh & this.faceOriginModels[face]) != 0) {
									int alpha = (this.faceAlpha[face] & 0xFF) + x * 8;
									if (alpha < 0) {
										alpha = 0;
									} else if (alpha > 255) {
										alpha = 255;
									}
									this.faceAlpha[face] = (byte) alpha;
								}
							}
						}
					}
					if (this.billboardFaces != null) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							javaBillboardFace = this.billboardFaces[billboardIndex];
							javaBillboardAttributes = this.billboardAttributes[billboardIndex];
							javaBillboardAttributes.diffuseColor = javaBillboardAttributes.diffuseColor & 0xFFFFFF | 255 - (this.faceAlpha[javaBillboardFace.face] & 0xFF) << 24;
						}
					}
				}
			} else if (type != 7) {
				@Pc(2631) JavaBillboardAttributes javaBillboardAttributes2;
				if (type == 8) {
					if (this.billboardLabels != null) {
						for (int labelIndex = 0; labelIndex < length; labelIndex++) {
							int label = labels[labelIndex];
							if (label < this.billboardLabels.length) {
								group = this.billboardLabels[label];
								for (int groupIndex = 0; groupIndex < group.length; groupIndex++) {
									javaBillboardAttributes2 = this.billboardAttributes[group[groupIndex]];
									javaBillboardAttributes2.offsetX += x;
									javaBillboardAttributes2.offsetY += y;
								}
							}
						}
					}
				} else if (type == 10) {
					if (this.billboardLabels != null) {
						for (int labelIndex = 0; labelIndex < length; labelIndex++) {
							int label = labels[labelIndex];
							if (label < this.billboardLabels.length) {
								group = this.billboardLabels[label];
								for (int groupIndex = 0; groupIndex < group.length; groupIndex++) {
									javaBillboardAttributes2 = this.billboardAttributes[group[groupIndex]];
									javaBillboardAttributes2.scaleX = javaBillboardAttributes2.scaleX * x >> 7;
									javaBillboardAttributes2.scaleY = javaBillboardAttributes2.scaleY * y >> 7;
								}
							}
						}
					}
				} else if (type == 9 && this.billboardLabels != null) {
					for (int labelIndex = 0; labelIndex < length; labelIndex++) {
						int label = labels[labelIndex];
						if (label < this.billboardLabels.length) {
							group = this.billboardLabels[label];
							for (int groupIndex = 0; groupIndex < group.length; groupIndex++) {
								javaBillboardAttributes2 = this.billboardAttributes[group[groupIndex]];
								javaBillboardAttributes2.rotation = javaBillboardAttributes2.rotation + x & 0x3FFF;
							}
						}
					}
				}
			} else if (this.faceLabels != null) {
				for (int labelIndex = 0; labelIndex < length; labelIndex++) {
					int label = labels[labelIndex];
					if (label < this.faceLabels.length) {
						group = this.faceLabels[label];
						for (int groupIndex = 0; groupIndex < group.length; groupIndex++) {
							int face = group[groupIndex];
							if (this.faceOriginModels == null || (submesh & this.faceOriginModels[face]) != 0) {
								int hsl = this.faceColour[face] & 0xFFFF;
								int oldHue = hsl >> 10 & 0x3F;
								int saturation = hsl >> 7 & 0x7;
								int lightness = hsl & 0x7F;
								@Pc(2500) int hue = oldHue + x & 0x3F;
								saturation += y;
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
								this.faceColour[face] = (short) (hue << 10 | saturation << 7 | lightness);
							}
						}
						this.recoloured = true;
					}
				}
				if (this.billboardFaces != null) {
					for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
						javaBillboardFace = this.billboardFaces[billboardIndex];
						javaBillboardAttributes = this.billboardAttributes[billboardIndex];
						javaBillboardAttributes.diffuseColor = javaBillboardAttributes.diffuseColor & 0xFF000000 | ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(this.faceColour[javaBillboardFace.face] & 0xFFFF) & 0xFFFF] & 0xFFFFFF;
					}
				}
			}
		} else if (transform == null) {
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int label = labels[labelIndex];
				if (label < this.vertexLabels.length) {
					group = this.vertexLabels[label];
					for (int groupIndex = 0; groupIndex < group.length; groupIndex++) {
						int vertex = group[groupIndex];
						if (this.originModels == null || (submesh & this.originModels[vertex]) != 0) {
							this.vertexX[vertex] -= this.pivotX;
							this.vertexY[vertex] -= this.pivotY;
							this.vertexZ[vertex] -= this.pivotZ;
							this.vertexX[vertex] = this.vertexX[vertex] * x / 128;
							this.vertexY[vertex] = this.vertexY[vertex] * y / 128;
							this.vertexZ[vertex] = this.vertexZ[vertex] * z / 128;
							this.vertexX[vertex] += this.pivotX;
							this.vertexY[vertex] += this.pivotY;
							this.vertexZ[vertex] += this.pivotZ;
						}
					}
				}
			}
		} else {
			if (!this.highPrecisionVertices) {
				for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
					this.vertexX[vertexIndex] <<= 0x4;
					this.vertexY[vertexIndex] <<= 0x4;
					this.vertexZ[vertexIndex] <<= 0x4;
				}
				this.highPrecisionVertices = true;
			}
			int translationX = transform[9] << 4;
			int translationY = transform[10] << 4;
			int translationZ = transform[11] << 4;
			int deltaX = transform[12] << 4;
			int deltaY = transform[13] << 4;
			int deltaZ = transform[14] << 4;
			if (this.pivotDirty) {
				int newPivotX = transform[0] * this.pivotX + transform[3] * this.pivotY + transform[6] * this.pivotZ + 8192 >> 14;
				int newPivotY = transform[1] * this.pivotX + transform[4] * this.pivotY + transform[7] * this.pivotZ + 8192 >> 14;
				int newPivotZ = transform[2] * this.pivotX + transform[5] * this.pivotY + transform[8] * this.pivotZ + 8192 >> 14;
				newPivotX += deltaX;
				newPivotY += deltaY;
				newPivotZ += deltaZ;
				this.pivotX = newPivotX;
				this.pivotY = newPivotY;
				this.pivotZ = newPivotZ;
				this.pivotDirty = false;
			}
			int scaleX = x << 15 >> 7;
			int scaleY = y << 15 >> 7;
			int scaleZ = z << 15 >> 7;
			int scaledNegPivotX = scaleX * -this.pivotX + 8192 >> 14;
			int scaledNegPivotY = scaleY * -this.pivotY + 8192 >> 14;
			int scaledNegPivotZ = scaleZ * -this.pivotZ + 8192 >> 14;
			int pivotShiftX = scaledNegPivotX + this.pivotX;
			int pivotShiftY = scaledNegPivotY + this.pivotY;
			int pivotShiftZ = scaledNegPivotZ + this.pivotZ;
			@Pc(1736) int[] scaledMatrix = new int[] { scaleX * transform[0] + 8192 >> 14, scaleX * transform[3] + 8192 >> 14, scaleX * transform[6] + 8192 >> 14, scaleY * transform[1] + 8192 >> 14, scaleY * transform[4] + 8192 >> 14, scaleY * transform[7] + 8192 >> 14, scaleZ * transform[2] + 8192 >> 14, scaleZ * transform[5] + 8192 >> 14, scaleZ * transform[8] + 8192 >> 14 };
			int scaledDeltaX = scaleX * deltaX + 8192 >> 14;
			int scaledDeltaY = scaleY * deltaY + 8192 >> 14;
			int scaledDeltaZ = scaleZ * deltaZ + 8192 >> 14;
			@Pc(1872) int translateX = scaledDeltaX + pivotShiftX;
			@Pc(1876) int translateY = scaledDeltaY + pivotShiftY;
			@Pc(1880) int translateZ = scaledDeltaZ + pivotShiftZ;
			@Pc(1883) int[] finalMatrix = new int[9];
			@Pc(1888) int local1888;
			for (int row = 0; row < 3; row++) {
				for (int column = 0; column < 3; column++) {
					int sum = 0;
					for (int k = 0; k < 3; k++) {
						sum += transform[row * 3 + k] * scaledMatrix[column + k * 3];
					}
					finalMatrix[row * 3 + column] = sum + 8192 >> 14;
				}
			}
			int vertexOffsetX = transform[0] * translateX + transform[1] * translateY + transform[2] * translateZ + 8192 >> 14;
			int vertexOffsetY = transform[3] * translateX + transform[4] * translateY + transform[5] * translateZ + 8192 >> 14;
			int vertexOffsetZ = transform[6] * translateX + transform[7] * translateY + transform[8] * translateZ + 8192 >> 14;
			vertexOffsetX += translationX;
			vertexOffsetY += translationY;
			vertexOffsetZ += translationZ;
			for (int labelIndex = 0; labelIndex < length; labelIndex++) {
				int label = labels[labelIndex];
				if (label < this.vertexLabels.length) {
					int[] vertices = this.vertexLabels[label];
					for (int groupIndex = 0; groupIndex < vertices.length; groupIndex++) {
						int vertex = vertices[groupIndex];
						if (this.originModels == null || (submesh & this.originModels[vertex]) != 0) {
							int rotatedX = finalMatrix[0] * this.vertexX[vertex] + finalMatrix[1] * this.vertexY[vertex] + finalMatrix[2] * this.vertexZ[vertex] + 8192 >> 14;
							int rotatedY = finalMatrix[3] * this.vertexX[vertex] + finalMatrix[4] * this.vertexY[vertex] + finalMatrix[5] * this.vertexZ[vertex] + 8192 >> 14;
							int rotatedZ = finalMatrix[6] * this.vertexX[vertex] + finalMatrix[7] * this.vertexY[vertex] + finalMatrix[8] * this.vertexZ[vertex] + 8192 >> 14;
							@Pc(2145) int newX = rotatedX + vertexOffsetX;
							@Pc(2149) int newY = rotatedY + vertexOffsetY;
							@Pc(2153) int newZ = rotatedZ + vertexOffsetZ;
							this.vertexX[vertex] = newX;
							this.vertexY[vertex] = newY;
							this.vertexZ[vertex] = newZ;
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "h", descriptor = "()V")
	private void ensureNormals() {
		if (this.lightingState != 0 || this.lb != null) {
			return;
		}
		if (this.toolkit.threadCount > 1) {
			synchronized (this) {
				this.calculateNormals();
			}
		} else {
			this.calculateNormals();
		}
	}

	@OriginalMember(owner = "client!jga", name = "wa", descriptor = "()V")
	@Override
	public void wa() {
		if (this.highPrecisionVertices) {
			for (@Pc(4) int i = 0; i < this.vertexCount; i++) {
				this.vertexX[i] = this.vertexX[i] + 7 >> 4;
				this.vertexY[i] = this.vertexY[i] + 7 >> 4;
				this.vertexZ[i] = this.vertexZ[i] + 7 >> 4;
			}
			this.highPrecisionVertices = false;
		}
		if (this.recoloured) {
			this.relight();
			this.recoloured = false;
		}
		this.boundsValid = false;
	}

	@OriginalMember(owner = "client!jga", name = "fa", descriptor = "()I")
	@Override
	public int fa() {
		if (!this.boundsValid) {
			this.calculateBounds();
		}
		return this.minY;
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(II)I")
	private int shadeHsl(@OriginalArg(0) int hsl, @OriginalArg(1) int lightness) {
		lightness = lightness * (hsl & 0x7F) >> 7;
		if (lightness < 2) {
			lightness = 2;
		} else if (lightness > 126) {
			lightness = 126;
		}
		return (hsl & 0xFF80) + lightness;
	}

	@OriginalMember(owner = "client!jga", name = "d", descriptor = "(I)Z")
	private boolean hasBillboard(@OriginalArg(0) int face) {
		if (this.faceBillboard == null) {
			return false;
		} else {
			return this.faceBillboard[face] != -1;
		}
	}

	@OriginalMember(owner = "client!jga", name = "da", descriptor = "()I")
	@Override
	public int da() {
		return this.contrast;
	}

	@OriginalMember(owner = "client!jga", name = "i", descriptor = "()V")
	private void rotate270() {
		synchronized (this) {
			for (@Pc(5) int i = 0; i < this.vertexCount; i++) {
				@Pc(11) int oldZ = this.vertexZ[i];
				this.vertexZ[i] = this.vertexX[i];
				this.vertexX[i] = -oldZ;
			}
			this.invalidateNormals();
		}
	}

	@OriginalMember(owner = "client!jga", name = "c", descriptor = "()Z")
	@Override
	public boolean loadedTextures() {
		if (this.faceTextures == null) {
			return true;
		}
		for (@Pc(6) int i = 0; i < this.faceTextures.length; i++) {
			if (this.faceTextures[i] != -1 && !this.toolkit.textureAvailable(this.faceTextures[i])) {
				return false;
			}
		}
		return true;
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Lclient!bca;)V")
	@Override
	public void updateParticlePosition(@OriginalArg(0) Matrix matrix) {
		@Pc(2) JavaMatrix javaMatrix = (JavaMatrix) matrix;
		@Pc(7) int i;
		if (this.emitters != null) {
			for (i = 0; i < this.emitters.length; i++) {
				@Pc(13) EmissiveTriangle modelParticleEmitter = this.emitters[i];
				@Pc(15) EmissiveTriangle modelParticleEmitter2 = modelParticleEmitter;
				if (modelParticleEmitter.child != null) {
					modelParticleEmitter2 = modelParticleEmitter.child;
				}
				modelParticleEmitter2.verticeZC = (int) (javaMatrix.tx + javaMatrix.e1_1 * (float) this.vertexX[modelParticleEmitter.faceA] + javaMatrix.e1_2 * (float) this.vertexY[modelParticleEmitter.faceA] + javaMatrix.e1_3 * (float) this.vertexZ[modelParticleEmitter.faceA]);
				modelParticleEmitter2.verticeXC = (int) (javaMatrix.ty + javaMatrix.e2_1 * (float) this.vertexX[modelParticleEmitter.faceA] + javaMatrix.e2_2 * (float) this.vertexY[modelParticleEmitter.faceA] + javaMatrix.e2_3 * (float) this.vertexZ[modelParticleEmitter.faceA]);
				modelParticleEmitter2.verticeYA = (int) (javaMatrix.tz + javaMatrix.e3_1 * (float) this.vertexX[modelParticleEmitter.faceA] + javaMatrix.e3_2 * (float) this.vertexY[modelParticleEmitter.faceA] + javaMatrix.e3_3 * (float) this.vertexZ[modelParticleEmitter.faceA]);
				modelParticleEmitter2.verticeZA = (int) (javaMatrix.tx + javaMatrix.e1_1 * (float) this.vertexX[modelParticleEmitter.faceB] + javaMatrix.e1_2 * (float) this.vertexY[modelParticleEmitter.faceB] + javaMatrix.e1_3 * (float) this.vertexZ[modelParticleEmitter.faceB]);
				modelParticleEmitter2.verticeXA = (int) (javaMatrix.ty + javaMatrix.e2_1 * (float) this.vertexX[modelParticleEmitter.faceB] + javaMatrix.e2_2 * (float) this.vertexY[modelParticleEmitter.faceB] + javaMatrix.e2_3 * (float) this.vertexZ[modelParticleEmitter.faceB]);
				modelParticleEmitter2.verticeZB = (int) (javaMatrix.tz + javaMatrix.e3_1 * (float) this.vertexX[modelParticleEmitter.faceB] + javaMatrix.e3_2 * (float) this.vertexY[modelParticleEmitter.faceB] + javaMatrix.e3_3 * (float) this.vertexZ[modelParticleEmitter.faceB]);
				modelParticleEmitter2.verticeYB = (int) (javaMatrix.tx + javaMatrix.e1_1 * (float) this.vertexX[modelParticleEmitter.vertexC] + javaMatrix.e1_2 * (float) this.vertexY[modelParticleEmitter.vertexC] + javaMatrix.e1_3 * (float) this.vertexZ[modelParticleEmitter.vertexC]);
				modelParticleEmitter2.transformedCY = (int) (javaMatrix.ty + javaMatrix.e2_1 * (float) this.vertexX[modelParticleEmitter.vertexC] + javaMatrix.e2_2 * (float) this.vertexY[modelParticleEmitter.vertexC] + javaMatrix.e2_3 * (float) this.vertexZ[modelParticleEmitter.vertexC]);
				modelParticleEmitter2.verticeYC = (int) (javaMatrix.tz + javaMatrix.e3_1 * (float) this.vertexX[modelParticleEmitter.vertexC] + javaMatrix.e3_2 * (float) this.vertexY[modelParticleEmitter.vertexC] + javaMatrix.e3_3 * (float) this.vertexZ[modelParticleEmitter.vertexC]);
			}
		}
		if (this.effectors == null) {
			return;
		}
		for (i = 0; i < this.effectors.length; i++) {
			@Pc(353) EffectiveVertex modelParticleEffector = this.effectors[i];
			@Pc(355) EffectiveVertex modelParticleEffector2 = modelParticleEffector;
			if (modelParticleEffector.child != null) {
				modelParticleEffector2 = modelParticleEffector.child;
			}
			if (modelParticleEffector.matrix == null) {
				modelParticleEffector.matrix = javaMatrix.copy();
			} else {
				modelParticleEffector.matrix.apply(javaMatrix);
			}
			modelParticleEffector2.verticeZ = (int) (javaMatrix.tx + javaMatrix.e1_1 * (float) this.vertexX[modelParticleEffector.appliesOnVertex] + javaMatrix.e1_2 * (float) this.vertexY[modelParticleEffector.appliesOnVertex] + javaMatrix.e1_3 * (float) this.vertexZ[modelParticleEffector.appliesOnVertex]);
			modelParticleEffector2.verticeX = (int) (javaMatrix.ty + javaMatrix.e2_1 * (float) this.vertexX[modelParticleEffector.appliesOnVertex] + javaMatrix.e2_2 * (float) this.vertexY[modelParticleEffector.appliesOnVertex] + javaMatrix.e2_3 * (float) this.vertexZ[modelParticleEffector.appliesOnVertex]);
			modelParticleEffector2.verticeY = (int) (javaMatrix.tz + javaMatrix.e3_1 * (float) this.vertexX[modelParticleEffector.appliesOnVertex] + javaMatrix.e3_2 * (float) this.vertexY[modelParticleEffector.appliesOnVertex] + javaMatrix.e3_3 * (float) this.vertexZ[modelParticleEffector.appliesOnVertex]);
		}
	}

	@OriginalMember(owner = "client!jga", name = "j", descriptor = "()V")
	private void relightColours() {
		for (@Pc(1) int face = 0; face < this.faceCount; face++) {
			@Pc(12) short texture = this.faceTextures == null ? -1 : this.faceTextures[face];
			if (texture == -1) {
				@Pc(22) int hsl = this.faceColour[face] & 0xFFFF;
				@Pc(31) int lightness = (hsl & 0x7F) * this.ambient >> 7;
				@Pc(39) short hsv = ColorUtil.hslToHsv(hsl & 0xFFFFFF80 | lightness);
				@Pc(52) int shade;
				if (this.faceColourC[face] == -1) {
					shade = this.faceColourA[face] & 0xFFFE0000;
					this.faceColourA[face] = shade | JavaModel.applyLightness(shade >> 17, hsv);
				} else if (this.faceColourC[face] != -2) {
					shade = this.faceColourA[face] & 0xFFFE0000;
					this.faceColourA[face] = shade | JavaModel.applyLightness(shade >> 17, hsv);
					shade = this.faceColourB[face] & 0xFFFE0000;
					this.faceColourB[face] = shade | JavaModel.applyLightness(shade >> 17, hsv);
					shade = this.faceColourC[face] & 0xFFFE0000;
					this.faceColourC[face] = shade | JavaModel.applyLightness(shade >> 17, hsv);
				}
			}
		}
		this.lightingState = 2;
	}

	@OriginalMember(owner = "client!jga", name = "k", descriptor = "(I)V")
	@Override
	public void k(@OriginalArg(0) int angle) {
		if ((this.functionMask & 0xD) != 13) {
			throw new IllegalStateException();
		} else if (this.lb == null) {
			this.a(angle);
		} else if (angle == 4096) {
			this.rotate90WithNormals();
		} else if (angle == 8192) {
			this.rotate180WithNormals();
		} else if (angle == 12288) {
			this.rotate270WithNormals();
		} else {
			@Pc(34) int sin = TextureCombineSource.SINE[angle];
			@Pc(38) int cos = TextureCombineSource.COSINE[angle];
			synchronized (this) {
				@Pc(61) int local61;
				for (@Pc(44) int i = 0; i < this.maxVertex; i++) {
					int rotatedX = this.vertexZ[i] * sin + this.vertexX[i] * cos >> 14;
					this.vertexZ[i] = this.vertexZ[i] * cos - this.vertexX[i] * sin >> 14;
					this.vertexX[i] = rotatedX;
					if (this.lb[i] != null) {
						rotatedX = this.lb[i].z * sin + this.lb[i].x * cos >> 14;
						this.lb[i].z = this.lb[i].z * cos - this.lb[i].x * sin >> 14;
						this.lb[i].x = rotatedX;
					}
				}
				@Pc(170) int newX;
				if (this.faceNormals != null) {
					for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
						if (this.faceNormals[faceIndex] != null) {
							newX = this.faceNormals[faceIndex].z * sin + this.faceNormals[faceIndex].x * cos >> 14;
							this.faceNormals[faceIndex].z = this.faceNormals[faceIndex].z * cos - this.faceNormals[faceIndex].x * sin >> 14;
							this.faceNormals[faceIndex].x = newX;
						}
					}
				}
				for (int vertexIndex = this.maxVertex; vertexIndex < this.vertexCount; vertexIndex++) {
					newX = this.vertexZ[vertexIndex] * sin + this.vertexX[vertexIndex] * cos >> 14;
					this.vertexZ[vertexIndex] = this.vertexZ[vertexIndex] * cos - this.vertexX[vertexIndex] * sin >> 14;
					this.vertexX[vertexIndex] = newX;
				}
				this.lightingState = 0;
				this.boundsValid = false;
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Lclient!bca;IZ)V")
	@Override
	public void transform(@OriginalArg(0) Matrix matrix, @OriginalArg(1) int originMask, @OriginalArg(2) boolean relative) {
		if (this.originModels == null) {
			return;
		}
		@Pc(6) int[] projected = new int[3];
		for (@Pc(8) int i = 0; i < this.maxVertex; i++) {
			if ((originMask & this.originModels[i]) != 0) {
				if (relative) {
					matrix.projectRelative(this.vertexX[i], this.vertexY[i], this.vertexZ[i], projected);
				} else {
					matrix.method4652(this.vertexX[i], this.vertexY[i], this.vertexZ[i], projected);
				}
				this.vertexX[i] = projected[0];
				this.vertexY[i] = projected[1];
				this.vertexZ[i] = projected[2];
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "b", descriptor = "()V")
	@Override
	public void uploadToGpu() {
	}

	@OriginalMember(owner = "client!jga", name = "k", descriptor = "()V")
	private void rotate270WithNormals() {
		synchronized (this) {
			@Pc(11) int local11;
			for (@Pc(5) int i = 0; i < this.maxVertex; i++) {
				int swapZ = this.vertexZ[i];
				this.vertexZ[i] = this.vertexX[i];
				this.vertexX[i] = -swapZ;
				if (this.lb[i] != null) {
					swapZ = this.lb[i].z;
					this.lb[i].z = this.lb[i].x;
					this.lb[i].x = -swapZ;
				}
			}
			@Pc(75) int oldZ;
			if (this.faceNormals != null) {
				for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
					if (this.faceNormals[faceIndex] != null) {
						oldZ = this.faceNormals[faceIndex].z;
						this.faceNormals[faceIndex].z = this.faceNormals[faceIndex].x;
						this.faceNormals[faceIndex].x = -oldZ;
					}
				}
			}
			for (int vertexIndex = this.maxVertex; vertexIndex < this.vertexCount; vertexIndex++) {
				oldZ = this.vertexZ[vertexIndex];
				this.vertexZ[vertexIndex] = this.vertexX[vertexIndex];
				this.vertexX[vertexIndex] = -oldZ;
			}
			this.lightingState = 0;
			this.boundsValid = false;
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(ISI)I")
	private int shadeTexturedRgb(@OriginalArg(0) int hsl, @OriginalArg(1) short texture, @OriginalArg(2) int lightness) {
		@Pc(6) int rgb = ColorUtil.hslToRgb[this.shadeHsl(hsl, lightness)];
		@Pc(15) MaterialType metrics = this.toolkit.materialProvider.getMetrics(texture & 0xFFFF);
		@Pc(20) int alpha = metrics.alpha & 0xFF;
		@Pc(26) int local26;
		@Pc(38) int local38;
		if (alpha != 0) {
			int greyRgb = lightness * 131586;
			if (alpha == 256) {
				rgb = greyRgb;
			} else {
				int inverseAlpha = 256 - alpha;
				rgb = ((greyRgb & 0xFF00FF) * alpha + (rgb & 0xFF00FF) * inverseAlpha & 0xFF00FF00) + ((greyRgb & 0xFF00) * alpha + (rgb & 0xFF00) * inverseAlpha & 0xFF0000) >> 8;
			}
		}
		int brightness = metrics.brightness & 0xFF;
		if (brightness != 0) {
			brightness += 256;
			@Pc(84) int red = (rgb >> 16 & 0xFF) * brightness;
			if (red > 65535) {
				red = 65535;
			}
			int green = (rgb >> 8 & 0xFF) * brightness;
			if (green > 65535) {
				green = 65535;
			}
			@Pc(108) int blue = (rgb & 0xFF) * brightness;
			if (blue > 65535) {
				blue = 65535;
			}
			rgb = ((red & 0xFF00) << 8) + (green & 0xFF00) + (blue >> 8);
		}
		return rgb;
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Lclient!bca;Lclient!iw;II)V")
	@Override
	public void renderOrtho(@OriginalArg(0) Matrix matrix, @OriginalArg(1) BoundingBoxPolygon cylinder, @OriginalArg(2) int orthoDepth, @OriginalArg(3) int flags) {
		this.draw(matrix, cylinder, orthoDepth, flags);
	}

	@OriginalMember(owner = "client!jga", name = "NA", descriptor = "()Z")
	@Override
	public boolean NA() {
		if (this.vertexLabels == null) {
			return false;
		} else {
			this.pivotX = 0;
			this.pivotY = 0;
			this.pivotZ = 0;
			return true;
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(IZZ)V")
	private void drawFaceRgb(@OriginalArg(0) int face, @OriginalArg(1) boolean nearClipped, @OriginalArg(2) boolean fogged) {
		if (this.faceColourC[face] == -2) {
			return;
		}
		@Pc(11) short a = this.faceA[face];
		@Pc(16) short b = this.faceB[face];
		@Pc(21) short c = this.faceC[face];
		@Pc(26) int screenXA = this.screenX[a];
		@Pc(31) int screenXB = this.screenX[b];
		@Pc(36) int screenXC = this.screenX[c];
		@Pc(52) int local52;
		if (nearClipped && (screenXA == -5000 || screenXB == -5000 || screenXC == -5000)) {
			int xA = this.cameraX[a];
			@Pc(57) int xB = this.cameraX[b];
			@Pc(62) int xC = this.cameraX[c];
			@Pc(67) int yA = this.cameraY[a];
			@Pc(72) int yB = this.cameraY[b];
			@Pc(77) int yC = this.cameraY[c];
			@Pc(82) int zA = this.cameraZ[a];
			@Pc(87) int zB = this.cameraZ[b];
			@Pc(92) int zC = this.cameraZ[c];
			@Pc(96) int abX = xA - xB;
			@Pc(100) int cbX = xC - xB;
			@Pc(104) int abY = yA - yB;
			@Pc(108) int cbY = yC - yB;
			@Pc(112) int abZ = zA - zB;
			@Pc(116) int cbZ = zC - zB;
			@Pc(124) int normalX = abY * cbZ - abZ * cbY;
			@Pc(132) int normalY = abZ * cbX - abX * cbZ;
			@Pc(140) int normalZ = abX * cbY - abY * cbX;
			if (xB * normalX + yB * normalY + zB * normalZ > 0) {
				this.drawClippedTriangleRgb(face);
				return;
			}
		} else if (this.faceBillboard[face] != -1 || (screenXA - screenXB) * (this.screenY[c] - this.screenY[b]) - (this.screenY[a] - this.screenY[b]) * (screenXC - screenXB) > 0) {
			if (screenXA >= 0 && screenXB >= 0 && screenXC >= 0 && screenXA <= this.threadResource.rasterWidth && screenXB <= this.threadResource.rasterWidth && screenXC <= this.threadResource.rasterWidth) {
				this.rasterizer.clamp = false;
			} else {
				this.rasterizer.clamp = true;
			}
			if (fogged) {
				int billboardIndex = this.faceBillboard[face];
				if (billboardIndex == -1 || !this.billboardFaces[billboardIndex].hideFace) {
					this.drawFoggedTriangleRgb(face);
				}
				return;
			}
			int billboardIndex = this.faceBillboard[face];
			if (billboardIndex != -1) {
				@Pc(253) JavaBillboardFace javaBillboardFace = this.billboardFaces[billboardIndex];
				@Pc(258) JavaBillboardAttributes javaBillboardAttributes = this.billboardAttributes[billboardIndex];
				if (!javaBillboardFace.hideFace) {
					this.drawTriangleRgb(face);
				}
				this.toolkit.drawBillboardRgb(javaBillboardAttributes.x, javaBillboardAttributes.y, javaBillboardAttributes.z, javaBillboardAttributes.projectedWidth, javaBillboardAttributes.projectedHeight, javaBillboardAttributes.rotation, javaBillboardFace.texture & 0xFFFF, javaBillboardAttributes.diffuseColor, javaBillboardFace.colourOp, javaBillboardFace.blendMode);
				return;
			}
			this.drawTriangleRgb(face);
		}
	}

	@OriginalMember(owner = "client!jga", name = "F", descriptor = "()Z")
	@Override
	public boolean F() {
		return this.transparent;
	}

	@OriginalMember(owner = "client!jga", name = "P", descriptor = "(IIII)V")
	@Override
	public void P(@OriginalArg(0) int type, @OriginalArg(1) int pivotX, @OriginalArg(2) int pivotY, @OriginalArg(3) int pivotZ) {
		@Pc(3) int local3;
		@Pc(14) int local14;
		if (type == 0) {
			int pivotCount = 0;
			this.pivotX = 0;
			this.pivotY = 0;
			this.pivotZ = 0;
			for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
				this.pivotX += this.vertexX[vertexIndex];
				this.pivotY += this.vertexY[vertexIndex];
				this.pivotZ += this.vertexZ[vertexIndex];
				pivotCount++;
			}
			if (pivotCount > 0) {
				this.pivotX = this.pivotX / pivotCount + pivotX;
				this.pivotY = this.pivotY / pivotCount + pivotY;
				this.pivotZ = this.pivotZ / pivotCount + pivotZ;
			} else {
				this.pivotX = pivotX;
				this.pivotY = pivotY;
				this.pivotZ = pivotZ;
			}
		} else if (type == 1) {
			for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
				this.vertexX[vertexIndex] += pivotX;
				this.vertexY[vertexIndex] += pivotY;
				this.vertexZ[vertexIndex] += pivotZ;
			}
		} else {
			@Pc(164) int local164;
			@Pc(182) int local182;
			if (type == 2) {
				for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
					this.vertexX[vertexIndex] -= this.pivotX;
					this.vertexY[vertexIndex] -= this.pivotY;
					this.vertexZ[vertexIndex] -= this.pivotZ;
					if (pivotZ != 0) {
						int sin = TextureCombineSource.SINE[pivotZ];
						int cos = TextureCombineSource.COSINE[pivotZ];
						int rotatedX = this.vertexY[vertexIndex] * sin + this.vertexX[vertexIndex] * cos + 16383 >> 14;
						this.vertexY[vertexIndex] = this.vertexY[vertexIndex] * cos + 16383 - this.vertexX[vertexIndex] * sin >> 14;
						this.vertexX[vertexIndex] = rotatedX;
					}
					if (pivotX != 0) {
						int sin = TextureCombineSource.SINE[pivotX];
						int cos = TextureCombineSource.COSINE[pivotX];
						int rotatedY = this.vertexY[vertexIndex] * cos + 16383 - this.vertexZ[vertexIndex] * sin >> 14;
						this.vertexZ[vertexIndex] = this.vertexY[vertexIndex] * sin + this.vertexZ[vertexIndex] * cos + 16383 >> 14;
						this.vertexY[vertexIndex] = rotatedY;
					}
					if (pivotY != 0) {
						int sin = TextureCombineSource.SINE[pivotY];
						int cos = TextureCombineSource.COSINE[pivotY];
						int rotatedX = this.vertexZ[vertexIndex] * sin + this.vertexX[vertexIndex] * cos + 16383 >> 14;
						this.vertexZ[vertexIndex] = this.vertexZ[vertexIndex] * cos + 16383 - this.vertexX[vertexIndex] * sin >> 14;
						this.vertexX[vertexIndex] = rotatedX;
					}
					this.vertexX[vertexIndex] += this.pivotX;
					this.vertexY[vertexIndex] += this.pivotY;
					this.vertexZ[vertexIndex] += this.pivotZ;
				}
			} else if (type == 3) {
				for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
					this.vertexX[vertexIndex] -= this.pivotX;
					this.vertexY[vertexIndex] -= this.pivotY;
					this.vertexZ[vertexIndex] -= this.pivotZ;
					this.vertexX[vertexIndex] = this.vertexX[vertexIndex] * pivotX / 128;
					this.vertexY[vertexIndex] = this.vertexY[vertexIndex] * pivotY / 128;
					this.vertexZ[vertexIndex] = this.vertexZ[vertexIndex] * pivotZ / 128;
					this.vertexX[vertexIndex] += this.pivotX;
					this.vertexY[vertexIndex] += this.pivotY;
					this.vertexZ[vertexIndex] += this.pivotZ;
				}
			} else {
				@Pc(500) JavaBillboardFace javaBillboardFace;
				@Pc(505) JavaBillboardAttributes javaBillboardAttributes;
				if (type == 5) {
					for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
						int alpha = (this.faceAlpha[faceIndex] & 0xFF) + pivotX * 8;
						if (alpha < 0) {
							alpha = 0;
						} else if (alpha > 255) {
							alpha = 255;
						}
						this.faceAlpha[faceIndex] = (byte) alpha;
					}
					if (this.billboardFaces != null) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							javaBillboardFace = this.billboardFaces[billboardIndex];
							javaBillboardAttributes = this.billboardAttributes[billboardIndex];
							javaBillboardAttributes.diffuseColor = javaBillboardAttributes.diffuseColor & 0xFFFFFF | 255 - (this.faceAlpha[javaBillboardFace.face] & 0xFF) << 24;
						}
					}
				} else if (type == 7) {
					for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
						int hsl = this.faceColour[faceIndex] & 0xFFFF;
						int oldHue = hsl >> 10 & 0x3F;
						int saturation = hsl >> 7 & 0x7;
						@Pc(558) int lightness = hsl & 0x7F;
						@Pc(564) int hue = oldHue + pivotX & 0x3F;
						saturation += pivotY;
						if (saturation < 0) {
							saturation = 0;
						} else if (saturation > 7) {
							saturation = 7;
						}
						lightness += pivotZ;
						if (lightness < 0) {
							lightness = 0;
						} else if (lightness > 127) {
							lightness = 127;
						}
						this.faceColour[faceIndex] = (short) (hue << 10 | saturation << 7 | lightness);
					}
					this.recoloured = true;
					if (this.billboardFaces != null) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							javaBillboardFace = this.billboardFaces[billboardIndex];
							javaBillboardAttributes = this.billboardAttributes[billboardIndex];
							javaBillboardAttributes.diffuseColor = javaBillboardAttributes.diffuseColor & 0xFF000000 | ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(this.faceColour[javaBillboardFace.face] & 0xFFFF) & 0xFFFF] & 0xFFFFFF;
						}
					}
				} else {
					@Pc(669) JavaBillboardAttributes javaBillboardAttributes2;
					if (type == 8) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							javaBillboardAttributes2 = this.billboardAttributes[billboardIndex];
							javaBillboardAttributes2.offsetX += pivotX;
							javaBillboardAttributes2.offsetY += pivotY;
						}
					} else if (type == 10) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							javaBillboardAttributes2 = this.billboardAttributes[billboardIndex];
							javaBillboardAttributes2.scaleX = javaBillboardAttributes2.scaleX * pivotX >> 7;
							javaBillboardAttributes2.scaleY = javaBillboardAttributes2.scaleY * pivotY >> 7;
						}
					} else if (type == 9) {
						for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
							javaBillboardAttributes2 = this.billboardAttributes[billboardIndex];
							javaBillboardAttributes2.rotation = javaBillboardAttributes2.rotation + pivotX & 0x3FFF;
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "na", descriptor = "()I")
	@Override
	public int na() {
		if (!this.boundsValid) {
			this.calculateBounds();
		}
		return this.radius;
	}

	@OriginalMember(owner = "client!jga", name = "ba", descriptor = "(Lclient!r;)Lclient!r;")
	@Override
	public Shadow ba(@OriginalArg(0) Shadow shadow) {
		return null;
	}

	@OriginalMember(owner = "client!jga", name = "p", descriptor = "(IILclient!s;Lclient!s;III)V")
	@Override
	public void p(@OriginalArg(0) int contourType, @OriginalArg(1) int contourModifier, @OriginalArg(2) Ground ground, @OriginalArg(3) Ground ground2, @OriginalArg(4) int x, @OriginalArg(5) int y, @OriginalArg(6) int z) {
		if (contourType == 3) {
			if ((this.functionMask & 0x7) != 7) {
				throw new IllegalStateException();
			}
		} else if ((this.functionMask & 0x2) != 2) {
			throw new IllegalStateException();
		}
		if (!this.boundsValid) {
			this.calculateBounds();
		}
		@Pc(32) int minTileX = x + this.minX;
		@Pc(37) int maxTileX = x + this.maxX;
		@Pc(42) int minTileZ = z + this.minZ;
		@Pc(47) int maxTileZ = z + this.maxZ;
		if (contourType != 4 && (minTileX < 0 || maxTileX + ground.tileUnits >> ground.tileScale >= ground.width || minTileZ < 0 || maxTileZ + ground.tileUnits >> ground.tileScale >= ground.length)) {
			return;
		}
		@Pc(79) int[][] tileHeights = ((JavaGround) ground).tileHeights;
		@Pc(81) int[][] otherHeights = null;
		if (ground2 != null) {
			otherHeights = ((JavaGround) ground2).tileHeights;
		}
		if (contourType == 4 || contourType == 5) {
			if (ground2 == null) {
				return;
			}
			if (minTileX < 0 || maxTileX + ground2.tileUnits >> ground2.tileScale >= ground2.width || minTileZ < 0 || maxTileZ + ground2.tileUnits >> ground2.tileScale >= ground2.length) {
				return;
			}
		} else {
			minTileX >>= ground.tileScale;
			maxTileX = maxTileX + ground.tileUnits - 1 >> ground.tileScale;
			minTileZ >>= ground.tileScale;
			maxTileZ = maxTileZ + ground.tileUnits - 1 >> ground.tileScale;
			if (tileHeights[minTileX][minTileZ] == y && tileHeights[maxTileX][minTileZ] == y && tileHeights[minTileX][maxTileZ] == y && tileHeights[maxTileX][maxTileZ] == y) {
				return;
			}
		}
		synchronized (this) {
			@Pc(192) int local192;
			@Pc(194) int local194;
			@Pc(202) int local202;
			@Pc(209) int local209;
			@Pc(213) int local213;
			@Pc(217) int local217;
			@Pc(222) int local222;
			@Pc(227) int local227;
			@Pc(251) int local251;
			@Pc(279) int local279;
			@Pc(293) int local293;
			@Pc(431) int local431;
			if (contourType == 1) {
				int tileMask = ground.tileUnits - 1;
				for (int vertexIndex = 0; vertexIndex < this.maxVertex; vertexIndex++) {
					int worldX = this.vertexX[vertexIndex] + x;
					int worldZ = this.vertexZ[vertexIndex] + z;
					int fracX = worldX & tileMask;
					int fracZ = worldZ & tileMask;
					int tileX = worldX >> ground.tileScale;
					int tileZ = worldZ >> ground.tileScale;
					int lowerHeight = tileHeights[tileX][tileZ] * (ground.tileUnits - fracX) + tileHeights[tileX + 1][tileZ] * fracX >> ground.tileScale;
					int upperHeight = tileHeights[tileX][tileZ + 1] * (ground.tileUnits - fracX) + tileHeights[tileX + 1][tileZ + 1] * fracX >> ground.tileScale;
					int groundHeight = lowerHeight * (ground.tileUnits - fracZ) + upperHeight * fracZ >> ground.tileScale;
					this.vertexY[vertexIndex] = this.vertexY[vertexIndex] + groundHeight - y;
				}
				for (int vertexIndex = this.maxVertex; vertexIndex < this.vertexCount; vertexIndex++) {
					int worldX = this.vertexX[vertexIndex] + x;
					int worldZ = this.vertexZ[vertexIndex] + z;
					int fracX = worldX & tileMask;
					int fracZ = worldZ & tileMask;
					int tileX = worldX >> ground.tileScale;
					int tileZ = worldZ >> ground.tileScale;
					if (tileX >= 0 && tileX < tileHeights.length - 1 && tileZ >= 0 && tileZ < tileHeights[0].length - 1) {
						int lowerHeight = tileHeights[tileX][tileZ] * (ground.tileUnits - fracX) + tileHeights[tileX + 1][tileZ] * fracX >> ground.tileScale;
						int upperHeight = tileHeights[tileX][tileZ + 1] * (ground.tileUnits - fracX) + tileHeights[tileX + 1][tileZ + 1] * fracX >> ground.tileScale;
						int groundHeight = lowerHeight * (ground.tileUnits - fracZ) + upperHeight * fracZ >> ground.tileScale;
						this.vertexY[vertexIndex] = this.vertexY[vertexIndex] + groundHeight - y;
					}
				}
			} else {
				@Pc(735) int local735;
				if (contourType == 2) {
					int tileMask = ground.tileUnits - 1;
					for (int vertexIndex = 0; vertexIndex < this.maxVertex; vertexIndex++) {
						int heightRatio = (this.vertexY[vertexIndex] << 16) / this.minY;
						if (heightRatio < contourModifier) {
							int worldX = this.vertexX[vertexIndex] + x;
							int worldZ = this.vertexZ[vertexIndex] + z;
							int fracX = worldX & tileMask;
							int fracZ = worldZ & tileMask;
							int tileX = worldX >> ground.tileScale;
							int tileZ = worldZ >> ground.tileScale;
							int lowerHeight = tileHeights[tileX][tileZ] * (ground.tileUnits - fracX) + tileHeights[tileX + 1][tileZ] * fracX >> ground.tileScale;
							int upperHeight = tileHeights[tileX][tileZ + 1] * (ground.tileUnits - fracX) + tileHeights[tileX + 1][tileZ + 1] * fracX >> ground.tileScale;
							int groundHeight = lowerHeight * (ground.tileUnits - fracZ) + upperHeight * fracZ >> ground.tileScale;
							this.vertexY[vertexIndex] += (groundHeight - y) * (contourModifier - heightRatio) / contourModifier;
						} else {
							this.vertexY[vertexIndex] = this.vertexY[vertexIndex];
						}
					}
					for (int vertexIndex = this.maxVertex; vertexIndex < this.vertexCount; vertexIndex++) {
						int heightRatio = (this.vertexY[vertexIndex] << 16) / this.minY;
						if (heightRatio < contourModifier) {
							int worldX = this.vertexX[vertexIndex] + x;
							int worldZ = this.vertexZ[vertexIndex] + z;
							int fracX = worldX & tileMask;
							int fracZ = worldZ & tileMask;
							int tileX = worldX >> ground.tileScale;
							int tileZ = worldZ >> ground.tileScale;
							if (tileX >= 0 && tileX < ground.width - 1 && tileZ >= 0 && tileZ < ground.length - 1) {
								int lowerHeight = tileHeights[tileX][tileZ] * (ground.tileUnits - fracX) + tileHeights[tileX + 1][tileZ] * fracX >> ground.tileScale;
								int upperHeight = tileHeights[tileX][tileZ + 1] * (ground.tileUnits - fracX) + tileHeights[tileX + 1][tileZ + 1] * fracX >> ground.tileScale;
								int groundHeight = lowerHeight * (ground.tileUnits - fracZ) + upperHeight * fracZ >> ground.tileScale;
								this.vertexY[vertexIndex] += (groundHeight - y) * (contourModifier - heightRatio) / contourModifier;
							}
						} else {
							this.vertexY[vertexIndex] = this.vertexY[vertexIndex];
						}
					}
				} else if (contourType == 3) {
					int sizeX = (contourModifier & 0xFF) * 4;
					int sizeZ = (contourModifier >> 8 & 0xFF) * 4;
					int maxRotateX = (contourModifier >> 16 & 0xFF) << 6;
					int maxRotateZ = (contourModifier >> 24 & 0xFF) << 6;
					if (x - (sizeX >> 1) < 0 || x + (sizeX >> 1) + ground.tileUnits >= ground.width << ground.tileScale || z - (sizeZ >> 1) < 0 || z + (sizeZ >> 1) + ground.tileUnits >= ground.length << ground.tileScale) {
						return;
					}
					this.hillChange(sizeX, maxRotateZ, x, sizeZ, y, maxRotateX, ground, z);
				} else if (contourType == 4) {
					int tileMask = ground2.tileUnits - 1;
					int modelHeight = this.maxY - this.minY;
					for (int vertexIndex = 0; vertexIndex < this.maxVertex; vertexIndex++) {
						int worldX = this.vertexX[vertexIndex] + x;
						int worldZ = this.vertexZ[vertexIndex] + z;
						int fracX = worldX & tileMask;
						int fracZ = worldZ & tileMask;
						int tileX = worldX >> ground2.tileScale;
						int tileZ = worldZ >> ground2.tileScale;
						int lowerHeight = otherHeights[tileX][tileZ] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1][tileZ] * fracX >> ground2.tileScale;
						int upperHeight = otherHeights[tileX][tileZ + 1] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1][tileZ + 1] * fracX >> ground2.tileScale;
						int groundHeight = lowerHeight * (ground2.tileUnits - fracZ) + upperHeight * fracZ >> ground2.tileScale;
						this.vertexY[vertexIndex] = this.vertexY[vertexIndex] + groundHeight + modelHeight - y;
					}
					for (int vertexIndex = this.maxVertex; vertexIndex < this.vertexCount; vertexIndex++) {
						int worldX = this.vertexX[vertexIndex] + x;
						int worldZ = this.vertexZ[vertexIndex] + z;
						int fracX = worldX & tileMask;
						int fracZ = worldZ & tileMask;
						int tileX = worldX >> ground2.tileScale;
						int tileZ = worldZ >> ground2.tileScale;
						if (tileX >= 0 && tileX < ground2.width - 1 && tileZ >= 0 && tileZ < ground2.length - 1) {
							int lowerHeight = otherHeights[tileX][tileZ] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1][tileZ] * fracX >> ground2.tileScale;
							int upperHeight = otherHeights[tileX][tileZ + 1] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1][tileZ + 1] * fracX >> ground2.tileScale;
							int groundHeight = lowerHeight * (ground2.tileUnits - fracZ) + upperHeight * fracZ >> ground2.tileScale;
							this.vertexY[vertexIndex] = this.vertexY[vertexIndex] + groundHeight + modelHeight - y;
						}
					}
				} else if (contourType == 5) {
					int tileMask = ground2.tileUnits - 1;
					int modelHeight = this.maxY - this.minY;
					@Pc(1316) int local1316;
					for (int vertexIndex = 0; vertexIndex < this.maxVertex; vertexIndex++) {
						int worldX = this.vertexX[vertexIndex] + x;
						int worldZ = this.vertexZ[vertexIndex] + z;
						int fracX = worldX & tileMask;
						int fracZ = worldZ & tileMask;
						int tileX = worldX >> ground.tileScale;
						int tileZ = worldZ >> ground.tileScale;
						int lowerHeight = tileHeights[tileX][tileZ] * (ground.tileUnits - fracX) + tileHeights[tileX + 1][tileZ] * fracX >> ground.tileScale;
						int upperHeight = tileHeights[tileX][tileZ + 1] * (ground.tileUnits - fracX) + tileHeights[tileX + 1][tileZ + 1] * fracX >> ground.tileScale;
						int groundHeight = lowerHeight * (ground.tileUnits - fracZ) + upperHeight * fracZ >> ground.tileScale;
						lowerHeight = otherHeights[tileX][tileZ] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1][tileZ] * fracX >> ground2.tileScale;
						upperHeight = otherHeights[tileX][tileZ + 1] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1][tileZ + 1] * fracX >> ground2.tileScale;
						int otherGroundHeight = lowerHeight * (ground2.tileUnits - fracZ) + upperHeight * fracZ >> ground2.tileScale;
						int groundDelta = groundHeight - otherGroundHeight - contourModifier;
						this.vertexY[vertexIndex] = ((this.vertexY[vertexIndex] << 8) / modelHeight * groundDelta >> 8) - (y - groundHeight);
					}
					for (int vertexIndex = this.maxVertex; vertexIndex < this.vertexCount; vertexIndex++) {
						int worldX = this.vertexX[vertexIndex] + x;
						int worldZ = this.vertexZ[vertexIndex] + z;
						int fracX = worldX & tileMask;
						int fracZ = worldZ & tileMask;
						int tileX = worldX >> ground.tileScale;
						int tileZ = worldZ >> ground.tileScale;
						if (tileX >= 0 && tileX < ground.width - 1 && tileX < ground2.width - 1 && tileZ >= 0 && tileZ < ground.length - 1 && tileZ < ground2.length - 1) {
							int lowerHeight = tileHeights[tileX][tileZ] * (ground.tileUnits - fracX) + tileHeights[tileX + 1][tileZ] * fracX >> ground.tileScale;
							int upperHeight = tileHeights[tileX][tileZ + 1] * (ground.tileUnits - fracX) + tileHeights[tileX + 1][tileZ + 1] * fracX >> ground.tileScale;
							int groundHeight = lowerHeight * (ground.tileUnits - fracZ) + upperHeight * fracZ >> ground.tileScale;
							lowerHeight = otherHeights[tileX][tileZ] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1][tileZ] * fracX >> ground2.tileScale;
							upperHeight = otherHeights[tileX][tileZ + 1] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1][tileZ + 1] * fracX >> ground2.tileScale;
							int otherGroundHeight = lowerHeight * (ground2.tileUnits - fracZ) + upperHeight * fracZ >> ground2.tileScale;
							@Pc(1544) int heightDelta = groundHeight - otherGroundHeight - contourModifier;
							this.vertexY[vertexIndex] = ((this.vertexY[vertexIndex] << 8) / modelHeight * heightDelta >> 8) - (y - groundHeight);
						}
					}
				}
			}
			this.boundsValid = false;
		}
	}

	@OriginalMember(owner = "client!jga", name = "C", descriptor = "(I)V")
	@Override
	public void C(@OriginalArg(0) int ambient) {
		if ((this.functionMask & 0x1000) != 4096) {
			throw new IllegalStateException();
		}
		this.ambient = ambient;
		this.lightingState = 0;
	}

	@OriginalMember(owner = "client!jga", name = "l", descriptor = "()V")
	private void rotate90() {
		synchronized (this) {
			for (@Pc(5) int i = 0; i < this.vertexCount; i++) {
				@Pc(11) int oldX = this.vertexX[i];
				this.vertexX[i] = this.vertexZ[i];
				this.vertexZ[i] = -oldX;
			}
			this.invalidateNormals();
		}
	}

	@OriginalMember(owner = "client!jga", name = "RA", descriptor = "()I")
	@Override
	public int RA() {
		if (!this.boundsValid) {
			this.calculateBounds();
		}
		return this.maxX;
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(IILclient!bca;ZII)Z")
	@Override
	public boolean pickedOrtho(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) Matrix matrix, @OriginalArg(3) boolean quick, @OriginalArg(4) int sizeShift, @OriginalArg(5) int angle) {
		return this.pick(x, y, matrix, quick, sizeShift, angle);
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "()[Lclient!eca;")
	@Override
	public EffectiveVertex[] getEffectiveVertices() {
		return this.effectors;
	}

	@OriginalMember(owner = "client!jga", name = "WA", descriptor = "()I")
	@Override
	public int WA() {
		return this.ambient;
	}

	@OriginalMember(owner = "client!jga", name = "s", descriptor = "(I)V")
	@Override
	public void s(@OriginalArg(0) int functionMask) {
		if (this.toolkit.threadCount <= 1) {
			if ((this.functionMask & 0x10000) == 65536 && (functionMask & 0x10000) == 0) {
				this.applyLighting(true);
			}
			this.functionMask = functionMask;
			return;
		}
		synchronized (this) {
			if ((this.functionMask & 0x10000) == 65536 && (functionMask & 0x10000) == 0) {
				this.applyLighting(true);
			}
			this.functionMask = functionMask;
		}
	}

	@OriginalMember(owner = "client!jga", name = "e", descriptor = "()V")
	@Override
	public void unlock() {
		if (this.toolkit.threadCount > 1) {
			synchronized (this) {
				super.isLocked = false;
				this.notifyAll();
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(IIII)V")
	@Override
	public void adjustColours(@OriginalArg(0) int hue, @OriginalArg(1) int saturation, @OriginalArg(2) int lightness, @OriginalArg(3) int scale) {
		if ((this.functionMask & 0x80000) != 524288) {
			throw new IllegalStateException("FMT");
		}
		@Pc(20) int local20;
		for (@Pc(12) int i = 0; i < this.faceCount; i++) {
			int hsl = this.faceColour[i] & 0xFFFF;
			@Pc(26) int faceHue = hsl >> 10 & 0x3F;
			@Pc(32) int faceSaturation = hsl >> 7 & 0x7;
			@Pc(36) int faceLightness = hsl & 0x7F;
			if (hue != -1) {
				faceHue += (hue - faceHue) * scale >> 7;
			}
			if (saturation != -1) {
				faceSaturation += (saturation - faceSaturation) * scale >> 7;
			}
			if (lightness != -1) {
				faceLightness += (lightness - faceLightness) * scale >> 7;
			}
			this.faceColour[i] = (short) (faceHue << 10 | faceSaturation << 7 | faceLightness);
		}
		if (this.billboardFaces != null) {
			for (int billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
				@Pc(105) JavaBillboardFace javaBillboardFace = this.billboardFaces[billboardIndex];
				@Pc(110) JavaBillboardAttributes javaBillboardAttributes = this.billboardAttributes[billboardIndex];
				javaBillboardAttributes.diffuseColor = javaBillboardAttributes.diffuseColor & 0xFF000000 | ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(this.faceColour[javaBillboardFace.face] & 0xFFFF)] & 0xFFFFFF;
			}
		}
		if (this.lightingState == 2) {
			this.lightingState = 1;
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(IILclient!bca;ZI)Z")
	@Override
	public boolean picked(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) Matrix matrix, @OriginalArg(3) boolean quick, @OriginalArg(4) int sizeShift) {
		return this.pick(x, y, matrix, quick, sizeShift, -1);
	}

	@OriginalMember(owner = "client!jga", name = "ma", descriptor = "()I")
	@Override
	public int ma() {
		if (!this.boundsValid) {
			this.calculateBounds();
		}
		return this.sphereRadius;
	}

	@OriginalMember(owner = "client!jga", name = "b", descriptor = "(IILclient!bca;ZII)Z")
	private boolean pick(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) Matrix matrix, @OriginalArg(3) boolean quick, @OriginalArg(4) int sizeShift, @OriginalArg(5) int orthoDepth) {
		this.modelMatrix = (JavaMatrix) matrix;
		@Pc(7) JavaMatrix javaMatrix = this.toolkit.camera;
		@Pc(31) float tx = javaMatrix.tx + javaMatrix.e1_1 * this.modelMatrix.tx + javaMatrix.e1_2 * this.modelMatrix.ty + javaMatrix.e1_3 * this.modelMatrix.tz;
		@Pc(55) float ty = javaMatrix.ty + javaMatrix.e2_1 * this.modelMatrix.tx + javaMatrix.e2_2 * this.modelMatrix.ty + javaMatrix.e2_3 * this.modelMatrix.tz;
		@Pc(79) float tz = javaMatrix.tz + javaMatrix.e3_1 * this.modelMatrix.tx + javaMatrix.e3_2 * this.modelMatrix.ty + javaMatrix.e3_3 * this.modelMatrix.tz;
		@Pc(100) float m11 = javaMatrix.e1_1 * this.modelMatrix.e1_1 + javaMatrix.e1_2 * this.modelMatrix.e2_1 + javaMatrix.e1_3 * this.modelMatrix.e3_1;
		@Pc(121) float m12 = javaMatrix.e1_1 * this.modelMatrix.e1_2 + javaMatrix.e1_2 * this.modelMatrix.e2_2 + javaMatrix.e1_3 * this.modelMatrix.e3_2;
		@Pc(142) float m13 = javaMatrix.e1_1 * this.modelMatrix.e1_3 + javaMatrix.e1_2 * this.modelMatrix.e2_3 + javaMatrix.e1_3 * this.modelMatrix.e3_3;
		@Pc(163) float m21 = javaMatrix.e2_1 * this.modelMatrix.e1_1 + javaMatrix.e2_2 * this.modelMatrix.e2_1 + javaMatrix.e2_3 * this.modelMatrix.e3_1;
		@Pc(184) float m22 = javaMatrix.e2_1 * this.modelMatrix.e1_2 + javaMatrix.e2_2 * this.modelMatrix.e2_2 + javaMatrix.e2_3 * this.modelMatrix.e3_2;
		@Pc(205) float m23 = javaMatrix.e2_1 * this.modelMatrix.e1_3 + javaMatrix.e2_2 * this.modelMatrix.e2_3 + javaMatrix.e2_3 * this.modelMatrix.e3_3;
		@Pc(226) float m31 = javaMatrix.e3_1 * this.modelMatrix.e1_1 + javaMatrix.e3_2 * this.modelMatrix.e2_1 + javaMatrix.e3_3 * this.modelMatrix.e3_1;
		@Pc(247) float m32 = javaMatrix.e3_1 * this.modelMatrix.e1_2 + javaMatrix.e3_2 * this.modelMatrix.e2_2 + javaMatrix.e3_3 * this.modelMatrix.e3_2;
		@Pc(268) float m33 = javaMatrix.e3_1 * this.modelMatrix.e1_3 + javaMatrix.e3_2 * this.modelMatrix.e2_3 + javaMatrix.e3_3 * this.modelMatrix.e3_3;
		@Pc(270) boolean anyCornerVisible = false;
		@Pc(274) int projectionCenterX = this.toolkit.projectionCenterX;
		@Pc(278) int projectionCenterY = this.toolkit.lb;
		@Pc(282) int projectionScaleX = this.toolkit.projectionScaleX;
		@Pc(286) int projectionScaleY = this.toolkit.projectionScaleY;
		@Pc(288) int minScreenX = Integer.MAX_VALUE;
		@Pc(290) int maxScreenX = Integer.MIN_VALUE;
		@Pc(292) int minScreenY = Integer.MAX_VALUE;
		@Pc(294) int maxScreenY = Integer.MIN_VALUE;
		this.useThreadBuffers(Thread.currentThread());
		if (!this.boundsValid) {
			this.calculateBounds();
		}
		@Pc(310) int halfX = this.maxX - this.minX >> 1;
		@Pc(318) int halfY = this.maxY - this.minY >> 1;
		@Pc(326) int halfZ = this.maxZ - this.minZ >> 1;
		@Pc(331) int midX = this.minX + halfX;
		@Pc(336) int midY = this.minY + halfY;
		@Pc(341) int midZ = this.minZ + halfZ;
		@Pc(347) int lowX = midX - (halfX << sizeShift);
		@Pc(353) int lowY = midY - (halfY << sizeShift);
		@Pc(359) int lowZ = midZ - (halfZ << sizeShift);
		@Pc(365) int highX = midX + (halfX << sizeShift);
		@Pc(371) int highY = midY + (halfY << sizeShift);
		@Pc(377) int highZ = midZ + (halfZ << sizeShift);
		this.boundsCornerX[0] = lowX;
		this.boundsCornerY[0] = lowY;
		this.boundsCornerZ[0] = lowZ;
		this.boundsCornerX[1] = highX;
		this.boundsCornerY[1] = lowY;
		this.boundsCornerZ[1] = lowZ;
		this.boundsCornerX[2] = lowX;
		this.boundsCornerY[2] = highY;
		this.boundsCornerZ[2] = lowZ;
		this.boundsCornerX[3] = highX;
		this.boundsCornerY[3] = highY;
		this.boundsCornerZ[3] = lowZ;
		this.boundsCornerX[4] = lowX;
		this.boundsCornerY[4] = lowY;
		this.boundsCornerZ[4] = highZ;
		this.boundsCornerX[5] = highX;
		this.boundsCornerY[5] = lowY;
		this.boundsCornerZ[5] = highZ;
		this.boundsCornerX[6] = lowX;
		this.boundsCornerY[6] = highY;
		this.boundsCornerZ[6] = highZ;
		this.boundsCornerX[7] = highX;
		this.boundsCornerY[7] = highY;
		this.boundsCornerZ[7] = highZ;
		@Pc(532) float viewX;
		@Pc(549) float viewY;
		@Pc(566) float depth;
		@Pc(505) int px;
		@Pc(510) int py;
		@Pc(515) int pz;
		@Pc(588) int local588;
		@Pc(598) int local598;
		for (@Pc(499) int i = 0; i < 8; i++) {
			px = this.boundsCornerX[i];
			py = this.boundsCornerY[i];
			pz = this.boundsCornerZ[i];
			viewX = tx + m11 * (float) px + m12 * (float) py + m13 * (float) pz;
			viewY = ty + m21 * (float) px + m22 * (float) py + m23 * (float) pz;
			depth = tz + m31 * (float) px + m32 * (float) py + m33 * (float) pz;
			if (depth >= (float) this.toolkit.zNear) {
				if (orthoDepth > 0) {
					depth = orthoDepth;
				}
				int cornerScreenX = projectionCenterX + (int) (viewX * (float) projectionScaleX / depth);
				int cornerScreenY = projectionCenterY + (int) (viewY * (float) projectionScaleY / depth);
				if (cornerScreenX < minScreenX) {
					minScreenX = cornerScreenX;
				}
				if (cornerScreenX > maxScreenX) {
					maxScreenX = cornerScreenX;
				}
				if (cornerScreenY < minScreenY) {
					minScreenY = cornerScreenY;
				}
				if (cornerScreenY > maxScreenY) {
					maxScreenY = cornerScreenY;
				}
				anyCornerVisible = true;
			}
		}
		if (anyCornerVisible && x > minScreenX && x < maxScreenX && y > minScreenY && y < maxScreenY) {
			if (quick) {
				return true;
			}
			for (int vertexIndex = 0; vertexIndex < this.vertexCount; vertexIndex++) {
				px = this.vertexX[vertexIndex];
				py = this.vertexY[vertexIndex];
				pz = this.vertexZ[vertexIndex];
				viewX = tx + m11 * (float) px + m12 * (float) py + m13 * (float) pz;
				viewY = ty + m21 * (float) px + m22 * (float) py + m23 * (float) pz;
				depth = tz + m31 * (float) px + m32 * (float) py + m33 * (float) pz;
				if (depth >= (float) this.toolkit.zNear) {
					if (orthoDepth > 0) {
						depth = orthoDepth;
					}
					this.screenX[vertexIndex] = projectionCenterX + (int) (viewX * (float) projectionScaleX / depth);
					this.screenY[vertexIndex] = projectionCenterY + (int) (viewY * (float) projectionScaleY / depth);
				} else {
					this.screenX[vertexIndex] = -999999;
				}
			}
			for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
				if (this.screenX[this.faceA[faceIndex]] != -999999 && this.screenX[this.faceB[faceIndex]] != -999999 && this.screenX[this.faceC[faceIndex]] != -999999 && this.pointInFaceBounds(x, y, this.screenY[this.faceA[faceIndex]], this.screenY[this.faceB[faceIndex]], this.screenY[this.faceC[faceIndex]], this.screenX[this.faceA[faceIndex]], this.screenX[this.faceB[faceIndex]], this.screenX[this.faceC[faceIndex]])) {
					return true;
				}
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!jga", name = "e", descriptor = "(I)V")
	private void drawClippedTriangleRgb(@OriginalArg(0) int face) {
		@Pc(1) int count = 0;
		@Pc(5) int zNear = this.toolkit.zNear;
		@Pc(10) short a = this.faceA[face];
		@Pc(15) short b = this.faceB[face];
		@Pc(20) short c = this.faceC[face];
		@Pc(25) int depthA = this.cameraZ[a];
		@Pc(30) int depthB = this.cameraZ[b];
		@Pc(35) int depthC = this.cameraZ[c];
		if (this.faceAlpha == null) {
			this.rasterizer.alpha = 0;
		} else {
			this.rasterizer.alpha = this.faceAlpha[face] & 0xFF;
		}
		@Pc(96) int local96;
		@Pc(101) int local101;
		@Pc(108) int local108;
		@Pc(121) int local121;
		if (depthA >= zNear) {
			this.clippedX[0] = this.screenX[a];
			this.clippedY[0] = this.screenY[a];
			this.clippedZ[0] = this.screenZ[a];
			count++;
			this.clippedColour[0] = this.faceColourA[face] & 0xFFFF;
		} else {
			int viewX = this.cameraX[a];
			int viewY = this.cameraY[a];
			int hsl = this.faceColourA[face] & 0xFFFF;
			if (depthC >= zNear) {
				int lerpFactor = (zNear - depthA) * (65536 / (depthC - depthA));
				this.clippedX[0] = this.threadResource.rasterMinX + (viewX + ((this.cameraX[c] - viewX) * lerpFactor >> 16)) * this.toolkit.projectionScaleX / zNear;
				this.clippedY[0] = this.threadResource.rasterMinY + (viewY + ((this.cameraY[c] - viewY) * lerpFactor >> 16)) * this.toolkit.projectionScaleY / zNear;
				this.clippedZ[0] = zNear;
				count++;
				this.clippedColour[0] = hsl + (((this.faceColourC[face] & 0xFFFF) - hsl) * lerpFactor >> 16);
			}
			if (depthB >= zNear) {
				int lerpFactor = (zNear - depthA) * (65536 / (depthB - depthA));
				this.clippedX[count] = this.threadResource.rasterMinX + (viewX + ((this.cameraX[b] - viewX) * lerpFactor >> 16)) * this.toolkit.projectionScaleX / zNear;
				this.clippedY[count] = this.threadResource.rasterMinY + (viewY + ((this.cameraY[b] - viewY) * lerpFactor >> 16)) * this.toolkit.projectionScaleY / zNear;
				this.clippedZ[count] = zNear;
				this.clippedColour[count++] = hsl + (((this.faceColourB[face] & 0xFFFF) - hsl) * lerpFactor >> 16);
			}
		}
		if (depthB >= zNear) {
			this.clippedX[count] = this.screenX[b];
			this.clippedY[count] = this.screenY[b];
			this.clippedZ[count] = this.screenZ[b];
			this.clippedColour[count++] = this.faceColourB[face] & 0xFFFF;
		} else {
			int viewX = this.cameraX[b];
			int viewY = this.cameraY[b];
			int hsl = this.faceColourB[face] & 0xFFFF;
			if (depthA >= zNear) {
				int lerpFactor = (zNear - depthB) * (65536 / (depthA - depthB));
				this.clippedX[count] = this.threadResource.rasterMinX + (viewX + ((this.cameraX[a] - viewX) * lerpFactor >> 16)) * this.toolkit.projectionScaleX / zNear;
				this.clippedY[count] = this.threadResource.rasterMinY + (viewY + ((this.cameraY[a] - viewY) * lerpFactor >> 16)) * this.toolkit.projectionScaleY / zNear;
				this.clippedZ[count] = zNear;
				this.clippedColour[count++] = hsl + (((this.faceColourA[face] & 0xFFFF) - hsl) * lerpFactor >> 16);
			}
			if (depthC >= zNear) {
				int lerpFactor = (zNear - depthB) * (65536 / (depthC - depthB));
				this.clippedX[count] = this.threadResource.rasterMinX + (viewX + ((this.cameraX[c] - viewX) * lerpFactor >> 16)) * this.toolkit.projectionScaleX / zNear;
				this.clippedY[count] = this.threadResource.rasterMinY + (viewY + ((this.cameraY[c] - viewY) * lerpFactor >> 16)) * this.toolkit.projectionScaleY / zNear;
				this.clippedZ[count] = zNear;
				this.clippedColour[count++] = hsl + (((this.faceColourC[face] & 0xFFFF) - hsl) * lerpFactor >> 16);
			}
		}
		if (depthC >= zNear) {
			this.clippedX[count] = this.screenX[c];
			this.clippedY[count] = this.screenY[c];
			this.clippedZ[count] = this.screenZ[c];
			this.clippedColour[count++] = this.faceColourC[face] & 0xFFFF;
		} else {
			int viewX = this.cameraX[c];
			int viewY = this.cameraY[c];
			int hsl = this.faceColourC[face] & 0xFFFF;
			if (depthB >= zNear) {
				int lerpFactor = (zNear - depthC) * (65536 / (depthB - depthC));
				this.clippedX[count] = this.threadResource.rasterMinX + (viewX + ((this.cameraX[b] - viewX) * lerpFactor >> 16)) * this.toolkit.projectionScaleX / zNear;
				this.clippedY[count] = this.threadResource.rasterMinY + (viewY + ((this.cameraY[b] - viewY) * lerpFactor >> 16)) * this.toolkit.projectionScaleY / zNear;
				this.clippedZ[count] = zNear;
				this.clippedColour[count++] = hsl + (((this.faceColourB[face] & 0xFFFF) - hsl) * lerpFactor >> 16);
			}
			if (depthA >= zNear) {
				int lerpFactor = (zNear - depthC) * (65536 / (depthA - depthC));
				this.clippedX[count] = this.threadResource.rasterMinX + (viewX + ((this.cameraX[a] - viewX) * lerpFactor >> 16)) * this.toolkit.projectionScaleX / zNear;
				this.clippedY[count] = this.threadResource.rasterMinY + (viewY + ((this.cameraY[a] - viewY) * lerpFactor >> 16)) * this.toolkit.projectionScaleY / zNear;
				this.clippedZ[count] = zNear;
				this.clippedColour[count++] = hsl + (((this.faceColourA[face] & 0xFFFF) - hsl) * lerpFactor >> 16);
			}
		}
		int clippedX0 = this.clippedX[0];
		int clippedX1 = this.clippedX[1];
		int clippedX2 = this.clippedX[2];
		int clippedY0 = this.clippedY[0];
		@Pc(779) int clippedY1 = this.clippedY[1];
		@Pc(784) int clippedY2 = this.clippedY[2];
		depthA = this.clippedZ[0];
		depthB = this.clippedZ[1];
		depthC = this.clippedZ[2];
		this.rasterizer.clamp = false;
		@Pc(915) int local915;
		@Pc(938) int colour;
		if (count == 3) {
			if (clippedX0 < 0 || clippedX1 < 0 || clippedX2 < 0 || clippedX0 > this.threadResource.rasterWidth || clippedX1 > this.threadResource.rasterWidth || clippedX2 > this.threadResource.rasterWidth) {
				this.rasterizer.clamp = true;
			}
			if (this.faceTextures != null && this.faceTextures[face] != -1) {
				int alphaBits = -16777216;
				if (this.faceAlpha != null) {
					alphaBits = 255 - (this.faceAlpha[face] & 0xFF) << 24;
				}
				colour = alphaBits | this.faceColourA[face] & 0xFFFFFF;
				if (this.faceColourC[face] == -1) {
					this.rasterizer.renderTexturedTriangleRgb((float) clippedY0, (float) clippedY1, (float) clippedY2, (float) clippedX0, (float) clippedX1, (float) clippedX2, (float) depthA, (float) depthB, (float) depthC, this.texCoordU[face][0], this.texCoordU[face][1], this.texCoordU[face][2], this.texCoordV[face][0], this.texCoordV[face][1], this.texCoordV[face][2], colour, colour, colour, this.threadResource.fogColour, 0, 0, 0, this.faceTextures[face]);
				} else {
					this.rasterizer.renderTexturedTriangleRgb((float) clippedY0, (float) clippedY1, (float) clippedY2, (float) clippedX0, (float) clippedX1, (float) clippedX2, (float) depthA, (float) depthB, (float) depthC, this.texCoordU[face][0], this.texCoordU[face][1], this.texCoordU[face][2], this.texCoordV[face][0], this.texCoordV[face][1], this.texCoordV[face][2], colour, colour, colour, this.threadResource.fogColour, 0, 0, 0, this.faceTextures[face]);
				}
			} else if (this.faceColourC[face] == -1) {
				this.rasterizer.renderFlatTriangleRgb((float) clippedY0, (float) clippedY1, (float) clippedY2, (float) clippedX0, (float) clippedX1, (float) clippedX2, (float) depthA, (float) depthB, (float) depthC, ColorUtil.hsvToRgbTable[this.faceColourA[face] & 0xFFFF]);
			} else {
				this.rasterizer.renderTriangleHslRgb((float) clippedY0, (float) clippedY1, (float) clippedY2, (float) clippedX0, (float) clippedX1, (float) clippedX2, (float) depthA, (float) depthB, (float) depthC, (float) this.clippedColour[0], (float) this.clippedColour[1], (float) this.clippedColour[2]);
			}
		}
		if (count != 4) {
			return;
		}
		if (clippedX0 < 0 || clippedX1 < 0 || clippedX2 < 0 || clippedX0 > this.threadResource.rasterWidth || clippedX1 > this.threadResource.rasterWidth || clippedX2 > this.threadResource.rasterWidth || this.clippedX[3] < 0 || this.clippedX[3] > this.threadResource.rasterWidth) {
			this.rasterizer.clamp = true;
		}
		if (this.faceTextures == null || this.faceTextures[face] == -1) {
			if (this.faceColourC[face] == -1) {
				int rgb = ColorUtil.hsvToRgbTable[this.faceColourA[face] & 0xFFFF];
				this.rasterizer.renderFlatTriangleRgb((float) clippedY0, (float) clippedY1, (float) clippedY2, (float) clippedX0, (float) clippedX1, (float) clippedX2, (float) depthA, (float) depthB, (float) depthC, rgb);
				this.rasterizer.renderFlatTriangleRgb((float) clippedY0, (float) clippedY2, (float) this.clippedY[3], (float) clippedX0, (float) clippedX2, (float) this.clippedX[3], (float) depthA, (float) depthB, (float) this.clippedZ[3], rgb);
				return;
			} else {
				this.rasterizer.renderTriangleHslRgb((float) clippedY0, (float) clippedY1, (float) clippedY2, (float) clippedX0, (float) clippedX1, (float) clippedX2, (float) depthA, (float) depthB, (float) depthC, (float) this.clippedColour[0], (float) this.clippedColour[1], (float) this.clippedColour[2]);
				this.rasterizer.renderTriangleHslRgb((float) clippedY0, (float) clippedY2, (float) this.clippedY[3], (float) clippedX0, (float) clippedX2, (float) this.clippedX[3], (float) depthA, (float) depthB, (float) this.clippedZ[3], (float) this.clippedColour[0], (float) this.clippedColour[2], (float) this.clippedColour[3]);
				return;
			}
		}
		int alphaBits = -16777216;
		if (this.faceAlpha != null) {
			alphaBits = 255 - (this.faceAlpha[face] & 0xFF) << 24;
		}
		colour = alphaBits | this.faceColourA[face] & 0xFFFFFF;
		if (this.faceColourC[face] == -1) {
			this.rasterizer.renderTexturedTriangleRgb((float) clippedY0, (float) clippedY1, (float) clippedY2, (float) clippedX0, (float) clippedX1, (float) clippedX2, (float) depthA, (float) depthB, (float) depthC, this.texCoordU[face][0], this.texCoordU[face][1], this.texCoordU[face][2], this.texCoordV[face][0], this.texCoordV[face][1], this.texCoordV[face][2], colour, colour, colour, this.threadResource.fogColour, 0, 0, 0, this.faceTextures[face]);
			this.rasterizer.renderTexturedTriangleRgb((float) clippedY0, (float) clippedY2, (float) this.clippedY[3], (float) clippedX0, (float) clippedX2, (float) this.clippedX[3], (float) depthA, (float) depthC, (float) this.clippedZ[3], this.texCoordU[face][0], this.texCoordU[face][1], this.texCoordU[face][2], this.texCoordV[face][0], this.texCoordV[face][1], this.texCoordV[face][2], colour, colour, colour, this.threadResource.fogColour, 0, 0, 0, this.faceTextures[face]);
			return;
		}
		this.rasterizer.renderTexturedTriangleRgb((float) clippedY0, (float) clippedY1, (float) clippedY2, (float) clippedX0, (float) clippedX1, (float) clippedX2, (float) depthA, (float) depthB, (float) depthC, this.texCoordU[face][0], this.texCoordU[face][1], this.texCoordU[face][2], this.texCoordV[face][0], this.texCoordV[face][1], this.texCoordV[face][2], colour, colour, colour, this.threadResource.fogColour, 0, 0, 0, this.faceTextures[face]);
		this.rasterizer.renderTexturedTriangleRgb((float) clippedY0, (float) clippedY2, (float) this.clippedY[3], (float) clippedX0, (float) clippedX2, (float) this.clippedX[3], (float) depthA, (float) depthC, (float) this.clippedZ[3], this.texCoordU[face][0], this.texCoordU[face][1], this.texCoordU[face][2], this.texCoordV[face][0], this.texCoordV[face][1], this.texCoordV[face][2], colour, colour, colour, this.threadResource.fogColour, 0, 0, 0, this.faceTextures[face]);
	}

	@OriginalMember(owner = "client!jga", name = "m", descriptor = "()V")
	private void relight() {
		if (this.lightingState == 0) {
			this.applyLighting(false);
		} else if (this.toolkit.threadCount > 1) {
			synchronized (this) {
				this.relightColours();
			}
		} else {
			this.relightColours();
		}
	}

	@OriginalMember(owner = "client!jga", name = "b", descriptor = "(Ljava/lang/Thread;)V")
	private void useThreadCopyCache(@OriginalArg(0) Thread thread) {
		@Pc(4) JavaThreadResource resource = this.toolkit.threadResource(thread);
		if (resource == this.copyThreadResource) {
			return;
		}
		this.copyThreadResource = resource;
		this.copyDstModel1 = this.copyThreadResource.copyDstModel1;
		this.copyDstModel2 = this.copyThreadResource.copyDstModel2;
		this.copyDstModel3 = this.copyThreadResource.copyDstModel3;
		this.copyDstModel4 = this.copyThreadResource.copyDstModel4;
		this.copyDstModel5 = this.copyThreadResource.copyDstModel5;
		this.copySrcModel1 = this.copyThreadResource.copySrcModel1;
		this.copySrcModel2 = this.copyThreadResource.copySrcModel2;
		this.copySrcModel3 = this.copyThreadResource.copySrcModel3;
		this.copySrcModel4 = this.copyThreadResource.copySrcModel4;
		this.copySrcModel5 = this.copyThreadResource.copySrcModel5;
	}

	@OriginalMember(owner = "client!jga", name = "LA", descriptor = "(I)V")
	@Override
	public void LA(@OriginalArg(0) int contrast) {
		if ((this.functionMask & 0x2000) != 8192) {
			throw new IllegalStateException();
		}
		this.contrast = contrast;
		this.lightingState = 0;
	}

	@OriginalMember(owner = "client!jga", name = "VA", descriptor = "(I)V")
	@Override
	public void VA(@OriginalArg(0) int angle) {
		if ((this.functionMask & 0x3) != 3) {
			throw new IllegalStateException();
		}
		@Pc(13) int sin = TextureCombineSource.SINE[angle];
		@Pc(17) int cos = TextureCombineSource.COSINE[angle];
		synchronized (this) {
			for (@Pc(23) int i = 0; i < this.vertexCount; i++) {
				@Pc(40) int newX = this.vertexY[i] * sin + this.vertexX[i] * cos >> 14;
				this.vertexY[i] = this.vertexY[i] * cos - this.vertexX[i] * sin >> 14;
				this.vertexX[i] = newX;
			}
			this.invalidateNormals();
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(I)V")
	@Override
	public void a(@OriginalArg(0) int angle) {
		if ((this.functionMask & 0x5) != 5) {
			throw new IllegalStateException();
		} else if (angle == 4096) {
			this.rotate90();
		} else if (angle == 8192) {
			this.rotate180();
		} else if (angle == 12288) {
			this.rotate270();
		} else {
			@Pc(31) int sin = TextureCombineSource.SINE[angle];
			@Pc(35) int cos = TextureCombineSource.COSINE[angle];
			synchronized (this) {
				for (@Pc(41) int i = 0; i < this.vertexCount; i++) {
					@Pc(58) int newX = this.vertexZ[i] * sin + this.vertexX[i] * cos >> 14;
					this.vertexZ[i] = this.vertexZ[i] * cos - this.vertexX[i] * sin >> 14;
					this.vertexX[i] = newX;
				}
				this.invalidateNormals();
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "aa", descriptor = "(SS)V")
	@Override
	public void aa(@OriginalArg(0) short src, @OriginalArg(1) short dst) {
		if (this.faceTextures == null) {
			return;
		}
		if (!this.movingTextures && dst >= 0) {
			@Pc(17) MaterialType textureMetrics = this.toolkit.materialProvider.getMetrics(dst & 0xFFFF);
			if (textureMetrics.speedU != 0 || textureMetrics.speedV != 0) {
				this.movingTextures = true;
			}
		}
		for (@Pc(28) int i = 0; i < this.faceCount; i++) {
			if (this.faceTextures[i] == src) {
				this.faceTextures[i] = dst;
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Lclient!ka;IIIZ)V")
	@Override
	public void shareLight(@OriginalArg(0) Model other, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int z, @OriginalArg(4) boolean renderTypeAdjustment) {
		@Pc(2) JavaModel javaModel = (JavaModel) other;
		if ((this.functionMask & 0x10000) != 65536) {
			throw new IllegalStateException("");
		} else if ((javaModel.functionMask & 0x10000) == 65536) {
			this.useThreadBuffers(Thread.currentThread());
			this.calculateBounds();
			this.ensureNormals();
			javaModel.calculateBounds();
			javaModel.ensureNormals();
			PureJavaModel.renderCycle++;
			@Pc(41) int sharedVertexCount = 0;
			@Pc(44) int[] vertexX = javaModel.vertexX;
			@Pc(47) int maxVertex = javaModel.maxVertex;
			@Pc(65) int local65;
			for (@Pc(49) int i = 0; i < this.maxVertex; i++) {
				@Pc(55) VertexNormal vertexNormal = this.lb[i];
				if (vertexNormal.magnitude != 0) {
					int dy = this.vertexY[i] - y;
					if (dy >= javaModel.minY && dy <= javaModel.maxY) {
						@Pc(81) int dx = this.vertexX[i] - x;
						if (dx >= javaModel.minX && dx <= javaModel.maxX) {
							@Pc(97) int dz = this.vertexZ[i] - z;
							if (dz >= javaModel.minZ && dz <= javaModel.maxZ) {
								for (@Pc(108) int j = 0; j < maxVertex; j++) {
									@Pc(114) VertexNormal vertexNormal2 = javaModel.lb[j];
									if (dx == vertexX[j] && dz == javaModel.vertexZ[j] && dy == javaModel.vertexY[j] && vertexNormal2.magnitude != 0) {
										if (this.vertexNormalsOffset == null) {
											this.vertexNormalsOffset = new VertexNormal[this.maxVertex];
										}
										if (javaModel.vertexNormalsOffset == null) {
											javaModel.vertexNormalsOffset = new VertexNormal[maxVertex];
										}
										@Pc(154) VertexNormal vertexNormal3 = this.vertexNormalsOffset[i];
										if (vertexNormal3 == null) {
											vertexNormal3 = this.vertexNormalsOffset[i] = new VertexNormal(vertexNormal);
										}
										@Pc(171) VertexNormal vertexNormal4 = javaModel.vertexNormalsOffset[j];
										if (vertexNormal4 == null) {
											vertexNormal4 = javaModel.vertexNormalsOffset[j] = new VertexNormal(vertexNormal2);
										}
										vertexNormal3.x += vertexNormal2.x;
										vertexNormal3.y += vertexNormal2.y;
										vertexNormal3.z += vertexNormal2.z;
										vertexNormal3.magnitude += vertexNormal2.magnitude;
										vertexNormal4.x += vertexNormal.x;
										vertexNormal4.y += vertexNormal.y;
										vertexNormal4.z += vertexNormal.z;
										vertexNormal4.magnitude += vertexNormal.magnitude;
										sharedVertexCount++;
										this.mergeStamps[i] = PureJavaModel.renderCycle;
										this.otherMergeStamps[j] = PureJavaModel.renderCycle;
									}
								}
							}
						}
					}
				}
			}
			if (sharedVertexCount >= 3 && renderTypeAdjustment) {
				for (@Pc(267) int k = 0; k < this.faceCount; k++) {
					if (this.mergeStamps[this.faceA[k]] == PureJavaModel.renderCycle && this.mergeStamps[this.faceB[k]] == PureJavaModel.renderCycle && this.mergeStamps[this.faceC[k]] == PureJavaModel.renderCycle) {
						if (this.shadingType == null) {
							this.shadingType = new byte[this.faceCount];
						}
						this.shadingType[k] = 2;
					}
				}
				for (int otherFaceIndex = 0; otherFaceIndex < javaModel.faceCount; otherFaceIndex++) {
					if (this.otherMergeStamps[javaModel.faceA[otherFaceIndex]] == PureJavaModel.renderCycle && this.otherMergeStamps[javaModel.faceB[otherFaceIndex]] == PureJavaModel.renderCycle && this.otherMergeStamps[javaModel.faceC[otherFaceIndex]] == PureJavaModel.renderCycle) {
						if (javaModel.shadingType == null) {
							javaModel.shadingType = new byte[javaModel.faceCount];
						}
						javaModel.shadingType[otherFaceIndex] = 2;
					}
				}
			}
		} else {
			throw new IllegalStateException("");
		}
	}

	@OriginalMember(owner = "client!jga", name = "n", descriptor = "()V")
	private void calculateNormals() {
		this.lb = new VertexNormal[this.maxVertex];
		for (@Pc(6) int i = 0; i < this.maxVertex; i++) {
			this.lb[i] = new VertexNormal();
		}
		for (@Pc(21) int j = 0; j < this.faceCount; j++) {
			@Pc(27) short a = this.faceA[j];
			@Pc(32) short b = this.faceB[j];
			@Pc(37) short c = this.faceC[j];
			@Pc(47) int dxAB = this.vertexX[b] - this.vertexX[a];
			@Pc(57) int dyAB = this.vertexY[b] - this.vertexY[a];
			@Pc(67) int dzAB = this.vertexZ[b] - this.vertexZ[a];
			@Pc(77) int dxAC = this.vertexX[c] - this.vertexX[a];
			@Pc(87) int dyAC = this.vertexY[c] - this.vertexY[a];
			@Pc(97) int dzAC = this.vertexZ[c] - this.vertexZ[a];
			@Pc(105) int nx = dyAB * dzAC - dyAC * dzAB;
			@Pc(113) int ny = dzAB * dxAC - dzAC * dxAB;
			@Pc(121) int nz;
			for (nz = dxAB * dyAC - dxAC * dyAB; nx > 8192 || ny > 8192 || nz > 8192 || nx < -8192 || ny < -8192 || nz < -8192; nz >>= 0x1) {
				nx >>= 0x1;
				ny >>= 0x1;
			}
			@Pc(167) int length = (int) Math.sqrt((double) (nx * nx + ny * ny + nz * nz));
			if (length <= 0) {
				length = 1;
			}
			nx = nx * 256 / length;
			ny = ny * 256 / length;
			nz = nz * 256 / length;
			@Pc(194) byte shading;
			if (this.shadingType == null) {
				shading = 0;
			} else {
				shading = this.shadingType[j];
			}
			if (shading == 0) {
				@Pc(207) VertexNormal normalA = this.lb[a];
				normalA.x += nx;
				normalA.y += ny;
				normalA.z += nz;
				normalA.magnitude++;
				@Pc(236) VertexNormal normalB = this.lb[b];
				normalB.x += nx;
				normalB.y += ny;
				normalB.z += nz;
				normalB.magnitude++;
				@Pc(265) VertexNormal normalC = this.lb[c];
				normalC.x += nx;
				normalC.y += ny;
				normalC.z += nz;
				normalC.magnitude++;
			} else if (shading == 1) {
				if (this.faceNormals == null) {
					this.faceNormals = new FaceNormal[this.faceCount];
				}
				@Pc(310) FaceNormal faceNormal = this.faceNormals[j] = new FaceNormal();
				faceNormal.x = nx;
				faceNormal.y = ny;
				faceNormal.z = nz;
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "H", descriptor = "(III)V")
	@Override
	public void H(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z) {
		if (x != 0 && (this.functionMask & 0x1) != 1) {
			throw new IllegalStateException();
		} else if (y != 0 && (this.functionMask & 0x2) != 2) {
			throw new IllegalStateException();
		} else if (z == 0 || (this.functionMask & 0x4) == 4) {
			synchronized (this) {
				for (@Pc(41) int i = 0; i < this.vertexCount; i++) {
					this.vertexX[i] += x;
					this.vertexY[i] += y;
					this.vertexZ[i] += z;
				}
			}
		} else {
			throw new IllegalStateException();
		}
	}

	@OriginalMember(owner = "client!jga", name = "v", descriptor = "()V")
	@Override
	public void v() {
		if ((this.functionMask & 0x10) != 16) {
			throw new IllegalStateException();
		}
		synchronized (this) {
			for (@Pc(15) int i = 0; i < this.vertexCount; i++) {
				this.vertexZ[i] = -this.vertexZ[i];
			}
			@Pc(35) int index;
			if (this.lb != null) {
				for (index = 0; index < this.maxVertex; index++) {
					if (this.lb[index] != null) {
						this.lb[index].z = -this.lb[index].z;
					}
				}
			}
			if (this.vertexNormalsOffset != null) {
				for (index = 0; index < this.maxVertex; index++) {
					if (this.vertexNormalsOffset[index] != null) {
						this.vertexNormalsOffset[index].z = -this.vertexNormalsOffset[index].z;
					}
				}
			}
			if (this.faceNormals != null) {
				for (index = 0; index < this.faceCount; index++) {
					if (this.faceNormals[index] != null) {
						this.faceNormals[index].z = -this.faceNormals[index].z;
					}
				}
			}
			@Pc(114) short[] oldFaceA = this.faceA;
			this.faceA = this.faceC;
			this.faceC = oldFaceA;
			if (this.texCoordU != null) {
				for (@Pc(126) int j = 0; j < this.faceCount; j++) {
					@Pc(139) float swap;
					if (this.texCoordU[j] != null) {
						swap = this.texCoordU[j][0];
						this.texCoordU[j][0] = this.texCoordU[j][2];
						this.texCoordU[j][2] = swap;
					}
					if (this.texCoordV[j] != null) {
						swap = this.texCoordV[j][0];
						this.texCoordV[j][0] = this.texCoordV[j][2];
						this.texCoordV[j][2] = swap;
					}
				}
			}
			this.boundsValid = false;
			this.lightingState = 0;
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(ZZII)V")
	private void drawFacesRgb(@OriginalArg(0) boolean nearClipped, @OriginalArg(1) boolean fogged, @OriginalArg(2) int minDepth, @OriginalArg(3) int depthRange) {
		@Pc(4) int i;
		if (this.billboardFaces != null) {
			i = 0;
			while (i < this.billboardCount) {
				@Pc(10) JavaBillboardFace billboard = this.billboardFaces[i];
				this.faceBillboard[billboard.face] = i++;
			}
		}
		if (!this.transparent && this.billboardFaces == null) {
			for (i = 0; i < this.faceCount; i++) {
				this.drawFaceRgb(i, nearClipped, fogged);
			}
		} else if ((this.functionMask & 0x100) == 0 && this.faceIndices != null) {
			for (i = 0; i < this.faceCount; i++) {
				@Pc(43) short face = this.faceIndices[i];
				this.drawFaceRgb(face, nearClipped, fogged);
			}
		} else {
			for (i = 0; i < this.faceCount; i++) {
				if (!this.isTranslucent(i) && !this.hasBillboard(i)) {
					this.drawFaceRgb(i, nearClipped, fogged);
				}
			}
			@Pc(81) int local81;
			if (this.facePriority == null) {
				for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
					if (this.isTranslucent(faceIndex) || this.hasBillboard(faceIndex)) {
						this.drawFaceRgb(faceIndex, nearClipped, fogged);
					}
				}
			} else {
				for (int priority = 0; priority < 12; priority++) {
					for (@Pc(106) int face = 0; face < this.faceCount; face++) {
						if (this.facePriority[face] == priority && (this.isTranslucent(face) || this.hasBillboard(face))) {
							this.drawFaceRgb(face, nearClipped, fogged);
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "o", descriptor = "()V")
	private void rotate180() {
		synchronized (this) {
			for (@Pc(5) int i = 0; i < this.vertexCount; i++) {
				this.vertexX[i] = -this.vertexX[i];
				this.vertexZ[i] = -this.vertexZ[i];
			}
			this.invalidateNormals();
		}
	}

	@OriginalMember(owner = "client!jga", name = "ia", descriptor = "(SS)V")
	@Override
	public void ia(@OriginalArg(0) short src, @OriginalArg(1) short dst) {
		for (@Pc(1) int i = 0; i < this.faceCount; i++) {
			if (this.faceColour[i] == src) {
				this.faceColour[i] = dst;
			}
		}
		if (this.billboardFaces != null) {
			for (@Pc(23) int j = 0; j < this.billboardCount; j++) {
				@Pc(29) JavaBillboardFace javaBillboardFace = this.billboardFaces[j];
				@Pc(34) JavaBillboardAttributes javaBillboardAttributes = this.billboardAttributes[j];
				javaBillboardAttributes.diffuseColor = javaBillboardAttributes.diffuseColor & 0xFF000000 | ColorUtil.hsvToRgbTable[ColorUtil.hslToHsv(this.faceColour[javaBillboardFace.face]) & 0xFFFF] & 0xFFFFFF;
			}
		}
		if (this.lightingState == 2) {
			this.lightingState = 1;
		}
	}

	@OriginalMember(owner = "client!jga", name = "p", descriptor = "()V")
	private void rotate180WithNormals() {
		synchronized (this) {
			for (@Pc(5) int i = 0; i < this.maxVertex; i++) {
				this.vertexX[i] = -this.vertexX[i];
				this.vertexZ[i] = -this.vertexZ[i];
				if (this.lb[i] != null) {
					this.lb[i].x = -this.lb[i].x;
					this.lb[i].z = -this.lb[i].z;
				}
			}
			@Pc(61) int maxVertex;
			if (this.faceNormals != null) {
				for (maxVertex = 0; maxVertex < this.faceCount; maxVertex++) {
					if (this.faceNormals[maxVertex] != null) {
						this.faceNormals[maxVertex].x = -this.faceNormals[maxVertex].x;
						this.faceNormals[maxVertex].z = -this.faceNormals[maxVertex].z;
					}
				}
			}
			for (maxVertex = this.maxVertex; maxVertex < this.vertexCount; maxVertex++) {
				this.vertexX[maxVertex] = -this.vertexX[maxVertex];
				this.vertexZ[maxVertex] = -this.vertexZ[maxVertex];
			}
			this.lightingState = 0;
			this.boundsValid = false;
		}
	}

	@OriginalMember(owner = "client!jga", name = "q", descriptor = "()V")
	private void rotate90WithNormals() {
		synchronized (this) {
			@Pc(11) int local11;
			for (@Pc(5) int i = 0; i < this.maxVertex; i++) {
				int swapX = this.vertexX[i];
				this.vertexX[i] = this.vertexZ[i];
				this.vertexZ[i] = -swapX;
				if (this.lb[i] != null) {
					swapX = this.lb[i].x;
					this.lb[i].x = this.lb[i].z;
					this.lb[i].z = -swapX;
				}
			}
			@Pc(75) int oldX;
			if (this.faceNormals != null) {
				for (int faceIndex = 0; faceIndex < this.faceCount; faceIndex++) {
					if (this.faceNormals[faceIndex] != null) {
						oldX = this.faceNormals[faceIndex].x;
						this.faceNormals[faceIndex].x = this.faceNormals[faceIndex].z;
						this.faceNormals[faceIndex].z = -oldX;
					}
				}
			}
			for (int vertexIndex = this.maxVertex; vertexIndex < this.vertexCount; vertexIndex++) {
				oldX = this.vertexX[vertexIndex];
				this.vertexX[vertexIndex] = this.vertexZ[vertexIndex];
				this.vertexZ[vertexIndex] = -oldX;
			}
			this.lightingState = 0;
			this.boundsValid = false;
		}
	}

	@OriginalMember(owner = "client!jga", name = "b", descriptor = "(Lclient!bca;Lclient!iw;II)V")
	private void draw(@OriginalArg(0) Matrix matrix, @OriginalArg(1) BoundingBoxPolygon cylinder, @OriginalArg(2) int orthoDepth, @OriginalArg(3) int flags) {
		if (this.maxVertex < 1) {
			return;
		}
		this.modelMatrix = (JavaMatrix) matrix;
		@Pc(12) JavaMatrix javaMatrix = this.toolkit.camera;
		if (!this.boundsValid) {
			this.calculateBounds();
		}
		@Pc(19) boolean identityRotation = false;
		if (this.modelMatrix.e1_1 == 16384.0F && this.modelMatrix.e1_2 == 0.0F && this.modelMatrix.e1_3 == 0.0F && this.modelMatrix.e2_1 == 0.0F && this.modelMatrix.e2_2 == 16384.0F && this.modelMatrix.e2_3 == 0.0F && this.modelMatrix.e3_1 == 0.0F && this.modelMatrix.e3_2 == 0.0F && this.modelMatrix.e3_3 == 16384.0F) {
			identityRotation = true;
		}
		@Pc(99) float tz = javaMatrix.tz + javaMatrix.e3_1 * this.modelMatrix.tx + javaMatrix.e3_2 * this.modelMatrix.ty + javaMatrix.e3_3 * this.modelMatrix.tz;
		@Pc(125) float m32 = identityRotation ? javaMatrix.e3_2 : javaMatrix.e3_1 * this.modelMatrix.e1_2 + javaMatrix.e3_2 * this.modelMatrix.e2_2 + javaMatrix.e3_3 * this.modelMatrix.e3_2;
		@Pc(134) int depthAtMinY = (int) (tz + (float) this.minY * m32);
		@Pc(143) int depthAtMaxY = (int) (tz + (float) this.maxY * m32);
		@Pc(151) int near;
		@Pc(156) int far;
		if (depthAtMinY > depthAtMaxY) {
			near = depthAtMaxY - this.radius;
			far = depthAtMinY + this.radius;
		} else {
			near = depthAtMinY - this.radius;
			far = depthAtMaxY + this.radius;
		}
		if (near >= this.toolkit.zFar || far <= this.toolkit.zNear) {
			return;
		}
		@Pc(203) float tx = javaMatrix.tx + javaMatrix.e1_1 * this.modelMatrix.tx + javaMatrix.e1_2 * this.modelMatrix.ty + javaMatrix.e1_3 * this.modelMatrix.tz;
		@Pc(229) float m12 = identityRotation ? javaMatrix.e1_2 : javaMatrix.e1_1 * this.modelMatrix.e1_2 + javaMatrix.e1_2 * this.modelMatrix.e2_2 + javaMatrix.e1_3 * this.modelMatrix.e3_2;
		@Pc(238) int xAtMinY = (int) (tx + (float) this.minY * m12);
		@Pc(247) int xAtMaxY = (int) (tx + (float) this.maxY * m12);
		@Pc(259) int left;
		@Pc(268) int right;
		if (xAtMinY > xAtMaxY) {
			left = (xAtMaxY - this.radius) * this.toolkit.projectionScaleX;
			right = (xAtMinY + this.radius) * this.toolkit.projectionScaleX;
		} else {
			left = (xAtMinY - this.radius) * this.toolkit.projectionScaleX;
			right = (xAtMaxY + this.radius) * this.toolkit.projectionScaleX;
		}
		if (orthoDepth == -1) {
			if (left / far >= this.toolkit.viewX2) {
				return;
			}
			if (right / far <= this.toolkit.viewX1) {
				return;
			}
		} else if (left / orthoDepth >= this.toolkit.viewX2) {
			return;
		} else if (right / orthoDepth <= this.toolkit.viewX1) {
			return;
		}
		@Pc(346) float ty = javaMatrix.ty + javaMatrix.e2_1 * this.modelMatrix.tx + javaMatrix.e2_2 * this.modelMatrix.ty + javaMatrix.e2_3 * this.modelMatrix.tz;
		@Pc(372) float m22 = identityRotation ? javaMatrix.e2_2 : javaMatrix.e2_1 * this.modelMatrix.e1_2 + javaMatrix.e2_2 * this.modelMatrix.e2_2 + javaMatrix.e2_3 * this.modelMatrix.e3_2;
		@Pc(381) int yAtMinY = (int) (ty + (float) this.minY * m22);
		@Pc(390) int yAtMaxY = (int) (ty + (float) this.maxY * m22);
		@Pc(402) int top;
		@Pc(411) int bottom;
		if (yAtMinY > yAtMaxY) {
			top = (yAtMaxY - this.radius) * this.toolkit.projectionScaleY;
			bottom = (yAtMinY + this.radius) * this.toolkit.projectionScaleY;
		} else {
			top = (yAtMinY - this.radius) * this.toolkit.projectionScaleY;
			bottom = (yAtMaxY + this.radius) * this.toolkit.projectionScaleY;
		}
		if (orthoDepth == -1) {
			if (top / far >= this.toolkit.viewY2) {
				return;
			}
			if (bottom / far <= this.toolkit.viewY1) {
				return;
			}
		} else if (top / orthoDepth >= this.toolkit.viewY2) {
			return;
		} else if (bottom / orthoDepth <= this.toolkit.viewY1) {
			return;
		}
		@Pc(470) float m11;
		@Pc(473) float m21;
		@Pc(476) float m31;
		@Pc(479) float m13;
		@Pc(482) float m23;
		@Pc(485) float m33;
		if (identityRotation) {
			m11 = javaMatrix.e1_1;
			m21 = javaMatrix.e2_1;
			m31 = javaMatrix.e3_1;
			m13 = javaMatrix.e1_3;
			m23 = javaMatrix.e2_3;
			m33 = javaMatrix.e3_3;
		} else {
			m11 = javaMatrix.e1_1 * this.modelMatrix.e1_1 + javaMatrix.e1_2 * this.modelMatrix.e2_1 + javaMatrix.e1_3 * this.modelMatrix.e3_1;
			m21 = javaMatrix.e2_1 * this.modelMatrix.e1_1 + javaMatrix.e2_2 * this.modelMatrix.e2_1 + javaMatrix.e2_3 * this.modelMatrix.e3_1;
			m31 = javaMatrix.e3_1 * this.modelMatrix.e1_1 + javaMatrix.e3_2 * this.modelMatrix.e2_1 + javaMatrix.e3_3 * this.modelMatrix.e3_1;
			m13 = javaMatrix.e1_1 * this.modelMatrix.e1_3 + javaMatrix.e1_2 * this.modelMatrix.e2_3 + javaMatrix.e1_3 * this.modelMatrix.e3_3;
			m23 = javaMatrix.e2_1 * this.modelMatrix.e1_3 + javaMatrix.e2_2 * this.modelMatrix.e2_3 + javaMatrix.e2_3 * this.modelMatrix.e3_3;
			m33 = javaMatrix.e3_1 * this.modelMatrix.e1_3 + javaMatrix.e3_2 * this.modelMatrix.e2_3 + javaMatrix.e3_3 * this.modelMatrix.e3_3;
		}
		if (this.toolkit.threadCount > 1) {
			synchronized (this) {
				while (this.rendering) {
					try {
						this.wait();
					} catch (@Pc(626) InterruptedException ex) {
					}
				}
				this.rendering = true;
			}
		}
		this.useThreadBuffers(Thread.currentThread());
		if ((flags & 0x2) == 0) {
			this.rasterizer.setWireframe(false);
		} else {
			this.rasterizer.setWireframe(true);
		}
		@Pc(657) boolean nearClipped = false;
		@Pc(666) boolean nearPlaneCrossed = near <= this.toolkit.zNear;
		@Pc(678) boolean storeCameraCoords = nearPlaneCrossed || this.emitters != null || this.effectors != null;
		this.threadResource.rasterWidth = this.rasterizer.width;
		this.threadResource.rasterMinX = this.rasterizer.minX;
		this.threadResource.rasterMinY = this.rasterizer.minY;
		@Pc(700) int projectionScaleX = this.toolkit.projectionScaleX;
		@Pc(704) int projectionScaleY = this.toolkit.projectionScaleY;
		@Pc(708) int zNear = this.toolkit.zNear;
		@Pc(746) float viewX;
		@Pc(763) float viewY;
		@Pc(780) float depth;
		@Pc(719) int px;
		@Pc(724) int py;
		@Pc(729) int pz;
		@Pc(713) int i;
		@Pc(892) int billboardIndex;
		@Pc(898) JavaBillboardFace javaBillboardFace;
		@Pc(903) JavaBillboardAttributes javaBillboardAttributes;
		@Pc(909) short a;
		@Pc(915) short b;
		@Pc(921) short c;
		if (orthoDepth == -1) {
			for (i = 0; i < this.vertexCount; i++) {
				px = this.vertexX[i];
				py = this.vertexY[i];
				pz = this.vertexZ[i];
				viewX = tx + m11 * (float) px + m12 * (float) py + m13 * (float) pz;
				viewY = ty + m21 * (float) px + m22 * (float) py + m23 * (float) pz;
				depth = tz + m31 * (float) px + m32 * (float) py + m33 * (float) pz;
				this.screenZ[i] = (int) depth;
				if (depth >= (float) zNear) {
					this.screenX[i] = this.threadResource.rasterMinX + (int) (viewX * (float) projectionScaleX / depth);
					this.screenY[i] = this.threadResource.rasterMinY + (int) (viewY * (float) projectionScaleY / depth);
				} else {
					this.screenX[i] = -5000;
					nearClipped = true;
				}
				if (storeCameraCoords) {
					this.cameraX[i] = (int) viewX;
					this.cameraY[i] = (int) viewY;
					this.cameraZ[i] = (int) depth;
				}
				if (this.threadResource.water) {
					this.worldY[i] = (int) (this.modelMatrix.ty + this.modelMatrix.e2_1 * (float) px + this.modelMatrix.e2_2 * (float) py + this.modelMatrix.e2_3 * (float) pz);
				}
			}
			if (this.billboardFaces != null) {
				for (billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
					javaBillboardFace = this.billboardFaces[billboardIndex];
					javaBillboardAttributes = this.billboardAttributes[billboardIndex];
					a = this.faceA[javaBillboardFace.face];
					b = this.faceB[javaBillboardFace.face];
					c = this.faceC[javaBillboardFace.face];
					px = (this.vertexX[a] + this.vertexX[b] + this.vertexX[c]) / 3;
					py = (this.vertexY[a] + this.vertexY[b] + this.vertexY[c]) / 3;
					pz = (this.vertexZ[a] + this.vertexZ[b] + this.vertexZ[c]) / 3;
					viewX = (float) javaBillboardAttributes.offsetX + tx + m11 * (float) px + m12 * (float) py + m13 * (float) pz;
					viewY = (float) javaBillboardAttributes.offsetY + ty + m21 * (float) px + m22 * (float) py + m23 * (float) pz;
					depth = tz + m31 * (float) px + m32 * (float) py + m33 * (float) pz;
					if (depth > (float) this.toolkit.zNear) {
						javaBillboardAttributes.x = this.toolkit.projectionCenterX + (int) (viewX * (float) projectionScaleX / depth);
						javaBillboardAttributes.y = this.toolkit.lb + (int) (viewY * (float) projectionScaleY / depth);
						javaBillboardAttributes.z = (int) depth - javaBillboardFace.distance;
						javaBillboardAttributes.projectedWidth = (int) ((float) (javaBillboardAttributes.scaleX * javaBillboardFace.width * projectionScaleX) / (depth * 128.0F));
						javaBillboardAttributes.projectedHeight = (int) ((float) (javaBillboardAttributes.scaleY * javaBillboardFace.height * projectionScaleY) / (depth * 128.0F));
					} else {
						javaBillboardAttributes.projectedWidth = javaBillboardAttributes.projectedHeight = 0;
					}
				}
			}
		} else {
			for (i = 0; i < this.vertexCount; i++) {
				px = this.vertexX[i];
				py = this.vertexY[i];
				pz = this.vertexZ[i];
				viewX = tx + m11 * (float) px + m12 * (float) py + m13 * (float) pz;
				viewY = ty + m21 * (float) px + m22 * (float) py + m23 * (float) pz;
				depth = tz + m31 * (float) px + m32 * (float) py + m33 * (float) pz;
				this.screenZ[i] = (int) depth;
				this.screenX[i] = this.threadResource.rasterMinX + (int) (viewX * (float) projectionScaleX / (float) orthoDepth);
				this.screenY[i] = this.threadResource.rasterMinY + (int) (viewY * (float) projectionScaleY / (float) orthoDepth);
				if (storeCameraCoords) {
					this.cameraX[i] = (int) viewX;
					this.cameraY[i] = (int) viewY;
					this.cameraZ[i] = orthoDepth;
				}
				if (this.threadResource.water) {
					this.worldY[i] = (int) (this.modelMatrix.ty + this.modelMatrix.e2_1 * (float) px + this.modelMatrix.e2_2 * (float) py + this.modelMatrix.e2_3 * (float) pz);
				}
			}
			if (this.billboardFaces != null) {
				for (billboardIndex = 0; billboardIndex < this.billboardCount; billboardIndex++) {
					javaBillboardFace = this.billboardFaces[billboardIndex];
					javaBillboardAttributes = this.billboardAttributes[billboardIndex];
					a = this.faceA[javaBillboardFace.face];
					b = this.faceB[javaBillboardFace.face];
					c = this.faceC[javaBillboardFace.face];
					px = (this.vertexX[a] + this.vertexX[b] + this.vertexX[c]) / 3;
					py = (this.vertexY[a] + this.vertexY[b] + this.vertexY[c]) / 3;
					pz = (this.vertexZ[a] + this.vertexZ[b] + this.vertexZ[c]) / 3;
					viewX = tx + m11 * (float) px + m12 * (float) py + m13 * (float) pz;
					viewY = ty + m21 * (float) px + m22 * (float) py + m23 * (float) pz;
					javaBillboardAttributes.x = this.toolkit.projectionCenterX + (int) (viewX * (float) projectionScaleX / (float) orthoDepth);
					javaBillboardAttributes.y = this.toolkit.lb + (int) (viewY * (float) projectionScaleY / (float) orthoDepth);
					javaBillboardAttributes.z = orthoDepth - javaBillboardFace.distance;
					javaBillboardAttributes.projectedWidth = javaBillboardAttributes.scaleX * javaBillboardFace.width * projectionScaleX / (orthoDepth << 7);
					javaBillboardAttributes.projectedHeight = javaBillboardAttributes.scaleY * javaBillboardFace.height * projectionScaleY / (orthoDepth << 7);
				}
			}
		}
		if (cylinder != null) {
			@Pc(1483) boolean clipped = false;
			@Pc(1485) boolean visible = true;
			@Pc(1493) int midX = this.minX + this.maxX >> 1;
			@Pc(1501) int midZ = this.minZ + this.maxZ >> 1;
			@Pc(1506) short sampleY = this.minY;
			viewX = tx + m11 * (float) midX + m12 * (float) sampleY + m13 * (float) midZ;
			viewY = ty + m21 * (float) midX + m22 * (float) sampleY + m23 * (float) midZ;
			depth = tz + m31 * (float) midX + m32 * (float) sampleY + m33 * (float) midZ;
			if (depth >= (float) zNear) {
				@Pc(1567) int projectionDepth = (int) depth;
				if (orthoDepth != -1) {
					projectionDepth = orthoDepth;
				}
				cylinder.left = this.toolkit.projectionCenterX + (int) (viewX * (float) projectionScaleX / (float) projectionDepth);
				cylinder.bottom = this.toolkit.lb + (int) (viewY * (float) projectionScaleY / (float) projectionDepth);
			} else {
				clipped = true;
			}
			sampleY = this.maxY;
			@Pc(1627) float maxYViewX = tx + m11 * (float) midX + m12 * (float) sampleY + m13 * (float) midZ;
			@Pc(1644) float maxYViewY = ty + m21 * (float) midX + m22 * (float) sampleY + m23 * (float) midZ;
			@Pc(1661) float maxYDepth = tz + m31 * (float) midX + m32 * (float) sampleY + m33 * (float) midZ;
			@Pc(1669) int projectionDepth;
			if (maxYDepth >= (float) zNear) {
				projectionDepth = (int) maxYDepth;
				if (orthoDepth != -1) {
					projectionDepth = orthoDepth;
				}
				cylinder.right = this.toolkit.projectionCenterX + (int) (maxYViewX * (float) projectionScaleX / (float) projectionDepth);
				cylinder.top = this.toolkit.lb + (int) (maxYViewY * (float) projectionScaleY / (float) projectionDepth);
			} else {
				clipped = true;
			}
			if (clipped) {
				if (depth < (float) zNear && maxYDepth < (float) zNear) {
					visible = false;
				} else {
					@Pc(1745) int nearX;
					@Pc(1754) int nearY;
					@Pc(1756) int nearDepth;
					@Pc(1736) float t;
					if (depth < (float) zNear) {
						t = (maxYDepth - (float) this.toolkit.zNear) / (maxYDepth - depth);
						nearX = (int) (maxYViewX + (maxYViewX - viewX) * t);
						nearY = (int) (maxYViewY + (maxYViewY - viewY) * t);
						nearDepth = zNear;
						if (orthoDepth != -1) {
							nearDepth = orthoDepth;
						}
						cylinder.left = this.toolkit.projectionCenterX + nearX * projectionScaleX / nearDepth;
						cylinder.bottom = this.toolkit.lb + nearY * projectionScaleY / nearDepth;
					} else if (maxYDepth < (float) zNear) {
						t = (depth - (float) zNear) / (depth - maxYDepth);
						nearX = (int) (viewX + (viewX - maxYViewX) * t);
						nearY = (int) (viewY + (viewY - maxYViewY) * t);
						nearDepth = zNear;
						if (orthoDepth != -1) {
							nearDepth = orthoDepth;
						}
						cylinder.left = this.toolkit.projectionCenterX + nearX * projectionScaleX / nearDepth;
						cylinder.bottom = this.toolkit.lb + nearY * projectionScaleY / nearDepth;
					}
				}
			}
			if (visible) {
				if (depth > maxYDepth) {
					projectionDepth = (int) depth;
					if (orthoDepth != -1) {
						projectionDepth = orthoDepth;
					}
					cylinder.size = this.toolkit.projectionCenterX + (int) ((viewX + (float) this.radius) * (float) projectionScaleX / (float) projectionDepth) - cylinder.left;
				} else {
					projectionDepth = (int) maxYDepth;
					if (orthoDepth != -1) {
						projectionDepth = orthoDepth;
					}
					cylinder.size = this.toolkit.projectionCenterX + (int) ((maxYViewX + (float) this.radius) * (float) projectionScaleX / (float) projectionDepth) - cylinder.right;
				}
				cylinder.positionCalculated = true;
			}
		}
		this.applyLighting(true);
		this.rasterizer.fastScanline = (flags & 0x1) == 0;
		this.rasterizer.halfBlend = false;
		try {
			this.drawFacesRgb(nearClipped, this.threadResource.fogActive && far > this.threadResource.fogPlane || this.threadResource.water, near, far - near);
		} catch (@Pc(1955) Exception ex2) {
		}
		if (this.billboardFaces != null) {
			for (i = 0; i < this.faceCount; i++) {
				this.faceBillboard[i] = -1;
			}
		}
		this.rasterizer = null;
		if (this.toolkit.threadCount > 1) {
			synchronized (this) {
				this.rendering = false;
				this.notifyAll();
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "r", descriptor = "()Z")
	@Override
	public boolean r() {
		return this.movingTextures;
	}

	@OriginalMember(owner = "client!jga", name = "s", descriptor = "()V")
	private void calculateBounds() {
		if (this.boundsValid) {
			return;
		}
		@Pc(5) int maxRadiusSq = 0;
		@Pc(7) int maxSphereSq = 0;
		@Pc(9) int lowX = 32767;
		@Pc(11) int lowY = 32767;
		@Pc(13) int lowZ = 32767;
		@Pc(15) int highX = -32768;
		@Pc(17) int highY = -32768;
		@Pc(19) int highZ = -32768;
		for (@Pc(21) int i = 0; i < this.maxVertex; i++) {
			@Pc(27) int vx = this.vertexX[i];
			@Pc(32) int vy = this.vertexY[i];
			@Pc(37) int vz = this.vertexZ[i];
			if (vx < lowX) {
				lowX = vx;
			}
			if (vx > highX) {
				highX = vx;
			}
			if (vy < lowY) {
				lowY = vy;
			}
			if (vy > highY) {
				highY = vy;
			}
			if (vz < lowZ) {
				lowZ = vz;
			}
			if (vz > highZ) {
				highZ = vz;
			}
			@Pc(75) int distSq = vx * vx + vz * vz;
			if (distSq > maxRadiusSq) {
				maxRadiusSq = distSq;
			}
			distSq += vy * vy;
			if (distSq > maxSphereSq) {
				maxSphereSq = distSq;
			}
		}
		this.minX = (short) lowX;
		this.maxX = (short) highX;
		this.minY = (short) lowY;
		this.maxY = (short) highY;
		this.minZ = (short) lowZ;
		this.maxZ = (short) highZ;
		this.radius = (short) (Math.sqrt((double) maxRadiusSq) + 0.99D);
		this.sphereRadius = (short) (Math.sqrt((double) maxSphereSq) + 0.99D);
		this.boundsValid = true;
	}

	@OriginalMember(owner = "client!jga", name = "f", descriptor = "(I)I")
	private int clampLightness(@OriginalArg(0) int lightness) {
		if (lightness < 2) {
			lightness = 2;
		} else if (lightness > 126) {
			lightness = 126;
		}
		return lightness;
	}

	@OriginalMember(owner = "client!jga", name = "b", descriptor = "(Z)V")
	private void calculateLighting(@OriginalArg(0) boolean discardShading) {
		if (this.lightingState == 1) {
			this.relight();
		} else if (this.lightingState == 2) {
			if ((this.functionMask & 0x97098) == 0 && this.texCoordU == null) {
				this.faceColour = null;
			}
			if (discardShading) {
				this.shadingType = null;
			}
		} else {
			this.ensureNormals();
			@Pc(33) int sunX = this.toolkit.sunX;
			@Pc(37) int sunY = this.toolkit.sunY;
			@Pc(41) int sunZ = this.toolkit.sunZ;
			@Pc(47) int ambientLight = this.toolkit.ambient >> 8;
			@Pc(56) int sunIntensity = this.toolkit.sunIntensity * 768 / this.contrast;
			@Pc(65) int reverseSunIntensity = this.toolkit.reverseSunIntensity * 768 / this.contrast;
			if (this.faceColourA == null) {
				this.faceColourA = new int[this.faceCount];
				this.faceColourB = new int[this.faceCount];
				this.faceColourC = new int[this.faceCount];
			}
			for (@Pc(85) int face = 0; face < this.faceCount; face++) {
				@Pc(91) byte shading;
				if (this.shadingType == null) {
					shading = 0;
				} else {
					shading = this.shadingType[face];
				}
				@Pc(102) byte alpha;
				if (this.faceAlpha == null) {
					alpha = 0;
				} else {
					alpha = this.faceAlpha[face];
				}
				@Pc(113) short texture;
				if (this.faceTextures == null) {
					texture = -1;
				} else {
					texture = this.faceTextures[face];
				}
				if (alpha == -2) {
					shading = 3;
				}
				if (alpha == -1) {
					shading = 2;
				}
				@Pc(141) int hsl;
				@Pc(223) int local223;
				if (texture == -1) {
					@Pc(206) int local206;
					@Pc(213) int local213;
					@Pc(158) short hsv;
					@Pc(150) int lightness;
					if (shading == 0) {
						hsl = this.faceColour[face] & 0xFFFF;
						lightness = (hsl & 0x7F) * this.ambient >> 7;
						hsv = ColorUtil.hslToHsv(hsl & 0xFFFFFF80 | lightness);
						@Pc(177) VertexNormal normal;
						if (this.vertexNormalsOffset == null || this.vertexNormalsOffset[this.faceA[face]] == null) {
							normal = this.lb[this.faceA[face]];
						} else {
							normal = this.vertexNormalsOffset[this.faceA[face]];
						}
						int dot = (sunX * normal.x + sunY * normal.y + sunZ * normal.z) / normal.magnitude >> 16;
						int intensity = dot > 256 ? sunIntensity : reverseSunIntensity;
						int light = (ambientLight >> 1) + (intensity * dot >> 17);
						this.faceColourA[face] = light << 17 | JavaModel.applyLightness(light, hsv);
						if (this.vertexNormalsOffset == null || this.vertexNormalsOffset[this.faceB[face]] == null) {
							normal = this.lb[this.faceB[face]];
						} else {
							normal = this.vertexNormalsOffset[this.faceB[face]];
						}
						dot = (sunX * normal.x + sunY * normal.y + sunZ * normal.z) / normal.magnitude >> 16;
						intensity = dot > 256 ? sunIntensity : reverseSunIntensity;
						light = (ambientLight >> 1) + (intensity * dot >> 17);
						this.faceColourB[face] = light << 17 | JavaModel.applyLightness(light, hsv);
						if (this.vertexNormalsOffset == null || this.vertexNormalsOffset[this.faceC[face]] == null) {
							normal = this.lb[this.faceC[face]];
						} else {
							normal = this.vertexNormalsOffset[this.faceC[face]];
						}
						dot = (sunX * normal.x + sunY * normal.y + sunZ * normal.z) / normal.magnitude >> 16;
						intensity = dot > 256 ? sunIntensity : reverseSunIntensity;
						light = (ambientLight >> 1) + (intensity * dot >> 17);
						this.faceColourC[face] = light << 17 | JavaModel.applyLightness(light, hsv);
					} else if (shading == 1) {
						hsl = this.faceColour[face] & 0xFFFF;
						lightness = (hsl & 0x7F) * this.ambient >> 7;
						hsv = ColorUtil.hslToHsv(hsl & 0xFFFFFF80 | lightness);
						@Pc(422) FaceNormal faceNormal = this.faceNormals[face];
						int dot = sunX * faceNormal.x + sunY * faceNormal.y + sunZ * faceNormal.z >> 16;
						int intensity = dot > 256 ? sunIntensity : reverseSunIntensity;
						int light = (ambientLight >> 1) + (intensity * dot >> 17);
						this.faceColourA[face] = light << 17 | JavaModel.applyLightness(light, hsv);
						this.faceColourC[face] = -1;
					} else if (shading == 3) {
						this.faceColourA[face] = 128;
						this.faceColourC[face] = -1;
					} else {
						this.faceColourC[face] = -2;
					}
				} else {
					hsl = this.faceColour[face] & 0xFFFF;
					@Pc(570) int local570;
					@Pc(551) int local551;
					if (shading == 0) {
						@Pc(522) VertexNormal vertexNormal;
						if (this.vertexNormalsOffset == null || this.vertexNormalsOffset[this.faceA[face]] == null) {
							vertexNormal = this.lb[this.faceA[face]];
						} else {
							vertexNormal = this.vertexNormalsOffset[this.faceA[face]];
						}
						int dot = (sunX * vertexNormal.x + sunY * vertexNormal.y + sunZ * vertexNormal.z) / vertexNormal.magnitude >> 16;
						int intensity = dot > 256 ? sunIntensity : reverseSunIntensity;
						int light = this.clampLightness((ambientLight >> 2) + (intensity * dot >> 18));
						this.faceColourA[face] = light << 24 | this.shadeTexturedRgb(hsl, texture, light);
						if (this.vertexNormalsOffset == null || this.vertexNormalsOffset[this.faceB[face]] == null) {
							vertexNormal = this.lb[this.faceB[face]];
						} else {
							vertexNormal = this.vertexNormalsOffset[this.faceB[face]];
						}
						dot = (sunX * vertexNormal.x + sunY * vertexNormal.y + sunZ * vertexNormal.z) / vertexNormal.magnitude >> 16;
						intensity = dot > 256 ? sunIntensity : reverseSunIntensity;
						light = this.clampLightness((ambientLight >> 2) + (intensity * dot >> 18));
						this.faceColourB[face] = light << 24 | this.shadeTexturedRgb(hsl, texture, light);
						if (this.vertexNormalsOffset == null || this.vertexNormalsOffset[this.faceC[face]] == null) {
							vertexNormal = this.lb[this.faceC[face]];
						} else {
							vertexNormal = this.vertexNormalsOffset[this.faceC[face]];
						}
						dot = (sunX * vertexNormal.x + sunY * vertexNormal.y + sunZ * vertexNormal.z) / vertexNormal.magnitude >> 16;
						intensity = dot > 256 ? sunIntensity : reverseSunIntensity;
						light = this.clampLightness((ambientLight >> 2) + (intensity * dot >> 18));
						this.faceColourC[face] = light << 24 | this.shadeTexturedRgb(hsl, texture, light);
					} else if (shading == 1) {
						@Pc(752) FaceNormal faceNormal2 = this.faceNormals[face];
						int dot = sunX * faceNormal2.x + sunY * faceNormal2.y + sunZ * faceNormal2.z >> 16;
						int intensity = dot > 256 ? sunIntensity : reverseSunIntensity;
						int light = this.clampLightness((ambientLight >> 2) + (intensity * dot >> 18));
						this.faceColourA[face] = light << 24 | this.shadeTexturedRgb(hsl, texture, light);
						this.faceColourC[face] = -1;
					} else {
						this.faceColourC[face] = -2;
					}
				}
			}
			this.lb = null;
			this.vertexNormalsOffset = null;
			this.faceNormals = null;
			if ((this.functionMask & 0x97098) == 0 && this.texCoordU == null) {
				this.faceColour = null;
			}
			if (discardShading) {
				this.shadingType = null;
			}
			this.lightingState = 2;
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(BIZ)Lclient!ka;")
	@Override
	public Model copy(@OriginalArg(0) byte copyType, @OriginalArg(1) int functionMask, @OriginalArg(2) boolean ensureLit) {
		this.useThreadCopyCache(Thread.currentThread());
		@Pc(11) JavaModel javaModel;
		@Pc(8) JavaModel javaModel2;
		if (copyType == 1) {
			javaModel2 = this.copySrcModel1;
			javaModel = this.copyDstModel1;
		} else if (copyType == 2) {
			javaModel2 = this.copySrcModel2;
			javaModel = this.copyDstModel2;
		} else if (copyType == 3) {
			javaModel2 = this.copySrcModel3;
			javaModel = this.copyDstModel3;
		} else if (copyType == 4) {
			javaModel2 = this.copySrcModel4;
			javaModel = this.copyDstModel4;
		} else if (copyType == 5) {
			javaModel2 = this.copySrcModel5;
			javaModel = this.copyDstModel5;
		} else {
			javaModel = javaModel2 = new JavaModel(this.toolkit);
		}
		return this.copyTo(javaModel, javaModel2, functionMask, copyType != 0, ensureLit);
	}

	@OriginalMember(owner = "client!jga", name = "O", descriptor = "(III)V")
	@Override
	public void O(@OriginalArg(0) int scaleX, @OriginalArg(1) int scaleY, @OriginalArg(2) int scaleZ) {
		if (scaleX != 128 && (this.functionMask & 0x1) != 1) {
			throw new IllegalStateException();
		} else if (scaleY != 128 && (this.functionMask & 0x2) != 2) {
			throw new IllegalStateException();
		} else if (scaleZ == 128 || (this.functionMask & 0x4) == 4) {
			synchronized (this) {
				for (@Pc(44) int i = 0; i < this.vertexCount; i++) {
					this.vertexX[i] = this.vertexX[i] * scaleX >> 7;
					this.vertexY[i] = this.vertexY[i] * scaleY >> 7;
					this.vertexZ[i] = this.vertexZ[i] * scaleZ >> 7;
				}
				this.boundsValid = false;
			}
		} else {
			throw new IllegalStateException();
		}
	}

	@OriginalMember(owner = "client!jga", name = "ua", descriptor = "()I")
	@Override
	public int ua() {
		return this.functionMask;
	}

	@OriginalMember(owner = "client!jga", name = "g", descriptor = "(I)V")
	private void drawFoggedTriangleRgb(@OriginalArg(0) int face) {
		@Pc(8) short a;
		@Pc(13) short b;
		@Pc(18) short c;
		@Pc(27) int fogA;
		@Pc(46) int fogB;
		@Pc(65) int fogC;
		@Pc(81) int local81;
		@Pc(325) int local325;
		if (this.threadResource.water) {
			a = this.faceA[face];
			b = this.faceB[face];
			c = this.faceC[face];
			fogA = 0;
			fogB = 0;
			fogC = 0;
			if (this.worldY[a] > this.threadResource.waterDepth) {
				fogA = 255;
			} else if (this.worldY[a] > this.threadResource.waterHeight) {
				fogA = (this.threadResource.waterHeight - this.worldY[a]) * 255 / (this.threadResource.waterHeight - this.threadResource.waterDepth);
			}
			if (this.worldY[b] > this.threadResource.waterDepth) {
				fogB = 255;
			} else if (this.worldY[b] > this.threadResource.waterHeight) {
				fogB = (this.threadResource.waterHeight - this.worldY[b]) * 255 / (this.threadResource.waterHeight - this.threadResource.waterDepth);
			}
			if (this.worldY[c] > this.threadResource.waterDepth) {
				fogC = 255;
			} else if (this.worldY[c] > this.threadResource.waterHeight) {
				fogC = (this.threadResource.waterHeight - this.worldY[c]) * 255 / (this.threadResource.waterHeight - this.threadResource.waterDepth);
			}
			if (this.faceAlpha == null) {
				this.rasterizer.alpha = 0;
			} else {
				this.rasterizer.alpha = this.faceAlpha[face] & 0xFF;
			}
			if (this.faceTextures != null && this.faceTextures[face] != -1) {
				int alphaBits = -16777216;
				if (this.faceAlpha != null) {
					alphaBits = 255 - (this.faceAlpha[face] & 0xFF) << 24;
				}
				if (this.faceColourC[face] == -1) {
					int colour = alphaBits | this.faceColourA[face] & 0xFFFFFF;
					this.rasterizer.renderTexturedTriangleRgb((float) this.screenY[a], (float) this.screenY[b], (float) this.screenY[c], (float) this.screenX[a], (float) this.screenX[b], (float) this.screenX[c], (float) this.screenZ[a], (float) this.screenZ[b], (float) this.screenZ[c], this.texCoordU[face][0], this.texCoordU[face][1], this.texCoordU[face][2], this.texCoordV[face][0], this.texCoordV[face][1], this.texCoordV[face][2], colour, colour, colour, this.threadResource.fogColour, fogA, fogB, fogC, this.faceTextures[face]);
				} else {
					this.rasterizer.renderTexturedTriangleRgb((float) this.screenY[a], (float) this.screenY[b], (float) this.screenY[c], (float) this.screenX[a], (float) this.screenX[b], (float) this.screenX[c], (float) this.screenZ[a], (float) this.screenZ[b], (float) this.screenZ[c], this.texCoordU[face][0], this.texCoordU[face][1], this.texCoordU[face][2], this.texCoordV[face][0], this.texCoordV[face][1], this.texCoordV[face][2], alphaBits | this.faceColourA[face] & 0xFFFFFF, alphaBits | this.faceColourB[face] & 0xFFFFFF, alphaBits | this.faceColourC[face] & 0xFFFFFF, this.threadResource.fogColour, fogA, fogB, fogC, this.faceTextures[face]);
				}
			} else if (this.faceColourC[face] == -1) {
				this.rasterizer.renderTriangleRgb((float) this.screenY[a], (float) this.screenY[b], (float) this.screenY[c], (float) this.screenX[a], (float) this.screenX[b], (float) this.screenX[c], (float) this.screenZ[a], (float) this.screenZ[b], (float) this.screenZ[c], ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable[this.faceColourA[face] & 0xFFFF], fogA << 24 | this.threadResource.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable[this.faceColourA[face] & 0xFFFF], fogB << 24 | this.threadResource.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable[this.faceColourA[face] & 0xFFFF], fogC << 24 | this.threadResource.fogColour));
			} else {
				this.rasterizer.renderTriangleRgb((float) this.screenY[a], (float) this.screenY[b], (float) this.screenY[c], (float) this.screenX[a], (float) this.screenX[b], (float) this.screenX[c], (float) this.screenZ[a], (float) this.screenZ[b], (float) this.screenZ[c], ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable[this.faceColourA[face] & 0xFFFF], fogA << 24 | this.threadResource.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable[this.faceColourB[face] & 0xFFFF], fogB << 24 | this.threadResource.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable[this.faceColourC[face] & 0xFFFF], fogC << 24 | this.threadResource.fogColour));
			}
			return;
		}
		a = this.faceA[face];
		b = this.faceB[face];
		c = this.faceC[face];
		fogA = this.screenZ[a] - this.threadResource.fogPlane;
		if (fogA > 255) {
			fogA = 255;
		} else if (fogA < 0) {
			fogA = 0;
		}
		fogB = this.screenZ[b] - this.threadResource.fogPlane;
		if (fogB > 255) {
			fogB = 255;
		} else if (fogB < 0) {
			fogB = 0;
		}
		fogC = this.screenZ[c] - this.threadResource.fogPlane;
		if (fogC > 255) {
			fogC = 255;
		} else if (fogC < 0) {
			fogC = 0;
		}
		int fogSum = fogA + fogB + fogC;
		if (fogSum == 765) {
			return;
		}
		if (fogSum == 0) {
			this.drawTriangleRgb(face);
			return;
		}
		if (this.faceAlpha == null) {
			this.rasterizer.alpha = 0;
		} else {
			this.rasterizer.alpha = this.faceAlpha[face] & 0xFF;
		}
		if (this.faceTextures != null && this.faceTextures[face] != -1) {
			int alphaBits = -16777216;
			if (this.faceAlpha != null) {
				alphaBits = 255 - (this.faceAlpha[face] & 0xFF) << 24;
			}
			if (this.faceColourC[face] == -1) {
				@Pc(354) int colour = alphaBits | this.faceColourA[face] & 0xFFFFFF;
				this.rasterizer.renderTexturedTriangleRgb((float) this.screenY[a], (float) this.screenY[b], (float) this.screenY[c], (float) this.screenX[a], (float) this.screenX[b], (float) this.screenX[c], (float) this.screenZ[a], (float) this.screenZ[b], (float) this.screenZ[c], this.texCoordU[face][0], this.texCoordU[face][1], this.texCoordU[face][2], this.texCoordV[face][0], this.texCoordV[face][1], this.texCoordV[face][2], colour, colour, colour, this.threadResource.fogColour, fogA, fogB, fogC, this.faceTextures[face]);
			} else {
				this.rasterizer.renderTexturedTriangleRgb((float) this.screenY[a], (float) this.screenY[b], (float) this.screenY[c], (float) this.screenX[a], (float) this.screenX[b], (float) this.screenX[c], (float) this.screenZ[a], (float) this.screenZ[b], (float) this.screenZ[c], this.texCoordU[face][0], this.texCoordU[face][1], this.texCoordU[face][2], this.texCoordV[face][0], this.texCoordV[face][1], this.texCoordV[face][2], alphaBits | this.faceColourA[face] & 0xFFFFFF, alphaBits | this.faceColourB[face] & 0xFFFFFF, alphaBits | this.faceColourC[face] & 0xFFFFFF, this.threadResource.fogColour, fogA, fogB, fogC, this.faceTextures[face]);
			}
		} else if (this.faceColourC[face] == -1) {
			this.rasterizer.renderTriangleRgb((float) this.screenY[a], (float) this.screenY[b], (float) this.screenY[c], (float) this.screenX[a], (float) this.screenX[b], (float) this.screenX[c], (float) this.screenZ[a], (float) this.screenZ[b], (float) this.screenZ[c], ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable[this.faceColourA[face] & 0xFFFF], fogA << 24 | this.threadResource.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable[this.faceColourA[face] & 0xFFFF], fogB << 24 | this.threadResource.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable[this.faceColourA[face] & 0xFFFF], fogC << 24 | this.threadResource.fogColour));
		} else {
			this.rasterizer.renderTriangleRgb((float) this.screenY[a], (float) this.screenY[b], (float) this.screenY[c], (float) this.screenX[a], (float) this.screenX[b], (float) this.screenX[c], (float) this.screenZ[a], (float) this.screenZ[b], (float) this.screenZ[c], ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable[this.faceColourA[face] & 0xFFFF], fogA << 24 | this.threadResource.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable[this.faceColourB[face] & 0xFFFF], fogB << 24 | this.threadResource.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable[this.faceColourC[face] & 0xFFFF], fogC << 24 | this.threadResource.fogColour));
		}
	}
	@OriginalMember(owner = "client!vw", name = "a", descriptor = "(III)I")
	public static int applyLightness(@OriginalArg(1) int light, @OriginalArg(2) int hsl) {
		light = light * (hsl & 0x7F) >> 7;
		if (light < 2) {
			light = 2;
		} else if (light > 126) {
			light = 126;
		}
		return (hsl & 0xFF80) + light;
	}
}
