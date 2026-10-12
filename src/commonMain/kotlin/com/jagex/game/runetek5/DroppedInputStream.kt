package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.js5.Whirlpool
import io.InputStream
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!ek")
class DroppedInputStream : InputStream() {

	@OriginalMember(owner = "client!ek", name = "read", descriptor = "()I")
	override fun read(): Int {
		TimeUtils.sleep(30000L)
		return -1
	}

	companion object {
		/*
		 * Not part of the original client: the S-box, two bytes per char, as the original's string
		 * literal held it. Some of its chars are lone surrogates, which a JavaScript bundle (UTF-8)
		 * cannot carry, so the browser saw U+FFFD in their place and every Whirlpool digest was
		 * wrong; the same chars as a CharArray reach both platforms intact (milestone 4).
		 */
		private val S_BOX: CharArray = charArrayOf(
			'\u1823', '\uc6e8', '\u87b8', '\u014f', '\u36a6', '\ud2f5', '\u796f', '\u9152',
			'\u60bc', '\u9b8e', '\ua30c', '\u7b35', '\u1de0', '\ud7c2', '\u2e4b', '\ufe57',
			'\u1577', '\u37e5', '\u9ff0', '\u4ada', '\u58c9', '\u290a', '\ub1a0', '\u6b85',
			'\ubd5d', '\u10f4', '\ucb3e', '\u0567', '\ue427', '\u418b', '\ua77d', '\u95d8',
			'\ufbee', '\u7c66', '\udd17', '\u479e', '\uca2d', '\ubf07', '\uad5a', '\u8333',
			'\u6302', '\uaa71', '\uc819', '\u49d9', '\uf2e3', '\u5b88', '\u9a26', '\u32b0',
			'\ue90f', '\ud580', '\ubecd', '\u3448', '\uff7a', '\u905f', '\u2068', '\u1aae',
			'\ub454', '\u9322', '\u64f1', '\u7312', '\u4008', '\uc3ec', '\udba1', '\u8d3d',
			'\u9700', '\ucf2b', '\u7682', '\ud61b', '\ub5af', '\u6a50', '\u45f3', '\u30ef',
			'\u3f55', '\ua2ea', '\u65ba', '\u2fc0', '\ude1c', '\ufd4d', '\u9275', '\u068a',
			'\ub2e6', '\u0e1f', '\u62d4', '\ua896', '\uf9c5', '\u2559', '\u8472', '\u394c',
			'\u5e78', '\u388c', '\ud1a5', '\ue261', '\ub321', '\u9c1e', '\u43c7', '\ufc04',
			'\u5199', '\u6d0d', '\ufadf', '\u7e24', '\u3bab', '\uce11', '\u8f4e', '\ub7eb',
			'\u3c81', '\u94f7', '\ub913', '\u2cd3', '\ue76e', '\uc403', '\u5644', '\u7fa9',
			'\u2abb', '\uc153', '\udc0b', '\u9d6c', '\u3174', '\uf646', '\uac89', '\u14e1',
			'\u163a', '\u6909', '\u70b6', '\ud0ed', '\ucc42', '\u98a4', '\u285c', '\uf886',
		)

		@OriginalMember(owner = "client!ek", name = "f", descriptor = "[J")
		@JvmField
		val ROUND_CONSTANTS: LongArray = LongArray(11)

		@OriginalMember(owner = "client!ek", name = "e", descriptor = "[[J")
		@JvmField
		val CIRCULANT_TABLE: Array<LongArray> = Array(8) { LongArray(256) }

		init {
			for (@Pc(8) x in 0 until 256) {
				@Pc(15) val c: Char = S_BOX[x / 2]
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
