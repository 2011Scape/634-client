package com.jagex.game.runetek5.graphics.model

import com.jagex.game.runetek5.graphics.texture.TextureTransformationMatrix
import com.jagex.game.runetek5.GpuVertexNormal
import com.jagex.game.runetek5.GpuFaceNormal
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.GpuModelIndexStream
import com.jagex.game.runetek5.GpuModelVertexStream
import com.jagex.game.runetek5.cache.node.lru.GpuShadow
import com.jagex.game.runetek5.graphics.model.billboard.BillboardType
import com.jagex.game.runetek5.NativeMatrix
import com.jagex.game.runetek5.NativeBillboardAttributes
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.normal.MeshBillboard
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import jaclib.memory.Buffer
import jaclib.memory.Stream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.entity.BoundingBoxPolygon
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit
import com.jagex.game.runetek5.graphics.GpuModelNormals
import com.jagex.game.runetek5.graphics.GpuIndexBuffer
import com.jagex.game.runetek5.graphics.NativeBillboardFace
import com.jagex.game.runetek5.toolkit.companion.NativeStream
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.PrimitiveType
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.graphics.model.billboard.BillboardTypeList
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField

@OriginalClass("client!nca")
class GpuModel : Model {

	@OriginalMember(owner = "client!nca", name = "o", descriptor = "I")
	private var size3d: Int = 0

	@OriginalMember(owner = "client!nca", name = "p", descriptor = "Z")
	private var updateRequired: Boolean = false

	@OriginalMember(owner = "client!nca", name = "F", descriptor = "I")
	private var minZ: Int = 0

	@OriginalMember(owner = "client!nca", name = "G", descriptor = "Lclient!wga;")
	private val nativeToolkit: AbstractNativeToolkit

	@OriginalMember(owner = "client!nca", name = "L", descriptor = "Lclient!cd;")
	private var normalStream: GpuModelVertexStream? = null

	@OriginalMember(owner = "client!nca", name = "eb", descriptor = "I")
	private var maxZ: Int = 0

	@OriginalMember(owner = "client!nca", name = "vb", descriptor = "Lclient!cd;")
	private var texCoordStream: GpuModelVertexStream? = null

	@OriginalMember(owner = "client!nca", name = "wb", descriptor = "I")
	private var minY: Int = 0

	@OriginalMember(owner = "client!nca", name = "xb", descriptor = "Z")
	private var dynamic: Boolean = false

	@OriginalMember(owner = "client!nca", name = "yb", descriptor = "I")
	private var functionMask: Int = 0

	@OriginalMember(owner = "client!nca", name = "Cb", descriptor = "I")
	private var maxX: Int = 0

	@OriginalMember(owner = "client!nca", name = "Hb", descriptor = "I")
	private var preferenceFlags: Int = 0

	@OriginalMember(owner = "client!nca", name = "Kb", descriptor = "I")
	private var numNormals: Int = 0

	@OriginalMember(owner = "client!nca", name = "Mb", descriptor = "Z")
	private var boundsCalculated: Boolean = false

	@OriginalMember(owner = "client!nca", name = "Rb", descriptor = "Lclient!cd;")
	private var colourStream: GpuModelVertexStream? = null

	@OriginalMember(owner = "client!nca", name = "Ub", descriptor = "Lclient!qq;")
	private var indexStream: GpuModelIndexStream? = null

	@OriginalMember(owner = "client!nca", name = "mc", descriptor = "Lclient!ok;")
	private var mergedNormals: GpuModelNormals? = null

	@OriginalMember(owner = "client!nca", name = "qc", descriptor = "I")
	private var minX: Int = 0

	@OriginalMember(owner = "client!nca", name = "sc", descriptor = "I")
	private var maxY: Int = 0

	@OriginalMember(owner = "client!nca", name = "tc", descriptor = "I")
	private var size2d: Int = 0

	@OriginalMember(owner = "client!nca", name = "Dc", descriptor = "Lclient!cd;")
	private var positionStream: GpuModelVertexStream? = null

	@OriginalMember(owner = "client!nca", name = "qb", descriptor = "[I")
	private var triangles: IntArray? = null

	@OriginalMember(owner = "client!nca", name = "Nb", descriptor = "I")
	private var numFaces: Int = 0

	@OriginalMember(owner = "client!nca", name = "zc", descriptor = "I")
	private var renderedFaceCount: Int = 0

	@OriginalMember(owner = "client!nca", name = "P", descriptor = "Z")
	private var hasAlphaTransition: Boolean = false

	@OriginalMember(owner = "client!nca", name = "lc", descriptor = "Z")
	private var textureAnimating: Boolean = false

	@OriginalMember(owner = "client!nca", name = "gc", descriptor = "I")
	private var numVertices: Int = 0

	@OriginalMember(owner = "client!nca", name = "Q", descriptor = "[I")
	private var verticesZ: IntArray? = null

	@OriginalMember(owner = "client!nca", name = "Cc", descriptor = "[I")
	private var verticesY: IntArray? = null

