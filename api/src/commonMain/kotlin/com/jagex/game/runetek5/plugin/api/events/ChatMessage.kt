package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * See [Events.CHAT_MESSAGE]. [type] is the client's message type (0 is a game message, 2 public chat,
 * 3 and 6 private, 9 trade requests, 99 filtered game messages...), [name] the sender or empty.
 * A listener may set [message] (RuneLite's MessageNode.setValue): the chat box shows what it is once
 * every listener has run, the later listeners seeing the change. Settable since API 19. A listener may
 * also [consume] it (RuneLite's chat filter dropping a message): the chat box never stores the line, the
 * later listeners still seeing it, consumed. Since API 26.
 */
class ChatMessage(@JvmField val type: Int, @JvmField val name: String?, @JvmField var message: String?, @JvmField val clan: String?) {
	private var consumed: Boolean = false

	/** The line is not stored in the chat box. Since API 26. */
	fun consume() {
		consumed = true
	}

	/** Whether a listener [consume]d the line. Since API 26. */
	fun isConsumed(): Boolean = consumed
}
