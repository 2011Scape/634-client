package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.js5.Whirlpool
import java.io.InputStream
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ek")
class DroppedInputStream : InputStream() {

	@OriginalMember(owner = "client!ek", name = "read", descriptor = "()I")
	override fun read(): Int {
		TimeUtils.sleep(30000L)
		return -1
	}

	companion object {
		@OriginalMember(owner = "client!ek", name = "f", descriptor = "[J")
		@JvmField
		val ROUND_CONSTANTS: LongArray = LongArray(11)

		@OriginalMember(owner = "client!ek", name = "e", descriptor = "[[J")
		@JvmField
		val CIRCULANT_TABLE: Array<LongArray> = Array(8) { LongArray(256) }

		init {
			for (@Pc(8) x in 0 until 256) {
				@Pc(15) val c: Char = "ᠣ웨螸ŏ㚦틵祯酒悼鮎ꌌ笵ᷠퟂ\u2e4b﹗ᕷ㟥\u9ff0䫚壉⤊놠殅뵝ჴ쬾է\ue427䆋Ᵹ闘ﯮ籦\udd17䞞쨭뼇굚茳挂ꩱ젙䧙\uf2e3守騦㊰\ue90f햀뻍㑈ｺ遟\u2068\u1aae둔錢擱猒䀈쏬\udba1贽需켫皂혛떯橐䗳ワ㽕ꋪ斺⿀\ude1c\ufd4d鉵ڊ닦ฟ拔ꢖ暈╙葲㥌幸㢌톥\ue261댡鰞䏇ﰄ写洍\ufadf縤㮫츑轎럫㲁铷뤓ⳓ\ue76e쐃噄義⪻셓\udc0b鵬ㅴ\uf646겉ᓡᘺ椉炶탭챂颤⡜\uf886"[x / 2]
				@Pc(31) val v1: Long = (if ((x and 0x1) == 0) c.code ushr 8 else c.code and 0xFF).toLong()
				@Pc(35) var v2: Long = v1 shl 1
				if (v2 >= 256L) {
					v2 = v2 xor 0x11DL
				}
				@Pc(49) var v4: Long = v2 shl 1
				if (v4 >= 256L) {
					v4 = v4 xor 0x11DL
				}
				@Pc(61) val v5: Long = v1 xor v4
				@Pc(65) var v8: Long = v4 shl 1
				if (v8 >= 256L) {
					v8 = v8 xor 0x11DL
				}
				@Pc(77) val v9: Long = v8 xor v1
				CIRCULANT_TABLE[0][x] = Whirlpool.or(v9, Whirlpool.or(v2 shl 8, Whirlpool.or(v5 shl 16, Whirlpool.or(v8 shl 24, Whirlpool.or(v1 shl 32, Whirlpool.or(Whirlpool.or(v1 shl 56, v1 shl 48), v4 shl 40))))))
				for (@Pc(113) t in 1 until 8) {
					CIRCULANT_TABLE[t][x] = Whirlpool.or(CIRCULANT_TABLE[t - 1][x] ushr 8, CIRCULANT_TABLE[t - 1][x] shl 56)
				}
			}
			ROUND_CONSTANTS[0] = 0L
			for (@Pc(154) r in 1..10) {
				@Pc(161) val i: Int = r * 8 - 8
				ROUND_CONSTANTS[r] = Whirlpool.xor(Whirlpool.xor(Whirlpool.bitAnd(CIRCULANT_TABLE[6][i + 6], 65280L), Whirlpool.xor(Whirlpool.bitAnd(CIRCULANT_TABLE[5][i + 5], 16711680L), Whirlpool.xor(Whirlpool.xor(Whirlpool.xor(Whirlpool.bitAnd(CIRCULANT_TABLE[2][i + 2], 280375465082880L), Whirlpool.xor(Whirlpool.bitAnd(71776119061217280L, CIRCULANT_TABLE[1][i + 1]), Whirlpool.bitAnd(CIRCULANT_TABLE[0][i], -72057594037927936L))), Whirlpool.bitAnd(1095216660480L, CIRCULANT_TABLE[3][i + 3])), Whirlpool.bitAnd(CIRCULANT_TABLE[4][i + 4], 4278190080L)))), Whirlpool.bitAnd(CIRCULANT_TABLE[7][i + 7], 255L))
			}
		}
	}
}
