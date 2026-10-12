package com.jagex.game.runetek5.config.quickchat

import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!sga")
class QuickchatCategoryTypeList {

	@OriginalMember(owner = "client!sga", name = "b", descriptor = "Lclient!jr;")
	private val categoryCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!sga", name = "c", descriptor = "Lclient!in;")
	private val menuResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!sga", name = "g", descriptor = "Lclient!in;")
	private val messageResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!sga", name = "<init>", descriptor = "(ILclient!in;Lclient!in;)V")
	constructor(@OriginalArg(0) languageId: Int, @OriginalArg(1) menuResource: Js5ResourceProvider?, @OriginalArg(2) messageResource: Js5ResourceProvider?) {
		this.menuResource = menuResource
		this.messageResource = messageResource
		if (this.menuResource != null) {
			this.menuResource.getGroupCapacity(0)
		}
		if (this.messageResource != null) {
			this.messageResource.getGroupCapacity(0)
		}
	}

	@OriginalMember(owner = "client!sga", name = "a", descriptor = "(II)Lclient!ug;")
	fun list(@OriginalArg(1) id: Int): QuickChatCategoryType {
		@Pc(11) var quickChatCatType: QuickChatCategoryType? = this.categoryCache.get(id.toLong()) as QuickChatCategoryType?
		if (quickChatCatType != null) {
			return quickChatCatType
		}
		@Pc(27) val data: ByteArray?
		if (id < 32768) {
			data = this.menuResource!!.getFile(0, id)
		} else {
			data = this.messageResource!!.getFile(0, id and 0x7FFF)
		}
		quickChatCatType = QuickChatCategoryType()
		if (data != null) {
			quickChatCatType.decode(Packet(data))
		}
		if (id >= 32768) {
			quickChatCatType.postDecode()
		}
		this.categoryCache.put(quickChatCatType, id.toLong())
		return quickChatCatType
	}
}
