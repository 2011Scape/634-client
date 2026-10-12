package com.jagex.game.runetek5.graphics.model.particle

import com.jagex.game.runetek5.entity.ParticleSystem
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.node.HashTable
import org.openrs2.deob.annotation.Pc
import org.openrs2.deob.annotation.OriginalArg
import com.jagex.game.runetek5.CutsceneManager
import com.jagex.game.runetek5.config.particle.ParticleTypeList
import com.jagex.game.runetek5.entity.EntityNodeList
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.config.particle.ParticleProducerType
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object ParticleSystemManager {
	@OriginalMember(owner = "client!rd", name = "y", descriptor = "[I")
	@JvmField
	val DETAIL_LEVEL_LIMITS: IntArray = intArrayOf(3, 7, 15)

	@OriginalMember(owner = "client!bg", name = "O", descriptor = "I")
	@JvmField
	var killIndex: Int = 0

	@OriginalMember(owner = "client!ch", name = "d", descriptor = "I")
	@JvmField
	var renderedParticleCount: Int = 0

	@OriginalMember(owner = "client!eea", name = "s", descriptor = "[Lclient!ps;")
	@JvmField
	var particleCache: Array<Particle?>? = null

	@OriginalMember(owner = "client!rba", name = "z", descriptor = "I")
	@JvmField
	var activeVertexEffectCount: Int = 0

	@OriginalMember(owner = "client!dh", name = "W", descriptor = "[Lclient!uj;")
	@JvmField
	var particleSystemsAwaitingRemoval: Array<ParticleSystem?>? = null

	@OriginalMember(owner = "client!w", name = "a", descriptor = "I")
	@JvmField
	var lastRenderedParticleCount: Int = 0

	@OriginalMember(owner = "client!gba", name = "v", descriptor = "I")
	@JvmField
	var detailsSetting: Int = 2

	@OriginalMember(owner = "client!client", name = "ib", descriptor = "I")
	@JvmField
	var particleCacheCursorPos: Int = 0

	@OriginalMember(owner = "client!tm", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun destroy() {
		ParticleSystemManager.activeEffectiveVertexEffects = HashTable(8)
		ParticleSystemManager.activeVertexEffectCount = 0
		@Pc(22) var particleSystem: ParticleSystem? = ParticleSystemManager.sprite!!.getFirst() as ParticleSystem?
		while (particleSystem != null) {
			particleSystem.remove()
			particleSystem = ParticleSystemManager.sprite!!.getNext() as ParticleSystem?
		}
	}

	@OriginalMember(owner = "client!tp", name = "C", descriptor = "I")
	@JvmField
	var removalQueueTail: Int = 0

	@OriginalMember(owner = "client!nia", name = "a", descriptor = "(II)V")
	@JvmStatic
	fun setDetail(@OriginalArg(0) detailLevel: Int) {
		var detailLevel: Int = detailLevel
		if (detailLevel < 0 || detailLevel > 2) {
			detailLevel = 0
		}
		ParticleSystemManager.detailsSetting = detailLevel
		ParticleSystemManager.particleSystemsAwaitingRemoval = arrayOfNulls<ParticleSystem>(ParticleSystemManager.DETAIL_LEVEL_LIMITS[ParticleSystemManager.detailsSetting] + 1)
		ParticleSystemManager.removalQueueTail = 0
		ParticleSystemManager.removalQueueHead = 0
	}

	@OriginalMember(owner = "client!nga", name = "c", descriptor = "I")
	@JvmField
	var emitterCount: Int = 0

	@OriginalMember(owner = "client!vb", name = "d", descriptor = "[I")
	@JvmField
	val PARTICLE_COUNT_LIMITS: IntArray = intArrayOf(2047, 16383, 65535)

	@OriginalMember(owner = "client!gha", name = "n", descriptor = "I")
	@JvmField
	var removalQueueHead: Int = 0

	@OriginalMember(owner = "client!ol", name = "h", descriptor = "Lclient!tca;")
	@JvmField
	var sprite: EntityNodeList? = null

	@OriginalMember(owner = "client!tca", name = "a", descriptor = "(Lclient!ha;I)V")
	@JvmStatic
	fun processParticleCycle(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(16) var particleSystem: ParticleSystem? = ParticleSystemManager.sprite!!.getFirst() as ParticleSystem?
		while (particleSystem != null) {
			if (particleSystem.hasOntick) {
				particleSystem.processCycleAllParticles(toolkit)
			}
			particleSystem = ParticleSystemManager.sprite!!.getNext() as ParticleSystem?
		}
	}

	@OriginalMember(owner = "client!kg", name = "a", descriptor = "(ILclient!in;)V")
	@JvmStatic
	fun initializeIndex(@OriginalArg(1) particleResource: Js5ResourceProvider?) {
		ParticleSystemManager.particleCacheCursorPos = 0
		ParticleSystemManager.killIndex = 0
		ParticleSystemManager.sprite = EntityNodeList()
		ParticleSystemManager.particleCache = arrayOfNulls<Particle>(1024)
		ParticleSystemManager.particleSystemsAwaitingRemoval = arrayOfNulls<ParticleSystem>(ParticleSystemManager.DETAIL_LEVEL_LIMITS[ParticleSystemManager.detailsSetting] + 1)
		ParticleSystemManager.removalQueueTail = 0
		ParticleSystemManager.removalQueueHead = 0
		ParticleProducerType.setParticleResourceProvider(particleResource)
		ParticleTypeList.initialize(particleResource)
	}

	@OriginalMember(owner = "client!wm", name = "a", descriptor = "(Lclient!ha;BJ)V")
	@JvmStatic
	fun updateParticleSystems(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(2) lastTick: Long) {
		ParticleSystemManager.emitterCount = 0
		ParticleSystemManager.lastRenderedParticleCount = ParticleSystemManager.renderedParticleCount
		CutsceneManager.runningParticleSystemCount = 0
		ParticleSystemManager.renderedParticleCount = 0
		@Pc(14) val startTime: Long = Utils.currentTimeMillis()
		@Pc(19) var particleSystem: ParticleSystem? = ParticleSystemManager.sprite!!.getFirst() as ParticleSystem?
		while (particleSystem != null) {
			if (particleSystem.performUpdate(toolkit, lastTick)) {
				CutsceneManager.runningParticleSystemCount++
			}
			particleSystem = ParticleSystemManager.sprite!!.getNext() as ParticleSystem?
		}
		if (ParticleSystemManager.DEBUG_PARTICLE_STATS && lastTick % 100L == 0L) {
			println("Particle system count: " + ParticleSystemManager.sprite!!.size() + ", running: " + CutsceneManager.runningParticleSystemCount)
			println("Emitters: " + ParticleSystemManager.emitterCount + " Particles: " + ParticleSystemManager.renderedParticleCount + ". Time taken: " + (Utils.currentTimeMillis() - startTime) + "ms")
		}
	}

	@OriginalMember(owner = "client!or", name = "I", descriptor = "Lclient!ib;")
	@JvmField
	var activeEffectiveVertexEffects: HashTable = HashTable(8)

	@OriginalMember(owner = "client!iha", name = "G", descriptor = "Z")
	const val DEBUG_PARTICLE_STATS: Boolean = false
}
