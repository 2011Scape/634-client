package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.graphics.model.particle.Particle
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.cache.node.lru.ParticleEffector
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager

@OriginalClass("client!uj")
class ParticleSystem : EntityNode {

	@OriginalMember(owner = "client!uj", name = "j", descriptor = "J")
	private var lastTick: Long = 0L

	@OriginalMember(owner = "client!uj", name = "k", descriptor = "J")
	private var clock: Long = 0L

	@OriginalMember(owner = "client!uj", name = "v", descriptor = "I")
	@JvmField
	var level: Int = 0

	@OriginalMember(owner = "client!uj", name = "l", descriptor = "Z")
	private var stopped: Boolean = false

	@OriginalMember(owner = "client!uj", name = "o", descriptor = "I")
	@JvmField
	var nextParticleSlot: Int = 0

	@OriginalMember(owner = "client!uj", name = "i", descriptor = "Z")
	@JvmField
	var removed: Boolean = false

	@OriginalMember(owner = "client!uj", name = "q", descriptor = "Lclient!tca;")
	private var emitterCache: EntityNodeList = EntityNodeList()

	@OriginalMember(owner = "client!uj", name = "s", descriptor = "I")
	private var emitterCount: Int = 0

	@OriginalMember(owner = "client!uj", name = "r", descriptor = "Lclient!uh;")
	@JvmField
	var effectorCache: Deque = Deque()

	@OriginalMember(owner = "client!uj", name = "u", descriptor = "Z")
	@JvmField
	var hasOntick: Boolean = false

	@OriginalMember(owner = "client!uj", name = "x", descriptor = "I")
	private var effectorCount: Int = 0

	@OriginalMember(owner = "client!uj", name = "w", descriptor = "Z")
	private var awaitingStartup: Boolean = false

	@OriginalMember(owner = "client!uj", name = "t", descriptor = "Lclient!mda;")
	@JvmField
	val list: ParticleList = ParticleList()

	@OriginalMember(owner = "client!uj", name = "n", descriptor = "[Lclient!ps;")
	@JvmField
	val movingParticles: Array<Particle?> = arrayOfNulls<Particle>(8192)

