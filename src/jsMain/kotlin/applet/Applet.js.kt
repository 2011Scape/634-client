package applet

import awt.Panel
import net.URL

/*
 * Not part of the original client: java.applet.Applet for the browser (milestone 4). The page plays
 * the applet viewer: it gives the parameters (Applet.parameters, set by main) and the code base,
 * and the applet's panel is the page element the game's canvas goes in.
 */
actual open class Applet actual constructor() : Panel() {
    actual open fun init() {
    }

    actual open fun start() {
    }

    actual open fun stop() {
    }

    actual open fun destroy() {
    }

    actual open fun getParameter(name: String?): String? = parameters[name]

    actual open fun getCodeBase(): URL? = URL(codeBase)

    actual open fun getDocumentBase(): URL? = getCodeBase()

    actual open fun getAppletContext(): AppletContext? = PageAppletContext

    companion object {
        /* The applet's parameters, as an applet tag's param elements would give them. */
        val parameters: MutableMap<String?, String> = mutableMapOf()

        /* The code base: the server's web address, from which the client derives its host. */
        var codeBase: String = "http://127.0.0.1/"
    }
}

/*
 * A page does not navigate away for the client: GameShell shows its error screens through
 * showDocument ("error_game_crash", "js5connect"), and leaving the page would hide them.
 */
private object PageAppletContext : AppletContext {
    override fun showDocument(url: URL?, target: String?) {
        println("showDocument: " + url + " (target " + target + ")")
    }
}
