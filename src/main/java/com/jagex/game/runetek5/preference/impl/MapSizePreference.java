package com.jagex.game.runetek5.preference.impl;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!kia")
public final class MapSizePreference extends Preference {

	@OriginalMember(owner = "client!kia", name = "w", descriptor = "[I")
	public static final int[] DECODE_TABLE = new int[128];

	static {
		for (@Pc(4) int i = 0; i < DECODE_TABLE.length; i++) {
			DECODE_TABLE[i] = -1;
		}
		for (@Pc(20) int i = 65; i <= 90; i++) {
			DECODE_TABLE[i] = i - 65;
		}
		for (@Pc(33) int i = 97; i <= 122; i++) {
			DECODE_TABLE[i] = i + 26 - 97;
		}
		for (@Pc(48) int i = 48; i <= 57; i++) {
			DECODE_TABLE[i] = i + 52 - 48;
		}
		DECODE_TABLE[42] = DECODE_TABLE[43] = 62;
		DECODE_TABLE[45] = DECODE_TABLE[47] = 63;
	}

	@OriginalMember(owner = "client!kia", name = "<init>", descriptor = "(ILclient!dh;)V")
	public MapSizePreference(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!kia", name = "<init>", descriptor = "(Lclient!dh;)V")
	public MapSizePreference(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!kia", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 0;
	}

	@OriginalMember(owner = "client!kia", name = "d", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!kia", name = "e", descriptor = "(I)Z")
	public boolean canMod() {
		@Pc(17) int heapSize = super.gamePreferences.getProcessorSpecs().getMaxMemory();
		return heapSize >= 96;
	}

	@OriginalMember(owner = "client!kia", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		@Pc(10) int heapSize = super.gamePreferences.getProcessorSpecs().getMaxMemory();
		if (heapSize < 96) {
			super.value = 0;
		}
		if (super.value > 1 && heapSize < 128) {
			super.value = 1;
		}
		if (super.value > 2 && heapSize < 192) {
			super.value = 2;
		}
		if (super.value < 0 || super.value > 3) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!kia", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		@Pc(12) int heapSize = super.gamePreferences.getProcessorSpecs().getMaxMemory();
		if (heapSize < 96) {
			return 3;
		} else if (value > 1 && heapSize < 128) {
			return 3;
		} else if (value > 3 && heapSize < 192) {
			return 3;
		} else {
			return 1;
		}
	}

	@OriginalMember(owner = "client!kia", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}
	@OriginalMember(owner = "client!eh", name = "a", descriptor = "(II)I")
	public static int getMaxBuildArea(@OriginalArg(1) int maxMemory) {
		if (maxMemory < 96) {
			return 0;
		} else if (maxMemory < 128) {
			return 2;
		} else {
			return 3;
		}
	}
}
