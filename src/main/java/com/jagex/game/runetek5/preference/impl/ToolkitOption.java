package com.jagex.game.runetek5.preference.impl;

import com.jagex.game.runetek5.preference.GamePreferences;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.preference.Preference;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.NativeLibraryOwner;

@OriginalClass("client!eea")
public final class ToolkitOption extends Preference {

	@OriginalMember(owner = "client!eea", name = "z", descriptor = "Z")
	private boolean verifySettings = true;

	@OriginalMember(owner = "client!eea", name = "C", descriptor = "Z")
	public boolean isDefault = false;

	@OriginalMember(owner = "client!eea", name = "<init>", descriptor = "(ILclient!dh;)V")
	public ToolkitOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!eea", name = "<init>", descriptor = "(Lclient!dh;)V")
	public ToolkitOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!eea", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		if (super.gamePreferences.getProcessorSpecs().isSigned()) {
			return value == 3 && !NativeLibraryOwner.hasNativeLibraryFile("jagdx") ? 3 : 2;
		} else {
			return 3;
		}
	}

	@OriginalMember(owner = "client!eea", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
		this.isDefault = false;
	}

	@OriginalMember(owner = "client!eea", name = "b", descriptor = "(ZI)V")
	public void setActive(@OriginalArg(0) boolean verifySettings) {
		this.verifySettings = verifySettings;
	}

	@OriginalMember(owner = "client!eea", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		this.isDefault = true;
		return super.gamePreferences.getProcessorSpecs().isSigned() ? 2 : 0;
	}

	@OriginalMember(owner = "client!eea", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (!super.gamePreferences.getProcessorSpecs().isSigned()) {
			super.value = 0;
		}
		if (super.value < 0 || super.value > 5) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!eea", name = "f", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!eea", name = "a", descriptor = "(B)Z")
	public boolean isActive() {
		return this.verifySettings;
	}

	@OriginalMember(owner = "client!eea", name = "g", descriptor = "(I)Z")
	public boolean canMod() {
		return super.gamePreferences.getProcessorSpecs().isSigned();
	}
	@OriginalMember(owner = "client!oba", name = "e", descriptor = "Z")
	public static boolean renderWithCpu = false;

	@OriginalMember(owner = "client!paa", name = "tb", descriptor = "I")
	public static int autoSetupToolkit;
}
