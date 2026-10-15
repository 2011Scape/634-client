// The warnings of the original (deobfuscated) code, left as it was: the build treats warnings as errors.
@file:Suppress("DEPRECATION")

package lang.reflect

actual typealias Field = java.lang.reflect.Field

actual var Field.isAccessible: Boolean
    get() = (this as java.lang.reflect.Field).isAccessible
    set(value) { (this as java.lang.reflect.Field).isAccessible = value }