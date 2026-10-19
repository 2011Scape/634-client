// The warnings of the original (deobfuscated) code, left as it was: the build treats warnings as errors.
@file:Suppress("SENSELESS_COMPARISON")

package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.graphics.hint.HintDetails
import com.jagex.game.runetek5.config.sequence.MovingSequence
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.entity.pathingentity.EasedAngle
import com.jagex.game.runetek5.config.bas.BasType
import com.jagex.game.runetek5.config.hitsplat.HitSplatType
import com.jagex.game.runetek5.config.seqtype.SeqReplayMode
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.sequence.PathingEntitySequence
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.MoveSpeed
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.config.sequence.frameset.FrameSet
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.SoundManager
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import withLock

@OriginalClass("client!kda")
abstract class PathingEntity : AbstractInteractiveEntity, Actor {

	/* Not part of the original client: the plugin API's view of a player or npc (plugin.api.Actor). */

	override fun x(): Int {
		return this.x
	}

	override fun z(): Int {
		return this.z
	}

	override fun level(): Int {
		return this.level.toInt()
	}

	override fun sizeInTiles(): Int {
		return this.getSize()
	}

	override fun overheadHeight(): Int {
		return this.getOverheadHeight()
	}

	override fun healthRatio(): Int {
		return this.healthRatio
	}

	override fun isDrawnThisFrame(): Boolean {
		return SceneDrawer.timesSceneRendered == this.drawCycle
	}

	// Not part of the original client: where this entity stood at the previous and the current game
	// cycle, and where it really is while a frame shows it between the two; see ClientFeatures.blendEntities.
	@JvmField
	var blendCycle: Int = 0
	@JvmField
	var blendGap: Int = 0
	@JvmField
	var previousX: Int = 0
	@JvmField
	var previousZ: Int = 0
	@JvmField
	var previousYaw: Int = 0
	@JvmField
	var cycleX: Int = 0
	@JvmField
	var cycleZ: Int = 0
	@JvmField
	var cycleYaw: Int = 0
	@JvmField
	var realX: Int = 0
	@JvmField
	var realZ: Int = 0
	@JvmField
	var realYaw: Int = 0
	@JvmField
	var cycleLevel: Int = 0
	@JvmField
	var blended: Boolean = false

	@JvmField
	val secondarySpotAnimation: PathingEntitySpotAnimation = PathingEntitySpotAnimation()

	@OriginalMember(owner = "client!kda", name = "X", descriptor = "B")
	@JvmField
	var saturation: Byte = 0

	@OriginalMember(owner = "client!kda", name = "Z", descriptor = "I")
	@JvmField
	var modelRotateZ: Int = 0

	@OriginalMember(owner = "client!kda", name = "bb", descriptor = "I")
	@JvmField
	var secondaryBarDuration: Int = 0

	@JvmField
	val primarySpotAnimation: PathingEntitySpotAnimation = PathingEntitySpotAnimation()

	@OriginalMember(owner = "client!kda", name = "sb", descriptor = "I")
	@JvmField
	var secondaryBarHoldCycles: Int = 0

	@OriginalMember(owner = "client!kda", name = "wb", descriptor = "I")
	@JvmField
	var forceMoveDirection: Int = 0

	@OriginalMember(owner = "client!kda", name = "Eb", descriptor = "I")
	@JvmField
	var forceMovementT1Delay: Int = 0

	@OriginalMember(owner = "client!kda", name = "Gb", descriptor = "I")
	@JvmField
	var forceMovementT2XOff: Int = 0

	@OriginalMember(owner = "client!kda", name = "Hb", descriptor = "[I")
	@JvmField
	var wornTargets: IntArray? = null

	@OriginalMember(owner = "client!kda", name = "Rb", descriptor = "I")
	@JvmField
	var modelTranslateY: Int = 0

	@OriginalMember(owner = "client!kda", name = "Ub", descriptor = "B")
	@JvmField
	var hue: Byte = 0

	@OriginalMember(owner = "client!kda", name = "jc", descriptor = "B")
	@JvmField
	var lightness: Byte = 0

	@OriginalMember(owner = "client!kda", name = "kc", descriptor = "I")
	@JvmField
	var secondaryBarStep: Int = 0

	@OriginalMember(owner = "client!kda", name = "vc", descriptor = "I")
	@JvmField
	var lastYawRotation: Int = 0

	@OriginalMember(owner = "client!kda", name = "Bc", descriptor = "I")
	@JvmField
	var forceMovementT2YOff: Int = 0

	@OriginalMember(owner = "client!kda", name = "Dc", descriptor = "I")
	@JvmField
	var playerIndex: Int = 0

	@OriginalMember(owner = "client!kda", name = "Gc", descriptor = "I")
	@JvmField
	var forceMovementT1XOff: Int = 0

	@OriginalMember(owner = "client!kda", name = "Ic", descriptor = "I")
	@JvmField
	var healthRatio: Int = 0

	@OriginalMember(owner = "client!kda", name = "Jc", descriptor = "[I")
	@JvmField
	var wornRotation: IntArray? = null

	@OriginalMember(owner = "client!kda", name = "Nc", descriptor = "I")
	@JvmField
	var forceMovementT2Delay: Int = 0

	@OriginalMember(owner = "client!kda", name = "Oc", descriptor = "I")
	@JvmField
	var drawCycle: Int = 0

	@OriginalMember(owner = "client!kda", name = "Pc", descriptor = "I")
	@JvmField
	var modelRotateX: Int = 0

	@OriginalMember(owner = "client!kda", name = "bd", descriptor = "I")
	@JvmField
	var forceMovementT1YOff: Int = 0

	@OriginalMember(owner = "client!kda", name = "od", descriptor = "Lclient!uj;")
	@JvmField
	var particleSystem: ParticleSystem? = null

	@JvmField
	val movingSequence: MovingSequence = MovingSequence()

	@OriginalMember(owner = "client!kda", name = "Bb", descriptor = "[I")
	@JvmField
	var currentAnimations: IntArray? = null

	@JvmField
	val hintDetails: HintDetails = HintDetails()

	@OriginalMember(owner = "client!kda", name = "vb", descriptor = "I")
	@JvmField
	var minY: Int = 0

	@OriginalMember(owner = "client!kda", name = "Pb", descriptor = "I")
	@JvmField
	var tintEndCycle: Int = -1

	@OriginalMember(owner = "client!kda", name = "nc", descriptor = "I")
	@JvmField
	var groundContouredOffsetY: Int = 0

	@OriginalMember(owner = "client!kda", name = "gb", descriptor = "I")
	@JvmField
	var clientCycles: Int = -1

	@OriginalMember(owner = "client!kda", name = "Mb", descriptor = "I")
	@JvmField
	var turningTicks: Int = 0

	@OriginalMember(owner = "client!kda", name = "ub", descriptor = "[I")
	@JvmField
	val hitsplatText: IntArray = IntArray(PathingEntity.maximumHits)

	@JvmField
	val currentSequence: PathingEntitySequence = newCurrentSequence()

	@OriginalMember(owner = "client!kda", name = "Yb", descriptor = "[I")
	@JvmField
	val activeHitsplatIds: IntArray = IntArray(PathingEntity.maximumHits)

	@OriginalMember(owner = "client!kda", name = "Ac", descriptor = "I")
	@JvmField
	var secondaryBarEndCycle: Int = -1000

	@OriginalMember(owner = "client!kda", name = "ac", descriptor = "I")
	@JvmField
	var overheadStackCount: Int = 0

	@OriginalMember(owner = "client!kda", name = "nb", descriptor = "B")
	private var hitmarkPointer: Byte = 0

	@OriginalMember(owner = "client!kda", name = "yc", descriptor = "I")
	@JvmField
	var worldPriority: Int = 0

	@OriginalMember(owner = "client!kda", name = "sc", descriptor = "B")
	@JvmField
	var opacity: Byte = 0

	@OriginalMember(owner = "client!kda", name = "tc", descriptor = "I")
	private var size: Int = 1

	@OriginalMember(owner = "client!kda", name = "rb", descriptor = "Z")
	@JvmField
	var secondaryBarAlternate: Boolean = false

	@OriginalMember(owner = "client!kda", name = "Nb", descriptor = "I")
	@JvmField
	var tintStartCycle: Int = -1

	@OriginalMember(owner = "client!kda", name = "oc", descriptor = "I")
	@JvmField
	var faceEntity: Int = -1

	@OriginalMember(owner = "client!kda", name = "kb", descriptor = "[I")
	@JvmField
	val hitsplatCycles: IntArray = IntArray(PathingEntity.maximumHits)

	@OriginalMember(owner = "client!kda", name = "Qc", descriptor = "[I")
	@JvmField
	val hitsplatHealthRatios: IntArray = IntArray(PathingEntity.maximumHits)

	@OriginalMember(owner = "client!kda", name = "Tc", descriptor = "I")
	@JvmField
	var lastUpdate: Int = 0

	@OriginalMember(owner = "client!kda", name = "Wc", descriptor = "Z")
	@JvmField
	var isHidden: Boolean = true

	@OriginalMember(owner = "client!kda", name = "Xc", descriptor = "I")
	@JvmField
	var yawSpeed: Int = 256

	@OriginalMember(owner = "client!kda", name = "Vc", descriptor = "[Lclient!qca;")
	@JvmField
	val sequences: Array<PathingEntitySequence?> = arrayOfNulls<PathingEntitySequence>(12)

	@OriginalMember(owner = "client!kda", name = "Y", descriptor = "I")
	@JvmField
	var sphereRadius: Int = -32768

	@OriginalMember(owner = "client!kda", name = "Rc", descriptor = "[I")
	@JvmField
	val soakHitsplatIds: IntArray = IntArray(PathingEntity.maximumHits)

	@OriginalMember(owner = "client!kda", name = "xc", descriptor = "[I")
	@JvmField
	val soakHitsplatText: IntArray = IntArray(PathingEntity.maximumHits)

	@OriginalMember(owner = "client!kda", name = "ob", descriptor = "I")
	@JvmField
	var healthBarEndCycle: Int = -1000

	@OriginalMember(owner = "client!kda", name = "ib", descriptor = "Lclient!ui;")
	@JvmField
	val yaw: EasedAngle = EasedAngle()

