package com.jagex.game.runetek5.network

import io.IOException
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.core.io.BufferedSocket
import kotlin.jvm.JvmField

@OriginalClass("client!ss")
abstract class Connection {

	@OriginalMember(owner = "client!ss", name = "<init>", descriptor = "()V")
	constructor() {
	}

	@OriginalMember(owner = "client!ss", name = "a", descriptor = "(B)V")
	abstract fun close()

	@OriginalMember(owner = "client!ss", name = "a", descriptor = "(IIB[B)I")
	@Throws(IOException::class)
	abstract fun read(@OriginalArg(0) length: Int, @OriginalArg(1) offset: Int, @OriginalArg(3) buffer: ByteArray?): Int

	@OriginalMember(owner = "client!ss", name = "a", descriptor = "(I)V")
	abstract fun breakConnection()

	@OriginalMember(owner = "client!ss", name = "a", descriptor = "(II)Z")
	@Throws(IOException::class)
	abstract fun hasAvailable(@OriginalArg(0) length: Int): Boolean

	@OriginalMember(owner = "client!ss", name = "a", descriptor = "(III[B)V")
	@Throws(IOException::class)
	abstract fun write(@OriginalArg(2) length: Int, @OriginalArg(3) buffer: ByteArray?)

	companion object {
		@OriginalMember(owner = "client!qr", name = "g", descriptor = "Lclient!re;")
		@JvmField
		var updateConnection: BufferedSocket? = null
		@OriginalMember(owner = "client!jq", name = "Z", descriptor = "Lclient!ss;")
		@JvmField
		var connection: Connection? = null
	}
}
