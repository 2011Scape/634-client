package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.InteractHighlight
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.plugin.api.HoveredTarget
import com.jagex.game.runetek5.plugin.api.MenuTarget
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.NpcDefinition
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: what a menu entry acts on (HoveredTargets) and Interact Highlight's rules (InteractHighlight). */
class InteractHighlightTest {

	private val out: HoveredTarget = HoveredTarget()

	@AfterTest
	fun tidy() {
		InteractHighlight.clickedLocId = -1
		InteractHighlight.idleTicks = 0
	}

	@Test
	fun entriesAreReadAsTheirTargets() {
		assertTrue(HoveredTargets.decode(MiniMenuAction.OPNPC1, 0, 0, 1234L, "Talk-to", out))
		assertEquals(MenuTarget.NPC, out.kind)
		assertEquals(1234, out.id)
		// pushed below Walk (the level penalty) is still an npc
		assertTrue(HoveredTargets.decode(MiniMenuAction.OPNPC2 + 2000, 0, 0, 7L, "Attack", out))
		assertEquals(7, out.id)
		assertTrue(HoveredTargets.decode(MiniMenuAction.OPPLAYER1, 0, 0, 42L, "Follow", out))
		assertEquals(MenuTarget.PLAYER, out.kind)
		// a location's id is in the uid's high bits, its tile in the params; a non-interactive one sets the top bit
		val uid: Long = (1530L shl 32) or 0x40000000L or Long.MIN_VALUE
		assertTrue(HoveredTargets.decode(MiniMenuAction.OPLOC1, 51, 50, uid, "Open", out))
		assertEquals(MenuTarget.LOC, out.kind)
		assertEquals(1530, out.id)
		assertEquals(51, out.sceneX)
		assertEquals(50, out.sceneY)
		// Examine carries the id itself
		assertTrue(HoveredTargets.decode(MiniMenuAction.OPLOC6, 3, 4, 1530L, "Examine", out))
		assertEquals(1530, out.id)
		assertTrue(HoveredTargets.decode(MiniMenuAction.OPOBJ1, 10, 11, 995L, "Take", out))
		assertEquals(MenuTarget.GROUND_ITEM, out.kind)
		assertEquals(995, out.id)
	}

	@Test
	fun aWalkOrAnInterfaceIsNoTarget() {
		assertFalse(HoveredTargets.decode(MiniMenuAction.CANCEL, 0, 0, 0L, "Cancel", out))
		assertFalse(HoveredTargets.decode(MiniMenuAction.WALK, 0, 0, 0L, "Walk here", out))
	}

	@Test
	fun aClickedLocationIsLetGoOnceYouStandIdle() {
		InteractHighlight.clickedLocId = 1530
		InteractHighlight.idle(walking = true, animating = false)
		InteractHighlight.idle(walking = false, animating = true)
		assertEquals(1530, InteractHighlight.clickedLocId, "walking to it and working on it")
		InteractHighlight.idle(walking = false, animating = false)
		assertEquals(1530, InteractHighlight.clickedLocId, "one idle tick")
		InteractHighlight.idle(walking = false, animating = false)
		assertEquals(-1, InteractHighlight.clickedLocId, "two idle ticks")
	}

	@Test
	fun anNpcThatOffersAttackTakesTheAttackColours() {
		fun npc(vararg actions: String?): Npc {
			val type: NpcDefinition = Proxy.newProxyInstance(NpcDefinition::class.java.classLoader, arrayOf(NpcDefinition::class.java)) { _, m, a ->
				if (m.name == "action") actions.getOrNull(a[0] as Int) else null
			} as NpcDefinition
			return Proxy.newProxyInstance(Npc::class.java.classLoader, arrayOf(Npc::class.java)) { _, m, _ -> if (m.name == "type") type else null } as Npc
		}
		assertTrue(InteractHighlight.attackable(npc("Talk-to", "Attack")))
		assertFalse(InteractHighlight.attackable(npc("Talk-to", null, "Trade")))
	}
}
