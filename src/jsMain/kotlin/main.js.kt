import com.jagex.game.runetek5.client.GameShell
import io.CachePersistence
import kotlinx.browser.document
import kotlinx.browser.window
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
    readClientSwitches()
    readPageConfig()
    com.jagex.game.runetek5.client.features.LauncherDefaults.apply(window.screen.height)
    // Leaving the page logs out: a page kept in the back/forward cache would otherwise hold the
    // player's connection open. Coming back to such a page starts the client again.
    window.addEventListener("pagehide", { net.closeAllSockets() })
    window.addEventListener("pageshow", { event -> if (event.asDynamic().persisted == true) window.location.reload() })
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

/*
 * The client.* switches (docs/FEATURES.md) from the page's query string, as runClient takes them from
 * -P: ?client.msaa=4&client.smaa=true. A switch without a value is true (?client.smaa). They must be
 * set before client.main, because ClientFeatures reads them once.
 */
private fun readClientSwitches() {
    val query: String = window.location.search.removePrefix("?")
    if (query.isEmpty()) return
    for (pair in query.split('&')) {
        val equals: Int = pair.indexOf('=')
        val key: String = js("decodeURIComponent")(if (equals < 0) pair else pair.substring(0, equals)) as String
        val value: String = if (equals < 0) "true" else js("decodeURIComponent")(pair.substring(equals + 1)) as String
        if (key.startsWith("client.")) {
            lang.setProperty(key, value)
        }
    }
    // SMAA's shaders are desktop GLSL 1.30, which the WebGL 2 emulator does not translate
    // (docs/FEATURES.md); without its resources GlSmaaFilter leaves it off.
    if (lang.getBoolean("client.smaa")) {
        console.log("client.smaa: not available in the browser; SMAA is off")
    }
}

/*
 * The client.* options a hosted page gives in its configuration, before the bundle loads:
 * window.CONFIG = { url: "wss://example.org/relay", "client.msaa": 4, "client.zoom": false }.
 * A URL switch wins over the configuration, and both over the launcher's defaults.
 */
private fun readPageConfig() {
    val config: dynamic = window.asDynamic().CONFIG
    if (config == null || config == undefined) return
    val keys: Array<String> = js("Object.keys")(config) as Array<String>
    for (key in keys) {
        if (key.startsWith("client.") && lang.getProperty(key) == null) {
            lang.setProperty(key, config[key].toString())
        }
    }
}

