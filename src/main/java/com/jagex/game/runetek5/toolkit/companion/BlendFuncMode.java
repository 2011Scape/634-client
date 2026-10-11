package com.jagex.game.runetek5.toolkit.companion;

import com.jagex.game.runetek5.BlendMode;
import org.openrs2.deob.annotation.OriginalMember;

public final class BlendFuncMode {
	@OriginalMember(owner = "client!uw", name = "e", descriptor = "Lclient!pn;")
	public static final BlendMode COLOR_BLENDING = new BlendMode();

	@OriginalMember(owner = "client!sg", name = "S", descriptor = "Lclient!pn;")
	public static final BlendMode OPAQUE = new BlendMode();

	@OriginalMember(owner = "client!fa", name = "z", descriptor = "Lclient!pn;")
	public static final BlendMode ALPHA_BLENDING = new BlendMode();

	@OriginalMember(owner = "client!kea", name = "k", descriptor = "Lclient!pn;")
	public static final BlendMode OPAQUE_BLENDING = new BlendMode();

	private BlendFuncMode() {
		/* empty */
	}
}
