package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object FontMetricsLoader {

	@OriginalMember(owner = "client!bl", name = "a", descriptor = "(IIILclient!in;)Lclient!oea;")
	@JvmStatic
	fun getFontMetrics(@OriginalArg(0) fontId: Int, @OriginalArg(3) fontMetricsResource: Js5ResourceProvider?): FontMetrics? {
		@Pc(9) val data: ByteArray? = fontMetricsResource!!.getFile(fontId, 0)
		return if (data == null) null else FontMetrics(data)
	}
}
