package com.jagex.game.runetek5.preference;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!tt")
public final class LoadingSequenceOption extends Preference {

	@OriginalMember(owner = "client!tt", name = "<init>", descriptor = "(Lclient!dh;)V")
	public LoadingSequenceOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!tt", name = "<init>", descriptor = "(ILclient!dh;)V")
	public LoadingSequenceOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!tt", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 0;
	}

	@OriginalMember(owner = "client!tt", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
	}

	@OriginalMember(owner = "client!tt", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		return 1;
	}

	@OriginalMember(owner = "client!tt", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!tt", name = "e", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}
}
