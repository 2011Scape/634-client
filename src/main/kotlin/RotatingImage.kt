import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.gametip.component.RotatingImageInstance
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.client.GameShell

@OriginalClass("client!ep")
class RotatingImage : NewsSprite {

	@OriginalMember(owner = "client!ep", name = "q", descriptor = "I")
	private var angle: Int = 0

	@OriginalMember(owner = "client!ep", name = "<init>", descriptor = "(Lclient!in;Lclient!wg;)V")
	constructor(@OriginalArg(0) spriteResource: Js5ResourceProvider?, @OriginalArg(1) rotatingImageInstance: RotatingImageInstance?) : super(spriteResource, rotatingImageInstance)

	@OriginalMember(owner = "client!ep", name = "a", descriptor = "(BZ)V")
	override fun execute() {
		@Pc(18) val x: Int = super.spritePositioning!!.horizontalAnchor!!.getX(GameShell.GAME_WIDTH, super.sprite!!.scaleWidth()) + super.spritePositioning!!.y
		@Pc(32) val y: Int = super.spritePositioning!!.verticalAnchor!!.getY(MessageBox.paddingTop, super.sprite!!.scaleHeight()) + super.spritePositioning!!.x
		super.sprite!!.drawRotatedCenter((super.sprite!!.scaleWidth() / 2 + x).toFloat(), (super.sprite!!.scaleHeight() / 2 + y).toFloat(), 4096, this.angle)
		this.angle += (super.spritePositioning as RotatingImageInstance).utAngle
	}
}
