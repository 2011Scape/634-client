package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.ChatLineWriter
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.menu.MiniMenuEntry
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: what the client's own windows in the Graphics Options shell (the plugin
 * manager, PluginManagerScreen, and the plugins' screens built the same way) take from the game while one is
 * open, as a modal interface of the game's would. The server knows nothing of these windows, so this is the
 * client's to do: the game world's right-click entries go from the menu, leaving Cancel, and the Settings tab's
 * Graphics and Audio buttons answer with the game's own line instead of sending the click, since the server
 * would open those in the window's place.
 */
object ClientWindows {

	/** The Settings tab's Graphics Settings and Audio Settings buttons (interface 261). */
	private const val TAB_GRAPHICS: Int = (261 shl 16) or 16
	private const val TAB_AUDIO: Int = (261 shl 16) or 18

	/** Whether one of the client's own windows is open. */
	@JvmStatic
	fun isOpen(): Boolean = PluginManagerScreen.open || XpTrackerScreen.open || LootTrackerScreen.open

	/**
	 * Whether a menu entry of [clickType] (2000 and up while an item is selected) is for the game world: walking,
	 * an npc, a player, a location or an item on the ground. The interfaces' entries are not.
	 */
	@JvmStatic
	fun sceneEntry(clickType: Int): Boolean = when (clickType % 2000) {
		MiniMenuAction.WALK, MiniMenuAction.FACE_SQUARE,
		MiniMenuAction.OPNPC1, MiniMenuAction.OPNPC2, MiniMenuAction.OPNPC3, MiniMenuAction.OPNPC4, MiniMenuAction.OPNPC5, MiniMenuAction.OPNPC6,
		MiniMenuAction.OPLOC1, MiniMenuAction.OPLOC2, MiniMenuAction.OPLOC3, MiniMenuAction.OPLOC4, MiniMenuAction.OPLOC5, MiniMenuAction.OPLOC6,
		MiniMenuAction.OPOBJ1, MiniMenuAction.OPOBJ2, MiniMenuAction.OPOBJ3, MiniMenuAction.OPOBJ4, MiniMenuAction.OPOBJ5, MiniMenuAction.OPOBJ6,
		MiniMenuAction.OPPLAYER1, MiniMenuAction.OPPLAYER2, MiniMenuAction.OPPLAYER3, MiniMenuAction.OPPLAYER4,
		MiniMenuAction.OPPLAYER5, MiniMenuAction.OPPLAYER6, MiniMenuAction.OPPLAYER7, MiniMenuAction.OPPLAYER8,
		MiniMenuAction.TGT_GROUND, MiniMenuAction.TGT_LOC, MiniMenuAction.TGT_NPC, MiniMenuAction.TGT_OBJ, MiniMenuAction.TGT_PLAYER, MiniMenuAction.TGT_SELF,
		-> true
		else -> false
	}

	/**
	 * The menu for what is under the mouse is built (MiniMenuInputTick): with a window open, the game world's
	 * entries go, so a click on the world offers Cancel alone.
	 * @return whether the menu changed
	 */
	@JvmStatic
	fun menuBuilt(): Boolean {
		if (!isOpen() || MiniMenu.isOpen) {
			return false
		}
		val scene: ArrayList<MiniMenuEntry> = ArrayList()
		var item: MiniMenuEntry? = MiniMenu.entryList.first() as MiniMenuEntry?
		while (item != null) {
			if (sceneEntry(item.clickType)) {
				scene.add(item)
			}
			item = MiniMenu.entryList.next() as MiniMenuEntry?
		}
		for (entry in scene) {
			MiniMenu.removeEntry(entry)
		}
		return scene.isNotEmpty()
	}

	/**
	 * A menu entry is clicked (MiniMenuActionExecutor), [clickType] without its item-selected offset: with a window
	 * open, the Graphics or Audio Settings button answers with the game's own line, and the click goes no further.
	 * @return whether the click was taken
	 */
	@JvmStatic
	fun menuClicked(clickType: Int, componentHash: Int): Boolean {
		if (!isOpen() || (clickType != MiniMenuAction.IF_BUTTONX1 && clickType != MiniMenuAction.IF_BUTTONX2)) {
			return false
		}
		val options: String = when (componentHash) {
			TAB_GRAPHICS -> "graphics"
			TAB_AUDIO -> "audio"
			else -> return false
		}
		ChatLineWriter.writeUnfilterableMessage("Please close the interface you have open before setting your $options options.")
		return true
	}
}
