package com.jagex.game.runetek5.js5;

import com.jagex.game.runetek5.DroppedInputStream;
import com.jagex.game.runetek5.core.util.Arrays;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!lda")
public final class Whirlpool {

	@OriginalMember(owner = "client!lda", name = "e", descriptor = "[B")
	private final byte[] bitLength = new byte[32];

	@OriginalMember(owner = "client!lda", name = "k", descriptor = "[J")
	private final long[] L = new long[8];

	@OriginalMember(owner = "client!lda", name = "h", descriptor = "I")
	private int bufferPosition = 0;

	@OriginalMember(owner = "client!lda", name = "c", descriptor = "I")
	private int bufferBits = 0;

	@OriginalMember(owner = "client!lda", name = "l", descriptor = "[J")
	private final long[] K = new long[8];

	@OriginalMember(owner = "client!lda", name = "m", descriptor = "[J")
	private final long[] block = new long[8];

	@OriginalMember(owner = "client!lda", name = "o", descriptor = "[B")
	private final byte[] buffer = new byte[64];

	@OriginalMember(owner = "client!lda", name = "i", descriptor = "[J")
	private final long[] state = new long[8];

	@OriginalMember(owner = "client!lda", name = "n", descriptor = "[J")
	private final long[] hash = new long[8];

	@OriginalMember(owner = "client!lda", name = "a", descriptor = "(I)V")
	private void processBuffer() {
		@Pc(7) int blockIndex = 0;
		@Pc(9) int bufferOffset = 0;
		while (blockIndex < 8) {
			this.block[blockIndex] = Whirlpool.xor(Whirlpool.xor(Whirlpool.bitAnd(255L, (long) this.buffer[bufferOffset + 6]) << 8, Whirlpool.xor(Whirlpool.bitAnd((long) this.buffer[bufferOffset + 5] << 16, 0xFFL << 16), Whirlpool.xor(Whirlpool.xor(Whirlpool.bitAnd(0xFFL << 32, (long) this.buffer[bufferOffset + 3] << 32), Whirlpool.xor(Whirlpool.xor(Whirlpool.bitAnd(0xFFL << 48, (long) this.buffer[bufferOffset + 1] << 48), (long) this.buffer[bufferOffset] << 56), Whirlpool.bitAnd((long) this.buffer[bufferOffset + 2] << 40, 0xFFL << 40))), Whirlpool.bitAnd((long) this.buffer[bufferOffset + 4], 255L) << 24))), Whirlpool.bitAnd(255L, (long) this.buffer[bufferOffset + 7]));
			bufferOffset += 8;
			blockIndex++;
		}
		for (@Pc(121) int i = 0; i < 8; i++) {
			this.state[i] = Whirlpool.xor(this.block[i], this.K[i] = this.hash[i]);
		}
		@Pc(151) int row;
		for (@Pc(147) int j = 1; j <= 10; j++) {
			@Pc(160) int local160;
			@Pc(162) int local162;
			for (row = 0; row < 8; row++) {
				this.L[row] = 0L;
				int keyColumn = 0;
				int keyShift = 56;
				while (keyColumn < 8) {
					this.L[row] = Whirlpool.xor(this.L[row], DroppedInputStream.CIRCULANT_TABLE[keyColumn][(int) (this.K[row - keyColumn & 0x7] >>> keyShift) & 0xFF]);
					keyShift -= 8;
					keyColumn++;
				}
			}
			for (int keyWord = 0; keyWord < 8; keyWord++) {
				this.K[keyWord] = this.L[keyWord];
			}
			this.K[0] = Whirlpool.xor(this.K[0], DroppedInputStream.ROUND_CONSTANTS[j]);
			@Pc(245) int local245;
			for (int stateRow = 0; stateRow < 8; stateRow++) {
				this.L[stateRow] = this.K[stateRow];
				int stateColumn = 0;
				@Pc(247) int shift = 56;
				while (stateColumn < 8) {
					this.L[stateRow] = Whirlpool.xor(this.L[stateRow], DroppedInputStream.CIRCULANT_TABLE[stateColumn][(int) (this.state[stateRow - stateColumn & 0x7] >>> shift) & 0xFF]);
					shift -= 8;
					stateColumn++;
				}
			}
			for (int stateWord = 0; stateWord < 8; stateWord++) {
				this.state[stateWord] = this.L[stateWord];
			}
		}
		for (row = 0; row < 8; row++) {
			this.hash[row] = Whirlpool.xor(this.hash[row], Whirlpool.xor(this.block[row], this.state[row]));
		}
	}

	@OriginalMember(owner = "client!lda", name = "b", descriptor = "(I)V")
	public void NESSIEinit() {
		for (@Pc(15) int i = 0; i < 32; i++) {
			this.bitLength[i] = 0;
		}
		this.buffer[0] = 0;
		this.bufferBits = this.bufferPosition = 0;
		for (@Pc(39) int j = 0; j < 8; j++) {
			this.hash[j] = 0L;
		}
	}

