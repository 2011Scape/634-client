package com.jagex.game.runetek5

import com.jagex.game.runetek5.entity.EntityNode
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.entity.GroundRenderTask
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.core.util.TimeUtils

@OriginalClass("client!waa")
class RenderWorker @OriginalMember(owner = "client!waa", name = "<init>", descriptor = "(ILclient!ha;)V") constructor(@OriginalArg(0) threadId: Int, @OriginalArg(1) toolkit: Toolkit?) : Runnable {

	@OriginalMember(owner = "client!waa", name = "a", descriptor = "J")
	private var completionTime: Long = 0L

	@OriginalMember(owner = "client!waa", name = "b", descriptor = "Lclient!cia;")
	private var queue: RenderQueue? = null

	@OriginalMember(owner = "client!waa", name = "g", descriptor = "[Lclient!ab;")
	private val pointLights: Array<PointLight?> = arrayOfNulls<PointLight>(8)

	@OriginalMember(owner = "client!waa", name = "f", descriptor = "[I")
	private val screenCoords: IntArray = IntArray(3)

	@OriginalMember(owner = "client!waa", name = "i", descriptor = "Z")
	@Volatile
	private var paused: Boolean = true

	@OriginalMember(owner = "client!waa", name = "d", descriptor = "Z")
	@Volatile
	private var running: Boolean = true

	@OriginalMember(owner = "client!waa", name = "c", descriptor = "Z")
	@Volatile
	private var busy: Boolean = false

	@OriginalMember(owner = "client!waa", name = "h", descriptor = "I")
	private val threadId: Int

	@OriginalMember(owner = "client!waa", name = "e", descriptor = "Lclient!ha;")
	private val toolkit: Toolkit?

	init {
		this.threadId = threadId
		this.toolkit = toolkit
	}

	@OriginalMember(owner = "client!waa", name = "a", descriptor = "()J")
	fun getCompletionTime(): Long {
		return this.completionTime
	}

	@OriginalMember(owner = "client!waa", name = "run", descriptor = "()V")
	override fun run() {
		while (this.running) {
			this.process()
		}
	}

	@OriginalMember(owner = "client!waa", name = "b", descriptor = "()V")
	fun stop() {
		this.paused = false
		this.running = false
		synchronized(this) {
			(this as java.lang.Object).notify()
		}
	}

	@OriginalMember(owner = "client!waa", name = "a", descriptor = "(Lclient!cia;)V")
	fun setQueue(@OriginalArg(0) renderQueue: RenderQueue?) {
		if (this.queue != null) {
			this.queue!!.setWorker(null)
		}
		this.queue = renderQueue
		if (this.queue != null) {
			this.queue!!.setWorker(this)
		}
	}

	@OriginalMember(owner = "client!waa", name = "c", descriptor = "()V")
	private fun process() {
		this.toolkit!!.linkThreads(this.threadId)
		while (!this.paused && this.running) {
			if (this.queue == null || this.queue!!.isEmpty()) {
				this.busy = false
				this.completionTime = Scene.aClass158_1!!.nanoTime()
				synchronized(this) {
					try {
						(this as java.lang.Object).wait()
					} catch (@Pc(168) ex: InterruptedException) {
					}
				}
			} else {
				this.busy = true
				@Pc(21) val task: EntityNode? = this.queue!!.poll()
				if (task is Entity) {
					@Pc(27) val entity: Entity = task as Entity
					if (entity.particlesOnly) {
						entity.particleCycle(Scene.toolkit)
					} else {
						SceneDrawer.renderSceneEntity(entity, this.pointLights)
						if (W2DebugMode.font != null) {
							W2DebugMode.font!!.drawString(this.queue!!.name, -256, entity.screenY, entity.screenX, -16777216)
						}
					}
				} else {
					@Pc(58) val level: Int = (task as GroundRenderTask).level
					if (level >= 1 && level <= 4) {
						@Pc(70) val ground: Ground? = Scene.aClass65Array1!![level - 1]
						@Pc(72) var offsetX: Int = 0
						while (offsetX < SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
							@Pc(75) var offsetZ: Int = 0
							while (offsetZ < SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles) {
								if (Scene.levelTileVisibility!![level - 1]!![offsetX]!![offsetZ]) {
									@Pc(92) val x: Int = SceneDrawer.cameraGridPositionX + offsetX - SceneVisibleTiles.visibleTiles
									@Pc(98) val z: Int = Scene.cameraTileZ + offsetZ - SceneVisibleTiles.visibleTiles
									if (x >= 0 && x < ground!!.width && z >= 0 && z < ground.length) {
										Scene.toolkit!!.H(x shl Scene.tileUnits, ground.getHeight(z, x), z shl Scene.tileUnits, this.screenCoords)
										if (RenderWorker.getWorkerIndexForScreenX(this.screenCoords[0]) == this.threadId - 1) {
											ground.renderTile(x, z)
										}
									}
								}
								offsetZ++
							}
							offsetX++
						}
					}
				}
			}
		}
		this.toolkit.method7281(this.threadId)
		while (this.paused && this.running) {
			synchronized(this) {
				try {
					(this as java.lang.Object).wait()
				} catch (@Pc(195) ex2: InterruptedException) {
				}
			}
		}
	}

