package com.jagex.game.runetek5.preference.impl;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!aj")
public final class FlickeringEffectPreference extends Preference {

	@OriginalMember(owner = "client!aj", name = "<init>", descriptor = "(Lclient!dh;)V")
	public FlickeringEffectPreference(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!aj", name = "<init>", descriptor = "(ILclient!dh;)V")
	public FlickeringEffectPreference(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!aj", name = "d", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!aj", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (super.value != 1 && super.value != 0) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!aj", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		return 1;
	}

	@OriginalMember(owner = "client!aj", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!aj", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 1;
	}
}
