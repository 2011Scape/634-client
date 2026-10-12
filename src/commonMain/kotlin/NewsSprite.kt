import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem
import com.jagex.game.runetek5.config.gametip.component.SpritePositioning
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.client.GameShell
import kotlin.jvm.JvmField

@OriginalClass("client!nl")
open class NewsSprite : DrawableItem {

	@OriginalMember(owner = "client!nl", name = "d", descriptor = "Lclient!mi;")
	@JvmField
	protected var sprite: NativeSprite? = null

	@OriginalMember(owner = "client!nl", name = "j", descriptor = "Lclient!in;")
	private val spriteResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!nl", name = "h", descriptor = "Lclient!vr;")
	@JvmField
	protected val spritePositioning: SpritePositioning?

	@OriginalMember(owner = "client!nl", name = "<init>", descriptor = "(Lclient!in;Lclient!vr;)V")
	constructor(@OriginalArg(0) spriteResource: Js5ResourceProvider?, @OriginalArg(1) imageInstance: SpritePositioning?) {
		this.spriteResource = spriteResource
		this.spritePositioning = imageInstance
	}

	@OriginalMember(owner = "client!nl", name = "a", descriptor = "(B)Z")
	final override fun ready(): Boolean {
		return this.spriteResource!!.isFileReady(this.spritePositioning!!.spriteId)
	}

	@OriginalMember(owner = "client!nl", name = "a", descriptor = "(BZ)V")
	override fun execute() {
		@Pc(19) val x: Int = this.spritePositioning!!.horizontalAnchor!!.getX(GameShell.GAME_WIDTH, this.sprite!!.scaleWidth()) + this.spritePositioning.y
		@Pc(34) val y: Int = this.spritePositioning.verticalAnchor!!.getY(MessageBox.paddingTop, this.sprite!!.scaleHeight()) + this.spritePositioning.x
		this.sprite!!.render(x, y)
	}

	@OriginalMember(owner = "client!nl", name = "a", descriptor = "(I)V")
	final override fun init() {
		this.sprite = LoadingScreenSpriteCache.fromJs5(this.spritePositioning!!.spriteId, this.spriteResource)
	}
}
