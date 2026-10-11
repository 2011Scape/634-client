import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.scene.environment.EnvironmentManager;
import com.jagex.game.runetek5.client.window.WindowMode;
import com.jagex.game.runetek5.preference.GamePreferences;

public final class LowDetailPreset {

	@OriginalMember(owner = "client!pc", name = "a", descriptor = "(Z)V")
	public static void loadLowDetailPreferences() {
		GamePreferences.uid.setValue(GamePreferences.uid.animateBackground, 1);
		GamePreferences.uid.setValue(GamePreferences.uid.animateBackgroundOverride, 1);
		GamePreferences.uid.setValue(GamePreferences.uid.removeRoofs, 1);
		GamePreferences.uid.setValue(GamePreferences.uid.lb, 1);
		GamePreferences.uid.setValue(GamePreferences.uid.groundDecor, 1);
		GamePreferences.uid.setValue(GamePreferences.uid.groundBlending, 1);
		GamePreferences.uid.setValue(GamePreferences.uid.idleAnimations, 0);
		GamePreferences.uid.setValue(GamePreferences.uid.flickeringEffects, 0);
		GamePreferences.uid.setValue(GamePreferences.uid.characterShadows, 0);
		GamePreferences.uid.setValue(GamePreferences.uid.hardShadows, 0);
		GamePreferences.uid.setValue(GamePreferences.uid.textures, 0);
		GamePreferences.uid.setValue(GamePreferences.uid.lightDetail, 0);
		GamePreferences.uid.setValue(GamePreferences.uid.water, 0);
		GamePreferences.uid.setValue(GamePreferences.uid.fog, 0);
		GamePreferences.uid.setValue(GamePreferences.uid.antiAliasing, 0);
		GamePreferences.uid.setValue(GamePreferences.uid.antiAliasingPreview, 0);
		GamePreferences.uid.setValue(GamePreferences.uid.particles, 0);
		GamePreferences.uid.setValue(GamePreferences.uid.buildArea, 0);
		GamePreferences.uid.setValue(GamePreferences.uid.bloom, 0);
		GamePreferences.applyDefaultCpuUsage();
		GamePreferences.uid.setValue(GamePreferences.uid.maxScreenSize, 2);
		GamePreferences.uid.setValue(GamePreferences.uid.graphics, 2);
		GraphicCacheReset.resetGraphicCaches();
		EnvironmentManager.requestDirectEnvironmentTransition();
		WindowMode.needsResize = true;
	}
}
