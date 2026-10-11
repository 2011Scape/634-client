package com.jagex.game.runetek5.utils;

import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.sampler.MipmapTextureSampler;
import java.util.Random;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.preference.impl.MapSizePreference;

public final class Utils {
	@OriginalMember(owner = "client!aba", name = "G", descriptor = "J")
	public static long timeOffset;

	@OriginalMember(owner = "client!gq", name = "p", descriptor = "J")
	public static long lastSystemTime;

	@OriginalMember(owner = "client!ik", name = "e", descriptor = "[C")
	public static final char[] UNICODE_TABLE = new char[] { '€', '\u0000', '‚', 'ƒ', '„', '…', '†', '‡', 'ˆ', '‰', 'Š', '‹', 'Œ', '\u0000', 'Ž', '\u0000', '\u0000', '‘', '’', '“', '”', '•', '–', '—', '˜', '™', 'š', '›', 'œ', '\u0000', 'ž', 'Ÿ' };

	private Utils() {
		/* empty */
	}
	@OriginalMember(owner = "client!hc", name = "a", descriptor = "(Ljava/lang/String;I)Z")
	public static boolean isValidInteger(@OriginalArg(0) String string) {
		return Utils.isValidIntegerImpl(string);
	}

	@OriginalMember(owner = "client!eo", name = "a", descriptor = "(CZ)B")
	public static byte charToCp1252(@OriginalArg(0) char c) {
		@Pc(38) byte cp1252Byte;
		if (c > '\u0000' && c < '\u0080' || c >= ' ' && c <= 'ÿ') {
			cp1252Byte = (byte) c;
		} else if (c == '€') {
			cp1252Byte = -128;
		} else if (c == '‚') {
			cp1252Byte = -126;
		} else if (c == 'ƒ') {
			cp1252Byte = -125;
		} else if (c == '„') {
			cp1252Byte = -124;
		} else if (c == '…') {
			cp1252Byte = -123;
		} else if (c == '†') {
			cp1252Byte = -122;
		} else if (c == '‡') {
			cp1252Byte = -121;
		} else if (c == 'ˆ') {
			cp1252Byte = -120;
		} else if (c == '‰') {
			cp1252Byte = -119;
		} else if (c == 'Š') {
			cp1252Byte = -118;
		} else if (c == '‹') {
			cp1252Byte = -117;
		} else if (c == 'Œ') {
			cp1252Byte = -116;
		} else if (c == 'Ž') {
			cp1252Byte = -114;
		} else if (c == '‘') {
			cp1252Byte = -111;
		} else if (c == '’') {
			cp1252Byte = -110;
		} else if (c == '“') {
			cp1252Byte = -109;
		} else if (c == '”') {
			cp1252Byte = -108;
		} else if (c == '•') {
			cp1252Byte = -107;
		} else if (c == '–') {
			cp1252Byte = -106;
		} else if (c == '—') {
			cp1252Byte = -105;
		} else if (c == '˜') {
			cp1252Byte = -104;
		} else if (c == '™') {
			cp1252Byte = -103;
		} else if (c == 'š') {
			cp1252Byte = -102;
		} else if (c == '›') {
			cp1252Byte = -101;
		} else if (c == 'œ') {
			cp1252Byte = -100;
		} else if (c == 'ž') {
			cp1252Byte = -98;
		} else if (c == 'Ÿ') {
			cp1252Byte = -97;
		} else {
			cp1252Byte = 63;
		}
		return cp1252Byte;
	}

	@OriginalMember(owner = "client!jaa", name = "a", descriptor = "(BI)Z")
	public static boolean isValidCp1252Byte(@OriginalArg(0) byte b) {
		@Pc(7) int code = b & 0xFF;
		if (code == 0) {
			return false;
		} else {
			return code < 128 || code >= 160 || Utils.UNICODE_TABLE[code - 128] != '\u0000';
		}
	}

	@OriginalMember(owner = "client!gb", name = "a", descriptor = "(BI)C")
	public static char cp1252ToChar(@OriginalArg(0) byte b) {
		@Pc(7) int charCode = b & 0xFF;
		if (charCode == 0) {
			throw new IllegalArgumentException("Non cp1252 character 0x" + Integer.toString(charCode, 16) + " provided");
		}
		if (charCode >= 128 && charCode < 160) {
			@Pc(41) char unicodeChar = Utils.UNICODE_TABLE[charCode - 128];
			if (unicodeChar == '\u0000') {
				unicodeChar = '?';
			}
			charCode = unicodeChar;
		}
		return (char) charCode;
	}
	@OriginalMember(owner = "client!pea", name = "a", descriptor = "(ZI[Ljava/lang/String;I)Ljava/lang/String;")
	public static String joinStrings(@OriginalArg(1) int count, @OriginalArg(2) String[] strings, @OriginalArg(3) int offset) {
		if (count == 0) {
			return "";
		} else if (count == 1) {
			@Pc(19) String string = strings[offset];
			return string == null ? "null" : string.toString();
		} else {
			@Pc(30) int end = count + offset;
			@Pc(32) int totalLength = 0;
			for (@Pc(34) int i = offset; i < end; i++) {
				@Pc(40) String string2 = strings[i];
				if (string2 == null) {
					totalLength += 4;
				} else {
					totalLength += string2.length();
				}
			}
			@Pc(63) StringBuffer stringBuffer = new StringBuffer(totalLength);
			for (@Pc(65) int j = offset; j < end; j++) {
				@Pc(71) String string3 = strings[j];
				if (string3 == null) {
					stringBuffer.append("null");
				} else {
					stringBuffer.append(string3);
				}
			}
			return stringBuffer.toString();
		}
	}
	@OriginalMember(owner = "client!ffa", name = "a", descriptor = "(I[BIZLjava/lang/String;I)I")
	public static int encodeCp1252(@OriginalArg(1) byte[] buffer, @OriginalArg(2) int length, @OriginalArg(4) String string, @OriginalArg(5) int currentPosition) {
		@Pc(15) int charCount = length;
		for (@Pc(17) int i = 0; i < charCount; i++) {
			@Pc(25) char c = string.charAt(i);
			if (c > '\u0000' && c < '\u0080' || !(c < ' ' || c > 'ÿ')) {
				buffer[currentPosition + i] = (byte) c;
			} else if (c == '€') {
				buffer[i + currentPosition] = -128;
			} else if (c == '‚') {
				buffer[i + currentPosition] = -126;
			} else if (c == 'ƒ') {
				buffer[currentPosition + i] = -125;
			} else if (c == '„') {
				buffer[currentPosition + i] = -124;
			} else if (c == '…') {
				buffer[currentPosition + i] = -123;
			} else if (c == '†') {
				buffer[i + currentPosition] = -122;
			} else if (c == '‡') {
				buffer[i + currentPosition] = -121;
			} else if (c == 'ˆ') {
				buffer[currentPosition + i] = -120;
			} else if (c == '‰') {
				buffer[currentPosition + i] = -119;
			} else if (c == 'Š') {
				buffer[currentPosition + i] = -118;
			} else if (c == '‹') {
				buffer[i + currentPosition] = -117;
			} else if (c == 'Œ') {
				buffer[i + currentPosition] = -116;
			} else if (c == 'Ž') {
				buffer[currentPosition + i] = -114;
			} else if (c == '‘') {
				buffer[currentPosition + i] = -111;
			} else if (c == '’') {
				buffer[currentPosition + i] = -110;
			} else if (c == '“') {
				buffer[currentPosition + i] = -109;
			} else if (c == '”') {
				buffer[currentPosition + i] = -108;
			} else if (c == '•') {
				buffer[currentPosition + i] = -107;
			} else if (c == '–') {
				buffer[currentPosition + i] = -106;
			} else if (c == '—') {
				buffer[currentPosition + i] = -105;
			} else if (c == '˜') {
				buffer[i + currentPosition] = -104;
			} else if (c == '™') {
				buffer[i + currentPosition] = -103;
			} else if (c == 'š') {
				buffer[i + currentPosition] = -102;
			} else if (c == '›') {
				buffer[currentPosition + i] = -101;
			} else if (c == 'œ') {
				buffer[currentPosition + i] = -100;
			} else if (c == 'ž') {
				buffer[currentPosition + i] = -98;
			} else if (c == 'Ÿ') {
				buffer[currentPosition + i] = -97;
			} else {
				buffer[i + currentPosition] = 63;
			}
		}
		return charCount;
	}

