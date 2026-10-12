import com.jagex.game.runetek5.FontMetricsLoader
import com.jagex.game.runetek5.cache.node.JavaObjSprite
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.ToolkitType
import java.awt.Canvas
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.rendering.Renderers
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.Js5TextureSource

object ItemSpriteBatchRender {

	@OriginalMember(owner = "client!hk", name = "a", descriptor = "(Lclient!ha;I)V")
	@JvmStatic
	fun renderItems(@OriginalArg(0) toolkit: Toolkit?) {
		if (Component.ITEMS.size() == 0) {
			return
		}
		@Pc(23) var javaObjSprite: JavaObjSprite?
		if (GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.JAVA) {
			javaObjSprite = Component.ITEMS.first() as JavaObjSprite?
			while (javaObjSprite != null) {
				TypeLists.OBJ!!.getSprite(toolkit, javaObjSprite.objNumMode, javaObjSprite.graphicShadow, Renderers.FONT_RENDERER, if (javaObjSprite.objWearCol) PlayerUpdate.localPlayer!!.playerModel else null, javaObjSprite.objId, false, toolkit, javaObjSprite.outline, false, javaObjSprite.invCount)
				javaObjSprite.unlink()
				javaObjSprite = Component.ITEMS.next() as JavaObjSprite?
			}
			InterfaceRectangles.invalidateAllRectangles()
			return
		}
		if (Renderers.GPU_ITEM_RENDERER == null) {
			@Pc(72) val canvas: Canvas = Canvas()
			canvas.setSize(36, 32)
			Renderers.GPU_ITEM_RENDERER = CanvasToolkitFactory.create(ToolkitType.JAVA, Js5TextureSource.anInterface4_10, 0, canvas, Resource.SHADER)
			Renderers.GPU_ITEM_FONT_RENDERER = Renderers.GPU_ITEM_RENDERER!!.createFontRenderer(FontMetricsLoader.getFontMetrics(FontManager.p11FullIndex, Resource.FONT_METRICS), SpriteType.decodeGroup(Resource.SPRITES, FontManager.p11FullIndex))
		}
		javaObjSprite = Component.ITEMS.first() as JavaObjSprite?
		while (javaObjSprite != null) {
			TypeLists.OBJ!!.getSprite(Renderers.GPU_ITEM_RENDERER, javaObjSprite.objNumMode, javaObjSprite.graphicShadow, Renderers.GPU_ITEM_FONT_RENDERER, if (javaObjSprite.objWearCol) PlayerUpdate.localPlayer!!.playerModel else null, javaObjSprite.objId, false, toolkit, javaObjSprite.outline, false, javaObjSprite.invCount)
			javaObjSprite.unlink()
			javaObjSprite = Component.ITEMS.next() as JavaObjSprite?
		}
	}
}
