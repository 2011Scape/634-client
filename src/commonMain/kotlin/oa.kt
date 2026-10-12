/*
 * Not part of the original client: the common declaration of a class whose JVM version calls
 * native code (src/jvmMain/kotlin/oa.kt), so that common code can use it (milestone 3).
 */
import com.jagex.game.runetek5.DepthBuffer
import com.jagex.game.runetek5.EnvironmentSampler
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.cache.node.JavaSurface
import com.jagex.game.runetek5.cache.node.SsePointLight
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.FlipException
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.cache.node.MemoryPool
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.OffscreenSurface
import com.jagex.game.runetek5.graphics.model.particle.PointEntity
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.graphics.Renderer
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sprite.Sprite
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.config.material.MaterialProvider
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager
import com.jagex.game.runetek5.graphics.sw.SoftwareObject
import com.jagex.game.runetek5.statics.NativeModelScratch
import awt.Canvas
import awt.Dimension
import awt.Graphics
import awt.Rectangle
import lang.reflect.Method
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.NativeLibraryOwner

expect class oa : Toolkit, SoftwareObject {
	var anInt7473: Int
	var nativeid: Long
	constructor(canvas: Canvas?, textureSource: MaterialProvider?, arg2: Int, arg3: Int)
	override fun outlineRect(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int)
	override fun createCubeTexture(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): EnvironmentSampler?
	override fun setHeap(memoryPool: MemoryPool?)
	override fun w(arg0: Boolean)
	override fun A(arg0: Int, arg1: ComponentSurface?, arg2: Int, arg3: Int)
	override fun aa(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int)
	override fun compositeBloomEffects()
	override fun flipDirtyRect(rectangles: Array<Rectangle?>?, arg1: Int, arg2: Int, arg3: Int)
	override fun method7311(): Boolean
	override fun renderOrtho(particleList: ParticleList?, arg1: Int)
	override fun drawTriangle()
	override fun cacheReset()
	override fun generateSprite(indexedImage: SpriteType?, arg1: Boolean): NativeSprite?
	override fun HA(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: IntArray?)
	override fun stopBloom()
	override fun setTextureSize(arg0: Int)
	override fun H(arg0: Int, arg1: Int, arg2: Int, arg3: IntArray?)
	override fun b(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Double)
	override fun setBloomParams(arg0: Float, arg1: Float, arg2: Float)
	override fun createFontRenderer(fontMetrics: FontMetrics?, indexedImages: Array<SpriteType?>?): AbstractFontRenderer?
	override fun hasTextures(): Boolean
	fun method6405(): a?
	override fun scratchMatrix(): Matrix?
	override fun linkThreads(arg0: Int)
	override fun accumulateViewportDimensions(arg0: IntArray?)
	override fun DA(arg0: Int, arg1: Int, arg2: Int, arg3: Int)
	override fun createHeap(arg0: Int): MemoryPool?
	override fun initialize()
	override fun compareFunctionMasks(arg0: Int, arg1: Int): Int
	override fun xa(arg0: Float)
	override fun releaseSurface(canvas: Canvas?)
	override fun drawSurfaceLine(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, clippingMask: ComponentSurface?, arg6: Int, arg7: Int)
	override fun finalize()
	override fun i(): Int
	override fun render(particleList: ParticleList?)
	override fun getMaxLights(): Int
	override fun JA(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): Int
	override fun stop()
	override fun resizeCanvas(canvas: Canvas?, arg1: Int, arg2: Int)
	override fun bloom(): Boolean
	override fun Q(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: ByteArray?, arg7: Int, arg8: Int)
	override fun generateEmptySprite(arg0: Int, arg1: Int, arg2: Boolean): NativeSprite?
	override fun ya()
	override fun I(): Int
	override fun f(arg0: Int, arg1: Int)
	override fun KA(arg0: Int, arg1: Int, arg2: Int, arg3: Int)
	override fun tick(arg0: Int)
	override fun combineFunctionMasks(arg0: Int, arg1: Int): Int
	override fun ra(arg0: Int, arg1: Int, arg2: Int, arg3: Int)
	override fun E(): Int
	override fun pa()
	override fun strongLine(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int)
	override fun GA(arg0: Int)
	override fun extendBuildArea(): Boolean
	override fun X(arg0: Int)
	override fun createModel(mesh: Mesh?, arg1: Int, arg2: Int, arg3: Int, arg4: Int): Model?
	override fun renderer(): Renderer?
	override fun drawLine(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int)
	override fun F(arg0: Int, arg1: Int)
	override fun createNativeSprite(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean): NativeSprite?
	override fun allocateThreads(arg0: Int)
	override fun r(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int): Int
	override fun addCanvas(canvas: Canvas?, arg1: Int, arg2: Int)
	override fun drawQuad(environmentSampler: EnvironmentSampler?, environmentSampler2: EnvironmentSampler?, arg2: Float, environmentSampler3: EnvironmentSampler?): EnvironmentSampler?
	override fun K(arg0: IntArray?)
	override fun replaceCanvasOnDisplayUpdate(): Boolean
	override fun flip(arg0: Int, arg1: Int)
	override fun method7268(arg0: Int, arg1: Int): Sprite?
	override fun copyLights(arg0: Int, pointLights: Array<PointLight?>?)
	override fun za(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int)
	override fun na(arg0: Int, arg1: Int, arg2: Int, arg3: Int): IntArray?
	override fun method7250(arg0: Boolean)
	override fun createPointLight(arg0: Int, arg1: Int, arg2: Int, arg3: Int, range: Int, intensity: Float): PointLight?
	override fun setCanvas(canvas: Canvas?)
	override fun hardShadow(): Boolean
	override fun setSunCubeTexture(environmentSampler: EnvironmentSampler?)
	override fun createMatrix(): Matrix?
	override fun method7281(arg0: Int)
	override fun EA(arg0: Int, arg1: Int, arg2: Int, arg3: Int)
	override fun finish()
	override fun method7261(arg0: Int, arg1: Int, arg2: Int, arg3: Int)
	override fun P(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int)
	override fun forceFixedScreenmode(): Boolean
	override fun U(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int)
	override fun Y(): IntArray?
	override fun ZA(arg0: Int, arg1: Float, arg2: Float, arg3: Float, arg4: Float, arg5: Float)
	override fun createOffscreenSurface(surface: Sprite?, depthBuffer: DepthBuffer?): OffscreenSurface?
	override fun swapSurface(offscreenSurface: OffscreenSurface?)
	override fun da(arg0: Int, arg1: Int, arg2: Int, arg3: IntArray?)
	override fun createMask(arg0: Int, arg1: Int, arg2: IntArray?, arg3: IntArray?): ComponentSurface?
	override fun camera(): Matrix?
	override fun line(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int)
	override fun C(arg0: Boolean)
	override fun createDepthBuffer(arg0: Int, arg1: Int): DepthBuffer?
	override fun stillwaterEffectLoaded(): Boolean
	override fun L(arg0: Int, arg1: Int, arg2: Int)
	override fun createNativeSprite(arg0: IntArray?, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean): NativeSprite?
	override fun la()
	override fun setShrinkTextures(arg0: Boolean)
	override fun M(): Int
	override fun supportsBloom(): Boolean
	override fun supportsFullscreen(): Boolean
	override fun drawTexturedLine(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, clippingMask: ComponentSurface?, arg6: Int, arg7: Int, arg8: Int, arg9: Int, arg10: Int)
	override fun setCamera(matrix: Matrix?)
	override fun restoreSurface()
	override fun createGround(arg0: Int, arg1: Int, tileHeights: Array<IntArray?>?, arg3: Array<IntArray?>?, arg4: Int, arg5: Int): Ground?
	override fun XA(): Int
	override fun T(arg0: Int, arg1: Int, arg2: Int, arg3: Int)
	override fun utilisesNativeHeap(): Boolean
	override fun renderLastFrame()
	override fun hasBloom(): Boolean
	override fun supportsAntiAliasing(): Boolean
}
