import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.FontMetricsLoader
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.cache.node.MemoryPool
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.ToolkitType
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.systeminfo.SystemInfo
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.scene.environment.EnvironmentManager
import com.jagex.game.runetek5.Fullscreen
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.Js5TextureSource
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import com.jagex.game.runetek5.preference.impl.ToolkitOption
import com.jagex.game.runetek5.client.features.HdFallback
import kotlin.jvm.JvmStatic

object ActiveToolkitInit {

	@OriginalMember(owner = "client!au", name = "a", descriptor = "(Ljava/lang/String;IZI)V")
	@JvmStatic
	fun prepareForRendering(@OriginalArg(0) string: String?, @OriginalArg(1) toolkitId: Int, @OriginalArg(2) skipSettingsVerification: Boolean) {
		OrthoMode.disableOrthoMode()
		if (toolkitId == ToolkitType.JAVA) {
			ClientState.activeToolkit = CanvasToolkitFactory.create(ToolkitType.JAVA, Js5TextureSource.anInterface4_10, GamePreferences.uid!!.antiAliasingPreview!!.getValue() * 2, GameShell.gameCanvas, Resource.SHADER)
			if (string != null) {
				ClientState.activeToolkit!!.GA(0)
				@Pc(36) val fontMetrics: FontMetrics? = FontMetricsLoader.getFontMetrics(FontManager.p12FullIndex, Resource.FONT_METRICS)
				@Pc(45) val font: AbstractFontRenderer? = ClientState.activeToolkit!!.createFontRenderer(fontMetrics, SpriteType.decodeGroup(Resource.SPRITES, FontManager.p12FullIndex))
				GameShell.clearCanvasBackground()
				MessageBox.draw(true, font, fontMetrics, string, ClientState.activeToolkit)
			}
		} else {
			@Pc(58) var toolkit: Toolkit? = null
			@Pc(86) var font2: AbstractFontRenderer?
			if (string != null) {
				toolkit = CanvasToolkitFactory.create(ToolkitType.JAVA, Js5TextureSource.anInterface4_10, 0, GameShell.gameCanvas, Resource.SHADER)
				toolkit!!.GA(0)
				@Pc(77) val fontMetrics2: FontMetrics? = FontMetricsLoader.getFontMetrics(FontManager.p12FullIndex, Resource.FONT_METRICS)
				font2 = toolkit.createFontRenderer(fontMetrics2, SpriteType.decodeGroup(Resource.SPRITES, FontManager.p12FullIndex))
				GameShell.clearCanvasBackground()
				MessageBox.draw(true, font2, fontMetrics2, string, toolkit)
			}
			var var14: Boolean = false
			run label216@{
				try {
					var14 = true
					ClientState.activeToolkit = CanvasToolkitFactory.create(toolkitId, Js5TextureSource.anInterface4_10, GamePreferences.uid!!.antiAliasingPreview!!.getValue() * 2, GameShell.gameCanvas, Resource.SHADER)
					if (string != null) {
						toolkit!!.GA(0)
						@Pc(123) val fontMetrics3: FontMetrics? = FontMetricsLoader.getFontMetrics(FontManager.p12FullIndex, Resource.FONT_METRICS)
						@Pc(132) val font3: AbstractFontRenderer? = toolkit!!.createFontRenderer(fontMetrics3, SpriteType.decodeGroup(Resource.SPRITES, FontManager.p12FullIndex))
						GameShell.clearCanvasBackground()
						MessageBox.draw(true, font3, fontMetrics3, string, toolkit)
					}
					if (ClientState.activeToolkit!!.utilisesNativeHeap()) {
						@Pc(148) var highMemory: Boolean = true
						try {
							highMemory = SystemInfo.SYSTEM_INFO!!.ram > 256
						} catch (@Pc(158) ex: Throwable) {
						}
						@Pc(164) val memoryPool: MemoryPool?
						if (highMemory) {
							memoryPool = ClientState.activeToolkit!!.createHeap(146800640)
						} else {
							memoryPool = ClientState.activeToolkit!!.createHeap(104857600)
						}
						ClientState.activeToolkit!!.setHeap(memoryPool)
						var14 = false
					} else {
						var14 = false
					}
					return@label216
				} catch (@Pc(175) ex2: Throwable) {
					if (toolkitId == ToolkitType.HD) {
						// Not part of the original client: 634HD falls back to OpenGL, and from there, as
						// any toolkit does, to the Java toolkit; the player is told why (HdFallback).
						HdFallback.failed(ex2)
						GamePreferences.uid!!.setValue(GamePreferences.uid!!.toolkit, 0)
						prepareForRendering(string, ToolkitType.GL, skipSettingsVerification)
						var14 = false
					} else {
						@Pc(180) val toolkitSetting: Int = GamePreferences.uid!!.toolkit!!.getValue()
						if (toolkitSetting == 2) {
							ToolkitOption.renderWithCpu = true
						}
						GamePreferences.uid!!.setValue(GamePreferences.uid!!.toolkit, 0)
						prepareForRendering(string, toolkitSetting, skipSettingsVerification)
						@Pc(199) val obj: Any? = null
						var14 = false
					}
				} finally {
					if (var14) {
						font2 = null
						if (toolkit != null) {
							try {
								toolkit!!.free()
							} catch (@Pc(345) ex3: Throwable) {
							}
						}
					}
				}
				if (toolkit != null) {
					try {
						toolkit!!.free()
					} catch (@Pc(325) ex4: Throwable) {
					}
				}
				return
			}
			font2 = null
			if (toolkit != null) {
				try {
					toolkit!!.free()
				} catch (@Pc(335) ex5: Throwable) {
				}
			}
		}
		GamePreferences.uid!!.toolkit!!.setActive(!skipSettingsVerification)
		GamePreferences.uid!!.setValue(GamePreferences.uid!!.toolkit, toolkitId)
		Renderers.freeGpuItemRenderer()
		ClientState.activeToolkit!!.initialize()
		ClientState.activeToolkit!!.X(32)
		ClientState.aClass30_3 = ClientState.activeToolkit!!.createMatrix()
		InterfaceComponentRenderer.aClass30_6 = ClientState.activeToolkit!!.createMatrix()
		MapRegion.calculateViewRange()
		ClientState.activeToolkit!!.setShrinkTextures(GamePreferences.uid!!.smallTextures!!.getValue() == 1)
		if (ClientState.activeToolkit!!.supportsBloom()) {
			GamePreferences.setBloom(GamePreferences.uid!!.bloom!!.getValue() == 1)
		}
		EnvironmentManager.initialize(MapRegion.sceneSizeX shr 3, ClientState.activeToolkit, MapRegion.sceneSizeY shr 3)
		EnvironmentManager.requestDirectEnvironmentTransition()
		GamePreferences.preferencesSavedOnServer = false
		Fullscreen.aClass25Array1 = null
		WindowMode.needsResize = true
		OrthoMode.applyOrthographicMode()
	}
}
