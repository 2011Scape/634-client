import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.statics.ValidCharacterCount
import awt.Color
import awt.Font
import awt.FontMetrics
import awt.Graphics
import awt.Image
import awt.image.PixelGrabber
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.GameShell
import awt.getGraphics

@OriginalClass("client!cs")
class HelveticaFont {

	companion object {
		@OriginalMember(owner = "client!cs", name = "g", descriptor = "[I")
		private val VALID_CHARACTERS: IntArray = IntArray(256)

		init {
			for (@Pc(7) charCode in 0 until 256) {
				@Pc(12) var index: Int = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£\$%^&*()-_=+[{]};:'@#~,<.>/?\\| ÄËÏÖÜäëïöüÿßÁÀÉÈÍÌÓÒÚÙáàéèíìóòúùÂÊÎÔÛâêîôûÆæãÃõÕçÇ".indexOf(charCode.toChar())
				if (index == -1) {
					index = 74
				}
				VALID_CHARACTERS[charCode] = index
			}
		}
	}

	@OriginalMember(owner = "client!cs", name = "d", descriptor = "I")
	private var startY: Int = 0

	@OriginalMember(owner = "client!cs", name = "e", descriptor = "I")
	private var sizeY: Int = 0

	@OriginalMember(owner = "client!cs", name = "a", descriptor = "Z")
	private var loadPlain: Boolean = false

	@OriginalMember(owner = "client!cs", name = "h", descriptor = "[I")
	private val clipping: IntArray = IntArray(4)

	@OriginalMember(owner = "client!cs", name = "f", descriptor = "[Lclient!mi;")
	private val nativeSprites: Array<NativeSprite?>

	@OriginalMember(owner = "client!cs", name = "c", descriptor = "[I")
	private val characterWidths: IntArray

