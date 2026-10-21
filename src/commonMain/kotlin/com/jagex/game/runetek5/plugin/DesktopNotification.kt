package com.jagex.game.runetek5.plugin

/**
 * Not part of the original client: a notification on the desktop for the plugins
 * (plugin.api.ClientApi.notify), titled [title] and saying [text]. A balloon from the system tray
 * on the JVM, a browser notification on the web. True when the platform showed it; false when it
 * cannot (no system tray, a headless JVM, the browser's permission not given).
 */
expect fun showDesktopNotification(title: String, text: String): Boolean
