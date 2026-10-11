package com.jagex.game.runetek5.toolkit.companion;

import com.jagex.game.runetek5.TextureCombineFunction;
import org.openrs2.deob.annotation.OriginalMember;

public final class TextureCombineMode {
	@OriginalMember(owner = "client!pga", name = "q", descriptor = "Lclient!sia;")
	public static final TextureCombineFunction MODULATE = new TextureCombineFunction();

	private TextureCombineMode() {
		/* empty */
	}
}
