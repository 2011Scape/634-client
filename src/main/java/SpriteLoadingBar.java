import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.gametip.component.ImageProgressBarInstance;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!tq")
public final class SpriteLoadingBar extends AbstractLoadingBar {

	@OriginalMember(owner = "client!tq", name = "p", descriptor = "Lclient!mi;")
	private NativeSprite fillTileSprite;

	@OriginalMember(owner = "client!tq", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!kh;)V")
	public SpriteLoadingBar(@OriginalArg(0) Js5ResourceProvider spriteResource, @OriginalArg(1) Js5ResourceProvider fontResource, @OriginalArg(2) ImageProgressBarInstance imageProgressBarInstance) {
		super(spriteResource, fontResource, imageProgressBarInstance);
	}

	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(IZBI)V")
	@Override
	protected void drawBorder(@OriginalArg(0) int x, @OriginalArg(3) int y) {
		ClientState.activeToolkit.outlineRect(x - 2, y, super.anchoredElement.width + 4, super.anchoredElement.height + 2, ((ImageProgressBarInstance) super.anchoredElement).colour, 0);
		ClientState.activeToolkit.outlineRect(x - 1, y + 1, super.anchoredElement.width + 2, super.anchoredElement.height, 0, 0);
	}

	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(B)Z")
	@Override
	public boolean ready() {
		return super.ready() ? super.fontResource.isFileReady(((ImageProgressBarInstance) super.anchoredElement).sprite) : false;
	}

	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(I)V")
	@Override
	public void init() {
		super.init();
		this.fillTileSprite = LoadingScreenSpriteCache.fromJs5(((ImageProgressBarInstance) super.anchoredElement).sprite, super.fontResource);
	}

	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(IIIZ)V")
	@Override
	protected void drawBackground(@OriginalArg(1) int y, @OriginalArg(2) int x) {
		@Pc(13) int width = this.getSmoothedProgress() * super.anchoredElement.width / 10000;
		@Pc(16) int[] clipping = new int[4];
		ClientState.activeToolkit.K(clipping);
		ClientState.activeToolkit.KA(x, y + 2, x - -width, y + super.anchoredElement.height);
		this.fillTileSprite.drawTiled(x, y + 2, super.anchoredElement.width, super.anchoredElement.height);
		ClientState.activeToolkit.KA(clipping[0], clipping[1], clipping[2], clipping[3]);
	}
}
