package com.jagex.game.runetek5.preference.impl;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.ToolkitType;

@OriginalClass("client!vca")
public final class AntiAliasingPreference extends Preference {

	@OriginalMember(owner = "client!vca", name = "<init>", descriptor = "(ILclient!dh;)V")
	public AntiAliasingPreference(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!vca", name = "<init>", descriptor = "(Lclient!dh;)V")
	public AntiAliasingPreference(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!vca", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 0;
	}

	@OriginalMember(owner = "client!vca", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		return ToolkitType.isHardwareToolkit(super.gamePreferences.toolkit.getValue()) ? 1 : 3;
	}

	@OriginalMember(owner = "client!vca", name = "d", descriptor = "(I)Z")
	public boolean canMod() {
		return ToolkitType.isHardwareToolkit(super.gamePreferences.toolkit.getValue());
	}

	@OriginalMember(owner = "client!vca", name = "e", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!vca", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!vca", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (super.gamePreferences.toolkit.isActive() && !ToolkitType.isHardwareToolkit(super.gamePreferences.toolkit.getValue())) {
			super.value = 0;
		}
		if (super.value < 0 || super.value > 2) {
			super.value = this.getDefaultValue();
		}
	}
}
