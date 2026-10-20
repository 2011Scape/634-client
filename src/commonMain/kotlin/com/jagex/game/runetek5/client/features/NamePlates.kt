package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: every other player's name over their head, red when they can
 * attack you here, green for a friend, blue for a member of your friends chat (with the chat's name
 * as <Name> underneath), white otherwise. On only with `client.namePlates=true` or after
 * `::nameplates`, kept in the plugin's settings.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md) on API 5: Player.isAttackable and the friends
 * chat reads are what API 4 lacked.
 */
object NamePlates : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("nameplates", "Name plates", "1", PluginApi.VERSION, "Other players' names over their heads: red when attackable, green for friends, blue for your friends chat (::nameplates turns it on and off).", "634-client")

	const val ATTACKABLE: Int = 0xFFFF3030.toInt()

	const val FRIEND: Int = 0xFF40FF40.toInt()

	const val CHAT: Int = 0xFF60A0FF.toInt()

	const val PLAIN: Int = 0xFFFFFFFF.toInt()

	private const val SHADOW: Int = 0xFF000000.toInt()

	/** How far above the head the name sits, in pixels, as the npc labels do. */
	private const val RAISE: Int = 14

	/** One more line up while the player's chat shows over their head. */
	private const val LINE: Int = 12

	@JvmField
	var enabled: Boolean = false

	/** The plates the last frame drew, for the smoke test. */
	@JvmField
	var drawnLastFrame: Int = 0

	private val point: IntArray = IntArray(2)

	private var chatName: String? = null

	private var chatLine: String? = null

	private var context: PluginContext? = null

	private val overlay: Overlay = Overlay { g, view -> draw(g, view) }

	private val toggle: CommandHandler = CommandHandler { _, _ ->
		val context: PluginContext = this.context ?: return@CommandHandler
		enabled = !enabled
		context.config().setBoolean("enabled", enabled)
		context.config().save()
		context.client().consolePrint(if (enabled) "Name plates on." else "Name plates off.")
	}

	override fun start(context: PluginContext) {
		this.context = context
		enabled = context.config().getBoolean("enabled", lang.getBoolean("client.namePlates"))
		context.addOverlay(overlay)
		context.registerCommand("nameplates", toggle)
	}

	override fun stop() {
		context = null
	}

	/** The colour by what the player is to you: attackable first, then friend, then chat, else plain. */
	@JvmStatic
	fun colourFor(attackable: Boolean, friend: Boolean, inChat: Boolean): Int {
		return if (attackable) ATTACKABLE else if (friend) FRIEND else if (inChat) CHAT else PLAIN
	}

	/** The "<Name>" line for the chat, made again only when the chat changes. */
	private fun chatLine(client: ClientApi): String? {
		val name: String? = client.friendsChatName()
		if (name != chatName) {
			chatName = name
			chatLine = if (name == null) null else "<" + name + ">"
		}
		return chatLine
	}

	private fun draw(g: Graphics, view: Viewport) {
		drawnLastFrame = 0
		val client: ClientApi = context?.client() ?: return
		if (!enabled || !g.fontReady() || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		for (i in 0 until client.playerCount()) {
			val player: Player? = client.player(i)
			if (player != null && drawPlate(g, view, client, player)) {
				drawnLastFrame++
			}
		}
	}

	/** One player's plate; false when there is nothing to draw for them. */
	private fun drawPlate(g: Graphics, view: Viewport, client: ClientApi, player: Player): Boolean {
		if (player.isLocalPlayer() || !player.isDrawnThisFrame()) {
			return false
		}
		val name: String = player.displayName() ?: return false
		if (!client.projectActor(player, player.overheadHeight(), point)) {
			return false
		}
		val inChat: Boolean = client.isInFriendsChat(player.name())
		val colour: Int = colourFor(player.isAttackable(), client.isFriend(player.name()), inChat)
		val x: Int = view.x + point[0]
		var y: Int = view.y + point[1] - RAISE
		if (player.overheadText() != null) {
			y -= LINE // the chat over the head keeps its place
		}
		g.textCentered(x, y, name, colour, SHADOW)
		if (inChat) {
			val line: String? = chatLine(client)
			if (line != null) {
				g.textCentered(x, y + LINE, line, colour, SHADOW)
			}
		}
		return true
	}
}
