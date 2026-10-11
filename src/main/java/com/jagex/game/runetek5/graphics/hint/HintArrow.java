package com.jagex.game.runetek5.graphics.hint;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.sprite.StaticMedia;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.OverlayManager;

@OriginalClass("client!wo")
public final class HintArrow {

	@OriginalMember(owner = "client!wo", name = "a", descriptor = "I")
	public int arrowSprite;

	@OriginalMember(owner = "client!wo", name = "b", descriptor = "I")
	public int y;

	@OriginalMember(owner = "client!wo", name = "c", descriptor = "I")
	public int targetIndex;

	@OriginalMember(owner = "client!wo", name = "e", descriptor = "I")
	public int x;

	@OriginalMember(owner = "client!wo", name = "g", descriptor = "I")
	public int distance;

	@OriginalMember(owner = "client!wo", name = "h", descriptor = "I")
	public int plane;

	@OriginalMember(owner = "client!wo", name = "i", descriptor = "I")
	public int height;

	@OriginalMember(owner = "client!wo", name = "j", descriptor = "I")
	public int targetType;

	@OriginalMember(owner = "client!wo", name = "d", descriptor = "I")
	public int modelId = -1;
	@OriginalMember(owner = "client!qf", name = "d", descriptor = "I")
	public static int hintPreferenceFlags;
	@OriginalMember(owner = "client!nr", name = "b", descriptor = "I")
	public static int scaledOrthoZoom = OrthoMode.orthoZoom;

	@OriginalMember(owner = "client!eca", name = "q", descriptor = "Lclient!jr;")
	public static final LRUCache hintCache = new LRUCache(32);
	@OriginalMember(owner = "client!ur", name = "a", descriptor = "(IZ)V")
	public static void setHintPreferenceFlags(@OriginalArg(0) int hintPreferenceFlags) {
		HintArrow.hintPreferenceFlags = hintPreferenceFlags;
		@Pc(12) LRUCache referenceCache = HintArrow.hintCache;
		synchronized (HintArrow.hintCache) {
			HintArrow.hintCache.reset();
		}
	}
	@OriginalMember(owner = "client!pl", name = "a", descriptor = "(II)V")
	public static void cacheClean() {
		@Pc(1) LRUCache referenceCache = HintArrow.hintCache;
		synchronized (HintArrow.hintCache) {
			HintArrow.hintCache.clean(5);
		}
	}
	@OriginalMember(owner = "client!eea", name = "e", descriptor = "(I)V")
	public static void cacheReset() {
		@Pc(10) LRUCache referenceCache = HintArrow.hintCache;
		synchronized (HintArrow.hintCache) {
			HintArrow.hintCache.reset();
		}
	}
	@OriginalMember(owner = "client!pe", name = "b", descriptor = "(I)V")
	public static void cacheRemoveSoftReferences() {
		@Pc(5) LRUCache referenceCache = HintArrow.hintCache;
		synchronized (HintArrow.hintCache) {
			HintArrow.hintCache.removeSoftReferences();
		}
	}
	@OriginalMember(owner = "client!uo", name = "a", descriptor = "(IIIIIII)V")
	public static void drawHintArrows(@OriginalArg(0) int x, @OriginalArg(2) int y, @OriginalArg(3) int width, @OriginalArg(5) int height) {
		@Pc(14) HintArrow[] hintArrows = PlayerUpdate.HINT_ARROWS;
		for (@Pc(16) int i = 0; i < hintArrows.length; i++) {
			@Pc(22) HintArrow hintArrow = hintArrows[i];
			if (hintArrow != null && hintArrow.targetType == 2) {
				OverlayManager.projectToScreen(width >> 1, hintArrow.height * 2, height >> 1, hintArrow.plane, hintArrow.y, hintArrow.x);
				if (OverlayManager.projectedScreenPosition[0] > -1 && ClientState.clientCycle % 20 < 10) {
					@Pc(66) NativeSprite sprite = StaticMedia.hintHeadiconSprites[hintArrow.arrowSprite];
					@Pc(75) int spriteX = x + OverlayManager.projectedScreenPosition[0] - 12;
					@Pc(84) int spriteY = y + OverlayManager.projectedScreenPosition[1] - 28;
					sprite.render(spriteX, spriteY);
					OrthoMode.markOrthoTilesDirty(spriteX, spriteX + sprite.scaleWidth(), spriteY, sprite.scaleHeight() + spriteY);
				}
			}
		}
	}
}
