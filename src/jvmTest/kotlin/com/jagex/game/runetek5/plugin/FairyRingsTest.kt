package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.FairyRings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: Fairy Rings' codes, the travel log's rows and its search (FairyRings). */
class FairyRingsTest {

	@Test
	fun theLogsRowsAreTheCodesInAlphabeticalOrder() {
		assertEquals("AIP", FairyRings.rowCode(14))
		assertEquals("AIQ", FairyRings.rowCode(15))
		assertEquals("AKQ", FairyRings.rowCode(23))
		assertEquals("DLS", FairyRings.rowCode(77))
		assertNull(FairyRings.rowCode(13))
		assertNull(FairyRings.rowCode(78))
		for (row in 14..77) {
			assertEquals(row, FairyRings.rowOf(FairyRings.rowCode(row)!!))
		}
	}

	@Test
	fun searchMatchesLettersOrDestination() {
		assertEquals(listOf("AKQ", "AKS", "DKS"), FairyRings.matching("hunter"))
		assertEquals(listOf("CKS"), FairyRings.matching("c k s"))
		assertEquals(listOf("ALQ", "BKR", "CKS"), FairyRings.matching("CANIFIS"))
		assertEquals(FairyRings.DESTINATIONS.size, FairyRings.matching("  ").size)
		assertTrue(FairyRings.matching("nowhere at all").isEmpty())
	}

	@Test
	fun everyCodeIsThreeLettersOfItsDials() {
		assertEquals(43, FairyRings.DESTINATIONS.size)
		for (code in FairyRings.DESTINATIONS.keys) {
			assertTrue(code.length == 3 && code[0] in "ABCD" && code[1] in "IJKL" && code[2] in "PQRS", code)
		}
	}
}
