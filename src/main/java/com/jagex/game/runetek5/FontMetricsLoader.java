package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class FontMetricsLoader {

	@OriginalMember(owner = "client!bl", name = "a", descriptor = "(IIILclient!in;)Lclient!oea;")
	public static FontMetrics getFontMetrics(@OriginalArg(0) int fontId, @OriginalArg(3) Js5ResourceProvider fontMetricsResource) {
		@Pc(9) byte[] data = fontMetricsResource.getFile(fontId, 0);
		return data == null ? null : new FontMetrics(data);
	}
}
