package util

actual typealias Calendar = java.util.Calendar

actual typealias GregorianCalendar = java.util.GregorianCalendar

actual typealias Date = java.util.Date

actual typealias TimeZone = java.util.TimeZone

actual fun calendarInstance(): Calendar = java.util.Calendar.getInstance()

actual fun calendarInstance(zone: TimeZone?): Calendar = java.util.Calendar.getInstance(zone)

actual fun timeZone(id: String?): TimeZone = java.util.TimeZone.getTimeZone(id)
