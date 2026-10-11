package com.jagex.game.runetek5.buffer;

import com.jagex.game.runetek5.cache.node.Node;
import java.math.BigInteger;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.GlxProgram;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!cea")
public class Packet extends Node {

	@OriginalMember(owner = "client!cea", name = "wb", descriptor = "I")
	public int currentPosition;

	@OriginalMember(owner = "client!cea", name = "F", descriptor = "[B")
	public byte[] buffer;

	@OriginalMember(owner = "client!cea", name = "<init>", descriptor = "(I)V")
	public Packet(@OriginalArg(0) int size) {
		this.currentPosition = 0;
		this.buffer = CircularBuffer.allocatePacketBuffer(size);
	}

	@OriginalMember(owner = "client!cea", name = "<init>", descriptor = "([B)V")
	public Packet(@OriginalArg(0) byte[] src) {
		this.buffer = src;
		this.currentPosition = 0;
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(B)I")
	public final int readUByteInverse() {
		return -this.buffer[this.currentPosition++] & 0xFF;
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(I)I")
	public final int readUShort() {
		this.currentPosition += 2;
		return (this.buffer[this.currentPosition - 1] & 0xFF) + ((this.buffer[this.currentPosition - 2] & 0xFF) << 8);
	}

	@OriginalMember(owner = "client!cea", name = "b", descriptor = "(B)J")
	public final long readULongLittle() {
		@Pc(15) long low = (long) this.readUIntLittle() & 0xFFFFFFFFL;
		@Pc(22) long high = (long) this.readUIntLittle() & 0xFFFFFFFFL;
		return (high << 32) + low;
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(BLjava/lang/String;)V")
	public final void writeString(@OriginalArg(1) String string) {
		@Pc(13) int nulIndex = string.indexOf(0);
		if (nulIndex >= 0) {
			throw new IllegalArgumentException("NUL character at " + nulIndex + " - cannot pjstr");
		}
		this.currentPosition += Utils.encodeCp1252(this.buffer, string.length(), string, this.currentPosition);
		this.buffer[this.currentPosition++] = 0;
	}

	@OriginalMember(owner = "client!cea", name = "b", descriptor = "(I)I")
	public final int readSum() {
		@Pc(7) int total = 0;
		@Pc(11) int part;
		for (part = this.readUSmart(); part == 32767; part = this.readUSmart()) {
			total += 32767;
		}
		return total + part;
	}

	@OriginalMember(owner = "client!cea", name = "c", descriptor = "(II)V")
	public final void writeShortAdd(@OriginalArg(1) int value) {
		this.buffer[this.currentPosition++] = (byte) (value >> 8);
		this.buffer[this.currentPosition++] = (byte) (value + 128);
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(IB)V")
	public final void writeIntMiddle(@OriginalArg(0) int value) {
		this.buffer[this.currentPosition++] = (byte) (value >> 8);
		this.buffer[this.currentPosition++] = (byte) value;
		this.buffer[this.currentPosition++] = (byte) (value >> 24);
		this.buffer[this.currentPosition++] = (byte) (value >> 16);
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(BI)V")
	public final void writeShort(@OriginalArg(1) int value) {
		this.buffer[this.currentPosition++] = (byte) (value >> 8);
		this.buffer[this.currentPosition++] = (byte) value;
	}

	@OriginalMember(owner = "client!cea", name = "d", descriptor = "(II)V")
	public final void writeIntLength(@OriginalArg(1) int length) {
		this.buffer[this.currentPosition - length - 4] = (byte) (length >> 24);
		this.buffer[this.currentPosition - length - 3] = (byte) (length >> 16);
		this.buffer[this.currentPosition - length - 2] = (byte) (length >> 8);
		this.buffer[this.currentPosition - length - 1] = (byte) length;
	}

	@OriginalMember(owner = "client!cea", name = "e", descriptor = "(II)V")
	public final void writeIndex(@OriginalArg(1) int length) {
		this.buffer[this.currentPosition - length - 1] = (byte) length;
	}

	@OriginalMember(owner = "client!cea", name = "f", descriptor = "(II)V")
	public final void writeMedium(@OriginalArg(1) int value) {
		this.buffer[this.currentPosition++] = (byte) (value >> 16);
		this.buffer[this.currentPosition++] = (byte) (value >> 8);
		this.buffer[this.currentPosition++] = (byte) value;
	}

	@OriginalMember(owner = "client!cea", name = "d", descriptor = "(I)B")
	public final byte readByteAdd() {
		return (byte) (this.buffer[this.currentPosition++] - 128);
	}

	@OriginalMember(owner = "client!cea", name = "e", descriptor = "(B)I")
	public final int readUByteSubtract() {
		return 128 - this.buffer[this.currentPosition++] & 0xFF;
	}

	@OriginalMember(owner = "client!cea", name = "f", descriptor = "(B)I")
	public final int readUIntMiddle() {
		this.currentPosition += 4;
		return ((this.buffer[this.currentPosition - 1] & 0xFF) << 16) + (this.buffer[this.currentPosition - 2] << 24 & 0xFF000000) + ((this.buffer[this.currentPosition + -4] & 0xFF) << 8) + (this.buffer[this.currentPosition - 3] & 0xFF);
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(IZ)I")
	public final int writeCrc(@OriginalArg(0) int start) {
		@Pc(17) int crc = Packet.getCrc32(this.currentPosition, this.buffer, start);
		this.writeInt(crc);
		return crc;
	}

	@OriginalMember(owner = "client!cea", name = "e", descriptor = "(I)B")
	public final byte readByteSubtract() {
		return (byte) (128 - this.buffer[this.currentPosition++]);
	}

	@OriginalMember(owner = "client!cea", name = "f", descriptor = "(I)I")
	public final int readUIntInverseMiddle() {
		this.currentPosition += 4;
		return ((this.buffer[this.currentPosition - 1] & 0xFF) << 8) + ((this.buffer[this.currentPosition - 3] & 0xFF) << 24) + ((this.buffer[this.currentPosition - 4] & 0xFF) << 16) + (this.buffer[this.currentPosition - 2] & 0xFF);
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "([BIIB)V")
	public final void readBytesReversed(@OriginalArg(0) byte[] bytes, @OriginalArg(2) int length) {
		for (@Pc(11) int i = length - 1; i >= 0; i--) {
			bytes[i] = this.buffer[this.currentPosition++];
		}
	}

	@OriginalMember(owner = "client!cea", name = "g", descriptor = "(II)V")
	public final void writeIntLittle(@OriginalArg(1) int value) {
		this.buffer[this.currentPosition++] = (byte) value;
		this.buffer[this.currentPosition++] = (byte) (value >> 8);
		this.buffer[this.currentPosition++] = (byte) (value >> 16);
		this.buffer[this.currentPosition++] = (byte) (value >> 24);
	}

	@OriginalMember(owner = "client!cea", name = "h", descriptor = "(II)V")
	public final void writeShortAddLittle(@OriginalArg(1) int value) {
		this.buffer[this.currentPosition++] = (byte) (value + 128);
		this.buffer[this.currentPosition++] = (byte) (value >> 8);
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(IZ[II)V")
	public final void encryptXtea(@OriginalArg(0) int start, @OriginalArg(2) int[] keys, @OriginalArg(3) int end) {
		@Pc(8) int savedPosition = this.currentPosition;
		this.currentPosition = start;
		@Pc(18) int blockCount = (end - start) / 8;
		for (@Pc(20) int i = 0; i < blockCount; i++) {
			@Pc(26) int v0 = this.readInt();
			@Pc(30) int v1 = this.readInt();
			@Pc(32) int sum = 0;
			@Pc(36) int rounds = 32;
			while (rounds-- > 0) {
				v0 += sum + keys[sum & 0x3] ^ (v1 >>> 5 ^ v1 << 4) - -v1;
				sum += -1640531527;
				v1 += sum + keys[sum >>> 11 & 0x3] ^ v0 + (v0 >>> 5 ^ v0 << 4);
			}
			this.currentPosition -= 8;
			this.writeInt(v0);
			this.writeInt(v1);
		}
		this.currentPosition = savedPosition;
	}

	@OriginalMember(owner = "client!cea", name = "g", descriptor = "(I)I")
	public final int readUMedium() {
		this.currentPosition += 3;
		return ((this.buffer[this.currentPosition - 2] & 0xFF) << 8) + (((this.buffer[this.currentPosition - 3] & 0xFF) << 16) + (this.buffer[this.currentPosition - 1] & 0xFF));
	}

	@OriginalMember(owner = "client!cea", name = "h", descriptor = "(I)Z")
	public final boolean checkCrc() {
		this.currentPosition -= 4;
		@Pc(24) int calculatedCrc = Packet.getCrc32(this.currentPosition, this.buffer, 0);
		@Pc(28) int expectedCrc = this.readInt();
		return calculatedCrc == expectedCrc;
	}

	@OriginalMember(owner = "client!cea", name = "b", descriptor = "(IB)V")
	public final void writeShortLittle(@OriginalArg(0) int value) {
		this.buffer[this.currentPosition++] = (byte) value;
		this.buffer[this.currentPosition++] = (byte) (value >> 8);
	}

	@OriginalMember(owner = "client!cea", name = "j", descriptor = "(I)I")
	public final int readUShortAdd() {
		this.currentPosition += 2;
		return ((this.buffer[this.currentPosition - 2] & 0xFF) << 8) + (this.buffer[this.currentPosition - 1] - 128 & 0xFF);
	}

	@OriginalMember(owner = "client!cea", name = "i", descriptor = "(II)V")
	public final void writeIntInverseMiddle(@OriginalArg(0) int value) {
		this.buffer[this.currentPosition++] = (byte) (value >> 16);
		this.buffer[this.currentPosition++] = (byte) (value >> 24);
		this.buffer[this.currentPosition++] = (byte) value;
		this.buffer[this.currentPosition++] = (byte) (value >> 8);
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(IJB)V")
	public final void writeVariableLengthLong(@OriginalArg(0) int size, @OriginalArg(1) long value) {
		@Pc(6) int lastByte = size - 1;
		if (lastByte < 0 || lastByte > 7) {
			throw new IllegalArgumentException();
		}
		for (@Pc(32) int i = lastByte * 8; i >= 0; i -= 8) {
			this.buffer[this.currentPosition++] = (byte) (value >> i);
		}
	}

	@OriginalMember(owner = "client!cea", name = "k", descriptor = "(I)I")
	public final int readUShortAddLittle() {
		this.currentPosition += 2;
		return (this.buffer[this.currentPosition - 2] - 128 & 0xFF) + ((this.buffer[this.currentPosition - 1] & 0xFF) << 8);
	}

	@OriginalMember(owner = "client!cea", name = "l", descriptor = "(I)I")
	public final int readUIntLittle() {
		this.currentPosition += 4;
		return ((this.buffer[this.currentPosition - 3] & 0xFF) << 8) + (this.buffer[this.currentPosition - 1] << 24 & 0xFF000000) + ((this.buffer[this.currentPosition + -2] & 0xFF) << 16) + (this.buffer[this.currentPosition + -4] & 0xFF);
	}

	@OriginalMember(owner = "client!cea", name = "g", descriptor = "(B)V")
	public final void flush() {
		if (this.buffer != null) {
			CircularBuffer.releasePacketBuffer(this.buffer);
		}
		this.buffer = null;
	}

	@OriginalMember(owner = "client!cea", name = "m", descriptor = "(I)B")
	public final byte readByteInverse() {
		return (byte) -this.buffer[this.currentPosition++];
	}

	@OriginalMember(owner = "client!cea", name = "h", descriptor = "(B)I")
	public final int readSmart() {
		@Pc(11) int peek = this.buffer[this.currentPosition] & 0xFF;
		return peek < 128 ? this.readUByte() - 64 : this.readUShort() - 49152;
	}

	@OriginalMember(owner = "client!cea", name = "n", descriptor = "(I)I")
	public final int readUMediumLittle() {
		this.currentPosition += 3;
		return (this.buffer[this.currentPosition - 3] & 0xFF) + ((this.buffer[this.currentPosition - 2] & 0xFF) << 8) + ((this.buffer[this.currentPosition + -1] & 0xFF) << 16);
	}

	@OriginalMember(owner = "client!cea", name = "i", descriptor = "(B)I")
	public final int readIntLE() {
		this.currentPosition += 4;
		return (this.buffer[this.currentPosition - 4] & 0xFF) + ((this.buffer[this.currentPosition - 3] & 0xFF) << 8) + ((this.buffer[this.currentPosition - 1] & 0xFF) << 24) + ((this.buffer[this.currentPosition - 2] & 0xFF) << 16);
	}

	@OriginalMember(owner = "client!cea", name = "o", descriptor = "(I)J")
	public final long readLong() {
		@Pc(15) long high = (long) this.readInt() & 0xFFFFFFFFL;
		@Pc(22) long low = (long) this.readInt() & 0xFFFFFFFFL;
		return low + (high << 32);
	}

	@OriginalMember(owner = "client!cea", name = "j", descriptor = "(B)I")
	public final int readVarInt() {
		@Pc(14) byte current = this.buffer[this.currentPosition++];
		@Pc(16) int value = 0;
		while (current < 0) {
			value = (current & 0x7F | value) << 7;
			current = this.buffer[this.currentPosition++];
		}
		return value | current;
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(I[III)V")
	public final void decryptXtea(@OriginalArg(1) int[] keys, @OriginalArg(3) int length) {
		@Pc(8) int currentPosition = this.currentPosition;
		this.currentPosition = 5;
		@Pc(18) int blockCount = (length - 5) / 8;
		for (@Pc(20) int i = 0; i < blockCount; i++) {
			@Pc(26) int v0 = this.readInt();
			@Pc(30) int v1 = this.readInt();
			@Pc(32) int sum = -957401312;
			@Pc(36) int rounds = 32;
			while (rounds-- > 0) {
				v1 -= (v0 << 4 ^ v0 >>> 5) + v0 ^ sum - -keys[sum >>> 11 & 0x4D000003];
				sum -= -1640531527;
				v0 -= sum + keys[sum & 0x3] ^ (v1 << 4 ^ v1 >>> 5) - -v1;
			}
			this.currentPosition -= 8;
			this.writeInt(v0);
			this.writeInt(v1);
		}
		this.currentPosition = currentPosition;
	}

	@OriginalMember(owner = "client!cea", name = "j", descriptor = "(II)J")
	public final long bitpackLong(@OriginalArg(0) int size) {
		@Pc(2) int lastByte = size - 1;
		if (lastByte < 0 || lastByte > 7) {
			throw new IllegalArgumentException();
		}
		@Pc(26) int shift = lastByte * 8;
		@Pc(28) long value = 0L;
		while (shift >= 0) {
			value |= ((long) this.buffer[this.currentPosition++] & 0xFFL) << shift;
			shift -= 8;
		}
		return value;
	}

	@OriginalMember(owner = "client!cea", name = "k", descriptor = "(B)I")
	public final int readMedium() {
		this.currentPosition += 3;
		@Pc(48) int value = (this.buffer[this.currentPosition - 1] & 0xFF) + ((this.buffer[this.currentPosition - 3] & 0xFF) << 16) + ((this.buffer[this.currentPosition + -2] & 0xFF) << 8);
		if (value > 8388607) {
			value -= 16777216;
		}
		return value;
	}

	@OriginalMember(owner = "client!cea", name = "b", descriptor = "(BI)V")
	public final void writeByteSubtract(@OriginalArg(1) int value) {
		this.buffer[this.currentPosition++] = (byte) (128 - value);
	}

	@OriginalMember(owner = "client!cea", name = "p", descriptor = "(I)Ljava/lang/String;")
	public final String readJagString() {
		@Pc(14) byte version = this.buffer[this.currentPosition++];
		if (version != 0) {
			throw new IllegalStateException("Bad version number in gjstr2");
		}
		@Pc(24) int currentPosition = this.currentPosition;
		while (this.buffer[this.currentPosition++] != 0) {
		}
		@Pc(46) int length = this.currentPosition - currentPosition - 1;
		return length == 0 ? "" : Utils.readString(this.buffer, length, currentPosition);
	}

	@OriginalMember(owner = "client!cea", name = "q", descriptor = "(I)I")
	public final int readShort() {
		this.currentPosition += 2;
		@Pc(40) int value = (this.buffer[this.currentPosition - 1] & 0xFF) + ((this.buffer[this.currentPosition - 2] & 0xFF) << 8);
		if (value > 32767) {
			value -= 65536;
		}
		return value;
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(Z)I")
	public final int readUShortLittle() {
		this.currentPosition += 2;
		return ((this.buffer[this.currentPosition - 1] & 0xFF) << 8) + (this.buffer[this.currentPosition - 2] & 0xFF);
	}

	@OriginalMember(owner = "client!cea", name = "c", descriptor = "(BI)V")
	public final void writeByteInverse(@OriginalArg(1) int value) {
		this.buffer[this.currentPosition++] = (byte) -value;
	}

	@OriginalMember(owner = "client!cea", name = "l", descriptor = "(B)I")
	public final int readShortAdd() {
		this.currentPosition += 2;
		@Pc(33) int value = (this.buffer[this.currentPosition - 1] - 128 & 0xFF) + ((this.buffer[this.currentPosition - 2] & 0xFF) << 8);
		if (value > 32767) {
			value -= 65536;
		}
		return value;
	}

	@OriginalMember(owner = "client!cea", name = "m", descriptor = "(B)Ljava/lang/String;")
	public final String readString() {
		@Pc(14) int currentPosition = this.currentPosition;
		while (this.buffer[this.currentPosition++] != 0) {
		}
		@Pc(36) int length = this.currentPosition - currentPosition - 1;
		return length == 0 ? "" : Utils.readString(this.buffer, length, currentPosition);
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(ZI)V")
	public final void writeByte(@OriginalArg(1) int value) {
		this.buffer[this.currentPosition++] = (byte) value;
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(II[BI)V")
	public final void writeBytes(@OriginalArg(0) int length, @OriginalArg(1) int offset, @OriginalArg(2) byte[] bytes) {
		for (@Pc(3) int i = offset; i < offset + length; i++) {
			this.buffer[this.currentPosition++] = bytes[i];
		}
	}

	@OriginalMember(owner = "client!cea", name = "l", descriptor = "(II)V")
	public final void writeSmart(@OriginalArg(1) int data) {
		if (data >= 0 && data < 128) {
			this.writeByte(data);
		} else if (data >= 0 && data < 32768) {
			this.writeShort(data + 32768);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@OriginalMember(owner = "client!cea", name = "s", descriptor = "(I)I")
	public final int readUSmart() {
		@Pc(16) int peek = this.buffer[this.currentPosition] & 0xFF;
		return peek < 128 ? this.readUByte() : this.readUShort() - 32768;
	}

	@OriginalMember(owner = "client!cea", name = "m", descriptor = "(II)V")
	public final void writeShortLength(@OriginalArg(1) int length) {
		this.buffer[this.currentPosition - length - 2] = (byte) (length >> 8);
		this.buffer[this.currentPosition - length - 1] = (byte) length;
	}

	@OriginalMember(owner = "client!cea", name = "t", descriptor = "(I)Ljava/lang/String;")
	public final String readNullString() {
		if (this.buffer[this.currentPosition] == 0) {
			this.currentPosition++;
			return null;
		} else {
			return this.readString();
		}
	}

	@OriginalMember(owner = "client!cea", name = "n", descriptor = "(B)I")
	public final int readInt() {
		this.currentPosition += 4;
		return (this.buffer[this.currentPosition - 1] & 0xFF) + ((this.buffer[this.currentPosition - 4] & 0xFF) << 24) + ((this.buffer[this.currentPosition + -3] & 0xFF) << 16) + ((this.buffer[this.currentPosition + -2] & 0xFF) << 8);
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(Ljava/lang/String;I)V")
	public final void writeJagString(@OriginalArg(0) String string) {
		@Pc(15) int nulIndex = string.indexOf(0);
		if (nulIndex >= 0) {
			throw new IllegalArgumentException("NUL character at " + nulIndex + " - cannot pjstr2");
		}
		this.buffer[this.currentPosition++] = 0;
		this.currentPosition += Utils.encodeCp1252(this.buffer, string.length(), string, this.currentPosition);
		this.buffer[this.currentPosition++] = 0;
	}

	@OriginalMember(owner = "client!cea", name = "u", descriptor = "(I)I")
	public final int readUByte() {
		return this.buffer[this.currentPosition++] & 0xFF;
	}

	@OriginalMember(owner = "client!cea", name = "v", descriptor = "(I)B")
	public final byte readByte() {
		return this.buffer[this.currentPosition++];
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(III[B)V")
	public final void readBytes(@OriginalArg(1) int offset, @OriginalArg(2) int length, @OriginalArg(3) byte[] bytes) {
		for (@Pc(7) int i = offset; i < length + offset; i++) {
			bytes[i] = this.buffer[this.currentPosition++];
		}
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(Ljava/math/BigInteger;BLjava/math/BigInteger;)V")
	public final void applyRSA(@OriginalArg(0) BigInteger modulus, @OriginalArg(2) BigInteger exponent) {
		@Pc(6) int currentPosition = this.currentPosition;
		this.currentPosition = 0;
		@Pc(12) byte[] data = new byte[currentPosition];
		this.readBytes(0, currentPosition, data);
		@Pc(28) BigInteger bigInteger3 = new BigInteger(data);
		@Pc(33) BigInteger bigInteger4 = bigInteger3.modPow(exponent, modulus);
		@Pc(36) byte[] encrypted = bigInteger4.toByteArray();
		this.currentPosition = 0;
		this.writeShort(encrypted.length);
		this.writeBytes(encrypted.length, 0, encrypted);
	}

	@OriginalMember(owner = "client!cea", name = "d", descriptor = "(BI)V")
	public final void writeInt(@OriginalArg(1) int value) {
		this.buffer[this.currentPosition++] = (byte) (value >> 24);
		this.buffer[this.currentPosition++] = (byte) (value >> 16);
		this.buffer[this.currentPosition++] = (byte) (value >> 8);
		this.buffer[this.currentPosition++] = (byte) value;
	}

	@OriginalMember(owner = "client!cea", name = "a", descriptor = "(JB)V")
	public final void writeLong(@OriginalArg(0) long value) {
		this.buffer[this.currentPosition++] = (byte) (value >> 56);
		this.buffer[this.currentPosition++] = (byte) (value >> 48);
		this.buffer[this.currentPosition++] = (byte) (value >> 40);
		this.buffer[this.currentPosition++] = (byte) (value >> 32);
		this.buffer[this.currentPosition++] = (byte) (value >> 24);
		this.buffer[this.currentPosition++] = (byte) (value >> 16);
		this.buffer[this.currentPosition++] = (byte) (value >> 8);
		this.buffer[this.currentPosition++] = (byte) value;
	}

	@OriginalMember(owner = "client!cea", name = "o", descriptor = "(B)I")
	public final int readUByteAdd() {
		return this.buffer[this.currentPosition++] - 128 & 0xFF;
	}

	@OriginalMember(owner = "client!cea", name = "n", descriptor = "(II)V")
	public final void writeIntLE(@OriginalArg(1) int value) {
		this.buffer[this.currentPosition++] = (byte) value;
		this.buffer[this.currentPosition++] = (byte) (value >> 8);
		this.buffer[this.currentPosition++] = (byte) (value >> 16);
		this.buffer[this.currentPosition++] = (byte) (value >> 24);
	}

	@OriginalMember(owner = "client!cea", name = "e", descriptor = "(BI)V")
	public final void writeByteAdd(@OriginalArg(1) int value) {
		this.buffer[this.currentPosition++] = (byte) (value + 128);
	}

	@OriginalMember(owner = "client!cea", name = "o", descriptor = "(II)V")
	public final void writeFlags(@OriginalArg(0) int flags) {
		if ((flags & 0xFFFFFF80) != 0) {
			if ((flags & 0xFFFFC000) != 0) {
				if ((flags & 0xFFE00000) != 0) {
					if ((flags & 0xF0000000) != 0) {
						this.writeByte(flags >>> 28 | 0x80);
					}
					this.writeByte(flags >>> 21 | 0x80);
				}
				this.writeByte(flags >>> 14 | 0x80);
			}
			this.writeByte(flags >>> 7 | 0x80);
		}
		this.writeByte(flags & 0x7F);
	}

	@OriginalMember(owner = "client!cea", name = "p", descriptor = "(II)V")
	public final void writeShortLE(@OriginalArg(1) int value) {
		this.buffer[this.currentPosition++] = (byte) value;
		this.buffer[this.currentPosition++] = (byte) (value >> 8);
	}

	@OriginalMember(owner = "client!qh", name = "K", descriptor = "I")
	public static int mediumPos = 0;
	@OriginalMember(owner = "client!pl", name = "a", descriptor = "(IZ[BI)I")
	public static int getCrc32(@OriginalArg(0) int currentPosition, @OriginalArg(2) byte[] buffer, @OriginalArg(3) int start) {
		@Pc(12) int crc = -1;
		for (@Pc(14) int i = start; i < currentPosition; i++) {
			crc = crc >>> 8 ^ GlxProgram.CRC_TABLE[(crc ^ buffer[i]) & 0xFF];
		}
		return ~crc;
	}
	@OriginalMember(owner = "client!rga", name = "a", descriptor = "(II[B)I")
	public static int crc32(@OriginalArg(1) int currentPosition, @OriginalArg(2) byte[] buffer) {
		return Packet.getCrc32(currentPosition, buffer, 0);
	}
}
