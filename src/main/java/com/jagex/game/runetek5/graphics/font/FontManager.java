package com.jagex.game.runetek5.graphics.font;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.config.fonttype.FontType;
import org.openrs2.deob.annotation.Pc;

public final class FontManager {
	@OriginalMember(owner = "client!vw", name = "r", descriptor = "I")
	public static int b12FullIndex;

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "I")
	public static int p12FullIndex;

	@OriginalMember(owner = "client!jj", name = "G", descriptor = "I")
	public static int p11FullIndex;

	@OriginalMember(owner = "client!ca", name = "g", descriptor = "Lclient!oea;")
	public static FontMetrics messageboxFont;

	private FontManager() {
		/* empty */
	}
	@OriginalMember(owner = "client!gj", name = "w", descriptor = "Lclient!oea;")
	public static FontMetrics worldMapAreaFont;
	@OriginalMember(owner = "client!ce", name = "d", descriptor = "(I)[I")
	public static int[] getDefaultFontIds() {
		return new int[] { FontManager.p11FullIndex, FontManager.p12FullIndex, FontManager.b12FullIndex };
	}

	@OriginalMember(owner = "client!mia", name = "a", descriptor = "(Lclient!in;I)V")
	public static void loadFontIds(@OriginalArg(0) Js5ResourceProvider fontArchive) {
		FontManager.p11FullIndex = fontArchive.getGroupId("p11_full");
		FontManager.p12FullIndex = fontArchive.getGroupId("p12_full");
		FontManager.b12FullIndex = fontArchive.getGroupId("b12_full");
	}
	@OriginalMember(owner = "client!km", name = "t", descriptor = "Lclient!da;")
	public static AbstractFontRenderer aClass68_4;

	@OriginalMember(owner = "client!fn", name = "a", descriptor = "(Lclient!ha;I)V")
	public static void loadDefaultFonts(@OriginalArg(0) Toolkit toolkit) {
		Renderers.FONT_RENDERER = FontManager.getFont(toolkit, true, FontManager.p11FullIndex);
		FontManager.worldMapAreaFont = FontManager.getFontMetrics(toolkit, FontManager.p11FullIndex);
		FontManager.aClass68_4 = FontManager.getFont(toolkit, true, FontManager.p12FullIndex);
		FontManager.messageboxFont = FontManager.getFontMetrics(toolkit, FontManager.p12FullIndex);
		MiniMenu.minimenuHeader = FontManager.getFont(toolkit, true, FontManager.b12FullIndex);
		FontMetrics.defaultFontMetrics = FontManager.getFontMetrics(toolkit, FontManager.b12FullIndex);
	}
	@OriginalMember(owner = "client!bi", name = "a", descriptor = "(ILclient!ha;I)Lclient!oea;")
	public static FontMetrics getFontMetrics(@OriginalArg(1) Toolkit toolkit, @OriginalArg(2) int fontId) {
		@Pc(14) FontType fontType = FontCombo.getFont(fontId, toolkit, true);
		return fontType == null ? null : fontType.aClass239_4;
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(Lclient!ha;BZI)Lclient!da;")
	public static AbstractFontRenderer getFont(@OriginalArg(0) Toolkit toolkit, @OriginalArg(2) boolean includeMetrics, @OriginalArg(3) int fontId) {
		@Pc(14) FontType fontType = FontCombo.getFont(fontId, toolkit, includeMetrics);
		return fontType == null ? null : fontType.font;
	}
}
