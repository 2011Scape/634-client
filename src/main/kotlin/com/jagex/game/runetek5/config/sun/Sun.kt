package com.jagex.game.runetek5.config.sun

import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.TextureTypeList
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.TextureTypeListInit

@OriginalClass("client!ki")
class Sun {

	@OriginalMember(owner = "client!ki", name = "c", descriptor = "I")
	private var positionOffset: Int = 0

	@OriginalMember(owner = "client!ki", name = "e", descriptor = "I")
	private var sunRotationY: Int = 0

	@OriginalMember(owner = "client!ki", name = "f", descriptor = "I")
	private var sunUseDimension: Int = 0

	@OriginalMember(owner = "client!ki", name = "g", descriptor = "Lclient!mi;")
	private var sunSprite: NativeSprite? = null

	@OriginalMember(owner = "client!ki", name = "q", descriptor = "I")
	private var sunRotationX: Int = 0

	@OriginalMember(owner = "client!ki", name = "r", descriptor = "I")
	@JvmField
	var distance: Int = 0

	@OriginalMember(owner = "client!ki", name = "k", descriptor = "I")
	private val x: Int

	@OriginalMember(owner = "client!ki", name = "b", descriptor = "I")
	private val y: Int

	@OriginalMember(owner = "client!ki", name = "o", descriptor = "I")
	private val z: Int

	@OriginalMember(owner = "client!ki", name = "i", descriptor = "Z")
	private val invertPosition: Boolean

	@OriginalMember(owner = "client!ki", name = "j", descriptor = "I")
	private val sunMaterialId: Int

	@OriginalMember(owner = "client!ki", name = "h", descriptor = "I")
	private val renderType: Int

	@OriginalMember(owner = "client!ki", name = "d", descriptor = "I")
	private val positionOffsetStep: Int

	@OriginalMember(owner = "client!ki", name = "p", descriptor = "I")
	private val colour: Int

