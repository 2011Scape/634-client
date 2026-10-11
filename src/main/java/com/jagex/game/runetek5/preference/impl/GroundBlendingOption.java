package com.jagex.game.runetek5.preference.impl;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.Game;

@OriginalClass("client!mm")
public final class GroundBlendingOption extends Preference {

	@OriginalMember(owner = "client!mm", name = "<init>", descriptor = "(ILclient!dh;)V")
	public GroundBlendingOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!mm", name = "<init>", descriptor = "(Lclient!dh;)V")
	public GroundBlendingOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!mm", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!mm", name = "d", descriptor = "(I)Z")
	public boolean canMod() {
		if (super.gamePreferences.isLowMemoryJava()) {
			return false;
		} else {
			return super.gamePreferences.getGame() == Game.RUNESCAPE;
		}
	}

	@OriginalMember(owner = "client!mm", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		if (super.gamePreferences.isLowMemoryJava()) {
			return 3;
		} else if (super.gamePreferences.getGame() == Game.RUNESCAPE) {
			if (value == 0) {
				if (super.gamePreferences.fog.getValue() == 1) {
					return 2;
				}
				if (super.gamePreferences.textures.getValue() == 1) {
					return 2;
				}
				if (super.gamePreferences.water.getValue() > 0) {
					return 2;
				}
			}
			return 1;
		} else {
			return 3;
		}
	}

	@OriginalMember(owner = "client!mm", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (super.gamePreferences.getGame() != Game.RUNESCAPE) {
			super.value = 1;
		} else if (super.gamePreferences.isLowMemoryJava()) {
			super.value = 0;
		}
		if (super.value != 0 && super.value != 1) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!mm", name = "f", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!mm", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 1;
	}
}
