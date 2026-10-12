package com.jagex.game.runetek5

import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.graphics.model.particle.Silhouette
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.InteractiveEntityContainer
import com.jagex.game.runetek5.entity.PickableEntity
import com.jagex.game.runetek5.entity.grounditem.AbstractGroundItem
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.cache.node.ObjStackEntry
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.client.ClientState

@OriginalClass("client!mq")
class ObjStackEntity : AbstractGroundItem {

	@OriginalMember(owner = "client!mq", name = "L", descriptor = "I")
	@JvmField
	var firstId: Int = 0

	@OriginalMember(owner = "client!mq", name = "Q", descriptor = "I")
	@JvmField
	var firstCount: Int = 0

	@OriginalMember(owner = "client!mq", name = "R", descriptor = "I")
	@JvmField
	var thirdCount: Int = 0

	@OriginalMember(owner = "client!mq", name = "V", descriptor = "I")
	@JvmField
	var count: Int = 0

	@OriginalMember(owner = "client!mq", name = "K", descriptor = "I")
	@JvmField
	var thirdId: Int = -1

	@OriginalMember(owner = "client!mq", name = "O", descriptor = "I")
	private var sphereRadius: Int = 0

	@OriginalMember(owner = "client!mq", name = "W", descriptor = "Z")
	private var transparent: Boolean = false

	@OriginalMember(owner = "client!mq", name = "U", descriptor = "I")
	@JvmField
	var secondId: Int = -1

	@OriginalMember(owner = "client!mq", name = "bb", descriptor = "I")
	@JvmField
	var tileHeightOffset: Int = 0

