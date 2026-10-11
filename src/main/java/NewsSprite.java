import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem;
import com.jagex.game.runetek5.config.gametip.component.SpritePositioning;
import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.client.GameShell;

@OriginalClass("client!nl")
public class NewsSprite implements DrawableItem {

	@OriginalMember(owner = "client!nl", name = "d", descriptor = "Lclient!mi;")
	protected NativeSprite sprite;

	@OriginalMember(owner = "client!nl", name = "j", descriptor = "Lclient!in;")
	private final Js5ResourceProvider spriteResource;

	@OriginalMember(owner = "client!nl", name = "h", descriptor = "Lclient!vr;")
	protected final SpritePositioning spritePositioning;

	@OriginalMember(owner = "client!nl", name = "<init>", descriptor = "(Lclient!in;Lclient!vr;)V")
	public NewsSprite(@OriginalArg(0) Js5ResourceProvider spriteResource, @OriginalArg(1) SpritePositioning imageInstance) {
		this.spriteResource = spriteResource;
		this.spritePositioning = imageInstance;
	}

	@OriginalMember(owner = "client!nl", name = "a", descriptor = "(B)Z")
	@Override
	public final boolean ready() {
		return this.spriteResource.isFileReady(this.spritePositioning.spriteId);
	}

	@OriginalMember(owner = "client!nl", name = "a", descriptor = "(BZ)V")
	@Override
	public void execute() {
		@Pc(19) int x = this.spritePositioning.horizontalAnchor.getX(GameShell.GAME_WIDTH, this.sprite.scaleWidth()) + this.spritePositioning.y;
		@Pc(34) int y = this.spritePositioning.verticalAnchor.getY(MessageBox.paddingTop, this.sprite.scaleHeight()) + this.spritePositioning.x;
		this.sprite.render(x, y);
	}

	@OriginalMember(owner = "client!nl", name = "a", descriptor = "(I)V")
	@Override
	public final void init() {
		this.sprite = LoadingScreenSpriteCache.fromJs5(this.spritePositioning.spriteId, this.spriteResource);
	}
}
