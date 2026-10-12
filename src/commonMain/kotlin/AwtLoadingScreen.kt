import awt.Color
import awt.Font
import awt.FontMetrics
import awt.Graphics
import awt.Image
import awt.image.ImageObserver
import lang.reflect.Field
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.loginscreen.LoginScreenComponent
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import lang.Class
import lang.IllegalAccessException
import lang.NoSuchFieldException
import awt.getHeight
import awt.getWidth
import lang.jClass
import awt.getGraphics

@OriginalClass("client!wba")
class AwtLoadingScreen : LoginScreenComponent {

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "Z")
	private var drawInCenterX: Boolean = false

	@OriginalMember(owner = "client!wba", name = "b", descriptor = "I")
	private var xOffset: Int = 0

	@OriginalMember(owner = "client!wba", name = "c", descriptor = "Z")
	private var fallback: Boolean = false

	@OriginalMember(owner = "client!wba", name = "d", descriptor = "I")
	private var width: Int = 0

	@OriginalMember(owner = "client!wba", name = "e", descriptor = "Ljava/awt/FontMetrics;")
	private var bodyFontMetrics: FontMetrics? = null

	@OriginalMember(owner = "client!wba", name = "g", descriptor = "Ljava/awt/Image;")
	private var bodyRight: Image? = null

	@OriginalMember(owner = "client!wba", name = "j", descriptor = "I")
	private var boxXOffset: Int = 0

	@OriginalMember(owner = "client!wba", name = "k", descriptor = "Ljava/awt/Image;")
	private var bodyLeft: Image? = null

	@OriginalMember(owner = "client!wba", name = "m", descriptor = "Ljava/awt/Image;")
	private var left: Image? = null

	@OriginalMember(owner = "client!wba", name = "o", descriptor = "Ljava/awt/Color;")
	private var textColour: Color? = null

	@OriginalMember(owner = "client!wba", name = "p", descriptor = "Ljava/awt/Image;")
	private var background: Image? = null

	@OriginalMember(owner = "client!wba", name = "q", descriptor = "I")
	private var height: Int = 0

	@OriginalMember(owner = "client!wba", name = "r", descriptor = "Ljava/awt/Image;")
	private var right: Image? = null

	@OriginalMember(owner = "client!wba", name = "u", descriptor = "I")
	private var boxYOffset: Int = 0

	@OriginalMember(owner = "client!wba", name = "v", descriptor = "Ljava/awt/Image;")
	private var bottom: Image? = null

	@OriginalMember(owner = "client!wba", name = "w", descriptor = "I")
	private var offsetPerTenCycles: Int = 0

	@OriginalMember(owner = "client!wba", name = "z", descriptor = "Z")
	private var drawInCenterY: Boolean = false

	@OriginalMember(owner = "client!wba", name = "A", descriptor = "I")
	private var textYOffset: Int = 0

	@OriginalMember(owner = "client!wba", name = "B", descriptor = "Ljava/awt/Image;")
	private var bar: Image? = null

	@OriginalMember(owner = "client!wba", name = "E", descriptor = "I")
	private var boxWidth: Int = 0

	@OriginalMember(owner = "client!wba", name = "F", descriptor = "I")
	private var yOffset: Int = 0

	@OriginalMember(owner = "client!wba", name = "H", descriptor = "Ljava/awt/Font;")
	private var bodyFont: Font? = null

	@OriginalMember(owner = "client!wba", name = "I", descriptor = "Ljava/awt/Image;")
	private var top: Image? = null

	@OriginalMember(owner = "client!wba", name = "K", descriptor = "Ljava/awt/Image;")
	private var bodyFill: Image? = null

	@OriginalMember(owner = "client!wba", name = "N", descriptor = "Ljava/awt/Image;")
	private var image: Image? = null

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(Z)V")
	@Throws(IllegalAccessException::class, NoSuchFieldException::class)
	private fun load() {
		@Pc(6) val appletClass: Class<*> = GameShell.applet!!.jClass
		this.bar = this.getObject(appletClass, "bar") as Image?
		this.background = this.getObject(appletClass, "background") as Image?
		this.left = this.getObject(appletClass, "left") as Image?
		this.right = this.getObject(appletClass, "right") as Image?
		this.top = this.getObject(appletClass, "top") as Image?
		this.bottom = this.getObject(appletClass, "bottom") as Image?
		this.bodyLeft = this.getObject(appletClass, "bodyLeft") as Image?
		this.bodyRight = this.getObject(appletClass, "bodyRight") as Image?
		this.bodyFill = this.getObject(appletClass, "bodyFill") as Image?
		this.bodyFont = this.getObject(appletClass, "bf") as Font?
		this.bodyFontMetrics = this.getObject(appletClass, "bfm") as FontMetrics?
		this.textColour = this.getObject(appletClass, "colourtext") as Color?
		@Pc(108) val `object`: Any? = this.getObject(appletClass, "lb")
		@Pc(111) val lbClass: Class<*> = `object`!!.jClass
		this.drawInCenterX = this.getBooleanField(`object`, lbClass, "xMiddle")
		this.drawInCenterY = this.getBooleanField(`object`, lbClass, "yMiddle")
		this.xOffset = this.getIntField(`object`, lbClass, "xOffset")
		this.yOffset = this.getIntField(`object`, lbClass, "yOffset")
		this.width = this.getIntField(`object`, lbClass, "width")
		this.height = this.getIntField(`object`, lbClass, "height")
		this.boxXOffset = this.getIntField(`object`, lbClass, "boxXOffset")
		this.boxYOffset = this.getIntField(`object`, lbClass, "boxYOffset")
		this.boxWidth = this.getIntField(`object`, lbClass, "boxWidth")
		this.textYOffset = this.getIntField(`object`, lbClass, "textYOffset")
		this.offsetPerTenCycles = this.getIntField(`object`, lbClass, "offsetPerTenCycles")
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;I)I")
	@Throws(IllegalAccessException::class, NoSuchFieldException::class)
	private fun getIntField(@OriginalArg(0) `object`: Any?, @OriginalArg(1) class2: Class<*>?, @OriginalArg(2) string: String?): Int {
		@Pc(15) val field: Field = class2!!.getDeclaredField(string)
		return field.getInt(`object`)
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(Ljava/lang/Class;ILjava/lang/String;)Ljava/lang/Object;")
	@Throws(IllegalAccessException::class, NoSuchFieldException::class)
	private fun getObject(@OriginalArg(0) clazz: Class<*>?, @OriginalArg(2) string: String?): Any? {
		@Pc(12) val field: Field = clazz!!.getDeclaredField(string)
		@Pc(16) val `object`: Any? = field.get(GameShell.applet)
		field.set(GameShell.applet, null)
		return `object`
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(B)I")
	override fun getFadeDuration(): Int {
		return 0
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(BJ)Z")
	override fun finished(@OriginalArg(1) initTime: Long): Boolean {
		return true
	}

	@OriginalMember(owner = "client!wba", name = "c", descriptor = "(I)I")
	override fun percentage(): Int {
		return 100
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(BI)I")
	private fun getDrawX(@OriginalArg(1) width: Int): Int {
		return if (this.drawInCenterX) (GameShell.screenCanvasWidth - width) / 2 else 0
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(I)V")
	override fun init() {
	}

	@OriginalMember(owner = "client!wba", name = "b", descriptor = "(I)V")
	override fun cleanup() {
		AwtLoadingBarReset.cleanupPlainLoadingBarResources()
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(Ljava/lang/Object;ZLjava/lang/Class;Ljava/lang/String;)Z")
	@Throws(IllegalAccessException::class, NoSuchFieldException::class)
	private fun getBooleanField(@OriginalArg(0) `object`: Any?, @OriginalArg(2) class2: Class<*>?, @OriginalArg(3) string: String?): Boolean {
		@Pc(15) val field: Field = class2!!.getDeclaredField(string)
		return field.getBoolean(`object`)
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(ZI)V")
	override fun render(@OriginalArg(0) fullRepaint: Boolean) {
		if (!this.fallback) {
			if (GameShell.applet == null) {
				this.fallback = true
			} else if (this.bodyFont == null) {
				try {
					this.load()
				} catch (@Pc(31) ex: Exception) {
					this.fallback = true
				}
			}
		}
		if (this.fallback) {
			this.drawPlainLoadingBar()
			return
		}
		@Pc(45) val graphics: Graphics? = GameShell.gameCanvas!!.getGraphics()
		if (graphics == null) {
			GameShell.gameCanvas!!.repaint()
			return
		}
		try {
			@Pc(51) val loadingPercentage: Int = ActiveLoadingScreenRenderer.aClass8_1!!.getLoadingPercentage()
			@Pc(55) val string: String? = ActiveLoadingScreenRenderer.aClass8_1!!.getLoadingText()
			if (Static658.canvasImage == null) {
				Static658.canvasImage = GameShell.gameCanvas!!.createImage(GameShell.screenCanvasWidth, GameShell.screenCanvasHeight)
			}
			@Pc(65) val graphics2: Graphics = Static658.canvasImage!!.getGraphics()
			graphics2.clearRect(0, 0, GameShell.screenCanvasWidth, GameShell.screenCanvasHeight)
			@Pc(76) val bodyLeftWidth: Int = this.bodyLeft!!.getWidth(null)
			@Pc(81) val bodyRightWidth: Int = this.bodyRight!!.getWidth(null)
			@Pc(86) val bodyFillWidth: Int = this.bodyFill!!.getWidth(null)
			@Pc(91) val bodyLeftHeight: Int = this.bodyLeft!!.getHeight(null)
			@Pc(96) val bodyRightHeight: Int = this.bodyRight!!.getHeight(null)
			@Pc(101) val bodyFillHeight: Int = this.bodyFill!!.getHeight(null)
			graphics2.drawImage(this.bodyLeft, this.getDrawX(bodyLeftWidth) + this.boxXOffset - this.boxWidth / 2, this.getDrawY(bodyLeftHeight) + this.boxYOffset, null)
			@Pc(138) val fillStartX: Int = bodyLeftWidth + this.boxXOffset - this.boxWidth / 2
			@Pc(146) val fillEndX: Int = this.boxXOffset + this.boxWidth / 2
			@Pc(148) var i: Int = fillStartX
			while (i <= fillEndX) {
				graphics2.drawImage(this.bodyFill, i + this.getDrawX(bodyLeftWidth) + this.boxXOffset, this.getDrawY(bodyFillHeight) + this.boxYOffset, null)
				i += bodyFillWidth
			}
			graphics2.drawImage(this.bodyRight, this.getDrawX(bodyRightWidth) + this.boxXOffset + this.boxWidth / 2, this.getDrawY(bodyRightHeight) - -this.boxYOffset, null)
			@Pc(216) val leftWidth: Int = this.left!!.getWidth(null)
			@Pc(221) val leftHeight: Int = this.left!!.getHeight(null)
			@Pc(226) val rightWidth: Int = this.right!!.getWidth(null)
			@Pc(231) val rightHeight: Int = this.right!!.getHeight(null)
			@Pc(236) val bottomHeight: Int = this.bottom!!.getHeight(null)
			@Pc(241) val topWidth: Int = this.top!!.getWidth(null)
			@Pc(246) val topHeight: Int = this.top!!.getHeight(null)
			@Pc(251) val bottomWidth: Int = this.bottom!!.getWidth(null)
			@Pc(256) val barWidth: Int = this.bar!!.getWidth(null)
			@Pc(261) val backgroundWidth: Int = this.background!!.getWidth(null)
			@Pc(270) val barX: Int = this.getDrawX(this.width) + this.xOffset
			@Pc(280) val barY: Int = this.getDrawY(this.height) + this.yOffset
			graphics2.drawImage(this.left, barX, barY + (this.height - leftHeight) / 2, null)
			graphics2.drawImage(this.right, this.width + barX - rightWidth, (this.height - rightHeight) / 2 + barY, null)
			if (this.image == null) {
				this.image = GameShell.gameCanvas!!.createImage(this.width - rightWidth - leftWidth, this.height)
			}
			@Pc(338) val graphics3: Graphics = this.image!!.getGraphics()
			@Pc(340) var j: Int = 0
			while (j < this.width - rightWidth - leftWidth) {
				graphics3.drawImage(this.top, j, 0, null)
				j += topWidth
			}
			@Pc(370) var k: Int = 0
			while (k < this.width - leftWidth - rightWidth) {
				graphics3.drawImage(this.bottom, k, this.height - bottomHeight, null)
				k += bottomWidth
			}
			@Pc(413) var segmentWidth: Int = loadingPercentage * (this.width - rightWidth - leftWidth) / 100
			@Pc(452) var tileX: Int
			if (segmentWidth > 0) {
				@Pc(430) val image2: Image = GameShell.gameCanvas!!.createImage(segmentWidth, this.height - topHeight - bottomHeight)
				@Pc(434) val completedImageWidth: Int = image2.getWidth(null)
				@Pc(437) val graphics4: Graphics = image2.getGraphics()
				@Pc(447) val scrollOffset: Int = this.offsetPerTenCycles * LoadingScreenCycleCount.getCycle() / 10 % barWidth
				tileX = scrollOffset - barWidth
				while (tileX < completedImageWidth) {
					graphics4.drawImage(this.bar, tileX, 0, null)
					tileX += barWidth
				}
				graphics3.drawImage(image2, 0, topHeight, null)
			}
			@Pc(482) val completedWidth: Int = segmentWidth
			segmentWidth = this.width - segmentWidth - rightWidth - leftWidth
			if (segmentWidth > 0) {
				@Pc(508) val image3: Image = GameShell.gameCanvas!!.createImage(segmentWidth, this.height - topHeight - bottomHeight)
				@Pc(512) val remainingImageWidth: Int = image3.getWidth(null)
				@Pc(515) val graphics5: Graphics = image3.getGraphics()
				tileX = 0
				while (tileX < remainingImageWidth) {
					graphics5.drawImage(this.background, tileX, 0, null)
					tileX += backgroundWidth
				}
				graphics3.drawImage(image3, completedWidth, topHeight, null)
			}
			graphics2.drawImage(this.image, leftWidth + barX, barY, null)
			graphics2.setFont(this.bodyFont)
			graphics2.setColor(this.textColour)
			graphics2.drawString(string, barX + (this.width - this.bodyFontMetrics!!.stringWidth(string)) / 2, this.textYOffset + 4 + this.height / 2 + barY)
			graphics.drawImage(Static658.canvasImage, 0, 0, null)
		} catch (@Pc(594) ex2: Exception) {
			this.fallback = true
		}
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(II)I")
	private fun getDrawY(@OriginalArg(0) height: Int): Int {
		return if (this.drawInCenterY) (GameShell.screenCanvasHeight - height) / 2 else 0
	}

	@OriginalMember(owner = "client!wba", name = "e", descriptor = "(I)V")
	private fun drawPlainLoadingBar() {
		AwtLoadingBarPainter.drawPlainLoadingBar(ActiveLoadingScreenRenderer.aClass8_1!!.getLoadingPercentage(), AwtLoadingScreen.DEBUG_LOAD_COLOR[AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX], ActiveLoadingScreenRenderer.aClass8_1!!.getLoadingText(), AwtLoadingScreen.DEBUG_LOAD_OUTLINE_COLOR[AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX], AwtLoadingScreen.DEBUG_LOAD_TEXT_COLOR[AwtLoadingScreen.DEBUG_DISPLAY_COLOR_INDEX])
	}

	companion object {
		@OriginalMember(owner = "client!bi", name = "h", descriptor = "[Ljava/awt/Color;")
		@JvmField
		val DEBUG_LOAD_COLOR: Array<Color?> = arrayOf<Color?>(Color(9179409), Color(3289650), Color(3289650), Color(3289650))

		@OriginalMember(owner = "client!sda", name = "b", descriptor = "I")
		@JvmField
		var DEBUG_DISPLAY_COLOR_INDEX: Int = 0

		@OriginalMember(owner = "client!hc", name = "l", descriptor = "[Ljava/awt/Color;")
		@JvmField
		val DEBUG_LOAD_TEXT_COLOR: Array<Color?> = arrayOf<Color?>(Color(16777215), Color(16777215), Color(16741381), Color(16741381))

		@OriginalMember(owner = "client!jq", name = "X", descriptor = "[Ljava/awt/Color;")
		@JvmField
		val DEBUG_LOAD_OUTLINE_COLOR: Array<Color?> = arrayOf<Color?>(Color(9179409), Color(16777215), Color(16726277), Color(16726277))
	}
}
