package com.jagex.game.runetek5.statics;

import org.openrs2.deob.annotation.OriginalMember;

public final class ValidCharacterCount {

	@OriginalMember(owner = "client!cs", name = "b", descriptor = "I")
	public static final int VALID_CHARACTER_COUNT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ÄËÏÖÜäëïöüÿßÁÀÉÈÍÌÓÒÚÙáàéèíìóòúùÂÊÎÔÛâêîôûÆæãÃõÕçÇ".length();
}
