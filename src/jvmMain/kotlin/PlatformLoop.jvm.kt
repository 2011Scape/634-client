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

// GLSL 1.30 needs a GL 3.0 context; macOS gives a legacy 2.1 one (GlContext), which has GLSL 1.20.
// Read when GlSmaaFilter compiles its shaders, with the toolkit's context current.
actual val smaaGlslVersion: String
    get() {
        val version: String = jaggl.OpenGL.glGetString(0x1F02) ?: return "#version 120\n"
        val major: Int = version.takeWhile { it.isDigit() }.toIntOrNull() ?: 0
        return if (major >= 3) "#version 130\n" else "#version 120\n"
    }

actual val maxTicksPerFrame: Int = 10

actual val rememberTextures: Boolean = false

actual val fastRaster: Boolean = false

actual fun spanToInt(value: Float): Int = value.toInt()

actual val emulatedLong: Boolean = false

actual fun turnElapsedMs(): Double = -1.0

private val secureRandom: java.security.SecureRandom = java.security.SecureRandom()

actual fun secureSeed(): Int = secureRandom.nextInt(99999999)

actual val homeCacheOnly: Boolean = true

actual fun createHdToolkit(multiSamples: Int, canvas: awt.Canvas?, textureSource: com.jagex.game.runetek5.config.material.MaterialProvider?): com.jagex.game.runetek5.graphics.Toolkit =
    com.jagex.game.runetek5.graphics.hd.HdToolkit.create(multiSamples, canvas, textureSource)
actual val hdAvailable: Boolean = true

actual fun memoryReport(): String {
    // after a collection, so the heap and the direct buffers it frees show what is really held
    System.gc()
    val runtime = Runtime.getRuntime()
    val heap = (runtime.totalMemory() - runtime.freeMemory()) shr 20
    val direct = java.lang.management.ManagementFactory.getPlatformMXBeans(java.lang.management.BufferPoolMXBean::class.java)
        .firstOrNull { it.name == "direct" }?.memoryUsed?.shr(20) ?: -1L
    val rss = try {
        java.io.File("/proc/self/status").readLines().firstOrNull { it.startsWith("VmRSS:") }
            ?.substringAfter(':')?.trim()?.substringBefore(' ')?.toLong()?.shr(10) ?: -1L
    } catch (ex: Exception) {
        -1L
    }
    val gpu = com.jagex.game.runetek5.graphics.hd.HdToolkit.freeVideoMemoryMb()
    return "heap=${heap}MB direct=${direct}MB rss=${rss}MB" + (if (gpu >= 0) " gpuFree=${gpu}MB" else "")
}
