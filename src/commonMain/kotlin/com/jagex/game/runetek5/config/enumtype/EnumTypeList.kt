package com.jagex.game.runetek5.config.enumtype

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.enumtype.EnumType
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic
import withLock

@OriginalClass("client!iba")
class EnumTypeList {

	@OriginalMember(owner = "client!iba", name = "e", descriptor = "Lclient!jr;")
	private val cache: LRUCache = LRUCache(128)

	@OriginalMember(owner = "client!iba", name = "c", descriptor = "Lclient!in;")
	private val enumTypeResource: Js5ResourceProvider?

	@OriginalMember(owner = "client!iba", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) languageId: Int, @OriginalArg(2) enumTypeResource: Js5ResourceProvider?) {
		this.enumTypeResource = enumTypeResource
		if (this.enumTypeResource != null) {
			@Pc(20) val lastGroupId: Int = this.enumTypeResource.getCapacity() - 1
			this.enumTypeResource.getGroupCapacity(lastGroupId)
		}
	}

	@OriginalMember(owner = "client!iba", name = "a", descriptor = "(ZI)Lclient!nd;")
	fun list(@OriginalArg(1) id: Int): EnumType? {
		@Pc(6) val referenceCache: LRUCache = this.cache
		@Pc(16) var enumType: EnumType?
		withLock(this.cache) {
			enumType = this.cache.get(id.toLong()) as EnumType?
		}
		if (enumType != null) {
			return enumType
		}
		@Pc(37) val data: ByteArray? = this.enumTypeResource!!.getFile(EnumTypeList.getGroupId(id), EnumTypeList.getFileId(id))
		enumType = EnumType()
		if (data != null) {
			enumType!!.decode(Packet(data))
		}
		@Pc(53) val referenceCache2: LRUCache = this.cache
		withLock(this.cache) {
			this.cache.put(enumType, id.toLong())
			return enumType
		}
	}

	companion object {
		@OriginalMember(owner = "client!tq", name = "a", descriptor = "(II)I")
		@JvmStatic
		fun getGroupId(@OriginalArg(1) id: Int): Int {
			return id ushr 8
		}

		@OriginalMember(owner = "client!pi", name = "a", descriptor = "(IZ)I")
		@JvmStatic
		fun getFileId(@OriginalArg(0) id: Int): Int {
			return id and 0xFF
		}
	}
}
