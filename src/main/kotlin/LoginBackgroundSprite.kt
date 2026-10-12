import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem
import com.jagex.game.runetek5.client.loading.screen.op.instance.BackgroundImageInstance
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.client.GameShell

@OriginalClass("client!uk")
class LoginBackgroundSprite : DrawableItem {

	@OriginalMember(owner = "client!uk", name = "b", descriptor = "Lclient!mi;")
	private var nativeSprite: NativeSprite? = null

	@OriginalMember(owner = "client!uk", name = "h", descriptor = "Lclient!in;")
	private val spriteResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!uk", name = "a", descriptor = "Lclient!nfa;")
	private val tipsSprite: BackgroundImageInstance?

	@OriginalMember(owner = "client!uk", name = "<init>", descriptor = "(Lclient!in;Lclient!nfa;)V")
	constructor(@OriginalArg(0) spriteResource: Js5ResourceProvider?, @OriginalArg(1) backgroundImageInstance: BackgroundImageInstance?) {
		this.spriteResource = spriteResource
		this.tipsSprite = backgroundImageInstance
	}

	@OriginalMember(owner = "client!uk", name = "a", descriptor = "(I)V")
	override fun init() {
		this.nativeSprite = LoadingScreenSpriteCache.fromJs5(this.tipsSprite!!.image, this.spriteResource)
	}

	@OriginalMember(owner = "client!uk", name = "a", descriptor = "(BZ)V")
	override fun execute() {
		@Pc(19) val maxWidth: Int = if (GameShell.screenCanvasWidth > GameShell.GAME_WIDTH) GameShell.screenCanvasWidth else GameShell.GAME_WIDTH
		@Pc(30) val maxHeight: Int = if (MessageBox.paddingTop < GameShell.screenCanvasHeight) GameShell.screenCanvasHeight else MessageBox.paddingTop
		@Pc(34) val spriteWidth: Int = this.nativeSprite!!.scaleWidth()
		@Pc(38) val spriteHeight: Int = this.nativeSprite!!.scaleHeight()
		@Pc(40) var x: Int = 0
		@Pc(42) var width: Int = maxWidth
		@Pc(48) var height: Int = maxWidth * spriteHeight / spriteWidth
		@Pc(55) var y: Int = (maxHeight - height) / 2
		if (maxHeight < height) {
			height = maxHeight
			width = maxHeight * spriteWidth / spriteHeight
			y = 0
			x = (maxWidth - width) / 2
		}
		this.nativeSprite!!.drawTintedScaled(x, y, width, height)
	}

	@OriginalMember(owner = "client!uk", name = "a", descriptor = "(B)Z")
	override fun ready(): Boolean {
		return this.spriteResource!!.isFileReady(this.tipsSprite!!.image)
	}
}
