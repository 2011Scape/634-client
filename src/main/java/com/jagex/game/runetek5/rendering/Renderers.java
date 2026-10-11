package com.jagex.game.runetek5.rendering;

import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.core.datastruct.key.Deque;

public final class Renderers {
	@OriginalMember(owner = "client!be", name = "o", descriptor = "Lclient!ha;")
	public static Toolkit GPU_ITEM_RENDERER;

	private Renderers() {
		/* empty */
	}
	@OriginalMember(owner = "client!ta", name = "m", descriptor = "Lclient!da;")
	public static AbstractFontRenderer FONT_RENDERER;
	@OriginalMember(owner = "client!kj", name = "P", descriptor = "Lclient!da;")
	public static AbstractFontRenderer GPU_ITEM_FONT_RENDERER;
	@OriginalMember(owner = "client!uc", name = "S", descriptor = "Lclient!uh;")
	public static final Deque activeToolkit = new Deque();
	@OriginalMember(owner = "client!cea", name = "i", descriptor = "(I)V")
	public static void freeGpuItemRenderer() {
		if (Renderers.GPU_ITEM_RENDERER != null) {
			Renderers.GPU_ITEM_RENDERER.free();
			Renderers.GPU_ITEM_FONT_RENDERER = null;
			Renderers.GPU_ITEM_RENDERER = null;
		}
	}
}
