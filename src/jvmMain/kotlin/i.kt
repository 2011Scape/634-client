import com.jagex.game.runetek5.graphics.model.billboard.BillboardType
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.normal.MeshBillboard
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.entity.BoundingBoxPolygon
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import com.jagex.game.runetek5.statics.NativeModelScratch
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.model.billboard.BillboardTypeList

@OriginalClass("client!i")
class i : Model, SoftwareObject {

	@OriginalMember(owner = "client!i", name = "nativeid", descriptor = "J")
	@JvmField
	var nativeid: Long = 0L

	@OriginalMember(owner = "client!i", name = "o", descriptor = "Lclient!oa;")
	private val anOa2: oa?

	@OriginalMember(owner = "client!i", name = "r", descriptor = "Lclient!ya;")
	private val aYa1: ya?

	@OriginalMember(owner = "client!i", name = "q", descriptor = "[Lclient!nn;")
	@JvmField
	var aClass227Array2: Array<EmissiveTriangle?>? = null

	@OriginalMember(owner = "client!i", name = "p", descriptor = "[Lclient!eca;")
	@JvmField
	var aClass87Array2: Array<EffectiveVertex?>? = null

	@OriginalMember(owner = "client!i", name = "<init>", descriptor = "(Lclient!oa;Lclient!ya;Lclient!bh;IIII)V")
	constructor(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: ya?, @OriginalArg(2) mesh: Mesh?, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int) {
		this.anOa2 = arg0
		this.aYa1 = arg1
		this.aClass227Array2 = mesh!!.particleEmitters
		this.aClass87Array2 = mesh.particleEffectors
		@Pc(24) val local24: Int = if (mesh.particleEmitters == null) 0 else mesh.particleEmitters!!.size
		@Pc(33) val local33: Int = if (mesh.particleEffectors == null) 0 else mesh.particleEffectors!!.size
		@Pc(35) var local35: Int = 0
		@Pc(42) val local42: IntArray = IntArray(local24 * 3 + local33)
		@Pc(44) var i: Int = 0
		while (i < local24) {
			local42[local35++] = this.aClass227Array2!![i]!!.faceA
			local42[local35++] = this.aClass227Array2!![i]!!.faceB
			local42[local35++] = this.aClass227Array2!![i]!!.vertexC
			i++
		}
		@Pc(78) var j: Int = 0
		while (j < local33) {
			local42[local35++] = this.aClass87Array2!![j]!!.appliesOnVertex
			j++
		}
		@Pc(101) val local101: Int = if (mesh.billboards == null) 0 else mesh.billboards!!.size
		@Pc(106) val local106: IntArray = IntArray(local101 * 8)
		@Pc(108) var local108: Int = 0
		@Pc(110) var k: Int = 0
		while (k < local101) {
			@Pc(116) val meshBillboard: MeshBillboard = mesh.billboards!![k]!!
			@Pc(121) val lightType: BillboardType = BillboardTypeList.list(meshBillboard.billboardId)
			local106[local108++] = meshBillboard.appliesOnFace
			local106[local108++] = lightType.size2d
			local106[local108++] = lightType.size3d
			local106[local108++] = lightType.materialId
			local106[local108++] = lightType.blendType
			local106[local108++] = lightType.shape
			local106[local108++] = if (lightType.hasUid) -1 else 0
			k++
		}
		@Pc(173) var m: Int = 0
		while (m < local101) {
			@Pc(179) val meshBillboard2: MeshBillboard = mesh.billboards!![m]!!
			local106[local108++] = meshBillboard2.depth
			m++
		}
		this.R(this.anOa2, this.aYa1, mesh.numVertices, mesh.maxDepth, mesh.verticesX, mesh.verticesY, mesh.verticesZ, mesh.vertexGroups, mesh.verticesSubmeshes, mesh.numFaces, mesh.facesA, mesh.facesB, mesh.facesC, mesh.faceRenderType, mesh.facesLayer, mesh.facesAlpha, mesh.facesTexture, mesh.facesColor, mesh.facesMaterial, mesh.faceGroups, mesh.priority, mesh.facesSubmeshes, mesh.numTextures, mesh.texturesMapping, mesh.textureFaceX, mesh.textureFaceY, mesh.textureFaceZ, mesh.texturesScaleX, mesh.texturesScaleY, mesh.texturesScaleZ, mesh.texturesRotation, mesh.texturesDirection, mesh.texturesSpeed, mesh.texturesUtrans, mesh.texturesVtrans, local42, local24, local33, arg3, arg4, arg5, arg6, local106)
	}

	@OriginalMember(owner = "client!i", name = "<init>", descriptor = "(Lclient!oa;)V")
	constructor(@OriginalArg(0) arg0: oa?) {
		this.anOa2 = arg0
		this.aYa1 = null
		this.oa(arg0)
	}

