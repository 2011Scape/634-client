package com.jagex.game.runetek5

import com.jagex.game.runetek5.toolkit.companion.NativeStream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!cd")
class GpuModelVertexStream @OriginalMember(owner = "client!cd", name = "<init>", descriptor = "(Z)V") constructor(@OriginalArg(0) dynamic: Boolean) {

	@OriginalMember(owner = "client!cd", name = "c", descriptor = "Lclient!tc;")
	@JvmField
	var buffer: NativeStream? = null

	@OriginalMember(owner = "client!cd", name = "f", descriptor = "Lclient!tc;")
	@JvmField
	var activeBuffer: NativeStream? = null

	@OriginalMember(owner = "client!cd", name = "i", descriptor = "Z")
	@JvmField
	var uploaded: Boolean = false

	@OriginalMember(owner = "client!cd", name = "d", descriptor = "Z")
	@JvmField
	var dynamic: Boolean

	init {
		this.dynamic = dynamic
	}

	@OriginalMember(owner = "client!cd", name = "a", descriptor = "(Z)V")
	fun release() {
		if (this.buffer != null) {
			this.buffer!!.deallocate()
		}
		this.uploaded = false
	}

	@OriginalMember(owner = "client!cd", name = "b", descriptor = "(B)Z")
	fun canReleaseSourceData(): Boolean {
		return this.uploaded && !this.dynamic
	}
}
