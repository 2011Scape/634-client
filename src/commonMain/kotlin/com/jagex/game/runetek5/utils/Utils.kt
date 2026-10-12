package com.jagex.game.runetek5.utils

import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler
import util.Random
import com.jagex.game.runetek5.preference.impl.MapSizePreference
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.StringBuffer
import kotlin.jvm.Synchronized
import kotlin.math.pow

object Utils {
	@OriginalMember(owner = "client!aba", name = "G", descriptor = "J")
	@JvmField
	var timeOffset: Long = 0L

	@OriginalMember(owner = "client!gq", name = "p", descriptor = "J")
	@JvmField
	var lastSystemTime: Long = 0L

	@OriginalMember(owner = "client!ik", name = "e", descriptor = "[C")
	@JvmField
	val UNICODE_TABLE: CharArray = charArrayOf('€', '\u0000', '‚', 'ƒ', '„', '…', '†', '‡', 'ˆ', '‰', 'Š', '‹', 'Œ', '\u0000', 'Ž', '\u0000', '\u0000', '‘', '’', '“', '”', '•', '–', '—', '˜', '™', 'š', '›', 'œ', '\u0000', 'ž', 'Ÿ')

	@OriginalMember(owner = "client!hc", name = "a", descriptor = "(Ljava/lang/String;I)Z")
	@JvmStatic
	fun isValidInteger(@OriginalArg(0) string: String): Boolean {
		return Utils.isValidIntegerImpl(string)
	}

	@OriginalMember(owner = "client!eo", name = "a", descriptor = "(CZ)B")
	@JvmStatic
	fun charToCp1252(@OriginalArg(0) c: Char): Byte {
		@Pc(38) val cp1252Byte: Byte
		if (c > '\u0000' && c < '\u0080' || c >= '\u00A0' && c <= 'ÿ') {
			cp1252Byte = c.code.toByte()
		} else if (c == '€') {
			cp1252Byte = -128
		} else if (c == '‚') {
			cp1252Byte = -126
		} else if (c == 'ƒ') {
			cp1252Byte = -125
		} else if (c == '„') {
			cp1252Byte = -124
		} else if (c == '…') {
			cp1252Byte = -123
		} else if (c == '†') {
			cp1252Byte = -122
		} else if (c == '‡') {
			cp1252Byte = -121
		} else if (c == 'ˆ') {
			cp1252Byte = -120
		} else if (c == '‰') {
			cp1252Byte = -119
		} else if (c == 'Š') {
			cp1252Byte = -118
		} else if (c == '‹') {
			cp1252Byte = -117
		} else if (c == 'Œ') {
			cp1252Byte = -116
		} else if (c == 'Ž') {
			cp1252Byte = -114
		} else if (c == '‘') {
			cp1252Byte = -111
		} else if (c == '’') {
			cp1252Byte = -110
		} else if (c == '“') {
			cp1252Byte = -109
		} else if (c == '”') {
			cp1252Byte = -108
		} else if (c == '•') {
			cp1252Byte = -107
		} else if (c == '–') {
			cp1252Byte = -106
		} else if (c == '—') {
			cp1252Byte = -105
		} else if (c == '˜') {
			cp1252Byte = -104
		} else if (c == '™') {
			cp1252Byte = -103
		} else if (c == 'š') {
			cp1252Byte = -102
		} else if (c == '›') {
			cp1252Byte = -101
		} else if (c == 'œ') {
			cp1252Byte = -100
		} else if (c == 'ž') {
			cp1252Byte = -98
		} else if (c == 'Ÿ') {
			cp1252Byte = -97
		} else {
			cp1252Byte = 63
		}
		return cp1252Byte
	}

	@OriginalMember(owner = "client!jaa", name = "a", descriptor = "(BI)Z")
	@JvmStatic
	fun isValidCp1252Byte(@OriginalArg(0) b: Byte): Boolean {
		@Pc(7) val code: Int = b.toInt() and 0xFF
		if (code == 0) {
			return false
		} else {
			return code < 128 || code >= 160 || Utils.UNICODE_TABLE[code - 128] != '\u0000'
		}
	}

	@OriginalMember(owner = "client!gb", name = "a", descriptor = "(BI)C")
	@JvmStatic
	fun cp1252ToChar(@OriginalArg(0) b: Byte): Char {
		@Pc(7) var charCode: Int = b.toInt() and 0xFF
		if (charCode == 0) {
			throw IllegalArgumentException("Non cp1252 character 0x" + charCode.toString(16) + " provided")
		}
		if (charCode >= 128 && charCode < 160) {
			@Pc(41) var unicodeChar: Char = Utils.UNICODE_TABLE[charCode - 128]
			if (unicodeChar == '\u0000') {
				unicodeChar = '?'
			}
			charCode = unicodeChar.code
		}
		return charCode.toChar()
	}

	@OriginalMember(owner = "client!pea", name = "a", descriptor = "(ZI[Ljava/lang/String;I)Ljava/lang/String;")
	@JvmStatic
	fun joinStrings(@OriginalArg(1) count: Int, @OriginalArg(2) strings: Array<String?>?, @OriginalArg(3) offset: Int): String {
		if (count == 0) {
			return ""
		} else if (count == 1) {
			@Pc(19) val string: String? = strings!![offset]
			return if (string == null) "null" else string.toString()
		} else {
			@Pc(30) val end: Int = count + offset
			@Pc(32) var totalLength: Int = 0
			for (@Pc(34) i in offset until end) {
				@Pc(40) val string2: String? = strings!![i]
				if (string2 == null) {
					totalLength += 4
				} else {
					totalLength += string2.length
				}
			}
			@Pc(63) val stringBuffer: StringBuffer = StringBuffer(totalLength)
			for (@Pc(65) j in offset until end) {
				@Pc(71) val string3: String? = strings!![j]
				if (string3 == null) {
					stringBuffer.append("null")
				} else {
					stringBuffer.append(string3)
				}
			}
			return stringBuffer.toString()
		}
	}