	@OriginalMember(owner = "client!hea", name = "a", descriptor = "(ILjava/lang/String;C)I")
	public static int countChar(@OriginalArg(1) String string, @OriginalArg(2) char c) {
		@Pc(7) int count = 0;
		@Pc(15) int length = string.length();
		for (@Pc(17) int i = 0; i < length; i++) {
			if (c == string.charAt(i)) {
				count++;
			}
		}
		return count;
	}

	@OriginalMember(owner = "client!ht", name = "a", descriptor = "(JI)Ljava/lang/String;")
	public static String nameForLong(@OriginalArg(0) long encoded) {
		if (encoded <= 0L || encoded >= 6582952005840035281L) {
			return null;
		} else if (encoded % 37L == 0L) {
			return null;
		} else {
			@Pc(27) int length = 0;
			for (@Pc(29) long remaining = encoded; remaining != 0L; remaining /= 37L) {
				length++;
			}
			@Pc(52) StringBuffer stringBuffer = new StringBuffer(length);
			while (encoded != 0L) {
				@Pc(56) long previous = encoded;
				encoded /= 37L;
				stringBuffer.append(Utils.BASE37_CHARS[(int) (previous - encoded * 37L)]);
			}
			return stringBuffer.reverse().toString();
		}
	}
	@OriginalMember(owner = "client!ls", name = "a", descriptor = "(Ljava/lang/String;B)[B")
	public static byte[] toCp1252Bytes(@OriginalArg(0) String string) {
		@Pc(8) int length = string.length();
		@Pc(11) byte[] bytes = new byte[length];
		for (@Pc(18) int i = 0; i < length; i++) {
			@Pc(24) char c = string.charAt(i);
			if (c > '\u0000' && c < '\u0080' || !(c < ' ' || c > 'ÿ')) {
				bytes[i] = (byte) c;
			} else if (c == '€') {
				bytes[i] = -128;
			} else if (c == '‚') {
				bytes[i] = -126;
			} else if (c == 'ƒ') {
				bytes[i] = -125;
			} else if (c == '„') {
				bytes[i] = -124;
			} else if (c == '…') {
				bytes[i] = -123;
			} else if (c == '†') {
				bytes[i] = -122;
			} else if (c == '‡') {
				bytes[i] = -121;
			} else if (c == 'ˆ') {
				bytes[i] = -120;
			} else if (c == '‰') {
				bytes[i] = -119;
			} else if (c == 'Š') {
				bytes[i] = -118;
			} else if (c == '‹') {
				bytes[i] = -117;
			} else if (c == 'Œ') {
				bytes[i] = -116;
			} else if (c == 'Ž') {
				bytes[i] = -114;
			} else if (c == '‘') {
				bytes[i] = -111;
			} else if (c == '’') {
				bytes[i] = -110;
			} else if (c == '“') {
				bytes[i] = -109;
			} else if (c == '”') {
				bytes[i] = -108;
			} else if (c == '•') {
				bytes[i] = -107;
			} else if (c == '–') {
				bytes[i] = -106;
			} else if (c == '—') {
				bytes[i] = -105;
			} else if (c == '˜') {
				bytes[i] = -104;
			} else if (c == '™') {
				bytes[i] = -103;
			} else if (c == 'š') {
				bytes[i] = -102;
			} else if (c == '›') {
				bytes[i] = -101;
			} else if (c == 'œ') {
				bytes[i] = -100;
			} else if (c == 'ž') {
				bytes[i] = -98;
			} else if (c == 'Ÿ') {
				bytes[i] = -97;
			} else {
				bytes[i] = 63;
			}
		}
		return bytes;
	}

	@OriginalMember(owner = "client!uba", name = "a", descriptor = "(ZLjava/lang/String;)I")
	public static int parseInt(@OriginalArg(1) String string) {
		return Utils.parseIntRadix(10, string);
	}

	@OriginalMember(owner = "client!cc", name = "a", descriptor = "(CZLjava/lang/String;)[Ljava/lang/String;")
	public static String[] split(@OriginalArg(0) char delimiter, @OriginalArg(2) String string) {
		@Pc(10) int delimiterCount = Utils.countChar(string, delimiter);
		@Pc(15) String[] strings = new String[delimiterCount + 1];
		@Pc(17) int resultIndex = 0;
		@Pc(19) int searchStart = 0;
		for (@Pc(26) int i = 0; i < delimiterCount; i++) {
			@Pc(30) int delimiterPos;
			for (delimiterPos = searchStart; string.charAt(delimiterPos) != delimiter; delimiterPos++) {
			}
			strings[resultIndex++] = string.substring(searchStart, delimiterPos);
			searchStart = delimiterPos + 1;
		}
		strings[delimiterCount] = string.substring(searchStart);
		return strings;
	}

