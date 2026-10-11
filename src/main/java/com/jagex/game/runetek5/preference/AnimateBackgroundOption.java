package com.jagex.game.runetek5.preference;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.Game;

@OriginalClass("client!fo")
public final class AnimateBackgroundOption extends Preference {

	@OriginalMember(owner = "client!fo", name = "<init>", descriptor = "(ILclient!dh;)V")
	public AnimateBackgroundOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!fo", name = "<init>", descriptor = "(Lclient!dh;)V")
	public AnimateBackgroundOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!fo", name = "d", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!fo", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!fo", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		return 3;
	}

	@OriginalMember(owner = "client!fo", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return super.gamePreferences.getGame() == Game.RUNESCAPE && super.gamePreferences.isLowMemoryJava() ? 0 : 1;
	}

	@OriginalMember(owner = "client!fo", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		super.value = this.getDefaultValue();
	}
}
