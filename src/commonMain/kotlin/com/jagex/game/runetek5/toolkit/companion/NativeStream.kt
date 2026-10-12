package com.jagex.game.runetek5.toolkit.companion

import com.jagex.game.runetek5.config.Identifiable
import jaclib.memory.Buffer
import jaclib.memory.Source
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!tc")
interface NativeStream : Identifiable {

	@OriginalMember(owner = "client!tc", name = "a", descriptor = "(Lclient!jaclib/memory/Source;IIB)Z")
	fun createFromSource(@OriginalArg(0) source: Source?, @OriginalArg(1) stride: Int, @OriginalArg(2) size: Int): Boolean

	@OriginalMember(owner = "client!tc", name = "a", descriptor = "(I)Z")
	fun unlock(): Boolean

	@OriginalMember(owner = "client!tc", name = "a", descriptor = "(ZI)Lclient!jaclib/memory/Buffer;")
	fun lock(): Buffer?

	@OriginalMember(owner = "client!tc", name = "a", descriptor = "(IBI)Z")
	fun create(@OriginalArg(0) size: Int, @OriginalArg(2) stride: Int): Boolean

	@OriginalMember(owner = "client!tc", name = "b", descriptor = "(I)V")
	fun deallocate()
}
