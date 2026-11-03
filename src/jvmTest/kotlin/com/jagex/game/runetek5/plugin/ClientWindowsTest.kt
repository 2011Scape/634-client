package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ClientWindows
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClientWindowsTest {

	@Test
	fun theGameWorldsEntriesAreSceneEntriesAndTheInterfacesAreNot() {
		for (type in intArrayOf(MiniMenuAction.WALK, MiniMenuAction.OPNPC1, MiniMenuAction.OPNPC6, MiniMenuAction.OPLOC1, MiniMenuAction.OPLOC6, MiniMenuAction.OPOBJ1, MiniMenuAction.OPOBJ6, MiniMenuAction.OPPLAYER1, MiniMenuAction.OPPLAYER8, MiniMenuAction.TGT_NPC, MiniMenuAction.TGT_GROUND)) {
			assertTrue(ClientWindows.sceneEntry(type), "scene: $type")
			assertTrue(ClientWindows.sceneEntry(type + 2000), "scene with an item selected: $type")
		}
		for (type in intArrayOf(MiniMenuAction.CANCEL, MiniMenuAction.IF_BUTTONX1, MiniMenuAction.IF_BUTTONX2, MiniMenuAction.IF_BUTTONT, MiniMenuAction.TGT_BUTTON, MiniMenuAction.PAUSE_BUTTON, MiniMenuAction.OVERLAY_MENU, MiniMenuAction.PLUGIN_FIRST, MiniMenuAction.OP_MAPELEMENT1)) {
			assertFalse(ClientWindows.sceneEntry(type), "not scene: $type")
		}
	}
}
