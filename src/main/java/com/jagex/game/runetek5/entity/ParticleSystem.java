package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.entity.EntityNode;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.core.datastruct.ref.key.ShadedMaskTest;
import com.jagex.game.runetek5.graphics.model.particle.Particle;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import com.jagex.game.runetek5.entity.ParticleEmitter;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.cache.node.lru.ParticleEffector;
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager;

@OriginalClass("client!uj")
public final class ParticleSystem extends EntityNode {

	@OriginalMember(owner = "client!uj", name = "j", descriptor = "J")
	private long lastTick;

	@OriginalMember(owner = "client!uj", name = "k", descriptor = "J")
	private long clock;

	@OriginalMember(owner = "client!uj", name = "v", descriptor = "I")
	public int level;

	@OriginalMember(owner = "client!uj", name = "l", descriptor = "Z")
	private boolean stopped = false;

	@OriginalMember(owner = "client!uj", name = "o", descriptor = "I")
	public int nextParticleSlot = 0;

	@OriginalMember(owner = "client!uj", name = "i", descriptor = "Z")
	public boolean removed = false;

	@OriginalMember(owner = "client!uj", name = "q", descriptor = "Lclient!tca;")
	private EntityNodeList emitterCache = new EntityNodeList();

	@OriginalMember(owner = "client!uj", name = "s", descriptor = "I")
	private int emitterCount = 0;

	@OriginalMember(owner = "client!uj", name = "r", descriptor = "Lclient!uh;")
	public Deque effectorCache = new Deque();

	@OriginalMember(owner = "client!uj", name = "u", descriptor = "Z")
	public boolean hasOntick = false;

	@OriginalMember(owner = "client!uj", name = "x", descriptor = "I")
	private int effectorCount = 0;

	@OriginalMember(owner = "client!uj", name = "w", descriptor = "Z")
	private boolean awaitingStartup = false;

	@OriginalMember(owner = "client!uj", name = "t", descriptor = "Lclient!mda;")
	public final ParticleList list = new ParticleList();

	@OriginalMember(owner = "client!uj", name = "n", descriptor = "[Lclient!ps;")
	public final Particle[] movingParticles = new Particle[8192];

