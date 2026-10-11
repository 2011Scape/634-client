package com.jagex.game.runetek5.core.compress;

import com.jagex.game.runetek5.BZip2InputStream;
import com.jagex.game.runetek5.core.compress.BzipDecompressor;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ig")
public final class BzipDecompressor {

	@OriginalMember(owner = "client!ig", name = "c", descriptor = "I")
	public int strmTotalOutLo32;

	@OriginalMember(owner = "client!ig", name = "e", descriptor = "I")
	public int blockSize100k;

	@OriginalMember(owner = "client!ig", name = "f", descriptor = "[B")
	public byte[] output;

	@OriginalMember(owner = "client!ig", name = "i", descriptor = "I")
	public int origPtr;

	@OriginalMember(owner = "client!ig", name = "l", descriptor = "I")
	public int stateOutLen;

	@OriginalMember(owner = "client!ig", name = "m", descriptor = "[B")
	public byte[] input;

	@OriginalMember(owner = "client!ig", name = "o", descriptor = "I")
	public int strmTotalInLo32;

	@OriginalMember(owner = "client!ig", name = "p", descriptor = "I")
	public int k0;

	@OriginalMember(owner = "client!ig", name = "q", descriptor = "I")
	public int availableOut;

	@OriginalMember(owner = "client!ig", name = "r", descriptor = "B")
	public byte stateOutCh;

	@OriginalMember(owner = "client!ig", name = "t", descriptor = "I")
	public int nInUse;

	@OriginalMember(owner = "client!ig", name = "y", descriptor = "I")
	public int bsLive;

	@OriginalMember(owner = "client!ig", name = "D", descriptor = "I")
	public int saveNblock;

	@OriginalMember(owner = "client!ig", name = "F", descriptor = "I")
	public int tPos;

	@OriginalMember(owner = "client!ig", name = "H", descriptor = "I")
	public int bsBuff;

	@OriginalMember(owner = "client!ig", name = "I", descriptor = "I")
	public int nblockused;

	@OriginalMember(owner = "client!ig", name = "d", descriptor = "[I")
	public final int[] minLens = new int[6];

	@OriginalMember(owner = "client!ig", name = "j", descriptor = "[B")
	public final byte[] selector = new byte[18002];

	@OriginalMember(owner = "client!ig", name = "a", descriptor = "I")
	public int strmNextInPtr = 0;

	@OriginalMember(owner = "client!ig", name = "h", descriptor = "[[I")
	public final int[][] perm = new int[6][258];

	@OriginalMember(owner = "client!ig", name = "n", descriptor = "[I")
	public final int[] unzftab = new int[256];

	@OriginalMember(owner = "client!ig", name = "w", descriptor = "[B")
	public final byte[] selectorMtf = new byte[18002];

	@OriginalMember(owner = "client!ig", name = "b", descriptor = "[Z")
	public final boolean[] inUse = new boolean[256];

	@OriginalMember(owner = "client!ig", name = "g", descriptor = "[B")
	public final byte[] seqToUnseq = new byte[256];

	@OriginalMember(owner = "client!ig", name = "B", descriptor = "I")
	public int strmNextOutPtr = 0;

	@OriginalMember(owner = "client!ig", name = "C", descriptor = "[[B")
	public final byte[][] len = new byte[6][258];

	@OriginalMember(owner = "client!ig", name = "x", descriptor = "[B")
	public final byte[] mtfa = new byte[4096];

	@OriginalMember(owner = "client!ig", name = "s", descriptor = "[I")
	public final int[] mtfbase = new int[16];

	@OriginalMember(owner = "client!ig", name = "k", descriptor = "[Z")
	public final boolean[] inUse16 = new boolean[16];

	@OriginalMember(owner = "client!ig", name = "G", descriptor = "[[I")
	public final int[][] base = new int[6][258];

	@OriginalMember(owner = "client!ig", name = "J", descriptor = "[[I")
	public final int[][] limit = new int[6][258];

	@OriginalMember(owner = "client!ig", name = "K", descriptor = "[I")
	public final int[] cftab = new int[257];

	@OriginalMember(owner = "client!bs", name = "a", descriptor = "([BI[BII)I")
	public static int bunzip(@OriginalArg(0) byte[] output, @OriginalArg(1) int outputLength, @OriginalArg(2) byte[] input, @OriginalArg(3) int inLength) {
		@Pc(1) BzipDecompressor bzipDecompressor = BZip2InputStream.INSTANCE;
		synchronized (BZip2InputStream.INSTANCE) {
			BZip2InputStream.INSTANCE.input = input;
			BZip2InputStream.INSTANCE.strmNextInPtr = 9;
			BZip2InputStream.INSTANCE.output = output;
			BZip2InputStream.INSTANCE.strmNextOutPtr = 0;
			BZip2InputStream.INSTANCE.availableOut = outputLength;
			BZip2InputStream.INSTANCE.bsLive = 0;
			BZip2InputStream.INSTANCE.bsBuff = 0;
			BZip2InputStream.INSTANCE.strmTotalInLo32 = 0;
			BZip2InputStream.INSTANCE.strmTotalOutLo32 = 0;
			BZip2InputStream.decompress(BZip2InputStream.INSTANCE);
			@Pc(37) int size = outputLength - BZip2InputStream.INSTANCE.availableOut;
			BZip2InputStream.INSTANCE.input = null;
			BZip2InputStream.INSTANCE.output = null;
			return size;
		}
	}

	@OriginalMember(owner = "client!bs", name = "a", descriptor = "([I[I[I[BIII)V")
	public static void buildHuffmanTable(@OriginalArg(0) int[] limitTable, @OriginalArg(1) int[] baseTable, @OriginalArg(2) int[] permTable, @OriginalArg(3) byte[] lengthTable, @OriginalArg(4) int minLength, @OriginalArg(5) int maxLength, @OriginalArg(6) int symbolCount) {
		@Pc(1) int permIndex = 0;
		@Pc(3) int local3;
		for (int codeLength = minLength; codeLength <= maxLength; codeLength++) {
			for (@Pc(6) int i = 0; i < symbolCount; i++) {
				if (lengthTable[i] == codeLength) {
					permTable[permIndex] = i;
					permIndex++;
				}
			}
		}
		for (int codeLength = 0; codeLength < 23; codeLength++) {
			baseTable[codeLength] = 0;
		}
		for (int symbol = 0; symbol < symbolCount; symbol++) {
			baseTable[lengthTable[symbol] + 1]++;
		}
		for (int codeLength = 1; codeLength < 23; codeLength++) {
			baseTable[codeLength] += baseTable[codeLength - 1];
		}
		for (int codeLength = 0; codeLength < 23; codeLength++) {
			limitTable[codeLength] = 0;
		}
		@Pc(85) int code = 0;
		for (int codeLength = minLength; codeLength <= maxLength; codeLength++) {
			code += baseTable[codeLength + 1] - baseTable[codeLength];
			limitTable[codeLength] = code - 1;
			code <<= 0x1;
		}
		for (int codeLength = minLength + 1; codeLength <= maxLength; codeLength++) {
			baseTable[codeLength] = (limitTable[codeLength - 1] + 1 << 1) - baseTable[codeLength];
		}
	}
}
