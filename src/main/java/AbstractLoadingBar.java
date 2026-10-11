import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem;
import com.jagex.game.runetek5.config.gametip.component.AnchoredElement;
import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!gl")
public abstract class AbstractLoadingBar implements DrawableItem {

	@OriginalMember(owner = "client!gl", name = "d", descriptor = "I")
	private int lastLoadPercent;

	@OriginalMember(owner = "client!gl", name = "k", descriptor = "Lclient!da;")
	private AbstractFontRenderer fontRenderer;

	@OriginalMember(owner = "client!gl", name = "n", descriptor = "J")
	private long lastLoadPercentChange;

	@OriginalMember(owner = "client!gl", name = "e", descriptor = "Lclient!in;")
	private final Js5ResourceProvider spriteResource;

	@OriginalMember(owner = "client!gl", name = "f", descriptor = "Lclient!in;")
	protected final Js5ResourceProvider fontResource;

	@OriginalMember(owner = "client!gl", name = "i", descriptor = "Lclient!fn;")
	protected final AnchoredElement anchoredElement;

	@OriginalMember(owner = "client!gl", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!fn;)V")
	protected AbstractLoadingBar(@OriginalArg(0) Js5ResourceProvider fontResource, @OriginalArg(1) Js5ResourceProvider spriteResource, @OriginalArg(2) AnchoredElement progressBarInstance) {
		this.spriteResource = spriteResource;
		this.fontResource = fontResource;
		this.anchoredElement = progressBarInstance;
	}

	@OriginalMember(owner = "client!gl", name = "a", descriptor = "(IZBI)V")
	protected abstract void drawBorder(@OriginalArg(0) int x, @OriginalArg(3) int y);

	@OriginalMember(owner = "client!gl", name = "a", descriptor = "(B)Z")
	@Override
	public boolean ready() {
		@Pc(13) boolean cached = true;
		if (!this.fontResource.isFileReady(this.anchoredElement.fileId)) {
			cached = false;
		}
		if (!this.spriteResource.isFileReady(this.anchoredElement.fileId)) {
			cached = false;
		}
		return cached;
	}

	@OriginalMember(owner = "client!gl", name = "a", descriptor = "(I)V")
	@Override
	public void init() {
		@Pc(15) FontMetrics fontMetrics = FontMetrics.list(this.anchoredElement.fileId, this.spriteResource);
		this.fontRenderer = ClientState.activeToolkit.createFontRenderer(fontMetrics, SpriteType.listGroup(this.fontResource, this.anchoredElement.fileId));
	}

	@OriginalMember(owner = "client!gl", name = "a", descriptor = "(IIIZ)V")
	protected abstract void drawBackground(@OriginalArg(1) int y, @OriginalArg(2) int x);

	@OriginalMember(owner = "client!gl", name = "c", descriptor = "(I)I")
	protected final int getSmoothedProgress() {
		@Pc(15) int loadPercentage = ActiveLoadingScreenRenderer.aClass8_1.getLoadingPercentage();
		@Pc(19) int progress = loadPercentage * 100;
		if (loadPercentage == this.lastLoadPercent && loadPercentage != 0) {
			@Pc(45) int stageCompletion = ActiveLoadingScreenRenderer.aClass8_1.getLoadingStageCompletion();
			if (loadPercentage < stageCompletion) {
				@Pc(55) long timeDelta = this.lastLoadPercentChange - ActiveLoadingScreenRenderer.aClass8_1.getLoadingStageChangedTime();
				if (timeDelta > 0L) {
					@Pc(71) long timeForCompletion = (long) (stageCompletion - loadPercentage) * (timeDelta * 10000L / (long) loadPercentage);
					@Pc(80) long elapsedTime = (Utils.currentTimeMillis() - this.lastLoadPercentChange) * 10000L;
					if (timeForCompletion > elapsedTime) {
						progress = (int) ((long) (loadPercentage * 100) + (long) (stageCompletion - loadPercentage) * elapsedTime * 100L / timeForCompletion);
					} else {
						progress = stageCompletion * 100;
					}
				}
			}
		} else {
			this.lastLoadPercent = loadPercentage;
			this.lastLoadPercentChange = Utils.currentTimeMillis();
		}
		return progress;
	}

	@OriginalMember(owner = "client!gl", name = "a", descriptor = "(BZ)V")
	@Override
	public final void execute() {
		@Pc(21) int x = this.anchoredElement.horizontalAnchor.getX(GameShell.GAME_WIDTH, this.anchoredElement.width) + this.anchoredElement.offsetX;
		@Pc(37) int y = this.anchoredElement.verticalAnchor.getY(MessageBox.paddingTop, this.anchoredElement.height) + this.anchoredElement.offsetY;
		this.drawBorder(x, y);
		this.drawBackground(y, x);
		@Pc(55) String string = ActiveLoadingScreenRenderer.aClass8_1.getLoadingText();
		if (Utils.currentTimeMillis() - this.lastLoadPercentChange > 10000L) {
			string = string + " (" + ActiveLoadingScreenRenderer.aClass8_1.getLoadingStage().getStep() + ")";
		}
		this.fontRenderer.renderCentered(x + this.anchoredElement.width / 2, this.anchoredElement.textColor, string, -1, this.anchoredElement.textOffsetY + y + this.anchoredElement.height / 2 + 4);
	}
}
