package com.jagex.game.runetek5;

import com.jagex.game.runetek5.entity.EntityNode;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.Entity;
import com.jagex.game.runetek5.entity.GroundRenderTask;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!waa")
public final class RenderWorker implements Runnable {

	@OriginalMember(owner = "client!waa", name = "a", descriptor = "J")
	private long completionTime;

	@OriginalMember(owner = "client!waa", name = "b", descriptor = "Lclient!cia;")
	private RenderQueue queue;

	@OriginalMember(owner = "client!waa", name = "g", descriptor = "[Lclient!ab;")
	private final PointLight[] pointLights = new PointLight[8];

	@OriginalMember(owner = "client!waa", name = "f", descriptor = "[I")
	private final int[] screenCoords = new int[3];

	@OriginalMember(owner = "client!waa", name = "i", descriptor = "Z")
	private volatile boolean paused = true;

	@OriginalMember(owner = "client!waa", name = "d", descriptor = "Z")
	private volatile boolean running = true;

	@OriginalMember(owner = "client!waa", name = "c", descriptor = "Z")
	private volatile boolean busy = false;

	@OriginalMember(owner = "client!waa", name = "h", descriptor = "I")
	private final int threadId;

	@OriginalMember(owner = "client!waa", name = "e", descriptor = "Lclient!ha;")
	private final Toolkit toolkit;

	@OriginalMember(owner = "client!waa", name = "<init>", descriptor = "(ILclient!ha;)V")
	public RenderWorker(@OriginalArg(0) int threadId, @OriginalArg(1) Toolkit toolkit) {
		this.threadId = threadId;
		this.toolkit = toolkit;
	}

	@OriginalMember(owner = "client!waa", name = "a", descriptor = "()J")
	public long getCompletionTime() {
		return this.completionTime;
	}

	@OriginalMember(owner = "client!waa", name = "run", descriptor = "()V")
	@Override
	public void run() {
		while (this.running) {
			this.process();
		}
	}

	@OriginalMember(owner = "client!waa", name = "b", descriptor = "()V")
	public void stop() {
		this.paused = false;
		this.running = false;
		synchronized (this) {
			this.notify();
		}
	}

	@OriginalMember(owner = "client!waa", name = "a", descriptor = "(Lclient!cia;)V")
	public void setQueue(@OriginalArg(0) RenderQueue renderQueue) {
		if (this.queue != null) {
			this.queue.setWorker(null);
		}
		this.queue = renderQueue;
		if (this.queue != null) {
			this.queue.setWorker(this);
		}
	}

