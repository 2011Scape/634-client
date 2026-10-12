package lang

/* Math.round: the closest integer, ties up; 0 for NaN; clamped to the type's range. */
actual fun round(a: Double): Long {
    if (a.isNaN()) return 0L
    val f = kotlin.math.floor(a)
    return (if (a - f >= 0.5) f + 1.0 else f).toLong()
}

actual fun round(a: Float): Int {
    if (a.isNaN()) return 0
    val d = a.toDouble()
    val f = kotlin.math.floor(d)
    return (if (d - f >= 0.5) f + 1.0 else f).toInt()
}
