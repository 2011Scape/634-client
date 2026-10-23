package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Party
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: the Party plugin's reading of the server's party variables (Party.read). */
class PartyTest {

	@AfterTest
	fun tidy() {
		Party.enabled = false
		Party.resetPings()
	}

	/** Void's Party.base37. */
	private fun base37(name: String): Pair<Int, Int> {
		var value = 0L
		for (char in name.lowercase().take(12)) {
			value *= 37
			value += when (char) {
				in 'a'..'z' -> char - 'a' + 1
				in '0'..'9' -> char - '0' + 27
				else -> 0
			}
		}
		return (value ushr 32).toInt() to value.toInt()
	}

	private fun pack(level: Int, x: Int, y: Int): Int = level shl 28 or (x shl 14) or y

	private fun vars(vararg members: Triple<String, Int, Int>, local: Int = 0, leader: Int = 0, invite: String? = null): Map<Int, Int> {
		val map: HashMap<Int, Int> = HashMap()
		if (members.isNotEmpty()) {
			map[Party.BASE] = members.size or (local shl 4) or (leader shl 8) or (1 shl 12)
		}
		for ((slot, member) in members.withIndex()) {
			val base: Int = Party.BASE + 1 + slot * Party.FIELDS
			val (hi, lo) = base37(member.first)
			map[base] = hi
			map[base + 1] = lo
			map[base + 2] = 450 shl 16 or 990
			map[base + 3] = 300 shl 16 or 990
			map[base + 4] = 75 shl 8 or 100
			map[base + 5] = if (member.second < 0) -1 else pack(member.third, member.second, 3200)
			map[base + 6] = if (member.second < 0) 1 else 0
		}
		invite?.let {
			val (hi, lo) = base37(it)
			map[Party.INVITE_HI] = hi
			map[Party.INVITE_LO] = lo
		}
		return map
	}

	@Test
	fun aTileIsNamedByItsArea() {
		assertEquals("Lumbridge", Party.areaName(3222, 3218))
		assertEquals("Edgeville", Party.areaName(3094, 3491))
		assertEquals("Tutorial Island", Party.areaName(3094, 3107))
		assertNull(Party.areaName(0, 0))
	}

	@Test
	fun theTooltipNamesWhereTheyAre() {
		val there = Party.Member(1, "Bob", 50, 99, 40, 70, 75, 82, 3222, 3218, 0, false)
		assertTrue(Party.tooltip(there).contains("Location: Lumbridge"))
		val hidden = Party.Member(1, "Bob", 50, 99, 40, 70, 75, 82, -1, -1, 0, true)
		assertTrue(Party.tooltip(hidden).contains("Location hidden (Wilderness)"))
		val nowhere = Party.Member(1, "Bob", 50, 99, 40, 70, 75, 82, 1, 1, 0, false)
		assertTrue(Party.tooltip(nowhere).contains("unknown area"))
	}

	@Test
	fun namesDecodeAsTheGameShowsThem() {
		val (hi, lo) = base37("bob the cat")
		assertEquals("Bob The Cat", Party.name(hi, lo))
		val (hi2, lo2) = base37("zezima12")
		assertEquals("Zezima12", Party.name(hi2, lo2))
		val (hi3, lo3) = base37("abcdefghijkl")
		assertEquals("Abcdefghijkl", Party.name(hi3, lo3))
		assertNull(Party.name(0, 0))
	}

	@Test
	fun theMembersAreRead() {
		val map = vars(Triple("alice", 3222, 0), Triple("bob", -1, 0), local = 1, leader = 0)
		val state: Party.State = Party.read { map[it] ?: 0 }
		assertTrue(state.inParty())
		assertEquals(listOf("Alice", "Bob"), state.members.map { it.name })
		assertEquals(1, state.local)
		assertEquals(0, state.leader)
		val alice: Party.Member = state.members[0]
		assertEquals(450, alice.hp)
		assertEquals(990, alice.hpMax)
		assertEquals(300, alice.prayer)
		assertEquals(75, alice.spec)
		assertEquals(100, alice.run)
		assertEquals(3222, alice.x)
		assertEquals(3200, alice.y)
		assertFalse(alice.inPvp)
		val bob: Party.Member = state.members[1]
		assertEquals(-1, bob.x, "a member in PvP has no tile")
		assertTrue(bob.inPvp)
		assertEquals(450, bob.hp, "but keeps their stats")
	}

	@Test
	fun noPartyButAnInvite() {
		val map = vars(invite = "carol")
		val state: Party.State = Party.read { map[it] ?: 0 }
		assertFalse(state.inParty())
		assertEquals("Carol", state.invite)
		assertFalse(Party.read { 0 }.inParty())
		assertNull(Party.read { 0 }.invite)
	}

	@Test
	fun theFirstPingCountReadIsOldAndAChangeIsAPing() {
		Party.enabled = true
		Party.pinged(null, 1 shl 24 or 5, pack(0, 3222, 3218))
		assertFalse(Party.pingShowing(), "the value found at login is an old ping")
		Party.pinged(null, 1 shl 24 or 6, pack(0, 3223, 3218))
		assertTrue(Party.pingShowing())
	}

	@Test
	fun theUnsetVariableIsMinusOneAndTheFirstPingStillShows() {
		Party.enabled = true
		Party.pinged(null, -1, -1)
		Party.pinged(null, 0 shl 24 or 1, pack(0, 3222, 3218))
		assertTrue(Party.pingShowing())
	}

	@Test
	fun aCountThatComesBeforeItsTileWaitsForIt() {
		Party.enabled = true
		Party.pinged(null, 0, 0)
		Party.pinged(null, 1 shl 24 or 7, 0)
		Party.pinged(null, 1 shl 24 or 7, pack(0, 3224, 3218))
		assertTrue(Party.pingShowing())
	}
}
