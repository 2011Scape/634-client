package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.core.constants.LocShapes
import com.jagex.game.runetek5.entity.gameobject.GameObject
import com.jagex.game.runetek5.config.loctype.LocInteractivity
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
import com.jagex.game.runetek5.OrthoMode

@OriginalClass("client!ge")
class StaticWall : Wall, GameObject {

	@OriginalMember(owner = "client!ge", name = "db", descriptor = "Lclient!hw;")
	private var cylinder: Silhouette? = null

	@OriginalMember(owner = "client!ge", name = "cb", descriptor = "S")
	private val id: Short

	@OriginalMember(owner = "client!ge", name = "M", descriptor = "Z")
	private val underwater: Boolean

	@OriginalMember(owner = "client!ge", name = "lb", descriptor = "Z")
	private var lb: Boolean

	@OriginalMember(owner = "client!ge", name = "X", descriptor = "Z")
	private val interactive: Boolean

	@OriginalMember(owner = "client!ge", name = "ab", descriptor = "B")
	private var shape: Byte

	@OriginalMember(owner = "client!ge", name = "I", descriptor = "B")
	private val rotation: Byte

	@OriginalMember(owner = "client!ge", name = "K", descriptor = "Z")
	private val hardShadow: Boolean

	@OriginalMember(owner = "client!ge", name = "pb", descriptor = "Lclient!r;")
	private var shadow: Shadow? = null

	@OriginalMember(owner = "client!ge", name = "S", descriptor = "Lclient!ka;")
	@JvmField
	var model: Model? = null

