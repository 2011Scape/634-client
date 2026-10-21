package com.jagex.game.runetek5.plugin

import kotlinx.browser.window

/*
 * Not part of the original client: the browser side of requestWindowFocus. window.focus() raises
 * the page's window where the browser lets a page do that (most refuse it outside a user gesture,
 * and then nothing happens). There is always a window, so this is true.
 */
@Suppress("TooGenericExceptionCaught", "SwallowedException") // a browser that refuses is the same as one that ignores it
actual fun requestWindowFocus(): Boolean {
	try {
		window.focus()
	} catch (error: Throwable) {
		/* refused: nothing more to do */
	}
	return true
}
