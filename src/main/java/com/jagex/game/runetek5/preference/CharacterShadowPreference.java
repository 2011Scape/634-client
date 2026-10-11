package com.jagex.game.runetek5.preference;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!vu")
public final class CharacterShadowPreference extends Preference {

	@OriginalMember(owner = "client!vu", name = "<init>", descriptor = "(Lclient!dh;)V")
	public CharacterShadowPreference(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!vu", name = "<init>", descriptor = "(ILclient!dh;)V")
	public CharacterShadowPreference(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!vu", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 1;
	}

	@OriginalMember(owner = "client!vu", name = "d", descriptor = "(I)Z")
	public boolean canMod() {
		return !super.gamePreferences.isLowMemoryJava();
	}

	@OriginalMember(owner = "client!vu", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!vu", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		return super.gamePreferences.isLowMemoryJava() ? 3 : 1;
	}

	@OriginalMember(owner = "client!vu", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (super.gamePreferences.isLowMemoryJava()) {
			super.value = 0;
		}
		if (super.value != 1 && super.value != 0) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!vu", name = "e", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}
}
