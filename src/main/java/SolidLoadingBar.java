import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.gametip.component.ColorLoadingBarElement;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!lj")
public final class SolidLoadingBar extends AbstractLoadingBar {

	@OriginalMember(owner = "client!lj", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!ou;)V")
	public SolidLoadingBar(@OriginalArg(0) Js5ResourceProvider spriteResource, @OriginalArg(1) Js5ResourceProvider fontResource, @OriginalArg(2) ColorLoadingBarElement solidProgressBarInstance) {
		super(spriteResource, fontResource, solidProgressBarInstance);
	}

	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(IZBI)V")
	@Override
	protected void drawBorder(@OriginalArg(0) int x, @OriginalArg(3) int y) {
		ClientState.activeToolkit.outlineRect(x - 2, y, super.anchoredElement.width + 4, super.anchoredElement.height + 2, ((ColorLoadingBarElement) super.anchoredElement).borderColour, 0);
		ClientState.activeToolkit.outlineRect(x - 1, y + 1, super.anchoredElement.width + 2, super.anchoredElement.height, 0, 0);
	}

	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(IIIZ)V")
	@Override
	protected void drawBackground(@OriginalArg(1) int y, @OriginalArg(2) int x) {
		@Pc(21) int width = this.getSmoothedProgress() * super.anchoredElement.width / 10000;
		ClientState.activeToolkit.aa(x, y + 2, width, super.anchoredElement.height - 2, ((ColorLoadingBarElement) super.anchoredElement).backgroundColour, 0);
		ClientState.activeToolkit.aa(width + x, y + 2, super.anchoredElement.width - width, super.anchoredElement.height - 2, 0, 0);
	}
}
