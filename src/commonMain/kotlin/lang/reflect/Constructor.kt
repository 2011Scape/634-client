package lang.reflect

/* Not part of void-client's shims: java.lang.reflect.Constructor (milestone 3). */
expect class Constructor<T> {
    fun newInstance(vararg initargs: Any?): T
    fun setAccessible(flag: Boolean)
}
