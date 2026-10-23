package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.AttackStyles
import com.jagex.game.runetek5.client.features.AttackStyles.AttackStyle
import com.jagex.game.runetek5.client.features.WeaponStyles
import com.jagex.game.runetek5.plugin.api.Skill
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: Attack Styles' styles from Void's weapon types, and its warnings (AttackStyles). */
class AttackStylesTest {

	@BeforeTest
	fun fresh() {
		AttackStyles.reset()
		AttackStyles.warnForAttack = false
		AttackStyles.warnForDefensive = false
		AttackStyles.warnForStrength = false
		AttackStyles.warnForRanged = false
		AttackStyles.warnForMagic = false
	}

	@AfterTest
	fun tidy() {
		fresh()
	}

	@Test
	fun aSwordsThirdStyleIsControlled() {
		assertEquals(AttackStyle.CONTROLLED, AttackStyles.styleOf(6, 2, 0, 0))
		assertEquals(AttackStyle.DEFENSIVE, AttackStyles.styleOf(6, 3, 0, 0))
	}

	@Test
	fun unarmedHasThreeStylesAndBlockIsTheThird() {
		assertEquals(3, WeaponStyles.styles(0).size)
		assertEquals(AttackStyle.DEFENSIVE, AttackStyles.styleOf(0, 2, 0, 0))
		assertEquals(AttackStyle.OTHER, AttackStyles.styleOf(0, 3, 0, 0))
	}

	@Test
	fun aBowsLongRangeTrainsRangedAndDefence() {
		assertEquals(AttackStyle.RANGING, AttackStyles.styleOf(16, 1, 0, 0))
		assertEquals(AttackStyle.LONGRANGE, AttackStyles.styleOf(16, 2, 0, 0))
	}

	@Test
	fun autocastingIsCastingWhateverTheStyle() {
		assertEquals(AttackStyle.CASTING, AttackStyles.styleOf(1, 0, 143, 0))
		assertEquals(AttackStyle.DEFENSIVE_CASTING, AttackStyles.styleOf(1, 0, 143, 1))
	}

	@Test
	fun anUnknownTypeIsOther() {
		assertEquals(AttackStyle.OTHER, AttackStyles.styleOf(99, 0, 0, 0))
	}

	@Test
	fun controlledIsWarnedForAnyOfItsSkills() {
		assertFalse(AttackStyles.isWarned(AttackStyle.CONTROLLED))
		AttackStyles.warnForDefensive = true
		assertTrue(AttackStyles.isWarned(AttackStyle.CONTROLLED))
		assertTrue(AttackStyles.isWarned(AttackStyle.LONGRANGE))
		assertFalse(AttackStyles.isWarned(AttackStyle.RANGING))
		assertTrue(AttackStyles.isWarned(Skill.DEFENCE))
	}

	@Test
	fun everyWeaponTypeOfVoidHasStyles() {
		for (type in 0..27) {
			assertTrue(WeaponStyles.styles(type).isNotEmpty(), "type $type")
		}
	}
}
