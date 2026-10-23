package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Emojis
import com.jagex.game.runetek5.plugin.api.ChatType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Emojis' rewriting of a line (Emojis.updateMessage). */
class EmojisTest {

	/** Each trigger's icon numbered 100 on. */
	private val icons: IntArray = IntArray(Emojis.TRIGGERS.size) { 100 + it }

	private fun icon(trigger: String): String = "<img=" + (100 + Emojis.TRIGGERS.indexOf(trigger)) + ">"

	@Test
	fun aTriggerWordBecomesItsIcon() {
		assertEquals("hello " + icon(":)"), Emojis.updateMessage("hello :)", icons))
	}

	@Test
	fun theEscapedHeartMatches() {
		assertEquals("i " + icon("<lt>3") + " you", Emojis.updateMessage("i <lt>3 you", icons))
	}

	@Test
	fun aTriggerInsideAWordIsLeft() {
		assertNull(Emojis.updateMessage("hello:) there", icons))
	}

	@Test
	fun aLineWithoutTriggersIsUnchanged() {
		assertNull(Emojis.updateMessage("nothing to see", icons))
	}

	@Test
	fun colourTagsAroundAWordAreIgnored() {
		assertEquals("<col=ff0000>" + icon(":D") + "</col>", Emojis.updateMessage("<col=ff0000>:D</col>", icons))
	}

	@Test
	fun theTriggersAreRuneLitesSixtySeven() {
		assertEquals(67, Emojis.TRIGGERS.size)
		assertEquals(67, Emojis.TRIGGERS.toSet().size)
		assertTrue(":$" in Emojis.TRIGGERS)
	}

	@Test
	fun onlyPlayersLinesAreChanged() {
		assertTrue(Emojis.isPlayerChat(ChatType.PUBLIC))
		assertTrue(Emojis.isPlayerChat(ChatType.FRIENDS_CHAT))
		assertFalse(Emojis.isPlayerChat(ChatType.GAME))
	}
}
