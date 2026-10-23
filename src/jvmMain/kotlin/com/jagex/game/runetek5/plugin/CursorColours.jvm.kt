package com.jagex.game.runetek5.plugin

import java.awt.GraphicsEnvironment
import java.awt.Toolkit

/* Not part of the original client: AWT's cursor colours; on X11 (and XWayland) only 2, a foreground and a background. */
actual fun maximumCursorColours(): Int = if (GraphicsEnvironment.isHeadless()) 0 else Toolkit.getDefaultToolkit().maximumCursorColors
