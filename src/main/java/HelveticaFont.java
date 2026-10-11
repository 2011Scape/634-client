import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.statics.ValidCharacterCount;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.GameShell;

@OriginalClass("client!cs")
public final class HelveticaFont {

	@OriginalMember(owner = "client!cs", name = "g", descriptor = "[I")
	private static final int[] VALID_CHARACTERS = new int[256];

	@OriginalMember(owner = "client!cs", name = "d", descriptor = "I")
	private int startY;

	@OriginalMember(owner = "client!cs", name = "e", descriptor = "I")
	private int sizeY;

	@OriginalMember(owner = "client!cs", name = "a", descriptor = "Z")
	private boolean loadPlain = false;

	@OriginalMember(owner = "client!cs", name = "h", descriptor = "[I")
	private final int[] clipping = new int[4];

	@OriginalMember(owner = "client!cs", name = "f", descriptor = "[Lclient!mi;")
	private final NativeSprite[] nativeSprites;

	@OriginalMember(owner = "client!cs", name = "c", descriptor = "[I")
	private final int[] characterWidths;

	static {
		for (@Pc(7) int charCode = 0; charCode < 256; charCode++) {
			@Pc(12) int index = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ÄËÏÖÜäëïöüÿßÁÀÉÈÍÌÓÒÚÙáàéèíìóòúùÂÊÎÔÛâêîôûÆæãÃõÕçÇ".indexOf(charCode);
			if (index == -1) {
				index = 74;
			}
			VALID_CHARACTERS[charCode] = index;
		}
	}

