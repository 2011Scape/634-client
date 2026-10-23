package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.ChatLineView

/** Not part of the original client: a chat box line as the plugins see it (ClientApi.chatLine), a copy. */
class PluginChatLine(private val id: Int, private val type: Int, private val name: String?, private val message: String?, private val clan: String?) : ChatLineView {
	override fun id(): Int = id

	override fun type(): Int = type

	override fun name(): String? = name

	override fun message(): String? = message

	override fun clan(): String? = clan
}
