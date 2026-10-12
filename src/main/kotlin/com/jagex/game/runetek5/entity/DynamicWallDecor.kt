package com.jagex.game.runetek5.entity

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
import com.jagex.game.runetek5.OrthoMode

@OriginalClass("client!pw")
class DynamicWallDecor : WallDecor, GameObject {

	@OriginalMember(owner = "client!pw", name = "O", descriptor = "Lclient!hw;")
	private var cylinder: Silhouette? = null

	@OriginalMember(owner = "client!pw", name = "A", descriptor = "Z")
	private var transparent: Boolean = false

	@OriginalMember(owner = "client!pw", name = "W", descriptor = "Lclient!bv;")
	@JvmField
	val locEntity: LocEntity

	@OriginalMember(owner = "client!pw", name = "U", descriptor = "Z")
	private var interactive: Boolean

	@OriginalMember(owner = "client!pw", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIIIZIIIII)V")
	constructor(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) locType: LocType?, @OriginalArg(2) level: Int, @OriginalArg(3) virtualLevel: Int, @OriginalArg(4) x: Int, @OriginalArg(5) y: Int, @OriginalArg(6) z: Int, @OriginalArg(7) underwater: Boolean, @OriginalArg(8) offsetX: Int, @OriginalArg(9) offsetZ: Int, @OriginalArg(10) shape: Int, @OriginalArg(11) rotation: Int, @OriginalArg(12) animationId: Int) : super(x, y, z, level, virtualLevel, offsetX, offsetZ) {
		this.locEntity = LocEntity(toolkit, locType, shape, rotation, this.level.toInt(), virtualLevel, this, underwater, animationId)
		this.interactive = locType!!.hasActions != LocInteractivity.NONINTERACTIVE && !underwater
	}

	@OriginalMember(owner = "client!pw", name = "a", descriptor = "(Lclient!ha;I)V")
	override fun removeShadow(@OriginalArg(0) toolkit: Toolkit?) {
		this.locEntity.removeShadow(toolkit)
	}

	@OriginalMember(owner = "client!pw", name = "c", descriptor = "(Z)I")
	override fun getModelMinY(): Int {
		return this.locEntity.getModelMinY()
	}

	@OriginalMember(owner = "client!pw", name = "d", descriptor = "(I)I")
	override fun getRotation(): Int {
		return this.locEntity.rotation
	}

	@OriginalMember(owner = "client!pw", name = "a", descriptor = "(I)Z")
	override fun hardShadow(): Boolean {
		return this.locEntity.hardShadow()
	}

	@OriginalMember(owner = "client!pw", name = "a", descriptor = "(ILclient!ha;)V")
	override fun addShadow(@OriginalArg(1) toolkit: Toolkit?) {
		this.locEntity.refreshShadow(toolkit)
	}

	@OriginalMember(owner = "client!pw", name = "e", descriptor = "(I)I")
	override fun getId(): Int {
		return this.locEntity.id
	}

	@OriginalMember(owner = "client!pw", name = "c", descriptor = "(Lclient!ha;I)V")
	override fun particleCycle(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(18) val model: Model? = this.locEntity.getModel(false, true, 262144, toolkit)
		if (model == null) {
			return
		}
		@Pc(25) val tileX: Int = this.x shr 9
		@Pc(30) val tileZ: Int = this.z shr 9
		@Pc(33) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.applyTranslation(this.x, this.y, this.z)
		this.locEntity.animateParticles(tileX, matrix, tileZ, toolkit, tileX, model, false, tileZ)
	}

	@OriginalMember(owner = "client!pw", name = "b", descriptor = "(I)I")
	override fun getShape(): Int {
		return this.locEntity.shape
	}

	@OriginalMember(owner = "client!pw", name = "g", descriptor = "(I)Z")
	override fun isStationary(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!pw", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	override fun render(@OriginalArg(1) toolkit: Toolkit?): PickableEntity? {
		@Pc(14) val model: Model? = this.locEntity.getModel(false, true, 2048, toolkit)
		if (model == null) {
			return null
		}
		@Pc(21) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.applyTranslation(this.x + this.offsetX, this.y, this.z + this.offsetZ)
		@Pc(41) val pickableEntity: PickableEntity = PickableEntity.obtain(1, this.interactive)
		@Pc(46) val tileX: Int = this.x shr 9
		@Pc(51) val tileZ: Int = this.z shr 9
		this.locEntity.animateParticles(tileX, matrix, tileZ, toolkit, tileX, model, true, tileZ)
		if (OrthoMode.orthographicProjection) {
			model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons!![0], HintArrow.scaledOrthoZoom, 0)
		} else {
			model.render(matrix, pickableEntity.boundingBoxPolygons!![0], 0)
		}
		if (this.locEntity.particleSystem != null) {
			@Pc(93) val particleList: ParticleList? = this.locEntity.particleSystem.getList()
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
		return pickableEntity
	}

	@OriginalMember(owner = "client!pw", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	override fun getCylinder(@OriginalArg(0) toolkit: Toolkit?): Silhouette? {
		return this.cylinder
	}

	@OriginalMember(owner = "client!pw", name = "b", descriptor = "(B)Z")
	override fun hasAlpha(): Boolean {
		return this.transparent
	}

	@OriginalMember(owner = "client!pw", name = "a", descriptor = "(Lclient!ha;III)Z")
	override fun picked(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) mouseY: Int, @OriginalArg(2) mouseX: Int): Boolean {
		@Pc(12) val model: Model? = this.locEntity.getModel(false, false, 131072, toolkit)
		if (model == null) {
			return false
		} else {
			@Pc(23) val matrix: Matrix = toolkit!!.scratchMatrix()!!
			matrix.applyTranslation(this.offsetX + this.x, this.y, this.offsetZ + this.z)
			return if (OrthoMode.orthographicProjection) model.pickedOrtho(mouseX, mouseY, matrix, false, 0, HintArrow.scaledOrthoZoom) else model.picked(mouseX, mouseY, matrix, false, 0)
		}
	}

	@OriginalMember(owner = "client!pw", name = "i", descriptor = "(I)I")
	override fun getMinY(@OriginalArg(0) check: Int): Int {
		if (check != -25675) {
			this.interactive = false
		}
		return this.locEntity.getModelRadius(-119)
	}

	@OriginalMember(owner = "client!pw", name = "c", descriptor = "(I)V")
	override fun upload() {
	}
}
