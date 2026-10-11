package com.jagex.game.runetek5.preference.impl;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!sba")
public final class FogOption extends Preference {

	@OriginalMember(owner = "client!sba", name = "<init>", descriptor = "(Lclient!dh;)V")
	public FogOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!sba", name = "<init>", descriptor = "(ILclient!dh;)V")
	public FogOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!sba", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		return value == 0 || super.gamePreferences.groundBlending.getValue() == 1 ? 1 : 2;
	}

	@OriginalMember(owner = "client!sba", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (super.value != 0 && super.gamePreferences.groundBlending.getValue() != 1) {
			super.value = 0;
		}
		if (super.value < 0 || super.value > 1) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!sba", name = "d", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!sba", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!sba", name = "e", descriptor = "(I)Z")
	public boolean canMod() {
		return true;
	}

	@OriginalMember(owner = "client!sba", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 1;
	}
}
