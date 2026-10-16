// The warnings of the original (deobfuscated) code, left as it was: the build treats warnings as errors.
@file:Suppress("DEPRECATION", "SENSELESS_COMPARISON")

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
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Rectangle
import java.lang.reflect.Method
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.utils.ColorUtil
import com.jagex.game.runetek5.NativeLibraryOwner

@OriginalClass("client!oa")
actual class oa : Toolkit, SoftwareObject {

	@OriginalMember(owner = "client!oa", name = "x", descriptor = "Lclient!ya;")
	private var aYa2: ya? = null

	@OriginalMember(owner = "client!oa", name = "B", descriptor = "Lclient!bca;")
	private var matrix: Matrix? = null

	@OriginalMember(owner = "client!oa", name = "N", descriptor = "Lclient!p;")
	private var aP1: p? = null

	@OriginalMember(owner = "client!oa", name = "R", descriptor = "I")
	@JvmField
	actual var anInt7473: Int = 0

	@OriginalMember(owner = "client!oa", name = "T", descriptor = "[Lclient!a;")
	private var anAArray1: Array<a?>? = null

	// The deobfuscated Java declared this `final long nativeid = 0L`, so javac replaced every read with 0
	// and the client never saw the id the natives store here. The original client (and void-client) keep
	// it a plain field and read it; so does this one.
	@OriginalMember(owner = "client!oa", name = "nativeid", descriptor = "J")
	@JvmField
	actual var nativeid: Long = 0L

	@OriginalMember(owner = "client!oa", name = "A", descriptor = "Z")
	private var aBoolean541: Boolean = false

	@OriginalMember(owner = "client!oa", name = "z", descriptor = "Lclient!uh;")
	private val aClass341_44: Deque = Deque()

	@OriginalMember(owner = "client!oa", name = "D", descriptor = "I")
	private var anInt7471: Int = 4096

	@OriginalMember(owner = "client!oa", name = "E", descriptor = "I")
	private var anInt7472: Int = 4096

	@OriginalMember(owner = "client!oa", name = "C", descriptor = "Lclient!eq;")
	private val aClass99_52: IterableHashTable = IterableHashTable(4)

	@OriginalMember(owner = "client!oa", name = "S", descriptor = "Z")
	private var aBoolean542: Boolean = false

	@OriginalMember(owner = "client!oa", name = "H", descriptor = "Lclient!bca;")
	private var aClass30_11: Matrix? = null

	@OriginalMember(owner = "client!oa", name = "<init>", descriptor = "(Ljava/awt/Canvas;Lclient!d;II)V")
	actual constructor(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) textureSource: MaterialProvider?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) : super(textureSource) {
		try {
			if (!NativeLibraryOwner.loadLibrary("sw3d")) {
				throw RuntimeException("")
			}
			SoftwareMemoryManager.instanceCreated()
			this.MA(super.materialProvider, 0, 0)
			ColorUtil.initColorTables(false, true)
			this.aBoolean542 = true
			this.aClass30_11 = ja()
			this.setCamera(ja())
			this.allocateThreads(1)
			this.linkThreads(0)
			if (canvas != null) {
				this.addCanvas(canvas, arg2, arg3)
				this.setCanvas(canvas)
			}
		} catch (@Pc(80) ex: Throwable) {
			this.free()
			throw RuntimeException()
		}
	}

	@OriginalMember(owner = "client!oa", name = "MA", descriptor = "(Lclient!d;II)V")
	private external fun MA(@OriginalArg(0) arg0: MaterialProvider?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIII)V")
	actual override fun outlineRect(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int) {
		this.U(arg0, arg1, arg2, arg4, arg5)
		this.U(arg0, arg1 + arg3 - 1, arg2, arg4, arg5)
		this.P(arg0, arg1 + 1, arg3 - 1, arg4, arg5)
		this.P(arg0 + arg2 - 1, arg1 + 1, arg3 - 1, arg4, arg5)
	}

	@OriginalMember(owner = "client!oa", name = "ma", descriptor = "(J)V")
	private external fun ma(@OriginalArg(0) arg0: Long)

	@OriginalMember(owner = "client!oa", name = "d", descriptor = "(IIIIII)Lclient!wh;")
	actual override fun createCubeTexture(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int): EnvironmentSampler? {
		return null
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!za;)V")
	actual override fun setHeap(@OriginalArg(0) memoryPool: MemoryPool?) {
		this.aYa2 = memoryPool as ya?
		this.va(memoryPool)
	}

	@OriginalMember(owner = "client!oa", name = "w", descriptor = "(Z)V")
	actual external override fun w(@OriginalArg(0) arg0: Boolean)

	@OriginalMember(owner = "client!oa", name = "A", descriptor = "(ILclient!aa;II)V")
	actual external override fun A(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: ComponentSurface?, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!oa", name = "aa", descriptor = "(IIIIII)V")
	actual external override fun aa(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int)

	@OriginalMember(owner = "client!oa", name = "v", descriptor = "()V")
	actual override fun compositeBloomEffects() {
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	@Throws(FlipException::class)
	actual override fun flipDirtyRect(@OriginalArg(0) rectangles: Array<Rectangle?>?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
		if (this.aP1 == null) {
			throw IllegalStateException("off")
		}
		if (ClientFeatures.scaled()) {
			// Not part of the original client: the whole scaled frame is drawn; see flip.
			this.flip(arg2, arg3)
			return
		}
		this.aP1!!.method6665(rectangles, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!oa", name = "z", descriptor = "()Z")
	actual override fun method7311(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!mda;I)V")
	actual override fun renderOrtho(@OriginalArg(0) particleList: ParticleList?, @OriginalArg(1) arg1: Int) {
		this.method6407(particleList)
		this.method6405()!!.method14(this, NativeModelScratch.particlePositions, NativeModelScratch.particleColours, NativeModelScratch.particleSizes, NativeModelScratch.particleMaterialIds, particleList!!.particles.size())
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIIIIIIIIII)V")
	actual override fun drawTriangle() {
	}

	@OriginalMember(owner = "client!oa", name = "l", descriptor = "()V")
	actual override fun cacheReset() {
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!rh;Z)Lclient!mi;")
	actual override fun generateSprite(@OriginalArg(0) indexedImage: SpriteType?, @OriginalArg(1) arg1: Boolean): NativeSprite? {
		@Pc(17) val local17: j = j(this, indexedImage!!.pallete, indexedImage.pixels, indexedImage.alpha, 0, indexedImage.width, indexedImage.width, indexedImage.height)
		local17.setOffsets(indexedImage.minX, indexedImage.minY, indexedImage.borderX, indexedImage.borderY)
		return local17
	}

	@OriginalMember(owner = "client!oa", name = "HA", descriptor = "(IIII[I)V")
	actual external override fun HA(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: IntArray?)

	@OriginalMember(owner = "client!oa", name = "A", descriptor = "()V")
	actual override fun stopBloom() {
	}

	@OriginalMember(owner = "client!oa", name = "AA", descriptor = "(SSIBBIZBBBBZZZZZBZZI)V")
	private external fun AA(@OriginalArg(0) arg0: Short, @OriginalArg(1) arg1: Short, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Byte, @OriginalArg(4) arg4: Byte, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Boolean, @OriginalArg(7) arg7: Byte, @OriginalArg(8) arg8: Byte, @OriginalArg(9) arg9: Byte, @OriginalArg(10) arg10: Byte, @OriginalArg(11) arg11: Boolean, @OriginalArg(12) arg12: Boolean, @OriginalArg(13) arg13: Boolean, @OriginalArg(14) arg14: Boolean, @OriginalArg(15) arg15: Boolean, @OriginalArg(16) arg16: Byte, @OriginalArg(17) arg17: Boolean, @OriginalArg(18) arg18: Boolean, @OriginalArg(19) arg19: Int)

	@OriginalMember(owner = "client!oa", name = "h", descriptor = "(I)V")
	actual override fun setTextureSize(@OriginalArg(0) arg0: Int) {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!oa", name = "H", descriptor = "(III[I)V")
	actual external override fun H(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: IntArray?)

	@OriginalMember(owner = "client!oa", name = "g", descriptor = "()V")
	private fun g() {
		System.gc()
		System.runFinalization()
		SoftwareMemoryManager.freePending()
	}

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(IIIID)V")
	actual external override fun b(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Double)

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(FFF)V")
	actual override fun setBloomParams(@OriginalArg(0) arg0: Float, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float) {
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!oea;[Lclient!rh;Z)Lclient!da;")
	actual override fun createFontRenderer(@OriginalArg(0) fontMetrics: FontMetrics?, @OriginalArg(1) indexedImages: Array<SpriteType?>?): AbstractFontRenderer? {
		@Pc(3) val local3: IntArray = IntArray(indexedImages!!.size)
		@Pc(7) val local7: IntArray = IntArray(indexedImages.size)
		@Pc(9) var local9: Boolean = false
		@Pc(11) var i: Int = 0
		while (i < indexedImages.size) {
			local3[i] = indexedImages[i]!!.width
			local7[i] = indexedImages[i]!!.height
			if (indexedImages[i]!!.alpha != null) {
				local9 = true
			}
			i++
		}
		if (local9) {
			throw IllegalArgumentException("Cannot specify alpha with non-mono font unless someone writes it")
		}
		return h(this, this.aYa2, fontMetrics, indexedImages, null)
	}

	@OriginalMember(owner = "client!oa", name = "wa", descriptor = "(IIIIII)V")
	private external fun wa(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int)

	@OriginalMember(owner = "client!oa", name = "f", descriptor = "()Z")
	actual override fun hasTextures(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!oa", name = "B", descriptor = "()Lclient!a;")
	actual fun method6405(): a? {
		@Pc(1) var i: Int = 0
		while (i < this.anInt7473) {
			if (this.anAArray1!![i]!!.aRunnable1 === Thread.currentThread()) {
				return this.anAArray1!![i]
			}
			i++
		}
		return null
	}

	@OriginalMember(owner = "client!oa", name = "t", descriptor = "(Lclient!p;)V")
	private external fun t(@OriginalArg(0) arg0: p?)

	@OriginalMember(owner = "client!oa", name = "y", descriptor = "()Lclient!bca;")
	actual override fun scratchMatrix(): Matrix? {
		return this.aClass30_11
	}

	@OriginalMember(owner = "client!oa", name = "WA", descriptor = "(S)Z")
	private fun WA(@OriginalArg(0) arg0: Short): Boolean {
		@Pc(2) val textureSource: MaterialProvider? = super.materialProvider
		synchronized(super.materialProvider!!) {
			if (!super.materialProvider!!.textureAvailable(arg0.toInt())) {
				return false
			}
			@Pc(22) val textureMetrics: MaterialType? = super.materialProvider!!.getMetrics(arg0.toInt())
			if (textureMetrics == null) {
				return false
			}
			@Pc(44) val local44: IntArray?
			if (textureMetrics.effectCombiner == 2) {
				local44 = super.materialProvider!!.getPixelsArgb(128, 0.7F, arg0.toInt(), 128)
			} else {
				local44 = super.materialProvider!!.getPixels(true, arg0.toInt(), 0.7F, 128, 128)
			}
			this.CA(arg0, local44, textureMetrics.colorHsl, textureMetrics.effectCombiner, textureMetrics.effectType, textureMetrics.effectParam1, textureMetrics.effectParam2, textureMetrics.isSmall, textureMetrics.alpha, textureMetrics.brightness, textureMetrics.speedU, textureMetrics.speedV, textureMetrics.detailsOnly, textureMetrics.skipTriangles, textureMetrics.unusedFlag, textureMetrics.repeatS, textureMetrics.repeatT, textureMetrics.mipmapping, textureMetrics.hdr, textureMetrics.transposed, textureMetrics.combineMode)
			return true
		}
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!mda;Z)V")
	private fun method6407(@OriginalArg(0) particleList: ParticleList?) {
		@Pc(1) var local1: Int = 0
		@Pc(3) var local3: Int = 0
		@Pc(5) var local5: Int = 0
		@Pc(7) var local7: Int = 0
		@Pc(15) var particle: PointEntity? = particleList!!.particles.first() as PointEntity?
		while (particle != null) {
			NativeModelScratch.particlePositions[local1++] = particle.x
			NativeModelScratch.particlePositions[local1++] = particle.y
			NativeModelScratch.particlePositions[local1++] = particle.z
			NativeModelScratch.particleColours[local3++] = particle.colorArgb
			NativeModelScratch.particleMaterialIds[local7++] = particle.materialId.toShort()
			NativeModelScratch.particleSizes[local5++] = particle.size
			particle = particleList.particles.next() as PointEntity?
		}
	}

	@OriginalMember(owner = "client!oa", name = "e", descriptor = "(I)V")
	actual override fun linkThreads(@OriginalArg(0) arg0: Int) {
		this.anAArray1!![arg0]!!.method13()
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "([I)V")
	actual override fun accumulateViewportDimensions(@OriginalArg(0) arg0: IntArray?) {
		@Pc(4) val dimension: Dimension = this.aP1!!.canvas!!.size
		arg0!![0] = dimension.width
		arg0[1] = dimension.height
	}

	@OriginalMember(owner = "client!oa", name = "CA", descriptor = "(S[ISIBBIZBBBBZZZZZBZZI)V")
	private external fun CA(@OriginalArg(0) arg0: Short, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: Short, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Byte, @OriginalArg(5) arg5: Byte, @OriginalArg(6) arg6: Int, @OriginalArg(7) arg7: Boolean, @OriginalArg(8) arg8: Byte, @OriginalArg(9) arg9: Byte, @OriginalArg(10) arg10: Byte, @OriginalArg(11) arg11: Byte, @OriginalArg(12) arg12: Boolean, @OriginalArg(13) arg13: Boolean, @OriginalArg(14) arg14: Boolean, @OriginalArg(15) arg15: Boolean, @OriginalArg(16) arg16: Boolean, @OriginalArg(17) arg17: Byte, @OriginalArg(18) arg18: Boolean, @OriginalArg(19) arg19: Boolean, @OriginalArg(20) arg20: Int)

	@OriginalMember(owner = "client!oa", name = "DA", descriptor = "(IIII)V")
	actual external override fun DA(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!oa", name = "i", descriptor = "(I)Lclient!za;")
	actual override fun createHeap(@OriginalArg(0) arg0: Int): MemoryPool? {
		@Pc(5) val local5: ya = ya(this, arg0)
		this.aClass341_44.addLast(local5)
		return local5
	}

	@OriginalMember(owner = "client!oa", name = "c", descriptor = "(I)V")
	actual override fun initialize() {
		this.anInt7471 = 10000
		this.anInt7472 = this.anInt7471
		if (this.anInt7473 > 1) {
			throw IllegalStateException("No MT")
		}
		this.allocateThreads(this.anInt7473)
		this.linkThreads(0)
	}

	@OriginalMember(owner = "client!oa", name = "e", descriptor = "(II)I")
	actual override fun compareFunctionMasks(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): Int {
		@Pc(3) val local3: Int = arg0 and 0xFFFFF
		@Pc(7) val local7: Int = arg1 and 0xFFFFF
		return (local3 and local7) xor local7
	}

	@OriginalMember(owner = "client!oa", name = "xa", descriptor = "(F)V")
	actual external override fun xa(@OriginalArg(0) arg0: Float)

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(Ljava/awt/Canvas;)V")
	actual override fun releaseSurface(@OriginalArg(0) canvas: Canvas?) {
		if (this.aP1!!.canvas === canvas) {
			this.setCanvas(null)
		}
		@Pc(16) val local16: p? = this.aClass99_52.get(canvas!!.hashCode().toLong()) as p?
		if (local16 != null) {
			local16.unlink()
			local16.method6667()
		}
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIIILclient!aa;II)V")
	actual override fun drawSurfaceLine(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(6) clippingMask: ComponentSurface?, @OriginalArg(7) arg6: Int, @OriginalArg(8) arg7: Int) {
		this.Z(arg0, arg1, arg2, arg3, arg4, 1, clippingMask, arg6, arg7)
	}

	@OriginalMember(owner = "client!oa", name = "finalize", descriptor = "()V")
	@Synchronized
	actual override fun finalize() {
		this.free()
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this)
		}
	}

	@OriginalMember(owner = "client!oa", name = "d", descriptor = "(I)V")
	private external fun d(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!oa", name = "i", descriptor = "()I")
	actual external override fun i(): Int

	@OriginalMember(owner = "client!oa", name = "n", descriptor = "(JJ)V")
	private external fun n(@OriginalArg(0) arg0: Long, @OriginalArg(1) arg1: Long)

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!mda;)V")
	actual override fun render(@OriginalArg(0) particleList: ParticleList?) {
		if (particleList!!.particles.size() != 0) {
			this.method6407(particleList)
			this.method6405()!!.method14(this, NativeModelScratch.particlePositions, NativeModelScratch.particleColours, NativeModelScratch.particleSizes, NativeModelScratch.particleMaterialIds, particleList.particles.size())
		}
	}

	@OriginalMember(owner = "client!oa", name = "x", descriptor = "()I")
	actual override fun getMaxLights(): Int {
		return 4
	}

	@OriginalMember(owner = "client!oa", name = "JA", descriptor = "(IIIIII)I")
	actual external override fun JA(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int): Int

	@OriginalMember(owner = "client!oa", name = "h", descriptor = "()V")
	actual override fun stop() {
		if (this.aBoolean541) {
			return
		}
		this.anAArray1 = null
		this.aP1 = null
		this.aYa2 = null
		this.aClass30_11 = null
		this.aClass99_52.clear()
		@Pc(25) var local25: ya? = this.aClass341_44.first() as ya?
		while (local25 != null) {
			local25.ga()
			local25 = this.aClass341_44.next() as ya?
		}
		this.aClass341_44.clear()
		this.FA()
		if (this.aBoolean542) {
			ColorUtil.releaseColorTables(true, false)
			this.aBoolean542 = false
		}
		this.g()
		SoftwareMemoryManager.instanceDestroyed()
		this.aBoolean541 = true
	}

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(Ljava/awt/Canvas;II)V")
	actual override fun resizeCanvas(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
		@Pc(8) val local8: p? = this.aClass99_52.get(canvas!!.hashCode().toLong()) as p?
		local8!!.method6666(canvas, arg1, arg2)
		if (canvas != null && canvas === this.aP1!!.canvas) {
			this.setCanvas(canvas)
		}
	}

	@OriginalMember(owner = "client!oa", name = "c", descriptor = "(S)Z")
	private fun c(@OriginalArg(0) arg0: Short): Boolean {
		synchronized(this) {
			@Pc(9) val textureMetrics: MaterialType? = super.materialProvider!!.getMetrics(arg0.toInt())
			if (textureMetrics == null) {
				return false
			} else {
				this.AA(arg0, textureMetrics.colorHsl, textureMetrics.effectCombiner, textureMetrics.effectType, textureMetrics.effectParam1, textureMetrics.effectParam2, textureMetrics.isSmall, textureMetrics.alpha, textureMetrics.brightness, textureMetrics.speedU, textureMetrics.speedV, textureMetrics.detailsOnly, textureMetrics.skipTriangles, textureMetrics.unusedFlag, textureMetrics.repeatS, textureMetrics.repeatT, textureMetrics.mipmapping, textureMetrics.hdr, textureMetrics.transposed, textureMetrics.combineMode)
				return true
			}
		}
	}

	@OriginalMember(owner = "client!oa", name = "m", descriptor = "()Z")
	actual override fun bloom(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!oa", name = "Q", descriptor = "(IIIIII[BII)V")
	actual external override fun Q(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: ByteArray?, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int)

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIZ)Lclient!mi;")
	actual override fun generateEmptySprite(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Boolean): NativeSprite? {
		return j(this, arg0, arg1)
	}

	@OriginalMember(owner = "client!oa", name = "ya", descriptor = "()V")
	actual external override fun ya()

	@OriginalMember(owner = "client!oa", name = "I", descriptor = "()I")
	actual external override fun I(): Int

	@OriginalMember(owner = "client!oa", name = "f", descriptor = "(II)V")
	actual external override fun f(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

	@OriginalMember(owner = "client!oa", name = "KA", descriptor = "(IIII)V")
	actual external override fun KA(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(I)V")
	actual override fun tick(@OriginalArg(0) arg0: Int) {
		SoftwareMemoryManager.freePending()
		this.d(arg0)
		@Pc(10) var local10: ya? = this.aClass341_44.first() as ya?
		while (local10 != null) {
			local10.r()
			local10 = this.aClass341_44.next() as ya?
		}
	}

	@OriginalMember(owner = "client!oa", name = "g", descriptor = "(II)I")
	actual override fun combineFunctionMasks(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): Int {
		return arg0 or arg1
	}

	@OriginalMember(owner = "client!oa", name = "ra", descriptor = "(IIII)V")
	actual external override fun ra(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!oa", name = "E", descriptor = "()I")
	actual external override fun E(): Int

	@OriginalMember(owner = "client!oa", name = "pa", descriptor = "()V")
	actual external override fun pa()

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIIII)V")
	actual override fun strongLine(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int) {
		this.method6405()!!.method9(this, arg0, arg1, arg2, arg3, arg4, arg5)
	}

	@OriginalMember(owner = "client!oa", name = "GA", descriptor = "(I)V")
	actual external override fun GA(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!oa", name = "n", descriptor = "()Z")
	actual override fun extendBuildArea(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!oa", name = "X", descriptor = "(I)V")
	actual external override fun X(@OriginalArg(0) arg0: Int)

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!bh;IIII)Lclient!ka;")
	actual override fun createModel(@OriginalArg(0) mesh: Mesh?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int): Model? {
		return i(this, this.aYa2, mesh, arg1, arg2, arg3, arg4)
	}

	@OriginalMember(owner = "client!oa", name = "c", descriptor = "()Lclient!fia;")
	actual override fun renderer(): Renderer? {
		return Renderer(0, "SSE", 1, "CPU", 0L)
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIIIIII)V")
	actual override fun drawLine(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(6) arg5: Int, @OriginalArg(7) arg6: Int, @OriginalArg(8) arg7: Int) {
	}

	@OriginalMember(owner = "client!oa", name = "F", descriptor = "(II)V")
	actual external override fun F(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int)

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(IIIIZ)Lclient!mi;")
	actual override fun createNativeSprite(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Boolean): NativeSprite? {
		return j(this, arg0, arg1, arg2, arg3, !arg4)
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(I)V")
	actual override fun allocateThreads(@OriginalArg(0) arg0: Int) {
		this.anInt7473 = arg0
		this.anAArray1 = arrayOfNulls<a>(this.anInt7473)
		@Pc(9) var i: Int = 0
		while (i < this.anInt7473) {
			this.anAArray1!![i] = a(this, this.anInt7472, this.anInt7471)
			i++
		}
	}

	@OriginalMember(owner = "client!oa", name = "r", descriptor = "(IIIIIII)I")
	actual external override fun r(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: Int): Int

	@OriginalMember(owner = "client!oa", name = "va", descriptor = "(Lclient!za;)V")
	private external fun va(@OriginalArg(0) arg0: MemoryPool?)

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Ljava/awt/Canvas;II)V")
	actual override fun addCanvas(@OriginalArg(0) canvas: Canvas?, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int) {
		@Pc(8) var local8: p? = this.aClass99_52.get(canvas!!.hashCode().toLong()) as p?
		if (local8 == null) {
			try {
				@Pc(13) val class2: Class<*> = Class.forName("java.awt.Canvas")
				@Pc(23) val method: Method = class2.getMethod("setIgnoreRepaint", java.lang.Boolean.TYPE)
				method.invoke(canvas, java.lang.Boolean.TRUE)
			} catch (@Pc(35) ex: Exception) {
			}
			local8 = p(this, canvas, arg1, arg2)
			this.aClass99_52.put(canvas.hashCode().toLong(), local8)
		} else if (local8.anInt7778 != arg1 || local8.anInt7779 != arg2) {
			local8.method6666(canvas, arg1, arg2)
		}
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!wh;Lclient!wh;FLclient!wh;)Lclient!wh;")
	actual override fun drawQuad(@OriginalArg(0) environmentSampler: EnvironmentSampler?, @OriginalArg(1) environmentSampler2: EnvironmentSampler?, @OriginalArg(2) arg2: Float, @OriginalArg(3) environmentSampler3: EnvironmentSampler?): EnvironmentSampler? {
		return null
	}

	@OriginalMember(owner = "client!oa", name = "K", descriptor = "([I)V")
	actual external override fun K(@OriginalArg(0) arg0: IntArray?)

	@OriginalMember(owner = "client!oa", name = "t", descriptor = "()Z")
	actual override fun replaceCanvasOnDisplayUpdate(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(II)V")
	@Throws(FlipException::class)
	actual override fun flip(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int) {
		if (this.aP1 == null) {
			throw IllegalStateException("off")
		}
		if (ClientFeatures.scaled()) {
			// Not part of the original client: with client.scale the frame is read back from the
			// native renderer and drawn through the canvas's scaled graphics, since the native
			// copy to the window cannot scale.
			this.flipScaled(arg0, arg1)
			return
		}
		this.aP1!!.method6664(arg0, arg1)
	}

	// Not part of the original client: the Java image the frame is read back into with client.scale.
	private var scaledSurface: JavaSurface? = null

	private fun flipScaled(x: Int, y: Int) {
		val width: Int = this.aP1!!.anInt7778
		val height: Int = this.aP1!!.anInt7779
		if (width <= 0 || height <= 0) {
			return
		}
		if (this.scaledSurface == null || this.scaledSurface!!.width != width || this.scaledSurface!!.height != height) {
			this.scaledSurface = JavaSurfaceFactory.createSurface(height, this.aP1!!.canvas, width)
		}
		val pixels: IntArray? = this.na(0, 0, width, height)
		if (pixels == null || pixels.size < width * height) {
			return
		}
		System.arraycopy(pixels, 0, this.scaledSurface!!.raster, 0, width * height)
		try {
			val graphics: Graphics = this.aP1!!.canvas!!.graphics
			this.scaledSurface!!.clip(0, x, height, graphics, 0, width, y)
			graphics.dispose()
		} catch (ex: Exception) {
			this.aP1!!.canvas!!.repaint()
		}
	}

	@OriginalMember(owner = "client!oa", name = "d", descriptor = "(II)Lclient!he;")
	actual override fun method7268(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): Sprite? {
		return this.generateEmptySprite(arg0, arg1, false)
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(I[Lclient!ab;)V")
	actual override fun copyLights(@OriginalArg(0) arg0: Int, @OriginalArg(1) pointLights: Array<PointLight?>?) {
		@Pc(1) var local1: Int = 0
		@Pc(3) var i: Int = 0
		while (i < arg0) {
			NativeModelScratch.pointLightData[local1++] = pointLights!![i]!!.getX()
			NativeModelScratch.pointLightData[local1++] = pointLights[i]!!.getY()
			NativeModelScratch.pointLightData[local1++] = pointLights[i]!!.getZ()
			NativeModelScratch.pointLightData[local1++] = pointLights[i]!!.getRadius()
			NativeModelScratch.pointLightIntensities[i] = pointLights[i]!!.getIntensity()
			NativeModelScratch.pointLightData[local1++] = pointLights[i]!!.getColour()
			i++
		}
		this.N(arg0, NativeModelScratch.pointLightData, NativeModelScratch.pointLightIntensities)
	}

	@OriginalMember(owner = "client!oa", name = "za", descriptor = "(IIIII)V")
	actual external override fun za(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int)

	@OriginalMember(owner = "client!oa", name = "na", descriptor = "(IIII)[I")
	actual external override fun na(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int): IntArray?

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(Z)V")
	actual override fun method7250(@OriginalArg(0) arg0: Boolean) {
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIIF)Lclient!ab;")
	actual override fun createPointLight(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) range: Int, @OriginalArg(5) intensity: Float): PointLight? {
		return SsePointLight(arg0, arg1, arg2, arg3, range, intensity)
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Ljava/awt/Canvas;)V")
	actual override fun setCanvas(@OriginalArg(0) canvas: Canvas?) {
		if (canvas == null) {
			this.aP1 = null
			this.t(null)
		} else {
			@Pc(10) val local10: p? = this.aClass99_52.get(canvas.hashCode().toLong()) as p?
			this.aP1 = local10
			this.t(local10)
		}
	}

	@OriginalMember(owner = "client!oa", name = "r", descriptor = "()Z")
	actual override fun hardShadow(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!wh;)V")
	actual override fun setSunCubeTexture(@OriginalArg(0) environmentSampler: EnvironmentSampler?) {
	}

	@OriginalMember(owner = "client!oa", name = "j", descriptor = "()Lclient!bca;")
	actual override fun createMatrix(): Matrix? {
		return ja()
	}

	@OriginalMember(owner = "client!oa", name = "f", descriptor = "(I)V")
	actual override fun method7281(@OriginalArg(0) arg0: Int) {
		this.anAArray1!![arg0]!!.method15()
	}

	@OriginalMember(owner = "client!oa", name = "EA", descriptor = "(IIII)V")
	actual external override fun EA(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "()V")
	actual override fun finish() {
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIII)V")
	actual override fun method7261(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int) {
	}

	@OriginalMember(owner = "client!oa", name = "FA", descriptor = "()V")
	private external fun FA()

	@OriginalMember(owner = "client!oa", name = "P", descriptor = "(IIIII)V")
	actual external override fun P(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int)

	@OriginalMember(owner = "client!oa", name = "u", descriptor = "()Z")
	actual override fun forceFixedScreenmode(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!oa", name = "U", descriptor = "(IIIII)V")
	actual external override fun U(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int)

	@OriginalMember(owner = "client!oa", name = "Y", descriptor = "()[I")
	actual external override fun Y(): IntArray?

	@OriginalMember(owner = "client!oa", name = "ZA", descriptor = "(IFFFFF)V")
	actual external override fun ZA(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Float, @OriginalArg(2) arg2: Float, @OriginalArg(3) arg3: Float, @OriginalArg(4) arg4: Float, @OriginalArg(5) arg5: Float)

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!he;Lclient!vm;)Lclient!is;")
	actual override fun createOffscreenSurface(@OriginalArg(0) surface: Sprite?, @OriginalArg(1) depthBuffer: DepthBuffer?): OffscreenSurface? {
		return wa(this, surface as j?, depthBuffer as xa?)
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!is;)V")
	actual override fun swapSurface(@OriginalArg(0) offscreenSurface: OffscreenSurface?) {
		@Pc(2) val local2: wa? = offscreenSurface as wa?
		this.n(local2!!.aJ1!!.nativeid, local2.aXa1!!.nativeid)
	}

	@OriginalMember(owner = "client!oa", name = "da", descriptor = "(III[I)V")
	actual external override fun da(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: IntArray?)

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(II[I[I)Lclient!aa;")
	actual override fun createMask(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: IntArray?, @OriginalArg(3) arg3: IntArray?): ComponentSurface? {
		return na(this, this.aYa2, arg0, arg1, arg2, arg3)
	}

	@OriginalMember(owner = "client!oa", name = "e", descriptor = "()Lclient!bca;")
	actual override fun camera(): Matrix? {
		return this.matrix
	}

	@OriginalMember(owner = "client!oa", name = "e", descriptor = "(IIIIII)V")
	actual override fun line(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int) {
		this.wa(arg0, arg1, arg2, arg3, arg4, arg5)
	}

	@OriginalMember(owner = "client!oa", name = "Z", descriptor = "(IIIIIILclient!aa;II)V")
	private external fun Z(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(5) arg5: Int, @OriginalArg(6) arg6: ComponentSurface?, @OriginalArg(7) arg7: Int, @OriginalArg(8) arg8: Int)

	@OriginalMember(owner = "client!oa", name = "C", descriptor = "(Z)V")
	actual external override fun C(@OriginalArg(0) arg0: Boolean)

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(II)Lclient!vm;")
	actual override fun createDepthBuffer(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int): DepthBuffer? {
		return xa(arg0, arg1)
	}

	@OriginalMember(owner = "client!oa", name = "d", descriptor = "()Z")
	actual override fun stillwaterEffectLoaded(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!oa", name = "L", descriptor = "(III)V")
	actual external override fun L(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int)

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "([IIIIIZ)Lclient!mi;")
	actual override fun createNativeSprite(@OriginalArg(0) arg0: IntArray?, @OriginalArg(2) arg1: Int, @OriginalArg(3) arg2: Int, @OriginalArg(4) arg3: Int, @OriginalArg(5) arg4: Boolean): NativeSprite? {
		return j(this, arg0, 0, arg1, arg2, arg3, false)
	}

	@OriginalMember(owner = "client!oa", name = "la", descriptor = "()V")
	actual external override fun la()

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Z)V")
	actual override fun setShrinkTextures(@OriginalArg(0) arg0: Boolean) {
	}

	@OriginalMember(owner = "client!oa", name = "M", descriptor = "()I")
	actual external override fun M(): Int

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "()Z")
	actual override fun supportsBloom(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!oa", name = "OA", descriptor = "()Ljava/lang/Object;")
	private fun OA(): Any? {
		return ba(this)
	}

	@OriginalMember(owner = "client!oa", name = "k", descriptor = "()Z")
	actual override fun supportsFullscreen(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIIILclient!aa;IIIII)V")
	actual override fun drawTexturedLine(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int, @OriginalArg(4) arg4: Int, @OriginalArg(6) clippingMask: ComponentSurface?, @OriginalArg(7) arg6: Int, @OriginalArg(8) arg7: Int, @OriginalArg(9) arg8: Int, @OriginalArg(10) arg9: Int, @OriginalArg(11) arg10: Int) {
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!bca;)V")
	actual override fun setCamera(@OriginalArg(0) matrix: Matrix?) {
		this.matrix = matrix
		this.ma((matrix as ja).nativeid)
	}

	@OriginalMember(owner = "client!oa", name = "p", descriptor = "()V")
	actual override fun restoreSurface() {
		this.setCanvas(this.aP1!!.canvas)
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(II[[I[[IIII)Lclient!s;")
	actual override fun createGround(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) tileHeights: Array<IntArray?>?, @OriginalArg(3) arg3: Array<IntArray?>?, @OriginalArg(5) arg4: Int, @OriginalArg(6) arg5: Int): Ground? {
		return t(this, this.aYa2, arg0, arg1, tileHeights, arg3, 512, arg4, arg5)
	}

	@OriginalMember(owner = "client!oa", name = "XA", descriptor = "()I")
	actual external override fun XA(): Int

	@OriginalMember(owner = "client!oa", name = "N", descriptor = "(I[I[F)V")
	private external fun N(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: IntArray?, @OriginalArg(2) arg2: FloatArray?)

	@OriginalMember(owner = "client!oa", name = "T", descriptor = "(IIII)V")
	actual external override fun T(@OriginalArg(0) arg0: Int, @OriginalArg(1) arg1: Int, @OriginalArg(2) arg2: Int, @OriginalArg(3) arg3: Int)

	@OriginalMember(owner = "client!oa", name = "s", descriptor = "()Z")
	actual override fun utilisesNativeHeap(): Boolean {
		return true
	}

	@OriginalMember(owner = "client!oa", name = "q", descriptor = "()V")
	actual override fun renderLastFrame() {
	}

	@OriginalMember(owner = "client!oa", name = "o", descriptor = "()Z")
	actual override fun hasBloom(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!oa", name = "w", descriptor = "()Z")
	actual override fun supportsAntiAliasing(): Boolean {
		return false
	}
}

