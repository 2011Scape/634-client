package com.jagex.game.runetek5.preference.impl;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager;

@OriginalClass("client!pr")
public final class ParticlesOption extends Preference {

	@OriginalMember(owner = "client!pr", name = "<init>", descriptor = "(ILclient!dh;)V")
	public ParticlesOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
		ParticleSystemManager.setDetail(super.value);
	}

	@OriginalMember(owner = "client!pr", name = "<init>", descriptor = "(Lclient!dh;)V")
	public ParticlesOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
		ParticleSystemManager.setDetail(super.value);
	}

	@OriginalMember(owner = "client!pr", name = "a", descriptor = "(Z)V")
	@Override
	public void validate() {
		if (super.gamePreferences.getProcessorSpecs().getMaxMemory() < 96) {
			super.value = 0;
		}
		if (super.value < 0 || super.value > 2) {
			super.value = this.getDefaultValue();
		}
	}

	@OriginalMember(owner = "client!pr", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		return super.gamePreferences.getProcessorSpecs().getMaxMemory() < 96 ? 3 : 1;
	}

	@OriginalMember(owner = "client!pr", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
		ParticleSystemManager.setDetail(super.value);
	}

	@OriginalMember(owner = "client!pr", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return super.gamePreferences.getProcessorSpecs().getMaxMemory() < 96 ? 0 : 2;
	}

	@OriginalMember(owner = "client!pr", name = "d", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!pr", name = "f", descriptor = "(I)Z")
	public boolean canMod() {
		return super.gamePreferences.getProcessorSpecs().getMaxMemory() >= 96;
	}
}