	@OriginalMember(owner = "client!kda", name = "cd", descriptor = "Lclient!ui;")
	private val roll: EasedAngle = EasedAngle()

	@OriginalMember(owner = "client!kda", name = "dd", descriptor = "Lclient!ui;")
	private val pitch: EasedAngle = EasedAngle()

	@OriginalMember(owner = "client!kda", name = "fd", descriptor = "Z")
	@JvmField
	var particlesInitialized: Boolean = false

	@OriginalMember(owner = "client!kda", name = "ed", descriptor = "[I")
	@JvmField
	val movementBaseY: IntArray = IntArray(10)

	@OriginalMember(owner = "client!kda", name = "jd", descriptor = "I")
	@JvmField
	var previousMovementStepsLeft: Int = 0

	@OriginalMember(owner = "client!kda", name = "ld", descriptor = "Z")
	@JvmField
	var transparent: Boolean = false

	@OriginalMember(owner = "client!kda", name = "id", descriptor = "[B")
	@JvmField
	val movementQueueSpeed: ByteArray = ByteArray(10)

	@OriginalMember(owner = "client!kda", name = "md", descriptor = "I")
	@JvmField
	var walkSpeedAccumulator: Int = 0

	@OriginalMember(owner = "client!kda", name = "kd", descriptor = "[Lclient!ka;")
	@JvmField
	val models: Array<Model?> = arrayOfNulls<Model>(3)

	@OriginalMember(owner = "client!kda", name = "gd", descriptor = "I")
	@JvmField
	var pathPointer: Int = 0

	@OriginalMember(owner = "client!kda", name = "hd", descriptor = "[I")
	@JvmField
	val movementBaseX: IntArray = IntArray(10)

	@OriginalMember(owner = "client!kda", name = "nd", descriptor = "I")
	@JvmField
	var stalledStepCount: Int = 0

	@OriginalMember(owner = "client!kda", name = "<init>", descriptor = "()V")
	constructor() : super(0, 0, 0, 0, 0, 0, 0, 0, 0, false, 0.toByte())

	@OriginalMember(owner = "client!kda", name = "f", descriptor = "(B)I")
	abstract fun getBasId(): Int

	@OriginalMember(owner = "client!kda", name = "g", descriptor = "(B)Lclient!sg;")
	fun getBasType(): BasType? {
		@Pc(20) val basId: Int = this.getBasId()
		return if (basId == -1) PathingEntity.DEFAULT_BASTYPE else TypeLists.BAS!!.list(basId)
	}

