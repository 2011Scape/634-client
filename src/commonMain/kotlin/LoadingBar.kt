import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.gametip.component.MultiSpriteLoadingBarElement
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField

@OriginalClass("client!ce")
open class LoadingBar @OriginalMember(owner = "client!ce", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!um;)V") constructor(@OriginalArg(0) index1: Js5ResourceProvider?, @OriginalArg(1) index2: Js5ResourceProvider?, @OriginalArg(2) fancyProgressBarInstance: MultiSpriteLoadingBarElement?) : AbstractLoadingBar(index1, index2, fancyProgressBarInstance) {

	@OriginalMember(owner = "client!ce", name = "t", descriptor = "Lclient!mi;")
	private var bottomBorderSprite: NativeSprite? = null

	@OriginalMember(owner = "client!ce", name = "u", descriptor = "Lclient!mi;")
	private var rightCapSprite: NativeSprite? = null

	@OriginalMember(owner = "client!ce", name = "x", descriptor = "Lclient!mi;")
	private var leftCapSprite: NativeSprite? = null

	@OriginalMember(owner = "client!ce", name = "y", descriptor = "Lclient!mi;")
	private var topBorderSprite: NativeSprite? = null

	@OriginalMember(owner = "client!ce", name = "C", descriptor = "Lclient!mi;")
	private var emptySprite: NativeSprite? = null

	@OriginalMember(owner = "client!ce", name = "G", descriptor = "Lclient!mi;")
	@JvmField
	protected var fillSprite: NativeSprite? = null

	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(B)Z")
	final override fun ready(): Boolean {
		if (!super.ready()) {
			return false
		}
		@Pc(13) val instance: MultiSpriteLoadingBarElement = super.anchoredElement as MultiSpriteLoadingBarElement
		if (!super.fontResource!!.isFileReady(instance.spriteD)) {
			return false
		} else if (!super.fontResource!!.isFileReady(instance.spriteF)) {
			return false
		} else if (!super.fontResource!!.isFileReady(instance.spriteC)) {
			return false
		} else if (!super.fontResource!!.isFileReady(instance.spriteB)) {
			return false
		} else if (super.fontResource!!.isFileReady(instance.spriteA)) {
			return super.fontResource!!.isFileReady(instance.spriteE)
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(IIBII)V")
	protected open fun drawCompleted(@OriginalArg(0) y: Int, @OriginalArg(1) height: Int, @OriginalArg(3) x: Int, @OriginalArg(4) width: Int) {
		this.fillSprite!!.drawTiled(x, y, width, height)
	}

	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(IZBI)V")
	final override fun drawBorder(@OriginalArg(0) x: Int, @OriginalArg(3) y: Int) {
		@Pc(4) val clipping: IntArray = IntArray(4)
		ClientState.activeToolkit!!.K(clipping)
		ClientState.activeToolkit!!.KA(x, y, x + super.anchoredElement!!.width, y + super.anchoredElement!!.height)
		@Pc(26) val leftWidth: Int = this.leftCapSprite!!.scaleWidth()
		@Pc(30) val leftHeight: Int = this.leftCapSprite!!.scaleHeight()
		@Pc(34) val rightWidth: Int = this.rightCapSprite!!.scaleWidth()
		@Pc(38) val rightHeight: Int = this.rightCapSprite!!.scaleHeight()
		this.leftCapSprite!!.render(x, y + (super.anchoredElement!!.height - leftHeight) / 2)
		this.rightCapSprite!!.render(super.anchoredElement!!.width + x - rightWidth, y + (super.anchoredElement!!.height + -rightHeight) / 2)
		ClientState.activeToolkit!!.KA(x, y, super.anchoredElement!!.width + x, y - -this.topBorderSprite!!.scaleHeight())
		this.topBorderSprite!!.drawTiled(x + leftWidth, y, super.anchoredElement!!.width - leftWidth - rightWidth, super.anchoredElement!!.height)
		@Pc(111) val height: Int = this.bottomBorderSprite!!.scaleHeight()
		ClientState.activeToolkit!!.KA(x, y + super.anchoredElement!!.height - height, super.anchoredElement!!.width + x, y + super.anchoredElement!!.height)
		this.bottomBorderSprite!!.drawTiled(leftWidth + x, -height + y + super.anchoredElement!!.height, super.anchoredElement!!.width - leftWidth - rightWidth, super.anchoredElement!!.height)
		ClientState.activeToolkit!!.KA(clipping[0], clipping[1], clipping[2], clipping[3])
	}

	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(IIIZ)V")
	final override fun drawBackground(@OriginalArg(1) y: Int, @OriginalArg(2) x: Int) {
		@Pc(17) val x1: Int = this.leftCapSprite!!.scaleWidth() + x
		@Pc(28) val x2: Int = super.anchoredElement!!.width + x - this.rightCapSprite!!.scaleWidth()
		@Pc(35) val y1: Int = y + this.topBorderSprite!!.scaleHeight()
		@Pc(45) val y2: Int = y + super.anchoredElement!!.height - this.bottomBorderSprite!!.scaleHeight()
		@Pc(50) val width: Int = x2 - x1
		@Pc(55) val height: Int = y2 - y1
		@Pc(63) val progressWidth: Int = this.getSmoothedProgress() * width / 10000
		@Pc(66) val clipping: IntArray = IntArray(4)
		ClientState.activeToolkit!!.K(clipping)
		ClientState.activeToolkit!!.KA(x1, y1, progressWidth + x1, y2)
		this.drawCompleted(y1, height, x1, width)
		ClientState.activeToolkit!!.KA(progressWidth + x1, y1, x2, y2)
		this.emptySprite!!.drawTiled(x1, y1, width, height)
		ClientState.activeToolkit!!.KA(clipping[0], clipping[1], clipping[2], clipping[3])
	}

	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(I)V")
	final override fun init() {
		super.init()
		@Pc(10) val fancyProgressBarInstance: MultiSpriteLoadingBarElement = super.anchoredElement as MultiSpriteLoadingBarElement
		this.fillSprite = LoadingScreenSpriteCache.fromJs5(fancyProgressBarInstance.spriteD, super.fontResource)
		this.emptySprite = LoadingScreenSpriteCache.fromJs5(fancyProgressBarInstance.spriteF, super.fontResource)
		this.leftCapSprite = LoadingScreenSpriteCache.fromJs5(fancyProgressBarInstance.spriteC, super.fontResource)
		this.rightCapSprite = LoadingScreenSpriteCache.fromJs5(fancyProgressBarInstance.spriteB, super.fontResource)
		this.topBorderSprite = LoadingScreenSpriteCache.fromJs5(fancyProgressBarInstance.spriteA, super.fontResource)
		this.bottomBorderSprite = LoadingScreenSpriteCache.fromJs5(fancyProgressBarInstance.spriteE, super.fontResource)
	}
}
