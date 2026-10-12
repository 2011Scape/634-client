import com.jagex.game.runetek5.LoadingScreenType
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.loginscreen.LoginScreenComponent
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!qt")
class ProceduralLoadingScreen : LoginScreenComponent {

	@OriginalMember(owner = "client!qt", name = "h", descriptor = "Lclient!ha;")
	private var toolkit: Toolkit? = null

	@OriginalMember(owner = "client!qt", name = "i", descriptor = "Z")
	private var newToolkit: Boolean = false

	@OriginalMember(owner = "client!qt", name = "d", descriptor = "Lclient!vp;")
	private val screen: LoadingScreenType?

	@OriginalMember(owner = "client!qt", name = "j", descriptor = "[Lclient!aaa;")
	private val drawableItems: Array<DrawableItem?>

	@OriginalMember(owner = "client!qt", name = "<init>", descriptor = "(Lclient!vp;Lclient!mia;)V")
	constructor(@OriginalArg(0) loadingScreenType: LoadingScreenType?, @OriginalArg(1) loadingScreenOpFactory: LoadingScreenOpFactory?) {
		this.screen = loadingScreenType
		this.drawableItems = arrayOfNulls<DrawableItem>(this.screen!!.ops!!.size)
		@Pc(15) var i: Int = 0
		while (i < this.drawableItems.size) {
			this.drawableItems[i] = loadingScreenOpFactory!!.fromDetails(this.screen.ops!![i])
			i++
		}
	}

	@OriginalMember(owner = "client!qt", name = "a", descriptor = "(I)V")
	override fun init() {
		if (this.toolkit !== ClientState.activeToolkit) {
			this.toolkit = ClientState.activeToolkit
			this.newToolkit = true
		}
		this.toolkit!!.GA(0)
		@Pc(22) val ops: Array<DrawableItem?> = this.drawableItems
		@Pc(24) var i: Int = 0
		while (i < ops.size) {
			@Pc(30) val op: DrawableItem? = ops[i]
			if (op != null) {
				op.init()
			}
			i++
		}
	}

	@OriginalMember(owner = "client!qt", name = "a", descriptor = "(ZI)V")
	override fun render(@OriginalArg(0) fullRepaint: Boolean) {
		@Pc(10) val ops: Array<DrawableItem?> = this.drawableItems
		@Pc(12) var i: Int = 0
		while (i < ops.size) {
			@Pc(18) val op: DrawableItem? = ops[i]
			if (op != null) {
				op.execute()
			}
			i++
		}
		this.newToolkit = false
	}

	@OriginalMember(owner = "client!qt", name = "b", descriptor = "(I)V")
	override fun cleanup() {
	}

	@OriginalMember(owner = "client!qt", name = "a", descriptor = "(B)I")
	override fun getFadeDuration(): Int {
		return this.screen!!.fadeDuration
	}

	@OriginalMember(owner = "client!qt", name = "a", descriptor = "(BJ)Z")
	override fun finished(@OriginalArg(1) initTime: Long): Boolean {
		return Utils.currentTimeMillis() >= initTime + this.screen!!.start.toLong()
	}

	@OriginalMember(owner = "client!qt", name = "c", descriptor = "(I)I")
	override fun percentage(): Int {
		@Pc(14) var count: Int = 0
		@Pc(17) val ops: Array<DrawableItem?> = this.drawableItems
		@Pc(19) var i: Int = 0
		while (i < ops.size) {
			@Pc(25) val op: DrawableItem? = ops[i]
			if (op == null || op.ready()) {
				count++
			}
			i++
		}
		return count * 100 / this.drawableItems.size
	}
}
