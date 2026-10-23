package com.jagex.game.runetek5.plugin.api

/**
 * A line the chat box holds (RuneLite's MessageNode), as [ClientApi.chatLine] gives it: [id] stays the
 * line's while it is held, [type] is [ChatMessage]'s, [message] the text shown (after the plugins'
 * changes). Since API 26.
 */
interface ChatLineView {
	fun id(): Int

	fun type(): Int

	fun name(): String?

	fun message(): String?

	fun clan(): String?
}
