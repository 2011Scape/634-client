package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.graphics.model.FaceBuffer
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

@OriginalClass("client!uo")
class StaticGroundDecor : GroundDecor, GameObject {

	@OriginalMember(owner = "client!uo", name = "lb", descriptor = "Lclient!hw;")
	private var lb: Silhouette? = null

	@OriginalMember(owner = "client!uo", name = "V", descriptor = "Z")
	private val underwater: Boolean

	@OriginalMember(owner = "client!uo", name = "M", descriptor = "Z")
	private val interactive: Boolean

	@OriginalMember(owner = "client!uo", name = "bb", descriptor = "Z")
	private var copyNormals: Boolean

	@OriginalMember(owner = "client!uo", name = "S", descriptor = "B")
	private val rotation: Byte

	@OriginalMember(owner = "client!uo", name = "db", descriptor = "S")
	private val id: Short

	@OriginalMember(owner = "client!uo", name = "ab", descriptor = "Z")
	private val hardShadow: Boolean

	@OriginalMember(owner = "client!uo", name = "fb", descriptor = "Lclient!ka;")
	private var model: Model? = null

	@OriginalMember(owner = "client!uo", name = "O", descriptor = "Lclient!r;")
	private var shadow: Shadow? = null

	@OriginalMember(owner = "client!uo", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIIIZIZ)V")
	constructor(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) locType: LocType?, @OriginalArg(2) level: Int, @OriginalArg(3) virtualLevel: Int, @OriginalArg(4) x: Int, @OriginalArg(5) y: Int, @OriginalArg(6) z: Int, @OriginalArg(7) underwater: Boolean, @OriginalArg(8) rotation: Int, @OriginalArg(9) copyNormals: Boolean) : super(x, y, z, level, virtualLevel, locType!!.groundDecorationHeight) {
		this.underwater = underwater
		this.interactive = locType!!.hasActions != LocInteractivity.NONINTERACTIVE && !underwater
		this.z = z
		this.copyNormals = copyNormals
		this.x = x
		this.rotation = rotation.toByte()
		this.id = locType.id.toShort()
		this.hardShadow = toolkit!!.hardShadow() && locType.dynamicShadow && !this.underwater && GamePreferences.uid!!.hardShadows!!.getValue() != 0
		@Pc(66) var functionMask: Int = 2048
		if (this.copyNormals) {
			functionMask = functionMask or 0x10000
		}
		@Pc(81) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(toolkit, functionMask, this.hardShadow)
		if (modelAndShadow != null) {
			this.model = modelAndShadow.model
			this.shadow = modelAndShadow.shadow
			if (this.copyNormals) {
				this.model = this.model!!.copy(0.toByte(), functionMask, false)
				return
			}
		}
	}

	@OriginalMember(owner = "client!uo", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	override fun shareLight(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) offsetY: Int, @OriginalArg(2) hideSharedFaces: Boolean, @OriginalArg(3) entity: Entity?, @OriginalArg(4) offsetZ: Int, @OriginalArg(6) offsetX: Int) {
		if (entity !is StaticGroundDecor) {
			return
		}
		@Pc(16) val staticGroundDecor: StaticGroundDecor = entity
		if (this.model != null && staticGroundDecor.model != null) {
			this.model!!.shareLight(staticGroundDecor.model, offsetX, offsetY, offsetZ, hideSharedFaces)
			return
		}
	}

	@OriginalMember(owner = "client!uo", name = "i", descriptor = "(I)I")
	override fun getMinY(@OriginalArg(0) check: Int): Int {
		if (check == -25675) {
			return if (this.model == null) 0 else this.model!!.ma()
		} else {
			return -120
		}
	}

	@OriginalMember(owner = "client!uo", name = "a", descriptor = "(Lclient!ha;I)V")
	override fun removeShadow(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(34) val shadow: Shadow?
		if (this.shadow == null && this.hardShadow) {
			@Pc(27) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(toolkit, 262144, true)
			shadow = if (modelAndShadow == null) null else modelAndShadow.shadow
		} else {
			shadow = this.shadow
			this.shadow = null
		}
		if (shadow != null) {
			Scene.removeShadow(shadow, this.virtualLevel.toInt(), this.x, this.z, null)
		}
	}

	@OriginalMember(owner = "client!uo", name = "a", descriptor = "(Lclient!ha;IZI)Lclient!ak;")
	private fun getModelAndShadow(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) functionMask: Int, @OriginalArg(2) withShadow: Boolean): ModelAndShadow? {
		@Pc(22) val locType: LocType? = TypeLists.LOC!!.list(this.id.toInt() and 0xFFFF)
		@Pc(30) val ground: Ground?
		@Pc(34) val ground2: Ground?
		if (this.underwater) {
			ground = Scene.aClass65Array3!![this.virtualLevel.toInt()]
			ground2 = Scene.aClass65Array2!![0]
		} else {
			ground = Scene.aClass65Array2!![this.virtualLevel.toInt()]
			if (this.virtualLevel < 3) {
				ground2 = Scene.aClass65Array2!![this.virtualLevel + 1]
			} else {
				ground2 = null
			}
		}
		return locType!!.getModelAndShadow(toolkit, ground2, 22, functionMask, this.x, withShadow, ground, this.y, this.z, this.rotation.toInt())
	}

	@OriginalMember(owner = "client!uo", name = "b", descriptor = "(Z)V")
	override fun removeTileShadow() {
		this.copyNormals = false
		if (this.model != null) {
			this.model!!.s(this.model!!.ua() and 0xFFFEFFFF.toInt())
		}
	}

	@OriginalMember(owner = "client!uo", name = "e", descriptor = "(I)I")
	override fun getId(): Int {
		return this.id.toInt() and 0xFFFF
	}

	@OriginalMember(owner = "client!uo", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
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

	@OriginalMember(owner = "client!uo", name = "a", descriptor = "(I)Z")
	override fun hardShadow(): Boolean {
		return this.hardShadow
	}

	@OriginalMember(owner = "client!uo", name = "j", descriptor = "(I)Z")
	override fun hasTileShadow(): Boolean {
		return this.copyNormals
	}

	@OriginalMember(owner = "client!uo", name = "g", descriptor = "(I)Z")
	override fun isStationary(): Boolean {
		if (this.model == null) {
			return true
		} else {
			return !this.model!!.r()
		}
	}

	@OriginalMember(owner = "client!uo", name = "c", descriptor = "(Z)I")
	override fun getModelMinY(): Int {
		return if (this.model == null) 0 else this.model!!.fa()
	}

	@OriginalMember(owner = "client!uo", name = "d", descriptor = "(I)I")
	override fun getRotation(): Int {
		return this.rotation.toInt()
	}

	@OriginalMember(owner = "client!uo", name = "c", descriptor = "(I)V")
	override fun upload() {
		if (this.model != null) {
			this.model!!.uploadToGpu()
		}
	}

	@OriginalMember(owner = "client!uo", name = "b", descriptor = "(B)Z")
	override fun hasAlpha(): Boolean {
		return if (this.model == null) false else this.model!!.F()
	}

	@OriginalMember(owner = "client!uo", name = "a", descriptor = "(ILclient!ha;)V")
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

	/** Not part of the original client: see Entity.pickShape; [picked]'s model and placement. */
	override fun pickShape(toolkit: Toolkit?, out: FaceBuffer): Boolean {
		if (toolkit == null || OrthoMode.orthographicProjection) {
			return false
		}
		val model: Model = this.getModel(toolkit, 131072) ?: return false
		val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.applyTranslation(this.x, this.y, this.z)
		return model.projectFaces(matrix, out)
	}

	@OriginalMember(owner = "client!uo", name = "a", descriptor = "(Lclient!ha;III)Z")
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

	@OriginalMember(owner = "client!uo", name = "a", descriptor = "(Lclient!ha;II)Lclient!ka;")
	private fun getModel(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(2) functionMask: Int): Model? {
		if (this.model != null && toolkit!!.compareFunctionMasks(this.model!!.ua(), functionMask) == 0) {
			return this.model
		} else {
			@Pc(31) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(toolkit, functionMask, false)
			return if (modelAndShadow == null) null else modelAndShadow.model
		}
	}

	@OriginalMember(owner = "client!uo", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	override fun getCylinder(@OriginalArg(0) toolkit: Toolkit?): Silhouette? {
		if (this.lb == null) {
			this.lb = Silhouette.fromModel(this.x, this.getModel(toolkit, 0), this.z, this.y)
		}
		return this.lb
	}

	@OriginalMember(owner = "client!uo", name = "c", descriptor = "(Lclient!ha;I)V")
	override fun particleCycle(@OriginalArg(0) toolkit: Toolkit?) {
	}

	@OriginalMember(owner = "client!uo", name = "b", descriptor = "(I)I")
	override fun getShape(): Int {
		return LocShapes.GROUNDDECOR
	}
}
