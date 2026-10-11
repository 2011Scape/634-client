package com.jagex.game.runetek5.config.enumtype;

import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.enumtype.EnumType;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!iba")
public final class EnumTypeList {

	@OriginalMember(owner = "client!iba", name = "e", descriptor = "Lclient!jr;")
	private final LRUCache cache = new LRUCache(128);

	@OriginalMember(owner = "client!iba", name = "c", descriptor = "Lclient!in;")
	private final Js5ResourceProvider enumTypeResource;

	@OriginalMember(owner = "client!iba", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public EnumTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider enumTypeResource) {
		this.enumTypeResource = enumTypeResource;
		if (this.enumTypeResource != null) {
			@Pc(20) int lastGroupId = this.enumTypeResource.getCapacity() - 1;
			this.enumTypeResource.getGroupCapacity(lastGroupId);
		}
	}

	@OriginalMember(owner = "client!iba", name = "a", descriptor = "(ZI)Lclient!nd;")
	public EnumType list(@OriginalArg(1) int id) {
		@Pc(6) LRUCache referenceCache = this.cache;
		@Pc(16) EnumType enumType;
		synchronized (this.cache) {
			enumType = (EnumType) this.cache.get((long) id);
		}
		if (enumType != null) {
			return enumType;
		}
		@Pc(37) byte[] data = this.enumTypeResource.getFile(EnumTypeList.getGroupId(id), EnumTypeList.getFileId(id));
		enumType = new EnumType();
		if (data != null) {
			enumType.decode(new Packet(data));
		}
		@Pc(53) LRUCache referenceCache2 = this.cache;
		synchronized (this.cache) {
			this.cache.put(enumType, (long) id);
			return enumType;
		}
	}
	@OriginalMember(owner = "client!tq", name = "a", descriptor = "(II)I")
	public static int getGroupId(@OriginalArg(1) int id) {
		return id >>> 8;
	}
	@OriginalMember(owner = "client!pi", name = "a", descriptor = "(IZ)I")
	public static int getFileId(@OriginalArg(0) int id) {
		return id & 0xFF;
	}
}