	@OriginalMember(owner = "client!uj", name = "<init>", descriptor = "(IZ)V")
	constructor(@OriginalArg(0) cycle: Int, @OriginalArg(1) hasOntick: Boolean) : super() {
		this.init(cycle, hasOntick)
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "()Lclient!mda;")
	fun getList(): ParticleList {
		return this.list
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(Lclient!ha;)V")
	fun processCycleAllParticles(@OriginalArg(0) toolkit: Toolkit?) {
		this.list.particles.clear()
		@Pc(10) var particleEmitter: ParticleEmitter? = this.emitterCache.getFirst() as ParticleEmitter?
		while (particleEmitter != null) {
			particleEmitter.processCycle(toolkit, this.lastTick)
			particleEmitter = this.emitterCache.getNext() as ParticleEmitter?
		}
	}

	@OriginalMember(owner = "client!uj", name = "c", descriptor = "()V")
	fun restart() {
		this.awaitingStartup = true
	}

	@OriginalMember(owner = "client!uj", name = "d", descriptor = "()V")
	fun remove() {
		this.removed = true
		@Pc(8) var effector: ParticleEffector? = this.effectorCache.first() as ParticleEffector?
		while (effector != null) {
			if (effector.type!!.particleHandlingType == 1) {
				effector.unlinkSecondary()
			}
			effector = this.effectorCache.next() as ParticleEffector?
		}
		@Pc(27) var i: Int = 0
		while (i < this.movingParticles.size) {
			if (this.movingParticles[i] != null) {
				this.movingParticles[i]!!.remove()
				this.movingParticles[i] = null
			}
			i++
		}
		this.nextParticleSlot = 0
		this.emitterCache = EntityNodeList()
		this.emitterCount = 0
		this.effectorCache = Deque()
		this.effectorCount = 0
		this.unlinkNode()
		ParticleSystemManager.particleSystemsAwaitingRemoval!![ParticleSystemManager.removalQueueTail] = this
		ParticleSystemManager.removalQueueTail = (ParticleSystemManager.removalQueueTail + 1) and ParticleSystemManager.DETAIL_LEVEL_LIMITS[ParticleSystemManager.detailsSetting]
	}

	@OriginalMember(owner = "client!uj", name = "b", descriptor = "(IZ)V")
	fun init(@OriginalArg(0) cycle: Int, @OriginalArg(1) hasOntick: Boolean) {
		ParticleSystemManager.sprite!!.offer(this)
		this.clock = cycle.toLong()
		this.lastTick = cycle.toLong()
		this.awaitingStartup = true
		this.hasOntick = hasOntick
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(Lclient!ha;[Lclient!nn;Z)V")
	private fun updateEmitters(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) emitters: Array<EmissiveTriangle?>?) {
		@Pc(1) var i: Int = 0
		while (i < 32) {
			ParticleSystem.ACTIVE_PRODUCERS[i] = false
			i++
		}
		@Pc(21) var index: Int
		@Pc(16) var particleEmitter: ParticleEmitter? = this.emitterCache.getFirst() as ParticleEmitter?
		label62@ while (particleEmitter != null) {
			if (emitters != null) {
				index = 0
				while (index < emitters.size) {
					if (particleEmitter!!.model === emitters[index] || particleEmitter!!.model === emitters[index]!!.child) {
						ParticleSystem.ACTIVE_PRODUCERS[index] = true
						particleEmitter!!.updateTriangle()
						particleEmitter!!.inactive = false
						particleEmitter = this.emitterCache.getNext() as ParticleEmitter?
						continue@label62
					}
					index++
				}
			}
			if (particleEmitter!!.particleCount == 0) {
				particleEmitter!!.unlinkNode()
				this.emitterCount--
			} else {
				particleEmitter!!.inactive = true
			}
			particleEmitter = this.emitterCache.getNext() as ParticleEmitter?
		}
		if (emitters == null) {
			return
		}
		index = 0
		while (index < emitters.size && index != 32 && this.emitterCount != 32) {
			if (!ParticleSystem.ACTIVE_PRODUCERS[index]) {
				@Pc(104) val particleEmitter2: ParticleEmitter = ParticleEmitter(toolkit, emitters[index], this, this.clock)
				this.emitterCache.offer(particleEmitter2)
				this.emitterCount++
				ParticleSystem.ACTIVE_PRODUCERS[index] = true
			}
			index++
		}
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(IIIII)V")
	fun updateBounds(@OriginalArg(0) level: Int, @OriginalArg(1) startX: Int, @OriginalArg(2) endX: Int, @OriginalArg(3) startZ: Int, @OriginalArg(4) endZ: Int) {
		this.level = level
	}

	@OriginalMember(owner = "client!uj", name = "e", descriptor = "()V")
	fun stopped() {
		this.stopped = true
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(Lclient!ha;J)Z")
	fun performUpdate(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) lastTick: Long): Boolean {
		if (this.clock == this.lastTick) {
			this.running()
		} else {
			this.stopped()
		}
		if (lastTick - this.clock > 750L) {
			this.remove()
			return false
		}
		@Pc(27) val elapsedTime: Int = (lastTick - this.lastTick).toInt()
		@Pc(36) var particleEmitter: ParticleEmitter?
		if (this.awaitingStartup) {
			particleEmitter = this.emitterCache.getFirst() as ParticleEmitter?
			while (particleEmitter != null) {
				@Pc(39) var i: Int = 0
				while (i < particleEmitter.type!!.updatesPerCycle) {
					particleEmitter.performUpdate(toolkit, 1, lastTick, !this.stopped)
					i++
				}
				particleEmitter = this.emitterCache.getNext() as ParticleEmitter?
			}
			this.awaitingStartup = false
		}
		particleEmitter = this.emitterCache.getFirst() as ParticleEmitter?
		while (particleEmitter != null) {
			particleEmitter.performUpdate(toolkit, elapsedTime, lastTick, !this.stopped)
			particleEmitter = this.emitterCache.getNext() as ParticleEmitter?
		}
		this.lastTick = lastTick
		return true
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(Lclient!ha;J[Lclient!nn;[Lclient!eca;Z)V")
	fun update(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) clock: Long, @OriginalArg(2) emitters: Array<EmissiveTriangle?>?, @OriginalArg(3) effectors: Array<EffectiveVertex?>?) {
		if (!this.removed) {
			this.updateEmitters(toolkit, emitters)
			this.updateEffectors(effectors)
			this.clock = clock
		}
	}

	@OriginalMember(owner = "client!uj", name = "f", descriptor = "()V")
	private fun running() {
		this.stopped = false
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "([Lclient!eca;Z)V")
	private fun updateEffectors(@OriginalArg(0) effectors: Array<EffectiveVertex?>?) {
		@Pc(1) var i: Int = 0
		while (i < 8) {
			ParticleSystem.EFFECT_SLOT_OCCUPIED[i] = false
			i++
		}
		@Pc(21) var index: Int
		@Pc(16) var particleEffector: ParticleEffector? = this.effectorCache.first() as ParticleEffector?
		label71@ while (particleEffector != null) {
			if (effectors != null) {
				index = 0
				while (index < effectors.size) {
					if (particleEffector!!.model === effectors[index] || particleEffector!!.model === effectors[index]!!.child) {
						ParticleSystem.EFFECT_SLOT_OCCUPIED[index] = true
						particleEffector!!.update()
						particleEffector = this.effectorCache.next() as ParticleEffector?
						continue@label71
					}
					index++
				}
			}
			particleEffector!!.unlink()
			this.effectorCount--
			if (particleEffector!!.islinkedSecondary()) {
				particleEffector!!.unlinkSecondary()
				ParticleSystemManager.activeVertexEffectCount--
			}
			particleEffector = this.effectorCache.next() as ParticleEffector?
		}
		if (effectors == null) {
			return
		}
		index = 0
		while (index < effectors.size && index != 8 && this.effectorCount != 8) {
			if (!ParticleSystem.EFFECT_SLOT_OCCUPIED[index]) {
				@Pc(96) var particleEffector2: ParticleEffector? = null
				if (effectors[index]!!.getParticleType()!!.particleHandlingType == 1 && ParticleSystemManager.activeVertexEffectCount < 32) {
					particleEffector2 = ParticleEffector(effectors[index], this)
					ParticleSystemManager.activeEffectiveVertexEffects.put(particleEffector2, effectors[index]!!.effectId.toLong())
					ParticleSystemManager.activeVertexEffectCount++
				}
				if (particleEffector2 == null) {
					particleEffector2 = ParticleEffector(effectors[index], this)
				}
				this.effectorCache.addLast(particleEffector2)
				this.effectorCount++
				ParticleSystem.EFFECT_SLOT_OCCUPIED[index] = true
			}
			index++
		}
	}

	@OriginalMember(owner = "client!uj", name = "g", descriptor = "()Lclient!mda;")
	fun rebuildRenderedParticles(): ParticleList {
		this.list.particles.clear()
		@Pc(6) var i: Int = 0
		while (i < this.movingParticles.size) {
			if (this.movingParticles[i] != null && this.movingParticles[i]!!.producer != null) {
				this.list.particles.insert(this.movingParticles[i])
			}
			i++
		}
		return this.list
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(J)V")
	fun setClock(@OriginalArg(0) clock: Long) {
		this.clock = clock
	}

	companion object {
		@OriginalMember(owner = "client!uj", name = "m", descriptor = "[Z")
		@JvmField
		val ACTIVE_PRODUCERS: BooleanArray = BooleanArray(32)

		@OriginalMember(owner = "client!uj", name = "p", descriptor = "[Z")
		@JvmField
		val EFFECT_SLOT_OCCUPIED: BooleanArray = BooleanArray(8)

		@OriginalMember(owner = "client!uj", name = "a", descriptor = "(IZ)Lclient!uj;")
		@JvmStatic
		fun get(@OriginalArg(0) cycle: Int, @OriginalArg(1) hasOntick: Boolean): ParticleSystem {
			if (ParticleSystemManager.removalQueueTail == ParticleSystemManager.removalQueueHead) {
				return ParticleSystem(cycle, hasOntick)
			} else {
				@Pc(6) val particleSystem: ParticleSystem? = ParticleSystemManager.particleSystemsAwaitingRemoval!![ParticleSystemManager.removalQueueHead]
				ParticleSystemManager.removalQueueHead = (ParticleSystemManager.removalQueueHead + 1) and ParticleSystemManager.DETAIL_LEVEL_LIMITS[ParticleSystemManager.detailsSetting]
				particleSystem!!.init(cycle, hasOntick)
				return particleSystem
			}
		}
	}
}