	@OriginalMember(owner = "client!ki", name = "<init>", descriptor = "(IIIIIIIZ)V")
	constructor(@OriginalArg(0) colour: Int, @OriginalArg(1) sunMaterialId: Int, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) z: Int, @OriginalArg(5) positionOffsetStep: Int, @OriginalArg(6) renderType: Int, @OriginalArg(7) invertPosition: Boolean) {
		this.x = x
		this.y = y
		this.z = z
		this.invertPosition = invertPosition
		this.sunMaterialId = sunMaterialId
		this.renderType = renderType
		this.positionOffsetStep = positionOffsetStep
		this.colour = colour
	}

	@OriginalMember(owner = "client!ki", name = "a", descriptor = "(Lclient!ha;Lclient!ki;)Z")
	private fun performAction(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) sun: Sun?): Boolean {
		if (this.sunSprite == null) {
			if (this.colour == 0) {
				if (TextureTypeListInit.textureSource.textureAvailable(this.sunMaterialId)) {
					@Pc(23) val pixels: IntArray? = TextureTypeListInit.textureSource.getPixelsArgb(this.sunUseDimension, 0.7f, this.sunMaterialId, this.sunUseDimension)
					this.sunSprite = toolkit!!.createNativeSprite(this.sunUseDimension, pixels, this.sunUseDimension, this.sunUseDimension)
				}
			} else if (this.colour == 2) {
				this.drawColoredSun(toolkit, sun)
			} else if (this.colour == 1) {
				this.drawTexturedSun(toolkit, sun)
			}
		}
		return this.sunSprite != null
	}

	@OriginalMember(owner = "client!ki", name = "a", descriptor = "(IIII)Z")
	fun update(@OriginalArg(0) offsetX: Int, @OriginalArg(1) offsetY: Int, @OriginalArg(2) offsetZ: Int, @OriginalArg(3) viewportHeight: Int): Boolean {
		@Pc(7) var dx: Int
		@Pc(12) var dy: Int
		@Pc(17) var dz: Int
		if (this.invertPosition) {
			this.distance = 1073741823
			dx = this.x
			dy = this.y
			dz = this.z
		} else {
			dx = this.x - offsetX
			dy = this.y - offsetY
			dz = this.z - offsetZ
			this.distance = java.lang.Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toInt()
			if (this.distance == 0) {
				this.distance = 1
			}
			dx = (dx shl 8) / this.distance
			dy = (dy shl 8) / this.distance
			dz = (dz shl 8) / this.distance
		}
		@Pc(90) val length: Int = (java.lang.Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble()) * 256.0).toInt()
		if (length > 128) {
			dx = (dx shl 16) / length
			dy = (dy shl 16) / length
			dz = (dz shl 16) / length
			this.positionOffset = this.positionOffsetStep * viewportHeight / (if (this.invertPosition) 1024 else this.distance)
		} else {
			this.positionOffset = 0
		}
		if (this.positionOffset < 8) {
			this.sunSprite = null
			return false
		}
		@Pc(143) var nextSize: Int = Utils.nextPowerOfTwo(this.positionOffset)
		if (nextSize > viewportHeight) {
			nextSize = Utils.floorPowerOfTwo(viewportHeight)
		}
		if (nextSize > 512) {
			nextSize = 512
		}
		if (nextSize != this.sunUseDimension) {
			this.sunUseDimension = nextSize
		}
		this.sunRotationX = (java.lang.Math.asin((dy.toFloat() / 256.0f).toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
		this.sunRotationY = (java.lang.Math.atan2(dx.toDouble(), (-dz).toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
		this.sunSprite = null
		return true
	}

	@OriginalMember(owner = "client!ki", name = "b", descriptor = "()V")
	fun resetSprite() {
		this.sunSprite = null
	}

	@OriginalMember(owner = "client!ki", name = "b", descriptor = "(Lclient!ha;Lclient!ki;)V")
	private fun drawTexturedSun(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) sun: Sun?) {
		Sun.generateModel(toolkit)
		Sun.generateSunSprites(toolkit)
		toolkit!!.K(Sun.SUN_CLIPPING)
		toolkit.KA(0, 0, this.sunUseDimension, this.sunUseDimension)
		toolkit.ya()
		toolkit.aa(0, 0, this.sunUseDimension, this.sunUseDimension, this.renderType or 0xFF000000.toInt(), 0)
		@Pc(31) var positionX: Int = 0
		@Pc(33) var positionY: Int = 0
		@Pc(35) var positionZ: Int = 256
		if (sun != null) {
			if (sun.invertPosition) {
				positionX = -sun.x
				positionY = -sun.y
				positionZ = -sun.z
			} else {
				positionX = sun.x - this.x
				positionY = sun.y - this.y
				positionZ = sun.z - this.z
			}
		}
		@Pc(79) var sin: Int
		@Pc(84) val local84: Int
		@Pc(94) var temp: Int
		if (this.sunRotationX != 0) {
			sin = TextureCombineSource.SINE[this.sunRotationX]
			val cos: Int = TextureCombineSource.COSINE[this.sunRotationX]
			temp = (positionY * cos - positionZ * sin) shr 14
			positionZ = (positionY * sin + positionZ * cos) shr 14
			positionY = temp
		}
		if (this.sunRotationY != 0) {
			sin = TextureCombineSource.SINE[this.sunRotationY]
			val cos: Int = TextureCombineSource.COSINE[this.sunRotationY]
			temp = (positionZ * sin + positionX * cos) shr 14
			positionZ = (positionZ * cos - positionX * sin) shr 14
			positionX = temp
		}
		@Pc(147) val model: Model = Sun.sunModel!!.copy(0.toByte(), 51200, true)!!
		model.aa(0.toShort(), this.sunMaterialId.toShort())
		toolkit.xa(1.0f)
		toolkit.ZA(16777215, 1.0f, 1.0f, positionX.toFloat(), positionY.toFloat(), positionZ.toFloat())
		var scale: Int = this.sunUseDimension * 1024 / (model.RA() - model.V())
		if (this.renderType != 0) {
			scale = scale * 13 / 16
		}
		toolkit.DA(this.sunUseDimension / 2, this.sunUseDimension / 2, scale, scale)
		toolkit.setCamera(toolkit.createMatrix())
		@Pc(206) val matrix: Matrix = toolkit.createMatrix()!!
		matrix.applyTranslation(0, 0, toolkit.i() - model.HA())
		model.renderOrtho(matrix, null, 1024, 1)
		@Pc(228) val antiSunSize: Int = this.sunUseDimension * 13 / 16
		@Pc(235) val antiSunOffset: Int = (this.sunUseDimension - antiSunSize) / 2
		Sun.activeAntiSun!!.drawCircle(antiSunOffset, antiSunOffset, antiSunSize, antiSunSize, 0, this.renderType or 0xFF000000.toInt(), 1)
		this.sunSprite = toolkit.createNativeSprite(0, 0, this.sunUseDimension, this.sunUseDimension, true)
		toolkit.ya()
		toolkit.aa(0, 0, this.sunUseDimension, this.sunUseDimension, 0, 0)
		Sun.activeSun!!.drawCircle(0, 0, this.sunUseDimension, this.sunUseDimension, 1, 0, 0)
		this.sunSprite!!.copyAlpha(0)
		toolkit.KA(Sun.SUN_CLIPPING[0], Sun.SUN_CLIPPING[1], Sun.SUN_CLIPPING[2], Sun.SUN_CLIPPING[3])
	}

	@OriginalMember(owner = "client!ki", name = "c", descriptor = "(Lclient!ha;Lclient!ki;)Z")
	fun isSunGenerated(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) sun: Sun?): Boolean {
		return this.sunSprite != null || this.performAction(toolkit, sun)
	}

	@OriginalMember(owner = "client!ki", name = "a", descriptor = "(Lclient!ha;IIIIII)V")
	fun calculateProjection(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) width: Int, @OriginalArg(2) height: Int, @OriginalArg(3) sunDeltaX: Int, @OriginalArg(4) sunDeltaY: Int, @OriginalArg(5) sunDeltaZ: Int, @OriginalArg(6) alpha: Int) {
		if (this.sunSprite == null) {
			return
		}
		@Pc(10) var rotationXDelta: Int = (this.sunRotationX - sunDeltaZ) and 0x3FFF
		@Pc(17) var rotationYDelta: Int = (this.sunRotationY - alpha) and 0x3FFF
		if (rotationYDelta > 8192) {
			rotationYDelta -= 16384
		}
		if (rotationXDelta > 8192) {
			rotationXDelta -= 16384
		}
		@Pc(38) val drawY: Int = rotationXDelta * sunDeltaY / 4096 + (sunDeltaY - this.positionOffset) / 2
		@Pc(51) val drawX: Int = rotationYDelta * sunDeltaY / -4096 + (sunDeltaX - this.positionOffset) / 2
		if (drawY < sunDeltaY && drawY + this.positionOffset > 0 && drawX < sunDeltaX && drawX + this.positionOffset > 0) {
			this.sunSprite!!.drawTintedScaled(drawX + width, drawY + height, this.positionOffset, this.positionOffset)
		}
	}

	@OriginalMember(owner = "client!ki", name = "d", descriptor = "(Lclient!ha;Lclient!ki;)V")
	private fun drawColoredSun(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) sun: Sun?) {
		@Pc(6) val mesh: Mesh? = Mesh.fromResource(TextureTypeList.modelResource, this.sunMaterialId)
		if (mesh == null) {
			return
		}
		toolkit!!.K(Sun.SUN_CLIPPING)
		toolkit.KA(0, 0, this.sunUseDimension, this.sunUseDimension)
		toolkit.aa(0, 0, this.sunUseDimension, this.sunUseDimension, 0, 0)
		@Pc(32) var positionX: Int = 0
		@Pc(34) var positionY: Int = 0
		@Pc(36) var positionZ: Int = 256
		if (sun != null) {
			if (sun.invertPosition) {
				positionX = -sun.x
				positionY = -sun.y
				positionZ = -sun.z
			} else {
				positionX = this.x - sun.x
				positionY = this.y - sun.y
				positionZ = this.z - sun.z
			}
		}
		@Pc(81) var rotation: Int
		@Pc(85) val local85: Int
		@Pc(89) val local89: Int
		@Pc(99) val local99: Int
		if (this.sunRotationX != 0) {
			rotation = (-this.sunRotationX) and 0x3FFF
			val sin: Int = TextureCombineSource.SINE[rotation]
			val cos: Int = TextureCombineSource.COSINE[rotation]
			val rotatedY: Int = (positionY * cos - positionZ * sin) shr 14
			positionZ = (positionY * sin + positionZ * cos) shr 14
			positionY = rotatedY
		}
		if (this.sunRotationY != 0) {
			rotation = (-this.sunRotationY) and 0x3FFF
			val sin: Int = TextureCombineSource.SINE[rotation]
			val cos: Int = TextureCombineSource.COSINE[rotation]
			val rotatedX: Int = (positionZ * sin + positionX * cos) shr 14
			positionZ = (positionZ * cos - positionX * sin) shr 14
			positionX = rotatedX
		}
		toolkit.xa(1.0f)
		toolkit.ZA(16777215, 1.0f, 1.0f, positionX.toFloat(), positionY.toFloat(), positionZ.toFloat())
		@Pc(172) val model: Model = toolkit.createModel(mesh, 2048, 0, 64, 768)!!
		val modelWidth: Int = model.RA() - model.V()
		val modelDepth: Int = model.EA() - model.fa()
		val centerX: Int = model.V() + modelWidth / 2
		@Pc(198) val centerY: Int = model.fa() + modelDepth / 2
		@Pc(205) val maxSize: Int = if (modelWidth > modelDepth) modelWidth else modelDepth
		toolkit.DA(centerX, centerY, maxSize, maxSize)
		toolkit.setCamera(toolkit.createMatrix())
		@Pc(218) val matrix: Matrix = toolkit.scratchMatrix()!!
		matrix.applyTranslation(0, 0, toolkit.i() - model.HA())
		model.renderOrtho(matrix, null, toolkit.i(), 1)
		this.sunSprite = toolkit.createNativeSprite(0, 0, this.sunUseDimension, this.sunUseDimension, true)
		this.sunSprite!!.copyAlpha(3)
		toolkit.KA(Sun.SUN_CLIPPING[0], Sun.SUN_CLIPPING[1], Sun.SUN_CLIPPING[2], Sun.SUN_CLIPPING[3])
	}

	companion object {
		@OriginalMember(owner = "client!ki", name = "l", descriptor = "[I")
		@JvmField
		val SUN_CLIPPING: IntArray = IntArray(4)

		@OriginalMember(owner = "client!ki", name = "m", descriptor = "Lclient!ka;")
		@JvmField
		var sunModel: Model? = null

		@OriginalMember(owner = "client!ki", name = "b", descriptor = "(Lclient!ha;)V")
		@JvmStatic
		fun generateModel(@OriginalArg(0) toolkit: Toolkit?) {
			if (sunModel != null) {
				return
			}
			@Pc(8) val mesh: Mesh = Mesh(580, 1104, 1)
			mesh.addSunTexture()
			mesh.addVertex(128, 0, 0)
			mesh.addVertex(-128, 0, 0)
			for (@Pc(37) i in 0..24) {
				@Pc(44) val longitude: Int = i * 8192 / 24
				@Pc(48) val sinLongitude: Int = TextureCombineSource.SINE[longitude]
				@Pc(52) val cosLongitude: Int = TextureCombineSource.COSINE[longitude]
				@Pc(61) val local61: Int
				@Pc(67) val local67: Int
				@Pc(75) val local75: Int
				@Pc(83) val local83: Int
				for (@Pc(54) j in 1 until 24) {
					val latitude: Int = j * 8192 / 24
					val vertexX: Int = TextureCombineSource.COSINE[latitude] shr 7
					val vertexY: Int = (TextureCombineSource.SINE[latitude] * sinLongitude) shr 21
					val vertexZ: Int = (TextureCombineSource.SINE[latitude] * cosLongitude) shr 21
					mesh.addVertex(vertexX, -vertexY, vertexZ)
				}
				if (i > 0) {
					var ringVertex: Int = i * 23 + 2
					var prevRingVertex: Int = ringVertex - 23
					mesh.addFace(0, 0.toByte(), 0.toByte(), 0.toShort(), 127.toShort(), prevRingVertex, 0.toByte(), ringVertex)
					for (segment in 1 until 23) {
						val nextPrevRingVertex: Int = prevRingVertex + 1
						@Pc(130) val nextVertex: Int = ringVertex + 1
						mesh.addFace(prevRingVertex, 0.toByte(), 0.toByte(), 0.toShort(), 127.toShort(), nextPrevRingVertex, 0.toByte(), ringVertex)
						mesh.addFace(nextPrevRingVertex, 0.toByte(), 0.toByte(), 0.toShort(), 127.toShort(), nextVertex, 0.toByte(), ringVertex)
						prevRingVertex = nextPrevRingVertex
						ringVertex = nextVertex
					}
					mesh.addFace(ringVertex, 0.toByte(), 0.toByte(), 0.toShort(), 127.toShort(), prevRingVertex, 0.toByte(), 1)
				}
			}
			mesh.maxDepth = mesh.numVertices
			mesh.faceGroups = null
			mesh.vertexGroups = null
			mesh.facesLayer = null
			sunModel = toolkit!!.createModel(mesh, 51200, 33, 64, 768)
		}

		@OriginalMember(owner = "client!ki", name = "a", descriptor = "Lclient!mi;")
		@JvmField
		var activeAntiSun: NativeSprite? = null

		@OriginalMember(owner = "client!ki", name = "a", descriptor = "(Lclient!ha;)V")
		@JvmStatic
		fun generateSunSprites(@OriginalArg(0) toolkit: Toolkit?) {
			if (activeSun != null) {
				return
			}
			@Pc(4) val shading: IntArray = IntArray(16384)
			@Pc(7) val mask: IntArray = IntArray(16384)
			for (@Pc(9) i in 0 until 64) {
				@Pc(14) val dy: Int = 64 - i
				@Pc(18) val dySquared: Int = dy * dy
				@Pc(24) val mirrorY: Int = 128 - i - 1
				@Pc(28) val row: Int = i * 128
				@Pc(32) val mirrorRow: Int = mirrorY * 128
				for (@Pc(34) j in 0 until 64) {
					@Pc(39) val dx: Int = 64 - j
					@Pc(43) val dxSquared: Int = dx * dx
					@Pc(49) val mirrorX: Int = 128 - j - 1
					@Pc(59) var intensity: Int = 256 - ((dxSquared + dySquared) shl 8) / 4096
					intensity = intensity * 16 * 192 / 1536
					if (intensity < 0) {
						intensity = 0
					} else if (intensity > 255) {
						intensity = 255
					}
					@Pc(81) val halfIntensity: Int = intensity / 2
					val maskValue: Int = (intensity or 0xFF00) shl 16
					mask[mirrorRow + mirrorX] = maskValue
					mask[mirrorRow + j] = maskValue
					mask[row + mirrorX] = maskValue
					mask[row + j] = maskValue
					val shadingValue: Int = ((127 - halfIntensity) shl 24) or 0xFFFFFF
					shading[mirrorRow + mirrorX] = shadingValue
					shading[mirrorRow + j] = shadingValue
					shading[row + mirrorX] = shadingValue
					shading[row + j] = shadingValue
				}
			}
			activeSun = toolkit!!.createNativeSprite(128, mask, 128, 128)
			activeAntiSun = toolkit.createNativeSprite(128, shading, 128, 128)
		}

		@OriginalMember(owner = "client!ki", name = "n", descriptor = "Lclient!mi;")
		@JvmField
		var activeSun: NativeSprite? = null
	}
}