	@OriginalMember(owner = "client!cs", name = "<init>", descriptor = "(Lclient!ha;IZLjava/awt/Component;)V")
	public HelveticaFont(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int size, @OriginalArg(2) boolean arg2, @OriginalArg(3) java.awt.Component component) {
		this.loadPlain = false;
		this.nativeSprites = new NativeSprite[256];
		this.characterWidths = new int[256];
		@Pc(30) Font font = new Font("Helvetica", 1, size);
		@Pc(34) FontMetrics fontMetrics = component.getFontMetrics(font);
		for (@Pc(36) int i = 0; i < ValidCharacterCount.VALID_CHARACTER_COUNT; i++) {
			this.load(toolkit, font, fontMetrics, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ÄËÏÖÜäëïöüÿßÁÀÉÈÍÌÓÒÚÙáàéèíìóòúùÂÊÎÔÛâêîôûÆæãÃõÕçÇ".charAt(i), i, false);
		}
		if (this.loadPlain) {
			this.loadPlain = false;
			font = new Font("Helvetica", 0, size);
			fontMetrics = component.getFontMetrics(font);
			for (@Pc(72) int j = 0; j < ValidCharacterCount.VALID_CHARACTER_COUNT; j++) {
				this.load(toolkit, font, fontMetrics, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ÄËÏÖÜäëïöüÿßÁÀÉÈÍÌÓÒÚÙáàéèíìóòúùÂÊÎÔÛâêîôûÆæãÃõÕçÇ".charAt(j), j, false);
			}
			if (!this.loadPlain) {
				this.loadPlain = false;
				for (@Pc(95) int k = 0; k < ValidCharacterCount.VALID_CHARACTER_COUNT; k++) {
					this.load(toolkit, font, fontMetrics, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ÄËÏÖÜäëïöüÿßÁÀÉÈÍÌÓÒÚÙáàéèíìóòúùÂÊÎÔÛâêîôûÆæãÃõÕçÇ".charAt(k), k, true);
				}
			}
		}
	}

	@OriginalMember(owner = "client!cs", name = "a", descriptor = "(Lclient!ha;Ljava/awt/Font;Ljava/awt/FontMetrics;CIZ)V")
	private void load(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) Font font, @OriginalArg(2) FontMetrics metrics, @OriginalArg(3) char c, @OriginalArg(4) int id, @OriginalArg(5) boolean bold) {
		@Pc(3) int width = metrics.charWidth(c);
		@Pc(5) int charWidth = width;
		if (bold) {
			try {
				if (c == '/') {
					bold = false;
				}
				if (c == 'f' || c == 't' || c == 'w' || c == 'v' || c == 'k' || c == 'x' || c == 'y' || c == 'A' || c == 'V' || c == 'W') {
					width++;
				}
			} catch (@Pc(45) Exception ex) {
			}
		}
		@Pc(49) int maxAscent = metrics.getMaxAscent();
		@Pc(55) int height = metrics.getMaxAscent() + metrics.getMaxDescent();
		@Pc(58) int lineHeight = metrics.getHeight();
		@Pc(63) Image image = GameShell.gameCanvas.createImage(width, height);
		@Pc(66) Graphics graphics = image.getGraphics();
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(Color.white);
		graphics.setFont(font);
		graphics.drawString(String.valueOf(c), 0, maxAscent);
		if (bold) {
			graphics.drawString(String.valueOf(c), 1, maxAscent);
		}
		@Pc(100) int[] pixels = new int[width * height];
		@Pc(112) PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
		try {
			pixelGrabber.grabPixels();
		} catch (@Pc(117) Exception ex2) {
		}
		image.flush();
		@Pc(123) int topY = 0;
		@Pc(128) int local128;
		label66: for (@Pc(125) int i = 0; i < height; i++) {
			for (int x = 0; x < width; x++) {
				@Pc(137) int pixel = pixels[x + i * width];
				if ((pixel & 0xFFFFFF) != 0) {
					topY = i;
					break label66;
				}
			}
		}
		for (int pixelIndex = 0; pixelIndex < pixels.length; pixelIndex++) {
			if ((pixels[pixelIndex] & 0xFFFFFF) == 0) {
				pixels[pixelIndex] = 0;
			}
		}
		this.sizeY = maxAscent - topY;
		this.startY = lineHeight;
		this.characterWidths[id] = charWidth;
		this.nativeSprites[id] = toolkit.createNativeSprite(width, pixels, width, height);
	}

	@OriginalMember(owner = "client!cs", name = "a", descriptor = "()I")
	public int getHeight() {
		return this.sizeY;
	}

	@OriginalMember(owner = "client!cs", name = "a", descriptor = "(Lclient!ha;Ljava/lang/String;[IIIIZ)V")
	private void render(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) String string, @OriginalArg(2) int[] clipping, @OriginalArg(3) int x, @OriginalArg(4) int y, @OriginalArg(5) int colour, @OriginalArg(6) boolean shadow) {
		if (colour == 0) {
			shadow = false;
		}
		@Pc(7) int combinedColour = colour | 0xFF000000;
		for (@Pc(9) int i = 0; i < string.length(); i++) {
			@Pc(16) int c = VALID_CHARACTERS[string.charAt(i)];
			if (shadow) {
				this.nativeSprites[c].render(x + 1, y + 1, 0, -16777216, 1);
			}
			this.nativeSprites[c].render(x, y, 0, combinedColour, 1);
			x += this.characterWidths[c];
		}
	}

	@OriginalMember(owner = "client!cs", name = "a", descriptor = "(Lclient!ha;Ljava/lang/String;IIIZ)V")
	public void renderCenter(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) String string, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int colour) {
		@Pc(5) int halfWidth = this.getTextWidth(string) / 2;
		toolkit.K(this.clipping);
		if (x - halfWidth <= this.clipping[2] && (x + halfWidth >= this.clipping[0] && (y - this.sizeY <= this.clipping[3] && y + this.startY >= this.clipping[1]))) {
			this.render(toolkit, string, this.clipping, x - halfWidth, y, colour, true);
		}
	}

	@OriginalMember(owner = "client!cs", name = "a", descriptor = "(Ljava/lang/String;)I")
	public int getTextWidth(@OriginalArg(0) String text) {
		@Pc(1) int width = 0;
		for (@Pc(3) int i = 0; i < text.length(); i++) {
			@Pc(10) int index = VALID_CHARACTERS[text.charAt(i)];
			width += this.characterWidths[index];
		}
		return width;
	}

	@OriginalMember(owner = "client!cs", name = "c", descriptor = "()I")
	public int getLineHeight() {
		return this.startY - 1;
	}
}
