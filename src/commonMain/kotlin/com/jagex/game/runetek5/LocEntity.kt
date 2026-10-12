package com.jagex.game.runetek5

import com.jagex.game.runetek5.core.constants.LocShapes
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.ModelAndShadow
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.entity.ParticleSystem
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField

@OriginalClass("client!bv")
class LocEntity @OriginalMember(owner = "client!bv", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIILclient!ga;ZI)V") constructor(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) locType: LocType?, @OriginalArg(2) shape: Int, @OriginalArg(3) rotation: Int, @OriginalArg(4) level: Int, @OriginalArg(5) virtualLevel: Int, @OriginalArg(6) entity: Entity?, @OriginalArg(7) underwater: Boolean, @OriginalArg(8) sequenceId: Int) {

	@OriginalMember(owner = "client!bv", name = "e", descriptor = "I")
	private var frameCycle: Int = 0

	@OriginalMember(owner = "client!bv", name = "h", descriptor = "Lclient!hg;")
	private var sequence: SeqType? = null

	@OriginalMember(owner = "client!bv", name = "j", descriptor = "Lclient!ka;")
	private var model: Model? = null

	@OriginalMember(owner = "client!bv", name = "v", descriptor = "Lclient!r;")
	private var shadow: Shadow? = null

	@OriginalMember(owner = "client!bv", name = "w", descriptor = "[Z")
	private var castShadowAtPlane: BooleanArray? = null

	@OriginalMember(owner = "client!bv", name = "y", descriptor = "I")
	private var frameStartCycle: Int = 0

	@OriginalMember(owner = "client!bv", name = "F", descriptor = "Lclient!uj;")
	@JvmField
	var particleSystem: ParticleSystem? = null

	@OriginalMember(owner = "client!bv", name = "H", descriptor = "I")
	private var nextFrame: Int = 0

	@OriginalMember(owner = "client!bv", name = "I", descriptor = "I")
	private var currentFrame: Int = 0

	@OriginalMember(owner = "client!bv", name = "k", descriptor = "I")
	private var modelMinY: Int = 0

	@OriginalMember(owner = "client!bv", name = "f", descriptor = "I")
	private var modelRadius: Int = 0

	@OriginalMember(owner = "client!bv", name = "o", descriptor = "Z")
	private var hasSequenceId: Boolean = false

	@OriginalMember(owner = "client!bv", name = "d", descriptor = "I")
	private var lastLocId: Int = -1

	@OriginalMember(owner = "client!bv", name = "t", descriptor = "Z")
	private var underwater: Boolean = false

	@OriginalMember(owner = "client!bv", name = "g", descriptor = "I")
	private var shadowLocId: Int = -1

	@OriginalMember(owner = "client!bv", name = "x", descriptor = "I")
	private var shadowFrame: Int = -1

	@OriginalMember(owner = "client!bv", name = "D", descriptor = "I")
	@JvmField
	val shape: Int

	@OriginalMember(owner = "client!bv", name = "n", descriptor = "I")
	@JvmField
	val id: Int

	@OriginalMember(owner = "client!bv", name = "b", descriptor = "Lclient!ga;")
	private val entity: Entity?

	@OriginalMember(owner = "client!bv", name = "J", descriptor = "B")
	private val level: Byte

	@OriginalMember(owner = "client!bv", name = "r", descriptor = "B")
	private val virtualLevel: Byte

	@OriginalMember(owner = "client!bv", name = "c", descriptor = "I")
	@JvmField
	val rotation: Int

	@OriginalMember(owner = "client!bv", name = "p", descriptor = "Z")
	private val hardShadow: Boolean

	init {
		this.shape = shape
		this.id = locType!!.id
		this.entity = entity
		this.level = level.toByte()
		this.virtualLevel = virtualLevel.toByte()
		this.underwater = underwater
		this.rotation = rotation
		this.hardShadow = toolkit!!.hardShadow() && locType.dynamicShadow && !this.underwater
		if (sequenceId != -1) {
			this.hasSequenceId = true
		}
		this.updateSequence(sequenceId)
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(I)I")
	fun getModelMinY(): Int {
		return this.modelMinY
	}

	@OriginalMember(owner = "client!bv", name = "b", descriptor = "(I)I")
	fun getModelRadius(@OriginalArg(0) arg0: Int): Int {
		if (arg0 > -20) {
			this.finalize()
		}
		return this.modelRadius
	}

	@OriginalMember(owner = "client!bv", name = "c", descriptor = "(I)Z")
	fun hardShadow(): Boolean {
		return this.hardShadow
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(BI)V")
	private fun updateSequence(@OriginalArg(1) sequenceId: Int) {
		@Pc(13) var resolvedSequenceId: Int = sequenceId
		@Pc(15) var replaySequence: Boolean = false
		if (sequenceId == -1) {
			@Pc(26) var locType: LocType? = TypeLists.LOC!!.list(this.id)
			@Pc(28) var locType2: LocType? = locType
			if (locType!!.alternativeIds != null) {
				locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider)
			}
			if (locType == null) {
				return
			}
			if (locType2 === locType) {
				locType2 = null
			}
			if (locType.animations != null) {
				if (this.sequence != null && locType.hasAnimation(this.sequence!!.id)) {
					return
				}
				resolvedSequenceId = locType.randomAnimation()
				if (locType.id != this.lastLocId) {
					replaySequence = locType.replaySequence
				}
			} else if (locType.animationId == -1) {
				if (locType2 != null && locType2.animations != null) {
					if (this.sequence != null && locType2.hasAnimation(this.sequence!!.id)) {
						return
					}
					resolvedSequenceId = locType2.randomAnimation()
					if (this.lastLocId != locType2.id) {
						replaySequence = locType2.replaySequence
					}
				} else if (locType2 != null && locType2.animationId != -1 && locType2.id != this.lastLocId) {
					replaySequence = locType2.replaySequence
					resolvedSequenceId = locType2.animationId
				}
			} else if (locType.id != this.lastLocId) {
				resolvedSequenceId = locType.animationId
				replaySequence = locType.replaySequence
			}
		}
		if (resolvedSequenceId == -1) {
			this.sequence = null
			return
		}
		this.model = null
		if (this.sequence == null || resolvedSequenceId != this.sequence!!.id) {
			this.sequence = TypeLists.SEQ!!.list(resolvedSequenceId)
		} else if (this.sequence!!.loopType == 0) {
			return
		}
		if (this.sequence!!.frames == null) {
			this.sequence = null
			return
		}
		if (replaySequence) {
			this.currentFrame = (this.sequence!!.frames!!.size.toDouble() * util.random()).toInt()
			this.frameCycle = (this.sequence!!.durations!![this.currentFrame].toDouble() * util.random()).toInt() + 1
		} else {
			this.currentFrame = 0
			this.frameCycle = 1
		}
		this.nextFrame = this.currentFrame + 1
		if (this.nextFrame < 0 || this.sequence!!.frames!!.size <= this.nextFrame) {
			this.nextFrame = -1
		}
		this.frameStartCycle = ClientState.clientCycle - this.frameCycle
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(BLclient!ha;)V")
	fun refreshShadow(@OriginalArg(1) toolkit: Toolkit?) {
		this.getModel(true, true, 262144, toolkit)
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(ZZIILclient!ha;)Lclient!ka;")
	fun getModel(@OriginalArg(0) shadowOnly: Boolean, @OriginalArg(1) castShadow: Boolean, @OriginalArg(3) functionMask: Int, @OriginalArg(4) toolkit: Toolkit?): Model? {
		var castShadow: Boolean = castShadow
		var functionMask: Int = functionMask
		@Pc(11) var locType: LocType? = TypeLists.LOC!!.list(this.id)
		if (locType!!.alternativeIds != null) {
			locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider)
		}
		if (locType == null) {
			this.removeShadow(toolkit)
			this.shadowFrame = -1
			this.shadowLocId = -1
			this.lastLocId = -1
			return null
		}
		if (!this.hasSequenceId && this.lastLocId != locType.id) {
			this.model = null
			this.updateSequence(-1)
		}
		this.progressAnimation(this.entity)
		if (castShadow) {
			@Pc(76) val shadowEnabled: Boolean = castShadow and this.hardShadow and (GamePreferences.uid!!.hardShadows!!.getValue() != 0)
			castShadow = shadowEnabled and (locType.id != this.shadowLocId || this.sequence != null && GamePreferences.uid!!.hardShadows!!.getValue() >= 2 && (this.shadowFrame != this.currentFrame || (this.sequence!!.shadowTweened || SeqType.forceTweening) && this.currentFrame != this.nextFrame))
		}
		if (shadowOnly && !castShadow) {
			this.lastLocId = locType.id
			return null
		}
		if (castShadow) {
			Scene.removeShadow(this.shadow, this.virtualLevel.toInt(), this.entity!!.x, this.entity.z, this.castShadowAtPlane)
			this.shadowLocId = -1
			this.shadowFrame = -1
		}
		@Pc(161) val ground: Ground? = Scene.aClass65Array1!![this.virtualLevel.toInt()]
		@Pc(177) val ground2: Ground?
		if (this.underwater) {
			ground2 = Scene.aClass65Array2!![0]
		} else {
			ground2 = if (this.virtualLevel >= 3) null else Scene.aClass65Array1!![this.virtualLevel + 1]
		}
		@Pc(185) var model: Model? = null
		if (this.sequence != null) {
			if (castShadow) {
				functionMask = functionMask or 0x40000
			}
			model = locType.getAnimatedModel(this.nextFrame, this.frameCycle, if (this.shape == 11) 10 else this.shape, ground!!.averageHeight(this.entity!!.x, this.entity.z), toolkit, if (this.shape == 11) this.rotation + 4 else this.rotation, functionMask, ground2, this.entity.z, this.currentFrame, this.entity.x, this.sequence, ground)
			if (model == null) {
				this.modelRadius = 0
				this.castShadowAtPlane = null
				this.modelMinY = 0
				this.shadow = null
			} else {
				if (castShadow) {
					if (this.castShadowAtPlane == null) {
						this.castShadowAtPlane = BooleanArray(4)
					}
					this.shadow = model.ba(this.shadow)
					Shadow.renderShadows(this.shadow, this.virtualLevel.toInt(), this.entity.x, this.entity.z, this.castShadowAtPlane)
					this.shadowFrame = this.currentFrame
					this.shadowLocId = locType.id
				}
				this.modelMinY = model.fa()
				this.modelRadius = model.ma()
			}
			this.model = null
		} else if (this.model != null && functionMask == (functionMask and this.model!!.ua()) && locType.id == this.lastLocId) {
			model = this.model
		} else {
			if (this.model != null) {
				functionMask = functionMask or this.model!!.ua()
			}
			@Pc(261) val modelAndShadow: ModelAndShadow? = locType.getModelAndShadow(toolkit, ground2, if (this.shape == LocShapes.CENTREPIECE_DIAGONAL) 10 else this.shape, functionMask, this.entity!!.x, castShadow, ground, ground!!.averageHeight(this.entity.x, this.entity.z), this.entity.z, if (this.shape == 11) this.rotation + 4 else this.rotation)
			if (modelAndShadow == null) {
				this.modelRadius = 0
				this.castShadowAtPlane = null
				this.shadow = null
				this.modelMinY = 0
				this.model = null
			} else {
				model = modelAndShadow.model
				this.model = modelAndShadow.model
				if (castShadow) {
					this.castShadowAtPlane = null
					this.shadow = modelAndShadow.shadow
					Shadow.renderShadows(this.shadow, this.virtualLevel.toInt(), this.entity.x, this.entity.z, null)
					this.shadowFrame = -1
					this.shadowLocId = locType.id
				}
				this.modelMinY = model!!.fa()
				this.modelRadius = model.ma()
			}
		}
		this.lastLocId = locType.id
		return model
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(ILclient!bca;ILclient!ha;ILclient!ka;ZBI)V")
	fun animateParticles(@OriginalArg(0) endX: Int, @OriginalArg(1) matrix: Matrix?, @OriginalArg(2) endZ: Int, @OriginalArg(3) toolkit: Toolkit?, @OriginalArg(4) startX: Int, @OriginalArg(5) model: Model?, @OriginalArg(6) performCycle: Boolean, @OriginalArg(8) startZ: Int) {
		@Pc(12) val modelParticleEmitters: Array<EmissiveTriangle?>? = model!!.getEmissiveTriangles()
		@Pc(15) val modelParticleEffectors: Array<EffectiveVertex?>? = model.getEffectiveVertices()
		if ((this.particleSystem == null || this.particleSystem!!.removed) && (modelParticleEmitters != null || modelParticleEffectors != null)) {
			@Pc(32) var locType: LocType? = TypeLists.LOC!!.list(this.id)
			if (locType!!.alternativeIds != null) {
				locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider)
			}
			if (locType != null) {
				this.particleSystem = ParticleSystem.get(ClientState.clientCycle, true)
			}
		}
		if (this.particleSystem == null) {
			return
		}
		model.updateParticlePosition(matrix)
		if (performCycle) {
			this.particleSystem!!.update(toolkit, ClientState.clientCycle.toLong(), modelParticleEmitters, modelParticleEffectors)
		} else {
			this.particleSystem!!.setClock(ClientState.clientCycle.toLong())
		}
		this.particleSystem!!.updateBounds(this.level.toInt(), startX, endX, startZ, endZ)
	}

	@OriginalMember(owner = "client!bv", name = "b", descriptor = "(II)V")
	fun playSequence(@OriginalArg(1) sequenceId: Int) {
		this.hasSequenceId = true
		this.updateSequence(sequenceId)
	}

	@OriginalMember(owner = "client!bv", name = "finalize", descriptor = "()V")
	fun finalize() {
		if (this.particleSystem != null) {
			this.particleSystem!!.stopped()
		}
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(Lclient!ga;Z)V")
	private fun progressAnimation(@OriginalArg(0) entity: Entity?) {
		label80@ while (true) {
			if (this.sequence == null) {
				if (this.hasSequenceId) {
					return
				}
				this.updateSequence(-1)
				if (this.sequence == null) {
					return
				}
			}
			@Pc(33) var elapsed: Int = ClientState.clientCycle - this.frameStartCycle
			if (elapsed > 100 && this.sequence!!.replayInterval > 0) {
				@Pc(50) val replayStart: Int = this.sequence!!.frames!!.size - this.sequence!!.replayInterval
				while (this.currentFrame < replayStart && elapsed > this.sequence!!.durations!![this.currentFrame]) {
					elapsed -= this.sequence!!.durations!![this.currentFrame]
					this.currentFrame++
				}
				if (this.currentFrame >= replayStart) {
					@Pc(93) var loopDuration: Int = 0
					@Pc(95) var i: Int = replayStart
					while (i < this.sequence!!.frames!!.size) {
						loopDuration += this.sequence!!.durations!![i]
						i++
					}
					elapsed %= loopDuration
				}
				this.nextFrame = this.currentFrame + 1
				if (this.nextFrame >= this.sequence!!.frames!!.size) {
					this.nextFrame -= this.sequence!!.replayInterval
					if (this.nextFrame < 0 || this.sequence!!.frames!!.size <= this.nextFrame) {
						this.nextFrame = -1
					}
				}
			}
			while (elapsed > this.sequence!!.durations!![this.currentFrame]) {
				SoundManager.playSequenceSound(entity, this.currentFrame, this.sequence)
				elapsed -= this.sequence!!.durations!![this.currentFrame]
				this.currentFrame++
				if (this.sequence!!.frames!!.size <= this.currentFrame) {
					this.currentFrame -= this.sequence!!.replayInterval
					if (this.currentFrame < 0 || this.sequence!!.frames!!.size <= this.currentFrame) {
						this.sequence = null
						continue@label80
					}
				}
				this.nextFrame = this.currentFrame + 1
				if (this.sequence!!.frames!!.size <= this.nextFrame) {
					this.nextFrame -= this.sequence!!.replayInterval
					if (this.nextFrame < 0 || this.nextFrame >= this.sequence!!.frames!!.size) {
						this.nextFrame = -1
					}
				}
			}
			this.frameCycle = elapsed
			this.frameStartCycle = ClientState.clientCycle - elapsed
			return
		}
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(ILclient!ha;)V")
	fun removeShadow(@OriginalArg(1) toolkit: Toolkit?) {
		if (this.shadow != null) {
			Scene.removeShadow(this.shadow, this.virtualLevel.toInt(), this.entity!!.x, this.entity.z, this.castShadowAtPlane)
			this.shadow = null
			this.castShadowAtPlane = null
		}
	}
}
