/*
 * Not part of the original client: the browser side of a native class of the native software
 * toolkit (milestone 4). Until the port of void-client's Kotlin rasteriser takes its place it throws
 * NativeUnavailableException, and the client keeps to its Java toolkits.
 */
import awt.Canvas
import awt.Dimension
import awt.Graphics
import awt.Rectangle
import com.jagex.game.runetek5.DepthBuffer
import com.jagex.game.runetek5.EnvironmentSampler
import com.jagex.game.runetek5.NativeLibraryOwner
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.cache.node.JavaSurface
import com.jagex.game.runetek5.cache.node.MemoryPool
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.cache.node.SsePointLight
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.graphics.FlipException
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.OffscreenSurface
import com.jagex.game.runetek5.graphics.Renderer
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.model.particle.PointEntity
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sprite.Sprite
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.statics.NativeModelScratch
import com.jagex.game.runetek5.utils.ColorUtil
import lang.reflect.Method

actual class oa : Toolkit, SoftwareObject {
	actual var anInt7473: Int = 0
	actual var nativeid: Long = 0L
	actual constructor(canvas: Canvas?, textureSource: MaterialProvider?, arg2: Int, arg3: Int) : super(nativeUnavailable())
	actual override fun outlineRect(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) {
		throw NativeUnavailableException("outlineRect")
	}
	actual override fun createCubeTexture(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): EnvironmentSampler? {
		throw NativeUnavailableException("createCubeTexture")
	}
	actual override fun setHeap(memoryPool: MemoryPool?) {
		throw NativeUnavailableException("setHeap")
	}
	actual override fun w(arg0: Boolean) {
		throw NativeUnavailableException("w")
	}
	actual override fun A(arg0: Int, arg1: ComponentSurface?, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("A")
	}
	actual override fun aa(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) {
		throw NativeUnavailableException("aa")
	}
	actual override fun compositeBloomEffects() {
		throw NativeUnavailableException("compositeBloomEffects")
	}
	actual override fun flipDirtyRect(rectangles: Array<Rectangle?>?, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("flipDirtyRect")
	}
	actual override fun method7311(): Boolean {
		throw NativeUnavailableException("method7311")
	}
	actual override fun renderOrtho(particleList: ParticleList?, arg1: Int) {
		throw NativeUnavailableException("renderOrtho")
	}
	actual override fun drawTriangle() {
		throw NativeUnavailableException("drawTriangle")
	}
	actual override fun cacheReset() {
		throw NativeUnavailableException("cacheReset")
	}
	actual override fun generateSprite(indexedImage: SpriteType?, arg1: Boolean): NativeSprite? {
		throw NativeUnavailableException("generateSprite")
	}
	actual override fun HA(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: IntArray?) {
		throw NativeUnavailableException("HA")
	}
	actual override fun stopBloom() {
		throw NativeUnavailableException("stopBloom")
	}
	actual override fun setTextureSize(arg0: Int) {
		throw NativeUnavailableException("setTextureSize")
	}
	actual override fun H(arg0: Int, arg1: Int, arg2: Int, arg3: IntArray?) {
		throw NativeUnavailableException("H")
	}
	actual override fun b(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Double) {
		throw NativeUnavailableException("b")
	}
	actual override fun setBloomParams(arg0: Float, arg1: Float, arg2: Float) {
		throw NativeUnavailableException("setBloomParams")
	}
	actual override fun createFontRenderer(fontMetrics: FontMetrics?, indexedImages: Array<SpriteType?>?): AbstractFontRenderer? {
		throw NativeUnavailableException("createFontRenderer")
	}
	actual override fun hasTextures(): Boolean {
		throw NativeUnavailableException("hasTextures")
	}
	actual fun method6405(): a? {
		throw NativeUnavailableException("method6405")
	}
	actual override fun scratchMatrix(): Matrix? {
		throw NativeUnavailableException("scratchMatrix")
	}
	actual override fun linkThreads(arg0: Int) {
		throw NativeUnavailableException("linkThreads")
	}
	actual override fun accumulateViewportDimensions(arg0: IntArray?) {
		throw NativeUnavailableException("accumulateViewportDimensions")
	}
	actual override fun DA(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("DA")
	}
	actual override fun createHeap(arg0: Int): MemoryPool? {
		throw NativeUnavailableException("createHeap")
	}
	actual override fun initialize() {
		throw NativeUnavailableException("initialize")
	}
	actual override fun compareFunctionMasks(arg0: Int, arg1: Int): Int {
		throw NativeUnavailableException("compareFunctionMasks")
	}
	actual override fun xa(arg0: Float) {
		throw NativeUnavailableException("xa")
	}
	actual override fun releaseSurface(canvas: Canvas?) {
		throw NativeUnavailableException("releaseSurface")
	}
	actual override fun drawSurfaceLine(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, clippingMask: ComponentSurface?, arg6: Int, arg7: Int) {
		throw NativeUnavailableException("drawSurfaceLine")
	}
	actual override fun finalize() {
		throw NativeUnavailableException("finalize")
	}
	actual override fun i(): Int {
		throw NativeUnavailableException("i")
	}
	actual override fun render(particleList: ParticleList?) {
		throw NativeUnavailableException("render")
	}
	actual override fun getMaxLights(): Int {
		throw NativeUnavailableException("getMaxLights")
	}
	actual override fun JA(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): Int {
		throw NativeUnavailableException("JA")
	}
	actual override fun stop() {
		throw NativeUnavailableException("stop")
	}
	actual override fun resizeCanvas(canvas: Canvas?, arg1: Int, arg2: Int) {
		throw NativeUnavailableException("resizeCanvas")
	}
	actual override fun bloom(): Boolean {
		throw NativeUnavailableException("bloom")
	}
	actual override fun Q(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: ByteArray?, arg7: Int, arg8: Int) {
		throw NativeUnavailableException("Q")
	}
	actual override fun generateEmptySprite(arg0: Int, arg1: Int, arg2: Boolean): NativeSprite? {
		throw NativeUnavailableException("generateEmptySprite")
	}
	actual override fun ya() {
		throw NativeUnavailableException("ya")
	}
	actual override fun I(): Int {
		throw NativeUnavailableException("I")
	}
	actual override fun f(arg0: Int, arg1: Int) {
		throw NativeUnavailableException("f")
	}
	actual override fun KA(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("KA")
	}
	actual override fun tick(arg0: Int) {
		throw NativeUnavailableException("tick")
	}
	actual override fun combineFunctionMasks(arg0: Int, arg1: Int): Int {
		throw NativeUnavailableException("combineFunctionMasks")
	}
	actual override fun ra(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("ra")
	}
	actual override fun E(): Int {
		throw NativeUnavailableException("E")
	}
	actual override fun pa() {
		throw NativeUnavailableException("pa")
	}
	actual override fun strongLine(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) {
		throw NativeUnavailableException("strongLine")
	}
	actual override fun GA(arg0: Int) {
		throw NativeUnavailableException("GA")
	}
	actual override fun extendBuildArea(): Boolean {
		throw NativeUnavailableException("extendBuildArea")
	}
	actual override fun X(arg0: Int) {
		throw NativeUnavailableException("X")
	}
	actual override fun createModel(mesh: Mesh?, arg1: Int, arg2: Int, arg3: Int, arg4: Int): Model? {
		throw NativeUnavailableException("createModel")
	}
	actual override fun renderer(): Renderer? {
		throw NativeUnavailableException("renderer")
	}
	actual override fun drawLine(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int) {
		throw NativeUnavailableException("drawLine")
	}
	actual override fun F(arg0: Int, arg1: Int) {
		throw NativeUnavailableException("F")
	}
	actual override fun createNativeSprite(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean): NativeSprite? {
		throw NativeUnavailableException("createNativeSprite")
	}
	actual override fun allocateThreads(arg0: Int) {
		throw NativeUnavailableException("allocateThreads")
	}
	actual override fun r(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int): Int {
		throw NativeUnavailableException("r")
	}
	actual override fun addCanvas(canvas: Canvas?, arg1: Int, arg2: Int) {
		throw NativeUnavailableException("addCanvas")
	}
	actual override fun drawQuad(environmentSampler: EnvironmentSampler?, environmentSampler2: EnvironmentSampler?, arg2: Float, environmentSampler3: EnvironmentSampler?): EnvironmentSampler? {
		throw NativeUnavailableException("drawQuad")
	}
	actual override fun K(arg0: IntArray?) {
		throw NativeUnavailableException("K")
	}
	actual override fun replaceCanvasOnDisplayUpdate(): Boolean {
		throw NativeUnavailableException("replaceCanvasOnDisplayUpdate")
	}
	actual override fun flip(arg0: Int, arg1: Int) {
		throw NativeUnavailableException("flip")
	}
	actual override fun method7268(arg0: Int, arg1: Int): Sprite? {
		throw NativeUnavailableException("method7268")
	}
	actual override fun copyLights(arg0: Int, pointLights: Array<PointLight?>?) {
		throw NativeUnavailableException("copyLights")
	}
	actual override fun za(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) {
		throw NativeUnavailableException("za")
	}
	actual override fun na(arg0: Int, arg1: Int, arg2: Int, arg3: Int): IntArray? {
		throw NativeUnavailableException("na")
	}
	actual override fun method7250(arg0: Boolean) {
		throw NativeUnavailableException("method7250")
	}
	actual override fun createPointLight(arg0: Int, arg1: Int, arg2: Int, arg3: Int, range: Int, intensity: Float): PointLight? {
		throw NativeUnavailableException("createPointLight")
	}
	actual override fun setCanvas(canvas: Canvas?) {
		throw NativeUnavailableException("setCanvas")
	}
	actual override fun hardShadow(): Boolean {
		throw NativeUnavailableException("hardShadow")
	}
	actual override fun setSunCubeTexture(environmentSampler: EnvironmentSampler?) {
		throw NativeUnavailableException("setSunCubeTexture")
	}
	actual override fun createMatrix(): Matrix? {
		throw NativeUnavailableException("createMatrix")
	}
	actual override fun method7281(arg0: Int) {
		throw NativeUnavailableException("method7281")
	}
	actual override fun EA(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("EA")
	}
	actual override fun finish() {
		throw NativeUnavailableException("finish")
	}
	actual override fun method7261(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("method7261")
	}
	actual override fun P(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) {
		throw NativeUnavailableException("P")
	}
	actual override fun forceFixedScreenmode(): Boolean {
		throw NativeUnavailableException("forceFixedScreenmode")
	}
	actual override fun U(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) {
		throw NativeUnavailableException("U")
	}
	actual override fun Y(): IntArray? {
		throw NativeUnavailableException("Y")
	}
	actual override fun ZA(arg0: Int, arg1: Float, arg2: Float, arg3: Float, arg4: Float, arg5: Float) {
		throw NativeUnavailableException("ZA")
	}
	actual override fun createOffscreenSurface(surface: Sprite?, depthBuffer: DepthBuffer?): OffscreenSurface? {
		throw NativeUnavailableException("createOffscreenSurface")
	}
	actual override fun swapSurface(offscreenSurface: OffscreenSurface?) {
		throw NativeUnavailableException("swapSurface")
	}
	actual override fun da(arg0: Int, arg1: Int, arg2: Int, arg3: IntArray?) {
		throw NativeUnavailableException("da")
	}
	actual override fun createMask(arg0: Int, arg1: Int, arg2: IntArray?, arg3: IntArray?): ComponentSurface? {
		throw NativeUnavailableException("createMask")
	}
	actual override fun camera(): Matrix? {
		throw NativeUnavailableException("camera")
	}
	actual override fun line(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) {
		throw NativeUnavailableException("line")
	}
	actual override fun C(arg0: Boolean) {
		throw NativeUnavailableException("C")
	}
	actual override fun createDepthBuffer(arg0: Int, arg1: Int): DepthBuffer? {
		throw NativeUnavailableException("createDepthBuffer")
	}
	actual override fun stillwaterEffectLoaded(): Boolean {
		throw NativeUnavailableException("stillwaterEffectLoaded")
	}
	actual override fun L(arg0: Int, arg1: Int, arg2: Int) {
		throw NativeUnavailableException("L")
	}
	actual override fun createNativeSprite(arg0: IntArray?, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean): NativeSprite? {
		throw NativeUnavailableException("createNativeSprite")
	}
	actual override fun la() {
		throw NativeUnavailableException("la")
	}
	actual override fun setShrinkTextures(arg0: Boolean) {
		throw NativeUnavailableException("setShrinkTextures")
	}
	actual override fun M(): Int {
		throw NativeUnavailableException("M")
	}
	actual override fun supportsBloom(): Boolean {
		throw NativeUnavailableException("supportsBloom")
	}
	actual override fun supportsFullscreen(): Boolean {
		throw NativeUnavailableException("supportsFullscreen")
	}
	actual override fun drawTexturedLine(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, clippingMask: ComponentSurface?, arg6: Int, arg7: Int, arg8: Int, arg9: Int, arg10: Int) {
		throw NativeUnavailableException("drawTexturedLine")
	}
	actual override fun setCamera(matrix: Matrix?) {
		throw NativeUnavailableException("setCamera")
	}
	actual override fun restoreSurface() {
		throw NativeUnavailableException("restoreSurface")
	}
	actual override fun createGround(arg0: Int, arg1: Int, tileHeights: Array<IntArray?>?, arg3: Array<IntArray?>?, arg4: Int, arg5: Int): Ground? {
		throw NativeUnavailableException("createGround")
	}
	actual override fun XA(): Int {
		throw NativeUnavailableException("XA")
	}
	actual override fun T(arg0: Int, arg1: Int, arg2: Int, arg3: Int) {
		throw NativeUnavailableException("T")
	}
	actual override fun utilisesNativeHeap(): Boolean {
		throw NativeUnavailableException("utilisesNativeHeap")
	}
	actual override fun renderLastFrame() {
		throw NativeUnavailableException("renderLastFrame")
	}
	actual override fun hasBloom(): Boolean {
		throw NativeUnavailableException("hasBloom")
	}
	actual override fun supportsAntiAliasing(): Boolean {
		throw NativeUnavailableException("supportsAntiAliasing")
	}
}
