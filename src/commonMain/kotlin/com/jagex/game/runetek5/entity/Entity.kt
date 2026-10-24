package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.graphics.model.particle.Silhouette
import com.jagex.game.runetek5.cache.node.PointLight
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.model.FaceBuffer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.rsinterface.cs2.CS2Executor
import com.jagex.game.runetek5.scene.Scene
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!ga")
abstract class Entity : EntityNode {

	@OriginalMember(owner = "client!ga", name = "i", descriptor = "B")
	@JvmField
	var virtualLevel: Byte = 0

	@OriginalMember(owner = "client!ga", name = "j", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!ga", name = "l", descriptor = "Lclient!ga;")
	@JvmField
	var nextEntity: Entity? = null

	@OriginalMember(owner = "client!ga", name = "m", descriptor = "I")
	@JvmField
	var screenY: Int = 0

	@OriginalMember(owner = "client!ga", name = "n", descriptor = "B")
	@JvmField
	var level: Byte = 0

	@OriginalMember(owner = "client!ga", name = "o", descriptor = "I")
	@JvmField
	var y: Int = 0

	@OriginalMember(owner = "client!ga", name = "s", descriptor = "I")
	@JvmField
	var screenX: Int = 0

	@OriginalMember(owner = "client!ga", name = "t", descriptor = "Z")
	@JvmField
	var particlesOnly: Boolean = false

	@OriginalMember(owner = "client!ga", name = "u", descriptor = "I")
	@JvmField
	var z: Int = 0

	@OriginalMember(owner = "client!ga", name = "v", descriptor = "I")
	@JvmField
	var screenDepth: Int = 0

	@OriginalMember(owner = "client!ga", name = "x", descriptor = "Z")
	@JvmField
	var markDirtyOnRender: Boolean = false

	@OriginalMember(owner = "client!ga", name = "<init>", descriptor = "()V")
	constructor() : super() {
	}

	@OriginalMember(owner = "client!ga", name = "g", descriptor = "(I)Z")
	abstract fun isStationary(): Boolean

	@OriginalMember(owner = "client!ga", name = "b", descriptor = "(B)Z")
	abstract fun hasAlpha(): Boolean

	@OriginalMember(owner = "client!ga", name = "h", descriptor = "(I)Z")
	abstract fun canRender(): Boolean

	@OriginalMember(owner = "client!ga", name = "i", descriptor = "(I)I")
	abstract fun getMinY(@OriginalArg(0) check: Int): Int

	@OriginalMember(owner = "client!ga", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	abstract fun shareLight(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) offsetY: Int, @OriginalArg(2) hideSharedFaces: Boolean, @OriginalArg(3) entity: Entity?, @OriginalArg(4) offsetZ: Int, @OriginalArg(6) offsetX: Int)

	@OriginalMember(owner = "client!ga", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	abstract fun getCylinder(@OriginalArg(0) toolkit: Toolkit?): Silhouette?

	@OriginalMember(owner = "client!ga", name = "c", descriptor = "(B)Z")
	abstract fun isOccluded(): Boolean

	@OriginalMember(owner = "client!ga", name = "a", descriptor = "([Lclient!ab;I)I")
	abstract fun findLights(@OriginalArg(0) pointLights: Array<PointLight?>?): Int

	@OriginalMember(owner = "client!ga", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	abstract fun render(@OriginalArg(1) toolkit: Toolkit?): PickableEntity?

	@OriginalMember(owner = "client!ga", name = "c", descriptor = "(Lclient!ha;I)V")
	abstract fun particleCycle(@OriginalArg(0) toolkit: Toolkit?)

	@OriginalMember(owner = "client!ga", name = "j", descriptor = "(I)Z")
	abstract fun hasTileShadow(): Boolean

	@OriginalMember(owner = "client!ga", name = "a", descriptor = "([Lclient!ab;III)I")
	fun findTileLights(@OriginalArg(0) pointLights: Array<PointLight?>?, @OriginalArg(1) tileX: Int, @OriginalArg(3) tileZ: Int): Int {
		@Pc(20) val lightUids: Long = Scene.tileLightUids!![this.level.toInt()]!![tileX]!![tileZ]
		@Pc(22) var uidShift: Long = 0L
		@Pc(24) var lightCount: Int = 0
		@Pc(34) val local34: Int
		while (uidShift <= 48L) {
			val lightIndex: Int = ((lightUids shr uidShift.toInt()) and 0xFFFFL).toInt()
			if (lightIndex <= 0) {
				break
			}
			pointLights!![lightCount++] = Scene.aClass300Array1!![lightIndex - 1]!!.pointLight
			uidShift += 16L
		}
		var slot: Int = lightCount
		while (slot < 4) {
			pointLights!![slot] = null
			slot++
		}
		return lightCount
	}

	@OriginalMember(owner = "client!ga", name = "a", descriptor = "(Lclient!ha;III)Z")
	abstract fun picked(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) mouseY: Int, @OriginalArg(2) mouseX: Int): Boolean

	/**
	 * Not part of the original client: the triangles [picked] tests, projected into [out] (Model.projectFaces), for a
	 * plugin's outline of a location (plugin.LocGeometry). False when this kind of entity or its toolkit cannot.
	 */
	open fun pickShape(toolkit: Toolkit?, out: FaceBuffer): Boolean = false

	@OriginalMember(owner = "client!ga", name = "b", descriptor = "(Z)V")
	abstract fun removeTileShadow()

	@OriginalMember(owner = "client!ga", name = "l", descriptor = "(I)I")
	open fun getPickSizeShift(): Int {
		return 0
	}

	@OriginalMember(owner = "client!ga", name = "c", descriptor = "(Z)I")
	abstract fun getModelMinY(): Int

	companion object {
		@OriginalMember(owner = "client!ed", name = "a", descriptor = "(Lclient!ga;)V")
		@JvmStatic
		fun computeScreenPosition(@OriginalArg(0) entity: Entity?) {
			Scene.toolkit!!.H(entity!!.x, entity.y + (entity.getModelMinY() shr 1), entity.z, CS2Executor.projectionBuffer)
			entity.screenX = CS2Executor.projectionBuffer[0]
			entity.screenY = CS2Executor.projectionBuffer[1]
			entity.screenDepth = CS2Executor.projectionBuffer[2]
		}

		@OriginalMember(owner = "client!kd", name = "a", descriptor = "([Lclient!ga;II)V")
		@JvmStatic
		fun quicksortAbove(@OriginalArg(0) entitys: Array<Entity?>?, @OriginalArg(1) start: Int, @OriginalArg(2) end: Int) {
			if (start >= end) {
				return
			}
			@Pc(8) val mid: Int = (start + end) / 2
			@Pc(10) var storeIndex: Int = start
			@Pc(14) val entity: Entity? = entitys!![mid]
			entitys[mid] = entitys[end]
			entitys[end] = entity
			@Pc(27) val screenDepth: Int = entity!!.screenDepth
			@Pc(29) var i: Int = start
			while (i < end) {
				if (entitys[i]!!.screenDepth > screenDepth + (i and 0x1)) {
					@Pc(44) val entity2: Entity? = entitys[i]
					entitys[i] = entitys[storeIndex]
					entitys[storeIndex++] = entity2
				}
				i++
			}
			entitys[end] = entitys[storeIndex]
			entitys[storeIndex] = entity
			quicksortAbove(entitys, start, storeIndex - 1)
			quicksortAbove(entitys, storeIndex + 1, end)
		}
	}
}
