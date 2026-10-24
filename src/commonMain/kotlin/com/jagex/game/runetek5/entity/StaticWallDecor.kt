package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.graphics.model.FaceBuffer
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
import com.jagex.game.runetek5.OrthoMode

@OriginalClass("client!co")
class StaticWallDecor : WallDecor, GameObject {

	@OriginalMember(owner = "client!co", name = "Y", descriptor = "Lclient!hw;")
	private var cylinder: Silhouette? = null

	@OriginalMember(owner = "client!co", name = "bb", descriptor = "Z")
	private val interactive: Boolean

	@OriginalMember(owner = "client!co", name = "qb", descriptor = "B")
	private val shape: Byte

	@OriginalMember(owner = "client!co", name = "ab", descriptor = "S")
	private val id: Short

	@OriginalMember(owner = "client!co", name = "jb", descriptor = "Z")
	private val underwater: Boolean

	@OriginalMember(owner = "client!co", name = "A", descriptor = "B")
	private val rotation: Byte

	@OriginalMember(owner = "client!co", name = "ib", descriptor = "Z")
	private val hardShadow: Boolean

	@OriginalMember(owner = "client!co", name = "V", descriptor = "Lclient!ka;")
	private var model: Model? = null

	@OriginalMember(owner = "client!co", name = "Z", descriptor = "Lclient!r;")
	private var shadow: Shadow? = null

