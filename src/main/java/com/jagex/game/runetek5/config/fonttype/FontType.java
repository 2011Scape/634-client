package com.jagex.game.runetek5.config.fonttype;

import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;

@OriginalClass("client!el")
public final class FontType {

	@OriginalMember(owner = "client!el", name = "d", descriptor = "Lclient!oea;")
	public FontMetrics aClass239_4 = null;

	@OriginalMember(owner = "client!el", name = "c", descriptor = "Lclient!da;")
	public AbstractFontRenderer font = null;

	@OriginalMember(owner = "client!el", name = "<init>", descriptor = "(Lclient!da;)V")
	public FontType(@OriginalArg(0) AbstractFontRenderer font) {
		this.font = font;
	}

	@OriginalMember(owner = "client!el", name = "<init>", descriptor = "(Lclient!da;Lclient!oea;)V")
	public FontType(@OriginalArg(0) AbstractFontRenderer font, @OriginalArg(1) FontMetrics fontMetrics) {
		this.aClass239_4 = fontMetrics;
		this.font = font;
	}
	@OriginalMember(owner = "client!dj", name = "b", descriptor = "Lclient!jr;")
	public static final LRUCache aClass169_15 = new LRUCache(20);
	@OriginalMember(owner = "client!ak", name = "a", descriptor = "(II)V")
	public static void cacheClean() {
		FontType.aClass169_15.clean(50);
	}
	@OriginalMember(owner = "client!rn", name = "f", descriptor = "(I)V")
	public static void removeFontTypeSoftReferences() {
		FontType.aClass169_15.removeSoftReferences();
	}

	@OriginalMember(owner = "client!lca", name = "a", descriptor = "(I)V")
	public static void resetFontTypeCache() {
		FontType.aClass169_15.reset();
	}
}
