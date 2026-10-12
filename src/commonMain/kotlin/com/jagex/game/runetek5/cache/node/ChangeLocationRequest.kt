package com.jagex.game.runetek5.cache.node

import com.jagex.game.runetek5.core.constants.LocLayer
import com.jagex.game.runetek5.entity.gameobject.GameObject
import com.jagex.game.runetek5.scene.Scene
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.jClass

@OriginalClass("client!mk")
class ChangeLocationRequest : Node() {

	@OriginalMember(owner = "client!mk", name = "o", descriptor = "I")
	@JvmField
	var srcRotation: Int = 0

	@OriginalMember(owner = "client!mk", name = "r", descriptor = "I")
	@JvmField
	var dstShape: Int = 0

	@OriginalMember(owner = "client!mk", name = "s", descriptor = "I")
	@JvmField
	var y: Int = 0

	@OriginalMember(owner = "client!mk", name = "v", descriptor = "I")
	@JvmField
	var plane: Int = 0

	@OriginalMember(owner = "client!mk", name = "w", descriptor = "I")
	@JvmField
	var dstRotation: Int = 0

	@OriginalMember(owner = "client!mk", name = "y", descriptor = "I")
	@JvmField
	var srcId: Int = 0

	@OriginalMember(owner = "client!mk", name = "z", descriptor = "I")
	@JvmField
	var srcShape: Int = 0

	@OriginalMember(owner = "client!mk", name = "A", descriptor = "I")
	@JvmField
	var layer: Int = 0

	@OriginalMember(owner = "client!mk", name = "B", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!mk", name = "D", descriptor = "I")
	@JvmField
	var dstId: Int = 0

	@OriginalMember(owner = "client!mk", name = "p", descriptor = "I")
	@JvmField
	var revertDelay: Int = -1

	@OriginalMember(owner = "client!mk", name = "q", descriptor = "I")
	@JvmField
	var spawnDelay: Int = 0

	companion object {
		@OriginalMember(owner = "client!qea", name = "a", descriptor = "(ILclient!mk;)V")
		@JvmStatic
		fun fillSourceLocation(@OriginalArg(1) changeLocationRequest: ChangeLocationRequest?) {
			if (Scene.aClass100ArrayArrayArray3 == null) {
				return
			}
			@Pc(16) var location: GameObject? = null
			if (changeLocationRequest!!.layer == LocLayer.WALL) {
				location = Scene.getWall(changeLocationRequest.plane, changeLocationRequest.x, changeLocationRequest.y) as GameObject?
			}
			if (changeLocationRequest.layer == LocLayer.WALLDECOR) {
				location = Scene.getWallDecoration(changeLocationRequest.plane, changeLocationRequest.x, changeLocationRequest.y) as GameObject?
			}
			if (changeLocationRequest.layer == LocLayer.GROUND) {
				location = Scene.getInteractiveOfType(changeLocationRequest.plane, changeLocationRequest.x, changeLocationRequest.y, GameObject::class.jClass) as GameObject?
			}
			if (changeLocationRequest.layer == LocLayer.GROUNDDECOR) {
				location = Scene.getGroundDecoration(changeLocationRequest.plane, changeLocationRequest.x, changeLocationRequest.y) as GameObject?
			}
			if (location == null) {
				changeLocationRequest.srcRotation = 0
				changeLocationRequest.srcId = -1
				changeLocationRequest.srcShape = 0
			} else {
				changeLocationRequest.srcId = location.getId()
				changeLocationRequest.srcShape = location.getShape()
				changeLocationRequest.srcRotation = location.getRotation()
			}
		}
	}
}
