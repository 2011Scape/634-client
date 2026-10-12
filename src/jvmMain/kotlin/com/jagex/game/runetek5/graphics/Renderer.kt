package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!fia")
class Renderer {

	@OriginalMember(owner = "client!fia", name = "b", descriptor = "J")
	@JvmField
	val driverVersion: Long

	@OriginalMember(owner = "client!fia", name = "f", descriptor = "I")
	@JvmField
	val version: Int

	@OriginalMember(owner = "client!fia", name = "g", descriptor = "I")
	@JvmField
	val vendor: Int

	@OriginalMember(owner = "client!fia", name = "c", descriptor = "Ljava/lang/String;")
	@JvmField
	val device: String?

	@OriginalMember(owner = "client!fia", name = "a", descriptor = "Ljava/lang/String;")
	@JvmField
	val name: String?

	@OriginalMember(owner = "client!fia", name = "<init>", descriptor = "(ILjava/lang/String;ILjava/lang/String;J)V")
	constructor(@OriginalArg(0) vendor: Int, @OriginalArg(1) string: String?, @OriginalArg(2) version: Int, @OriginalArg(3) string2: String?, @OriginalArg(4) driverVersion: Long) {
		this.driverVersion = driverVersion
		this.version = version
		this.vendor = vendor
		this.device = string2
		this.name = string
	}
}
