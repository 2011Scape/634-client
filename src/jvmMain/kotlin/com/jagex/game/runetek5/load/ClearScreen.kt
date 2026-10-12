package com.jagex.game.runetek5.load

import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!pu")
class ClearScreen : DrawableItem {

	@OriginalMember(owner = "client!pu", name = "i", descriptor = "Lclient!jca;")
	private val loadingScreenClearConfig: LoadingScreenClearConfig?

	@OriginalMember(owner = "client!pu", name = "<init>", descriptor = "(Lclient!jca;)V")
	constructor(@OriginalArg(0) loadingScreenClearConfig: LoadingScreenClearConfig?) {
		this.loadingScreenClearConfig = loadingScreenClearConfig
	}

	@OriginalMember(owner = "client!pu", name = "a", descriptor = "(BZ)V")
	override fun execute() {
		ClientState.activeToolkit!!.aa(0, 0, GameShell.screenCanvasWidth, GameShell.screenCanvasHeight, this.loadingScreenClearConfig!!.colour, 0)
	}

	@OriginalMember(owner = "client!pu", name = "a", descriptor = "(I)V")
	override fun init() {
	}

	@OriginalMember(owner = "client!pu", name = "a", descriptor = "(B)Z")
	override fun ready(): Boolean {
		return true
	}
}