	@OriginalMember(owner = "client!ffa", name = "a", descriptor = "(I[BIZLjava/lang/String;I)I")
	@JvmStatic
	fun encodeCp1252(@OriginalArg(1) buffer: ByteArray?, @OriginalArg(2) length: Int, @OriginalArg(4) string: String?, @OriginalArg(5) currentPosition: Int): Int {
		@Pc(15) val charCount: Int = length
		for (@Pc(17) i in 0 until charCount) {
			@Pc(25) val c: Char = string!![i]
			if (c > '\u0000' && c < '\u0080' || !(c < '\u00A0' || c > 'ÿ')) {
				buffer!![currentPosition + i] = c.code.toByte()
			} else if (c == '€') {
				buffer!![i + currentPosition] = -128
			} else if (c == '‚') {
				buffer!![i + currentPosition] = -126
			} else if (c == 'ƒ') {
				buffer!![currentPosition + i] = -125
			} else if (c == '„') {
				buffer!![currentPosition + i] = -124
			} else if (c == '…') {
				buffer!![currentPosition + i] = -123
			} else if (c == '†') {
				buffer!![i + currentPosition] = -122
			} else if (c == '‡') {
				buffer!![i + currentPosition] = -121
			} else if (c == 'ˆ') {
				buffer!![currentPosition + i] = -120
			} else if (c == '‰') {
				buffer!![currentPosition + i] = -119
			} else if (c == 'Š') {
				buffer!![currentPosition + i] = -118
			} else if (c == '‹') {
				buffer!![i + currentPosition] = -117
			} else if (c == 'Œ') {
				buffer!![i + currentPosition] = -116
			} else if (c == 'Ž') {
				buffer!![currentPosition + i] = -114
			} else if (c == '‘') {
				buffer!![currentPosition + i] = -111
			} else if (c == '’') {
				buffer!![currentPosition + i] = -110
			} else if (c == '“') {
				buffer!![currentPosition + i] = -109
			} else if (c == '”') {
				buffer!![currentPosition + i] = -108
			} else if (c == '•') {
				buffer!![currentPosition + i] = -107
			} else if (c == '–') {
				buffer!![currentPosition + i] = -106
			} else if (c == '—') {
				buffer!![currentPosition + i] = -105
			} else if (c == '˜') {
				buffer!![i + currentPosition] = -104
			} else if (c == '™') {
				buffer!![i + currentPosition] = -103
			} else if (c == 'š') {
				buffer!![i + currentPosition] = -102
			} else if (c == '›') {
				buffer!![currentPosition + i] = -101
			} else if (c == 'œ') {
				buffer!![currentPosition + i] = -100
			} else if (c == 'ž') {
				buffer!![currentPosition + i] = -98
			} else if (c == 'Ÿ') {
				buffer!![currentPosition + i] = -97
			} else {
				buffer!![i + currentPosition] = 63
			}
		}
		return charCount
	}

	@OriginalMember(owner = "client!hea", name = "a", descriptor = "(ILjava/lang/String;C)I")
	@JvmStatic
	fun countChar(@OriginalArg(1) string: String, @OriginalArg(2) c: Char): Int {
		@Pc(7) var count: Int = 0
		@Pc(15) val length: Int = string.length
		for (@Pc(17) i in 0 until length) {
			if (c == string[i]) {
				count++
			}
		}
		return count
	}

	@OriginalMember(owner = "client!ht", name = "a", descriptor = "(JI)Ljava/lang/String;")
	@JvmStatic
	fun nameForLong(@OriginalArg(0) encoded: Long): String? {
		var encoded: Long = encoded
		if (encoded <= 0L || encoded >= 6582952005840035281L) {
			return null
		} else if (encoded % 37L == 0L) {
			return null
		} else {
			@Pc(27) var length: Int = 0
			@Pc(29) var remaining: Long = encoded
			while (remaining != 0L) {
				length++
				remaining /= 37L
			}
			@Pc(52) val stringBuffer: StringBuffer = StringBuffer(length)
			while (encoded != 0L) {
				@Pc(56) val previous: Long = encoded
				encoded /= 37L
				stringBuffer.append(Utils.BASE37_CHARS[(previous - encoded * 37L).toInt()])
			}
			return stringBuffer.reverse().toString()
		}
	}

	@OriginalMember(owner = "client!ls", name = "a", descriptor = "(Ljava/lang/String;B)[B")
	@JvmStatic
	fun toCp1252Bytes(@OriginalArg(0) string: String): ByteArray {
		@Pc(8) val length: Int = string.length
		@Pc(11) val bytes: ByteArray = ByteArray(length)
		for (@Pc(18) i in 0 until length) {
			@Pc(24) val c: Char = string[i]
			if (c > '\u0000' && c < '\u0080' || !(c < '\u00A0' || c > 'ÿ')) {
				bytes[i] = c.code.toByte()
			} else if (c == '€') {
				bytes[i] = -128
			} else if (c == '‚') {
				bytes[i] = -126
			} else if (c == 'ƒ') {
				bytes[i] = -125
			} else if (c == '„') {
				bytes[i] = -124
			} else if (c == '…') {
				bytes[i] = -123
			} else if (c == '†') {
				bytes[i] = -122
			} else if (c == '‡') {
				bytes[i] = -121
			} else if (c == 'ˆ') {
				bytes[i] = -120
			} else if (c == '‰') {
				bytes[i] = -119
			} else if (c == 'Š') {
				bytes[i] = -118
			} else if (c == '‹') {
				bytes[i] = -117
			} else if (c == 'Œ') {
				bytes[i] = -116
			} else if (c == 'Ž') {
				bytes[i] = -114
			} else if (c == '‘') {
				bytes[i] = -111
			} else if (c == '’') {
				bytes[i] = -110
			} else if (c == '“') {
				bytes[i] = -109
			} else if (c == '”') {
				bytes[i] = -108
			} else if (c == '•') {
				bytes[i] = -107
			} else if (c == '–') {
				bytes[i] = -106
			} else if (c == '—') {
				bytes[i] = -105
			} else if (c == '˜') {
				bytes[i] = -104
			} else if (c == '™') {
				bytes[i] = -103
			} else if (c == 'š') {
				bytes[i] = -102
			} else if (c == '›') {
				bytes[i] = -101
			} else if (c == 'œ') {
				bytes[i] = -100
			} else if (c == 'ž') {
				bytes[i] = -98
			} else if (c == 'Ÿ') {
				bytes[i] = -97
			} else {
				bytes[i] = 63
			}
		}
		return bytes
	}

	@OriginalMember(owner = "client!uba", name = "a", descriptor = "(ZLjava/lang/String;)I")
	@JvmStatic
	fun parseInt(@OriginalArg(1) string: String): Int {
		return Utils.parseIntRadix(10, string)
	}

	@OriginalMember(owner = "client!cc", name = "a", descriptor = "(CZLjava/lang/String;)[Ljava/lang/String;")
	@JvmStatic
	fun split(@OriginalArg(0) delimiter: Char, @OriginalArg(2) string: String): Array<String?> {
		@Pc(10) val delimiterCount: Int = Utils.countChar(string, delimiter)
		@Pc(15) val strings: Array<String?> = arrayOfNulls<String>(delimiterCount + 1)
		@Pc(17) var resultIndex: Int = 0
		@Pc(19) var searchStart: Int = 0
		for (@Pc(26) i in 0 until delimiterCount) {
			@Pc(30) var delimiterPos: Int = searchStart
			while (string[delimiterPos] != delimiter) {
				delimiterPos++
			}
			strings[resultIndex++] = string.substring(searchStart, delimiterPos)
			searchStart = delimiterPos + 1
		}
		strings[delimiterCount] = string.substring(searchStart)
		return strings
	}

