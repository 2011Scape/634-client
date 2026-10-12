package com.jagex.game.runetek5.core.datastruct

import com.jagex.game.runetek5.graphics.model.particle.ParticleNode
import com.jagex.game.runetek5.utils.Utils
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!ev")
class LoginStateUpdate : ParticleNode {

	@OriginalMember(owner = "client!ev", name = "q", descriptor = "I")
	@JvmField
	val time: Int = (Utils.currentTimeMillis() / 1000L).toInt()

	@OriginalMember(owner = "client!ev", name = "m", descriptor = "Ljava/lang/String;")
	@JvmField
	val name: String?

	@OriginalMember(owner = "client!ev", name = "p", descriptor = "S")
	@JvmField
	val actionType: Short

	@OriginalMember(owner = "client!ev", name = "<init>", descriptor = "(Ljava/lang/String;I)V")
	constructor(@OriginalArg(0) string: String?, @OriginalArg(1) actionType: Int) : super() {
		this.name = string
		this.actionType = actionType.toShort()
	}
}
