package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.ChatLineWriter
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.OverlayManager
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.cache.node.ObjStackEntry
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.input.console.Console
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.core.constants.MainLogicStep
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.entity.InteractiveEntityContainer
import com.jagex.game.runetek5.entity.PathingEntity
import com.jagex.game.runetek5.entity.gameobject.GameObject
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.plugin.api.Actor
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.GroundItemVisitor
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.ItemDefinition
import com.jagex.game.runetek5.plugin.api.LocDefinition
import com.jagex.game.runetek5.plugin.api.LocKind
import com.jagex.game.runetek5.plugin.api.LocVisitor
import com.jagex.game.runetek5.plugin.api.Npc
import com.jagex.game.runetek5.plugin.api.NpcDefinition
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.Widget
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.scene.CollisionMap
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.tile.Tile
import com.jagex.game.runetek5.statics.OutPlayerIndexCount
import com.jagex.game.runetek5.statics.StatBaseLevels
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import kotlin.jvm.JvmStatic
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer as ClientItemContainer

/**
 * Not part of the original client: the plugin API's view of the game, over the client's statics
 * (docs/ENTITIES.md, CONFIG.md, CS2.md). Every answer is for the game thread.
 */
object ClientApiImpl : ClientApi {

	/** The broad state a main logic step is in. */
	@JvmStatic
	fun stateOf(step: MainLogicStep?): GameState {
		return when (step) {
			MainLogicStep.STEP_GAME_SCREEN, MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD, MainLogicStep.STEP_RECONNECTING -> GameState.LOGGED_IN
			MainLogicStep.STEP_LOBBY_SCREEN, MainLogicStep.STEP_LOBBY_SCREEN_MAP_BUILD, MainLogicStep.STEP_LOGGING_IN_FROM_LOBBYSCREEN_TO_GAME, MainLogicStep.STEP_LOGGING_IN_FROM_GAME_TO_LOBBY -> GameState.LOBBY
			MainLogicStep.STEP_LOGIN_SCREEN, MainLogicStep.STEP_LOGIN_SCREEN_MAP_BUILD, MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_LOBBY, MainLogicStep.STEP_LOGGING_IN_FROM_LOGINSCREEN_TO_GAME -> GameState.LOGIN_SCREEN
			else -> GameState.LOADING
		}
	}

	override fun gameState(): GameState {
		return stateOf(ClientState.mainLogicStep)
	}

	override fun clientCycle(): Int {
		return ClientState.clientCycle
	}

	override fun serverTick(): Int {
		return ClientState.serverTick
	}

	override fun localPlayer(): Player? {
		return PlayerUpdate.localPlayer
	}

	override fun playerCount(): Int {
		return OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
	}

	override fun player(i: Int): Player? {
		if (i < 0 || i >= OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES) {
			return null
		}
		return PlayerUpdate.players[PlayerUpdate.OUT_PLAYER_INDICES[i]]
	}

	override fun npcCount(): Int {
		return NPCList.NPC_UPDATE_INDEX
	}

	override fun npc(i: Int): Npc? {
		if (i < 0 || i >= NPCList.NPC_UPDATE_INDEX) {
			return null
		}
		val node: NPCEntityNode = NPCList.NPC_MAP.get(NPCList.NPC_UPDATE_INDICES[i].toLong()) as NPCEntityNode? ?: return null
		return node.npc
	}

	override fun groundItems(visitor: GroundItemVisitor) {
		var node: Node? = ObjStack.GROUND_ITEMS.first()
		while (node != null) {
			// the key packs level << 28 | absolute y << 14 | absolute x (ObjStack.addGroundItem)
			val key: Long = node.key
			val level: Int = (key ushr 28 and 0x3L).toInt()
			val sceneX: Int = (key and 0x3FFFL).toInt() - MapRegion.sceneBaseX
			val sceneY: Int = (key shr 14 and 0x3FFFL).toInt() - MapRegion.sceneBaseY
			val pile: ObjStack = node as ObjStack
			var entry: ObjStackEntry? = pile.groundItems.first() as ObjStackEntry?
			while (entry != null) {
				visitor.visit(sceneX, sceneY, level, entry.id, entry.count)
				entry = pile.groundItems.next() as ObjStackEntry?
			}
			node = ObjStack.GROUND_ITEMS.next()
		}
	}

	override fun skillXp(skill: Int): Int {
		return if (skill >= 0 && skill < Skill.COUNT) client.statXp[skill] else 0
	}

	override fun skillLevel(skill: Int): Int {
		return if (skill >= 0 && skill < Skill.COUNT) client.statLevels[skill] else 0
	}

	override fun skillBaseLevel(skill: Int): Int {
		return if (skill >= 0 && skill < Skill.COUNT) StatBaseLevels.statBaseLevels[skill] else 0
	}

	override fun varp(id: Int): Int {
		val vars: PlayerVarDomain = PlayerVarDomain.playerVarProvider ?: return 0
		if (id < 0 || id >= vars.activeVars.size) {
			return 0
		}
		return vars.getVarValueInt(id)
	}

	override fun varbit(id: Int): Int {
		val vars: PlayerVarDomain = PlayerVarDomain.playerVarProvider ?: return 0
		if (id < 0 || TypeLists.VARBIT == null || TypeLists.VARBIT!!.list(id) == null) {
			return 0
		}
		return vars.getVarBitValue(id)
	}

	override fun itemContainer(id: Int): ItemContainer? {
		return ClientItemContainer.getContainer(id, false)
	}

	override fun widget(interfaceId: Int, componentId: Int): Widget? {
		val interfaces: Array<Array<Component?>?> = Interface.interfaces ?: return null
		if (interfaceId < 0 || componentId < 0 || interfaceId >= interfaces.size) {
			return null
		}
		// a read: an interface the client has not loaded is not loaded here either
		val components: Array<Component?> = interfaces[interfaceId] ?: return null
		return if (componentId < components.size) components[componentId] else null
	}

