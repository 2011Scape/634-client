package com.jagex.game.runetek5;

import java.awt.Rectangle;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.camera.Camera;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!qaa")
public final class InterfaceRectangles {

	@OriginalMember(owner = "client!qaa", name = "b", descriptor = "[Ljava/awt/Rectangle;")
	public static final Rectangle[] INTERFACE_BG_COMPONENTS = new Rectangle[100];

	static {
		for (@Pc(7) int index = 0; index < 100; index++) {
			INTERFACE_BG_COMPONENTS[index] = new Rectangle();
		}
	}
	@OriginalMember(owner = "client!am", name = "a", descriptor = "(IIIII)V")
	public static void inboundsComponentToTop(@OriginalArg(0) int x, @OriginalArg(1) int width, @OriginalArg(2) int y, @OriginalArg(4) int height) {
		for (@Pc(3) int i = 0; i < Camera.componentBackgroundCount; i++) {
			@Pc(9) Rectangle rectangle = InterfaceRectangles.INTERFACE_BG_COMPONENTS[i];
			if (rectangle.width + rectangle.x > x && rectangle.x < width + x && rectangle.y + rectangle.height > y && rectangle.y < y + height) {
				InterfaceRectangles.IS_COMPONENT_ON_TOP[i] = true;
			}
		}
		OrthoMode.markOrthoTilesDirty(x, x + width, y, height + y);
	}

	@OriginalMember(owner = "client!dj", name = "a", descriptor = "(IIZII)V")
	public static void markRectanglesForFlip(@OriginalArg(0) int width, @OriginalArg(1) int y, @OriginalArg(3) int x, @OriginalArg(4) int height) {
		for (@Pc(7) int i = 0; i < Camera.componentBackgroundCount; i++) {
			@Pc(13) Rectangle rectangle = InterfaceRectangles.INTERFACE_BG_COMPONENTS[i];
			if (x < rectangle.width + rectangle.x && width + x > rectangle.x && y < rectangle.height + rectangle.y && height + y > rectangle.y) {
				InterfaceRectangles.RECTANGLE_NEEDS_FLIP[i] = true;
			}
		}
		OrthoMode.markOrthoTilesDirty(x, x + width, y, y + height);
	}
	@OriginalMember(owner = "client!pq", name = "q", descriptor = "[Z")
	public static final boolean[] PREV_COMPONENT_ON_TOP = new boolean[100];

	@OriginalMember(owner = "client!cb", name = "j", descriptor = "[Z")
	public static final boolean[] RECTANGLE_NEEDS_FLIP = new boolean[100];

	@OriginalMember(owner = "client!bp", name = "t", descriptor = "[Ljava/awt/Rectangle;")
	public static final Rectangle[] FLIP_RECTANGLES = new Rectangle[100];

	@OriginalMember(owner = "client!ol", name = "g", descriptor = "[Z")
	public static final boolean[] IS_COMPONENT_ON_TOP = new boolean[100];
	@OriginalMember(owner = "client!eo", name = "a", descriptor = "(I)V")
	public static void invalidateAllRectangles() {
		for (@Pc(15) int i = 0; i < 100; i++) {
			InterfaceRectangles.IS_COMPONENT_ON_TOP[i] = true;
		}
	}
}
