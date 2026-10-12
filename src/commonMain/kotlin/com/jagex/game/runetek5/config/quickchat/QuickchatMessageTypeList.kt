package com.jagex.game.runetek5.config.quickchat

import com.jagex.game.runetek5.QuickChatValueType
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.quickchat.QuickchatFillerList
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.QuickChatPhrase
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.Find
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import util.Locale
import util.lowercase

@OriginalClass("client!aha")
class QuickchatMessageTypeList {

	@OriginalMember(owner = "client!aha", name = "d", descriptor = "I")
	@JvmField
	var configClientSmallNum: Int = 0

	@OriginalMember(owner = "client!aha", name = "c", descriptor = "I")
	@JvmField
	var configClientLargeNum: Int = 0

	@OriginalMember(owner = "client!aha", name = "a", descriptor = "Lclient!jr;")
	private val messageTypeCache: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!aha", name = "l", descriptor = "Lclient!bq;")
	private var quickchatFillerlist: QuickchatFillerList? = null

	@OriginalMember(owner = "client!aha", name = "g", descriptor = "Lclient!in;")
	private val messageResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!aha", name = "h", descriptor = "Lclient!in;")
	private val menuResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!aha", name = "<init>", descriptor = "(ILclient!in;Lclient!in;Lclient!bq;)V")
	constructor(@OriginalArg(0) languageId: Int, @OriginalArg(1) messageResource: Js5ResourceProvider?, @OriginalArg(2) menuResource: Js5ResourceProvider?, @OriginalArg(3) quickChatFillerDecoder: QuickchatFillerList?) {
		this.quickchatFillerlist = quickChatFillerDecoder
		this.messageResource = messageResource
		this.menuResource = menuResource
		if (this.messageResource != null) {
			this.configClientSmallNum = this.messageResource.getGroupCapacity(1)
		}
		if (this.menuResource != null) {
			this.configClientLargeNum = this.menuResource.getGroupCapacity(1)
		}
	}

	@OriginalMember(owner = "client!aha", name = "a", descriptor = "(IB)Lclient!td;")
	fun list(@OriginalArg(0) id: Int): QuickchatMessageType {
		@Pc(11) var quickChatPhraseType: QuickchatMessageType? = this.messageTypeCache.get(id.toLong()) as QuickchatMessageType?
		if (quickChatPhraseType != null) {
			return quickChatPhraseType
		}
		@Pc(29) val data: ByteArray?
		if (id >= 32768) {
			data = this.menuResource!!.getFile(1, id and 0x7FFF)
		} else {
			data = this.messageResource!!.getFile(1, id)
		}
		quickChatPhraseType = QuickchatMessageType()
		quickChatPhraseType.quickchatMessageTypelist = this
		if (data != null) {
			quickChatPhraseType.decode(Packet(data))
		}
		if (id >= 32768) {
			quickChatPhraseType.postDecode()
		}
		this.messageTypeCache.put(quickChatPhraseType, id.toLong())
		return quickChatPhraseType
	}

	@OriginalMember(owner = "client!aha", name = "a", descriptor = "(J[IILclient!oaa;)Ljava/lang/String;")
	fun getValueDisplay(@OriginalArg(0) data: Long, @OriginalArg(1) configs: IntArray?, @OriginalArg(3) valueType: QuickChatValueType?): String {
		if (this.quickchatFillerlist != null) {
			@Pc(20) val string: String? = this.quickchatFillerlist!!.decode(data, valueType, configs)
			if (string != null) {
				return string
			}
		}
		return data.toString()
	}

	companion object {
		@OriginalMember(owner = "client!jca", name = "a", descriptor = "(BLclient!cea;)Lclient!iia;")
		@JvmStatic
		fun decodeQuickchatMessage(@OriginalArg(1) packet: Packet): QuickChatPhrase {
			@Pc(7) val quickChatPhrase: QuickChatPhrase = QuickChatPhrase()
			quickChatPhrase.id = packet.readUShort()
			quickChatPhrase.type = TypeLists.QUICK_CHAT_MESSAGE!!.list(quickChatPhrase.id)
			return quickChatPhrase
		}

		@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IZLjava/lang/String;)V")
		@JvmStatic
		fun searchQuickChatPhrases(@OriginalArg(1) isMenuSearch: Boolean, @OriginalArg(2) query: String) {
			@Pc(8) val string2: String = query.lowercase(util.defaultLocale())
			@Pc(11) var matchingIds: ShortArray = ShortArray(16)
			@Pc(13) var matchCount: Int = 0
			@Pc(19) val startIndex: Int = if (isMenuSearch) 32768 else 0
			@Pc(29) val endIndex: Int = (if (isMenuSearch) TypeLists.QUICK_CHAT_MESSAGE!!.configClientLargeNum else TypeLists.QUICK_CHAT_MESSAGE!!.configClientSmallNum) + startIndex
			for (@Pc(31) i in startIndex until endIndex) {
				@Pc(38) val quickChatPhraseType: QuickchatMessageType = TypeLists.QUICK_CHAT_MESSAGE!!.list(i)
				if (quickChatPhraseType.searchable && quickChatPhraseType.getText().lowercase(util.defaultLocale()).indexOf(string2) != -1) {
					if (matchCount >= 50) {
						Find.queryResults = null
						Find.findResultCount = -1
						return
					}
					if (matchingIds.size <= matchCount) {
						@Pc(67) val expandedIds: ShortArray = ShortArray(matchingIds.size * 2)
						for (@Pc(69) j in 0 until matchCount) {
							expandedIds[j] = matchingIds[j]
						}
						matchingIds = expandedIds
					}
					matchingIds[matchCount++] = i.toShort()
				}
			}
			Find.findResultCount = matchCount
			MessageBox.queryResultsCursor = 0
			Find.queryResults = matchingIds
			@Pc(106) val strings: Array<String?> = arrayOfNulls<String>(Find.findResultCount)
			@Pc(108) var k: Int = 0
			while (k < Find.findResultCount) {
				strings[k] = TypeLists.QUICK_CHAT_MESSAGE!!.list(matchingIds[k].toInt()).getText()
				k++
			}
			Utils.sortByStrings(Find.queryResults, strings)
		}
	}
}
