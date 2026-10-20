package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.event.keyboard.SimpleKeyboardMonitorConstants
import com.jagex.game.runetek5.plugin.api.KeyCode
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: the API's key codes must be the client's (KeyCode). */
class KeyCodeTest {

	@Test
	fun theApisCodesAreTheClients() {
		assertEquals(SimpleKeyboardMonitorConstants.KEY_CODE_SHIFT, KeyCode.SHIFT)
		assertEquals(SimpleKeyboardMonitorConstants.KEY_CODE_CONTROL, KeyCode.CONTROL)
		assertEquals(SimpleKeyboardMonitorConstants.KEY_CODE_ALT, KeyCode.ALT)
	}
}
