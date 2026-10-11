package com.jagex.game.runetek5.graphics.model.particle;

import com.jagex.game.runetek5.entity.ParticleSystem;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.HashTable;
import org.openrs2.deob.annotation.Pc;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.CutsceneManager;
import com.jagex.game.runetek5.config.particle.ParticleTypeList;
import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.config.particle.ParticleProducerType;
import com.jagex.game.runetek5.utils.Utils;

public final class ParticleSystemManager {
	@OriginalMember(owner = "client!rd", name = "y", descriptor = "[I")
	public static final int[] DETAIL_LEVEL_LIMITS = new int[] { 3, 7, 15 };

	@OriginalMember(owner = "client!bg", name = "O", descriptor = "I")
	public static int killIndex = 0;

	@OriginalMember(owner = "client!ch", name = "d", descriptor = "I")
	public static int renderedParticleCount = 0;

	@OriginalMember(owner = "client!eea", name = "s", descriptor = "[Lclient!ps;")
	public static Particle[] particleCache;

	@OriginalMember(owner = "client!rba", name = "z", descriptor = "I")
	public static int activeVertexEffectCount = 0;

	@OriginalMember(owner = "client!dh", name = "W", descriptor = "[Lclient!uj;")
	public static ParticleSystem[] particleSystemsAwaitingRemoval;

	@OriginalMember(owner = "client!w", name = "a", descriptor = "I")
	public static int lastRenderedParticleCount = 0;

	@OriginalMember(owner = "client!gba", name = "v", descriptor = "I")
	public static int detailsSetting = 2;

	private ParticleSystemManager() {
		/* empty */
	}
	@OriginalMember(owner = "client!client", name = "ib", descriptor = "I")
	public static int particleCacheCursorPos = 0;
	@OriginalMember(owner = "client!tm", name = "a", descriptor = "(B)V")
	public static void destroy() {
		ParticleSystemManager.activeEffectiveVertexEffects = new HashTable(8);
		ParticleSystemManager.activeVertexEffectCount = 0;
		for (@Pc(22) ParticleSystem particleSystem = (ParticleSystem) ParticleSystemManager.sprite.getFirst(); particleSystem != null; particleSystem = (ParticleSystem) ParticleSystemManager.sprite.getNext()) {
			particleSystem.remove();
		}
	}

	@OriginalMember(owner = "client!tp", name = "C", descriptor = "I")
	public static int removalQueueTail = 0;
	@OriginalMember(owner = "client!nia", name = "a", descriptor = "(II)V")
	public static void setDetail(@OriginalArg(0) int detailLevel) {
		if (detailLevel < 0 || detailLevel > 2) {
			detailLevel = 0;
		}
		ParticleSystemManager.detailsSetting = detailLevel;
		ParticleSystemManager.particleSystemsAwaitingRemoval = new ParticleSystem[ParticleSystemManager.DETAIL_LEVEL_LIMITS[ParticleSystemManager.detailsSetting] + 1];
		ParticleSystemManager.removalQueueTail = 0;
		ParticleSystemManager.removalQueueHead = 0;
	}
	@OriginalMember(owner = "client!nga", name = "c", descriptor = "I")
	public static int emitterCount = 0;

	@OriginalMember(owner = "client!vb", name = "d", descriptor = "[I")
	public static final int[] PARTICLE_COUNT_LIMITS = new int[] { 2047, 16383, 65535 };
	@OriginalMember(owner = "client!gha", name = "n", descriptor = "I")
	public static int removalQueueHead = 0;

	@OriginalMember(owner = "client!ol", name = "h", descriptor = "Lclient!tca;")
	public static EntityNodeList sprite;

	@OriginalMember(owner = "client!tca", name = "a", descriptor = "(Lclient!ha;I)V")
	public static void processParticleCycle(@OriginalArg(0) Toolkit toolkit) {
		for (@Pc(16) ParticleSystem particleSystem = (ParticleSystem) ParticleSystemManager.sprite.getFirst(); particleSystem != null; particleSystem = (ParticleSystem) ParticleSystemManager.sprite.getNext()) {
			if (particleSystem.hasOntick) {
				particleSystem.processCycleAllParticles(toolkit);
			}
		}
	}

	@OriginalMember(owner = "client!kg", name = "a", descriptor = "(ILclient!in;)V")
	public static void initializeIndex(@OriginalArg(1) Js5ResourceProvider particleResource) {
		ParticleSystemManager.particleCacheCursorPos = 0;
		ParticleSystemManager.killIndex = 0;
		ParticleSystemManager.sprite = new EntityNodeList();
		ParticleSystemManager.particleCache = new Particle[1024];
		ParticleSystemManager.particleSystemsAwaitingRemoval = new ParticleSystem[ParticleSystemManager.DETAIL_LEVEL_LIMITS[ParticleSystemManager.detailsSetting] + 1];
		ParticleSystemManager.removalQueueTail = 0;
		ParticleSystemManager.removalQueueHead = 0;
		ParticleProducerType.setParticleResourceProvider(particleResource);
		ParticleTypeList.initialize(particleResource);
	}

	@OriginalMember(owner = "client!wm", name = "a", descriptor = "(Lclient!ha;BJ)V")
	public static void updateParticleSystems(@OriginalArg(0) Toolkit toolkit, @OriginalArg(2) long lastTick) {
		ParticleSystemManager.emitterCount = 0;
		ParticleSystemManager.lastRenderedParticleCount = ParticleSystemManager.renderedParticleCount;
		CutsceneManager.runningParticleSystemCount = 0;
		ParticleSystemManager.renderedParticleCount = 0;
		@Pc(14) long startTime = Utils.currentTimeMillis();
		for (@Pc(19) ParticleSystem particleSystem = (ParticleSystem) ParticleSystemManager.sprite.getFirst(); particleSystem != null; particleSystem = (ParticleSystem) ParticleSystemManager.sprite.getNext()) {
			if (particleSystem.performUpdate(toolkit, lastTick)) {
				CutsceneManager.runningParticleSystemCount++;
			}
		}
		if (ParticleSystemManager.DEBUG_PARTICLE_STATS && lastTick % 100L == 0L) {
			System.out.println("Particle system count: " + ParticleSystemManager.sprite.size() + ", running: " + CutsceneManager.runningParticleSystemCount);
			System.out.println("Emitters: " + ParticleSystemManager.emitterCount + " Particles: " + ParticleSystemManager.renderedParticleCount + ". Time taken: " + (Utils.currentTimeMillis() - startTime) + "ms");
		}
	}
	@OriginalMember(owner = "client!or", name = "I", descriptor = "Lclient!ib;")
	public static HashTable activeEffectiveVertexEffects = new HashTable(8);
	@OriginalMember(owner = "client!iha", name = "G", descriptor = "Z")
	public static final boolean DEBUG_PARTICLE_STATS = false;
}
