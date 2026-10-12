package lang

/*
 * java.lang.Math.round, which kotlin.math has no exact equivalent of: roundToInt and roundToLong
 * throw on NaN, where Java returns 0. (The other Math functions the client uses are kotlin.math's,
 * which compile to java.lang.Math on the JVM.)
 */
expect fun round(a: Double): Long

expect fun round(a: Float): Int
