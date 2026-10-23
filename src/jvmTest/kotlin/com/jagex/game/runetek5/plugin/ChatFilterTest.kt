package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.ChatFilter
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: Chat Filter's censoring, removal and collapsing (ChatFilter). */
class ChatFilterTest {

	private val friends: Set<String> = setOf("Buddy")

	private var newestId: Int = 10

	private val removed: ArrayList<Int> = ArrayList()

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"isFriend" -> args[0] in friends
			"isInFriendsChat" -> false
			"chatLine" -> PluginChatLine(newestId, 0, null, null, null)
			"removeChatLine" -> {
				removed.add(args[0] as Int)
				true
			}
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() {
		ChatFilter.filteredWords = "gold, bot"
		ChatFilter.filteredRegex = ""
		ChatFilter.filteredNames = ""
		ChatFilter.filterType = ChatFilter.CENSOR_WORDS
		ChatFilter.filterFriends = false
		ChatFilter.filterGameChat = false
		ChatFilter.collapseGameChat = false
		ChatFilter.collapsePlayerChat = false
		ChatFilter.maxRepeatedPublicChats = 0
		ChatFilter.stripAccents = false
		ChatFilter.compile()
		removed.clear()
	}

	@AfterTest
	fun tidy() {
		ChatFilter.filteredWords = ""
		ChatFilter.compile()
	}

	private fun say(type: Int, name: String, text: String): ChatMessage {
		val event = ChatMessage(type, name, text, null)
		ChatFilter.chat(client, event)
		newestId++
		return event
	}

	@Test
	fun wordsAreStarredCaseBlind() {
		assertEquals("buy **** cheap, no ***s", say(ChatType.PUBLIC, "Spammer", "buy GOLD cheap, no bots").message)
	}

	@Test
	fun theMessageCanBeReplacedOrRemoved() {
		ChatFilter.filterType = ChatFilter.CENSOR_MESSAGE
		assertEquals(ChatFilter.CENSOR_TEXT, say(ChatType.PUBLIC, "Spammer", "buy gold").message)
		ChatFilter.filterType = ChatFilter.REMOVE_MESSAGE
		assertTrue(say(ChatType.PUBLIC, "Spammer", "buy gold").isConsumed())
	}

	@Test
	fun friendsAndTheGameAreLeftAloneUnlessAsked() {
		assertEquals("buy gold", say(ChatType.PUBLIC, "Buddy", "buy gold").message)
		assertEquals("You find gold.", say(ChatType.GAME, "", "You find gold.").message)
		ChatFilter.filterGameChat = true
		assertEquals("You find ****.", say(ChatType.GAME, "", "You find gold.").message)
	}

	@Test
	fun aFilteredNameIsCensoredWhole() {
		ChatFilter.filteredNames = "^Spam.*"
		ChatFilter.compile()
		assertEquals("*****", say(ChatType.PUBLIC, "Spammer", "hello").message)
	}

	@Test
	fun accentsCanBeStripped() {
		assertEquals("góld", say(ChatType.PUBLIC, "Spammer", "góld").message)
		ChatFilter.stripAccents = true
		ChatFilter.compile()
		assertEquals("****", say(ChatType.PUBLIC, "Spammer", "góld").message)
	}

	@Test
	fun repeatsCollapseIntoTheLatest() {
		ChatFilter.collapseGameChat = true
		assertEquals("You need a pickaxe.", say(ChatType.GAME, "", "You need a pickaxe.").message)
		assertEquals("You need a pickaxe. (2)", say(ChatType.GAME, "", "You need a pickaxe.").message)
		assertEquals(listOf(11), removed)
		assertEquals("You need a pickaxe. (3)", say(ChatType.GAME, "", "You need a pickaxe.").message)
		assertEquals(listOf(11, 12), removed)
	}

	@Test
	fun publicChatRepeatedTooOftenIsDropped() {
		ChatFilter.collapsePlayerChat = true
		ChatFilter.maxRepeatedPublicChats = 2
		assertFalse(say(ChatType.PUBLIC, "Talker", "hi").isConsumed())
		assertFalse(say(ChatType.PUBLIC, "Talker", "hi").isConsumed())
		assertTrue(say(ChatType.PUBLIC, "Talker", "hi").isConsumed())
	}
}
