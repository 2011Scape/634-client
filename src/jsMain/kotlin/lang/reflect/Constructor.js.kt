package lang.reflect

/* A constructor from the browser's class registry (lang/ClassRegistry.js.kt). */
actual class Constructor<T> internal constructor(private val factory: (Array<out Any?>) -> T) {
    actual fun newInstance(vararg initargs: Any?): T = factory(initargs)

    actual fun setAccessible(flag: Boolean) {
    }
}
