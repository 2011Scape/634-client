package applet

import net.URL

actual interface AppletContext {
    actual fun showDocument(url: URL?, target: String?)
}
