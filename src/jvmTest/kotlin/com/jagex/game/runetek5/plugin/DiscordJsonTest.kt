package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.DiscordActivity
import com.jagex.game.runetek5.client.features.DiscordJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Not part of the original client: the Discord plugin's hand-written JSON (DiscordJson). */
class DiscordJsonTest {

	@Test
	fun theHandshakeNamesTheApplication() {
		assertEquals("{\"v\":1,\"client_id\":\"1562513489396768839\"}", DiscordJson.handshake("1562513489396768839"))
	}

	@Test
	fun setActivityWritesEveryFieldAndEscapes() {
		val text: String = DiscordJson.setActivity(42, "7", DiscordActivity("Seers' \"Village\"", "Training: Fishing", "Void v1", "icon_fishing", 1700000000))
		assertEquals("{\"cmd\":\"SET_ACTIVITY\",\"args\":{\"pid\":42,\"activity\":{\"state\":\"Seers' \\\"Village\\\"\",\"details\":\"Training: Fishing\",\"timestamps\":{\"start\":1700000000},\"assets\":{\"large_image\":\"default\",\"large_text\":\"Void v1\",\"small_image\":\"icon_fishing\"},\"instance\":false}},\"nonce\":\"7\"}", text)
		val parsed: Any? = DiscordJson.parse(text)
		val activity: Map<*, *>? = DiscordJson.obj(DiscordJson.obj(parsed, "args"), "activity")
		assertEquals("Seers' \"Village\"", DiscordJson.string(activity, "state"))
		assertEquals(1700000000L, DiscordJson.long(DiscordJson.obj(activity, "timestamps"), "start"))
	}

	@Test
	fun noTimestampNoSmallImageAndEmptyLinesAreLeftOut() {
		val text: String = DiscordJson.setActivity(1, "1", DiscordActivity("In Game", "", "Void", null, 0))
		assertEquals("{\"cmd\":\"SET_ACTIVITY\",\"args\":{\"pid\":1,\"activity\":{\"state\":\"In Game\",\"assets\":{\"large_image\":\"default\",\"large_text\":\"Void\"},\"instance\":false}},\"nonce\":\"1\"}", text)
		val none: String = DiscordJson.setActivity(1, "1", DiscordActivity("", "", "Void", null, 0))
		assertTrue(!none.contains("state") && !none.contains("details"), none)
	}

	@Test
	fun clearingHasNoActivity() {
		assertEquals("{\"cmd\":\"SET_ACTIVITY\",\"args\":{\"pid\":5},\"nonce\":\"2\"}", DiscordJson.setActivity(5, "2", null))
	}

	@Test
	fun theReaderFindsTheUserInReady() {
		val ready: String = "{\"cmd\":\"DISPATCH\",\"data\":{\"v\":1,\"config\":{\"cdn_host\":\"cdn.discordapp.com\",\"api_endpoint\":\"//discord.com/api\",\"environment\":\"production\"},\"user\":{\"id\":\"123\",\"username\":\"pixel\",\"discriminator\":\"0\",\"avatar\":null,\"flags\":64,\"premium_type\":2.5}},\"evt\":\"READY\",\"nonce\":null}"
		val parsed: Any? = DiscordJson.parse(ready)
		assertEquals("READY", DiscordJson.string(parsed, "evt"))
		val user: Map<*, *>? = DiscordJson.obj(DiscordJson.obj(parsed, "data"), "user")
		assertEquals("pixel", DiscordJson.string(user, "username"))
		assertNull(DiscordJson.string(user, "avatar"))
		assertEquals(64L, DiscordJson.long(user, "flags"))
		assertEquals(2L, DiscordJson.long(user, "premium_type"))
	}

	@Test
	fun theReaderFindsAnError() {
		val parsed: Any? = DiscordJson.parse("{\"cmd\":\"SET_ACTIVITY\",\"data\":{\"code\":4000,\"message\":\"Invalid Client ID\"},\"evt\":\"ERROR\",\"nonce\":\"3\"}")
		assertEquals("ERROR", DiscordJson.string(parsed, "evt"))
		assertEquals(4000L, DiscordJson.long(DiscordJson.obj(parsed, "data"), "code"))
		assertEquals("Invalid Client ID", DiscordJson.string(DiscordJson.obj(parsed, "data"), "message"))
	}

	@Test
	fun escapesListsAndNestingRead() {
		val parsed: Any? = DiscordJson.parse(" {\"a\":[1,-2,true,false,null,\"x\\n\\u0041\\\"\"],\"b\":{\"c\":{}} , \"d\":[]} ")
		val a: List<*> = (parsed as Map<*, *>)["a"] as List<*>
		assertEquals(listOf<Any?>(1L, -2L, true, false, null, "x\nA\""), a)
		assertEquals(0, DiscordJson.obj(DiscordJson.obj(parsed, "b"), "c")!!.size)
	}

	@Test
	fun brokenTextIsNull() {
		assertNull(DiscordJson.parse(""))
		assertNull(DiscordJson.parse("{\"a\":}"))
		assertNull(DiscordJson.parse("{\"a\":1} x"))
		assertNull(DiscordJson.parse("[1,2"))
		assertNull(DiscordJson.parse("\"open"))
		assertNull(DiscordJson.string("not an object", "a"))
	}
}
