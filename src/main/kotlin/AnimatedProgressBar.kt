import com.jagex.game.runetek5.config.gametip.component.AnimatedLoadingBarElement
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ww")
class AnimatedProgressBar @OriginalMember(owner = "client!ww", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!fu;)V") constructor(@OriginalArg(0) index1: Js5ResourceProvider?, @OriginalArg(1) index2: Js5ResourceProvider?, @OriginalArg(2) animatedProgressBarInstance: AnimatedLoadingBarElement?) : LoadingBar(index1, index2, animatedProgressBarInstance) {

	@OriginalMember(owner = "client!ww", name = "a", descriptor = "(IIBII)V")
	override fun drawCompleted(@OriginalArg(0) y: Int, @OriginalArg(1) height: Int, @OriginalArg(3) x: Int, @OriginalArg(4) width: Int) {
		@Pc(12) val barWidth: Int = super.fillSprite!!.scaleWidth()
		@Pc(24) val offsetX: Int = (super.anchoredElement as AnimatedLoadingBarElement).scrollSpeed * LoadingScreenCycleCount.getCycle() / 10 % barWidth
		super.fillSprite!!.drawTiled(x + offsetX - barWidth, y, width + barWidth - offsetX, height)
	}
}
