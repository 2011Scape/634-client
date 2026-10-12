import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.core.constants.PlayerListConstants
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.HookRequest
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import com.jagex.game.runetek5.Cursor
import com.jagex.game.runetek5.PickList
import com.jagex.game.runetek5.entity.EntityNode
import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.Skybox
import com.jagex.game.runetek5.client.LoadingStage
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.graphics.skybox.SunModelReset
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import kotlin.jvm.JvmStatic

object Static657 {

	@OriginalMember(owner = "client!ec", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun cancelInterfaceSelection() {
		if (!MiniMenu.interfaceSelectionActive) {
			return
		}
		@Pc(16) val component: Component? = Component.getComponentWithSlot(MiniMenu.slotId, MiniMenuBuilder.USE_OPTIONS_FLAGS)
		if (component != null && component.hookParams != null) {
			@Pc(25) val hookRequest: HookRequest = HookRequest()
			hookRequest.source = component
			hookRequest.params = component.hookParams
			CS2Interpreter.executeHookInner(hookRequest)
		}
		MiniMenu.selectedItemId = -1
		MiniMenu.interfaceSelectionActive = false
		Cursor.interfaceCursorId = -1
		if (component != null) {
			Component.redrawComponent(component)
		}
	}

	@OriginalMember(owner = "client!ec", name = "c", descriptor = "(I)V")
	@JvmStatic
	fun method2905() {
		if (ClientState.activeToolkit == null) {
			return
		}
		if (OrthoMode.orthoEnabled) {
			OrthoMode.disableOrthoMode()
		}
		PickList.aClass182_1!!.clear()
		Scene.destroy()
		ComponentMinimap.resetMinimap()
		MiniMenu.resetMiniMenuSprites()
		EntityNode.resetPickableEntityPools()
		EnvironmentManager.reset()
		if (Skybox.configNpcJs5 != null) {
			Skybox.configNpcJs5!!.clearSprites()
		}
		SunModelReset.reset()
		TypeLists.resetAllCaches()
		StaticMedia.clearSprites()
		LoadingStage.initializeTips()
		WorldMapRelease.resetWorldmapCaches(false)
		@Pc(47) var i: Int = 0
		while (i < PlayerListConstants.COUNT) {
			@Pc(53) val playerEntity: PlayerEntity? = PlayerUpdate.players[i]
			if (playerEntity != null) {
				@Pc(57) var j: Int = 0
				while (j < playerEntity.models!!.size) {
					playerEntity.models!![j] = null
					j++
				}
			}
			i++
		}
		@Pc(78) var k: Int = 0
		while (k < NPCList.npcCount) {
			@Pc(85) val npcEntity: NPCEntity? = NPCList.NPC_ARRAY[k]!!.npc
			if (npcEntity != null) {
				@Pc(89) var m: Int = 0
				while (m < npcEntity.models!!.size) {
					npcEntity.models!![m] = null
					m++
				}
			}
			k++
		}
		InterfaceComponentRenderer.aClass30_6 = null
		ClientState.aClass30_3 = null
		ClientState.activeToolkit!!.free()
		ClientState.activeToolkit = null
	}
}
