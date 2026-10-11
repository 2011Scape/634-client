package com.jagex.game.runetek5;

import com.jagex.game.runetek5.cache.node.lru.AbstractLRUNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ca")
public abstract class ReferenceFactory {

	@OriginalMember(owner = "client!ca", name = "a", descriptor = "(ILclient!qia;)Lclient!qia;")
	public abstract AbstractLRUNode createSoftReference(@OriginalArg(1) AbstractLRUNode node);
	@OriginalMember(owner = "client!vha", name = "a", descriptor = "(B)Lclient!ca;")
	public static ReferenceFactory createSoftReferenceFactory() {
		try {
			return (ReferenceFactory) Class.forName("com.jagex.game.runetek5.core.datastruct.ref.SoftReferenceFactory").getDeclaredConstructor().newInstance();
		} catch (@Pc(14) Throwable ex) {
			return null;
		}
	}
}
