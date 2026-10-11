package com.jagex.game.runetek5.graphics.map.minimap;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.LocType;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.config.mapsprite.MapSpriteType;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.Minimap;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.scene.Scene;

public final class ComponentMinimap {
	@OriginalMember(owner = "client!dk", name = "e", descriptor = "I")
	public static int MAP_PLANE = -1;

	private ComponentMinimap() {
		/* empty */
	}
	@OriginalMember(owner = "client!kd", name = "b", descriptor = "Lclient!mi;")
	public static NativeSprite worldMapOverviewSprite;

	@OriginalMember(owner = "client!eh", name = "f", descriptor = "I")
	public static int mapAssetLoadingPercentage = 0;
	@OriginalMember(owner = "client!uea", name = "h", descriptor = "I")
	public static int minimapState = 0;

	@OriginalMember(owner = "client!up", name = "a", descriptor = "(ILclient!iv;ILclient!ha;II)V")
	public static void drawMapIcon(@OriginalArg(0) int x, @OriginalArg(1) LocType locType, @OriginalArg(2) int y, @OriginalArg(3) Toolkit toolkit, @OriginalArg(4) int rotation) {
		@Pc(9) MapSpriteType msiType = TypeLists.renderer.list(locType.mapIconId);
		if (msiType.fileId == -1) {
			return;
		}
		if (locType.mapIconRotates) {
			@Pc(23) int combinedRotation = rotation + locType.mapIconRotation;
			rotation = combinedRotation & 0x3;
		} else {
			rotation = 0;
		}
		@Pc(39) NativeSprite sprite = msiType.createRotatedNativeSprite(rotation, locType.mapIconFlipped, toolkit);
		if (sprite == null) {
			return;
		}
		@Pc(45) int sizeX = locType.sizeX;
		@Pc(48) int sizeY = locType.sizeY;
		if ((rotation & 0x1) == 1) {
			sizeX = locType.sizeY;
			sizeY = locType.sizeX;
		}
		@Pc(70) int width = sprite.scaleWidth();
		@Pc(73) int height = sprite.scaleHeight();
		if (msiType.requiresUpscaling) {
			height = sizeY * 4;
			width = sizeX * 4;
		}
		if (msiType.backgroundColor == 0) {
			sprite.drawTintedScaled(x, y - (sizeY - 1) * 4, width, height);
		} else {
			sprite.drawCircle(x, y - (sizeY - 1) * 4, width, height, 0, msiType.backgroundColor | 0xFF000000, 1);
		}
	}
	@OriginalMember(owner = "client!wl", name = "a", descriptor = "(Lclient!ha;B)V")
	public static void updateMinimapLevel(@OriginalArg(0) Toolkit toolkit) {
		if (ComponentMinimap.MAP_PLANE != PlayerUpdate.localPlayer.level && (Scene.aClass100ArrayArrayArray3 != null && Minimap.buildMinimapSprite(PlayerUpdate.localPlayer.level, toolkit))) {
			ComponentMinimap.MAP_PLANE = PlayerUpdate.localPlayer.level;
		}
	}
	@OriginalMember(owner = "client!gfa", name = "b", descriptor = "(I)V")
	public static void resetMinimap() {
		ComponentMinimap.MAP_PLANE = -1;
		Minimap.aClass46_22 = null;
	}
}