	@OriginalMember(owner = "client!i", name = "C", descriptor = "(I)V")
	external override fun C(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!i", name = "k", descriptor = "(I)V")
	external override fun k(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!i", name = "w", descriptor = "(Z)V")
	external override fun w(@OriginalArg(0) arg0: Boolean)

	@OriginalMember(owner = "client!i", name = "O", descriptor = "(III)V")
	external override fun O(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

	@OriginalMember(owner = "client!i", name = "s", descriptor = "(I)V")
	external override fun s(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!i", name = "LA", descriptor = "(I)V")
	external override fun LA(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!i", name = "fa", descriptor = "()I")
	external override fun fa(): Int

	@OriginalMember(owner = "client!i", name = "NA", descriptor = "()Z")
	external override fun NA(): Boolean

	@OriginalMember(owner = "client!i", name = "F", descriptor = "()Z")
	external override fun F(): Boolean

	@OriginalMember(owner = "client!i", name = "ZA", descriptor = "(Lclient!i;Lclient!i;IZZ)V")
	external fun ZA(@OriginalArg(0) arg0: i?, @OriginalArg(1) arg1: i?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Boolean, @OriginalArg(4) arg4: Boolean)

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(Lclient!ka;IIIZ)V")
	override fun shareLight(@OriginalArg(0) model: Model?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Boolean) {
		this.anOa2!!.method6405()!!.method3(this, model, arg1, arg2, arg3, arg4)
	}

	@OriginalMember(owner = "client!i", name = "aa", descriptor = "(SS)V")
	external override fun aa(@OriginalArg(0) arg0: Short, @OriginalArg(1) arg1: Short)

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(IILclient!bca;ZII)Z")
	override fun pickedOrtho(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) matrix: Matrix?, @OriginalArg(3) arg3: Boolean, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int): Boolean {
		return this.anOa2!!.method6405()!!.method2(this, arg0, arg1, matrix, arg3, arg5)
	}

	@OriginalMember(owner = "client!i", name = "EA", descriptor = "()I")
	external override fun EA(): Int

	@OriginalMember(owner = "client!i", name = "d", descriptor = "()[Lclient!nn;")
	override fun getEmissiveTriangles(): Array<EmissiveTriangle?>? {
		return this.aClass227Array2
	}

	@OriginalMember(owner = "client!i", name = "wa", descriptor = "()V")
	external override fun wa()

	@OriginalMember(owner = "client!i", name = "P", descriptor = "(IIII)V")
	external override fun P(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(Lclient!bca;Lclient!iw;II)V")
	override fun renderOrtho(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) pickingCylinder: BoundingBoxPolygon?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		if (pickingCylinder == null) {
			this.anOa2!!.method6405()!!.method8(this, matrix, null, arg2, arg3)
			return
		}
		NativeModelScratch.pickingResult[5] = 0
		this.anOa2!!.method6405()!!.method8(this, matrix, NativeModelScratch.pickingResult, arg2, arg3)
		pickingCylinder.left = NativeModelScratch.pickingResult[0]
		pickingCylinder.bottom = NativeModelScratch.pickingResult[1]
		pickingCylinder.right = NativeModelScratch.pickingResult[2]
		pickingCylinder.top = NativeModelScratch.pickingResult[3]
		pickingCylinder.size = NativeModelScratch.pickingResult[4]
		pickingCylinder.positionCalculated = NativeModelScratch.pickingResult[5] != 0
	}

	@OriginalMember(owner = "client!i", name = "v", descriptor = "()V")
	external override fun v()

	@OriginalMember(owner = "client!i", name = "l", descriptor = "(JI[IIIIIZ)V")
	private external fun l(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Boolean)

	@OriginalMember(owner = "client!i", name = "c", descriptor = "()Z")
	override fun loadedTextures(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!i", name = "e", descriptor = "()V")
	override fun unlock() {
		if (this.anOa2!!.anInt7473 > 1) {
			synchronized(this) {
				super.isLocked = false
				(this as java.lang.Object).notifyAll()
			}
		}
	}

	@OriginalMember(owner = "client!i", name = "VA", descriptor = "(I)V")
	external override fun VA(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(IILclient!bca;ZI)Z")
	override fun picked(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) matrix: Matrix?, @OriginalArg(3) arg3: Boolean, @OriginalArg(4) arg4: Int): Boolean {
		return this.anOa2!!.method6405()!!.method5(this, arg0, arg1, matrix, arg3)
	}

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(BIZ)Lclient!ka;")
	override fun copy(@OriginalArg(0) arg0: Byte, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Boolean): Model? {
		return this.anOa2!!.method6405()!!.method11(this, arg0, arg1, arg2)
	}

	@OriginalMember(owner = "client!i", name = "na", descriptor = "()I")
	external override fun na(): Int

	@OriginalMember(owner = "client!i", name = "HA", descriptor = "()I")
	external override fun HA(): Int

	@OriginalMember(owner = "client!i", name = "b", descriptor = "()V")
	override fun uploadToGpu() {
	}

	@OriginalMember(owner = "client!i", name = "p", descriptor = "(IILclient!s;Lclient!s;III)V")
	external override fun p(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Ground?, @OriginalArg(3) arg3: Ground?, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int)

	@OriginalMember(owner = "client!i", name = "V", descriptor = "()I")
	external override fun V(): Int

	@OriginalMember(owner = "client!i", name = "da", descriptor = "()I")
	external override fun da(): Int

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(I[IIIIIZ)V")
	override fun applyTransformUnmasked(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Boolean) {
		this.l(this.nativeid, arg0, arg1, arg2, arg3, arg4, arg5, arg6)
	}

	@OriginalMember(owner = "client!i", name = "G", descriptor = "()I")
	external override fun G(): Int

	@OriginalMember(owner = "client!i", name = "ma", descriptor = "()I")
	external override fun ma(): Int

	@OriginalMember(owner = "client!i", name = "R", descriptor = "(Lclient!oa;Lclient!ya;II[I[I[I[I[SI[S[S[S[B[B[B[B[S[S[IB[SI[B[S[S[S[I[I[I[B[B[I[I[I[IIIIIII[I)V")
	private external fun R(@OriginalArg(0) arg0: oa?, @OriginalArg(1) arg1: ya?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IntArray?, @OriginalArg(5) arg5: IntArray?, @OriginalArg(6) arg6: IntArray?, @OriginalArg(7) arg7: IntArray?, @OriginalArg(8) arg8: ShortArray?, @OriginalArg(9) arg9: Int, @OriginalArg(10) arg10: ShortArray?, @OriginalArg(11) arg11: ShortArray?, @OriginalArg(12) arg12: ShortArray?, @OriginalArg(13) arg13: ByteArray?, @OriginalArg(14) arg14: ByteArray?, @OriginalArg(15) arg15: ByteArray?, @OriginalArg(16) arg16: ByteArray?, @OriginalArg(17) arg17: ShortArray?, @OriginalArg(18) arg18: ShortArray?, @OriginalArg(19) arg19: IntArray?, @OriginalArg(20) arg20: Byte, @OriginalArg(21) arg21: ShortArray?, @OriginalArg(22) arg22: Int, @OriginalArg(23) arg23: ByteArray?, @OriginalArg(24) arg24: ShortArray?, @OriginalArg(25) arg25: ShortArray?, @OriginalArg(26) arg26: ShortArray?, @OriginalArg(27) arg27: IntArray?, @OriginalArg(28) arg28: IntArray?, @OriginalArg(29) arg29: IntArray?, @OriginalArg(30) arg30: ByteArray?, @OriginalArg(31) arg31: ByteArray?, @OriginalArg(32) arg32: IntArray?, @OriginalArg(33) arg33: IntArray?, @OriginalArg(34) arg34: IntArray?, @OriginalArg(35) arg35: IntArray?, @OriginalArg(36) arg36: Int, @OriginalArg(37) arg37: Int, @OriginalArg(38) arg38: Int, @OriginalArg(39) arg39: Int, @OriginalArg(40) arg40: Int, @OriginalArg(41) arg41: Int, @OriginalArg(42) arg42: IntArray?)

	@OriginalMember(owner = "client!i", name = "FA", descriptor = "(I)V")
	external override fun FA(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(IIII)V")
	override fun adjustColours(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
	}

	@OriginalMember(owner = "client!i", name = "ia", descriptor = "(SS)V")
	external override fun ia(@OriginalArg(0) arg0: Short, @OriginalArg(1) arg1: Short)

	@OriginalMember(owner = "client!i", name = "finalize", descriptor = "()V")
	fun finalize() {
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}

	@OriginalMember(owner = "client!i", name = "WA", descriptor = "()I")
	external override fun WA(): Int

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(Lclient!bca;IZ)V")
	override fun transform(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Boolean) {
		this.A((matrix as ja).nativeid, arg1, arg2)
	}

	@OriginalMember(owner = "client!i", name = "f", descriptor = "()V")
	override fun lock() {
		if (this.anOa2!!.anInt7473 <= 1) {
			return
		}
		synchronized(this) {
			while (super.isLocked) {
				try {
					(this as java.lang.Object).wait()
				} catch (@Pc(13) ex: InterruptedException) {
				}
			}
			super.isLocked = true
		}
	}

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(Lclient!bca;Lclient!iw;I)V")
	override fun render(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) pickingCylinder: BoundingBoxPolygon?, @OriginalArg(2) arg2: Int) {
		if (pickingCylinder == null) {
			this.anOa2!!.method6405()!!.method4(this, matrix, null, arg2)
			return
		}
		NativeModelScratch.pickingResult[5] = 0
		this.anOa2!!.method6405()!!.method4(this, matrix, NativeModelScratch.pickingResult, arg2)
		pickingCylinder.left = NativeModelScratch.pickingResult[0]
		pickingCylinder.bottom = NativeModelScratch.pickingResult[1]
		pickingCylinder.right = NativeModelScratch.pickingResult[2]
		pickingCylinder.top = NativeModelScratch.pickingResult[3]
		pickingCylinder.size = NativeModelScratch.pickingResult[4]
		pickingCylinder.positionCalculated = NativeModelScratch.pickingResult[5] != 0
	}

	@OriginalMember(owner = "client!i", name = "r", descriptor = "()Z")
	external override fun r(): Boolean

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(Lclient!bca;)V")
	override fun updateParticlePosition(@OriginalArg(0) matrix: Matrix?) {
		this.method4358(NativeModelScratch.effectVertices, matrix)
		@Pc(5) var local5: Int = 0
		@Pc(10) var local10: Int
		if (this.aClass227Array2 != null) {
			local10 = 0
			while (local10 < this.aClass227Array2!!.size) {
				@Pc(16) val modelParticleEmitter: EmissiveTriangle = this.aClass227Array2!![local10]!!
				modelParticleEmitter.verticeZC = NativeModelScratch.effectVertices[local5++]
				modelParticleEmitter.verticeXC = NativeModelScratch.effectVertices[local5++]
				modelParticleEmitter.verticeYA = NativeModelScratch.effectVertices[local5++]
				modelParticleEmitter.verticeZA = NativeModelScratch.effectVertices[local5++]
				modelParticleEmitter.verticeXA = NativeModelScratch.effectVertices[local5++]
				modelParticleEmitter.verticeZB = NativeModelScratch.effectVertices[local5++]
				modelParticleEmitter.verticeYB = NativeModelScratch.effectVertices[local5++]
				modelParticleEmitter.transformedCY = NativeModelScratch.effectVertices[local5++]
				modelParticleEmitter.verticeYC = NativeModelScratch.effectVertices[local5++]
				local10++
			}
		}
		if (this.aClass87Array2 == null) {
			return
		}
		local10 = 0
		while (local10 < this.aClass87Array2!!.size) {
			@Pc(87) val modelParticleEffector: EffectiveVertex = this.aClass87Array2!![local10]!!
			@Pc(89) var modelParticleEffector2: EffectiveVertex = modelParticleEffector
			if (modelParticleEffector.child != null) {
				modelParticleEffector2 = modelParticleEffector.child!!
			}
			if (modelParticleEffector.matrix == null) {
				modelParticleEffector.matrix = matrix!!.copy()
			} else {
				modelParticleEffector.matrix!!.apply(matrix)
			}
			modelParticleEffector2.verticeZ = NativeModelScratch.effectVertices[local5++]
			modelParticleEffector2.verticeX = NativeModelScratch.effectVertices[local5++]
			modelParticleEffector2.verticeY = NativeModelScratch.effectVertices[local5++]
			local10++
		}
	}

	@OriginalMember(owner = "client!i", name = "a", descriptor = "(I)V")
	external override fun a(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!i", name = "a", descriptor = "()[Lclient!eca;")
	override fun getEffectiveVertices(): Array<EffectiveVertex?>? {
		return this.aClass87Array2
	}

	@OriginalMember(owner = "client!i", name = "a", descriptor = "([ILclient!bca;)V")
	private fun method4358(@OriginalArg(0) arg0: IntArray?, @OriginalArg(1) matrix: Matrix?) {
		this.anOa2!!.method6405()!!.method10(this, arg0, matrix)
	}

	@OriginalMember(owner = "client!i", name = "ua", descriptor = "()I")
	external override fun ua(): Int

	@OriginalMember(owner = "client!i", name = "H", descriptor = "(III)V")
	external override fun H(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

	@OriginalMember(owner = "client!i", name = "A", descriptor = "(JIZ)V")
	private external fun A(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Boolean)

	@OriginalMember(owner = "client!i", name = "ba", descriptor = "(Lclient!r;)Lclient!r;")
	external override fun ba(@OriginalArg(0) arg0: Shadow?): Shadow?

	@OriginalMember(owner = "client!i", name = "oa", descriptor = "(Lclient!oa;)V")
	private external fun oa(@OriginalArg(0) arg0: oa?)

	@OriginalMember(owner = "client!i", name = "RA", descriptor = "()I")
	external override fun RA(): Int

	@OriginalMember(owner = "client!i", name = "I", descriptor = "(I[IIIIZI[I)V")
	external override fun I(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Boolean, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: IntArray?)
}

