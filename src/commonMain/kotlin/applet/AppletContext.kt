package applet

import net.URL

/* Not part of the original client: java.applet.AppletContext, for common code (milestone 3). */
expect interface AppletContext {
    fun showDocument(url: URL?, target: String?)
}
