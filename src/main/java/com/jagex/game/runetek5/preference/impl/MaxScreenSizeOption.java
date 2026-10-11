package com.jagex.game.runetek5.preference.impl;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!bl")
public final class MaxScreenSizeOption extends Preference {

	@OriginalMember(owner = "client!bl", name = "<init>", descriptor = "(ILclient!dh;)V")
	public MaxScreenSizeOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!bl", name = "<init>", descriptor = "(Lclient!dh;)V")
	public MaxScreenSizeOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!bl", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		if (super.gamePreferences.isLowMemoryJava()) {
			return 2;
		} else if (super.gamePreferences.toolkit.isActive() && MaxScreenSizeOption.isSoftwareToolkit(super.gamePreferences.toolkit.getValue())) {
			return 1;
		} else {
			return 0;
		}
	}

	@OriginalMember(owner = "client!bl", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (super.gamePreferences.isLowMemoryJava()) {
			super.value = 2;
		}
		if (super.value < 0 || super.value > 2) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!bl", name = "d", descriptor = "(I)Z")
	public boolean canMod() {
		return !super.gamePreferences.isLowMemoryJava();
	}

	@OriginalMember(owner = "client!bl", name = "e", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!bl", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		return super.gamePreferences.isLowMemoryJava() ? 3 : 1;
	}

	@OriginalMember(owner = "client!bl", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}
	@OriginalMember(owner = "client!tw", name = "a", descriptor = "(II)Z")
	public static boolean isSoftwareToolkit(@OriginalArg(0) int toolkitId) {
		return toolkitId == 0 || toolkitId == 2;
	}
}
