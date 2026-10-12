package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.entity.gameobject.GameObject
import com.jagex.game.runetek5.graphics.model.particle.Silhouette
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.ModelAndShadow
import com.jagex.game.runetek5.cache.node.lru.Shadow
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.core.constants.LocShapes
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.OrthoMode
import kotlin.jvm.JvmField

@OriginalClass("client!dm")
class StaticLocation : AbstractInteractiveEntity, GameObject {

	@OriginalMember(owner = "client!dm", name = "nb", descriptor = "Lclient!hw;")
	private var cylinder: Silhouette? = null

	@OriginalMember(owner = "client!dm", name = "ub", descriptor = "S")
	private val id: Short

	// Java field "rotation" (hides AbstractInteractiveEntity.rotation); renamed because Kotlin forbids hiding.
	@OriginalMember(owner = "client!dm", name = "db", descriptor = "B")
	private val locRotation: Byte

	@OriginalMember(owner = "client!dm", name = "mb", descriptor = "B")
	private val shape: Byte

	@OriginalMember(owner = "client!dm", name = "cb", descriptor = "Z")
	private val interactive: Boolean

	@OriginalMember(owner = "client!dm", name = "fb", descriptor = "Z")
	private val underwater: Boolean

	@OriginalMember(owner = "client!dm", name = "hb", descriptor = "Z")
	private var copyNormals: Boolean

	@OriginalMember(owner = "client!dm", name = "I", descriptor = "Z")
	private val hardShadow: Boolean

	@OriginalMember(owner = "client!dm", name = "qb", descriptor = "Lclient!ka;")
	@JvmField
	var model: Model? = null

	@OriginalMember(owner = "client!dm", name = "yb", descriptor = "Lclient!r;")
	private var shadow: Shadow? = null

