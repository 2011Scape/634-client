package com.jagex.game.runetek5.preference;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.Game;

@OriginalClass("client!dba")
public final class TextureQuality extends Preference {

	@OriginalMember(owner = "client!dba", name = "<init>", descriptor = "(Lclient!dh;)V")
	public TextureQuality(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!dba", name = "<init>", descriptor = "(ILclient!dh;)V")
	public TextureQuality(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!dba", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!dba", name = "d", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!dba", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 1;
	}

	@OriginalMember(owner = "client!dba", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		if (super.gamePreferences.getGame() != Game.RUNESCAPE) {
			return 3;
		} else if (super.gamePreferences.isLowMemoryJava()) {
			return 3;
		} else if (value == 0 || super.gamePreferences.groundBlending.getValue() == 1) {
			return 1;
		} else {
			return 2;
		}
	}

	@OriginalMember(owner = "client!dba", name = "a", descriptor = "(Z)V")
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

	@OriginalMember(owner = "client!dba", name = "e", descriptor = "(I)Z")
	public boolean canMod() {
		if (super.gamePreferences.getGame() == Game.RUNESCAPE) {
			return !super.gamePreferences.isLowMemoryJava();
		} else {
			return false;
		}
	}
}
