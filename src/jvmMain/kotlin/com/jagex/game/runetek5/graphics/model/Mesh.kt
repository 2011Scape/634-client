package com.jagex.game.runetek5.graphics.model

import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.model.normal.MeshBillboard
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.js5.Js5ResourceProvider

@OriginalClass("client!bh")
class Mesh {

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "[I")
	@JvmField
	var texturesScaleY: IntArray? = null

	@OriginalMember(owner = "client!bh", name = "e", descriptor = "[B")
	@JvmField
	var facesTexture: ByteArray? = null

	@OriginalMember(owner = "client!bh", name = "g", descriptor = "[S")
	@JvmField
	var facesMaterial: ShortArray? = null

	@OriginalMember(owner = "client!bh", name = "h", descriptor = "[B")
	@JvmField
	var texturesMapping: ByteArray? = null

	@OriginalMember(owner = "client!bh", name = "i", descriptor = "[I")
	@JvmField
	var faceGroups: IntArray? = null

	@OriginalMember(owner = "client!bh", name = "j", descriptor = "[S")
	@JvmField
	var textureFaceZ: ShortArray? = null

	@OriginalMember(owner = "client!bh", name = "n", descriptor = "[S")
	@JvmField
	var textureFaceX: ShortArray? = null

	@OriginalMember(owner = "client!bh", name = "q", descriptor = "[Lclient!pd;")
	@JvmField
	var billboards: Array<MeshBillboard?>? = null

	@OriginalMember(owner = "client!bh", name = "r", descriptor = "[B")
	@JvmField
	var texturesRotation: ByteArray? = null

	@OriginalMember(owner = "client!bh", name = "s", descriptor = "[B")
	@JvmField
	var facesAlpha: ByteArray? = null

	@OriginalMember(owner = "client!bh", name = "t", descriptor = "[S")
	@JvmField
	var facesB: ShortArray? = null

	@OriginalMember(owner = "client!bh", name = "x", descriptor = "[B")
	@JvmField
	var facesLayer: ByteArray? = null

	@OriginalMember(owner = "client!bh", name = "z", descriptor = "[I")
	@JvmField
	var verticesX: IntArray? = null

	@OriginalMember(owner = "client!bh", name = "A", descriptor = "[S")
	@JvmField
	var verticesSubmeshes: ShortArray? = null

	@OriginalMember(owner = "client!bh", name = "B", descriptor = "[B")
	@JvmField
	var faceRenderType: ByteArray? = null

	@OriginalMember(owner = "client!bh", name = "C", descriptor = "[I")
	@JvmField
	var texturesScaleZ: IntArray? = null

	@OriginalMember(owner = "client!bh", name = "E", descriptor = "[Lclient!nn;")
	@JvmField
	var particleEmitters: Array<EmissiveTriangle?>? = null

	@OriginalMember(owner = "client!bh", name = "F", descriptor = "[I")
	@JvmField
	var verticesY: IntArray? = null

	@OriginalMember(owner = "client!bh", name = "I", descriptor = "[S")
	@JvmField
	var textureFaceY: ShortArray? = null

	@OriginalMember(owner = "client!bh", name = "L", descriptor = "[I")
	@JvmField
	var verticesZ: IntArray? = null

	@OriginalMember(owner = "client!bh", name = "M", descriptor = "[B")
	@JvmField
	var texturesDirection: ByteArray? = null

	@OriginalMember(owner = "client!bh", name = "O", descriptor = "[S")
	@JvmField
	var facesC: ShortArray? = null

	@OriginalMember(owner = "client!bh", name = "P", descriptor = "[S")
	@JvmField
	var facesSubmeshes: ShortArray? = null

	@OriginalMember(owner = "client!bh", name = "Q", descriptor = "[I")
	@JvmField
	var texturesUtrans: IntArray? = null

	@OriginalMember(owner = "client!bh", name = "S", descriptor = "[I")
	@JvmField
	var texturesScaleX: IntArray? = null

	@OriginalMember(owner = "client!bh", name = "V", descriptor = "[S")
	@JvmField
	var facesColor: ShortArray? = null

	@OriginalMember(owner = "client!bh", name = "W", descriptor = "[S")
	@JvmField
	var facesA: ShortArray? = null

	@OriginalMember(owner = "client!bh", name = "Y", descriptor = "[I")
	@JvmField
	var texturesVtrans: IntArray? = null

	@OriginalMember(owner = "client!bh", name = "Z", descriptor = "[Lclient!eca;")
	@JvmField
	var particleEffectors: Array<EffectiveVertex?>? = null

	@OriginalMember(owner = "client!bh", name = "ab", descriptor = "[I")
	@JvmField
	var texturesSpeed: IntArray? = null

	@OriginalMember(owner = "client!bh", name = "bb", descriptor = "[I")
	@JvmField
	var vertexGroups: IntArray? = null

	@OriginalMember(owner = "client!bh", name = "o", descriptor = "I")
	@JvmField
	var version: Int = 12

	@OriginalMember(owner = "client!bh", name = "u", descriptor = "I")
	@JvmField
	var numVertices: Int = 0

	@OriginalMember(owner = "client!bh", name = "b", descriptor = "I")
	@JvmField
	var numFaces: Int = 0

	@OriginalMember(owner = "client!bh", name = "K", descriptor = "B")
	@JvmField
	var priority: Byte = 0

	@OriginalMember(owner = "client!bh", name = "f", descriptor = "I")
	@JvmField
	var maxDepth: Int = 0

	@OriginalMember(owner = "client!bh", name = "c", descriptor = "I")
	@JvmField
	var numTextures: Int = 0

	@OriginalMember(owner = "client!bh", name = "<init>", descriptor = "()V")
	private constructor() {
	}

	@OriginalMember(owner = "client!bh", name = "<init>", descriptor = "([B)V")
	constructor(@OriginalArg(0) data: ByteArray?) {
		if (data!![data.size - 1].toInt() == -1 && data[data.size - 2].toInt() == -1) {
			this.decodeNewFormat(data)
		} else {
			this.decodeOldFormat(data)
		}
	}

	@OriginalMember(owner = "client!bh", name = "<init>", descriptor = "(III)V")
	constructor(@OriginalArg(0) verticeCount: Int, @OriginalArg(1) faceCount: Int, @OriginalArg(2) textureCount: Int) {
		this.facesTexture = ByteArray(faceCount)
		this.facesA = ShortArray(faceCount)
		this.facesColor = ShortArray(faceCount)
		this.vertexGroups = IntArray(verticeCount)
		this.faceGroups = IntArray(faceCount)
		this.verticesZ = IntArray(verticeCount)
		if (textureCount > 0) {
			this.texturesUtrans = IntArray(textureCount)
			this.texturesSpeed = IntArray(textureCount)
			this.textureFaceY = ShortArray(textureCount)
			this.texturesScaleZ = IntArray(textureCount)
			this.textureFaceZ = ShortArray(textureCount)
			this.texturesVtrans = IntArray(textureCount)
			this.texturesRotation = ByteArray(textureCount)
			this.texturesScaleX = IntArray(textureCount)
			this.textureFaceX = ShortArray(textureCount)
			this.texturesMapping = ByteArray(textureCount)
			this.texturesScaleY = IntArray(textureCount)
			this.texturesDirection = ByteArray(textureCount)
		}
		this.facesMaterial = ShortArray(faceCount)
		this.facesLayer = ByteArray(faceCount)
		this.facesC = ShortArray(faceCount)
		this.facesB = ShortArray(faceCount)
		this.verticesX = IntArray(verticeCount)
		this.faceRenderType = ByteArray(faceCount)
		this.verticesY = IntArray(verticeCount)
		this.facesAlpha = ByteArray(faceCount)
	}

	@OriginalMember(owner = "client!bh", name = "<init>", descriptor = "([Lclient!bh;I)V")
	constructor(@OriginalArg(0) meshes: Array<Mesh?>?, @OriginalArg(1) meshCount: Int) {
		this.numTextures = 0
		this.numFaces = 0
		this.numVertices = 0
		@Pc(32) var emitterCount: Int = 0
		@Pc(34) var effectorCount: Int = 0
		@Pc(36) var billboardCount: Int = 0
		@Pc(38) var hasRenderType: Boolean = false
		@Pc(40) var hasPriorities: Boolean = false
		@Pc(42) var hasAlpha: Boolean = false
		@Pc(44) var hasFaceTextures: Boolean = false
		@Pc(46) var hasFaceMaterials: Boolean = false
		this.priority = -1
		@Pc(51) var hasFaceGroups: Boolean = false
		@Pc(53) var i: Int = 0
		while (i < meshCount) {
			@Pc(59) val mesh: Mesh? = meshes!![i]
			if (mesh != null) {
				this.numFaces += mesh.numFaces
				this.numVertices += mesh.numVertices
				this.numTextures += mesh.numTextures
				hasRenderType = hasRenderType or (mesh.faceRenderType != null)
				if (mesh.billboards != null) {
					billboardCount += mesh.billboards!!.size
				}
				if (mesh.particleEmitters != null) {
					emitterCount += mesh.particleEmitters!!.size
				}
				if (mesh.particleEffectors != null) {
					effectorCount += mesh.particleEffectors!!.size
				}
				hasFaceMaterials = hasFaceMaterials or (mesh.facesMaterial != null)
				hasFaceTextures = hasFaceTextures or (mesh.facesTexture != null)
				if (mesh.facesLayer == null) {
					if (this.priority.toInt() == -1) {
						this.priority = mesh.priority
					}
					if (mesh.priority != this.priority) {
						hasPriorities = true
					}
				} else {
					hasPriorities = true
				}
				hasAlpha = hasAlpha or (mesh.facesAlpha != null)
				hasFaceGroups = hasFaceGroups or (mesh.faceGroups != null)
			}
			i++
		}
		this.facesB = ShortArray(this.numFaces)
		if (this.numTextures > 0) {
			this.textureFaceX = ShortArray(this.numTextures)
			this.texturesMapping = ByteArray(this.numTextures)
			this.texturesSpeed = IntArray(this.numTextures)
			this.texturesScaleZ = IntArray(this.numTextures)
			this.texturesRotation = ByteArray(this.numTextures)
			this.texturesUtrans = IntArray(this.numTextures)
			this.texturesVtrans = IntArray(this.numTextures)
			this.texturesScaleX = IntArray(this.numTextures)
			this.texturesDirection = ByteArray(this.numTextures)
			this.textureFaceY = ShortArray(this.numTextures)
			this.texturesScaleY = IntArray(this.numTextures)
			this.textureFaceZ = ShortArray(this.numTextures)
		}
		this.facesSubmeshes = ShortArray(this.numFaces)
		if (hasFaceMaterials) {
			this.facesMaterial = ShortArray(this.numFaces)
		}
		this.facesA = ShortArray(this.numFaces)
		if (hasFaceGroups) {
			this.faceGroups = IntArray(this.numFaces)
		}
		if (hasPriorities) {
			this.facesLayer = ByteArray(this.numFaces)
		}
		if (hasRenderType) {
			this.faceRenderType = ByteArray(this.numFaces)
		}
		if (billboardCount > 0) {
			this.billboards = arrayOfNulls<MeshBillboard>(billboardCount)
		}
		if (effectorCount > 0) {
			this.particleEffectors = arrayOfNulls<EffectiveVertex>(effectorCount)
		}
		this.vertexGroups = IntArray(this.numVertices)
		if (hasFaceTextures) {
			this.facesTexture = ByteArray(this.numFaces)
		}
		this.verticesZ = IntArray(this.numVertices)
		this.facesColor = ShortArray(this.numFaces)
		this.verticesX = IntArray(this.numVertices)
		this.verticesY = IntArray(this.numVertices)
		if (emitterCount > 0) {
			this.particleEmitters = arrayOfNulls<EmissiveTriangle>(emitterCount)
		}
		this.facesC = ShortArray(this.numFaces)
		if (hasAlpha) {
			this.facesAlpha = ByteArray(this.numFaces)
		}
		this.verticesSubmeshes = ShortArray(this.numVertices)
		this.numTextures = 0
		effectorCount = 0
		billboardCount = 0
		emitterCount = 0
		this.numFaces = 0
		this.numVertices = 0
		@Pc(616) var local616: Int
		@Pc(627) var local627: Int
		@Pc(383) var j: Int = 0
		while (j < meshCount) {
			@Pc(390) val meshFlag: Short = (0x1 shl j).toShort()
			@Pc(394) val mesh2: Mesh? = meshes!![j]
			if (mesh2 != null) {
				@Pc(401) var local401: Int
				if (mesh2.billboards != null) {
					var billboardIndex: Int = 0
					while (billboardIndex < mesh2.billboards!!.size) {
						@Pc(408) val meshBillboard: MeshBillboard? = mesh2.billboards!![billboardIndex]
						this.billboards!![billboardCount++] = meshBillboard!!.copyWithFace(meshBillboard.appliesOnFace + this.numFaces)
						billboardIndex++
					}
				}
				var face: Int = 0
				while (face < mesh2.numFaces) {
					if (hasRenderType && mesh2.faceRenderType != null) {
						this.faceRenderType!![this.numFaces] = mesh2.faceRenderType!![face]
					}
					if (hasPriorities) {
						if (mesh2.facesLayer == null) {
							this.facesLayer!![this.numFaces] = mesh2.priority
						} else {
							this.facesLayer!![this.numFaces] = mesh2.facesLayer!![face]
						}
					}
					if (hasAlpha && mesh2.facesAlpha != null) {
						this.facesAlpha!![this.numFaces] = mesh2.facesAlpha!![face]
					}
					if (hasFaceMaterials) {
						if (mesh2.facesMaterial == null) {
							this.facesMaterial!![this.numFaces] = -1
						} else {
							this.facesMaterial!![this.numFaces] = mesh2.facesMaterial!![face]
						}
					}
					if (hasFaceGroups) {
						if (mesh2.faceGroups == null) {
							this.faceGroups!![this.numFaces] = -1
						} else {
							this.faceGroups!![this.numFaces] = mesh2.faceGroups!![face]
						}
					}
					this.facesA!![this.numFaces] = this.connect(mesh2, mesh2.facesA!![face].toInt(), meshFlag).toShort()
					this.facesB!![this.numFaces] = this.connect(mesh2, mesh2.facesB!![face].toInt(), meshFlag).toShort()
					this.facesC!![this.numFaces] = this.connect(mesh2, mesh2.facesC!![face].toInt(), meshFlag).toShort()
					this.facesSubmeshes!![this.numFaces] = meshFlag
					this.facesColor!![this.numFaces] = mesh2.facesColor!![face]
					this.numFaces++
					face++
				}
				@Pc(603) var local603: Int
				if (mesh2.particleEmitters != null) {
					var emitterIndex: Int = 0
					while (emitterIndex < mesh2.particleEmitters!!.size) {
						val emitterVertexA: Int = this.connect(mesh2, mesh2.particleEmitters!![emitterIndex]!!.faceA, meshFlag)
						val emitterVertexB: Int = this.connect(mesh2, mesh2.particleEmitters!![emitterIndex]!!.faceB, meshFlag)
						@Pc(638) val emitterVertexC: Int = this.connect(mesh2, mesh2.particleEmitters!![emitterIndex]!!.vertexC, meshFlag)
						this.particleEmitters!![emitterCount] = mesh2.particleEmitters!![emitterIndex]!!.copyWithFaces(emitterVertexB, emitterVertexC, emitterVertexA)
						emitterCount++
						emitterIndex++
					}
				}
				if (mesh2.particleEffectors != null) {
					var effectorIndex: Int = 0
					while (effectorIndex < mesh2.particleEffectors!!.size) {
						val effectorVertex: Int = this.connect(mesh2, mesh2.particleEffectors!![effectorIndex]!!.appliesOnVertex, meshFlag)
						this.particleEffectors!![effectorCount] = mesh2.particleEffectors!![effectorIndex]!!.copyWithVertex(effectorVertex)
						effectorCount++
						effectorIndex++
					}
				}
			}
			j++
		}
		@Pc(708) var faceIndex: Int = 0
		this.maxDepth = this.numVertices
		@Pc(714) var k: Int = 0
		while (k < meshCount) {
			@Pc(721) val meshFlag: Short = (0x1 shl k).toShort()
			@Pc(725) val mesh3: Mesh? = meshes!![k]
			if (mesh3 != null) {
				var face: Int = 0
				while (face < mesh3.numFaces) {
					if (hasFaceTextures) {
						this.facesTexture!![faceIndex++] = (if (mesh3.facesTexture == null || mesh3.facesTexture!![face].toInt() == -1) -1 else mesh3.facesTexture!![face] + this.numTextures).toByte()
					}
					face++
				}
				var texture: Int = 0
				while (texture < mesh3.numTextures) {
					this.texturesMapping!![this.numTextures] = mesh3.texturesMapping!![texture]
					@Pc(778) val mapping: Byte = this.texturesMapping!![this.numTextures]
					if (mapping.toInt() == 0) {
						this.textureFaceX!![this.numTextures] = this.connect(mesh3, mesh3.textureFaceX!![texture].toInt(), meshFlag).toShort()
						this.textureFaceY!![this.numTextures] = this.connect(mesh3, mesh3.textureFaceY!![texture].toInt(), meshFlag).toShort()
						this.textureFaceZ!![this.numTextures] = this.connect(mesh3, mesh3.textureFaceZ!![texture].toInt(), meshFlag).toShort()
					}
					if (mapping.toInt() >= 1 && mapping.toInt() <= 3) {
						this.textureFaceX!![this.numTextures] = mesh3.textureFaceX!![texture]
						this.textureFaceY!![this.numTextures] = mesh3.textureFaceY!![texture]
						this.textureFaceZ!![this.numTextures] = mesh3.textureFaceZ!![texture]
						this.texturesScaleX!![this.numTextures] = mesh3.texturesScaleX!![texture]
						this.texturesScaleY!![this.numTextures] = mesh3.texturesScaleY!![texture]
						this.texturesScaleZ!![this.numTextures] = mesh3.texturesScaleZ!![texture]
						this.texturesRotation!![this.numTextures] = mesh3.texturesRotation!![texture]
						this.texturesDirection!![this.numTextures] = mesh3.texturesDirection!![texture]
						this.texturesSpeed!![this.numTextures] = mesh3.texturesSpeed!![texture]
					}
					if (mapping.toInt() == 2) {
						this.texturesUtrans!![this.numTextures] = mesh3.texturesUtrans!![texture]
						this.texturesVtrans!![this.numTextures] = mesh3.texturesVtrans!![texture]
					}
					this.numTextures++
					texture++
				}
			}
			k++
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(II)V")
	fun upscale() {
		@Pc(7) var i: Int = 0
		while (i < this.numVertices) {
			this.verticesX!![i] = this.verticesX!![i] shl 0x2
			this.verticesY!![i] = this.verticesY!![i] shl 0x2
			this.verticesZ!![i] = this.verticesZ!![i] shl 0x2
			i++
		}
		if (this.numTextures <= 0 || this.texturesScaleX == null) {
			return
		}
		@Pc(58) var j: Int = 0
		while (j < this.texturesScaleX!!.size) {
			this.texturesScaleX!![j] = this.texturesScaleX!![j] shl 0x2
			this.texturesScaleY!![j] = this.texturesScaleY!![j] shl 0x2
			if (this.texturesMapping!![j].toInt() != 1) {
				this.texturesScaleZ!![j] = this.texturesScaleZ!![j] shl 0x2
			}
			j++
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(B)[[I")
	fun createBillboardGroups(): Array<IntArray?> {
		@Pc(8) val counts: IntArray = IntArray(256)
		@Pc(10) var max: Int = 0
		@Pc(12) var i: Int = 0
		while (i < this.billboards!!.size) {
			@Pc(20) val group: Int = this.billboards!![i]!!.depth
			if (group >= 0) {
				@Pc(26) val previousCount: Int = counts[group]++
				if (max < group) {
					max = group
				}
			}
			i++
		}
		@Pc(45) val groups: Array<IntArray?> = arrayOfNulls<IntArray>(max + 1)
		@Pc(47) var j: Int = 0
		while (j <= max) {
			groups[j] = IntArray(counts[j])
			counts[j] = 0
			j++
		}
		@Pc(75) var k: Int = 0
		while (k < this.billboards!!.size) {
			@Pc(83) val depth: Int = this.billboards!![k]!!.depth
			if (depth >= 0) {
				groups[depth]!![counts[depth]++] = k
			}
			k++
		}
		return groups
	}

	@OriginalMember(owner = "client!bh", name = "b", descriptor = "(B)[[I")
	fun createFaceGroups(): Array<IntArray?> {
		@Pc(8) val counts: IntArray = IntArray(256)
		@Pc(10) var max: Int = 0
		@Pc(12) var i: Int = 0
		while (i < this.numFaces) {
			@Pc(19) val group: Int = this.faceGroups!![i]
			if (group >= 0) {
				if (max < group) {
					max = group
				}
				@Pc(33) val previousCount: Int = counts[group]++
			}
			i++
		}
		@Pc(46) val groups: Array<IntArray?> = arrayOfNulls<IntArray>(max + 1)
		@Pc(63) var j: Int = 0
		while (j <= max) {
			groups[j] = IntArray(counts[j])
			counts[j] = 0
			j++
		}
		@Pc(86) var k: Int = 0
		while (k < this.numFaces) {
			@Pc(93) val faceGroup: Int = this.faceGroups!![k]
			if (faceGroup >= 0) {
				groups[faceGroup]!![counts[faceGroup]++] = k
			}
			k++
		}
		return groups
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(SIS)V")
	fun retexture(@OriginalArg(0) src: Short, @OriginalArg(2) dst: Short) {
		if (this.facesMaterial == null) {
			return
		}
		@Pc(11) var i: Int = 0
		while (i < this.numFaces) {
			if (src == this.facesMaterial!![i]) {
				this.facesMaterial!![i] = dst
			}
			i++
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(SBS)V")
	fun recolor(@OriginalArg(0) src: Short, @OriginalArg(2) dst: Short) {
		@Pc(7) var i: Int = 0
		while (i < this.numFaces) {
			if (this.facesColor!![i] == src) {
				this.facesColor!![i] = dst
			}
			i++
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(BIII)V")
	fun translate(@OriginalArg(1) deltaZ: Int, @OriginalArg(2) deltaX: Int, @OriginalArg(3) deltaY: Int) {
		@Pc(3) var i: Int = 0
		while (i < this.numVertices) {
			this.verticesX!![i] = this.verticesX!![i] + deltaX
			this.verticesY!![i] = this.verticesY!![i] + deltaY
			this.verticesZ!![i] = this.verticesZ!![i] + deltaZ
			i++
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(ZI)[[I")
	fun createVertexGroups(@OriginalArg(0) flag: Boolean): Array<IntArray?> {
		@Pc(8) val counts: IntArray = IntArray(256)
		@Pc(10) var max: Int = 0
		@Pc(18) val count: Int = if (flag) this.numVertices else this.maxDepth
		@Pc(20) var i: Int = 0
		while (i < count) {
			@Pc(27) val group: Int = this.vertexGroups!![i]
			if (group >= 0) {
				@Pc(36) val previousCount: Int = counts[group]++
				if (max < group) {
					max = group
				}
			}
			i++
		}
		@Pc(53) val groups: Array<IntArray?> = arrayOfNulls<IntArray>(max + 1)
		@Pc(55) var j: Int = 0
		while (j <= max) {
			groups[j] = IntArray(counts[j])
			counts[j] = 0
			j++
		}
		@Pc(78) var k: Int = 0
		while (k < count) {
			@Pc(85) val vertexGroup: Int = this.vertexGroups!![k]
			if (vertexGroup >= 0) {
				groups[vertexGroup]!![counts[vertexGroup]++] = k
			}
			k++
		}
		return groups
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(IIII)I")
	fun addVertex(@OriginalArg(0) y: Int, @OriginalArg(1) z: Int, @OriginalArg(2) x: Int): Int {
		@Pc(7) var i: Int = 0
		while (i < this.numVertices) {
			if (this.verticesX!![i] == x && y == this.verticesY!![i] && this.verticesZ!![i] == z) {
				return i
			}
			i++
		}
		this.verticesX!![this.numVertices] = x
		this.verticesY!![this.numVertices] = y
		this.verticesZ!![this.numVertices] = z
		this.maxDepth = this.numVertices + 1
		return this.numVertices++
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(IBBSBSIBI)I")
	fun addFace(@OriginalArg(0) faceA: Int, @OriginalArg(1) faceRenderType: Byte, @OriginalArg(2) faceTexture: Byte, @OriginalArg(3) faceMaterial: Short, @OriginalArg(5) faceColor: Short, @OriginalArg(6) faceB: Int, @OriginalArg(7) faceAlpha: Byte, @OriginalArg(8) faceC: Int): Int {
		this.facesA!![this.numFaces] = faceA.toShort()
		this.facesB!![this.numFaces] = faceB.toShort()
		this.facesC!![this.numFaces] = faceC.toShort()
		this.faceRenderType!![this.numFaces] = faceRenderType
		this.facesTexture!![this.numFaces] = faceTexture
		this.facesColor!![this.numFaces] = faceColor
		this.facesAlpha!![this.numFaces] = faceAlpha
		this.facesMaterial!![this.numFaces] = faceMaterial
		return this.numFaces++
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(I[B)V")
	private fun decodeOldFormat(@OriginalArg(1) data: ByteArray) {
		@Pc(7) var hasFaceTypes: Boolean = false
		@Pc(9) var hasMaterials: Boolean = false
		@Pc(14) val packet: Packet = Packet(data)
		@Pc(19) val packet2: Packet = Packet(data)
		@Pc(24) val packet3: Packet = Packet(data)
		@Pc(29) val packet4: Packet = Packet(data)
		@Pc(34) val packet5: Packet = Packet(data)
		packet.currentPosition = data.size - 18
		this.numVertices = packet.readUShort()
		this.numFaces = packet.readUShort()
		this.numTextures = packet.readUByte()
		@Pc(61) val hasFaceInfo: Int = packet.readUByte()
		@Pc(65) val modelPriority: Int = packet.readUByte()
		@Pc(69) val hasFaceAlpha: Int = packet.readUByte()
		@Pc(73) val hasFaceGroups: Int = packet.readUByte()
		@Pc(77) val hasVertexGroups: Int = packet.readUByte()
		@Pc(81) val verticesXSize: Int = packet.readUShort()
		@Pc(85) val verticesYSize: Int = packet.readUShort()
		@Pc(89) val verticesZSize: Int = packet.readUShort()
		@Pc(93) val faceIndicesSize: Int = packet.readUShort()
		@Pc(102) var offset: Int = this.numVertices
		@Pc(104) val faceIndexTypesOffset: Int = offset
		offset += this.numFaces
		@Pc(111) val facePriorityOffset: Int = offset
		if (modelPriority == 255) {
			offset += this.numFaces
		}
		@Pc(123) val faceGroupsOffset: Int = offset
		if (hasFaceGroups == 1) {
			offset += this.numFaces
		}
		@Pc(135) val faceTypeOffset: Int = offset
		if (hasFaceInfo == 1) {
			offset += this.numFaces
		}
		@Pc(145) val vertexGroupsOffset: Int = offset
		if (hasVertexGroups == 1) {
			offset += this.numVertices
		}
		@Pc(155) val faceAlphaOffset: Int = offset
		if (hasFaceAlpha == 1) {
			offset += this.numFaces
		}
		@Pc(165) val faceIndicesDataOffset: Int = offset
		offset += faceIndicesSize
		@Pc(171) val faceColorOffset: Int = offset
		offset += this.numFaces * 2
		@Pc(180) val textureTriOffset: Int = offset
		offset += this.numTextures * 6
		@Pc(189) val verticesXDataOffset: Int = offset
		offset += verticesXSize
		@Pc(195) val verticesYDataOffset: Int = offset
		offset += verticesYSize
		this.facesColor = ShortArray(this.numFaces)
		if (modelPriority == 255) {
			this.facesLayer = ByteArray(this.numFaces)
		} else {
			this.priority = modelPriority.toByte()
		}
		if (hasVertexGroups == 1) {
			this.vertexGroups = IntArray(this.numVertices)
		}
		if (hasFaceAlpha == 1) {
			this.facesAlpha = ByteArray(this.numFaces)
		}
		this.facesA = ShortArray(this.numFaces)
		if (hasFaceInfo == 1) {
			this.facesMaterial = ShortArray(this.numFaces)
			this.facesTexture = ByteArray(this.numFaces)
			this.faceRenderType = ByteArray(this.numFaces)
		}
		this.verticesY = IntArray(this.numVertices)
		if (hasFaceGroups == 1) {
			this.faceGroups = IntArray(this.numFaces)
		}
		this.facesC = ShortArray(this.numFaces)
		if (this.numTextures > 0) {
			this.texturesMapping = ByteArray(this.numTextures)
			this.textureFaceX = ShortArray(this.numTextures)
			this.textureFaceY = ShortArray(this.numTextures)
			this.textureFaceZ = ShortArray(this.numTextures)
		}
		this.facesB = ShortArray(this.numFaces)
		this.verticesZ = IntArray(this.numVertices)
		this.verticesX = IntArray(this.numVertices)
		packet.currentPosition = 0
		packet2.currentPosition = verticesXDataOffset
		packet3.currentPosition = verticesYDataOffset
		packet4.currentPosition = offset
		packet5.currentPosition = vertexGroupsOffset
		@Pc(342) var lastVertexX: Int = 0
		@Pc(344) var lastVertexY: Int = 0
		@Pc(346) var lastVertexZ: Int = 0
		@Pc(354) var local354: Int
		@Pc(356) var local356: Int
		@Pc(348) var i: Int = 0
		while (i < this.numVertices) {
			val vertexFlags: Int = packet.readUByte()
			var deltaX: Int = 0
			if ((vertexFlags and 0x1) != 0) {
				deltaX = packet2.readSmart()
			}
			@Pc(366) var deltaY: Int = 0
			if ((vertexFlags and 0x2) != 0) {
				deltaY = packet3.readSmart()
			}
			@Pc(379) var deltaZ: Int = 0
			if ((vertexFlags and 0x4) != 0) {
				deltaZ = packet4.readSmart()
			}
			this.verticesX!![i] = deltaX + lastVertexX
			this.verticesY!![i] = lastVertexY + deltaY
			this.verticesZ!![i] = lastVertexZ + deltaZ
			lastVertexZ = this.verticesZ!![i]
			lastVertexY = this.verticesY!![i]
			lastVertexX = this.verticesX!![i]
			if (hasVertexGroups == 1) {
				this.vertexGroups!![i] = packet5.readUByte()
			}
			i++
		}
		packet.currentPosition = faceColorOffset
		packet2.currentPosition = faceTypeOffset
		packet3.currentPosition = facePriorityOffset
		packet4.currentPosition = faceAlphaOffset
		packet5.currentPosition = faceGroupsOffset
		var face: Int = 0
		while (face < this.numFaces) {
			this.facesColor!![face] = packet.readUShort().toShort()
			if (hasFaceInfo == 1) {
				val faceInfo: Int = packet2.readUByte()
				if ((faceInfo and 0x1) == 1) {
					this.faceRenderType!![face] = 1
					hasFaceTypes = true
				} else {
					this.faceRenderType!![face] = 0
				}
				if ((faceInfo and 0x2) == 2) {
					this.facesTexture!![face] = (faceInfo shr 2).toByte()
					this.facesMaterial!![face] = this.facesColor!![face]
					this.facesColor!![face] = 127
					if (this.facesMaterial!![face].toInt() != -1) {
						hasMaterials = true
					}
				} else {
					this.facesTexture!![face] = -1
					this.facesMaterial!![face] = -1
				}
			}
			if (modelPriority == 255) {
				this.facesLayer!![face] = packet3.readByte()
			}
			if (hasFaceAlpha == 1) {
				this.facesAlpha!![face] = packet4.readByte()
			}
			if (hasFaceGroups == 1) {
				this.faceGroups!![face] = packet5.readUByte()
			}
			face++
		}
		packet.currentPosition = faceIndicesDataOffset
		this.maxDepth = -1
		packet2.currentPosition = faceIndexTypesOffset
		@Pc(595) var vertexA: Short = 0
		@Pc(597) var vertexB: Short = 0
		@Pc(599) var vertexC: Short = 0
		@Pc(601) var lastVertex: Short = 0
		@Pc(609) var local609: Int
		@Pc(603) var j: Int = 0
		while (j < this.numFaces) {
			val faceIndexType: Int = packet2.readUByte()
			if (faceIndexType == 1) {
				vertexA = (lastVertex + packet.readSmart()).toShort()
				vertexB = (packet.readSmart() + vertexA).toShort()
				vertexC = (vertexB + packet.readSmart()).toShort()
				this.facesA!![j] = vertexA
				lastVertex = vertexC
				this.facesB!![j] = vertexB
				this.facesC!![j] = vertexC
				if (this.maxDepth < vertexA) {
					this.maxDepth = vertexA.toInt()
				}
				if (vertexB > this.maxDepth) {
					this.maxDepth = vertexB.toInt()
				}
				if (vertexC > this.maxDepth) {
					this.maxDepth = vertexC.toInt()
				}
			}
			if (faceIndexType == 2) {
				vertexB = vertexC
				vertexC = (packet.readSmart() + lastVertex).toShort()
				this.facesA!![j] = vertexA
				lastVertex = vertexC
				this.facesB!![j] = vertexB
				this.facesC!![j] = vertexC
				if (vertexC > this.maxDepth) {
					this.maxDepth = vertexC.toInt()
				}
			}
			if (faceIndexType == 3) {
				vertexA = vertexC
				vertexC = (packet.readSmart() + lastVertex).toShort()
				lastVertex = vertexC
				this.facesA!![j] = vertexA
				this.facesB!![j] = vertexB
				this.facesC!![j] = vertexC
				if (this.maxDepth < vertexC) {
					this.maxDepth = vertexC.toInt()
				}
			}
			if (faceIndexType == 4) {
				@Pc(768) val temp: Short = vertexA
				vertexA = vertexB
				vertexC = (packet.readSmart() + lastVertex).toShort()
				vertexB = temp
				lastVertex = vertexC
				this.facesA!![j] = vertexA
				this.facesB!![j] = temp
				this.facesC!![j] = vertexC
				if (this.maxDepth < vertexC) {
					this.maxDepth = vertexC.toInt()
				}
			}
			j++
		}
		this.maxDepth++
		packet.currentPosition = textureTriOffset
		var texture: Int = 0
		while (texture < this.numTextures) {
			this.texturesMapping!![texture] = 0
			this.textureFaceX!![texture] = packet.readUShort().toShort()
			this.textureFaceY!![texture] = packet.readUShort().toShort()
			this.textureFaceZ!![texture] = packet.readUShort().toShort()
			texture++
		}
		if (this.facesTexture != null) {
			@Pc(868) var usesTextureFaces: Boolean = false
			@Pc(870) var k: Int = 0
			while (k < this.numFaces) {
				@Pc(879) val textureIndex: Int = this.facesTexture!![k].toInt() and 0xFF
				if (textureIndex != 255) {
					if ((this.textureFaceX!![textureIndex].toInt() and 0xFFFF) == this.facesA!![k].toInt() && this.facesB!![k].toInt() == (this.textureFaceY!![textureIndex].toInt() and 0xFFFF) && (this.textureFaceZ!![textureIndex].toInt() and 0xFFFF) == this.facesC!![k].toInt()) {
						this.facesTexture!![k] = -1
					} else {
						usesTextureFaces = true
					}
				}
				k++
			}
			if (!usesTextureFaces) {
				this.facesTexture = null
			}
		}
		if (!hasFaceTypes) {
			this.faceRenderType = null
		}
		if (!hasMaterials) {
			this.facesMaterial = null
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(Lclient!bh;IIS)I")
	private fun connect(@OriginalArg(0) mesh: Mesh, @OriginalArg(2) vertex: Int, @OriginalArg(3) submeshFlag: Short): Int {
		@Pc(10) val x: Int = mesh.verticesX!![vertex]
		@Pc(15) val y: Int = mesh.verticesY!![vertex]
		@Pc(20) val z: Int = mesh.verticesZ!![vertex]
		@Pc(22) var i: Int = 0
		while (i < this.numVertices) {
			if (this.verticesX!![i] == x && y == this.verticesY!![i] && this.verticesZ!![i] == z) {
				this.verticesSubmeshes!![i] = (this.verticesSubmeshes!![i].toInt() or submeshFlag.toInt()).toShort()
				return i
			}
			i++
		}
		this.verticesX!![this.numVertices] = x
		this.verticesY!![this.numVertices] = y
		this.verticesZ!![this.numVertices] = z
		this.verticesSubmeshes!![this.numVertices] = submeshFlag
		this.vertexGroups!![this.numVertices] = if (mesh.vertexGroups == null) -1 else mesh.vertexGroups!![vertex]
		return this.numVertices++
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(IBBSSBSSSS)B")
	fun addSunTexture(): Byte {
		if (this.numTextures >= 255) {
			throw IllegalStateException()
		}
		this.texturesMapping!![this.numTextures] = 3
		this.textureFaceX!![this.numTextures] = 0
		this.textureFaceY!![this.numTextures] = 32767
		this.textureFaceZ!![this.numTextures] = 0
		this.texturesScaleX!![this.numTextures] = 1024
		this.texturesScaleY!![this.numTextures] = 1024
		this.texturesScaleZ!![this.numTextures] = 1024
		this.texturesRotation!![this.numTextures] = 0
		this.texturesDirection!![this.numTextures] = 0
		this.texturesSpeed!![this.numTextures] = 0
		return (this.numTextures++).toByte()
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "([BB)V")
	private fun decodeNewFormat(@OriginalArg(0) data: ByteArray) {
		@Pc(10) val packet: Packet = Packet(data)
		@Pc(15) val packet2: Packet = Packet(data)
		@Pc(20) val packet3: Packet = Packet(data)
		@Pc(25) val packet4: Packet = Packet(data)
		@Pc(30) val packet5: Packet = Packet(data)
		@Pc(35) val packet6: Packet = Packet(data)
		@Pc(40) val packet7: Packet = Packet(data)
		packet.currentPosition = data.size - 23
		this.numVertices = packet.readUShort()
		this.numFaces = packet.readUShort()
		this.numTextures = packet.readUByte()
		@Pc(65) val footerFlags: Int = packet.readUByte()
		@Pc(76) val hasFaceTypes: Boolean = (footerFlags and 0x1) == 1
		@Pc(85) val hasParticleEffects: Boolean = (footerFlags and 0x2) == 2
		@Pc(96) val hasBillboards: Boolean = (footerFlags and 0x4) == 4
		@Pc(105) val hasVersion: Boolean = (footerFlags and 0x8) == 8
		if (hasVersion) {
			packet.currentPosition -= 7
			this.version = packet.readUByte()
			packet.currentPosition += 6
		}
		@Pc(128) val modelPriority: Int = packet.readUByte()
		@Pc(132) val hasFaceAlpha: Int = packet.readUByte()
		@Pc(136) val hasFaceGroups: Int = packet.readUByte()
		@Pc(140) val hasFaceTextures: Int = packet.readUByte()
		@Pc(144) val hasVertexGroups: Int = packet.readUByte()
		@Pc(148) val verticesXSize: Int = packet.readUShort()
		@Pc(152) val verticesYSize: Int = packet.readUShort()
		@Pc(156) val verticesZSize: Int = packet.readUShort()
		@Pc(160) val faceIndicesSize: Int = packet.readUShort()
		@Pc(164) val textureIndicesSize: Int = packet.readUShort()
		@Pc(166) var simpleTextureCount: Int = 0
		@Pc(168) var complexTextureCount: Int = 0
		@Pc(170) var cubeTextureCount: Int = 0
		@Pc(186) var local186: Int
		if (this.numTextures > 0) {
			packet.currentPosition = 0
			this.texturesMapping = ByteArray(this.numTextures)
			var texture: Int = 0
			while (texture < this.numTextures) {
				@Pc(197) val mapping: Byte = packet.readByte()
				this.texturesMapping!![texture] = mapping
				if (mapping >= 1 && mapping <= 3) {
					complexTextureCount++
				}
				if (mapping.toInt() == 2) {
					cubeTextureCount++
				}
				if (mapping.toInt() == 0) {
					simpleTextureCount++
				}
				texture++
			}
		}
		var dataOffset: Int = this.numTextures
		@Pc(227) val vertexFlagsOffset: Int = dataOffset
		dataOffset += this.numVertices
		@Pc(234) val faceTypesOffset: Int = dataOffset
		if (hasFaceTypes) {
			dataOffset += this.numFaces
		}
		@Pc(243) val faceIndexTypesOffset: Int = dataOffset
		dataOffset += this.numFaces
		@Pc(250) val facePriorityOffset: Int = dataOffset
		if (modelPriority == 255) {
			dataOffset += this.numFaces
		}
		@Pc(262) val faceGroupsOffset: Int = dataOffset
		if (hasFaceGroups == 1) {
			dataOffset += this.numFaces
		}
		@Pc(274) val vertexGroupsOffset: Int = dataOffset
		if (hasVertexGroups == 1) {
			dataOffset += this.numVertices
		}
		@Pc(284) val faceAlphaOffset: Int = dataOffset
		if (hasFaceAlpha == 1) {
			dataOffset += this.numFaces
		}
		@Pc(300) val faceIndicesDataOffset: Int = dataOffset
		dataOffset += faceIndicesSize
		@Pc(306) val faceMaterialOffset: Int = dataOffset
		if (hasFaceTextures == 1) {
			dataOffset += this.numFaces * 2
		}
		@Pc(318) val textureIndexDataOffset: Int = dataOffset
		dataOffset += textureIndicesSize
		@Pc(324) val faceColorOffset: Int = dataOffset
		dataOffset += this.numFaces * 2
		@Pc(333) val verticesXOffset: Int = dataOffset
		dataOffset += verticesXSize
		@Pc(339) val verticesYOffset: Int = dataOffset
		dataOffset += verticesYSize
		@Pc(345) val verticesZOffset: Int = dataOffset
		dataOffset += verticesZSize
		@Pc(351) val simpleTextureOffset: Int = dataOffset
		dataOffset += simpleTextureCount * 6
		@Pc(359) val complexTextureVertOffset: Int = dataOffset
		dataOffset += complexTextureCount * 6
		@Pc(367) var textureScaleBytes: Byte = 6
		if (this.version == 14) {
			textureScaleBytes = 7
		} else if (this.version >= 15) {
			textureScaleBytes = 9
		}
		@Pc(383) val complexTextureScaleOffset: Int = dataOffset
		dataOffset += textureScaleBytes * complexTextureCount
		@Pc(391) val complexTextureRotOffset: Int = dataOffset
		dataOffset += complexTextureCount
		@Pc(397) val complexTextureDirOffset: Int = dataOffset
		dataOffset += complexTextureCount
		@Pc(403) val complexTextureSpeedOffset: Int = dataOffset
		dataOffset += cubeTextureCount * 2 + complexTextureCount
		this.facesB = ShortArray(this.numFaces)
		this.facesColor = ShortArray(this.numFaces)
		if (hasFaceGroups == 1) {
			this.faceGroups = IntArray(this.numFaces)
		}
		if (this.numTextures > 0) {
			if (cubeTextureCount > 0) {
				this.texturesUtrans = IntArray(cubeTextureCount)
				this.texturesVtrans = IntArray(cubeTextureCount)
			}
			this.textureFaceY = ShortArray(this.numTextures)
			this.textureFaceX = ShortArray(this.numTextures)
			if (complexTextureCount > 0) {
				this.texturesSpeed = IntArray(complexTextureCount)
				this.texturesDirection = ByteArray(complexTextureCount)
				this.texturesScaleY = IntArray(complexTextureCount)
				this.texturesRotation = ByteArray(complexTextureCount)
				this.texturesScaleZ = IntArray(complexTextureCount)
				this.texturesScaleX = IntArray(complexTextureCount)
			}
			this.textureFaceZ = ShortArray(this.numTextures)
		}
		if (hasFaceTypes) {
			this.faceRenderType = ByteArray(this.numFaces)
		}
		if (hasFaceAlpha == 1) {
			this.facesAlpha = ByteArray(this.numFaces)
		}
		if (modelPriority == 255) {
			this.facesLayer = ByteArray(this.numFaces)
		} else {
			this.priority = modelPriority.toByte()
		}
		this.facesC = ShortArray(this.numFaces)
		this.verticesY = IntArray(this.numVertices)
		this.facesA = ShortArray(this.numFaces)
		if (hasFaceTextures == 1 && this.numTextures > 0) {
			this.facesTexture = ByteArray(this.numFaces)
		}
		this.verticesZ = IntArray(this.numVertices)
		this.verticesX = IntArray(this.numVertices)
		if (hasVertexGroups == 1) {
			this.vertexGroups = IntArray(this.numVertices)
		}
		packet.currentPosition = vertexFlagsOffset
		if (hasFaceTextures == 1) {
			this.facesMaterial = ShortArray(this.numFaces)
		}
		packet2.currentPosition = verticesXOffset
		packet3.currentPosition = verticesYOffset
		packet4.currentPosition = verticesZOffset
		packet5.currentPosition = vertexGroupsOffset
		@Pc(601) var lastVertexX: Int = 0
		@Pc(603) var lastVertexY: Int = 0
		@Pc(605) var lastVertexZ: Int = 0
		@Pc(613) var local613: Int
		@Pc(607) var i: Int = 0
		while (i < this.numVertices) {
			val vertexFlags: Int = packet.readUByte()
			@Pc(615) var deltaX: Int = 0
			if ((vertexFlags and 0x1) != 0) {
				deltaX = packet2.readSmart()
			}
			@Pc(628) var deltaY: Int = 0
			if ((vertexFlags and 0x2) != 0) {
				deltaY = packet3.readSmart()
			}
			@Pc(641) var deltaZ: Int = 0
			if ((vertexFlags and 0x4) != 0) {
				deltaZ = packet4.readSmart()
			}
			this.verticesX!![i] = deltaX + lastVertexX
			this.verticesY!![i] = deltaY + lastVertexY
			this.verticesZ!![i] = lastVertexZ + deltaZ
			lastVertexY = this.verticesY!![i]
			lastVertexX = this.verticesX!![i]
			lastVertexZ = this.verticesZ!![i]
			if (hasVertexGroups == 1) {
				this.vertexGroups!![i] = packet5.readUByte()
			}
			i++
		}
		packet.currentPosition = faceColorOffset
		packet2.currentPosition = faceTypesOffset
		packet3.currentPosition = facePriorityOffset
		packet4.currentPosition = faceAlphaOffset
		packet5.currentPosition = faceGroupsOffset
		packet6.currentPosition = faceMaterialOffset
		packet7.currentPosition = textureIndexDataOffset
		var face: Int = 0
		while (face < this.numFaces) {
			this.facesColor!![face] = packet.readUShort().toShort()
			if (hasFaceTypes) {
				this.faceRenderType!![face] = packet2.readByte()
			}
			if (modelPriority == 255) {
				this.facesLayer!![face] = packet3.readByte()
			}
			if (hasFaceAlpha == 1) {
				this.facesAlpha!![face] = packet4.readByte()
			}
			if (hasFaceGroups == 1) {
				this.faceGroups!![face] = packet5.readUByte()
			}
			if (hasFaceTextures == 1) {
				this.facesMaterial!![face] = (packet6.readUShort() - 1).toShort()
			}
			if (this.facesTexture != null) {
				if (this.facesMaterial!![face].toInt() == -1) {
					this.facesTexture!![face] = -1
				} else {
					this.facesTexture!![face] = (packet7.readUByte() - 1).toByte()
				}
			}
			face++
		}
		packet.currentPosition = faceIndicesDataOffset
		this.maxDepth = -1
		packet2.currentPosition = faceIndexTypesOffset
		@Pc(838) var vertexA: Short = 0
		@Pc(840) var vertexB: Short = 0
		@Pc(842) var vertexC: Short = 0
		@Pc(844) var lastVertex: Short = 0
		@Pc(852) var local852: Int
		@Pc(846) var j: Int = 0
		while (j < this.numFaces) {
			val faceIndexType: Int = packet2.readUByte()
			if (faceIndexType == 1) {
				vertexA = (packet.readSmart() + lastVertex).toShort()
				vertexB = (packet.readSmart() + vertexA).toShort()
				vertexC = (vertexB + packet.readSmart()).toShort()
				lastVertex = vertexC
				this.facesA!![j] = vertexA
				this.facesB!![j] = vertexB
				this.facesC!![j] = vertexC
				if (this.maxDepth < vertexA) {
					this.maxDepth = vertexA.toInt()
				}
				if (this.maxDepth < vertexB) {
					this.maxDepth = vertexB.toInt()
				}
				if (vertexC > this.maxDepth) {
					this.maxDepth = vertexC.toInt()
				}
			}
			if (faceIndexType == 2) {
				vertexB = vertexC
				vertexC = (packet.readSmart() + lastVertex).toShort()
				this.facesA!![j] = vertexA
				lastVertex = vertexC
				this.facesB!![j] = vertexB
				this.facesC!![j] = vertexC
				if (this.maxDepth < vertexC) {
					this.maxDepth = vertexC.toInt()
				}
			}
			if (faceIndexType == 3) {
				vertexA = vertexC
				vertexC = (packet.readSmart() + lastVertex).toShort()
				lastVertex = vertexC
				this.facesA!![j] = vertexA
				this.facesB!![j] = vertexB
				this.facesC!![j] = vertexC
				if (this.maxDepth < vertexC) {
					this.maxDepth = vertexC.toInt()
				}
			}
			if (faceIndexType == 4) {
				@Pc(1003) val temp: Short = vertexA
				vertexA = vertexB
				vertexC = (lastVertex + packet.readSmart()).toShort()
				vertexB = temp
				this.facesA!![j] = vertexA
				lastVertex = vertexC
				this.facesB!![j] = temp
				this.facesC!![j] = vertexC
				if (this.maxDepth < vertexC) {
					this.maxDepth = vertexC.toInt()
				}
			}
			j++
		}
		packet.currentPosition = simpleTextureOffset
		this.maxDepth++
		packet2.currentPosition = complexTextureVertOffset
		packet3.currentPosition = complexTextureScaleOffset
		packet4.currentPosition = complexTextureRotOffset
		packet5.currentPosition = complexTextureDirOffset
		packet6.currentPosition = complexTextureSpeedOffset
		@Pc(1082) var local1082: Int
		var texture: Int = 0
		while (texture < this.numTextures) {
			val mapping: Int = this.texturesMapping!![texture].toInt() and 0xFF
			if (mapping == 0) {
				this.textureFaceX!![texture] = packet.readUShort().toShort()
				this.textureFaceY!![texture] = packet.readUShort().toShort()
				this.textureFaceZ!![texture] = packet.readUShort().toShort()
			}
			if (mapping == 1) {
				this.textureFaceX!![texture] = packet2.readUShort().toShort()
				this.textureFaceY!![texture] = packet2.readUShort().toShort()
				this.textureFaceZ!![texture] = packet2.readUShort().toShort()
				if (this.version >= 15) {
					this.texturesScaleX!![texture] = packet3.readUMedium()
					this.texturesScaleY!![texture] = packet3.readUMedium()
					this.texturesScaleZ!![texture] = packet3.readUMedium()
				} else {
					this.texturesScaleX!![texture] = packet3.readUShort()
					if (this.version >= 14) {
						this.texturesScaleY!![texture] = packet3.readUMedium()
					} else {
						this.texturesScaleY!![texture] = packet3.readUShort()
					}
					this.texturesScaleZ!![texture] = packet3.readUShort()
				}
				this.texturesRotation!![texture] = packet4.readByte()
				this.texturesDirection!![texture] = packet5.readByte()
				this.texturesSpeed!![texture] = packet6.readByte().toInt()
			}
			if (mapping == 2) {
				this.textureFaceX!![texture] = packet2.readUShort().toShort()
				this.textureFaceY!![texture] = packet2.readUShort().toShort()
				this.textureFaceZ!![texture] = packet2.readUShort().toShort()
				if (this.version >= 15) {
					this.texturesScaleX!![texture] = packet3.readUMedium()
					this.texturesScaleY!![texture] = packet3.readUMedium()
					this.texturesScaleZ!![texture] = packet3.readUMedium()
				} else {
					this.texturesScaleX!![texture] = packet3.readUShort()
					if (this.version < 14) {
						this.texturesScaleY!![texture] = packet3.readUShort()
					} else {
						this.texturesScaleY!![texture] = packet3.readUMedium()
					}
					this.texturesScaleZ!![texture] = packet3.readUShort()
				}
				this.texturesRotation!![texture] = packet4.readByte()
				this.texturesDirection!![texture] = packet5.readByte()
				this.texturesSpeed!![texture] = packet6.readByte().toInt()
				this.texturesUtrans!![texture] = packet6.readByte().toInt()
				this.texturesVtrans!![texture] = packet6.readByte().toInt()
			}
			if (mapping == 3) {
				this.textureFaceX!![texture] = packet2.readUShort().toShort()
				this.textureFaceY!![texture] = packet2.readUShort().toShort()
				this.textureFaceZ!![texture] = packet2.readUShort().toShort()
				if (this.version < 15) {
					this.texturesScaleX!![texture] = packet3.readUShort()
					if (this.version < 14) {
						this.texturesScaleY!![texture] = packet3.readUShort()
					} else {
						this.texturesScaleY!![texture] = packet3.readUMedium()
					}
					this.texturesScaleZ!![texture] = packet3.readUShort()
				} else {
					this.texturesScaleX!![texture] = packet3.readUMedium()
					this.texturesScaleY!![texture] = packet3.readUMedium()
					this.texturesScaleZ!![texture] = packet3.readUMedium()
				}
				this.texturesRotation!![texture] = packet4.readByte()
				this.texturesDirection!![texture] = packet5.readByte()
				this.texturesSpeed!![texture] = packet6.readByte().toInt()
			}
			texture++
		}
		packet.currentPosition = dataOffset
		@Pc(1486) var local1486: Int
		@Pc(1492) var local1492: Int
		@Pc(1496) var local1496: Int
		@Pc(1561) var local1561: Int
		if (hasParticleEffects) {
			val emitterCount: Int = packet.readUByte()
			if (emitterCount > 0) {
				this.particleEmitters = arrayOfNulls<EmissiveTriangle>(emitterCount)
				var emitterIndex: Int = 0
				while (emitterIndex < emitterCount) {
					val producerId: Int = packet.readUShort()
					val emitterFace: Int = packet.readUShort()
					@Pc(1504) val priority: Byte
					if (modelPriority == 255) {
						priority = this.facesLayer!![emitterFace]
					} else {
						priority = modelPriority.toByte()
					}
					this.particleEmitters!![emitterIndex] = EmissiveTriangle(producerId, this.facesA!![emitterFace].toInt(), this.facesB!![emitterFace].toInt(), this.facesC!![emitterFace].toInt(), priority)
					emitterIndex++
				}
			}
			val effectorCount: Int = packet.readUByte()
			if (effectorCount > 0) {
				this.particleEffectors = arrayOfNulls<EffectiveVertex>(effectorCount)
				var effectorIndex: Int = 0
				while (effectorIndex < effectorCount) {
					val effectId: Int = packet.readUShort()
					val appliesOnVertex: Int = packet.readUShort()
					this.particleEffectors!![effectorIndex] = EffectiveVertex(effectId, appliesOnVertex)
					effectorIndex++
				}
			}
		}
		if (!hasBillboards) {
			return
		}
		val billboardCount: Int = packet.readUByte()
		if (billboardCount <= 0) {
			return
		}
		this.billboards = arrayOfNulls<MeshBillboard>(billboardCount)
		var billboardIndex: Int = 0
		while (billboardIndex < billboardCount) {
			val billboardId: Int = packet.readUShort()
			val appliesOnFace: Int = packet.readUShort()
			val depth: Int = packet.readUByte()
			@Pc(1609) val distance: Byte = packet.readByte()
			this.billboards!![billboardIndex] = MeshBillboard(billboardId, appliesOnFace, depth, distance.toInt())
			billboardIndex++
		}
		return
	}

	@OriginalMember(owner = "client!bh", name = "b", descriptor = "(IIII)V")
	fun rotateWithoutNormals(@OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) x: Int) {
		@Pc(10) var sin: Int
		@Pc(14) var cos: Int
		@Pc(16) var vertex: Int
		@Pc(34) var rotated: Int
		if (z != 0) {
			sin = TextureCombineSource.SINE[z]
			cos = TextureCombineSource.COSINE[z]
			vertex = 0
			while (vertex < this.numVertices) {
				rotated = (cos * this.verticesX!![vertex] + this.verticesY!![vertex] * sin) shr 14
				this.verticesY!![vertex] = (this.verticesY!![vertex] * cos - this.verticesX!![vertex] * sin) shr 14
				this.verticesX!![vertex] = rotated
				vertex++
			}
		}
		if (x != 0) {
			sin = TextureCombineSource.SINE[x]
			cos = TextureCombineSource.COSINE[x]
			vertex = 0
			while (vertex < this.numVertices) {
				rotated = (this.verticesY!![vertex] * cos - sin * this.verticesZ!![vertex]) shr 14
				this.verticesZ!![vertex] = (this.verticesY!![vertex] * sin + cos * this.verticesZ!![vertex]) shr 14
				this.verticesY!![vertex] = rotated
				vertex++
			}
		}
		if (y == 0) {
			return
		}
		sin = TextureCombineSource.SINE[y]
		cos = TextureCombineSource.COSINE[y]
		vertex = 0
		while (vertex < this.numVertices) {
			rotated = (cos * this.verticesX!![vertex] + this.verticesZ!![vertex] * sin) shr 14
			this.verticesZ!![vertex] = (cos * this.verticesZ!![vertex] - this.verticesX!![vertex] * sin) shr 14
			this.verticesX!![vertex] = rotated
			vertex++
		}
	}

	companion object {
		@OriginalMember(owner = "client!wia", name = "a", descriptor = "(ILclient!in;II)Lclient!bh;")
		@JvmStatic
		fun fromResource(@OriginalArg(1) resource: Js5ResourceProvider?, @OriginalArg(2) id: Int): Mesh? {
			@Pc(14) val data: ByteArray? = resource!!.getFile(id, 0)
			return if (data == null) null else Mesh(data)
		}
	}
}