	@OriginalMember(owner = "client!lda", name = "a", descriptor = "(J[BI)V")
	public void NESSIEadd(@OriginalArg(0) long sourceBits, @OriginalArg(1) byte[] source) {
		@Pc(7) int sourcePos = 0;
		@Pc(16) int sourceGap = 8 - ((int) sourceBits & 0x7) & 0x7;
		@Pc(21) int bufferRem = this.bufferBits & 0x7;
		@Pc(23) long value = sourceBits;
		@Pc(25) int lengthIndex = 31;
		@Pc(27) int carry = 0;
		while (lengthIndex >= 0) {
			carry += (this.bitLength[lengthIndex] & 0xFF) + ((int) value & 0xFF);
			this.bitLength[lengthIndex] = (byte) carry;
			value >>>= 0x8;
			carry >>>= 0x8;
			lengthIndex--;
		}
		@Pc(92) int b;
		while (sourceBits > 8L) {
			b = source[sourcePos] << sourceGap & 0xFF | (source[sourcePos + 1] & 0xFF) >>> 8 - sourceGap;
			if (b < 0 || b >= 256) {
				throw new RuntimeException("LOGIC ERROR");
			}
			this.buffer[this.bufferPosition] = (byte) (this.buffer[this.bufferPosition] | b >>> bufferRem);
			this.bufferPosition++;
			this.bufferBits += 8 - bufferRem;
			if (this.bufferBits == 512) {
				this.processBuffer();
				this.bufferBits = this.bufferPosition = 0;
			}
			this.buffer[this.bufferPosition] = (byte) (b << 8 - bufferRem & 0xFF);
			sourcePos++;
			sourceBits -= 8L;
			this.bufferBits += bufferRem;
		}
		if (sourceBits > 0L) {
			b = source[sourcePos] << sourceGap & 0xFF;
			this.buffer[this.bufferPosition] = (byte) (this.buffer[this.bufferPosition] | b >>> bufferRem);
		} else {
			b = 0;
		}
		if (sourceBits + (long) bufferRem < 8L) {
			this.bufferBits = (int) ((long) this.bufferBits + sourceBits);
			return;
		}
		this.bufferBits += 8 - bufferRem;
		sourceBits -= 8 - bufferRem;
		this.bufferPosition++;
		if (this.bufferBits == 512) {
			this.processBuffer();
			this.bufferBits = this.bufferPosition = 0;
		}
		this.buffer[this.bufferPosition] = (byte) (b << 8 - bufferRem & 0xFF);
		this.bufferBits += (int) sourceBits;
	}

	@OriginalMember(owner = "client!lda", name = "a", descriptor = "(ZI[B)V")
	public void NESSIEfinalize(@OriginalArg(2) byte[] digest) {
		this.buffer[this.bufferPosition] = (byte) (this.buffer[this.bufferPosition] | 0x80 >>> (this.bufferBits & 0x7));
		this.bufferPosition++;
		if (this.bufferPosition > 32) {
			while (true) {
				if (this.bufferPosition >= 64) {
					this.processBuffer();
					this.bufferPosition = 0;
					break;
				}
				this.buffer[this.bufferPosition++] = 0;
			}
		}
		while (this.bufferPosition < 32) {
			this.buffer[this.bufferPosition++] = 0;
		}
		Arrays.copy(this.bitLength, 0, this.buffer, 32, 32);
		this.processBuffer();
		@Pc(90) int i = 0;
		@Pc(101) int offset = 0;
		while (i < 8) {
			@Pc(108) long value = this.hash[i];
			digest[offset] = (byte) (value >>> 56);
			digest[offset + 1] = (byte) (value >>> 48);
			digest[offset + 2] = (byte) (value >>> 40);
			digest[offset + 3] = (byte) (value >>> 32);
			digest[offset + 4] = (byte) (value >>> 24);
			digest[offset + 5] = (byte) (value >>> 16);
			digest[offset + 6] = (byte) (value >>> 8);
			digest[offset + 7] = (byte) value;
			i++;
			offset += 8;
		}
	}
	@OriginalMember(owner = "client!dga", name = "a", descriptor = "(II[BI)[B")
	public static byte[] digest(@OriginalArg(1) int srcPosition, @OriginalArg(2) byte[] src, @OriginalArg(3) int size) {
		@Pc(22) byte[] dst;
		if (srcPosition > 0) {
			dst = new byte[size];
			for (@Pc(24) int i = 0; i < size; i++) {
				dst[i] = src[srcPosition + i];
			}
		} else {
			dst = src;
		}
		@Pc(50) Whirlpool whirlpool = new Whirlpool();
		whirlpool.NESSIEinit();
		whirlpool.NESSIEadd((long) (size * 8), dst);
		@Pc(66) byte[] digest = new byte[64];
		whirlpool.NESSIEfinalize(digest);
		return digest;
	}
	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(JJ)J")
	public static long xor(@OriginalArg(0) long lhs, @OriginalArg(1) long rhs) {
		return lhs ^ rhs;
	}

	@OriginalMember(owner = "client!vd", name = "a", descriptor = "(JJ)J")
	public static long or(@OriginalArg(0) long a, @OriginalArg(1) long b) {
		return a | b;
	}
	@OriginalMember(owner = "client!gia", name = "a", descriptor = "(JJ)J")
	public static long bitAnd(@OriginalArg(0) long a, @OriginalArg(1) long b) {
		return a & b;
	}
}
