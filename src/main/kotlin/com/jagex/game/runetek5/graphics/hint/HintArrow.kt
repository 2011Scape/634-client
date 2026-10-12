package com.jagex.game.runetek5.graphics.hint

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.OverlayManager

@OriginalClass("client!wo")
class HintArrow {

	@OriginalMember(owner = "client!wo", name = "a", descriptor = "I")
	@JvmField
	var arrowSprite: Int = 0

	@OriginalMember(owner = "client!wo", name = "b", descriptor = "I")
	@JvmField
	var y: Int = 0

	@OriginalMember(owner = "client!wo", name = "c", descriptor = "I")
	@JvmField
	var targetIndex: Int = 0

	@OriginalMember(owner = "client!wo", name = "e", descriptor = "I")
	@JvmField
	var x: Int = 0

	@OriginalMember(owner = "client!wo", name = "g", descriptor = "I")
	@JvmField
	var distance: Int = 0

	@OriginalMember(owner = "client!wo", name = "h", descriptor = "I")
	@JvmField
	var plane: Int = 0

	@OriginalMember(owner = "client!wo", name = "i", descriptor = "I")
	@JvmField
	var height: Int = 0

	@OriginalMember(owner = "client!wo", name = "j", descriptor = "I")
	@JvmField
	var targetType: Int = 0

	@OriginalMember(owner = "client!wo", name = "d", descriptor = "I")
	@JvmField
	var modelId: Int = -1

	companion object {
		@OriginalMember(owner = "client!qf", name = "d", descriptor = "I")
		@JvmField
		var hintPreferenceFlags: Int = 0
		@OriginalMember(owner = "client!nr", name = "b", descriptor = "I")
		@JvmField
		var scaledOrthoZoom: Int = OrthoMode.orthoZoom

		@OriginalMember(owner = "client!eca", name = "q", descriptor = "Lclient!jr;")
		@JvmField
		val hintCache: LRUCache = LRUCache(32)
		@OriginalMember(owner = "client!ur", name = "a", descriptor = "(IZ)V")
		@JvmStatic
		fun setHintPreferenceFlags(@OriginalArg(0) hintPreferenceFlags: Int) {
			HintArrow.hintPreferenceFlags = hintPreferenceFlags
			@Pc(12) val referenceCache: LRUCache = HintArrow.hintCache
			synchronized(HintArrow.hintCache) {
				HintArrow.hintCache.reset()
			}
		}
		@OriginalMember(owner = "client!pl", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun cacheClean() {
			@Pc(1) val referenceCache: LRUCache = HintArrow.hintCache
			synchronized(HintArrow.hintCache) {
				HintArrow.hintCache.clean(5)
			}
		}
		@OriginalMember(owner = "client!eea", name = "e", descriptor = "(I)V")
		@JvmStatic
		fun cacheReset() {
			@Pc(10) val referenceCache: LRUCache = HintArrow.hintCache
			synchronized(HintArrow.hintCache) {
				HintArrow.hintCache.reset()
			}
		}
		@OriginalMember(owner = "client!pe", name = "b", descriptor = "(I)V")
		@JvmStatic
		fun cacheRemoveSoftReferences() {
			@Pc(5) val referenceCache: LRUCache = HintArrow.hintCache
			synchronized(HintArrow.hintCache) {
				HintArrow.hintCache.removeSoftReferences()
			}
		}
		@OriginalMember(owner = "client!uo", name = "a", descriptor = "(IIIIIII)V")
		@JvmStatic
		fun drawHintArrows(@OriginalArg(0) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) width: Int, @OriginalArg(5) height: Int) {
			@Pc(14) val hintArrows: Array<HintArrow?> = PlayerUpdate.HINT_ARROWS
			@Pc(16) var i: Int = 0
			while (i < hintArrows.size) {
				@Pc(22) val hintArrow: HintArrow? = hintArrows[i]
				if (hintArrow != null && hintArrow.targetType == 2) {
					OverlayManager.projectToScreen(width shr 1, hintArrow.height * 2, height shr 1, hintArrow.plane, hintArrow.y, hintArrow.x)
					if (OverlayManager.projectedScreenPosition[0] > -1 && ClientState.clientCycle % 20 < 10) {
						@Pc(66) val sprite: NativeSprite? = StaticMedia.hintHeadiconSprites!![hintArrow.arrowSprite]
						@Pc(75) val spriteX: Int = x + OverlayManager.projectedScreenPosition[0] - 12
						@Pc(84) val spriteY: Int = y + OverlayManager.projectedScreenPosition[1] - 28
						sprite!!.render(spriteX, spriteY)
						OrthoMode.markOrthoTilesDirty(spriteX, spriteX + sprite.scaleWidth(), spriteY, sprite.scaleHeight() + spriteY)
					}
				}
				i++
			}
		}
	}
}
