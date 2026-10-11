import com.jagex.game.runetek5.TipRenderDetails;
import com.jagex.game.runetek5.config.gametip.component.AnimatedLoadingBarElement;
import com.jagex.game.runetek5.load.ClearScreen;
import com.jagex.game.runetek5.load.LoadingScreenClearConfig;
import com.jagex.game.runetek5.network.newsitem.NewsItemFetcher;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem;
import com.jagex.game.runetek5.client.loading.screen.op.News;
import com.jagex.game.runetek5.client.loading.screen.op.Text;
import com.jagex.game.runetek5.client.loading.screen.op.instance.BackgroundImageInstance;
import com.jagex.game.runetek5.config.gametip.component.MultiSpriteLoadingBarElement;
import com.jagex.game.runetek5.config.gametip.component.SpritePositioning;
import com.jagex.game.runetek5.config.gametip.component.ImageProgressBarInstance;
import com.jagex.game.runetek5.config.gametip.ITipDetails;
import com.jagex.game.runetek5.config.gametip.component.OutlinedNewsItem;
import com.jagex.game.runetek5.config.gametip.component.RotatingImageInstance;
import com.jagex.game.runetek5.config.gametip.component.ColorLoadingBarElement;
import com.jagex.game.runetek5.client.loading.screen.op.instance.TextInstance;

@OriginalClass("client!mia")
public final class LoadingScreenOpFactory {

	@OriginalMember(owner = "client!mia", name = "c", descriptor = "Lclient!eaa;")
	private NewsItemFetcher newsitemFetcher;

	@OriginalMember(owner = "client!mia", name = "g", descriptor = "Lclient!in;")
	private final Js5ResourceProvider spriteResource;

	@OriginalMember(owner = "client!mia", name = "k", descriptor = "Lclient!in;")
	private final Js5ResourceProvider fontResource;

	@OriginalMember(owner = "client!mia", name = "<init>", descriptor = "(Lclient!in;Lclient!in;)V")
	public LoadingScreenOpFactory(@OriginalArg(0) Js5ResourceProvider spriteResource, @OriginalArg(1) Js5ResourceProvider fontResource) {
		this.spriteResource = spriteResource;
		this.fontResource = fontResource;
	}

	@OriginalMember(owner = "client!mia", name = "a", descriptor = "(I)Lclient!eaa;")
	private NewsItemFetcher getNewsitemFetcher() {
		if (this.newsitemFetcher == null) {
			this.newsitemFetcher = new NewsItemFetcher();
		}
		return this.newsitemFetcher;
	}

	@OriginalMember(owner = "client!mia", name = "a", descriptor = "(ZLclient!af;)Lclient!aaa;")
	public DrawableItem fromDetails(@OriginalArg(1) ITipDetails loadingScreenOpInstance) {
		if (loadingScreenOpInstance == null) {
			return null;
		}
		@Pc(11) TipRenderDetails renderDetails = loadingScreenOpInstance.getRenderDetails();
		if (TipRenderDetails.aClass6_11 == renderDetails) {
			return new ClearScreen((LoadingScreenClearConfig) loadingScreenOpInstance);
		} else if (TipRenderDetails.aClass6_4 == renderDetails) {
			return new News(this.getNewsitemFetcher(), (OutlinedNewsItem) loadingScreenOpInstance);
		} else if (renderDetails == TipRenderDetails.aClass6_10) {
			return new NewsSprite(this.spriteResource, (SpritePositioning) loadingScreenOpInstance);
		} else if (renderDetails == TipRenderDetails.aClass6_5) {
			return new RotatingImage(this.spriteResource, (RotatingImageInstance) loadingScreenOpInstance);
		} else if (renderDetails == TipRenderDetails.aClass6_12) {
			return new SolidLoadingBar(this.spriteResource, this.fontResource, (ColorLoadingBarElement) loadingScreenOpInstance);
		} else if (renderDetails == TipRenderDetails.aClass6_2) {
			return new SpriteLoadingBar(this.spriteResource, this.fontResource, (ImageProgressBarInstance) loadingScreenOpInstance);
		} else if (renderDetails == TipRenderDetails.OPPLAYER5) {
			return new LoadingBar(this.spriteResource, this.fontResource, (MultiSpriteLoadingBarElement) loadingScreenOpInstance);
		} else if (renderDetails == TipRenderDetails.aClass6_7) {
			return new Text(this.spriteResource, this.fontResource, (TextInstance) loadingScreenOpInstance);
		} else if (TipRenderDetails.aClass6_6 == renderDetails) {
			return new LoginBackgroundSprite(this.spriteResource, (BackgroundImageInstance) loadingScreenOpInstance);
		} else if (TipRenderDetails.aClass6_9 == renderDetails) {
			return new AnimatedProgressBar(this.spriteResource, this.fontResource, (AnimatedLoadingBarElement) loadingScreenOpInstance);
		} else {
			return null;
		}
	}
}
