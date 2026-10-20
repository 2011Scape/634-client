package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.ChatLineWriter
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.OverlayManager
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.ObjStack
import com.jagex.game.runetek5.cache.node.HashTableIterator
import com.jagex.game.runetek5.cache.node.ObjStackEntry
import com.jagex.game.runetek5.core.datastruct.key.DequeIterator
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
import com.jagex.game.runetek5.AbstractMouseManager
import com.jagex.game.runetek5.FriendsChatPlayer
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.Minimap
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.camera.CameraMode
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder
import com.jagex.game.runetek5.client.input.mouse.MouseRecord
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.interaction.FriendList
import com.jagex.game.runetek5.interaction.IgnoreList
import com.jagex.game.runetek5.interaction.chat.friendschat.FriendsChat
import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.WorldConnectionInfo
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer
import com.jagex.game.runetek5.config.rsinterface.SubInterface
import com.jagex.game.runetek5.plugin.api.InterfaceVisitor
import com.jagex.game.runetek5.plugin.api.ItemContainer
import com.jagex.game.runetek5.plugin.api.MenuEntry
import com.jagex.game.runetek5.plugin.api.NameVisitor
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

	override fun npcByIndex(index: Int): Npc? {
		if (index < 0) {
			return null
		}
		val node: NPCEntityNode = NPCList.NPC_MAP.get(index.toLong()) as NPCEntityNode? ?: return null
		return node.npc
	}

	override fun playerByIndex(index: Int): Player? {
		return if (index >= 0 && index < PlayerUpdate.players.size) PlayerUpdate.players[index] else null
	}

	override fun groundItems(visitor: GroundItemVisitor) {
		// cursors of their own: the client walks these lists with their shared cursors, and a plugin may ask from inside such a walk
		val piles: HashTableIterator = HashTableIterator(ObjStack.GROUND_ITEMS)
		var node: Node? = piles.first()
		while (node != null) {
			// the key packs level << 28 | absolute y << 14 | absolute x (ObjStack.addGroundItem)
			val key: Long = node.key
			val level: Int = (key ushr 28 and 0x3L).toInt()
			val sceneX: Int = (key and 0x3FFFL).toInt() - MapRegion.sceneBaseX
			val sceneY: Int = (key shr 14 and 0x3FFFL).toInt() - MapRegion.sceneBaseY
			val pile: ObjStack = node as ObjStack
			val entries: DequeIterator = DequeIterator(pile.groundItems)
			var entry: ObjStackEntry? = entries.first() as ObjStackEntry?
			while (entry != null) {
				visitor.visit(sceneX, sceneY, level, entry.id, entry.count)
				entry = entries.next() as ObjStackEntry?
			}
			node = piles.next()
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
		// a read: an interface the client has not loaded is not loaded here either
		val components: Array<Component?> = Interface.peekComponents(interfaceId) ?: return null
		return if (componentId >= 0 && componentId < components.size) components[componentId] else null
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

	private val corner: IntArray = IntArray(2)

	override fun tilePolygon(tileX: Int, tileY: Int, level: Int, size: Int, xs: IntArray, ys: IntArray): Boolean {
		if (xs.size < 4 || ys.size < 4 || size < 1) {
			return false
		}
		// south-west, north-west, north-east, south-east: each corner is one projection, with its camera set
		for (i in 0 until 4) {
			val east: Int = if (i >= 2) size else 0
			val north: Int = if (i == 1 || i == 2) size else 0
			if (!projectPoint((tileX + east) * 512, (tileY + north) * 512, level, 0, corner)) {
				return false
			}
			xs[i] = corner[0]
			ys[i] = corner[1]
		}
		return true
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

	override fun widgetById(id: Int): Widget? {
		return if (id < 0) null else widget(id ushr 16, id and 0xFFFF)
	}

	override fun topInterface(): Int {
		return ClientState.BASE_WINDOW_ID
	}

	override fun openInterfaces(visitor: InterfaceVisitor) {
		val open: HashTableIterator = HashTableIterator(InterfaceManager.OPEN_INTERFACES)
		var sub: SubInterface? = open.first() as SubInterface?
		while (sub != null) {
			visitor.visit(sub.interfaceId, sub.key.toInt(), sub.openType?.id ?: -1)
			sub = open.next() as SubInterface?
		}
	}

	override fun menuEntryCount(): Int {
		return PluginRuntime.menuEntryCount()
	}

	override fun menuEntry(i: Int): MenuEntry? {
		return PluginRuntime.menuEntry(i)
	}

	override fun removeMenuEntry(i: Int): Boolean {
		return PluginRuntime.removeMenuEntry(i)
	}

	override fun promoteMenuEntry(i: Int): Boolean {
		return PluginRuntime.promoteMenuEntry(i)
	}

	override fun renameMenuEntry(i: Int, option: String): Boolean {
		return PluginRuntime.renameMenuEntry(i, option)
	}

	override fun mouseX(): Int {
		val mouse: AbstractMouseManager = MouseRecord.mouseRecorder ?: return -1
		return mouse.getMouseX()
	}

	override fun mouseY(): Int {
		val mouse: AbstractMouseManager = MouseRecord.mouseRecorder ?: return -1
		return mouse.getMouseY()
	}

	override fun isMouseButtonDown(button: Int): Boolean {
		val mouse: AbstractMouseManager = MouseRecord.mouseRecorder ?: return false
		return when (button) {
			0 -> mouse.isLeftButtonDown()
			1 -> mouse.isMiddleButtonDown()
			2 -> mouse.isRightButtonDown()
			else -> false
		}
	}

	override fun isKeyHeld(keyCode: Int): Boolean {
		val keys: KeyRecorder = KeyRecorder.keyRecorder ?: return false
		return keyCode >= 0 && keys.isPressed(keyCode)
	}

	override fun hoveredTileX(): Int {
		return PluginRuntime.hoveredTileX
	}

	override fun hoveredTileY(): Int {
		return PluginRuntime.hoveredTileY
	}

	override fun cameraYaw(): Int {
		return Camera.camAngleY.toInt() and 0x3FFF
	}

	override fun cameraPitch(): Int {
		return Camera.camAngleX.toInt()
	}

	override fun cameraX(): Int {
		return Camera.cameraPositionAbsoluteX
	}

	override fun cameraZ(): Int {
		// the client's "y" of the camera is the north-south axis
		return Camera.cameraPositionAbsoluteY
	}

	override fun cameraHeight(): Int {
		return Camera.cameraPositionAbsoluteZ
	}

	private fun minimapShown(): Boolean {
		// the overlays draw in the scene pass, before this frame's interfaces: the previous frame's minimap is the one to go by
		return SceneDrawer.timesSceneRendered - PluginRuntime.minimapFrame <= 2 && PluginRuntime.minimapWidth > 0
	}

	override fun minimapArea(out: IntArray): Boolean {
		if (!minimapShown() || out.size < 4) {
			return false
		}
		out[0] = PluginRuntime.minimapX
		out[1] = PluginRuntime.minimapY
		out[2] = PluginRuntime.minimapWidth
		out[3] = PluginRuntime.minimapHeight
		return true
	}

	override fun projectMinimap(tileX: Int, tileY: Int, out: IntArray): Boolean {
		if (!minimapShown() || out.size < 2) {
			return false
		}
		// the centre as Minimap.drawMinimap takes it, then the dot's place as drawMapDot computes it
		val centreX: Int
		val centreZ: Int
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			centreX = Camera.cameraTargetAbsX
			centreZ = Camera.cameraTargetAbsZ
		} else {
			val player: PathingEntity = PlayerUpdate.localPlayer ?: return false
			centreX = player.x
			centreZ = player.z
		}
		val width: Int = PluginRuntime.minimapWidth
		val height: Int = PluginRuntime.minimapHeight
		if (!Minimap.projectDot(tileX * 4 + 2 - centreX / 128, tileY * 4 + 2 - centreZ / 128, width, height, out)) {
			return false
		}
		out[0] = out[0] + width / 2 + PluginRuntime.minimapX
		out[1] = PluginRuntime.minimapY + height / 2 - out[1]
		return true
	}

	override fun runEnergy(): Int {
		return ClientState.runEnergy
	}

	override fun weight(): Int {
		return client.runWeight
	}

	override fun worldId(): Int {
		if (ClientState.mainLogicStep !== MainLogicStep.STEP_GAME_SCREEN) {
			return -1 // the world is chosen on the login screen, long before the game
		}
		val request: WorldConnectionInfo = TcpPacket.CURRENT_REQUEST ?: return -1
		return request.worldId
	}

	override fun regionId(): Int {
		val player: PathingEntity = PlayerUpdate.localPlayer ?: return -1
		val absoluteX: Int = (player.x shr 9) + MapRegion.sceneBaseX
		val absoluteY: Int = (player.z shr 9) + MapRegion.sceneBaseY
		return ((absoluteX shr 6) shl 8) + (absoluteY shr 6)
	}

	override fun isMember(): Boolean {
		return LoginManager.isMember
	}

	override fun playSound(id: Int, volume: Int) {
		if (id < 0) {
			return
		}
		val level: Int = if (volume < 0) 0 else if (volume > 255) 255 else volume
		AreaSoundPlayer.playSoundSynth(level, id, 1, 0, 256)
	}

	override fun friends(visitor: NameVisitor) {
		for (i in 0 until FriendList.FRIEND_COUNT) {
			visitor.visit(FriendList.friendNames[i], FriendList.friendWorldIds[i], FriendList.friendRanks[i])
		}
	}

	override fun ignores(visitor: NameVisitor) {
		for (i in 0 until IgnoreList.ignoreCount) {
			visitor.visit(IgnoreList.ignoreNames[i], 0, -1)
		}
	}

	override fun clanMembers(visitor: NameVisitor) {
		val members: Array<FriendsChatPlayer?> = FriendsChat.clanChatUsers ?: return
		for (i in 0 until FriendsChat.clanChatUserCount) {
			val member: FriendsChatPlayer = members[i] ?: continue
			visitor.visit(member.displayName ?: member.username, member.worldId, member.rank.toInt())
		}
	}
}