	@OriginalMember(owner = "client!mq", name = "<init>", descriptor = "(IIIII)V")
	constructor(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) level: Int, @OriginalArg(4) virtualLevel: Int) : super(x, y, z, level, virtualLevel)

	@OriginalMember(owner = "client!mq", name = "l", descriptor = "(I)I")
	override fun getPickSizeShift(): Int {
		@Pc(14) val objType: ItemType = TypeLists.OBJ!!.list(this.firstId)
		@Pc(17) var picksizeshift: Int = objType.pickSizeShift
		@Pc(28) var objType2: ItemType
		if (this.secondId != -1) {
			objType2 = TypeLists.OBJ!!.list(this.secondId)
			if (objType2.pickSizeShift > picksizeshift) {
				picksizeshift = objType2.pickSizeShift
			}
		}
		if (this.thirdId != -1) {
			objType2 = TypeLists.OBJ!!.list(this.thirdId)
			if (picksizeshift < objType2.pickSizeShift) {
				picksizeshift = objType2.pickSizeShift
			}
		}
		return picksizeshift
	}

	@OriginalMember(owner = "client!mq", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	override fun getCylinder(@OriginalArg(0) toolkit: Toolkit?): Silhouette? {
		return null
	}

	@OriginalMember(owner = "client!mq", name = "c", descriptor = "(Z)I")
	override fun getModelMinY(): Int {
		return -10
	}

	@OriginalMember(owner = "client!mq", name = "c", descriptor = "(Lclient!ha;I)V")
	override fun particleCycle(@OriginalArg(0) toolkit: Toolkit?) {
	}

	@OriginalMember(owner = "client!mq", name = "a", descriptor = "(Lclient!ha;III)Z")
	override fun picked(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) mouseY: Int, @OriginalArg(2) mouseX: Int): Boolean {
		@Pc(6) val matrix: Matrix? = toolkit!!.scratchMatrix()
		matrix!!.applyTranslation(super.x, super.y - 10, super.z)
		@Pc(24) val objType: ItemType = TypeLists.OBJ!!.list(this.firstId)
		@Pc(37) var model: Model? = objType.getAnimatedModel(null, null, toolkit, 131072, 0, this.firstCount, 0, -1)
		if (model != null && (if (OrthoMode.orthographicProjection) model.pickedOrtho(mouseX, mouseY, matrix, true, objType.pickSizeShift, HintArrow.scaledOrthoZoom) else model.picked(mouseX, mouseY, matrix, true, objType.pickSizeShift))) {
			return true
		}
		@Pc(74) var objType2: ItemType
		if (this.secondId != -1) {
			objType2 = TypeLists.OBJ!!.list(this.secondId)
			model = objType2.getAnimatedModel(null, null, toolkit, 131072, 0, this.count, 0, -1)
			if (model != null && (if (OrthoMode.orthographicProjection) model.pickedOrtho(mouseX, mouseY, matrix, true, objType2.pickSizeShift, HintArrow.scaledOrthoZoom) else model.picked(mouseX, mouseY, matrix, true, objType2.pickSizeShift))) {
				return true
			}
		}
		if (this.thirdId != -1) {
			objType2 = TypeLists.OBJ!!.list(this.thirdId)
			model = objType2.getAnimatedModel(null, null, toolkit, 131072, 0, this.thirdCount, 0, -1)
			if (model != null && (if (OrthoMode.orthographicProjection) model.pickedOrtho(mouseX, mouseY, matrix, true, objType2.pickSizeShift, HintArrow.scaledOrthoZoom) else model.picked(mouseX, mouseY, matrix, true, objType2.pickSizeShift))) {
				return true
			}
		}
		return false
	}

	@OriginalMember(owner = "client!mq", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	override fun render(@OriginalArg(1) toolkit: Toolkit?): PickableEntity? {
		@Pc(25) val positionEntityNode: InteractiveEntityContainer? = Scene.getTileInteractives(super.level.toInt(), super.x shr Scene.tileUnits, super.z shr Scene.tileUnits)
		if (positionEntityNode != null && positionEntityNode.interactiveObject!!.supportsItems) {
			@Pc(36) val supportHeight: Int = positionEntityNode.interactiveObject!!.getModelMinY()
			if (this.tileHeightOffset != supportHeight) {
				super.y -= this.tileHeightOffset
				this.tileHeightOffset = supportHeight
				super.y += supportHeight
			}
		}
		@Pc(59) val matrix: Matrix? = toolkit!!.scratchMatrix()
		matrix!!.makeIdentity()
		if (positionEntityNode == null || !positionEntityNode.interactiveObject!!.supportsItems) {
			@Pc(78) val ground: Ground? = Scene.aClass65Array1!![super.virtualLevel.toInt()]
			@Pc(83) val sampleSize: Int = this.sphereRadius shl 1
			@Pc(90) val nwOffsetX: Int = -sampleSize / 2
			@Pc(95) val nwOffsetZ: Int = -sampleSize / 2
			@Pc(107) val nwHeight: Int = ground!!.averageHeight(super.x + nwOffsetX, super.z + nwOffsetZ)
			@Pc(111) val neOffsetX: Int = sampleSize / 2
			@Pc(116) val neOffsetZ: Int = -sampleSize / 2
			@Pc(129) val neHeight: Int = ground.averageHeight(neOffsetX + super.x, super.z - -neOffsetZ)
			@Pc(134) val swOffsetX: Int = -sampleSize / 2
			@Pc(138) val swOffsetZ: Int = sampleSize / 2
			@Pc(150) val swHeight: Int = ground.averageHeight(super.x + swOffsetX, swOffsetZ + super.z)
			@Pc(154) val seOffsetX: Int = sampleSize / 2
			@Pc(158) val seOffsetZ: Int = sampleSize / 2
			@Pc(170) val seHeight: Int = ground.averageHeight(super.x + seOffsetX, super.z + seOffsetZ)
			@Pc(177) val minNorthHeight: Int = if (nwHeight >= neHeight) neHeight else nwHeight
			@Pc(188) val minSouthHeight: Int = if (seHeight > swHeight) swHeight else seHeight
			@Pc(199) val minEastHeight: Int = if (neHeight < seHeight) neHeight else seHeight
			if (sampleSize != 0) {
				@Pc(215) val tiltX: Int = (Math.atan2((minNorthHeight - minSouthHeight).toDouble(), sampleSize.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
				if (tiltX != 0) {
					matrix.rotateAxisX(tiltX)
				}
			}
			@Pc(231) val minWestHeight: Int = if (nwHeight >= swHeight) swHeight else nwHeight
			@Pc(236) var heightOffset: Int = nwHeight + seHeight
			if (sampleSize != 0) {
				@Pc(251) val tiltZ: Int = (Math.atan2((minWestHeight - minEastHeight).toDouble(), sampleSize.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
				if (tiltZ != 0) {
					matrix.rotateAxisZ(-tiltZ)
				}
			}
			if (heightOffset > swHeight + neHeight) {
				heightOffset = swHeight + neHeight
			}
			heightOffset = (heightOffset shr 1) - super.y
			if (heightOffset != 0) {
				matrix.translate(0, heightOffset, 0)
			}
		}
		matrix.translate(super.x, super.y - 10, super.z)
		@Pc(303) val pickableEntity: PickableEntity = PickableEntity.obtain(3, true)
		this.transparent = false
		this.sphereRadius = 0
		@Pc(333) var model: Model?
		if (this.thirdId != -1) {
			model = TypeLists.OBJ!!.list(this.thirdId).getAnimatedModel(null, null, toolkit, 2048, 0, this.thirdCount, 0, -1)
			if (model != null) {
				if (OrthoMode.orthographicProjection) {
					model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons!![2], HintArrow.scaledOrthoZoom, 0)
				} else {
					model.render(matrix, pickableEntity.boundingBoxPolygons!![2], 0)
				}
				this.transparent = this.transparent or model.F()
				this.sphereRadius = model.ma()
			}
		}
		if (this.secondId != -1) {
			model = TypeLists.OBJ!!.list(this.secondId).getAnimatedModel(null, null, toolkit, 2048, 0, this.count, 0, -1)
			if (model != null) {
				if (OrthoMode.orthographicProjection) {
					model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons!![1], HintArrow.scaledOrthoZoom, 0)
				} else {
					model.render(matrix, pickableEntity.boundingBoxPolygons!![1], 0)
				}
				this.transparent = this.transparent or model.F()
				if (model.ma() > this.sphereRadius) {
					this.sphereRadius = model.ma()
				}
			}
		}
		model = TypeLists.OBJ!!.list(this.firstId).getAnimatedModel(null, null, toolkit, 2048, 0, this.firstCount, 0, -1)
		if (model != null) {
			if (OrthoMode.orthographicProjection) {
				model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons!![0], HintArrow.scaledOrthoZoom, 0)
			} else {
				model.render(matrix, pickableEntity.boundingBoxPolygons!![0], 0)
			}
			this.transparent = this.transparent or model.F()
			if (model.ma() > this.sphereRadius) {
				this.sphereRadius = model.ma()
			}
		}
		return pickableEntity
	}

	@OriginalMember(owner = "client!mq", name = "i", descriptor = "(I)I")
	override fun getMinY(@OriginalArg(0) check: Int): Int {
		if (check != -25675) {
			QuickChatValueType.aClass234_18 = null
		}
		return this.sphereRadius
	}

	@OriginalMember(owner = "client!mq", name = "b", descriptor = "(B)Z")
	override fun hasAlpha(): Boolean {
		return this.transparent
	}

	@OriginalMember(owner = "client!mq", name = "g", descriptor = "(I)Z")
	override fun isStationary(): Boolean {
		return false
	}

	companion object {
		@OriginalMember(owner = "client!rea", name = "a", descriptor = "(IBII)V")
		@JvmStatic
		fun refreshObjStack(@OriginalArg(0) y: Int, @OriginalArg(2) x: Int, @OriginalArg(3) level: Int) {
			@Pc(10) val absoluteX: Int = x + MapRegion.sceneBaseX
			@Pc(14) val absoluteY: Int = MapRegion.sceneBaseY + y
			if (Scene.aClass100ArrayArrayArray3 == null || x < 0 || y < 0 || x >= MapRegion.sceneSizeX || MapRegion.sceneSizeY <= y || GamePreferences.uid!!.animateBackgroundOverride!!.getValue() == 0 && level != PlayerUpdate.localPlayer!!.level.toInt()) {
				return
			}
			@Pc(52) val key: Long = ((absoluteY shl 14) or (level shl 28) or absoluteX).toLong()
			@Pc(58) val objStack: ObjStack? = ObjStack.GROUND_ITEMS.get(key) as ObjStack?
			if (objStack == null) {
				Scene.removeGroundItem(level, x, y)
				return
			}
			@Pc(72) val objStackEntry: ObjStackEntry? = objStack.groundItems.first() as ObjStackEntry?
			if (objStackEntry == null) {
				Scene.removeGroundItem(level, x, y)
				return
			}
			@Pc(86) var objStackEntity: ObjStackEntity? = Scene.removeGroundItem(level, x, y) as ObjStackEntity?
			if (objStackEntity == null) {
				objStackEntity = ObjStackEntity(x shl 9, Scene.aClass65Array1!![level]!!.getHeight(y, x), y shl 9, level, level)
			} else {
				objStackEntity.thirdId = -1
				objStackEntity.secondId = objStackEntity.thirdId
			}
			objStackEntity.firstId = objStackEntry.id
			objStackEntity.firstCount = objStackEntry.count
			label56@ while (true) {
				@Pc(134) val objStackEntry2: ObjStackEntry? = objStack.groundItems.next() as ObjStackEntry?
				if (objStackEntry2 == null) {
					break
				}
				if (objStackEntity.firstId != objStackEntry2.id) {
					objStackEntity.secondId = objStackEntry2.id
					objStackEntity.count = objStackEntry2.count
					while (true) {
						@Pc(159) val objStackEntry3: ObjStackEntry? = objStack.groundItems.next() as ObjStackEntry?
						if (objStackEntry3 == null) {
							break@label56
						}
						if (objStackEntry3.id != objStackEntity.firstId && objStackEntry3.id != objStackEntity.secondId) {
							objStackEntity.thirdId = objStackEntry3.id
							objStackEntity.thirdCount = objStackEntry3.count
						}
					}
				}
			}
			@Pc(207) val height: Int = Scene.tileHeightForAbsolutePosition((x shl 9) + 256, level, (y shl 9) + 256)
			objStackEntity.z = y shl 9
			objStackEntity.level = level.toByte()
			objStackEntity.tileHeightOffset = 0
			objStackEntity.x = x shl 9
			objStackEntity.y = height
			objStackEntity.virtualLevel = level.toByte()
			if (SettingsBits.isBridge(y, x)) {
				objStackEntity.virtualLevel++
			}
			Scene.addGroundItem(level, x, y, height, objStackEntity)
		}
	}
}
