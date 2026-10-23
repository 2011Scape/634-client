import com.jagex.game.runetek5.FontMetricsLoader
import com.jagex.game.runetek5.cache.node.JavaObjSprite
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.ToolkitType
import awt.Canvas
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
import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.config.objtype.ObjNumMode
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import kotlin.jvm.JvmStatic

object ItemSpriteBatchRender {

	/**
	 * Not part of the original client: item [objId]'s icon as ARGB pixels (36 by 32), drawn as the batch
	 * draws one (on the software toolkit), for a plugin (ClientApi.itemImage); null while its model is not
	 * loaded yet.
	 */
	@JvmStatic
	fun itemPixels(objId: Int, amount: Int): IntArray? {
		val toolkit: Toolkit = ClientState.activeToolkit ?: return null
		val renderer: Toolkit
		val font: AbstractFontRenderer?
		if (GamePreferences.uid!!.toolkit!!.getValue() == ToolkitType.JAVA) {
			renderer = toolkit
			font = Renderers.FONT_RENDERER
		} else {
			if (Renderers.GPU_ITEM_RENDERER == null) {
				val canvas: Canvas = Canvas()
				canvas.setSize(36, 32)
				Renderers.GPU_ITEM_RENDERER = CanvasToolkitFactory.create(ToolkitType.JAVA, Js5TextureSource.anInterface4_10, 0, canvas, Resource.SHADER)
				Renderers.GPU_ITEM_FONT_RENDERER = Renderers.GPU_ITEM_RENDERER!!.createFontRenderer(FontMetricsLoader.getFontMetrics(FontManager.p11FullIndex, Resource.FONT_METRICS), SpriteType.decodeGroup(Resource.SPRITES, FontManager.p11FullIndex))
			}
			renderer = Renderers.GPU_ITEM_RENDERER!!
			font = Renderers.GPU_ITEM_FONT_RENDERER
		}
		var type: ItemType = TypeLists.OBJ!!.list(objId)
		val stacks: IntArray? = type.stackIds
		if (amount > 1 && stacks != null) {
			// the stack's own look, as ObjTypeList.getSprite picks it
			var stackId: Int = -1
			for (i in 0 until 10) {
				if (amount >= type.stackTriggerAmount!![i] && type.stackTriggerAmount!![i] != 0) {
					stackId = stacks[i]
				}
			}
			if (stackId != -1) {
				type = TypeLists.OBJ!!.list(stackId)
			}
		}
		return type.getSpritePixels(amount, false, 3153952, toolkit, renderer, font, null, ObjNumMode.SHOWCOUNT_IFNOT1, 1)
	}

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
