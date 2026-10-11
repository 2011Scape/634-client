package com.jagex.game.runetek5.preference;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.ToolkitType;

@OriginalClass("client!jaa")
public final class OrthographicOption extends Preference {

	@OriginalMember(owner = "client!jaa", name = "<init>", descriptor = "(Lclient!dh;)V")
	public OrthographicOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!jaa", name = "<init>", descriptor = "(ILclient!dh;)V")
	public OrthographicOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!jaa", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!jaa", name = "d", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!jaa", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 0;
	}

	@OriginalMember(owner = "client!jaa", name = "e", descriptor = "(I)Z")
	public boolean canMod() {
		return true;
	}

	@OriginalMember(owner = "client!jaa", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (this.isEnabled()) {
			if (super.gamePreferences.toolkit.isActive() && !ToolkitType.isSoftwareToolkit(super.gamePreferences.toolkit.getValue())) {
				super.value = 1;
			}
			if (super.gamePreferences.fullScreen.getValue() == 1) {
				super.value = 1;
			}
		}
		if (super.value == 3) {
			super.value = 2;
		}
		if (super.value < 0 || super.value > 3) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!jaa", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		if (OrthographicOption.isOrthographicMode(value)) {
			if (super.gamePreferences.toolkit.isActive() && !ToolkitType.isSoftwareToolkit(super.gamePreferences.toolkit.getValue())) {
				return 3;
			}
			if (super.gamePreferences.fullScreen.getValue() == 1) {
				return 3;
			}
		}
		if (value == 3) {
			return 3;
		} else if (OrthographicOption.isOrthographicMode(value)) {
			return 2;
		} else {
			return 1;
		}
	}

	@OriginalMember(owner = "client!jaa", name = "b", descriptor = "(Z)Z")
	public boolean isEnabled() {
		return OrthographicOption.isOrthographicMode(super.value);
	}
	@OriginalMember(owner = "client!jr", name = "a", descriptor = "(II)Z")
	public static boolean isOrthographicMode(@OriginalArg(0) int mode) {
		return mode == 2 || mode == 3;
	}
}
