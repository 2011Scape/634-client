package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ChatCommands
import com.jagex.game.runetek5.client.features.HiscoreLookup
import com.jagex.game.runetek5.plugin.api.Skill
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Chat Commands' parsing and answers (ChatCommands). */
class ChatCommandsTest {

	private val red: String = ChatCommands.HIGHLIGHT
	private val end: String = ChatCommands.NORMAL

	private fun hiscores(level: Int): HiscoreLookup.Result {
		val r = HiscoreLookup.Result("zezima", IntArray(Skill.COUNT) { level }, IntArray(Skill.COUNT) { 1_234 }, LongArray(Skill.COUNT) { 13_034_431L })
		r.overallLevel = level * Skill.COUNT
		r.overallXp = 13_034_431L * Skill.COUNT
		r.overallRank = 7
		return r
	}

	@Test
	fun skillsByNameAbbreviationOrTotal() {
		assertEquals(Skill.ATTACK, ChatCommands.skillOf("attack"))
		assertEquals(Skill.ATTACK, ChatCommands.skillOf("ATT"))
		assertEquals(Skill.CONSTITUTION, ChatCommands.skillOf("hp"))
		assertEquals(Skill.DUNGEONEERING, ChatCommands.skillOf("dg"))
		assertEquals(Skill.RUNECRAFTING, ChatCommands.skillOf("rc"))
		assertEquals(ChatCommands.OVERALL, ChatCommands.skillOf("total"))
		assertNull(ChatCommands.skillOf("sailing"))
	}

	@Test
	fun levelAsRuneLiteWritesIt() {
		assertEquals("Level ${red}Attack: 99$end Experience: ${red}13,034,431$end Rank: ${red}1,234$end", ChatCommands.hiscoreAnswer(hiscores(99), "!lvl att"))
		assertEquals("Level ${red}Overall: 2475$end Experience: ${red}325,860,775$end Rank: ${red}7$end", ChatCommands.hiscoreAnswer(hiscores(99), "!total"))
		assertNull(ChatCommands.hiscoreAnswer(hiscores(99), "!lvl sailing"))
	}

	@Test
	fun combatWithSummoning() {
		// 99s everywhere with Summoning: 138
		val answer: String = ChatCommands.combatAnswer(hiscores(99))!!
		assertTrue(answer.startsWith("Combat Level: ${red}138$end A: ${red}99$end"), answer)
		assertTrue(answer.endsWith("Su: ${red}99$end"), answer)
		// unranked: Constitution 10, the rest 1
		assertTrue(ChatCommands.combatAnswer(hiscores(-1))!!.startsWith("Combat Level: ${red}3$end"))
	}

	@Test
	fun priceSearchPrefersTheExactName() {
		val names: Array<String?> = arrayOf(null, "rune scimitar", "rune", "rune platebody", "dragon scimitar")
		val prices: (Int) -> Int = { if (it == 3) 0 else 100 }
		assertEquals(2, ChatCommands.bestMatch(names, "Rune", prices))
		assertEquals(1, ChatCommands.bestMatch(names, "scim", prices))
		// a name with no guide price is not one
		assertNull(ChatCommands.bestMatch(names, "platebody", prices))
		assertNull(ChatCommands.bestMatch(names, "", prices))
	}

	@Test
	fun whichLinesAreCommands() {
		assertTrue(ChatCommands.needsHiscores("!lvl attack"))
		assertTrue(ChatCommands.needsHiscores("!TOTAL"))
		assertTrue(ChatCommands.needsHiscores("!cmb"))
		assertFalse(ChatCommands.needsHiscores("!lvlattack"))
		assertTrue(ChatCommands.isCommandType(2) && ChatCommands.isCommandType(6) && ChatCommands.isCommandType(9))
		assertFalse(ChatCommands.isCommandType(0))
		assertEquals("Quest points: ${red}54$end", ChatCommands.questPointsAnswer(54))
	}
}
