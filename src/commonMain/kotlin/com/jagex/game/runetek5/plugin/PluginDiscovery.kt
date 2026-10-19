package com.jagex.game.runetek5.plugin

/**
 * Not part of the original client: the plugins the platform can load from outside the client, each
 * as a registration (a failed one for a jar that could not be loaded). On the JVM, the jars in the
 * plugins folder (PluginLoader); in a browser, none.
 */
expect fun discoverPlugins(): Array<PluginRegistration>