	override fun itemName(id: Int): String? {
		if (id < 0 || TypeLists.OBJ == null) {
			return null
		}
		return TypeLists.OBJ!!.list(id).name
	}

	override fun npcName(id: Int): String? {
		if (id < 0 || TypeLists.NPC == null) {
			return null
		}
		return TypeLists.NPC!!.list(id)?.name
	}

	override fun locName(id: Int): String? {
		if (id < 0 || TypeLists.LOC == null) {
			return null
		}
		return TypeLists.LOC!!.list(id)?.name
	}

	override fun sceneBaseX(): Int {
		return MapRegion.sceneBaseX
	}

	override fun sceneBaseY(): Int {
		return MapRegion.sceneBaseY
	}

	override fun sceneSize(): Int {
		return MapRegion.sceneSizeX
	}

	override fun toolkitId(): Int {
		if (PluginRuntime.toolkitId >= 0) {
			return PluginRuntime.toolkitId
		}
		val preferences: GamePreferences = GamePreferences.uid ?: return -1
		return preferences.toolkit!!.getValue()
	}

	override fun projectPoint(sceneX: Int, sceneY: Int, level: Int, heightOffset: Int, out: IntArray): Boolean {
		if (ClientState.activeToolkit == null || out.size < 2) {
			return false
		}
		OverlayManager.projectToScreenWithOffset(level, heightOffset, 0, sceneX, 0, sceneY, 0)
		out[0] = OverlayManager.projectedScreenPosition[0]
		out[1] = OverlayManager.projectedScreenPosition[1]
		return out[0] >= 0
	}

	override fun projectTile(tileX: Int, tileY: Int, level: Int, heightOffset: Int, out: IntArray): Boolean {
		return projectPoint(tileX * 512 + 256, tileY * 512 + 256, level, heightOffset, out)
	}

	override fun projectActor(actor: Actor, heightOffset: Int, out: IntArray): Boolean {
		val entity: PathingEntity = actor as? PathingEntity ?: return false
		if (ClientState.activeToolkit == null || out.size < 2) {
			return false
		}
		OverlayManager.projectEntityToScreen(heightOffset, 0, 0, entity)
		out[0] = OverlayManager.projectedScreenPosition[0]
		out[1] = OverlayManager.projectedScreenPosition[1]
		return out[0] >= 0
	}

	override fun chatMessage(text: String) {
		if (ClientState.mainLogicStep !== MainLogicStep.STEP_GAME_SCREEN) {
			return
		}
		ChatLineWriter.writeUnfilterableMessage(text)
	}

	override fun consolePrint(text: String) {
		Console.printToConsole(text)
	}

	override fun addMenuEntry(label: String, clickType: Int) {
		PluginRuntime.addMenuEntry(label, clickType)
	}

	override fun item(id: Int): ItemDefinition? {
		if (id < 0 || TypeLists.OBJ == null) {
			return null
		}
		return TypeLists.OBJ!!.list(id)
	}

	override fun npcType(id: Int): NpcDefinition? {
		if (id < 0 || TypeLists.NPC == null) {
			return null
		}
		return TypeLists.NPC!!.list(id)
	}

	override fun locType(id: Int): LocDefinition? {
		if (id < 0 || TypeLists.LOC == null) {
			return null
		}
		return TypeLists.LOC!!.list(id)
	}

	override fun locsAt(tileX: Int, tileY: Int, level: Int, visitor: LocVisitor) {
		val levels: Array<Array<Array<Tile?>?>?> = Scene.aClass100ArrayArrayArray3 ?: return
		if (level < 0 || level >= levels.size) {
			return
		}
		val columns: Array<Array<Tile?>?> = levels[level] ?: return
		if (tileX < 0 || tileX >= columns.size) {
			return
		}
		val column: Array<Tile?> = columns[tileX] ?: return
		if (tileY < 0 || tileY >= column.size) {
			return
		}
		val tile: Tile = column[tileY] ?: return
		visitLoc(tile.primaryWall, LocKind.WALL, visitor)
		visitLoc(tile.secondaryWall, LocKind.WALL, visitor)
		visitLoc(tile.primaryWallDecoration, LocKind.WALL_DECORATION, visitor)
		visitLoc(tile.secondaryWallDecoration, LocKind.WALL_DECORATION, visitor)
		visitLoc(tile.groundDecoration, LocKind.GROUND_DECORATION, visitor)
		var container: InteractiveEntityContainer? = tile.interactives
		while (container != null) {
			// players, npcs, graphics and ground items share the list: only the locations are GameObjects
			visitLoc(container.interactiveObject, LocKind.INTERACTIVE, visitor)
			container = container.child
		}
	}

	private fun visitLoc(entity: Any?, kind: Int, visitor: LocVisitor) {
		val loc: GameObject = entity as? GameObject ?: return
		visitor.visit(loc.getId(), kind, loc.getRotation(), loc.getShape())
	}

	override fun collisionFlags(tileX: Int, tileY: Int, level: Int): Int {
		if (level < 0 || level >= MapRegion.aClass104Array3.size) {
			return 0
		}
		val map: CollisionMap = MapRegion.aClass104Array3[level] ?: return 0
		val flags: Array<IntArray?> = map.collisionFlags ?: return 0
		val x: Int = tileX - map.xOffset
		val z: Int = tileY - map.zOffset
		if (x < 0 || z < 0 || x >= flags.size) {
			return 0
		}
		val column: IntArray = flags[x] ?: return 0
		return if (z < column.size) column[z] else 0
	}
}
