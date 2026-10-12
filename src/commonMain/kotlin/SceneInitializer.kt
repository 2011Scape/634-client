import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.entity.AbstractInteractiveEntity
import com.jagex.game.runetek5.RenderQueue
import com.jagex.game.runetek5.RenderWorker
import com.jagex.game.runetek5.ScriptRunner
import com.jagex.game.runetek5.SceneVisibleTiles
import com.jagex.game.runetek5.scene.tile.Tile
import com.jagex.game.runetek5.graphics.light.StaticPointLight
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.statics.UnderwaterTileGrid
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.AbstractNanoTimer
import com.jagex.game.runetek5.PickList
import com.jagex.game.runetek5.SceneDrawer
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.scene.culling.Culling
import kotlin.jvm.JvmStatic
import lang.Thread

object SceneInitializer {

	@OriginalMember(owner = "client!kw", name = "a", descriptor = "(Lclient!ha;IIIIIIZZ)V")
	@JvmStatic
	fun initializeScene(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) renderCores: Int, @OriginalArg(4) sceneWidth: Int, @OriginalArg(5) sceneLength: Int, @OriginalArg(6) visibleTiles: Int, @OriginalArg(7) hasUnderwaterMap: Boolean, @OriginalArg(8) hasLights: Boolean) {
		Scene.toolkit = toolkit
		SceneDrawer.renderThreadCount = renderCores
		SceneDrawer.multithreadedRendering = SceneDrawer.renderThreadCount > 1 && Scene.toolkit!!.method7311()
		Scene.tileUnits = 9
		Culling.fullTileDimension = 0x1 shl Scene.tileUnits
		Scene.halfTileOffset = Culling.fullTileDimension shr 1
		kotlin.math.sqrt((Scene.halfTileOffset * Scene.halfTileOffset + Scene.halfTileOffset * Scene.halfTileOffset).toDouble())
		Scene.length = 4
		Scene.sceneWidth = sceneWidth
		Camera.sceneLength = sceneLength
		SceneVisibleTiles.visibleTiles = visibleTiles
		Scene.aClass158_1 = AbstractNanoTimer.create()
		SceneCullingInit.initializeCulling()
		Scene.aClass100ArrayArrayArray1 = Array<Array<Array<Tile?>?>?>(4) { Array<Array<Tile?>?>(Scene.sceneWidth) { arrayOfNulls<Tile>(Camera.sceneLength) } }
		Scene.aClass65Array2 = arrayOfNulls<Ground>(4)
		if (hasUnderwaterMap) {
			Scene.waterColors = Array<IntArray?>(Scene.sceneWidth) { IntArray(Camera.sceneLength) }
			Scene.waterIntensities = Array<ByteArray?>(Scene.sceneWidth) { ByteArray(Camera.sceneLength) }
			Scene.waterScales = Array<ShortArray?>(Scene.sceneWidth) { ShortArray(Camera.sceneLength) }
			UnderwaterTileGrid.aClass100ArrayArrayArray2 = Array<Array<Array<Tile?>?>?>(1) { Array<Array<Tile?>?>(Scene.sceneWidth) { arrayOfNulls<Tile>(Camera.sceneLength) } }
			Scene.aClass65Array3 = arrayOfNulls<Ground>(1)
		} else {
			Scene.waterColors = null
			Scene.waterIntensities = null
			Scene.waterScales = null
			UnderwaterTileGrid.aClass100ArrayArrayArray2 = null
			Scene.aClass65Array3 = null
		}
		if (hasLights) {
			Scene.tileLightUids = Array<Array<LongArray?>?>(4) { Array<LongArray?>(sceneWidth) { LongArray(sceneLength) } }
			Scene.aClass300Array1 = arrayOfNulls<StaticPointLight>(65535)
			Scene.pointLightApplied = BooleanArray(65535)
			Scene.lightSlotCount = 0
		} else {
			Scene.tileLightUids = null
			Scene.aClass300Array1 = null
			Scene.pointLightApplied = null
			Scene.lightSlotCount = 0
		}
		Scene.setUnderwater(false)
		Scene.stringStack = arrayOfNulls<Entity>(2)
		Scene.aClass3_Sub1Array129 = arrayOfNulls<Entity>(2)
		Scene.aClass3_Sub1Array94 = arrayOfNulls<Entity>(2)
		Scene.aClass3_Sub1Array143 = arrayOfNulls<Entity>(10000)
		Scene.onscreenOpaqueCount = 0
		Scene.aClass3_Sub1Array145 = arrayOfNulls<Entity>(5000)
		Scene.transparentEntityCount = 0
		Scene.aClass3_Sub1_Sub3Array1 = arrayOfNulls<AbstractInteractiveEntity>(5000)
		Scene.interactiveObjectCount = 0
		Scene.tileInFrustum = Array<BooleanArray?>(SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 1) { BooleanArray(SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 1) }
		Scene.tileOnScreen = Array<BooleanArray?>(SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 2) { BooleanArray(SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 2) }
		Scene.tileVisibility = IntArray(SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 2)
		PickList.aClass182_1 = PickList.aClass182_2
		if (SceneDrawer.multithreadedRendering) {
			Scene.levelTileVisibility = Array<Array<BooleanArray?>?>(4) { Array<BooleanArray?>(SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 1) { BooleanArray(SceneVisibleTiles.visibleTiles + SceneVisibleTiles.visibleTiles + 1) } }
			Scene.aBooleanArrayArrayArray1 = arrayOfNulls<Array<BooleanArray?>>(4)
			if (RenderWorker.aClass366Array1 != null) {
				Scene.stopRenderWorkers()
			}
			RenderWorker.aClass366Array1 = arrayOfNulls<RenderWorker>(SceneDrawer.renderThreadCount)
			Scene.toolkit!!.allocateThreads(RenderWorker.aClass366Array1!!.size + 1)
			Scene.toolkit!!.linkThreads(0)
			@Pc(205) var i: Int = 0
			while (i < RenderWorker.aClass366Array1!!.size) {
				RenderWorker.aClass366Array1!![i] = RenderWorker(i + 1, Scene.toolkit)
				(Thread(RenderWorker.aClass366Array1!![i], "wr" + i)).start()
				i++
			}
			@Pc(240) val queueCount: Byte
			if (SceneDrawer.renderThreadCount == 2) {
				queueCount = 4
				Scene.renderThreadCount = 2
			} else if (SceneDrawer.renderThreadCount == 3) {
				queueCount = 6
				Scene.renderThreadCount = 3
			} else {
				queueCount = 8
				Scene.renderThreadCount = 4
			}
			Scene.aClass59Array1 = arrayOfNulls<RenderQueue>(queueCount.toInt())
			@Pc(260) var j: Int = 0
			while (j < queueCount) {
				Scene.aClass59Array1!![j] = RenderQueue(RenderQueue.QUEUE_NAMES_BY_CORE_COUNT[SceneDrawer.renderThreadCount - 2]!![j])
				j++
			}
		} else {
			Scene.renderThreadCount = 1
		}
		ScriptRunner.threadSplitX = IntArray(Scene.renderThreadCount - 1)
		SceneDrawer.threadSplitOffsets = IntArray(Scene.renderThreadCount - 1)
	}
}
