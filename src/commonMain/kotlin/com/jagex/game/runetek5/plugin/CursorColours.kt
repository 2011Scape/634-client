package com.jagex.game.runetek5.plugin

/*
 * Not part of the original client: how many colours the platform's mouse cursors can have
 * (java.awt.Toolkit.getMaximumCursorColors). Fewer than a full palette and PluginCursor draws a plugin's
 * cursor itself.
 */
expect fun maximumCursorColours(): Int
