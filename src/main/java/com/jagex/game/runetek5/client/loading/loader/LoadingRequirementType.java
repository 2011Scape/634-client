package com.jagex.game.runetek5.client.loading.loader;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!kea")
public final class LoadingRequirementType {

	@OriginalMember(owner = "client!kea", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!kea", name = "i", descriptor = "Lclient!kea;")
	public static final LoadingRequirementType NATIVE_LIBRARY = new LoadingRequirementType();

	@OriginalMember(owner = "client!kea", name = "g", descriptor = "Lclient!kea;")
	public static final LoadingRequirementType JS5_FILE = new LoadingRequirementType();

	@OriginalMember(owner = "client!kea", name = "e", descriptor = "Lclient!kea;")
	public static final LoadingRequirementType JS5 = new LoadingRequirementType();

	@OriginalMember(owner = "client!kea", name = "h", descriptor = "Lclient!kea;")
	public static final LoadingRequirementType JS5_GROUP = new LoadingRequirementType();
}
