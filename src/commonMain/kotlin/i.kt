/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/i.kt), so that common code can use it (milestone 3).
 */
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
import com.jagex.game.runetek5.graphics.model.billboard.BillboardTypeList

expect class i : Model, SoftwareObject {
	var nativeid: Long
	var aClass227Array2: Array<EmissiveTriangle?>?
	var aClass87Array2: Array<EffectiveVertex?>?
	constructor(arg0: oa?, arg1: ya?, mesh: Mesh?, arg3: Int, arg4: Int, arg5: Int, arg6: Int)
	constructor(arg0: oa?)
	override fun C(arg0: Int)
	override fun k(arg0: Int)
	override fun w(arg0: Boolean)
	override fun O(arg0: Int, arg1: Int, arg2: Int)
	override fun s(arg0: Int)
	override fun LA(arg0: Int)
	override fun fa(): Int
	override fun NA(): Boolean
	override fun F(): Boolean
	fun ZA(arg0: i?, arg1: i?, arg2: Int, arg3: Boolean, arg4: Boolean)
	override fun shareLight(model: Model?, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean)
	override fun aa(arg0: Short, arg1: Short)
	override fun pickedOrtho(arg0: Int, arg1: Int, matrix: Matrix?, arg3: Boolean, arg4: Int, arg5: Int): Boolean
	override fun EA(): Int
	override fun getEmissiveTriangles(): Array<EmissiveTriangle?>?
	override fun wa()
	override fun P(arg0: Int, arg1: Int, arg2: Int, arg3: Int)
	override fun renderOrtho(matrix: Matrix?, pickingCylinder: BoundingBoxPolygon?, arg2: Int, arg3: Int)
	override fun v()
	override fun loadedTextures(): Boolean
	override fun unlock()
	override fun VA(arg0: Int)
	override fun picked(arg0: Int, arg1: Int, matrix: Matrix?, arg3: Boolean, arg4: Int): Boolean
	override fun copy(arg0: Byte, arg1: Int, arg2: Boolean): Model?
	override fun na(): Int
	override fun HA(): Int
	override fun uploadToGpu()
	override fun p(arg0: Int, arg1: Int, arg2: Ground?, arg3: Ground?, arg4: Int, arg5: Int, arg6: Int)
	override fun V(): Int
	override fun da(): Int
	override fun applyTransformUnmasked(arg0: Int, arg1: IntArray?, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Boolean)
	override fun G(): Int
	override fun ma(): Int
	override fun FA(arg0: Int)
	override fun adjustColours(arg0: Int, arg1: Int, arg2: Int, arg3: Int)
	override fun ia(arg0: Short, arg1: Short)
	fun finalize()
	override fun WA(): Int
	override fun transform(matrix: Matrix?, arg1: Int, arg2: Boolean)
	override fun lock()
	override fun render(matrix: Matrix?, pickingCylinder: BoundingBoxPolygon?, arg2: Int)
	override fun r(): Boolean
	override fun updateParticlePosition(matrix: Matrix?)
	override fun a(arg0: Int)
	override fun getEffectiveVertices(): Array<EffectiveVertex?>?
	override fun ua(): Int
	override fun H(arg0: Int, arg1: Int, arg2: Int)
	override fun ba(arg0: Shadow?): Shadow?
	override fun RA(): Int
	override fun I(arg0: Int, arg1: IntArray?, arg2: Int, arg3: Int, arg4: Int, arg5: Boolean, arg6: Int, arg7: IntArray?)
}
