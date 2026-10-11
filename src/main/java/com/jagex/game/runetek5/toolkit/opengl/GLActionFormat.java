package com.jagex.game.runetek5.toolkit.opengl;

import com.jagex.game.runetek5.DataType;
import com.jagex.game.runetek5.PixelFormat;
import org.openrs2.deob.annotation.OriginalMember;

public final class GLActionFormat {
	@OriginalMember(owner = "client!baa", name = "d", descriptor = "Lclient!baa;")
	public static final DataType DEPTH_COMPONENT_24 = new DataType(2);

	@OriginalMember(owner = "client!fi", name = "L", descriptor = "Lclient!wq;")
	public static final PixelFormat PIXEL_FORMAT = new PixelFormat(1);

	private GLActionFormat() {
		/* empty */
	}
}
