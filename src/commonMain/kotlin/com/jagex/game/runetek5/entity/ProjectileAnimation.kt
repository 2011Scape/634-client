package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.config.bas.BasType
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType
import com.jagex.game.runetek5.graphics.model.particle.Silhouette
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.cache.node.lru.ProjectileAnimationNode
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.OrthoMode
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!to")
class ProjectileAnimation : AbstractInteractiveEntity {

	@OriginalMember(owner = "client!to", name = "X", descriptor = "D")
	private var translateZ: Double = 0.0

	@OriginalMember(owner = "client!to", name = "eb", descriptor = "Lclient!uj;")
	private var particleSystem: ParticleSystem? = null

	@OriginalMember(owner = "client!to", name = "jb", descriptor = "D")
	private var verticalAcceleration: Double = 0.0

	@OriginalMember(owner = "client!to", name = "kb", descriptor = "D")
	private var translateY: Double = 0.0

	@OriginalMember(owner = "client!to", name = "mb", descriptor = "I")
	private var rotateY: Int = 0

	@OriginalMember(owner = "client!to", name = "wb", descriptor = "I")
	private var rotateX: Int = 0

	@OriginalMember(owner = "client!to", name = "Ab", descriptor = "D")
	private var horizontalSpeed: Double = 0.0

	@OriginalMember(owner = "client!to", name = "Bb", descriptor = "D")
	private var speedX: Double = 0.0

	@OriginalMember(owner = "client!to", name = "Ib", descriptor = "D")
	private var translateX: Double = 0.0

	@OriginalMember(owner = "client!to", name = "Jb", descriptor = "D")
	private var verticalSpeed: Double = 0.0

	@OriginalMember(owner = "client!to", name = "Ob", descriptor = "D")
	private var speedZ: Double = 0.0

	@OriginalMember(owner = "client!to", name = "V", descriptor = "I")
	private var nextFrame: Int = -1

	@OriginalMember(owner = "client!to", name = "I", descriptor = "I")
	private var frame: Int = 0

	@OriginalMember(owner = "client!to", name = "Db", descriptor = "I")
	private var frameCycle: Int = 0

	@OriginalMember(owner = "client!to", name = "Hb", descriptor = "Z")
	private var ticked: Boolean = false

	@OriginalMember(owner = "client!to", name = "zb", descriptor = "Z")
	private var transparent: Boolean = false

	@OriginalMember(owner = "client!to", name = "Kb", descriptor = "I")
	private var sphereRadius: Int = 0

	@OriginalMember(owner = "client!to", name = "U", descriptor = "I")
	private var minY: Int = 0

	@OriginalMember(owner = "client!to", name = "tb", descriptor = "I")
	private val displacement: Int

	@OriginalMember(owner = "client!to", name = "T", descriptor = "I")
	private val wornSlot: Int

	@OriginalMember(owner = "client!to", name = "Mb", descriptor = "I")
	@JvmField
	val t1: Int

	@OriginalMember(owner = "client!to", name = "yb", descriptor = "Z")
	private val groundRelative: Boolean

	@OriginalMember(owner = "client!to", name = "S", descriptor = "I")
	@JvmField
	val entity2: Int

	@OriginalMember(owner = "client!to", name = "rb", descriptor = "I")
	private val id: Int

	@OriginalMember(owner = "client!to", name = "sb", descriptor = "I")
	@JvmField
	val t2: Int

	@OriginalMember(owner = "client!to", name = "Nb", descriptor = "I")
	private val y1: Int

	@OriginalMember(owner = "client!to", name = "hb", descriptor = "I")
	private val verticalAngle: Int

	@OriginalMember(owner = "client!to", name = "Lb", descriptor = "I")
	@JvmField
	val y2: Int

	@OriginalMember(owner = "client!to", name = "nb", descriptor = "I")
	private val entity1: Int

	@OriginalMember(owner = "client!to", name = "bb", descriptor = "Lclient!hg;")
	private val seqType: SeqType?

