package util

/*
 * Not part of void-client's shims: java.util.Calendar, GregorianCalendar, Date and TimeZone, as
 * far as the client uses them (milestone 3). Statics are top-level functions.
 */
expect abstract class Calendar {
    fun setTime(date: Date?)
    fun getTime(): Date
    open fun get(field: Int): Int
    open fun set(field: Int, value: Int)
    fun set(year: Int, month: Int, date: Int)
    fun clear()
}

expect open class GregorianCalendar : Calendar {
    constructor()
}

expect open class Date {
    constructor(date: Long)
    open fun getTime(): Long
}

expect abstract class TimeZone

/* Calendar.getInstance(). */
expect fun calendarInstance(): Calendar

/* Calendar.getInstance(zone). */
expect fun calendarInstance(zone: TimeZone?): Calendar

/* TimeZone.getTimeZone(id). */
expect fun timeZone(id: String?): TimeZone