	@OriginalMember(owner = "client!vp", name = "a", descriptor = "(Ljava/lang/String;B)J")
	@JvmStatic
	fun hashStringLong(@OriginalArg(0) string: String): Long {
		@Pc(8) val length: Int = string.length
		@Pc(10) var hash: Long = 0L
		for (@Pc(19) i in 0 until length) {
			hash = (hash shl 5) + string[i].code.toLong() - hash
		}
		return hash
	}

	@OriginalMember(owner = "client!kl", name = "a", descriptor = "([BIII)Ljava/lang/String;")
	@JvmStatic
	fun readString(@OriginalArg(0) buffer: ByteArray?, @OriginalArg(2) length: Int, @OriginalArg(3) offset: Int): String {
		@Pc(8) val chars: CharArray = CharArray(length)
		@Pc(10) var count: Int = 0
		for (@Pc(12) i in 0 until length) {
			@Pc(22) var charCode: Int = buffer!![i + offset].toInt() and 0xFF
			if (charCode != 0) {
				if (charCode >= 128 && charCode < 160) {
					@Pc(36) var unicodeChar: Char = Utils.UNICODE_TABLE[charCode - 128]
					if (unicodeChar == '\u0000') {
						unicodeChar = '?'
					}
					charCode = unicodeChar.code
				}
				chars[count++] = charCode.toChar()
			}
		}
		return String(chars, 0, count)
	}

	@OriginalMember(owner = "client!mfa", name = "a", descriptor = "([BIII[[B[[B[I[I)I")
	@JvmStatic
	fun calculateKerning(@OriginalArg(0) glyphWidths: ByteArray?, @OriginalArg(2) secondGlyph: Int, @OriginalArg(3) firstGlyph: Int, @OriginalArg(4) leftOffsets: Array<ByteArray?>?, @OriginalArg(5) rightOffsets: Array<ByteArray?>?, @OriginalArg(6) rowStarts: IntArray?, @OriginalArg(7) rowCounts: IntArray?): Int {
		@Pc(17) val firstStart: Int = rowStarts!![firstGlyph]
		@Pc(23) val firstEnd: Int = rowCounts!![firstGlyph] + firstStart
		@Pc(27) val secondStart: Int = rowStarts[secondGlyph]
		@Pc(33) val secondEnd: Int = rowCounts[secondGlyph] + secondStart
		@Pc(35) var overlapStart: Int = firstStart
		if (secondStart > firstStart) {
			overlapStart = secondStart
		}
		@Pc(46) var overlapEnd: Int = firstEnd
		if (secondEnd < firstEnd) {
			overlapEnd = secondEnd
		}
		@Pc(57) var minGap: Int = glyphWidths!![firstGlyph].toInt() and 0xFF
		if (minGap > (glyphWidths[secondGlyph].toInt() and 0xFF)) {
			minGap = glyphWidths[secondGlyph].toInt() and 0xFF
		}
		@Pc(74) val firstProfile: ByteArray? = rightOffsets!![firstGlyph]
		@Pc(78) val secondProfile: ByteArray? = leftOffsets!![secondGlyph]
		@Pc(83) var firstIndex: Int = overlapStart - firstStart
		@Pc(88) var secondIndex: Int = overlapStart - secondStart
		for (@Pc(90) i in overlapStart until overlapEnd) {
			@Pc(102) val gap: Int = secondProfile!![secondIndex++].toInt() + firstProfile!![firstIndex++].toInt()
			if (minGap > gap) {
				minGap = gap
			}
		}
		return -minGap
	}

	@OriginalMember(owner = "client!cb", name = "a", descriptor = "(ILjava/lang/String;)J")
	@JvmStatic
	fun encodeBase37(@OriginalArg(1) string: String): Long {
		@Pc(7) var uid: Long = 0L
		@Pc(10) val length: Int = string.length
		for (@Pc(12) i in 0 until length) {
			uid *= 37L
			@Pc(22) val ch: Char = string[i]
			if (ch >= 'A' && ch <= 'Z') {
				uid += (ch.code + 1 - 65).toLong()
			} else if (ch >= 'a' && ch <= 'z') {
				uid += (ch.code + 1 - 97).toLong()
			} else if (ch >= '0' && ch <= '9') {
				uid += (ch.code + 27 - 48).toLong()
			}
			if (uid >= 177917621779460413L) {
				break
			}
		}
		while (uid % 37L == 0L && uid != 0L) {
			uid /= 37L
		}
		return uid
	}

	@OriginalMember(owner = "client!vr", name = "a", descriptor = "(BLjava/lang/String;)I")
	@JvmStatic
	fun getJagStringLength(@OriginalArg(1) string: String): Int {
		return string.length + 2
	}

	@OriginalMember(owner = "client!po", name = "a", descriptor = "([SI[Ljava/lang/String;)V")
	@JvmStatic
	fun sortByStrings(@OriginalArg(0) ids: ShortArray?, @OriginalArg(2) strings: Array<String?>) {
		ArrayUtils.quicksort(strings.size - 1, strings, ids, 0)
	}

	@OriginalMember(owner = "client!po", name = "a", descriptor = "(ZZILjava/lang/String;)Z")
	@JvmStatic
	fun isValidIntegerImpl(@OriginalArg(3) string: String): Boolean {
		@Pc(28) var negative: Boolean = false
		@Pc(30) var hasDigits: Boolean = false
		@Pc(32) var value: Int = 0
		@Pc(40) val length: Int = string.length
		for (@Pc(42) i in 0 until length) {
			@Pc(48) val c: Char = string[i]
			if (i == 0) {
				if (c == '-') {
					negative = true
					continue
				}
				if (c == '+') {
					continue
				}
			}
			@Pc(93) var digit: Int
			if (c >= '0' && c <= '9') {
				digit = c - '0'
			} else if (c >= 'A' && c <= 'Z') {
				digit = c - '7'
			} else if (c >= 'a' && c <= 'z') {
				digit = c - 'W'
			} else {
				return false
			}
			if (digit >= 10) {
				return false
			}
			if (negative) {
				digit = -digit
			}
			@Pc(118) val newValue: Int = value * 10 + digit
			if (value != newValue / 10) {
				return false
			}
			hasDigits = true
			value = newValue
		}
		return hasDigits
	}

