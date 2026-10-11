package com.jagex.game.runetek5.graphics.shader;

import com.jagex.game.runetek5.network.packet.ServerProt;
import org.openrs2.deob.annotation.OriginalMember;

public final class ShaderVariableType {
	@OriginalMember(owner = "client!pw", name = "jb", descriptor = "Lclient!mu;")
	public static final ServerProt IF_SETVIDEO = new ServerProt(2, 6);

	private ShaderVariableType() {
		/* empty */
	}
}
