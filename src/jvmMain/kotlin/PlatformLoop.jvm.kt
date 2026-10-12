/* Not part of the original client: the JVM side of PlatformLoop, the original blocking loops. */

/* The original try/while/catch/finally exactly: the JVM gives the loop a real thread. */
actual fun runLoop(
    prelude: () -> Boolean,
    step: () -> Boolean,
    onError: (Throwable) -> Unit,
    onFinally: () -> Unit,
) {
    try {
        if (prelude()) {
            while (step()) {
                // the body is all in step()
            }
        }
    } catch (throwable: Throwable) {
        onError(throwable)
    } finally {
        onFinally()
    }
}

actual val executeWorkerTasksInline: Boolean = false
actual val canBlock: Boolean = true

actual val js5ReadsPerTick: Int = 100

actual val clientArraysAvailable: Boolean = true

actual val textAntialiased: Boolean = false

actual val classFieldsAvailable: Boolean = true

actual val smaaGlslVersion: String = "#version 130\n"

actual val maxTicksPerFrame: Int = 10

actual val rememberTextures: Boolean = false

actual val fastRaster: Boolean = false

actual fun spanToInt(value: Float): Int = value.toInt()

actual val emulatedLong: Boolean = false

actual fun turnElapsedMs(): Double = -1.0
