package lang

/*
 * Not part of the original client: @Synchronized for common code (milestone 3). Kotlin's own is
 * JVM-only there; this optional expectation is kotlin.jvm.Synchronized on the JVM, so the methods
 * stay ACC_SYNCHRONIZED as before, and a platform without monitors can leave it out.
 */
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.SOURCE)
expect annotation class Synchronized()
