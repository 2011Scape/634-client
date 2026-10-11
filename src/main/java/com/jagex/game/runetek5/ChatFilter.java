package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!sh")
public final class ChatFilter {

	@OriginalMember(owner = "client!sh", name = "e", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!sh", name = "<init>", descriptor = "(I)V")
	public ChatFilter(@OriginalArg(0) int id) {
		this.id = id;
	}

	@OriginalMember(owner = "client!sh", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!mea", name = "o", descriptor = "Lclient!sh;")
	public static final ChatFilter FRIENDS_ONLY = new ChatFilter(1);

	@OriginalMember(owner = "client!iea", name = "a", descriptor = "(BI)Lclient!sh;")
	public static ChatFilter chatFilterForId(@OriginalArg(1) int id) {
		@Pc(15) ChatFilter[] filters = ChatFilter.values();
		for (@Pc(17) int i = 0; i < filters.length; i++) {
			@Pc(23) ChatFilter filter = filters[i];
			if (id == filter.id) {
				return filter;
			}
		}
		return null;
	}

	@OriginalMember(owner = "client!lh", name = "b", descriptor = "(I)[Lclient!sh;")
	public static ChatFilter[] values() {
		return new ChatFilter[] { ChatFilter.aClass313_4, ChatFilter.FRIENDS_ONLY, ChatFilter.aClass313_2 };
	}

	@OriginalMember(owner = "client!qk", name = "m", descriptor = "Lclient!sh;")
	public static final ChatFilter aClass313_4 = new ChatFilter(0);

	@OriginalMember(owner = "client!go", name = "i", descriptor = "Lclient!sh;")
	public static final ChatFilter aClass313_2 = new ChatFilter(2);
}
