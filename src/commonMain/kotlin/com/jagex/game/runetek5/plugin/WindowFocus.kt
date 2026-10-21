package com.jagex.game.runetek5.plugin

/**
 * Not part of the original client: brings the game's window to the front and asks for the keyboard
 * focus, for the plugins (plugin.api.ClientApi.requestFocus). The frame on the JVM (the desktop may
 * only mark it as wanting attention), the browser's own window on the web. True when there was a
 * window to ask for.
 */
expect fun requestWindowFocus(): Boolean