	@OriginalMember(owner = "client!ge", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIIIZIIZ)V")
	constructor(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) locType: LocType?, @OriginalArg(2) level: Int, @OriginalArg(3) virtualLevel: Int, @OriginalArg(4) x: Int, @OriginalArg(5) y: Int, @OriginalArg(6) z: Int, @OriginalArg(7) underwater: Boolean, @OriginalArg(8) shape: Int, @OriginalArg(9) rotation: Int, @OriginalArg(10) copyNormals: Boolean) : super(x, y, z, level, virtualLevel, LocShapes.getWallSideMask(rotation, shape)) {
		this.id = locType!!.id.toShort()
		this.underwater = underwater
		this.lb = copyNormals
		this.x = x
		this.interactive = locType.hasActions != LocInteractivity.NONINTERACTIVE && !underwater
		this.z = z
		this.shape = shape.toByte()
		this.rotation = rotation.toByte()
		this.hardShadow = toolkit!!.hardShadow() && locType.dynamicShadow && !this.underwater && GamePreferences.uid!!.hardShadows!!.getValue() != 0
		@Pc(69) var functionMask: Int = 2048
		if (this.lb) {
			functionMask = functionMask or 0x10000
		}
		@Pc(84) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(this.hardShadow, functionMask, toolkit)
		if (modelAndShadow != null) {
			this.shadow = modelAndShadow.shadow
			this.model = modelAndShadow.model
			if (this.lb) {
				this.model = this.model!!.copy(0.toByte(), functionMask, false)
				return
			}
		}
	}

	@OriginalMember(owner = "client!ge", name = "b", descriptor = "(I)I")
	override fun getShape(): Int {
		return this.shape.toInt()
	}

	@OriginalMember(owner = "client!ge", name = "c", descriptor = "(I)V")
	override fun upload() {
		if (this.model != null) {
			this.model!!.uploadToGpu()
		}
	}

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(Lclient!ha;III)Z")
	override fun picked(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) mouseY: Int, @OriginalArg(2) mouseX: Int): Boolean {
		@Pc(9) val model: Model? = this.getModel(toolkit, 131072)
		if (model == null) {
			return false
		} else {
			@Pc(20) val matrix: Matrix = toolkit!!.scratchMatrix()
			matrix.applyTranslation(this.x, this.y, this.z)
			return if (OrthoMode.orthographicProjection) model.pickedOrtho(mouseX, mouseY, matrix, false, 0, HintArrow.scaledOrthoZoom) else model.picked(mouseX, mouseY, matrix, false, 0)
		}
	}

	@OriginalMember(owner = "client!ge", name = "c", descriptor = "(Lclient!ha;I)V")
	override fun particleCycle(@OriginalArg(0) toolkit: Toolkit?) {
	}

	@OriginalMember(owner = "client!ge", name = "d", descriptor = "(I)I")
	override fun getRotation(): Int {
		return this.rotation.toInt()
	}

	@OriginalMember(owner = "client!ge", name = "b", descriptor = "(Z)V")
	override fun removeTileShadow() {
		this.lb = false
		if (this.model != null) {
			this.model!!.s(this.model!!.ua() and 0xFFFEFFFF.toInt())
		}
	}

	@OriginalMember(owner = "client!ge", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	override fun getCylinder(@OriginalArg(0) toolkit: Toolkit?): Silhouette? {
		if (this.cylinder == null) {
			this.cylinder = Silhouette.fromModel(this.x, this.getModel(toolkit, 0), this.z, this.y)
		}
		return this.cylinder
	}

	@OriginalMember(owner = "client!ge", name = "e", descriptor = "(I)I")
	override fun getId(): Int {
		return this.id.toInt() and 0xFFFF
	}

	@OriginalMember(owner = "client!ge", name = "i", descriptor = "(I)I")
	override fun getMinY(@OriginalArg(0) check: Int): Int {
		if (check != -25675) {
			this.shape = 53
		}
		return if (this.model == null) 0 else this.model!!.ma()
	}

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(I)Z")
	override fun hardShadow(): Boolean {
		return this.hardShadow
	}

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	override fun shareLight(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) offsetY: Int, @OriginalArg(2) hideSharedFaces: Boolean, @OriginalArg(3) entity: Entity?, @OriginalArg(4) offsetZ: Int, @OriginalArg(6) offsetX: Int) {
		if (entity is StaticWall) {
			@Pc(12) val staticWall: StaticWall = entity
			if (this.model != null && staticWall.model != null) {
				this.model!!.shareLight(staticWall.model, offsetX, offsetY, offsetZ, hideSharedFaces)
			}
		} else if (entity is StaticLocation) {
			@Pc(35) val staticLocation: StaticLocation = entity
			if (this.model != null && staticLocation.model != null) {
				this.model!!.shareLight(staticLocation.model, offsetX, offsetY, offsetZ, hideSharedFaces)
			}
		}
	}

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(ILclient!ha;)V")
	override fun addShadow(@OriginalArg(1) toolkit: Toolkit?) {
		@Pc(25) val shadow: Shadow?
		if (this.shadow == null && this.hardShadow) {
			@Pc(18) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(true, 262144, toolkit)
			shadow = if (modelAndShadow == null) null else modelAndShadow.shadow
		} else {
			shadow = this.shadow
			this.shadow = null
		}
		if (shadow != null) {
			Shadow.renderShadows(shadow, this.virtualLevel.toInt(), this.x, this.z, null)
		}
	}

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(ZIILclient!ha;)Lclient!ak;")
	private fun getModelAndShadow(@OriginalArg(0) withShadow: Boolean, @OriginalArg(1) functionMask: Int, @OriginalArg(3) toolkit: Toolkit?): ModelAndShadow? {
		@Pc(19) val locType: LocType? = TypeLists.LOC!!.list(this.id.toInt() and 0xFFFF)
		@Pc(27) val ground: Ground?
		@Pc(40) val ground2: Ground?
		if (this.underwater) {
			ground2 = Scene.aClass65Array2!![0]
			ground = Scene.aClass65Array3!![this.virtualLevel.toInt()]
		} else {
			ground = Scene.aClass65Array2!![this.virtualLevel.toInt()]
			if (this.virtualLevel < 3) {
				ground2 = Scene.aClass65Array2!![this.virtualLevel + 1]
			} else {
				ground2 = null
			}
		}
		return locType!!.getModelAndShadow(toolkit, ground2, this.shape.toInt(), functionMask, this.x, withShadow, ground, this.y, this.z, this.rotation.toInt())
	}

	@OriginalMember(owner = "client!ge", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	override fun render(@OriginalArg(1) toolkit: Toolkit?): PickableEntity? {
		if (this.model == null) {
			return null
		}
		@Pc(11) val matrix: Matrix = toolkit!!.scratchMatrix()
		matrix.applyTranslation(this.x, this.y, this.z)
		@Pc(25) val entity: PickableEntity = PickableEntity.obtain(1, this.interactive)
		if (OrthoMode.orthographicProjection) {
			this.model!!.renderOrtho(matrix, entity.boundingBoxPolygons!![0], HintArrow.scaledOrthoZoom, 0)
		} else {
			this.model!!.render(matrix, entity.boundingBoxPolygons!![0], 0)
		}
		return entity
	}

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(Lclient!ha;BI)Lclient!ka;")
	private fun getModel(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(2) functionMask: Int): Model? {
		if (this.model != null && toolkit!!.compareFunctionMasks(this.model!!.ua(), functionMask) == 0) {
			return this.model
		} else {
			@Pc(33) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(false, functionMask, toolkit)
			return if (modelAndShadow == null) null else modelAndShadow.model
		}
	}

	@OriginalMember(owner = "client!ge", name = "c", descriptor = "(Z)I")
	override fun getModelMinY(): Int {
		return if (this.model == null) 0 else this.model!!.fa()
	}

	@OriginalMember(owner = "client!ge", name = "j", descriptor = "(I)Z")
	override fun hasTileShadow(): Boolean {
		return this.lb
	}

	@OriginalMember(owner = "client!ge", name = "b", descriptor = "(B)Z")
	override fun hasAlpha(): Boolean {
		return if (this.model == null) false else this.model!!.F()
	}

	@OriginalMember(owner = "client!ge", name = "g", descriptor = "(I)Z")
	override fun isStationary(): Boolean {
		if (this.model == null) {
			return true
		} else {
			return !this.model!!.r()
		}
	}

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(Lclient!ha;I)V")
	override fun removeShadow(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(31) val shadow: Shadow?
		if (this.shadow == null && this.hardShadow) {
			@Pc(24) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(true, 262144, toolkit)
			shadow = if (modelAndShadow == null) null else modelAndShadow.shadow
		} else {
			shadow = this.shadow
			this.shadow = null
		}
		if (shadow != null) {
			Scene.removeShadow(shadow, this.virtualLevel.toInt(), this.x, this.z, null)
		}
	}
}
