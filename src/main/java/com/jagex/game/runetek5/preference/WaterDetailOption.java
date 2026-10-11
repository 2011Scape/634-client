package com.jagex.game.runetek5.preference;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!tl")
public final class WaterDetailOption extends Preference {

	@OriginalMember(owner = "client!tl", name = "<init>", descriptor = "(ILclient!dh;)V")
	public WaterDetailOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!tl", name = "<init>", descriptor = "(Lclient!dh;)V")
	public WaterDetailOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!tl", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		if (super.gamePreferences.isLowMemoryJava()) {
			return 3;
		} else if (value == 0 || super.gamePreferences.groundBlending.getValue() == 1) {
			return 1;
		} else {
			return 2;
		}
	}

	@OriginalMember(owner = "client!tl", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 1;
	}

	@OriginalMember(owner = "client!tl", name = "e", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!tl", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (super.gamePreferences.isLowMemoryJava()) {
			super.value = 0;
		}
		if (super.value < 0 && super.value > 2) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!tl", name = "f", descriptor = "(I)Z")
	public boolean canMod() {
		return !super.gamePreferences.isLowMemoryJava();
	}

	@OriginalMember(owner = "client!tl", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}
}
