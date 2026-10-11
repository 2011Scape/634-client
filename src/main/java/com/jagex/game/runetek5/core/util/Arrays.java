package com.jagex.game.runetek5.core.util;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class Arrays {

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([BI[BII)V")
	public static void copy(@OriginalArg(0) byte[] src, @OriginalArg(1) int srcPos, @OriginalArg(2) byte[] dest, @OriginalArg(3) int destPos, @OriginalArg(4) int length) {
		@Pc(15) int local15;
		if (src == dest) {
			if (srcPos == destPos) {
				return;
			}
			if (destPos > srcPos && destPos < srcPos + length) {
				int srcLimit = length - 1;
				@Pc(19) int srcIndex = srcPos + srcLimit;
				@Pc(23) int destIndex = destPos + srcLimit;
				srcLimit = srcIndex - srcLimit;
				srcLimit += 7;
				while (srcIndex >= srcLimit) {
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
				}
				srcLimit -= 7;
				while (srcIndex >= srcLimit) {
					dest[destIndex--] = src[srcIndex--];
				}
				return;
			}
		}
		int srcEnd = length + srcPos;
		@Pc(115) int unrolledEnd = srcEnd - 7;
		while (srcPos < unrolledEnd) {
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
		}
		srcEnd = unrolledEnd + 7;
		while (srcPos < srcEnd) {
			dest[destPos++] = src[srcPos++];
		}
	}

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([FI[FII)V")
	public static void copy(@OriginalArg(0) float[] src, @OriginalArg(1) int srcPos, @OriginalArg(2) float[] dest, @OriginalArg(3) int destPos, @OriginalArg(4) int length) {
		@Pc(15) int local15;
		if (src == dest) {
			if (srcPos == destPos) {
				return;
			}
			if (destPos > srcPos && destPos < srcPos + length) {
				int srcLimit = length - 1;
				@Pc(19) int srcIndex = srcPos + srcLimit;
				@Pc(23) int destIndex = destPos + srcLimit;
				srcLimit = srcIndex - srcLimit;
				srcLimit += 7;
				while (srcIndex >= srcLimit) {
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
					dest[destIndex--] = src[srcIndex--];
				}
				srcLimit -= 7;
				while (srcIndex >= srcLimit) {
					dest[destIndex--] = src[srcIndex--];
				}
				return;
			}
		}
		int srcEnd = length + srcPos;
		@Pc(115) int unrolledEnd = srcEnd - 7;
		while (srcPos < unrolledEnd) {
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
		}
		srcEnd = unrolledEnd + 7;
		while (srcPos < srcEnd) {
			dest[destPos++] = src[srcPos++];
		}
	}

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([III)V")
	public static void clear(@OriginalArg(0) int[] src, @OriginalArg(1) int off, @OriginalArg(2) int initialLen) {
		@Pc(5) int len = initialLen - 7;
		while (off < len) {
			src[off++] = 0;
			src[off++] = 0;
			src[off++] = 0;
			src[off++] = 0;
			src[off++] = 0;
			src[off++] = 0;
			src[off++] = 0;
			src[off++] = 0;
		}
		len += 7;
		while (off < len) {
			src[off++] = 0;
		}
	}

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([IIII)V")
	public static void setVariables(@OriginalArg(0) int[] array, @OriginalArg(1) int position, @OriginalArg(2) int startPosition, @OriginalArg(3) int variableToSet) {
		@Pc(5) int end = position + startPosition - 7;
		while (position < end) {
			array[position++] = variableToSet;
			array[position++] = variableToSet;
			array[position++] = variableToSet;
			array[position++] = variableToSet;
			array[position++] = variableToSet;
			array[position++] = variableToSet;
			array[position++] = variableToSet;
			array[position++] = variableToSet;
		}
		end += 7;
		while (position < end) {
			array[position++] = variableToSet;
		}
	}

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([II[III)V")
	public static void copy(@OriginalArg(0) int[] pallete, @OriginalArg(1) int srcPos, @OriginalArg(2) int[] dest, @OriginalArg(3) int destPos, @OriginalArg(4) int length) {
		@Pc(15) int local15;
		if (pallete == dest) {
			if (srcPos == destPos) {
				return;
			}
			if (destPos > srcPos && destPos < srcPos + length) {
				int srcLimit = length - 1;
				@Pc(19) int srcIndex = srcPos + srcLimit;
				@Pc(23) int destIndex = destPos + srcLimit;
				srcLimit = srcIndex - srcLimit;
				srcLimit += 7;
				while (srcIndex >= srcLimit) {
					dest[destIndex--] = pallete[srcIndex--];
					dest[destIndex--] = pallete[srcIndex--];
					dest[destIndex--] = pallete[srcIndex--];
					dest[destIndex--] = pallete[srcIndex--];
					dest[destIndex--] = pallete[srcIndex--];
					dest[destIndex--] = pallete[srcIndex--];
					dest[destIndex--] = pallete[srcIndex--];
					dest[destIndex--] = pallete[srcIndex--];
				}
				srcLimit -= 7;
				while (srcIndex >= srcLimit) {
					dest[destIndex--] = pallete[srcIndex--];
				}
				return;
			}
		}
		int srcEnd = length + srcPos;
		@Pc(115) int unrolledEnd = srcEnd - 7;
		while (srcPos < unrolledEnd) {
			dest[destPos++] = pallete[srcPos++];
			dest[destPos++] = pallete[srcPos++];
			dest[destPos++] = pallete[srcPos++];
			dest[destPos++] = pallete[srcPos++];
			dest[destPos++] = pallete[srcPos++];
			dest[destPos++] = pallete[srcPos++];
			dest[destPos++] = pallete[srcPos++];
			dest[destPos++] = pallete[srcPos++];
		}
		srcEnd = unrolledEnd + 7;
		while (srcPos < srcEnd) {
			dest[destPos++] = pallete[srcPos++];
		}
	}

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([Ljava/lang/Object;I[Ljava/lang/Object;II)V")
	public static void copy(@OriginalArg(0) Object[] objects, @OriginalArg(1) int srcPos, @OriginalArg(2) Object[] objects2, @OriginalArg(3) int destPos, @OriginalArg(4) int length) {
		@Pc(15) int local15;
		if (objects == objects2) {
			if (srcPos == destPos) {
				return;
			}
			if (destPos > srcPos && destPos < srcPos + length) {
				int srcLimit = length - 1;
				@Pc(19) int srcIndex = srcPos + srcLimit;
				@Pc(23) int destIndex = destPos + srcLimit;
				srcLimit = srcIndex - srcLimit;
				srcLimit += 7;
				while (srcIndex >= srcLimit) {
					objects2[destIndex--] = objects[srcIndex--];
					objects2[destIndex--] = objects[srcIndex--];
					objects2[destIndex--] = objects[srcIndex--];
					objects2[destIndex--] = objects[srcIndex--];
					objects2[destIndex--] = objects[srcIndex--];
					objects2[destIndex--] = objects[srcIndex--];
					objects2[destIndex--] = objects[srcIndex--];
					objects2[destIndex--] = objects[srcIndex--];
				}
				srcLimit -= 7;
				while (srcIndex >= srcLimit) {
					objects2[destIndex--] = objects[srcIndex--];
				}
				return;
			}
		}
		int srcEnd = length + srcPos;
		@Pc(115) int unrolledEnd = srcEnd - 7;
		while (srcPos < unrolledEnd) {
			objects2[destPos++] = objects[srcPos++];
			objects2[destPos++] = objects[srcPos++];
			objects2[destPos++] = objects[srcPos++];
			objects2[destPos++] = objects[srcPos++];
			objects2[destPos++] = objects[srcPos++];
			objects2[destPos++] = objects[srcPos++];
			objects2[destPos++] = objects[srcPos++];
			objects2[destPos++] = objects[srcPos++];
		}
		srcEnd = unrolledEnd + 7;
		while (srcPos < srcEnd) {
			objects2[destPos++] = objects[srcPos++];
		}
	}

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([SI[SII)V")
	public static void copy(@OriginalArg(0) short[] src, @OriginalArg(1) int srcPos, @OriginalArg(2) short[] dest, @OriginalArg(3) int destPos, @OriginalArg(4) int length) {
		if (src == dest) {
			return;
		}
		@Pc(114) int end = length;
		@Pc(115) int unrolledEnd = end - 7;
		while (srcPos < unrolledEnd) {
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
			dest[destPos++] = src[srcPos++];
		}
		end = unrolledEnd + 7;
		while (srcPos < end) {
			dest[destPos++] = src[srcPos++];
		}
	}
	@OriginalMember(owner = "client!lt", name = "a", descriptor = "(I[BII)[B")
	public static byte[] copyBlock(@OriginalArg(0) int offset, @OriginalArg(1) byte[] src) {
		@Pc(11) byte[] copy = new byte[32768];
		Arrays.copy(src, offset, copy, 0, 32768);
		return copy;
	}

	@OriginalMember(owner = "client!lt", name = "a", descriptor = "(IB[B)[B")
	public static byte[] copyOf(@OriginalArg(0) int length, @OriginalArg(2) byte[] src) {
		@Pc(11) byte[] copy = new byte[length];
		Arrays.copy(src, 0, copy, 0, length);
		return copy;
	}
	@OriginalMember(owner = "client!cf", name = "a", descriptor = "(I[B)[B")
	public static byte[] copyOf(@OriginalArg(1) byte[] src) {
		if (src == null) {
			return null;
		} else {
			@Pc(11) byte[] copy = new byte[src.length];
			Arrays.copy(src, 0, copy, 0, src.length);
			return copy;
		}
	}
	@OriginalMember(owner = "client!hc", name = "a", descriptor = "([SB)[S")
	public static short[] copyOf(@OriginalArg(0) short[] src) {
		if (src == null) {
			return null;
		} else {
			@Pc(11) short[] copy = new short[src.length];
			Arrays.copy(src, 0, copy, 0, src.length);
			return copy;
		}
	}
}
