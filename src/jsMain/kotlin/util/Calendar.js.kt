package util

import kotlin.js.Date as JsDate

/*
 * A Gregorian calendar over the browser's Date, for the fields the client reads and sets: YEAR (1),
 * MONTH (2), DAY_OF_MONTH (5), DAY_OF_YEAR (6), DAY_OF_WEEK (7, Sunday = 1), AM_PM (9), HOUR (10),
 * HOUR_OF_DAY (11), MINUTE (12), SECOND (13), MILLISECOND (14). A zone is the local one or GMT.
 */
actual abstract class TimeZone internal constructor(internal val utc: Boolean)

internal class SimpleTimeZone(utc: Boolean) : TimeZone(utc)

actual open class Date {
    private val millis: Long

    actual constructor(date: Long) {
        millis = date
    }

    actual open fun getTime(): Long = millis
}

actual abstract class Calendar internal constructor(private val zone: TimeZone) {
    // year, month, day of month, hour of day, minute, second, millisecond
    private val f: IntArray = IntArray(7)

    init {
        load(lang.currentTimeMillis())
    }

    private fun load(millis: Long) {
        val d = JsDate(millis.toDouble())
        if (zone.utc) {
            f[0] = d.getUTCFullYear(); f[1] = d.getUTCMonth(); f[2] = d.getUTCDate()
            f[3] = d.getUTCHours(); f[4] = d.getUTCMinutes(); f[5] = d.getUTCSeconds(); f[6] = d.getUTCMilliseconds()
        } else {
            f[0] = d.getFullYear(); f[1] = d.getMonth(); f[2] = d.getDate()
            f[3] = d.getHours(); f[4] = d.getMinutes(); f[5] = d.getSeconds(); f[6] = d.getMilliseconds()
        }
    }

    private fun millis(): Long {
        val y = f[0]; val m = f[1]; val day = f[2]; val h = f[3]; val mi = f[4]; val s = f[5]; val ms = f[6]
        val t: Double = if (zone.utc) {
            js("Date.UTC(y, m, day, h, mi, s, ms)") as Double
        } else {
            js("new Date(y, m, day, h, mi, s, ms).getTime()") as Double
        }
        return t.toLong()
    }

    actual fun setTime(date: Date?) {
        load(date!!.getTime())
    }

    actual fun getTime(): Date = Date(millis())

    actual open fun get(field: Int): Int {
        return when (field) {
            1 -> f[0]
            2 -> f[1]
            5 -> f[2]
            6 -> {
                val start = Date(0L).let { calendarOf(zone, f[0], 0, 1).millis() }
                ((millis() - start) / 86400000L).toInt() + 1
            }
            7 -> {
                val d = JsDate(millis().toDouble())
                (if (zone.utc) d.getUTCDay() else d.getDay()) + 1
            }
            9 -> if (f[3] < 12) 0 else 1
            10 -> f[3] % 12
            11 -> f[3]
            12 -> f[4]
            13 -> f[5]
            14 -> f[6]
            else -> throw IllegalArgumentException("Calendar field " + field)
        }
    }

    actual open fun set(field: Int, value: Int) {
        when (field) {
            1 -> f[0] = value
            2 -> f[1] = value
            5 -> f[2] = value
            11 -> f[3] = value
            12 -> f[4] = value
            13 -> f[5] = value
            14 -> f[6] = value
            else -> throw IllegalArgumentException("Calendar field " + field)
        }
        load(millis())
    }

    actual fun set(year: Int, month: Int, date: Int) {
        f[0] = year
        f[1] = month
        f[2] = date
        load(millis())
    }

    /* Every field unset: the epoch's fields. */
    actual fun clear() {
        f[0] = 1970; f[1] = 0; f[2] = 1; f[3] = 0; f[4] = 0; f[5] = 0; f[6] = 0
    }

    internal companion object {
        fun calendarOf(zone: TimeZone, year: Int, month: Int, day: Int): Calendar {
            val c = GregorianCalendar(zone)
            c.clear()
            c.set(year, month, day)
            return c
        }
    }
}

actual open class GregorianCalendar : Calendar {
    actual constructor() : super(SimpleTimeZone(false))

    internal constructor(zone: TimeZone) : super(zone)
}

actual fun calendarInstance(): Calendar = GregorianCalendar()

actual fun calendarInstance(zone: TimeZone?): Calendar = GregorianCalendar(zone ?: SimpleTimeZone(false))

/* "GMT" and "UTC" are GMT; any other id is the local zone. */
actual fun timeZone(id: String?): TimeZone = SimpleTimeZone(id == "GMT" || id == "UTC")