	@OriginalMember(owner = "client!waa", name = "d", descriptor = "()V")
	fun pause() {
		this.paused = true
		synchronized(this) {
			(this as java.lang.Object).notify()
		}
	}

	@OriginalMember(owner = "client!waa", name = "e", descriptor = "()V")
	fun resume() {
		this.paused = false
		synchronized(this) {
			(this as java.lang.Object).notify()
		}
	}

	@OriginalMember(owner = "client!waa", name = "f", descriptor = "()Z")
	fun isIdle(): Boolean {
		return this.queue == null || !this.busy && this.queue!!.isEmpty()
	}

	companion object {
		@OriginalMember(owner = "client!wq", name = "a", descriptor = "()V")
		@JvmStatic
		fun waitForWorkers() {
			while (true) {
				@Pc(1) var allIdle: Boolean = true
				@Pc(3) var index: Int
				index = 0
				while (index < RenderWorker.aClass366Array1!!.size) {
					if (RenderWorker.aClass366Array1!![index]!!.isIdle()) {
						SceneDrawer.workerCompletionTimes[index] = RenderWorker.aClass366Array1!![index]!!.getCompletionTime()
					} else {
						synchronized(RenderWorker.aClass366Array1!![index]!!) {
							(RenderWorker.aClass366Array1!![index] as java.lang.Object).notify()
						}
						allIdle = false
					}
					index++
				}
				if (allIdle) {
					RenderWorker.aClass366Array1!![RenderWorker.aClass366Array1!!.size - 1]!!.pause()
					SceneDrawer.assignRenderQueues(1)
					while (true) {
						allIdle = true
						index = 0
						while (index < RenderWorker.aClass366Array1!!.size - 1) {
							if (!RenderWorker.aClass366Array1!![index]!!.isIdle()) {
								synchronized(RenderWorker.aClass366Array1!![index]!!) {
									(RenderWorker.aClass366Array1!![index] as java.lang.Object).notify()
								}
								allIdle = false
							}
							index++
						}
						if (allIdle) {
							@Pc(102) var i: Int = 1
							while (i < RenderWorker.aClass366Array1!!.size - 2) {
								RenderWorker.aClass366Array1!![i]!!.pause()
								i++
							}
							SceneDrawer.assignRenderQueues(2)
							while (!RenderWorker.aClass366Array1!![0]!!.isIdle()) {
								synchronized(RenderWorker.aClass366Array1!![0]!!) {
									(RenderWorker.aClass366Array1!![0] as java.lang.Object).notify()
								}
								try {
									TimeUtils.sleep(1L)
								} catch (@Pc(142) ex: Exception) {
								}
							}
							RenderWorker.aClass366Array1!![0]!!.pause()
							return
						}
						try {
							TimeUtils.sleep(1L)
						} catch (@Pc(99) ex2: Exception) {
						}
					}
				}
				try {
					TimeUtils.sleep(1L)
				} catch (@Pc(47) ex3: Exception) {
				}
			}
		}

		@OriginalMember(owner = "client!cfa", name = "J", descriptor = "[Lclient!waa;")
		@JvmField
		var aClass366Array1: Array<RenderWorker?>? = null

		@OriginalMember(owner = "client!er", name = "e", descriptor = "(I)I")
		@JvmStatic
		fun getWorkerIndexForScreenX(@OriginalArg(0) screenX: Int): Int {
			@Pc(1) var workerIndex: Int = -1
			@Pc(3) var i: Int = 0
			while (i < Scene.renderThreadCount - 1) {
				if (screenX < ScriptRunner.threadSplitX!![i] + SceneDrawer.threadSplitOffsets!![i]) {
					workerIndex = i
					break
				}
				i++
			}
			if (workerIndex == -1) {
				workerIndex = Scene.renderThreadCount - 1
			}
			return workerIndex
		}
	}
}
