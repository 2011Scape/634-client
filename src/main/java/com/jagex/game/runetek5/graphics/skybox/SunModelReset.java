package com.jagex.game.runetek5.graphics.skybox;

import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.sun.Sun;

public final class SunModelReset {

	@OriginalMember(owner = "client!ki", name = "c", descriptor = "()V")
	public static void resetMedia() {
		Sun.sunModel = null;
		Sun.activeSun = null;
	}
	@OriginalMember(owner = "client!vc", name = "b", descriptor = "(Z)V")
	public static void reset() {
		SunModelReset.resetMedia();
	}
}
