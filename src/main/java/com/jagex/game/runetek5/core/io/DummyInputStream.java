package com.jagex.game.runetek5.core.io;

import java.io.InputStream;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.core.util.TimeUtils;

@OriginalClass("client!sfa")
public final class DummyInputStream extends InputStream {

	@OriginalMember(owner = "client!sfa", name = "read", descriptor = "()I")
	@Override
	public int read() {
		TimeUtils.sleep(30000L);
		return -1;
	}
}
