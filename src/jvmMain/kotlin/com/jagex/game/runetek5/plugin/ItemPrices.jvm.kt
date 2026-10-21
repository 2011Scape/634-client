package com.jagex.game.runetek5.plugin

import java.net.HttpURLConnection
import java.net.URI

/* Not part of the original client: the JVM's fetch for ItemPrices, on a daemon thread with short timeouts. */
@Suppress("TooGenericExceptionCaught", "SwallowedException") // any failure is "no prices this time"; the next try comes later
actual fun fetchText(url: String, done: (String?) -> Unit) {
	val thread: Thread = Thread({
		var result: String? = null
		try {
			val connection: HttpURLConnection = URI(url).toURL().openConnection() as HttpURLConnection
			connection.connectTimeout = 10_000
			connection.readTimeout = 20_000
			connection.setRequestProperty("User-Agent", "634-client")
			connection.setRequestProperty("Accept", "application/json")
			if (connection.responseCode == 200) {
				result = connection.inputStream.use { it.readBytes().toString(Charsets.UTF_8) }
			}
			connection.disconnect()
		} catch (error: Throwable) {
			result = null
		}
		done(result)
	}, "item-prices")
	thread.isDaemon = true
	thread.start()
}
