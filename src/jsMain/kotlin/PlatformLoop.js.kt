import kotlinx.browser.window
import lang.pendingSleepMillis

/**
 * A setTimeout chain rather than a `while` loop, so the browser gets the thread back between
 * iterations. The delay comes from whatever sleep the step asked for via lang.sleep (which records
 * the request instead of blocking), preserving the client's own ~20ms pacing.
 */
/*
 * Set by lang.halt (main.js.kt's haltPage): the client has finished, as a JVM would by exiting. Every
 * loop ends at its next turn, without its error or finally handlers.
 */
internal var halted: Boolean = false

actual fun runLoop(
    prelude: () -> Boolean,
    step: () -> Boolean,
    onError: (Throwable) -> Unit,
    onFinally: () -> Unit,
) {
    try {
        if (!prelude()) {
            onFinally()
            return
        }
    } catch (throwable: Throwable) {
        onError(throwable)
        onFinally()
        return
    }

    fun tick() {
        if (halted) return
        turnStart = window.performance.now()
        pendingSleepMillis = 0L
        val more = try {
            step()
        } catch (throwable: Throwable) {
            if (halted) return
            onError(throwable)
            onFinally()
            return
        }
        if (!more) {
            onFinally()
            return
        }
        scheduleTurn(pendingSleepMillis.coerceIn(1L, 50L).toInt()) { tick() }
    }

    scheduleTurn(0) { tick() }
}

actual val executeWorkerTasksInline: Boolean = true
actual val canBlock: Boolean = false

actual val js5ReadsPerTick: Int = Int.MAX_VALUE

actual val clientArraysAvailable: Boolean = false

actual val textAntialiased: Boolean = true

actual val classFieldsAvailable: Boolean = false

actual val smaaGlslVersion: String = ""

actual val maxTicksPerFrame: Int = 4

actual val rememberTextures: Boolean = true

/* When the current turn of runLoop started, by performance.now(). */
private var turnStart: Double = 0.0

actual fun turnElapsedMs(): Double = window.performance.now() - turnStart
