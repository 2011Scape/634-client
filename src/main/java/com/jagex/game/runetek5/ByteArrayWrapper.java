package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.sequence.SeqTypeList;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.utils.ArrayUtils;

@OriginalClass("client!ah")
public abstract class ByteArrayWrapper {

	@OriginalMember(owner = "client!ah", name = "a", descriptor = "(III)[B")
	public abstract byte[] readBlock(@OriginalArg(2) int position);

	@OriginalMember(owner = "client!ah", name = "a", descriptor = "(B[B)V")
	public abstract void set(@OriginalArg(1) byte[] bytes);

	@OriginalMember(owner = "client!ah", name = "b", descriptor = "(I)[B")
	public abstract byte[] get();
	@OriginalMember(owner = "client!bo", name = "a", descriptor = "([BZB)Ljava/lang/Object;")
	public static Object wrap(@OriginalArg(0) byte[] data) {
		if (data == null) {
			return null;
		}
		if (data.length > 136 && !SeqTypeList.forceTween) {
			try {
				@Pc(25) ByteArrayWrapper wrapper = (ByteArrayWrapper) Class.forName("com.jagex.game.runetek5.core.io.ByteBufferArrayWrapper").getDeclaredConstructor().newInstance();
				wrapper.set(data);
				return wrapper;
			} catch (@Pc(32) Throwable ex) {
				SeqTypeList.forceTween = true;
			}
		}
		return data;
	}
	@OriginalMember(owner = "client!pn", name = "a", descriptor = "(BLjava/lang/Object;II)[B")
	public static byte[] unwrapBlock(@OriginalArg(1) Object object, @OriginalArg(2) int offset) {
		if (object == null) {
			return null;
		} else if (object instanceof byte[]) {
			@Pc(13) byte[] bytes = (byte[]) object;
			return Arrays.copyBlock(offset, bytes);
		} else if (object instanceof ByteArrayWrapper) {
			@Pc(30) ByteArrayWrapper wrapper = (ByteArrayWrapper) object;
			return wrapper.readBlock(offset);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@OriginalMember(owner = "client!gb", name = "a", descriptor = "(ZLjava/lang/Object;I)[B")
	public static byte[] unwrap(@OriginalArg(0) boolean copy, @OriginalArg(1) Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof byte[]) {
			@Pc(13) byte[] bytes = (byte[]) object;
			return copy ? ArrayUtils.copyOf(bytes) : bytes;
		} else if (object instanceof ByteArrayWrapper) {
			@Pc(32) ByteArrayWrapper wrapper = (ByteArrayWrapper) object;
			return wrapper.get();
		} else {
			throw new IllegalArgumentException();
		}
	}
}
