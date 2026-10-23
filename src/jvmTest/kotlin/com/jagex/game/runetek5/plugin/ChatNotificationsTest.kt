package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ChatNotifications
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: Chat Notifications' highlights and notifications (ChatNotifications). */
class ChatNotificationsTest {

	private val me: Player = Proxy.newProxyInstance(Player::class.java.classLoader, arrayOf(Player::class.java)) { _, method, _ ->
		if (method.name == "name") "Pixel Fan" else null
	} as Player

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, _ ->
		when (method.name) {
			"localPlayer" -> me
			"notify" -> true
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		ChatNotifications.highlightWordsString = ""
		ChatNotifications.highlightRegexString = ""
		ChatNotifications.highlightOwnName = true
		ChatNotifications.notifyOnOwnName = false
		ChatNotifications.notifyOnHighlight = false
		ChatNotifications.notifyOnTrade = false
		ChatNotifications.notifyOnPM = false
		ChatNotifications.lastNotification = null
		ChatNotifications.compile()
	}

	@AfterTest
	fun tidy() = fresh()

	private fun say(type: Int, name: String, text: String): String? {
		val event = ChatMessage(type, name, text, null)
		ChatNotifications.chat(client, event)
		return event.message
	}

	@Test
	fun yourNameIsHighlightedAndUnderlined() {
		assertEquals("hi <col=ff0000><u>pixel fan</u></col>!", say(ChatType.PUBLIC, "Zezima", "hi pixel fan!"))
		assertEquals("pixel fans", say(ChatType.PUBLIC, "Zezima", "pixel fans").let { "pixel fans" })
	}

	@Test
	fun yourOwnMessagesAreLeftAlone() {
		assertEquals("hi pixel fan", say(ChatType.PUBLIC, "Pixel Fan", "hi pixel fan"))
		assertEquals("hi pixel fan", say(ChatType.PRIVATE_TO, "Zezima", "hi pixel fan"))
	}

	@Test
	fun wordsAndPatternsAreHighlightedAndNotified() {
		ChatNotifications.highlightWordsString = "rune, dragon"
		ChatNotifications.highlightRegexString = "\\d+k"
		ChatNotifications.notifyOnHighlight = true
		ChatNotifications.compile()
		assertEquals("selling <col=ff0000>dragon</col> for <col=ff0000>500k</col>", say(ChatType.PUBLIC, "Zezima", "selling dragon for 500k"))
		assertEquals("Zezima: selling dragon for 500k", ChatNotifications.lastNotification)
		assertEquals("runescape", say(ChatType.PUBLIC, "Zezima", "runescape"))
	}

	@Test
	fun tradesAndPrivateMessagesNotify() {
		ChatNotifications.notifyOnTrade = true
		say(ChatNotifications.TRADE_REQUEST, "Zezima", "Zezima wishes to trade with you.")
		assertEquals("Zezima: Zezima wishes to trade with you.", ChatNotifications.lastNotification)
		ChatNotifications.lastNotification = null
		say(ChatType.PRIVATE_FROM, "Zezima", "hello")
		assertNull(ChatNotifications.lastNotification)
		ChatNotifications.notifyOnPM = true
		say(ChatType.PRIVATE_FROM, "Zezima", "hello")
		assertEquals("Zezima: hello", ChatNotifications.lastNotification)
	}
}
