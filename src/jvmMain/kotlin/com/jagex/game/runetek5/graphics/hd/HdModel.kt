/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlModel (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.client.features.RenderStats
import com.jagex.game.runetek5.graphics.hd.scene.HdDynamicStream
import com.jagex.game.runetek5.graphics.hd.scene.HdSceneRenderer

import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model

import com.jagex.game.runetek5.graphics.texture.TextureTransformationMatrix
import com.jagex.game.runetek5.VertexNormalGL
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.FaceNormalGL
import com.jagex.game.runetek5.graphics.model.billboard.BillboardSurfaceGL
import com.jagex.game.runetek5.graphics.model.billboard.BillboardType
import com.jagex.game.runetek5.Matrix43
import com.jagex.game.runetek5.Protocol
import com.jagex.game.runetek5.load.ModelTransformOriginY
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.normal.MeshBillboard
import com.jagex.game.runetek5.entity.BoundingBoxPolygon
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import com.jagex.game.runetek5.js5.ModelTexCoordFlag
import jaclib.memory.Stream
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.buffer.ElementsBufferGL
import com.jagex.game.runetek5.graphics.model.buffer.ModelElementsBufferGL
import com.jagex.game.runetek5.graphics.model.normal.ModelNormalsGL
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer
import com.jagex.game.runetek5.graphics.gl.GlBillboardFace
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.graphics.model.billboard.BillboardTypeList
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

class HdModel : Model {

	private var triangles: IntArray? = null

	private var billboardCount: Int = 0

	private var vertexGroups: Array<IntArray?>? = null

	private var minX: Short = 0

	private var effectiveVertices: Array<EffectiveVertex?>? = null

	private var facesAlpha: ByteArray? = null

	private var verticesX: IntArray? = null

	private var ambient: Short = 0

	private var textureU: FloatArray? = null

	private var faceGroups: Array<IntArray?>? = null

	private var dynamicArrayBuffer: ArrayBufferGL? = null

	private var preferenceFlags: Int = 0

	private var size3d: Short = 0

	private var normalsX: ShortArray? = null

	private var facesC: ShortArray? = null

	private var textureV: FloatArray? = null

	private var mergedNormals: ModelNormalsGL? = null

	private var facesSubmeshes: ShortArray? = null

	private var normalsZ: ShortArray? = null

	private var facesB: ShortArray? = null

	private var facesColor: ShortArray? = null

	private var renderState: Byte = 0

	private var normalsY: ShortArray? = null

	private var maxX: Short = 0

	private var minY: Short = 0

	private var facesA: ShortArray? = null

	private var verticesY: IntArray? = null

	private var magnitudes: ByteArray? = null

	private var billboardAttributes: Array<BillboardSurfaceGL?>? = null

	private var facesMaterial: ShortArray? = null

	private var verticesSubmeshes: ShortArray? = null

	private var minZ: Short = 0

	private var billboardFaces: Array<GlBillboardFace?>? = null

	private var materialBatchOffsets: IntArray? = null

	private var billboardGroups: Array<IntArray?>? = null

	private var maxY: Short = 0

	private var size2d: Short = 0

	private var emissiveTriangles: Array<EmissiveTriangle?>? = null

	private var verticesZ: IntArray? = null

	private var contrast: Short = 0

	private var maxZ: Short = 0

	private var dynamicElementsBuffer: ElementsBufferGL? = null

	private var functionMask: Int = 0

	private var vertexNormalIndices: ShortArray? = null

	private var renderedFaceCount: Int = 0

	private var updateRequired: Boolean = true

	private var hasAlphaTransition: Boolean = false

	private var boundsCalculated: Boolean = false

	private var numNormals: Int = 0

	private var numFaces: Int = 0

	private var maxDepth: Int = 0

	private var numVertices: Int = 0

	private var textureAnimating: Boolean = false

	private val glToolkit: HdToolkit?

	private var vertexBuffer: OpenGlArrayDrawBuffer? = null

	private var textureCoordinatesBuffer: OpenGlArrayDrawBuffer? = null

	private var normalsBuffer: OpenGlArrayDrawBuffer? = null

	private var colorsBuffer: OpenGlArrayDrawBuffer? = null

	private var modelElementsBuffer: ModelElementsBufferGL? = null

	/** 634HD retained scene (HdSceneRenderer): this model's object there, or -1. */
	@JvmField
	var retainedObject: Int = -1

	/** Per material batch, whether the retained scene draws it (the rest draw here). */
	@JvmField
	var retainedBatches: BooleanArray? = null

	/** Material batches as built, kept for the retained scene (materialBatchOffsets may be released). */
	private var sceneBatchOffsets: IntArray? = null

	private var sceneBatchMaterials: IntArray? = null

	constructor(glToolkit: HdToolkit?) : super() {
		this.glToolkit = glToolkit
		this.vertexBuffer = OpenGlArrayDrawBuffer(null, 5126, 3, 0)
		this.textureCoordinatesBuffer = OpenGlArrayDrawBuffer(null, 5126, 2, 0)
		this.normalsBuffer = OpenGlArrayDrawBuffer(null, 5126, 3, 0)
		this.colorsBuffer = OpenGlArrayDrawBuffer(null, 5121, 4, 0)
		this.modelElementsBuffer = ModelElementsBufferGL()
	}

	constructor(glToolkit: HdToolkit?, mesh: Mesh?, functionMask: Int, ambient: Int, contrast: Int, preferenceFlags: Int) : super() {
		this.preferenceFlags = preferenceFlags
		this.functionMask = functionMask
		this.glToolkit = glToolkit
		if (HdModel.hasVertices(functionMask, preferenceFlags)) {
			this.vertexBuffer = OpenGlArrayDrawBuffer(null, 5126, 3, 0)
		}
		if (ModelTexCoordFlag.hasTextureCoordinates(preferenceFlags, functionMask)) {
			this.textureCoordinatesBuffer = OpenGlArrayDrawBuffer(null, 5126, 2, 0)
		}
		if (HdModel.hasNormals(preferenceFlags, functionMask)) {
			this.normalsBuffer = OpenGlArrayDrawBuffer(null, 5126, 3, 0)
		}
		if (HdModel.hasColors(preferenceFlags, functionMask)) {
			this.colorsBuffer = OpenGlArrayDrawBuffer(null, 5121, 4, 0)
		}
		if (HdModel.hasElementsBuffer(functionMask, preferenceFlags)) {
			this.modelElementsBuffer = ModelElementsBufferGL()
		}
		val textureSource: MaterialProvider? = glToolkit!!.materialProvider
		this.triangles = IntArray(mesh!!.maxDepth + 1)
		val faces: IntArray = IntArray(mesh.numFaces)
		for (i in 0 until mesh.numFaces) {
			if (mesh.faceRenderType == null || mesh.faceRenderType!![i].toInt() != 2) {
				if (mesh.facesMaterial != null && mesh.facesMaterial!![i].toInt() != -1) {
					val textureMetrics: MaterialType? = textureSource!!.getMetrics(mesh.facesMaterial!![i].toInt() and 0xFFFF)
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
		val sortKeys: LongArray = LongArray(this.numFaces)
		val modifiesAlpha: Boolean = (this.functionMask and 0x100) != 0
		var local248: Int
		var local261: Int
		var local436: Int
		for (j in 0 until this.numFaces) {
			val face: Int = faces[j]
			var textureMetrics2: MaterialType? = null
			var sortKeyHigh: Int = 0
			val renderFlags: Byte = 0
			var effectType: Byte = 0
			var effectParam1: Byte = 0
			if (mesh.billboards != null) {
				var hasUid: Boolean = false
				for (billboardIndex in 0 until mesh.billboards!!.size) {
					val meshBillboard: MeshBillboard? = mesh.billboards!![billboardIndex]
					if (face == meshBillboard!!.appliesOnFace) {
						val lightType: BillboardType = BillboardTypeList.list(meshBillboard!!.billboardId)
						if (lightType.hasUid) {
							hasUid = true
						}
						if (lightType.materialId != -1) {
							val textureMetrics3: MaterialType? = textureSource!!.getMetrics(lightType.materialId)
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
			var materialId: Short = -1
			if (mesh.facesMaterial != null) {
				materialId = mesh.facesMaterial!![face]
				if (materialId.toInt() != -1) {
					textureMetrics2 = textureSource!!.getMetrics(materialId.toInt() and 0xFFFF)
					if ((this.preferenceFlags and 0x40) != 0 && textureMetrics2!!.detailsOnly) {
						materialId = -1
						textureMetrics2 = null
					} else {
						effectParam1 = textureMetrics2!!.effectParam1
						if (textureMetrics2.speedU.toInt() != 0 || textureMetrics2.speedV.toInt() != 0) {
							this.textureAnimating = true
						}
						effectType = textureMetrics2.effectType
					}
				}
			}
			val alphaTransition: Boolean = mesh.facesAlpha != null && mesh.facesAlpha!![face].toInt() != 0 || textureMetrics2 != null && textureMetrics2.effectCombiner != 0
			if ((modifiesAlpha || alphaTransition) && mesh.facesLayer != null) {
				sortKeyHigh += mesh.facesLayer!![face].toInt() shl 17
			}
			if (alphaTransition) {
				sortKeyHigh += 65536
			}
			sortKeyHigh += (effectType.toInt() and 0xFF) shl 8
			sortKeyHigh += effectParam1.toInt() and 0xFF
			val materialKey: Int = renderFlags + ((materialId.toInt() and 0xFFFF) shl 16)
			val sortKeyLow: Int = materialKey + (j and 0xFFFF)
			sortKeys[j] = (sortKeyHigh.toLong() shl 32) + sortKeyLow.toLong()
			this.textureAnimating = this.textureAnimating or (textureMetrics2 != null && (textureMetrics2.speedU.toInt() != 0 || textureMetrics2.speedV.toInt() != 0))
			this.hasAlphaTransition = this.hasAlphaTransition or alphaTransition
		}
		ArrayUtils.quicksort(faces, sortKeys)
		this.verticesZ = mesh.verticesZ
		this.verticesY = mesh.verticesY
		this.numVertices = mesh.numVertices
		this.verticesX = mesh.verticesX
		this.verticesSubmeshes = mesh.verticesSubmeshes
		this.maxDepth = mesh.maxDepth
		this.effectiveVertices = mesh.particleEffectors
		val vertexNormals: Array<VertexNormalGL?> = arrayOfNulls<VertexNormalGL>(this.maxDepth)
		this.emissiveTriangles = mesh.particleEmitters
		var local543: Int
		var local557: Int
		var local603: Int
		if (mesh.billboards != null) {
			this.billboardCount = mesh.billboards!!.size
			this.billboardAttributes = arrayOfNulls<BillboardSurfaceGL>(this.billboardCount)
			this.billboardFaces = arrayOfNulls<GlBillboardFace>(this.billboardCount)
			for (billboardIndex in 0 until this.billboardCount) {
				val meshBillboard2: MeshBillboard? = mesh.billboards!![billboardIndex]
				val lightType2: BillboardType = BillboardTypeList.list(meshBillboard2!!.billboardId)
				var sortedFaceIndex: Int = -1
				for (k in 0 until this.numFaces) {
					if (faces[k] == meshBillboard2!!.appliesOnFace) {
						sortedFaceIndex = k
						break
					}
				}
				if (sortedFaceIndex == -1) {
					throw RuntimeException()
				}
				val faceRgb: Int = ColorUtil.hslToRgb!![mesh.facesColor!![meshBillboard2!!.appliesOnFace].toInt() and 0xFFFF] and 0xFFFFFF
				val diffuseColor: Int = faceRgb or ((255 - (if (mesh.facesAlpha == null) 0 else mesh.facesAlpha!![meshBillboard2!!.appliesOnFace].toInt())) shl 24)
				this.billboardFaces!![billboardIndex] = GlBillboardFace(sortedFaceIndex, mesh.facesA!![meshBillboard2!!.appliesOnFace].toInt(), mesh.facesB!![meshBillboard2!!.appliesOnFace].toInt(), mesh.facesC!![meshBillboard2!!.appliesOnFace].toInt(), lightType2.size2d, lightType2.size3d, lightType2.materialId, lightType2.blendType, lightType2.shape, lightType2.hasUid, lightType2.stationary, meshBillboard2!!.distance)
				this.billboardAttributes!![billboardIndex] = BillboardSurfaceGL(diffuseColor)
			}
		}
		val faceVertexCount: Int = this.numFaces * 3
		this.vertexNormalIndices = ShortArray(faceVertexCount)
		HdModel.normalsUid = LongArray(faceVertexCount)
		this.facesMaterial = ShortArray(this.numFaces)
		this.textureV = FloatArray(faceVertexCount)
		this.normalsX = ShortArray(faceVertexCount)
		this.normalsZ = ShortArray(faceVertexCount)
		this.magnitudes = ByteArray(faceVertexCount)
		this.normalsY = ShortArray(faceVertexCount)
		if (mesh.facesSubmeshes != null) {
			this.facesSubmeshes = ShortArray(this.numFaces)
		}
		this.ambient = ambient.toShort()
		this.facesColor = ShortArray(this.numFaces)
		this.facesB = ShortArray(this.numFaces)
		this.facesA = ShortArray(this.numFaces)
		this.contrast = contrast.toShort()
		this.facesAlpha = ByteArray(this.numFaces)
		this.textureU = FloatArray(faceVertexCount)
		this.facesC = ShortArray(this.numFaces)
		var triangleOffset: Int = 0
		for (vertexIndex in 0 until mesh.maxDepth) {
			val vertexFaceCount: Int = this.triangles!![vertexIndex]
			this.triangles!![vertexIndex] = triangleOffset
			vertexNormals[vertexIndex] = VertexNormalGL()
			triangleOffset += vertexFaceCount
		}
		this.triangles!![mesh.maxDepth] = triangleOffset
		val textureMatrix: TextureTransformationMatrix? = TextureTransformationMatrix.generateTextureMatrix(this.numFaces, mesh, faces)
		val faceNormals: Array<FaceNormalGL?> = arrayOfNulls<FaceNormalGL>(mesh.numFaces)
		var local847: Int
		var local858: Int
		var local880: Int
		var local891: Int
		var local900: Int
		var local909: Int
		var local826: Short
		var local837: Int
		for (meshFaceIndex in 0 until mesh.numFaces) {
			val faceA: Short = mesh.facesA!![meshFaceIndex]
			val faceB: Short = mesh.facesB!![meshFaceIndex]
			val faceC: Short = mesh.facesC!![meshFaceIndex]
			val deltaX1: Int = this.verticesX!![faceB.toInt()] - this.verticesX!![faceA.toInt()]
			val deltaY1: Int = this.verticesY!![faceB.toInt()] - this.verticesY!![faceA.toInt()]
			val deltaZ1: Int = this.verticesZ!![faceB.toInt()] - this.verticesZ!![faceA.toInt()]
			val deltaX2: Int = this.verticesX!![faceC.toInt()] - this.verticesX!![faceA.toInt()]
			val deltaY2: Int = this.verticesY!![faceC.toInt()] - this.verticesY!![faceA.toInt()]
			val deltaZ2: Int = this.verticesZ!![faceC.toInt()] - this.verticesZ!![faceA.toInt()]
			var normalX: Int = deltaY1 * deltaZ2 - deltaY2 * deltaZ1
			var normalY: Int = deltaX2 * deltaZ1 - deltaZ2 * deltaX1
			var normalZ: Int = deltaY2 * deltaX1 - deltaY1 * deltaX2
			while (normalX > 8192 || normalY > 8192 || normalZ > 8192 || normalX < -8192 || normalY < -8192 || normalZ < -8192) {
				normalX = normalX shr 0x1
				normalZ = normalZ shr 0x1
				normalY = normalY shr 0x1
			}
			var magnitude: Int = kotlin.math.sqrt((normalZ * normalZ + normalY * normalY + normalX * normalX).toDouble()).toInt()
			if (magnitude <= 0) {
				magnitude = 1
			}
			normalZ = normalZ * 256 / magnitude
			normalX = normalX * 256 / magnitude
			normalY = normalY * 256 / magnitude
			val renderType: Byte = if (mesh.faceRenderType == null) 0 else mesh.faceRenderType!![meshFaceIndex]
			if (renderType.toInt() == 0) {
				val vertexNormalA: VertexNormalGL = vertexNormals[faceA.toInt()]!!
				vertexNormalA.y += normalY
				vertexNormalA.z += normalZ
				vertexNormalA.magnitude++
				vertexNormalA.x += normalX
				val vertexNormalB: VertexNormalGL = vertexNormals[faceB.toInt()]!!
				vertexNormalB.x += normalX
				vertexNormalB.y += normalY
				vertexNormalB.magnitude++
				vertexNormalB.z += normalZ
				val vertexNormalC: VertexNormalGL = vertexNormals[faceC.toInt()]!!
				vertexNormalC.magnitude++
				vertexNormalC.z += normalZ
				vertexNormalC.y += normalY
				vertexNormalC.x += normalX
			} else if (renderType.toInt() == 1) {
				val faceNormal: FaceNormalGL = FaceNormalGL()
				faceNormals[meshFaceIndex] = faceNormal
				faceNormal.z = normalZ
				faceNormal.y = normalY
				faceNormal.x = normalX
			}
		}
		var local1125: Int
		var faceMaterialId: Short
		for (faceIndex in 0 until this.numFaces) {
			val meshFace: Int = faces[faceIndex]
			val color: Int = mesh.facesColor!![meshFace].toInt() and 0xFFFF
			var faceTexture: Int
			if (mesh.facesTexture == null) {
				faceTexture = -1
			} else {
				faceTexture = mesh.facesTexture!![meshFace].toInt()
			}
			var faceAlpha: Int
			if (mesh.facesAlpha == null) {
				faceAlpha = 0
			} else {
				faceAlpha = mesh.facesAlpha!![meshFace].toInt() and 0xFF
			}
			faceMaterialId = if (mesh.facesMaterial == null) (-1).toShort() else mesh.facesMaterial!![meshFace]
			if (faceMaterialId.toInt() != -1 && (this.preferenceFlags and 0x40) != 0) {
				val textureMetrics4: MaterialType? = textureSource!!.getMetrics(faceMaterialId.toInt() and 0xFFFF)
				if (textureMetrics4!!.detailsOnly) {
					faceMaterialId = -1
				}
			}
			var aU: Float = 0.0f
			var aV: Float = 0.0f
			var bU: Float = 0.0f
			var bV: Float = 0.0f
			var cU: Float = 0.0f
			var cV: Float = 0.0f
			var uidOffsetB: Byte = 0
			var uidOffsetC: Byte = 0
			var cubeFaceIndex: Int = 0
			var local1245: Byte
			var local1265: Short
			var local1270: Short
			var local1275: Short
			if (faceMaterialId.toInt() != -1) {
				if (faceTexture == -1) {
					bV = 1.0f
					uidOffsetC = 2
					cV = 0.0f
					uidOffsetB = 1
					aV = 1.0f
					aU = 0.0f
					cU = 0.0f
					bU = 1.0f
				} else {
					faceTexture = faceTexture and 0xFF
					val mappingType: Byte = mesh.texturesMapping!![faceTexture]
					var vertexA: Short
					var vertexB: Short
					var local1298: Float
					var local1306: Float
					var local1314: Float
					var local1401: Float
					var local1410: Float
					var local1419: Float
					var local1428: Float
					var local1437: Float
					var local1446: Float
					if (mappingType.toInt() == 0) {
						vertexA = mesh.facesA!![meshFace]
						vertexB = mesh.facesB!![meshFace]
						val vertexC: Short = mesh.facesC!![meshFace]
						val texVertexA: Short = mesh.textureFaceX!![faceTexture]
						val texVertexB: Short = mesh.textureFaceY!![faceTexture]
						val texVertexC: Short = mesh.textureFaceZ!![faceTexture]
						val texOriginX: Float = mesh.verticesX!![texVertexA.toInt()].toFloat()
						val texOriginY: Float = mesh.verticesY!![texVertexA.toInt()].toFloat()
						val texOriginZ: Float = mesh.verticesZ!![texVertexA.toInt()].toFloat()
						val texEdge1X: Float = mesh.verticesX!![texVertexB.toInt()].toFloat() - texOriginX
						val texEdge1Y: Float = mesh.verticesY!![texVertexB.toInt()].toFloat() - texOriginY
						val texEdge1Z: Float = mesh.verticesZ!![texVertexB.toInt()].toFloat() - texOriginZ
						val texEdge2X: Float = mesh.verticesX!![texVertexC.toInt()].toFloat() - texOriginX
						val texEdge2Y: Float = mesh.verticesY!![texVertexC.toInt()].toFloat() - texOriginY
						val texEdge2Z: Float = mesh.verticesZ!![texVertexC.toInt()].toFloat() - texOriginZ
						val vertexAOffX: Float = mesh.verticesX!![vertexA.toInt()].toFloat() - texOriginX
						val vertexAOffY: Float = mesh.verticesY!![vertexA.toInt()].toFloat() - texOriginY
						val vertexAOffZ: Float = mesh.verticesZ!![vertexA.toInt()].toFloat() - texOriginZ
						val vertexBOffX: Float = mesh.verticesX!![vertexB.toInt()].toFloat() - texOriginX
						val vertexBOffY: Float = mesh.verticesY!![vertexB.toInt()].toFloat() - texOriginY
						val vertexBOffZ: Float = mesh.verticesZ!![vertexB.toInt()].toFloat() - texOriginZ
						val vertexCOffX: Float = mesh.verticesX!![vertexC.toInt()].toFloat() - texOriginX
						val vertexCOffY: Float = mesh.verticesY!![vertexC.toInt()].toFloat() - texOriginY
						val vertexCOffZ: Float = mesh.verticesZ!![vertexC.toInt()].toFloat() - texOriginZ
						val texCrossX: Float = texEdge1Y * texEdge2Z - texEdge1Z * texEdge2Y
						val texCrossY: Float = texEdge2X * texEdge1Z - texEdge2Z * texEdge1X
						val texCrossZ: Float = texEdge2Y * texEdge1X - texEdge2X * texEdge1Y
						val uBasisX: Float = texCrossZ * texEdge2Y - texEdge2Z * texCrossY
						val uBasisY: Float = texEdge2Z * texCrossX - texEdge2X * texCrossZ
						val uBasisZ: Float = texCrossY * texEdge2X - texEdge2Y * texCrossX
						val uInvDeterminant: Float = 1.0f / (texEdge1Z * uBasisZ + texEdge1Y * uBasisY + uBasisX * texEdge1X)
						cU = uInvDeterminant * (vertexCOffX * uBasisX + uBasisY * vertexCOffY + vertexCOffZ * uBasisZ)
						aU = uInvDeterminant * (uBasisX * vertexAOffX + uBasisY * vertexAOffY + uBasisZ * vertexAOffZ)
						bU = uInvDeterminant * (vertexBOffZ * uBasisZ + vertexBOffX * uBasisX + uBasisY * vertexBOffY)
						val vBasisZ: Float = texEdge1X * texCrossY - texCrossX * texEdge1Y
						val vBasisX: Float = texEdge1Y * texCrossZ - texCrossY * texEdge1Z
						val vBasisY: Float = texEdge1Z * texCrossX - texCrossZ * texEdge1X
						val vInvDeterminant: Float = 1.0f / (texEdge2Z * vBasisZ + texEdge2X * vBasisX + vBasisY * texEdge2Y)
						bV = (vertexBOffX * vBasisX + vBasisY * vertexBOffY + vBasisZ * vertexBOffZ) * vInvDeterminant
						aV = (vBasisZ * vertexAOffZ + vBasisY * vertexAOffY + vertexAOffX * vBasisX) * vInvDeterminant
						cV = (vBasisY * vertexCOffY + vBasisX * vertexCOffX + vertexCOffZ * vBasisZ) * vInvDeterminant
					} else {
						vertexA = mesh.facesA!![meshFace]
						vertexB = mesh.facesB!![meshFace]
						val vertexC: Short = mesh.facesC!![meshFace]
						val centerX: Int = textureMatrix!!.centerX!![faceTexture]
						val centerY: Int = textureMatrix!!.centerY!![faceTexture]
						val centerZ: Int = textureMatrix!!.centerZ!![faceTexture]
						val matrix: FloatArray? = textureMatrix!!.matrix!![faceTexture]
						val direction: Byte = mesh.texturesDirection!![faceTexture]
						val speedOffset: Float = mesh.texturesSpeed!![faceTexture].toFloat() / 256.0f
						if (mappingType.toInt() == 1) {
							val uScale: Float = mesh.texturesScaleZ!![faceTexture].toFloat() / 1024.0f
							Model.mapCylindricalUv(centerZ, mesh.verticesZ!![vertexA.toInt()], direction.toInt(), mesh.verticesX!![vertexA.toInt()], HdModel.textureCoordsOut, mesh.verticesY!![vertexA.toInt()], speedOffset, centerY, centerX, uScale, matrix)
							aU = HdModel.textureCoordsOut[0]
							aV = HdModel.textureCoordsOut[1]
							Model.mapCylindricalUv(centerZ, mesh.verticesZ!![vertexB.toInt()], direction.toInt(), mesh.verticesX!![vertexB.toInt()], HdModel.textureCoordsOut, mesh.verticesY!![vertexB.toInt()], speedOffset, centerY, centerX, uScale, matrix)
							bU = HdModel.textureCoordsOut[0]
							bV = HdModel.textureCoordsOut[1]
							Model.mapCylindricalUv(centerZ, mesh.verticesZ!![vertexC.toInt()], direction.toInt(), mesh.verticesX!![vertexC.toInt()], HdModel.textureCoordsOut, mesh.verticesY!![vertexC.toInt()], speedOffset, centerY, centerX, uScale, matrix)
							cU = HdModel.textureCoordsOut[0]
							cV = HdModel.textureCoordsOut[1]
							val halfUScale: Float = uScale / 2.0f
							if ((direction.toInt() and 0x1) == 0) {
								if (halfUScale < cU - aU) {
									cU -= uScale
									uidOffsetC = 1
								} else if (halfUScale < aU - cU) {
									cU += uScale
									uidOffsetC = 2
								}
								if (bU - aU > halfUScale) {
									uidOffsetB = 1
									bU -= uScale
								} else if (aU - bU > halfUScale) {
									uidOffsetB = 2
									bU += uScale
								}
							} else {
								if (bV - aV > halfUScale) {
									bV -= uScale
									uidOffsetB = 1
								} else if (aV - bV > halfUScale) {
									uidOffsetB = 2
									bV += uScale
								}
								if (halfUScale < cV - aV) {
									uidOffsetC = 1
									cV -= uScale
								} else if (aV - cV > halfUScale) {
									uidOffsetC = 2
									cV += uScale
								}
							}
						} else if (mappingType.toInt() == 2) {
							val uTrans: Float = mesh.texturesUtrans!![faceTexture].toFloat() / 256.0f
							val vTrans: Float = mesh.texturesVtrans!![faceTexture].toFloat() / 256.0f
							val edge1X: Int = mesh.verticesX!![vertexB.toInt()] - mesh.verticesX!![vertexA.toInt()]
							val edge1Y: Int = mesh.verticesY!![vertexB.toInt()] - mesh.verticesY!![vertexA.toInt()]
							val edge1Z: Int = mesh.verticesZ!![vertexB.toInt()] - mesh.verticesZ!![vertexA.toInt()]
							val edge2X: Int = mesh.verticesX!![vertexC.toInt()] - mesh.verticesX!![vertexA.toInt()]
							val edge2Y: Int = mesh.verticesY!![vertexC.toInt()] - mesh.verticesY!![vertexA.toInt()]
							val edge2Z: Int = mesh.verticesZ!![vertexC.toInt()] - mesh.verticesZ!![vertexA.toInt()]
							val crossX: Int = edge1Y * edge2Z - edge1Z * edge2Y
							val crossY: Int = edge2X * edge1Z - edge2Z * edge1X
							val crossZ: Int = edge2Y * edge1X - edge2X * edge1Y
							val inverseScaleX: Float = 64.0f / mesh.texturesScaleX!![faceTexture].toFloat()
							val inverseScaleY: Float = 64.0f / mesh.texturesScaleY!![faceTexture].toFloat()
							val inverseScaleZ: Float = 64.0f / mesh.texturesScaleZ!![faceTexture].toFloat()
							val scaledNormalX: Float = (matrix!![2] * crossZ.toFloat() + crossY.toFloat() * matrix[1] + crossX.toFloat() * matrix[0]) / inverseScaleX
							val scaledNormalY: Float = (matrix[5] * crossZ.toFloat() + crossY.toFloat() * matrix[4] + matrix[3] * crossX.toFloat()) / inverseScaleY
							val scaledNormalZ: Float = (crossZ.toFloat() * matrix[8] + matrix[7] * crossY.toFloat() + crossX.toFloat() * matrix[6]) / inverseScaleZ
							cubeFaceIndex = Model.getCubeFace(scaledNormalY, scaledNormalZ, scaledNormalX)
							Model.mapCubeUv(vTrans, speedOffset, matrix, mesh.verticesZ!![vertexA.toInt()], centerZ, direction.toInt(), centerX, mesh.verticesX!![vertexA.toInt()], mesh.verticesY!![vertexA.toInt()], uTrans, HdModel.textureCoordsOut, centerY, cubeFaceIndex)
							aV = HdModel.textureCoordsOut[1]
							aU = HdModel.textureCoordsOut[0]
							Model.mapCubeUv(vTrans, speedOffset, matrix, mesh.verticesZ!![vertexB.toInt()], centerZ, direction.toInt(), centerX, mesh.verticesX!![vertexB.toInt()], mesh.verticesY!![vertexB.toInt()], uTrans, HdModel.textureCoordsOut, centerY, cubeFaceIndex)
							bV = HdModel.textureCoordsOut[1]
							bU = HdModel.textureCoordsOut[0]
							Model.mapCubeUv(vTrans, speedOffset, matrix, mesh.verticesZ!![vertexC.toInt()], centerZ, direction.toInt(), centerX, mesh.verticesX!![vertexC.toInt()], mesh.verticesY!![vertexC.toInt()], uTrans, HdModel.textureCoordsOut, centerY, cubeFaceIndex)
							cU = HdModel.textureCoordsOut[0]
							cV = HdModel.textureCoordsOut[1]
						} else if (mappingType.toInt() == 3) {
							Model.mapSphericalUv(centerZ, direction.toInt(), speedOffset, mesh.verticesX!![vertexA.toInt()], HdModel.textureCoordsOut, mesh.verticesZ!![vertexA.toInt()], centerX, mesh.verticesY!![vertexA.toInt()], centerY, matrix)
							aV = HdModel.textureCoordsOut[1]
							aU = HdModel.textureCoordsOut[0]
							Model.mapSphericalUv(centerZ, direction.toInt(), speedOffset, mesh.verticesX!![vertexB.toInt()], HdModel.textureCoordsOut, mesh.verticesZ!![vertexB.toInt()], centerX, mesh.verticesY!![vertexB.toInt()], centerY, matrix)
							bU = HdModel.textureCoordsOut[0]
							bV = HdModel.textureCoordsOut[1]
							Model.mapSphericalUv(centerZ, direction.toInt(), speedOffset, mesh.verticesX!![vertexC.toInt()], HdModel.textureCoordsOut, mesh.verticesZ!![vertexC.toInt()], centerX, mesh.verticesY!![vertexC.toInt()], centerY, matrix)
							cV = HdModel.textureCoordsOut[1]
							cU = HdModel.textureCoordsOut[0]
							if ((direction.toInt() and 0x1) == 0) {
								if (cU - aU > 0.5f) {
									uidOffsetC = 1
									cU--
								} else if (aU - cU > 0.5f) {
									uidOffsetC = 2
									cU++
								}
								if (bU - aU > 0.5f) {
									uidOffsetB = 1
									bU--
								} else if (aU - bU > 0.5f) {
									uidOffsetB = 2
									bU++
								}
							} else {
								if (bV - aV > 0.5f) {
									uidOffsetB = 1
									bV--
								} else if (aV - bV > 0.5f) {
									bV++
									uidOffsetB = 2
								}
								if (cV - aV > 0.5f) {
									cV--
									uidOffsetC = 1
								} else if (aV - cV > 0.5f) {
									uidOffsetC = 2
									cV++
								}
							}
						}
					}
				}
			}
			var faceRenderType: Byte
			if (mesh.faceRenderType == null) {
				faceRenderType = 0
			} else {
				faceRenderType = mesh.faceRenderType!![meshFace]
			}
			if (faceRenderType.toInt() == 0) {
				val uid: Long = (((color shl 8).toLong() + (cubeFaceIndex shl 24).toLong() + faceAlpha.toLong()) shl 32) + (faceTexture shl 2).toLong()
				val vertexA: Short = mesh.facesA!![meshFace]
				val vertexB: Short = mesh.facesB!![meshFace]
				val vertexC: Short = mesh.facesC!![meshFace]
				val vertexNormalA: VertexNormalGL = vertexNormals[vertexA.toInt()]!!
				this.facesA!![faceIndex] = this.connectNormal(vertexNormalA.y, vertexNormalA.z, uid, vertexNormalA.x, mesh, vertexNormalA.magnitude, aU, vertexA.toInt(), aV)
				val vertexNormalB: VertexNormalGL = vertexNormals[vertexB.toInt()]!!
				this.facesB!![faceIndex] = this.connectNormal(vertexNormalB.y, vertexNormalB.z, uidOffsetB.toLong() + uid, vertexNormalB.x, mesh, vertexNormalB.magnitude, bU, vertexB.toInt(), bV)
				val vertexNormalC: VertexNormalGL = vertexNormals[vertexC.toInt()]!!
				this.facesC!![faceIndex] = this.connectNormal(vertexNormalC.y, vertexNormalC.z, uid + uidOffsetC.toLong(), vertexNormalC.x, mesh, vertexNormalC.magnitude, cU, vertexC.toInt(), cV)
			} else if (faceRenderType.toInt() == 1) {
				val faceNormal: FaceNormalGL = faceNormals[meshFace]!!
				val uid: Long = (((color shl 8).toLong() + (cubeFaceIndex shl 24).toLong() + faceAlpha.toLong()) shl 32) + ((if (faceNormal.x > 0) 1024 else 2048) + (faceTexture shl 2) + ((faceNormal.y + 256) shl 12) + ((faceNormal.z + 256) shl 22)).toLong()
				this.facesA!![faceIndex] = this.connectNormal(faceNormal.y, faceNormal.z, uid, faceNormal.x, mesh, 0, aU, mesh.facesA!![meshFace].toInt(), aV)
				this.facesB!![faceIndex] = this.connectNormal(faceNormal.y, faceNormal.z, uid + uidOffsetB.toLong(), faceNormal.x, mesh, 0, bU, mesh.facesB!![meshFace].toInt(), bV)
				this.facesC!![faceIndex] = this.connectNormal(faceNormal.y, faceNormal.z, uid + uidOffsetC.toLong(), faceNormal.x, mesh, 0, cU, mesh.facesC!![meshFace].toInt(), cV)
			}
			if (mesh.facesAlpha != null) {
				this.facesAlpha!![faceIndex] = mesh.facesAlpha!![meshFace]
			}
			if (mesh.facesSubmeshes != null) {
				this.facesSubmeshes!![faceIndex] = mesh.facesSubmeshes!![meshFace]
			}
			this.facesColor!![faceIndex] = mesh.facesColor!![meshFace]
			this.facesMaterial!![faceIndex] = faceMaterialId
		}
		var batchCount: Int = 0
		var previousMaterial: Short = -10000
		for (faceIndex in 0 until this.renderedFaceCount) {
			val material: Short = this.facesMaterial!![faceIndex]
			if (previousMaterial != material) {
				batchCount++
				previousMaterial = material
			}
		}
		this.materialBatchOffsets = IntArray(batchCount + 1)
		batchCount = 0
		previousMaterial = -10000
		for (faceIndex in 0 until this.renderedFaceCount) {
			faceMaterialId = this.facesMaterial!![faceIndex]
			if (previousMaterial != faceMaterialId) {
				this.materialBatchOffsets!![batchCount++] = faceIndex
				previousMaterial = faceMaterialId
			}
		}
		this.materialBatchOffsets!![batchCount] = this.renderedFaceCount
		this.sceneBatchOffsets = this.materialBatchOffsets
		val sceneMaterials: IntArray = IntArray(batchCount)
		for (i in 0 until batchCount) {
			var material: Int = this.facesMaterial!![this.materialBatchOffsets!![i]].toInt() and 0xFFFF
			if (material == 65535) {
				material = -1
			}
			sceneMaterials[i] = material
		}
		this.sceneBatchMaterials = sceneMaterials
		HdModel.normalsUid = null
		this.normalsX = ArrayUtils.method9229(this.normalsX, this.numNormals)
		this.normalsY = ArrayUtils.method9229(this.normalsY, this.numNormals)
		this.normalsZ = ArrayUtils.method9229(this.normalsZ, this.numNormals)
		this.magnitudes = Arrays.copyOf(this.numNormals, this.magnitudes)
		this.textureU = ArrayUtils.method7862(this.numNormals, this.textureU)
		this.textureV = ArrayUtils.method7862(this.numNormals, this.textureV)
		if (mesh.vertexGroups != null && HdModel.altersVertices(this.preferenceFlags, functionMask)) {
			this.vertexGroups = mesh.createVertexGroups(false)
		}
		if (mesh.billboards != null && HdModel.altersBillboards(functionMask, this.preferenceFlags)) {
			this.billboardGroups = mesh.createBillboardGroups()
		}
		if (mesh.faceGroups != null && HdModel.altersFaces(functionMask, this.preferenceFlags)) {
			var maxFaceGroup: Int = 0
			val groupCounts: IntArray = IntArray(256)
			for (faceIndex in 0 until this.numFaces) {
				val faceGroup: Int = mesh.faceGroups!![faces[faceIndex]]
				if (faceGroup >= 0) {
					if (faceGroup > maxFaceGroup) {
						maxFaceGroup = faceGroup
					}
					val previousCount: Int = groupCounts[faceGroup]++
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

	override fun FA(angle: Int) {
		val sin: Int = TextureCombineSource.SINE[angle]
		val cos: Int = TextureCombineSource.COSINE[angle]
		for (i in 0 until this.maxDepth) {
			val rotatedY: Int = (this.verticesY!![i] * cos - sin * this.verticesZ!![i]) shr 14
			this.verticesZ!![i] = (this.verticesY!![i] * sin + this.verticesZ!![i] * cos) shr 14
			this.verticesY!![i] = rotatedY
		}
		if (this.vertexBuffer != null) {
			this.vertexBuffer!!.buffer = null
		}
		this.boundsCalculated = false
	}

	override fun copy(copyType: Byte, functionMask: Int, ensureLit: Boolean): Model? {
		val glModel: HdModel?
		val glModel2: HdModel?
		if (copyType.toInt() == 1) {
			glModel = this.glToolkit!!.copyTarget1
			glModel2 = this.glToolkit.copyBuffer1
		} else if (copyType.toInt() == 2) {
			glModel2 = this.glToolkit!!.copyBuffer2
			glModel = this.glToolkit.copyTarget2
		} else if (copyType.toInt() == 3) {
			glModel = this.glToolkit!!.copyTarget3
			glModel2 = this.glToolkit.copyBuffer3
		} else if (copyType.toInt() == 4) {
			glModel = this.glToolkit!!.copyTarget4
			glModel2 = this.glToolkit.copyBuffer4
		} else if (copyType.toInt() == 5) {
			glModel = this.glToolkit!!.copyTarget5
			glModel2 = this.glToolkit.copyBuffer5
		} else {
			glModel2 = HdModel(this.glToolkit)
			glModel = glModel2
		}
		return this.copyModelData(glModel!!, functionMask, ensureLit, glModel2, copyType.toInt() != 0)
	}

	private fun drawOpaqueFaces() {
		if (this.renderedFaceCount == 0) {
			return
		}
		if (this.renderState.toInt() != 0) {
			this.upload(true)
		}
		this.upload(false)
		if (this.modelElementsBuffer != null) {
			if (this.modelElementsBuffer!!.openglElementsBuffer == null) {
				this.buildElementsBuffer((this.renderState.toInt() and 0x10) != 0)
			}
			if (this.modelElementsBuffer!!.openglElementsBuffer != null) {
				// 634HD: stream ranges from an earlier frame are written again (HdDynamicStream)
				(this.dynamicArrayBuffer as? HdDynamicStream.Range)?.ensureCurrent()
				(this.modelElementsBuffer!!.openglElementsBuffer as? HdDynamicStream.Range)?.ensureCurrent()
				this.glToolkit!!.setLightingEnabled(this.normalsBuffer != null)
				this.glToolkit.toggleDrawingClientstates(this.vertexBuffer, this.colorsBuffer, this.textureCoordinatesBuffer, this.normalsBuffer)
				val batchCount: Int = this.materialBatchOffsets!!.size - 1
				val retained: BooleanArray? = if (this.retainedObject >= 0 && this.glToolkit.sceneRetained()) this.retainedBatches else null
				for (i in 0 until batchCount) {
					if (retained != null && retained[i]) {
						continue
					}
					val start: Int = this.materialBatchOffsets!![i]
					val end: Int = this.materialBatchOffsets!![i + 1]
					var materialId: Int = this.facesMaterial!![start].toInt() and 0xFFFF
					if (materialId == 65535) {
						materialId = -1
					}
					this.glToolkit.renderTexture(this.normalsBuffer != null, materialId)
					this.glToolkit.drawElements((end - start) * 3, this.modelElementsBuffer!!.openglElementsBuffer, start * 3)
				}
			}
		}
		this.releaseUploadedData()
	}

	private fun buildElementsBuffer(reuseBuffer: Boolean) {
		if (this.renderedFaceCount * 6 > this.glToolkit!!.textureBuffer.buffer!!.size) {
			this.glToolkit.textureBuffer = GlBitBuffer((this.renderedFaceCount + 100) * 6)
		} else {
			this.glToolkit.textureBuffer.currentPosition = 0
		}
		val gpuPacket: GlBitBuffer = this.glToolkit.textureBuffer
		var face: Int
		if (this.glToolkit.bigEndianess) {
			face = 0
			while (face < this.renderedFaceCount) {
				gpuPacket.writeShort(this.facesA!![face].toInt())
				gpuPacket.writeShort(this.facesB!![face].toInt())
				gpuPacket.writeShort(this.facesC!![face].toInt())
				face++
			}
		} else {
			face = 0
			while (face < this.renderedFaceCount) {
				gpuPacket.writeShortLE(this.facesA!![face].toInt())
				gpuPacket.writeShortLE(this.facesB!![face].toInt())
				gpuPacket.writeShortLE(this.facesC!![face].toInt())
				face++
			}
		}
		if (gpuPacket.currentPosition == 0) {
			return
		}
		if (reuseBuffer) {
			// 634HD: a range of the frame's stream buffer (scene/HdDynamicStream)
			if (this.dynamicElementsBuffer == null) {
				this.dynamicElementsBuffer = this.glToolkit.elementStream.ElementRange()
			}
			this.dynamicElementsBuffer!!.putElements(gpuPacket.buffer, gpuPacket.currentPosition)
			this.modelElementsBuffer!!.openglElementsBuffer = this.dynamicElementsBuffer
		} else {
			this.modelElementsBuffer!!.openglElementsBuffer = this.glToolkit.createElementsArrayBuffer(gpuPacket.currentPosition, gpuPacket.buffer, false)
		}
		if (!reuseBuffer) {
			this.updateRequired = true
		}
	}

	override fun picked(x: Int, y: Int, matrix: Matrix?, quick: Boolean, sizeShift: Int): Boolean {
		return this.pick(quick, -1, y, sizeShift, x, matrix)
	}

	private fun isHovering(mouseY: Int, xB: Int, xC: Int, yB: Int, xA: Int, yA: Int, mouseX: Int, yC: Int): Boolean {
		if (yA > mouseY && mouseY < yB && mouseY < yC) {
			return false
		} else if (mouseY > yA && yB < mouseY && yC < mouseY) {
			return false
		} else if (xA > mouseX && xB > mouseX && mouseX < xC) {
			return false
		} else {
			return xA >= mouseX || mouseX <= xB || mouseX <= xC
		}
	}

	override fun VA(angle: Int) {
		val sin: Int = TextureCombineSource.SINE[angle]
		val cos: Int = TextureCombineSource.COSINE[angle]
		for (i in 0 until this.maxDepth) {
			val rotatedX: Int = (this.verticesX!![i] * cos + sin * this.verticesY!![i]) shr 14
			this.verticesY!![i] = (this.verticesY!![i] * cos - sin * this.verticesX!![i]) shr 14
			this.verticesX!![i] = rotatedX
		}
		if (this.vertexBuffer != null) {
			this.vertexBuffer!!.buffer = null
		}
		this.boundsCalculated = false
	}

	private fun hslToRgb(ambient: Int, alpha: Byte, materialId: Short, color: Int): Int {
		var rgb: Int = ColorUtil.hslToRgb!![HdModel.adjustLuminance(color, ambient)]
		if (materialId.toInt() != -1) {
			val textureMetrics: MaterialType? = this.glToolkit!!.materialProvider!!.getMetrics(materialId.toInt() and 0xFFFF)
			val materialAlpha: Int = textureMetrics!!.alpha.toInt() and 0xFF
			var local46: Int
			var local68: Int
			if (materialAlpha != 0) {
				var ambientColour: Int
				if (ambient < 0) {
					ambientColour = 0
				} else if (ambient > 127) {
					ambientColour = 16777215
				} else {
					ambientColour = ambient * 131586
				}
				if (materialAlpha == 256) {
					rgb = ambientColour
				} else {
					val inverseAlpha: Int = 256 - materialAlpha
					rgb = ((((ambientColour and 0xFF00FF) * materialAlpha + inverseAlpha * (rgb and 0xFF00FF)) and 0xFF00FF00.toInt()) + (((ambientColour and 0xFF00) * materialAlpha + (rgb and 0xFF00) * inverseAlpha) and 0xFF0000)) shr 8
				}
			}
			var brightness: Int = textureMetrics.brightness.toInt() and 0xFF
			if (brightness != 0) {
				brightness += 256
				var red: Int = ((rgb shr 16) and 0xFF) * brightness
				if (red > 65535) {
					red = 65535
				}
				var green: Int = brightness * ((rgb shr 8) and 0xFF)
				if (green > 65535) {
					green = 65535
				}
				var blue: Int = (rgb and 0xFF) * brightness
				if (blue > 65535) {
					blue = 65535
				}
				rgb = (blue shr 8) + (((red and 0xA400FF00.toInt()) shl 8) + (green and 0xFF00))
			}
		}
		return (rgb shl 8) + 255 - (alpha.toInt() and 0xFF)
	}

	override fun loadedTextures(): Boolean {
		if (this.facesMaterial == null) {
			return true
		}
		for (i in 0 until this.facesMaterial!!.size) {
			if (this.facesMaterial!![i].toInt() != -1 && !this.glToolkit!!.materialProvider!!.textureAvailable(this.facesMaterial!![i].toInt())) {
				return false
			}
		}
		return true
	}

	override fun shareLight(model: Model?, x: Int, y: Int, z: Int, renderTypeAdjustment: Boolean) {
		val glModel: HdModel? = model as HdModel?
		if (this.numFaces == 0 || glModel!!.numFaces == 0) {
			return
		}
		val otherVertexCount: Int = glModel.maxDepth
		val otherVerticesX: IntArray? = glModel.verticesX
		val otherVerticesY: IntArray? = glModel.verticesY
		val otherVerticesZ: IntArray? = glModel.verticesZ
		val otherNormalsX: ShortArray? = glModel.normalsX
		val otherNormalsY: ShortArray? = glModel.normalsY
		val otherNormalsZ: ShortArray? = glModel.normalsZ
		val otherMagnitudes: ByteArray? = glModel.magnitudes
		var mergedNormalsX: ShortArray?
		var mergedNormalsY: ShortArray?
		var mergedNormalsZ: ShortArray?
		var mergedMagnitudes: ByteArray?
		if (this.mergedNormals == null) {
			mergedNormalsZ = null
			mergedNormalsY = null
			mergedMagnitudes = null
			mergedNormalsX = null
		} else {
			mergedNormalsX = this.mergedNormals!!.normalsX
			mergedMagnitudes = this.mergedNormals!!.magnitude
			mergedNormalsY = this.mergedNormals!!.normalsY
			mergedNormalsZ = this.mergedNormals!!.normalsZ
		}
		var otherMergedNormalsX: ShortArray?
		var otherMergedNormalsY: ShortArray?
		var otherMergedNormalsZ: ShortArray?
		var otherMergedMagnitudes: ByteArray?
		if (glModel.mergedNormals == null) {
			otherMergedMagnitudes = null
			otherMergedNormalsY = null
			otherMergedNormalsX = null
			otherMergedNormalsZ = null
		} else {
			otherMergedNormalsZ = glModel.mergedNormals!!.normalsZ
			otherMergedMagnitudes = glModel.mergedNormals!!.magnitude
			otherMergedNormalsX = glModel.mergedNormals!!.normalsX
			otherMergedNormalsY = glModel.mergedNormals!!.normalsY
		}
		val otherTriangles: IntArray? = glModel.triangles
		val otherVertexNormalIndices: ShortArray? = glModel.vertexNormalIndices
		if (!glModel.boundsCalculated) {
			glModel.calculateBounds()
		}
		val otherMinY: Short = glModel.minY
		val otherMaxY: Short = glModel.maxY
		val otherMinX: Short = glModel.minX
		val otherMaxX: Short = glModel.maxX
		val otherMinZ: Short = glModel.minZ
		val otherMaxZ: Short = glModel.maxZ
		for (i in 0 until this.maxDepth) {
			val relY: Int = this.verticesY!![i] - y
			if (relY >= otherMinY && relY <= otherMaxY) {
				val relX: Int = this.verticesX!![i] - x
				if (relX >= otherMinX && relX <= otherMaxX) {
					val relZ: Int = this.verticesZ!![i] - z
					if (otherMinZ <= relZ && otherMaxZ >= relZ) {
						var normalIndex: Int = -1
						var start: Int = this.triangles!![i]
						var end: Int = this.triangles!![i + 1]
						for (j in start until end) {
							normalIndex = this.vertexNormalIndices!![j] - 1
							if (normalIndex == -1 || this.magnitudes!![normalIndex].toInt() != 0) {
								break
							}
						}
						if (normalIndex != -1) {
							for (k in 0 until otherVertexCount) {
								if (otherVerticesX!![k] == relX && relZ == otherVerticesZ!![k] && otherVerticesY!![k] == relY) {
									end = otherTriangles!![k + 1]
									var otherNormalIndex: Int = -1
									start = otherTriangles[k]
									for (m in start until end) {
										otherNormalIndex = otherVertexNormalIndices!![m] - 1
										if (otherNormalIndex == -1 || otherMagnitudes!![otherNormalIndex].toInt() != 0) {
											break
										}
									}
									if (otherNormalIndex != -1) {
										if (mergedNormalsX == null) {
											this.mergedNormals = ModelNormalsGL()
											mergedNormalsX = Arrays.copyOf(this.normalsX)
											this.mergedNormals!!.normalsX = mergedNormalsX
											mergedNormalsY = Arrays.copyOf(this.normalsY)
											this.mergedNormals!!.normalsY = mergedNormalsY
											mergedNormalsZ = Arrays.copyOf(this.normalsZ)
											this.mergedNormals!!.normalsZ = mergedNormalsZ
											mergedMagnitudes = Arrays.copyOf(this.magnitudes)
											this.mergedNormals!!.magnitude = mergedMagnitudes
										}
										if (otherMergedNormalsX == null) {
											val otherMergedNormals: ModelNormalsGL = ModelNormalsGL()
											glModel.mergedNormals = otherMergedNormals
											otherMergedNormalsX = Arrays.copyOf(otherNormalsX)
											otherMergedNormals.normalsX = otherMergedNormalsX
											otherMergedNormalsY = Arrays.copyOf(otherNormalsY)
											otherMergedNormals.normalsY = otherMergedNormalsY
											otherMergedNormalsZ = Arrays.copyOf(otherNormalsZ)
											otherMergedNormals.normalsZ = otherMergedNormalsZ
											otherMergedMagnitudes = Arrays.copyOf(otherMagnitudes)
											otherMergedNormals.magnitude = otherMergedMagnitudes
										}
										var normalX: Short = this.normalsX!![normalIndex]
										var normalY: Short = this.normalsY!![normalIndex]
										var normalZ: Short = this.normalsZ!![normalIndex]
										var magnitude: Byte = this.magnitudes!![normalIndex]
										start = otherTriangles[k]
										end = otherTriangles[k + 1]
										var local420: Int
										for (index in start until end) {
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
										normalY = otherNormalsY!![otherNormalIndex]
										normalZ = otherNormalsZ!![otherNormalIndex]
										magnitude = otherMagnitudes!![otherNormalIndex]
										start = this.triangles!![i]
										end = this.triangles!![i + 1]
										normalX = otherNormalsX!![otherNormalIndex]
										for (index in start until end) {
											val targetNormalIndex: Int = this.vertexNormalIndices!![index] - 1
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
										if (this.normalsBuffer == null && this.colorsBuffer != null) {
											this.colorsBuffer!!.buffer = null
										}
										if (this.normalsBuffer != null) {
											this.normalsBuffer!!.buffer = null
										}
										if (glModel.normalsBuffer == null && glModel.colorsBuffer != null) {
											glModel.colorsBuffer!!.buffer = null
										}
										if (glModel.normalsBuffer != null) {
											glModel.normalsBuffer!!.buffer = null
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

	override fun pickedOrtho(x: Int, y: Int, matrix: Matrix?, quick: Boolean, sizeShift: Int, zoom: Int): Boolean {
		return this.pick(quick, zoom, y, sizeShift, x, matrix)
	}

	private fun renderBillboards() {
		if (this.billboardFaces == null) {
			return
		}
		val billboardMatrix: Matrix43? = this.glToolkit!!.billboardMatrix
		this.glToolkit.pushMatrix()
		this.glToolkit.C(!this.hasAlphaTransition)
		this.glToolkit.setLightingEnabled(false)
		this.glToolkit.toggleDrawingClientstates(this.glToolkit.quadVertexBuffer, null, this.glToolkit.quadTexCoordBuffer, null)
		for (i in 0 until this.billboardCount) {
			val glBillboardFace: GlBillboardFace = this.billboardFaces!![i]!!
			val glBillboardAttributes: BillboardSurfaceGL = this.billboardAttributes!![i]!!
			if (!glBillboardFace.hideWithBloom || !this.glToolkit.bloom()) {
				val centerX: Float = (this.verticesX!![glBillboardFace.vertexC] + this.verticesX!![glBillboardFace.vertexA] + this.verticesX!![glBillboardFace.vertexB]).toFloat() * 0.3333333f
				val centerY: Float = (this.verticesY!![glBillboardFace.vertexA] + this.verticesY!![glBillboardFace.vertexB] + this.verticesY!![glBillboardFace.vertexC]).toFloat() * 0.3333333f
				val centerZ: Float = (this.verticesZ!![glBillboardFace.vertexA] + this.verticesZ!![glBillboardFace.vertexB] + this.verticesZ!![glBillboardFace.vertexC]).toFloat() * 0.3333333f
				val viewX: Float = HdModel.modelViewXW + centerZ * HdModel.modelViewXZ + centerY * HdModel.modelViewXY + HdModel.modelViewXX * centerX
				val viewY: Float = HdModel.modelViewYW + HdModel.modelViewYZ * centerZ + centerY * HdModel.modelViewYY + HdModel.modelViewYX * centerX
				val viewZ: Float = centerX * HdModel.modelViewZX + centerY * HdModel.modelViewZY + centerZ * HdModel.modelViewZZ + HdModel.modelViewZW
				val scale: Float = (1.0 / kotlin.math.sqrt((viewZ * viewZ + viewY * viewY + viewX * viewX).toDouble())).toFloat() * glBillboardFace.distance.toFloat()
				billboardMatrix!!.setBillboardTransform(viewZ * scale - viewZ, (glBillboardFace.width * glBillboardAttributes.scaleX) shr 7, viewX + glBillboardAttributes.offsetX.toFloat() - viewX * scale, (glBillboardFace.height * glBillboardAttributes.scaleY) shr 7, glBillboardAttributes.angle, scale * viewY + glBillboardAttributes.offsetY.toFloat() - viewY)
				this.glToolkit.loadMatrix(billboardMatrix)
				val diffuseColor: Int = glBillboardAttributes.diffuseColor
				HdGl.glColor4ub((diffuseColor shr 16).toByte(), (diffuseColor shr 8).toByte(), diffuseColor.toByte(), (diffuseColor shr 24).toByte())
				this.glToolkit.renderTexture(glBillboardFace.texture.toInt())
				this.glToolkit.setBlendMode(glBillboardFace.blendMode.toInt())
				this.glToolkit.drawArrays(4)
			}
		}
		this.glToolkit.C(true)
		this.glToolkit.popMatrix()
	}

	private fun connectNormal(y: Int, z: Int, uid: Long, x: Int, mesh: Mesh?, magnitude: Int, u: Float, vertex: Int, v: Float): Short {
		val start: Int = this.triangles!![vertex]
		val end: Int = this.triangles!![vertex + 1]
		var freeSlot: Int = 0
		for (i in start until end) {
			val normalIndex: Short = this.vertexNormalIndices!![i]
			if (normalIndex.toInt() == 0) {
				freeSlot = i
				break
			}
			if (uid == HdModel.normalsUid!![i]) {
				return (normalIndex - 1).toShort()
			}
		}
		this.vertexNormalIndices!![freeSlot] = (this.numNormals + 1).toShort()
		HdModel.normalsUid!![freeSlot] = uid
		this.normalsX!![this.numNormals] = x.toShort()
		this.normalsY!![this.numNormals] = y.toShort()
		this.normalsZ!![this.numNormals] = z.toShort()
		this.magnitudes!![this.numNormals] = magnitude.toByte()
		this.textureU!![this.numNormals] = u
		this.textureV!![this.numNormals] = v
		return (this.numNormals++).toShort()
	}

	override fun EA(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.maxY.toInt()
	}

	private fun pick(quick: Boolean, zoom: Int, y: Int, sizeShift: Int, x: Int, matrix: Matrix?): Boolean {
		val modelMatrix: Matrix43? = matrix as Matrix43?
		val viewMatrix: Matrix43 = this.glToolkit!!.viewMatrix43
		val translateX: Float = viewMatrix.xD + modelMatrix!!.zD * viewMatrix.xC + viewMatrix.xA * modelMatrix.xD + modelMatrix.yD * viewMatrix.xB
		val translateY: Float = viewMatrix.yD + modelMatrix.zD * viewMatrix.yC + modelMatrix.xD * viewMatrix.yA + modelMatrix.yD * viewMatrix.yB
		HdModel.modelViewXY = viewMatrix.xC * modelMatrix.zB + modelMatrix.yB * viewMatrix.xB + viewMatrix.xA * modelMatrix.xB
		HdModel.modelViewYZ = modelMatrix.xC * viewMatrix.yA + viewMatrix.yB * modelMatrix.yC + viewMatrix.yC * modelMatrix.zC
		HdModel.modelViewZX = viewMatrix.zB * modelMatrix.yA + modelMatrix.xA * viewMatrix.zA + modelMatrix.zA * viewMatrix.zC
		HdModel.modelViewZZ = modelMatrix.xC * viewMatrix.zA + modelMatrix.yC * viewMatrix.zB + viewMatrix.zC * modelMatrix.zC
		val translateZ: Float = viewMatrix.zA * modelMatrix.xD + viewMatrix.zB * modelMatrix.yD + modelMatrix.zD * viewMatrix.zC + viewMatrix.zD
		HdModel.modelViewYY = viewMatrix.yA * modelMatrix.xB + viewMatrix.yB * modelMatrix.yB + viewMatrix.yC * modelMatrix.zB
		HdModel.modelViewYX = modelMatrix.zA * viewMatrix.yC + modelMatrix.yA * viewMatrix.yB + viewMatrix.yA * modelMatrix.xA
		HdModel.modelViewXZ = viewMatrix.xB * modelMatrix.yC + viewMatrix.xA * modelMatrix.xC + modelMatrix.zC * viewMatrix.xC
		HdModel.modelViewXX = modelMatrix.zA * viewMatrix.xC + modelMatrix.xA * viewMatrix.xA + modelMatrix.yA * viewMatrix.xB
		HdModel.modelViewZY = viewMatrix.zC * modelMatrix.zB + modelMatrix.yB * viewMatrix.zB + modelMatrix.xB * viewMatrix.zA
		var anyProjected: Boolean = false
		var minScreenX: Float = Float.MAX_VALUE
		var maxScreenX: Float = -3.4028235E38f
		var minScreenY: Float = Float.MAX_VALUE
		var maxScreenY: Float = -3.4028235E38f
		val scaleX: Int = this.glToolkit.projectionScaleX
		val scaleY: Int = this.glToolkit.projectionScaleY
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		val extentX: Int = (this.maxX - this.minX) shr 1
		val extentY: Int = (this.maxY - this.minY) shr 1
		val extentZ: Int = (this.maxZ - this.minZ) shr 1
		val centerX: Int = this.minX + extentX
		val centerY: Int = this.minY + extentY
		val centerZ: Int = extentZ + this.minZ
		val boxMinX: Int = centerX - (extentX shl sizeShift)
		val boxMinY: Int = centerY - (extentY shl sizeShift)
		val boxMinZ: Int = centerZ - (extentZ shl sizeShift)
		val boxMaxX: Int = centerX + (extentX shl sizeShift)
		val boxMaxY: Int = (extentY shl sizeShift) + centerY
		val boxMaxZ: Int = (extentZ shl sizeShift) + centerZ
		HdModel.matrixX[0] = boxMinX
		HdModel.matrixY[0] = boxMinY
		HdModel.matrixX[1] = boxMaxX
		HdModel.matrixZ[0] = boxMinZ
		HdModel.matrixY[1] = boxMinY
		HdModel.matrixX[2] = boxMinX
		HdModel.matrixZ[1] = boxMinZ
		HdModel.matrixY[2] = boxMaxY
		HdModel.matrixZ[2] = boxMinZ
		HdModel.matrixX[3] = boxMaxX
		HdModel.matrixY[3] = boxMaxY
		HdModel.matrixX[4] = boxMinX
		HdModel.matrixZ[3] = boxMinZ
		HdModel.matrixY[4] = boxMinY
		HdModel.matrixZ[4] = boxMaxZ
		HdModel.matrixX[5] = boxMaxX
		HdModel.matrixY[5] = boxMinY
		HdModel.matrixZ[5] = boxMaxZ
		HdModel.matrixX[6] = boxMinX
		HdModel.matrixY[6] = boxMaxY
		HdModel.matrixX[7] = boxMaxX
		HdModel.matrixZ[6] = boxMaxZ
		HdModel.matrixY[7] = boxMaxY
		HdModel.matrixZ[7] = boxMaxZ
		var viewX: Float
		var viewY: Float
		var viewZ: Float
		var pointX: Float
		var pointY: Float
		var pointZ: Float
		for (i in 0 until 8) {
			pointZ = HdModel.matrixZ[i].toFloat()
			pointY = HdModel.matrixY[i].toFloat()
			pointX = HdModel.matrixX[i].toFloat()
			viewZ = HdModel.modelViewZZ * pointZ + pointX * HdModel.modelViewZX + HdModel.modelViewZY * pointY + translateZ
			viewY = translateY + HdModel.modelViewYX * pointX + HdModel.modelViewYY * pointY + HdModel.modelViewYZ * pointZ
			viewX = translateX + HdModel.modelViewXY * pointY + pointX * HdModel.modelViewXX + HdModel.modelViewXZ * pointZ
			if (this.glToolkit.zNear.toFloat() <= viewZ) {
				if (zoom > 0) {
					viewZ = zoom.toFloat()
				}
				val screenX: Float = viewX * scaleX.toFloat() / viewZ + this.glToolkit.projectionCenterX.toFloat()
				if (screenX < minScreenX) {
					minScreenX = screenX
				}
				if (screenX > maxScreenX) {
					maxScreenX = screenX
				}
				val screenY: Float = viewY * scaleY.toFloat() / viewZ + this.glToolkit.projectionCenterY.toFloat()
				if (screenY > maxScreenY) {
					maxScreenY = screenY
				}
				anyProjected = true
				if (screenY < minScreenY) {
					minScreenY = screenY
				}
			}
		}
		if (anyProjected && x.toFloat() > minScreenX && x.toFloat() < maxScreenX && y.toFloat() > minScreenY && y.toFloat() < maxScreenY) {
			if (quick) {
				return true
			}
			if (this.glToolkit.scratchVerticesX.size < this.numNormals) {
				this.glToolkit.scratchVerticesY = IntArray(this.numNormals)
				this.glToolkit.scratchVerticesX = IntArray(this.numNormals)
			}
			val projectedX: IntArray = this.glToolkit.scratchVerticesX
			val projectedY: IntArray = this.glToolkit.scratchVerticesY
			var local706: Int
			for (j in 0 until this.maxDepth) {
				pointX = this.verticesX!![j].toFloat()
				pointZ = this.verticesZ!![j].toFloat()
				pointY = this.verticesY!![j].toFloat()
				viewZ = translateZ + pointZ * HdModel.modelViewZZ + HdModel.modelViewZX * pointX + pointY * HdModel.modelViewZY
				viewX = translateX + pointY * HdModel.modelViewXY + pointX * HdModel.modelViewXX + HdModel.modelViewXZ * pointZ
				viewY = translateY + HdModel.modelViewYY * pointY + pointX * HdModel.modelViewYX + pointZ * HdModel.modelViewYZ
				var local713: Int
				var local715: Int
				var local724: Int
				if (viewZ >= this.glToolkit.zNear.toFloat()) {
					if (zoom > 0) {
						viewZ = zoom.toFloat()
					}
					val screenX: Int = (this.glToolkit.projectionCenterX.toFloat() + viewX * scaleX.toFloat() / viewZ).toInt()
					val screenY: Int = (this.glToolkit.projectionCenterY.toFloat() + scaleY.toFloat() * viewY / viewZ).toInt()
					val start: Int = this.triangles!![j]
					val end: Int = this.triangles!![j + 1]
					for (k in start until end) {
						val normalIndex: Int = this.vertexNormalIndices!![k] - 1
						if (normalIndex == -1) {
							break
						}
						projectedX[normalIndex] = screenX
						projectedY[normalIndex] = screenY
					}
				} else {
					val start: Int = this.triangles!![j]
					val end: Int = this.triangles!![j + 1]
					for (index in start until end) {
						val normalIndex: Int = this.vertexNormalIndices!![index] - 1
						if (normalIndex == -1) {
							break
						}
						projectedX[this.vertexNormalIndices!![index] - 1] = -999999
					}
				}
			}
			for (faceIndex in 0 until this.numFaces) {
				if (projectedX[this.facesA!![faceIndex].toInt()] != -999999 && projectedX[this.facesB!![faceIndex].toInt()] != -999999 && projectedX[this.facesC!![faceIndex].toInt()] != -999999 && this.isHovering(y, projectedX[this.facesB!![faceIndex].toInt()], projectedX[this.facesC!![faceIndex].toInt()], projectedY[this.facesB!![faceIndex].toInt()], projectedX[this.facesA!![faceIndex].toInt()], projectedY[this.facesA!![faceIndex].toInt()], x, projectedY[this.facesC!![faceIndex].toInt()])) {
					return true
				}
			}
		}
		return false
	}

	override fun O(scaleX: Int, scaleY: Int, scaleZ: Int) {
		for (i in 0 until this.maxDepth) {
			if (scaleX != 128) {
				this.verticesX!![i] = (scaleX * this.verticesX!![i]) shr 7
			}
			if (scaleY != 128) {
				this.verticesY!![i] = (this.verticesY!![i] * scaleY) shr 7
			}
			if (scaleZ != 128) {
				this.verticesZ!![i] = (this.verticesZ!![i] * scaleZ) shr 7
			}
		}
		if (this.vertexBuffer != null) {
			this.vertexBuffer!!.buffer = null
		}
		this.boundsCalculated = false
	}

	override fun ba(shadow: Shadow?): Shadow? {
		if (this.numNormals == 0) {
			return null
		}
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		val shadowMinX: Int
		val shadowMaxX: Int
		if (this.glToolkit!!.sunSlopeX > 0) {
			shadowMinX = (this.minX - ((this.maxY * this.glToolkit.sunSlopeX) shr 8)) shr this.glToolkit.shadowScaleShift
			shadowMaxX = (this.maxX - ((this.minY * this.glToolkit.sunSlopeX) shr 8)) shr this.glToolkit.shadowScaleShift
		} else {
			shadowMinX = (this.minX - ((this.minY * this.glToolkit.sunSlopeX) shr 8)) shr this.glToolkit.shadowScaleShift
			shadowMaxX = (this.maxX - ((this.glToolkit.sunSlopeX * this.maxY) shr 8)) shr this.glToolkit.shadowScaleShift
		}
		val shadowMinZ: Int
		val shadowMaxZ: Int
		if (this.glToolkit.sunSlopeZ > 0) {
			shadowMinZ = (this.minZ - ((this.glToolkit.sunSlopeZ * this.maxY) shr 8)) shr this.glToolkit.shadowScaleShift
			shadowMaxZ = (this.maxZ - ((this.minY * this.glToolkit.sunSlopeZ) shr 8)) shr this.glToolkit.shadowScaleShift
		} else {
			shadowMinZ = (this.minZ - ((this.glToolkit.sunSlopeZ * this.minY) shr 8)) shr this.glToolkit.shadowScaleShift
			shadowMaxZ = (this.maxZ - ((this.glToolkit.sunSlopeZ * this.maxY) shr 8)) shr this.glToolkit.shadowScaleShift
		}
		val width: Int = shadowMaxX + 1 - shadowMinX
		val height: Int = shadowMaxZ + 1 - shadowMinZ
		val glShadow: HdShadow? = shadow as HdShadow?
		val glShadow2: HdShadow
		if (glShadow != null && glShadow.fitsDimension(height, width)) {
			glShadow2 = glShadow
			glShadow.reset()
		} else {
			glShadow2 = HdShadow(this.glToolkit, width, height)
		}
		glShadow2.setDimension(shadowMaxZ, shadowMaxX, shadowMinZ, shadowMinX)
		this.projectShadow(glShadow2)
		return glShadow2
	}

	override fun uploadToGpu() {
		if (this.numNormals <= 0 || this.renderedFaceCount <= 0) {
			return
		}
		this.upload(false)
		if ((this.renderState.toInt() and 0x10) == 0 && this.modelElementsBuffer!!.openglElementsBuffer == null) {
			this.buildElementsBuffer(false)
		}
		this.releaseUploadedData()
	}

	override fun na(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.size2d.toInt()
	}

	override fun k(angle: Int) {
		val sin: Int = TextureCombineSource.SINE[angle]
		val cos: Int = TextureCombineSource.COSINE[angle]
		var local33: Int
		for (i in 0 until this.maxDepth) {
			val rotatedX: Int = (this.verticesX!![i] * cos + this.verticesZ!![i] * sin) shr 14
			this.verticesZ!![i] = (this.verticesZ!![i] * cos - this.verticesX!![i] * sin) shr 14
			this.verticesX!![i] = rotatedX
		}
		for (normalIndex in 0 until this.numNormals) {
			val rotatedNormalX: Int = (this.normalsX!![normalIndex] * cos + this.normalsZ!![normalIndex] * sin) shr 14
			this.normalsZ!![normalIndex] = ((cos * this.normalsZ!![normalIndex] - sin * this.normalsX!![normalIndex]) shr 14).toShort()
			this.normalsX!![normalIndex] = rotatedNormalX.toShort()
		}
		if (this.normalsBuffer == null && this.colorsBuffer != null) {
			this.colorsBuffer!!.buffer = null
		}
		if (this.normalsBuffer != null) {
			this.normalsBuffer!!.buffer = null
		}
		if (this.vertexBuffer != null) {
			this.vertexBuffer!!.buffer = null
		}
		this.boundsCalculated = false
	}

	private fun projectShadow(shadow: HdShadow) {
		if (this.numNormals > this.glToolkit!!.scratchVerticesX.size) {
			this.glToolkit.scratchVerticesY = IntArray(this.numNormals)
			this.glToolkit.scratchVerticesX = IntArray(this.numNormals)
		}
		val shadowX: IntArray = this.glToolkit.scratchVerticesX
		val shadowZ: IntArray = this.glToolkit.scratchVerticesY
		var local70: Int
		var local109: Int
		var local118: Int
		for (i in 0 until this.maxDepth) {
			val vertexShadowX: Int = ((this.verticesX!![i] - ((this.glToolkit.sunSlopeX * this.verticesY!![i]) shr 8)) shr this.glToolkit.shadowScaleShift) - shadow.startX
			val vertexShadowZ: Int = ((this.verticesZ!![i] - ((this.glToolkit.sunSlopeZ * this.verticesY!![i]) shr 8)) shr this.glToolkit.shadowScaleShift) - shadow.startZ
			val start: Int = this.triangles!![i]
			val end: Int = this.triangles!![i + 1]
			for (index in start until end) {
				val normalIndex: Int = this.vertexNormalIndices!![index] - 1
				if (normalIndex == -1) {
					break
				}
				shadowX[normalIndex] = vertexShadowX
				shadowZ[normalIndex] = vertexShadowZ
			}
		}
		for (faceIndex in 0 until this.renderedFaceCount) {
			if (this.facesAlpha == null || this.facesAlpha!![faceIndex] <= 128) {
				val vertexA: Short = this.facesA!![faceIndex]
				val vertexB: Short = this.facesB!![faceIndex]
				val vertexC: Short = this.facesC!![faceIndex]
				val xA: Int = shadowX[vertexA.toInt()]
				val xB: Int = shadowX[vertexB.toInt()]
				val xC: Int = shadowX[vertexC.toInt()]
				val zA: Int = shadowZ[vertexA.toInt()]
				val zB: Int = shadowZ[vertexB.toInt()]
				val zC: Int = shadowZ[vertexC.toInt()]
				if ((xA - xB) * (zB - zC) - (xC - xB) * (zB - zA) > 0) {
					shadow.fillTriangle(xC, xA, zA, xB, zC, zB)
				}
			}
		}
	}

	override fun adjustColours(hue: Int, saturation: Int, lightness: Int, opacity: Int) {
		var local16: Int
		for (i in 0 until this.numFaces) {
			val faceColor: Int = this.facesColor!![i].toInt() and 0xFFFF
			var faceHue: Int = (faceColor shr 10) and 0x3F
			var faceSaturation: Int = (faceColor shr 7) and 0x7
			if (hue != -1) {
				faceHue += (opacity * (hue - faceHue)) shr 7
			}
			var faceLightness: Int = faceColor and 0x7F
			if (saturation != -1) {
				faceSaturation += (opacity * (saturation - faceSaturation)) shr 7
			}
			if (lightness != -1) {
				faceLightness += ((lightness - faceLightness) * opacity) shr 7
			}
			this.facesColor!![i] = ((faceSaturation shl 7) or (faceHue shl 10) or faceLightness).toShort()
		}
		if (this.billboardFaces != null) {
			for (billboardIndex in 0 until this.billboardCount) {
				val glBillboardFace: GlBillboardFace = this.billboardFaces!![billboardIndex]!!
				val glBillboardAttributes: BillboardSurfaceGL = this.billboardAttributes!![billboardIndex]!!
				glBillboardAttributes.diffuseColor = (ColorUtil.hslToRgb!![this.facesColor!![glBillboardFace.face].toInt() and 0xFFFF] and 0xFFFFFF) or (glBillboardAttributes.diffuseColor and 0xFF000000.toInt())
			}
		}
		if (this.colorsBuffer != null) {
			this.colorsBuffer!!.buffer = null
		}
	}

	override fun H(x: Int, y: Int, z: Int) {
		for (i in 0 until this.maxDepth) {
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
		this.boundsCalculated = false
		if (this.vertexBuffer != null) {
			this.vertexBuffer!!.buffer = null
		}
	}

	override fun a(angle: Int) {
		val sin: Int = TextureCombineSource.SINE[angle]
		val cos: Int = TextureCombineSource.COSINE[angle]
		for (i in 0 until this.maxDepth) {
			val rotatedX: Int = (cos * this.verticesX!![i] + sin * this.verticesZ!![i]) shr 14
			this.verticesZ!![i] = (this.verticesZ!![i] * cos - this.verticesX!![i] * sin) shr 14
			this.verticesX!![i] = rotatedX
		}
		this.boundsCalculated = false
		if (this.vertexBuffer != null) {
			this.vertexBuffer!!.buffer = null
		}
	}

	override fun F(): Boolean {
		return this.hasAlphaTransition
	}

	override fun lock() {
	}

	private fun copyModelData(glModel: HdModel, functionMask: Int, ensureLit: Boolean, glModel2: HdModel?, usesPreallocated: Boolean): Model? {
		glModel.numVertices = this.numVertices
		glModel.preferenceFlags = this.preferenceFlags
		glModel.billboardCount = this.billboardCount
		glModel.renderState = 0
		glModel.ambient = this.ambient
		glModel.numNormals = this.numNormals
		glModel.textureAnimating = this.textureAnimating
		glModel.functionMask = functionMask
		glModel.contrast = this.contrast
		glModel.numFaces = this.numFaces
		glModel.renderedFaceCount = this.renderedFaceCount
		glModel.maxDepth = this.maxDepth
		if ((functionMask and 0x100) == 0) {
			glModel.hasAlphaTransition = this.hasAlphaTransition
		} else {
			glModel.hasAlphaTransition = true
		}
		val copyVerticesX: Boolean = HdModel.altersVerticesX(this.preferenceFlags, functionMask)
		val copyVerticesY: Boolean = HdModel.altersVerticesY(this.preferenceFlags, functionMask)
		val copyVerticesZ: Boolean = HdModel.altersVerticesZ(this.preferenceFlags, functionMask)
		val copyVertices: Boolean = copyVerticesZ or copyVerticesY or copyVerticesX
		var billboardCount: Int
		if (copyVertices) {
			if (!copyVerticesX) {
				glModel.verticesX = this.verticesX
			} else if (glModel2!!.verticesX == null || glModel2.verticesX!!.size < this.numVertices) {
				glModel2.verticesX = IntArray(this.numVertices)
				glModel.verticesX = glModel2.verticesX
			} else {
				glModel.verticesX = glModel2.verticesX
			}
			if (!copyVerticesY) {
				glModel.verticesY = this.verticesY
			} else if (glModel2!!.verticesY == null || this.numVertices > glModel2.verticesY!!.size) {
				glModel2.verticesY = IntArray(this.numVertices)
				glModel.verticesY = glModel2.verticesY
			} else {
				glModel.verticesY = glModel2.verticesY
			}
			if (!copyVerticesZ) {
				glModel.verticesZ = this.verticesZ
			} else if (glModel2!!.verticesZ == null || this.numVertices > glModel2.verticesZ!!.size) {
				glModel2.verticesZ = IntArray(this.numVertices)
				glModel.verticesZ = glModel2.verticesZ
			} else {
				glModel.verticesZ = glModel2.verticesZ
			}
			billboardCount = 0
			while (billboardCount < this.numVertices) {
				if (copyVerticesX) {
					glModel.verticesX!![billboardCount] = this.verticesX!![billboardCount]
				}
				if (copyVerticesY) {
					glModel.verticesY!![billboardCount] = this.verticesY!![billboardCount]
				}
				if (copyVerticesZ) {
					glModel.verticesZ!![billboardCount] = this.verticesZ!![billboardCount]
				}
				billboardCount++
			}
		} else {
			glModel.verticesZ = this.verticesZ
			glModel.verticesY = this.verticesY
			glModel.verticesX = this.verticesX
		}
		if (HdModel.altersAnyVerticeType(functionMask, this.preferenceFlags)) {
			glModel.vertexBuffer = glModel2!!.vertexBuffer
			if (usesPreallocated) {
				glModel.renderState = (glModel.renderState.toInt() or 0x1).toByte()
			}
			glModel.vertexBuffer!!.buffer = this.vertexBuffer!!.buffer
			glModel.vertexBuffer!!.offset = this.vertexBuffer!!.offset
		} else if (HdModel.hasVertices(functionMask, this.preferenceFlags)) {
			glModel.vertexBuffer = this.vertexBuffer
		} else {
			glModel.vertexBuffer = null
		}
		if (GpuModelRetainMasks.altersFaceColors(this.preferenceFlags, functionMask)) {
			if (glModel2!!.facesColor == null || this.numFaces > glModel2.facesColor!!.size) {
				glModel2.facesColor = ShortArray(this.numFaces)
				glModel.facesColor = glModel2.facesColor
			} else {
				glModel.facesColor = glModel2.facesColor
			}
			billboardCount = 0
			while (billboardCount < this.numFaces) {
				glModel.facesColor!![billboardCount] = this.facesColor!![billboardCount]
				billboardCount++
			}
		} else {
			glModel.facesColor = this.facesColor
		}
		if (HdModel.altersAlpha(functionMask, this.preferenceFlags)) {
			if (glModel2!!.facesAlpha == null || this.numFaces > glModel2.facesAlpha!!.size) {
				glModel2.facesAlpha = ByteArray(this.numFaces)
				glModel.facesAlpha = glModel2.facesAlpha
			} else {
				glModel.facesAlpha = glModel2.facesAlpha
			}
			billboardCount = 0
			while (billboardCount < this.numFaces) {
				glModel.facesAlpha!![billboardCount] = this.facesAlpha!![billboardCount]
				billboardCount++
			}
		} else {
			glModel.facesAlpha = this.facesAlpha
		}
		if (HdModel.requiresColorBufferRebuild(functionMask, this.preferenceFlags)) {
			glModel.colorsBuffer = glModel2!!.colorsBuffer
			if (usesPreallocated) {
				glModel.renderState = (glModel.renderState.toInt() or 0x2).toByte()
			}
			glModel.colorsBuffer!!.buffer = this.colorsBuffer!!.buffer
			glModel.colorsBuffer!!.offset = this.colorsBuffer!!.offset
		} else if (HdModel.hasColors(this.preferenceFlags, functionMask)) {
			glModel.colorsBuffer = this.colorsBuffer
		} else {
			glModel.colorsBuffer = null
		}
		var local593: Int
		if (HdModel.altersLight(this.preferenceFlags, functionMask)) {
			if (glModel2!!.normalsX == null || glModel2.normalsX!!.size < this.numNormals) {
				billboardCount = this.numNormals
				glModel2.normalsY = ShortArray(billboardCount)
				glModel.normalsY = glModel2.normalsY
				glModel2.normalsX = ShortArray(billboardCount)
				glModel.normalsX = glModel2.normalsX
				glModel2.normalsZ = ShortArray(billboardCount)
				glModel.normalsZ = glModel2.normalsZ
			} else {
				glModel.normalsX = glModel2.normalsX
				glModel.normalsY = glModel2.normalsY
				glModel.normalsZ = glModel2.normalsZ
			}
			if (this.mergedNormals == null) {
				billboardCount = 0
				while (billboardCount < this.numNormals) {
					glModel.normalsX!![billboardCount] = this.normalsX!![billboardCount]
					glModel.normalsY!![billboardCount] = this.normalsY!![billboardCount]
					glModel.normalsZ!![billboardCount] = this.normalsZ!![billboardCount]
					billboardCount++
				}
			} else {
				if (glModel2.mergedNormals == null) {
					glModel2.mergedNormals = ModelNormalsGL()
				}
				val targetNormals: ModelNormalsGL? = glModel2.mergedNormals
				glModel.mergedNormals = targetNormals
				if (targetNormals!!.normalsX == null || this.numNormals > targetNormals.normalsX!!.size) {
					val normalCount: Int = this.numNormals
					targetNormals.normalsY = ShortArray(normalCount)
					targetNormals.normalsZ = ShortArray(normalCount)
					targetNormals.normalsX = ShortArray(normalCount)
					targetNormals.magnitude = ByteArray(normalCount)
				}
				for (normalIndex in 0 until this.numNormals) {
					glModel.normalsX!![normalIndex] = this.normalsX!![normalIndex]
					glModel.normalsY!![normalIndex] = this.normalsY!![normalIndex]
					glModel.normalsZ!![normalIndex] = this.normalsZ!![normalIndex]
					targetNormals.normalsX!![normalIndex] = this.mergedNormals!!.normalsX!![normalIndex]
					targetNormals.normalsY!![normalIndex] = this.mergedNormals!!.normalsY!![normalIndex]
					targetNormals.normalsZ!![normalIndex] = this.mergedNormals!!.normalsZ!![normalIndex]
					targetNormals.magnitude!![normalIndex] = this.mergedNormals!!.magnitude!![normalIndex]
				}
			}
			glModel.magnitudes = this.magnitudes
		} else {
			glModel.normalsX = this.normalsX
			glModel.normalsY = this.normalsY
			glModel.mergedNormals = this.mergedNormals
			glModel.magnitudes = this.magnitudes
			glModel.normalsZ = this.normalsZ
		}
		if (HdModel.requiresNormalsBufferRebuild(functionMask, this.preferenceFlags)) {
			if (usesPreallocated) {
				glModel.renderState = (glModel.renderState.toInt() or 0x4).toByte()
			}
			glModel.normalsBuffer = glModel2!!.normalsBuffer
			glModel.normalsBuffer!!.buffer = this.normalsBuffer!!.buffer
			glModel.normalsBuffer!!.offset = this.normalsBuffer!!.offset
		} else if (HdModel.hasNormals(this.preferenceFlags, functionMask)) {
			glModel.normalsBuffer = this.normalsBuffer
		} else {
			glModel.normalsBuffer = null
		}
		if (HdModel.altersTextureCoordinates(functionMask, this.preferenceFlags)) {
			if (glModel2!!.textureU == null || this.numFaces > glModel2.textureU!!.size) {
				billboardCount = this.numNormals
				glModel2.textureV = FloatArray(billboardCount)
				glModel.textureV = glModel2.textureV
				glModel2.textureU = FloatArray(billboardCount)
				glModel.textureU = glModel2.textureU
			} else {
				glModel.textureV = glModel2.textureV
				glModel.textureU = glModel2.textureU
			}
			billboardCount = 0
			while (billboardCount < this.numNormals) {
				glModel.textureU!![billboardCount] = this.textureU!![billboardCount]
				glModel.textureV!![billboardCount] = this.textureV!![billboardCount]
				billboardCount++
			}
		} else {
			glModel.textureV = this.textureV
			glModel.textureU = this.textureU
		}
		if (HdModel.altersTextureUV(this.preferenceFlags, functionMask)) {
			glModel.textureCoordinatesBuffer = glModel2!!.textureCoordinatesBuffer
			if (usesPreallocated) {
				glModel.renderState = (glModel.renderState.toInt() or 0x8).toByte()
			}
			glModel.textureCoordinatesBuffer!!.buffer = this.textureCoordinatesBuffer!!.buffer
			glModel.textureCoordinatesBuffer!!.offset = this.textureCoordinatesBuffer!!.offset
		} else if (ModelTexCoordFlag.hasTextureCoordinates(this.preferenceFlags, functionMask)) {
			glModel.textureCoordinatesBuffer = this.textureCoordinatesBuffer
		} else {
			glModel.textureCoordinatesBuffer = null
		}
		if (HdModel.altersFacing(this.preferenceFlags, functionMask)) {
			if (glModel2!!.facesA == null || glModel2.facesA!!.size < this.numFaces) {
				billboardCount = this.numFaces
				glModel2.facesC = ShortArray(billboardCount)
				glModel.facesC = glModel2.facesC
				glModel2.facesA = ShortArray(billboardCount)
				glModel.facesA = glModel2.facesA
				glModel2.facesB = ShortArray(billboardCount)
				glModel.facesB = glModel2.facesB
			} else {
				glModel.facesB = glModel2.facesB
				glModel.facesA = glModel2.facesA
				glModel.facesC = glModel2.facesC
			}
			billboardCount = 0
			while (billboardCount < this.numFaces) {
				glModel.facesA!![billboardCount] = this.facesA!![billboardCount]
				glModel.facesB!![billboardCount] = this.facesB!![billboardCount]
				glModel.facesC!![billboardCount] = this.facesC!![billboardCount]
				billboardCount++
			}
		} else {
			glModel.facesC = this.facesC
			glModel.facesB = this.facesB
			glModel.facesA = this.facesA
		}
		if (HdModel.copyElementsBuffer(functionMask, this.preferenceFlags)) {
			if (usesPreallocated) {
				glModel.renderState = (glModel.renderState.toInt() or 0x10).toByte()
			}
			glModel.modelElementsBuffer = glModel2!!.modelElementsBuffer
			glModel.modelElementsBuffer!!.openglElementsBuffer = this.modelElementsBuffer!!.openglElementsBuffer
		} else if (HdModel.hasElementsBuffer(functionMask, this.preferenceFlags)) {
			glModel.modelElementsBuffer = this.modelElementsBuffer
		} else {
			glModel.modelElementsBuffer = null
		}
		if (HdModel.altersTextures(this.preferenceFlags, functionMask)) {
			if (glModel2!!.facesMaterial == null || glModel2.facesMaterial!!.size < this.numFaces) {
				billboardCount = this.numFaces
				glModel2.facesMaterial = ShortArray(billboardCount)
				glModel.facesMaterial = glModel2.facesMaterial
			} else {
				glModel.facesMaterial = glModel2.facesMaterial
			}
			billboardCount = 0
			while (billboardCount < this.numFaces) {
				glModel.facesMaterial!![billboardCount] = this.facesMaterial!![billboardCount]
				billboardCount++
			}
		} else {
			glModel.facesMaterial = this.facesMaterial
		}
		if (!HdModel.copyBillboards(functionMask, this.preferenceFlags)) {
			glModel.billboardAttributes = this.billboardAttributes
		} else if (glModel2!!.billboardAttributes == null || this.billboardCount > glModel2.billboardAttributes!!.size) {
			billboardCount = this.billboardCount
			glModel2.billboardAttributes = arrayOfNulls<BillboardSurfaceGL>(billboardCount)
			glModel.billboardAttributes = glModel2.billboardAttributes
			for (billboardIndex in 0 until this.billboardCount) {
				glModel.billboardAttributes!![billboardIndex] = this.billboardAttributes!![billboardIndex]!!.copy()
			}
		} else {
			glModel.billboardAttributes = glModel2.billboardAttributes
			billboardCount = 0
			while (billboardCount < this.billboardCount) {
				glModel.billboardAttributes!![billboardCount]!!.copyFrom(this.billboardAttributes!![billboardCount])
				billboardCount++
			}
		}
		glModel.triangles = this.triangles
		glModel.effectiveVertices = this.effectiveVertices
		glModel.materialBatchOffsets = this.materialBatchOffsets
		glModel.sceneBatchOffsets = this.sceneBatchOffsets
		glModel.sceneBatchMaterials = this.sceneBatchMaterials
		glModel.retainedObject = -1
		glModel.retainedBatches = null
		if (this.boundsCalculated) {
			glModel.minX = this.minX
			glModel.size3d = this.size3d
			glModel.minZ = this.minZ
			glModel.maxX = this.maxX
			glModel.maxY = this.maxY
			glModel.minY = this.minY
			glModel.boundsCalculated = true
			glModel.size2d = this.size2d
			glModel.maxZ = this.maxZ
		} else {
			glModel.boundsCalculated = false
		}
		glModel.billboardFaces = this.billboardFaces
		glModel.verticesSubmeshes = this.verticesSubmeshes
		glModel.vertexGroups = this.vertexGroups
		glModel.vertexNormalIndices = this.vertexNormalIndices
		glModel.faceGroups = this.faceGroups
		glModel.facesSubmeshes = this.facesSubmeshes
		glModel.billboardGroups = this.billboardGroups
		glModel.emissiveTriangles = this.emissiveTriangles
		return glModel
	}

	override fun LA(contrast: Int) {
		if (this.colorsBuffer != null) {
			this.colorsBuffer!!.buffer = null
		}
		this.contrast = contrast.toShort()
		if (this.normalsBuffer != null) {
			this.normalsBuffer!!.buffer = null
		}
	}

	override fun wa() {
		for (i in 0 until this.numVertices) {
			this.verticesX!![i] = (this.verticesX!![i] + 7) shr 4
			this.verticesY!![i] = (this.verticesY!![i] + 7) shr 4
			this.verticesZ!![i] = (this.verticesZ!![i] + 7) shr 4
		}
		if (this.vertexBuffer != null) {
			this.vertexBuffer!!.buffer = null
		}
		this.boundsCalculated = false
	}

	override fun renderOrtho(matrix: Matrix?, cylinder: BoundingBoxPolygon?, zoom: Int, flags: Int) {
		if (this.numNormals == 0) {
			return
		}
		val viewMatrix: Matrix43 = this.glToolkit!!.viewMatrix43
		val modelMatrix: Matrix43? = matrix as Matrix43?
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		HdModel.modelViewZY = modelMatrix!!.xB * viewMatrix.zA + viewMatrix.zB * modelMatrix.yB + viewMatrix.zC * modelMatrix.zB
		HdModel.modelViewZW = modelMatrix.zD * viewMatrix.zC + viewMatrix.zA * modelMatrix.xD + modelMatrix.yD * viewMatrix.zB + viewMatrix.zD
		val depthAtMinY: Float = HdModel.modelViewZW + HdModel.modelViewZY * this.minY.toFloat()
		val depthAtMaxY: Float = HdModel.modelViewZY * this.maxY.toFloat() + HdModel.modelViewZW
		val nearDepth: Float
		val farDepth: Float
		if (depthAtMinY > depthAtMaxY) {
			farDepth = depthAtMinY + this.size2d.toFloat()
			nearDepth = (-this.size2d).toFloat() + depthAtMaxY
		} else {
			farDepth = this.size2d.toFloat() + depthAtMaxY
			nearDepth = depthAtMinY - this.size2d.toFloat()
		}
		if (nearDepth >= this.glToolkit.farClipDistance || this.glToolkit.zNear.toFloat() >= farDepth) {
			return
		}
		HdModel.modelViewXY = viewMatrix.xC * modelMatrix.zB + modelMatrix.yB * viewMatrix.xB + modelMatrix.xB * viewMatrix.xA
		HdModel.modelViewXW = viewMatrix.xC * modelMatrix.zD + modelMatrix.yD * viewMatrix.xB + viewMatrix.xA * modelMatrix.xD + viewMatrix.xD
		val xAtMinY: Float = HdModel.modelViewXW + this.minY.toFloat() * HdModel.modelViewXY
		val xAtMaxY: Float = HdModel.modelViewXW + this.maxY.toFloat() * HdModel.modelViewXY
		val screenLeft: Float
		val screenRight: Float
		if (xAtMinY > xAtMaxY) {
			screenRight = this.glToolkit.projectionScaleX.toFloat() * (xAtMinY + this.size2d.toFloat())
			screenLeft = this.glToolkit.projectionScaleX.toFloat() * (xAtMaxY - this.size2d.toFloat())
		} else {
			screenLeft = this.glToolkit.projectionScaleX.toFloat() * ((-this.size2d).toFloat() + xAtMinY)
			screenRight = this.glToolkit.projectionScaleX.toFloat() * (xAtMaxY + this.size2d.toFloat())
		}
		if (this.glToolkit.viewX2 <= screenLeft / zoom.toFloat() || screenRight / zoom.toFloat() <= this.glToolkit.viewX1) {
			return
		}
		HdModel.modelViewYY = viewMatrix.yB * modelMatrix.yB + modelMatrix.xB * viewMatrix.yA + modelMatrix.zB * viewMatrix.yC
		HdModel.modelViewYW = viewMatrix.yD + viewMatrix.yC * modelMatrix.zD + viewMatrix.yB * modelMatrix.yD + viewMatrix.yA * modelMatrix.xD
		val yAtMinY: Float = HdModel.modelViewYY * this.minY.toFloat() + HdModel.modelViewYW
		val yAtMaxY: Float = HdModel.modelViewYW + this.maxY.toFloat() * HdModel.modelViewYY
		val screenTop: Float
		val screenBottom: Float
		if (yAtMaxY < yAtMinY) {
			screenTop = ((-this.size2d).toFloat() + yAtMaxY) * this.glToolkit.projectionScaleY.toFloat()
			screenBottom = this.glToolkit.projectionScaleY.toFloat() * (this.size2d.toFloat() + yAtMinY)
		} else {
			screenBottom = this.glToolkit.projectionScaleY.toFloat() * (yAtMaxY + this.size2d.toFloat())
			screenTop = this.glToolkit.projectionScaleY.toFloat() * (yAtMinY - this.size2d.toFloat())
		}
		if (screenTop / zoom.toFloat() >= this.glToolkit.viewY2 || this.glToolkit.viewY1 >= screenBottom / zoom.toFloat()) {
			return
		}
		if (cylinder != null || this.billboardFaces != null) {
			HdModel.modelViewYX = modelMatrix.xA * viewMatrix.yA + viewMatrix.yB * modelMatrix.yA + modelMatrix.zA * viewMatrix.yC
			HdModel.modelViewXZ = viewMatrix.xC * modelMatrix.zC + modelMatrix.xC * viewMatrix.xA + viewMatrix.xB * modelMatrix.yC
			HdModel.modelViewZX = viewMatrix.zC * modelMatrix.zA + modelMatrix.xA * viewMatrix.zA + viewMatrix.zB * modelMatrix.yA
			HdModel.modelViewYZ = modelMatrix.yC * viewMatrix.yB + modelMatrix.xC * viewMatrix.yA + viewMatrix.yC * modelMatrix.zC
			HdModel.modelViewZZ = viewMatrix.zC * modelMatrix.zC + modelMatrix.yC * viewMatrix.zB + modelMatrix.xC * viewMatrix.zA
			HdModel.modelViewXX = modelMatrix.zA * viewMatrix.xC + modelMatrix.yA * viewMatrix.xB + modelMatrix.xA * viewMatrix.xA
		}
		if (cylinder != null) {
			val centerX: Int = (this.maxX + this.minX) shr 1
			val centerZ: Int = (this.minZ + this.maxZ) shr 1
			val bottomX: Int = (this.minY.toFloat() * HdModel.modelViewXY + centerX.toFloat() * HdModel.modelViewXX + HdModel.modelViewXW + HdModel.modelViewXZ * centerZ.toFloat()).toInt()
			val bottomY: Int = (HdModel.modelViewYW + centerX.toFloat() * HdModel.modelViewYX + HdModel.modelViewYY * this.minY.toFloat() + HdModel.modelViewYZ * centerZ.toFloat()).toInt()
			val bottomZ: Int = (HdModel.modelViewZW + centerX.toFloat() * HdModel.modelViewZX + HdModel.modelViewZY * this.minY.toFloat() + HdModel.modelViewZZ * centerZ.toFloat()).toInt()
			val topX: Int = (HdModel.modelViewXZ * centerZ.toFloat() + HdModel.modelViewXY * this.maxY.toFloat() + HdModel.modelViewXW + HdModel.modelViewXX * centerX.toFloat()).toInt()
			val topY: Int = (HdModel.modelViewYW + HdModel.modelViewYX * centerX.toFloat() + HdModel.modelViewYY * this.maxY.toFloat() + centerZ.toFloat() * HdModel.modelViewYZ).toInt()
			cylinder.left = this.glToolkit.projectionCenterX + bottomX * this.glToolkit.projectionScaleX / zoom
			cylinder.bottom = bottomY * this.glToolkit.projectionScaleY / zoom + this.glToolkit.projectionCenterY
			cylinder.top = this.glToolkit.projectionCenterY + topY * this.glToolkit.projectionScaleY / zoom
			val topZ: Int = (centerZ.toFloat() * HdModel.modelViewZZ + HdModel.modelViewZY * this.maxY.toFloat() + HdModel.modelViewZX * centerX.toFloat() + HdModel.modelViewZW).toInt()
			cylinder.right = this.glToolkit.projectionCenterX + topX * this.glToolkit.projectionScaleX / zoom
			if (this.glToolkit.zNear <= bottomZ || topZ >= this.glToolkit.zNear) {
				cylinder.positionCalculated = true
				cylinder.size = this.glToolkit.projectionCenterX + this.glToolkit.projectionScaleX * (this.size2d + bottomX) / zoom - cylinder.left
			}
		}
		this.glToolkit.setOrthoZoom(zoom.toFloat())
		this.glToolkit.setupOrthoMatrixRendering()
		this.glToolkit.multiMatrix(modelMatrix)
		this.drawOpaqueFaces()
		this.glToolkit.popMatrix()
		this.renderBillboards()
	}

	override fun da(): Int {
		return this.contrast.toInt()
	}

	override fun NA(): Boolean {
		if (this.vertexGroups == null) {
			return false
		}
		for (i in 0 until this.numVertices) {
			this.verticesX!![i] = this.verticesX!![i] shl 0x4
			this.verticesY!![i] = this.verticesY!![i] shl 0x4
			this.verticesZ!![i] = this.verticesZ!![i] shl 0x4
		}
		ModelTransformOriginY.originY = 0
		HdModel.originX = 0
		HdModel.originZ = 0
		return true
	}

	override fun RA(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.maxX.toInt()
	}

	override fun G(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.maxZ.toInt()
	}

	override fun p(contourType: Int, contourModifier: Int, ground: Ground?, ground2: Ground?, x: Int, y: Int, z: Int) {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		var startX: Int = this.minX + x
		var endX: Int = x + this.maxX
		var startZ: Int = z + this.minZ
		var endZ: Int = this.maxZ + z
		if ((contourType == 1 || contourType == 2 || contourType == 3 || contourType == 5) && (startX < 0 || ground!!.width <= ((ground.tileUnits + endX) shr ground.tileScale) || startZ < 0 || ground.length <= ((endZ + ground.tileUnits) shr ground.tileScale))) {
			return
		}
		if (contourType == 4 || contourType == 5) {
			if (ground2 == null) {
				return
			}
			if (startX < 0 || ground2.width <= ((endX + ground2.tileUnits) shr ground2.tileScale) || startZ < 0 || ground2.length <= ((ground2.tileUnits + endZ) shr ground2.tileScale)) {
				return
			}
		} else {
			startX = startX shr ground!!.tileScale
			endX = (endX + ground.tileUnits - 1) shr ground.tileScale
			startZ = startZ shr ground.tileScale
			endZ = (endZ + ground.tileUnits - 1) shr ground.tileScale
			if (ground.getHeight(startZ, startX) == y && ground.getHeight(startZ, endX) == y && y == ground.getHeight(endZ, startX) && y == ground.getHeight(endZ, endX)) {
				return
			}
		}
		var local199: Int
		if (contourType == 1) {
			for (vertexIndex in 0 until this.maxDepth) {
				this.verticesY!![vertexIndex] = this.verticesY!![vertexIndex] + ground!!.averageHeight(this.verticesX!![vertexIndex] + x, z + this.verticesZ!![vertexIndex]) - y
			}
		} else {
			var local257: Int
			var local265: Int
			if (contourType == 2) {
				val modelMinY: Short = this.minY
				if (modelMinY.toInt() == 0) {
					return
				}
				for (vertexIndex in 0 until this.maxDepth) {
					val heightRatio: Int = (this.verticesY!![vertexIndex] shl 16) / modelMinY
					if (contourModifier > heightRatio) {
						this.verticesY!![vertexIndex] -= -((ground!!.averageHeight(this.verticesX!![vertexIndex] + x, z + this.verticesZ!![vertexIndex]) - y) * (-heightRatio + contourModifier) / contourModifier)
					}
				}
			} else {
				var local273: Int
				if (contourType == 3) {
					val sizeX: Int = (contourModifier and 0xFF) * 4
					val sizeZ: Int = ((contourModifier shr 8) and 0xFF) * 4
					val maxRotateX: Int = ((contourModifier shr 16) and 0xFF) shl 6
					val maxRotateZ: Int = ((contourModifier shr 24) and 0xFF) shl 6
					if (x - (sizeX shr 1) < 0 || (sizeX shr 1) + (x + ground!!.tileUnits) >= (ground.width shl ground.tileScale) || z - (sizeZ shr 1) < 0 || (sizeZ shr 1) + z + ground.tileUnits >= (ground.length shl ground.tileScale)) {
						return
					}
					this.hillChange(sizeX, maxRotateZ, x, sizeZ, y, maxRotateX, ground, z)
				} else if (contourType == 4) {
					val modelHeight: Int = this.maxY - this.minY
					for (vertexIndex in 0 until this.maxDepth) {
						this.verticesY!![vertexIndex] -= y - ground2!!.averageHeight(this.verticesX!![vertexIndex] + x, this.verticesZ!![vertexIndex] + z) - modelHeight
					}
				} else if (contourType == 5) {
					val modelHeight: Int = this.maxY - this.minY
					for (vertexIndex in 0 until this.maxDepth) {
						val worldX: Int = this.verticesX!![vertexIndex] + x
						val worldZ: Int = this.verticesZ!![vertexIndex] + z
						val height: Int = ground!!.averageHeight(worldX, worldZ)
						val upperHeight: Int = ground2!!.averageHeight(worldX, worldZ)
						val heightDelta: Int = height - contourModifier - upperHeight
						this.verticesY!![vertexIndex] = height + ((((this.verticesY!![vertexIndex] shl 8) / modelHeight) * heightDelta) shr 8) - y
					}
				}
			}
		}
		this.boundsCalculated = false
		if (this.vertexBuffer != null) {
			this.vertexBuffer!!.buffer = null
		}
	}

	override fun aa(src: Short, dst: Short) {
		val textureSource: MaterialProvider? = this.glToolkit!!.materialProvider
		for (i in 0 until this.numFaces) {
			if (this.facesMaterial!![i] == src) {
				this.facesMaterial!![i] = dst
			}
		}
		// 634HD retained scene: the batches' materials follow (a copy: copies of a model share the array)
		val sceneMaterials: IntArray? = this.sceneBatchMaterials
		if (sceneMaterials != null) {
			val from: Int = if ((src.toInt() and 0xFFFF) == 65535) -1 else src.toInt() and 0xFFFF
			val to: Int = if ((dst.toInt() and 0xFFFF) == 65535) -1 else dst.toInt() and 0xFFFF
			var changed: IntArray? = null
			for (i in 0 until sceneMaterials.size) {
				if (sceneMaterials[i] == from) {
					if (changed == null) {
						changed = sceneMaterials.copyOf()
					}
					changed[i] = to
				}
			}
			if (changed != null) {
				this.sceneBatchMaterials = changed
			}
		}
		var alpha: Byte = 0
		var brightness: Byte = 0
		if (src.toInt() != -1) {
			val textureMetrics: MaterialType? = textureSource!!.getMetrics(src.toInt() and 0xFFFF)
			brightness = textureMetrics!!.brightness
			alpha = textureMetrics.alpha
		}
		var newAlpha: Byte = 0
		var newBrightness: Byte = 0
		if (dst.toInt() != -1) {
			val textureMetrics2: MaterialType? = textureSource!!.getMetrics(dst.toInt() and 0xFFFF)
			if (textureMetrics2!!.speedU.toInt() != 0 || textureMetrics2.speedV.toInt() != 0) {
				this.textureAnimating = true
			}
			newAlpha = textureMetrics2.alpha
			newBrightness = textureMetrics2.brightness
		}
		if (!((newBrightness != brightness) or (newAlpha != alpha))) {
			return
		}
		if (this.billboardFaces != null) {
			for (j in 0 until this.billboardCount) {
				val glBillboardFace: GlBillboardFace = this.billboardFaces!![j]!!
				val glBillboardAttributes: BillboardSurfaceGL = this.billboardAttributes!![j]!!
				glBillboardAttributes.diffuseColor = (ColorUtil.hslToRgb!![this.facesColor!![glBillboardFace.face].toInt() and 0xFFFF] and 0xFFFFFF) or (glBillboardAttributes.diffuseColor and 0xFF000000.toInt())
			}
		}
		if (this.colorsBuffer != null) {
			this.colorsBuffer!!.buffer = null
			return
		}
	}

	private fun upload(checkFlags: Boolean) {
		var uploadColours: Boolean = this.colorsBuffer != null && this.colorsBuffer!!.buffer == null
		var uploadNormals: Boolean = this.normalsBuffer != null && this.normalsBuffer!!.buffer == null
		var uploadVertices: Boolean = this.vertexBuffer != null && this.vertexBuffer!!.buffer == null
		var uploadTexCoords: Boolean = this.textureCoordinatesBuffer != null && this.textureCoordinatesBuffer!!.buffer == null
		if (checkFlags) {
			uploadTexCoords = uploadTexCoords and ((this.renderState.toInt() and 0x8) != 0)
			uploadNormals = uploadNormals and ((this.renderState.toInt() and 0x4) != 0)
			uploadColours = uploadColours and ((this.renderState.toInt() and 0x2) != 0)
			uploadVertices = uploadVertices and ((this.renderState.toInt() and 0x1) != 0)
		}
		var stride: Byte = 0
		var coloursOffset: Byte = 0
		var normalsOffset: Byte = 0
		if (uploadVertices) {
			stride = 12
		}
		var texCoordsOffset: Byte = 0
		if (uploadColours) {
			coloursOffset = stride
			stride = (stride + 4).toByte()
		}
		if (uploadNormals) {
			normalsOffset = stride
			stride = (stride + 12).toByte()
		}
		if (uploadTexCoords) {
			texCoordsOffset = stride
			stride = (stride + 8).toByte()
		}
		if (stride.toInt() == 0) {
			return
		}
		if (this.glToolkit!!.textureBuffer.buffer!!.size < this.numNormals * stride) {
			this.glToolkit.textureBuffer = GlBitBuffer(stride * (this.numNormals + 100))
		} else {
			this.glToolkit.textureBuffer.currentPosition = 0
		}
		val gpuPacket: GlBitBuffer = this.glToolkit.textureBuffer
		var local193: Int
		var local202: Int
		var local230: Int
		var local239: Int
		if (uploadVertices) {
			var yBits: Int
			var zBits: Int
			var start: Int
			var end: Int
			if (this.glToolkit.bigEndianess) {
				for (vertexIndex in 0 until this.maxDepth) {
					val xBits: Int = Stream.floatToRawIntBits(this.verticesX!![vertexIndex].toFloat())
					yBits = Stream.floatToRawIntBits(this.verticesY!![vertexIndex].toFloat())
					zBits = Stream.floatToRawIntBits(this.verticesZ!![vertexIndex].toFloat())
					start = this.triangles!![vertexIndex]
					end = this.triangles!![vertexIndex + 1]
					for (index in start until end) {
						val normalIndex: Int = this.vertexNormalIndices!![index] - 1
						if (normalIndex == -1) {
							break
						}
						gpuPacket.currentPosition = stride * normalIndex
						gpuPacket.writeInt(xBits)
						gpuPacket.writeInt(yBits)
						gpuPacket.writeInt(zBits)
					}
				}
			} else {
				for (vertexIndex in 0 until this.maxDepth) {
					val xBits: Int = Stream.floatToRawIntBits(this.verticesX!![vertexIndex].toFloat())
					yBits = Stream.floatToRawIntBits(this.verticesY!![vertexIndex].toFloat())
					zBits = Stream.floatToRawIntBits(this.verticesZ!![vertexIndex].toFloat())
					start = this.triangles!![vertexIndex]
					end = this.triangles!![vertexIndex + 1]
					for (index in start until end) {
						val normalIndex: Int = this.vertexNormalIndices!![index] - 1
						if (normalIndex == -1) {
							break
						}
						gpuPacket.currentPosition = stride * normalIndex
						gpuPacket.writeIntLE(xBits)
						gpuPacket.writeIntLE(yBits)
						gpuPacket.writeIntLE(zBits)
					}
				}
			}
		}
		var local487: Float
		var srcNormalsX: ShortArray?
		var srcNormalsY: ShortArray?
		var srcNormalsZ: ShortArray?
		var magnitude: ByteArray?
		var local528: Float
		if (uploadColours) {
			if (this.normalsBuffer == null) {
				if (this.mergedNormals == null) {
					srcNormalsZ = this.normalsZ
					magnitude = this.magnitudes
					srcNormalsY = this.normalsY
					srcNormalsX = this.normalsX
				} else {
					srcNormalsX = this.mergedNormals!!.normalsX
					srcNormalsZ = this.mergedNormals!!.normalsZ
					srcNormalsY = this.mergedNormals!!.normalsY
					magnitude = this.mergedNormals!!.magnitude
				}
				val sunX: Float = this.glToolkit.sunDirection[0]
				val sunY: Float = this.glToolkit.sunDirection[1]
				val sunZ: Float = this.glToolkit.sunDirection[2]
				val sunBrightness: Float = this.glToolkit.sunBrightness
				val lightScale: Float = this.glToolkit.sunColorIntensity * 768.0f / this.contrast.toFloat()
				val antiLightScale: Float = this.glToolkit.antiSunColorIntensity * 768.0f / this.contrast.toFloat()
				for (i in 0 until this.numFaces) {
					val color: Int = this.hslToRgb(this.ambient.toInt(), this.facesAlpha!![i], this.facesMaterial!![i], this.facesColor!![i].toInt())
					val baseRed: Float = this.glToolkit.sunColorRed * (color ushr 24).toFloat()
					val baseBlue: Float = ((color shr 8) and 0xFF).toFloat() * this.glToolkit.sunColorBlue
					val baseGreen: Float = ((color shr 16) and 0xFF).toFloat() * this.glToolkit.sunColorGreen
					var normalIndex: Short = this.facesA!![i]
					var normalMagnitude: Short = magnitude!![normalIndex.toInt()].toShort()
					var lightDot: Float
					if (normalMagnitude.toInt() == 0) {
						lightDot = (srcNormalsZ!![normalIndex.toInt()].toFloat() * sunZ + sunX * srcNormalsX!![normalIndex.toInt()].toFloat() + srcNormalsY!![normalIndex.toInt()].toFloat() * sunY) * 0.0026041667f
					} else {
						lightDot = (sunY * srcNormalsY!![normalIndex.toInt()].toFloat() + sunX * srcNormalsX!![normalIndex.toInt()].toFloat() + sunZ * srcNormalsZ!![normalIndex.toInt()].toFloat()) / (normalMagnitude * 256).toFloat()
					}
					var intensity: Float = sunBrightness + lightDot * (if (lightDot < 0.0f) antiLightScale else lightScale)
					var red: Int = (intensity * baseRed).toInt()
					var green: Int = (intensity * baseGreen).toInt()
					if (red < 0) {
						red = 0
					} else if (red > 255) {
						red = 255
					}
					var blue: Int = (intensity * baseBlue).toInt()
					if (green < 0) {
						green = 0
					} else if (green > 255) {
						green = 255
					}
					gpuPacket.currentPosition = normalIndex * stride + coloursOffset
					if (blue < 0) {
						blue = 0
					} else if (blue > 255) {
						blue = 255
					}
					gpuPacket.writeByte(red)
					gpuPacket.writeByte(green)
					gpuPacket.writeByte(blue)
					gpuPacket.writeByte(255 - (this.facesAlpha!![i].toInt() and 0xFF))
					normalIndex = this.facesB!![i]
					normalMagnitude = magnitude[normalIndex.toInt()].toShort()
					if (normalMagnitude.toInt() == 0) {
						lightDot = (sunX * srcNormalsX!![normalIndex.toInt()].toFloat() + srcNormalsY!![normalIndex.toInt()].toFloat() * sunY + sunZ * srcNormalsZ!![normalIndex.toInt()].toFloat()) * 0.0026041667f
					} else {
						lightDot = (srcNormalsZ!![normalIndex.toInt()].toFloat() * sunZ + sunX * srcNormalsX!![normalIndex.toInt()].toFloat() + srcNormalsY!![normalIndex.toInt()].toFloat() * sunY) / (normalMagnitude * 256).toFloat()
					}
					intensity = sunBrightness + lightDot * (if (lightDot < 0.0f) antiLightScale else lightScale)
					red = (baseRed * intensity).toInt()
					green = (intensity * baseGreen).toInt()
					if (red < 0) {
						red = 0
					} else if (red > 255) {
						red = 255
					}
					if (green < 0) {
						green = 0
					} else if (green > 255) {
						green = 255
					}
					blue = (intensity * baseBlue).toInt()
					gpuPacket.currentPosition = coloursOffset + normalIndex * stride
					if (blue < 0) {
						blue = 0
					} else if (blue > 255) {
						blue = 255
					}
					gpuPacket.writeByte(red)
					gpuPacket.writeByte(green)
					gpuPacket.writeByte(blue)
					gpuPacket.writeByte(255 - (this.facesAlpha!![i].toInt() and 0xFF))
					normalIndex = this.facesC!![i]
					normalMagnitude = magnitude[normalIndex.toInt()].toShort()
					if (normalMagnitude.toInt() == 0) {
						lightDot = (srcNormalsZ!![normalIndex.toInt()].toFloat() * sunZ + srcNormalsY!![normalIndex.toInt()].toFloat() * sunY + sunX * srcNormalsX!![normalIndex.toInt()].toFloat()) * 0.0026041667f
					} else {
						lightDot = (srcNormalsZ!![normalIndex.toInt()].toFloat() * sunZ + srcNormalsY!![normalIndex.toInt()].toFloat() * sunY + srcNormalsX!![normalIndex.toInt()].toFloat() * sunX) / (normalMagnitude * 256).toFloat()
					}
					intensity = sunBrightness + (if (lightDot < 0.0f) antiLightScale else lightScale) * lightDot
					red = (intensity * baseRed).toInt()
					green = (intensity * baseGreen).toInt()
					if (red < 0) {
						red = 0
					} else if (red > 255) {
						red = 255
					}
					if (green < 0) {
						green = 0
					} else if (green > 255) {
						green = 255
					}
					blue = (baseBlue * intensity).toInt()
					if (blue < 0) {
						blue = 0
					} else if (blue > 255) {
						blue = 255
					}
					gpuPacket.currentPosition = stride * normalIndex + coloursOffset
					gpuPacket.writeByte(red)
					gpuPacket.writeByte(green)
					gpuPacket.writeByte(blue)
					gpuPacket.writeByte(255 - (this.facesAlpha!![i].toInt() and 0xFF))
				}
			} else {
				for (faceIndex in 0 until this.numFaces) {
					val color: Int = this.hslToRgb(this.ambient.toInt(), this.facesAlpha!![faceIndex], this.facesMaterial!![faceIndex], this.facesColor!![faceIndex].toInt())
					gpuPacket.currentPosition = coloursOffset + this.facesA!![faceIndex] * stride
					gpuPacket.writeInt(color)
					gpuPacket.currentPosition = coloursOffset + this.facesB!![faceIndex] * stride
					gpuPacket.writeInt(color)
					gpuPacket.currentPosition = coloursOffset + stride * this.facesC!![faceIndex]
					gpuPacket.writeInt(color)
				}
			}
		}
		if (uploadNormals) {
			if (this.mergedNormals == null) {
				srcNormalsX = this.normalsX
				srcNormalsY = this.normalsY
				magnitude = this.magnitudes
				srcNormalsZ = this.normalsZ
			} else {
				magnitude = this.mergedNormals!!.magnitude
				srcNormalsY = this.mergedNormals!!.normalsY
				srcNormalsZ = this.mergedNormals!!.normalsZ
				srcNormalsX = this.mergedNormals!!.normalsX
			}
			val normalScale: Float = 3.0f / this.contrast.toFloat()
			gpuPacket.currentPosition = normalsOffset.toInt()
			val defaultNormalScale: Float = 3.0f / (this.contrast / 2 + this.contrast).toFloat()
			if (this.glToolkit.bigEndianess) {
				for (normalIndex in 0 until this.numNormals) {
					val normalMagnitude: Int = magnitude!![normalIndex].toInt() and 0xFF
					if (normalMagnitude == 0) {
						gpuPacket.writeFloatBigEndian(defaultNormalScale * srcNormalsX!![normalIndex].toFloat())
						gpuPacket.writeFloatBigEndian(srcNormalsY!![normalIndex].toFloat() * defaultNormalScale)
						gpuPacket.writeFloatBigEndian(defaultNormalScale * srcNormalsZ!![normalIndex].toFloat())
					} else {
						val magnitudeScale: Float = normalScale / normalMagnitude.toFloat()
						gpuPacket.writeFloatBigEndian(srcNormalsX!![normalIndex].toFloat() * magnitudeScale)
						gpuPacket.writeFloatBigEndian(magnitudeScale * srcNormalsY!![normalIndex].toFloat())
						gpuPacket.writeFloatBigEndian(srcNormalsZ!![normalIndex].toFloat() * magnitudeScale)
					}
					gpuPacket.currentPosition += stride - 12
				}
			} else {
				for (normalIndex in 0 until this.numNormals) {
					val normalMagnitude: Int = magnitude!![normalIndex].toInt() and 0xFF
					if (normalMagnitude == 0) {
						gpuPacket.writeFloatLittleEndian(srcNormalsX!![normalIndex].toFloat() * defaultNormalScale)
						gpuPacket.writeFloatLittleEndian(srcNormalsY!![normalIndex].toFloat() * defaultNormalScale)
						gpuPacket.writeFloatLittleEndian(defaultNormalScale * srcNormalsZ!![normalIndex].toFloat())
					} else {
						val magnitudeScale: Float = normalScale / normalMagnitude.toFloat()
						gpuPacket.writeFloatLittleEndian(srcNormalsX!![normalIndex].toFloat() * magnitudeScale)
						gpuPacket.writeFloatLittleEndian(magnitudeScale * srcNormalsY!![normalIndex].toFloat())
						gpuPacket.writeFloatLittleEndian(magnitudeScale * srcNormalsZ!![normalIndex].toFloat())
					}
					gpuPacket.currentPosition += stride - 12
				}
			}
		}
		if (uploadTexCoords) {
			gpuPacket.currentPosition = texCoordsOffset.toInt()
			if (this.glToolkit.bigEndianess) {
				for (normalIndex in 0 until this.numNormals) {
					gpuPacket.writeFloatBigEndian(this.textureU!![normalIndex])
					gpuPacket.writeFloatBigEndian(this.textureV!![normalIndex])
					gpuPacket.currentPosition += stride - 8
				}
			} else {
				for (normalIndex in 0 until this.numNormals) {
					gpuPacket.writeFloatLittleEndian(this.textureU!![normalIndex])
					gpuPacket.writeFloatLittleEndian(this.textureV!![normalIndex])
					gpuPacket.currentPosition += stride - 8
				}
			}
		}
		gpuPacket.currentPosition = this.numNormals * stride
		val arrayBuffer: ArrayBufferGL?
		if (checkFlags) {
			// 634HD: a range of the frame's stream buffer (scene/HdDynamicStream), not a buffer of its own
			if (this.dynamicArrayBuffer == null) {
				this.dynamicArrayBuffer = this.glToolkit.vertexStream.VertexRange()
			}
			this.dynamicArrayBuffer!!.putArray(stride.toInt(), gpuPacket.currentPosition, gpuPacket.buffer)
			arrayBuffer = this.dynamicArrayBuffer
			this.renderState = 0
		} else {
			arrayBuffer = this.glToolkit.createArrayBuffer(false, stride.toInt(), gpuPacket.buffer, gpuPacket.currentPosition)
			this.updateRequired = true
		}
		if (uploadVertices) {
			this.vertexBuffer!!.offset = 0
			this.vertexBuffer!!.buffer = arrayBuffer
		}
		if (uploadTexCoords) {
			this.textureCoordinatesBuffer!!.offset = texCoordsOffset
			this.textureCoordinatesBuffer!!.buffer = arrayBuffer
		}
		if (uploadColours) {
			this.colorsBuffer!!.offset = coloursOffset
			this.colorsBuffer!!.buffer = arrayBuffer
		}
		if (uploadNormals) {
			this.normalsBuffer!!.offset = normalsOffset
			this.normalsBuffer!!.buffer = arrayBuffer
		}
	}

	override fun render(matrix: Matrix?, cylinder: BoundingBoxPolygon?, flags: Int) {
		if (this.numNormals == 0) {
			return
		}
		val capturing: HdSceneRenderer? = HdSceneRenderer.capturing
		if (capturing != null) {
			// 634HD retained scene: a static entity drawn once at build time, to learn its model
			capturing.capture(this)
			return
		}
		val viewMatrix: Matrix43 = this.glToolkit!!.viewMatrix43
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		val modelMatrix: Matrix43? = matrix as Matrix43?
		HdModel.modelViewZY = modelMatrix!!.zB * viewMatrix.zC + modelMatrix.xB * viewMatrix.zA + modelMatrix.yB * viewMatrix.zB
		HdModel.modelViewZW = viewMatrix.zD + viewMatrix.zA * modelMatrix.xD + modelMatrix.yD * viewMatrix.zB + modelMatrix.zD * viewMatrix.zC
		val depthAtMinY: Float = HdModel.modelViewZW + HdModel.modelViewZY * this.minY.toFloat()
		val depthAtMaxY: Float = HdModel.modelViewZY * this.maxY.toFloat() + HdModel.modelViewZW
		val nearDepth: Float
		val farDepth: Float
		if (depthAtMinY > depthAtMaxY) {
			nearDepth = depthAtMaxY - this.size2d.toFloat()
			farDepth = depthAtMinY + this.size2d.toFloat()
		} else {
			nearDepth = (-this.size2d).toFloat() + depthAtMinY
			farDepth = depthAtMaxY + this.size2d.toFloat()
		}
		if (this.glToolkit.projectedFarClipDistance <= nearDepth || this.glToolkit.zNear.toFloat() >= farDepth) {
			return
		}
		HdModel.modelViewXY = viewMatrix.xB * modelMatrix.yB + modelMatrix.xB * viewMatrix.xA + viewMatrix.xC * modelMatrix.zB
		HdModel.modelViewXW = viewMatrix.xD + viewMatrix.xC * modelMatrix.zD + viewMatrix.xA * modelMatrix.xD + modelMatrix.yD * viewMatrix.xB
		val xAtMinY: Float = this.minY.toFloat() * HdModel.modelViewXY + HdModel.modelViewXW
		val xAtMaxY: Float = this.maxY.toFloat() * HdModel.modelViewXY + HdModel.modelViewXW
		val screenLeft: Float
		val screenRight: Float
		if (xAtMinY > xAtMaxY) {
			screenLeft = this.glToolkit.projectionScaleX.toFloat() * ((-this.size2d).toFloat() + xAtMaxY)
			screenRight = this.glToolkit.projectionScaleX.toFloat() * (this.size2d.toFloat() + xAtMinY)
		} else {
			screenRight = this.glToolkit.projectionScaleX.toFloat() * (xAtMaxY + this.size2d.toFloat())
			screenLeft = ((-this.size2d).toFloat() + xAtMinY) * this.glToolkit.projectionScaleX.toFloat()
		}
		if (this.glToolkit.viewX2 <= screenLeft / farDepth || screenRight / farDepth <= this.glToolkit.viewX1) {
			return
		}
		HdModel.modelViewYW = modelMatrix.yD * viewMatrix.yB + modelMatrix.xD * viewMatrix.yA + viewMatrix.yC * modelMatrix.zD + viewMatrix.yD
		HdModel.modelViewYY = viewMatrix.yA * modelMatrix.xB + viewMatrix.yB * modelMatrix.yB + viewMatrix.yC * modelMatrix.zB
		val yAtMinY: Float = HdModel.modelViewYW + HdModel.modelViewYY * this.minY.toFloat()
		val yAtMaxY: Float = HdModel.modelViewYW + this.maxY.toFloat() * HdModel.modelViewYY
		val screenTop: Float
		val screenBottom: Float
		if (yAtMinY > yAtMaxY) {
			screenBottom = (this.size2d.toFloat() + yAtMinY) * this.glToolkit.projectionScaleY.toFloat()
			screenTop = this.glToolkit.projectionScaleY.toFloat() * (yAtMaxY - this.size2d.toFloat())
		} else {
			screenBottom = (this.size2d.toFloat() + yAtMaxY) * this.glToolkit.projectionScaleY.toFloat()
			screenTop = this.glToolkit.projectionScaleY.toFloat() * (yAtMinY - this.size2d.toFloat())
		}
		if (screenTop / farDepth >= this.glToolkit.viewY2 || this.glToolkit.viewY1 >= screenBottom / farDepth) {
			return
		}
		if (cylinder != null || this.billboardFaces != null) {
			HdModel.modelViewZZ = modelMatrix.yC * viewMatrix.zB + modelMatrix.xC * viewMatrix.zA + modelMatrix.zC * viewMatrix.zC
			HdModel.modelViewZX = viewMatrix.zC * modelMatrix.zA + modelMatrix.xA * viewMatrix.zA + viewMatrix.zB * modelMatrix.yA
			HdModel.modelViewXZ = viewMatrix.xC * modelMatrix.zC + modelMatrix.yC * viewMatrix.xB + modelMatrix.xC * viewMatrix.xA
			HdModel.modelViewYX = viewMatrix.yA * modelMatrix.xA + modelMatrix.yA * viewMatrix.yB + modelMatrix.zA * viewMatrix.yC
			HdModel.modelViewXX = modelMatrix.yA * viewMatrix.xB + viewMatrix.xA * modelMatrix.xA + viewMatrix.xC * modelMatrix.zA
			HdModel.modelViewYZ = modelMatrix.zC * viewMatrix.yC + viewMatrix.yB * modelMatrix.yC + viewMatrix.yA * modelMatrix.xC
		}
		if (cylinder != null) {
			var clipped: Boolean = false
			var visible: Boolean = true
			val centerX: Int = (this.minX + this.maxX) shr 1
			val centerZ: Int = (this.minZ + this.maxZ) shr 1
			val bottomX: Int = (centerZ.toFloat() * HdModel.modelViewXZ + HdModel.modelViewXX * centerX.toFloat() + HdModel.modelViewXW + this.minY.toFloat() * HdModel.modelViewXY).toInt()
			val bottomY: Int = (HdModel.modelViewYW + centerX.toFloat() * HdModel.modelViewYX + this.minY.toFloat() * HdModel.modelViewYY + centerZ.toFloat() * HdModel.modelViewYZ).toInt()
			val bottomZ: Int = (HdModel.modelViewZX * centerX.toFloat() + HdModel.modelViewZW + HdModel.modelViewZY * this.minY.toFloat() + HdModel.modelViewZZ * centerZ.toFloat()).toInt()
			if (bottomZ >= this.glToolkit.zNear) {
				cylinder.left = bottomX * this.glToolkit.projectionScaleX / bottomZ + this.glToolkit.projectionCenterX
				cylinder.bottom = bottomY * this.glToolkit.projectionScaleY / bottomZ + this.glToolkit.projectionCenterY
			} else {
				clipped = true
			}
			val topX: Int = (HdModel.modelViewXW + HdModel.modelViewXX * centerX.toFloat() + HdModel.modelViewXY * this.maxY.toFloat() + HdModel.modelViewXZ * centerZ.toFloat()).toInt()
			val topY: Int = (centerZ.toFloat() * HdModel.modelViewYZ + HdModel.modelViewYY * this.maxY.toFloat() + HdModel.modelViewYW + HdModel.modelViewYX * centerX.toFloat()).toInt()
			val topZ: Int = (HdModel.modelViewZZ * centerZ.toFloat() + centerX.toFloat() * HdModel.modelViewZX + HdModel.modelViewZW + this.maxY.toFloat() * HdModel.modelViewZY).toInt()
			if (topZ >= this.glToolkit.zNear) {
				cylinder.right = topX * this.glToolkit.projectionScaleX / topZ + this.glToolkit.projectionCenterX
				cylinder.top = topY * this.glToolkit.projectionScaleY / topZ + this.glToolkit.projectionCenterY
			} else {
				clipped = true
			}
			if (clipped) {
				if (this.glToolkit.zNear > bottomZ && topZ < this.glToolkit.zNear) {
					visible = false
				} else {
					var clipRatio: Int
					var clippedX: Int
					var clippedY: Int
					if (this.glToolkit.zNear > bottomZ) {
						clipRatio = ((topZ - this.glToolkit.zNear) shl 16) / (topZ - bottomZ)
						clippedX = (((topX - bottomX) * clipRatio) shr 16) + topX
						cylinder.left = this.glToolkit.projectionCenterX + clippedX * this.glToolkit.projectionScaleX / this.glToolkit.zNear
						clippedY = ((clipRatio * (topY - bottomY)) shr 16) + topY
						cylinder.bottom = clippedY * this.glToolkit.projectionScaleY / this.glToolkit.zNear + this.glToolkit.projectionCenterY
					} else if (this.glToolkit.zNear > topZ) {
						clipRatio = ((bottomZ - this.glToolkit.zNear) shl 16) / (bottomZ - topZ)
						clippedX = bottomX + ((clipRatio * (bottomX - topX)) shr 16)
						clippedY = (((bottomY - topY) * clipRatio) shr 16) + bottomY
						cylinder.left = clippedX * this.glToolkit.projectionScaleX / this.glToolkit.zNear + this.glToolkit.projectionCenterX
						cylinder.bottom = this.glToolkit.projectionCenterY + this.glToolkit.projectionScaleY * clippedY / this.glToolkit.zNear
					}
				}
			}
			if (visible) {
				if (topZ < bottomZ) {
					cylinder.size = this.glToolkit.projectionCenterX + this.glToolkit.projectionScaleX * (bottomX + this.size2d) / bottomZ - cylinder.left
				} else {
					cylinder.size = this.glToolkit.projectionCenterX + this.glToolkit.projectionScaleX * (this.size2d + topX) / topZ - cylinder.right
				}
				cylinder.positionCalculated = true
			}
		}
		this.glToolkit.setupMatrixRendering()
		val retained: BooleanArray? = this.retainedBatches
		if (retained != null && this.retainedObject >= 0 && this.glToolkit.sceneRetained()) {
			// 634HD retained scene: the batches it holds draw with it after the opaque entities
			this.glToolkit.sceneRenderer.markVisible(this.retainedObject, modelMatrix.getColumnMajor(), this.normalsBuffer != null)
		}
		this.glToolkit.multiMatrix(modelMatrix)
		RenderStats.category = RenderStats.CATEGORY_MODEL
		this.drawOpaqueFaces()
		RenderStats.category = RenderStats.CATEGORY_OTHER
		this.glToolkit.popMatrix()
		this.renderBillboards()
	}

	/**
	 * 634HD retained scene: makes sure the model's buffers are uploaded and hands them to [scene] as
	 * object [objectId]. False when no batch of it can be retained.
	 */
	fun exportForScene(scene: HdSceneRenderer, objectId: Int): Boolean {
		val offsets: IntArray = this.sceneBatchOffsets ?: return false
		val materials: IntArray = this.sceneBatchMaterials ?: return false
		if (this.renderedFaceCount == 0 || this.numNormals == 0) {
			return false
		}
		if (this.renderState.toInt() != 0) {
			this.upload(true)
		}
		this.upload(false)
		val elements: ModelElementsBufferGL = this.modelElementsBuffer ?: return false
		if (elements.openglElementsBuffer == null) {
			this.buildElementsBuffer((this.renderState.toInt() and 0x10) != 0)
		}
		val elementBuffer: ElementsBufferGL = elements.openglElementsBuffer ?: return false
		val positions: OpenGlArrayDrawBuffer = this.vertexBuffer ?: return false
		val colours: OpenGlArrayDrawBuffer = this.colorsBuffer ?: return false
		val taken: BooleanArray = scene.appendModel(objectId, this.numNormals, positions, colours, this.normalsBuffer, this.textureCoordinatesBuffer, elementBuffer, this.renderedFaceCount, offsets, materials) ?: return false
		this.retainedObject = objectId
		this.retainedBatches = taken
		this.releaseUploadedData()
		return true
	}

	override fun WA(): Int {
		return this.ambient.toInt()
	}

	private fun releaseUploadedData() {
		if (!this.updateRequired) {
			return
		}
		this.updateRequired = false
		if (this.emissiveTriangles == null && this.effectiveVertices == null && this.billboardFaces == null) {
			if (this.verticesX != null && !HdModel.retainsVerticesX(this.functionMask, this.preferenceFlags)) {
				if (this.vertexBuffer != null && this.vertexBuffer!!.buffer == null) {
					this.updateRequired = true
				} else {
					if (!this.boundsCalculated) {
						this.calculateBounds()
					}
					this.verticesX = null
				}
			}
			if (this.verticesY != null && !HdModel.retainsVerticesY(this.preferenceFlags, this.functionMask)) {
				if (this.vertexBuffer != null && this.vertexBuffer!!.buffer == null) {
					this.updateRequired = true
				} else {
					if (!this.boundsCalculated) {
						this.calculateBounds()
					}
					this.verticesY = null
				}
			}
			if (this.verticesZ != null && !HdModel.retainsVerticesZ(this.functionMask, this.preferenceFlags)) {
				if (this.vertexBuffer != null && this.vertexBuffer!!.buffer == null) {
					this.updateRequired = true
				} else {
					if (!this.boundsCalculated) {
						this.calculateBounds()
					}
					this.verticesZ = null
				}
			}
		}
		if (this.vertexNormalIndices != null && this.verticesX == null && this.verticesY == null && this.verticesZ == null) {
			this.vertexNormalIndices = null
			this.triangles = null
		}
		if (this.magnitudes != null && !HdModel.retainsNormals(this.functionMask, this.preferenceFlags)) {
			if (this.normalsBuffer == null) {
				if (this.colorsBuffer != null && this.colorsBuffer!!.buffer == null) {
					this.updateRequired = true
				} else {
					this.magnitudes = null
					this.normalsZ = null
					this.normalsY = null
					this.normalsX = null
				}
			} else if (this.normalsBuffer!!.buffer == null) {
				this.updateRequired = true
			} else {
				this.magnitudes = null
				this.normalsZ = null
				this.normalsY = null
				this.normalsX = null
			}
		}
		if (this.facesColor != null && !HdModel.retainsFaceColors(this.functionMask, this.preferenceFlags)) {
			if (this.colorsBuffer != null && this.colorsBuffer!!.buffer == null) {
				this.updateRequired = true
			} else {
				this.facesColor = null
			}
		}
		if (this.facesAlpha != null && !HdModel.retainsFaceAlphas(this.preferenceFlags, this.functionMask)) {
			if (this.colorsBuffer != null && this.colorsBuffer!!.buffer == null) {
				this.updateRequired = true
			} else {
				this.facesAlpha = null
			}
		}
		if (this.textureU != null && !HdModel.retainsTextureCoordinates(this.functionMask, this.preferenceFlags)) {
			if (this.textureCoordinatesBuffer != null && this.textureCoordinatesBuffer!!.buffer == null) {
				this.updateRequired = true
			} else {
				this.textureV = null
				this.textureU = null
			}
		}
		if (this.facesMaterial != null && !HdModel.retainsFaceMaterials(this.preferenceFlags, this.functionMask)) {
			if (this.colorsBuffer != null && this.colorsBuffer!!.buffer == null) {
				this.updateRequired = true
			} else {
				this.facesMaterial = null
			}
		}
		if (this.facesA != null && !HdModel.retainsFaceIndices(this.functionMask, this.preferenceFlags)) {
			if ((this.modelElementsBuffer == null || this.modelElementsBuffer!!.openglElementsBuffer != null) && (this.colorsBuffer == null || this.colorsBuffer!!.buffer != null)) {
				this.facesC = null
				this.facesB = null
				this.facesA = null
			} else {
				this.updateRequired = true
			}
		}
		if (this.faceGroups != null && !HdModel.altersFaces(this.functionMask, this.preferenceFlags)) {
			this.facesSubmeshes = null
			this.faceGroups = null
		}
		if (this.vertexGroups != null && !HdModel.altersVertices(this.preferenceFlags, this.functionMask)) {
			this.vertexGroups = null
			this.verticesSubmeshes = null
		}
		if (this.billboardGroups != null && !HdModel.altersBillboards(this.functionMask, this.preferenceFlags)) {
			this.billboardGroups = null
		}
		if (this.materialBatchOffsets != null && (this.functionMask and 0x800) == 0 && (this.functionMask and 0x40000) == 0) {
			this.materialBatchOffsets = null
		}
	}

	override fun unlock() {
	}

	override fun ia(src: Short, dst: Short) {
		for (i in 0 until this.numFaces) {
			if (src == this.facesColor!![i]) {
				this.facesColor!![i] = dst
			}
		}
		if (this.billboardFaces != null) {
			for (j in 0 until this.billboardCount) {
				val glBillboardFace: GlBillboardFace = this.billboardFaces!![j]!!
				val glBillboardAttributes: BillboardSurfaceGL = this.billboardAttributes!![j]!!
				glBillboardAttributes.diffuseColor = (ColorUtil.hslToRgb!![this.facesColor!![glBillboardFace.face].toInt() and 0xFFFF] and 0xFFFFFF) or (glBillboardAttributes.diffuseColor and 0xFF000000.toInt())
			}
		}
		if (this.colorsBuffer != null) {
			this.colorsBuffer!!.buffer = null
		}
	}

	override fun getEmissiveTriangles(): Array<EmissiveTriangle?>? {
		return this.emissiveTriangles
	}

	override fun getEffectiveVertices(): Array<EffectiveVertex?>? {
		return this.effectiveVertices
	}

	override fun V(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.minX.toInt()
	}

	override fun r(): Boolean {
		return this.textureAnimating
	}

	override fun updateParticlePosition(matrix: Matrix?) {
		val modelMatrix: Matrix43? = matrix as Matrix43?
		var local13: Int
		if (this.emissiveTriangles != null) {
			for (emitterIndex in 0 until this.emissiveTriangles!!.size) {
				val modelParticleEmitter: EmissiveTriangle = this.emissiveTriangles!![emitterIndex]!!
				var modelParticleEmitter2: EmissiveTriangle? = modelParticleEmitter
				if (modelParticleEmitter.child != null) {
					modelParticleEmitter2 = modelParticleEmitter.child
				}
				modelParticleEmitter2!!.verticeZC = (modelMatrix!!.xD + this.verticesY!![modelParticleEmitter.faceA].toFloat() * modelMatrix.xB + modelMatrix.xA * this.verticesX!![modelParticleEmitter.faceA].toFloat() + this.verticesZ!![modelParticleEmitter.faceA].toFloat() * modelMatrix.xC).toInt()
				modelParticleEmitter2!!.verticeXC = (modelMatrix.yD + this.verticesY!![modelParticleEmitter.faceA].toFloat() * modelMatrix.yB + modelMatrix.yA * this.verticesX!![modelParticleEmitter.faceA].toFloat() + this.verticesZ!![modelParticleEmitter.faceA].toFloat() * modelMatrix.yC).toInt()
				modelParticleEmitter2!!.verticeYA = (modelMatrix.zD + this.verticesX!![modelParticleEmitter.faceA].toFloat() * modelMatrix.zA + modelMatrix.zB * this.verticesY!![modelParticleEmitter.faceA].toFloat() + this.verticesZ!![modelParticleEmitter.faceA].toFloat() * modelMatrix.zC).toInt()
				modelParticleEmitter2!!.verticeZA = (modelMatrix.xD + modelMatrix.xA * this.verticesX!![modelParticleEmitter.faceB].toFloat() + this.verticesY!![modelParticleEmitter.faceB].toFloat() * modelMatrix.xB + this.verticesZ!![modelParticleEmitter.faceB].toFloat() * modelMatrix.xC).toInt()
				modelParticleEmitter2!!.verticeXA = (modelMatrix.yD + modelMatrix.yC * this.verticesZ!![modelParticleEmitter.faceB].toFloat() + this.verticesX!![modelParticleEmitter.faceB].toFloat() * modelMatrix.yA + this.verticesY!![modelParticleEmitter.faceB].toFloat() * modelMatrix.yB).toInt()
				modelParticleEmitter2!!.verticeZB = (modelMatrix.zD + modelMatrix.zC * this.verticesZ!![modelParticleEmitter.faceB].toFloat() + modelMatrix.zB * this.verticesY!![modelParticleEmitter.faceB].toFloat() + this.verticesX!![modelParticleEmitter.faceB].toFloat() * modelMatrix.zA).toInt()
				modelParticleEmitter2!!.verticeYB = (modelMatrix.xD + this.verticesY!![modelParticleEmitter.vertexC].toFloat() * modelMatrix.xB + this.verticesX!![modelParticleEmitter.vertexC].toFloat() * modelMatrix.xA + modelMatrix.xC * this.verticesZ!![modelParticleEmitter.vertexC].toFloat()).toInt()
				modelParticleEmitter2!!.transformedCY = (modelMatrix.yD + this.verticesZ!![modelParticleEmitter.vertexC].toFloat() * modelMatrix.yC + modelMatrix.yA * this.verticesX!![modelParticleEmitter.vertexC].toFloat() + modelMatrix.yB * this.verticesY!![modelParticleEmitter.vertexC].toFloat()).toInt()
				modelParticleEmitter2!!.verticeYC = (modelMatrix.zB * this.verticesY!![modelParticleEmitter.vertexC].toFloat() + modelMatrix.zA * this.verticesX!![modelParticleEmitter.vertexC].toFloat() + this.verticesZ!![modelParticleEmitter.vertexC].toFloat() * modelMatrix.zC + modelMatrix.zD).toInt()
			}
		}
		if (this.effectiveVertices == null) {
			return
		}
		for (effectorIndex in 0 until this.effectiveVertices!!.size) {
			val modelParticleEffector: EffectiveVertex = this.effectiveVertices!![effectorIndex]!!
			var modelParticleEffector2: EffectiveVertex? = modelParticleEffector
			if (modelParticleEffector.child != null) {
				modelParticleEffector2 = modelParticleEffector.child
			}
			if (modelParticleEffector.matrix == null) {
				modelParticleEffector.matrix = modelMatrix!!.copy()
			} else {
				modelParticleEffector.matrix!!.apply(modelMatrix)
			}
			modelParticleEffector2!!.verticeZ = (modelMatrix!!.xD + this.verticesZ!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.xC + this.verticesX!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.xA + modelMatrix.xB * this.verticesY!![modelParticleEffector.appliesOnVertex].toFloat()).toInt()
			modelParticleEffector2!!.verticeX = (modelMatrix.yD + this.verticesY!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.yB + modelMatrix.yA * this.verticesX!![modelParticleEffector.appliesOnVertex].toFloat() + this.verticesZ!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.yC).toInt()
			modelParticleEffector2!!.verticeY = (modelMatrix.zD + this.verticesZ!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.zC + this.verticesX!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.zA + this.verticesY!![modelParticleEffector.appliesOnVertex].toFloat() * modelMatrix.zB).toInt()
		}
	}

	override fun ma(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.size3d.toInt()
	}

	override fun applyTransformUnmasked(type: Int, labels: IntArray?, x: Int, y: Int, z: Int, rotation: Int, lights: Boolean) {
		val length: Int = labels!!.size
		var local29: Int
		var local35: Int
		var local59: Int
		val scaledX: Int
		val scaledY: Int
		val scaledZ: Int
		var group: Int
		var local53: Int
		var groupVertices: IntArray?
		if (type == 0) {
			scaledZ = z shl 4
			scaledY = y shl 4
			scaledX = x shl 4
			HdModel.originZ = 0
			var vertexCount: Int = 0
			ModelTransformOriginY.originY = 0
			HdModel.originX = 0
			for (labelIndex in 0 until length) {
				group = labels[labelIndex]
				if (group < this.vertexGroups!!.size) {
					groupVertices = this.vertexGroups!![group]
					for (groupMemberIndex in 0 until groupVertices!!.size) {
						val vertex: Int = groupVertices[groupMemberIndex]
						HdModel.originX += this.verticesX!![vertex]
						ModelTransformOriginY.originY += this.verticesY!![vertex]
						vertexCount++
						HdModel.originZ += this.verticesZ!![vertex]
					}
				}
			}
			if (vertexCount <= 0) {
				ModelTransformOriginY.originY = scaledY
				HdModel.originZ = scaledZ
				HdModel.originX = scaledX
			} else {
				ModelTransformOriginY.originY = scaledY + ModelTransformOriginY.originY / vertexCount
				HdModel.originX = HdModel.originX / vertexCount + scaledX
				HdModel.originZ = scaledZ + HdModel.originZ / vertexCount
			}
			return
		}
		var members: IntArray?
		var memberIndex: Int
		if (type == 1) {
			scaledX = x shl 4
			scaledY = y shl 4
			scaledZ = z shl 4
			for (labelIndex in 0 until length) {
				val groupId: Int = labels[labelIndex]
				if (groupId < this.vertexGroups!!.size) {
					members = this.vertexGroups!![groupId]
					memberIndex = 0
					while (memberIndex < members!!.size) {
						val vertex: Int = members[memberIndex]
						this.verticesX!![vertex] += scaledX
						this.verticesY!![vertex] += scaledY
						this.verticesZ!![vertex] += scaledZ
						memberIndex++
					}
				}
			}
			return
		}
		var local269: Int
		var local288: Int
		var local746: Int
		if (type == 2) {
			for (labelIndex in 0 until length) {
				val groupId: Int = labels[labelIndex]
				if (this.vertexGroups!!.size > groupId) {
					members = this.vertexGroups!![groupId]
					if ((rotation and 0x1) == 0) {
						memberIndex = 0
						while (memberIndex < members!!.size) {
							val vertex: Int = members[memberIndex]
							this.verticesX!![vertex] -= HdModel.originX
							this.verticesY!![vertex] -= ModelTransformOriginY.originY
							this.verticesZ!![vertex] -= HdModel.originZ
							if (z != 0) {
								val sin: Int = TextureCombineSource.SINE[z]
								val cos: Int = TextureCombineSource.COSINE[z]
								val rotated: Int = (this.verticesX!![vertex] * cos + sin * this.verticesY!![vertex] + 16383) shr 14
								this.verticesY!![vertex] = (this.verticesY!![vertex] * cos + 16383 - sin * this.verticesX!![vertex]) shr 14
								this.verticesX!![vertex] = rotated
							}
							if (x != 0) {
								val sin: Int = TextureCombineSource.SINE[x]
								val cos: Int = TextureCombineSource.COSINE[x]
								val rotated: Int = (cos * this.verticesY!![vertex] + 16383 - this.verticesZ!![vertex] * sin) shr 14
								this.verticesZ!![vertex] = (cos * this.verticesZ!![vertex] + sin * this.verticesY!![vertex] + 16383) shr 14
								this.verticesY!![vertex] = rotated
							}
							if (y != 0) {
								val sin: Int = TextureCombineSource.SINE[y]
								val cos: Int = TextureCombineSource.COSINE[y]
								val rotated: Int = (sin * this.verticesZ!![vertex] + cos * this.verticesX!![vertex] + 16383) shr 14
								this.verticesZ!![vertex] = (this.verticesZ!![vertex] * cos + 16383 - this.verticesX!![vertex] * sin) shr 14
								this.verticesX!![vertex] = rotated
							}
							this.verticesX!![vertex] += HdModel.originX
							this.verticesY!![vertex] += ModelTransformOriginY.originY
							this.verticesZ!![vertex] += HdModel.originZ
							memberIndex++
						}
					} else {
						memberIndex = 0
						while (memberIndex < members!!.size) {
							val vertex: Int = members[memberIndex]
							this.verticesX!![vertex] -= HdModel.originX
							this.verticesY!![vertex] -= ModelTransformOriginY.originY
							this.verticesZ!![vertex] -= HdModel.originZ
							if (x != 0) {
								val sin: Int = TextureCombineSource.SINE[x]
								val cos: Int = TextureCombineSource.COSINE[x]
								val rotated: Int = (cos * this.verticesY!![vertex] + 16383 - this.verticesZ!![vertex] * sin) shr 14
								this.verticesZ!![vertex] = (this.verticesY!![vertex] * sin + cos * this.verticesZ!![vertex] + 16383) shr 14
								this.verticesY!![vertex] = rotated
							}
							if (z != 0) {
								val sin: Int = TextureCombineSource.SINE[z]
								val cos: Int = TextureCombineSource.COSINE[z]
								val rotated: Int = (this.verticesX!![vertex] * cos + sin * this.verticesY!![vertex] + 16383) shr 14
								this.verticesY!![vertex] = (this.verticesY!![vertex] * cos + 16383 - sin * this.verticesX!![vertex]) shr 14
								this.verticesX!![vertex] = rotated
							}
							if (y != 0) {
								val sin: Int = TextureCombineSource.SINE[y]
								val cos: Int = TextureCombineSource.COSINE[y]
								val rotated: Int = (sin * this.verticesZ!![vertex] + this.verticesX!![vertex] * cos + 16383) shr 14
								this.verticesZ!![vertex] = (this.verticesZ!![vertex] * cos + 16383 - sin * this.verticesX!![vertex]) shr 14
								this.verticesX!![vertex] = rotated
							}
							this.verticesX!![vertex] += HdModel.originX
							this.verticesY!![vertex] += ModelTransformOriginY.originY
							this.verticesZ!![vertex] += HdModel.originZ
							memberIndex++
						}
					}
				}
			}
			if (lights) {
				for (labelIndex in 0 until length) {
					group = labels[labelIndex]
					if (group < this.vertexGroups!!.size) {
						groupVertices = this.vertexGroups!![group]
						for (groupMemberIndex in 0 until groupVertices!!.size) {
							val vertex: Int = groupVertices[groupMemberIndex]
							val start: Int = this.triangles!![vertex]
							val end: Int = this.triangles!![vertex + 1]
							for (index in start until end) {
								val normalIndex: Int = this.vertexNormalIndices!![index] - 1
								if (normalIndex == -1) {
									break
								}
								var sin: Int
								var cos: Int
								var rotated: Int
								if (z != 0) {
									sin = TextureCombineSource.SINE[z]
									cos = TextureCombineSource.COSINE[z]
									rotated = (cos * this.normalsX!![normalIndex] + this.normalsY!![normalIndex] * sin + 16383) shr 14
									this.normalsY!![normalIndex] = ((cos * this.normalsY!![normalIndex] + 16383 - this.normalsX!![normalIndex] * sin) shr 14).toShort()
									this.normalsX!![normalIndex] = rotated.toShort()
								}
								if (x != 0) {
									sin = TextureCombineSource.SINE[x]
									cos = TextureCombineSource.COSINE[x]
									rotated = (cos * this.normalsY!![normalIndex] + 16383 - this.normalsZ!![normalIndex] * sin) shr 14
									this.normalsZ!![normalIndex] = ((cos * this.normalsZ!![normalIndex] + sin * this.normalsY!![normalIndex] + 16383) shr 14).toShort()
									this.normalsY!![normalIndex] = rotated.toShort()
								}
								if (y != 0) {
									sin = TextureCombineSource.SINE[y]
									cos = TextureCombineSource.COSINE[y]
									rotated = (sin * this.normalsZ!![normalIndex] + this.normalsX!![normalIndex] * cos + 16383) shr 14
									this.normalsZ!![normalIndex] = ((this.normalsZ!![normalIndex] * cos + 16383 - this.normalsX!![normalIndex] * sin) shr 14).toShort()
									this.normalsX!![normalIndex] = rotated.toShort()
								}
							}
						}
					}
				}
				if (this.normalsBuffer == null && this.colorsBuffer != null) {
					this.colorsBuffer!!.buffer = null
				}
				if (this.normalsBuffer != null) {
					this.normalsBuffer!!.buffer = null
				}
			}
		} else if (type == 3) {
			for (labelIndex in 0 until length) {
				val groupId: Int = labels[labelIndex]
				if (this.vertexGroups!!.size > groupId) {
					members = this.vertexGroups!![groupId]
					memberIndex = 0
					while (memberIndex < members!!.size) {
						val vertex: Int = members[memberIndex]
						this.verticesX!![vertex] -= HdModel.originX
						this.verticesY!![vertex] -= ModelTransformOriginY.originY
						this.verticesZ!![vertex] -= HdModel.originZ
						this.verticesX!![vertex] = (x * this.verticesX!![vertex]) shr 7
						this.verticesY!![vertex] = (this.verticesY!![vertex] * y) shr 7
						this.verticesZ!![vertex] = (this.verticesZ!![vertex] * z) shr 7
						this.verticesX!![vertex] += HdModel.originX
						this.verticesY!![vertex] += ModelTransformOriginY.originY
						this.verticesZ!![vertex] += HdModel.originZ
						memberIndex++
					}
				}
			}
		} else {
			var glBillboardFace: GlBillboardFace
			var glBillboardAttributes: BillboardSurfaceGL
			if (type == 5) {
				if (this.faceGroups != null) {
					for (labelIndex in 0 until length) {
						val groupId: Int = labels[labelIndex]
						if (this.faceGroups!!.size > groupId) {
							members = this.faceGroups!![groupId]
							memberIndex = 0
							while (memberIndex < members!!.size) {
								val face: Int = members[memberIndex]
								var alpha: Int = (this.facesAlpha!![face].toInt() and 0xFF) + x * 8
								if (alpha < 0) {
									alpha = 0
								} else if (alpha > 255) {
									alpha = 255
								}
								this.facesAlpha!![face] = alpha.toByte()
								memberIndex++
							}
							if (members.size > 0 && this.colorsBuffer != null) {
								this.colorsBuffer!!.buffer = null
							}
						}
					}
					if (this.billboardFaces != null) {
						for (billboardIndex in 0 until this.billboardCount) {
							glBillboardFace = this.billboardFaces!![billboardIndex]!!
							glBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
							glBillboardAttributes.diffuseColor = ((255 - (this.facesAlpha!![glBillboardFace.face].toInt() and 0xFF)) shl 24) or (glBillboardAttributes.diffuseColor and 0xFFFFFF)
						}
					}
				}
			} else if (type != 7) {
				var glBillboardAttributes2: BillboardSurfaceGL
				if (type == 8) {
					if (this.billboardGroups != null) {
						for (labelIndex in 0 until length) {
							val groupId: Int = labels[labelIndex]
							if (this.billboardGroups!!.size > groupId) {
								members = this.billboardGroups!![groupId]
								memberIndex = 0
								while (memberIndex < members!!.size) {
									glBillboardAttributes2 = this.billboardAttributes!![members[memberIndex]]!!
									glBillboardAttributes2.offsetX += x
									glBillboardAttributes2.offsetY += y
									memberIndex++
								}
							}
						}
					}
				} else if (type == 10) {
					if (this.billboardGroups != null) {
						for (labelIndex in 0 until length) {
							val groupId: Int = labels[labelIndex]
							if (groupId < this.billboardGroups!!.size) {
								members = this.billboardGroups!![groupId]
								memberIndex = 0
								while (memberIndex < members!!.size) {
									glBillboardAttributes2 = this.billboardAttributes!![members[memberIndex]]!!
									glBillboardAttributes2.scaleX = (x * glBillboardAttributes2.scaleX) shr 7
									glBillboardAttributes2.scaleY = (glBillboardAttributes2.scaleY * y) shr 7
									memberIndex++
								}
							}
						}
					}
				} else if (type == 9 && this.billboardGroups != null) {
					for (labelIndex in 0 until length) {
						val groupId: Int = labels[labelIndex]
						if (groupId < this.billboardGroups!!.size) {
							members = this.billboardGroups!![groupId]
							memberIndex = 0
							while (memberIndex < members!!.size) {
								glBillboardAttributes2 = this.billboardAttributes!![members[memberIndex]]!!
								glBillboardAttributes2.angle = (glBillboardAttributes2.angle + x) and 0x3FFF
								memberIndex++
							}
						}
					}
				}
			} else if (this.faceGroups != null) {
				for (labelIndex in 0 until length) {
					val groupId: Int = labels[labelIndex]
					if (groupId < this.faceGroups!!.size) {
						members = this.faceGroups!![groupId]
						memberIndex = 0
						while (memberIndex < members!!.size) {
							val face: Int = members[memberIndex]
							val color: Int = this.facesColor!![face].toInt() and 0xFFFF
							val hue: Int = (color shr 10) and 0x3F
							var saturation: Int = (color shr 7) and 0x7
							saturation += y / 4
							val newHue: Int = (hue + x) and 0x3F
							var lightness: Int = color and 0x7F
							lightness += z
							if (saturation < 0) {
								saturation = 0
							} else if (saturation > 7) {
								saturation = 7
							}
							if (lightness < 0) {
								lightness = 0
							} else if (lightness > 127) {
								lightness = 127
							}
							this.facesColor!![face] = ((saturation shl 7) or (newHue shl 10) or lightness).toShort()
							memberIndex++
						}
						if (members.size > 0 && this.colorsBuffer != null) {
							this.colorsBuffer!!.buffer = null
						}
					}
				}
				if (this.billboardFaces != null) {
					for (billboardIndex in 0 until this.billboardCount) {
						glBillboardFace = this.billboardFaces!![billboardIndex]!!
						glBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
						glBillboardAttributes.diffuseColor = (ColorUtil.hslToRgb!![this.facesColor!![glBillboardFace.face].toInt() and 0xFFFF] and 0xFFFFFF) or (glBillboardAttributes.diffuseColor and 0xFF000000.toInt())
					}
				}
			}
		}
	}

	override fun HA(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.minZ.toInt()
	}

	override fun ua(): Int {
		return this.functionMask
	}

	override fun P(type: Int, x: Int, y: Int, z: Int) {
		var local15: Int
		var local17: Int
		if (type == 0) {
			HdModel.originX = 0
			HdModel.originZ = 0
			ModelTransformOriginY.originY = 0
			var vertexCount: Int = 0
			for (vertexIndex in 0 until this.maxDepth) {
				HdModel.originX += this.verticesX!![vertexIndex]
				ModelTransformOriginY.originY += this.verticesY!![vertexIndex]
				vertexCount++
				HdModel.originZ += this.verticesZ!![vertexIndex]
			}
			if (vertexCount <= 0) {
				ModelTransformOriginY.originY = y
				HdModel.originZ = z
				HdModel.originX = x
			} else {
				ModelTransformOriginY.originY = ModelTransformOriginY.originY / vertexCount + y
				HdModel.originX = x + HdModel.originX / vertexCount
				HdModel.originZ = HdModel.originZ / vertexCount + z
			}
		} else if (type == 1) {
			for (vertexIndex in 0 until this.maxDepth) {
				this.verticesX!![vertexIndex] += x
				this.verticesY!![vertexIndex] += y
				this.verticesZ!![vertexIndex] += z
			}
		} else {
			var local155: Int
			var local173: Int
			if (type == 2) {
				for (vertexIndex in 0 until this.maxDepth) {
					this.verticesX!![vertexIndex] -= HdModel.originX
					this.verticesY!![vertexIndex] -= ModelTransformOriginY.originY
					this.verticesZ!![vertexIndex] -= HdModel.originZ
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
						val rotatedY: Int = (cos * this.verticesY!![vertexIndex] + 16383 - this.verticesZ!![vertexIndex] * sin) shr 14
						this.verticesZ!![vertexIndex] = (sin * this.verticesY!![vertexIndex] + cos * this.verticesZ!![vertexIndex] + 16383) shr 14
						this.verticesY!![vertexIndex] = rotatedY
					}
					if (y != 0) {
						val sin: Int = TextureCombineSource.SINE[y]
						val cos: Int = TextureCombineSource.COSINE[y]
						val rotatedX: Int = (this.verticesZ!![vertexIndex] * sin + cos * this.verticesX!![vertexIndex] + 16383) shr 14
						this.verticesZ!![vertexIndex] = (cos * this.verticesZ!![vertexIndex] + 16383 - this.verticesX!![vertexIndex] * sin) shr 14
						this.verticesX!![vertexIndex] = rotatedX
					}
					this.verticesX!![vertexIndex] += HdModel.originX
					this.verticesY!![vertexIndex] += ModelTransformOriginY.originY
					this.verticesZ!![vertexIndex] += HdModel.originZ
				}
			} else if (type == 3) {
				for (vertexIndex in 0 until this.maxDepth) {
					this.verticesX!![vertexIndex] -= HdModel.originX
					this.verticesY!![vertexIndex] -= ModelTransformOriginY.originY
					this.verticesZ!![vertexIndex] -= HdModel.originZ
					this.verticesX!![vertexIndex] = x * this.verticesX!![vertexIndex] / 128
					this.verticesY!![vertexIndex] = y * this.verticesY!![vertexIndex] / 128
					this.verticesZ!![vertexIndex] = this.verticesZ!![vertexIndex] * z / 128
					this.verticesX!![vertexIndex] += HdModel.originX
					this.verticesY!![vertexIndex] += ModelTransformOriginY.originY
					this.verticesZ!![vertexIndex] += HdModel.originZ
				}
			} else {
				var glBillboardFace: GlBillboardFace
				var glBillboardAttributes: BillboardSurfaceGL
				if (type == 5) {
					for (faceIndex in 0 until this.numFaces) {
						var alpha: Int = (this.facesAlpha!![faceIndex].toInt() and 0xFF) + x * 8
						if (alpha < 0) {
							alpha = 0
						} else if (alpha > 255) {
							alpha = 255
						}
						this.facesAlpha!![faceIndex] = alpha.toByte()
					}
					if (this.colorsBuffer != null) {
						this.colorsBuffer!!.buffer = null
					}
					if (this.billboardFaces != null) {
						for (billboardIndex in 0 until this.billboardCount) {
							glBillboardFace = this.billboardFaces!![billboardIndex]!!
							glBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
							glBillboardAttributes.diffuseColor = (glBillboardAttributes.diffuseColor and 0xFFFFFF) or ((255 - (this.facesAlpha!![glBillboardFace.face].toInt() and 0xFF)) shl 24)
						}
					}
				} else if (type == 7) {
					for (faceIndex in 0 until this.numFaces) {
						val hsl: Int = this.facesColor!![faceIndex].toInt() and 0xFFFF
						val hue: Int = (hsl shr 10) and 0x3F
						var saturation: Int = (hsl shr 7) and 0x7
						val newHue: Int = (hue + x) and 0x3F
						var newLightness: Int = hsl and 0x7F
						saturation += y / 4
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
						this.facesColor!![faceIndex] = (newLightness or (saturation shl 7) or (newHue shl 10)).toShort()
					}
					if (this.colorsBuffer != null) {
						this.colorsBuffer!!.buffer = null
					}
					if (this.billboardFaces != null) {
						for (billboardIndex in 0 until this.billboardCount) {
							glBillboardFace = this.billboardFaces!![billboardIndex]!!
							glBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
							glBillboardAttributes.diffuseColor = (ColorUtil.hslToRgb!![this.facesColor!![glBillboardFace.face].toInt() and 0xFFFF] and 0xFFFFFF) or (glBillboardAttributes.diffuseColor and 0xFF000000.toInt())
						}
					}
				} else {
					var glBillboardAttributes2: BillboardSurfaceGL
					if (type == 8) {
						for (billboardIndex in 0 until this.billboardCount) {
							glBillboardAttributes2 = this.billboardAttributes!![billboardIndex]!!
							glBillboardAttributes2.offsetY += y
							glBillboardAttributes2.offsetX += x
						}
					} else if (type == 10) {
						for (billboardIndex in 0 until this.billboardCount) {
							glBillboardAttributes2 = this.billboardAttributes!![billboardIndex]!!
							glBillboardAttributes2.scaleX = (glBillboardAttributes2.scaleX * x) shr 7
							glBillboardAttributes2.scaleY = (y * glBillboardAttributes2.scaleY) shr 7
						}
					} else if (type == 9) {
						for (billboardIndex in 0 until this.billboardCount) {
							glBillboardAttributes2 = this.billboardAttributes!![billboardIndex]!!
							glBillboardAttributes2.angle = (glBillboardAttributes2.angle + x) and 0x3FFF
						}
					}
				}
			}
		}
	}

	override fun s(functionMask: Int) {
		this.functionMask = functionMask
		if (this.mergedNormals != null && (this.functionMask and 0x10000) == 0) {
			this.normalsZ = this.mergedNormals!!.normalsZ
			this.magnitudes = this.mergedNormals!!.magnitude
			this.normalsX = this.mergedNormals!!.normalsX
			this.normalsY = this.mergedNormals!!.normalsY
			this.mergedNormals = null
		}
		this.updateRequired = true
		this.releaseUploadedData()
	}

	override fun C(ambient: Int) {
		this.ambient = ambient.toShort()
		if (this.colorsBuffer != null) {
			this.colorsBuffer!!.buffer = null
		}
	}

	override fun v() {
		for (i in 0 until this.maxDepth) {
			this.verticesZ!![i] = -this.verticesZ!![i]
		}
		for (j in 0 until this.numNormals) {
			this.normalsZ!![j] = (-this.normalsZ!![j]).toShort()
		}
		for (k in 0 until this.numFaces) {
			val temp: Short = this.facesA!![k]
			this.facesA!![k] = this.facesC!![k]
			this.facesC!![k] = temp
		}
		if (this.normalsBuffer == null && this.colorsBuffer != null) {
			this.colorsBuffer!!.buffer = null
		}
		if (this.normalsBuffer != null) {
			this.normalsBuffer!!.buffer = null
		}
		this.boundsCalculated = false
		if (this.modelElementsBuffer != null) {
			this.modelElementsBuffer!!.openglElementsBuffer = null
		}
		if (this.vertexBuffer != null) {
			this.vertexBuffer!!.buffer = null
		}
	}

	override fun transform(matrix: Matrix?, submesh: Int, relative: Boolean) {
		if (this.verticesSubmeshes == null) {
			return
		}
		val result: IntArray = IntArray(3)
		for (i in 0 until this.maxDepth) {
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

	override fun I(type: Int, labels: IntArray?, x: Int, y: Int, z: Int, lights: Boolean, submesh: Int, matrix: IntArray?) {
		var x: Int = x
		var y: Int = y
		var z: Int = z
		val length: Int = labels!!.size
		var local24: Int
		var local32: Int
		var local60: Int
		var local38: Int
		var local54: Int
		var groupVertices: IntArray?
		if (type == 0) {
			y = y shl 0x4
			z = z shl 0x4
			x = x shl 0x4
			var vertexCount: Int = 0
			HdModel.originX = 0
			ModelTransformOriginY.originY = 0
			HdModel.originZ = 0
			for (labelIndex in 0 until length) {
				val label: Int = labels[labelIndex]
				if (label < this.vertexGroups!!.size) {
					groupVertices = this.vertexGroups!![label]
					for (memberIndex in 0 until groupVertices!!.size) {
						val vertexIndex: Int = groupVertices[memberIndex]
						if (this.verticesSubmeshes == null || (submesh and this.verticesSubmeshes!![vertexIndex].toInt()) != 0) {
							HdModel.originX += this.verticesX!![vertexIndex]
							ModelTransformOriginY.originY += this.verticesY!![vertexIndex]
							vertexCount++
							HdModel.originZ += this.verticesZ!![vertexIndex]
						}
					}
				}
			}
			if (vertexCount <= 0) {
				HdModel.originX = x
				HdModel.originZ = z
				ModelTransformOriginY.originY = y
			} else {
				HdModel.originX = x + HdModel.originX / vertexCount
				ModelTransformOriginY.originY = ModelTransformOriginY.originY / vertexCount + y
				HdModel.originZ = z + HdModel.originZ / vertexCount
				Protocol.originPending = true
			}
			return
		}
		var members: IntArray?
		var local245: Int
		if (type == 1) {
			if (matrix != null) {
				val transformedX: Int = (matrix[2] * z + matrix[0] * x + y * matrix[1] + 8192) shr 14
				val transformedY: Int = (y * matrix[4] + x * matrix[3] + matrix[5] * z + 8192) shr 14
				val transformedZ: Int = (z * matrix[8] + x * matrix[6] + y * matrix[7] + 8192) shr 14
				x = transformedX
				z = transformedZ
				y = transformedY
			}
			x = x shl 0x4
			z = z shl 0x4
			y = y shl 0x4
			for (labelIndex in 0 until length) {
				val label: Int = labels[labelIndex]
				if (label < this.vertexGroups!!.size) {
					members = this.vertexGroups!![label]
					for (memberIndex in 0 until members!!.size) {
						val vertexIndex: Int = members[memberIndex]
						if (this.verticesSubmeshes == null || (this.verticesSubmeshes!![vertexIndex].toInt() and submesh) != 0) {
							this.verticesX!![vertexIndex] += x
							this.verticesY!![vertexIndex] += y
							this.verticesZ!![vertexIndex] += z
						}
					}
				}
			}
			return
		}
		var local379: Int
		var local398: Int
		var local628: Int
		var local637: Int
		var local649: Int
		var local653: Int
		var local671: Int
		var local1011: Int
		var local1019: Int
		var local1175: Int
		var local1201: Int
		var local1206: Int
		var local1215: Int
		var local1220: Int
		var local1224: Int
		var local1228: Int
		var local1230: Int
		var local1359: IntArray?
		var local1361: Int
		var local1365: Int
		var local1369: Int
		var local1371: Int
		var local1502: Int
		if (type == 2) {
			if (matrix == null) {
				for (labelIndex in 0 until length) {
					val label: Int = labels[labelIndex]
					if (label < this.vertexGroups!!.size) {
						members = this.vertexGroups!![label]
						for (memberIndex in 0 until members!!.size) {
							val vertexIndex: Int = members[memberIndex]
							if (this.verticesSubmeshes == null || (submesh and this.verticesSubmeshes!![vertexIndex].toInt()) != 0) {
								this.verticesX!![vertexIndex] -= HdModel.originX
								this.verticesY!![vertexIndex] -= ModelTransformOriginY.originY
								this.verticesZ!![vertexIndex] -= HdModel.originZ
								if (z != 0) {
									val sin: Int = TextureCombineSource.SINE[z]
									val cos: Int = TextureCombineSource.COSINE[z]
									val rotatedX: Int = (sin * this.verticesY!![vertexIndex] + this.verticesX!![vertexIndex] * cos + 16383) shr 14
									this.verticesY!![vertexIndex] = (this.verticesY!![vertexIndex] * cos + 16383 - this.verticesX!![vertexIndex] * sin) shr 14
									this.verticesX!![vertexIndex] = rotatedX
								}
								if (x != 0) {
									val sin: Int = TextureCombineSource.SINE[x]
									val cos: Int = TextureCombineSource.COSINE[x]
									val rotatedY: Int = (cos * this.verticesY!![vertexIndex] + 16383 - this.verticesZ!![vertexIndex] * sin) shr 14
									this.verticesZ!![vertexIndex] = (sin * this.verticesY!![vertexIndex] + this.verticesZ!![vertexIndex] * cos + 16383) shr 14
									this.verticesY!![vertexIndex] = rotatedY
								}
								if (y != 0) {
									val sin: Int = TextureCombineSource.SINE[y]
									val cos: Int = TextureCombineSource.COSINE[y]
									val rotatedX: Int = (sin * this.verticesZ!![vertexIndex] + cos * this.verticesX!![vertexIndex] + 16383) shr 14
									this.verticesZ!![vertexIndex] = (this.verticesZ!![vertexIndex] * cos + 16383 - this.verticesX!![vertexIndex] * sin) shr 14
									this.verticesX!![vertexIndex] = rotatedX
								}
								this.verticesX!![vertexIndex] += HdModel.originX
								this.verticesY!![vertexIndex] += ModelTransformOriginY.originY
								this.verticesZ!![vertexIndex] += HdModel.originZ
							}
						}
					}
				}
				if (lights) {
					for (labelIndex in 0 until length) {
						val label: Int = labels[labelIndex]
						if (this.vertexGroups!!.size > label) {
							groupVertices = this.vertexGroups!![label]
							for (memberIndex in 0 until groupVertices!!.size) {
								val vertexIndex: Int = groupVertices[memberIndex]
								if (this.verticesSubmeshes == null || (this.verticesSubmeshes!![vertexIndex].toInt() and submesh) != 0) {
									val normalStart: Int = this.triangles!![vertexIndex]
									val normalEnd: Int = this.triangles!![vertexIndex + 1]
									for (normalSlot in normalStart until normalEnd) {
										val normalIndex: Int = this.vertexNormalIndices!![normalSlot] - 1
										if (normalIndex == -1) {
											break
										}
										if (z != 0) {
											val sin: Int = TextureCombineSource.SINE[z]
											val cos: Int = TextureCombineSource.COSINE[z]
											val rotatedX: Int = (this.normalsX!![normalIndex] * cos + this.normalsY!![normalIndex] * sin + 16383) shr 14
											this.normalsY!![normalIndex] = ((cos * this.normalsY!![normalIndex] + 16383 - sin * this.normalsX!![normalIndex]) shr 14).toShort()
											this.normalsX!![normalIndex] = rotatedX.toShort()
										}
										if (x != 0) {
											val sin: Int = TextureCombineSource.SINE[x]
											val cos: Int = TextureCombineSource.COSINE[x]
											val rotatedY: Int = (this.normalsY!![normalIndex] * cos + 16383 - sin * this.normalsZ!![normalIndex]) shr 14
											this.normalsZ!![normalIndex] = ((sin * this.normalsY!![normalIndex] + cos * this.normalsZ!![normalIndex] + 16383) shr 14).toShort()
											this.normalsY!![normalIndex] = rotatedY.toShort()
										}
										if (y != 0) {
											val sin: Int = TextureCombineSource.SINE[y]
											val cos: Int = TextureCombineSource.COSINE[y]
											val rotatedX: Int = (cos * this.normalsX!![normalIndex] + sin * this.normalsZ!![normalIndex] + 16383) shr 14
											this.normalsZ!![normalIndex] = ((this.normalsZ!![normalIndex] * cos + 16383 - sin * this.normalsX!![normalIndex]) shr 14).toShort()
											this.normalsX!![normalIndex] = rotatedX.toShort()
										}
									}
								}
							}
						}
					}
					if (this.normalsBuffer == null && this.colorsBuffer != null) {
						this.colorsBuffer!!.buffer = null
					}
					if (this.normalsBuffer != null) {
						this.normalsBuffer!!.buffer = null
						return
					}
				}
			} else {
				val parentOriginX: Int = matrix[9] shl 4
				val parentOriginY: Int = matrix[10] shl 4
				val parentOriginZ: Int = matrix[11] shl 4
				val parentTranslateX: Int = matrix[12] shl 4
				val parentTranslateY: Int = matrix[13] shl 4
				val parentTranslateZ: Int = matrix[14] shl 4
				if (Protocol.originPending) {
					var newOriginX: Int = (HdModel.originX * matrix[0] + matrix[3] * ModelTransformOriginY.originY + matrix[6] * HdModel.originZ + 8192) shr 14
					var newOriginY: Int = (matrix[1] * HdModel.originX + ModelTransformOriginY.originY * matrix[4] + matrix[7] * HdModel.originZ + 8192) shr 14
					newOriginX += parentTranslateX
					newOriginY += parentTranslateY
					var newOriginZ: Int = (HdModel.originZ * matrix[8] + matrix[2] * HdModel.originX + matrix[5] * ModelTransformOriginY.originY + 8192) shr 14
					newOriginZ += parentTranslateZ
					HdModel.originX = newOriginX
					ModelTransformOriginY.originY = newOriginY
					HdModel.originZ = newOriginZ
					Protocol.originPending = false
				}
				val rotationMatrix: IntArray = IntArray(9)
				val cosX: Int = TextureCombineSource.COSINE[x]
				val sinX: Int = TextureCombineSource.SINE[x]
				val cosY: Int = TextureCombineSource.COSINE[y]
				val sinY: Int = TextureCombineSource.SINE[y]
				val cosZ: Int = TextureCombineSource.COSINE[z]
				val sinZ: Int = TextureCombineSource.SINE[z]
				val sinXCosZ: Int = (cosZ * sinX + 8192) shr 14
				val sinXSinZ: Int = (sinX * sinZ + 8192) shr 14
				rotationMatrix[6] = (sinXSinZ * cosY + cosZ * -sinY + 8192) shr 14
				rotationMatrix[8] = (cosX * cosY + 8192) shr 14
				rotationMatrix[4] = (cosX * cosZ + 8192) shr 14
				rotationMatrix[1] = (sinZ * -cosY + sinXCosZ * sinY + 8192) shr 14
				rotationMatrix[3] = (cosX * sinZ + 8192) shr 14
				rotationMatrix[5] = -sinX
				rotationMatrix[0] = (sinY * sinXSinZ + cosZ * cosY + 8192) shr 14
				rotationMatrix[7] = (cosY * sinXCosZ + sinZ * sinY + 8192) shr 14
				rotationMatrix[2] = (sinY * cosX + 8192) shr 14
				val rotatedPivotX: Int = (rotationMatrix[1] * -ModelTransformOriginY.originY + -HdModel.originX * rotationMatrix[0] + -HdModel.originZ * rotationMatrix[2] + 8192) shr 14
				val rotatedPivotY: Int = (-HdModel.originX * rotationMatrix[3] + rotationMatrix[4] * -ModelTransformOriginY.originY + rotationMatrix[5] * -HdModel.originZ + 8192) shr 14
				val rotatedPivotZ: Int = (-HdModel.originZ * rotationMatrix[8] + rotationMatrix[6] * -HdModel.originX + rotationMatrix[7] * -ModelTransformOriginY.originY + 8192) shr 14
				val pivotTranslateX: Int = rotatedPivotX + HdModel.originX
				val pivotTranslateY: Int = rotatedPivotY + ModelTransformOriginY.originY
				val pivotTranslateZ: Int = HdModel.originZ + rotatedPivotZ
				val combinedMatrix: IntArray = IntArray(9)
				for (row in 0 until 3) {
					for (column in 0 until 3) {
						var dotProduct: Int = 0
						for (k in 0 until 3) {
							dotProduct += matrix[column * 3 + k] * rotationMatrix[row * 3 + k]
						}
						combinedMatrix[column + row * 3] = (dotProduct + 8192) shr 14
					}
				}
				var translatedX: Int = (rotationMatrix[2] * parentTranslateZ + parentTranslateX * rotationMatrix[0] + parentTranslateY * rotationMatrix[1] + 8192) shr 14
				var translatedY: Int = (parentTranslateY * rotationMatrix[4] + rotationMatrix[3] * parentTranslateX + rotationMatrix[5] * parentTranslateZ + 8192) shr 14
				var translatedZ: Int = (rotationMatrix[8] * parentTranslateZ + rotationMatrix[7] * parentTranslateY + rotationMatrix[6] * parentTranslateX + 8192) shr 14
				translatedY += pivotTranslateY
				translatedX += pivotTranslateX
				translatedZ += pivotTranslateZ
				val finalMatrix: IntArray = IntArray(9)
				for (row in 0 until 3) {
					for (column in 0 until 3) {
						var dotProduct: Int = 0
						for (k in 0 until 3) {
							dotProduct += matrix[row * 3 + k] * combinedMatrix[k * 3 + column]
						}
						finalMatrix[row * 3 + column] = (dotProduct + 8192) shr 14
					}
				}
				var finalTranslateX: Int = (matrix[2] * translatedZ + translatedY * matrix[1] + translatedX * matrix[0] + 8192) shr 14
				var finalTranslateY: Int = (matrix[3] * translatedX + matrix[4] * translatedY + matrix[5] * translatedZ + 8192) shr 14
				finalTranslateY += parentOriginY
				var finalTranslateZ: Int = (translatedZ * matrix[8] + matrix[7] * translatedY + translatedX * matrix[6] + 8192) shr 14
				finalTranslateX += parentOriginX
				finalTranslateZ += parentOriginZ
				for (labelIndex in 0 until length) {
					val group: Int = labels[labelIndex]
					if (group < this.vertexGroups!!.size) {
						val vertexGroup: IntArray? = this.vertexGroups!![group]
						for (i in 0 until vertexGroup!!.size) {
							val vertex: Int = vertexGroup[i]
							if (this.verticesSubmeshes == null || (this.verticesSubmeshes!![vertex].toInt() and submesh) != 0) {
								val rotatedX: Int = (finalMatrix[2] * this.verticesZ!![vertex] + finalMatrix[1] * this.verticesY!![vertex] + finalMatrix[0] * this.verticesX!![vertex] + 8192) shr 14
								val rotatedY: Int = (finalMatrix[5] * this.verticesZ!![vertex] + this.verticesX!![vertex] * finalMatrix[3] + finalMatrix[4] * this.verticesY!![vertex] + 8192) shr 14
								val newX: Int = rotatedX + finalTranslateX
								val newY: Int = rotatedY + finalTranslateY
								val rotatedZ: Int = (finalMatrix[8] * this.verticesZ!![vertex] + this.verticesX!![vertex] * finalMatrix[6] + finalMatrix[7] * this.verticesY!![vertex] + 8192) shr 14
								val newZ: Int = rotatedZ + finalTranslateZ
								this.verticesX!![vertex] = newX
								this.verticesY!![vertex] = newY
								this.verticesZ!![vertex] = newZ
							}
						}
					}
				}
			}
		} else if (type != 3) {
			var glBillboardFace: GlBillboardFace
			var glBillboardAttributes: BillboardSurfaceGL
			if (type == 5) {
				if (this.faceGroups != null) {
					for (labelIndex in 0 until length) {
						val label: Int = labels[labelIndex]
						if (label < this.faceGroups!!.size) {
							members = this.faceGroups!![label]
							for (memberIndex in 0 until members!!.size) {
								val faceIndex: Int = members[memberIndex]
								if (this.facesSubmeshes == null || (this.facesSubmeshes!![faceIndex].toInt() and submesh) != 0) {
									var alpha: Int = (this.facesAlpha!![faceIndex].toInt() and 0xFF) + (x * 8)
									if (alpha < 0) {
										alpha = 0
									} else if (alpha > 255) {
										alpha = 255
									}
									this.facesAlpha!![faceIndex] = alpha.toByte()
									if (this.colorsBuffer != null) {
										this.colorsBuffer!!.buffer = null
									}
								}
							}
						}
					}
					if (this.billboardFaces != null) {
						for (billboardIndex in 0 until this.billboardCount) {
							glBillboardFace = this.billboardFaces!![billboardIndex]!!
							glBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
							glBillboardAttributes.diffuseColor = ((255 - (this.facesAlpha!![glBillboardFace.face].toInt() and 0xFF)) shl 24) or (glBillboardAttributes.diffuseColor and 0xFFFFFF)
						}
					}
				}
			} else if (type != 7) {
				var glBillboardAttributes2: BillboardSurfaceGL
				if (type == 8) {
					if (this.billboardGroups != null) {
						for (labelIndex in 0 until length) {
							val label: Int = labels[labelIndex]
							if (label < this.billboardGroups!!.size) {
								members = this.billboardGroups!![label]
								for (memberIndex in 0 until members!!.size) {
									glBillboardAttributes2 = this.billboardAttributes!![members[memberIndex]]!!
									glBillboardAttributes2.offsetY += y
									glBillboardAttributes2.offsetX += x
								}
							}
						}
					}
				} else if (type == 10) {
					if (this.billboardGroups != null) {
						for (labelIndex in 0 until length) {
							val label: Int = labels[labelIndex]
							if (label < this.billboardGroups!!.size) {
								members = this.billboardGroups!![label]
								for (memberIndex in 0 until members!!.size) {
									glBillboardAttributes2 = this.billboardAttributes!![members[memberIndex]]!!
									glBillboardAttributes2.scaleY = (y * glBillboardAttributes2.scaleY) shr 7
									glBillboardAttributes2.scaleX = (x * glBillboardAttributes2.scaleX) shr 7
								}
							}
						}
					}
				} else if (type == 9 && this.billboardGroups != null) {
					for (labelIndex in 0 until length) {
						val label: Int = labels[labelIndex]
						if (this.billboardGroups!!.size > label) {
							members = this.billboardGroups!![label]
							for (memberIndex in 0 until members!!.size) {
								glBillboardAttributes2 = this.billboardAttributes!![members[memberIndex]]!!
								glBillboardAttributes2.angle = (glBillboardAttributes2.angle + x) and 0x3FFF
							}
						}
					}
				}
			} else if (this.faceGroups != null) {
				for (labelIndex in 0 until length) {
					val label: Int = labels[labelIndex]
					if (this.faceGroups!!.size > label) {
						members = this.faceGroups!![label]
						for (memberIndex in 0 until members!!.size) {
							val faceIndex: Int = members[memberIndex]
							if (this.facesSubmeshes == null || (this.facesSubmeshes!![faceIndex].toInt() and submesh) != 0) {
								val hsl: Int = this.facesColor!![faceIndex].toInt() and 0xFFFF
								val hue: Int = (hsl shr 10) and 0x3F
								var saturation: Int = (hsl shr 7) and 0x7
								saturation += y / 4
								var lightness: Int = hsl and 0x7F
								val newHue: Int = (hue + x) and 0x3F
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
								if (this.colorsBuffer != null) {
									this.colorsBuffer!!.buffer = null
								}
							}
						}
					}
				}
				if (this.billboardFaces != null) {
					for (billboardIndex in 0 until this.billboardCount) {
						glBillboardFace = this.billboardFaces!![billboardIndex]!!
						glBillboardAttributes = this.billboardAttributes!![billboardIndex]!!
						glBillboardAttributes.diffuseColor = (ColorUtil.hslToRgb!![this.facesColor!![glBillboardFace.face].toInt() and 0xFFFF] and 0xFFFFFF) or (glBillboardAttributes.diffuseColor and 0xFF000000.toInt())
					}
				}
			}
		} else if (matrix == null) {
			for (labelIndex in 0 until length) {
				val label: Int = labels[labelIndex]
				if (this.vertexGroups!!.size > label) {
					members = this.vertexGroups!![label]
					for (memberIndex in 0 until members!!.size) {
						val vertexIndex: Int = members[memberIndex]
						if (this.verticesSubmeshes == null || (submesh and this.verticesSubmeshes!![vertexIndex].toInt()) != 0) {
							this.verticesX!![vertexIndex] -= HdModel.originX
							this.verticesY!![vertexIndex] -= ModelTransformOriginY.originY
							this.verticesZ!![vertexIndex] -= HdModel.originZ
							this.verticesX!![vertexIndex] = (this.verticesX!![vertexIndex] * x) shr 7
							this.verticesY!![vertexIndex] = (this.verticesY!![vertexIndex] * y) shr 7
							this.verticesZ!![vertexIndex] = (z * this.verticesZ!![vertexIndex]) shr 7
							this.verticesX!![vertexIndex] += HdModel.originX
							this.verticesY!![vertexIndex] += ModelTransformOriginY.originY
							this.verticesZ!![vertexIndex] += HdModel.originZ
						}
					}
				}
			}
		} else {
			val parentOriginX: Int = matrix[9] shl 4
			val parentOriginY: Int = matrix[10] shl 4
			val parentOriginZ: Int = matrix[11] shl 4
			val parentTranslateX: Int = matrix[12] shl 4
			val parentTranslateY: Int = matrix[13] shl 4
			val parentTranslateZ: Int = matrix[14] shl 4
			if (Protocol.originPending) {
				var newOriginX: Int = (HdModel.originX * matrix[0] + ModelTransformOriginY.originY * matrix[3] + matrix[6] * HdModel.originZ + 8192) shr 14
				var newOriginY: Int = (matrix[7] * HdModel.originZ + ModelTransformOriginY.originY * matrix[4] + matrix[1] * HdModel.originX + 8192) shr 14
				newOriginY += parentTranslateY
				var newOriginZ: Int = (matrix[5] * ModelTransformOriginY.originY + HdModel.originX * matrix[2] + matrix[8] * HdModel.originZ + 8192) shr 14
				newOriginX += parentTranslateX
				ModelTransformOriginY.originY = newOriginY
				newOriginZ += parentTranslateZ
				HdModel.originX = newOriginX
				Protocol.originPending = false
				HdModel.originZ = newOriginZ
			}
			val scaleX: Int = (x shl 15) shr 7
			val scaleY: Int = (y shl 15) shr 7
			val scaleZ: Int = (z shl 15) shr 7
			val scaledPivotX: Int = (-HdModel.originX * scaleX + 8192) shr 14
			val scaledPivotY: Int = (scaleY * -ModelTransformOriginY.originY + 8192) shr 14
			val scaledPivotZ: Int = (-HdModel.originZ * scaleZ + 8192) shr 14
			val pivotOffsetX: Int = scaledPivotX + HdModel.originX
			val pivotOffsetY: Int = scaledPivotY + ModelTransformOriginY.originY
			val pivotOffsetZ: Int = HdModel.originZ + scaledPivotZ
			val scaleMatrix: IntArray = intArrayOf((matrix[0] * scaleX + 8192) shr 14, (matrix[3] * scaleX + 8192) shr 14, (scaleX * matrix[6] + 8192) shr 14, (scaleY * matrix[1] + 8192) shr 14, (scaleY * matrix[4] + 8192) shr 14, (scaleY * matrix[7] + 8192) shr 14, (scaleZ * matrix[2] + 8192) shr 14, (scaleZ * matrix[5] + 8192) shr 14, (scaleZ * matrix[8] + 8192) shr 14)
			val scaledTranslateX: Int = (scaleX * parentTranslateX + 8192) shr 14
			val scaledTranslateY: Int = (scaleY * parentTranslateY + 8192) shr 14
			val translateY: Int = scaledTranslateY + pivotOffsetY
			val scaledTranslateZ: Int = (parentTranslateZ * scaleZ + 8192) shr 14
			val translateX: Int = scaledTranslateX + pivotOffsetX
			val translateZ: Int = scaledTranslateZ + pivotOffsetZ
			val combinedMatrix: IntArray = IntArray(9)
			var local2022: Int
			for (row in 0 until 3) {
				for (column in 0 until 3) {
					var dotProduct: Int = 0
					for (k in 0 until 3) {
						dotProduct += matrix[row * 3 + k] * scaleMatrix[k * 3 + column]
					}
					combinedMatrix[row * 3 + column] = (dotProduct + 8192) shr 14
				}
			}
			var finalTranslateX: Int = (matrix[1] * translateY + matrix[0] * translateX + translateZ * matrix[2] + 8192) shr 14
			var finalTranslateY: Int = (translateZ * matrix[5] + matrix[3] * translateX + matrix[4] * translateY + 8192) shr 14
			var finalTranslateZ: Int = (translateX * matrix[6] + translateY * matrix[7] + matrix[8] * translateZ + 8192) shr 14
			finalTranslateX += parentOriginX
			finalTranslateY += parentOriginY
			finalTranslateZ += parentOriginZ
			for (labelIndex in 0 until length) {
				val label: Int = labels[labelIndex]
				if (label < this.vertexGroups!!.size) {
					val vertexGroup: IntArray? = this.vertexGroups!![label]
					for (memberIndex in 0 until vertexGroup!!.size) {
						val vertexIndex: Int = vertexGroup[memberIndex]
						if (this.verticesSubmeshes == null || (this.verticesSubmeshes!![vertexIndex].toInt() and submesh) != 0) {
							val rotatedX: Int = (combinedMatrix[1] * this.verticesY!![vertexIndex] + combinedMatrix[0] * this.verticesX!![vertexIndex] + combinedMatrix[2] * this.verticesZ!![vertexIndex] + 8192) shr 14
							val rotatedY: Int = (combinedMatrix[4] * this.verticesY!![vertexIndex] + this.verticesX!![vertexIndex] * combinedMatrix[3] + combinedMatrix[5] * this.verticesZ!![vertexIndex] + 8192) shr 14
							val newX: Int = rotatedX + finalTranslateX
							val newY: Int = rotatedY + finalTranslateY
							val rotatedZ: Int = (combinedMatrix[7] * this.verticesY!![vertexIndex] + combinedMatrix[6] * this.verticesX!![vertexIndex] + combinedMatrix[8] * this.verticesZ!![vertexIndex] + 8192) shr 14
							this.verticesX!![vertexIndex] = newX
							val newZ: Int = rotatedZ + finalTranslateZ
							this.verticesY!![vertexIndex] = newY
							this.verticesZ!![vertexIndex] = newZ
						}
					}
				}
			}
		}
	}

	private fun calculateBounds() {
		var minVertexX: Int = 32767
		var minVertexY: Int = 32767
		var minVertexZ: Int = 32767
		var maxVertexX: Int = -32768
		var maxVertexY: Int = -32768
		var maxVertexZ: Int = -32768
		var maxSquaredRadius2d: Int = 0
		var maxSquaredRadius3d: Int = 0
		for (i in 0 until this.maxDepth) {
			val x: Int = this.verticesX!![i]
			val y: Int = this.verticesY!![i]
			if (y > maxVertexY) {
				maxVertexY = y
			}
			if (y < minVertexY) {
				minVertexY = y
			}
			val z: Int = this.verticesZ!![i]
			if (maxVertexX < x) {
				maxVertexX = x
			}
			if (minVertexX > x) {
				minVertexX = x
			}
			if (z > maxVertexZ) {
				maxVertexZ = z
			}
			if (minVertexZ > z) {
				minVertexZ = z
			}
			var squaredDistance: Int = x * x + z * z
			if (maxSquaredRadius2d < squaredDistance) {
				maxSquaredRadius2d = squaredDistance
			}
			squaredDistance = z * z + x * x + y * y
			if (squaredDistance > maxSquaredRadius3d) {
				maxSquaredRadius3d = squaredDistance
			}
		}
		this.minY = minVertexY.toShort()
		this.minZ = minVertexZ.toShort()
		this.maxZ = maxVertexZ.toShort()
		this.minX = minVertexX.toShort()
		this.maxY = maxVertexY.toShort()
		this.maxX = maxVertexX.toShort()
		this.size2d = (kotlin.math.sqrt(maxSquaredRadius2d.toDouble()) + 0.99).toInt().toShort()
		this.size3d = (kotlin.math.sqrt(maxSquaredRadius3d.toDouble()) + 0.99).toInt().toShort()
		this.boundsCalculated = true
	}

	override fun fa(): Int {
		if (!this.boundsCalculated) {
			this.calculateBounds()
		}
		return this.minY.toInt()
	}

	companion object {
		@JvmField
		var modelViewXY: Float = 0f
		@JvmField
		var originX: Int = 0

		@JvmField
		var normalsUid: LongArray? = null

		@JvmField
		var modelViewZZ: Float = 0f
		@JvmField
		var originZ: Int = 0
		@JvmField
		var modelViewXX: Float = 0f

		@JvmField
		var modelViewYZ: Float = 0f

		@JvmField
		var modelViewZW: Float = 0f

		@JvmField
		var modelViewZY: Float = 0f

		@JvmField
		var modelViewXZ: Float = 0f
		@JvmStatic
		fun altersVertices(preferenceFlags: Int, functionMask: Int): Boolean {
			return (functionMask and 0x20) != 0
		}

		@JvmStatic
		fun hasVertices(functionMask: Int, preferenceFlags: Int): Boolean {
			return (functionMask and 0x800) != 0
		}

		@JvmStatic
		fun altersFacing(preferenceFlags: Int, functionMask: Int): Boolean {
			return (functionMask and 0x10) != 0
		}

		@JvmField
		val matrixX: IntArray = IntArray(8)

		@JvmStatic
		fun retainsVerticesX(functionMask: Int, preferenceFlags: Int): Boolean {
			return (((functionMask and 0x70000) != 0) or HdModel.altersVerticesX(preferenceFlags, functionMask)) || HdModel.altersAnyVerticeType(functionMask, preferenceFlags)
		}

		@JvmStatic
		fun altersFaces(functionMask: Int, preferenceFlags: Int): Boolean {
			return (functionMask and 0x180) != 0
		}

		@JvmStatic
		fun hasElementsBuffer(functionMask: Int, preferenceFlags: Int): Boolean {
			return (functionMask and 0x800) != 0
		}

		@JvmStatic
		fun retainsFaceMaterials(preferenceFlags: Int, functionMask: Int): Boolean {
			return (HdModel.altersTextures(preferenceFlags, functionMask) or ((functionMask and 0x800) != 0)) || HdModel.requiresColorBufferRebuild(functionMask, preferenceFlags)
		}

		@JvmStatic
		fun altersTextures(preferenceFlags: Int, functionMask: Int): Boolean {
			return (functionMask and 0x8000) != 0
		}

		@JvmStatic
		fun requiresColorBufferRebuild(functionMask: Int, preferenceFlags: Int): Boolean {
			if (HdModel.hasColors(preferenceFlags, functionMask)) {
				return if (GpuModelRetainMasks.altersFaceColors(preferenceFlags, functionMask) or ((functionMask and 0xB000) != 0) or HdModel.altersAlpha(functionMask, preferenceFlags)) true else ((HdModel.altersLight(preferenceFlags, functionMask) or GpuModelRetainMasks.altersShading(functionMask, preferenceFlags)) and ((preferenceFlags and 0x37) == 0))
			} else {
				return false
			}
		}

		@JvmField
		var modelViewYX: Float = 0f

		@JvmField
		val matrixZ: IntArray = IntArray(8)

		@JvmField
		var modelViewZX: Float = 0f

		@JvmStatic
		fun requiresNormalsBufferRebuild(functionMask: Int, preferenceFlags: Int): Boolean {
			return (((functionMask and 0x2000) != 0) or HdModel.altersLight(preferenceFlags, functionMask) or GpuModelRetainMasks.altersShading(functionMask, preferenceFlags)) and HdModel.hasNormals(preferenceFlags, functionMask)
		}

		@JvmStatic
		fun altersVerticesZ(preferenceFlags: Int, functionMask: Int): Boolean {
			return (functionMask and 0x34) != 0
		}

		@JvmStatic
		fun altersAnyVerticeType(functionMask: Int, preferenceFlags: Int): Boolean {
			return (HdModel.altersVerticesX(preferenceFlags, functionMask) or HdModel.altersVerticesY(preferenceFlags, functionMask) or HdModel.altersVerticesZ(preferenceFlags, functionMask)) and HdModel.hasVertices(functionMask, preferenceFlags)
		}

		@JvmStatic
		fun altersTextureCoordinates(functionMask: Int, preferenceFlags: Int): Boolean {
			return false
		}

		@JvmStatic
		fun retainsVerticesZ(functionMask: Int, preferenceFlags: Int): Boolean {
			return (HdModel.altersVerticesZ(preferenceFlags, functionMask) or ((functionMask and 0x70000) != 0)) || HdModel.altersAnyVerticeType(functionMask, preferenceFlags)
		}

		@JvmStatic
		fun copyElementsBuffer(functionMask: Int, preferenceFlags: Int): Boolean {
			return HdModel.altersFacing(preferenceFlags, functionMask) and HdModel.hasElementsBuffer(functionMask, preferenceFlags)
		}

		@JvmStatic
		fun retainsVerticesY(preferenceFlags: Int, functionMask: Int): Boolean {
			return (((functionMask and 0x70000) != 0) or HdModel.altersVerticesY(preferenceFlags, functionMask)) || HdModel.altersAnyVerticeType(functionMask, preferenceFlags)
		}

		@JvmStatic
		fun altersTextureUV(preferenceFlags: Int, functionMask: Int): Boolean {
			return HdModel.altersTextureCoordinates(functionMask, preferenceFlags) and ModelTexCoordFlag.hasTextureCoordinates(preferenceFlags, functionMask)
		}

		@JvmField
		var modelViewXW: Float = 0f

		@JvmStatic
		fun retainsFaceIndices(functionMask: Int, preferenceFlags: Int): Boolean {
			return (HdModel.altersFacing(preferenceFlags, functionMask) or ((functionMask and 0x60000) != 0)) || HdModel.requiresColorBufferRebuild(functionMask, preferenceFlags) || copyElementsBuffer(functionMask, preferenceFlags)
		}

		@JvmStatic
		fun altersVerticesX(preferenceFlags: Int, functionMask: Int): Boolean {
			return (functionMask and 0x21) != 0
		}

		@JvmStatic
		fun altersLight(preferenceFlags: Int, functionMask: Int): Boolean {
			return ((functionMask and 0x18) != 0) or ((functionMask and 0x220) == 544)
		}

		@JvmField
		val textureCoordsOut: FloatArray = FloatArray(2)

		@JvmStatic
		fun retainsFaceColors(functionMask: Int, preferenceFlags: Int): Boolean {
			return GpuModelRetainMasks.altersFaceColors(preferenceFlags, functionMask) || HdModel.requiresColorBufferRebuild(functionMask, preferenceFlags)
		}

		@JvmStatic
		fun copyBillboards(functionMask: Int, preferenceFlags: Int): Boolean {
			return (functionMask and 0xC580) != 0
		}

		@JvmField
		val matrixY: IntArray = IntArray(8)

		@JvmStatic
		fun altersAlpha(functionMask: Int, preferenceFlags: Int): Boolean {
			return (functionMask and 0x100100) != 0
		}

		@JvmField
		var modelViewYW: Float = 0f

		@JvmStatic
		fun altersBillboards(functionMask: Int, preferenceFlags: Int): Boolean {
			return (functionMask and 0x400) != 0
		}

		@JvmStatic
		fun hasNormals(preferenceFlags: Int, functionMask: Int): Boolean {
			return (functionMask and 0x800) != 0 && (preferenceFlags and 0x37) != 0
		}

		@JvmStatic
		fun altersVerticesY(preferenceFlags: Int, functionMask: Int): Boolean {
			return (functionMask and 0x22) != 0
		}

		@JvmField
		var modelViewYY: Float = 0f
		@JvmStatic
		fun hasColors(preferenceFlags: Int, functionMask: Int): Boolean {
			return (functionMask and 0x800) != 0
		}

		@JvmStatic
		fun adjustLuminance(hsl: Int, luminanceFactor: Int): Int {
			var luminanceFactor: Int = luminanceFactor
			luminanceFactor = ((hsl and 0x7F) * luminanceFactor) shr 7
			if (luminanceFactor < 2) {
				luminanceFactor = 2
			} else if (luminanceFactor > 126) {
				luminanceFactor = 126
			}
			return luminanceFactor + (hsl and 0xFF80)
		}

		@JvmStatic
		fun retainsFaceAlphas(preferenceFlags: Int, functionMask: Int): Boolean {
			return (HdModel.altersAlpha(functionMask, preferenceFlags) or ((functionMask and 0x40000) != 0)) || HdModel.requiresColorBufferRebuild(functionMask, preferenceFlags)
		}

		@JvmStatic
		fun retainsNormals(functionMask: Int, preferenceFlags: Int): Boolean {
			if ((HdModel.altersLight(preferenceFlags, functionMask) or ((functionMask and 0x10000) != 0)) || HdModel.requiresNormalsBufferRebuild(functionMask, preferenceFlags)) {
				return true
			} else {
				return (preferenceFlags and 0x37) == 0 && HdModel.requiresColorBufferRebuild(functionMask, preferenceFlags)
			}
		}

		@JvmStatic
		fun retainsTextureCoordinates(functionMask: Int, preferenceFlags: Int): Boolean {
			return HdModel.altersTextureCoordinates(functionMask, preferenceFlags) || HdModel.altersTextureUV(preferenceFlags, functionMask)
		}
	}
}
