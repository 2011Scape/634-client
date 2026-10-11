import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.FontMetricsLoader;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.cache.node.MemoryPool;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.ToolkitType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.systeminfo.SystemInfo;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.scene.environment.EnvironmentManager;
import com.jagex.game.runetek5.Fullscreen;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.client.window.WindowMode;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.Js5TextureSource;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer;
import com.jagex.game.runetek5.preference.impl.ToolkitOption;

public final class ActiveToolkitInit {

	@OriginalMember(owner = "client!au", name = "a", descriptor = "(Ljava/lang/String;IZI)V")
	public static void prepareForRendering(@OriginalArg(0) String string, @OriginalArg(1) int toolkitId, @OriginalArg(2) boolean skipSettingsVerification) {
		OrthoMode.disableOrthoMode();
		if (toolkitId == ToolkitType.JAVA) {
			ClientState.activeToolkit = CanvasToolkitFactory.create(ToolkitType.JAVA, Js5TextureSource.anInterface4_10, GamePreferences.uid.antiAliasingPreview.getValue() * 2, GameShell.gameCanvas, Resource.SHADER);
			if (string != null) {
				ClientState.activeToolkit.GA(0);
				@Pc(36) FontMetrics fontMetrics = FontMetricsLoader.getFontMetrics(FontManager.p12FullIndex, Resource.FONT_METRICS);
				@Pc(45) AbstractFontRenderer font = ClientState.activeToolkit.createFontRenderer(fontMetrics, SpriteType.decodeGroup(Resource.SPRITES, FontManager.p12FullIndex));
				GameShell.clearCanvasBackground();
				MessageBox.draw(true, font, fontMetrics, string, ClientState.activeToolkit);
			}
		} else {
			@Pc(58) Toolkit toolkit = null;
			@Pc(86) AbstractFontRenderer font2;
			if (string != null) {
				toolkit = CanvasToolkitFactory.create(ToolkitType.JAVA, Js5TextureSource.anInterface4_10, 0, GameShell.gameCanvas, Resource.SHADER);
				toolkit.GA(0);
				@Pc(77) FontMetrics fontMetrics2 = FontMetricsLoader.getFontMetrics(FontManager.p12FullIndex, Resource.FONT_METRICS);
				font2 = toolkit.createFontRenderer(fontMetrics2, SpriteType.decodeGroup(Resource.SPRITES, FontManager.p12FullIndex));
				GameShell.clearCanvasBackground();
				MessageBox.draw(true, font2, fontMetrics2, string, toolkit);
			}
			boolean var14 = false;
			label216: {
				try {
					var14 = true;
					ClientState.activeToolkit = CanvasToolkitFactory.create(toolkitId, Js5TextureSource.anInterface4_10, GamePreferences.uid.antiAliasingPreview.getValue() * 2, GameShell.gameCanvas, Resource.SHADER);
					if (string != null) {
						toolkit.GA(0);
						@Pc(123) FontMetrics fontMetrics3 = FontMetricsLoader.getFontMetrics(FontManager.p12FullIndex, Resource.FONT_METRICS);
						@Pc(132) AbstractFontRenderer font3 = toolkit.createFontRenderer(fontMetrics3, SpriteType.decodeGroup(Resource.SPRITES, FontManager.p12FullIndex));
						GameShell.clearCanvasBackground();
						MessageBox.draw(true, font3, fontMetrics3, string, toolkit);
					}
					if (ClientState.activeToolkit.utilisesNativeHeap()) {
						@Pc(148) boolean highMemory = true;
						try {
							highMemory = SystemInfo.SYSTEM_INFO.ram > 256;
						} catch (@Pc(158) Throwable ex) {
						}
						@Pc(164) MemoryPool memoryPool;
						if (highMemory) {
							memoryPool = ClientState.activeToolkit.createHeap(146800640);
						} else {
							memoryPool = ClientState.activeToolkit.createHeap(104857600);
						}
						ClientState.activeToolkit.setHeap(memoryPool);
						var14 = false;
					} else {
						var14 = false;
					}
					break label216;
				} catch (@Pc(175) Throwable ex2) {
					@Pc(180) int toolkitSetting = GamePreferences.uid.toolkit.getValue();
					if (toolkitSetting == 2) {
						ToolkitOption.renderWithCpu = true;
					}
					GamePreferences.uid.setValue(GamePreferences.uid.toolkit, 0);
					prepareForRendering(string, toolkitSetting, skipSettingsVerification);
					@Pc(199) Object object = null;
					var14 = false;
				} finally {
					if (var14) {
						font2 = null;
						if (toolkit != null) {
							try {
								toolkit.free();
							} catch (@Pc(345) Throwable ex3) {
							}
						}
					}
				}
				if (toolkit != null) {
					try {
						toolkit.free();
					} catch (@Pc(325) Throwable ex4) {
					}
				}
				return;
			}
			font2 = null;
			if (toolkit != null) {
				try {
					toolkit.free();
				} catch (@Pc(335) Throwable ex5) {
				}
			}
		}
		GamePreferences.uid.toolkit.setActive(!skipSettingsVerification);
		GamePreferences.uid.setValue(GamePreferences.uid.toolkit, toolkitId);
		Renderers.freeGpuItemRenderer();
		ClientState.activeToolkit.initialize();
		ClientState.activeToolkit.X(32);
		ClientState.aClass30_3 = ClientState.activeToolkit.createMatrix();
		InterfaceComponentRenderer.aClass30_6 = ClientState.activeToolkit.createMatrix();
		MapRegion.calculateViewRange();
		ClientState.activeToolkit.setShrinkTextures(GamePreferences.uid.smallTextures.getValue() == 1);
		if (ClientState.activeToolkit.supportsBloom()) {
			GamePreferences.setBloom(GamePreferences.uid.bloom.getValue() == 1);
		}
		EnvironmentManager.initialize(MapRegion.sceneSizeX >> 3, ClientState.activeToolkit, MapRegion.sceneSizeY >> 3);
		EnvironmentManager.requestDirectEnvironmentTransition();
		GamePreferences.preferencesSavedOnServer = false;
		Fullscreen.aClass25Array1 = null;
		WindowMode.needsResize = true;
		OrthoMode.applyOrthographicMode();
	}
}
