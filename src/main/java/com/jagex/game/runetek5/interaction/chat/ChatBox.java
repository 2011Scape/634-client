package com.jagex.game.runetek5.interaction.chat;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.ChatLine;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.config.quest.QuestType;
import com.jagex.game.runetek5.graphics.SpriteDataProvider;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.graphics.sprite.StaticMedia;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.ChatLineWriter;
import com.jagex.game.runetek5.ChatFilter;
import com.jagex.game.runetek5.client.ClientState;

public final class ChatBox {
	@OriginalMember(owner = "client!ai", name = "v", descriptor = "I")
	public static int NUM_CHAT_LINES;

	private ChatBox() {
		/* empty */
	}
	@OriginalMember(owner = "client!vo", name = "p", descriptor = "[Lclient!og;")
	public static final ChatLine[] CHAT_LINES = new ChatLine[100];

	@OriginalMember(owner = "client!ic", name = "b", descriptor = "Lclient!jr;")
	public static final LRUCache CHAT_ICONS = new LRUCache(8);
	@OriginalMember(owner = "client!pe", name = "a", descriptor = "([IZ)Ljava/lang/String;")
	public static String getQuestIconTags(@OriginalArg(0) int[] questIds) {
		@Pc(9) StringBuffer stringBuffer = new StringBuffer();
		@Pc(11) int totalChatIcons = StaticMedia.totalChatIcons;
		for (@Pc(13) int i = 0; i < questIds.length; i++) {
			@Pc(22) QuestType questType = TypeLists.QUEST.list(questIds[i]);
			if (questType.graphicId != -1) {
				@Pc(35) NativeSprite sprite = (NativeSprite) ChatBox.CHAT_ICONS.get((long) questType.graphicId);
				if (sprite == null) {
					@Pc(43) SpriteType indexedImage = SpriteDataProvider.listSingle(Resource.SPRITES, questType.graphicId, 0);
					if (indexedImage != null) {
						sprite = ClientState.activeToolkit.generateSprite(indexedImage, true);
						ChatBox.CHAT_ICONS.put(sprite, (long) questType.graphicId);
					}
				}
				if (sprite != null) {
					StaticMedia.chatIcons[totalChatIcons] = sprite;
					stringBuffer.append(" <img=").append(totalChatIcons).append(">");
					totalChatIcons++;
				}
			}
		}
		return stringBuffer.toString();
	}
	@OriginalMember(owner = "client!s", name = "b", descriptor = "(II)Lclient!og;")
	public static ChatLine getChatLine(@OriginalArg(1) int line) {
		return line >= 0 && line < 100 ? ChatBox.CHAT_LINES[line] : null;
	}

	@OriginalMember(owner = "client!za", name = "q", descriptor = "I")
	public static int publicFilter = 0;
	@OriginalMember(owner = "client!wr", name = "b", descriptor = "(I)V")
	public static void clearChatLines() {
		for (@Pc(11) int i = 0; i < 100; i++) {
			ChatBox.CHAT_LINES[i] = null;
		}
		ChatBox.NUM_CHAT_LINES = 0;
	}

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(Ljava/lang/String;ZI)V")
	public static void writeGameMessage(@OriginalArg(0) String string) {
		ChatLineWriter.writeMessage("", 4, 0, string, "", "");
	}

	@OriginalMember(owner = "client!cd", name = "h", descriptor = "I")
	public static int tradeFilter = 0;
	@OriginalMember(owner = "client!b", name = "j", descriptor = "Lclient!sh;")
	public static ChatFilter privateFilter;
}
