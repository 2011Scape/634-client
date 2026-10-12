import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem
import com.jagex.game.runetek5.config.gametip.component.AnchoredElement
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!gl")
abstract class AbstractLoadingBar @OriginalMember(owner = "client!gl", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!fn;)V") protected constructor(@OriginalArg(0) fontResource: Js5ResourceProvider?, @OriginalArg(1) spriteResource: Js5ResourceProvider?, @OriginalArg(2) progressBarInstance: AnchoredElement?) : DrawableItem {

	@OriginalMember(owner = "client!gl", name = "d", descriptor = "I")
	private var lastLoadPercent: Int = 0

	@OriginalMember(owner = "client!gl", name = "k", descriptor = "Lclient!da;")
	private var fontRenderer: AbstractFontRenderer? = null

	@OriginalMember(owner = "client!gl", name = "n", descriptor = "J")
	private var lastLoadPercentChange: Long = 0L

	@OriginalMember(owner = "client!gl", name = "e", descriptor = "Lclient!in;")
	private val spriteResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!gl", name = "f", descriptor = "Lclient!in;")
	@JvmField
	protected val fontResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!gl", name = "i", descriptor = "Lclient!fn;")
	@JvmField
	protected val anchoredElement: AnchoredElement?

	init {
		this.spriteResource = spriteResource
		this.fontResource = fontResource
		this.anchoredElement = progressBarInstance
	}

	@OriginalMember(owner = "client!gl", name = "a", descriptor = "(IZBI)V")
	protected abstract fun drawBorder(@OriginalArg(0) x: Int, @OriginalArg(3) y: Int)

	@OriginalMember(owner = "client!gl", name = "a", descriptor = "(B)Z")
	override fun ready(): Boolean {
		@Pc(13) var cached: Boolean = true
		if (!this.fontResource!!.isFileReady(this.anchoredElement!!.fileId)) {
			cached = false
		}
		if (!this.spriteResource!!.isFileReady(this.anchoredElement.fileId)) {
			cached = false
		}
		return cached
	}

	@OriginalMember(owner = "client!gl", name = "a", descriptor = "(I)V")
	override fun init() {
		@Pc(15) val fontMetrics: FontMetrics? = FontMetrics.list(this.anchoredElement!!.fileId, this.spriteResource)
		this.fontRenderer = ClientState.activeToolkit!!.createFontRenderer(fontMetrics, SpriteType.listGroup(this.fontResource, this.anchoredElement.fileId))
	}

	@OriginalMember(owner = "client!gl", name = "a", descriptor = "(IIIZ)V")
	protected abstract fun drawBackground(@OriginalArg(1) y: Int, @OriginalArg(2) x: Int)

	@OriginalMember(owner = "client!gl", name = "c", descriptor = "(I)I")
	protected fun getSmoothedProgress(): Int {
		@Pc(15) val loadPercentage: Int = ActiveLoadingScreenRenderer.aClass8_1!!.getLoadingPercentage()
		@Pc(19) var progress: Int = loadPercentage * 100
		if (loadPercentage == this.lastLoadPercent && loadPercentage != 0) {
			@Pc(45) val stageCompletion: Int = ActiveLoadingScreenRenderer.aClass8_1!!.getLoadingStageCompletion()
			if (loadPercentage < stageCompletion) {
				@Pc(55) val timeDelta: Long = this.lastLoadPercentChange - ActiveLoadingScreenRenderer.aClass8_1!!.getLoadingStageChangedTime()
				if (timeDelta > 0L) {
					@Pc(71) val timeForCompletion: Long = (stageCompletion - loadPercentage).toLong() * (timeDelta * 10000L / loadPercentage.toLong())
					@Pc(80) val elapsedTime: Long = (Utils.currentTimeMillis() - this.lastLoadPercentChange) * 10000L
					if (timeForCompletion > elapsedTime) {
						progress = ((loadPercentage * 100).toLong() + (stageCompletion - loadPercentage).toLong() * elapsedTime * 100L / timeForCompletion).toInt()
					} else {
						progress = stageCompletion * 100
					}
				}
			}
		} else {
			this.lastLoadPercent = loadPercentage
			this.lastLoadPercentChange = Utils.currentTimeMillis()
		}
		return progress
	}

	@OriginalMember(owner = "client!gl", name = "a", descriptor = "(BZ)V")
	final override fun execute() {
		@Pc(21) val x: Int = this.anchoredElement!!.horizontalAnchor!!.getX(GameShell.GAME_WIDTH, this.anchoredElement.width) + this.anchoredElement.offsetX
		@Pc(37) val y: Int = this.anchoredElement.verticalAnchor!!.getY(MessageBox.paddingTop, this.anchoredElement.height) + this.anchoredElement.offsetY
		this.drawBorder(x, y)
		this.drawBackground(y, x)
		@Pc(55) var string: String? = ActiveLoadingScreenRenderer.aClass8_1!!.getLoadingText()
		if (Utils.currentTimeMillis() - this.lastLoadPercentChange > 10000L) {
			string = string + " (" + ActiveLoadingScreenRenderer.aClass8_1!!.getLoadingStage()!!.getStep() + ")"
		}
		this.fontRenderer!!.renderCentered(x + this.anchoredElement.width / 2, this.anchoredElement.textColor, string, -1, this.anchoredElement.textOffsetY + y + this.anchoredElement.height / 2 + 4)
	}
}
