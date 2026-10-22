package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField

/**
 * Not part of the original client: the skills' pictures and colours the built-in plugins share (XP
 * Tracker, XP Drops, Boosts Information), by skill number.
 */
object SkillSprites {

	/** The skills' icons, the Stats tab's sprites, by skill. */
	@JvmField
	val ICONS: IntArray = intArrayOf(197, 199, 198, 203, 200, 201, 202, 212, 214, 208, 211, 213, 207, 210, 209, 205, 204, 206, 216, 217, 215, 220, 221, 222, 3032)

	/** RuneLite's SkillColor, by skill; Summoning and Dungeoneering, which it lacks, in their own colours. */
	@JvmField
	val COLOURS: IntArray = intArrayOf(
		0x9b2007, 0x6277be, 0x04955a, 0x837e7e, 0x6d9017, 0x9f9323, 0x3250c1, 0x702386, 0x348c25, 0x038d7d, 0x6a84a4, 0xbd7819,
		0x976e4d, 0x6c6b52, 0x5d8fa7, 0x078509, 0x3a3c89, 0x6c3457, 0x646464, 0x65983f, 0xaa8d1a, 0x5c5941, 0x82745f, 0x3c9999, 0x8a4b2a,
	)
}
