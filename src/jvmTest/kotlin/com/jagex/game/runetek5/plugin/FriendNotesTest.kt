package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.FriendNotes
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Widget
import java.lang.reflect.Proxy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Not part of the original client: Friend Notes' notes and how it finds a friend's row (FriendNotes). */
class FriendNotesTest {

	/** A friends list row: text, place, size. */
	private fun row(text: String, x: Int, y: Int): Widget = Proxy.newProxyInstance(Widget::class.java.classLoader, arrayOf(Widget::class.java)) { _, method, _ ->
		when (method.name) {
			"text" -> text
			"screenX" -> x
			"screenY" -> y
			"width" -> 120
			"height" -> 15
			"isHidden" -> false
			"isDrawnThisFrame" -> true
			"childCount" -> 0
			else -> null
		}
	} as Widget

	private val rows: List<Widget> = listOf(row("<col=ffffff>Zezima", 10, 100), row("<img=1>Bob Builder", 10, 115), row("Offline", 10, 130))

	/** Component 3 of the friends list holds [rows] as its dynamic children; the friends are Zezima and Bob Builder. */
	private val list: Widget = Proxy.newProxyInstance(Widget::class.java.classLoader, arrayOf(Widget::class.java)) { _, method, args ->
		when (method.name) {
			"childCount" -> rows.size
			"child" -> rows.getOrNull(args[0] as Int)
			"isHidden" -> false
			"isDrawnThisFrame" -> true
			else -> null
		}
	} as Widget

	private val client: ClientApi = Proxy.newProxyInstance(ClientApi::class.java.classLoader, arrayOf(ClientApi::class.java)) { _, method, args ->
		when (method.name) {
			"widget" -> if (args[0] == FriendNotes.FRIENDS && args[1] == 3) list else null
			"isFriend" -> (args[0] as String?) in listOf("Zezima", "Bob Builder")
			else -> null
		}
	} as ClientApi

	@BeforeTest
	fun fresh() = FriendNotes.notes.clear()

	@AfterTest
	fun tidy() = fresh()

	@Test
	fun notesAreKeptByName() {
		FriendNotes.setNote("Bob Builder", "  owes me 10k  ")
		assertEquals("owes me 10k", FriendNotes.note("bob builder"))
		assertEquals("owes me 10k", FriendNotes.note("Bob Builder"))
		FriendNotes.setNote("Bob Builder", " ")
		assertNull(FriendNotes.note("Bob Builder"))
		FriendNotes.setNote("Zezima", "x".repeat(200))
		assertEquals(FriendNotes.MAX_NOTE, FriendNotes.note("Zezima")!!.length)
	}

	@Test
	fun aRowIsAFriendsByItsText() {
		assertEquals("Zezima", FriendNotes.plain("<col=ffffff>Zezima"))
		val id: Int = (FriendNotes.FRIENDS shl 16) or 3
		assertEquals("Zezima", FriendNotes.rowName(client, id, 0))
		assertEquals("Bob Builder", FriendNotes.rowName(client, id, 1))
		assertNull(FriendNotes.rowName(client, id, 2), "not a friend")
	}

	@Test
	fun theNoteShowsOverItsRow() {
		FriendNotes.setNote("Bob Builder", "tank")
		assertEquals("Note: tank", FriendNotes.tooltipAt(client, 50, 120))
		assertNull(FriendNotes.tooltipAt(client, 50, 105), "Zezima has none")
		assertNull(FriendNotes.tooltipAt(client, 500, 120))
		assertEquals(115, FriendNotes.rowOf(client, "bob builder")?.screenY())
	}
}
