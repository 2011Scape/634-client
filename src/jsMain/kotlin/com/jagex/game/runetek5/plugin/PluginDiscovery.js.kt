package com.jagex.game.runetek5.plugin

/*
 * Not part of the original client: a browser loads no plugins from outside the bundle; the
 * built-in plugins (BuiltinPlugins) are all it runs.
 */
actual fun discoverPlugins(): Array<PluginRegistration> {
    return arrayOf()
}
