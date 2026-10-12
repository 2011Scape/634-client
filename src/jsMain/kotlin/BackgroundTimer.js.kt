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

/* Runs block after delay milliseconds, timed by the worker while the page is hidden. */
internal fun scheduleTurn(delay: Int, block: () -> Unit) {
    if (worker == null || !document.asDynamic().hidden.unsafeCast<Boolean>() || !listening) {
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
