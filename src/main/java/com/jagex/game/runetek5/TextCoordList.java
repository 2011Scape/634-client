package com.jagex.game.runetek5;

import com.jagex.game.runetek5.scene.tile.TileMessage;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

public final class TextCoordList {

	@OriginalMember(owner = "client!go", name = "a", descriptor = "(IIILjava/lang/String;IIII)V")
	public static void offer(@OriginalArg(0) int cycleDuration, @OriginalArg(1) int absoluteX, @OriginalArg(2) int minimenuColor, @OriginalArg(3) String string, @OriginalArg(4) int tileHeight, @OriginalArg(6) int absoluteY, @OriginalArg(7) int plane) {
		@Pc(7) TileMessage tileMessage = new TileMessage();
		tileMessage.absoluteX = absoluteX;
		tileMessage.plane = plane;
		tileMessage.absoluteY = absoluteY;
		tileMessage.lastCycle = cycleDuration + ClientState.clientCycle;
		tileMessage.minimenuText = string;
		tileMessage.minimenuColor = minimenuColor;
		tileMessage.tileHeight = tileHeight;
		TileMessage.aClass325_8.offer(tileMessage);
	}
}