	@OriginalMember(owner = "client!kda", name = "e", descriptor = "(B)V")
	final override fun updateBounds() {
		@Pc(12) val offset: Int = ((this.size - 1) shl 8) + 240
		super.endZ = ((offset + super.z) shr 9).toShort()
		super.startZ = ((super.z - offset) shr 9).toShort()
		super.endX = ((offset + super.x) shr 9).toShort()
		super.startX = ((super.x - offset) shr 9).toShort()
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(III)Z")
	fun rotateEquipment(@OriginalArg(1) equipIndex: Int, @OriginalArg(2) requestedAngle: Int): Boolean {
		if (this.wornRotation == null) {
			if (requestedAngle == -1) {
				return true
			}
			this.wornRotation = IntArray(12)
			@Pc(19) var i: Int = 0
			while (i < 12) {
				this.wornRotation!![i] = -1
				i++
			}
		}
		@Pc(42) val basType: BasType? = this.getBasType()
		@Pc(44) var angleAdjustment: Int = 256
		if (basType!!.turnAngleAdjustment != null && basType.turnAngleAdjustment!![equipIndex] > 0) {
			angleAdjustment = basType.turnAngleAdjustment!![equipIndex]
		}
		@Pc(75) val local75: Int
		@Pc(80) val local80: Int
		if (requestedAngle != -1) {
			if (this.wornRotation!![equipIndex] == -1) {
				this.wornRotation!![equipIndex] = this.yaw.getRotation()
			}
			val currentAngle: Int = this.wornRotation!![equipIndex]
			val angleDiff: Int = requestedAngle - currentAngle
			if (-angleAdjustment <= angleDiff && angleDiff <= angleAdjustment) {
				this.wornRotation!![equipIndex] = requestedAngle
				return true
			}
			if ((angleDiff <= 0 || angleDiff > 8192) && angleDiff > -8192) {
				this.wornRotation!![equipIndex] = (currentAngle - angleAdjustment) and 0x3FFF
			} else {
				this.wornRotation!![equipIndex] = (currentAngle + angleAdjustment) and 0x3FFF
			}
			return false
		} else if (this.wornRotation!![equipIndex] == -1) {
			return true
		} else {
			val yawAngle: Int = this.yaw.getRotation()
			val currentAngle: Int = this.wornRotation!![equipIndex]
			@Pc(85) val angleDelta: Int = yawAngle - currentAngle
			if (angleDelta < -angleAdjustment || angleDelta > angleAdjustment) {
				if ((angleDelta <= 0 || angleDelta > 8192) && angleDelta > -8192) {
					this.wornRotation!![equipIndex] = (currentAngle - angleAdjustment) and 0x3FFF
				} else {
					this.wornRotation!![equipIndex] = (currentAngle + angleAdjustment) and 0x3FFF
				}
				return false
			}
			this.wornRotation!![equipIndex] = -1
			@Pc(99) var j: Int = 0
			while (j < 12) {
				if (this.wornRotation!![j] != -1) {
					return true
				}
				j++
			}
			this.wornRotation = null
			return true
		}
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(IIIIBI)V")
	fun hillChange(@OriginalArg(0) rotation: Int, @OriginalArg(1) sizeX: Int, @OriginalArg(2) maxRotateZ: Int, @OriginalArg(3) sizeZ: Int, @OriginalArg(5) maxRotateX: Int) {
		@Pc(13) val centerX: Int = (super.startX + super.endX) shr 1
		@Pc(21) val centerZ: Int = (super.startZ + super.endZ) shr 1
		@Pc(25) val sinRotation: Int = TextureCombineSource.SINE[rotation]
		@Pc(29) val cosRotation: Int = TextureCombineSource.COSINE[rotation]
		@Pc(34) val nwLocalX: Int = -sizeX / 2
		@Pc(39) val nwLocalZ: Int = -sizeZ / 2
		@Pc(49) val nwRotatedX: Int = (nwLocalX * cosRotation + nwLocalZ * sinRotation) shr 14
		@Pc(60) val nwRotatedZ: Int = (nwLocalZ * cosRotation - sinRotation * nwLocalX) shr 14
		@Pc(75) val nwHeight: Int = Scene.getBridgedAverageHeight(nwRotatedX + super.x, nwRotatedZ + super.z, centerX, super.level.toInt(), centerZ)
		@Pc(79) val neLocalX: Int = sizeX / 2
		@Pc(84) val neLocalZ: Int = -sizeZ / 2
		@Pc(94) val neRotatedX: Int = (neLocalX * cosRotation + sinRotation * neLocalZ) shr 14
		@Pc(105) val neRotatedZ: Int = (neLocalZ * cosRotation - sinRotation * neLocalX) shr 14
		@Pc(120) val neHeight: Int = Scene.getBridgedAverageHeight(neRotatedX + super.x, super.z + neRotatedZ, centerX, super.level.toInt(), centerZ)
		@Pc(125) val swLocalX: Int = -sizeX / 2
		@Pc(129) val swLocalZ: Int = sizeZ / 2
		@Pc(140) val swRotatedX: Int = (swLocalZ * sinRotation + swLocalX * cosRotation) shr 14
		@Pc(151) val swRotatedZ: Int = (swLocalZ * cosRotation - swLocalX * sinRotation) shr 14
		@Pc(166) val swHeight: Int = Scene.getBridgedAverageHeight(swRotatedX + super.x, super.z + swRotatedZ, centerX, super.level.toInt(), centerZ)
		@Pc(176) val seLocalX: Int = sizeX / 2
		@Pc(180) val seLocalZ: Int = sizeZ / 2
		@Pc(190) val seRotatedX: Int = (cosRotation * seLocalX + seLocalZ * sinRotation) shr 14
		@Pc(201) val seRotatedZ: Int = (cosRotation * seLocalZ - sinRotation * seLocalX) shr 14
		@Pc(216) val seHeight: Int = Scene.getBridgedAverageHeight(seRotatedX + super.x, super.z + seRotatedZ, centerX, super.level.toInt(), centerZ)
		@Pc(227) val minNorthHeight: Int = if (nwHeight >= neHeight) neHeight else nwHeight
		@Pc(234) val minSouthHeight: Int = if (swHeight < seHeight) swHeight else seHeight
		@Pc(245) val minEastHeight: Int = if (neHeight >= seHeight) seHeight else neHeight
		this.modelRotateX = (kotlin.math.atan2((minNorthHeight - minSouthHeight).toDouble(), sizeZ.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
		@Pc(267) val minWestHeight: Int = if (nwHeight < swHeight) nwHeight else swHeight
		this.modelRotateZ = (kotlin.math.atan2((minWestHeight - minEastHeight).toDouble(), sizeX.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
		@Pc(294) var clampLimit: Int
		if (this.modelRotateX != 0 && maxRotateX != 0) {
			clampLimit = 16384 - maxRotateX
			if (this.modelRotateX <= 8192) {
				if (this.modelRotateX > maxRotateX) {
					this.modelRotateX = maxRotateX
				}
			} else if (this.modelRotateX < clampLimit) {
				this.modelRotateX = clampLimit
			}
		}
		if (this.modelRotateZ != 0 && maxRotateZ != 0) {
			clampLimit = 16384 - maxRotateZ
			if (this.modelRotateZ > 8192) {
				if (clampLimit > this.modelRotateZ) {
					this.modelRotateZ = clampLimit
				}
			} else if (this.modelRotateZ > maxRotateZ) {
				this.modelRotateZ = maxRotateZ
			}
		}
		this.modelTranslateY = nwHeight + seHeight
		if (this.modelTranslateY > neHeight + swHeight) {
			this.modelTranslateY = neHeight + swHeight
		}
		this.modelTranslateY = (this.modelTranslateY shr 1) - super.y
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(I)I")
	abstract fun getOverheadSprite(): Int

	@OriginalMember(owner = "client!kda", name = "b", descriptor = "(I)I")
	open fun getOverheadHeight(): Int {
		@Pc(7) val basType: BasType? = this.getBasType()
		if (basType!!.iconHeightOverride == -1) {
			return if (this.sphereRadius == -32768) 200 else -this.sphereRadius
		} else {
			return basType.iconHeightOverride
		}
	}

	@OriginalMember(owner = "client!kda", name = "c", descriptor = "(I)V")
	fun stopMoving() {
		this.pathPointer = 0
		this.previousMovementStepsLeft = 0
	}

	@OriginalMember(owner = "client!kda", name = "d", descriptor = "(I)I")
	fun rotationTick(): Int {
		@Pc(9) val basType: BasType? = this.getBasType()
		@Pc(20) val yaw: Int = this.yaw.rotation
		@Pc(34) val isTurning: Boolean
		if (basType!!.yawAcceleration == 0) {
			isTurning = this.yaw.calculateRotation(this.yawSpeed, this.lastYawRotation, this.yawSpeed)
		} else {
			isTurning = this.yaw.calculateRotation(basType.yawMaxVelocity, this.lastYawRotation, basType.yawAcceleration)
		}
		@Pc(54) val deltaYaw: Int = this.yaw.rotation - yaw
		if (deltaYaw == 0) {
			this.turningTicks = 0
			this.yaw.setRotation(this.lastYawRotation)
		} else {
			this.turningTicks++
		}
		if (isTurning) {
			if (basType.rollAcceleration != 0) {
				if (deltaYaw <= 0) {
					this.roll.calculateRotation(basType.rollMaxVelocity, -basType.rollTargetAngle, basType.rollAcceleration)
				} else {
					this.roll.calculateRotation(basType.rollMaxVelocity, basType.rollTargetAngle, basType.rollAcceleration)
				}
			}
			if (basType.pitchAcceleration != 0) {
				this.pitch.calculateRotation(basType.pitchMaxVelocity, basType.pitchTargetAngle, basType.pitchAcceleration)
			}
		} else {
			if (basType.rollAcceleration == 0) {
				this.roll.setRotation(0)
			} else {
				this.roll.calculateRotation(basType.rollMaxVelocity, 0, basType.rollAcceleration)
			}
			if (basType.pitchAcceleration == 0) {
				this.pitch.setRotation(0)
			} else {
				this.pitch.calculateRotation(basType.pitchMaxVelocity, 0, basType.pitchAcceleration)
			}
		}
		return deltaYaw
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "([I[II)V")
	fun updateWornTargets(@OriginalArg(0) flags: IntArray?, @OriginalArg(1) targets: IntArray?) {
		if (this.wornTargets == null && targets != null) {
			this.wornTargets = IntArray(12)
		} else if (targets == null) {
			this.wornTargets = null
			return
		}
		@Pc(22) var i: Int = 0
		while (i < this.wornTargets!!.size) {
			this.wornTargets!![i] = -1
			i++
		}
		@Pc(37) var j: Int = 0
		while (j < targets!!.size) {
			@Pc(43) var flag: Int = flags!![j]
			@Pc(45) var slot: Int = 0
			while (slot < this.wornTargets!!.size) {
				if ((flag and 0x1) != 0) {
					this.wornTargets!![slot] = targets[j]
				}
				flag = flag shr 0x1
				slot++
			}
			j++
		}
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(Lclient!ha;ILclient!bca;Z[Lclient!ka;)V")
	fun updateParticles(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(2) matrix: Matrix?, @OriginalArg(3) alreadyInitialized: Boolean, @OriginalArg(4) models: Array<Model?>?) {
		if (!alreadyInitialized) {
			@Pc(13) val model: Model? = models!![0]
			@Pc(17) val model2: Model? = models[1]
			@Pc(21) val model3: Model? = models[2]
			if (model != null) {
				model.updateParticlePosition(matrix)
				@Pc(28) var length: Int = 0
				@Pc(30) var modelsWithEmissiveTriangles: Int = 0
				@Pc(32) var effectiveVertexCount: Int = 0
				@Pc(34) var modelsWithEffectiveVertices: Int = 0
				@Pc(37) val modelParticleEmitters: Array<EmissiveTriangle?>? = model.getEmissiveTriangles()
				@Pc(40) val modelParticleEffectors: Array<EffectiveVertex?>? = model.getEffectiveVertices()
				if (modelParticleEffectors != null) {
					modelsWithEffectiveVertices++
					effectiveVertexCount = modelParticleEffectors.size
				}
				if (modelParticleEmitters != null) {
					length = modelParticleEmitters.size
					modelsWithEmissiveTriangles++
				}
				@Pc(58) var modelParticleEmitters2: Array<EmissiveTriangle?>? = null
				@Pc(60) var modelParticleEffectors2: Array<EffectiveVertex?>? = null
				if (model2 != null) {
					model2.updateParticlePosition(matrix)
					modelParticleEmitters2 = model2.getEmissiveTriangles()
					modelParticleEffectors2 = model2.getEffectiveVertices()
				}
				if (modelParticleEffectors2 != null) {
					modelsWithEffectiveVertices++
					effectiveVertexCount += modelParticleEffectors2.size
				}
				if (modelParticleEmitters2 != null) {
					modelsWithEmissiveTriangles++
					length += modelParticleEmitters2.size
				}
				@Pc(89) var modelParticleEmitters3: Array<EmissiveTriangle?>? = null
				@Pc(91) var modelParticleEffectors3: Array<EffectiveVertex?>? = null
				if (model3 != null) {
					model3.updateParticlePosition(matrix)
					modelParticleEmitters3 = model3.getEmissiveTriangles()
					modelParticleEffectors3 = model3.getEffectiveVertices()
				}
				if (modelParticleEffectors3 != null) {
					modelsWithEffectiveVertices++
					effectiveVertexCount += modelParticleEffectors3.size
				}
				if (modelParticleEmitters3 != null) {
					length += modelParticleEmitters3.size
					modelsWithEmissiveTriangles++
				}
				if ((this.particleSystem == null || this.particleSystem!!.removed) && (modelsWithEmissiveTriangles > 0 || modelsWithEffectiveVertices > 0)) {
					this.particleSystem = ParticleSystem.get(ClientState.clientCycle, true)
				}
				if (this.particleSystem != null) {
					@Pc(145) val modelParticleEmitters4: Array<EmissiveTriangle?>?
					if (modelsWithEmissiveTriangles != 1) {
						modelParticleEmitters4 = arrayOfNulls<EmissiveTriangle>(length)
						@Pc(147) var trianglePosition: Int = 0
						if (modelParticleEmitters != null) {
							Arrays.copy(modelParticleEmitters, 0, modelParticleEmitters4, 0, modelParticleEmitters.size)
							trianglePosition = modelParticleEmitters.size
						}
						if (modelParticleEmitters2 != null) {
							Arrays.copy(modelParticleEmitters2, 0, modelParticleEmitters4, trianglePosition, modelParticleEmitters2.size)
							trianglePosition += modelParticleEmitters2.size
						}
						if (modelParticleEmitters3 != null) {
							Arrays.copy(modelParticleEmitters3, 0, modelParticleEmitters4, trianglePosition, modelParticleEmitters3.size)
						}
					} else if (modelParticleEmitters2 != null) {
						modelParticleEmitters4 = modelParticleEmitters2
					} else if (modelParticleEmitters3 == null) {
						modelParticleEmitters4 = modelParticleEmitters
					} else {
						modelParticleEmitters4 = modelParticleEmitters3
					}
					@Pc(208) val modelParticleEffectors4: Array<EffectiveVertex?>?
					if (modelsWithEffectiveVertices != 1) {
						modelParticleEffectors4 = arrayOfNulls<EffectiveVertex>(effectiveVertexCount)
						@Pc(222) var vertexPosition: Int = 0
						if (modelParticleEffectors != null) {
							Arrays.copy(modelParticleEffectors, 0, modelParticleEffectors4, 0, modelParticleEffectors.size)
							vertexPosition = modelParticleEffectors.size
						}
						if (modelParticleEffectors2 != null) {
							Arrays.copy(modelParticleEffectors2, 0, modelParticleEffectors4, vertexPosition, modelParticleEffectors2.size)
							vertexPosition += modelParticleEffectors2.size
						}
						if (modelParticleEffectors3 != null) {
							Arrays.copy(modelParticleEffectors3, 0, modelParticleEffectors4, vertexPosition, modelParticleEffectors3.size)
						}
					} else if (modelParticleEffectors2 != null) {
						modelParticleEffectors4 = modelParticleEffectors2
					} else if (modelParticleEffectors3 == null) {
						modelParticleEffectors4 = modelParticleEffectors
					} else {
						modelParticleEffectors4 = modelParticleEffectors3
					}
					this.particleSystem!!.update(toolkit, ClientState.clientCycle.toLong(), modelParticleEmitters4, modelParticleEffectors4)
				}
				this.particlesInitialized = true
			}
		} else if (this.particleSystem != null) {
			this.particleSystem!!.setClock(ClientState.clientCycle.toLong())
		}
		if (this.particleSystem != null) {
			this.particleSystem!!.updateBounds(super.level.toInt(), super.startX.toInt(), super.endX.toInt(), super.startZ.toInt(), super.endZ.toInt())
		}
	}

	@OriginalMember(owner = "client!kda", name = "finalize", descriptor = "()V")
	fun finalize() {
		if (this.particleSystem != null) {
			this.particleSystem!!.stopped()
		}
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(BI)V")
	fun setSize(@OriginalArg(1) size: Int) {
		this.size = size
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(BIZ)V")
	fun turn(@OriginalArg(1) rotation: Int, @OriginalArg(2) force: Boolean) {
		@Pc(7) val basType: BasType? = this.getBasType()
		if (force || basType!!.yawAcceleration != 0 || this.yawSpeed != 0) {
			this.lastYawRotation = rotation and 0x3FFF
			this.yaw.setRotation(this.lastYawRotation)
		}
	}

	@OriginalMember(owner = "client!kda", name = "i", descriptor = "(I)I")
	final override fun getMinY(@OriginalArg(0) check: Int): Int {
		return if (check == -25675) this.minY else -109
	}

	@OriginalMember(owner = "client!kda", name = "c", descriptor = "(Z)I")
	final override fun getModelMinY(): Int {
		return if (this.sphereRadius == -32768) 0 else this.sphereRadius
	}

	@OriginalMember(owner = "client!kda", name = "h", descriptor = "(B)I")
	open fun getSize(): Int {
		return this.size
	}

	@OriginalMember(owner = "client!kda", name = "g", descriptor = "(I)Z")
	final override fun isStationary(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!kda", name = "b", descriptor = "(B)Z")
	final override fun hasAlpha(): Boolean {
		return this.transparent
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(IIIIZI)V")
	fun playSpotAnimation(@OriginalArg(0) rotation: Int, @OriginalArg(1) spotAnimationId: Int, @OriginalArg(3) adjustmentType: Int, @OriginalArg(4) secondary: Boolean, @OriginalArg(5) packedDelayAndHeight: Int) {
		@Pc(11) val currentSpotAnimationId: Int = if (secondary) this.secondarySpotAnimation.spotAnimationId else this.primarySpotAnimation.spotAnimationId
		@Pc(49) var seqType: SeqType?
		if (spotAnimationId != -1 && currentSpotAnimationId != -1) {
			@Pc(29) val spotAnimationType: SpotAnimationType
			if (spotAnimationId == currentSpotAnimationId) {
				spotAnimationType = TypeLists.SPOT_ANIM!!.list(spotAnimationId)
				if (spotAnimationType.replay && spotAnimationType.sequenceId != -1) {
					@Pc(81) val seqType2: SeqType = TypeLists.SEQ!!.list(spotAnimationType.sequenceId)
					@Pc(84) val loopType: Int = seqType2.loopType
					if (loopType == SeqReplayMode.STOP) {
						return
					}
					if (loopType == SeqReplayMode.RESTART_LOOP) {
						if (!secondary) {
							this.primarySpotAnimation.currentLoopCount = 0
							return
						}
						this.secondarySpotAnimation.currentLoopCount = 0
						return
					}
				}
			} else {
				spotAnimationType = TypeLists.SPOT_ANIM!!.list(spotAnimationId)
				@Pc(34) val spotAnimationType2: SpotAnimationType = TypeLists.SPOT_ANIM!!.list(currentSpotAnimationId)
				if (spotAnimationType.sequenceId != -1 && spotAnimationType2.sequenceId != -1) {
					seqType = TypeLists.SEQ!!.list(spotAnimationType.sequenceId)
					@Pc(57) val seqType3: SeqType = TypeLists.SEQ!!.list(spotAnimationType2.sequenceId)
					if (seqType3.priority > seqType.priority) {
						return
					}
				}
			}
		}
		@Pc(105) val currentCycle: Int = ClientState.clientCycle
		@Pc(172) var sequenceId: Int
		if (secondary) {
			this.secondarySpotAnimation.nextFrameId = 1
			this.secondarySpotAnimation.height = packedDelayAndHeight shr 16
			this.secondarySpotAnimation.spotAnimationId = spotAnimationId
			this.secondarySpotAnimation.startCycle = (packedDelayAndHeight and 0xFFFF) + currentCycle
			this.secondarySpotAnimation.currentFrameId = 0
			this.secondarySpotAnimation.adjustmentType = adjustmentType
			this.secondarySpotAnimation.rotation = rotation
			this.secondarySpotAnimation.currentFrameDuration = 0
			if (currentCycle < this.secondarySpotAnimation.startCycle) {
				this.secondarySpotAnimation.currentFrameId = -1
			}
			if (this.secondarySpotAnimation.spotAnimationId != -1 && currentCycle == this.secondarySpotAnimation.startCycle) {
				sequenceId = TypeLists.SPOT_ANIM!!.list(this.secondarySpotAnimation.spotAnimationId).sequenceId
				if (sequenceId != -1) {
					seqType = TypeLists.SEQ!!.list(sequenceId)
					if (seqType != null && seqType.frames != null && !this.isHidden) {
						SoundManager.playSequenceSound(this, 0, seqType)
						return
					}
				}
			}
			return
		}
		this.primarySpotAnimation.currentFrameDuration = 0
		this.primarySpotAnimation.adjustmentType = adjustmentType
		this.primarySpotAnimation.rotation = rotation
		this.primarySpotAnimation.startCycle = (packedDelayAndHeight and 0xFFFF) + currentCycle
		this.primarySpotAnimation.nextFrameId = 1
		this.primarySpotAnimation.currentFrameId = 0
		this.primarySpotAnimation.spotAnimationId = spotAnimationId
		this.primarySpotAnimation.height = packedDelayAndHeight shr 16
		if (currentCycle < this.primarySpotAnimation.startCycle) {
			this.primarySpotAnimation.currentFrameId = -1
		}
		if (this.primarySpotAnimation.spotAnimationId != -1 && this.primarySpotAnimation.startCycle == currentCycle) {
			sequenceId = TypeLists.SPOT_ANIM!!.list(this.primarySpotAnimation.spotAnimationId).sequenceId
			if (sequenceId != -1) {
				seqType = TypeLists.SEQ!!.list(sequenceId)
				if (seqType != null && seqType.frames != null && !this.isHidden) {
					SoundManager.playSequenceSound(this, 0, seqType)
					return
				}
			}
		}
		return
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(IIIIIIIB)V")
	fun hit(@OriginalArg(0) hitAmount: Int, @OriginalArg(1) currentCycle: Int, @OriginalArg(2) hitsplatId: Int, @OriginalArg(3) soakAmount: Int, @OriginalArg(4) soakHitsplatId: Int, @OriginalArg(5) delay: Int, @OriginalArg(6) healthRatio: Int) {
		@Pc(7) var clearCurrentHits: Boolean = true
		@Pc(9) var allSlotsActive: Boolean = true
		@Pc(15) var i: Int = 0
		while (i < PathingEntity.maximumHits) {
			if (currentCycle < this.hitsplatCycles[i]) {
				clearCurrentHits = false
			} else {
				allSlotsActive = false
			}
			i++
		}
		@Pc(39) var hitIndex: Int = -1
		@Pc(41) var displayType: Int = -1
		@Pc(43) var cyclesVisible: Int = 0
		if (hitsplatId >= 0) {
			@Pc(50) val hitmarkType: HitSplatType = TypeLists.aClass307_2!!.list(hitsplatId)
			displayType = hitmarkType.displayType
			cyclesVisible = hitmarkType.cyclesVisible
		}
		@Pc(65) val local65: Int
		if (allSlotsActive) {
			if (displayType == -1) {
				return
			}
			hitIndex = 0
			var lowestValue: Int = 0
			if (displayType == 0) {
				lowestValue = this.hitsplatCycles[0]
			} else if (displayType == 1) {
				lowestValue = this.hitsplatText[0]
			}
			@Pc(136) var j: Int = 1
			while (j < PathingEntity.maximumHits) {
				if (displayType == 0) {
					if (this.hitsplatCycles[j] < lowestValue) {
						lowestValue = this.hitsplatCycles[j]
						hitIndex = j
					}
				} else if (displayType == 1 && this.hitsplatText[j] < lowestValue) {
					lowestValue = this.hitsplatText[j]
					hitIndex = j
				}
				j++
			}
			if (displayType == 1 && hitAmount <= lowestValue) {
				return
			}
		} else {
			if (clearCurrentHits) {
				this.hitmarkPointer = 0
			}
			var attempt: Int = 0
			while (attempt < PathingEntity.maximumHits) {
				@Pc(70) val hitmarkPointer: Byte = this.hitmarkPointer
				this.hitmarkPointer = ((this.hitmarkPointer + 1) % PathingEntity.maximumHits).toByte()
				if (this.hitsplatCycles[hitmarkPointer.toInt()] <= currentCycle) {
					hitIndex = hitmarkPointer.toInt()
					break
				}
				attempt++
			}
		}
		if (hitIndex < 0) {
			return
		}
		this.activeHitsplatIds[hitIndex] = hitsplatId
		this.hitsplatText[hitIndex] = hitAmount
		this.soakHitsplatIds[hitIndex] = soakHitsplatId
		this.soakHitsplatText[hitIndex] = soakAmount
		this.hitsplatCycles[hitIndex] = delay + cyclesVisible + currentCycle
		this.hitsplatHealthRatios[hitIndex] = healthRatio
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(ILclient!ka;)V")
	fun rotate(@OriginalArg(1) model: Model?) {
		@Pc(7) val rotation: Int = this.roll.rotation
		@Pc(17) val angleX: Int = this.pitch.rotation
		if (rotation == 0 && angleX == 0) {
			return
		}
		@Pc(26) val translateY: Int = model!!.fa() / 2
		model.H(0, -translateY, 0)
		model.VA(rotation and 0x3FFF)
		model.FA(angleX and 0x3FFF)
		model.H(0, translateY, 0)
	}

	@OriginalMember(owner = "client!kda", name = "b", descriptor = "(BI)V")
	fun turn(@OriginalArg(1) rotation: Int) {
		@Pc(7) val basType: BasType? = this.getBasType()
		if (basType!!.yawAcceleration == 0 && this.yawSpeed == 0) {
			return
		}
		this.yaw.clamp()
		@Pc(33) val delta: Int = (rotation - this.yaw.rotation) and 0x3FFF
		if (delta <= 8192) {
			this.lastYawRotation = this.yaw.rotation + delta
		} else {
			this.lastYawRotation = this.yaw.rotation + delta - 16384
		}
	}

	companion object {

		/**
		 * Not part of the original client: builds currentSequence with the initial values its fields had on
		 * this class.
		 */
		@JvmStatic
		private fun newCurrentSequence(): PathingEntitySequence {
			val group: PathingEntitySequence = PathingEntitySequence()
			group.nextFrameId = -1
			group.sequenceId = -1
			return group
		}

		@OriginalMember(owner = "client!in", name = "R", descriptor = "I")
		@JvmField
		var overheadSpriteCount: Int = 0
		@OriginalMember(owner = "client!sg", name = "E", descriptor = "Lclient!sg;")
		@JvmField
		val DEFAULT_BASTYPE: BasType = BasType()
		@OriginalMember(owner = "client!kga", name = "a", descriptor = "(IIBLclient!kda;I)V")
		@JvmStatic
		fun animateMovement(@OriginalArg(0) moveType: Int, @OriginalArg(1) rotation: Int, @OriginalArg(3) sequence: PathingEntity?, @OriginalArg(4) turnDirectIndex: Int) {
			@Pc(14) val basType: BasType? = sequence!!.getBasType()
			@Pc(23) val turnDirection: Int = (sequence.lastYawRotation - sequence.yaw.rotation) and 0x3FFF
			if (moveType == MoveSpeed.STATIONARY) {
				if (turnDirection != 0 || sequence.turningTicks > 25) {
					if (rotation < 0 && basType!!.standTurnCcwSequence != -1) {
						sequence.movingSequence.hasType = false
						sequence.movingSequence.sequenceId = basType.standTurnCcwSequence
					} else if (rotation <= 0 || basType!!.standTurnCwSequence == -1) {
						sequence.movingSequence.sequenceId = basType!!.walkSequence
					} else {
						sequence.movingSequence.sequenceId = basType.standTurnCwSequence
					}
					sequence.movingSequence.hasType = false
				} else if (!sequence.movingSequence.hasType || !basType!!.isReady(sequence.movingSequence.sequenceId)) {
					sequence.movingSequence.sequenceId = basType!!.ready()
					sequence.movingSequence.hasType = sequence.movingSequence.sequenceId != -1
				}
			} else if (sequence.faceEntity != -1 && (turnDirection >= 10240 || turnDirection <= 2048)) {
				@Pc(216) val walkDirection: Int = (MiniMenu.NPC_TURN_DIRECTION[turnDirectIndex] - sequence.yaw.rotation) and 0x3FFF
				if (moveType == MoveSpeed.RUNNING && basType!!.runningSequence != -1) {
					if (walkDirection > 2048 && walkDirection <= 6144 && basType.runDir1 != -1) {
						sequence.movingSequence.sequenceId = basType.runDir1
					} else if (walkDirection >= 10240 && walkDirection < 14336 && basType.runDir2 != -1) {
						sequence.movingSequence.sequenceId = basType.runDir2
					} else if (walkDirection <= 6144 || walkDirection >= 10240 || basType.runDir3 == -1) {
						sequence.movingSequence.sequenceId = basType.runningSequence
					} else {
						sequence.movingSequence.sequenceId = basType.runDir3
					}
				} else if (moveType == MoveSpeed.CRAWL && basType!!.teleportSequence != -1) {
					if (walkDirection > 2048 && walkDirection <= 6144 && basType.teleDir1 != -1) {
						sequence.movingSequence.sequenceId = basType.teleDir1
					} else if (walkDirection >= 10240 && walkDirection < 14336 && basType.teleDir2 != -1) {
						sequence.movingSequence.sequenceId = basType.teleDir2
					} else if (walkDirection <= 6144 || walkDirection >= 10240 || basType.teleDir3 == -1) {
						sequence.movingSequence.sequenceId = basType.teleportSequence
					} else {
						sequence.movingSequence.sequenceId = basType.teleDir3
					}
				} else if (walkDirection > 2048 && walkDirection <= 6144 && basType!!.walkDir1 != -1) {
					sequence.movingSequence.sequenceId = basType.walkDir1
				} else if (walkDirection >= 10240 && walkDirection < 14336 && basType!!.walkDir2 != -1) {
					sequence.movingSequence.sequenceId = basType.walkDir2
				} else if (walkDirection <= 6144 || walkDirection >= 10240 || basType!!.walkDir3 == -1) {
					sequence.movingSequence.sequenceId = basType!!.walkSequence
				} else {
					sequence.movingSequence.sequenceId = basType.walkDir3
				}
				sequence.movingSequence.hasType = false
			} else if (turnDirection == 0 && sequence.turningTicks <= 25) {
				if (moveType == MoveSpeed.RUNNING && basType!!.runningSequence != -1) {
					sequence.movingSequence.sequenceId = basType.runningSequence
				} else if (moveType == MoveSpeed.CRAWL && basType!!.teleportSequence != -1) {
					sequence.movingSequence.sequenceId = basType.teleportSequence
				} else {
					sequence.movingSequence.sequenceId = basType!!.walkSequence
				}
				sequence.movingSequence.hasType = false
			} else {
				if (moveType == MoveSpeed.RUNNING && basType!!.runningSequence != -1) {
					if (rotation < 0 && basType.runTurnCcwSequence != -1) {
						sequence.movingSequence.sequenceId = basType.runTurnCcwSequence
					} else if (rotation <= 0 || basType.runTurnCwSequence == -1) {
						sequence.movingSequence.sequenceId = basType.runningSequence
					} else {
						sequence.movingSequence.sequenceId = basType.runTurnCwSequence
					}
				} else if (moveType == MoveSpeed.CRAWL && basType!!.teleportSequence != -1) {
					if (rotation < 0 && basType.teleTurnCcwSequence != -1) {
						sequence.movingSequence.sequenceId = basType.teleTurnCcwSequence
					} else if (rotation <= 0 || basType.teleTurnCwSequence == -1) {
						sequence.movingSequence.sequenceId = basType.teleportSequence
					} else {
						sequence.movingSequence.sequenceId = basType.teleTurnCwSequence
					}
				} else if (rotation < 0 && basType!!.walkTurnCcwSequence != -1) {
					sequence.movingSequence.sequenceId = basType.walkTurnCcwSequence
				} else if (rotation <= 0 || basType!!.walkTurnCwSequence == -1) {
					sequence.movingSequence.sequenceId = basType!!.walkSequence
				} else {
					sequence.movingSequence.sequenceId = basType.walkTurnCwSequence
				}
				sequence.movingSequence.hasType = false
			}
		}
		@OriginalMember(owner = "client!eh", name = "a", descriptor = "(IILclient!ka;IZIIIILclient!ha;IIILclient!hg;)Lclient!ka;")
		@JvmStatic
		fun getAnimatedShadowModel(@OriginalArg(0) rotateX: Int, @OriginalArg(1) yaw: Int, @OriginalArg(2) model: Model?, @OriginalArg(3) rotateZ: Int, @OriginalArg(5) frameId: Int, @OriginalArg(6) shadowColourSrc: Int, @OriginalArg(7) size: Int, @OriginalArg(8) translateY: Int, @OriginalArg(9) toolkit: Toolkit?, @OriginalArg(10) shadowAlphaSrc: Int, @OriginalArg(11) shadowColourDst: Int, @OriginalArg(12) shadowAlphaDst: Int, @OriginalArg(13) seqType: SeqType?): Model? {
			var frameId: Int = frameId
			if (model == null) {
				return null
			}
			@Pc(11) var functionMask: Int = 2055
			if (seqType != null) {
				functionMask = seqType.getFunctionMask(-1, frameId, false) or 0x807
				functionMask = functionMask and 0xFFFFFDFF.toInt()
			}
			@Pc(53) val key: Long = (shadowColourDst.toLong() shl 48) + (size + (shadowAlphaSrc shl 16) + (shadowAlphaDst shl 24)).toLong() + (shadowColourSrc.toLong() shl 32)
			@Pc(55) val referenceCache: LRUCache = HintArrow.hintCache
			@Pc(63) var model2: Model?
			withLock(HintArrow.hintCache) {
				model2 = HintArrow.hintCache.get(key) as Model?
			}
			if (model2 == null || toolkit!!.compareFunctionMasks(model2!!.ua(), functionMask) != 0) {
				if (model2 != null) {
					functionMask = toolkit!!.combineFunctionMasks(functionMask, model2!!.ua())
				}
				@Pc(96) val segmentCount: Byte
				if (size == 1) {
					segmentCount = 9
				} else if (size == 2) {
					segmentCount = 12
				} else if (size == 3) {
					segmentCount = 15
				} else if (size == 4) {
					segmentCount = 18
				} else {
					segmentCount = 21
				}
				@Pc(133) val radii: IntArray = intArrayOf(64, 96, 128)
				@Pc(151) val mesh: Mesh = Mesh(segmentCount * 3 + 1, segmentCount * 2 * 3 + -segmentCount, 0)
				@Pc(158) val centerVertex: Int = mesh.addVertex(0, 0, 0)
				@Pc(162) val ringVertices: Array<IntArray> = Array(3) { IntArray(segmentCount.toInt()) }
				@Pc(170) val local170: Int
				@Pc(174) val local174: Int
				@Pc(176) val local176: Int
				@Pc(200) val local200: Int
				@Pc(164) var i: Int = 0
				while (i < 3) {
					val radiusZ: Int = radii[i]
					val radiusY: Int = radii[i]
					var segment: Int = 0
					while (segment < segmentCount) {
						@Pc(184) val angle: Int = (segment shl 14) / segmentCount
						@Pc(192) val vertexZ: Int = (TextureCombineSource.SINE[angle] * radiusZ) shr 14
						val vertexY: Int = (TextureCombineSource.COSINE[angle] * radiusY) shr 14
						ringVertices[i][segment] = mesh.addVertex(0, vertexY, vertexZ)
						segment++
					}
					i++
				}
				var ring: Int = 0
				while (ring < 3) {
					val dstWeight: Int = (ring * 256 + 128) / 3
					val srcWeight: Int = 256 - dstWeight
					@Pc(251) val alpha: Byte = ((dstWeight * shadowAlphaDst + shadowAlphaSrc * srcWeight) shr 8).toByte()
					@Pc(296) val colour: Short = (((((shadowColourDst and 0xFC00) * dstWeight + srcWeight * (shadowColourSrc and 0xFC00)) and 0xFC0000) + (((shadowColourDst and 0x380) * dstWeight + srcWeight * (shadowColourSrc and 0x380)) and 0x38000) + ((dstWeight * (shadowColourDst and 0x7F) + (shadowColourSrc and 0x7F) * srcWeight) and 0x7F00)) shr 8).toShort()
					var segment: Int = 0
					while (segment < segmentCount) {
						if (ring == 0) {
							mesh.addFace(centerVertex, 1.toByte(), (-1).toByte(), (-1).toShort(), colour, ringVertices[0][(segment + 1) % segmentCount], alpha, ringVertices[0][segment])
						} else {
							mesh.addFace(ringVertices[ring - 1][segment], 1.toByte(), (-1).toByte(), (-1).toShort(), colour, ringVertices[ring - 1][(segment + 1) % segmentCount], alpha, ringVertices[ring][(segment + 1) % segmentCount])
							mesh.addFace(ringVertices[ring - 1][segment], 1.toByte(), (-1).toByte(), (-1).toShort(), colour, ringVertices[ring][(segment + 1) % segmentCount], alpha, ringVertices[ring][segment])
						}
						segment++
					}
					ring++
				}
				model2 = toolkit!!.createModel(mesh, functionMask, HintArrow.hintPreferenceFlags, 64, 768)
				@Pc(420) val referenceCache2: LRUCache = HintArrow.hintCache
				withLock(HintArrow.hintCache) {
					HintArrow.hintCache.put(model2, key)
				}
			}
			@Pc(436) val minX: Int = model.V()
			@Pc(439) val maxX: Int = model.RA()
			@Pc(442) val minZ: Int = model.HA()
			@Pc(445) val maxZ: Int = model.G()
			@Pc(447) var animFrameset: FrameSet? = null
			if (seqType != null) {
				@Pc(454) val packedFrame: Int = seqType.frames!![frameId]
				animFrameset = TypeLists.SEQ!!.listFrameSet(packedFrame shr 16)
				frameId = packedFrame and 0xFFFF
			}
			if (animFrameset == null) {
				model2 = model2!!.copy(3.toByte(), functionMask, true)
				model2!!.O((maxX - minX) shr 1, 128, (maxZ - minZ) shr 1)
				model2!!.H((maxX + minX) shr 1, 0, (maxZ + minZ) shr 1)
			} else {
				model2 = model2!!.copy(3.toByte(), functionMask, true)
				model2!!.O((maxX - minX) shr 1, 128, (maxZ - minZ) shr 1)
				model2!!.H((maxX + minX) shr 1, 0, (maxZ + minZ) shr 1)
				model2!!.animateShadowFrame(frameId, animFrameset)
			}
			if (rotateX != 0) {
				model2!!.FA(rotateX)
			}
			if (rotateZ != 0) {
				model2!!.VA(rotateZ)
			}
			if (translateY != 0) {
				model2!!.H(0, translateY, 0)
			}
			return model2
		}

		@OriginalMember(owner = "client!sj", name = "a", descriptor = "(ILclient!kda;)V")
		@JvmStatic
		fun updateAnimations(@OriginalArg(1) pathingEntity: PathingEntity?) {
			@Pc(15) var seqType: SeqType?
			if (pathingEntity!!.movingSequence.sequenceId != -1) {
				seqType = TypeLists.SEQ!!.list(pathingEntity.movingSequence.sequenceId)
				if (seqType == null || seqType.frames == null) {
					pathingEntity.movingSequence.hasType = false
					pathingEntity.movingSequence.sequenceId = -1
				} else {
					label357@ do {
						pathingEntity.movingSequence.currentFrameDuration++
						if (pathingEntity.movingSequence.currentFrameId < seqType!!.frames!!.size && seqType.durations!![pathingEntity.movingSequence.currentFrameId] < pathingEntity.movingSequence.currentFrameDuration) {
							pathingEntity.movingSequence.nextFrameId++
							pathingEntity.movingSequence.currentFrameId++
							pathingEntity.movingSequence.currentFrameDuration = 1
							if (!pathingEntity.isHidden) {
								SoundManager.playSequenceSound(pathingEntity, pathingEntity.movingSequence.currentFrameId, seqType)
							}
						}
						if (pathingEntity.movingSequence.currentFrameId >= seqType.frames!!.size) {
							pathingEntity.movingSequence.currentFrameId = 0
							pathingEntity.movingSequence.currentFrameDuration = 0
							if (pathingEntity.movingSequence.hasType) {
								pathingEntity.movingSequence.sequenceId = pathingEntity.getBasType()!!.ready()
								if (pathingEntity.movingSequence.sequenceId == -1) {
									pathingEntity.movingSequence.hasType = false
									break@label357
								}
								seqType = TypeLists.SEQ!!.list(pathingEntity.movingSequence.sequenceId)
							}
							if (!pathingEntity.isHidden) {
								SoundManager.playSequenceSound(pathingEntity, pathingEntity.movingSequence.currentFrameId, seqType)
							}
						}
						pathingEntity.movingSequence.nextFrameId = pathingEntity.movingSequence.currentFrameId + 1
						if (seqType!!.frames == null) {
							pathingEntity.movingSequence.sequenceId = -1
							pathingEntity.movingSequence.hasType = false
						} else if (pathingEntity.movingSequence.nextFrameId >= seqType.frames!!.size) {
							pathingEntity.movingSequence.nextFrameId = 0
						}
					} while (false)
				}
			}
			@Pc(161) var sequenceId: Int
			@Pc(169) var seqType2: SeqType?
			@Pc(158) var spotAnimationType: SpotAnimationType
			if (pathingEntity.primarySpotAnimation.spotAnimationId != -1 && ClientState.clientCycle >= pathingEntity.primarySpotAnimation.startCycle) {
				spotAnimationType = TypeLists.SPOT_ANIM!!.list(pathingEntity.primarySpotAnimation.spotAnimationId)
				sequenceId = spotAnimationType.sequenceId
				if (sequenceId == -1) {
					pathingEntity.primarySpotAnimation.spotAnimationId = -1
				} else {
					label358@ do {
						seqType2 = TypeLists.SEQ!!.list(sequenceId)
						if (spotAnimationType.replay) {
							if (seqType2!!.interruptType == 3) {
								if (pathingEntity.previousMovementStepsLeft > 0 && pathingEntity.forceMovementT1Delay <= ClientState.clientCycle && ClientState.clientCycle > pathingEntity.forceMovementT2Delay) {
									pathingEntity.primarySpotAnimation.spotAnimationId = -1
									break@label358
								}
							} else if (seqType2!!.interruptType == 1 && pathingEntity.previousMovementStepsLeft > 0 && pathingEntity.forceMovementT1Delay <= ClientState.clientCycle && ClientState.clientCycle > pathingEntity.forceMovementT2Delay) {
								pathingEntity.primarySpotAnimation.startCycle = ClientState.clientCycle + 1
								break@label358
							}
						}
						if (seqType2 == null || seqType2!!.frames == null) {
							pathingEntity.primarySpotAnimation.spotAnimationId = -1
						} else {
							if (pathingEntity.primarySpotAnimation.currentFrameId < 0) {
								pathingEntity.primarySpotAnimation.currentFrameId = 0
								if (!pathingEntity.isHidden) {
									SoundManager.playSequenceSound(pathingEntity, 0, seqType2)
								}
							}
							pathingEntity.primarySpotAnimation.currentFrameDuration++
							if (pathingEntity.primarySpotAnimation.currentFrameId < seqType2!!.frames!!.size && seqType2!!.durations!![pathingEntity.primarySpotAnimation.currentFrameId] < pathingEntity.primarySpotAnimation.currentFrameDuration) {
								pathingEntity.primarySpotAnimation.currentFrameId++
								pathingEntity.primarySpotAnimation.currentFrameDuration = 1
								if (!pathingEntity.isHidden) {
									SoundManager.playSequenceSound(pathingEntity, pathingEntity.primarySpotAnimation.currentFrameId, seqType2)
								}
							}
							if (seqType2!!.frames!!.size <= pathingEntity.primarySpotAnimation.currentFrameId) {
								if (spotAnimationType.replay) {
									pathingEntity.primarySpotAnimation.currentLoopCount++
									pathingEntity.primarySpotAnimation.currentFrameId -= seqType2!!.replayInterval
									if (seqType2!!.replayCount <= pathingEntity.primarySpotAnimation.currentLoopCount) {
										pathingEntity.primarySpotAnimation.spotAnimationId = -1
									} else if (pathingEntity.primarySpotAnimation.currentFrameId < 0 || pathingEntity.primarySpotAnimation.currentFrameId >= seqType2!!.frames!!.size) {
										pathingEntity.primarySpotAnimation.spotAnimationId = -1
									} else if (!pathingEntity.isHidden) {
										SoundManager.playSequenceSound(pathingEntity, pathingEntity.primarySpotAnimation.currentFrameId, seqType2)
									}
								} else {
									pathingEntity.primarySpotAnimation.spotAnimationId = -1
								}
							}
							pathingEntity.primarySpotAnimation.nextFrameId = pathingEntity.primarySpotAnimation.currentFrameId + 1
							if (seqType2!!.frames!!.size <= pathingEntity.primarySpotAnimation.nextFrameId) {
								if (spotAnimationType.replay) {
									pathingEntity.primarySpotAnimation.nextFrameId -= seqType2!!.replayInterval
									if (pathingEntity.primarySpotAnimation.currentLoopCount + 1 >= seqType2!!.replayCount) {
										pathingEntity.primarySpotAnimation.nextFrameId = -1
									} else if (pathingEntity.primarySpotAnimation.nextFrameId < 0 || seqType2!!.frames!!.size <= pathingEntity.primarySpotAnimation.nextFrameId) {
										pathingEntity.primarySpotAnimation.nextFrameId = -1
									}
								} else {
									pathingEntity.primarySpotAnimation.nextFrameId = -1
								}
							}
						}
					} while (false)
				}
			}
			if (pathingEntity.secondarySpotAnimation.spotAnimationId != -1 && ClientState.clientCycle >= pathingEntity.secondarySpotAnimation.startCycle) {
				spotAnimationType = TypeLists.SPOT_ANIM!!.list(pathingEntity.secondarySpotAnimation.spotAnimationId)
				sequenceId = spotAnimationType.sequenceId
				if (sequenceId == -1) {
					pathingEntity.secondarySpotAnimation.spotAnimationId = -1
				} else {
					label362@ do {
						seqType2 = TypeLists.SEQ!!.list(sequenceId)
						if (spotAnimationType.replay) {
							if (seqType2!!.interruptType == 3) {
								if (pathingEntity.previousMovementStepsLeft > 0 && ClientState.clientCycle >= pathingEntity.forceMovementT1Delay && ClientState.clientCycle > pathingEntity.forceMovementT2Delay) {
									pathingEntity.secondarySpotAnimation.spotAnimationId = -1
									break@label362
								}
							} else if (seqType2!!.interruptType == 1 && pathingEntity.previousMovementStepsLeft > 0 && ClientState.clientCycle >= pathingEntity.forceMovementT1Delay && pathingEntity.forceMovementT2Delay < ClientState.clientCycle) {
								pathingEntity.secondarySpotAnimation.startCycle = ClientState.clientCycle + 1
								break@label362
							}
						}
						if (seqType2 == null || seqType2!!.frames == null) {
							pathingEntity.secondarySpotAnimation.spotAnimationId = -1
						} else {
							if (pathingEntity.secondarySpotAnimation.currentFrameId < 0) {
								pathingEntity.secondarySpotAnimation.currentFrameId = 0
								if (!pathingEntity.isHidden) {
									SoundManager.playSequenceSound(pathingEntity, 0, seqType2)
								}
							}
							pathingEntity.secondarySpotAnimation.currentFrameDuration++
							if (seqType2!!.frames!!.size > pathingEntity.secondarySpotAnimation.currentFrameId && seqType2!!.durations!![pathingEntity.secondarySpotAnimation.currentFrameId] < pathingEntity.secondarySpotAnimation.currentFrameDuration) {
								pathingEntity.secondarySpotAnimation.currentFrameId++
								pathingEntity.secondarySpotAnimation.currentFrameDuration = 1
								if (!pathingEntity.isHidden) {
									SoundManager.playSequenceSound(pathingEntity, pathingEntity.secondarySpotAnimation.currentFrameId, seqType2)
								}
							}
							if (pathingEntity.secondarySpotAnimation.currentFrameId >= seqType2!!.frames!!.size) {
								if (spotAnimationType.replay) {
									pathingEntity.secondarySpotAnimation.currentLoopCount++
									pathingEntity.secondarySpotAnimation.currentFrameId -= seqType2!!.replayInterval
									if (seqType2!!.replayCount <= pathingEntity.secondarySpotAnimation.currentLoopCount) {
										pathingEntity.secondarySpotAnimation.spotAnimationId = -1
									} else if (pathingEntity.secondarySpotAnimation.currentFrameId < 0 || pathingEntity.secondarySpotAnimation.currentFrameId >= seqType2!!.frames!!.size) {
										pathingEntity.secondarySpotAnimation.spotAnimationId = -1
									} else if (!pathingEntity.isHidden) {
										SoundManager.playSequenceSound(pathingEntity, pathingEntity.secondarySpotAnimation.currentFrameId, seqType2)
									}
								} else {
									pathingEntity.secondarySpotAnimation.spotAnimationId = -1
								}
							}
							pathingEntity.secondarySpotAnimation.nextFrameId = pathingEntity.secondarySpotAnimation.currentFrameId + 1
							if (seqType2!!.frames!!.size <= pathingEntity.secondarySpotAnimation.nextFrameId) {
								if (spotAnimationType.replay) {
									pathingEntity.secondarySpotAnimation.nextFrameId -= seqType2!!.replayInterval
									if (pathingEntity.secondarySpotAnimation.currentLoopCount + 1 >= seqType2!!.replayCount) {
										pathingEntity.secondarySpotAnimation.nextFrameId = -1
									} else if (pathingEntity.secondarySpotAnimation.nextFrameId < 0 || seqType2!!.frames!!.size <= pathingEntity.secondarySpotAnimation.nextFrameId) {
										pathingEntity.secondarySpotAnimation.nextFrameId = -1
									}
								} else {
									pathingEntity.secondarySpotAnimation.nextFrameId = -1
								}
							}
						}
					} while (false)
				}
			}
			if (pathingEntity.currentSequence.sequenceId != -1 && pathingEntity.currentSequence.speed <= 1) {
				seqType = TypeLists.SEQ!!.list(pathingEntity.currentSequence.sequenceId)
				if (seqType.interruptType == 3) {
					if (pathingEntity.previousMovementStepsLeft > 0 && pathingEntity.forceMovementT1Delay <= ClientState.clientCycle && ClientState.clientCycle > pathingEntity.forceMovementT2Delay) {
						pathingEntity.currentSequence.sequenceId = -1
						pathingEntity.currentAnimations = null
					}
				} else if (seqType.interruptType == 1 && pathingEntity.previousMovementStepsLeft > 0 && pathingEntity.forceMovementT1Delay <= ClientState.clientCycle && pathingEntity.forceMovementT2Delay < ClientState.clientCycle) {
					pathingEntity.currentSequence.speed = 2
				}
			}
			if (pathingEntity.currentSequence.sequenceId != -1 && pathingEntity.currentSequence.speed == 0) {
				seqType = TypeLists.SEQ!!.list(pathingEntity.currentSequence.sequenceId)
				if (seqType == null || seqType.frames == null) {
					pathingEntity.currentSequence.sequenceId = -1
					pathingEntity.currentAnimations = null
				} else {
					pathingEntity.currentSequence.currentFrameDuration++
					if (seqType.frames!!.size > pathingEntity.currentSequence.currentFrameId && pathingEntity.currentSequence.currentFrameDuration > seqType.durations!![pathingEntity.currentSequence.currentFrameId]) {
						pathingEntity.currentSequence.currentFrameId++
						pathingEntity.currentSequence.currentFrameDuration = 1
						if (!pathingEntity.isHidden) {
							SoundManager.playSequenceSound(pathingEntity, pathingEntity.currentSequence.currentFrameId, seqType)
						}
					}
					if (seqType.frames!!.size <= pathingEntity.currentSequence.currentFrameId) {
						pathingEntity.currentSequence.currentFrameId -= seqType.replayInterval
						pathingEntity.currentSequence.currentLoopCount++
						if (seqType.replayCount <= pathingEntity.currentSequence.currentLoopCount) {
							pathingEntity.currentAnimations = null
							pathingEntity.currentSequence.sequenceId = -1
						} else if (pathingEntity.currentSequence.currentFrameId < 0 || seqType.frames!!.size <= pathingEntity.currentSequence.currentFrameId) {
							pathingEntity.currentAnimations = null
							pathingEntity.currentSequence.sequenceId = -1
						} else if (!pathingEntity.isHidden) {
							SoundManager.playSequenceSound(pathingEntity, pathingEntity.currentSequence.currentFrameId, seqType)
						}
					}
					pathingEntity.currentSequence.nextFrameId = pathingEntity.currentSequence.currentFrameId + 1
					if (pathingEntity.currentSequence.nextFrameId >= seqType.frames!!.size) {
						pathingEntity.currentSequence.nextFrameId -= seqType.replayInterval
						if (seqType.replayCount <= pathingEntity.currentSequence.currentLoopCount + 1) {
							pathingEntity.currentSequence.nextFrameId = -1
						} else if (pathingEntity.currentSequence.nextFrameId < 0 || seqType.frames!!.size <= pathingEntity.currentSequence.nextFrameId) {
							pathingEntity.currentSequence.nextFrameId = -1
						}
					}
				}
			}
			if (pathingEntity.currentSequence.speed > 0) {
				pathingEntity.currentSequence.speed--
			}
			sequenceId = 0
			while (sequenceId < pathingEntity.sequences.size) {
				@Pc(975) val pathingEntitySequence: PathingEntitySequence? = pathingEntity.sequences[sequenceId]
				if (pathingEntitySequence != null) {
					if (pathingEntitySequence.speed > 0) {
						pathingEntitySequence.speed--
					} else {
						@Pc(994) val seqType3: SeqType? = TypeLists.SEQ!!.list(pathingEntitySequence.sequenceId)
						if (seqType3 == null || seqType3.frames == null) {
							pathingEntity.sequences[sequenceId] = null
						} else {
							pathingEntitySequence.currentFrameDuration++
							if (pathingEntitySequence.currentFrameId < seqType3.frames!!.size && seqType3.durations!![pathingEntitySequence.currentFrameId] < pathingEntitySequence.currentFrameDuration) {
								pathingEntitySequence.currentFrameId++
								pathingEntitySequence.currentFrameDuration = 1
								if (!pathingEntity.isHidden) {
									SoundManager.playSequenceSound(pathingEntity, pathingEntitySequence.currentFrameId, seqType3)
								}
							}
							if (pathingEntitySequence.currentFrameId >= seqType3.frames!!.size) {
								pathingEntitySequence.currentFrameId -= seqType3.replayInterval
								pathingEntitySequence.currentLoopCount++
								if (pathingEntitySequence.currentLoopCount >= seqType3.replayCount) {
									pathingEntity.sequences[sequenceId] = null
								} else if (pathingEntitySequence.currentFrameId < 0 || seqType3.frames!!.size <= pathingEntitySequence.currentFrameId) {
									pathingEntity.sequences[sequenceId] = null
								} else if (!pathingEntity.isHidden) {
									SoundManager.playSequenceSound(pathingEntity, pathingEntitySequence.currentFrameId, seqType3)
								}
							}
							pathingEntitySequence.nextFrameId = pathingEntitySequence.currentFrameId + 1
							if (seqType3.frames!!.size <= pathingEntitySequence.nextFrameId) {
								pathingEntitySequence.nextFrameId -= seqType3.replayInterval
								if (seqType3.replayCount <= pathingEntitySequence.currentLoopCount + 1) {
									pathingEntitySequence.nextFrameId = -1
								} else if (pathingEntitySequence.nextFrameId < 0 || seqType3.frames!!.size <= pathingEntitySequence.nextFrameId) {
									pathingEntitySequence.nextFrameId = -1
								}
							}
						}
					}
				}
				sequenceId++
			}
		}
		@OriginalMember(owner = "client!gia", name = "a", descriptor = "(BLclient!kda;)I")
		@JvmStatic
		fun turnTo(@OriginalArg(1) pathingEntity: PathingEntity?): Int {
			if (pathingEntity!!.yawSpeed == 0) {
				return 0
			}
			@Pc(66) var deltaZ: Int
			@Pc(59) var deltaX: Int
			if (pathingEntity.faceEntity != -1) {
				@Pc(19) var pathingEntity2: PathingEntity? = null
				if (pathingEntity.faceEntity < 32768) {
					@Pc(45) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(pathingEntity.faceEntity.toLong()) as NPCEntityNode?
					if (npcEntityNode != null) {
						pathingEntity2 = npcEntityNode.npc
					}
				} else if (pathingEntity.faceEntity >= 32768) {
					pathingEntity2 = PlayerUpdate.players[pathingEntity.faceEntity - 32768]
				}
				if (pathingEntity2 != null) {
					deltaX = pathingEntity.x - pathingEntity2.x
					deltaZ = pathingEntity.z - pathingEntity2.z
					if (deltaX != 0 || deltaZ != 0) {
						pathingEntity.turn((kotlin.math.atan2(deltaX.toDouble(), deltaZ.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF)
					}
				}
			}
			if (pathingEntity is PlayerEntity) {
				@Pc(92) val playerEntity: PlayerEntity = pathingEntity
				if (playerEntity.faceDirection != -1 && (playerEntity.pathPointer == 0 || playerEntity.stalledStepCount > 0)) {
					playerEntity.turn(playerEntity.faceDirection)
					playerEntity.faceDirection = -1
				}
			} else if (pathingEntity is NPCEntity) {
				@Pc(122) val npcEntity: NPCEntity = pathingEntity
				if (npcEntity.faceX != -1 && (npcEntity.pathPointer == 0 || npcEntity.stalledStepCount > 0)) {
					deltaX = npcEntity.x - (npcEntity.faceX - MapRegion.sceneBaseX - MapRegion.sceneBaseX) * 256
					deltaZ = npcEntity.z - (npcEntity.faceY - MapRegion.sceneBaseY - MapRegion.sceneBaseY) * 256
					if (deltaX != 0 || deltaZ != 0) {
						npcEntity.turn((kotlin.math.atan2(deltaX.toDouble(), deltaZ.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF)
					}
					npcEntity.faceX = -1
				}
			}
			return pathingEntity.rotationTick()
		}

		@OriginalMember(owner = "client!client", name = "a", descriptor = "()V")
		@JvmStatic
		fun buildOverlappingEntityList() {
			PathingEntity.overheadSpriteCount = 0
			@Pc(3) var i: Int = 0
			while (i < NPCList.NPC_UPDATE_INDEX) {
				@Pc(14) val npcEntity: NPCEntity? = (NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[i].toLong()) as NPCEntityNode).npc
				if (npcEntity!!.isHidden && npcEntity!!.getOverheadSprite() != -1) {
					@Pc(32) val sizeOffset: Int = (npcEntity!!.getSize() - 1) * 256 + 252
					@Pc(39) val tileX: Int = (npcEntity!!.x - sizeOffset) shr 9
					@Pc(46) val tileZ: Int = (npcEntity!!.z - sizeOffset) shr 9
					@Pc(53) val pathingEntity: PathingEntity? = Scene.getLargestPathingEntityOnTile(tileX, npcEntity!!.level.toInt(), tileZ)
					if (pathingEntity != null) {
						@Pc(58) var entityIndex: Int = pathingEntity.playerIndex
						if (pathingEntity is NPCEntity) {
							entityIndex += 2048
						}
						if (pathingEntity.overheadStackCount == 0 && pathingEntity.getOverheadSprite() != -1) {
							PathingEntity.ENTITY_INDEX_REFERENCES[PathingEntity.overheadSpriteCount] = entityIndex
							PathingEntity.NPC_INDEX_REFERENCES[PathingEntity.overheadSpriteCount] = entityIndex
							PathingEntity.overheadSpriteCount++
							pathingEntity.overheadStackCount++
						}
						PathingEntity.ENTITY_INDEX_REFERENCES[PathingEntity.overheadSpriteCount] = entityIndex
						PathingEntity.NPC_INDEX_REFERENCES[PathingEntity.overheadSpriteCount] = npcEntity!!.playerIndex + 2048
						PathingEntity.overheadSpriteCount++
						pathingEntity.overheadStackCount++
					}
				}
				i++
			}
			ArrayUtils.quicksort(PathingEntity.NPC_INDEX_REFERENCES, PathingEntity.ENTITY_INDEX_REFERENCES, 0, PathingEntity.overheadSpriteCount - 1)
		}

		@OriginalMember(owner = "client!nr", name = "a", descriptor = "I")
		@JvmField
		var maximumHits: Int = 4

		@OriginalMember(owner = "client!mf", name = "a", descriptor = "(ILclient!kda;)V")
		@JvmStatic
		fun processForceMovementT2(@OriginalArg(1) pathingEntity: PathingEntity?) {
			@Pc(5) var shouldInterpolate: Boolean = false
			if (ClientState.clientCycle == pathingEntity!!.forceMovementT2Delay || pathingEntity.currentSequence.sequenceId == -1 || pathingEntity.currentSequence.speed != 0) {
				shouldInterpolate = true
			} else {
				@Pc(28) val seqType: SeqType = TypeLists.SEQ!!.list(pathingEntity.currentSequence.sequenceId)
				if (seqType.tweened || pathingEntity.currentSequence.currentFrameDuration + 1 > seqType.durations!![pathingEntity.currentSequence.currentFrameId]) {
					shouldInterpolate = true
				}
			}
			if (shouldInterpolate) {
				@Pc(60) val duration: Int = pathingEntity.forceMovementT2Delay - pathingEntity.forceMovementT1Delay
				@Pc(66) val elapsed: Int = ClientState.clientCycle - pathingEntity.forceMovementT1Delay
				@Pc(77) val startX: Int = pathingEntity.forceMovementT1XOff * 512 + pathingEntity.getSize() * 256
				@Pc(89) val startZ: Int = pathingEntity.forceMovementT1YOff * 512 + pathingEntity.getSize() * 256
				@Pc(100) val endX: Int = pathingEntity.forceMovementT2XOff * 512 + pathingEntity.getSize() * 256
				@Pc(112) val endZ: Int = pathingEntity.forceMovementT2YOff * 512 + pathingEntity.getSize() * 256
				pathingEntity.x = (endX * elapsed + (duration - elapsed) * startX) / duration
				pathingEntity.z = (startZ * (duration - elapsed) + elapsed * endZ) / duration
			}
			pathingEntity.stalledStepCount = 0
			if (pathingEntity.forceMoveDirection == 0) {
				pathingEntity.turn(8192, false)
			}
			if (pathingEntity.forceMoveDirection == 1) {
				pathingEntity.turn(12288, false)
			}
			if (pathingEntity.forceMoveDirection == 2) {
				pathingEntity.turn(0, false)
			}
			if (pathingEntity.forceMoveDirection == 3) {
				pathingEntity.turn(4096, false)
			}
		}
		@OriginalMember(owner = "client!cu", name = "a", descriptor = "(ZLclient!kda;)V")
		@JvmStatic
		fun updateEquipmentRotation(@OriginalArg(1) pathingEntity: PathingEntity?) {
			if (pathingEntity!!.wornTargets == null && pathingEntity.wornRotation == null) {
				return
			}
			@Pc(14) var allCleared: Boolean = true
			@Pc(16) var i: Int = 0
			while (i < pathingEntity.wornTargets!!.size) {
				@Pc(20) var target: Int = -1
				if (pathingEntity.wornTargets != null) {
					target = pathingEntity.wornTargets!![i]
				}
				if (target != -1) {
					allCleared = false
					@Pc(74) val dx: Int
					@Pc(89) val dz: Int
					@Pc(59) val local59: Int
					if ((target and 0xC0000000.toInt()) == -1073741824) {
						val packedCoord: Int = target and 0xFFFFFFF
						@Pc(63) val targetX: Int = packedCoord shr 14
						dx = pathingEntity.x - (targetX - MapRegion.sceneBaseX) * 512 - 256
						@Pc(78) val targetZ: Int = packedCoord and 0x3FFF
						dz = pathingEntity.z - (targetZ - MapRegion.sceneBaseY) * 512 - 256
					} else if ((target and 0x8000) == 0) {
						@Pc(102) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(target.toLong()) as NPCEntityNode?
						if (npcEntityNode == null) {
							pathingEntity.rotateEquipment(i, -1)
							i++
							continue
						}
						@Pc(115) val npcEntity: NPCEntity? = npcEntityNode.npc
						dz = pathingEntity.z - npcEntity!!.z
						dx = pathingEntity.x - npcEntity!!.x
					} else {
						val targetPlayerIndex: Int = target and 0x7FFF
						@Pc(138) val playerEntity: PlayerEntity? = PlayerUpdate.players[targetPlayerIndex]
						if (playerEntity == null) {
							pathingEntity.rotateEquipment(i, -1)
							i++
							continue
						}
						dz = pathingEntity.z - playerEntity.z
						dx = pathingEntity.x - playerEntity.x
					}
					if (dx != 0 || dz != 0) {
						pathingEntity.rotateEquipment(i, (kotlin.math.atan2(dx.toDouble(), dz.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF)
					}
				} else if (!pathingEntity.rotateEquipment(i, -1)) {
					allCleared = false
				}
				i++
			}
			if (allCleared) {
				pathingEntity.wornTargets = null
				pathingEntity.wornRotation = null
			}
		}

		@OriginalMember(owner = "client!cb", name = "d", descriptor = "[I")
		@JvmField
		val NPC_INDEX_REFERENCES: IntArray = IntArray(500)

		@OriginalMember(owner = "client!wha", name = "w", descriptor = "[I")
		@JvmField
		val ENTITY_INDEX_REFERENCES: IntArray = IntArray(500)
		@OriginalMember(owner = "client!fca", name = "a", descriptor = "(Lclient!kda;B)V")
		@JvmStatic
		fun processForceMovementT1(@OriginalArg(0) pathingEntity: PathingEntity?) {
			@Pc(8) val remainingCycles: Int = pathingEntity!!.forceMovementT1Delay - ClientState.clientCycle
			@Pc(22) val destX: Int = pathingEntity.forceMovementT1XOff * 512 + pathingEntity.getSize() * 256
			@Pc(34) val destZ: Int = pathingEntity.forceMovementT1YOff * 512 + pathingEntity.getSize() * 256
			pathingEntity.stalledStepCount = 0
			pathingEntity.z += (destZ - pathingEntity.z) / remainingCycles
			pathingEntity.x += (destX - pathingEntity.x) / remainingCycles
			if (pathingEntity.forceMoveDirection == 0) {
				pathingEntity.turn(8192)
			}
			if (pathingEntity.forceMoveDirection == 1) {
				pathingEntity.turn(12288)
			}
			if (pathingEntity.forceMoveDirection == 2) {
				pathingEntity.turn(0)
			}
			if (pathingEntity.forceMoveDirection == 3) {
				pathingEntity.turn(4096)
			}
		}
	}
}
