package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.graphics.model.FaceBuffer
import com.jagex.game.runetek5.core.constants.LocShapes
import com.jagex.game.runetek5.entity.gameobject.GameObject
import com.jagex.game.runetek5.config.loctype.LocInteractivity
import com.jagex.game.runetek5.graphics.model.particle.Silhouette
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.LocEntity
import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.OrthoMode
import kotlin.jvm.JvmField

@OriginalClass("client!saa")
class DynamicGroundDecor : GroundDecor, GameObject {

	@OriginalMember(owner = "client!saa", name = "O", descriptor = "Lclient!hw;")
	private var cylinder: Silhouette? = null

	@OriginalMember(owner = "client!saa", name = "J", descriptor = "Z")
	private var transparent: Boolean = false

	@OriginalMember(owner = "client!saa", name = "T", descriptor = "Lclient!bv;")
	@JvmField
	val locEntity: LocEntity

	@OriginalMember(owner = "client!saa", name = "eb", descriptor = "Z")
	private val interactive: Boolean

	@OriginalMember(owner = "client!saa", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIIIZII)V")
	constructor(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) locType: LocType?, @OriginalArg(2) level: Int, @OriginalArg(3) virtualLevel: Int, @OriginalArg(4) x: Int, @OriginalArg(5) y: Int, @OriginalArg(6) z: Int, @OriginalArg(7) underwater: Boolean, @OriginalArg(8) rotation: Int, @OriginalArg(9) animationId: Int) : super(x, y, z, level, virtualLevel, locType!!.groundDecorationHeight) {
		this.locEntity = LocEntity(toolkit, locType, LocShapes.GROUNDDECOR, rotation, level, virtualLevel, this, underwater, animationId)
		this.interactive = locType!!.hasActions != LocInteractivity.NONINTERACTIVE && !underwater
	}

	@OriginalMember(owner = "client!saa", name = "e", descriptor = "(I)I")
	override fun getId(): Int {
		return this.locEntity.id
	}

	@OriginalMember(owner = "client!saa", name = "c", descriptor = "(Lclient!ha;I)V")
	override fun particleCycle(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(18) val model: Model? = this.locEntity.getModel(true, true, 262144, toolkit)
		if (model == null) {
			return
		}
		@Pc(25) val tileX: Int = this.x shr 9
		@Pc(30) val tileZ: Int = this.z shr 9
		@Pc(33) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.applyTranslation(this.x, this.y, this.z)
		this.locEntity.animateParticles(tileX, matrix, tileZ, toolkit, tileX, model, false, tileZ)
	}

	@OriginalMember(owner = "client!saa", name = "c", descriptor = "(Z)I")
	override fun getModelMinY(): Int {
		return this.locEntity.getModelMinY()
	}

	@OriginalMember(owner = "client!saa", name = "c", descriptor = "(I)V")
	override fun upload() {
	}

	@OriginalMember(owner = "client!saa", name = "d", descriptor = "(I)I")
	override fun getRotation(): Int {
		return this.locEntity.rotation
	}

	@OriginalMember(owner = "client!saa", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	override fun render(@OriginalArg(1) toolkit: Toolkit?): PickableEntity? {
		@Pc(14) val model: Model? = this.locEntity.getModel(false, true, 2048, toolkit)
		if (model == null) {
			return null
		}
		@Pc(21) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.applyTranslation(this.x, this.y, this.z)
		@Pc(35) val entity: PickableEntity = PickableEntity.obtain(1, this.interactive)
		@Pc(40) val x1: Int = this.x shr 9
		@Pc(45) val z1: Int = this.z shr 9
		this.locEntity.animateParticles(x1, matrix, z1, toolkit, x1, model, true, z1)
		if (OrthoMode.orthographicProjection) {
			model.renderOrtho(matrix, entity.boundingBoxPolygons!![0], HintArrow.scaledOrthoZoom, 0)
		} else {
			model.render(matrix, entity.boundingBoxPolygons!![0], 0)
		}
		if (this.locEntity.particleSystem != null) {
			@Pc(87) val particleList: ParticleList? = this.locEntity.particleSystem!!.getList()
			if (OrthoMode.orthographicProjection) {
				toolkit.renderOrtho(particleList, HintArrow.scaledOrthoZoom)
			} else {
				toolkit.render(particleList)
			}
		}
		this.transparent = model.F() || this.locEntity.particleSystem != null
		if (this.cylinder == null) {
			this.cylinder = Silhouette.fromModel(this.x, model, this.z, this.y)
		} else {
			Silhouette.updateFromModel(this.z, this.y, this.cylinder, model, this.x)
		}
		return entity
	}

	@OriginalMember(owner = "client!saa", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	override fun shareLight(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) offsetY: Int, @OriginalArg(2) hideSharedFaces: Boolean, @OriginalArg(3) entity: Entity?, @OriginalArg(4) offsetZ: Int, @OriginalArg(6) offsetX: Int) {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!saa", name = "b", descriptor = "(B)Z")
	override fun hasAlpha(): Boolean {
		return this.transparent
	}

	@OriginalMember(owner = "client!saa", name = "i", descriptor = "(I)I")
	override fun getMinY(@OriginalArg(0) check: Int): Int {
		if (check != -25675) {
			StaticMedia.headiconPkSprites = null
		}
		return this.locEntity.getModelRadius(check xor 0x647D)
	}

	@OriginalMember(owner = "client!saa", name = "b", descriptor = "(Z)V")
	override fun removeTileShadow() {
		throw IllegalStateException()
	}

	/** Not part of the original client: see Entity.pickShape; [picked]'s model and placement. */
	override fun pickShape(toolkit: Toolkit?, out: FaceBuffer): Boolean {
		if (toolkit == null || OrthoMode.orthographicProjection) {
			return false
		}
		val model: Model = this.locEntity.getModel(false, false, 131072, toolkit) ?: return false
		val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.applyTranslation(this.x, this.y, this.z)
		return model.projectFaces(matrix, out)
	}

	@OriginalMember(owner = "client!saa", name = "a", descriptor = "(Lclient!ha;III)Z")
	override fun picked(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) mouseY: Int, @OriginalArg(2) mouseX: Int): Boolean {
		@Pc(12) val model: Model? = this.locEntity.getModel(false, false, 131072, toolkit)
		if (model == null) {
			return false
		} else {
			@Pc(19) val matrix: Matrix = toolkit!!.scratchMatrix()!!
			matrix.applyTranslation(this.x, this.y, this.z)
			return if (OrthoMode.orthographicProjection) model.pickedOrtho(mouseX, mouseY, matrix, false, 0, HintArrow.scaledOrthoZoom) else model.picked(mouseX, mouseY, matrix, false, 0)
		}
	}

	@OriginalMember(owner = "client!saa", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	override fun getCylinder(@OriginalArg(0) toolkit: Toolkit?): Silhouette? {
		return this.cylinder
	}

	@OriginalMember(owner = "client!saa", name = "j", descriptor = "(I)Z")
	override fun hasTileShadow(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!saa", name = "a", descriptor = "(I)Z")
	override fun hardShadow(): Boolean {
		return this.locEntity.hardShadow()
	}

	@OriginalMember(owner = "client!saa", name = "a", descriptor = "(Lclient!ha;I)V")
	override fun removeShadow(@OriginalArg(0) toolkit: Toolkit?) {
		this.locEntity.removeShadow(toolkit)
	}

	@OriginalMember(owner = "client!saa", name = "g", descriptor = "(I)Z")
	override fun isStationary(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!saa", name = "b", descriptor = "(I)I")
	override fun getShape(): Int {
		return this.locEntity.shape
	}

	@OriginalMember(owner = "client!saa", name = "a", descriptor = "(ILclient!ha;)V")
	override fun addShadow(@OriginalArg(1) toolkit: Toolkit?) {
		this.locEntity.refreshShadow(toolkit)
	}
}
