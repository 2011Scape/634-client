package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType
import com.jagex.game.runetek5.graphics.model.particle.Silhouette
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.config.sequence.PositionedGfx
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.SoundManager
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.OrthoMode

@OriginalClass("client!lf")
class SpotAnimation : AbstractInteractiveEntity {

	@OriginalMember(owner = "client!lf", name = "eb", descriptor = "Lclient!uj;")
	private var particleSystem: ParticleSystem? = null

	@OriginalMember(owner = "client!lf", name = "Y", descriptor = "I")
	private var frame: Int = 0

	@OriginalMember(owner = "client!lf", name = "cb", descriptor = "I")
	private var frameCycle: Int = 0

	@OriginalMember(owner = "client!lf", name = "bb", descriptor = "Z")
	private var transparent: Boolean = true

	@OriginalMember(owner = "client!lf", name = "ib", descriptor = "Z")
	@JvmField
	var finished: Boolean = false

	@OriginalMember(owner = "client!lf", name = "U", descriptor = "I")
	private var objectMinY: Int = 0

	// Java field "rotation" (hides AbstractInteractiveEntity.rotation); renamed because Kotlin forbids hiding.
	@OriginalMember(owner = "client!lf", name = "X", descriptor = "I")
	private var spotRotation: Int = 0

	@OriginalMember(owner = "client!lf", name = "nb", descriptor = "I")
	private val nextFrame: Int = -1

	@OriginalMember(owner = "client!lf", name = "tb", descriptor = "I")
	private var minY: Int = 0

	@OriginalMember(owner = "client!lf", name = "sb", descriptor = "I")
	private val id: Int

	@OriginalMember(owner = "client!lf", name = "mb", descriptor = "I")
	@JvmField
	val startCycle: Int

	@OriginalMember(owner = "client!lf", name = "V", descriptor = "Lclient!hg;")
	private var seqType: SeqType? = null

	@OriginalMember(owner = "client!lf", name = "<init>", descriptor = "(IIIIIIIIIIIII)V")
	constructor(@OriginalArg(0) id: Int, @OriginalArg(1) delay: Int, @OriginalArg(2) currentCycle: Int, @OriginalArg(3) level: Int, @OriginalArg(4) virtualLevel: Int, @OriginalArg(5) x: Int, @OriginalArg(6) y: Int, @OriginalArg(7) z: Int, @OriginalArg(8) startX: Int, @OriginalArg(9) endX: Int, @OriginalArg(10) startZ: Int, @OriginalArg(11) endZ: Int, @OriginalArg(12) rotation: Int) : super(level, virtualLevel, x, y, z, startX, endX, startZ, endZ, false, 0.toByte()) {
		this.id = id
		this.spotRotation = rotation
		this.startCycle = currentCycle + delay
		@Pc(53) val spotAnimationType: SpotAnimationType? = TypeLists.SPOT_ANIM!!.list(this.id)
		@Pc(56) val sequenceId: Int = spotAnimationType!!.sequenceId
		if (sequenceId == -1) {
			this.finished = true
		} else {
			this.seqType = TypeLists.SEQ!!.list(sequenceId)
			this.finished = false
		}
		if (this.startCycle == currentCycle) {
			SoundManager.playSequenceSound(this, this.frame, this.seqType)
		}
	}

	@OriginalMember(owner = "client!lf", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	override fun render(@OriginalArg(1) toolkit: Toolkit?): PickableEntity? {
		@Pc(23) val model: Model? = this.generateGfxModel(toolkit, this.id, (if (this.spotRotation == 0) 0 else 5) or 0x800)
		if (model == null) {
			return null
		}
		if (this.spotRotation != 0) {
			model.a(this.spotRotation * 2048)
		}
		@Pc(39) val matrix: Matrix = toolkit!!.scratchMatrix()!!
		matrix.applyTranslation(this.x, this.y, this.z)
		this.updateParticles(model, toolkit, matrix)
		@Pc(58) val pickableEntity: PickableEntity = PickableEntity.obtain(1, false)
		if (OrthoMode.orthographicProjection) {
			model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons!![0], HintArrow.scaledOrthoZoom, 0)
		} else {
			model.render(matrix, pickableEntity.boundingBoxPolygons!![0], 0)
		}
		if (this.particleSystem != null) {
			@Pc(86) val particles: ParticleList? = this.particleSystem!!.getList()
			if (OrthoMode.orthographicProjection) {
				toolkit.renderOrtho(particles, HintArrow.scaledOrthoZoom)
			} else {
				toolkit.render(particles)
			}
		}
		this.transparent = model.F()
		this.objectMinY = model.fa()
		this.minY = model.ma()
		return pickableEntity
	}

