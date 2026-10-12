import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import awt.Dimension
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.config.defaults.EntityDefaults
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import com.jagex.game.runetek5.scene.culling.Culling
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import awt.height
import awt.width

object ToolkitProfiler {

	@OriginalMember(owner = "client!pp", name = "a", descriptor = "(III)I")
	@JvmStatic
	fun getProfilingPerformance(@OriginalArg(0) toolkitId: Int, @OriginalArg(2) profilingTime: Int): Int {
		if (EntityDefaults.profilingMesh == -1) {
			return 1
		}
		if (GamePreferences.uid!!.toolkit!!.getValue() != toolkitId) {
			ToolkitRebuild.loadAssets(LocalizedText.aClass351_15.get(ClientState.language), true, toolkitId)
			if (toolkitId != GamePreferences.uid!!.toolkit!!.getValue()) {
				return -1
			}
		}
		try {
			@Pc(39) val dimension: Dimension = GameShell.gameCanvas!!.getSize()
			MessageBox.draw(true, FontManager.aClass68_4, FontManager.messageboxFont, LocalizedText.aClass351_15.get(ClientState.language), ClientState.activeToolkit)
			@Pc(55) val mesh: Mesh? = Mesh.fromResource(Resource.MODEL, EntityDefaults.profilingMesh)
			@Pc(58) val startTime: Long = Utils.currentTimeMillis()
			ClientState.activeToolkit!!.la()
			ClientState.aClass30_3!!.applyTranslation(0, Scene.halfTileOffset, 0)
			ClientState.activeToolkit!!.setCamera(ClientState.aClass30_3)
			ClientState.activeToolkit!!.DA(dimension.width / 2, dimension.height / 2, 512, 512)
			ClientState.activeToolkit!!.xa(1.0f)
			ClientState.activeToolkit!!.ZA(16777215, 0.5f, 0.5f, 20.0f, -50.0f, 30.0f)
			@Pc(104) val model: Model? = ClientState.activeToolkit!!.createModel(mesh, 2048, 64, 64, 768)
			@Pc(106) var modelsDrawn: Int = 0
			@Pc(108) var i: Int = 0
			label41@ while (i < 500) {
				ClientState.activeToolkit!!.GA(0)
				ClientState.activeToolkit!!.ya()
				@Pc(117) var j: Int = 15
				while (j >= 0) {
					@Pc(121) var k: Int = 0
					while (k <= j) {
						InterfaceComponentRenderer.aClass30_6!!.applyTranslation(((-(j.toFloat() / 2.0f) + k.toFloat()) * Culling.fullTileDimension.toFloat()).toInt(), 0, (j + 1) * Culling.fullTileDimension)
						modelsDrawn++
						model!!.render(InterfaceComponentRenderer.aClass30_6, null, 0)
						if (profilingTime.toLong() <= Utils.currentTimeMillis() - startTime) {
							break@label41
						}
						k++
					}
					j--
				}
				i++
			}
			ClientState.activeToolkit!!.finish()
			@Pc(197) val performanceIndicator: Long = (modelsDrawn * 1000).toLong() / (Utils.currentTimeMillis() - startTime)
			ClientState.activeToolkit!!.GA(0)
			ClientState.activeToolkit!!.ya()
			return performanceIndicator.toInt()
		} catch (@Pc(206) ex: Throwable) {
			ex.printStackTrace()
			return -1
		}
	}

	@OriginalMember(owner = "client!sh", name = "c", descriptor = "I")
	@JvmField
	var debugNpcCount: Int = 0

	@OriginalMember(owner = "client!ai", name = "x", descriptor = "I")
	@JvmField
	var debugPlayerCount: Int = 0

	@OriginalMember(owner = "client!qi", name = "d", descriptor = "Z")
	@JvmField
	var showProfiling: Boolean = false

	@OriginalMember(owner = "client!mf", name = "b", descriptor = "(B)V")
	@JvmStatic
	fun resetEntityDebugCounters() {
		ToolkitProfiler.debugPlayerCount = 0
		ToolkitProfiler.debugNpcCount = 0
		ToolkitProfiler.anInt6496 = 0
		ToolkitProfiler.anInt8154 = 0
	}

	@OriginalMember(owner = "client!pu", name = "d", descriptor = "I")
	@JvmField
	var anInt8154: Int = 0

	@OriginalMember(owner = "client!lea", name = "f", descriptor = "I")
	@JvmField
	var anInt6496: Int = 0
}