	@OriginalMember(owner = "client!waa", name = "c", descriptor = "()V")
	private void process() {
		this.toolkit.linkThreads(this.threadId);
		while (!this.paused && this.running) {
			if (this.queue == null || this.queue.isEmpty()) {
				this.busy = false;
				this.completionTime = Scene.aClass158_1.nanoTime();
				synchronized (this) {
					try {
						this.wait();
					} catch (@Pc(168) InterruptedException ex) {
					}
				}
			} else {
				this.busy = true;
				@Pc(21) EntityNode task = this.queue.poll();
				if (task instanceof Entity) {
					@Pc(27) Entity entity = (Entity) task;
					if (entity.particlesOnly) {
						entity.particleCycle(Scene.toolkit);
					} else {
						SceneDrawer.renderSceneEntity(entity, this.pointLights);
						if (W2DebugMode.font != null) {
							W2DebugMode.font.drawString(this.queue.name, -256, entity.screenY, entity.screenX, -16777216);
						}
					}
				} else {
					@Pc(58) int level = ((GroundRenderTask) task).level;
					if (level >= 1 && level <= 4) {
						@Pc(70) Ground ground = Scene.aClass65Array1[level - 1];
						for (@Pc(72) int offsetX = 0; offsetX < SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles; offsetX++) {
							for (@Pc(75) int offsetZ = 0; offsetZ < SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles; offsetZ++) {
								if (Scene.levelTileVisibility[level - 1][offsetX][offsetZ]) {
									@Pc(92) int x = SceneDrawer.cameraGridPositionX + offsetX - SceneVisibleTiles.visibleTiles;
									@Pc(98) int z = Scene.cameraTileZ + offsetZ - SceneVisibleTiles.visibleTiles;
									if (x >= 0 && x < ground.width && z >= 0 && z < ground.length) {
										Scene.toolkit.H(x << Scene.tileUnits, ground.getHeight(z, x), z << Scene.tileUnits, this.screenCoords);
										if (RenderWorker.getWorkerIndexForScreenX(this.screenCoords[0]) == this.threadId - 1) {
											ground.renderTile(x, z);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		this.toolkit.method7281(this.threadId);
		while (this.paused && this.running) {
			synchronized (this) {
				try {
					this.wait();
				} catch (@Pc(195) InterruptedException ex2) {
				}
			}
		}
	}

	@OriginalMember(owner = "client!waa", name = "d", descriptor = "()V")
	public void pause() {
		this.paused = true;
		synchronized (this) {
			this.notify();
		}
	}

	@OriginalMember(owner = "client!waa", name = "e", descriptor = "()V")
	public void resume() {
		this.paused = false;
		synchronized (this) {
			this.notify();
		}
	}

	@OriginalMember(owner = "client!waa", name = "f", descriptor = "()Z")
	public boolean isIdle() {
		return this.queue == null || !this.busy && this.queue.isEmpty();
	}
	@OriginalMember(owner = "client!wq", name = "a", descriptor = "()V")
	public static void waitForWorkers() {
		while (true) {
			@Pc(1) boolean allIdle = true;
			@Pc(3) int index;
			for (index = 0; index < RenderWorker.aClass366Array1.length; index++) {
				if (RenderWorker.aClass366Array1[index].isIdle()) {
					SceneDrawer.workerCompletionTimes[index] = RenderWorker.aClass366Array1[index].getCompletionTime();
				} else {
					synchronized (RenderWorker.aClass366Array1[index]) {
						RenderWorker.aClass366Array1[index].notify();
					}
					allIdle = false;
				}
			}
			if (allIdle) {
				RenderWorker.aClass366Array1[RenderWorker.aClass366Array1.length - 1].pause();
				SceneDrawer.assignRenderQueues(1);
				while (true) {
					allIdle = true;
					for (index = 0; index < RenderWorker.aClass366Array1.length - 1; index++) {
						if (!RenderWorker.aClass366Array1[index].isIdle()) {
							synchronized (RenderWorker.aClass366Array1[index]) {
								RenderWorker.aClass366Array1[index].notify();
							}
							allIdle = false;
						}
					}
					if (allIdle) {
						for (@Pc(102) int i = 1; i < RenderWorker.aClass366Array1.length - 2; i++) {
							RenderWorker.aClass366Array1[i].pause();
						}
						SceneDrawer.assignRenderQueues(2);
						while (!RenderWorker.aClass366Array1[0].isIdle()) {
							synchronized (RenderWorker.aClass366Array1[0]) {
								RenderWorker.aClass366Array1[0].notify();
							}
							try {
								TimeUtils.sleep(1L);
							} catch (@Pc(142) Exception ex) {
							}
						}
						RenderWorker.aClass366Array1[0].pause();
						return;
					}
					try {
						TimeUtils.sleep(1L);
					} catch (@Pc(99) Exception ex2) {
					}
				}
			}
			try {
				TimeUtils.sleep(1L);
			} catch (@Pc(47) Exception ex3) {
			}
		}
	}

	@OriginalMember(owner = "client!cfa", name = "J", descriptor = "[Lclient!waa;")
	public static RenderWorker[] aClass366Array1;

	@OriginalMember(owner = "client!er", name = "e", descriptor = "(I)I")
	public static int getWorkerIndexForScreenX(@OriginalArg(0) int screenX) {
		@Pc(1) int workerIndex = -1;
		for (@Pc(3) int i = 0; i < Scene.renderThreadCount - 1; i++) {
			if (screenX < ScriptRunner.threadSplitX[i] + SceneDrawer.threadSplitOffsets[i]) {
				workerIndex = i;
				break;
			}
		}
		if (workerIndex == -1) {
			workerIndex = Scene.renderThreadCount - 1;
		}
		return workerIndex;
	}
}
