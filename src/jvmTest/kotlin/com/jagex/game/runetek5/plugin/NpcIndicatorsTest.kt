package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.NpcIndicators
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: the npc indicators' list, wildcards and menu colouring (NpcIndicators). */
class NpcIndicatorsTest {

	@Test
	fun wildcardsMatchAsRuneLitesDo() {
		assertTrue(NpcIndicators.wildcardMatches("TzHaar-Hur", "tzhaar-hur"), "case aside")
		assertTrue(NpcIndicators.wildcardMatches("TzHaar*", "TzHaar-Ket"))
		assertTrue(NpcIndicators.wildcardMatches("*impling", "Dragon impling"))
		assertTrue(NpcIndicators.wildcardMatches("*", "Anything"))
		assertTrue(NpcIndicators.wildcardMatches("Gob*lin", "Goblin"))
		assertFalse(NpcIndicators.wildcardMatches("TzHaar*", "Goblin"))
		assertFalse(NpcIndicators.wildcardMatches("Goblin", "Goblin guard"))
		assertFalse(NpcIndicators.wildcardMatches("*impling", "Impling jar"))
	}

	@Test
	fun theListIsCommaSeparatedAndBlanksAreDropped() {
		NpcIndicators.setNames("TzHaar*, *impling,, Goblin ,")
		assertEquals(listOf("TzHaar*", "*impling", "Goblin"), NpcIndicators.names)
		assertEquals("TzHaar*, *impling, Goblin", NpcIndicators.namesCsv())
		assertTrue(NpcIndicators.listed("goblin"))
		assertFalse(NpcIndicators.listed("TzHaar-Ket"), "a wildcard match is not listed as itself")
		assertTrue(NpcIndicators.matchedByWildcard("TzHaar-Ket"))
		assertFalse(NpcIndicators.matchedByWildcard("Goblin"))
		assertTrue(NpcIndicators.matches("Essence impling"))
		NpcIndicators.setNames("")
		assertEquals(0, NpcIndicators.names.size)
	}

	@Test
	fun aMenuTargetIsRecolouredByStyle() {
		val target: String = "<col=ffff00>Goblin<col=00ff00> (level: 2)"
		assertEquals("<col=00ffff>Goblin<col=00ff00> (level: 2)", NpcIndicators.recolourTarget(target, NpcIndicators.MENU_NAME, 0xFF00FFFF.toInt()))
		assertEquals("<col=ffff00>Goblin<col=00ffff> (level: 2)", NpcIndicators.recolourTarget(target, NpcIndicators.MENU_LEVEL, 0xFF00FFFF.toInt()))
		assertEquals("<col=00ffff>Goblin<col=00ffff> (level: 2)", NpcIndicators.recolourTarget(target, NpcIndicators.MENU_BOTH, 0xFF00FFFF.toInt()))
		assertEquals(target, NpcIndicators.recolourTarget(target, NpcIndicators.MENU_NONE, 0xFF00FFFF.toInt()))
		assertEquals("<col=ff0000>Banker", NpcIndicators.recolourTarget("Banker", NpcIndicators.MENU_BOTH, 0xFFFF0000.toInt()), "an uncoloured target gets the tag")
		assertEquals("Banker", NpcIndicators.recolourTarget("Banker", NpcIndicators.MENU_LEVEL, 0xFFFF0000.toInt()), "no level to colour")
	}

	@Test
	fun coloursHaveNamesAndNoneIsNothing() {
		assertEquals(0xFF00FFFF.toInt(), NpcIndicators.colourNamed("Cyan", 0))
		assertEquals(0, NpcIndicators.colourNamed("None", 0))
		assertEquals(0xFFFFFF00.toInt(), NpcIndicators.colourNamed("no such", 0xFFFFFF00.toInt()))
	}
}