	@OriginalMember(owner = "client!to", name = "<init>", descriptor = "(IIIIIIIIIIIIIZI)V")
	constructor(@OriginalArg(0) id: Int, @OriginalArg(1) level: Int, @OriginalArg(2) virtualLevel: Int, @OriginalArg(3) x: Int, @OriginalArg(4) z: Int, @OriginalArg(5) startHeight: Int, @OriginalArg(6) startCycle: Int, @OriginalArg(7) endCycle: Int, @OriginalArg(8) verticalAngle: Int, @OriginalArg(9) displacement: Int, @OriginalArg(10) entity1: Int, @OriginalArg(11) targetIndex: Int, @OriginalArg(12) entity2: Int, @OriginalArg(13) groundRelative: Boolean, @OriginalArg(14) wornSlot: Int) : super(level, virtualLevel, x, Scene.tileHeightForAbsolutePosition(x, level, z) - startHeight, z, x shr 9, x shr 9, z shr 9, z shr 9, false, 0.toByte()) {
		this.displacement = displacement
		this.wornSlot = wornSlot
		this.t1 = targetIndex
		this.groundRelative = groundRelative
		this.ticked = false
		this.entity2 = entity2
		this.id = id
		this.t2 = endCycle
		this.y1 = startHeight
		this.verticalAngle = verticalAngle
		this.y2 = startCycle
		this.entity1 = entity1
		@Pc(91) val seqId: Int = TypeLists.SPOT_ANIM!!.list(this.id)!!.sequenceId
		if (seqId == -1) {
			this.seqType = null
		} else {
			this.seqType = TypeLists.SEQ!!.list(seqId)
		}
	}

