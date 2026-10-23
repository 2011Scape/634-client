package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ChatHistory
import com.jagex.game.runetek5.plugin.api.ChatLineView
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Not part of the original client: Chat History's kept lines, their return after a login, and the tabs' clearing (ChatHistory). */
class ChatHistoryTest {

	private val lines: ArrayList<ChatLineView> = ArrayList()

	private val added: ArrayList<String> = ArrayList()

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"addChatLine" -> {
				added.add(args[2] as String)
				null
			}
			"chatLineCount" -> lines.size
			"chatLine" -> lines.getOrNull(args[0] as Int)
			"removeChatLine" -> lines.removeAll { it.id() == args[0] }
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		ChatHistory.retainChatHistory = true
		ChatHistory.history.clear()
		ChatHistory.restorePending = false
	}

	@AfterTest
	fun tidy() = fresh()

	private fun say(type: Int, text: String) = ChatHistory.chat(client, ChatMessage(type, "Zezima", text, null))

	@Test
	fun playerChatIsKeptAndComesBackAfterALogin() {
		say(ChatType.PUBLIC, "hello")
		say(ChatType.GAME, "You catch a shrimp.")
		say(ChatType.PRIVATE_FROM, "psst")
		assertEquals(2, ChatHistory.history.size)
		ChatHistory.restorePending = true
		say(ChatType.GAME, "Welcome.")
		assertEquals(listOf("hello", "psst"), added)
	}

	@Test
	fun onlyTheLastHundredAreKept() {
		repeat(130) { say(ChatType.PUBLIC, "line " + it) }
		assertEquals(100, ChatHistory.history.size)
		assertEquals("line 30", ChatHistory.history.first().message)
	}

	@Test
	fun theTabsAreFoundByTheirLayersAndParts() {
		assertEquals(0, ChatHistory.tab(2))
		assertEquals(2, ChatHistory.tab(4))
		assertEquals(6, ChatHistory.tab(12))
		assertEquals(2, ChatHistory.tab(25))
		assertEquals(0, ChatHistory.tab(31))
		assertEquals(-1, ChatHistory.tab(40))
	}

	@Test
	fun clearingATabTakesItsLinesOut() {
		lines.add(PluginChatLine(3, ChatType.PUBLIC, "a", "hi", null))
		lines.add(PluginChatLine(2, ChatType.GAME, null, "game", null))
		lines.add(PluginChatLine(1, ChatType.PUBLIC, "b", "yo", null))
		say(ChatType.PUBLIC, "hi")
		ChatHistory.clearTab(client, 4)
		assertEquals(listOf(2), lines.map { it.id() })
		assertEquals(0, ChatHistory.history.size)
	}
}
