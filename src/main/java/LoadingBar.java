import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.gametip.component.MultiSpriteLoadingBarElement;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!ce")
public class LoadingBar extends AbstractLoadingBar {

	@OriginalMember(owner = "client!ce", name = "t", descriptor = "Lclient!mi;")
	private NativeSprite bottomBorderSprite;

	@OriginalMember(owner = "client!ce", name = "u", descriptor = "Lclient!mi;")
	private NativeSprite rightCapSprite;

	@OriginalMember(owner = "client!ce", name = "x", descriptor = "Lclient!mi;")
	private NativeSprite leftCapSprite;

	@OriginalMember(owner = "client!ce", name = "y", descriptor = "Lclient!mi;")
	private NativeSprite topBorderSprite;

	@OriginalMember(owner = "client!ce", name = "C", descriptor = "Lclient!mi;")
	private NativeSprite emptySprite;

	@OriginalMember(owner = "client!ce", name = "G", descriptor = "Lclient!mi;")
	protected NativeSprite fillSprite;

	@OriginalMember(owner = "client!ce", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!um;)V")
	public LoadingBar(@OriginalArg(0) Js5ResourceProvider index1, @OriginalArg(1) Js5ResourceProvider index2, @OriginalArg(2) MultiSpriteLoadingBarElement fancyProgressBarInstance) {
		super(index1, index2, fancyProgressBarInstance);
	}

	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(B)Z")
	@Override
	public final boolean ready() {
		if (!super.ready()) {
			return false;
		}
		@Pc(13) MultiSpriteLoadingBarElement instance = (MultiSpriteLoadingBarElement) super.anchoredElement;
		if (!super.fontResource.isFileReady(instance.spriteD)) {
			return false;
		} else if (!super.fontResource.isFileReady(instance.spriteF)) {
			return false;
		} else if (!super.fontResource.isFileReady(instance.spriteC)) {
			return false;
		} else if (!super.fontResource.isFileReady(instance.spriteB)) {
			return false;
		} else if (super.fontResource.isFileReady(instance.spriteA)) {
			return super.fontResource.isFileReady(instance.spriteE);
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(IIBII)V")
	protected void drawCompleted(@OriginalArg(0) int y, @OriginalArg(1) int height, @OriginalArg(3) int x, @OriginalArg(4) int width) {
		this.fillSprite.drawTiled(x, y, width, height);
	}

	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(IZBI)V")
	@Override
	protected final void drawBorder(@OriginalArg(0) int x, @OriginalArg(3) int y) {
		@Pc(4) int[] clipping = new int[4];
		ClientState.activeToolkit.K(clipping);
		ClientState.activeToolkit.KA(x, y, x + super.anchoredElement.width, y + super.anchoredElement.height);
		@Pc(26) int leftWidth = this.leftCapSprite.scaleWidth();
		@Pc(30) int leftHeight = this.leftCapSprite.scaleHeight();
		@Pc(34) int rightWidth = this.rightCapSprite.scaleWidth();
		@Pc(38) int rightHeight = this.rightCapSprite.scaleHeight();
		this.leftCapSprite.render(x, y + (super.anchoredElement.height - leftHeight) / 2);
		this.rightCapSprite.render(super.anchoredElement.width + x - rightWidth, y + (super.anchoredElement.height + -rightHeight) / 2);
		ClientState.activeToolkit.KA(x, y, super.anchoredElement.width + x, y - -this.topBorderSprite.scaleHeight());
		this.topBorderSprite.drawTiled(x + leftWidth, y, super.anchoredElement.width - leftWidth - rightWidth, super.anchoredElement.height);
		@Pc(111) int height = this.bottomBorderSprite.scaleHeight();
		ClientState.activeToolkit.KA(x, y + super.anchoredElement.height - height, super.anchoredElement.width + x, y + super.anchoredElement.height);
		this.bottomBorderSprite.drawTiled(leftWidth + x, -height + y + super.anchoredElement.height, super.anchoredElement.width - leftWidth - rightWidth, super.anchoredElement.height);
		ClientState.activeToolkit.KA(clipping[0], clipping[1], clipping[2], clipping[3]);
	}

	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(IIIZ)V")
	@Override
	protected final void drawBackground(@OriginalArg(1) int y, @OriginalArg(2) int x) {
		@Pc(17) int x1 = this.leftCapSprite.scaleWidth() + x;
		@Pc(28) int x2 = super.anchoredElement.width + x - this.rightCapSprite.scaleWidth();
		@Pc(35) int y1 = y + this.topBorderSprite.scaleHeight();
		@Pc(45) int y2 = y + super.anchoredElement.height - this.bottomBorderSprite.scaleHeight();
		@Pc(50) int width = x2 - x1;
		@Pc(55) int height = y2 - y1;
		@Pc(63) int progressWidth = this.getSmoothedProgress() * width / 10000;
		@Pc(66) int[] clipping = new int[4];
		ClientState.activeToolkit.K(clipping);
		ClientState.activeToolkit.KA(x1, y1, progressWidth + x1, y2);
		this.drawCompleted(y1, height, x1, width);
		ClientState.activeToolkit.KA(progressWidth + x1, y1, x2, y2);
		this.emptySprite.drawTiled(x1, y1, width, height);
		ClientState.activeToolkit.KA(clipping[0], clipping[1], clipping[2], clipping[3]);
	}

	@OriginalMember(owner = "client!ce", name = "a", descriptor = "(I)V")
	@Override
	public final void init() {
		super.init();
		@Pc(10) MultiSpriteLoadingBarElement fancyProgressBarInstance = (MultiSpriteLoadingBarElement) super.anchoredElement;
		this.fillSprite = LoadingScreenSpriteCache.fromJs5(fancyProgressBarInstance.spriteD, super.fontResource);
		this.emptySprite = LoadingScreenSpriteCache.fromJs5(fancyProgressBarInstance.spriteF, super.fontResource);
		this.leftCapSprite = LoadingScreenSpriteCache.fromJs5(fancyProgressBarInstance.spriteC, super.fontResource);
		this.rightCapSprite = LoadingScreenSpriteCache.fromJs5(fancyProgressBarInstance.spriteB, super.fontResource);
		this.topBorderSprite = LoadingScreenSpriteCache.fromJs5(fancyProgressBarInstance.spriteA, super.fontResource);
		this.bottomBorderSprite = LoadingScreenSpriteCache.fromJs5(fancyProgressBarInstance.spriteE, super.fontResource);
	}
}
