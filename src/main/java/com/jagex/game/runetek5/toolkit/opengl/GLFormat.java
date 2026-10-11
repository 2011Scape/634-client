package com.jagex.game.runetek5.toolkit.opengl;

import com.jagex.game.runetek5.DataType;
import com.jagex.game.runetek5.PixelFormat;
import org.openrs2.deob.annotation.OriginalMember;

public final class GLFormat {
	@OriginalMember(owner = "client!baa", name = "f", descriptor = "Lclient!baa;")
	public static final DataType RGBA = new DataType(1);

	@OriginalMember(owner = "client!mo", name = "g", descriptor = "Lclient!wq;")
	public static final PixelFormat HDR = new PixelFormat(4);

	private GLFormat() {
		/* empty */
	}
}
