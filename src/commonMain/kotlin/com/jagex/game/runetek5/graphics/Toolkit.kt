package com.jagex.game.runetek5.graphics

import com.jagex.game.runetek5.DepthBuffer
import com.jagex.game.runetek5.EnvironmentSampler
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface
import com.jagex.game.runetek5.graphics.FlipException
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.cache.node.MemoryPool
import com.jagex.game.runetek5.graphics.OffscreenSurface
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.graphics.Renderer
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sprite.Sprite
import com.jagex.game.runetek5.config.material.MaterialProvider
import awt.Canvas
import awt.Rectangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.toolkit.AbstractToolkit
import kotlin.jvm.JvmField

@OriginalClass("client!ha")
abstract class Toolkit {

	@OriginalMember(owner = "client!ha", name = "s", descriptor = "Lclient!d;")
	@JvmField
	val materialProvider: MaterialProvider?

	@OriginalMember(owner = "client!ha", name = "g", descriptor = "I")
	@JvmField
	val index: Int

	@OriginalMember(owner = "client!ha", name = "<init>", descriptor = "(Lclient!d;)V")
	constructor(@OriginalArg(0) textureSource: MaterialProvider?) {
		this.materialProvider = textureSource
		@Pc(8) var freeIndex: Int = -1
		@Pc(10) var i: Int = 0
		while (i < 8) {
			if (!AbstractToolkit.ACTIVE_RENDER_UNITS[i]) {
				AbstractToolkit.ACTIVE_RENDER_UNITS[i] = true
				freeIndex = i
				break
			}
			i++
		}
		if (freeIndex == -1) {
			throw IllegalStateException("NFTI")
		}
		this.index = freeIndex
	}

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(II)Lclient!vm;")
	abstract fun createDepthBuffer(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int): DepthBuffer?

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(IIIID)V")
	abstract fun b(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) depthOffset: Double)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!bh;IIII)Lclient!ka;")
	abstract fun createModel(@OriginalArg(0) mesh: Mesh?, @OriginalArg(1) functionMask: Int, @OriginalArg(2) featureMask: Int, @OriginalArg(3) ambient: Int, @OriginalArg(4) contrast: Int): Model?

	@OriginalMember(owner = "client!ha", name = "F", descriptor = "(II)V")
	abstract fun F(@OriginalArg(0) offsetX: Int, @OriginalArg(1) offsetY: Int)

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(II)V")
	@Throws(FlipException::class)
	abstract fun flip(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "()Z")
	abstract fun supportsBloom(): Boolean

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIII)V")
	abstract fun outlineRect(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) mode: Int)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIZ)Lclient!mi;")
	abstract fun generateEmptySprite(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int, @OriginalArg(2) hasAlpha: Boolean): NativeSprite?

	@OriginalMember(owner = "client!ha", name = "Y", descriptor = "()[I")
	abstract fun Y(): IntArray?

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Z)V")
	abstract fun setShrinkTextures(@OriginalArg(0) shrink: Boolean)

	@OriginalMember(owner = "client!ha", name = "E", descriptor = "()I")
	abstract fun E(): Int

	@OriginalMember(owner = "client!ha", name = "I", descriptor = "()I")
	abstract fun I(): Int

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(I)V")
	abstract fun allocateThreads(@OriginalArg(0) threadCount: Int)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "([I)V")
	abstract fun accumulateViewportDimensions(@OriginalArg(0) cacheInto: IntArray?)

	@OriginalMember(owner = "client!ha", name = "X", descriptor = "(I)V")
	abstract fun X(@OriginalArg(0) shadowSize: Int)

	@OriginalMember(owner = "client!ha", name = "xa", descriptor = "(F)V")
	abstract fun xa(@OriginalArg(0) intensity: Float)

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "()V")
	abstract fun finish()

	@OriginalMember(owner = "client!ha", name = "ya", descriptor = "()V")
	abstract fun ya()

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!he;Lclient!vm;)Lclient!is;")
	abstract fun createOffscreenSurface(@OriginalArg(0) sprite: Sprite?, @OriginalArg(1) depthBuffer: DepthBuffer?): OffscreenSurface?

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(B)V")
	fun free() {
		AbstractToolkit.ACTIVE_RENDER_UNITS[this.index] = false
		this.stop()
	}

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIIILclient!aa;II)V")
	abstract fun drawSurfaceLine(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) colour: Int, @OriginalArg(6) clippingMask: ComponentSurface?, @OriginalArg(7) maskX: Int, @OriginalArg(8) maskY: Int)

	@OriginalMember(owner = "client!ha", name = "c", descriptor = "()Lclient!fia;")
	abstract fun renderer(): Renderer?

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!bca;)V")
	abstract fun setCamera(@OriginalArg(0) matrix: Matrix?)

	@OriginalMember(owner = "client!ha", name = "na", descriptor = "(IIII)[I")
	abstract fun na(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int): IntArray?

	@OriginalMember(owner = "client!ha", name = "r", descriptor = "(IIIIIII)I")
	abstract fun r(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) z1: Int, @OriginalArg(3) x2: Int, @OriginalArg(4) y2: Int, @OriginalArg(5) z2: Int, @OriginalArg(6) zoom: Int): Int

	@OriginalMember(owner = "client!ha", name = "DA", descriptor = "(IIII)V")
	abstract fun DA(@OriginalArg(0) projectionCenterX: Int, @OriginalArg(1) projectionCenterY: Int, @OriginalArg(2) projectionScaleX: Int, @OriginalArg(3) projectionScaleY: Int)

	@OriginalMember(owner = "client!ha", name = "d", descriptor = "()Z")
	abstract fun stillwaterEffectLoaded(): Boolean

	@OriginalMember(owner = "client!ha", name = "e", descriptor = "()Lclient!bca;")
	abstract fun camera(): Matrix?

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(I[Lclient!ab;)V")
	abstract fun copyLights(@OriginalArg(0) count: Int, @OriginalArg(1) lights: Array<PointLight?>?)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Ljava/awt/Canvas;II)V")
	abstract fun addCanvas(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int)

	@OriginalMember(owner = "client!ha", name = "f", descriptor = "()Z")
	abstract fun hasTextures(): Boolean

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(IIIIII)V")
	fun drawOpaqueLine(@OriginalArg(0) y: Int, @OriginalArg(1) x: Int, @OriginalArg(2) width: Int, @OriginalArg(4) colorArgb: Int, @OriginalArg(5) height: Int) {
		this.line(x, y, width, height, colorArgb, 1)
	}

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(I)V")
	abstract fun tick(@OriginalArg(0) time: Int)

	/** Turns the native toolkit's own profiling on or off with the profiling overlay; the Java toolkits ignore it. */
	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(Z)V")
	abstract fun method7250(@OriginalArg(0) enabled: Boolean)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(II[[I[[IIII)Lclient!s;")
	abstract fun createGround(@OriginalArg(0) width: Int, @OriginalArg(1) length: Int, @OriginalArg(2) tileHeights: Array<IntArray?>?, @OriginalArg(3) nextLevelHeights: Array<IntArray?>?, @OriginalArg(5) groundFlags: Int, @OriginalArg(6) featureFlags: Int): Ground?

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(BIIII)V")
	fun horizontalLine(@OriginalArg(1) y: Int, @OriginalArg(2) colour: Int, @OriginalArg(3) x: Int, @OriginalArg(4) strength: Int) {
		this.U(strength, colour, y, x, 1)
	}

	@OriginalMember(owner = "client!ha", name = "c", descriptor = "(I)V")
	abstract fun initialize()

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!za;)V")
	abstract fun setHeap(@OriginalArg(0) heap: MemoryPool?)

	@OriginalMember(owner = "client!ha", name = "P", descriptor = "(IIIII)V")
	abstract fun P(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) height: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) mode: Int)

	@OriginalMember(owner = "client!ha", name = "h", descriptor = "()V")
	abstract fun stop()

	@OriginalMember(owner = "client!ha", name = "za", descriptor = "(IIIII)V")
	abstract fun za(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) mode: Int)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!wh;)V")
	abstract fun setSunCubeTexture(@OriginalArg(0) cubeTexture: EnvironmentSampler?)

	@OriginalMember(owner = "client!ha", name = "j", descriptor = "()Lclient!bca;")
	abstract fun createMatrix(): Matrix?

	@OriginalMember(owner = "client!ha", name = "JA", descriptor = "(IIIIII)I")
	abstract fun JA(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) z1: Int, @OriginalArg(3) x2: Int, @OriginalArg(4) y2: Int, @OriginalArg(5) z2: Int): Int

	@OriginalMember(owner = "client!ha", name = "i", descriptor = "()I")
	abstract fun i(): Int

	@OriginalMember(owner = "client!ha", name = "k", descriptor = "()Z")
	abstract fun supportsFullscreen(): Boolean

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(BIIII)V")
	fun fillCircle(@OriginalArg(1) colour: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) x: Int, @OriginalArg(4) y: Int) {
		this.za(x, y, radius, colour, 1)
	}

	@OriginalMember(owner = "client!ha", name = "L", descriptor = "(III)V")
	abstract fun L(@OriginalArg(0) colour: Int, @OriginalArg(1) distance: Int, @OriginalArg(2) offset: Int)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "([Ljava/awt/Rectangle;II)V")
	@Throws(FlipException::class)
	fun flipDirtyRects(@OriginalArg(0) rectangles: Array<Rectangle?>?, @OriginalArg(1) count: Int) {
		this.flipDirtyRect(rectangles, count, 0, 0)
	}

	@OriginalMember(owner = "client!ha", name = "f", descriptor = "(II)V")
	abstract fun f(@OriginalArg(0) zNear: Int, @OriginalArg(1) zFar: Int)

	@OriginalMember(owner = "client!ha", name = "la", descriptor = "()V")
	abstract fun la()

	/** Prepares post-processing (bloom) for the area of a container component about to be drawn: x, y, width and height on screen. */
	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIII)V")
	abstract fun method7261(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int)

	@OriginalMember(owner = "client!ha", name = "e", descriptor = "(I)V")
	abstract fun linkThreads(@OriginalArg(0) threadId: Int)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIZ)V")
	fun verticalLine(@OriginalArg(0) x: Int, @OriginalArg(1) colour: Int, @OriginalArg(2) height: Int, @OriginalArg(3) y: Int) {
		this.P(x, y, height, colour, 1)
	}

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(II[I[I)Lclient!aa;")
	abstract fun createMask(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int, @OriginalArg(2) offsets: IntArray?, @OriginalArg(3) widths: IntArray?): ComponentSurface?

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(I[IBIII)Lclient!mi;")
	fun createNativeSprite(@OriginalArg(0) width: Int, @OriginalArg(1) pixels: IntArray?, @OriginalArg(4) rowLength: Int, @OriginalArg(5) height: Int): NativeSprite? {
		return this.createNativeSprite(pixels, rowLength, width, height, true)
	}

	@OriginalMember(owner = "client!ha", name = "l", descriptor = "()V")
	abstract fun cacheReset()

	/** Creates a blank sprite of the given size to render into; only the software toolkit has one, the others return null. */
	@OriginalMember(owner = "client!ha", name = "d", descriptor = "(II)Lclient!he;")
	abstract fun method7268(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int): Sprite?

	@OriginalMember(owner = "client!ha", name = "pa", descriptor = "()V")
	abstract fun pa()

	@OriginalMember(owner = "client!ha", name = "m", descriptor = "()Z")
	abstract fun bloom(): Boolean

	@OriginalMember(owner = "client!ha", name = "da", descriptor = "(III[I)V")
	abstract fun da(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) screenCoords: IntArray?)

	@OriginalMember(owner = "client!ha", name = "e", descriptor = "(II)I")
	abstract fun compareFunctionMasks(@OriginalArg(0) maskA: Int, @OriginalArg(1) maskB: Int): Int

	@OriginalMember(owner = "client!ha", name = "Q", descriptor = "(IIIIII[BII)V")
	abstract fun Q(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) backgroundColour: Int, @OriginalArg(5) foregroundColour: Int, @OriginalArg(6) mask: ByteArray?, @OriginalArg(7) maskWidth: Int, @OriginalArg(8) mode: Int)

	@OriginalMember(owner = "client!ha", name = "c", descriptor = "(IIIIII)V")
	fun drawOpaqueRectangle(@OriginalArg(0) width: Int, @OriginalArg(1) y: Int, @OriginalArg(2) color: Int, @OriginalArg(3) x: Int, @OriginalArg(4) height: Int) {
		this.outlineRect(x, y, width, height, color, 1)
	}

	@OriginalMember(owner = "client!ha", name = "HA", descriptor = "(IIII[I)V")
	abstract fun HA(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) zoom: Int, @OriginalArg(4) screenCoords: IntArray?)

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(Ljava/awt/Canvas;II)V")
	abstract fun resizeCanvas(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int)

	@OriginalMember(owner = "client!ha", name = "n", descriptor = "()Z")
	abstract fun extendBuildArea(): Boolean

	@OriginalMember(owner = "client!ha", name = "o", descriptor = "()Z")
	abstract fun hasBloom(): Boolean

	@OriginalMember(owner = "client!ha", name = "p", descriptor = "()V")
	abstract fun restoreSurface()

	@OriginalMember(owner = "client!ha", name = "q", descriptor = "()V")
	abstract fun renderLastFrame()

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIIIIII)V")
	abstract fun drawLine(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) colorArgb: Int, @OriginalArg(6) lineSpacing: Int, @OriginalArg(7) lineLength: Int, @OriginalArg(8) offset: Int)

	@OriginalMember(owner = "client!ha", name = "EA", descriptor = "(IIII)V")
	abstract fun EA(@OriginalArg(0) waterHeight: Int, @OriginalArg(1) waterColour: Int, @OriginalArg(2) waterScale: Int, @OriginalArg(3) waterIntensity: Int)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IBIIII)V")
	fun fillRect(@OriginalArg(0) width: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) height: Int, @OriginalArg(5) colour: Int) {
		this.aa(x, y, width, height, colour, 1)
	}

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIIIIIIIIII)V")
	abstract fun drawTriangle()

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Ljava/awt/Canvas;)V")
	abstract fun setCanvas(@OriginalArg(0) canvas: Canvas?)

	/** Releases what a render worker thread holds of the toolkit when it finishes its part of the frame (its thread resources or context). */
	@OriginalMember(owner = "client!ha", name = "f", descriptor = "(I)V")
	abstract fun method7281(@OriginalArg(0) threadId: Int)

	@OriginalMember(owner = "client!ha", name = "g", descriptor = "(II)I")
	abstract fun combineFunctionMasks(@OriginalArg(0) maskA: Int, @OriginalArg(1) maskB: Int): Int

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!mi;I)V")
	fun renderToSprite(@OriginalArg(0) sprite: NativeSprite?) {
		this.swapSurface(this.createOffscreenSurface(sprite, this.createDepthBuffer(sprite!!.getWidth(), sprite.getHeight())))
	}

	@OriginalMember(owner = "client!ha", name = "r", descriptor = "()Z")
	abstract fun hardShadow(): Boolean

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(IIIIZ)Lclient!mi;")
	abstract fun createNativeSprite(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) hasAlpha: Boolean): NativeSprite?

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!mda;)V")
	abstract fun render(@OriginalArg(0) particleList: ParticleList?)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!mda;I)V")
	abstract fun renderOrtho(@OriginalArg(0) particleList: ParticleList?, @OriginalArg(1) zoom: Int)

	@OriginalMember(owner = "client!ha", name = "K", descriptor = "([I)V")
	abstract fun K(@OriginalArg(0) clip: IntArray?)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!oea;[Lclient!rh;Z)Lclient!da;")
	abstract fun createFontRenderer(@OriginalArg(0) metrics: FontMetrics?, @OriginalArg(1) images: Array<SpriteType?>?): AbstractFontRenderer?

	@OriginalMember(owner = "client!ha", name = "H", descriptor = "(III[I)V")
	abstract fun H(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) screenCoords: IntArray?)

	@OriginalMember(owner = "client!ha", name = "T", descriptor = "(IIII)V")
	abstract fun T(@OriginalArg(0) left: Int, @OriginalArg(1) top: Int, @OriginalArg(2) right: Int, @OriginalArg(3) bottom: Int)

	@OriginalMember(owner = "client!ha", name = "ZA", descriptor = "(IFFFFF)V")
	abstract fun ZA(@OriginalArg(0) colour: Int, @OriginalArg(1) sunIntensity: Float, @OriginalArg(2) antiSunIntensity: Float, @OriginalArg(3) x: Float, @OriginalArg(4) y: Float, @OriginalArg(5) z: Float)

	@OriginalMember(owner = "client!ha", name = "ra", descriptor = "(IIII)V")
	abstract fun ra(@OriginalArg(0) waterHeight: Int, @OriginalArg(1) waterColour: Int, @OriginalArg(2) waterScale: Int, @OriginalArg(3) waterIntensity: Int)

	@OriginalMember(owner = "client!ha", name = "M", descriptor = "()I")
	abstract fun M(): Int

	@OriginalMember(owner = "client!ha", name = "C", descriptor = "(Z)V")
	abstract fun C(@OriginalArg(0) enabled: Boolean)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!is;)V")
	abstract fun swapSurface(@OriginalArg(0) surface: OffscreenSurface?)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIIII)V")
	abstract fun strongLine(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) x2: Int, @OriginalArg(3) y2: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) width: Int)

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(B)V")
	@Throws(FlipException::class)
	fun renderFrame() {
		this.flip(0, 0)
	}

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIIF)Lclient!ab;")
	abstract fun createPointLight(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) radius: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) intensity: Float): PointLight?

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!rh;Z)Lclient!mi;")
	abstract fun generateSprite(@OriginalArg(0) spriteType: SpriteType?, @OriginalArg(1) paletteSprite: Boolean): NativeSprite?

	@OriginalMember(owner = "client!ha", name = "s", descriptor = "()Z")
	abstract fun utilisesNativeHeap(): Boolean

	@OriginalMember(owner = "client!ha", name = "t", descriptor = "()Z")
	abstract fun replaceCanvasOnDisplayUpdate(): Boolean

	@OriginalMember(owner = "client!ha", name = "u", descriptor = "()Z")
	abstract fun forceFixedScreenmode(): Boolean

	@OriginalMember(owner = "client!ha", name = "h", descriptor = "(I)V")
	abstract fun setTextureSize(@OriginalArg(0) size: Int)

	@OriginalMember(owner = "client!ha", name = "d", descriptor = "(IIIIII)Lclient!wh;")
	abstract fun createCubeTexture(@OriginalArg(0) materialPositiveX: Int, @OriginalArg(1) materialNegativeX: Int, @OriginalArg(2) materialPositiveY: Int, @OriginalArg(3) materialNegativeY: Int, @OriginalArg(4) materialPositiveZ: Int, @OriginalArg(5) materialNegativeZ: Int): EnvironmentSampler?

	@OriginalMember(owner = "client!ha", name = "finalize", descriptor = "()V")
	open fun finalize() {
		this.free()
	}

	@OriginalMember(owner = "client!ha", name = "KA", descriptor = "(IIII)V")
	abstract fun KA(@OriginalArg(0) left: Int, @OriginalArg(1) top: Int, @OriginalArg(2) right: Int, @OriginalArg(3) bottom: Int)

	@OriginalMember(owner = "client!ha", name = "v", descriptor = "()V")
	abstract fun compositeBloomEffects()

	@OriginalMember(owner = "client!ha", name = "w", descriptor = "()Z")
	abstract fun supportsAntiAliasing(): Boolean

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(FFF)V")
	abstract fun setBloomParams(@OriginalArg(0) whitePoint: Float, @OriginalArg(1) bloom: Float, @OriginalArg(2) brightpass: Float)

	@OriginalMember(owner = "client!ha", name = "A", descriptor = "(ILclient!aa;II)V")
	abstract fun A(@OriginalArg(0) colour: Int, @OriginalArg(1) mask: ComponentSurface?, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int)

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(Ljava/awt/Canvas;)V")
	abstract fun releaseSurface(@OriginalArg(0) canvas: Canvas?)

	@OriginalMember(owner = "client!ha", name = "i", descriptor = "(I)Lclient!za;")
	abstract fun createHeap(@OriginalArg(0) size: Int): MemoryPool?

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIIILclient!aa;IIIII)V")
	abstract fun drawTexturedLine(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) colour: Int, @OriginalArg(6) clippingMask: ComponentSurface?, @OriginalArg(7) maskX: Int, @OriginalArg(8) maskY: Int, @OriginalArg(9) lineSpacing: Int, @OriginalArg(10) lineLength: Int, @OriginalArg(11) offset: Int)

	@OriginalMember(owner = "client!ha", name = "x", descriptor = "()I")
	abstract fun getMaxLights(): Int

	@OriginalMember(owner = "client!ha", name = "y", descriptor = "()Lclient!bca;")
	abstract fun scratchMatrix(): Matrix?

	@OriginalMember(owner = "client!ha", name = "XA", descriptor = "()I")
	abstract fun XA(): Int

	@OriginalMember(owner = "client!ha", name = "U", descriptor = "(IIIII)V")
	abstract fun U(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) mode: Int)

	@OriginalMember(owner = "client!ha", name = "aa", descriptor = "(IIIIII)V")
	abstract fun aa(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) mode: Int)

	@OriginalMember(owner = "client!ha", name = "GA", descriptor = "(I)V")
	abstract fun GA(@OriginalArg(0) colour: Int)

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!wh;Lclient!wh;FLclient!wh;)Lclient!wh;")
	abstract fun drawQuad(@OriginalArg(0) sourceA: EnvironmentSampler?, @OriginalArg(1) sourceB: EnvironmentSampler?, @OriginalArg(2) alpha: Float, @OriginalArg(3) existing: EnvironmentSampler?): EnvironmentSampler?

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	@Throws(FlipException::class)
	abstract fun flipDirtyRect(@OriginalArg(0) rectangles: Array<Rectangle?>?, @OriginalArg(1) count: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int)

	/** Whether the scene may be drawn by several render worker threads at once; only the software toolkit allows it. */
	@OriginalMember(owner = "client!ha", name = "z", descriptor = "()Z")
	abstract fun method7311(): Boolean

	@OriginalMember(owner = "client!ha", name = "e", descriptor = "(IIIIII)V")
	abstract fun line(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) x2: Int, @OriginalArg(3) y2: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) mode: Int)

	@OriginalMember(owner = "client!ha", name = "A", descriptor = "()V")
	abstract fun stopBloom()

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "([IIIIIZ)Lclient!mi;")
	abstract fun createNativeSprite(@OriginalArg(0) pixels: IntArray?, @OriginalArg(2) rowLength: Int, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int, @OriginalArg(5) copyPixels: Boolean): NativeSprite?

	/**
	 * Not part of the original client: the plugins' drawing over the finished frame starts
	 * (PluginRuntime.drawFrame). The OpenGL toolkits put their 2D state right again here: a depth test the
	 * frame left on would hide the plugins' drawing wherever an interface was drawn.
	 */
	open fun beginOverlayFrame() {
	}

	/*
	 * Not part of the original client: hooks for a toolkit that keeps the scene on the GPU (634HD).
	 * The others ignore them.
	 */

	/** A region's scene is built (MapBuildStep.loadRegion, after shading). */
	open fun sceneBuilt() {
	}

	/** Whether the toolkit casts real shadows now: characters then leave out their flat shadow models. */
	open fun castsShadows(): Boolean {
		return false
	}

	/** A loc was added to, removed from or moved on tile ([x], [z]) of [level] after the build. */
	open fun sceneChanged(level: Int, x: Int, z: Int) {
	}

	/** SceneDrawer has drawn the opaque entities of a pass, before the ground. */
	open fun opaqueEntitiesDone() {
	}

	/** SceneDrawer has drawn the transparent entities of a pass. */
	open fun transparentEntitiesDone() {
	}
}
