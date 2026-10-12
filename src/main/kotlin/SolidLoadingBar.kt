import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.gametip.component.ColorLoadingBarElement
import com.jagex.game.runetek5.client.ClientState

@OriginalClass("client!lj")
class SolidLoadingBar : AbstractLoadingBar {

	@OriginalMember(owner = "client!lj", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!ou;)V")
	constructor(@OriginalArg(0) spriteResource: Js5ResourceProvider?, @OriginalArg(1) fontResource: Js5ResourceProvider?, @OriginalArg(2) solidProgressBarInstance: ColorLoadingBarElement?) : super(spriteResource, fontResource, solidProgressBarInstance)

	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(IZBI)V")
	public override fun drawBorder(@OriginalArg(0) x: Int, @OriginalArg(3) y: Int) {
		ClientState.activeToolkit!!.outlineRect(x - 2, y, super.anchoredElement!!.width + 4, super.anchoredElement!!.height + 2, (super.anchoredElement as ColorLoadingBarElement).borderColour, 0)
		ClientState.activeToolkit!!.outlineRect(x - 1, y + 1, super.anchoredElement!!.width + 2, super.anchoredElement!!.height, 0, 0)
	}

	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(IIIZ)V")
	public override fun drawBackground(@OriginalArg(1) y: Int, @OriginalArg(2) x: Int) {
		@Pc(21) val width: Int = this.getSmoothedProgress() * super.anchoredElement!!.width / 10000
		ClientState.activeToolkit!!.aa(x, y + 2, width, super.anchoredElement!!.height - 2, (super.anchoredElement as ColorLoadingBarElement).backgroundColour, 0)
		ClientState.activeToolkit!!.aa(width + x, y + 2, super.anchoredElement!!.width - width, super.anchoredElement!!.height - 2, 0, 0)
	}
}
