package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.client.features.MenuSwapper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: how a swap rule reads an entry (MenuSwapper). */
class MenuSwapperTest {

	@Test
	fun aRuleIsTheOptionOrItsFirstWord() {
		assertTrue(MenuSwapper.optionMatches("Mark tile", "mark"))
		assertTrue(MenuSwapper.optionMatches("Bank", "BANK"))
		assertFalse(MenuSwapper.optionMatches("Marketing", "mark"))
		assertFalse(MenuSwapper.optionMatches("Mark", "mark tile"))
	}

	@Test
	fun theTagsComeOffTheText() {
		assertEquals("Mark tile", ClientFeatures.plainText("Mark tile"))
		assertEquals("Bones", ClientFeatures.plainText("<col=ff9040>Bones</col> "))
		assertEquals("", ClientFeatures.plainText(null))
	}
}