	@OriginalMember(owner = "client!cs", name = "<init>", descriptor = "(Lclient!ha;IZLjava/awt/Component;)V")
	constructor(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) size: Int, @OriginalArg(2) arg2: Boolean, @OriginalArg(3) component: awt.Component?) {
		this.loadPlain = false
		this.nativeSprites = arrayOfNulls<NativeSprite>(256)
		this.characterWidths = IntArray(256)
		@Pc(30) var font: Font = Font("Helvetica", 1, size)
		@Pc(34) var fontMetrics: FontMetrics = component!!.getFontMetrics(font)
		for (@Pc(36) i in 0 until ValidCharacterCount.VALID_CHARACTER_COUNT) {
			this.load(toolkit, font, fontMetrics, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£\$%^&*()-_=+[{]};:'@#~,<.>/?\\| ÄËÏÖÜäëïöüÿßÁÀÉÈÍÌÓÒÚÙáàéèíìóòúùÂÊÎÔÛâêîôûÆæãÃõÕçÇ"[i], i, false)
		}
		if (this.loadPlain) {
			this.loadPlain = false
			font = Font("Helvetica", 0, size)
			fontMetrics = component.getFontMetrics(font)
			for (@Pc(72) j in 0 until ValidCharacterCount.VALID_CHARACTER_COUNT) {
				this.load(toolkit, font, fontMetrics, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£\$%^&*()-_=+[{]};:'@#~,<.>/?\\| ÄËÏÖÜäëïöüÿßÁÀÉÈÍÌÓÒÚÙáàéèíìóòúùÂÊÎÔÛâêîôûÆæãÃõÕçÇ"[j], j, false)
			}
			if (!this.loadPlain) {
				this.loadPlain = false
				for (@Pc(95) k in 0 until ValidCharacterCount.VALID_CHARACTER_COUNT) {
					this.load(toolkit, font, fontMetrics, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£\$%^&*()-_=+[{]};:'@#~,<.>/?\\| ÄËÏÖÜäëïöüÿßÁÀÉÈÍÌÓÒÚÙáàéèíìóòúùÂÊÎÔÛâêîôûÆæãÃõÕçÇ"[k], k, true)
				}
			}
		}
	}

	@OriginalMember(owner = "client!cs", name = "a", descriptor = "(Lclient!ha;Ljava/awt/Font;Ljava/awt/FontMetrics;CIZ)V")
	private fun load(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) font: Font?, @OriginalArg(2) metrics: FontMetrics, @OriginalArg(3) c: Char, @OriginalArg(4) id: Int, @OriginalArg(5) bold: Boolean) {
		var bold: Boolean = bold
		@Pc(3) var width: Int = metrics.charWidth(c)
		@Pc(5) val charWidth: Int = width
		if (bold) {
			try {
				if (c == '/') {
					bold = false
				}
				if (c == 'f' || c == 't' || c == 'w' || c == 'v' || c == 'k' || c == 'x' || c == 'y' || c == 'A' || c == 'V' || c == 'W') {
					width++
				}
			} catch (@Pc(45) ex: Exception) {
			}
		}
		@Pc(49) val maxAscent: Int = metrics.getMaxAscent()
		@Pc(55) val height: Int = metrics.getMaxAscent() + metrics.getMaxDescent()
		@Pc(58) val lineHeight: Int = metrics.getHeight()
		@Pc(63) val image: Image = GameShell.gameCanvas!!.createImage(width, height)
		@Pc(66) val graphics: Graphics = image.getGraphics()
		graphics.setColor(awt.COLOR_BLACK)
		graphics.fillRect(0, 0, width, height)
		graphics.setColor(awt.COLOR_WHITE)
		graphics.setFont(font)
		graphics.drawString(c.toString(), 0, maxAscent)
		if (bold) {
			graphics.drawString(c.toString(), 1, maxAscent)
		}
		@Pc(100) val pixels: IntArray = IntArray(width * height)
		@Pc(112) val pixelGrabber: PixelGrabber = PixelGrabber(image, 0, 0, width, height, pixels, 0, width)
		try {
			pixelGrabber.grabPixels()
		} catch (@Pc(117) ex2: Exception) {
		}
		image.flush()
		@Pc(123) var topY: Int = 0
		@Pc(128) var local128: Int
		label66@ for (@Pc(125) i in 0 until height) {
			for (x in 0 until width) {
				@Pc(137) val pixel: Int = pixels[x + i * width]
				if ((pixel and 0xFFFFFF) != 0) {
					topY = i
					break@label66
				}
			}
		}
		for (pixelIndex in 0 until pixels.size) {
			if ((pixels[pixelIndex] and 0xFFFFFF) == 0) {
				pixels[pixelIndex] = 0
			}
		}
		this.sizeY = maxAscent - topY
		this.startY = lineHeight
		this.characterWidths[id] = charWidth
		this.nativeSprites[id] = toolkit!!.createNativeSprite(width, pixels, width, height)
	}

	@OriginalMember(owner = "client!cs", name = "a", descriptor = "()I")
	fun getHeight(): Int {
		return this.sizeY
	}

	@OriginalMember(owner = "client!cs", name = "a", descriptor = "(Lclient!ha;Ljava/lang/String;[IIIIZ)V")
	private fun render(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) string: String, @OriginalArg(2) clipping: IntArray?, @OriginalArg(3) x: Int, @OriginalArg(4) y: Int, @OriginalArg(5) colour: Int, @OriginalArg(6) shadow: Boolean) {
		var x: Int = x
		var shadow: Boolean = shadow
		if (colour == 0) {
			shadow = false
		}
		@Pc(7) val combinedColour: Int = colour or 0xFF000000.toInt()
		@Pc(9) var i: Int = 0
		while (i < string.length) {
			@Pc(16) val c: Int = VALID_CHARACTERS[string[i].code]
			if (shadow) {
				this.nativeSprites[c]!!.render(x + 1, y + 1, 0, -16777216, 1)
			}
			this.nativeSprites[c]!!.render(x, y, 0, combinedColour, 1)
			x += this.characterWidths[c]
			i++
		}
	}

	@OriginalMember(owner = "client!cs", name = "a", descriptor = "(Lclient!ha;Ljava/lang/String;IIIZ)V")
	fun renderCenter(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) string: String?, @OriginalArg(2) x: Int, @OriginalArg(3) y: Int, @OriginalArg(4) colour: Int) {
		@Pc(5) val halfWidth: Int = this.getTextWidth(string) / 2
		toolkit!!.K(this.clipping)
		if (x - halfWidth <= this.clipping[2] && (x + halfWidth >= this.clipping[0] && (y - this.sizeY <= this.clipping[3] && y + this.startY >= this.clipping[1]))) {
			this.render(toolkit, string!!, this.clipping, x - halfWidth, y, colour, true)
		}
	}

	@OriginalMember(owner = "client!cs", name = "a", descriptor = "(Ljava/lang/String;)I")
	fun getTextWidth(@OriginalArg(0) text: String?): Int {
		@Pc(1) var width: Int = 0
		@Pc(3) var i: Int = 0
		while (i < text!!.length) {
			@Pc(10) val index: Int = VALID_CHARACTERS[text[i].code]
			width += this.characterWidths[index]
			i++
		}
		return width
	}

	@OriginalMember(owner = "client!cs", name = "c", descriptor = "()I")
	fun getLineHeight(): Int {
		return this.startY - 1
	}
}
