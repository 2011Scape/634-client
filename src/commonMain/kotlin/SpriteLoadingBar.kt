import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.gametip.component.ImageProgressBarInstance
import com.jagex.game.runetek5.client.ClientState

@OriginalClass("client!tq")
class SpriteLoadingBar : AbstractLoadingBar {

	@OriginalMember(owner = "client!tq", name = "p", descriptor = "Lclient!mi;")
	private var fillTileSprite: NativeSprite? = null

	@OriginalMember(owner = "client!tq", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!kh;)V")
	constructor(@OriginalArg(0) spriteResource: Js5ResourceProvider?, @OriginalArg(1) fontResource: Js5ResourceProvider?, @OriginalArg(2) imageProgressBarInstance: ImageProgressBarInstance?) : super(spriteResource, fontResource, imageProgressBarInstance)

	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(IZBI)V")
	public override fun drawBorder(@OriginalArg(0) x: Int, @OriginalArg(3) y: Int) {
		ClientState.activeToolkit!!.outlineRect(x - 2, y, super.anchoredElement!!.width + 4, super.anchoredElement!!.height + 2, (super.anchoredElement as ImageProgressBarInstance).colour, 0)
		ClientState.activeToolkit!!.outlineRect(x - 1, y + 1, super.anchoredElement!!.width + 2, super.anchoredElement!!.height, 0, 0)
	}

	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(B)Z")
	override fun ready(): Boolean {
		return if (super.ready()) super.fontResource!!.isFileReady((super.anchoredElement as ImageProgressBarInstance).sprite) else false
	}

	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(I)V")
	override fun init() {
		super.init()
		this.fillTileSprite = LoadingScreenSpriteCache.fromJs5((super.anchoredElement as ImageProgressBarInstance).sprite, super.fontResource)
	}

	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(IIIZ)V")
	public override fun drawBackground(@OriginalArg(1) y: Int, @OriginalArg(2) x: Int) {
		@Pc(13) val width: Int = this.getSmoothedProgress() * super.anchoredElement!!.width / 10000
		@Pc(16) val clipping: IntArray = IntArray(4)
		ClientState.activeToolkit!!.K(clipping)
		ClientState.activeToolkit!!.KA(x, y + 2, x - -width, y + super.anchoredElement!!.height)
		this.fillTileSprite!!.drawTiled(x, y + 2, super.anchoredElement!!.width, super.anchoredElement!!.height)
		ClientState.activeToolkit!!.KA(clipping[0], clipping[1], clipping[2], clipping[3])
	}
}
