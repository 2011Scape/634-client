package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.OverlayLayer
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OverlayLayoutTest {

	private fun panel(name: String, layer: OverlayLayer): PanelOverlay = object : PanelOverlay(name) {
		override fun render(g: Graphics, view: Viewport): Size? = null
	}.also { it.layer = layer }

	@Test
	fun theHoverGoesToThePanelDrawnLastOfALayer() {
		val roster = panel("roster", OverlayLayer.UNDER_WIDGETS)
		val picker = panel("picker", OverlayLayer.UNDER_WIDGETS)
		assertTrue(OverlayLayout.takesHover(null, roster), "the first panel under the mouse takes it")
		assertTrue(OverlayLayout.takesHover(roster, picker), "a window drawn over a HUD panel takes the clicks aimed at it")
		assertTrue(OverlayLayout.takesHover(picker, roster), "and back again, in draw order: the last drawn is on top")
	}

	@Test
	fun aPanelOverTheInterfacesKeepsTheHoverFromOneUnder() {
		val roster = panel("roster", OverlayLayer.UNDER_WIDGETS)
		val loadout = panel("loadout", OverlayLayer.ABOVE_WIDGETS)
		assertTrue(OverlayLayout.takesHover(roster, loadout), "over the interfaces beats under them")
		assertFalse(OverlayLayout.takesHover(loadout, roster), "and is not taken back by one under them")
	}
}
