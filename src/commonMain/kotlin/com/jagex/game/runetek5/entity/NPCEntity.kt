package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.NpcDefinition
import com.jagex.game.runetek5.plugin.PluginRuntime
import com.jagex.game.runetek5.config.bas.BasType
import com.jagex.game.runetek5.config.npc.NPCType
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType
import com.jagex.game.runetek5.graphics.model.particle.Silhouette
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.scene.tile.Tile
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.SoundManager
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.config.sequence.PathingEntitySequence
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.OrthoMode
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!ff")
class NPCEntity : PathingEntity(), Npc {

	/* Not part of the original client: the plugin API's view of an npc (plugin.api.Npc). */

	/** The type the npc shows now: its own, or the one the player's variables transform it into. */
	fun shownType(): NPCType? {
		var type: NPCType? = this.npcType
		if (type != null && type.alternativeIds != null) {
			type = type.getMultiNpc(PlayerVarDomain.playerVarProvider)
		}
		return type
	}

	override fun typeId(): Int {
		val type: NPCType = shownType() ?: return -1
		return type.id
	}

	override fun name(): String? {
		val type: NPCType = shownType() ?: return null
		return type.name
	}

	override fun combatLevel(): Int {
		val type: NPCType = shownType() ?: return 0
		return if (type.combatLevel < 0) 0 else type.combatLevel
	}

	override fun index(): Int {
		return this.playerIndex
	}

	override fun headIcon(): Int {
		val type: NPCType = shownType() ?: return -1
		return type.headIcons
	}

	override fun type(): NpcDefinition? {
		return shownType()
	}

	@OriginalMember(owner = "client!ff", name = "Bd", descriptor = "Lclient!bb;")
	@JvmField
	var npcType: NPCType? = null

	@OriginalMember(owner = "client!ff", name = "vd", descriptor = "I")
	@JvmField
	var faceX: Int = -1

	@OriginalMember(owner = "client!ff", name = "Id", descriptor = "I")
	@JvmField
	var faceY: Int = -1

