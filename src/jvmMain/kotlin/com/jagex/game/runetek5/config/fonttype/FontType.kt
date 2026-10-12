package com.jagex.game.runetek5.config.fonttype

import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.node.lru.LRUCache

@OriginalClass("client!el")
class FontType {

	@OriginalMember(owner = "client!el", name = "d", descriptor = "Lclient!oea;")
	@JvmField
	var aClass239_4: FontMetrics? = null

	@OriginalMember(owner = "client!el", name = "c", descriptor = "Lclient!da;")
	@JvmField
	var font: AbstractFontRenderer? = null

	@OriginalMember(owner = "client!el", name = "<init>", descriptor = "(Lclient!da;)V")
	constructor(@OriginalArg(0) font: AbstractFontRenderer?) {
		this.font = font
	}

	@OriginalMember(owner = "client!el", name = "<init>", descriptor = "(Lclient!da;Lclient!oea;)V")
	constructor(@OriginalArg(0) font: AbstractFontRenderer?, @OriginalArg(1) fontMetrics: FontMetrics?) {
		this.aClass239_4 = fontMetrics
		this.font = font
	}

	companion object {
		@OriginalMember(owner = "client!dj", name = "b", descriptor = "Lclient!jr;")
		@JvmField
		val aClass169_15: LRUCache = LRUCache(20)

		@OriginalMember(owner = "client!ak", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun cacheClean() {
			FontType.aClass169_15.clean(50)
		}

		@OriginalMember(owner = "client!rn", name = "f", descriptor = "(I)V")
		@JvmStatic
		fun removeFontTypeSoftReferences() {
			FontType.aClass169_15.removeSoftReferences()
		}

		@OriginalMember(owner = "client!lca", name = "a", descriptor = "(I)V")
		@JvmStatic
		fun resetFontTypeCache() {
			FontType.aClass169_15.reset()
		}
	}
}
