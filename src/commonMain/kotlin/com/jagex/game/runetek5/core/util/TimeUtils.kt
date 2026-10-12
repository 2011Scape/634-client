package com.jagex.game.runetek5.core.util

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import util.Calendar
import util.Date
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.InterruptedException
import lang.Thread

object TimeUtils {

	const val MILLISECONDS_PER_MINUTE: Long = 60000L

	const val MILLISECONDS_PER_DAY: Long = 86400000L

	const val MILLISECONDS_PER_THREE_YEARS: Long = 94608000000L

	const val RUNEDAYS_SINCE_UNIX_EPOCH: Int = 11745

	@OriginalMember(owner = "client!dia", name = "b", descriptor = "[Ljava/lang/String;")
	@JvmField
	val MONTH_ABBREVIATIONS: Array<String?> = arrayOf<String?>("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(BJ)V")
	@JvmStatic
	fun sleep(@OriginalArg(1) millis: Long) {
		if (millis <= 0L) {
			return
		}
		if (millis % 10L == 0L) {
			TimeUtils.sleepQuietly(millis - 1L)
			TimeUtils.sleepQuietly(1L)
		} else {
			TimeUtils.sleepQuietly(millis)
		}
	}

	@OriginalMember(owner = "client!mba", name = "a", descriptor = "(JI)Ljava/lang/String;")
	@JvmStatic
	fun formatCookieDate(@OriginalArg(0) timeMillis: Long): String {
		TimeUtils.ACTIVE_TIMEZONE_CALENDER.setTime(Date(timeMillis))
		@Pc(13) val dayOfWeek: Int = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(7)
		@Pc(17) val dayOfMonth: Int = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(5)
		@Pc(26) val month: Int = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(2)
		@Pc(30) val year: Int = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(1)
		@Pc(34) val hour: Int = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(11)
		@Pc(38) val minute: Int = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(12)
		@Pc(42) val second: Int = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(13)
		return TimeUtils.DAY_ABBREVIATIONS[dayOfWeek - 1] + ", " + dayOfMonth / 10 + dayOfMonth % 10 + "-" + TimeUtils.MONTH_ABBREVIATIONS[month] + "-" + year + " " + hour / 10 + hour % 10 + ":" + minute / 10 + minute % 10 + ":" + second / 10 + second % 10 + " GMT"
	}

	@OriginalMember(owner = "client!em", name = "l", descriptor = "Ljava/util/Calendar;")
	@JvmField
	val ACTIVE_TIMEZONE_CALENDER: Calendar = util.calendarInstance(util.timeZone("GMT"))

	@OriginalMember(owner = "client!lt", name = "c", descriptor = "[Ljava/lang/String;")
	@JvmField
	val DAY_ABBREVIATIONS: Array<String?> = arrayOf<String?>("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

	@OriginalMember(owner = "client!de", name = "a", descriptor = "(IJ)V")
	@JvmStatic
	fun sleepQuietly(@OriginalArg(1) millis: Long) {
		try {
			lang.sleep(millis)
		} catch (@Pc(15) ex: InterruptedException) {
		}
	}
}
