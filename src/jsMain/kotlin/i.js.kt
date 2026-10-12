/*
 * Not part of the original client: the browser side of a native class of the native software
 * toolkit (milestone 4). Until the port of void-client's Kotlin rasteriser takes its place it throws
 * NativeUnavailableException, and the client keeps to its Java toolkits.
 */
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.entity.BoundingBoxPolygon
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.model.billboard.BillboardType
import com.jagex.game.runetek5.graphics.model.billboard.BillboardTypeList
import com.jagex.game.runetek5.graphics.model.normal.MeshBillboard
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.statics.NativeModelScratch

actual class i : Model, SoftwareObject {
	actual var nativeid: Long = 0L
	actual var aClass227Array2: Array<EmissiveTriangle?>? = null
	actual var aClass87Array2: Array<EffectiveVertex?>? = null
	actual constructor(arg0: oa?, arg1: ya?, mesh: Mesh?, arg3: Int, arg4: Int, arg5: Int, arg6: Int) : super()
	actual constructor(arg0: oa?) : super()
	actual override fun C(arg0: Int) {
		throw NativeUnavailableException("C")
	}
	actual override fun k(arg0: Int) {
		throw NativeUnavailableException("k")
	}
	actual override fun w(arg0: Boolean) {
		throw NativeUnavailableException("w")
	}
	actual override fun O(arg0: Int, arg1: Int, arg2: Int) {
		throw NativeUnavailableException("O")
	}
	actual override fun s(arg0: Int) {
		throw NativeUnavailableException("s")
	}
	actual override fun LA(arg0: Int) {
		throw NativeUnavailableException("LA")
	}
	actual override fun fa(): Int {
		throw NativeUnavailableException("fa")
	}
	actual override fun NA(): Boolean {
		throw NativeUnavailableException("NA")
	}
	actual override fun F(): Boolean {
		throw NativeUnavailableException("F")
	}
	actual fun ZA(arg0: i?, arg1: i?, arg2: Int, arg3: Boolean, arg4: Boolean) {
		throw NativeUnavailableException("ZA")
	}
	actual override fun shareLight(model: Model?, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean) {
		throw NativeUnavailableException("shareLight")
	}
	actual override fun aa(arg0: Short, arg1: Short) {
		throw NativeUnavailableException("aa")
	}
	actual override fun pickedOrtho(arg0: Int, arg1: Int, matrix: Matrix?, arg3: Boolean, arg4: Int, arg5: Int): Boolean {
		throw NativeUnavailableException("pickedOrtho")
	}
	actual override fun EA(): Int {
		throw NativeUnavailableException("EA")
	}
	actual override fun getEmissiveTriangles(): Array<EmissiveTriangle?>? {
		throw NativeUnavailableException("getEmissiveTriangles")
	}
	actual override fun wa() {
		throw NativeUnavailableException("wa")
	}
	actual override fun P(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("P")
	}
	actual override fun renderOrtho(matrix: Matrix?, pickingCylinder: BoundingBoxPolygon?, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("renderOrtho")
	}
	actual override fun v() {
		throw NativeUnavailableException("v")
	}
	actual override fun loadedTextures(): Boolean {
		throw NativeUnavailableException("loadedTextures")
	}
	actual override fun unlock() {
		throw NativeUnavailableException("unlock")
	}
	actual override fun VA(arg0: Int) {
		throw NativeUnavailableException("VA")
	}
	actual override fun picked(arg0: Int, arg1: Int, matrix: Matrix?, arg3: Boolean, arg4: Int): Boolean {
		throw NativeUnavailableException("picked")
	}
	actual override fun copy(arg0: Byte, arg1: Int, arg2: Boolean): Model? {
		throw NativeUnavailableException("copy")
	}
	actual override fun na(): Int {
		throw NativeUnavailableException("na")
	}
	actual override fun HA(): Int {
		throw NativeUnavailableException("HA")
	}
	actual override fun uploadToGpu() {
		throw NativeUnavailableException("uploadToGpu")
	}
	actual override fun p(arg0: Int, arg1: Int, arg2: Ground?, arg3: Ground?, arg4: Int, arg5: Int, arg6: Int) {
		throw NativeUnavailableException("p")
	}
	actual override fun V(): Int {
		throw NativeUnavailableException("V")
	}
	actual override fun da(): Int {
		throw NativeUnavailableException("da")
	}
	actual override fun applyTransformUnmasked(arg0: Int, arg1: IntArray?, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Boolean) {
		throw NativeUnavailableException("applyTransformUnmasked")
	}
	actual override fun G(): Int {
		throw NativeUnavailableException("G")
	}
	actual override fun ma(): Int {
		throw NativeUnavailableException("ma")
	}
	actual override fun FA(arg0: Int) {
		throw NativeUnavailableException("FA")
	}
	actual override fun adjustColours(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("adjustColours")
	}
	actual override fun ia(arg0: Short, arg1: Short) {
		throw NativeUnavailableException("ia")
	}
	actual fun finalize() {
		throw NativeUnavailableException("finalize")
	}
	actual override fun WA(): Int {
		throw NativeUnavailableException("WA")
	}
	actual override fun transform(matrix: Matrix?, arg1: Int, arg2: Boolean) {
		throw NativeUnavailableException("transform")
	}
	actual override fun lock() {
		throw NativeUnavailableException("lock")
	}
	actual override fun render(matrix: Matrix?, pickingCylinder: BoundingBoxPolygon?, arg2: Int) {
		throw NativeUnavailableException("render")
	}
	actual override fun r(): Boolean {
		throw NativeUnavailableException("r")
	}
	actual override fun updateParticlePosition(matrix: Matrix?) {
		throw NativeUnavailableException("updateParticlePosition")
	}
	actual override fun a(arg0: Int) {
		throw NativeUnavailableException("a")
	}
	actual override fun getEffectiveVertices(): Array<EffectiveVertex?>? {
		throw NativeUnavailableException("getEffectiveVertices")
	}
	actual override fun ua(): Int {
		throw NativeUnavailableException("ua")
	}
	actual override fun H(arg0: Int, arg1: Int, arg2: Int) {
		throw NativeUnavailableException("H")
	}
	actual override fun ba(arg0: Shadow?): Shadow? {
		throw NativeUnavailableException("ba")
	}
	actual override fun RA(): Int {
		throw NativeUnavailableException("RA")
	}
	actual override fun I(arg0: Int, arg1: IntArray?, arg2: Int, arg3: Int, arg4: Int, arg5: Boolean, arg6: Int, arg7: IntArray?) {
		throw NativeUnavailableException("I")
	}
}
