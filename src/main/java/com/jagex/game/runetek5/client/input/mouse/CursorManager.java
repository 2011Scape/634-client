package com.jagex.game.runetek5.client.input.mouse;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.config.cursor.CursorType;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.preference.GamePreferences;

@OriginalClass("client!pfa")
public final class CursorManager {

	@OriginalMember(owner = "client!pfa", name = "b", descriptor = "Ljava/awt/Component;")
	private Component component;

	@OriginalMember(owner = "client!pfa", name = "a", descriptor = "Ljava/awt/Robot;")
	private final Robot positionManager = new Robot();

	@OriginalMember(owner = "client!pfa", name = "<init>", descriptor = "()V")
	public CursorManager() throws Exception {
	}

	@OriginalMember(owner = "client!pfa", name = "movemouse", descriptor = "(II)V")
	private void movemouse(@OriginalArg(0) int x, @OriginalArg(1) int y) {
		this.positionManager.mouseMove(x, y);
	}

	@OriginalMember(owner = "client!pfa", name = "showcursor", descriptor = "(Ljava/awt/Component;Z)V")
	private void showcursor(@OriginalArg(0) Component component2, @OriginalArg(1) boolean show) {
		if (show) {
			component2 = null;
		} else if (component2 == null) {
			throw new NullPointerException();
		}
		if (component2 == this.component) {
			return;
		}
		if (this.component != null) {
			this.component.setCursor(null);
			this.component = null;
		}
		if (component2 != null) {
			component2.setCursor(component2.getToolkit().createCustomCursor(new BufferedImage(1, 1, 2), new Point(0, 0), null));
			this.component = component2;
		}
	}

	@OriginalMember(owner = "client!pfa", name = "setcustomcursor", descriptor = "(Ljava/awt/Component;[IIILjava/awt/Point;)V")
	private void setcustomcursor(@OriginalArg(0) Component component, @OriginalArg(1) int[] pixels, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) Point point) {
		if (pixels == null) {
			component.setCursor(null);
		} else {
			@Pc(8) BufferedImage bufferedImage = new BufferedImage(width, height, 2);
			bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
			component.setCursor(component.getToolkit().createCustomCursor(bufferedImage, point, null));
		}
	}
	@OriginalMember(owner = "client!mba", name = "A", descriptor = "I")
	public static int defaultCursorId = -1;
	@OriginalMember(owner = "client!iea", name = "j", descriptor = "I")
	public static int defaultAttackCursor = -1;

	@OriginalMember(owner = "client!aga", name = "a", descriptor = "I")
	public static int activeCursorId = -1;
	@OriginalMember(owner = "client!dw", name = "g", descriptor = "I")
	public static int currentCursor = -1;
	@OriginalMember(owner = "client!iba", name = "a", descriptor = "(II)V")
	public static void setCursor(@OriginalArg(1) int cursorId) {
		if (GamePreferences.uid.customCursor.getValue() == 0) {
			cursorId = -1;
		}
		if (CursorManager.currentCursor == cursorId) {
			return;
		}
		if (cursorId != -1) {
			@Pc(27) CursorType cursorType = TypeLists.CURSOR.list(cursorId);
			@Pc(31) SpriteType indexedImage = cursorType.getSprite();
			if (indexedImage == null) {
				cursorId = -1;
			} else {
				GameShell.signLink.setCustomCursor(indexedImage.heightSum(), indexedImage.getPixels(), new Point(cursorType.hotspotPointX, cursorType.hotspotPointY), GameShell.gameCanvas, indexedImage.widthSum());
				CursorManager.currentCursor = cursorId;
			}
		}
		if (cursorId == -1 && CursorManager.currentCursor != -1) {
			GameShell.signLink.setCustomCursor(-1, null, new Point(), GameShell.gameCanvas, -1);
			CursorManager.currentCursor = -1;
		}
	}
}
