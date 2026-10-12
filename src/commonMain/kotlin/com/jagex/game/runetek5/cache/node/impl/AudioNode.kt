package com.jagex.game.runetek5.cache.node.impl

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!ji")
abstract class AudioNode @OriginalMember(owner = "client!ji", name = "<init>", descriptor = "()V") constructor() : Node() {

	@OriginalMember(owner = "client!ji", name = "o", descriptor = "I")
	@JvmField
	var activeVoiceCount: Int = 0
}
