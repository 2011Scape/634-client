package com.jagex.game.runetek5.graphics.model.billboard;

import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

public final class BillboardTypeList {
	@OriginalMember(owner = "client!um", name = "w", descriptor = "Lclient!in;")
	public static Js5ResourceProvider BILLBOARD_RESOURCE;

	private BillboardTypeList() {
		/* empty */
	}
	@OriginalMember(owner = "client!rv", name = "e", descriptor = "Lclient!jr;")
	public static final LRUCache BILLBOARD_CACHE = new LRUCache(64);
	@OriginalMember(owner = "client!km", name = "a", descriptor = "(ILclient!in;)V")
	public static void initialize(@OriginalArg(1) Js5ResourceProvider billboardResource) {
		BillboardTypeList.BILLBOARD_RESOURCE = billboardResource;
	}

	@OriginalMember(owner = "client!kk", name = "a", descriptor = "(II)Lclient!qj;")
	public static BillboardType list(@OriginalArg(1) int billboardId) {
		@Pc(10) BillboardType lightType = (BillboardType) BillboardTypeList.BILLBOARD_CACHE.get((long) billboardId);
		if (lightType != null) {
			return lightType;
		}
		@Pc(20) byte[] data = BillboardTypeList.BILLBOARD_RESOURCE.getFile(0, billboardId);
		lightType = new BillboardType();
		if (data != null) {
			lightType.decode(billboardId, new Packet(data));
		}
		BillboardTypeList.BILLBOARD_CACHE.put(lightType, (long) billboardId);
		return lightType;
	}
}
