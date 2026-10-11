package com.jagex.game.runetek5.load;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!pu")
public final class ClearScreen implements DrawableItem {

	@OriginalMember(owner = "client!pu", name = "i", descriptor = "Lclient!jca;")
	private final LoadingScreenClearConfig loadingScreenClearConfig;

	@OriginalMember(owner = "client!pu", name = "<init>", descriptor = "(Lclient!jca;)V")
	public ClearScreen(@OriginalArg(0) LoadingScreenClearConfig loadingScreenClearConfig) {
		this.loadingScreenClearConfig = loadingScreenClearConfig;
	}

	@OriginalMember(owner = "client!pu", name = "a", descriptor = "(BZ)V")
	@Override
	public void execute() {
		ClientState.activeToolkit.aa(0, 0, GameShell.screenCanvasWidth, GameShell.screenCanvasHeight, this.loadingScreenClearConfig.colour, 0);
	}

	@OriginalMember(owner = "client!pu", name = "a", descriptor = "(I)V")
	@Override
	public void init() {
	}

	@OriginalMember(owner = "client!pu", name = "a", descriptor = "(B)Z")
	@Override
	public boolean ready() {
		return true;
	}
}
