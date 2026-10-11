import com.jagex.game.runetek5.config.gametip.component.AnimatedLoadingBarElement;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ww")
public final class AnimatedProgressBar extends LoadingBar {

	@OriginalMember(owner = "client!ww", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!fu;)V")
	public AnimatedProgressBar(@OriginalArg(0) Js5ResourceProvider index1, @OriginalArg(1) Js5ResourceProvider index2, @OriginalArg(2) AnimatedLoadingBarElement animatedProgressBarInstance) {
		super(index1, index2, animatedProgressBarInstance);
	}

	@OriginalMember(owner = "client!ww", name = "a", descriptor = "(IIBII)V")
	@Override
	protected void drawCompleted(@OriginalArg(0) int y, @OriginalArg(1) int height, @OriginalArg(3) int x, @OriginalArg(4) int width) {
		@Pc(12) int barWidth = super.fillSprite.scaleWidth();
		@Pc(24) int offsetX = ((AnimatedLoadingBarElement) super.anchoredElement).scrollSpeed * LoadingScreenCycleCount.getCycle() / 10 % barWidth;
		super.fillSprite.drawTiled(x + offsetX - barWidth, y, width + barWidth - offsetX, height);
	}
}
