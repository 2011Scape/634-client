package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.DiscordAreaType
import com.jagex.game.runetek5.client.features.DiscordGameEvent
import com.jagex.game.runetek5.client.features.DiscordGameEvents
import com.jagex.game.runetek5.client.features.DiscordPresence
import com.jagex.game.runetek5.client.features.DiscordState
import com.jagex.game.runetek5.plugin.api.Skill
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: the Discord plugin's table of events and places (DiscordGameEvents). */
class DiscordGameEventsTest {

	@Test
	fun everyPlaceHasRegionsAndEveryRegionOnePlace() {
		var regions: Int = 0
		for (area in DiscordGameEvents.AREAS) {
			assertTrue(area.regionIds!!.isNotEmpty(), area.name)
			assertEquals(area.name, area.state)
			assertEquals(-2, area.priority)
			assertTrue(area.shouldClear && area.shouldBeCleared && !area.shouldTimeout && !area.shouldRestart, area.name)
			for (regionId in area.regionIds!!) {
				assertEquals(area, DiscordGameEvents.fromRegion(regionId), area.name + " " + regionId)
				regions++
			}
		}
		assertEquals(regions, DiscordGameEvents.regionCount(), "no region in two places")
	}

	@Test
	fun knownPlaces() {
		assertEquals("Lumbridge", DiscordGameEvents.fromRegion(12850)!!.state)
		assertEquals(DiscordAreaType.CITIES, DiscordGameEvents.fromRegion(12853)!!.areaType)
		assertEquals("Edgeville Dungeon", DiscordGameEvents.fromRegion(12441)!!.state)
		assertEquals(DiscordAreaType.DUNGEONS, DiscordGameEvents.fromRegion(12441)!!.areaType)
		assertEquals("Daemonheim", DiscordGameEvents.fromRegion(13626)!!.state)
		assertEquals("Soul Wars", DiscordGameEvents.fromRegion(7473)!!.state)
		assertEquals(DiscordAreaType.MINIGAMES, DiscordGameEvents.fromRegion(7473)!!.areaType)
		assertEquals("King Black Dragon", DiscordGameEvents.fromRegion(9033)!!.state)
		assertEquals(DiscordAreaType.BOSSES, DiscordGameEvents.fromRegion(9033)!!.areaType)
		assertEquals("Wilderness", DiscordGameEvents.fromRegion(12343)!!.state)
		assertEquals(DiscordAreaType.REGIONS, DiscordGameEvents.fromRegion(12343)!!.areaType)
		assertNull(DiscordGameEvents.fromRegion(0))
		assertNull(DiscordGameEvents.fromRegion(-1))
	}

	@Test
	fun skillsTrainAndRank() {
		for (skill in 0 until Skill.COUNT) {
			val event: DiscordGameEvent = DiscordGameEvents.fromSkill(skill)!!
			assertEquals("Training: " + Skill.NAMES[skill], event.details)
			assertEquals("icon_" + Skill.NAMES[skill].lowercase(), event.imageKey)
			assertNull(event.state)
			assertTrue(event.shouldTimeout && event.shouldBeCleared && !event.shouldClear && !event.shouldRestart, event.name)
		}
		assertEquals(-1, DiscordGameEvents.fromSkill(Skill.CONSTITUTION)!!.priority)
		assertEquals(1, DiscordGameEvents.fromSkill(Skill.SLAYER)!!.priority)
		assertEquals(1, DiscordGameEvents.fromSkill(Skill.FISHING)!!.priority)
		assertEquals(0, DiscordGameEvents.fromSkill(Skill.DUNGEONEERING)!!.priority)
		assertEquals("icon_runecrafting", DiscordGameEvents.fromSkill(Skill.RUNECRAFTING)!!.imageKey)
		assertNull(DiscordGameEvents.fromSkill(Skill.COUNT))
		assertNull(DiscordGameEvents.fromSkill(-1))
	}

	@Test
	fun theTwoStates() {
		assertTrue(DiscordGameEvents.IN_MENU.shouldRestart && DiscordGameEvents.IN_MENU.shouldTimeout && !DiscordGameEvents.IN_MENU.shouldBeCleared)
		assertFalse(DiscordGameEvents.IN_GAME.shouldRestart || DiscordGameEvents.IN_GAME.shouldTimeout || DiscordGameEvents.IN_GAME.shouldBeCleared)
		assertEquals(-3, DiscordGameEvents.IN_GAME.priority)
	}

	@Test
	fun theElapsedOptionsMapToModes() {
		assertEquals(DiscordState.ELAPSED_TOTAL, DiscordPresence.elapsedMode("Total elapsed time"))
		assertEquals(DiscordState.ELAPSED_ACTIVITY, DiscordPresence.elapsedMode("Per activity"))
		assertEquals(DiscordState.ELAPSED_HIDDEN, DiscordPresence.elapsedMode("Hide elapsed time"))
		assertEquals(DiscordState.ELAPSED_TOTAL, DiscordPresence.elapsedMode("nonsense"))
	}
}
