package com.jagex.game.runetek5.graphics.model.billboard

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object BillboardTypeList {
	@OriginalMember(owner = "client!um", name = "w", descriptor = "Lclient!in;")
	@JvmField
	var BILLBOARD_RESOURCE: Js5ResourceProvider? = null

	@OriginalMember(owner = "client!rv", name = "e", descriptor = "Lclient!jr;")
	@JvmField
	val BILLBOARD_CACHE: LRUCache = LRUCache(64)

	@OriginalMember(owner = "client!km", name = "a", descriptor = "(ILclient!in;)V")
	@JvmStatic
	fun initialize(@OriginalArg(1) billboardResource: Js5ResourceProvider?) {
		BillboardTypeList.BILLBOARD_RESOURCE = billboardResource
	}

	@OriginalMember(owner = "client!kk", name = "a", descriptor = "(II)Lclient!qj;")
	@JvmStatic
	fun list(@OriginalArg(1) billboardId: Int): BillboardType {
		@Pc(10) var lightType: BillboardType? = BillboardTypeList.BILLBOARD_CACHE.get(billboardId.toLong()) as BillboardType?
		if (lightType != null) {
			return lightType
		}
		@Pc(20) val data: ByteArray? = BillboardTypeList.BILLBOARD_RESOURCE!!.getFile(0, billboardId)
		lightType = BillboardType()
		if (data != null) {
			lightType.decode(billboardId, Packet(data))
		}
		BillboardTypeList.BILLBOARD_CACHE.put(lightType, billboardId.toLong())
		return lightType
	}
}
