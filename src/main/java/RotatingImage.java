import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.gametip.component.RotatingImageInstance;
import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.client.GameShell;

@OriginalClass("client!ep")
public final class RotatingImage extends NewsSprite {

	@OriginalMember(owner = "client!ep", name = "q", descriptor = "I")
	private int angle = 0;

	@OriginalMember(owner = "client!ep", name = "<init>", descriptor = "(Lclient!in;Lclient!wg;)V")
	public RotatingImage(@OriginalArg(0) Js5ResourceProvider spriteResource, @OriginalArg(1) RotatingImageInstance rotatingImageInstance) {
		super(spriteResource, rotatingImageInstance);
	}

	@OriginalMember(owner = "client!ep", name = "a", descriptor = "(BZ)V")
	@Override
	public void execute() {
		@Pc(18) int x = super.spritePositioning.horizontalAnchor.getX(GameShell.GAME_WIDTH, super.sprite.scaleWidth()) + super.spritePositioning.y;
		@Pc(32) int y = super.spritePositioning.verticalAnchor.getY(MessageBox.paddingTop, super.sprite.scaleHeight()) + super.spritePositioning.x;
		super.sprite.drawRotatedCenter((float) (super.sprite.scaleWidth() / 2 + x), (float) (super.sprite.scaleHeight() / 2 + y), 4096, this.angle);
		this.angle += ((RotatingImageInstance) super.spritePositioning).utAngle;
	}
}