	@OriginalMember(owner = "client!uga", name = "a", descriptor = "(Ljava/lang/String;BCLjava/lang/String;)Ljava/lang/String;")
	@JvmStatic
	fun replaceCarriageReturns(@OriginalArg(0) string: String, @OriginalArg(3) string2: String): String {
		@Pc(8) val length: Int = string.length
		@Pc(11) val replacementLength: Int = string2.length
		@Pc(13) var capacity: Int = length
		@Pc(17) val growthPerMatch: Int = replacementLength - 1
		if (growthPerMatch != 0) {
			@Pc(24) var searchIndex: Int = 0
			while (true) {
				searchIndex = string.indexOf(13.toChar(), searchIndex)
				if (searchIndex < 0) {
					break
				}
				searchIndex++
				capacity += growthPerMatch
			}
		}
		@Pc(43) val stringBuffer: StringBuffer = StringBuffer(capacity)
		@Pc(45) var start: Int = 0
		while (true) {
			@Pc(55) val end: Int = string.indexOf(13.toChar(), start)
			if (end < 0) {
				stringBuffer.append(string.substring(start))
				return stringBuffer.toString()
			}
			stringBuffer.append(string.substring(start, end))
			stringBuffer.append(string2)
			start = end + 1
		}
	}

	@OriginalMember(owner = "client!nga", name = "a", descriptor = "(IC)Z")
	@JvmStatic
	fun isCp1252Char(@OriginalArg(1) c: Char): Boolean {
		if (c > '\u0000' && c < '\u0080' || !(c < '\u00A0' || c > 'ÿ')) {
			return true
		}
		if (c != '\u0000') {
			@Pc(37) val unicodeTable: CharArray = Utils.UNICODE_TABLE
			for (@Pc(39) i in 0 until unicodeTable.size) {
				@Pc(45) val unicodeChar: Char = unicodeTable[i]
				if (unicodeChar == c) {
					return true
				}
			}
		}
		return false
	}

	@OriginalMember(owner = "client!oc", name = "a", descriptor = "(IIZ)Ljava/lang/String;")
	@JvmStatic
	fun toSignedString(@OriginalArg(0) value: Int, @OriginalArg(2) showPlusSign: Boolean): String {
		return if (showPlusSign && value >= 0) Utils.formatWithPlusSign(value, showPlusSign) else value.toString()
	}

	@OriginalMember(owner = "client!hs", name = "a", descriptor = "(BC)Z")
	@JvmStatic
	fun isNameSeparator(@OriginalArg(1) c: Char): Boolean {
		return c == '\u00A0' || c == ' ' || c == '_' || c == '-'
	}

	@OriginalMember(owner = "client!rs", name = "a", descriptor = "(IC)Z")
	@JvmStatic
	fun isAlphabetic(@OriginalArg(1) c: Char): Boolean {
		return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z'
	}

	@OriginalMember(owner = "client!ec", name = "a", descriptor = "(Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;")
	@JvmStatic
	fun replaceAll(@OriginalArg(0) string: String?, @OriginalArg(2) string2: String, @OriginalArg(3) string3: String): String {
		var string3: String = string3
		@Pc(9) var i: Int = string3.indexOf(string2)
		while (i != -1) {
			string3 = string3.substring(0, i) + string + string3.substring(string2.length + i)
			i = string3.indexOf(string2, string!!.length + i)
		}
		return string3
	}

	@OriginalMember(owner = "client!dp", name = "a", descriptor = "(IIB)I")
	@JvmStatic
	fun randomLogUniform(@OriginalArg(0) max: Int, @OriginalArg(1) min: Int): Int {
		@Pc(10) val logMax: Double = kotlin.math.ln(max.toDouble()) / kotlin.math.ln(2.0)
		@Pc(17) val logMin: Double = kotlin.math.ln(min.toDouble()) / kotlin.math.ln(2.0)
		@Pc(33) val exponent: Double = (logMax - logMin) * util.random() + logMin
		return (2.0.pow(exponent) + 0.5).toInt()
	}

	@OriginalMember(owner = "client!ei", name = "a", descriptor = "(IBI)I")
	@JvmStatic
	fun floorMod(@OriginalArg(0) value: Int, @OriginalArg(2) limit: Int): Int {
		@Pc(19) val offset: Int = (limit - 1) and (value shr 31)
		return (value + (value ushr 31)) % limit + offset
	}

