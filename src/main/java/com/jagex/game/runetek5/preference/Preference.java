package com.jagex.game.runetek5.preference;

import com.jagex.game.runetek5.preference.GamePreferences;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!su")
public abstract class Preference {

	@OriginalMember(owner = "client!su", name = "c", descriptor = "Lclient!dh;")
	public final GamePreferences gamePreferences;

	@OriginalMember(owner = "client!su", name = "e", descriptor = "I")
	public int value;

	@OriginalMember(owner = "client!su", name = "<init>", descriptor = "(Lclient!dh;)V")
	public Preference(@OriginalArg(0) GamePreferences clientOptions) {
		this.gamePreferences = clientOptions;
		this.value = this.getDefaultValue();
	}

	@OriginalMember(owner = "client!su", name = "<init>", descriptor = "(ILclient!dh;)V")
	public Preference(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		this.gamePreferences = clientOptions;
		this.value = value;
	}

	@OriginalMember(owner = "client!su", name = "b", descriptor = "(I)I")
	public abstract int getDefaultValue();

	@OriginalMember(owner = "client!su", name = "a", descriptor = "(II)V")
	public abstract void setValue(@OriginalArg(1) int value);

	@OriginalMember(owner = "client!su", name = "b", descriptor = "(II)I")
	public abstract int canSet(@OriginalArg(1) int value);

	@OriginalMember(owner = "client!su", name = "a", descriptor = "(Z)V")
	public abstract void validate();

	@OriginalMember(owner = "client!su", name = "c", descriptor = "(II)V")
	public final void setSafeValue(@OriginalArg(0) int value) {
		if (this.canSet(value) != 3) {
			this.setValue(value);
		}
	}
}
