import com.jagex.game.runetek5.DepthBuffer
import com.jagex.game.runetek5.EnvironmentSampler
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.JavaDepthBuffer
import com.jagex.game.runetek5.JavaMatrix
import com.jagex.game.runetek5.cache.node.JavaSurface
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.graphics.model.particle.ParticleNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask
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
import com.jagex.game.runetek5.graphics.texture.SafeModeTexture2D
import awt.Canvas
import awt.Dimension
import awt.Graphics
import awt.Rectangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.model.ModelSM
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import lang.Runnable
import lang.Thread
import withLock
import awt.height
import awt.width
import awt.x
import awt.y

@OriginalClass("client!ao")
class JavaToolkit : Toolkit {

	@OriginalMember(owner = "client!ao", name = "x", descriptor = "I")
	private var width: Int = 0

	@OriginalMember(owner = "client!ao", name = "y", descriptor = "I")
	private var lastTickTime: Int = 0

	@OriginalMember(owner = "client!ao", name = "z", descriptor = "Lclient!eq;")
	private var surfaces: IterableHashTable? = null

	@OriginalMember(owner = "client!ao", name = "A", descriptor = "Ljava/awt/Canvas;")
	private var canvas: Canvas? = null

	@OriginalMember(owner = "client!ao", name = "B", descriptor = "Lclient!pi;")
	@JvmField
	var canvasSurface: JavaSurface? = null

	@OriginalMember(owner = "client!ao", name = "C", descriptor = "Z")
	private var stopped: Boolean = false

	@OriginalMember(owner = "client!ao", name = "D", descriptor = "Z")
	private var colourTables: Boolean = false

	@OriginalMember(owner = "client!ao", name = "E", descriptor = "I")
	private var height: Int = 0

	@OriginalMember(owner = "client!ao", name = "F", descriptor = "I")
	@JvmField
	var sunY: Int = 0

	@OriginalMember(owner = "client!ao", name = "G", descriptor = "I")
	@JvmField
	var sunIntensity: Int = 0

	@OriginalMember(owner = "client!ao", name = "H", descriptor = "Lclient!it;")
	private var offscreenSurface: JavaOffscreenSurface? = null

	@OriginalMember(owner = "client!ao", name = "I", descriptor = "I")
	@JvmField
	var clipY1: Int = 0

	@OriginalMember(owner = "client!ao", name = "J", descriptor = "I")
	@JvmField
	var surfaceWidth: Int = 0

	@OriginalMember(owner = "client!ao", name = "K", descriptor = "I")
	@JvmField
	var reverseSunIntensity: Int = 0

	@OriginalMember(owner = "client!ao", name = "L", descriptor = "I")
	@JvmField
	var sunZ: Int = 0

	@OriginalMember(owner = "client!ao", name = "M", descriptor = "[Lclient!pga;")
	private var resources: Array<JavaThreadResource?>? = null

	@OriginalMember(owner = "client!ao", name = "N", descriptor = "I")
	private var mainSurfaceWidth: Int = 0

	@OriginalMember(owner = "client!ao", name = "O", descriptor = "I")
	@JvmField
	var zNear: Int = 0

	@OriginalMember(owner = "client!ao", name = "P", descriptor = "[I")
	@JvmField
	var surfaceRaster: IntArray? = null

	@OriginalMember(owner = "client!ao", name = "R", descriptor = "I")
	@JvmField
	var sunX: Int = 0

	@OriginalMember(owner = "client!ao", name = "S", descriptor = "I")
	@JvmField
	var threadCount: Int = 0

	@OriginalMember(owner = "client!ao", name = "T", descriptor = "I")
	private var surfaceHeight: Int = 0

	@OriginalMember(owner = "client!ao", name = "U", descriptor = "I")
	private var count: Int = 0

	@OriginalMember(owner = "client!ao", name = "V", descriptor = "I")
	private var depthHeight: Int = 0

	@OriginalMember(owner = "client!ao", name = "W", descriptor = "Z")
	private var shrinkTextures: Boolean = false

	@OriginalMember(owner = "client!ao", name = "X", descriptor = "I")
	@JvmField
	var viewY1: Int = 0

	@OriginalMember(owner = "client!ao", name = "Y", descriptor = "I")
	@JvmField
	var projectionScaleX: Int = 0

	@OriginalMember(owner = "client!ao", name = "Z", descriptor = "Lclient!gq;")
	@JvmField
	var camera: JavaMatrix? = null

	@OriginalMember(owner = "client!ao", name = "ab", descriptor = "I")
	private var mainSurfaceHeight: Int = 0

	@OriginalMember(owner = "client!ao", name = "bb", descriptor = "I")
	@JvmField
	var zFar: Int = 0

	@OriginalMember(owner = "client!ao", name = "cb", descriptor = "I")
	private var depthWidth: Int = 0

	@OriginalMember(owner = "client!ao", name = "db", descriptor = "I")
	@JvmField
	var clipX1: Int = 0

	@OriginalMember(owner = "client!ao", name = "eb", descriptor = "I")
	@JvmField
	var projectionScaleY: Int = 0

	@OriginalMember(owner = "client!ao", name = "fb", descriptor = "Lclient!jr;")
	private val textureCache: LRUCache

	@OriginalMember(owner = "client!ao", name = "gb", descriptor = "Lclient!jr;")
	private val spriteCache: LRUCache

	@OriginalMember(owner = "client!ao", name = "hb", descriptor = "I")
	@JvmField
	var ambient: Int = 0

	@OriginalMember(owner = "client!ao", name = "ib", descriptor = "I")
	@JvmField
	var textureSize: Int = 0

	@OriginalMember(owner = "client!ao", name = "jb", descriptor = "[F")
	@JvmField
	var mainDepthBuffer: FloatArray? = null

	@OriginalMember(owner = "client!ao", name = "kb", descriptor = "I")
	@JvmField
	var clipY2: Int = 0

	@OriginalMember(owner = "client!ao", name = "lb", descriptor = "I")
	@JvmField
	var lb: Int = 0

	@OriginalMember(owner = "client!ao", name = "mb", descriptor = "I")
	private var modelCount: Int = 0

	@OriginalMember(owner = "client!ao", name = "nb", descriptor = "I")
	@JvmField
	var viewY2: Int = 0

	@OriginalMember(owner = "client!ao", name = "ob", descriptor = "I")
	@JvmField
	var clipX2: Int = 0

	@OriginalMember(owner = "client!ao", name = "pb", descriptor = "I")
	@JvmField
	var viewX2: Int = 0

	@OriginalMember(owner = "client!ao", name = "qb", descriptor = "I")
	@JvmField
	var viewX1: Int = 0

	@OriginalMember(owner = "client!ao", name = "rb", descriptor = "I")
	@JvmField
	var projectionCenterX: Int = 0

	@OriginalMember(owner = "client!ao", name = "sb", descriptor = "[F")
	@JvmField
	var depthBuffer: FloatArray? = null

	@OriginalMember(owner = "client!ao", name = "tb", descriptor = "I")
	private var textureId: Int = 0

	@OriginalMember(owner = "client!ao", name = "ub", descriptor = "Lclient!mi;")
	private var cachedBillboardSprite: NativeSprite? = null