	@OriginalMember(owner = "client!wd", name = "b", descriptor = "(Ljava/lang/String;I)Ljava/lang/String;")
	@JvmStatic
	fun escapeTags(@OriginalArg(0) string: String): String {
		@Pc(13) val length: Int = string.length
		@Pc(15) var extraLength: Int = 0
		for (@Pc(17) i in 0 until length) {
			@Pc(23) val c: Char = string[i]
			if (c == '<' || c == '>') {
				extraLength += 3
			}
		}
		@Pc(41) val stringBuffer: StringBuffer = StringBuffer(extraLength + length)
		for (@Pc(43) j in 0 until length) {
			@Pc(49) val c: Char = string[j]
			if (c == '<') {
				stringBuffer.append("<lt>")
			} else if (c == '>') {
				stringBuffer.append("<gt>")
			} else {
				stringBuffer.append(c)
			}
		}
		return stringBuffer.toString()
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(II)Ljava/lang/String;")
	@JvmStatic
	fun ipToString(@OriginalArg(1) intData1: Int): String {
		return ((intData1 shr 24) and 0xFF).toString() + "." + ((intData1 shr 16) and 0xFF) + "." + ((intData1 shr 8) and 0xFF) + "." + (intData1 and 0xFF)
	}

	@OriginalMember(owner = "client!wh", name = "a", descriptor = "(II)I")
	@JvmStatic
	fun bitLength(@OriginalArg(0) num: Int): Int {
		var num: Int = num
		@Pc(13) var sizeShift: Int = 0
		if (num < 0 || num >= 65536) {
			sizeShift += 16
			num = num ushr 0x10
		}
		if (num >= 256) {
			num = num ushr 0x8
			sizeShift += 8
		}
		if (num >= 16) {
			sizeShift += 4
			num = num ushr 0x4
		}
		if (num >= 4) {
			sizeShift += 2
			num = num ushr 0x2
		}
		if (num >= 1) {
			num = num ushr 0x1
			sizeShift++
		}
		return sizeShift + num
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(CI)Z")
	@JvmStatic
	fun isPrintableChar(@OriginalArg(0) c: Char): Boolean {
		if (c >= ' ' && c <= '~') {
			return true
		} else if (c >= '\u00A0' && c <= 'ÿ') {
			return true
		} else {
			return c == '€' || c == 'Œ' || c == '—' || c == 'œ' || c == 'Ÿ'
		}
	}

	@OriginalMember(owner = "client!sba", name = "a", descriptor = "(BJ)Ljava/lang/String;")
	@JvmStatic
	fun decodeUidName(@OriginalArg(1) uid: Long): String? {
		var uid: Long = uid
		if (uid <= 0L || uid >= 6582952005840035281L) {
			return null
		} else if (uid % 37L == 0L) {
			return null
		} else {
			@Pc(27) var length: Int = 0
			@Pc(29) var temp: Long = uid
			while (temp != 0L) {
				length++
				temp /= 37L
			}
			@Pc(53) val stringBuffer: StringBuffer = StringBuffer(length)
			while (uid != 0L) {
				@Pc(57) val previous: Long = uid
				uid /= 37L
				@Pc(71) var ch: Char = Utils.BASE37_CHARS[(previous - uid * 37L).toInt()]
				if (ch == '_') {
					@Pc(81) val lastIndex: Int = stringBuffer.length - 1
					stringBuffer.setCharAt(lastIndex, stringBuffer[lastIndex].uppercaseChar())
					ch = '\u00A0'
				}
				stringBuffer.append(ch)
			}
			stringBuffer.reverse()
			stringBuffer.setCharAt(0, stringBuffer[0].uppercaseChar())
			return stringBuffer.toString()
		}
	}

	@OriginalMember(owner = "client!vo", name = "a", descriptor = "([BBLjava/lang/String;I)I")
	@JvmStatic
	fun decodeBase64(@OriginalArg(0) dest: ByteArray?, @OriginalArg(2) string: String, @OriginalArg(3) offset: Int): Int {
		var offset: Int = offset
		@Pc(10) val length: Int = string.length
		@Pc(12) var i: Int = 0
		while (i < length) {
			@Pc(27) val sextet0: Int = Utils.base64CharValue(string[i])
			@Pc(42) val sextet1: Int = if (length > i + 1) Utils.base64CharValue(string[i + 1]) else -1
			@Pc(57) val sextet2: Int = if (i + 2 < length) Utils.base64CharValue(string[i + 2]) else -1
			@Pc(76) val sextet3: Int = if (i + 3 >= length) -1 else Utils.base64CharValue(string[i + 3])
			dest!![offset++] = ((sextet0 shl 2) or (sextet1 ushr 4)).toByte()
			if (sextet2 == -1) {
				break
			}
			dest[offset++] = (((sextet1 and 0xF) shl 4) or (sextet2 ushr 2)).toByte()
			if (sextet3 == -1) {
				break
			}
			dest[offset++] = (((sextet2 and 0x3) shl 6) or sextet3).toByte()
			i += 4
		}
		return offset
	}

	@OriginalMember(owner = "client!ti", name = "a", descriptor = "([BZ)Ljava/lang/String;")
	@JvmStatic
	fun bytesToString(@OriginalArg(0) buffer: ByteArray): String {
		return Utils.readString(buffer, buffer.size, 0)
	}

	@OriginalMember(owner = "client!vu", name = "a", descriptor = "(ILjava/lang/String;ILjava/lang/String;)I")
	@JvmStatic
	fun compareLocaleAware(@OriginalArg(1) string: String, @OriginalArg(2) language: Int, @OriginalArg(3) string2: String): Int {
		@Pc(8) val length1: Int = string2.length
		@Pc(11) val length2: Int = string.length
		@Pc(13) var index1: Int = 0
		@Pc(15) var index2: Int = 0
		@Pc(17) var pending1: Char = 0.toChar()
		@Pc(19) var pending2: Char = 0.toChar()
		while (length1 > index1 - pending1.code || index2 - pending2.code < length2) {
			if (index1 - pending1.code >= length1) {
				return -1
			}
			if (index2 - pending2.code >= length2) {
				return 1
			}
			@Pc(59) var c1: Char
			if (pending1 == '\u0000') {
				c1 = string2[index1++]
			} else {
				c1 = pending1
			}
			@Pc(72) var c2: Char
			if (pending2 == '\u0000') {
				c2 = string[index2++]
			} else {
				c2 = pending2
			}
			pending1 = Utils.getLigatureTail(c1)
			pending2 = Utils.getLigatureTail(c2)
			c1 = Utils.removeAccent(c1, language)
			c2 = Utils.removeAccent(c2, language)
			if (c2 != c1 && c1.uppercaseChar() != c2.uppercaseChar()) {
				c1 = c1.lowercaseChar()
				c2 = c2.lowercaseChar()
				if (c2 != c1) {
					return Utils.getCharSortKey(language, c1) - Utils.getCharSortKey(language, c2)
				}
			}
		}
		@Pc(145) val minLength: Int = kotlin.math.min(length1, length2)
		for (@Pc(147) i in 0 until minLength) {
			if (language == 2) {
				index2 = length2 - i - 1
				index1 = length1 - i - 1
			} else {
				index2 = i
				index1 = i
			}
			@Pc(176) var c1: Char = string2[index1]
			@Pc(180) var c2: Char = string[index2]
			if (c2 != c1 && c1.uppercaseChar() != c2.uppercaseChar()) {
				c1 = c1.lowercaseChar()
				c2 = c2.lowercaseChar()
				if (c2 != c1) {
					return Utils.getCharSortKey(language, c1) - Utils.getCharSortKey(language, c2)
				}
			}
		}
		@Pc(232) val lengthDiff: Int = length1 - length2
		if (lengthDiff != 0) {
			return lengthDiff
		}
		for (@Pc(241) j in 0 until minLength) {
			@Pc(247) val c1: Char = string2[j]
			@Pc(251) val c2: Char = string[j]
			if (c2 != c1) {
				return Utils.getCharSortKey(language, c1) - Utils.getCharSortKey(language, c2)
			}
		}
		return 0
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(IBI)I")
	@JvmStatic
	fun intPow(@OriginalArg(0) exponent: Int, @OriginalArg(2) base: Int): Int {
		var exponent: Int = exponent
		var base: Int = base
		@Pc(7) var result: Int = 1
		while (exponent > 1) {
			if ((exponent and 0x1) != 0) {
				result *= base
			}
			base *= base
			exponent = exponent shr 0x1
		}
		if (exponent == 1) {
			return result * base
		} else {
			return result
		}
	}

	@OriginalMember(owner = "client!ti", name = "a", descriptor = "([BIII)Ljava/lang/String;")
	@JvmStatic
	fun decodeUtf8(@OriginalArg(0) data: ByteArray?, @OriginalArg(1) offset: Int, @OriginalArg(3) length: Int): String {
		@Pc(8) val chars: CharArray = CharArray(length)
		@Pc(10) var charIndex: Int = 0
		@Pc(17) var byteIndex: Int = offset
		@Pc(21) val endIndex: Int = length + offset
		while (byteIndex < endIndex) {
			@Pc(30) val leadByte: Int = data!![byteIndex++].toInt() and 0xFF
			@Pc(39) var codePoint: Int
			if (leadByte < 128) {
				if (leadByte == 0) {
					codePoint = 65533
				} else {
					codePoint = leadByte
				}
			} else if (leadByte < 192) {
				codePoint = 65533
			} else if (leadByte >= 224) {
				if (leadByte < 240) {
					if (byteIndex + 1 < endIndex && (data[byteIndex].toInt() and 0xC0) == 128 && (data[byteIndex + 1].toInt() and 0xC0) == 128) {
						codePoint = ((leadByte and 0xF) shl 12) or ((data[byteIndex++].toInt() and 0x3F) shl 6) or (data[byteIndex++].toInt() and 0x3F)
						if (codePoint < 2048) {
							codePoint = 65533
						}
					} else {
						codePoint = 65533
					}
				} else if (leadByte >= 248) {
					codePoint = 65533
				} else if (endIndex > byteIndex + 2 && (data[byteIndex].toInt() and 0xC0) == 128 && (data[byteIndex + 1].toInt() and 0xC0) == 128 && (data[byteIndex + 2].toInt() and 0xC0) == 128) {
					codePoint = ((leadByte and 0x7) shl 18) or ((data[byteIndex++].toInt() and 0x3F) shl 12) or ((data[byteIndex++].toInt() and 0x3F) shl 6) or (data[byteIndex++].toInt() and 0x3F)
					if (codePoint >= 65536 && codePoint <= 1114111) {
						codePoint = 65533
					} else {
						codePoint = 65533
					}
				} else {
					codePoint = 65533
				}
			} else if (byteIndex < endIndex && (data[byteIndex].toInt() and 0xC0) == 128) {
				codePoint = (data[byteIndex++].toInt() and 0x3F) or ((leadByte and 0x1F) shl 6)
				if (codePoint < 128) {
					codePoint = 65533
				}
			} else {
				codePoint = 65533
			}
			chars[charIndex++] = codePoint.toChar()
		}
		return String(chars, 0, charIndex)
	}

	@OriginalMember(owner = "client!vp", name = "a", descriptor = "(CI)C")
	@JvmStatic
	fun normalizeNameChar(@OriginalArg(0) c: Char): Char {
		if (c == ' ' || c == '\u00A0' || c == '_' || c == '-') {
			return '_'
		} else if (c == '[' || c == ']' || c == '#') {
			return c
		} else if (c == 'à' || c == 'á' || c == 'â' || c == 'ä' || c == 'ã' || c == 'À' || c == 'Á' || c == 'Â' || c == 'Ä' || c == 'Ã') {
			return 'a'
		} else if (c == 'è' || c == 'é' || c == 'ê' || c == 'ë' || c == 'È' || c == 'É' || c == 'Ê' || c == 'Ë') {
			return 'e'
		} else if (c == 'í' || c == 'î' || c == 'ï' || c == 'Í' || c == 'Î' || c == 'Ï') {
			return 'i'
		} else if (c == 'ò' || c == 'ó' || c == 'ô' || c == 'ö' || c == 'õ' || c == 'Ò' || c == 'Ó' || c == 'Ô' || c == 'Ö' || c == 'Õ') {
			return 'o'
		} else if (c == 'ù' || c == 'ú' || c == 'û' || c == 'ü' || c == 'Ù' || c == 'Ú' || c == 'Û' || c == 'Ü') {
			return 'u'
		} else if (c == 'ç' || c == 'Ç') {
			return 'c'
		} else if (c == 'ÿ' || c == 'Ÿ') {
			return 'y'
		} else if (c == 'ñ' || c == 'Ñ') {
			return 'n'
		} else if (c == 'ß') {
			return 'b'
		} else {
			return c.lowercaseChar()
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(BILjava/util/Random;)I")
	@JvmStatic
	fun randomInt(@OriginalArg(1) limit: Int, @OriginalArg(2) random: Random?): Int {
		if (limit <= 0) {
			throw IllegalArgumentException()
		} else if (MipmapTextureSampler.isPowerOfTwo(limit)) {
			return ((limit.toLong() * (random!!.nextInt().toLong() and 0xFFFFFFFFL)) shr 32).toInt()
		} else {
			@Pc(34) val rejectionThreshold: Int = Int.MIN_VALUE - (4294967296L % limit.toLong()).toInt()
			@Pc(42) var randomInteger: Int
			do {
				randomInteger = random!!.nextInt()
			} while (rejectionThreshold <= randomInteger)
			return Utils.floorMod(randomInteger, limit)
		}
	}

	@OriginalMember(owner = "client!od", name = "a", descriptor = "(CII)C")
	@JvmStatic
	fun removeAccent(@OriginalArg(0) ch: Char, @OriginalArg(1) language: Int): Char {
		if (ch >= 'À' && ch <= 'ÿ') {
			if (ch >= 'À' && ch <= 'Æ') {
				return 'A'
			}
			if (ch == 'Ç') {
				return 'C'
			}
			if (ch >= 'È' && ch <= 'Ë') {
				return 'E'
			}
			if (ch >= 'Ì' && ch <= 'Ï') {
				return 'I'
			}
			if (ch >= 'Ò' && ch <= 'Ö') {
				return 'O'
			}
			if (ch >= 'Ù' && ch <= 'Ü') {
				return 'U'
			}
			if (ch == 'Ý') {
				return 'Y'
			}
			if (ch == 'ß') {
				return 's'
			}
			if (ch >= 'à' && ch <= 'æ') {
				return 'a'
			}
			if (ch == 'ç') {
				return 'c'
			}
			if (ch >= 'è' && ch <= 'ë') {
				return 'e'
			}
			if (ch >= 'ì' && ch <= 'ï') {
				return 'i'
			}
			if (ch >= 'ò' && ch <= 'ö') {
				return 'o'
			}
			if (ch >= 'ù' && ch <= 'ü') {
				return 'u'
			}
			if (ch == 'ý' || ch == 'ÿ') {
				return 'y'
			}
		}
		if (ch == 'Œ') {
			return 'O'
		} else if (ch == 'œ') {
			return 'o'
		} else if (ch == 'Ÿ') {
			return 'Y'
		} else {
			return ch
		}
	}

	@OriginalMember(owner = "client!kia", name = "a", descriptor = "(Ljava/lang/String;I)I")
	@JvmStatic
	fun getEncodedStringLength(@OriginalArg(0) string: String): Int {
		return string.length + 1
	}

	@OriginalMember(owner = "client!bf", name = "a", descriptor = "(IIC)I")
	@JvmStatic
	fun getCharSortKey(@OriginalArg(0) language: Int, @OriginalArg(2) ch: Char): Int {
		@Pc(12) var key: Int = ch.code shl 4
		if (ch.isUpperCase() || ch.isTitleCase()) {
			@Pc(21) val lowerCase: Char = ch.lowercaseChar()
			key = (lowerCase.code shl 4) + 1
		}
		return key
	}

	@OriginalMember(owner = "client!es", name = "a", descriptor = "(IZIJI)Ljava/lang/String;")
	@JvmStatic
	fun formatLocalisedNumber(@OriginalArg(0) decimalPlaces: Int, @OriginalArg(1) useThousandsSeparator: Boolean, @OriginalArg(2) language: Int, @OriginalArg(3) value: Long): String {
		var value: Long = value
		@Pc(7) var decimalSeparator: Char = ','
		@Pc(9) var thousandsSeparator: Char = '.'
		if (language == 0) {
			decimalSeparator = '.'
			thousandsSeparator = ','
		}
		if (language == 2) {
			thousandsSeparator = '\u00A0'
		}
		@Pc(27) var negative: Boolean = false
		if (value < 0L) {
			value = -value
			negative = true
		}
		@Pc(43) val stringBuffer: StringBuffer = StringBuffer(26)
		@Pc(50) var digitCount: Int
		@Pc(55) var previousValue: Int
		if (decimalPlaces > 0) {
			digitCount = 0
			while (digitCount < decimalPlaces) {
				previousValue = value.toInt()
				value /= 10L
				stringBuffer.append((previousValue + 48 - value.toInt() * 10).toChar())
				digitCount++
			}
			stringBuffer.append(decimalSeparator)
		}
		digitCount = 0
		while (true) {
			previousValue = value.toInt()
			value /= 10L
			stringBuffer.append((previousValue + 48 - value.toInt() * 10).toChar())
			if (value == 0L) {
				if (negative) {
					stringBuffer.append('-')
				}
				return stringBuffer.reverse().toString()
			}
			if (useThousandsSeparator) {
				digitCount++
				if (digitCount % 3 == 0) {
					stringBuffer.append(thousandsSeparator)
				}
			}
		}
	}

	@OriginalMember(owner = "client!aia", name = "e", descriptor = "(II)I")
	@JvmStatic
	fun floorPowerOfTwo(@OriginalArg(0) value: Int): Int {
		@Pc(12) val shifted: Int = value ushr 1
		@Pc(18) val smeared2: Int = shifted or (shifted ushr 1)
		@Pc(24) val smeared4: Int = smeared2 or (smeared2 ushr 2)
		@Pc(30) val smeared8: Int = smeared4 or (smeared4 ushr 4)
		@Pc(36) val smeared16: Int = smeared8 or (smeared8 ushr 8)
		@Pc(42) val smeared: Int = smeared16 or (smeared16 ushr 16)
		return smeared.inv() and value
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(Ljava/lang/String;B)I")
	@JvmStatic
	fun hashString(@OriginalArg(0) string: String): Int {
		@Pc(16) val length: Int = string.length
		@Pc(18) var hash: Int = 0
		for (@Pc(20) i in 0 until length) {
			hash = string[i].code + (hash shl 5) - hash
		}
		return hash
	}

	@OriginalMember(owner = "client!fca", name = "a", descriptor = "(IIZLjava/lang/String;)I")
	@JvmStatic
	fun parseIntRadix(@OriginalArg(1) radix: Int, @OriginalArg(3) string: String): Int {
		@Pc(24) var negative: Boolean = false
		@Pc(26) var hasDigits: Boolean = false
		@Pc(28) var result: Int = 0
		@Pc(39) val length: Int = string.length
		for (@Pc(41) i in 0 until length) {
			@Pc(47) val c: Char = string[i]
			if (i == 0) {
				if (c == '-') {
					negative = true
					continue
				}
				if (c == '+') {
					continue
				}
			}
			@Pc(74) var digit: Int
			if (c >= '0' && c <= '9') {
				digit = c - '0'
			} else if (c >= 'A' && c <= 'Z') {
				digit = c - '7'
			} else if (c >= 'a' && c <= 'z') {
				digit = c - 'W'
			} else {
				throw NumberFormatException()
			}
			if (radix <= digit) {
				throw NumberFormatException()
			}
			if (negative) {
				digit = -digit
			}
			@Pc(125) val next: Int = radix * result + digit
			if (result != next / radix) {
				throw NumberFormatException()
			}
			hasDigits = true
			result = next
		}
		if (!hasDigits) {
			throw NumberFormatException()
		}
		return result
	}

	@OriginalMember(owner = "client!iq", name = "f", descriptor = "[C")
	@JvmField
	val BASE37_CHARS: CharArray = charArrayOf('_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

	@OriginalMember(owner = "client!ko", name = "a", descriptor = "(IIIZ)Ljava/lang/String;")
	@JvmStatic
	fun formatWithPlusSign(@OriginalArg(2) value: Int, @OriginalArg(3) showPlusSign: Boolean): String {
		var value: Int = value
		if (!showPlusSign || value < 0) {
			return value.toString(10)
		}
		@Pc(35) var length: Int = 2
		@Pc(39) var quotient: Int = value / 10
		while (quotient != 0) {
			quotient /= 10
			length++
		}
		@Pc(51) val chars: CharArray = CharArray(length)
		chars[0] = '+'
		@Pc(59) var i: Int = length - 1
		while (i > 0) {
			@Pc(66) val previousValue: Int = value
			value /= 10
			@Pc(77) val digit: Int = previousValue - value * 10
			if (digit < 10) {
				chars[i] = (digit + 48).toChar()
			} else {
				chars[i] = (digit + 87).toChar()
			}
			i--
		}
		return String(chars)
	}

	@OriginalMember(owner = "client!fv", name = "a", descriptor = "(CI)Z")
	@JvmStatic
	fun isValidNameChar(@OriginalArg(0) c: Char): Boolean {
		if (c.isISOControl()) {
			return false
		} else if (Utils.isAlphanumeric(c)) {
			return true
		} else {
			@Pc(18) val allowedChars: CharArray = Utils.NAME_ALLOWED_CHARS
			for (@Pc(20) i in 0 until allowedChars.size) {
				@Pc(26) val allowedChar: Char = allowedChars[i]
				if (allowedChar == c) {
					return true
				}
			}
			@Pc(50) val allowedChars2: CharArray = Utils.NAME_ALLOWED_SYMBOLS
			for (@Pc(52) j in 0 until allowedChars2.size) {
				@Pc(58) val allowedChar2: Char = allowedChars2[j]
				if (c == allowedChar2) {
					return true
				}
			}
			return false
		}
	}

	@OriginalMember(owner = "client!iq", name = "a", descriptor = "(ILjava/lang/String;I)I")
	@JvmStatic
	fun parseHex(@OriginalArg(1) string: String): Int {
		return Utils.parseIntRadix(16, string)
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(CB)Z")
	@JvmStatic
	fun isAlphanumeric(@OriginalArg(0) c: Char): Boolean {
		return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z'
	}

	@OriginalMember(owner = "client!iba", name = "a", descriptor = "(IB)I")
	@JvmStatic
	fun nextPowerOfTwo(@OriginalArg(0) value: Int): Int {
		@Pc(4) val decremented: Int = value - 1
		@Pc(10) val smeared2: Int = decremented or (decremented ushr 1)
		@Pc(16) val smeared4: Int = smeared2 or (smeared2 ushr 2)
		@Pc(22) val smeared8: Int = smeared4 or (smeared4 ushr 4)
		@Pc(33) val smeared16: Int = smeared8 or (smeared8 ushr 8)
		@Pc(39) val smeared: Int = smeared16 or (smeared16 ushr 16)
		return smeared + 1
	}

	@OriginalMember(owner = "client!dk", name = "a", descriptor = "(ZC)Z")
	@JvmStatic
	fun isDigit(@OriginalArg(1) c: Char): Boolean {
		return c >= '0' && c <= '9'
	}

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(ILjava/lang/String;)Ljava/lang/String;")
	@JvmStatic
	fun formatDisplayName(@OriginalArg(1) string: String?): String? {
		if (string == null) {
			return null
		}
		@Pc(16) var start: Int = 0
		@Pc(19) var end: Int = string.length
		while (end > start && Utils.isNameSeparator(string[start])) {
			start++
		}
		while (end > start && Utils.isNameSeparator(string[end - 1])) {
			end--
		}
		@Pc(54) val length: Int = end - start
		if (length < 1 || length > 12) {
			return null
		}
		@Pc(69) val stringBuffer: StringBuffer = StringBuffer(length)
		for (@Pc(71) i in start until end) {
			@Pc(77) val character: Char = string[i]
			if (Utils.isValidNameChar(character)) {
				@Pc(87) val formatted: Char = Utils.normalizeNameChar(character)
				if (formatted != '\u0000') {
					stringBuffer.append(formatted)
				}
			}
		}
		if (stringBuffer.length == 0) {
			return null
		} else {
			return stringBuffer.toString()
		}
	}

	@OriginalMember(owner = "client!la", name = "a", descriptor = "(IC)I")
	@JvmStatic
	fun base64CharValue(@OriginalArg(1) ch: Char): Int {
		return if (ch >= '\u0000' && ch.code < MapSizePreference.DECODE_TABLE.size) MapSizePreference.DECODE_TABLE[ch.code] else -1
	}

	@OriginalMember(owner = "client!mda", name = "a", descriptor = "(Ljava/lang/String;I)[B")
	@JvmStatic
	fun base64Decode(@OriginalArg(0) string: String): ByteArray {
		@Pc(6) val inputLength: Int = string.length
		if (inputLength == 0) {
			return ByteArray(0)
		}
		@Pc(20) val paddedLength: Int = (inputLength + 3) and -4 // 0xFFFFFFFC
		@Pc(26) var outputLength: Int = paddedLength / 4 * 3
		if (paddedLength - 2 >= inputLength || Utils.base64CharValue(string[paddedLength - 2]) == -1) {
			outputLength -= 2
		} else if (inputLength <= paddedLength - 1 || Utils.base64CharValue(string[paddedLength - 1]) == -1) {
			outputLength--
		}
		@Pc(70) val output: ByteArray = ByteArray(outputLength)
		Utils.decodeBase64(output, string, 0)
		return output
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(CB)C")
	@JvmStatic
	fun getLigatureTail(@OriginalArg(0) c: Char): Char {
		if (c == 'Æ') {
			return 'E'
		} else if (c == 'æ') {
			return 'e'
		} else if (c == 'ß') {
			return 's'
		} else if (c == 'Œ') {
			return 'E'
		} else {
			return if (c == 'œ') 'e' else '\u0000'
		}
	}

	@OriginalMember(owner = "client!qf", name = "j", descriptor = "[C")
	@JvmField
	val NAME_ALLOWED_CHARS: CharArray = charArrayOf(' ', '\u00A0', '_', '-', 'à', 'á', 'â', 'ä', 'ã', 'À', 'Á', 'Â', 'Ä', 'Ã', 'è', 'é', 'ê', 'ë', 'È', 'É', 'Ê', 'Ë', 'í', 'î', 'ï', 'Í', 'Î', 'Ï', 'ò', 'ó', 'ô', 'ö', 'õ', 'Ò', 'Ó', 'Ô', 'Ö', 'Õ', 'ù', 'ú', 'û', 'ü', 'Ù', 'Ú', 'Û', 'Ü', 'ç', 'Ç', 'ÿ', 'Ÿ', 'ñ', 'Ñ', 'ß')

	@OriginalMember(owner = "client!mf", name = "a", descriptor = "(Ljava/lang/String;Z)Ljava/lang/String;")
	@JvmStatic
	fun urlDecode(@OriginalArg(0) string: String): String {
		@Pc(9) val stringBuffer: StringBuffer = StringBuffer()
		@Pc(12) val length: Int = string.length
		@Pc(19) var i: Int = 0
		while (i < length) {
			@Pc(25) var c: Char = string[i]
			if (c == '%' && i + 2 < length) {
				c = string[i + 1]
				@Pc(73) var decoded: Int
				if (c >= '0' && c <= '9') {
					decoded = c - '0'
				} else if (c >= 'a' && c <= 'f') {
					decoded = c.code + '\n'.code - 97
				} else {
					if (c < 'A' || c > 'F') {
						stringBuffer.append('%')
						i++
						continue
					}
					decoded = c.code - 55
				}
				c = string[i + 2]
				decoded *= 16
				if (c >= '0' && c <= '9') {
					decoded += c - '0'
				} else if (c >= 'a' && c <= 'f') {
					decoded += c.code + 10 - 'a'.code
				} else {
					if (c < 'A' || c > 'F') {
						stringBuffer.append('%')
						i++
						continue
					}
					decoded += c.code + '\n'.code - 65
				}
				if (decoded != 0 && Utils.isValidCp1252Byte(decoded.toByte())) {
					stringBuffer.append(Utils.cp1252ToChar(decoded.toByte()))
				}
				i += 2
			} else if (c == '+') {
				stringBuffer.append(' ')
			} else {
				stringBuffer.append(c)
			}
			i++
		}
		return stringBuffer.toString()
	}

	@OriginalMember(owner = "client!nq", name = "c", descriptor = "[C")
	@JvmField
	val NAME_ALLOWED_SYMBOLS: CharArray = charArrayOf('[', ']', '#')

	@OriginalMember(owner = "client!jw", name = "c", descriptor = "(I)J")
	@JvmStatic
	@Synchronized
	fun currentTimeMillis(): Long {
		@Pc(10) val now: Long = lang.currentTimeMillis()
		if (now < Utils.lastSystemTime) {
			Utils.timeOffset += Utils.lastSystemTime - now
		}
		Utils.lastSystemTime = now
		return now + Utils.timeOffset
	}
}
