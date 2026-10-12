package lang

actual fun round(a: Double): Long = java.lang.Math.round(a)

actual fun round(a: Float): Int = java.lang.Math.round(a)
