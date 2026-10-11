package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import org.openrs2.deob.annotation.OriginalArg;

public final class W2DebugMode {

	@OriginalMember(owner = "client!ju", name = "J", descriptor = "I")
	public static int w2DebugMode = 0;
	@OriginalMember(owner = "client!qw", name = "a", descriptor = "(I)V")
	public static void setW2DebugMode(@OriginalArg(0) int w2DebugMode) {
		W2DebugMode.sceneDebugMode = w2DebugMode;
	}

	@OriginalMember(owner = "client!bw", name = "a", descriptor = "(Lclient!da;)V")
	public static void setFont(@OriginalArg(0) AbstractFontRenderer font) {
		W2DebugMode.font = font;
	}

	@OriginalMember(owner = "client!jg", name = "C", descriptor = "I")
	public static int sceneDebugMode = 0;

	@OriginalMember(owner = "client!fg", name = "a", descriptor = "Lclient!da;")
	public static AbstractFontRenderer font;
}
