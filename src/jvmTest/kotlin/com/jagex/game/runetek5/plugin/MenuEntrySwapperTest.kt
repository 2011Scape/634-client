package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.features.MenuEntrySwapper
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: the menu entry swapper's keys and kinds (MenuEntrySwapper). */
class MenuEntrySwapperTest {

	@Test
	fun aSwapIsKeyedByKindSideAndThing() {
		assertEquals("npc_12", MenuEntrySwapper.key(MenuEntrySwapper.KIND_NPC, 12, 0, false))
		assertEquals("npc_shift_12", MenuEntrySwapper.key(MenuEntrySwapper.KIND_NPC, 12, 0, true))
		assertEquals("loc_2213", MenuEntrySwapper.key(MenuEntrySwapper.KIND_LOC, 2213, 0, false))
		assertEquals("ground_995", MenuEntrySwapper.key(MenuEntrySwapper.KIND_GROUND, 995, 0, false))
		assertEquals("item_526", MenuEntrySwapper.key(MenuEntrySwapper.KIND_ITEM, 526, 149 shl 16, false))
		assertEquals("ui_shift_25362432_995", MenuEntrySwapper.key(MenuEntrySwapper.KIND_WIDGET, 995, 387 shl 16, true))
	}

	@Test
	fun anEntrysKindComesFromItsClickType() {
		assertEquals(MenuEntrySwapper.KIND_NPC, MenuEntrySwapper.kindOf(MenuIndicatorsClick.OPNPC2, 0, -1))
		assertEquals(MenuEntrySwapper.KIND_NPC, MenuEntrySwapper.kindOf(MiniMenuAction.OPNPC2 + 2000, 0, -1), "an attack on a higher level")
		assertEquals(MenuEntrySwapper.KIND_LOC, MenuEntrySwapper.kindOf(MiniMenuAction.OPLOC1, 0, -1))
		assertEquals(MenuEntrySwapper.KIND_GROUND, MenuEntrySwapper.kindOf(MiniMenuAction.OPOBJ3, 0, 995))
		assertEquals(MenuEntrySwapper.KIND_ITEM, MenuEntrySwapper.kindOf(MiniMenuAction.IF_BUTTONX1, 149 shl 16, 526), "an item in the backpack")
		assertEquals(MenuEntrySwapper.KIND_WIDGET, MenuEntrySwapper.kindOf(MiniMenuAction.IF_BUTTONX2, 387 shl 16, 1163), "an item in another component")
		assertEquals(-1, MenuEntrySwapper.kindOf(MiniMenuAction.IF_BUTTONX1, 149 shl 16, -1), "a component with no item")
		assertEquals(-1, MenuEntrySwapper.kindOf(MiniMenuAction.WALK, 0, -1))
	}

	@Test
	fun theTagsComeOffTheText() {
		assertEquals("Mark tile", ClientFeatures.plainText("Mark tile"))
		assertEquals("Bones", ClientFeatures.plainText("<col=ff9040>Bones</col> "))
		assertEquals("", ClientFeatures.plainText(null))
	}
}

/** The npc click type the test reads by its own name, so the test reads as the plugin does. */
private object MenuIndicatorsClick {
	const val OPNPC2: Int = MiniMenuAction.OPNPC2
}
