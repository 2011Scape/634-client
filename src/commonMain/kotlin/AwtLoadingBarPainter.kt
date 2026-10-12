import awt.Color
import awt.Font
import awt.Graphics
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.GameShell
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import awt.getGraphics

object AwtLoadingBarPainter {

	@OriginalMember(owner = "client!rc", name = "a", descriptor = "(ILjava/awt/Color;Ljava/lang/String;Ljava/awt/Color;BLjava/awt/Color;)V")
	@JvmStatic
	fun drawPlainLoadingBar(@OriginalArg(0) loadingPercentage: Int, @OriginalArg(1) color: Color?, @OriginalArg(2) string: String?, @OriginalArg(3) color2: Color?, @OriginalArg(5) color3: Color?) {
		var color: Color? = color
		var color2: Color? = color2
		var color3: Color? = color3
		try {
			@Pc(6) val graphics: Graphics = GameShell.gameCanvas!!.getGraphics()!!
			if (AwtLoadingBarPainter.loadingFont == null) {
				AwtLoadingBarPainter.loadingFont = Font("Helvetica", 1, 13)
			}
			if (color == null) {
				color = Color(140, 17, 17)
			}
			if (color2 == null) {
				color2 = Color(140, 17, 17)
			}
			if (color3 == null) {
				color3 = Color(255, 255, 255)
			}
			@Pc(67) var local67: Int
			try {
				if (Static658.canvasImage == null) {
					Static658.canvasImage = GameShell.gameCanvas!!.createImage(GameShell.screenCanvasWidth, GameShell.screenCanvasHeight)
				}
				@Pc(52) val graphics2: Graphics = Static658.canvasImage!!.getGraphics()
				graphics2.setColor(awt.COLOR_BLACK)
				graphics2.fillRect(0, 0, GameShell.screenCanvasWidth, GameShell.screenCanvasHeight)
				val barX: Int = GameShell.screenCanvasWidth / 2 - 152
				@Pc(73) val y: Int = GameShell.screenCanvasHeight / 2 - 18
				graphics2.setColor(color2)
				graphics2.drawRect(barX, y, 303, 33)
				graphics2.setColor(color)
				graphics2.fillRect(barX + 2, y - -2, loadingPercentage * 3, 30)
				graphics2.setColor(awt.COLOR_BLACK)
				graphics2.drawRect(barX + 1, y + 1, 301, 31)
				graphics2.fillRect(loadingPercentage * 3 + barX + 2, y + 2, 300 - loadingPercentage * 3, 30)
				graphics2.setFont(AwtLoadingBarPainter.loadingFont)
				graphics2.setColor(color3)
				graphics2.drawString(string, barX + (304 - string!!.length * 6) / 2, y + 22)
				if (AwtLoadingBarPainter.loadingText != null) {
					graphics2.setFont(AwtLoadingBarPainter.loadingFont)
					graphics2.setColor(color3)
					graphics2.drawString(AwtLoadingBarPainter.loadingText, GameShell.screenCanvasWidth / 2 - AwtLoadingBarPainter.loadingText!!.length * 6 / 2, GameShell.screenCanvasHeight / 2 + -26)
				}
				graphics.drawImage(Static658.canvasImage, 0, 0, null)
			} catch (@Pc(190) ex: Exception) {
				graphics.setColor(awt.COLOR_BLACK)
				graphics.fillRect(0, 0, GameShell.screenCanvasWidth, GameShell.screenCanvasHeight)
				@Pc(205) val x: Int = GameShell.screenCanvasWidth / 2 - 152
				val barY: Int = GameShell.screenCanvasHeight / 2 - 18
				graphics.setColor(color2)
				graphics.drawRect(x, barY, 303, 33)
				graphics.setColor(color)
				graphics.fillRect(x + 2, barY + 2, loadingPercentage * 3, 30)
				graphics.setColor(awt.COLOR_BLACK)
				graphics.drawRect(x + 1, barY + 1, 301, 31)
				graphics.fillRect(loadingPercentage * 3 + x + 2, barY + 2, 300 - loadingPercentage * 3, 30)
				graphics.setFont(AwtLoadingBarPainter.loadingFont)
				graphics.setColor(color3)
				if (AwtLoadingBarPainter.loadingText != null) {
					graphics.setFont(AwtLoadingBarPainter.loadingFont)
					graphics.setColor(color3)
					graphics.drawString(AwtLoadingBarPainter.loadingText, GameShell.screenCanvasWidth / 2 - AwtLoadingBarPainter.loadingText!!.length * 6 / 2, GameShell.screenCanvasHeight / 2 - 26)
				}
				graphics.drawString(string, x + (304 - string!!.length * 6) / 2, barY + 22)
			}
		} catch (@Pc(320) ex2: Exception) {
			GameShell.gameCanvas!!.repaint()
		}
	}
	@OriginalMember(owner = "client!ffa", name = "B", descriptor = "Ljava/lang/String;")
	@JvmField
	var loadingText: String? = null
	@OriginalMember(owner = "client!ej", name = "h", descriptor = "Ljava/awt/Font;")
	@JvmField
	var loadingFont: Font? = null
}
