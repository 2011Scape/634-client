package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ReportButton
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: Report Button's formats (ReportButton). */
class ReportButtonTest {

	@AfterTest
	fun tidy() {
		ReportButton.twentyFourHour = false
	}

	@Test
	fun twelveHourTimeIsRuneLitesMediumStyle() {
		assertEquals("3:04:05 PM", ReportButton.format(15, 4, 5))
		assertEquals("12:00:00 AM", ReportButton.format(0, 0, 0))
		assertEquals("12:30:00 PM", ReportButton.format(12, 30, 0))
	}

	@Test
	fun twentyFourHourTimeIsPadded() {
		ReportButton.twentyFourHour = true
		assertEquals("09:04:05", ReportButton.format(9, 4, 5))
	}

	@Test
	fun theLoginTimerAndIdleTimeCount() {
		assertEquals("01:02:03", ReportButton.duration(3723L))
		assertEquals("02:05", ReportButton.idle(125L))
	}

	@Test
	fun theDateIsMonthDayYearInUtcHere() {
		// 2026-10-22 12:00 UTC: the local zone may move the day, never the format
		val date: String = ReportButton.date(1792670400000L)
		assertEquals(true, Regex("[A-Z][a-z]{2}\\. [0-9]{2}, 2026").matches(date))
	}
}