	@OriginalMember(owner = "client!uj", name = "<init>", descriptor = "(IZ)V")
	public ParticleSystem(@OriginalArg(0) int cycle, @OriginalArg(1) boolean hasOntick) {
		this.init(cycle, hasOntick);
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "()Lclient!mda;")
	public ParticleList getList() {
		return this.list;
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(Lclient!ha;)V")
	public void processCycleAllParticles(@OriginalArg(0) Toolkit toolkit) {
		this.list.particles.clear();
		for (@Pc(10) ParticleEmitter particleEmitter = (ParticleEmitter) this.emitterCache.getFirst(); particleEmitter != null; particleEmitter = (ParticleEmitter) this.emitterCache.getNext()) {
			particleEmitter.processCycle(toolkit, this.lastTick);
		}
	}

	@OriginalMember(owner = "client!uj", name = "c", descriptor = "()V")
	public void restart() {
		this.awaitingStartup = true;
	}

	@OriginalMember(owner = "client!uj", name = "d", descriptor = "()V")
	public void remove() {
		this.removed = true;
		for (@Pc(8) ParticleEffector effector = (ParticleEffector) this.effectorCache.first(); effector != null; effector = (ParticleEffector) this.effectorCache.next()) {
			if (effector.type.particleHandlingType == 1) {
				effector.unlinkSecondary();
			}
		}
		for (@Pc(27) int i = 0; i < this.movingParticles.length; i++) {
			if (this.movingParticles[i] != null) {
				this.movingParticles[i].remove();
				this.movingParticles[i] = null;
			}
		}
		this.nextParticleSlot = 0;
		this.emitterCache = new EntityNodeList();
		this.emitterCount = 0;
		this.effectorCache = new Deque();
		this.effectorCount = 0;
		this.unlinkNode();
		ParticleSystemManager.particleSystemsAwaitingRemoval[ParticleSystemManager.removalQueueTail] = this;
		ParticleSystemManager.removalQueueTail = ParticleSystemManager.removalQueueTail + 1 & ParticleSystemManager.DETAIL_LEVEL_LIMITS[ParticleSystemManager.detailsSetting];
	}

	@OriginalMember(owner = "client!uj", name = "b", descriptor = "(IZ)V")
	public void init(@OriginalArg(0) int cycle, @OriginalArg(1) boolean hasOntick) {
		ParticleSystemManager.sprite.offer(this);
		this.clock = cycle;
		this.lastTick = cycle;
		this.awaitingStartup = true;
		this.hasOntick = hasOntick;
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(Lclient!ha;[Lclient!nn;Z)V")
	private void updateEmitters(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) EmissiveTriangle[] emitters) {
		for (@Pc(1) int i = 0; i < 32; i++) {
			ParticleSystem.ACTIVE_PRODUCERS[i] = false;
		}
		@Pc(21) int index;
		label62: for (@Pc(16) ParticleEmitter particleEmitter = (ParticleEmitter) this.emitterCache.getFirst(); particleEmitter != null; particleEmitter = (ParticleEmitter) this.emitterCache.getNext()) {
			if (emitters != null) {
				for (index = 0; index < emitters.length; index++) {
					if (particleEmitter.model == emitters[index] || particleEmitter.model == emitters[index].child) {
						ParticleSystem.ACTIVE_PRODUCERS[index] = true;
						particleEmitter.updateTriangle();
						particleEmitter.inactive = false;
						continue label62;
					}
				}
			}
			if (particleEmitter.particleCount == 0) {
				particleEmitter.unlinkNode();
				this.emitterCount--;
			} else {
				particleEmitter.inactive = true;
			}
		}
		if (emitters == null) {
			return;
		}
		for (index = 0; index < emitters.length && index != 32 && this.emitterCount != 32; index++) {
			if (!ParticleSystem.ACTIVE_PRODUCERS[index]) {
				@Pc(104) ParticleEmitter particleEmitter2 = new ParticleEmitter(toolkit, emitters[index], this, this.clock);
				this.emitterCache.offer(particleEmitter2);
				this.emitterCount++;
				ParticleSystem.ACTIVE_PRODUCERS[index] = true;
			}
		}
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(IIIII)V")
	public void updateBounds(@OriginalArg(0) int level, @OriginalArg(1) int startX, @OriginalArg(2) int endX, @OriginalArg(3) int startZ, @OriginalArg(4) int endZ) {
		this.level = level;
	}

	@OriginalMember(owner = "client!uj", name = "e", descriptor = "()V")
	public void stopped() {
		this.stopped = true;
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(Lclient!ha;J)Z")
	public boolean performUpdate(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) long lastTick) {
		if (this.clock == this.lastTick) {
			this.running();
		} else {
			this.stopped();
		}
		if (lastTick - this.clock > 750L) {
			this.remove();
			return false;
		}
		@Pc(27) int elapsedTime = (int) (lastTick - this.lastTick);
		@Pc(36) ParticleEmitter particleEmitter;
		if (this.awaitingStartup) {
			for (particleEmitter = (ParticleEmitter) this.emitterCache.getFirst(); particleEmitter != null; particleEmitter = (ParticleEmitter) this.emitterCache.getNext()) {
				for (@Pc(39) int i = 0; i < particleEmitter.type.updatesPerCycle; i++) {
					particleEmitter.performUpdate(toolkit, 1, lastTick, !this.stopped);
				}
			}
			this.awaitingStartup = false;
		}
		for (particleEmitter = (ParticleEmitter) this.emitterCache.getFirst(); particleEmitter != null; particleEmitter = (ParticleEmitter) this.emitterCache.getNext()) {
			particleEmitter.performUpdate(toolkit, elapsedTime, lastTick, !this.stopped);
		}
		this.lastTick = lastTick;
		return true;
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(Lclient!ha;J[Lclient!nn;[Lclient!eca;Z)V")
	public void update(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) long clock, @OriginalArg(2) EmissiveTriangle[] emitters, @OriginalArg(3) EffectiveVertex[] effectors) {
		if (!this.removed) {
			this.updateEmitters(toolkit, emitters);
			this.updateEffectors(effectors);
			this.clock = clock;
		}
	}

	@OriginalMember(owner = "client!uj", name = "f", descriptor = "()V")
	private void running() {
		this.stopped = false;
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "([Lclient!eca;Z)V")
	private void updateEffectors(@OriginalArg(0) EffectiveVertex[] effectors) {
		for (@Pc(1) int i = 0; i < 8; i++) {
			ParticleSystem.EFFECT_SLOT_OCCUPIED[i] = false;
		}
		@Pc(21) int index;
		label71: for (@Pc(16) ParticleEffector particleEffector = (ParticleEffector) this.effectorCache.first(); particleEffector != null; particleEffector = (ParticleEffector) this.effectorCache.next()) {
			if (effectors != null) {
				for (index = 0; index < effectors.length; index++) {
					if (particleEffector.model == effectors[index] || particleEffector.model == effectors[index].child) {
						ParticleSystem.EFFECT_SLOT_OCCUPIED[index] = true;
						particleEffector.update();
						continue label71;
					}
				}
			}
			particleEffector.unlink();
			this.effectorCount--;
			if (particleEffector.islinkedSecondary()) {
				particleEffector.unlinkSecondary();
				ParticleSystemManager.activeVertexEffectCount--;
			}
		}
		if (effectors == null) {
			return;
		}
		for (index = 0; index < effectors.length && index != 8 && this.effectorCount != 8; index++) {
			if (!ParticleSystem.EFFECT_SLOT_OCCUPIED[index]) {
				@Pc(96) ParticleEffector particleEffector2 = null;
				if (effectors[index].getParticleType().particleHandlingType == 1 && ParticleSystemManager.activeVertexEffectCount < 32) {
					particleEffector2 = new ParticleEffector(effectors[index], this);
					ParticleSystemManager.activeEffectiveVertexEffects.put(particleEffector2, (long) effectors[index].effectId);
					ParticleSystemManager.activeVertexEffectCount++;
				}
				if (particleEffector2 == null) {
					particleEffector2 = new ParticleEffector(effectors[index], this);
				}
				this.effectorCache.addLast(particleEffector2);
				this.effectorCount++;
				ParticleSystem.EFFECT_SLOT_OCCUPIED[index] = true;
			}
		}
	}

	@OriginalMember(owner = "client!uj", name = "g", descriptor = "()Lclient!mda;")
	public ParticleList rebuildRenderedParticles() {
		this.list.particles.clear();
		for (@Pc(6) int i = 0; i < this.movingParticles.length; i++) {
			if (this.movingParticles[i] != null && this.movingParticles[i].producer != null) {
				this.list.particles.insert(this.movingParticles[i]);
			}
		}
		return this.list;
	}

	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(J)V")
	public void setClock(@OriginalArg(0) long clock) {
		this.clock = clock;
	}

	@OriginalMember(owner = "client!uj", name = "m", descriptor = "[Z")
	public static final boolean[] ACTIVE_PRODUCERS = new boolean[32];

	@OriginalMember(owner = "client!uj", name = "p", descriptor = "[Z")
	public static final boolean[] EFFECT_SLOT_OCCUPIED = new boolean[8];
	@OriginalMember(owner = "client!uj", name = "a", descriptor = "(IZ)Lclient!uj;")
	public static ParticleSystem get(@OriginalArg(0) int cycle, @OriginalArg(1) boolean hasOntick) {
		if (ParticleSystemManager.removalQueueTail == ParticleSystemManager.removalQueueHead) {
			return new ParticleSystem(cycle, hasOntick);
		} else {
			@Pc(6) ParticleSystem particleSystem = ParticleSystemManager.particleSystemsAwaitingRemoval[ParticleSystemManager.removalQueueHead];
			ParticleSystemManager.removalQueueHead = ParticleSystemManager.removalQueueHead + 1 & ParticleSystemManager.DETAIL_LEVEL_LIMITS[ParticleSystemManager.detailsSetting];
			particleSystem.init(cycle, hasOntick);
			return particleSystem;
		}
	}
}
