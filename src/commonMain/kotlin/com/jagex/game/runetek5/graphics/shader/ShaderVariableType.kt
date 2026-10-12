package com.jagex.game.runetek5.graphics.shader

import com.jagex.game.runetek5.network.packet.ServerProt
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object ShaderVariableType {
	@OriginalMember(owner = "client!pw", name = "jb", descriptor = "Lclient!mu;")
	@JvmField
	val IF_SETVIDEO: ServerProt = ServerProt(2, 6)
}
