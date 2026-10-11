package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!kf")
public final class TextureCombineSource {

	@OriginalMember(owner = "client!kf", name = "e", descriptor = "[I")
	public static final int[] SINE = new int[16384];

	@OriginalMember(owner = "client!kf", name = "b", descriptor = "[I")
	public static final int[] COSINE = new int[16384];

	static {
		@Pc(7) double radiansPerUnit = 3.834951969714103E-4D;
		for (@Pc(9) int angle = 0; angle < 16384; angle++) {
			SINE[angle] = (int) (Math.sin(radiansPerUnit * (double) angle) * 16384.0D);
			COSINE[angle] = (int) (Math.cos(radiansPerUnit * (double) angle) * 16384.0D);
		}
	}

	@OriginalMember(owner = "client!kf", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!fha", name = "H", descriptor = "Lclient!kf;")
	public static final TextureCombineSource PREVIOUS = new TextureCombineSource();

	@OriginalMember(owner = "client!ft", name = "M", descriptor = "Lclient!kf;")
	public static final TextureCombineSource CONSTANT_COLOR = new TextureCombineSource();
	@OriginalMember(owner = "client!eca", name = "j", descriptor = "Lclient!kf;")
	public static final TextureCombineSource TEXTURE = new TextureCombineSource();
	@OriginalMember(owner = "client!mw", name = "G", descriptor = "Lclient!kf;")
	public static final TextureCombineSource PRIMARY_COLOR = new TextureCombineSource();
}
