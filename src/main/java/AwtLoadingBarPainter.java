import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.GameShell;

public final class AwtLoadingBarPainter {

	@OriginalMember(owner = "client!rc", name = "a", descriptor = "(ILjava/awt/Color;Ljava/lang/String;Ljava/awt/Color;BLjava/awt/Color;)V")
	public static void drawPlainLoadingBar(@OriginalArg(0) int loadingPercentage, @OriginalArg(1) Color color, @OriginalArg(2) String string, @OriginalArg(3) Color color2, @OriginalArg(5) Color color3) {
		try {
			@Pc(6) Graphics graphics = GameShell.gameCanvas.getGraphics();
			if (AwtLoadingBarPainter.loadingFont == null) {
				AwtLoadingBarPainter.loadingFont = new Font("Helvetica", 1, 13);
			}
			if (color == null) {
				color = new Color(140, 17, 17);
			}
			if (color2 == null) {
				color2 = new Color(140, 17, 17);
			}
			if (color3 == null) {
				color3 = new Color(255, 255, 255);
			}
			@Pc(67) int local67;
			try {
				if (Static658.canvasImage == null) {
					Static658.canvasImage = GameShell.gameCanvas.createImage(GameShell.screenCanvasWidth, GameShell.screenCanvasHeight);
				}
				@Pc(52) Graphics graphics2 = Static658.canvasImage.getGraphics();
				graphics2.setColor(Color.black);
				graphics2.fillRect(0, 0, GameShell.screenCanvasWidth, GameShell.screenCanvasHeight);
				int barX = GameShell.screenCanvasWidth / 2 - 152;
				@Pc(73) int y = GameShell.screenCanvasHeight / 2 - 18;
				graphics2.setColor(color2);
				graphics2.drawRect(barX, y, 303, 33);
				graphics2.setColor(color);
				graphics2.fillRect(barX + 2, y - -2, loadingPercentage * 3, 30);
				graphics2.setColor(Color.black);
				graphics2.drawRect(barX + 1, y + 1, 301, 31);
				graphics2.fillRect(loadingPercentage * 3 + barX + 2, y + 2, 300 - loadingPercentage * 3, 30);
				graphics2.setFont(AwtLoadingBarPainter.loadingFont);
				graphics2.setColor(color3);
				graphics2.drawString(string, barX + (304 - string.length() * 6) / 2, y + 22);
				if (AwtLoadingBarPainter.loadingText != null) {
					graphics2.setFont(AwtLoadingBarPainter.loadingFont);
					graphics2.setColor(color3);
					graphics2.drawString(AwtLoadingBarPainter.loadingText, GameShell.screenCanvasWidth / 2 - AwtLoadingBarPainter.loadingText.length() * 6 / 2, GameShell.screenCanvasHeight / 2 + -26);
				}
				graphics.drawImage(Static658.canvasImage, 0, 0, null);
			} catch (@Pc(190) Exception ex) {
				graphics.setColor(Color.black);
				graphics.fillRect(0, 0, GameShell.screenCanvasWidth, GameShell.screenCanvasHeight);
				@Pc(205) int x = GameShell.screenCanvasWidth / 2 - 152;
				int barY = GameShell.screenCanvasHeight / 2 - 18;
				graphics.setColor(color2);
				graphics.drawRect(x, barY, 303, 33);
				graphics.setColor(color);
				graphics.fillRect(x + 2, barY + 2, loadingPercentage * 3, 30);
				graphics.setColor(Color.black);
				graphics.drawRect(x + 1, barY + 1, 301, 31);
				graphics.fillRect(loadingPercentage * 3 + x + 2, barY + 2, 300 - loadingPercentage * 3, 30);
				graphics.setFont(AwtLoadingBarPainter.loadingFont);
				graphics.setColor(color3);
				if (AwtLoadingBarPainter.loadingText != null) {
					graphics.setFont(AwtLoadingBarPainter.loadingFont);
					graphics.setColor(color3);
					graphics.drawString(AwtLoadingBarPainter.loadingText, GameShell.screenCanvasWidth / 2 - AwtLoadingBarPainter.loadingText.length() * 6 / 2, GameShell.screenCanvasHeight / 2 - 26);
				}
				graphics.drawString(string, x + (304 - string.length() * 6) / 2, barY + 22);
			}
		} catch (@Pc(320) Exception ex2) {
			GameShell.gameCanvas.repaint();
		}
	}
	@OriginalMember(owner = "client!ffa", name = "B", descriptor = "Ljava/lang/String;")
	public static String loadingText = null;
	@OriginalMember(owner = "client!ej", name = "h", descriptor = "Ljava/awt/Font;")
	public static Font loadingFont;
}
