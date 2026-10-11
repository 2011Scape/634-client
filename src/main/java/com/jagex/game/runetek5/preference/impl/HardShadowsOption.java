package com.jagex.game.runetek5.preference.impl;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!mca")
public final class HardShadowsOption extends Preference {

	@OriginalMember(owner = "client!mca", name = "<init>", descriptor = "(ILclient!dh;)V")
	public HardShadowsOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!mca", name = "<init>", descriptor = "(Lclient!dh;)V")
	public HardShadowsOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!mca", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		if (super.gamePreferences.isLowMemoryJava()) {
			return 3;
		} else if (super.gamePreferences.textures.getValue() == 0) {
			return 3;
		} else {
			return 1;
		}
	}

	@OriginalMember(owner = "client!mca", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (super.gamePreferences.isLowMemoryJava()) {
			super.value = 0;
		}
		if (super.gamePreferences.textures.getValue() == 0) {
			super.value = 0;
		}
		if (super.value < 0 || super.value > 2) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!mca", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!mca", name = "d", descriptor = "(I)Z")
	public boolean canMod() {
		if (super.gamePreferences.isLowMemoryJava()) {
			return false;
		} else {
			return super.gamePreferences.textures.getValue() != 0;
		}
	}

	@OriginalMember(owner = "client!mca", name = "e", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!mca", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 2;
	}
}
