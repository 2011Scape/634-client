package com.jagex.game.runetek5.plugin

import kotlinx.browser.window

/* Not part of the original client: the browser's fetch for ItemPrices; the website allows any origin to read the prices. */
@Suppress("TooGenericExceptionCaught", "SwallowedException") // a browser that refuses the request means "no prices this time"
actual fun fetchText(url: String, done: (String?) -> Unit) {
	try {
		window.fetch(url).then({ response ->
			if (response.ok) {
				response.text().then({ text -> done(text) }, { done(null) })
			} else {
				done(null)
			}
		}, { done(null) })
	} catch (error: Throwable) {
		done(null)
	}
}
