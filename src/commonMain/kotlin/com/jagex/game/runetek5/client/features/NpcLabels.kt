package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.OverlayManager
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.config.npc.NPCType
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: each npc on screen shows its name, and its combat level when it
 * has one, above its head. It picks npcs and their place the way the overhead text does
 * (OverlayManager.drawEntities): the npcs updated this cycle, the type a transforming npc shows
 * now, the npcs on the player's level or drawn this frame, and the point at their overhead height.
 * On only with `client.npcLabels=true`; the console command `npclabels` turns it on and
 * off while playing.
 */
object NpcLabels {

	@JvmField
	var enabled: Boolean = lang.getBoolean("client.npcLabels")

	private const val COLOUR: Int = 0xFF00FFFF.toInt()

	private const val SHADOW: Int = 0xFF000000.toInt()

	/** How far above the overhead point the label sits, clear of overhead chat. */
	private const val RAISE: Int = 14

	/** How many labels the last frame drew, for the smoke test. */
	@JvmField
	var drawnLastFrame: Int = 0

	/** The npc types and screen points the last frame labelled, for the smoke test. */
	@JvmField
	val lastTypes: IntArray = IntArray(NPCList.NPC_ARRAY.size)

	@JvmField
	val lastX: IntArray = IntArray(NPCList.NPC_ARRAY.size)

	@JvmField
	val lastY: IntArray = IntArray(NPCList.NPC_ARRAY.size)

	/** The label an npc type shows: its name, and its combat level when it has one. */
	@JvmStatic
	fun label(type: NPCType?): String? {
		return if (type!!.combatLevel > 0) plainText(type.name) + " (level-" + type.combatLevel + ")" else plainText(type.name)
	}

	/**
	 * A name from the cache as plain text: its < and > drawn as themselves rather than read as the
	 * font's tags (<col>, <img=>, <br>). The same string back when it has neither, as almost every name.
	 */
	@JvmStatic
	fun plainText(name: String?): String? {
		if (name == null || name.indexOf('<') == -1 && name.indexOf('>') == -1) {
			return name
		}
		return Utils.escapeTags(name)
	}

	/** Labels the npcs in the game view the scene was just drawn into. */
	@JvmStatic
	fun draw(clipLeft: Int, clipTop: Int, clipWidth: Int, height: Int) {
		drawnLastFrame = 0
		if (!enabled || Renderers.FONT_RENDERER == null) {
			return
		}
		var i: Int = 0
		while (i < NPCList.NPC_UPDATE_INDEX && drawnLastFrame < lastTypes.size) {
			val node: NPCEntityNode? = NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[i].toLong()) as NPCEntityNode?
			if (node == null) {
				i++
				continue
			}
			val npc: NPCEntity? = node.npc
			var type: NPCType? = npc!!.npcType
			if (type != null && type.alternativeIds != null) {
				type = type.getMultiNpc(PlayerVarDomain.playerVarProvider)
			}
			if (type == null || type.name == null || type.name == "null") {
				i++
				continue
			}
			if (npc.worldPriority < 0 || SceneDrawer.timesSceneRendered != npc.drawCycle && npc.level != PlayerUpdate.localPlayer!!.level) {
				i++
				continue
			}
			OverlayManager.projectEntityToScreen(npc.getOverheadHeight(), clipWidth shr 1, height shr 1, npc)
			if (OverlayManager.projectedScreenPosition[0] < 0) {
				i++
				continue
			}
			val x: Int = clipLeft + OverlayManager.projectedScreenPosition[0]
			val y: Int = clipTop + OverlayManager.projectedScreenPosition[1] - RAISE
			Renderers.FONT_RENDERER!!.renderCentered(x, COLOUR, label(type), SHADOW, y)
			lastTypes[drawnLastFrame] = type.id
			lastX[drawnLastFrame] = x
			lastY[drawnLastFrame] = y
			drawnLastFrame++
			i++
		}
	}
}
