package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.DiscordActivity

/* Not part of the original client: no Discord socket in a browser; the plugin runs, but says nothing. */
actual object DiscordRpc {
	actual fun start(applicationId: String, listener: DiscordRpcListener) {
		listener.event("Discord presence is not available in the browser")
	}

	actual fun setActivity(activity: DiscordActivity?) {
		// nowhere to send it
	}

	actual fun stop() {
		// nothing was started
	}

	actual fun isConnected(): Boolean {
		return false
	}

	actual fun status(): String {
		return "not available in the browser"
	}
}
