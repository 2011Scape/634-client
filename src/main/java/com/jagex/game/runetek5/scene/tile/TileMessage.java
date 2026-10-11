package com.jagex.game.runetek5.scene.tile;

import com.jagex.game.runetek5.entity.EntityNode;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.OverlayManager;

@OriginalClass("client!kha")
public final class TileMessage extends EntityNode {

	@OriginalMember(owner = "client!kha", name = "i", descriptor = "I")
	public int minimenuColor;

	@OriginalMember(owner = "client!kha", name = "j", descriptor = "Ljava/lang/String;")
	public String minimenuText;

	@OriginalMember(owner = "client!kha", name = "l", descriptor = "I")
	public int plane;

	@OriginalMember(owner = "client!kha", name = "m", descriptor = "I")
	public int absoluteX;

	@OriginalMember(owner = "client!kha", name = "n", descriptor = "I")
	public int tileHeight;

	@OriginalMember(owner = "client!kha", name = "o", descriptor = "I")
	public int lastCycle;

	@OriginalMember(owner = "client!kha", name = "p", descriptor = "I")
	public int absoluteY;
	@OriginalMember(owner = "client!sg", name = "fb", descriptor = "Lclient!tca;")
	public static final EntityNodeList aClass325_8 = new EntityNodeList();
	@OriginalMember(owner = "client!hi", name = "a", descriptor = "(IIIIIII)V")
	public static void drawTileMessages(@OriginalArg(1) int width, @OriginalArg(3) int height, @OriginalArg(4) int x, @OriginalArg(5) int y) {
		for (@Pc(6) TileMessage tileMessage = (TileMessage) TileMessage.aClass325_8.getFirst(); tileMessage != null; tileMessage = (TileMessage) TileMessage.aClass325_8.getNext()) {
			if (tileMessage.lastCycle <= ClientState.clientCycle) {
				tileMessage.unlinkNode();
			} else {
				OverlayManager.projectToScreen(width >> 1, tileMessage.tileHeight * 2, height >> 1, tileMessage.plane, (tileMessage.absoluteY << 9) + 256, (tileMessage.absoluteX << 9) + 256);
				MiniMenu.minimenuHeader.renderCentered(x + OverlayManager.projectedScreenPosition[0], tileMessage.minimenuColor | 0xFF000000, tileMessage.minimenuText, 0, y + OverlayManager.projectedScreenPosition[1]);
			}
		}
	}
}
