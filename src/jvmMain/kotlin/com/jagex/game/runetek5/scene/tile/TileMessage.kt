package com.jagex.game.runetek5.scene.tile

import com.jagex.game.runetek5.entity.EntityNode
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.entity.EntityNodeList
import com.jagex.game.runetek5.client.menu.MiniMenu
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.OverlayManager

@OriginalClass("client!kha")
class TileMessage : EntityNode() {

	@OriginalMember(owner = "client!kha", name = "i", descriptor = "I")
	@JvmField
	var minimenuColor: Int = 0

	@OriginalMember(owner = "client!kha", name = "j", descriptor = "Ljava/lang/String;")
	@JvmField
	var minimenuText: String? = null

	@OriginalMember(owner = "client!kha", name = "l", descriptor = "I")
	@JvmField
	var plane: Int = 0

	@OriginalMember(owner = "client!kha", name = "m", descriptor = "I")
	@JvmField
	var absoluteX: Int = 0

	@OriginalMember(owner = "client!kha", name = "n", descriptor = "I")
	@JvmField
	var tileHeight: Int = 0

	@OriginalMember(owner = "client!kha", name = "o", descriptor = "I")
	@JvmField
	var lastCycle: Int = 0

	@OriginalMember(owner = "client!kha", name = "p", descriptor = "I")
	@JvmField
	var absoluteY: Int = 0

	companion object {
		@OriginalMember(owner = "client!sg", name = "fb", descriptor = "Lclient!tca;")
		@JvmField
		val aClass325_8: EntityNodeList = EntityNodeList()

		@OriginalMember(owner = "client!hi", name = "a", descriptor = "(IIIIIII)V")
		@JvmStatic
		fun drawTileMessages(@OriginalArg(1) width: Int, @OriginalArg(3) height: Int, @OriginalArg(4) x: Int, @OriginalArg(5) y: Int) {
			@Pc(6) var tileMessage: TileMessage? = TileMessage.aClass325_8.getFirst() as TileMessage?
			while (tileMessage != null) {
				if (tileMessage.lastCycle <= ClientState.clientCycle) {
					tileMessage.unlinkNode()
				} else {
					OverlayManager.projectToScreen(width shr 1, tileMessage.tileHeight * 2, height shr 1, tileMessage.plane, (tileMessage.absoluteY shl 9) + 256, (tileMessage.absoluteX shl 9) + 256)
					MiniMenu.minimenuHeader!!.renderCentered(x + OverlayManager.projectedScreenPosition[0], tileMessage.minimenuColor or 0xFF000000.toInt(), tileMessage.minimenuText, 0, y + OverlayManager.projectedScreenPosition[1])
				}
				tileMessage = TileMessage.aClass325_8.getNext() as TileMessage?
			}
		}
	}
}
