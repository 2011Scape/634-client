package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import awt.Component

@OriginalClass("client!ee")
interface PcmDevice {

	@OriginalMember(owner = "client!ee", name = "a", descriptor = "(IB)V")
	fun close(@OriginalArg(0) index: Int)

	@OriginalMember(owner = "client!ee", name = "a", descriptor = "(BI)I")
	fun position(@OriginalArg(1) index: Int): Int

	@OriginalMember(owner = "client!ee", name = "a", descriptor = "(I[I)V")
	fun write(@OriginalArg(0) index: Int, @OriginalArg(1) samples: IntArray?)

	@OriginalMember(owner = "client!ee", name = "b", descriptor = "(BI)V")
	fun discardBuffer(@OriginalArg(1) index: Int)

	@OriginalMember(owner = "client!ee", name = "a", descriptor = "(IZLjava/awt/Component;I)V")
	@Throws(Exception::class)
	fun init(@OriginalArg(0) sampleRate: Int, @OriginalArg(1) stereo: Boolean, @OriginalArg(2) component: Component?)

	@OriginalMember(owner = "client!ee", name = "a", descriptor = "(IIB)V")
	@Throws(Exception::class)
	fun open(@OriginalArg(0) bufferSize: Int, @OriginalArg(1) index: Int)
}
