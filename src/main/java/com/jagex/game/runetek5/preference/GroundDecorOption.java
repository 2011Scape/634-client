package com.jagex.game.runetek5.preference;

import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.preference.Preference;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.Game;

@OriginalClass("client!kba")
public final class GroundDecorOption extends Preference {

	@OriginalMember(owner = "client!kba", name = "q", descriptor = "[F")
	public static final float[] SINE = new float[16384];

	@OriginalMember(owner = "client!kba", name = "s", descriptor = "[F")
	public static final float[] COSINE = new float[16384];

	static {
		@Pc(7) double angle = 3.834951969714103E-4D;
		for (@Pc(9) int index = 0; index < 16384; index++) {
			SINE[index] = (float) Math.sin(angle * (double) index);
			COSINE[index] = (float) Math.cos((double) index * angle);
		}
	}

	@OriginalMember(owner = "client!kba", name = "<init>", descriptor = "(Lclient!dh;)V")
	public GroundDecorOption(@OriginalArg(0) GamePreferences clientOptions) {
		super(clientOptions);
	}

	@OriginalMember(owner = "client!kba", name = "<init>", descriptor = "(ILclient!dh;)V")
	public GroundDecorOption(@OriginalArg(0) int value, @OriginalArg(1) GamePreferences clientOptions) {
		super(value, clientOptions);
	}

	@OriginalMember(owner = "client!kba", name = "a", descriptor = "(Z)V")
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

	@OriginalMember(owner = "client!kba", name = "a", descriptor = "(II)V")
	@Override
	public void setValue(@OriginalArg(1) int value) {
		super.value = value;
	}

	@OriginalMember(owner = "client!kba", name = "d", descriptor = "(I)Z")
	public boolean canMod() {
		if (super.gamePreferences.isLowMemoryJava()) {
			return false;
		} else {
			return super.gamePreferences.getGame() == Game.RUNESCAPE;
		}
	}

	@OriginalMember(owner = "client!kba", name = "b", descriptor = "(I)I")
	@Override
	public int getDefaultValue() {
		return 1;
	}

	@OriginalMember(owner = "client!kba", name = "e", descriptor = "(I)I")
	public int getValue() {
		return super.value;
	}

	@OriginalMember(owner = "client!kba", name = "b", descriptor = "(II)I")
	@Override
	public int canSet(@OriginalArg(1) int value) {
		if (super.gamePreferences.isLowMemoryJava()) {
			return 3;
		} else if (super.gamePreferences.getGame() == Game.RUNESCAPE) {
			return 1;
		} else {
			return 3;
		}
	}
}
