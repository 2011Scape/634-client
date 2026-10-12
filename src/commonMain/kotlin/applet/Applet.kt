package applet

import awt.Panel
import net.URL

/*
 * Not part of the original client: java.applet.Applet, the class the client's GameShell extends,
 * declared for common code (milestone 3). On the JVM it is the JDK's own, so the launcher still
 * drives the client as an applet.
 */
expect open class Applet : Panel {
    constructor()
    open fun init()
    open fun start()
    open fun stop()
    open fun destroy()
    open fun getParameter(name: String?): String?
    open fun getCodeBase(): URL?
    open fun getDocumentBase(): URL?
    open fun getAppletContext(): AppletContext?
}
