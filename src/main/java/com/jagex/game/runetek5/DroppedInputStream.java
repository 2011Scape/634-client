package com.jagex.game.runetek5;

import java.io.InputStream;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.js5.Whirlpool;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!ek")
public final class DroppedInputStream extends InputStream {

	@OriginalMember(owner = "client!ek", name = "f", descriptor = "[J")
	public static final long[] ROUND_CONSTANTS = new long[11];

	@OriginalMember(owner = "client!ek", name = "e", descriptor = "[[J")
	public static final long[][] CIRCULANT_TABLE = new long[8][256];

	static {
		for (@Pc(8) int x = 0; x < 256; x++) {
			@Pc(15) char c = "ᠣ웨螸ŏ㚦틵祯酒悼鮎ꌌ笵ᷠퟂ\u2e4b﹗ᕷ㟥\u9ff0䫚壉⤊놠殅뵝ჴ쬾է\ue427䆋Ᵹ闘ﯮ籦\udd17䞞쨭뼇굚茳挂ꩱ젙䧙\uf2e3守騦㊰\ue90f햀뻍㑈ｺ遟\u2068\u1aae둔錢擱猒䀈쏬\udba1贽需켫皂혛떯橐䗳ワ㽕ꋪ斺⿀\ude1c\ufd4d鉵ڊ닦ฟ拔ꢖ暈╙葲㥌幸㢌톥\ue261댡鰞䏇ﰄ写洍\ufadf縤㮫츑轎럫㲁铷뤓ⳓ\ue76e쐃噄義⪻셓\udc0b鵬ㅴ\uf646겉ᓡᘺ椉炶탭챂颤⡜\uf886".charAt(x / 2);
			@Pc(31) long v1 = (long) ((x & 0x1) == 0 ? c >>> 8 : c & 0xFF);
			@Pc(35) long v2 = v1 << 1;
			if (v2 >= 256L) {
				v2 ^= 0x11DL;
			}
			@Pc(49) long v4 = v2 << 1;
			if (v4 >= 256L) {
				v4 ^= 0x11DL;
			}
			@Pc(61) long v5 = v1 ^ v4;
			@Pc(65) long v8 = v4 << 1;
			if (v8 >= 256L) {
				v8 ^= 0x11DL;
			}
			@Pc(77) long v9 = v8 ^ v1;
			CIRCULANT_TABLE[0][x] = Whirlpool.or(v9, Whirlpool.or(v2 << 8, Whirlpool.or(v5 << 16, Whirlpool.or(v8 << 24, Whirlpool.or(v1 << 32, Whirlpool.or(Whirlpool.or(v1 << 56, v1 << 48), v4 << 40))))));
			for (@Pc(113) int t = 1; t < 8; t++) {
				CIRCULANT_TABLE[t][x] = Whirlpool.or(CIRCULANT_TABLE[t - 1][x] >>> 8, CIRCULANT_TABLE[t - 1][x] << 56);
			}
		}
		ROUND_CONSTANTS[0] = 0L;
		for (@Pc(154) int r = 1; r <= 10; r++) {
			@Pc(161) int i = r * 8 - 8;
			ROUND_CONSTANTS[r] = Whirlpool.xor(Whirlpool.xor(Whirlpool.bitAnd(CIRCULANT_TABLE[6][i + 6], 65280L), Whirlpool.xor(Whirlpool.bitAnd(CIRCULANT_TABLE[5][i + 5], 16711680L), Whirlpool.xor(Whirlpool.xor(Whirlpool.xor(Whirlpool.bitAnd(CIRCULANT_TABLE[2][i + 2], 280375465082880L), Whirlpool.xor(Whirlpool.bitAnd(71776119061217280L, CIRCULANT_TABLE[1][i + 1]), Whirlpool.bitAnd(CIRCULANT_TABLE[0][i], -72057594037927936L))), Whirlpool.bitAnd(1095216660480L, CIRCULANT_TABLE[3][i + 3])), Whirlpool.bitAnd(CIRCULANT_TABLE[4][i + 4], 4278190080L)))), Whirlpool.bitAnd(CIRCULANT_TABLE[7][i + 7], 255L));
		}
	}

	@OriginalMember(owner = "client!ek", name = "read", descriptor = "()I")
	@Override
	public int read() {
		TimeUtils.sleep(30000L);
		return -1;
	}
}
