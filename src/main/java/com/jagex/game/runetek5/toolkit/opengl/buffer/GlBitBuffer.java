package com.jagex.game.runetek5.toolkit.opengl.buffer;

import com.jagex.game.runetek5.buffer.Packet;
import jaclib.memory.Stream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!gh")
public final class GlBitBuffer extends Packet {

	@OriginalMember(owner = "client!gh", name = "<init>", descriptor = "(I)V")
	public GlBitBuffer(@OriginalArg(0) int size) {
		super(size);
	}

	@OriginalMember(owner = "client!gh", name = "a", descriptor = "(IF)V")
	public void writeFloatLittleEndian(@OriginalArg(1) float value) {
		@Pc(6) int rawIntBits = Stream.floatToRawIntBits(value);
		super.buffer[super.currentPosition++] = (byte) rawIntBits;
		super.buffer[super.currentPosition++] = (byte) (rawIntBits >> 8);
		super.buffer[super.currentPosition++] = (byte) (rawIntBits >> 16);
		super.buffer[super.currentPosition++] = (byte) (rawIntBits >> 24);
	}

	@OriginalMember(owner = "client!gh", name = "a", descriptor = "(FB)V")
	public void writeFloatBigEndian(@OriginalArg(0) float value) {
		@Pc(6) int rawIntBits = Stream.floatToRawIntBits(value);
		super.buffer[super.currentPosition++] = (byte) (rawIntBits >> 24);
		super.buffer[super.currentPosition++] = (byte) (rawIntBits >> 16);
		super.buffer[super.currentPosition++] = (byte) (rawIntBits >> 8);
		super.buffer[super.currentPosition++] = (byte) rawIntBits;
	}
}
