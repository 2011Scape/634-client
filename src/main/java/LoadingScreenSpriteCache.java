import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.SpriteDataProvider;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.LoadingScreenManager;
import com.jagex.game.runetek5.client.ClientState;

public final class LoadingScreenSpriteCache {

	@OriginalMember(owner = "client!fu", name = "a", descriptor = "(IBLclient!in;)Lclient!mi;")
	public static NativeSprite fromJs5(@OriginalArg(0) int fileId, @OriginalArg(2) Js5ResourceProvider spriteResource) {
		@Pc(10) NativeSprite sprite = (NativeSprite) LoadingScreenManager.aClass169_57.get((long) fileId);
		if (sprite == null) {
			if (LoadingScreenSpriteCache.useRawLoadingSprites) {
				sprite = ClientState.activeToolkit.generateSprite(SpriteDataProvider.listSingle(spriteResource, fileId), true);
			} else {
				sprite = JpegSpriteDecoder.decode(spriteResource.getFile(fileId));
			}
			LoadingScreenManager.aClass169_57.put(sprite, (long) fileId);
		}
		return sprite;
	}
	@OriginalMember(owner = "client!tn", name = "q", descriptor = "Z")
	public static boolean useRawLoadingSprites;
}
