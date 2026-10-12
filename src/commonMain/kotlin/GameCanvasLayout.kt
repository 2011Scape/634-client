import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.client.window.WindowModeType
import com.jagex.game.runetek5.client.menu.MiniMenu
import awt.Container
import awt.Insets
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.features.ClientFeatures
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.window.WindowMode
import com.jagex.game.runetek5.ModeWhere
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic
import withLock
import awt.getInsets
import awt.height
import awt.width

object GameCanvasLayout {

	@OriginalMember(owner = "client!or", name = "b", descriptor = "(Z)V")
	@JvmStatic
	fun adjustActiveWindow() {
		@Pc(11) val activeClient: client? = ActiveClient.activeClient
		withLock(ActiveClient.activeClient!!) {
			if (WindowMode.fullScreenFrame == null) {
				@Pc(22) val container: Container?
				if (GameShell.engineFrame != null) {
					container = GameShell.engineFrame
				} else if (GameShell.applet == null) {
					container = GameShell.anApplet_Sub1_1
				} else {
					container = GameShell.applet
				}
				WindowMode.resizableScreenWidth = container!!.getSize().width
				WindowMode.resizableScreenHeight = container.getSize().height
				@Pc(46) var insets: Insets
				if (GameShell.engineFrame === container) {
					insets = GameShell.engineFrame!!.getInsets()
					WindowMode.resizableScreenWidth -= insets.left + insets.right
					WindowMode.resizableScreenHeight -= insets.bottom + insets.top
					// Not part of the original client: with client.scale, the window is measured in game pixels.
					WindowMode.resizableScreenWidth = ClientFeatures.unscale(WindowMode.resizableScreenWidth)
					WindowMode.resizableScreenHeight = ClientFeatures.unscale(WindowMode.resizableScreenHeight)
				}
				if (WindowMode.getWindowMode() == WindowModeType.FIXED) {
					GameShell.screenCanvasWidth = GameShell.GAME_WIDTH
					GameShell.gameCanvasY = 0
					MiniMenu.gameCanvasX = (WindowMode.resizableScreenWidth - GameShell.GAME_WIDTH) / 2
					GameShell.screenCanvasHeight = MessageBox.paddingTop
				} else {
					InterfaceManager.canvasAdjustment()
				}
				if (ModeWhere.WTRC !== ServerEnvironment.LIVE) {
					@Pc(95) val smallCanvas: Boolean
					if (GameShell.screenCanvasWidth < 1024 && GameShell.screenCanvasHeight < 768) {
						smallCanvas = true
					} else {
						smallCanvas = false
					}
				}
				GameShell.gameCanvas!!.setSize(GameShell.screenCanvasWidth, GameShell.screenCanvasHeight)
				if (ClientState.activeToolkit != null) {
					if (OrthoMode.orthoEnabled) {
						OrthoMode.resizeOrthoCanvas(GameShell.gameCanvas)
					} else {
						ClientState.activeToolkit!!.resizeCanvas(GameShell.gameCanvas, GameShell.screenCanvasWidth, GameShell.screenCanvasHeight)
					}
				}
				if (GameShell.engineFrame === container) {
					insets = GameShell.engineFrame!!.getInsets()
					// Not part of the original client: with client.scale, the game offsets are in game pixels.
				GameShell.gameCanvas!!.setLocation(insets.left + ClientFeatures.scale(MiniMenu.gameCanvasX), ClientFeatures.scale(GameShell.gameCanvasY) + insets.top)
				} else {
					GameShell.gameCanvas!!.setLocation(ClientFeatures.scale(MiniMenu.gameCanvasX), ClientFeatures.scale(GameShell.gameCanvasY))
				}
				if (ClientState.BASE_WINDOW_ID != -1) {
					WindowMode.interfaceResize(true)
				}
				GameShell.clearCanvasBackground()
			}
		}
	}
}
