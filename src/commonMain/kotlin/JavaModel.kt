import com.jagex.game.runetek5.graphics.texture.TextureTransformationMatrix
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.JavaMatrix
import com.jagex.game.runetek5.graphics.model.billboard.BillboardType
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.JavaBillboardAttributes
import com.jagex.game.runetek5.graphics.JavaBillboardFace
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.normal.MeshBillboard
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.entity.BoundingBoxPolygon
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import com.jagex.game.runetek5.graphics.model.normal.FaceNormal
import com.jagex.game.runetek5.graphics.safe.PureJavaModel
import com.jagex.game.runetek5.graphics.model.normal.VertexNormal
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.graphics.model.billboard.BillboardTypeList
import kotlin.jvm.JvmStatic
import lang.InterruptedException
import lang.Thread
import lockNotifyAll
import lockWait
import withLock

@OriginalClass("client!jga")
class JavaModel : Model {

	@OriginalMember(owner = "client!jga", name = "o", descriptor = "[S")
	private var faceColour: ShortArray? = null

	@OriginalMember(owner = "client!jga", name = "p", descriptor = "[I")
	private var vertexZ: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "q", descriptor = "[Lclient!fb;")
	private var vertexNormalsOffset: Array<VertexNormal?>? = null

	@OriginalMember(owner = "client!jga", name = "r", descriptor = "[[F")
	private var texCoordU: Array<FloatArray?>? = null

	@OriginalMember(owner = "client!jga", name = "s", descriptor = "[I")
	private var clippedX: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "t", descriptor = "I")
	private var functionMask: Int = 0

	@OriginalMember(owner = "client!jga", name = "u", descriptor = "[S")
	private var faceA: ShortArray? = null

	@OriginalMember(owner = "client!jga", name = "v", descriptor = "[I")
	private var otherMergeStamps: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "w", descriptor = "Lclient!jga;")
	private var copySrcModel5: JavaModel? = null

	@OriginalMember(owner = "client!jga", name = "x", descriptor = "Lclient!gq;")
	private var modelMatrix: JavaMatrix? = null

	@OriginalMember(owner = "client!jga", name = "y", descriptor = "[I")
	private var screenX: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "z", descriptor = "[Lclient!nn;")
	private var emitters: Array<EmissiveTriangle?>? = null

	@OriginalMember(owner = "client!jga", name = "B", descriptor = "S")
	private var radius: Short = 0

	@OriginalMember(owner = "client!jga", name = "C", descriptor = "[B")
	private var faceAlpha: ByteArray? = null

	@OriginalMember(owner = "client!jga", name = "D", descriptor = "[I")
	private var mergeStamps: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "E", descriptor = "[S")
	private var faceIndices: ShortArray? = null

	@OriginalMember(owner = "client!jga", name = "F", descriptor = "Lclient!jga;")
	private var copySrcModel2: JavaModel? = null

	@OriginalMember(owner = "client!jga", name = "G", descriptor = "S")
	private var minY: Short = 0

	@OriginalMember(owner = "client!jga", name = "H", descriptor = "[[I")
	private var faceLabels: Array<IntArray?>? = null

	@OriginalMember(owner = "client!jga", name = "I", descriptor = "S")
	private var maxZ: Short = 0

	@OriginalMember(owner = "client!jga", name = "J", descriptor = "[I")
	private var vertexY: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "K", descriptor = "[S")
	private var originModels: ShortArray? = null

	@OriginalMember(owner = "client!jga", name = "L", descriptor = "[[I")
	private var vertexLabels: Array<IntArray?>? = null

	@OriginalMember(owner = "client!jga", name = "M", descriptor = "[Lclient!eca;")
	private var effectors: Array<EffectiveVertex?>? = null

	@OriginalMember(owner = "client!jga", name = "N", descriptor = "Lclient!pga;")
	private var copyThreadResource: JavaThreadResource? = null

	@OriginalMember(owner = "client!jga", name = "O", descriptor = "[I")
	private var faceColourB: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "P", descriptor = "I")
	private var pivotX: Int = 0

	@OriginalMember(owner = "client!jga", name = "Q", descriptor = "Lclient!jga;")
	private var copyDstModel2: JavaModel? = null

	@OriginalMember(owner = "client!jga", name = "T", descriptor = "I")
	private var pivotZ: Int = 0

	@OriginalMember(owner = "client!jga", name = "U", descriptor = "[I")
	private var screenY: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "V", descriptor = "I")
	private var ambient: Int = 0

	@OriginalMember(owner = "client!jga", name = "W", descriptor = "[[F")
	private var texCoordV: Array<FloatArray?>? = null

	@OriginalMember(owner = "client!jga", name = "Y", descriptor = "Lclient!jga;")
	private var copyDstModel4: JavaModel? = null

	@OriginalMember(owner = "client!jga", name = "Z", descriptor = "S")
	private var sphereRadius: Short = 0

	@OriginalMember(owner = "client!jga", name = "ab", descriptor = "I")
	private var contrast: Int = 0

	@OriginalMember(owner = "client!jga", name = "db", descriptor = "S")
	private var minZ: Short = 0

	@OriginalMember(owner = "client!jga", name = "gb", descriptor = "[I")
	private var screenZ: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "hb", descriptor = "[I")
	private var vertexX: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "jb", descriptor = "[B")
	private var facePriority: ByteArray? = null

	@OriginalMember(owner = "client!jga", name = "kb", descriptor = "[I")
	private var boundsCornerX: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "lb", descriptor = "[Lclient!fb;")
	private var lb: Array<VertexNormal?>? = null

	@OriginalMember(owner = "client!jga", name = "mb", descriptor = "[Lclient!go;")
	private var billboardFaces: Array<JavaBillboardFace?>? = null

	@OriginalMember(owner = "client!jga", name = "nb", descriptor = "[I")
	private var worldY: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "ob", descriptor = "[Lclient!ei;")
	private var billboardAttributes: Array<JavaBillboardAttributes?>? = null

	@OriginalMember(owner = "client!jga", name = "pb", descriptor = "[S")
	private var faceC: ShortArray? = null

	@OriginalMember(owner = "client!jga", name = "qb", descriptor = "S")
	private var maxY: Short = 0

	@OriginalMember(owner = "client!jga", name = "rb", descriptor = "[I")
	private var faceColourC: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "sb", descriptor = "Lclient!pga;")
	private var threadResource: JavaThreadResource? = null

	@OriginalMember(owner = "client!jga", name = "tb", descriptor = "[I")
	private var faceColourA: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "vb", descriptor = "[S")
	private var faceOriginModels: ShortArray? = null

	@OriginalMember(owner = "client!jga", name = "wb", descriptor = "[I")
	private var clippedY: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "yb", descriptor = "[I")
	private var boundsCornerY: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "zb", descriptor = "Lclient!jga;")
	private var copySrcModel1: JavaModel? = null

	@OriginalMember(owner = "client!jga", name = "Ab", descriptor = "I")
	private var pivotY: Int = 0

	@OriginalMember(owner = "client!jga", name = "Bb", descriptor = "Lclient!jga;")
	private var copySrcModel3: JavaModel? = null

	@OriginalMember(owner = "client!jga", name = "Cb", descriptor = "[I")
	private var clippedColour: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "Db", descriptor = "Lclient!jga;")
	private var copyDstModel1: JavaModel? = null

	@OriginalMember(owner = "client!jga", name = "Eb", descriptor = "[[I")
	private var billboardLabels: Array<IntArray?>? = null

	@OriginalMember(owner = "client!jga", name = "Fb", descriptor = "Z")
	private var recoloured: Boolean = false

	@OriginalMember(owner = "client!jga", name = "Gb", descriptor = "[I")
	private var clippedZ: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "Ib", descriptor = "Lclient!mp;")
	private var rasterizer: Rasterizer? = null

	@OriginalMember(owner = "client!jga", name = "Jb", descriptor = "[I")
	private var cameraY: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "Kb", descriptor = "[Lclient!iga;")
	private var faceNormals: Array<FaceNormal?>? = null

	@OriginalMember(owner = "client!jga", name = "Lb", descriptor = "[B")
	private var shadingType: ByteArray? = null

	@OriginalMember(owner = "client!jga", name = "Nb", descriptor = "[S")
	private var faceTextures: ShortArray? = null

	@OriginalMember(owner = "client!jga", name = "Ob", descriptor = "I")
	private var billboardCount: Int = 0

	@OriginalMember(owner = "client!jga", name = "Pb", descriptor = "Lclient!jga;")
	private var copyDstModel5: JavaModel? = null

	@OriginalMember(owner = "client!jga", name = "Rb", descriptor = "[I")
	private var cameraZ: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "Sb", descriptor = "S")
	private var maxX: Short = 0

	@OriginalMember(owner = "client!jga", name = "Tb", descriptor = "[S")
	private var faceB: ShortArray? = null

	@OriginalMember(owner = "client!jga", name = "Ub", descriptor = "S")
	private var minX: Short = 0

	@OriginalMember(owner = "client!jga", name = "Vb", descriptor = "Lclient!jga;")
	private var copyDstModel3: JavaModel? = null

	@OriginalMember(owner = "client!jga", name = "Wb", descriptor = "Lclient!jga;")
	private var copySrcModel4: JavaModel? = null

	@OriginalMember(owner = "client!jga", name = "Xb", descriptor = "[I")
	private var boundsCornerZ: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "Yb", descriptor = "[I")
	private var cameraX: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "Zb", descriptor = "[I")
	private var faceBillboard: IntArray? = null

	@OriginalMember(owner = "client!jga", name = "A", descriptor = "Z")
	private var boundsValid: Boolean = false

	@OriginalMember(owner = "client!jga", name = "R", descriptor = "I")
	private var vertexCount: Int = 0

	@OriginalMember(owner = "client!jga", name = "ib", descriptor = "Z")
	private var rendering: Boolean = false

	@OriginalMember(owner = "client!jga", name = "fb", descriptor = "I")
	private var lightingState: Int = 0

	@OriginalMember(owner = "client!jga", name = "ub", descriptor = "Z")
	private var pivotDirty: Boolean = false

	@OriginalMember(owner = "client!jga", name = "cb", descriptor = "I")
	private var faceCount: Int = 0

	@OriginalMember(owner = "client!jga", name = "xb", descriptor = "Z")
	private var highPrecisionVertices: Boolean = false

	@OriginalMember(owner = "client!jga", name = "Qb", descriptor = "Z")
	private var movingTextures: Boolean = false

	@OriginalMember(owner = "client!jga", name = "Mb", descriptor = "I")
	private var maxVertex: Int = 0

	@OriginalMember(owner = "client!jga", name = "Hb", descriptor = "Z")
	private var transparent: Boolean = false

	@OriginalMember(owner = "client!jga", name = "eb", descriptor = "Lclient!ao;")
	private val toolkit: JavaToolkit?

	@OriginalMember(owner = "client!jga", name = "<init>", descriptor = "(Lclient!ao;)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?) : super() {
		this.toolkit = javaToolkit
	}

	@OriginalMember(owner = "client!jga", name = "<init>", descriptor = "(Lclient!ao;Lclient!bh;IIII)V")
	constructor(@OriginalArg(0) javaToolkit: JavaToolkit?, @OriginalArg(1) mesh: Mesh?, @OriginalArg(2) functionMask: Int, @OriginalArg(3) ambient: Int, @OriginalArg(4) contrast: Int, @OriginalArg(5) featureMask: Int) : super() {
		this.toolkit = javaToolkit
		this.functionMask = functionMask
		this.ambient = ambient
		this.contrast = contrast
		@Pc(47) val textureSource: MaterialProvider? = this.toolkit!!.materialProvider
		this.vertexCount = mesh!!.numVertices
		this.maxVertex = mesh.maxDepth
		this.vertexX = mesh.verticesX
		this.vertexY = mesh.verticesY
		this.vertexZ = mesh.verticesZ
		this.faceCount = mesh.numFaces
		this.faceA = mesh.facesA
		this.faceB = mesh.facesB
		this.faceC = mesh.facesC
		this.facePriority = mesh.facesLayer
		this.faceColour = mesh.facesColor
		this.faceAlpha = mesh.facesAlpha
		this.faceOriginModels = mesh.facesSubmeshes
		this.shadingType = mesh.faceRenderType
		this.emitters = mesh.particleEmitters
		this.effectors = mesh.particleEffectors
		this.originModels = mesh.verticesSubmeshes
		@Pc(119) val faceOrder: IntArray? = IntArray(this.faceCount)
		@Pc(121) var index: Int = 0
		while (index < this.faceCount) {
			faceOrder!![index] = index++
		}
		@Pc(135) val sortKeys: LongArray? = LongArray(this.faceCount)
		@Pc(144) val usePriority: Boolean = (this.functionMask and 0x100) != 0
		@Pc(151) var local151: Int
		@Pc(315) var local315: Int
		@Pc(146) var i: Int = 0
		while (i < this.faceCount) {
			val face: Int = faceOrder!![i]
			@Pc(153) var textureMetrics: MaterialType? = null
			@Pc(155) var keyHigh: Int = 0
			@Pc(157) val materialKeyBase: Byte = 0
			@Pc(159) var effectType: Byte = 0
			@Pc(161) var effectParam1: Byte = 0
			if (mesh.billboards != null) {
				@Pc(166) var hasUidBillboard: Boolean = false
				@Pc(168) var j: Int = 0
				while (j < mesh.billboards!!.size) {
					@Pc(174) val meshBillboard: MeshBillboard? = mesh.billboards!![j]
					if (face == meshBillboard!!.appliesOnFace) {
						@Pc(183) val lightType: BillboardType? = BillboardTypeList.list(meshBillboard.billboardId)
						if (lightType!!.hasUid) {
							hasUidBillboard = true
						}
						if (lightType.materialId != -1) {
							@Pc(198) val textureMetrics2: MaterialType? = textureSource!!.getMetrics(lightType.materialId)
							if (textureMetrics2!!.effectCombiner == 2) {
								this.transparent = true
							}
						}
					}
					j++
				}
				if (hasUidBillboard) {
					sortKeys!![i] = Long.MAX_VALUE
				}
			}
			@Pc(219) var material: Short = -1
			if (mesh.facesMaterial != null) {
				material = mesh.facesMaterial!![face]
				if (material.toInt() != -1) {
					textureMetrics = textureSource!!.getMetrics(material.toInt() and 0xFFFF)
					if ((featureMask and 0x40) != 0 && textureMetrics!!.detailsOnly) {
						material = -1
					} else {
						effectType = textureMetrics!!.effectType
						effectParam1 = textureMetrics.effectParam1
					}
				}
			}
			@Pc(271) val transparentFace: Boolean = this.faceAlpha != null && this.faceAlpha!![face].toInt() != 0 || textureMetrics != null && textureMetrics.effectCombiner == 2
			if ((usePriority || transparentFace) && this.facePriority != null) {
				keyHigh += this.facePriority!![face].toInt() shl 17
			}
			if (transparentFace) {
				keyHigh += 65536
			}
			keyHigh += (effectType.toInt() and 0xFF) shl 8
			keyHigh += effectParam1.toInt() and 0xFF
			val materialKey: Int = materialKeyBase.toInt() + ((material.toInt() and 0xFFFF) shl 16)
			@Pc(321) val keyLow: Int = materialKey + (i and 0xFFFF)
			sortKeys!![i] = (keyHigh.toLong() shl 32) + keyLow.toLong()
			this.transparent = this.transparent or transparentFace
			i++
		}
		ArrayUtils.quicksort(faceOrder, sortKeys)
		if (mesh.billboards != null) {
			this.billboardCount = mesh.billboards!!.size
			this.billboardFaces = arrayOfNulls<JavaBillboardFace>(this.billboardCount)
			this.billboardAttributes = arrayOfNulls<JavaBillboardAttributes>(this.billboardCount)
			var billboardIndex: Int = 0
			while (billboardIndex < mesh.billboards!!.size) {
				@Pc(372) val meshBillboard2: MeshBillboard? = mesh.billboards!![billboardIndex]
				@Pc(377) val lightType2: BillboardType? = BillboardTypeList.list(meshBillboard2!!.billboardId)
				var billboardColour: Int = ColorUtil.hsvToRgbTable!![mesh.facesColor!![meshBillboard2.appliesOnFace].toInt() and 0xFFFF] and 0xFFFFFF
				billboardColour = billboardColour or (255 - (if (mesh.facesAlpha == null) 0 else mesh.facesAlpha!![meshBillboard2.appliesOnFace].toInt() and 0xFF) shl 24)
				this.billboardFaces!![billboardIndex] = JavaBillboardFace(meshBillboard2.appliesOnFace, mesh.facesA!![meshBillboard2.appliesOnFace].toInt(), mesh.facesB!![meshBillboard2.appliesOnFace].toInt(), mesh.facesC!![meshBillboard2.appliesOnFace].toInt(), lightType2!!.size2d, lightType2.size3d, lightType2.materialId, lightType2.blendType, lightType2.shape, lightType2.hasUid, meshBillboard2.distance)
				this.billboardAttributes!![billboardIndex] = JavaBillboardAttributes(billboardColour)
				billboardIndex++
			}
		}
		this.texCoordU = arrayOfNulls<FloatArray>(this.faceCount)
		this.texCoordV = arrayOfNulls<FloatArray>(this.faceCount)
		@Pc(477) val textureMatrix: TextureTransformationMatrix? = TextureTransformationMatrix.generateTextureMatrix(this.faceCount, mesh, faceOrder)
		@Pc(482) val javaThreadResource: JavaThreadResource? = this.toolkit!!.threadResource(lang.currentThread())
		@Pc(485) val texCoordScratch: FloatArray? = javaThreadResource!!.texCoordScratch
		@Pc(487) var textured: Boolean = false
		@Pc(494) var face: Int
		@Pc(515) var material: Short
		@Pc(529) var textureMetrics3: MaterialType?
		@Pc(589) var local589: Int
		@Pc(489) var k: Int = 0
		while (k < this.faceCount) {
			face = faceOrder!![k]
			@Pc(499) var mappingIndex: Byte
			if (mesh.facesTexture == null) {
				mappingIndex = -1
			} else {
				mappingIndex = mesh.facesTexture!![face]
			}
			material = if (mesh.facesMaterial == null) -1 else mesh.facesMaterial!![face]
			if (material.toInt() != -1 && (featureMask and 0x40) != 0) {
				textureMetrics3 = textureSource!!.getMetrics(material.toInt() and 0xFFFF)
				if (textureMetrics3!!.detailsOnly) {
					material = -1
				}
			}
			if (material.toInt() != -1) {
				textured = true
				this.texCoordU!![face] = FloatArray(3)
				@Pc(547) val u: FloatArray? = this.texCoordU!![face]
				this.texCoordV!![face] = FloatArray(3)
				@Pc(555) val v: FloatArray? = this.texCoordV!![face]
				if (mappingIndex.toInt() == -1) {
					u!![0] = 0.0F
					v!![0] = 1.0F
					u[1] = 1.0F
					v[1] = 1.0F
					u[2] = 0.0F
					v[2] = 0.0F
				} else {
					val textureIndex: Int = mappingIndex.toInt() and 0xFF
					@Pc(594) val mapping: Byte = mesh.texturesMapping!![textureIndex]
					@Pc(601) var a: Short
					@Pc(606) var b: Short
					@Pc(611) var c: Short
					@Pc(644) var local644: Float
					@Pc(652) var local652: Float
					@Pc(660) var local660: Float
					@Pc(740) var local740: Float
					@Pc(748) var local748: Float
					@Pc(756) var local756: Float
					@Pc(764) var local764: Float
					@Pc(772) var local772: Float
					@Pc(780) var local780: Float
					if (mapping.toInt() == 0) {
						a = this.faceA!![face]
						b = this.faceB!![face]
						c = this.faceC!![face]
						@Pc(616) val texP: Short = mesh.textureFaceX!![textureIndex]
						@Pc(621) val texM: Short = mesh.textureFaceY!![textureIndex]
						@Pc(626) val texN: Short = mesh.textureFaceZ!![textureIndex]
						@Pc(632) val originX: Float = this.vertexX!![texP.toInt()].toFloat()
						@Pc(638) val originY: Float = this.vertexY!![texP.toInt()].toFloat()
						val originZ: Float = this.vertexZ!![texP.toInt()].toFloat()
						val mX: Float = this.vertexX!![texM.toInt()].toFloat() - originX
						val mY: Float = this.vertexY!![texM.toInt()].toFloat() - originY
						@Pc(668) val mZ: Float = this.vertexZ!![texM.toInt()].toFloat() - originZ
						@Pc(676) val nX: Float = this.vertexX!![texN.toInt()].toFloat() - originX
						@Pc(684) val nY: Float = this.vertexY!![texN.toInt()].toFloat() - originY
						@Pc(692) val nZ: Float = this.vertexZ!![texN.toInt()].toFloat() - originZ
						@Pc(700) val aX: Float = this.vertexX!![a.toInt()].toFloat() - originX
						@Pc(708) val aY: Float = this.vertexY!![a.toInt()].toFloat() - originY
						@Pc(716) val aZ: Float = this.vertexZ!![a.toInt()].toFloat() - originZ
						@Pc(724) val bX: Float = this.vertexX!![b.toInt()].toFloat() - originX
						@Pc(732) val bY: Float = this.vertexY!![b.toInt()].toFloat() - originY
						val bZ: Float = this.vertexZ!![b.toInt()].toFloat() - originZ
						val cX: Float = this.vertexX!![c.toInt()].toFloat() - originX
						val cY: Float = this.vertexY!![c.toInt()].toFloat() - originY
						val cZ: Float = this.vertexZ!![c.toInt()].toFloat() - originZ
						val crossX: Float = mY * nZ - mZ * nY
						val crossY: Float = mZ * nX - mX * nZ
						@Pc(788) val crossZ: Float = mX * nY - mY * nX
						@Pc(796) val uBasisX: Float = nY * crossZ - nZ * crossY
						@Pc(804) val uBasisY: Float = nZ * crossX - nX * crossZ
						@Pc(812) val uBasisZ: Float = nX * crossY - nY * crossX
						@Pc(826) val uScale: Float = 1.0F / (uBasisX * mX + uBasisY * mY + uBasisZ * mZ)
						u!![0] = (uBasisX * aX + uBasisY * aY + uBasisZ * aZ) * uScale
						u[1] = (uBasisX * bX + uBasisY * bY + uBasisZ * bZ) * uScale
						u[2] = (uBasisX * cX + uBasisY * cY + uBasisZ * cZ) * uScale
						@Pc(882) val vBasisX: Float = mY * crossZ - mZ * crossY
						@Pc(890) val vBasisY: Float = mZ * crossX - mX * crossZ
						@Pc(898) val vBasisZ: Float = mX * crossY - mY * crossX
						@Pc(912) val vScale: Float = 1.0F / (vBasisX * nX + vBasisY * nY + vBasisZ * nZ)
						v!![0] = (vBasisX * aX + vBasisY * aY + vBasisZ * aZ) * vScale
						v[1] = (vBasisX * bX + vBasisY * bY + vBasisZ * bZ) * vScale
						v[2] = (vBasisX * cX + vBasisY * cY + vBasisZ * cZ) * vScale
					} else {
						a = this.faceA!![face]
						b = this.faceB!![face]
						c = this.faceC!![face]
						@Pc(981) val centerX: Int = textureMatrix!!.centerX!![textureIndex]
						@Pc(986) val centerY: Int = textureMatrix.centerY!![textureIndex]
						@Pc(991) val centerZ: Int = textureMatrix.centerZ!![textureIndex]
						@Pc(996) val matrix: FloatArray? = textureMatrix.matrix!![textureIndex]
						@Pc(1001) val direction: Byte = mesh.texturesDirection!![textureIndex]
						val speedOffset: Float = mesh.texturesSpeed!![textureIndex].toFloat() / 256.0F
						if (mapping.toInt() == 1) {
							val uScale: Float = mesh.texturesScaleZ!![textureIndex].toFloat() / 1024.0F
							Model.mapCylindricalUv(centerZ, this.vertexZ!![a.toInt()], direction.toInt(), this.vertexX!![a.toInt()], texCoordScratch, this.vertexY!![a.toInt()], speedOffset, centerY, centerX, uScale, matrix)
							u!![0] = texCoordScratch!![0]
							v!![0] = texCoordScratch[1]
							Model.mapCylindricalUv(centerZ, this.vertexZ!![b.toInt()], direction.toInt(), this.vertexX!![b.toInt()], texCoordScratch, this.vertexY!![b.toInt()], speedOffset, centerY, centerX, uScale, matrix)
							u[1] = texCoordScratch[0]
							v[1] = texCoordScratch[1]
							Model.mapCylindricalUv(centerZ, this.vertexZ!![c.toInt()], direction.toInt(), this.vertexX!![c.toInt()], texCoordScratch, this.vertexY!![c.toInt()], speedOffset, centerY, centerX, uScale, matrix)
							u[2] = texCoordScratch[0]
							v[2] = texCoordScratch[1]
							val halfUScale: Float = uScale / 2.0F
							if ((direction.toInt() and 0x1) == 0) {
								if (u[1] - u[0] > halfUScale) {
									u[1] -= uScale
								} else if (u[0] - u[1] > halfUScale) {
									u[1] += uScale
								}
								if (u[2] - u[0] > halfUScale) {
									u[2] -= uScale
								} else if (u[0] - u[2] > halfUScale) {
									u[2] += uScale
								}
							} else {
								if (v[1] - v[0] > halfUScale) {
									v[1] -= uScale
								} else if (v[0] - v[1] > halfUScale) {
									v[1] += uScale
								}
								if (v[2] - v[0] > halfUScale) {
									v[2] -= uScale
								} else if (v[0] - v[2] > halfUScale) {
									v[2] += uScale
								}
							}
						} else if (mapping.toInt() == 2) {
							val uTrans: Float = mesh.texturesUtrans!![textureIndex].toFloat() / 256.0F
							val vTrans: Float = mesh.texturesVtrans!![textureIndex].toFloat() / 256.0F
							@Pc(1301) val edge1X: Int = this.vertexX!![b.toInt()] - this.vertexX!![a.toInt()]
							@Pc(1311) val edge1Y: Int = this.vertexY!![b.toInt()] - this.vertexY!![a.toInt()]
							@Pc(1321) val edge1Z: Int = this.vertexZ!![b.toInt()] - this.vertexZ!![a.toInt()]
							@Pc(1331) val edge2X: Int = this.vertexX!![c.toInt()] - this.vertexX!![a.toInt()]
							@Pc(1341) val edge2Y: Int = this.vertexY!![c.toInt()] - this.vertexY!![a.toInt()]
							@Pc(1351) val edge2Z: Int = this.vertexZ!![c.toInt()] - this.vertexZ!![a.toInt()]
							@Pc(1359) val normalX: Int = edge1Y * edge2Z - edge2Y * edge1Z
							@Pc(1367) val normalY: Int = edge1Z * edge2X - edge2Z * edge1X
							@Pc(1375) val normalZ: Int = edge1X * edge2Y - edge2X * edge1Y
							val scaleX: Float = 64.0F / mesh.texturesScaleX!![textureIndex].toFloat()
							val scaleY: Float = 64.0F / mesh.texturesScaleY!![textureIndex].toFloat()
							val scaleZ: Float = 64.0F / mesh.texturesScaleZ!![textureIndex].toFloat()
							val scaledNormalX: Float = (normalX.toFloat() * matrix!![0] + normalY.toFloat() * matrix[1] + normalZ.toFloat() * matrix[2]) / scaleX
							val scaledNormalY: Float = (normalX.toFloat() * matrix[3] + normalY.toFloat() * matrix[4] + normalZ.toFloat() * matrix[5]) / scaleY
							val scaledNormalZ: Float = (normalX.toFloat() * matrix[6] + normalY.toFloat() * matrix[7] + normalZ.toFloat() * matrix[8]) / scaleZ
							@Pc(1474) val cubeFace: Int = Model.getCubeFace(scaledNormalY, scaledNormalZ, scaledNormalX)
							Model.mapCubeUv(vTrans, speedOffset, matrix, this.vertexZ!![a.toInt()], centerZ, direction.toInt(), centerX, this.vertexX!![a.toInt()], this.vertexY!![a.toInt()], uTrans, texCoordScratch, centerY, cubeFace)
							u!![0] = texCoordScratch!![0]
							v!![0] = texCoordScratch[1]
							Model.mapCubeUv(vTrans, speedOffset, matrix, this.vertexZ!![b.toInt()], centerZ, direction.toInt(), centerX, this.vertexX!![b.toInt()], this.vertexY!![b.toInt()], uTrans, texCoordScratch, centerY, cubeFace)
							u[1] = texCoordScratch[0]
							v[1] = texCoordScratch[1]
							Model.mapCubeUv(vTrans, speedOffset, matrix, this.vertexZ!![c.toInt()], centerZ, direction.toInt(), centerX, this.vertexX!![c.toInt()], this.vertexY!![c.toInt()], uTrans, texCoordScratch, centerY, cubeFace)
							u[2] = texCoordScratch[0]
							v[2] = texCoordScratch[1]
						} else if (mapping.toInt() == 3) {
							Model.mapSphericalUv(centerZ, direction.toInt(), speedOffset, this.vertexX!![a.toInt()], texCoordScratch, this.vertexZ!![a.toInt()], centerX, this.vertexY!![a.toInt()], centerY, matrix)
							u!![0] = texCoordScratch!![0]
							v!![0] = texCoordScratch[1]
							Model.mapSphericalUv(centerZ, direction.toInt(), speedOffset, this.vertexX!![b.toInt()], texCoordScratch, this.vertexZ!![b.toInt()], centerX, this.vertexY!![b.toInt()], centerY, matrix)
							u[1] = texCoordScratch[0]
							v[1] = texCoordScratch[1]
							Model.mapSphericalUv(centerZ, direction.toInt(), speedOffset, this.vertexX!![c.toInt()], texCoordScratch, this.vertexZ!![c.toInt()], centerX, this.vertexY!![c.toInt()], centerY, matrix)
							u[2] = texCoordScratch[0]
							v[2] = texCoordScratch[1]
							@Pc(1703) var local1703: Int
							if ((direction.toInt() and 0x1) == 0) {
								if (u[1] - u[0] > 0.5F) {
									u[1]--
								} else if (u[0] - u[1] > 0.5F) {
									u[1]++
								}
								if (u[2] - u[0] > 0.5F) {
									u[2]--
								} else if (u[0] - u[2] > 0.5F) {
									u[2]++
								}
							} else {
								if (v[1] - v[0] > 0.5F) {
									v[1]--
								} else if (v[0] - v[1] > 0.5F) {
									v[1]++
								}
								if (v[2] - v[0] > 0.5F) {
									v[2]--
								} else if (v[0] - v[2] > 0.5F) {
									v[2]++
								}
							}
						}
					}
				}
			}
			k++
		}
		if (!textured) {
			this.texCoordV = null
			this.texCoordU = this.texCoordV
		}
		if (mesh.vertexGroups != null && (this.functionMask and 0x20) != 0) {
			this.vertexLabels = mesh.createVertexGroups(true)
		}
		if (mesh.faceGroups != null && (this.functionMask and 0x180) != 0) {
			this.faceLabels = mesh.createFaceGroups()
		}
		if (mesh.billboards != null && (this.functionMask and 0x400) != 0) {
			this.billboardLabels = mesh.createBillboardGroups()
		}
		if (mesh.facesMaterial == null) {
			this.faceTextures = null
		} else {
			this.faceTextures = ShortArray(this.faceCount)
			@Pc(1893) var hasTextures: Boolean = false
			var faceIndex: Int = 0
			while (faceIndex < this.faceCount) {
				material = mesh.facesMaterial!![faceIndex]
				if (material.toInt() == -1) {
					this.faceTextures!![faceIndex] = -1
				} else {
					textureMetrics3 = this.toolkit!!.materialProvider!!.getMetrics(material.toInt())
					if ((featureMask and 0x40) != 0 && textureMetrics3!!.detailsOnly) {
						this.faceTextures!![faceIndex] = -1
					} else {
						this.faceTextures!![faceIndex] = material
						hasTextures = true
						if (textureMetrics3!!.effectCombiner == 2) {
							this.transparent = true
						}
						if (textureMetrics3.speedU.toInt() != 0 || textureMetrics3.speedV.toInt() != 0) {
							this.movingTextures = true
						}
					}
				}
				faceIndex++
			}
			if (!hasTextures) {
				this.faceTextures = null
			}
		}
		if (this.transparent || this.billboardFaces != null) {
			this.faceIndices = ShortArray(this.faceCount)
			face = 0
			while (face < this.faceCount) {
				this.faceIndices!![face] = faceOrder!![face].toShort()
				face++
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "b", descriptor = "(I)Z")
	private fun isTranslucent(@OriginalArg(0) face: Int): Boolean {
		if (this.faceAlpha == null) {
			return false
		} else {
			return this.faceAlpha!![face].toInt() != 0
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(IIIIIIII)Z")
	private fun pointInFaceBounds(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) y0: Int, @OriginalArg(3) y1: Int, @OriginalArg(4) y2: Int, @OriginalArg(5) x0: Int, @OriginalArg(6) x1: Int, @OriginalArg(7) x2: Int): Boolean {
		if (y < y0 && y < y1 && y < y2) {
			return false
		} else if (y > y0 && y > y1 && y > y2) {
			return false
		} else if (x < x0 && x < x1 && x < x2) {
			return false
		} else {
			return x <= x0 || x <= x1 || x <= x2
		}
	}

	@OriginalMember(owner = "client!jga", name = "g", descriptor = "()V")
	private fun invalidateNormals() {
		this.lb = null
		this.vertexNormalsOffset = null
		this.faceNormals = null
		this.boundsValid = false
	}

	@OriginalMember(owner = "client!jga", name = "EA", descriptor = "()I")
	override fun EA(): Int {
		if (!this.boundsValid) {
			this.calculateBounds()
		}
		return this.maxY.toInt()
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Ljava/lang/Thread;)V")
	private fun useThreadBuffers(@OriginalArg(0) thread: Thread?) {
		@Pc(4) val resource: JavaThreadResource? = this.toolkit!!.threadResource(thread)
		this.rasterizer = resource!!.rasterizer
		if (resource === this.threadResource) {
			return
		}
		this.threadResource = resource
		this.worldY = this.threadResource!!.worldY
		this.cameraX = this.threadResource!!.cameraX
		this.cameraY = this.threadResource!!.cameraY
		this.cameraZ = this.threadResource!!.cameraZ
		this.screenX = this.threadResource!!.vertexScreenX
		this.screenY = this.threadResource!!.vertexScreenY
		this.screenZ = this.threadResource!!.vertexScreenZ
		this.boundsCornerX = this.threadResource!!.boundsCornerX
		this.boundsCornerY = this.threadResource!!.boundsCornerY
		this.boundsCornerZ = this.threadResource!!.boundsCornerZ
		this.clippedX = this.threadResource!!.clippedX
		this.clippedY = this.threadResource!!.clippedY
		this.clippedZ = this.threadResource!!.clippedZ
		this.clippedColour = this.threadResource!!.clippedColour
		this.mergeStamps = this.threadResource!!.mergeStamps
		this.otherMergeStamps = this.threadResource!!.otherMergeStamps
		this.faceBillboard = this.threadResource!!.faceBillboard
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Lclient!jga;Lclient!jga;IZZ)Lclient!ka;")
	private fun copyTo(@OriginalArg(0) target: JavaModel?, @OriginalArg(1) buffers: JavaModel?, @OriginalArg(2) functionMask: Int, @OriginalArg(3) reserve: Boolean, @OriginalArg(4) ensureLit: Boolean): Model? {
		target!!.boundsValid = this.boundsValid
		if (this.boundsValid) {
			target.maxX = this.maxX
			target.maxY = this.maxY
			target.maxZ = this.maxZ
			target.minX = this.minX
			target.minY = this.minY
			target.minZ = this.minZ
			target.radius = this.radius
			target.sphereRadius = this.sphereRadius
		}
		target.ambient = this.ambient
		target.contrast = this.contrast
		target.vertexCount = this.vertexCount
		target.maxVertex = this.maxVertex
		target.faceCount = this.faceCount
		target.billboardCount = this.billboardCount
		if ((functionMask and 0x100) == 0) {
			target.transparent = this.transparent
		} else {
			target.transparent = true
		}
		target.movingTextures = this.movingTextures
		@Pc(95) val copyAll: Boolean = ((functionMask and 0x7) == 7) or ((functionMask and 0x20) != 0)
		@Pc(105) val copyX: Boolean = copyAll || (functionMask and 0x1) != 0
		@Pc(115) val copyY: Boolean = copyAll || (functionMask and 0x2) != 0
		@Pc(129) val copyZ: Boolean = copyAll || (functionMask and 0x4) != 0 || (functionMask and 0x10) != 0
		@Pc(161) var local161: Int
		if (copyX || copyY || copyZ) {
			if (copyX) {
				if (buffers!!.vertexX == null || buffers.vertexX!!.size < this.vertexCount) {
					buffers.vertexX = IntArray(this.vertexCount)
					target.vertexX = buffers.vertexX
				} else {
					target.vertexX = buffers.vertexX
				}
				var vertexIndex: Int = 0
				while (vertexIndex < this.vertexCount) {
					target.vertexX!![vertexIndex] = this.vertexX!![vertexIndex]
					vertexIndex++
				}
			} else {
				target.vertexX = this.vertexX
			}
			if (copyY) {
				if (buffers!!.vertexY == null || buffers.vertexY!!.size < this.vertexCount) {
					buffers.vertexY = IntArray(this.vertexCount)
					target.vertexY = buffers.vertexY
				} else {
					target.vertexY = buffers.vertexY
				}
				var vertexIndex: Int = 0
				while (vertexIndex < this.vertexCount) {
					target.vertexY!![vertexIndex] = this.vertexY!![vertexIndex]
					vertexIndex++
				}
			} else {
				target.vertexY = this.vertexY
			}
			if (copyZ) {
				if (buffers!!.vertexZ == null || buffers.vertexZ!!.size < this.vertexCount) {
					buffers.vertexZ = IntArray(this.vertexCount)
					target.vertexZ = buffers.vertexZ
				} else {
					target.vertexZ = buffers.vertexZ
				}
				var vertexIndex: Int = 0
				while (vertexIndex < this.vertexCount) {
					target.vertexZ!![vertexIndex] = this.vertexZ!![vertexIndex]
					vertexIndex++
				}
			} else {
				target.vertexZ = this.vertexZ
			}
		} else {
			target.vertexX = this.vertexX
			target.vertexY = this.vertexY
			target.vertexZ = this.vertexZ
		}
		if ((functionMask and 0x84080) == 0) {
			target.faceColour = this.faceColour
		} else {
			if (buffers!!.faceColour == null || buffers.faceColour!!.size < this.faceCount) {
				val capacity: Int = this.faceCount
				buffers.faceColour = ShortArray(capacity)
				target.faceColour = buffers.faceColour
			} else {
				target.faceColour = buffers.faceColour
			}
			var faceIndex: Int = 0
			while (faceIndex < this.faceCount) {
				target.faceColour!![faceIndex] = this.faceColour!![faceIndex]
				faceIndex++
			}
		}
		if ((functionMask and 0x97018) != 0) {
			target.lightingState = 0
			target.faceColourC = null
			target.faceColourB = target.faceColourC
			target.faceColourA = target.faceColourB
		} else if ((functionMask and 0x80) == 0) {
			if (ensureLit) {
				this.applyLighting(false)
			}
			target.faceColourA = this.faceColourA
			target.faceColourB = this.faceColourB
			target.faceColourC = this.faceColourC
			target.lightingState = this.lightingState
		} else {
			if (ensureLit) {
				this.applyLighting(false)
			}
			if (this.faceColourA != null) {
				if (buffers!!.faceColourA == null || buffers.faceColourA!!.size < this.faceCount) {
					val capacity: Int = this.faceCount
					buffers.faceColourA = IntArray(capacity)
					target.faceColourA = buffers.faceColourA
					buffers.faceColourB = IntArray(capacity)
					target.faceColourB = buffers.faceColourB
					buffers.faceColourC = IntArray(capacity)
					target.faceColourC = buffers.faceColourC
				} else {
					target.faceColourA = buffers.faceColourA
					target.faceColourB = buffers.faceColourB
					target.faceColourC = buffers.faceColourC
				}
				var faceIndex: Int = 0
				while (faceIndex < this.faceCount) {
					target.faceColourA!![faceIndex] = this.faceColourA!![faceIndex]
					target.faceColourB!![faceIndex] = this.faceColourB!![faceIndex]
					target.faceColourC!![faceIndex] = this.faceColourC!![faceIndex]
					faceIndex++
				}
			}
			target.lightingState = this.lightingState
		}
		if ((functionMask and 0x100) == 0) {
			target.faceAlpha = this.faceAlpha
		} else {
			if (buffers!!.faceAlpha == null || buffers.faceAlpha!!.size < this.faceCount) {
				val capacity: Int = this.faceCount
				buffers.faceAlpha = ByteArray(capacity)
				target.faceAlpha = buffers.faceAlpha
			} else {
				target.faceAlpha = buffers.faceAlpha
			}
			if (this.faceAlpha == null) {
				var faceIndex: Int = 0
				while (faceIndex < this.faceCount) {
					target.faceAlpha!![faceIndex] = 0
					faceIndex++
				}
			} else {
				var faceIndex: Int = 0
				while (faceIndex < this.faceCount) {
					target.faceAlpha!![faceIndex] = this.faceAlpha!![faceIndex]
					faceIndex++
				}
			}
		}
		if ((functionMask and 0x8) == 0 && (functionMask and 0x10) == 0) {
			if (ensureLit) {
				this.ensureNormals()
			}
			target.lb = this.lb
			target.faceNormals = this.faceNormals
		} else {
			if (buffers!!.lb == null || buffers.lb!!.size < this.maxVertex) {
				val capacity: Int = this.maxVertex
				buffers.lb = arrayOfNulls<VertexNormal>(capacity)
				target.lb = buffers.lb
			} else {
				target.lb = buffers.lb
			}
			if (this.lb == null) {
				target.lb = null
			} else {
				var vertexIndex: Int = 0
				while (vertexIndex < this.maxVertex) {
					target.lb!![vertexIndex] = VertexNormal(this.lb!![vertexIndex])
					vertexIndex++
				}
			}
			if (this.faceNormals == null) {
				target.faceNormals = null
			} else {
				if (buffers.faceNormals == null || buffers.faceNormals!!.size < this.faceCount) {
					val capacity: Int = this.faceCount
					buffers.faceNormals = arrayOfNulls<FaceNormal>(capacity)
					target.faceNormals = buffers.faceNormals
				} else {
					target.faceNormals = buffers.faceNormals
				}
				var faceIndex: Int = 0
				while (faceIndex < this.faceCount) {
					target.faceNormals!![faceIndex] = if (this.faceNormals!![faceIndex] == null) null else FaceNormal(this.faceNormals!![faceIndex])
					faceIndex++
				}
			}
		}
		if ((functionMask and 0x8000) == 0) {
			target.faceTextures = this.faceTextures
		} else if (this.faceTextures == null) {
			target.faceTextures = null
		} else {
			if (buffers!!.faceTextures == null || buffers.faceTextures!!.size < this.faceCount) {
				val capacity: Int = this.faceCount
				buffers.faceTextures = ShortArray(capacity)
				target.faceTextures = buffers.faceTextures
			} else {
				target.faceTextures = buffers.faceTextures
			}
			var faceIndex: Int = 0
			while (faceIndex < this.faceCount) {
				target.faceTextures!![faceIndex] = this.faceTextures!![faceIndex]
				faceIndex++
			}
		}
		if ((functionMask and 0x10000) == 0) {
			target.shadingType = this.shadingType
		} else if (this.shadingType == null) {
			target.shadingType = null
		} else {
			if (buffers!!.shadingType == null || buffers.shadingType!!.size < this.faceCount) {
				val capacity: Int = if (reserve) this.faceCount + 100 else this.faceCount
				buffers.shadingType = ByteArray(capacity)
				target.shadingType = buffers.shadingType
			} else {
				target.shadingType = buffers.shadingType
			}
			var faceIndex: Int = 0
			while (faceIndex < this.faceCount) {
				target.shadingType!![faceIndex] = this.shadingType!![faceIndex]
				faceIndex++
			}
		}
		@Pc(803) var local803: Int
		if ((functionMask and 0xC580) == 0) {
			target.billboardAttributes = this.billboardAttributes
		} else if (buffers!!.billboardAttributes == null || buffers.billboardAttributes!!.size < this.billboardCount) {
			val capacity: Int = this.billboardCount
			buffers.billboardAttributes = arrayOfNulls<JavaBillboardAttributes>(capacity)
			target.billboardAttributes = buffers.billboardAttributes
			var billboardIndex: Int = 0
			while (billboardIndex < this.billboardCount) {
				target.billboardAttributes!![billboardIndex] = this.billboardAttributes!![billboardIndex]!!.copy()
				billboardIndex++
			}
		} else {
			target.billboardAttributes = buffers.billboardAttributes
			var billboardIndex: Int = 0
			while (billboardIndex < this.billboardCount) {
				target.billboardAttributes!![billboardIndex]!!.copyFrom(this.billboardAttributes!![billboardIndex])
				billboardIndex++
			}
		}
		if (this.texCoordU == null || (functionMask and 0x10) == 0) {
			target.texCoordU = this.texCoordU
			target.texCoordV = this.texCoordV
		} else {
			if (buffers!!.texCoordU == null || buffers.texCoordU!!.size < this.faceCount) {
				val capacity: Int = if (reserve) this.faceCount + 100 else this.faceCount
				buffers.texCoordU = Array<FloatArray?>(capacity) { FloatArray(3) }
				target.texCoordU = buffers.texCoordU
			} else {
				target.texCoordU = buffers.texCoordU
			}
			var faceIndex: Int = 0
			while (faceIndex < this.faceCount) {
				if (this.texCoordU!![faceIndex] != null) {
					target.texCoordU!![faceIndex]!![0] = this.texCoordU!![faceIndex]!![0]
					target.texCoordU!![faceIndex]!![1] = this.texCoordU!![faceIndex]!![1]
					target.texCoordU!![faceIndex]!![2] = this.texCoordU!![faceIndex]!![2]
				}
				faceIndex++
			}
			if (buffers.texCoordV == null || buffers.texCoordV!!.size < this.faceCount) {
				val capacity: Int = if (reserve) this.faceCount + 100 else this.faceCount
				buffers.texCoordV = Array<FloatArray?>(capacity) { FloatArray(3) }
				target.texCoordV = buffers.texCoordV
			} else {
				target.texCoordV = buffers.texCoordV
			}
			faceIndex = 0
			while (faceIndex < this.faceCount) {
				if (this.texCoordV!![faceIndex] != null) {
					target.texCoordV!![faceIndex]!![0] = this.texCoordV!![faceIndex]!![0]
					target.texCoordV!![faceIndex]!![1] = this.texCoordV!![faceIndex]!![1]
					target.texCoordV!![faceIndex]!![2] = this.texCoordV!![faceIndex]!![2]
				}
				faceIndex++
			}
		}
		target.vertexLabels = this.vertexLabels
		target.faceLabels = this.faceLabels
		target.billboardLabels = this.billboardLabels
		target.originModels = this.originModels
		target.faceOriginModels = this.faceOriginModels
		target.facePriority = this.facePriority
		target.faceA = this.faceA
		target.faceB = this.faceB
		target.faceC = this.faceC
		target.emitters = this.emitters
		target.effectors = this.effectors
		target.billboardFaces = this.billboardFaces
		target.faceIndices = this.faceIndices
		target.functionMask = functionMask
		return target
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Z)V")
	private fun applyLighting(@OriginalArg(0) discardShading: Boolean) {
		if (this.toolkit!!.threadCount > 1) {
			withLock(this) {
				this.calculateLighting(discardShading)
			}
		} else {
			this.calculateLighting(discardShading)
		}
	}

	@OriginalMember(owner = "client!jga", name = "d", descriptor = "()[Lclient!nn;")
	override fun getEmissiveTriangles(): Array<EmissiveTriangle?>? {
		return this.emitters
	}

	@OriginalMember(owner = "client!jga", name = "V", descriptor = "()I")
	override fun V(): Int {
		if (!this.boundsValid) {
			this.calculateBounds()
		}
		return this.minX.toInt()
	}

	@OriginalMember(owner = "client!jga", name = "c", descriptor = "(I)V")
	private fun drawTriangleRgb(@OriginalArg(0) face: Int) {
		@Pc(4) val a: Short = this.faceA!![face]
		@Pc(9) val b: Short = this.faceB!![face]
		@Pc(14) val c: Short = this.faceC!![face]
		if (this.faceTextures != null && this.faceTextures!![face].toInt() != -1) {
			@Pc(175) var alphaBits: Int = -16777216
			if (this.faceAlpha != null) {
				alphaBits = 255 - (this.faceAlpha!![face].toInt() and 0xFF) shl 24
			}
			if (this.faceColourC!![face] == -1) {
				@Pc(204) val argb: Int = alphaBits or (this.faceColourA!![face] and 0xFFFFFF)
				this.rasterizer!!.renderTexturedTriangleRgb(this.screenY!![a.toInt()].toFloat(), this.screenY!![b.toInt()].toFloat(), this.screenY!![c.toInt()].toFloat(), this.screenX!![a.toInt()].toFloat(), this.screenX!![b.toInt()].toFloat(), this.screenX!![c.toInt()].toFloat(), this.screenZ!![a.toInt()].toFloat(), this.screenZ!![b.toInt()].toFloat(), this.screenZ!![c.toInt()].toFloat(), this.texCoordU!![face]!![0], this.texCoordU!![face]!![1], this.texCoordU!![face]!![2], this.texCoordV!![face]!![0], this.texCoordV!![face]!![1], this.texCoordV!![face]!![2], argb, argb, argb, this.threadResource!!.fogColour, 0, 0, 0, this.faceTextures!![face].toInt())
			} else {
				this.rasterizer!!.renderTexturedTriangleRgb(this.screenY!![a.toInt()].toFloat(), this.screenY!![b.toInt()].toFloat(), this.screenY!![c.toInt()].toFloat(), this.screenX!![a.toInt()].toFloat(), this.screenX!![b.toInt()].toFloat(), this.screenX!![c.toInt()].toFloat(), this.screenZ!![a.toInt()].toFloat(), this.screenZ!![b.toInt()].toFloat(), this.screenZ!![c.toInt()].toFloat(), this.texCoordU!![face]!![0], this.texCoordU!![face]!![1], this.texCoordU!![face]!![2], this.texCoordV!![face]!![0], this.texCoordV!![face]!![1], this.texCoordV!![face]!![2], alphaBits or (this.faceColourA!![face] and 0xFFFFFF), alphaBits or (this.faceColourB!![face] and 0xFFFFFF), alphaBits or (this.faceColourC!![face] and 0xFFFFFF), this.threadResource!!.fogColour, 0, 0, 0, this.faceTextures!![face].toInt())
			}
			return
		}
		if (this.faceAlpha == null) {
			this.rasterizer!!.alpha = 0
		} else {
			this.rasterizer!!.alpha = this.faceAlpha!![face].toInt() and 0xFF
		}
		if (this.faceColourC!![face] == -1) {
			this.rasterizer!!.renderFlatTriangleRgb(this.screenY!![a.toInt()].toFloat(), this.screenY!![b.toInt()].toFloat(), this.screenY!![c.toInt()].toFloat(), this.screenX!![a.toInt()].toFloat(), this.screenX!![b.toInt()].toFloat(), this.screenX!![c.toInt()].toFloat(), this.screenZ!![a.toInt()].toFloat(), this.screenZ!![b.toInt()].toFloat(), this.screenZ!![c.toInt()].toFloat(), ColorUtil.hsvToRgbTable!![this.faceColourA!![face] and 0xFFFF])
		} else {
			this.rasterizer!!.renderTriangleHslRgb(this.screenY!![a.toInt()].toFloat(), this.screenY!![b.toInt()].toFloat(), this.screenY!![c.toInt()].toFloat(), this.screenX!![a.toInt()].toFloat(), this.screenX!![b.toInt()].toFloat(), this.screenX!![c.toInt()].toFloat(), this.screenZ!![a.toInt()].toFloat(), this.screenZ!![b.toInt()].toFloat(), this.screenZ!![c.toInt()].toFloat(), (this.faceColourA!![face] and 0xFFFF).toFloat(), (this.faceColourB!![face] and 0xFFFF).toFloat(), (this.faceColourC!![face] and 0xFFFF).toFloat())
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Lclient!bca;Lclient!iw;I)V")
	override fun render(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) cylinder: BoundingBoxPolygon?, @OriginalArg(2) flags: Int) {
		this.draw(matrix, cylinder, -1, flags)
	}

	@OriginalMember(owner = "client!jga", name = "HA", descriptor = "()I")
	override fun HA(): Int {
		if (!this.boundsValid) {
			this.calculateBounds()
		}
		return this.minZ.toInt()
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(I[IIIIIZ)V")
	override fun applyTransformUnmasked(@OriginalArg(0) type: Int, @OriginalArg(1) labels: IntArray?, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) z: Int, @OriginalArg(5) rotation: Int, @OriginalArg(6) lights: Boolean) {
		@Pc(2) val length: Int = labels!!.size
		@Pc(21) var local21: Int
		@Pc(67) var local67: Int
		@Pc(89) var local89: Int
		@Pc(8) var scaledX: Int
		@Pc(12) var scaledY: Int
		@Pc(16) var scaledZ: Int
		@Pc(84) var local84: Int
		if (type == 0) {
			scaledX = x shl 4
			scaledY = y shl 4
			scaledZ = z shl 4
			if (!this.highPrecisionVertices) {
				var vertexIndex: Int = 0
				while (vertexIndex < this.vertexCount) {
					this.vertexX!![vertexIndex] = this.vertexX!![vertexIndex] shl 0x4
					this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex] shl 0x4
					this.vertexZ!![vertexIndex] = this.vertexZ!![vertexIndex] shl 0x4
					vertexIndex++
				}
				this.highPrecisionVertices = true
			}
			var pivotCount: Int = 0
			this.pivotX = 0
			this.pivotY = 0
			this.pivotZ = 0
			var labelIndex: Int = 0
			while (labelIndex < length) {
				@Pc(72) val label: Int = labels[labelIndex]
				if (label < this.vertexLabels!!.size) {
					@Pc(82) val vertexGroup: IntArray? = this.vertexLabels!![label]
					var groupIndex: Int = 0
					while (groupIndex < vertexGroup!!.size) {
						val vertex: Int = vertexGroup[groupIndex]
						this.pivotX += this.vertexX!![vertex]
						this.pivotY += this.vertexY!![vertex]
						this.pivotZ += this.vertexZ!![vertex]
						pivotCount++
						groupIndex++
					}
				}
				labelIndex++
			}
			if (pivotCount > 0) {
				this.pivotX = this.pivotX / pivotCount + scaledX
				this.pivotY = this.pivotY / pivotCount + scaledY
				this.pivotZ = this.pivotZ / pivotCount + scaledZ
			} else {
				this.pivotX = scaledX
				this.pivotY = scaledY
				this.pivotZ = scaledZ
			}
			return
		}
		@Pc(233) var group: IntArray?
		@Pc(235) var j: Int
		if (type == 1) {
			scaledX = x shl 4
			scaledY = y shl 4
			scaledZ = z shl 4
			if (!this.highPrecisionVertices) {
				var vertexIndex: Int = 0
				while (vertexIndex < this.vertexCount) {
					this.vertexX!![vertexIndex] = this.vertexX!![vertexIndex] shl 0x4
					this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex] shl 0x4
					this.vertexZ!![vertexIndex] = this.vertexZ!![vertexIndex] shl 0x4
					vertexIndex++
				}
				this.highPrecisionVertices = true
			}
			var labelIndex: Int = 0
			while (labelIndex < length) {
				val label: Int = labels[labelIndex]
				if (label < this.vertexLabels!!.size) {
					group = this.vertexLabels!![label]
					j = 0
					while (j < group!!.size) {
						val vertex: Int = group[j]
						this.vertexX!![vertex] += scaledX
						this.vertexY!![vertex] += scaledY
						this.vertexZ!![vertex] += scaledZ
						j++
					}
				}
				labelIndex++
			}
			return
		}
		@Pc(342) var local342: Int
		@Pc(360) var local360: Int
		if (type == 2) {
			var labelIndex: Int = 0
			while (labelIndex < length) {
				val label: Int = labels[labelIndex]
				if (label < this.vertexLabels!!.size) {
					group = this.vertexLabels!![label]
					if ((rotation and 0x1) == 0) {
						j = 0
						while (j < group!!.size) {
							val vertex: Int = group[j]
							this.vertexX!![vertex] -= this.pivotX
							this.vertexY!![vertex] -= this.pivotY
							this.vertexZ!![vertex] -= this.pivotZ
							if (z != 0) {
								val sin: Int = TextureCombineSource.SINE[z]
								val cos: Int = TextureCombineSource.COSINE[z]
								val rotatedX: Int = this.vertexY!![vertex] * sin + this.vertexX!![vertex] * cos + 16383 shr 14
								this.vertexY!![vertex] = this.vertexY!![vertex] * cos + 16383 - this.vertexX!![vertex] * sin shr 14
								this.vertexX!![vertex] = rotatedX
							}
							if (x != 0) {
								val sin: Int = TextureCombineSource.SINE[x]
								val cos: Int = TextureCombineSource.COSINE[x]
								val rotatedY: Int = this.vertexY!![vertex] * cos + 16383 - this.vertexZ!![vertex] * sin shr 14
								this.vertexZ!![vertex] = this.vertexY!![vertex] * sin + this.vertexZ!![vertex] * cos + 16383 shr 14
								this.vertexY!![vertex] = rotatedY
							}
							if (y != 0) {
								val sin: Int = TextureCombineSource.SINE[y]
								val cos: Int = TextureCombineSource.COSINE[y]
								val rotatedX: Int = this.vertexZ!![vertex] * sin + this.vertexX!![vertex] * cos + 16383 shr 14
								this.vertexZ!![vertex] = this.vertexZ!![vertex] * cos + 16383 - this.vertexX!![vertex] * sin shr 14
								this.vertexX!![vertex] = rotatedX
							}
							this.vertexX!![vertex] += this.pivotX
							this.vertexY!![vertex] += this.pivotY
							this.vertexZ!![vertex] += this.pivotZ
							j++
						}
					} else {
						j = 0
						while (j < group!!.size) {
							val vertex: Int = group[j]
							this.vertexX!![vertex] -= this.pivotX
							this.vertexY!![vertex] -= this.pivotY
							this.vertexZ!![vertex] -= this.pivotZ
							if (x != 0) {
								val sin: Int = TextureCombineSource.SINE[x]
								val cos: Int = TextureCombineSource.COSINE[x]
								val rotatedY: Int = this.vertexY!![vertex] * cos + 16383 - this.vertexZ!![vertex] * sin shr 14
								this.vertexZ!![vertex] = this.vertexY!![vertex] * sin + this.vertexZ!![vertex] * cos + 16383 shr 14
								this.vertexY!![vertex] = rotatedY
							}
							if (z != 0) {
								val sin: Int = TextureCombineSource.SINE[z]
								val cos: Int = TextureCombineSource.COSINE[z]
								val rotatedX: Int = this.vertexY!![vertex] * sin + this.vertexX!![vertex] * cos + 16383 shr 14
								this.vertexY!![vertex] = this.vertexY!![vertex] * cos + 16383 - this.vertexX!![vertex] * sin shr 14
								this.vertexX!![vertex] = rotatedX
							}
							if (y != 0) {
								val sin: Int = TextureCombineSource.SINE[y]
								val cos: Int = TextureCombineSource.COSINE[y]
								val rotatedX: Int = this.vertexZ!![vertex] * sin + this.vertexX!![vertex] * cos + 16383 shr 14
								this.vertexZ!![vertex] = this.vertexZ!![vertex] * cos + 16383 - this.vertexX!![vertex] * sin shr 14
								this.vertexX!![vertex] = rotatedX
							}
							this.vertexX!![vertex] += this.pivotX
							this.vertexY!![vertex] += this.pivotY
							this.vertexZ!![vertex] += this.pivotZ
							j++
						}
					}
				}
				labelIndex++
			}
		} else if (type == 3) {
			var labelIndex: Int = 0
			while (labelIndex < length) {
				val label: Int = labels[labelIndex]
				if (label < this.vertexLabels!!.size) {
					group = this.vertexLabels!![label]
					j = 0
					while (j < group!!.size) {
						val vertex: Int = group[j]
						this.vertexX!![vertex] -= this.pivotX
						this.vertexY!![vertex] -= this.pivotY
						this.vertexZ!![vertex] -= this.pivotZ
						this.vertexX!![vertex] = this.vertexX!![vertex] * x / 128
						this.vertexY!![vertex] = this.vertexY!![vertex] * y / 128
						this.vertexZ!![vertex] = this.vertexZ!![vertex] * z / 128
						this.vertexX!![vertex] += this.pivotX
						this.vertexY!![vertex] += this.pivotY
						this.vertexZ!![vertex] += this.pivotZ
						j++
					}
				}
				labelIndex++
			}
		} else {
			@Pc(967) var javaBillboardFace: JavaBillboardFace?
			@Pc(972) var javaBillboardAttributes: JavaBillboardAttributes?
			if (type == 5) {
				if (this.faceLabels != null && this.faceAlpha != null) {
					var labelIndex: Int = 0
					while (labelIndex < length) {
						val label: Int = labels[labelIndex]
						if (label < this.faceLabels!!.size) {
							group = this.faceLabels!![label]
							j = 0
							while (j < group!!.size) {
								val face: Int = group[j]
								var alpha: Int = (this.faceAlpha!![face].toInt() and 0xFF) + x * 8
								if (alpha < 0) {
									alpha = 0
								} else if (alpha > 255) {
									alpha = 255
								}
								this.faceAlpha!![face] = alpha.toByte()
								j++
							}
						}
						labelIndex++
					}
					if (this.billboardFaces != null) {
						var billboardIndex: Int = 0
						while (billboardIndex < this.billboardCount) {
							javaBillboardFace = this.billboardFaces!![billboardIndex]
							javaBillboardAttributes = this.billboardAttributes!![billboardIndex]
							javaBillboardAttributes!!.diffuseColor = (javaBillboardAttributes.diffuseColor and 0xFFFFFF) or (255 - (this.faceAlpha!![javaBillboardFace!!.face].toInt() and 0xFF) shl 24)
							billboardIndex++
						}
					}
				}
			} else if (type != 7) {
				@Pc(1186) var javaBillboardAttributes2: JavaBillboardAttributes?
				if (type == 8) {
					if (this.billboardLabels != null) {
						var labelIndex: Int = 0
						while (labelIndex < length) {
							val label: Int = labels[labelIndex]
							if (label < this.billboardLabels!!.size) {
								group = this.billboardLabels!![label]
								j = 0
								while (j < group!!.size) {
									javaBillboardAttributes2 = this.billboardAttributes!![group[j]]
									javaBillboardAttributes2!!.offsetX += x
									javaBillboardAttributes2.offsetY += y
									j++
								}
							}
							labelIndex++
						}
					}
				} else if (type == 10) {
					if (this.billboardLabels != null) {
						var labelIndex: Int = 0
						while (labelIndex < length) {
							val label: Int = labels[labelIndex]
							if (label < this.billboardLabels!!.size) {
								group = this.billboardLabels!![label]
								j = 0
								while (j < group!!.size) {
									javaBillboardAttributes2 = this.billboardAttributes!![group[j]]
									javaBillboardAttributes2!!.scaleX = javaBillboardAttributes2.scaleX * x shr 7
									javaBillboardAttributes2.scaleY = javaBillboardAttributes2.scaleY * y shr 7
									j++
								}
							}
							labelIndex++
						}
					}
				} else if (type == 9 && this.billboardLabels != null) {
					var labelIndex: Int = 0
					while (labelIndex < length) {
						val label: Int = labels[labelIndex]
						if (label < this.billboardLabels!!.size) {
							group = this.billboardLabels!![label]
							j = 0
							while (j < group!!.size) {
								javaBillboardAttributes2 = this.billboardAttributes!![group[j]]
								javaBillboardAttributes2!!.rotation = javaBillboardAttributes2.rotation + x and 0x3FFF
								j++
							}
						}
						labelIndex++
					}
				}
			} else if (this.faceLabels != null) {
				var labelIndex: Int = 0
				while (labelIndex < length) {
					val label: Int = labels[labelIndex]
					if (label < this.faceLabels!!.size) {
						group = this.faceLabels!![label]
						j = 0
						while (j < group!!.size) {
							val face: Int = group[j]
							val hsl: Int = this.faceColour!![face].toInt() and 0xFFFF
							val oldHue: Int = (hsl shr 10) and 0x3F
							var saturation: Int = (hsl shr 7) and 0x7
							@Pc(1049) var lightness: Int = hsl and 0x7F
							@Pc(1055) val hue: Int = oldHue + x and 0x3F
							saturation += y
							if (saturation < 0) {
								saturation = 0
							} else if (saturation > 7) {
								saturation = 7
							}
							lightness += z
							if (lightness < 0) {
								lightness = 0
							} else if (lightness > 127) {
								lightness = 127
							}
							this.faceColour!![face] = ((hue shl 10) or (saturation shl 7) or lightness).toShort()
							j++
						}
						this.recoloured = true
					}
					labelIndex++
				}
				if (this.billboardFaces != null) {
					var billboardIndex: Int = 0
					while (billboardIndex < this.billboardCount) {
						javaBillboardFace = this.billboardFaces!![billboardIndex]
						javaBillboardAttributes = this.billboardAttributes!![billboardIndex]
						javaBillboardAttributes!!.diffuseColor = (javaBillboardAttributes.diffuseColor and 0xFF000000.toInt()) or (ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(this.faceColour!![javaBillboardFace!!.face].toInt() and 0xFFFF).toInt() and 0xFFFF] and 0xFFFFFF)
						billboardIndex++
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "FA", descriptor = "(I)V")
	override fun FA(@OriginalArg(0) angle: Int) {
		if ((this.functionMask and 0x6) != 6) {
			throw IllegalStateException()
		}
		@Pc(13) val sin: Int = TextureCombineSource.SINE[angle]
		@Pc(17) val cos: Int = TextureCombineSource.COSINE[angle]
		withLock(this) {
			@Pc(23) var i: Int = 0
			while (i < this.vertexCount) {
				@Pc(40) val newY: Int = this.vertexY!![i] * cos - this.vertexZ!![i] * sin shr 14
				this.vertexZ!![i] = this.vertexY!![i] * sin + this.vertexZ!![i] * cos shr 14
				this.vertexY!![i] = newY
				i++
			}
			this.invalidateNormals()
		}
	}

	@OriginalMember(owner = "client!jga", name = "f", descriptor = "()V")
	override fun lock() {
		if (this.toolkit!!.threadCount <= 1) {
			return
		}
		withLock(this) {
			while (super.isLocked) {
				try {
					lockWait(this)
				} catch (@Pc(13) ex: InterruptedException) {
				}
			}
			super.isLocked = true
		}
	}

	@OriginalMember(owner = "client!jga", name = "G", descriptor = "()I")
	override fun G(): Int {
		if (!this.boundsValid) {
			this.calculateBounds()
		}
		return this.maxZ.toInt()
	}

	@OriginalMember(owner = "client!jga", name = "I", descriptor = "(I[IIIIZI[I)V")
	override fun I(@OriginalArg(0) type: Int, @OriginalArg(1) labels: IntArray?, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) z: Int, @OriginalArg(5) lights: Boolean, @OriginalArg(6) submesh: Int, @OriginalArg(7) transform: IntArray?) {
		var x: Int = x
		var y: Int = y
		var z: Int = z
		@Pc(2) val length: Int = labels!!.size
		@Pc(21) var local21: Int
		@Pc(67) var local67: Int
		@Pc(89) var local89: Int
		@Pc(72) var local72: Int
		@Pc(84) var local84: Int
		if (type == 0) {
			x = x shl 0x4
			y = y shl 0x4
			z = z shl 0x4
			if (!this.highPrecisionVertices) {
				var vertexIndex: Int = 0
				while (vertexIndex < this.vertexCount) {
					this.vertexX!![vertexIndex] = this.vertexX!![vertexIndex] shl 0x4
					this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex] shl 0x4
					this.vertexZ!![vertexIndex] = this.vertexZ!![vertexIndex] shl 0x4
					vertexIndex++
				}
				this.highPrecisionVertices = true
			}
			var pivotCount: Int = 0
			this.pivotX = 0
			this.pivotY = 0
			this.pivotZ = 0
			var labelIndex: Int = 0
			while (labelIndex < length) {
				val label: Int = labels[labelIndex]
				if (label < this.vertexLabels!!.size) {
					@Pc(82) val vertexGroup: IntArray? = this.vertexLabels!![label]
					var groupIndex: Int = 0
					while (groupIndex < vertexGroup!!.size) {
						val vertex: Int = vertexGroup[groupIndex]
						if (this.originModels == null || (submesh and this.originModels!![vertex].toInt()) != 0) {
							this.pivotX += this.vertexX!![vertex]
							this.pivotY += this.vertexY!![vertex]
							this.pivotZ += this.vertexZ!![vertex]
							pivotCount++
						}
						groupIndex++
					}
				}
				labelIndex++
			}
			if (pivotCount > 0) {
				this.pivotX = this.pivotX / pivotCount + x
				this.pivotY = this.pivotY / pivotCount + y
				this.pivotZ = this.pivotZ / pivotCount + z
				this.pivotDirty = true
			} else {
				this.pivotX = x
				this.pivotY = y
				this.pivotZ = z
			}
			return
		}
		@Pc(320) var group: IntArray?
		@Pc(322) var local322: Int
		if (type == 1) {
			if (transform != null) {
				val transformedX: Int = transform[0] * x + transform[1] * y + transform[2] * z + 8192 shr 14
				val transformedY: Int = transform[3] * x + transform[4] * y + transform[5] * z + 8192 shr 14
				val transformedZ: Int = transform[6] * x + transform[7] * y + transform[8] * z + 8192 shr 14
				x = transformedX
				y = transformedY
				z = transformedZ
			}
			x = x shl 0x4
			y = y shl 0x4
			z = z shl 0x4
			if (!this.highPrecisionVertices) {
				var vertexIndex: Int = 0
				while (vertexIndex < this.vertexCount) {
					this.vertexX!![vertexIndex] = this.vertexX!![vertexIndex] shl 0x4
					this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex] shl 0x4
					this.vertexZ!![vertexIndex] = this.vertexZ!![vertexIndex] shl 0x4
					vertexIndex++
				}
				this.highPrecisionVertices = true
			}
			var labelIndex: Int = 0
			while (labelIndex < length) {
				val label: Int = labels[labelIndex]
				if (label < this.vertexLabels!!.size) {
					group = this.vertexLabels!![label]
					var groupIndex: Int = 0
					while (groupIndex < group!!.size) {
						val vertex: Int = group[groupIndex]
						if (this.originModels == null || (submesh and this.originModels!![vertex].toInt()) != 0) {
							this.vertexX!![vertex] += x
							this.vertexY!![vertex] += y
							this.vertexZ!![vertex] += z
						}
						groupIndex++
					}
				}
				labelIndex++
			}
			return
		}
		@Pc(478) var local478: Int
		@Pc(503) var local503: Int
		@Pc(528) var local528: Int
		@Pc(567) var local567: Int
		@Pc(571) var local571: Int
		@Pc(575) var local575: Int
		@Pc(579) var local579: Int
		@Pc(587) var local587: Int
		@Pc(595) var local595: Int
		@Pc(754) var local754: Int
		@Pc(782) var local782: Int
		@Pc(787) var local787: Int
		@Pc(797) var local797: Int
		@Pc(802) var local802: Int
		@Pc(805) var local805: Int
		@Pc(808) var local808: Int
		@Pc(810) var local810: Int
		@Pc(934) var local934: IntArray?
		@Pc(936) var local936: Int
		@Pc(939) var local939: Int
		@Pc(942) var local942: Int
		@Pc(944) var local944: Int
		@Pc(1067) var local1067: Int
		if (type == 2) {
			if (transform == null) {
				var labelIndex: Int = 0
				while (labelIndex < length) {
					val label: Int = labels[labelIndex]
					if (label < this.vertexLabels!!.size) {
						group = this.vertexLabels!![label]
						var groupIndex: Int = 0
						while (groupIndex < group!!.size) {
							val vertex: Int = group[groupIndex]
							if (this.originModels == null || (submesh and this.originModels!![vertex].toInt()) != 0) {
								this.vertexX!![vertex] -= this.pivotX
								this.vertexY!![vertex] -= this.pivotY
								this.vertexZ!![vertex] -= this.pivotZ
								if (z != 0) {
									val sin: Int = TextureCombineSource.SINE[z]
									val cos: Int = TextureCombineSource.COSINE[z]
									val rotatedX: Int = this.vertexY!![vertex] * sin + this.vertexX!![vertex] * cos + 16383 shr 14
									this.vertexY!![vertex] = this.vertexY!![vertex] * cos + 16383 - this.vertexX!![vertex] * sin shr 14
									this.vertexX!![vertex] = rotatedX
								}
								if (x != 0) {
									val sin: Int = TextureCombineSource.SINE[x]
									val cos: Int = TextureCombineSource.COSINE[x]
									val rotatedY: Int = this.vertexY!![vertex] * cos + 16383 - this.vertexZ!![vertex] * sin shr 14
									this.vertexZ!![vertex] = this.vertexY!![vertex] * sin + this.vertexZ!![vertex] * cos + 16383 shr 14
									this.vertexY!![vertex] = rotatedY
								}
								if (y != 0) {
									val sin: Int = TextureCombineSource.SINE[y]
									val cos: Int = TextureCombineSource.COSINE[y]
									val rotatedX: Int = this.vertexZ!![vertex] * sin + this.vertexX!![vertex] * cos + 16383 shr 14
									this.vertexZ!![vertex] = this.vertexZ!![vertex] * cos + 16383 - this.vertexX!![vertex] * sin shr 14
									this.vertexX!![vertex] = rotatedX
								}
								this.vertexX!![vertex] += this.pivotX
								this.vertexY!![vertex] += this.pivotY
								this.vertexZ!![vertex] += this.pivotZ
							}
							groupIndex++
						}
					}
					labelIndex++
				}
			} else {
				if (!this.highPrecisionVertices) {
					var vertexIndex: Int = 0
					while (vertexIndex < this.vertexCount) {
						this.vertexX!![vertexIndex] = this.vertexX!![vertexIndex] shl 0x4
						this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex] shl 0x4
						this.vertexZ!![vertexIndex] = this.vertexZ!![vertexIndex] shl 0x4
						vertexIndex++
					}
					this.highPrecisionVertices = true
				}
				val translationX: Int = transform[9] shl 4
				val translationY: Int = transform[10] shl 4
				val translationZ: Int = transform[11] shl 4
				val deltaX: Int = transform[12] shl 4
				val deltaY: Int = transform[13] shl 4
				val deltaZ: Int = transform[14] shl 4
				if (this.pivotDirty) {
					var newPivotX: Int = transform[0] * this.pivotX + transform[3] * this.pivotY + transform[6] * this.pivotZ + 8192 shr 14
					var newPivotY: Int = transform[1] * this.pivotX + transform[4] * this.pivotY + transform[7] * this.pivotZ + 8192 shr 14
					var newPivotZ: Int = transform[2] * this.pivotX + transform[5] * this.pivotY + transform[8] * this.pivotZ + 8192 shr 14
					newPivotX += deltaX
					newPivotY += deltaY
					newPivotZ += deltaZ
					this.pivotX = newPivotX
					this.pivotY = newPivotY
					this.pivotZ = newPivotZ
					this.pivotDirty = false
				}
				@Pc(555) val matrix: IntArray? = IntArray(9)
				val cosX: Int = TextureCombineSource.COSINE[x]
				val sinX: Int = TextureCombineSource.SINE[x]
				val cosY: Int = TextureCombineSource.COSINE[y]
				val sinY: Int = TextureCombineSource.SINE[y]
				val cosZ: Int = TextureCombineSource.COSINE[z]
				val sinZ: Int = TextureCombineSource.SINE[z]
				val sinXCosZ: Int = sinX * cosZ + 8192 shr 14
				val sinXSinZ: Int = sinX * sinZ + 8192 shr 14
				matrix!![0] = cosY * cosZ + sinY * sinXSinZ + 8192 shr 14
				matrix[1] = -cosY * sinZ + sinY * sinXCosZ + 8192 shr 14
				matrix[2] = sinY * cosX + 8192 shr 14
				matrix[3] = cosX * sinZ + 8192 shr 14
				matrix[4] = cosX * cosZ + 8192 shr 14
				matrix[5] = -sinX
				matrix[6] = -sinY * cosZ + cosY * sinXSinZ + 8192 shr 14
				matrix[7] = sinY * sinZ + cosY * sinXCosZ + 8192 shr 14
				matrix[8] = cosY * cosX + 8192 shr 14
				@Pc(726) val rotatedNegPivotX: Int = matrix[0] * -this.pivotX + matrix[1] * -this.pivotY + matrix[2] * -this.pivotZ + 8192 shr 14
				val rotatedNegPivotY: Int = matrix[3] * -this.pivotX + matrix[4] * -this.pivotY + matrix[5] * -this.pivotZ + 8192 shr 14
				val rotatedNegPivotZ: Int = matrix[6] * -this.pivotX + matrix[7] * -this.pivotY + matrix[8] * -this.pivotZ + 8192 shr 14
				val translateX: Int = rotatedNegPivotX + this.pivotX
				@Pc(792) val translateY: Int = rotatedNegPivotY + this.pivotY
				val translateZ: Int = rotatedNegPivotZ + this.pivotZ
				@Pc(800) val combinedMatrix: IntArray? = IntArray(9)
				var row: Int = 0
				while (row < 3) {
					var column: Int = 0
					while (column < 3) {
						var sum: Int = 0
						var k: Int = 0
						while (k < 3) {
							sum += matrix[row * 3 + k] * transform[column * 3 + k]
							k++
						}
						combinedMatrix!![row * 3 + column] = sum + 8192 shr 14
						column++
					}
					row++
				}
				var fullOffsetX: Int = matrix[0] * deltaX + matrix[1] * deltaY + matrix[2] * deltaZ + 8192 shr 14
				var fullOffsetY: Int = matrix[3] * deltaX + matrix[4] * deltaY + matrix[5] * deltaZ + 8192 shr 14
				var fullOffsetZ: Int = matrix[6] * deltaX + matrix[7] * deltaY + matrix[8] * deltaZ + 8192 shr 14
				fullOffsetX += translateX
				fullOffsetY += translateY
				fullOffsetZ += translateZ
				val finalMatrix: IntArray? = IntArray(9)
				row = 0
				while (row < 3) {
					var column: Int = 0
					while (column < 3) {
						var sum: Int = 0
						var k: Int = 0
						while (k < 3) {
							sum += transform[row * 3 + k] * combinedMatrix!![column + k * 3]
							k++
						}
						finalMatrix!![row * 3 + column] = sum + 8192 shr 14
						column++
					}
					row++
				}
				var vertexOffsetX: Int = transform[0] * fullOffsetX + transform[1] * fullOffsetY + transform[2] * fullOffsetZ + 8192 shr 14
				var vertexOffsetY: Int = transform[3] * fullOffsetX + transform[4] * fullOffsetY + transform[5] * fullOffsetZ + 8192 shr 14
				var vertexOffsetZ: Int = transform[6] * fullOffsetX + transform[7] * fullOffsetY + transform[8] * fullOffsetZ + 8192 shr 14
				vertexOffsetX += translationX
				vertexOffsetY += translationY
				vertexOffsetZ += translationZ
				var labelIndex: Int = 0
				while (labelIndex < length) {
					@Pc(1072) val label: Int = labels[labelIndex]
					if (label < this.vertexLabels!!.size) {
						@Pc(1082) val vertices: IntArray? = this.vertexLabels!![label]
						@Pc(1084) var i: Int = 0
						while (i < vertices!!.size) {
							@Pc(1089) val vertex: Int = vertices[i]
							if (this.originModels == null || (submesh and this.originModels!![vertex].toInt()) != 0) {
								@Pc(1130) val rotatedX: Int = finalMatrix!![0] * this.vertexX!![vertex] + finalMatrix[1] * this.vertexY!![vertex] + finalMatrix[2] * this.vertexZ!![vertex] + 8192 shr 14
								@Pc(1161) val rotatedY: Int = finalMatrix[3] * this.vertexX!![vertex] + finalMatrix[4] * this.vertexY!![vertex] + finalMatrix[5] * this.vertexZ!![vertex] + 8192 shr 14
								@Pc(1192) val rotatedZ: Int = finalMatrix[6] * this.vertexX!![vertex] + finalMatrix[7] * this.vertexY!![vertex] + finalMatrix[8] * this.vertexZ!![vertex] + 8192 shr 14
								@Pc(1196) val newX: Int = rotatedX + vertexOffsetX
								@Pc(1200) val newY: Int = rotatedY + vertexOffsetY
								@Pc(1204) val newZ: Int = rotatedZ + vertexOffsetZ
								this.vertexX!![vertex] = newX
								this.vertexY!![vertex] = newY
								this.vertexZ!![vertex] = newZ
							}
							i++
						}
					}
					labelIndex++
				}
			}
		} else if (type != 3) {
			@Pc(2402) var javaBillboardFace: JavaBillboardFace?
			@Pc(2407) var javaBillboardAttributes: JavaBillboardAttributes?
			if (type == 5) {
				if (this.faceLabels != null && this.faceAlpha != null) {
					var labelIndex: Int = 0
					while (labelIndex < length) {
						val label: Int = labels[labelIndex]
						if (label < this.faceLabels!!.size) {
							group = this.faceLabels!![label]
							var groupIndex: Int = 0
							while (groupIndex < group!!.size) {
								val face: Int = group[groupIndex]
								if (this.faceOriginModels == null || (submesh and this.faceOriginModels!![face].toInt()) != 0) {
									var alpha: Int = (this.faceAlpha!![face].toInt() and 0xFF) + x * 8
									if (alpha < 0) {
										alpha = 0
									} else if (alpha > 255) {
										alpha = 255
									}
									this.faceAlpha!![face] = alpha.toByte()
								}
								groupIndex++
							}
						}
						labelIndex++
					}
					if (this.billboardFaces != null) {
						var billboardIndex: Int = 0
						while (billboardIndex < this.billboardCount) {
							javaBillboardFace = this.billboardFaces!![billboardIndex]
							javaBillboardAttributes = this.billboardAttributes!![billboardIndex]
							javaBillboardAttributes!!.diffuseColor = (javaBillboardAttributes.diffuseColor and 0xFFFFFF) or (255 - (this.faceAlpha!![javaBillboardFace!!.face].toInt() and 0xFF) shl 24)
							billboardIndex++
						}
					}
				}
			} else if (type != 7) {
				@Pc(2631) var javaBillboardAttributes2: JavaBillboardAttributes?
				if (type == 8) {
					if (this.billboardLabels != null) {
						var labelIndex: Int = 0
						while (labelIndex < length) {
							val label: Int = labels[labelIndex]
							if (label < this.billboardLabels!!.size) {
								group = this.billboardLabels!![label]
								var groupIndex: Int = 0
								while (groupIndex < group!!.size) {
									javaBillboardAttributes2 = this.billboardAttributes!![group[groupIndex]]
									javaBillboardAttributes2!!.offsetX += x
									javaBillboardAttributes2.offsetY += y
									groupIndex++
								}
							}
							labelIndex++
						}
					}
				} else if (type == 10) {
					if (this.billboardLabels != null) {
						var labelIndex: Int = 0
						while (labelIndex < length) {
							val label: Int = labels[labelIndex]
							if (label < this.billboardLabels!!.size) {
								group = this.billboardLabels!![label]
								var groupIndex: Int = 0
								while (groupIndex < group!!.size) {
									javaBillboardAttributes2 = this.billboardAttributes!![group[groupIndex]]
									javaBillboardAttributes2!!.scaleX = javaBillboardAttributes2.scaleX * x shr 7
									javaBillboardAttributes2.scaleY = javaBillboardAttributes2.scaleY * y shr 7
									groupIndex++
								}
							}
							labelIndex++
						}
					}
				} else if (type == 9 && this.billboardLabels != null) {
					var labelIndex: Int = 0
					while (labelIndex < length) {
						val label: Int = labels[labelIndex]
						if (label < this.billboardLabels!!.size) {
							group = this.billboardLabels!![label]
							var groupIndex: Int = 0
							while (groupIndex < group!!.size) {
								javaBillboardAttributes2 = this.billboardAttributes!![group[groupIndex]]
								javaBillboardAttributes2!!.rotation = javaBillboardAttributes2.rotation + x and 0x3FFF
								groupIndex++
							}
						}
						labelIndex++
					}
				}
			} else if (this.faceLabels != null) {
				var labelIndex: Int = 0
				while (labelIndex < length) {
					val label: Int = labels[labelIndex]
					if (label < this.faceLabels!!.size) {
						group = this.faceLabels!![label]
						var groupIndex: Int = 0
						while (groupIndex < group!!.size) {
							val face: Int = group[groupIndex]
							if (this.faceOriginModels == null || (submesh and this.faceOriginModels!![face].toInt()) != 0) {
								val hsl: Int = this.faceColour!![face].toInt() and 0xFFFF
								val oldHue: Int = (hsl shr 10) and 0x3F
								var saturation: Int = (hsl shr 7) and 0x7
								var lightness: Int = hsl and 0x7F
								@Pc(2500) val hue: Int = oldHue + x and 0x3F
								saturation += y
								if (saturation < 0) {
									saturation = 0
								} else if (saturation > 7) {
									saturation = 7
								}
								lightness += z
								if (lightness < 0) {
									lightness = 0
								} else if (lightness > 127) {
									lightness = 127
								}
								this.faceColour!![face] = ((hue shl 10) or (saturation shl 7) or lightness).toShort()
							}
							groupIndex++
						}
						this.recoloured = true
					}
					labelIndex++
				}
				if (this.billboardFaces != null) {
					var billboardIndex: Int = 0
					while (billboardIndex < this.billboardCount) {
						javaBillboardFace = this.billboardFaces!![billboardIndex]
						javaBillboardAttributes = this.billboardAttributes!![billboardIndex]
						javaBillboardAttributes!!.diffuseColor = (javaBillboardAttributes.diffuseColor and 0xFF000000.toInt()) or (ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(this.faceColour!![javaBillboardFace!!.face].toInt() and 0xFFFF).toInt() and 0xFFFF] and 0xFFFFFF)
						billboardIndex++
					}
				}
			}
		} else if (transform == null) {
			var labelIndex: Int = 0
			while (labelIndex < length) {
				val label: Int = labels[labelIndex]
				if (label < this.vertexLabels!!.size) {
					group = this.vertexLabels!![label]
					var groupIndex: Int = 0
					while (groupIndex < group!!.size) {
						val vertex: Int = group[groupIndex]
						if (this.originModels == null || (submesh and this.originModels!![vertex].toInt()) != 0) {
							this.vertexX!![vertex] -= this.pivotX
							this.vertexY!![vertex] -= this.pivotY
							this.vertexZ!![vertex] -= this.pivotZ
							this.vertexX!![vertex] = this.vertexX!![vertex] * x / 128
							this.vertexY!![vertex] = this.vertexY!![vertex] * y / 128
							this.vertexZ!![vertex] = this.vertexZ!![vertex] * z / 128
							this.vertexX!![vertex] += this.pivotX
							this.vertexY!![vertex] += this.pivotY
							this.vertexZ!![vertex] += this.pivotZ
						}
						groupIndex++
					}
				}
				labelIndex++
			}
		} else {
			if (!this.highPrecisionVertices) {
				var vertexIndex: Int = 0
				while (vertexIndex < this.vertexCount) {
					this.vertexX!![vertexIndex] = this.vertexX!![vertexIndex] shl 0x4
					this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex] shl 0x4
					this.vertexZ!![vertexIndex] = this.vertexZ!![vertexIndex] shl 0x4
					vertexIndex++
				}
				this.highPrecisionVertices = true
			}
			val translationX: Int = transform[9] shl 4
			val translationY: Int = transform[10] shl 4
			val translationZ: Int = transform[11] shl 4
			val deltaX: Int = transform[12] shl 4
			val deltaY: Int = transform[13] shl 4
			val deltaZ: Int = transform[14] shl 4
			if (this.pivotDirty) {
				var newPivotX: Int = transform[0] * this.pivotX + transform[3] * this.pivotY + transform[6] * this.pivotZ + 8192 shr 14
				var newPivotY: Int = transform[1] * this.pivotX + transform[4] * this.pivotY + transform[7] * this.pivotZ + 8192 shr 14
				var newPivotZ: Int = transform[2] * this.pivotX + transform[5] * this.pivotY + transform[8] * this.pivotZ + 8192 shr 14
				newPivotX += deltaX
				newPivotY += deltaY
				newPivotZ += deltaZ
				this.pivotX = newPivotX
				this.pivotY = newPivotY
				this.pivotZ = newPivotZ
				this.pivotDirty = false
			}
			val scaleX: Int = (x shl 15) shr 7
			val scaleY: Int = (y shl 15) shr 7
			val scaleZ: Int = (z shl 15) shr 7
			val scaledNegPivotX: Int = scaleX * -this.pivotX + 8192 shr 14
			val scaledNegPivotY: Int = scaleY * -this.pivotY + 8192 shr 14
			val scaledNegPivotZ: Int = scaleZ * -this.pivotZ + 8192 shr 14
			val pivotShiftX: Int = scaledNegPivotX + this.pivotX
			val pivotShiftY: Int = scaledNegPivotY + this.pivotY
			val pivotShiftZ: Int = scaledNegPivotZ + this.pivotZ
			@Pc(1736) val scaledMatrix: IntArray? = intArrayOf(scaleX * transform[0] + 8192 shr 14, scaleX * transform[3] + 8192 shr 14, scaleX * transform[6] + 8192 shr 14, scaleY * transform[1] + 8192 shr 14, scaleY * transform[4] + 8192 shr 14, scaleY * transform[7] + 8192 shr 14, scaleZ * transform[2] + 8192 shr 14, scaleZ * transform[5] + 8192 shr 14, scaleZ * transform[8] + 8192 shr 14)
			val scaledDeltaX: Int = scaleX * deltaX + 8192 shr 14
			val scaledDeltaY: Int = scaleY * deltaY + 8192 shr 14
			val scaledDeltaZ: Int = scaleZ * deltaZ + 8192 shr 14
			@Pc(1872) val translateX: Int = scaledDeltaX + pivotShiftX
			@Pc(1876) val translateY: Int = scaledDeltaY + pivotShiftY
			@Pc(1880) val translateZ: Int = scaledDeltaZ + pivotShiftZ
			@Pc(1883) val finalMatrix: IntArray? = IntArray(9)
			@Pc(1888) var local1888: Int
			var row: Int = 0
			while (row < 3) {
				var column: Int = 0
				while (column < 3) {
					var sum: Int = 0
					var k: Int = 0
					while (k < 3) {
						sum += transform[row * 3 + k] * scaledMatrix!![column + k * 3]
						k++
					}
					finalMatrix!![row * 3 + column] = sum + 8192 shr 14
					column++
				}
				row++
			}
			var vertexOffsetX: Int = transform[0] * translateX + transform[1] * translateY + transform[2] * translateZ + 8192 shr 14
			var vertexOffsetY: Int = transform[3] * translateX + transform[4] * translateY + transform[5] * translateZ + 8192 shr 14
			var vertexOffsetZ: Int = transform[6] * translateX + transform[7] * translateY + transform[8] * translateZ + 8192 shr 14
			vertexOffsetX += translationX
			vertexOffsetY += translationY
			vertexOffsetZ += translationZ
			var labelIndex: Int = 0
			while (labelIndex < length) {
				val label: Int = labels[labelIndex]
				if (label < this.vertexLabels!!.size) {
					val vertices: IntArray? = this.vertexLabels!![label]
					var groupIndex: Int = 0
					while (groupIndex < vertices!!.size) {
						val vertex: Int = vertices[groupIndex]
						if (this.originModels == null || (submesh and this.originModels!![vertex].toInt()) != 0) {
							val rotatedX: Int = finalMatrix!![0] * this.vertexX!![vertex] + finalMatrix[1] * this.vertexY!![vertex] + finalMatrix[2] * this.vertexZ!![vertex] + 8192 shr 14
							val rotatedY: Int = finalMatrix[3] * this.vertexX!![vertex] + finalMatrix[4] * this.vertexY!![vertex] + finalMatrix[5] * this.vertexZ!![vertex] + 8192 shr 14
							val rotatedZ: Int = finalMatrix[6] * this.vertexX!![vertex] + finalMatrix[7] * this.vertexY!![vertex] + finalMatrix[8] * this.vertexZ!![vertex] + 8192 shr 14
							@Pc(2145) val newX: Int = rotatedX + vertexOffsetX
							@Pc(2149) val newY: Int = rotatedY + vertexOffsetY
							@Pc(2153) val newZ: Int = rotatedZ + vertexOffsetZ
							this.vertexX!![vertex] = newX
							this.vertexY!![vertex] = newY
							this.vertexZ!![vertex] = newZ
						}
						groupIndex++
					}
				}
				labelIndex++
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "h", descriptor = "()V")
	private fun ensureNormals() {
		if (this.lightingState != 0 || this.lb != null) {
			return
		}
		if (this.toolkit!!.threadCount > 1) {
			withLock(this) {
				this.calculateNormals()
			}
		} else {
			this.calculateNormals()
		}
	}

	@OriginalMember(owner = "client!jga", name = "wa", descriptor = "()V")
	override fun wa() {
		if (this.highPrecisionVertices) {
			@Pc(4) var i: Int = 0
			while (i < this.vertexCount) {
				this.vertexX!![i] = this.vertexX!![i] + 7 shr 4
				this.vertexY!![i] = this.vertexY!![i] + 7 shr 4
				this.vertexZ!![i] = this.vertexZ!![i] + 7 shr 4
				i++
			}
			this.highPrecisionVertices = false
		}
		if (this.recoloured) {
			this.relight()
			this.recoloured = false
		}
		this.boundsValid = false
	}

	@OriginalMember(owner = "client!jga", name = "fa", descriptor = "()I")
	override fun fa(): Int {
		if (!this.boundsValid) {
			this.calculateBounds()
		}
		return this.minY.toInt()
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(II)I")
	private fun shadeHsl(@OriginalArg(0) hsl: Int, @OriginalArg(1) lightness: Int): Int {
		var lightness: Int = lightness
		lightness = lightness * (hsl and 0x7F) shr 7
		if (lightness < 2) {
			lightness = 2
		} else if (lightness > 126) {
			lightness = 126
		}
		return (hsl and 0xFF80) + lightness
	}

	@OriginalMember(owner = "client!jga", name = "d", descriptor = "(I)Z")
	private fun hasBillboard(@OriginalArg(0) face: Int): Boolean {
		if (this.faceBillboard == null) {
			return false
		} else {
			return this.faceBillboard!![face] != -1
		}
	}

	@OriginalMember(owner = "client!jga", name = "da", descriptor = "()I")
	override fun da(): Int {
		return this.contrast
	}

	@OriginalMember(owner = "client!jga", name = "i", descriptor = "()V")
	private fun rotate270() {
		withLock(this) {
			@Pc(5) var i: Int = 0
			while (i < this.vertexCount) {
				@Pc(11) val oldZ: Int = this.vertexZ!![i]
				this.vertexZ!![i] = this.vertexX!![i]
				this.vertexX!![i] = -oldZ
				i++
			}
			this.invalidateNormals()
		}
	}

	@OriginalMember(owner = "client!jga", name = "c", descriptor = "()Z")
	override fun loadedTextures(): Boolean {
		if (this.faceTextures == null) {
			return true
		}
		@Pc(6) var i: Int = 0
		while (i < this.faceTextures!!.size) {
			if (this.faceTextures!![i].toInt() != -1 && !this.toolkit!!.textureAvailable(this.faceTextures!![i].toInt())) {
				return false
			}
			i++
		}
		return true
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Lclient!bca;)V")
	override fun updateParticlePosition(@OriginalArg(0) matrix: Matrix?) {
		@Pc(2) val javaMatrix: JavaMatrix? = matrix as JavaMatrix?
		@Pc(7) var i: Int
		if (this.emitters != null) {
			i = 0
			while (i < this.emitters!!.size) {
				@Pc(13) val modelParticleEmitter: EmissiveTriangle? = this.emitters!![i]
				@Pc(15) var modelParticleEmitter2: EmissiveTriangle? = modelParticleEmitter
				if (modelParticleEmitter!!.child != null) {
					modelParticleEmitter2 = modelParticleEmitter.child
				}
				modelParticleEmitter2!!.verticeZC = (javaMatrix!!.tx + javaMatrix.e1_1 * this.vertexX!![modelParticleEmitter.faceA].toFloat() + javaMatrix.e1_2 * this.vertexY!![modelParticleEmitter.faceA].toFloat() + javaMatrix.e1_3 * this.vertexZ!![modelParticleEmitter.faceA].toFloat()).toInt()
				modelParticleEmitter2.verticeXC = (javaMatrix.ty + javaMatrix.e2_1 * this.vertexX!![modelParticleEmitter.faceA].toFloat() + javaMatrix.e2_2 * this.vertexY!![modelParticleEmitter.faceA].toFloat() + javaMatrix.e2_3 * this.vertexZ!![modelParticleEmitter.faceA].toFloat()).toInt()
				modelParticleEmitter2.verticeYA = (javaMatrix.tz + javaMatrix.e3_1 * this.vertexX!![modelParticleEmitter.faceA].toFloat() + javaMatrix.e3_2 * this.vertexY!![modelParticleEmitter.faceA].toFloat() + javaMatrix.e3_3 * this.vertexZ!![modelParticleEmitter.faceA].toFloat()).toInt()
				modelParticleEmitter2.verticeZA = (javaMatrix.tx + javaMatrix.e1_1 * this.vertexX!![modelParticleEmitter.faceB].toFloat() + javaMatrix.e1_2 * this.vertexY!![modelParticleEmitter.faceB].toFloat() + javaMatrix.e1_3 * this.vertexZ!![modelParticleEmitter.faceB].toFloat()).toInt()
				modelParticleEmitter2.verticeXA = (javaMatrix.ty + javaMatrix.e2_1 * this.vertexX!![modelParticleEmitter.faceB].toFloat() + javaMatrix.e2_2 * this.vertexY!![modelParticleEmitter.faceB].toFloat() + javaMatrix.e2_3 * this.vertexZ!![modelParticleEmitter.faceB].toFloat()).toInt()
				modelParticleEmitter2.verticeZB = (javaMatrix.tz + javaMatrix.e3_1 * this.vertexX!![modelParticleEmitter.faceB].toFloat() + javaMatrix.e3_2 * this.vertexY!![modelParticleEmitter.faceB].toFloat() + javaMatrix.e3_3 * this.vertexZ!![modelParticleEmitter.faceB].toFloat()).toInt()
				modelParticleEmitter2.verticeYB = (javaMatrix.tx + javaMatrix.e1_1 * this.vertexX!![modelParticleEmitter.vertexC].toFloat() + javaMatrix.e1_2 * this.vertexY!![modelParticleEmitter.vertexC].toFloat() + javaMatrix.e1_3 * this.vertexZ!![modelParticleEmitter.vertexC].toFloat()).toInt()
				modelParticleEmitter2.transformedCY = (javaMatrix.ty + javaMatrix.e2_1 * this.vertexX!![modelParticleEmitter.vertexC].toFloat() + javaMatrix.e2_2 * this.vertexY!![modelParticleEmitter.vertexC].toFloat() + javaMatrix.e2_3 * this.vertexZ!![modelParticleEmitter.vertexC].toFloat()).toInt()
				modelParticleEmitter2.verticeYC = (javaMatrix.tz + javaMatrix.e3_1 * this.vertexX!![modelParticleEmitter.vertexC].toFloat() + javaMatrix.e3_2 * this.vertexY!![modelParticleEmitter.vertexC].toFloat() + javaMatrix.e3_3 * this.vertexZ!![modelParticleEmitter.vertexC].toFloat()).toInt()
				i++
			}
		}
		if (this.effectors == null) {
			return
		}
		i = 0
		while (i < this.effectors!!.size) {
			@Pc(353) val modelParticleEffector: EffectiveVertex? = this.effectors!![i]
			@Pc(355) var modelParticleEffector2: EffectiveVertex? = modelParticleEffector
			if (modelParticleEffector!!.child != null) {
				modelParticleEffector2 = modelParticleEffector.child
			}
			if (modelParticleEffector.matrix == null) {
				modelParticleEffector.matrix = javaMatrix!!.copy()
			} else {
				modelParticleEffector.matrix!!.apply(javaMatrix)
			}
			modelParticleEffector2!!.verticeZ = (javaMatrix!!.tx + javaMatrix.e1_1 * this.vertexX!![modelParticleEffector.appliesOnVertex].toFloat() + javaMatrix.e1_2 * this.vertexY!![modelParticleEffector.appliesOnVertex].toFloat() + javaMatrix.e1_3 * this.vertexZ!![modelParticleEffector.appliesOnVertex].toFloat()).toInt()
			modelParticleEffector2.verticeX = (javaMatrix.ty + javaMatrix.e2_1 * this.vertexX!![modelParticleEffector.appliesOnVertex].toFloat() + javaMatrix.e2_2 * this.vertexY!![modelParticleEffector.appliesOnVertex].toFloat() + javaMatrix.e2_3 * this.vertexZ!![modelParticleEffector.appliesOnVertex].toFloat()).toInt()
			modelParticleEffector2.verticeY = (javaMatrix.tz + javaMatrix.e3_1 * this.vertexX!![modelParticleEffector.appliesOnVertex].toFloat() + javaMatrix.e3_2 * this.vertexY!![modelParticleEffector.appliesOnVertex].toFloat() + javaMatrix.e3_3 * this.vertexZ!![modelParticleEffector.appliesOnVertex].toFloat()).toInt()
			i++
		}
	}

	@OriginalMember(owner = "client!jga", name = "j", descriptor = "()V")
	private fun relightColours() {
		@Pc(1) var face: Int = 0
		while (face < this.faceCount) {
			@Pc(12) val texture: Short = if (this.faceTextures == null) -1 else this.faceTextures!![face]
			if (texture.toInt() == -1) {
				@Pc(22) val hsl: Int = this.faceColour!![face].toInt() and 0xFFFF
				@Pc(31) val lightness: Int = (hsl and 0x7F) * this.ambient shr 7
				@Pc(39) val hsv: Short = ColorUtil.hslToHsv((hsl and 0xFFFFFF80.toInt()) or lightness)
				@Pc(52) var shade: Int
				if (this.faceColourC!![face] == -1) {
					shade = this.faceColourA!![face] and 0xFFFE0000.toInt()
					this.faceColourA!![face] = shade or JavaModel.applyLightness(shade shr 17, hsv.toInt())
				} else if (this.faceColourC!![face] != -2) {
					shade = this.faceColourA!![face] and 0xFFFE0000.toInt()
					this.faceColourA!![face] = shade or JavaModel.applyLightness(shade shr 17, hsv.toInt())
					shade = this.faceColourB!![face] and 0xFFFE0000.toInt()
					this.faceColourB!![face] = shade or JavaModel.applyLightness(shade shr 17, hsv.toInt())
					shade = this.faceColourC!![face] and 0xFFFE0000.toInt()
					this.faceColourC!![face] = shade or JavaModel.applyLightness(shade shr 17, hsv.toInt())
				}
			}
			face++
		}
		this.lightingState = 2
	}

	@OriginalMember(owner = "client!jga", name = "k", descriptor = "(I)V")
	override fun k(@OriginalArg(0) angle: Int) {
		if ((this.functionMask and 0xD) != 13) {
			throw IllegalStateException()
		} else if (this.lb == null) {
			this.a(angle)
		} else if (angle == 4096) {
			this.rotate90WithNormals()
		} else if (angle == 8192) {
			this.rotate180WithNormals()
		} else if (angle == 12288) {
			this.rotate270WithNormals()
		} else {
			@Pc(34) val sin: Int = TextureCombineSource.SINE[angle]
			@Pc(38) val cos: Int = TextureCombineSource.COSINE[angle]
			withLock(this) {
				@Pc(61) var local61: Int
				@Pc(44) var i: Int = 0
				while (i < this.maxVertex) {
					var rotatedX: Int = this.vertexZ!![i] * sin + this.vertexX!![i] * cos shr 14
					this.vertexZ!![i] = this.vertexZ!![i] * cos - this.vertexX!![i] * sin shr 14
					this.vertexX!![i] = rotatedX
					if (this.lb!![i] != null) {
						rotatedX = this.lb!![i]!!.z * sin + this.lb!![i]!!.x * cos shr 14
						this.lb!![i]!!.z = this.lb!![i]!!.z * cos - this.lb!![i]!!.x * sin shr 14
						this.lb!![i]!!.x = rotatedX
					}
					i++
				}
				@Pc(170) var newX: Int
				if (this.faceNormals != null) {
					var faceIndex: Int = 0
					while (faceIndex < this.faceCount) {
						if (this.faceNormals!![faceIndex] != null) {
							newX = this.faceNormals!![faceIndex]!!.z * sin + this.faceNormals!![faceIndex]!!.x * cos shr 14
							this.faceNormals!![faceIndex]!!.z = this.faceNormals!![faceIndex]!!.z * cos - this.faceNormals!![faceIndex]!!.x * sin shr 14
							this.faceNormals!![faceIndex]!!.x = newX
						}
						faceIndex++
					}
				}
				var vertexIndex: Int = this.maxVertex
				while (vertexIndex < this.vertexCount) {
					newX = this.vertexZ!![vertexIndex] * sin + this.vertexX!![vertexIndex] * cos shr 14
					this.vertexZ!![vertexIndex] = this.vertexZ!![vertexIndex] * cos - this.vertexX!![vertexIndex] * sin shr 14
					this.vertexX!![vertexIndex] = newX
					vertexIndex++
				}
				this.lightingState = 0
				this.boundsValid = false
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Lclient!bca;IZ)V")
	override fun transform(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) originMask: Int, @OriginalArg(2) relative: Boolean) {
		if (this.originModels == null) {
			return
		}
		@Pc(6) val projected: IntArray? = IntArray(3)
		@Pc(8) var i: Int = 0
		while (i < this.maxVertex) {
			if ((originMask and this.originModels!![i].toInt()) != 0) {
				if (relative) {
					matrix!!.projectRelative(this.vertexX!![i], this.vertexY!![i], this.vertexZ!![i], projected)
				} else {
					matrix!!.method4652(this.vertexX!![i], this.vertexY!![i], this.vertexZ!![i], projected)
				}
				this.vertexX!![i] = projected!![0]
				this.vertexY!![i] = projected[1]
				this.vertexZ!![i] = projected[2]
			}
			i++
		}
	}

	@OriginalMember(owner = "client!jga", name = "b", descriptor = "()V")
	override fun uploadToGpu() {
	}

	@OriginalMember(owner = "client!jga", name = "k", descriptor = "()V")
	private fun rotate270WithNormals() {
		withLock(this) {
			@Pc(11) var local11: Int
			@Pc(5) var i: Int = 0
			while (i < this.maxVertex) {
				var swapZ: Int = this.vertexZ!![i]
				this.vertexZ!![i] = this.vertexX!![i]
				this.vertexX!![i] = -swapZ
				if (this.lb!![i] != null) {
					swapZ = this.lb!![i]!!.z
					this.lb!![i]!!.z = this.lb!![i]!!.x
					this.lb!![i]!!.x = -swapZ
				}
				i++
			}
			@Pc(75) var oldZ: Int
			if (this.faceNormals != null) {
				var faceIndex: Int = 0
				while (faceIndex < this.faceCount) {
					if (this.faceNormals!![faceIndex] != null) {
						oldZ = this.faceNormals!![faceIndex]!!.z
						this.faceNormals!![faceIndex]!!.z = this.faceNormals!![faceIndex]!!.x
						this.faceNormals!![faceIndex]!!.x = -oldZ
					}
					faceIndex++
				}
			}
			var vertexIndex: Int = this.maxVertex
			while (vertexIndex < this.vertexCount) {
				oldZ = this.vertexZ!![vertexIndex]
				this.vertexZ!![vertexIndex] = this.vertexX!![vertexIndex]
				this.vertexX!![vertexIndex] = -oldZ
				vertexIndex++
			}
			this.lightingState = 0
			this.boundsValid = false
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(ISI)I")
	private fun shadeTexturedRgb(@OriginalArg(0) hsl: Int, @OriginalArg(1) texture: Short, @OriginalArg(2) lightness: Int): Int {
		@Pc(6) var rgb: Int = ColorUtil.hslToRgb!![this.shadeHsl(hsl, lightness)]
		@Pc(15) val metrics: MaterialType? = this.toolkit!!.materialProvider!!.getMetrics(texture.toInt() and 0xFFFF)
		@Pc(20) val alpha: Int = metrics!!.alpha.toInt() and 0xFF
		@Pc(26) var local26: Int
		@Pc(38) var local38: Int
		if (alpha != 0) {
			val greyRgb: Int = lightness * 131586
			if (alpha == 256) {
				rgb = greyRgb
			} else {
				val inverseAlpha: Int = 256 - alpha
				rgb = ((greyRgb and 0xFF00FF) * alpha + (rgb and 0xFF00FF) * inverseAlpha and 0xFF00FF00.toInt()) + ((greyRgb and 0xFF00) * alpha + (rgb and 0xFF00) * inverseAlpha and 0xFF0000) shr 8
			}
		}
		var brightness: Int = metrics.brightness.toInt() and 0xFF
		if (brightness != 0) {
			brightness += 256
			@Pc(84) var red: Int = ((rgb shr 16) and 0xFF) * brightness
			if (red > 65535) {
				red = 65535
			}
			var green: Int = ((rgb shr 8) and 0xFF) * brightness
			if (green > 65535) {
				green = 65535
			}
			@Pc(108) var blue: Int = (rgb and 0xFF) * brightness
			if (blue > 65535) {
				blue = 65535
			}
			rgb = ((red and 0xFF00) shl 8) + (green and 0xFF00) + (blue shr 8)
		}
		return rgb
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Lclient!bca;Lclient!iw;II)V")
	override fun renderOrtho(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) cylinder: BoundingBoxPolygon?, @OriginalArg(2) orthoDepth: Int, @OriginalArg(3) flags: Int) {
		this.draw(matrix, cylinder, orthoDepth, flags)
	}

	@OriginalMember(owner = "client!jga", name = "NA", descriptor = "()Z")
	override fun NA(): Boolean {
		if (this.vertexLabels == null) {
			return false
		} else {
			this.pivotX = 0
			this.pivotY = 0
			this.pivotZ = 0
			return true
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(IZZ)V")
	private fun drawFaceRgb(@OriginalArg(0) face: Int, @OriginalArg(1) nearClipped: Boolean, @OriginalArg(2) fogged: Boolean) {
		if (this.faceColourC!![face] == -2) {
			return
		}
		@Pc(11) val a: Short = this.faceA!![face]
		@Pc(16) val b: Short = this.faceB!![face]
		@Pc(21) val c: Short = this.faceC!![face]
		@Pc(26) val screenXA: Int = this.screenX!![a.toInt()]
		@Pc(31) val screenXB: Int = this.screenX!![b.toInt()]
		@Pc(36) val screenXC: Int = this.screenX!![c.toInt()]
		@Pc(52) var local52: Int
		if (nearClipped && (screenXA == -5000 || screenXB == -5000 || screenXC == -5000)) {
			val xA: Int = this.cameraX!![a.toInt()]
			@Pc(57) val xB: Int = this.cameraX!![b.toInt()]
			@Pc(62) val xC: Int = this.cameraX!![c.toInt()]
			@Pc(67) val yA: Int = this.cameraY!![a.toInt()]
			@Pc(72) val yB: Int = this.cameraY!![b.toInt()]
			@Pc(77) val yC: Int = this.cameraY!![c.toInt()]
			@Pc(82) val zA: Int = this.cameraZ!![a.toInt()]
			@Pc(87) val zB: Int = this.cameraZ!![b.toInt()]
			@Pc(92) val zC: Int = this.cameraZ!![c.toInt()]
			@Pc(96) val abX: Int = xA - xB
			@Pc(100) val cbX: Int = xC - xB
			@Pc(104) val abY: Int = yA - yB
			@Pc(108) val cbY: Int = yC - yB
			@Pc(112) val abZ: Int = zA - zB
			@Pc(116) val cbZ: Int = zC - zB
			@Pc(124) val normalX: Int = abY * cbZ - abZ * cbY
			@Pc(132) val normalY: Int = abZ * cbX - abX * cbZ
			@Pc(140) val normalZ: Int = abX * cbY - abY * cbX
			if (xB * normalX + yB * normalY + zB * normalZ > 0) {
				this.drawClippedTriangleRgb(face)
				return
			}
		} else if (this.faceBillboard!![face] != -1 || (screenXA - screenXB) * (this.screenY!![c.toInt()] - this.screenY!![b.toInt()]) - (this.screenY!![a.toInt()] - this.screenY!![b.toInt()]) * (screenXC - screenXB) > 0) {
			if (screenXA >= 0 && screenXB >= 0 && screenXC >= 0 && screenXA <= this.threadResource!!.rasterWidth && screenXB <= this.threadResource!!.rasterWidth && screenXC <= this.threadResource!!.rasterWidth) {
				this.rasterizer!!.clamp = false
			} else {
				this.rasterizer!!.clamp = true
			}
			if (fogged) {
				val billboardIndex: Int = this.faceBillboard!![face]
				if (billboardIndex == -1 || !this.billboardFaces!![billboardIndex]!!.hideFace) {
					this.drawFoggedTriangleRgb(face)
				}
				return
			}
			val billboardIndex: Int = this.faceBillboard!![face]
			if (billboardIndex != -1) {
				@Pc(253) val javaBillboardFace: JavaBillboardFace? = this.billboardFaces!![billboardIndex]
				@Pc(258) val javaBillboardAttributes: JavaBillboardAttributes? = this.billboardAttributes!![billboardIndex]
				if (!javaBillboardFace!!.hideFace) {
					this.drawTriangleRgb(face)
				}
				this.toolkit!!.drawBillboardRgb(javaBillboardAttributes!!.x, javaBillboardAttributes.y, javaBillboardAttributes.z, javaBillboardAttributes.projectedWidth, javaBillboardAttributes.projectedHeight, javaBillboardAttributes.rotation, javaBillboardFace.texture.toInt() and 0xFFFF, javaBillboardAttributes.diffuseColor, javaBillboardFace.colourOp.toInt(), javaBillboardFace.blendMode.toInt())
				return
			}
			this.drawTriangleRgb(face)
		}
	}

	@OriginalMember(owner = "client!jga", name = "F", descriptor = "()Z")
	override fun F(): Boolean {
		return this.transparent
	}

	@OriginalMember(owner = "client!jga", name = "P", descriptor = "(IIII)V")
	override fun P(@OriginalArg(0) type: Int, @OriginalArg(1) pivotX: Int, @OriginalArg(2) pivotY: Int, @OriginalArg(3) pivotZ: Int) {
		@Pc(3) var local3: Int
		@Pc(14) var local14: Int
		if (type == 0) {
			var pivotCount: Int = 0
			this.pivotX = 0
			this.pivotY = 0
			this.pivotZ = 0
			var vertexIndex: Int = 0
			while (vertexIndex < this.vertexCount) {
				this.pivotX += this.vertexX!![vertexIndex]
				this.pivotY += this.vertexY!![vertexIndex]
				this.pivotZ += this.vertexZ!![vertexIndex]
				pivotCount++
				vertexIndex++
			}
			if (pivotCount > 0) {
				this.pivotX = this.pivotX / pivotCount + pivotX
				this.pivotY = this.pivotY / pivotCount + pivotY
				this.pivotZ = this.pivotZ / pivotCount + pivotZ
			} else {
				this.pivotX = pivotX
				this.pivotY = pivotY
				this.pivotZ = pivotZ
			}
		} else if (type == 1) {
			var vertexIndex: Int = 0
			while (vertexIndex < this.vertexCount) {
				this.vertexX!![vertexIndex] += pivotX
				this.vertexY!![vertexIndex] += pivotY
				this.vertexZ!![vertexIndex] += pivotZ
				vertexIndex++
			}
		} else {
			@Pc(164) var local164: Int
			@Pc(182) var local182: Int
			if (type == 2) {
				var vertexIndex: Int = 0
				while (vertexIndex < this.vertexCount) {
					this.vertexX!![vertexIndex] -= this.pivotX
					this.vertexY!![vertexIndex] -= this.pivotY
					this.vertexZ!![vertexIndex] -= this.pivotZ
					if (pivotZ != 0) {
						val sin: Int = TextureCombineSource.SINE[pivotZ]
						val cos: Int = TextureCombineSource.COSINE[pivotZ]
						val rotatedX: Int = this.vertexY!![vertexIndex] * sin + this.vertexX!![vertexIndex] * cos + 16383 shr 14
						this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex] * cos + 16383 - this.vertexX!![vertexIndex] * sin shr 14
						this.vertexX!![vertexIndex] = rotatedX
					}
					if (pivotX != 0) {
						val sin: Int = TextureCombineSource.SINE[pivotX]
						val cos: Int = TextureCombineSource.COSINE[pivotX]
						val rotatedY: Int = this.vertexY!![vertexIndex] * cos + 16383 - this.vertexZ!![vertexIndex] * sin shr 14
						this.vertexZ!![vertexIndex] = this.vertexY!![vertexIndex] * sin + this.vertexZ!![vertexIndex] * cos + 16383 shr 14
						this.vertexY!![vertexIndex] = rotatedY
					}
					if (pivotY != 0) {
						val sin: Int = TextureCombineSource.SINE[pivotY]
						val cos: Int = TextureCombineSource.COSINE[pivotY]
						val rotatedX: Int = this.vertexZ!![vertexIndex] * sin + this.vertexX!![vertexIndex] * cos + 16383 shr 14
						this.vertexZ!![vertexIndex] = this.vertexZ!![vertexIndex] * cos + 16383 - this.vertexX!![vertexIndex] * sin shr 14
						this.vertexX!![vertexIndex] = rotatedX
					}
					this.vertexX!![vertexIndex] += this.pivotX
					this.vertexY!![vertexIndex] += this.pivotY
					this.vertexZ!![vertexIndex] += this.pivotZ
					vertexIndex++
				}
			} else if (type == 3) {
				var vertexIndex: Int = 0
				while (vertexIndex < this.vertexCount) {
					this.vertexX!![vertexIndex] -= this.pivotX
					this.vertexY!![vertexIndex] -= this.pivotY
					this.vertexZ!![vertexIndex] -= this.pivotZ
					this.vertexX!![vertexIndex] = this.vertexX!![vertexIndex] * pivotX / 128
					this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex] * pivotY / 128
					this.vertexZ!![vertexIndex] = this.vertexZ!![vertexIndex] * pivotZ / 128
					this.vertexX!![vertexIndex] += this.pivotX
					this.vertexY!![vertexIndex] += this.pivotY
					this.vertexZ!![vertexIndex] += this.pivotZ
					vertexIndex++
				}
			} else {
				@Pc(500) var javaBillboardFace: JavaBillboardFace?
				@Pc(505) var javaBillboardAttributes: JavaBillboardAttributes?
				if (type == 5) {
					var faceIndex: Int = 0
					while (faceIndex < this.faceCount) {
						var alpha: Int = (this.faceAlpha!![faceIndex].toInt() and 0xFF) + pivotX * 8
						if (alpha < 0) {
							alpha = 0
						} else if (alpha > 255) {
							alpha = 255
						}
						this.faceAlpha!![faceIndex] = alpha.toByte()
						faceIndex++
					}
					if (this.billboardFaces != null) {
						var billboardIndex: Int = 0
						while (billboardIndex < this.billboardCount) {
							javaBillboardFace = this.billboardFaces!![billboardIndex]
							javaBillboardAttributes = this.billboardAttributes!![billboardIndex]
							javaBillboardAttributes!!.diffuseColor = (javaBillboardAttributes.diffuseColor and 0xFFFFFF) or (255 - (this.faceAlpha!![javaBillboardFace!!.face].toInt() and 0xFF) shl 24)
							billboardIndex++
						}
					}
				} else if (type == 7) {
					var faceIndex: Int = 0
					while (faceIndex < this.faceCount) {
						val hsl: Int = this.faceColour!![faceIndex].toInt() and 0xFFFF
						val oldHue: Int = (hsl shr 10) and 0x3F
						var saturation: Int = (hsl shr 7) and 0x7
						@Pc(558) var lightness: Int = hsl and 0x7F
						@Pc(564) val hue: Int = oldHue + pivotX and 0x3F
						saturation += pivotY
						if (saturation < 0) {
							saturation = 0
						} else if (saturation > 7) {
							saturation = 7
						}
						lightness += pivotZ
						if (lightness < 0) {
							lightness = 0
						} else if (lightness > 127) {
							lightness = 127
						}
						this.faceColour!![faceIndex] = ((hue shl 10) or (saturation shl 7) or lightness).toShort()
						faceIndex++
					}
					this.recoloured = true
					if (this.billboardFaces != null) {
						var billboardIndex: Int = 0
						while (billboardIndex < this.billboardCount) {
							javaBillboardFace = this.billboardFaces!![billboardIndex]
							javaBillboardAttributes = this.billboardAttributes!![billboardIndex]
							javaBillboardAttributes!!.diffuseColor = (javaBillboardAttributes.diffuseColor and 0xFF000000.toInt()) or (ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(this.faceColour!![javaBillboardFace!!.face].toInt() and 0xFFFF).toInt() and 0xFFFF] and 0xFFFFFF)
							billboardIndex++
						}
					}
				} else {
					@Pc(669) var javaBillboardAttributes2: JavaBillboardAttributes?
					if (type == 8) {
						var billboardIndex: Int = 0
						while (billboardIndex < this.billboardCount) {
							javaBillboardAttributes2 = this.billboardAttributes!![billboardIndex]
							javaBillboardAttributes2!!.offsetX += pivotX
							javaBillboardAttributes2.offsetY += pivotY
							billboardIndex++
						}
					} else if (type == 10) {
						var billboardIndex: Int = 0
						while (billboardIndex < this.billboardCount) {
							javaBillboardAttributes2 = this.billboardAttributes!![billboardIndex]
							javaBillboardAttributes2!!.scaleX = javaBillboardAttributes2.scaleX * pivotX shr 7
							javaBillboardAttributes2.scaleY = javaBillboardAttributes2.scaleY * pivotY shr 7
							billboardIndex++
						}
					} else if (type == 9) {
						var billboardIndex: Int = 0
						while (billboardIndex < this.billboardCount) {
							javaBillboardAttributes2 = this.billboardAttributes!![billboardIndex]
							javaBillboardAttributes2!!.rotation = javaBillboardAttributes2.rotation + pivotX and 0x3FFF
							billboardIndex++
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "na", descriptor = "()I")
	override fun na(): Int {
		if (!this.boundsValid) {
			this.calculateBounds()
		}
		return this.radius.toInt()
	}

	@OriginalMember(owner = "client!jga", name = "ba", descriptor = "(Lclient!r;)Lclient!r;")
	override fun ba(@OriginalArg(0) shadow: Shadow?): Shadow? {
		return null
	}

	@OriginalMember(owner = "client!jga", name = "p", descriptor = "(IILclient!s;Lclient!s;III)V")
	override fun p(@OriginalArg(0) contourType: Int, @OriginalArg(1) contourModifier: Int, @OriginalArg(2) ground: Ground?, @OriginalArg(3) ground2: Ground?, @OriginalArg(4) x: Int, @OriginalArg(5) y: Int, @OriginalArg(6) z: Int) {
		if (contourType == 3) {
			if ((this.functionMask and 0x7) != 7) {
				throw IllegalStateException()
			}
		} else if ((this.functionMask and 0x2) != 2) {
			throw IllegalStateException()
		}
		if (!this.boundsValid) {
			this.calculateBounds()
		}
		@Pc(32) var minTileX: Int = x + this.minX.toInt()
		@Pc(37) var maxTileX: Int = x + this.maxX.toInt()
		@Pc(42) var minTileZ: Int = z + this.minZ.toInt()
		@Pc(47) var maxTileZ: Int = z + this.maxZ.toInt()
		if (contourType != 4 && (minTileX < 0 || (maxTileX + ground!!.tileUnits shr ground.tileScale) >= ground.width || minTileZ < 0 || (maxTileZ + ground.tileUnits shr ground.tileScale) >= ground.length)) {
			return
		}
		@Pc(79) val tileHeights: Array<IntArray?>? = (ground as JavaGround?)!!.tileHeights
		@Pc(81) var otherHeights: Array<IntArray?>? = null
		if (ground2 != null) {
			otherHeights = (ground2 as JavaGround?)!!.tileHeights
		}
		if (contourType == 4 || contourType == 5) {
			if (ground2 == null) {
				return
			}
			if (minTileX < 0 || (maxTileX + ground2.tileUnits shr ground2.tileScale) >= ground2.width || minTileZ < 0 || (maxTileZ + ground2.tileUnits shr ground2.tileScale) >= ground2.length) {
				return
			}
		} else {
			minTileX = minTileX shr ground.tileScale
			maxTileX = maxTileX + ground.tileUnits - 1 shr ground.tileScale
			minTileZ = minTileZ shr ground.tileScale
			maxTileZ = maxTileZ + ground.tileUnits - 1 shr ground.tileScale
			if (tileHeights!![minTileX]!![minTileZ] == y && tileHeights[maxTileX]!![minTileZ] == y && tileHeights[minTileX]!![maxTileZ] == y && tileHeights[maxTileX]!![maxTileZ] == y) {
				return
			}
		}
		withLock(this) {
			@Pc(192) var local192: Int
			@Pc(194) var local194: Int
			@Pc(202) var local202: Int
			@Pc(209) var local209: Int
			@Pc(213) var local213: Int
			@Pc(217) var local217: Int
			@Pc(222) var local222: Int
			@Pc(227) var local227: Int
			@Pc(251) var local251: Int
			@Pc(279) var local279: Int
			@Pc(293) var local293: Int
			@Pc(431) var local431: Int
			if (contourType == 1) {
				val tileMask: Int = ground.tileUnits - 1
				var vertexIndex: Int = 0
				while (vertexIndex < this.maxVertex) {
					val worldX: Int = this.vertexX!![vertexIndex] + x
					val worldZ: Int = this.vertexZ!![vertexIndex] + z
					val fracX: Int = worldX and tileMask
					val fracZ: Int = worldZ and tileMask
					val tileX: Int = worldX shr ground.tileScale
					val tileZ: Int = worldZ shr ground.tileScale
					val lowerHeight: Int = tileHeights!![tileX]!![tileZ] * (ground.tileUnits - fracX) + tileHeights[tileX + 1]!![tileZ] * fracX shr ground.tileScale
					val upperHeight: Int = tileHeights[tileX]!![tileZ + 1] * (ground.tileUnits - fracX) + tileHeights[tileX + 1]!![tileZ + 1] * fracX shr ground.tileScale
					val groundHeight: Int = lowerHeight * (ground.tileUnits - fracZ) + upperHeight * fracZ shr ground.tileScale
					this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex] + groundHeight - y
					vertexIndex++
				}
				vertexIndex = this.maxVertex
				while (vertexIndex < this.vertexCount) {
					val worldX: Int = this.vertexX!![vertexIndex] + x
					val worldZ: Int = this.vertexZ!![vertexIndex] + z
					val fracX: Int = worldX and tileMask
					val fracZ: Int = worldZ and tileMask
					val tileX: Int = worldX shr ground.tileScale
					val tileZ: Int = worldZ shr ground.tileScale
					if (tileX >= 0 && tileX < tileHeights!!.size - 1 && tileZ >= 0 && tileZ < tileHeights[0]!!.size - 1) {
						val lowerHeight: Int = tileHeights[tileX]!![tileZ] * (ground.tileUnits - fracX) + tileHeights[tileX + 1]!![tileZ] * fracX shr ground.tileScale
						val upperHeight: Int = tileHeights[tileX]!![tileZ + 1] * (ground.tileUnits - fracX) + tileHeights[tileX + 1]!![tileZ + 1] * fracX shr ground.tileScale
						val groundHeight: Int = lowerHeight * (ground.tileUnits - fracZ) + upperHeight * fracZ shr ground.tileScale
						this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex] + groundHeight - y
					}
					vertexIndex++
				}
			} else {
				@Pc(735) var local735: Int
				if (contourType == 2) {
					val tileMask: Int = ground.tileUnits - 1
					var vertexIndex: Int = 0
					while (vertexIndex < this.maxVertex) {
						val heightRatio: Int = (this.vertexY!![vertexIndex] shl 16) / this.minY.toInt()
						if (heightRatio < contourModifier) {
							val worldX: Int = this.vertexX!![vertexIndex] + x
							val worldZ: Int = this.vertexZ!![vertexIndex] + z
							val fracX: Int = worldX and tileMask
							val fracZ: Int = worldZ and tileMask
							val tileX: Int = worldX shr ground.tileScale
							val tileZ: Int = worldZ shr ground.tileScale
							val lowerHeight: Int = tileHeights!![tileX]!![tileZ] * (ground.tileUnits - fracX) + tileHeights[tileX + 1]!![tileZ] * fracX shr ground.tileScale
							val upperHeight: Int = tileHeights[tileX]!![tileZ + 1] * (ground.tileUnits - fracX) + tileHeights[tileX + 1]!![tileZ + 1] * fracX shr ground.tileScale
							val groundHeight: Int = lowerHeight * (ground.tileUnits - fracZ) + upperHeight * fracZ shr ground.tileScale
							this.vertexY!![vertexIndex] += (groundHeight - y) * (contourModifier - heightRatio) / contourModifier
						} else {
							this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex]
						}
						vertexIndex++
					}
					vertexIndex = this.maxVertex
					while (vertexIndex < this.vertexCount) {
						val heightRatio: Int = (this.vertexY!![vertexIndex] shl 16) / this.minY.toInt()
						if (heightRatio < contourModifier) {
							val worldX: Int = this.vertexX!![vertexIndex] + x
							val worldZ: Int = this.vertexZ!![vertexIndex] + z
							val fracX: Int = worldX and tileMask
							val fracZ: Int = worldZ and tileMask
							val tileX: Int = worldX shr ground.tileScale
							val tileZ: Int = worldZ shr ground.tileScale
							if (tileX >= 0 && tileX < ground.width - 1 && tileZ >= 0 && tileZ < ground.length - 1) {
								val lowerHeight: Int = tileHeights!![tileX]!![tileZ] * (ground.tileUnits - fracX) + tileHeights[tileX + 1]!![tileZ] * fracX shr ground.tileScale
								val upperHeight: Int = tileHeights[tileX]!![tileZ + 1] * (ground.tileUnits - fracX) + tileHeights[tileX + 1]!![tileZ + 1] * fracX shr ground.tileScale
								val groundHeight: Int = lowerHeight * (ground.tileUnits - fracZ) + upperHeight * fracZ shr ground.tileScale
								this.vertexY!![vertexIndex] += (groundHeight - y) * (contourModifier - heightRatio) / contourModifier
							}
						} else {
							this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex]
						}
						vertexIndex++
					}
				} else if (contourType == 3) {
					val sizeX: Int = (contourModifier and 0xFF) * 4
					val sizeZ: Int = ((contourModifier shr 8) and 0xFF) * 4
					val maxRotateX: Int = ((contourModifier shr 16) and 0xFF) shl 6
					val maxRotateZ: Int = ((contourModifier shr 24) and 0xFF) shl 6
					if (x - (sizeX shr 1) < 0 || x + (sizeX shr 1) + ground.tileUnits >= (ground.width shl ground.tileScale) || z - (sizeZ shr 1) < 0 || z + (sizeZ shr 1) + ground.tileUnits >= (ground.length shl ground.tileScale)) {
						return
					}
					this.hillChange(sizeX, maxRotateZ, x, sizeZ, y, maxRotateX, ground, z)
				} else if (contourType == 4) {
					val tileMask: Int = ground2!!.tileUnits - 1
					val modelHeight: Int = this.maxY.toInt() - this.minY.toInt()
					var vertexIndex: Int = 0
					while (vertexIndex < this.maxVertex) {
						val worldX: Int = this.vertexX!![vertexIndex] + x
						val worldZ: Int = this.vertexZ!![vertexIndex] + z
						val fracX: Int = worldX and tileMask
						val fracZ: Int = worldZ and tileMask
						val tileX: Int = worldX shr ground2.tileScale
						val tileZ: Int = worldZ shr ground2.tileScale
						val lowerHeight: Int = otherHeights!![tileX]!![tileZ] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1]!![tileZ] * fracX shr ground2.tileScale
						val upperHeight: Int = otherHeights[tileX]!![tileZ + 1] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1]!![tileZ + 1] * fracX shr ground2.tileScale
						val groundHeight: Int = lowerHeight * (ground2.tileUnits - fracZ) + upperHeight * fracZ shr ground2.tileScale
						this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex] + groundHeight + modelHeight - y
						vertexIndex++
					}
					vertexIndex = this.maxVertex
					while (vertexIndex < this.vertexCount) {
						val worldX: Int = this.vertexX!![vertexIndex] + x
						val worldZ: Int = this.vertexZ!![vertexIndex] + z
						val fracX: Int = worldX and tileMask
						val fracZ: Int = worldZ and tileMask
						val tileX: Int = worldX shr ground2.tileScale
						val tileZ: Int = worldZ shr ground2.tileScale
						if (tileX >= 0 && tileX < ground2.width - 1 && tileZ >= 0 && tileZ < ground2.length - 1) {
							val lowerHeight: Int = otherHeights!![tileX]!![tileZ] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1]!![tileZ] * fracX shr ground2.tileScale
							val upperHeight: Int = otherHeights[tileX]!![tileZ + 1] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1]!![tileZ + 1] * fracX shr ground2.tileScale
							val groundHeight: Int = lowerHeight * (ground2.tileUnits - fracZ) + upperHeight * fracZ shr ground2.tileScale
							this.vertexY!![vertexIndex] = this.vertexY!![vertexIndex] + groundHeight + modelHeight - y
						}
						vertexIndex++
					}
				} else if (contourType == 5) {
					val tileMask: Int = ground2!!.tileUnits - 1
					val modelHeight: Int = this.maxY.toInt() - this.minY.toInt()
					@Pc(1316) var local1316: Int
					var vertexIndex: Int = 0
					while (vertexIndex < this.maxVertex) {
						val worldX: Int = this.vertexX!![vertexIndex] + x
						val worldZ: Int = this.vertexZ!![vertexIndex] + z
						val fracX: Int = worldX and tileMask
						val fracZ: Int = worldZ and tileMask
						val tileX: Int = worldX shr ground.tileScale
						val tileZ: Int = worldZ shr ground.tileScale
						var lowerHeight: Int = tileHeights!![tileX]!![tileZ] * (ground.tileUnits - fracX) + tileHeights[tileX + 1]!![tileZ] * fracX shr ground.tileScale
						var upperHeight: Int = tileHeights[tileX]!![tileZ + 1] * (ground.tileUnits - fracX) + tileHeights[tileX + 1]!![tileZ + 1] * fracX shr ground.tileScale
						val groundHeight: Int = lowerHeight * (ground.tileUnits - fracZ) + upperHeight * fracZ shr ground.tileScale
						lowerHeight = otherHeights!![tileX]!![tileZ] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1]!![tileZ] * fracX shr ground2.tileScale
						upperHeight = otherHeights[tileX]!![tileZ + 1] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1]!![tileZ + 1] * fracX shr ground2.tileScale
						val otherGroundHeight: Int = lowerHeight * (ground2.tileUnits - fracZ) + upperHeight * fracZ shr ground2.tileScale
						val groundDelta: Int = groundHeight - otherGroundHeight - contourModifier
						this.vertexY!![vertexIndex] = ((this.vertexY!![vertexIndex] shl 8) / modelHeight * groundDelta shr 8) - (y - groundHeight)
						vertexIndex++
					}
					vertexIndex = this.maxVertex
					while (vertexIndex < this.vertexCount) {
						val worldX: Int = this.vertexX!![vertexIndex] + x
						val worldZ: Int = this.vertexZ!![vertexIndex] + z
						val fracX: Int = worldX and tileMask
						val fracZ: Int = worldZ and tileMask
						val tileX: Int = worldX shr ground.tileScale
						val tileZ: Int = worldZ shr ground.tileScale
						if (tileX >= 0 && tileX < ground.width - 1 && tileX < ground2.width - 1 && tileZ >= 0 && tileZ < ground.length - 1 && tileZ < ground2.length - 1) {
							var lowerHeight: Int = tileHeights!![tileX]!![tileZ] * (ground.tileUnits - fracX) + tileHeights[tileX + 1]!![tileZ] * fracX shr ground.tileScale
							var upperHeight: Int = tileHeights[tileX]!![tileZ + 1] * (ground.tileUnits - fracX) + tileHeights[tileX + 1]!![tileZ + 1] * fracX shr ground.tileScale
							val groundHeight: Int = lowerHeight * (ground.tileUnits - fracZ) + upperHeight * fracZ shr ground.tileScale
							lowerHeight = otherHeights!![tileX]!![tileZ] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1]!![tileZ] * fracX shr ground2.tileScale
							upperHeight = otherHeights[tileX]!![tileZ + 1] * (ground2.tileUnits - fracX) + otherHeights[tileX + 1]!![tileZ + 1] * fracX shr ground2.tileScale
							val otherGroundHeight: Int = lowerHeight * (ground2.tileUnits - fracZ) + upperHeight * fracZ shr ground2.tileScale
							@Pc(1544) val heightDelta: Int = groundHeight - otherGroundHeight - contourModifier
							this.vertexY!![vertexIndex] = ((this.vertexY!![vertexIndex] shl 8) / modelHeight * heightDelta shr 8) - (y - groundHeight)
						}
						vertexIndex++
					}
				}
			}
			this.boundsValid = false
		}
	}

	@OriginalMember(owner = "client!jga", name = "C", descriptor = "(I)V")
	override fun C(@OriginalArg(0) ambient: Int) {
		if ((this.functionMask and 0x1000) != 4096) {
			throw IllegalStateException()
		}
		this.ambient = ambient
		this.lightingState = 0
	}

	@OriginalMember(owner = "client!jga", name = "l", descriptor = "()V")
	private fun rotate90() {
		withLock(this) {
			@Pc(5) var i: Int = 0
			while (i < this.vertexCount) {
				@Pc(11) val oldX: Int = this.vertexX!![i]
				this.vertexX!![i] = this.vertexZ!![i]
				this.vertexZ!![i] = -oldX
				i++
			}
			this.invalidateNormals()
		}
	}

	@OriginalMember(owner = "client!jga", name = "RA", descriptor = "()I")
	override fun RA(): Int {
		if (!this.boundsValid) {
			this.calculateBounds()
		}
		return this.maxX.toInt()
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(IILclient!bca;ZII)Z")
	override fun pickedOrtho(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) matrix: Matrix?, @OriginalArg(3) quick: Boolean, @OriginalArg(4) sizeShift: Int, @OriginalArg(5) angle: Int): Boolean {
		return this.pick(x, y, matrix, quick, sizeShift, angle)
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "()[Lclient!eca;")
	override fun getEffectiveVertices(): Array<EffectiveVertex?>? {
		return this.effectors
	}

	@OriginalMember(owner = "client!jga", name = "WA", descriptor = "()I")
	override fun WA(): Int {
		return this.ambient
	}

	@OriginalMember(owner = "client!jga", name = "s", descriptor = "(I)V")
	override fun s(@OriginalArg(0) functionMask: Int) {
		if (this.toolkit!!.threadCount <= 1) {
			if ((this.functionMask and 0x10000) == 65536 && (functionMask and 0x10000) == 0) {
				this.applyLighting(true)
			}
			this.functionMask = functionMask
			return
		}
		withLock(this) {
			if ((this.functionMask and 0x10000) == 65536 && (functionMask and 0x10000) == 0) {
				this.applyLighting(true)
			}
			this.functionMask = functionMask
		}
	}

	@OriginalMember(owner = "client!jga", name = "e", descriptor = "()V")
	override fun unlock() {
		if (this.toolkit!!.threadCount > 1) {
			withLock(this) {
				super.isLocked = false
				lockNotifyAll(this)
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(IIII)V")
	override fun adjustColours(@OriginalArg(0) hue: Int, @OriginalArg(1) saturation: Int, @OriginalArg(2) lightness: Int, @OriginalArg(3) scale: Int) {
		if ((this.functionMask and 0x80000) != 524288) {
			throw IllegalStateException("FMT")
		}
		@Pc(20) var local20: Int
		@Pc(12) var i: Int = 0
		while (i < this.faceCount) {
			val hsl: Int = this.faceColour!![i].toInt() and 0xFFFF
			@Pc(26) var faceHue: Int = (hsl shr 10) and 0x3F
			@Pc(32) var faceSaturation: Int = (hsl shr 7) and 0x7
			@Pc(36) var faceLightness: Int = hsl and 0x7F
			if (hue != -1) {
				faceHue += (hue - faceHue) * scale shr 7
			}
			if (saturation != -1) {
				faceSaturation += (saturation - faceSaturation) * scale shr 7
			}
			if (lightness != -1) {
				faceLightness += (lightness - faceLightness) * scale shr 7
			}
			this.faceColour!![i] = ((faceHue shl 10) or (faceSaturation shl 7) or faceLightness).toShort()
			i++
		}
		if (this.billboardFaces != null) {
			var billboardIndex: Int = 0
			while (billboardIndex < this.billboardCount) {
				@Pc(105) val javaBillboardFace: JavaBillboardFace? = this.billboardFaces!![billboardIndex]
				@Pc(110) val javaBillboardAttributes: JavaBillboardAttributes? = this.billboardAttributes!![billboardIndex]
				javaBillboardAttributes!!.diffuseColor = (javaBillboardAttributes.diffuseColor and 0xFF000000.toInt()) or (ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(this.faceColour!![javaBillboardFace!!.face].toInt() and 0xFFFF).toInt()] and 0xFFFFFF)
				billboardIndex++
			}
		}
		if (this.lightingState == 2) {
			this.lightingState = 1
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(IILclient!bca;ZI)Z")
	override fun picked(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) matrix: Matrix?, @OriginalArg(3) quick: Boolean, @OriginalArg(4) sizeShift: Int): Boolean {
		return this.pick(x, y, matrix, quick, sizeShift, -1)
	}

	@OriginalMember(owner = "client!jga", name = "ma", descriptor = "()I")
	override fun ma(): Int {
		if (!this.boundsValid) {
			this.calculateBounds()
		}
		return this.sphereRadius.toInt()
	}

	@OriginalMember(owner = "client!jga", name = "b", descriptor = "(IILclient!bca;ZII)Z")
	private fun pick(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) matrix: Matrix?, @OriginalArg(3) quick: Boolean, @OriginalArg(4) sizeShift: Int, @OriginalArg(5) orthoDepth: Int): Boolean {
		this.modelMatrix = matrix as JavaMatrix?
		@Pc(7) val javaMatrix: JavaMatrix? = this.toolkit!!.camera
		@Pc(31) val tx: Float = javaMatrix!!.tx + javaMatrix.e1_1 * this.modelMatrix!!.tx + javaMatrix.e1_2 * this.modelMatrix!!.ty + javaMatrix.e1_3 * this.modelMatrix!!.tz
		@Pc(55) val ty: Float = javaMatrix.ty + javaMatrix.e2_1 * this.modelMatrix!!.tx + javaMatrix.e2_2 * this.modelMatrix!!.ty + javaMatrix.e2_3 * this.modelMatrix!!.tz
		@Pc(79) val tz: Float = javaMatrix.tz + javaMatrix.e3_1 * this.modelMatrix!!.tx + javaMatrix.e3_2 * this.modelMatrix!!.ty + javaMatrix.e3_3 * this.modelMatrix!!.tz
		@Pc(100) val m11: Float = javaMatrix.e1_1 * this.modelMatrix!!.e1_1 + javaMatrix.e1_2 * this.modelMatrix!!.e2_1 + javaMatrix.e1_3 * this.modelMatrix!!.e3_1
		@Pc(121) val m12: Float = javaMatrix.e1_1 * this.modelMatrix!!.e1_2 + javaMatrix.e1_2 * this.modelMatrix!!.e2_2 + javaMatrix.e1_3 * this.modelMatrix!!.e3_2
		@Pc(142) val m13: Float = javaMatrix.e1_1 * this.modelMatrix!!.e1_3 + javaMatrix.e1_2 * this.modelMatrix!!.e2_3 + javaMatrix.e1_3 * this.modelMatrix!!.e3_3
		@Pc(163) val m21: Float = javaMatrix.e2_1 * this.modelMatrix!!.e1_1 + javaMatrix.e2_2 * this.modelMatrix!!.e2_1 + javaMatrix.e2_3 * this.modelMatrix!!.e3_1
		@Pc(184) val m22: Float = javaMatrix.e2_1 * this.modelMatrix!!.e1_2 + javaMatrix.e2_2 * this.modelMatrix!!.e2_2 + javaMatrix.e2_3 * this.modelMatrix!!.e3_2
		@Pc(205) val m23: Float = javaMatrix.e2_1 * this.modelMatrix!!.e1_3 + javaMatrix.e2_2 * this.modelMatrix!!.e2_3 + javaMatrix.e2_3 * this.modelMatrix!!.e3_3
		@Pc(226) val m31: Float = javaMatrix.e3_1 * this.modelMatrix!!.e1_1 + javaMatrix.e3_2 * this.modelMatrix!!.e2_1 + javaMatrix.e3_3 * this.modelMatrix!!.e3_1
		@Pc(247) val m32: Float = javaMatrix.e3_1 * this.modelMatrix!!.e1_2 + javaMatrix.e3_2 * this.modelMatrix!!.e2_2 + javaMatrix.e3_3 * this.modelMatrix!!.e3_2
		@Pc(268) val m33: Float = javaMatrix.e3_1 * this.modelMatrix!!.e1_3 + javaMatrix.e3_2 * this.modelMatrix!!.e2_3 + javaMatrix.e3_3 * this.modelMatrix!!.e3_3
		@Pc(270) var anyCornerVisible: Boolean = false
		@Pc(274) val projectionCenterX: Int = this.toolkit!!.projectionCenterX
		@Pc(278) val projectionCenterY: Int = this.toolkit!!.lb
		@Pc(282) val projectionScaleX: Int = this.toolkit!!.projectionScaleX
		@Pc(286) val projectionScaleY: Int = this.toolkit!!.projectionScaleY
		@Pc(288) var minScreenX: Int = Integer.MAX_VALUE
		@Pc(290) var maxScreenX: Int = Integer.MIN_VALUE
		@Pc(292) var minScreenY: Int = Integer.MAX_VALUE
		@Pc(294) var maxScreenY: Int = Integer.MIN_VALUE
		this.useThreadBuffers(lang.currentThread())
		if (!this.boundsValid) {
			this.calculateBounds()
		}
		@Pc(310) val halfX: Int = this.maxX.toInt() - this.minX.toInt() shr 1
		@Pc(318) val halfY: Int = this.maxY.toInt() - this.minY.toInt() shr 1
		@Pc(326) val halfZ: Int = this.maxZ.toInt() - this.minZ.toInt() shr 1
		@Pc(331) val midX: Int = this.minX.toInt() + halfX
		@Pc(336) val midY: Int = this.minY.toInt() + halfY
		@Pc(341) val midZ: Int = this.minZ.toInt() + halfZ
		@Pc(347) val lowX: Int = midX - (halfX shl sizeShift)
		@Pc(353) val lowY: Int = midY - (halfY shl sizeShift)
		@Pc(359) val lowZ: Int = midZ - (halfZ shl sizeShift)
		@Pc(365) val highX: Int = midX + (halfX shl sizeShift)
		@Pc(371) val highY: Int = midY + (halfY shl sizeShift)
		@Pc(377) val highZ: Int = midZ + (halfZ shl sizeShift)
		this.boundsCornerX!![0] = lowX
		this.boundsCornerY!![0] = lowY
		this.boundsCornerZ!![0] = lowZ
		this.boundsCornerX!![1] = highX
		this.boundsCornerY!![1] = lowY
		this.boundsCornerZ!![1] = lowZ
		this.boundsCornerX!![2] = lowX
		this.boundsCornerY!![2] = highY
		this.boundsCornerZ!![2] = lowZ
		this.boundsCornerX!![3] = highX
		this.boundsCornerY!![3] = highY
		this.boundsCornerZ!![3] = lowZ
		this.boundsCornerX!![4] = lowX
		this.boundsCornerY!![4] = lowY
		this.boundsCornerZ!![4] = highZ
		this.boundsCornerX!![5] = highX
		this.boundsCornerY!![5] = lowY
		this.boundsCornerZ!![5] = highZ
		this.boundsCornerX!![6] = lowX
		this.boundsCornerY!![6] = highY
		this.boundsCornerZ!![6] = highZ
		this.boundsCornerX!![7] = highX
		this.boundsCornerY!![7] = highY
		this.boundsCornerZ!![7] = highZ
		@Pc(532) var viewX: Float
		@Pc(549) var viewY: Float
		@Pc(566) var depth: Float
		@Pc(505) var px: Int
		@Pc(510) var py: Int
		@Pc(515) var pz: Int
		@Pc(588) var local588: Int
		@Pc(598) var local598: Int
		@Pc(499) var i: Int = 0
		while (i < 8) {
			px = this.boundsCornerX!![i]
			py = this.boundsCornerY!![i]
			pz = this.boundsCornerZ!![i]
			viewX = tx + m11 * px.toFloat() + m12 * py.toFloat() + m13 * pz.toFloat()
			viewY = ty + m21 * px.toFloat() + m22 * py.toFloat() + m23 * pz.toFloat()
			depth = tz + m31 * px.toFloat() + m32 * py.toFloat() + m33 * pz.toFloat()
			if (depth >= this.toolkit!!.zNear.toFloat()) {
				if (orthoDepth > 0) {
					depth = orthoDepth.toFloat()
				}
				val cornerScreenX: Int = projectionCenterX + (viewX * projectionScaleX.toFloat() / depth).toInt()
				val cornerScreenY: Int = projectionCenterY + (viewY * projectionScaleY.toFloat() / depth).toInt()
				if (cornerScreenX < minScreenX) {
					minScreenX = cornerScreenX
				}
				if (cornerScreenX > maxScreenX) {
					maxScreenX = cornerScreenX
				}
				if (cornerScreenY < minScreenY) {
					minScreenY = cornerScreenY
				}
				if (cornerScreenY > maxScreenY) {
					maxScreenY = cornerScreenY
				}
				anyCornerVisible = true
			}
			i++
		}
		if (anyCornerVisible && x > minScreenX && x < maxScreenX && y > minScreenY && y < maxScreenY) {
			if (quick) {
				return true
			}
			var vertexIndex: Int = 0
			while (vertexIndex < this.vertexCount) {
				px = this.vertexX!![vertexIndex]
				py = this.vertexY!![vertexIndex]
				pz = this.vertexZ!![vertexIndex]
				viewX = tx + m11 * px.toFloat() + m12 * py.toFloat() + m13 * pz.toFloat()
				viewY = ty + m21 * px.toFloat() + m22 * py.toFloat() + m23 * pz.toFloat()
				depth = tz + m31 * px.toFloat() + m32 * py.toFloat() + m33 * pz.toFloat()
				if (depth >= this.toolkit!!.zNear.toFloat()) {
					if (orthoDepth > 0) {
						depth = orthoDepth.toFloat()
					}
					this.screenX!![vertexIndex] = projectionCenterX + (viewX * projectionScaleX.toFloat() / depth).toInt()
					this.screenY!![vertexIndex] = projectionCenterY + (viewY * projectionScaleY.toFloat() / depth).toInt()
				} else {
					this.screenX!![vertexIndex] = -999999
				}
				vertexIndex++
			}
			var faceIndex: Int = 0
			while (faceIndex < this.faceCount) {
				if (this.screenX!![this.faceA!![faceIndex].toInt()] != -999999 && this.screenX!![this.faceB!![faceIndex].toInt()] != -999999 && this.screenX!![this.faceC!![faceIndex].toInt()] != -999999 && this.pointInFaceBounds(x, y, this.screenY!![this.faceA!![faceIndex].toInt()], this.screenY!![this.faceB!![faceIndex].toInt()], this.screenY!![this.faceC!![faceIndex].toInt()], this.screenX!![this.faceA!![faceIndex].toInt()], this.screenX!![this.faceB!![faceIndex].toInt()], this.screenX!![this.faceC!![faceIndex].toInt()])) {
					return true
				}
				faceIndex++
			}
		}
		return false
	}

	@OriginalMember(owner = "client!jga", name = "e", descriptor = "(I)V")
	private fun drawClippedTriangleRgb(@OriginalArg(0) face: Int) {
		@Pc(1) var count: Int = 0
		@Pc(5) val zNear: Int = this.toolkit!!.zNear
		@Pc(10) val a: Short = this.faceA!![face]
		@Pc(15) val b: Short = this.faceB!![face]
		@Pc(20) val c: Short = this.faceC!![face]
		@Pc(25) var depthA: Int = this.cameraZ!![a.toInt()]
		@Pc(30) var depthB: Int = this.cameraZ!![b.toInt()]
		@Pc(35) var depthC: Int = this.cameraZ!![c.toInt()]
		if (this.faceAlpha == null) {
			this.rasterizer!!.alpha = 0
		} else {
			this.rasterizer!!.alpha = this.faceAlpha!![face].toInt() and 0xFF
		}
		@Pc(96) var local96: Int
		@Pc(101) var local101: Int
		@Pc(108) var local108: Int
		@Pc(121) var local121: Int
		if (depthA >= zNear) {
			this.clippedX!![0] = this.screenX!![a.toInt()]
			this.clippedY!![0] = this.screenY!![a.toInt()]
			this.clippedZ!![0] = this.screenZ!![a.toInt()]
			count++
			this.clippedColour!![0] = this.faceColourA!![face] and 0xFFFF
		} else {
			val viewX: Int = this.cameraX!![a.toInt()]
			val viewY: Int = this.cameraY!![a.toInt()]
			val hsl: Int = this.faceColourA!![face] and 0xFFFF
			if (depthC >= zNear) {
				val lerpFactor: Int = (zNear - depthA) * (65536 / (depthC - depthA))
				this.clippedX!![0] = this.threadResource!!.rasterMinX + (viewX + ((this.cameraX!![c.toInt()] - viewX) * lerpFactor shr 16)) * this.toolkit!!.projectionScaleX / zNear
				this.clippedY!![0] = this.threadResource!!.rasterMinY + (viewY + ((this.cameraY!![c.toInt()] - viewY) * lerpFactor shr 16)) * this.toolkit!!.projectionScaleY / zNear
				this.clippedZ!![0] = zNear
				count++
				this.clippedColour!![0] = hsl + (((this.faceColourC!![face] and 0xFFFF) - hsl) * lerpFactor shr 16)
			}
			if (depthB >= zNear) {
				val lerpFactor: Int = (zNear - depthA) * (65536 / (depthB - depthA))
				this.clippedX!![count] = this.threadResource!!.rasterMinX + (viewX + ((this.cameraX!![b.toInt()] - viewX) * lerpFactor shr 16)) * this.toolkit!!.projectionScaleX / zNear
				this.clippedY!![count] = this.threadResource!!.rasterMinY + (viewY + ((this.cameraY!![b.toInt()] - viewY) * lerpFactor shr 16)) * this.toolkit!!.projectionScaleY / zNear
				this.clippedZ!![count] = zNear
				this.clippedColour!![count++] = hsl + (((this.faceColourB!![face] and 0xFFFF) - hsl) * lerpFactor shr 16)
			}
		}
		if (depthB >= zNear) {
			this.clippedX!![count] = this.screenX!![b.toInt()]
			this.clippedY!![count] = this.screenY!![b.toInt()]
			this.clippedZ!![count] = this.screenZ!![b.toInt()]
			this.clippedColour!![count++] = this.faceColourB!![face] and 0xFFFF
		} else {
			val viewX: Int = this.cameraX!![b.toInt()]
			val viewY: Int = this.cameraY!![b.toInt()]
			val hsl: Int = this.faceColourB!![face] and 0xFFFF
			if (depthA >= zNear) {
				val lerpFactor: Int = (zNear - depthB) * (65536 / (depthA - depthB))
				this.clippedX!![count] = this.threadResource!!.rasterMinX + (viewX + ((this.cameraX!![a.toInt()] - viewX) * lerpFactor shr 16)) * this.toolkit!!.projectionScaleX / zNear
				this.clippedY!![count] = this.threadResource!!.rasterMinY + (viewY + ((this.cameraY!![a.toInt()] - viewY) * lerpFactor shr 16)) * this.toolkit!!.projectionScaleY / zNear
				this.clippedZ!![count] = zNear
				this.clippedColour!![count++] = hsl + (((this.faceColourA!![face] and 0xFFFF) - hsl) * lerpFactor shr 16)
			}
			if (depthC >= zNear) {
				val lerpFactor: Int = (zNear - depthB) * (65536 / (depthC - depthB))
				this.clippedX!![count] = this.threadResource!!.rasterMinX + (viewX + ((this.cameraX!![c.toInt()] - viewX) * lerpFactor shr 16)) * this.toolkit!!.projectionScaleX / zNear
				this.clippedY!![count] = this.threadResource!!.rasterMinY + (viewY + ((this.cameraY!![c.toInt()] - viewY) * lerpFactor shr 16)) * this.toolkit!!.projectionScaleY / zNear
				this.clippedZ!![count] = zNear
				this.clippedColour!![count++] = hsl + (((this.faceColourC!![face] and 0xFFFF) - hsl) * lerpFactor shr 16)
			}
		}
		if (depthC >= zNear) {
			this.clippedX!![count] = this.screenX!![c.toInt()]
			this.clippedY!![count] = this.screenY!![c.toInt()]
			this.clippedZ!![count] = this.screenZ!![c.toInt()]
			this.clippedColour!![count++] = this.faceColourC!![face] and 0xFFFF
		} else {
			val viewX: Int = this.cameraX!![c.toInt()]
			val viewY: Int = this.cameraY!![c.toInt()]
			val hsl: Int = this.faceColourC!![face] and 0xFFFF
			if (depthB >= zNear) {
				val lerpFactor: Int = (zNear - depthC) * (65536 / (depthB - depthC))
				this.clippedX!![count] = this.threadResource!!.rasterMinX + (viewX + ((this.cameraX!![b.toInt()] - viewX) * lerpFactor shr 16)) * this.toolkit!!.projectionScaleX / zNear
				this.clippedY!![count] = this.threadResource!!.rasterMinY + (viewY + ((this.cameraY!![b.toInt()] - viewY) * lerpFactor shr 16)) * this.toolkit!!.projectionScaleY / zNear
				this.clippedZ!![count] = zNear
				this.clippedColour!![count++] = hsl + (((this.faceColourB!![face] and 0xFFFF) - hsl) * lerpFactor shr 16)
			}
			if (depthA >= zNear) {
				val lerpFactor: Int = (zNear - depthC) * (65536 / (depthA - depthC))
				this.clippedX!![count] = this.threadResource!!.rasterMinX + (viewX + ((this.cameraX!![a.toInt()] - viewX) * lerpFactor shr 16)) * this.toolkit!!.projectionScaleX / zNear
				this.clippedY!![count] = this.threadResource!!.rasterMinY + (viewY + ((this.cameraY!![a.toInt()] - viewY) * lerpFactor shr 16)) * this.toolkit!!.projectionScaleY / zNear
				this.clippedZ!![count] = zNear
				this.clippedColour!![count++] = hsl + (((this.faceColourA!![face] and 0xFFFF) - hsl) * lerpFactor shr 16)
			}
		}
		val clippedX0: Int = this.clippedX!![0]
		val clippedX1: Int = this.clippedX!![1]
		val clippedX2: Int = this.clippedX!![2]
		val clippedY0: Int = this.clippedY!![0]
		@Pc(779) val clippedY1: Int = this.clippedY!![1]
		@Pc(784) val clippedY2: Int = this.clippedY!![2]
		depthA = this.clippedZ!![0]
		depthB = this.clippedZ!![1]
		depthC = this.clippedZ!![2]
		this.rasterizer!!.clamp = false
		@Pc(915) var local915: Int
		@Pc(938) var colour: Int
		if (count == 3) {
			if (clippedX0 < 0 || clippedX1 < 0 || clippedX2 < 0 || clippedX0 > this.threadResource!!.rasterWidth || clippedX1 > this.threadResource!!.rasterWidth || clippedX2 > this.threadResource!!.rasterWidth) {
				this.rasterizer!!.clamp = true
			}
			if (this.faceTextures != null && this.faceTextures!![face].toInt() != -1) {
				var alphaBits: Int = -16777216
				if (this.faceAlpha != null) {
					alphaBits = 255 - (this.faceAlpha!![face].toInt() and 0xFF) shl 24
				}
				colour = alphaBits or (this.faceColourA!![face] and 0xFFFFFF)
				if (this.faceColourC!![face] == -1) {
					this.rasterizer!!.renderTexturedTriangleRgb(clippedY0.toFloat(), clippedY1.toFloat(), clippedY2.toFloat(), clippedX0.toFloat(), clippedX1.toFloat(), clippedX2.toFloat(), depthA.toFloat(), depthB.toFloat(), depthC.toFloat(), this.texCoordU!![face]!![0], this.texCoordU!![face]!![1], this.texCoordU!![face]!![2], this.texCoordV!![face]!![0], this.texCoordV!![face]!![1], this.texCoordV!![face]!![2], colour, colour, colour, this.threadResource!!.fogColour, 0, 0, 0, this.faceTextures!![face].toInt())
				} else {
					this.rasterizer!!.renderTexturedTriangleRgb(clippedY0.toFloat(), clippedY1.toFloat(), clippedY2.toFloat(), clippedX0.toFloat(), clippedX1.toFloat(), clippedX2.toFloat(), depthA.toFloat(), depthB.toFloat(), depthC.toFloat(), this.texCoordU!![face]!![0], this.texCoordU!![face]!![1], this.texCoordU!![face]!![2], this.texCoordV!![face]!![0], this.texCoordV!![face]!![1], this.texCoordV!![face]!![2], colour, colour, colour, this.threadResource!!.fogColour, 0, 0, 0, this.faceTextures!![face].toInt())
				}
			} else if (this.faceColourC!![face] == -1) {
				this.rasterizer!!.renderFlatTriangleRgb(clippedY0.toFloat(), clippedY1.toFloat(), clippedY2.toFloat(), clippedX0.toFloat(), clippedX1.toFloat(), clippedX2.toFloat(), depthA.toFloat(), depthB.toFloat(), depthC.toFloat(), ColorUtil.hsvToRgbTable!![this.faceColourA!![face] and 0xFFFF])
			} else {
				this.rasterizer!!.renderTriangleHslRgb(clippedY0.toFloat(), clippedY1.toFloat(), clippedY2.toFloat(), clippedX0.toFloat(), clippedX1.toFloat(), clippedX2.toFloat(), depthA.toFloat(), depthB.toFloat(), depthC.toFloat(), this.clippedColour!![0].toFloat(), this.clippedColour!![1].toFloat(), this.clippedColour!![2].toFloat())
			}
		}
		if (count != 4) {
			return
		}
		if (clippedX0 < 0 || clippedX1 < 0 || clippedX2 < 0 || clippedX0 > this.threadResource!!.rasterWidth || clippedX1 > this.threadResource!!.rasterWidth || clippedX2 > this.threadResource!!.rasterWidth || this.clippedX!![3] < 0 || this.clippedX!![3] > this.threadResource!!.rasterWidth) {
			this.rasterizer!!.clamp = true
		}
		if (this.faceTextures == null || this.faceTextures!![face].toInt() == -1) {
			if (this.faceColourC!![face] == -1) {
				val rgb: Int = ColorUtil.hsvToRgbTable!![this.faceColourA!![face] and 0xFFFF]
				this.rasterizer!!.renderFlatTriangleRgb(clippedY0.toFloat(), clippedY1.toFloat(), clippedY2.toFloat(), clippedX0.toFloat(), clippedX1.toFloat(), clippedX2.toFloat(), depthA.toFloat(), depthB.toFloat(), depthC.toFloat(), rgb)
				this.rasterizer!!.renderFlatTriangleRgb(clippedY0.toFloat(), clippedY2.toFloat(), this.clippedY!![3].toFloat(), clippedX0.toFloat(), clippedX2.toFloat(), this.clippedX!![3].toFloat(), depthA.toFloat(), depthB.toFloat(), this.clippedZ!![3].toFloat(), rgb)
				return
			} else {
				this.rasterizer!!.renderTriangleHslRgb(clippedY0.toFloat(), clippedY1.toFloat(), clippedY2.toFloat(), clippedX0.toFloat(), clippedX1.toFloat(), clippedX2.toFloat(), depthA.toFloat(), depthB.toFloat(), depthC.toFloat(), this.clippedColour!![0].toFloat(), this.clippedColour!![1].toFloat(), this.clippedColour!![2].toFloat())
				this.rasterizer!!.renderTriangleHslRgb(clippedY0.toFloat(), clippedY2.toFloat(), this.clippedY!![3].toFloat(), clippedX0.toFloat(), clippedX2.toFloat(), this.clippedX!![3].toFloat(), depthA.toFloat(), depthB.toFloat(), this.clippedZ!![3].toFloat(), this.clippedColour!![0].toFloat(), this.clippedColour!![2].toFloat(), this.clippedColour!![3].toFloat())
				return
			}
		}
		var alphaBits: Int = -16777216
		if (this.faceAlpha != null) {
			alphaBits = 255 - (this.faceAlpha!![face].toInt() and 0xFF) shl 24
		}
		colour = alphaBits or (this.faceColourA!![face] and 0xFFFFFF)
		if (this.faceColourC!![face] == -1) {
			this.rasterizer!!.renderTexturedTriangleRgb(clippedY0.toFloat(), clippedY1.toFloat(), clippedY2.toFloat(), clippedX0.toFloat(), clippedX1.toFloat(), clippedX2.toFloat(), depthA.toFloat(), depthB.toFloat(), depthC.toFloat(), this.texCoordU!![face]!![0], this.texCoordU!![face]!![1], this.texCoordU!![face]!![2], this.texCoordV!![face]!![0], this.texCoordV!![face]!![1], this.texCoordV!![face]!![2], colour, colour, colour, this.threadResource!!.fogColour, 0, 0, 0, this.faceTextures!![face].toInt())
			this.rasterizer!!.renderTexturedTriangleRgb(clippedY0.toFloat(), clippedY2.toFloat(), this.clippedY!![3].toFloat(), clippedX0.toFloat(), clippedX2.toFloat(), this.clippedX!![3].toFloat(), depthA.toFloat(), depthC.toFloat(), this.clippedZ!![3].toFloat(), this.texCoordU!![face]!![0], this.texCoordU!![face]!![1], this.texCoordU!![face]!![2], this.texCoordV!![face]!![0], this.texCoordV!![face]!![1], this.texCoordV!![face]!![2], colour, colour, colour, this.threadResource!!.fogColour, 0, 0, 0, this.faceTextures!![face].toInt())
			return
		}
		this.rasterizer!!.renderTexturedTriangleRgb(clippedY0.toFloat(), clippedY1.toFloat(), clippedY2.toFloat(), clippedX0.toFloat(), clippedX1.toFloat(), clippedX2.toFloat(), depthA.toFloat(), depthB.toFloat(), depthC.toFloat(), this.texCoordU!![face]!![0], this.texCoordU!![face]!![1], this.texCoordU!![face]!![2], this.texCoordV!![face]!![0], this.texCoordV!![face]!![1], this.texCoordV!![face]!![2], colour, colour, colour, this.threadResource!!.fogColour, 0, 0, 0, this.faceTextures!![face].toInt())
		this.rasterizer!!.renderTexturedTriangleRgb(clippedY0.toFloat(), clippedY2.toFloat(), this.clippedY!![3].toFloat(), clippedX0.toFloat(), clippedX2.toFloat(), this.clippedX!![3].toFloat(), depthA.toFloat(), depthC.toFloat(), this.clippedZ!![3].toFloat(), this.texCoordU!![face]!![0], this.texCoordU!![face]!![1], this.texCoordU!![face]!![2], this.texCoordV!![face]!![0], this.texCoordV!![face]!![1], this.texCoordV!![face]!![2], colour, colour, colour, this.threadResource!!.fogColour, 0, 0, 0, this.faceTextures!![face].toInt())
	}

	@OriginalMember(owner = "client!jga", name = "m", descriptor = "()V")
	private fun relight() {
		if (this.lightingState == 0) {
			this.applyLighting(false)
		} else if (this.toolkit!!.threadCount > 1) {
			withLock(this) {
				this.relightColours()
			}
		} else {
			this.relightColours()
		}
	}

	@OriginalMember(owner = "client!jga", name = "b", descriptor = "(Ljava/lang/Thread;)V")
	private fun useThreadCopyCache(@OriginalArg(0) thread: Thread?) {
		@Pc(4) val resource: JavaThreadResource? = this.toolkit!!.threadResource(thread)
		if (resource === this.copyThreadResource) {
			return
		}
		this.copyThreadResource = resource
		this.copyDstModel1 = this.copyThreadResource!!.copyDstModel1
		this.copyDstModel2 = this.copyThreadResource!!.copyDstModel2
		this.copyDstModel3 = this.copyThreadResource!!.copyDstModel3
		this.copyDstModel4 = this.copyThreadResource!!.copyDstModel4
		this.copyDstModel5 = this.copyThreadResource!!.copyDstModel5
		this.copySrcModel1 = this.copyThreadResource!!.copySrcModel1
		this.copySrcModel2 = this.copyThreadResource!!.copySrcModel2
		this.copySrcModel3 = this.copyThreadResource!!.copySrcModel3
		this.copySrcModel4 = this.copyThreadResource!!.copySrcModel4
		this.copySrcModel5 = this.copyThreadResource!!.copySrcModel5
	}

	@OriginalMember(owner = "client!jga", name = "LA", descriptor = "(I)V")
	override fun LA(@OriginalArg(0) contrast: Int) {
		if ((this.functionMask and 0x2000) != 8192) {
			throw IllegalStateException()
		}
		this.contrast = contrast
		this.lightingState = 0
	}

	@OriginalMember(owner = "client!jga", name = "VA", descriptor = "(I)V")
	override fun VA(@OriginalArg(0) angle: Int) {
		if ((this.functionMask and 0x3) != 3) {
			throw IllegalStateException()
		}
		@Pc(13) val sin: Int = TextureCombineSource.SINE[angle]
		@Pc(17) val cos: Int = TextureCombineSource.COSINE[angle]
		withLock(this) {
			@Pc(23) var i: Int = 0
			while (i < this.vertexCount) {
				@Pc(40) val newX: Int = this.vertexY!![i] * sin + this.vertexX!![i] * cos shr 14
				this.vertexY!![i] = this.vertexY!![i] * cos - this.vertexX!![i] * sin shr 14
				this.vertexX!![i] = newX
				i++
			}
			this.invalidateNormals()
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(I)V")
	override fun a(@OriginalArg(0) angle: Int) {
		if ((this.functionMask and 0x5) != 5) {
			throw IllegalStateException()
		} else if (angle == 4096) {
			this.rotate90()
		} else if (angle == 8192) {
			this.rotate180()
		} else if (angle == 12288) {
			this.rotate270()
		} else {
			@Pc(31) val sin: Int = TextureCombineSource.SINE[angle]
			@Pc(35) val cos: Int = TextureCombineSource.COSINE[angle]
			withLock(this) {
				@Pc(41) var i: Int = 0
				while (i < this.vertexCount) {
					@Pc(58) val newX: Int = this.vertexZ!![i] * sin + this.vertexX!![i] * cos shr 14
					this.vertexZ!![i] = this.vertexZ!![i] * cos - this.vertexX!![i] * sin shr 14
					this.vertexX!![i] = newX
					i++
				}
				this.invalidateNormals()
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "aa", descriptor = "(SS)V")
	override fun aa(@OriginalArg(0) src: Short, @OriginalArg(1) dst: Short) {
		if (this.faceTextures == null) {
			return
		}
		if (!this.movingTextures && dst.toInt() >= 0) {
			@Pc(17) val textureMetrics: MaterialType? = this.toolkit!!.materialProvider!!.getMetrics(dst.toInt() and 0xFFFF)
			if (textureMetrics!!.speedU.toInt() != 0 || textureMetrics.speedV.toInt() != 0) {
				this.movingTextures = true
			}
		}
		@Pc(28) var i: Int = 0
		while (i < this.faceCount) {
			if (this.faceTextures!![i].toInt() == src.toInt()) {
				this.faceTextures!![i] = dst
			}
			i++
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(Lclient!ka;IIIZ)V")
	override fun shareLight(@OriginalArg(0) other: Model?, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) z: Int, @OriginalArg(4) renderTypeAdjustment: Boolean) {
		@Pc(2) val javaModel: JavaModel? = other as JavaModel?
		if ((this.functionMask and 0x10000) != 65536) {
			throw IllegalStateException("")
		} else if ((javaModel!!.functionMask and 0x10000) == 65536) {
			this.useThreadBuffers(lang.currentThread())
			this.calculateBounds()
			this.ensureNormals()
			javaModel.calculateBounds()
			javaModel.ensureNormals()
			PureJavaModel.renderCycle++
			@Pc(41) var sharedVertexCount: Int = 0
			@Pc(44) val vertexX: IntArray? = javaModel.vertexX
			@Pc(47) val maxVertex: Int = javaModel.maxVertex
			@Pc(65) var local65: Int
			@Pc(49) var i: Int = 0
			while (i < this.maxVertex) {
				@Pc(55) val vertexNormal: VertexNormal? = this.lb!![i]
				if (vertexNormal!!.magnitude != 0) {
					val dy: Int = this.vertexY!![i] - y
					if (dy >= javaModel.minY.toInt() && dy <= javaModel.maxY.toInt()) {
						@Pc(81) val dx: Int = this.vertexX!![i] - x
						if (dx >= javaModel.minX.toInt() && dx <= javaModel.maxX.toInt()) {
							@Pc(97) val dz: Int = this.vertexZ!![i] - z
							if (dz >= javaModel.minZ.toInt() && dz <= javaModel.maxZ.toInt()) {
								@Pc(108) var j: Int = 0
								while (j < maxVertex) {
									@Pc(114) val vertexNormal2: VertexNormal? = javaModel.lb!![j]
									if (dx == vertexX!![j] && dz == javaModel.vertexZ!![j] && dy == javaModel.vertexY!![j] && vertexNormal2!!.magnitude != 0) {
										if (this.vertexNormalsOffset == null) {
											this.vertexNormalsOffset = arrayOfNulls<VertexNormal>(this.maxVertex)
										}
										if (javaModel.vertexNormalsOffset == null) {
											javaModel.vertexNormalsOffset = arrayOfNulls<VertexNormal>(maxVertex)
										}
										@Pc(154) var vertexNormal3: VertexNormal? = this.vertexNormalsOffset!![i]
										if (vertexNormal3 == null) {
											this.vertexNormalsOffset!![i] = VertexNormal(vertexNormal)
											vertexNormal3 = this.vertexNormalsOffset!![i]
										}
										@Pc(171) var vertexNormal4: VertexNormal? = javaModel.vertexNormalsOffset!![j]
										if (vertexNormal4 == null) {
											javaModel.vertexNormalsOffset!![j] = VertexNormal(vertexNormal2)
											vertexNormal4 = javaModel.vertexNormalsOffset!![j]
										}
										vertexNormal3!!.x += vertexNormal2.x
										vertexNormal3.y += vertexNormal2.y
										vertexNormal3.z += vertexNormal2.z
										vertexNormal3.magnitude += vertexNormal2.magnitude
										vertexNormal4!!.x += vertexNormal.x
										vertexNormal4.y += vertexNormal.y
										vertexNormal4.z += vertexNormal.z
										vertexNormal4.magnitude += vertexNormal.magnitude
										sharedVertexCount++
										this.mergeStamps!![i] = PureJavaModel.renderCycle
										this.otherMergeStamps!![j] = PureJavaModel.renderCycle
									}
									j++
								}
							}
						}
					}
				}
				i++
			}
			if (sharedVertexCount >= 3 && renderTypeAdjustment) {
				@Pc(267) var k: Int = 0
				while (k < this.faceCount) {
					if (this.mergeStamps!![this.faceA!![k].toInt()] == PureJavaModel.renderCycle && this.mergeStamps!![this.faceB!![k].toInt()] == PureJavaModel.renderCycle && this.mergeStamps!![this.faceC!![k].toInt()] == PureJavaModel.renderCycle) {
						if (this.shadingType == null) {
							this.shadingType = ByteArray(this.faceCount)
						}
						this.shadingType!![k] = 2
					}
					k++
				}
				var otherFaceIndex: Int = 0
				while (otherFaceIndex < javaModel.faceCount) {
					if (this.otherMergeStamps!![javaModel.faceA!![otherFaceIndex].toInt()] == PureJavaModel.renderCycle && this.otherMergeStamps!![javaModel.faceB!![otherFaceIndex].toInt()] == PureJavaModel.renderCycle && this.otherMergeStamps!![javaModel.faceC!![otherFaceIndex].toInt()] == PureJavaModel.renderCycle) {
						if (javaModel.shadingType == null) {
							javaModel.shadingType = ByteArray(javaModel.faceCount)
						}
						javaModel.shadingType!![otherFaceIndex] = 2
					}
					otherFaceIndex++
				}
			}
		} else {
			throw IllegalStateException("")
		}
	}

	@OriginalMember(owner = "client!jga", name = "n", descriptor = "()V")
	private fun calculateNormals() {
		this.lb = arrayOfNulls<VertexNormal>(this.maxVertex)
		@Pc(6) var i: Int = 0
		while (i < this.maxVertex) {
			this.lb!![i] = VertexNormal()
			i++
		}
		@Pc(21) var j: Int = 0
		while (j < this.faceCount) {
			@Pc(27) val a: Short = this.faceA!![j]
			@Pc(32) val b: Short = this.faceB!![j]
			@Pc(37) val c: Short = this.faceC!![j]
			@Pc(47) val dxAB: Int = this.vertexX!![b.toInt()] - this.vertexX!![a.toInt()]
			@Pc(57) val dyAB: Int = this.vertexY!![b.toInt()] - this.vertexY!![a.toInt()]
			@Pc(67) val dzAB: Int = this.vertexZ!![b.toInt()] - this.vertexZ!![a.toInt()]
			@Pc(77) val dxAC: Int = this.vertexX!![c.toInt()] - this.vertexX!![a.toInt()]
			@Pc(87) val dyAC: Int = this.vertexY!![c.toInt()] - this.vertexY!![a.toInt()]
			@Pc(97) val dzAC: Int = this.vertexZ!![c.toInt()] - this.vertexZ!![a.toInt()]
			@Pc(105) var nx: Int = dyAB * dzAC - dyAC * dzAB
			@Pc(113) var ny: Int = dzAB * dxAC - dzAC * dxAB
			@Pc(121) var nz: Int
			nz = dxAB * dyAC - dxAC * dyAB
			while (nx > 8192 || ny > 8192 || nz > 8192 || nx < -8192 || ny < -8192 || nz < -8192) {
				nx = nx shr 0x1
				ny = ny shr 0x1
				nz = nz shr 0x1
			}
			@Pc(167) var length: Int = kotlin.math.sqrt((nx * nx + ny * ny + nz * nz).toDouble()).toInt()
			if (length <= 0) {
				length = 1
			}
			nx = nx * 256 / length
			ny = ny * 256 / length
			nz = nz * 256 / length
			@Pc(194) var shading: Byte
			if (this.shadingType == null) {
				shading = 0
			} else {
				shading = this.shadingType!![j]
			}
			if (shading.toInt() == 0) {
				@Pc(207) val normalA: VertexNormal? = this.lb!![a.toInt()]
				normalA!!.x += nx
				normalA.y += ny
				normalA.z += nz
				normalA.magnitude++
				@Pc(236) val normalB: VertexNormal? = this.lb!![b.toInt()]
				normalB!!.x += nx
				normalB.y += ny
				normalB.z += nz
				normalB.magnitude++
				@Pc(265) val normalC: VertexNormal? = this.lb!![c.toInt()]
				normalC!!.x += nx
				normalC.y += ny
				normalC.z += nz
				normalC.magnitude++
			} else if (shading.toInt() == 1) {
				if (this.faceNormals == null) {
					this.faceNormals = arrayOfNulls<FaceNormal>(this.faceCount)
				}
				this.faceNormals!![j] = FaceNormal()
				@Pc(310) val faceNormal: FaceNormal? = this.faceNormals!![j]
				faceNormal!!.x = nx
				faceNormal.y = ny
				faceNormal.z = nz
			}
			j++
		}
	}

	@OriginalMember(owner = "client!jga", name = "H", descriptor = "(III)V")
	override fun H(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int) {
		if (x != 0 && (this.functionMask and 0x1) != 1) {
			throw IllegalStateException()
		} else if (y != 0 && (this.functionMask and 0x2) != 2) {
			throw IllegalStateException()
		} else if (z == 0 || (this.functionMask and 0x4) == 4) {
			withLock(this) {
				@Pc(41) var i: Int = 0
				while (i < this.vertexCount) {
					this.vertexX!![i] += x
					this.vertexY!![i] += y
					this.vertexZ!![i] += z
					i++
				}
			}
		} else {
			throw IllegalStateException()
		}
	}

	@OriginalMember(owner = "client!jga", name = "v", descriptor = "()V")
	override fun v() {
		if ((this.functionMask and 0x10) != 16) {
			throw IllegalStateException()
		}
		withLock(this) {
			@Pc(15) var i: Int = 0
			while (i < this.vertexCount) {
				this.vertexZ!![i] = -this.vertexZ!![i]
				i++
			}
			@Pc(35) var index: Int
			if (this.lb != null) {
				index = 0
				while (index < this.maxVertex) {
					if (this.lb!![index] != null) {
						this.lb!![index]!!.z = -this.lb!![index]!!.z
					}
					index++
				}
			}
			if (this.vertexNormalsOffset != null) {
				index = 0
				while (index < this.maxVertex) {
					if (this.vertexNormalsOffset!![index] != null) {
						this.vertexNormalsOffset!![index]!!.z = -this.vertexNormalsOffset!![index]!!.z
					}
					index++
				}
			}
			if (this.faceNormals != null) {
				index = 0
				while (index < this.faceCount) {
					if (this.faceNormals!![index] != null) {
						this.faceNormals!![index]!!.z = -this.faceNormals!![index]!!.z
					}
					index++
				}
			}
			@Pc(114) val oldFaceA: ShortArray? = this.faceA
			this.faceA = this.faceC
			this.faceC = oldFaceA
			if (this.texCoordU != null) {
				@Pc(126) var j: Int = 0
				while (j < this.faceCount) {
					@Pc(139) var swap: Float
					if (this.texCoordU!![j] != null) {
						swap = this.texCoordU!![j]!![0]
						this.texCoordU!![j]!![0] = this.texCoordU!![j]!![2]
						this.texCoordU!![j]!![2] = swap
					}
					if (this.texCoordV!![j] != null) {
						swap = this.texCoordV!![j]!![0]
						this.texCoordV!![j]!![0] = this.texCoordV!![j]!![2]
						this.texCoordV!![j]!![2] = swap
					}
					j++
				}
			}
			this.boundsValid = false
			this.lightingState = 0
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(ZZII)V")
	private fun drawFacesRgb(@OriginalArg(0) nearClipped: Boolean, @OriginalArg(1) fogged: Boolean, @OriginalArg(2) minDepth: Int, @OriginalArg(3) depthRange: Int) {
		@Pc(4) var i: Int
		if (this.billboardFaces != null) {
			i = 0
			while (i < this.billboardCount) {
				@Pc(10) val billboard: JavaBillboardFace? = this.billboardFaces!![i]
				this.faceBillboard!![billboard!!.face] = i++
			}
		}
		if (!this.transparent && this.billboardFaces == null) {
			i = 0
			while (i < this.faceCount) {
				this.drawFaceRgb(i, nearClipped, fogged)
				i++
			}
		} else if ((this.functionMask and 0x100) == 0 && this.faceIndices != null) {
			i = 0
			while (i < this.faceCount) {
				@Pc(43) val face: Short = this.faceIndices!![i]
				this.drawFaceRgb(face.toInt(), nearClipped, fogged)
				i++
			}
		} else {
			i = 0
			while (i < this.faceCount) {
				if (!this.isTranslucent(i) && !this.hasBillboard(i)) {
					this.drawFaceRgb(i, nearClipped, fogged)
				}
				i++
			}
			@Pc(81) var local81: Int
			if (this.facePriority == null) {
				var faceIndex: Int = 0
				while (faceIndex < this.faceCount) {
					if (this.isTranslucent(faceIndex) || this.hasBillboard(faceIndex)) {
						this.drawFaceRgb(faceIndex, nearClipped, fogged)
					}
					faceIndex++
				}
			} else {
				var priority: Int = 0
				while (priority < 12) {
					@Pc(106) var face: Int = 0
					while (face < this.faceCount) {
						if (this.facePriority!![face].toInt() == priority && (this.isTranslucent(face) || this.hasBillboard(face))) {
							this.drawFaceRgb(face, nearClipped, fogged)
						}
						face++
					}
					priority++
				}
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "o", descriptor = "()V")
	private fun rotate180() {
		withLock(this) {
			@Pc(5) var i: Int = 0
			while (i < this.vertexCount) {
				this.vertexX!![i] = -this.vertexX!![i]
				this.vertexZ!![i] = -this.vertexZ!![i]
				i++
			}
			this.invalidateNormals()
		}
	}

	@OriginalMember(owner = "client!jga", name = "ia", descriptor = "(SS)V")
	override fun ia(@OriginalArg(0) src: Short, @OriginalArg(1) dst: Short) {
		@Pc(1) var i: Int = 0
		while (i < this.faceCount) {
			if (this.faceColour!![i].toInt() == src.toInt()) {
				this.faceColour!![i] = dst
			}
			i++
		}
		if (this.billboardFaces != null) {
			@Pc(23) var j: Int = 0
			while (j < this.billboardCount) {
				@Pc(29) val javaBillboardFace: JavaBillboardFace? = this.billboardFaces!![j]
				@Pc(34) val javaBillboardAttributes: JavaBillboardAttributes? = this.billboardAttributes!![j]
				javaBillboardAttributes!!.diffuseColor = (javaBillboardAttributes.diffuseColor and 0xFF000000.toInt()) or (ColorUtil.hsvToRgbTable!![ColorUtil.hslToHsv(this.faceColour!![javaBillboardFace!!.face].toInt()).toInt() and 0xFFFF] and 0xFFFFFF)
				j++
			}
		}
		if (this.lightingState == 2) {
			this.lightingState = 1
		}
	}

	@OriginalMember(owner = "client!jga", name = "p", descriptor = "()V")
	private fun rotate180WithNormals() {
		withLock(this) {
			@Pc(5) var i: Int = 0
			while (i < this.maxVertex) {
				this.vertexX!![i] = -this.vertexX!![i]
				this.vertexZ!![i] = -this.vertexZ!![i]
				if (this.lb!![i] != null) {
					this.lb!![i]!!.x = -this.lb!![i]!!.x
					this.lb!![i]!!.z = -this.lb!![i]!!.z
				}
				i++
			}
			@Pc(61) var maxVertex: Int
			if (this.faceNormals != null) {
				maxVertex = 0
				while (maxVertex < this.faceCount) {
					if (this.faceNormals!![maxVertex] != null) {
						this.faceNormals!![maxVertex]!!.x = -this.faceNormals!![maxVertex]!!.x
						this.faceNormals!![maxVertex]!!.z = -this.faceNormals!![maxVertex]!!.z
					}
					maxVertex++
				}
			}
			maxVertex = this.maxVertex
			while (maxVertex < this.vertexCount) {
				this.vertexX!![maxVertex] = -this.vertexX!![maxVertex]
				this.vertexZ!![maxVertex] = -this.vertexZ!![maxVertex]
				maxVertex++
			}
			this.lightingState = 0
			this.boundsValid = false
		}
	}

	@OriginalMember(owner = "client!jga", name = "q", descriptor = "()V")
	private fun rotate90WithNormals() {
		withLock(this) {
			@Pc(11) var local11: Int
			@Pc(5) var i: Int = 0
			while (i < this.maxVertex) {
				var swapX: Int = this.vertexX!![i]
				this.vertexX!![i] = this.vertexZ!![i]
				this.vertexZ!![i] = -swapX
				if (this.lb!![i] != null) {
					swapX = this.lb!![i]!!.x
					this.lb!![i]!!.x = this.lb!![i]!!.z
					this.lb!![i]!!.z = -swapX
				}
				i++
			}
			@Pc(75) var oldX: Int
			if (this.faceNormals != null) {
				var faceIndex: Int = 0
				while (faceIndex < this.faceCount) {
					if (this.faceNormals!![faceIndex] != null) {
						oldX = this.faceNormals!![faceIndex]!!.x
						this.faceNormals!![faceIndex]!!.x = this.faceNormals!![faceIndex]!!.z
						this.faceNormals!![faceIndex]!!.z = -oldX
					}
					faceIndex++
				}
			}
			var vertexIndex: Int = this.maxVertex
			while (vertexIndex < this.vertexCount) {
				oldX = this.vertexX!![vertexIndex]
				this.vertexX!![vertexIndex] = this.vertexZ!![vertexIndex]
				this.vertexZ!![vertexIndex] = -oldX
				vertexIndex++
			}
			this.lightingState = 0
			this.boundsValid = false
		}
	}

	@OriginalMember(owner = "client!jga", name = "b", descriptor = "(Lclient!bca;Lclient!iw;II)V")
	private fun draw(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) cylinder: BoundingBoxPolygon?, @OriginalArg(2) orthoDepth: Int, @OriginalArg(3) flags: Int) {
		if (this.maxVertex < 1) {
			return
		}
		this.modelMatrix = matrix as JavaMatrix?
		@Pc(12) val javaMatrix: JavaMatrix? = this.toolkit!!.camera
		if (!this.boundsValid) {
			this.calculateBounds()
		}
		@Pc(19) var identityRotation: Boolean = false
		if (this.modelMatrix!!.e1_1 == 16384.0F && this.modelMatrix!!.e1_2 == 0.0F && this.modelMatrix!!.e1_3 == 0.0F && this.modelMatrix!!.e2_1 == 0.0F && this.modelMatrix!!.e2_2 == 16384.0F && this.modelMatrix!!.e2_3 == 0.0F && this.modelMatrix!!.e3_1 == 0.0F && this.modelMatrix!!.e3_2 == 0.0F && this.modelMatrix!!.e3_3 == 16384.0F) {
			identityRotation = true
		}
		@Pc(99) val tz: Float = javaMatrix!!.tz + javaMatrix.e3_1 * this.modelMatrix!!.tx + javaMatrix.e3_2 * this.modelMatrix!!.ty + javaMatrix.e3_3 * this.modelMatrix!!.tz
		@Pc(125) val m32: Float = if (identityRotation) javaMatrix.e3_2 else javaMatrix.e3_1 * this.modelMatrix!!.e1_2 + javaMatrix.e3_2 * this.modelMatrix!!.e2_2 + javaMatrix.e3_3 * this.modelMatrix!!.e3_2
		@Pc(134) val depthAtMinY: Int = (tz + this.minY.toFloat() * m32).toInt()
		@Pc(143) val depthAtMaxY: Int = (tz + this.maxY.toFloat() * m32).toInt()
		@Pc(151) var near: Int
		@Pc(156) var far: Int
		if (depthAtMinY > depthAtMaxY) {
			near = depthAtMaxY - this.radius.toInt()
			far = depthAtMinY + this.radius.toInt()
		} else {
			near = depthAtMinY - this.radius.toInt()
			far = depthAtMaxY + this.radius.toInt()
		}
		if (near >= this.toolkit!!.zFar || far <= this.toolkit!!.zNear) {
			return
		}
		@Pc(203) val tx: Float = javaMatrix.tx + javaMatrix.e1_1 * this.modelMatrix!!.tx + javaMatrix.e1_2 * this.modelMatrix!!.ty + javaMatrix.e1_3 * this.modelMatrix!!.tz
		@Pc(229) val m12: Float = if (identityRotation) javaMatrix.e1_2 else javaMatrix.e1_1 * this.modelMatrix!!.e1_2 + javaMatrix.e1_2 * this.modelMatrix!!.e2_2 + javaMatrix.e1_3 * this.modelMatrix!!.e3_2
		@Pc(238) val xAtMinY: Int = (tx + this.minY.toFloat() * m12).toInt()
		@Pc(247) val xAtMaxY: Int = (tx + this.maxY.toFloat() * m12).toInt()
		@Pc(259) var left: Int
		@Pc(268) var right: Int
		if (xAtMinY > xAtMaxY) {
			left = (xAtMaxY - this.radius.toInt()) * this.toolkit!!.projectionScaleX
			right = (xAtMinY + this.radius.toInt()) * this.toolkit!!.projectionScaleX
		} else {
			left = (xAtMinY - this.radius.toInt()) * this.toolkit!!.projectionScaleX
			right = (xAtMaxY + this.radius.toInt()) * this.toolkit!!.projectionScaleX
		}
		if (orthoDepth == -1) {
			if (left / far >= this.toolkit!!.viewX2) {
				return
			}
			if (right / far <= this.toolkit!!.viewX1) {
				return
			}
		} else if (left / orthoDepth >= this.toolkit!!.viewX2) {
			return
		} else if (right / orthoDepth <= this.toolkit!!.viewX1) {
			return
		}
		@Pc(346) val ty: Float = javaMatrix.ty + javaMatrix.e2_1 * this.modelMatrix!!.tx + javaMatrix.e2_2 * this.modelMatrix!!.ty + javaMatrix.e2_3 * this.modelMatrix!!.tz
		@Pc(372) val m22: Float = if (identityRotation) javaMatrix.e2_2 else javaMatrix.e2_1 * this.modelMatrix!!.e1_2 + javaMatrix.e2_2 * this.modelMatrix!!.e2_2 + javaMatrix.e2_3 * this.modelMatrix!!.e3_2
		@Pc(381) val yAtMinY: Int = (ty + this.minY.toFloat() * m22).toInt()
		@Pc(390) val yAtMaxY: Int = (ty + this.maxY.toFloat() * m22).toInt()
		@Pc(402) var top: Int
		@Pc(411) var bottom: Int
		if (yAtMinY > yAtMaxY) {
			top = (yAtMaxY - this.radius.toInt()) * this.toolkit!!.projectionScaleY
			bottom = (yAtMinY + this.radius.toInt()) * this.toolkit!!.projectionScaleY
		} else {
			top = (yAtMinY - this.radius.toInt()) * this.toolkit!!.projectionScaleY
			bottom = (yAtMaxY + this.radius.toInt()) * this.toolkit!!.projectionScaleY
		}
		if (orthoDepth == -1) {
			if (top / far >= this.toolkit!!.viewY2) {
				return
			}
			if (bottom / far <= this.toolkit!!.viewY1) {
				return
			}
		} else if (top / orthoDepth >= this.toolkit!!.viewY2) {
			return
		} else if (bottom / orthoDepth <= this.toolkit!!.viewY1) {
			return
		}
		@Pc(470) var m11: Float
		@Pc(473) var m21: Float
		@Pc(476) var m31: Float
		@Pc(479) var m13: Float
		@Pc(482) var m23: Float
		@Pc(485) var m33: Float
		if (identityRotation) {
			m11 = javaMatrix.e1_1
			m21 = javaMatrix.e2_1
			m31 = javaMatrix.e3_1
			m13 = javaMatrix.e1_3
			m23 = javaMatrix.e2_3
			m33 = javaMatrix.e3_3
		} else {
			m11 = javaMatrix.e1_1 * this.modelMatrix!!.e1_1 + javaMatrix.e1_2 * this.modelMatrix!!.e2_1 + javaMatrix.e1_3 * this.modelMatrix!!.e3_1
			m21 = javaMatrix.e2_1 * this.modelMatrix!!.e1_1 + javaMatrix.e2_2 * this.modelMatrix!!.e2_1 + javaMatrix.e2_3 * this.modelMatrix!!.e3_1
			m31 = javaMatrix.e3_1 * this.modelMatrix!!.e1_1 + javaMatrix.e3_2 * this.modelMatrix!!.e2_1 + javaMatrix.e3_3 * this.modelMatrix!!.e3_1
			m13 = javaMatrix.e1_1 * this.modelMatrix!!.e1_3 + javaMatrix.e1_2 * this.modelMatrix!!.e2_3 + javaMatrix.e1_3 * this.modelMatrix!!.e3_3
			m23 = javaMatrix.e2_1 * this.modelMatrix!!.e1_3 + javaMatrix.e2_2 * this.modelMatrix!!.e2_3 + javaMatrix.e2_3 * this.modelMatrix!!.e3_3
			m33 = javaMatrix.e3_1 * this.modelMatrix!!.e1_3 + javaMatrix.e3_2 * this.modelMatrix!!.e2_3 + javaMatrix.e3_3 * this.modelMatrix!!.e3_3
		}
		if (this.toolkit!!.threadCount > 1) {
			withLock(this) {
				while (this.rendering) {
					try {
						lockWait(this)
					} catch (@Pc(626) ex: InterruptedException) {
					}
				}
				this.rendering = true
			}
		}
		this.useThreadBuffers(lang.currentThread())
		if ((flags and 0x2) == 0) {
			this.rasterizer!!.setWireframe(false)
		} else {
			this.rasterizer!!.setWireframe(true)
		}
		@Pc(657) var nearClipped: Boolean = false
		@Pc(666) val nearPlaneCrossed: Boolean = near <= this.toolkit!!.zNear
		@Pc(678) val storeCameraCoords: Boolean = nearPlaneCrossed || this.emitters != null || this.effectors != null
		this.threadResource!!.rasterWidth = this.rasterizer!!.width
		this.threadResource!!.rasterMinX = this.rasterizer!!.minX
		this.threadResource!!.rasterMinY = this.rasterizer!!.minY
		@Pc(700) val projectionScaleX: Int = this.toolkit!!.projectionScaleX
		@Pc(704) val projectionScaleY: Int = this.toolkit!!.projectionScaleY
		@Pc(708) val zNear: Int = this.toolkit!!.zNear
		@Pc(746) var viewX: Float
		@Pc(763) var viewY: Float
		@Pc(780) var depth: Float
		@Pc(719) var px: Int
		@Pc(724) var py: Int
		@Pc(729) var pz: Int
		@Pc(713) var i: Int
		@Pc(892) var billboardIndex: Int
		@Pc(898) var javaBillboardFace: JavaBillboardFace?
		@Pc(903) var javaBillboardAttributes: JavaBillboardAttributes?
		@Pc(909) var a: Short
		@Pc(915) var b: Short
		@Pc(921) var c: Short
		if (orthoDepth == -1) {
			i = 0
			while (i < this.vertexCount) {
				px = this.vertexX!![i]
				py = this.vertexY!![i]
				pz = this.vertexZ!![i]
				viewX = tx + m11 * px.toFloat() + m12 * py.toFloat() + m13 * pz.toFloat()
				viewY = ty + m21 * px.toFloat() + m22 * py.toFloat() + m23 * pz.toFloat()
				depth = tz + m31 * px.toFloat() + m32 * py.toFloat() + m33 * pz.toFloat()
				this.screenZ!![i] = depth.toInt()
				if (depth >= zNear.toFloat()) {
					this.screenX!![i] = this.threadResource!!.rasterMinX + (viewX * projectionScaleX.toFloat() / depth).toInt()
					this.screenY!![i] = this.threadResource!!.rasterMinY + (viewY * projectionScaleY.toFloat() / depth).toInt()
				} else {
					this.screenX!![i] = -5000
					nearClipped = true
				}
				if (storeCameraCoords) {
					this.cameraX!![i] = viewX.toInt()
					this.cameraY!![i] = viewY.toInt()
					this.cameraZ!![i] = depth.toInt()
				}
				if (this.threadResource!!.water) {
					this.worldY!![i] = (this.modelMatrix!!.ty + this.modelMatrix!!.e2_1 * px.toFloat() + this.modelMatrix!!.e2_2 * py.toFloat() + this.modelMatrix!!.e2_3 * pz.toFloat()).toInt()
				}
				i++
			}
			if (this.billboardFaces != null) {
				billboardIndex = 0
				while (billboardIndex < this.billboardCount) {
					javaBillboardFace = this.billboardFaces!![billboardIndex]
					javaBillboardAttributes = this.billboardAttributes!![billboardIndex]
					a = this.faceA!![javaBillboardFace!!.face]
					b = this.faceB!![javaBillboardFace.face]
					c = this.faceC!![javaBillboardFace.face]
					px = (this.vertexX!![a.toInt()] + this.vertexX!![b.toInt()] + this.vertexX!![c.toInt()]) / 3
					py = (this.vertexY!![a.toInt()] + this.vertexY!![b.toInt()] + this.vertexY!![c.toInt()]) / 3
					pz = (this.vertexZ!![a.toInt()] + this.vertexZ!![b.toInt()] + this.vertexZ!![c.toInt()]) / 3
					viewX = javaBillboardAttributes!!.offsetX.toFloat() + tx + m11 * px.toFloat() + m12 * py.toFloat() + m13 * pz.toFloat()
					viewY = javaBillboardAttributes.offsetY.toFloat() + ty + m21 * px.toFloat() + m22 * py.toFloat() + m23 * pz.toFloat()
					depth = tz + m31 * px.toFloat() + m32 * py.toFloat() + m33 * pz.toFloat()
					if (depth > this.toolkit!!.zNear.toFloat()) {
						javaBillboardAttributes.x = this.toolkit!!.projectionCenterX + (viewX * projectionScaleX.toFloat() / depth).toInt()
						javaBillboardAttributes.y = this.toolkit!!.lb + (viewY * projectionScaleY.toFloat() / depth).toInt()
						javaBillboardAttributes.z = depth.toInt() - javaBillboardFace.distance
						javaBillboardAttributes.projectedWidth = ((javaBillboardAttributes.scaleX * javaBillboardFace.width.toInt() * projectionScaleX).toFloat() / (depth * 128.0F)).toInt()
						javaBillboardAttributes.projectedHeight = ((javaBillboardAttributes.scaleY * javaBillboardFace.height.toInt() * projectionScaleY).toFloat() / (depth * 128.0F)).toInt()
					} else {
						javaBillboardAttributes.projectedHeight = 0
						javaBillboardAttributes.projectedWidth = javaBillboardAttributes.projectedHeight
					}
					billboardIndex++
				}
			}
		} else {
			i = 0
			while (i < this.vertexCount) {
				px = this.vertexX!![i]
				py = this.vertexY!![i]
				pz = this.vertexZ!![i]
				viewX = tx + m11 * px.toFloat() + m12 * py.toFloat() + m13 * pz.toFloat()
				viewY = ty + m21 * px.toFloat() + m22 * py.toFloat() + m23 * pz.toFloat()
				depth = tz + m31 * px.toFloat() + m32 * py.toFloat() + m33 * pz.toFloat()
				this.screenZ!![i] = depth.toInt()
				this.screenX!![i] = this.threadResource!!.rasterMinX + (viewX * projectionScaleX.toFloat() / orthoDepth.toFloat()).toInt()
				this.screenY!![i] = this.threadResource!!.rasterMinY + (viewY * projectionScaleY.toFloat() / orthoDepth.toFloat()).toInt()
				if (storeCameraCoords) {
					this.cameraX!![i] = viewX.toInt()
					this.cameraY!![i] = viewY.toInt()
					this.cameraZ!![i] = orthoDepth
				}
				if (this.threadResource!!.water) {
					this.worldY!![i] = (this.modelMatrix!!.ty + this.modelMatrix!!.e2_1 * px.toFloat() + this.modelMatrix!!.e2_2 * py.toFloat() + this.modelMatrix!!.e2_3 * pz.toFloat()).toInt()
				}
				i++
			}
			if (this.billboardFaces != null) {
				billboardIndex = 0
				while (billboardIndex < this.billboardCount) {
					javaBillboardFace = this.billboardFaces!![billboardIndex]
					javaBillboardAttributes = this.billboardAttributes!![billboardIndex]
					a = this.faceA!![javaBillboardFace!!.face]
					b = this.faceB!![javaBillboardFace.face]
					c = this.faceC!![javaBillboardFace.face]
					px = (this.vertexX!![a.toInt()] + this.vertexX!![b.toInt()] + this.vertexX!![c.toInt()]) / 3
					py = (this.vertexY!![a.toInt()] + this.vertexY!![b.toInt()] + this.vertexY!![c.toInt()]) / 3
					pz = (this.vertexZ!![a.toInt()] + this.vertexZ!![b.toInt()] + this.vertexZ!![c.toInt()]) / 3
					viewX = tx + m11 * px.toFloat() + m12 * py.toFloat() + m13 * pz.toFloat()
					viewY = ty + m21 * px.toFloat() + m22 * py.toFloat() + m23 * pz.toFloat()
					javaBillboardAttributes!!.x = this.toolkit!!.projectionCenterX + (viewX * projectionScaleX.toFloat() / orthoDepth.toFloat()).toInt()
					javaBillboardAttributes.y = this.toolkit!!.lb + (viewY * projectionScaleY.toFloat() / orthoDepth.toFloat()).toInt()
					javaBillboardAttributes.z = orthoDepth - javaBillboardFace.distance
					javaBillboardAttributes.projectedWidth = javaBillboardAttributes.scaleX * javaBillboardFace.width.toInt() * projectionScaleX / (orthoDepth shl 7)
					javaBillboardAttributes.projectedHeight = javaBillboardAttributes.scaleY * javaBillboardFace.height.toInt() * projectionScaleY / (orthoDepth shl 7)
					billboardIndex++
				}
			}
		}
		if (cylinder != null) {
			@Pc(1483) var clipped: Boolean = false
			@Pc(1485) var visible: Boolean = true
			@Pc(1493) val midX: Int = this.minX.toInt() + this.maxX.toInt() shr 1
			@Pc(1501) val midZ: Int = this.minZ.toInt() + this.maxZ.toInt() shr 1
			@Pc(1506) var sampleY: Short = this.minY
			viewX = tx + m11 * midX.toFloat() + m12 * sampleY.toFloat() + m13 * midZ.toFloat()
			viewY = ty + m21 * midX.toFloat() + m22 * sampleY.toFloat() + m23 * midZ.toFloat()
			depth = tz + m31 * midX.toFloat() + m32 * sampleY.toFloat() + m33 * midZ.toFloat()
			if (depth >= zNear.toFloat()) {
				@Pc(1567) var projectionDepth: Int = depth.toInt()
				if (orthoDepth != -1) {
					projectionDepth = orthoDepth
				}
				cylinder.left = this.toolkit!!.projectionCenterX + (viewX * projectionScaleX.toFloat() / projectionDepth.toFloat()).toInt()
				cylinder.bottom = this.toolkit!!.lb + (viewY * projectionScaleY.toFloat() / projectionDepth.toFloat()).toInt()
			} else {
				clipped = true
			}
			sampleY = this.maxY
			@Pc(1627) val maxYViewX: Float = tx + m11 * midX.toFloat() + m12 * sampleY.toFloat() + m13 * midZ.toFloat()
			@Pc(1644) val maxYViewY: Float = ty + m21 * midX.toFloat() + m22 * sampleY.toFloat() + m23 * midZ.toFloat()
			@Pc(1661) val maxYDepth: Float = tz + m31 * midX.toFloat() + m32 * sampleY.toFloat() + m33 * midZ.toFloat()
			@Pc(1669) var projectionDepth: Int
			if (maxYDepth >= zNear.toFloat()) {
				projectionDepth = maxYDepth.toInt()
				if (orthoDepth != -1) {
					projectionDepth = orthoDepth
				}
				cylinder.right = this.toolkit!!.projectionCenterX + (maxYViewX * projectionScaleX.toFloat() / projectionDepth.toFloat()).toInt()
				cylinder.top = this.toolkit!!.lb + (maxYViewY * projectionScaleY.toFloat() / projectionDepth.toFloat()).toInt()
			} else {
				clipped = true
			}
			if (clipped) {
				if (depth < zNear.toFloat() && maxYDepth < zNear.toFloat()) {
					visible = false
				} else {
					@Pc(1745) var nearX: Int
					@Pc(1754) var nearY: Int
					@Pc(1756) var nearDepth: Int
					@Pc(1736) var t: Float
					if (depth < zNear.toFloat()) {
						t = (maxYDepth - this.toolkit!!.zNear.toFloat()) / (maxYDepth - depth)
						nearX = (maxYViewX + (maxYViewX - viewX) * t).toInt()
						nearY = (maxYViewY + (maxYViewY - viewY) * t).toInt()
						nearDepth = zNear
						if (orthoDepth != -1) {
							nearDepth = orthoDepth
						}
						cylinder.left = this.toolkit!!.projectionCenterX + nearX * projectionScaleX / nearDepth
						cylinder.bottom = this.toolkit!!.lb + nearY * projectionScaleY / nearDepth
					} else if (maxYDepth < zNear.toFloat()) {
						t = (depth - zNear.toFloat()) / (depth - maxYDepth)
						nearX = (viewX + (viewX - maxYViewX) * t).toInt()
						nearY = (viewY + (viewY - maxYViewY) * t).toInt()
						nearDepth = zNear
						if (orthoDepth != -1) {
							nearDepth = orthoDepth
						}
						cylinder.left = this.toolkit!!.projectionCenterX + nearX * projectionScaleX / nearDepth
						cylinder.bottom = this.toolkit!!.lb + nearY * projectionScaleY / nearDepth
					}
				}
			}
			if (visible) {
				if (depth > maxYDepth) {
					projectionDepth = depth.toInt()
					if (orthoDepth != -1) {
						projectionDepth = orthoDepth
					}
					cylinder.size = this.toolkit!!.projectionCenterX + ((viewX + this.radius.toFloat()) * projectionScaleX.toFloat() / projectionDepth.toFloat()).toInt() - cylinder.left
				} else {
					projectionDepth = maxYDepth.toInt()
					if (orthoDepth != -1) {
						projectionDepth = orthoDepth
					}
					cylinder.size = this.toolkit!!.projectionCenterX + ((maxYViewX + this.radius.toFloat()) * projectionScaleX.toFloat() / projectionDepth.toFloat()).toInt() - cylinder.right
				}
				cylinder.positionCalculated = true
			}
		}
		this.applyLighting(true)
		this.rasterizer!!.fastScanline = (flags and 0x1) == 0
		this.rasterizer!!.halfBlend = false
		try {
			this.drawFacesRgb(nearClipped, this.threadResource!!.fogActive && far > this.threadResource!!.fogPlane || this.threadResource!!.water, near, far - near)
		} catch (@Pc(1955) ex2: Exception) {
		}
		if (this.billboardFaces != null) {
			i = 0
			while (i < this.faceCount) {
				this.faceBillboard!![i] = -1
				i++
			}
		}
		this.rasterizer = null
		if (this.toolkit!!.threadCount > 1) {
			withLock(this) {
				this.rendering = false
				lockNotifyAll(this)
			}
		}
	}

	@OriginalMember(owner = "client!jga", name = "r", descriptor = "()Z")
	override fun r(): Boolean {
		return this.movingTextures
	}

	@OriginalMember(owner = "client!jga", name = "s", descriptor = "()V")
	private fun calculateBounds() {
		if (this.boundsValid) {
			return
		}
		@Pc(5) var maxRadiusSq: Int = 0
		@Pc(7) var maxSphereSq: Int = 0
		@Pc(9) var lowX: Int = 32767
		@Pc(11) var lowY: Int = 32767
		@Pc(13) var lowZ: Int = 32767
		@Pc(15) var highX: Int = -32768
		@Pc(17) var highY: Int = -32768
		@Pc(19) var highZ: Int = -32768
		@Pc(21) var i: Int = 0
		while (i < this.maxVertex) {
			@Pc(27) val vx: Int = this.vertexX!![i]
			@Pc(32) val vy: Int = this.vertexY!![i]
			@Pc(37) val vz: Int = this.vertexZ!![i]
			if (vx < lowX) {
				lowX = vx
			}
			if (vx > highX) {
				highX = vx
			}
			if (vy < lowY) {
				lowY = vy
			}
			if (vy > highY) {
				highY = vy
			}
			if (vz < lowZ) {
				lowZ = vz
			}
			if (vz > highZ) {
				highZ = vz
			}
			@Pc(75) var distSq: Int = vx * vx + vz * vz
			if (distSq > maxRadiusSq) {
				maxRadiusSq = distSq
			}
			distSq += vy * vy
			if (distSq > maxSphereSq) {
				maxSphereSq = distSq
			}
			i++
		}
		this.minX = lowX.toShort()
		this.maxX = highX.toShort()
		this.minY = lowY.toShort()
		this.maxY = highY.toShort()
		this.minZ = lowZ.toShort()
		this.maxZ = highZ.toShort()
		this.radius = (kotlin.math.sqrt(maxRadiusSq.toDouble()) + 0.99).toInt().toShort()
		this.sphereRadius = (kotlin.math.sqrt(maxSphereSq.toDouble()) + 0.99).toInt().toShort()
		this.boundsValid = true
	}

	@OriginalMember(owner = "client!jga", name = "f", descriptor = "(I)I")
	private fun clampLightness(@OriginalArg(0) lightness: Int): Int {
		var lightness: Int = lightness
		if (lightness < 2) {
			lightness = 2
		} else if (lightness > 126) {
			lightness = 126
		}
		return lightness
	}

	@OriginalMember(owner = "client!jga", name = "b", descriptor = "(Z)V")
	private fun calculateLighting(@OriginalArg(0) discardShading: Boolean) {
		if (this.lightingState == 1) {
			this.relight()
		} else if (this.lightingState == 2) {
			if ((this.functionMask and 0x97098) == 0 && this.texCoordU == null) {
				this.faceColour = null
			}
			if (discardShading) {
				this.shadingType = null
			}
		} else {
			this.ensureNormals()
			@Pc(33) val sunX: Int = this.toolkit!!.sunX
			@Pc(37) val sunY: Int = this.toolkit!!.sunY
			@Pc(41) val sunZ: Int = this.toolkit!!.sunZ
			@Pc(47) val ambientLight: Int = this.toolkit!!.ambient shr 8
			@Pc(56) val sunIntensity: Int = this.toolkit!!.sunIntensity * 768 / this.contrast
			@Pc(65) val reverseSunIntensity: Int = this.toolkit!!.reverseSunIntensity * 768 / this.contrast
			if (this.faceColourA == null) {
				this.faceColourA = IntArray(this.faceCount)
				this.faceColourB = IntArray(this.faceCount)
				this.faceColourC = IntArray(this.faceCount)
			}
			@Pc(85) var face: Int = 0
			while (face < this.faceCount) {
				@Pc(91) var shading: Byte
				if (this.shadingType == null) {
					shading = 0
				} else {
					shading = this.shadingType!![face]
				}
				@Pc(102) var alpha: Byte
				if (this.faceAlpha == null) {
					alpha = 0
				} else {
					alpha = this.faceAlpha!![face]
				}
				@Pc(113) var texture: Short
				if (this.faceTextures == null) {
					texture = -1
				} else {
					texture = this.faceTextures!![face]
				}
				if (alpha.toInt() == -2) {
					shading = 3
				}
				if (alpha.toInt() == -1) {
					shading = 2
				}
				@Pc(141) var hsl: Int
				@Pc(223) var local223: Int
				if (texture.toInt() == -1) {
					@Pc(206) var local206: Int
					@Pc(213) var local213: Int
					@Pc(158) var hsv: Short
					@Pc(150) var lightness: Int
					if (shading.toInt() == 0) {
						hsl = this.faceColour!![face].toInt() and 0xFFFF
						lightness = (hsl and 0x7F) * this.ambient shr 7
						hsv = ColorUtil.hslToHsv((hsl and 0xFFFFFF80.toInt()) or lightness)
						@Pc(177) var normal: VertexNormal?
						if (this.vertexNormalsOffset == null || this.vertexNormalsOffset!![this.faceA!![face].toInt()] == null) {
							normal = this.lb!![this.faceA!![face].toInt()]
						} else {
							normal = this.vertexNormalsOffset!![this.faceA!![face].toInt()]
						}
						var dot: Int = (sunX * normal!!.x + sunY * normal.y + sunZ * normal.z) / normal.magnitude shr 16
						var intensity: Int = if (dot > 256) sunIntensity else reverseSunIntensity
						var light: Int = (ambientLight shr 1) + (intensity * dot shr 17)
						this.faceColourA!![face] = (light shl 17) or JavaModel.applyLightness(light, hsv.toInt())
						if (this.vertexNormalsOffset == null || this.vertexNormalsOffset!![this.faceB!![face].toInt()] == null) {
							normal = this.lb!![this.faceB!![face].toInt()]
						} else {
							normal = this.vertexNormalsOffset!![this.faceB!![face].toInt()]
						}
						dot = (sunX * normal!!.x + sunY * normal.y + sunZ * normal.z) / normal.magnitude shr 16
						intensity = if (dot > 256) sunIntensity else reverseSunIntensity
						light = (ambientLight shr 1) + (intensity * dot shr 17)
						this.faceColourB!![face] = (light shl 17) or JavaModel.applyLightness(light, hsv.toInt())
						if (this.vertexNormalsOffset == null || this.vertexNormalsOffset!![this.faceC!![face].toInt()] == null) {
							normal = this.lb!![this.faceC!![face].toInt()]
						} else {
							normal = this.vertexNormalsOffset!![this.faceC!![face].toInt()]
						}
						dot = (sunX * normal!!.x + sunY * normal.y + sunZ * normal.z) / normal.magnitude shr 16
						intensity = if (dot > 256) sunIntensity else reverseSunIntensity
						light = (ambientLight shr 1) + (intensity * dot shr 17)
						this.faceColourC!![face] = (light shl 17) or JavaModel.applyLightness(light, hsv.toInt())
					} else if (shading.toInt() == 1) {
						hsl = this.faceColour!![face].toInt() and 0xFFFF
						lightness = (hsl and 0x7F) * this.ambient shr 7
						hsv = ColorUtil.hslToHsv((hsl and 0xFFFFFF80.toInt()) or lightness)
						@Pc(422) val faceNormal: FaceNormal? = this.faceNormals!![face]
						val dot: Int = sunX * faceNormal!!.x + sunY * faceNormal.y + sunZ * faceNormal.z shr 16
						val intensity: Int = if (dot > 256) sunIntensity else reverseSunIntensity
						val light: Int = (ambientLight shr 1) + (intensity * dot shr 17)
						this.faceColourA!![face] = (light shl 17) or JavaModel.applyLightness(light, hsv.toInt())
						this.faceColourC!![face] = -1
					} else if (shading.toInt() == 3) {
						this.faceColourA!![face] = 128
						this.faceColourC!![face] = -1
					} else {
						this.faceColourC!![face] = -2
					}
				} else {
					hsl = this.faceColour!![face].toInt() and 0xFFFF
					@Pc(570) var local570: Int
					@Pc(551) var local551: Int
					if (shading.toInt() == 0) {
						@Pc(522) var vertexNormal: VertexNormal?
						if (this.vertexNormalsOffset == null || this.vertexNormalsOffset!![this.faceA!![face].toInt()] == null) {
							vertexNormal = this.lb!![this.faceA!![face].toInt()]
						} else {
							vertexNormal = this.vertexNormalsOffset!![this.faceA!![face].toInt()]
						}
						var dot: Int = (sunX * vertexNormal!!.x + sunY * vertexNormal.y + sunZ * vertexNormal.z) / vertexNormal.magnitude shr 16
						var intensity: Int = if (dot > 256) sunIntensity else reverseSunIntensity
						var light: Int = this.clampLightness((ambientLight shr 2) + (intensity * dot shr 18))
						this.faceColourA!![face] = (light shl 24) or this.shadeTexturedRgb(hsl, texture, light)
						if (this.vertexNormalsOffset == null || this.vertexNormalsOffset!![this.faceB!![face].toInt()] == null) {
							vertexNormal = this.lb!![this.faceB!![face].toInt()]
						} else {
							vertexNormal = this.vertexNormalsOffset!![this.faceB!![face].toInt()]
						}
						dot = (sunX * vertexNormal!!.x + sunY * vertexNormal.y + sunZ * vertexNormal.z) / vertexNormal.magnitude shr 16
						intensity = if (dot > 256) sunIntensity else reverseSunIntensity
						light = this.clampLightness((ambientLight shr 2) + (intensity * dot shr 18))
						this.faceColourB!![face] = (light shl 24) or this.shadeTexturedRgb(hsl, texture, light)
						if (this.vertexNormalsOffset == null || this.vertexNormalsOffset!![this.faceC!![face].toInt()] == null) {
							vertexNormal = this.lb!![this.faceC!![face].toInt()]
						} else {
							vertexNormal = this.vertexNormalsOffset!![this.faceC!![face].toInt()]
						}
						dot = (sunX * vertexNormal!!.x + sunY * vertexNormal.y + sunZ * vertexNormal.z) / vertexNormal.magnitude shr 16
						intensity = if (dot > 256) sunIntensity else reverseSunIntensity
						light = this.clampLightness((ambientLight shr 2) + (intensity * dot shr 18))
						this.faceColourC!![face] = (light shl 24) or this.shadeTexturedRgb(hsl, texture, light)
					} else if (shading.toInt() == 1) {
						@Pc(752) val faceNormal2: FaceNormal? = this.faceNormals!![face]
						val dot: Int = sunX * faceNormal2!!.x + sunY * faceNormal2.y + sunZ * faceNormal2.z shr 16
						val intensity: Int = if (dot > 256) sunIntensity else reverseSunIntensity
						val light: Int = this.clampLightness((ambientLight shr 2) + (intensity * dot shr 18))
						this.faceColourA!![face] = (light shl 24) or this.shadeTexturedRgb(hsl, texture, light)
						this.faceColourC!![face] = -1
					} else {
						this.faceColourC!![face] = -2
					}
				}
				face++
			}
			this.lb = null
			this.vertexNormalsOffset = null
			this.faceNormals = null
			if ((this.functionMask and 0x97098) == 0 && this.texCoordU == null) {
				this.faceColour = null
			}
			if (discardShading) {
				this.shadingType = null
			}
			this.lightingState = 2
		}
	}

	@OriginalMember(owner = "client!jga", name = "a", descriptor = "(BIZ)Lclient!ka;")
	override fun copy(@OriginalArg(0) copyType: Byte, @OriginalArg(1) functionMask: Int, @OriginalArg(2) ensureLit: Boolean): Model? {
		this.useThreadCopyCache(lang.currentThread())
		@Pc(11) var javaModel: JavaModel?
		@Pc(8) var javaModel2: JavaModel?
		if (copyType.toInt() == 1) {
			javaModel2 = this.copySrcModel1
			javaModel = this.copyDstModel1
		} else if (copyType.toInt() == 2) {
			javaModel2 = this.copySrcModel2
			javaModel = this.copyDstModel2
		} else if (copyType.toInt() == 3) {
			javaModel2 = this.copySrcModel3
			javaModel = this.copyDstModel3
		} else if (copyType.toInt() == 4) {
			javaModel2 = this.copySrcModel4
			javaModel = this.copyDstModel4
		} else if (copyType.toInt() == 5) {
			javaModel2 = this.copySrcModel5
			javaModel = this.copyDstModel5
		} else {
			javaModel2 = JavaModel(this.toolkit)
			javaModel = javaModel2
		}
		return this.copyTo(javaModel, javaModel2, functionMask, copyType.toInt() != 0, ensureLit)
	}

	@OriginalMember(owner = "client!jga", name = "O", descriptor = "(III)V")
	override fun O(@OriginalArg(0) scaleX: Int, @OriginalArg(1) scaleY: Int, @OriginalArg(2) scaleZ: Int) {
		if (scaleX != 128 && (this.functionMask and 0x1) != 1) {
			throw IllegalStateException()
		} else if (scaleY != 128 && (this.functionMask and 0x2) != 2) {
			throw IllegalStateException()
		} else if (scaleZ == 128 || (this.functionMask and 0x4) == 4) {
			withLock(this) {
				@Pc(44) var i: Int = 0
				while (i < this.vertexCount) {
					this.vertexX!![i] = this.vertexX!![i] * scaleX shr 7
					this.vertexY!![i] = this.vertexY!![i] * scaleY shr 7
					this.vertexZ!![i] = this.vertexZ!![i] * scaleZ shr 7
					i++
				}
				this.boundsValid = false
			}
		} else {
			throw IllegalStateException()
		}
	}

	@OriginalMember(owner = "client!jga", name = "ua", descriptor = "()I")
	override fun ua(): Int {
		return this.functionMask
	}

	@OriginalMember(owner = "client!jga", name = "g", descriptor = "(I)V")
	private fun drawFoggedTriangleRgb(@OriginalArg(0) face: Int) {
		@Pc(8) var a: Short
		@Pc(13) var b: Short
		@Pc(18) var c: Short
		@Pc(27) var fogA: Int
		@Pc(46) var fogB: Int
		@Pc(65) var fogC: Int
		@Pc(81) var local81: Int
		@Pc(325) var local325: Int
		if (this.threadResource!!.water) {
			a = this.faceA!![face]
			b = this.faceB!![face]
			c = this.faceC!![face]
			fogA = 0
			fogB = 0
			fogC = 0
			if (this.worldY!![a.toInt()] > this.threadResource!!.waterDepth) {
				fogA = 255
			} else if (this.worldY!![a.toInt()] > this.threadResource!!.waterHeight) {
				fogA = (this.threadResource!!.waterHeight - this.worldY!![a.toInt()]) * 255 / (this.threadResource!!.waterHeight - this.threadResource!!.waterDepth)
			}
			if (this.worldY!![b.toInt()] > this.threadResource!!.waterDepth) {
				fogB = 255
			} else if (this.worldY!![b.toInt()] > this.threadResource!!.waterHeight) {
				fogB = (this.threadResource!!.waterHeight - this.worldY!![b.toInt()]) * 255 / (this.threadResource!!.waterHeight - this.threadResource!!.waterDepth)
			}
			if (this.worldY!![c.toInt()] > this.threadResource!!.waterDepth) {
				fogC = 255
			} else if (this.worldY!![c.toInt()] > this.threadResource!!.waterHeight) {
				fogC = (this.threadResource!!.waterHeight - this.worldY!![c.toInt()]) * 255 / (this.threadResource!!.waterHeight - this.threadResource!!.waterDepth)
			}
			if (this.faceAlpha == null) {
				this.rasterizer!!.alpha = 0
			} else {
				this.rasterizer!!.alpha = this.faceAlpha!![face].toInt() and 0xFF
			}
			if (this.faceTextures != null && this.faceTextures!![face].toInt() != -1) {
				var alphaBits: Int = -16777216
				if (this.faceAlpha != null) {
					alphaBits = 255 - (this.faceAlpha!![face].toInt() and 0xFF) shl 24
				}
				if (this.faceColourC!![face] == -1) {
					val colour: Int = alphaBits or (this.faceColourA!![face] and 0xFFFFFF)
					this.rasterizer!!.renderTexturedTriangleRgb(this.screenY!![a.toInt()].toFloat(), this.screenY!![b.toInt()].toFloat(), this.screenY!![c.toInt()].toFloat(), this.screenX!![a.toInt()].toFloat(), this.screenX!![b.toInt()].toFloat(), this.screenX!![c.toInt()].toFloat(), this.screenZ!![a.toInt()].toFloat(), this.screenZ!![b.toInt()].toFloat(), this.screenZ!![c.toInt()].toFloat(), this.texCoordU!![face]!![0], this.texCoordU!![face]!![1], this.texCoordU!![face]!![2], this.texCoordV!![face]!![0], this.texCoordV!![face]!![1], this.texCoordV!![face]!![2], colour, colour, colour, this.threadResource!!.fogColour, fogA, fogB, fogC, this.faceTextures!![face].toInt())
				} else {
					this.rasterizer!!.renderTexturedTriangleRgb(this.screenY!![a.toInt()].toFloat(), this.screenY!![b.toInt()].toFloat(), this.screenY!![c.toInt()].toFloat(), this.screenX!![a.toInt()].toFloat(), this.screenX!![b.toInt()].toFloat(), this.screenX!![c.toInt()].toFloat(), this.screenZ!![a.toInt()].toFloat(), this.screenZ!![b.toInt()].toFloat(), this.screenZ!![c.toInt()].toFloat(), this.texCoordU!![face]!![0], this.texCoordU!![face]!![1], this.texCoordU!![face]!![2], this.texCoordV!![face]!![0], this.texCoordV!![face]!![1], this.texCoordV!![face]!![2], alphaBits or (this.faceColourA!![face] and 0xFFFFFF), alphaBits or (this.faceColourB!![face] and 0xFFFFFF), alphaBits or (this.faceColourC!![face] and 0xFFFFFF), this.threadResource!!.fogColour, fogA, fogB, fogC, this.faceTextures!![face].toInt())
				}
			} else if (this.faceColourC!![face] == -1) {
				this.rasterizer!!.renderTriangleRgb(this.screenY!![a.toInt()].toFloat(), this.screenY!![b.toInt()].toFloat(), this.screenY!![c.toInt()].toFloat(), this.screenX!![a.toInt()].toFloat(), this.screenX!![b.toInt()].toFloat(), this.screenX!![c.toInt()].toFloat(), this.screenZ!![a.toInt()].toFloat(), this.screenZ!![b.toInt()].toFloat(), this.screenZ!![c.toInt()].toFloat(), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable!![this.faceColourA!![face] and 0xFFFF], (fogA shl 24) or this.threadResource!!.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable!![this.faceColourA!![face] and 0xFFFF], (fogB shl 24) or this.threadResource!!.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable!![this.faceColourA!![face] and 0xFFFF], (fogC shl 24) or this.threadResource!!.fogColour))
			} else {
				this.rasterizer!!.renderTriangleRgb(this.screenY!![a.toInt()].toFloat(), this.screenY!![b.toInt()].toFloat(), this.screenY!![c.toInt()].toFloat(), this.screenX!![a.toInt()].toFloat(), this.screenX!![b.toInt()].toFloat(), this.screenX!![c.toInt()].toFloat(), this.screenZ!![a.toInt()].toFloat(), this.screenZ!![b.toInt()].toFloat(), this.screenZ!![c.toInt()].toFloat(), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable!![this.faceColourA!![face] and 0xFFFF], (fogA shl 24) or this.threadResource!!.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable!![this.faceColourB!![face] and 0xFFFF], (fogB shl 24) or this.threadResource!!.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable!![this.faceColourC!![face] and 0xFFFF], (fogC shl 24) or this.threadResource!!.fogColour))
			}
			return
		}
		a = this.faceA!![face]
		b = this.faceB!![face]
		c = this.faceC!![face]
		fogA = this.screenZ!![a.toInt()] - this.threadResource!!.fogPlane
		if (fogA > 255) {
			fogA = 255
		} else if (fogA < 0) {
			fogA = 0
		}
		fogB = this.screenZ!![b.toInt()] - this.threadResource!!.fogPlane
		if (fogB > 255) {
			fogB = 255
		} else if (fogB < 0) {
			fogB = 0
		}
		fogC = this.screenZ!![c.toInt()] - this.threadResource!!.fogPlane
		if (fogC > 255) {
			fogC = 255
		} else if (fogC < 0) {
			fogC = 0
		}
		val fogSum: Int = fogA + fogB + fogC
		if (fogSum == 765) {
			return
		}
		if (fogSum == 0) {
			this.drawTriangleRgb(face)
			return
		}
		if (this.faceAlpha == null) {
			this.rasterizer!!.alpha = 0
		} else {
			this.rasterizer!!.alpha = this.faceAlpha!![face].toInt() and 0xFF
		}
		if (this.faceTextures != null && this.faceTextures!![face].toInt() != -1) {
			var alphaBits: Int = -16777216
			if (this.faceAlpha != null) {
				alphaBits = 255 - (this.faceAlpha!![face].toInt() and 0xFF) shl 24
			}
			if (this.faceColourC!![face] == -1) {
				@Pc(354) val colour: Int = alphaBits or (this.faceColourA!![face] and 0xFFFFFF)
				this.rasterizer!!.renderTexturedTriangleRgb(this.screenY!![a.toInt()].toFloat(), this.screenY!![b.toInt()].toFloat(), this.screenY!![c.toInt()].toFloat(), this.screenX!![a.toInt()].toFloat(), this.screenX!![b.toInt()].toFloat(), this.screenX!![c.toInt()].toFloat(), this.screenZ!![a.toInt()].toFloat(), this.screenZ!![b.toInt()].toFloat(), this.screenZ!![c.toInt()].toFloat(), this.texCoordU!![face]!![0], this.texCoordU!![face]!![1], this.texCoordU!![face]!![2], this.texCoordV!![face]!![0], this.texCoordV!![face]!![1], this.texCoordV!![face]!![2], colour, colour, colour, this.threadResource!!.fogColour, fogA, fogB, fogC, this.faceTextures!![face].toInt())
			} else {
				this.rasterizer!!.renderTexturedTriangleRgb(this.screenY!![a.toInt()].toFloat(), this.screenY!![b.toInt()].toFloat(), this.screenY!![c.toInt()].toFloat(), this.screenX!![a.toInt()].toFloat(), this.screenX!![b.toInt()].toFloat(), this.screenX!![c.toInt()].toFloat(), this.screenZ!![a.toInt()].toFloat(), this.screenZ!![b.toInt()].toFloat(), this.screenZ!![c.toInt()].toFloat(), this.texCoordU!![face]!![0], this.texCoordU!![face]!![1], this.texCoordU!![face]!![2], this.texCoordV!![face]!![0], this.texCoordV!![face]!![1], this.texCoordV!![face]!![2], alphaBits or (this.faceColourA!![face] and 0xFFFFFF), alphaBits or (this.faceColourB!![face] and 0xFFFFFF), alphaBits or (this.faceColourC!![face] and 0xFFFFFF), this.threadResource!!.fogColour, fogA, fogB, fogC, this.faceTextures!![face].toInt())
			}
		} else if (this.faceColourC!![face] == -1) {
			this.rasterizer!!.renderTriangleRgb(this.screenY!![a.toInt()].toFloat(), this.screenY!![b.toInt()].toFloat(), this.screenY!![c.toInt()].toFloat(), this.screenX!![a.toInt()].toFloat(), this.screenX!![b.toInt()].toFloat(), this.screenX!![c.toInt()].toFloat(), this.screenZ!![a.toInt()].toFloat(), this.screenZ!![b.toInt()].toFloat(), this.screenZ!![c.toInt()].toFloat(), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable!![this.faceColourA!![face] and 0xFFFF], (fogA shl 24) or this.threadResource!!.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable!![this.faceColourA!![face] and 0xFFFF], (fogB shl 24) or this.threadResource!!.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable!![this.faceColourA!![face] and 0xFFFF], (fogC shl 24) or this.threadResource!!.fogColour))
		} else {
			this.rasterizer!!.renderTriangleRgb(this.screenY!![a.toInt()].toFloat(), this.screenY!![b.toInt()].toFloat(), this.screenY!![c.toInt()].toFloat(), this.screenX!![a.toInt()].toFloat(), this.screenX!![b.toInt()].toFloat(), this.screenX!![c.toInt()].toFloat(), this.screenZ!![a.toInt()].toFloat(), this.screenZ!![b.toInt()].toFloat(), this.screenZ!![c.toInt()].toFloat(), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable!![this.faceColourA!![face] and 0xFFFF], (fogA shl 24) or this.threadResource!!.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable!![this.faceColourB!![face] and 0xFFFF], (fogB shl 24) or this.threadResource!!.fogColour), ColorUtil.alphaBlend(ColorUtil.hsvToRgbTable!![this.faceColourC!![face] and 0xFFFF], (fogC shl 24) or this.threadResource!!.fogColour))
		}
	}

	companion object {

		@OriginalMember(owner = "client!vw", name = "a", descriptor = "(III)I")
		@JvmStatic
		fun applyLightness(@OriginalArg(1) light: Int, @OriginalArg(2) hsl: Int): Int {
			var light: Int = light
			light = light * (hsl and 0x7F) shr 7
			if (light < 2) {
				light = 2
			} else if (light > 126) {
				light = 126
			}
			return (hsl and 0xFF80) + light
		}
	}
}
