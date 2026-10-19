package com.jagex.game.runetek5.plugin.api.events

import kotlin.jvm.JvmField

/**
 * See [Events.CHAT_MESSAGE]. [type] is the client's message type (0 is a game message, 2 public chat,
 * 3 and 6 private, 9 trade requests, 99 filtered game messages...), [name] the sender or empty.
 */
class ChatMessage(@JvmField val type: Int, @JvmField val name: String?, @JvmField val message: String?, @JvmField val clan: String?)