	@OriginalMember(owner = "client!ff", name = "m", descriptor = "(I)Z")
	private fun isVisible(): Boolean {
		return this.npcType!!.visible
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	override fun shareLight(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) offsetY: Int, @OriginalArg(2) hideSharedFaces: Boolean, @OriginalArg(3) entity: Entity?, @OriginalArg(4) offsetZ: Int, @OriginalArg(6) offsetX: Int) {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!ff", name = "b", descriptor = "(Z)V")
	override fun removeTileShadow() {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(Lclient!ha;III)Z")
	override fun picked(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) mouseY: Int, @OriginalArg(2) mouseX: Int): Boolean {
		if (this.npcType == null || !this.getAnimatedBodyModel(131072, toolkit)) {
			return false
		}
		@Pc(19) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		@Pc(24) val yawRotation: Int = super.yaw.getRotation()
		matrix.rotate(yawRotation)
		matrix.translate(super.x, super.y, super.z)
		@Pc(37) var picked: Boolean = false
		@Pc(39) var i: Int = 0
		while (i < super.models.size) {
			if (super.models[i] != null) {
				@Pc(63) var var10000: Boolean
				label41@ do {
					if (this.npcType!!.sizeShift <= 0) {
						label39@ do {
							if (this.npcType!!.shadowSize == -1) {
								if (this.npcType!!.size == 1) {
									break@label39
								}
								var10000 = false
							} else {
								if (this.npcType!!.shadowSize == 1) {
									break@label39
								}
								var10000 = false
							}
							if (!var10000) {
								var10000 = false
								break@label41
							}
						} while (false)
					}
					var10000 = true
				} while (false)
				@Pc(78) val quick: Boolean = var10000
				@Pc(94) val modelPicked: Boolean
				if (OrthoMode.orthographicProjection) {
					modelPicked = super.models[i]!!.pickedOrtho(mouseX, mouseY, matrix, quick, this.npcType!!.sizeShift, HintArrow.scaledOrthoZoom)
				} else {
					modelPicked = super.models[i]!!.picked(mouseX, mouseY, matrix, quick, this.npcType!!.sizeShift)
				}
				if (modelPicked) {
					picked = true
					break
				}
			}
			i++
		}
		super.models[2] = null
		super.models[1] = null
		super.models[0] = null
		return picked
	}

	@OriginalMember(owner = "client!ff", name = "b", descriptor = "(III)V")
	fun move(@OriginalArg(0) speed: Int, @OriginalArg(1) movementType: Int) {
		@Pc(10) var baseX: Int = super.movementBaseX[0]
		@Pc(15) var baseY: Int = super.movementBaseY[0]
		if (speed == 0) {
			baseY++
		}
		if (speed == 1) {
			baseX++
			baseY++
		}
		if (speed == 2) {
			baseX++
		}
		if (speed == 3) {
			baseX++
			baseY--
		}
		if (speed == 4) {
			baseY--
		}
		if (speed == 5) {
			baseX--
			baseY--
		}
		if (speed == 6) {
			baseX--
		}
		if (speed == 7) {
			baseX--
			baseY++
		}
		if (super.currentSequence.sequenceId != -1 && TypeLists.SEQ!!.list(super.currentSequence.sequenceId).moveType == 1) {
			super.currentSequence.sequenceId = -1
			super.currentAnimations = null
		}
		@Pc(90) var spotAnimationType: SpotAnimationType
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
		if (super.pathPointer < 9) {
			super.pathPointer++
		}
		@Pc(155) var pathPointer: Int = super.pathPointer
		while (pathPointer > 0) {
			super.movementBaseX[pathPointer] = super.movementBaseX[pathPointer - 1]
			super.movementBaseY[pathPointer] = super.movementBaseY[pathPointer - 1]
			super.movementQueueSpeed[pathPointer] = super.movementQueueSpeed[pathPointer - 1]
			pathPointer--
		}
		super.movementBaseX[0] = baseX
		super.movementBaseY[0] = baseY
		super.movementQueueSpeed[0] = movementType.toByte()
	}

	@OriginalMember(owner = "client!ff", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	override fun render(@OriginalArg(1) toolkit: Toolkit?): PickableEntity? {
		if (this.npcType == null || !this.getAnimatedBodyModel(2048, toolkit)) {
			return null
		}
		@Pc(21) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		@Pc(26) val yawRotation: Int = super.yaw.getRotation()
		matrix.rotate(yawRotation)
		@Pc(44) val tile: Tile? = Scene.aClass100ArrayArrayArray3!![super.level.toInt()]!![super.x shr Scene.tileUnits]!![super.z shr Scene.tileUnits]
		if (tile == null || tile.groundDecoration == null) {
			super.groundContouredOffsetY = (super.groundContouredOffsetY.toFloat() - super.groundContouredOffsetY.toFloat() / 10.0f).toInt()
		} else {
			@Pc(57) val offsetDelta: Int = super.groundContouredOffsetY - tile.groundDecoration!!.offsetY
			super.groundContouredOffsetY = (super.groundContouredOffsetY.toFloat() - offsetDelta.toFloat() / 10.0f).toInt()
		}
		matrix.translate(super.x, -super.groundContouredOffsetY + super.y - 20, super.z)
		@Pc(100) val basType: BasType? = this.getBasType()
		@Pc(113) val npcType: NPCType? = if (this.npcType!!.alternativeIds == null) this.npcType else this.npcType!!.getMultiNpc(PlayerVarDomain.playerVarProvider)
		super.transparent = false
		@Pc(118) var pickableEntity: PickableEntity? = null
		// Not part of the original client: no flat shadow model under the NPC when the toolkit casts real
		// shadows (634HD's sun shadows, Toolkit.castsShadows), or it would have two.
		if (GamePreferences.uid!!.characterShadows!!.getValue() == 1 && npcType!!.shadowed && basType!!.rendersShadow && !toolkit!!.castsShadows()) {
			@Pc(145) val seqType: SeqType? = if (super.currentSequence.sequenceId != -1 && super.currentSequence.speed == 0) TypeLists.SEQ!!.list(super.currentSequence.sequenceId) else null
			@Pc(162) val seqType2: SeqType? = if (super.movingSequence.sequenceId == -1 || super.movingSequence.hasType && seqType != null) null else TypeLists.SEQ!!.list(super.movingSequence.sequenceId)
			@Pc(212) val model: Model? = PathingEntity.getAnimatedShadowModel(super.modelRotateX, yawRotation, super.models[0], super.modelRotateZ, if (seqType2 == null) super.currentSequence.currentFrameId else super.movingSequence.currentFrameId, this.npcType!!.shadowColorSrc.toInt() and 0xFFFF, this.npcType!!.size, super.modelTranslateY, toolkit, this.npcType!!.shadowAlphaSrc.toInt() and 0xFF, this.npcType!!.shadowColorDst.toInt() and 0xFFFF, this.npcType!!.shadowAlphaDst.toInt() and 0xFF, if (seqType2 == null) seqType else seqType2)
			if (model != null) {
				pickableEntity = PickableEntity.obtain(super.models.size + 1, this.isVisible())
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
		matrix.rotate(yawRotation)
		matrix.translate(super.x, -super.groundContouredOffsetY + super.y - 5, super.z)
		if (pickableEntity == null) {
			pickableEntity = PickableEntity.obtain(super.models.size, this.isVisible())
		}
		this.updateParticles(toolkit, matrix, false, super.models)
		@Pc(299) var modelIndex: Int
		if (OrthoMode.orthographicProjection) {
			modelIndex = 0
			while (super.models.size > modelIndex) {
				if (super.models[modelIndex] != null) {
					super.models[modelIndex]!!.renderOrtho(matrix, pickableEntity!!.boundingBoxPolygons!![modelIndex], HintArrow.scaledOrthoZoom, 0)
				}
				modelIndex++
			}
		} else {
			modelIndex = 0
			while (super.models.size > modelIndex) {
				if (super.models[modelIndex] != null) {
					super.models[modelIndex]!!.render(matrix, pickableEntity!!.boundingBoxPolygons!![modelIndex], 0)
				}
				modelIndex++
			}
		}
		if (super.particleSystem != null) {
			@Pc(359) val particleList: ParticleList = super.particleSystem!!.getList()
			if (OrthoMode.orthographicProjection) {
				toolkit.renderOrtho(particleList, HintArrow.scaledOrthoZoom)
			} else {
				toolkit.render(particleList)
			}
		}
		modelIndex = 0
		while (modelIndex < super.models.size) {
			if (super.models[modelIndex] != null) {
				super.transparent = super.transparent or super.models[modelIndex]!!.F()
			}
			modelIndex++
		}
		super.drawCycle = SceneDrawer.timesSceneRendered
		super.models[2] = null
		super.models[1] = null
		super.models[0] = null
		return pickableEntity
	}

	@OriginalMember(owner = "client!ff", name = "f", descriptor = "(B)I")
	override fun getBasId(): Int {
		if (this.npcType!!.alternativeIds != null) {
			@Pc(13) val npcType: NPCType? = this.npcType!!.getMultiNpc(PlayerVarDomain.playerVarProvider)
			if (npcType != null && npcType.npcBasId != -1) {
				return npcType.npcBasId
			}
		}
		return this.npcType!!.npcBasId
	}

	@OriginalMember(owner = "client!ff", name = "l", descriptor = "(I)I")
	override fun getPickSizeShift(): Int {
		return if (this.npcType == null) 0 else this.npcType!!.sizeShift
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(IZIIII)V")
	fun clearPath(@OriginalArg(0) baseY: Int, @OriginalArg(1) teleport: Boolean, @OriginalArg(2) z: Int, @OriginalArg(4) size: Int, @OriginalArg(5) plane: Int) {
		super.virtualLevel = plane.toByte()
		super.level = super.virtualLevel
		if (SettingsBits.isBridge(baseY, z)) {
			super.virtualLevel++
		}
		if (super.currentSequence.sequenceId != -1 && TypeLists.SEQ!!.list(super.currentSequence.sequenceId).moveType == 1) {
			super.currentAnimations = null
			super.currentSequence.sequenceId = -1
		}
		@Pc(55) var spotAnimationType: SpotAnimationType
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
		if (!teleport) {
			@Pc(114) val offsetX: Int = z - super.movementBaseX[0]
			@Pc(122) val offsetY: Int = baseY - super.movementBaseY[0]
			if (offsetX >= -8 && offsetX <= 8 && offsetY >= -8 && offsetY <= 8) {
				if (super.pathPointer < 9) {
					super.pathPointer++
				}
				@Pc(151) var pathPointer: Int = super.pathPointer
				while (pathPointer > 0) {
					super.movementBaseX[pathPointer] = super.movementBaseX[pathPointer - 1]
					super.movementBaseY[pathPointer] = super.movementBaseY[pathPointer - 1]
					super.movementQueueSpeed[pathPointer] = super.movementQueueSpeed[pathPointer - 1]
					pathPointer--
				}
				super.movementBaseX[0] = z
				super.movementBaseY[0] = baseY
				super.movementQueueSpeed[0] = 1
				return
			}
		}
		super.previousMovementStepsLeft = 0
		super.pathPointer = 0
		super.movementBaseX[0] = z
		super.stalledStepCount = 0
		super.movementBaseY[0] = baseY
		super.x = (super.movementBaseX[0] shl 9) + (size shl 8)
		super.z = (super.movementBaseY[0] shl 9) + (size shl 8)
		if (super.particleSystem != null) {
			super.particleSystem!!.restart()
		}
	}

	@OriginalMember(owner = "client!ff", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	override fun getCylinder(@OriginalArg(0) toolkit: Toolkit?): Silhouette? {
		return null
	}

	@OriginalMember(owner = "client!ff", name = "i", descriptor = "(B)Z")
	fun hasType(): Boolean {
		return this.npcType != null
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(I)I")
	override fun getOverheadSprite(): Int {
		if (this.npcType!!.alternativeIds != null) {
			@Pc(13) val npcType: NPCType? = this.npcType!!.getMultiNpc(PlayerVarDomain.playerVarProvider)
			if (npcType != null && npcType.overheadSprite != -1) {
				return npcType.overheadSprite
			}
		}
		return this.npcType!!.overheadSprite
	}

	@OriginalMember(owner = "client!ff", name = "j", descriptor = "(I)Z")
	override fun hasTileShadow(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!ff", name = "b", descriptor = "(I)I")
	override fun getOverheadHeight(): Int {
		if (this.npcType!!.alternativeIds != null) {
			@Pc(18) val npcType: NPCType? = this.npcType!!.getMultiNpc(PlayerVarDomain.playerVarProvider)
			if (npcType != null && npcType.iconHeight != -1) {
				return npcType.iconHeight
			}
		}
		return if (this.npcType!!.iconHeight == -1) super.getOverheadHeight() else this.npcType!!.iconHeight
	}

	@OriginalMember(owner = "client!ff", name = "c", descriptor = "(Lclient!ha;I)V")
	override fun particleCycle(@OriginalArg(0) toolkit: Toolkit?) {
		if (this.npcType == null || !super.particlesInitialized && !this.getAnimatedBodyModel(0, toolkit)) {
			return
		}
		@Pc(30) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.rotate(super.yaw.getRotation())
		matrix.translate(super.x, super.y - 20, super.z)
		this.updateParticles(toolkit, matrix, super.particlesInitialized, super.models)
		super.models[2] = null
		super.models[1] = null
		super.models[0] = null
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(IILclient!ha;)Z")
	private fun getAnimatedBodyModel(@OriginalArg(1) functionMask: Int, @OriginalArg(2) toolkit: Toolkit?): Boolean {
		var functionMask: Int = functionMask
		@Pc(13) val initialFunctionMask: Int = functionMask
		@Pc(17) val basType: BasType? = this.getBasType()
		@Pc(32) val seqType: SeqType? = if (super.currentSequence.sequenceId != -1 && super.currentSequence.speed == 0) TypeLists.SEQ!!.list(super.currentSequence.sequenceId) else null
		@Pc(49) val seqType2: SeqType? = if (super.movingSequence.sequenceId == -1 || super.movingSequence.hasType && seqType != null) null else TypeLists.SEQ!!.list(super.movingSequence.sequenceId)
		@Pc(52) val modelWidth: Int = basType!!.modelWidth
		@Pc(55) val modelLength: Int = basType.modelLength
		if (modelWidth != 0 || modelLength != 0 || basType.rollTargetAngle != 0 || basType.pitchTargetAngle != 0) {
			functionMask = functionMask or 0x7
		}
		@Pc(90) val hasTint: Boolean = super.opacity.toInt() != 0 && ClientState.clientCycle >= super.tintStartCycle && ClientState.clientCycle < super.tintEndCycle
		if (hasTint) {
			functionMask = functionMask or 0x80000
		}
		@Pc(101) val yawRotation: Int = super.yaw.getRotation()
		@Pc(135) val model: Model? = this.npcType!!.getBodyModel(yawRotation, super.sequences, TypeLists.SEQ, seqType2, super.movingSequence.currentFrameDuration, TypeLists.BAS, super.currentSequence.currentFrameId, seqType, PlayerVarDomain.playerVarProvider, toolkit, super.movingSequence.nextFrameId, super.wornRotation, super.currentSequence.nextFrameId, super.movingSequence.currentFrameId, functionMask, super.currentSequence.currentFrameDuration)
		super.models[0] = model
		if (model == null) {
			return false
		}
		super.sphereRadius = model.fa()
		super.minY = model.ma()
		this.rotate(model)
		if (modelWidth == 0 && modelLength == 0) {
			this.hillChange(yawRotation, this.getSize() shl 9, 0, this.getSize() shl 9, 0)
		} else {
			this.hillChange(yawRotation, modelWidth, basType.hillRotateZ, modelLength, basType.hillRotateX)
			if (super.modelRotateX != 0) {
				super.models[0]!!.FA(super.modelRotateX)
			}
			if (super.modelRotateZ != 0) {
				super.models[0]!!.VA(super.modelRotateZ)
			}
			if (super.modelTranslateY != 0) {
				super.models[0]!!.H(0, super.modelTranslateY, 0)
			}
		}
		if (hasTint) {
			model.adjustColours(super.hue.toInt(), super.saturation.toInt(), super.lightness.toInt(), super.opacity.toInt() and 0xFF)
		}
		@Pc(253) var spotAnimationType: SpotAnimationType
		@Pc(267) var fullDisplacement: Boolean
		@Pc(269) var spotFunctionMask: Int
		@Pc(322) var model2: Model?
		@Pc(343) var translateX: Int
		@Pc(345) var translateY: Int
		@Pc(347) var translateZ: Int
		@Pc(434) var equipmentRotation: Int
		@Pc(462) var angleY: Int
		@Pc(474) var sin: Int
		@Pc(478) var cos: Int
		@Pc(489) var rotatedX: Int
		if (super.primarySpotAnimation.spotAnimationId == -1 || super.primarySpotAnimation.currentFrameId == -1) {
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
				if (super.primarySpotAnimation.adjustmentType >= 0) {
					translateX = 0
					translateY = 0
					translateZ = 0
					if (basType.equipmentTransforms != null && basType.equipmentTransforms!![super.primarySpotAnimation.adjustmentType] != null) {
						translateZ = basType.equipmentTransforms!![super.primarySpotAnimation.adjustmentType]!![2]
						translateX = basType.equipmentTransforms!![super.primarySpotAnimation.adjustmentType]!![0]
						translateY = basType.equipmentTransforms!![super.primarySpotAnimation.adjustmentType]!![1]
					}
					if (basType.projectionOffset != null && basType.projectionOffset!![super.primarySpotAnimation.adjustmentType] != null) {
						translateY += basType.projectionOffset!![super.primarySpotAnimation.adjustmentType]!![1]
						translateX += basType.projectionOffset!![super.primarySpotAnimation.adjustmentType]!![0]
						translateZ += basType.projectionOffset!![super.primarySpotAnimation.adjustmentType]!![2]
					}
					if (translateZ != 0 || translateX != 0) {
						equipmentRotation = yawRotation
						if (super.wornRotation != null && super.wornRotation!![super.primarySpotAnimation.adjustmentType] != -1) {
							equipmentRotation = super.wornRotation!![super.primarySpotAnimation.adjustmentType]
						}
						angleY = (equipmentRotation + super.primarySpotAnimation.rotation * 2048 - yawRotation) and 0x3FFF
						if (angleY != 0) {
							model2.a(angleY)
						}
						sin = TextureCombineSource.SINE[angleY]
						cos = TextureCombineSource.COSINE[angleY]
						rotatedX = (translateZ * sin + translateX * cos) shr 14
						translateZ = (translateZ * cos - translateX * sin) shr 14
						translateX = rotatedX
					}
					model2.H(translateX, translateY, translateZ)
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
		if (super.secondarySpotAnimation.spotAnimationId == -1 || super.secondarySpotAnimation.currentFrameId == -1) {
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
					translateX = 0
					translateY = 0
					translateZ = 0
					if (basType.equipmentTransforms != null && basType.equipmentTransforms!![super.secondarySpotAnimation.adjustmentType] != null) {
						translateY = basType.equipmentTransforms!![super.secondarySpotAnimation.adjustmentType]!![1]
						translateX = basType.equipmentTransforms!![super.secondarySpotAnimation.adjustmentType]!![0]
						translateZ = basType.equipmentTransforms!![super.secondarySpotAnimation.adjustmentType]!![2]
					}
					if (basType.projectionOffset != null && basType.projectionOffset!![super.secondarySpotAnimation.adjustmentType] != null) {
						translateY += basType.projectionOffset!![super.secondarySpotAnimation.adjustmentType]!![1]
						translateX += basType.projectionOffset!![super.secondarySpotAnimation.adjustmentType]!![0]
						translateZ += basType.projectionOffset!![super.secondarySpotAnimation.adjustmentType]!![2]
					}
					if (translateZ != 0 || translateX != 0) {
						equipmentRotation = yawRotation
						if (super.wornRotation != null && super.wornRotation!![super.secondarySpotAnimation.adjustmentType] != -1) {
							equipmentRotation = super.wornRotation!![super.secondarySpotAnimation.adjustmentType]
						}
						angleY = (equipmentRotation + super.secondarySpotAnimation.rotation * 2048 - yawRotation) and 0x3FFF
						if (angleY != 0) {
							model2.a(angleY)
						}
						sin = TextureCombineSource.SINE[angleY]
						cos = TextureCombineSource.COSINE[angleY]
						rotatedX = (translateZ * sin + cos * translateX) shr 14
						translateZ = (cos * translateZ - sin * translateX) shr 14
						translateX = rotatedX
					}
					model2.H(translateX, translateY, translateZ)
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

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(Lclient!bb;I)V")
	fun setNpcType(@OriginalArg(0) npcType: NPCType?) {
		this.npcType = npcType
		if (super.particleSystem != null) {
			super.particleSystem!!.restart()
		}
	}

	companion object {

		@OriginalMember(owner = "client!jf", name = "a", descriptor = "(IB[ILclient!ff;)V")
		@JvmStatic
		fun animateNpc(@OriginalArg(0) speed: Int, @OriginalArg(2) animations: IntArray?, @OriginalArg(3) npcEntity: NPCEntity?) {
			PluginRuntime.animationChanged(npcEntity, animations) // Not part of the original client: the plugins' AnimationChanged.
			@Pc(10) val local10: Boolean
			@Pc(12) var i: Int
			if (npcEntity!!.currentAnimations != null) {
				var animationsMatch: Boolean = true
				i = 0
				while (i < npcEntity.currentAnimations!!.size) {
					if (npcEntity.currentAnimations!![i] != animations!![i]) {
						animationsMatch = false
						break
					}
					i++
				}
				if (animationsMatch && npcEntity.currentSequence.sequenceId != -1) {
					@Pc(48) val seqType: SeqType = TypeLists.SEQ!!.list(npcEntity.currentSequence.sequenceId)
					@Pc(51) val loopType: Int = seqType.loopType
					if (loopType == 1) {
						npcEntity.currentSequence.currentFrameDuration = 0
						npcEntity.currentSequence.speed = speed
						npcEntity.currentSequence.currentLoopCount = 0
						npcEntity.currentSequence.currentFrameId = 0
						npcEntity.currentSequence.nextFrameId = 1
						if (!npcEntity.isHidden) {
							SoundManager.playSequenceSound(npcEntity, npcEntity.currentSequence.currentFrameId, seqType)
						}
					}
					if (loopType == 2) {
						npcEntity.currentSequence.currentLoopCount = 0
					}
				}
			}
			var allCleared: Boolean = true
			i = 0
			while (i < animations!!.size) {
				if (animations[i] != -1) {
					allCleared = false
				}
				if (npcEntity.currentAnimations == null || npcEntity.currentAnimations!![i] == -1 || TypeLists.SEQ!!.list(animations[i]).priority >= TypeLists.SEQ!!.list(npcEntity.currentAnimations!![i]).priority) {
					npcEntity.currentSequence.speed = speed
					npcEntity.previousMovementStepsLeft = npcEntity.pathPointer
					npcEntity.currentAnimations = animations
				}
				i++
			}
			if (allCleared) {
				npcEntity.currentAnimations = animations
				npcEntity.currentSequence.speed = speed
				npcEntity.previousMovementStepsLeft = npcEntity.pathPointer
			}
		}
		@OriginalMember(owner = "client!kl", name = "a", descriptor = "(ILclient!ff;[I[I[I)V")
		@JvmStatic
		fun animateNpcSlots(@OriginalArg(1) npcEntity: NPCEntity?, @OriginalArg(2) slotMasks: IntArray?, @OriginalArg(3) sequenceIds: IntArray?, @OriginalArg(4) speeds: IntArray?) {
			@Pc(8) var i: Int = 0
			while (i < sequenceIds!!.size) {
				@Pc(14) val sequenceId: Int = sequenceIds[i]
				@Pc(18) var slotMask: Int = slotMasks!![i]
				@Pc(22) val speed: Int = speeds!![i]
				@Pc(24) var j: Int = 0
				while (slotMask != 0 && j < npcEntity!!.sequences.size) {
					if ((slotMask and 0x1) != 0) {
						if (sequenceId == -1) {
							npcEntity.sequences[j] = null
						} else {
							@Pc(41) val seqType: SeqType = TypeLists.SEQ!!.list(sequenceId)
							@Pc(44) val loopType: Int = seqType.loopType
							@Pc(49) var pathingEntitySequence: PathingEntitySequence? = npcEntity.sequences[j]
							if (pathingEntitySequence != null) {
								if (pathingEntitySequence.sequenceId == sequenceId) {
									if (loopType == 0) {
										npcEntity.sequences[j] = null
										pathingEntitySequence = null
									} else if (loopType == 1) {
										pathingEntitySequence.speed = speed
										pathingEntitySequence.currentFrameDuration = 0
										pathingEntitySequence.nextFrameId = 1
										pathingEntitySequence.currentFrameId = 0
										pathingEntitySequence.currentLoopCount = 0
										if (!npcEntity.isHidden) {
											SoundManager.playSequenceSound(npcEntity, 0, seqType)
										}
									} else if (loopType == 2) {
										pathingEntitySequence.currentLoopCount = 0
									}
								} else if (seqType.priority >= TypeLists.SEQ!!.list(pathingEntitySequence.sequenceId).priority) {
									npcEntity.sequences[j] = null
									pathingEntitySequence = null
								}
							}
							if (pathingEntitySequence == null) {
								pathingEntitySequence = PathingEntitySequence()
								npcEntity.sequences[j] = pathingEntitySequence
								pathingEntitySequence.nextFrameId = 1
								pathingEntitySequence.sequenceId = sequenceId
								pathingEntitySequence.currentFrameDuration = 0
								pathingEntitySequence.currentLoopCount = 0
								pathingEntitySequence.currentFrameId = 0
								pathingEntitySequence.speed = speed
								if (!npcEntity.isHidden) {
									SoundManager.playSequenceSound(npcEntity, 0, seqType)
								}
							}
						}
					}
					slotMask = slotMask ushr 0x1
					j++
				}
				i++
			}
		}
	}
}
