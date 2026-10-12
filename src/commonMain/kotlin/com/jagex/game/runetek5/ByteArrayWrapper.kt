package com.jagex.game.runetek5

import com.jagex.game.runetek5.config.sequence.SeqTypeList
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.utils.ArrayUtils
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic
import lang.Class
import lang.getDeclaredConstructor

@OriginalClass("client!ah")
abstract class ByteArrayWrapper {

	@OriginalMember(owner = "client!ah", name = "a", descriptor = "(III)[B")
	abstract fun readBlock(@OriginalArg(2) position: Int): ByteArray?

	@OriginalMember(owner = "client!ah", name = "a", descriptor = "(B[B)V")
	abstract fun set(@OriginalArg(1) bytes: ByteArray?)

	@OriginalMember(owner = "client!ah", name = "b", descriptor = "(I)[B")
	abstract fun get(): ByteArray?

	companion object {
		@OriginalMember(owner = "client!bo", name = "a", descriptor = "([BZB)Ljava/lang/Object;")
		@JvmStatic
		fun wrap(@OriginalArg(0) data: ByteArray?): Any? {
			if (data == null) {
				return null
			}
			if (data.size > 136 && !SeqTypeList.forceTween) {
				try {
					@Pc(25) val wrapper: ByteArrayWrapper = lang.forName("com.jagex.game.runetek5.core.io.ByteBufferArrayWrapper").getDeclaredConstructor().newInstance() as ByteArrayWrapper
					wrapper.set(data)
					return wrapper
				} catch (@Pc(32) ex: Throwable) {
					SeqTypeList.forceTween = true
				}
			}
			return data
		}

		@OriginalMember(owner = "client!pn", name = "a", descriptor = "(BLjava/lang/Object;II)[B")
		@JvmStatic
		fun unwrapBlock(@OriginalArg(1) `object`: Any?, @OriginalArg(2) offset: Int): ByteArray? {
			if (`object` == null) {
				return null
			} else if (`object` is ByteArray) {
				@Pc(13) val bytes: ByteArray = `object`
				return Arrays.copyBlock(offset, bytes)
			} else if (`object` is ByteArrayWrapper) {
				@Pc(30) val wrapper: ByteArrayWrapper = `object`
				return wrapper.readBlock(offset)
			} else {
				throw IllegalArgumentException()
			}
		}

		@OriginalMember(owner = "client!gb", name = "a", descriptor = "(ZLjava/lang/Object;I)[B")
		@JvmStatic
		fun unwrap(@OriginalArg(0) copy: Boolean, @OriginalArg(1) `object`: Any?): ByteArray? {
			if (`object` == null) {
				return null
			} else if (`object` is ByteArray) {
				@Pc(13) val bytes: ByteArray = `object`
				return if (copy) ArrayUtils.copyOf(bytes) else bytes
			} else if (`object` is ByteArrayWrapper) {
				@Pc(32) val wrapper: ByteArrayWrapper = `object`
				return wrapper.get()
			} else {
				throw IllegalArgumentException()
			}
		}
	}
}
