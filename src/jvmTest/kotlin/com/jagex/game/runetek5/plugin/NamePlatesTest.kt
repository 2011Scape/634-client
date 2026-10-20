package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.NamePlates
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: a name plate's colour by what the player is to you (NamePlates). */
class NamePlatesTest {

	@Test
	fun attackableBeatsFriendBeatsChat() {
		assertEquals(NamePlates.ATTACKABLE, NamePlates.colourFor(true, true, true))
		assertEquals(NamePlates.FRIEND, NamePlates.colourFor(false, true, true))
		assertEquals(NamePlates.CHAT, NamePlates.colourFor(false, false, true))
		assertEquals(NamePlates.PLAIN, NamePlates.colourFor(false, false, false))
	}
}
