import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.lang.reflect.Field;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.loginscreen.LoginScreenComponent;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!wba")
public final class AwtLoadingScreen implements LoginScreenComponent {

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "Z")
	private boolean drawInCenterX;

	@OriginalMember(owner = "client!wba", name = "b", descriptor = "I")
	private int xOffset;

	@OriginalMember(owner = "client!wba", name = "c", descriptor = "Z")
	private boolean fallback;

	@OriginalMember(owner = "client!wba", name = "d", descriptor = "I")
	private int width;

	@OriginalMember(owner = "client!wba", name = "e", descriptor = "Ljava/awt/FontMetrics;")
	private FontMetrics bodyFontMetrics;

	@OriginalMember(owner = "client!wba", name = "g", descriptor = "Ljava/awt/Image;")
	private Image bodyRight;

	@OriginalMember(owner = "client!wba", name = "j", descriptor = "I")
	private int boxXOffset;

	@OriginalMember(owner = "client!wba", name = "k", descriptor = "Ljava/awt/Image;")
	private Image bodyLeft;

	@OriginalMember(owner = "client!wba", name = "m", descriptor = "Ljava/awt/Image;")
	private Image left;

	@OriginalMember(owner = "client!wba", name = "o", descriptor = "Ljava/awt/Color;")
	private Color textColour;

	@OriginalMember(owner = "client!wba", name = "p", descriptor = "Ljava/awt/Image;")
	private Image background;

	@OriginalMember(owner = "client!wba", name = "q", descriptor = "I")
	private int height;

	@OriginalMember(owner = "client!wba", name = "r", descriptor = "Ljava/awt/Image;")
	private Image right;

	@OriginalMember(owner = "client!wba", name = "u", descriptor = "I")
	private int boxYOffset;

	@OriginalMember(owner = "client!wba", name = "v", descriptor = "Ljava/awt/Image;")
	private Image bottom;

	@OriginalMember(owner = "client!wba", name = "w", descriptor = "I")
	private int offsetPerTenCycles;

	@OriginalMember(owner = "client!wba", name = "z", descriptor = "Z")
	private boolean drawInCenterY;

	@OriginalMember(owner = "client!wba", name = "A", descriptor = "I")
	private int textYOffset;

	@OriginalMember(owner = "client!wba", name = "B", descriptor = "Ljava/awt/Image;")
	private Image bar;

	@OriginalMember(owner = "client!wba", name = "E", descriptor = "I")
	private int boxWidth;

	@OriginalMember(owner = "client!wba", name = "F", descriptor = "I")
	private int yOffset;

	@OriginalMember(owner = "client!wba", name = "H", descriptor = "Ljava/awt/Font;")
	private Font bodyFont;

	@OriginalMember(owner = "client!wba", name = "I", descriptor = "Ljava/awt/Image;")
	private Image top;

	@OriginalMember(owner = "client!wba", name = "K", descriptor = "Ljava/awt/Image;")
	private Image bodyFill;

	@OriginalMember(owner = "client!wba", name = "N", descriptor = "Ljava/awt/Image;")
	private Image image;

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(Z)V")
	private void load() throws IllegalAccessException, NoSuchFieldException {
		@Pc(6) Class appletClass = GameShell.applet.getClass();
		this.bar = (Image) this.getObject(appletClass, "bar");
		this.background = (Image) this.getObject(appletClass, "background");
		this.left = (Image) this.getObject(appletClass, "left");
		this.right = (Image) this.getObject(appletClass, "right");
		this.top = (Image) this.getObject(appletClass, "top");
		this.bottom = (Image) this.getObject(appletClass, "bottom");
		this.bodyLeft = (Image) this.getObject(appletClass, "bodyLeft");
		this.bodyRight = (Image) this.getObject(appletClass, "bodyRight");
		this.bodyFill = (Image) this.getObject(appletClass, "bodyFill");
		this.bodyFont = (Font) this.getObject(appletClass, "bf");
		this.bodyFontMetrics = (FontMetrics) this.getObject(appletClass, "bfm");
		this.textColour = (Color) this.getObject(appletClass, "colourtext");
		@Pc(108) Object object = this.getObject(appletClass, "lb");
		@Pc(111) Class lbClass = object.getClass();
		this.drawInCenterX = this.getBooleanField(object, lbClass, "xMiddle");
		this.drawInCenterY = this.getBooleanField(object, lbClass, "yMiddle");
		this.xOffset = this.getIntField(object, lbClass, "xOffset");
		this.yOffset = this.getIntField(object, lbClass, "yOffset");
		this.width = this.getIntField(object, lbClass, "width");
		this.height = this.getIntField(object, lbClass, "height");
		this.boxXOffset = this.getIntField(object, lbClass, "boxXOffset");
		this.boxYOffset = this.getIntField(object, lbClass, "boxYOffset");
		this.boxWidth = this.getIntField(object, lbClass, "boxWidth");
		this.textYOffset = this.getIntField(object, lbClass, "textYOffset");
		this.offsetPerTenCycles = this.getIntField(object, lbClass, "offsetPerTenCycles");
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;I)I")
	private int getIntField(@OriginalArg(0) Object object, @OriginalArg(1) Class class2, @OriginalArg(2) String string) throws IllegalAccessException, NoSuchFieldException {
		@Pc(15) Field field = class2.getDeclaredField(string);
		return field.getInt(object);
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(Ljava/lang/Class;ILjava/lang/String;)Ljava/lang/Object;")
	private Object getObject(@OriginalArg(0) Class clazz, @OriginalArg(2) String string) throws IllegalAccessException, NoSuchFieldException {
		@Pc(12) Field field = clazz.getDeclaredField(string);
		@Pc(16) Object object = field.get(GameShell.applet);
		field.set(GameShell.applet, null);
		return object;
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(B)I")
	@Override
	public int getFadeDuration() {
		return 0;
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(BJ)Z")
	@Override
	public boolean finished(@OriginalArg(1) long initTime) {
		return true;
	}

	@OriginalMember(owner = "client!wba", name = "c", descriptor = "(I)I")
	@Override
	public int percentage() {
		return 100;
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(BI)I")
	private int getDrawX(@OriginalArg(1) int width) {
		return this.drawInCenterX ? (GameShell.screenCanvasWidth - width) / 2 : 0;
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(I)V")
	@Override
	public void init() {
	}

	@OriginalMember(owner = "client!wba", name = "b", descriptor = "(I)V")
	@Override
	public void cleanup() {
		AwtLoadingBarReset.cleanupPlainLoadingBarResources();
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(Ljava/lang/Object;ZLjava/lang/Class;Ljava/lang/String;)Z")
	private boolean getBooleanField(@OriginalArg(0) Object object, @OriginalArg(2) Class class2, @OriginalArg(3) String string) throws IllegalAccessException, NoSuchFieldException {
		@Pc(15) Field field = class2.getDeclaredField(string);
		return field.getBoolean(object);
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(ZI)V")
	@Override
	public void render(@OriginalArg(0) boolean fullRepaint) {
		if (!this.fallback) {
			if (GameShell.applet == null) {
				this.fallback = true;
			} else if (this.bodyFont == null) {
				try {
					this.load();
				} catch (@Pc(31) Exception ex) {
					this.fallback = true;
				}
			}
		}
		if (this.fallback) {
			this.drawPlainLoadingBar();
			return;
		}
		@Pc(45) Graphics graphics = GameShell.gameCanvas.getGraphics();
		if (graphics == null) {
			GameShell.gameCanvas.repaint();
			return;
		}
		try {
			@Pc(51) int loadingPercentage = ActiveLoadingScreenRenderer.aClass8_1.getLoadingPercentage();
			@Pc(55) String string = ActiveLoadingScreenRenderer.aClass8_1.getLoadingText();
			if (Static658.canvasImage == null) {
				Static658.canvasImage = GameShell.gameCanvas.createImage(GameShell.screenCanvasWidth, GameShell.screenCanvasHeight);
			}
			@Pc(65) Graphics graphics2 = Static658.canvasImage.getGraphics();
			graphics2.clearRect(0, 0, GameShell.screenCanvasWidth, GameShell.screenCanvasHeight);
			@Pc(76) int bodyLeftWidth = this.bodyLeft.getWidth(null);
			@Pc(81) int bodyRightWidth = this.bodyRight.getWidth(null);
			@Pc(86) int bodyFillWidth = this.bodyFill.getWidth(null);
			@Pc(91) int bodyLeftHeight = this.bodyLeft.getHeight(null);
			@Pc(96) int bodyRightHeight = this.bodyRight.getHeight(null);
			@Pc(101) int bodyFillHeight = this.bodyFill.getHeight(null);
			graphics2.drawImage(this.bodyLeft, this.getDrawX(bodyLeftWidth) + this.boxXOffset - this.boxWidth / 2, this.getDrawY(bodyLeftHeight) + this.boxYOffset, null);
			@Pc(138) int fillStartX = bodyLeftWidth + this.boxXOffset - this.boxWidth / 2;
			@Pc(146) int fillEndX = this.boxXOffset + this.boxWidth / 2;
			for (@Pc(148) int i = fillStartX; i <= fillEndX; i += bodyFillWidth) {
				graphics2.drawImage(this.bodyFill, i + this.getDrawX(bodyLeftWidth) + this.boxXOffset, this.getDrawY(bodyFillHeight) + this.boxYOffset, null);
			}
			graphics2.drawImage(this.bodyRight, this.getDrawX(bodyRightWidth) + this.boxXOffset + this.boxWidth / 2, this.getDrawY(bodyRightHeight) - -this.boxYOffset, null);
			@Pc(216) int leftWidth = this.left.getWidth(null);
			@Pc(221) int leftHeight = this.left.getHeight(null);
			@Pc(226) int rightWidth = this.right.getWidth(null);
			@Pc(231) int rightHeight = this.right.getHeight(null);
			@Pc(236) int bottomHeight = this.bottom.getHeight(null);
			@Pc(241) int topWidth = this.top.getWidth(null);
			@Pc(246) int topHeight = this.top.getHeight(null);
			@Pc(251) int bottomWidth = this.bottom.getWidth(null);
			@Pc(256) int barWidth = this.bar.getWidth(null);
			@Pc(261) int backgroundWidth = this.background.getWidth(null);
			@Pc(270) int barX = this.getDrawX(this.width) + this.xOffset;
			@Pc(280) int barY = this.getDrawY(this.height) + this.yOffset;
			graphics2.drawImage(this.left, barX, barY + (this.height - leftHeight) / 2, null);
			graphics2.drawImage(this.right, this.width + barX - rightWidth, (this.height - rightHeight) / 2 + barY, null);
			if (this.image == null) {
				this.image = GameShell.gameCanvas.createImage(this.width - rightWidth - leftWidth, this.height);
			}
			@Pc(338) Graphics graphics3 = this.image.getGraphics();
			for (@Pc(340) int j = 0; j < this.width - rightWidth - leftWidth; j += topWidth) {
				graphics3.drawImage(this.top, j, 0, null);
			}
			for (@Pc(370) int k = 0; k < this.width - leftWidth - rightWidth; k += bottomWidth) {
				graphics3.drawImage(this.bottom, k, this.height - bottomHeight, null);
			}
			@Pc(413) int segmentWidth = loadingPercentage * (this.width - rightWidth - leftWidth) / 100;
			@Pc(452) int tileX;
			if (segmentWidth > 0) {
				@Pc(430) Image image2 = GameShell.gameCanvas.createImage(segmentWidth, this.height - topHeight - bottomHeight);
				@Pc(434) int completedImageWidth = image2.getWidth(null);
				@Pc(437) Graphics graphics4 = image2.getGraphics();
				@Pc(447) int scrollOffset = this.offsetPerTenCycles * LoadingScreenCycleCount.getCycle() / 10 % barWidth;
				for (tileX = scrollOffset - barWidth; tileX < completedImageWidth; tileX += barWidth) {
					graphics4.drawImage(this.bar, tileX, 0, null);
				}
				graphics3.drawImage(image2, 0, topHeight, null);
			}
			@Pc(482) int completedWidth = segmentWidth;
			segmentWidth = this.width - segmentWidth - rightWidth - leftWidth;
			if (segmentWidth > 0) {
				@Pc(508) Image image3 = GameShell.gameCanvas.createImage(segmentWidth, this.height - topHeight - bottomHeight);
				@Pc(512) int remainingImageWidth = image3.getWidth(null);
				@Pc(515) Graphics graphics5 = image3.getGraphics();
				for (tileX = 0; tileX < remainingImageWidth; tileX += backgroundWidth) {
					graphics5.drawImage(this.background, tileX, 0, null);
				}
				graphics3.drawImage(image3, completedWidth, topHeight, null);
			}
			graphics2.drawImage(this.image, leftWidth + barX, barY, null);
			graphics2.setFont(this.bodyFont);
			graphics2.setColor(this.textColour);
			graphics2.drawString(string, barX + (this.width - this.bodyFontMetrics.stringWidth(string)) / 2, this.textYOffset + 4 + this.height / 2 + barY);
			graphics.drawImage(Static658.canvasImage, 0, 0, null);
		} catch (@Pc(594) Exception ex2) {
			this.fallback = true;
		}
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(II)I")
	private int getDrawY(@OriginalArg(0) int height) {
		return this.drawInCenterY ? (GameShell.screenCanvasHeight - height) / 2 : 0;
	}

	@OriginalMember(owner = "client!wba", name = "e", descriptor = "(I)V")
	private void drawPlainLoadingBar() {
		AwtLoadingBarPainter.drawPlainLoadingBar(ActiveLoadingScreenRenderer.aClass8_1.getLoadingPercentage(), AwtLoadingScreen.DEBUG_LOAD_COLOR[AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX], ActiveLoadingScreenRenderer.aClass8_1.getLoadingText(), AwtLoadingScreen.DEBUG_LOAD_OUTLINE_COLOR[AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX], AwtLoadingScreen.DEBUG_LOAD_TEXT_COLOR[AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX]);
	}
	@OriginalMember(owner = "client!bi", name = "h", descriptor = "[Ljava/awt/Color;")
	public static final Color[] DEBUG_LOAD_COLOR = new Color[] { new Color(9179409), new Color(3289650), new Color(3289650), new Color(3289650) };

	@OriginalMember(owner = "client!sda", name = "b", descriptor = "I")
	public static int DEBUG_DISPLAY_COLOR_INDEX = 0;
	@OriginalMember(owner = "client!hc", name = "l", descriptor = "[Ljava/awt/Color;")
	public static final Color[] DEBUG_LOAD_TEXT_COLOR = new Color[] { new Color(16777215), new Color(16777215), new Color(16741381), new Color(16741381) };

	@OriginalMember(owner = "client!jq", name = "X", descriptor = "[Ljava/awt/Color;")
	public static final Color[] DEBUG_LOAD_OUTLINE_COLOR = new Color[] { new Color(9179409), new Color(16777215), new Color(16726277), new Color(16726277) };
}
