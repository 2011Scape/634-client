package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.plugin.api.HoveredTarget
import com.jagex.game.runetek5.plugin.api.MenuTarget
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: a menu entry read as what it acts on (ClientApi.hoveredTarget). An
 * npc's and a player's entries carry the index as their uid; a location's carry its scene tile in param0
 * and param1 and its id in the uid's high 32 bits (MapLoader.getLocMenuUid), except Examine, whose uid is
 * the id itself; a ground item's carry its tile and its id as the uid. A level penalty (2000, an entry
 * pushed below Walk here) is taken off first.
 */
object HoveredTargets {

	private const val LEVEL_PENALTY: Int = 2000

	private val NPC: IntArray = intArrayOf(MiniMenuAction.OPNPC1, MiniMenuAction.OPNPC2, MiniMenuAction.OPNPC3, MiniMenuAction.OPNPC4, MiniMenuAction.OPNPC5, MiniMenuAction.OPNPC6, MiniMenuAction.TGT_NPC)

	private val PLAYER: IntArray = intArrayOf(MiniMenuAction.OPPLAYER1, MiniMenuAction.OPPLAYER2, MiniMenuAction.OPPLAYER3, MiniMenuAction.OPPLAYER4, MiniMenuAction.OPPLAYER5, MiniMenuAction.OPPLAYER6, MiniMenuAction.OPPLAYER7, MiniMenuAction.OPPLAYER8, MiniMenuAction.TGT_PLAYER)

	private val LOC: IntArray = intArrayOf(MiniMenuAction.OPLOC1, MiniMenuAction.OPLOC2, MiniMenuAction.OPLOC3, MiniMenuAction.OPLOC4, MiniMenuAction.OPLOC5, MiniMenuAction.TGT_LOC)

	private val OBJ: IntArray = intArrayOf(MiniMenuAction.OPOBJ1, MiniMenuAction.OPOBJ2, MiniMenuAction.OPOBJ3, MiniMenuAction.OPOBJ4, MiniMenuAction.OPOBJ5, MiniMenuAction.OPOBJ6, MiniMenuAction.TGT_OBJ)

	/** An entry of [clickType] with [param0], [param1] and [uid] as a target into [out]; false when it is on none. */
	@JvmStatic
	fun decode(clickType: Int, param0: Int, param1: Int, uid: Long, option: String?, out: HoveredTarget): Boolean {
		val type: Int = if (clickType >= LEVEL_PENALTY) clickType - LEVEL_PENALTY else clickType
		out.option = option
		out.sceneX = 0
		out.sceneY = 0
		when (type) {
			in NPC -> {
				out.kind = MenuTarget.NPC
				out.id = uid.toInt()
			}
			in PLAYER -> {
				out.kind = MenuTarget.PLAYER
				out.id = uid.toInt()
			}
			in LOC, MiniMenuAction.OPLOC6 -> {
				out.kind = MenuTarget.LOC
				out.id = if (type == MiniMenuAction.OPLOC6) uid.toInt() else (uid ushr 32).toInt() and 0x7FFFFFFF
				out.sceneX = param0
				out.sceneY = param1
			}
			in OBJ -> {
				out.kind = MenuTarget.GROUND_ITEM
				out.id = uid.toInt()
				out.sceneX = param0
				out.sceneY = param1
			}
			else -> return false
		}
		return true
	}
}
