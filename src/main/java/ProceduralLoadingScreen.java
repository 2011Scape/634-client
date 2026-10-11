import com.jagex.game.runetek5.LoadingScreenType;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.loginscreen.LoginScreenComponent;
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!qt")
public final class ProceduralLoadingScreen implements LoginScreenComponent {

	@OriginalMember(owner = "client!qt", name = "h", descriptor = "Lclient!ha;")
	private Toolkit toolkit;

	@OriginalMember(owner = "client!qt", name = "i", descriptor = "Z")
	private boolean newToolkit;

	@OriginalMember(owner = "client!qt", name = "d", descriptor = "Lclient!vp;")
	private final LoadingScreenType screen;

	@OriginalMember(owner = "client!qt", name = "j", descriptor = "[Lclient!aaa;")
	private final DrawableItem[] drawableItems;

	@OriginalMember(owner = "client!qt", name = "<init>", descriptor = "(Lclient!vp;Lclient!mia;)V")
	public ProceduralLoadingScreen(@OriginalArg(0) LoadingScreenType loadingScreenType, @OriginalArg(1) LoadingScreenOpFactory loadingScreenOpFactory) {
		this.screen = loadingScreenType;
		this.drawableItems = new DrawableItem[this.screen.ops.length];
		for (@Pc(15) int i = 0; i < this.drawableItems.length; i++) {
			this.drawableItems[i] = loadingScreenOpFactory.fromDetails(this.screen.ops[i]);
		}
	}

	@OriginalMember(owner = "client!qt", name = "a", descriptor = "(I)V")
	@Override
	public void init() {
		if (this.toolkit != ClientState.activeToolkit) {
			this.toolkit = ClientState.activeToolkit;
			this.newToolkit = true;
		}
		this.toolkit.GA(0);
		@Pc(22) DrawableItem[] ops = this.drawableItems;
		for (@Pc(24) int i = 0; i < ops.length; i++) {
			@Pc(30) DrawableItem op = ops[i];
			if (op != null) {
				op.init();
			}
		}
	}

	@OriginalMember(owner = "client!qt", name = "a", descriptor = "(ZI)V")
	@Override
	public void render(@OriginalArg(0) boolean fullRepaint) {
		@Pc(10) DrawableItem[] ops = this.drawableItems;
		for (@Pc(12) int i = 0; i < ops.length; i++) {
			@Pc(18) DrawableItem op = ops[i];
			if (op != null) {
				op.execute();
			}
		}
		this.newToolkit = false;
	}

	@OriginalMember(owner = "client!qt", name = "b", descriptor = "(I)V")
	@Override
	public void cleanup() {
	}

	@OriginalMember(owner = "client!qt", name = "a", descriptor = "(B)I")
	@Override
	public int getFadeDuration() {
		return this.screen.fadeDuration;
	}

	@OriginalMember(owner = "client!qt", name = "a", descriptor = "(BJ)Z")
	@Override
	public boolean finished(@OriginalArg(1) long initTime) {
		return Utils.currentTimeMillis() >= initTime + (long) this.screen.start;
	}

	@OriginalMember(owner = "client!qt", name = "c", descriptor = "(I)I")
	@Override
	public int percentage() {
		@Pc(14) int count = 0;
		@Pc(17) DrawableItem[] ops = this.drawableItems;
		for (@Pc(19) int i = 0; i < ops.length; i++) {
			@Pc(25) DrawableItem op = ops[i];
			if (op == null || op.ready()) {
				count++;
			}
		}
		return count * 100 / this.drawableItems.length;
	}
}