	@OriginalMember(owner = "client!co", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIIIZIIII)V")
	constructor(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) locType: LocType?, @OriginalArg(2) level: Int, @OriginalArg(3) virtualLevel: Int, @OriginalArg(4) x: Int, @OriginalArg(5) y: Int, @OriginalArg(6) z: Int, @OriginalArg(7) underwater: Boolean, @OriginalArg(8) offsetX: Int, @OriginalArg(9) offsetZ: Int, @OriginalArg(10) shape: Int, @OriginalArg(11) rotation: Int) : super(x, y, z, level, virtualLevel, offsetX, offsetZ) {
		this.interactive = locType!!.hasActions != 0 && !underwater
		this.shape = shape.toByte()
		this.id = locType.id.toShort()
		this.z = z
		this.underwater = underwater
		this.x = x
		this.rotation = rotation.toByte()
		this.hardShadow = toolkit!!.hardShadow() && locType.dynamicShadow && !this.underwater && GamePreferences.uid!!.hardShadows!!.getValue() != 0
		@Pc(70) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(this.hardShadow, 2048, toolkit)
		if (modelAndShadow != null) {
			this.model = modelAndShadow.model
			this.shadow = modelAndShadow.shadow
		}
	}

	@OriginalMember(owner = "client!co", name = "a", descriptor = "(IILclient!ha;)Lclient!ka;")
	private fun getModel(@OriginalArg(0) functionMask: Int, @OriginalArg(2) toolkit: Toolkit?): Model? {
		if (this.model != null && toolkit!!.compareFunctionMasks(this.model!!.ua(), functionMask) == 0) {
			return this.model
		} else {
			@Pc(27) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(false, functionMask, toolkit)
			return if (modelAndShadow == null) null else modelAndShadow.model
		}
	}

	@OriginalMember(owner = "client!co", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	override fun render(@OriginalArg(1) toolkit: Toolkit?): PickableEntity? {
		if (this.model == null) {
			return null
		}
		@Pc(11) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.applyTranslation(this.x + this.offsetX, this.y, this.z + this.offsetZ)
		@Pc(33) val entity: PickableEntity = PickableEntity.obtain(1, this.interactive)
		if (OrthoMode.orthographicProjection) {
			this.model!!.renderOrtho(matrix, entity.boundingBoxPolygons!![0], HintArrow.scaledOrthoZoom, 0)
		} else {
			this.model!!.render(matrix, entity.boundingBoxPolygons!![0], 0)
		}
		return entity
	}

	@OriginalMember(owner = "client!co", name = "c", descriptor = "(I)V")
	override fun upload() {
		if (this.model != null) {
			this.model!!.uploadToGpu()
		}
	}

	@OriginalMember(owner = "client!co", name = "b", descriptor = "(B)Z")
	override fun hasAlpha(): Boolean {
		return if (this.model == null) false else this.model!!.F()
	}

	@OriginalMember(owner = "client!co", name = "e", descriptor = "(I)I")
	override fun getId(): Int {
		return this.id.toInt() and 0xFFFF
	}

	@OriginalMember(owner = "client!co", name = "i", descriptor = "(I)I")
	override fun getMinY(@OriginalArg(0) check: Int): Int {
		if (check != -25675) {
			this.model = null
		}
		return if (this.model == null) 0 else this.model!!.ma()
	}

	/** Not part of the original client: see Entity.pickShape; [picked]'s model and placement. */
	override fun pickShape(toolkit: Toolkit?, out: FaceBuffer): Boolean {
		if (toolkit == null || OrthoMode.orthographicProjection) {
			return false
		}
		val model: Model = this.getModel(131072, toolkit) ?: return false
		val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.applyTranslation(this.x, this.y, this.z)
		return model.projectFaces(matrix, out)
	}

	@OriginalMember(owner = "client!co", name = "a", descriptor = "(Lclient!ha;III)Z")
	override fun picked(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) mouseY: Int, @OriginalArg(2) mouseX: Int): Boolean {
		@Pc(9) val model: Model? = this.getModel(131072, toolkit)
		if (model == null) {
			return false
		} else {
			@Pc(14) val matrix: Matrix = toolkit!!.scratchMatrix()!!
			matrix.applyTranslation(this.x, this.y, this.z)
			return if (OrthoMode.orthographicProjection) model.pickedOrtho(mouseX, mouseY, matrix, false, 0, HintArrow.scaledOrthoZoom) else model.picked(mouseX, mouseY, matrix, false, 0)
		}
	}

	@OriginalMember(owner = "client!co", name = "g", descriptor = "(I)Z")
	override fun isStationary(): Boolean {
		if (this.model == null) {
			return true
		} else {
			return !this.model!!.r()
		}
	}

	@OriginalMember(owner = "client!co", name = "b", descriptor = "(I)I")
	override fun getShape(): Int {
		return this.shape.toInt()
	}

	@OriginalMember(owner = "client!co", name = "a", descriptor = "(Lclient!ha;I)V")
	override fun removeShadow(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(20) val shadow: Shadow?
		if (this.shadow == null && this.hardShadow) {
			@Pc(31) val modelAndShadow: ModelAndShadow? = this.getModelAndShadow(true, 262144, toolkit)
			shadow = if (modelAndShadow == null) null else modelAndShadow.shadow
		} else {
			shadow = this.shadow
			this.shadow = null
		}
		if (shadow != null) {
			Scene.removeShadow(shadow, this.virtualLevel.toInt(), this.x, this.z, null)
		}
	}

	@OriginalMember(owner = "client!co", name = "c", descriptor = "(Lclient!ha;I)V")
	override fun particleCycle(@OriginalArg(0) toolkit: Toolkit?) {
	}

	@OriginalMember(owner = "client!co", name = "a", descriptor = "(ILclient!ha;)V")
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

	@OriginalMember(owner = "client!co", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	override fun getCylinder(@OriginalArg(0) toolkit: Toolkit?): Silhouette? {
		if (this.cylinder == null) {
			this.cylinder = Silhouette.fromModel(this.x, this.getModel(0, toolkit), this.z, this.y)
		}
		return this.cylinder
	}

	@OriginalMember(owner = "client!co", name = "a", descriptor = "(BZILclient!ha;)Lclient!ak;")
	private fun getModelAndShadow(@OriginalArg(1) withShadow: Boolean, @OriginalArg(2) functionMask: Int, @OriginalArg(3) toolkit: Toolkit?): ModelAndShadow? {
		@Pc(18) val locType: LocType? = TypeLists.LOC!!.list(this.id.toInt() and 0xFFFF)
		@Pc(26) val ground: Ground?
		@Pc(39) val ground2: Ground?
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
		return locType!!.getModelAndShadow(toolkit, ground2, this.shape.toInt(), functionMask, this.x, withShadow, ground, this.y, this.z, this.rotation.toInt())
	}

	@OriginalMember(owner = "client!co", name = "c", descriptor = "(Z)I")
	override fun getModelMinY(): Int {
		return if (this.model == null) 0 else this.model!!.fa()
	}

	@OriginalMember(owner = "client!co", name = "d", descriptor = "(I)I")
	override fun getRotation(): Int {
		return this.rotation.toInt()
	}

	@OriginalMember(owner = "client!co", name = "a", descriptor = "(I)Z")
	override fun hardShadow(): Boolean {
		return this.hardShadow
	}
}