	@OriginalMember(owner = "client!nca", name = "dc", descriptor = "[S")
	private var verticesSubmeshes: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "U", descriptor = "I")
	private var maxDepth: Int = 0

	@OriginalMember(owner = "client!nca", name = "uc", descriptor = "[I")
	private var verticesX: IntArray? = null

	@OriginalMember(owner = "client!nca", name = "Jb", descriptor = "[Lclient!eca;")
	private var effectiveVertices: Array<EffectiveVertex?>? = null

	@OriginalMember(owner = "client!nca", name = "sb", descriptor = "[Lclient!nn;")
	private var emissiveTriangles: Array<EmissiveTriangle?>? = null

	@OriginalMember(owner = "client!nca", name = "t", descriptor = "I")
	private var billboardCount: Int = 0

	@OriginalMember(owner = "client!nca", name = "Hc", descriptor = "[Lclient!en;")
	private var billboardFaces: Array<NativeBillboardFace?>? = null

	@OriginalMember(owner = "client!nca", name = "db", descriptor = "[Lclient!dk;")
	private var billboardAttributes: Array<NativeBillboardAttributes?>? = null

	@OriginalMember(owner = "client!nca", name = "ab", descriptor = "[S")
	private var vertexNormalIndices: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "rc", descriptor = "[S")
	private var facesA: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "K", descriptor = "[S")
	private var facesMaterial: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "pc", descriptor = "[F")
	private var textureV: FloatArray? = null

	@OriginalMember(owner = "client!nca", name = "X", descriptor = "[S")
	private var normalsZ: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "Vb", descriptor = "[S")
	private var facesColor: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "ic", descriptor = "[B")
	private var magnitudes: ByteArray? = null

	@OriginalMember(owner = "client!nca", name = "vc", descriptor = "[S")
	private var facesC: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "cc", descriptor = "[S")
	private var normalsX: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "T", descriptor = "[S")
	private var facesSubmeshes: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "Fb", descriptor = "[S")
	private var normalVertices: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "D", descriptor = "[S")
	private var normalFaces: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "ec", descriptor = "[S")
	private var facesB: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "nb", descriptor = "S")
	private var ambient: Short = 0

	@OriginalMember(owner = "client!nca", name = "yc", descriptor = "[B")
	private var facesAlpha: ByteArray? = null

	@OriginalMember(owner = "client!nca", name = "Lb", descriptor = "[F")
	private var textureU: FloatArray? = null

	@OriginalMember(owner = "client!nca", name = "O", descriptor = "S")
	private var contrast: Short = 0

	@OriginalMember(owner = "client!nca", name = "Z", descriptor = "[S")
	private var normalsY: ShortArray? = null

	@OriginalMember(owner = "client!nca", name = "C", descriptor = "[I")
	private var materialBatchMinIndex: IntArray? = null

	@OriginalMember(owner = "client!nca", name = "r", descriptor = "[I")
	private var materialBatchOffsets: IntArray? = null

	@OriginalMember(owner = "client!nca", name = "Ib", descriptor = "[I")
	private var materialBatchVertexCount: IntArray? = null

	@OriginalMember(owner = "client!nca", name = "A", descriptor = "[[I")
	private var vertexGroups: Array<IntArray?>? = null

	@OriginalMember(owner = "client!nca", name = "z", descriptor = "[[I")
	private var billboardGroups: Array<IntArray?>? = null

	@OriginalMember(owner = "client!nca", name = "bc", descriptor = "[[I")
	private var faceGroups: Array<IntArray?>? = null


	@OriginalMember(owner = "client!nca", name = "<init>", descriptor = "(Lclient!wga;Lclient!bh;IIII)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit, @OriginalArg(1) mesh: Mesh, @OriginalArg(2) functionMask: Int, @OriginalArg(3) ambient: Int, @OriginalArg(4) contrast: Int, @OriginalArg(5) preferenceFlags: Int) : this(nativeToolkit, functionMask, preferenceFlags, true, false) {
		@Pc(11) val textureSource: MaterialProvider? = nativeToolkit.materialProvider
		@Pc(15) val faces = IntArray(mesh.numFaces)
		this.triangles = IntArray(mesh.maxDepth + 1)
		for (@Pc(24) i in 0 until mesh.numFaces) {
			if (mesh.faceRenderType == null || mesh.faceRenderType!![i].toInt() != 2) {
				if (mesh.facesMaterial != null && mesh.facesMaterial!![i].toInt() != -1) {
					@Pc(56) val textureMetrics: MaterialType? = textureSource!!.getMetrics(mesh.facesMaterial!![i].toInt() and 0xFFFF)
					if (((this.preferenceFlags and 0x40) == 0 || !textureMetrics!!.detailsOnly) && textureMetrics!!.skipTriangles) {
						continue
					}
				}
				faces[this.numFaces++] = i
				this.triangles!![mesh.facesA!![i].toInt()]++
				this.triangles!![mesh.facesB!![i].toInt()]++
				this.triangles!![mesh.facesC!![i].toInt()]++
			}
		}
		this.renderedFaceCount = this.numFaces
		@Pc(125) val sortKeys = LongArray(this.numFaces)
		@Pc(137) val modifiesAlpha: Boolean = (this.functionMask and 0x100) != 0
		@Pc(149) val local149: Int
		@Pc(162) val local162: Int
		@Pc(322) val local322: Int
		for (@Pc(139) j in 0 until this.numFaces) {
			@Pc(145) val face: Int = faces[j]
			@Pc(147) var textureMetrics2: MaterialType? = null
			var sortKeyHigh = 0
			@Pc(151) val renderFlags: Byte = 0
			@Pc(153) var effectType: Byte = 0
			@Pc(155) var effectParam1: Byte = 0
			if (mesh.billboards != null) {
				@Pc(160) var hasUid = false
				for (billboardIndex in 0 until mesh.billboards!!.size) {
					@Pc(169) val meshBillboard: MeshBillboard = mesh.billboards!![billboardIndex]!!
					if (meshBillboard.appliesOnFace == face) {
						@Pc(178) val lightType: BillboardType = BillboardTypeList.list(meshBillboard.billboardId)
						if (lightType.hasUid) {
							hasUid = true
						}
						if (lightType.materialId != -1) {
							@Pc(194) val textureMetrics3: MaterialType? = textureSource!!.getMetrics(lightType.materialId)
							if (textureMetrics3!!.effectCombiner == 2) {
								this.hasAlphaTransition = true
							}
						}
					}
				}
				if (hasUid) {
					sortKeys[j] = Long.MAX_VALUE
					this.renderedFaceCount--
					continue
				}
			}
			@Pc(223) var materialId: Short = -1
			if (mesh.facesMaterial != null) {
				materialId = mesh.facesMaterial!![face]
				if (materialId.toInt() != -1) {
					textureMetrics2 = textureSource!!.getMetrics(materialId.toInt() and 0xFFFF)
					if ((this.preferenceFlags and 0x40) != 0 && textureMetrics2!!.detailsOnly) {
						materialId = -1
						textureMetrics2 = null
					} else {
						effectParam1 = textureMetrics2!!.effectParam1
						effectType = textureMetrics2.effectType
					}
				}
			}
			@Pc(284) val alphaTransition: Boolean = mesh.facesAlpha != null && mesh.facesAlpha!![face].toInt() != 0 || textureMetrics2 != null && textureMetrics2.effectCombiner != 0
			if ((modifiesAlpha || alphaTransition) && mesh.facesLayer != null) {
				sortKeyHigh += mesh.facesLayer!![face].toInt() shl 17
			}
			if (alphaTransition) {
				sortKeyHigh += 65536
			}
			sortKeyHigh += (effectType.toInt() and 0xFF) shl 8
			val materialKey: Int = renderFlags + ((materialId.toInt() and 0xFFFF) shl 16)
			sortKeyHigh += effectParam1.toInt() and 0xFF
			@Pc(334) val sortKeyLow: Int = materialKey + (j and 0xFFFF)
			sortKeys[j] = (sortKeyHigh.toLong() shl 32) + sortKeyLow.toLong()
			this.textureAnimating = this.textureAnimating or (textureMetrics2 != null && (textureMetrics2.speedU.toInt() != 0 || textureMetrics2.speedV.toInt() != 0))
			this.hasAlphaTransition = this.hasAlphaTransition or alphaTransition
		}
		ArrayUtils.quicksort(faces, sortKeys)
		this.numVertices = mesh.numVertices
		this.verticesZ = mesh.verticesZ
		this.verticesY = mesh.verticesY
		this.verticesSubmeshes = mesh.verticesSubmeshes
		this.maxDepth = mesh.maxDepth
		this.verticesX = mesh.verticesX
		this.effectiveVertices = mesh.particleEffectors
		@Pc(414) val vertexNormals = arrayOfNulls<GpuVertexNormal>(this.maxDepth)
		this.emissiveTriangles = mesh.particleEmitters
		@Pc(438) val local438: Int
		@Pc(452) val local452: Int
		@Pc(491) val local491: Int
		if (mesh.billboards != null) {
			this.billboardCount = mesh.billboards!!.size
			this.billboardFaces = arrayOfNulls<NativeBillboardFace>(this.billboardCount)
			this.billboardAttributes = arrayOfNulls<NativeBillboardAttributes>(this.billboardCount)
			for (billboardIndex in 0 until this.billboardCount) {
				@Pc(445) val meshBillboard2: MeshBillboard = mesh.billboards!![billboardIndex]!!
				@Pc(450) val lightType2: BillboardType = BillboardTypeList.list(meshBillboard2.billboardId)
				var sortedFaceIndex = -1
				for (@Pc(454) k in 0 until this.numFaces) {
					if (meshBillboard2.appliesOnFace == faces[k]) {
						sortedFaceIndex = k
						break
					}
				}
				if (sortedFaceIndex == -1) {
					throw RuntimeException()
				}
				val rgb: Int = ColorUtil.hslToRgb!![mesh.facesColor!![meshBillboard2.appliesOnFace].toInt() and 0xFFFF] and 0xFFFFFF
				@Pc(509) val diffuseColor: Int = rgb or ((255 - (if (mesh.facesAlpha == null) 0 else mesh.facesAlpha!![meshBillboard2.appliesOnFace].toInt())) shl 24)
				this.billboardFaces!![billboardIndex] = NativeBillboardFace(sortedFaceIndex, mesh.facesA!![meshBillboard2.appliesOnFace].toInt(), mesh.facesB!![meshBillboard2.appliesOnFace].toInt(), mesh.facesC!![meshBillboard2.appliesOnFace].toInt(), lightType2.size2d, lightType2.size3d, lightType2.materialId, lightType2.blendType, lightType2.shape, lightType2.hasUid, lightType2.stationary, meshBillboard2.distance)
				this.billboardAttributes!![billboardIndex] = NativeBillboardAttributes(diffuseColor)
			}
		}
		val maxNormals: Int = this.numFaces * 3
		this.vertexNormalIndices = ShortArray(maxNormals)
		this.facesA = ShortArray(this.numFaces)
		this.facesMaterial = ShortArray(this.numFaces)
		this.textureV = FloatArray(maxNormals)
		this.normalsZ = ShortArray(maxNormals)
		this.facesColor = ShortArray(this.numFaces)
		this.magnitudes = ByteArray(maxNormals)
		this.facesC = ShortArray(this.numFaces)
		this.normalsX = ShortArray(maxNormals)
		if (mesh.facesSubmeshes != null) {
			this.facesSubmeshes = ShortArray(this.numFaces)
		}
		this.normalVertices = ShortArray(maxNormals)
		this.normalFaces = ShortArray(maxNormals)
		GpuModel.normalsUid = LongArray(maxNormals)
		this.facesB = ShortArray(this.numFaces)
		this.ambient = ambient.toShort()
		this.facesAlpha = ByteArray(this.numFaces)
		this.textureU = FloatArray(maxNormals)
		this.contrast = contrast.toShort()
		this.normalsY = ShortArray(maxNormals)
		var offset = 0
		for (vertexIndex in 0 until mesh.maxDepth) {
			val normalCount: Int = this.triangles!![vertexIndex]
			this.triangles!![vertexIndex] = offset
			offset += normalCount
			vertexNormals[vertexIndex] = GpuVertexNormal()
		}
		this.triangles!![mesh.maxDepth] = offset
		@Pc(703) val textureMatrix: TextureTransformationMatrix = TextureTransformationMatrix.generateTextureMatrix(this.numFaces, mesh, faces)
		@Pc(707) val faceNormals = arrayOfNulls<GpuFaceNormal>(mesh.numFaces)
		@Pc(747) val local747: Int
		@Pc(757) val local757: Int
		@Pc(768) val local768: Int
		@Pc(726) val local726: Short
		@Pc(736) val local736: Int
		for (faceIndex in 0 until mesh.numFaces) {
			@Pc(716) val faceA: Int = mesh.facesA!![faceIndex].toInt()
			@Pc(721) val faceB: Int = mesh.facesB!![faceIndex].toInt()
			val faceC: Int = mesh.facesC!![faceIndex].toInt()
			val deltaX1: Int = this.verticesX!![faceB] - this.verticesX!![faceA]
			val deltaY1: Int = this.verticesY!![faceB] - this.verticesY!![faceA]
			val deltaZ1: Int = this.verticesZ!![faceB] - this.verticesZ!![faceA]
			val deltaX2: Int = this.verticesX!![faceC] - this.verticesX!![faceA]
			@Pc(779) val deltaY2: Int = this.verticesY!![faceC] - this.verticesY!![faceA]
			@Pc(790) val deltaZ2: Int = this.verticesZ!![faceC] - this.verticesZ!![faceA]
			@Pc(799) var normalX: Int = deltaY1 * deltaZ2 - deltaY2 * deltaZ1
			@Pc(808) var normalY: Int = deltaZ1 * deltaX2 - deltaX1 * deltaZ2
			@Pc(816) var normalZ: Int
			normalZ = deltaX1 * deltaY2 - deltaX2 * deltaY1
			while (normalX > 8192 || normalY > 8192 || normalZ > 8192 || normalX < -8192 || normalY < -8192 || normalZ < -8192) {
				normalY = normalY shr 0x1
				normalX = normalX shr 0x1
				normalZ = normalZ shr 0x1
			}
			@Pc(870) var magnitude: Int = kotlin.math.sqrt((normalY * normalY + normalX * normalX + normalZ * normalZ).toDouble()).toInt()
			if (magnitude <= 0) {
				magnitude = 1
			}
			normalZ = normalZ * 256 / magnitude
			normalY = normalY * 256 / magnitude
			normalX = normalX * 256 / magnitude
			@Pc(905) val renderType: Int = if (mesh.faceRenderType == null) 0 else mesh.faceRenderType!![faceIndex].toInt()
			if (renderType == 0) {
				@Pc(936) val vertexNormalA: GpuVertexNormal = vertexNormals[faceA]!!
				vertexNormalA.magnitude++
				vertexNormalA.y += normalY
				vertexNormalA.x += normalX
				vertexNormalA.z += normalZ
				@Pc(964) val vertexNormalB: GpuVertexNormal = vertexNormals[faceB]!!
				vertexNormalB.x += normalX
				vertexNormalB.magnitude++
				vertexNormalB.y += normalY
				vertexNormalB.z += normalZ
				@Pc(992) val vertexNormalC: GpuVertexNormal = vertexNormals[faceC]!!
				vertexNormalC.magnitude++
				vertexNormalC.x += normalX
				vertexNormalC.y += normalY
				vertexNormalC.z += normalZ
			} else if (renderType == 1) {
				faceNormals[faceIndex] = GpuFaceNormal()
				@Pc(921) val faceNormal: GpuFaceNormal = faceNormals[faceIndex]!!
				faceNormal.x = normalX
				faceNormal.y = normalY
				faceNormal.z = normalZ
			}
		}
		@Pc(1033) val local1033: Int
		@Pc(1076) val local1076: Short
		for (faceIndex in 0 until this.numFaces) {
			val face: Int = faces[faceIndex]
			@Pc(1040) val color: Int = mesh.facesColor!![face].toInt() and 0xFFFF
			var faceTexture: Int
			if (mesh.facesTexture == null) {
				faceTexture = -1
			} else {
				faceTexture = mesh.facesTexture!![face].toInt()
			}
			val faceAlpha: Int
			if (mesh.facesAlpha == null) {
				faceAlpha = 0
			} else {
				faceAlpha = mesh.facesAlpha!![face].toInt() and 0xFF
			}
			var materialId: Short = if (mesh.facesMaterial == null) -1 else mesh.facesMaterial!![face]
			if (materialId.toInt() != -1 && (this.preferenceFlags and 0x40) != 0) {
				@Pc(1095) val textureMetrics4: MaterialType? = textureSource!!.getMetrics(materialId.toInt() and 0xFFFF)
				if (textureMetrics4!!.detailsOnly) {
					materialId = -1
				}
			}
			@Pc(1102) var aU = 0.0F
			@Pc(1104) var aV = 0.0F
			@Pc(1106) var bU = 0.0F
			@Pc(1108) var bV = 0.0F
			@Pc(1110) var cU = 0.0F
			@Pc(1112) var cV = 0.0F
			@Pc(1114) var uidOffsetB: Byte = 0
			@Pc(1116) var uidOffsetC: Byte = 0
			@Pc(1118) var cubeFaceIndex = 0
			@Pc(1133) val local1133: Byte
			@Pc(1153) val local1153: Short
			@Pc(1952) val local1952: Short
			@Pc(1957) val local1957: Short
			if (materialId.toInt() != -1) {
				if (faceTexture == -1) {
					bU = 1.0F
					cV = 0.0F
					uidOffsetB = 1
					uidOffsetC = 2
					cU = 0.0F
					aU = 0.0F
					bV = 1.0F
					aV = 1.0F
				} else {
					faceTexture = faceTexture and 0xFF
					val mappingType: Byte = mesh.texturesMapping!![faceTexture]
					@Pc(1143) val vertexA: Int
					@Pc(1148) val vertexB: Int
					@Pc(1186) val local1186: Float
					@Pc(1200) val local1200: Float
					@Pc(1208) val local1208: Float
					@Pc(1308) val local1308: Float
					@Pc(1316) val local1316: Float
					@Pc(1324) val local1324: Float
					@Pc(1347) val local1347: Float
					@Pc(1370) val local1370: Float
					@Pc(1393) val local1393: Float
					if (mappingType.toInt() == 0) {
						vertexA = mesh.facesA!![face].toInt()
						vertexB = mesh.facesB!![face].toInt()
						val vertexC: Int = mesh.facesC!![face].toInt()
						val texVertexA: Int = mesh.textureFaceX!![faceTexture].toInt()
						val texVertexB: Int = mesh.textureFaceY!![faceTexture].toInt()
						@Pc(1962) val texVertexC: Int = mesh.textureFaceZ!![faceTexture].toInt()
						@Pc(1968) val texOriginX: Float = mesh.verticesX!![texVertexA].toFloat()
						@Pc(1974) val texOriginY: Float = mesh.verticesY!![texVertexA].toFloat()
						val texOriginZ: Float = mesh.verticesZ!![texVertexA].toFloat()
						val texEdge1X: Float = mesh.verticesX!![texVertexB].toFloat() - texOriginX
						val texEdge1Y: Float = mesh.verticesY!![texVertexB].toFloat() - texOriginY
						@Pc(2006) val texEdge1Z: Float = mesh.verticesZ!![texVertexB].toFloat() - texOriginZ
						@Pc(2015) val texEdge2X: Float = mesh.verticesX!![texVertexC].toFloat() - texOriginX
						@Pc(2023) val texEdge2Y: Float = mesh.verticesY!![texVertexC].toFloat() - texOriginY
						@Pc(2032) val texEdge2Z: Float = mesh.verticesZ!![texVertexC].toFloat() - texOriginZ
						@Pc(2041) val vertexAOffX: Float = mesh.verticesX!![vertexA].toFloat() - texOriginX
						@Pc(2049) val vertexAOffY: Float = mesh.verticesY!![vertexA].toFloat() - texOriginY
						@Pc(2058) val vertexAOffZ: Float = mesh.verticesZ!![vertexA].toFloat() - texOriginZ
						@Pc(2067) val vertexBOffX: Float = mesh.verticesX!![vertexB].toFloat() - texOriginX
						@Pc(2075) val vertexBOffY: Float = mesh.verticesY!![vertexB].toFloat() - texOriginY
						val vertexBOffZ: Float = mesh.verticesZ!![vertexB].toFloat() - texOriginZ
						val vertexCOffX: Float = mesh.verticesX!![vertexC].toFloat() - texOriginX
						val vertexCOffY: Float = mesh.verticesY!![vertexC].toFloat() - texOriginY
						val vertexCOffZ: Float = mesh.verticesZ!![vertexC].toFloat() - texOriginZ
						val texCrossX: Float = texEdge1Y * texEdge2Z - texEdge2Y * texEdge1Z
						val texCrossY: Float = texEdge2X * texEdge1Z - texEdge1X * texEdge2Z
						@Pc(2134) val texCrossZ: Float = texEdge2Y * texEdge1X - texEdge2X * texEdge1Y
						@Pc(2143) val uBasisX: Float = texCrossZ * texEdge2Y - texCrossY * texEdge2Z
						@Pc(2152) val uBasisY: Float = texCrossX * texEdge2Z - texCrossZ * texEdge2X
						@Pc(2161) val uBasisZ: Float = texCrossY * texEdge2X - texCrossX * texEdge2Y
						@Pc(2175) val uInvDeterminant: Float = 1.0F / (uBasisY * texEdge1Y + uBasisX * texEdge1X + texEdge1Z * uBasisZ)
						aU = uInvDeterminant * (uBasisZ * vertexAOffZ + uBasisX * vertexAOffX + uBasisY * vertexAOffY)
						cU = (uBasisZ * vertexCOffZ + uBasisX * vertexCOffX + vertexCOffY * uBasisY) * uInvDeterminant
						bU = uInvDeterminant * (vertexBOffZ * uBasisZ + uBasisY * vertexBOffY + uBasisX * vertexBOffX)
						@Pc(2225) val vBasisX: Float = texCrossZ * texEdge1Y - texCrossY * texEdge1Z
						@Pc(2234) val vBasisY: Float = texEdge1Z * texCrossX - texEdge1X * texCrossZ
						@Pc(2243) val vBasisZ: Float = texCrossY * texEdge1X - texCrossX * texEdge1Y
						@Pc(2257) val vInvDeterminant: Float = 1.0F / (vBasisX * texEdge2X + texEdge2Y * vBasisY + texEdge2Z * vBasisZ)
						bV = (vertexBOffX * vBasisX + vertexBOffY * vBasisY + vBasisZ * vertexBOffZ) * vInvDeterminant
						cV = (vBasisX * vertexCOffX + vBasisY * vertexCOffY + vBasisZ * vertexCOffZ) * vInvDeterminant
						aV = vInvDeterminant * (vertexAOffZ * vBasisZ + vertexAOffY * vBasisY + vertexAOffX * vBasisX)
					} else {
						vertexA = mesh.facesA!![face].toInt()
						vertexB = mesh.facesB!![face].toInt()
						val vertexC: Int = mesh.facesC!![face].toInt()
						@Pc(1158) val centerX: Int = textureMatrix.centerX!![faceTexture]
						@Pc(1163) val centerY: Int = textureMatrix.centerY!![faceTexture]
						@Pc(1168) val centerZ: Int = textureMatrix.centerZ!![faceTexture]
						@Pc(1173) val matrix: FloatArray? = textureMatrix.matrix!![faceTexture]
						@Pc(1178) val direction: Byte = mesh.texturesDirection!![faceTexture]
						val speedOffset: Float = mesh.texturesSpeed!![faceTexture].toFloat() / 256.0F
						if (mappingType.toInt() == 1) {
							val uScale: Float = mesh.texturesScaleZ!![faceTexture].toFloat() / 1024.0F
							Model.mapCylindricalUv(centerZ, mesh.verticesZ!![vertexA], direction.toInt(), mesh.verticesX!![vertexA], GpuModel.UV_RESULT, mesh.verticesY!![vertexA], speedOffset, centerY, centerX, uScale, matrix)
							aV = GpuModel.UV_RESULT[1]
							aU = GpuModel.UV_RESULT[0]
							Model.mapCylindricalUv(centerZ, mesh.verticesZ!![vertexB], direction.toInt(), mesh.verticesX!![vertexB], GpuModel.UV_RESULT, mesh.verticesY!![vertexB], speedOffset, centerY, centerX, uScale, matrix)
							bV = GpuModel.UV_RESULT[1]
							bU = GpuModel.UV_RESULT[0]
							Model.mapCylindricalUv(centerZ, mesh.verticesZ!![vertexC], direction.toInt(), mesh.verticesX!![vertexC], GpuModel.UV_RESULT, mesh.verticesY!![vertexC], speedOffset, centerY, centerX, uScale, matrix)
							cU = GpuModel.UV_RESULT[0]
							cV = GpuModel.UV_RESULT[1]
							val halfUScale: Float = uScale / 2.0F
							if ((direction.toInt() and 0x1) == 0) {
								if (halfUScale < bU - aU) {
									uidOffsetB = 1
									bU -= uScale
								} else if (halfUScale < aU - bU) {
									bU += uScale
									uidOffsetB = 2
								}
								if (halfUScale < cU - aU) {
									cU -= uScale
									uidOffsetC = 1
								} else if (aU - cU > halfUScale) {
									uidOffsetC = 2
									cU += uScale
								}
							} else {
								if (halfUScale < cV - aV) {
									cV -= uScale
									uidOffsetC = 1
								} else if (aV - cV > halfUScale) {
									uidOffsetC = 2
									cV += uScale
								}
								if (bV - aV > halfUScale) {
									bV -= uScale
									uidOffsetB = 1
								} else if (aV - bV > halfUScale) {
									bV += uScale
									uidOffsetB = 2
								}
							}
						} else if (mappingType.toInt() == 2) {
							val uTrans: Float = mesh.texturesUtrans!![faceTexture].toFloat() / 256.0F
							val vTrans: Float = mesh.texturesVtrans!![faceTexture].toFloat() / 256.0F
							@Pc(1219) val edge1X: Int = mesh.verticesX!![vertexB] - mesh.verticesX!![vertexA]
							@Pc(1230) val edge1Y: Int = mesh.verticesY!![vertexB] - mesh.verticesY!![vertexA]
							@Pc(1241) val edge1Z: Int = mesh.verticesZ!![vertexB] - mesh.verticesZ!![vertexA]
							@Pc(1252) val edge2X: Int = mesh.verticesX!![vertexC] - mesh.verticesX!![vertexA]
							@Pc(1262) val edge2Y: Int = mesh.verticesY!![vertexC] - mesh.verticesY!![vertexA]
							@Pc(1273) val edge2Z: Int = mesh.verticesZ!![vertexC] - mesh.verticesZ!![vertexA]
							@Pc(1282) val crossX: Int = edge1Y * edge2Z - edge1Z * edge2Y
							@Pc(1291) val crossY: Int = edge2X * edge1Z - edge1X * edge2Z
							@Pc(1300) val crossZ: Int = edge2Y * edge1X - edge1Y * edge2X
							val invScaleX: Float = 64.0F / mesh.texturesScaleX!![faceTexture].toFloat()
							val invScaleY: Float = 64.0F / mesh.texturesScaleY!![faceTexture].toFloat()
							val invScaleZ: Float = 64.0F / mesh.texturesScaleZ!![faceTexture].toFloat()
							val cubeZ: Float = (crossZ.toFloat() * matrix!![2] + crossX.toFloat() * matrix[0] + crossY.toFloat() * matrix[1]) / invScaleX
							val cubeX: Float = (matrix[5] * crossZ.toFloat() + matrix[3] * crossX.toFloat() + crossY.toFloat() * matrix[4]) / invScaleY
							val cubeY: Float = (crossY.toFloat() * matrix[7] + matrix[6] * crossX.toFloat() + matrix[8] * crossZ.toFloat()) / invScaleZ
							cubeFaceIndex = Model.getCubeFace(cubeX, cubeY, cubeZ)
							Model.mapCubeUv(vTrans, speedOffset, matrix, mesh.verticesZ!![vertexA], centerZ, direction.toInt(), centerX, mesh.verticesX!![vertexA], mesh.verticesY!![vertexA], uTrans, GpuModel.UV_RESULT, centerY, cubeFaceIndex)
							aV = GpuModel.UV_RESULT[1]
							aU = GpuModel.UV_RESULT[0]
							Model.mapCubeUv(vTrans, speedOffset, matrix, mesh.verticesZ!![vertexB], centerZ, direction.toInt(), centerX, mesh.verticesX!![vertexB], mesh.verticesY!![vertexB], uTrans, GpuModel.UV_RESULT, centerY, cubeFaceIndex)
							bV = GpuModel.UV_RESULT[1]
							bU = GpuModel.UV_RESULT[0]
							Model.mapCubeUv(vTrans, speedOffset, matrix, mesh.verticesZ!![vertexC], centerZ, direction.toInt(), centerX, mesh.verticesX!![vertexC], mesh.verticesY!![vertexC], uTrans, GpuModel.UV_RESULT, centerY, cubeFaceIndex)
							cU = GpuModel.UV_RESULT[0]
							cV = GpuModel.UV_RESULT[1]
						} else if (mappingType.toInt() == 3) {
							Model.mapSphericalUv(centerZ, direction.toInt(), speedOffset, mesh.verticesX!![vertexA], GpuModel.UV_RESULT, mesh.verticesZ!![vertexA], centerX, mesh.verticesY!![vertexA], centerY, matrix)
							aU = GpuModel.UV_RESULT[0]
							aV = GpuModel.UV_RESULT[1]
							Model.mapSphericalUv(centerZ, direction.toInt(), speedOffset, mesh.verticesX!![vertexB], GpuModel.UV_RESULT, mesh.verticesZ!![vertexB], centerX, mesh.verticesY!![vertexB], centerY, matrix)
							bU = GpuModel.UV_RESULT[0]
							bV = GpuModel.UV_RESULT[1]
							Model.mapSphericalUv(centerZ, direction.toInt(), speedOffset, mesh.verticesX!![vertexC], GpuModel.UV_RESULT, mesh.verticesZ!![vertexC], centerX, mesh.verticesY!![vertexC], centerY, matrix)
							cU = GpuModel.UV_RESULT[0]
							cV = GpuModel.UV_RESULT[1]
							if ((direction.toInt() and 0x1) == 0) {
								if (cU - aU > 0.5F) {
									cU--
									uidOffsetC = 1
								} else if (aU - cU > 0.5F) {
									cU++
									uidOffsetC = 2
								}
								if (bU - aU > 0.5F) {
									bU--
									uidOffsetB = 1
								} else if (aU - bU > 0.5F) {
									bU++
									uidOffsetB = 2
								}
							} else {
								if (cV - aV > 0.5F) {
									uidOffsetC = 1
									cV--
								} else if (aV - cV > 0.5F) {
									uidOffsetC = 2
									cV++
								}
								if (bV - aV > 0.5F) {
									bV--
									uidOffsetB = 1
								} else if (aV - bV > 0.5F) {
									uidOffsetB = 2
									bV++
								}
							}
						}
					}
				}
			}
			val faceRenderType: Byte
			if (mesh.faceRenderType == null) {
				faceRenderType = 0
			} else {
				faceRenderType = mesh.faceRenderType!![face]
			}
			if (faceRenderType.toInt() == 0) {
				@Pc(2481) val uid: Long = (faceTexture shl 2).toLong() + (((color shl 8).toLong() + (cubeFaceIndex shl 24).toLong() + faceAlpha.toLong()) shl 32)
				val vertexA: Int = mesh.facesA!![face].toInt()
				val vertexB: Int = mesh.facesB!![face].toInt()
				val vertexC: Int = mesh.facesC!![face].toInt()
				@Pc(2500) val vertexNormalA: GpuVertexNormal = vertexNormals[vertexA]!!
				this.facesA!![faceIndex] = this.connectNormal(vertexNormalA.z, uid, faceIndex, vertexNormalA.magnitude, vertexNormalA.x, aU, vertexA, aV, mesh, vertexNormalA.y)
				@Pc(2525) val vertexNormalB: GpuVertexNormal = vertexNormals[vertexB]!!
				this.facesB!![faceIndex] = this.connectNormal(vertexNormalB.z, uidOffsetB.toLong() + uid, faceIndex, vertexNormalB.magnitude, vertexNormalB.x, bU, vertexB, bV, mesh, vertexNormalB.y)
				@Pc(2553) val vertexNormalC: GpuVertexNormal = vertexNormals[vertexC]!!
				this.facesC!![faceIndex] = this.connectNormal(vertexNormalC.z, uidOffsetC.toLong() + uid, faceIndex, vertexNormalC.magnitude, vertexNormalC.x, cU, vertexC, cV, mesh, vertexNormalC.y)
			} else if (faceRenderType.toInt() == 1) {
				@Pc(2338) val faceNormal: GpuFaceNormal = faceNormals[face]!!
				@Pc(2383) val uid: Long = ((faceAlpha.toLong() + (cubeFaceIndex shl 24).toLong() + (color shl 8).toLong()) shl 32) + (((faceNormal.y + 256) shl 12) + (if (faceNormal.x > 0) 1024 else 2048) + (faceTexture shl 2) + ((faceNormal.z - -256) shl 22)).toLong()
				this.facesA!![faceIndex] = this.connectNormal(faceNormal.z, uid, faceIndex, 0, faceNormal.x, aU, mesh.facesA!![face].toInt(), aV, mesh, faceNormal.y)
				this.facesB!![faceIndex] = this.connectNormal(faceNormal.z, uid + uidOffsetB.toLong(), faceIndex, 0, faceNormal.x, bU, mesh.facesB!![face].toInt(), bV, mesh, faceNormal.y)
				this.facesC!![faceIndex] = this.connectNormal(faceNormal.z, uid + uidOffsetC.toLong(), faceIndex, 0, faceNormal.x, cU, mesh.facesC!![face].toInt(), cV, mesh, faceNormal.y)
			}
			if (mesh.facesAlpha != null) {
				this.facesAlpha!![faceIndex] = mesh.facesAlpha!![face]
			}
			if (mesh.facesSubmeshes != null) {
				this.facesSubmeshes!![faceIndex] = mesh.facesSubmeshes!![face]
			}
			this.facesColor!![faceIndex] = mesh.facesColor!![face]
			this.facesMaterial!![faceIndex] = materialId
		}
		if (this.renderedFaceCount > 0) {
			var batchCount = 1
			var batchMaterial: Short = this.facesMaterial!![0]
			for (faceIndex in 0 until this.renderedFaceCount) {
				@Pc(2640) val material: Short = this.facesMaterial!![faceIndex]
				if (batchMaterial != material) {
					batchCount++
					batchMaterial = material
				}
			}
			this.materialBatchMinIndex = IntArray(batchCount)
			this.materialBatchOffsets = IntArray(batchCount + 1)
			this.materialBatchVertexCount = IntArray(batchCount)
			this.materialBatchOffsets!![0] = 0
			var minIndex: Int = this.numNormals
			batchCount = 0
			var maxIndex: Short = 0
			batchMaterial = this.facesMaterial!![0]
			for (faceIndex in 0 until this.renderedFaceCount) {
				@Pc(2699) val material: Short = this.facesMaterial!![faceIndex]
				if (material != batchMaterial) {
					this.materialBatchMinIndex!![batchCount] = minIndex
					this.materialBatchVertexCount!![batchCount] = maxIndex + 1 - minIndex
					batchCount++
					this.materialBatchOffsets!![batchCount] = faceIndex
					batchMaterial = material
					maxIndex = 0
					minIndex = this.numNormals
				}
				@Pc(2735) var faceVertex: Short = this.facesA!![faceIndex]
				if (maxIndex < faceVertex) {
					maxIndex = faceVertex
				}
				if (minIndex > faceVertex) {
					minIndex = faceVertex.toInt()
				}
				faceVertex = this.facesB!![faceIndex]
				if (faceVertex > maxIndex) {
					maxIndex = faceVertex
				}
				if (faceVertex < minIndex) {
					minIndex = faceVertex.toInt()
				}
				faceVertex = this.facesC!![faceIndex]
				if (faceVertex > maxIndex) {
					maxIndex = faceVertex
				}
				if (faceVertex < minIndex) {
					minIndex = faceVertex.toInt()
				}
			}
			this.materialBatchMinIndex!![batchCount] = minIndex
			this.materialBatchVertexCount!![batchCount] = maxIndex + 1 - minIndex
			batchCount++
			this.materialBatchOffsets!![batchCount] = this.renderedFaceCount
		}
		GpuModel.normalsUid = null
		this.normalVertices = ArrayUtils.copyOf(this.normalVertices, this.numNormals)
		this.normalFaces = ArrayUtils.copyOf(this.normalFaces, this.numNormals)
		this.normalsX = ArrayUtils.copyOf(this.normalsX, this.numNormals)
		this.normalsY = ArrayUtils.copyOf(this.normalsY, this.numNormals)
		this.normalsZ = ArrayUtils.copyOf(this.normalsZ, this.numNormals)
		this.magnitudes = ArrayUtils.copyOf(this.magnitudes, this.numNormals)
		this.textureU = ArrayUtils.copyOf(this.numNormals, this.textureU)
		this.textureV = ArrayUtils.copyOf(this.numNormals, this.textureV)
		if (mesh.vertexGroups != null && GpuModelRetainMasks.altersVertices(this.preferenceFlags, functionMask)) {
			this.vertexGroups = mesh.createVertexGroups(false)
		}
		if (mesh.billboards != null && GpuModelRetainMasks.retainBillboards(this.preferenceFlags, functionMask)) {
			this.billboardGroups = mesh.createBillboardGroups()
		}
		if (mesh.faceGroups != null && GpuModelRetainMasks.altersFaces(this.preferenceFlags, functionMask)) {
			var maxFaceGroup = 0
			@Pc(2927) val groupCounts = IntArray(256)
			for (faceIndex in 0 until this.numFaces) {
				val faceGroup: Int = mesh.faceGroups!![faces[faceIndex]]
				if (faceGroup >= 0) {
					@Pc(2947) val previousCount: Int = groupCounts[faceGroup]++
					if (maxFaceGroup < faceGroup) {
						maxFaceGroup = faceGroup
					}
				}
			}
			this.faceGroups = arrayOfNulls<IntArray>(maxFaceGroup + 1)
			for (group in 0..maxFaceGroup) {
				this.faceGroups!![group] = IntArray(groupCounts[group])
				groupCounts[group] = 0
			}
			for (faceIndex in 0 until this.numFaces) {
				val faceGroup: Int = mesh.faceGroups!![faces[faceIndex]]
				if (faceGroup >= 0) {
					this.faceGroups!![faceGroup]!![groupCounts[faceGroup]++] = faceIndex
				}
			}
		}
	}

	@OriginalMember(owner = "client!nca", name = "<init>", descriptor = "(Lclient!wga;IIZZ)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit, @OriginalArg(1) functionMask: Int, @OriginalArg(2) preferenceFlags: Int, @OriginalArg(3) createAllStreams: Boolean, @OriginalArg(4) dynamic: Boolean) : super() {
		this.updateRequired = true
		this.maxDepth = 0
		this.hasAlphaTransition = false
		this.boundsCalculated = false
		this.numFaces = 0
		this.numNormals = 0
		this.renderedFaceCount = 0
		this.textureAnimating = false
		this.numVertices = 0
		this.dynamic = false
		this.functionMask = functionMask
		this.dynamic = dynamic
		this.preferenceFlags = preferenceFlags
		this.nativeToolkit = nativeToolkit
		if (createAllStreams || GpuModelRetainMasks.hasVertices(this.functionMask, this.preferenceFlags)) {
			this.positionStream = GpuModelVertexStream(GpuModelRetainMasks.altersAnyVertices(this.functionMask, this.preferenceFlags))
		}
		if (createAllStreams || GpuModelRetainMasks.hasTextureCoordinates(this.preferenceFlags, this.functionMask)) {
			this.texCoordStream = GpuModelVertexStream(GpuModelRetainMasks.requiresTexCoordBufferRebuild(this.preferenceFlags, this.functionMask))
		}
		if (createAllStreams || GpuModelRetainMasks.hasColors(this.functionMask, this.preferenceFlags)) {
			this.colourStream = GpuModelVertexStream(GpuModelRetainMasks.requiresColorBufferRebuild(this.functionMask, this.preferenceFlags))
		}
		if (createAllStreams || GpuModelRetainMasks.hasNormals(this.preferenceFlags, this.functionMask)) {
			this.normalStream = GpuModelVertexStream(GpuModelRetainMasks.requiresNormalsBufferRebuild(this.functionMask, this.preferenceFlags))
		}
		if (createAllStreams || GpuModelRetainMasks.hasElementsBuffer(this.functionMask, this.preferenceFlags)) {
			this.indexStream = GpuModelIndexStream(GpuModelRetainMasks.copyElementsBuffer(this.preferenceFlags, this.functionMask))
		}
	}

	@OriginalMember(owner = "client!nca", name = "b", descriptor = "(I)V")
	private fun renderBillboards() {
		if (this.billboardFaces == null) {
			return
		}
		this.nativeToolkit.C(!this.hasAlphaTransition)
		this.nativeToolkit.toggleLights(false)
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.CONSTANT_COLOR)
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.CONSTANT_COLOR)
		for (@Pc(32) i in 0 until this.billboardCount) {
			@Pc(39) val nativeBillboardFace: NativeBillboardFace = this.billboardFaces!![i]!!
			@Pc(44) val nativeBillboardAttributes: NativeBillboardAttributes = this.billboardAttributes!![i]!!
			if (!nativeBillboardFace.hideWithBloom || !this.nativeToolkit.bloom()) {
				@Pc(73) val centerX: Float = (this.verticesX!![nativeBillboardFace.vertexC] + this.verticesX!![nativeBillboardFace.vertexB] + this.verticesX!![nativeBillboardFace.vertexA]).toFloat() * 0.3333333F
				@Pc(94) val centerY: Float = (this.verticesY!![nativeBillboardFace.vertexB] + this.verticesY!![nativeBillboardFace.vertexC] + this.verticesY!![nativeBillboardFace.vertexA]).toFloat() * 0.3333333F
				@Pc(115) val centerZ: Float = (this.verticesZ!![nativeBillboardFace.vertexA] + this.verticesZ!![nativeBillboardFace.vertexB] + this.verticesZ!![nativeBillboardFace.vertexC]).toFloat() * 0.3333333F
				@Pc(129) val viewX: Float = centerZ * GpuModel.modelViewXC + GpuModel.modelViewXA * centerX + centerY * GpuModel.modelViewXB + GpuModel.modelViewXD
				@Pc(143) val viewY: Float = GpuModel.modelViewYD + GpuModel.modelViewYC * centerZ + GpuModel.modelViewYA * centerX + centerY * GpuModel.modelViewYB
				@Pc(157) val viewZ: Float = GpuModel.modelViewZD + GpuModel.modelViewZB * centerY + centerX * GpuModel.modelViewZA + centerZ * GpuModel.modelViewZC
				@Pc(178) val scale: Float = (1.0 / kotlin.math.sqrt((viewZ * viewZ + viewY * viewY + viewX * viewX).toDouble())).toFloat() * nativeBillboardFace.distance.toFloat()
				@Pc(183) val modelMatrix: NativeMatrix = this.nativeToolkit.getModelMatrix()
				modelMatrix.setTextureCoordTransform(nativeBillboardAttributes.angle, (nativeBillboardFace.width * nativeBillboardAttributes.scaleY) shr 7, nativeBillboardAttributes.offsetX.toFloat() + viewX - viewX * scale, -(scale * viewZ) + viewZ, nativeBillboardAttributes.offsetY.toFloat() + viewY - viewY * scale, (nativeBillboardAttributes.scaleX * nativeBillboardFace.height) shr 7)
				modelMatrix.multiply(this.nativeToolkit.inverseSunMatrix43)
				this.nativeToolkit.commitModelMatrix()
				@Pc(240) val colour: Int = nativeBillboardAttributes.colour
				this.nativeToolkit.setActiveMaterial(false, false, nativeBillboardFace.texture.toInt())
				this.nativeToolkit.toggleBlending(nativeBillboardFace.blendMode.toInt())
				this.nativeToolkit.setColourValue(colour)
				this.nativeToolkit.drawQuadTriangleFan()
			}
		}
		this.nativeToolkit.setTextureAlphaStageState(1, TextureCombineSource.PREVIOUS)
		this.nativeToolkit.setTextureRgbStageState(1, TextureCombineSource.PREVIOUS)
		this.nativeToolkit.C(true)
	}

	@OriginalMember(owner = "client!nca", name = "c", descriptor = "(I)Z")
	private fun uploadStreams(): Boolean {
		@Pc(13) val uploadColours: Boolean = !this.colourStream!!.uploaded
		@Pc(29) val uploadNormals: Boolean = (this.preferenceFlags and 0x37) != 0 && !this.normalStream!!.uploaded
		@Pc(37) val uploadPositions: Boolean = !this.positionStream!!.uploaded
		@Pc(45) val uploadTexCoords: Boolean = !this.texCoordStream!!.uploaded
		if (!uploadPositions && !uploadColours && !uploadNormals && !uploadTexCoords) {
			return true
		}
		@Pc(57) var success = true
		@Pc(76) var vertexBuffer: NativeStream?
		@Pc(90) var buffer: Buffer?
		if (uploadPositions) {
			if (this.positionStream!!.buffer == null) {
				this.positionStream!!.buffer = this.nativeToolkit.createStream(this.dynamic)
			}
			vertexBuffer = this.positionStream!!.buffer
			vertexBuffer!!.create(this.numNormals * 12, 12)
			buffer = vertexBuffer.lock()
			if (buffer == null) {
				success = false
			} else {
				this.nativeToolkit.nativeInterface.copyPositions(this.verticesX, this.verticesY, this.verticesZ, this.normalVertices, 0, 12, this.numNormals, buffer.getAddress())
				if (vertexBuffer.unlock()) {
					this.positionStream!!.activeBuffer = vertexBuffer
					this.positionStream!!.uploaded = true
				} else {
					success = false
				}
			}
		}
		@Pc(186) var srcNormalsX: ShortArray?
		@Pc(189) var srcNormalsY: ShortArray?
		@Pc(183) var srcNormalsZ: ShortArray?
		@Pc(180) var srcMagnitudes: ByteArray?
		if (uploadColours) {
			if (this.colourStream!!.buffer == null) {
				this.colourStream!!.buffer = this.nativeToolkit.createStream(this.dynamic)
			}
			vertexBuffer = this.colourStream!!.buffer
			vertexBuffer!!.create(this.numNormals * 4, 4)
			buffer = vertexBuffer.lock()
			if (buffer == null) {
				success = false
			} else {
				if ((this.preferenceFlags and 0x37) == 0) {
					if (this.mergedNormals == null) {
						srcMagnitudes = this.magnitudes
						srcNormalsZ = this.normalsZ
						srcNormalsX = this.normalsX
						srcNormalsY = this.normalsY
					} else {
						srcNormalsZ = this.mergedNormals!!.normalsZ
						srcNormalsY = this.mergedNormals!!.normalsY
						srcMagnitudes = this.mergedNormals!!.magnitudes
						srcNormalsX = this.mergedNormals!!.normalsX
					}
					this.nativeToolkit.nativeInterface.copyLighting(this.facesColor, this.facesAlpha, this.facesMaterial, srcNormalsX, srcNormalsY, srcNormalsZ, srcMagnitudes, this.ambient.toInt(), this.contrast.toInt(), this.normalFaces, 0, 4, this.numNormals, buffer.getAddress())
				} else {
					this.nativeToolkit.nativeInterface.copyColours(this.facesColor, this.facesAlpha, this.facesMaterial, this.ambient.toInt(), this.normalFaces, 0, 4, this.numNormals, buffer.getAddress())
				}
				if (vertexBuffer.unlock()) {
					this.colourStream!!.uploaded = true
					this.colourStream!!.activeBuffer = vertexBuffer
				} else {
					success = false
				}
			}
		}
		if (uploadNormals) {
			if (this.normalStream!!.buffer == null) {
				this.normalStream!!.buffer = this.nativeToolkit.createStream(this.dynamic)
			}
			vertexBuffer = this.normalStream!!.buffer
			vertexBuffer!!.create(this.numNormals * 12, 12)
			buffer = vertexBuffer.lock()
			if (buffer == null) {
				success = false
			} else {
				if (this.mergedNormals == null) {
					srcNormalsZ = this.normalsZ
					srcNormalsX = this.normalsX
					srcMagnitudes = this.magnitudes
					srcNormalsY = this.normalsY
				} else {
					srcMagnitudes = this.mergedNormals!!.magnitudes
					srcNormalsZ = this.mergedNormals!!.normalsZ
					srcNormalsX = this.mergedNormals!!.normalsX
					srcNormalsY = this.mergedNormals!!.normalsY
				}
				this.nativeToolkit.nativeInterface.copyNormals(srcNormalsX, srcNormalsY, srcNormalsZ, srcMagnitudes, 3.0F / this.contrast.toFloat(), 3.0F / (this.contrast + this.contrast / 2).toFloat(), 0, 12, this.numNormals, buffer.getAddress())
				if (vertexBuffer.unlock()) {
					this.normalStream!!.uploaded = true
					this.normalStream!!.activeBuffer = vertexBuffer
				} else {
					success = false
				}
			}
		}
		if (uploadTexCoords) {
			if (this.texCoordStream!!.buffer == null) {
				this.texCoordStream!!.buffer = this.nativeToolkit.createStream(this.dynamic)
			}
			vertexBuffer = this.texCoordStream!!.buffer
			vertexBuffer!!.create(this.numNormals * 8, 8)
			buffer = vertexBuffer.lock()
			if (buffer == null) {
				success = false
			} else {
				this.nativeToolkit.nativeInterface.copyTexCoords(this.textureU, this.textureV, 0, 8, this.numNormals, buffer.getAddress())
				if (vertexBuffer.unlock()) {
					this.texCoordStream!!.activeBuffer = vertexBuffer
					this.texCoordStream!!.uploaded = true
				} else {
					success = false
				}
			}
		}
		return success
	}

	@OriginalMember(owner = "client!nca", name = "na", descriptor = "()I")
	override fun na(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.size2d
	}

	@OriginalMember(owner = "client!nca", name = "NA", descriptor = "()Z")
	override fun NA(): Boolean {
		if (this.vertexGroups == null) {
			return false
		}
		for (@Pc(12) i in 0 until this.numVertices) {
			this.verticesX!![i] = this.verticesX!![i] shl 0x4
			this.verticesY!![i] = this.verticesY!![i] shl 0x4
			this.verticesZ!![i] = this.verticesZ!![i] shl 0x4
		}
		GpuModel.originX = 0
		GpuModel.originZ = 0
		GpuModel.originY = 0
		return true
	}

	@OriginalMember(owner = "client!nca", name = "f", descriptor = "()V")
	override fun lock() {
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "()[Lclient!eca;")
	override fun getEffectiveVertices(): Array<EffectiveVertex?>? {
		return this.effectiveVertices
	}

	@OriginalMember(owner = "client!nca", name = "LA", descriptor = "(I)V")
	override fun LA(@OriginalArg(0) contrast: Int) {
		this.contrast = contrast.toShort()
		this.invalidatePositions()
		this.invalidateLighting()
	}

	@OriginalMember(owner = "client!nca", name = "VA", descriptor = "(I)V")
	override fun VA(@OriginalArg(0) angle: Int) {
		@Pc(9) val sin: Int = TextureCombineSource.SINE[angle]
		@Pc(13) val cos: Int = TextureCombineSource.COSINE[angle]
		for (@Pc(15) i in 0 until this.maxDepth) {
			@Pc(33) val rotatedX: Int = (this.verticesX!![i] * cos + sin * this.verticesY!![i]) shr 14
			this.verticesY!![i] = (cos * this.verticesY!![i] - sin * this.verticesX!![i]) shr 14
			this.verticesX!![i] = rotatedX
		}
		this.invalidatePositions()
		this.boundsCalculated = false
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(IILclient!bca;ZI)Z")
	override fun picked(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) matrix: Matrix?, @OriginalArg(3) quick: Boolean, @OriginalArg(4) sizeShift: Int): Boolean {
		return this.pick(x, sizeShift, -1, y, quick, matrix)
	}

	@OriginalMember(owner = "client!nca", name = "ba", descriptor = "(Lclient!r;)Lclient!r;")
	override fun ba(@OriginalArg(0) shadow: Shadow?): Shadow? {
		if (this.numNormals == 0) {
			return null
		}
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		@Pc(43) val shadowMinX: Int
		@Pc(59) val shadowMaxX: Int
		if (this.nativeToolkit.shadowOffsetX <= 0) {
			shadowMinX = (this.minX - ((this.minY * this.nativeToolkit.shadowOffsetX) shr 8)) shr this.nativeToolkit.shadowSizeShift
			shadowMaxX = (this.maxX - ((this.nativeToolkit.shadowOffsetX * this.maxY) shr 8)) shr this.nativeToolkit.shadowSizeShift
		} else {
			shadowMinX = (this.minX - ((this.maxY * this.nativeToolkit.shadowOffsetX) shr 8)) shr this.nativeToolkit.shadowSizeShift
			shadowMaxX = (this.maxX - ((this.minY * this.nativeToolkit.shadowOffsetX) shr 8)) shr this.nativeToolkit.shadowSizeShift
		}
		@Pc(114) val shadowMinZ: Int
		@Pc(131) val shadowMaxZ: Int
		if (this.nativeToolkit.shadowOffsetZ > 0) {
			shadowMinZ = (this.minZ - ((this.maxY * this.nativeToolkit.shadowOffsetZ) shr 8)) shr this.nativeToolkit.shadowSizeShift
			shadowMaxZ = (this.maxZ - ((this.minY * this.nativeToolkit.shadowOffsetZ) shr 8)) shr this.nativeToolkit.shadowSizeShift
		} else {
			shadowMinZ = (this.minZ - ((this.minY * this.nativeToolkit.shadowOffsetZ) shr 8)) shr this.nativeToolkit.shadowSizeShift
			shadowMaxZ = (this.maxZ - ((this.maxY * this.nativeToolkit.shadowOffsetZ) shr 8)) shr this.nativeToolkit.shadowSizeShift
		}
		@Pc(172) val width: Int = shadowMaxX + 1 - shadowMinX
		@Pc(179) val height: Int = shadowMaxZ + 1 - shadowMinZ
		@Pc(182) val nativeShadow: GpuShadow? = shadow as GpuShadow?
		@Pc(198) val result: GpuShadow
		if (nativeShadow != null && nativeShadow.fitsDimension(width, height)) {
			result = nativeShadow
			nativeShadow.reset()
		} else {
			result = GpuShadow(this.nativeToolkit, width, height)
		}
		result.setDimension(shadowMaxX, shadowMaxZ, shadowMinZ, shadowMinX)
		this.projectShadow(result)
		return result
	}

	@OriginalMember(owner = "client!nca", name = "wa", descriptor = "()V")
	override fun wa() {
		for (@Pc(7) i in 0 until this.numVertices) {
			this.verticesX!![i] = (this.verticesX!![i] + 7) shr 4
			this.verticesY!![i] = (this.verticesY!![i] + 7) shr 4
			this.verticesZ!![i] = (this.verticesZ!![i] + 7) shr 4
		}
		this.invalidatePositions()
		this.boundsCalculated = false
	}

	@OriginalMember(owner = "client!nca", name = "EA", descriptor = "()I")
	override fun EA(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.maxY
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(IIIIIZLclient!bca;)Z")
	private fun pick(@OriginalArg(0) x: Int, @OriginalArg(1) sizeShift: Int, @OriginalArg(2) zoom: Int, @OriginalArg(4) y: Int, @OriginalArg(5) quick: Boolean, @OriginalArg(6) matrix: Matrix?): Boolean {
		@Pc(8) val modelMatrix: NativeMatrix = matrix as NativeMatrix
		@Pc(12) val viewMatrix43: NativeMatrix? = this.nativeToolkit.viewMatrix43
		@Pc(33) val translateX: Float = viewMatrix43!!.xD + viewMatrix43!!.xA * modelMatrix.xD + viewMatrix43!!.xB * modelMatrix.yD + viewMatrix43!!.xC * modelMatrix.zD
		@Pc(54) val translateY: Float = viewMatrix43!!.yC * modelMatrix.zD + modelMatrix.yD * viewMatrix43!!.yB + modelMatrix.xD * viewMatrix43!!.yA + viewMatrix43!!.yD
		GpuModel.modelViewXC = viewMatrix43!!.xC * modelMatrix.zC + modelMatrix.xC * viewMatrix43!!.xA + modelMatrix.yC * viewMatrix43!!.xB
		GpuModel.modelViewZA = modelMatrix.zA * viewMatrix43!!.zC + viewMatrix43!!.zB * modelMatrix.yA + modelMatrix.xA * viewMatrix43!!.zA
		GpuModel.modelViewYB = modelMatrix.yB * viewMatrix43!!.yB + viewMatrix43!!.yA * modelMatrix.xB + viewMatrix43!!.yC * modelMatrix.zB
		GpuModel.modelViewZC = modelMatrix.xC * viewMatrix43!!.zA + viewMatrix43!!.zB * modelMatrix.yC + modelMatrix.zC * viewMatrix43!!.zC
		GpuModel.modelViewXB = modelMatrix.zB * viewMatrix43!!.xC + viewMatrix43!!.xA * modelMatrix.xB + viewMatrix43!!.xB * modelMatrix.yB
		GpuModel.modelViewYA = viewMatrix43!!.yC * modelMatrix.zA + modelMatrix.xA * viewMatrix43!!.yA + modelMatrix.yA * viewMatrix43!!.yB
		GpuModel.modelViewXA = viewMatrix43!!.xC * modelMatrix.zA + viewMatrix43!!.xB * modelMatrix.yA + modelMatrix.xA * viewMatrix43!!.xA
		@Pc(201) val translateZ: Float = viewMatrix43!!.zD + viewMatrix43!!.zC * modelMatrix.zD + viewMatrix43!!.zA * modelMatrix.xD + modelMatrix.yD * viewMatrix43!!.zB
		GpuModel.modelViewYC = viewMatrix43!!.yB * modelMatrix.yC + modelMatrix.xC * viewMatrix43!!.yA + modelMatrix.zC * viewMatrix43!!.yC
		GpuModel.modelViewZB = viewMatrix43!!.zB * modelMatrix.yB + modelMatrix.xB * viewMatrix43!!.zA + modelMatrix.zB * viewMatrix43!!.zC
		@Pc(239) var anyProjected = false
		@Pc(241) var minScreenX: Float = Float.MAX_VALUE
		@Pc(243) var maxScreenX: Float = -3.4028235E38F
		@Pc(245) var minScreenY: Float = Float.MAX_VALUE
		@Pc(247) var maxScreenY: Float = -3.4028235E38F
		@Pc(251) val scaleX: Int = this.nativeToolkit.projectionScaleX
		@Pc(255) val scaleY: Int = this.nativeToolkit.projectionScaleY
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		@Pc(270) val extentX: Int = (this.maxX - this.minX) shr 1
		@Pc(279) val extentY: Int = (this.maxY - this.minY) shr 1
		@Pc(296) val extentZ: Int = (this.maxZ - this.minZ) shr 1
		@Pc(301) val centerX: Int = extentX + this.minX
		@Pc(306) val centerY: Int = extentY + this.minY
		@Pc(311) val centerZ: Int = extentZ + this.minZ
		@Pc(317) val boxMinX: Int = centerX - (extentX shl sizeShift)
		@Pc(323) val boxMinY: Int = centerY - (extentY shl sizeShift)
		@Pc(330) val boxMinZ: Int = centerZ - (extentZ shl sizeShift)
		@Pc(336) val boxMaxX: Int = (extentX shl sizeShift) + centerX
		@Pc(342) val boxMaxY: Int = centerY + (extentY shl sizeShift)
		GpuModel.BOX_CORNERS_X[0] = boxMinX
		@Pc(352) val boxMaxZ: Int = (extentZ shl sizeShift) + centerZ
		GpuModel.BOX_CORNERS_Y[0] = boxMinY
		GpuModel.BOX_CORNERS_X[1] = boxMaxX
		GpuModel.BOX_CORNERS_Z[0] = boxMinZ
		GpuModel.BOX_CORNERS_Y[1] = boxMinY
		GpuModel.BOX_CORNERS_Z[1] = boxMinZ
		GpuModel.BOX_CORNERS_X[2] = boxMinX
		GpuModel.BOX_CORNERS_Y[2] = boxMaxY
		GpuModel.BOX_CORNERS_Z[2] = boxMinZ
		GpuModel.BOX_CORNERS_X[3] = boxMaxX
		GpuModel.BOX_CORNERS_Y[3] = boxMaxY
		GpuModel.BOX_CORNERS_Z[3] = boxMinZ
		GpuModel.BOX_CORNERS_X[4] = boxMinX
		GpuModel.BOX_CORNERS_Y[4] = boxMinY
		GpuModel.BOX_CORNERS_Z[4] = boxMaxZ
		GpuModel.BOX_CORNERS_X[5] = boxMaxX
		GpuModel.BOX_CORNERS_Y[5] = boxMinY
		GpuModel.BOX_CORNERS_Z[5] = boxMaxZ
		GpuModel.BOX_CORNERS_X[6] = boxMinX
		GpuModel.BOX_CORNERS_Y[6] = boxMaxY
		GpuModel.BOX_CORNERS_X[7] = boxMaxX
		GpuModel.BOX_CORNERS_Z[6] = boxMaxZ
		GpuModel.BOX_CORNERS_Y[7] = boxMaxY
		GpuModel.BOX_CORNERS_Z[7] = boxMaxZ
		@Pc(491) var viewX: Float
		@Pc(477) var viewY: Float
		@Pc(505) var viewZ: Float
		@Pc(453) var pointX: Float
		@Pc(463) var pointY: Float
		@Pc(458) var pointZ: Float
		for (@Pc(446) i in 0 until 8) {
			pointX = GpuModel.BOX_CORNERS_X[i].toFloat()
			pointZ = GpuModel.BOX_CORNERS_Z[i].toFloat()
			pointY = GpuModel.BOX_CORNERS_Y[i].toFloat()
			viewY = pointX * GpuModel.modelViewYA + pointY * GpuModel.modelViewYB + GpuModel.modelViewYC * pointZ + translateY
			viewX = GpuModel.modelViewXA * pointX + pointY * GpuModel.modelViewXB + pointZ * GpuModel.modelViewXC + translateX
			viewZ = translateZ + pointZ * GpuModel.modelViewZC + GpuModel.modelViewZB * pointY + GpuModel.modelViewZA * pointX
			if (viewZ >= this.nativeToolkit.camNear.toFloat()) {
				if (zoom > 0) {
					viewZ = zoom.toFloat()
				}
				@Pc(532) val screenX: Float = viewX * scaleX.toFloat() / viewZ + this.nativeToolkit.projectionCenterX.toFloat()
				@Pc(544) val screenY: Float = this.nativeToolkit.projectionCenterY.toFloat() + scaleY.toFloat() * viewY / viewZ
				if (screenX < minScreenX) {
					minScreenX = screenX
				}
				if (screenX > maxScreenX) {
					maxScreenX = screenX
				}
				if (screenY < minScreenY) {
					minScreenY = screenY
				}
				anyProjected = true
				if (maxScreenY < screenY) {
					maxScreenY = screenY
				}
			}
		}
		if (anyProjected && minScreenX < x.toFloat() && maxScreenX > x.toFloat() && minScreenY < y.toFloat() && maxScreenY > y.toFloat()) {
			if (quick) {
				return true
			}
			if (GpuModel.projectedVerticesX.size < this.numNormals) {
				GpuModel.projectedVerticesY = IntArray(this.numNormals)
				GpuModel.projectedVerticesX = IntArray(this.numNormals)
			}
			@Pc(708) val local708: Int
			for (@Pc(621) j in 0 until this.maxDepth) {
				pointZ = this.verticesZ!![j].toFloat()
				pointY = this.verticesY!![j].toFloat()
				pointX = this.verticesX!![j].toFloat()
				viewZ = GpuModel.modelViewZC * pointZ + pointX * GpuModel.modelViewZA + GpuModel.modelViewZB * pointY + translateZ
				viewX = translateX + GpuModel.modelViewXB * pointY + GpuModel.modelViewXA * pointX + pointZ * GpuModel.modelViewXC
				viewY = GpuModel.modelViewYC * pointZ + pointX * GpuModel.modelViewYA + GpuModel.modelViewYB * pointY + translateY
				@Pc(721) val local721: Int
				@Pc(726) val local726: Int
				@Pc(733) val local733: Int
				if (viewZ >= this.nativeToolkit.camNear.toFloat()) {
					if (zoom > 0) {
						viewZ = zoom.toFloat()
					}
					val projectedX: Int = (scaleX.toFloat() * viewX / viewZ + this.nativeToolkit.projectionCenterX.toFloat()).toInt()
					val projectedY: Int = (this.nativeToolkit.projectionCenterY.toFloat() + viewY * scaleY.toFloat() / viewZ).toInt()
					val normalStart: Int = this.triangles!![j]
					val normalEnd: Int = this.triangles!![j + 1]
					for (@Pc(735) k in normalStart until normalEnd) {
						@Pc(744) val normalIndex: Int = this.vertexNormalIndices!![k] - 1
						if (normalIndex == -1) {
							break
						}
						GpuModel.projectedVerticesX[normalIndex] = projectedX
						GpuModel.projectedVerticesY[normalIndex] = projectedY
					}
				} else {
					val normalStart: Int = this.triangles!![j]
					val normalEnd: Int = this.triangles!![j + 1]
					for (normalSlot in normalStart until normalEnd) {
						val normalIndex: Int = this.vertexNormalIndices!![normalSlot] - 1
						if (normalIndex == -1) {
							break
						}
						GpuModel.projectedVerticesX[this.vertexNormalIndices!![normalSlot] - 1] = -999999
					}
				}
			}
			for (faceIndex in 0 until this.numFaces) {
				if (GpuModel.projectedVerticesX[this.facesA!![faceIndex].toInt()] != -999999 && GpuModel.projectedVerticesX[this.facesB!![faceIndex].toInt()] != -999999 && GpuModel.projectedVerticesX[this.facesC!![faceIndex].toInt()] != -999999 && this.isHovering(x, GpuModel.projectedVerticesX[this.facesC!![faceIndex].toInt()], GpuModel.projectedVerticesX[this.facesB!![faceIndex].toInt()], GpuModel.projectedVerticesY[this.facesC!![faceIndex].toInt()], GpuModel.projectedVerticesY[this.facesA!![faceIndex].toInt()], y, GpuModel.projectedVerticesX[this.facesA!![faceIndex].toInt()], GpuModel.projectedVerticesY[this.facesB!![faceIndex].toInt()])) {
					return true
				}
			}
		}
		return false
	}

	@OriginalMember(owner = "client!nca", name = "d", descriptor = "(I)V")
	private fun releaseUploadedData() {
		if (!this.updateRequired) {
			return
		}
		this.updateRequired = false
		if (this.emissiveTriangles == null && this.effectiveVertices == null && this.billboardFaces == null) {
			if (this.verticesX != null && !GpuModelRetainMasks.retainVerticesX(this.preferenceFlags, this.functionMask)) {
				if (this.positionStream == null || this.positionStream!!.canReleaseSourceData()) {
					if (!this.boundsCalculated) {
						this.calculateBounds()
					}
					this.verticesX = null
				} else {
					this.updateRequired = true
				}
			}
			if (this.verticesY != null && !GpuModelRetainMasks.retainVerticesY(this.preferenceFlags, this.functionMask)) {
				if (this.positionStream == null || this.positionStream!!.canReleaseSourceData()) {
					if (!this.boundsCalculated) {
						this.calculateBounds()
					}
					this.verticesY = null
				} else {
					this.updateRequired = true
				}
			}
			if (this.verticesZ != null && !GpuModelRetainMasks.retainVerticesZ(this.preferenceFlags, this.functionMask)) {
				if (this.positionStream == null || this.positionStream!!.canReleaseSourceData()) {
					if (!this.boundsCalculated) {
						this.calculateBounds()
					}
					this.verticesZ = null
				} else {
					this.updateRequired = true
				}
			}
		}
		if (this.vertexNormalIndices != null && this.verticesX == null && this.verticesY == null && this.verticesZ == null) {
			this.triangles = null
			this.vertexNormalIndices = null
		}
		if (this.magnitudes != null && !GpuModelRetainMasks.retainNormals(this.preferenceFlags, this.functionMask)) {
			run label197@{
				run label196@{
					@Pc(161) val canRelease: Boolean
					if ((this.preferenceFlags and 0x37) == 0) {
						if (this.colourStream == null || this.colourStream!!.canReleaseSourceData()) {
							return@label196
						}
						canRelease = false
					} else {
						if (this.normalStream == null || this.normalStream!!.canReleaseSourceData()) {
							return@label196
						}
						canRelease = false
					}
					if (!canRelease) {
						this.updateRequired = true
						return@label197
					}
				}
				this.normalsZ = null
				this.normalsY = this.normalsZ
				this.normalsX = this.normalsY
				this.magnitudes = null
			}
		}
		if (this.facesColor != null && !GpuModelRetainMasks.retainFaceColors(this.preferenceFlags, this.functionMask)) {
			if (this.colourStream == null || this.colourStream!!.canReleaseSourceData()) {
				this.facesColor = null
			} else {
				this.updateRequired = true
			}
		}
		if (this.facesAlpha != null && !GpuModelRetainMasks.retainFaceAlphas(this.preferenceFlags, this.functionMask)) {
			if (this.colourStream == null || this.colourStream!!.canReleaseSourceData()) {
				this.facesAlpha = null
			} else {
				this.updateRequired = true
			}
		}
		if (this.textureU != null && !GpuModelRetainMasks.retainTextureCoordinates(this.preferenceFlags, this.functionMask)) {
			if (this.texCoordStream == null || this.texCoordStream!!.canReleaseSourceData()) {
				this.textureV = null
				this.textureU = this.textureV
			} else {
				this.updateRequired = true
			}
		}
		if (this.facesMaterial != null && !Model.retainFaceMaterials(this.functionMask, this.preferenceFlags)) {
			if (this.colourStream == null || this.colourStream!!.canReleaseSourceData()) {
				this.facesMaterial = null
			} else {
				this.updateRequired = true
			}
		}
		if (this.facesA != null && !GpuModelRetainMasks.retainFaceIndices(this.functionMask, this.preferenceFlags)) {
			if ((this.indexStream == null || this.indexStream!!.canReleaseSourceData()) && (this.colourStream == null || this.colourStream!!.canReleaseSourceData())) {
				this.facesC = null
				this.facesB = this.facesC
				this.facesA = this.facesB
			} else {
				this.updateRequired = true
			}
		}
		if (this.normalVertices != null) {
			if (this.positionStream == null || this.positionStream!!.canReleaseSourceData()) {
				this.normalVertices = null
			} else {
				this.updateRequired = true
			}
		}
		if (this.normalFaces != null) {
			if (this.colourStream == null || this.colourStream!!.canReleaseSourceData()) {
				this.normalFaces = null
			} else {
				this.updateRequired = true
			}
		}
		if (this.faceGroups != null && !GpuModelRetainMasks.altersFaces(this.preferenceFlags, this.functionMask)) {
			this.facesSubmeshes = null
			this.faceGroups = null
		}
		if (this.vertexGroups != null && !GpuModelRetainMasks.altersVertices(this.preferenceFlags, this.functionMask)) {
			this.vertexGroups = null
			this.verticesSubmeshes = null
		}
		if (this.billboardGroups != null && !GpuModelRetainMasks.retainBillboards(this.preferenceFlags, this.functionMask)) {
			this.billboardGroups = null
		}
		if (this.materialBatchOffsets != null && (this.functionMask and 0x800) == 0 && (this.functionMask and 0x40000) == 0) {
			this.materialBatchOffsets = null
			this.materialBatchVertexCount = null
			this.materialBatchMinIndex = null
		}
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(I)V")
	override fun a(@OriginalArg(0) angle: Int) {
		@Pc(9) val sin: Int = TextureCombineSource.SINE[angle]
		@Pc(13) val cos: Int = TextureCombineSource.COSINE[angle]
		for (@Pc(15) i in 0 until this.maxDepth) {
			@Pc(33) val rotatedX: Int = (cos * this.verticesX!![i] + this.verticesZ!![i] * sin) shr 14
			this.verticesZ!![i] = (cos * this.verticesZ!![i] - sin * this.verticesX!![i]) shr 14
			this.verticesX!![i] = rotatedX
		}
		this.invalidatePositions()
		this.boundsCalculated = false
	}

	@OriginalMember(owner = "client!nca", name = "FA", descriptor = "(I)V")
	override fun FA(@OriginalArg(0) angle: Int) {
		@Pc(9) val sin: Int = TextureCombineSource.SINE[angle]
		@Pc(13) val cos: Int = TextureCombineSource.COSINE[angle]
		for (@Pc(15) i in 0 until this.maxDepth) {
			@Pc(34) val rotatedY: Int = (this.verticesY!![i] * cos - this.verticesZ!![i] * sin) shr 14
			this.verticesZ!![i] = (this.verticesZ!![i] * cos + this.verticesY!![i] * sin) shr 14
			this.verticesY!![i] = rotatedY
		}
		this.invalidatePositions()
		this.boundsCalculated = false
	}

	@OriginalMember(owner = "client!nca", name = "ua", descriptor = "()I")
	override fun ua(): Int {
		return this.functionMask
	}

	@OriginalMember(owner = "client!nca", name = "G", descriptor = "()I")
	override fun G(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.maxZ
	}

	@OriginalMember(owner = "client!nca", name = "e", descriptor = "(I)V")
	fun releaseStreams() {
		if (this.positionStream != null) {
			this.positionStream!!.release()
		}
		if (this.texCoordStream != null) {
			this.texCoordStream!!.release()
		}
		if (this.colourStream != null) {
			this.colourStream!!.release()
		}
		if (this.normalStream != null) {
			this.normalStream!!.release()
		}
		if (this.indexStream != null) {
			this.indexStream!!.release()
		}
	}

	@OriginalMember(owner = "client!nca", name = "d", descriptor = "()[Lclient!nn;")
	override fun getEmissiveTriangles(): Array<EmissiveTriangle?>? {
		return this.emissiveTriangles
	}

	@OriginalMember(owner = "client!nca", name = "P", descriptor = "(IIII)V")
	override fun P(@OriginalArg(0) type: Int, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) z: Int) {
		@Pc(9) val local9: Int
		@Pc(17) val local17: Int
		if (type == 0) {
			var count = 0
			GpuModel.originX = 0
			GpuModel.originZ = 0
			GpuModel.originY = 0
			for (vertexIndex in 0 until this.maxDepth) {
				GpuModel.originX += this.verticesX!![vertexIndex]
				GpuModel.originY += this.verticesY!![vertexIndex]
				GpuModel.originZ += this.verticesZ!![vertexIndex]
				count++
			}
			if (count > 0) {
				GpuModel.originZ = z + GpuModel.originZ / count
				GpuModel.originY = y + GpuModel.originY / count
				GpuModel.originX = x + GpuModel.originX / count
			} else {
				GpuModel.originY = y
				GpuModel.originZ = z
				GpuModel.originX = x
			}
		} else if (type == 1) {
			for (vertexIndex in 0 until this.maxDepth) {
				this.verticesX!![vertexIndex] += x
				this.verticesY!![vertexIndex] += y
				this.verticesZ!![vertexIndex] += z
			}
		} else {
			@Pc(158) val local158: Int
			@Pc(176) val local176: Int
			if (type == 2) {
				for (vertexIndex in 0 until this.maxDepth) {
					this.verticesX!![vertexIndex] -= GpuModel.originX
					this.verticesY!![vertexIndex] -= GpuModel.originY
					this.verticesZ!![vertexIndex] -= GpuModel.originZ
					if (z != 0) {
						val sin: Int = TextureCombineSource.SINE[z]
						val cos: Int = TextureCombineSource.COSINE[z]
						val rotatedX: Int = (cos * this.verticesX!![vertexIndex] + this.verticesY!![vertexIndex] * sin + 16383) shr 14
						this.verticesY!![vertexIndex] = (this.verticesY!![vertexIndex] * cos + 16383 - this.verticesX!![vertexIndex] * sin) shr 14
						this.verticesX!![vertexIndex] = rotatedX
					}
					if (x != 0) {
						val sin: Int = TextureCombineSource.SINE[x]
						val cos: Int = TextureCombineSource.COSINE[x]
						val rotatedY: Int = (this.verticesY!![vertexIndex] * cos + 16383 - sin * this.verticesZ!![vertexIndex]) shr 14
						this.verticesZ!![vertexIndex] = (this.verticesZ!![vertexIndex] * cos + this.verticesY!![vertexIndex] * sin + 16383) shr 14
						this.verticesY!![vertexIndex] = rotatedY
					}
					if (y != 0) {
						val sin: Int = TextureCombineSource.SINE[y]
						val cos: Int = TextureCombineSource.COSINE[y]
						val rotatedX: Int = (cos * this.verticesX!![vertexIndex] + this.verticesZ!![vertexIndex] * sin + 16383) shr 14
						this.verticesZ!![vertexIndex] = (cos * this.verticesZ!![vertexIndex] + 16383 - sin * this.verticesX!![vertexIndex]) shr 14
						this.verticesX!![vertexIndex] = rotatedX
					}
					this.verticesX!![vertexIndex] += GpuModel.originX
					this.verticesY!![vertexIndex] += GpuModel.originY
					this.verticesZ!![vertexIndex] += GpuModel.originZ
				}
			} else if (type == 3) {
				for (vertexIndex in 0 until this.maxDepth) {
					this.verticesX!![vertexIndex] -= GpuModel.originX
					this.verticesY!![vertexIndex] -= GpuModel.originY
					this.verticesZ!![vertexIndex] -= GpuModel.originZ
					this.verticesX!![vertexIndex] = this.verticesX!![vertexIndex] * x / 128
					this.verticesY!![vertexIndex] = this.verticesY!![vertexIndex] * y / 128
					this.verticesZ!![vertexIndex] = this.verticesZ!![vertexIndex] * z / 128
					this.verticesX!![vertexIndex] += GpuModel.originX
					this.verticesY!![vertexIndex] += GpuModel.originY
					this.verticesZ!![vertexIndex] += GpuModel.originZ
				}
			} else {
				@Pc(497) var nativeBillboardFace: NativeBillboardFace
				@Pc(502) var nativeBillboardAttributes: NativeBillboardAttributes
				if (type == 5) {
					for (faceIndex in 0 until this.numFaces) {
						var alpha: Int = x * 8 + (this.facesAlpha!![faceIndex].toInt() and 0xFF)
						if (alpha < 0) {
							alpha = 0
						} else if (alpha > 255) {
							alpha = 255
						}
						this.facesAlpha!![faceIndex] = alpha.toByte()
					}
					if (this.billboardFaces != null) {
						for (billboardIndex in 0 until this.billboardCount) {
							nativeBillboardFace = this.billboardFaces!![billboardIndex]!!
							nativeBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
							nativeBillboardAttributes.colour = (nativeBillboardAttributes.colour and 0xFFFFFF) or ((255 - (this.facesAlpha!![nativeBillboardFace.face].toInt() and 0xFF)) shl 24)
						}
					}
					this.invalidateColours()
				} else if (type == 7) {
					for (faceIndex in 0 until this.numFaces) {
						val faceColour: Int = this.facesColor!![faceIndex].toInt() and 0xFFFF
						val hue: Int = (faceColour shr 10) and 0x3F
						var saturation: Int = (faceColour shr 7) and 0x7
						saturation += y / 4
						@Pc(571) var newLightness: Int = faceColour and 0x7F
						@Pc(577) val newHue: Int = (hue + x) and 0x3F
						newLightness += z
						if (saturation < 0) {
							saturation = 0
						} else if (saturation > 7) {
							saturation = 7
						}
						if (newLightness < 0) {
							newLightness = 0
						} else if (newLightness > 127) {
							newLightness = 127
						}
						this.facesColor!![faceIndex] = ((saturation shl 7) or (newHue shl 10) or newLightness).toShort()
					}
					if (this.billboardFaces != null) {
						for (billboardIndex in 0 until this.billboardCount) {
							nativeBillboardFace = this.billboardFaces!![billboardIndex]!!
							nativeBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
							nativeBillboardAttributes.colour = (ColorUtil.hslToRgb!![this.facesColor!![nativeBillboardFace.face].toInt() and 0xFFFF] and 0xFFFFFF) or (nativeBillboardAttributes.colour and 0xFF000000.toInt())
						}
					}
					this.invalidateColours()
				} else {
					@Pc(687) var nativeBillboardAttributes2: NativeBillboardAttributes
					if (type == 8) {
						for (billboardIndex in 0 until this.billboardCount) {
							nativeBillboardAttributes2 = this.billboardAttributes!![billboardIndex]!!
							nativeBillboardAttributes2.offsetY += y
							nativeBillboardAttributes2.offsetX += x
						}
					} else if (type == 10) {
						for (billboardIndex in 0 until this.billboardCount) {
							nativeBillboardAttributes2 = this.billboardAttributes!![billboardIndex]!!
							nativeBillboardAttributes2.scaleY = (nativeBillboardAttributes2.scaleY * y) shr 7
							nativeBillboardAttributes2.scaleX = (nativeBillboardAttributes2.scaleX * x) shr 7
						}
					} else if (type == 9) {
						for (billboardIndex in 0 until this.billboardCount) {
							nativeBillboardAttributes2 = this.billboardAttributes!![billboardIndex]!!
							nativeBillboardAttributes2.angle = (nativeBillboardAttributes2.angle + x) and 0x3FFF
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!nca", name = "O", descriptor = "(III)V")
	override fun O(@OriginalArg(0) scaleX: Int, @OriginalArg(1) scaleY: Int, @OriginalArg(2) scaleZ: Int) {
		for (@Pc(3) i in 0 until this.maxDepth) {
			if (scaleX != 128) {
				this.verticesX!![i] = (scaleX * this.verticesX!![i]) shr 7
			}
			if (scaleY != 128) {
				this.verticesY!![i] = (scaleY * this.verticesY!![i]) shr 7
			}
			if (scaleZ != 128) {
				this.verticesZ!![i] = (scaleZ * this.verticesZ!![i]) shr 7
			}
		}
		this.invalidatePositions()
		this.boundsCalculated = false
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(ILclient!fc;)V")
	private fun projectShadow(@OriginalArg(1) shadow: GpuShadow) {
		if (this.numNormals > GpuModel.projectedVerticesX.size) {
			GpuModel.projectedVerticesY = IntArray(this.numNormals)
			GpuModel.projectedVerticesX = IntArray(this.numNormals)
		}
		@Pc(50) val local50: Int
		@Pc(89) val local89: Int
		@Pc(98) val local98: Int
		for (@Pc(23) i in 0 until this.maxDepth) {
			val shadowX: Int = ((this.verticesX!![i] - ((this.nativeToolkit.shadowOffsetX * this.verticesY!![i]) shr 8)) shr this.nativeToolkit.shadowSizeShift) - shadow.startX
			@Pc(75) val vertexShadowZ: Int = ((this.verticesZ!![i] - ((this.nativeToolkit.shadowOffsetZ * this.verticesY!![i]) shr 8)) shr this.nativeToolkit.shadowSizeShift) - shadow.startZ
			@Pc(80) val start: Int = this.triangles!![i]
			@Pc(87) val end: Int = this.triangles!![i + 1]
			for (normalSlot in start until end) {
				val normalIndex: Int = this.vertexNormalIndices!![normalSlot] - 1
				if (normalIndex == -1) {
					break
				}
				GpuModel.projectedVerticesX[normalIndex] = shadowX
				GpuModel.projectedVerticesY[normalIndex] = vertexShadowZ
			}
		}
		for (faceIndex in 0 until this.renderedFaceCount) {
			if (this.facesAlpha == null || this.facesAlpha!![faceIndex] <= 128) {
				@Pc(138) val vertexA: Int = this.facesA!![faceIndex].toInt()
				@Pc(143) val vertexB: Int = this.facesB!![faceIndex].toInt()
				@Pc(148) val vertexC: Int = this.facesC!![faceIndex].toInt()
				val xA: Int = GpuModel.projectedVerticesX[vertexA]
				val xB: Int = GpuModel.projectedVerticesX[vertexB]
				@Pc(160) val xC: Int = GpuModel.projectedVerticesX[vertexC]
				@Pc(164) val zA: Int = GpuModel.projectedVerticesY[vertexA]
				@Pc(168) val zB: Int = GpuModel.projectedVerticesY[vertexB]
				@Pc(172) val zC: Int = GpuModel.projectedVerticesY[vertexC]
				if (-((xC - xB) * (-zA + zB)) + (zB - zC) * (xA - xB) > 0) {
					shadow.fillTriangle(zA, xA, xB, zB, zC, xC)
				}
			}
		}
	}

	@OriginalMember(owner = "client!nca", name = "k", descriptor = "(I)V")
	override fun k(@OriginalArg(0) angle: Int) {
		@Pc(9) val sin: Int = TextureCombineSource.SINE[angle]
		@Pc(13) val cos: Int = TextureCombineSource.COSINE[angle]
		@Pc(33) val local33: Int
		for (@Pc(15) i in 0 until this.maxDepth) {
			val rotatedX: Int = (this.verticesX!![i] * cos + this.verticesZ!![i] * sin) shr 14
			this.verticesZ!![i] = (cos * this.verticesZ!![i] - sin * this.verticesX!![i]) shr 14
			this.verticesX!![i] = rotatedX
		}
		for (normalIndex in 0 until this.numNormals) {
			@Pc(84) val rotatedNormalX: Int = (this.normalsZ!![normalIndex] * sin + this.normalsX!![normalIndex] * cos) shr 14
			this.normalsZ!![normalIndex] = ((cos * this.normalsZ!![normalIndex] - sin * this.normalsX!![normalIndex]) shr 14).toShort()
			this.normalsX!![normalIndex] = rotatedNormalX.toShort()
		}
		this.invalidatePositions()
		this.invalidateLighting()
		this.boundsCalculated = false
	}

	@OriginalMember(owner = "client!nca", name = "f", descriptor = "(I)Z")
	private fun buildElementsBuffer(): Boolean {
		if (this.indexStream!!.uploaded) {
			return true
		}
		if (this.indexStream!!.buffer == null) {
			this.indexStream!!.buffer = this.nativeToolkit.createIndexBuffer(this.dynamic)
		}
		@Pc(28) val gpuIndexBuffer: GpuIndexBuffer = this.indexStream!!.buffer!!
		gpuIndexBuffer.allocate(this.renderedFaceCount * 6)
		@Pc(40) val buffer: Buffer? = gpuIndexBuffer.lock()
		if (buffer != null) {
			@Pc(56) val stream: Stream = this.nativeToolkit.wrapBuffer(buffer)
			@Pc(60) var face: Int
			if (Stream.c()) {
				face = 0
				while (face < this.renderedFaceCount) {
					stream.d(this.facesA!![face].toInt())
					stream.d(this.facesB!![face].toInt())
					stream.d(this.facesC!![face].toInt())
					face++
				}
			} else {
				face = 0
				while (face < this.renderedFaceCount) {
					stream.a(this.facesA!![face].toInt())
					stream.a(this.facesB!![face].toInt())
					stream.a(this.facesC!![face].toInt())
					face++
				}
			}
			stream.a()
			if (gpuIndexBuffer.unlock()) {
				this.indexStream!!.uploaded = true
				this.updateRequired = true
				this.indexStream!!.activeBuffer = gpuIndexBuffer
				return true
			}
		}
		return false
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(Lclient!bca;IZ)V")
	override fun transform(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) submesh: Int, @OriginalArg(2) relative: Boolean) {
		if (this.verticesSubmeshes == null) {
			return
		}
		@Pc(12) val result = IntArray(3)
		for (@Pc(14) i in 0 until this.maxDepth) {
			if ((submesh and this.verticesSubmeshes!![i].toInt()) != 0) {
				if (relative) {
					matrix!!.projectRelative(this.verticesX!![i], this.verticesY!![i], this.verticesZ!![i], result)
				} else {
					matrix!!.method4652(this.verticesX!![i], this.verticesY!![i], this.verticesZ!![i], result)
				}
				this.verticesX!![i] = result[0]
				this.verticesY!![i] = result[1]
				this.verticesZ!![i] = result[2]
			}
		}
	}

	@OriginalMember(owner = "client!nca", name = "g", descriptor = "(I)V")
	private fun invalidatePositions() {
		if (this.positionStream != null) {
			this.positionStream!!.uploaded = false
		}
	}

	@OriginalMember(owner = "client!nca", name = "H", descriptor = "(III)V")
	override fun H(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int) {
		for (@Pc(7) i in 0 until this.maxDepth) {
			if (x != 0) {
				this.verticesX!![i] += x
			}
			if (y != 0) {
				this.verticesY!![i] += y
			}
			if (z != 0) {
				this.verticesZ!![i] += z
			}
		}
		this.invalidatePositions()
		this.boundsCalculated = false
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(ZLclient!nca;Lclient!nca;IZ)Lclient!ka;")
	private fun copyModelData(@OriginalArg(0) usesPreallocated: Boolean, @OriginalArg(1) gpuModel: GpuModel, @OriginalArg(2) gpuModel2: GpuModel, @OriginalArg(3) functionMask: Int, @OriginalArg(4) ensureLit: Boolean): Model {
		gpuModel2.numVertices = this.numVertices
		gpuModel2.numFaces = this.numFaces
		gpuModel2.numNormals = this.numNormals
		gpuModel2.contrast = this.contrast
		gpuModel2.maxDepth = this.maxDepth
		gpuModel2.renderedFaceCount = this.renderedFaceCount
		gpuModel2.functionMask = functionMask
		gpuModel2.preferenceFlags = this.preferenceFlags
		gpuModel2.billboardCount = this.billboardCount
		gpuModel2.textureAnimating = this.textureAnimating
		if ((functionMask and 0x100) == 0) {
			gpuModel2.hasAlphaTransition = this.hasAlphaTransition
		} else {
			gpuModel2.hasAlphaTransition = true
		}
		gpuModel2.ambient = this.ambient
		@Pc(70) val copyVerticesX: Boolean = GpuModelRetainMasks.altersVerticesX(this.preferenceFlags, functionMask)
		@Pc(78) val copyVerticesY: Boolean = GpuModelRetainMasks.altersVerticesY(functionMask, this.preferenceFlags)
		@Pc(84) val copyVerticesZ: Boolean = GpuModelRetainMasks.altersVerticesZ(this.preferenceFlags, functionMask)
		@Pc(90) val copyVertices: Boolean = copyVerticesX or copyVerticesY or copyVerticesZ
		@Pc(209) var billboardCount: Int
		if (copyVertices) {
			if (!copyVerticesX) {
				gpuModel2.verticesX = this.verticesX
			} else if (gpuModel.verticesX == null || gpuModel.verticesX!!.size < this.numVertices) {
				gpuModel.verticesX = IntArray(this.numVertices)
				gpuModel2.verticesX = gpuModel.verticesX
			} else {
				gpuModel2.verticesX = gpuModel.verticesX
			}
			if (!copyVerticesY) {
				gpuModel2.verticesY = this.verticesY
			} else if (gpuModel.verticesY == null || this.numVertices > gpuModel.verticesY!!.size) {
				gpuModel.verticesY = IntArray(this.numVertices)
				gpuModel2.verticesY = gpuModel.verticesY
			} else {
				gpuModel2.verticesY = gpuModel.verticesY
			}
			if (!copyVerticesZ) {
				gpuModel2.verticesZ = this.verticesZ
			} else if (gpuModel.verticesZ == null || gpuModel.verticesZ!!.size < this.numVertices) {
				gpuModel.verticesZ = IntArray(this.numVertices)
				gpuModel2.verticesZ = gpuModel.verticesZ
			} else {
				gpuModel2.verticesZ = gpuModel.verticesZ
			}
			billboardCount = 0
			while (billboardCount < this.numVertices) {
				if (copyVerticesX) {
					gpuModel2.verticesX!![billboardCount] = this.verticesX!![billboardCount]
				}
				if (copyVerticesY) {
					gpuModel2.verticesY!![billboardCount] = this.verticesY!![billboardCount]
				}
				if (copyVerticesZ) {
					gpuModel2.verticesZ!![billboardCount] = this.verticesZ!![billboardCount]
				}
				billboardCount++
			}
		} else {
			gpuModel2.verticesX = this.verticesX
			gpuModel2.verticesZ = this.verticesZ
			gpuModel2.verticesY = this.verticesY
		}
		if (GpuModelRetainMasks.altersAnyVertices(functionMask, this.preferenceFlags)) {
			gpuModel2.positionStream = gpuModel.positionStream
			gpuModel2.positionStream!!.activeBuffer = this.positionStream!!.activeBuffer
			gpuModel2.positionStream!!.uploaded = this.positionStream!!.uploaded
			gpuModel2.positionStream!!.dynamic = true
		} else if (GpuModelRetainMasks.hasVertices(functionMask, this.preferenceFlags)) {
			gpuModel2.positionStream = this.positionStream
		} else {
			gpuModel2.positionStream = null
		}
		if (GpuModelRetainMasks.altersColor(functionMask, this.preferenceFlags)) {
			if (gpuModel.facesColor == null || gpuModel.facesColor!!.size < this.numFaces) {
				gpuModel.facesColor = ShortArray(this.numFaces)
				gpuModel2.facesColor = gpuModel.facesColor
			} else {
				gpuModel2.facesColor = gpuModel.facesColor
			}
			billboardCount = 0
			while (billboardCount < this.numFaces) {
				gpuModel2.facesColor!![billboardCount] = this.facesColor!![billboardCount]
				billboardCount++
			}
		} else {
			gpuModel2.facesColor = this.facesColor
		}
		if (Model.retainFaceAlpha(this.preferenceFlags, functionMask)) {
			if (gpuModel.facesAlpha == null || gpuModel.facesAlpha!!.size < this.numFaces) {
				gpuModel.facesAlpha = ByteArray(this.numFaces)
				gpuModel2.facesAlpha = gpuModel.facesAlpha
			} else {
				gpuModel2.facesAlpha = gpuModel.facesAlpha
			}
			billboardCount = 0
			while (billboardCount < this.numFaces) {
				gpuModel2.facesAlpha!![billboardCount] = this.facesAlpha!![billboardCount]
				billboardCount++
			}
		} else {
			gpuModel2.facesAlpha = this.facesAlpha
		}
		if (GpuModelRetainMasks.requiresColorBufferRebuild(functionMask, this.preferenceFlags)) {
			gpuModel2.colourStream = gpuModel.colourStream
			gpuModel2.colourStream!!.dynamic = true
			gpuModel2.colourStream!!.uploaded = this.colourStream!!.uploaded
			gpuModel2.colourStream!!.activeBuffer = this.colourStream!!.activeBuffer
		} else if (GpuModelRetainMasks.hasColors(functionMask, this.preferenceFlags)) {
			gpuModel2.colourStream = this.colourStream
		} else {
			gpuModel2.colourStream = null
		}
		@Pc(598) val local598: Int
		if (GpuModelRetainMasks.altersLight(functionMask, this.preferenceFlags)) {
			if (gpuModel.normalsX == null || gpuModel.normalsX!!.size < this.numNormals) {
				billboardCount = this.numNormals
				gpuModel.normalsZ = ShortArray(billboardCount)
				gpuModel2.normalsZ = gpuModel.normalsZ
				gpuModel.normalsX = ShortArray(billboardCount)
				gpuModel2.normalsX = gpuModel.normalsX
				gpuModel.normalsY = ShortArray(billboardCount)
				gpuModel2.normalsY = gpuModel.normalsY
			} else {
				gpuModel2.normalsZ = gpuModel.normalsZ
				gpuModel2.normalsX = gpuModel.normalsX
				gpuModel2.normalsY = gpuModel.normalsY
			}
			if (this.mergedNormals == null) {
				gpuModel2.mergedNormals = null
				billboardCount = 0
				while (billboardCount < this.numNormals) {
					gpuModel2.normalsX!![billboardCount] = this.normalsX!![billboardCount]
					gpuModel2.normalsY!![billboardCount] = this.normalsY!![billboardCount]
					gpuModel2.normalsZ!![billboardCount] = this.normalsZ!![billboardCount]
					billboardCount++
				}
			} else {
				if (gpuModel.mergedNormals == null) {
					gpuModel.mergedNormals = GpuModelNormals()
				}
				gpuModel2.mergedNormals = gpuModel.mergedNormals
				@Pc(582) val targetNormals: GpuModelNormals = gpuModel2.mergedNormals!!
				if (targetNormals.normalsX == null || this.numNormals > targetNormals.normalsX!!.size) {
					val normalCount: Int = this.numNormals
					targetNormals.magnitudes = ByteArray(normalCount)
					targetNormals.normalsX = ShortArray(normalCount)
					targetNormals.normalsZ = ShortArray(normalCount)
					targetNormals.normalsY = ShortArray(normalCount)
				}
				for (normalIndex in 0 until this.numNormals) {
					gpuModel2.normalsX!![normalIndex] = this.normalsX!![normalIndex]
					gpuModel2.normalsY!![normalIndex] = this.normalsY!![normalIndex]
					gpuModel2.normalsZ!![normalIndex] = this.normalsZ!![normalIndex]
					targetNormals.normalsX!![normalIndex] = this.mergedNormals!!.normalsX!![normalIndex]
					targetNormals.normalsY!![normalIndex] = this.mergedNormals!!.normalsY!![normalIndex]
					targetNormals.normalsZ!![normalIndex] = this.mergedNormals!!.normalsZ!![normalIndex]
					targetNormals.magnitudes!![normalIndex] = this.mergedNormals!!.magnitudes!![normalIndex]
				}
			}
			gpuModel2.magnitudes = this.magnitudes
		} else {
			gpuModel2.magnitudes = this.magnitudes
			gpuModel2.mergedNormals = this.mergedNormals
			gpuModel2.normalsZ = this.normalsZ
			gpuModel2.normalsX = this.normalsX
			gpuModel2.normalsY = this.normalsY
		}
		if (GpuModelRetainMasks.requiresNormalsBufferRebuild(functionMask, this.preferenceFlags)) {
			gpuModel2.normalStream = gpuModel.normalStream
			gpuModel2.normalStream!!.activeBuffer = this.normalStream!!.activeBuffer
			gpuModel2.normalStream!!.dynamic = true
			gpuModel2.normalStream!!.uploaded = this.normalStream!!.uploaded
		} else if (GpuModelRetainMasks.hasNormals(this.preferenceFlags, functionMask)) {
			gpuModel2.normalStream = this.normalStream
		} else {
			gpuModel2.normalStream = null
		}
		if (GpuModelRetainMasks.altersTextureCoordinates(functionMask, this.preferenceFlags)) {
			if (gpuModel.textureU == null || this.numFaces > gpuModel.textureU!!.size) {
				billboardCount = this.numNormals
				gpuModel.textureV = FloatArray(billboardCount)
				gpuModel2.textureV = gpuModel.textureV
				gpuModel.textureU = FloatArray(billboardCount)
				gpuModel2.textureU = gpuModel.textureU
			} else {
				gpuModel2.textureU = gpuModel.textureU
				gpuModel2.textureV = gpuModel.textureV
			}
			billboardCount = 0
			while (billboardCount < this.numNormals) {
				gpuModel2.textureU!![billboardCount] = this.textureU!![billboardCount]
				gpuModel2.textureV!![billboardCount] = this.textureV!![billboardCount]
				billboardCount++
			}
		} else {
			gpuModel2.textureU = this.textureU
			gpuModel2.textureV = this.textureV
		}
		if (GpuModelRetainMasks.requiresTexCoordBufferRebuild(this.preferenceFlags, functionMask)) {
			gpuModel2.texCoordStream = gpuModel.texCoordStream
			gpuModel2.texCoordStream!!.activeBuffer = this.texCoordStream!!.activeBuffer
			gpuModel2.texCoordStream!!.uploaded = this.texCoordStream!!.uploaded
			gpuModel2.texCoordStream!!.dynamic = true
		} else if (GpuModelRetainMasks.hasTextureCoordinates(this.preferenceFlags, functionMask)) {
			gpuModel2.texCoordStream = this.texCoordStream
		} else {
			gpuModel2.texCoordStream = null
		}
		if (GpuModelRetainMasks.altersFacing(functionMask, this.preferenceFlags)) {
			if (gpuModel.facesA == null || this.numFaces > gpuModel.facesA!!.size) {
				billboardCount = this.numFaces
				gpuModel.facesC = ShortArray(billboardCount)
				gpuModel2.facesC = gpuModel.facesC
				gpuModel.facesB = ShortArray(billboardCount)
				gpuModel2.facesB = gpuModel.facesB
				gpuModel.facesA = ShortArray(billboardCount)
				gpuModel2.facesA = gpuModel.facesA
			} else {
				gpuModel2.facesA = gpuModel.facesA
				gpuModel2.facesB = gpuModel.facesB
				gpuModel2.facesC = gpuModel.facesC
			}
			billboardCount = 0
			while (billboardCount < this.numFaces) {
				gpuModel2.facesA!![billboardCount] = this.facesA!![billboardCount]
				gpuModel2.facesB!![billboardCount] = this.facesB!![billboardCount]
				gpuModel2.facesC!![billboardCount] = this.facesC!![billboardCount]
				billboardCount++
			}
		} else {
			gpuModel2.facesB = this.facesB
			gpuModel2.facesC = this.facesC
			gpuModel2.facesA = this.facesA
		}
		if (GpuModelRetainMasks.copyElementsBuffer(this.preferenceFlags, functionMask)) {
			gpuModel2.indexStream = gpuModel.indexStream
			gpuModel2.indexStream!!.uploaded = this.indexStream!!.uploaded
			gpuModel2.indexStream!!.dynamic = true
			gpuModel2.indexStream!!.activeBuffer = this.indexStream!!.activeBuffer
		} else if (GpuModelRetainMasks.hasElementsBuffer(functionMask, this.preferenceFlags)) {
			gpuModel2.indexStream = this.indexStream
		} else {
			gpuModel2.indexStream = null
		}
		if (GpuModelRetainMasks.altersTextures(this.preferenceFlags, functionMask)) {
			if (gpuModel.facesMaterial == null || gpuModel.facesMaterial!!.size < this.numFaces) {
				billboardCount = this.numFaces
				gpuModel.facesMaterial = ShortArray(billboardCount)
				gpuModel2.facesMaterial = gpuModel.facesMaterial
			} else {
				gpuModel2.facesMaterial = gpuModel.facesMaterial
			}
			billboardCount = 0
			while (billboardCount < this.numFaces) {
				gpuModel2.facesMaterial!![billboardCount] = this.facesMaterial!![billboardCount]
				billboardCount++
			}
		} else {
			gpuModel2.facesMaterial = this.facesMaterial
		}
		if (!GpuModelRetainMasks.copyBillboards(functionMask, this.preferenceFlags)) {
			gpuModel2.billboardAttributes = this.billboardAttributes
		} else if (gpuModel.billboardAttributes == null || this.billboardCount > gpuModel.billboardAttributes!!.size) {
			billboardCount = this.billboardCount
			gpuModel.billboardAttributes = arrayOfNulls<NativeBillboardAttributes>(billboardCount)
			gpuModel2.billboardAttributes = gpuModel.billboardAttributes
			for (billboardIndex in 0 until this.billboardCount) {
				gpuModel2.billboardAttributes!![billboardIndex] = this.billboardAttributes!![billboardIndex]!!.copy()
			}
		} else {
			gpuModel2.billboardAttributes = gpuModel.billboardAttributes
			billboardCount = 0
			while (billboardCount < this.billboardCount) {
				gpuModel2.billboardAttributes!![billboardCount]!!.copyFrom(this.billboardAttributes!![billboardCount])
				billboardCount++
			}
		}
		if (this.boundsCalculated) {
			gpuModel2.minZ = this.minZ
			gpuModel2.size2d = this.size2d
			gpuModel2.maxX = this.maxX
			gpuModel2.maxY = this.maxY
			gpuModel2.boundsCalculated = true
			gpuModel2.size3d = this.size3d
			gpuModel2.minY = this.minY
			gpuModel2.minX = this.minX
			gpuModel2.maxZ = this.maxZ
		} else {
			gpuModel2.boundsCalculated = false
		}
		gpuModel2.effectiveVertices = this.effectiveVertices
		gpuModel2.facesSubmeshes = this.facesSubmeshes
		gpuModel2.faceGroups = this.faceGroups
		gpuModel2.triangles = this.triangles
		gpuModel2.normalFaces = this.normalFaces
		gpuModel2.verticesSubmeshes = this.verticesSubmeshes
		gpuModel2.materialBatchOffsets = this.materialBatchOffsets
		gpuModel2.vertexNormalIndices = this.vertexNormalIndices
		gpuModel2.materialBatchVertexCount = this.materialBatchVertexCount
		gpuModel2.billboardFaces = this.billboardFaces
		gpuModel2.vertexGroups = this.vertexGroups
		gpuModel2.materialBatchMinIndex = this.materialBatchMinIndex
		gpuModel2.emissiveTriangles = this.emissiveTriangles
		gpuModel2.billboardGroups = this.billboardGroups
		gpuModel2.normalVertices = this.normalVertices
		return gpuModel2
	}

	@OriginalMember(owner = "client!nca", name = "ma", descriptor = "()I")
	override fun ma(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.size3d
	}

	@OriginalMember(owner = "client!nca", name = "WA", descriptor = "()I")
	override fun WA(): Int {
		return this.ambient.toInt()
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(Lclient!bca;)V")
	override fun updateParticlePosition(@OriginalArg(0) matrix: Matrix?) {
		@Pc(8) val modelMatrix: NativeMatrix? = matrix as NativeMatrix?
		@Pc(13) val local13: Int
		if (this.emissiveTriangles != null) {
			for (emitterIndex in 0 until this.emissiveTriangles!!.size) {
				@Pc(20) val modelParticleEmitter: EmissiveTriangle = this.emissiveTriangles!![emitterIndex]!!
				@Pc(22) var modelParticleEmitter2: EmissiveTriangle = modelParticleEmitter
				if (modelParticleEmitter.child != null) {
					modelParticleEmitter2 = modelParticleEmitter.child!!
				}
				modelParticleEmitter2.verticeZC = (modelMatrix!!.xD + this.verticesZ!![modelParticleEmitter.faceA].toFloat() * modelMatrix.xC + this.verticesX!![modelParticleEmitter.faceA].toFloat() * modelMatrix.xA + modelMatrix.xB * this.verticesY!![modelParticleEmitter.faceA].toFloat()).toInt()
				modelParticleEmitter2.verticeXC = (modelMatrix.yD + this.verticesZ!![modelParticleEmitter.faceA].toFloat() * modelMatrix.yC + this.verticesX!![modelParticleEmitter.faceA].toFloat() * modelMatrix.yA + this.verticesY!![modelParticleEmitter.faceA].toFloat() * modelMatrix.yB).toInt()
				modelParticleEmitter2.verticeYA = (this.verticesY!![modelParticleEmitter.faceA].toFloat() * modelMatrix.zB + modelMatrix.zA * this.verticesX!![modelParticleEmitter.faceA].toFloat() + this.verticesZ!![modelParticleEmitter.faceA].toFloat() * modelMatrix.zC + modelMatrix.zD).toInt()
				modelParticleEmitter2.verticeZA = (modelMatrix.xA * this.verticesX!![modelParticleEmitter.faceB].toFloat() + this.verticesY!![modelParticleEmitter.faceB].toFloat() * modelMatrix.xB + this.verticesZ!![modelParticleEmitter.faceB].toFloat() * modelMatrix.xC + modelMatrix.xD).toInt()
				modelParticleEmitter2.verticeXA = (modelMatrix.yD + this.verticesZ!![modelParticleEmitter.faceB].toFloat() * modelMatrix.yC + modelMatrix.yA * this.verticesX!![modelParticleEmitter.faceB].toFloat() + modelMatrix.yB * this.verticesY!![modelParticleEmitter.faceB].toFloat()).toInt()
				modelParticleEmitter2.verticeZB = (modelMatrix.zD + modelMatrix.zC * this.verticesZ!![modelParticleEmitter.faceB].toFloat() + modelMatrix.zB * this.verticesY!![modelParticleEmitter.faceB].toFloat() + modelMatrix.zA * this.verticesX!![modelParticleEmitter.faceB].toFloat()).toInt()
				modelParticleEmitter2.verticeYB = (modelMatrix.xD + modelMatrix.xB * this.verticesY!![modelParticleEmitter.vertexC].toFloat() + this.verticesX!![modelParticleEmitter.vertexC].toFloat() * modelMatrix.xA + this.verticesZ!![modelParticleEmitter.vertexC].toFloat() * modelMatrix.xC).toInt()
				modelParticleEmitter2.transformedCY = (this.verticesZ!![modelParticleEmitter.vertexC].toFloat() * modelMatrix.yC + this.verticesY!![modelParticleEmitter.vertexC].toFloat() * modelMatrix.yB + modelMatrix.yA * this.verticesX!![modelParticleEmitter.vertexC].toFloat() + modelMatrix.yD).toInt()
				modelParticleEmitter2.verticeYC = (modelMatrix.zA * this.verticesX!![modelParticleEmitter.vertexC].toFloat() + modelMatrix.zB * this.verticesY!![modelParticleEmitter.vertexC].toFloat() + this.verticesZ!![modelParticleEmitter.vertexC].toFloat() * modelMatrix.zC + modelMatrix.zD).toInt()
			}
		}
		if (this.effectiveVertices == null) {
			return
		}
		for (effectorIndex in 0 until this.effectiveVertices!!.size) {
			@Pc(365) val modelParticleEffector: EffectiveVertex = this.effectiveVertices!![effectorIndex]!!
			@Pc(367) var modelParticleEffector2: EffectiveVertex = modelParticleEffector
			if (modelParticleEffector.child != null) {
				modelParticleEffector2 = modelParticleEffector.child!!
			}
			if (modelParticleEffector.matrix == null) {
				modelParticleEffector.matrix = modelMatrix!!.copy()
			} else {
				modelParticleEffector.matrix!!.apply(modelMatrix)
			}
			modelParticleEffector2.verticeZ = (this.verticesY!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix!!.xB + this.verticesX!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.xA + this.verticesZ!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.xC + modelMatrix.xD).toInt()
			modelParticleEffector2.verticeX = (this.verticesZ!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.yC + modelMatrix.yB * this.verticesY!![modelParticleEffector.appliesOnVertex].toFloat() + this.verticesX!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.yA + modelMatrix.yD).toInt()
			modelParticleEffector2.verticeY = (this.verticesY!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.zB + this.verticesX!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.zA + this.verticesZ!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.zC + modelMatrix.zD).toInt()
		}
	}

	@OriginalMember(owner = "client!nca", name = "HA", descriptor = "()I")
	override fun HA(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.minZ
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(Lclient!bca;Lclient!iw;II)V")
	override fun renderOrtho(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) cylinder: BoundingBoxPolygon?, @OriginalArg(2) zoom: Int, @OriginalArg(3) flags: Int) {
		if (this.numNormals == 0) {
			return
		}
		@Pc(13) val viewMatrix43: NativeMatrix? = this.nativeToolkit.viewMatrix43
		@Pc(16) val modelMatrix: NativeMatrix = matrix as NativeMatrix
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		GpuModel.modelViewZD = viewMatrix43!!.zD + modelMatrix.yD * viewMatrix43!!.zB + modelMatrix.xD * viewMatrix43!!.zA + modelMatrix.zD * viewMatrix43!!.zC
		GpuModel.modelViewZB = viewMatrix43!!.zB * modelMatrix.yB + viewMatrix43!!.zA * modelMatrix.xB + viewMatrix43!!.zC * modelMatrix.zB
		@Pc(69) val depthAtMinY: Float = GpuModel.modelViewZD + this.minY.toFloat() * GpuModel.modelViewZB
		@Pc(77) val depthAtMaxY: Float = GpuModel.modelViewZB * this.maxY.toFloat() + GpuModel.modelViewZD
		@Pc(94) val nearDepth: Float
		@Pc(87) val farDepth: Float
		if (depthAtMaxY < depthAtMinY) {
			farDepth = depthAtMinY + this.size2d.toFloat()
			nearDepth = (-this.size2d).toFloat() + depthAtMaxY
		} else {
			nearDepth = (-this.size2d).toFloat() + depthAtMinY
			farDepth = this.size2d.toFloat() + depthAtMaxY
		}
		if (this.nativeToolkit.orthoFar <= nearDepth || this.nativeToolkit.camNear.toFloat() >= farDepth) {
			return
		}
		GpuModel.modelViewXB = viewMatrix43!!.xA * modelMatrix.xB + modelMatrix.yB * viewMatrix43!!.xB + modelMatrix.zB * viewMatrix43!!.xC
		GpuModel.modelViewXD = modelMatrix.yD * viewMatrix43!!.xB + viewMatrix43!!.xA * modelMatrix.xD + viewMatrix43!!.xC * modelMatrix.zD + viewMatrix43!!.xD
		@Pc(171) val xAtMinY: Float = this.minY.toFloat() * GpuModel.modelViewXB + GpuModel.modelViewXD
		@Pc(179) val xAtMaxY: Float = GpuModel.modelViewXB * this.maxY.toFloat() + GpuModel.modelViewXD
		@Pc(206) val screenLeft: Float
		@Pc(194) val screenRight: Float
		if (xAtMaxY < xAtMinY) {
			screenRight = this.nativeToolkit.projectionScaleX.toFloat() * (this.size2d.toFloat() + xAtMinY)
			screenLeft = this.nativeToolkit.projectionScaleX.toFloat() * (xAtMaxY - this.size2d.toFloat())
		} else {
			screenRight = this.nativeToolkit.projectionScaleX.toFloat() * (this.size2d.toFloat() + xAtMaxY)
			screenLeft = ((-this.size2d).toFloat() + xAtMinY) * this.nativeToolkit.projectionScaleX.toFloat()
		}
		if (this.nativeToolkit.viewX2 <= screenLeft / zoom.toFloat() || screenRight / zoom.toFloat() <= this.nativeToolkit.viewX1) {
			return
		}
		GpuModel.modelViewYB = modelMatrix.zB * viewMatrix43!!.yC + viewMatrix43!!.yB * modelMatrix.yB + modelMatrix.xB * viewMatrix43!!.yA
		GpuModel.modelViewYD = viewMatrix43!!.yD + viewMatrix43!!.yA * modelMatrix.xD + modelMatrix.yD * viewMatrix43!!.yB + modelMatrix.zD * viewMatrix43!!.yC
		@Pc(297) val yAtMinY: Float = GpuModel.modelViewYD + GpuModel.modelViewYB * this.minY.toFloat()
		@Pc(305) val yAtMaxY: Float = this.maxY.toFloat() * GpuModel.modelViewYB + GpuModel.modelViewYD
		@Pc(332) val screenTop: Float
		@Pc(320) val screenBottom: Float
		if (yAtMaxY < yAtMinY) {
			screenBottom = this.nativeToolkit.projectionScaleY.toFloat() * (yAtMinY + this.size2d.toFloat())
			screenTop = ((-this.size2d).toFloat() + yAtMaxY) * this.nativeToolkit.projectionScaleY.toFloat()
		} else {
			screenBottom = (yAtMaxY + this.size2d.toFloat()) * this.nativeToolkit.projectionScaleY.toFloat()
			screenTop = this.nativeToolkit.projectionScaleY.toFloat() * (yAtMinY - this.size2d.toFloat())
		}
		if (screenTop / zoom.toFloat() >= this.nativeToolkit.viewY2 || screenBottom / zoom.toFloat() <= this.nativeToolkit.viewY1) {
			return
		}
		if (cylinder != null || this.billboardFaces != null) {
			GpuModel.modelViewXA = viewMatrix43!!.xB * modelMatrix.yA + viewMatrix43!!.xA * modelMatrix.xA + viewMatrix43!!.xC * modelMatrix.zA
			GpuModel.modelViewXC = viewMatrix43!!.xA * modelMatrix.xC + modelMatrix.yC * viewMatrix43!!.xB + modelMatrix.zC * viewMatrix43!!.xC
			GpuModel.modelViewYC = viewMatrix43!!.yA * modelMatrix.xC + viewMatrix43!!.yB * modelMatrix.yC + viewMatrix43!!.yC * modelMatrix.zC
			GpuModel.modelViewZA = modelMatrix.zA * viewMatrix43!!.zC + viewMatrix43!!.zB * modelMatrix.yA + viewMatrix43!!.zA * modelMatrix.xA
			GpuModel.modelViewYA = modelMatrix.xA * viewMatrix43!!.yA + modelMatrix.yA * viewMatrix43!!.yB + viewMatrix43!!.yC * modelMatrix.zA
			GpuModel.modelViewZC = viewMatrix43!!.zA * modelMatrix.xC + viewMatrix43!!.zB * modelMatrix.yC + modelMatrix.zC * viewMatrix43!!.zC
		}
		if (cylinder != null) {
			@Pc(499) val centerX: Int = (this.maxX + this.minX) shr 1
			@Pc(507) val centerZ: Int = (this.minZ + this.maxZ) shr 1
			@Pc(526) val bottomX: Int = (GpuModel.modelViewXD + centerX.toFloat() * GpuModel.modelViewXA + GpuModel.modelViewXB * this.minY.toFloat() + centerZ.toFloat() * GpuModel.modelViewXC).toInt()
			@Pc(545) val bottomY: Int = (GpuModel.modelViewYD + GpuModel.modelViewYA * centerX.toFloat() + this.minY.toFloat() * GpuModel.modelViewYB + GpuModel.modelViewYC * centerZ.toFloat()).toInt()
			@Pc(564) val bottomZ: Int = (this.minY.toFloat() * GpuModel.modelViewZB + GpuModel.modelViewZA * centerX.toFloat() + GpuModel.modelViewZD + centerZ.toFloat() * GpuModel.modelViewZC).toInt()
			@Pc(583) val topX: Int = (this.maxY.toFloat() * GpuModel.modelViewXB + GpuModel.modelViewXD + GpuModel.modelViewXA * centerX.toFloat() + GpuModel.modelViewXC * centerZ.toFloat()).toInt()
			@Pc(602) val topY: Int = (GpuModel.modelViewYC * centerZ.toFloat() + GpuModel.modelViewYD + GpuModel.modelViewYA * centerX.toFloat() + GpuModel.modelViewYB * this.maxY.toFloat()).toInt()
			cylinder.bottom = bottomY * this.nativeToolkit.projectionScaleY / zoom + this.nativeToolkit.projectionCenterY
			cylinder.left = this.nativeToolkit.projectionCenterX + bottomX * this.nativeToolkit.projectionScaleX / zoom
			cylinder.top = this.nativeToolkit.projectionCenterY + this.nativeToolkit.projectionScaleY * topY / zoom
			cylinder.right = topX * this.nativeToolkit.projectionScaleX / zoom + this.nativeToolkit.projectionCenterX
			@Pc(675) val topZ: Int = (GpuModel.modelViewZC * centerZ.toFloat() + this.maxY.toFloat() * GpuModel.modelViewZB + GpuModel.modelViewZA * centerX.toFloat() + GpuModel.modelViewZD).toInt()
			if (this.nativeToolkit.camNear <= bottomZ || topZ >= this.nativeToolkit.camNear) {
				cylinder.size = this.nativeToolkit.projectionCenterX + (this.size2d + bottomX) * this.nativeToolkit.projectionScaleX / zoom - cylinder.left
				cylinder.positionCalculated = true
			}
		}
		this.nativeToolkit.setOrthoZoom(zoom.toFloat())
		this.nativeToolkit.setupOrthoSceneRenderState()
		this.nativeToolkit.setModelMatrix(modelMatrix)
		this.drawOpaqueFaces()
		this.renderBillboards()
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(IJIIIIFIFLclient!bh;I)S")
	private fun connectNormal(@OriginalArg(0) z: Int, @OriginalArg(1) uid: Long, @OriginalArg(2) face: Int, @OriginalArg(3) magnitude: Int, @OriginalArg(4) x: Int, @OriginalArg(6) u: Float, @OriginalArg(7) vertex: Int, @OriginalArg(8) v: Float, @OriginalArg(9) mesh: Mesh?, @OriginalArg(10) y: Int): Short {
		@Pc(16) val start: Int = this.triangles!![vertex]
		@Pc(23) val end: Int = this.triangles!![vertex + 1]
		@Pc(25) var freeSlot = 0
		for (@Pc(27) i in start until end) {
			@Pc(34) val normalIndex: Short = this.vertexNormalIndices!![i]
			if (normalIndex.toInt() == 0) {
				freeSlot = i
				break
			}
			if (uid == GpuModel.normalsUid!![i]) {
				return (normalIndex - 1).toShort()
			}
		}
		this.vertexNormalIndices!![freeSlot] = (this.numNormals + 1).toShort()
		GpuModel.normalsUid!![freeSlot] = uid
		this.normalFaces!![this.numNormals] = face.toShort()
		this.normalVertices!![this.numNormals] = vertex.toShort()
		this.normalsX!![this.numNormals] = x.toShort()
		this.normalsY!![this.numNormals] = y.toShort()
		this.normalsZ!![this.numNormals] = z.toShort()
		this.magnitudes!![this.numNormals] = magnitude.toByte()
		this.textureU!![this.numNormals] = u
		this.textureV!![this.numNormals] = v
		return (this.numNormals++).toShort()
	}

	@OriginalMember(owner = "client!nca", name = "r", descriptor = "()Z")
	override fun r(): Boolean {
		return this.textureAnimating
	}

	@OriginalMember(owner = "client!nca", name = "da", descriptor = "()I")
	override fun da(): Int {
		return this.contrast.toInt()
	}

	@OriginalMember(owner = "client!nca", name = "RA", descriptor = "()I")
	override fun RA(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.maxX
	}

	@OriginalMember(owner = "client!nca", name = "fa", descriptor = "()I")
	override fun fa(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.minY
	}

	@OriginalMember(owner = "client!nca", name = "h", descriptor = "(I)V")
	private fun invalidateColours() {
		if (this.colourStream != null) {
			this.colourStream!!.uploaded = false
		}
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(I[IIIIIZ)V")
	override fun applyTransformUnmasked(@OriginalArg(0) type: Int, @OriginalArg(1) labels: IntArray?, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) z: Int, @OriginalArg(5) rotation: Int, @OriginalArg(6) lights: Boolean) {
		@Pc(8) val length: Int = labels!!.size
		@Pc(28) val local28: Int
		@Pc(32) val local32: Int
		@Pc(60) val local60: Int
		@Pc(18) val scaledX: Int
		@Pc(14) val scaledY: Int
		@Pc(22) val scaledZ: Int
		@Pc(38) val local38: Int
		@Pc(52) var groupMembers: IntArray
		@Pc(54) val local54: Int
		if (type == 0) {
			scaledY = y shl 4
			scaledX = x shl 4
			scaledZ = z shl 4
			GpuModel.originY = 0
			GpuModel.originX = 0
			var count = 0
			GpuModel.originZ = 0
			for (labelIndex in 0 until length) {
				val group: Int = labels[labelIndex]
				if (this.vertexGroups!!.size > group) {
					groupMembers = this.vertexGroups!![group]!!
					for (groupMemberIndex in 0 until groupMembers.size) {
						val vertexIndex: Int = groupMembers[groupMemberIndex]
						GpuModel.originX += this.verticesX!![vertexIndex]
						GpuModel.originY += this.verticesY!![vertexIndex]
						GpuModel.originZ += this.verticesZ!![vertexIndex]
						count++
					}
				}
			}
			if (count > 0) {
				GpuModel.originY = GpuModel.originY / count + scaledY
				GpuModel.originZ = GpuModel.originZ / count + scaledZ
				GpuModel.originX = GpuModel.originX / count + scaledX
			} else {
				GpuModel.originY = scaledY
				GpuModel.originZ = scaledZ
				GpuModel.originX = scaledX
			}
			return
		}
		@Pc(162) var members: IntArray
		@Pc(164) var memberIndex: Int
		if (type == 1) {
			scaledY = y shl 4
			scaledX = x shl 4
			scaledZ = z shl 4
			for (labelIndex in 0 until length) {
				val group: Int = labels[labelIndex]
				if (group < this.vertexGroups!!.size) {
					members = this.vertexGroups!![group]!!
					memberIndex = 0
					while (memberIndex < members.size) {
						val vertexIndex: Int = members[memberIndex]
						this.verticesX!![vertexIndex] += scaledX
						this.verticesY!![vertexIndex] += scaledY
						this.verticesZ!![vertexIndex] += scaledZ
						memberIndex++
					}
				}
			}
			return
		}
		@Pc(287) val local287: Int
		@Pc(305) val local305: Int
		@Pc(762) val local762: Int
		@Pc(771) val local771: Int
		if (type == 2) {
			for (labelIndex in 0 until length) {
				val group: Int = labels[labelIndex]
				if (this.vertexGroups!!.size > group) {
					members = this.vertexGroups!![group]!!
					if ((rotation and 0x1) == 0) {
						memberIndex = 0
						while (memberIndex < members.size) {
							val vertexIndex: Int = members[memberIndex]
							this.verticesX!![vertexIndex] -= GpuModel.originX
							this.verticesY!![vertexIndex] -= GpuModel.originY
							this.verticesZ!![vertexIndex] -= GpuModel.originZ
							if (z != 0) {
								val sin: Int = TextureCombineSource.SINE[z]
								val cos: Int = TextureCombineSource.COSINE[z]
								val rotatedX: Int = (cos * this.verticesX!![vertexIndex] + this.verticesY!![vertexIndex] * sin + 16383) shr 14
								this.verticesY!![vertexIndex] = (cos * this.verticesY!![vertexIndex] + 16383 - sin * this.verticesX!![vertexIndex]) shr 14
								this.verticesX!![vertexIndex] = rotatedX
							}
							if (x != 0) {
								val sin: Int = TextureCombineSource.SINE[x]
								val cos: Int = TextureCombineSource.COSINE[x]
								val rotatedY: Int = (cos * this.verticesY!![vertexIndex] + 16383 - sin * this.verticesZ!![vertexIndex]) shr 14
								this.verticesZ!![vertexIndex] = (sin * this.verticesY!![vertexIndex] + this.verticesZ!![vertexIndex] * cos + 16383) shr 14
								this.verticesY!![vertexIndex] = rotatedY
							}
							if (y != 0) {
								val sin: Int = TextureCombineSource.SINE[y]
								val cos: Int = TextureCombineSource.COSINE[y]
								val rotatedX: Int = (sin * this.verticesZ!![vertexIndex] + cos * this.verticesX!![vertexIndex] + 16383) shr 14
								this.verticesZ!![vertexIndex] = (cos * this.verticesZ!![vertexIndex] + 16383 - sin * this.verticesX!![vertexIndex]) shr 14
								this.verticesX!![vertexIndex] = rotatedX
							}
							this.verticesX!![vertexIndex] += GpuModel.originX
							this.verticesY!![vertexIndex] += GpuModel.originY
							this.verticesZ!![vertexIndex] += GpuModel.originZ
							memberIndex++
						}
					} else {
						memberIndex = 0
						while (memberIndex < members.size) {
							val vertexIndex: Int = members[memberIndex]
							this.verticesX!![vertexIndex] -= GpuModel.originX
							this.verticesY!![vertexIndex] -= GpuModel.originY
							this.verticesZ!![vertexIndex] -= GpuModel.originZ
							if (x != 0) {
								val sin: Int = TextureCombineSource.SINE[x]
								val cos: Int = TextureCombineSource.COSINE[x]
								val rotatedY: Int = (this.verticesY!![vertexIndex] * cos + 16383 - sin * this.verticesZ!![vertexIndex]) shr 14
								this.verticesZ!![vertexIndex] = (this.verticesZ!![vertexIndex] * cos + sin * this.verticesY!![vertexIndex] + 16383) shr 14
								this.verticesY!![vertexIndex] = rotatedY
							}
							if (z != 0) {
								val sin: Int = TextureCombineSource.SINE[z]
								val cos: Int = TextureCombineSource.COSINE[z]
								val rotatedX: Int = (cos * this.verticesX!![vertexIndex] + sin * this.verticesY!![vertexIndex] + 16383) shr 14
								this.verticesY!![vertexIndex] = (this.verticesY!![vertexIndex] * cos + 16383 - this.verticesX!![vertexIndex] * sin) shr 14
								this.verticesX!![vertexIndex] = rotatedX
							}
							if (y != 0) {
								val sin: Int = TextureCombineSource.SINE[y]
								val cos: Int = TextureCombineSource.COSINE[y]
								val rotatedX: Int = (cos * this.verticesX!![vertexIndex] + sin * this.verticesZ!![vertexIndex] + 16383) shr 14
								this.verticesZ!![vertexIndex] = (this.verticesZ!![vertexIndex] * cos + 16383 - this.verticesX!![vertexIndex] * sin) shr 14
								this.verticesX!![vertexIndex] = rotatedX
							}
							this.verticesX!![vertexIndex] += GpuModel.originX
							this.verticesY!![vertexIndex] += GpuModel.originY
							this.verticesZ!![vertexIndex] += GpuModel.originZ
							memberIndex++
						}
					}
				}
			}
			if (lights) {
				for (labelIndex in 0 until length) {
					val group: Int = labels[labelIndex]
					if (group < this.vertexGroups!!.size) {
						groupMembers = this.vertexGroups!![group]!!
						for (groupMemberIndex in 0 until groupMembers.size) {
							val vertexIndex: Int = groupMembers[groupMemberIndex]
							val normalStart: Int = this.triangles!![vertexIndex]
							val normalEnd: Int = this.triangles!![vertexIndex + 1]
							for (normalSlot in normalStart until normalEnd) {
								val normalIndex: Int = this.vertexNormalIndices!![normalSlot] - 1
								if (normalIndex == -1) {
									break
								}
								@Pc(783) var sin: Int
								@Pc(787) var cos: Int
								@Pc(805) var rotated: Int
								if (z != 0) {
									sin = TextureCombineSource.SINE[z]
									cos = TextureCombineSource.COSINE[z]
									rotated = (this.normalsY!![normalIndex] * sin + this.normalsX!![normalIndex] * cos + 16383) shr 14
									this.normalsY!![normalIndex] = ((cos * this.normalsY!![normalIndex] + 16383 - this.normalsX!![normalIndex] * sin) shr 14).toShort()
									this.normalsX!![normalIndex] = rotated.toShort()
								}
								if (x != 0) {
									sin = TextureCombineSource.SINE[x]
									cos = TextureCombineSource.COSINE[x]
									rotated = (this.normalsY!![normalIndex] * cos + 16383 - sin * this.normalsZ!![normalIndex]) shr 14
									this.normalsZ!![normalIndex] = ((cos * this.normalsZ!![normalIndex] + this.normalsY!![normalIndex] * sin + 16383) shr 14).toShort()
									this.normalsY!![normalIndex] = rotated.toShort()
								}
								if (y != 0) {
									sin = TextureCombineSource.SINE[y]
									cos = TextureCombineSource.COSINE[y]
									rotated = (cos * this.normalsX!![normalIndex] + sin * this.normalsZ!![normalIndex] + 16383) shr 14
									this.normalsZ!![normalIndex] = ((this.normalsZ!![normalIndex] * cos + 16383 - sin * this.normalsX!![normalIndex]) shr 14).toShort()
									this.normalsX!![normalIndex] = rotated.toShort()
								}
							}
						}
					}
				}
				this.invalidateLighting()
			}
		} else if (type == 3) {
			for (labelIndex in 0 until length) {
				val group: Int = labels[labelIndex]
				if (this.vertexGroups!!.size > group) {
					members = this.vertexGroups!![group]!!
					memberIndex = 0
					while (memberIndex < members.size) {
						val vertexIndex: Int = members[memberIndex]
						this.verticesX!![vertexIndex] -= GpuModel.originX
						this.verticesY!![vertexIndex] -= GpuModel.originY
						this.verticesZ!![vertexIndex] -= GpuModel.originZ
						this.verticesX!![vertexIndex] = (this.verticesX!![vertexIndex] * x) shr 7
						this.verticesY!![vertexIndex] = (this.verticesY!![vertexIndex] * y) shr 7
						this.verticesZ!![vertexIndex] = (z * this.verticesZ!![vertexIndex]) shr 7
						this.verticesX!![vertexIndex] += GpuModel.originX
						this.verticesY!![vertexIndex] += GpuModel.originY
						this.verticesZ!![vertexIndex] += GpuModel.originZ
						memberIndex++
					}
				}
			}
		} else {
			@Pc(1207) var nativeBillboardAttributes: NativeBillboardAttributes
			@Pc(1115) var modified: Boolean
			@Pc(1202) var nativeBillboardFace: NativeBillboardFace
			if (type == 5) {
				if (this.faceGroups != null) {
					modified = false
					for (labelIndex in 0 until length) {
						val group: Int = labels[labelIndex]
						if (this.faceGroups!!.size > group) {
							groupMembers = this.faceGroups!![group]!!
							for (groupMemberIndex in 0 until groupMembers.size) {
								val faceIndex: Int = groupMembers[groupMemberIndex]
								var alpha: Int = (this.facesAlpha!![faceIndex].toInt() and 0xFF) + (x * 8)
								if (alpha < 0) {
									alpha = 0
								} else if (alpha > 255) {
									alpha = 255
								}
								this.facesAlpha!![faceIndex] = alpha.toByte()
							}
							modified = modified or (groupMembers.size > 0)
						}
					}
					if (modified) {
						if (this.billboardFaces != null) {
							for (billboardIndex in 0 until this.billboardCount) {
								nativeBillboardFace = this.billboardFaces!![billboardIndex]!!
								nativeBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
								nativeBillboardAttributes.colour = (nativeBillboardAttributes.colour and 0xFFFFFF) or ((255 - (this.facesAlpha!![nativeBillboardFace.face].toInt() and 0xFF)) shl 24)
							}
						}
						this.invalidateColours()
					}
				}
			} else if (type == 7) {
				if (this.faceGroups != null) {
					modified = false
					for (labelIndex in 0 until length) {
						val group: Int = labels[labelIndex]
						if (group < this.faceGroups!!.size) {
							groupMembers = this.faceGroups!![group]!!
							for (groupMemberIndex in 0 until groupMembers.size) {
								val faceIndex: Int = groupMembers[groupMemberIndex]
								val faceColour: Int = this.facesColor!![faceIndex].toInt() and 0xFFFF
								val hue: Int = (faceColour shr 10) and 0x3F
								var saturation: Int = (faceColour shr 7) and 0x7
								var lightness: Int = faceColour and 0x7F
								saturation += y / 4
								@Pc(1310) val newHue: Int = (x + hue) and 0x3F
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
								this.facesColor!![faceIndex] = (lightness or (newHue shl 10) or (saturation shl 7)).toShort()
							}
							modified = modified or (groupMembers.size > 0)
						}
					}
					if (modified) {
						if (this.billboardFaces != null) {
							for (billboardIndex in 0 until this.billboardCount) {
								nativeBillboardFace = this.billboardFaces!![billboardIndex]!!
								nativeBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
								nativeBillboardAttributes.colour = (ColorUtil.hslToRgb!![this.facesColor!![nativeBillboardFace.face].toInt() and 0xFFFF] and 0xFFFFFF) or (nativeBillboardAttributes.colour and 0xFF000000.toInt())
							}
						}
						this.invalidateColours()
					}
				}
			} else if (type == 8) {
				if (this.billboardGroups != null) {
					for (labelIndex in 0 until length) {
						val group: Int = labels[labelIndex]
						if (group < this.billboardGroups!!.size) {
							members = this.billboardGroups!![group]!!
							memberIndex = 0
							while (memberIndex < members.size) {
								nativeBillboardAttributes = this.billboardAttributes!![members[memberIndex]]!!
								nativeBillboardAttributes.offsetX += x
								nativeBillboardAttributes.offsetY += y
								memberIndex++
							}
						}
					}
				}
			} else if (type == 10) {
				if (this.billboardGroups != null) {
					for (labelIndex in 0 until length) {
						val group: Int = labels[labelIndex]
						if (group < this.billboardGroups!!.size) {
							members = this.billboardGroups!![group]!!
							memberIndex = 0
							while (memberIndex < members.size) {
								nativeBillboardAttributes = this.billboardAttributes!![members[memberIndex]]!!
								nativeBillboardAttributes.scaleX = (x * nativeBillboardAttributes.scaleX) shr 7
								nativeBillboardAttributes.scaleY = (y * nativeBillboardAttributes.scaleY) shr 7
								memberIndex++
							}
						}
					}
				}
			} else if (type == 9 && this.billboardGroups != null) {
				for (labelIndex in 0 until length) {
					val group: Int = labels[labelIndex]
					if (this.billboardGroups!!.size > group) {
						members = this.billboardGroups!![group]!!
						memberIndex = 0
						while (memberIndex < members.size) {
							nativeBillboardAttributes = this.billboardAttributes!![members[memberIndex]]!!
							nativeBillboardAttributes.angle = (x + nativeBillboardAttributes.angle) and 0x3FFF
							memberIndex++
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(Lclient!bca;Lclient!iw;I)V")
	override fun render(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) cylinder: BoundingBoxPolygon?, @OriginalArg(2) flags: Int) {
		if (this.numNormals == 0) {
			return
		}
		@Pc(16) val viewMatrix43: NativeMatrix? = this.nativeToolkit.viewMatrix43
		@Pc(19) val modelMatrix: NativeMatrix = matrix as NativeMatrix
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		GpuModel.modelViewZB = viewMatrix43!!.zA * modelMatrix.xB + viewMatrix43!!.zB * modelMatrix.yB + modelMatrix.zB * viewMatrix43!!.zC
		GpuModel.modelViewZD = modelMatrix.zD * viewMatrix43!!.zC + viewMatrix43!!.zB * modelMatrix.yD + modelMatrix.xD * viewMatrix43!!.zA + viewMatrix43!!.zD
		@Pc(72) val depthAtMinY: Float = GpuModel.modelViewZB * this.minY.toFloat() + GpuModel.modelViewZD
		@Pc(80) val depthAtMaxY: Float = this.maxY.toFloat() * GpuModel.modelViewZB + GpuModel.modelViewZD
		@Pc(96) val nearDepth: Float
		@Pc(90) val farDepth: Float
		if (depthAtMinY > depthAtMaxY) {
			farDepth = this.size2d.toFloat() + depthAtMinY
			nearDepth = depthAtMaxY - this.size2d.toFloat()
		} else {
			farDepth = this.size2d.toFloat() + depthAtMaxY
			nearDepth = depthAtMinY - this.size2d.toFloat()
		}
		if (this.nativeToolkit.perspectiveFar <= nearDepth || farDepth <= this.nativeToolkit.camNear.toFloat()) {
			return
		}
		GpuModel.modelViewXD = viewMatrix43!!.xD + viewMatrix43!!.xB * modelMatrix.yD + viewMatrix43!!.xA * modelMatrix.xD + viewMatrix43!!.xC * modelMatrix.zD
		GpuModel.modelViewXB = viewMatrix43!!.xC * modelMatrix.zB + modelMatrix.xB * viewMatrix43!!.xA + viewMatrix43!!.xB * modelMatrix.yB
		@Pc(172) val xAtMinY: Float = this.minY.toFloat() * GpuModel.modelViewXB + GpuModel.modelViewXD
		@Pc(180) val xAtMaxY: Float = GpuModel.modelViewXD + GpuModel.modelViewXB * this.maxY.toFloat()
		@Pc(207) val screenLeft: Float
		@Pc(195) val screenRight: Float
		if (xAtMinY > xAtMaxY) {
			screenRight = (xAtMinY + this.size2d.toFloat()) * this.nativeToolkit.projectionScaleX.toFloat()
			screenLeft = this.nativeToolkit.projectionScaleX.toFloat() * ((-this.size2d).toFloat() + xAtMaxY)
		} else {
			screenRight = (xAtMaxY + this.size2d.toFloat()) * this.nativeToolkit.projectionScaleX.toFloat()
			screenLeft = this.nativeToolkit.projectionScaleX.toFloat() * ((-this.size2d).toFloat() + xAtMinY)
		}
		if (this.nativeToolkit.viewX2 <= screenLeft / farDepth || screenRight / farDepth <= this.nativeToolkit.viewX1) {
			return
		}
		GpuModel.modelViewYB = modelMatrix.zB * viewMatrix43!!.yC + viewMatrix43!!.yB * modelMatrix.yB + viewMatrix43!!.yA * modelMatrix.xB
		GpuModel.modelViewYD = modelMatrix.zD * viewMatrix43!!.yC + modelMatrix.xD * viewMatrix43!!.yA + modelMatrix.yD * viewMatrix43!!.yB + viewMatrix43!!.yD
		@Pc(297) val yAtMinY: Float = this.minY.toFloat() * GpuModel.modelViewYB + GpuModel.modelViewYD
		@Pc(305) val yAtMaxY: Float = GpuModel.modelViewYB * this.maxY.toFloat() + GpuModel.modelViewYD
		@Pc(332) val screenTop: Float
		@Pc(320) val screenBottom: Float
		if (yAtMaxY < yAtMinY) {
			screenBottom = this.nativeToolkit.projectionScaleY.toFloat() * (this.size2d.toFloat() + yAtMinY)
			screenTop = this.nativeToolkit.projectionScaleY.toFloat() * ((-this.size2d).toFloat() + yAtMaxY)
		} else {
			screenBottom = (this.size2d.toFloat() + yAtMaxY) * this.nativeToolkit.projectionScaleY.toFloat()
			screenTop = (yAtMinY - this.size2d.toFloat()) * this.nativeToolkit.projectionScaleY.toFloat()
		}
		if (this.nativeToolkit.viewY2 <= screenTop / farDepth || screenBottom / farDepth <= this.nativeToolkit.viewY1) {
			return
		}
		if (cylinder != null || this.billboardFaces != null) {
			GpuModel.modelViewZA = viewMatrix43!!.zC * modelMatrix.zA + modelMatrix.xA * viewMatrix43!!.zA + viewMatrix43!!.zB * modelMatrix.yA
			GpuModel.modelViewYC = viewMatrix43!!.yA * modelMatrix.xC + modelMatrix.yC * viewMatrix43!!.yB + viewMatrix43!!.yC * modelMatrix.zC
			GpuModel.modelViewXA = viewMatrix43!!.xC * modelMatrix.zA + viewMatrix43!!.xB * modelMatrix.yA + viewMatrix43!!.xA * modelMatrix.xA
			GpuModel.modelViewXC = viewMatrix43!!.xA * modelMatrix.xC + modelMatrix.yC * viewMatrix43!!.xB + viewMatrix43!!.xC * modelMatrix.zC
			GpuModel.modelViewYA = viewMatrix43!!.yA * modelMatrix.xA + viewMatrix43!!.yB * modelMatrix.yA + viewMatrix43!!.yC * modelMatrix.zA
			GpuModel.modelViewZC = modelMatrix.zC * viewMatrix43!!.zC + modelMatrix.xC * viewMatrix43!!.zA + modelMatrix.yC * viewMatrix43!!.zB
		}
		if (cylinder != null) {
			@Pc(491) var clipped = false
			@Pc(493) var visible = true
			@Pc(502) val centerX: Int = (this.minX + this.maxX) shr 1
			@Pc(510) val centerZ: Int = (this.minZ + this.maxZ) shr 1
			@Pc(529) val bottomX: Int = (this.minY.toFloat() * GpuModel.modelViewXB + GpuModel.modelViewXD + GpuModel.modelViewXA * centerX.toFloat() + GpuModel.modelViewXC * centerZ.toFloat()).toInt()
			@Pc(548) val bottomY: Int = (GpuModel.modelViewYC * centerZ.toFloat() + centerX.toFloat() * GpuModel.modelViewYA + GpuModel.modelViewYD + GpuModel.modelViewYB * this.minY.toFloat()).toInt()
			@Pc(567) val bottomZ: Int = (GpuModel.modelViewZD + centerX.toFloat() * GpuModel.modelViewZA + this.minY.toFloat() * GpuModel.modelViewZB + centerZ.toFloat() * GpuModel.modelViewZC).toInt()
			if (bottomZ < this.nativeToolkit.camNear) {
				clipped = true
			} else {
				cylinder.bottom = this.nativeToolkit.projectionCenterY + bottomY * this.nativeToolkit.projectionScaleY / bottomZ
				cylinder.left = this.nativeToolkit.projectionScaleX * bottomX / bottomZ + this.nativeToolkit.projectionCenterX
			}
			@Pc(626) val topX: Int = (GpuModel.modelViewXB * this.maxY.toFloat() + GpuModel.modelViewXD + GpuModel.modelViewXA * centerX.toFloat() + centerZ.toFloat() * GpuModel.modelViewXC).toInt()
			@Pc(645) val topY: Int = (GpuModel.modelViewYB * this.maxY.toFloat() + GpuModel.modelViewYD + centerX.toFloat() * GpuModel.modelViewYA + centerZ.toFloat() * GpuModel.modelViewYC).toInt()
			@Pc(664) val topZ: Int = (this.maxY.toFloat() * GpuModel.modelViewZB + GpuModel.modelViewZD + centerX.toFloat() * GpuModel.modelViewZA + GpuModel.modelViewZC * centerZ.toFloat()).toInt()
			if (this.nativeToolkit.camNear > topZ) {
				clipped = true
			} else {
				cylinder.right = this.nativeToolkit.projectionCenterX + topX * this.nativeToolkit.projectionScaleX / topZ
				cylinder.top = this.nativeToolkit.projectionCenterY + topY * this.nativeToolkit.projectionScaleY / topZ
			}
			if (clipped) {
				if (this.nativeToolkit.camNear > bottomZ && topZ < this.nativeToolkit.camNear) {
					visible = false
				} else {
					@Pc(748) val clipRatio: Int
					@Pc(758) val clippedX: Int
					@Pc(769) val clippedY: Int
					if (this.nativeToolkit.camNear > bottomZ) {
						clipRatio = ((topZ - this.nativeToolkit.camNear) shl 16) / (topZ - bottomZ)
						clippedX = ((clipRatio * (topX - bottomX)) shr 16) + topX
						clippedY = topY + ((clipRatio * (topY - bottomY)) shr 16)
						cylinder.left = this.nativeToolkit.projectionCenterX + clippedX * this.nativeToolkit.projectionScaleX / this.nativeToolkit.camNear
						cylinder.bottom = clippedY * this.nativeToolkit.projectionScaleY / this.nativeToolkit.camNear + this.nativeToolkit.projectionCenterY
					} else if (this.nativeToolkit.camNear > topZ) {
						clipRatio = ((bottomZ - this.nativeToolkit.camNear) shl 16) / (bottomZ - topZ)
						clippedX = bottomX + (((bottomX - topX) * clipRatio) shr 16)
						clippedY = (((bottomY - topY) * clipRatio) shr 16) + bottomY
						cylinder.left = this.nativeToolkit.projectionCenterX + this.nativeToolkit.projectionScaleX * clippedX / this.nativeToolkit.camNear
						cylinder.bottom = this.nativeToolkit.projectionCenterY + clippedY * this.nativeToolkit.projectionScaleY / this.nativeToolkit.camNear
					}
				}
			}
			if (visible) {
				if (topZ >= bottomZ) {
					cylinder.size = this.nativeToolkit.projectionScaleX * (topX + this.size2d) / topZ + this.nativeToolkit.projectionCenterX - cylinder.right
				} else {
					cylinder.size = (bottomX + this.size2d) * this.nativeToolkit.projectionScaleX / bottomZ + this.nativeToolkit.projectionCenterX - cylinder.left
				}
				cylinder.positionCalculated = true
			}
		}
		this.nativeToolkit.setupSceneRenderState()
		this.nativeToolkit.setModelMatrix(modelMatrix)
		this.drawOpaqueFaces()
		this.renderBillboards()
	}

	@OriginalMember(owner = "client!nca", name = "s", descriptor = "(I)V")
	override fun s(@OriginalArg(0) functionMask: Int) {
		if (this.positionStream != null) {
			this.positionStream!!.dynamic = GpuModelRetainMasks.altersAnyVertices(functionMask, this.preferenceFlags)
		}
		if (this.texCoordStream != null) {
			this.texCoordStream!!.dynamic = GpuModelRetainMasks.requiresTexCoordBufferRebuild(this.preferenceFlags, functionMask)
		}
		if (this.colourStream != null) {
			this.colourStream!!.dynamic = GpuModelRetainMasks.requiresColorBufferRebuild(functionMask, this.preferenceFlags)
		}
		if (this.normalStream != null) {
			this.normalStream!!.dynamic = GpuModelRetainMasks.requiresNormalsBufferRebuild(functionMask, this.preferenceFlags)
		}
		this.functionMask = functionMask
		this.updateRequired = true
		if (this.mergedNormals != null && (this.functionMask and 0x10000) == 0) {
			this.normalsY = this.mergedNormals!!.normalsY
			this.normalsZ = this.mergedNormals!!.normalsZ
			this.normalsX = this.mergedNormals!!.normalsX
			this.magnitudes = this.mergedNormals!!.magnitudes
			this.mergedNormals = null
		}
		this.releaseUploadedData()
	}

	@OriginalMember(owner = "client!nca", name = "V", descriptor = "()I")
	override fun V(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.minX
	}

	@OriginalMember(owner = "client!nca", name = "c", descriptor = "()Z")
	override fun loadedTextures(): Boolean {
		if (this.facesMaterial == null) {
			return true
		}
		for (@Pc(12) i in 0 until this.facesMaterial!!.size) {
			if (this.facesMaterial!![i].toInt() != -1 && !this.nativeToolkit.materialProvider!!.textureAvailable(this.facesMaterial!![i].toInt())) {
				return false
			}
		}
		return true
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(IIIIIIIII)Z")
	private fun isHovering(@OriginalArg(0) mouseX: Int, @OriginalArg(1) xC: Int, @OriginalArg(2) xB: Int, @OriginalArg(3) yC: Int, @OriginalArg(4) yA: Int, @OriginalArg(5) mouseY: Int, @OriginalArg(7) xA: Int, @OriginalArg(8) yB: Int): Boolean {
		if (mouseY < yA && mouseY < yB && mouseY < yC) {
			return false
		} else if (mouseY > yA && yB < mouseY && mouseY > yC) {
			return false
		} else if (xA > mouseX && xB > mouseX && mouseX < xC) {
			return false
		} else {
			return xA >= mouseX || xB >= mouseX || mouseX <= xC
		}
	}

	@OriginalMember(owner = "client!nca", name = "b", descriptor = "()V")
	override fun uploadToGpu() {
		if (this.numNormals > 0 && this.renderedFaceCount > 0) {
			this.uploadStreams()
			this.buildElementsBuffer()
			this.releaseUploadedData()
		}
	}

	@OriginalMember(owner = "client!nca", name = "i", descriptor = "(I)V")
	private fun drawOpaqueFaces() {
		if (this.renderedFaceCount == 0) {
			return
		}
		if (this.uploadStreams() && this.buildElementsBuffer()) {
			this.nativeToolkit.setStreamSource(this.positionStream!!.activeBuffer, 0)
			this.nativeToolkit.setStreamSource(this.colourStream!!.activeBuffer, 1)
			this.nativeToolkit.setStreamSource(this.texCoordStream!!.activeBuffer, 2)
			@Pc(61) val lit: Boolean
			if ((this.preferenceFlags and 0x37) == 0) {
				lit = false
				this.nativeToolkit.toggleLights(false)
				this.nativeToolkit.setVertexDeclaration(this.nativeToolkit.positionColorTexCoordDeclaration)
			} else {
				lit = true
				this.nativeToolkit.toggleLights(true)
				this.nativeToolkit.setStreamSource(this.normalStream!!.activeBuffer, 3)
				this.nativeToolkit.setVertexDeclaration(this.nativeToolkit.positionColorTexCoordNormalDeclaration)
			}
			for (@Pc(101) i in 0 until this.materialBatchMinIndex!!.size) {
				@Pc(108) val start: Int = this.materialBatchOffsets!![i]
				@Pc(115) val end: Int = this.materialBatchOffsets!![i + 1]
				@Pc(122) var materialId: Int = this.facesMaterial!![start].toInt() and 0xFFFF
				if (materialId == 65535) {
					materialId = -1
				}
				this.nativeToolkit.setActiveMaterial(true, lit, materialId)
				this.nativeToolkit.drawIndexedPrimitive(PrimitiveType.aClass134_6, end - start, this.indexStream!!.activeBuffer, this.materialBatchMinIndex!![i], start * 3, this.materialBatchVertexCount!![i])
			}
		}
		this.releaseUploadedData()
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(IILclient!bca;ZII)Z")
	override fun pickedOrtho(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) matrix: Matrix?, @OriginalArg(3) quick: Boolean, @OriginalArg(4) sizeShift: Int, @OriginalArg(5) angle: Int): Boolean {
		return this.pick(x, sizeShift, angle, y, quick, matrix)
	}

	@OriginalMember(owner = "client!nca", name = "c", descriptor = "(B)V")
	private fun invalidateLighting() {
		if ((this.preferenceFlags and 0x37) == 0) {
			if (this.colourStream != null) {
				this.colourStream!!.uploaded = false
			}
		} else if (this.normalStream != null) {
			this.normalStream!!.uploaded = false
		}
	}

	@OriginalMember(owner = "client!nca", name = "j", descriptor = "(I)V")
	private fun invalidateIndices() {
		if (this.indexStream != null) {
			this.indexStream!!.uploaded = false
		}
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(Lclient!ka;IIIZ)V")
	override fun shareLight(@OriginalArg(0) model: Model?, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) z: Int, @OriginalArg(4) renderTypeAdjustment: Boolean) {
		@Pc(8) val gpuModel: GpuModel? = model as GpuModel
		if (this.numFaces == 0 || gpuModel!!.numFaces == 0) {
			return
		}
		@Pc(24) val otherVertexCount: Int = gpuModel!!.maxDepth
		@Pc(27) val otherVerticesX: IntArray? = gpuModel!!.verticesX
		@Pc(30) val otherVerticesY: IntArray? = gpuModel!!.verticesY
		@Pc(33) val otherVerticesZ: IntArray? = gpuModel!!.verticesZ
		@Pc(36) val otherNormalsX: ShortArray? = gpuModel!!.normalsX
		@Pc(39) val otherNormalsY: ShortArray? = gpuModel!!.normalsY
		@Pc(42) val otherNormalsZ: ShortArray? = gpuModel!!.normalsZ
		@Pc(45) val otherMagnitudes: ByteArray? = gpuModel!!.magnitudes
		@Pc(60) var mergedNormalsX: ShortArray?
		@Pc(56) var mergedNormalsY: ShortArray?
		@Pc(64) var mergedNormalsZ: ShortArray?
		@Pc(52) var mergedMagnitudes: ByteArray?
		if (this.mergedNormals == null) {
			mergedNormalsY = null
			mergedNormalsX = null
			mergedMagnitudes = null
			mergedNormalsZ = null
		} else {
			mergedMagnitudes = this.mergedNormals!!.magnitudes
			mergedNormalsY = this.mergedNormals!!.normalsY
			mergedNormalsX = this.mergedNormals!!.normalsX
			mergedNormalsZ = this.mergedNormals!!.normalsZ
		}
		@Pc(93) var otherMergedNormalsX: ShortArray?
		@Pc(89) var otherMergedNormalsY: ShortArray?
		@Pc(81) var otherMergedNormalsZ: ShortArray?
		@Pc(85) var otherMergedMagnitudes: ByteArray?
		if (gpuModel!!.mergedNormals == null) {
			otherMergedNormalsY = null
			otherMergedMagnitudes = null
			otherMergedNormalsZ = null
			otherMergedNormalsX = null
		} else {
			otherMergedNormalsZ = gpuModel!!.mergedNormals!!.normalsZ
			otherMergedMagnitudes = gpuModel!!.mergedNormals!!.magnitudes
			otherMergedNormalsY = gpuModel!!.mergedNormals!!.normalsY
			otherMergedNormalsX = gpuModel!!.mergedNormals!!.normalsX
		}
		@Pc(106) val otherTriangles: IntArray? = gpuModel!!.triangles
		@Pc(109) val otherVertexNormalIndices: ShortArray? = gpuModel!!.vertexNormalIndices
		if (!gpuModel!!.boundsCalculated) {
			gpuModel!!.calculateBounds()
		}
		@Pc(118) val otherMinY: Int = gpuModel!!.minY
		@Pc(121) val otherMaxY: Int = gpuModel!!.maxY
		@Pc(124) val otherMinX: Int = gpuModel!!.minX
		@Pc(127) val otherMaxX: Int = gpuModel!!.maxX
		@Pc(130) val otherMinZ: Int = gpuModel!!.minZ
		@Pc(133) val otherMaxZ: Int = gpuModel!!.maxZ
		for (@Pc(135) i in 0 until this.maxDepth) {
			@Pc(144) val relY: Int = this.verticesY!![i] - y
			if (otherMinY <= relY && relY <= otherMaxY) {
				@Pc(160) val relX: Int = this.verticesX!![i] - x
				if (relX >= otherMinX && relX <= otherMaxX) {
					@Pc(175) val relZ: Int = this.verticesZ!![i] - z
					if (otherMinZ <= relZ && otherMaxZ >= relZ) {
						@Pc(189) var normalIndex = -1
						@Pc(194) var start: Int = this.triangles!![i]
						@Pc(201) var end: Int = this.triangles!![i + 1]
						for (@Pc(203) j in start until end) {
							normalIndex = this.vertexNormalIndices!![j] - 1
							if (normalIndex == -1 || this.magnitudes!![normalIndex].toInt() != 0) {
								break
							}
						}
						if (normalIndex != -1) {
							for (@Pc(233) k in 0 until otherVertexCount) {
								if (relX == otherVerticesX!![k] && relZ == otherVerticesZ!![k] && otherVerticesY!![k] == relY) {
									@Pc(264) var otherNormalIndex = -1
									start = otherTriangles!![k]
									end = otherTriangles[k + 1]
									for (@Pc(276) m in start until end) {
										otherNormalIndex = otherVertexNormalIndices!![m] - 1
										if (otherNormalIndex == -1 || otherMagnitudes!![otherNormalIndex].toInt() != 0) {
											break
										}
									}
									if (otherNormalIndex != -1) {
										if (mergedNormalsX == null) {
											this.mergedNormals = GpuModelNormals()
											this.mergedNormals!!.normalsX = Arrays.copyOf(this.normalsX)
											mergedNormalsX = this.mergedNormals!!.normalsX
											this.mergedNormals!!.normalsY = Arrays.copyOf(this.normalsY)
											mergedNormalsY = this.mergedNormals!!.normalsY
											this.mergedNormals!!.normalsZ = Arrays.copyOf(this.normalsZ)
											mergedNormalsZ = this.mergedNormals!!.normalsZ
											this.mergedNormals!!.magnitudes = Arrays.copyOf(this.magnitudes)
											mergedMagnitudes = this.mergedNormals!!.magnitudes
										}
										if (otherMergedNormalsX == null) {
											gpuModel!!.mergedNormals = GpuModelNormals()
											@Pc(352) val otherMergedNormals: GpuModelNormals = gpuModel!!.mergedNormals!!
											otherMergedNormals.normalsX = Arrays.copyOf(otherNormalsX)
											otherMergedNormalsX = otherMergedNormals.normalsX
											otherMergedNormals.normalsY = Arrays.copyOf(otherNormalsY)
											otherMergedNormalsY = otherMergedNormals.normalsY
											otherMergedNormals.normalsZ = Arrays.copyOf(otherNormalsZ)
											otherMergedNormalsZ = otherMergedNormals.normalsZ
											otherMergedNormals.magnitudes = Arrays.copyOf(otherMagnitudes)
											otherMergedMagnitudes = otherMergedNormals.magnitudes
										}
										@Pc(385) var normalX: Short = this.normalsX!![normalIndex]
										@Pc(390) var normalY: Short = this.normalsY!![normalIndex]
										@Pc(395) var normalZ: Short = this.normalsZ!![normalIndex]
										start = otherTriangles[k]
										end = otherTriangles[k + 1]
										@Pc(410) var magnitude: Byte = this.magnitudes!![normalIndex]
										@Pc(420) val local420: Int
										for (@Pc(412) index in start until end) {
											val otherTargetNormalIndex: Int = otherVertexNormalIndices!![index] - 1
											if (otherTargetNormalIndex == -1) {
												break
											}
											if (otherMergedMagnitudes!![otherTargetNormalIndex].toInt() != 0) {
												otherMergedNormalsX!![otherTargetNormalIndex] = (otherMergedNormalsX[otherTargetNormalIndex] + normalX).toShort()
												otherMergedNormalsY!![otherTargetNormalIndex] = (otherMergedNormalsY[otherTargetNormalIndex] + normalY).toShort()
												otherMergedNormalsZ!![otherTargetNormalIndex] = (otherMergedNormalsZ[otherTargetNormalIndex] + normalZ).toShort()
												otherMergedMagnitudes[otherTargetNormalIndex] = (otherMergedMagnitudes[otherTargetNormalIndex] + magnitude).toByte()
											}
										}
										start = this.triangles!![i]
										normalZ = otherNormalsZ!![otherNormalIndex]
										magnitude = otherMagnitudes!![otherNormalIndex]
										normalX = otherNormalsX!![otherNormalIndex]
										normalY = otherNormalsY!![otherNormalIndex]
										end = this.triangles!![i + 1]
										for (normalSlot in start until end) {
											@Pc(506) val targetNormalIndex: Int = this.vertexNormalIndices!![normalSlot] - 1
											if (targetNormalIndex == -1) {
												break
											}
											if (mergedMagnitudes!![targetNormalIndex].toInt() != 0) {
												mergedNormalsX!![targetNormalIndex] = (mergedNormalsX[targetNormalIndex] + normalX).toShort()
												mergedNormalsY!![targetNormalIndex] = (mergedNormalsY[targetNormalIndex] + normalY).toShort()
												mergedNormalsZ!![targetNormalIndex] = (mergedNormalsZ[targetNormalIndex] + normalZ).toShort()
												mergedMagnitudes[targetNormalIndex] = (mergedMagnitudes[targetNormalIndex] + magnitude).toByte()
											}
										}
										gpuModel!!.invalidateLighting()
										this.invalidateLighting()
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
	override fun ia(@OriginalArg(0) src: Short, @OriginalArg(1) dst: Short) {
		for (@Pc(7) i in 0 until this.numFaces) {
			if (this.facesColor!![i] == src) {
				this.facesColor!![i] = dst
			}
		}
		if (this.billboardFaces != null) {
			for (@Pc(34) j in 0 until this.billboardCount) {
				@Pc(41) val nativeBillboardFace: NativeBillboardFace = this.billboardFaces!![j]!!
				@Pc(46) val nativeBillboardAttributes: NativeBillboardAttributes = this.billboardAttributes!![j]!!
				nativeBillboardAttributes.colour = (ColorUtil.hslToRgb!![this.facesColor!![nativeBillboardFace.face].toInt() and 0xFFFF] and 0xFFFFFF) or (nativeBillboardAttributes.colour and 0xFF000000.toInt())
			}
		}
		this.invalidateColours()
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(BIZ)Lclient!ka;")
	override fun copy(@OriginalArg(0) copyType: Byte, @OriginalArg(1) functionMask: Int, @OriginalArg(2) ensureLit: Boolean): Model {
		@Pc(32) val gpuModel: GpuModel?
		@Pc(36) val gpuModel2: GpuModel?
		if (copyType.toInt() == 1) {
			gpuModel2 = this.nativeToolkit.copyType1Scratch
			gpuModel = this.nativeToolkit.copyType1Result
		} else if (copyType.toInt() == 2) {
			gpuModel2 = this.nativeToolkit.copyType2Scratch
			gpuModel = this.nativeToolkit.copyType2Result
		} else if (copyType.toInt() == 3) {
			gpuModel2 = this.nativeToolkit.copyType3Scratch
			gpuModel = this.nativeToolkit.copyType3Result
		} else if (copyType.toInt() == 4) {
			gpuModel2 = this.nativeToolkit.copyType4Scratch
			gpuModel = this.nativeToolkit.copyType4Result
		} else if (copyType.toInt() == 5) {
			gpuModel = this.nativeToolkit.copyType5Result
			gpuModel2 = this.nativeToolkit.copyType5Scratch
		} else {
			gpuModel2 = GpuModel(this.nativeToolkit, 0, 0, true, false)
			gpuModel = gpuModel2
		}
		return this.copyModelData(copyType.toInt() != 0, gpuModel2!!, gpuModel!!, functionMask, ensureLit)
	}

	@OriginalMember(owner = "client!nca", name = "p", descriptor = "(IILclient!s;Lclient!s;III)V")
	override fun p(@OriginalArg(0) contourType: Int, @OriginalArg(1) contourModifier: Int, @OriginalArg(2) ground: Ground?, @OriginalArg(3) ground2: Ground?, @OriginalArg(4) x: Int, @OriginalArg(5) y: Int, @OriginalArg(6) z: Int) {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		@Pc(17) var startX: Int = x + this.minX
		@Pc(22) var endX: Int = this.maxX + x
		@Pc(27) var startZ: Int = this.minZ + z
		@Pc(33) var endZ: Int = z + this.maxZ
		if ((contourType == 1 || contourType == 2 || contourType == 3 || contourType == 5) && (startX < 0 || ground!!.width <= ((endX + ground.tileUnits) shr ground.tileScale) || startZ < 0 || ground.length <= ((ground.tileUnits + endZ) shr ground.tileScale))) {
			return
		}
		if (contourType == 4 || contourType == 5) {
			if (ground2 == null) {
				return
			}
			if (startX < 0 || ground2.width <= ((ground2.tileUnits + endX) shr ground2.tileScale) || startZ < 0 || ground2.length <= ((endZ + ground2.tileUnits) shr ground2.tileScale)) {
				return
			}
		} else {
			startX = startX shr ground!!.tileScale
			endX = (ground.tileUnits + endX - 1) shr ground.tileScale
			startZ = startZ shr ground.tileScale
			endZ = (ground.tileUnits + endZ - 1) shr ground.tileScale
			if (y == ground.getHeight(startZ, startX) && y == ground.getHeight(startZ, endX) && ground.getHeight(endZ, startX) == y && ground.getHeight(endZ, endX) == y) {
				return
			}
		}
		@Pc(192) val local192: Int
		if (contourType == 1) {
			for (vertexIndex in 0 until this.maxDepth) {
				this.verticesY!![vertexIndex] += ground!!.averageHeight(this.verticesX!![vertexIndex] + x, this.verticesZ!![vertexIndex] + z) - y
			}
		} else {
			@Pc(197) val local197: Int
			@Pc(208) val local208: Int
			if (contourType == 2) {
				val minHeight: Int = this.minY
				if (minHeight == 0) {
					return
				}
				for (vertexIndex in 0 until this.maxDepth) {
					val heightRatio: Int = (this.verticesY!![vertexIndex] shl 16) / minHeight
					if (heightRatio < contourModifier) {
						this.verticesY!![vertexIndex] -= -((contourModifier - heightRatio) * (-y + ground!!.averageHeight(x + this.verticesX!![vertexIndex], this.verticesZ!![vertexIndex] - -z)) / contourModifier)
					}
				}
			} else {
				@Pc(296) val local296: Int
				if (contourType == 3) {
					val sizeX: Int = (contourModifier and 0xFF) * 4
					val sizeZ: Int = ((contourModifier shr 8) and 0xFF) * 4
					val maxRotateX: Int = ((contourModifier shr 16) and 0xFF) shl 6
					val maxRotateZ: Int = ((contourModifier shr 24) and 0xFF) shl 6
					if (x - (sizeX shr 1) < 0 || (ground!!.width shl ground.tileScale) <= ground.tileUnits + (sizeX shr 1) + x || z - (sizeZ shr 1) < 0 || (ground.length shl ground.tileScale) <= ground.tileUnits + (sizeZ shr 1) + z) {
						return
					}
					this.hillChange(sizeX, maxRotateZ, x, sizeZ, y, maxRotateX, ground, z)
				} else if (contourType == 4) {
					val modelHeight: Int = this.maxY - this.minY
					for (vertexIndex in 0 until this.maxDepth) {
						this.verticesY!![vertexIndex] = modelHeight + this.verticesY!![vertexIndex] + ground2!!.averageHeight(this.verticesX!![vertexIndex] + x, this.verticesZ!![vertexIndex] - -z) - y
					}
				} else if (contourType == 5) {
					val modelHeight: Int = this.maxY - this.minY
					for (vertexIndex in 0 until this.maxDepth) {
						val worldX: Int = x + this.verticesX!![vertexIndex]
						val worldZ: Int = z + this.verticesZ!![vertexIndex]
						@Pc(302) val height: Int = ground!!.averageHeight(worldX, worldZ)
						@Pc(308) val upperHeight: Int = ground2!!.averageHeight(worldX, worldZ)
						@Pc(315) val heightDelta: Int = height - upperHeight - contourModifier
						this.verticesY!![vertexIndex] = ((heightDelta * ((this.verticesY!![vertexIndex] shl 8) / modelHeight)) shr 8) + height - y
					}
				}
			}
		}
		this.invalidatePositions()
		this.boundsCalculated = false
	}

	@OriginalMember(owner = "client!nca", name = "F", descriptor = "()Z")
	override fun F(): Boolean {
		return this.hasAlphaTransition
	}

	@OriginalMember(owner = "client!nca", name = "e", descriptor = "()V")
	override fun unlock() {
	}

	@OriginalMember(owner = "client!nca", name = "a", descriptor = "(IIII)V")
	override fun adjustColours(@OriginalArg(0) hue: Int, @OriginalArg(1) saturation: Int, @OriginalArg(2) lightness: Int, @OriginalArg(3) opacity: Int) {
		@Pc(16) val local16: Int
		for (@Pc(7) i in 0 until this.numFaces) {
			val faceColour: Int = this.facesColor!![i].toInt() and 0xFFFF
			@Pc(22) var faceHue: Int = (faceColour shr 10) and 0x3F
			@Pc(28) var faceSaturation: Int = (faceColour shr 7) and 0x7
			if (hue != -1) {
				faceHue += (opacity * (hue - faceHue)) shr 7
			}
			@Pc(46) var faceLightness: Int = faceColour and 0x7F
			if (saturation != -1) {
				faceSaturation += ((saturation - faceSaturation) * opacity) shr 7
			}
			if (lightness != -1) {
				faceLightness += ((lightness - faceLightness) * opacity) shr 7
			}
			this.facesColor!![i] = ((faceHue shl 10) or (faceSaturation shl 7) or faceLightness).toShort()
		}
		if (this.billboardFaces != null) {
			for (billboardIndex in 0 until this.billboardCount) {
				@Pc(109) val nativeBillboardFace: NativeBillboardFace = this.billboardFaces!![billboardIndex]!!
				@Pc(114) val nativeBillboardAttributes: NativeBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
				nativeBillboardAttributes.colour = (nativeBillboardAttributes.colour and 0xFF000000.toInt()) or (ColorUtil.hslToRgb!![this.facesColor!![nativeBillboardFace.face].toInt() and 0xFFFF] and 0xFFFFFF)
			}
		}
		this.invalidateColours()
	}

	@OriginalMember(owner = "client!nca", name = "v", descriptor = "()V")
	override fun v() {
		for (@Pc(7) i in 0 until this.maxDepth) {
			this.verticesZ!![i] = -this.verticesZ!![i]
		}
		for (@Pc(29) j in 0 until this.numNormals) {
			this.normalsZ!![j] = (-this.normalsZ!![j]).toShort()
		}
		for (@Pc(48) k in 0 until this.numFaces) {
			@Pc(55) val temp: Short = this.facesA!![k]
			this.facesA!![k] = this.facesC!![k]
			this.facesC!![k] = temp
		}
		this.invalidatePositions()
		this.invalidateLighting()
		this.invalidateIndices()
		this.boundsCalculated = false
	}

	@OriginalMember(owner = "client!nca", name = "aa", descriptor = "(SS)V")
	override fun aa(@OriginalArg(0) src: Short, @OriginalArg(1) dst: Short) {
		@Pc(9) val textureSource: MaterialProvider? = this.nativeToolkit.materialProvider
		for (@Pc(11) i in 0 until this.numFaces) {
			if (this.facesMaterial!![i] == src) {
				this.facesMaterial!![i] = dst
			}
		}
		@Pc(35) var alpha: Byte = 0
		@Pc(37) var brightness: Byte = 0
		if (src.toInt() != -1) {
			@Pc(48) val textureMetrics: MaterialType = textureSource!!.getMetrics(src.toInt() and 0xFFFF)!!
			brightness = textureMetrics.brightness
			alpha = textureMetrics.alpha
		}
		@Pc(56) var newAlpha: Byte = 0
		@Pc(58) var newBrightness: Byte = 0
		if (dst.toInt() != -1) {
			@Pc(68) val textureMetrics2: MaterialType = textureSource!!.getMetrics(dst.toInt() and 0xFFFF)!!
			newBrightness = textureMetrics2.brightness
			if (textureMetrics2.speedU.toInt() != 0 || textureMetrics2.speedV.toInt() != 0) {
				this.textureAnimating = true
			}
			newAlpha = textureMetrics2.alpha
		}
		if (!((newBrightness != brightness) or (alpha != newAlpha))) {
			return
		}
		if (this.billboardFaces != null) {
			for (@Pc(105) j in 0 until this.billboardCount) {
				@Pc(112) val nativeBillboardFace: NativeBillboardFace = this.billboardFaces!![j]!!
				@Pc(117) val nativeBillboardAttributes: NativeBillboardAttributes = this.billboardAttributes!![j]!!
				nativeBillboardAttributes.colour = (nativeBillboardAttributes.colour and 0xFF000000.toInt()) or (ColorUtil.hslToRgb!![this.facesColor!![nativeBillboardFace.face].toInt() and 0xFFFF] and 0xFFFFFF)
			}
		}
		this.invalidateColours()
	}

	@OriginalMember(owner = "client!nca", name = "C", descriptor = "(I)V")
	override fun C(@OriginalArg(0) ambient: Int) {
		this.ambient = ambient.toShort()
		this.invalidateColours()
	}

	@OriginalMember(owner = "client!nca", name = "l", descriptor = "(I)V")
	private fun calculateBounds() {
		@Pc(7) var minVertexX = 32767
		@Pc(9) var minVertexY = 32767
		@Pc(11) var minVertexZ = 32767
		@Pc(13) var maxVertexX = -32768
		@Pc(15) var maxVertexY = -32768
		@Pc(17) var maxVertexZ = -32768
		@Pc(19) var maxSquaredRadius2d = 0
		@Pc(21) var maxSquaredRadius3d = 0
		for (@Pc(23) i in 0 until this.maxDepth) {
			@Pc(30) val x: Int = this.verticesX!![i]
			@Pc(35) val y: Int = this.verticesY!![i]
			@Pc(40) val z: Int = this.verticesZ!![i]
			if (maxVertexX < x) {
				maxVertexX = x
			}
			if (minVertexY > y) {
				minVertexY = y
			}
			if (y > maxVertexY) {
				maxVertexY = y
			}
			if (x < minVertexX) {
				minVertexX = x
			}
			if (minVertexZ > z) {
				minVertexZ = z
			}
			if (z > maxVertexZ) {
				maxVertexZ = z
			}
			@Pc(82) var squaredDistance: Int = z * z + x * x
			if (squaredDistance > maxSquaredRadius2d) {
				maxSquaredRadius2d = squaredDistance
			}
			squaredDistance = y * y + x * x + z * z
			if (maxSquaredRadius3d < squaredDistance) {
				maxSquaredRadius3d = squaredDistance
			}
		}
		this.minY = minVertexY
		this.minZ = minVertexZ
		this.minX = minVertexX
		this.maxX = maxVertexX
		this.maxY = maxVertexY
		this.maxZ = maxVertexZ
		this.size2d = (kotlin.math.sqrt(maxSquaredRadius2d.toDouble()) + 0.99).toInt()
		this.size3d = (kotlin.math.sqrt(maxSquaredRadius3d.toDouble()) + 0.99).toInt()
		this.boundsCalculated = true
	}

	@OriginalMember(owner = "client!nca", name = "I", descriptor = "(I[IIIIZI[I)V")
	override fun I(@OriginalArg(0) type: Int, @OriginalArg(1) labels: IntArray?, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) z: Int, @OriginalArg(5) lights: Boolean, @OriginalArg(6) submesh: Int, @OriginalArg(7) matrix: IntArray?) {
		var x: Int = x
		var y: Int = y
		var z: Int = z
		@Pc(8) val length: Int = labels!!.size
		@Pc(29) val local29: Int
		@Pc(35) val local35: Int
		@Pc(59) val local59: Int
		@Pc(41) val local41: Int
		@Pc(51) var groupMembers: IntArray
		@Pc(53) val local53: Int
		if (type == 0) {
			x = x shl 0x4
			y = y shl 0x4
			z = z shl 0x4
			GpuModel.originY = 0
			var vertexCount = 0
			GpuModel.originZ = 0
			GpuModel.originX = 0
			for (labelIndex in 0 until length) {
				val group: Int = labels[labelIndex]
				if (group < this.vertexGroups!!.size) {
					groupMembers = this.vertexGroups!![group]!!
					for (memberIndex in 0 until groupMembers.size) {
						val vertex: Int = groupMembers[memberIndex]
						if (this.verticesSubmeshes == null || (this.verticesSubmeshes!![vertex].toInt() and submesh) != 0) {
							GpuModel.originX += this.verticesX!![vertex]
							GpuModel.originY += this.verticesY!![vertex]
							GpuModel.originZ += this.verticesZ!![vertex]
							vertexCount++
						}
					}
				}
			}
			if (vertexCount > 0) {
				GpuModel.originZ = GpuModel.originZ / vertexCount + z
				GpuModel.withinOrigin = true
				GpuModel.originY = GpuModel.originY / vertexCount + y
				GpuModel.originX = x + GpuModel.originX / vertexCount
			} else {
				GpuModel.originZ = z
				GpuModel.originX = x
				GpuModel.originY = y
			}
			return
		}
		@Pc(244) var members: IntArray
		@Pc(246) val local246: Int
		if (type == 1) {
			if (matrix != null) {
				val rotatedX: Int = (matrix[0] * x + matrix[1] * y + z * matrix[2] + 8192) shr 14
				val rotatedY: Int = (z * matrix[5] + matrix[3] * x + matrix[4] * y + 8192) shr 14
				val rotatedZ: Int = (matrix[8] * z + matrix[6] * x + y * matrix[7] + 8192) shr 14
				z = rotatedZ
				y = rotatedY
				x = rotatedX
			}
			x = x shl 0x4
			z = z shl 0x4
			y = y shl 0x4
			for (labelIndex in 0 until length) {
				val group: Int = labels[labelIndex]
				if (this.vertexGroups!!.size > group) {
					members = this.vertexGroups!![group]!!
					for (memberIndex in 0 until members.size) {
						val vertex: Int = members[memberIndex]
						if (this.verticesSubmeshes == null || (this.verticesSubmeshes!![vertex].toInt() and submesh) != 0) {
							this.verticesX!![vertex] += x
							this.verticesY!![vertex] += y
							this.verticesZ!![vertex] += z
						}
					}
				}
			}
			return
		}
		@Pc(367) val local367: Int
		@Pc(390) val local390: Int
		@Pc(416) val local416: Int
		@Pc(447) val local447: Int
		@Pc(451) val local451: Int
		@Pc(455) val local455: Int
		@Pc(459) val local459: Int
		@Pc(467) val local467: Int
		@Pc(475) val local475: Int
		@Pc(630) val local630: Int
		@Pc(655) val local655: Int
		@Pc(659) val local659: Int
		@Pc(668) val local668: Int
		@Pc(673) val local673: Int
		@Pc(677) val local677: Int
		@Pc(681) val local681: Int
		@Pc(683) val local683: Int
		@Pc(815) val local815: IntArray
		@Pc(817) val local817: Int
		@Pc(821) val local821: Int
		@Pc(825) val local825: Int
		@Pc(827) val local827: Int
		@Pc(954) val local954: Int
		if (type == 2) {
			if (matrix == null) {
				for (labelIndex in 0 until length) {
					val group: Int = labels[labelIndex]
					if (this.vertexGroups!!.size > group) {
						members = this.vertexGroups!![group]!!
						for (memberIndex in 0 until members.size) {
							val vertex: Int = members[memberIndex]
							if (this.verticesSubmeshes == null || (this.verticesSubmeshes!![vertex].toInt() and submesh) != 0) {
								this.verticesX!![vertex] -= GpuModel.originX
								this.verticesY!![vertex] -= GpuModel.originY
								this.verticesZ!![vertex] -= GpuModel.originZ
								if (z != 0) {
									val sin: Int = TextureCombineSource.SINE[z]
									val cos: Int = TextureCombineSource.COSINE[z]
									val rotated: Int = (sin * this.verticesY!![vertex] + cos * this.verticesX!![vertex] + 16383) shr 14
									this.verticesY!![vertex] = (this.verticesY!![vertex] * cos + 16383 - this.verticesX!![vertex] * sin) shr 14
									this.verticesX!![vertex] = rotated
								}
								if (x != 0) {
									val sin: Int = TextureCombineSource.SINE[x]
									val cos: Int = TextureCombineSource.COSINE[x]
									val rotated: Int = (cos * this.verticesY!![vertex] + 16383 - sin * this.verticesZ!![vertex]) shr 14
									this.verticesZ!![vertex] = (cos * this.verticesZ!![vertex] + sin * this.verticesY!![vertex] + 16383) shr 14
									this.verticesY!![vertex] = rotated
								}
								if (y != 0) {
									val sin: Int = TextureCombineSource.SINE[y]
									val cos: Int = TextureCombineSource.COSINE[y]
									val rotated: Int = (this.verticesZ!![vertex] * sin + cos * this.verticesX!![vertex] + 16383) shr 14
									this.verticesZ!![vertex] = (cos * this.verticesZ!![vertex] + 16383 - sin * this.verticesX!![vertex]) shr 14
									this.verticesX!![vertex] = rotated
								}
								this.verticesX!![vertex] += GpuModel.originX
								this.verticesY!![vertex] += GpuModel.originY
								this.verticesZ!![vertex] += GpuModel.originZ
							}
						}
					}
				}
				if (lights) {
					for (labelIndex in 0 until length) {
						val group: Int = labels[labelIndex]
						if (this.vertexGroups!!.size > group) {
							groupMembers = this.vertexGroups!![group]!!
							for (memberIndex in 0 until groupMembers.size) {
								val vertex: Int = groupMembers[memberIndex]
								if (this.verticesSubmeshes == null || (this.verticesSubmeshes!![vertex].toInt() and submesh) != 0) {
									val start: Int = this.triangles!![vertex]
									val end: Int = this.triangles!![vertex + 1]
									for (index in start until end) {
										val normalIndex: Int = this.vertexNormalIndices!![index] - 1
										if (normalIndex == -1) {
											break
										}
										if (z != 0) {
											val sin: Int = TextureCombineSource.SINE[z]
											val cos: Int = TextureCombineSource.COSINE[z]
											val rotated: Int = (cos * this.normalsX!![normalIndex] + this.normalsY!![normalIndex] * sin + 16383) shr 14
											this.normalsY!![normalIndex] = ((cos * this.normalsY!![normalIndex] + 16383 - this.normalsX!![normalIndex] * sin) shr 14).toShort()
											this.normalsX!![normalIndex] = rotated.toShort()
										}
										if (x != 0) {
											val sin: Int = TextureCombineSource.SINE[x]
											val cos: Int = TextureCombineSource.COSINE[x]
											val rotated: Int = (this.normalsY!![normalIndex] * cos + 16383 - this.normalsZ!![normalIndex] * sin) shr 14
											this.normalsZ!![normalIndex] = ((sin * this.normalsY!![normalIndex] + cos * this.normalsZ!![normalIndex] + 16383) shr 14).toShort()
											this.normalsY!![normalIndex] = rotated.toShort()
										}
										if (y != 0) {
											val sin: Int = TextureCombineSource.SINE[y]
											val cos: Int = TextureCombineSource.COSINE[y]
											val rotated: Int = (this.normalsZ!![normalIndex] * sin + cos * this.normalsX!![normalIndex] + 16383) shr 14
											this.normalsZ!![normalIndex] = ((cos * this.normalsZ!![normalIndex] + 16383 - sin * this.normalsX!![normalIndex]) shr 14).toShort()
											this.normalsX!![normalIndex] = rotated.toShort()
										}
									}
								}
							}
						}
					}
					this.invalidateLighting()
				}
			} else {
				val matrixTranslateX: Int = matrix[9] shl 4
				val matrixTranslateY: Int = matrix[10] shl 4
				val matrixTranslateZ: Int = matrix[11] shl 4
				val originOffsetX: Int = matrix[12] shl 4
				val originOffsetY: Int = matrix[13] shl 4
				val originOffsetZ: Int = matrix[14] shl 4
				if (GpuModel.withinOrigin) {
					var transformedOriginX: Int = (matrix[6] * GpuModel.originZ + GpuModel.originX * matrix[0] + GpuModel.originY * matrix[3] + 8192) shr 14
					var transformedOriginY: Int = (matrix[7] * GpuModel.originZ + matrix[1] * GpuModel.originX + matrix[4] * GpuModel.originY + 8192) shr 14
					transformedOriginX += originOffsetX
					var transformedOriginZ: Int = (GpuModel.originZ * matrix[8] + GpuModel.originX * matrix[2] + GpuModel.originY * matrix[5] + 8192) shr 14
					transformedOriginY += originOffsetY
					transformedOriginZ += originOffsetZ
					GpuModel.originX = transformedOriginX
					GpuModel.originY = transformedOriginY
					GpuModel.withinOrigin = false
					GpuModel.originZ = transformedOriginZ
				}
				@Pc(435) val rotationMatrix = IntArray(9)
				val cosX: Int = TextureCombineSource.COSINE[x]
				val sinX: Int = TextureCombineSource.SINE[x]
				val cosY: Int = TextureCombineSource.COSINE[y]
				val sinY: Int = TextureCombineSource.SINE[y]
				val cosZ: Int = TextureCombineSource.COSINE[z]
				val sinZ: Int = TextureCombineSource.SINE[z]
				val sinXCosZ: Int = (sinX * cosZ + 8192) shr 14
				val sinXSinZ: Int = (sinX * sinZ + 8192) shr 14
				rotationMatrix[5] = -sinX
				rotationMatrix[4] = (cosX * cosZ + 8192) shr 14
				rotationMatrix[1] = (-cosY * sinZ + sinY * sinXCosZ + 8192) shr 14
				rotationMatrix[6] = (-sinY * cosZ + sinXSinZ * cosY + 8192) shr 14
				rotationMatrix[7] = (sinZ * sinY + cosY * sinXCosZ + 8192) shr 14
				rotationMatrix[0] = (sinXSinZ * sinY + cosZ * cosY + 8192) shr 14
				rotationMatrix[2] = (cosX * sinY + 8192) shr 14
				rotationMatrix[8] = (cosY * cosX + 8192) shr 14
				rotationMatrix[3] = (sinZ * cosX + 8192) shr 14
				@Pc(604) val rotatedPivotX: Int = (-GpuModel.originZ * rotationMatrix[2] + rotationMatrix[1] * -GpuModel.originY + rotationMatrix[0] * -GpuModel.originX + 8192) shr 14
				val rotatedPivotY: Int = (-GpuModel.originZ * rotationMatrix[5] + -GpuModel.originX * rotationMatrix[3] + rotationMatrix[4] * -GpuModel.originY + 8192) shr 14
				val rotatedPivotZ: Int = (-GpuModel.originZ * rotationMatrix[8] + -GpuModel.originX * rotationMatrix[6] + -GpuModel.originY * rotationMatrix[7] + 8192) shr 14
				val pivotTranslateX: Int = GpuModel.originX + rotatedPivotX
				@Pc(663) val pivotTranslateY: Int = rotatedPivotY + GpuModel.originY
				val pivotTranslateZ: Int = rotatedPivotZ + GpuModel.originZ
				@Pc(671) val combinedMatrix = IntArray(9)
				for (row in 0 until 3) {
					for (column in 0 until 3) {
						var sum = 0
						for (k in 0 until 3) {
							sum += matrix[column * 3 + k] * rotationMatrix[row * 3 + k]
						}
						combinedMatrix[row * 3 + column] = (sum + 8192) shr 14
					}
				}
				var translateX: Int = (rotationMatrix[2] * originOffsetZ + rotationMatrix[0] * originOffsetX + originOffsetY * rotationMatrix[1] + 8192) shr 14
				var translateY: Int = (rotationMatrix[4] * originOffsetY + rotationMatrix[3] * originOffsetX + originOffsetZ * rotationMatrix[5] + 8192) shr 14
				translateX += pivotTranslateX
				var translateZ: Int = (originOffsetZ * rotationMatrix[8] + rotationMatrix[6] * originOffsetX + rotationMatrix[7] * originOffsetY + 8192) shr 14
				translateY += pivotTranslateY
				translateZ += pivotTranslateZ
				val transformMatrix = IntArray(9)
				for (row in 0 until 3) {
					for (column in 0 until 3) {
						var sum = 0
						for (k in 0 until 3) {
							sum += matrix[k + row * 3] * combinedMatrix[k * 3 + column]
						}
						transformMatrix[row * 3 + column] = (sum + 8192) shr 14
					}
				}
				var finalTranslateX: Int = (translateY * matrix[1] + matrix[0] * translateX + translateZ * matrix[2] + 8192) shr 14
				var finalTranslateY: Int = (matrix[5] * translateZ + matrix[4] * translateY + translateX * matrix[3] + 8192) shr 14
				var finalTranslateZ: Int = (matrix[7] * translateY + matrix[6] * translateX + translateZ * matrix[8] + 8192) shr 14
				finalTranslateX += matrixTranslateX
				finalTranslateY += matrixTranslateY
				finalTranslateZ += matrixTranslateZ
				for (labelIndex in 0 until length) {
					@Pc(960) val group: Int = labels[labelIndex]
					if (group < this.vertexGroups!!.size) {
						@Pc(970) val vertexGroup: IntArray = this.vertexGroups!![group]!!
						for (@Pc(972) i in 0 until vertexGroup.size) {
							@Pc(978) val vertex: Int = vertexGroup[i]
							if (this.verticesSubmeshes == null || (submesh and this.verticesSubmeshes!![vertex].toInt()) != 0) {
								@Pc(1023) val rotatedX: Int = (this.verticesY!![vertex] * transformMatrix[1] + this.verticesX!![vertex] * transformMatrix[0] + transformMatrix[2] * this.verticesZ!![vertex] + 8192) shr 14
								@Pc(1054) val rotatedY: Int = (transformMatrix[3] * this.verticesX!![vertex] + transformMatrix[4] * this.verticesY!![vertex] + this.verticesZ!![vertex] * transformMatrix[5] + 8192) shr 14
								@Pc(1058) val newX: Int = rotatedX + finalTranslateX
								@Pc(1089) val rotatedZ: Int = (transformMatrix[6] * this.verticesX!![vertex] + this.verticesY!![vertex] * transformMatrix[7] + this.verticesZ!![vertex] * transformMatrix[8] + 8192) shr 14
								@Pc(1093) val newY: Int = rotatedY + finalTranslateY
								@Pc(1097) val newZ: Int = rotatedZ + finalTranslateZ
								this.verticesX!![vertex] = newX
								this.verticesY!![vertex] = newY
								this.verticesZ!![vertex] = newZ
							}
						}
					}
				}
			}
		} else if (type != 3) {
			@Pc(2573) var nativeBillboardAttributes: NativeBillboardAttributes
			@Pc(2457) var modified: Boolean
			@Pc(2568) var nativeBillboardFace: NativeBillboardFace
			if (type == 5) {
				if (this.faceGroups != null) {
					modified = false
					for (labelIndex in 0 until length) {
						val group: Int = labels[labelIndex]
						if (this.faceGroups!!.size > group) {
							groupMembers = this.faceGroups!![group]!!
							for (memberIndex in 0 until groupMembers.size) {
								val face: Int = groupMembers[memberIndex]
								if (this.facesSubmeshes == null || (this.facesSubmeshes!![face].toInt() and submesh) != 0) {
									var alpha: Int = (this.facesAlpha!![face].toInt() and 0xFF) + (x * 8)
									if (alpha < 0) {
										alpha = 0
									} else if (alpha > 255) {
										alpha = 255
									}
									this.facesAlpha!![face] = alpha.toByte()
								}
							}
							modified = modified or (groupMembers.size > 0)
						}
					}
					if (modified) {
						if (this.billboardFaces != null) {
							for (billboardIndex in 0 until this.billboardCount) {
								nativeBillboardFace = this.billboardFaces!![billboardIndex]!!
								nativeBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
								nativeBillboardAttributes.colour = ((255 - (this.facesAlpha!![nativeBillboardFace.face].toInt() and 0xFF)) shl 24) or (nativeBillboardAttributes.colour and 0xFFFFFF)
							}
						}
						this.invalidateColours()
					}
				}
			} else if (type == 7) {
				if (this.faceGroups != null) {
					modified = false
					for (labelIndex in 0 until length) {
						val group: Int = labels[labelIndex]
						if (group < this.faceGroups!!.size) {
							groupMembers = this.faceGroups!![group]!!
							for (memberIndex in 0 until groupMembers.size) {
								val face: Int = groupMembers[memberIndex]
								if (this.facesSubmeshes == null || (this.facesSubmeshes!![face].toInt() and submesh) != 0) {
									val color: Int = this.facesColor!![face].toInt() and 0xFFFF
									val hue: Int = (color shr 10) and 0x3F
									var saturation: Int = (color shr 7) and 0x7
									saturation += y / 4
									@Pc(2680) val newHue: Int = (x + hue) and 0x3F
									var lightness: Int = color and 0x7F
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
									this.facesColor!![face] = ((saturation shl 7) or (newHue shl 10) or lightness).toShort()
								}
							}
							modified = modified or (groupMembers.size > 0)
						}
					}
					if (modified) {
						if (this.billboardFaces != null) {
							for (billboardIndex in 0 until this.billboardCount) {
								nativeBillboardFace = this.billboardFaces!![billboardIndex]!!
								nativeBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
								nativeBillboardAttributes.colour = (nativeBillboardAttributes.colour and 0xFF000000.toInt()) or (ColorUtil.hslToRgb!![this.facesColor!![nativeBillboardFace.face].toInt() and 0xFFFF] and 0xFFFFFF)
							}
						}
						this.invalidateColours()
					}
				}
			} else if (type == 8) {
				if (this.billboardGroups != null) {
					for (labelIndex in 0 until length) {
						val group: Int = labels[labelIndex]
						if (group < this.billboardGroups!!.size) {
							members = this.billboardGroups!![group]!!
							for (memberIndex in 0 until members.size) {
								nativeBillboardAttributes = this.billboardAttributes!![members[memberIndex]]!!
								nativeBillboardAttributes.offsetX += x
								nativeBillboardAttributes.offsetY += y
							}
						}
					}
				}
			} else if (type == 10) {
				if (this.billboardGroups != null) {
					for (labelIndex in 0 until length) {
						val group: Int = labels[labelIndex]
						if (this.billboardGroups!!.size > group) {
							members = this.billboardGroups!![group]!!
							for (memberIndex in 0 until members.size) {
								nativeBillboardAttributes = this.billboardAttributes!![members[memberIndex]]!!
								nativeBillboardAttributes.scaleY = (y * nativeBillboardAttributes.scaleY) shr 7
								nativeBillboardAttributes.scaleX = (x * nativeBillboardAttributes.scaleX) shr 7
							}
						}
					}
				}
			} else if (type == 9 && this.billboardGroups != null) {
				for (labelIndex in 0 until length) {
					val group: Int = labels[labelIndex]
					if (this.billboardGroups!!.size > group) {
						members = this.billboardGroups!![group]!!
						for (memberIndex in 0 until members.size) {
							nativeBillboardAttributes = this.billboardAttributes!![members[memberIndex]]!!
							nativeBillboardAttributes.angle = (x + nativeBillboardAttributes.angle) and 0x3FFF
						}
					}
				}
			}
		} else if (matrix == null) {
			for (labelIndex in 0 until length) {
				val group: Int = labels[labelIndex]
				if (group < this.vertexGroups!!.size) {
					members = this.vertexGroups!![group]!!
					for (memberIndex in 0 until members.size) {
						val vertex: Int = members[memberIndex]
						if (this.verticesSubmeshes == null || (submesh and this.verticesSubmeshes!![vertex].toInt()) != 0) {
							this.verticesX!![vertex] -= GpuModel.originX
							this.verticesY!![vertex] -= GpuModel.originY
							this.verticesZ!![vertex] -= GpuModel.originZ
							this.verticesX!![vertex] = (x * this.verticesX!![vertex]) shr 7
							this.verticesY!![vertex] = (this.verticesY!![vertex] * y) shr 7
							this.verticesZ!![vertex] = (z * this.verticesZ!![vertex]) shr 7
							this.verticesX!![vertex] += GpuModel.originX
							this.verticesY!![vertex] += GpuModel.originY
							this.verticesZ!![vertex] += GpuModel.originZ
						}
					}
				}
			}
		} else {
			val matrixTranslateX: Int = matrix[9] shl 4
			val matrixTranslateY: Int = matrix[10] shl 4
			val matrixTranslateZ: Int = matrix[11] shl 4
			val originOffsetX: Int = matrix[12] shl 4
			val originOffsetY: Int = matrix[13] shl 4
			val originOffsetZ: Int = matrix[14] shl 4
			if (GpuModel.withinOrigin) {
				var transformedOriginX: Int = (GpuModel.originZ * matrix[6] + GpuModel.originX * matrix[0] + matrix[3] * GpuModel.originY + 8192) shr 14
				var transformedOriginY: Int = (GpuModel.originX * matrix[1] + GpuModel.originY * matrix[4] + matrix[7] * GpuModel.originZ + 8192) shr 14
				transformedOriginX += originOffsetX
				transformedOriginY += originOffsetY
				var transformedOriginZ: Int = (matrix[2] * GpuModel.originX + matrix[5] * GpuModel.originY + GpuModel.originZ * matrix[8] + 8192) shr 14
				transformedOriginZ += originOffsetZ
				GpuModel.originX = transformedOriginX
				GpuModel.originY = transformedOriginY
				GpuModel.withinOrigin = false
				GpuModel.originZ = transformedOriginZ
			}
			val scaleX: Int = (x shl 15) shr 7
			val scaleY: Int = (y shl 15) shr 7
			val scaleZ: Int = (z shl 15) shr 7
			val scaledOriginX: Int = (-GpuModel.originX * scaleX + 8192) shr 14
			val scaledOriginY: Int = (-GpuModel.originY * scaleY + 8192) shr 14
			val scaledOriginZ: Int = (-GpuModel.originZ * scaleZ + 8192) shr 14
			val pivotTranslateX: Int = scaledOriginX + GpuModel.originX
			val pivotTranslateY: Int = scaledOriginY + GpuModel.originY
			val pivotTranslateZ: Int = GpuModel.originZ + scaledOriginZ
			@Pc(1987) val scaleMatrix: IntArray = intArrayOf((scaleX * matrix[0] + 8192) shr 14, (matrix[3] * scaleX + 8192) shr 14, (matrix[6] * scaleX + 8192) shr 14, (scaleY * matrix[1] + 8192) shr 14, (scaleY * matrix[4] + 8192) shr 14, (scaleY * matrix[7] + 8192) shr 14, (scaleZ * matrix[2] + 8192) shr 14, (scaleZ * matrix[5] + 8192) shr 14, (scaleZ * matrix[8] + 8192) shr 14)
			val scaledOffsetX: Int = (originOffsetX * scaleX + 8192) shr 14
			val scaledOffsetY: Int = (originOffsetY * scaleY + 8192) shr 14
			@Pc(2115) val translateX: Int = scaledOffsetX + pivotTranslateX
			val scaledOffsetZ: Int = (scaleZ * originOffsetZ + 8192) shr 14
			@Pc(2127) val translateY: Int = scaledOffsetY + pivotTranslateY
			@Pc(2131) val translateZ: Int = scaledOffsetZ + pivotTranslateZ
			@Pc(2134) val combinedMatrix = IntArray(9)
			@Pc(2140) val local2140: Int
			for (row in 0 until 3) {
				for (column in 0 until 3) {
					var sum = 0
					for (k in 0 until 3) {
						sum += scaleMatrix[column + k * 3] * matrix[k + row * 3]
					}
					combinedMatrix[row * 3 + column] = (sum + 8192) shr 14
				}
			}
			var finalTranslateX: Int = (translateY * matrix[1] + translateX * matrix[0] + matrix[2] * translateZ + 8192) shr 14
			var finalTranslateY: Int = (translateZ * matrix[5] + matrix[3] * translateX + translateY * matrix[4] + 8192) shr 14
			var finalTranslateZ: Int = (translateX * matrix[6] + translateY * matrix[7] + matrix[8] * translateZ + 8192) shr 14
			finalTranslateY += matrixTranslateY
			finalTranslateX += matrixTranslateX
			finalTranslateZ += matrixTranslateZ
			for (labelIndex in 0 until length) {
				val group: Int = labels[labelIndex]
				if (group < this.vertexGroups!!.size) {
					val vertexGroup: IntArray = this.vertexGroups!![group]!!
					for (groupVertexIndex in 0 until vertexGroup.size) {
						val vertex: Int = vertexGroup[groupVertexIndex]
						if (this.verticesSubmeshes == null || (submesh and this.verticesSubmeshes!![vertex].toInt()) != 0) {
							val rotatedX: Int = (combinedMatrix[1] * this.verticesY!![vertex] + this.verticesX!![vertex] * combinedMatrix[0] + this.verticesZ!![vertex] * combinedMatrix[2] + 8192) shr 14
							val rotatedY: Int = (combinedMatrix[5] * this.verticesZ!![vertex] + combinedMatrix[4] * this.verticesY!![vertex] + combinedMatrix[3] * this.verticesX!![vertex] + 8192) shr 14
							@Pc(2382) val newY: Int = rotatedY + finalTranslateY
							@Pc(2386) val newX: Int = rotatedX + finalTranslateX
							val rotatedZ: Int = (this.verticesY!![vertex] * combinedMatrix[7] + this.verticesX!![vertex] * combinedMatrix[6] + combinedMatrix[8] * this.verticesZ!![vertex] + 8192) shr 14
							this.verticesX!![vertex] = newX
							@Pc(2427) val newZ: Int = rotatedZ + finalTranslateZ
							this.verticesY!![vertex] = newY
							this.verticesZ!![vertex] = newZ
						}
					}
				}
			}
		}
	}

	companion object {
		@OriginalMember(owner = "client!ef", name = "a", descriptor = "F")
		@JvmField
		var modelViewXD: Float = 0f

		@OriginalMember(owner = "client!fd", name = "Q", descriptor = "[J")
		@JvmField
		var normalsUid: LongArray? = null

		@OriginalMember(owner = "client!jfa", name = "G", descriptor = "F")
		@JvmField
		var modelViewXA: Float = 0f
		@OriginalMember(owner = "client!bca", name = "a", descriptor = "[I")
		@JvmField
		var projectedVerticesY: IntArray = IntArray(1)

		@OriginalMember(owner = "client!jn", name = "y", descriptor = "F")
		@JvmField
		var modelViewYA: Float = 0f

		@OriginalMember(owner = "client!ed", name = "T", descriptor = "[I")
		@JvmField
		val BOX_CORNERS_X: IntArray = IntArray(8)

		@OriginalMember(owner = "client!paa", name = "Sb", descriptor = "Z")
		@JvmField
		var withinOrigin: Boolean = false

		@OriginalMember(owner = "client!ha", name = "v", descriptor = "F")
		@JvmField
		var modelViewZC: Float = 0f
		@OriginalMember(owner = "client!cd", name = "e", descriptor = "F")
		@JvmField
		var modelViewYB: Float = 0f

		@OriginalMember(owner = "client!db", name = "S", descriptor = "[I")
		@JvmField
		val BOX_CORNERS_Y: IntArray = IntArray(8)

		@OriginalMember(owner = "client!efa", name = "a", descriptor = "I")
		@JvmField
		var originZ: Int = 0

		@OriginalMember(owner = "client!ta", name = "b", descriptor = "F")
		@JvmField
		var modelViewZD: Float = 0f

		@OriginalMember(owner = "client!ar", name = "G", descriptor = "F")
		@JvmField
		var modelViewYD: Float = 0f

		@OriginalMember(owner = "client!nv", name = "i", descriptor = "I")
		@JvmField
		var originX: Int = 0
		@OriginalMember(owner = "client!lm", name = "C", descriptor = "F")
		@JvmField
		var modelViewXC: Float = 0f

		@OriginalMember(owner = "client!hs", name = "n", descriptor = "I")
		@JvmField
		var originY: Int = 0

		@OriginalMember(owner = "client!rl", name = "h", descriptor = "F")
		@JvmField
		var modelViewZA: Float = 0f

		@OriginalMember(owner = "client!fga", name = "c", descriptor = "[I")
		@JvmField
		val BOX_CORNERS_Z: IntArray = IntArray(8)
		@OriginalMember(owner = "client!saa", name = "N", descriptor = "[I")
		@JvmField
		var projectedVerticesX: IntArray = IntArray(1)

		@OriginalMember(owner = "client!ql", name = "o", descriptor = "F")
		@JvmField
		var modelViewYC: Float = 0f

		@OriginalMember(owner = "client!en", name = "a", descriptor = "F")
		@JvmField
		var modelViewZB: Float = 0f

		@OriginalMember(owner = "client!vh", name = "c", descriptor = "[F")
		@JvmField
		val UV_RESULT: FloatArray = FloatArray(2)
		@OriginalMember(owner = "client!mq", name = "eb", descriptor = "F")
		@JvmField
		var modelViewXB: Float = 0f
	}
}
