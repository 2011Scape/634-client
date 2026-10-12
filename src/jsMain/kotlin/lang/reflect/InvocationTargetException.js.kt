package lang.reflect

actual open class InvocationTargetException(private val target: Throwable?) : Exception(target) {
    actual fun getTargetException(): Throwable? = target
}
