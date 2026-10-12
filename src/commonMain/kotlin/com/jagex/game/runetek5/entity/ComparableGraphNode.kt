package com.jagex.game.runetek5.entity

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!we")
interface ComparableGraphNode {

	@OriginalMember(owner = "client!we", name = "a", descriptor = "(B)J")
	fun toLong(): Long

	@OriginalMember(owner = "client!we", name = "a", descriptor = "(ILclient!we;)Z")
	fun matches(@OriginalArg(1) other: ComparableGraphNode?): Boolean
}
