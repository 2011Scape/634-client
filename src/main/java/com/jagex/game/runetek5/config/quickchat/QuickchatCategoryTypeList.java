package com.jagex.game.runetek5.config.quickchat;

import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!sga")
public final class QuickchatCategoryTypeList {

	@OriginalMember(owner = "client!sga", name = "b", descriptor = "Lclient!jr;")
	private final LRUCache categoryCache = new LRUCache(64);

	@OriginalMember(owner = "client!sga", name = "c", descriptor = "Lclient!in;")
	private final Js5ResourceProvider menuResource;

	@OriginalMember(owner = "client!sga", name = "g", descriptor = "Lclient!in;")
	private final Js5ResourceProvider messageResource;

	@OriginalMember(owner = "client!sga", name = "<init>", descriptor = "(ILclient!in;Lclient!in;)V")
	public QuickchatCategoryTypeList(@OriginalArg(0) int languageId, @OriginalArg(1) Js5ResourceProvider menuResource, @OriginalArg(2) Js5ResourceProvider messageResource) {
		this.menuResource = menuResource;
		this.messageResource = messageResource;
		if (this.menuResource != null) {
			this.menuResource.getGroupCapacity(0);
		}
		if (this.messageResource != null) {
			this.messageResource.getGroupCapacity(0);
		}
	}

	@OriginalMember(owner = "client!sga", name = "a", descriptor = "(II)Lclient!ug;")
	public QuickChatCategoryType list(@OriginalArg(1) int id) {
		@Pc(11) QuickChatCategoryType quickChatCatType = (QuickChatCategoryType) this.categoryCache.get((long) id);
		if (quickChatCatType != null) {
			return quickChatCatType;
		}
		@Pc(27) byte[] data;
		if (id < 32768) {
			data = this.menuResource.getFile(0, id);
		} else {
			data = this.messageResource.getFile(0, id & 0x7FFF);
		}
		quickChatCatType = new QuickChatCategoryType();
		if (data != null) {
			quickChatCatType.decode(new Packet(data));
		}
		if (id >= 32768) {
			quickChatCatType.postDecode();
		}
		this.categoryCache.put(quickChatCatType, (long) id);
		return quickChatCatType;
	}
}
