import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.client.window.WindowModeType
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.ServerConnectionReader
import com.jagex.game.runetek5.preference.GamePreferences
import java.awt.Container
import java.awt.Insets
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.ModeWhere
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.network.Connection
import com.jagex.game.runetek5.MainLogicManager
import com.jagex.game.runetek5.client.ClientState

object WindowModeApply {

	@OriginalMember(owner = "client!rs", name = "a", descriptor = "(BZIIII)V")
	@JvmStatic
	fun applyWindowMode(@OriginalArg(1) replaceCanvas: Boolean, @OriginalArg(2) width: Int, @OriginalArg(3) currentMode: WindowModeType?, @OriginalArg(4) requestedMode: WindowModeType?, @OriginalArg(5) height: Int) {
		if (WindowMode.fullScreenFrame != null && (requestedMode !== WindowModeType.FULLSCREEN || width != WindowMode.fullScreenWidth || height != WindowMode.fullScreenHeight)) {
			InterfaceManager.disposeFrame(WindowMode.fullScreenFrame, GameShell.signLink)
			WindowMode.fullScreenFrame = null
		}
		if (requestedMode === WindowModeType.FULLSCREEN && WindowMode.fullScreenFrame == null) {
			WindowMode.fullScreenFrame = InterfaceManager.calculateFullscreenFrame(height, width, GameShell.signLink, 0)
			if (WindowMode.fullScreenFrame != null) {
				WindowMode.fullScreenWidth = width
				WindowMode.fullScreenHeight = height
				GamePreferences.savePreferences()
			}
		}
		if (requestedMode === WindowModeType.FULLSCREEN && WindowMode.fullScreenFrame == null) {
			applyWindowMode(true, -1, currentMode, WindowModeType.fromId(GamePreferences.uid!!.fixedScreen!!.getValue()), -1)
			return
		}
		@Pc(77) val container: Container?
		@Pc(102) var insets: Insets
		if (WindowMode.fullScreenFrame != null) {
			WindowMode.resizableScreenHeight = height
			WindowMode.resizableScreenWidth = width
			container = WindowMode.fullScreenFrame
		} else if (GameShell.engineFrame == null) {
			if (GameShell.applet == null) {
				container = GameShell.anApplet_Sub1_1
			} else {
				container = GameShell.applet
			}
			WindowMode.resizableScreenWidth = container!!.getSize().width
			WindowMode.resizableScreenHeight = container.getSize().height
		} else {
			insets = GameShell.engineFrame!!.getInsets()
			@Pc(108) val negLeftInset: Int = -insets.left
			WindowMode.resizableScreenWidth = GameShell.engineFrame!!.getSize().width + negLeftInset - insets.right
			@Pc(123) val verticalInsets: Int = insets.top + insets.bottom
			WindowMode.resizableScreenHeight = GameShell.engineFrame!!.getSize().height - verticalInsets
			// Not part of the original client: with client.scale, the window is measured in game pixels.
			WindowMode.resizableScreenWidth = ClientFeatures.unscale(WindowMode.resizableScreenWidth)
			WindowMode.resizableScreenHeight = ClientFeatures.unscale(WindowMode.resizableScreenHeight)
			container = GameShell.engineFrame
		}
		if (requestedMode === WindowModeType.FIXED) {
			GameShell.screenCanvasWidth = GameShell.GAME_WIDTH
			GameShell.screenCanvasHeight = MessageBox.paddingTop
			MiniMenu.gameCanvasX = (WindowMode.resizableScreenWidth - GameShell.GAME_WIDTH) / 2
			GameShell.gameCanvasY = 0
		} else {
			InterfaceManager.canvasAdjustment()
		}
		if (ModeWhere.WTRC !== ServerEnvironment.LIVE) {
			@Pc(161) val smallCanvas: Boolean
			if (GameShell.screenCanvasWidth < 1024 && GameShell.screenCanvasHeight < 768) {
				smallCanvas = true
			} else {
				smallCanvas = false
			}
		}
		if (replaceCanvas) {
			CanvasReplace.exitFullscreen()
		} else {
			GameShell.gameCanvas!!.setSize(GameShell.screenCanvasWidth, GameShell.screenCanvasHeight)
			if (OrthoMode.orthoEnabled) {
				OrthoMode.resizeOrthoCanvas(GameShell.gameCanvas)
			} else {
				ClientState.activeToolkit!!.resizeCanvas(GameShell.gameCanvas, GameShell.screenCanvasWidth, GameShell.screenCanvasHeight)
			}
			if (GameShell.engineFrame === container) {
				insets = GameShell.engineFrame!!.getInsets()
				// Not part of the original client: with client.scale, the game offsets are in game pixels.
				GameShell.gameCanvas!!.setLocation(ClientFeatures.scale(MiniMenu.gameCanvasX) + insets.left, insets.top - -ClientFeatures.scale(GameShell.gameCanvasY))
			} else {
				GameShell.gameCanvas!!.setLocation(ClientFeatures.scale(MiniMenu.gameCanvasX), ClientFeatures.scale(GameShell.gameCanvasY))
			}
		}
		if (requestedMode === WindowModeType.FIXED) {
			WindowMode.resizableMode = false
		} else {
			WindowMode.resizableMode = true
		}
		if (ClientState.BASE_WINDOW_ID != -1) {
			WindowMode.interfaceResize(true)
		}
		if (Connection.connection != null && MainLogicManager.isInGameStep(ClientState.mainLogicStep)) {
			ServerConnectionReader.sendWindowmodePreferenceToServer()
		}
		@Pc(235) var i: Int = 0
		while (i < 100) {
			InterfaceRectangles.IS_COMPONENT_ON_TOP[i] = true
			i++
		}
		GameShell.fullRedrawRequested = true
	}
}
