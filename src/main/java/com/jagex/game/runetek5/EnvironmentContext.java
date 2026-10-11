package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.water.WaterTextureGenerator;

@OriginalClass("client!rc")
public final class EnvironmentContext {

	@OriginalMember(owner = "client!rc", name = "h", descriptor = "[I")
	public static final int[] FADE_TABLE = new int[4096];

	@OriginalMember(owner = "client!rc", name = "c", descriptor = "I")
	private final int processorCount;

	@OriginalMember(owner = "client!rc", name = "b", descriptor = "Z")
	private final boolean arm;

	@OriginalMember(owner = "client!rc", name = "l", descriptor = "I")
	private final int maxMemory;

	@OriginalMember(owner = "client!rc", name = "f", descriptor = "Z")
	private final boolean signed;

	static {
		for (@Pc(4) int i = 0; i < 4096; i++) {
			FADE_TABLE[i] = WaterTextureGenerator.fade(i);
		}
	}

	@OriginalMember(owner = "client!rc", name = "<init>", descriptor = "(ZIIZ)V")
	public EnvironmentContext(@OriginalArg(0) boolean signed, @OriginalArg(1) int maxMemory, @OriginalArg(2) int processorCount, @OriginalArg(3) boolean arm) {
		this.processorCount = processorCount;
		this.arm = arm;
		this.maxMemory = maxMemory;
		this.signed = signed;
	}

	@OriginalMember(owner = "client!rc", name = "a", descriptor = "(B)Z")
	public boolean isArm() {
		return this.arm;
	}

	@OriginalMember(owner = "client!rc", name = "a", descriptor = "(I)I")
	public int getMaxMemory() {
		return this.maxMemory;
	}

	@OriginalMember(owner = "client!rc", name = "b", descriptor = "(I)Z")
	public boolean isSigned() {
		return this.signed;
	}

	@OriginalMember(owner = "client!rc", name = "c", descriptor = "(I)I")
	public int getProcessorCount() {
		return this.processorCount;
	}
}
