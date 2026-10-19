package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.client.features.SmokeTest
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.ExplicitRuntimeException
import com.jagex.game.runetek5.MoveSpeed
import com.jagex.game.runetek5.PathFinder
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance
import com.jagex.game.runetek5.config.bas.BasType
import com.jagex.game.runetek5.config.enumtype.EnumType
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType
import com.jagex.game.runetek5.graphics.model.particle.Silhouette
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.Cursor
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.scene.tile.Tile
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.defaults.EquipmentDefaultsType
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.sound.player.area.AmbientSound
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.ModeWhere
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.OrthoMode
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!ke")
class PlayerEntity : PathingEntity(), Player {

	/* Not part of the original client: the plugin API's view of a player (plugin.api.Player). */

	override fun name(): String? {
		return this.username
	}

	override fun combatLevel(): Int {
		return this.combatLevel
	}

	override fun index(): Int {
		return this.playerIndex
	}

	override fun isLocalPlayer(): Boolean {
		return this === PlayerUpdate.localPlayer
	}


	@OriginalMember(owner = "client!ke", name = "td", descriptor = "I")
	private var basId: Int = 0

	@OriginalMember(owner = "client!ke", name = "Ed", descriptor = "I")
	@JvmField
	var basePositionY: Int = 0

	@OriginalMember(owner = "client!ke", name = "Jd", descriptor = "Lclient!oo;")
	@JvmField
	var playerModel: PlayerAppearance? = null

	@OriginalMember(owner = "client!ke", name = "Kd", descriptor = "Ljava/lang/String;")
	@JvmField
	var username: String? = null

	@OriginalMember(owner = "client!ke", name = "Rd", descriptor = "Ljava/lang/String;")
	@JvmField
	var displayName: String? = null

	@OriginalMember(owner = "client!ke", name = "Wd", descriptor = "I")
	@JvmField
	var basePositionX: Int = 0

	@OriginalMember(owner = "client!ke", name = "qd", descriptor = "Z")
	@JvmField
	var vorbis: Boolean = false

	@OriginalMember(owner = "client!ke", name = "pd", descriptor = "I")
	@JvmField
	var combatLevel: Int = 0

	@OriginalMember(owner = "client!ke", name = "Id", descriptor = "I")
	@JvmField
	var walkingSoundEffectId: Int = -1

	@OriginalMember(owner = "client!ke", name = "ud", descriptor = "Z")
	@JvmField
	var shouldSuppressMovingAnimation: Boolean = false

	@OriginalMember(owner = "client!ke", name = "zd", descriptor = "I")
	@JvmField
	var teleportingSoundEffectId: Int = -1

	@OriginalMember(owner = "client!ke", name = "Md", descriptor = "Z")
	@JvmField
	var hasPendingPositionUpdate: Boolean = false

	@OriginalMember(owner = "client!ke", name = "sd", descriptor = "I")
	@JvmField
	var runningSoundEffectId: Int = -1

	@OriginalMember(owner = "client!ke", name = "vd", descriptor = "I")
	@JvmField
	var prayerHeadIconId: Int = -1

	@OriginalMember(owner = "client!ke", name = "be", descriptor = "Z")
	@JvmField
	var pMapdot: Boolean = false

	@OriginalMember(owner = "client!ke", name = "ee", descriptor = "I")
	@JvmField
	var cbLevelWithSumm: Int = 0

	@OriginalMember(owner = "client!ke", name = "xd", descriptor = "I")
	@JvmField
	var faceDirection: Int = -1

	@OriginalMember(owner = "client!ke", name = "Yd", descriptor = "Z")
	@JvmField
	var hidden: Boolean = false

	@OriginalMember(owner = "client!ke", name = "he", descriptor = "I")
	@JvmField
	var defaultSoundEffectId: Int = -1

	@OriginalMember(owner = "client!ke", name = "Pd", descriptor = "I")
	@JvmField
	var teamId: Int = 0

	@OriginalMember(owner = "client!ke", name = "ae", descriptor = "I")
	@JvmField
	var isNpc: Int = 0

	@OriginalMember(owner = "client!ke", name = "de", descriptor = "B")
	private var titleId: Byte = 0

	@OriginalMember(owner = "client!ke", name = "fe", descriptor = "I")
	@JvmField
	var ambientSoundVolume: Int = 255

	@OriginalMember(owner = "client!ke", name = "Nd", descriptor = "I")
	@JvmField
	var skullId: Int = -1

	@OriginalMember(owner = "client!ke", name = "Zd", descriptor = "B")
	private var titleType: Byte = 0

	@OriginalMember(owner = "client!ke", name = "Ld", descriptor = "B")
	private var gender: Byte = 0

	@OriginalMember(owner = "client!ke", name = "ie", descriptor = "I")
	@JvmField
	var cbLevelRelated: Int = -1

	@OriginalMember(owner = "client!ke", name = "le", descriptor = "I")
	@JvmField
	var elo: Int = 0