	@OriginalMember(owner = "client!dm", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIIIZIIIIIIZ)V")
	constructor(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) locType: LocType?, @OriginalArg(2) level: Int, @OriginalArg(3) virtualLevel: Int, @OriginalArg(4) x: Int, @OriginalArg(5) y: Int, @OriginalArg(6) z: Int, @OriginalArg(7) underwater: Boolean, @OriginalArg(8) startX: Int, @OriginalArg(9) endX: Int, @OriginalArg(10) startZ: Int, @OriginalArg(11) endZ: Int, @OriginalArg(12) shape: Int, @OriginalArg(13) rotation: Int, @OriginalArg(14) copyNormals: Boolean) : super(level, virtualLevel, x, y, z, startX, endX, startZ, endZ, locType!!.supportsItems == 1, LocShapes.getDiagonalOrientation(rotation, shape)) {
		this.id = locType!!.id.toShort()
		this.locRotation = rotation.toByte()
		this.shape = shape.toByte()
		this.interactive = locType.hasActions != 0 && !underwater
		this.underwater = underwater
		this.copyNormals = copyNormals
		this.virtualLevel = virtualLevel.toByte()
		this.hardShadow = toolkit!!.hardShadow() && locType.dynamicShadow && !this.underwater && GamePreferences.uid!!.hardShadows!!.getValue() != 0
		@Pc(77) var functionMask: Int = 2048
		if (this.copyNormals) {
			functionMask = functionMask or 0x10000
		}
		@Pc(92) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(toolkit, functionMask, this.hardShadow)
		if (modelAndShadow != null) {
			this.model = modelAndShadow.model
			this.shadow = modelAndShadow.shadow
			if (this.copyNormals) {
				this.model = this.model!!.copy(0.toByte(), functionMask, false)
				return
			}
		}
	}

	@OriginalMember(owner = "client!dm", name = "c", descriptor = "(I)V")
	override fun upload() {
		if (this.model != null) {
			this.model!!.uploadToGpu()
		}
	}

	@OriginalMember(owner = "client!dm", name = "c", descriptor = "(Z)I")
	override fun getModelMinY(): Int {
		return if (this.model == null) 0 else this.model!!.fa()
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(ILclient!ha;)V")
	override fun addShadow(@OriginalArg(1) toolkit: Toolkit?) {
		@Pc(14) val shadow: Shadow?
		if (this.shadow == null && this.hardShadow) {
			@Pc(25) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(toolkit, 262144, true)
			shadow = if (modelAndShadow == null) null else modelAndShadow.shadow
		} else {
			shadow = this.shadow
			this.shadow = null
		}
		if (shadow != null) {
			Shadow.renderShadows(shadow, this.virtualLevel.toInt(), this.x, this.z, null)
		}
	}

	@OriginalMember(owner = "client!dm", name = "i", descriptor = "(I)I")
	override fun getMinY(@OriginalArg(0) check: Int): Int {
		if (check != -25675) {
			Utils.hashString((null as String?)!!)
		}
		return if (this.model == null) 0 else this.model!!.ma()
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(I)Z")
	override fun hardShadow(): Boolean {
		return this.hardShadow
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(Lclient!ha;III)Z")
	override fun picked(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) mouseY: Int, @OriginalArg(2) mouseX: Int): Boolean {
		@Pc(13) val model: Model? = this.getModel(toolkit, 131072)
		if (model == null) {
			return false
		} else {
			@Pc(18) val matrix: Matrix = toolkit!!.scratchMatrix()!!
			matrix.applyTranslation(this.x, this.y, this.z)
			return if (OrthoMode.orthographicProjection) model.pickedOrtho(mouseX, mouseY, matrix, false, 0, HintArrow.scaledOrthoZoom) else model.picked(mouseX, mouseY, matrix, false, 0)
		}
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(ILclient!ha;I)Lclient!ka;")
	private fun getModel(@OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) functionMask: Int): Model? {
		if (this.model != null && toolkit!!.compareFunctionMasks(this.model!!.ua(), functionMask) == 0) {
			return this.model
		} else {
			@Pc(32) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(toolkit, functionMask, false)
			return if (modelAndShadow == null) null else modelAndShadow.model
		}
	}

	@OriginalMember(owner = "client!dm", name = "c", descriptor = "(Lclient!ha;I)V")
	override fun particleCycle(@OriginalArg(0) toolkit: Toolkit?) {
	}

	@OriginalMember(owner = "client!dm", name = "n", descriptor = "(I)I")
	fun shadowValue(): Int {
		return if (this.model == null) 15 else this.model!!.na() / 4
	}

	@OriginalMember(owner = "client!dm", name = "j", descriptor = "(I)Z")
	override fun hasTileShadow(): Boolean {
		return this.copyNormals
	}

	@OriginalMember(owner = "client!dm", name = "d", descriptor = "(I)I")
	override fun getRotation(): Int {
		return this.locRotation.toInt()
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	override fun shareLight(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) offsetY: Int, @OriginalArg(2) hideSharedFaces: Boolean, @OriginalArg(3) entity: Entity?, @OriginalArg(4) offsetZ: Int, @OriginalArg(6) offsetX: Int) {
		if (entity is StaticWall) {
			@Pc(9) val staticWall: StaticWall = entity
			if (this.model != null && staticWall.model != null) {
				this.model!!.shareLight(staticWall.model, offsetX, offsetY, offsetZ, hideSharedFaces)
			}
		} else if (entity is StaticLocation) {
			@Pc(32) val staticLocation: StaticLocation = entity
			if (this.model != null && staticLocation.model != null) {
				this.model!!.shareLight(staticLocation.model, offsetX, offsetY, offsetZ, hideSharedFaces)
			}
		}
	}

	@OriginalMember(owner = "client!dm", name = "e", descriptor = "(I)I")
	override fun getId(): Int {
		return this.id.toInt() and 0xFFFF
	}

	@OriginalMember(owner = "client!dm", name = "b", descriptor = "(Z)V")
	override fun removeTileShadow() {
		this.copyNormals = false
		if (this.model != null) {
			this.model!!.s(this.model!!.ua() and 0xFFFEFFFF.toInt())
		}
	}

	@OriginalMember(owner = "client!dm", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	override fun getCylinder(@OriginalArg(0) toolkit: Toolkit?): Silhouette? {
		if (this.cylinder == null) {
			this.cylinder = Silhouette.fromModel(this.x, this.getModel(toolkit, 0), this.z, this.y)
		}
		return this.cylinder
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(ILclient!ha;IZ)Lclient!ak;")
	private fun getModelAndShadow(@OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) functionMask: Int, @OriginalArg(3) withShadow: Boolean): ModelAndShadow? {
		@Pc(13) val locType: LocType? = TypeLists.LOC!!.list(this.id.toInt() and 0xFFFF)
		@Pc(21) val ground: Ground?
		@Pc(25) val ground2: Ground?
		if (this.underwater) {
			ground = Scene.aClass65Array3!![this.virtualLevel.toInt()]
			ground2 = Scene.aClass65Array2!![0]
		} else {
			ground = Scene.aClass65Array2!![this.virtualLevel.toInt()]
			if (this.virtualLevel >= 3) {
				ground2 = null
			} else {
				ground2 = Scene.aClass65Array2!![this.virtualLevel + 1]
			}
		}
		return locType!!.getModelAndShadow(toolkit, ground2, if (this.shape.toInt() == 11) 10 else this.shape.toInt(), functionMask, this.x, withShadow, ground, this.y, this.z, if (this.shape.toInt() == 11) this.locRotation + 4 else this.locRotation.toInt())
	}

	@OriginalMember(owner = "client!dm", name = "g", descriptor = "(I)Z")
	override fun isStationary(): Boolean {
		if (this.model == null) {
			return true
		} else {
			return !this.model!!.r()
		}
	}

	@OriginalMember(owner = "client!dm", name = "b", descriptor = "(B)Z")
	override fun hasAlpha(): Boolean {
		return if (this.model == null) false else this.model!!.F()
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(Lclient!ha;I)V")
	override fun removeShadow(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(33) val shadow: Shadow?
		if (this.shadow == null && this.hardShadow) {
			@Pc(26) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(toolkit, 262144, true)
			shadow = if (modelAndShadow == null) null else modelAndShadow.shadow
		} else {
			shadow = this.shadow
			this.shadow = null
		}
		if (shadow != null) {
			Scene.removeShadow(shadow, this.virtualLevel.toInt(), this.x, this.z, null)
		}
	}

	@OriginalMember(owner = "client!dm", name = "b", descriptor = "(I)I")
	override fun getShape(): Int {
		return this.shape.toInt()
	}

	@OriginalMember(owner = "client!dm", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	override fun render(@OriginalArg(1) toolkit: Toolkit?): PickableEntity? {
		if (this.model == null) {
			return null
		}
		@Pc(11) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.applyTranslation(this.x, this.y, this.z)
		@Pc(25) val entity: PickableEntity = PickableEntity.obtain(1, this.interactive)
		if (OrthoMode.orthographicProjection) {
			this.model!!.renderOrtho(matrix, entity.boundingBoxPolygons!![0], HintArrow.scaledOrthoZoom, 0)
		} else {
			this.model!!.render(matrix, entity.boundingBoxPolygons!![0], 0)
		}
		return entity
	}

	companion object {
		@OriginalMember(owner = "client!dm", name = "lb", descriptor = "I")
		@JvmField
		var lb: Int = -60
	}
}
