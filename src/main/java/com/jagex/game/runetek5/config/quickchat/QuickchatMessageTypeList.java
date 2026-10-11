package com.jagex.game.runetek5.config.quickchat;

import com.jagex.game.runetek5.QuickChatValueType;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.quickchat.QuickchatFillerList;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.QuickChatPhrase;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.Find;

@OriginalClass("client!aha")
public final class QuickchatMessageTypeList {

	@OriginalMember(owner = "client!aha", name = "d", descriptor = "I")
	public int configClientSmallNum = 0;

	@OriginalMember(owner = "client!aha", name = "c", descriptor = "I")
	public int configClientLargeNum = 0;

	@OriginalMember(owner = "client!aha", name = "a", descriptor = "Lclient!jr;")
	private final LRUCache messageTypeCache = new LRUCache(64);

	@OriginalMember(owner = "client!aha", name = "l", descriptor = "Lclient!bq;")
	private QuickchatFillerList quickchatFillerlist = null;

	@OriginalMember(owner = "client!aha", name = "g", descriptor = "Lclient!in;")
	private final Js5ResourceProvider messageResource;

	@OriginalMember(owner = "client!aha", name = "h", descriptor = "Lclient!in;")
	private final Js5ResourceProvider menuResource;

	@OriginalMember(owner = "client!aha", name = "<init>", descriptor = "(ILclient!in;Lclient!in;Lclient!bq;)V")
	public QuickchatMessageTypeList(@OriginalArg(0) int languageId, @OriginalArg(1) Js5ResourceProvider messageResource, @OriginalArg(2) Js5ResourceProvider menuResource, @OriginalArg(3) QuickchatFillerList quickChatFillerDecoder) {
		this.quickchatFillerlist = quickChatFillerDecoder;
		this.messageResource = messageResource;
		this.menuResource = menuResource;
		if (this.messageResource != null) {
			this.configClientSmallNum = this.messageResource.getGroupCapacity(1);
		}
		if (this.menuResource != null) {
			this.configClientLargeNum = this.menuResource.getGroupCapacity(1);
		}
	}

	@OriginalMember(owner = "client!aha", name = "a", descriptor = "(IB)Lclient!td;")
	public QuickchatMessageType list(@OriginalArg(0) int id) {
		@Pc(11) QuickchatMessageType quickChatPhraseType = (QuickchatMessageType) this.messageTypeCache.get((long) id);
		if (quickChatPhraseType != null) {
			return quickChatPhraseType;
		}
		@Pc(29) byte[] data;
		if (id >= 32768) {
			data = this.menuResource.getFile(1, id & 0x7FFF);
		} else {
			data = this.messageResource.getFile(1, id);
		}
		quickChatPhraseType = new QuickchatMessageType();
		quickChatPhraseType.quickchatMessageTypelist = this;
		if (data != null) {
			quickChatPhraseType.decode(new Packet(data));
		}
		if (id >= 32768) {
			quickChatPhraseType.postDecode();
		}
		this.messageTypeCache.put(quickChatPhraseType, (long) id);
		return quickChatPhraseType;
	}

	@OriginalMember(owner = "client!aha", name = "a", descriptor = "(J[IILclient!oaa;)Ljava/lang/String;")
	public String getValueDisplay(@OriginalArg(0) long data, @OriginalArg(1) int[] configs, @OriginalArg(3) QuickChatValueType valueType) {
		if (this.quickchatFillerlist != null) {
			@Pc(20) String string = this.quickchatFillerlist.decode(data, valueType, configs);
			if (string != null) {
				return string;
			}
		}
		return Long.toString(data);
	}
	@OriginalMember(owner = "client!jca", name = "a", descriptor = "(BLclient!cea;)Lclient!iia;")
	public static QuickChatPhrase decodeQuickchatMessage(@OriginalArg(1) Packet packet) {
		@Pc(7) QuickChatPhrase quickChatPhrase = new QuickChatPhrase();
		quickChatPhrase.id = packet.readUShort();
		quickChatPhrase.type = TypeLists.QUICK_CHAT_MESSAGE.list(quickChatPhrase.id);
		return quickChatPhrase;
	}
	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IZLjava/lang/String;)V")
	public static void searchQuickChatPhrases(@OriginalArg(1) boolean isMenuSearch, @OriginalArg(2) String query) {
		@Pc(8) String string2 = query.toLowerCase();
		@Pc(11) short[] matchingIds = new short[16];
		@Pc(13) int matchCount = 0;
		@Pc(19) int startIndex = isMenuSearch ? 32768 : 0;
		@Pc(29) int endIndex = (isMenuSearch ? TypeLists.QUICK_CHAT_MESSAGE.configClientLargeNum : TypeLists.QUICK_CHAT_MESSAGE.configClientSmallNum) + startIndex;
		for (@Pc(31) int i = startIndex; i < endIndex; i++) {
			@Pc(38) QuickchatMessageType quickChatPhraseType = TypeLists.QUICK_CHAT_MESSAGE.list(i);
			if (quickChatPhraseType.searchable && quickChatPhraseType.getText().toLowerCase().indexOf(string2) != -1) {
				if (matchCount >= 50) {
					Find.queryResults = null;
					Find.findResultCount = -1;
					return;
				}
				if (matchingIds.length <= matchCount) {
					@Pc(67) short[] expandedIds = new short[matchingIds.length * 2];
					for (@Pc(69) int j = 0; j < matchCount; j++) {
						expandedIds[j] = matchingIds[j];
					}
					matchingIds = expandedIds;
				}
				matchingIds[matchCount++] = (short) i;
			}
		}
		Find.findResultCount = matchCount;
		MessageBox.queryResultsCursor = 0;
		Find.queryResults = matchingIds;
		@Pc(106) String[] strings = new String[Find.findResultCount];
		for (@Pc(108) int k = 0; k < Find.findResultCount; k++) {
			strings[k] = TypeLists.QUICK_CHAT_MESSAGE.list(matchingIds[k]).getText();
		}
		Utils.sortByStrings(Find.queryResults, strings);
	}
}
