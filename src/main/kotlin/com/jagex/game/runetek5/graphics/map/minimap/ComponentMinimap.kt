package com.jagex.game.runetek5.graphics.map.minimap

import com.jagex.game.runetek5.LocType
import com.jagex.game.runetek5.Minimap
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.mapsprite.MapSpriteType
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.scene.Scene
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object ComponentMinimap {
	@OriginalMember(owner = "client!dk", name = "e", descriptor = "I")
	@JvmField
	var MAP_PLANE: Int = -1

	@OriginalMember(owner = "client!kd", name = "b", descriptor = "Lclient!mi;")
	@JvmField
	var worldMapOverviewSprite: NativeSprite? = null

	@OriginalMember(owner = "client!eh", name = "f", descriptor = "I")
	@JvmField
	var mapAssetLoadingPercentage: Int = 0

	@OriginalMember(owner = "client!uea", name = "h", descriptor = "I")
	@JvmField
	var minimapState: Int = 0

	@OriginalMember(owner = "client!up", name = "a", descriptor = "(ILclient!iv;ILclient!ha;II)V")
	@JvmStatic
	fun drawMapIcon(@OriginalArg(0) x: Int, @OriginalArg(1) locType: LocType?, @OriginalArg(2) y: Int, @OriginalArg(3) toolkit: Toolkit?, @OriginalArg(4) rotation: Int) {
		var rotation: Int = rotation
		@Pc(9) val msiType: MapSpriteType? = TypeLists.renderer!!.list(locType!!.mapIconId)
		if (msiType!!.fileId == -1) {
			return
		}
		if (locType.mapIconRotates) {
			@Pc(23) val combinedRotation: Int = rotation + locType.mapIconRotation
			rotation = combinedRotation and 0x3
		} else {
			rotation = 0
		}
		@Pc(39) val sprite: NativeSprite? = msiType.createRotatedNativeSprite(rotation, locType.mapIconFlipped, toolkit)
		if (sprite == null) {
			return
		}
		@Pc(45) var sizeX: Int = locType.sizeX
		@Pc(48) var sizeY: Int = locType.sizeY
		if ((rotation and 0x1) == 1) {
			sizeX = locType.sizeY
			sizeY = locType.sizeX
		}
		@Pc(70) var width: Int = sprite.scaleWidth()
		@Pc(73) var height: Int = sprite.scaleHeight()
		if (msiType.requiresUpscaling) {
			height = sizeY * 4
			width = sizeX * 4
		}
		if (msiType.backgroundColor == 0) {
			sprite.drawTintedScaled(x, y - (sizeY - 1) * 4, width, height)
		} else {
			sprite.drawCircle(x, y - (sizeY - 1) * 4, width, height, 0, msiType.backgroundColor or 0xFF000000.toInt(), 1)
		}
	}

	@OriginalMember(owner = "client!wl", name = "a", descriptor = "(Lclient!ha;B)V")
	@JvmStatic
	fun updateMinimapLevel(@OriginalArg(0) toolkit: Toolkit?) {
		if (ComponentMinimap.MAP_PLANE != PlayerUpdate.localPlayer!!.level.toInt() && (Scene.aClass100ArrayArrayArray3 != null && Minimap.buildMinimapSprite(PlayerUpdate.localPlayer!!.level.toInt(), toolkit))) {
			ComponentMinimap.MAP_PLANE = PlayerUpdate.localPlayer!!.level.toInt()
		}
	}

	@OriginalMember(owner = "client!gfa", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun resetMinimap() {
		ComponentMinimap.MAP_PLANE = -1
		Minimap.aClass46_22 = null
	}
}
