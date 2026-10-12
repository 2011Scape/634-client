import kotlinx.browser.document
import kotlinx.browser.window

/*
 * Not part of the original client: the timer runLoop's turns wait on (PlatformLoop.js.kt).
 *
 * A browser throttles the timers of a page that is not shown: Chrome runs them at most once a second,
 * and after a few minutes once a minute. The client's loops then fall behind the server, and when the
 * tab is shown again the game runs fast until it has caught up. Timers in a Web Worker are not
 * throttled that way, so while the page is hidden each wait is timed by a worker, which posts back
 * when it is up. While the page is shown the window's own setTimeout is used, as before.
 */
private val worker: dynamic = try {
    val source = "onmessage = function (e) { setTimeout(function () { postMessage(e.data.id); }, e.data.delay); };"
    val blob = js("new Blob([source], {type: 'text/javascript'})")
    js("new Worker(URL.createObjectURL(blob))")
} catch (e: Throwable) {
    null
}

/* A wait this long or shorter is taken to the next animation frame (see scheduleTurn). */
private const val FRAME_WAIT_MS: Int = 17

/* How long a turn waits for an animation frame before a timer runs it instead. */
private const val FALLBACK_MS: Int = 34

private val pending: MutableMap<Int, () -> Unit> = mutableMapOf()
private var nextId: Int = 1

private val listening: Boolean = run {
    if (worker != null) {
        worker.onmessage = { event: dynamic ->
            val id = event.data as Int
            pending.remove(id)?.invoke()
        }
    }
    true
}

/*
 * Runs block after delay milliseconds. While the page is shown, a wait of one frame or less runs on
 * the next animation frame instead, in step with the display's refresh and free of the ~4 ms minimum
 * browsers put on nested timers (the frame loop draws once a turn, so this paces it at the refresh
 * rate). While the page is hidden the wait is timed by the worker.
 */
internal fun scheduleTurn(delay: Int, block: () -> Unit) {
    val hidden: Boolean = document.asDynamic().hidden.unsafeCast<Boolean>()
    if (!hidden && delay <= FRAME_WAIT_MS) {
        // A page can be "visible" while the browser sends it no animation frames (its window covered
        // by others): a timer two frames away runs the turn then. Whichever comes first runs it, once.
        var ran: Boolean = false
        val turn: () -> Unit = {
            if (!ran) {
                ran = true
                block()
            }
        }
        window.requestAnimationFrame { turn() }
        window.setTimeout(turn, FALLBACK_MS)
        return
    }
    if (worker == null || !hidden || !listening) {
        window.setTimeout(block, delay)
        return
    }
    val id = nextId++
    pending[id] = block
    val message: dynamic = js("({})")
    message.id = id
    message.delay = delay
    worker.postMessage(message)
}

/*
 * Asks the browser not to freeze or discard the page while it is hidden (Chrome leaves a page that
 * holds a Web Lock running). The lock is never released: the page holds it until it closes.
 */
internal fun holdPageAwake() {
    val locks = window.navigator.asDynamic().locks ?: return
    locks.request("runetek5-634-client") { js("new Promise(function () {})") }
}
