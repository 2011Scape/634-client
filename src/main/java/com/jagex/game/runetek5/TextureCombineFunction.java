package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.toolkit.companion.TextureCombineMode;
import org.openrs2.deob.annotation.OriginalArg;

@OriginalClass("client!sia")
public final class TextureCombineFunction {
	@OriginalMember(owner = "client!rl", name = "d", descriptor = "Lclient!sia;")
	public static final TextureCombineFunction aClass315_4 = new TextureCombineFunction();
	@OriginalMember(owner = "client!tha", name = "l", descriptor = "Lclient!sia;")
	public static final TextureCombineFunction aClass315_5 = new TextureCombineFunction();
	@OriginalMember(owner = "client!gda", name = "r", descriptor = "Lclient!sia;")
	public static final TextureCombineFunction aClass315_1 = new TextureCombineFunction();
	@OriginalMember(owner = "client!naa", name = "A", descriptor = "Lclient!sia;")
	public static final TextureCombineFunction aClass315_2 = new TextureCombineFunction();
	@OriginalMember(owner = "client!qw", name = "a", descriptor = "(BLclient!sia;)I")
	public static int toGlCombineFunction(@OriginalArg(1) TextureCombineFunction function) {
		if (function == TextureCombineFunction.aClass315_4) {
			return 7681;
		} else if (TextureCombineMode.MODULATE == function) {
			return 8448;
		} else if (function == TextureCombineFunction.aClass315_5) {
			return 34165;
		} else if (TextureCombineFunction.aClass315_2 == function) {
			return 260;
		} else if (function == TextureCombineFunction.aClass315_1) {
			return 34023;
		} else {
			throw new IllegalArgumentException();
		}
	}
}
