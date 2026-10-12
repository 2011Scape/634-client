package com.jagex.game.runetek5.graphics.font

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.OriginalArg
import com.jagex.game.runetek5.config.fonttype.FontType
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object FontCombo {
	@OriginalMember(owner = "client!s", name = "b", descriptor = "Lclient!in;")
	@JvmField
	var fontmetricsResource: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!ifa", name = "e", descriptor = "Lclient!in;")
	@JvmField
	var loadingFontResource: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!ab", name = "q", descriptor = "[I")
	@JvmField
	var fontFileIds: IntArray? = null

	@OriginalMember(owner = "client!lk", name = "a", descriptor = "(Lclient!in;I[ILclient!in;)V")
	@JvmStatic
	fun init(@OriginalArg(0) fontmetricsResource: Js5ResourceProvider?, @OriginalArg(2) fontFileIds: IntArray?, @OriginalArg(3) loadingFontResource: Js5ResourceProvider?) {
		if (fontFileIds != null) {
			FontCombo.fontFileIds = fontFileIds
		}
		FontCombo.fontmetricsResource = fontmetricsResource
		FontCombo.loadingFontResource = loadingFontResource
	}
	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(ILclient!ha;)V")
	@JvmStatic
	fun createFonts(@OriginalArg(1) toolkit: Toolkit?) {
		FontCombo.aClass95Array1 = arrayOfNulls<FontType>(FontCombo.fontFileIds!!.size)
		@Pc(16) var i: Int = 0
		while (i < FontCombo.fontFileIds!!.size) {
			@Pc(22) val fontId: Int = FontCombo.fontFileIds!![i]
			@Pc(27) val fontMetrics: FontMetrics? = FontMetrics.list(fontId, FontCombo.fontmetricsResource)
			@Pc(35) val font: AbstractFontRenderer? = toolkit!!.createFontRenderer(fontMetrics, SpriteType.listGroup(FontCombo.loadingFontResource, fontId))
			FontCombo.aClass95Array1!![i] = FontType(font, fontMetrics)
			i++
		}
	}

	@OriginalMember(owner = "client!ffa", name = "d", descriptor = "(I)I")
	@JvmStatic
	fun getTotalFileCount(): Int {
		return if (FontCombo.fontFileIds == null) 0 else FontCombo.fontFileIds!!.size * 2
	}

	@OriginalMember(owner = "client!rc", name = "m", descriptor = "[Lclient!el;")
	@JvmField
	var aClass95Array1: Array<FontType?>? = null

	@OriginalMember(owner = "client!ua", name = "b", descriptor = "(IZ)I")
	@JvmStatic
	fun getReadyFileCount(@OriginalArg(1) recount: Boolean): Int {
		if (FontCombo.fontFileIds == null) {
			return 0
		} else if (recount || FontCombo.aClass95Array1 == null) {
			@Pc(20) var readyCount: Int = 0
			@Pc(29) var i: Int = 0
			while (i < FontCombo.fontFileIds!!.size) {
				@Pc(35) val fileId: Int = FontCombo.fontFileIds!![i]
				if (FontCombo.loadingFontResource!!.isFileReady(fileId)) {
					readyCount++
				}
				if (FontCombo.fontmetricsResource!!.isFileReady(fileId)) {
					readyCount++
				}
				i++
			}
			return readyCount
		} else {
			return FontCombo.fontFileIds!!.size * 2
		}
	}
	@OriginalMember(owner = "client!jaa", name = "a", descriptor = "(ILclient!ha;BZ)Lclient!el;")
	@JvmStatic
	fun getFont(@OriginalArg(0) fontId: Int, @OriginalArg(1) toolkit: Toolkit?, @OriginalArg(3) loadMetrics: Boolean): FontType? {
		if (fontId == -1) {
			return null
		}
		if (FontCombo.fontFileIds != null) {
			@Pc(15) var i: Int = 0
			while (i < FontCombo.fontFileIds!!.size) {
				if (fontId == FontCombo.fontFileIds!![i]) {
					return FontCombo.aClass95Array1!![i]
				}
				i++
			}
		}
		@Pc(42) var fontType: FontType? = FontType.aClass169_15.get(fontId.toLong()) as FontType?
		if (fontType != null) {
			if (loadMetrics && fontType.aClass239_4 == null) {
				@Pc(54) val fontMetrics: FontMetrics? = FontMetrics.list(fontId, FontCombo.fontmetricsResource)
				if (fontMetrics == null) {
					return null
				}
				fontType.aClass239_4 = fontMetrics
			}
			return fontType
		}
		@Pc(75) val indexedImages: Array<SpriteType?>? = SpriteType.listGroup(FontCombo.loadingFontResource, fontId)
		if (indexedImages == null) {
			return null
		}
		@Pc(84) val fontMetrics2: FontMetrics? = FontMetrics.list(fontId, FontCombo.fontmetricsResource)
		if (fontMetrics2 == null) {
			return null
		}
		if (loadMetrics) {
			fontType = FontType(toolkit!!.createFontRenderer(fontMetrics2, indexedImages), fontMetrics2)
		} else {
			fontType = FontType(toolkit!!.createFontRenderer(fontMetrics2, indexedImages))
		}
		FontType.aClass169_15.put(fontType, fontId.toLong())
		return fontType
	}
	@OriginalMember(owner = "client!ls", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun clearFontComboFonts() {
		FontCombo.aClass95Array1 = null
	}

	@OriginalMember(owner = "client!cv", name = "d", descriptor = "(I)I")
	@JvmStatic
	fun getFontComboReadyFileCount(): Int {
		return FontCombo.getReadyFileCount(false)
	}
}
