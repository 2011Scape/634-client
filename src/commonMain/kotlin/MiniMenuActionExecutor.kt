import com.jagex.game.runetek5.plugin.PluginRuntime
import com.jagex.game.runetek5.core.constants.MiniMenuAction
import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.client.menu.MiniMenuEntry
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.NPCEntityNode
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.ResumePauseButtonSender
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.IFEvents
import com.jagex.game.runetek5.config.npc.NPCType
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.menu.MenuInteraction
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.client.input.keyboard.KeyRecorder
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.ClientPacket
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.PathFinder
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.Minimap
import com.jagex.game.runetek5.vardomain.PlayerVarDomain
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import kotlin.jvm.JvmStatic

object MiniMenuActionExecutor {

	@OriginalMember(owner = "client!daa", name = "a", descriptor = "(BLclient!db;II)V")
	@JvmStatic
	fun processMenuClick(@OriginalArg(1) miniMenuEntryInner: MiniMenuEntry?, @OriginalArg(2) mouseY: Int, @OriginalArg(3) mouseX: Int) {
		if (miniMenuEntryInner == null || miniMenuEntryInner === MiniMenu.entryList.sentinel) {
			return
		}
		@Pc(15) val param0: Int = miniMenuEntryInner.param0
		@Pc(18) val param1: Int = miniMenuEntryInner.param1
		@Pc(21) var clickType: Int = miniMenuEntryInner.clickType
		@Pc(25) val uid: Int = miniMenuEntryInner.entityUid.toInt()
		if (clickType >= 2000) {
			clickType -= 2000
		}
		@Pc(32) val entityUid: Long = miniMenuEntryInner.entityUid
		@Pc(53) var packet: TcpPacket?
		// Not part of the original client: the plugins hear of the click, and a plugin's own entry is theirs (PluginRuntime).
		if (PluginRuntime.menuClicked(miniMenuEntryInner, clickType)) {
			return
		}
		if (clickType == MiniMenuAction.WALK) {
			if (LoginManager.staffModLevel > 0 && KeyRecorder.keyRecorder!!.isPressed(82) && KeyRecorder.keyRecorder!!.isPressed(81)) {
				StaffTeleportCommand.executeTeleportCommand(param1 + MapRegion.sceneBaseY, PlayerUpdate.localPlayer!!.level.toInt(), param0 + MapRegion.sceneBaseX)
			} else {
				packet = Minimap.createMovePacket(param1, param0, uid)
				if (uid == 1) {
					packet!!.buffer!!.writeByte(-1)
					packet!!.buffer!!.writeByte(-1)
					packet!!.buffer!!.writeShort(Camera.camAngleY.toInt())
					packet!!.buffer!!.writeByte(57)
					packet!!.buffer!!.writeByte(Minimap.minimapRotation)
					packet!!.buffer!!.writeByte(Minimap.minimapZoom)
					packet!!.buffer!!.writeByte(89)
					packet!!.buffer!!.writeShort(PlayerUpdate.localPlayer!!.x)
					packet!!.buffer!!.writeShort(PlayerUpdate.localPlayer!!.z)
					packet!!.buffer!!.writeByte(63)
				} else {
					StaticMedia.crossY = mouseY
					StaticMedia.crossX = mouseX
					StaticMedia.crossSpritePtr = 0
					StaticMedia.crossType = 1
				}
				BufferedConnectionContext.queuePacket(packet)
				PathFinder.calculateRoute(true, param0, param1, 1, 0, 1, -4, 0)
			}
		}
		if (clickType == MiniMenuAction.OPLOC2) {
			StaticMedia.crossY = mouseY
			StaticMedia.crossType = 2
			StaticMedia.crossX = mouseX
			StaticMedia.crossSpritePtr = 0
			packet = TcpPacket.createPacket(ClientProt.OPLOC2, TcpPacket.isaac)
			packet!!.buffer!!.writeShortAddLittle(MapRegion.sceneBaseY + param1)
			packet!!.buffer!!.writeShortAdd(param0 + MapRegion.sceneBaseX)
			packet!!.buffer!!.writeByteSubtract(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			packet!!.buffer!!.writeShortAddLittle(Int.MAX_VALUE and (entityUid ushr 32).toInt())
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.walkToLoc(param1, entityUid, param0)
		}
		@Pc(236) var npcEntity: NPCEntity?
		@Pc(231) var npcEntityNode: NPCEntityNode?
		if (clickType == MiniMenuAction.OPNPC6) {
			StaticMedia.crossType = 2
			StaticMedia.crossY = mouseY
			StaticMedia.crossX = mouseX
			StaticMedia.crossSpritePtr = 0
			npcEntityNode = NPCList.NPC_MAP.get(uid.toLong()) as NPCEntityNode?
			if (npcEntityNode != null) {
				npcEntity = npcEntityNode.npc
				@Pc(239) var npcType: NPCType? = npcEntity!!.npcType
				if (npcType!!.alternativeIds != null) {
					npcType = npcType.getMultiNpc(PlayerVarDomain.playerVarProvider)
				}
				if (npcType != null) {
					@Pc(258) val examinePacket: TcpPacket? = TcpPacket.createPacket(ClientProt.OPNPC6, TcpPacket.isaac)
					examinePacket!!.buffer!!.writeShort(npcType.id)
					BufferedConnectionContext.queuePacket(examinePacket)
				}
			}
		}
		@Pc(276) var playerEntity: PlayerEntity?
		@Pc(295) var playerPacket: TcpPacket?
		if (clickType == MiniMenuAction.OPPLAYER5) {
			playerEntity = PlayerUpdate.players[uid]
			if (playerEntity != null) {
				StaticMedia.crossSpritePtr = 0
				StaticMedia.crossY = mouseY
				StaticMedia.crossX = mouseX
				StaticMedia.crossType = 2
				playerPacket = TcpPacket.createPacket(ClientProt.OPPLAYER5, TcpPacket.isaac)
				playerPacket!!.buffer!!.writeShortAdd(uid)
				playerPacket!!.buffer!!.writeByte(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				BufferedConnectionContext.queuePacket(playerPacket)
				PathFinder.calculateRoute(true, playerEntity.movementBaseX[0], playerEntity.movementBaseY[0], playerEntity.getSize(), 0, playerEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.TGT_OBJ) {
			StaticMedia.crossX = mouseX
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossType = 2
			StaticMedia.crossY = mouseY
			packet = TcpPacket.createPacket(ClientProt.OPOBJT, TcpPacket.isaac)
			packet!!.buffer!!.writeShortLittle(param0 + MapRegion.sceneBaseX)
			packet!!.buffer!!.writeShortAdd(uid)
			packet!!.buffer!!.writeShortLittle(MiniMenu.slotId)
			packet!!.buffer!!.writeShort(MapRegion.sceneBaseY + param1)
			packet!!.buffer!!.writeByte(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			packet!!.buffer!!.writeShortAdd(MiniMenu.selectedItemId)
			packet!!.buffer!!.writeIntMiddle(MiniMenuBuilder.USE_OPTIONS_FLAGS)
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.routeToGroundItem(param1, param0)
		}
		@Pc(419) var component: Component?
		if (clickType == MiniMenuAction.TGT_BUTTON) {
			component = Component.getComponentWithSlot(param0, param1)
			if (component != null) {
				InterfaceOnInterfaceAction.sendInterfaceOnInterface(component)
			}
		}
		if (clickType == MiniMenuAction.OPOBJ5) {
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossX = mouseX
			StaticMedia.crossType = 2
			StaticMedia.crossY = mouseY
			packet = TcpPacket.createPacket(ClientProt.OPOBJ5, TcpPacket.isaac)
			packet!!.buffer!!.writeShort(MapRegion.sceneBaseY + param1)
			packet!!.buffer!!.writeShortAdd(param0 + MapRegion.sceneBaseX)
			packet!!.buffer!!.writeByteInverse(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			packet!!.buffer!!.writeShortAdd(uid)
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.routeToGroundItem(param1, param0)
		}
		@Pc(516) var npcPacket: TcpPacket?
		if (clickType == MiniMenuAction.OPNPC4) {
			npcEntityNode = NPCList.NPC_MAP.get(uid.toLong()) as NPCEntityNode?
			if (npcEntityNode != null) {
				StaticMedia.crossSpritePtr = 0
				StaticMedia.crossX = mouseX
				StaticMedia.crossType = 2
				StaticMedia.crossY = mouseY
				npcEntity = npcEntityNode.npc
				npcPacket = TcpPacket.createPacket(ClientProt.OPNPC4, TcpPacket.isaac)
				npcPacket!!.buffer!!.writeShortLittle(uid)
				npcPacket!!.buffer!!.writeByteAdd(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				BufferedConnectionContext.queuePacket(npcPacket)
				PathFinder.calculateRoute(true, npcEntity!!.movementBaseX[0], npcEntity.movementBaseY[0], npcEntity.getSize(), 0, npcEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.OPPLAYER1) {
			playerEntity = PlayerUpdate.players[uid]
			if (playerEntity != null) {
				StaticMedia.crossSpritePtr = 0
				StaticMedia.crossY = mouseY
				StaticMedia.crossType = 2
				StaticMedia.crossX = mouseX
				playerPacket = TcpPacket.createPacket(ClientProt.OPPLAYER1, TcpPacket.isaac)
				playerPacket!!.buffer!!.writeShortAddLittle(uid)
				playerPacket!!.buffer!!.writeByteInverse(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				BufferedConnectionContext.queuePacket(playerPacket)
				PathFinder.calculateRoute(true, playerEntity.movementBaseX[0], playerEntity.movementBaseY[0], playerEntity.getSize(), 0, playerEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.OPLOC4) {
			StaticMedia.crossX = mouseX
			StaticMedia.crossY = mouseY
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossType = 2
			packet = TcpPacket.createPacket(ClientProt.OPLOC4, TcpPacket.isaac)
			packet!!.buffer!!.writeByteAdd(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			packet!!.buffer!!.writeShortAdd((entityUid ushr 32).toInt() and Int.MAX_VALUE)
			packet!!.buffer!!.writeShortAdd(MapRegion.sceneBaseX + param0)
			packet!!.buffer!!.writeShortLittle(MapRegion.sceneBaseY + param1)
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.walkToLoc(param1, entityUid, param0)
		}
		if (clickType == MiniMenuAction.TGT_NPC) {
			npcEntityNode = NPCList.NPC_MAP.get(uid.toLong()) as NPCEntityNode?
			if (npcEntityNode != null) {
				StaticMedia.crossSpritePtr = 0
				StaticMedia.crossY = mouseY
				StaticMedia.crossX = mouseX
				npcEntity = npcEntityNode.npc
				StaticMedia.crossType = 2
				npcPacket = TcpPacket.createPacket(ClientProt.OPNPCT, TcpPacket.isaac)
				npcPacket!!.buffer!!.writeShortAddLittle(MiniMenu.slotId)
				npcPacket!!.buffer!!.writeInt(MiniMenuBuilder.USE_OPTIONS_FLAGS)
				npcPacket!!.buffer!!.writeShortLittle(uid)
				npcPacket!!.buffer!!.writeByteAdd(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				npcPacket!!.buffer!!.writeShortAdd(MiniMenu.selectedItemId)
				BufferedConnectionContext.queuePacket(npcPacket)
				PathFinder.calculateRoute(true, npcEntity!!.movementBaseX[0], npcEntity.movementBaseY[0], npcEntity.getSize(), 0, npcEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.FACE_SQUARE) {
			if (LoginManager.staffModLevel > 0 && KeyRecorder.keyRecorder!!.isPressed(82) && KeyRecorder.keyRecorder!!.isPressed(81)) {
				StaffTeleportCommand.executeTeleportCommand(param1 + MapRegion.sceneBaseY, PlayerUpdate.localPlayer!!.level.toInt(), MapRegion.sceneBaseX + param0)
			} else {
				StaticMedia.crossY = mouseY
				StaticMedia.crossX = mouseX
				StaticMedia.crossSpritePtr = 0
				StaticMedia.crossType = 1
				packet = TcpPacket.createPacket(ClientProt.aClass94_89, TcpPacket.isaac)
				packet!!.buffer!!.writeShortAdd(MapRegion.sceneBaseX + param0)
				packet!!.buffer!!.writeShort(MapRegion.sceneBaseY + param1)
				BufferedConnectionContext.queuePacket(packet)
			}
		}
		if (clickType == MiniMenuAction.OPLOC6) {
			StaticMedia.crossY = mouseY
			StaticMedia.crossType = 2
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossX = mouseX
			packet = TcpPacket.createPacket(ClientProt.OPLOC6, TcpPacket.isaac)
			packet!!.buffer!!.writeShort(uid)
			BufferedConnectionContext.queuePacket(packet)
		}
		if (clickType == MiniMenuAction.TGT_PLAYER) {
			playerEntity = PlayerUpdate.players[uid]
			if (playerEntity != null) {
				StaticMedia.crossX = mouseX
				StaticMedia.crossType = 2
				StaticMedia.crossSpritePtr = 0
				StaticMedia.crossY = mouseY
				playerPacket = TcpPacket.createPacket(ClientProt.OPPLAYERT, TcpPacket.isaac)
				playerPacket!!.buffer!!.writeShortAddLittle(MiniMenu.slotId)
				playerPacket!!.buffer!!.writeShortLittle(uid)
				playerPacket!!.buffer!!.writeShortLittle(MiniMenu.selectedItemId)
				playerPacket!!.buffer!!.writeIntInverseMiddle(MiniMenuBuilder.USE_OPTIONS_FLAGS)
				playerPacket!!.buffer!!.writeByteInverse(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				BufferedConnectionContext.queuePacket(playerPacket)
				PathFinder.calculateRoute(true, playerEntity.movementBaseX[0], playerEntity.movementBaseY[0], playerEntity.getSize(), 0, playerEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.OPPLAYER3) {
			playerEntity = PlayerUpdate.players[uid]
			if (playerEntity != null) {
				StaticMedia.crossSpritePtr = 0
				StaticMedia.crossY = mouseY
				StaticMedia.crossType = 2
				StaticMedia.crossX = mouseX
				playerPacket = TcpPacket.createPacket(ClientProt.OPPLAYER3, TcpPacket.isaac)
				playerPacket!!.buffer!!.writeByteSubtract(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				playerPacket!!.buffer!!.writeShortLittle(uid)
				BufferedConnectionContext.queuePacket(playerPacket)
				PathFinder.calculateRoute(true, playerEntity.movementBaseX[0], playerEntity.movementBaseY[0], playerEntity.getSize(), 0, playerEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.OPLOC5) {
			StaticMedia.crossType = 2
			StaticMedia.crossX = mouseX
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossY = mouseY
			packet = TcpPacket.createPacket(ClientProt.OPLOC5, TcpPacket.isaac)
			packet!!.buffer!!.writeShortLittle(MapRegion.sceneBaseY + param1)
			packet!!.buffer!!.writeByteAdd(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			packet!!.buffer!!.writeShortAddLittle(MapRegion.sceneBaseX + param0)
			packet!!.buffer!!.writeShortAdd(Int.MAX_VALUE and (entityUid ushr 32).toInt())
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.walkToLoc(param1, entityUid, param0)
		}
		if (clickType == MiniMenuAction.OPPLAYER4) {
			playerEntity = PlayerUpdate.players[uid]
			if (playerEntity != null) {
				StaticMedia.crossType = 2
				StaticMedia.crossSpritePtr = 0
				StaticMedia.crossX = mouseX
				StaticMedia.crossY = mouseY
				playerPacket = TcpPacket.createPacket(ClientProt.OPPLAYER4, TcpPacket.isaac)
				playerPacket!!.buffer!!.writeShort(uid)
				playerPacket!!.buffer!!.writeByteAdd(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				BufferedConnectionContext.queuePacket(playerPacket)
				PathFinder.calculateRoute(true, playerEntity.movementBaseX[0], playerEntity.movementBaseY[0], playerEntity.getSize(), 0, playerEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.PAUSE_BUTTON && InterfaceManager.activeDialogueContinue == null) {
			ResumePauseButtonSender.continueInterfacePressed(param1, param0)
			InterfaceManager.activeDialogueContinue = Component.getComponentWithSlot(param0, param1)
			Component.redrawComponent(InterfaceManager.activeDialogueContinue)
		}
		if (clickType == MiniMenuAction.OPPLAYER6) {
			playerEntity = PlayerUpdate.players[uid]
			if (playerEntity != null) {
				StaticMedia.crossY = mouseY
				StaticMedia.crossX = mouseX
				StaticMedia.crossType = 2
				StaticMedia.crossSpritePtr = 0
				playerPacket = TcpPacket.createPacket(ClientProt.OPPLAYER6, TcpPacket.isaac)
				playerPacket!!.buffer!!.writeByte(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				playerPacket!!.buffer!!.writeShortAdd(uid)
				BufferedConnectionContext.queuePacket(playerPacket)
				PathFinder.calculateRoute(true, playerEntity.movementBaseX[0], playerEntity.movementBaseY[0], playerEntity.getSize(), 0, playerEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.OPOBJ1) {
			StaticMedia.crossY = mouseY
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossType = 2
			StaticMedia.crossX = mouseX
			packet = TcpPacket.createPacket(ClientProt.OPOBJ1, TcpPacket.isaac)
			packet!!.buffer!!.writeShort(uid)
			packet!!.buffer!!.writeShort(MapRegion.sceneBaseX + param0)
			packet!!.buffer!!.writeShort(param1 + MapRegion.sceneBaseY)
			packet!!.buffer!!.writeByteSubtract(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.routeToGroundItem(param1, param0)
		}
		if (clickType == MiniMenuAction.TGT_GROUND) {
			StaticMedia.crossType = 1
			StaticMedia.crossX = mouseX
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossY = mouseY
			packet = TcpPacket.createPacket(ClientProt.APCOORDT, TcpPacket.isaac)
			packet!!.buffer!!.writeShortLittle(MapRegion.sceneBaseX + param0)
			packet!!.buffer!!.writeShortAdd(MiniMenu.selectedItemId)
			packet!!.buffer!!.writeShortLittle(MiniMenu.slotId)
			packet!!.buffer!!.writeInt(MiniMenuBuilder.USE_OPTIONS_FLAGS)
			packet!!.buffer!!.writeShortAdd(MapRegion.sceneBaseY + param1)
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.calculateRoute(true, param0, param1, 1, 0, 1, -4, 0)
		}
		if (clickType == MiniMenuAction.OPNPC5) {
			npcEntityNode = NPCList.NPC_MAP.get(uid.toLong()) as NPCEntityNode?
			if (npcEntityNode != null) {
				StaticMedia.crossSpritePtr = 0
				StaticMedia.crossX = mouseX
				StaticMedia.crossY = mouseY
				npcEntity = npcEntityNode.npc
				StaticMedia.crossType = 2
				npcPacket = TcpPacket.createPacket(ClientProt.OPNPC5, TcpPacket.isaac)
				npcPacket!!.buffer!!.writeShort(uid)
				npcPacket!!.buffer!!.writeByteInverse(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				BufferedConnectionContext.queuePacket(npcPacket)
				PathFinder.calculateRoute(true, npcEntity!!.movementBaseX[0], npcEntity.movementBaseY[0], npcEntity.getSize(), 0, npcEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.TGT_LOC) {
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossType = 2
			StaticMedia.crossX = mouseX
			StaticMedia.crossY = mouseY
			packet = TcpPacket.createPacket(ClientProt.OPLOCT, TcpPacket.isaac)
			packet!!.buffer!!.writeShort(MiniMenu.selectedItemId)
			packet!!.buffer!!.writeShortAddLittle(MapRegion.sceneBaseX + param0)
			packet!!.buffer!!.writeIntLittle(MiniMenuBuilder.USE_OPTIONS_FLAGS)
			packet!!.buffer!!.writeShortAdd(MapRegion.sceneBaseY + param1)
			packet!!.buffer!!.writeByteSubtract(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			packet!!.buffer!!.writeShortLittle(MiniMenu.slotId)
			packet!!.buffer!!.writeShortLittle((entityUid ushr 32).toInt() and Int.MAX_VALUE)
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.walkToLoc(param1, entityUid, param0)
		}
		if (clickType == MiniMenuAction.OPPLAYER8) {
			playerEntity = PlayerUpdate.players[uid]
			if (playerEntity != null) {
				StaticMedia.crossSpritePtr = 0
				StaticMedia.crossY = mouseY
				StaticMedia.crossX = mouseX
				StaticMedia.crossType = 2
				playerPacket = TcpPacket.createPacket(ClientProt.OPPLAYER8, TcpPacket.isaac)
				playerPacket!!.buffer!!.writeByte(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				playerPacket!!.buffer!!.writeShortAdd(uid)
				BufferedConnectionContext.queuePacket(playerPacket)
				PathFinder.calculateRoute(true, playerEntity.movementBaseX[0], playerEntity.movementBaseY[0], playerEntity.getSize(), 0, playerEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.OPPLAYER2) {
			playerEntity = PlayerUpdate.players[uid]
			if (playerEntity != null) {
				StaticMedia.crossType = 2
				StaticMedia.crossY = mouseY
				StaticMedia.crossX = mouseX
				StaticMedia.crossSpritePtr = 0
				playerPacket = TcpPacket.createPacket(ClientProt.OPPLAYER2, TcpPacket.isaac)
				playerPacket!!.buffer!!.writeByte(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				playerPacket!!.buffer!!.writeShort(uid)
				BufferedConnectionContext.queuePacket(playerPacket)
				PathFinder.calculateRoute(true, playerEntity.movementBaseX[0], playerEntity.movementBaseY[0], playerEntity.getSize(), 0, playerEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.OPOBJ3) {
			StaticMedia.crossX = mouseX
			StaticMedia.crossType = 2
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossY = mouseY
			packet = TcpPacket.createPacket(ClientProt.OPOBJ3, TcpPacket.isaac)
			packet!!.buffer!!.writeShort(uid)
			packet!!.buffer!!.writeShortAdd(param0 + MapRegion.sceneBaseX)
			packet!!.buffer!!.writeByte(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			packet!!.buffer!!.writeShortAddLittle(param1 + MapRegion.sceneBaseY)
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.routeToGroundItem(param1, param0)
		}
		if (clickType == MiniMenuAction.OPLOC1) {
			StaticMedia.crossX = mouseX
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossType = 2
			StaticMedia.crossY = mouseY
			packet = TcpPacket.createPacket(ClientProt.OPLOC1, TcpPacket.isaac)
			packet!!.buffer!!.writeByteSubtract(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			packet!!.buffer!!.writeShortAddLittle(MapRegion.sceneBaseX + param0)
			packet!!.buffer!!.writeShortLittle(param1 + MapRegion.sceneBaseY)
			packet!!.buffer!!.writeShort(Int.MAX_VALUE and (entityUid ushr 32).toInt())
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.walkToLoc(param1, entityUid, param0)
		}
		if (clickType == MiniMenuAction.OPNPC2) {
			npcEntityNode = NPCList.NPC_MAP.get(uid.toLong()) as NPCEntityNode?
			if (npcEntityNode != null) {
				StaticMedia.crossType = 2
				StaticMedia.crossX = mouseX
				StaticMedia.crossY = mouseY
				npcEntity = npcEntityNode.npc
				StaticMedia.crossSpritePtr = 0
				npcPacket = TcpPacket.createPacket(ClientProt.OPNPC2, TcpPacket.isaac)
				npcPacket!!.buffer!!.writeShortAddLittle(uid)
				npcPacket!!.buffer!!.writeByteAdd(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				BufferedConnectionContext.queuePacket(npcPacket)
				PathFinder.calculateRoute(true, npcEntity!!.movementBaseX[0], npcEntity.movementBaseY[0], npcEntity.getSize(), 0, npcEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.OPOBJ6) {
			StaticMedia.crossType = 2
			StaticMedia.crossY = mouseY
			StaticMedia.crossX = mouseX
			StaticMedia.crossSpritePtr = 0
			packet = TcpPacket.createPacket(ClientProt.OPOBJ6, TcpPacket.isaac)
			packet!!.buffer!!.writeShort(uid)
			BufferedConnectionContext.queuePacket(packet)
		}
		if (clickType == MiniMenuAction.OPOBJ4) {
			StaticMedia.crossType = 2
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossY = mouseY
			StaticMedia.crossX = mouseX
			packet = TcpPacket.createPacket(ClientProt.OPOBJ4, TcpPacket.isaac)
			packet!!.buffer!!.writeByteSubtract(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			packet!!.buffer!!.writeShortAdd(MapRegion.sceneBaseX + param0)
			packet!!.buffer!!.writeShortLittle(MapRegion.sceneBaseY + param1)
			packet!!.buffer!!.writeShort(uid)
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.routeToGroundItem(param1, param0)
		}
		if (clickType == MiniMenuAction.TGT_SELF) {
			StaticMedia.crossX = mouseX
			StaticMedia.crossType = 2
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossY = mouseY
			packet = TcpPacket.createPacket(ClientProt.OPPLAYERT, TcpPacket.isaac)
			packet!!.buffer!!.writeShortAddLittle(MiniMenu.slotId)
			packet!!.buffer!!.writeShortLittle(PlayerUpdate.localPlayer!!.playerIndex)
			packet!!.buffer!!.writeShortLittle(MiniMenu.selectedItemId)
			packet!!.buffer!!.writeIntInverseMiddle(MiniMenuBuilder.USE_OPTIONS_FLAGS)
			packet!!.buffer!!.writeByteInverse(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			BufferedConnectionContext.queuePacket(packet)
		}
		if (clickType == MiniMenuAction.IF_BUTTONX1 || clickType == MiniMenuAction.IF_BUTTONX2) {
			InterfaceOptionInteract.ifOptionInteract(param0, miniMenuEntryInner.op, uid, param1)
		}
		if (clickType == MiniMenuAction.IF_BUTTONT) {
			component = Component.getComponentWithSlot(param0, param1)
			if (component != null) {
				Static657.cancelInterfaceSelection()
				@Pc(2049) val serverActiveProperties: IFEvents? = InterfaceManager.getComponentSettings(component)
				InterfaceSelectionStart.startInterfaceSelection(serverActiveProperties!!.interfaceId, serverActiveProperties.getUseOptionFlags(), component)
				MiniMenu.useOnType = Component.getUseOptionString(component)
				MiniMenu.useOnTarget = component.opBase + "<col=ffffff>"
				if (MiniMenu.useOnType == null) {
					MiniMenu.useOnType = "Null"
				}
			}
			return
		}
		if (clickType == MiniMenuAction.OP_MAPELEMENT1 || clickType == MiniMenuAction.OP_MAPELEMENT2 || clickType == MiniMenuAction.OP_MAPELEMENT3 || clickType == MiniMenuAction.OP_MAPELEMENT4 || clickType == MiniMenuAction.OP_MAPELEMENT5) {
			MiniMenuTriggerDispatch.onWorldmapMinimenuClick(clickType, param0, uid)
		}
		if (clickType == MiniMenuAction.OPNPC1) {
			npcEntityNode = NPCList.NPC_MAP.get(uid.toLong()) as NPCEntityNode?
			if (npcEntityNode != null) {
				StaticMedia.crossType = 2
				StaticMedia.crossY = mouseY
				npcEntity = npcEntityNode.npc
				StaticMedia.crossX = mouseX
				StaticMedia.crossSpritePtr = 0
				npcPacket = TcpPacket.createPacket(ClientProt.OPNPC1, TcpPacket.isaac)
				npcPacket!!.buffer!!.writeByte(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				npcPacket!!.buffer!!.writeShortLittle(uid)
				BufferedConnectionContext.queuePacket(npcPacket)
				PathFinder.calculateRoute(true, npcEntity!!.movementBaseX[0], npcEntity.movementBaseY[0], npcEntity.getSize(), 0, npcEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.OPPLAYER7) {
			playerEntity = PlayerUpdate.players[uid]
			if (playerEntity != null) {
				StaticMedia.crossType = 2
				StaticMedia.crossY = mouseY
				StaticMedia.crossSpritePtr = 0
				StaticMedia.crossX = mouseX
				playerPacket = TcpPacket.createPacket(ClientProt.OPPLAYER7, TcpPacket.isaac)
				playerPacket!!.buffer!!.writeShortAdd(uid)
				playerPacket!!.buffer!!.writeByteAdd(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				BufferedConnectionContext.queuePacket(playerPacket)
				PathFinder.calculateRoute(true, playerEntity.movementBaseX[0], playerEntity.movementBaseY[0], playerEntity.getSize(), 0, playerEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.OPLOC3) {
			StaticMedia.crossX = mouseX
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossType = 2
			StaticMedia.crossY = mouseY
			packet = TcpPacket.createPacket(ClientProt.OPLOC3, TcpPacket.isaac)
			packet!!.buffer!!.writeShortAdd(MapRegion.sceneBaseY + param1)
			packet!!.buffer!!.writeShortAddLittle(Int.MAX_VALUE and (entityUid ushr 32).toInt())
			packet!!.buffer!!.writeShortLittle(param0 + MapRegion.sceneBaseX)
			packet!!.buffer!!.writeByteAdd(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.walkToLoc(param1, entityUid, param0)
		}
		if (clickType == MiniMenuAction.OPNPC3) {
			npcEntityNode = NPCList.NPC_MAP.get(uid.toLong()) as NPCEntityNode?
			if (npcEntityNode != null) {
				npcEntity = npcEntityNode.npc
				StaticMedia.crossY = mouseY
				StaticMedia.crossSpritePtr = 0
				StaticMedia.crossX = mouseX
				StaticMedia.crossType = 2
				npcPacket = TcpPacket.createPacket(ClientProt.OPNPC3, TcpPacket.isaac)
				npcPacket!!.buffer!!.writeShort(uid)
				npcPacket!!.buffer!!.writeByte(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
				BufferedConnectionContext.queuePacket(npcPacket)
				PathFinder.calculateRoute(true, npcEntity!!.movementBaseX[0], npcEntity.movementBaseY[0], npcEntity.getSize(), 0, npcEntity.getSize(), -2, 0)
			}
		}
		if (clickType == MiniMenuAction.OPOBJ2) {
			StaticMedia.crossType = 2
			StaticMedia.crossX = mouseX
			StaticMedia.crossSpritePtr = 0
			StaticMedia.crossY = mouseY
			packet = TcpPacket.createPacket(ClientProt.OPOBJ2, TcpPacket.isaac)
			packet!!.buffer!!.writeShortAdd(param1 + MapRegion.sceneBaseY)
			packet!!.buffer!!.writeShortAdd(uid)
			packet!!.buffer!!.writeShortLittle(MapRegion.sceneBaseX + param0)
			packet!!.buffer!!.writeByteInverse(if (KeyRecorder.keyRecorder!!.isPressed(82)) 1 else 0)
			BufferedConnectionContext.queuePacket(packet)
			PathFinder.routeToGroundItem(param1, param0)
		}
		if (MiniMenu.interfaceSelectionActive) {
			Static657.cancelInterfaceSelection()
		}
		if (MenuInteraction.lastHoveredComponent != null && MenuInteraction.menuItemHoverCycle == 0) {
			Component.redrawComponent(MenuInteraction.lastHoveredComponent)
		}
	}
}
