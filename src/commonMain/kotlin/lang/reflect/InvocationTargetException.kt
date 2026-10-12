package lang.reflect

/* Not part of void-client's shims: java.lang.reflect.InvocationTargetException (milestone 3). */
expect open class InvocationTargetException : Exception {
    fun getTargetException(): Throwable?
}
