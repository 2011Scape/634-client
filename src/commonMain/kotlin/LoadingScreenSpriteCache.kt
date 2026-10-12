import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.SpriteDataProvider
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.LoadingScreenManager
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object LoadingScreenSpriteCache {

	@OriginalMember(owner = "client!fu", name = "a", descriptor = "(IBLclient!in;)Lclient!mi;")
	@JvmStatic
	fun fromJs5(@OriginalArg(0) fileId: Int, @OriginalArg(2) spriteResource: Js5ResourceProvider?): NativeSprite? {
		@Pc(10) var sprite: NativeSprite? = LoadingScreenManager.aClass169_57.get(fileId.toLong()) as NativeSprite?
		if (sprite == null) {
			if (LoadingScreenSpriteCache.useRawLoadingSprites) {
				sprite = ClientState.activeToolkit!!.generateSprite(SpriteDataProvider.listSingle(spriteResource, fileId), true)
			} else {
				sprite = JpegSpriteDecoder.decode(spriteResource!!.getFile(fileId))
			}
			LoadingScreenManager.aClass169_57.put(sprite, fileId.toLong())
		}
		return sprite
	}
	@OriginalMember(owner = "client!tn", name = "q", descriptor = "Z")
	@JvmField
	var useRawLoadingSprites: Boolean = false
}
