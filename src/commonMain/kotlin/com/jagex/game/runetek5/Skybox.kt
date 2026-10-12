package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.config.material.MaterialType
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.config.sun.Sun
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField

@OriginalClass("client!wr")
class Skybox @OriginalMember(owner = "client!wr", name = "<init>", descriptor = "(I[Lclient!ki;IIII)V") constructor(@OriginalArg(0) materialId: Int, @OriginalArg(1) skyBoxSpheres: Array<Sun?>?, @OriginalArg(2) defaultSunIndex: Int, @OriginalArg(3) positionX: Int, @OriginalArg(4) positionY: Int, @OriginalArg(5) positionZ: Int) {

	@OriginalMember(owner = "client!wr", name = "a", descriptor = "I")
	private var availableSunCount: Int = 0

	@OriginalMember(owner = "client!wr", name = "l", descriptor = "I")
	private var spriteSize: Int = 0

	@OriginalMember(owner = "client!wr", name = "p", descriptor = "Lclient!mi;")
	private var backgroundSprite: NativeSprite? = null

	@OriginalMember(owner = "client!wr", name = "f", descriptor = "Z")
	private var prepareSun: Boolean = true

	@OriginalMember(owner = "client!wr", name = "h", descriptor = "I")
	private var lastComponentHeight: Int = -1

	@OriginalMember(owner = "client!wr", name = "b", descriptor = "I")
	private val positionZ: Int

	@OriginalMember(owner = "client!wr", name = "g", descriptor = "I")
	private val materialId: Int

	@OriginalMember(owner = "client!wr", name = "k", descriptor = "[Lclient!ki;")
	private val skyBoxSpheres: Array<Sun?>?

	@OriginalMember(owner = "client!wr", name = "c", descriptor = "I")
	private val positionY: Int

	@OriginalMember(owner = "client!wr", name = "o", descriptor = "I")
	private val positionX: Int

	@OriginalMember(owner = "client!wr", name = "n", descriptor = "[Lclient!ki;")
	private val availableSuns: Array<Sun?>?

	@OriginalMember(owner = "client!wr", name = "t", descriptor = "Lclient!ki;")
	private val activeSun: Sun?

	init {
		this.positionZ = positionZ
		this.materialId = materialId
		this.skyBoxSpheres = skyBoxSpheres
		this.positionY = positionY
		this.positionX = positionX
		if (skyBoxSpheres == null) {
			this.availableSuns = null
			this.activeSun = null
		} else {
			this.availableSuns = arrayOfNulls<Sun>(skyBoxSpheres.size)
			this.activeSun = if (defaultSunIndex < 0) null else skyBoxSpheres[defaultSunIndex]
		}
	}