	@OriginalMember(owner = "client!vp", name = "a", descriptor = "(Ljava/lang/String;B)J")
	public static long hashStringLong(@OriginalArg(0) String string) {
		@Pc(8) int length = string.length();
		@Pc(10) long hash = 0L;
		for (@Pc(19) int i = 0; i < length; i++) {
			hash = (hash << 5) + (long) string.charAt(i) - hash;
		}
		return hash;
	}
	@OriginalMember(owner = "client!kl", name = "a", descriptor = "([BIII)Ljava/lang/String;")
	public static String readString(@OriginalArg(0) byte[] buffer, @OriginalArg(2) int length, @OriginalArg(3) int offset) {
		@Pc(8) char[] chars = new char[length];
		@Pc(10) int count = 0;
		for (@Pc(12) int i = 0; i < length; i++) {
			@Pc(22) int charCode = buffer[i + offset] & 0xFF;
			if (charCode != 0) {
				if (charCode >= 128 && charCode < 160) {
					@Pc(36) char unicodeChar = Utils.UNICODE_TABLE[charCode - 128];
					if (unicodeChar == '\u0000') {
						unicodeChar = '?';
					}
					charCode = unicodeChar;
				}
				chars[count++] = (char) charCode;
			}
		}
		return new String(chars, 0, count);
	}

	@OriginalMember(owner = "client!mfa", name = "a", descriptor = "([BIII[[B[[B[I[I)I")
	public static int calculateKerning(@OriginalArg(0) byte[] glyphWidths, @OriginalArg(2) int secondGlyph, @OriginalArg(3) int firstGlyph, @OriginalArg(4) byte[][] leftOffsets, @OriginalArg(5) byte[][] rightOffsets, @OriginalArg(6) int[] rowStarts, @OriginalArg(7) int[] rowCounts) {
		@Pc(17) int firstStart = rowStarts[firstGlyph];
		@Pc(23) int firstEnd = rowCounts[firstGlyph] + firstStart;
		@Pc(27) int secondStart = rowStarts[secondGlyph];
		@Pc(33) int secondEnd = rowCounts[secondGlyph] + secondStart;
		@Pc(35) int overlapStart = firstStart;
		if (secondStart > firstStart) {
			overlapStart = secondStart;
		}
		@Pc(46) int overlapEnd = firstEnd;
		if (secondEnd < firstEnd) {
			overlapEnd = secondEnd;
		}
		@Pc(57) int minGap = glyphWidths[firstGlyph] & 0xFF;
		if (minGap > (glyphWidths[secondGlyph] & 0xFF)) {
			minGap = glyphWidths[secondGlyph] & 0xFF;
		}
		@Pc(74) byte[] firstProfile = rightOffsets[firstGlyph];
		@Pc(78) byte[] secondProfile = leftOffsets[secondGlyph];
		@Pc(83) int firstIndex = overlapStart - firstStart;
		@Pc(88) int secondIndex = overlapStart - secondStart;
		for (@Pc(90) int i = overlapStart; i < overlapEnd; i++) {
			@Pc(102) int gap = secondProfile[secondIndex++] + firstProfile[firstIndex++];
			if (minGap > gap) {
				minGap = gap;
			}
		}
		return -minGap;
	}
	@OriginalMember(owner = "client!cb", name = "a", descriptor = "(ILjava/lang/String;)J")
	public static long encodeBase37(@OriginalArg(1) String string) {
		@Pc(7) long uid = 0L;
		@Pc(10) int length = string.length();
		for (@Pc(12) int i = 0; i < length; i++) {
			uid *= 37L;
			@Pc(22) char ch = string.charAt(i);
			if (ch >= 'A' && ch <= 'Z') {
				uid += ch + 1 - 65;
			} else if (ch >= 'a' && ch <= 'z') {
				uid += ch + 1 - 97;
			} else if (ch >= '0' && ch <= '9') {
				uid += ch + 27 - 48;
			}
			if (uid >= 177917621779460413L) {
				break;
			}
		}
		while (uid % 37L == 0L && uid != 0L) {
			uid /= 37L;
		}
		return uid;
	}

	@OriginalMember(owner = "client!vr", name = "a", descriptor = "(BLjava/lang/String;)I")
	public static int getJagStringLength(@OriginalArg(1) String string) {
		return string.length() + 2;
	}

	@OriginalMember(owner = "client!po", name = "a", descriptor = "([SI[Ljava/lang/String;)V")
	public static void sortByStrings(@OriginalArg(0) short[] ids, @OriginalArg(2) String[] strings) {
		ArrayUtils.quicksort(strings.length - 1, strings, ids, 0);
	}

	@OriginalMember(owner = "client!po", name = "a", descriptor = "(ZZILjava/lang/String;)Z")
	public static boolean isValidIntegerImpl(@OriginalArg(3) String string) {
		@Pc(28) boolean negative = false;
		@Pc(30) boolean hasDigits = false;
		@Pc(32) int value = 0;
		@Pc(40) int length = string.length();
		for (@Pc(42) int i = 0; i < length; i++) {
			@Pc(48) char c = string.charAt(i);
			if (i == 0) {
				if (c == '-') {
					negative = true;
					continue;
				}
				if (c == '+') {
					continue;
				}
			}
			@Pc(93) int digit;
			if (c >= '0' && c <= '9') {
				digit = c - '0';
			} else if (c >= 'A' && c <= 'Z') {
				digit = c - '7';
			} else if (c >= 'a' && c <= 'z') {
				digit = c - 'W';
			} else {
				return false;
			}
			if (digit >= 10) {
				return false;
			}
			if (negative) {
				digit = -digit;
			}
			@Pc(118) int newValue = value * 10 + digit;
			if (value != newValue / 10) {
				return false;
			}
			hasDigits = true;
			value = newValue;
		}
		return hasDigits;
	}

