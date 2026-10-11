package com.jagex.game.runetek5.core.datastruct;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.model.particle.ParticleNode;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!ev")
public final class LoginStateUpdate extends ParticleNode {

	@OriginalMember(owner = "client!ev", name = "q", descriptor = "I")
	public final int time = (int) (Utils.currentTimeMillis() / 1000L);

	@OriginalMember(owner = "client!ev", name = "m", descriptor = "Ljava/lang/String;")
	public final String name;

	@OriginalMember(owner = "client!ev", name = "p", descriptor = "S")
	public final short actionType;

	@OriginalMember(owner = "client!ev", name = "<init>", descriptor = "(Ljava/lang/String;I)V")
	public LoginStateUpdate(@OriginalArg(0) String string, @OriginalArg(1) int actionType) {
		this.name = string;
		this.actionType = (short) actionType;
	}
}
