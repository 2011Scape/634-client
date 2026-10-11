package com.jagex.game.runetek5.preference;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.ToolkitType;

@OriginalClass("client!av")
public final class BloomOption extends Preference {

	@OriginalMember(owner = "client!av", name = "<init>", descriptor = "(Lclient!dh;)V")
	public BloomOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!av", name = "<init>", descriptor = "(ILclient!dh;)V")
	public BloomOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!av", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		return ToolkitType.isHardwareToolkit(super.gamePreferences.toolkit.getValue()) ? 1 : 3;
	}

	@OriginalMember(owner = "client!av", name = "d", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!av", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!av", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (super.gamePreferences.toolkit.isActive() && !ToolkitType.isHardwareToolkit(super.gamePreferences.toolkit.getValue())) {
			super.value = 0;
		}
		if (super.value < 0 || super.value > 1) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!av", name = "f", descriptor = "(I)Z")
	public boolean canMod() {
		return ToolkitType.isHardwareToolkit(super.gamePreferences.toolkit.getValue());
	}

	@OriginalMember(owner = "client!av", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 0;
	}
}
