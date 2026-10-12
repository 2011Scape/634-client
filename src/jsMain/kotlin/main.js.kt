import com.jagex.game.runetek5.client.GameShell
import io.CachePersistence
import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/*
 * Not part of the original client: the browser entry point (milestone 4). It plays the part of
 * ./gradlew runClient: the cache files come back from IndexedDB, then client.main starts the client
 * with the same arguments, in local mode against a Void server (reached through the WebSocket relay
 * that net.Socket connects to), and the client's window goes on the page.
 */
fun main() {
    keepConsoleLog()
    GlobalScope.launch {
        CachePersistence.hydrate()
        client.main(arrayOf("3594", "3594", "0", "live", "english", "game0"))
        val frame = GameShell.engineFrame
        if (frame != null) {
            (document.getElementById("client") ?: document.body!!).appendChild(frame.element)
        }
        CachePersistence.startAutoFlush()
    }
}

/* The last 500 lines the client printed, in window.__log, for a page whose console was not open. */
private fun keepConsoleLog() {
    js("""
        window.__log = [];
        ['log', 'warn', 'error'].forEach(function (level) {
            var original = console[level];
            console[level] = function () {
                var line = Array.prototype.map.call(arguments, String).join(' ');
                window.__log.push(level + ': ' + line);
                if (window.__log.length > 500) window.__log.shift();
                original.apply(console, arguments);
            };
        });
        window.addEventListener('error', function (e) { window.__log.push('uncaught: ' + e.message); });
    """)
}
