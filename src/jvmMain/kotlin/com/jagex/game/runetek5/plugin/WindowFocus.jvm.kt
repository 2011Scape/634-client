package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.window.WindowMode
import java.awt.Canvas
import java.awt.Frame

/*
 * Not part of the original client: the JVM side of requestWindowFocus. The full screen frame when
 * there is one, else the windowed frame. toFront asks the window manager to raise and activate the
 * window; KDE and GNOME may answer with "demands attention" (a highlighted task bar entry) instead,
 * their focus stealing prevention, which is theirs to decide. The canvas then asks for the keyboard
 * inside the window, so the game keys work once it is active.
 */
actual fun requestWindowFocus(): Boolean {
	val frame: Frame = WindowMode.fullScreenFrame ?: GameShell.engineFrame ?: return false
	frame.toFront()
	frame.requestFocus()
	val canvas: Canvas? = GameShell.gameCanvas
	if (canvas != null) {
		canvas.requestFocusInWindow()
	}
	return true
}
