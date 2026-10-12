package com.jagex.game.runetek5.scene

import com.jagex.game.runetek5.core.datastruct.key.Deque
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.cache.node.ChangeLocationRequest
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.client.ClientState

object LocAction {
	@OriginalMember(owner = "client!jo", name = "F", descriptor = "Lclient!uh;")
	@JvmField
	var pendingRemovals: Deque? = Deque()

	@OriginalMember(owner = "client!ar", name = "a", descriptor = "(IIIIIIIIBI)V")
	@JvmStatic
	fun queueLocChange(@OriginalArg(0) plane: Int, @OriginalArg(2) y: Int, @OriginalArg(4) shape: Int, @OriginalArg(5) layer: Int, @OriginalArg(6) rotation: Int, @OriginalArg(7) id: Int, @OriginalArg(9) x: Int) {
		@Pc(11) var changeLocationRequest: ChangeLocationRequest? = null
		@Pc(16) var changeLocationRequest2: ChangeLocationRequest? = LocAction.pendingRemovals!!.first() as ChangeLocationRequest?
		while (changeLocationRequest2 != null) {
			if (plane == changeLocationRequest2.plane && changeLocationRequest2.x == x && y == changeLocationRequest2.y && changeLocationRequest2.layer == layer) {
				changeLocationRequest = changeLocationRequest2
				break
			}
			changeLocationRequest2 = LocAction.pendingRemovals!!.next() as ChangeLocationRequest?
		}
		if (changeLocationRequest == null) {
			changeLocationRequest = ChangeLocationRequest()
			changeLocationRequest.plane = plane
			changeLocationRequest.layer = layer
			changeLocationRequest.y = y
			changeLocationRequest.x = x
			if (x >= 0 && y >= 0 && MapRegion.sceneSizeX > x && y < MapRegion.sceneSizeY) {
				ChangeLocationRequest.fillSourceLocation(changeLocationRequest)
			}
			LocAction.pendingRemovals!!.addLast(changeLocationRequest)
		}
		changeLocationRequest.revertDelay = -1
		changeLocationRequest.dstRotation = rotation
		changeLocationRequest.dstShape = shape
		changeLocationRequest.spawnDelay = 0
		changeLocationRequest.dstId = id
	}

	@OriginalMember(owner = "client!fga", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun refreshPendingChanges() {
		@Pc(10) var changeLocationRequest: ChangeLocationRequest? = LocAction.pendingRemovals!!.first() as ChangeLocationRequest?
		while (changeLocationRequest != null) {
			if (changeLocationRequest.revertDelay == -1) {
				changeLocationRequest.spawnDelay = 0
				if (changeLocationRequest.x >= 0 && changeLocationRequest.y >= 0 && MapRegion.sceneSizeX > changeLocationRequest.x && MapRegion.sceneSizeY > changeLocationRequest.y) {
					ChangeLocationRequest.fillSourceLocation(changeLocationRequest)
				}
			} else {
				changeLocationRequest.unlink()
			}
			changeLocationRequest = LocAction.pendingRemovals!!.next() as ChangeLocationRequest?
		}
	}

	@OriginalMember(owner = "client!iea", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun processPendingChanges() {
		@Pc(10) var changeLocationRequest: ChangeLocationRequest? = LocAction.pendingRemovals!!.first() as ChangeLocationRequest?
		while (changeLocationRequest != null) {
			if (changeLocationRequest.revertDelay > 0) {
				changeLocationRequest.revertDelay--
			}
			if (changeLocationRequest.revertDelay != 0) {
				if (changeLocationRequest.spawnDelay > 0) {
					changeLocationRequest.spawnDelay--
				}
				if (changeLocationRequest.spawnDelay == 0 && changeLocationRequest.x >= 1 && changeLocationRequest.y >= 1 && changeLocationRequest.x <= MapRegion.sceneSizeX - 2 && changeLocationRequest.y <= MapRegion.sceneSizeY - 2 && (changeLocationRequest.dstId < 0 || LocType.isModelReady(changeLocationRequest.dstShape, changeLocationRequest.dstId))) {
					LocAction.replaceLoc(changeLocationRequest.x, changeLocationRequest.layer, -1, changeLocationRequest.dstId, changeLocationRequest.dstShape, changeLocationRequest.y, changeLocationRequest.dstRotation, changeLocationRequest.plane)
					changeLocationRequest.spawnDelay = -1
					if (changeLocationRequest.dstId == changeLocationRequest.srcId && changeLocationRequest.srcId == -1) {
						changeLocationRequest.unlink()
					} else if (changeLocationRequest.srcId == changeLocationRequest.dstId && changeLocationRequest.dstRotation == changeLocationRequest.srcRotation && changeLocationRequest.dstShape == changeLocationRequest.srcShape) {
						changeLocationRequest.unlink()
					}
				}
			} else if (changeLocationRequest.srcId < 0 || LocType.isModelReady(changeLocationRequest.srcShape, changeLocationRequest.srcId)) {
				LocAction.replaceLoc(changeLocationRequest.x, changeLocationRequest.layer, -1, changeLocationRequest.srcId, changeLocationRequest.srcShape, changeLocationRequest.y, changeLocationRequest.srcRotation, changeLocationRequest.plane)
				changeLocationRequest.unlink()
			}
			changeLocationRequest = LocAction.pendingRemovals!!.next() as ChangeLocationRequest?
		}
	}

	@OriginalMember(owner = "client!rw", name = "a", descriptor = "(IIIIIIIII)V")
	@JvmStatic
	fun replaceLoc(@OriginalArg(0) x: Int, @OriginalArg(2) layer: Int, @OriginalArg(3) sequenceId: Int, @OriginalArg(4) locId: Int, @OriginalArg(5) shape: Int, @OriginalArg(6) y: Int, @OriginalArg(7) rotation: Int, @OriginalArg(8) level: Int) {
		if (x < 1 || y < 1 || x > MapRegion.sceneSizeX - 2 || y > MapRegion.sceneSizeY - 2) {
			return
		}
		@Pc(29) var plane: Int = level
		if (level < 3 && SettingsBits.isBridge(y, x)) {
			plane = level + 1
		}
		if (GamePreferences.uid!!.animateBackgroundOverride!!.getValue() == 0 && !SettingsBits.isVisibleFromLevel(y, x, MapRegion.mapBuildLevel, plane)) {
			return
		}
		if (Scene.aClass100ArrayArrayArray3 == null) {
			return
		}
		MapRegion.aClass322_Sub1_1!!.removeGameObject(MapRegion.aClass104Array3[level], layer, level, ClientState.activeToolkit, x, y)
		if (locId >= 0) {
			@Pc(77) val previousGroundDecor: Int = GamePreferences.uid!!.groundDecor!!.getValue()
			GamePreferences.uid!!.setValue(GamePreferences.uid!!.groundDecor, 1)
			MapRegion.aClass322_Sub1_1!!.addObject(x, level, locId, ClientState.activeToolkit, plane, shape, y, sequenceId, rotation, MapRegion.aClass104Array3[level])
			GamePreferences.uid!!.setValue(GamePreferences.uid!!.groundDecor, previousGroundDecor)
		}
	}
}
