package com.jagex.game.runetek5.graphics.font

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.config.fonttype.FontType
import org.openrs2.deob.annotation.Pc

object FontManager {
	@OriginalMember(owner = "client!vw", name = "r", descriptor = "I")
	@JvmField
	var b12FullIndex: Int = 0

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "I")
	@JvmField
	var p12FullIndex: Int = 0

	@OriginalMember(owner = "client!jj", name = "G", descriptor = "I")
	@JvmField
	var p11FullIndex: Int = 0

	@OriginalMember(owner = "client!ca", name = "g", descriptor = "Lclient!oea;")
	@JvmField
	var messageboxFont: FontMetrics? = null

	@OriginalMember(owner = "client!gj", name = "w", descriptor = "Lclient!oea;")
	@JvmField
	var worldMapAreaFont: FontMetrics? = null
	@OriginalMember(owner = "client!ce", name = "d", descriptor = "(I)[I")
	@JvmStatic
	fun getDefaultFontIds(): IntArray {
		return intArrayOf(FontManager.p11FullIndex, FontManager.p12FullIndex, FontManager.b12FullIndex)
	}

	@OriginalMember(owner = "client!mia", name = "a", descriptor = "(Lclient!in;I)V")
	@JvmStatic
	fun loadFontIds(@OriginalArg(0) fontArchive: Js5ResourceProvider?) {
		FontManager.p11FullIndex = fontArchive!!.getGroupId("p11_full")
		FontManager.p12FullIndex = fontArchive.getGroupId("p12_full")
		FontManager.b12FullIndex = fontArchive.getGroupId("b12_full")
	}
	@OriginalMember(owner = "client!km", name = "t", descriptor = "Lclient!da;")
	@JvmField
	var aClass68_4: AbstractFontRenderer? = null

	@OriginalMember(owner = "client!fn", name = "a", descriptor = "(Lclient!ha;I)V")
	@JvmStatic
	fun loadDefaultFonts(@OriginalArg(0) toolkit: Toolkit?) {
		Renderers.FONT_RENDERER = FontManager.getFont(toolkit, true, FontManager.p11FullIndex)
		FontManager.worldMapAreaFont = FontManager.getFontMetrics(toolkit, FontManager.p11FullIndex)
		FontManager.aClass68_4 = FontManager.getFont(toolkit, true, FontManager.p12FullIndex)
		FontManager.messageboxFont = FontManager.getFontMetrics(toolkit, FontManager.p12FullIndex)
		MiniMenu.minimenuHeader = FontManager.getFont(toolkit, true, FontManager.b12FullIndex)
		FontMetrics.defaultFontMetrics = FontManager.getFontMetrics(toolkit, FontManager.b12FullIndex)
	}
	@OriginalMember(owner = "client!bi", name = "a", descriptor = "(ILclient!ha;I)Lclient!oea;")
	@JvmStatic
	fun getFontMetrics(@OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) fontId: Int): FontMetrics? {
		@Pc(14) val fontType: FontType? = FontCombo.getFont(fontId, toolkit, true)
		return if (fontType == null) null else fontType.aClass239_4
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(Lclient!ha;BZI)Lclient!da;")
	@JvmStatic
	fun getFont(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(2) includeMetrics: Boolean, @OriginalArg(3) fontId: Int): AbstractFontRenderer? {
		@Pc(14) val fontType: FontType? = FontCombo.getFont(fontId, toolkit, includeMetrics)
		return if (fontType == null) null else fontType.font
	}
}
