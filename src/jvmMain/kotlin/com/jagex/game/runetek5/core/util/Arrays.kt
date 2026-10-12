package com.jagex.game.runetek5.core.util

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object Arrays {

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([BI[BII)V")
	@JvmStatic
	fun copy(@OriginalArg(0) src: ByteArray?, @OriginalArg(1) srcPos: Int, @OriginalArg(2) dest: ByteArray?, @OriginalArg(3) destPos: Int, @OriginalArg(4) length: Int) {
		var srcPos: Int = srcPos
		var destPos: Int = destPos
		@Pc(15) val local15: Int
		if (src === dest) {
			if (srcPos == destPos) {
				return
			}
			if (destPos > srcPos && destPos < srcPos + length) {
				var srcLimit: Int = length - 1
				@Pc(19) var srcIndex: Int = srcPos + srcLimit
				@Pc(23) var destIndex: Int = destPos + srcLimit
				srcLimit = srcIndex - srcLimit
				srcLimit += 7
				while (srcIndex >= srcLimit) {
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
				}
				srcLimit -= 7
				while (srcIndex >= srcLimit) {
					dest!![destIndex--] = src!![srcIndex--]
				}
				return
			}
		}
		var srcEnd: Int = length + srcPos
		@Pc(115) val unrolledEnd: Int = srcEnd - 7
		while (srcPos < unrolledEnd) {
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
		}
		srcEnd = unrolledEnd + 7
		while (srcPos < srcEnd) {
			dest!![destPos++] = src!![srcPos++]
		}
	}

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([FI[FII)V")
	@JvmStatic
	fun copy(@OriginalArg(0) src: FloatArray?, @OriginalArg(1) srcPos: Int, @OriginalArg(2) dest: FloatArray?, @OriginalArg(3) destPos: Int, @OriginalArg(4) length: Int) {
		var srcPos: Int = srcPos
		var destPos: Int = destPos
		@Pc(15) val local15: Int
		if (src === dest) {
			if (srcPos == destPos) {
				return
			}
			if (destPos > srcPos && destPos < srcPos + length) {
				var srcLimit: Int = length - 1
				@Pc(19) var srcIndex: Int = srcPos + srcLimit
				@Pc(23) var destIndex: Int = destPos + srcLimit
				srcLimit = srcIndex - srcLimit
				srcLimit += 7
				while (srcIndex >= srcLimit) {
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
					dest!![destIndex--] = src!![srcIndex--]
				}
				srcLimit -= 7
				while (srcIndex >= srcLimit) {
					dest!![destIndex--] = src!![srcIndex--]
				}
				return
			}
		}
		var srcEnd: Int = length + srcPos
		@Pc(115) val unrolledEnd: Int = srcEnd - 7
		while (srcPos < unrolledEnd) {
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
		}
		srcEnd = unrolledEnd + 7
		while (srcPos < srcEnd) {
			dest!![destPos++] = src!![srcPos++]
		}
	}

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([III)V")
	@JvmStatic
	fun clear(@OriginalArg(0) src: IntArray?, @OriginalArg(1) off: Int, @OriginalArg(2) initialLen: Int) {
		var off: Int = off
		@Pc(5) var len: Int = initialLen - 7
		while (off < len) {
			src!![off++] = 0
			src!![off++] = 0
			src!![off++] = 0
			src!![off++] = 0
			src!![off++] = 0
			src!![off++] = 0
			src!![off++] = 0
			src!![off++] = 0
		}
		len += 7
		while (off < len) {
			src!![off++] = 0
		}
	}

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([IIII)V")
	@JvmStatic
	fun setVariables(@OriginalArg(0) array: IntArray?, @OriginalArg(1) position: Int, @OriginalArg(2) startPosition: Int, @OriginalArg(3) variableToSet: Int) {
		var position: Int = position
		@Pc(5) var end: Int = position + startPosition - 7
		while (position < end) {
			array!![position++] = variableToSet
			array!![position++] = variableToSet
			array!![position++] = variableToSet
			array!![position++] = variableToSet
			array!![position++] = variableToSet
			array!![position++] = variableToSet
			array!![position++] = variableToSet
			array!![position++] = variableToSet
		}
		end += 7
		while (position < end) {
			array!![position++] = variableToSet
		}
	}

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([II[III)V")
	@JvmStatic
	fun copy(@OriginalArg(0) pallete: IntArray?, @OriginalArg(1) srcPos: Int, @OriginalArg(2) dest: IntArray?, @OriginalArg(3) destPos: Int, @OriginalArg(4) length: Int) {
		var srcPos: Int = srcPos
		var destPos: Int = destPos
		@Pc(15) val local15: Int
		if (pallete === dest) {
			if (srcPos == destPos) {
				return
			}
			if (destPos > srcPos && destPos < srcPos + length) {
				var srcLimit: Int = length - 1
				@Pc(19) var srcIndex: Int = srcPos + srcLimit
				@Pc(23) var destIndex: Int = destPos + srcLimit
				srcLimit = srcIndex - srcLimit
				srcLimit += 7
				while (srcIndex >= srcLimit) {
					dest!![destIndex--] = pallete!![srcIndex--]
					dest!![destIndex--] = pallete!![srcIndex--]
					dest!![destIndex--] = pallete!![srcIndex--]
					dest!![destIndex--] = pallete!![srcIndex--]
					dest!![destIndex--] = pallete!![srcIndex--]
					dest!![destIndex--] = pallete!![srcIndex--]
					dest!![destIndex--] = pallete!![srcIndex--]
					dest!![destIndex--] = pallete!![srcIndex--]
				}
				srcLimit -= 7
				while (srcIndex >= srcLimit) {
					dest!![destIndex--] = pallete!![srcIndex--]
				}
				return
			}
		}
		var srcEnd: Int = length + srcPos
		@Pc(115) val unrolledEnd: Int = srcEnd - 7
		while (srcPos < unrolledEnd) {
			dest!![destPos++] = pallete!![srcPos++]
			dest!![destPos++] = pallete!![srcPos++]
			dest!![destPos++] = pallete!![srcPos++]
			dest!![destPos++] = pallete!![srcPos++]
			dest!![destPos++] = pallete!![srcPos++]
			dest!![destPos++] = pallete!![srcPos++]
			dest!![destPos++] = pallete!![srcPos++]
			dest!![destPos++] = pallete!![srcPos++]
		}
		srcEnd = unrolledEnd + 7
		while (srcPos < srcEnd) {
			dest!![destPos++] = pallete!![srcPos++]
		}
	}

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([Ljava/lang/Object;I[Ljava/lang/Object;II)V")
	@JvmStatic
	fun <T> copy(@OriginalArg(0) objects: Array<out T>?, @OriginalArg(1) srcPos: Int, @OriginalArg(2) objects2: Array<T>?, @OriginalArg(3) destPos: Int, @OriginalArg(4) length: Int) {
		var srcPos: Int = srcPos
		var destPos: Int = destPos
		@Pc(15) val local15: Int
		if (objects === objects2) {
			if (srcPos == destPos) {
				return
			}
			if (destPos > srcPos && destPos < srcPos + length) {
				var srcLimit: Int = length - 1
				@Pc(19) var srcIndex: Int = srcPos + srcLimit
				@Pc(23) var destIndex: Int = destPos + srcLimit
				srcLimit = srcIndex - srcLimit
				srcLimit += 7
				while (srcIndex >= srcLimit) {
					objects2!![destIndex--] = objects!![srcIndex--]
					objects2!![destIndex--] = objects!![srcIndex--]
					objects2!![destIndex--] = objects!![srcIndex--]
					objects2!![destIndex--] = objects!![srcIndex--]
					objects2!![destIndex--] = objects!![srcIndex--]
					objects2!![destIndex--] = objects!![srcIndex--]
					objects2!![destIndex--] = objects!![srcIndex--]
					objects2!![destIndex--] = objects!![srcIndex--]
				}
				srcLimit -= 7
				while (srcIndex >= srcLimit) {
					objects2!![destIndex--] = objects!![srcIndex--]
				}
				return
			}
		}
		var srcEnd: Int = length + srcPos
		@Pc(115) val unrolledEnd: Int = srcEnd - 7
		while (srcPos < unrolledEnd) {
			objects2!![destPos++] = objects!![srcPos++]
			objects2!![destPos++] = objects!![srcPos++]
			objects2!![destPos++] = objects!![srcPos++]
			objects2!![destPos++] = objects!![srcPos++]
			objects2!![destPos++] = objects!![srcPos++]
			objects2!![destPos++] = objects!![srcPos++]
			objects2!![destPos++] = objects!![srcPos++]
			objects2!![destPos++] = objects!![srcPos++]
		}
		srcEnd = unrolledEnd + 7
		while (srcPos < srcEnd) {
			objects2!![destPos++] = objects!![srcPos++]
		}
	}

	@OriginalMember(owner = "client!rr", name = "a", descriptor = "([SI[SII)V")
	@JvmStatic
	fun copy(@OriginalArg(0) src: ShortArray?, @OriginalArg(1) srcPos: Int, @OriginalArg(2) dest: ShortArray?, @OriginalArg(3) destPos: Int, @OriginalArg(4) length: Int) {
		var srcPos: Int = srcPos
		var destPos: Int = destPos
		if (src === dest) {
			return
		}
		@Pc(114) var end: Int = length
		@Pc(115) val unrolledEnd: Int = end - 7
		while (srcPos < unrolledEnd) {
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
			dest!![destPos++] = src!![srcPos++]
		}
		end = unrolledEnd + 7
		while (srcPos < end) {
			dest!![destPos++] = src!![srcPos++]
		}
	}

	@OriginalMember(owner = "client!lt", name = "a", descriptor = "(I[BII)[B")
	@JvmStatic
	fun copyBlock(@OriginalArg(0) offset: Int, @OriginalArg(1) src: ByteArray?): ByteArray {
		@Pc(11) val copy: ByteArray = ByteArray(32768)
		Arrays.copy(src, offset, copy, 0, 32768)
		return copy
	}

	@OriginalMember(owner = "client!lt", name = "a", descriptor = "(IB[B)[B")
	@JvmStatic
	fun copyOf(@OriginalArg(0) length: Int, @OriginalArg(2) src: ByteArray?): ByteArray {
		@Pc(11) val copy: ByteArray = ByteArray(length)
		Arrays.copy(src, 0, copy, 0, length)
		return copy
	}

	@OriginalMember(owner = "client!cf", name = "a", descriptor = "(I[B)[B")
	@JvmStatic
	fun copyOf(@OriginalArg(1) src: ByteArray?): ByteArray? {
		if (src == null) {
			return null
		} else {
			@Pc(11) val copy: ByteArray = ByteArray(src.size)
			Arrays.copy(src, 0, copy, 0, src.size)
			return copy
		}
	}

	@OriginalMember(owner = "client!hc", name = "a", descriptor = "([SB)[S")
	@JvmStatic
	fun copyOf(@OriginalArg(0) src: ShortArray?): ShortArray? {
		if (src == null) {
			return null
		} else {
			@Pc(11) val copy: ShortArray = ShortArray(src.size)
			Arrays.copy(src, 0, copy, 0, src.size)
			return copy
		}
	}
}
