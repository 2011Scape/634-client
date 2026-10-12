package com.jagex.game.runetek5.statics

import org.openrs2.deob.annotation.OriginalMember

object ValidCharacterCount {

	@OriginalMember(owner = "client!cs", name = "b", descriptor = "I")
	@JvmField
	val VALID_CHARACTER_COUNT: Int = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£\$%^&*()-_=+[{]};:'@#~,<.>/?\\| ÄËÏÖÜäëïöüÿßÁÀÉÈÍÌÓÒÚÙáàéèíìóòúùÂÊÎÔÛâêîôûÆæãÃõÕçÇ".length
}
