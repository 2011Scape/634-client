package com.jagex.game.runetek5.preference;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!gga")
public final class SmallTexturesOption extends Preference {

	@OriginalMember(owner = "client!gga", name = "<init>", descriptor = "(Lclient!dh;)V")
	public SmallTexturesOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!gga", name = "<init>", descriptor = "(ILclient!dh;)V")
	public SmallTexturesOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!gga", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		return 3;
	}

	@OriginalMember(owner = "client!gga", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		super.value = this.getDefaultValue();
	}

	@OriginalMember(owner = "client!gga", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return super.gamePreferences.isLowMemoryJava() ? 1 : 0;
	}

	@OriginalMember(owner = "client!gga", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!gga", name = "f", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}
}
