package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!om")
abstract class ScheduledAudioEvent @OriginalMember(owner = "client!om", name = "<init>", descriptor = "()V") private constructor() : Node() {

	@OriginalMember(owner = "client!om", name = "o", descriptor = "I")
	@JvmField
	var scheduledTime: Int = 0

	@OriginalMember(owner = "client!om", name = "a", descriptor = "(Lclient!sq;)I")
	abstract fun run(@OriginalArg(0) mixer: PcmStreamMixer?): Int

	@OriginalMember(owner = "client!om", name = "a", descriptor = "()V")
	abstract fun onRemove()
}
