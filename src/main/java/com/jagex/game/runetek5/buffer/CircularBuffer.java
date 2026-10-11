package com.jagex.game.runetek5.buffer;

import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

public final class CircularBuffer {
	@OriginalMember(owner = "client!ap", name = "e", descriptor = "[[B")
	public static final byte[][] UNUSED_1 = new byte[1000][];

	@OriginalMember(owner = "client!pi", name = "o", descriptor = "I")
	public static int UNUSED_2_CURSOR_POS = 0;

	@OriginalMember(owner = "client!ns", name = "g", descriptor = "[[B")
	public static final byte[][] LOGIN_PACKET_BUFFERS = new byte[250][];

	@OriginalMember(owner = "client!bp", name = "r", descriptor = "[I")
	public static int[] SLOT_IN_USE;

	@OriginalMember(owner = "client!vn", name = "Q", descriptor = "I")
	public static int UNUSED_1_CURSOR_POS = 0;

	@OriginalMember(owner = "client!mw", name = "I", descriptor = "[[B")
	public static final byte[][] UNUSED_2 = new byte[50][];

	@OriginalMember(owner = "client!wd", name = "L", descriptor = "[I")
	public static int[] STASH_FOR_BUFFER_SIZE;

	@OriginalMember(owner = "client!hr", name = "L", descriptor = "[[[B")
	public static byte[][][] STASH;

	private CircularBuffer() {
		/* empty */
	}
	@OriginalMember(owner = "client!rp", name = "W", descriptor = "I")
	public static int LOGIN_PACKET_CURSOR_POS = 0;
	@OriginalMember(owner = "client!db", name = "a", descriptor = "([I[II)V")
	public static void initialize(@OriginalArg(0) int[] stashForBufferSize, @OriginalArg(1) int[] maxStashCapacities) {
		if (stashForBufferSize == null || maxStashCapacities == null) {
			CircularBuffer.STASH = null;
			CircularBuffer.SLOT_IN_USE = null;
			CircularBuffer.STASH_FOR_BUFFER_SIZE = null;
			return;
		}
		CircularBuffer.STASH_FOR_BUFFER_SIZE = stashForBufferSize;
		CircularBuffer.SLOT_IN_USE = new int[stashForBufferSize.length];
		CircularBuffer.STASH = new byte[stashForBufferSize.length][][];
		for (@Pc(28) int i = 0; i < CircularBuffer.STASH_FOR_BUFFER_SIZE.length; i++) {
			CircularBuffer.STASH[i] = new byte[maxStashCapacities[i]][];
		}
	}
	@OriginalMember(owner = "client!ida", name = "a", descriptor = "(I[B)V")
	public static synchronized void releasePacketBuffer(@OriginalArg(1) byte[] buffer) {
		if (buffer.length == 100 && CircularBuffer.UNUSED_1_CURSOR_POS < 1000) {
			CircularBuffer.UNUSED_1[CircularBuffer.UNUSED_1_CURSOR_POS++] = buffer;
		} else if (buffer.length == 5000 && CircularBuffer.LOGIN_PACKET_CURSOR_POS < 250) {
			CircularBuffer.LOGIN_PACKET_BUFFERS[CircularBuffer.LOGIN_PACKET_CURSOR_POS++] = buffer;
		} else if (buffer.length == 30000 && CircularBuffer.UNUSED_2_CURSOR_POS < 50) {
			CircularBuffer.UNUSED_2[CircularBuffer.UNUSED_2_CURSOR_POS++] = buffer;
		} else if (CircularBuffer.STASH != null) {
			for (@Pc(65) int i = 0; i < CircularBuffer.STASH_FOR_BUFFER_SIZE.length; i++) {
				if (CircularBuffer.STASH_FOR_BUFFER_SIZE[i] == buffer.length && CircularBuffer.STASH[i].length > CircularBuffer.SLOT_IN_USE[i]) {
					CircularBuffer.STASH[i][CircularBuffer.SLOT_IN_USE[i]++] = buffer;
					return;
				}
			}
		}
	}

	@OriginalMember(owner = "client!ida", name = "b", descriptor = "(II)[B")
	public static synchronized byte[] allocatePacketBuffer(@OriginalArg(0) int size) {
		@Pc(21) byte[] buffer;
		if (size == 100 && CircularBuffer.UNUSED_1_CURSOR_POS > 0) {
			buffer = CircularBuffer.UNUSED_1[--CircularBuffer.UNUSED_1_CURSOR_POS];
			CircularBuffer.UNUSED_1[CircularBuffer.UNUSED_1_CURSOR_POS] = null;
			return buffer;
		} else if (size == 5000 && CircularBuffer.LOGIN_PACKET_CURSOR_POS > 0) {
			buffer = CircularBuffer.LOGIN_PACKET_BUFFERS[--CircularBuffer.LOGIN_PACKET_CURSOR_POS];
			CircularBuffer.LOGIN_PACKET_BUFFERS[CircularBuffer.LOGIN_PACKET_CURSOR_POS] = null;
			return buffer;
		} else if (size == 30000 && CircularBuffer.UNUSED_2_CURSOR_POS > 0) {
			buffer = CircularBuffer.UNUSED_2[--CircularBuffer.UNUSED_2_CURSOR_POS];
			CircularBuffer.UNUSED_2[CircularBuffer.UNUSED_2_CURSOR_POS] = null;
			return buffer;
		} else {
			if (CircularBuffer.STASH != null) {
				for (@Pc(79) int i = 0; i < CircularBuffer.STASH_FOR_BUFFER_SIZE.length; i++) {
					if (CircularBuffer.STASH_FOR_BUFFER_SIZE[i] == size && CircularBuffer.SLOT_IN_USE[i] > 0) {
						@Pc(109) byte[] stashedBuffer = CircularBuffer.STASH[i][--CircularBuffer.SLOT_IN_USE[i]];
						CircularBuffer.STASH[i][CircularBuffer.SLOT_IN_USE[i]] = null;
						return stashedBuffer;
					}
				}
			}
			return new byte[size];
		}
	}
}