	@OriginalMember(owner = "client!uga", name = "a", descriptor = "(Ljava/lang/String;BCLjava/lang/String;)Ljava/lang/String;")
	public static String replaceCarriageReturns(@OriginalArg(0) String string, @OriginalArg(3) String string2) {
		@Pc(8) int length = string.length();
		@Pc(11) int replacementLength = string2.length();
		@Pc(13) int capacity = length;
		@Pc(17) int growthPerMatch = replacementLength - 1;
		if (growthPerMatch != 0) {
			@Pc(24) int searchIndex = 0;
			while (true) {
				searchIndex = string.indexOf(13, searchIndex);
				if (searchIndex < 0) {
					break;
				}
				searchIndex++;
				capacity += growthPerMatch;
			}
		}
		@Pc(43) StringBuffer stringBuffer = new StringBuffer(capacity);
		@Pc(45) int start = 0;
		while (true) {
			@Pc(55) int end = string.indexOf(13, start);
			if (end < 0) {
				stringBuffer.append(string.substring(start));
				return stringBuffer.toString();
			}
			stringBuffer.append(string.substring(start, end));
			stringBuffer.append(string2);
			start = end + 1;
		}
	}
	@OriginalMember(owner = "client!nga", name = "a", descriptor = "(IC)Z")
	public static boolean isCp1252Char(@OriginalArg(1) char c) {
		if (c > '\u0000' && c < '\u0080' || !(c < ' ' || c > 'ÿ')) {
			return true;
		}
		if (c != '\u0000') {
			@Pc(37) char[] unicodeTable = Utils.UNICODE_TABLE;
			for (@Pc(39) int i = 0; i < unicodeTable.length; i++) {
				@Pc(45) char unicodeChar = unicodeTable[i];
				if (unicodeChar == c) {
					return true;
				}
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!oc", name = "a", descriptor = "(IIZ)Ljava/lang/String;")
	public static String toSignedString(@OriginalArg(0) int value, @OriginalArg(2) boolean showPlusSign) {
		return showPlusSign && value >= 0 ? Utils.formatWithPlusSign(value, showPlusSign) : Integer.toString(value);
	}

	@OriginalMember(owner = "client!hs", name = "a", descriptor = "(BC)Z")
	public static boolean isNameSeparator(@OriginalArg(1) char c) {
		return c == ' ' || c == ' ' || c == '_' || c == '-';
	}

	@OriginalMember(owner = "client!rs", name = "a", descriptor = "(IC)Z")
	public static boolean isAlphabetic(@OriginalArg(1) char c) {
		return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
	}

	@OriginalMember(owner = "client!ec", name = "a", descriptor = "(Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;")
	public static String replaceAll(@OriginalArg(0) String string, @OriginalArg(2) String string2, @OriginalArg(3) String string3) {
		for (@Pc(9) int i = string3.indexOf(string2); i != -1; i = string3.indexOf(string2, string.length() + i)) {
			string3 = string3.substring(0, i) + string + string3.substring(string2.length() + i);
		}
		return string3;
	}
	@OriginalMember(owner = "client!dp", name = "a", descriptor = "(IIB)I")
	public static int randomLogUniform(@OriginalArg(0) int max, @OriginalArg(1) int min) {
		@Pc(10) double logMax = Math.log((double) max) / Math.log(2.0D);
		@Pc(17) double logMin = Math.log((double) min) / Math.log(2.0D);
		@Pc(33) double exponent = (logMax - logMin) * Math.random() + logMin;
		return (int) (Math.pow(2.0D, exponent) + 0.5D);
	}

	@OriginalMember(owner = "client!ei", name = "a", descriptor = "(IBI)I")
	public static int floorMod(@OriginalArg(0) int value, @OriginalArg(2) int limit) {
		@Pc(19) int offset = limit - 1 & value >> 31;
		return (value + (value >>> 31)) % limit + offset;
	}

	@OriginalMember(owner = "client!wd", name = "b", descriptor = "(Ljava/lang/String;I)Ljava/lang/String;")
	public static String escapeTags(@OriginalArg(0) String string) {
		@Pc(13) int length = string.length();
		@Pc(15) int extraLength = 0;
		for (@Pc(17) int i = 0; i < length; i++) {
			@Pc(23) char c = string.charAt(i);
			if (c == '<' || c == '>') {
				extraLength += 3;
			}
		}
		@Pc(41) StringBuffer stringBuffer = new StringBuffer(extraLength + length);
		for (@Pc(43) int j = 0; j < length; j++) {
			@Pc(49) char c = string.charAt(j);
			if (c == '<') {
				stringBuffer.append("<lt>");
			} else if (c == '>') {
				stringBuffer.append("<gt>");
			} else {
				stringBuffer.append(c);
			}
		}
		return stringBuffer.toString();
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(II)Ljava/lang/String;")
	public static String ipToString(@OriginalArg(1) int intData1) {
		return (intData1 >> 24 & 0xFF) + "." + (intData1 >> 16 & 0xFF) + "." + (intData1 >> 8 & 0xFF) + "." + (intData1 & 0xFF);
	}

	@OriginalMember(owner = "client!wh", name = "a", descriptor = "(II)I")
	public static int bitLength(@OriginalArg(0) int num) {
		@Pc(13) int sizeShift = 0;
		if (num < 0 || num >= 65536) {
			sizeShift += 16;
			num >>>= 0x10;
		}
		if (num >= 256) {
			num >>>= 0x8;
			sizeShift += 8;
		}
		if (num >= 16) {
			sizeShift += 4;
			num >>>= 0x4;
		}
		if (num >= 4) {
			sizeShift += 2;
			num >>>= 0x2;
		}
		if (num >= 1) {
			num >>>= 0x1;
			sizeShift++;
		}
		return sizeShift + num;
	}

	@OriginalMember(owner = "client!vaa", name = "a", descriptor = "(CI)Z")
	public static boolean isPrintableChar(@OriginalArg(0) char c) {
		if (c >= ' ' && c <= '~') {
			return true;
		} else if (c >= ' ' && c <= 'ÿ') {
			return true;
		} else {
			return c == '€' || c == 'Œ' || c == '—' || c == 'œ' || c == 'Ÿ';
		}
	}

	@OriginalMember(owner = "client!sba", name = "a", descriptor = "(BJ)Ljava/lang/String;")
	public static String decodeUidName(@OriginalArg(1) long uid) {
		if (uid <= 0L || uid >= 6582952005840035281L) {
			return null;
		} else if (uid % 37L == 0L) {
			return null;
		} else {
			@Pc(27) int length = 0;
			for (@Pc(29) long temp = uid; temp != 0L; temp /= 37L) {
				length++;
			}
			@Pc(53) StringBuffer stringBuffer = new StringBuffer(length);
			while (uid != 0L) {
				@Pc(57) long previous = uid;
				uid /= 37L;
				@Pc(71) char ch = Utils.BASE37_CHARS[(int) (previous - uid * 37L)];
				if (ch == '_') {
					@Pc(81) int lastIndex = stringBuffer.length() - 1;
					stringBuffer.setCharAt(lastIndex, Character.toUpperCase(stringBuffer.charAt(lastIndex)));
					ch = ' ';
				}
				stringBuffer.append(ch);
			}
			stringBuffer.reverse();
			stringBuffer.setCharAt(0, Character.toUpperCase(stringBuffer.charAt(0)));
			return stringBuffer.toString();
		}
	}

	@OriginalMember(owner = "client!vo", name = "a", descriptor = "([BBLjava/lang/String;I)I")
	public static int decodeBase64(@OriginalArg(0) byte[] dest, @OriginalArg(2) String string, @OriginalArg(3) int offset) {
		@Pc(10) int length = string.length();
		for (@Pc(12) int i = 0; i < length; i += 4) {
			@Pc(27) int sextet0 = Utils.base64CharValue(string.charAt(i));
			@Pc(42) int sextet1 = length > i + 1 ? Utils.base64CharValue(string.charAt(i + 1)) : -1;
			@Pc(57) int sextet2 = i + 2 < length ? Utils.base64CharValue(string.charAt(i + 2)) : -1;
			@Pc(76) int sextet3 = i + 3 >= length ? -1 : Utils.base64CharValue(string.charAt(i + 3));
			dest[offset++] = (byte) (sextet0 << 2 | sextet1 >>> 4);
			if (sextet2 == -1) {
				break;
			}
			dest[offset++] = (byte) ((sextet1 & 0xF) << 4 | sextet2 >>> 2);
			if (sextet3 == -1) {
				break;
			}
			dest[offset++] = (byte) ((sextet2 & 0x3) << 6 | sextet3);
		}
		return offset;
	}

	@OriginalMember(owner = "client!ti", name = "a", descriptor = "([BZ)Ljava/lang/String;")
	public static String bytesToString(@OriginalArg(0) byte[] buffer) {
		return Utils.readString(buffer, buffer.length, 0);
	}

	@OriginalMember(owner = "client!vu", name = "a", descriptor = "(ILjava/lang/String;ILjava/lang/String;)I")
	public static int compareLocaleAware(@OriginalArg(1) String string, @OriginalArg(2) int language, @OriginalArg(3) String string2) {
		@Pc(8) int length1 = string2.length();
		@Pc(11) int length2 = string.length();
		@Pc(13) int index1 = 0;
		@Pc(15) int index2 = 0;
		@Pc(17) char pending1 = 0;
		@Pc(19) char pending2 = 0;
		while (length1 > index1 - pending1 || index2 - pending2 < length2) {
			if (index1 - pending1 >= length1) {
				return -1;
			}
			if (index2 - pending2 >= length2) {
				return 1;
			}
			@Pc(59) char c1;
			if (pending1 == '\u0000') {
				c1 = string2.charAt(index1++);
			} else {
				c1 = pending1;
			}
			@Pc(72) char c2;
			if (pending2 == '\u0000') {
				c2 = string.charAt(index2++);
			} else {
				c2 = pending2;
			}
			pending1 = Utils.getLigatureTail(c1);
			pending2 = Utils.getLigatureTail(c2);
			c1 = Utils.removeAccent(c1, language);
			c2 = Utils.removeAccent(c2, language);
			if (c2 != c1 && Character.toUpperCase(c1) != Character.toUpperCase(c2)) {
				c1 = Character.toLowerCase(c1);
				c2 = Character.toLowerCase(c2);
				if (c2 != c1) {
					return Utils.getCharSortKey(language, c1) - Utils.getCharSortKey(language, c2);
				}
			}
		}
		@Pc(145) int minLength = Math.min(length1, length2);
		for (@Pc(147) int i = 0; i < minLength; i++) {
			if (language == 2) {
				index2 = length2 - i - 1;
				index1 = length1 - i - 1;
			} else {
				index2 = i;
				index1 = i;
			}
			@Pc(176) char c1 = string2.charAt(index1);
			@Pc(180) char c2 = string.charAt(index2);
			if (c2 != c1 && Character.toUpperCase(c1) != Character.toUpperCase(c2)) {
				c1 = Character.toLowerCase(c1);
				c2 = Character.toLowerCase(c2);
				if (c2 != c1) {
					return Utils.getCharSortKey(language, c1) - Utils.getCharSortKey(language, c2);
				}
			}
		}
		@Pc(232) int lengthDiff = length1 - length2;
		if (lengthDiff != 0) {
			return lengthDiff;
		}
		for (@Pc(241) int j = 0; j < minLength; j++) {
			@Pc(247) char c1 = string2.charAt(j);
			@Pc(251) char c2 = string.charAt(j);
			if (c2 != c1) {
				return Utils.getCharSortKey(language, c1) - Utils.getCharSortKey(language, c2);
			}
		}
		return 0;
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(IBI)I")
	public static int intPow(@OriginalArg(0) int exponent, @OriginalArg(2) int base) {
		@Pc(7) int result = 1;
		while (exponent > 1) {
			if ((exponent & 0x1) != 0) {
				result *= base;
			}
			base *= base;
			exponent >>= 0x1;
		}
		if (exponent == 1) {
			return result * base;
		} else {
			return result;
		}
	}

	@OriginalMember(owner = "client!ti", name = "a", descriptor = "([BIII)Ljava/lang/String;")
	public static String decodeUtf8(@OriginalArg(0) byte[] data, @OriginalArg(1) int offset, @OriginalArg(3) int length) {
		@Pc(8) char[] chars = new char[length];
		@Pc(10) int charIndex = 0;
		@Pc(17) int byteIndex = offset;
		@Pc(21) int endIndex = length + offset;
		while (byteIndex < endIndex) {
			@Pc(30) int leadByte = data[byteIndex++] & 0xFF;
			@Pc(39) int codePoint;
			if (leadByte < 128) {
				if (leadByte == 0) {
					codePoint = 65533;
				} else {
					codePoint = leadByte;
				}
			} else if (leadByte < 192) {
				codePoint = 65533;
			} else if (leadByte >= 224) {
				if (leadByte < 240) {
					if (byteIndex + 1 < endIndex && (data[byteIndex] & 0xC0) == 128 && (data[byteIndex + 1] & 0xC0) == 128) {
						codePoint = (leadByte & 0xF) << 12 | (data[byteIndex++] & 0x3F) << 6 | data[byteIndex++] & 0x3F;
						if (codePoint < 2048) {
							codePoint = 65533;
						}
					} else {
						codePoint = 65533;
					}
				} else if (leadByte >= 248) {
					codePoint = 65533;
				} else if (endIndex > byteIndex + 2 && (data[byteIndex] & 0xC0) == 128 && (data[byteIndex + 1] & 0xC0) == 128 && (data[byteIndex + 2] & 0xC0) == 128) {
					codePoint = (leadByte & 0x7) << 18 | (data[byteIndex++] & 0x3F) << 12 | (data[byteIndex++] & 0x3F) << 6 | data[byteIndex++] & 0x3F;
					if (codePoint >= 65536 && codePoint <= 1114111) {
						codePoint = 65533;
					} else {
						codePoint = 65533;
					}
				} else {
					codePoint = 65533;
				}
			} else if (byteIndex < endIndex && (data[byteIndex] & 0xC0) == 128) {
				codePoint = data[byteIndex++] & 0x3F | (leadByte & 0x1F) << 6;
				if (codePoint < 128) {
					codePoint = 65533;
				}
			} else {
				codePoint = 65533;
			}
			chars[charIndex++] = (char) codePoint;
		}
		return new String(chars, 0, charIndex);
	}

	@OriginalMember(owner = "client!vp", name = "a", descriptor = "(CI)C")
	public static char normalizeNameChar(@OriginalArg(0) char c) {
		if (c == ' ' || c == ' ' || c == '_' || c == '-') {
			return '_';
		} else if (c == '[' || c == ']' || c == '#') {
			return c;
		} else if (c == 'à' || c == 'á' || c == 'â' || c == 'ä' || c == 'ã' || c == 'À' || c == 'Á' || c == 'Â' || c == 'Ä' || c == 'Ã') {
			return 'a';
		} else if (c == 'è' || c == 'é' || c == 'ê' || c == 'ë' || c == 'È' || c == 'É' || c == 'Ê' || c == 'Ë') {
			return 'e';
		} else if (c == 'í' || c == 'î' || c == 'ï' || c == 'Í' || c == 'Î' || c == 'Ï') {
			return 'i';
		} else if (c == 'ò' || c == 'ó' || c == 'ô' || c == 'ö' || c == 'õ' || c == 'Ò' || c == 'Ó' || c == 'Ô' || c == 'Ö' || c == 'Õ') {
			return 'o';
		} else if (c == 'ù' || c == 'ú' || c == 'û' || c == 'ü' || c == 'Ù' || c == 'Ú' || c == 'Û' || c == 'Ü') {
			return 'u';
		} else if (c == 'ç' || c == 'Ç') {
			return 'c';
		} else if (c == 'ÿ' || c == 'Ÿ') {
			return 'y';
		} else if (c == 'ñ' || c == 'Ñ') {
			return 'n';
		} else if (c == 'ß') {
			return 'b';
		} else {
			return Character.toLowerCase(c);
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(BILjava/util/Random;)I")
	public static int randomInt(@OriginalArg(1) int limit, @OriginalArg(2) Random random) {
		if (limit <= 0) {
			throw new IllegalArgumentException();
		} else if (MipmapTextureSampler.isPowerOfTwo(limit)) {
			return (int) ((long) limit * ((long) random.nextInt() & 0xFFFFFFFFL) >> 32);
		} else {
			@Pc(34) int rejectionThreshold = Integer.MIN_VALUE - (int) (4294967296L % (long) limit);
			@Pc(42) int randomInteger;
			do {
				randomInteger = random.nextInt();
			} while (rejectionThreshold <= randomInteger);
			return Utils.floorMod(randomInteger, limit);
		}
	}
	@OriginalMember(owner = "client!od", name = "a", descriptor = "(CII)C")
	public static char removeAccent(@OriginalArg(0) char ch, @OriginalArg(1) int language) {
		if (ch >= 'À' && ch <= 'ÿ') {
			if (ch >= 'À' && ch <= 'Æ') {
				return 'A';
			}
			if (ch == 'Ç') {
				return 'C';
			}
			if (ch >= 'È' && ch <= 'Ë') {
				return 'E';
			}
			if (ch >= 'Ì' && ch <= 'Ï') {
				return 'I';
			}
			if (ch >= 'Ò' && ch <= 'Ö') {
				return 'O';
			}
			if (ch >= 'Ù' && ch <= 'Ü') {
				return 'U';
			}
			if (ch == 'Ý') {
				return 'Y';
			}
			if (ch == 'ß') {
				return 's';
			}
			if (ch >= 'à' && ch <= 'æ') {
				return 'a';
			}
			if (ch == 'ç') {
				return 'c';
			}
			if (ch >= 'è' && ch <= 'ë') {
				return 'e';
			}
			if (ch >= 'ì' && ch <= 'ï') {
				return 'i';
			}
			if (ch >= 'ò' && ch <= 'ö') {
				return 'o';
			}
			if (ch >= 'ù' && ch <= 'ü') {
				return 'u';
			}
			if (ch == 'ý' || ch == 'ÿ') {
				return 'y';
			}
		}
		if (ch == 'Œ') {
			return 'O';
		} else if (ch == 'œ') {
			return 'o';
		} else if (ch == 'Ÿ') {
			return 'Y';
		} else {
			return ch;
		}
	}

	@OriginalMember(owner = "client!kia", name = "a", descriptor = "(Ljava/lang/String;I)I")
	public static int getEncodedStringLength(@OriginalArg(0) String string) {
		return string.length() + 1;
	}

	@OriginalMember(owner = "client!bf", name = "a", descriptor = "(IIC)I")
	public static int getCharSortKey(@OriginalArg(0) int language, @OriginalArg(2) char ch) {
		@Pc(12) int key = ch << 4;
		if (Character.isUpperCase(ch) || Character.isTitleCase(ch)) {
			@Pc(21) char lowerCase = Character.toLowerCase(ch);
			key = (lowerCase << 4) + 1;
		}
		return key;
	}

	@OriginalMember(owner = "client!es", name = "a", descriptor = "(IZIJI)Ljava/lang/String;")
	public static String formatLocalisedNumber(@OriginalArg(0) int decimalPlaces, @OriginalArg(1) boolean useThousandsSeparator, @OriginalArg(2) int language, @OriginalArg(3) long value) {
		@Pc(7) char decimalSeparator = ',';
		@Pc(9) char thousandsSeparator = '.';
		if (language == 0) {
			decimalSeparator = '.';
			thousandsSeparator = ',';
		}
		if (language == 2) {
			thousandsSeparator = ' ';
		}
		@Pc(27) boolean negative = false;
		if (value < 0L) {
			value = -value;
			negative = true;
		}
		@Pc(43) StringBuffer stringBuffer = new StringBuffer(26);
		@Pc(50) int digitCount;
		@Pc(55) int previousValue;
		if (decimalPlaces > 0) {
			for (digitCount = 0; digitCount < decimalPlaces; digitCount++) {
				previousValue = (int) value;
				value /= 10L;
				stringBuffer.append((char) (previousValue + 48 - (int) value * 10));
			}
			stringBuffer.append(decimalSeparator);
		}
		digitCount = 0;
		while (true) {
			previousValue = (int) value;
			value /= 10L;
			stringBuffer.append((char) (previousValue + 48 - (int) value * 10));
			if (value == 0L) {
				if (negative) {
					stringBuffer.append('-');
				}
				return stringBuffer.reverse().toString();
			}
			if (useThousandsSeparator) {
				digitCount++;
				if (digitCount % 3 == 0) {
					stringBuffer.append(thousandsSeparator);
				}
			}
		}
	}

	@OriginalMember(owner = "client!aia", name = "e", descriptor = "(II)I")
	public static int floorPowerOfTwo(@OriginalArg(0) int value) {
		@Pc(12) int shifted = value >>> 1;
		@Pc(18) int smeared2 = shifted | shifted >>> 1;
		@Pc(24) int smeared4 = smeared2 | smeared2 >>> 2;
		@Pc(30) int smeared8 = smeared4 | smeared4 >>> 4;
		@Pc(36) int smeared16 = smeared8 | smeared8 >>> 8;
		@Pc(42) int smeared = smeared16 | smeared16 >>> 16;
		return ~smeared & value;
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(Ljava/lang/String;B)I")
	public static int hashString(@OriginalArg(0) String string) {
		@Pc(16) int length = string.length();
		@Pc(18) int hash = 0;
		for (@Pc(20) int i = 0; i < length; i++) {
			hash = string.charAt(i) + (hash << 5) - hash;
		}
		return hash;
	}

	@OriginalMember(owner = "client!fca", name = "a", descriptor = "(IIZLjava/lang/String;)I")
	public static int parseIntRadix(@OriginalArg(1) int radix, @OriginalArg(3) String string) {
		@Pc(24) boolean negative = false;
		@Pc(26) boolean hasDigits = false;
		@Pc(28) int result = 0;
		@Pc(39) int length = string.length();
		for (@Pc(41) int i = 0; i < length; i++) {
			@Pc(47) char c = string.charAt(i);
			if (i == 0) {
				if (c == '-') {
					negative = true;
					continue;
				}
				if (c == '+') {
					continue;
				}
			}
			@Pc(74) int digit;
			if (c >= '0' && c <= '9') {
				digit = c - '0';
			} else if (c >= 'A' && c <= 'Z') {
				digit = c - '7';
			} else if (c >= 'a' && c <= 'z') {
				digit = c - 'W';
			} else {
				throw new NumberFormatException();
			}
			if (radix <= digit) {
				throw new NumberFormatException();
			}
			if (negative) {
				digit = -digit;
			}
			@Pc(125) int next = radix * result + digit;
			if (result != next / radix) {
				throw new NumberFormatException();
			}
			hasDigits = true;
			result = next;
		}
		if (!hasDigits) {
			throw new NumberFormatException();
		}
		return result;
	}

	@OriginalMember(owner = "client!iq", name = "f", descriptor = "[C")
	public static final char[] BASE37_CHARS = new char[] { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	@OriginalMember(owner = "client!ko", name = "a", descriptor = "(IIIZ)Ljava/lang/String;")
	public static String formatWithPlusSign(@OriginalArg(2) int value, @OriginalArg(3) boolean showPlusSign) {
		if (!showPlusSign || value < 0) {
			return Integer.toString(value, 10);
		}
		@Pc(35) int length = 2;
		@Pc(39) int quotient = value / 10;
		while (quotient != 0) {
			quotient /= 10;
			length++;
		}
		@Pc(51) char[] chars = new char[length];
		chars[0] = '+';
		for (@Pc(59) int i = length - 1; i > 0; i--) {
			@Pc(66) int previousValue = value;
			value /= 10;
			@Pc(77) int digit = previousValue - value * 10;
			if (digit < 10) {
				chars[i] = (char) (digit + 48);
			} else {
				chars[i] = (char) (digit + 87);
			}
		}
		return new String(chars);
	}

	@OriginalMember(owner = "client!fv", name = "a", descriptor = "(CI)Z")
	public static boolean isValidNameChar(@OriginalArg(0) char c) {
		if (Character.isISOControl(c)) {
			return false;
		} else if (Utils.isAlphanumeric(c)) {
			return true;
		} else {
			@Pc(18) char[] allowedChars = Utils.NAME_ALLOWED_CHARS;
			for (@Pc(20) int i = 0; i < allowedChars.length; i++) {
				@Pc(26) char allowedChar = allowedChars[i];
				if (allowedChar == c) {
					return true;
				}
			}
			@Pc(50) char[] allowedChars2 = Utils.NAME_ALLOWED_SYMBOLS;
			for (@Pc(52) int j = 0; j < allowedChars2.length; j++) {
				@Pc(58) char allowedChar2 = allowedChars2[j];
				if (c == allowedChar2) {
					return true;
				}
			}
			return false;
		}
	}

	@OriginalMember(owner = "client!iq", name = "a", descriptor = "(ILjava/lang/String;I)I")
	public static int parseHex(@OriginalArg(1) String string) {
		return Utils.parseIntRadix(16, string);
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(CB)Z")
	public static boolean isAlphanumeric(@OriginalArg(0) char c) {
		return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
	}

	@OriginalMember(owner = "client!iba", name = "a", descriptor = "(IB)I")
	public static int nextPowerOfTwo(@OriginalArg(0) int value) {
		@Pc(4) int decremented = value - 1;
		@Pc(10) int smeared2 = decremented | decremented >>> 1;
		@Pc(16) int smeared4 = smeared2 | smeared2 >>> 2;
		@Pc(22) int smeared8 = smeared4 | smeared4 >>> 4;
		@Pc(33) int smeared16 = smeared8 | smeared8 >>> 8;
		@Pc(39) int smeared = smeared16 | smeared16 >>> 16;
		return smeared + 1;
	}

	@OriginalMember(owner = "client!dk", name = "a", descriptor = "(ZC)Z")
	public static boolean isDigit(@OriginalArg(1) char c) {
		return c >= '0' && c <= '9';
	}

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(ILjava/lang/String;)Ljava/lang/String;")
	public static String formatDisplayName(@OriginalArg(1) String string) {
		if (string == null) {
			return null;
		}
		@Pc(16) int start = 0;
		@Pc(19) int end = string.length();
		while (end > start && Utils.isNameSeparator(string.charAt(start))) {
			start++;
		}
		while (end > start && Utils.isNameSeparator(string.charAt(end - 1))) {
			end--;
		}
		@Pc(54) int length = end - start;
		if (length < 1 || length > 12) {
			return null;
		}
		@Pc(69) StringBuffer stringBuffer = new StringBuffer(length);
		for (@Pc(71) int i = start; i < end; i++) {
			@Pc(77) char character = string.charAt(i);
			if (Utils.isValidNameChar(character)) {
				@Pc(87) char formatted = Utils.normalizeNameChar(character);
				if (formatted != '\u0000') {
					stringBuffer.append(formatted);
				}
			}
		}
		if (stringBuffer.length() == 0) {
			return null;
		} else {
			return stringBuffer.toString();
		}
	}
	@OriginalMember(owner = "client!la", name = "a", descriptor = "(IC)I")
	public static int base64CharValue(@OriginalArg(1) char ch) {
		return ch >= '\u0000' && ch < MapSizePreference.DECODE_TABLE.length ? MapSizePreference.DECODE_TABLE[ch] : -1;
	}

	@OriginalMember(owner = "client!mda", name = "a", descriptor = "(Ljava/lang/String;I)[B")
	public static byte[] base64Decode(@OriginalArg(0) String string) {
		@Pc(6) int inputLength = string.length();
		if (inputLength == 0) {
			return new byte[0];
		}
		@Pc(20) int paddedLength = inputLength + 3 & 0xFFFFFFFC;
		@Pc(26) int outputLength = paddedLength / 4 * 3;
		if (paddedLength - 2 >= inputLength || Utils.base64CharValue(string.charAt(paddedLength - 2)) == -1) {
			outputLength -= 2;
		} else if (inputLength <= paddedLength - 1 || Utils.base64CharValue(string.charAt(paddedLength - 1)) == -1) {
			outputLength--;
		}
		@Pc(70) byte[] output = new byte[outputLength];
		Utils.decodeBase64(output, string, 0);
		return output;
	}

	@OriginalMember(owner = "client!nda", name = "a", descriptor = "(CB)C")
	public static char getLigatureTail(@OriginalArg(0) char c) {
		if (c == 'Æ') {
			return 'E';
		} else if (c == 'æ') {
			return 'e';
		} else if (c == 'ß') {
			return 's';
		} else if (c == 'Œ') {
			return 'E';
		} else {
			return (char) (c == 'œ' ? 'e' : '\u0000');
		}
	}

	@OriginalMember(owner = "client!qf", name = "j", descriptor = "[C")
	public static final char[] NAME_ALLOWED_CHARS = new char[] { ' ', ' ', '_', '-', 'à', 'á', 'â', 'ä', 'ã', 'À', 'Á', 'Â', 'Ä', 'Ã', 'è', 'é', 'ê', 'ë', 'È', 'É', 'Ê', 'Ë', 'í', 'î', 'ï', 'Í', 'Î', 'Ï', 'ò', 'ó', 'ô', 'ö', 'õ', 'Ò', 'Ó', 'Ô', 'Ö', 'Õ', 'ù', 'ú', 'û', 'ü', 'Ù', 'Ú', 'Û', 'Ü', 'ç', 'Ç', 'ÿ', 'Ÿ', 'ñ', 'Ñ', 'ß' };

	@OriginalMember(owner = "client!mf", name = "a", descriptor = "(Ljava/lang/String;Z)Ljava/lang/String;")
	public static String urlDecode(@OriginalArg(0) String string) {
		@Pc(9) StringBuffer stringBuffer = new StringBuffer();
		@Pc(12) int length = string.length();
		for (@Pc(19) int i = 0; i < length; i++) {
			@Pc(25) char c = string.charAt(i);
			if (c == '%' && i + 2 < length) {
				c = string.charAt(i + 1);
				@Pc(73) int decoded;
				if (c >= '0' && c <= '9') {
					decoded = c - '0';
				} else if (c >= 'a' && c <= 'f') {
					decoded = c + '\n' - 97;
				} else {
					if (c < 'A' || c > 'F') {
						stringBuffer.append('%');
						continue;
					}
					decoded = c - 55;
				}
				c = string.charAt(i + 2);
				decoded *= 16;
				if (c >= '0' && c <= '9') {
					decoded += c - '0';
				} else if (c >= 'a' && c <= 'f') {
					decoded += c + 10 - 'a';
				} else {
					if (c < 'A' || c > 'F') {
						stringBuffer.append('%');
						continue;
					}
					decoded += c + '\n' - 65;
				}
				if (decoded != 0 && Utils.isValidCp1252Byte((byte) decoded)) {
					stringBuffer.append(Utils.cp1252ToChar((byte) decoded));
				}
				i += 2;
			} else if (c == '+') {
				stringBuffer.append(' ');
			} else {
				stringBuffer.append(c);
			}
		}
		return stringBuffer.toString();
	}

	@OriginalMember(owner = "client!nq", name = "c", descriptor = "[C")
	public static final char[] NAME_ALLOWED_SYMBOLS = new char[] { '[', ']', '#' };

	@OriginalMember(owner = "client!jw", name = "c", descriptor = "(I)J")
	public static synchronized long currentTimeMillis() {
		@Pc(10) long now = System.currentTimeMillis();
		if (now < Utils.lastSystemTime) {
			Utils.timeOffset += Utils.lastSystemTime - now;
		}
		Utils.lastSystemTime = now;
		return now + Utils.timeOffset;
	}
}
