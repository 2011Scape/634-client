package com.jagex.game.runetek5.load

import com.jagex.game.runetek5.TipRenderDetails
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.gametip.ITipDetails
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!jca")
class LoadingScreenClearConfig : ITipDetails {

	@OriginalMember(owner = "client!jca", name = "f", descriptor = "I")
	@JvmField
	val colour: Int

	@OriginalMember(owner = "client!jca", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) colour: Int) {
		this.colour = colour
	}

	@OriginalMember(owner = "client!jca", name = "a", descriptor = "(B)Lclient!ad;")
	override fun getRenderDetails(): TipRenderDetails? {
		return TipRenderDetails.aClass6_11
	}

	companion object {
		@OriginalMember(owner = "client!pia", name = "a", descriptor = "(BLclient!cea;)Lclient!jca;")
		@JvmStatic
		fun decode(@OriginalArg(1) packet: Packet?): LoadingScreenClearConfig {
			@Pc(7) val colour: Int = packet!!.readInt()
			return LoadingScreenClearConfig(colour)
		}
	}
}
