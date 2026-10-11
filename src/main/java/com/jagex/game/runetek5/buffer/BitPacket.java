package com.jagex.game.runetek5.buffer;

import com.jagex.game.runetek5.tools.Isaac;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ua")
public final class BitPacket extends Packet {

	@OriginalMember(owner = "client!ua", name = "Lb", descriptor = "I")
	private int bitPosition;

	@OriginalMember(owner = "client!ua", name = "Nb", descriptor = "Lclient!ku;")
	private Isaac random;

	@OriginalMember(owner = "client!ua", name = "<init>", descriptor = "(I)V")
	public BitPacket(@OriginalArg(0) int size) {
		super(size);
	}

	@OriginalMember(owner = "client!ua", name = "w", descriptor = "(I)Z")
	public boolean largeOpcode() {
		@Pc(21) int v = super.buffer[super.currentPosition] - this.random.peek() & 0xFF;
		return v >= 128;
	}

	@OriginalMember(owner = "client!ua", name = "a", descriptor = "(I[I)V")
	public void initIsaac(@OriginalArg(1) int[] seed) {
		this.random = new Isaac(seed);
	}

	@OriginalMember(owner = "client!ua", name = "x", descriptor = "(I)I")
	public int readOpcode() {
		@Pc(21) int v = super.buffer[super.currentPosition++] - this.random.next() & 0xFF;
		return v < 128 ? v : (super.buffer[super.currentPosition++] - this.random.next() & 0xFF) + (v - 128 << 8);
	}

	@OriginalMember(owner = "client!ua", name = "q", descriptor = "(II)V")
	public void startPacket(@OriginalArg(0) int op) {
		super.buffer[super.currentPosition++] = (byte) (op + this.random.next());
	}

	@OriginalMember(owner = "client!ua", name = "a", descriptor = "(I[BII)V")
	public void readEncryptedBytes(@OriginalArg(0) int length, @OriginalArg(1) byte[] bytes) {
		for (@Pc(7) int i = 0; i < length; i++) {
			bytes[i] = (byte) (super.buffer[super.currentPosition++] - this.random.next());
		}
	}

	@OriginalMember(owner = "client!ua", name = "f", descriptor = "(BI)I")
	public int gbit(@OriginalArg(1) int n) {
		@Pc(15) int index = this.bitPosition >> 3;
		@Pc(23) int interest = 8 - (this.bitPosition & 0x7);
		this.bitPosition += n;
		@Pc(31) int v = 0;
		while (interest < n) {
			v += (super.buffer[index++] & BitPacket.BIT_MASKS[interest]) << n - interest;
			n -= interest;
			interest = 8;
		}
		if (interest == n) {
			v += BitPacket.BIT_MASKS[interest] & super.buffer[index];
		} else {
			v += super.buffer[index] >> interest - n & BitPacket.BIT_MASKS[n];
		}
		return v;
	}

	@OriginalMember(owner = "client!ua", name = "b", descriptor = "(Z)V")
	public void exitBitMode() {
		super.currentPosition = (this.bitPosition + 7) / 8;
	}

	@OriginalMember(owner = "client!ua", name = "r", descriptor = "(II)I")
	public int bitsRemaining(@OriginalArg(1) int pos) {
		return pos * 8 - this.bitPosition;
	}

	@OriginalMember(owner = "client!ua", name = "a", descriptor = "(ILclient!ku;)V")
	public void setRandom(@OriginalArg(1) Isaac isaac) {
		this.random = isaac;
	}

	@OriginalMember(owner = "client!ua", name = "z", descriptor = "(I)V")
	public void enterBitMode() {
		this.bitPosition = super.currentPosition * 8;
	}
	@OriginalMember(owner = "client!be", name = "s", descriptor = "[I")
	public static final int[] BIT_MASKS = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727, 268435455, 536870911, 1073741823, Integer.MAX_VALUE, -1 };
}
