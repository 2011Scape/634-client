/*
 * Not part of the original client: the native software toolkit (2) in a browser (milestone 4). The
 * sw3d library is not there; like void-client's browser oa, this one draws with the client's own
 * software renderer: it hands every Toolkit call to a JavaToolkit, the same renderer as toolkit 0,
 * instead of duplicating it. The client cannot tell the difference except by the class (SceneCullingInit
 * checks it), so it behaves as it does with the native toolkit.
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
	private val delegate: JavaToolkit
	actual var anInt7473: Int = 0
	actual var nativeid: Long = 0L
	actual constructor(canvas: Canvas?, textureSource: MaterialProvider?, arg2: Int, arg3: Int) : super(textureSource) {
		this.delegate = JavaToolkit(canvas, textureSource, arg2, arg3)
	}
	actual override fun outlineRect(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) = delegate.outlineRect(arg0, arg1, arg2, arg3, arg4, arg5)
	actual override fun createCubeTexture(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): EnvironmentSampler? = delegate.createCubeTexture(arg0, arg1, arg2, arg3, arg4, arg5)
	actual override fun setHeap(memoryPool: MemoryPool?) = delegate.setHeap(memoryPool)
	actual override fun w(arg0: Boolean) {
	}
	actual override fun A(arg0: Int, arg1: ComponentSurface?, arg2: Int, arg3: Int) = delegate.A(arg0, arg1, arg2, arg3)
	actual override fun aa(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) = delegate.aa(arg0, arg1, arg2, arg3, arg4, arg5)
	actual override fun compositeBloomEffects() = delegate.compositeBloomEffects()
	actual override fun flipDirtyRect(rectangles: Array<Rectangle?>?, arg1: Int, arg2: Int, arg3: Int) = delegate.flipDirtyRect(rectangles, arg1, arg2, arg3)
	actual override fun method7311(): Boolean = delegate.method7311()
	actual override fun renderOrtho(particleList: ParticleList?, arg1: Int) = delegate.renderOrtho(particleList, arg1)
	actual override fun drawTriangle() = delegate.drawTriangle()
	actual override fun cacheReset() = delegate.cacheReset()
	actual override fun generateSprite(indexedImage: SpriteType?, arg1: Boolean): NativeSprite? = delegate.generateSprite(indexedImage, arg1)
	actual override fun HA(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: IntArray?) = delegate.HA(arg0, arg1, arg2, arg3, arg4)
	actual override fun stopBloom() = delegate.stopBloom()
	actual override fun setTextureSize(arg0: Int) = delegate.setTextureSize(arg0)
	actual override fun H(arg0: Int, arg1: Int, arg2: Int, arg3: IntArray?) = delegate.H(arg0, arg1, arg2, arg3)
	actual override fun b(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Double) = delegate.b(arg0, arg1, arg2, arg3, arg4)
	actual override fun setBloomParams(arg0: Float, arg1: Float, arg2: Float) = delegate.setBloomParams(arg0, arg1, arg2)
	actual override fun createFontRenderer(fontMetrics: FontMetrics?, indexedImages: Array<SpriteType?>?): AbstractFontRenderer? = delegate.createFontRenderer(fontMetrics, indexedImages)
	actual override fun hasTextures(): Boolean = delegate.hasTextures()
	actual fun method6405(): a? {
		return null
	}
	actual override fun scratchMatrix(): Matrix? = delegate.scratchMatrix()
	actual override fun linkThreads(arg0: Int) = delegate.linkThreads(arg0)
	actual override fun accumulateViewportDimensions(arg0: IntArray?) = delegate.accumulateViewportDimensions(arg0)
	actual override fun DA(arg0: Int, arg1: Int, arg2: Int, arg3: Int) = delegate.DA(arg0, arg1, arg2, arg3)
	actual override fun createHeap(arg0: Int): MemoryPool? = delegate.createHeap(arg0)
	actual override fun initialize() = delegate.initialize()
	actual override fun compareFunctionMasks(arg0: Int, arg1: Int): Int = delegate.compareFunctionMasks(arg0, arg1)
	actual override fun xa(arg0: Float) = delegate.xa(arg0)
	actual override fun releaseSurface(canvas: Canvas?) = delegate.releaseSurface(canvas)
	actual override fun drawSurfaceLine(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, clippingMask: ComponentSurface?, arg6: Int, arg7: Int) = delegate.drawSurfaceLine(arg0, arg1, arg2, arg3, arg4, clippingMask, arg6, arg7)
	actual override fun finalize() = delegate.finalize()
	actual override fun i(): Int = delegate.i()
	actual override fun render(particleList: ParticleList?) = delegate.render(particleList)
	actual override fun getMaxLights(): Int = delegate.getMaxLights()
	actual override fun JA(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int): Int = delegate.JA(arg0, arg1, arg2, arg3, arg4, arg5)
	actual override fun stop() = delegate.free()
	actual override fun resizeCanvas(canvas: Canvas?, arg1: Int, arg2: Int) = delegate.resizeCanvas(canvas, arg1, arg2)
	actual override fun bloom(): Boolean = delegate.bloom()
	actual override fun Q(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: ByteArray?, arg7: Int, arg8: Int) = delegate.Q(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
	actual override fun generateEmptySprite(arg0: Int, arg1: Int, arg2: Boolean): NativeSprite? = delegate.generateEmptySprite(arg0, arg1, arg2)
	actual override fun ya() = delegate.ya()
	actual override fun I(): Int = delegate.I()
	actual override fun f(arg0: Int, arg1: Int) = delegate.f(arg0, arg1)
	actual override fun KA(arg0: Int, arg1: Int, arg2: Int, arg3: Int) = delegate.KA(arg0, arg1, arg2, arg3)
	actual override fun tick(arg0: Int) = delegate.tick(arg0)
	actual override fun combineFunctionMasks(arg0: Int, arg1: Int): Int = delegate.combineFunctionMasks(arg0, arg1)
	actual override fun ra(arg0: Int, arg1: Int, arg2: Int, arg3: Int) = delegate.ra(arg0, arg1, arg2, arg3)
	actual override fun E(): Int = delegate.E()
	actual override fun pa() = delegate.pa()
	actual override fun strongLine(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) = delegate.strongLine(arg0, arg1, arg2, arg3, arg4, arg5)
	actual override fun GA(arg0: Int) = delegate.GA(arg0)
	actual override fun extendBuildArea(): Boolean = delegate.extendBuildArea()
	actual override fun X(arg0: Int) = delegate.X(arg0)
	actual override fun createModel(mesh: Mesh?, arg1: Int, arg2: Int, arg3: Int, arg4: Int): Model? = delegate.createModel(mesh, arg1, arg2, arg3, arg4)
	actual override fun renderer(): Renderer? = delegate.renderer()
	actual override fun drawLine(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int, arg7: Int) = delegate.drawLine(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
	actual override fun F(arg0: Int, arg1: Int) = delegate.F(arg0, arg1)
	actual override fun createNativeSprite(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean): NativeSprite? = delegate.createNativeSprite(arg0, arg1, arg2, arg3, arg4)
	actual override fun allocateThreads(arg0: Int) = delegate.allocateThreads(arg0)
	actual override fun r(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int, arg6: Int): Int = delegate.r(arg0, arg1, arg2, arg3, arg4, arg5, arg6)
	actual override fun addCanvas(canvas: Canvas?, arg1: Int, arg2: Int) = delegate.addCanvas(canvas, arg1, arg2)
	actual override fun drawQuad(environmentSampler: EnvironmentSampler?, environmentSampler2: EnvironmentSampler?, arg2: Float, environmentSampler3: EnvironmentSampler?): EnvironmentSampler? = delegate.drawQuad(environmentSampler, environmentSampler2, arg2, environmentSampler3)
	actual override fun K(arg0: IntArray?) = delegate.K(arg0)
	actual override fun replaceCanvasOnDisplayUpdate(): Boolean = delegate.replaceCanvasOnDisplayUpdate()
	actual override fun flip(arg0: Int, arg1: Int) = delegate.flip(arg0, arg1)
	actual override fun method7268(arg0: Int, arg1: Int): Sprite? = delegate.method7268(arg0, arg1)
	actual override fun copyLights(arg0: Int, pointLights: Array<PointLight?>?) = delegate.copyLights(arg0, pointLights)
	actual override fun za(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) = delegate.za(arg0, arg1, arg2, arg3, arg4)
	actual override fun na(arg0: Int, arg1: Int, arg2: Int, arg3: Int): IntArray? = delegate.na(arg0, arg1, arg2, arg3)
	actual override fun method7250(arg0: Boolean) = delegate.method7250(arg0)
	actual override fun createPointLight(arg0: Int, arg1: Int, arg2: Int, arg3: Int, range: Int, intensity: Float): PointLight? = delegate.createPointLight(arg0, arg1, arg2, arg3, range, intensity)
	actual override fun setCanvas(canvas: Canvas?) = delegate.setCanvas(canvas)
	actual override fun hardShadow(): Boolean = delegate.hardShadow()
	actual override fun setSunCubeTexture(environmentSampler: EnvironmentSampler?) = delegate.setSunCubeTexture(environmentSampler)
	actual override fun createMatrix(): Matrix? = delegate.createMatrix()
	actual override fun method7281(arg0: Int) = delegate.method7281(arg0)
	actual override fun EA(arg0: Int, arg1: Int, arg2: Int, arg3: Int) = delegate.EA(arg0, arg1, arg2, arg3)
	actual override fun finish() = delegate.finish()
	actual override fun method7261(arg0: Int, arg1: Int, arg2: Int, arg3: Int) = delegate.method7261(arg0, arg1, arg2, arg3)
	actual override fun P(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) = delegate.P(arg0, arg1, arg2, arg3, arg4)
	actual override fun forceFixedScreenmode(): Boolean = delegate.forceFixedScreenmode()
	actual override fun U(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int) = delegate.U(arg0, arg1, arg2, arg3, arg4)
	actual override fun Y(): IntArray? = delegate.Y()
	actual override fun ZA(arg0: Int, arg1: Float, arg2: Float, arg3: Float, arg4: Float, arg5: Float) = delegate.ZA(arg0, arg1, arg2, arg3, arg4, arg5)
	actual override fun createOffscreenSurface(surface: Sprite?, depthBuffer: DepthBuffer?): OffscreenSurface? = delegate.createOffscreenSurface(surface, depthBuffer)
	actual override fun swapSurface(offscreenSurface: OffscreenSurface?) = delegate.swapSurface(offscreenSurface)
	actual override fun da(arg0: Int, arg1: Int, arg2: Int, arg3: IntArray?) = delegate.da(arg0, arg1, arg2, arg3)
	actual override fun createMask(arg0: Int, arg1: Int, arg2: IntArray?, arg3: IntArray?): ComponentSurface? = delegate.createMask(arg0, arg1, arg2, arg3)
	actual override fun camera(): Matrix? = delegate.camera()
	actual override fun line(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, arg5: Int) = delegate.line(arg0, arg1, arg2, arg3, arg4, arg5)
	actual override fun C(arg0: Boolean) = delegate.C(arg0)
	actual override fun createDepthBuffer(arg0: Int, arg1: Int): DepthBuffer? = delegate.createDepthBuffer(arg0, arg1)
	actual override fun stillwaterEffectLoaded(): Boolean = delegate.stillwaterEffectLoaded()
	actual override fun L(arg0: Int, arg1: Int, arg2: Int) = delegate.L(arg0, arg1, arg2)
	actual override fun createNativeSprite(arg0: IntArray?, arg1: Int, arg2: Int, arg3: Int, arg4: Boolean): NativeSprite? = delegate.createNativeSprite(arg0, arg1, arg2, arg3, arg4)
	actual override fun la() = delegate.la()
	actual override fun setShrinkTextures(arg0: Boolean) = delegate.setShrinkTextures(arg0)
	actual override fun M(): Int = delegate.M()
	actual override fun supportsBloom(): Boolean = delegate.supportsBloom()
	actual override fun supportsFullscreen(): Boolean = delegate.supportsFullscreen()
	actual override fun drawTexturedLine(arg0: Int, arg1: Int, arg2: Int, arg3: Int, arg4: Int, clippingMask: ComponentSurface?, arg6: Int, arg7: Int, arg8: Int, arg9: Int, arg10: Int) = delegate.drawTexturedLine(arg0, arg1, arg2, arg3, arg4, clippingMask, arg6, arg7, arg8, arg9, arg10)
	actual override fun setCamera(matrix: Matrix?) = delegate.setCamera(matrix)
	actual override fun restoreSurface() = delegate.restoreSurface()
	actual override fun createGround(arg0: Int, arg1: Int, tileHeights: Array<IntArray?>?, arg3: Array<IntArray?>?, arg4: Int, arg5: Int): Ground? = delegate.createGround(arg0, arg1, tileHeights, arg3, arg4, arg5)
	actual override fun XA(): Int = delegate.XA()
	actual override fun T(arg0: Int, arg1: Int, arg2: Int, arg3: Int) = delegate.T(arg0, arg1, arg2, arg3)
	actual override fun utilisesNativeHeap(): Boolean = delegate.utilisesNativeHeap()
	actual override fun renderLastFrame() = delegate.renderLastFrame()
	actual override fun hasBloom(): Boolean = delegate.hasBloom()
	actual override fun supportsAntiAliasing(): Boolean = delegate.supportsAntiAliasing()
}
