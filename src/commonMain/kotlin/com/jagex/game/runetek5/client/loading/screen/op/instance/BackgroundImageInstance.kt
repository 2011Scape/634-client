package com.jagex.game.runetek5.client.loading.screen.op.instance

import com.jagex.game.runetek5.TipRenderDetails
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.gametip.ITipDetails
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!nfa")
class BackgroundImageInstance @OriginalMember(owner = "client!nfa", name = "<init>", descriptor = "(I)V") constructor(@OriginalArg(0) image: Int) : ITipDetails {

	@OriginalMember(owner = "client!nfa", name = "e", descriptor = "I")
	@JvmField
	val image: Int

	init {
		this.image = image
	}

	@OriginalMember(owner = "client!nfa", name = "a", descriptor = "(B)Lclient!ad;")
	override fun getRenderDetails(): TipRenderDetails? {
		return TipRenderDetails.aClass6_6
	}

	companion object {
		@OriginalMember(owner = "client!hm", name = "a", descriptor = "(Lclient!cea;Z)Lclient!nfa;")
		@JvmStatic
		fun decode(@OriginalArg(0) packet: Packet?): BackgroundImageInstance? {
			@Pc(7) val image: Int = packet!!.readUShort()
			return BackgroundImageInstance(image)
		}
	}
}
