import com.jagex.game.runetek5.entity.Entity;
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity;
import com.jagex.game.runetek5.RenderQueue;
import com.jagex.game.runetek5.RenderWorker;
import com.jagex.game.runetek5.ScriptRunner;
import com.jagex.game.runetek5.SceneVisibleTiles;
import com.jagex.game.runetek5.scene.tile.Tile;
import com.jagex.game.runetek5.graphics.light.StaticPointLight;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.statics.UnderwaterTileGrid;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.AbstractNanoTimer;
import com.jagex.game.runetek5.PickList;
import com.jagex.game.runetek5.SceneDrawer;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.scene.culling.Culling;

public final class SceneInitializer {

	@OriginalMember(owner = "client!kw", name = "a", descriptor = "(Lclient!ha;IIIIIIZZ)V")
	public static void initializeScene(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int renderCores, @OriginalArg(4) int sceneWidth, @OriginalArg(5) int sceneLength, @OriginalArg(6) int visibleTiles, @OriginalArg(7) boolean hasUnderwaterMap, @OriginalArg(8) boolean hasLights) {
		Scene.toolkit = toolkit;
		SceneDrawer.renderThreadCount = renderCores;
		SceneDrawer.multithreadedRendering = SceneDrawer.renderThreadCount > 1 && Scene.toolkit.method7311();
		Scene.tileUnits = 9;
		Culling.fullTileDimension = 0x1 << Scene.tileUnits;
		Scene.halfTileOffset = Culling.fullTileDimension >> 1;
		Math.sqrt((double) (Scene.halfTileOffset * Scene.halfTileOffset + Scene.halfTileOffset * Scene.halfTileOffset));
		Scene.length = 4;
		Scene.sceneWidth = sceneWidth;
		Camera.sceneLength = sceneLength;
		SceneVisibleTiles.visibleTiles = visibleTiles;
		Scene.aClass158_1 = AbstractNanoTimer.create();
		SceneCullingInit.initializeCulling();
		Scene.aClass100ArrayArrayArray1 = new Tile[4][Scene.sceneWidth][Camera.sceneLength];
		Scene.aClass65Array2 = new Ground[4];
		if (hasUnderwaterMap) {
			Scene.waterColors = new int[Scene.sceneWidth][Camera.sceneLength];
			Scene.waterIntensities = new byte[Scene.sceneWidth][Camera.sceneLength];
			Scene.waterScales = new short[Scene.sceneWidth][Camera.sceneLength];
			UnderwaterTileGrid.aClass100ArrayArrayArray2 = new Tile[1][Scene.sceneWidth][Camera.sceneLength];
			Scene.aClass65Array3 = new Ground[1];
		} else {
			Scene.waterColors = null;
			Scene.waterIntensities = null;
			Scene.waterScales = null;
			UnderwaterTileGrid.aClass100ArrayArrayArray2 = null;
			Scene.aClass65Array3 = null;
		}
		if (hasLights) {
			Scene.tileLightUids = new long[4][sceneWidth][sceneLength];
			Scene.aClass300Array1 = new StaticPointLight[65535];
			Scene.pointLightApplied = new boolean[65535];
			Scene.lightSlotCount = 0;
		} else {
			Scene.tileLightUids = null;
			Scene.aClass300Array1 = null;
			Scene.pointLightApplied = null;
			Scene.lightSlotCount = 0;
		}
		Scene.setUnderwater(false);
		Scene.stringStack = new Entity[2];
		Scene.aClass3_Sub1Array129 = new Entity[2];
		Scene.aClass3_Sub1Array94 = new Entity[2];
		Scene.aClass3_Sub1Array143 = new Entity[10000];
		Scene.onscreenOpaqueCount = 0;
		Scene.aClass3_Sub1Array145 = new Entity[5000];
		Scene.transparentEntityCount = 0;
		Scene.aClass3_Sub1_Sub3Array1 = new AbstractInteractiveEntity[5000];
		Scene.interactiveObjectCount = 0;
		Scene.tileInFrustum = new boolean[SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 1][SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 1];
		Scene.tileOnScreen = new boolean[SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 2][SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 2];
		Scene.tileVisibility = new int[SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 2];
		PickList.aClass182_1 = PickList.aClass182_2;
		if (SceneDrawer.multithreadedRendering) {
			Scene.levelTileVisibility = new boolean[4][SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 1][SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 1];
			Scene.aBooleanArrayArrayArray1 = new boolean[4][][];
			if (RenderWorker.aClass366Array1 != null) {
				Scene.stopRenderWorkers();
			}
			RenderWorker.aClass366Array1 = new RenderWorker[SceneDrawer.renderThreadCount];
			Scene.toolkit.allocateThreads(RenderWorker.aClass366Array1.length + 1);
			Scene.toolkit.linkThreads(0);
			for (@Pc(205) int i = 0; i < RenderWorker.aClass366Array1.length; i++) {
				RenderWorker.aClass366Array1[i] = new RenderWorker(i + 1, Scene.toolkit);
				(new Thread(RenderWorker.aClass366Array1[i], "wr" + i)).start();
			}
			@Pc(240) byte queueCount;
			if (SceneDrawer.renderThreadCount == 2) {
				queueCount = 4;
				Scene.renderThreadCount = 2;
			} else if (SceneDrawer.renderThreadCount == 3) {
				queueCount = 6;
				Scene.renderThreadCount = 3;
			} else {
				queueCount = 8;
				Scene.renderThreadCount = 4;
			}
			Scene.aClass59Array1 = new RenderQueue[queueCount];
			for (@Pc(260) int j = 0; j < queueCount; j++) {
				Scene.aClass59Array1[j] = new RenderQueue(RenderQueue.QUEUE_NAMES_BY_CORE_COUNT[SceneDrawer.renderThreadCount - 2][j]);
			}
		} else {
			Scene.renderThreadCount = 1;
		}
		ScriptRunner.threadSplitX = new int[Scene.renderThreadCount - 1];
		SceneDrawer.threadSplitOffsets = new int[Scene.renderThreadCount - 1];
	}
}
