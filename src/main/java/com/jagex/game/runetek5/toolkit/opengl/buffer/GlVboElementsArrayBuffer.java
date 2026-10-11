package com.jagex.game.runetek5.toolkit.opengl.buffer;

import com.jagex.game.runetek5.toolkit.opengl.buffer.AbstractArbBufferGL;
import com.jagex.game.runetek5.buffer.ElementsBufferGL;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!kaa")
public final class GlVboElementsArrayBuffer extends AbstractArbBufferGL implements ElementsBufferGL {

	@OriginalMember(owner = "client!kaa", name = "w", descriptor = "[C")
	private static final char[] BASE64_CHARS = new char[64];

	@OriginalMember(owner = "client!kaa", name = "o", descriptor = "I")
	private int indexType;

	static {
		for (@Pc(8) int i = 0; i < 26; i++) {
			BASE64_CHARS[i] = (char) (i + 65);
		}
		for (@Pc(24) int i = 26; i < 52; i++) {
			BASE64_CHARS[i] = (char) (i + 71);
		}
		for (@Pc(40) int i = 52; i < 62; i++) {
			BASE64_CHARS[i] = (char) (i - 4);
		}
		BASE64_CHARS[63] = '-';
		BASE64_CHARS[62] = '*';
	}

	@OriginalMember(owner = "client!kaa", name = "<init>", descriptor = "(Lclient!qo;I[BIZ)V")
	public GlVboElementsArrayBuffer(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int indexType, @OriginalArg(2) byte[] data, @OriginalArg(3) int bufferSize, @OriginalArg(4) boolean streamDraw) {
		super(glToolkit, 34963, data, bufferSize, streamDraw);
		this.indexType = indexType;
	}

	@OriginalMember(owner = "client!kaa", name = "d", descriptor = "(I)V")
	@Override
	public void setAsActiveBuffer() {
		super.toolkit.setElementsBuffer(this);
	}

	@OriginalMember(owner = "client!kaa", name = "b", descriptor = "(I)I")
	@Override
	public int getIndexType() {
		return this.indexType;
	}

	@OriginalMember(owner = "client!kaa", name = "a", descriptor = "([BIII)V")
	@Override
	public void putElements(@OriginalArg(0) byte[] data, @OriginalArg(3) int bufferSize) {
		this.put(data, bufferSize);
		this.indexType = 5123;
	}

	@OriginalMember(owner = "client!kaa", name = "a", descriptor = "(I)I")
	@Override
	public int getId() {
		return super.id;
	}

	@OriginalMember(owner = "client!kaa", name = "a", descriptor = "(B)J")
	@Override
	public long getAddress() {
		return 0L;
	}
}
