package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.MouseTooltips
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: which top entries Mouse Tooltips shows, and how (MouseTooltips.tooltip). */
class MouseTooltipsTest {

	@BeforeTest
	fun fresh() {
		MouseTooltips.uiTooltip = true
		MouseTooltips.chatboxTooltip = true
		MouseTooltips.disableSpellbooktooltip = false
	}

	@AfterTest
	fun tidy() {
		fresh()
	}

	@Test
	fun anNpcsEntryIsItsOptionAndTarget() {
		assertEquals("Attack <col=ffff00>Goblin", MouseTooltips.tooltip("Attack", "<col=ffff00>Goblin", 25, 0))
	}

	@Test
	fun walkCancelAndContinueAreLeftOut() {
		assertNull(MouseTooltips.tooltip("Walk here", "", 19, 0))
		assertNull(MouseTooltips.tooltip("Cancel", "", 1004, 0))
		assertNull(MouseTooltips.tooltip("Continue", "", 16, 0))
		assertNull(MouseTooltips.tooltip("Move", "Sliding piece", 18, 0))
	}

	@Test
	fun theOverlaysMenuIsLeftOut() {
		assertNull(MouseTooltips.tooltip("Configure", "Panel", 1014, 0))
	}

	@Test
	fun interfaceEntriesFollowTheirSettings() {
		val backpack: Int = 149 shl 16
		val chat: Int = 137 shl 16
		val spells: Int = 192 shl 16
		assertEquals("Drop <col=ff9040>Coins", MouseTooltips.tooltip("Drop", "<col=ff9040>Coins", 1011, backpack))
		MouseTooltips.chatboxTooltip = false
		assertNull(MouseTooltips.tooltip("Report", "", 18, chat))
		MouseTooltips.disableSpellbooktooltip = true
		assertNull(MouseTooltips.tooltip("Cast", "Wind Strike", 2018, spells))
		MouseTooltips.uiTooltip = false
		assertNull(MouseTooltips.tooltip("Drop", "Coins", 1011, backpack))
		assertEquals("Attack Goblin", MouseTooltips.tooltip("Attack", "Goblin", 25, 0))
	}

	@Test
	fun anEntryWithoutATargetIsItsOption() {
		assertEquals("Logout", MouseTooltips.tooltip("Logout", "", 18, 182 shl 16))
	}
}
