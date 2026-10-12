import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.preference.GamePreferences

object MinDetailPreset {

	@OriginalMember(owner = "client!aw", name = "a", descriptor = "(IZ)V")
	@JvmStatic
	fun loadMinDetailPreferences() {
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.animateBackground, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.animateBackgroundOverride, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.removeRoofs, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.lb, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.groundDecor, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.fog, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.groundBlending, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.idleAnimations, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.flickeringEffects, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.characterShadows, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.hardShadows, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.textures, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.lightDetail, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.water, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.antiAliasing, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.antiAliasingPreview, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.particles, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.buildArea, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.bloom, 0)
		GamePreferences.applyDefaultCpuUsage()
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.maxScreenSize, 2)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.graphics, 1)
		GraphicCacheReset.resetGraphicCaches()
		EnvironmentManager.requestDirectEnvironmentTransition()
		WindowMode.needsResize = true
	}
}