	@OriginalMember(owner = "client!ke", name = "c", descriptor = "(Lclient!ha;I)V")
	override fun particleCycle(@OriginalArg(0) toolkit: Toolkit?) {
		if (this.playerModel == null || !super.particlesInitialized && !this.getAnimatedBodyModel(0, toolkit)) {
			return
		}
		@Pc(20) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.rotate(super.yaw.getRotation())
		matrix.translate(super.x, super.y - 5, super.z)
		this.updateParticles(toolkit, matrix, super.particlesInitialized, super.models)
		super.models[2] = null
		super.models[1] = null
		super.models[0] = null
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(IIB)V")
	fun move(@OriginalArg(0) baseY: Int, @OriginalArg(1) baseX: Int) {
		super.previousMovementStepsLeft = 0
		super.movementBaseX[0] = baseX
		super.pathPointer = 0
		super.stalledStepCount = 0
		super.movementBaseY[0] = baseY
		@Pc(31) val size: Int = this.getSize()
		super.z = size * 256 + super.movementBaseY[0] * 512
		super.x = super.movementBaseX[0] * 512 + size * 256
		if (PlayerUpdate.localPlayer === this) {
			EnvironmentManager.requestDirectEnvironmentTransition()
		}
		if (super.particleSystem != null) {
			super.particleSystem!!.restart()
		}
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(ZI)Ljava/lang/String;")
	fun getDisplayName(): String? {
		return this.displayName
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(BIII)V")
	fun addMovementStep(@OriginalArg(0) moveSpeed: Byte, @OriginalArg(1) baseX: Int, @OriginalArg(3) baseY: Int) {
		if (super.pathPointer < 9) {
			super.pathPointer++
		}
		@Pc(26) var pathPointer: Int = super.pathPointer
		while (pathPointer > 0) {
			super.movementBaseX[pathPointer] = super.movementBaseX[pathPointer - 1]
			super.movementBaseY[pathPointer] = super.movementBaseY[pathPointer - 1]
			super.movementQueueSpeed[pathPointer] = super.movementQueueSpeed[pathPointer - 1]
			pathPointer--
		}
		super.movementBaseX[0] = baseX
		super.movementQueueSpeed[0] = moveSpeed
		super.movementBaseY[0] = baseY
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(Lclient!ha;III)Z")
	override fun picked(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) mouseY: Int, @OriginalArg(2) mouseX: Int): Boolean {
		if (this.playerModel == null || !this.getAnimatedBodyModel(131072, toolkit)) {
			return false
		}
		@Pc(19) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		@Pc(24) val rotation: Int = super.yaw.getRotation()
		matrix.rotate(rotation)
		matrix.translate(super.x, super.y, super.z)
		@Pc(37) var picked: Boolean = false
		@Pc(39) var i: Int = 0
		while (i < super.models.size) {
			if (super.models[i] != null && (if (OrthoMode.orthographicProjection) super.models[i]!!.pickedOrtho(mouseX, mouseY, matrix, true, 0, HintArrow.scaledOrthoZoom) else super.models[i]!!.picked(mouseX, mouseY, matrix, true, 0))) {
				picked = true
				break
			}
			i++
		}
		super.models[2] = null
		super.models[1] = null
		super.models[0] = null
		return picked
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(BLclient!cea;)V")
	fun decodeAppearance(@OriginalArg(1) buffer: Packet?) {
		buffer!!.currentPosition = 0
		@Pc(12) val flags: Int = buffer.readUByte()
		this.gender = (flags and 0x1).toByte()
		@Pc(21) val vorbis: Boolean = this.vorbis
		this.vorbis = (flags and 0x2) != 0
		@Pc(41) val showElo: Boolean = (flags and 0x4) != 0
		@Pc(45) val size: Int = super.getSize()
		this.setSize(((flags shr 3) and 0x7) + 1)
		this.titleType = ((flags shr 6) and 0x3).toByte()
		super.x += (this.getSize() - size) shl 8
		super.z += (this.getSize() - size) shl 8
		this.titleId = buffer.readByte()
		this.skullId = buffer.readByte().toInt()
		this.prayerHeadIconId = buffer.readByte().toInt()
		this.hidden = buffer.readByte().toInt() == 1
		if (ServerEnvironment.LIVE === ModeWhere.WTRC && LoginManager.staffModLevel >= 2) {
			this.hidden = false
		}
		this.teamId = 0
		@Pc(132) var morphId: Int = -1
		@Pc(135) val bodyAppearances: IntArray = IntArray(12)
		@Pc(158) val local158: Int
		@Pc(164) val local164: Int
		@Pc(205) val local205: Int
		@Pc(137) var i: Int = 0
		while (i < 12) {
			@Pc(143) val equipmentHigh: Int = buffer.readUByte()
			if (equipmentHigh == 0) {
				bodyAppearances[i] = 0
			} else {
				val equipmentLow: Int = buffer.readUByte()
				var equipmentId: Int = equipmentLow + (equipmentHigh shl 8)
				if (i == 0 && equipmentId == 65535) {
					morphId = buffer.readUShort()
					this.teamId = buffer.readUByte()
					break
				}
				if (equipmentId >= 32768) {
					equipmentId = EquipmentDefaultsType.customizableObjSlots!![equipmentId - 32768]
					bodyAppearances[i] = equipmentId or 0x40000000
					val objTeamId: Int = TypeLists.OBJ!!.list(equipmentId).teamId
					if (objTeamId != 0) {
						this.teamId = objTeamId
					}
				} else {
					bodyAppearances[i] = (equipmentId - 256) or Int.MIN_VALUE
				}
			}
			i++
		}
		@Pc(229) val newRecolorDstIndices: IntArray = IntArray(5)
		var recolorSlot: Int = 0
		while (recolorSlot < 5) {
			var colourIndex: Int = buffer.readUByte()
			if (PlayerAppearance.recolorPaletteDst!!.size < 1 || colourIndex < 0 || PlayerAppearance.recolorPaletteDst!![0]!![recolorSlot]!!.size <= colourIndex) {
				colourIndex = 0
			}
			newRecolorDstIndices[recolorSlot] = colourIndex
			recolorSlot++
		}
		this.basId = buffer.readUShort()
		this.username = buffer.readString()
		this.displayName = this.username
		if (PlayerUpdate.localPlayer === this) {
			PlayerUpdate.localUsername = this.username
		}
		this.combatLevel = buffer.readUByte()
		if (showElo) {
			this.elo = buffer.readUShort()
			this.cbLevelWithSumm = this.combatLevel
			if (this.elo == 65535) {
				this.elo = -1
			}
			this.cbLevelRelated = -1
		} else {
			this.elo = 0
			this.cbLevelWithSumm = buffer.readUByte()
			this.cbLevelRelated = buffer.readUByte()
			if (this.cbLevelRelated == 255) {
				this.cbLevelRelated = -1
			}
		}
		val previousIsNpc: Int = this.isNpc
		this.isNpc = buffer.readUByte()
		@Pc(371) val local371: Int
		if (this.isNpc == 0) {
			AmbientSound.unlinkPlayerSound(this)
		} else {
			val previousWalkingSound: Int = this.walkingSoundEffectId
			@Pc(368) val previousTeleportSound: Int = this.teleportingSoundEffectId
			val previousDefaultSound: Int = this.defaultSoundEffectId
			@Pc(374) val previousRunningSound: Int = this.runningSoundEffectId
			@Pc(377) val previousAmbientVolume: Int = this.ambientSoundVolume
			this.walkingSoundEffectId = buffer.readUShort()
			this.teleportingSoundEffectId = buffer.readUShort()
			this.defaultSoundEffectId = buffer.readUShort()
			this.runningSoundEffectId = buffer.readUShort()
			this.ambientSoundVolume = buffer.readUByte()
			if (this.vorbis != vorbis || this.isNpc != previousIsNpc || this.walkingSoundEffectId != previousWalkingSound || previousTeleportSound != this.teleportingSoundEffectId || previousDefaultSound != this.defaultSoundEffectId || this.runningSoundEffectId != previousRunningSound || previousAmbientVolume != this.ambientSoundVolume) {
				AmbientSound.updatePlayerAmbientSound(this)
			}
		}
		if (this.playerModel == null) {
			this.playerModel = PlayerAppearance()
		}
		val previousMorphId: Int = this.playerModel!!.morphism
		@Pc(467) val recolorDstIndices: IntArray? = this.playerModel!!.recolorDstIndices
		this.playerModel!!.setAppearance(morphId, this.getBasId(), bodyAppearances, this.gender.toInt() == 1, newRecolorDstIndices)
		if (morphId != previousMorphId) {
			super.x = (super.movementBaseX[0] shl 9) + (this.getSize() shl 8)
			super.z = (super.movementBaseY[0] shl 9) + (this.getSize() shl 8)
		}
		if (super.playerIndex == PlayerUpdate.myPlayerIndex && recolorDstIndices != null) {
			recolorSlot = 0
			while (recolorSlot < newRecolorDstIndices.size) {
				if (newRecolorDstIndices[recolorSlot] != recolorDstIndices[recolorSlot]) {
					TypeLists.OBJ!!.spriteCacheReset()
					break
				}
				recolorSlot++
			}
		}
		if (super.particleSystem != null) {
			super.particleSystem!!.restart()
		}
		if (super.movingSequence.sequenceId == -1 || !super.movingSequence.hasType) {
			return
		}
		@Pc(567) val basType: BasType? = this.getBasType()
		if (!basType!!.isReady(super.movingSequence.sequenceId)) {
			super.movingSequence.hasType = false
			super.movingSequence.sequenceId = -1
			return
		}
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(ILclient!ha;ILclient!ka;IILclient!bca;I)V")
	private fun drawHint(@OriginalArg(0) deltaX: Int, @OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) maxDistanceSquared: Int, @OriginalArg(3) model: Model?, @OriginalArg(5) deltaY: Int, @OriginalArg(6) matrix: Matrix?, @OriginalArg(7) modelId: Int) {
		@Pc(14) val distanceSquared: Int = deltaX * deltaX + deltaY * deltaY
		if (distanceSquared < 262144 || maxDistanceSquared < distanceSquared) {
			return
		}
		@Pc(34) val angle: Int = (kotlin.math.atan2(deltaX.toDouble(), deltaY.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
		@Pc(46) val model2: Model? = PlayerEntity.createPlayerHint(super.modelTranslateY, super.modelRotateX, super.modelRotateZ, angle, modelId, toolkit)
		if (model2 != null) {
			toolkit!!.C(false)
			model2.render(matrix, null, 0)
			toolkit.C(true)
		}
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(IIIILclient!ka;ILclient!bca;Lclient!ha;I)V")
	private fun drawHintOrtho(@OriginalArg(1) deltaX: Int, @OriginalArg(2) orthoZoom: Int, @OriginalArg(3) modelId: Int, @OriginalArg(4) model: Model?, @OriginalArg(5) maxDistanceSquared: Int, @OriginalArg(6) matrix: Matrix?, @OriginalArg(7) toolkit: Toolkit?, @OriginalArg(8) deltaY: Int) {
		@Pc(17) val distanceSquared: Int = deltaY * deltaY + deltaX * deltaX
		if (distanceSquared < 262144 || maxDistanceSquared < distanceSquared) {
			return
		}
		@Pc(39) val angle: Int = (kotlin.math.atan2(deltaX.toDouble(), deltaY.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
		@Pc(51) val model2: Model? = PlayerEntity.createPlayerHint(super.modelTranslateY, super.modelRotateX, super.modelRotateZ, angle, modelId, toolkit)
		if (model2 != null) {
			toolkit!!.C(false)
			model2.renderOrtho(matrix, null, orthoZoom, 0)
			toolkit.C(true)
		}
	}

	@OriginalMember(owner = "client!ke", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	override fun getCylinder(@OriginalArg(0) toolkit: Toolkit?): Silhouette? {
		return null
	}

	@OriginalMember(owner = "client!ke", name = "b", descriptor = "(Z)V")
	override fun removeTileShadow() {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	override fun shareLight(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) offsetY: Int, @OriginalArg(2) hideSharedFaces: Boolean, @OriginalArg(3) entity: Entity?, @OriginalArg(4) offsetZ: Int, @OriginalArg(6) offsetX: Int) {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(IIBI)V")
	fun move(@OriginalArg(0) baseY: Int, @OriginalArg(2) moveSpeed: Byte, @OriginalArg(3) baseX: Int) {
		if (super.currentSequence.sequenceId != -1 && TypeLists.SEQ!!.list(super.currentSequence.sequenceId).moveType == 1) {
			super.currentSequence.sequenceId = -1
			super.currentAnimations = null
		}
		@Pc(42) var spotAnimationType: SpotAnimationType
		if (super.primarySpotAnimation.spotAnimationId != -1) {
			spotAnimationType = TypeLists.SPOT_ANIM!!.list(super.primarySpotAnimation.spotAnimationId)
			if (spotAnimationType.replay && spotAnimationType.sequenceId != -1 && TypeLists.SEQ!!.list(spotAnimationType.sequenceId).moveType == 1) {
				super.primarySpotAnimation.spotAnimationId = -1
			}
		}
		if (super.secondarySpotAnimation.spotAnimationId != -1) {
			spotAnimationType = TypeLists.SPOT_ANIM!!.list(super.secondarySpotAnimation.spotAnimationId)
			if (spotAnimationType.replay && spotAnimationType.sequenceId != -1 && TypeLists.SEQ!!.list(spotAnimationType.sequenceId).moveType == 1) {
				super.secondarySpotAnimation.spotAnimationId = -1
			}
		}
		this.faceDirection = -1
		if (baseX < 0 || baseX >= MapRegion.sceneSizeX || baseY < 0 || baseY >= MapRegion.sceneSizeY) {
			this.move(baseY, baseX)
		} else if (super.movementBaseX[0] >= 0 && MapRegion.sceneSizeX > super.movementBaseX[0] && super.movementBaseY[0] >= 0 && MapRegion.sceneSizeY > super.movementBaseY[0]) {
			if (moveSpeed.toInt() == MoveSpeed.RUNNING) {
				PlayerEntity.findRoute(baseY, baseX, this)
			}
			this.addMovementStep(moveSpeed, baseX, baseY)
		} else {
			this.move(baseY, baseX)
		}
	}

	@OriginalMember(owner = "client!ke", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	override fun render(@OriginalArg(1) toolkit: Toolkit?): PickableEntity? {
		if (this.playerModel == null || !this.getAnimatedBodyModel(2048, toolkit)) {
			return null
		}
		@Pc(19) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		@Pc(24) val rotation: Int = super.yaw.getRotation()
		matrix.rotate(rotation)
		@Pc(42) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![super.level.toInt()]!![super.x shr Scene.tileUnits]!![super.z shr Scene.tileUnits]
		if (tile == null || tile.groundDecoration == null) {
			super.groundContouredOffsetY = (super.groundContouredOffsetY.toFloat() - super.groundContouredOffsetY.toFloat() / 10.0f).toInt()
		} else {
			@Pc(69) val offsetDelta: Int = super.groundContouredOffsetY - tile.groundDecoration!!.offsetY
			super.groundContouredOffsetY = (super.groundContouredOffsetY.toFloat() - offsetDelta.toFloat() / 10.0f).toInt()
		}
		matrix.translate(super.x, super.y - super.groundContouredOffsetY - 20, super.z)
		@Pc(95) var pickableEntity: PickableEntity? = null
		super.transparent = false
		// Not part of the original client: no flat shadow model under the player when the toolkit casts
		// real shadows (634HD's sun shadows, Toolkit.castsShadows), or it would have two.
		if (GamePreferences.uid!!.characterShadows!!.getValue() == 1 && !toolkit!!.castsShadows()) {
			@Pc(110) val basType: BasType? = this.getBasType()
			if (basType!!.rendersShadow && (this.playerModel!!.morphism == -1 || TypeLists.NPC!!.list(this.playerModel!!.morphism).shadowed)) {
				@Pc(144) val seqType: SeqType? = if (super.currentSequence.sequenceId != -1 && super.currentSequence.speed == 0) TypeLists.SEQ!!.list(super.currentSequence.sequenceId) else null
				@Pc(165) val seqType2: SeqType? = if (super.movingSequence.sequenceId == -1 || this.shouldSuppressMovingAnimation || super.movingSequence.hasType && seqType != null) null else TypeLists.SEQ!!.list(super.movingSequence.sequenceId)
				@Pc(197) val model: Model? = PathingEntity.getAnimatedShadowModel(super.modelRotateX, rotation, super.models[0], super.modelRotateZ, if (seqType2 == null) super.currentSequence.currentFrameId else super.movingSequence.currentFrameId, 0, 1, super.modelTranslateY, toolkit, 160, 0, 240, if (seqType2 == null) seqType else seqType2)
				if (model != null) {
					pickableEntity = PickableEntity.obtain(super.models.size + 1, true)
					super.transparent = true
					toolkit.C(false)
					if (OrthoMode.orthographicProjection) {
						model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons!![super.models.size], HintArrow.scaledOrthoZoom, 0)
					} else {
						model.render(matrix, pickableEntity.boundingBoxPolygons!![super.models.size], 0)
					}
					toolkit.C(true)
				}
			}
		}
		@Pc(258) val local258: Int
		if (PlayerUpdate.localPlayer === this) {
			matrix.applyTranslation(super.x, super.y, super.z)
			var hintIndex: Int = PlayerUpdate.HINT_ARROWS.size - 1
			while (hintIndex >= 0) {
				@Pc(264) val hintArrow: HintArrow? = PlayerUpdate.HINT_ARROWS[hintIndex]
				if (hintArrow != null && hintArrow.modelId != -1) {
					@Pc(296) val local296: Int
					if (hintArrow.targetType == 1) {
						@Pc(284) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(hintArrow.targetIndex.toLong()) as NPCEntityNode?
						if (npcEntityNode != null) {
							@Pc(289) val npcEntity: NPCEntity? = npcEntityNode.npc
							val deltaX: Int = npcEntity!!.x - PlayerUpdate.localPlayer!!.x
							@Pc(303) val deltaY: Int = npcEntity!!.z - PlayerUpdate.localPlayer!!.z
							if (OrthoMode.orthographicProjection) {
								this.drawHintOrtho(deltaX, HintArrow.scaledOrthoZoom, hintArrow.modelId, super.models[0], 92160000, matrix, toolkit, deltaY)
							} else {
								this.drawHint(deltaX, toolkit, 92160000, super.models[0], deltaY, matrix, hintArrow.modelId)
							}
						}
					}
					@Pc(358) val local358: Int
					if (hintArrow.targetType == 2) {
						@Pc(349) val deltaX: Int = hintArrow.x + 256 - PlayerUpdate.localPlayer!!.x
						val deltaY: Int = hintArrow.y + 256 - PlayerUpdate.localPlayer!!.z
						var maxDistanceSquared: Int = hintArrow.distance shl 9
						maxDistanceSquared *= maxDistanceSquared
						if (OrthoMode.orthographicProjection) {
							this.drawHintOrtho(deltaX, HintArrow.scaledOrthoZoom, hintArrow.modelId, super.models[0], maxDistanceSquared, matrix, toolkit, deltaY)
						} else {
							this.drawHint(deltaX, toolkit, maxDistanceSquared, super.models[0], deltaY, matrix, hintArrow.modelId)
						}
					}
					if (hintArrow.targetType == 10 && hintArrow.targetIndex >= 0 && PlayerUpdate.players.size > hintArrow.targetIndex) {
						@Pc(424) val playerEntity: PlayerEntity? = PlayerUpdate.players[hintArrow.targetIndex]
						if (playerEntity != null) {
							val deltaX: Int = playerEntity.x - PlayerUpdate.localPlayer!!.x
							val deltaY: Int = playerEntity.z - PlayerUpdate.localPlayer!!.z
							if (OrthoMode.orthographicProjection) {
								this.drawHintOrtho(deltaX, HintArrow.scaledOrthoZoom, hintArrow.modelId, super.models[0], 92160000, matrix, toolkit, deltaY)
							} else {
								this.drawHint(deltaX, toolkit, 92160000, super.models[0], deltaY, matrix, hintArrow.modelId)
							}
						}
					}
				}
				hintIndex--
			}
			matrix.rotate(rotation)
			matrix.translate(super.x, super.y, super.z)
		}
		matrix.rotate(rotation)
		matrix.translate(super.x, super.y - super.groundContouredOffsetY - 5, super.z)
		if (pickableEntity == null) {
			pickableEntity = PickableEntity.obtain(super.models.size, true)
		}
		this.updateParticles(toolkit, matrix, false, super.models)
		if (OrthoMode.orthographicProjection) {
			var modelIndex: Int = 0
			while (modelIndex < super.models.size) {
				if (super.models[modelIndex] != null) {
					super.models[modelIndex]!!.renderOrtho(matrix, pickableEntity!!.boundingBoxPolygons!![modelIndex], HintArrow.scaledOrthoZoom, if (PlayerUpdate.localPlayer === this) 1 else 0)
				}
				modelIndex++
			}
		} else {
			var modelIndex: Int = 0
			while (modelIndex < super.models.size) {
				if (super.models[modelIndex] != null) {
					super.models[modelIndex]!!.render(matrix, pickableEntity!!.boundingBoxPolygons!![modelIndex], if (PlayerUpdate.localPlayer === this) 1 else 0)
				}
				modelIndex++
			}
		}
		if (super.particleSystem != null) {
			@Pc(597) val particleList: ParticleList = super.particleSystem!!.getList()
			if (OrthoMode.orthographicProjection) {
				toolkit.renderOrtho(particleList, HintArrow.scaledOrthoZoom)
			} else {
				toolkit.render(particleList)
			}
		}
		var modelIndex: Int = 0
		while (modelIndex < super.models.size) {
			if (super.models[modelIndex] != null) {
				super.transparent = super.transparent or super.models[modelIndex]!!.F()
			}
			modelIndex++
		}
		// Not part of the original client: the smoke test (client.test=smoke) sees which models were drawn.
		SmokeTest.drawn(this, super.models)
		super.models[2] = null
		super.models[1] = null
		super.models[0] = null
		super.drawCycle = SceneDrawer.timesSceneRendered
		return pickableEntity
	}

	@OriginalMember(owner = "client!ke", name = "b", descriptor = "(ZI)Ljava/lang/String;")
	fun getUsernameWithTitle(): String {
		@Pc(7) var string: String = ""
		if (PlayerEntity.playerNamePrefixes != null) {
			string = string + PlayerEntity.playerNamePrefixes!![this.titleType.toInt()]
		}
		@Pc(31) val titleEnumIds: IntArray?
		if (this.gender.toInt() == 1 && PlayerEntity.femaleTitleEnumIds != null) {
			titleEnumIds = PlayerEntity.femaleTitleEnumIds
		} else {
			titleEnumIds = PlayerEntity.defaultTitleEnumIds
		}
		if (titleEnumIds != null && titleEnumIds[this.titleType.toInt()] != -1) {
			@Pc(57) val enumType: EnumType? = TypeLists.ENUM!!.list(titleEnumIds[this.titleType.toInt()])
			if (enumType!!.valueType == 's') {
				string = string + enumType.getString(this.titleId.toInt() and 0xFF)
			} else {
				ExplicitRuntimeException.reportError("gdn1", Throwable())
				titleEnumIds[this.titleType.toInt()] = -1
			}
		}
		string = string + this.username
		if (PlayerEntity.playerNameSuffixes != null) {
			string = string + PlayerEntity.playerNameSuffixes!![this.titleType.toInt()]
		}
		return string
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(I)I")
	override fun getOverheadSprite(): Int {
		return -1
	}

	@OriginalMember(owner = "client!ke", name = "i", descriptor = "(B)Z")
	fun hasAppearance(): Boolean {
		return this.playerModel != null
	}

	@OriginalMember(owner = "client!ke", name = "j", descriptor = "(I)Z")
	override fun hasTileShadow(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!ke", name = "h", descriptor = "(B)I")
	override fun getSize(): Int {
		return if (this.playerModel == null || this.playerModel!!.morphism == -1) super.getSize() else TypeLists.NPC!!.list(this.playerModel!!.morphism).size
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(ILclient!ha;I)Z")
	private fun getAnimatedBodyModel(@OriginalArg(0) functionMask: Int, @OriginalArg(1) toolkit: Toolkit?): Boolean {
		var functionMask: Int = functionMask
		@Pc(7) val initialFunctionMask: Int = functionMask
		@Pc(11) val basType: BasType? = this.getBasType()
		@Pc(26) val seqType: SeqType? = if (super.currentSequence.sequenceId != -1 && super.currentSequence.speed == 0) TypeLists.SEQ!!.list(super.currentSequence.sequenceId) else null
		@Pc(47) val seqType2: SeqType? = if (super.movingSequence.sequenceId == -1 || this.shouldSuppressMovingAnimation || super.movingSequence.hasType && seqType != null) null else TypeLists.SEQ!!.list(super.movingSequence.sequenceId)
		@Pc(50) val modelWidth: Int = basType!!.modelWidth
		@Pc(53) val modelLength: Int = basType.modelLength
		if (modelWidth != 0 || modelLength != 0 || basType.rollTargetAngle != 0 || basType.pitchTargetAngle != 0) {
			functionMask = functionMask or 0x7
		}
		@Pc(75) val rotation: Int = super.yaw.getRotation()
		@Pc(94) val tinted: Boolean = super.opacity.toInt() != 0 && super.tintStartCycle <= ClientState.clientCycle && super.tintEndCycle > ClientState.clientCycle
		if (tinted) {
			functionMask = functionMask or 0x80000
		}
		@Pc(138) val model: Model? = this.playerModel!!.getBodyModel(PlayerVarDomain.playerVarProvider, seqType2, super.sequences, TypeLists.OBJ, rotation, seqType, super.currentSequence.nextFrameId, super.movingSequence.nextFrameId, super.wornRotation, functionMask, TypeLists.IDK, super.currentSequence.currentFrameDuration, toolkit, TypeLists.NPC, TypeLists.SEQ, super.movingSequence.currentFrameId, super.currentSequence.currentFrameId, super.movingSequence.currentFrameDuration, TypeLists.BAS)
		super.models[0] = model
		@Pc(141) val bodyModelCount: Int = PlayerAppearance.countBodyModels()
		if (GameShell.maxMemory < 96 && bodyModelCount > 50) {
			Scene.uploadAllGameObjects()
		}
		if (ModeWhere.WTRC !== ServerEnvironment.LIVE && bodyModelCount < 50) {
			@Pc(172) val targetBufferCount: Int = 50 - bodyModelCount
			while (targetBufferCount > Cursor.interaction) {
				PlayerAppearance.TEST_ENVIRONMENT_APPEARANCE_OVERRIDE_DATA!![Cursor.interaction] = ByteArray(102400)
				Cursor.interaction++
			}
			while (Cursor.interaction > targetBufferCount) {
				Cursor.interaction--
				PlayerAppearance.TEST_ENVIRONMENT_APPEARANCE_OVERRIDE_DATA!![Cursor.interaction] = null
			}
		} else if (ServerEnvironment.LIVE !== ModeWhere.WTRC) {
			Cursor.interaction = 0
			PlayerAppearance.TEST_ENVIRONMENT_APPEARANCE_OVERRIDE_DATA = arrayOfNulls<ByteArray>(50)
		}
		if (model == null) {
			return false
		}
		super.sphereRadius = model.fa()
		super.minY = model.ma()
		this.rotate(model)
		if (modelWidth == 0 && modelLength == 0) {
			this.hillChange(rotation, this.getSize() shl 9, 0, this.getSize() shl 9, 0)
		} else {
			this.hillChange(rotation, modelWidth, basType.hillRotateZ, modelLength, basType.hillRotateX)
			if (super.modelRotateX != 0) {
				model.FA(super.modelRotateX)
			}
			if (super.modelRotateZ != 0) {
				model.VA(super.modelRotateZ)
			}
			if (super.modelTranslateY != 0) {
				model.H(0, super.modelTranslateY, 0)
			}
		}
		if (tinted) {
			model.adjustColours(super.hue.toInt(), super.saturation.toInt(), super.lightness.toInt(), super.opacity.toInt() and 0xFF)
		}
		@Pc(339) var fullDisplacement: Boolean
		@Pc(341) var spotFunctionMask: Int
		@Pc(391) var model2: Model?
		@Pc(407) var offsetX: Int
		@Pc(409) var offsetY: Int
		@Pc(411) var offsetZ: Int
		@Pc(495) var equipmentRotation: Int
		@Pc(523) var spotRotation: Int
		@Pc(535) var sine: Int
		@Pc(539) var cosine: Int
		@Pc(549) var rotatedX: Int
		@Pc(324) var spotAnimationType: SpotAnimationType
		if (this.shouldSuppressMovingAnimation || super.primarySpotAnimation.spotAnimationId == -1 || super.primarySpotAnimation.currentFrameId == -1) {
			super.models[1] = null
		} else {
			spotAnimationType = TypeLists.SPOT_ANIM!!.list(super.primarySpotAnimation.spotAnimationId)
			fullDisplacement = spotAnimationType.contourType.toInt() == 3 && (modelWidth != 0 || modelLength != 0)
			spotFunctionMask = initialFunctionMask
			if (fullDisplacement) {
				spotFunctionMask = initialFunctionMask or 0x7
			} else {
				if (super.primarySpotAnimation.rotation != 0) {
					spotFunctionMask = initialFunctionMask or 0x5
				}
				if (super.primarySpotAnimation.height != 0) {
					spotFunctionMask = spotFunctionMask or 0x2
				}
				if (super.primarySpotAnimation.adjustmentType >= 0) {
					spotFunctionMask = spotFunctionMask or 0x7
				}
			}
			model2 = spotAnimationType.getModel(super.primarySpotAnimation.currentFrameId, toolkit, super.primarySpotAnimation.currentFrameDuration, spotFunctionMask, super.primarySpotAnimation.nextFrameId, TypeLists.SEQ)
			super.models[1] = model2
			if (model2 != null) {
				if (super.primarySpotAnimation.adjustmentType >= 0 && basType.equipmentTransforms != null && basType.equipmentTransforms!![super.primarySpotAnimation.adjustmentType] != null) {
					offsetX = 0
					offsetY = 0
					offsetZ = 0
					if (basType.equipmentTransforms != null && basType.equipmentTransforms!![super.primarySpotAnimation.adjustmentType] != null) {
						offsetY = basType.equipmentTransforms!![super.primarySpotAnimation.adjustmentType]!![1]
						offsetZ = basType.equipmentTransforms!![super.primarySpotAnimation.adjustmentType]!![2]
						offsetX = basType.equipmentTransforms!![super.primarySpotAnimation.adjustmentType]!![0]
					}
					if (basType.projectionOffset != null && basType.projectionOffset!![super.primarySpotAnimation.adjustmentType] != null) {
						offsetY += basType.projectionOffset!![super.primarySpotAnimation.adjustmentType]!![1]
						offsetZ += basType.projectionOffset!![super.primarySpotAnimation.adjustmentType]!![2]
						offsetX += basType.projectionOffset!![super.primarySpotAnimation.adjustmentType]!![0]
					}
					if (offsetZ != 0 || offsetX != 0) {
						equipmentRotation = rotation
						if (super.wornRotation != null && super.wornRotation!![super.primarySpotAnimation.adjustmentType] != -1) {
							equipmentRotation = super.wornRotation!![super.primarySpotAnimation.adjustmentType]
						}
						spotRotation = (equipmentRotation + super.primarySpotAnimation.rotation * 2048 - rotation) and 0x3FFF
						if (spotRotation != 0) {
							model2.a(spotRotation)
						}
						sine = TextureCombineSource.SINE[spotRotation]
						cosine = TextureCombineSource.COSINE[spotRotation]
						rotatedX = (cosine * offsetX + offsetZ * sine) shr 14
						offsetZ = (offsetZ * cosine - offsetX * sine) shr 14
						offsetX = rotatedX
					}
					model2.H(offsetX, offsetY, offsetZ)
				} else if (super.primarySpotAnimation.rotation != 0) {
					model2.a(super.primarySpotAnimation.rotation * 2048)
				}
				if (super.primarySpotAnimation.height != 0) {
					model2.H(0, (-super.primarySpotAnimation.height) shl 2, 0)
				}
				if (fullDisplacement) {
					if (super.modelRotateX != 0) {
						model2.FA(super.modelRotateX)
					}
					if (super.modelRotateZ != 0) {
						model2.VA(super.modelRotateZ)
					}
					if (super.modelTranslateY != 0) {
						model2.H(0, super.modelTranslateY, 0)
					}
				}
			}
		}
		if (this.shouldSuppressMovingAnimation || super.secondarySpotAnimation.spotAnimationId == -1 || super.secondarySpotAnimation.currentFrameId == -1) {
			super.models[2] = null
		} else {
			spotAnimationType = TypeLists.SPOT_ANIM!!.list(super.secondarySpotAnimation.spotAnimationId)
			fullDisplacement = spotAnimationType.contourType.toInt() == 3 && (modelWidth != 0 || modelLength != 0)
			spotFunctionMask = initialFunctionMask
			if (fullDisplacement) {
				spotFunctionMask = initialFunctionMask or 0x7
			} else {
				if (super.secondarySpotAnimation.rotation != 0) {
					spotFunctionMask = initialFunctionMask or 0x5
				}
				if (super.secondarySpotAnimation.height != 0) {
					spotFunctionMask = spotFunctionMask or 0x2
				}
				if (super.secondarySpotAnimation.adjustmentType >= 0) {
					spotFunctionMask = spotFunctionMask or 0x7
				}
			}
			model2 = spotAnimationType.getSecondaryModel(super.secondarySpotAnimation.currentFrameDuration, super.secondarySpotAnimation.nextFrameId, toolkit, spotFunctionMask, TypeLists.SEQ, super.secondarySpotAnimation.currentFrameId)
			super.models[2] = model2
			if (model2 != null) {
				if (super.secondarySpotAnimation.adjustmentType >= 0 && basType.equipmentTransforms != null && basType.equipmentTransforms!![super.secondarySpotAnimation.adjustmentType] != null) {
					offsetX = 0
					offsetY = 0
					offsetZ = 0
					if (basType.equipmentTransforms != null && basType.equipmentTransforms!![super.secondarySpotAnimation.adjustmentType] != null) {
						offsetX = basType.equipmentTransforms!![super.secondarySpotAnimation.adjustmentType]!![0]
						offsetY = basType.equipmentTransforms!![super.secondarySpotAnimation.adjustmentType]!![1]
						offsetZ = basType.equipmentTransforms!![super.secondarySpotAnimation.adjustmentType]!![2]
					}
					if (basType.projectionOffset != null && basType.projectionOffset!![super.secondarySpotAnimation.adjustmentType] != null) {
						offsetX += basType.projectionOffset!![super.secondarySpotAnimation.adjustmentType]!![0]
						offsetY += basType.projectionOffset!![super.secondarySpotAnimation.adjustmentType]!![1]
						offsetZ += basType.projectionOffset!![super.secondarySpotAnimation.adjustmentType]!![2]
					}
					if (offsetZ != 0 || offsetX != 0) {
						equipmentRotation = rotation
						if (super.wornRotation != null && super.wornRotation!![super.secondarySpotAnimation.adjustmentType] != -1) {
							equipmentRotation = super.wornRotation!![super.secondarySpotAnimation.adjustmentType]
						}
						spotRotation = (super.secondarySpotAnimation.rotation * 2048 + equipmentRotation - rotation) and 0x3FFF
						if (spotRotation != 0) {
							model2.a(spotRotation)
						}
						sine = TextureCombineSource.SINE[spotRotation]
						cosine = TextureCombineSource.COSINE[spotRotation]
						rotatedX = (offsetZ * sine + cosine * offsetX) shr 14
						offsetZ = (cosine * offsetZ - sine * offsetX) shr 14
						offsetX = rotatedX
					}
					model2.H(offsetX, offsetY, offsetZ)
				} else if (super.secondarySpotAnimation.rotation != 0) {
					model2.a(super.secondarySpotAnimation.rotation * 2048)
				}
				if (super.secondarySpotAnimation.height != 0) {
					model2.H(0, (-super.secondarySpotAnimation.height) shl 2, 0)
				}
				if (fullDisplacement) {
					if (super.modelRotateX != 0) {
						model2.FA(super.modelRotateX)
					}
					if (super.modelRotateZ != 0) {
						model2.VA(super.modelRotateZ)
					}
					if (super.modelTranslateY != 0) {
						model2.H(0, super.modelTranslateY, 0)
					}
				}
			}
		}
		return true
	}

	@OriginalMember(owner = "client!ke", name = "f", descriptor = "(B)I")
	override fun getBasId(): Int {
		return this.basId
	}

	companion object {

		@OriginalMember(owner = "client!rw", name = "a", descriptor = "(BIIILclient!ke;)V")
		@JvmStatic
		fun findRoute(@OriginalArg(1) baseY: Int, @OriginalArg(3) baseX: Int, @OriginalArg(4) playerEntity: PlayerEntity?) {
			@Pc(10) val currentX: Int = playerEntity!!.movementBaseX[0]
			@Pc(15) val currentY: Int = playerEntity.movementBaseY[0]
			if (currentX < 0 || MapRegion.sceneSizeX <= currentX || currentY < 0 || MapRegion.sceneSizeY <= currentY || (baseX < 0 || MapRegion.sceneSizeX <= baseX || baseY < 0 || baseY >= MapRegion.sceneSizeY)) {
				return
			}
			@Pc(77) val stepCount: Int = PathFinder.findRoute(baseX, playerEntity.getSize(), 0, Camera.WALKING_STEPS_Y, true, -4, currentX, currentY, PathFinder.WALKING_STEPS_X, baseY, MapRegion.aClass104Array3[playerEntity.level.toInt()], 0, 0, 0)
			if (stepCount >= 1 && stepCount <= 3) {
				@Pc(94) var i: Int = 0
				while (i < stepCount - 1) {
					playerEntity.addMovementStep(2.toByte(), PathFinder.WALKING_STEPS_X[i], Camera.WALKING_STEPS_Y[i])
					i++
				}
			}
		}
		@OriginalMember(owner = "client!hba", name = "a", descriptor = "(Lclient!ke;IBI)V")
		@JvmStatic
		fun animatePlayer(@OriginalArg(0) playerEntity: PlayerEntity?, @OriginalArg(1) animationId: Int, @OriginalArg(3) speed: Int) {
			@Pc(11) val animationIds: IntArray = IntArray(4)
			Arrays.setVariables(animationIds, 0, animationIds.size, animationId)
			PlayerUpdate.animate(animationIds, speed, playerEntity!!)
		}
		@OriginalMember(owner = "client!vg", name = "a", descriptor = "(IIIIIBLclient!ha;)Lclient!ka;")
		@JvmStatic
		fun createPlayerHint(@OriginalArg(0) translateY: Int, @OriginalArg(1) rotateX: Int, @OriginalArg(2) rotateZ: Int, @OriginalArg(3) angle: Int, @OriginalArg(4) modelId: Int, @OriginalArg(6) toolkit: Toolkit?): Model? {
			@Pc(11) val key: Long = modelId.toLong()
			@Pc(17) var model: Model? = PlayerEntity.aClass169_28.get(key) as Model?
			if (model == null) {
				@Pc(27) val mesh: Mesh? = Mesh.fromResource(Resource.MODEL, modelId)
				if (mesh == null) {
					return null
				}
				if (mesh.version < 13) {
					mesh.upscale()
				}
				model = toolkit!!.createModel(mesh, 2055, PlayerEntity.playerHintFeatureMask, 64, 768)
				PlayerEntity.aClass169_28.put(model, key)
			}
			model = model!!.copy(2.toByte(), 2055, true)
			if (angle != 0) {
				model!!.a(angle)
			}
			if (rotateX != 0) {
				model!!.FA(rotateX)
			}
			if (rotateZ != 0) {
				model!!.VA(rotateZ)
			}
			if (translateY != 0) {
				model!!.H(0, translateY, 0)
			}
			return model
		}

		@OriginalMember(owner = "client!ti", name = "r", descriptor = "[I")
		@JvmField
		var femaleTitleEnumIds: IntArray? = null
		@OriginalMember(owner = "client!ko", name = "u", descriptor = "[Ljava/lang/String;")
		@JvmField
		var playerNamePrefixes: Array<String?>? = null

		@OriginalMember(owner = "client!jea", name = "i", descriptor = "[Ljava/lang/String;")
		@JvmField
		var playerNameSuffixes: Array<String?>? = null
		@OriginalMember(owner = "client!al", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun setPlayerHintFeatureMask(@OriginalArg(0) featureMask: Int) {
			PlayerEntity.playerHintFeatureMask = featureMask
			PlayerEntity.aClass169_28.reset()
		}

		@OriginalMember(owner = "client!pl", name = "i", descriptor = "I")
		@JvmField
		var playerHintFeatureMask: Int = 0

		@OriginalMember(owner = "client!cj", name = "b", descriptor = "(I)V")
		@JvmStatic
		fun clearPlayerHintModels() {
			PlayerEntity.aClass169_28.reset()
		}

		@OriginalMember(owner = "client!uu", name = "f", descriptor = "[I")
		@JvmField
		val playerNameAffixCounts: IntArray = intArrayOf(1, 4, 1, 2)

		@OriginalMember(owner = "client!kg", name = "j", descriptor = "[I")
		@JvmField
		var defaultTitleEnumIds: IntArray? = null

		@OriginalMember(owner = "client!lw", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun cleanPlayerHintModelCache() {
			PlayerEntity.aClass169_28.clean(5)
		}

		@OriginalMember(owner = "client!hea", name = "a", descriptor = "Lclient!jr;")
		@JvmField
		val aClass169_28: LRUCache = LRUCache(4)

		@OriginalMember(owner = "client!ik", name = "a", descriptor = "(B)V")
		@JvmStatic
		fun removePlayerHintModelSoftRefs() {
			PlayerEntity.aClass169_28.removeSoftReferences()
		}
	}
}
