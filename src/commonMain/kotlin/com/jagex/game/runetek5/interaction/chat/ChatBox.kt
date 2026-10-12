package com.jagex.game.runetek5.interaction.chat

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.ChatLine
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.quest.QuestType
import com.jagex.game.runetek5.graphics.SpriteDataProvider
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.graphics.sprite.StaticMedia
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.ChatLineWriter
import com.jagex.game.runetek5.ChatFilter
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.StringBuffer

object ChatBox {
	@OriginalMember(owner = "client!ai", name = "v", descriptor = "I")
	@JvmField
	var NUM_CHAT_LINES: Int = 0

	@OriginalMember(owner = "client!vo", name = "p", descriptor = "[Lclient!og;")
	@JvmField
	val CHAT_LINES: Array<ChatLine?> = arrayOfNulls<ChatLine>(100)

	@OriginalMember(owner = "client!ic", name = "b", descriptor = "Lclient!jr;")
	@JvmField
	val CHAT_ICONS: LRUCache = LRUCache(8)
	@OriginalMember(owner = "client!pe", name = "a", descriptor = "([IZ)Ljava/lang/String;")
	@JvmStatic
	fun getQuestIconTags(@OriginalArg(0) questIds: IntArray?): String {
		@Pc(9) val stringBuffer: StringBuffer = StringBuffer()
		@Pc(11) var totalChatIcons: Int = StaticMedia.totalChatIcons
		@Pc(13) var i: Int = 0
		while (i < questIds!!.size) {
			@Pc(22) val questType: QuestType? = TypeLists.QUEST!!.list(questIds[i])
			if (questType!!.graphicId != -1) {
				@Pc(35) var sprite: NativeSprite? = ChatBox.CHAT_ICONS.get(questType.graphicId.toLong()) as NativeSprite?
				if (sprite == null) {
					@Pc(43) val indexedImage: SpriteType? = SpriteDataProvider.listSingle(Resource.SPRITES, questType.graphicId, 0)
					if (indexedImage != null) {
						sprite = ClientState.activeToolkit!!.generateSprite(indexedImage, true)
						ChatBox.CHAT_ICONS.put(sprite, questType.graphicId.toLong())
					}
				}
				if (sprite != null) {
					StaticMedia.chatIcons!![totalChatIcons] = sprite
					stringBuffer.append(" <img=").append(totalChatIcons).append(">")
					totalChatIcons++
				}
			}
			i++
		}
		return stringBuffer.toString()
	}
	@OriginalMember(owner = "client!s", name = "b", descriptor = "(II)Lclient!og;")
	@JvmStatic
	fun getChatLine(@OriginalArg(1) line: Int): ChatLine? {
		return if (line >= 0 && line < 100) ChatBox.CHAT_LINES[line] else null
	}

	@OriginalMember(owner = "client!za", name = "q", descriptor = "I")
	@JvmField
	var publicFilter: Int = 0
	@OriginalMember(owner = "client!wr", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun clearChatLines() {
		@Pc(11) var i: Int = 0
		while (i < 100) {
			ChatBox.CHAT_LINES[i] = null
			i++
		}
		ChatBox.NUM_CHAT_LINES = 0
	}

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(Ljava/lang/String;ZI)V")
	@JvmStatic
	fun writeGameMessage(@OriginalArg(0) string: String?) {
		ChatLineWriter.writeMessage("", 4, 0, string, "", "")
	}

	@OriginalMember(owner = "client!cd", name = "h", descriptor = "I")
	@JvmField
	var tradeFilter: Int = 0
	@OriginalMember(owner = "client!b", name = "j", descriptor = "Lclient!sh;")
	@JvmField
	var privateFilter: ChatFilter? = null
}
