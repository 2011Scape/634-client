package com.jagex.game.runetek5

import com.jagex.game.runetek5.entity.EntityNodeList
import com.jagex.game.runetek5.entity.EntityNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.entity.GroundRenderTask
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmField
import lockNotify
import withLock

@OriginalClass("client!cia")
class RenderQueue @OriginalMember(owner = "client!cia", name = "<init>", descriptor = "(Ljava/lang/String;)V") constructor(@OriginalArg(0) string: String?) {

	@OriginalMember(owner = "client!cia", name = "m", descriptor = "Lclient!waa;")
	private var worker: RenderWorker? = null

	@OriginalMember(owner = "client!cia", name = "n", descriptor = "I")
	@Volatile
	private var size: Int = 0

	@OriginalMember(owner = "client!cia", name = "c", descriptor = "Lclient!tca;")
	private val tasks: EntityNodeList = EntityNodeList()

	@OriginalMember(owner = "client!cia", name = "j", descriptor = "Ljava/lang/String;")
	@JvmField
	val name: String?

	init {
		this.name = string
	}

	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(Lclient!ga;Z)V")
	fun addEntity(@OriginalArg(0) entity: Entity?) {
		entity!!.particlesOnly = false
		@Pc(9) val linkedList: EntityNodeList = this.tasks
		withLock(this.tasks) {
			this.tasks.offer(entity)
			this.size++
		}
		if (this.worker != null) {
			@Pc(34) val renderWorker: RenderWorker? = this.worker
			withLock(this.worker!!) {
				lockNotify(this.worker)
			}
		}
	}

	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(Lclient!waa;B)V")
	fun setWorker(@OriginalArg(0) renderWorker: RenderWorker?) {
		this.worker = renderWorker
	}

	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(Lclient!vo;I)V")
	fun addGroundTask(@OriginalArg(0) groundRenderTask: GroundRenderTask?) {
		@Pc(9) val linkedList: EntityNodeList = this.tasks
		withLock(this.tasks) {
			this.tasks.offer(groundRenderTask)
			this.size++
		}
		if (this.worker != null) {
			@Pc(38) val renderWorker: RenderWorker? = this.worker
			withLock(this.worker!!) {
				lockNotify(this.worker)
			}
		}
	}

	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(I)Z")
	fun isEmpty(): Boolean {
		return this.size == 0
	}

	@OriginalMember(owner = "client!cia", name = "a", descriptor = "(ILclient!ga;)V")
	fun addParticleEntity(@OriginalArg(1) entity: Entity?) {
		entity!!.particlesOnly = true
		@Pc(9) val linkedList: EntityNodeList = this.tasks
		withLock(this.tasks) {
			this.tasks.offer(entity)
			this.size++
		}
		if (this.worker != null) {
			@Pc(40) val renderWorker: RenderWorker? = this.worker
			withLock(this.worker!!) {
				lockNotify(this.worker)
			}
		}
	}

	@OriginalMember(owner = "client!cia", name = "b", descriptor = "(I)Lclient!cm;")
	fun poll(): EntityNode? {
		@Pc(8) val linkedList: EntityNodeList = this.tasks
		withLock(this.tasks) {
			@Pc(22) val node: EntityNode? = this.tasks.getFirst()
			node!!.unlinkNode()
			this.size--
			return node
		}
	}

	companion object {
		@OriginalMember(owner = "client!dba", name = "x", descriptor = "[[Ljava/lang/String;")
		@JvmField
		val QUEUE_NAMES_BY_CORE_COUNT: Array<Array<String?>?> = arrayOf<Array<String?>?>(arrayOf<String?>("M1", "M2", "S1", "F"), arrayOf<String?>("M1", "M2", "M3", "S1", "S2", "F"), arrayOf<String?>("M1", "M2", "M3", "M4", "S1", "S2", "S3", "F"))
	}
}
