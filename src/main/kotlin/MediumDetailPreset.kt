import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.preference.GamePreferences

object MediumDetailPreset {

	@OriginalMember(owner = "client!io", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun loadMediumDetailPreferences() {
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.animateBackground, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.animateBackgroundOverride, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.removeRoofs, 2)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.lb, 2)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.groundDecor, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.groundBlending, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.idleAnimations, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.flickeringEffects, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.characterShadows, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.textures, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.hardShadows, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.lightDetail, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.water, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.fog, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.antiAliasing, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.antiAliasingPreview, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.particles, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.buildArea, 0)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.bloom, 0)
		GamePreferences.applyDefaultCpuUsage()
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.maxScreenSize, 1)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.graphics, 3)
		GraphicCacheReset.resetGraphicCaches()
		EnvironmentManager.requestDirectEnvironmentTransition()
		WindowMode.needsResize = true
	}
}
