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
