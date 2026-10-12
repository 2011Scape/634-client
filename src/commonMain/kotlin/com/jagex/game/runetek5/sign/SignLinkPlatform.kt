package com.jagex.game.runetek5.sign

import awt.Component
import awt.Frame
import awt.Point
import lang.reflect.Method

/*
 * Not part of the original client: what SignLink reaches through reflection or the JVM's process
 * API (milestone 3). On the JVM each is the original code; another platform gives its own way, or
 * throws, which SignLink already handles as an unsupported feature.
 */

/* Component.setFocusTraversalKeysEnabled(boolean), looked up by reflection (Java 1.4). */
expect fun focusTraversalKeysEnabledMethod(): Method

/* Container.setFocusCycleRoot(boolean), looked up by reflection (Java 1.4). */
expect fun focusCycleRootMethod(): Method

/* A new instance of the class `className`, created by reflection so a missing class is not fatal. */
expect fun newInstanceOf(className: String): Any

/* WindowMode.listmodes, WindowMode.enter and WindowMode.exit on `windowMode`, by reflection. */
expect fun windowModeListModes(windowMode: Any?): Any?

expect fun windowModeEnter(windowMode: Any?, frame: Frame?, width: Int, height: Int, bitDepth: Int, refreshRate: Int)

expect fun windowModeExit(windowMode: Any?)

/* CursorManager.movemouse, showcursor and setcustomcursor on `cursorManager`, by reflection. */
expect fun cursorManagerMoveMouse(cursorManager: Any?, x: Int, y: Int)

expect fun cursorManagerShowCursor(cursorManager: Any?, component: Component?, visible: Boolean)

expect fun cursorManagerSetCustomCursor(cursorManager: Any?, component: Component?, pixels: IntArray?, width: Int, height: Int, hotspot: Point?)

/* Opens `url` with "cmd /c start" (Windows). */
expect fun openUrlWithCmd(url: String?)
