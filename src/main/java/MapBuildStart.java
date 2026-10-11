import com.jagex.game.runetek5.cache.node.ChangeLocationRequest;
import com.jagex.game.runetek5.core.constants.MainLogicStep;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.MapList;
import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.core.constants.AreaMode;
import com.jagex.game.runetek5.core.constants.PlayerListConstants;
import com.jagex.game.runetek5.camera.CameraMode;
import com.jagex.game.runetek5.statics.WorldMapRenderer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.ObjStack;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.scene.LocAction;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.sound.player.area.AreaSoundPlayer;
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.scene.environment.EnvironmentManager;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.Minimap;
import com.jagex.game.runetek5.config.staticelements.StaticElements;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.scene.tile.TileMessage;
import com.jagex.game.runetek5.entity.ProjectileAnimation;
import com.jagex.game.runetek5.graphics.map.minimap.ComponentMinimap;
import com.jagex.game.runetek5.graphics.map.worldmap.WorldMap;
import com.jagex.game.runetek5.client.ClientState;

public final class MapBuildStart {

	@OriginalMember(owner = "client!uba", name = "a", descriptor = "(IBIIZ)V")
	public static void performUpdate(@OriginalArg(0) int baseY, @OriginalArg(2) int baseX, @OriginalArg(3) MainLogicStep step, @OriginalArg(4) boolean forcedPositionUpdate) {
		if (!forcedPositionUpdate && baseX == MapRegion.mapCenterChunkX && baseY == MapRegion.mapBaseChunkY && (PlayerUpdate.currentLevel == MapRegion.mapBuildLevel || GamePreferences.uid.animateBackgroundOverride.getValue() == 1)) {
			return;
		}
		MapRegion.mapCenterChunkX = baseX;
		MapRegion.mapBaseChunkY = baseY;
		MapRegion.mapBuildLevel = PlayerUpdate.currentLevel;
		if (GamePreferences.uid.animateBackgroundOverride.getValue() == 1) {
			MapRegion.mapBuildLevel = 0;
		}
		MainLogicStepTransition.setMainLogicStep(step);
		MessageBox.draw(true, FontManager.aClass68_4, FontManager.messageboxFont, LocalizedText.aClass351_14.get(ClientState.language), ClientState.activeToolkit);
		@Pc(56) int oldSceneBaseX = MapRegion.sceneBaseX;
		@Pc(58) int oldSceneBaseY = MapRegion.sceneBaseY;
		MapRegion.sceneBaseX = (MapRegion.mapCenterChunkX - (MapRegion.sceneSizeX >> 4)) * 8;
		MapRegion.sceneBaseY = (MapRegion.mapBaseChunkY - (MapRegion.sceneSizeY >> 4)) * 8;
		WorldMap.aClass2_Sub6_Sub4_3 = MapList.getMapDef(MapRegion.mapCenterChunkX * 8, MapRegion.mapBaseChunkY * 8);
		StaticElements.aClass333_3 = null;
		@Pc(89) int deltaX = MapRegion.sceneBaseX - oldSceneBaseX;
		@Pc(94) int deltaY = MapRegion.sceneBaseY - oldSceneBaseY;
		@Pc(99) int index;
		@Pc(112) int local112;
		@Pc(236) int local236;
		@Pc(174) int local174;
		if (step == MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD) {
			for (index = 0; index < NPCList.npcCount; index++) {
				@Pc(105) NPCEntityNode npcEntityNode = NPCList.NPC_ARRAY[index];
				if (npcEntityNode != null) {
					@Pc(110) NPCEntity npcEntity = npcEntityNode.npc;
					for (int stepIndex = 0; stepIndex < 10; stepIndex++) {
						npcEntity.movementBaseX[stepIndex] -= deltaX;
						npcEntity.movementBaseY[stepIndex] -= deltaY;
					}
					npcEntity.z -= deltaY * 512;
					npcEntity.x -= deltaX * 512;
				}
			}
		} else {
			NPCList.NPC_UPDATE_INDEX = 0;
			@Pc(162) boolean npcsRemoved = false;
			@Pc(168) int maxX = MapRegion.sceneSizeX * 512 - 512;
			int maxZ = (MapRegion.sceneSizeY - 1) * 512;
			for (int npcIndex = 0; npcIndex < NPCList.npcCount; npcIndex++) {
				@Pc(182) NPCEntityNode npcEntityNode2 = NPCList.NPC_ARRAY[npcIndex];
				if (npcEntityNode2 != null) {
					@Pc(187) NPCEntity npcEntity2 = npcEntityNode2.npc;
					npcEntity2.z -= deltaY * 512;
					npcEntity2.x -= deltaX * 512;
					if (npcEntity2.x >= 0 && npcEntity2.x <= maxX && npcEntity2.z >= 0 && maxZ >= npcEntity2.z) {
						@Pc(234) boolean stepsInScene = true;
						for (int stepIndex = 0; stepIndex < 10; stepIndex++) {
							npcEntity2.movementBaseX[stepIndex] -= deltaX;
							npcEntity2.movementBaseY[stepIndex] -= deltaY;
							if (npcEntity2.movementBaseX[stepIndex] < 0 || MapRegion.sceneSizeX <= npcEntity2.movementBaseX[stepIndex] || npcEntity2.movementBaseY[stepIndex] < 0 || npcEntity2.movementBaseY[stepIndex] >= MapRegion.sceneSizeY) {
								stepsInScene = false;
							}
						}
						if (stepsInScene) {
							NPCList.NPC_UPDATE_INDICES[NPCList.NPC_UPDATE_INDEX++] = npcEntity2.playerIndex;
						} else {
							npcEntity2.setNpcType(null);
							npcsRemoved = true;
							npcEntityNode2.unlink();
						}
					} else {
						npcEntity2.setNpcType(null);
						npcEntityNode2.unlink();
						npcsRemoved = true;
					}
				}
			}
			if (npcsRemoved) {
				NPCList.npcCount = NPCList.NPC_MAP.size();
				NPCList.NPC_MAP.toArray(NPCList.NPC_ARRAY);
			}
		}
		for (index = 0; index < PlayerListConstants.COUNT; index++) {
			@Pc(348) PlayerEntity playerEntity = PlayerUpdate.players[index];
			if (playerEntity != null) {
				for (int stepIndex = 0; stepIndex < 10; stepIndex++) {
					playerEntity.movementBaseX[stepIndex] -= deltaX;
					playerEntity.movementBaseY[stepIndex] -= deltaY;
				}
				playerEntity.x -= deltaX * 512;
				playerEntity.z -= deltaY * 512;
			}
		}
		@Pc(398) HintArrow[] hintArrows = PlayerUpdate.HINT_ARROWS;
		for (int arrowIndex = 0; arrowIndex < hintArrows.length; arrowIndex++) {
			@Pc(406) HintArrow hintArrow = hintArrows[arrowIndex];
			if (hintArrow != null) {
				hintArrow.y -= deltaY * 512;
				hintArrow.x -= deltaX * 512;
			}
		}
		for (@Pc(438) ChangeLocationRequest changeLocationRequest = (ChangeLocationRequest) LocAction.pendingRemovals.first(); changeLocationRequest != null; changeLocationRequest = (ChangeLocationRequest) LocAction.pendingRemovals.next()) {
			changeLocationRequest.y -= deltaY;
			changeLocationRequest.x -= deltaX;
			if (MapRegion.areaMode != 4 && (changeLocationRequest.x < 0 || changeLocationRequest.y < 0 || MapRegion.sceneSizeX <= changeLocationRequest.x || MapRegion.sceneSizeY <= changeLocationRequest.y)) {
				changeLocationRequest.unlink();
			}
		}
		if (MapRegion.areaMode != AreaMode.RETAIN_OUT_OF_BOUNDS) {
			for (@Pc(495) ObjStack objStack = (ObjStack) ObjStack.GROUND_ITEMS.first(); objStack != null; objStack = (ObjStack) ObjStack.GROUND_ITEMS.next()) {
				@Pc(503) int tileX = (int) (objStack.key & 0x3FFFL);
				@Pc(508) int localX = tileX - MapRegion.sceneBaseX;
				int tileY = (int) (objStack.key >> 14 & 0x3FFFL);
				@Pc(521) int localY = tileY - MapRegion.sceneBaseY;
				if (localX < 0 || localY < 0 || MapRegion.sceneSizeX <= localX || localY >= MapRegion.sceneSizeY) {
					objStack.unlink();
				}
			}
		}
		if (Minimap.minimapFlagX != 0) {
			Minimap.minimapFlagX -= deltaX;
			Minimap.minimapFlagY -= deltaY;
		}
		AreaSoundPlayer.resetAreaSounds();
		if (step != MainLogicStep.STEP_GAME_SCREEN_MAP_BUILD) {
			Camera.moveToLocalY -= deltaY;
			Camera.cameraPositionAbsoluteX -= deltaX * 512;
			Camera.lookAtLocalX -= deltaX;
			Camera.lookAtLocalY -= deltaY;
			Camera.moveToLocalX -= deltaX;
			Camera.cameraPositionAbsoluteY -= deltaY * 512;
			if (Math.abs(deltaX) > MapRegion.sceneSizeX || Math.abs(deltaY) > MapRegion.sceneSizeY) {
				EnvironmentManager.requestDirectEnvironmentTransition();
			}
		} else if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWCOORD) {
			Camera.cameraTargetAbsZ -= deltaY * 512;
			Camera.cameraBaseX -= deltaX * 512;
			Camera.cameraBaseZ -= deltaY * 512;
			Camera.cameraTargetAbsX -= deltaX * 512;
		} else {
			Camera.cameraCalculationType = CameraMode.MODE_DEFAULT;
			Camera.roofRemovalTargetY = -1;
			Camera.roofRemovalTargetX = -CameraMode.MODE_DEFAULT;
		}
		MiniMenu.reset();
		ComponentMinimap.resetMinimap();
		Renderers.activeToolkit.clear();
		ProjectileAnimation.aClass341_24.clear();
		TileMessage.aClass325_8.clear();
		ParticleSystemManager.destroy();
	}
}
