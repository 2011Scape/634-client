package com.jagex.game.runetek5.graphics.font;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.config.fonttype.FontType;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import org.openrs2.deob.annotation.Pc;

public final class FontCombo {
	@OriginalMember(owner = "client!s", name = "b", descriptor = "Lclient!in;")
	public static Js5ResourceProvider fontmetricsResource;

	@OriginalMember(owner = "client!ifa", name = "e", descriptor = "Lclient!in;")
	public static Js5ResourceProvider loadingFontResource;

	@OriginalMember(owner = "client!ab", name = "q", descriptor = "[I")
	public static int[] fontFileIds = null;

	private FontCombo() {
		/* empty */
	}
	@OriginalMember(owner = "client!lk", name = "a", descriptor = "(Lclient!in;I[ILclient!in;)V")
	public static void init(@OriginalArg(0) Js5ResourceProvider fontmetricsResource, @OriginalArg(2) int[] fontFileIds, @OriginalArg(3) Js5ResourceProvider loadingFontResource) {
		if (fontFileIds != null) {
			FontCombo.fontFileIds = fontFileIds;
		}
		FontCombo.fontmetricsResource = fontmetricsResource;
		FontCombo.loadingFontResource = loadingFontResource;
	}
	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(ILclient!ha;)V")
	public static void createFonts(@OriginalArg(1) Toolkit toolkit) {
		FontCombo.aClass95Array1 = new FontType[FontCombo.fontFileIds.length];
		for (@Pc(16) int i = 0; i < FontCombo.fontFileIds.length; i++) {
			@Pc(22) int fontId = FontCombo.fontFileIds[i];
			@Pc(27) FontMetrics fontMetrics = FontMetrics.list(fontId, FontCombo.fontmetricsResource);
			@Pc(35) AbstractFontRenderer font = toolkit.createFontRenderer(fontMetrics, SpriteType.listGroup(FontCombo.loadingFontResource, fontId));
			FontCombo.aClass95Array1[i] = new FontType(font, fontMetrics);
		}
	}

	@OriginalMember(owner = "client!ffa", name = "d", descriptor = "(I)I")
	public static int getTotalFileCount() {
		return FontCombo.fontFileIds == null ? 0 : FontCombo.fontFileIds.length * 2;
	}

	@OriginalMember(owner = "client!rc", name = "m", descriptor = "[Lclient!el;")
	public static FontType[] aClass95Array1 = null;

	@OriginalMember(owner = "client!ua", name = "b", descriptor = "(IZ)I")
	public static int getReadyFileCount(@OriginalArg(1) boolean recount) {
		if (FontCombo.fontFileIds == null) {
			return 0;
		} else if (recount || FontCombo.aClass95Array1 == null) {
			@Pc(20) int readyCount = 0;
			for (@Pc(29) int i = 0; i < FontCombo.fontFileIds.length; i++) {
				@Pc(35) int fileId = FontCombo.fontFileIds[i];
				if (FontCombo.loadingFontResource.isFileReady(fileId)) {
					readyCount++;
				}
				if (FontCombo.fontmetricsResource.isFileReady(fileId)) {
					readyCount++;
				}
			}
			return readyCount;
		} else {
			return FontCombo.fontFileIds.length * 2;
		}
	}
	@OriginalMember(owner = "client!jaa", name = "a", descriptor = "(ILclient!ha;BZ)Lclient!el;")
	public static FontType getFont(@OriginalArg(0) int fontId, @OriginalArg(1) Toolkit toolkit, @OriginalArg(3) boolean loadMetrics) {
		if (fontId == -1) {
			return null;
		}
		if (FontCombo.fontFileIds != null) {
			for (@Pc(15) int i = 0; i < FontCombo.fontFileIds.length; i++) {
				if (fontId == FontCombo.fontFileIds[i]) {
					return FontCombo.aClass95Array1[i];
				}
			}
		}
		@Pc(42) FontType fontType = (FontType) FontType.aClass169_15.get((long) fontId);
		if (fontType != null) {
			if (loadMetrics && fontType.aClass239_4 == null) {
				@Pc(54) FontMetrics fontMetrics = FontMetrics.list(fontId, FontCombo.fontmetricsResource);
				if (fontMetrics == null) {
					return null;
				}
				fontType.aClass239_4 = fontMetrics;
			}
			return fontType;
		}
		@Pc(75) SpriteType[] indexedImages = SpriteType.listGroup(FontCombo.loadingFontResource, fontId);
		if (indexedImages == null) {
			return null;
		}
		@Pc(84) FontMetrics fontMetrics2 = FontMetrics.list(fontId, FontCombo.fontmetricsResource);
		if (fontMetrics2 == null) {
			return null;
		}
		if (loadMetrics) {
			fontType = new FontType(toolkit.createFontRenderer(fontMetrics2, indexedImages), fontMetrics2);
		} else {
			fontType = new FontType(toolkit.createFontRenderer(fontMetrics2, indexedImages));
		}
		FontType.aClass169_15.put(fontType, (long) fontId);
		return fontType;
	}
	@OriginalMember(owner = "client!ls", name = "b", descriptor = "(I)V")
	public static void clearFontComboFonts() {
		FontCombo.aClass95Array1 = null;
	}

	@OriginalMember(owner = "client!cv", name = "d", descriptor = "(I)I")
	public static int getFontComboReadyFileCount() {
		return FontCombo.getReadyFileCount(false);
	}
}