	@OriginalMember(owner = "client!to", name = "c", descriptor = "(Lclient!ha;I)V")
	override fun particleCycle(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(9) val model: Model? = this.generateGfxModel(0, toolkit)
		if (model == null) {
			return
		}
		@Pc(21) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.makeRotationX(this.rotateX)
		matrix.rotateAxisY(this.rotateY)
		matrix.translate(this.translateX.toInt(), this.translateY.toInt(), this.translateZ.toInt())
		this.sphereRadius = model.fa()
		this.minY = model.ma()
		this.updateParticles(matrix, model, toolkit)
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(BIIII)V")
	fun target(@OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) currentCycle: Int, @OriginalArg(4) x: Int) {
		@Pc(9) val local9: Double
		if (!this.ticked) {
			val offsetX: Double = (x - this.x).toDouble()
			@Pc(16) val offsetZ: Double = (z - this.z).toDouble()
			@Pc(25) val distance: Double = kotlin.math.sqrt(offsetX * offsetX + offsetZ * offsetZ)
			this.translateX = this.x.toDouble() + this.displacement.toDouble() * offsetX / distance
			this.translateZ = this.displacement.toDouble() * offsetZ / distance + this.z.toDouble()
			if (this.groundRelative) {
				this.translateY = (Scene.tileHeightForAbsolutePosition(this.translateX.toInt(), this.level.toInt(), this.translateZ.toInt()) - this.y1).toDouble()
			} else {
				this.translateY = this.y.toDouble()
			}
		}
		val remainingCycles: Double = (this.t2 + 1 - currentCycle).toDouble()
		this.speedZ = (z.toDouble() - this.translateZ) / remainingCycles
		this.speedX = (x.toDouble() - this.translateX) / remainingCycles
		this.horizontalSpeed = kotlin.math.sqrt(this.speedX * this.speedX + this.speedZ * this.speedZ)
		if (this.verticalAngle == -1) {
			this.verticalSpeed = (y.toDouble() - this.translateY) / remainingCycles
		} else {
			if (!this.ticked) {
				this.verticalSpeed = -this.horizontalSpeed * kotlin.math.tan(this.verticalAngle.toDouble() * 0.02454369)
			}
			this.verticalAcceleration = (y.toDouble() - this.translateY - remainingCycles * this.verticalSpeed) * 2.0 / (remainingCycles * remainingCycles)
		}
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	override fun shareLight(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) offsetY: Int, @OriginalArg(2) hideSharedFaces: Boolean, @OriginalArg(3) entity: Entity?, @OriginalArg(4) offsetZ: Int, @OriginalArg(6) offsetX: Int) {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!to", name = "finalize", descriptor = "()V")
	fun finalize() {
		if (this.particleSystem != null) {
			this.particleSystem!!.stopped()
		}
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(I)V")
	fun stopParticles() {
		if (this.particleSystem != null) {
			this.particleSystem!!.stopped()
		}
	}

	@OriginalMember(owner = "client!to", name = "j", descriptor = "(I)Z")
	override fun hasTileShadow(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(Lclient!bca;Lclient!ka;Lclient!ha;I)V")
	private fun updateParticles(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) model: Model?, @OriginalArg(2) toolkit: Toolkit?) {
		model!!.updateParticlePosition(matrix)
		@Pc(9) val modelParticleEmitters: Array<EmissiveTriangle?>? = model.getEmissiveTriangles()
		@Pc(12) val modelParticleEffectors: Array<EffectiveVertex?>? = model.getEffectiveVertices()
		if ((this.particleSystem == null || this.particleSystem!!.removed) && (modelParticleEmitters != null || modelParticleEffectors != null)) {
			this.particleSystem = ParticleSystem.get(ClientState.clientCycle, true)
		}
		if (this.particleSystem != null) {
			this.particleSystem!!.update(toolkit, ClientState.clientCycle.toLong(), modelParticleEmitters, modelParticleEffectors)
			this.particleSystem!!.updateBounds(this.level.toInt(), this.startX.toInt(), this.endX.toInt(), this.startZ.toInt(), this.endZ.toInt())
		}
	}

	@OriginalMember(owner = "client!to", name = "i", descriptor = "(I)I")
	override fun getMinY(@OriginalArg(0) check: Int): Int {
		return if (check == -25675) this.minY else -92
	}

	@OriginalMember(owner = "client!to", name = "c", descriptor = "(Z)I")
	override fun getModelMinY(): Int {
		return this.sphereRadius
	}

	@OriginalMember(owner = "client!to", name = "f", descriptor = "(B)V")
	fun cycle() {
		if (this.ticked || this.entity1 == 0) {
			return
		}
		@Pc(19) var pathingEntity: PathingEntity? = null
		@Pc(31) val entityIndex: Int
		if (this.entity1 < 0) {
			entityIndex = -this.entity1 - 1
			if (PlayerUpdate.myPlayerIndex == entityIndex) {
				pathingEntity = PlayerUpdate.localPlayer
			} else {
				pathingEntity = PlayerUpdate.players[entityIndex]
			}
		} else {
			entityIndex = this.entity1 - 1
			@Pc(58) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get(entityIndex.toLong()) as NPCEntityNode?
			if (npcEntityNode != null) {
				pathingEntity = npcEntityNode.npc
			}
		}
		if (pathingEntity == null) {
			return
		}
		this.z = pathingEntity.z
		this.x = pathingEntity.x
		this.y = Scene.tileHeightForAbsolutePosition(pathingEntity.x, this.level.toInt(), pathingEntity.z) - this.y1
		if (this.wornSlot < 0) {
			return
		}
		@Pc(94) val basType: BasType? = pathingEntity.getBasType()
		@Pc(96) var projectionX: Int = 0
		@Pc(98) var projectionZ: Int = 0
		if (basType!!.equipmentTransforms != null && basType.equipmentTransforms!![this.wornSlot] != null) {
			projectionX = basType.equipmentTransforms!![this.wornSlot]!![0]
			projectionZ = basType.equipmentTransforms!![this.wornSlot]!![2]
		}
		if (basType.projectionOffset != null && basType.projectionOffset!![this.wornSlot] != null) {
			projectionZ += basType.projectionOffset!![this.wornSlot]!![2]
			projectionX += basType.projectionOffset!![this.wornSlot]!![0]
		}
		if (projectionX != 0 || projectionZ != 0) {
			@Pc(171) val entityYaw: Int = pathingEntity.yaw.getRotation()
			@Pc(173) var baseRotation: Int = entityYaw
			if (pathingEntity.wornRotation != null && pathingEntity.wornRotation!![this.wornSlot] != -1) {
				baseRotation = pathingEntity.wornRotation!![this.wornSlot]
			}
			@Pc(197) val relativeRotation: Int = (baseRotation - entityYaw) and 0x3FFF
			@Pc(201) val sin: Int = TextureCombineSource.SINE[relativeRotation]
			@Pc(205) val cos: Int = TextureCombineSource.COSINE[relativeRotation]
			@Pc(215) val offsetX: Int = (projectionZ * sin + projectionX * cos) shr 14
			projectionZ = (projectionZ * cos - projectionX * sin) shr 14
			this.x += offsetX
			this.z += projectionZ
		}
		return
	}

	@OriginalMember(owner = "client!to", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	override fun getCylinder(@OriginalArg(0) toolkit: Toolkit?): Silhouette? {
		return null
	}

	@OriginalMember(owner = "client!to", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	override fun render(@OriginalArg(1) toolkit: Toolkit?): PickableEntity? {
		@Pc(11) val model: Model? = this.generateGfxModel(2048, toolkit)
		if (model == null) {
			return null
		}
		@Pc(18) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.makeRotationX(this.rotateX)
		matrix.rotateAxisY(this.rotateY)
		matrix.translate(this.translateX.toInt(), this.translateY.toInt(), this.translateZ.toInt())
		this.updateParticles(matrix, model, toolkit)
		@Pc(48) val pickableEntity: PickableEntity = PickableEntity.obtain(1, false)
		if (OrthoMode.orthographicProjection) {
			model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons!![0], HintArrow.scaledOrthoZoom, 0)
		} else {
			model.render(matrix, pickableEntity.boundingBoxPolygons!![0], 0)
		}
		if (this.particleSystem != null) {
			@Pc(76) val particles: ParticleList? = this.particleSystem!!.getList()
			if (OrthoMode.orthographicProjection) {
				toolkit.renderOrtho(particles, HintArrow.scaledOrthoZoom)
			} else {
				toolkit.render(particles)
			}
		}
		this.transparent = model.F()
		this.sphereRadius = model.fa()
		this.minY = model.ma()
		return pickableEntity
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(Lclient!ha;III)Z")
	override fun picked(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) mouseY: Int, @OriginalArg(2) mouseX: Int): Boolean {
		return false
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(BI)V")
	fun tick(@OriginalArg(1) time: Int) {
		this.ticked = true
		this.translateX += time.toDouble() * this.speedX
		this.translateZ += time.toDouble() * this.speedZ
		if (this.groundRelative) {
			this.translateY = (Scene.tileHeightForAbsolutePosition(this.translateX.toInt(), this.level.toInt(), this.translateZ.toInt()) - this.y1).toDouble()
		} else if (this.verticalAngle == -1) {
			this.translateY += this.verticalSpeed * time.toDouble()
		} else {
			this.translateY += time.toDouble() * this.verticalAcceleration * 0.5 * time.toDouble() + time.toDouble() * this.verticalSpeed
			this.verticalSpeed += this.verticalAcceleration * time.toDouble()
		}
		this.rotateY = ((kotlin.math.atan2(this.speedX, this.speedZ) * 2607.5945876176133).toInt() + 8192) and 0x3FFF
		this.rotateX = (kotlin.math.atan2(this.verticalSpeed, this.horizontalSpeed) * 2607.5945876176133).toInt() and 0x3FFF
		if (this.seqType == null) {
			return
		}
		this.frameCycle += time
		while (true) {
			do {
				do {
					if (this.frameCycle <= this.seqType.durations!![this.frame]) {
						return
					}
					this.frameCycle -= this.seqType.durations!![this.frame]
					this.frame++
					if (this.seqType.frames!!.size <= this.frame) {
						this.frame -= this.seqType.replayInterval
						if (this.frame < 0 || this.frame >= this.seqType.frames!!.size) {
							this.frame = 0
						}
					}
					this.nextFrame = this.frame + 1
				} while (this.seqType.frames!!.size > this.nextFrame)
				this.nextFrame -= this.seqType.replayInterval
			} while (this.nextFrame >= 0 && this.seqType.frames!!.size > this.nextFrame)
			this.nextFrame = -1
		}
	}

	@OriginalMember(owner = "client!to", name = "b", descriptor = "(Z)V")
	override fun removeTileShadow() {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!to", name = "g", descriptor = "(I)Z")
	override fun isStationary(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!to", name = "e", descriptor = "(B)V")
	override fun updateBounds() {
		this.endX = (this.translateX / 512.0).toInt().toShort()
		this.startX = this.endX
		this.endZ = (this.translateZ / 512.0).toInt().toShort()
		this.startZ = this.endZ
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(ILclient!ha;B)Lclient!ka;")
	private fun generateGfxModel(@OriginalArg(0) functionMask: Int, @OriginalArg(1) toolkit: Toolkit?): Model? {
		@Pc(9) val spotAnimationType: SpotAnimationType? = TypeLists.SPOT_ANIM!!.list(this.id)
		return spotAnimationType!!.getModel(this.frame, toolkit, this.frameCycle, functionMask, this.nextFrame, TypeLists.SEQ)
	}

	@OriginalMember(owner = "client!to", name = "b", descriptor = "(B)Z")
	override fun hasAlpha(): Boolean {
		return this.transparent
	}

	companion object {
		@OriginalMember(owner = "client!vga", name = "a", descriptor = "(I)V")
		@JvmStatic
		fun updateProjectiles() {
			@Pc(10) var projectileAnimationNode: ProjectileAnimationNode? = ProjectileAnimation.aClass341_24.first() as ProjectileAnimationNode?
			while (projectileAnimationNode != null) {
				@Pc(23) val projectileAnimation: ProjectileAnimation? = projectileAnimationNode.projectileAnimation
				if (projectileAnimation!!.t2 < ClientState.clientCycle) {
					projectileAnimationNode.unlink()
					projectileAnimation.stopParticles()
				} else if (ClientState.clientCycle >= projectileAnimation.y2) {
					projectileAnimation.cycle()
					if (projectileAnimation.t1 > 0) {
						@Pc(63) val npcEntityNode: NPCEntityNode? = NPCList.NPC_MAP.get((projectileAnimation.t1 - 1).toLong()) as NPCEntityNode?
						if (npcEntityNode != null) {
							@Pc(68) val npcEntity: NPCEntity? = npcEntityNode.npc
							if (npcEntity!!.x >= 0 && MapRegion.sceneSizeX * 512 > npcEntity.x && npcEntity.z >= 0 && MapRegion.sceneSizeY * 512 > npcEntity.z) {
								projectileAnimation.target(Scene.tileHeightForAbsolutePosition(npcEntity.x, projectileAnimation.level.toInt(), npcEntity.z) - projectileAnimation.entity2, npcEntity.z, ClientState.clientCycle, npcEntity.x)
							}
						}
					}
					if (projectileAnimation.t1 < 0) {
						@Pc(121) val playerIndex: Int = -projectileAnimation.t1 - 1
						@Pc(126) val playerEntity: PlayerEntity?
						if (playerIndex == PlayerUpdate.myPlayerIndex) {
							playerEntity = PlayerUpdate.localPlayer
						} else {
							playerEntity = PlayerUpdate.players[playerIndex]
						}
						if (playerEntity != null && playerEntity.x >= 0 && MapRegion.sceneSizeX * 512 > playerEntity.x && playerEntity.z >= 0 && playerEntity.z < MapRegion.sceneSizeY * 512) {
							projectileAnimation.target(Scene.tileHeightForAbsolutePosition(playerEntity.x, projectileAnimation.level.toInt(), playerEntity.z) - projectileAnimation.entity2, playerEntity.z, ClientState.clientCycle, playerEntity.x)
						}
					}
					projectileAnimation.tick(ClientState.animatingStep)
					Scene.addInteractive(projectileAnimation, true)
				}
				projectileAnimationNode = ProjectileAnimation.aClass341_24.next() as ProjectileAnimationNode?
			}
		}

		@OriginalMember(owner = "client!pj", name = "R", descriptor = "Lclient!uh;")
		@JvmField
		val aClass341_24: Deque = Deque()
	}
}