	@OriginalMember(owner = "client!ao", name = "<init>", descriptor = "(Ljava/awt/Canvas;Lclient!d;II)V")
	constructor(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) textureSource: MaterialProvider?, @OriginalArg(2) screenWidth: Int, @OriginalArg(3) screenHeight: Int) : this(textureSource) {
		try {
			this.addCanvas(canvas, screenWidth, screenHeight)
			this.setCanvas(canvas)
		} catch (@Pc(12) ex: Throwable) {
			ex.printStackTrace()
			this.free()
			throw RuntimeException("")
		}
	}

	@OriginalMember(owner = "client!ao", name = "<init>", descriptor = "(Lclient!d;)V")
	private constructor(@OriginalArg(0) textureSource: MaterialProvider?) : super(textureSource) {
		this.stopped = false
		this.colourTables = false
		this.surfaces = IterableHashTable(4)
		this.sunIntensity = 45823
		this.shrinkTextures = false
		this.count = 0
		this.textureSize = 128
		this.clipY1 = 0
		this.zNear = 50
		this.clipY2 = 0
		this.clipX1 = 0
		this.projectionScaleY = 512
		this.ambient = 75518
		this.projectionScaleX = 512
		this.modelCount = 0
		this.zFar = 3500
		this.clipX2 = 0
		this.reverseSunIntensity = 78642
		this.spriteCache = LRUCache(16)
		this.textureId = -1
		try {
			this.textureCache = LRUCache(256)
			this.camera = JavaMatrix()
			this.allocateThreads(1)
			this.linkThreads(0)
			ColorUtil.initColorTables(true, true)
			this.colourTables = true
			this.lastTickTime = Utils.currentTimeMillis().toInt()
		} catch (@Pc(99) ex: Throwable) {
			ex.printStackTrace()
			this.free()
			throw RuntimeException("")
		}
	}

	@OriginalMember(owner = "client!ao", name = "u", descriptor = "()Z")
	override fun forceFixedScreenmode(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!ao", name = "GA", descriptor = "(I)V")
	override fun GA(@OriginalArg(0) colour: Int) {
		this.aa(0, 0, this.surfaceWidth, this.surfaceHeight, colour, 0)
	}

	@OriginalMember(owner = "client!ao", name = "na", descriptor = "(IIII)[I")
	override fun na(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int): IntArray? {
		@Pc(4) val pixels: IntArray = IntArray(width * height)
		@Pc(6) var index: Int = 0
		@Pc(8) var i: Int = 0
		while (i < height) {
			@Pc(18) val rowOffset: Int = (y + i) * this.surfaceWidth + x
			@Pc(20) var j: Int = 0
			while (j < width) {
				pixels[index++] = this.surfaceRaster!![rowOffset + j]
				j++
			}
			i++
		}
		return pixels
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIII)V")
	override fun outlineRect(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) mode: Int) {
		this.U(x, y, width, colour, mode)
		this.U(x, y + height - 1, width, colour, mode)
		this.P(x, y + 1, height - 2, colour, mode)
		this.P(x + width - 1, y + 1, height - 2, colour, mode)
	}

	@OriginalMember(owner = "client!ao", name = "pa", descriptor = "()V")
	override fun pa() {
		@Pc(1) var i: Int = 0
		while (i < this.resources!!.size) {
			this.resources!![i]!!.fogColour = this.resources!![i]!!.savedFogColour
			this.resources!![i]!!.water = false
			i++
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIILclient!aa;IIIII)V")
	override fun drawTexturedLine(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) colour: Int, @OriginalArg(6) clippingMask: ComponentSurface?, @OriginalArg(7) surfaceClipLeft: Int, @OriginalArg(8) surfaceClipTop: Int, @OriginalArg(9) dashLength: Int, @OriginalArg(10) gapLength: Int, @OriginalArg(11) dashOffset: Int) {
		var startX: Int = startX
		var startY: Int = startY
		var endX: Int = endX
		var endY: Int = endY
		var dashOffset: Int = dashOffset
		@Pc(2) val javaClippingMask: JavaClippingMask = clippingMask as JavaClippingMask
		@Pc(5) val lineWidths: IntArray = javaClippingMask.lineWidths!!
		@Pc(8) val lineOffsets: IntArray = javaClippingMask.lineOffsets!!
		@Pc(17) val clipTop: Int = if (this.clipY1 > surfaceClipTop) this.clipY1 else surfaceClipTop
		@Pc(32) val clipBottom: Int = if (this.clipY2 < surfaceClipTop + lineWidths.size) this.clipY2 else surfaceClipTop + lineWidths.size
		@Pc(36) var rawDashOffset: Int = dashOffset shl 8
		@Pc(40) val scaledDash: Int = dashLength shl 8
		@Pc(44) val scaledGap: Int = gapLength shl 8
		@Pc(48) val dashCycle: Int = scaledDash + scaledGap
		dashOffset = rawDashOffset % dashCycle
		endX -= startX
		endY -= startY
		@Pc(77) var local77: Int
		@Pc(81) var local81: Int
		if (endX + endY < 0) {
			val lineLength: Int = (kotlin.math.sqrt((endX * endX + endY * endY).toDouble()) * 256.0).toInt()
			val lengthInCycle: Int = lineLength % dashCycle
			rawDashOffset = dashCycle + scaledDash - dashOffset - lengthInCycle
			dashOffset = rawDashOffset % dashCycle
			if (dashOffset < 0) {
				dashOffset += dashCycle
			}
			startX += endX
			endX = -endX
			startY += endY
			endY = -endY
		}
		@Pc(159) var local159: Int
		@Pc(172) var local172: Int
		@Pc(176) var local176: Int
		@Pc(199) var local199: Int
		@Pc(299) var local299: Int
		@Pc(316) var local316: Int
		@Pc(142) var local142: Int
		@Pc(128) var local128: Int
		@Pc(263) var premultipliedColour: Int
		if (endX <= endY) {
			startX = startX shl 0x10
			startX += 32768
			val deltaFixed: Int = endX shl 16
			val step: Int = kotlin.math.floor(deltaFixed.toDouble() / endY.toDouble() + 0.5).toInt()
			val dashStep: Int = kotlin.math.sqrt(((step shr 8) * (step shr 8) + 65536).toDouble()).toInt()
			val lastY: Int = endY + startY
			val alpha: Int = colour ushr 24
			if (alpha == 255) {
				while (startY <= lastY) {
					val pixelX: Int = startX shr 16
					val maskRow: Int = startY - surfaceClipTop
					if (startY >= clipTop && startY < clipBottom && pixelX >= this.clipX1 && pixelX < this.clipX2 && dashOffset < scaledDash && pixelX >= surfaceClipLeft + lineWidths[maskRow] && pixelX < surfaceClipLeft + lineWidths[maskRow] + lineOffsets[maskRow]) {
						this.surfaceRaster!![pixelX + startY * this.surfaceWidth] = colour
					}
					startX += step
					startY++
					rawDashOffset = dashOffset + dashStep
					dashOffset = rawDashOffset % dashCycle
				}
			} else {
				premultipliedColour = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
				val inverseAlpha: Int = 256 - alpha
				while (startY <= lastY) {
					val pixelX: Int = startX shr 16
					val maskRow: Int = startY - surfaceClipTop
					if (startY >= clipTop && startY < clipBottom && pixelX >= this.clipX1 && pixelX < this.clipX2 && dashOffset < scaledDash && pixelX >= surfaceClipLeft + lineWidths[maskRow] && pixelX < surfaceClipLeft + lineWidths[maskRow] + lineOffsets[maskRow]) {
						val pixelIndex: Int = pixelX + startY * this.surfaceWidth
						val dest: Int = this.surfaceRaster!![pixelIndex]
						@Pc(707) val faded: Int = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
						this.surfaceRaster!![pixelX + startY * this.surfaceWidth] = premultipliedColour + faded
					}
					startX += step
					startY++
					rawDashOffset = dashOffset + dashStep
					dashOffset = rawDashOffset % dashCycle
				}
			}
			return
		}
		startY = startY shl 0x10
		startY += 32768
		val deltaFixed: Int = endY shl 16
		val step: Int = kotlin.math.floor(deltaFixed.toDouble() / endX.toDouble() + 0.5).toInt()
		val lastX: Int = endX + startX
		val alpha: Int = colour ushr 24
		val dashStep: Int = kotlin.math.sqrt(((step shr 8) * (step shr 8) + 65536).toDouble()).toInt()
		if (alpha == 255) {
			while (startX <= lastX) {
				val pixelY: Int = startY shr 16
				val maskRow: Int = pixelY - surfaceClipTop
				if (startX >= this.clipX1 && startX < this.clipX2 && pixelY >= clipTop && pixelY < clipBottom && dashOffset < scaledDash) {
					val maskStart: Int = surfaceClipLeft + lineWidths[maskRow]
					if (startX >= maskStart && startX < maskStart + lineOffsets[maskRow]) {
						this.surfaceRaster!![startX + pixelY * this.surfaceWidth] = colour
					}
				}
				startY += step
				startX++
				rawDashOffset = dashOffset + dashStep
				dashOffset = rawDashOffset % dashCycle
			}
			return
		}
		premultipliedColour = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
		val inverseAlpha: Int = 256 - alpha
		while (startX <= lastX) {
			val pixelY: Int = startY shr 16
			val maskRow: Int = pixelY - surfaceClipTop
			if (startX >= this.clipX1 && startX < this.clipX2 && pixelY >= clipTop && pixelY < clipBottom && dashOffset < scaledDash) {
				val maskStart: Int = surfaceClipLeft + lineWidths[maskRow]
				if (startX >= maskStart && startX < maskStart + lineOffsets[maskRow]) {
					val pixelIndex: Int = startX + pixelY * this.surfaceWidth
					@Pc(321) val dest: Int = this.surfaceRaster!![pixelIndex]
					@Pc(341) val faded: Int = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
					this.surfaceRaster!![pixelIndex] = premultipliedColour + faded
				}
			}
			startY += step
			startX++
			rawDashOffset = dashOffset + dashStep
			dashOffset = rawDashOffset % dashCycle
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(II[I[I)Lclient!aa;")
	override fun createMask(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int, @OriginalArg(2) offsets: IntArray?, @OriginalArg(3) widths: IntArray?): ComponentSurface? {
		return JavaClippingMask(width, height, offsets, widths)
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!mda;I)V")
	override fun renderOrtho(@OriginalArg(0) particleList: ParticleList?, @OriginalArg(1) zoom: Int) {
		@Pc(3) val resource: JavaThreadResource? = this.threadResource(lang.currentThread())
		@Pc(7) val sentinel: ParticleNode = particleList!!.particles.current
		@Pc(10) var node: ParticleNode? = sentinel.next2
		while (node !== sentinel) {
			@Pc(14) val particle: PointEntity = node as PointEntity
			@Pc(19) val x: Int = particle.x shr 12
			@Pc(24) val y: Int = particle.y shr 12
			@Pc(29) val z: Int = particle.z shr 12
			@Pc(54) var depth: Float = this.camera!!.tz + this.camera!!.e3_1 * x.toFloat() + this.camera!!.e3_2 * y.toFloat() + this.camera!!.e3_3 * z.toFloat()
			if (!(depth < this.zNear.toFloat()) && !(depth > resource!!.fogPlane.toFloat())) {
				@Pc(103) val px: Int = this.projectionCenterX + (this.projectionScaleX.toFloat() * (this.camera!!.tx + this.camera!!.e1_1 * x.toFloat() + this.camera!!.e1_2 * y.toFloat() + this.camera!!.e1_3 * z.toFloat()) / zoom.toFloat()).toInt()
				@Pc(139) val py: Int = this.lb + (this.projectionScaleY.toFloat() * (this.camera!!.ty + this.camera!!.e2_1 * x.toFloat() + this.camera!!.e2_2 * y.toFloat() + this.camera!!.e2_3 * z.toFloat()) / zoom.toFloat()).toInt()
				if (px >= this.clipX1 && px <= this.clipX2 && py >= this.clipY1 && py <= this.clipY2) {
					if (depth == 0.0f) {
						depth = 1.0f
					}
					this.drawParticle(particle, px, py, depth.toInt(), ((particle.size * this.projectionScaleX) shr 12) / zoom)
				}
			}
			node = node.next2
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(I[Lclient!ab;)V")
	override fun copyLights(@OriginalArg(0) count: Int, @OriginalArg(1) pointLights: Array<PointLight?>?) {
	}

	@OriginalMember(owner = "client!ao", name = "s", descriptor = "()Z")
	override fun utilisesNativeHeap(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!ao", name = "A", descriptor = "()V")
	override fun stopBloom() {
	}

	@OriginalMember(owner = "client!ao", name = "P", descriptor = "(IIIII)V")
	override fun P(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) height: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) mode: Int) {
		var y: Int = y
		var height: Int = height
		if (x < this.clipX1 || x >= this.clipX2) {
			return
		}
		if (y < this.clipY1) {
			height -= this.clipY1 - y
			y = this.clipY1
		}
		if (y + height > this.clipY2) {
			height = this.clipY2 - y
		}
		@Pc(40) val pixelIndex: Int = x + y * this.surfaceWidth
		@Pc(44) val alpha: Int = colour ushr 24
		@Pc(54) var local54: Int
		if (mode == 0 || mode == 1 && alpha == 255) {
			var row: Int = 0
			while (row < height) {
				this.surfaceRaster!![pixelIndex + row * this.surfaceWidth] = colour
				row++
			}
			return
		}
		@Pc(103) var local103: Int
		@Pc(111) var local111: Int
		@Pc(116) var local116: Int
		if (mode == 1) {
			@Pc(97) val premultipliedColour: Int = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
			val inverseAlpha: Int = 256 - alpha
			var row: Int = 0
			while (row < height) {
				val dstIndex: Int = pixelIndex + row * this.surfaceWidth
				var dstColour: Int = this.surfaceRaster!![dstIndex]
				dstColour = ((((dstColour and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dstColour and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
				this.surfaceRaster!![dstIndex] = premultipliedColour + dstColour
				row++
			}
		} else if (mode == 2) {
			var row: Int = 0
			while (row < height) {
				val dstIndex: Int = pixelIndex + row * this.surfaceWidth
				val dstColour: Int = this.surfaceRaster!![dstIndex]
				val sum: Int = colour + dstColour
				@Pc(178) val redBlueSum: Int = (colour and 0xFF00FF) + (dstColour and 0xFF00FF)
				@Pc(188) val overflow: Int = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
				this.surfaceRaster!![dstIndex] = (sum - overflow) or (overflow - (overflow ushr 8))
				row++
			}
		} else {
			throw IllegalArgumentException()
		}
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(Z)V")
	override fun method7250(@OriginalArg(0) enabled: Boolean) {
	}

	@OriginalMember(owner = "client!ao", name = "xa", descriptor = "(F)V")
	override fun xa(@OriginalArg(0) ambient: Float) {
		this.ambient = (ambient * 65535.0f).toInt()
	}

	@OriginalMember(owner = "client!ao", name = "ra", descriptor = "(IIII)V")
	override fun ra(@OriginalArg(0) waterHeight: Int, @OriginalArg(1) fogColour: Int, @OriginalArg(2) waterDepth: Int, @OriginalArg(3) waterIntensity: Int) {
		@Pc(1) var i: Int = 0
		while (i < this.resources!!.size) {
			this.resources!![i]!!.savedFogColour = this.resources!![i]!!.fogColour
			this.resources!![i]!!.waterHeight = waterHeight
			this.resources!![i]!!.fogColour = fogColour
			this.resources!![i]!!.waterDepth = waterDepth
			this.resources!![i]!!.water = true
			i++
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!za;)V")
	override fun setHeap(@OriginalArg(0) memoryPool: MemoryPool?) {
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!jc;IIII)V")
	private fun drawParticle(@OriginalArg(0) particle: PointEntity?, @OriginalArg(1) screenX: Int, @OriginalArg(2) screenY: Int, @OriginalArg(3) depth: Int, @OriginalArg(4) size: Int) {
		@Pc(2) val materialId: Int = particle!!.materialId
		@Pc(8) var diameter: Int = size shl 1
		if (materialId == -1) {
			this.fillDepthTestedCircle(screenX, screenY, depth, size, particle.colorArgb, 1)
			return
		}
		if (this.textureId != materialId) {
			@Pc(33) var sprite: NativeSprite? = this.spriteCache.get(materialId.toLong()) as NativeSprite?
			if (sprite == null) {
				@Pc(39) val pixels: IntArray? = this.getArgbTexture(materialId)
				if (pixels == null) {
					return
				}
				@Pc(50) val spriteSize: Int = if (this.smallTexture(materialId)) 64 else this.textureSize
				sprite = this.createNativeSprite(spriteSize, pixels, spriteSize, spriteSize)
				this.spriteCache.put(sprite, materialId.toLong())
			}
			this.textureId = materialId
			this.cachedBillboardSprite = sprite
		}
		diameter++
		(this.cachedBillboardSprite as SpriteSM).renderDepthTestedRgb(screenX - size, screenY - size, depth, diameter, diameter, 0, particle.colorArgb, 1)
	}

	@OriginalMember(owner = "client!ao", name = "g", descriptor = "()V")
	private fun updateViewport() {
		this.viewX1 = this.clipX1 - this.projectionCenterX
		this.viewX2 = this.clipX2 - this.projectionCenterX
		this.viewY1 = this.clipY1 - this.lb
		this.viewY2 = this.clipY2 - this.lb
		@Pc(29) var i: Int = 0
		while (i < this.threadCount) {
			@Pc(36) val rasterizer: Rasterizer = this.resources!![i]!!.rasterizer!!
			rasterizer.minX = this.projectionCenterX - this.clipX1
			rasterizer.minY = this.lb - this.clipY1
			rasterizer.width = this.clipX2 - this.clipX1
			rasterizer.height = this.clipY2 - this.clipY1
			i++
		}
		@Pc(78) var pixel: Int = this.clipY1 * this.surfaceWidth + this.clipX1
		@Pc(81) var y: Int = this.clipY1
		while (y < this.clipY2) {
			@Pc(84) var j: Int = 0
			while (j < this.threadCount) {
				this.resources!![j]!!.rasterizer!!.lineOffsets!![y - this.clipY1] = pixel
				j++
			}
			pixel += this.surfaceWidth
			y++
		}
	}

	@OriginalMember(owner = "client!ao", name = "i", descriptor = "()I")
	override fun i(): Int {
		return this.zNear
	}

	@OriginalMember(owner = "client!ao", name = "y", descriptor = "()Lclient!bca;")
	override fun scratchMatrix(): Matrix? {
		@Pc(3) val resource: JavaThreadResource? = this.threadResource(lang.currentThread())
		return resource!!.scratchMatrix
	}

	@OriginalMember(owner = "client!ao", name = "d", descriptor = "(I)Z")
	fun textureRepeats(@OriginalArg(0) id: Int): Boolean {
		return super.materialProvider!!.getMetrics(id)!!.repeatS || super.materialProvider!!.getMetrics(id)!!.repeatT
	}

	@OriginalMember(owner = "client!ao", name = "Q", descriptor = "(IIIIII[BII)V")
	override fun Q(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) backgroundColour: Int, @OriginalArg(5) foregroundColour: Int, @OriginalArg(6) mask: ByteArray?, @OriginalArg(7) maskWidth: Int, @OriginalArg(8) mode: Int) {
		var width: Int = width
		var height: Int = height
		if (width <= 0 || height <= 0) {
			return
		}
		@Pc(6) var maskX: Int = 0
		@Pc(8) var maskY: Int = 0
		@Pc(14) val stepX: Int = (maskWidth shl 16) / width
		@Pc(23) val stepY: Int = ((mask!!.size / maskWidth) shl 16) / height
		@Pc(30) var pixelIndex: Int = x + y * this.surfaceWidth
		@Pc(35) var rowSkip: Int = this.surfaceWidth - width
		if (y + height > this.clipY2) {
			height -= y + height - this.clipY2
		}
		@Pc(59) var local59: Int
		if (y < this.clipY1) {
			val clipAmount: Int = this.clipY1 - y
			height -= clipAmount
			pixelIndex += clipAmount * this.surfaceWidth
			maskY += stepY * clipAmount
		}
		if (x + width > this.clipX2) {
			val clipAmount: Int = x + width - this.clipX2
			width -= clipAmount
			rowSkip += clipAmount
		}
		if (x < this.clipX1) {
			val clipAmount: Int = this.clipX1 - x
			width -= clipAmount
			pixelIndex += clipAmount
			maskX += stepX * clipAmount
			rowSkip += clipAmount
		}
		val backgroundAlpha: Int = backgroundColour ushr 24
		@Pc(132) val foregroundAlpha: Int = foregroundColour ushr 24
		@Pc(145) var startMaskX: Int
		@Pc(148) var row: Int
		@Pc(155) var maskRow: Int
		@Pc(158) var column: Int
		if (mode == 0 || mode == 1 && backgroundAlpha == 255 && foregroundAlpha == 255) {
			startMaskX = maskX
			row = -height
			while (row < 0) {
				maskRow = (maskY shr 16) * maskWidth
				column = -width
				while (column < 0) {
					if (mask[(maskX shr 16) + maskRow].toInt() == 0) {
						this.surfaceRaster!![pixelIndex++] = backgroundColour
					} else {
						this.surfaceRaster!![pixelIndex++] = foregroundColour
					}
					maskX += stepX
					column++
				}
				maskY += stepY
				maskX = startMaskX
				pixelIndex += rowSkip
				row++
			}
			return
		}
		@Pc(222) var colour: Int
		@Pc(236) var local236: Int
		@Pc(240) var local240: Int
		@Pc(245) var local245: Int
		if (mode == 1) {
			startMaskX = maskX
			row = -height
			while (row < 0) {
				maskRow = (maskY shr 16) * maskWidth
				column = -width
				while (column < 0) {
					colour = backgroundColour
					if (mask[(maskX shr 16) + maskRow].toInt() != 0) {
						colour = foregroundColour
					}
					val alpha: Int = colour ushr 24
					val inverseAlpha: Int = 255 - alpha
					val dstColour: Int = this.surfaceRaster!![pixelIndex]
					this.surfaceRaster!![pixelIndex++] = ((((colour and 0xFF00FF) * alpha + (dstColour and 0xFF00FF) * inverseAlpha) and 0xFF00FF00.toInt()) + (((colour and 0xFF00) * alpha + (dstColour and 0xFF00) * inverseAlpha) and 0xFF0000)) shr 8
					maskX += stepX
					column++
				}
				maskY += stepY
				maskX = startMaskX
				pixelIndex += rowSkip
				row++
			}
		} else if (mode == 2) {
			startMaskX = maskX
			row = -height
			while (row < 0) {
				maskRow = (maskY shr 16) * maskWidth
				column = -width
				while (column < 0) {
					colour = backgroundColour
					if (mask[(maskX shr 16) + maskRow].toInt() != 0) {
						colour = foregroundColour
					}
					if (colour == 0) {
						pixelIndex++
					} else {
						val dstColour: Int = this.surfaceRaster!![pixelIndex]
						val sum: Int = colour + dstColour
						val redBlueSum: Int = (colour and 0xFF00FF) + (dstColour and 0xFF00FF)
						@Pc(360) val overflow: Int = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
						this.surfaceRaster!![pixelIndex++] = (sum - overflow) or (overflow - (overflow ushr 8))
					}
					maskX += stepX
					column++
				}
				maskY += stepY
				maskX = startMaskX
				pixelIndex += rowSkip
				row++
			}
		} else {
			throw IllegalArgumentException()
		}
	}

	@OriginalMember(owner = "client!ao", name = "k", descriptor = "()Z")
	override fun supportsFullscreen(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!ao", name = "d", descriptor = "(IIIIII)Lclient!wh;")
	override fun createCubeTexture(@OriginalArg(0) materialPositiveX: Int, @OriginalArg(1) materialNegativeX: Int, @OriginalArg(2) materialPositiveY: Int, @OriginalArg(3) materialNegativeY: Int, @OriginalArg(4) materialPositiveZ: Int, @OriginalArg(5) materialNegativeZ: Int): EnvironmentSampler? {
		return null
	}

	@OriginalMember(owner = "client!ao", name = "KA", descriptor = "(IIII)V")
	override fun KA(@OriginalArg(0) left: Int, @OriginalArg(1) top: Int, @OriginalArg(2) right: Int, @OriginalArg(3) bottom: Int) {
		var left: Int = left
		var top: Int = top
		var right: Int = right
		var bottom: Int = bottom
		if (left < 0) {
			left = 0
		}
		if (top < 0) {
			top = 0
		}
		if (right > this.surfaceWidth) {
			right = this.surfaceWidth
		}
		if (bottom > this.surfaceHeight) {
			bottom = this.surfaceHeight
		}
		this.clipX1 = left
		this.clipX2 = right
		this.clipY1 = top
		this.clipY2 = bottom
		this.updateViewport()
	}

	@OriginalMember(owner = "client!ao", name = "v", descriptor = "()V")
	override fun compositeBloomEffects() {
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!bca;)V")
	override fun setCamera(@OriginalArg(0) matrix: Matrix?) {
		this.camera = matrix as JavaMatrix?
	}

	@OriginalMember(owner = "client!ao", name = "A", descriptor = "(ILclient!aa;II)V")
	override fun A(@OriginalArg(0) colour: Int, @OriginalArg(1) clippingMask: ComponentSurface?, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int) {
		var y: Int = y
		@Pc(2) val javaClippingMask: JavaClippingMask = clippingMask as JavaClippingMask
		@Pc(5) val lineWidths: IntArray = javaClippingMask.lineWidths!!
		@Pc(8) val lineOffsets: IntArray = javaClippingMask.lineOffsets!!
		@Pc(20) val endRow: Int
		if (this.clipY2 < y + lineWidths.size) {
			endRow = this.clipY2 - y
		} else {
			endRow = lineWidths.size
		}
		@Pc(33) val startRow: Int
		if (this.clipY1 > y) {
			startRow = this.clipY1 - y
			y = this.clipY1
		} else {
			startRow = 0
		}
		if (endRow - startRow <= 0) {
			return
		}
		@Pc(49) var rowOffset: Int = y * this.surfaceWidth
		@Pc(51) var i: Int = startRow
		while (i < endRow) {
			@Pc(58) var spanOffset: Int = x + lineWidths[i]
			@Pc(62) var length: Int = lineOffsets[i]
			if (this.clipX1 > spanOffset) {
				length -= this.clipX1 - spanOffset
				spanOffset = this.clipX1
			}
			if (this.clipX2 < spanOffset + length) {
				length = this.clipX2 - spanOffset
			}
			spanOffset += rowOffset
			@Pc(94) var j: Int = -length
			while (j < 0) {
				this.surfaceRaster!![spanOffset++] = colour
				j++
			}
			rowOffset += this.surfaceWidth
			i++
		}
	}

	@OriginalMember(owner = "client!ao", name = "e", descriptor = "(II)I")
	override fun compareFunctionMasks(@OriginalArg(0) maskA: Int, @OriginalArg(1) maskB: Int): Int {
		@Pc(3) val combined: Int = maskA or 0x20800
		return (combined and maskB) xor maskB
	}

	@OriginalMember(owner = "client!ao", name = "f", descriptor = "(II)V")
	override fun f(@OriginalArg(0) zNear: Int, @OriginalArg(1) zFar: Int) {
		@Pc(3) val javaThreadResource: JavaThreadResource? = this.threadResource(lang.currentThread())
		this.zNear = zNear
		this.zFar = zFar
		javaThreadResource!!.fogPlane = this.zFar - 255
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIIII)V")
	private fun horizontalDashedLine(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) length: Int, @OriginalArg(3) colour: Int, @OriginalArg(5) dashLength: Int, @OriginalArg(6) gapLength: Int, @OriginalArg(7) phase: Int) {
		if (y < this.clipY1 || y >= this.clipY2) {
			return
		}
		@Pc(15) val pixel: Int = x + y * this.surfaceWidth
		@Pc(19) val alpha: Int = colour ushr 24
		@Pc(23) val period: Int = dashLength + gapLength
		@Pc(27) var offset: Int = phase % period
		@Pc(37) var local37: Int
		if (alpha == 255) {
			var column: Int = 0
			while (column < length) {
				if (x + column >= this.clipX1 && x + column < this.clipX2 && offset < dashLength) {
					this.surfaceRaster!![pixel + column] = colour
				}
				column++
				offset++
				offset %= period
			}
			return
		}
		@Pc(97) val blended: Int = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
		val inverseAlpha: Int = 256 - alpha
		@Pc(103) var i: Int = 0
		while (i < length) {
			if (x + i >= this.clipX1 && x + i < this.clipX2 && offset < dashLength) {
				@Pc(126) val dest: Int = this.surfaceRaster!![pixel + i]
				@Pc(146) val faded: Int = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
				this.surfaceRaster!![pixel + i] = blended + faded
			}
			i++
			offset++
			offset %= period
		}
	}

	@OriginalMember(owner = "client!ao", name = "Y", descriptor = "()[I")
	override fun Y(): IntArray? {
		return intArrayOf(this.projectionCenterX, this.lb, this.projectionScaleX, this.projectionScaleY)
	}

	@OriginalMember(owner = "client!ao", name = "ya", descriptor = "()V")
	override fun ya() {
		@Pc(19) var local19: Int
		@Pc(25) var local25: Int
		@Pc(27) var local27: Int
		if (this.clipX1 == 0 && this.clipX2 == this.surfaceWidth && this.clipY1 == 0 && this.clipY2 == this.surfaceHeight) {
			val depthLength: Int = this.depthBuffer!!.size
			val unrolledLength: Int = depthLength - (depthLength and 0x7)
			var depthIndex: Int = 0
			while (depthIndex < unrolledLength) {
				this.depthBuffer!![depthIndex++] = 2.14748365E9f
				this.depthBuffer!![depthIndex++] = 2.14748365E9f
				this.depthBuffer!![depthIndex++] = 2.14748365E9f
				this.depthBuffer!![depthIndex++] = 2.14748365E9f
				this.depthBuffer!![depthIndex++] = 2.14748365E9f
				this.depthBuffer!![depthIndex++] = 2.14748365E9f
				this.depthBuffer!![depthIndex++] = 2.14748365E9f
				this.depthBuffer!![depthIndex++] = 2.14748365E9f
			}
			while (depthIndex < depthLength) {
				this.depthBuffer!![depthIndex++] = 2.14748365E9f
			}
			return
		}
		var clipWidth: Int = this.clipX2 - this.clipX1
		val clipHeight: Int = this.clipY2 - this.clipY1
		val rowSkip: Int = this.surfaceWidth - clipWidth
		@Pc(116) val startPixel: Int = this.clipX1 + this.clipY1 * this.surfaceWidth
		@Pc(120) val blockCount: Int = clipWidth shr 3
		@Pc(124) val remainder: Int = clipWidth and 0x7
		clipWidth = startPixel - 1
		@Pc(131) var i: Int = -clipHeight
		while (i < 0) {
			@Pc(136) var remaining: Int
			if (blockCount > 0) {
				remaining = blockCount
				do {
					clipWidth++
					this.depthBuffer!![clipWidth] = 2.14748365E9f
					clipWidth++
					this.depthBuffer!![clipWidth] = 2.14748365E9f
					clipWidth++
					this.depthBuffer!![clipWidth] = 2.14748365E9f
					clipWidth++
					this.depthBuffer!![clipWidth] = 2.14748365E9f
					clipWidth++
					this.depthBuffer!![clipWidth] = 2.14748365E9f
					clipWidth++
					this.depthBuffer!![clipWidth] = 2.14748365E9f
					clipWidth++
					this.depthBuffer!![clipWidth] = 2.14748365E9f
					clipWidth++
					this.depthBuffer!![clipWidth] = 2.14748365E9f
					remaining--
				} while (remaining > 0)
			}
			if (remainder > 0) {
				remaining = remainder
				do {
					clipWidth++
					this.depthBuffer!![clipWidth] = 2.14748365E9f
					remaining--
				} while (remaining > 0)
			}
			clipWidth += rowSkip
			i++
		}
	}

	@OriginalMember(owner = "client!ao", name = "d", descriptor = "()Z")
	override fun stillwaterEffectLoaded(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!ao", name = "U", descriptor = "(IIIII)V")
	override fun U(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) mode: Int) {
		var x: Int = x
		var width: Int = width
		if (y < this.clipY1 || y >= this.clipY2) {
			return
		}
		if (x < this.clipX1) {
			width -= this.clipX1 - x
			x = this.clipX1
		}
		if (x + width > this.clipX2) {
			width = this.clipX2 - x
		}
		@Pc(40) val pixelIndex: Int = x + y * this.surfaceWidth
		@Pc(44) val alpha: Int = colour ushr 24
		@Pc(54) var local54: Int
		if (mode == 0 || mode == 1 && alpha == 255) {
			var column: Int = 0
			while (column < width) {
				this.surfaceRaster!![pixelIndex + column] = colour
				column++
			}
			return
		}
		@Pc(100) var local100: Int
		@Pc(108) var local108: Int
		if (mode == 1) {
			@Pc(94) val premultipliedColour: Int = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
			val inverseAlpha: Int = 256 - alpha
			var column: Int = 0
			while (column < width) {
				var dstColour: Int = this.surfaceRaster!![pixelIndex + column]
				dstColour = ((((dstColour and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dstColour and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
				this.surfaceRaster!![pixelIndex + column] = premultipliedColour + dstColour
				column++
			}
		} else if (mode == 2) {
			var column: Int = 0
			while (column < width) {
				val dstColour: Int = this.surfaceRaster!![pixelIndex + column]
				val sum: Int = colour + dstColour
				@Pc(167) val redBlueSum: Int = (colour and 0xFF00FF) + (dstColour and 0xFF00FF)
				@Pc(177) val overflow: Int = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
				this.surfaceRaster!![pixelIndex + column] = (sum - overflow) or (overflow - (overflow ushr 8))
				column++
			}
		} else {
			throw IllegalArgumentException()
		}
	}

	@OriginalMember(owner = "client!ao", name = "HA", descriptor = "(IIII[I)V")
	override fun HA(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) zoom: Int, @OriginalArg(4) screenCoords: IntArray?) {
		@Pc(24) val depth: Float = this.camera!!.tz + this.camera!!.e3_1 * x.toFloat() + this.camera!!.e3_2 * y.toFloat() + this.camera!!.e3_3 * z.toFloat()
		if (depth < this.zNear.toFloat() || depth > this.zFar.toFloat()) {
			screenCoords!![2] = -1
			screenCoords[1] = -1
			screenCoords[0] = -1
			return
		}
		@Pc(82) val screenX: Int = (this.projectionScaleX.toFloat() * (this.camera!!.tx + this.camera!!.e1_1 * x.toFloat() + this.camera!!.e1_2 * y.toFloat() + this.camera!!.e1_3 * z.toFloat()) / zoom.toFloat()).toInt()
		@Pc(115) val screenY: Int = (this.projectionScaleY.toFloat() * (this.camera!!.ty + this.camera!!.e2_1 * x.toFloat() + this.camera!!.e2_2 * y.toFloat() + this.camera!!.e2_3 * z.toFloat()) / zoom.toFloat()).toInt()
		if (screenX >= this.viewX1 && screenX <= this.viewX2 && screenY >= this.viewY1 && screenY <= this.viewY2) {
			screenCoords!![0] = screenX - this.viewX1
			screenCoords[1] = screenY - this.viewY1
			screenCoords[2] = depth.toInt()
		} else {
			screenCoords!![2] = -1
			screenCoords[1] = -1
			screenCoords[0] = -1
		}
	}

	@OriginalMember(owner = "client!ao", name = "B", descriptor = "()Z")
	fun stopped(): Boolean {
		return this.stopped
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIII)V")
	override fun method7261(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int) {
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!mda;)V")
	override fun render(@OriginalArg(0) particleList: ParticleList?) {
		@Pc(3) val javaThreadResource: JavaThreadResource? = this.threadResource(lang.currentThread())
		@Pc(7) val node2: ParticleNode = particleList!!.particles.current
		@Pc(10) var node22: ParticleNode? = node2.next2
		while (node22 !== node2) {
			@Pc(14) val particle: PointEntity = node22 as PointEntity
			@Pc(19) val x: Int = particle.x shr 12
			@Pc(24) val y: Int = particle.y shr 12
			@Pc(29) val z: Int = particle.z shr 12
			@Pc(54) var depth: Float = this.camera!!.tz + this.camera!!.e3_1 * x.toFloat() + this.camera!!.e3_2 * y.toFloat() + this.camera!!.e3_3 * z.toFloat()
			if (!(depth < this.zNear.toFloat()) && !(depth > javaThreadResource!!.fogPlane.toFloat())) {
				@Pc(102) val px: Int = this.projectionCenterX + (this.projectionScaleX.toFloat() * (this.camera!!.tx + this.camera!!.e1_1 * x.toFloat() + this.camera!!.e1_2 * y.toFloat() + this.camera!!.e1_3 * z.toFloat()) / depth).toInt()
				@Pc(137) val py: Int = this.lb + (this.projectionScaleY.toFloat() * (this.camera!!.ty + this.camera!!.e2_1 * x.toFloat() + this.camera!!.e2_2 * y.toFloat() + this.camera!!.e2_3 * z.toFloat()) / depth).toInt()
				if (px >= this.clipX1 && px <= this.clipX2 && py >= this.clipY1 && py <= this.clipY2) {
					if (depth == 0.0f) {
						depth = 1.0f
					}
					this.drawParticle(particle, px, py, depth.toInt(), (((particle.size * this.projectionScaleX) shr 12).toFloat() / depth).toInt())
				}
			}
			node22 = node22.next2
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(FFF)V")
	override fun setBloomParams(@OriginalArg(0) whitePoint: Float, @OriginalArg(1) bloom: Float, @OriginalArg(2) brightpass: Float) {
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(IIIIZ)Lclient!mi;")
	override fun createNativeSprite(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) hasAlpha: Boolean): NativeSprite? {
		@Pc(4) val pixels: IntArray = IntArray(width * height)
		@Pc(11) var srcIndex: Int = y * this.surfaceWidth + x
		@Pc(16) val srcSkip: Int = this.surfaceWidth - width
		@Pc(18) var i: Int = 0
		while (i < height) {
			@Pc(23) val rowOffset: Int = i * width
			@Pc(25) var j: Int = 0
			while (j < width) {
				pixels[rowOffset + j] = this.surfaceRaster!![srcIndex++]
				j++
			}
			srcIndex += srcSkip
			i++
		}
		if (hasAlpha) {
			return TransparentSpriteSM(this, pixels, width, height)
		} else {
			return OpaqueSpriteSM(this, pixels, width, height)
		}
	}

	@OriginalMember(owner = "client!ao", name = "C", descriptor = "()V")
	private fun reset() {
		@Pc(1) var i: Int = 0
		while (i < this.threadCount) {
			this.resources!![i]!!.resetRasterizer()
			i++
		}
		this.la()
	}

	@OriginalMember(owner = "client!ao", name = "r", descriptor = "()Z")
	override fun hardShadow(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!ao", name = "n", descriptor = "()Z")
	override fun extendBuildArea(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!ao", name = "DA", descriptor = "(IIII)V")
	override fun DA(@OriginalArg(0) projectionCenterX: Int, @OriginalArg(1) projectionCenterY: Int, @OriginalArg(2) projectionScaleX: Int, @OriginalArg(3) projectionScaleY: Int) {
		this.projectionCenterX = projectionCenterX
		this.lb = projectionCenterY
		this.projectionScaleX = projectionScaleX
		this.projectionScaleY = projectionScaleY
		this.updateViewport()
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(Ljava/awt/Canvas;)V")
	override fun releaseSurface(@OriginalArg(0) canvas2: Canvas?) {
		if (this.canvas === canvas2) {
			this.setCanvas(null)
		}
		@Pc(15) val surface: JavaSurface? = this.surfaces!!.get(canvas2!!.hashCode().toLong()) as JavaSurface?
		if (surface != null) {
			surface.unlink()
		}
	}

	@OriginalMember(owner = "client!ao", name = "L", descriptor = "(III)V")
	override fun L(@OriginalArg(0) colour: Int, @OriginalArg(1) distance: Int, @OriginalArg(2) offset: Int) {
		@Pc(1) var i: Int = 0
		while (i < this.resources!!.size) {
			@Pc(7) val javaThreadResource: JavaThreadResource = this.resources!![i]!!
			javaThreadResource.fogColour = colour and 0xFFFFFF
			@Pc(19) var red: Int = (javaThreadResource.fogColour ushr 16) and 0xFF
			if (red < 2) {
				red = 2
			}
			@Pc(31) var green: Int = (javaThreadResource.fogColour shr 8) and 0xFF
			if (green < 2) {
				green = 2
			}
			@Pc(41) var blue: Int = javaThreadResource.fogColour and 0xFF
			if (blue < 2) {
				blue = 2
			}
			javaThreadResource.fogColour = (red shl 16) or (green shl 8) or blue
			if (distance < 0) {
				javaThreadResource.fogActive = false
			} else {
				javaThreadResource.fogActive = true
			}
			i++
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "()Z")
	override fun supportsBloom(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Ljava/awt/Canvas;II)V")
	override fun addCanvas(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int) {
		@Pc(8) var surface: JavaSurface? = this.surfaces!!.get(canvas!!.hashCode().toLong()) as JavaSurface?
		if (surface == null) {
			surface = JavaSurfaceFactory.createSurface(height, canvas, width)
			this.surfaces!!.put(canvas.hashCode().toLong(), surface)
		} else if (surface.width != width || surface.height != height) {
			this.resizeCanvas(canvas, width, height)
		}
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(IIIID)V")
	override fun b(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) offset: Double) {
		@Pc(4) val rowSkip: Int = this.depthWidth - width
		@Pc(11) var index: Int = y * this.depthWidth + x
		@Pc(14) val depthBuffer: FloatArray = this.depthBuffer!!
		@Pc(16) var row: Int = 0
		while (row < height) {
			@Pc(19) var column: Int = 0
			while (column < width) {
				@Pc(24) val depth: Float = depthBuffer[index]
				if (depth != 2.14748365E9f) {
					depthBuffer[index] = (depth.toDouble() + offset).toFloat()
				}
				column++
				index++
			}
			row++
			index += rowSkip
		}
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "()V")
	override fun finish() {
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(I)V")
	override fun allocateThreads(@OriginalArg(0) threadCount: Int) {
		this.threadCount = threadCount
		this.resources = arrayOfNulls<JavaThreadResource>(this.threadCount)
		@Pc(9) var i: Int = 0
		while (i < this.threadCount) {
			this.resources!![i] = JavaThreadResource(this)
			i++
		}
	}

	@OriginalMember(owner = "client!ao", name = "e", descriptor = "(IIIIII)V")
	override fun line(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) x2: Int, @OriginalArg(3) y2: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) mode: Int) {
		var x1: Int = x1
		var y1: Int = y1
		var x2: Int = x2
		var y2: Int = y2
		x2 -= x1
		y2 -= y1
		if (y2 == 0) {
			if (x2 >= 0) {
				this.U(x1, y1, x2 + 1, colour, mode)
			} else {
				this.U(x1 + x2, y1, 1 - x2, colour, mode)
			}
		} else if (x2 != 0) {
			if (x2 + y2 < 0) {
				x1 += x2
				x2 = -x2
				y1 += y2
				y2 = -y2
			}
			@Pc(104) var step: Int
			@Pc(137) var alpha: Int
			@Pc(150) var local150: Int
			@Pc(213) var local213: Int
			@Pc(228) var local228: Int
			@Pc(233) var local233: Int
			@Pc(309) var redBlueSum: Int
			@Pc(204) var premultipliedColour: Int
			if (x2 > y2) {
				y1 = y1 shl 0x10
				y1 += 32768
				@Pc(94) val deltaFixed: Int = y2 shl 16
				step = kotlin.math.floor(deltaFixed.toDouble() / x2.toDouble() + 0.5).toInt()
				x2 += x1
				if (x1 < this.clipX1) {
					y1 += step * (this.clipX1 - x1)
					x1 = this.clipX1
				}
				if (x2 >= this.clipX2) {
					x2 = this.clipX2 - 1
				}
				alpha = colour ushr 24
				if (mode == 0 || mode == 1 && alpha == 255) {
					while (x1 <= x2) {
						val pixelY: Int = y1 shr 16
						if (pixelY >= this.clipY1 && pixelY < this.clipY2) {
							this.surfaceRaster!![x1 + pixelY * this.surfaceWidth] = colour
						}
						y1 += step
						x1++
					}
				} else if (mode == 1) {
					premultipliedColour = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
					val inverseAlpha: Int = 256 - alpha
					while (x1 <= x2) {
						val pixelY: Int = y1 shr 16
						if (pixelY >= this.clipY1 && pixelY < this.clipY2) {
							val pixelIndex: Int = x1 + pixelY * this.surfaceWidth
							var dest: Int = this.surfaceRaster!![pixelIndex]
							dest = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
							this.surfaceRaster!![pixelIndex] = premultipliedColour + dest
						}
						y1 += step
						x1++
					}
				} else if (mode == 2) {
					while (x1 <= x2) {
						val pixelY: Int = y1 shr 16
						if (pixelY >= this.clipY1 && pixelY < this.clipY2) {
							val pixelIndex: Int = x1 + pixelY * this.surfaceWidth
							var dest: Int = this.surfaceRaster!![pixelIndex]
							val sum: Int = colour + dest
							redBlueSum = (colour and 0xFF00FF) + (dest and 0xFF00FF)
							dest = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
							this.surfaceRaster!![pixelIndex] = (sum - dest) or (dest - (dest ushr 8))
						}
						y1 += step
						x1++
					}
				} else {
					throw IllegalArgumentException()
				}
			} else {
				x1 = x1 shl 0x10
				x1 += 32768
				@Pc(357) val deltaFixed: Int = x2 shl 16
				step = kotlin.math.floor(deltaFixed.toDouble() / y2.toDouble() + 0.5).toInt()
				y2 += y1
				if (y1 < this.clipY1) {
					x1 += step * (this.clipY1 - y1)
					y1 = this.clipY1
				}
				if (y2 >= this.clipY2) {
					y2 = this.clipY2 - 1
				}
				alpha = colour ushr 24
				if (mode == 0 || mode == 1 && alpha == 255) {
					while (y1 <= y2) {
						val pixelX: Int = x1 shr 16
						if (pixelX >= this.clipX1 && pixelX < this.clipX2) {
							this.surfaceRaster!![pixelX + y1 * this.surfaceWidth] = colour
						}
						x1 += step
						y1++
					}
				} else if (mode == 1) {
					premultipliedColour = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
					val inverseAlpha: Int = 256 - alpha
					while (y1 <= y2) {
						val pixelX: Int = x1 shr 16
						if (pixelX >= this.clipX1 && pixelX < this.clipX2) {
							val pixelIndex: Int = pixelX + y1 * this.surfaceWidth
							var dest: Int = this.surfaceRaster!![pixelIndex]
							dest = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
							this.surfaceRaster!![pixelX + y1 * this.surfaceWidth] = premultipliedColour + dest
						}
						x1 += step
						y1++
					}
				} else if (mode == 2) {
					while (y1 <= y2) {
						val pixelX: Int = x1 shr 16
						if (pixelX >= this.clipX1 && pixelX < this.clipX2) {
							val pixelIndex: Int = pixelX + y1 * this.surfaceWidth
							val dest: Int = this.surfaceRaster!![pixelIndex]
							val sum: Int = colour + dest
							redBlueSum = (colour and 0xFF00FF) + (dest and 0xFF00FF)
							@Pc(587) val overflow: Int = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
							this.surfaceRaster!![pixelIndex] = (sum - overflow) or (overflow - (overflow ushr 8))
						}
						x1 += step
						y1++
					}
				} else {
					throw IllegalArgumentException()
				}
			}
		} else if (y2 >= 0) {
			this.P(x1, y1, y2 + 1, colour, mode)
		} else {
			this.P(x1, y1 + y2, -y2 + 1, colour, mode)
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIIIIIIIII)V")
	override fun drawTriangle() {
		@Pc(3) val resource: JavaThreadResource? = this.threadResource(lang.currentThread())
		@Pc(6) val rasterizer: Rasterizer = resource!!.rasterizer!!
		rasterizer.fastScanline = false
		@Pc(14) val newX1: Int = 5 - this.viewX1
		@Pc(19) val newX2: Int = 75 - this.viewX1
		@Pc(24) val newX3: Int = 15 - this.viewX1
		@Pc(29) val newY1: Int = 10 - this.viewY1
		@Pc(34) val newY2: Int = 50 - this.viewY1
		@Pc(39) val newY3: Int = 90 - this.viewY1
		rasterizer.clamp = newX1 < 0 || newX1 > rasterizer.width || newX2 < 0 || newX2 > rasterizer.width || newX3 < 0 || newX3 > rasterizer.width
		rasterizer.alpha = 0
		rasterizer.halfBlend = false
		rasterizer.renderTriangleRgb(newY1.toFloat(), newY2.toFloat(), newY3.toFloat(), newX1.toFloat(), newX2.toFloat(), newX3.toFloat(), 100.toFloat(), 100.toFloat(), 100.toFloat(), -65536, -65536, -65536)
		rasterizer.fastScanline = true
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!oea;[Lclient!rh;Z)Lclient!da;")
	override fun createFontRenderer(@OriginalArg(0) metrics: FontMetrics?, @OriginalArg(1) images: Array<SpriteType?>?): AbstractFontRenderer? {
		@Pc(3) val widths: IntArray = IntArray(images!!.size)
		@Pc(7) val heights: IntArray = IntArray(images.size)
		@Pc(9) var alpha: Boolean = false
		@Pc(11) var i: Int = 0
		while (i < images.size) {
			widths[i] = images[i]!!.width
			heights[i] = images[i]!!.height
			if (images[i]!!.alpha != null) {
				alpha = true
			}
			i++
		}
		if (alpha) {
			return JavaMonoAlphaFont(this, metrics, images, widths, heights)
		} else {
			return JavaMonoFont(this, metrics, images, widths, heights)
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!he;Lclient!vm;)Lclient!is;")
	override fun createOffscreenSurface(@OriginalArg(0) surface: Sprite?, @OriginalArg(1) buffer: DepthBuffer?): OffscreenSurface? {
		return JavaOffscreenSurface(this, surface as NativeSprite?, buffer as JavaDepthBuffer?)
	}

	@OriginalMember(owner = "client!ao", name = "C", descriptor = "(Z)V")
	override fun C(@OriginalArg(0) zWrite: Boolean) {
		@Pc(3) val javaThreadResource: JavaThreadResource? = this.threadResource(lang.currentThread())
		javaThreadResource!!.zWrite = zWrite
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIILclient!aa;II)V")
	override fun drawSurfaceLine(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) colour: Int, @OriginalArg(6) clippingMask: ComponentSurface?, @OriginalArg(7) surfaceClipLeft: Int, @OriginalArg(8) surfaceClipTop: Int) {
		var startX: Int = startX
		var startY: Int = startY
		var endX: Int = endX
		var endY: Int = endY
		@Pc(2) val javaClippingMask: JavaClippingMask = clippingMask as JavaClippingMask
		@Pc(5) val lineWidths: IntArray = javaClippingMask.lineWidths!!
		@Pc(8) val lineOffsets: IntArray = javaClippingMask.lineOffsets!!
		@Pc(17) val clipTop: Int = if (this.clipY1 > surfaceClipTop) this.clipY1 else surfaceClipTop
		@Pc(32) val clipBottom: Int = if (this.clipY2 < surfaceClipTop + lineWidths.size) this.clipY2 else surfaceClipTop + lineWidths.size
		endX -= startX
		endY -= startY
		if (endX + endY < 0) {
			startX += endX
			endX = -endX
			startY += endY
			endY = -endY
		}
		@Pc(83) var step: Int
		@Pc(116) var alpha: Int
		@Pc(129) var local129: Int
		@Pc(133) var local133: Int
		@Pc(145) var local145: Int
		@Pc(226) var spanStart: Int
		@Pc(243) var index: Int
		@Pc(248) var dest: Int
		@Pc(201) var premultipliedColour: Int
		if (endX <= endY) {
			startX = startX shl 0x10
			startX += 32768
			@Pc(390) val deltaFixed: Int = endX shl 16
			step = kotlin.math.floor(deltaFixed.toDouble() / endY.toDouble() + 0.5).toInt()
			endY += startY
			if (startY < clipTop) {
				startX += step * (clipTop - startY)
				startY = clipTop
			}
			if (endY >= clipBottom) {
				endY = clipBottom - 1
			}
			alpha = colour ushr 24
			if (alpha == 255) {
				while (startY <= endY) {
					val pixelX: Int = startX shr 16
					val maskRow: Int = startY - surfaceClipTop
					val maskStart: Int = surfaceClipLeft + lineWidths[maskRow]
					if (pixelX >= this.clipX1 && pixelX < this.clipX2 && pixelX >= maskStart && pixelX < maskStart + lineOffsets[maskRow]) {
						this.surfaceRaster!![pixelX + startY * this.surfaceWidth] = colour
					}
					startX += step
					startY++
				}
			} else {
				premultipliedColour = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
				val inverseAlpha: Int = 256 - alpha
				while (startY <= endY) {
					val pixelX: Int = startX shr 16
					val maskRow: Int = startY - surfaceClipTop
					spanStart = surfaceClipLeft + lineWidths[maskRow]
					if (pixelX >= this.clipX1 && pixelX < this.clipX2 && pixelX >= spanStart && pixelX < spanStart + lineOffsets[maskRow]) {
						index = pixelX + startY * this.surfaceWidth
						dest = this.surfaceRaster!![index]
						@Pc(584) val faded: Int = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
						this.surfaceRaster!![pixelX + startY * this.surfaceWidth] = premultipliedColour + faded
					}
					startX += step
					startY++
				}
			}
			return
		}
		startY = startY shl 0x10
		startY += 32768
		@Pc(73) val deltaFixed: Int = endY shl 16
		step = kotlin.math.floor(deltaFixed.toDouble() / endX.toDouble() + 0.5).toInt()
		endX += startX
		if (startX < this.clipX1) {
			startY += step * (this.clipX1 - startX)
			startX = this.clipX1
		}
		if (endX >= this.clipX2) {
			endX = this.clipX2 - 1
		}
		alpha = colour ushr 24
		if (alpha == 255) {
			while (startX <= endX) {
				val pixelY: Int = startY shr 16
				val maskRow: Int = pixelY - surfaceClipTop
				if (pixelY >= clipTop && pixelY < clipBottom) {
					val maskStart: Int = surfaceClipLeft + lineWidths[maskRow]
					if (startX >= maskStart && startX < maskStart + lineOffsets[maskRow]) {
						this.surfaceRaster!![startX + pixelY * this.surfaceWidth] = colour
					}
				}
				startY += step
				startX++
			}
			return
		}
		premultipliedColour = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
		val inverseAlpha: Int = 256 - alpha
		while (startX <= endX) {
			val pixelY: Int = startY shr 16
			val maskRow: Int = pixelY - surfaceClipTop
			if (pixelY >= clipTop && pixelY < clipBottom) {
				spanStart = surfaceClipLeft + lineWidths[maskRow]
				if (startX >= spanStart && startX < spanStart + lineOffsets[maskRow]) {
					index = startX + pixelY * this.surfaceWidth
					dest = this.surfaceRaster!![index]
					dest = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
					this.surfaceRaster!![index] = premultipliedColour + dest
				}
			}
			startY += step
			startX++
		}
	}

	@OriginalMember(owner = "client!ao", name = "XA", descriptor = "()I")
	override fun XA(): Int {
		return this.zFar
	}

	@OriginalMember(owner = "client!ao", name = "T", descriptor = "(IIII)V")
	override fun T(@OriginalArg(0) clipX1: Int, @OriginalArg(1) clipY1: Int, @OriginalArg(2) clipX2: Int, @OriginalArg(3) clipY2: Int) {
		if (this.clipX1 < clipX1) {
			this.clipX1 = clipX1
		}
		if (this.clipY1 < clipY1) {
			this.clipY1 = clipY1
		}
		if (this.clipX2 > clipX2) {
			this.clipX2 = clipX2
		}
		if (this.clipY2 > clipY2) {
			this.clipY2 = clipY2
		}
		this.updateViewport()
	}

	@OriginalMember(owner = "client!ao", name = "aa", descriptor = "(IIIIII)V")
	override fun aa(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) mode: Int) {
		var x: Int = x
		var y: Int = y
		var width: Int = width
		var height: Int = height
		if (x < this.clipX1) {
			width -= this.clipX1 - x
			x = this.clipX1
		}
		if (y < this.clipY1) {
			height -= this.clipY1 - y
			y = this.clipY1
		}
		if (x + width > this.clipX2) {
			width = this.clipX2 - x
		}
		if (y + height > this.clipY2) {
			height = this.clipY2 - y
		}
		if (width <= 0 || height <= 0 || x > this.clipX2 || y > this.clipY2) {
			return
		}
		@Pc(67) val rowSkip: Int = this.surfaceWidth - width
		@Pc(74) var pixelIndex: Int = x + y * this.surfaceWidth
		@Pc(78) val alpha: Int = colour ushr 24
		@Pc(90) var local90: Int
		@Pc(94) var local94: Int
		@Pc(101) var local101: Int
		if (mode != 0 && (mode != 1 || alpha != 255)) {
			@Pc(217) var local217: Int
			if (mode == 1) {
				@Pc(201) val premultipliedColour: Int = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00FF00.toInt()) ushr 8) * alpha) and 0xFF00FF00.toInt())
				val inverseAlpha: Int = 256 - alpha
				var row: Int = 0
				while (row < height) {
					var column: Int = -width
					while (column < 0) {
						var dest: Int = this.surfaceRaster!![pixelIndex]
						dest = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00FF00.toInt()) ushr 8) * inverseAlpha) and 0xFF00FF00.toInt())
						this.surfaceRaster!![pixelIndex++] = premultipliedColour + dest
						column++
					}
					pixelIndex += rowSkip
					row++
				}
			} else if (mode == 2) {
				var row: Int = 0
				while (row < height) {
					var column: Int = -width
					while (column < 0) {
						val dest: Int = this.surfaceRaster!![pixelIndex]
						val sum: Int = colour + dest
						@Pc(284) val redBlueSum: Int = (colour and 0xFF00FF) + (dest and 0xFF00FF)
						@Pc(294) val overflow: Int = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
						this.surfaceRaster!![pixelIndex++] = (sum - overflow) or (overflow - (overflow ushr 8))
						column++
					}
					pixelIndex += rowSkip
					row++
				}
			} else {
				throw IllegalArgumentException()
			}
			return
		}
		val blockCount: Int = width shr 3
		val remainder: Int = width and 0x7
		width = pixelIndex - 1
		var row: Int = -height
		while (row < 0) {
			if (blockCount > 0) {
				x = blockCount
				do {
					width++
					this.surfaceRaster!![width] = colour
					width++
					this.surfaceRaster!![width] = colour
					width++
					this.surfaceRaster!![width] = colour
					width++
					this.surfaceRaster!![width] = colour
					width++
					this.surfaceRaster!![width] = colour
					width++
					this.surfaceRaster!![width] = colour
					width++
					this.surfaceRaster!![width] = colour
					width++
					this.surfaceRaster!![width] = colour
					x--
				} while (x > 0)
			}
			if (remainder > 0) {
				x = remainder
				do {
					width++
					this.surfaceRaster!![width] = colour
					x--
				} while (x > 0)
			}
			width += rowSkip
			row++
		}
	}

	@OriginalMember(owner = "client!ao", name = "j", descriptor = "(I)[I")
	fun getTexels(@OriginalArg(0) id: Int): IntArray? {
		@Pc(2) val cache: LRUCache = this.textureCache
		@Pc(12) var texture: SafeModeTexture2D?
		withLock(this.textureCache) {
			texture = this.textureCache.get(id.toLong()) as SafeModeTexture2D?
			if (texture == null) {
				if (!super.materialProvider!!.textureAvailable(id)) {
					return null
				}
				@Pc(32) val metrics: MaterialType = super.materialProvider!!.getMetrics(id)!!
				@Pc(43) val size: Int = if (metrics.isSmall || this.shrinkTextures) 64 else this.textureSize
				texture = SafeModeTexture2D(id, size, super.materialProvider!!.getPixels(true, id, 0.7f, size, size), metrics.effectCombiner != 1)
				this.textureCache.put(texture, id.toLong())
			}
		}
		texture!!.isAnimated = true
		return texture!!.getArgbColor()
	}

	@OriginalMember(owner = "client!ao", name = "h", descriptor = "()V")
	override fun stop() {
		if (this.colourTables) {
			ColorUtil.releaseColorTables(false, true)
			this.colourTables = false
		}
		this.canvasSurface = null
		this.canvas = null
		this.width = 0
		this.height = 0
		this.surfaces = null
		this.stopped = true
	}

	@OriginalMember(owner = "client!ao", name = "e", descriptor = "()Lclient!bca;")
	override fun camera(): Matrix? {
		return this.camera
	}

	@OriginalMember(owner = "client!ao", name = "k", descriptor = "(I)[I")
	fun getArgbTexture(@OriginalArg(0) id: Int): IntArray? {
		@Pc(2) val cache: LRUCache = this.textureCache
		@Pc(14) var texture: SafeModeTexture2D?
		withLock(this.textureCache) {
			texture = this.textureCache.get(id.toLong() or Long.MIN_VALUE) as SafeModeTexture2D?
			if (texture == null) {
				if (!super.materialProvider!!.textureAvailable(id)) {
					return null
				}
				@Pc(34) val metrics: MaterialType = super.materialProvider!!.getMetrics(id)!!
				@Pc(45) val size: Int = if (metrics.isSmall || this.shrinkTextures) 64 else this.textureSize
				texture = SafeModeTexture2D(id, size, super.materialProvider!!.getPixelsArgb(size, 0.7f, id, size), metrics.effectCombiner != 1)
				this.textureCache.put(texture, id.toLong() or Long.MIN_VALUE)
			}
		}
		texture!!.isAnimated = true
		return texture!!.getArgbColor()
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(II[[I[[IIII)Lclient!s;")
	override fun createGround(@OriginalArg(0) width: Int, @OriginalArg(1) length: Int, @OriginalArg(2) tileHeights: Array<IntArray?>?, @OriginalArg(3) heights: Array<IntArray?>?, @OriginalArg(5) groundFlags: Int, @OriginalArg(6) featureFlags: Int): Ground? {
		return JavaGround(this, groundFlags, featureFlags, width, length, tileHeights, heights, 512)
	}

	@OriginalMember(owner = "client!ao", name = "H", descriptor = "(III[I)V")
	override fun H(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) screenCoords: IntArray?) {
		@Pc(24) val depth: Float = this.camera!!.tz + this.camera!!.e3_1 * x.toFloat() + this.camera!!.e3_2 * y.toFloat() + this.camera!!.e3_3 * z.toFloat()
		if (depth == 0.0f) {
			screenCoords!![2] = -1
			screenCoords[1] = -1
			screenCoords[0] = -1
			return
		}
		@Pc(73) val screenX: Int = (this.projectionScaleX.toFloat() * (this.camera!!.tx + this.camera!!.e1_1 * x.toFloat() + this.camera!!.e1_2 * y.toFloat() + this.camera!!.e1_3 * z.toFloat()) / depth).toInt()
		@Pc(105) val screenY: Int = (this.projectionScaleY.toFloat() * (this.camera!!.ty + this.camera!!.e2_1 * x.toFloat() + this.camera!!.e2_2 * y.toFloat() + this.camera!!.e2_3 * z.toFloat()) / depth).toInt()
		screenCoords!![0] = screenX - this.viewX1
		screenCoords[1] = screenY - this.viewY1
		screenCoords[2] = depth.toInt()
	}

	@OriginalMember(owner = "client!ao", name = "j", descriptor = "()Lclient!bca;")
	override fun createMatrix(): Matrix? {
		return JavaMatrix()
	}

	@OriginalMember(owner = "client!ao", name = "la", descriptor = "()V")
	override fun la() {
		this.clipX1 = 0
		this.clipY1 = 0
		this.clipX2 = this.surfaceWidth
		this.clipY2 = this.surfaceHeight
		this.updateViewport()
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!bh;IIII)Lclient!ka;")
	override fun createModel(@OriginalArg(0) mesh: Mesh?, @OriginalArg(1) functionMask: Int, @OriginalArg(2) featureMask: Int, @OriginalArg(3) ambient: Int, @OriginalArg(4) contrast: Int): Model? {
		return JavaModel(this, mesh, functionMask, ambient, contrast, featureMask)
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(II)Lclient!vm;")
	override fun createDepthBuffer(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int): DepthBuffer? {
		return JavaDepthBuffer(width, height)
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "([I)V")
	override fun accumulateViewportDimensions(@OriginalArg(0) cacheInto: IntArray?) {
		cacheInto!![0] = this.surfaceWidth
		cacheInto[1] = this.surfaceHeight
	}

	@OriginalMember(owner = "client!ao", name = "F", descriptor = "(II)V")
	override fun F(@OriginalArg(0) dx: Int, @OriginalArg(1) dy: Int) {
		@Pc(6) val rasterOffset: Int = dy * this.surfaceWidth + dx
		@Pc(13) val depthOffset: Int = dy * this.depthWidth + dx
		if (rasterOffset == 0 && depthOffset == 0) {
			return
		}
		@Pc(21) val raster: IntArray? = this.surfaceRaster
		@Pc(24) val depthBuffer: FloatArray? = this.depthBuffer
		@Pc(31) var length: Int
		if (rasterOffset < 0) {
			length = raster!!.size + rasterOffset
			Arrays.copy(raster, -rasterOffset, raster, 0, length)
		} else if (rasterOffset > 0) {
			length = raster!!.size - rasterOffset
			Arrays.copy(raster, 0, raster, rasterOffset, length)
		}
		if (depthOffset < 0) {
			length = depthBuffer!!.size + depthOffset
			Arrays.copy(depthBuffer, -depthOffset, depthBuffer, 0, length)
		} else if (depthOffset > 0) {
			length = depthBuffer!!.size - depthOffset
			Arrays.copy(depthBuffer, 0, depthBuffer, depthOffset, length)
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!is;)V")
	override fun swapSurface(@OriginalArg(0) offscreenSurface: OffscreenSurface?) {
		@Pc(2) val javaSurface: JavaOffscreenSurface = offscreenSurface as JavaOffscreenSurface
		this.surfaceWidth = javaSurface.width
		this.surfaceHeight = javaSurface.height
		this.surfaceRaster = javaSurface.raster
		this.offscreenSurface = javaSurface
		this.depthWidth = javaSurface.width
		this.depthHeight = javaSurface.height
		this.depthBuffer = javaSurface.depthBuffer
		this.reset()
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIIIIII)V")
	fun drawBillboardRgb(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) halfWidth: Int, @OriginalArg(4) halfHeight: Int, @OriginalArg(5) rotation: Int, @OriginalArg(6) id: Int, @OriginalArg(7) colour: Int, @OriginalArg(8) op: Int, @OriginalArg(9) mode: Int) {
		if (halfWidth == 0 || halfHeight == 0) {
			return
		}
		if (id == 65535 || super.materialProvider!!.getMetrics(id)!!.detailsOnly) {
			this.fillDepthTestedCircle(x, y, z, halfWidth, colour, mode)
			return
		}
		if (this.textureId != id) {
			@Pc(26) var sprite: NativeSprite? = this.spriteCache.get(id.toLong()) as NativeSprite?
			if (sprite == null) {
				@Pc(32) val data: IntArray? = this.getArgbTexture(id)
				if (data == null) {
					return
				}
				@Pc(44) val size: Int = if (this.smallTexture(id)) 64 else this.textureSize
				sprite = this.createNativeSprite(size, data, size, size)
				this.spriteCache.put(sprite, id.toLong())
			}
			this.textureId = id
			this.cachedBillboardSprite = sprite
		}
		(this.cachedBillboardSprite as SpriteSM).renderDepthTestedRgb(x - halfWidth, y - halfHeight, z, halfWidth shl 1, halfHeight shl 1, op, colour, mode)
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!wh;)V")
	override fun setSunCubeTexture(@OriginalArg(0) environmentSampler: EnvironmentSampler?) {
	}

	@OriginalMember(owner = "client!ao", name = "EA", descriptor = "(IIII)V")
	override fun EA(@OriginalArg(0) waterHeight: Int, @OriginalArg(1) fogColour: Int, @OriginalArg(2) waterDepth: Int, @OriginalArg(3) waterIntensity: Int) {
		@Pc(3) val javaThreadResource: JavaThreadResource? = this.threadResource(lang.currentThread())
		javaThreadResource!!.waterHeight = waterHeight
		javaThreadResource.fogColour = fogColour
		javaThreadResource.waterDepth = waterDepth
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(IIIIIIII)V")
	private fun verticalDashedLine(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) length: Int, @OriginalArg(3) colour: Int, @OriginalArg(5) dashLength: Int, @OriginalArg(6) gapLength: Int, @OriginalArg(7) phase: Int) {
		if (x < this.clipX1 || x >= this.clipX2) {
			return
		}
		@Pc(15) val pixel: Int = x + y * this.surfaceWidth
		@Pc(19) val alpha: Int = colour ushr 24
		@Pc(23) val period: Int = dashLength + gapLength
		@Pc(27) var offset: Int = phase % period
		@Pc(37) var local37: Int
		if (alpha == 255) {
			var row: Int = 0
			while (row < length) {
				if (y + row >= this.clipY1 && y + row < this.clipY2 && offset < dashLength) {
					this.surfaceRaster!![pixel + row * this.surfaceWidth] = colour
				}
				row++
				offset++
				offset %= period
			}
			return
		}
		@Pc(100) val blended: Int = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
		val inverseAlpha: Int = 256 - alpha
		@Pc(106) var i: Int = 0
		while (i < length) {
			if (y + i >= this.clipY1 && y + i < this.clipY2 && offset < dashLength) {
				@Pc(129) val index: Int = pixel + i * this.surfaceWidth
				@Pc(134) val dest: Int = this.surfaceRaster!![index]
				@Pc(154) val faded: Int = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
				this.surfaceRaster!![index] = blended + faded
			}
			i++
			offset++
			offset %= period
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Ljava/awt/Canvas;)V")
	override fun setCanvas(@OriginalArg(0) canvas2: Canvas?) {
		if (canvas2 == null) {
			this.canvas = null
			this.canvasSurface = null
			if (this.offscreenSurface == null) {
				this.surfaceRaster = null
				this.surfaceHeight = 1
				this.surfaceWidth = 1
				this.depthHeight = 1
				this.depthWidth = 1
				this.reset()
			}
			return
		}
		@Pc(10) val surface: JavaSurface? = this.surfaces!!.get(canvas2.hashCode().toLong()) as JavaSurface?
		if (surface == null) {
			return
		}
		this.canvas = canvas2
		@Pc(18) val dimension: Dimension = canvas2.getSize()
		this.width = dimension.width
		this.height = dimension.height
		this.canvasSurface = surface
		if (this.offscreenSurface != null) {
			return
		}
		this.surfaceRaster = surface.raster
		this.surfaceWidth = surface.width
		this.surfaceHeight = surface.height
		if (this.surfaceWidth != this.depthWidth || this.surfaceHeight != this.depthHeight) {
			this.depthWidth = this.surfaceWidth
			this.mainSurfaceWidth = this.depthWidth
			this.depthHeight = this.surfaceHeight
			this.mainSurfaceHeight = this.depthHeight
			this.depthBuffer = FloatArray(this.depthWidth * this.depthHeight)
			this.mainDepthBuffer = this.depthBuffer
		}
		this.reset()
		return
	}

	@OriginalMember(owner = "client!ao", name = "JA", descriptor = "(IIIIII)I")
	override fun JA(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) z1: Int, @OriginalArg(3) x2: Int, @OriginalArg(4) y2: Int, @OriginalArg(5) z2: Int): Int {
		@Pc(1) var outcode: Int = 0
		@Pc(26) var depth1: Float = this.camera!!.e3_1 * x1.toFloat() + this.camera!!.e3_2 * y1.toFloat() + this.camera!!.e3_3 * z1.toFloat() + this.camera!!.tz
		if (depth1 < 1.0f) {
			depth1 = 1.0f
		}
		@Pc(57) var depth2: Float = this.camera!!.e3_1 * x2.toFloat() + this.camera!!.e3_2 * y2.toFloat() + this.camera!!.e3_3 * z2.toFloat() + this.camera!!.tz
		if (depth2 < 1.0f) {
			depth2 = 1.0f
		}
		if (depth1 < this.zNear.toFloat() && depth2 < this.zNear.toFloat()) {
			outcode = outcode or 0x10
		} else if (depth1 > this.zFar.toFloat() && depth2 > this.zFar.toFloat()) {
			outcode = outcode or 0x20
		}
		@Pc(128) val screenX1: Int = (this.projectionScaleX.toFloat() * (this.camera!!.e1_1 * x1.toFloat() + this.camera!!.e1_2 * y1.toFloat() + this.camera!!.e1_3 * z1.toFloat() + this.camera!!.tx) / depth1).toInt()
		@Pc(160) val screenX2: Int = (this.projectionScaleX.toFloat() * (this.camera!!.e1_1 * x2.toFloat() + this.camera!!.e1_2 * y2.toFloat() + this.camera!!.e1_3 * z2.toFloat() + this.camera!!.tx) / depth2).toInt()
		if (screenX1 < this.viewX1 && screenX2 < this.viewX1) {
			outcode = outcode or 0x1
		} else if (screenX1 > this.viewX2 && screenX2 > this.viewX2) {
			outcode = outcode or 0x2
		}
		@Pc(217) val screenY1: Int = (this.projectionScaleY.toFloat() * (this.camera!!.e2_1 * x1.toFloat() + this.camera!!.e2_2 * y1.toFloat() + this.camera!!.e2_3 * z1.toFloat() + this.camera!!.ty) / depth1).toInt()
		@Pc(249) val screenY2: Int = (this.projectionScaleY.toFloat() * (this.camera!!.e2_1 * x2.toFloat() + this.camera!!.e2_2 * y2.toFloat() + this.camera!!.e2_3 * z2.toFloat() + this.camera!!.ty) / depth2).toInt()
		if (screenY1 < this.viewY1 && screenY2 < this.viewY1) {
			outcode = outcode or 0x4
		} else if (screenY1 > this.viewY2 && screenY2 > this.viewY2) {
			outcode = outcode or 0x8
		}
		return outcode
	}

	@OriginalMember(owner = "client!ao", name = "f", descriptor = "(I)V")
	override fun method7281(@OriginalArg(0) threadId: Int) {
		this.resources!![threadId]!!.bindThread(null)
	}

	@OriginalMember(owner = "client!ao", name = "p", descriptor = "()V")
	override fun restoreSurface() {
		if (this.canvas == null) {
			this.surfaceWidth = 1
			this.surfaceHeight = 1
			this.surfaceRaster = null
			this.depthWidth = 1
			this.depthHeight = 1
			this.depthBuffer = null
		} else {
			this.surfaceRaster = this.canvasSurface!!.raster
			this.surfaceWidth = this.canvasSurface!!.width
			this.surfaceHeight = this.canvasSurface!!.height
			this.depthBuffer = this.mainDepthBuffer
			this.depthWidth = this.mainSurfaceWidth
			this.depthHeight = this.mainSurfaceHeight
		}
		this.offscreenSurface = null
		this.reset()
	}

	@OriginalMember(owner = "client!ao", name = "l", descriptor = "(I)I")
	fun textureHsl(@OriginalArg(0) id: Int): Int {
		return super.materialProvider!!.getMetrics(id)!!.colorHsl.toInt() and 0xFFFF
	}

	@OriginalMember(owner = "client!ao", name = "w", descriptor = "()Z")
	override fun supportsAntiAliasing(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!ao", name = "o", descriptor = "()Z")
	override fun hasBloom(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "([IIIIIZ)Lclient!mi;")
	override fun createNativeSprite(@OriginalArg(0) srcPixels: IntArray?, @OriginalArg(2) srcStride: Int, @OriginalArg(3) width: Int, @OriginalArg(4) height: Int, @OriginalArg(5) copyPixels: Boolean): NativeSprite? {
		@Pc(1) var transparent: Boolean = false
		@Pc(3) var index: Int = 0
		@Pc(5) var i: Int = 0
		while (i < height) {
			@Pc(8) var j: Int = 0
			while (j < width) {
				@Pc(16) val alpha: Int = srcPixels!![index++] ushr 24
				if (alpha != 0 && alpha != 255) {
					transparent = true
					return if (transparent) TransparentSpriteSM(this, srcPixels, 0, srcStride, width, height, copyPixels) else OpaqueSpriteSM(this, srcPixels, 0, srcStride, width, height, copyPixels)
				}
				j++
			}
			i++
		}
		return if (transparent) TransparentSpriteSM(this, srcPixels, 0, srcStride, width, height, copyPixels) else OpaqueSpriteSM(this, srcPixels, 0, srcStride, width, height, copyPixels)
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!rh;Z)Lclient!mi;")
	override fun generateSprite(@OriginalArg(0) indexedImage: SpriteType?, @OriginalArg(1) paletteSprite: Boolean): NativeSprite? {
		@Pc(2) val pallete: IntArray? = indexedImage!!.pallete
		@Pc(5) val pixels: ByteArray? = indexedImage.pixels
		@Pc(8) val width: Int = indexedImage.width
		@Pc(11) val height: Int = indexedImage.height
		@Pc(76) val javaSprite: SpriteSM
		@Pc(20) var local20: IntArray
		@Pc(25) var local25: ByteArray
		@Pc(27) var y: Int
		@Pc(32) var local32: Int
		@Pc(34) var x: Int
		if (paletteSprite && indexedImage.alpha == null) {
			val paletteCopy: IntArray = IntArray(pallete!!.size)
			val paletteIndices: ByteArray = ByteArray(width * height)
			y = 0
			while (y < height) {
				val rowOffset: Int = y * width
				x = 0
				while (x < width) {
					paletteIndices[rowOffset + x] = pixels!![rowOffset + x]
					x++
				}
				y++
			}
			var paletteIndex: Int = 0
			while (paletteIndex < pallete.size) {
				paletteCopy[paletteIndex] = pallete[paletteIndex]
				paletteIndex++
			}
			javaSprite = PaletteSpriteSM(this, paletteIndices, paletteCopy, width, height)
		} else {
			val argbPixels: IntArray = IntArray(width * height)
			val alphaChannel: ByteArray? = indexedImage.alpha
			if (alphaChannel == null) {
				y = 0
				while (y < height) {
					val rowOffset: Int = y * width
					x = 0
					while (x < width) {
						@Pc(156) val rgb: Int = pallete!![pixels!![rowOffset + x].toInt() and 0xFF]
						argbPixels[rowOffset + x] = if (rgb == 0) 0 else rgb or 0xFF000000.toInt()
						x++
					}
					y++
				}
				javaSprite = OpaqueSpriteSM(this, argbPixels, width, height)
			} else {
				y = 0
				while (y < height) {
					val rowOffset: Int = y * width
					x = 0
					while (x < width) {
						argbPixels[rowOffset + x] = pallete!![pixels!![rowOffset + x].toInt() and 0xFF] or (alphaChannel[rowOffset + x].toInt() shl 24)
						x++
					}
					y++
				}
				javaSprite = TransparentSpriteSM(this, argbPixels, width, height)
			}
		}
		javaSprite.setOffsets(indexedImage.minX, indexedImage.minY, indexedImage.borderX, indexedImage.borderY)
		return javaSprite
	}

	@OriginalMember(owner = "client!ao", name = "f", descriptor = "(IIIIII)V")
	private fun fillDepthTestedCircle(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) size: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) mode: Int) {
		var y: Int = y
		var size: Int = size
		if (size < 0) {
			size = -size
		}
		@Pc(8) var y1: Int = y - size
		if (y1 < this.clipY1) {
			y1 = this.clipY1
		}
		@Pc(21) var y2: Int = y + size + 1
		if (y2 > this.clipY2) {
			y2 = this.clipY2
		}
		@Pc(30) var currY: Int = y1
		@Pc(34) val squareRadius: Int = size * size
		@Pc(36) var width: Int = 0
		@Pc(40) var dy: Int = y - y1
		@Pc(44) var distanceSq: Int = dy * dy
		@Pc(48) var midpointDistanceSq: Int = distanceSq - dy
		if (y > y2) {
			y = y2
		}
		@Pc(57) val alpha: Int = colour ushr 24
		@Pc(91) var local91: Int
		@Pc(102) var local102: Int
		@Pc(116) var local116: Int
		@Pc(118) var local118: Int
		if (mode == 0 || mode == 1 && alpha == 255) {
			while (currY < y) {
				while (midpointDistanceSq <= squareRadius || distanceSq <= squareRadius) {
					distanceSq += width + width
					midpointDistanceSq += width++ + width
				}
				var startX: Int = x + 1 - width
				if (startX < this.clipX1) {
					startX = this.clipX1
				}
				var endX: Int = x + width
				if (endX > this.clipX2) {
					endX = this.clipX2
				}
				var pixelIndex: Int = startX + currY * this.surfaceWidth
				var currX: Int = startX
				while (currX < endX) {
					if (z.toFloat() < this.depthBuffer!![pixelIndex]) {
						this.surfaceRaster!![pixelIndex] = colour
					}
					pixelIndex++
					currX++
				}
				currY++
				distanceSq -= dy-- + dy
				midpointDistanceSq -= dy + dy
			}
			width = size
			dy = currY - y
			midpointDistanceSq = dy * dy + squareRadius
			distanceSq = midpointDistanceSq - size
			midpointDistanceSq -= dy
			while (currY < y2) {
				while (midpointDistanceSq > squareRadius && distanceSq > squareRadius) {
					midpointDistanceSq -= width-- + width
					distanceSq -= width + width
				}
				var startX: Int = x - width
				if (startX < this.clipX1) {
					startX = this.clipX1
				}
				var endX: Int = x + width
				if (endX > this.clipX2 - 1) {
					endX = this.clipX2 - 1
				}
				var pixelIndex: Int = startX + currY * this.surfaceWidth
				var currX: Int = startX
				while (currX <= endX) {
					if (z.toFloat() < this.depthBuffer!![pixelIndex]) {
						this.surfaceRaster!![pixelIndex] = colour
					}
					pixelIndex++
					currX++
				}
				currY++
				midpointDistanceSq += dy + dy
				distanceSq += dy++ + dy
			}
			return
		}
		@Pc(350) var local350: Int
		@Pc(364) var local364: Int
		if (mode == 1) {
			@Pc(293) val blended: Int = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
			val inverseAlpha: Int = 256 - alpha
			while (currY < y) {
				while (midpointDistanceSq <= squareRadius || distanceSq <= squareRadius) {
					distanceSq += width + width
					midpointDistanceSq += width++ + width
				}
				var startX: Int = x + 1 - width
				if (startX < this.clipX1) {
					startX = this.clipX1
				}
				var endX: Int = x + width
				if (endX > this.clipX2) {
					endX = this.clipX2
				}
				var pixelIndex: Int = startX + currY * this.surfaceWidth
				var currX: Int = startX
				while (currX < endX) {
					if (z.toFloat() < this.depthBuffer!![pixelIndex]) {
						var dest: Int = this.surfaceRaster!![pixelIndex]
						dest = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
						this.surfaceRaster!![pixelIndex] = blended + dest
					}
					pixelIndex++
					currX++
				}
				currY++
				distanceSq -= dy-- + dy
				midpointDistanceSq -= dy + dy
			}
			width = size
			dy = -dy
			midpointDistanceSq = dy * dy + squareRadius
			distanceSq = midpointDistanceSq - size
			midpointDistanceSq -= dy
			while (currY < y2) {
				while (midpointDistanceSq > squareRadius && distanceSq > squareRadius) {
					midpointDistanceSq -= width-- + width
					distanceSq -= width + width
				}
				var startX: Int = x - width
				if (startX < this.clipX1) {
					startX = this.clipX1
				}
				var endX: Int = x + width
				if (endX > this.clipX2 - 1) {
					endX = this.clipX2 - 1
				}
				var pixelIndex: Int = startX + currY * this.surfaceWidth
				var currX: Int = startX
				while (currX <= endX) {
					if (z.toFloat() < this.depthBuffer!![pixelIndex]) {
						var dest: Int = this.surfaceRaster!![pixelIndex]
						dest = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
						this.surfaceRaster!![pixelIndex] = blended + dest
					}
					pixelIndex++
					currX++
				}
				currY++
				midpointDistanceSq += dy + dy
				distanceSq += dy++ + dy
			}
		} else if (mode == 2) {
			@Pc(633) var redBlueSum: Int
			while (currY < y) {
				while (midpointDistanceSq <= squareRadius || distanceSq <= squareRadius) {
					distanceSq += width + width
					midpointDistanceSq += width++ + width
				}
				var startX: Int = x + 1 - width
				if (startX < this.clipX1) {
					startX = this.clipX1
				}
				var endX: Int = x + width
				if (endX > this.clipX2) {
					endX = this.clipX2
				}
				var pixelIndex: Int = startX + currY * this.surfaceWidth
				var currX: Int = startX
				while (currX < endX) {
					if (z.toFloat() < this.depthBuffer!![pixelIndex]) {
						val dest: Int = this.surfaceRaster!![pixelIndex]
						val sum: Int = colour + dest
						redBlueSum = (colour and 0xFF00FF) + (dest and 0xFF00FF)
						@Pc(643) val overflow: Int = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
						this.surfaceRaster!![pixelIndex] = (sum - overflow) or (overflow - (overflow ushr 8))
					}
					pixelIndex++
					currX++
				}
				currY++
				distanceSq -= dy-- + dy
				midpointDistanceSq -= dy + dy
			}
			width = size
			dy = -dy
			midpointDistanceSq = dy * dy + squareRadius
			distanceSq = midpointDistanceSq - size
			midpointDistanceSq -= dy
			while (currY < y2) {
				while (midpointDistanceSq > squareRadius && distanceSq > squareRadius) {
					midpointDistanceSq -= width-- + width
					distanceSq -= width + width
				}
				var startX: Int = x - width
				if (startX < this.clipX1) {
					startX = this.clipX1
				}
				var endX: Int = x + width
				if (endX > this.clipX2 - 1) {
					endX = this.clipX2 - 1
				}
				var pixelIndex: Int = startX + currY * this.surfaceWidth
				var currX: Int = startX
				while (currX <= endX) {
					if (z.toFloat() < this.depthBuffer!![pixelIndex]) {
						var dest: Int = this.surfaceRaster!![pixelIndex]
						val sum: Int = colour + dest
						redBlueSum = (colour and 0xFF00FF) + (dest and 0xFF00FF)
						dest = (redBlueSum and 0x1000100) + ((sum - redBlueSum) and 0x10000)
						this.surfaceRaster!![pixelIndex] = (sum - dest) or (dest - (dest ushr 8))
					}
					pixelIndex++
					currX++
				}
				currY++
				midpointDistanceSq += dy + dy
				distanceSq += dy++ + dy
			}
		} else {
			throw IllegalArgumentException()
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIII)V")
	override fun strongLine(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) x2: Int, @OriginalArg(3) y2: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) strength: Int) {
		@Pc(3) val resource: JavaThreadResource? = this.threadResource(lang.currentThread())
		@Pc(6) val rasterizer: Rasterizer = resource!!.rasterizer!!
		@Pc(10) var width: Int = x2 - x1
		@Pc(14) var height: Int = y2 - y1
		@Pc(21) val absWidth: Int = if (width >= 0) width else -width
		@Pc(28) val absHeight: Int = if (height >= 0) height else -height
		@Pc(30) var minDimension: Int = absWidth
		if (absWidth < absHeight) {
			minDimension = absHeight
		}
		if (minDimension == 0) {
			return
		}
		@Pc(44) var deltaX: Int = (width shl 16) / minDimension
		@Pc(50) var deltaY: Int = (height shl 16) / minDimension
		width += deltaX shr 16
		height += deltaY shr 16
		if (deltaY <= deltaX) {
			deltaX = -deltaX
		} else {
			deltaY = -deltaY
		}
		@Pc(78) val strongX1: Int = (strength * deltaY) shr 17
		@Pc(86) val strongX2: Int = (strength * deltaY + 1) shr 17
		@Pc(92) val strongY1: Int = (strength * deltaX) shr 17
		@Pc(100) val strongY2: Int = (strength * deltaX + 1) shr 17
		@Pc(105) val centerX: Int = x1 - rasterizer.offsetX()
		@Pc(110) val centerY: Int = y1 - rasterizer.offsetY()
		@Pc(114) val lineX1: Int = centerX + strongX1
		@Pc(118) val lineX2: Int = centerX - strongX2
		@Pc(124) val lineX3: Int = centerX + width - strongX2
		@Pc(130) val lineX4: Int = centerX + width + strongX1
		@Pc(134) val lineY1: Int = centerY + strongY1
		@Pc(138) val lineY2: Int = centerY - strongY2
		@Pc(144) val lineY3: Int = centerY + height - strongY2
		@Pc(150) val lineY4: Int = centerY + height + strongY1
		rasterizer.alpha = 0
		this.C(false)
		rasterizer.clamp = lineX1 < 0 || lineX1 > rasterizer.width || lineX2 < 0 || lineX2 > rasterizer.width || lineX3 < 0 || lineX3 > rasterizer.width
		rasterizer.renderFlatTriangleRgb(lineY1.toFloat(), lineY2.toFloat(), lineY3.toFloat(), lineX1.toFloat(), lineX2.toFloat(), lineX3.toFloat(), 100.0f, 100.0f, 100.0f, colour)
		rasterizer.clamp = lineX1 < 0 || lineX1 > rasterizer.width || lineX3 < 0 || lineX3 > rasterizer.width || lineX4 < 0 || lineX4 > rasterizer.width
		rasterizer.renderFlatTriangleRgb(lineY1.toFloat(), lineY3.toFloat(), lineY4.toFloat(), lineX1.toFloat(), lineX3.toFloat(), lineX4.toFloat(), 100.0f, 100.0f, 100.0f, colour)
		this.C(true)
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIZ)Lclient!mi;")
	override fun generateEmptySprite(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int, @OriginalArg(2) hasAlpha: Boolean): NativeSprite? {
		return if (hasAlpha) TransparentSpriteSM(this, width, height) else OpaqueSpriteSM(this, width, height)
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Z)V")
	override fun setShrinkTextures(@OriginalArg(0) shrinkTextures: Boolean) {
		this.shrinkTextures = shrinkTextures
		this.textureCache.reset()
	}

	@OriginalMember(owner = "client!ao", name = "c", descriptor = "(I)V")
	override fun initialize() {
		ModelSM.maxModelVertices = 10000
		ModelSM.maxModelTriangles = 10000
		if (this.threadCount > 1) {
			throw IllegalStateException("No MT")
		}
		this.allocateThreads(this.threadCount)
		this.linkThreads(0)
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIF)Lclient!ab;")
	override fun createPointLight(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) radius: Int, @OriginalArg(4) colour: Int, @OriginalArg(5) intensity: Float): PointLight? {
		return null
	}

	@OriginalMember(owner = "client!ao", name = "m", descriptor = "()Z")
	override fun bloom(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!wh;Lclient!wh;FLclient!wh;)Lclient!wh;")
	override fun drawQuad(@OriginalArg(0) environmentSampler: EnvironmentSampler?, @OriginalArg(1) environmentSampler2: EnvironmentSampler?, @OriginalArg(2) alpha: Float, @OriginalArg(3) environmentSampler3: EnvironmentSampler?): EnvironmentSampler? {
		return null
	}

	@OriginalMember(owner = "client!ao", name = "za", descriptor = "(IIIII)V")
	override fun za(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) radius: Int, @OriginalArg(3) colour: Int, @OriginalArg(4) mode: Int) {
		var y: Int = y
		var radius: Int = radius
		if (radius < 0) {
			radius = -radius
		}
		@Pc(8) var y1: Int = y - radius
		if (y1 < this.clipY1) {
			y1 = this.clipY1
		}
		@Pc(21) var y2: Int = y + radius + 1
		if (y2 > this.clipY2) {
			y2 = this.clipY2
		}
		@Pc(30) var currY: Int = y1
		@Pc(34) val squareRadius: Int = radius * radius
		@Pc(36) var halfWidth: Int = 0
		@Pc(40) var dy: Int = y - y1
		@Pc(44) var distanceSq: Int = dy * dy
		@Pc(48) var midpointDistanceSq: Int = distanceSq - dy
		if (y > y2) {
			y = y2
		}
		@Pc(57) val alpha: Int = colour ushr 24
		@Pc(91) var local91: Int
		@Pc(102) var local102: Int
		@Pc(116) var local116: Int
		@Pc(118) var local118: Int
		if (alpha == 255) {
			while (currY < y) {
				while (midpointDistanceSq <= squareRadius || distanceSq <= squareRadius) {
					distanceSq += halfWidth + halfWidth
					midpointDistanceSq += halfWidth++ + halfWidth
				}
				var startX: Int = x + 1 - halfWidth
				if (startX < this.clipX1) {
					startX = this.clipX1
				}
				var endX: Int = x + halfWidth
				if (endX > this.clipX2) {
					endX = this.clipX2
				}
				var pixelIndex: Int = startX + currY * this.surfaceWidth
				var currX: Int = startX
				while (currX < endX) {
					this.surfaceRaster!![pixelIndex++] = colour
					currX++
				}
				currY++
				distanceSq -= dy-- + dy
				midpointDistanceSq -= dy + dy
			}
			halfWidth = radius
			dy = currY - y
			midpointDistanceSq = dy * dy + squareRadius
			distanceSq = midpointDistanceSq - radius
			midpointDistanceSq -= dy
			while (currY < y2) {
				while (midpointDistanceSq > squareRadius && distanceSq > squareRadius) {
					midpointDistanceSq -= halfWidth-- + halfWidth
					distanceSq -= halfWidth + halfWidth
				}
				var startX: Int = x - halfWidth
				if (startX < this.clipX1) {
					startX = this.clipX1
				}
				var endX: Int = x + halfWidth
				if (endX > this.clipX2 - 1) {
					endX = this.clipX2 - 1
				}
				var pixelIndex: Int = startX + currY * this.surfaceWidth
				var currX: Int = startX
				while (currX <= endX) {
					this.surfaceRaster!![pixelIndex++] = colour
					currX++
				}
				currY++
				midpointDistanceSq += dy + dy
				distanceSq += dy++ + dy
			}
			return
		}
		@Pc(277) val premultipliedColour: Int = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
		val inverseAlpha: Int = 256 - alpha
		@Pc(334) var currX: Int
		@Pc(340) var dest: Int
		while (currY < y) {
			while (midpointDistanceSq <= squareRadius || distanceSq <= squareRadius) {
				distanceSq += halfWidth + halfWidth
				midpointDistanceSq += halfWidth++ + halfWidth
			}
			var startX: Int = x + 1 - halfWidth
			if (startX < this.clipX1) {
				startX = this.clipX1
			}
			var endX: Int = x + halfWidth
			if (endX > this.clipX2) {
				endX = this.clipX2
			}
			var pixelIndex: Int = startX + currY * this.surfaceWidth
			currX = startX
			while (currX < endX) {
				dest = this.surfaceRaster!![pixelIndex]
				@Pc(360) val faded: Int = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
				this.surfaceRaster!![pixelIndex++] = premultipliedColour + faded
				currX++
			}
			currY++
			distanceSq -= dy-- + dy
			midpointDistanceSq -= dy + dy
		}
		halfWidth = radius
		dy = -dy
		midpointDistanceSq = dy * dy + squareRadius
		distanceSq = midpointDistanceSq - radius
		midpointDistanceSq -= dy
		while (currY < y2) {
			while (midpointDistanceSq > squareRadius && distanceSq > squareRadius) {
				midpointDistanceSq -= halfWidth-- + halfWidth
				distanceSq -= halfWidth + halfWidth
			}
			var startX: Int = x - halfWidth
			if (startX < this.clipX1) {
				startX = this.clipX1
			}
			var endX: Int = x + halfWidth
			if (endX > this.clipX2 - 1) {
				endX = this.clipX2 - 1
			}
			var pixelIndex: Int = startX + currY * this.surfaceWidth
			currX = startX
			while (currX <= endX) {
				dest = this.surfaceRaster!![pixelIndex]
				dest = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
				this.surfaceRaster!![pixelIndex++] = premultipliedColour + dest
				currX++
			}
			currY++
			midpointDistanceSq += dy + dy
			distanceSq += dy++ + dy
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Ljava/lang/Runnable;)Lclient!pga;")
	fun threadResource(@OriginalArg(0) thread: Runnable?): JavaThreadResource? {
		@Pc(1) var i: Int = 0
		while (i < this.threadCount) {
			if (this.resources!![i]!!.thread === thread) {
				return this.resources!![i]
			}
			i++
		}
		return null
	}

	@OriginalMember(owner = "client!ao", name = "E", descriptor = "()I")
	override fun E(): Int {
		return 0
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(II)V")
	@Throws(FlipException::class)
	override fun flip(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int) {
		if (this.canvas == null || this.canvasSurface == null) {
			throw IllegalStateException("off")
		}
		try {
			@Pc(14) val graphics: Graphics? = this.canvas!!.getGraphics()
			this.canvasSurface!!.clip(0, x, this.height, graphics, 0, this.width, y)
		} catch (@Pc(29) ex: Exception) {
			this.canvas!!.repaint()
		}
	}

	@OriginalMember(owner = "client!ao", name = "ZA", descriptor = "(IFFFFF)V")
	override fun ZA(@OriginalArg(0) colour: Int, @OriginalArg(1) sunIntensity: Float, @OriginalArg(2) antiSunIntensity: Float, @OriginalArg(3) x: Float, @OriginalArg(4) y: Float, @OriginalArg(5) z: Float) {
		this.sunIntensity = (sunIntensity * 65535.0f).toInt()
		this.reverseSunIntensity = (antiSunIntensity * 65535.0f).toInt()
		@Pc(26) val length: Float = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
		this.sunX = (x * 65535.0f / length).toInt()
		this.sunY = (y * 65535.0f / length).toInt()
		this.sunZ = (z * 65535.0f / length).toInt()
	}

	@OriginalMember(owner = "client!ao", name = "h", descriptor = "(I)V")
	override fun setTextureSize(@OriginalArg(0) size: Int) {
		this.textureSize = size
		this.textureCache.reset()
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(Ljava/awt/Canvas;II)V")
	override fun resizeCanvas(@OriginalArg(0) canvas2: Canvas?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int) {
		@Pc(8) var surface: JavaSurface? = this.surfaces!!.get(canvas2!!.hashCode().toLong()) as JavaSurface?
		if (surface == null) {
			return
		}
		surface.unlink()
		surface = JavaSurfaceFactory.createSurface(height, canvas2, width)
		this.surfaces!!.put(canvas2.hashCode().toLong(), surface)
		if (this.canvas !== canvas2 || this.offscreenSurface != null) {
			return
		}
		@Pc(37) val dimension: Dimension = canvas2.getSize()
		this.width = dimension.width
		this.height = dimension.height
		this.canvasSurface = surface
		this.surfaceRaster = surface!!.raster
		this.surfaceWidth = surface.width
		this.surfaceHeight = surface.height
		if (this.surfaceWidth != this.depthWidth || this.surfaceHeight != this.depthHeight) {
			this.depthWidth = this.surfaceWidth
			this.mainSurfaceWidth = this.depthWidth
			this.depthHeight = this.surfaceHeight
			this.mainSurfaceHeight = this.depthHeight
			this.depthBuffer = FloatArray(this.depthWidth * this.depthHeight)
			this.mainDepthBuffer = this.depthBuffer
		}
		this.reset()
	}

	@OriginalMember(owner = "client!ao", name = "r", descriptor = "(IIIIIII)I")
	override fun r(@OriginalArg(0) x1: Int, @OriginalArg(1) y1: Int, @OriginalArg(2) z1: Int, @OriginalArg(3) x2: Int, @OriginalArg(4) y2: Int, @OriginalArg(5) z2: Int, @OriginalArg(6) zoom: Int): Int {
		@Pc(24) val depth1: Float = this.camera!!.e3_1 * x1.toFloat() + this.camera!!.e3_2 * y1.toFloat() + this.camera!!.e3_3 * z1.toFloat() + this.camera!!.tz
		@Pc(49) val depth2: Float = this.camera!!.e3_1 * x2.toFloat() + this.camera!!.e3_2 * y2.toFloat() + this.camera!!.e3_3 * z2.toFloat() + this.camera!!.tz
		@Pc(51) var outcode: Int = 0
		if (depth1 < this.zNear.toFloat() && depth2 < this.zNear.toFloat()) {
			outcode = outcode or 0x10
		} else if (depth1 > this.zFar.toFloat() && depth2 > this.zFar.toFloat()) {
			outcode = outcode or 0x20
		}
		@Pc(117) val screenX1: Int = (this.projectionScaleX.toFloat() * (this.camera!!.e1_1 * x1.toFloat() + this.camera!!.e1_2 * y1.toFloat() + this.camera!!.e1_3 * z1.toFloat() + this.camera!!.tx) / zoom.toFloat()).toInt()
		@Pc(150) val screenX2: Int = (this.projectionScaleX.toFloat() * (this.camera!!.e1_1 * x2.toFloat() + this.camera!!.e1_2 * y2.toFloat() + this.camera!!.e1_3 * z2.toFloat() + this.camera!!.tx) / zoom.toFloat()).toInt()
		if (screenX1 < this.viewX1 && screenX2 < this.viewX1) {
			outcode = outcode or 0x1
		} else if (screenX1 > this.viewX2 && screenX2 > this.viewX2) {
			outcode = outcode or 0x2
		}
		@Pc(208) val screenY1: Int = (this.projectionScaleY.toFloat() * (this.camera!!.e2_1 * x1.toFloat() + this.camera!!.e2_2 * y1.toFloat() + this.camera!!.e2_3 * z1.toFloat() + this.camera!!.ty) / zoom.toFloat()).toInt()
		@Pc(241) val screenY2: Int = (this.projectionScaleY.toFloat() * (this.camera!!.e2_1 * x2.toFloat() + this.camera!!.e2_2 * y2.toFloat() + this.camera!!.e2_3 * z2.toFloat() + this.camera!!.ty) / zoom.toFloat()).toInt()
		if (screenY1 < this.viewY1 && screenY2 < this.viewY1) {
			outcode = outcode or 0x4
		} else if (screenY1 > this.viewY2 && screenY2 > this.viewY2) {
			outcode = outcode or 0x8
		}
		return outcode
	}

	@OriginalMember(owner = "client!ao", name = "m", descriptor = "(I)Z")
	fun textureAvailable(@OriginalArg(0) id: Int): Boolean {
		return super.materialProvider!!.textureAvailable(id)
	}

	@OriginalMember(owner = "client!ao", name = "z", descriptor = "()Z")
	override fun method7311(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!ao", name = "K", descriptor = "([I)V")
	override fun K(@OriginalArg(0) clip: IntArray?) {
		clip!![0] = this.clipX1
		clip[1] = this.clipY1
		clip[2] = this.clipX2
		clip[3] = this.clipY2
	}

	@OriginalMember(owner = "client!ao", name = "i", descriptor = "(I)Lclient!za;")
	override fun createHeap(@OriginalArg(0) size: Int): MemoryPool? {
		return null
	}

	@OriginalMember(owner = "client!ao", name = "d", descriptor = "(II)Lclient!he;")
	override fun method7268(@OriginalArg(0) width: Int, @OriginalArg(1) height: Int): Sprite? {
		return this.generateEmptySprite(width, height, false)
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIIIII)V")
	override fun drawLine(@OriginalArg(0) startX: Int, @OriginalArg(1) startY: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) endY: Int, @OriginalArg(4) colour: Int, @OriginalArg(6) dashLength: Int, @OriginalArg(7) gapLength: Int, @OriginalArg(8) dashOffset: Int) {
		var startX: Int = startX
		var startY: Int = startY
		var endX: Int = endX
		var endY: Int = endY
		var dashOffset: Int = dashOffset
		endX -= startX
		endY -= startY
		@Pc(28) var dashCycle: Int
		@Pc(32) var rawDashOffset: Int
		if (endY == 0) {
			if (endX >= 0) {
				this.horizontalDashedLine(startX, startY, endX + 1, colour, dashLength, gapLength, dashOffset)
			} else {
				dashCycle = dashLength + gapLength
				rawDashOffset = dashOffset % dashCycle
				rawDashOffset = dashCycle + dashLength - rawDashOffset - (-endX + 1) % dashCycle
				dashOffset = rawDashOffset % dashCycle
				if (dashOffset < 0) {
					dashOffset += dashCycle
				}
				this.horizontalDashedLine(startX + endX, startY, 1 - endX, colour, dashLength, gapLength, dashOffset)
			}
		} else if (endX != 0) {
			rawDashOffset = dashOffset shl 8
			@Pc(143) val scaledDash: Int = dashLength shl 8
			@Pc(147) val scaledGap: Int = gapLength shl 8
			dashCycle = scaledDash + scaledGap
			dashOffset = rawDashOffset % dashCycle
			@Pc(172) var local172: Int
			@Pc(176) var local176: Int
			if (endX + endY < 0) {
				val lineLength: Int = (kotlin.math.sqrt((endX * endX + endY * endY).toDouble()) * 256.0).toInt()
				val lengthRemainder: Int = lineLength % dashCycle
				rawDashOffset = dashCycle + scaledDash - dashOffset - lengthRemainder
				dashOffset = rawDashOffset % dashCycle
				if (dashOffset < 0) {
					dashOffset += dashCycle
				}
				startX += endX
				endX = -endX
				startY += endY
				endY = -endY
			}
			@Pc(254) var phaseStep: Int
			@Pc(267) var local267: Int
			@Pc(349) var local349: Int
			@Pc(375) var index: Int
			@Pc(380) var dest: Int
			@Pc(237) var local237: Int
			@Pc(223) var local223: Int
			@Pc(340) var premultipliedColour: Int
			if (endX > endY) {
				startY = startY shl 0x10
				startY += 32768
				val fixedDeltaY: Int = endY shl 16
				val slope: Int = kotlin.math.floor(fixedDeltaY.toDouble() / endX.toDouble() + 0.5).toInt()
				val lastX: Int = endX + startX
				val alpha: Int = colour ushr 24
				phaseStep = kotlin.math.sqrt(((slope shr 8) * (slope shr 8) + 65536).toDouble()).toInt()
				if (alpha == 255) {
					while (startX <= lastX) {
						val pixelY: Int = startY shr 16
						if (startX >= this.clipX1 && startX < this.clipX2 && pixelY >= this.clipY1 && pixelY < this.clipY2 && dashOffset < scaledDash) {
							this.surfaceRaster!![startX + pixelY * this.surfaceWidth] = colour
						}
						startY += slope
						startX++
						rawDashOffset = dashOffset + phaseStep
						dashOffset = rawDashOffset % dashCycle
					}
				} else {
					premultipliedColour = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
					val inverseAlpha: Int = 256 - alpha
					while (startX <= lastX) {
						val pixelY: Int = startY shr 16
						if (startX >= this.clipX1 && startX < this.clipX2 && pixelY >= this.clipY1 && pixelY < this.clipY2 && dashOffset < scaledDash) {
							index = startX + pixelY * this.surfaceWidth
							dest = this.surfaceRaster!![index]
							dest = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
							this.surfaceRaster!![index] = premultipliedColour + dest
						}
						startY += slope
						startX++
						rawDashOffset = dashOffset + phaseStep
						dashOffset = rawDashOffset % dashCycle
					}
				}
			} else {
				startX = startX shl 0x10
				startX += 32768
				val fixedDeltaX: Int = endX shl 16
				val slope: Int = kotlin.math.floor(fixedDeltaX.toDouble() / endY.toDouble() + 0.5).toInt()
				val lastY: Int = endY + startY
				val alpha: Int = colour ushr 24
				phaseStep = kotlin.math.sqrt(((slope shr 8) * (slope shr 8) + 65536).toDouble()).toInt()
				if (alpha == 255) {
					while (startY <= lastY) {
						val pixelX: Int = startX shr 16
						if (startY >= this.clipY1 && startY < this.clipY2 && pixelX >= this.clipX1 && pixelX < this.clipX2 && dashOffset < scaledDash) {
							this.surfaceRaster!![pixelX + startY * this.surfaceWidth] = colour
						}
						startX += slope
						startY++
						rawDashOffset = dashOffset + phaseStep
						dashOffset = rawDashOffset % dashCycle
					}
				} else {
					premultipliedColour = ((((colour and 0xFF00FF) * alpha) shr 8) and 0xFF00FF) + ((((colour and 0xFF00) * alpha) shr 8) and 0xFF00) + (alpha shl 24)
					val inverseAlpha: Int = 256 - alpha
					while (startY <= lastY) {
						val pixelX: Int = startX shr 16
						if (startY >= this.clipY1 && startY < this.clipY2 && pixelX >= this.clipX1 && pixelX < this.clipX2 && dashOffset < scaledDash) {
							index = pixelX + startY * this.surfaceWidth
							dest = this.surfaceRaster!![index]
							@Pc(708) val faded: Int = ((((dest and 0xFF00FF) * inverseAlpha) shr 8) and 0xFF00FF) + ((((dest and 0xFF00) * inverseAlpha) shr 8) and 0xFF00)
							this.surfaceRaster!![pixelX + startY * this.surfaceWidth] = premultipliedColour + faded
						}
						startX += slope
						startY++
						rawDashOffset = dashOffset + phaseStep
						dashOffset = rawDashOffset % dashCycle
					}
				}
			}
		} else if (endY >= 0) {
			this.verticalDashedLine(startX, startY, endY + 1, colour, dashLength, gapLength, dashOffset)
		} else {
			dashCycle = dashLength + gapLength
			rawDashOffset = dashOffset % dashCycle
			rawDashOffset = dashCycle + dashLength - rawDashOffset - (-endY + 1) % dashCycle
			dashOffset = rawDashOffset % dashCycle
			if (dashOffset < 0) {
				dashOffset += dashCycle
			}
			this.verticalDashedLine(startX, startY + endY, -endY + 1, colour, dashLength, gapLength, dashOffset)
		}
	}

	@OriginalMember(owner = "client!ao", name = "X", descriptor = "(I)V")
	override fun X(@OriginalArg(0) shadowSize: Int) {
	}

	@OriginalMember(owner = "client!ao", name = "c", descriptor = "()Lclient!fia;")
	override fun renderer(): Renderer? {
		return Renderer(0, "Pure Java", 1, "CPU", 0L)
	}

	@OriginalMember(owner = "client!ao", name = "e", descriptor = "(I)V")
	override fun linkThreads(@OriginalArg(0) index: Int) {
		this.resources!![index]!!.bindThread(lang.currentThread())
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	@Throws(FlipException::class)
	override fun flipDirtyRect(@OriginalArg(0) rectangles: Array<Rectangle?>?, @OriginalArg(1) count: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int) {
		if (this.canvas == null || this.canvasSurface == null) {
			throw IllegalStateException("off")
		}
		try {
			@Pc(14) val graphics: Graphics? = this.canvas!!.getGraphics()
			@Pc(16) var i: Int = 0
			while (i < count) {
				@Pc(21) val rect: Rectangle = rectangles!![i]!!
				if (rect.x + x <= this.surfaceWidth && rect.y + y <= this.surfaceHeight && rect.x + x + rect.width > 0 && rect.y + y + rect.height > 0) {
					this.canvasSurface!!.clip(rect.y, rect.x + x, rect.height, graphics, rect.x, rect.width, rect.y + y)
				}
				i++
			}
		} catch (@Pc(79) exception: Exception) {
			this.canvas!!.repaint()
		}
	}

	@OriginalMember(owner = "client!ao", name = "M", descriptor = "()I")
	override fun M(): Int {
		@Pc(2) val modelCount: Int = this.modelCount
		this.modelCount = 0
		return modelCount
	}

	@OriginalMember(owner = "client!ao", name = "n", descriptor = "(I)I")
	fun textureAlphaBlendMode(@OriginalArg(0) id: Int): Int {
		return super.materialProvider!!.getMetrics(id)!!.effectCombiner
	}

	@OriginalMember(owner = "client!ao", name = "I", descriptor = "()I")
	override fun I(): Int {
		@Pc(2) val count: Int = this.count
		this.count = 0
		return count
	}

	@OriginalMember(owner = "client!ao", name = "x", descriptor = "()I")
	override fun getMaxLights(): Int {
		return 0
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(I)V")
	override fun tick(@OriginalArg(0) time: Int) {
		@Pc(4) val elapsed: Int = time - this.lastTickTime
		@Pc(9) var `object`: Any? = this.textureCache.first()
		while (`object` != null) {
			@Pc(13) val texture: SafeModeTexture2D = `object` as SafeModeTexture2D
			if (texture.isAnimated) {
				texture.cycle += elapsed
				@Pc(27) val frames: Int = texture.cycle / 20
				if (frames > 0) {
					@Pc(36) val metrics: MaterialType = super.materialProvider!!.getMetrics(texture.materialId)!!
					texture.animate(metrics.speedU * elapsed * 50 / 1000, metrics.speedV * elapsed * 50 / 1000)
					texture.cycle -= frames * 20
				}
				texture.isAnimated = false
			}
			`object` = this.textureCache.next()
		}
		this.lastTickTime = time
		this.spriteCache.clean(5)
		this.textureCache.clean(5)
	}

	@OriginalMember(owner = "client!ao", name = "t", descriptor = "()Z")
	override fun replaceCanvasOnDisplayUpdate(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!ao", name = "da", descriptor = "(III[I)V")
	override fun da(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) screenCoords: IntArray?) {
		@Pc(24) val depth: Float = this.camera!!.tz + this.camera!!.e3_1 * x.toFloat() + this.camera!!.e3_2 * y.toFloat() + this.camera!!.e3_3 * z.toFloat()
		if (depth < this.zNear.toFloat() || depth > this.zFar.toFloat()) {
			screenCoords!![2] = -1
			screenCoords[1] = -1
			screenCoords[0] = -1
			return
		}
		@Pc(81) val screenX: Int = (this.projectionScaleX.toFloat() * (this.camera!!.tx + this.camera!!.e1_1 * x.toFloat() + this.camera!!.e1_2 * y.toFloat() + this.camera!!.e1_3 * z.toFloat()) / depth).toInt()
		@Pc(113) val screenY: Int = (this.projectionScaleY.toFloat() * (this.camera!!.ty + this.camera!!.e2_1 * x.toFloat() + this.camera!!.e2_2 * y.toFloat() + this.camera!!.e2_3 * z.toFloat()) / depth).toInt()
		if (screenX >= this.viewX1 && screenX <= this.viewX2 && screenY >= this.viewY1 && screenY <= this.viewY2) {
			screenCoords!![0] = screenX - this.viewX1
			screenCoords[1] = screenY - this.viewY1
			screenCoords[2] = depth.toInt()
		} else {
			screenCoords!![2] = -1
			screenCoords[1] = -1
			screenCoords[0] = -1
		}
	}

	@OriginalMember(owner = "client!ao", name = "q", descriptor = "()V")
	override fun renderLastFrame() {
	}

	@OriginalMember(owner = "client!ao", name = "f", descriptor = "()Z")
	override fun hasTextures(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!ao", name = "l", descriptor = "()V")
	override fun cacheReset() {
		this.textureCache.reset()
		this.spriteCache.reset()
	}

	@OriginalMember(owner = "client!ao", name = "g", descriptor = "(II)I")
	override fun combineFunctionMasks(@OriginalArg(0) maskA: Int, @OriginalArg(1) maskB: Int): Int {
		return maskA or maskB
	}

	@OriginalMember(owner = "client!ao", name = "o", descriptor = "(I)Z")
	fun smallTexture(@OriginalArg(0) id: Int): Boolean {
		return this.shrinkTextures || super.materialProvider!!.getMetrics(id)!!.isSmall
	}
}
