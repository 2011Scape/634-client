package com.jagex.game.runetek5.preference;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!wca")
public final class CustomCursorsPreference extends Preference {

	@OriginalMember(owner = "client!wca", name = "<init>", descriptor = "(Lclient!dh;)V")
	public CustomCursorsPreference(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!wca", name = "<init>", descriptor = "(ILclient!dh;)V")
	public CustomCursorsPreference(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!wca", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 1;
	}

	@OriginalMember(owner = "client!wca", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (super.value != 1 && super.value != 0) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!wca", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!wca", name = "d", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!wca", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		return 1;
	}
}
