import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.core.constants.PlayerListConstants;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.HookRequest;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.graphics.sprite.StaticMedia;
import com.jagex.game.runetek5.Cursor;
import com.jagex.game.runetek5.PickList;
import com.jagex.game.runetek5.entity.EntityNode;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.Skybox;
import com.jagex.game.runetek5.client.LoadingStage;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.graphics.skybox.SunModelReset;
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap;
import com.jagex.game.runetek5.scene.environment.EnvironmentManager;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer;

public final class Static657 {

	@OriginalMember(owner = "client!ec", name = "b", descriptor = "(I)V")
	public static void cancelInterfaceSelection() {
		if (!MiniMenu.interfaceSelectionActive) {
			return;
		}
		@Pc(16) Component component = Component.getComponentWithSlot(MiniMenu.slotId, MiniMenuBuilder.USE_OPTIONS_FLAGS);
		if (component != null && component.hookParams != null) {
			@Pc(25) HookRequest hookRequest = new HookRequest();
			hookRequest.source = component;
			hookRequest.params = component.hookParams;
			CS2Interpreter.executeHookInner(hookRequest);
		}
		MiniMenu.selectedItemId = -1;
		MiniMenu.interfaceSelectionActive = false;
		Cursor.interfaceCursorId = -1;
		if (component != null) {
			Component.redrawComponent(component);
		}
	}

	@OriginalMember(owner = "client!ec", name = "c", descriptor = "(I)V")
	public static void method2905() {
		if (ClientState.activeToolkit == null) {
			return;
		}
		if (OrthoMode.orthoEnabled) {
			OrthoMode.disableOrthoMode();
		}
		PickList.aClass182_1.clear();
		Scene.destroy();
		ComponentMinimap.resetMinimap();
		MiniMenu.resetMiniMenuSprites();
		EntityNode.resetPickableEntityPools();
		EnvironmentManager.reset();
		if (Skybox.configNpcJs5 != null) {
			Skybox.configNpcJs5.clearSprites();
		}
		SunModelReset.reset();
		TypeLists.resetAllCaches();
		StaticMedia.clearSprites();
		LoadingStage.initializeTips();
		WorldMapRelease.resetWorldmapCaches(false);
		for (@Pc(47) int i = 0; i < PlayerListConstants.COUNT; i++) {
			@Pc(53) PlayerEntity playerEntity = PlayerUpdate.players[i];
			if (playerEntity != null) {
				for (@Pc(57) int j = 0; j < playerEntity.models.length; j++) {
					playerEntity.models[j] = null;
				}
			}
		}
		for (@Pc(78) int k = 0; k < NPCList.npcCount; k++) {
			@Pc(85) NPCEntity npcEntity = NPCList.NPC_ARRAY[k].npc;
			if (npcEntity != null) {
				for (@Pc(89) int m = 0; m < npcEntity.models.length; m++) {
					npcEntity.models[m] = null;
				}
			}
		}
		InterfaceComponentRenderer.aClass30_6 = null;
		ClientState.aClass30_3 = null;
		ClientState.activeToolkit.free();
		ClientState.activeToolkit = null;
	}
}