	@OriginalMember(owner = "client!wr", name = "a", descriptor = "(ILclient!ha;I)Z")
	fun updateForHeight(@OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) componentHeight: Int): Boolean {
		if (componentHeight != this.lastComponentHeight) {
			this.lastComponentHeight = componentHeight
			@Pc(16) var size: Int = Utils.floorPowerOfTwo(componentHeight)
			if (size > 512) {
				size = 512
			}
			if (size <= 0) {
				size = 1
			}
			if (this.spriteSize != size) {
				this.backgroundSprite = null
				this.spriteSize = size
			}
			if (this.skyBoxSpheres != null) {
				this.availableSunCount = 0
				@Pc(53) val sunSizes: IntArray = IntArray(this.skyBoxSpheres.size)
				@Pc(55) var i: Int = 0
				while (i < this.skyBoxSpheres.size) {
					@Pc(62) val skyBoxSphere: Sun? = this.skyBoxSpheres[i]
					if (skyBoxSphere!!.update(this.positionX, this.positionY, this.positionZ, this.lastComponentHeight)) {
						sunSizes[this.availableSunCount] = skyBoxSphere.distance
						this.availableSuns!![this.availableSunCount++] = skyBoxSphere
					}
					i++
				}
				IntKeyedQuicksort.quicksort(this.availableSunCount - 1, 0, sunSizes, @Suppress("UNCHECKED_CAST") (this.availableSuns!! as Array<Any?>))
			}
			this.prepareSun = true
		}
		@Pc(121) var ready: Boolean = false
		if (this.prepareSun) {
			this.prepareSun = false
			@Pc(132) var j: Int = this.availableSunCount - 1
			while (j >= 0) {
				@Pc(143) val sunReady: Boolean = this.availableSuns!![j]!!.isSunGenerated(toolkit, this.activeSun)
				ready = ready or sunReady
				this.prepareSun = this.prepareSun or !sunReady
				j--
			}
		}
		return ready
	}

	@OriginalMember(owner = "client!wr", name = "a", descriptor = "(ILclient!ha;BIIIIIII)V")
	fun renderSkybox(@OriginalArg(0) angle: Int, @OriginalArg(1) toolkit: Toolkit?, @OriginalArg(3) x: Int, @OriginalArg(4) width: Int, @OriginalArg(5) y: Int, @OriginalArg(6) rotationY: Int, @OriginalArg(7) sunDeltaZ: Int, @OriginalArg(8) backgroundColour: Int, @OriginalArg(9) height: Int) {
		@Pc(7) val adjustedAngle: Int = (angle + rotationY) and 0x3FFF
		if (this.materialId == -1 || this.spriteSize == 0) {
			toolkit!!.aa(x, y, width, height, backgroundColour, 0)
		} else {
			@Pc(28) val textureMetrics: MaterialType? = TextureTypeListInit.textureSource!!.getMetrics(this.materialId)
			if (this.backgroundSprite == null && TextureTypeListInit.textureSource!!.textureAvailable(this.materialId)) {
				@Pc(67) val pixels: IntArray? = if (textureMetrics!!.effectCombiner == 2) TextureTypeListInit.textureSource!!.getPixelsArgb(this.spriteSize, 0.7f, this.materialId, this.spriteSize) else TextureTypeListInit.textureSource!!.getPixels(false, this.materialId, 0.7f, this.spriteSize, this.spriteSize)
				this.backgroundSprite = toolkit!!.createNativeSprite(this.spriteSize, pixels, this.spriteSize, this.spriteSize)
			}
			if (textureMetrics!!.effectCombiner == 2) {
				toolkit!!.aa(x, y, width, height, backgroundColour, 0)
			}
			if (this.backgroundSprite != null) {
				@Pc(105) val combineMode: Int = if (textureMetrics.effectCombiner == 2) 1 else 0
				@Pc(111) var yOffset: Int = height * sunDeltaZ / -4096
				@Pc(124) var xOffset: Int
				xOffset = (width - height) / 2 + height * adjustedAngle / 4096
				while (xOffset > height) {
					xOffset -= height
				}
				while (xOffset < 0) {
					xOffset += height
				}
				while (height < yOffset) {
					yOffset -= height
				}
				while (yOffset < 0) {
					yOffset += height
				}
				@Pc(163) var i: Int = xOffset - height
				while (i < width) {
					@Pc(170) var j: Int = yOffset - height
					while (j < height) {
						this.backgroundSprite!!.drawCircle(x + i, j + y, height, height, 1, 0, combineMode)
						j += height
					}
					i += height
				}
			}
		}
		@Pc(223) var k: Int = this.availableSunCount - 1
		while (k >= 0) {
			this.availableSuns!![k]!!.calculateProjection(toolkit, x, y, width, height, sunDeltaZ, adjustedAngle)
			k--
		}
	}

	@OriginalMember(owner = "client!wr", name = "a", descriptor = "(B)V")
	fun clearSprites() {
		if (this.skyBoxSpheres != null) {
			@Pc(10) var i: Int = 0
			while (i < this.skyBoxSpheres.size) {
				this.skyBoxSpheres[i]!!.resetSprite()
				i++
			}
		}
		this.backgroundSprite = null
	}

	companion object {
		@OriginalMember(owner = "client!qf", name = "c", descriptor = "I")
		@JvmField
		var skyboxAngle: Int = 0

		@OriginalMember(owner = "client!uea", name = "f", descriptor = "Lclient!wr;")
		@JvmField
		var configNpcJs5: Skybox? = null
	}
}
