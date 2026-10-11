package com.jagex.game.runetek5.toolkit.opengl.buffer;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.buffer.ArrayBufferGL;

@OriginalClass("client!nh")
public final class OpenGlArrayDrawBuffer {

	@OriginalMember(owner = "client!nh", name = "a", descriptor = "S")
	public final short type;

	@OriginalMember(owner = "client!nh", name = "c", descriptor = "B")
	public byte offset;

	@OriginalMember(owner = "client!nh", name = "e", descriptor = "B")
	public final byte size;

	@OriginalMember(owner = "client!nh", name = "b", descriptor = "Lclient!hca;")
	public ArrayBufferGL buffer;

	@OriginalMember(owner = "client!nh", name = "<init>", descriptor = "(Lclient!hca;III)V")
	public OpenGlArrayDrawBuffer(@OriginalArg(0) ArrayBufferGL buffer, @OriginalArg(1) int type, @OriginalArg(2) int size, @OriginalArg(3) int offset) {
		this.type = (short) type;
		this.offset = (byte) offset;
		this.size = (byte) size;
		this.buffer = buffer;
	}
}
