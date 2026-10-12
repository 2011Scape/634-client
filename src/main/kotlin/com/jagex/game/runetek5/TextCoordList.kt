package com.jagex.game.runetek5

import com.jagex.game.runetek5.scene.tile.TileMessage
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ClientState

object TextCoordList {

	@OriginalMember(owner = "client!go", name = "a", descriptor = "(IIILjava/lang/String;IIII)V")
	@JvmStatic
	fun offer(@OriginalArg(0) cycleDuration: Int, @OriginalArg(1) absoluteX: Int, @OriginalArg(2) minimenuColor: Int, @OriginalArg(3) string: String?, @OriginalArg(4) tileHeight: Int, @OriginalArg(6) absoluteY: Int, @OriginalArg(7) plane: Int) {
		@Pc(7) val tileMessage: TileMessage = TileMessage()
		tileMessage.absoluteX = absoluteX
		tileMessage.plane = plane
		tileMessage.absoluteY = absoluteY
		tileMessage.lastCycle = cycleDuration + ClientState.clientCycle
		tileMessage.minimenuText = string
		tileMessage.minimenuColor = minimenuColor
		tileMessage.tileHeight = tileHeight
		TileMessage.aClass325_8.offer(tileMessage)
	}
}
