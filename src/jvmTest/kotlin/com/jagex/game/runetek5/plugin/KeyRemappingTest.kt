package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.KeyRemapping
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: Key Remapping's keys while the chat is locked (KeyRemapping.remap), and the chat line's start. */
class KeyRemappingTest {

	@BeforeTest
	fun fresh() {
		KeyRemapping.cameraRemap = true
		KeyRemapping.up = KeyRemapping.W
		KeyRemapping.down = KeyRemapping.S
		KeyRemapping.left = KeyRemapping.A
		KeyRemapping.right = KeyRemapping.D
		KeyRemapping.fkeyRemap = false
		KeyRemapping.DEFAULT_F_KEYS.copyInto(KeyRemapping.fKeys)
		KeyRemapping.esc = KeyRemapping.ESCAPE
		KeyRemapping.space = KeyRemapping.SPACE
		KeyRemapping.control = 0
		KeyRemapping.dialogOpen = false
		KeyRemapping.optionsDialogOpen = false
	}

	@AfterTest
	fun tidy() {
		fresh()
	}

	@Test
	fun wasdTurnTheCamera() {
		assertEquals(KeyRemapping.UP, KeyRemapping.remap(KeyRemapping.W))
		assertEquals(KeyRemapping.LEFT, KeyRemapping.remap(KeyRemapping.A))
		assertEquals(KeyRemapping.DOWN, KeyRemapping.remap(KeyRemapping.S))
		assertEquals(KeyRemapping.RIGHT, KeyRemapping.remap(KeyRemapping.D))
		KeyRemapping.cameraRemap = false
		assertEquals(0, KeyRemapping.remap(KeyRemapping.W))
	}

	@Test
	fun theNumberKeysAreFKeysOnlyWhenAskedAndNotInADialogue() {
		assertEquals(0, KeyRemapping.remap(16))
		KeyRemapping.fkeyRemap = true
		assertEquals(1, KeyRemapping.remap(16))
		assertEquals(10, KeyRemapping.remap(25))
		assertEquals(12, KeyRemapping.remap(27))
		KeyRemapping.dialogOpen = true
		assertEquals(0, KeyRemapping.remap(16))
	}

	@Test
	fun spaceStandsInOnlyInADialogueThatIsNotAMenu() {
		KeyRemapping.space = 51
		assertEquals(0, KeyRemapping.remap(51))
		KeyRemapping.dialogOpen = true
		assertEquals(KeyRemapping.SPACE, KeyRemapping.remap(51))
		KeyRemapping.optionsDialogOpen = true
		assertEquals(0, KeyRemapping.remap(51))
	}

	@Test
	fun theChatLinesStartIsTheNameAndTheColour() {
		assertEquals("smoketest<img=3>: <col=7fa9ff>", KeyRemapping.prefix("smoketest<img=3>: <col=7fa9ff>hello"))
		assertEquals("Zezima: ", KeyRemapping.prefix("Zezima: hi"))
		assertNull(KeyRemapping.prefix("Enter amount"))
	}
}
