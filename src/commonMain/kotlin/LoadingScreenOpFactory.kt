import com.jagex.game.runetek5.TipRenderDetails
import com.jagex.game.runetek5.config.gametip.component.AnimatedLoadingBarElement
import com.jagex.game.runetek5.load.ClearScreen
import com.jagex.game.runetek5.load.LoadingScreenClearConfig
import com.jagex.game.runetek5.network.newsitem.NewsItemFetcher
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem
import com.jagex.game.runetek5.client.loading.screen.op.News
import com.jagex.game.runetek5.client.loading.screen.op.Text
import com.jagex.game.runetek5.client.loading.screen.op.instance.BackgroundImageInstance
import com.jagex.game.runetek5.config.gametip.component.MultiSpriteLoadingBarElement
import com.jagex.game.runetek5.config.gametip.component.SpritePositioning
import com.jagex.game.runetek5.config.gametip.component.ImageProgressBarInstance
import com.jagex.game.runetek5.config.gametip.ITipDetails
import com.jagex.game.runetek5.config.gametip.component.OutlinedNewsItem
import com.jagex.game.runetek5.config.gametip.component.RotatingImageInstance
import com.jagex.game.runetek5.config.gametip.component.ColorLoadingBarElement
import com.jagex.game.runetek5.client.loading.screen.op.instance.TextInstance

@OriginalClass("client!mia")
class LoadingScreenOpFactory @OriginalMember(owner = "client!mia", name = "<init>", descriptor = "(Lclient!in;Lclient!in;)V") constructor(@OriginalArg(0) spriteResource: Js5ResourceProvider?, @OriginalArg(1) fontResource: Js5ResourceProvider?) {

	@OriginalMember(owner = "client!mia", name = "c", descriptor = "Lclient!eaa;")
	private var newsitemFetcher: NewsItemFetcher? = null

	@OriginalMember(owner = "client!mia", name = "g", descriptor = "Lclient!in;")
	private val spriteResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!mia", name = "k", descriptor = "Lclient!in;")
	private val fontResource: Js5ResourceProvider?

	init {
		this.spriteResource = spriteResource
		this.fontResource = fontResource
	}

	@OriginalMember(owner = "client!mia", name = "a", descriptor = "(I)Lclient!eaa;")
	private fun getNewsitemFetcher(): NewsItemFetcher? {
		if (this.newsitemFetcher == null) {
			this.newsitemFetcher = NewsItemFetcher()
		}
		return this.newsitemFetcher
	}

	@OriginalMember(owner = "client!mia", name = "a", descriptor = "(ZLclient!af;)Lclient!aaa;")
	fun fromDetails(@OriginalArg(1) loadingScreenOpInstance: ITipDetails?): DrawableItem? {
		if (loadingScreenOpInstance == null) {
			return null
		}
		@Pc(11) val renderDetails: TipRenderDetails? = loadingScreenOpInstance.getRenderDetails()
		if (TipRenderDetails.aClass6_11 === renderDetails) {
			return ClearScreen(loadingScreenOpInstance as LoadingScreenClearConfig)
		} else if (TipRenderDetails.aClass6_4 === renderDetails) {
			return News(this.getNewsitemFetcher(), loadingScreenOpInstance as OutlinedNewsItem)
		} else if (renderDetails === TipRenderDetails.aClass6_10) {
			return NewsSprite(this.spriteResource, loadingScreenOpInstance as SpritePositioning)
		} else if (renderDetails === TipRenderDetails.aClass6_5) {
			return RotatingImage(this.spriteResource, loadingScreenOpInstance as RotatingImageInstance)
		} else if (renderDetails === TipRenderDetails.aClass6_12) {
			return SolidLoadingBar(this.spriteResource, this.fontResource, loadingScreenOpInstance as ColorLoadingBarElement)
		} else if (renderDetails === TipRenderDetails.aClass6_2) {
			return SpriteLoadingBar(this.spriteResource, this.fontResource, loadingScreenOpInstance as ImageProgressBarInstance)
		} else if (renderDetails === TipRenderDetails.OPPLAYER5) {
			return LoadingBar(this.spriteResource, this.fontResource, loadingScreenOpInstance as MultiSpriteLoadingBarElement)
		} else if (renderDetails === TipRenderDetails.aClass6_7) {
			return Text(this.spriteResource, this.fontResource, loadingScreenOpInstance as TextInstance)
		} else if (TipRenderDetails.aClass6_6 === renderDetails) {
			return LoginBackgroundSprite(this.spriteResource, loadingScreenOpInstance as BackgroundImageInstance)
		} else if (TipRenderDetails.aClass6_9 === renderDetails) {
			return AnimatedProgressBar(this.spriteResource, this.fontResource, loadingScreenOpInstance as AnimatedLoadingBarElement)
		} else {
			return null
		}
	}
}
