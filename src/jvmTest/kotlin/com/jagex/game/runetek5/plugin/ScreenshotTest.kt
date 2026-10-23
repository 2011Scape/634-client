package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Screenshot
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Screenshot's triggers on Void's lines, and the names it saves under. */
class ScreenshotTest {

	@AfterTest
	fun tidy() {
		Screenshot.pendingFolder = null
		Screenshot.screenshotPlayerDeath = false
		Screenshot.screenshotKicks = false
		Screenshot.screenshotLevels = true
	}

	private fun chat(type: Int, text: String) = Screenshot.chat(ChatMessage(type, "", text, null))

	@Test
	fun aLevelUpIsTakenInLevels() {
		chat(ChatType.GAME, "You've just advanced an Attack level! You have reached level 99.")
		assertEquals("Levels", Screenshot.pendingFolder)
		assertEquals("Attack(99)", Screenshot.pendingName)
	}

	@Test
	fun deathsAndKicksOnlyWhenAsked() {
		chat(ChatType.GAME, "Oh dear, you are dead!")
		assertNull(Screenshot.pendingFolder)
		Screenshot.screenshotPlayerDeath = true
		chat(ChatType.GAME, "Oh dear, you are dead!")
		assertEquals("Deaths", Screenshot.pendingFolder)
		Screenshot.pendingFolder = null
		chat(11, "You have been kicked from the channel.")
		assertNull(Screenshot.pendingFolder)
		Screenshot.screenshotKicks = true
		chat(11, "You have been kicked from the channel.")
		assertEquals("Clan Chat Kicks", Screenshot.pendingFolder)
	}

	@Test
	fun oneAtATime() {
		Screenshot.request("Levels", "Attack(2)")
		Screenshot.request("Deaths", "Death")
		assertEquals("Attack(2)", Screenshot.pendingName)
	}

	@Test
	fun namesAreSafeForAnyDisk() {
		assertEquals("Quest(Rune_Mysteries_ )", ClientApiImpl.safeName("Quest(Rune/Mysteries? )"))
		assertEquals("Zezima/Levels", ClientApiImpl.safePath("Zezima//Levels/"))
		assertEquals("_", ClientApiImpl.safeName("..."))
		assertTrue(Screenshot.fileDate(0L).matches(Regex("\\d{4}-\\d\\d-\\d\\d_\\d\\d-\\d\\d-\\d\\d")))
	}
}