	@OriginalMember(owner = "client!lf", name = "c", descriptor = "(Z)I")
	override fun getModelMinY(): Int {
		return this.objectMinY
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	override fun shareLight(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) offsetY: Int, @OriginalArg(2) hideSharedFaces: Boolean, @OriginalArg(3) entity: Entity?, @OriginalArg(4) offsetZ: Int, @OriginalArg(6) offsetX: Int) {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!lf", name = "finalize", descriptor = "()V")
	fun finalize() {
		if (this.particleSystem != null) {
			this.particleSystem!!.stopped()
		}
	}

	@OriginalMember(owner = "client!lf", name = "b", descriptor = "(Z)V")
	override fun removeTileShadow() {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!lf", name = "g", descriptor = "(I)Z")
	override fun isStationary(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(Lclient!ha;IIB)Lclient!ka;")
	private fun generateGfxModel(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) currentId: Int, @OriginalArg(2) functionMask: Int): Model? {
		@Pc(13) val spotAnimationType: SpotAnimationType? = TypeLists.SPOT_ANIM!!.list(currentId)
		@Pc(18) val ground: Ground? = Scene.aClass65Array2!![this.level.toInt()]
		@Pc(33) val ground2: Ground? = if (this.virtualLevel < 3) Scene.aClass65Array2!![this.virtualLevel + 1] else null
		return if (this.finished) spotAnimationType!!.getContouredModel(0, -1, this.y, functionMask, toolkit, -1, this.x, TypeLists.SEQ, ground2, this.z, ground) else spotAnimationType!!.getContouredModel(this.frameCycle, this.frame, this.y, functionMask, toolkit, this.nextFrame, this.x, TypeLists.SEQ, ground2, this.z, ground)
	}

	@OriginalMember(owner = "client!lf", name = "e", descriptor = "(Z)V")
	fun stopParticleSystem() {
		if (this.particleSystem != null) {
			this.particleSystem!!.stopped()
		}
	}

	@OriginalMember(owner = "client!lf", name = "b", descriptor = "(B)Z")
	override fun hasAlpha(): Boolean {
		return this.transparent
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(Lclient!ka;Lclient!ha;ILclient!bca;)V")
	private fun updateParticles(@OriginalArg(0) model: Model?, @OriginalArg(1) toolkit: Toolkit?, @OriginalArg(3) matrix: Matrix?) {
		model!!.updateParticlePosition(matrix)
		@Pc(9) val modelParticleEmitters: Array<EmissiveTriangle?>? = model.getEmissiveTriangles()
		@Pc(12) val modelParticleEffectors: Array<EffectiveVertex?>? = model.getEffectiveVertices()
		if ((this.particleSystem == null || this.particleSystem!!.removed) && (modelParticleEmitters != null || modelParticleEffectors != null)) {
			this.particleSystem = ParticleSystem.get(ClientState.clientCycle, true)
		}
		if (this.particleSystem != null) {
			this.particleSystem!!.update(toolkit, ClientState.clientCycle.toLong(), modelParticleEmitters, modelParticleEffectors)
			this.particleSystem!!.updateBounds(this.level.toInt(), this.startX.toInt(), this.endX.toInt(), this.startZ.toInt(), this.endZ.toInt())
		}
	}

	@OriginalMember(owner = "client!lf", name = "i", descriptor = "(I)I")
	override fun getMinY(@OriginalArg(0) check: Int): Int {
		if (check != -25675) {
			this.spotRotation = 73
		}
		return this.minY
	}

	@OriginalMember(owner = "client!lf", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	override fun getCylinder(@OriginalArg(0) toolkit: Toolkit?): Silhouette? {
		return null
	}

	@OriginalMember(owner = "client!lf", name = "j", descriptor = "(I)Z")
	override fun hasTileShadow(): Boolean {
		return false
	}

	@OriginalMember(owner = "client!lf", name = "c", descriptor = "(Lclient!ha;I)V")
	override fun particleCycle(@OriginalArg(0) toolkit: Toolkit?) {
		@Pc(17) val model: Model? = this.generateGfxModel(toolkit, this.id, 0)
		if (model != null) {
			@Pc(22) val matrix: Matrix = toolkit!!.scratchMatrix()!!
			matrix.applyTranslation(this.x, this.y, this.z)
			this.updateParticles(model, toolkit, matrix)
		}
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(Lclient!ha;III)Z")
	override fun picked(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) mouseY: Int, @OriginalArg(2) mouseX: Int): Boolean {
		return false
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(IB)V")
	fun tick(@OriginalArg(0) time: Int) {
		if (this.finished) {
			return
		}
		this.frameCycle += time
		while (this.frameCycle > this.seqType!!.durations!![this.frame]) {
			this.frameCycle -= this.seqType!!.durations!![this.frame]
			this.frame++
			if (this.seqType!!.frames!!.size <= this.frame) {
				this.finished = true
				break
			}
		}
		if (!this.finished) {
			SoundManager.playSequenceSound(this, this.frame, this.seqType)
		}
	}

	companion object {
		@OriginalMember(owner = "client!mo", name = "a", descriptor = "(I)V")
		@JvmStatic
		fun addSpotAnimationsToScene() {
			@Pc(10) var spotAnimationNode: PositionedGfx? = Renderers.activeToolkit.first() as PositionedGfx?
			while (spotAnimationNode != null) {
				@Pc(15) val spotAnimation: SpotAnimation? = spotAnimationNode.objectGfx
				if (spotAnimation!!.finished) {
					spotAnimationNode.unlink()
					spotAnimation.stopParticleSystem()
				} else if (spotAnimation.startCycle <= ClientState.clientCycle) {
					spotAnimation.tick(ClientState.animatingStep)
					if (spotAnimation.finished) {
						spotAnimationNode.unlink()
					} else {
						Scene.addInteractive(spotAnimation, true)
					}
				}
				spotAnimationNode = Renderers.activeToolkit.next() as PositionedGfx?
			}
		}
	}
}
