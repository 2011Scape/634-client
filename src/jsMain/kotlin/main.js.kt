import com.jagex.game.runetek5.client.GameShell
import io.CachePersistence
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
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
    fixRenderDistance()
    // The browser draws at the display's refresh (scheduleTurn), so frames are paced at 60 a second
    // unless the page asks for another rate; the smoke test too.
    if (lang.getProperty("client.fps") == null) {
        lang.setProperty("client.fps", "60")
    }
    // The smoke test starts the client as ./gradlew runClient -Pclient.test=smoke does, with every
    // extra off unless the address turns it on, so that its frames compare with the desktop's.
    if ("smoke" != lang.getProperty("client.test")) {
        com.jagex.game.runetek5.client.features.LauncherDefaults.apply(window.screen.height)
    }
    // Leaving the page logs out: a page kept in the back/forward cache would otherwise hold the
    // player's connection open. Coming back to such a page starts the client again.
    window.addEventListener("pagehide", { net.closeAllSockets() })
    holdPageAwake()
    window.addEventListener("pageshow", { event -> if (event.asDynamic().persisted == true) window.location.reload() })
    GlobalScope.launch {
        CachePersistence.hydrate()
        fetchPageResources()
        if ("smoke" == lang.getProperty("client.test")) {
            // the reference frames are served beside the page (tools/serve.mjs, from ~/634-smoke-ref/browser)
            if (lang.getProperty("client.test.reference") == null) {
                lang.setProperty("client.test.reference", "/smoke-ref")
            }
            com.jagex.game.runetek5.client.features.preloadSmokeReferences(lang.getProperty("client.test.reference")!!)
        }
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

/*
 * lang.halt in a browser, where the client finished as a JVM would by exiting (SmokeTest does, with
 * 0 for a pass and 1 for a failure): every loop stops, the relay connections close (the server logs
 * the player out), and the last SMOKE PASS/FAIL line goes in the page, in #smoke-result (made if the
 * page has none) and the title, for whoever is watching.
 */
fun haltPage(status: Int) {
    if (halted) return
    halted = true
    net.closeAllSockets()
    val lines: Array<String> = window.asDynamic().__log.unsafeCast<Array<String>>()
    var result: String = "halt(" + status + ")"
    for (line in lines) {
        for (kind in arrayOf("SMOKE PASS", "SMOKE FAIL", "SMOKE BENCH")) {
            val at: Int = line.indexOf(kind)
            if (at >= 0) result = line.substring(at)
        }
    }
    var element: org.w3c.dom.Element? = document.getElementById("smoke-result")
    if (element == null) {
        element = document.createElement("pre")
        element.id = "smoke-result"
        document.body!!.insertBefore(element, document.body!!.firstChild)
    }
    element.textContent = result
    element.setAttribute("data-status", status.toString())
    document.title = if (status != 0) "SMOKE FAIL" else if (result.startsWith("SMOKE BENCH")) "SMOKE BENCH" else "SMOKE PASS"
}

/*
 * The class path resources the switches need, fetched from beside the bundle into lang.readResource:
 * the SMAA shader and lookup textures when client.smaa is on. A file that cannot be fetched stays
 * missing, and SMAA turns itself off as it does on the JVM.
 */
private suspend fun fetchPageResources() {
    if (!lang.getBoolean("client.smaa")) return
    for (name in arrayOf("SMAA.hlsl", "AreaTex.bin", "SearchTex.bin")) {
        try {
            val response: dynamic = window.fetch("smaa/" + name).await()
            if (response.ok != true) continue
            val buffer: org.khronos.webgl.ArrayBuffer = (response.arrayBuffer() as kotlin.js.Promise<org.khronos.webgl.ArrayBuffer>).await()
            lang.pageResources["/smaa/" + name] = org.khronos.webgl.Int8Array(buffer).unsafeCast<ByteArray>()
        } catch (e: Throwable) {
        }
    }
}

/*
 * The browser always draws at the normal render distance: a page has far less memory and time per
 * frame than the desktop, and a longer view (the launcher's default is twice as far) drops the
 * software toolkit well under 10 frames a second. A URL switch or window.CONFIG asking for another
 * distance is overridden, and the console says so.
 */
private fun fixRenderDistance() {
    val asked: String? = lang.getProperty("client.renderDistance")
    if (asked != null && asked.toDoubleOrNull() != 1.0) {
        console.log("client.renderDistance: " + asked + " is not available in the browser; drawing at 1")
    }
    lang.setProperty("client.renderDistance", "1")
}

