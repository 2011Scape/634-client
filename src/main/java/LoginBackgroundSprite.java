import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem;
import com.jagex.game.runetek5.client.loading.screen.op.instance.BackgroundImageInstance;
import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.client.GameShell;

@OriginalClass("client!uk")
public final class LoginBackgroundSprite implements DrawableItem {

	@OriginalMember(owner = "client!uk", name = "b", descriptor = "Lclient!mi;")
	private NativeSprite nativeSprite;

	@OriginalMember(owner = "client!uk", name = "h", descriptor = "Lclient!in;")
	private final Js5ResourceProvider spriteResource;

	@OriginalMember(owner = "client!uk", name = "a", descriptor = "Lclient!nfa;")
	private final BackgroundImageInstance tipsSprite;

	@OriginalMember(owner = "client!uk", name = "<init>", descriptor = "(Lclient!in;Lclient!nfa;)V")
	public LoginBackgroundSprite(@OriginalArg(0) Js5ResourceProvider spriteResource, @OriginalArg(1) BackgroundImageInstance backgroundImageInstance) {
		this.spriteResource = spriteResource;
		this.tipsSprite = backgroundImageInstance;
	}

	@OriginalMember(owner = "client!uk", name = "a", descriptor = "(I)V")
	@Override
	public void init() {
		this.nativeSprite = LoadingScreenSpriteCache.fromJs5(this.tipsSprite.image, this.spriteResource);
	}

	@OriginalMember(owner = "client!uk", name = "a", descriptor = "(BZ)V")
	@Override
	public void execute() {
		@Pc(19) int maxWidth = GameShell.screenCanvasWidth > GameShell.GAME_WIDTH ? GameShell.screenCanvasWidth : GameShell.GAME_WIDTH;
		@Pc(30) int maxHeight = MessageBox.paddingTop < GameShell.screenCanvasHeight ? GameShell.screenCanvasHeight : MessageBox.paddingTop;
		@Pc(34) int spriteWidth = this.nativeSprite.scaleWidth();
		@Pc(38) int spriteHeight = this.nativeSprite.scaleHeight();
		@Pc(40) int x = 0;
		@Pc(42) int width = maxWidth;
		@Pc(48) int height = maxWidth * spriteHeight / spriteWidth;
		@Pc(55) int y = (maxHeight - height) / 2;
		if (maxHeight < height) {
			height = maxHeight;
			width = maxHeight * spriteWidth / spriteHeight;
			y = 0;
			x = (maxWidth - width) / 2;
		}
		this.nativeSprite.drawTintedScaled(x, y, width, height);
	}

	@OriginalMember(owner = "client!uk", name = "a", descriptor = "(B)Z")
	@Override
	public boolean ready() {
		return this.spriteResource.isFileReady(this.tipsSprite.image);
	}
}
