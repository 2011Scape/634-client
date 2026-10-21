package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.DiscordActivity
import com.jagex.game.runetek5.client.features.DiscordGameEvents
import com.jagex.game.runetek5.client.features.DiscordState
import com.jagex.game.runetek5.plugin.api.Skill
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** Not part of the original client: the Discord plugin's stack of events, after RuneLite's rules (DiscordState). */
class DiscordStateTest {

	private var now: Long = 1_000_000

	private val sent: ArrayList<DiscordActivity?> = ArrayList()

	private fun state(): DiscordState {
		val state: DiscordState = DiscordState({ now }, { activity -> sent.add(activity) })
		state.largeImageText = "Void v1"
		return state
	}

	private fun lumbridge() = DiscordGameEvents.fromRegion(12850)!!

	private fun varrock() = DiscordGameEvents.fromRegion(12853)!!

	@Test
	fun aSkillGivesTheDetailsAndThePlaceTheState() {
		val state: DiscordState = state()
		state.triggerEvent(DiscordGameEvents.IN_GAME)
		state.triggerEvent(lumbridge())
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.FISHING])
		val activity: DiscordActivity = assertNotNull(sent.last())
		assertEquals("Training: Fishing", activity.details)
		assertEquals("Lumbridge", activity.state)
		assertEquals("icon_fishing", activity.smallImageKey)
		assertEquals("Void v1", activity.largeImageText)
		assertEquals(3, state.eventCount())
	}

	@Test
	fun theHigherPrioritySkillNamesTheDetailsWhateverCameLast() {
		val state: DiscordState = state()
		state.triggerEvent(DiscordGameEvents.IN_GAME)
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.SLAYER])
		now += 10
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.CONSTITUTION])
		now += 10
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.ATTACK])
		assertEquals("Training: Slayer", sent.last()!!.details)
		assertEquals("In Game", sent.last()!!.state)
	}

	@Test
	fun theMostRecentOfEqualPrioritiesWins() {
		val state: DiscordState = state()
		state.triggerEvent(DiscordGameEvents.IN_GAME)
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.MINING])
		now += 10
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.SMITHING])
		assertEquals("Training: Smithing", sent.last()!!.details)
		now += 10
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.MINING])
		assertEquals("Training: Mining", sent.last()!!.details)
	}

	@Test
	fun aNewPlaceClearsTheSkillsAndTheOldPlaceButNotInGame() {
		val state: DiscordState = state()
		state.triggerEvent(DiscordGameEvents.IN_GAME)
		state.triggerEvent(lumbridge())
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.WOODCUTTING])
		state.triggerEvent(varrock())
		assertEquals(2, state.eventCount())
		assertEquals("", sent.last()!!.details)
		assertEquals("Varrock", sent.last()!!.state)
		assertNull(sent.last()!!.smallImageKey)
		state.triggerEvent(DiscordGameEvents.IN_GAME)
		assertEquals(1, state.eventCount(), "an unknown region drops the place too")
		assertEquals("In Game", sent.last()!!.state)
	}

	@Test
	fun aSkillClearsNothing() {
		val state: DiscordState = state()
		state.triggerEvent(DiscordGameEvents.IN_GAME)
		state.triggerEvent(lumbridge())
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.COOKING])
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.FIREMAKING])
		assertEquals(4, state.eventCount())
	}

	@Test
	fun onlySkillsTimeOutAndOnlyAfterTheirLastDrop() {
		val state: DiscordState = state()
		state.timeoutMillis = 1000
		state.triggerEvent(DiscordGameEvents.IN_GAME)
		state.triggerEvent(lumbridge())
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.FISHING])
		now += 900
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.FISHING])
		val before: Int = sent.size
		now += 900
		state.checkForTimeout()
		assertEquals(before, sent.size, "fished 900 ms ago: still shown")
		now += 200
		state.checkForTimeout()
		assertEquals("", sent.last()!!.details)
		assertEquals("Lumbridge", sent.last()!!.state, "the place and In Game stay")
		assertEquals(2, state.eventCount())
		now += 100_000
		state.checkForTimeout()
		assertEquals(2, state.eventCount())
	}

	@Test
	fun inMenuRestartsItsClockAndNeverExpires() {
		val state: DiscordState = state()
		state.elapsedMode = DiscordState.ELAPSED_ACTIVITY
		state.timeoutMillis = 1000
		state.triggerEvent(DiscordGameEvents.IN_MENU)
		assertEquals(now / 1000, sent.last()!!.startTimestamp)
		now += 5000
		state.checkForTimeout()
		assertEquals(1, state.eventCount())
		state.triggerEvent(DiscordGameEvents.IN_MENU)
		assertEquals(now / 1000, sent.last()!!.startTimestamp, "the clock started over")
	}

	@Test
	fun elapsedModesPickTheStart() {
		val state: DiscordState = state()
		state.triggerEvent(DiscordGameEvents.IN_GAME)
		val login: Long = now
		now += 60_000
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.MAGIC])
		assertEquals(login / 1000, sent.last()!!.startTimestamp, "total: since login")
		state.elapsedMode = DiscordState.ELAPSED_ACTIVITY
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.MAGIC])
		assertEquals(now / 1000, sent.last()!!.startTimestamp, "activity: since magic began")
		now += 1000
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.MAGIC])
		assertEquals((now - 1000) / 1000, sent.last()!!.startTimestamp, "the first drop, not the last")
		state.elapsedMode = DiscordState.ELAPSED_HIDDEN
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.MAGIC])
		assertEquals(0, sent.last()!!.startTimestamp)
	}

	@Test
	fun anUnchangedPresenceIsNotSentAgain() {
		val state: DiscordState = state()
		state.triggerEvent(DiscordGameEvents.IN_GAME)
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.FISHING])
		val count: Int = sent.size
		now += 10
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.FISHING])
		state.triggerEvent(DiscordGameEvents.TRAINING[Skill.FISHING])
		assertEquals(count, sent.size)
	}

	@Test
	fun resetClearsThePresence() {
		val state: DiscordState = state()
		state.triggerEvent(DiscordGameEvents.IN_GAME)
		state.reset()
		assertNull(sent.last())
		assertNull(state.lastActivity)
		assertEquals(0, state.eventCount())
	}
}
