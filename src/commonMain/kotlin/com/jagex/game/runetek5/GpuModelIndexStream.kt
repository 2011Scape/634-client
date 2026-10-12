package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.GpuIndexBuffer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!qq")
class GpuModelIndexStream @OriginalMember(owner = "client!qq", name = "<init>", descriptor = "(Z)V") constructor(@OriginalArg(0) dynamic: Boolean) {

	@OriginalMember(owner = "client!qq", name = "a", descriptor = "Lclient!uq;")
	@JvmField
	var activeBuffer: GpuIndexBuffer? = null

	@OriginalMember(owner = "client!qq", name = "e", descriptor = "Lclient!uq;")
	@JvmField
	var buffer: GpuIndexBuffer? = null

	@OriginalMember(owner = "client!qq", name = "f", descriptor = "Z")
	@JvmField
	var uploaded: Boolean = false

	@OriginalMember(owner = "client!qq", name = "c", descriptor = "Z")
	@JvmField
	var dynamic: Boolean

	init {
		this.dynamic = dynamic
	}

	@OriginalMember(owner = "client!qq", name = "a", descriptor = "(B)V")
	fun release() {
		if (this.buffer != null) {
			this.buffer!!.release()
		}
		this.uploaded = false
	}

	@OriginalMember(owner = "client!qq", name = "b", descriptor = "(B)Z")
	fun canReleaseSourceData(): Boolean {
		return this.uploaded && !this.dynamic
	}
}
