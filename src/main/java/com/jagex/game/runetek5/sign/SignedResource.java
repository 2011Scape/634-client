package com.jagex.game.runetek5.sign;

import com.jagex.game.runetek5.core.constants.SignedResourceType;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!of")
public final class SignedResource {

	@OriginalMember(owner = "client!of", name = "a", descriptor = "I")
	public SignedResourceType kind;

	@OriginalMember(owner = "client!of", name = "b", descriptor = "Lclient!of;")
	public SignedResource next;

	@OriginalMember(owner = "client!of", name = "c", descriptor = "Ljava/lang/Object;")
	public Object objectData;

	@OriginalMember(owner = "client!of", name = "e", descriptor = "Ljava/lang/Object;")
	public volatile Object result;

	@OriginalMember(owner = "client!of", name = "f", descriptor = "I")
	public int intData2;

	@OriginalMember(owner = "client!of", name = "g", descriptor = "I")
	public int intData1;

	@OriginalMember(owner = "client!of", name = "d", descriptor = "I")
	public volatile int status = 0;
}
